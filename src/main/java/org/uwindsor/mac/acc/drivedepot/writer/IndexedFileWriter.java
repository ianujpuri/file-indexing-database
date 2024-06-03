package org.uwindsor.mac.acc.drivedepot.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.uwindsor.mac.acc.drivedepot.model.IndexTable;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;


/**
 * This class is designed to write into a given file Object/Path. It uses NIO Channels to write data to the file, 
 * so non-blocking. Array of bytes is written to the underlying storage in bulk based on block size.</br>
 * It creates the file based on given file path or file object, if it does not exist already. 
 * Auto flush flag ensures of writing the data to the file at the earliest with best efforts.</br>
 * <b>NOTE</b>: It is not thread-safe and uses NIO.
 * @author 110120950 (Anuj Puri)
 *
 */
public final class IndexedFileWriter implements IWriter {

	//8KB, DO NOT MODIFY THIS VALUE
	private static final int DEFAULT_WRITE_BLOCK_SIZE = 8192;

	//start of file or an array
	private static final int POSITION_START = 0; 

	//write mode by default
	private static final String FILE_READ_WRITE_MODE = "rw"; 


	//array for writing data of size equal to file system block (64-bit)
	private byte[] dataBlock = new byte[DEFAULT_WRITE_BLOCK_SIZE]; 

	//storing/buffering data locally
	private byte[] storageByteArray = null;

	private byte[] trashByteArray = new byte[DEFAULT_WRITE_BLOCK_SIZE]; 

	private boolean isClosed = false;
	private int dataStoreSize = 0; 
	private int position = 0;
	private long recordIndex = 0;
	private String encodingCharset = ENCODING_UTF_8;
	//private String encodingCharset = ENCODING_DEFAULT;
	private RandomAccessFile fileRandomAccess;
	private FileChannel channelRandomAccess;
	private IndexTable<String, Long> indexTable;
	
	/**
	 * Considering to write more than 32KB data in one shot
	 */
	public static final byte WRITE_LENTHY_SIZED_STRINGS = 0;
	
	/**
	 * Considering to write less than 32KB data in one shot
	 */
	public static final byte WRITE_REGULAR_SIZED_STRINGS = 1;
	
	/**
	 * Constructor with file path as parameter
	 * @param filePath
	 * @throws IOException
	 */
	public IndexedFileWriter(String filePath) throws IOException {
		this(filePath, false);
	}
	
	/**
	 * Cosntructor with file path as parameter. 
	 * @param filePath
	 * @param openInAppendMode if true, starts writing data at eof, else overrides 
	 * @throws IOException
	 */
	public IndexedFileWriter(String filePath, boolean append) throws IOException {
		this(IOUtils.getFile(filePath), append, WRITE_REGULAR_SIZED_STRINGS);
	}

	
	/**
	 * Constructor with File object as parameter and autoflush flag
	 * @param file : file to which the data has to be written
	 * @param autoFlush : sets the autoflush flag
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public IndexedFileWriter(File file) throws FileNotFoundException, IOException {
		this(file, false, WRITE_REGULAR_SIZED_STRINGS);
	}
	
	/**
	 * Constructor with File object as parameter and autoflush flag
	 * @param file : file path to which the data has to be written
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public IndexedFileWriter(String file, boolean append, byte dataWriteSize) throws FileNotFoundException, IOException {		
		this(IOUtils.getFile(file), append, dataWriteSize);

	}
	
	/**
	 * Constructor with File object as parameter and autoflush flag
	 * @param file : file to which the data has to be written
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public IndexedFileWriter(File file, boolean append, byte dataWriteSize) throws FileNotFoundException, IOException {
		this.fileRandomAccess = new RandomAccessFile(file, FILE_READ_WRITE_MODE);
		this.channelRandomAccess = fileRandomAccess.getChannel();
		if(append) {
			this.fileRandomAccess.seek(file.length());
			this.channelRandomAccess.position(file.length());
			recordIndex += file.length(); //big change
		}
		init(append, dataWriteSize);
	}
	
	/**
	 * Delete contents of file if not opened in append mode and initializes the trashByteArray by default
	 * @param openInAppendMode : if true, file contents are preserved
	 * @throws IOException
	 */
	private void init(boolean openInAppendMode, byte dataWriteSize) throws IOException {
		if(!openInAppendMode) {
			deleteContent();
		}
		
		for(int index=0; index < trashByteArray.length; index++) {
			trashByteArray[index] = -1;
		}
		
		if(dataWriteSize == WRITE_LENTHY_SIZED_STRINGS) {
			storageByteArray = new byte[DEFAULT_WRITE_BLOCK_SIZE<<3];
		} else {
			storageByteArray = new byte[DEFAULT_WRITE_BLOCK_SIZE<<2];
		}
	}
	
	public void setEncoding(String charset) {
		encodingCharset = charset;
	}

	/**
	 * This method handles the logic of storing the data locally and writing it in blocks
	 * to the specified file. It writes the data in append mode. 
	 * @param record : byte array to be written to the file
	 * @throws IOException
	 */
	//TODO: create a circular data structure to avoid shift of data to front of storage
	@Override
	public void write(byte[] dataBytes) throws IOException {
		dataStoreSize += dataBytes.length;

		arrayCopy(dataBytes, POSITION_START, storageByteArray, position, dataBytes.length);

		position += dataBytes.length;		
		if(dataStoreSize >= (DEFAULT_WRITE_BLOCK_SIZE<<1)) {
			int dataToWrite = dataStoreSize;
			int readStartPosition = POSITION_START;
			while(dataToWrite >= DEFAULT_WRITE_BLOCK_SIZE) {
				arrayCopy(storageByteArray, readStartPosition, dataBlock, POSITION_START, dataBlock.length);				
				writeRecord(dataBlock);
				arrayCopy(trashByteArray, POSITION_START, storageByteArray, readStartPosition, trashByteArray.length);
				dataStoreSize = (dataToWrite -= dataBlock.length);
				position = (readStartPosition += dataBlock.length);
			}

			arrayCopy(storageByteArray, position, storageByteArray, POSITION_START, dataStoreSize);
			position = dataStoreSize;			
		}
	}

	/**
	 * This method handles the logic of storing the data locally and writing it in blocks
	 * to the specified file. It writes the data in append mode. 
	 * @param record : String data to be written to the file
	 * @throws IOException
	 */
	@Override
	public void write(String record) throws IOException {
		write(record.getBytes(encodingCharset));		
	}
	
//	/**
//	 * This method writes the data into the file at a specified index.
//	 * It should be used with care only in special cased where the records are
//	 * required to be written or inserted at a particular place in the
//	 * file at any given time.
//	 * 
//	 * @param record
//	 * @param idx
//	 * @throws IOException 
//	 */
	
	/*
	 * CAUTION :  THIS METHOD CAN CAUSE SERIOUS PROBLEMS IF ONE LIKE ME
	 * DOES NOT UNDERSTAND HOW MOST OF THE FILE SYSTEMS WORK.
	 * 
	 */
//	public void writeAt(String record, long idx) throws IOException {
//		this.fileRandomAccess.seek(idx);
//		this.channelRandomAccess.position(idx);
//		writeRecord(record.getBytes(encodingCharset));
//	}

	/**
	 * This method handles the logic of storing the data locally and writing it in blocks.
	 * It will create the index of each record being written to the file system byte array 
	 * and store it into the given IndexTable
	 * @param record
	 * @param key
	 * @param indexTable
	 * @throws IOException
	 */
	public void writeAndIndex(String record, Object key, IndexTable<Object, Object> indexTable) throws IOException {
		if(indexTable == null) {
			throw new NullPointerException(" index table is null");
		}
		
		indexTable.put(key, Long.valueOf(recordIndex));
		byte[] data = record.getBytes(encodingCharset);
		recordIndex += data.length;
		write(data);

	}

	/**
	 * flushes the data left in storageByteArray to the file, if any
	 * @throws IOException 
	 */
	private void flush() throws IOException {
		if(dataStoreSize <= 0) {
			return;
		}

		position = POSITION_START;
		int dataToWrite = Math.min(dataStoreSize, dataBlock.length);
		byte[] data = null;
		while(dataToWrite > 0) {
			data = new byte[dataToWrite]; 

			arrayCopy(storageByteArray, position, data, POSITION_START, dataToWrite);

			writeRecord(data);

			position += DEFAULT_WRITE_BLOCK_SIZE;
			dataToWrite = (dataStoreSize - position);
		}
	}


	/**
	 * Does same work as System.arraycopy(...), just keeping it clean
	 * @param src
	 * @param srcPos
	 * @param dest
	 * @param destPos
	 * @param length
	 */
	private void arrayCopy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}


	/**
	 * Separating the core writing from storing and creating data blocks logic
	 * @param record : the String to be written to the file
	 * @throws IOException
	 */
	protected void writeRecord(byte[] data) throws IOException {
		channelRandomAccess.write(ByteBuffer.wrap(data));
		channelRandomAccess.force(true);
	}

	private void deleteContent() throws IOException {
		channelRandomAccess.truncate(POSITION_START);
	}
	
	/**
	 * ture if close method has been called, false otherwise
	 * @return
	 */
	public boolean isClosed() {
		return this.isClosed;
	}

	/**
	 * It closes the underlying file stream  and all the associated channels to it.
	 * Makes sure to flush the data to the underlying storage before closing the channels
	 * @throws IOException
	 */
	public synchronized void close() throws IOException {
		flush();
		isClosed = true; 
		if(channelRandomAccess != null) {
			channelRandomAccess.close();
		}

		if(fileRandomAccess != null) {
			fileRandomAccess.close();
		}

		trashByteArray = null;
		dataBlock = null;
		storageByteArray = null;
		dataStoreSize = -1;
		position = 0;
	}

	/**
	 * Overriding this method to make sure the lock on file is released
	 */
	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {  

		}
	}
}
