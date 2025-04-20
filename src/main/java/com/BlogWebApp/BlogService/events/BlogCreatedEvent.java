package com.BlogWebApp.BlogService.events;

import com.BlogWebApp.BlogService.model.Blog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class BlogCreatedEvent implements Serializable {
    private final Long blogId;
    private final String title;

    public BlogCreatedEvent(Blog blog) {
        this.blogId = blog.getBlogId();
        this.title = blog.getTitle();
    }
}

