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
import org.uwindsor.mac.acc.drivedepot.model.RowConfig;
import org.uwindsor.mac.acc.drivedepot.parser.csv.IndexedFileParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;
import org.uwindsor.mac.acc.drivedepot.util.StringUtils;
import org.uwindsor.mac.acc.drivedepot.writer.MultiIndexFileWriter;

/**
 * HTML Parser implementation for parsing Car Pages. Extracts car details from
 * HTML pages, creates Car and Page objects, and builds inverted indexes.
 * 
 * @author Anuj Puri (110120950)
 *
 *         Method Names and Descriptions:
 *
 *         <br>
 *         CarPagesParser(): Constructor initializing file writers, parsers, and
 *         master index table. <br>
 *         parseChildNodeDetails(List<Element> childNodes): Parses details of
 *         child nodes and adds records to the file. <br>
 *         release(): Releases resources, stores indexes, and closes file
 *         writers. <br>
 *         getCars(String key): Retrieves a list of cars associated with the
 *         given key from the master index table. <br>
 *         createPageObjForCache(Row record): Creates a Car and Page object for
 *         caching and building inverted indexes. <br>
 *         parseElementsByTag(String tag): Overrides the method to parse
 *         elements by tag. <br>
 *         isValidRecord(Element element): Checks if a parsed record is valid
 *         based on title and price presence. <br>
 *         parse(): Overrides the method to parse HTML elements and extract car
 *         information.
 */
public class CarPagesParser extends HTMLParser {

	private MultiIndexFileWriter fileWriterCar = new MultiIndexFileWriter(
			ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_CARPAGE), true);
	private IndexedFileParser fileParser = new IndexedFileParser(
			ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_CARPAGE), StringUtils.SYMBOL_TILDA);
	private static final String[] KEYELEMENTS_SELECTOR = { Constants.ELEMENT_CAR_PAGE_DETAILS,
			Constants.ELEMENT_CAR_PAGE_LOCATION, Constants.ELEMENT_CAR_PAGE_ODOMETER, Constants.ELEMENT_CAR_PAGE_PRICE,
			Constants.ELEMENT_CAR_PAGE_TITLE, Constants.ELEMENT_CAR_PAGE_SELLER_NAME };

	private IndexTable<String, List<Long>> masterIndexTableCarPages = new IndexTable<String, List<Long>>();

	public CarPagesParser() throws IOException {
		super();
		try {
			masterIndexTableCarPages = (IndexTable<String, List<Long>>) IOUtils
					.unmarshalingObject(IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.KEY_INDEX_CARPAGE)));
		} catch (Exception e) {
			masterIndexTableCarPages = new IndexTable<>();
		}
		buildInvertedIndexes(ConfigUtil.getpropertyValue(Constants.KEY_OUPUT_PATH_CARPAGE), Constants.URL_CARPAGES_CA);
	}

	@Override
	public List<Element> parseElementsByTag(String tag) {
		return new ArrayList<>(); // avoid null handling
	}

	@Override
	public void parseChildNodeDetails(List<Element> childNodes) throws IOException {
		String header = ConfigUtil.getpropertyValue(Constants.KEY_HEADER_FILE);
		RowConfig rConfig = new RowConfig(header, StringUtils.SYMBOL_TILDA);
		rConfig.setUniqueColumns(INDEX_COLUMNS);
		for (Element childNode : childNodes) {
			if (isValidRecord(childNode)) {
				Row record = new Row(rConfig);
				for (String selector : KEYELEMENTS_SELECTOR) {
					if (selector != Constants.ELEMENT_CAR_PAGE_ODOMETER) {
						record.setColumnValue(Constants.MAP_ELEMENT_NAME_HEADER.get(selector),
								parseElementByClass(childNode, selector));
					} else {
						List<String> list = childNode.select(Constants.ELEMENT_CAR_PAGE_ODOMETER).eachText();
						int endIndex = list.size() - 1;
						String odoValue = StringUtils
								.strListToString(childNode.select(Constants.ELEMENT_CAR_PAGE_ODOMETER).eachText()
										.subList(0, endIndex > 0 ? endIndex : 0));
						record.setColumnValue(Constants.MAP_ELEMENT_NAME_HEADER.get(selector), odoValue);
					}
				}
				record.setColumnValue(Constants.HEADER_CAR_IMG_URL, childNode.select("img").first().attr("src"));
				setValuesUsingPattern(record);
				createPageObjForCache(record);
				if (setRecords.add(record.toString())) {
					fileWriterCar.write(record.toString(), record.uniqueKey());
				}

			}

		}
	}

	// target is to add object into Cache as well as build inverted indexes
	private Car createPageObjForCache(Row record) {
		Car car = new Car(record);

		Page page = new Page(Constants.URL_CARPAGES_CA);
		page.add(car);

		trieStore.add(car.getCarSellerName(), page);
		trieStore.add(car.getCarMake(), page);
		trieStore.add(car.getCarLocation(), page);
		trieStore.add(record.uniqueKey(), page);

		return car;
	}

	public List<Car> getAllCars() {
		Iterator<String> keys = masterIndexTableCarPages.keySet().iterator();
		List<Car> cars = new ArrayList<>();

		while (keys.hasNext()) {
			cars.addAll(getCars(keys.next()));
		}

		return cars;
	}

	public List<Car> getCars(String key) {
		List<Car> cars = new ArrayList<>();
		try {
			List<Long> list = fileWriterCar.getIndexMapAggregated().get(key);
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

	private boolean isValidRecord(Element element) {
		return !(StringUtils.isNullOrEmpty(parseElementByClass(element, Constants.ELEMENT_CAR_PAGE_TITLE))
				|| StringUtils.isNullOrEmpty(parseElementByClass(element, Constants.ELEMENT_CAR_PAGE_PRICE)));
	}

	@Override
	public void release() {
		try {
			IOUtils.marshalingObject(fileWriterCar.getIndexMapAggregated(),
					IOUtils.getFile(ConfigUtil.getpropertyValue(Constants.KEY_INDEX_CARPAGE)));
		} catch (Exception e) {
			// ignore, not able to store indexes on file system
		}

		fileWriterCar.close();
	}

	@Override
	public void parse() {
		Elements elements = parseElementsByClass(ConfigUtil.getpropertyValue(Constants.KEY_LISTITNG_SELECTOR_CLASS));
		try {
			parseChildNodeDetails(elements);
		} catch (IOException e) {
			// ignore
		}

	}

}
