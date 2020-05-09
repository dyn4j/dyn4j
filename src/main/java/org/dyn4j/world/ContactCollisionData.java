package org.dyn4j.world;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;

public interface ContactCollisionData<T extends PhysicsBody> extends CollisionData<T, BodyFixture> {
	public ContactConstraint<T> getContactConstraint();
}
