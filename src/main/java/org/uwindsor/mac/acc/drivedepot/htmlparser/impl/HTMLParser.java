package org.uwindsor.mac.acc.drivedepot.htmlparser.impl;

/**
 * Abstract class for parsing HTML pages and building inverted indexes.
 * Provides common functionality for parsing HTML elements.
 * @author Anuj Puri (110120950)
 *
 * Method Names and Descriptions:
 *
 * <br> HTMLParser(): Constructor initializing properties and RowConfig.
 * <br> init(File file): Initializes the HTML parser with the given file.
 * <br> parseElementById(String elementId): Parses and returns an HTML element by its ID.
 * <br> parseElementsByClass(String selector): Parses and returns HTML elements by class selector.
 * <br> parseElementByClass(Element e, String selector): Parses and returns the text content of an element by class selector.
 * <br> setValuesUsingPattern(Row record): Sets column values in the Row using predefined patterns.
 * <br> parseFiles(File file): Parses HTML files, extracting car information.
 * <br> buildInvertedIndexes(String filePath, String url): Builds inverted indexes from the provided file path and URL.
 */
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.htmlparser.parser.IParser;
import org.uwindsor.mac.acc.drivedepot.indextable.IndexTableProvider;
import org.uwindsor.mac.acc.drivedepot.invertedindex.dsstore.Trie;
import org.uwindsor.mac.acc.drivedepot.model.Car;
import org.uwindsor.mac.acc.drivedepot.model.Page;
import org.uwindsor.mac.acc.drivedepot.model.Row;
import org.uwindsor.mac.acc.drivedepot.model.RowConfig;
import org.uwindsor.mac.acc.drivedepot.parser.csv.IndexedFileParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.PatternFinder;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;

public abstract class HTMLParser implements IParser {

	/**
     * Path to the application properties file.
     */
	public static final String PATH_PROPERTIES_FILE = "src/main/resources/application.properties";

	protected Set<String> setRecords = new HashSet<>();
	protected String location = StringUtils.EMPTY_BLANK_STRING;
	protected boolean isUsedCar = true;
	
	/**
     * Trie data structure for building inverted indexes.
     */
	protected static Trie trieStore = IndexTableProvider.getTrie();
	
	/**
     * Jsoup parser for HTML documents.
     */
	private Document jsoupParser = null;
	
	/**
     * Configuration for row data.
     */
	protected RowConfig rConfig = null;

	protected static final Logger LOGGER = Logger.getLogger(HTMLParser.class);
	/**
     * Array of index columns.
     */
	protected static final String[] INDEX_COLUMNS = {Constants.HEADER_CAR_LOCATION,Constants.HEADER_CAR_MAKE, 
			 Constants.HEADER_CAR_TYPE};

	/**
     * Static block to load properties from the application properties file.
     */
	static {
		File propertiesFile;
		try {
			propertiesFile = IOUtils.getFile(PATH_PROPERTIES_FILE);
			ConfigUtil.loadProperties(propertiesFile, new Properties());
		} catch (IOException e) {
			System.err.println("Error Occurred while loading properties.");
		}
	}

	/**
    * Constructor initializing properties and RowConfig.
    * @throws IOException If an I/O error occurs.
    */
	public HTMLParser() throws IOException {
		File propertiesFile = IOUtils.getFile(PATH_PROPERTIES_FILE);
		ConfigUtil.loadProperties(propertiesFile, new Properties());
		
		String header = ConfigUtil.getpropertyValue(Constants.KEY_HEADER_FILE);
		this.rConfig = new RowConfig(header, StringUtils.SYMBOL_TILDA);
	}

	/**
     * Initializes the HTML parser with the given file.
     * @param file The file to initialize the parser with.
     * @throws IOException If an I/O error occurs.
     */
	protected void init(File file) throws IOException {
		this.jsoupParser = Jsoup.parse(file, "UTF-8", file.getAbsolutePath());
		isUsedCar = file.getAbsolutePath().contains(Constants.TYPE_CAR_USED);
	}

	/**
     * Parses and returns an HTML element by its ID.
     * @param elementId The ID of the element to parse.
     * @return The parsed HTML element.
     */
	@Override
	public Element parseElementById(String elementId) {
		return this.jsoupParser.getElementById(elementId);
	}

	/**
     * Parses and returns HTML elements by class selector.
     * @param selector The class selector to parse elements for.
     * @return The parsed HTML elements.
     */
	public Elements parseElementsByClass(String selector) {
		return this.jsoupParser.select(selector);
	}

	 /**
     * Parses and returns the text content of an element by class selector.
     * @param e The HTML element to parse.
     * @param selector The class selector to parse the element for.
     * @return The parsed text content.
     */
	protected String parseElementByClass(Element e, String selector) {
		
		if (e.select(selector).eachText().size() > 1) {
			return e.select(selector).eachText().get(0);

		} else {

			return (e.select(selector).text());
		}
	}

	/**
     * Sets column values in the Row using predefined patterns.
     * @param record The Row to set values for.
     */
	protected void setValuesUsingPattern(Row record) {
		record.setColumnValue(Constants.HEADER_CAR_MAKE, PatternFinder.getCarCompany(record.toString()));
		record.setColumnValue(Constants.HEADER_CAR_MODEL, PatternFinder.getCarName(record.toString()));
		record.setColumnValue(Constants.HEADER_CAR_YEAR, PatternFinder.extractYearFromInputString(record.toString()));
		record.setColumnValue(Constants.HEADER_CAR_TYPE, isUsedCar ? Constants.TYPE_CAR_USED : Constants.TYPE_CAR_NEW);
		record.setColumnValue(Constants.HEADER_CAR_LOCATION, location);
	}

	/**
     * Parses HTML files, extracting car information.
     * It uses DFS recursive method to find all the files and folders 
     * for agive directory path.
     * @param file The file or directory to parse.
     * @throws IOException If an I/O error occurs.
     */
	public void parseFiles(File file) throws IOException {

		if (!file.isDirectory()) {
			this.init(file);
			this.parse();
			return;
		}
		location = file.getName();
		for (File f : file.listFiles()) {
			parseFiles(f);
		}
	}
	
	/**
     * Builds inverted indexes from the provided file path and URL.
     * @param filePath The file path to build indexes from.
     * @param url The URL associated with the data.
     */
	public void buildInvertedIndexes(String filePath, String url) {
		try {
			IndexedFileParser parser = new IndexedFileParser(IOUtils.getFile(filePath), StringUtils.SYMBOL_TILDA);
			List<String[]> listOfRecords = parser.getAllRows();
			for(String[] row : listOfRecords) {
				Page page = new Page(url);
				trieStore.add(row[rConfig.getIndex(Constants.HEADER_CAR_SELLER_NAME)], page);
				trieStore.add(row[rConfig.getIndex(Constants.HEADER_CAR_MAKE)], page);
				trieStore.add(row[rConfig.getIndex(Constants.HEADER_CAR_LOCATION)], page);
				
				StringBuilder uniqueKey = new StringBuilder();
				uniqueKey.append(StringUtils.trimIt(row[rConfig.getIndex(Constants.HEADER_CAR_MAKE)]));
				uniqueKey.append(StringUtils.trimIt(row[rConfig.getIndex(Constants.HEADER_CAR_TYPE)]));
				uniqueKey.append(StringUtils.trimIt(row[rConfig.getIndex(Constants.HEADER_CAR_LOCATION)]));
				
				trieStore.add(uniqueKey.toString(), page);
				
			}
		} catch(Exception e) {
			//ignore, it won't hamper the code 
			//inverted indexes will be created later while crawling as well.
		}
		
	}

}
