package com.ceos.fetch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String FETCH_EXCHANGE = "rss.fetch.exchange";
    public static final String FETCH_QUEUE = "rss.fetch.queue";
    public static final String FETCH_ROUTING_KEY = "rss.fetch.routing.key";

    @Bean
    public DirectExchange fetchExchange() {
        return new DirectExchange(FETCH_EXCHANGE);
    }

    @Bean
    public Queue fetchQueue() {
        return QueueBuilder.durable(FETCH_QUEUE)
            .withArgument("x-dead-letter-exchange", FETCH_EXCHANGE + ".dlx")
            .withArgument("x-dead-letter-routing-key", FETCH_ROUTING_KEY + ".dlq")
            .build();
    }

    @Bean
    public Binding fetchBinding() {
        return BindingBuilder.bind(fetchQueue())
            .to(fetchExchange())
            .with(FETCH_ROUTING_KEY);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(FETCH_EXCHANGE + ".dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(FETCH_QUEUE + ".dlq").build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with(FETCH_ROUTING_KEY + ".dlq");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
} 