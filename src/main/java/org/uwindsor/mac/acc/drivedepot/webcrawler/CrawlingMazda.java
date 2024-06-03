/*
 * Developed by: Abdul Rahman Mohammed
 * Student ID: 110128321
 * 
 * Methods Included:
 * 1. startCrawling(String x)
 * 2. createScrapedFile(WebDriver, int, String)
 * 3. displayLocations (Map<String, String>)
 * 4. getUserInput (String)
 * 5. getPostalCode (Map<String, String>, String)
 * 6. crawlAutoTraderSite()
 * */

package org.uwindsor.mac.acc.drivedepot.webcrawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;

public class CrawlingMazda {

	public void startCrawling(String userInput, String originalKey) throws Exception {

		// Define Category
		String category = "New";
		// Create a Chrome WebDriver instance
		WebDriver driver = new ChromeDriver();

		// Navigate to homepage
		driver.get("https://www.mazda.ca/en/shopping/build-and-price/");
		Thread.sleep(15000);
		WebElement locationSearch = driver.findElement(By.className("mz-dropdown-select"));
		Select select = new Select(locationSearch);

		// Iterate through the options and select the desired one
		for (WebElement option : select.getOptions()) {
			if (option.getText().equals(userInput)) {
				Thread.sleep(8000);
				option.click();
				break;
			}
		}

		Thread.sleep(2000);

		createScrapedFile(driver, originalKey, category);
		
		// Close the browser when done
		driver.quit();
	}

	public static void createScrapedFile(WebDriver driver, String originalKey, String category) {

		WebElement result = driver.findElement(By.className("mz-tabs__content"));
		String fileName = "Mazda - " + originalKey + " - " + category;
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(result.getAttribute("outerHTML"));
		try {
			String fileSeperator = FileSystems.getDefault().getSeparator();
			String filePath = ConfigUtil.getpropertyValue(Constants.DIR_MAZDA_HTML_PATH) + fileSeperator
					+ originalKey + fileSeperator + fileName + ".txt";

			// Create a FileWriter object with the specified file path
			FileWriter fileWriter = new FileWriter(IOUtils.getFile(filePath));

			// Wrapping FileWriter in BufferedWriter for efficient writing
			BufferedWriter buffered_writer = new BufferedWriter(fileWriter);
			buffered_writer.write(strBuffer.toString());

			// Close the BufferedWriter to flush the stream and close the file
			buffered_writer.close();
			strBuffer.setLength(0);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
