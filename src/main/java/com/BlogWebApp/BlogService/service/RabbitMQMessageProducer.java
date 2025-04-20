package com.BlogWebApp.BlogService.service;


import com.BlogWebApp.BlogService.events.BlogCreatedEvent;
import com.BlogWebApp.BlogService.events.BlogDeletedEvent;
import com.BlogWebApp.BlogService.events.BlogUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.blog.created}")
    private String routingKeyBlogCreated;

    @Value("${rabbitmq.routing.key.blog.updated}")
    private String routingKeyBlogUpdated;

    @Value("${rabbitmq.routing.key.blog.deleted}")
    private String routingKeyBlogDeleted;

    public RabbitMQMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBlogCreatedNotification(BlogCreatedEvent blogCreatedEvent) {
        rabbitTemplate.convertAndSend(exchange, routingKeyBlogCreated, blogCreatedEvent);
        System.out.println("Blog oluşturuldu:  " + blogCreatedEvent.getBlogId());
    }

    public void sendBlogUpdatedNotification(BlogUpdatedEvent blogUpdatedEvent){
        rabbitTemplate.convertAndSend(exchange,routingKeyBlogUpdated,blogUpdatedEvent);
        System.out.println("Blog güncellendi: " + blogUpdatedEvent.getBlogId());
    }

    public void sendBlogDeletedNotification(BlogDeletedEvent blogDeletedEvent){
        rabbitTemplate.convertAndSend(exchange,routingKeyBlogDeleted,blogDeletedEvent);
        System.out.println("Blog silindi: " + blogDeletedEvent.getBlogId());
    }
}
