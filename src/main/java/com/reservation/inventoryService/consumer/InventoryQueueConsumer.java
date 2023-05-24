package com.reservation.inventoryService.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.reservation.inventoryService.model.BusInventory;
import com.reservation.inventoryService.service.InventoryService;

@Component
public class InventoryQueueConsumer {
	Logger logger = LoggerFactory.getLogger(InventoryQueueConsumer.class);

	@Autowired
	InventoryService inventoryService;

	@RabbitListener(queues="inventoryQueue",ackMode="MANUAL")
	public void receiveMessageFromInventoryQueue(String message, Channel channel,
			@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		logger.debug("receiveMessageFromInventoryQueue: "+message);
		
		try {
			BusInventory busInv = inventoryService.updateSeats(Integer.parseInt(message));
			
			if(busInv != null) {
				logger.debug("Queue processed: Inventory Updated");
				channel.basicAck(tag, false);
			}			
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		
	}
}
