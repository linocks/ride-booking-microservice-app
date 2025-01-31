package com.ecabs.dispatch_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queue.name}")
    private String QUEUE_NAME;
    @Value("${spring.rabbitmq.exchange.name}")
    private String EXCHANGE_NAME;
    @Value("${spring.rabbitmq.routing.key}")
    private String ROUTING_KEY;

    @Bean
    public Queue rideRequestQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange rideRequestExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue rideRequestQueue, DirectExchange rideRequestExchange) {
        return BindingBuilder.bind(rideRequestQueue)
                .to(rideRequestExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
