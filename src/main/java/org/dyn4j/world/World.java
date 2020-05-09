package org.dyn4j.world;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

public class World extends AbstractPhysicsWorld<Body, WorldCollisionData<Body>> {

	public World() {
		this(CollisionWorld.DEFAULT_BODY_COUNT);
	}
	
	public World(int initialBodyCapacity) {
		super(initialBodyCapacity);
	}
	
	@Override
	protected WorldCollisionData<Body> createCollisionData(CollisionPair<Body, BodyFixture> pair) {
		return new WorldCollisionData<Body>(pair);
	}
}
