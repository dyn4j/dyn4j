package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.Shiftable;

public interface CollisionData<T extends CollisionBody<E>, E extends Fixture> extends ManifoldCollisionData<T, E>, NarrowphaseCollisionData<T, E>, BroadphaseCollisionData<T, E>, Shiftable {
	public boolean isBroadphaseCollision();
	public void setBroadphaseCollision(boolean flag);
	
	public boolean isNarrowphaseCollision();
	public void setNarrowphaseCollision(boolean flag);
	
	public boolean isManifoldCollision();
	public void setManifoldCollision(boolean flag);
	
	public void reset();
}
