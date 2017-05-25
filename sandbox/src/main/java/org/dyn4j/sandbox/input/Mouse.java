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
package org.dyn4j.sandbox.input;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

/**
 * Represents a polled Mouse input device.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Mouse implements MouseListener, MouseWheelListener {
	/** The map of mouse buttons */
	private Map<Integer, MouseButton> buttons = new Hashtable<Integer, MouseButton>();
	
	/** The current mouse location */
	private Point location;
	
	/** Whether the mouse has moved */
	private boolean moved;
	
	/** The scroll amount */
	private int scroll;
	
	/**
	 * Returns true if the given MouseEvent code was clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the clicked state
		return mb.wasClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was double clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasDoubleClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasDoubleClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and is waiting to be released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean isPressed(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.isPressed();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and was waiting to be released
	 * but is now released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasReleased(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasReleased();
	}
	
	/**
	 * Returns the current location of the mouse relative to
	 * the listening component.
	 * @return Point
	 */
	public Point getLocation() {
		return this.location;
	}
	
	/**
	 * Returns true if the mouse has moved.
	 * @return boolean
	 */
	public boolean hasMoved() {
		return this.moved;
	}
	
	/**
	 * Clears the state of the given MouseEvent code.
	 * @param code the MouseEvent code
	 */
	public void clear(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return;
		}
		// clear the state
		mb.clear();
	}
	
	/**
	 * Clears the state of all MouseEvents.
	 */
	public void clear() {
		Iterator<MouseButton> buttons = this.buttons.values().iterator();
		while (buttons.hasNext()) {
			buttons.next().clear();
		}
		this.moved = false;
		this.scroll = 0;
	}
	
	/**
	 * Returns true if the user has scrolled the mouse wheel.
	 * @return boolean
	 */
	public boolean hasScrolled() {
		return this.scroll != 0;
	}
	
	/**
	 * Returns the number of 'clicks' the mouse wheel has scrolled.
	 * @return int
	 */
	public int getScrollAmount() {
		return this.scroll;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		// set the value directly (since this can be a single/double/triple etc click)
		mb.setValue(e.getClickCount());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// called when a mouse button is pressed and is waiting for release
		
		// set the mouse state to pressed + held for the button
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseReleased(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// called when a mouse button is waiting for release and was released
		
		// set the mouse state to released for the button
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(false);
		mb.setWasReleased(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseDragged(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// called when a mouse button is waiting for release and the mouse is moving
		
		// set the mouse location
		this.moved = true;
		this.location = new Point(e.getX(), e.getY());
		// set the mouse button pressed flag
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseMoved(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		this.moved = true;
		this.location = new Point(e.getX(), e.getY());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseWheelMoved(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseEvent e) {
//		this.scroll += e.getWheelRotation();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.scroll += e.getWheelRotation();
	}
	
	// not used
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {}
}
