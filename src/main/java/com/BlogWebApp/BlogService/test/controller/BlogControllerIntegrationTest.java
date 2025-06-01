package com.BlogWebApp.BlogService.controller;

import com.BlogWebApp.BlogService.service.BlogService;
import com.BlogWebApp.Common.dto.BlogRequest;
import com.BlogWebApp.Common.dto.BlogResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc'yi otomatik olarak yapılandırır
@ActiveProfiles("test") // Test ortamı için özel profili etkinleştirir (isteğe bağlı ama önerilir)
public class BlogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // HTTP isteklerini simüle etmek için kullanılır

    @Autowired
    private ObjectMapper objectMapper; // Java objelerini JSON'a dönüştürmek için kullanılır

    @MockitoBean
    private BlogService blogService; // BlogService'i mock'luyoruz çünkü bu katmanın iç detayları bu testin kapsamı dışında.
    // Sadece controller'ın service ile doğru şekilde etkileşip etkileşmediğini test etmek istiyoruz.

    private final String MOCK_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJBRE1JTiJdfQ.someDummySignature";

    private BlogRequest blogRequest;
    private BlogResponse blogResponse;
    private List<BlogResponse> blogResponseList;

    @BeforeEach
    void setUp() {
        blogRequest = BlogRequest.builder()
                .title("Test Blog Title")
                .fullContent("This is the content of the test blog.")
                .build();

        blogResponse = BlogResponse.builder()
                .blogId(1L)
                .title("Test Blog Title")
                .fullContent("This is the content of the test blog.")
                .build();

        blogResponseList = Arrays.asList(
                BlogResponse.builder().blogId(1L).title("Blog 1").fullContent("Content 1").build(),
                BlogResponse.builder().blogId(2L).title("Blog 2").fullContent("Content 2").build()
        );
    }

    @Test
    @DisplayName("should create a new blog post successfully")
    void createBlog_Success() throws Exception {
        doNothing().when(blogService).createBlog(any(BlogRequest.class), eq(MOCK_TOKEN));

        mockMvc.perform(post("/api/blog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(blogRequest)))
                .andExpect(status().isOk()); // 200 OK bekleniyor
    }

    @Test
    @DisplayName("should update an existing blog post successfully")
    void updateBlog_Success() throws Exception {
        Long blogId = 1L;
        doNothing().when(blogService).updateBlog(eq(blogId), any(BlogRequest.class), eq(MOCK_TOKEN));

        mockMvc.perform(patch("/api/blog/{id}", blogId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(blogRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should delete a blog post successfully")
    void deleteBlog_Success() throws Exception {
        Long blogId = 1L;
        doNothing().when(blogService).deleteBlog(eq(blogId), eq(MOCK_TOKEN));

        mockMvc.perform(delete("/api/blog/{id}", blogId)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return all blog posts")
    void getAllBlog_Success() throws Exception {
        when(blogService.getAllBlog()).thenReturn(blogResponseList);

        mockMvc.perform(get("/api/blog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Blog 1"));
    }

    @Test
    @DisplayName("should return a blog post by ID")
    void getBlogById_Success() throws Exception {
        Long blogId = 1L;
        when(blogService.getBlogById(eq(blogId))).thenReturn(blogResponse);

        mockMvc.perform(get("/api/blog/{id}", blogId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(blogId))
                .andExpect(jsonPath("$.title").value("Test Blog Title"));
    }

    @Test
    @DisplayName("should return top 5 blog posts")
    void getTop5Blog_Success() throws Exception {
        when(blogService.getTop5Blog()).thenReturn(blogResponseList); // Gerçekte 5 tane dönebilir ama mock olarak listemizi kullandık

        mockMvc.perform(get("/api/blog/index")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Blog 1"));
    }

    @Test
    @DisplayName("Admin endpoint should grant access for ADMIN role")
    @WithMockUser(roles = {"ADMIN"}) // Spring Security bağlamında ADMIN rolüyle bir kullanıcı oluşturur
    void adminEndpoint_AdminRole_Success() throws Exception {
        mockMvc.perform(get("/api/blog/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin access granted"));
    }

    @Test
    @DisplayName("Admin endpoint should deny access for non-ADMIN role")
    @WithMockUser(roles = {"USER"}) // Spring Security bağlamında USER rolüyle bir kullanıcı oluşturur
    void adminEndpoint_UserRole_Forbidden() throws Exception {
        mockMvc.perform(get("/api/blog/admin/test"))
                .andExpect(status().isForbidden()); // 403 Forbidden bekleniyor
    }

    @Test
    @DisplayName("Admin endpoint should deny access for unauthenticated user")
    void adminEndpoint_NoUser_Unauthorized() throws Exception {
        // @WithMockUser kullanılmadığında varsayılan olarak kimliği doğrulanmamış kabul edilir
        mockMvc.perform(get("/api/blog/admin/test"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized bekleniyor
    }
}