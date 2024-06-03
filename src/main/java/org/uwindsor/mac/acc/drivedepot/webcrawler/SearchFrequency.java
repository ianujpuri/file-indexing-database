package org.uwindsor.mac.acc.drivedepot.webcrawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SearchFrequency {
    public static void main(String[] args) {
        // Create a HashMap to store word frequencies
        Map<String, Integer> wordFrequencyMap = new HashMap<>();

        // Create a Scanner for user input
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a word (type 'exit' to stop): ");
            String inputWord = scanner.next();

            if (inputWord.equalsIgnoreCase("exit")) {
                break;
            }

            // Update word frequency in the map
            updateWordFrequency(wordFrequencyMap, inputWord);

            // Display word frequencies
            displayWordFrequencies(wordFrequencyMap);
        }

        // Close the scanner
        scanner.close();
    }

    private static void updateWordFrequency(Map<String, Integer> wordFrequencyMap, String word) {
        // Update word frequency in the map
        wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
    }

    private static void displayWordFrequencies(Map<String, Integer> wordFrequencyMap) {
        // Display word frequencies
        System.out.println("Search Frequencies:");
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " times");
        }
        System.out.println();
    }
}
