package org.uwindsor.mac.acc.drivedepot.webcrawler;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
/**
* @author Nimisha (110131861)
* Methods Included:
* 1. TrieNode()
*    - Constructor for TrieNode, initializing child nodes map and end of word flag.
*
* 2. Trie()
*    - Constructor for Trie, initializing the root node.
*
* 3. insert(String word)
*    - Inserts a word into the Trie data structure.
*
* 4. search(String word_i)
*    - Searches for a word in the Trie.
*
* 5. suggestWords(String prefix_x)
*    - Gets suggestions for word completion based on a given prefix.
*
* 6. suggestWordsUtil(TrieNode node_e, String current_Prefix, List<String> suggestion_s)
*    - Recursive utility function for gathering word suggestions.
*
* 7. WordCompletion()
*    - Private constructor for the WordCompletion class.
*
* 8. WordCompletion(String file_Path)
*    - Constructor for WordCompletion with a file path parameter.
*
* 9. WordCompletor(String User_Input)
*    - Performs word completion based on user input.
*
* 10. readFromFile(String file_Path)
*     - Reads content from a file and returns it as a string.
*
* 11. populateTrie(Trie trie_e, String text_r)
*     - Populates the Trie with words from the cleaned text.
*/
 
//TrieNode represents a node in the Trie data structure
class TrieNode {
	// Map to store child nodes
    Map<Character, TrieNode> child_node;
// Flag to mark the end of a word
    boolean isEnd_Of_Word;
 
    public TrieNode() {
 
        child_node = new HashMap<>();
 
        isEnd_Of_Word = false;
 
    }
 
}
//Trie class represents a Trie data structure
class Trie {
	// The root node of the Trie
    private TrieNode root_node_e;
 
    public Trie() {
 
        root_node_e = new TrieNode();
 
    }
    // Inserting a word into the Trie
    public void insert(String word) {
 
        TrieNode node_e = root_node_e;
 
        for (char c : word.toCharArray()) {
 
            node_e.child_node.putIfAbsent(c, new TrieNode());
 
            node_e = node_e.child_node.get(c);
 
        }
 
        node_e.isEnd_Of_Word = true;
 
    }
    // Searching for a word in the Trie
    public boolean search(String word_i) {
 
        TrieNode node_e = root_node_e;
 
        for (char c : word_i.toCharArray()) {
 
            if (!node_e.child_node.containsKey(c)) {
 
                return false;
 
            }
 
            node_e = node_e.child_node.get(c);
 
        }
 
        return node_e.isEnd_Of_Word;
 
    }
    // Getting the suggestions for word completion based on a prefix
    public List<String> suggestWords(String prefix_x) {
 
        List<String> suggestion_s = new ArrayList<>();
 
        TrieNode node_e = root_node_e;
        // Traversing the Trie to the node representing the last character of the prefix
        for (char c_c : prefix_x.toCharArray()) {
 
        	if(node_e != null)
 
        		node_e = node_e.child_node.get(c_c);
 
        	else
 
        		break;
 
        }
        // Utilizing  a recursive function to gather word suggestions
        suggestWordsUtil(node_e, prefix_x, suggestion_s);
 
        return suggestion_s;
 
    }
    // Recursively calling the function in order to gather word suggestions
 
    private void suggestWordsUtil(TrieNode node_e, String current_Prefix, List<String> suggestion_s) {
 
    	if(node_e == null) {
 
    		return;
 
    	}
        // If the current node represents the end of a word then adding  it to suggestions
        if (node_e.isEnd_Of_Word) {
 
            suggestion_s.add(current_Prefix);
 
        }
        // Recursively calling the function for each child node
        for (char c : node_e.child_node.keySet()) {
 
            suggestWordsUtil(node_e.child_node.get(c), current_Prefix + c, suggestion_s);
 
        }
 
    }
 
}
//WordCompletion class for word completion functionality
public class WordCompletion {
	// File path for the dictionary
	public String file_Path  = "";
	private WordCompletion() {
	}
    // Constructor with file path as the parameter
	public WordCompletion(String file_Path) {
		this();
		this.file_Path = file_Path;
	}
    // Method to perform word completion
    public List<String> WordCompletor(String User_Input) {
 
 
        String file_Text = readFromFile(file_Path);
 
        // Creating  a trie
        //inserting the words from the text file
 
        Trie trie_word = new Trie();
 
        populateTrie(trie_word, file_Text);
 
        // Taking the  manual input for word completion
 
            String prefix_x = User_Input.toLowerCase();
 
            // Performing word completion
 
            List<String> suggestion_s = trie_word.suggestWords(prefix_x);
 
            //System.out.println(suggestions);
 
            return suggestion_s;
 
    }
//method to read from the file
    private static String readFromFile(String file_Path) {
 
        StringBuilder content_t = new StringBuilder();
 
        try (BufferedReader reader_r = new BufferedReader(new FileReader(IOUtils.getFile(file_Path)))) {
 
            String line_e;
            while ((line_e = reader_r.readLine()) != null) {
 
                content_t.append(line_e).append("\n");
 
            }
 
        } catch (IOException exc) {
            System.err.printf("Failed in reading the dictionary" , exc.getMessage());
        }
        return content_t.toString();
 
    }
    // Method to populate the Trie with words
 
    private static void populateTrie(Trie trie_e, String text_r) {
 
        // Removing the  HTML tags
    	//inserting the words into the trie
 
        String cleaned_Text = text_r.replaceAll("<[^>]*>", "").toLowerCase();
 
        String[] words_s = cleaned_Text.split("\\r?\\n");
 
        for (String word_i : words_s) {
            trie_e.insert(word_i);
 
        }
 
    }
 
}