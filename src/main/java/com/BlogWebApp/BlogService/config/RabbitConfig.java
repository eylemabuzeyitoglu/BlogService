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
    public DirectExchange blogExchange() {
        return new DirectExchange(RabbitMQConstants.BLOG_EXCHANGE);
    }

    @Bean
    public Queue blogLikedQueue() {
        return new Queue(RabbitMQConstants.BLOG_LIKED_QUEUE, true);
    }

    @Bean
    public Queue blogCreatedQueue() {
        return new Queue(RabbitMQConstants.BLOG_CREATED_QUEUE, true);
    }

    @Bean
    public Queue blogUpdatedQueue() {
        return new Queue(RabbitMQConstants.BLOG_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue blogDeletedQueue() {
        return new Queue(RabbitMQConstants.BLOG_DELETED_QUEUE, true);
    }

    private Binding createBinding(Queue queue, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(blogExchange())
                .with(routingKey);
    }

    @Bean
    public Binding blogLikedBinding() {
        return createBinding(blogLikedQueue(), RabbitMQConstants.BLOG_LIKED_ROUTING_KEY);
    }

    @Bean
    public Binding blogCreatedBinding() {
        return createBinding(blogCreatedQueue(), RabbitMQConstants.BLOG_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding blogUpdatedBinding() {
        return createBinding(blogUpdatedQueue(), RabbitMQConstants.BLOG_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding blogDeletedBinding() {
        return createBinding(blogDeletedQueue(), RabbitMQConstants.BLOG_DELETED_ROUTING_KEY);
    }
}
