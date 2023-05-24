package com.reservation.inventoryService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DirectExchangeConfig {
	Logger logger = LoggerFactory.getLogger(DirectExchangeConfig.class);
	
	@Bean
	Queue bookingQueue() {
		logger.debug("bookingQueue");
		
		//false: non-durable
		return new Queue("bookingQueue", false);		
	}
	
	@Bean
	DirectExchange bookingDirectExchange() {
		logger.debug("bookingDirectExchange");
		
		return new DirectExchange("bookingDirectExchange");
	}
	
	@Bean
	Binding bookingDirectBinding(Queue bookingQueue, DirectExchange bookingDirectExchange) {
		logger.debug("bookingDirectBinding");
		
		return BindingBuilder.bind(bookingQueue).to(bookingDirectExchange).with("queue.bookingQueue");
	}
	

}
