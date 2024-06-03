package org.uwindsor.mac.acc.drivedepot.parser.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.uwindsor.mac.acc.drivedepot.comparator.Comparator;
import org.uwindsor.mac.acc.drivedepot.model.IndexTable;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;



/**
 * The class is designed to read flat files (text-based) where each line of the file is considered 
 * to be a record.
 * Records are separated by a delimiter to differentiate the columns.</br></br>
 * The rows comprising of the column values can be retrieved as a list of Array.</br>
 * 
 * For example : File with ','(comma) separated records containing information about Network Elements
 * with different properties like ipaddress, elementName etc.</br></br>  
 * 
 * <b>NOTE</b>: This is not a thread-safe implementation
 * @author 110120950(Anuj Puri)
 *
 */
public final class IndexedFileParser implements IParser {

	//public class variables
	public final static String FILE_READ_MODE = "r";

	//private class variables
	private final static short DEFAULT_ROWS_BATCH_SIZE = 3000;
	private final static short DEFAULT_COLUMN_INDEX = -1;
	private final static short BYTE_BUFFER_SIZE = 8192; // An 8KB buffer for reading bytes, file system block size
	private final static short POSITION_START_FILE = 0;

	//private instance variables
	private RandomAccessFile fileRandomAccess;
	private FileChannel channelRandomAccess;
	private ByteBuffer dataBlock = ByteBuffer.allocateDirect(1024);
	private String delimiter;
	private Charset charSet = Charset.forName("UTF-8");
	private HashMap<String, String> mapSortByColumn = new HashMap<String, String>();

	private boolean createIndexs = false;
	private int rowsBatchSize = DEFAULT_ROWS_BATCH_SIZE;
	private int delimiterRegexLimit;
	private int sortByColumnIndex = 0;
	private int[] indexColumns = null;
	private long filePointer;

	private IndexTable<String, Long> indexTable = new IndexTable<String, Long>();

	@SuppressWarnings("rawtypes")
	private Comparator columnComparator = null;

	/**
	 * Constructor for initializing the IndexedFileParser</br>
	 * It will open the file in read-only mode and the default batch size (3000) will be applied
	 * @param file : File to be read to get the records
	 * @throws FileNotFoundException
	 */
	public IndexedFileParser(String filePathName) throws FileNotFoundException, IOException {
		this(filePathName, "");

	}
	
	/**
	 * Constructor for initializing the IndexedFileParser</br>
	 * It will open the file in read-only mode and the default batch size (3000) will be applied
	 * @param file : File to be read to get the records
	 * @param delimiter : The delimiter to parse the column values
	 * @throws FileNotFoundException
	 */
	public IndexedFileParser(String filePathName, String delimiter) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(filePathName), delimiter, DEFAULT_ROWS_BATCH_SIZE ,false, false, null);
	}

	/**
	 * Constructor for initializing the IndexedFileParser</br>
	 * It will open the file in read-only mode and the default batch size (3000) will be applied
	 * @param filePathName : File to be read to get the records
	 * @param delimiter : The delimiter to parse the column values
	 * @param isIncludeColumnsWithEmptyValue : This flag is set if the output records are considered to have empty values for input columns with empty values 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public IndexedFileParser(String filePathName, String delimiter , Boolean isIncludeColumnsWithEmptyValue) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(filePathName), delimiter, DEFAULT_ROWS_BATCH_SIZE, isIncludeColumnsWithEmptyValue, false, null);
	}

	/**
	 * Constructor for initializing the IndexedFileParser</br>
	 * It will open the file in read-only mode and the default batch size (3000) will be applied
	 * @param filePathName : File to be read to get the records
	 * @param delimiter : The delimiter to parse the column values
	 * @param shouldIncludeColumnsWithEmptyValue : This flag is set if the output records are considered to have empty values for input columns with empty values
	 * @param isEnableIndexing : This flag is set if the records the required to be index while reading the file
	 * @param keyColumns : unique columns (values) to be used to form key to that row or line 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public IndexedFileParser(String filePathName, String delimiter , Boolean shouldIncludeColumnsWithEmptyValue, boolean isEnableIndexing, int[] keyColumns) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(filePathName), delimiter, DEFAULT_ROWS_BATCH_SIZE, shouldIncludeColumnsWithEmptyValue, isEnableIndexing, keyColumns);
	}

	/**
	 * Constructor for initializing the IndexedFileParser 
	 * @param file : The file to be read to get the records
	 * @param mode : File mode (FILE_READ_MODE)
	 * @param delimeter : The delimiter to parse the column values
	 * @param batchSize : To define how many records to be read in one batch
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public IndexedFileParser(String filePathName, String delimiter, int batchSize) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(filePathName), delimiter, batchSize ,false, false, null);
	}

	/**
	 * Constructor for initializing the IndexedFileParser</br>
	 * It will open the file in read-only mode and the default batch size (3000) will be applied
	 * @param file : File to be read to get the records
	 * @param delimiter : The delimiter to parse the column values
	 * @throws FileNotFoundException
	 */
	public IndexedFileParser(File file, String delimiter) throws FileNotFoundException {
		this(file, delimiter, DEFAULT_ROWS_BATCH_SIZE ,false, false, null);
	}


	/**
	 * Constructor for initializing the IndexedFileParser 
	 * @param file : The file to be read to get the records
	 * @param mode : File mode (FILE_READ_MODE)
	 * @param delimeter : The delimiter to parse the column values
	 * @param batchSize : To define how many records to be read in one batch
	 * @param shouldIncludeColumnsWithEmptyValue : This flag is set if the output records are considered to have empty values for input columns with empty values 
	 * @throws FileNotFoundException
	 */
	public IndexedFileParser(File file, String delimeter, int batchSize, boolean shouldIncludeColumnsWithEmptyValue, boolean isCreateIndexesEnabled, int[] keyColumns) throws FileNotFoundException {
		if(file == null ) {
			throw new IllegalArgumentException("file object is null");
		} 

		if (isCreateIndexesEnabled && (keyColumns == null || keyColumns.length <= 0)) {
			throw new IllegalArgumentException("key columns cant be null or empty if indexing is enabled");
		}

		if(batchSize <= 0) {
			throw new IllegalArgumentException(" incorrect batch size ");
		}
		
		this.rowsBatchSize = batchSize;
		this.delimiter = delimeter;
		this.createIndexs = isCreateIndexesEnabled;
		this.indexColumns = keyColumns;
		this.fileRandomAccess = new RandomAccessFile(file, FILE_READ_MODE);
		this.channelRandomAccess = fileRandomAccess.getChannel();
		this.delimiterRegexLimit = shouldIncludeColumnsWithEmptyValue ? -1 : 0;
	}

	/**
	 * Every call to this method will return the number of lines defined by batchSize property.</br>
	 * Note : The batch size is not strictly followed due to some optimization. There could be a 5-10% tolerance of getting more records.
	 * Use of return list size or iterator.hasNext() method is recommended for iterating over the results.
	 * @return List of String[] 
	 * @throws Exception
	 */
	public ArrayList<String[]> getNextRowsBatchAsColumnArray() throws Exception {

		ArrayList<String[]> rowsBatch = new ArrayList<String[]>(rowsBatchSize);

		getRecords(true, rowsBatch, DEFAULT_COLUMN_INDEX);

		return rowsBatch;
	}

	/**
	 * reset this parser 
	 * @return
	 */
	public void reset() {
		filePointer = POSITION_START_FILE;
	}

	/**
	 * Create a new {@link IndexedFileParser} from the existing one for reading different file with provided delimiter
	 * @param file
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public IndexedFileParser createReader(File file, String delimiter) throws IOException {
		close();
		return (new IndexedFileParser(file, delimiter));
	}

	/**
	 * Reads and returns all the records in the give file
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String[]> getAllRows() throws IOException {
		ArrayList<String[]> columnRecordsList = new ArrayList<String[]>();
		getRecords(false, columnRecordsList, DEFAULT_COLUMN_INDEX);
		return columnRecordsList;
	}
	

	/**
	 * Splits the line read into String array using the delimiter
	 * and adds it to the resulting records list which will be returned
	 * as part of reading the file.
	 * @param row
	 * @param records
	 * @param record
	 */
	private void addRowToList(StringBuilder row, ArrayList<String[]> records, String[] record) {
		long line = filePointer;
		filePointer += (row.toString().getBytes().length + 1); // +1 for new line char		
		record = row.toString().split(delimiter, delimiterRegexLimit);

		//Adding the indexes to the index table
		if(createIndexs) {
			String key = "";
			for(int index : indexColumns) {
				key += StringUtils.trimIt(record[index]);
			}

			if(sortByColumnIndex >= 0) {
				updateIndex(key, new Long(line), record[sortByColumnIndex]);
			} else {
				updateIndex(key, new Long(line), null);
			}
		}

		records.add(record); 
		row.delete(0, row.length());
	}

	/**
	 * The method which does the job of reading the file based on different batch size and  delimiter</br>
	 * It is not supposed to be exposed or be accessible outside this class.
	 * @param isBatchEnabled : Considers the batch size and add it to records list
	 * @param records : The array list filled with records
	 * @param indexColumns : This parameter is used if you need the values only from a particular column
	 * @throws IOException
	 */
	private void getRecords(final boolean isBatchEnabled, ArrayList<String[]> records, final int columnIndex) throws IOException {
		channelRandomAccess.position(filePointer);
		ByteBuffer buffer = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);

		int numberOfBytesRead = channelRandomAccess.read(buffer); //read 8KB into buffer

		StringBuilder row = new StringBuilder();
		String[] record = null;
		while(numberOfBytesRead != -1) { 
			buffer.flip(); 
			CharBuffer charBuffer = charSet.decode(buffer);
			while (charBuffer.hasRemaining()) {
				char c = charBuffer.get();
				if(StringUtils.isNewline(c)) {
					addRowToList(row, records, record);
					continue;
				}
				row.append(c);
			}			
			buffer.clear(); 

			if(isBatchEnabled && (records.size() >= rowsBatchSize)) {
				break;
			}
			numberOfBytesRead = channelRandomAccess.read(buffer);

			//If not eol detected but eof, so include that record too
			if(numberOfBytesRead == -1 && row.toString().length() != 0) {
				addRowToList(row, records, record);				
			}
		}
	}

	/*
	 * Updates the index iff the sortByColumn value is greater than equal to the existing value
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void updateIndex(String key, Long index, final String currentValue) {		
		String previousValue = null;
		boolean isUpdateRequired = true;

		if(columnComparator != null && ((previousValue = mapSortByColumn.get(key)) != null)) {
			isUpdateRequired = columnComparator.compare(previousValue, currentValue);
		}

		if(isUpdateRequired) {
			indexTable.put(key, index);
			mapSortByColumn.put(key, currentValue);
		}
	}

	/**
	 * Get one record/line as String at a time.
	 * @throws IOException
	 */
	public String getNextRow() throws IOException {	
		StringBuilder record = new StringBuilder();
		channelRandomAccess.read(dataBlock);
		dataBlock.flip();

		CharBuffer charBuffer = charSet.decode(dataBlock);

		while(charBuffer.hasRemaining()) {
			char c = charBuffer.get();
			if(StringUtils.isNewline(c)) {					
				filePointer += (record.toString().getBytes().length + 1); // +1 for new line char				
				channelRandomAccess.position(filePointer);
				break;
			}
			record.append(c);
		}
		
		dataBlock.clear();

		return StringUtils.trimIt(record.toString());
	}
	
	/**
	 * Get one record/line as String at a time.
	 * @return
	 * @throws IOException
	 */
	public String[] getNextRowAsArray() throws IOException {
		Long line = filePointer; 
		String[] record =  getNextRow().split(delimiter, delimiterRegexLimit);
		
		if(createIndexs) {
			String key = "";
			for(int index : indexColumns) {
				key += StringUtils.trimIt(record[index]);
			}

			if(sortByColumnIndex >= 0) {
				updateIndex(key, Long.valueOf(line), record[sortByColumnIndex]);
			} else {
				updateIndex(key, Long.valueOf(line), null);
			}
		}
		
		return record;
	}

	/**
	 * To read lines as plain text and without splitting it into columns.
	 * This implementation would be following the batching mode as well.
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String> getNextRowsBatchAsText() throws IOException {
		ArrayList<String> listOfRows = new ArrayList<String>(rowsBatchSize);
		String line = getNextRow();
			while(listOfRows.size() < rowsBatchSize && !(StringUtils.isNullOrEmpty(line))) {
				listOfRows.add(line);
				line = getNextRow();
			}
		return listOfRows;
	}
	

	/**

	 * This methods reads one line or row at a time and split 
	 * the column values using the delimiter and returns the Array.
	 * @param offset in bytes
	 * @return String array as row
	 * @throws IOException
	 */
	public String[] getNextRowAsArrayAt(long offset) throws IOException {
		String[] row = null;
		fileRandomAccess.seek(offset);
		String line = getNextRow();

		if(line == null)
			return null;

		row = line.split(delimiter, delimiterRegexLimit);

		return row;
	}


	/**
	 * Get the index table for the current file being read.
	 * It will be having any records if indexing is enable or createIndexTable is called atleast once with this {@link IndexedFileParser} instance.
	 * @return
	 */
	public IndexTable<String, Long> getIndexTable () {
		return indexTable;
	}
	/**
	 * Returns the record residing on the file system at the specified index.
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	public String getNextRowAsStringAt(long offset) throws IOException {
		fileRandomAccess.seek(offset);
		return getNextRow();
		//		return fileRandomAccess.readLine();
	}

	/**
	 * Reads the very first line from the file to get column headers</br>
	 * Note : This method will not be resetting the file pointer position to first position 
	 * @return
	 * @throws IOException
	 */
	public String[] getColumnsHeaders() throws IOException {
		String headers[] = getNextRowAsArrayAt(POSITION_START_FILE);

		fileRandomAccess.seek(filePointer);
		return headers;

	}	

	/**
	 * Retrieves the all values of a column in a set
	 * @param columnIndex
	 * @return Set containing the values
	 */
	public Set<String> getSetOfValuesAt(int columnIndex) {
		HashSet<String> set = new HashSet<String>();
		
		return set;
	}

	/**
	 * Reads the specified file and returns the index table for it.
	 * The key column would be the indexes of the columns in the delimiter separated file combined to create the key to the row or line.
	 * Example : A,B,C,D row with index {1, 3} has key value as "AC".
	 * @param keyColumns : columns to be used as key to that row or line
	 * @return {@link IndexTable} 
	 * @throws IOException
	 */
	public IndexTable<String, Long> createIndexTable(int[] keyColumns) throws IOException {
		return createIndexTable(keyColumns, -1, null);
	}

	/**
	 * Reads the specified file and returns the index table for it.
	 * The key column would be the indexes of the columns in the delimiter separated file combined to create the key to the row or line.
	 * Example : A,B,C,D row with index {1, 3} has key value as "AC".
	 * @param keyColumns : columns to be used as key to that row or line
	 * @param sortColumnIndex
	 * @param comparator :   to compare the currently processed value vs stored in  mapSortByColumn
	 * @return {@link IndexTable} 
	 * @throws IOException
	 */
	public IndexTable<String, Long> createIndexTable(int[] keyColumns, int sortColumnIndex, @SuppressWarnings("rawtypes") Comparator comparator) throws IOException {
		indexTable.clear();
		this.sortByColumnIndex = sortColumnIndex;
		this.columnComparator = comparator;
		long prevPointerPosition = filePointer;		
		filePointer = POSITION_START_FILE;
		createIndexs = true;
		if(keyColumns == null || keyColumns.length == 0) {
			throw new IllegalArgumentException(" Key columns can't be null or empty. ");
		}
		indexColumns = keyColumns;
		getAllRows().clear();
		createIndexs = false;
		filePointer = prevPointerPosition;
		return indexTable;
	}

	/**
	 * Overriding this method to make sure the lock on file is released
	 */
	@Override
	public void finalize() {
		try {
			close();
		} catch (Exception e) {  
			//can't do much here, let it be collected.
		}
	}


	/**
	 * It closes the underlying file stream  and all the associated channels to it.
	 * @throws IOException
	 */
	public void close() throws IOException {

		if(channelRandomAccess != null) {
			channelRandomAccess.close();
		}

		if(fileRandomAccess != null) {
			fileRandomAccess.close();
		}

		indexTable.clear();
		dataBlock.clear();
		filePointer = POSITION_START_FILE;		
	}	
}