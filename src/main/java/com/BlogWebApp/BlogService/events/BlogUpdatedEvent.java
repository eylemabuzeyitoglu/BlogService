package com.BlogWebApp.BlogService.events;

import com.BlogWebApp.BlogService.model.Blog;

public class BlogUpdatedEvent extends AbstractBlogEvent {
    public BlogUpdatedEvent(Blog blog) {
        super(blog.getBlogId(), blog.getTitle());
    }
}
