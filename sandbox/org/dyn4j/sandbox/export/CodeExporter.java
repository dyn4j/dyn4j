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
package org.dyn4j.sandbox.export;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.dyn4j.Version;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.SapBruteForce;
import org.dyn4j.collision.broadphase.SapIncremental;
import org.dyn4j.collision.broadphase.SapTree;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
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
 * @version 1.0.1
 * @since 1.0.1
 */
public class CodeExporter {
	/** The line separator for the system */
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	/**
	 * Exports the given world and settings to Java code.
	 * <p>
	 * Returns a string containing the code for the export.
	 * @param name the name
	 * @param world the world
	 * @param settings the settings
	 * @return String
	 */
	public static final String export(String name, World world, Settings settings) {
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
		.append("// dyn4j v").append(Version.getVersion()).append(NEW_LINE)
		.append("public class ").append(name).append(" { ").append(NEW_LINE).append(NEW_LINE)
		// private constructor
		.append("\tprivate ").append(name).append("() {}").append(NEW_LINE).append(NEW_LINE)
		// single static setup method
		.append("\tpublic static final void setup(World world, Settings settings) {").append(NEW_LINE);
		
		// output settings
		sb.append(export(settings));
		
		// output world settings
		Vector2 g = world.getGravity();
		sb.append("\t\tworld.setGravity(").append(export(g)).append(");").append(NEW_LINE);

		BroadphaseDetector<?> bpd = world.getBroadphaseDetector();
		NarrowphaseDetector npd = world.getNarrowphaseDetector();
		ManifoldSolver msr = world.getManifoldSolver();
		TimeOfImpactDetector tid = world.getTimeOfImpactDetector();
		if (bpd instanceof SapBruteForce) {
			sb.append("\t\tworld.setBroadphaseDetector(new SapBruteForce<Body>());").append(NEW_LINE);
		} else if (bpd instanceof SapIncremental) {
			sb.append("\t\tworld.setBroadphaseDetector(new SapIncremental<Body>());").append(NEW_LINE);
		} else if (bpd instanceof SapTree) {
			sb.append("\t\tworld.setBroadphaseDetector(new SapTree<Body>());").append(NEW_LINE);
		} else if (bpd instanceof DynamicAABBTree) {
			sb.append("\t\tworld.setBroadphaseDetector(new DynamicAABBTree<Body>());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), bpd.getClass().getName()));
		}
		
		if (npd instanceof Sat) {
			sb.append("\t\tworld.setNarrowphaseDetector(new Sat());").append(NEW_LINE);
		} else if (npd instanceof Gjk) {
			sb.append("\t\tworld.setNarrowphaseDetector(new Gjk());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), npd.getClass().getName()));
		}
		
		if (msr instanceof ClippingManifoldSolver) {
			sb.append("\t\tworld.setManifoldSolver(new ClippingManifoldSolver());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), msr.getClass().getName()));
		}
		
		if (tid instanceof ConservativeAdvancement) {
			sb.append("\t\tworld.setTimeOfImpactDetector(new ConservativeAdvancement());").append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), tid.getClass().getName()));
		}
		
		sb.append(NEW_LINE);
		Bounds bounds = world.getBounds();
		if (bounds instanceof NullBounds || bounds == null) {
			sb.append("\t\tworld.setBounds(null);").append(NEW_LINE);
		} else if (bounds instanceof RectangularBounds) {
			RectangularBounds rb = (RectangularBounds)bounds;
			Rectangle r = rb.getBounds();
			sb.append("\t\tRectangularBounds bounds = new RectangularBounds(new Rectangle(").append(r.getWidth()).append(", ").append(r.getHeight()).append("));").append(NEW_LINE)
			.append("\t\tbounds.rotate(Math.toRadians(").append(Math.toDegrees(rb.getTransform().getRotation())).append("));").append(NEW_LINE)
			.append("\t\tbounds.translate(").append(export(rb.getTransform().getTranslation())).append(");").append(NEW_LINE)
			.append("\t\tworld.setBounds(bounds);").append(NEW_LINE)
			.append(NEW_LINE);
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), bounds.getClass().getName()));
		}
		
		// output bodies
		int bSize = world.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i);
			// save the id+name
			idNameMap.put(body.getId(), "body" + i);
			Mass mass = body.getMass();
			// output the body settings
			sb.append("\t\t// ").append(body.getUserData()).append(NEW_LINE)
			.append("\t\tBody body").append(i).append(" = new Body();").append(NEW_LINE);
			// add all fixtures
			int fSize = body.getFixtureCount();
			for (int j = 0; j < fSize; j++) {
				BodyFixture bf = body.getFixture(j);
				sb.append("\t\t{// ").append(bf.getUserData()).append(NEW_LINE)
				// create the shape
				.append(export(bf.getShape(), "\t\t\t"))
				// create the fixture
				.append("\t\t\tBodyFixture bf = new BodyFixture(c);").append(NEW_LINE)
				// set the fixture properties
				.append("\t\t\tbf.setSensor(").append(bf.isSensor()).append(");").append(NEW_LINE)
				.append("\t\t\tbf.setDensity(").append(bf.getDensity()).append(");").append(NEW_LINE)
				.append("\t\t\tbf.setFriction(").append(bf.getFriction()).append(");").append(NEW_LINE)
				.append("\t\t\tbf.setRestitution(").append(bf.getRestitution()).append(");").append(NEW_LINE)
				// set the filter properties
				.append(export(bf.getFilter(), "\t\t\t"))
				// add the fixture to the body
				.append("\t\t\tbody").append(i).append(".addFixture(bf);").append(NEW_LINE)
				.append("\t\t}").append(NEW_LINE);
			}
			// set the transform
			sb.append("\t\tbody").append(i).append(".rotate(Math.toRadians(").append(Math.toDegrees(body.getTransform().getRotation())).append("));").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".translate(").append(export(body.getTransform().getTranslation())).append(");").append(NEW_LINE)
			// set velocity
			.append("\t\tbody").append(i).append(".setVelocity(").append(export(body.getVelocity())).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".setAngularVelocity(Math.toRadians(").append(Math.toDegrees(body.getAngularVelocity())).append("));").append(NEW_LINE)
			// set force/torque accumulators
			.append("\t\tbody").append(i).append(".apply(").append(export(body.getAccumulatedForce())).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".apply(").append(body.getAccumulatedTorque()).append(");").append(NEW_LINE)
			// set state properties
			.append("\t\tbody").append(i).append(".setActive(").append(body.isActive()).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".setAsleep(").append(body.isAsleep()).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".setAutoSleepingEnabled(").append(body.isAutoSleepingEnabled()).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".setBullet(").append(body.isBullet()).append(");").append(NEW_LINE)
			// set damping
			.append("\t\tbody").append(i).append(".setLinearDamping(").append(body.getLinearDamping()).append(");").append(NEW_LINE)
			.append("\t\tbody").append(i).append(".setAngularDamping(").append(body.getAngularDamping()).append(");").append(NEW_LINE)
			// set gravity scale
			.append("\t\tbody").append(i).append(".setGravityScale(").append(body.getGravityScale()).append(");").append(NEW_LINE)
			// set mass properties last
			.append("\t\tbody").append(i).append(".setMass(").append(export(mass)).append(");").append(NEW_LINE)
			// set mass type
			.append("\t\tbody").append(i).append(".setMassType(Mass.Type.").append(mass.getType()).append(");").append(NEW_LINE)
			.append("\t\tworld.add(body").append(i).append(");").append(NEW_LINE).append(NEW_LINE);
		}
		
		// output joints
		int jSize = world.getJointCount();
		for (int i = 0; i < jSize; i++) {
			Joint joint = world.getJoint(i);
			
			SandboxBody body1 = (SandboxBody)joint.getBody1();
			SandboxBody body2 = (SandboxBody)joint.getBody2();
			
			sb.append("\t\t// ").append(joint.getUserData()).append(NEW_LINE);
			if (joint instanceof AngleJoint) {
				AngleJoint aj = (AngleJoint)joint;
				sb.append("\t\tAngleJoint joint").append(i).append(" = new AngleJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(aj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(aj.getUpperLimit())).append("));").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimitEnabled(").append(aj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(aj.getReferenceAngle())).append("));").append(NEW_LINE);
			} else if (joint instanceof DistanceJoint) {
				DistanceJoint dj = (DistanceJoint)joint;
				sb.append("\t\tDistanceJoint joint").append(i).append(" = new DistanceJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(dj.getAnchor1())).append(", ").append(export(dj.getAnchor2())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setFrequency(").append(dj.getFrequency()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setDampingRatio(").append(dj.getDampingRatio()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setDistance(").append(dj.getDistance()).append(");").append(NEW_LINE);
			} else if (joint instanceof FrictionJoint) {
				FrictionJoint fj = (FrictionJoint)joint;
				sb.append("\t\tFrictionJoint joint").append(i).append(" = new FrictionJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(fj.getAnchor1())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMaximumForce(").append(fj.getMaximumForce()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMaximumTorque(").append(fj.getMaximumTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof MouseJoint) {
				MouseJoint mj = (MouseJoint)joint;
				sb.append("\t\tMouseJoint joint").append(i).append(" = new MouseJoint(").append(idNameMap.get(body1.getId())).append(", ").append(export(mj.getAnchor2())).append(", ").append(mj.getFrequency()).append(", ").append(mj.getDampingRatio()).append(", ").append(mj.getMaximumForce()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setTarget(").append(export(mj.getAnchor1())).append(");").append(NEW_LINE);
			} else if (joint instanceof PrismaticJoint) {
				PrismaticJoint pj = (PrismaticJoint)joint;
				sb.append("\t\tPrismaticJoint joint").append(i).append(" = new PrismaticJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAxis())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimitEnabled(").append(pj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimits(").append(pj.getLowerLimit()).append(", ").append(pj.getUpperLimit()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(pj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorEnabled(").append(pj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorSpeed(").append(pj.getMotorSpeed()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMaximumMotorForce(").append(pj.getMaximumMotorForce()).append(");").append(NEW_LINE);
			} else if (joint instanceof PulleyJoint) {
				PulleyJoint pj = (PulleyJoint)joint;
				sb.append("\t\tPulleyJoint joint").append(i).append(" = new PulleyJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(pj.getPulleyAnchor1())).append(", ").append(export(pj.getPulleyAnchor2())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAnchor2())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setRatio(").append(pj.getRatio()).append(");").append(NEW_LINE);
			} else if (joint instanceof RevoluteJoint) {
				RevoluteJoint rj = (RevoluteJoint)joint;
				sb.append("\t\tRevoluteJoint joint").append(i).append(" = new RevoluteJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(rj.getAnchor1())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimitEnabled(").append(rj.isLimitEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimits(Math.toRadians(").append(Math.toDegrees(rj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(rj.getUpperLimit())).append("));").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(rj.getReferenceAngle())).append("));").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorEnabled(").append(rj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorSpeed(").append(rj.getMotorSpeed()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMaximumMotorTorque(").append(rj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else if (joint instanceof RopeJoint) {
				RopeJoint rj = (RopeJoint)joint;
				sb.append("\t\tRopeJoint joint").append(i).append(" = new RopeJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(rj.getAnchor1())).append(", ").append(export(rj.getAnchor2())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLimits(").append(rj.getLowerLimit()).append(", ").append(rj.getUpperLimit()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setLowerLimitEnabled(").append(rj.isLowerLimitEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setUpperLimitEnabled(").append(rj.isUpperLimitEnabled()).append(");").append(NEW_LINE);
			} else if (joint instanceof WeldJoint) {
				WeldJoint wj = (WeldJoint)joint;
				sb.append("\t\tWeldJoint joint").append(i).append(" = new WeldJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(wj.getAnchor1())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setFrequency(").append(wj.getFrequency()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setDampingRatio(").append(wj.getDampingRatio()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(wj.getReferenceAngle())).append("));").append(NEW_LINE);
			} else if (joint instanceof WheelJoint) {
				WheelJoint wj = (WheelJoint)joint;
				sb.append("\t\tWheelJoint joint").append(i).append(" = new WheelJoint(").append(idNameMap.get(body1.getId())).append(", ").append(idNameMap.get(body2.getId())).append(", ").append(export(wj.getAnchor1())).append(", ").append(export(wj.getAxis())).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setFrequency(").append(wj.getFrequency()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setDampingRatio(").append(wj.getDampingRatio()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorEnabled(").append(wj.isMotorEnabled()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMotorSpeed(").append(wj.getMotorSpeed()).append(");").append(NEW_LINE)
				.append("\t\tjoint").append(i).append(".setMaximumMotorTorque(").append(wj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
			} else {
				throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), joint.getClass().getName()));
			}
			
			sb.append("\t\tjoint").append(i).append(".setCollisionAllowed(").append(joint.isCollisionAllowed()).append(");").append(NEW_LINE);
			sb.append(NEW_LINE);
		}
		
		// end setup method
		sb.append("\t}").append(NEW_LINE)
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
		sb.append("\t\tsettings.setStepFrequency(").append(1.0 / settings.getStepFrequency()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setMaximumTranslation(").append(settings.getMaximumTranslation()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setMaximumRotation(Math.toRadians(").append(Math.toDegrees(settings.getMaximumRotation())).append("));").append(NEW_LINE)
		.append("\t\tsettings.setAutoSleepingEnabled(").append(settings.isAutoSleepingEnabled()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setSleepLinearVelocity(").append(settings.getSleepLinearVelocity()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setSleepAngularVelocity(Math.toRadians(").append(Math.toDegrees(settings.getSleepAngularVelocity())).append("));").append(NEW_LINE)
		.append("\t\tsettings.setSleepTime(").append(settings.getSleepTime()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setVelocityConstraintSolverIterations(").append(settings.getVelocityConstraintSolverIterations()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setPositionConstraintSolverIterations(").append(settings.getPositionConstraintSolverIterations()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setWarmStartDistance(").append(settings.getWarmStartDistance()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setRestitutionVelocity(").append(settings.getRestitutionVelocity()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setLinearTolerance(").append(settings.getLinearTolerance()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setAngularTolerance(Math.toRadians(").append(Math.toDegrees(settings.getAngularTolerance())).append("));").append(NEW_LINE)
		.append("\t\tsettings.setMaximumLinearCorrection(").append(settings.getMaximumLinearCorrection()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setMaximumAngularCorrection(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAngularCorrection())).append("));").append(NEW_LINE)
		.append("\t\tsettings.setBaumgarte(").append(settings.getBaumgarte()).append(");").append(NEW_LINE)
		.append("\t\tsettings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.").append(settings.getContinuousDetectionMode()).append(");").append(NEW_LINE)
		.append(NEW_LINE);
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
			sb.append(tabs).append("c.translate(").append(export(circle.getCenter())).append(");").append(NEW_LINE);
		} else if (c instanceof Rectangle) {
			Rectangle rectangle = (Rectangle)c;
			sb.append(tabs).append("Convex c = Geometry.createRectangle(").append(rectangle.getWidth()).append(", ").append(rectangle.getHeight()).append(");").append(NEW_LINE);
			sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(rectangle.getRotation())).append("));").append(NEW_LINE);
			sb.append(tabs).append("c.translate(").append(export(rectangle.getCenter())).append(");").append(NEW_LINE);
		} else if (c instanceof Triangle) {
			Triangle triangle = (Triangle)c;
			sb.append(tabs).append("Convex c = Geometry.createTriangle(").append(export(triangle.getVertices()[0])).append(", ").append(export(triangle.getVertices()[1])).append(", ").append(export(triangle.getVertices()[2])).append(");").append(NEW_LINE);
			// translation is maintained by the vertices
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
			// translation is maintained by the vertices
		} else if (c instanceof Segment) {
			Segment segment = (Segment)c;
			sb.append(tabs).append("Convex c = Geometry.createSegment(").append(export(segment.getVertices()[0])).append(", ").append(export(segment.getVertices()[1])).append(");").append(NEW_LINE);
			// translation is maintained by the vertices
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
