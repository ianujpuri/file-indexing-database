package org.uwindsor.mac.acc.drivedepot.model;

import java.util.HashMap;

import org.uwindsor.mac.acc.drivedepot.util.StringUtils;


/**
 * Wrapper around the {@link HashMap} to handle the 
 * case insensitivity for the inserted keys.
 * @author 110120950 (AP)
 *
 * @param <K> String type keys
 * @param <V>
 */
public class IgnoreCaseMap<K, V> extends HashMap<String , V> {

	private static final long serialVersionUID = 8026065232972746597L;

	@Override
	public V put(String key, V value) {
		if(!StringUtils.isNullOrEmpty(key)) {
			key = key.toLowerCase();
		}
		return super.put(key, value);
	}

	@Override
	public V get(Object key) {
		if(key instanceof String && !StringUtils.isNullOrEmpty((String)key)) {
			key = ((String)key).toLowerCase();
		}
		return super.get(key);
	}
}
