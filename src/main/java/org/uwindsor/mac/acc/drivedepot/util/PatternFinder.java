package org.uwindsor.mac.acc.drivedepot.util;
 
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
*
* @author Nimisha (110131861)
* Methods Included:
* 1. main(String[] args)
*    - Driver method for user input and car information extraction.
*
* 2. getCarCompany(String input_String)
*    - Extracts the car company name from the input string.
*
* 3. getCarName(String input_String)
*    - Gets the car name from the input string using both patterns.
*
* 4. getCarNameWithFirstPattern(String input_String)
*    - Gets the car name using the first pattern from the input string.
*
* 5. getCarNameWithSecondPattern(String input_String)
*    - Gets the car name using the second pattern from the input string.
*
* 6. extractYearFromInputString(String input_String)
*    - Extracts the year from the input string.
**/
public class PatternFinder {
 
		public static void main(String[] args) {
	        Scanner scanner_r = new Scanner(System.in);
 
	        System.out.print("Enter the car information string: ");
	        String input_String = scanner_r.nextLine();
 
	        // Get the car company from the input string
	        String car_Company = getCarCompany(input_String);
 
	        // Extract the year from the input string
	        String year_num = extractYearFromInputString(input_String);
 
	        // Get the car name using the second pattern
	        String car_Name = getCarNameWithSecondPattern(input_String);
 
	        // If the second pattern didn't match, try the first pattern
	        if (car_Name.isEmpty()) {
	            car_Name = getCarNameWithFirstPattern(input_String);
	        }
 
	        // Store the extracted information in an array
	        String[] carInfoArray = {car_Company, car_Name, String.valueOf(year_num)};
 
	        // Display the extracted information in array format
	        System.out.println("Car Information Array: ");
	        for (String info : carInfoArray) {
	            System.out.println(info);
	        }
 
	        scanner_r.close();
	    }
	    public static String getCarCompany(String input_String) {
	        Pattern pattern_n = Pattern.compile("~\\d{4}\\s(\\w+)");
	        Matcher matcher_r = pattern_n.matcher(input_String);
	        if (matcher_r.find()) {
	            return matcher_r.group(1);
	        } else {
	            System.out.println("Car company name not found.");
	            return "";
	        }
	    }
	    public static String getCarName(String input_String) {
	        String car_Name = getCarNameWithSecondPattern(input_String);
	        // If the second pattern didn't match, try the first pattern
	        if (car_Name.isEmpty()) {
	            car_Name = getCarNameWithFirstPattern(input_String);
	        }
	        return car_Name;
	    }
	    private static String getCarNameWithFirstPattern(String input_String) {
	        Pattern pattern_n = Pattern.compile("\\d{4}\\s[A-Za-z0-9]+\\s(.+?)~");
	        Matcher matcher_r = pattern_n.matcher(input_String);
	        if (matcher_r.find()) {
	            return matcher_r.group(1).trim();
	        }
	        return "";
	    }
	    private static String getCarNameWithSecondPattern(String input_String) {
	        Pattern pattern_n = Pattern.compile("~\\d{4}\\s(\\w+)(?:\\s(\\w+\\s\\w+)|-(\\w+\\s\\w+))?");
	        Matcher matcher_r = pattern_n.matcher(input_String);
	        if (matcher_r.find()) {
	            // Check if the first alternative (\\w+\\s\\w+) is present
	            if (matcher_r.group(2) != null) {
	                return matcher_r.group(2).trim();
	            }
	            // Check if the second alternative (-(\\w+\\s\\w+)) is present
	            else if (matcher_r.group(3) != null) {
	                return matcher_r.group(3).trim();
	            }
	        }
	        return "";
	    }
	    public static String extractYearFromInputString(String input_String) {
	        // Define a pattern to match a four-digit number representing the year
	        Pattern pattern_n = Pattern.compile("\\b\\d{4}\\b");
 
	        // Create a matcher with the input string
	        Matcher matcher_r = pattern_n.matcher(input_String);
 
	        // Check if a match is found
	        if (matcher_r.find()) {
	            // Extract and parse the matched year
	            String matched_Year = matcher_r.group();
	            try {
	                return StringUtils.stringValueOf(Integer.parseInt(matched_Year));
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	                // Handle the exception, for example, by returning a default value or throwing an exception
	                return StringUtils.EMPTY_BLANK_STRING; // or throw new RuntimeException("Unable to parse year", e);
	            }
	        } else {
	            System.out.println("Year not found in the input string.");
	            return StringUtils.EMPTY_BLANK_STRING; // or throw new RuntimeException("Year not found in the input string.");
	        }
	    }
	}
 