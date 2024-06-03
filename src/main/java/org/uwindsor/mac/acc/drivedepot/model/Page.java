package org.uwindsor.mac.acc.drivedepot.model;

import java.util.HashSet;
import java.util.Set;

import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
 * The Model class created to store data related to webpage.
 * It has attributes url, title, wordFrequency, records
 * it is primarily being used as part of the trie-in-memory-indexes
 * @author Anuj Puri(110120950)
 *
 */
public class Page {

	private String url;
	private String title;
	private volatile int wordFrequency = 0;
	private Set<Car> records;
	
	private Page() {
		
	}
	
	public Page(String url) {
		this();
		if(StringUtils.isNullOrEmpty(url)) {
			throw new IllegalArgumentException("Webpage URL cannot be null.");
		}
		this.records = new HashSet<>();
		this.url = url;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public synchronized void incrementWordFrequency() {
		this.wordFrequency += 1;
	}
	
	public synchronized void decrementWordFrequency() {
		this.wordFrequency -= 1;
		
	}
	
	public void add(Car record) {
		if(record != null) {
			this.records.add(record);
		}
	}
	
	public Set<Car> getRecords() {
		return this.records;
	}
	
	public int getWordFreuency() {
		return this.wordFrequency;
	}
	
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof Page)) {
			return false;
		}
		
		Page page = (Page)obj;		
		return page.url.equals(this.url);
	}
}
