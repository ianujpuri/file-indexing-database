package org.uwindsor.mac.acc.drivedepot.pagerank;

/***
 * 
 * Developed by: Samatha Guddepogu
 * Student ID: 110126164
 * Feature: Page Ranking
 * 
 * Methods Included in PageRank Class
 * 
 * PageRankingOfKeyword(String keyword): Performs page ranking of a specified keyword in three specified text files.
 * countKeywordFrequency(Map<String, Integer> word_Frequency_Map, String keyword): Counts the frequency of a keyword in a given word frequency map.
 * 
 * Using the "WordFrequencyCounter" program to count word frequencies
 * 
 * **/
 
import java.util.HashMap;
 
import java.util.Map;
 
import org.uwindsor.mac.acc.drivedepot.wordfrequency.FrequencyCount;
 
 
public class PageRank {
        public static void PageRankingOfKeyword(String keyword) {
 
        // Three text files to perform page ranking.
 
        String[] file_Paths = {
 
                "/Users/anujp/git/repository/car-finder/src/main/resources/testOutput_1_AUTO.txt",
 
                "/Users/anujp/git/repository/car-finder/src/main/resources/testOutput_1_CARPAGE.txt",
 
                "/Users/anujp/git/repository/car-finder/src/main/resources/testOutput_1_MAZDA.txt"
 
        };
        
        // Creating a map for storing the keyword frequencies of each file
 
        Map<String, Integer> file_Keyword_Frequencies = new HashMap<>();
        
        // Reading the content from each file and counting keyword frequencies
        
        for (String file_Path : file_Paths) {
 
            // Using the WordFrequencyCounter program for counting word frequencies
 
            Map<String, Integer> word_Frequency_Map = FrequencyCount.analyzeFile(file_Path);
 
            int keyword_Frequency = countKeywordFrequency(word_Frequency_Map, keyword);
            
            // Storing the keyword frequency for the file
 
            file_Keyword_Frequencies.put(file_Path, keyword_Frequency);
 
        }
        
        // Displaying the files sorted by the keyword frequency in descending order
 
        file_Keyword_Frequencies.entrySet().stream()
 
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
 
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
 
    }
    private static int countKeywordFrequency(Map<String, Integer> word_Frequency_Map, String keyword) {
 
        // Extracting the keyword frequency from the map
 
        return word_Frequency_Map.getOrDefault(keyword.toLowerCase(), 0);
 
    }
 
}