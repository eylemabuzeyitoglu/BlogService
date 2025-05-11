package com.BlogWebApp.BlogService.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractBlogEvent implements Serializable {
    private final Long blogId;
    private final String title;
}
