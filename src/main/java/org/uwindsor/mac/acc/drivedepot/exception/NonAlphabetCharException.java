package org.uwindsor.mac.acc.drivedepot.exception;

/**
 * 
 * @author Anuj Puri (110120950)
 *
 */
public class NonAlphabetCharException extends Exception {

	@Override
	public String getMessage() {
		return "Invalid character, not in scope of [a-z] + [A-Z]";
	}
}
