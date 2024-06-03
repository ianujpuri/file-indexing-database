/*
 * Developed by: Abdul Rahman Mohammed
 * Student ID: 110128321
 * 
 * Methods Included:
 * 1. startCrawling(String x)
 * 2. createScrapedFile(WebDriver, int, String)
 * 3. displayLocations (Map<String, String>)
 * 4. getUserInput (String)
 * 5. getProvinceName (Map<String, String>, String)
 * 6. crawlAutoTraderSite()
 * */
package org.uwindsor.mac.acc.drivedepot.webcrawler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
public class CrawlingCarPages {
	
	public void startCrawling(String userInput) throws Exception { 
        // Create a Chrome WebDriver instance
        WebDriver driver = new ChromeDriver();
        
      //Define Category - New/Used
        String category;
        
        // Navigate to homepage
        driver.get("https://www.carpages.ca/");
        Thread.sleep(5000);
        // Find the location input field
        WebElement searchBox = driver.findElement(By.className("tt-input"));
        searchBox.clear();
        Thread.sleep(5000);
        searchBox.sendKeys(userInput);
        Thread.sleep(5000);
        driver.findElement(By.className("suggestions-city")).click();
        Thread.sleep(15000);

    	//Click "search" button
        WebElement currentLoc = driver.findElement(By.xpath("//button[contains(text(),'Find A Car')]"));
        Thread.sleep(6000);
        currentLoc.click();

        Thread.sleep(10000);
        
      //logic to click NEW, crawl and create files for new
        category = "New";
        driver.findElement(By.xpath("/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/form[1]/div[1]/fieldset[1]/div[2]/label[2]")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//button[@id='srp-filter-button']")).click();
        Thread.sleep(8000);
        //Click next button and start iterations
        boolean isNextAvailable = true;
        WebElement nextButton = null;
        try {
            // Attempt to find the element
        	 nextButton = driver.findElement(By.className("nextprev"));
        	iteratePages(nextButton, driver, userInput, category);

        } catch (Exception e) {
            // Handle the exception (e.g., print a message or take alternative actions)\
        	isNextAvailable = false;
            System.out.println("Element not found: " + e.getMessage());
        }
        
        
        //logic to click USED, crawl and create files for old
        category = "Used";
        driver.findElement(By.xpath("/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/form[1]/div[1]/fieldset[1]/div[2]/label[1]")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/form[1]/div[1]/fieldset[1]/div[2]/label[2]")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//button[@id='srp-filter-button']")).click();
        Thread.sleep(8000);
        if(isNextAvailable) {
        nextButton = driver.findElement(By.className("nextprev"));
        iteratePages(nextButton, driver, userInput, category);
        }
     // Close the browser when done
        driver.quit();
	}
	
	private static void iteratePages(WebElement nextButton,WebDriver driver, String userInput, String category ) {
		int pageCount = 2;
		
        // Iterate the first 1 pages available
        if(nextButton != null) {
        	for (int i = 1; i <= pageCount; i++) {
        		try {
        		Thread.sleep(8000);
        		createScrapedFile(driver, i, userInput, category);
        		Thread.sleep(8000);
				nextButton.click();
				Thread.sleep(8000);
				nextButton = driver.findElement(By.className("nextprev"));
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }
        else {
        	System.out.println("Encountered an error while finding more pages");
        }
	}
	
    public static void createScrapedFile(WebDriver driver, int fileCount , String userInput, String category) {

		WebElement result = driver.findElement(By.xpath("//body/div[2]/div[1]/div[2]/div[1]"));
		String fileName = "CarPages - " + userInput + " " + fileCount + " - " + category;
		//String elementHTML = result.getAttribute("outerHTML");
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(result.getAttribute("outerHTML"));
		//System.out.println("The result is: \n"+elementHTML);
		
	
		try {
		    // Specify the file path
		String fileSeperator = FileSystems.getDefault().getSeparator();
		String filePath = ConfigUtil.getpropertyValue(Constants.DIR_CARPAGES_HTML_PATH)+fileSeperator+ userInput + fileSeperator + fileName + ".txt";
		
		// Create a FileWriter object with the specified file path
		FileWriter fileWriter = new FileWriter(IOUtils.getFile(filePath));
		
		// Wrap the FileWriter in a BufferedWriter for efficient writing
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

