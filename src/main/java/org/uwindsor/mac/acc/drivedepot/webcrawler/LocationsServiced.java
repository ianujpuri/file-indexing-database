package org.uwindsor.mac.acc.drivedepot.webcrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.AutoTradeCAParser;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.CarPagesParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

class LocationInfo {
	private String postalCode;
	private String abbreviation;

	public LocationInfo(String postalCode, String abbreviation) {
		this.postalCode = postalCode;
		this.abbreviation = abbreviation;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public static String enterLocation(WebCrawler user_input) {
		return user_input.getUserInput("Enter the location/province you belong to: ");
	}
}

public class LocationsServiced {
	public static String userLocationInput;
	public static boolean found;
	private static AutoTradeCAParser autoParser = null;
	private static CarPagesParser carPageParser = null;

	public LocationsServiced() {
		init();
	}

	public void init() {
		try {
			autoParser = new AutoTradeCAParser();
			carPageParser = new CarPagesParser();
		} catch (Exception e) {
			// e
		}
	}

	// private static void nextSteps(Map<String, LocationInfo> locationInfoMap,
	// String userLocationInput, webCrawler webCrawler) throws Exception {
	public static void nextSteps(Map<String, LocationInfo> locationInfoMap, WebCrawler webCrawler) throws Exception {
		found = false;

		for (Map.Entry<String, LocationInfo> entry : locationInfoMap.entrySet()) {
//			String key = entry.getKey().toLowerCase().trim(); // Convert key to lowercase and remove spaces

			// if (key.equals(userLocationInput)) {
			if (locationInfoMap.containsKey(userLocationInput.toString())) {
				// If the location is found, get the corresponding LocationInfo object
				LocationInfo selectedLocation = locationInfoMap.get(userLocationInput);
//				LocationInfo selectedLocation = entry.getValue();

				// Print key, postal code, and abbreviation
				String originalKey = userLocationInput; // Use the original key for printing
				String postalCode = selectedLocation.getPostalCode();
				String abbreviation = selectedLocation.getAbbreviation();
				found = true;

				String liveOldData = webCrawler
						.getUserInput("Do you want to get live data or use the existing deals? Please enter Y or N");
				if (liveOldData.equalsIgnoreCase("y")) {
					System.out.println(
							"🚀 Hold tight, we're unleashing our data-scraping ninjas to hunt down the hottest deals in real-time! ");
					CrawlingAutoTrader crawlAT = new CrawlingAutoTrader();
//					CrawlingCarPages crawlCP = new CrawlingCarPages();
//					CrawlingMazda crawlMZ = new CrawlingMazda();

					crawlAT.startCrawling(postalCode, originalKey);
//					crawlCP.startCrawling(originalKey);
//					crawlMZ.startCrawling(abbreviation, originalKey);
					found = true;
					parseHtmls(webCrawler);
					return;
				} else if (liveOldData.equalsIgnoreCase("n")) {
					System.out.println("Great! Let us get you the great deals available!");
					found = true;
					parseHtmls(webCrawler);
					// Parsing code comes here.
					return;
				} else {
					liveOldData = webCrawler.getUserInput("Invalid input! Please enter Y or N");
					// System.out.println("Invalid input! Please enter Y or N");
				}
			}
		}
		if (!found) {
			System.out.println("Please select from locations available.");
			userLocationInput = LocationInfo.enterLocation(webCrawler);
		}
	}
	
	public static void parseHtmls(WebCrawler webCrawler) {
		try {
			File filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_AUTOTRADE_HTML_PATH));
			autoParser.parseFiles(filePath);
			autoParser.release();

			filePath = IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.DIR_CARPAGES_HTML_PATH));
			carPageParser.parseFiles(filePath);
			carPageParser.release();
		} catch (Exception e) {
			// e
		}
		inputFromUser(webCrawler);
	}

	public static void inputFromUser(WebCrawler webcrawler) {
		boolean exit = false;
		do {
			String brandChoice = webcrawler.getUserInput(Constants.QUERY_MAKE_CHOICE);
			exit = brandChoice.equalsIgnoreCase("exit");
			if (!exit) {
				WordCompletion brandNameSuggest = new WordCompletion(ConfigUtil.getpropertyValue(Constants.KEY_DICTIONARY_BRANDS));
				List<String> suggestions = brandNameSuggest.WordCompletor(brandChoice);
				System.out.println(suggestions);
			}
			
			String yearChoice = webcrawler.getUserInput(Constants.QUERY_MAKE_CHOICE);
			exit = yearChoice.equalsIgnoreCase("exit");
			if (!exit) {
				
			}
			String typeChoice = webcrawler.getUserInput(Constants.QUERY_MAKE_CHOICE);
			exit = typeChoice.equalsIgnoreCase("exit");
			if (!exit && (typeChoice.equalsIgnoreCase(Constants.TYPE_CAR_USED) || typeChoice.equalsIgnoreCase(Constants.TYPE_CAR_NEW))) {
				typeChoice = StringUtils.toCamelCase(typeChoice);
			}
		} while (!exit);
	}

	private static void displayLocations(Map<String, LocationInfo> locationInfoMap) {
		System.out.println("List of locations available: ");
		for (String province : locationInfoMap.keySet()) {
			System.out.println(province);
		}
	}

	public void driveDepotMain() throws Exception {
		Map<String, LocationInfo> locationInfoMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		locationInfoMap.put("Alberta", new LocationInfo("T2N 1N4", "AB"));
		locationInfoMap.put("British Columbia", new LocationInfo("V6T 1Z4", "BC"));
		locationInfoMap.put("Manitoba", new LocationInfo("R3T 2N2", "MB"));
		locationInfoMap.put("New Brunswick", new LocationInfo("E3B 5H1", "NB"));
		locationInfoMap.put("Newfoundland and Labrador", new LocationInfo("A1A 1A1", "NL"));
		locationInfoMap.put("Nova Scotia", new LocationInfo("B3H 4R2", "NS"));
		locationInfoMap.put("Ontario", new LocationInfo("M5B2E2", "ON"));
		locationInfoMap.put("Prince Edward Island", new LocationInfo("C1A 1B2", "PE"));
		locationInfoMap.put("Quebec", new LocationInfo("H2Y 1C6", "QC"));
		locationInfoMap.put("Saskatchewan", new LocationInfo("S7K 3S5", "SK"));

		displayLocations(locationInfoMap);

		WebCrawler webCrawler = new WebCrawler();
		AVLSpellChecker spellChecker = new AVLSpellChecker();

		userLocationInput = LocationInfo.enterLocation(webCrawler);
		// Trim the user input to remove leading and trailing spaces
		userLocationInput = userLocationInput.trim();
		boolean locationExists = false;

		// Check spelling and if "wrong" or "incomplete" - suggest words from
		// dictionary.
		List<String> spellCheckSuggestions = new ArrayList<String>();

		do {
			/*
			 * Perform Spell Check and if not satisfied then Word Completion suggestions
			 */
			if (!(locationInfoMap.containsKey(userLocationInput)
					|| (spellCheckSuggestions = spellChecker.spellCheckm(userLocationInput)).isEmpty())) {
				System.out.println("\nDid you mean any of these? ");
				for (int i = 0; i < spellCheckSuggestions.size(); i++) {
					System.out.println((i + 1) + ". " + spellCheckSuggestions.get(i));
				}
				String userChoice = webCrawler
						.getUserInput("Select the option you feel is closest (or press 'N' to enter location again): ");

				if (userChoice.equalsIgnoreCase("N")) {
					// User wants to enter location again
					userLocationInput = LocationInfo.enterLocation(webCrawler);
					// Trim the user input to remove leading and trailing spaces
					userLocationInput = userLocationInput.trim();

					WordCompletion wordCompletion = new WordCompletion(ConfigUtil.getpropertyValue(Constants.KEY_DICTIONARY_LOCATION));
					List<String> wordCompletionSuggestions = wordCompletion.WordCompletor(userLocationInput);

					if (!wordCompletionSuggestions.isEmpty()) { 
						System.out.println("\nDo you mean? ");
						for (int i = 0; i < wordCompletionSuggestions.size(); i++) {
							System.out.println((i + 1) + ". " + wordCompletionSuggestions.get(i));
						}
						userChoice = webCrawler.getUserInput("If yes! Select number");
						try {
							int choiceIndex = Integer.parseInt(userChoice) - 1; // Adjust for 0-based indexing
							if (choiceIndex >= 0 && choiceIndex < wordCompletionSuggestions.size()) {
								userLocationInput = wordCompletionSuggestions.get(choiceIndex);
								// Use the selectedOption as needed
								System.out.println("You selected: " + userLocationInput);
								nextSteps(locationInfoMap, webCrawler);
								break;
							} else {
								System.out.println("Invalid choice. Please select a valid option.");
							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid input. Please enter a number.");
						}
					} else {
						// Continue the loop to suggest from spell checking
						continue;
					}
				} else {
					try {
						int choiceIndex = Integer.parseInt(userChoice) - 1; // Adjust for 0-based indexing
						if (choiceIndex >= 0 && choiceIndex < spellCheckSuggestions.size()) {
							userLocationInput = spellCheckSuggestions.get(choiceIndex);
							// MAKE SURE THE SUGGESTIONS ARE CORRECT!
							System.out.println("You selected: " + userLocationInput);
							nextSteps(locationInfoMap, webCrawler);
							return;
						} else {
							System.out.println("Invalid choice. Please select a valid option.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a number.");
					}
				}

			}
//			else if(locationExists) {
//				nextSteps(locationInfoMap, userLocationInput, webCrawler);
//			}
			else {
				// nextSteps(locationInfoMap, userLocationInput, webCrawler);
				nextSteps(locationInfoMap, webCrawler);
			}

		} while (!locationInfoMap.containsKey(userLocationInput));
		if (!found) {
			nextSteps(locationInfoMap, webCrawler);
		}

	}

}
