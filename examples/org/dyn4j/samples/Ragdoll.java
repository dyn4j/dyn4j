/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.samples;

import java.awt.Graphics2D;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * A somewhat complex scene with a ragdoll.
 * @author William Bittle
 * @since 3.2.1
 * @version 3.2.0
 */
public class Ragdoll extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -2350301592218819726L;

	/**
	 * Default constructor.
	 */
	public Ragdoll() {
		super("Ragdoll", 64.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {		
		// Ground
	    Body ground = new SimulationBody();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(100.0, 1.0);
	      BodyFixture bf = new BodyFixture(c);
	      ground.addFixture(bf);
	    }
	    ground.translate(new Vector2(0.6875, -8.75));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // the ragdoll
	    
	    // Head
	    Body head = new SimulationBody();
	    {// Fixture2
	      Convex c = Geometry.createCircle(0.25);
	      BodyFixture bf = new BodyFixture(c);
	      head.addFixture(bf);
	    }
	    head.setMass(MassType.NORMAL);
	    world.addBody(head);

	    // Torso
	    Body torso = new SimulationBody();
	    {// Fixture4
	      Convex c = Geometry.createRectangle(0.5, 1.0);
	      BodyFixture bf = new BodyFixture(c);
	      torso.addFixture(bf);
	    }
	    {// Fixture16
	      Convex c = Geometry.createRectangle(1.0, 0.25);
	      c.translate(new Vector2(0.00390625, 0.375));
	      BodyFixture bf = new BodyFixture(c);
	      torso.addFixture(bf);
	    }
	    torso.translate(new Vector2(0.0234375, -0.8125));
	    torso.setMass(MassType.NORMAL);
	    world.addBody(torso);

	    // Right Humerus
	    Body rightHumerus = new SimulationBody();
	    {// Fixture5
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      rightHumerus.addFixture(bf);
	    }
	    rightHumerus.translate(new Vector2(0.4375, -0.609375));
	    rightHumerus.setMass(MassType.NORMAL);
	    world.addBody(rightHumerus);

	    // Right Ulna
	    Body rightUlna = new SimulationBody();
	    {// Fixture6
	      Convex c = Geometry.createRectangle(0.25, 0.4);
	      BodyFixture bf = new BodyFixture(c);
	      rightUlna.addFixture(bf);
	    }
	    rightUlna.translate(new Vector2(0.44140625, -0.98828125));
	    rightUlna.setMass(MassType.NORMAL);
	    world.addBody(rightUlna);

	    // Neck
	    Body neck = new SimulationBody();
	    {// Fixture7
	      Convex c = Geometry.createRectangle(0.15, 0.2);
	      BodyFixture bf = new BodyFixture(c);
	      neck.addFixture(bf);
	    }
	    neck.translate(new Vector2(0.015625, -0.2734375));
	    neck.setMass(MassType.NORMAL);
	    world.addBody(neck);

	    // Left Humerus
	    Body leftHumerus = new SimulationBody();
	    {// Fixture9
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      leftHumerus.addFixture(bf);
	    }
	    leftHumerus.translate(new Vector2(-0.3828125, -0.609375));
	    leftHumerus.setMass(MassType.NORMAL);
	    world.addBody(leftHumerus);

	    // Left Ulna
	    Body leftUlna = new SimulationBody();
	    {// Fixture11
	      Convex c = Geometry.createRectangle(0.25, 0.4);
	      BodyFixture bf = new BodyFixture(c);
	      leftUlna.addFixture(bf);
	    }
	    leftUlna.translate(new Vector2(-0.3828125, -0.9765625));
	    leftUlna.setMass(MassType.NORMAL);
	    world.addBody(leftUlna);

	    // Right Femur
	    Body rightFemur = new SimulationBody();
	    {// Fixture12
	      Convex c = Geometry.createRectangle(0.25, 0.75);
	      BodyFixture bf = new BodyFixture(c);
	      rightFemur.addFixture(bf);
	    }
	    rightFemur.translate(new Vector2(0.1796875, -1.5703125));
	    rightFemur.setMass(MassType.NORMAL);
	    world.addBody(rightFemur);

	    // Left Femur
	    Body leftFemur = new SimulationBody();
	    {// Fixture13
	      Convex c = Geometry.createRectangle(0.25, 0.75);
	      BodyFixture bf = new BodyFixture(c);
	      leftFemur.addFixture(bf);
	    }
	    leftFemur.translate(new Vector2(-0.1328125, -1.5703125));
	    leftFemur.setMass(MassType.NORMAL);
	    world.addBody(leftFemur);

	    // Right Tibia
	    Body rightTibia = new SimulationBody();
	    {// Fixture14
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      rightTibia.addFixture(bf);
	    }
	    rightTibia.translate(new Vector2(0.18359375, -2.11328125));
	    rightTibia.setMass(MassType.NORMAL);
	    world.addBody(rightTibia);

	    // Left Tibia
	    Body leftTibia = new SimulationBody();
	    {// Fixture15
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      leftTibia.addFixture(bf);
	    }
	    leftTibia.translate(new Vector2(-0.1328125, -2.1171875));
	    leftTibia.setMass(MassType.NORMAL);
	    world.addBody(leftTibia);

	    // Head to Neck
	    RevoluteJoint headToNeck = new RevoluteJoint(head, neck, new Vector2(0.01, -0.2));
	    headToNeck.setLimitEnabled(false);
	    headToNeck.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    headToNeck.setReferenceAngle(Math.toRadians(0.0));
	    headToNeck.setMotorEnabled(false);
	    headToNeck.setMotorSpeed(Math.toRadians(0.0));
	    headToNeck.setMaximumMotorTorque(0.0);
	    headToNeck.setCollisionAllowed(false);
	    world.addJoint(headToNeck);
	    
	    // Neck to Torso
	    RevoluteJoint neckToTorso = new RevoluteJoint(neck, torso, new Vector2(0.01, -0.35));
	    neckToTorso.setLimitEnabled(false);
	    neckToTorso.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    neckToTorso.setReferenceAngle(Math.toRadians(0.0));
	    neckToTorso.setMotorEnabled(false);
	    neckToTorso.setMotorSpeed(Math.toRadians(0.0));
	    neckToTorso.setMaximumMotorTorque(0.0);
	    neckToTorso.setCollisionAllowed(false);
	    world.addJoint(neckToTorso);
	    
	    // Torso to Left Humerus
	    RevoluteJoint torsoToLeftHumerus = new RevoluteJoint(torso, leftHumerus, new Vector2(-0.4, -0.4));
	    torsoToLeftHumerus.setLimitEnabled(false);
	    torsoToLeftHumerus.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToLeftHumerus.setReferenceAngle(Math.toRadians(0.0));
	    torsoToLeftHumerus.setMotorEnabled(false);
	    torsoToLeftHumerus.setMotorSpeed(Math.toRadians(0.0));
	    torsoToLeftHumerus.setMaximumMotorTorque(0.0);
	    torsoToLeftHumerus.setCollisionAllowed(false);
	    world.addJoint(torsoToLeftHumerus);
	    
	    // Torso to Right Humerus
	    RevoluteJoint torsoToRightHumerus = new RevoluteJoint(torso, rightHumerus, new Vector2(0.4, -0.4));
	    torsoToRightHumerus.setLimitEnabled(false);
	    torsoToRightHumerus.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToRightHumerus.setReferenceAngle(Math.toRadians(0.0));
	    torsoToRightHumerus.setMotorEnabled(false);
	    torsoToRightHumerus.setMotorSpeed(Math.toRadians(0.0));
	    torsoToRightHumerus.setMaximumMotorTorque(0.0);
	    torsoToRightHumerus.setCollisionAllowed(false);
	    world.addJoint(torsoToRightHumerus);
	    
	    // Right Humerus to Right Ulna
	    RevoluteJoint rightHumerusToRightUlna = new RevoluteJoint(rightHumerus, rightUlna, new Vector2(0.43, -0.82));
	    rightHumerusToRightUlna.setLimitEnabled(false);
	    rightHumerusToRightUlna.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    rightHumerusToRightUlna.setReferenceAngle(Math.toRadians(0.0));
	    rightHumerusToRightUlna.setMotorEnabled(false);
	    rightHumerusToRightUlna.setMotorSpeed(Math.toRadians(0.0));
	    rightHumerusToRightUlna.setMaximumMotorTorque(0.0);
	    rightHumerusToRightUlna.setCollisionAllowed(false);
	    world.addJoint(rightHumerusToRightUlna);
	    
	    // Left Humerus to Left Ulna
	    RevoluteJoint leftHumerusToLeftUlna = new RevoluteJoint(leftHumerus, leftUlna, new Vector2(-0.4, -0.81));
	    leftHumerusToLeftUlna.setLimitEnabled(false);
	    leftHumerusToLeftUlna.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setReferenceAngle(Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setMotorEnabled(false);
	    leftHumerusToLeftUlna.setMotorSpeed(Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setMaximumMotorTorque(0.0);
	    leftHumerusToLeftUlna.setCollisionAllowed(false);
	    world.addJoint(leftHumerusToLeftUlna);
	    
	    // Torso to Right Femur
	    RevoluteJoint torsoToRightFemur = new RevoluteJoint(torso, rightFemur, new Vector2(0.16, -1.25));
	    torsoToRightFemur.setLimitEnabled(false);
	    torsoToRightFemur.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToRightFemur.setReferenceAngle(Math.toRadians(0.0));
	    torsoToRightFemur.setMotorEnabled(false);
	    torsoToRightFemur.setMotorSpeed(Math.toRadians(0.0));
	    torsoToRightFemur.setMaximumMotorTorque(0.0);
	    torsoToRightFemur.setCollisionAllowed(false);
	    world.addJoint(torsoToRightFemur);
	    
	    // Torso to Left Femur
	    RevoluteJoint torsoToLeftFemur = new RevoluteJoint(torso, leftFemur, new Vector2(-0.13, -1.25));
	    torsoToLeftFemur.setLimitEnabled(false);
	    torsoToLeftFemur.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToLeftFemur.setReferenceAngle(Math.toRadians(0.0));
	    torsoToLeftFemur.setMotorEnabled(false);
	    torsoToLeftFemur.setMotorSpeed(Math.toRadians(0.0));
	    torsoToLeftFemur.setMaximumMotorTorque(0.0);
	    torsoToLeftFemur.setCollisionAllowed(false);
	    world.addJoint(torsoToLeftFemur);
	    
	    // Right Femur to Right Tibia
	    RevoluteJoint rightFemurToRightTibia = new RevoluteJoint(rightFemur, rightTibia, new Vector2(0.17, -1.9));
	    rightFemurToRightTibia.setLimitEnabled(false);
	    rightFemurToRightTibia.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    rightFemurToRightTibia.setReferenceAngle(Math.toRadians(0.0));
	    rightFemurToRightTibia.setMotorEnabled(false);
	    rightFemurToRightTibia.setMotorSpeed(Math.toRadians(0.0));
	    rightFemurToRightTibia.setMaximumMotorTorque(0.0);
	    rightFemurToRightTibia.setCollisionAllowed(false);
	    world.addJoint(rightFemurToRightTibia);
	    
	    // Left Femur to Left Tibia
	    RevoluteJoint leftFemurToLeftTibia = new RevoluteJoint(leftFemur, leftTibia, new Vector2(-0.14, -1.9));
	    leftFemurToLeftTibia.setLimitEnabled(false);
	    leftFemurToLeftTibia.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    leftFemurToLeftTibia.setReferenceAngle(Math.toRadians(0.0));
	    leftFemurToLeftTibia.setMotorEnabled(false);
	    leftFemurToLeftTibia.setMotorSpeed(Math.toRadians(0.0));
	    leftFemurToLeftTibia.setMaximumMotorTorque(0.0);
	    leftFemurToLeftTibia.setCollisionAllowed(false);
	    world.addJoint(leftFemurToLeftTibia);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		// move the view a bit
		g.translate(0, 300);
		
		super.render(g, elapsedTime);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Ragdoll simulation = new Ragdoll();
		simulation.run();
	}
}
