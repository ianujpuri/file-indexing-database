package org.uwindsor.driverdepot;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.HTMLParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;

@SpringBootApplication
public class DriverDepotApplication {

	static {
		File propertiesFile;
		try {
			propertiesFile = IOUtils.getFile(HTMLParser.PATH_PROPERTIES_FILE);
			ConfigUtil.loadProperties(propertiesFile, new Properties());
		} catch (IOException e) {
			System.err.println("Error Occurred while loading properties.");
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DriverDepotApplication.class, args);
	}

}
