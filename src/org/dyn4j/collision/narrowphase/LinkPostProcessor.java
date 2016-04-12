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
		Vector2 p1p2 = p1.to(p2);
		
		double ndp1p2 = n.dot(p1p2);
		
		if (link.getPoint0() != null && ndp1p2 < 0) {
			Vector2 p0 = tx1.getTransformed(link.getPoint0());
			Vector2 p0p1 = p0.to(p1);
			double t1 = n.dot(p1p2);
			double t2 = n.dot(p0p1);
			// is the normal in ok zone?
			if (!(t1 < 0 && t2 > 0)) {
				// nope, so we need to adjust it
				if (p0p1.cross(p1p2) < 0) {
					// convex
					System.out.println("case2");
					Vector2 newN = getCorrectedNormal(p0p1, p0, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				} else if (p0p1.cross(p1p2) >= 0) {
					// concave
					System.out.println("case4");
					Vector2 newN = getCorrectedNormal(p1p2, p1, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				}
			}
		}
		
		if (link.getPoint3() != null && ndp1p2 > 0) {
			Vector2 p3 = tx1.getTransformed(link.getPoint3());
			Vector2 p2p3 = p2.to(p3);
			double t1 = n.dot(p1p2);
			double t2 = n.dot(p2p3);
			// is the normal in ok zone?
			if (!(t1 > 0 && t2 < 0)) {
				// nope, so we need to adjust it
				if (p1p2.cross(p2p3) < 0) {
					// convex
					System.out.println("case1");
					Vector2 newN = getCorrectedNormal(p2p3, p2, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				} else if (p1p2.cross(p2p3) >= 0) {
					// concave
					System.out.println("case3");
					Vector2 newN = getCorrectedNormal(p1p2, p1, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				}
			}
		}
	}
	
	private static final Vector2 getCorrectedNormal(Vector2 edge, Vector2 edgePoint1, Vector2 center) {
		Vector2 sp = edgePoint1.to(center);
		// determine what side of the edge the other shape's center
		// is on so we know what normal of the edge to use
		if (edge.cross(sp) >= 0) {
			return edge.right();
		} else {
			return edge.left();
		}
	}
	
	private static final void adjust(Penetration p, Vector2 newNormal) {
		newNormal.normalize();
		// project the old normal onto the new normal to obtain
		// an estimated depth and take the absolute value
		// so that if the normals are opposite we still get a positive depth
		double newD = Math.abs(p.getNormal().dot(newNormal) * p.getDepth());
		if (newD == 0.0) {
			// the depth will be zero if the old normal and the new normal
			// are perpendicular, so just use the original depth
			newD = p.getDepth();
		}
		p.setNormal(newNormal);
		p.setDepth(newD);
	}
}
