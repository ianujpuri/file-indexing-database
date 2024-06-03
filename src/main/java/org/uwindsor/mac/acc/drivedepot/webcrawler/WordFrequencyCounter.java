package org.uwindsor.mac.acc.drivedepot.webcrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordFrequencyCounter {
    public static void main(String[] args) {
        // Get URL input from the user
        System.out.print("Enter the URL: ");
        String urlInput = getUserInput();

        try {
            // Read content from the URL
            String content = readContentFromURL(urlInput);

            // Count word frequency
            Map<String, Integer> wordFrequencyMap = countWordFrequency(content);

            // Display word frequency
            displayWordFrequency(wordFrequencyMap);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the content from the URL.");
            e.printStackTrace();
        }
    }

    private static String getUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readContentFromURL(String urlString) throws IOException {
        // Use Jsoup to parse HTML content
        Document document = Jsoup.connect(urlString).get();

        // Get the text content from the parsed HTML
        return document.text();
    }

    private static Map<String, Integer> countWordFrequency(String content) {
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        String[] words = content.split("\\s+");

        for (String word : words) {
            word = word.toLowerCase().replaceAll("[^a-zA-Z]", ""); // Remove non-alphabetic characters
            if (!word.isEmpty()) {
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
            }
        }

        return wordFrequencyMap;
    }

    private static void displayWordFrequency(Map<String, Integer> wordFrequencyMap) {
        System.out.println("Word Frequency:");
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
