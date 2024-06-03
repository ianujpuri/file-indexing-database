package org.uwindsor.mac.acc.drivedepot.constants;

import java.util.HashMap;

/**
 * Common class to add literals and constants
 * @author 110120950 (Anuj Puri)
 *
 */
public final class Constants {

	 
	
//	public static final String KEY_CAR_BRANDS = "key.car.make";
//	public static final String KEY_CAR_YEAR = "key.car.year";
	public static final String KEY_DICTIONARY_BRANDS = "key.dictionary.brands";
	public static final String KEY_DICTIONARY_LOCATION = "key.dictionary.location";
	public static final String KEY_PATH_CHROME_WEBDRIVER = "key.webdriver.chrome";
	
	/*
	 * Common config values
	 * Future scope to add multi-threading for parsing 
	 */
	public static final String THREAD_COUNT_WRITING = "WRITING";
	public static final String THREAD_COUNT_EXTRACTOR = "EXTRACTOR";
	
	//delta record trans type
	//for future scope to notify the new deals
	public static final String TRANS_TYPE_NEW = "NEW";
	public static final String TRANS_TYPE_MODIFY = "MOD";
	public static final String TRANS_TYPE_DELETE = "DEL";
	
	public static final String 	KEY_TRANS_TYPE = "TRANS_TYPE";
	public static final String 	KEY_TIMESTAMP = "TIMESTAMP";
	
	public static final String TYPE_CAR_USED = "Used";
	public static final String TYPE_CAR_NEW = "New";
	
	
	//OUTPUT HEADER FOR FILE
	public static final String KEY_HEADER_FILE = "app.autocartrader.output.header"; 
	public static final String HEADER_CAR_TITLE = "CARS_TITLE";
	public static final String HEADER_CAR_DETAILS = "CAR_DETAILS";
	public static final String HEADER_CAR_SELLER_NAME = "SELLER_NAME";
	public static final String HEADER_CAR_PRICE = "CAR_PRICE";
	public static final String HEADER_CAR_DEAL = "CAR_DEAL";
	public static final String HEADER_CAR_LOCATION_DISTANCE = "CAR_LOCATION_DISTANCE";
	public static final String HEADER_CAR_ODOMETER = "CAR_ODOMETER";
	public static final String HEADER_CAR_MAKE = "CAR_MAKE";
	public static final String HEADER_CAR_MODEL = "CAR_MODEL";
	public static final String HEADER_CAR_YEAR = "CAR_YEAR";
	public static final String HEADER_CAR_TYPE = "CAR_TYPE";
	public static final String HEADER_CAR_LOCATION = "CAR_LOCATION";
	public static final String HEADER_CAR_IMG_URL = "CAR_IMG_URL";
	
	//literals for Auto-trade Car data processing  
	public static final String KEY_OUPUT_PATH_AUTOTRADE = "key.output.path.autotrade";
	public static final String KEY_INDEX_AUTOTRADE = "key.index.autotrade";
	public static final String URL_AUTOTRADER_CA = "https://www.autotrader.ca/";
	public static final String DIR_AUTOTRADE_HTML_PATH = "key.autotrade.data.dir";
	public static final String KEY_XPATH_PARENT_LISTING = "xpath.car.listing";
	public static final String KEY_ID_CAR_LISTINGS = "id.car.listings";
	public static final String ELEMENT_CARS_LIST ="div.result-item-inner";
	public static final String ELEMENT_SELLER_NAME = "div.seller-name";
	public static final String ELEMENT_CAR_DETAILS = "p.details";
	public static final String ELEMENT_CARS_TITLE ="span.title-with-trim";
	public static final String ELEMENT_CAR_PRICE = "span.price-amount";
	public static final String ELEMENT_CAR_DEAL = "div.price-delta-text";
	public static final String ELEMENT_CAR_LOCATION_DISTANCE = "span.proximity-text";
	public static final String ELEMENT_CAR_ODOMETER = "span.odometer-proximity";
	public static final HashMap<String, String> MAP_ELEMENT_NAME_HEADER = new HashMap<>();
	static {
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CARS_TITLE, HEADER_CAR_TITLE);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_DETAILS, HEADER_CAR_DETAILS);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PRICE, HEADER_CAR_PRICE);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_DEAL, HEADER_CAR_DEAL);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_ODOMETER, HEADER_CAR_ODOMETER);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_SELLER_NAME, HEADER_CAR_SELLER_NAME);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_LOCATION_DISTANCE, HEADER_CAR_LOCATION_DISTANCE);
	}
	
	//literals for car pages data processing
	public static final String KEY_INDEX_CARPAGE = "key.index.carpage";
	public static final String KEY_OUPUT_PATH_CARPAGE = "key.output.path.carpage";
	public static final String URL_CARPAGES_CA = "https://www.carpages.ca/";
	public static final String DIR_CARPAGES_HTML_PATH = "key.carpages.data.dir";
	public static final String DIR_MAZDA_HTML_PATH = "key.mazda.data.dir";
	public static final String KEY_LISTITNG_SELECTOR_CLASS = "carpages.selector.class";
	public static final String ELEMENT_CAR_PAGE_TITLE = "hgroup.t-m-0 a";
	public static final String ELEMENT_CAR_PAGE_PRICE = "span.t-font-bold";
	public static final String ELEMENT_CAR_PAGE_DETAILS = "hgroup.t-m-0 h5.hN";
	public static final String ELEMENT_CAR_PAGE_SELLER_NAME = "hgroup.vehicle__card--dealerInfo h5.hN";
	public static final String ELEMENT_CAR_PAGE_LOCATION = " hgroup.vehicle__card--dealerInfo p.hN";
	public static final String ELEMENT_CAR_PAGE_ODOMETER = "div.t-col-span-full div.t-text-gray-500 span";
	static {
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_TITLE, HEADER_CAR_TITLE);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_DETAILS, HEADER_CAR_DETAILS);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_PRICE, HEADER_CAR_PRICE);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_ODOMETER, HEADER_CAR_ODOMETER);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_SELLER_NAME, HEADER_CAR_SELLER_NAME);
		MAP_ELEMENT_NAME_HEADER.put(ELEMENT_CAR_PAGE_LOCATION, HEADER_CAR_LOCATION_DISTANCE);
	}
		
	//mazda page literals
	public static final String KEY_OUTPUT_PATH_MAZDA = "key.output.path.mazda";
	public static final String KEY_INDEX_MAZDA = "key.index.mazda";
	public static final String URL_PAGE_MAZDA = "https://www.mazda.ca";
	public static final String KEY_PARENT_TAG_MAZDA = "div.mz-tabs__content";
	public static final String KEY_LISTING_SELECTOR_MAZDA = "div.mz-tabs__list";
	public static final String ELEMENT_CAR_MAZDA_TITLE = "div.mz-jelly-content";
	public static final String ELEMENT_CAR_MAZDA_TITLE_NAME =  ELEMENT_CAR_MAZDA_TITLE + " h5";
	public static final String ELEMENT_CAR_MAZDA_TITLE_PRICE =  ELEMENT_CAR_MAZDA_TITLE + " div";
	public static final String ELEMENT_CAR_MAZDA_DETAILS = "div.mz-jelly-iconslist";
	public static final String MAZDA_CAR_SELLER_NAME = "Mazda";
	public static final String MAZDA_CAR_TYPE = "New";
	public static final String MAZDA_CAR_LOCATION = "Canada";
	public static final String MAZDA_CAR_ODOMETER = "0 Kms";
	public static final String MAZDA_CAR_MAKE = "Mazda";
	public static final String MAZDA_CAR_YEAR = "2023";
	//User queries
	public static final String QUERY_MAKE_CHOICE = "Please enter your make choice ? (type exit, for home.)";
	public static final String QUERY_YEAR_CHOICE = "Please enter model year (2012-2023)? (type exit, for home.)";
	public static final String QUERY_TYPE_CHOICE = "Are you looking for Used or New cars ? (type exit, for home.)";
	
	//user menu
	
	//Welcome message
	public static final String MENU_WELCOME_MESSAGE = " Welcome to DriveDepot !! \nOne stop for driving Dreams";
	
	//main menu
	public static final String MAIN_MENU_OPTION_LOCATION = "Please choose the location from the following list.";
	
}
