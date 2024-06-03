package org.uwindsor.mac.acc.drivedepot.exception;

import org.uwindsor.mac.acc.drivedepot.webcrawler.UserMenu;

/**
 * 
 * This is exclusively used in {@link UserMenu} class
 * to differentiate between exit option vs any other exception.
 * @author Anuj Puri (110120950)
 *
 */
public class ExitException extends Exception {

	public ExitException(String e) {
		super(e);
	}
}
