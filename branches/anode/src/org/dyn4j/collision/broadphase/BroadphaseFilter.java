package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;

public interface BroadphaseFilter<E extends Collidable<T>, T extends Fixture> {
	public abstract boolean isAllowed(E collidable1, T fixture1, E collidable2, T fixture2);
}
