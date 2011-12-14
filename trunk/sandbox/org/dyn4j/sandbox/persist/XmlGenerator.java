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
package org.dyn4j.sandbox.persist;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.RectangularBounds;
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
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.Camera;
import org.dyn4j.sandbox.Sandbox;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.SandboxRay;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.SystemUtilities;

/**
 * Class used to export the world to xml.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class XmlGenerator {
	/**
	 * Returns the xml for the given world object.
	 * @param world the world
	 * @param rays the list of rays
	 * @param settings the global settings
	 * @param camera the camera settings
	 * @return String
	 */
	public static final String toXml(World world, List<SandboxRay> rays, Settings settings, Camera camera) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<Simulation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.dyn4j.org/Sandbox/sandbox.xsd\" version=\"" + Sandbox.VERSION + "\">");
		
		// output the simulation name
		sb.append("<Name>").append(world.getUserData()).append("</Name>");
		
		// output the camera information
		sb.append("<Camera>");
		sb.append("<Scale>").append(camera.getScale()).append("</Scale>");
		sb.append(XmlGenerator.toXml(camera.getTranslation(), "Translation"));
		sb.append("</Camera>");
		
		// output system properties
		sb.append(XmlGenerator.toXml());
		
		// output settings
		sb.append(XmlGenerator.toXml(settings));
		
		// output rays
		sb.append("<Rays>");
		int rSize = rays.size();
		for (int i = 0; i < rSize; i++) {
			SandboxRay ray = rays.get(i);
			sb.append(XmlGenerator.toXml(ray));
		}
		sb.append("</Rays>");
		
		// output the world
		sb.append("<World>");
		
		// algorithms
		sb.append("<BroadphaseDetector>").append(world.getBroadphaseDetector().getClass().getSimpleName()).append("</BroadphaseDetector>");
		sb.append("<NarrowphaseDetector>").append(world.getNarrowphaseDetector().getClass().getSimpleName()).append("</NarrowphaseDetector>");
		sb.append("<ManifoldSolver>").append(world.getManifoldSolver().getClass().getSimpleName()).append("</ManifoldSolver>");
		sb.append("<TimeOfImpactDetector>").append(world.getTimeOfImpactDetector().getClass().getSimpleName()).append("</TimeOfImpactDetector>");
		
		// gravity
		sb.append(XmlGenerator.toXml(world.getGravity(), "Gravity"));
		
		// bounds
		if (world.getBounds() instanceof RectangularBounds) {
			RectangularBounds bounds = (RectangularBounds)world.getBounds();
			Rectangle r = bounds.getBounds();
			sb.append("<Bounds>");
			sb.append("<Rectangle Id=\"").append(r.getId()).append("\">")
			.append(XmlGenerator.toXml(r.getCenter(), "LocalCenter"))
			.append("<Width>").append(r.getWidth()).append("</Width>")
			.append("<Height>").append(r.getHeight()).append("</Height>")
			.append("<LocalRotation>").append(Math.toDegrees(r.getRotation())).append("</LocalRotation>")
			.append("</Rectangle>");
			sb.append(XmlGenerator.toXml(bounds.getTransform()));
			sb.append("</Bounds>");
		}
		
		// bodies
		sb.append("<Bodies>");
		int bSize = world.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i);
			sb.append(XmlGenerator.toXml(body));
		}
		sb.append("</Bodies>");
		
		// joints
		sb.append("<Joints>");
		int jSize = world.getJointCount();
		for (int i = 0; i < jSize; i++) {
			Joint joint = world.getJoint(i);
			sb.append(XmlGenerator.toXml(joint));
		}
		sb.append("</Joints>");
		
		sb.append("</World>");
		sb.append("</Simulation>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the current system.
	 * @return String
	 */
	private static final String toXml() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<System>")
		.append("<JavaVersion>").append(SystemUtilities.getJavaVersion()).append("</JavaVersion>")
		.append("<JavaVendor>").append(SystemUtilities.getJavaVendor()).append("</JavaVendor>")
		.append("<OperatingSystem>").append(SystemUtilities.getOperatingSystem()).append("</OperatingSystem>")
		.append("<Architecture>").append(SystemUtilities.getArchitecture()).append("</Architecture>")
		.append("<NumberOfCpus>").append(Runtime.getRuntime().availableProcessors()).append("</NumberOfCpus>")
		.append("<Locale>").append(Locale.getDefault().getLanguage()).append("_").append(Locale.getDefault().getCountry()).append("</Locale>")
		.append("</System>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given settings instance.
	 * @param settings the settings
	 * @return String
	 */
	private static final String toXml(Settings settings) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Settings>")
		.append("<StepFrequency>").append(1.0 / settings.getStepFrequency()).append("</StepFrequency>")
		.append("<MaximumTranslation>").append(settings.getMaximumTranslation()).append("</MaximumTranslation>")
		.append("<MaximumRotation>").append(Math.toDegrees(settings.getMaximumRotation())).append("</MaximumRotation>")
		.append("<ContinuousCollisionDetectionMode>").append(settings.getContinuousDetectionMode()).append("</ContinuousCollisionDetectionMode>")
		.append("<AutoSleep>").append(settings.isAutoSleepingEnabled()).append("</AutoSleep>")
		.append("<SleepTime>").append(settings.getSleepTime()).append("</SleepTime>")
		.append("<SleepLinearVelocity>").append(settings.getSleepLinearVelocity()).append("</SleepLinearVelocity>")
		.append("<SleepAngularVelocity>").append(Math.toDegrees(settings.getSleepAngularVelocity())).append("</SleepAngularVelocity>")
		.append("<VelocitySolverIterations>").append(settings.getVelocityConstraintSolverIterations()).append("</VelocitySolverIterations>")
		.append("<PositionSolverIterations>").append(settings.getPositionConstraintSolverIterations()).append("</PositionSolverIterations>")
		.append("<WarmStartDistance>").append(settings.getWarmStartDistance()).append("</WarmStartDistance>")
		.append("<RestitutionVelocity>").append(settings.getRestitutionVelocity()).append("</RestitutionVelocity>")
		.append("<LinearTolerance>").append(settings.getLinearTolerance()).append("</LinearTolerance>")
		.append("<AngularTolerance>").append(Math.toDegrees(settings.getAngularTolerance())).append("</AngularTolerance>")
		.append("<MaximumLinearCorrection>").append(settings.getMaximumLinearCorrection()).append("</MaximumLinearCorrection>")
		.append("<MaximumAngularCorrection>").append(Math.toDegrees(settings.getMaximumAngularCorrection())).append("</MaximumAngularCorrection>")
		.append("<Baumgarte>").append(settings.getBaumgarte()).append("</Baumgarte>")
		.append("</Settings>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given vector with the given element name.
	 * @param v the vector
	 * @param name the element name
	 * @return String
	 */
	private static final String toXml(Vector2 v, String name) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<")
		.append(name)
		.append(" x=\"")
		.append(v.x)
		.append("\" y=\"")
		.append(v.y)
		.append("\" />");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given transform object.
	 * @param t the transform
	 * @return String
	 */
	private static final String toXml(Transform t) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Transform>")
		.append(XmlGenerator.toXml(t.getTranslation(), "Translation"))
		.append("<Rotation>")
		.append(Math.toDegrees(t.getRotation()))
		.append("</Rotation>")
		.append("</Transform>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given shape object.
	 * @param shape the shape
	 * @return String
	 */
	private static final String toXml(Shape shape) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Shape Id=\"")
		.append(shape.getId())
		.append("\" xsi:type=\"");
		String c = XmlGenerator.toXml(shape.getCenter(), "LocalCenter");
		if (shape instanceof Circle) {
			sb.append("Circle\">")
			.append(c)
			.append("<Radius>")
			.append(((Circle)shape).getRadius())
			.append("</Radius>");
		} else if (shape instanceof Rectangle) {
			Rectangle r = (Rectangle)shape;
			sb.append("Rectangle\">")
			.append(c)
			.append("<Width>").append(r.getWidth()).append("</Width>")
			.append("<Height>").append(r.getHeight()).append("</Height>")
			.append("<LocalRotation>").append(Math.toDegrees(r.getRotation())).append("</LocalRotation>");
		} else if (shape instanceof Triangle) {
			Triangle t = (Triangle)shape;
			Vector2[] vs = t.getVertices();
			sb.append("Triangle\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else if (shape instanceof Polygon) {
			Polygon p = (Polygon)shape;
			Vector2[] vs = p.getVertices();
			sb.append("Polygon\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else if (shape instanceof Segment) {
			Segment s = (Segment)shape;
			Vector2[] vs = s.getVertices();
			sb.append("Segment\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), shape.getClass().getName()));
		}
		
		sb.append("</Shape>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given filter object.
	 * @param filter the filter
	 * @return String
	 */
	private static final String toXml(Filter filter) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Filter xsi:type=\"");
		
		if (filter == Filter.DEFAULT_FILTER) {
			sb.append("DefaultFilter\" />");
		} else if (filter instanceof CategoryFilter) {
			sb.append("CategoryFilter\">");
			CategoryFilter cf = (CategoryFilter)filter;
			sb.append("<PartOfGroups>");
			sb.append(XmlGenerator.toXml(cf.getCategory()));
			sb.append("</PartOfGroups>");
			sb.append("<CollideWithGroups>");
			sb.append(XmlGenerator.toXml(cf.getMask()));
			sb.append("</CollideWithGroups>");
			sb.append("</Filter>");
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), filter.getClass().getName()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for all the categories for the given cateories int.
	 * @param categories the categories
	 * @return String
	 */
	private static final String toXml(int categories) {
		StringBuilder sb = new StringBuilder();
		
		if ((categories & Integer.MAX_VALUE) == Integer.MAX_VALUE) {
			// if the categories is "all" then only output all
			sb.append("<All" + " Value=\"" + Integer.MAX_VALUE + "\" />");
		} else {
			// otherwise output each category
			int mask = 1;
			for (int i = 1; i < 32; i++) {
				if ((categories & mask) == mask) {
					// append the group
					sb.append("<Group" + i + " Value=\"" + mask + "\" />");
				}
				mask *= 2;
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for a fixture object.
	 * @param fixture the fixture
	 * @return String
	 */
	private static final String toXml(BodyFixture fixture) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Fixture Id=\"")
		.append(fixture.getId())
		.append("\" Name=\"")
		.append(fixture.getUserData())
		.append("\">")
		.append(XmlGenerator.toXml(fixture.getShape()))
		.append(XmlGenerator.toXml(fixture.getFilter()))
		.append("<Sensor>").append(fixture.isSensor()).append("</Sensor>")
		.append("<Density>").append(fixture.getDensity()).append("</Density>")
		.append("<Friction>").append(fixture.getFriction()).append("</Friction>")
		.append("<Restitution>").append(fixture.getRestitution()).append("</Restitution>")
		.append("</Fixture>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given mass object.
	 * @param mass the mass
	 * @param explicit true if the mass was set explicitly
	 * @return String
	 */
	private static final String toXml(Mass mass, boolean explicit) {
		StringBuilder sb = new StringBuilder();
		
		// create a temporary mass from the passed in one and change the mass type
		// so that we can get the mass and inertia values for masses with infinite
		// or fixed types
		Mass m = new Mass(mass);
		m.setType(Mass.Type.NORMAL);
		
		sb.append("<Mass>")
		.append(XmlGenerator.toXml(mass.getCenter(), "LocalCenter"))
		.append("<Type>").append(mass.getType()).append("</Type>")
		.append("<Mass>").append(m.getMass()).append("</Mass>")
		.append("<Inertia>").append(m.getInertia()).append("</Inertia>")
		.append("<Explicit>").append(explicit).append("</Explicit>")
		.append("</Mass>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given color.
	 * @param color the color
	 * @param name the tag name
	 * @return String
	 */
	private static final String toXml(float[] color, String name) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<").append(name)
		.append(" r=\"").append(color[0])
		.append("\" g=\"").append(color[1])
		.append("\" b=\"").append(color[2])
		.append("\" />");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given body object.
	 * @param body the body
	 * @return String
	 */
	private static final String toXml(SandboxBody body) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Body Id=\"")
		.append(body.getId())
		.append("\" Name=\"")
		.append(body.getUserData())
		.append("\">");
		
		// output colors
		sb.append(XmlGenerator.toXml(body.getOutlineColor(), "OutlineColor"));
		sb.append(XmlGenerator.toXml(body.getFillColor(), "FillColor"));
		
		// output fixtures
		sb.append("<Fixtures>");
		int fSize = body.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bf = body.getFixture(i);
			sb.append(XmlGenerator.toXml(bf));
		}
		sb.append("</Fixtures>");
		
		// output transform
		sb.append(XmlGenerator.toXml(body.getTransform()));
		
		// output mass
		sb.append(XmlGenerator.toXml(body.getMass(), body.isMassExplicit()));
		
		sb.append(XmlGenerator.toXml(body.getVelocity(), "Velocity"));
		sb.append("<AngularVelocity>").append(Math.toDegrees(body.getAngularVelocity())).append("</AngularVelocity>");
		sb.append(XmlGenerator.toXml(body.getAccumulatedForce(), "AccumulatedForce"));
		sb.append("<AccumulatedTorque>").append(body.getAccumulatedTorque()).append("</AccumulatedTorque>");
		sb.append("<AutoSleep>").append(body.isAutoSleepingEnabled()).append("</AutoSleep>");
		sb.append("<Asleep>").append(body.isAsleep()).append("</Asleep>");
		sb.append("<Active>").append(body.isActive()).append("</Active>");
		sb.append("<Bullet>").append(body.isBullet()).append("</Bullet>");
		sb.append("<LinearDamping>").append(body.getLinearDamping()).append("</LinearDamping>");
		sb.append("<AngularDamping>").append(body.getAngularDamping()).append("</AngularDamping>");
		sb.append("<GravityScale>").append(body.getGravityScale()).append("</GravityScale>");
		
		sb.append("</Body>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given joint.
	 * @param joint the joint
	 * @return String
	 */
	private static final String toXml(Joint joint) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Joint Id=\"")
		.append(joint.getId())
		.append("\" Name=\"")
		.append(joint.getUserData())
		.append("\" xsi:type=\"")
		.append(joint.getClass().getSimpleName())
		.append("\">");
		
		sb.append("<BodyId1>").append(joint.getBody1().getId()).append("</BodyId1>");
		sb.append("<BodyId2>").append(joint.getBody2().getId()).append("</BodyId2>");
		sb.append("<CollisionAllowed>").append(joint.isCollisionAllowed()).append("</CollisionAllowed>");
		
		if (joint instanceof AngleJoint) {
			AngleJoint aj = (AngleJoint)joint;
			sb.append("<LowerLimit>").append(Math.toDegrees(aj.getLowerLimit())).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(Math.toDegrees(aj.getUpperLimit())).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(aj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<ReferenceAngle>").append(Math.toDegrees(aj.getReferenceAngle())).append("</ReferenceAngle>");
		} else if (joint instanceof DistanceJoint) {
			DistanceJoint dj = (DistanceJoint)joint;
			sb.append(XmlGenerator.toXml(dj.getAnchor1(), "Anchor1"));
			sb.append(XmlGenerator.toXml(dj.getAnchor2(), "Anchor2"));
			sb.append("<Frequency>").append(dj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(dj.getDampingRatio()).append("</DampingRatio>");
			sb.append("<Distance>").append(dj.getDistance()).append("</Distance>");
		} else if (joint instanceof FrictionJoint) {
			FrictionJoint fj = (FrictionJoint)joint;
			sb.append(XmlGenerator.toXml(fj.getAnchor1(), "Anchor"));
			sb.append("<MaximumForce>").append(fj.getMaximumForce()).append("</MaximumForce>");
			sb.append("<MaximumTorque>").append(fj.getMaximumTorque()).append("</MaximumTorque>");
		} else if (joint instanceof MouseJoint) {
			MouseJoint mj = (MouseJoint)joint;
			sb.append(XmlGenerator.toXml(mj.getAnchor2(), "Anchor"));
			sb.append(XmlGenerator.toXml(mj.getAnchor1(), "Target"));
			sb.append("<Frequency>").append(mj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(mj.getDampingRatio()).append("</DampingRatio>");
			sb.append("<MaximumForce>").append(mj.getMaximumForce()).append("</MaximumForce>");
		} else if (joint instanceof PrismaticJoint) {
			PrismaticJoint pj = (PrismaticJoint)joint;
			sb.append(XmlGenerator.toXml(pj.getAnchor1(), "Anchor"));
			sb.append(XmlGenerator.toXml(pj.getAxis(), "Axis"));
			sb.append("<LowerLimit>").append(pj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(pj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(pj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<MotorSpeed>").append(pj.getMotorSpeed()).append("</MotorSpeed>");
			sb.append("<MaximumMotorForce>").append(pj.getMaximumMotorForce()).append("</MaximumMotorForce>");
			sb.append("<MotorEnabled>").append(pj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<ReferenceAngle>").append(Math.toDegrees(pj.getReferenceAngle())).append("</ReferenceAngle>");
		} else if (joint instanceof PulleyJoint) {
			PulleyJoint pj = (PulleyJoint)joint;
			sb.append(XmlGenerator.toXml(pj.getPulleyAnchor1(), "PulleyAnchor1"));
			sb.append(XmlGenerator.toXml(pj.getPulleyAnchor2(), "PulleyAnchor2"));
			sb.append(XmlGenerator.toXml(pj.getAnchor1(), "BodyAnchor1"));
			sb.append(XmlGenerator.toXml(pj.getAnchor2(), "BodyAnchor2"));
			sb.append("<Ratio>").append(pj.getRatio()).append("</Ratio>");
		} else if (joint instanceof RevoluteJoint) {
			RevoluteJoint rj = (RevoluteJoint)joint;
			sb.append(XmlGenerator.toXml(rj.getAnchor1(), "Anchor"));
			sb.append("<LowerLimit>").append(Math.toDegrees(rj.getLowerLimit())).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(Math.toDegrees(rj.getUpperLimit())).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(rj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<MotorSpeed>").append(Math.toDegrees(rj.getMotorSpeed())).append("</MotorSpeed>");
			sb.append("<MaximumMotorTorque>").append(rj.getMaximumMotorTorque()).append("</MaximumMotorTorque>");
			sb.append("<MotorEnabled>").append(rj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<ReferenceAngle>").append(Math.toDegrees(rj.getReferenceAngle())).append("</ReferenceAngle>");
		} else if (joint instanceof RopeJoint) {
			RopeJoint rj = (RopeJoint)joint;
			sb.append(XmlGenerator.toXml(rj.getAnchor1(), "Anchor1"));
			sb.append(XmlGenerator.toXml(rj.getAnchor2(), "Anchor2"));
			sb.append("<LowerLimit>").append(rj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(rj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LowerLimitEnabled>").append(rj.isLowerLimitEnabled()).append("</LowerLimitEnabled>");
			sb.append("<UpperLimitEnabled>").append(rj.isUpperLimitEnabled()).append("</UpperLimitEnabled>");
		} else if (joint instanceof WeldJoint) {
			WeldJoint wj = (WeldJoint)joint;
			sb.append(XmlGenerator.toXml(wj.getAnchor1(), "Anchor"));
			sb.append("<ReferenceAngle>").append(Math.toDegrees(wj.getReferenceAngle())).append("</ReferenceAngle>");
			sb.append("<Frequency>").append(wj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(wj.getDampingRatio()).append("</DampingRatio>");
		} else if (joint instanceof WheelJoint) {
			WheelJoint wj = (WheelJoint)joint;
			sb.append(XmlGenerator.toXml(wj.getAnchor1(), "Anchor"));
			sb.append(XmlGenerator.toXml(wj.getAxis(), "Axis"));
			sb.append("<MotorSpeed>").append(Math.toDegrees(wj.getMotorSpeed())).append("</MotorSpeed>");
			sb.append("<MaximumMotorTorque>").append(wj.getMaximumMotorTorque()).append("</MaximumMotorTorque>");
			sb.append("<MotorEnabled>").append(wj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<Frequency>").append(wj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(wj.getDampingRatio()).append("</DampingRatio>");
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.getString("exception.persist.unknownClass"), joint.getClass().getName()));
		}
		
		sb.append("</Joint>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given ray object.
	 * @param ray the ray
	 * @return String
	 */
	private static final String toXml(SandboxRay ray) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Ray Name=\"").append(ray.getName()).append("\">")
		.append(XmlGenerator.toXml(ray.getStart(), "Start"))
		.append("<Direction>").append(ray.getDirection()).append("</Direction>")
		.append("<Length>").append(ray.getLength()).append("</Length>")
		.append("<IgnoreSensors>").append(ray.isIgnoreSensors()).append("</IgnoreSensors>")
		.append("<TestAll>").append(ray.isAll()).append("</TestAll>")
		.append("</Ray>");
		
		return sb.toString();
	}
}
