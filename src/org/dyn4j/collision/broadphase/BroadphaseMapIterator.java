package org.dyn4j.collision.broadphase;

import java.util.Iterator;
import java.util.Map;

import org.dyn4j.collision.Fixture;

public class BroadphaseMapIterator<E> implements Iterator<E> {
	protected BroadphaseMap<E> map;
	protected Iterator<Map<Fixture, E>> rootIterator;
	protected Iterator<E> iterator;
	
	public BroadphaseMapIterator(BroadphaseMap<E> map) {
		this.map = map;
		this.rootIterator = map.storage.values().iterator();
	}
	
	@Override
	public boolean hasNext() {
		return rootIterator.hasNext();
	}
	
	@Override
	public E next() {
		if (this.iterator == null && this.rootIterator.hasNext()) {
			this.iterator = this.rootIterator.next().values().iterator();
		}
		while (!this.iterator.hasNext() && this.rootIterator.hasNext()) {
			this.iterator = this.rootIterator.next().values().iterator();
		}
		return this.iterator.next();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
