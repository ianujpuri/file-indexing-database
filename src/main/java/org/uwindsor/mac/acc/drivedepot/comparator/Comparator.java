package org.uwindsor.mac.acc.drivedepot.comparator;

/**
 * 
 * @author Anuj Puri (110120950)
 *
 * @param <T>
 */
public interface Comparator<T> {
	
	public boolean compare(T obj1, T obj2);
	
}
