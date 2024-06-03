package org.uwindsor.mac.acc.drivedepot.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.uwindsor.mac.acc.drivedepot.indextable.IndexTableProvider;
import org.uwindsor.mac.acc.drivedepot.model.IndexTable;
import org.uwindsor.mac.acc.drivedepot.model.Row;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

/**
 * It writes the data into the file. Along
 * with writing the data, it does the indexing of the data as well.
 * This class uses {@link IndexedFileWriter}, and maintains multiple
 * indexes for the same KEY. Primarily useful when you have keys being duplicated.
 * @author 110120950 (Anuj Puri)
 *
 */
public class MultiIndexFileWriter {
	
	private IndexedFileWriter indexFileWriter = null;
	private IndexTable<String, List<Long>> masterIndexTable = new IndexTable<String, List<Long>>(); 
	private IndexTable<Object, Object> indexTable = IndexTableProvider.getAppIndexTable();	
	
	/**
	 * 
	 * 
	 * @param outputFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MultiIndexFileWriter(String outputFilePath) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(outputFilePath), false, false);
		
	}

	
	/**
	 * 
	 * 
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MultiIndexFileWriter(String outputFile, boolean appendMode) throws FileNotFoundException, IOException {
		indexFileWriter = new IndexedFileWriter(outputFile, appendMode, IndexedFileWriter.WRITE_REGULAR_SIZED_STRINGS);
	}
	
	/**
	 * 
	 * 
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MultiIndexFileWriter(File outputFile, boolean appendMode, boolean writeLengthyString) throws FileNotFoundException, IOException {
		indexFileWriter = new IndexedFileWriter(outputFile, appendMode, writeLengthyString ? IndexedFileWriter.WRITE_LENTHY_SIZED_STRINGS : IndexedFileWriter.WRITE_REGULAR_SIZED_STRINGS);
	}

	/**
	 * 
	 * 
	 * @param outputFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MultiIndexFileWriter(String outputFilePath, boolean appendMode, boolean writeLengthyString) throws FileNotFoundException, IOException {
		this(IOUtils.getFile(outputFilePath), appendMode, writeLengthyString);
		
	}
	
	/**
	 * 
	 * 
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MultiIndexFileWriter(File outputFile) throws FileNotFoundException, IOException {
		this(outputFile, false, false);
	}

	
	/**
	 * 
	 * 
	 * @param row
	 * @param key
	 * @throws IOException
	 */
	public void write(String row, String key) throws IOException {
		if(!StringUtils.isNullOrEmpty(row)) {
			indexFileWriter.writeAndIndex(row, key, indexTable);
			
			Long index = (Long)indexTable.get(key);
			List<Long> listOfIndices = masterIndexTable.get(key);
			if(listOfIndices == null) {
				listOfIndices = new ArrayList<Long>();
				masterIndexTable.put(key, listOfIndices);
			}
			
			listOfIndices.add(index);
		}
	}
	
	/**
	 * 
	 * 
	 * @param row
	 * @param key
	 * @throws IOException
	 */
	public void write(Row row) throws IOException {
		write(row.toString(), row.uniqueKey());
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, List<Long>> getIndexMapAggregated() {
		return this.masterIndexTable;
	}
	
	/*
	 * close the file stream
	 */
	public void close() {
		IOUtils.close(indexFileWriter);
	}
}
