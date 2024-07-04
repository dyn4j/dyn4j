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

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an object in a simulation that reacts as defined by newtonian mechanics.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public interface PhysicsBody extends CollisionBody<BodyFixture>, Transformable, Shiftable, DataContainer, Ownable {
	/** The default linear damping; value = {@link #DEFAULT_LINEAR_DAMPING} */
	public static final double DEFAULT_LINEAR_DAMPING = 0.0;
	
	/** The default angular damping; value = {@link #DEFAULT_ANGULAR_DAMPING} */
	public static final double DEFAULT_ANGULAR_DAMPING 	= 0.01;
	
	/**
	 * {@inheritDoc}
	 * @return {@link PhysicsBody}
	 * @since 6.0.0
	 */
	public PhysicsBody copy();
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link PhysicsBody}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #updateMass()}
	 * or {@link #setMass(MassType)} method to compute the new total
	 * {@link Mass} for the body.
	 * <p>
	 * This is a convenience method for setting the density of a {@link BodyFixture}.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param convex the {@link Convex} {@link Shape} to add to the {@link PhysicsBody}
	 * @param density the density of the shape in kg/m<sup>2</sup>; in the range (0.0, &infin;]
	 * @return {@link BodyFixture} the fixture created using the given {@link Shape} and added to the {@link PhysicsBody}
	 * @throws NullPointerException if convex is null
	 * @throws IllegalArgumentException if density is less than or equal to zero; if friction or restitution is less than zero
	 * @see #addFixture(Convex)
	 * @see #addFixture(Convex, double, double, double)
	 * @since 3.1.5
	 */
	public BodyFixture addFixture(Convex convex, double density);
	
	/**
	 * Creates a {@link BodyFixture} for the given {@link Convex} {@link Shape},
	 * adds it to the {@link PhysicsBody}, and returns it for configuration.
	 * <p>
	 * After adding or removing fixtures make sure to call the {@link #updateMass()}
	 * or {@link #setMass(MassType)} method to compute the new total
	 * {@link Mass} for the body.
	 * <p>
	 * This is a convenience method for setting the properties of a {@link BodyFixture}.
	 * Use the {@link BodyFixture#DEFAULT_DENSITY}, {@link BodyFixture#DEFAULT_FRICTION},
	 * and {@link BodyFixture#DEFAULT_RESTITUTION} values if you need to only set one
	 * of these properties.  
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param convex the {@link Convex} {@link Shape} to add to the {@link PhysicsBody}
	 * @param density the density of the shape in kg/m<sup>2</sup>; in the range (0.0, &infin;]
	 * @param friction the coefficient of friction; in the range [0.0, &infin;]
	 * @param restitution the coefficient of restitution; in the range [0.0, &infin;]
	 * @return {@link BodyFixture} the fixture created using the given {@link Shape} and added to the {@link PhysicsBody}
	 * @throws NullPointerException if convex is null
	 * @throws IllegalArgumentException if density is less than or equal to zero; if friction or restitution is less than zero
	 * @see #addFixture(Convex)
	 * @see #addFixture(Convex, double)
	 * @since 3.1.1
	 */
	public BodyFixture addFixture(Convex convex, double density, double friction, double restitution);
	
	/**
	 * This is a shortcut method for the {@link #setMass(org.dyn4j.geometry.MassType)}
	 * method that will use the current mass type as the mass type and
	 * then recompute the mass from the body's fixtures.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @return {@link PhysicsBody} this body
	 * @since 3.2.0
	 * @see #setMass(org.dyn4j.geometry.MassType)
	 */
	public PhysicsBody updateMass();
	
	/**
	 * This method should be called after fixture modification
	 * is complete.
	 * <p>
	 * This method will calculate a total mass for the body 
	 * given the masses of the attached fixtures.
	 * <p>
	 * A {@link org.dyn4j.geometry.MassType} can be used to create special mass
	 * types.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param type the mass type
	 * @return {@link PhysicsBody} this body
	 */
	public PhysicsBody setMass(MassType type);
	
	/**
	 * Explicitly sets this {@link PhysicsBody}'s mass information.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param mass the new {@link Mass}
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if the given mass is null
	 */
	public PhysicsBody setMass(Mass mass);
	
	/**
	 * Sets the {@link org.dyn4j.geometry.MassType} of this {@link PhysicsBody}.
	 * <p>
	 * This method does not compute/recompute the mass of the body but solely
	 * sets the mass type to one of the special types.
	 * <p>
	 * Since its possible to create a {@link Mass} object with zero mass and/or
	 * zero inertia (<code>Mass m = new Mass(new Vector2(), 0, 0);</code> for example), setting the type 
	 * to something other than MassType.INFINITE can have undefined results.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param type the desired type
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if the given mass type is null
	 * @since 2.2.3
	 */
	public PhysicsBody setMassType(MassType type);
	
	/**
	 * Returns this {@link PhysicsBody}'s mass information.
	 * @return {@link Mass}
	 */
	public Mass getMass();
	
	/**
	 * Applies the given force to this {@link PhysicsBody}.
	 * <p>
	 * This method does not apply the force if this body 
	 * returns zero from the {@link Mass#getMass()} method.
	 * <p>
	 * The force is not applied immediately, but instead stored in the 
	 * force accumulator ({@link #getAccumulatedForce()}).  This is to 
	 * preserve the last time step's computed force ({@link #getForce()}.
	 * <p>
	 * The force is assumed to be in world space coordinates.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param force the force
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if force is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyForce(Vector2 force);
	
	/**
	 * Applies the given {@link Force} to this {@link PhysicsBody}.
	 * <p>
	 * This method does not apply the force if this body 
	 * returns zero from the {@link Mass#getMass()} method.
	 * <p>
	 * The force is not applied immediately, but instead stored in the 
	 * force accumulator ({@link #getAccumulatedForce()}).  This is to 
	 * preserve the last time step's computed force ({@link #getForce()}.
	 * <p>
	 * The force is assumed to be in world space coordinates.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param force the force
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if force is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyForce(Force force);
	
	/**
	 * Applies the given torque about the center of this {@link PhysicsBody}.
	 * <p>
	 * This method does not apply the torque if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * The torque is not applied immediately, but instead stored in the 
	 * torque accumulator ({@link #getAccumulatedTorque()}).  This is to 
	 * preserve the last time step's computed torque ({@link #getTorque()}.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param torque the torque about the center
	 * @return {@link PhysicsBody} this body
	 * @since 3.1.1
	 */
	public PhysicsBody applyTorque(double torque);
	
	/**
	 * Applies the given {@link Torque} to this {@link PhysicsBody}.
	 * <p>
	 * This method does not apply the torque if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * The torque is not applied immediately, but instead stored in the 
	 * torque accumulator ({@link #getAccumulatedTorque()}).  This is to 
	 * preserve the last time step's computed torque ({@link #getTorque()}.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param torque the torque
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if torque is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyTorque(Torque torque);

	/**
	 * Applies the given force to this {@link PhysicsBody} at the
	 * given point (torque).
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
	 * <p>
	 * The force and point are assumed to be in world space coordinates.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param force the force
	 * @param point the application point in world coordinates
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if force or point is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyForce(Vector2 force, Vector2 point);
	
	/**
	 * Applies a linear impulse to this {@link PhysicsBody} at its center of mass.
	 * <p>
	 * This method does not apply the impulse if this body's mass 
	 * returns zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * <p>
	 * The impulse is assumed to be in world space coordinates.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param impulse the impulse to apply
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if impulse is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyImpulse(Vector2 impulse);
	
	/**
	 * Applies an angular impulse to this {@link PhysicsBody} about its center of mass.
	 * <p>
	 * This method does not apply the impulse if this body's inertia 
	 * returns zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param impulse the impulse to apply
	 * @return {@link PhysicsBody} this body
	 * @since 3.1.1
	 */
	public PhysicsBody applyImpulse(double impulse);
	
	/**
	 * Applies an impulse to this {@link PhysicsBody} at the given point.
	 * <p>
	 * This method does not apply the linear impulse if this body 
	 * returns zero from the {@link Mass#getMass()} method nor 
	 * will it apply the angular impulse if this body returns 
	 * zero from the {@link Mass#getInertia()} method.
	 * <p>
	 * <b>NOTE:</b> Applying an impulse differs from applying a force and/or torque. Forces
	 * and torques are stored in accumulators, but impulses are applied to the
	 * velocities of the body immediately.
	 * <p>
	 * The impulse and point are assumed to be in world space coordinates.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param impulse the impulse to apply
	 * @param point the world space point to apply the impulse
	 * @return {@link PhysicsBody} this body
	 * @throws NullPointerException if impulse or point is null
	 * @since 3.1.1
	 */
	public PhysicsBody applyImpulse(Vector2 impulse, Vector2 point);
	
	/**
	 * Clears the last time step's force on the {@link PhysicsBody}.
	 */
	public void clearForce();
	
	/**
	 * Clears the forces stored in the force accumulator.
	 * <p>
	 * Renamed from clearForces (3.0.0 and below).
	 * @since 3.0.1
	 */
	public void clearAccumulatedForce();
	
	/**
	 * Clears the last time step's torque on the {@link PhysicsBody}.
	 */
	public void clearTorque();
	
	/**
	 * Clears the torques stored in the torque accumulator.
	 * <p>
	 * Renamed from clearTorques (3.0.0 and below).
	 * @since 3.0.1
	 */
	public void clearAccumulatedTorque();

	/**
	 * Integrates the forces, torques, and gravity to update the linear
	 * and angular velocity of this body.
	 * @param gravity the world gravity
	 * @param timestep the timestep information
	 * @param settings the world settings
	 */
	public void integrateVelocity(Vector2 gravity, TimeStep timestep, Settings settings);
	
	/**
	 * Integrates the linear and angular velocities to update the position
	 * and rotation of this body
	 * @param timestep the timestep information
	 * @param settings the world settings
	 */
	public void integratePosition(TimeStep timestep, Settings settings);
	
	/**
	 * Updates the at-rest time for this body based on the given timestep and
	 * returns the current at-rest time.
	 * <p>
	 * For {@link #isStatic()} bodies, this method will return -1 to indicate
	 * that this body can always be at-rest since it's velocity is zero and
	 * cannot be moved.
	 * @return double
	 * @param timestep the timestep information
	 * @param settings the world settings
	 */
	public double updateAtRestTime(TimeStep timestep, Settings settings);
	
	/**
	 * Returns true if this body's mass type is {@link MassType#INFINITE} and
	 * the linear and angular velocity are close to zero (as determined by
	 * {@link Epsilon#E}.
	 * @return boolean
	 */
	public boolean isStatic();
	
	/**
	 * Returns true if this body's mass type is {@link MassType#INFINITE} and
	 * either the linear or angular velocity are NOT zero (i.e. it's moving).
	 * @return boolean
	 */
	public boolean isKinematic();
	
	/**
	 * Returns true if this body's mass type is NOT {@link MassType#INFINITE}.
	 * @return boolean
	 */
	public boolean isDynamic();
	
	/**
	 * Determines whether this {@link PhysicsBody} can participate in automatic
	 * at-rest detection.
	 * @param flag true if it should
	 * @since 4.0.0
	 */
	public void setAtRestDetectionEnabled(boolean flag);
	
	/**
	 * Returns true if this {@link PhysicsBody} can participate in automatic
	 * at-rest detection.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isAtRestDetectionEnabled();

	/**
	 * Sets whether this {@link PhysicsBody} is at-rest or not.
	 * <p>
	 * If flag is true, this body's velocity, angular velocity,
	 * force, torque, and accumulators are cleared.
	 * @param flag true if the body should be at-rest
	 * @since 4.0.0
	 */
	public void setAtRest(boolean flag);
	
	/**
	 * Returns true if this {@link PhysicsBody} is at-rest.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isAtRest();

	/**
	 * Sets the bullet flag for this {@link PhysicsBody}.
	 * <p>
	 * A bullet is a very fast moving body that requires
	 * continuous collision detection with <b>all</b> other
	 * {@link PhysicsBody}s to ensure that no collisions are missed.
	 * @param flag true if this {@link PhysicsBody} is a bullet
	 * @since 1.2.0
	 */
	public void setBullet(boolean flag);
	
	/**
	 * Returns true if this {@link PhysicsBody} is a bullet.
	 * @see #setBullet(boolean)
	 * @return boolean
	 * @since 1.2.0
	 */
	public boolean isBullet();
	
	/**
	 * Returns the change in position computed from last frame's transform
	 * and this frame's transform.
	 * @return Vector2
	 * @since 3.1.5
	 */
	public Vector2 getChangeInPosition();
	
	/**
	 * Returns the change in orientation computed from last frame's transform
	 * and this frame's transform.
	 * <p>
	 * This method will return a change in the range [0, 2&pi;).  This isn't as useful
	 * if the angular velocity is greater than 2&pi; per time step.  Since we don't have
	 * the timestep here, we can't compute the exact change in this case.
	 * <p>
	 * If the angular velocity is zero, this method returns the minimum difference
	 * in orientation.
	 * @return double
	 * @since 3.1.5
	 */
	public double getChangeInOrientation();
	
	/**
	 * Returns the linear velocity.
	 * @return {@link Vector2}
	 * @since 3.1.5
	 */
	public Vector2 getLinearVelocity();
	
	/**
	 * Returns the velocity of this body at the given world space point.
	 * @param point the point in world space
	 * @return {@link Vector2}
	 * @since 3.1.5
	 */
	public Vector2 getLinearVelocity(Vector2 point);
	
	/**
	 * Sets the linear velocity.
	 * <p>
	 * Call the {@link #setAtRest(boolean)} method to wake up the {@link PhysicsBody}
	 * if the {@link PhysicsBody} is asleep and the velocity is not zero.
	 * @param velocity the desired velocity
	 * @throws NullPointerException if velocity is null
	 * @since 3.1.5
	 */
	public void setLinearVelocity(Vector2 velocity);

	/**
	 * Sets the linear velocity.
	 * <p>
	 * Call the {@link #setAtRest(boolean)} method to wake up the {@link PhysicsBody}
	 * if the {@link PhysicsBody} is asleep and the velocity is not zero.
	 * @param x the linear velocity along the x-axis
	 * @param y the linear velocity along the y-axis
	 * @since 3.1.5
	 */
	public void setLinearVelocity(double x, double y);
	
	/**
	 * Returns the angular velocity.
	 * @return double
	 */
	public double getAngularVelocity();

	/**
	 * Sets the angular velocity in radians per second
	 * <p>
	 * Call the {@link #setAtRest(boolean)} method to wake up the {@link PhysicsBody}
	 * if the {@link PhysicsBody} is asleep and the velocity is not zero.
	 * @param angularVelocity the angular velocity in radians per second
	 */
	public void setAngularVelocity(double angularVelocity);
	
	/**
	 * Returns the force applied in the last iteration.
	 * <p>
	 * This is the accumulated force from the last iteration.
	 * @return {@link Vector2}
	 */
	public Vector2 getForce();
	
	/**
	 * Returns the total force currently stored in the force accumulator.
	 * @return {@link Vector2}
	 * @since 3.0.1
	 */
	public Vector2 getAccumulatedForce();
	
	/**
	 * Returns the torque applied in the last iteration.
	 * <p>
	 * This is the accumulated torque from the last iteration.
	 * @return double
	 */
	public double getTorque();

	/**
	 * Returns the total torque currently stored in the torque accumulator.
	 * @return double
	 * @since 3.0.1
	 */
	public double getAccumulatedTorque();
	
	/**
	 * Returns the linear damping.
	 * @return double
	 * @see #setLinearDamping(double)
	 */
	public double getLinearDamping();

	/**
	 * Sets the linear damping.
	 * <p>
	 * Linear damping is used to reduce the linear velocity over time.  The default is
	 * zero and larger values will cause the linear velocity to reduce faster.
	 * <p>
	 * The units are seconds<sup>-1</sup>. 
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param linearDamping the linear damping; must be greater than or equal to zero
	 * @throws IllegalArgumentException if linearDamping is less than zero
	 */
	public void setLinearDamping(double linearDamping);
	
	/**
	 * Returns the angular damping.
	 * @return double
	 * @see #setAngularDamping(double)
	 */
	public double getAngularDamping();
	
	/**
	 * Sets the angular damping.
	 * <p>
	 * Angular damping is used to reduce the angular velocity over time.  The default is
	 * zero and larger values will cause the angular velocity to reduce faster.
	 * <p>
	 * The units are seconds<sup>-1</sup>.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param angularDamping the angular damping; must be greater than or equal to zero
	 * @throws IllegalArgumentException if angularDamping is less than zero
	 */
	public void setAngularDamping(double angularDamping);
	
	/**
	 * Returns the gravity scale.
	 * @return double
	 * @since 3.0.0
	 * @see #setGravityScale(double)
	 */
	public double getGravityScale();
	
	/**
	 * Sets the gravity scale.
	 * <p>
	 * The gravity scale is a multiplier applied to the acceleration due to
	 * gravity before applying the force of gravity to the body.  This allows
	 * bodies to be affected differently under the same gravity.
	 * <p>
	 * Calling this method will reset the body's rest state to not at rest.
	 * @param scale the gravity scale for this body
	 * @since 3.0.0
	 */
	public void setGravityScale(double scale);
	
	/**
	 * Returns a swept {@link AABB} that contains the maximal space in which the {@link PhysicsBody} exists from the 
	 * initial transform to the final transform.
	 * <p>
	 * The AABB returned from this method can vary based on the state of the body, the number of fixtures,
	 * whether the body was rotating or not, and so on. This method attempts to return the tightest fitting
	 * AABB possible, but it should not be relied on.
	 * <p>
	 * <b>NOTE</b>: This method is dependent upon the {@link #getRotationDiscRadius()} and {@link #getWorldCenter()}
	 * methods in some scenarios, please ensure these are properly setup before calling this method.
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public abstract AABB createSweptAABB();
	
	/**
	 * Creates a swept {@link AABB} from the given start and end {@link Transform}s
	 * using the fixtures on this {@link PhysicsBody}.
	 * <p>
	 * The AABB returned from this method can vary based on the state of the body, the number of fixtures,
	 * whether the body was rotating or not, and so on. This method attempts to return the tightest fitting
	 * AABB possible, but it should not be relied on.
	 * <p>
	 * <b>NOTE</b>: This method is dependent upon the {@link #getRotationDiscRadius()} and {@link #getWorldCenter()}
	 * methods in some scenarios, please ensure these are properly setup before calling this method.
	 * @param initialTransform the initial {@link Transform}
	 * @param finalTransform the final {@link Transform}
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public abstract AABB createSweptAABB(Transform initialTransform, Transform finalTransform);
	
	/**
	 * Computes a swept {@link AABB} that contains the maximal space in which the {@link PhysicsBody} 
	 * exists from the initial transform to the final transform and places the result in the given AABB.
	 * <p>
	 * The AABB returned from this method can vary based on the state of the body, the number of fixtures,
	 * whether the body was rotating or not, and so on. This method attempts to return the tightest fitting
	 * AABB possible, but it should not be relied on.
	 * <p>
	 * <b>NOTE</b>: This method is dependent upon the {@link #getRotationDiscRadius()} and {@link #getWorldCenter()}
	 * methods in some scenarios, please ensure these are properly setup before calling this method.
	 * @param result the AABB to set
	 * @since 4.1.0
	 */
	public abstract void computeSweptAABB(AABB result);
	
	/**
	 * Computes a swept {@link AABB} from the given start and end {@link Transform}s
	 * using the fixtures on this {@link PhysicsBody} and places the result in the given AABB.
	 * <p>
	 * The AABB returned from this method can vary based on the state of the body, the number of fixtures,
	 * whether the body was rotating or not, and so on. This method attempts to return the tightest fitting
	 * AABB possible, but it should not be relied on.
	 * <p>
	 * <b>NOTE</b>: This method is dependent upon the {@link #getRotationDiscRadius()} and {@link #getWorldCenter()}
	 * methods in some scenarios, please ensure these are properly setup before calling this method.
	 * @param initialTransform the initial {@link Transform}
	 * @param finalTransform the final {@link Transform}
	 * @param result the AABB to set
	 * @since 4.1.0
	 */
	public abstract void computeSweptAABB(Transform initialTransform, Transform finalTransform, AABB result);
}