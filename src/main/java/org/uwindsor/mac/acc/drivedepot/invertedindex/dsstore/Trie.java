package org.uwindsor.mac.acc.drivedepot.invertedindex.dsstore;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.uwindsor.mac.acc.drivedepot.exception.NonAlphabetCharException;
import org.uwindsor.mac.acc.drivedepot.model.Page;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
* Trie class represents a trie data structure for efficient string storage and retrieval.
* The trie organizes keys as a tree structure with each node representing a character.
* This implementation supports adding, searching, and removing keys, associating them with web pages.
*
* Method Names and Descriptions:
*
* <br> Trie(): Constructs a Trie with an initial empty root node.
* <br> add(String key): Adds the given key to the Trie without associated webpage information.
* <br> add(String key, Page webPage): Adds the given key to the Trie with associated webpage information.
* <br> search(String key): Searches for the given key in the Trie.
* <br> search(char[] key): Searches for the given char array key in the Trie.
* <br> get(String key): Returns the associated end TrieNode containing data at the leaf level for the given key.
* <br> getPageInfo(String key): Gets the list of URL pages associated with the given key.
* <br> getPages(String key): Gets the set of web pages associated with the given key.
* <br> remove(String key): Removes the given key from the Trie.
* <br> remove(String key, TrieNode node, int level): Recursively removes characters related to the key from the Trie.
* <br> size(): Gets the current size of the Trie, indicating the number of unique keys.
*/
public class Trie {

	TrieNode rootNode = null;
	private int size = 0;

	/**
     * Constructs a Trie with an initial empty root node.
     */
	public Trie() {
		this.rootNode = new TrieNode();
	}

	/**
     * Adds the given key to the Trie without associated webpage information.
     * @param key The string key to be added to the Trie.
     */
	public void add(String key)  {
		this.add(key, null);
	}
	
	/**
	 * Add the given key to the Trie data
	 * @param key
	 * @throws NonAlphabetCharException 
	 */
	public void add(String key, Page webPage) {

		if(StringUtils.isNullOrEmpty(key)) {
			return;
		}
		key = StringUtils.toLowercase(key);
		boolean addedNewNode = false;
		//start with the root node
		TrieNode currentNode = this.rootNode;
		char[] charactersInKey =  key.toCharArray(); 

		for(int index = 0; index < charactersInKey.length; index++) {
			TrieNode node = currentNode.getChildNode(charactersInKey[index]);
			if(node == null) {
				addedNewNode = true;
				node = currentNode.add(charactersInKey[index]);
			}			
			currentNode = node;
		}		

		currentNode.addPageInfo(key, webPage);
		currentNode.setLastNode(true);
		this.size = addedNewNode ? this.size+=1 : this.size;
	}

	/**
	 * Search the given key in the Trie.
	 * @param key
	 * @return returns true if found, false otherwise.
	 */
	public boolean search(String key) {
		return (get(key) != null);
	}
	
	/**
	 * Search the given key in the Trie.
	 * @param key the char array representaion of key.
	 * @return returns true if found, false otherwise.
	 */
	public boolean search(char[] key) {
		return (get(new String(key)) != null);
	}
	
	/**
	 * Returns the associated end {@link TrieNode} which contains the data 
	 * at the leaf level.
	 * @param key
	 * @return
	 */
	public TrieNode get(String key) {

		if(StringUtils.isNullOrEmpty(key)) {
			return null;
		}
		key = StringUtils.toLowercase(key);
		boolean exists = false;

		TrieNode currentNode = this.rootNode;
		char[] charsInKey = key.toCharArray();
		int idx = 0;
		for(; idx < key.length(); idx++) {
			TrieNode node  = currentNode.getChildNode(charsInKey[idx]);
			if(node == null ) {
				break;
			}
			currentNode = node;
		}		
		exists = (currentNode != null && currentNode.isLastNode() && idx == key.length());

		return exists ? currentNode : null;
	}
	
	
	/**
	 * Get the list of URL page with which the key is associated. 
	 * @param key
	 * @return
	 */
	public List<String> getPageInfo(String key) {
		TrieNode node = get(key);
		if(node != null) {
			return node.getLinkedPage(key);
		}
		
		//return empty list to avoid null handling
		return new LinkedList<>();
	}
	
	/**
     * Gets the set of web pages associated with the given key.
     * @param key The string key.
     * @return A set of web pages associated with the key.
     */
	public Set<Page> getPages(String key) {
		TrieNode node = get(key);
		if(node != null) {
			return node.getLinkedPages(key);
		}
		
		//return empty list to avoid null handling
		return new HashSet<>();
	}
	
	/**
     * Removes the given key from the Trie.
     * @param key The string key to be removed.
     * @return Returns true if the key is successfully removed, false otherwise.
     */
	public boolean remove(String key) {
		return remove(key, this.rootNode, 0);
	}
	
	/**
	 * To remove the chars related to key from the Trie.
	 * It is a recursive function which takes care of three scenarios
	 * <br>1. If the key exists and it is unique, remove the complete key
	 * <br>2. if the key exists as prefix to another key, just make the last node as false and remove it.
	 * <br>3. If the key contains part of other keys, then just backtrack and delete the chars which only belong to this key.
	 * @param key - to be deleted
	 * @param node - {@link TrieNode} for tracking the chars in Trie
	 * @param level - used for backtracking and traversing the key inside the Trie.
	 * @return
	 */
	private boolean remove(String key, TrieNode node, int level) {
		
		if(StringUtils.isNullOrEmpty(key)) {
			return false;
		}
		key = StringUtils.toLowercase(key);
		//case: if key found and traversed, return true, otherwise false
		if(level == key.length()) {
			return node == null ? false : node.isLastNode();
		}
		
		//Case : key does not exist
		if(node == null && level < key.length()) {
			return false;
		}
		
		//case: if contains a prefix to some other key
		boolean exists = remove(key, node.getChildNode(key.charAt(level)), level+1);
		if(exists) {
			
			char k = key.charAt(level);
			TrieNode currentNode = node.getChildNode(k);
			if(currentNode != null && currentNode.size() == 0) {
				node.deleteElement(k);
			}
			
			if (currentNode.isLastNode()) {
				currentNode.setLastNode(false);
			}
		}
		this.size = exists ? this.size-=1: this.size;
		return exists;
	}
	
	/**
     * Gets the current size of the Trie, indicating the number of unique keys.
     * @return The size of the Trie.
     */
	public int size() {
		return this.size;
	}
}
