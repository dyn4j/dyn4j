package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.BodyFixture;

public interface BroadphaseCollisionData<T extends CollisionBody<E>, E extends Fixture> {
	public CollisionPair<T, E> getPair();
	public T getBody1();
	public E getFixture1();
	public T getBody2();
	public E getFixture2();
}
