/*
 * Developed by: Abdul Rahman Mohammed
 * Student ID: 110128321
 * 
 * Methods Included:
 * 1. searchFrequencyMap(String word)
 * 2. updateWordFrequency(String word)
 * 3. updateMaxHeap()
 * 4. displayWordFrequencies()
 *
*/

package org.uwindsor.mac.acc.drivedepot.webcrawler;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
 
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

public class SearchFrequencyHeap {
    private static Queue<WordFrequency> maxHeap = new PriorityQueue<>((a, b) -> b.frequency - a.frequency);
    private static Map<String, Integer> wordFrequencyMap = new HashMap<>();
    public static void searchFrequencyMap(String userInput) {
        // Update word frequency in the map
        updateWordFrequency(userInput);
        // Update the max heap
        updateMaxHeap(); 
    }
    private static void updateWordFrequency(String word) {
        // Update word frequency within the HashMap
    	word = StringUtils.trimIt(word);
        wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
    }
    private static void updateMaxHeap() {
        // Clear the max heap at every iteration
        maxHeap.clear();
        // Populate the max heap with WordFrequency objects
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            maxHeap.add(new WordFrequency(StringUtils.trimIt(entry.getKey()), entry.getValue()));
        }
    }
    public static void displayWordFrequencies() {
        // Displaying the search history ranked by word frequencies
        System.out.println("Top Searches Frequencies (Top 5):");
        int count = 0;
        while (!maxHeap.isEmpty() && count < 5) {
            WordFrequency wordFrequency = maxHeap.poll();
            System.out.println(StringUtils.trimIt(wordFrequency.word) + ": " + wordFrequency.frequency + " times");
            count++;
        }
        System.out.println();
    }
    // Helper class to represent word frequencies
    private static class WordFrequency {
        String word;
        int frequency;
        WordFrequency(String word, int frequency) {
            this.word = StringUtils.trimIt(word);
            this.frequency = frequency;
        }
    }
}