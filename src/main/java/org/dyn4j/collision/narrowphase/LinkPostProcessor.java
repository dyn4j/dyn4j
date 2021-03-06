/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * A {@link NarrowphasePostProcessor} specifically for the {@link Link} class to solve the 
 * internal edge problem when using a chain of segments.
 * <p>
 * NOTE: the {@link Link} class assumes one-way intersection as the right-handed normal of the
 * edge. 
 * @author William Bittle
 * @version 4.2.0
 * @since 3.2.2
 * @see <a href="https://bullet.googlecode.com/files/GDC10_Coumans_Erwin_Contact.pdf">Slides 46-54</a>
 */
public final class LinkPostProcessor implements NarrowphasePostProcessor {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphasePostProcessor#process(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Penetration)
	 */
	@Override
	public void process(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Penetration penetration) {
		if (convex1 instanceof Link) {
			process((Link)convex1, penetration);
		} else if (convex2 instanceof Link) {
			// for this case we convert the parameters to match the order specified
			// by the other method and negate the incoming and outgoing normal to match
			penetration.normal.negate();
			process((Link)convex2, penetration);
			penetration.normal.negate();
		}
	}
	
	/**
	 * Attempts to use the connectivity information to determine if the normal found in the narrow-phase is valid.
	 * If not, the normal is modified to within the valid range of normals based on the connectivity and the collision
	 * depth is adjusted.
	 * @param link the link
	 * @param penetration the narrow-phase collision information
	 */
	public void process(Link link, Penetration penetration) {
		Link prev = link.getPrevious();
		Link next = link.getNext();
		
		if (prev == null && next == null) {
			// if there's no connectivity info, then take
			// what the narrowphase gave us
			return;
		}
		
		Vector2 normal = penetration.getNormal().copy();
		Vector2 edge = link.getEdgeVector();
		Vector2 edgeNormal = edge.getLeftHandOrthogonalVector();
		
		// what "side" is the normal pointing towards?
		double side = normal.dot(edge);
		
		// check if the normal is pointing behind the edge normal
		double back = normal.dot(edgeNormal);
		
		if (side <= 0) {
			// test against the previous edge normal
			if (prev == null) {
				// if previous is null, then do normal 
				// two-sided segment behavior
				return;
			}
			
			Vector2 prevEdge = prev.getEdgeVector();
			prevEdge.normalize();
			
			// does the previous edge and this edge form a convex feature?
			boolean isConvex = prevEdge.cross(edge) > 0;
			if (isConvex) {
				// check if the normal is outside the allowable range
				double region = normal.cross(prevEdge.getLeftHandOrthogonalVector());
				if (region > 0.0) {
					// else skip
					penetration.clear();
				}
				
				// it's allowed as is
			} else if (back < 0.0) {
				// else skip
				penetration.clear();
			} else {
				// the previous edge and this edge form a concave feature
				// for this case, it's always the edge normal
				Vector2 norm = edgeNormal;
				penetration.normal.x = norm.x;
				penetration.normal.y = norm.y;
			}
		} else {
			// test against the next edge normal
			if (next == null) {
				// if next is null, then do normal 
				// two-sided segment behavior
				return;
			}
			
			Vector2 nextEdge = next.getEdgeVector();
			nextEdge.normalize();
			
			// does this edge and the next edge form a convex feature?
			boolean isConvex = edge.cross(nextEdge) > 0;
			if (isConvex) {
				// check if the normal is outside the allowable range
				double region = nextEdge.getLeftHandOrthogonalVector().cross(normal);
				if (region > 0.0) {
					// else skip
					penetration.clear();
				}
				
				// it's allowed as is
			} else if (back < 0.0) {
				// else skip
				penetration.clear();
			} else {
				// this edge and the next edge form a concave feature
				// for this case, it's always the edge normal
				Vector2 norm = edgeNormal;
				penetration.normal.x = norm.x;
				penetration.normal.y = norm.y;
			}
		}
		
		return;
	}
}
