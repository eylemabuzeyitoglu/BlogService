package com.BlogWebApp.BlogService.events;

import com.BlogWebApp.BlogService.model.Blog;

public class BlogDeletedEvent extends AbstractBlogEvent {
    public BlogDeletedEvent(Blog blog) {
        super(blog.getBlogId(), blog.getTitle());
    }
}