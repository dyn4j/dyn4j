/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.continuous.Swept;
import org.dyn4j.game2d.dynamics.contact.Contact;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactEdge;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.JointEdge;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Transformable;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents some physical {@link Body}.
 * <p>
 * A {@link Body} requires that at least one {@link BodyFixture} represent it, but 
 * allows any number of {@link BodyFixture}s.  When a {@link Body} is created there
 * are no {@link BodyFixture}s attached.  Concave {@link Body}s can be created
 * by attaching multiple {@link Convex} {@link BodyFixture}s.
 * <p>
 * Use the {@link #setMass()} or {@link #setMass(org.dyn4j.game2d.geometry.Mass.Type)}
 * methods to set the mass of the entire {@link Body} given the currently attached
 * {@link BodyFixture}s.  The {@link #setMass(Mass)} method can be used to set
 * the mass directly.  Use the {@link #setMassType(org.dyn4j.game2d.geometry.Mass.Type)}
 * method to toggle the mass type between the special types.
 * <p>
 * The coefficient of friction and restitution and linear and angular damping
 * are all defaulted but can be changed via the accessor and mutator methods.
 * <p>
 * By default {@link Body}s are allowed to be put to sleep automatically. {@link Body}s are 
 * put to sleep when they come to rest for a certain amount of time.  Applying any force,
 * torque, or impulse will wake the {@link Body}.
 * <p>
 * A {@link Body} becomes inactive when the {@link Body} has left the boundary of
 * the world.
 * <p>
 * A {@link Body} is dynamic if either its inertia or mass is greater than zero.
 * A {@link Body} is static if both its inertia and mass are zero.
 * <p>
 * A {@link Body} that is a sensor will not be handled in the collision
 * resolution but is handled in collision detection.
 * <p>
 * A {@link Body} flagged as a Bullet will be check for tunneling depending on the CCD
 * setting in the {@link Settings} singleton.  Use this if the body is a fast moving
 * body, but be careful as this will incur a performance hit.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Body implements Swept, Collidable, Transformable {
	/** The default linear damping; value = {@value #DEFAULT_LINEAR_DAMPING} */
	public static final double DEFAULT_LINEAR_DAMPING = 0.0;
	
	/** The default angular damping; value = {@value #DEFAULT_ANGULAR_DAMPING} */
	public static final double DEFAULT_ANGULAR_DAMPING 	= 0.01;
	
	/** The state flag for allowing automatic sleeping */
	protected static final int AUTO_SLEEP = 1;
	
	/** The state flag for the {@link Body} being asleep */
	protected static final int ASLEEP = 2;
	
	/** The state flag for the {@link Body} being active (out of bounds for example) */
	protected static final int ACTIVE = 4;
	
	/** The state flag indicating the {@link Body} has been added to an {@link Island} */
	protected static final int ISLAND = 8;
	
	/** The state flag indicating the {@link Body} is a really fast object and requires CCD */
	protected static final int BULLET = 16;
	
	/** The {@link Body}'s unique identifier */
	protected String id;
	
	/** The beginning transform for CCD */
	protected Transform transform0;
	
	/** The current {@link Transform} */
	protected Transform transform;

	/** The {@link BodyFixture}s list */
	protected List<BodyFixture> fixtures;
	
	/** The the rotation disk radius */
	protected double radius;
	
	/** The user data associated to this {@link Body} */
	protected Object userData;
	
	/** The {@link Mass} information */
	protected Mass mass;
	
	/** The current linear velocity */
	protected Vector2 velocity;

	/** The current angular velocity */
	protected double angularVelocity;

	/** The current force */
	protected Vector2 force;
	
	/** The current torque */
	protected double torque;
	
	/** The force accumulator */
	protected List<Force> forces;
	
	/** The torque accumulator */
	protected List<Torque> torques;
	
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
	 * Default constructor.
	 */
	public Body() {
		// the majority of bodies will contain one fixture/shape
		this.fixtures = new ArrayList<BodyFixture>(1);
		this.radius = 0.0;
		this.mass = new Mass();
		this.id = UUID.randomUUID().toString();
		this.transform0 = new Transform();
		this.transform = new Transform();
		this.velocity = new Vector2();
		this.angularVelocity = 0.0;
		this.force = new Vector2();
		this.torque = 0.0;
		this.forces = new ArrayList<Force>();
		this.torques = new ArrayList<Torque>();
		// initialize the state
		this.state = 0;
		// allow sleeping
		this.state |= Body.AUTO_SLEEP;
		// start off active
		this.state |= Body.ACTIVE;
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
		sb.append("BODY[").append(id).append("|FIXTURES[");
		// append all the shapes
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			sb.append(this.fixtures.get(i));
		}
		sb.append("]|").append(this.radius)
		.append("|TRANSFORM_0[").append(this.transform0).append("]")
		.append("|TRANSFORM[").append(this.transform).append("]")
		.append("|").append(this.mass)
		.append("|").append(this.velocity)
		.append("|").append(this.angularVelocity)
		.append("|").append(this.force)
		.append("|").append(this.torque)
		.append("|{");
		size = this.forces.size();
		for (int i = 0; i < size; i++) {
			sb.append(this.forces.get(i));
		}
		sb.append("}|{");
		size = this.torques.size();
		for (int i = 0; i < size; i++) {
			sb.append(this.torques.get(i));
		}
		sb.append("}|").append(this.state)
		.append("|").append(this.linearDamping)
		.append("|").append(this.angularDamping)
		.append("|").append(this.sleepTime);
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link Body}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * @param convex the {@link Convex} {@link Shape} to add to the {@link Body}
	 * @return {@link BodyFixture} the fixture created using the given {@link Shape} and added to the {@link Body}
	 * @throws NullPointerException if convex is null
	 */
	public BodyFixture addFixture(Convex convex) {
		// make sure the convex shape is not null
		if (convex == null) throw new NullPointerException("The convex shape cannot be null.");
		// create the fixture
		BodyFixture fixture = new BodyFixture(convex);
		// add the fixture to the body
		this.fixtures.add(fixture);
		// return the fixture so the caller can configure it
		return fixture;
	}
	
	/**
	 * Adds the given {@link BodyFixture} to this {@link Body}.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * @param fixture the {@link BodyFixture}
	 * @return {@link Body} this body
	 * @throws NullPointerException if fixture is null
	 */
	public Body addFixture(BodyFixture fixture) {
		// make sure neither is null
		if (fixture == null) throw new NullPointerException("The fixture cannot be null.");
		// add the shape and mass to the respective lists
		this.fixtures.add(fixture);
		// return this body to facilitate chaining
		return this;
	}

	/**
	 * Removes the given {@link BodyFixture} from the {@link Body}.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * @param fixture the {@link BodyFixture}
	 * @return boolean true if the {@link BodyFixture} was removed from this {@link Body}
	 */
	public boolean removeFixture(BodyFixture fixture) {
		// make sure the passed in fixture is not null
		if (fixture == null) return false;
		// get the number of fixtures
		int size = this.fixtures.size();
		// check fixtures size
		if (size > 0) {
			return this.fixtures.remove(fixture);
		}
		return false;
	}
	
	/**
	 * Removes the {@link BodyFixture} at the given index.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * @param index the index
	 * @return {@link BodyFixture} the fixture removed
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 */
	public BodyFixture removeFixture(int index) {
		return this.fixtures.remove(index);
	}
	
	/**
	 * This method should be called after fixture modification
	 * is complete.
	 * <p>
	 * This method will calculate a total mass for the body 
	 * given the masses of the fixtures.
	 * @return {@link Body} this body
	 * @see #setMass(Mass.Type)
	 * @see #addFixture(BodyFixture)
	 * @see #removeFixture(BodyFixture)
	 * @see #removeFixture(int)
	 */
	public Body setMass() {
		return this.setMass(Mass.Type.NORMAL);
	}
	
	/**
	 * This method should be called after fixture modification
	 * is complete.
	 * <p>
	 * This method will calculate a total mass for the body 
	 * given the masses of the fixtures.
	 * <p>
	 * A {@link org.dyn4j.game2d.geometry.Mass.Type} can be used to create special mass
	 * types.
	 * <p>
	 * If this method is called before any fixtures are added, the
	 * mass is set to a new {@link Mass} object with infinite mass despite
	 * the given type passed in.
	 * @param type the {@link org.dyn4j.game2d.geometry.Mass.Type}
	 * @return {@link Body} this body
	 * @throws NullPointerException if type is null
	 * @see #addFixture(BodyFixture)
	 * @see #removeFixture(BodyFixture)
	 * @see #removeFixture(int)
	 */
	public Body setMass(Mass.Type type) {
		// check for null
		if (type == null) throw new NullPointerException("The specified mass type cannot be null.");
		// get the size
		int size = this.fixtures.size();
		// check the size
		if (size == 0) {
			// set the mass to an infinite point mass at (0, 0)
			this.mass = new Mass();
			// ignore the passed in type
		} else if (size == 1) {
			// then just use the mass for the first shape
			this.mass = this.fixtures.get(0).createMass();
			// set the type
			this.mass.setType(type);
		} else {
			// create a list of mass objects
			List<Mass> masses = new ArrayList<Mass>();
			// create a mass object for each shape
			for (int i = 0; i < size; i++) {
				Mass mass = this.fixtures.get(i).createMass();
				masses.add(mass);
			}
			this.mass = Mass.create(masses);
			// set the type
			this.mass.setType(type);
		}
		// compute the rotation disc radius
		this.setRotationDiscRadius();
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Sets this {@link Body}'s mass information.
	 * <p>
	 * This method can be used to set the mass of the body explicitly.
	 * @param mass the new {@link Mass}
	 * @return {@link Body} this body
	 * @throws NullPointerException if mass is null
	 */
	public Body setMass(Mass mass) {
		// make sure the mass is not null
		if (mass == null) throw new NullPointerException("The mass cannot be null.");
		// set the mass
		this.mass = mass;
		// compute the rotation disc radius
		this.setRotationDiscRadius();
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Sets the {@link org.dyn4j.game2d.geometry.Mass.Type} of this {@link Body}.
	 * <p>
	 * This method does not compute/recompute the mass of the body but solely
	 * sets the mass type to one of the special types.
	 * <p>
	 * If the mass of this body has not been set previously by one of the {@link #setMass()}
	 * methods, then this method will compute the mass.
	 * <p>
	 * Since its possible to create a {@link Mass} object with zero mass and/or
	 * zero inertia (<code>Mass m = new Mass(new Vector2(), 0, 0);</code> for example), setting the type 
	 * to something other than Mass.Type.INFINITE can have undefined results.  In this case its 
	 * best to use the {@link #setMass(org.dyn4j.game2d.geometry.Mass.Type)} methods to recompute 
	 * the mass.
	 * @param type the desired type
	 * @return {@link Body} this body
	 * @throws NullPointerException if type is null
	 * @since 2.2.3
	 */
	public Body setMassType(Mass.Type type) {
		// check for null type
		if (type == null) throw new NullPointerException("The mass type cannot be null.");
		// make sure the current mass is not null
		if (this.mass == null) {
			// if its null then just compute it for the first time
			this.setMass(type);
		} else {
			// otherwise just set the type
			this.mass.setType(type);
		}
		// return this body
		return this;
	}
	
	/**
	 * Computes the rotation disc for this {@link Body}.
	 * <p>
	 * This method requires that the center of mass be
	 * computed first.
	 * @since 2.0.0
	 */
	protected void setRotationDiscRadius() {
		double r = 0.0;
		// get the number of fixtures
		int size = this.fixtures.size();
		// check for zero fixtures
		if (size == 0) return;
		// get the body's center of mass
		Vector2 c = this.mass.getCenter();
		// loop over the fixtures
		for (int i = 0; i < size; i++) {
			// get the fixture and convex
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			// get the convex's center and radius
			Vector2 cc = convex.getCenter();
			double cr = convex.getRadius();
			// get the distance from the convex's center
			// to the center of mass
			double d = c.distance(cc);
			// keep the maximum
			r = Math.max(r, d + cr);
		}
		// return the max
		this.radius = r;
	}
	
	/**
	 * Returns this {@link Body}'s mass information.
	 * @return {@link Mass}
	 */
	public Mass getMass() {
		return this.mass;
	}
	
	/**
	 * Applies the given force to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 */
	public Body apply(Vector2 force) {
		// check for null
		if (force == null) throw new NullPointerException("Cannot apply a null force.");
		// apply the force
		this.forces.add(new Force(force));
		// wake up the body
		this.setAsleep(false);
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Applies the given {@link Force} to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 */
	public Body apply(Force force) {
		// check for null
		if (force == null) throw new NullPointerException("Cannot apply a null force.");
		// add the force to the list
		this.forces.add(force);
		// wake up the body
		this.setAsleep(false);
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Applies the given torque about the center of this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param torque the torque about the center
	 * @return {@link Body} this body
	 */
	public Body apply(double torque) {
		// apply the torque
		this.torques.add(new Torque(torque));
		// wake up the body
		this.setAsleep(false);
		// return this body
		return this;
	}
	
	/**
	 * Applies the given {@link Torque} to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param torque the torque
	 * @return {@link Body} this body
	 * @throws NullPointerException if torque is null
	 */
	public Body apply(Torque torque) {
		// check for null
		if (torque == null) throw new NullPointerException("Cannot apply a null torque.");
		// add the torque to the list
		this.torques.add(torque);
		// wake up the body
		this.setAsleep(false);
		// return this body to facilitate chaining
		return this;
	}

	/**
	 * Applies the given force to this {@link Body} at the
	 * given point (torque).
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @param point the application point in world coordinates
	 * @return {@link Body} this body
	 * @throws NullPointerException if force or point is null
	 */
	public Body apply(Vector2 force, Vector2 point) {
		// check for null
		if (force == null) throw new NullPointerException("Cannot apply a torque with a null force.");
		if (point == null) throw new NullPointerException("Cannot apply a torque with a null application point.");
		// apply the force
		this.forces.add(new Force(force));
		// compute the moment r
		Vector2 r = this.getWorldCenter().to(point);
		// check for the zero vector
		if (!r.isZero()) {
			// find the torque about the given point
			double tao = r.cross(force);
			// apply the torque
			this.torques.add(new Torque(tao));
		}
		// wake up the body
		this.setAsleep(false);
		// return this body to facilitate chaining
		return this;
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
	protected void accumulate() {
		// set the current force to zero
		this.force.zero();
		// get the number of forces
		int size = this.forces.size();
		// check if the size is greater than zero
		if (size > 0) {
			// apply all the forces
			Iterator<Force> it = this.forces.iterator();
			while(it.hasNext()) {
				Force force = it.next();
				force.apply(this);
				// see if we should remove the force
				if (force.isComplete()) {
					it.remove();
				}
			}
		}
		// set the current torque to zero
		this.torque = 0.0;
		// get the number of torques
		size = this.torques.size();
		// check the size
		if (size > 0) {
			// apply all the torques
			Iterator<Torque> it = this.torques.iterator();
			while(it.hasNext()) {
				Torque torque = it.next();
				torque.apply(this);
				// see if we should remove the torque
				if (torque.isComplete()) {
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Returns true if this body has infinite mass and
	 * the velocity and angular velocity is zero.
	 * @return boolean
	 */
	public boolean isStatic() {
		return this.mass.isInfinite() && this.velocity.isZero() && this.angularVelocity == 0.0;
	}
	
	/**
	 * Returns true if this body has infinite mass and
	 * the velocity or angular velocity are NOT zero.
	 * @return boolean
	 */
	public boolean isKinematic() {
		return this.mass.isInfinite() && (!this.velocity.isZero() || this.angularVelocity != 0.0);
	}
	
	/**
	 * Returns true if this body does not have infinite mass.
	 * @return boolean
	 */
	public boolean isDynamic() {
		return !this.mass.isInfinite();
	}
	
	/**
	 * Sets the {@link Body} to allow or disallow automatic sleeping.
	 * @param flag true if the {@link Body} is allowed to sleep
	 * @since 1.2.0
	 */
	public void setAutoSleepingEnabled(boolean flag) {
		// see if the body can already sleep
		if (flag) {
			this.state |= Body.AUTO_SLEEP;
		} else {
			this.state &= ~Body.AUTO_SLEEP;
		}
	}
	
	/**
	 * Returns true if this {@link Body} is allowed to be 
	 * put to sleep automatically.
	 * @return boolean
	 * @since 1.2.0
	 */
	public boolean isAutoSleepingEnabled() {
		return (this.state & Body.AUTO_SLEEP) == Body.AUTO_SLEEP;
	}
	
	/**
	 * Returns true if this {@link Body} is sleeping.
	 * @return boolean
	 */
	public boolean isAsleep() {
		return (this.state & Body.ASLEEP) == Body.ASLEEP;
	}
	
	/**
	 * Sets whether this {@link Body} is awake or not.
	 * @param flag true if the body should be put to sleep
	 */
	public void setAsleep(boolean flag) {
		if (flag) {
			this.state |= Body.ASLEEP;
			this.velocity.zero();
			this.angularVelocity = 0.0;
			this.clearForce();
			this.clearTorque();
			this.forces.clear();
			this.torques.clear();
		} else {
			// check if the body is asleep
			if ((this.state & Body.ASLEEP) == Body.ASLEEP) {
				// if the body is asleep then wake it up
				this.sleepTime = 0.0;
				this.state &= ~Body.ASLEEP;
			}
			// otherwise do nothing
		}
	}
	
	/**
	 * Returns the duration the body has been attempting to sleep.
	 * <p>
	 * Once a body is asleep the sleep time is reset to zero.
	 * @return double
	 */
	protected double getSleepTime() {
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
	 * Returns true if this {@link Body} is active.
	 * @return boolean
	 */
	public boolean isActive() {
		return (this.state & Body.ACTIVE) == Body.ACTIVE;
	}
	
	/**
	 * Sets whether this {@link Body} is active or not.
	 * @param flag true if this {@link Body} should be active
	 */
	public void setActive(boolean flag) {
		if (flag) {
			this.state |= Body.ACTIVE;
		} else {
			this.state &= ~Body.ACTIVE;
		}
	}
	
	/**
	 * Returns true if this {@link Body} has been added to an {@link Island}.
	 * @return boolean true if this {@link Body} has been added to an {@link Island} 
	 */
	protected boolean isOnIsland() {
		return (this.state & Body.ISLAND) == Body.ISLAND;
	}
	
	/**
	 * Sets the flag indicating that the {@link Body} has been added to an {@link Island}.
	 * @param flag true if the {@link Body} has been added to an {@link Island}
	 */
	protected void setOnIsland(boolean flag) {
		if (flag) {
			this.state |= Body.ISLAND;
		} else {
			this.state &= ~Body.ISLAND;
		}
	}
	
	/**
	 * Returns true if this {@link Body} is a bullet.
	 * @see #setBullet(boolean)
	 * @return boolean
	 * @since 1.2.0
	 */
	public boolean isBullet() {
		return (this.state & Body.BULLET) == Body.BULLET;
	}
	
	/**
	 * Sets the bullet flag for this {@link Body}.
	 * <p>
	 * A bullet is a very fast moving body that requires
	 * continuous collision detection with <b>all</b> other
	 * {@link Body}s to ensure that no collisions are missed.
	 * @param flag true if this {@link Body} is a bullet
	 * @since 1.2.0
	 */
	public void setBullet(boolean flag) {
		if (flag) {
			this.state |= Body.BULLET;
		} else {
			this.state &= ~Body.BULLET;
		}
	}
	
	/**
	 * Returns true if the given {@link Body} is connected
	 * to this {@link Body} by a {@link Joint}.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the suspect connected body
	 * @return boolean
	 */
	public boolean isConnected(Body body) {
		// check for a null body
		if (body == null) return false;
		int size = this.joints.size();
		// check the size
		if (size == 0) return false;
		// loop over all the joints
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			// testing object references should be sufficient
			if (je.getOther() == body) {
				// if it is then return true
				return true;
			}
		}
		// not found, so return false
		return false;
	}
	
	/**
	 * Returns true if the given {@link Body} is connected to this
	 * {@link Body}, given the collision flag, via a {@link Joint}.
	 * <p>
	 * If the given collision flag is true, this method will return true
	 * only if collision is allowed between the two joined {@link Body}s.
	 * <p>
	 * If the given collision flag is false, this method will return true
	 * only if collision is <b>NOT</b> allowed between the two joined {@link Body}s.
	 * <p>
	 * If the {@link Body}s are connected by more than one joint, if any allows
	 * collision, then the bodies are considered connected AND allowing collision.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the suspect connected body
	 * @param collisionAllowed the collision allowed flag
	 * @return boolean
	 */
	public boolean isConnected(Body body, boolean collisionAllowed) {
		// check for a null body
		if (body == null) return false;
		int size = this.joints.size();
		// check the size
		if (size == 0) return false;
		// loop over all the joints
		boolean allowed = false;
		boolean connected = false;
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			// testing object references should be sufficient
			if (je.getOther() == body) {
				// get the joint
				Joint joint = je.getJoint();
				// set that they are connected
				connected = true;
				// check if collision is allowed
				// we do an or here to find if there is at least one
				// joint joining the two bodies that allows collision
				allowed |= joint.isCollisionAllowed();
			}
		}
		// if they are not connected at all we can ignore the collision
		// allowed flag passed in and return false
		if (!connected) return false;
		// if at least one joint between the two bodies allow collision
		// then the allowed variable will be true, check this against 
		// the desired flag passed in
		if (allowed == collisionAllowed) {
			return true;
		}
		// not found, so return false
		return false;
	}
	
	/**
	 * Returns true if the given {@link Body} is in collision with this {@link Body}.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the {@link Body} to test
	 * @return boolean true if the given {@link Body} is in collision with this {@link Body}
	 * @since 1.2.0
	 */
	public boolean isInContact(Body body) {
		// check for a null body
		if (body == null) return false;
		// get the number of contacts
		int size = this.contacts.size();
		// check for zero contacts
		if (size == 0) return false;
		// loop over the contacts
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// is the other body equal to the given body?
			if (ce.getOther() == body) {
				// if so then return true
				return true;
			}
		}
		// if we get here then we know no contact exists
		return false;
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
	public void rotate(double theta, Vector2 point) {
		this.transform.rotate(theta, point);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	/**
	 * Rotates the {@link Body} about its center of mass.
	 * @param theta the angle of rotation in radians
	 */
	public void rotateAboutCenter(double theta) {
		Vector2 center = this.getWorldCenter();
		this.rotate(theta, center);
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
	public void translate(Vector2 vector) {
		this.transform.translate(vector);
	}
	/**
	 * Translates the body to the origin.
	 * <p>
	 * This method is useful if bodies have a number of fixtures and the center of mass
	 * is not at the origin.  This method will reposition the body so that the center of
	 * mass is at the origin.
	 * @since 2.2.2
	 */
	public void translateToOrigin() {
		// get the world space center of mass
		Vector2 wc = this.transform.getTransformed(this.mass.getCenter());
		// translate the body negative that much to put it at the origin
		this.transform.translate(-wc.x, -wc.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getFixture(int)
	 */
	public BodyFixture getFixture(int index) {
		return this.fixtures.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getFixtureCount()
	 */
	public int getFixtureCount() {
		return this.fixtures.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getTransform()
	 */
	public Transform getTransform() {
		return this.transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.continuous.Swept#getInitialTransform()
	 */
	@Override
	public Transform getInitialTransform() {
		return this.transform0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.continuous.Swept#getFinalTransform()
	 */
	@Override
	public Transform getFinalTransform() {
		return this.transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.continuous.Swept#getRotationDiscRadius()
	 */
	public double getRotationDiscRadius() {
		return this.radius;
	}
	
	/**
	 * Sets this {@link Body}'s transform.
	 * @param transform the transform
	 * @throws NullPointerException if transform is null
	 * @since 1.1.0
	 */
	public void setTransform(Transform transform) {
		if (transform == null) throw new NullPointerException("A body cannot have a null transform.");
		this.transform.set(transform);
		this.transform0.set(transform);
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
	 * Returns the center of mass for the body in local coordinates.
	 * @return {@link Vector2} the center of mass in local coordinates
	 */
	public Vector2 getLocalCenter() {
		return this.mass.getCenter();
	}
	
	/**
	 * Returns the center of mass for the body in world coordinates.
	 * @return {@link Vector2} the center of mass in world coordinates
	 */
	public Vector2 getWorldCenter() {
		return this.transform.getTransformed(this.mass.getCenter());
	}
	
	/**
	 * Returns a new point in local coordinates of this body given
	 * a point in world coordinates.
	 * @param worldPoint a world space point
	 * @return {@link Vector2} local space point
	 */
	public Vector2 getLocalPoint(Vector2 worldPoint) {
		return this.transform.getInverseTransformed(worldPoint);
	}
	
	/**
	 * Returns a new point in world coordinates given a point in the
	 * local coordinates of this {@link Body}.
	 * @param localPoint a point in the local coordinates of this {@link Body}
	 * @return {@link Vector2} world space point
	 */
	public Vector2 getWorldPoint(Vector2 localPoint) {
		return this.transform.getTransformed(localPoint);
	}
	
	/**
	 * Returns a new vector in local coordinates of this body given
	 * a vector in world coordinates.
	 * @param worldVector a world space vector
	 * @return {@link Vector2} local space vector
	 */
	public Vector2 getLocalVector(Vector2 worldVector) {
		return this.transform.getInverseTransformedR(worldVector);
	}
	
	/**
	 * Returns a new vector in world coordinates given a vector in the
	 * local coordinates of this {@link Body}.
	 * @param localVector a vector in the local coordinates of this {@link Body}
	 * @return {@link Vector2} world space vector
	 */
	public Vector2 getWorldVector(Vector2 localVector) {
		return this.transform.getTransformedR(localVector);
	}
	
	/**
	 * Returns the velocity {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getVelocity() {
		return this.velocity;
	}
	
	/**
	 * Sets the velocity {@link Vector2}.
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * @param velocity the velocity
	 * @throws NullPointerException if velocity is null
	 */
	public void setVelocity(Vector2 velocity) {
		if (velocity == null) throw new NullPointerException("The velocity vector cannot be null.");
		this.velocity = velocity;
	}

	/**
	 * Returns the angular velocity.
	 * @return double
	 */
	public double getAngularVelocity() {
		return this.angularVelocity;
	}

	/**
	 * Sets the angular velocity.
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * @param angularVelocity the angular velocity
	 */
	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
	
	/**
	 * Returns the force {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getForce() {
		return this.force;
	}

	/**
	 * Returns the torque.
	 * @return double
	 */
	public double getTorque() {
		return this.torque;
	}
	
	/**
	 * Returns the linear damping.
	 * @return double
	 */
	public double getLinearDamping() {
		return this.linearDamping;
	}

	/**
	 * Sets the linear damping.
	 * @param linearDamping the linear damping
	 * @throws IllegalArgumentException if linearDamping is less than zero
	 */
	public void setLinearDamping(double linearDamping) {
		if (linearDamping < 0) throw new IllegalArgumentException("The linear damping must be greater than or equal to zero.");
		this.linearDamping = linearDamping;
	}
	
	/**
	 * Returns the angular damping.
	 * @return double
	 */
	public double getAngularDamping() {
		return this.angularDamping;
	}
	
	/**
	 * Sets the angular damping.
	 * @param angularDamping the angular damping
	 * @throws IllegalArgumentException if angularDamping is less than zero
	 */
	public void setAngularDamping(double angularDamping) {
		if (angularDamping < 0) throw new IllegalArgumentException("The angular damping must be greater than or equal to zero.");
		this.angularDamping = angularDamping;
	}
	
	/**
	 * Returns a list of {@link Body}s connected
	 * by {@link Joint}s.
	 * @return List&lt;{@link Body}&gt;
	 * @since 1.0.1
	 */
	public List<Body> getJoinedBodies() {
		int size = this.joints.size();
		// create a list of the correct capacity
		List<Body> bodies = new ArrayList<Body>(size);
		// add all the joint bodies
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			bodies.add(je.getOther());
		}
		// return the connected bodies
		return bodies;
	}

	/**
	 * Returns a list of {@link Joint}s that this 
	 * {@link Body} is connected with.
	 * @return List&lt;{@link Joint}&gt;
	 * @since 1.0.1
	 */
	public List<Joint> getJoints() {
		int size = this.joints.size();
		// create a list of the correct capacity
		List<Joint> joints = new ArrayList<Joint>(size);
		// add all the joints
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			joints.add(je.getJoint());
		}
		// return the connected joints
		return joints;
	}
	
	/**
	 * Returns a list of {@link Body}s that are in
	 * contact with this {@link Body}.
	 * <p>
	 * Passing a value of true results in a list containing only
	 * the sensed contacts for this body.  Passing a value of false
	 * results in a list containing only normal contacts.
	 * <p>
	 * Calling this method from any of the {@link CollisionListener} methods
	 * may produce incorrect results.
	 * @param sensed true for only sensed contacts; false for only normal contacts
	 * @return List&lt;{@link Body}&gt;
	 * @since 1.0.1
	 */
	public List<Body> getInContactBodies(boolean sensed) {
		int size = this.contacts.size();
		// create a list of the correct capacity
		List<Body> bodies = new ArrayList<Body>(size);
		// add all the contact bodies
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// check for sensor contact
			ContactConstraint constraint = ce.getContactConstraint();
			if (sensed == constraint.isSensor()) {
				// add it to the list
				bodies.add(ce.getOther());
			}
		}
		// return the connected bodies
		return bodies;
	}
	
	/**
	 * Returns a list of {@link ContactPoint}s 
	 * <p>
	 * Passing a value of true results in a list containing only
	 * the sensed contacts for this body.  Passing a value of false
	 * results in a list containing only normal contacts.
	 * <p>
	 * Calling this method from any of the {@link CollisionListener} methods
	 * may produce incorrect results.
	 * <p>
	 * Modifying the {@link ContactPoint}s returned is not advised.  Use the
	 * {@link ContactListener} methods instead.
	 * @param sensed true for only sensed contacts; false for only normal contacts
	 * @return List&lt;{@link ContactPoint}&gt;
	 * @since 1.0.1
	 */
	public List<ContactPoint> getContacts(boolean sensed) {
		int size = this.contacts.size();
		// create a list to store the contacts (worst case initial capacity)
		List<ContactPoint> contactPoints = new ArrayList<ContactPoint>(size * 2);
		// add all the contact points
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// check for sensor contact
			ContactConstraint constraint = ce.getContactConstraint();
			if (sensed == constraint.isSensor()) {
				// loop over the contacts
				Contact[] contacts = constraint.getContacts();
				int csize = contacts.length;
				for (int j = 0; j < csize; j++) {
					// get the contact
					Contact contact = contacts[j];
					// create the contact point
					ContactPoint contactPoint = new ContactPoint(
							constraint.body1, constraint.getFixture1(),
							constraint.body2, constraint.getFixture2(),
							contact.isEnabled(),
							contact.getPoint(),
							constraint.getNormal(),
							contact.getDepth());
					// add the point
					contactPoints.add(contactPoint);
				}
			}
		}
		// return the connected bodies
		return contactPoints;
	}
}