package org.uwindsor.mac.acc.drivedepot.parser.csv;

import java.io.IOException;
import java.util.List;

/**
 * Abstraction for any index file parser
 * @author Anuj Puri(110120950)
 *
 */
public interface IParser {

	List<String[]>  getNextRowsBatchAsColumnArray() throws Exception;
	
	List<String[]> getAllRows() throws Exception ;
	
	void close() throws IOException;
}
