/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.collision;

import java.util.List;

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Transformable;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Test {@link Collidable} class for junit test cases.
 * @author William Bittle
 */
public class CollidableTest implements Collidable, Transformable {
	/** The collision {@link Filter} */
	protected Filter filter;
	
	/** The {@link Convex} {@link Shape} list */
	protected List<Convex> shapes;
	
	/** The {@link Transform} */
	protected Transform transform;
	
	/**
	 * Full constructor.
	 * @param shapes the {@link Convex} {@link Shape} list
	 * @param filter the collision {@link Filter}
	 */
	public CollidableTest(List<Convex> shapes, Filter filter) {
		this.shapes = shapes;
		this.filter = filter;
		this.transform = new Transform();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		return shapes;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Collidable#getTransform()
	 */
	@Override
	public Transform getTransform() {
		return transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector point) {
		this.transform.rotate(theta, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void translate(Vector vector) {
		this.transform.translate(vector);
	}
}
