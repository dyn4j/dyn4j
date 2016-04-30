package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class LinkPostProcessor {
	public static void process(Penetration p, Convex convex, Transform tx1, Link link, Transform tx2) {
		// for this case we convert the parameters to match the order specified
		// by the other method and negate the incoming and outgoing normal to match
		
		// negate the normal
		p.getNormal().negate();
		LinkPostProcessor.process(p, link, tx2, convex, tx1);
		p.getNormal().negate();
		
	}
	
	public static void process(Penetration p, Link link, Transform tx1, Convex convex, Transform tx2) {
		Vector2 n = p.getNormal();
		Vector2 c = tx2.getTransformed(convex.getCenter());
		
		Vector2 p1 = tx1.getTransformed(link.getPoint1());
		Vector2 p2 = tx1.getTransformed(link.getPoint2());
		Vector2 p0 = link.getPoint0() != null ? tx1.getTransformed(link.getPoint0()) : null;
		Vector2 p3 = link.getPoint3() != null ? tx1.getTransformed(link.getPoint3()) : null;
		
		boolean convex1 = false;
		boolean convex2 = false;
		
		Vector2 normal = null;
		Vector2 edge0 = null;
		Vector2 edge1 = null;
		Vector2 edge2 = null;
		Vector2 normal0 = null;
		Vector2 normal1 = null;
		Vector2 normal2 = null;
		Vector2 upper = null;
		Vector2 lower = null;
		
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
		
		//System.out.println("n: " + n + " nn: " + normal + " l: " + lower + " u: " + upper);
		
		// determine which normal, the upper or the lower, we should rotate the
		// collision normal towards
		Vector2 perp = normal.getRightHandOrthogonalVector();
		if (n.dot(perp) >= 0) {
			// use the upper normal
			p.normal = upper;
			// adjust the depth
			p.depth = upper.dot(n) * p.depth;
		} else {
			// use the lower normal
			p.normal = lower;
			// adjust the depth
			p.depth = lower.dot(n) * p.depth;
		}
		
		// make sure the adjusted normal is pointing in
		// the same direction as the collision normal
		if (n.dot(p.normal) < 0) {
			p.normal.negate();
		}
	}
}
