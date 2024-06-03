package org.uwindsor.mac.acc.drivedepot.model;

import java.io.Serializable;
import java.util.HashMap;

import org.uwindsor.mac.acc.drivedepot.util.StringUtils;


/**
 * This Object contains the configuration of a Record or Row
 * It is primarily created to support different type of 
 * records in different files and provide flexibility in case
 *  of storing different parameters/columns from different webpages.
 * @author 110120950(Anuj Puri)
 *
 */
public final class RowConfig implements Serializable {

	/**
	 * serial version UID 
	 */
	private static final long serialVersionUID = 3447024891568024651L;

	private HashMap<String, Integer> columnIndexMap = new IgnoreCaseMap<String, Integer>();

	private String header = StringUtils.EMPTY_BLANK_STRING;
	private String delimiter;
	private int columnLength;
	private boolean[] uniqueColumnIndex;

	/**
	 * Instatiates {@link RowConfig} with the provided header
	 * to initialize the one time config for creating records with header column length.
	 * This sets the column delimiter to CIDLA by default. 
	 * @param header
	 */
	public RowConfig(String header) {
		this(header, StringUtils.SYMBOL_CIDLA);
	}

	/**
	 * Instatiates {@link RowConfig} with the provided header and delimiter 
	 * to initialize the one time config for creating records with header column length.
	 * @param header
	 * @param delimiter
	 */
	public RowConfig(String header, String delimiter) {		
		this.delimiter = delimiter;
		init(header);
	}

	private void init(String header) {
		if(StringUtils.isNullOrEmpty(header) ||  StringUtils.isNullOrEmptyWithTab(delimiter)) {
			throw new NullPointerException("Header or delimiter cannot be null");
		}
		this.header = header;
		String[] headerCols = this.header.split(this.delimiter);
		generateColumnIndexMap(headerCols);		
		setColumnLength(headerCols.length);
		initUniqueIndexBitMap();
	}

	void setHeader(String header) {
		init(header);
	}
	
	/**
	 * Number of columns in a row
	 * @param colLength
	 */
	private void setColumnLength(int colLength) {
		if(colLength < 0) {
			throw new IllegalArgumentException("Negetive record length");
		}
		columnLength = colLength;
	}

	public int getColumnLength() {
		return this.columnLength;
	}

	/**
	 * Get the index for a property rowUniqueKey
	 * @param rowUniqueKey
	 * @return
	 */
	public int getIndex(String key) {
		Integer index = columnIndexMap.get(key);
		return index != null ? index : Integer.MIN_VALUE;
	}
	

	/**
	 * Properties to be used for identifying the unique records 
	 * @param keys
	 */
	public void setUniqueColumns(String[] keys) {
		if(keys == null || keys.length == 0) {
			throw new IllegalArgumentException("Unique columns cannot be null.");
		}

		for(int index = 0; index < keys.length; index++) {
			uniqueColumnIndex[getIndex(keys[index])] = true;
		}
	}
	
	
	/**
	 * Check if the index column contributes to uniqueness
	 * @param index
	 * @return
	 */
	boolean isUniqueColumn(int index) {
		return uniqueColumnIndex[index];
	}

	/**
	 * Check if the column contributes to uniqueness
	 * @param key
	 * @return
	 */
	boolean isUniqueColumn(String key) {
		return isUniqueColumn(getIndex(key));
	}


	/**
	 * Return the map containing the column header as rowUniqueKey to the column index for the provided output file header
	 * @return
	 */
	private HashMap<String, Integer> generateColumnIndexMap(final String[] headerCols) {		
		if(headerCols == null || headerCols.length == 0) {
			throw new IllegalArgumentException("Incorrect header " + headerCols);
		}
		setColumnLength(headerCols.length);

		for(int index=0; index < headerCols.length; index++) {
			columnIndexMap.put(headerCols[index], index);
		}

		return columnIndexMap;
	}

	/**
	 * Initializing the bitmap with all the values as false
	 */
	private void initUniqueIndexBitMap() {
		uniqueColumnIndex = new boolean[columnLength];
		for(int index = 0; index < columnLength; index++) {
			uniqueColumnIndex[index] = false;
		}
	}

	/**
	 * Get the delimiter associated with this {@link RowConfig}
	 * @return
	 */
	String getDelimiter(){
		return this.delimiter;
	}

	/**
	 * Get the header associated with this {@link RowConfig}
	 * @return
	 */
	String getHeader() {
		return this.header;
	}
}
