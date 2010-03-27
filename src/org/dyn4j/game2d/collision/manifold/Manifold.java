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
package org.dyn4j.game2d.collision.manifold;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a collision {@link Manifold}.
 * <p>
 * A {@link Manifold} has a list of {@link ManifoldPoint}s for a given penetration normal.
 * <p>
 * All {@link ManifoldPoint}s are in world space.
 * @author William Bittle
 */
public class Manifold {
	/** The {@link ManifoldPoint} in world space */
	protected List<ManifoldPoint> points;
	
	/** The penetration normal */
	protected Vector normal;
	
	/** The reference edge index */
	protected int referenceIndex;
	
	/** The incident edge index */
	protected int incidentIndex;
	
	/**
	 * Default constructor.
	 */
	public Manifold() {
		this.points = new ArrayList<ManifoldPoint>(2);
	}
	
	/**
	 * Full constructor.
	 * @param points the manifold points
	 * @param normal the manifold normal
	 * @param referenceIndex the reference edge index
	 * @param incidentIndex the incident edge index
	 */
	public Manifold(List<ManifoldPoint> points, Vector normal, int referenceIndex, int incidentIndex) {
		this.points = points;
		this.normal = normal;
		this.referenceIndex = referenceIndex;
		this.incidentIndex = incidentIndex;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MANIFOLD[").append("{");
		int size = points.size();
		for (int i = 0; i < size; i++) {
			sb.append(this.points.get(i));
		}
		sb.append("}|").append(this.normal).append("|")
		.append(this.referenceIndex).append("|")
		.append(this.incidentIndex).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the list of manifold points.
	 * @return List&lt;{@link ManifoldPoint}&gt;
	 */
	public List<ManifoldPoint> getPoints() {
		return this.points;
	}
	
	/**
	 * Returns the normal.
	 * @return {@link Vector}
	 */
	public Vector getNormal() {
		return this.normal;
	}
	
	/**
	 * Returns the incident index.
	 * @return int
	 */
	public int getIncidentIndex() {
		return this.incidentIndex;
	}
	
	/**
	 * Returns the reference index.
	 * @return int
	 */
	public int getReferenceIndex() {
		return this.referenceIndex;
	}
}
