package com.BlogWebApp.BlogService.service;

import com.BlogWebApp.BlogService.events.BlogCreatedEvent;
import com.BlogWebApp.BlogService.events.BlogDeletedEvent;
import com.BlogWebApp.BlogService.events.BlogUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public void sendBlogCreatedNotification(BlogCreatedEvent event) {
        sendMessage(routingKeyBlogCreated, event, "Blog Oluşturuldu");
    }

    public void sendBlogUpdatedNotification(BlogUpdatedEvent event) {
        sendMessage(routingKeyBlogUpdated, event, "Blog Güncellendi");
    }

    public void sendBlogDeletedNotification(BlogDeletedEvent event) {
        sendMessage(routingKeyBlogDeleted, event, "Blog Silindi");
    }

    private void sendMessage(String routingKey, Object event, String action) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Blog {}: ID = {}", action, getEventId(event));
    }

    private Long getEventId(Object event) {
        try {
            return (Long) event.getClass().getMethod("getBlogId").invoke(event);
        } catch (Exception e) {
            log.error("Event ID alınırken hata oluştu", e);
            return null;
        }
    }
}
