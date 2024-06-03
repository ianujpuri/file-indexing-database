package org.uwindsor.mac.acc.drivedepot.indextable;

import org.uwindsor.mac.acc.drivedepot.invertedindex.dsstore.Trie;
import org.uwindsor.mac.acc.drivedepot.model.IndexTable;

/**
 * 
 * @author Anuj Puri (110120950)
 *
 */
/*
 * This class is a hack to control the usage on single indexTable
 * across the application as well the use for Trie.
 * TODO: Need to find better alternative.
 */
public final class IndexTableProvider {

	
	//Location+Make+Type
	private static final IndexTable<Object, Object> APP_INDEX_TABLE = new IndexTable<>();
	private static final Trie TRIE_STORE = new Trie();
	
	private IndexTableProvider() {
		//cannot instantiate this class
	}	
	
	public static IndexTable<Object, Object> getAppIndexTable() {
		return APP_INDEX_TABLE;
	}
	
	//get the trie store for the entire application
	public static Trie getTrie() {
		return TRIE_STORE;
	}

}
