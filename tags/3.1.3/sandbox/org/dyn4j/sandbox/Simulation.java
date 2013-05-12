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
package org.dyn4j.sandbox;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Class to store a simulation.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public class Simulation {
	/** The default simulation name */
	public static final String DEFAULT_SIMULATION_NAME = Messages.getString("world.name.default");
	
	/** The lock used to make changes to the simulation */
	public static final Object LOCK = new Object();
	
	/** The sandbox camera */
	protected Camera camera;
	
	/** The current list of rays */
	protected List<SandboxRay> rays;
	
	/** The world */
	protected World world;
	
	/** The contact counter */
	protected ContactCounter contactCounter;
	
	/**
	 * Creates a default simulation.
	 */
	public Simulation() {
		this.camera = new Camera(32, new Vector2());
		this.rays = new ArrayList<SandboxRay>();
		this.world = new World();
		this.world.setUserData(DEFAULT_SIMULATION_NAME);
		this.contactCounter = new ContactCounter();
		this.world.addListener(this.contactCounter);
	}
	
	/**
	 * Creates a simulation from the given information.
	 * @param camera the camera
	 * @param rays the rays
	 * @param world the world
	 */
	public Simulation(Camera camera, List<SandboxRay> rays, World world) {
		this.camera = camera;
		this.rays = rays;
		this.world = world;
		this.contactCounter = new ContactCounter();
		this.world.addListener(this.contactCounter);
	}
	
	/**
	 * Returns the simulation camera.
	 * @return Camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Returns the list of rays.
	 * @return List&lt;SandboxRay&gt;
	 */
	public List<SandboxRay> getRays() {
		return rays;
	}
	
	/**
	 * Returns the world.
	 * @return World
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Returns the contact counter.
	 * @return ContactCounter
	 */
	public ContactCounter getContactCounter() {
		return contactCounter;
	}
}
