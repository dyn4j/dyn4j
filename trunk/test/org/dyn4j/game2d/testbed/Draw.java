/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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

import java.awt.Color;

/**
 * Settings singleton for drawing.
 * @author William Bittle
 * @version 2.2.1
 * @since 1.0.0
 */
public class Draw {
	/** Whether to draw centers of mass or not */
	private boolean center = false;
	
	/** The color used when rendering the center */
	private Color centerColor = Color.GREEN;
	
	/** Whether to draw velocity vectors or not */
	private boolean velocity = false;
	
	/** The color used when rendering velocity vectors */
	private Color velocityColor = Color.MAGENTA;
	
	/** Whether to draw contacts or not */
	private boolean contacts = false;
	
	/** The color used when rendering contact points */
	private Color contactColor = Color.ORANGE;
	
	/** Whether to draw contact forces or not */
	private boolean contactForces = false;
	
	/** The color used when rendering contact forces */
	private Color contactForcesColor = Color.BLUE;
	
	/** Whether to draw contact pairs or not */
	private boolean contactPairs = false;
	
	/** The color used when rendering contact pairs */
	private Color contactPairsColor = Color.YELLOW;
	
	/** Whether to draw contact friction forces or not */
	private boolean frictionForces = false;
	
	/** The color used when rendering friction forces */
	private Color frictionForcesColor = Color.BLUE;
	
	/** Whether to draw joints or not */
	private boolean joints = true;
	
	/** Whether to draw the bounds or not */
	private boolean bounds = false;
	
	/** The color used when rendering the bounds */
	private Color boundsColor = Color.CYAN;
	
	/** Whether to draw the metrics panel or not */
	private boolean panel = true;
	
	/** Whether to fill shapes with color */
	private boolean fill = true;
	
	/** Whether to draw shape outlines */
	private boolean outline = true;
	
	/** Whether to draw edge normals */
	private boolean normals = false;
	
	/** The color used when rendering normals */
	private Color normalsColor = Color.RED;
	
	/** Whether to draw body rotation discs or not */
	private boolean rotationDisc = false;
	
	/** The color used when rendering rotation discs */
	private Color rotationDiscColor = Color.PINK;
	
	/** Whether to blur the metrics panel using a convolve-op */
	private boolean panelBlurred = false;
	
	/** Whether to use anti-aliasing */
	private boolean antiAliased = false;
	
	/** Whether to use text anti-aliasing */
	private boolean textAntiAliased = false;
	
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
	 * Returns true if velocity vectors shoudl be drawn.
	 * @return boolean
	 */
	public boolean drawVelocityVectors() {
		return velocity;
	}

	/**
	 * Sets whether the velocity vectors should be drawn.
	 * @param flag true if velocity vectors should be drawn
	 */
	public synchronized void setDrawVelocityVectors(boolean flag) {
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
	 * Returns true if contact forces should be drawn.
	 * @return boolean
	 */
	public boolean drawContactForces() {
		return this.contactForces;
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
	 * Sets whether contact forces should be drawn.
	 * @param flag true if contact forces should be drawn
	 */
	public synchronized void setDrawContactForces(boolean flag) {
		this.contactForces = flag;
	}
	
	/**
	 * Returns true if friction forces should be drawn.
	 * @return boolean
	 */
	public boolean drawFrictionForces() {
		return this.frictionForces;
	}
	
	/**
	 * Sets whether friction forces should be drawn.
	 * @param flag true if friction forces should be drawn
	 */
	public synchronized void setDrawFrictionForces(boolean flag) {
		this.frictionForces = flag;
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
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getCenterColor() {
		return centerColor;
	}
	
	/**
	 * Sets the color to use when drawing the center of mass.
	 * @param centerColor the color
	 * @since 2.1.0
	 */
	public synchronized void setCenterColor(Color centerColor) {
		this.centerColor = centerColor;
	}
	
	/**
	 * Returns the color used when drawing velocity vectors.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getVelocityColor() {
		return velocityColor;
	}
	
	/**
	 * Sets the color used when drawing velocity vectors.
	 * @param velocityColor the color
	 * @since 2.1.0
	 */
	public synchronized void setVelocityColor(Color velocityColor) {
		this.velocityColor = velocityColor;
	}
	
	/**
	 * Returns the color used when drawing contact points.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getContactColor() {
		return contactColor;
	}
	
	/**
	 * Sets the color used when drawing contact points.
	 * @param contactColor the color
	 * @since 2.1.0
	 */
	public synchronized void setContactColor(Color contactColor) {
		this.contactColor = contactColor;
	}
	
	/**
	 * Returns the color used when drawing contact normal forces.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getContactForcesColor() {
		return contactForcesColor;
	}
	
	/**
	 * Sets the color used when drawing contact normal forces.
	 * @param contactForcesColor the color
	 * @since 2.1.0
	 */
	public synchronized void setContactForcesColor(Color contactForcesColor) {
		this.contactForcesColor = contactForcesColor;
	}
	
	/**
	 * Returns the color used when drawing contact pairs.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getContactPairsColor() {
		return contactPairsColor;
	}
	
	/**
	 * Sets the color used when drawing contact pairs.
	 * @param contactPairsColor the color
	 * @since 2.1.0
	 */
	public synchronized void setContactPairsColor(Color contactPairsColor) {
		this.contactPairsColor = contactPairsColor;
	}
	
	/**
	 * Returns the color used when drawing contact friction forces.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getFrictionForcesColor() {
		return frictionForcesColor;
	}
	
	/**
	 * Sets the color used when drawing contact friction forces.
	 * @param frictionForcesColor the color
	 * @since 2.1.0
	 */
	public synchronized void setFrictionForcesColor(Color frictionForcesColor) {
		this.frictionForcesColor = frictionForcesColor;
	}
	
	/**
	 * Returns the color used when drawing the bounds.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getBoundsColor() {
		return boundsColor;
	}
	
	/**
	 * Sets the color used when drawing the bounds.
	 * @param boundsColor the color
	 * @since 2.1.0
	 */
	public synchronized void setBoundsColor(Color boundsColor) {
		this.boundsColor = boundsColor;
	}
	
	/**
	 * Returns the color used when drawing surface normals.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getNormalsColor() {
		return normalsColor;
	}
	
	/**
	 * Sets the color used when drawing surface normals.
	 * @param normalsColor the color
	 * @since 2.1.0
	 */
	public synchronized void setNormalsColor(Color normalsColor) {
		this.normalsColor = normalsColor;
	}
	
	/**
	 * Returns the color used when drawing rotation discs.
	 * @return Color
	 * @since 2.1.0
	 */
	public Color getRotationDiscColor() {
		return rotationDiscColor;
	}
	
	/**
	 * Sets the color used when drawing rotation discs.
	 * @param rotationDiscColor the color
	 * @since 2.1.0
	 */
	public synchronized void setRotationDiscColor(Color rotationDiscColor) {
		this.rotationDiscColor = rotationDiscColor;
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
	 * Returns true if text anti-aliasing should be used.
	 * @return boolean
	 * @since 2.2.1
	 */
	public boolean isTextAntiAliased() {
		return this.textAntiAliased;
	}
	
	/**
	 * Sets whether text anti-aliasing should be used.
	 * @param flag true if text anti-aliasing should be used
	 * @since 2.2.1
	 */
	public void setTextAntiAliased(boolean flag) {
		this.textAntiAliased = flag;
	}
}
