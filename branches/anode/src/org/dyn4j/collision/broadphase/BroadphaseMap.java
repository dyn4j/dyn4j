package org.dyn4j.collision.broadphase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;

public class BroadphaseMap<E> {
	protected Map<Collidable<?>, Map<Fixture, E>> storage;
	
	public BroadphaseMap() {
		this(10);
	}
	
	public BroadphaseMap(int initialCapacity) {
		this.storage = new LinkedHashMap<Collidable<?>, Map<Fixture, E>>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	public E get(Collidable<?> key, Fixture subkey) {
		Map<Fixture, E> map = this.storage.get(key);
		if (map != null) {
			return map.get(subkey.getId());
		}
		return null;
	}
	
	public void put(Collidable<?> key, Fixture subkey, E value) {
		Map<Fixture, E> map = this.storage.get(key);
		if (map == null) {
			map = new LinkedHashMap<Fixture, E>();
			this.storage.put(key, map);
		}
		map.put(subkey, value);
	}
	
	public E remove(Collidable key, Fixture subkey) {
		Map<Fixture, E> map = this.storage.get(key);
		if (map != null) {
			return map.remove(subkey);
		}
		return null;
	}
	
	public Map<Fixture, E> remove(Collidable key) {
		return this.storage.remove(key);
	}
	
	public boolean contains(Collidable key) {
		return this.storage.containsKey(key);
	}
	
	public boolean contains(Collidable key, Fixture subkey) {
		Map<Fixture, E> map = this.storage.get(key);
		if (map != null) {
			return map.containsKey(subkey);
		}
		return false;
	}
	
	public int size() {
		return this.storage.size();
	}
	
	public int size(Collidable key) {
		Map<Fixture, E> map = this.storage.get(key);
		if (map != null) {
			return map.size();
		}
		return 0;
	}
	
	public void clear() {
		this.storage.clear();
	}
	
	public Iterator<E> iterator() {
		return new BroadphaseMapIterator<E>(this);
	}
	
	public List<E> toList() {
		List<E> items = new ArrayList<E>();
		Iterator<E> iterator = this.iterator();
		while (iterator.hasNext()) {
			items.add(iterator.next());
		}
		return items;
	}
}
