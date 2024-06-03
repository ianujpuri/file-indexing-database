package org.uwindsor.mac.acc.drivedepot.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Utility class to have string manipulation methods
 * @author 110120950 (Anuj Puri)
 *
 */
public final class StringUtils
{
	/**
	 * Instantiation of this class is not required</br>
	 * All the methods and variables can be accessed with class name
	 */
	//TODO: Make it available for instatiation if YOU really feel it is needed.
	private StringUtils() {

	}

	//public class variables
	public static final String YES = "YES";
	public static final String NO = "NO";

	public static final String DELIMITER_SPECIAL_CHAR = "\u0001";
	public static final String DELIMITER_SYMBOL_FILE_SEPARATOR = "\u002F";  //File.separator;
	public static final String SYMBOL_EQUAL = "\u003D";		//"=";
	public static final String SYMBOL_COMMA = "\u002C";		//",";
	public static final String SYMBOL_DOT = "\u002E";			//".";
	public static final String SYMBOL_TAB = "\u0009";			//"\t";
	public static final String SYMBOL_COLON = "\u003A";		//":";
	public static final String SYMBOL_SEMICOLON ="\u003B";	//";";
	public static final String SYMBOL_HYPHEN = "\u002D";		//"-";
	public static final String SYMBOL_UNDERSCORE = "\u005F";  //"_"
	public static final String SYMBOL_CIDLA = "\u00C7";
	public static final String SYMBOL_CARET = "\u005E";
	public static final String SYMBOL_TILDA = "\u007E";
	public static final String SYMBOL_SINGLE_QT = "\u0027";  //"'" -- single quote
	public static final String SYMBOL_LEFT_BRACE = "\u005B";
	public static final String SYMBOL_RIGHT_BRACE = "\u005D";
	public static final String SYMBOL_FORWARD_SLASH = "\u002F";  // "/"
	public static final String SYMBOL_PIPE_UNDERSCORE_PIPE_INPUT_FILE = "\\|_\\|";
	public static final String EMPTY_SPACE_STRING = "\u0020";
	public static final String EMPTY_BLANK_STRING = "";
	public static final String STRING_NULL = null;
	public static final String NEW_LINE = "\n";
	public static final String CARRIAGE_RETURN = "\r";
	public static final String END_OF_FILE_PATTERN = "99\u00C7EOF"; 	//EOF pattern

	public static final String EXT_CSV = ".csv";

	/**
	 * Check if the String is null or empty
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null) || (str.trim().equals(""));
	}

	/**
	 * Check if the String is null or empty
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(StringBuilder str) {
		return (str == null) || (str.toString().trim().equals(""));
	}

	/**
	 * Check if the String is null or empty but may be TAB
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmptyWithTab(String str) {
		return (str == null) || ((str.toString().trim().equals("") && !SYMBOL_TAB.equals(str)));
	}


	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException nfex) {
			try {
				Double.parseDouble(str);
				return true;
			} catch(Exception e) {
				return false;
			}
		}
	}

	public static String multiplyNumericBy(String numericValue, long multiplier) {
		if(isNumeric(numericValue)) {
			return stringValueOf(new BigDecimal(Double.parseDouble(numericValue) * multiplier));
		} else {
			return numericValue;
		}

	}

	/**
	 * Converting a given string into camel case
	 * @param strValue
	 * @return
	 */
	public static String toCamelCase(String strValue){
		String camelCase = strValue.substring(0,1).toUpperCase()+strValue.substring(1).toLowerCase();
		return camelCase;
	}


	public static String toUppercase(String strValue) {
		if(isNullOrEmpty(strValue)) {
			return strValue;
		}

		return strValue.toUpperCase(); 
	}
	
	public static String toLowercase(String strValue) {
		if(isNullOrEmpty(strValue)) {
			return strValue;
		}

		return strValue.toLowerCase(); 
	}

	/**
	 * A map to store and retrieve string keys by ignoring the casing issues. 
	 * @return
	 */
	public static Map<String, String> ignoreCaseMap() {
		return new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8026065232972746597L;

			@Override
			public String put(String key, String value) {
				if(!StringUtils.isNullOrEmpty(key)) {
					key = key.toLowerCase();
				}
				return super.put(key, value);
			}

			@Override
			public String get(Object key) {
				if(key instanceof String && !isNullOrEmpty((String)key)) {
					key = ((String)key).toLowerCase();
				}
				return super.get(key);
			}
		};
	}

	/**
	 * 
	 * @param value
	 * @return the encoded UTF-8 string value corressponding to value
	 */
	public static String getUTF8EncodedValue(String value) {
		if(isNullOrEmpty(value)) {
			return EMPTY_BLANK_STRING;
		}

		ByteBuffer bytes = Charset.forName("UTF-8").encode(value);
		return new String(bytes.array());

	}

	/**
	 * Converts the given string into string array
	 * @param string
	 * @param delimiter
	 * @return
	 */
	public static String[] toStringArray(String string, String delimiter)
	{
		if ((string == null) || (delimiter == null))
			throw new IllegalArgumentException("String or Delimiter cannot be null");

		return toStringArray(string, delimiter, false);
	}

	/**
	 * Converts the given string into string array
	 * @param string
	 * @param delimiter
	 * @return
	 */
	public static String[] toStringArray(String string, String delimiter, boolean includeEmptyValues)
	{
		if ((string == null) || (delimiter == null))
			throw new IllegalArgumentException("String or Delimiter cannot be null");
		int limit = includeEmptyValues ? -1 : 0;

		return string.split(delimiter, limit);
	}

	public static String[] toStringArray(String string)
	{
		return toStringArray(string, SYMBOL_COMMA);
	}

	public static String[] getStringArray(int size) {
		if(size < 0) {
			throw new IllegalArgumentException("String or Delimiter cannot be null");
		}

		String[] array = new String[size];
		for(int i=0;i<size;i++) { array[i] = EMPTY_BLANK_STRING; };

		return array;
	}

	public static String arrayToString(String[] str) {
		return arrayToString(str, SYMBOL_COMMA);
	}

	public static String arrayToString(String[] str, String delimiter) {
		return arrayToString(str, delimiter, false);
	}

	public static String arrayToString(String[] str, String delimiter, boolean replaceNullString) {
		if ((str == null) || (delimiter == null))
			return "";

		StringBuilder strBuilder = new StringBuilder();
		for(int index = 0; index < str.length ; index++) {
			if(replaceNullString) { str[index] = isNullOrEmpty(str[index]) ? StringUtils.EMPTY_SPACE_STRING : str[index]; }

			strBuilder.append(str[index]);
			if( (index + 1) < str.length) {
				strBuilder.append(delimiter);
			}
		}
		return strBuilder.toString();
	}

	public static String arrayToString(String[] str, String delimiter, boolean excludeEmptyOrNull, boolean extra) {
		if ((str == null) || (delimiter == null))
			return "";

		StringBuilder strBuilder = new StringBuilder();
		for(int index = 0; index < str.length ; index++) {
			if(excludeEmptyOrNull && isNullOrEmpty(str[index])) { continue; }

			strBuilder.append(str[index]);
			if( (index + 1) < str.length) {
				strBuilder.append(delimiter);
			}
		}
		return strBuilder.toString();
	}



	public static String arrayToString(List<String> str, String delimiter) {
		if ((str == null) || (delimiter == null))
			throw new IllegalArgumentException("String array OR Delimiter cannot be null");

		StringBuilder strBuilder = new StringBuilder();
		for(String s : str){
			strBuilder.append(s);
			strBuilder.append(delimiter);
		}
		return strBuilder.toString();
	}

	public static boolean isNewline(char c) {
		String str = c+"";
		return (NEW_LINE.equals(str));
	}

	public static boolean isCarriageReturn(char c) {
		String str = c+"";
		return (CARRIAGE_RETURN.equals(str));
	}

	public static char toChar(String string)
	{
		if ((string == null) || (string.length() != 1))
			throw new IllegalArgumentException("String needs to have a single character");
		return string.charAt(0);
	}

	public static int toInt(String str) {
		return toInt(str, Integer.MIN_VALUE);
	}

	public static int toInt(String str, int defaultValue) {
		int value = 0;
		try {
			value  = Integer.valueOf(str);
		} catch(Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static String trimIt(String str) {
		if(str == null) {
			return "";
		}
		return str.trim();
	}

	public static boolean isAlphabet(char c) {
		int asciCode = (int)c;

		return ((asciCode >=65 && asciCode <= 90) || (asciCode >=97 && asciCode <=122));
	}

	public static String stringValueOf(Object str) {		
		String value = String.valueOf(str);
		return (value.equals("null") ? "" : value);
	}

	public static String toString(char c)
	{
		return new String(new char[] { c });
	}

	public static String capitalize(String target)
	{
		if (isNullOrEmpty(target))
			return null;

		return Character.toUpperCase(target.charAt(0)) + target.substring(1);
	}

	/**
	 * Check if two string arrays are equal or not.
	 * This method assumes that the data is in sorted order and so compares left ith element with right ith element
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean containEqualValues(String[] left, String[] right, boolean[] ignoreColumns) {
		boolean flag = true;

		if(left.length != right.length) {
			flag = false;
		} else {
			for(int i=0; i < left.length; i++) {
				if(ignoreColumns != null && ignoreColumns[i]) {
					continue;
				}

				if(!trimIt(left[i]).equals(trimIt(right[i]))) {
					flag = false;
					break;
				}
			}
		}

		return flag;
	}

	public static String formatTextWithNewLine(String[] content)
	{
		try
		{
			StringWriter sw = new StringWriter();
			BufferedWriter bf = new BufferedWriter(sw);
			for (int i = 0; i < content.length; i++) {
				bf.write(content[i]);
				bf.newLine();
			}
			bf.close();
			return sw.getBuffer().toString();
		}
		catch (IOException ioe) {}
		return "";
	}

	/**
	 * 
	 * @param patterns
	 * @param value
	 * @return
	 */
	public static List<String> applyPattern(char[] patterns, String value) {
		List<String> listOfValues = new ArrayList<String>();
		if(patterns == null || patterns.length == 0) {
			listOfValues.add(value);
		} else if(isNullOrEmpty(value)) {
			//do nothing
		} else {
	
			int beginIndex = 0;
			for(int index = 0; index < patterns.length; index++) {
				int endIndex = value.indexOf(patterns[index], beginIndex);
				String sbstr = value.substring(beginIndex, endIndex);
				if(!isNullOrEmpty(sbstr))
					listOfValues.add(sbstr);
				
				beginIndex = endIndex+1;
			}
			listOfValues.add(value.substring(beginIndex));
		}

		return listOfValues;
	}
	
	public static String toDelimiterSeparatedString(String[] array, String delimiter)
	{
		StringBuilder expression = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (!isNullOrEmpty(array[i])) {
				expression.append(array[i]);
				expression.append(delimiter);
			}
		}

		if (expression.length() > 2) expression.deleteCharAt(expression.length() - 1);
		return expression.toString();
	}

	public static boolean hasDigit(String s)
	{
		for (int i = 0; i < s.length(); i++) {
			if (isDigit(s.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean isDigit(char c) {
		return Character.isDigit(c);
	}


	public static HashMap<String, Integer> generateColumnIndexMap(final String[] header, String delimiter) {		
		return generateColumnIndexMap(arrayToString(header), delimiter);
	}

	/**
	 * Return the map containing the column header as key to the column index for the provided output file header
	 * @return
	 */
	public static HashMap<String, Integer> generateColumnIndexMap(final String header, String delimiter) {		

		HashMap<String, Integer> columnIndexMap = new HashMap<String, Integer>();

		if(StringUtils.isNullOrEmpty(header) ||  StringUtils.isNullOrEmpty(delimiter)) {
			throw new NullPointerException("Header or delimiter cannot be null");
		}

		String[] headerCols = header.split(delimiter);
		if(headerCols == null || headerCols.length == 0) {
			throw new IllegalArgumentException("Incorrect header " + headerCols);
		}

		for(int index=0; index < headerCols.length; index++) {
			columnIndexMap.put(headerCols[index], index);
		}

		return columnIndexMap;
	}

	public static HashMap<String, String> stringToKeyValueMap(String str, String delimeter, String keyValueDelimeter) {
		HashMap<String, String> map = new HashMap<String, String>();
		if(isNullOrEmpty(str) || isNullOrEmpty(delimeter) || isNullOrEmpty(keyValueDelimeter)) {
			return map;
		}

		String[] keyValueArrays = str.split(delimeter);
		for(String kv : keyValueArrays) {
			String[] keyValue = kv.split(keyValueDelimeter);
			if(keyValue.length == 2 && !StringUtils.isNullOrEmpty(keyValue[0])) { // length check and key null check 
				map.put(trimIt(keyValue[0]), trimIt(keyValue[1]));
			}
		}

		return map;
	}



	/**
	 * Check if two string arrays are equal or not for given column indexes.
	 * This method assumes that the data is in sorted order and so compares left ith element with right ith element where i is in given column index array.
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean containEqualValuesAtGivenColIndexes(String[] left, String[] right, int[] columnsToCompare) {
		boolean flag = true;

		if(left.length != right.length) {
			flag = false;
		} else {
			for(int i=0; i < columnsToCompare.length; i++) {

				if(!trimIt(left[columnsToCompare[i]]).equals(trimIt(right[columnsToCompare[i]]))) {
					flag = false;
					break;
				}
			}
		}

		return flag;
	}

	/**
	 * Utility which converts the list into a hashmap where the key is defined
	 * by keyColumn index and value would be an object array(list item)
	 * @param input
	 * @param keyColumn
	 * @return
	 */
	public static HashMap<String, Object[]> listToKeyValueMap(List<Object[]> input, int keyColumn) {
		return listToKeyValueMap(input, new int[]{keyColumn});
	}

	public static HashMap<String, Object[]> listToKeyValueMap(List<Object[]> input, int[] keyColumns) {				
		HashMap<String, Object[]> outputMap = new HashMap<String, Object[]>();

		for(int index = 0; index < input.size(); index++ ) {

			StringBuilder key = new StringBuilder();
			for(int keyColumn : keyColumns) {
				key.append(trimIt(stringValueOf((input.get(index))[keyColumn])));
			}

			outputMap.put(key.toString(), input.get(index));			
		}

		return outputMap;
	}

	/**
	 * onverts the list of values separated with delimiter represented as String to Set Colleciton
	 * @param list
	 * @param delimiter
	 * @return
	 */
	public static HashSet<String> listStringToSet(String list, String delimiter) {		
		HashSet<String> resultSet = new HashSet<String>();

		if(!(isNullOrEmpty(list) || isNullOrEmpty(delimiter))) {
			String[] values = list.split(delimiter, -1);
			for(String value : values) {
				resultSet.add(trimIt(value));
			}
		}

		return resultSet;
	}

	/**
	 * Converts the list of values represented as string to Set Colleciton
	 * Assumes the delimiter to be comma
	 * @param list
	 * @return
	 */
	public static HashSet<String> listStringToSet(String list) {
		return listStringToSet(list, SYMBOL_COMMA);
	}


	/**
	 * Utility which converts the list into a hashmap where the key is defined
	 * by keyColumn index and value would be the a list of object array.
	 * This function considers that the key can be mapped to multiple values.
	 * @param input
	 * @param keyColumn
	 * @return
	 */
	public static Map<String, List<Object[]>> listToKeyValueMapWithDupes(List<Object[]> input, int keyColumn) {
		HashMap<String, List<Object[]>> outputMap = new HashMap<String, List<Object[]>>();

		if(input != null) {
			for(int index = 0; index < input.size(); index++ ) {

				String key = stringValueOf((input.get(index))[keyColumn]);
				if(outputMap.get(key) == null) {
					List<Object[]> list = new ArrayList<Object[]>();							
					outputMap.put(stringValueOf((input.get(index))[keyColumn]), list);
				}

				outputMap.get(key).add(input.get(index));
			}
		}
		return outputMap;
	}

	

	public static Map<String, ArrayList<String>> stringToKeyValueMapWithDupes(String str, String delimiter, String keyValueDelimeter)
	{
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		if ((isNullOrEmpty(str)) || (isNullOrEmpty(delimiter))) {
			return map;
		}
		String[] keyValueArrays = str.split(delimiter);
		for (String kv : keyValueArrays)
		{
			ArrayList<String> valuesList = new ArrayList<String>();
			String[] keyValue = kv.split(keyValueDelimeter);
			if ((keyValue.length == 2) && (!isNullOrEmpty(keyValue[0])))
			{
				String key = trimIt(keyValue[0]);
				String value = trimIt(keyValue[1]);
				if (map.containsKey(key))
				{
					map.get(key).add(value);
				}
				else
				{
					valuesList.add(value);
					map.put(trimIt(keyValue[0]), valuesList);
				}
			}
		}
		return map;
	}

	public static String strListToString(List<String> str) {
		if(str == null || str.size() == 0) {
			return EMPTY_BLANK_STRING;
		}
		
		StringBuilder value = new StringBuilder();
		for(int i=0; i < str.size(); i++) {
			value.append(str.get(i));
		}
		
		return value.toString();
	}
	
	/**
	 * Joins two String arrays
	 * 
	 * @param one
	 * @param two
	 * @return new String Arrays containing values from both arrays
	 */
	public static String[] joinStringArrays(String[] one, String[] two) {

		String[] newArray = new String[one.length + two.length];
		System.arraycopy(one, 0, newArray, 0, one.length);
		System.arraycopy(two, 0, newArray, one.length, two.length);
		return newArray;
	}
	
	/**
	 * Check if the provided character is a lower case alphabet.
	 * @param ch
	 * @return
	 */
	public static boolean isLowerCase(char ch) {
		return ch>= 97 && ch<=122;
	}
	
	/**
	 * Check if the provided character is an upper case alphabet.
	 * @param ch
	 * @return
	 */
	public static boolean isUpperCase(char ch) {
		return ch>=65 && ch<=90;
	}
	
}