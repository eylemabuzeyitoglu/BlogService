package com.BlogWebApp.BlogService.exceptions;

public class BlogCreationException extends RuntimeException{
    public BlogCreationException(String message) {
        super(message);
    }
}
