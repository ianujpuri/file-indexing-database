package org.uwindsor.mac.acc.drivedepot.writer;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * This is the base interface for supporting any writing logic.
 * Can be implemented to support DB writing, File writing etc.
 * @author 110120950 (Anuj Puri)
 *
 */
public interface IWriter {

	String ENCODING_UTF_16 = "UTF-16";
	String ENCODING_UTF_8 = "UTF-8";
	String ENCODING_DEFAULT = Charset.defaultCharset().name();
	
	
	/**
	 * Write method of String flavour
	 * @param record
	 * @throws IOException
	 */
	void write(String record) throws IOException;
	
	/**
	 * Write method of byte array flavour
	 * @param data
	 * @throws IOException
	 */
	void write(byte[] data) throws IOException;
	
	void close() throws IOException;
}
