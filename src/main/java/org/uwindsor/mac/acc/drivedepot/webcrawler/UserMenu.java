package org.uwindsor.mac.acc.drivedepot.webcrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.exception.ExitException;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.AutoTradeCAParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.CarPagesParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.MazdaCarParser;
import org.uwindsor.mac.acc.drivedepot.indextable.IndexTableProvider;
import org.uwindsor.mac.acc.drivedepot.model.Car;
import org.uwindsor.mac.acc.drivedepot.model.InputValidator;
import org.uwindsor.mac.acc.drivedepot.pagerank.PageRank;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
 * This is the User Menu class which is primarily used to interact with user
 * Input. 
 * 
 * @author Anuj Puri (110120950), Abdul Rahman Mohammed(110128321)
 *
 */
public class UserMenu {

	private static final WebCrawler WEB_CRAWLER = new WebCrawler();
	private static final AVLSpellChecker SPELL_CHECKER = new AVLSpellChecker();
	private static final SearchFrequencyHeap searchHistory = new SearchFrequencyHeap();

	private static AutoTradeCAParser autoParser = null;
	private static CarPagesParser carPageParser = null;
	private static MazdaCarParser mazdaCarParser = null;

	public void init() {
		try {
			autoParser = new AutoTradeCAParser();
			carPageParser = new CarPagesParser();
			mazdaCarParser = new MazdaCarParser();
			displayMainMenu();
		} catch (Exception e) {
			// e
		}
	}

	/**
	 * Main menu
	 */
	private static void displayMainMenu() {
		System.out.println(Constants.MENU_WELCOME_MESSAGE);
		Scanner scanner = IOUtils.getConsoleScanner();

		boolean exitMenu = false;
		do {
			try {
				int choice;
				System.out.println("1. Get best car deals.");
				System.out.println("2. View search history.");
				System.out.println("3. Exit.");
				System.out.println("===========================");
				System.out.print("Enter your choice: ");
				choice = scanner.nextInt();
				scanner.nextLine();
				switch (choice) {
				case 1:
					menuLocationSelection();
					exitMenu = true;
					break;
				case 2:
					SearchFrequencyHeap.displayWordFrequencies();
					break;
				case 3:
					// Add more options as needed
					System.out.println("Exiting the program. Goodbye!");
					exitMenu = true;
					System.exit(0);
					break;
				default:
					System.out.println("Invalid choice. Please enter a valid option.");
				}
			} catch (Exception e) {
				System.out.println("Invalid choice. Please enter a valid option.");

				/*
				 * This is one such non-sense implementation by SUN or Oracle So putting a hack,
				 * don't mind this comment.
				 */
				scanner.nextLine(); // consuming extra char
			}
		} while (!exitMenu);
	}

	/*
	 * Sub menu
	 */
	private static void menuLocationSelection() {
		System.out.println("===========================");
		System.out.println("1. Enter location to proceed.");
		System.out.println("2. Go back to main menu");
		System.out.println("===========================");
		Scanner scanner = IOUtils.getConsoleScanner();
		boolean exitMenu = false;
		int choice;
		do {
			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character

				switch (choice) {
				case 1:
					System.out.println("\n===== Location List =====");
					String location = displayLocations(getLocationMap());
					menuGenerateCarData(location);
					exitMenu = true;
					break;
				case 2:
					// Add more options as needed
					displayMainMenu();
					exitMenu = true;
					break;
				default:
					System.out.println("Invalid choice. Please enter a valid option.");
				}
			} catch (Exception e) {
				System.out.println("Invalid choice. Please enter a valid option.");
				scanner.nextLine();
			}
		} while (!exitMenu);

//        String userInput = webCrawlerMenu.getUserInput(Constants.MAIN_MENU_OPTION_LOCATION);
//        runWordCompletion(userInput, ConfigUtil.getpropertyValue(Constants.KEY_DICTIONARY_LOCATION));
	}

	public static void menuGenerateCarData(String location) {

		boolean exitMenu = false;
		Scanner scanner = IOUtils.getConsoleScanner();
		int choice;
		do {
			try {
				System.out.println("===========================");
				System.out.println("1. Crawl live data.");
				System.out.println("2. Get Deals from existing data");
				System.out.println("3. Change Location");
				System.out.println("4. View search history");
				System.out.println("5. Return to main menu.");
				System.out.println("===========================");
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character

				switch (choice) {
				case 1:
					// craw live data logic
					crawLiveData(getLocationMap(), location);
					getDeals(location);
					exitMenu = true;
					break;
				case 2:
					getDeals(location);
					exitMenu = true;
					break;
				case 3:
					menuLocationSelection();
					exitMenu = true;
					break;
				case 4:
					SearchFrequencyHeap.displayWordFrequencies();
					break;
				case 5:
					displayMainMenu();
					exitMenu = true;
					break;
				default:
					System.out.println("Invalid choice. Please enter a valid option.");
				}
			} catch (ExitException e) {
				// ignore, someone just typed exit.
			} catch (Exception e) {
				System.out.println("Something went wrong, hit enter to try again...");
//				e.printStackTrace();
				scanner.nextLine();
			}
		} while (!exitMenu);

	}

	private static void getDeals(String location) throws Exception {

		StringBuilder key = new StringBuilder();

		String make = displayCarMakes();

		make = StringUtils.toCamelCase(make);
		make = StringUtils.trimIt(make);

		location = StringUtils.trimIt(location);

		String type = StringUtils.trimIt(getCarTypeMenu());

		StringUtils.trimIt(key.append(make).append(type).append(location).toString());

		System.out.println("Searching data for combination : " + location + " <> " + make + " <> " + type);
		int totalCount = 0;
		List<Car> carsList = autoParser.getCars(key.toString());
		totalCount += carsList.size();
		showPrettyCarDeals(carsList, key.toString());
		carsList.clear();

		carsList.addAll(carPageParser.getCars(key.toString()));
		totalCount += carsList.size();
		showPrettyCarDeals(carsList, key.toString());
		carsList.clear();

		carsList.addAll(mazdaCarParser.getCars(key.toString()));
		totalCount += carsList.size();
		showPrettyCarDeals(carsList, key.toString(), totalCount == 0 ? -1 : totalCount);

		menuGenerateCarData(location);
	}

	private static void showPrettyCarDeals(List<Car> cars, String key) {

		showPrettyCarDeals(cars, key, 1);
	}

	private static void showPrettyCarDeals(List<Car> cars, String key, int totalCount) {

		int count = 1;

		for (Car car : cars) {
			if (!StringUtils.isNullOrEmpty(car.getCarPrice())) {
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
		} else if (totalCount < 0) {
			System.out.println("Sorry, could not find deals. Please try another combination.");
		}
	}

	/**
	 * Main menu
	 * 
	 * @throws Exception
	 */
	private static String getCarTypeMenu() throws Exception {

		boolean exitMenu = false;
		do {
			String choice;
			System.out.println("Choose the type of the Car.");
			System.out.println("1. New");
			System.out.println("2. Used");
			choice = IOUtils.promptForString("Choose the Option or type 'exit' to previous menu)");

			choice = StringUtils.toLowercase(choice);

			// validate if user input is a number
			if (InputValidator.isValidNumber(choice)) {
				switch (choice) {
				case "1":
					exitMenu = true;
					return Constants.TYPE_CAR_NEW;
				case "2":
					exitMenu = true;
					return Constants.TYPE_CAR_USED;
				case "exit":
					exitMenu = true;
					throw new ExitException("Exiting this menu");
				default:
					System.out.println("Invalid choice. Please enter a valid option.");
				}
			} else {
				System.out.println("Invalid choice. Please enter a valid option.");
			}
		} while (!exitMenu);

		return Constants.TYPE_CAR_USED;
	}

	private static void crawLiveData(Map<String, LocationInfo> locationInfoMap, String location) throws Exception {
		LocationInfo selectedLocation = locationInfoMap.get(location);

		// Print key, postal code, and abbreviation
		String postalCode = selectedLocation.getPostalCode();
		String abbreviation = selectedLocation.getAbbreviation();

		System.out.println(
				"Hold tight, we're unleashing our data-scraping ninjas to hunt down the hottest deals in real-time! ");
		CrawlingAutoTrader crawlAT = new CrawlingAutoTrader();
		CrawlingCarPages crawlCP = new CrawlingCarPages();
		CrawlingMazda crawlMZ = new CrawlingMazda();

		crawlAT.startCrawling(postalCode, location);
//		crawlCP.startCrawling(location);
		crawlMZ.startCrawling(abbreviation, location);
		parseHtmls();
	}

	public static void parseHtmls() {
		try {
			File filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_AUTOTRADE_HTML_PATH));
			autoParser.parseFiles(filePath);
			autoParser.release();

			filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_CARPAGES_HTML_PATH));
			carPageParser.parseFiles(filePath);
			carPageParser.release();

			filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_MAZDA_HTML_PATH));
			mazdaCarParser.parseFiles(filePath);
			mazdaCarParser.release();

		} catch (Exception e) {
			// ignore
		}
	}

	private static Map<String, LocationInfo> getLocationMap() {
		Map<String, LocationInfo> locationInfoMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		locationInfoMap.put("Alberta", new LocationInfo("T2N 1N4", "AB"));
//		locationInfoMap.put("British Columbia", new LocationInfo("V6T 1Z4", "BC"));
		locationInfoMap.put("Manitoba", new LocationInfo("R3T 2N2", "MB"));
		locationInfoMap.put("New Brunswick", new LocationInfo("E3B 5H1", "NB"));
		locationInfoMap.put("Newfoundland and Labrador", new LocationInfo("A1A 1A1", "NL"));
		locationInfoMap.put("Nova Scotia", new LocationInfo("B3H 4R2", "NS"));
		locationInfoMap.put("Ontario", new LocationInfo("M5B2E2", "ON"));
		locationInfoMap.put("Prince Edward Island", new LocationInfo("C1A 1B2", "PE"));
		locationInfoMap.put("Quebec", new LocationInfo("H2Y 1C6", "QC"));
		locationInfoMap.put("Saskatchewan", new LocationInfo("S7K 3S5", "SK"));

		return locationInfoMap;
	}

	private static String displayCarMakes() throws Exception {
		String dictionaryLocationPath = ConfigUtil.getpropertyValue(Constants.KEY_DICTIONARY_BRANDS);
		List<String> listOfMakes = IOUtils.getPropertiesList(dictionaryLocationPath);

		String makeChoice = StringUtils.EMPTY_BLANK_STRING;
		System.out.println("List of Car Makes available: ");
		for (String make : listOfMakes) {
			System.out.println(make);
		}

		Scanner scanner = IOUtils.getConsoleScanner();
		do {
			try {
				System.out.println("Please enter a Car Make from the above list. ");
				String userCarMakeInput = StringUtils.toCamelCase(StringUtils.trimIt(scanner.next()));
				if (InputValidator.isValidText(userCarMakeInput)) {
					if (listOfMakes.contains(userCarMakeInput)) {
						SearchFrequencyHeap.searchFrequencyMap(userCarMakeInput);
						makeChoice = userCarMakeInput;
						break;
					} else {
						makeChoice = runWordCompletion(userCarMakeInput, dictionaryLocationPath);
						if (!StringUtils.isNullOrEmpty(makeChoice)) {
							break;
						} else {

							makeChoice = runSpellChecker(userCarMakeInput, dictionaryLocationPath);
							if (!StringUtils.isNullOrEmpty(makeChoice)) {
								break;
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// do nothing
			} catch (Exception e) {
				System.out.println("Invalid choice. Please enter a valid option.");
				scanner.nextLine();
			}
		} while (true);

		return StringUtils.trimIt(makeChoice);
	}

	private static String displayLocations(Map<String, LocationInfo> locationInfoMap) {
		String dictionaryLocationPath = ConfigUtil.getpropertyValue(Constants.KEY_DICTIONARY_LOCATION);
		String locationChoice = StringUtils.EMPTY_BLANK_STRING;
		System.out.println("List of locations available: ");
		for (String province : locationInfoMap.keySet()) {
			System.out.println(province);
		}

		Scanner scanner = IOUtils.getConsoleScanner();
		do {
			try {
				System.out.println("Please enter a location from the above list. ");
				String userLocationInput = StringUtils.toLowercase(StringUtils.trimIt(scanner.next()));
				if (InputValidator.isValidText(userLocationInput)) {
					if (locationInfoMap.containsKey(userLocationInput)) {
						SearchFrequencyHeap.searchFrequencyMap(userLocationInput);
						locationChoice = userLocationInput;
						break;
					} else {
						locationChoice = runWordCompletion(userLocationInput, dictionaryLocationPath);
						if (!StringUtils.isNullOrEmpty(locationChoice)) {
							break;
						} else {
							locationChoice = runSpellChecker(userLocationInput, dictionaryLocationPath);
							if (!StringUtils.isNullOrEmpty(locationChoice)) {
								break;
							}
						}
					}
				}

			} catch (IllegalArgumentException e) {
				// do nothing
			} catch (Exception e) {
				System.out.println("Invalid choice. Please enter a valid option.");
				scanner.nextLine();
			}
		} while (true);

		return StringUtils.trimIt(locationChoice);
	}

	private static String runWordCompletion(String userInput, String filePath) {
		List<String> list = new ArrayList<>();
		WordCompletion wordCompletion = new WordCompletion(filePath);
		list.addAll(wordCompletion.WordCompletor(userInput));
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				System.out.println((i + 1) + ". " + list.get(i));
			}
			String choice = IOUtils
					.promptForString("Select the option you feel is closest (or press 'N' to enter location again): ");
			if (choice.equalsIgnoreCase("N") || !StringUtils.isNumeric(choice)) {
				throw new IllegalArgumentException("bad choice.");
			} else {
				int choiceIndex = Integer.parseInt(choice) - 1; // Adjust for 0-based indexing
				if (choiceIndex >= 0 && choiceIndex < list.size()) {
					choice = list.get(choiceIndex);
					// Use the selectedOption as needed
					System.out.println("You selected: " + choice);
					SearchFrequencyHeap.searchFrequencyMap(choice);
					return choice;
				} else {
					throw new IllegalArgumentException("bad choice.");
				}
			}
		}
		return StringUtils.EMPTY_BLANK_STRING;
	}

	private static String runSpellChecker(String userInput, String filePath) {
		List<String> list = new ArrayList<>();
		list.addAll(SPELL_CHECKER.spellCheckm(userInput, filePath, true));
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				System.out.println((i + 1) + ". " + list.get(i));
			}
			String choice = IOUtils
					.promptForString("Did you mean any of the following? (or press 'N' to enter location again): ");

			if (choice.equalsIgnoreCase("N") || !StringUtils.isNumeric(choice)) {
				throw new IllegalArgumentException("bad choice.");
			} else {
				int choiceIndex = Integer.parseInt(choice) - 1; // Adjust for 0-based indexing
				if (choiceIndex >= 0 && choiceIndex < list.size()) {
					choice = list.get(choiceIndex);
					// Use the selectedOption as needed
					System.out.println("You selected: " + choice);
					SearchFrequencyHeap.searchFrequencyMap(choice);
					return choice;
				} else {
					throw new IllegalArgumentException("bad choice.");
				}
			}
		}
		return StringUtils.EMPTY_BLANK_STRING;

	}

}
