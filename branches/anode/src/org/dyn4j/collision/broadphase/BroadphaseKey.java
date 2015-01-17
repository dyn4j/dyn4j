package org.dyn4j.collision.broadphase;

import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;

public class BroadphaseKey {
	public final UUID collidable;
	public final UUID fixture;
	
	public BroadphaseKey(UUID collidable, UUID fixture) {
		this.collidable = collidable;
		this.fixture = fixture;
	}
	
	public static BroadphaseKey get(Collidable<?> collidable, Fixture fixture) {
		return new BroadphaseKey(collidable.getId(), fixture.getId());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return false;
		if (obj instanceof BroadphaseKey) {
			BroadphaseKey key = (BroadphaseKey)obj;
			return key.collidable.equals(this.collidable) &&
				   key.fixture.equals(this.fixture);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.collidable.hashCode();
		hash = hash * 31 + this.fixture.hashCode();
		return hash;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
