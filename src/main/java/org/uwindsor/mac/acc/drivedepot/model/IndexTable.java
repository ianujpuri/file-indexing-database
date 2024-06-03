package org.uwindsor.mac.acc.drivedepot.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Own {@link HashMap} extension to avoid the null keys and control the duplicates</br>
 * and have control on serial key
 * @author case Anuj Puri (110120950)
 *
 * @param <K>
 * @param <V>
 */
public class IndexTable<K, V> extends HashMap<K, V> {

	private transient Set<Object> processedRecords; 
	
	/**
	 *  serializable code
	 */
	private static final long serialVersionUID = -6719111511314584222L;
	private static final int DEFAULT_SIZE = 32;


	public IndexTable() {
		this(DEFAULT_SIZE);		
	}

	public IndexTable(int defaultSize) {
		super(defaultSize);
		this.processedRecords = new HashSet<Object>(defaultSize);
	}
	
	public void initProcessedRecordsContainer() {
		this.processedRecords = new HashSet<Object>(DEFAULT_SIZE);
	}

	/*
	 * Don't want the over head of null keys 
	 * (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key , V value) {
		if(key == null ) {
			return null;
		}

		return super.put(key, value);		
	}

	/*
	 * Hashmap get method
	 * (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public V get(Object k) {
		return super.get(k);
	}

	/**
	 * Marking the record which has been prcoessed(mostly likely deleted) as part of the processing of it's Parent record
	 * and should not be processed if it appears again in the input list for creating delta.
	 * @param record to be marked as processed
	 */
	public void processedRecord(Object record) {
		processedRecords.add(record);
	}

	/**
	 * It clears the records stored in the IndexTable and 
	 * the bindings to the processed records, if any. 
	 */
	@Override
	public void clear() {
		processedRecords.clear();
		super.clear();
	}

	/**
	 * Removes the mapping for the provided key and the 
	 * bindings to the processed records, if any.
	 */
	@Override
	public V remove(Object key) {
		if(key != null) {
			processedRecords.remove(key);
		}
		
		return super.remove(key);
	}

	/**
	 * <b>true</b> if the specified records has been marked as processed, <b>false</b> otherwise.
	 * @param record to be checked for the marker
	 * @return
	 */
	public boolean isRecordProcesed(Object record) {
		return processedRecords.contains(record);
	}
}
