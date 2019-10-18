package com.jpaquery.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可合并的Map
 * 
 * @author lujijiang
 *
 * @param <K>
 * @param <V>
 */
public class _MergeMap<K, V> extends AbstractMap<K, V> {

	/**
	 * 其它引用的Map
	 */
	Set<Map<K, V>> others = Collections.synchronizedSet(new LinkedHashSet<>());
	/**
	 * 自身Map
	 */
	Map<K, V> own = Collections.synchronizedMap(new LinkedHashMap<>());

	@Override
	public Set<K> keySet() {
		Set<K> keys = new LinkedHashSet<>();
		keys.addAll(own.keySet());
		for (Map<K, V> other : others) {
			keys.addAll(other.keySet());
		}
		return keys;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {

		final Set<K> keys = keySet();

		return new AbstractSet<java.util.Map.Entry<K, V>>() {

			@Override
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				return new Iterator<java.util.Map.Entry<K, V>>() {

					Iterator<K> iterator = keys.iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public java.util.Map.Entry<K, V> next() {
						return new java.util.Map.Entry<K, V>() {

							K key = iterator.next();

							@Override
							public K getKey() {
								return key;
							}

							@Override
							public V getValue() {
								return get(key);
							}

							@Override
							public V setValue(V value) {
								throw new UnsupportedOperationException();
							}
						};
					}
				};
			}

			@Override
			public int size() {
				return keys.size();
			}
		};
	}

	@Override
	public V put(K key, V value) {
		V oldValue = null;
		for (Map<K, V> other : others) {
			if (other.containsKey(key)) {
				oldValue = other.put(key, value);
			}
		}
		if (own.containsKey(key)) {
			oldValue = own.get(key);
		}
		own.put(key, value);
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		others.add((Map<K, V>) m);
	}

	@Override
	public boolean containsKey(Object key) {
		if (own.containsKey(key)) {
			return true;
		}
		for (Map<K, V> other : others) {
			if (other.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		if (own.containsKey(key)) {
			return own.get(key);
		}
		for (Map<K, V> other : others) {
			if (other.containsKey(key)) {
				return other.get(key);
			}
		}
		return null;
	}

	@Override
	public V remove(Object key) {
		V value = null;
		for (Map<K, V> other : others) {
			if (other.containsKey(key)) {
				value = other.remove(key);
			}
		}
		if (own.containsKey(key)) {
			value = own.remove(key);
		}
		return value;
	}
}
