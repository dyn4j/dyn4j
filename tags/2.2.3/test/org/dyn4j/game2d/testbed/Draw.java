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
package org.dyn4j.game2d.testbed;

/**
 * Settings singleton for drawing.
 * <p>
 * Colors are represented by float arrays of size 4.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Draw {
	/** Black color */
	public static final float[] BLACK = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	/** Dark grey color */
	public static final float[] DARK_GREY = new float[] {0.2f, 0.2f, 0.2f, 1.0f};
	/** Grey color */
	public static final float[] GREY = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
	/** Light grey color */
	public static final float[] LIGHT_GREY = new float[] {0.8f, 0.8f, 0.8f, 1.0f};
	/** White color */
	public static final float[] WHITE = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	/** Cyan color */
	public static final float[] CYAN = new float[] {0.0f, 1.0f, 1.0f, 1.0f};
	/** Blue color */
	public static final float[] BLUE = new float[] {0.0f, 0.0f, 1.0f, 1.0f};
	/** Yellow color */
	public static final float[] YELLOW = new float[] {1.0f, 1.0f, 0.0f, 1.0f};
	/** Green color */
	public static final float[] GREEN = new float[] {0.0f, 1.0f, 0.0f, 1.0f};
	/** Magenta color */
	public static final float[] MAGENTA = new float[] {1.0f, 0.0f, 1.0f, 1.0f};
	/** Orange color */
	public static final float[] ORANGE = new float[] {1.0f, 0.6f, 0.0f, 1.0f};
	/** Red color */
	public static final float[] RED = new float[] {1.0f, 0.0f, 0.0f, 1.0f};
	/** Pink color */
	public static final float[] PINK = new float[] {1.0f, 0.8f, 0.8f, 1.0f};
	
	/** Whether to draw centers of mass or not */
	private boolean center = false;
	
	/** The color used when rendering the center */
	private float[] centerColor = Draw.GREEN;
	
	/** Whether to draw velocity vectors or not */
	private boolean velocity = false;
	
	/** The color used when rendering velocity vectors */
	private float[] velocityColor = Draw.MAGENTA;
	
	/** Whether to draw contacts or not */
	private boolean contacts = false;
	
	/** The color used when rendering contact points */
	private float[] contactColor = Draw.ORANGE;
	
	/** Whether to draw contact impulses or not */
	private boolean contactImpulses = false;
	
	/** The color used when rendering contact forces */
	private float[] contactImpulsesColor = Draw.BLUE;
	
	/** Whether to draw contact pairs or not */
	private boolean contactPairs = false;
	
	/** The color used when rendering contact pairs */
	private float[] contactPairsColor = Draw.YELLOW;
	
	/** Whether to draw contact friction forces or not */
	private boolean frictionImpulses = false;
	
	/** The color used when rendering friction forces */
	private float[] frictionImpulsesColor = Draw.BLUE;
	
	/** Whether to draw joints or not */
	private boolean joints = true;
	
	/** Whether to draw the bounds or not */
	private boolean bounds = false;
	
	/** The color used when rendering the bounds */
	private float[] boundsColor = Draw.CYAN;
	
	/** Whether to draw the metrics panel or not */
	private boolean panel = true;
	
	/** Whether to fill shapes with color */
	private boolean fill = true;
	
	/** Whether to draw shape outlines */
	private boolean outline = true;
	
	/** Whether to draw edge normals */
	private boolean normals = false;
	
	/** The color used when rendering normals */
	private float[] normalsColor = Draw.RED;
	
	/** Whether to draw body rotation discs or not */
	private boolean rotationDisc = false;
	
	/** The color used when rendering rotation discs */
	private float[] rotationDiscColor = Draw.PINK;
	
	/** Whether to blur the metrics panel using a convolve-op */
	private boolean panelBlurred = true;
	
	/** Whether to use anti-aliasing */
	private boolean antiAliased = false;
	
	/** Whether vertical sync is enabled */
	private boolean verticalSyncEnabled = false;
	
	/** The singleton instance */
	private static final Draw instance = new Draw();
	
	/**
	 * Hidden default constructor.
	 * @since 2.1.0
	 */
	private Draw() {}
	
	/**
	 * Returns the singleton instance.
	 * @return {@link Draw} the singleton instance
	 */
	public synchronized static Draw getInstance() {
		return instance;
	}

	/**
	 * Returns true if centers of mass should be drawn.
	 * @return boolean
	 */
	public boolean drawCenter() {
		return center;
	}

	/**
	 * Sets whether the centers of mass should be drawn.
	 * @param flag true if centers of mass should be drawn
	 */
	public synchronized void setDrawCenter(boolean flag) {
		this.center = flag;
	}

	/**
	 * Returns true if velocity vectors should be drawn.
	 * @return boolean
	 */
	public boolean drawVelocity() {
		return velocity;
	}

	/**
	 * Sets whether the velocity vectors should be drawn.
	 * @param flag true if velocity vectors should be drawn
	 */
	public synchronized void setDrawVelocity(boolean flag) {
		this.velocity = flag;
	}

	/**
	 * Returns true if contacts should be drawn.
	 * @return boolean
	 */
	public boolean drawContacts() {
		return contacts;
	}

	/**
	 * Sets whether contacts should be drawn.
	 * @param flag true if contacts should be drawn
	 */
	public synchronized void setDrawContacts(boolean flag) {
		this.contacts = flag;
	}
	
	/**
	 * Returns true if contact impulses should be drawn.
	 * @return boolean
	 */
	public boolean drawContactImpulses() {
		return this.contactImpulses;
	}
	
	/**
	 * Returns true if contact pairs should be drawn.
	 * @return boolean
	 */
	public boolean drawContactPairs() {
		return contactPairs;
	}

	/**
	 * Sets whether contact pairs should be drawn.
	 * @param flag true if contact pairs should be drawn
	 */
	public void setDrawContactPairs(boolean flag) {
		this.contactPairs = flag;
	}

	/**
	 * Sets whether contact impulses should be drawn.
	 * @param flag true if contact impulses should be drawn
	 */
	public synchronized void setDrawContactImpulses(boolean flag) {
		this.contactImpulses = flag;
	}
	
	/**
	 * Returns true if friction impulses should be drawn.
	 * @return boolean
	 */
	public boolean drawFrictionImpulses() {
		return this.frictionImpulses;
	}
	
	/**
	 * Sets whether friction impulses should be drawn.
	 * @param flag true if friction impulses should be drawn
	 */
	public synchronized void setDrawFrictionImpulses(boolean flag) {
		this.frictionImpulses = flag;
	}
	
	/**
	 * Returns true if joints should be drawn.
	 * @return boolean
	 */
	public boolean drawJoints() {
		return this.joints;
	}
	
	/**
	 * Sets whether joints should be drawn.
	 * @param flag true if joints should be drawn
	 */
	public synchronized void setDrawJoints(boolean flag) {
		this.joints = flag;
	}

	/**
	 * Returns true if the bounds should be drawn.
	 * @return boolean
	 */
	public boolean drawBounds() {
		return bounds;
	}

	/**
	 * Sets whether the bounds should be drawn.
	 * @param flag true if the bounds should be drawn
	 */
	public synchronized void setDrawBounds(boolean flag) {
		this.bounds = flag;
	}

	/**
	 * Returns true if the metrics panel should be drawn.
	 * @return boolean
	 */
	public boolean drawPanel() {
		return panel;
	}

	/**
	 * Sets whether the metrics panel should be drawn.
	 * @param flag true if the metrics panel should be drawn
	 */
	public synchronized void setDrawPanel(boolean flag) {
		this.panel = flag;
	}
	
	/**
	 * Returns true if shapes should be filled with a random color.
	 * @return boolean
	 */
	public boolean drawFill() {
		return fill;
	}
	
	/**
	 * Sets whether shapes should be filled with a random color.
	 * @param flag true if shapes should be filled
	 */
	public synchronized void setDrawFill(boolean flag) {
		this.fill = flag;
	}
	
	/**
	 * Returns true if shape outlines should be drawn.
	 * @return boolean
	 */
	public boolean drawOutline() {
		return outline;
	}
	
	/**
	 * Sets whether a shapes outlines should be drawn.
	 * @param flag true if shape outlines should be drawn
	 */
	public synchronized void setDrawOutline(boolean flag) {
		this.outline = flag;
	}
	
	/**
	 * Returns true if shape edge normals should be drawn.
	 * @return boolean
	 */
	public boolean drawNormals() {
		return normals;
	}
	
	/**
	 * Sets whether a shape edge normals should be drawn.
	 * @param flag true if shape edge normals should be drawn
	 */
	public synchronized void setDrawNormals(boolean flag) {
		this.normals = flag;
	}
	
	/**
	 * Returns true if body rotation discs should be drawn.
	 * @return boolean
	 * @since 2.0.0
	 */
	public boolean drawRotationDisc() {
		return this.rotationDisc;
	}
	
	/**
	 * Sets whether body rotation discs should be drawn.
	 * @param flag true if body rotation discs should be drawn
	 * @since 2.0.0
	 */
	public synchronized void setDrawRotationDisc(boolean flag) {
		this.rotationDisc = flag;
	}
	
	/**
	 * Returns the color used when drawing the center of mass.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getCenterColor() {
		return centerColor;
	}
	
	/**
	 * Sets the color to use when drawing the center of mass.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setCenterColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The center color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.centerColor = color;
	}
	
	/**
	 * Returns the color used when drawing velocity vectors.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getVelocityColor() {
		return velocityColor;
	}
	
	/**
	 * Sets the color used when drawing velocity vectors.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setVelocityColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The velocity color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.velocityColor = color;
	}
	
	/**
	 * Returns the color used when drawing contact points.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getContactColor() {
		return contactColor;
	}
	
	/**
	 * Sets the color used when drawing contact points.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setContactColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The contact point color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.contactColor = color;
	}
	
	/**
	 * Returns the color used when drawing contact normal forces.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getContactImpulsesColor() {
		return contactImpulsesColor;
	}
	
	/**
	 * Sets the color used when drawing contact normal forces.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setContactImpulsesColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The contact forces color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.contactImpulsesColor = color;
	}
	
	/**
	 * Returns the color used when drawing contact pairs.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getContactPairsColor() {
		return contactPairsColor;
	}
	
	/**
	 * Sets the color used when drawing contact pairs.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setContactPairsColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The contact pair color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.contactPairsColor = color;
	}
	
	/**
	 * Returns the color used when drawing contact friction forces.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getFrictionImpulsesColor() {
		return frictionImpulsesColor;
	}
	
	/**
	 * Sets the color used when drawing contact friction forces.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setFrictionImpulsesColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The friction forces color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.frictionImpulsesColor = color;
	}
	
	/**
	 * Returns the color used when drawing the bounds.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getBoundsColor() {
		return boundsColor;
	}
	
	/**
	 * Sets the color used when drawing the bounds.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setBoundsColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The bounds color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.boundsColor = color;
	}
	
	/**
	 * Returns the color used when drawing surface normals.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getNormalsColor() {
		return normalsColor;
	}
	
	/**
	 * Sets the color used when drawing surface normals.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setNormalsColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The normals color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.normalsColor = color;
	}
	
	/**
	 * Returns the color used when drawing rotation discs.
	 * @return float[]
	 * @since 2.2.3
	 */
	public float[] getRotationDiscColor() {
		return rotationDiscColor;
	}
	
	/**
	 * Sets the color used when drawing rotation discs.
	 * @param color the color
	 * @since 2.2.3
	 */
	public synchronized void setRotationDiscColor(float[] color) {
		// check for null
		if (color == null) throw new NullPointerException("The rotation disc color cannot be null.");
		// check the size
		if (color.length != 4) throw new IndexOutOfBoundsException("Colors must be arrays of 4 elements.");
		// set the value
		this.rotationDiscColor = color;
	}
	
	/**
	 * Returns true if the metrics panel background should be blurred.
	 * @return boolean
	 * @since 2.2.1
	 */
	public boolean isPanelBlurred() {
		return panelBlurred;
	}
	
	/**
	 * Sets whether the background of the metrics panel should be blurred or not.
	 * @param flag true if the metrics panel's background should be blurred
	 * @since 2.2.1
	 */
	public synchronized void setPanelBlurred(boolean flag) {
		this.panelBlurred = flag;
	}
	
	/**
	 * Returns true if anti-aliasing should be used.
	 * @return boolean
	 * @since 2.2.1
	 */
	public boolean isAntiAliased() {
		return this.antiAliased;
	}
	
	/**
	 * Sets whether anti-aliasing should be used.
	 * @param flag true if anti-aliasing should be used
	 * @since 2.2.1
	 */
	public void setAntiAliased(boolean flag) {
		this.antiAliased = flag;
	}
	
	/**
	 * Returns true if vertical sync is enabled.
	 * @return boolean
	 * @since 2.2.3
	 */
	public boolean isVerticalSyncEnabled() {
		return this.verticalSyncEnabled;
	}
	
	/**
	 * Sets whether vertical sync is enabled or not.
	 * @param flag true if vertical sync should be enabled
	 * @since 2.2.3
	 */
	public void setVerticalSyncEnabled(boolean flag) {
		this.verticalSyncEnabled = flag;
	}
}
