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
package org.dyn4j.testbed.test;

import java.awt.event.KeyEvent;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;
import org.dyn4j.testbed.input.Input;
import org.dyn4j.testbed.input.Input.Hold;
import org.dyn4j.testbed.input.Keyboard;
import org.dyn4j.testbed.input.Mouse;

/**
 * Tests bodies being put to sleep.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.0
 */
public class Sleep extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Sleep";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests bodies being put to sleep. This test ensures that bodies are " +
			   "put to sleep when they come to rest.  Bodies are put to sleep after " +
			   "they are at rest for the configured sleep time";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// create the world
		this.world = new World(new AxisAlignedBounds(16.0, 15.0));
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.addListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		this.world.addBody(floor);
		
		// create the stack
		
		Rectangle rect = new Rectangle(1.0, 1.0);
		
		Entity box1 = new Entity();
		box1.addFixture(new BodyFixture(rect));
		box1.setMass();
		box1.translate(0.0, 1.0);
		this.world.addBody(box1);
		
		Entity box2 = new Entity();
		box2.addFixture(new BodyFixture(rect));
		box2.setMass();
		box2.translate(0.0, 2.0);
		this.world.addBody(box2);

		Entity box3 = new Entity();
		box3.addFixture(new BodyFixture(rect));
		box3.setMass();
		box3.translate(0.0, 3.0);
		this.world.addBody(box3);
		
		Entity box4 = new Entity();
		box4.addFixture(new BodyFixture(rect));
		box4.setMass();
		box4.translate(0.0, 4.0);
		this.world.addBody(box4);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		// register to listen for the enter key
		keyboard.add(new Input(KeyEvent.VK_ENTER, Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#poll(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// check for the enter key
		if (keyboard.isPressed(KeyEvent.VK_ENTER)) {
			// check the size of the list
			if (this.world.getBodyCount() > 1) {
				this.world.removeBody(this.world.getBody(1));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#getControls()
	 */
	@Override
	public String[][] getControls() {
		return new String[][] {
				{"Remove Body", "Removes a body from the world.", "<html><span style='color: blue;'>Enter</span></html>"},
		};
	}
}