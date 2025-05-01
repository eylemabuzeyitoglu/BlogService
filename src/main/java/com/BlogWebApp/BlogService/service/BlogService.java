package com.BlogWebApp.BlogService.service;

import com.BlogWebApp.BlogService.events.BlogCreatedEvent;
import com.BlogWebApp.BlogService.events.BlogDeletedEvent;
import com.BlogWebApp.BlogService.events.BlogLikedEvent;
import com.BlogWebApp.BlogService.events.BlogUpdatedEvent;
import com.BlogWebApp.BlogService.exceptions.BlogNotFoundException;
import com.BlogWebApp.BlogService.mapper.BlogMapper;
import com.BlogWebApp.BlogService.model.Blog;
import com.BlogWebApp.BlogService.repository.BlogRepository;
import com.BlogWebApp.CommonSecurity.dto.BlogRequest;
import com.BlogWebApp.CommonSecurity.dto.BlogResponse;
import com.BlogWebApp.CommonSecurity.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;
    private final RabbitMQMessageProducer messageProducer;
    private final JwtUtil jwtUtil;


    public List<BlogResponse> getAllBlog(){
        return blogRepository.findAll()
                .stream()
                .map(blog -> blogMapper.toBlogResponse(blog,blog.getBlogId()))
                .collect(Collectors.toList());
    }

    public BlogResponse getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog bulunamadı"));

        return blogMapper.toBlogResponse(blog,blog.getBlogId());

    }

    public List<BlogResponse> getTop5Blog(){
        return  blogRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(blog -> blogMapper.toBlogResponse(blog, blog.getBlogId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public BlogResponse createBlog(BlogRequest blogRequest, String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (jwtUtil.isTokenExpired(jwtToken)) {
            throw new AccessDeniedException("Token süresi dolmuş");
        }

        List<String> roles = jwtUtil.extractRoles(jwtToken);
        if (!roles.contains("ADMIN")) {
            throw new AccessDeniedException("Yetkin yok");
        }

        Blog blog = blogMapper.toBlogEntity(blogRequest);

        if (blogRequest.getTitle() == null || blogRequest.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Başlık boş olamaz");
        }
        if(blog.getFullContent() != null){
            String content = blog.getFullContent();
            blog.setShortContent(content.length() > 100 ? content.substring(0,100) + "...": content);
        }
        Blog saveBlog = blogRepository.save(blog);
        messageProducer.sendBlogCreatedNotification(new BlogCreatedEvent(saveBlog));
        return blogMapper.toBlogResponse(saveBlog,saveBlog.getBlogId());
    }


    @Transactional
    public BlogResponse updateBlog(Long blogId,BlogRequest blogRequest, String token ){
        List<String> roles = jwtUtil.extractRoles(token);
        if (!roles.contains("ADMIN")) {
            throw new AccessDeniedException("Yetkin yok");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Güncellenecek blog bulunamadı"));

        if (blogRequest.getTitle() != null && !blogRequest.getTitle().isEmpty()) {
            blog.setTitle(blogRequest.getTitle());
        }

        if (blogRequest.getImgUrl() != null && !blogRequest.getImgUrl().isEmpty()) {
            blog.setImgUrl(blogRequest.getImgUrl());
        }
        if (blogRequest.getFullContent() != null && !blogRequest.getFullContent().isEmpty()) {
            blog.setFullContent(blogRequest.getFullContent());
            blog.setShortContent(blogRequest.getFullContent().length() > 100 ?
                    blogRequest.getFullContent().substring(0, 100) + "..." : blogRequest.getFullContent());
        }
        Blog updated = blogRepository.save(blog);
        messageProducer.sendBlogUpdatedNotification(new BlogUpdatedEvent(updated));
        return blogMapper.toBlogResponse(updated,updated.getBlogId());
    }

    @Transactional
    public void deleteBlog(Long blogId,String token ){

        List<String> roles = jwtUtil.extractRoles(token);
        if (!roles.contains("ADMIN")) {
            throw new AccessDeniedException("Yetkin yok");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Silinecek blog bulunamadı"));

         blogRepository.delete(blog);
         messageProducer.sendBlogDeletedNotification(new BlogDeletedEvent(blog));
    }

    @RabbitListener(queues = "blog-liked-queue")
    public void handleBlogLikedEvent(BlogLikedEvent event) {
        Long userId = event.getUserId();
        Long blogId = event.getBlogId();

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog bulunamadı"));

        blog.getLikedUserIds().add(userId);
        blogRepository.save(blog);
    }


}


























