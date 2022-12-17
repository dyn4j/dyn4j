/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a pin joint.
 * <p>
 * A pin joint is a joint that pins a body to a specified world space point.
 * This joint will attempt to place the given anchor point at the target 
 * position.
 * <p>
 * NOTE: The anchor point does not have to be within the bounds of the body.
 * <p>
 * By default the target position will be the given world space anchor. Use 
 * the {@link #setTarget(Vector2)} method to set a different target.
 * <p>
 * By default the pin joint is setup with a linear spring-damper with a 
 * maximum force. The defaults are a frequency of 8.0, damping ratio of 0.3
 * and a maximum force of 1000.0. You can disable the spring-damper using
 * the {@link #setSpringEnabled(boolean)} method which will pin the body
 * to the specified point (similar to the RevoluteJoint).
 * <p>
 * NOTE: When the spring-damper system is disabled, the point constraint
 * is solved completely, so when using this joint for mouse control, you'll
 * see the connected body travel through immovable bodies.  Use caution when
 * enabling this mode.
 * <p>
 * The {@link #getAnchor()} method returns the anchor point on body in world
 * space.  The {@link #getTarget()} returns the target point in world space.
 * <p>
 * Renamed from MouseJoint in 3.2.0. Can function without a spring-damper as
 * of 5.0.0.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Pin_Joint" target="_blank">Documentation</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class PinJoint<T extends PhysicsBody> extends AbstractSingleBodyJoint<T> implements LinearSpringJoint, SingleBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The world space target point */
	protected final Vector2 target;
	
	/** The local anchor point for the body */
	protected final Vector2 localAnchor;
	
	// spring-damper constraint
	
	/** True if the spring is enabled */
	protected boolean springEnabled;
	
	/** The current spring mode */
	protected int springMode;
	
	/** The oscillation frequency in hz */
	protected double springFrequency;

	/** The stiffness (k) of the spring */
	protected double springStiffness;
	
	/** True if the spring's damper is enabled */
	protected boolean springDamperEnabled;
	
	/** The damping ratio */
	protected double springDampingRatio;
	
	/** True if the spring maximum force is enabled */
	protected boolean springMaximumForceEnabled;
	
	/** The maximum force the spring can apply */
	protected double springMaximumForce;
	
	// current state

	/** The world-space vector from the local center to the local anchor point */
	private Vector2 r;
	
	/** The damping coefficient of the spring-damper */
	private double damping;

	/** The bias for adding work to the constraint (simulating a spring) */
	private Vector2 bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	/** The constraint mass; K = J * Minv * Jtrans */
	private final Matrix22 K;
	
	// output
	
	/** The impulse applied to the body to satisfy the constraint */
	private Vector2 impulse;
	
	/**
	 * Full constructor.
	 * @param body the body to attach the joint to
	 * @param anchor the anchor point on the body
	 * @throws NullPointerException if body or anchor is null
	 */
	public PinJoint(T body, Vector2 anchor) {
		super(body);
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.pin.nullAnchor"));
		
		this.target = anchor.copy();
		this.localAnchor = body.getLocalPoint(anchor);
		
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = true;
		this.springFrequency = 8.0;
		this.springStiffness = 0.0;
		this.springDamperEnabled = true;
		this.springDampingRatio = 0.3;
		this.springMaximumForceEnabled = true;
		this.springMaximumForce = 1000.0;
		
		// initialize
		this.damping = 0.0;
		this.gamma = 0.0;
		this.bias = new Vector2();
		this.K = new Matrix22();
		
		this.impulse = new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PinJoint[").append(super.toString())
		  .append("|Target=").append(this.target)
		  .append("|Anchor=").append(this.localAnchor)
		  .append("|Frequency=").append(this.springFrequency)
		  .append("|DampingRatio=").append(this.springDampingRatio)
		  .append("|MaximumForce=").append(this.springMaximumForce)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
		T body = this.body;
		Transform transform = body.getTransform();
		
		Mass mass = this.body.getMass();
		
		double m = mass.getMass();
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// check if the mass is zero
		if (m <= Epsilon.E) {
			// if the mass is zero, use the inertia
			// this will allow the pin joint to work with
			// all mass types other than INFINITE
			m = mass.getInertia();
		}

		// compute the r vector
		this.r = transform.getTransformedR(body.getLocalCenter().to(this.localAnchor));
		
		// compute the K inverse matrix (point-to-point constraint)
		this.K.m00 = invM + this.r.y * this.r.y * invI;
		this.K.m01 = -invI * this.r.x * this.r.y; 
		this.K.m10 = this.K.m01;
		this.K.m11 = invM + this.r.x * this.r.x * invI;
		
		// recompute spring reduced mass (m), stiffness (k), and damping (d)
		// since frequency, dampingRatio, or the masses of the joined bodies
		// could change
		if (this.springEnabled) {
			this.updateSpringCoefficients();
			
			// get the delta time
			double dt = step.getDeltaTime();
			
			// compute the CIM
			this.gamma = AbstractJoint.getConstraintImpulseMixing(dt, this.springStiffness, this.damping);
			
			// compute the ERP
			double erp = AbstractJoint.getErrorReductionParameter(dt, this.springStiffness, this.damping);
			
			// compute the bias = ERP where ERP = hk / (hk + d)
			this.bias = body.getWorldCenter().add(this.r).difference(this.target);
			this.bias.multiply(erp);

			// apply the spring
			this.K.m00 += this.gamma;
			this.K.m11 += this.gamma;
		} else {
			this.bias.zero();
			this.gamma = 0.0;
		}
		
		// warm start
		if (settings.isWarmStartingEnabled()) {
			this.impulse.multiply(step.getDeltaTimeRatio());
			
			body.getLinearVelocity().add(this.impulse.product(invM));
			body.setAngularVelocity(body.getAngularVelocity() + invI * this.r.cross(this.impulse));
		} else {
			this.impulse.zero();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {
		T body = this.body;
		
		Mass mass = this.body.getMass();
		
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// Cdot = v + cross(w, r)
		Vector2 C = this.r.cross(body.getAngularVelocity()).add(body.getLinearVelocity());
		// compute Jv + b
		Vector2 jvb = C;
		jvb.add(this.bias);
		jvb.add(this.impulse.product(this.gamma));
		jvb.negate();
		Vector2 J = this.K.solve(jvb);
		
		if (this.springEnabled && this.springMaximumForceEnabled) {
			// clamp using the maximum force
			Vector2 currentAccumulatedImpulse = this.impulse.copy();
			this.impulse.add(J);
			double maxImpulse = step.getDeltaTime() * this.springMaximumForce;
			if (this.impulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
				this.impulse.normalize();
				this.impulse.multiply(maxImpulse);
			}
			J = this.impulse.difference(currentAccumulatedImpulse);
		} else {
			this.impulse.add(J);
		}
		
		body.getLinearVelocity().add(J.product(invM));
		body.setAngularVelocity(body.getAngularVelocity() + invI * this.r.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		if (!this.springEnabled) {
			double linearTolerance = settings.getLinearTolerance();
			
			Transform tx = this.body.getTransform();
			
			Mass m = this.body.getMass();
			double invM = m.getInverseMass();
			double invI = m.getInverseInertia();
			
			double linearError = 0.0;
	
			// always solve the point-to-point constraint
	
			// compute the r vector
			Vector2 r = tx.getTransformedR(this.body.getLocalCenter().to(this.localAnchor));
			Vector2 d = this.body.getWorldCenter().add(r).difference(this.target);
			
			linearError = d.getMagnitude();
			
			// compute the K matrix
			Matrix22 K = new Matrix22();
			K.m00 = invM + r.y * r.y * invI;
			K.m01 = -invI * r.x * r.y; 
			K.m10 = K.m01;
			K.m11 = invM + r.x * r.x * invI;
			
			// solve for the impulse
			Vector2 J = K.solve(d.negate());
	
			// translate and rotate the objects
			this.body.translate(J.product(invM));
			this.body.rotateAboutCenter(invI * r.cross(J));
			
			return linearError <= linearTolerance;
		}
		
		// nothing to do here if spring is enabled
		return true;
	}

	/**
	 * Computes the spring coefficients from the current state of the joint.
	 * <p>
	 * This method is intended to set the springStiffness OR springFrequency and
	 * damping for use during constraint solving.
	 */
	protected void updateSpringCoefficients() {
		Mass mass = this.body.getMass();
		double m = mass.getMass();
		
		// check if the mass is zero
		if (m <= Epsilon.E) {
			// if the mass is zero, use the inertia
			// this will allow the pin joint to work with
			// all mass types other than INFINITE
			m = mass.getInertia();
		}
		
		double nf = 0.0;
		
		if (this.springMode == SPRING_MODE_FREQUENCY) {
			// compute the stiffness based on the frequency
			nf = getNaturalFrequency(this.springFrequency);
			this.springStiffness = getSpringStiffness(m, nf);
		} else if (this.springMode == SPRING_MODE_STIFFNESS) {
			// compute the frequency based on the stiffness
			nf = getNaturalFrequency(this.springStiffness, m);
			this.springFrequency = getFrequency(nf);
		}
		
		if (this.springDamperEnabled) {
			this.damping = getSpringDampingCoefficient(m, nf, this.springDampingRatio);
		} else {
			this.damping = 0.0;
		}
	}
	
	/**
	 * Returns the anchor point on the body in world space.
	 * @return Vector2
	 */
	public Vector2 getAnchor() {
		return this.body.getWorldPoint(this.localAnchor);
	}

	/**
	 * Returns the target point in world coordinates.
	 * @param target the target point
	 * @throws NullPointerException if target is null
	 */
	public void setTarget(Vector2 target) {
		// make sure the target is non null
		if (target == null) throw new NullPointerException(Messages.getString("dynamics.joint.pin.nullTarget"));
		// only wake the body if the target has changed
		if (!target.equals(this.target)) {
			// wake up the body
			this.body.setAtRest(false);
			// set the new target
			this.target.set(target);
		}
	}
	
	/**
	 * Returns the target point in world coordinates
	 * @return {@link Vector2}
	 */
	public Vector2 getTarget() {
		return this.target;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.impulse.product(invdt);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * Always returns zero.
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isCollisionAllowed()
	 */
	@Override
	public boolean isCollisionAllowed() {
		// never allow collisions since there is only one body attached
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// the target point must be moved
		this.target.add(shift);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringDampingRatio()
	 */
	@Override
	public double getSpringDampingRatio() {
		return this.springDampingRatio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDampingRatio(double)
	 */
	@Override
	public void setSpringDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio <= 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
		// did it change?
		if (this.springDampingRatio != dampingRatio) {
			// set the damping ratio
			this.springDampingRatio = dampingRatio;
			// only wake if the damper would be applied
			if (this.springEnabled && this.springDamperEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringFrequency()
	 */
	@Override
	public double getSpringFrequency() {
		return this.springFrequency;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringStiffness()
	 */
	@Override
	public double getSpringStiffness() {
		return this.springStiffness;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringFrequency(double)
	 */
	@Override
	public void setSpringFrequency(double frequency) {
		// check for valid value
		if (frequency <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequency"));
		// set the spring mode
		this.springMode = SPRING_MODE_FREQUENCY;
		// check for change
		if (this.springFrequency != frequency) {
			// make the change
			this.springFrequency = frequency;
			// check if the spring is enabled
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringStiffness(double)
	 */
	@Override
	public void setSpringStiffness(double stiffness) {
		// check for valid value
		if (stiffness <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidStiffness"));
		// set the spring mode
		this.springMode = SPRING_MODE_STIFFNESS;
		// only update if necessary
		if (this.springStiffness != stiffness) {
			this.springStiffness = stiffness;
			// wake up the bodies
			if (this.springEnabled) {
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMaximumForce()
	 */
	@Override
	public double getMaximumSpringForce() {
		return this.springMaximumForce;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForce(double)
	 */
	@Override
	public void setMaximumSpringForce(double maximum) {
		// check for valid value
		if (maximum <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidSpringMaximumForce"));
		// check if changed
		if (this.springMaximumForce != maximum) {
			this.springMaximumForce = maximum;
			// wake up the bodies
			if (this.springEnabled && this.springMaximumForceEnabled) {
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringMaximumForceEnabled()
	 */
	@Override
	public boolean isMaximumSpringForceEnabled() {
		return this.springMaximumForceEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForceEnabled(boolean)
	 */
	@Override
	public void setMaximumSpringForceEnabled(boolean enabled) {
		if (this.springMaximumForceEnabled != enabled) {
			this.springMaximumForceEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringEnabled()
	 */
	public boolean isSpringEnabled() {
		return this.springEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringEnabled(boolean)
	 */
	@Override
	public void setSpringEnabled(boolean enabled) {
		if (this.springEnabled != enabled) {
			// update the flag
			this.springEnabled = enabled;
			// wake the bodies
			this.body.setAtRest(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringDamperEnabled()
	 */
	public boolean isSpringDamperEnabled() {
		return this.springDamperEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDamperEnabled(boolean)
	 */
	@Override
	public void setSpringDamperEnabled(boolean enabled) {
		if (this.springDamperEnabled != enabled) {
			// update the flag
			this.springDamperEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringForce(double)
	 */
	@Override
	public double getSpringForce(double invdt) {
		if (this.springEnabled) {
			return this.impulse.getMagnitude() * invdt;
		}
		return 0.0;
	}
}
