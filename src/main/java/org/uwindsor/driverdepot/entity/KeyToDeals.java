package org.uwindsor.driverdepot.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

@Component
public class KeyToDeals implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String location;
	private String carMake;
	private String carType;
	
	public String getLocation() {
		return StringUtils.toLowercase(this.location);
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getCarMake() {
		return carMake;
	}
	
	public void setCarMake(String carMake) {
		this.carMake = carMake;
	}
	
	public String getCarType() {
		return StringUtils.toCamelCase(carType);
	}
	
	public void setCarType(String carType) {
		this.carType = carType;
	}
	
}
