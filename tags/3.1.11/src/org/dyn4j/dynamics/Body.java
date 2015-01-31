/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dyn4j.Epsilon;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactEdge;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.ContactPointId;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.JointEdge;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents some physical {@link Body}.
 * <p>
 * A {@link Body} requires that at least one {@link BodyFixture} represent it, but 
 * allows any number of {@link BodyFixture}s.  When a {@link Body} is created there
 * are no {@link BodyFixture}s attached.  Concave {@link Body}s can be created
 * by attaching multiple {@link Convex} {@link BodyFixture}s.
 * <p>
 * Use the {@link #setMass()} or {@link #setMass(org.dyn4j.geometry.Mass.Type)}
 * methods to set the mass of the entire {@link Body} given the currently attached
 * {@link BodyFixture}s.  The {@link #setMass(Mass)} method can be used to set
 * the mass directly.  Use the {@link #setMassType(org.dyn4j.geometry.Mass.Type)}
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
 * setting in the world's {@link Settings}.  Use this if the body is a fast moving
 * body, but be careful as this will incur a performance hit.
 * @author William Bittle
 * @version 3.1.8
 * @since 1.0.0
 */
public class Body implements Collidable, Transformable {
	/** Number of fixtures typically added to a {@link Body} */
	private static final int TYPICAL_FIXTURE_COUNT = 1;
	
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
	
	/** The world this body belongs to */
	protected World world;
	
	/** The {@link Body}'s unique identifier */
	protected UUID id;
	
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
	
	/** The per body gravity scale factor */
	protected double gravityScale;
	
	/** The {@link Body}'s contacts */
	protected List<ContactEdge> contacts;
	
	/** The {@link Body}'s joints */
	protected List<JointEdge> joints;
	
	/**
	 * Default constructor.
	 */
	public Body() {
		this(Body.TYPICAL_FIXTURE_COUNT);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a new {@link Body} using the given estimated fixture count.
	 * Assignment of the initial fixture count allows sizing of internal structures
	 * for optimal memory/performance.  This estimated fixture count is <b>not</b> a
	 * limit on the number of fixtures.
	 * @param fixtureCount the estimated number of fixtures
	 * @throws IllegalArgumentException if fixtureCount less than zero
	 * @since 3.1.1
	 */
	public Body(int fixtureCount) {
		this.world = null;
		// the majority of bodies will contain one fixture/shape
		this.fixtures = new ArrayList<BodyFixture>(fixtureCount);
		this.radius = 0.0;
		this.mass = new Mass();
		this.id = UUID.randomUUID();
		this.transform0 = new Transform();
		this.transform = new Transform();
		this.velocity = new Vector2();
		this.angularVelocity = 0.0;
		this.force = new Vector2();
		this.torque = 0.0;
		this.forces = new ArrayList<Force>(8);
		this.torques = new ArrayList<Torque>(8);
		// initialize the state
		this.state = 0;
		// allow sleeping
		this.state |= Body.AUTO_SLEEP;
		// start off active
		this.state |= Body.ACTIVE;
		this.sleepTime = 0.0;
		this.linearDamping = Body.DEFAULT_LINEAR_DAMPING;
		this.angularDamping = Body.DEFAULT_ANGULAR_DAMPING;
		this.gravityScale = 1.0;
		this.contacts = new ArrayList<ContactEdge>(Collisions.getEstimatedCollisions());
		this.joints = new ArrayList<JointEdge>(2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Body[Id=").append(id).append("|Fixtures={");
		// append all the shapes
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			if (i != 0) sb.append(",");
			sb.append(this.fixtures.get(i));
		}
		sb.append("}|InitialTransform=").append(this.transform0)
		.append("|Transform=").append(this.transform)
		.append("|RotationDiscRadius=").append(this.radius)
		.append("|Mass=").append(this.mass)
		.append("|Velocity=").append(this.velocity)
		.append("|AngularVelocity=").append(this.angularVelocity)
		.append("|Force=").append(this.force)
		.append("|Torque=").append(this.torque)
		.append("|AccumulatedForce=").append(this.getAccumulatedForce())
		.append("|AccumulatedTorque=").append(this.getAccumulatedTorque())
		.append("|IsAutoSleepingEnabled=").append(this.isAutoSleepingEnabled())
		.append("|IsAsleep=").append(this.isAsleep())
		.append("|IsActive=").append(this.isActive())
		.append("|IsOnIsland=").append(this.isOnIsland())
		.append("|IsBullet=").append(this.isBullet())
		.append("|SleepTime=").append(this.sleepTime)
		.append("|LinearDamping=").append(this.linearDamping)
		.append("|AngularDamping").append(this.angularDamping)
		.append("|GravityScale=").append(this.gravityScale)
		.append("]");
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
	 * @see #addFixture(Convex, double, double, double)
	 */
	public BodyFixture addFixture(Convex convex) {
		// make sure the convex shape is not null
		if (convex == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullShape"));
		// create the fixture
		BodyFixture fixture = new BodyFixture(convex);
		// add the fixture to the body
		this.fixtures.add(fixture);
		// return the fixture so the caller can configure it
		return fixture;
	}
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link Body}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * <p>
	 * This is a convenience method for setting the density of a {@link BodyFixture}.
	 * @param convex the {@link Convex} {@link Shape} to add to the {@link Body}
	 * @param density the density of the shape in kg/m<sup>2</sup>; in the range (0.0, &infin;]
	 * @return {@link BodyFixture} the fixture created using the given {@link Shape} and added to the {@link Body}
	 * @throws NullPointerException if convex is null
	 * @throws IllegalArgumentException if density is less than or equal to zero; if friction or restitution is less than zero
	 * @see #addFixture(Convex)
	 * @see #addFixture(Convex, double, double, double)
	 * @since 3.1.5
	 */
	public BodyFixture addFixture(Convex convex, double density) {
		// make sure the convex shape is not null
		if (convex == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullShape"));
		// create the fixture
		BodyFixture fixture = new BodyFixture(convex);
		// set the properties
		fixture.setDensity(density);
		// add the fixture to the body
		this.fixtures.add(fixture);
		// return the fixture so the caller can configure it
		return fixture;
	}
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link Body}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * <p>
	 * This is a convenience method for setting the properties of a {@link BodyFixture}.
	 * Use the {@link BodyFixture#DEFAULT_DENSITY}, {@link BodyFixture#DEFAULT_FRICTION},
	 * and {@link BodyFixture#DEFAULT_RESTITUTION} values if you need to only set one
	 * of these properties.  
	 * @param convex the {@link Convex} {@link Shape} to add to the {@link Body}
	 * @param density the density of the shape in kg/m<sup>2</sup>; in the range (0.0, &infin;]
	 * @param friction the coefficient of friction; in the range [0.0, &infin;]
	 * @param restitution the coefficient of restitution; in the range [0.0, &infin;]
	 * @return {@link BodyFixture} the fixture created using the given {@link Shape} and added to the {@link Body}
	 * @throws NullPointerException if convex is null
	 * @throws IllegalArgumentException if density is less than or equal to zero; if friction or restitution is less than zero
	 * @see #addFixture(Convex)
	 * @see #addFixture(Convex, double)
	 * @since 3.1.1
	 */
	public BodyFixture addFixture(Convex convex, double density, double friction, double restitution) {
		// make sure the convex shape is not null
		if (convex == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullShape"));
		// create the fixture
		BodyFixture fixture = new BodyFixture(convex);
		// set the properties
		fixture.setDensity(density);
		fixture.setFriction(friction);
		fixture.setRestitution(restitution);
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
		if (fixture == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullFixture"));
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
	 * Removes all fixtures from this body and returns them.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #setMass()}
	 * or {@link #setMass(Mass.Type)} method to compute the new total
	 * {@link Mass} for the body.
	 * @return List&lt;{@link BodyFixture}&gt;
	 * @since 3.0.1
	 */
	public List<BodyFixture> removeAllFixtures() {
		// return the current list
		List<BodyFixture> fixtures = this.fixtures;
		// create a new list to replace the current list
		this.fixtures = new ArrayList<BodyFixture>(Body.TYPICAL_FIXTURE_COUNT);
		// return the current list
		return fixtures;
	}
	
	/**
	 * Returns true if this {@link Body} contains the given {@link BodyFixture}.
	 * @param fixture the fixture
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean containsFixture(BodyFixture fixture) {
		return this.fixtures.contains(fixture);
	}
	
	/**
	 * This method should be called after fixture modification
	 * is complete.
	 * <p>
	 * This method will calculate a total mass for the body 
	 * given the masses of the fixtures.
	 * <p>
	 * This method will always set this body's mass type to Normal.
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
	 * A {@link org.dyn4j.geometry.Mass.Type} can be used to create special mass
	 * types.
	 * @param type the {@link org.dyn4j.geometry.Mass.Type}
	 * @return {@link Body} this body
	 * @throws NullPointerException if type is null
	 * @see #addFixture(BodyFixture)
	 * @see #removeFixture(BodyFixture)
	 * @see #removeFixture(int)
	 */
	public Body setMass(Mass.Type type) {
		// check for null
		if (type == null) throw new NullPointerException(Messages.getString("dynamics.body.nullMassType"));
		// get the size
		int size = this.fixtures.size();
		// check the size
		if (size == 0) {
			// set the mass to an infinite point mass at (0, 0)
			this.mass = new Mass();
		} else if (size == 1) {
			// then just use the mass for the first shape
			this.mass = this.fixtures.get(0).createMass();
		} else {
			// create a list of mass objects
			List<Mass> masses = new ArrayList<Mass>(size);
			// create a mass object for each shape
			for (int i = 0; i < size; i++) {
				Mass mass = this.fixtures.get(i).createMass();
				masses.add(mass);
			}
			this.mass = Mass.create(masses);
		}
		// set the type
		this.mass.setType(type);
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
		if (mass == null) throw new NullPointerException(Messages.getString("dynamics.body.nullMass"));
		// set the mass
		this.mass = mass;
		// compute the rotation disc radius
		this.setRotationDiscRadius();
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Sets the {@link org.dyn4j.geometry.Mass.Type} of this {@link Body}.
	 * <p>
	 * This method does not compute/recompute the mass of the body but solely
	 * sets the mass type to one of the special types.
	 * <p>
	 * If the mass of this body has not been set previously by one of the {@link #setMass()}
	 * methods, then this method will compute the mass.
	 * <p>
	 * Since its possible to create a {@link Mass} object with zero mass and/or
	 * zero inertia (<code>Mass m = new Mass(new Vector2(), 0, 0);</code> for example), setting the type 
	 * to something other than Mass.Type.INFINITE can have undefined results.
	 * @param type the desired type
	 * @return {@link Body} this body
	 * @throws NullPointerException if type is null
	 * @since 2.2.3
	 */
	public Body setMassType(Mass.Type type) {
		// check for null type
		if (type == null) throw new NullPointerException(Messages.getString("dynamics.body.nullMassType"));
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
	 * <p>
	 * The rotation disc radius is the radius, from the center of mass,
	 * of the disc that encompasses the entire body as if it was rotated
	 * 360 degrees.
	 * @since 2.0.0
	 * @see #getRotationDiscRadius()
	 */
	protected void setRotationDiscRadius() {
		double r = 0.0;
		// get the number of fixtures
		int size = this.fixtures.size();
		// check for zero fixtures
		if (size == 0) {
			// set the radius to zero
			this.radius = 0.0;
			return;
		}
		// get the body's center of mass
		Vector2 c = this.mass.getCenter();
		// loop over the fixtures
		for (int i = 0; i < size; i++) {
			// get the fixture and convex
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			// get the convex's radius using the
			// body's center of mass
			double cr = convex.getRadius(c);
			// keep the maximum
			r = Math.max(r, cr);
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
	 * <p>
	 * This method does not apply the force if this body 
	 * returns zero from the {@link Mass#getMass()} method.
	 * <p>
	 * The force is not applied immediately, but instead stored in the 
	 * force accumulator ({@link #getAccumulatedForce()}).  This is to 
	 * preserve the last time step's computed force ({@link #getForce()}.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 * @since 3.1.1
	 */
	public Body applyForce(Vector2 force) {
		// check for null
		if (force == null) throw new NullPointerException(Messages.getString("dynamics.body.nullForce"));
		// check the linear mass of the body
		if (this.mass.getMass() == 0.0) {
			// this means that applying a force will do nothing
			// so, just return
			return this;
		}
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
	 * <p>
	 * This method does not apply the force if this body 
	 * returns zero from the {@link Mass#getMass()} method.
	 * <p>
	 * The force is not applied immediately, but instead stored in the 
	 * force accumulator ({@link #getAccumulatedForce()}).  This is to 
	 * preserve the last time step's computed force ({@link #getForce()}.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 * @since 3.1.1
	 */
	public Body applyForce(Force force) {
		// check for null
		if (force == null) throw new NullPointerException(Messages.getString("dynamics.body.nullForce"));
		// check the linear mass of the body
		if (this.mass.getMass() == 0.0) {
			// this means that applying a force will do nothing
			// so, just return
			return this;
		}
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
	 * <p>
	 * This method does not apply the torque if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * The torque is not applied immediately, but instead stored in the 
	 * torque accumulator ({@link #getAccumulatedTorque()}).  This is to 
	 * preserve the last time step's computed torque ({@link #getTorque()}.
	 * @param torque the torque about the center
	 * @return {@link Body} this body
	 * @since 3.1.1
	 */
	public Body applyTorque(double torque) {
		// apply the torque
		this.torques.add(new Torque(torque));
		// check the angular mass of the body
		if (this.mass.getInertia() == 0.0) {
			// this means that applying a torque will do nothing
			// so, just return
			return this;
		}
		// wake up the body
		this.setAsleep(false);
		// return this body
		return this;
	}
	
	/**
	 * Applies the given {@link Torque} to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * <p>
	 * This method does not apply the torque if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * The torque is not applied immediately, but instead stored in the 
	 * torque accumulator ({@link #getAccumulatedTorque()}).  This is to 
	 * preserve the last time step's computed torque ({@link #getTorque()}.
	 * @param torque the torque
	 * @return {@link Body} this body
	 * @throws NullPointerException if torque is null
	 * @since 3.1.1
	 */
	public Body applyTorque(Torque torque) {
		// check for null
		if (torque == null) throw new NullPointerException(Messages.getString("dynamics.body.nullTorque"));
		// check the angular mass of the body
		if (this.mass.getInertia() == 0.0) {
			// this means that applying a torque will do nothing
			// so, just return
			return this;
		}
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
	 * <p>
	 * This method does not apply the force if this body  
	 * returns zero from the {@link Mass#getMass()} method nor 
	 * will it apply the torque if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * The force/torque is not applied immediately, but instead stored in the 
	 * force/torque accumulators ({@link #getAccumulatedForce()} and
	 * {@link #getAccumulatedTorque()}).  This is to preserve the last time 
	 * step's computed force ({@link #getForce()} and torque ({@link #getTorque()}).
	 * @param force the force
	 * @param point the application point in world coordinates
	 * @return {@link Body} this body
	 * @throws NullPointerException if force or point is null
	 * @since 3.1.1
	 */
	public Body applyForce(Vector2 force, Vector2 point) {
		// check for null
		if (force == null) throw new NullPointerException(Messages.getString("dynamics.body.nullForceForTorque"));
		if (point == null) throw new NullPointerException(Messages.getString("dynamics.body.nullPointForTorque"));
		boolean awaken = false;
		// check the linear mass of the body
		if (this.mass.getMass() != 0.0) {
			// apply the force
			this.forces.add(new Force(force));
			awaken = true;
		}
		// check the angular mass of the body
		if (this.mass.getInertia() != 0.0) {
			// compute the moment r
			Vector2 r = this.getWorldCenter().to(point);
			// check for the zero vector
			if (!r.isZero()) {
				// find the torque about the given point
				double tao = r.cross(force);
				// apply the torque
				this.torques.add(new Torque(tao));
				awaken = true;
			}
		}
		// see if we applied either
		if (awaken) {
			// wake up the body
			this.setAsleep(false);
		}
		// return this body to facilitate chaining
		return this;
	}
	
	/**
	 * Applies a linear impulse to this {@link Body} at its center of mass.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * <p>
	 * This method does not apply the impulse if this body's mass 
	 * returns zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * @param impulse the impulse to apply
	 * @return {@link Body} this body
	 * @throws NullPointerException if impulse is null
	 * @since 3.1.1
	 */
	public Body applyImpulse(Vector2 impulse) {
		// check for null
		if (impulse == null) throw new NullPointerException(Messages.getString("dynamics.body.nullImpulse"));
		// get the inverse linear mass
		double invM = this.mass.getInverseMass();
		// check the linear mass
		if (invM == 0.0) {
			// this means that applying an impulse will do nothing
			// so, just return
			return this;
		}
		// apply the impulse immediately
		this.velocity.add(impulse.x * invM, impulse.y * invM);
		// wake up the body
		this.setAsleep(false);
		// return this body
		return this;
	}
	
	/**
	 * Applies an angular impulse to this {@link Body} about its center of mass.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * <p>
	 * This method does not apply the impulse if this body's inertia 
	 * returns zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * @param impulse the impulse to apply
	 * @return {@link Body} this body
	 * @since 3.1.1
	 */
	public Body applyImpulse(double impulse) {
		double invI = this.mass.getInverseInertia();
		// check the angular mass
		if (invI == 0.0) {
			// this means that applying an impulse will do nothing
			// so, just return
			return this;
		}
		// apply the impulse immediately
		this.angularVelocity += invI * impulse;
		// wake up the body
		this.setAsleep(false);
		// return this body
		return this;
	}
	
	/**
	 * Applies an impulse to this {@link Body} at the given point.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * <p>
	 * This method does not apply the linear impulse if this body 
	 * returns zero from the {@link Mass#getMass()} method nor 
	 * will it apply the angular impulse if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * @param impulse the impulse to apply
	 * @param point the world space point to apply the impulse
	 * @return {@link Body} this body
	 * @throws NullPointerException if impulse or point is null
	 * @since 3.1.1
	 */
	public Body applyImpulse(Vector2 impulse, Vector2 point) {
		// check for null
		if (impulse == null) throw new NullPointerException(Messages.getString("dynamics.body.nullImpulse"));
		if (point == null) throw new NullPointerException(Messages.getString("dynamics.body.nullPointForImpulse"));
		boolean awaken = false;
		// get the inverse mass
		double invM = this.mass.getInverseMass();
		double invI = this.mass.getInverseInertia();
		// check the linear mass
		if (invM != 0.0) {
			// apply the impulse immediately
			this.velocity.add(impulse.x * invM, impulse.y * invM);
			awaken = true;
		}
		if (invI != 0.0) {
			// apply the impulse immediately
			Vector2 r = this.getWorldCenter().to(point);
			this.angularVelocity += invI * r.cross(impulse);
			awaken = true;
		}
		if (awaken) {
			// wake up the body
			this.setAsleep(false);
		}
		// return this body
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
	 * <p>
	 * Renamed from clearForces (3.0.0 and below).
	 * @since 3.0.1
	 */
	public void clearAccumulatedForce() {
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
	 * <p>
	 * Renamed from clearTorques (3.0.0 and below).
	 * @since 3.0.1
	 */
	public void clearAccumulatedTorque() {
		this.torques.clear();
	}
	
	/**
	 * Accumulates the forces and torques.
	 * @param elapsedTime the elapsed time since the last call
	 * @since 3.1.0
	 */
	protected void accumulate(double elapsedTime) {
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
				if (force.isComplete(elapsedTime)) {
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
				if (torque.isComplete(elapsedTime)) {
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
		return this.mass.isInfinite() && this.velocity.isZero() && Math.abs(this.angularVelocity) <= Epsilon.E;
	}
	
	/**
	 * Returns true if this body has infinite mass and
	 * the velocity or angular velocity are NOT zero.
	 * @return boolean
	 */
	public boolean isKinematic() {
		return this.mass.isInfinite() && (!this.velocity.isZero() || Math.abs(this.angularVelocity) > Epsilon.E);
	}
	
	/**
	 * Returns true if this body does not have infinite mass.
	 * @return boolean
	 */
	public boolean isDynamic() {
		return this.mass.getType() != Mass.Type.INFINITE;
	}
	
	/**
	 * Sets the world that this body belongs to.
	 * @param world the world
	 * @since 3.0.3
	 */
	protected void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * Returns the world this body belongs to.
	 * <p>
	 * Returns null if the body has not been added to a world
	 * or has been removed.
	 * @return {@link World}
	 * @since 3.0.3
	 */
	public World getWorld() {
		return this.world;
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
	 * <p>
	 * If flag is true, this body's velocity, angular velocity,
	 * force, torque, and accumulators are cleared.
	 * @param flag true if the body should be put to sleep
	 */
	public void setAsleep(boolean flag) {
		if (flag) {
			this.state |= Body.ASLEEP;
			this.velocity.zero();
			this.angularVelocity = 0.0;
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
	 * @see org.dyn4j.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.transform.rotate(theta, point);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double)
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
	 * @see org.dyn4j.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(org.dyn4j.geometry.Vector)
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
	
	/**
	 * Shifts (translates) this body by the given shift amount.
	 * <p>
	 * Typically this method should not be called directly.  Instead 
	 * use the {@link World#shiftCoordinates(Vector2)} method to move the 
	 * entire world.
	 * @param shift the distance to shift along the x and y axes
	 * @since 3.1.0
	 */
	protected void shiftCoordinates(Vector2 shift) {
		this.transform.translate(shift);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixture(int)
	 */
	public BodyFixture getFixture(int index) {
		return this.fixtures.get(index);
	}
	
	/**
	 * Returns the first fixture, as determined by the order in which they were added, that
	 * contains the given point.
	 * <p>
	 * Returns null if the point is not contained in any fixture in this body.
	 * @param point a world space point
	 * @return {@link BodyFixture}
	 * @since 3.1.8
	 */
	public BodyFixture getFixture(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				return fixture;
			}
		}
		return null;
	}
	
	/**
	 * Returns all the fixtures that contain the given point.
	 * <p>
	 * Returns an empty list if the point is not contained in any fixture in this body.
	 * @param point a world space point
	 * @return List&lt;{@link BodyFixture}&gt;
	 * @since 3.1.8
	 */
	public List<BodyFixture> getFixtures(Vector2 point) {
		List<BodyFixture> fixtures = new ArrayList<BodyFixture>();
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				fixtures.add(fixture);
			}
		}
		return fixtures;
	}
	
	/**
	 * Removes the first fixture, as determined by the order in which they were added, that
	 * contains the given point and returns it.
	 * <p>
	 * Returns null if the point is not contained in any fixture in this body.
	 * @param point a world space point
	 * @return {@link BodyFixture}
	 * @since 3.1.8
	 */
	public BodyFixture removeFixture(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				this.fixtures.remove(i);
				return fixture;
			}
		}
		return null;
	}
	
	/**
	 * Returns all the fixtures that contain the given point.
	 * <p>
	 * Returns an empty list if the point is not contained in any fixture in this body.
	 * @param point a world space point
	 * @return List&lt;{@link BodyFixture}&gt;
	 * @since 3.1.8
	 */
	public List<BodyFixture> removeFixtures(Vector2 point) {
		List<BodyFixture> fixtures = new ArrayList<BodyFixture>();
		Iterator<BodyFixture> it = this.fixtures.iterator();
		while (it.hasNext()) {
			BodyFixture fixture = it.next();
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				it.remove();
				fixtures.add(fixture);
			}
		}
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtureCount()
	 */
	public int getFixtureCount() {
		return this.fixtures.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtures()
	 */
	public List<BodyFixture> getFixtures() {
		List<BodyFixture> fixtures = new ArrayList<BodyFixture>(this.fixtures.size());
		fixtures.addAll(this.fixtures);
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getTransform()
	 */
	public Transform getTransform() {
		return this.transform;
	}
	
	/**
	 * Returns the transform of the last iteration.
	 * <p>
	 * This transform represents the last frame's position and
	 * orientation.
	 * @return {@link Transform}
	 */
	public Transform getInitialTransform() {
		return this.transform0;
	}
	
	/**
	 * Returns the maximum radius of the disk that the
	 * {@link Collidable} creates if rotated 360 degrees.
	 * @return double the maximum radius of the rotation disk
	 */
	public double getRotationDiscRadius() {
		return this.radius;
	}
	
	/**
	 * Sets this {@link Body}'s transform.
	 * <p>
	 * This method sets both the initial and the current transform of this body.  This is 
	 * important to know when this body is a fast moving body (bullet) and needs to
	 * be checked for tunneling.  Instead, set the initial and final transforms
	 * explicitly by calling {@link #getInitialTransform()} and {@link #getTransform()}
	 * and calling the {@link Transform#set(Transform)} method.
	 * @param transform the transform
	 * @throws NullPointerException if transform is null
	 * @since 1.1.0
	 */
	public void setTransform(Transform transform) {
		if (transform == null) throw new NullPointerException(Messages.getString("dynamics.body.nullTransform"));
		this.transform.set(transform);
		this.transform0.set(transform);
	}
	
	/**
	 * Returns the user data associated to this {@link Body}.
	 * @return Object
	 */
	public Object getUserData() {
		return this.userData;
	}
	
	/**
	 * Sets the user data associated to this {@link Body}.
	 * @param userData the user data object
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getId()
	 */
	public UUID getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#createAABB()
	 */
	@Override
	public AABB createAABB() {
		return this.createAABB(this.transform);
	}
	
	/**
	 * Creates an {@link AABB} from this {@link Body} using the given 
	 * world space {@link Transform}.
	 * <p>
	 * This method returns a degenerate AABB, (0.0, 0.0) to (0.0, 0.0),
	 * for {@link Body}s that have no fixtures.
	 * @param transform the world space {@link Transform}
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB createAABB(Transform transform) {
		// get the number of fixtures
		int size = this.fixtures.size();
		// make sure there is at least one
		if (size > 0) {
			// create the aabb for the first fixture
			AABB aabb = this.fixtures.get(0).getShape().createAABB(transform);
			// loop over the remaining fixtures, unioning the aabbs
			for (int i = 1; i < size; i++) {
				// create the aabb for the current fixture
				AABB faabb = this.fixtures.get(i).getShape().createAABB(transform);
				// union the aabbs
				aabb.union(faabb);
			}
			// return the aabb
			return aabb;
		}
		return new AABB(new Vector2(0.0, 0.0), new Vector2(0.0, 0.0));
	}

	/**
	 * Returns an AABB that contains the maximal space in which
	 * the {@link Collidable} exists from the initial transform
	 * to the final transform.
	 * <p>
	 * This method takes the bounding circle, using the world center
	 * and rotation disc radius, at the initial and final transforms
	 * and creates an AABB containing both.
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB createSweptAABB() {
		return this.createSweptAABB(this.transform0, this.transform);
	}
	
	/**
	 * Creates a swept {@link AABB} from the given start and end {@link Transform}s
	 * for this {@link Body}.
	 * <p>
	 * This method may return a degenerate AABB, where the min == max, if the body 
	 * has not moved and does not have any fixtures.  If this body does have 
	 * fixtures, but didn't move, an AABB encompassing the initial and final center 
	 * points is returned.
	 * @param initialTransform the initial {@link Transform}
	 * @param finalTransform the final {@link Transform}
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB createSweptAABB(Transform initialTransform, Transform finalTransform) {
		// get the initial transform's world center
		Vector2 iCenter = initialTransform.getTransformed(this.mass.getCenter());
		// get the final transform's world center
		Vector2 fCenter = finalTransform.getTransformed(this.mass.getCenter());
		// return an AABB containing both points (expanded into circles by the
		// rotation disc radius)
		return new AABB(
				new Vector2(
					Math.min(iCenter.x, fCenter.x) - this.radius,
					Math.min(iCenter.y, fCenter.y) - this.radius),
				new Vector2(
					Math.max(iCenter.x, fCenter.x) + this.radius,
					Math.max(iCenter.y, fCenter.y) + this.radius));
	}
	
	/**
	 * Returns the change in position computed from last frame's transform
	 * and this frame's transform.
	 * @return Vector2
	 * @since 3.1.5
	 */
	public Vector2 getChangeInPosition() {
		return this.transform.getTranslation().subtract(this.transform0.getTranslation());
	}
	
	/**
	 * Returns the change in orientation computed from last frame's transform
	 * and this frame's transform.
	 * <p>
	 * This method will return a change in the range [0, 2&pi;).  This isn't as useful
	 * if the angular velocity is greater than 2&pi; per time step.  Since we don't have
	 * the timestep here, we can't compute the exact change in this case.
	 * @return double
	 * @since 3.1.5
	 */
	public double getChangeInOrientation() {
		double ri = this.transform0.getRotation();
		double rf = this.transform.getRotation();
		
		final double twopi = 2.0 * Math.PI;
		
		// put the angles in the range [0, 2pi] rather than [-pi, pi]
		if (ri < 0) ri += twopi;
		if (rf < 0) rf += twopi;
		
		// compute the difference
		double r = rf - ri;
		
		// determine which way the angular velocity was going so that
		// we know which angle is correct
		// check if the end is smaller than the start and for a positive velocity
		if (rf < ri && this.angularVelocity > 0) r += twopi;
		// check if the end is larger than the start and for a negative velocity
		if (rf > ri && this.angularVelocity < 0) r -= twopi;
		
		// return the rotation
		return r;
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
	 * Returns true if the given world space point is contained in this body.
	 * @param point the world space test point
	 * @return boolean
	 * @since 3.1.5
	 */
	public boolean contains(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			BodyFixture fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the linear velocity.
	 * <p>
	 * This method replaces the {@link Body#getVelocity(Vector2)} method.
	 * @return {@link Vector2}
	 * @since 3.1.5
	 */
	public Vector2 getLinearVelocity() {
		return this.velocity;
	}
	
	/**
	 * Returns the velocity of this body at the given world space point.
	 * <p>
	 * This method replaces the {@link Body#getVelocity(Vector2)} method.
	 * @param point the point in world space
	 * @return {@link Vector2}
	 * @since 3.1.5
	 */
	public Vector2 getLinearVelocity(Vector2 point) {
		// get the world space center point
		Vector2 c = this.getWorldCenter();
		// compute the r vector from the center of mass to the point
		Vector2 r = c.to(point);
		// compute the velocity
		return r.cross(this.angularVelocity).add(this.velocity);
	}
	
	/**
	 * Sets the linear velocity.
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * <p>
	 * This method replaces the {@link Body#setVelocity(Vector2)} method.
	 * @param velocity the desired velocity
	 * @throws NullPointerException if velocity is null
	 * @since 3.1.5
	 */
	public void setLinearVelocity(Vector2 velocity) {
		if (velocity == null) throw new NullPointerException(Messages.getString("dynamics.body.nullVelocity"));
		this.velocity.set(velocity);
	}

	/**
	 * Sets the linear velocity.
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * @param x the linear velocity along the x-axis
	 * @param y the linear velocity along the y-axis
	 * @since 3.1.5
	 */
	public void setLinearVelocity(double x, double y) {
		this.velocity.x = x;
		this.velocity.y = y;
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
	 * Returns the force applied in the last iteration.
	 * @return {@link Vector2}
	 */
	public Vector2 getForce() {
		return this.force;
	}
	
	/**
	 * Returns the total force currently stored in the force accumulator.
	 * @return {@link Vector2}
	 * @since 3.0.1
	 */
	public Vector2 getAccumulatedForce() {
		int fSize = this.forces.size();
		Vector2 force = new Vector2();
		for (int i = 0; i < fSize; i++) {
			Vector2 tf = this.forces.get(i).force;
			force.add(tf);
		}
		return force;
	}
	
	/**
	 * Returns the torque applied in the last iteration.
	 * @return double
	 */
	public double getTorque() {
		return this.torque;
	}

	/**
	 * Returns the total torque currently stored in the torque accumulator.
	 * @return double
	 * @since 3.0.1
	 */
	public double getAccumulatedTorque() {
		int tSize = this.torques.size();
		double torque = 0.0;
		for (int i = 0; i < tSize; i++) {
			torque += this.torques.get(i).torque;
		}
		return torque;
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
		if (linearDamping < 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.invalidLinearDamping"));
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
		if (angularDamping < 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.invalidAngularDamping"));
		this.angularDamping = angularDamping;
	}
	
	/**
	 * Returns the gravity scale.
	 * @return double
	 * @since 3.0.0
	 */
	public double getGravityScale() {
		return this.gravityScale;
	}
	
	/**
	 * Sets the gravity scale.
	 * @param scale the gravity scale for this body
	 * @since 3.0.0
	 */
	public void setGravityScale(double scale) {
		this.gravityScale = scale;
	}
	
	/**
	 * Returns a list of {@link Body}s connected
	 * by {@link Joint}s.
	 * <p>
	 * If a body is connected to another body with more
	 * than one joint, this method will return just one
	 * entry for the connected body.
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
			// get the other body
			Body other = je.getOther();
			// make sure that the body hasn't been added
			// to the list already
			if (!bodies.contains(other)) {
				bodies.add(other);
			}
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
	 * <p>
	 * If this body has multiple contact constraints with another body (which can
	 * happen when either body has multiple fixtures), this method will only return
	 * one entry for the in contact body.
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
				// get the other body
				Body other = ce.getOther();
				// make sure the body hasn't been added to 
				// the list already
				if (!bodies.contains(other)) {
					// add it to the list
					bodies.add(other);
				}
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
				List<Contact> contacts = constraint.getContacts();
				int csize = contacts.size();
				for (int j = 0; j < csize; j++) {
					// get the contact
					Contact contact = contacts.get(j);
					// create the contact point
					ContactPoint contactPoint = new ContactPoint(
							new ContactPointId(constraint.getId(), contact.getId()),
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
	
	// deprecated methods
	
	/**
	 * Applies the given force to this {@link Body} at the
	 * given point (torque).
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @param point the application point in world coordinates
	 * @return {@link Body} this body
	 * @throws NullPointerException if force or point is null
	 * @deprecated replaced with {@link #applyForce(Vector2, Vector2)} in 3.1.1
	 * @see #applyForce(Vector2, Vector2)
	 */
	@Deprecated
	public Body apply(Vector2 force, Vector2 point) {
		return this.applyForce(force, point);
	}
	
	/**
	 * Applies the given {@link Torque} to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param torque the torque
	 * @return {@link Body} this body
	 * @throws NullPointerException if torque is null
	 * @deprecated replaced with {@link #applyTorque(Torque)} in 3.1.1
	 * @see #applyTorque(Torque)
	 */
	@Deprecated
	public Body apply(Torque torque) {
		return this.applyTorque(torque);
	}
	
	/**
	 * Applies the given torque about the center of this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param torque the torque about the center
	 * @return {@link Body} this body
	 * @deprecated replaced with {@link #applyTorque(double)} in 3.1.1
	 * @see #applyTorque(double)
	 */
	@Deprecated
	public Body apply(double torque) {
		return this.applyTorque(torque);
	}
	
	/**
	 * Applies the given {@link Force} to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 * @deprecated replaced with {@link #applyForce(Force)} in 3.1.1
	 * @see #applyForce(Force)
	 */
	@Deprecated
	public Body apply(Force force) {
		return this.applyForce(force);
	}

	/**
	 * Applies the given force to this {@link Body}.
	 * <p>
	 * This method will wake-up the body if its sleeping.
	 * @param force the force
	 * @return {@link Body} this body
	 * @throws NullPointerException if force is null
	 * @deprecated replaced with {@link #applyForce(Vector2)} in 3.1.1
	 * @see #applyForce(Vector2)
	 */
	@Deprecated
	public Body apply(Vector2 force) {
		return this.applyForce(force);
	}

	/**
	 * Returns the velocity {@link Vector2}.
	 * @return {@link Vector2}
	 * @deprecated replaced by {@link Body#getLinearVelocity()} in 3.1.5
	 */
	@Deprecated
	public Vector2 getVelocity() {
		return this.getLinearVelocity();
	}

	/**
	 * Returns the velocity of this body at the given point on the body.
	 * @param point the point
	 * @return {@link Vector2}
	 * @since 3.0.1
	 * @deprecated replaced by {@link Body#getLinearVelocity(Vector2)} in 3.1.5
	 */
	@Deprecated
	public Vector2 getVelocity(Vector2 point) {
		return this.getLinearVelocity(point);
	}

	/**
	 * Sets the velocity {@link Vector2}.
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * @param velocity the velocity
	 * @throws NullPointerException if velocity is null
	 * @deprecated replaced by {@link Body#setLinearVelocity(Vector2)} in 3.1.5
	 */
	@Deprecated
	public void setVelocity(Vector2 velocity) {
		this.setLinearVelocity(velocity);
	}
}