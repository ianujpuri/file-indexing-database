package org.uwindsor.mac.acc.drivedepot.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Developed by: Divyateja Kandlagunta
* Student ID: 110127819
* Feature: Data Validation Using Regular Expressions
*
* Methods Included:
* <br>1. isValidEmail(String email)
* <br>2. isValidText(String text)
* <br>3. isValidCarYear(String year)
* <br>4. isValidPassword(String password)
* <br>5. isValidNumber(String number)
* <br>6. validateAndPrintResult(String input, String regex)
*/
public class InputValidator {
 
    /**
     * Validates an email address.
     *
     * @param email The email address to be validated.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        return validateResult(email,
                "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }
    /**
     * Validates generic text.
     *
     * @param text The text to be validated.
     * @return true if the text is valid, false otherwise.
     */
    public static boolean isValidText(String text) {
        return validateResult(text, "^[a-zA-Z]+[a-zA-Z0-9\\s]*$");
    }
    
     /**
     *Validates a number.          
     * @param number The number to be validated. 
     * @return true if the number is valid, false otherwise.
     */  
    
//    public static boolean isValidNumber(String number) {         
//    	return validateResult(number, "^[0-9]+$");     
//    	}
    
    public static boolean isValidNumber(String number) {     
    	// Case-insensitive check for the word "exit"    
    	if (number.equalsIgnoreCase("exit"))
    	{         
    		return true;     
    	}     
    	// Check if the input is a valid number    
    	return validateResult(number, "^[0-9]+$");
    	}
 
    /**
     * Validates a car year.
     *
     * @param year The car year to be validated.
     * @return true if the car year is valid, false otherwise.
     */
    public static boolean isValidCarYear(String year) {
        return validateResult(year, "^(201[2-9]|202[0-3])$");
    }
 
    /**
     * Validates a password based on certain criteria.
     *
     * @param password The password to be validated.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean isValidPassword(String password) {
        return validateResult(password, "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
 
    /**
     * Validates the input against the provided regular expression.
     *
     * @param input The input to be validated.
     * @param regex The regular expression used for validation.
     * @return true if the input matches the regex, false otherwise.
     */
    private static boolean validateResult(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
 
        return matcher.matches();
    }
}
 