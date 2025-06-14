package com.BlogWebApp.BlogService.controller;

import com.BlogWebApp.BlogService.service.BlogService;
import com.BlogWebApp.Common.dto.request.BlogRequest;
import com.BlogWebApp.Common.dto.response.BlogResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    public void createBlog(@RequestBody BlogRequest blogRequest,
                           @RequestHeader("Authorization") String token){
        blogService.createBlog(blogRequest,token);
    }

    @PatchMapping("{id}")
    public void updateBlog(@PathVariable long id,
                           @RequestBody BlogRequest blogRequest,
                           @RequestHeader("Authorization") String token) {
        blogService.updateBlog(id,blogRequest,token);
    }

    @DeleteMapping("/{id}")
    public void deleteBlog(@PathVariable Long id,
                           @RequestHeader("Authorization") String token){
        blogService.deleteBlog(id,token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/test")
    public String adminEndpoint() {
        return "Admin access granted";
    }

    @GetMapping
    public List<BlogResponse> getAllBlog(){
       return  blogService.getAllBlog();
    }

    @GetMapping("/{id}")
    public BlogResponse getBlogById(@PathVariable Long id){
        return blogService.getBlogById(id);
    }

    @GetMapping("/index")
    public List<BlogResponse> getTop5Blog(){
        return blogService.getTop5Blog();
    }


}
