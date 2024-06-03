package org.uwindsor.mac.acc.drivedepot.webcrawler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.uwindsor.mac.acc.drivedepot.constants.Constants;
import org.uwindsor.mac.acc.drivedepot.htmlparser.impl.HTMLParser;
import org.uwindsor.mac.acc.drivedepot.util.ConfigUtil;
import org.uwindsor.mac.acc.drivedepot.util.IOUtils;

/**
 * 
 * @author Abdul Rahman Mohammed(110128321)
 *
 */
public class WebCrawler {
    private static final Scanner scanner = new Scanner(System.in);

	static {
		File propertiesFile;
		try {
			propertiesFile = IOUtils.getFile(HTMLParser.PATH_PROPERTIES_FILE);
			ConfigUtil.loadProperties(propertiesFile, new Properties());
	        System.setProperty("webdriver.chrome.driver", ConfigUtil.getpropertyValue(Constants.KEY_PATH_CHROME_WEBDRIVER));
		} catch (IOException e) {
			System.err.println("Error Occurred while loading properties.");
		}
	}
	
	public String getUserInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine().toLowerCase();
    }

	public int getUserInputInt(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

	public static void main(String[] args) throws Exception {
		
//		LocationsServiced startDriveDepot = new LocationsServiced();
//		startDriveDepot.driveDepotMain();
		
		UserMenu menu = new UserMenu();
		menu.init();
		
	}

}
