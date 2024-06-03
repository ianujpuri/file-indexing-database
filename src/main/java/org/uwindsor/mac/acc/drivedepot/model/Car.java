package org.uwindsor.mac.acc.drivedepot.model;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
 * Car object for wrapping the Row 
 * for easy retrieval of information
 * @author Anuj Puri(110120950)
 *
 */
public class Car {

	private Row row = null;
	
	//cannot instatiate using no-args constructor
	private Car() {

	}

	public Car(Row row) {
		this();
		this.row = row;
	}
	
	public Car(RowConfig rConfig, String record) {
		this();
		this.row = new Row(rConfig);
		String r[] = StringUtils.toStringArray(record, StringUtils.SYMBOL_TILDA, true);
		
		if(r != null && r.length < rConfig.getColumnLength()) {
			return;
		}
			
		for(int i = 0; i <rConfig.getColumnLength(); i++) {
			this.row.setColumnValue(i, r[i]);
		}
		
	}
	
	public Car(RowConfig rConfig, String[] record) {
		this();
		this.row = new Row(rConfig);
		
		if(record != null && record.length < rConfig.getColumnLength()) {
			return;
		}
			
		for(int i = 0; i <rConfig.getColumnLength(); i++) {
			this.row.setColumnValue(i, record[i]);
		}
		
	}

	public String getCarTitle() {
		return row.getColumnValue(Constants.HEADER_CAR_TITLE);
	}

	public String getCarDetails() {
		return row.getColumnValue(Constants.HEADER_CAR_DETAILS);
	}

	public String getCarSellerName() {
		return row.getColumnValue(Constants.HEADER_CAR_SELLER_NAME);
	}

	public String getCarSellLocation() {
		return row.getColumnValue(Constants.HEADER_CAR_LOCATION_DISTANCE);
	}
	
	public String getCarLocation() {
		return row.getColumnValue(Constants.HEADER_CAR_LOCATION);
	}
 
	public String getCarOdometer() {
		return row.getColumnValue(Constants.HEADER_CAR_ODOMETER);
	}

	public String getCarPrice() {
		return row.getColumnValue(Constants.HEADER_CAR_PRICE);
	}

	public String getCarDealDiscount() {
		return row.getColumnValue(Constants.HEADER_CAR_DEAL);
	}
	
	public String getCarYear() {
		return row.getColumnValue(Constants.HEADER_CAR_YEAR);
	}
	
	public String getCarMake() {
		return row.getColumnValue(Constants.HEADER_CAR_MAKE);
	}
	
	public String getCarModel() {
		return row.getColumnValue(Constants.HEADER_CAR_MODEL);
	}
	
	public String getCarType() {
		return row.getColumnValue(Constants.HEADER_CAR_TYPE);
	}
	
	public String getCarImageUrl() {
		return row.getColumnValue(Constants.HEADER_CAR_IMG_URL);

	}
	
	
	public String toString() {
		return this.row.toString();
	}

}
