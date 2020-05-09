package org.dyn4j.world.result;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.narrowphase.Penetration;

public class ConvexDetectResult<T extends CollisionBody<E>, E extends Fixture> extends DetectResult<T, E> {
	protected Penetration penetration;
	
	public ConvexDetectResult() {
		
	}
	
	public ConvexDetectResult(T body, E fixture, Penetration penetration) {
		super(body, fixture);
		this.penetration = penetration;
	}

	public Penetration getPenetration() {
		return this.penetration;
	}
	
	public void setPenetration(Penetration penetration) {
		this.penetration = penetration;
	}

	public ConvexDetectResult<T, E> copy() {
		return new ConvexDetectResult<T, E>(this.body, this.fixture, this.penetration);
	}
}
