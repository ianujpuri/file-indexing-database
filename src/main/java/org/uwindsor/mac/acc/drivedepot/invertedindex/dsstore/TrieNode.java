package org.uwindsor.mac.acc.drivedepot.invertedindex.dsstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uwindsor.mac.acc.drivedepot.exception.NonAlphabetCharException;
import org.uwindsor.mac.acc.drivedepot.model.Page;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
 * Node implementation for {@link Trie} data structure.
 * @author Anuj Puri (110120950)
 *
 * Method Names and Descriptions:
 *
 * <br> TrieNode(): Default constructor to initialize a TrieNode.
 * <br> TrieNode(Character key): Constructor with a character key.
 * <br> TrieNode(char key, boolean isLastNode): Constructor with a character key and lastNode flag.
 * <br> add(Character key): Adds a child TrieNode with the given character key.
 * <br> addA(char key): Adds a child TrieNode using character ASCII value (for lowercase alphabets).
 * <br> getChildNode(Character key): Retrieves the child TrieNode with the given character key.
 * <br> getChildNodeA(char key): Retrieves the child TrieNode using character ASCII value.
 * <br> isLastNode(): Checks if the current node is the last node in the Trie.
 * <br> setLastNode(boolean isLastNode): Sets the lastNode flag for the current TrieNode.
 * <br> addPageInfo(String word, Page page): Adds page information associated with a word to the TrieNode.
 * <br> getLinkedPage(String word): Retrieves linked page URLs for a given word.
 * <br> getLinkedPages(String word): Retrieves linked pages for a given word.
 * <br> getFrequency(String word): Retrieves the total frequency of a word across linked pages.
 * <br> deleteElement(char element): Deletes a child TrieNode with the specified character element.
 * <br> deleteElementA(char element): Deletes a child TrieNode using character ASCII value.
 * <br> deleteChildLink(char element): Deletes the link to a child TrieNode with the specified character element.
 * <br> size(): Gets the number of child TrieNodes.
 * <br> sizeA(): Gets the number of child TrieNodes using character ASCII values.
 * <br> iterator(): Gets an iterator for the character keys of child TrieNodes.
 * <br> iteratorA(): Gets an iterator for child TrieNodes using character ASCII values.
 * <br> calculateHash(char c): Calculates the hash based on the ASCII value of the lower or upper case alphabet.
 * <br> getChar(int c): Converts an ASCII value to a corresponding character.
 * <br> toString(): Returns a string representation of the TrieNode with character keys and lastNode flag.
 * <br> toStringA(): Returns a string representation of the TrieNode using character ASCII values.
 */
public class TrieNode {

	private Map<Character, TrieNode> mapOfCharacters = null;

	/*
	 * Store the value of character values from
	 * [a-z] and [A-Z]
	 */
	private TrieNode[] characterASCIIStore = null;
	private static final int JUNK_CHAR_INDEX = 52;
	private static final int MAX_CAPACITY = 53;
	private int size = 0;


	private Map<String, Set<Page>> mapWordPages = null;	

	/**
	 * Track if this is the last node or not in the TRIE.
	 * Mark true if lastNode, false otherwise.
	 */
	private boolean isLastNode = false;

	/**
	 * Default constructor to initialize a TrieNode.
	 */
	public TrieNode() {
		this.characterASCIIStore = new TrieNode[MAX_CAPACITY];
		this.mapOfCharacters = new HashMap<>(52);
		this.mapWordPages = new Hashtable<>();
	}

	/**
	 * Constructor with a character key.
	 * @param key The character key for the TrieNode.
	 */
	public TrieNode(Character key) {
		this.characterASCIIStore = new TrieNode[MAX_CAPACITY];
	}

	/**
	 * Constructor with a character key and lastNode flag.
	 * @param key The character key for the TrieNode.
	 * @param isLastNode Flag indicating if this is the last node in the Trie.
	 */
	public TrieNode(char key, boolean isLastNode) {

	}

	/**
     * Adds a child TrieNode with the given character key.
     * @param key The character key for the new child TrieNode.
     * @return The newly added TrieNode.
     */
	public TrieNode add(Character key) {
		this.mapOfCharacters.put(key, new TrieNode());
		return this.mapOfCharacters.get(key);
	}	

	/**
     * Adds a child TrieNode using character ASCII value (for lowercase alphabets).
     * @param key The character key for the new child TrieNode.
     * @return The newly added TrieNode.
     * @throws NonAlphabetCharException If the character is not an alphabet.
     */
	public TrieNode addA(char key) throws NonAlphabetCharException {

		if(!StringUtils.isAlphabet(key)) {
			throw new NonAlphabetCharException();
		}

		int hashIndex = calculateHash(key);
		this.characterASCIIStore[hashIndex] = new TrieNode();
		this.size+=1;
		return this.characterASCIIStore[hashIndex];
	}

	/**
     * Retrieves the child TrieNode with the given character key.
     * @param key The character key to search for.
     * @return The child TrieNode or null if not found.
     */
	public TrieNode getChildNode(Character key) {
		return this.mapOfCharacters.get(key);
	}

	/**
     * Retrieves the child TrieNode using character ASCII value.
     * @param key The character key to search for.
     * @return The child TrieNode or null if not found.
     */
	public TrieNode getChildNodeA(char key) {

		int hash = calculateHash(key);
		return this.characterASCIIStore[hash];
	}

	/**
     * Checks if the current node is the last node in the Trie.
     * @return True if the current node is the last node, false otherwise.
     */
	public boolean isLastNode() {
		return isLastNode;
	}

	/**
     * Sets the lastNode flag for the current TrieNode.
     * @param isLastNode Flag indicating if this is the last node in the Trie.
     */
	public void setLastNode(boolean isLastNode) {
		this.isLastNode = isLastNode;
	}

	/**
     * Adds page information associated with a word to the TrieNode.
     * @param word The word to associate with the page.
     * @param page The page to be associated with the word.
     */
	public void addPageInfo(String word, Page page) {
		word = StringUtils.toLowercase(word);
		Set<Page> pages = this.mapWordPages.get(word);
		if(pages != null) {
			pages.add(page);
			for(Page pg : pages) {
				if(pg.equals(page)) {
					pg.incrementWordFrequency();
				}
			}
		} else {
			pages = new HashSet<>();
			pages.add(page);			
		}
			
		this.mapWordPages.put(word, pages);
	}

	/**
     * Retrieves linked page URLs for a given word.
     * @param word The word to retrieve linked page URLs for.
     * @return A list of linked page URLs.
     */
	public List<String> getLinkedPage(String word) {
		word = StringUtils.toLowercase(word);
		List<String> urls = new ArrayList<>();
		Iterator<Page> pages =  this.mapWordPages.get(word).iterator();
		while(pages.hasNext()) {
			urls.add(pages.next().getURL());
		}
		
		return urls;
	}
	
	/**
     * Retrieves linked pages for a given word.
     * @param word The word to retrieve linked pages for.
     * @return A set of linked pages.
     */
	public Set<Page> getLinkedPages(String word) {
		word = StringUtils.toLowercase(word);
		return this.mapWordPages.get(word);
	}
	
	/**
     * Retrieves the total frequency of a word across linked pages.
     * @param word The word to calculate frequency for.
     * @return The total frequency of the word.
     */
	public int getFrequency(String word) {
		int total = 0;
		word = StringUtils.toLowercase(word);
		Iterator<Page> pages =  this.mapWordPages.get(word).iterator();
		while(pages.hasNext()) {
			total += pages.next().getWordFreuency();
		}
		return total;
	}

	/**
     * Deletes a child TrieNode with the specified character element.
     * @param element The character element to be deleted.
     */
	public void deleteElement(char  element) {
		this.mapOfCharacters.remove(element);
	}

	/**
     * Deletes a child TrieNode using character ASCII value.
     * @param element The character element to be deleted.
     */
	public void deleteElementA(char element) {
		int hash = calculateHash(element);
		TrieNode node = this.characterASCIIStore[hash];

		if(node != null) { 
			this.characterASCIIStore[hash] = null;
			this.size -= 1;
		}
	}

	 /**
     * Deletes the link to a child TrieNode with the specified character element.
     * @param element The character element to unlink.
     */
	public void deleteChildLink(char element) {
		this.mapOfCharacters.put(element, null);
	}


	/**
     * Gets the number of child TrieNodes.
     * @return The number of child TrieNodes.
     */
	public int size() {
		return this.mapOfCharacters.size();
	}
	
	/**
     * Gets the number of child TrieNodes using character ASCII values.
     * @return The number of child TrieNodes.
     */
	public int sizeA() {
		return this.size;
	}

	public Iterator<Character> iterator() {
		return this.mapOfCharacters.keySet().iterator();
	}
	
	public Iterator<TrieNode> iteratorA() {
		return Arrays.asList(characterASCIIStore).iterator();
	}

	/**
	 * Calculate Hash based on the ASCII value of the 
	 * lower or upper case alphabet.
	 * 
	 * For lower case, assigning the second half of the array.
	 * For upper case, assigning the first half of the array.
	 * @param c
	 * @return
	 */
	private int calculateHash(char c) {
		if(StringUtils.isLowerCase(c)) {
			return ((c - 97) + 26);
		}

		return c - 65;
	}
	
	/**
     * Converts an ASCII value to a corresponding character.
     * @param c The ASCII value to convert.
     * @return The corresponding character.
     */
	public char getChar(int c) {
		if(c<=25) {
			return (char)(c+65);
		}
		return (char)(c-26+97);
	}

	/**
     * Returns a string representation of the TrieNode with character keys and lastNode flag.
     * @return The string representation of the TrieNode.
     */
	@Override
	public String toString() {
		return this.mapOfCharacters.keySet().toString() + ", last ? " + this.isLastNode;
	}
	
	/**
     * Returns a string representation of the TrieNode using character ASCII values.
     * @return The string representation of the TrieNode.
     */
	public String toStringA() {
		StringBuilder elements = new StringBuilder();
		elements.append('[');
		
		for(int i = 0; i < this.characterASCIIStore.length; i++) {
			if(this.characterASCIIStore[i] != null) {
				elements.append(getChar(i));
				if(i < this.characterASCIIStore.length-1 && size>1) {
					elements.append(',');
				}
			}
		}
		
		elements.append(']');
		return elements.toString();
	}

}
