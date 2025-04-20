package com.BlogWebApp.BlogService.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlogLikedEvent {
    private Long userId;
    private Long blogId;
}
