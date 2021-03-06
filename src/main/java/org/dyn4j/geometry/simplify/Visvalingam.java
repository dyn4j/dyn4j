package org.dyn4j.geometry.simplify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Vector2;

// TODO this is the simple Visvalingam algorithm that doesn't handle self-intersection
public final class Visvalingam extends AbstractSimplifier implements Simplifier {
	private final double clusterTolerance;
	private final double minimumTriangleArea;
	private final boolean avoidSelfIntersection;
	
	private final RTree tree;
	
	public Visvalingam(double clusterTolerance, double minimumTriangleArea) {
		this(clusterTolerance, minimumTriangleArea, true);
	}
	
	public Visvalingam(double clusterTolerance, double minimumTriangleArea, boolean avoidSelfIntersection) {
		this.clusterTolerance = clusterTolerance;
		this.minimumTriangleArea = minimumTriangleArea;
		this.avoidSelfIntersection = avoidSelfIntersection;
		
		this.tree = new RTree();
	}
	
	public List<Vector2> simplify(List<Vector2> vertices) {
		if (vertices == null) {
			return vertices;
		}
		
		if (vertices.size() < 4) {
			return vertices;
		}
		
		// 0. first reduce any clustered vertices in the polygon
		vertices = this.simplifyClusteredVertices(vertices, this.clusterTolerance);
		
		// 2. split into two polylines to simplify
		List<Vector2> aReduced = this.visvalingam(vertices);
		
		this.tree.clear();
		
		return aReduced;
	}
	
	/**
	 * Recursively sub-divide the given polyline performing the douglas Peucker algorithm.
	 * <p>
	 * O(mn) in worst case, O(n log m) in best case, where n is the number of vertices in the
	 * original polyline and m is the number of vertices in the reduced polyline.
	 * @param polyline
	 * @return
	 */
	private List<Vector2> visvalingam(List<Vector2> polyline) {
		int size = polyline.size();
		
		// 1. compute the triangle area of each triplet of vertices
		// 2. put all of them into a priority queue sorted by least area
		// 3. iterate through all triangles
		//		a. Pop triangle from queue
		//		b. if triangle area > epsilon then skip
		//		c. if triangle area <= epsilon
		//			i. remove the middle point
		//			ii. recompute the areas of the two adjacent triangles
		//			
		// maintain a link from the vertices to a result list
		// when a vertex is removed, remove it from the result
		// maintain a doubly link list to update adjacent triangles?
		// maintain a priority queue to order the triangles by least area

		Queue<Vertex> queue = new PriorityQueue<Vertex>();
		
		Vector2 v0 = polyline.get(size - 1);
		Vector2 v1 = polyline.get(0);
		Vector2 v2 = polyline.get(1);
		
		Vertex vertex = new Vertex();
		vertex.point = v1;
		vertex.index = 0;
		vertex.area = getTriangleArea(v0, v1, v2);
		vertex.next = null;
		vertex.prev = null;
		vertex.prevSegment = null;
		vertex.nextSegment = null;
		
		if (this.avoidSelfIntersection) {
			// create the segments and add to RTree
			RTreeLeaf nextSegment = new RTreeLeaf(v1, v2, 0, 1);
			vertex.nextSegment = nextSegment;
			tree.add(nextSegment);
		}
		
		// reference the segments on the vertices (so we can remove/add when we remove add vertices)
		
		Vertex first = vertex;
		Vertex prev = vertex;
		for (int i = 1; i < size; i++) {
			int i0 = i - 1;
			int i2 = i + 1 == size ? 0 : i + 1;
			
			v0 = polyline.get(i0);
			v1 = polyline.get(i);
			v2 = polyline.get(i2);
			
			vertex = new Vertex();
			vertex.point = v1;
			vertex.index = i;
			vertex.area = getTriangleArea(v0, v1, v2);
			vertex.next = null;
			vertex.prev = prev;

			if (this.avoidSelfIntersection) {
				vertex.prevSegment = prev.nextSegment;
				vertex.nextSegment = new RTreeLeaf(v1, v2, i, i2);
				tree.add(vertex.nextSegment);
			}
			
			prev.next = vertex;
			prev = vertex;
			queue.add(vertex);			
		}
		
		first.prev = prev;
		prev.next = first;
		
		if (this.avoidSelfIntersection) {
			first.prevSegment = prev.nextSegment;
		}
		
		List<Vector2> result = new ArrayList<Vector2>();
		do {
			Vertex v = queue.poll();
			
			if (v.area < this.minimumTriangleArea) {
				if (this.avoidSelfIntersection && intersects(v)) {
					// keep the vertex
					continue;
				}
				
				// skip this triangle, and update the others
				Vertex tprev = v.prev;
				Vertex tnext = v.next;
				tprev.next = tnext;
				tnext.prev = tprev;
				
				v0 = tprev.prev.point;
				v1 = tprev.point;
				v2 = tnext.point;
				v.prev.area = getTriangleArea(v0, v1, v2);
				
				v0 = tprev.point;
				v1 = tnext.point;
				v2 = tnext.next.point;
				v.next.area = getTriangleArea(v0, v1, v2);
				
				if (this.avoidSelfIntersection) {
					v.prev.nextSegment = new RTreeLeaf(v1, v2, tprev.index, tnext.index);
					v.next.prevSegment = v.prev.nextSegment;
					tree.remove(v.prevSegment);
					tree.remove(v.nextSegment);
					tree.add(v.prev.nextSegment);
				}
				
				queue.remove(tprev);
				queue.remove(tnext);
				
				queue.add(tprev);
				queue.add(tnext);
			} else {
				result.add(v.point);
				Vertex n = v.next;
				while (n != v) {
					result.add(n.point);
					n = n.next;
				}
				break;
			}
		} while (!queue.isEmpty());
		
		return result;
	}
	
	private double getTriangleArea(Vector2 v0, Vector2 v1, Vector2 v2) {
		double area = v0.x * (v1.y - v2.y) + v1.x * (v2.y - v0.y) + v2.x * (v0.y - v1.y);
		area *= 0.5;
		return Math.abs(area);
	}
	

	public boolean intersects(Vertex vertex) {
		Vector2 v1 = vertex.prev.point;
		Vector2 v2 = vertex.next.point;
		
		AABB aabb = AABB.createFromPoints(v1, v2);
		Iterator<RTreeLeaf> bp = tree.getAABBDetectIterator(aabb);
		while (bp.hasNext()) {
			RTreeLeaf leaf = bp.next();
			
			if (vertex.index == leaf.index1 || vertex.index == leaf.index2) {
				continue;
			}
			
			if (vertex.prev.index == leaf.index1 || vertex.prev.index == leaf.index2) {
				continue;
			}
			
			if (vertex.next.index == leaf.index1 || vertex.next.index == leaf.index2) {
				continue;
			}
			
			// we need to verify the segments truly overlap
			if (intersects(v1, v2, leaf.point1, leaf.point2)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean intersects(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2) {
		Vector2 A = a1.to(a2);
		Vector2 B = b1.to(b2);

		// compute the bottom
		double BxA = B.cross(A);
		// compute the top
		double ambxA = a1.difference(b1).cross(A);
		
		// if the bottom is zero, then the segments are either parallel or coincident
		if (Math.abs(BxA) <= Epsilon.E) {
			// if the top is zero, then the segments are coincident
			if (Math.abs(ambxA) <= Epsilon.E) {
				// project the segment points onto the segment vector (which
				// is the same for A and B since they are coincident)
				A.normalize();
				double ad1 = a1.dot(A);
				double ad2 = a2.dot(A);
				double bd1 = b1.dot(A);
				double bd2 = b2.dot(A);
				
				// then compare their location on the number line for intersection
				Interval ia = new Interval(ad1, ad2);
				Interval ib = new Interval(bd1 < bd2 ? bd1 : bd2, bd1 > bd2 ? bd1 : bd2);
				
				if (ia.overlaps(ib)) {
					return true;
				}
			}
			
			// otherwise they are parallel
			return false;
		}
		
		// if just the top is zero, then there's no intersection
		if (Math.abs(ambxA) <= Epsilon.E) {
			return false;
		}
		
		// compute tb
		double tb = ambxA / BxA;
		if (tb <= 0.0 || tb >= 1.0) {
			// no intersection
			return false;
		}
		
		// compute the intersection point
		Vector2 ip = B.product(tb).add(b1);
		
		// since both are segments we need to verify that
		// ta is also valid.
		// compute ta
		double ta = ip.difference(a1).dot(A) / A.dot(A);
		if (ta <= 0.0 || ta >= 1.0) {
			// no intersection
			return false;
		}
		
		return true;
		
//		// solve the problem algebraically
//		Vector2 p0 = a1;
//		Vector2 d0 = a1.to(a2);
//		
//		Vector2 p1 = b1;
//		Vector2 p2 = b2;
//		Vector2 d1 = b1.to(b2);
//		
//		// is the segment vertical or horizontal?
//		boolean isVertical = Math.abs(d1.x) <= Epsilon.E;
//		boolean isHorizontal = Math.abs(d1.y) <= Epsilon.E;
//		
//		// if it's both, then it's degenerate
//		if (isVertical && isHorizontal) {
//			// it's a degenerate line segment
//			return false;
//		}
//		
//		// any point on a ray can be found by the parametric equation:
//		// P = tD0 + P0
//		// any point on a segment can be found by:
//		// P = sD1 + P1
//		// substituting the first equation into the second yields:
//		// tD0 + P0 = sD1 + P1
//		// solve for s and t:
//		// tD0.x + P0.x = sD1.x + P1.x
//		// tD0.y + P0.y = sD1.y + P1.y
//		// solve the first equation for s
//		// s = (tD0.x + P0.x - P1.x) / D1.x
//		// substitute into the second equation
//		// tD0.y + P0.y = ((tD0.x + P0.x - P1.x) / D1.x) * D1.y + P1.y
//		// solve for t
//		// tD0.yD1.x + P0.yD1.x = tD0.xD1.y + P0.xD1.y - P1.xD1.y + P1.yD1.x
//		// t(D0.yD1.x - D0.xD1.y) = P0.xD1.y - P0.yD1.x + D1.xP1.y - D1.yP1.x
//		// t(D0.yD1.x - D0.xD1.y) = P0.cross(D1) + D1.cross(P1)
//		// since the cross product is anti-cummulative
//		// t(D0.yD1.x - D0.xD1.y) = -D1.cross(P0) + D1.cross(P1)
//		// t(D0.yD1.x - D0.xD1.y) = D1.cross(P1) - D1.cross(P0)
//		// t(D0.yD1.x - D0.xD1.y) = D1.cross(P1 - P0)
//		// tD1.cross(D0) = D1.cross(P1 - P0)
//		// t = D1.cross(P1 - P0) / D1.cross(D0)
//		Vector2 p0ToP1 = p1.difference(p0);
//		double num = d1.cross(p0ToP1);
//		double den = d1.cross(d0);
//		
//		// check for zero denominator
//		if (Math.abs(den) <= Epsilon.E) {
//			// they are parallel but could be overlapping
//			
//			// since they are parallel d0 is the direction for both the
//			// segment and the ray; ie d0 = d1
//			
//			// get the common direction's normal
//			Vector2 n = d0.getRightHandOrthogonalVector();
//			// project a point from each onto the normal
//			double nDotP0 = n.dot(p0);
//			double nDotP1 = n.dot(p1);
//			// project the segment and ray onto the common direction's normal
//			if (Math.abs(nDotP0 - nDotP1) < Epsilon.E) {
//				// if their projections are close enough then they are
//				// on the same line
//				
//				// project the ray start point onto the ray direction
//				double d0DotP0 = d0.dot(p0);
//				
//				// project the segment points onto the ray direction
//				// and subtract the ray start point to receive their
//				// location on the ray direction relative to the ray
//				// start
//				double d0DotP1 = d0.dot(p1) - d0DotP0;
//				double d0DotP2 = d0.dot(p2) - d0DotP0;
//				
//				// if one or both are behind the ray, then
//				// we consider this a non-intersection
//				if (d0DotP1 < 0.0 || d0DotP2 < 0.0) {
//					// if either point is behind the ray
//					return false;
//				}
//				
//				return true;
//			} else {
//				// parallel but not overlapping
//				return false;
//			}
//		}
//		
//		// compute t
//		double t = num / den;
//		
//		// t should be in the range t >= 0.0
//		if (t < 0.0) {
//			return false;
//		}
//		
//		double s = 0;
//		if (isVertical) {
//			// use the y values to compute s
//			s = (t * d0.y + p0.y - p1.y) / d1.y;
//		} else {
//			// use the x values to compute s
//			s = (t * d0.x + p0.x - p1.x) / d1.x;
//		}
//		
//		// s should be in the range 0.0 <= s <= 1.0
//		if (s < 0.0 || s > 1.0) {
//			return false;
//		}
//		
//		// return success
//		return true;
		
	}
	
	private class Vertex implements Comparable<Vertex> {
		/** The index of the vertex in the original simple polygon */
		public int index;
		
		/** The next vertex */
		public Vertex next;
		
		/** The prev vertex */
		public Vertex prev;
		
		/** The vertex point */
		public Vector2 point;
		
		/** The triangle area */
		public double area;
		
		// only used if avoiding self intersection
		
		public RTreeLeaf prevSegment;
		public RTreeLeaf nextSegment;
		
		@Override
		public int compareTo(Vertex o) {
			// order based on triangle area
			double diff = this.area - o.area;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			}
			return 0;
		}
		
		@Override
		public String toString() {
			return this.point.toString();
		}
	}
}
