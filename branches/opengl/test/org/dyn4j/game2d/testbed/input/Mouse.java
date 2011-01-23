/*
 * Copyright (c) 2009, William Bittle
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
 *   * Neither the name of William Bittle nor the names of its contributors may be used to endorse or 
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
package org.dyn4j.game2d.testbed.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import org.dyn4j.game2d.testbed.input.Input.Hold;

/**
 * Represents a mouse input device.
 * @author William Bittle
 * @version $Revision: 424 $
 */
public class Mouse implements MouseListener, MouseWheelListener, MouseMotionListener {
	/** The mouse buttons */
	protected Map<Integer, Input> buttons = new HashMap<Integer, Input>();
		
	/** The mouse location relative to the source component */
	protected Point relativeLocation = new Point();
	
	/** The screen location of the mouse */
	protected Point screenLocation = new Point();
	
	/** The change in the x position of the mouse */
	protected int dx;
	
	/** The change in the y position of the mouse */
	protected int dy;
	
	/** The change in the scroll wheel value */
	protected int ds;
	
	/**
	 * Adds the given button.
	 * @param button the button to listen for
	 */
	public void add(Input button) {
		synchronized (this.buttons) {
			this.buttons.put(button.getEvent(), button);
		}
	}
	
	/**
	 * Removes the input with the given event code.
	 * @param code the event code
	 * @return {@link Input}
	 */
	public Input remove(int code) {
		synchronized (this.buttons) {
			return this.buttons.remove(code);
		}
	}
	
	/**
	 * Returns true if the given input has been signaled.
	 * <p>
	 * Calling this method will clear the value of the input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @param code the event code
	 * @return boolean
	 */
	public boolean isPressed(int code) {
		synchronized (this.buttons) {
			if (this.buttons.containsKey(code)) {
				return this.buttons.get(code).isPressed();
			}
		}
		return false;
	}
	
	/**
	 * Returns the value of the given input.
	 * <p>
	 * Returns zero if the input is not being listened for.
	 * <p>
	 * Calling this method will clear the value of the input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @param code the event code
	 * @return int
	 */
	public int getValue(int code) {
		synchronized (this.buttons) {
			if (this.buttons.containsKey(code)) {
				return this.buttons.get(code).getValue();
			}
		}
		return 0;
	}
	
	/**
	 * Resets the given input.
	 * @param code the event code
	 */
	public void reset(int code) {
		synchronized (this.buttons) {
			if (this.buttons.containsKey(code)) {
				this.buttons.get(code).reset();
			}
		}
	}
	
	/**
	 * Returns true if the mouse has moved.
	 * <p>
	 * Calling this method will clear the delta x and y values.
	 * @return boolean
	 */
	public synchronized boolean hasMoved() {
		boolean moved = this.dx != 0 || this.dy != 0;
		this.dx = 0;
		this.dy = 0;
		return moved;
	}
	
	/**
	 * Returns true if the mouse wheel has been scrolled.
	 * <p>
	 * Calling this method will clear the delta scroll value.
	 * @return boolean
	 */
	public synchronized boolean hasScrolled() {
		boolean scrolled = this.ds != 0;
		this.ds = 0;
		return scrolled;
	}
	
	/**
	 * Returns the change in x and y from the last call of this method.
	 * <p>
	 * Calling this method will clear the delta x and y values.
	 * @return Point
	 */
	public synchronized Point getDeltaMovement() {
		Point p = new Point(this.dx, this.dy);
		this.dx = 0;
		this.dy = 0;
		return p;
	}

	/**
	 * Returns an non zero value if the mouse wheel has been scrolled.
	 * <p>
	 * Calling this method will clear the delta scroll wheel value.
	 * @return int
	 */
	public synchronized int getScroll() {
		int ds = this.ds;
		this.ds = 0;
		return ds;
	}
	
	/**
	 * Returns the location of the mouse relative to the source component.
	 * @return Point
	 */
	public Point getRelativeLocation() {
		return this.relativeLocation;
	}
	
	/**
	 * Returns the location of the mouse in screen space.
	 * @return Point
	 */
	public Point getScreenLocation() {
		return this.screenLocation;
	}
	
	/**
	 * Clears all input mappings.
	 */
	public void clear() {
		this.buttons.clear();
		this.ds = 0;
		this.dx = 0;
		this.dy = 0;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		if (button != MouseEvent.NOBUTTON) {
			// obtain the lock on the buttons map
			synchronized (this.buttons) {
				// check for the event id
				if (this.buttons.containsKey(button)) {
					// press the input
					this.buttons.get(button).press();
				}
			}
		}
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button != MouseEvent.NOBUTTON) {
			// obtain the lock on the buttons map
			synchronized (this.buttons) {
				// check for the event id
				if (this.buttons.containsKey(button)) {
					// release the input
					this.buttons.get(button).release();
				}
			}
		}
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public synchronized void mouseWheelMoved(MouseWheelEvent e) {
		this.ds += e.getWheelRotation();
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public synchronized void mouseMoved(MouseEvent e) {
		this.dx += e.getX() - this.relativeLocation.x;
		this.dy += e.getY() - this.relativeLocation.y;
		
        // set the current mouse location
        this.relativeLocation = e.getPoint();
        this.screenLocation = e.getLocationOnScreen();
        e.consume();
	}
}
