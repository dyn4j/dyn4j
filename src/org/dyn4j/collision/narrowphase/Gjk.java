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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.collision.Collidable;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of the Gilbert-Johnson-Keerthi (GJK) algorithm for collision detection.
 * <p>
 * {@link Gjk} is an algorithm used to find minimum distance from one {@link Convex} {@link Shape} 
 * to another, but can also be used to determine whether or not they intersect.
 * <p>
 * {@link Gjk} uses a specific mathematical construct called the {@link MinkowskiSum}.  The 
 * {@link MinkowskiSum} of two {@link Convex} {@link Shape}s create another {@link Convex} 
 * {@link Shape}.  If the shapes are labeled A and B, the {@link MinkowskiSum} is the convex hull
 * of adding every point in A to every point in B.
 * <p>  
 * Now, if we subtract every point in A and every point in B, we still end up with a 
 * {@link Convex} {@link Shape}, but we also get another interesting property.  If the two {@link Convex} 
 * {@link Shape}s are penetrating one another (overlapping) then the {@link MinkowskiSum}, using the difference
 * operator, will contain the origin.
 * <p>
 * Computing the {@link MinkowskiSum} directly would not be very efficient and performance would be directly linked
 * to the number of vertices each shape contained (n*m subtractions).  In addition, curved shapes have an infinite 
 * number of vertices.
 * <p>
 * That said, it's not necessary to compute the {@link MinkowskiSum}. Instead, to determine whether the origin is 
 * contained in the {@link MinkowskiSum} we iteratively create a {@link Shape} inside the {@link MinkowskiSum} that 
 * encloses the origin.  This is called the simplex and for 2D it will be a triangle.  If we can enclose the origin
 * using a shape contained within the {@link MinkowskiSum}, then we can conclude that the origin is contained within
 * the {@link MinkowskiSum}, and that the two shapes are penetrating. If we cannot, then the shapes are separated.
 * <p>
 * To create a shape inside the {@link MinkowskiSum}, we use what is called a support function. The support function
 * returns a point on the edge of the {@link MinkowskiSum} farthest in a given direction.  This can be obtained by taking 
 * the farthest point in shape A minus the farthest point in shape B in the opposite direction.
 * <p>
 * If the {@link MinkowskiSum} is:
 * <pre>
 * A - B
 * </pre>
 * then the support function would be:
 * <pre>
 * (farthest point in direction D in A) - (farthest point in direction -D in B)
 * </pre>
 * With this we can obtain a point which is on the edge of the {@link MinkowskiSum} shape in any direction.  Next we 
 * iteratively create these points so that we create a shape (triangle in the 2d case) that encloses the origin.
 * <p>
 * Algorithm psuedo-code:
 * <pre>
 * // get a point farthest in the direction
 * // choose some random direction (selection of the initial direction can
 * // determine the speed at which the algorithm terminates)
 * Point p = support(A, B, direction);
 * // add it to the simplex
 * simplex.addPoint(p);
 * // negate the direction
 * direction = -direction;
 * // make sure the point we are about to add is actually past the origin
 * // if its not past the origin then that means we can never enclose the origin
 * // therefore its not in the Minkowski sum and therefore there is no penetration.
 * while (p = support(A, B, direction).dot(direction) &gt; 0) {
 * 	// if the point is past the origin then add it to the simplex
 * 	simplex.add(p);
 *	// then check to see if the simplex contains the origin
 *	// passing back a new search direction if it does not
 * 	if (check(simplex, direction)) {
 * 		return true;
 * 	}
 * }
 * return false;
 * </pre>
 * The last method to discuss is the check method.  This method can be implemented in
 * any fashion, however, if the simplex points are stored in a way that we always know what point
 * was added last, many optimizations can be done.  For these optimizations please refer
 * to the source documentation on {@link Gjk#checkSimplex(List, Vector2)}.
 * <p>
 * Once {@link Gjk} has found that the two {@link Collidable}s are penetrating it will exit 
 * and hand off the resulting simplex to a {@link MinkowskiPenetrationSolver} to find the
 * collision depth and normal.
 * <p>
 * {@link Gjk}'s default {@link MinkowskiPenetrationSolver} is {@link Epa}.
 * <p>
 * The {@link Gjk} algorithm's original intent was to find the minimum distance between two {@link Convex}
 * {@link Shape}s.  Refer to {@link Gjk#distance(Convex, Transform, Convex, Transform, Separation)}
 * for details on the implementation.
 * @author William Bittle
 * @version 3.1.11
 * @since 1.0.0
 * @see Epa
 * @see <a href="http://www.dyn4j.org/2010/04/gjk-gilbert-johnson-keerthi/" target="_blank">GJK (Gilbert-Johnson-Keerthi)</a>
 * @see <a href="http://www.dyn4j.org/2010/04/gjk-distance-closest-points/" target="_blank">GJK - Distance &amp; Closest Points</a>
 */
public class Gjk implements NarrowphaseDetector, DistanceDetector, RaycastDetector {
	/** The origin point */
	private static final Vector2 ORIGIN = new Vector2();
	
	/** The default {@link Gjk} maximum iterations */
	public static final int DEFAULT_MAX_ITERATIONS = 30;
	
	/** The default {@link Gjk} distance epsilon in meters; near 1E-8 */
	public static final double DEFAULT_DISTANCE_EPSILON = Math.sqrt(Epsilon.E);
	
	/** The penetration solver; defaults to {@link Epa} */
	protected MinkowskiPenetrationSolver minkowskiPenetrationSolver = new Epa();
	
	/** The maximum number of {@link Gjk} iterations */
	protected int maxIterations = Gjk.DEFAULT_MAX_ITERATIONS;
	
	/** The {@link Gjk} distance epsilon in meters */
	protected double distanceEpsilon = Gjk.DEFAULT_DISTANCE_EPSILON;
	
	/**
	 * Default constructor.
	 */
	public Gjk() {}
	
	/**
	 * Optional constructor.
	 * @param minkowskiPenetrationSolver the {@link MinkowskiPenetrationSolver} to use
	 * @throws NullPointerException if minkowskiPenetrationSolver is null
	 */
	public Gjk(MinkowskiPenetrationSolver minkowskiPenetrationSolver) {
		if (minkowskiPenetrationSolver == null) throw new NullPointerException(Messages.getString("collision.narrowphase.gjk.nullMinkowskiPenetrationSolver"));
		this.minkowskiPenetrationSolver = minkowskiPenetrationSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Penetration)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Penetration penetration) {
		// check for circles
		if (convex1 instanceof Circle && convex2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return CircleDetector.detect((Circle) convex1, transform1, (Circle) convex2, transform2, penetration);
		}
		
		// define the simplex
		List<Vector2> simplex = new ArrayList<Vector2>(3);
		
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(convex1, transform1, convex2, transform2);
		
		// choose some search direction
		Vector2 d = this.getInitialDirection(convex1, transform1, convex2, transform2);
		
		// perform the detection
		if (this.detect(ms, simplex, d)) {
			this.minkowskiPenetrationSolver.getPenetration(simplex, ms, penetration);
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		// check for circles
		if (convex1 instanceof Circle && convex2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return CircleDetector.detect((Circle) convex1, transform1, (Circle) convex2, transform2);
		}
		
		// define the simplex
		List<Vector2> simplex = new ArrayList<Vector2>(3);
		
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(convex1, transform1, convex2, transform2);
		
		// choose some search direction
		Vector2 d = this.getInitialDirection(convex1, transform1, convex2, transform2);
		
		// perform the detection
		return detect(ms, simplex, d);
	}
	
	/**
	 * Returns a vector for the initial direction for the GJK algorithm in world coordinates.
	 * <p>
	 * This implementation returns the vector from the center of the first convex to the center of the second.
	 * @param convex1 the first convex
	 * @param transform1 the first convex's transform
	 * @param convex2 the second convex
	 * @param transform2 the second convex's transform
	 * @return Vector2
	 */
	protected Vector2 getInitialDirection(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		// transform into world space if transform is not null
		Vector2 c1 = transform1.getTransformed(convex1.getCenter());
		Vector2 c2 = transform2.getTransformed(convex2.getCenter());
		// choose some search direction
		return c2.subtract(c1);
	}
	
	/**
	 * The main {@link Gjk} algorithm loop.
	 * <p>
	 * Returns true if a collision was detected and false otherwise.
	 * <p>
	 * The simplex and direction parameters will reflect the state of the algorithm at termination, whether
	 * a collision was found or not.  This is useful for subsequent algorithms that use the GJK simplex to
	 * find the collision information ({@link Epa} for example).
	 * @param ms the {@link MinkowskiSum}
	 * @param simplex the simplex; should be an empty list
	 * @param d the initial direction
	 * @return boolean
	 */
	protected boolean detect(MinkowskiSum ms, List<Vector2> simplex, Vector2 d) {
		// check for a zero direction vector
		if (d.isZero()) d.set(1.0, 0.0);
		// add the first point
		simplex.add(ms.getSupportPoint(d));
		// is the support point past the origin along d?
		if (simplex.get(0).dot(d) <= 0.0) {
			return false;
		}
		// negate the search direction
		d.negate();
		// start the loop
		while (true) {
			// always add another point to the simplex at the beginning of the loop
			simplex.add(ms.getSupportPoint(d));
			// make sure that the last point we added was past the origin
			if (simplex.get(simplex.size() - 1).dot(d) <= 0.0) {
				// a is not past the origin so therefore the shapes do not intersect
				// here we treat the origin on the line as no intersection
				// immediately return with null indicating no penetration
				return false;
			} else {
				// if it is past the origin, then test whether the simplex contains the origin
				if (this.checkSimplex(simplex, d)) {
					// if the simplex contains the origin then we know that there is an intersection.
					// if we broke out of the loop then we know there was an intersection
					return true;
				}
				// if the simplex does not contain the origin then we need to loop using the new
				// search direction and simplex
			}
		}
	}
	
	/**
	 * Determines whether the given simplex contains the origin.  If it does contain the origin,
	 * then this method will return true.  If it does not, this method will update both the given
	 * simplex and also the given search direction.
	 * <p>
	 * This method only handles the line segment and triangle simplex cases, however, these two cases
	 * should be the only ones needed for 2 dimensional {@link Gjk}.  The single point case is handled
	 * in {@link #detect(MinkowskiSum, List, Vector2)}.
	 * <p>
	 * This method also assumes that the last point in the simplex is the most recently added point.
	 * This matters because optimizations are available when you know this information.
	 * @param simplex the simplex
	 * @param direction the search direction
	 * @return boolean true if the simplex contains the origin
	 */
	protected boolean checkSimplex(List<Vector2> simplex, Vector2 direction) {
		// this method should never be supplied anything other than 2 or 3 points for the simplex
		// get the last point added (a)
		Vector2 a = simplex.get(simplex.size() - 1);
		// this is the same as a.to(ORIGIN);
		Vector2 ao = a.getNegative();
		// check to see what type of simplex we have
		if (simplex.size() == 3) {
			// then we have a triangle
			Vector2 b = simplex.get(1);
			Vector2 c = simplex.get(0);
			// get the edges
			Vector2 ab = a.to(b);
			Vector2 ac = a.to(c);
			// get the edge normals
			Vector2 abPerp = Vector2.tripleProduct(ac, ab, ab);
			Vector2 acPerp = Vector2.tripleProduct(ab, ac, ac);
			// see where the origin is at
			double acLocation = acPerp.dot(ao);
			if (acLocation >= 0.0) {
				// the origin lies on the right side of A->C
				// because of the condition for the gjk loop to continue the origin 
				// must lie between A and C so remove B and set the
				// new search direction to A->C perpendicular vector
				simplex.remove(1);
				// this used to be direction.set(Vector.tripleProduct(ac, ao, ac));
				// but was changed since the origin may lie on the segment created
				// by a -> c in which case would produce a zero vector normal
				// calculating ac's normal using b is more robust
				direction.set(acPerp);
			} else {
				double abLocation = abPerp.dot(ao);
				// the origin lies on the left side of A->C
				if (abLocation < 0.0) {
					// the origin lies on the right side of A->B and therefore in the
					// triangle, we have an intersection
					return true;
				} else {
					// the origin lies between A and B so remove C and set the
					// search direction to A->B perpendicular vector
					simplex.remove(0);
					// this used to be direction.set(Vector.tripleProduct(ab, ao, ab));
					// but was changed since the origin may lie on the segment created
					// by a -> b in which case would produce a zero vector normal
					// calculating ab's normal using c is more robust
					direction.set(abPerp);
				}
			}
		} else {
			// get the b point
			Vector2 b = simplex.get(0);
			Vector2 ab = a.to(b);
			// otherwise we have 2 points (line segment)
			// because of the condition for the gjk loop to continue the origin 
			// must lie in between A and B, so keep both points in the simplex and
			// set the direction to the perp of the line segment towards the origin
			direction.set(Vector2.tripleProduct(ab, ao, ab));
			// check for degenerate cases where the origin lies on the segment
			// created by a -> b which will yield a zero edge normal
			if (direction.getMagnitudeSquared() <= Epsilon.E) {
				// in this case just choose either normal (left or right)
				direction.set(ab.left());
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.DistanceDetector#distance(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Separation)
	 */
	@Override
	public boolean distance(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Separation separation) {
		// check for circles
		if (convex1 instanceof Circle && convex2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return CircleDetector.distance((Circle) convex1, transform1, (Circle) convex2, transform2, separation);
		}
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(convex1, transform1, convex2, transform2);
		// define some Minkowski points
		MinkowskiSumPoint a = null;
		MinkowskiSumPoint b = null;
		MinkowskiSumPoint c = null;
		// transform into world space if transform is not null
		Vector2 c1 = transform1.getTransformed(convex1.getCenter());
		Vector2 c2 = transform2.getTransformed(convex2.getCenter());
		// choose some search direction
		Vector2 d = c1.to(c2);
		// check for a zero direction vector
		// a zero direction vector indicates that the center's are coincident
		// which guarantees that the convex shapes are overlapping
		if (d.isZero()) return false;
		// add the first point 
		a = ms.getSupportPoints(d);
		// negate the direction
		d.negate();
		// get a second support point
		b = ms.getSupportPoints(d);
		// find the point on the simplex (segment) closest to the origin
		// and use that as the new search direction
		d = Segment.getPointOnSegmentClosestToPoint(ORIGIN, b.point, a.point);
		for (int i = 0; i < this.maxIterations; i++) {
			// the vector from the point we found to the origin is the new search direction
			d.negate();
			// check if d is zero
			if (d.getMagnitudeSquared() <= Epsilon.E) {
				// if the closest point is the origin then the shapes are not separated
				return false;
			}
			
			// get the farthest point along d
			c = ms.getSupportPoints(d);
			
			// test if the triangle made by a, b, and c contains the origin
			if (this.containsOrigin(a.point, b.point, c.point)){
				// if it does then return false;
				return false;
			}
			
			// see if the new point is far enough along d
			double projection = c.point.dot(d);
			if ((projection - a.point.dot(d)) < this.distanceEpsilon) {
				// then the new point we just made is not far enough
				// in the direction of n so we can stop now
				// normalize d
				d.normalize();
				separation.normal = d;
				// compute the real distance
				separation.distance = -c.point.dot(d);
				// get the closest points
				this.findClosestPoints(a, b, separation);
				// return true to indicate separation
				return true;
			}
			
			// get the closest point on each segment to the origin
			Vector2 p1 = Segment.getPointOnSegmentClosestToPoint(ORIGIN, a.point, c.point);
			Vector2 p2 = Segment.getPointOnSegmentClosestToPoint(ORIGIN, c.point, b.point);
			
			// get the distance to the origin
			double p1Mag = p1.getMagnitudeSquared();
			double p2Mag = p2.getMagnitudeSquared();
			
			// check if the origin lies close enough to either edge
			if (p1Mag <= Epsilon.E) {
				// if so then we have a separation (although its
				// nearly zero separation)
				d.normalize();
				separation.distance = p1.normalize();
				separation.normal = d;
				this.findClosestPoints(a, c, separation);
				return true;
			} else if (p2Mag <= Epsilon.E) {
				// if so then we have a separation (although its
				// nearly zero separation)
				d.normalize();
				separation.distance = p2.normalize();
				separation.normal = d;
				this.findClosestPoints(c, b, separation);
				return true;
			}
			
			// test which point is closer and replace the one that is farthest
			// with the new point c and set the new search direction
			if (p1Mag < p2Mag) {
				// a was closest so replace b with c
				b = c;
				d = p1;
			} else {
				// b was closest so replace a with c
				a = c;
				d = p2;
			}
		}
		// if we made it here then we know that we hit the maximum number of iterations
		// this is really a catch all termination case
		d.normalize();
		separation.normal = d;
		separation.distance = -c.point.dot(d);
		// get the closest points
		this.findClosestPoints(a, b, separation);
		// return true to indicate separation
		return true;
	}
	
	/**
	 * Finds the closest points on A and B given the termination simplex and places 
	 * them into point1 and point2 of the given {@link Separation} object.
	 * <p>
	 * The support points used to obtain a and b are not good enough since the support
	 * methods of {@link Convex} {@link Shape}s only return the farthest <em>vertex</em>, not
	 * necessarily the farthest point.
	 * @param a the first simplex point
	 * @param b the second simplex point
	 * @param separation the {@link Separation} object to populate
	 * @see <a href="http://www.dyn4j.org/2010/04/gjk-distance-closest-points/" target="_blank">GJK - Distance &amp; Closest Points</a>
	 */
	protected void findClosestPoints(MinkowskiSumPoint a, MinkowskiSumPoint b, Separation separation) {
		Vector2 p1 = new Vector2();
		Vector2 p2 = new Vector2();
		
		// find lambda1 and lambda2
		Vector2 l = a.point.to(b.point);
		
		// check if a and b are the same point
		if (l.isZero()) {
			// then the closest points are a or b support points
			p1.set(a.supportPoint1);
			p2.set(a.supportPoint2);
		} else {
			// otherwise compute lambda1 and lambda2
			double ll = l.dot(l);
			double l2 = -l.dot(a.point) / ll;
			double l1 = 1 - l2;
			
			// check if either lambda1 or lambda2 is less than zero
			if (l1 < 0) {
				// if lambda1 is less than zero then that means that
				// the support points of the Minkowski point B are
				// the closest points
				p1.set(b.supportPoint1);
				p2.set(b.supportPoint2);
			} else if (l2 < 0) {
				// if lambda2 is less than zero then that means that
				// the support points of the Minkowski point A are
				// the closest points
				p1.set(a.supportPoint1);
				p2.set(a.supportPoint2);
			} else {
				// compute the closest points using lambda1 and lambda2
				// this is the expanded version of
				// p1 = a.p1.multiply(l1).add(b.p1.multiply(l2));
				// p2 = a.p2.multiply(l1).add(b.p2.multiply(l2));
				p1.x = a.supportPoint1.x * l1 + b.supportPoint1.x * l2;
				p1.y = a.supportPoint1.y * l1 + b.supportPoint1.y * l2;
				p2.x = a.supportPoint2.x * l1 + b.supportPoint2.x * l2;
				p2.y = a.supportPoint2.y * l1 + b.supportPoint2.y * l2;
			}
		}
		// set the new points in the separation object
		separation.point1 = p1;
		separation.point2 = p2;
	}
	
	/**
	 * Returns true if the origin is within the triangle given by
	 * a, b, and c.
	 * <p>
	 * If the origin lies on the same side of all the points then we
	 * know that the origin is in the triangle.
	 * <pre> sign(location(origin, a, b)) == sign(location(origin, b, c)) == sign(location(origin, c, a))</pre>
	 * The {@link Segment#getLocation(Vector2, Vector2, Vector2)} method 
	 * can be simplified because we are using the origin as the search point:
	 * <pre> = (b.x - a.x) * (origin.y - a.y) - (origin.x - a.x) * (b.y - a.y)
	 * = (b.x - a.x) * (-a.y) - (-a.x) * (b.y - a.y)
	 * = -a.y * b.x + a.y * a.x + a.x * b.y - a.x * a.y
	 * = -a.y * b.x + a.x * b.y
	 * = a.x * b.y - a.y * b.x
	 * = a.cross(b)</pre>
	 * @param a the first point
	 * @param b the second point
	 * @param c the third point
	 * @return boolean
	 */
	protected boolean containsOrigin(Vector2 a, Vector2 b, Vector2 c) {
		double sa = a.cross(b);
		double sb = b.cross(c);
		double sc = c.cross(a);
		// this is sufficient (we do not need to test sb * sc)
		return (sa * sb  > 0 && sa * sc > 0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.RaycastDetector#raycast(org.dyn4j.geometry.Ray, double, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Raycast)
	 */
	@Override
	public boolean raycast(Ray ray, double maxLength, Convex convex, Transform transform, Raycast raycast) {
		// check for circle
		if (convex instanceof Circle) {
			// if the convex is a circle then use the more efficient method
			return CircleDetector.raycast(ray, maxLength, (Circle) convex, transform, raycast);
		}
		// check for segment
		if (convex instanceof Segment) {
			// if the convex is a segment then use the more efficient method
			return SegmentDetector.raycast(ray, maxLength, (Segment) convex, transform, raycast);
		}
		
		// otherwise proceed with GJK raycast
		double lambda = 0;
		
		// do we need to check against the max length?
		boolean lengthCheck = maxLength > 0;
		
		// create the holders for the simplex
		Vector2 a = null;
		Vector2 b = null;
		
		// get the start point of the ray
		Vector2 start = ray.getStart();
		// x is the current closest point on the ray
		Vector2 x = start;
		// r is the ray direction
		Vector2 r = ray.getDirectionVector();
		// n is the normal at the hit point
		Vector2 n = new Vector2();
		
		// is the start point contained in the convex?
		if (convex.contains(start, transform)) {
			// return false if the start of the ray is inside the convex
			return false;
		}
		
		// get an arbitrary point within the convex shape
		// we can use the center point
		Vector2 c = transform.getTransformed(convex.getCenter());
		// the center to the start point
		Vector2 d = c.to(x);
		
		// define an epsilon to compare the distance with
		double distanceSqrd = Double.MAX_VALUE;
		int iterations = 0;
		// loop until we have found the correct distance
		while (distanceSqrd > this.distanceEpsilon) {
			// get a point on the edge of the convex in the direction of d
			Vector2 p = convex.getFarthestPoint(d, transform);
			// get the vector from the current closest point to the edge point
			Vector2 w = p.to(x);
			// is the current point on the ray to the new point
			// in the same direction as d?
			double dDotW = d.dot(w);
			if (dDotW > 0.0) {
				// is the ray direction in the same direction as d?
				double dDotR = d.dot(r);
				if (dDotR >= 0.0) {
					// immediately return false since this indicates that the
					// ray is moving in the opposite direction
					return false;
				} else {
					// otherwise compute the new closest point on the
					// ray to the edge point
					lambda = lambda - dDotW / dDotR;
					// check if l is larger than the length
					if (lengthCheck && lambda > maxLength) {
						// then return false
						return false;
					}
					x = r.product(lambda).add(start);
					// set d as the best normal we have so far
					// d will be normalized when the loop terminates
					n.set(d);
				}
			}
			// now reduce the simplex to two points such that we keep the
			// two points that form a segment that is closest to x
			if (a != null) {
				if (b != null) {
					// reduce the set to two points
					// get the closest point on each segment to the origin
					Vector2 p1 = Segment.getPointOnSegmentClosestToPoint(x, a, p);
					Vector2 p2 = Segment.getPointOnSegmentClosestToPoint(x, p, b);
					
					// test which point is closer and replace the one that is farthest
					// with the new point p and set the new search direction
					if (p1.distanceSquared(x) < p2.distanceSquared(x)) {
						// a was closest so replace b with p
						b.set(p);
						// update the distance
						distanceSqrd = p1.distanceSquared(x);
					} else {
						// b was closest so replace a with p
						a.set(p);
						// update the distance
						distanceSqrd = p2.distanceSquared(x);
					}
					// get the new search direction
					Vector2 ab = a.to(b);
					Vector2 ax = a.to(x);
					d = Vector2.tripleProduct(ab, ax, ab);
				} else {
					// b is null so just set b
					b = p;
					// get the new search direction
					Vector2 ab = a.to(b);
					Vector2 ax = a.to(x);
					d = Vector2.tripleProduct(ab, ax, ab);
				}
			} else {
				// both a and b are null so just set a and use -d as the
				// new direction
				a = p;
				d.negate();
			}
			
			// check for the maximum number of iterations
			if (iterations == this.maxIterations) {
				// we have hit the maximum number of iterations and
				// still are not close enough to the ray, in this case
				// just exit returning false
				return false;
			}
			
			// increment the number of iterations
			iterations++;
		}
		
		// set the raycast result values
		raycast.point = x;
		raycast.normal = n; n.normalize();
		raycast.distance = lambda;
		
		// return true to indicate that we were successful
		return true;
	}
	
	/**
	 * Returns the maximum number of iterations the {@link Gjk} algorithm will perform when
	 * computing the distance between two separated bodies.
	 * @return int the number of {@link Gjk} distance iterations
	 * @see #setMaxIterations(int)
	 */
	public int getMaxIterations() {
		return this.maxIterations;
	}

	/**
	 * Sets the maximum number of iterations the {@link Gjk} algorithm will perform when
	 * computing the distance between two separated bodies.
	 * <p>
	 * Valid values are in the range [5, &infin;].
	 * @param maxIterations the maximum number of {@link Gjk} iterations
	 * @throws IllegalArgumentException if maxIterations is less than 5
	 */
	public void setMaxIterations(int maxIterations) {
		if (maxIterations < 5) throw new IllegalArgumentException(Messages.getString("collision.narrowphase.gjk.invalidMaximumIterations"));
		this.maxIterations = maxIterations;
	}

	/**
	 * Returns the {@link Gjk} distance epsilon.
	 * @return double the {@link Gjk} distance epsilon
	 * @see #setDistanceEpsilon(double)
	 */
	public double getDistanceEpsilon() {
		return this.distanceEpsilon;
	}

	/**
	 * The minimum distance between two iterations of the {@link Gjk} distance algorithm.
	 * <p>
	 * Valid values are in the range (0, &infin;].
	 * @param distanceEpsilon the {@link Gjk} distance epsilon
	 * @throws IllegalArgumentException if distanceEpsilon is less than or equal to zero
	 */
	public void setDistanceEpsilon(double distanceEpsilon) {
		if (distanceEpsilon <= 0) throw new IllegalArgumentException(Messages.getString("collision.narrowphase.gjk.invalidDistanceEpsilon"));
		this.distanceEpsilon = distanceEpsilon;
	}
	
	/**
	 * Returns the {@link MinkowskiPenetrationSolver} used to obtain the
	 * penetration vector and depth.
	 * @return {@link MinkowskiPenetrationSolver}
	 */
	public MinkowskiPenetrationSolver getMinkowskiPenetrationSolver() {
		return this.minkowskiPenetrationSolver;
	}
	
	/**
	 * Sets the {@link MinkowskiPenetrationSolver} used to obtain the 
	 * penetration vector and depth.
	 * @param minkowskiPenetrationSolver the {@link MinkowskiPenetrationSolver}
	 * @throws NullPointerException if minkowskiPenetrationSolver is null
	 */
	public void setMinkowskiPenetrationSolver(MinkowskiPenetrationSolver minkowskiPenetrationSolver) {
		if (minkowskiPenetrationSolver == null) throw new NullPointerException(Messages.getString("collision.narrowphase.gjk.nullMinkowskiPenetrationSolver"));
		this.minkowskiPenetrationSolver = minkowskiPenetrationSolver;
	}
}
