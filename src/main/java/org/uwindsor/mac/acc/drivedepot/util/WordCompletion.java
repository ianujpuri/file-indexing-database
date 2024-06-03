package org.uwindsor.mac.acc.drivedepot.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return false;
            }
            node = node.children.get(c);
        }
        return node.isEndOfWord;
    }

    public List<String> suggestWords(String prefix) {
        List<String> suggestions = new ArrayList<>();
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                System.out.println("No suggestions found for " + prefix);
                return suggestions;
            }
            node = node.children.get(c);
        }

        suggestWordsUtil(node, prefix, suggestions);
        return suggestions;
    }

    private void suggestWordsUtil(TrieNode node, String currentPrefix, List<String> suggestions) {
        if (node.isEndOfWord) {
            suggestions.add(currentPrefix);
        }

        for (char c : node.children.keySet()) {
            suggestWordsUtil(node.children.get(c), currentPrefix + c, suggestions);
        }
    }
}

public class WordCompletion {
    public static void main(String[] args) {
        // Specify the file path
        String filePath = "src/main/resources/dictionay.txt";

        // Fetch text from the file
        String fileText = readFromFile(filePath);

        // Create a trie and insert words from the text file
        Trie trie = new Trie();
        populateTrie(trie, fileText);
//
//        // Take manual input for word completion
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter a prefix for word completion: ");
            String prefix = reader.readLine().toLowerCase();

            // Perform word completion
            System.out.println("Suggestions for '" + prefix + "':");
            List<String> suggestions = trie.suggestWords(prefix);
            System.out.println(suggestions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        trie.insert("Anuj 123");
        System.out.println(trie.search("123Anuj"));
    }

    private static String readFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
System.out.println();
        try (BufferedReader reader = new BufferedReader(new FileReader(IOUtils.getFile(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private static void populateTrie(Trie trie, String text) {
        // Remove HTML tags and insert words into the trie
        String cleanedText = text.replaceAll("<[^>]*>", "").toLowerCase();
        String[] words = cleanedText.split("\\s+");

        for (String word : words) {
            trie.insert(word);
        }
    }
 
}
