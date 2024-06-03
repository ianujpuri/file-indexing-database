package org.uwindsor.mac.accdrivedepot.changelistener;

/**
 * 
 * Abstraction to implement change listener for files.
 * @author Anuj Puri(110120950)
 *
 * @param <T>
 */
public interface ChangeListener<T> {

	void onChange(T data);
}
