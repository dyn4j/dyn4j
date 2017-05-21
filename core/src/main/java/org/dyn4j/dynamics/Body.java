/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.collision.AbstractCollidable;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.ContactPointId;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents a physical {@link Body}.
 * <p>
 * A {@link Body} typically has at least one {@link BodyFixture} attached to it. 
 * the {@link BodyFixture}s represent the shape of the body.  When a body 
 * is first created the body is a shapeless infinite mass body.  Add fixtures to
 * the body using the <code>addFixture</code> methods.
 * <p>
 * Use the {@link #setMass(org.dyn4j.geometry.MassType)} methods to calculate the 
 * mass of the entire {@link Body} given the currently attached
 * {@link BodyFixture}s.  The {@link #setMass(Mass)} method can be used to set
 * the mass directly.  Use the {@link #setMassType(org.dyn4j.geometry.MassType)}
 * method to toggle the mass type between the special types.
 * <p>
 * The coefficient of friction and restitution and the linear and angular damping
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
 * A {@link Body} flagged as a Bullet will be checked for tunneling depending on the CCD
 * setting in the world's {@link Settings}.  Use this if the body is a fast moving
 * body, but be careful as this will incur a performance hit.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class Body extends AbstractCollidable<BodyFixture> implements Collidable<BodyFixture>, Transformable, DataContainer {
	/** The default linear damping; value = {@value #DEFAULT_LINEAR_DAMPING} */
	public static final double DEFAULT_LINEAR_DAMPING = 0.0;
	
	/** The default angular damping; value = {@value #DEFAULT_ANGULAR_DAMPING} */
	public static final double DEFAULT_ANGULAR_DAMPING 	= 0.01;
	
	/** The state flag for allowing automatic sleeping */
	private static final int AUTO_SLEEP = 1;
	
	/** The state flag for the {@link Body} being asleep */
	private static final int ASLEEP = 2;
	
	/** The state flag for the {@link Body} being active (out of bounds for example) */
	private static final int ACTIVE = 4;
	
	/** The state flag indicating the {@link Body} has been added to an {@link Island} */
	private static final int ISLAND = 8;
	
	/** The state flag indicating the {@link Body} is a really fast object and requires CCD */
	private static final int BULLET = 16;
	
	/** The {@link Mass} information */
	protected Mass mass;
	
	/** The current linear velocity */
	protected Vector2 velocity;

	/** The current angular velocity */
	protected double angularVelocity;

	/** The {@link Body}'s linear damping */
	protected double linearDamping;
	
	/** The {@link Body}'s angular damping */
	protected double angularDamping;
	
	/** The per body gravity scale factor */
	protected double gravityScale;

	// internal

	/** The beginning transform for CCD */
	Transform transform0;
	
	/** The {@link Body}'s state */
	private int state;
	
	/** The world this body belongs to */
	World world;
	
	/** The time that the {@link Body} has been waiting to be put sleep */
	double sleepTime;

	// last iteration accumulated force/torque

	/** The current force */
	Vector2 force;
	
	/** The current torque */
	double torque;
	
	// force/torque accumulators
	
	/** The force accumulator */
	final List<Force> forces;
	
	/** The torque accumulator */
	final List<Torque> torques;
	
	// interaction graph
	
	/** The {@link Body}'s contacts */
	final List<ContactEdge> contacts;
	
	/** The {@link Body}'s joints */
	final List<JointEdge> joints;
	
	/**
	 * Default constructor.
	 */
	public Body() {
		this(AbstractCollidable.TYPICAL_FIXTURE_COUNT);
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
		super(fixtureCount);
		this.world = null;
		this.radius = 0.0;
		this.mass = new Mass();
		this.transform0 = new Transform();
		this.velocity = new Vector2();
		this.angularVelocity = 0.0;
		this.force = new Vector2();
		this.torque = 0.0;
		// its common to apply a force or two to a body during a timestep
		// so 1 is a good trade off
		this.forces = new ArrayList<Force>(1);
		this.torques = new ArrayList<Torque>(1);
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
		this.contacts = new ArrayList<ContactEdge>(Collisions.getEstimatedCollisionsPerObject());
		// its more common that bodies do not have joints attached
		// then they do, so by default don't allocate anything
		// for the joints list
		this.joints = new ArrayList<JointEdge>(0);
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
		.append("|IsBullet=").append(this.isBullet())
		.append("|LinearDamping=").append(this.linearDamping)
		.append("|AngularDamping").append(this.angularDamping)
		.append("|GravityScale=").append(this.gravityScale)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#addFixture(org.dyn4j.geometry.Convex)
	 */
	public BodyFixture addFixture(Convex convex) {
		return this.addFixture(convex, BodyFixture.DEFAULT_DENSITY, BodyFixture.DEFAULT_FRICTION, BodyFixture.DEFAULT_RESTITUTION);
	}
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link Body}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #updateMass()}
	 * or {@link #setMass(MassType)} method to compute the new total
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
		return this.addFixture(convex, density, BodyFixture.DEFAULT_FRICTION, BodyFixture.DEFAULT_RESTITUTION);
	}
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link Body}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #updateMass()}
	 * or {@link #setMass(MassType)} method to compute the new total
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
		// add the fixture to the broadphase
		if (this.world != null) {
			this.world.broadphaseDetector.add(this, fixture);
		}
		// return the fixture so the caller can configure it
		return fixture;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#addFixture(org.dyn4j.collision.Fixture)
	 */
	public Body addFixture(BodyFixture fixture) {
		// make sure neither is null
		if (fixture == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullFixture"));
		// add the shape and mass to the respective lists
		this.fixtures.add(fixture);
		// add the fixture to the broadphase
		if (this.world != null) {
			this.world.broadphaseDetector.add(this, fixture);
		}
		// return this body to facilitate chaining
		return this;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.AbstractCollidable#removeFixture(org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean removeFixture(BodyFixture fixture) {
		if (this.world != null) {
			this.world.broadphaseDetector.remove(this, fixture);
		}
		return super.removeFixture(fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.AbstractCollidable#removeFixture(int)
	 */
	@Override
	public BodyFixture removeFixture(int index) {
		BodyFixture fixture = super.removeFixture(index);
		if (this.world != null) {
			this.world.broadphaseDetector.remove(this, fixture);
		}
		return fixture;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.AbstractCollidable#removeFixture(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public BodyFixture removeFixture(Vector2 point) {
		BodyFixture fixture = super.removeFixture(point);
		if (this.world != null) {
			this.world.broadphaseDetector.remove(this, fixture);
		}
		return fixture;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.AbstractCollidable#removeAllFixtures()
	 */
	@Override
	public List<BodyFixture> removeAllFixtures() {
		List<BodyFixture> fixtures = super.removeAllFixtures();
		int size = fixtures.size();
		if (this.world != null) {
			for (int i = 0; i < size; i++) {
				this.world.broadphaseDetector.remove(this, fixtures.get(i));
			}
		}
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.AbstractCollidable#removeFixtures(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public List<BodyFixture> removeFixtures(Vector2 point) {
		List<BodyFixture> fixtures = super.removeFixtures(point);
		int size = fixtures.size();
		if (this.world != null) {
			for (int i = 0; i < size; i++) {
				this.world.broadphaseDetector.remove(this, fixtures.get(i));
			}
		}
		return fixtures;
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
	 * @deprecated removed in 3.2.0 use {@link #setMass(MassType)} instead
	 */
	@Deprecated
	public Body setMass() {
		return this.setMass(MassType.NORMAL);
	}
	
	/**
	 * This is a shortcut method for the {@link #setMass(org.dyn4j.geometry.MassType)}
	 * method that will use the current mass type as the mass type and
	 * then recompute the mass from the body's fixtures.
	 * @return {@link Body} this body
	 * @since 3.2.0
	 * @see #setMass(org.dyn4j.geometry.MassType)
	 */
	public Body updateMass() {
		return this.setMass(this.mass.getType());
	}
	
	/**
	 * This method should be called after fixture modification
	 * is complete.
	 * <p>
	 * This method will calculate a total mass for the body 
	 * given the masses of the attached fixtures.
	 * <p>
	 * A {@link org.dyn4j.geometry.MassType} can be used to create special mass
	 * types.
	 * @param type the mass type
	 * @return {@link Body} this body
	 */
	public Body setMass(MassType type) {
		// check for null
		if (type == null) {
			type = this.mass.getType();
		}
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
	 * Explicitly sets this {@link Body}'s mass information.
	 * @param mass the new {@link Mass}
	 * @return {@link Body} this body
	 * @throws NullPointerException if the given mass is null
	 */
	public Body setMass(Mass mass) {
		// make sure the mass is not null
		if (mass == null) throw new NullPointerException(Messages.getString("dynamics.body.nullMass"));
		// set the mass
		this.mass = mass;
		// compute the rotation disc radius
		this.setRotationDiscRadius();
		return this;
	}
	
	/**
	 * Sets the {@link org.dyn4j.geometry.MassType} of this {@link Body}.
	 * <p>
	 * This method does not compute/recompute the mass of the body but solely
	 * sets the mass type to one of the special types.
	 * <p>
	 * Since its possible to create a {@link Mass} object with zero mass and/or
	 * zero inertia (<code>Mass m = new Mass(new Vector2(), 0, 0);</code> for example), setting the type 
	 * to something other than MassType.INFINITE can have undefined results.
	 * @param type the desired type
	 * @return {@link Body} this body
	 * @throws NullPointerException if the given mass type is null
	 * @since 2.2.3
	 */
	public Body setMassType(MassType type) {
		// check for null type
		if (type == null) throw new NullPointerException(Messages.getString("dynamics.body.nullMassType"));
		// otherwise just set the type
		this.mass.setType(type);
		// return this body
		return this;
	}
	
	/**
	 * Computes the rotation disc for this {@link Body}.
	 * <p>
	 * This method requires that the center of mass be computed first.
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
	void accumulate(double elapsedTime) {
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
				this.force.add(force.force);
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
				this.torque += torque.torque;
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
		return this.mass.getType() != MassType.INFINITE;
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
	boolean isOnIsland() {
		return (this.state & Body.ISLAND) == Body.ISLAND;
	}
	
	/**
	 * Sets the flag indicating that the {@link Body} has been added to an {@link Island}.
	 * @param flag true if the {@link Body} has been added to an {@link Island}
	 */
	void setOnIsland(boolean flag) {
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
			if (je.other == body) {
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
			if (je.other == body) {
				// get the joint
				Joint joint = je.interaction;
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
			if (ce.other == body) {
				// if so then return true
				return true;
			}
		}
		// if we get here then we know no contact exists
		return false;
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
	 * Returns the linear velocity.
	 * @return {@link Vector2}
	 * @since 3.1.5
	 */
	public Vector2 getLinearVelocity() {
		return this.velocity;
	}
	
	/**
	 * Returns the velocity of this body at the given world space point.
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
	 * Sets the angular velocity in radians per second
	 * <p>
	 * Call the {@link #setAsleep(boolean)} method to wake up the {@link Body}
	 * if the {@link Body} is asleep and the velocity is not zero.
	 * @param angularVelocity the angular velocity in radians per second
	 */
	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
	
	/**
	 * Returns the force applied in the last iteration.
	 * <p>
	 * This is the accumulated force from the last iteration.
	 * @return {@link Vector2}
	 */
	public Vector2 getForce() {
		return this.force.copy();
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
	 * <p>
	 * This is the accumulated torque from the last iteration.
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
	 * @see #setLinearDamping(double)
	 */
	public double getLinearDamping() {
		return this.linearDamping;
	}

	/**
	 * Sets the linear damping.
	 * <p>
	 * Linear damping is used to reduce the linear velocity over time.  The default is
	 * zero and larger values will cause the linear velocity to reduce faster.
	 * @param linearDamping the linear damping; must be greater than or equal to zero
	 * @throws IllegalArgumentException if linearDamping is less than zero
	 */
	public void setLinearDamping(double linearDamping) {
		if (linearDamping < 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.invalidLinearDamping"));
		this.linearDamping = linearDamping;
	}
	
	/**
	 * Returns the angular damping.
	 * @return double
	 * @see #setAngularDamping(double)
	 */
	public double getAngularDamping() {
		return this.angularDamping;
	}
	
	/**
	 * Sets the angular damping.
	 * <p>
	 * Angular damping is used to reduce the angular velocity over time.  The default is
	 * zero and larger values will cause the angular velocity to reduce faster.
	 * @param angularDamping the angular damping; must be greater than or equal to zero
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
	 * @see #setGravityScale(double)
	 */
	public double getGravityScale() {
		return this.gravityScale;
	}
	
	/**
	 * Sets the gravity scale.
	 * <p>
	 * The gravity scale is a multiplier applied to the acceleration due to
	 * gravity before applying the force of gravity to the body.  This allows
	 * bodies to be affected differently under the same gravity.
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
			Body other = je.other;
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
			joints.add(je.interaction);
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
			ContactConstraint constraint = ce.interaction;
			if (sensed == constraint.isSensor()) {
				// get the other body
				Body other = ce.other;
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
			ContactConstraint constraint = ce.interaction;
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
							constraint.getBody1(), constraint.getFixture1(),
							constraint.getBody2(), constraint.getFixture2(),
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