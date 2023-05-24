package com.reservation.inventoryService.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.reservation.inventoryService.model.Booking;
import com.reservation.inventoryService.model.BusInventory;
import com.reservation.inventoryService.repository.InventoryRepository;
import com.reservation.inventoryService.service.InventoryService;

@Component
public class InventoryServiceImpl implements InventoryService{

	Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	public int getSeats(int busNumber) {
		int availableSeats = 0;
		
		BusInventory busInventory = inventoryRepository.findByBusNumber(busNumber);
		if(busInventory != null) {
			availableSeats = busInventory.getAvailableSeats();
		} else {
			BusInventory busInv = freshBooking(busNumber);
			availableSeats = busInv.getAvailableSeats();
		}
		
		return availableSeats;
	}

	@Override
	public BusInventory updateSeats(int bookingNumber) {
		ResponseEntity<Booking> responseEntity = restTemplate
                .getForEntity("http://localhost:8117/api/booking/booking/" + bookingNumber,
                		Booking.class);
		Booking booking = responseEntity.getBody();
		BusInventory busInv = updateSeat(booking.getBusNumber(), booking.getSeats());
		
		if(busInv != null) {
			//Register order queue
			String exchange = "bookingDirectBinding";
			String routingKey = "queue.bookingQueue";
			
			amqpTemplate.convertAndSend(exchange, routingKey, booking.getBookingNumber());
			logger.debug("Registered Order queue using direct exchange");
			
			return busInv;
		}
		
		return null;
	}
	
	public BusInventory updateSeat(int busNumber, int seats) {
		BusInventory busInventory = inventoryRepository.findByBusNumber(busNumber);
		BusInventory busInv = new BusInventory();
		int availableSeats = 0;
		
		if(busInventory != null) {
			logger.debug("This bus has already started its booking");			
			availableSeats = busInventory.getAvailableSeats() - seats;	
			
			busInv.setBusNumber(busNumber);
			busInv.setAvailableSeats(availableSeats);
			busInv.setUpdatedDate(new Date());			
			
			return updateAvailableSeatsInDB(busInv);
		} else {
			busInv = freshBooking(busNumber);
			return busInv;
		}
		
		
	}
	
	public BusInventory freshBooking(int busNumber) {
		logger.debug("Fresh booking");
		
		BusInventory busInv = new BusInventory();
		ResponseEntity<Integer> responseEntity = restTemplate
                .getForEntity("http://localhost:8117/api/admin/route/" + busNumber,
                		Integer.class);
		
		int availableSeats = responseEntity.getBody();	
		
		busInv.setBusNumber(busNumber);
		busInv.setAvailableSeats(availableSeats);
		busInv.setUpdatedDate(new Date());			
		
		return updateAvailableSeatsInDB(busInv);
	}
	
	public BusInventory updateAvailableSeatsInDB(BusInventory busInv) {
		return inventoryRepository.save(busInv);
	}
	
	/*public BusInventory updateSeat(BusRoute route) {
		BusInventory busInv = updateSeat(route.getBusNumber(), route.getSeats());
		return busInv;
	}*/
}
