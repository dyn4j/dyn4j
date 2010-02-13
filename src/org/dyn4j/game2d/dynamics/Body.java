/*
 * Copyright (c) 2010, William Bittle
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.game2d.dynamics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.Filter;
import org.dyn4j.game2d.dynamics.contact.ContactEdge;
import org.dyn4j.game2d.dynamics.joint.JointEdge;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Transformable;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents some physical {@link Body}.
 * <p>
 * A {@link Body} requires that at least one {@link Shape} represent it, but 
 * allows any number of {@link Shape}s.  The {@link Shape} list can be used 
 * to create convex or concave {@link Body}s.
 * <p>
 * The {@link Mass} should be the {@link Mass} of all the {@link Shape}s in the 
 * geometry list.  See {@link Mass} for methods to create {@link Mass} objects 
 * from {@link Shape}s.
 * <p>
 * The coefficient of friction and restitution and linear and angular damping
 * are all defaulted but can be changed via the accessor and mutator methods.
 * <p>
 * By default {@link Body}s are allowed to sleep. {@link Body}s are put to sleep
 * when they come to rest for a certain amount of time.  Applying any force,
 * torque, or impulse will wake the {@link Body}.
 * <p>
 * A {@link Body} becomes frozen when the {@link Body} has left the boundary of
 * the world.
 * <p>
 * A {@link Body} is dynamic if either its inertia or mass is greater than zero.
 * A {@link Body} is static if both its inertia and mass are zero.
 * <p>
 * A {@link Body} that is a sensor will not be handled in the collision
 * resolution but is handled in collision detection.
 * @author William Bittle
 */
public class Body implements Collidable, Transformable {
	/** The default coefficient of friction; value = {@value #DEFAULT_MU} */
	public static final double DEFAULT_MU = 0.2;
	
	/** The default coefficient of restitution; value = {@value #DEFAULT_E} */
	public static final double DEFAULT_E = 0.0;
	
	/** The default linear damping; value = {@value #DEFAULT_LINEAR_DAMPING} */
	public static final double DEFAULT_LINEAR_DAMPING = 0.0;
	
	/** The default angular damping; value = {@value #DEFAULT_ANGULAR_DAMPING} */
	public static final double DEFAULT_ANGULAR_DAMPING 	= 0.01;
	
	/** The state flag for allowing sleeping */
	protected static final int SLEEP = 1;
	
	/** The state flag for the {@link Body} being asleep */
	protected static final int ASLEEP = 2;
	
	/** The state flag for the {@link Body} being frozen (out of bounds) */
	protected static final int FROZEN = 4;

	/** The state flag for the {@link Body} being a sensor */
	protected static final int SENSOR = 8;
	
	/** The state flag indicating the {@link Body} has been added to an {@link Island} */
	protected static final int ISLAND = 16;
	
	/** The {@link Body}'s unique identifier */
	protected String id;
	
	/** The current {@link Transform} */
	protected Transform transform;

	/** The {@link Body}'s {@link Shape} list */
	protected List<Convex> shapes;
	
	/** The user data associated to this {@link Body} */
	protected Object userData;
	
	/** The {@link Body}'s collision {@link Filter} */
	protected Filter filter;
	
	/** The {@link Mass} information */
	protected Mass mass;
	
	/** The current linear velocity */
	protected Vector v;

	/** The current angular velocity */
	protected double av;

	/** The current force */
	protected Vector force;
	
	/** The current torque */
	protected double torque;
	
	/** The force accumulator */
	protected List<Force> forces;
	
	/** The torque accumulator */
	protected List<Torque> torques;
	
	/** The coefficient of friction */
	protected double mu;
	
	/** The coefficient of restitution */
	protected double e;

	/** The {@link Body}'s state */
	protected int state;
	
	/** The time that the {@link Body} has been waiting to be put sleep */
	protected double sleepTime;

	/** The {@link Body}'s linear damping */
	protected double linearDamping;
	
	/** The {@link Body}'s angular damping */
	protected double angularDamping;
	
	/** The {@link Body}'s contacts */
	protected List<ContactEdge> contacts;
	
	/** The {@link Body}'s joints */
	protected List<JointEdge> joints;
	
	/**
	 * Full constructor.
	 * @param shape the {@link Convex} {@link Shape}
	 * @param mass the {@link Mass}
	 */
	public Body(Convex shape, Mass mass) {
		super();
		// check the geometry
		if (shape == null) {
			throw new IllegalArgumentException("The shape cannot be null.");
		}
		// check the mass
		if (mass == null) {
			throw new IllegalArgumentException("The mass cannot be null.");
		}
		this.shapes = new ArrayList<Convex>(1);
		this.shapes.add(shape);
		this.mass = mass;
		this.initialize();
	}
	
	/**
	 * Full constructor.
	 * @param shapes the {@link Convex} {@link Shape} list; at least one {@link Convex} {@link Shape}
	 * @param mass the {@link Mass}; the {@link Mass} of all the {@link Shape}s
	 */
	public Body(List<Convex> shapes, Mass mass) {
		super();
		// check the geometry
		if (shapes == null || shapes.size() == 0) {
			throw new IllegalArgumentException("You must supply at least one shape.");
		}
		// check the mass
		if (mass == null) {
			throw new IllegalArgumentException("The mass cannot be null.");
		}
		this.shapes = shapes;
		this.mass = mass;
		this.initialize();
	}
	
	/**
	 * Initializes all fields not set in the constructor.
	 */
	private void initialize() {
		this.id = UUID.randomUUID().toString();
		this.transform = new Transform();
		this.filter = Filter.DEFAULT_FILTER;
		this.v = new Vector();
		this.av = 0.0;
		this.force = new Vector();
		this.torque = 0.0;
		this.forces = new ArrayList<Force>();
		this.torques = new ArrayList<Torque>();
		this.mu = Body.DEFAULT_MU;
		this.e = Body.DEFAULT_E;
		// initialize the state
		this.state = 0;
		// allow sleeping
		this.state |= Body.SLEEP;
		this.sleepTime = 0.0;
		this.linearDamping = Body.DEFAULT_LINEAR_DAMPING;
		this.angularDamping = Body.DEFAULT_ANGULAR_DAMPING;
		this.contacts = new ArrayList<ContactEdge>();
		this.joints = new ArrayList<JointEdge>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BODY[").append(id).append("|SHAPES{");
		// append all the shapes
		int size = this.shapes.size();
		for (int i = 0; i < size; i++) {
			if (i != 0) sb.append("|");
			sb.append(shapes.get(i));
		}
		sb.append("}|").append("TRANSFORM[").append(transform).append("]")
		.append("|").append(mass).append("|VELOCITY[").append(v).append("]")
		.append("|").append("ANGULAR_VELOCITY[").append(av).append("]")
		.append("|").append("FORCE[").append(force).append("]")
		.append("|").append("TORQUE[").append(torque).append("]")
		.append("|").append("MU[").append(mu).append("]")
		.append("|").append("E[").append(e).append("]")
		.append("|").append("STATE[").append(state).append("]")
		.append("|").append("LINEAR_DAMPING[").append(linearDamping).append("]")
		.append("|").append("ANGULAR_DAMPING[").append(angularDamping).append("]")
		.append("|").append("SLEEP_TIME[").append(sleepTime).append("]");
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Applies the given force to this {@link Body}.
	 * @param force the force
	 */
	public void apply(Vector force) {
		this.apply(new Force(force));
	}
	
	/**
	 * Applies the given {@link Force} to this {@link Body}
	 * @param force the force
	 */
	public void apply(Force force) {
		this.forces.add(force);
	}

	/**
	 * Applies the given force to this {@link Body} at the
	 * given point (torque).
	 * @param force the force
	 * @param point the application point in world coordinates
	 */
	public void apply(Vector force, Vector point) {
		this.apply(new Torque(force, point));
	}
	
	/**
	 * Applies the given {@link Torque} to this {@link Body}.
	 * @param torque the torque
	 */
	public void apply(Torque torque) {
		this.torques.add(torque);
	}
	
	/**
	 * Clears the last time step's force on the {@link Body}.
	 */
	public void clearForce() {
		this.force.zero();
	}
	
	/**
	 * Clears the forces stored in the force accumulator.
	 */
	public void clearForces() {
		this.forces.clear();
	}
	
	/**
	 * Clears the last time step's torque on the {@link Body}.
	 */
	public void clearTorque() {
		this.torque = 0.0;
	}
	
	/**
	 * Clears the torques stored in the torque accumulator.
	 */
	public void clearTorques() {
		this.torques.clear();
	}
	
	/**
	 * Accumulates the forces and torques.
	 */
	public void accumulate() {
		// set the current force to zero
		this.force.zero();
		// get the number of forces
		int size = this.forces.size();
		// check if the size is greater than zero
		if (size > 0) {
			// apply all the forces
			for (int i = 0; i < size; i++) {
				Force force = this.forces.get(i);
				force.apply(this);
			}
			// wake the body up
			this.awaken();
		}
		// remove the forces from the accumulator
		this.forces.clear();
		// set the current torque to zero
		this.torque = 0.0;
		// get the number of torques
		size = this.torques.size();
		// check the size
		if (size > 0) {
			// apply all the torques
			for (int i = 0; i < size; i++) {
				Torque torque = this.torques.get(i);
				torque.apply(this);
			}
			// wake the body up
			this.awaken();
		}
		// remove the torques from the accumulator
		this.torques.clear();
	}
	
	/**
	 * Returns true if both inertia and mass are infinite.
	 * @return boolean
	 */
	public boolean isStatic() {
		return this.mass.isInfinite();
	}
	
	/**
	 * Returns true if this body is dynamic.
	 * @return boolean
	 */
	public boolean isDynamic() {
		return !this.isStatic();
	}
	
	/**
	 * Sets the {@link Body} to allow or disallow sleeping.
	 * @param flag true if the {@link Body} is allowed to sleep
	 */
	public void setSleep(boolean flag) {
		// see if the body can already sleep
		if (this.canSleep()) {
			// if it can and the user doesn't want it to then
			// remove the state, otherwise do nothing
			if (!flag) {
				this.state ^= Body.SLEEP;
				this.awaken();
			}
		} else {
			// if it cannot sleep and the user does want it to
			// then add it to the state, otherwise do nothing
			if (flag) {
				this.state |= Body.SLEEP;
			}
		}
	}
	
	/**
	 * Returns true if this {@link Body} is allowed to sleep.
	 * @return boolean
	 */
	public boolean canSleep() {
		return (this.state & Body.SLEEP) == Body.SLEEP;
	}
	
	/**
	 * Returns true if this {@link Body} is sleeping.
	 * @return boolean
	 */
	public boolean isAsleep() {
		return (this.state & Body.ASLEEP) == Body.ASLEEP;
	}
	
	/**
	 * Sets this {@link Body} to sleep.
	 */
	public void sleep() {
		// make sure we are allowed to sleep it
		if (this.canSleep()) {
			this.state |= Body.ASLEEP;
			this.v.zero();
			this.av = 0.0;
			this.clearForce();
			this.clearTorque();
		}
	}
	
	/**
	 * Returns the duration the body has been attempting to sleep.
	 * <p>
	 * Once a body is asleep the sleep time is reset to zero.
	 * @return double
	 */
	public double getSleepTime() {
		return this.sleepTime;
	}
	
	/**
	 * Increments the sleep time for the {@link Body}.
	 * @param dt the increment
	 */
	protected void incrementSleepTime(double dt) {
		// only increment the sleep time if the body is not
		// already asleep
		if (!this.isAsleep()) {
			this.sleepTime += dt;
		}
	}
	
	/**
	 * Wakes up this {@link Body} from sleeping.
	 */
	public void awaken() {
		this.sleepTime = 0.0;
		this.state &= ~Body.ASLEEP;
	}
	
	/**
	 * Returns true if this {@link Body} is frozen.
	 * @return boolean
	 */
	public boolean isFrozen() {
		return (this.state & Body.FROZEN) == Body.FROZEN;
	}
	
	/**
	 * Freezes the {@link Body}.
	 */
	public void freeze() {
		this.state |= Body.FROZEN;
		this.v.zero();
		this.av = 0.0;
		this.force.zero();
		this.torque = 0.0;
	}
	
	/**
	 * Un-freezes the {@link Body}.
	 */
	public void thaw() {
		this.state &= ~Body.FROZEN;
	}

	/**
	 * Returns true if the {@link Body} is a sensor.
	 * @return boolean true if the {@link Body} is a sensor
	 */
	public boolean isSensor() {
		return (this.state & Body.SENSOR) == Body.SENSOR;
	}
	
	/**
	 * Sets the {@link Body} to be a sensor if given true.
	 * @param flag if true the {@link Body} will be a sensor
	 */
	public void setSensor(boolean flag) {
		if (flag) {
			this.state |= Body.SENSOR;
		} else {
			this.state &= ~Body.SENSOR;
		}
	}
	
	/**
	 * Returns true if this {@link Body} has been added to an {@link Island}.
	 * @return boolean true if this {@link Body} has been added to an {@link Island} 
	 */
	protected boolean onIsland() {
		return (this.state & Body.ISLAND) == Body.ISLAND;
	}
	
	/**
	 * Sets the flag indicating that the {@link Body} has been added to an {@link Island}.
	 * @param flag true if the {@link Body} has been added to an {@link Island}
	 */
	protected void setIsland(boolean flag) {
		if (flag) {
			this.state |= Body.ISLAND;
		} else {
			this.state &= ~Body.ISLAND;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector point) {
		this.transform.rotate(theta, point);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void translate(Vector vector) {
		this.transform.translate(vector);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		return this.shapes;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getTransform()
	 */
	public Transform getTransform() {
		return this.transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return this.filter;
	}
	
	/**
	 * Sets the collision {@link Filter} for this {@link Body}.
	 * @param filter the collision {@link Filter}
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	/**
	 * Returns the user data associated to this {@link Body}.
	 * @return Object
	 */
	public Object getUserData() {
		return userData;
	}
	
	/**
	 * Sets the user data associated to this {@link Body}.
	 * @param userData the user data object
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/**
	 * Returns the unique identifier for this body instance.
	 * @return String
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Returns this {@link Body}'s mass information.
	 * @return {@link Mass}
	 */
	public Mass getMass() {
		return mass;
	}
	
	/**
	 * Sets this {@link Body}'s mass information.
	 * @param mass the new {@link Mass}
	 */
	public void setMass(Mass mass) {
		this.mass = mass;
	}

	/**
	 * Returns the center of mass for the body in local coordinates.
	 * @return {@link Vector} the center of mass in local coordinates
	 */
	public Vector getLocalCenter() {
		return this.mass.c;
	}
	
	/**
	 * Returns the center of mass for the body in world coordinates.
	 * @return {@link Vector} the center of mass in world coordinates
	 */
	public Vector getWorldCenter() {
		return this.transform.getTransformed(this.mass.c);
	}
	
	/**
	 * Returns a new point in local coordinates of this body given
	 * a point in world coordinates.
	 * @param worldPoint a world space point
	 * @return {@link Vector} local space point
	 */
	public Vector getLocalPoint(Vector worldPoint) {
		return this.transform.getInverseTransformed(worldPoint);
	}
	
	/**
	 * Returns a new point in world coordinates given a point in the
	 * local coordinates of this {@link Body}.
	 * @param localPoint a point in the local coordinates of this {@link Body}
	 * @return {@link Vector} world space point
	 */
	public Vector getWorldPoint(Vector localPoint) {
		return this.transform.getTransformed(localPoint);
	}
	
	/**
	 * Returns the velocity {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector getV() {
		return v;
	}
	
	/**
	 * Sets the velocity {@link Vector}.
	 * @param v the velocity
	 */
	public void setV(Vector v) {
		this.v = v;
	}

	/**
	 * Returns the angular velocity.
	 * @return double
	 */
	public double getAv() {
		return av;
	}

	/**
	 * Sets the angular velocity.
	 * @param av the angular velocity
	 */
	public void setAv(double av) {
		this.av = av;
	}
	
	/**
	 * Return the force {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector getForce() {
		return force;
	}

	/**
	 * Returns the torque.
	 * @return double
	 */
	public double getTorque() {
		return torque;
	}

	/**
	 * Returns the coefficient of friction.
	 * @return double
	 */
	public double getMu() {
		return mu;
	}

	/**
	 * Sets the coefficient of friction.
	 * @param mu the coefficient of friction
	 */
	public void setMu(double mu) {
		if (mu < 0) throw new IllegalArgumentException("The coefficient of friction cannot be negative.");
		this.mu = mu;
	}
	
	/**
	 * Returns the coefficient of restitution.
	 * @return double
	 */
	public double getE() {
		return e;
	}

	/**
	 * Sets the coefficient of restitution.
	 * @param e the coefficient of restitution
	 */
	public void setE(double e) {
		if (e < 0 || e > 1) throw new IllegalArgumentException("The coefficient of restitution must be between 0 and 1 inclusive.");
		this.e = e;
	}
	
	/**
	 * Returns the linear damping.
	 * @return double
	 */
	public double getLinearDamping() {
		return linearDamping;
	}

	/**
	 * Sets the linear damping.
	 * @param linearDamping the linear damping
	 */
	public void setLinearDamping(double linearDamping) {
		if (linearDamping <= 0) throw new IllegalArgumentException("The linear damping must be greater than or equal to zero.");
		this.linearDamping = linearDamping;
	}
	
	/**
	 * Returns the angular damping.
	 * @return double
	 */
	public double getAngularDamping() {
		return angularDamping;
	}
	
	/**
	 * Sets the angular damping.
	 * @param angularDamping the angular damping
	 */
	public void setAngularDamping(double angularDamping) {
		if (angularDamping <= 0) throw new IllegalArgumentException("The angular damping must be greater than or equal to zero.");
		this.angularDamping = angularDamping;
	}
}