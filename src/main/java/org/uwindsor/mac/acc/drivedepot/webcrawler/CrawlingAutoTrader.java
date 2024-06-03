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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
public class CrawlingAutoTrader {
	
	public void startCrawling(String userInput, String originalKey) throws Exception {
				 
        // Create a Chrome WebDriver instance
        WebDriver driver = new ChromeDriver();
 
        // Navigate to Autotrader's homepage
        driver.get("https://www.autotrader.ca/");
        Thread.sleep(5000);
        
        //Define Category
        String category;
        // Find the location input field
        WebElement searchBox = driver.findElement(By.xpath("//body/div[@id='wrapper']/div[1]/article[1]/section[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[5]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/form[1]/input[1]"));
        searchBox.click();
    	searchBox.sendKeys(userInput);
    	
    	//Click "search" button
        WebElement currentLoc = driver.findElement(By.id("SearchButton"));
        currentLoc.click();
        Thread.sleep(5000);
        
        // Select "100" entries per page
        WebElement noOfItems = driver.findElement(By.xpath("//body/div[@id='wrapper']/div[1]/div[1]/div[1]/div[4]/div[2]/div[2]/div[1]/select[1]/option[4]"));
        noOfItems.click();
        Thread.sleep(8000);

        //logic to click NEW, crawl and create files for new
        category = "New";
        driver.findElement(By.id("faceted-parent-Condition")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//label[contains(text(),'Used')]")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//label[contains(text(),'Certified Pre-Owned')]")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("applyCondition")).click();
        Thread.sleep(8000);
        //Click next button and start iterations
        WebElement nextButton = driver.findElement(By.className("last-page-link"));
        iteratePages(nextButton, driver, originalKey, category);
        
        //logic to click USED, crawl and create files for old
        category = "Used";
        driver.findElement(By.id("faceted-parent-Condition")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//label[contains(text(),'Used')]")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//label[contains(text(),'Certified Pre-Owned')]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//label[contains(text(),'New')]")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("applyCondition")).click();
        Thread.sleep(8000);
        nextButton = driver.findElement(By.className("last-page-link"));
        iteratePages(nextButton, driver, originalKey, category);
     // Close the browser when done
        driver.quit();
	}
	
	private static void iteratePages(WebElement nextButton,WebDriver driver, String originalKey, String category ) {
		 int pageCount = 3;
	        // Iterate the first 3 pages available
	        if(nextButton != null) {
	        	for (int i = 1; i <= pageCount; i++) {
	        		try {
	        		createScrapedFile(driver, i, originalKey, category);
	        		Thread.sleep(8000);
					nextButton.click();
					Thread.sleep(8000);
					nextButton = driver.findElement(By.className("last-page-link"));
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
	        	}
	        }
	        else {
	        	System.out.println("Encountered an error while finding more pages");
	        }
	}
	
    public static void createScrapedFile(WebDriver driver, int fileCount, String originalKey, String category) {

		WebElement result = driver.findElement(By.xpath("//div[@id='SearchListings']"));
		String fileName = "AutoTrader - " + originalKey + " " + fileCount + " - " + category;
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(result.getAttribute("outerHTML"));
	
		try {
		// Specify the file path
		String fileSeperator = FileSystems.getDefault().getSeparator();
		String filePath = ConfigUtil.getpropertyValue(Constants.DIR_AUTOTRADE_HTML_PATH)+fileSeperator+ originalKey + fileSeperator + fileName + ".txt";
		
		// Creating a FileWriter object with the specified file path
		FileWriter fileWriter = new FileWriter(IOUtils.getFile(filePath));
		
		// Wrap the FileWriter in a BufferedWriter
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(strBuffer.toString());
		
		// Close the BufferedWriter to flush the stream and close the file
		bufferedWriter.close();
		strBuffer.setLength(0);
		
		} catch (IOException e) {
		    e.printStackTrace();
		} 
    }

    }