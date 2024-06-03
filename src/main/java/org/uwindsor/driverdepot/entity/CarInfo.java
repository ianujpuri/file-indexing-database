package org.uwindsor.driverdepot.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.uwindsor.mac.acc.drivedepot.model.Car;


public class CarInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Car> cars;
	private Map<String, List<String>> urls;
	
	public List<Car> getCars() {
		return cars;
	}
	
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	
	public Map<String, List<String>> getUrls() {
		return urls;
	}
	
	public void setUrls(Map<String, List<String>> urls) {
		this.urls = urls;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
