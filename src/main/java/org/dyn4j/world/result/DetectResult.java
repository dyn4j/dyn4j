package org.dyn4j.world.result;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;

public class DetectResult<T extends CollisionBody<E>, E extends Fixture> {
	protected T body;
	protected E fixture;
	
	public DetectResult() {
		
	}
	
	public DetectResult(T body, E fixture) {
		this.body = body;
		this.fixture = fixture;
	}
	
	public T getBody() {
		return this.body;
	}
	
	public E getFixture() {
		return this.fixture;
	}
	
	public void setBody(T body) {
		this.body = body;
	}

	public void setFixture(E fixture) {
		this.fixture = fixture;
	}

	public DetectResult<T, E> copy() {
		return new DetectResult<T, E>(this.body, this.fixture);
	}
}
