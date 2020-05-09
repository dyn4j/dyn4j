package org.dyn4j.world;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.Joint;

public final class InteractionGraphNode<T extends PhysicsBody> {
	protected final T body;
	protected final List<ContactConstraint<T>> contacts;
	protected final List<Joint<T>> joints;
	
	public InteractionGraphNode(T body) {
		this.body = body;
		this.contacts = new ArrayList<ContactConstraint<T>>();
		this.joints = new ArrayList<Joint<T>>();
	}
}
