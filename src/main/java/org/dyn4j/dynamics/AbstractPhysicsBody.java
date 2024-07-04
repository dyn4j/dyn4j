/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
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
import org.dyn4j.Ownable;
import org.dyn4j.collision.AbstractCollisionBody;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract implementation of the {@link PhysicsBody} interface.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public abstract class AbstractPhysicsBody extends AbstractCollisionBody<BodyFixture> implements PhysicsBody, CollisionBody<BodyFixture>, Transformable, DataContainer, Ownable {
	/** The {@link Mass} information */
	protected Mass mass;
	
	/** The current linear velocity */
	protected final Vector2 linearVelocity;

	/** The current angular velocity */
	protected double angularVelocity;

	/** The {@link AbstractPhysicsBody}'s linear damping */
	protected double linearDamping;
	
	/** The {@link AbstractPhysicsBody}'s angular damping */
	protected double angularDamping;
	
	/** The per body gravity scale factor */
	protected double gravityScale;

	/** True if the body is fast, small or both */
	protected boolean bullet;
	
	// at-rest detection/state
	
	/** True if at-rest detection is enabled */
	protected boolean atRestDetectionEnabled;
	
	/** True if the body is at-rest */
	protected boolean atRest;
	
	/** The time that the {@link PhysicsBody} has been at-rest */
	protected double atRestTime;

	// last iteration accumulated force/torque

	/** The current force */
	protected final Vector2 force;
	
	/** The current torque */
	protected double torque;
	
	// force/torque accumulators
	
	/** The force accumulator */
	protected final List<Force> forces;
	
	/** The torque accumulator */
	protected final List<Torque> torques;
	
	/**
	 * Default constructor.
	 */
	public AbstractPhysicsBody() {
		this(AbstractCollisionBody.TYPICAL_FIXTURE_COUNT);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a new {@link AbstractPhysicsBody} using the given estimated fixture count.
	 * Assignment of the initial fixture count allows sizing of internal structures
	 * for optimal memory/performance.  This estimated fixture count is <b>not</b> a
	 * limit on the number of fixtures.
	 * @param fixtureCount the estimated number of fixtures
	 * @throws IllegalArgumentException if fixtureCount less than zero
	 * @since 3.1.1
	 */
	public AbstractPhysicsBody(int fixtureCount) {
		super(fixtureCount);
		this.radius = 0.0;
		this.mass = new Mass();
		this.linearVelocity = new Vector2();
		this.angularVelocity = 0.0;
		this.force = new Vector2();
		this.torque = 0.0;
		// its common to apply a force or two to a body during a timestep
		// so 5 is a good trade off
		this.forces = new ArrayList<Force>(5);
		this.torques = new ArrayList<Torque>(5);
		// initialize the state
		this.atRestDetectionEnabled = true;
		this.atRest = false;
		this.bullet = false;
		this.atRestTime = 0.0;
		// other properties
		this.linearDamping = AbstractPhysicsBody.DEFAULT_LINEAR_DAMPING;
		this.angularDamping = AbstractPhysicsBody.DEFAULT_ANGULAR_DAMPING;
		this.gravityScale = 1.0;
	}
	
	/**
	 * Copy constructor.
	 * @param body the body to copy
	 * @since 6.0.0
	 */
	protected AbstractPhysicsBody(AbstractPhysicsBody body) {
		super(body);
		
		this.radius = body.radius;
		this.mass = body.mass.copy();
		this.linearVelocity = body.linearVelocity.copy();
		this.angularVelocity = body.angularVelocity;
		this.force = body.force.copy();
		this.torque = body.torque;
		this.atRestDetectionEnabled = body.atRestDetectionEnabled;
		this.atRest = body.atRest;
		this.bullet = body.bullet;
		this.atRestTime = body.atRestTime;
		this.linearDamping = body.linearDamping;
		this.angularDamping = body.angularDamping;
		this.gravityScale = body.gravityScale;
		
		this.forces = new ArrayList<Force>(5);
		this.torques = new ArrayList<Torque>(5);
		for (Force force : body.forces) {
			this.forces.add(force.copy());
		}
		for (Torque torque : body.torques) {
			this.torques.add(torque.copy());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Body[HashCode=").append(this.hashCode()).append("|Fixtures={");
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
		.append("|LinearVelocity=").append(this.linearVelocity)
		.append("|AngularVelocity=").append(this.angularVelocity)
		.append("|Force=").append(this.force)
		.append("|Torque=").append(this.torque)
		.append("|AccumulatedForce=").append(this.getAccumulatedForce())
		.append("|AccumulatedTorque=").append(this.getAccumulatedTorque())
		.append("|IsAtRestDetectionEnabled=").append(this.atRestDetectionEnabled)
		.append("|IsAtRest=").append(this.atRest)
		.append("|IsActive=").append(this.enabled)
		.append("|IsBullet=").append(this.bullet)
		.append("|LinearDamping=").append(this.linearDamping)
		.append("|AngularDamping").append(this.angularDamping)
		.append("|GravityScale=").append(this.gravityScale)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public BodyFixture addFixture(Convex convex) {
		return this.addFixture(convex, BodyFixture.DEFAULT_DENSITY, BodyFixture.DEFAULT_FRICTION, BodyFixture.DEFAULT_RESTITUTION);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#addFixture(org.dyn4j.geometry.Convex, double)
	 */
	@Override
	public BodyFixture addFixture(Convex convex, double density) {
		return this.addFixture(convex, density, BodyFixture.DEFAULT_FRICTION, BodyFixture.DEFAULT_RESTITUTION);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#addFixture(org.dyn4j.geometry.Convex, double, double, double)
	 */
	@Override
	public BodyFixture addFixture(Convex convex, double density, double friction, double restitution) {
		// make sure the convex shape is not null
		if (convex == null) 
			throw new ArgumentNullException("convex");
		
		// create the fixture
		BodyFixture fixture = new BodyFixture(convex);
		// set the properties
		fixture.setDensity(density);
		fixture.setFriction(friction);
		fixture.setRestitution(restitution);
		// add the fixture to the body
		super.addFixture(fixture);
		// wake the body up
		this.setAtRest(false);
		// return the fixture so the caller can configure it
		return fixture;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public List<BodyFixture> removeAllFixtures() {
		List<BodyFixture> fixtures = super.removeAllFixtures();
		// wake the body if something was removed
		if (fixtures.size() > 0) {
			this.setAtRest(false);
		}
		return fixtures;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public boolean removeFixture(BodyFixture fixture) {
		boolean removed = super.removeFixture(fixture);
		// if something was removed, then wake the body
		if (removed) {
			this.setAtRest(false);
		}
		return removed;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public BodyFixture removeFixture(int index) {
		BodyFixture bf = super.removeFixture(index);
		this.setAtRest(false);
		return bf;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public BodyFixture removeFixture(Vector2 point) {
		BodyFixture bf = super.removeFixture(point);
		// if it was removed, then wake the body
		if (bf != null) {
			this.setAtRest(false);
		}
		return bf;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public List<BodyFixture> removeFixtures(Vector2 point) {
		List<BodyFixture> fixtures = super.removeFixtures(point);
		// if anything was removed, then wake the body
		if (fixtures.size() > 0) {
			this.setAtRest(false);
		}
		return fixtures;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled && enabled) {
			this.setAtRest(false);
		}
		super.setEnabled(enabled);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#updateMass()
	 */
	@Override
	public AbstractPhysicsBody updateMass() {
		return this.setMass(this.mass.getType());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setMass(org.dyn4j.geometry.MassType)
	 */
	@Override
	public AbstractPhysicsBody setMass(MassType type) {
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
				// only include fixtures with density greater than zero
				BodyFixture fixture = this.fixtures.get(i);
				if (fixture.density > 0) {
					Mass mass = fixture.createMass();
					masses.add(mass);
				}
			}
			// the list of masses will be empty if all
			// fixtures have zero density
			if (masses.size() > 0) {
				this.mass = Mass.create(masses);
			} else {
				this.mass = new Mass();
			}
		}
		// set the type
		this.mass.setType(type);
		// compute the rotation disc radius
		this.setRotationDiscRadius(this.mass.getCenter());
		// wake the body up
		this.setAtRest(false);
		// return this body to facilitate chaining
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setMass(org.dyn4j.geometry.Mass)
	 */
	@Override
	public AbstractPhysicsBody setMass(Mass mass) {
		// make sure the mass is not null
		if (mass == null) 
			throw new ArgumentNullException("mass");
		
		// set the mass
		this.mass = mass;
		// compute the rotation disc radius
		this.setRotationDiscRadius(this.mass.getCenter());
		// wake the body up
		this.setAtRest(false);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setMassType(org.dyn4j.geometry.MassType)
	 */
	@Override
	public AbstractPhysicsBody setMassType(MassType type) {
		// check for null type
		if (type == null) 
			throw new ArgumentNullException("type");
		
		// wake the body up if the mass type is really changing
		if (type != this.mass.getType()) {
			this.setAtRest(false);
		}
		
		// otherwise just set the type
		this.mass.setType(type);
		// return this body
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getMass()
	 */
	@Override
	public Mass getMass() {
		return this.mass;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyForce(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public AbstractPhysicsBody applyForce(Vector2 force) {
		// check for null
		if (force == null) 
			throw new ArgumentNullException("force");
		
		// check the linear mass of the body
		if (this.mass.getMass() == 0.0) {
			// this means that applying a force will do nothing
			// so, just return
			return this;
		}
		// apply the force
		this.forces.add(new Force(force));
		// wake up the body
		this.setAtRest(false);
		// return this body to facilitate chaining
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyForce(org.dyn4j.dynamics.Force)
	 */
	@Override
	public AbstractPhysicsBody applyForce(Force force) {
		// check for null
		if (force == null) 
			throw new ArgumentNullException("force");
		
		// check the linear mass of the body
		if (this.mass.getMass() == 0.0) {
			// this means that applying a force will do nothing
			// so, just return
			return this;
		}
		// add the force to the list
		this.forces.add(force);
		// wake up the body
		this.setAtRest(false);
		// return this body to facilitate chaining
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyTorque(double)
	 */
	@Override
	public AbstractPhysicsBody applyTorque(double torque) {
		// check the angular mass of the body
		if (this.mass.getInertia() == 0.0) {
			// this means that applying a torque will do nothing
			// so, just return
			return this;
		}
		// apply the torque
		this.torques.add(new Torque(torque));
		// wake up the body
		this.setAtRest(false);
		// return this body
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyTorque(org.dyn4j.dynamics.Torque)
	 */
	@Override
	public AbstractPhysicsBody applyTorque(Torque torque) {
		// check for null
		if (torque == null) 
			throw new ArgumentNullException("torque");
		
		// check the angular mass of the body
		if (this.mass.getInertia() == 0.0) {
			// this means that applying a torque will do nothing
			// so, just return
			return this;
		}
		// add the torque to the list
		this.torques.add(torque);
		// wake up the body
		this.setAtRest(false);
		// return this body to facilitate chaining
		return this;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyForce(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2)
	 */
	@Override
	public AbstractPhysicsBody applyForce(Vector2 force, Vector2 point) {
		// check for null
		if (force == null) 
			throw new ArgumentNullException("force");
		
		if (point == null) 
			throw new ArgumentNullException("point");
		
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
			this.setAtRest(false);
		}
		// return this body to facilitate chaining
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyImpulse(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public AbstractPhysicsBody applyImpulse(Vector2 impulse) {
		// check for null
		if (impulse == null) 
			throw new ArgumentNullException("impulse");
		
		// get the inverse linear mass
		double invM = this.mass.getInverseMass();
		// check the linear mass
		if (invM == 0.0) {
			// this means that applying an impulse will do nothing
			// so, just return
			return this;
		}
		// apply the impulse immediately
		this.linearVelocity.add(impulse.x * invM, impulse.y * invM);
		// wake up the body
		this.setAtRest(false);
		// return this body
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyImpulse(double)
	 */
	@Override
	public AbstractPhysicsBody applyImpulse(double impulse) {
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
		this.setAtRest(false);
		// return this body
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#applyImpulse(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2)
	 */
	@Override
	public AbstractPhysicsBody applyImpulse(Vector2 impulse, Vector2 point) {
		// check for null
		if (impulse == null) 
			throw new ArgumentNullException("impulse");
		
		if (point == null) 
			throw new ArgumentNullException("point");
		
		boolean awaken = false;
		// get the inverse mass
		double invM = this.mass.getInverseMass();
		double invI = this.mass.getInverseInertia();
		// check the linear mass
		if (invM != 0.0) {
			// apply the impulse immediately
			this.linearVelocity.add(impulse.x * invM, impulse.y * invM);
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
			this.setAtRest(false);
		}
		// return this body
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#clearForce()
	 */
	@Override
	public void clearForce() {
		this.force.zero();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#clearAccumulatedForce()
	 */
	@Override
	public void clearAccumulatedForce() {
		this.forces.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#clearTorque()
	 */
	@Override
	public void clearTorque() {
		this.torque = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#clearAccumulatedTorque()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#integrateVelocity(org.dyn4j.geometry.Vector2, org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void integrateVelocity(Vector2 gravity, TimeStep timestep, Settings settings) {
		// only integrate dynamic bodies
		if (this.mass.getType() == MassType.INFINITE) {
			return;
		}
		
		double elapsedTime = timestep.dt;
		
		// accumulate the forces and torques
		this.accumulate(elapsedTime);
		
		// get the mass properties
		double mass = this.mass.getMass();
		double inverseMass = this.mass.getInverseMass();
		double inverseInertia = this.mass.getInverseInertia();
		
		// integrate force and torque to modify the velocity and
		// angular velocity (sympletic euler)
		// v1 = v0 + ((f / m) + g) * dt
		if (inverseMass > Epsilon.E) {
			// only perform this step if the body does not have
			// a fixed linear velocity
			
			// F = ma
			// Fg = mg
			// a = F / m
			// v1 = v0 + at
			// v1 = v0 + (Fg + F)t
			// v1 = v0 + (mg + F)t
			this.linearVelocity.x += elapsedTime * inverseMass * (gravity.x * this.gravityScale * mass + this.force.x);
			this.linearVelocity.y += elapsedTime * inverseMass * (gravity.y * this.gravityScale * mass + this.force.y);
		}
		
		// av1 = av0 + (t / I) * dt
		if (inverseInertia > Epsilon.E) {
			// only perform this step if the body does not have
			// a fixed angular velocity
			this.angularVelocity += inverseInertia * this.torque * elapsedTime;
		}
		
		// apply linear damping
		if (this.linearDamping != 0.0) {
			// Because DEFAULT_LINEAR_DAMPING is 0.0 apply linear damping only if needed
			double linear = 1.0 - elapsedTime * this.linearDamping;
			linear = Interval.clamp(linear, 0.0, 1.0);
			
			// inline body.velocity.multiply(linear);
			this.linearVelocity.x *= linear;
			this.linearVelocity.y *= linear;	
		}
		
		// apply angular damping
		double angular = 1.0 - elapsedTime * this.angularDamping;
		angular = Interval.clamp(angular, 0.0, 1.0);
		
		this.angularVelocity *= angular;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#integratePosition(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void integratePosition(TimeStep timestep, Settings settings) {
		double elapsedTime = timestep.dt;
		double maxTranslation = settings.getMaximumTranslation();
		double maxTranslationSquared = settings.getMaximumTranslationSquared();
		double maxRotation = settings.getMaximumRotation();
		
		// if the body isn't moving then don't bother
		if (this.isStatic()) {
			return;
		}
		
		// compute the translation and rotation for this time step
		double translationX = this.linearVelocity.x * elapsedTime;
		double translationY = this.linearVelocity.y * elapsedTime;
		double translationMagnitudeSquared = translationX * translationX + translationY * translationY;
		
		// make sure the translation is not over the maximum
		if (translationMagnitudeSquared > maxTranslationSquared) {
			double translationMagnitude = Math.sqrt(translationMagnitudeSquared);
			double ratio = maxTranslation / translationMagnitude;
			
			this.linearVelocity.multiply(ratio);

			translationX *= ratio;
			translationY *= ratio;
		}
		
		double rotation = this.angularVelocity * elapsedTime;
		
		// make sure the rotation is not over the maximum
		if (rotation > maxRotation) {
			double ratio = maxRotation / Math.abs(rotation);
			
			this.angularVelocity *= ratio;
			rotation *= ratio;
		}
		
		// apply the translation/rotation
		this.translate(translationX, translationY);
		this.rotateAboutCenter(rotation);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#updateAtRestTime(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public double updateAtRestTime(TimeStep timestep, Settings settings) {
		// see if the body is allowed to sleep
		if (this.atRestDetectionEnabled) {

			// just skip static bodies
			if (this.isStatic()) {
				return -1;
			}
			
			// get maximum velocities
			double maximumAtRestLinearVelocitySquared = settings.getMaximumAtRestLinearVelocitySquared();
			double maximumAtRestAngularVeclotiy = settings.getMaximumAtRestAngularVelocity();
			
			// check the linear and angular velocity
			if (this.linearVelocity.getMagnitudeSquared() > maximumAtRestLinearVelocitySquared || Math.abs(this.angularVelocity) > maximumAtRestAngularVeclotiy) {
				// if either the linear or angular velocity is above the 
				// threshold then reset the sleep time
				this.atRestTime = 0.0;
			} else {
				// then increment the sleep time
				this.atRestTime += timestep.dt;
			}
		} else {
			this.atRestTime = 0.0;
		}
		
		return this.atRestTime;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isStatic()
	 */
	@Override
	public boolean isStatic() {
		return this.mass.getType() == MassType.INFINITE &&
			   Math.abs(this.linearVelocity.x) <= Epsilon.E &&
			   Math.abs(this.linearVelocity.y) <= Epsilon.E &&
			   Math.abs(this.angularVelocity) <= Epsilon.E;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isKinematic()
	 */
	@Override
	public boolean isKinematic() {
		return this.mass.getType() == MassType.INFINITE &&
				   (Math.abs(this.linearVelocity.x) > Epsilon.E ||
				    Math.abs(this.linearVelocity.y) > Epsilon.E ||
				    Math.abs(this.angularVelocity) > Epsilon.E);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return this.mass.getType() != MassType.INFINITE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setAtRestDetectionEnabled(boolean)
	 */
	@Override
	public void setAtRestDetectionEnabled(boolean flag) {
		this.atRestDetectionEnabled = flag;
		if (!flag) {
			this.setAtRest(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isAtRestDetectionEnabled()
	 */
	@Override
	public boolean isAtRestDetectionEnabled() {
		return this.atRestDetectionEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isAtRest()
	 */
	@Override
	public boolean isAtRest() {
		return this.atRest;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setAtRest(boolean)
	 */
	@Override
	public void setAtRest(boolean flag) {
		if (flag) {
			this.atRest = true;
			this.linearVelocity.zero();
			this.angularVelocity = 0.0;
			this.forces.clear();
			this.torques.clear();
		} else {
			// check if the body is asleep
			if (this.atRest) {
				// if the body is asleep then wake it up
				this.atRestTime = 0.0;
				this.atRest = false;
			}
			// otherwise do nothing
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#isBullet()
	 */
	@Override
	public boolean isBullet() {
		return this.bullet;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setBullet(boolean)
	 */
	@Override
	public void setBullet(boolean flag) {
		this.bullet = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#createSweptAABB()
	 */
	@Override
	public AABB createSweptAABB() {
		AABB aabb = new AABB(0,0,0,0);
		this.computeSweptAABB(this.transform0, this.transform, aabb);
		return aabb;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#createSweptAABB(org.dyn4j.geometry.Transform, org.dyn4j.geometry.Transform)
	 */
	@Override
	public AABB createSweptAABB(Transform initialTransform, Transform finalTransform) {
		AABB aabb = new AABB(0,0,0,0);
		this.computeSweptAABB(initialTransform, finalTransform, aabb);
		return aabb;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#computeSweptAABB(org.dyn4j.geometry.AABB)
	 */
	@Override
	public void computeSweptAABB(AABB result) {
		this.computeSweptAABB(this.transform0, this.transform, result);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#computeSweptAABB(org.dyn4j.geometry.Transform, org.dyn4j.geometry.Transform, org.dyn4j.geometry.AABB)
	 */
	@Override
	public void computeSweptAABB(Transform initialTransform, Transform finalTransform, AABB result) {
		int size = this.fixtures.size();
		
		// check for zero fixtures and return
		// a degenerate AABB
		if (size == 0) {
			result.zero();
			return;
		}
		
		// get the initial transform's world center
		Vector2 iCenter = initialTransform.getTransformed(this.getLocalCenter());
		// get the final transform's world center
		Vector2 fCenter = finalTransform.getTransformed(this.getLocalCenter());
		
		// there are few ways to think about swept AABBs and its all about
		// trade-offs.  On one hand having the most tight fitting AABB would
		// be the most ideal, but generating it would be very difficult.  On
		// the other hand, if the AABB generated is too conservative it'll
		// produce a lot of false positives in the broadphase
		
		// so I opted for a hybrid approach based on some common scenarios
		// if the body isn't rotating and it has only one fixture then use
		// the fixture's AABB
		
		// note that just checking the start/end state of the transforms is not
		// sufficient for rotation as the body could completely rotate one revolution
		// in a single timestep
		
		// did the body rotate or does it have more than one fixture?
		if (initialTransform.getCost() != finalTransform.getCost() ||
			initialTransform.getSint() != finalTransform.getSint() ||
			this.angularVelocity != 0.0 ||
			size > 1) {
			
			// if the body rotated or has more than one fixture it's probably most
			// efficient to just create a really conservative AABB by using the center
			// points for the start and end position and the rotation radius

			AABB.setFromPoints(iCenter, fCenter, result);
			result.expand(this.radius * 2);
		} else if (initialTransform.getTranslationX() == finalTransform.getTranslationX() &&
				   initialTransform.getTranslationY() == finalTransform.getTranslationY()) {
			// if the body's rotation didn't change, it has only one fixture, and
			// it didn't translate (i.e. it's at rest)
			
			// in this scenario we can just return the AABB of the only fixture at either
			// the initial or final transform (they're the same)
			
			// this scenario is specific to at rest bodies (including static bodies)
			this.fixtures.get(0).getShape().computeAABB(initialTransform, result);
		} else {
			// if we get here, the body didn't rotate, but did translate, and has only
			// one fixture
			
			// for this scenario we can compute the AABB at the initial transform
			// then translate it to the final transform and then union them
			this.fixtures.get(0).getShape().computeAABB(initialTransform, result);
			AABB aabb1 = result.copy();
			
			// we can just translate the initial aabb since we know we had no rotation
			aabb1.translate(fCenter.x - iCenter.x, fCenter.y - iCenter.y);
			
			// then just union the two
			result.union(aabb1);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getChangeInPosition()
	 */
	@Override
	public Vector2 getChangeInPosition() {
		return this.transform.getTranslation().subtract(this.transform0.getTranslation());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getChangeInOrientation()
	 */
	@Override
	public double getChangeInOrientation() {
		double ri = this.transform0.getRotationAngle();
		double rf = this.transform.getRotationAngle();
		
		// special case of no rotation
		if (ri == rf) return 0.0;
		
		final double twopi = Geometry.TWO_PI;
		
		// put the angles in the range [0, 2pi] rather than [-pi, pi]
		if (ri < 0) ri += twopi;
		if (rf < 0) rf += twopi;
		
		// compute the difference
		double r = rf - ri;
		
		// special case for zero angular velocity
		if (this.angularVelocity == 0.0) {
			return r > Math.PI ? r - twopi 
					: r < -Math.PI ? r + twopi 
						: r;
		}
		
		// determine which way the angular velocity was going so that
		// we know which angle is correct
		// check if the end is smaller than the start and for a positive velocity
		if (rf < ri && this.angularVelocity > 0) r += twopi;
		// check if the end is larger than the start and for a negative velocity
		if (rf > ri && this.angularVelocity < 0) r -= twopi;
		
		// return the rotation
		return r;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getLocalCenter()
	 */
	@Override
	public Vector2 getLocalCenter() {
		return this.mass.getCenter();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getWorldCenter()
	 */
	@Override
	public Vector2 getWorldCenter() {
		return this.transform.getTransformed(this.mass.getCenter());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getLinearVelocity()
	 */
	@Override
	public Vector2 getLinearVelocity() {
		return this.linearVelocity;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getLinearVelocity(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public Vector2 getLinearVelocity(Vector2 point) {
		// get the world space center point
		Vector2 c = this.getWorldCenter();
		// compute the r vector from the center of mass to the point
		Vector2 r = c.to(point);
		// compute the velocity
		return r.cross(this.angularVelocity).add(this.linearVelocity);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setLinearVelocity(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void setLinearVelocity(Vector2 velocity) {
		if (velocity == null) 
			throw new ArgumentNullException("velocity");

		this.linearVelocity.set(velocity);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setLinearVelocity(double, double)
	 */
	@Override
	public void setLinearVelocity(double x, double y) {
		this.linearVelocity.x = x;
		this.linearVelocity.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getAngularVelocity()
	 */
	@Override
	public double getAngularVelocity() {
		return this.angularVelocity;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setAngularVelocity(double)
	 */
	@Override
	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getForce()
	 */
	@Override
	public Vector2 getForce() {
		return this.force.copy();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getAccumulatedForce()
	 */
	@Override
	public Vector2 getAccumulatedForce() {
		int fSize = this.forces.size();
		Vector2 force = new Vector2();
		for (int i = 0; i < fSize; i++) {
			Vector2 tf = this.forces.get(i).force;
			force.add(tf);
		}
		return force;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getTorque()
	 */
	@Override
	public double getTorque() {
		return this.torque;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getAccumulatedTorque()
	 */
	@Override
	public double getAccumulatedTorque() {
		int tSize = this.torques.size();
		double torque = 0.0;
		for (int i = 0; i < tSize; i++) {
			torque += this.torques.get(i).torque;
		}
		return torque;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getLinearDamping()
	 */
	@Override
	public double getLinearDamping() {
		return this.linearDamping;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setLinearDamping(double)
	 */
	@Override
	public void setLinearDamping(double linearDamping) {
		if (linearDamping < 0) 
			throw new ValueOutOfRangeException("linearDamping", linearDamping, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (linearDamping != this.linearDamping) {
			this.setAtRest(false);
		}
		
		this.linearDamping = linearDamping;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getAngularDamping()
	 */
	@Override
	public double getAngularDamping() {
		return this.angularDamping;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setAngularDamping(double)
	 */
	@Override
	public void setAngularDamping(double angularDamping) {
		if (angularDamping < 0) 
			throw new ValueOutOfRangeException("angularDamping", angularDamping, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (angularDamping != this.angularDamping) {
			this.setAtRest(false);
		}
		
		this.angularDamping = angularDamping;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#getGravityScale()
	 */
	@Override
	public double getGravityScale() {
		return this.gravityScale;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.PhysicsBody#setGravityScale(double)
	 */
	@Override
	public void setGravityScale(double scale) {
		if (scale != this.gravityScale) {
			this.setAtRest(false);
		}
		
		this.gravityScale = scale;
	}
}