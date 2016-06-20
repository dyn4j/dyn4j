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
package org.dyn4j.collision.manifold;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;

/**
 * Represents a {@link ManifoldPointId} that uses edge indexing.
 * <p>
 * The the edge and vertex indicies are the indicies of the edges
 * and verticies in the reference and incident {@link Convex} {@link Shape}s in
 * the collision.
 * <p>
 * The flipped flag is set when the default reference edge is swapped
 * to be the incident edge.
 * <p>
 * For a given {@link Convex} {@link Shape} the indicies should not change, although
 * there is no mechanism preventing this. In the case they change, this should only
 * affect any caching of this information.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see ManifoldPointId#DISTANCE
 */
public class IndexedManifoldPointId implements ManifoldPointId {
	/** The reference edge index */
	private final int referenceEdge;
	
	/** The incident edge index */
	private final int incidentEdge;
	
	/** The index of the incident vertex */
	private final int incidentVertex;
	
	/** Whether the reference and incident features flipped */
	private final boolean flipped;
	
	/**
	 * Optional constructor.
	 * @param referenceEdge the reference edge index
	 * @param incidentEdge the incident edge index
	 * @param incidentVertex the incident vertex index
	 * @since 3.1.5
	 */
	public IndexedManifoldPointId(int referenceEdge, int incidentEdge, int incidentVertex) {
		this(referenceEdge, incidentEdge, incidentVertex, false);
	}
	
	/**
	 * Full constructor.
	 * @param referenceEdge the reference edge index
	 * @param incidentEdge the incident edge index
	 * @param incidentVertex the incident vertex index
	 * @param flipped whether the reference and incident features flipped
	 */
	public IndexedManifoldPointId(int referenceEdge, int incidentEdge, int incidentVertex, boolean flipped) {
		this.referenceEdge = referenceEdge;
		this.incidentEdge = incidentEdge;
		this.incidentVertex = incidentVertex;
		this.flipped = flipped;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof IndexedManifoldPointId) {
			IndexedManifoldPointId o = (IndexedManifoldPointId) other;
			if (this.referenceEdge == o.referenceEdge
			 && this.incidentEdge == o.incidentEdge
			 && this.incidentVertex == o.incidentVertex
			 && this.flipped == o.flipped) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = this.referenceEdge;
		hash = 37 * hash + this.incidentEdge;
		hash = 37 * hash + this.incidentVertex;
		hash = 37 * hash + (this.flipped ? 1231 : 1237);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IndexedManifoldPointId[ReferenceEdge=").append(this.referenceEdge)
		.append("|IncidentEdge=").append(this.incidentEdge)
		.append("|IncidentVertex=").append(this.incidentVertex)
		.append("|IsFlipped=").append(this.flipped)
		.append("]");
		return sb.toString();
	}

	/**
	 * Returns the reference edge index of this manifold
	 * on the {@link Shape}.
	 * <p>
	 * The reference edge is the edge that is most perpendicular to the collision normal.
	 * @return int
	 */
	public int getReferenceEdge() {
		return this.referenceEdge;
	}

	/**
	 * Returns the incident edge index of this manifold
	 * on the other {@link Shape}.
	 * @return int
	 */
	public int getIncidentEdge() {
		return this.incidentEdge;
	}

	/**
	 * Returns the index of the deepest collision point of the incident edge of this manifold on
	 * the other {@link Shape}.
	 * @return int
	 */
	public int getIncidentVertex() {
		return this.incidentVertex;
	}

	/**
	 * Returns true if the reference edge and incident edges were swapped.
	 * @return boolean
	 */
	public boolean isFlipped() {
		return this.flipped;
	}
}
