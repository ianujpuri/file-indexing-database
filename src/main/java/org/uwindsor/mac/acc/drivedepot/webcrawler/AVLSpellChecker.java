package org.uwindsor.mac.acc.drivedepot.webcrawler;


/**
 * 
 * Developed by: Samatha Guddepogu
 * Student ID: 110126164
 * Feature: Spell Checking
 * 
 * Methods Included
 * 
 * AVLTree Class
 * 
 * height(AVLNode current_Node): Returns the height of the AVL tree rooted at the given node.
 * getBalance(AVLNode current_Node): Calculates the balance factor of the AVL tree at the given node.
 * rightRotate(AVLNode y_Node): Performs a right rotation at the given node in the AVL tree.
 * leftRotate(AVLNode x_Node): Performs a left rotation at the given node in the AVL tree.
 * insert(AVLNode current_Node, String key): Inserts a new node with the given key into the AVL tree rooted at the current node and performs necessary rotations to maintain AVL balance.
 * insert(String key): Inserts a new node with the given key into the AVL tree.
 * search(AVLNode current_Node, String key): Searches for a key in the AVL tree rooted at the given node.
 * search(String key): Searches for a key in the AVL tree.
 * inOrderTraversal(AVLNode current_Node, List<String> wordsp): Performs an in-order traversal of the AVL tree rooted at the given node and adds keys to the provided list.
 * getInOrderTraversal(): Returns a list of keys obtained from an in-order traversal of the AVL tree.
 * 
 * 
 * AVLSpellChecker Class
 * 
 * loadDataFromFile(String file_Name): Loads data from a file and inserts it into the AVL tree.
 * calculateDistance(String a, String b, int max_Dist): Calculates the Levenshtein distance between two strings with a specified maximum distance.
 * findRelatedWords(AVLNode current_Node, String input_word, List<String> related_Words, int max_Dist): Finds words in the AVL tree related to the input word within a given maximum distance.
 * suggestedWords(String input_word, int max_Dist): Returns a list of suggested words related to the input word within a given maximum distance.
 * spellCheckm(String input_Word): Performs spell checking on the input word using a default file path.
 * spellCheckm(String input_Word, String file_Path): Performs spell checking on the input word using a specified file path.
 * 
 * **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.HTMLParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;


//Node class for AVL Tree
class AVLNode {
    String key;
    int height_Of_AVLTree;
    AVLNode left_Node, right_Node;
    
    // Constructor for initializing a node with a key
    public AVLNode(String key) {
        this.key = key;
        this.height_Of_AVLTree = 1;
        this.left_Node = this.right_Node = null;
    }
}

//AVL Tree class
class AVLTree {
    AVLNode root_Node;
    
    //The height of the AVL tree rooted at the given node is returned
    private int height(AVLNode current_Node) {
        if (current_Node == null)
            return 0;
        return current_Node.height_Of_AVLTree;
    }
    
    //The balance factor of the AVL tree at the given node is calculated
    private int getBalance(AVLNode current_Node) {
        if (current_Node == null)
            return 0;
        return height(current_Node.left_Node) - height(current_Node.right_Node);
    }
    
    //A right rotation operation at the given node in the AVL tree is performed
    private AVLNode rightRotate(AVLNode y_Node) {
        AVLNode x_Node = y_Node.left_Node;
        AVLNode T2 = x_Node.right_Node;
        x_Node.right_Node = y_Node;
        y_Node.left_Node = T2;
        y_Node.height_Of_AVLTree = Math.max(height(y_Node.left_Node), height(y_Node.right_Node)) + 1;
        x_Node.height_Of_AVLTree = Math.max(height(x_Node.left_Node), height(x_Node.right_Node)) + 1;
        return x_Node;
    }
    
    //A left rotation operation at the given node in the AVL tree is performed
    private AVLNode leftRotate(AVLNode x_Node) {
        AVLNode y_Node = x_Node.right_Node;
        AVLNode T2 = y_Node.left_Node;
        y_Node.left_Node = x_Node;
        x_Node.right_Node = T2;
        x_Node.height_Of_AVLTree = Math.max(height(x_Node.left_Node), height(x_Node.right_Node)) + 1;
        y_Node.height_Of_AVLTree = Math.max(height(y_Node.left_Node), height(y_Node.right_Node)) + 1;
        return y_Node;
    }
    
    //Recursive function to insert a key into the AVL tree and perform rotations
    private AVLNode insert(AVLNode current_Node, String key) {
        if (current_Node == null)
            return new AVLNode(key);
        if (key.compareTo(current_Node.key) < 0)
            current_Node.left_Node = insert(current_Node.left_Node, key);
        else if (key.compareTo(current_Node.key) > 0)
            current_Node.right_Node = insert(current_Node.right_Node, key);
        else
            return current_Node;
        current_Node.height_Of_AVLTree = 1 + Math.max(height(current_Node.left_Node), height(current_Node.right_Node));
        int balanceVal = getBalance(current_Node);
        // Left Left Case
        if (balanceVal > 1 && key.compareTo(current_Node.left_Node.key) < 0)
            return rightRotate(current_Node);
        // Right Right Case
        if (balanceVal < -1 && key.compareTo(current_Node.right_Node.key) > 0)
            return leftRotate(current_Node);
        // Left Right Case
        if (balanceVal > 1 && key.compareTo(current_Node.left_Node.key) > 0) {
            current_Node.left_Node = leftRotate(current_Node.left_Node);
            return rightRotate(current_Node);
        }
        // Right Left Case
        if (balanceVal < -1 && key.compareTo(current_Node.right_Node.key) < 0) {
            current_Node.right_Node = rightRotate(current_Node.right_Node);
            return leftRotate(current_Node);
        }
        return current_Node;
    }
    
    // Public method to insert a key into the AVL tree
    public void insert(String key) {    	
        root_Node = insert(root_Node, key);
    }
    
    // Recursive function to search for a key in the AVL tree
    private boolean search(AVLNode current_Node, String key) {
        if (current_Node == null)
            return false;
        if (key.equals(current_Node.key))
            return true;
        if (key.compareTo(current_Node.key) < 0)
            return search(current_Node.left_Node, key);
        else
            return search(current_Node.right_Node, key);
    }
    
    // Public method to search for a key in the AVL tree
    public boolean search(String key) {
        return search(root_Node, key);
    }
    
    public void clearTree() {
    	clearTree(root_Node);
    	root_Node = null;
    }
    
    private void clearTree(AVLNode node) {
    	if(node == null) {
    		return;
    	}
    	
    	clearTree(node.left_Node);
    	clearTree(node.right_Node);
    	node.key = null;
    	node.left_Node = null;
    	node.right_Node = null;
    	
    }
    
    // Recursive function for in-order traversal of the AVL tree
    private void inOrderTraversal(AVLNode current_Node, List<String> wordsp) {
        if (current_Node != null) {
            inOrderTraversal(current_Node.left_Node, wordsp);
            wordsp.add(current_Node.key);
            inOrderTraversal(current_Node.right_Node, wordsp);
        }
    }
    
    // Public method to get in-order traversal of the AVL tree
    public List<String> getInOrderTraversal() {
        List<String> wordsp = new ArrayList<>();
        if(root_Node == null) {
        	return wordsp;
        }
        
        inOrderTraversal(root_Node, wordsp);

        return wordsp;
    }
}

//Spell checker class using AVL Tree
public class AVLSpellChecker {
    private static AVLTree avlTree = new AVLTree();
    
    
    static {
		File propertiesFile;
		try {
			propertiesFile = IOUtils.getFile(HTMLParser.PATH_PROPERTIES_FILE);
			ConfigUtil.loadProperties(propertiesFile, new Properties());
	        System.setProperty("webdriver.chrome.driver", ConfigUtil.getpropertyValue(Constants.KEY_PATH_CHROME_WEBDRIVER));
		} catch (IOException e) {
			System.err.println("Error Occurred while loading properties.");
		}
	}
    
    public AVLSpellChecker() {
    	
    }
    
    public AVLSpellChecker(String fileName) {
    	this();
    	loadDataFromFile(fileName);   	
    }
    
    public AVLSpellChecker(String fileName, boolean cleanOldData) {
    	this();
    	loadDataFromFile(fileName, cleanOldData);   	
    }
    
    // Loading data from a file and inserting into AVL tree
    private static void loadDataFromFile(String file_Name) {
       loadDataFromFile(file_Name, false);
    }
    
    private static void loadDataFromFile(String file_Name, boolean cleanOlder) {
    	if(cleanOlder) {
    		avlTree.clearTree();
    	}
        try (BufferedReader buffered_Reader = new BufferedReader(new FileReader(file_Name))) {
            String linep;
            while ((linep = buffered_Reader.readLine()) != null) {
                //String[] wordsp = linep.split("\\s+");
                //for (String word : wordsp) {
                    avlTree.insert(linep.toLowerCase().trim());
                }
            }
    //    }
    catch (IOException ex) {
        }
    }
    
    // Calculating the Levenshtein distance between two strings
    private static int calculateDistance(String a, String b, int max_Dist) {
        int[][] min_Dist = new int[a.length() + 1][b.length() + 1];
        for (int l = 0; l <= a.length(); l++) {
            for (int m = 0; m <= b.length(); m++) {
                if (l == 0) {
                    min_Dist[l][m] = m;
                } else if (m == 0) {
                    min_Dist[l][m] = l;
                } else {
                    int cost = (a.charAt(l - 1) == b.charAt(m - 1)) ? 0 : 1;
                    min_Dist[l][m] = Math.min(Math.min(min_Dist[l - 1][m] + 1, min_Dist[l][m - 1] + 1), min_Dist[l - 1][m - 1] + cost);
                    if (l > 1 && m > 1 && a.charAt(l - 1) == b.charAt(m - 2) && a.charAt(l - 2) == b.charAt(m - 1)) {
                        min_Dist[l][m] = Math.min(min_Dist[l][m], min_Dist[l - 2][m - 2] + cost);
                    }
                }
            }
        }
        if (min_Dist[a.length()][b.length()] > max_Dist) {
            return Integer.MAX_VALUE;
        }
        return min_Dist[a.length()][b.length()];
    }
    
    // Finding the related words in AVL tree based on the Levenshtein distance
    private static void findRelatedWords(AVLNode current_Node, String input_word, List<String> related_Words, int max_Dist) {
        if (current_Node == null) {
            return;
        }
        if (input_word.compareTo(current_Node.key) < 0) {
            findRelatedWords(current_Node.left_Node, input_word, related_Words, max_Dist);
        }
        int dist_calculated = calculateDistance(current_Node.key, input_word, max_Dist);
        if (dist_calculated == 0) {
            related_Words.add(current_Node.key);
        } else if (dist_calculated <= max_Dist) {
            related_Words.add(current_Node.key);
        }
        if (input_word.compareTo(current_Node.key) > 0) {
            findRelatedWords(current_Node.right_Node, input_word, related_Words, max_Dist);
        }
    }
    
    // Get a list of suggested words based on Levenshtein distance
    private static List<String> suggestedWords(String input_word, int max_Dist) {
        List<String> related_Words = new ArrayList<>();
        findRelatedWords(avlTree.root_Node, input_word.toLowerCase(), related_Words, max_Dist);
        return related_Words;
    }
    
    public void printTree() {
    	System.out.println(" data : " + avlTree.getInOrderTraversal());
    }
 
    public void clearTree() {
    	avlTree.clearTree();
    	avlTree.root_Node = null;
    }
    
    //	method for spell checking a word
    public List<String> spellCheckm(String input_Word) {
        String file_Path = "C:\\Users\\mohda\\OneDrive\\Documents\\Masters Documents\\ACC Project\\carApplication\\src\\carApplication\\Files\\locations_dictionary.txt";
        return this.spellCheckm(input_Word, file_Path, false);
    }
    
    public List<String> spellCheckm(String input_Word, String file_Path, boolean cleanOlderData) {
        loadDataFromFile(file_Path, cleanOlderData);
        List<String> result_Words = new ArrayList<>();
            if (!avlTree.search(input_Word)) {
                List<String> suggested_Words = suggestedWords(input_Word, 4);
                for (int iy = 0; iy < suggested_Words.size(); iy++) {
                	result_Words.add(suggested_Words.get(iy));
                }
            }
            return result_Words;
    }
}