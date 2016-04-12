package org.dyn4j.collision.narrowphase;

import java.io.ObjectInputStream.GetField;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class LinkPostProcessor {
	public static void process(Penetration p, Convex convex, Transform tx1, Link link, Transform tx2) {
		// negate the normal
//		p.getNormal().negate();
		LinkPostProcessor.process(p, link, tx2, convex, tx1);
		// make sure the new normal is pointing from convex -> link 
//		Vector2 c1 = tx1.getTransformed(convex.getCenter());
//		Vector2 c2 = tx2.getTransformed(link.getCenter());
//		Vector2 cToc = c1.to(c2);
//		if (cToc.dot(p.getNormal()) < 0) {
//			// negate the normal if its not
//			p.getNormal().negate();
//		}
	}
	
	public static void process(Penetration p, Link link, Transform tx1, Convex convex, Transform tx2) {
		Vector2 n = p.getNormal();
		Vector2 c = convex.getCenter();
		
		Vector2 p1 = link.getPoint1();
		Vector2 p2 = link.getPoint2();
		Vector2 p1p2 = p1.to(p2);
		Vector2 p2p1 = p2.to(p1);
		
		Vector2 p0 = link.getPoint0();
		Vector2 p3 = link.getPoint3();
		
		double ndp1p2 = n.dot(p1p2);
		
		if (p0 != null && ndp1p2 < 0) {
			Vector2 p0p1 = p0.to(p1);
			double t1 = n.dot(p2p1);
			double t2 = n.dot(p0p1);
			// is the normal in ok zone?
			if (!(t1 > 0 && t2 > 0)) {
				// nope, so we need to adjust it
				if (p0p1.cross(p1p2) < 0) {
					// convex
//					System.out.println("case2");
					Vector2 newN = getNormalInDirectionOf(p0p1.left(), p0, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				} else if (p0p1.cross(p1p2) >= 0) {
					// concave
//					System.out.println("case4");
					Vector2 newN = getNormalInDirectionOf(p1p2.left(), p1, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				}
			}
		}
		
		if (p3 != null && ndp1p2 > 0) {
			Vector2 p3p2 = p3.to(p2);
			Vector2 p2p3 = p2.to(p3);
			double t1 = n.dot(p1p2);
			double t2 = n.dot(p3p2);
			// is the normal in ok zone?
			if (!(t1 > 0 && t2 > 0)) {
				// nope, so we need to adjust it
				if (p1p2.cross(p2p3) < 0) {
					// convex
//					System.out.println("case1");
					Vector2 newN = getNormalInDirectionOf(p3p2.left(), p3, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				} else if (p1p2.cross(p2p3) >= 0) {
					// concave
//					System.out.println("case3");
					Vector2 newN = getNormalInDirectionOf(p1p2.left(), p1, c);
					adjust(p, newN);
//					System.out.println(p.getNormal());
//					System.out.println(p.getDepth());
					return;
				}
			}
		}
	}
	
	private static final Vector2 getNormalInDirectionOf(Vector2 n, Vector2 s, Vector2 p) {
		Vector2 sp = s.to(p);
		if (n.dot(sp) > 0) {
			return n;
		} else {
			return n.getNegative();
		}
	}
	
	private static final void adjust(Penetration p, Vector2 newNormal) {
		newNormal.normalize();
		double newD = Math.abs(p.getNormal().dot(newNormal) * p.getDepth());
		if (newD == 0.0) {
			newD = p.getDepth();
		}
		p.setNormal(newNormal);
		p.setDepth(newD);
	}
}
