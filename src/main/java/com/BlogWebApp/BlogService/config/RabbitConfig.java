package com.BlogWebApp.BlogService.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {
    @Bean
    public Queue blogLikedQueue() {
        return new Queue("blog-liked-queue", true);
    }

    @Bean
    public DirectExchange blogExchange() {
        return new DirectExchange("blog-exchange");
    }

    @Bean
    public Binding blogLikedBinding() {
        return BindingBuilder.bind(blogLikedQueue())
                .to(blogExchange())
                .with("blog.liked");
    }
}
