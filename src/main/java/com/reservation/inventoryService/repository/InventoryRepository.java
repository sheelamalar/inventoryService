package com.reservation.inventoryService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.reservation.inventoryService.model.BusInventory;

@Repository
public interface InventoryRepository extends JpaRepository<BusInventory, Integer> {

	@Query(value = "select * from businventory where bus_number  = ?1",  nativeQuery = true)
	BusInventory findByBusNumber(int busNumber);

}
