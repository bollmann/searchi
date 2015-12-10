package utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	public static final int CACHE_SIZE = 100;

	public LRUCache() {
		super(CACHE_SIZE);
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() >= CACHE_SIZE;
	}
}
