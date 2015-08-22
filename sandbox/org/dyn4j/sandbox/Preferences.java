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
package org.dyn4j.sandbox;

/**
 * Simple thread safe class to manage preferences.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Preferences {
	// body related settings
	
	/** True if a static color should be used or a random color for bodies */
	private static boolean bodyColorRandom = false;
	
	/** True if the body should be stenciled or rendered normally */
	private static boolean bodyStenciled = false;
	
	/** True if labels should be shown for bodies */
	private static boolean bodyLabeled = false;
	
	/** True if labels should be shown for fixtures */
	private static boolean fixtureLabeled = false;
	
	/** True if bodies that are asleep should be recolored */
	private static boolean bodyAsleepColorEnabled = true;
	
	/** The color to use for asleep bodies */
	private static float[] bodyAsleepColor = new float[] {0.78f, 0.78f, 1.0f, 1.0f};

	/** True if bodies that are inactive should be recolored */
	private static boolean bodyInActiveColorEnabled = true;
	
	/** The color to use for inactive bodies */
	private static float[] bodyInActiveColor = new float[] {1.0f, 1.0f, 0.78f, 1.0f};
	
	/** True if the body center should be rendered */
	private static boolean bodyCenterEnabled = true;
	
	/** The color to use for the body center */
	private static float[] bodyCenterColor = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	
	/** True if body AABBs should be rendered */
	private static boolean bodyAABBEnabled = false;
	
	/** The color to use for body AABBs */
	private static float[] bodyAABBColor = new float[] {0.8f, 0.8f, 0.8f, 1.0f};
	
	/** True if body fixture normals should be rendered */
	private static boolean bodyNormalEnabled = false;
	
	/** The color to use for body fixture normals */
	private static float[] bodyNormalColor = new float[] {1.0f, 0.0f, 0.0f, 1.0f};
	
	/** True if body rotation discs should be rendered */
	private static boolean bodyRotationDiscEnabled = false;
	
	/** The color to use for body rotation discs */
	private static float[] bodyRotationDiscColor = new float[] {1.0f, 0.8f, 0.8f, 1.0f};
	
	/** True if body velocities should be rendered */
	private static boolean bodyVelocityEnabled = false;
	
	/** The color to use for body velocities */
	private static float[] bodyVelocityColor = new float[] {1.0f, 0.0f, 1.0f, 1.0f};
	
	// other settings
	
	/** True if anti-aliasing should be used (2X MSAA) */
	private static boolean antiAliasingEnabled = false;
	
	/** True if vertical sync should be enabled */
	private static boolean verticalSyncEnabled = true;
	
	/** True if the origin point should be shown */
	private static boolean originLabeled = true;
	
	/** The color for the selected body or fixture border */
	private static float[] selectedColor = new float[] {0.5f, 0.5f, 1.0f, 1.0f};
	
	/** True if the world bounds should be rendered */
	private static boolean boundsEnabled = true;
	
	/** The color to use when rendering the bounds */
	private static float[] boundsColor = new float[] {0.6f, 0.6f, 0.6f, 1.0f};
	
	/** True if the scale should be rendered */
	private static boolean scaleEnabled = true;
	
	/** True if contact pairs should be rendered */
	private static boolean contactPairEnabled = false;
	
	/** The color to use for contact pairs */
	private static float[] contactPairColor = new float[] {1.0f, 1.0f, 0.0f, 1.0f};
	
	/** True if contact points should be rendered */
	private static boolean contactPointEnabled = false;
	
	/** The color to use for contact points */
	private static float[] contactPointColor = new float[] {1.0f, 0.6f, 0.0f, 1.0f};
	
	/** True if contact impulses should be rendered */
	private static boolean contactImpulseEnabled = false;
	
	/** The color to use for contact impulses */
	private static float[] contactImpulseColor = new float[] {0.0f, 0.0f, 1.0f, 1.0f};
	
	/** True if friction impulses should be rendered */
	private static boolean frictionImpulseEnabled = false;
	
	/** The color to use for friction impulses */
	private static float[] frictionImpulseColor = new float[] {0.0f, 0.0f, 1.0f, 1.0f};
	
	/**
	 * Returns true if random colors should be used for new bodies.
	 * @return boolean
	 */
	public static synchronized boolean isBodyColorRandom() {
		return bodyColorRandom;
	}
	
	/**
	 * Sets the random color flag.
	 * @param flag true if new bodies should have a randomly generated color
	 */
	public static synchronized void setBodyColorRandom(boolean flag) {
		bodyColorRandom = flag;
	}
	
	/**
	 * Returns true if bodies should be stenciled instead of rendered normally.
	 * @return boolean
	 */
	public static synchronized boolean isBodyStenciled() {
		return bodyStenciled;
	}
	
	/**
	 * Sets the stencil flag.
	 * @param flag true if bodies should be stenciled instead of rendered normally
	 */
	public static synchronized void setBodyStenciled(boolean flag) {
		bodyStenciled = flag;
	}
	
	/**
	 * Returns true if body labels should be shown.
	 * @return boolean
	 */
	public static synchronized boolean isBodyLabeled() {
		return bodyLabeled;
	}
	
	/**
	 * Sets the body labels flag.
	 * @param flag true if body labels should be shown
	 */
	public static synchronized void setBodyLabeled(boolean flag) {
		bodyLabeled = flag;
	}

	/**
	 * Returns true if body labels should be shown.
	 * @return boolean
	 */
	public static synchronized boolean isFixtureLabeled() {
		return fixtureLabeled;
	}
	
	/**
	 * Sets the body labels flag.
	 * @param flag true if body labels should be shown
	 */
	public static synchronized void setFixtureLabeled(boolean flag) {
		fixtureLabeled = flag;
	}
	
	/**
	 * Returns true if anti-aliasing should be used.
	 * @return boolean
	 */
	public static synchronized boolean isAntiAliasingEnabled() {
		return antiAliasingEnabled;
	}
	
	/**
	 * Sets the anti-aliasing flag.
	 * @param flag true if anti-aliasing should be used
	 */
	public static synchronized void setAntiAliasingEnabled(boolean flag) {
		antiAliasingEnabled = flag;
	}
	
	/**
	 * Returns true if vertical sync should be enabled.
	 * @return boolean
	 */
	public static synchronized boolean isVerticalSyncEnabled() {
		return verticalSyncEnabled;
	}
	
	/**
	 * Sets the vertical sync flag.
	 * @param flag true if vertical sync should be enabled
	 */
	public static synchronized void setVerticalSyncEnabled(boolean flag) {
		verticalSyncEnabled = flag;
	}
	
	/**
	 * Returns true if the origin and origin label should be shown.
	 * @return boolean
	 */
	public static synchronized boolean isOriginLabeled() {
		return originLabeled;
	}
	
	/**
	 * Sets the origin label flag.
	 * @param flag true if the origin and origin label should be shown
	 */
	public static synchronized void setOriginLabeled(boolean flag) {
		originLabeled = flag;
	}
	
	/**
	 * Returns true if a different color should be used for asleep bodies.
	 * @return boolean
	 */
	public static boolean isBodyAsleepColorEnabled() {
		return bodyAsleepColorEnabled;
	}
	
	/**
	 * Enables or disables using a different color for bodies that are asleep.
	 * @param flag true if a different color should be used to render asleep bodies
	 */
	public static void setBodyAsleepColorEnabled(boolean flag) {
		Preferences.bodyAsleepColorEnabled = flag;
	}
	
	/**
	 * Returns the color used when bodies are put to sleep.
	 * @return float[]
	 */
	public static float[] getBodyAsleepColor() {
		return bodyAsleepColor;
	}
	
	/**
	 * Sets the color used when bodies are put to sleep.
	 * @param color the color
	 */
	public static void setBodyAsleepColor(float[] color) {
		Preferences.bodyAsleepColor = color;
	}

	/**
	 * Returns true if a different color should be used for inactive bodies.
	 * @return boolean
	 */
	public static boolean isBodyInActiveColorEnabled() {
		return bodyInActiveColorEnabled;
	}
	
	/**
	 * Enables or disables using a different color for bodies that are inactive.
	 * @param flag true if a different color should be used to render inactive bodies
	 */
	public static void setBodyInActiveColorEnabled(boolean flag) {
		Preferences.bodyInActiveColorEnabled = flag;
	}
	
	/**
	 * Returns the color used when bodies are in active.
	 * @return float[]
	 */
	public static float[] getBodyInActiveColor() {
		return bodyInActiveColor;
	}
	
	/**
	 * Sets the color used when bodies are in active.
	 * @param color the color
	 */
	public static void setBodyInActiveColor(float[] color) {
		Preferences.bodyInActiveColor = color;
	}
	
	/**
	 * Returns true if the center of mass should be rendered for bodies.
	 * @return boolean
	 */
	public static boolean isBodyCenterEnabled() {
		return bodyCenterEnabled;
	}
	
	/**
	 * Enables or disables the rendering of the center of mass for bodies.
	 * @param flag true if the center of mass should be rendered
	 */
	public static void setBodyCenterEnabled(boolean flag) {
		Preferences.bodyCenterEnabled = flag;
	}
	
	/**
	 * Returns the color used to render the body center.
	 * @return float[]
	 */
	public static float[] getBodyCenterColor() {
		return bodyCenterColor;
	}
	
	/**
	 * Sets the color used to render the body center.
	 * @param color the color
	 */
	public static void setBodyCenterColor(float[] color) {
		Preferences.bodyCenterColor = color;
	}
	
	/**
	 * Returns the color used to outline selected bodies or fixtures.
	 * @return float[]
	 */
	public static float[] getSelectedColor() {
		return selectedColor;
	}
	
	/**
	 * Sets the color used to outline selected bodies or fixtures.
	 * @param color the color
	 */
	public static void setSelectedColor(float[] color) {
		Preferences.selectedColor = color;
	}
	
	/**
	 * Returns true if the bounds should be rendered.
	 * @return boolean
	 */
	public static boolean isBoundsEnabled() {
		return boundsEnabled;
	}
	
	/**
	 * Enables or disables the rendering of the bounds.
	 * @param flag true if the bounds should be rendered
	 */
	public static void setBoundsEnabled(boolean flag) {
		Preferences.boundsEnabled = flag;
	}
	
	/**
	 * Returns the color used to render the bounds.
	 * @return float[]
	 */
	public static float[] getBoundsColor() {
		return boundsColor;
	}

	/**
	 * Sets the color used to render the bounds.
	 * @param color the bounds color
	 */
	public static void setBoundsColor(float[] color) {
		Preferences.boundsColor = color;
	}

	/**
	 * Returns true if the scale should be rendered.
	 * @return boolean
	 */
	public static boolean isScaleEnabled() {
		return scaleEnabled;
	}
	
	/**
	 * Enables or disables the rendering of the scale.
	 * @param flag true if the scale should be rendered
	 */
	public static void setScaleEnabled(boolean flag) {
		scaleEnabled = flag;
	}
	
	/**
	 * Returns true if body AABBs should be rendered.
	 * @return boolean
	 */
	public static boolean isBodyAABBEnabled() {
		return bodyAABBEnabled;
	}
	
	/**
	 * Enables or disables the rendering of body AABBs.
	 * @param flag true if body AABBs should be rendered
	 */
	public static void setBodyAABBEnabled(boolean flag) {
		Preferences.bodyAABBEnabled = flag;
	}
	
	/**
	 * Returns the color used to render body AABBs.
	 * @return float[]
	 */
	public static float[] getBodyAABBColor() {
		return bodyAABBColor;
	}
	
	/**
	 * Sets the color to use when rendering body AABBs.
	 * @param color the color to use when rendering body AABBs
	 */
	public static void setBodyAABBColor(float[] color) {
		Preferences.bodyAABBColor = color;
	}
	
	/**
	 * Returns true if body normals should be rendered.
	 * @return boolean
	 */
	public static boolean isBodyNormalEnabled() {
		return bodyNormalEnabled;
	}
	
	/**
	 * Enables or disables the rendering of body normals.
	 * @param flag true if body normals should be rendered
	 */
	public static void setBodyNormalEnabled(boolean flag) {
		Preferences.bodyNormalEnabled = flag;
	}
	
	/**
	 * Returns the color used to render body fixture normals.
	 * @return float[]
	 */
	public static float[] getBodyNormalColor() {
		return bodyNormalColor;
	}
	
	/**
	 * Sets the color used to render body fixture normals.
	 * @param color the color
	 */
	public static void setBodyNormalColor(float[] color) {
		Preferences.bodyNormalColor = color;
	}
	
	/**
	 * Returns true if the body rotation disc should be rendered.
	 * @return boolean
	 */
	public static boolean isBodyRotationDiscEnabled() {
		return bodyRotationDiscEnabled;
	}
	
	/**
	 * Enables or disables the rendering of body rotation discs.
	 * @param flag true if body rotation discs should be rendered
	 */
	public static void setBodyRotationDiscEnabled(boolean flag) {
		Preferences.bodyRotationDiscEnabled = flag;
	}
	
	/**
	 * Returns the color used to render body rotation discs.
	 * @return float[]
	 */
	public static float[] getBodyRotationDiscColor() {
		return bodyRotationDiscColor;
	}
	
	/**
	 * Sets the color used to render body rotation discs.
	 * @param color the color
	 */
	public static void setBodyRotationDiscColor(float[] color) {
		Preferences.bodyRotationDiscColor = color;
	}
	
	/**
	 * Returns true if body velocities should be rendered.
	 * @return boolean
	 */
	public static boolean isBodyVelocityEnabled() {
		return bodyVelocityEnabled;
	}
	
	/**
	 * Enables or disables the rendering of body velocity vectors.
	 * @param flag true if velocities should be rendered
	 */
	public static void setBodyVelocityEnabled(boolean flag) {
		Preferences.bodyVelocityEnabled = flag;
	}
	
	/**
	 * Returns the color used to render body velocities.
	 * @return float[]
	 */
	public static float[] getBodyVelocityColor() {
		return bodyVelocityColor;
	}
	
	/**
	 * Sets the color used to render body velocities.
	 * @param color the color
	 */
	public static void setBodyVelocityColor(float[] color) {
		Preferences.bodyVelocityColor = color;
	}
	
	/**
	 * Returns true if contact pairs should be rendered.
	 * @return boolean
	 */
	public static boolean isContactPairEnabled() {
		return contactPairEnabled;
	}
	
	/**
	 * Enables or disables the rendering of contact pairs.
	 * @param flag true if contact pairs should be rendered
	 */
	public static void setContactPairEnabled(boolean flag) {
		Preferences.contactPairEnabled = flag;
	}
	
	/**
	 * Returns the color used to render contact pairs.
	 * @return float[]
	 */
	public static float[] getContactPairColor() {
		return contactPairColor;
	}
	
	/**
	 * Sets the color used to render contact pairs.
	 * @param color the color
	 */
	public static void setContactPairColor(float[] color) {
		Preferences.contactPairColor = color;
	}
	
	/**
	 * Returns true if contact points should be rendered.
	 * @return boolean
	 */
	public static boolean isContactPointEnabled() {
		return contactPointEnabled;
	}
	
	/**
	 * Enables or disables the rendering of contact points.
	 * @param flag true if contact points should be rendered
	 */
	public static void setContactPointEnabled(boolean flag) {
		Preferences.contactPointEnabled = flag;
	}
	
	/**
	 * Returns the color used to render contact points.
	 * @return float[]
	 */
	public static float[] getContactPointColor() {
		return contactPointColor;
	}
	
	/**
	 * Sets the color used to render contact points.
	 * @param color the color
	 */
	public static void setContactPointColor(float[] color) {
		Preferences.contactPointColor = color;
	}
	
	/**
	 * Returns true if contact impulses should be rendered.
	 * @return boolean
	 */
	public static boolean isContactImpulseEnabled() {
		return contactImpulseEnabled;
	}
	
	/**
	 * Enables or disables the rendering of contact impulses.
	 * @param flag true if contact impulses should be rendered
	 */
	public static void setContactImpulseEnabled(boolean flag) {
		Preferences.contactImpulseEnabled = flag;
	}
	
	/**
	 * Returns the color used to render contact impulses
	 * @return float[]
	 */
	public static float[] getContactImpulseColor() {
		return contactImpulseColor;
	}
	
	/**
	 * Sets the color to use for contact impulses.
	 * @param color the color
	 */
	public static void setContactImpulseColor(float[] color) {
		Preferences.contactImpulseColor = color;
	}
	
	/**
	 * Returns true if friction impulses should be rendered.
	 * @return boolean
	 */
	public static boolean isFrictionImpulseEnabled() {
		return frictionImpulseEnabled;
	}
	
	/**
	 * Enables or disables rendering of friction impulses.
	 * @param flag true if friction impulses should be rendered
	 */
	public static void setFrictionImpulseEnabled(boolean flag) {
		Preferences.frictionImpulseEnabled = flag;
	}
	
	/**
	 * Returns the color used to render friction impulses.
	 * @return float[]
	 */
	public static float[] getFrictionImpulseColor() {
		return frictionImpulseColor;
	}
	
	/**
	 * Sets the color to use to render friction impulses.
	 * @param color the color
	 */
	public static void setFrictionImpulseColor(float[] color) {
		Preferences.frictionImpulseColor = color;
	}
}
