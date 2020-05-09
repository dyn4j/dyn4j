package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Vector2;

public class WorldCollisionData<T extends PhysicsBody> implements ContactCollisionData<T> {
	private boolean isBroadphaseCollision;
	private boolean isNarrowphaseCollision;
	private boolean isManifoldCollision;
	
	private final CollisionPair<T, BodyFixture> pair;
	private final Penetration penetration;
	private final Manifold manifold;
	private final ContactConstraint<T> contactConstraint;
	
	
	public WorldCollisionData(CollisionPair<T, BodyFixture> pair) {
		this.pair = pair;
		this.penetration = new Penetration();
		this.manifold = new Manifold();
		this.contactConstraint = new ContactConstraint<T>(pair);
		
		this.isBroadphaseCollision = false;
		this.isNarrowphaseCollision = false;
		this.isManifoldCollision = false;
	}
	
	@Override
	public CollisionPair<T, BodyFixture> getPair() {
		return this.pair;
	}
	
	@Override
	public T getBody1() {
		return this.pair.getBody1();
	}

	@Override
	public BodyFixture getFixture1() {
		return this.pair.getFixture1();
	}

	@Override
	public T getBody2() {
		return this.pair.getBody2();
	}

	@Override
	public BodyFixture getFixture2() {
		return this.pair.getFixture2();
	}

	@Override
	public void shift(Vector2 shift) {
		this.manifold.shift(shift);
	}

	@Override
	public Manifold getManifold() {
		return this.manifold;
	}

	@Override
	public Penetration getPenetration() {
		return this.penetration;
	}

	@Override
	public Vector2 getSeparationNormal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContactConstraint<T> getContactConstraint() {
		return this.contactConstraint;
	}

	@Override
	public boolean isBroadphaseCollision() {
		return this.isBroadphaseCollision;
	}
	
	@Override
	public void setBroadphaseCollision(boolean flag) {
		this.isBroadphaseCollision = flag;
	}
	
	@Override
	public boolean isNarrowphaseCollision() {
		return this.isNarrowphaseCollision;
	}

	@Override
	public void setNarrowphaseCollision(boolean flag) {
		this.isNarrowphaseCollision = flag;
	}

	@Override
	public boolean isManifoldCollision() {
		return this.isManifoldCollision;
	}

	@Override
	public void setManifoldCollision(boolean flag) {
		this.isManifoldCollision = flag;
	}

	@Override
	public void reset() {
		this.isBroadphaseCollision = false;
		this.isNarrowphaseCollision = false;
		this.isManifoldCollision = false;
		this.penetration.clear();
		this.manifold.clear();
		// don't clear the contact constraint because we need to report
		// ending of contacts when it happens
	}
}
