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
package org.dyn4j.collision;

import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Translatable;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract implementation of the {@link Bounds} interface.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public abstract class AbstractBounds implements Bounds, Translatable {
	/** The {@link Bounds} {@link Transform} */
	protected Transform transform;
	
	/**
	 * Default constructor.
	 */
	public AbstractBounds() {
		this.transform = new Transform();
	}

	/**
	 * Optional constructor.
	 * @param x the initial x translation of the bounds
	 * @param y the initial x translation of the bounds
	 */
	public AbstractBounds(double x, double y) {
		this();
		this.translate(x, y);
	}
	
	/**
	 * Optional constructor.
	 * @param translation the initial translation of the bounds
	 */
	public AbstractBounds(Vector2 translation) {
		this();
		this.translate(translation);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Bounds#getTranslation()
	 */
	@Override
	public Vector2 getTranslation() {
		return this.transform.getTranslation();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(org.dyn4j.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.transform.translate(vector);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		this.transform.translate(shift);
	}
}
