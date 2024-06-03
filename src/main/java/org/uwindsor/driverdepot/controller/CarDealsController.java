package org.uwindsor.driverdepot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.uwindsor.driverdepot.entity.CarInfo;
import org.uwindsor.driverdepot.entity.KeyToDeals;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.AutoTradeCAParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.CarPagesParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.MazdaCarParser;
import org.uwindsor.mac.acc.drivedepot.indextable.IndexTableProvider;
import org.uwindsor.mac.acc.drivedepot.model.Car;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class CarDealsController {

	private static AutoTradeCAParser autoParser = null;
	private static CarPagesParser carPageParser = null;
	private static MazdaCarParser mazdaCarParser = null;

	@GetMapping("/name")
	public String getName() {
		return "String....";
	}

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

	@RequestMapping(value = "/cardeals", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CarInfo> getCarss(@RequestBody KeyToDeals keyCars) {
		try {

			return ResponseEntity.ok(getCars(keyCars));
		} catch (IOException e) {
			return ResponseEntity.noContent().build();
		}
	}

	private CarInfo getCars(KeyToDeals keyCars) throws IOException {
		List<Car> carsList = new ArrayList<>();
		CarInfo carInfo = new CarInfo();
		
		String key = keyCars.getCarMake() + keyCars.getCarType() + keyCars.getLocation();
		carsList.addAll(autoParser.getCars(key.toString()));
		carsList.addAll(carPageParser.getCars(key.toString()));
		carsList.addAll(mazdaCarParser.getCars(key.toString()));
		carInfo.setCars(carsList);
		
		Map<String, List<String>> mapUrls = new HashMap<>();
		
		mapUrls.put(keyCars.getCarMake(), IndexTableProvider.getTrie().getPageInfo(keyCars.getCarMake()));
		mapUrls.put(key, IndexTableProvider.getTrie().getPageInfo(key));
		mapUrls.put(keyCars.getLocation(), IndexTableProvider.getTrie().getPageInfo(keyCars.getLocation()));
		carInfo.setUrls(mapUrls);
		
		return carInfo;

	}
}
