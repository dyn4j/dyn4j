/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.export;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.dyn4j.Epsilon;
import org.dyn4j.Version;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.SapBruteForce;
import org.dyn4j.collision.broadphase.SapIncremental;
import org.dyn4j.collision.broadphase.SapTree;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.NullBounds;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Class to export a simulation to Java code.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.1
 */
public class CodeExporter {
	/** The line separator for the system */
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	/** One tab */
	private static final String TAB1 = "  ";
	
	/** Two tabs */
	private static final String TAB2 = TAB1 + TAB1;
	
	/** Three tabs */
	private static final String TAB3 = TAB1 + TAB1 + TAB1;
	
	/**
	 * Exports the given world and settings to Java code.
	 * <p>
	 * Returns a string containing the code for the export.
	 * @param name the name of the generated class
	 * @param world the world to export
	 * @return String
	 */
	public static final String export(String name, World world) {
		StringBuilder sb = new StringBuilder();
		// this map contains the id to output name for bodies
		Map<String, String> idNameMap = new HashMap<String, String>();
		
		sb
		// imports
		.append("import org.dyn4j.collision.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.broadphase.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.continuous.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.manifold.*;").append(NEW_LINE)
		.append("import org.dyn4j.collision.narrowphase.*;").append(NEW_LINE)
		.append("import org.dyn4j.dynamics.*;").append(NEW_LINE)
		.append("import org.dyn4j.dynamics.joint.*;").append(NEW_LINE)
		.append("import org.dyn4j.geometry.*;").append(NEW_LINE).append(NEW_LINE)
		// class declaration
		.append("// ").append(world.getUserData()).append(NEW_LINE)
		.append("// generated for dyn4j v").append(Version.getVersion()).append(NEW_LINE)
		.append("public class ").append(name).append(" { ").append(NEW_LINE).append(NEW_LINE)
		// private constructor
		.append(TAB1).append("private ").append(name).append("() {}").append(NEW_LINE).append(NEW_LINE)
		// single static setup method
		.append(TAB1).append("public static final void setup(World world) {").append(NEW_LINE)
		// get the settings object from the world
		.append(TAB2).append("Settings settings = world.getSettings();").append(NEW_LINE);
		
		// output settings
		sb.append(export(world.getSettings()));
		
		// output world settings
		sb.append(NEW_LINE);
		Vector2 g = world.getGravity();
		if (g == World.EARTH_GRAVITY || g.equals(0.0, -9.8)) {
			// don't output anything since its the default
		} else if (g == World.ZERO_GRAVITY || g.isZero()) {
			sb.append(TAB2).append("world.setGravity(World.ZERO_GRAVITY);").append(NEW_LINE);
		} else {
			sb.append(TAB2).append("world.setGravity(").append(export(g)).append(");").append(NEW_LINE);
		}

		BroadphaseDetector<?> bpd = world.getBroadphaseDetector();
		NarrowphaseDetector npd = world.getNarrowphaseDetector();
//		ManifoldSolver msr = world.getManifoldSolver();
//		TimeOfImpactDetector tid = world.getTimeOfImpactDetector();
		if (bpd instanceof SapBruteForce) {
			sb.append(TAB2).append("world.setBroadphaseDetector(new SapBruteForce<Body>());").append(NEW_LINE);
		} else if (bpd instanceof SapIncremental) {
			sb.append(TAB2).append("world.setBroadphaseDetector(new SapIncremental<Body>());").append(NEW_LINE);
		} else if (bpd instanceof SapTree) {
			sb.append(TAB2).append("world.setBroadphaseDetector(new SapTree<Body>());").append(NEW_LINE);
		} else if (bpd instanceof DynamicAABBTree) {
			// don't output anything since its the default
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), bpd.getClass().getName()));
		}
		
		if (npd instanceof Sat) {
			sb.append(TAB2).append("world.setNarrowphaseDetector(new Sat());").append(NEW_LINE);
		} else if (npd instanceof Gjk) {
			// don't output anything since its the default
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), npd.getClass().getName()));
		}
		
		// don't output anything since its the default
//		if (msr instanceof ClippingManifoldSolver) {
//			sb.append(TAB2).append("world.setManifoldSolver(new ClippingManifoldSolver());").append(NEW_LINE);
//		} else {
//			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), msr.getClass().getName()));
//		}
		
		// don't output anything since its the default
//		if (tid instanceof ConservativeAdvancement) {
//			sb.append(TAB2).append("world.setTimeOfImpactDetector(new ConservativeAdvancement());").append(NEW_LINE);
//		} else {
//			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), tid.getClass().getName()));
//		}
		
		Bounds bounds = world.getBounds();
		if (bounds instanceof NullBounds || bounds == null) {
			// don't output anything since its the default
		} else if (bounds instanceof AxisAlignedBounds) {
			AxisAlignedBounds aab = (AxisAlignedBounds)bounds;
			double w = aab.getWidth();
			double h = aab.getHeight();
			sb.append(NEW_LINE)
			.append(TAB2).append("AxisAlignedBounds bounds = new AxisAlignedBounds(").append(w).append(", ").append(h).append(");").append(NEW_LINE);
			if (!aab.getTransform().getTranslation().isZero()) {
				sb.append(TAB2).append("bounds.translate(").append(export(aab.getTransform().getTranslation())).append(");").append(NEW_LINE);
			}
			sb.append(TAB2).append("world.setBounds(bounds);").append(NEW_LINE)
			.append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), bounds.getClass().getName()));
		}
		
		// output bodies
		int bSize = world.getBodyCount();
		for (int i = 1; i < bSize + 1; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i - 1);
			// save the id+name
			idNameMap.put(body.getId(), "body" + i);
			Mass mass = body.getMass();
			// output the body settings
			sb.append(TAB2).append("// ").append(body.getUserData()).append(NEW_LINE)
			.append(TAB2).append("Body body").append(i).append(" = new Body();").append(NEW_LINE);
			// add all fixtures
			int fSize = body.getFixtureCount();
			for (int j = 0; j < fSize; j++) {
				BodyFixture bf = body.getFixture(j);
				sb.append(TAB2).append("{// ").append(bf.getUserData()).append(NEW_LINE)
				// create the shape
				.append(export(bf.getShape(), TAB3))
				// create the fixture
				.append(TAB3).append("BodyFixture bf = new BodyFixture(c);").append(NEW_LINE);
				// set the fixture properties
				if (bf.isSensor()) {
					sb.append(TAB3).append("bf.setSensor(").append(bf.isSensor()).append(");").append(NEW_LINE);
				} // by default fixtures are not sensors
				if (bf.getDensity() != BodyFixture.DEFAULT_DENSITY) {
					sb.append(TAB3).append("bf.setDensity(").append(bf.getDensity()).append(");").append(NEW_LINE);
				}
				if (bf.getFriction() != BodyFixture.DEFAULT_FRICTION) {
					sb.append(TAB3).append("bf.setFriction(").append(bf.getFriction()).append(");").append(NEW_LINE);
				}
				if (bf.getRestitution() != BodyFixture.DEFAULT_RESTITUTION) {
					sb.append(TAB3).append("bf.setRestitution(").append(bf.getRestitution()).append(");").append(NEW_LINE);
				}
				// set the filter properties
				sb.append(export(bf.getFilter(), TAB3))
				// add the fixture to the body
				.append(TAB3).append("body").append(i).append(".addFixture(bf);").append(NEW_LINE)
				.append(TAB2).append("}").append(NEW_LINE);
			}
			// set the transform
			if (Math.abs(body.getTransform().getRotation()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".rotate(Math.toRadians(").append(Math.toDegrees(body.getTransform().getRotation())).append("));").append(NEW_LINE);
			}
			if (!body.getTransform().getTranslation().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".translate(").append(export(body.getTransform().getTranslation())).append(");").append(NEW_LINE);
			}
			// set velocity
			if (!body.getVelocity().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".setVelocity(").append(export(body.getVelocity())).append(");").append(NEW_LINE);
			}
			if (Math.abs(body.getAngularVelocity()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".setAngularVelocity(Math.toRadians(").append(Math.toDegrees(body.getAngularVelocity())).append("));").append(NEW_LINE);
			}
			// set force/torque accumulators
			if (!body.getAccumulatedForce().isZero()) {
				sb.append(TAB2).append("body").append(i).append(".apply(").append(export(body.getAccumulatedForce())).append(");").append(NEW_LINE);
			}
			if (Math.abs(body.getAccumulatedTorque()) > Epsilon.E) {
				sb.append(TAB2).append("body").append(i).append(".apply(").append(body.getAccumulatedTorque()).append(");").append(NEW_LINE);
			}
			// set state properties
			if (!body.isActive()) {
				sb.append(TAB2).append("body").append(i).append(".setActive(false);").append(NEW_LINE);
			} // by default the body is active
			if (body.isAsleep()) {
				sb.append(TAB2).append("body").append(i).append(".setAsleep(true);").append(NEW_LINE);
			} // by default the body is awake
			if (!body.isAutoSleepingEnabled()) {
				sb.append(TAB2).append("body").append(i).append(".setAutoSleepingEnabled(false);").append(NEW_LINE);
			} // by default auto sleeping is true
			if (body.isBullet()) {
				sb.append(TAB2).append("body").append(i).append(".setBullet(true);").append(NEW_LINE);
			} // by default the body is not a bullet
			// set damping
			if (body.getLinearDamping() != Body.DEFAULT_LINEAR_DAMPING) {
				sb.append(TAB2).append("body").append(i).append(".setLinearDamping(").append(body.getLinearDamping()).append(");").append(NEW_LINE);
			}
			if (body.getAngularDamping() != Body.DEFAULT_ANGULAR_DAMPING) {
				sb.append(TAB2).append("body").append(i).append(".setAngularDamping(").append(body.getAngularDamping()).append(");").append(NEW_LINE);
			}
			// set gravity scale
			if (body.getGravityScale() != 1.0) {
				sb.append(TAB2).append("body").append(i).append(".setGravityScale(").append(body.getGravityScale()).append(");").append(NEW_LINE);
			}
			// set mass properties last
			if (body.isMassExplicit()) {
				sb.append(TAB2).append("body").append(i).append(".setMass(").append(export(mass)).append(");").append(NEW_LINE)
				// set the mass type
				.append(TAB2).append("body").append(i).append(".setMassType(Mass.Type.").append(mass.getType()).append(");").append(NEW_LINE);
			} else {
				sb.append(TAB2).append("body").append(i).append(".setMass(Mass.Type.").append(mass.getType()).append(");").append(NEW_LINE);
			}
			// add the body to the world
			sb.append(TAB2).append("world.addBody(body").append(i).append(");").append(NEW_LINE).append(NEW_LINE);
		}
		
		// output joints
		int jSize = world.getJointCount();
		for (int i = 1; i < jSize + 1; i++) {
			Joint joint = world.getJoint(i - 1);
			
			SandboxBody body1 = (SandboxBody)joint.getBody1();
			SandboxBody body2 = (SandboxBody)joint.getBody2();
			
			sb.append(TAB2).append("// ").append(joint.getUserData()).append(NEW_LINE);
			if (joint instanceof AngleJoint) {
				AngleJoint aj = (AngleJoint)joint;
				sb.append(TAB2).append("AngleJoint joint").append(i).append(" = new AngleJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(aj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(aj.getUpperLimit())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitEnabled(").append(aj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(aj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setRatio(").append(aj.getRatio()).append(");").append(NEW_LINE);
			} else if (joint instanceof DistanceJoint) {
				DistanceJoint dj = (DistanceJoint)joint;
				sb.append(TAB2).append("DistanceJoint joint").append(i).append(" = new DistanceJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(dj.getAnchor1())).append(", ").append(export(dj.getAnchor2())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setFrequency(").append(dj.getFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setDampingRatio(").append(dj.getDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setDistance(").append(dj.getDistance()).append(");").append(NEW_LINE);
			} else if (joint instanceof FrictionJoint) {
				FrictionJoint fj = (FrictionJoint)joint;
				sb.append(TAB2).append("FrictionJoint joint").append(i).append(" = new FrictionJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(fj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumForce(").append(fj.getMaximumForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumTorque(").append(fj.getMaximumTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof MouseJoint) {
				MouseJoint mj = (MouseJoint)joint;
				sb.append(TAB2).append("MouseJoint joint").append(i).append(" = new MouseJoint(").append(idNameMap.get(body1.getId())).append(", ").append(export(mj.getAnchor2())).append(", ").append(mj.getFrequency()).append(", ").append(mj.getDampingRatio()).append(", ").append(mj.getMaximumForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setTarget(").append(export(mj.getAnchor1())).append(");").append(NEW_LINE);
			} else if (joint instanceof PrismaticJoint) {
				PrismaticJoint pj = (PrismaticJoint)joint;
				sb.append(TAB2).append("PrismaticJoint joint").append(i).append(" = new PrismaticJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAxis())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitEnabled(").append(pj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(").append(pj.getLowerLimit()).append(", ").append(pj.getUpperLimit()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(pj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(pj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(").append(pj.getMotorSpeed()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorForce(").append(pj.getMaximumMotorForce()).append(");").append(NEW_LINE);
			} else if (joint instanceof PulleyJoint) {
				PulleyJoint pj = (PulleyJoint)joint;
				sb.append(TAB2).append("PulleyJoint joint").append(i).append(" = new PulleyJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(pj.getPulleyAnchor1())).append(", ").append(export(pj.getPulleyAnchor2())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAnchor2())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setRatio(").append(pj.getRatio()).append(");").append(NEW_LINE);
			} else if (joint instanceof RevoluteJoint) {
				RevoluteJoint rj = (RevoluteJoint)joint;
				sb.append(TAB2).append("RevoluteJoint joint").append(i).append(" = new RevoluteJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(rj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimitEnabled(").append(rj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(rj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(rj.getUpperLimit())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(rj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(rj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(rj.getMotorSpeed())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorTorque(").append(rj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof RopeJoint) {
				RopeJoint rj = (RopeJoint)joint;
				sb.append(TAB2).append("RopeJoint joint").append(i).append(" = new RopeJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(rj.getAnchor1())).append(", ").append(export(rj.getAnchor2())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLimits(").append(rj.getLowerLimit()).append(", ").append(rj.getUpperLimit()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLowerLimitEnabled(").append(rj.isLowerLimitEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setUpperLimitEnabled(").append(rj.isUpperLimitEnabled()).append(");").append(NEW_LINE);
			} else if (joint instanceof WeldJoint) {
				WeldJoint wj = (WeldJoint)joint;
				sb.append(TAB2).append("WeldJoint joint").append(i).append(" = new WeldJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(wj.getAnchor1())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setFrequency(").append(wj.getFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setDampingRatio(").append(wj.getDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(wj.getReferenceAngle())).append("));").append(NEW_LINE);
			} else if (joint instanceof WheelJoint) {
				WheelJoint wj = (WheelJoint)joint;
				sb.append(TAB2).append("WheelJoint joint").append(i).append(" = new WheelJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(wj.getAnchor1())).append(", ").append(export(wj.getAxis())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setFrequency(").append(wj.getFrequency()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setDampingRatio(").append(wj.getDampingRatio()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorEnabled(").append(wj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(wj.getMotorSpeed())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumMotorTorque(").append(wj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof MotorJoint) {
				MotorJoint mj = (MotorJoint)joint;
				sb.append(TAB2).append("MotorJoint joint").append(i).append(" = new MotorJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setLinearTarget(").append(export(mj.getLinearTarget())).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setAngularTarget(Math.toRadians(").append(Math.toDegrees(mj.getAngularTarget())).append("));").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setCorrectionFactor(").append(mj.getCorrectionFactor()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumForce(").append(mj.getMaximumForce()).append(");").append(NEW_LINE)
				.append(TAB2).append("joint").append(i).append(".setMaximumTorque(").append(mj.getMaximumTorque()).append(");").append(NEW_LINE);
			} else {
				throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), joint.getClass().getName()));
			}
			
			sb.append(TAB2).append("joint").append(i).append(".setCollisionAllowed(").append(joint.isCollisionAllowed()).append(");").append(NEW_LINE);
			sb.append(TAB2).append("world.addJoint(joint").append(i).append(");");
			sb.append(NEW_LINE);
		}
		
		// end setup method
		sb.append(TAB1).append("}").append(NEW_LINE)
		// end class declaration
		.append("}").append(NEW_LINE);
		
		
		return sb.toString();
	}
	
	/**
	 * Exports the given settings.
	 * @param settings the settings
	 * @return String
	 */
	private static final String export(Settings settings) {
		StringBuilder sb = new StringBuilder();
		if (settings.getStepFrequency() != Settings.DEFAULT_STEP_FREQUENCY) {
			sb.append(TAB2).append("settings.setStepFrequency(").append(1.0 / settings.getStepFrequency()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumTranslation() != Settings.DEFAULT_MAXIMUM_TRANSLATION) {
			sb.append(TAB2).append("settings.setMaximumTranslation(").append(settings.getMaximumTranslation()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumRotation() != Settings.DEFAULT_MAXIMUM_ROTATION) {
			sb.append(TAB2).append("settings.setMaximumRotation(Math.toRadians(").append(Math.toDegrees(settings.getMaximumRotation())).append("));").append(NEW_LINE);
		}
		if (!settings.isAutoSleepingEnabled()) {
			sb.append(TAB2).append("settings.setAutoSleepingEnabled(false);").append(NEW_LINE);
		}
		if (settings.getSleepLinearVelocity() != Settings.DEFAULT_SLEEP_LINEAR_VELOCITY) {
			sb.append(TAB2).append("settings.setSleepLinearVelocity(").append(settings.getSleepLinearVelocity()).append(");").append(NEW_LINE);
		}
		if (settings.getSleepAngularVelocity() != Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY) {
			sb.append(TAB2).append("settings.setSleepAngularVelocity(Math.toRadians(").append(Math.toDegrees(settings.getSleepAngularVelocity())).append("));").append(NEW_LINE);
		}
		if (settings.getSleepTime() != Settings.DEFAULT_SLEEP_TIME) {
			sb.append(TAB2).append("settings.setSleepTime(").append(settings.getSleepTime()).append(");").append(NEW_LINE);
		}
		if (settings.getVelocityConstraintSolverIterations() != Settings.DEFAULT_SOLVER_ITERATIONS) {
			sb.append(TAB2).append("settings.setVelocityConstraintSolverIterations(").append(settings.getVelocityConstraintSolverIterations()).append(");").append(NEW_LINE);
		}
		if (settings.getPositionConstraintSolverIterations() != Settings.DEFAULT_SOLVER_ITERATIONS) {
			sb.append(TAB2).append("settings.setPositionConstraintSolverIterations(").append(settings.getPositionConstraintSolverIterations()).append(");").append(NEW_LINE);
		}
		if (settings.getWarmStartDistance() != Settings.DEFAULT_WARM_START_DISTANCE) {
			sb.append(TAB2).append("settings.setWarmStartDistance(").append(settings.getWarmStartDistance()).append(");").append(NEW_LINE);
		}
		if (settings.getRestitutionVelocity() != Settings.DEFAULT_RESTITUTION_VELOCITY) {
			sb.append(TAB2).append("settings.setRestitutionVelocity(").append(settings.getRestitutionVelocity()).append(");").append(NEW_LINE);
		}
		if (settings.getLinearTolerance() != Settings.DEFAULT_LINEAR_TOLERANCE) {
			sb.append(TAB2).append("settings.setLinearTolerance(").append(settings.getLinearTolerance()).append(");").append(NEW_LINE);
		}
		if (settings.getAngularTolerance() != Settings.DEFAULT_ANGULAR_TOLERANCE) {
			sb.append(TAB2).append("settings.setAngularTolerance(Math.toRadians(").append(Math.toDegrees(settings.getAngularTolerance())).append("));").append(NEW_LINE);
		}
		if (settings.getMaximumLinearCorrection() != Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION) {
			sb.append(TAB2).append("settings.setMaximumLinearCorrection(").append(settings.getMaximumLinearCorrection()).append(");").append(NEW_LINE);
		}
		if (settings.getMaximumAngularCorrection() != Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION) {
			sb.append(TAB2).append("settings.setMaximumAngularCorrection(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAngularCorrection())).append("));").append(NEW_LINE);
		}
		if (settings.getBaumgarte() != Settings.DEFAULT_BAUMGARTE) {
			sb.append(TAB2).append("settings.setBaumgarte(").append(settings.getBaumgarte()).append(");").append(NEW_LINE);
		}
		if (settings.getContinuousDetectionMode() != Settings.ContinuousDetectionMode.ALL) {
			sb.append(TAB2).append("settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.").append(settings.getContinuousDetectionMode()).append(");").append(NEW_LINE);
		}
		return sb.toString();
	}
	
	/**
	 * Exports the given mass.
	 * <p>
	 * Exports in the format:
	 * <pre>
	 * new Mass(...)
	 * </pre>
	 * @param mass the mass
	 * @return String
	 */
	private static final String export(Mass mass) {
		StringBuilder sb = new StringBuilder();
		// create a temporary mass so we can set the
		// mass type and get the correct mass and inertia values
		Mass temp = new Mass(mass);
		temp.setType(Mass.Type.NORMAL);
		sb.append("new Mass(new Vector2(").append(temp.getCenter().x).append(", ").append(temp.getCenter().y).append("), ").append(temp.getMass()).append(", ").append(temp.getInertia()).append(")");
		return sb.toString();
	}
	
	/**
	 * Exports the given vector.
	 * <p>
	 * Exports in the format:
	 * <pre>
	 * new Vector2(v.x, v.y)
	 * </pre>
	 * @param v the vector
	 * @return String
	 */
	private static final String export(Vector2 v) {
		StringBuilder sb = new StringBuilder();
		sb.append("new Vector2(").append(v.x).append(", ").append(v.y).append(")");
		return sb.toString();
	}
	
	/**
	 * Exports the given convex shape.
	 * @param c the convex shape
	 * @param tabs the tabs string for formatting
	 * @return String
	 */
	private static final String export(Convex c, String tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (c instanceof Circle) {
			Circle circle = (Circle)c;
			sb.append(tabs).append("Convex c = Geometry.createCircle(").append(circle.getRadius()).append(");").append(NEW_LINE);
			// translate only if the center is not (0, 0)
			if (!circle.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(circle.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Rectangle) {
			Rectangle rectangle = (Rectangle)c;
			sb.append(tabs).append("Convex c = Geometry.createRectangle(").append(rectangle.getWidth()).append(", ").append(rectangle.getHeight()).append(");").append(NEW_LINE);
			// rotate only if the rotation is greater than zero
			if (Math.abs(rectangle.getRotation()) > Epsilon.E) {
				sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(rectangle.getRotation())).append("));").append(NEW_LINE);
			}
			// translate only if the center is not (0, 0)
			if (!rectangle.getCenter().isZero()) {
				sb.append(tabs).append("c.translate(").append(export(rectangle.getCenter())).append(");").append(NEW_LINE);
			}
		} else if (c instanceof Triangle) {
			Triangle triangle = (Triangle)c;
			sb.append(tabs).append("Convex c = Geometry.createTriangle(").append(export(triangle.getVertices()[0])).append(", ").append(export(triangle.getVertices()[1])).append(", ").append(export(triangle.getVertices()[2])).append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else if (c instanceof Polygon) {
			Polygon polygon = (Polygon)c;
			sb.append(tabs).append("Convex c = Geometry.createPolygon(");
			int vSize = polygon.getVertices().length;
			for (int i = 0; i < vSize; i++) {
				Vector2 v = polygon.getVertices()[i];
				if (i != 0) sb.append(", ");
				sb.append(export(v));
			}
			sb.append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else if (c instanceof Segment) {
			Segment segment = (Segment)c;
			sb.append(tabs).append("Convex c = Geometry.createSegment(").append(export(segment.getVertices()[0])).append(", ").append(export(segment.getVertices()[1])).append(");").append(NEW_LINE);
			// transformations are maintained by the vertices
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), c.getClass().getName()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Exports the given filter.
	 * @param f the filter
	 * @param tabs the tabs string for formatting
	 * @return String
	 */
	private static final String export(Filter f, String tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (f == Filter.DEFAULT_FILTER) {
			// output nothing
		} else if (f instanceof CategoryFilter) {
			CategoryFilter cf = (CategoryFilter)f;
			sb.append(tabs).append("bf.setFilter(new CategoryFilter(").append(cf.getCategory()).append(", ").append(cf.getMask()).append("));").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), f.getClass().getName()));
		}
		
		return sb.toString();
	}
}
