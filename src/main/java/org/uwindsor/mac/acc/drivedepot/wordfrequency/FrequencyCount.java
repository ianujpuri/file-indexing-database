package org.uwindsor.mac.acc.drivedepot.wordfrequency;
 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uwindsor.mac.acc.drivedepot.util.StringUtils;
 
/**
* Developed by: Divyateja Kandlagunta
* Student ID: 110127819
* Feature: Frequency Count
*
* Methods Included:
* 1. analyzeFile(String filePath)
* 2. readContentFromFile(String filePath)
* 3. countWordFrequency(String content)
*/
public class FrequencyCount {
    private static final Logger LOGGER = Logger.getLogger(FrequencyCount.class.getName());
 
    /**
     * Analyzes a file and counts the frequency of each word.
     *
     * @param filePath The path to the file to be analyzed.
     * @return A map containing word frequencies, or null if an error occurs.
     */
    public static Map<String, Integer> analyzeFile(String filePath) {
 
        String content = readContentFromFile(filePath);
 
		if (content != null) {
 
		    return countWordFrequency(content);
 
		} else {
 
		    // Handle the case where content is null
 
		    LOGGER.log(Level.SEVERE, "Content is null.");
 
		    return new HashMap<>(); //return empty hashmap
 
		}
 
    }
 
    /**
     * Reads content from a file.
     *
     * @param filePath The path to the file.
     * @return The content of the file as a string.
     * @throws IOException If an error occurs while reading the file.
     */
    private static String readContentFromFile(String filePath) {
 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
 
            StringBuilder content = new StringBuilder();
 
            String line;
 
            while ((line = br.readLine()) != null) {
 
                content.append(line).append("\n");
 
            }
 
            return content.toString();
 
        } catch(Exception e) {
        	return StringUtils.EMPTY_BLANK_STRING;
        }
 
    }
 
    /**
     * Counts the frequency of each word in the provided content.
     *
     * @param content The content to be analyzed.
     * @return A map containing word frequencies.
     */
    private static Map<String, Integer> countWordFrequency(String content) {
 
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
 
        // Updated regular expression to include additional characters for word splitting
 
        String[] words = content.toLowerCase().split("[\\s\\p{Punct}]+");
        for (String word : words) {
 
            if (!word.isEmpty()) {
 
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
 
            }
 
        }
        return wordFrequencyMap;
 
    }
 
}
 