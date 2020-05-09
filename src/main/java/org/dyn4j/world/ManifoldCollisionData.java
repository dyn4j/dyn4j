package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.manifold.Manifold;

public interface ManifoldCollisionData<T extends CollisionBody<E>, E extends Fixture> extends NarrowphaseCollisionData<T, E> {
	public Manifold getManifold();
}
