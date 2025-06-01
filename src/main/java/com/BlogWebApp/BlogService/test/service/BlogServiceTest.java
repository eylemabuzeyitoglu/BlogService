package com.BlogWebApp.BlogService.test.service;

import com.BlogWebApp.BlogService.events.BlogCreatedEvent;
import com.BlogWebApp.BlogService.events.BlogDeletedEvent;
import com.BlogWebApp.BlogService.events.BlogLikedEvent;
import com.BlogWebApp.BlogService.events.BlogUpdatedEvent;
import com.BlogWebApp.BlogService.exceptions.BlogNotFoundException;
import com.BlogWebApp.BlogService.mapper.BlogMapper;
import com.BlogWebApp.BlogService.model.Blog;
import com.BlogWebApp.BlogService.repository.BlogRepository;
import com.BlogWebApp.BlogService.service.BlogService;
import com.BlogWebApp.BlogService.service.RabbitMQMessageProducer;
import com.BlogWebApp.Common.dto.BlogRequest;
import com.BlogWebApp.Common.dto.BlogResponse;
import com.BlogWebApp.Common.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BlogServiceTest {
    @Mock private BlogRepository blogRepository;
    @Mock private BlogMapper blogMapper;
    @Mock private RabbitMQMessageProducer messageProducer;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private BlogService blogService;

    private Blog blog;
    private BlogRequest blogRequest;
    private BlogResponse blogResponse;

    @BeforeEach
    void Setup(){
        blog = new Blog();
        blog.setBlogId(1L);
        blog.setTitle("Test title");
        blog.setFullContent("Full content of the blog.");
        blog.setShortContent("Short content of the blog.");
    }

    @Test
    void getAllBlog_shouldReturnBlogList(){
        when(blogRepository.findAll()).thenReturn(List.of(blog));
        when(blogMapper.toBlogResponse(blog, 1L)).thenReturn(new BlogResponse());

        List<BlogResponse> result = blogService.getAllBlog();

        assertEquals(1, result.size());
        verify(blogRepository).findAll();
    }

    @Test
    void shouldReturnBlogById() {
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogMapper.toBlogResponse(blog, 1L)).thenReturn(new BlogResponse());

        BlogResponse response = blogService.getBlogById(1L);

        assertNotNull(response);
        verify(blogRepository).findById(1L);
    }

    @Test
    void shouldReturnTop5Blogs() {
        when(blogRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(blog));
        when(blogMapper.toBlogResponse(blog, 1L)).thenReturn(new BlogResponse());

        List<BlogResponse> top5 = blogService.getTop5Blog();

        assertEquals(1, top5.size());
        verify(blogRepository).findTop5ByOrderByCreatedAtDesc();
    }

    @Test
    void shouldCreateBlogWhenUserIsAdmin() {
        BlogRequest request = new BlogRequest();
        request.setTitle("Title");
        request.setFullContent("Some long content that exceeds 100 characters ..................................................");

        blog.setFullContent(request.getFullContent());
        Blog savedBlog = new Blog(); savedBlog.setBlogId(3L);

        when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));
        when(blogMapper.toBlogEntity(request)).thenReturn(blog);
        when(blogRepository.save(blog)).thenReturn(savedBlog);
        when(blogMapper.toBlogResponse(savedBlog, 3L)).thenReturn(new BlogResponse());

        BlogResponse response = blogService.createBlog(request, "Bearer token");

        assertNotNull(response);
        verify(messageProducer).sendBlogCreatedNotification(any(BlogCreatedEvent.class));
    }

    @Test
    void shouldUpdateBlogIfAdmin() {
        BlogRequest request = new BlogRequest();
        request.setTitle("Updated Title");
        request.setFullContent("Updated Content");

        Blog existingBlog = new Blog(); existingBlog.setBlogId(4L);
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));
        when(blogRepository.findById(4L)).thenReturn(Optional.of(existingBlog));
        when(blogRepository.save(existingBlog)).thenReturn(existingBlog);
        when(blogMapper.toBlogResponse(existingBlog, 4L)).thenReturn(new BlogResponse());

        BlogResponse response = blogService.updateBlog(4L, request, "token");

        assertNotNull(response);
        verify(messageProducer).sendBlogUpdatedNotification(any(BlogUpdatedEvent.class));
    }

    @Test
    void shouldDeleteBlogIfAdmin() {
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));
        when(blogRepository.findById(5L)).thenReturn(Optional.of(blog));

        blogService.deleteBlog(5L, "token");

        verify(blogRepository).delete(blog);
        verify(messageProducer).sendBlogDeletedNotification(any(BlogDeletedEvent.class));
    }

    @Test
    void shouldHandleBlogLikedEvent() {
        BlogLikedEvent event = new BlogLikedEvent(1L, 2L);
        blog.setLikedUserIds(new HashSet<>());

        when(blogRepository.findById(2L)).thenReturn(Optional.of(blog));

        blogService.handleBlogLikedEvent(event);

        assertTrue(blog.getLikedUserIds().contains(1L));
        verify(blogRepository).save(blog);
    }

    @Test
    void shouldThrowAccessDeniedIfTokenExpired() {
        BlogRequest request = new BlogRequest();
        request.setTitle("Test");

        when(jwtUtil.isTokenExpired(anyString())).thenReturn(true);

        assertThrows(AccessDeniedException.class, () ->
                blogService.createBlog(request, "Bearer expiredtoken")
        );
    }

    @Test
    void shouldThrowAccessDeniedIfNotAdminOnCreate() {
        BlogRequest request = new BlogRequest();
        request.setTitle("Test");

        when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("USER"));

        assertThrows(AccessDeniedException.class, () ->
                blogService.createBlog(request, "Bearer token")
        );
    }

    @Test
    void shouldThrowIllegalArgumentWhenTitleIsEmpty() {
        BlogRequest request = new BlogRequest();
        request.setTitle("");

        when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));

        assertThrows(IllegalArgumentException.class, () ->
                blogService.createBlog(request, "Bearer token")
        );
    }

    @Test
    void shouldThrowWhenBlogToUpdateNotFound() {
        BlogRequest request = new BlogRequest();
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));
        when(blogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BlogNotFoundException.class, () ->
                blogService.updateBlog(99L, request, "token")
        );
    }

    @Test
    void shouldThrowWhenBlogToDeleteNotFound() {
        when(jwtUtil.extractRoles(anyString())).thenReturn(List.of("ADMIN"));
        when(blogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BlogNotFoundException.class, () ->
                blogService.deleteBlog(99L, "token")
        );
    }

    @Test
    void shouldThrowWhenBlogIdNotFound() {
        when(blogRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(BlogNotFoundException.class, () ->
                blogService.getBlogById(100L)
        );
    }

    @Test
    void shouldExtractTokenWithoutBearerPrefix() {
        BlogRequest request = new BlogRequest();
        request.setTitle("Title");

        when(jwtUtil.isTokenExpired("rawtoken")).thenReturn(false);
        when(jwtUtil.extractRoles("rawtoken")).thenReturn(List.of("ADMIN"));
        when(blogMapper.toBlogEntity(request)).thenReturn(blog);
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(blogMapper.toBlogResponse(any(), any())).thenReturn(new BlogResponse());

        BlogResponse response = blogService.createBlog(request, "rawtoken");

        assertNotNull(response);
    }



}
