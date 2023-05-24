package com.reservation.inventoryService.service;

import org.springframework.stereotype.Component;

import com.reservation.inventoryService.model.BusInventory;

@Component
public interface InventoryService {
	//public BusInventory updateSeat(BusRoute route);

	public int getSeats(int busNumber);

	public BusInventory updateSeats(int bookingNumber);
}
