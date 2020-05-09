package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.Vector2;

public interface NarrowphaseCollisionData<T extends CollisionBody<E>, E extends Fixture> extends BroadphaseCollisionData<T, E> {
	public Penetration getPenetration();
	
	// TODO maybe...?
	public Vector2 getSeparationNormal();
}
