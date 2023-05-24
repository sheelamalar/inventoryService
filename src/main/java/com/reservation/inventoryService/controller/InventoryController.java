package com.reservation.inventoryService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.reservation.inventoryService.service.InventoryService;

import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
	Logger logger = LoggerFactory.getLogger(InventoryController.class);
	
	@Autowired
	InventoryService service;
	
	 @Value("${db.username:bad boy}")
	 private String role;
	 
	 @Value("${password:me}")
	 private String role1;
	 
	 @Autowired
	 private Environment env;
	 
	/*
	 * CRUD - Receive
	 * URL: http://localhost:8112/api/inventory/route/78
	 */
	@GetMapping("/route/{busNumber}")
	public int getSeats(@PathVariable(value = "busNumber") int busNumber) throws Exception {
		logger.debug(" getSeats: "+busNumber);
		logger.debug("*********** "+role+"***********");
		logger.debug("*********** "+role1+"***********");
		logger.debug("*********** "+env.getProperty("password")+"***********");
		
		
		int availableSeats = service.getSeats(busNumber);		
		return availableSeats;
	}
}
