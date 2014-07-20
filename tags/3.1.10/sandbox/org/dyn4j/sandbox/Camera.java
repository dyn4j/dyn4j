/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.geometry.Vector2;

/**
 * Class used to encapsulate camera properties.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Camera {
	/** The zoom factor */
	protected double scale;
	
	/** The translation from 0,0 */
	protected Vector2 translation;
	
	/**
	 * Default constructor.
	 * <p>
	 * Defaults to a 1 to 1 scale and zero translation.
	 */
	public Camera() {
		this.scale = 1.0;
		this.translation = new Vector2();
	}
	
	/**
	 * Full constructor.
	 * @param scale the scale factor (pixels per meter)
	 * @param translation the translation (in world coordinates) from 0,0
	 */
	public Camera(double scale, Vector2 translation) {
		this.scale = scale;
		this.translation = translation;
	}
	
	/**
	 * Zooms out the camera.
	 */
	public void zoomOut() {
		this.scale *= 2.0;
	}
	
	/**
	 * Zooms in the camera.
	 */
	public void zoomIn() {
		this.scale *= 0.5;
	}
	
	/**
	 * Moves the camera back to the origin.
	 */
	public void toOrigin() {
		this.translation.zero();
	}
	
	/**
	 * Translates the camera the given amount along the x and y axes.
	 * @param tx the translation
	 */
	public void translate(Vector2 tx) {
		this.translate(tx.x, tx.y);
	}
	
	/**
	 * Translates the camera the given amount along the x and y axes.
	 * @param x the x translation
	 * @param y the y translation
	 */
	public void translate(double x, double y) {
		this.translation.x += x;
		this.translation.y += y;
	}
	
	// getter/setters
	
	/**
	 * Returns the scale factor.
	 * @return double
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * Sets the scale factor in pixels per meter.
	 * @param scale the desired scale factor
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	/**
	 * Returns the translation.
	 * @return Vector2
	 */
	public Vector2 getTranslation() {
		return translation;
	}
	
	/**
	 * Sets the translation from the origin in world coordinates.
	 * @param translation the translation
	 */
	public void setTranslation(Vector2 translation) {
		this.translation = translation;
	}
}
