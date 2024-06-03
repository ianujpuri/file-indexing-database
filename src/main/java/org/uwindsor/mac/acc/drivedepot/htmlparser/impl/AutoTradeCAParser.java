package org.uwindsor.mac.acc.drivedepot.htmlparser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * HTML Parser implementation for AutoTrader Canada website. Parses HTML pages,
 * extracts car information, and builds inverted indexes.
 * 
 * @author Anuj Puri (110120950)
 *
 *         Method Names and Descriptions:
 *
 *         <br>
 *         AutoTradeCAParser(): Constructor initializing file writers, parsers,
 *         and master index table. <br>
 *         parseChildNodeDetails(List<Element> childNodes): Parses details of
 *         child nodes and adds records to the file. <br>
 *         release(): Releases resources, stores indexes, and closes file
 *         writers. <br>
 *         getCars(String key): Retrieves a list of cars associated with the
 *         given key from the master index table. <br>
 *         createPageObjForCache(Row record): Creates a Car and Page object for
 *         caching and building inverted indexes. <br>
 *         main(String[] args): Entry point for parsing AutoTrader Canada HTML
 *         files and releasing resources. <br>
 *         parseElementsByTag(String tag): Overrides the method to parse
 *         elements by tag. <br>
 *         isValidRecord(Element element): Checks if a parsed record is valid
 *         based on title and price presence. <br>
 *         parse(): Overrides the method to parse HTML elements and extract car
 *         information.
 */
public class AutoTradeCAParser extends HTMLParser {

	private MultiIndexFileWriter fileWriterAuto = new MultiIndexFileWriter(
			ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_AUTOTRADE), true);
	private IndexedFileParser fileParser = new IndexedFileParser(
			ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_AUTOTRADE), StringUtils.SYMBOL_TILDA);

	private IndexTable<String, List<Long>> masterIndexTableAuto = new IndexTable<String, List<Long>>();

	private static final String[] KEYELEMENTS_SELECTOR = { Constants.ELEMENT_CAR_DEAL, Constants.ELEMENT_CAR_DETAILS,
			Constants.ELEMENT_CAR_LOCATION_DISTANCE, Constants.ELEMENT_CAR_ODOMETER, Constants.ELEMENT_CAR_PRICE,
			Constants.ELEMENT_CARS_TITLE, Constants.ELEMENT_SELLER_NAME };

	/**
	 * Constructor foe AutoTradeCAParser.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public AutoTradeCAParser() throws IOException {
		super();

		try {
			masterIndexTableAuto = (IndexTable<String, List<Long>>) IOUtils
					.unmarshalingObject(IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.KEY_INDEX_AUTOTRADE)));
		} catch (Exception e) {
			masterIndexTableAuto = new IndexTable<>();
		}
		buildInvertedIndexes(ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_AUTOTRADE),
				Constants.URL_AUTOTRADER_CA);
	}

	/**
	 * Parses details of child nodes and adds records to the file.
	 * 
	 * @param childNodes The list of child nodes to parse.
	 * @throws IOException If an I/O error occurs.
	 */
	@Override
	public void parseChildNodeDetails(List<Element> childNodes) throws IOException {
		rConfig.setUniqueColumns(INDEX_COLUMNS);
		for (Element childNode : childNodes) {
			if (isValidRecord(childNode)) {
				Row record = new Row(rConfig);
				for (String selector : KEYELEMENTS_SELECTOR) {
					record.setColumnValue(Constants.MAP_ELEMENT_NAME_HEADER.get(selector),
							parseElementByClass(childNode, selector).replaceAll(StringUtils.SYMBOL_TILDA,
									StringUtils.EMPTY_BLANK_STRING));

				}
				try {
					String image = childNode.select("div.photo-area").select("div.main-photo").select("img")
							.attr("data-original");
					if (StringUtils.isNullOrEmpty(image)) {
						image = childNode.select("div.photo-area").select("div.main-photo").select("img").attr("src");
					}
					record.setColumnValue(Constants.HEADER_CAR_IMG_URL, image);
				} catch (Exception e) {

				}
				setValuesUsingPattern(record);
				createPageObjForCache(record);

				if (setRecords.add(record.toString())) {
					fileWriterAuto.write(record.toString(), record.uniqueKey());
				}
			}
		}
	}

	/**
	 * Releases resources, stores indexes, and closes file writers.
	 */
	@Override
	public void release() {
		try {
			IOUtils.marshalingObject(fileWriterAuto.getIndexMapAggregated(),
					IOUtils.getFile("src/main/resources/index_master.dat"));
		} catch (Exception e) {
			// ignore, not able to store indexes on file system
		}
		fileWriterAuto.close();
	}

	public List<Car> getAllCars() {
		Iterator<String> keys = masterIndexTableAuto.keySet().iterator();
		List<Car> cars = new ArrayList<>();

		while (keys.hasNext()) {
			cars.addAll(getCars(keys.next()));
		}

		return cars;
	}

	/**
	 * Retrieves a list of cars associated with the given key from the master index
	 * table.
	 * 
	 * @param key The key to retrieve cars for.
	 * @return The list of cars.
	 */
	public List<Car> getCars(String key) {
		List<Car> cars = new ArrayList<>();
		try {
			List<Long> list = masterIndexTableAuto.get(key);
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

	/**
	 * Creates a Car and Page object for caching and building inverted indexes.
	 * 
	 * @param record The row record to create a Car from.
	 * @return The created Car.
	 */
	private Car createPageObjForCache(Row record) {
		Car car = new Car(record);

		Page page = new Page(Constants.URL_AUTOTRADER_CA);
		page.add(car);

		trieStore.add(car.getCarSellerName(), page);
		trieStore.add(car.getCarMake(), page);
		trieStore.add(record.uniqueKey(), page);
		trieStore.getPageInfo(car.getCarMake());

		return car;
	}

	@Override
	public List<Element> parseElementsByTag(String tag) {
		return new Elements();
	}

	/**
	 * Checks if a parsed record is valid based on title and price presence.
	 * 
	 * @param element The HTML element to validate.
	 * @return True if the record is valid, false otherwise.
	 */
	private boolean isValidRecord(Element element) {
		return !(StringUtils.isNullOrEmpty(parseElementByClass(element, Constants.ELEMENT_CARS_TITLE))
				|| StringUtils.isNullOrEmpty(parseElementByClass(element, Constants.ELEMENT_CAR_PRICE)));
	}

	/**
	 * Overrides the method to parse HTML elements and extract car information.
	 */
	@Override
	public void parse() {
		try {
			Element element = parseElementById((ConfigUtil.getpropertyValue(Constants.KEY_ID_CAR_LISTINGS)));

			Elements resultItemInnerDivElements = element.select(Constants.ELEMENT_CARS_LIST);
			parseChildNodeDetails(resultItemInnerDivElements);

		} catch (Exception e) {
			LOGGER.error("Error Occurred while parsing an HTML.." + e.getMessage());

		}

	}

}
