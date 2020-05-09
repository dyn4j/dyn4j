package org.dyn4j.world.result;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;

public class RaycastResult<T extends CollisionBody<E>, E extends Fixture> extends DetectResult<T, E> implements Comparable<RaycastResult<T, E>> {
	protected Raycast raycast;
	
	public RaycastResult() {
		
	}
	
	public RaycastResult(T body, E fixture, Raycast raycast) {
		super(body, fixture);
		this.raycast = raycast;
	}

	public Raycast getRaycast() {
		return this.raycast;
	}
	
	public void setRaycast(Raycast raycast) {
		this.raycast = raycast;
	}

	@Override
	public int compareTo(RaycastResult<T, E> o) {
		return (int)Math.signum(this.raycast.getDistance() - o.raycast.getDistance());
	}
	
	public RaycastResult<T, E> copy() {
		return new RaycastResult<T, E>(this.body, this.fixture, this.raycast);
	}
}
