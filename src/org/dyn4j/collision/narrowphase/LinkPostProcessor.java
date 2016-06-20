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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * A {@link NarrowphasePostProcessor} specifically for the {@link Link} class to solve the 
 * internal edge problem when using a chain of segments.
 * @author Willima Bittle
 * @version 3.2.2
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
			process((Link)convex1, transform1, convex2, transform2, penetration);
		} else if (convex2 instanceof Link) {
			// for this case we convert the parameters to match the order specified
			// by the other method and negate the incoming and outgoing normal to match
			penetration.normal.negate();
			process((Link)convex2, transform2, convex1, transform1, penetration);
			penetration.normal.negate();
		}
	}
	
	/**
	 * Attempts to use the connectivity information to determine if the normal found in the narrow-phase is valid.
	 * If not, the normal is modified to within the valid range of normals based on the connectivity and the collision
	 * depth is adjusted.
	 * @param link the link
	 * @param transform1 the link's transform
	 * @param convex the other convex
	 * @param transform2 the other convex transform
	 * @param penetration the narrow-phase collision information
	 */
	public void process(Link link, Transform transform1, Convex convex, Transform transform2, Penetration penetration) {
		Vector2 n = penetration.getNormal();
		Vector2 c = transform2.getTransformed(convex.getCenter());
		
		Vector2 p1 = transform1.getTransformed(link.getPoint1());
		Vector2 p2 = transform1.getTransformed(link.getPoint2());
		Vector2 p0 = link.getPoint0() != null ? transform1.getTransformed(link.getPoint0()) : null;
		Vector2 p3 = link.getPoint3() != null ? transform1.getTransformed(link.getPoint3()) : null;
		
		boolean convex1 = false;
		boolean convex2 = false;
		
		// segments
		Vector2 edge0 = null;
		Vector2 edge1 = null;
		Vector2 edge2 = null;
		// segment normals
		Vector2 normal0 = null;
		Vector2 normal1 = null;
		Vector2 normal2 = null;
		
		// the valid normal range
		Vector2 normal = null;
		Vector2 upper = null;
		Vector2 lower = null;
		
		// where is the center of the other shape
		// relative to the previous, next, and this
		// segment?
		// - = right side
		// + = left side
		double offset0 = 0;
		double offset1 = 0;
		double offset2 = 0;
		
		edge1 = p1.to(p2);
		edge1.normalize();
		normal1 = edge1.getLeftHandOrthogonalVector();
		offset1 = normal1.dot(p1.to(c));
		
		if (p0 != null) {
			edge0 = p0.to(p1);
			edge0.normalize();
			// get the normal for p0->p1
			normal0 = edge0.getLeftHandOrthogonalVector();
			// does p0->p1->p2 make a convex feature?
			convex1 = edge0.cross(edge1) >= 0;
			// where is the center of the other shape
			offset0 = normal0.dot(p0.to(c));
		}
		
		if (p3 != null) {
			edge2 = p2.to(p3);
			edge2.normalize();
			// get the normal for p2->p3
			normal2 = edge2.getLeftHandOrthogonalVector();
			// does p1->p2->p3 make a convex feature?
			convex2 = edge1.cross(edge2) >= 0;
			// where is the center of the other shape
			offset2 = normal2.dot(p2.to(c));
		}
		
		// do we have both previous and next vertices?
		if (p0 != null && p3 != null) {
			// are both features convex?
			if (convex1 && convex2) {
				//   O----O
				//  /      \
				// O        O
				// is the center of the other shape on the convex side or the concave side?
				boolean front = offset0 >= 0 || offset1 >= 0 || offset2 >= 0;
				if (front) {
					// its on the outside (convex), so the normal has be between the
					// normal of p0->p1 and p2->p3
					normal = normal1;
					lower = normal0;
					upper = normal2;
				} else {
					// its on the inside (concave), so just use the normal of the segment
					normal = normal1.getNegative();
					lower = normal;
					upper = normal;
				}
			} else if (convex1) {
				//          O
				//         /
				//   O----O
				//  /
				// O 
				// only p0->p1->p2 is convex
				// is the center of the other shape on the convex side or the concave side?
				boolean front = offset0 >= 0 || (offset1 >= 0 && offset2 >= 0);
				if (front) {
					// its on the convex side, the normal must be between the normals of
					// p0->p1 and p1->p2
					normal = normal1;
					lower = normal0;
					upper = normal1;
				} else {
					// its on the concave side, the normal must be between the normals of
					// p1->p2 and p2->p3 on the convex side of p1->p2->p3
					normal = normal1.getNegative();
					lower = normal2.getNegative();
					upper = normal1.getNegative();
				}
			} else if (convex2) {
				// O
				//  \
				//   O----O
				//         \
				//          O
				// only p1->p2->p3 is convex
				// is the center of the other shape on the convex side or the concave side?
				boolean front = offset2 >= 0 || (offset0 >= 0 && offset1 >= 0);
				if (front) {
					// its on the convex side, the normal must be in between the normals of
					// p1->p2 and p2->p3
					normal = normal1;
					lower = normal1;
					upper = normal2;
				} else {
					// its on the concave side, the normal must be between the normals of
					// p0->p1 and p1->p2 on the convex side of p0->p1->p2
					normal = normal1.getNegative();
					lower = normal1.getNegative();
					upper = normal0.getNegative();
				}
			} else {
				// O        O
				//  \      /
				//   O----O
				// otherwise both are convex
				// is the center of the other shape on the convex or concave side?
				boolean front = offset0 >= 0 && offset1 >= 0 && offset2 >= 0;
				if (front) {
					// its on the concave side, so just use the normal of the segment
					normal = normal1;
					lower = normal1;
					upper = normal1;
				} else {
					// its on the convex side, so the normal must be between the normals of
					// p0->p1 and p2->p3
					normal = normal1.getNegative();
					lower = normal2.getNegative();
					upper = normal0.getNegative();
				}
			}
		} else if (p0 != null) {
			// we only have a previous point
			// does p0->p1->p2 make a convex feature?
			if (convex1) {
				//   O----O
				//  /
				// O
				// is the center of the other shape on the convex or concave side?
				boolean front = offset0 >= 0 || offset1 >= 0;
				if (front) {
					// its on the convex side, so the normal must be between the normals of
					// p0->p1 and p1->p2 rotated -pi
					normal = normal1;
					lower = normal0;
					upper = normal1.getNegative();
				} else {
					// its on the concave side, so the normal must be between the normals of
					// p1->p2 and p1->p2 rotated -pi
					normal = normal1.getNegative();
					lower = normal1;
					upper = normal1.getNegative();
				}
			} else {
				// O
				//  \
				//   O----O
				// is the center of the other shape on the convex or concave side?
				boolean front = offset0 >= 0 && offset1 >= 0;
				if (front) {
					// its on the concave side, so the normal must be between the normals of
					// p1->p2 and p1->p2 rotated -pi
					normal = normal1;
					lower = normal1;
					upper = normal1.getNegative();
				} else {
					// its on the convex side, so the normal must be between the normals of
					// p1->p2 and p0->p1 rotated -pi
					normal = normal1.getNegative();
					lower = normal1;
					upper = normal0.getNegative();
				}
			}
		} else if (p3 != null) {
			// we only have a next point
			// does p1->p2->p3 make a convex feature?
			if (convex2) {
				// O----O
				//       \
				//        O
				// is the center of the other shape on the convex or concave side?
				boolean front = offset1 >= 0 || offset2 >= 0;
				if (front) {
					// its on the convex side, so the normal must be between the normals of
					// p1->p2 rotated -pi and p2->p3
					normal = normal1;
					lower = normal1.getNegative();
					upper = normal2;
				} else {
					// its on the concave side, so the normal must be between the normals of
					// p1->p2 rotated -pi and p1->p2
					normal = normal1.getNegative();
					lower = normal1.getNegative();
					upper = normal1;
				}
			} else {
				//        O
				//       /
				// O----O
				// is the center of the other shape on the convex or concave side?
				boolean front = offset1 >= 0 && offset2 >= 0;
				if (front) {
					// its on the concave side, so the normal must be between the normals of
					// p1->p2 rotated -pi and p1->p2
					normal = normal1;
					lower = normal1.getNegative();
					upper = normal1;
				} else {
					// its on the convex side, so the normal must be between the normals of
					// p2->p3 rotated -pi and p1->p2
					normal = normal1.getNegative();
					lower = normal2.getNegative();
					upper = normal1;
				}
			}
		} else {
			// otherwise we don't have any adjacency information
			boolean front = offset1 >= 0;
			if (front) {
				normal = normal1;
				lower = normal1.getNegative();
				upper = normal1.getNegative();
			} else {
				normal = normal1.getNegative();
				lower = normal1;
				upper = normal1;
			}
		}
		
		// determine which normal, the upper or the lower, we should rotate the
		// collision normal towards
		Vector2 perp = normal.getRightHandOrthogonalVector();
		if (n.dot(perp) >= 0) {
			// the normal can't be outside the upper
			if (n.difference(upper).dot(normal) < 0){
				// use the upper normal
				penetration.normal = upper;
				// adjust the depth
				penetration.depth = upper.dot(n) * penetration.depth;
			}
		} else {
			// the normal can't be outside the lower
			if (n.difference(lower).dot(normal) < 0) {
				// use the lower normal
				penetration.normal = lower;
				// adjust the depth
				penetration.depth = lower.dot(n) * penetration.depth;
			}
		}
	}
}
