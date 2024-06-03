package org.uwindsor.mac.acc.drivedepot.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading the properties from the configuration
 * file placed on the file system. This class is defined as final 
 * and the constructor is declared as private. It contains static methods.
 * 
 * @author 110120950(Anuj Puri)
 *
 */
public final class ConfigUtil {

	/**
	 * Contains the all the properties keys and corresponding values.
	 */
	private static Properties properties = null;

	/*
	 * private constuctor
	 */
	private ConfigUtil () {

	}

	/**
	 * 
	 * @param propertiesFile
	 * @param properties
	 */
	public static void loadProperties(File propertiesFile, Properties properties) {
		try {
			FileReader reader = new FileReader(propertiesFile);  

			properties = new Properties();  			
			properties.load(reader);
			ConfigUtil.properties = properties;
		} catch(IOException ioex) {
			System.out.println(" unable to load properties ... ");
			ConfigUtil.properties = new Properties();			
		}
	}
	
	/**
	 * Get the value of the property stored against the propertyKey
	 * @param propertyKey associated with the value stored in Properties
	 * @return
	 */
	public static String getpropertyValue(String propertyKey) {		
		return properties.getProperty(propertyKey);
	}
	
	/**
	 * Get the value of the property stored against the propertyKey
	 * 
	 * If no value stored against the provided property key, 
	 * defaultValue will be returned.
	 * 
 	 * @param propertyKey associated with the value stored in Properties.
 	 * @param defaultValue return in case not value stored against propertyKey
	 * @return
	 */
	public static String getPropertyValue(String propertyKey, String defaultValue) {
		return properties.getProperty(propertyKey, defaultValue);
	}
}
