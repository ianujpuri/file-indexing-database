package org.uwindsor.mac.acc.drivedepot.htmlparser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.model.Car;
import org.uwindsor.mac.acc.drivedepot.model.IndexTable;
import org.uwindsor.mac.acc.drivedepot.model.Page;
import org.uwindsor.mac.acc.drivedepot.model.Row;
import org.uwindsor.mac.acc.drivedepot.parser.csv.IndexedFileParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;
import org.uwindsor.mac.acc.drivedepot.writer.MultiIndexFileWriter;

/**
 * Parser implementation for Mazda car listings HTML pages. Extends the
 * HTMLParser abstract class. Responsible for parsing Mazda-specific elements
 * and building inverted indexes.
 * 
 * @author Anuj Puri (110120950)
 *
 *         Method Names and Descriptions:
 *
 *         <br>
 *         MazdaCarParser(): Constructor initializing file writer, file parser,
 *         and master index table for Mazda. <br>
 *         parseElementsByTag(String tag): Overrides the parent method to return
 *         an empty set of elements. <br>
 *         parseChildNodeDetails(List<Element> childNodes): Parses details of
 *         child nodes, extracting and processing relevant Mazda car
 *         information. <br>
 *         price(String price): Extracts and returns the price from the given
 *         input string. <br>
 *         createPageObjForCache(Row record): Creates a Car object and adds it
 *         to the cache, updating inverted indexes. <br>
 *         parse(): Parses the main elements on the Mazda car listings HTML
 *         page. <br>
 *         getCars(String key): Retrieves a list of Car objects associated with
 *         the given key from the master index table. <br>
 *         release(): Releases resources associated with the Mazda car parser.
 *         <br>
 *         main(String[] args): Main method for testing and running the
 *         MazdaCarParser.
 *
 */
public class MazdaCarParser extends HTMLParser {
	private MultiIndexFileWriter fileWriterMazda = new MultiIndexFileWriter(
			ConfigUtil.getpropertyValue(Constants.KEY_OUTPUT_PATH_MAZDA), true);
	private IndexedFileParser fileParser = new IndexedFileParser(
			ConfigUtil.getpropertyValue(Constants.KEY_OUTPUT_PATH_MAZDA), StringUtils.SYMBOL_TILDA);

	private IndexTable<String, List<Long>> masterIndexTableMazda = new IndexTable<String, List<Long>>();

	/**
	 * Constructor for MazdaCarParser. Initializes file writer, file parser, and
	 * master index table for Mazda.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public MazdaCarParser() throws IOException {
		super();
		try {
			masterIndexTableMazda = (IndexTable<String, List<Long>>) IOUtils
					.unmarshalingObject(IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.KEY_INDEX_MAZDA)));
		} catch (Exception e) {
			masterIndexTableMazda = new IndexTable<>();
		}
		buildInvertedIndexes(ConfigUtil.getpropertyValue(Constants.KEY_OUTPUT_PATH_MAZDA), Constants.URL_PAGE_MAZDA);
	}

	/**
	 * Overrides the parent method to return an empty set of elements.
	 * 
	 * @param tag
	 * @return List of elements (empty)
	 */
	@Override
	public List<Element> parseElementsByTag(String tag) {
		return new Elements();
	}

	/**
	 * Parses details of child nodes, extracting and processing relevant Mazda car
	 * information.
	 * 
	 * @param childNodes List of child nodes
	 * @throws IOException
	 */
	@Override
	public void parseChildNodeDetails(List<Element> childNodes) throws IOException {
		rConfig.setUniqueColumns(INDEX_COLUMNS);
		for (Element e : childNodes) {
			Row record = new Row(rConfig);

			String carName = parseElementByClass(e, Constants.ELEMENT_CAR_MAZDA_TITLE_NAME);
			record.setColumnValue(Constants.HEADER_CAR_TITLE, carName);
			record.setColumnValue(Constants.HEADER_CAR_MODEL, carName);

			String namePrice = parseElementByClass(e, Constants.ELEMENT_CAR_MAZDA_TITLE_PRICE);
			record.setColumnValue(Constants.HEADER_CAR_PRICE, price(namePrice));

			String detail = namePrice.replace(price(namePrice), StringUtils.EMPTY_BLANK_STRING);
			detail += parseElementByClass(e, Constants.ELEMENT_CAR_MAZDA_DETAILS);
			record.setColumnValue(Constants.HEADER_CAR_DETAILS, detail);

			record.setColumnValue(Constants.HEADER_CAR_SELLER_NAME, Constants.MAZDA_CAR_SELLER_NAME);
			record.setColumnValue(Constants.HEADER_CAR_MAKE, Constants.MAZDA_CAR_MAKE);
			record.setColumnValue(Constants.HEADER_CAR_TYPE, Constants.MAZDA_CAR_TYPE);
			record.setColumnValue(Constants.HEADER_CAR_LOCATION,
					StringUtils.isNullOrEmpty(location) ? Constants.MAZDA_CAR_LOCATION
							: StringUtils.toLowercase(location));

			record.setColumnValue(Constants.HEADER_CAR_ODOMETER, Constants.MAZDA_CAR_ODOMETER);
			try {
				record.setColumnValue(Constants.HEADER_CAR_IMG_URL, e.select("img.lazy").attr("src"));
			} catch (Exception ex) {

			}
			createPageObjForCache(record);

			if (setRecords.add(record.toString())) {
				fileWriterMazda.write(record.toString(), record.uniqueKey());
			}

			System.out.println("\n");
		}
	}

	private Car createPageObjForCache(Row record) {
		Car car = new Car(record);

		Page page = new Page(Constants.URL_PAGE_MAZDA);
		page.add(car);

		trieStore.add(car.getCarSellerName(), page);
		trieStore.add(car.getCarMake(), page);
		trieStore.add(record.uniqueKey(), page);

		return car;
	}

	/**
	 * Extracts and returns the price from the given input string.
	 * 
	 * @param price Price input string
	 * @return Extracted price
	 */
	public String price(String price) {
		Pattern pricePattern = Pattern.compile("\\$[\\d,]+");

		// Create a Matcher object
		Matcher matcher = pricePattern.matcher(price);

		// Check if the pattern is found in the input
		if (matcher.find()) {
			// Get the matched price
			return matcher.group();
		}

		return StringUtils.EMPTY_BLANK_STRING;
	}

	/**
	 * Parses the main elements on the Mazda car listings HTML page.
	 */
	@Override
	public void parse() {
		try {
			Elements e = parseElementsByClass(Constants.KEY_PARENT_TAG_MAZDA);
			if (e.size() > 0) {

				Elements elements = e.get(0).select(Constants.KEY_LISTING_SELECTOR_MAZDA);
				;
				parseChildNodeDetails(elements);
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

	public List<Car> getAllCars() {
		Iterator<String> keys = masterIndexTableMazda.keySet().iterator();
		List<Car> cars = new ArrayList<>();

		while (keys.hasNext()) {
			cars.addAll(getCars(keys.next()));
		}

		return cars;
	}

	/**
	 * Retrieves a list of Car objects associated with the given key from the master
	 * index table.
	 * 
	 * @param key Key for retrieval
	 * @return List of Car objects
	 */
	public List<Car> getCars(String key) {
//		System.out.println(masterIndexTableMazda);
//		System.out.println(key);
		List<Car> cars = new ArrayList<>();
		try {
			List<Long> list = masterIndexTableMazda.get(key);
			Iterator<Long> indexes = list.iterator();

			while (indexes.hasNext()) {
				long value = Long.valueOf(StringUtils.stringValueOf(indexes.next()));

				Car car = new Car(rConfig, fileParser.getNextRowAsStringAt(value));
				cars.add(car);

			}
		} catch (Exception e) {
			return cars;
		}

		return cars;
	}

	@Override
	public void release() {
		try {
			IOUtils.marshalingObject(fileWriterMazda.getIndexMapAggregated(),
					IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.KEY_INDEX_MAZDA)));
		} catch (Exception e) {
			// ignore, not able to store indexes on file system
		}
		fileWriterMazda.close();
	}

}
