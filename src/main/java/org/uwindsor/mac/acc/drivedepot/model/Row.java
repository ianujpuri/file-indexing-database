package org.uwindsor.mac.acc.drivedepot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.util.DateUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;



/**
 * Row object to hold all the properties' values of a CAR.
 * It acts as a generic data structure such that can store any number of columns dynamically 
 * using a header string for a file.
 * @author Anuj Puri (110120950)
 */
/*
 * This object is capable of supporting threaded tree structure but not being used as such currently
 * as the serialization cost would be really high as compared to serializing the String
 * as part of IndexTable serialization for all the parsing processing flows supporting 
 * Delta creation. (future scope)
 */
public final class Row implements Serializable {

	/**
	 * Serial version UID for {@link Row}
	 */
	private static final long serialVersionUID = 7744726479241407365L;
	private String[] columns;
	private String rowUniqueKey = "";

	/**
	 * List of all the children connected to this {@link Row} object
	 */
	private transient ArrayList<Row> listChildren;

	/*
	 *  
	 */
	private transient Row parentKey;

	private int rowID = -1;

	private Row dummyRowObjectUsedAsKey = null;
	private RowConfig rowConfig;


	/**
	 * Constructor to initialize this object's properties/attributes
	 * @param rConfig the {@link RowConfig} object to define the configuration of this {@link Row} object instance
	 * @param setDefaultHeader to set the TIMESTAMP and TRANSTYPE column value by default, if ture.
	 */
	public Row(RowConfig rConfig, boolean setDefaultHeader) {
		if(rConfig == null) {
			throw new NullPointerException(" row config cannot be null");
		}

		this.rowConfig = rConfig;
		this.parentKey = null;
		this.columns = new String[rowConfig.getColumnLength()];
		this.listChildren = new ArrayList<Row>();	

		if(setDefaultHeader) {
			setColumnValue(Constants.KEY_TIMESTAMP, DateUtils.getCurrentTimestamp());
		}
	}

	/**
	 * Constructor to initialize this object's properties/attributes 
	 * @param rConfig {@link RowConfig} object to define the configuration of this {@link Row} object instance
	 */
	public Row(RowConfig rConfig) {
		this(rConfig, true);
	}

	/**
	 * Copy constructor for {@link Row} instance
	 * @param row
	 */
	public Row(Row row) {
		this(row, true);		
	}

	/**
	 * Copy constructor for {@link Row} instance
	 * @param row
	 */
	public Row(Row row, boolean setDefaultHeader) {
		this(row.rowConfig, setDefaultHeader);
		this.copyColumns(row);
	}

	public void setColumnDefaultValue(int defaultValue) {
		init(StringUtils.stringValueOf(defaultValue));
	}

	public void setColumnDefaultValue(float defaultValue) {
		init(StringUtils.stringValueOf(defaultValue));
	}

	/**
	 * Initialize all the columns in the 
	 * @param defaultValue
	 */
	public void setColumnDefaultValue(String defaultValue) {
		init(defaultValue);
	}

	/**
	 * Setting the default value 
	 * @param defaultValue
	 */
	private void init(String defaultValue) {
		for(int idx = 0; idx < this.columns.length; idx++) {
			this.columns[idx] = defaultValue;
		}
	}

	/**
	 * Copy the values from given {@link Row} object
	 * @param r
	 */
	public void copyColumns(Row r) {
		for(int index=0; index < r.columns.length; index++) {
			setColumnValue(index, r.columns[index]);
		}
	}

	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	/**
	 * Setting the Parent of this row, if any.
	 * This will be particularly used to update the parent if changed.
	 */
	public void setParentKey(Row parent) {
		this.parentKey = parent;
	}

	/**
	 * Returns the parent to this node/object
	 */
	public Row getParentKey() {
		return parentKey;
	}

	/**
	 * Add the {@link Row} as a child object to this current/parent/node row object
	 * @param child
	 */
	public void addChild(Row child) {
		this.listChildren.add(child);
	}

	/**
	 * Remove the child from the list
	 * @param e
	 * @return
	 */
	public boolean removeChild(Row e) {
		return this.listChildren.remove(e);
	}

	/**
	 * Return all the children associated to this Object/Parent
	 * @return
	 */
	public ArrayList<Row> getChildren() {
		return this.listChildren;
	}

	/**
	 * Delete this particular row
	 * @return
	 */
	public ArrayList<Row> deleteRow() {
		ArrayList<Row> deletedNodes = new ArrayList<Row>();

		/*
		 * Marking the root (this) node as a deleted Row.
		 */
		this.setColumnValue(Constants.KEY_TRANS_TYPE, Constants.TRANS_TYPE_DELETE);
		deletedNodes.add(this);

		delete(this, deletedNodes);

		return deletedNodes;
	}

	/**
	 * Delete the provided row along with all the subsequent children
	 * @param node
	 * @param deletedNode
	 */
	private void delete(Row node, List<Row> deletedNode) {
		List<Row> list = node.getChildren();		

		for(Row r : list) {
			r.setColumnValue(Constants.KEY_TRANS_TYPE, Constants.TRANS_TYPE_DELETE);
			deletedNode.add(r);
			delete(r, deletedNode);
		}
	}


	/**
	 * Set the property values for a given column name
	 * @param index
	 */
	public void setColumnValue(String key, String value) {
		if(rowConfig.getIndex(key) != Integer.MIN_VALUE) {
			setColumnValue(rowConfig.getIndex(key), value);
		}
	}

	/**
	 * Set the property values at the specified index
	 * @param index
	 * @param value
	 */
	public void setColumnValue(int index, String value) {
		if(index >= 0 && index < this.columns.length) {
			this.columns[index] = StringUtils.trimIt(value);
			if(rowConfig.isUniqueColumn(index)) {
				this.rowUniqueKey += this.columns[index];
				setValueForRowObjectAsKey(index, this.columns[index]);
			}
		}
	}
	
	/**
	 * Get value of type String for the specified property
	 * @param propertyKey
	 * @return
	 */
	public String getColumnValue(String propertyKey) {
		if(rowConfig.getIndex(propertyKey) != Integer.MIN_VALUE) { 
			return columns[rowConfig.getIndex(propertyKey)];
		}
		
		return null;
	}	

	/**
	 * Get the value of type String for the property place
	 * the specified index
	 * @param index
	 * @return
	 */
	public String getColumnValue(int index) {
		if(index >= 0 && index < columns.length) {
			return columns[index];
		}

		return StringUtils.EMPTY_BLANK_STRING;
	}

	/**
	 * Get value of type  int for the specified property
	 * @param propertyKey
	 * @return
	 */
	public int getValueInt(String propertyKey) {
		return Integer.parseInt(getColumnValue(propertyKey));
	}

	/**
	 * Get a key using which row can be identified uniquely 
	 * @return
	 */
	public String uniqueKey() {
		return rowUniqueKey;
	}

	/**
	 * Returns the row object with values only specified for unique columns.
	 * This is for efficiently using the memory by not saving the complete Row object with all column values.
	 * Mainly used in storing the key at index table.
	 * @param row
	 * @return
	 */
	public Row getRowAsKey() {
		return this.dummyRowObjectUsedAsKey;
	}
	
	/**
	 * This method modifies the existing Config({@link RowConfig}) object 
	 * by adding the new column names to the header.
	 * After adding the new column names to the existing header,
	 * it sets the values too, for the new column names provided
	 * in the map parameter.
	 * 
	 */
	public void addColumnsValue(Map<String, String> mapColumnNameValue) {
		StringBuilder header = new StringBuilder(rowConfig.getHeader());		
		Iterator<String> columns = mapColumnNameValue.keySet().iterator();
		
		while(columns.hasNext()) {
			String col = columns.next();
			if(!StringUtils.isNullOrEmpty(col)) {
				header.append(rowConfig.getDelimiter()).append(col);
			}
		}
		
		rowConfig.setHeader(header.toString());
		
		while(columns.hasNext()) {
			String col = columns.next();
			if(!StringUtils.isNullOrEmpty(col)) {
				setColumnValue(col, mapColumnNameValue.get(col));
			}
		}
	}
	

	/**
	 * Returns the instance of the String array which backs up this
	 * Row instance,
	 * </br> Any changes to the array will directly impact the column values of this Row object 
	 * @return
	 */
	public String[] toArray() {
		return this.columns;
	}

	/**
	 * Setting values for the Row object to be used as key before hand.
	 * @param keyIndex
	 * @param value
	 */
	private synchronized void setValueForRowObjectAsKey(int keyIndex, String value) {
		if(dummyRowObjectUsedAsKey == null) {
			dummyRowObjectUsedAsKey = new Row(rowConfig);
			dummyRowObjectUsedAsKey.setColumnValue(Constants.KEY_TRANS_TYPE, "");
			dummyRowObjectUsedAsKey.setColumnValue(Constants.KEY_TIMESTAMP, "");
		}

		dummyRowObjectUsedAsKey.columns[keyIndex] = value;
		dummyRowObjectUsedAsKey.rowUniqueKey += value;
	}

	/**
	 * Clear the contents of this {@link Row} object
	 */
	public void clear() {
		this.clear(false);
	}

	/**
	 * Clear the contents of this {@link Row} object
	 * @param resetDefaults set the default params (TIMESTAMP and TRANS_TYPE) if true
	 */
	public void clear(boolean resetDefaults) {
		for(int index = 0; index < columns.length; index++) {
			columns[index] = StringUtils.EMPTY_BLANK_STRING;			
		}
		rowUniqueKey = StringUtils.EMPTY_BLANK_STRING;

		if(resetDefaults) {
			setColumnValue(Constants.KEY_TIMESTAMP, DateUtils.getCurrentTimestamp());
			setColumnValue(Constants.KEY_TRANS_TYPE, Constants.TRANS_TYPE_NEW);
		}
	}

	/**
	 * Hashcode generation for a Row
	 * @return
	 */
	@Override
	public int hashCode() {			
		return StringUtils.trimIt(this.rowUniqueKey).hashCode();
	}

	/**
	 * Equals implemetation for hashing support
	 */
	@Override
	public boolean equals(Object obj) {	
		if(obj == null || !(obj instanceof Row)) {
			return false;
		}

		return StringUtils.trimIt(((Row)obj).uniqueKey()).equals(StringUtils.trimIt(this.rowUniqueKey));
	}

	/**
	 * Return the String properties delimited by {@link StringUtils}.DELIMITER_SYMBOL_TILDA character
	 */
	public String toString() {
		return StringUtils.arrayToString(columns, rowConfig.getDelimiter(), true).replaceAll(StringUtils.EMPTY_SPACE_STRING+rowConfig.getDelimiter(), rowConfig.getDelimiter()) + StringUtils.NEW_LINE;
	}

}
