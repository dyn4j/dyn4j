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

/**
 * Represents a {@link ManifoldPointId} that uses indexing.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class IndexedManifoldPointId implements ManifoldPointId {
	/** The reference edge index */
	protected int referenceEdge;
	
	/** The incident edge index */
	protected int incidentEdge;
	
	/** The index of the incident vertex */
	protected int incidentVertex;
	
	/** Whether the reference and incident features flipped */
	protected boolean flipped;
	
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("INDEXED_MANIFOLD_POINT_ID[")
		.append(this.referenceEdge).append("|")
		.append(this.incidentEdge).append("|")
		.append(this.incidentVertex).append("|")
		.append(this.flipped).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the index of the reference edge.
	 * @return int
	 */
	public int getReferenceEdge() {
		return referenceEdge;
	}
	
	/**
	 * Returns the index of the incident edge.
	 * @return int
	 */
	public int getIncidentEdge() {
		return incidentEdge;
	}
	
	/**
	 * Returns the index of the incident vertex.
	 * @return int
	 */
	public int getIncidentVertex() {
		return incidentVertex;
	}
	
	/**
	 * Returns true if the reference and incident edges flipped.
	 * @return boolean
	 */
	public boolean flipped() {
		return flipped;
	}
	
	/**
	 * Sets the flipped flag.
	 * @param flipped true if the reference and incident features flipped
	 */
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	/**
	 * Sets the reference edge's index.
	 * @param referenceEdge the reference edge's index
	 */
	public void setReferenceEdge(int referenceEdge) {
		this.referenceEdge = referenceEdge;
	}
	
	/**
	 * Sets the incident edge's index.
	 * @param incidentEdge the incident edge's index
	 */
	public void setIncidentEdge(int incidentEdge) {
		this.incidentEdge = incidentEdge;
	}
	
	/**
	 * Sets the incident vertex index.
	 * @param incidentVertex the incident vertex index
	 */
	public void setIncidentVertex(int incidentVertex) {
		this.incidentVertex = incidentVertex;
	}
}
