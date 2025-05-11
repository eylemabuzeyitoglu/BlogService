package com.BlogWebApp.BlogService.events;

import com.BlogWebApp.BlogService.model.Blog;

public class BlogCreatedEvent extends AbstractBlogEvent {
    public BlogCreatedEvent(Blog blog) {
        super(blog.getBlogId(), blog.getTitle());
    }
}
