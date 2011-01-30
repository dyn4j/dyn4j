/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.geometry.decompose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Implementation of the <a href="http://mnbayazit.com/406/bayazit">Bayazit</a> convex decomposition algorithm.
 * <p>
 * This algorithm is a O(nr) complexity algorithm where n is the number of input vertices and r is the number of
 * output convex polygons.  This algorithm can achieve optimal decompositions, however this is not guaranteed.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 */
public class Bayazit implements Decomposer {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.decompose.Decomposer#decompose(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public List<Convex> decompose(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("Cannot decompose a null array of points.");
		// get the number of points
		int size = points.length;
		// check the size
		if (size < 4) throw new IllegalArgumentException("The polygon must have 4 or more vertices.");
		
		// get the winding order
		double winding = Geometry.getWinding(points);
		
		// reverse the array if the points are in clockwise order
		if (winding < 0.0) {
			Geometry.reverseWinding(points);
		}
		
		// create a list for the points to go in
		List<Vector2> polygon = new ArrayList<Vector2>();
		
		// copy the points to the list
		Collections.addAll(polygon, points);
		
		// create a list for the polygons to live 
		List<Convex> polygons = new ArrayList<Convex>();
		
		// decompose the polygon
		this.decomposePolygon(polygon, polygons);
		
		// return the result
		return polygons;
	}
	
	/**
	 * Internal recursive method to decompose the given polygon into convex sub-polygons.
	 * @param polygon the polygon to decompose
	 * @param polygons the list to store the convex polygons resulting from the decomposition
	 */
	protected void decomposePolygon(List<Vector2> polygon, List<Convex> polygons) {
		// get the size of the given polygon
		int size = polygon.size();
		
		// initialize
		Vector2 upperIntersection = new Vector2();
		Vector2 lowerIntersection = new Vector2();
		double upperDistance = Double.MAX_VALUE;
		double lowerDistance = Double.MAX_VALUE;
		double closestDistance = Double.MAX_VALUE;
		int upperIndex = 0;
		int lowerIndex = 0;
		int closestIndex = 0;
		
		List<Vector2> lower = new ArrayList<Vector2>();
		List<Vector2> upper = new ArrayList<Vector2>();
		
		// loop over all the vertices
		for (int i = 0; i < size; i++) {
			// get the current vertex
			Vector2 p = polygon.get(i);
			
			// get the adjacent vertices
			Vector2 p0 = polygon.get(i - 1 < 0 ? size - 1 : i - 1);
			Vector2 p1 = polygon.get(i + 1 == size ? 0 : i + 1);
			
			// check if the vertex is a reflex vertex
			if (isReflex(p0, p, p1)) {
				
				// loop over the vertices to determine if both extended
				// adjacent edges intersect one edge (in which case a 
				// steiner point will be added)
				for (int j = 0; j < size; j++) {
					Vector2 q = polygon.get(j);
					
					// get the adjacent vertices
					Vector2 q0 = polygon.get(j - 1 < 0 ? size - 1 : j - 1);
					Vector2 q1 = polygon.get(j + 1 == size ? 0 : j + 1);
					
					// create a storage location for the intersection point
					Vector2 s = new Vector2();
					
					// extend the previous edge
					// does the line p0->p go between the vertices q and q0
					if (left(p0, p, q) && rightOn(p0, p, q0)) {
						// get the intersection point
						if (this.getIntersection(p0, p, q, q0, s)) {
							// make sure the intersection point is to the right of
							// the edge p1->p (this makes sure its inside the polygon)
							if (right(p1, p, s)) {
								// get the distance from p to the intersection point s
								double dist = p.distanceSquared(s);
								// only save the smallest
								if (dist < lowerDistance) {
									lowerDistance = dist;
									lowerIntersection.set(s);
									lowerIndex = j;
								}
							}
						}
					}
					
					// extend the next edge
					// does the line p1->p go between q and q1
					if (left(p1, p, q1) && rightOn(p1, p, q)) {
						// get the intersection point
						if (this.getIntersection(p1, p, q, q1, s)) {
							// make sure the intersection point is to the left of
							// the edge p0->p (this makes sure its inside the polygon)
							if (left(p0, p, s)) {
								// get the distance from p to the intersection point s
								double dist = p.distanceSquared(s);
								// only save the smallest
								if (dist < upperDistance) {
									upperDistance = dist;
									upperIntersection.set(s);
									upperIndex = j;
								}
							}
						}
					}
				}
				
				// if the lower index and upper index are equal then this means
				// that the range of p only included an edge (both extended previous 
				// and next edges of p only intersected the same edge, therefore no
				// point exists within that range to connect to)
				if (lowerIndex == (upperIndex + 1) % size) {
					// create a steiner point in the middle
					Vector2 s = upperIntersection.sum(lowerIntersection).multiply(0.5);
					
					// partition the polygon
					if (i < upperIndex) {
						lower.addAll(polygon.subList(i, upperIndex + 1));
						lower.add(s);
						upper.add(s);
						if (lowerIndex != 0) upper.addAll(polygon.subList(lowerIndex, size));
						upper.addAll(polygon.subList(0, i + 1));
					} else {
						if (i != 0) lower.addAll(polygon.subList(i, size));
						lower.addAll(polygon.subList(0, upperIndex + 1));
						lower.add(s);
						upper.add(s);
						upper.addAll(polygon.subList(lowerIndex, i + 1));
					}
				} else {
					// otherwise we need to find the closest "visible" point to p
					
					if (lowerIndex > upperIndex) {
						upperIndex += size;
					}
					
					// find the closest visible point
					for (int j = lowerIndex; j <= upperIndex; j++) {
						Vector2 q = polygon.get(j % size);
						
						if (q == p || q == p0 || q == p1) continue;
						
						// make sure q is in the range made by extending the
						// previous and next edges of p
						if (leftOn(p0, p, q) && rightOn(p1, p, q)) {
							// get the distance
							double dist = p.distanceSquared(q);
							// only keep the closest
							if (dist < closestDistance) {
								closestDistance = dist;
								closestIndex = j % size;
							}
						}
					}
					
					// once we find the closest partition the polygon
					if (i < closestIndex) {
						lower.addAll(polygon.subList(i, closestIndex + 1));
						if (closestIndex != 0) upper.addAll(polygon.subList(closestIndex, size));
						upper.addAll(polygon.subList(0, i + 1));
					} else {
						if (i != 0) lower.addAll(polygon.subList(i, size));
						lower.addAll(polygon.subList(0, closestIndex + 1));
						upper.addAll(polygon.subList(closestIndex, i + 1));
					}
				}
				
				// decompose the smaller first
				if (lower.size() < upper.size()) {
					decomposePolygon(lower, polygons);
					decomposePolygon(upper, polygons);
				} else {
					decomposePolygon(upper, polygons);
					decomposePolygon(lower, polygons);
				}
				
				// if the given polygon contains a reflex vertex, then return
				return;
			}
		}
		
		// if we get here, we know the given polygon has 0 reflex vertices
		// and is therefore convex, add it to the list of convex polygons
		Vector2[] vertices = new Vector2[polygon.size()];
		polygon.toArray(vertices);
		polygons.add(new Polygon(vertices));
	}
	
	/**
	 * Returns true if the given vertex, b, is a reflex vertex.
	 * <p>
	 * A reflex vertex is a vertex who's interior angle is greater
	 * than 180 degrees.
	 * @param p0 the vertex to test
	 * @param p the previous vertex
	 * @param p1 the next vertex
	 * @return boolean
	 */
	protected boolean isReflex(Vector2 p0, Vector2 p, Vector2 p1) {
		// if the point p is to the right of the line p0-p1 then
		// the point is a reflex vertex
		return right(p1, p0, p);
	}
	
	/**
	 * Returns true if the given point p is to the left
	 * of the line created by a-b.
	 * @param a the first point of the line
	 * @param b the second point of the line
	 * @param p the point to test
	 * @return boolean
	 */
	protected boolean left(Vector2 a, Vector2 b, Vector2 p) {
	    return Segment.getLocation(p, a, b) > 0;
	}
	
	/**
	 * Returns true if the given point p is to the left
	 * or on the line created by a-b.
	 * @param a the first point of the line
	 * @param b the second point of the line
	 * @param p the point to test
	 * @return boolean
	 */
	protected boolean leftOn(Vector2 a, Vector2 b, Vector2 p) {
	    return Segment.getLocation(p, a, b) >= 0;
	}
	
	/**
	 * Returns true if the given point p is to the right
	 * of the line created by a-b.
	 * @param a the first point of the line
	 * @param b the second point of the line
	 * @param p the point to test
	 * @return boolean
	 */
	protected boolean right(Vector2 a, Vector2 b, Vector2 p) {
	    return Segment.getLocation(p, a, b) < 0;
	}
	
	/**
	 * Returns true if the given point p is to the right
	 * or on the line created by a-b.
	 * @param a the first point of the line
	 * @param b the second point of the line
	 * @param p the point to test
	 * @return boolean
	 */
	protected boolean rightOn(Vector2 a, Vector2 b, Vector2 p) {
	    return Segment.getLocation(p, a, b) <= 0;
	}
	
	/**
	 * Returns true if the given lines intersect and returns the intersection point in
	 * the p parameter.
	 * @param a1 the first point of the first line
	 * @param a2 the second point of the first line
	 * @param b1 the first point of the second line
	 * @param b2 the second point of the second line
	 * @param p the destination object for the intersection point
	 * @return boolean
	 */
	protected boolean getIntersection(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2, Vector2 p) {
		// any point on a line can be found by the parametric equation:
		// P = (1 - t)A + tB 
		// or 
		// P.x = (1 - t)A.x + tB.x
		// P.y = (1 - t)A.y + tB.y
		
		// so we get:
		
		// P.x = (1 - t1)A1.x + t1A2.x
		// P.y = (1 - t1)A1.y + t1A2.y
		
		// P.x = (1 - t2)B1.x + t2B2.x
		// P.y = (1 - t2)B1.y + t2B2.y
		
		
		// since P is the same we can set the equations equal
		
		// (1 - t1)A1.x + t1A2.x = (1 - t2)B1.x + t2B2.x
		// (1 - t1)A1.y + t1A2.y = (1 - t2)B1.y + t2B2.y
		
		// A1.x - t1A1.x + t1A2.x = B1.x - t2B1.x + t2B2.x
		// A1.y - t1A1.y + t1A2.y = B1.y - t2B1.y + t2B2.y
		
		// t2(B1.x - B2.x) - t1(A1.x - A2.x) = B1.x - A1.x
		// t2(B1.y - B2.y) - t1(A1.y - A2.y) = B1.y - A1.y
		
		// solve the system of equations
		
		// t1 = -(B1.x - A1.x - t2(B1.x - B2.x)) / (A1.x - A2.x)
		// t2(B2.y - B2.y) + (B1.x - A1.x - t2(B1.x - B2.x)) / (A1.x - A2.x) * (A1.y - A2.y) = B1.y - A1.y
		// t2(B2.y - B2.y)(A1.x - A2.x) + (B1.x - A1.x - t2(B1.x - B2.x))(A1.y - A2.y) = (B1.y - A1.y)(A1.x - A2.x)
		// t2S2.yS1.x + B1.xS1.y - A1.xS1.y - t2S2.xS1.y = B1.yS1.x - A1.yS1.x
		// t2(S2.yS1.x - S2.xS1.y) = B1.yS1.x - A1.yS1.x - B1.xS1.y + A1.xS1.y
		// t2(S1.cross(S2)) = B1.yS1.x - B1.xS1.y + A1.xS1.y - A1.yS1.x
		// t2(S1.cross(S2)) = A1.cross(S1) - B1.cross(S1) 
		// t2 = (A1.cross(S1) - B1.cross(S1)) / S1.cross(S2)
		
		// if S1.cross(S2) is near zero then there is no solution
		
		// compute S1 and S2
		Vector2 s1 = a1.difference(a2);
		Vector2 s2 = b1.difference(b2);
		
		// compute the cross product (the determinant if we used matrix solving techniques)
		double det = s1.cross(s2);
		
		// make sure the matrix isn't singular (the lines could be parallel)
		if (Math.abs(det) < Epsilon.E) {
			// return false since there is no way that the segments could be intersecting
			return false;
		} else {
			// pre-divide the determinant
			det = 1.0 / det;
			
			// compute t2
			double t2 = det * (a1.cross(s1) - b1.cross(s1));
			
			// compute the intersection point
			// P = B1(1.0 - t2) + B2(t2)
			p.x = b1.x * (1.0 - t2) + b2.x * t2;
			p.y = b1.y * (1.0 - t2) + b2.y * t2;
			
			// return that they intersect
			return true;
		}
	}
}
