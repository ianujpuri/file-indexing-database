package org.uwindsor.mac.acc.drivedepot.htmlparser.parser;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Element;



public interface IParser {
	
	public List<Element> parseElementsByTag(String tag);
	
	public Element parseElementById(String elementId);
	
	public void parseChildNodeDetails(List<Element> childNodes) throws IOException;
	
	public void parse();
	
	public void release();

}
