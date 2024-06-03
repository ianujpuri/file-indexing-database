package org.uwindsor.driverdepot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.AutoTradeCAParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.CarPagesParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.MazdaCarParser;
import org.uwindsor.mac.acc.drivedepot.model.Car;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/inventory")
public class CarInventoryController {

	private static AutoTradeCAParser autoParser = null;
	private static CarPagesParser carPageParser = null;
	private static MazdaCarParser mazdaCarParser = null;

	@PostConstruct
	public void init() {
		try {
			autoParser = new AutoTradeCAParser();
			carPageParser = new CarPagesParser();
			mazdaCarParser = new MazdaCarParser();
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Get all the cars
	 * @return
	 */
	@RequestMapping (
			value = "/allcars",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Car>> getAllCars() {
		try {
			List<Car> carsList = new ArrayList<>();

			carsList.addAll(autoParser.getAllCars());
			carsList.addAll(carPageParser.getAllCars());
			carsList.addAll(mazdaCarParser.getAllCars());

			return ResponseEntity.ok(carsList);
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}
}
