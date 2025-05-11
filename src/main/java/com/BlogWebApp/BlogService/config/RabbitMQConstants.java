package com.BlogWebApp.BlogService.config;

public class RabbitMQConstants {
    public static final String BLOG_LIKED_QUEUE = "blog-liked-queue";
    public static final String BLOG_CREATED_QUEUE = "blog-created-queue";
    public static final String BLOG_UPDATED_QUEUE = "blog-updated-queue";
    public static final String BLOG_DELETED_QUEUE = "blog-deleted-queue";
    public static final String BLOG_EXCHANGE = "blog-exchange";
    public static final String BLOG_LIKED_ROUTING_KEY = "blog.liked";
    public static final String BLOG_CREATED_ROUTING_KEY = "blog.created";
    public static final String BLOG_UPDATED_ROUTING_KEY = "blog.updated";
    public static final String BLOG_DELETED_ROUTING_KEY = "blog.deleted";
}
