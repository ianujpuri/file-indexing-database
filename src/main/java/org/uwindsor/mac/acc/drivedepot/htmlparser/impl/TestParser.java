package org.uwindsor.mac.acc.drivedepot.htmlparser.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.indextable.IndexTableProvider;
import org.uwindsor.mac.acc.drivedepot.invertedindex.dsstore.Trie;
import org.uwindsor.mac.acc.drivedepot.model.Car;
import org.uwindsor.mac.acc.drivedepot.model.Page;
import org.uwindsor.mac.acc.drivedepot.pagerank.PageRank;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

public class TestParser {

	protected static final Logger LOGGER = Logger.getLogger(TestParser.class);
	

	public static void main(String[] args) throws IOException {
		autoTrader();
		AutoTradeCAParser parser = new AutoTradeCAParser();
//		List<Car> cars = parser.getCars("AcuraUsedsaskatchewan");
		System.out.println(parser.getCars("AcuraUsedsaskatchewan"));
////		carPages();
//		showPrettyCarDeals(cars, "AcuraUsedsaskatchewan");

	}
	
	private static void showPrettyCarDeals(List<Car> cars, String key) {

		int count = 1;

		for (Car car : cars) {
			System.out.println("===========================");
			System.out.println(count + " : " + car.getCarModel());
			System.out.println("Price : " + car.getCarPrice());
			if (!StringUtils.isNullOrEmpty(car.getCarDealDiscount())) {
				System.out.println(car.getCarDealDiscount() + " !");
			}

			System.out.println("Description : " + car.getCarDetails());
			System.out.println("Seller Name: " + car.getCarSellerName());
			System.out.println();
			count++;
		}
		if (cars.size() > 0) {
			PageRank.PageRankingOfKeyword(cars.get(0).getCarMake());
			System.out.println("Found this combination on Page(s) : " + IndexTableProvider.getTrie().getPageInfo(key));
			System.out.println("Found the make on Page(s) : "
					+ IndexTableProvider.getTrie().getPageInfo(cars.get(0).getCarMake()));
			System.out.println("Found the seller on Page(s) : "
					+ IndexTableProvider.getTrie().getPageInfo(cars.get(0).getCarSellerName()));
			System.out.println("Found the Location on Page(s) : "
					+ IndexTableProvider.getTrie().getPageInfo(cars.get(0).getCarLocation()));
		}
	}
	
	public static void autoTrader() {
		try {
			AutoTradeCAParser autoTradeParser = new AutoTradeCAParser();
			File filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_AUTOTRADE_HTML_PATH));
			autoTradeParser.parseFiles(filePath);
			autoTradeParser.release();
//
			MazdaCarParser mazdaParser = new MazdaCarParser();
			File filePathM = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_MAZDA_HTML_PATH));
			mazdaParser.parseFiles(filePathM);
//			
//	
////			System.out.println(trieStore.getPages("VolkswagenUsedontario"));
//			
//			
////			System.out.println("Pages " + trieStore.getPageInfo("VolkswagenUsedontario"));
			mazdaParser.release();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error Occurred while parsing HTML..");
		}

	}
	
	public static void carPages() {
		try {
			
			File filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_CARPAGES_HTML_PATH));
//			File filePath = IOUtils.getFile("src/main/resources/CarPages - saskatchewan 1.txt");
			CarPagesParser parser = new CarPagesParser();
			parser.parseFiles(filePath);
			Trie trieStore = IndexTableProvider.getTrie();
			System.out.println("Pages " + trieStore.getPageInfo("TOYOTA"));
			System.out.println("Pages " + trieStore.getPageInfo("ONTARIO"));

			parser.release();
//			parser.utilityTemp();
//			parser.getCars("ONEKEY");
			
		} catch (IOException e) {
			LOGGER.error("Error Occurred while parsing HTML..");
		}
	}
	
	
}
