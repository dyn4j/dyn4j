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

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Class devoted to {@link Circle} detection queries.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public final class CircleDetector {
	/**
	 * Hidden constructor.
	 */
	private CircleDetector() {}
	
	/**
	 * Fast method for determining a collision between two {@link Circle}s.
	 * <p>
	 * Returns true if the given {@link Circle}s are intersecting and places the
	 * penetration vector and depth in the given {@link Penetration} object.
	 * <p>
	 * If the {@link Circle} centers are coincident then the penetration {@link Vector2}
	 * will be the zero {@link Vector2}, however, the penetration depth will be
	 * correct.  In this case its up to the caller to determine a reasonable penetration
	 * {@link Vector2}.
	 * @param circle1 the first {@link Circle}
	 * @param transform1 the first {@link Circle}'s {@link Transform}
	 * @param circle2 the second {@link Circle}
	 * @param transform2 the second {@link Circle}'s {@link Transform}
	 * @param penetration the {@link Penetration} object to fill
	 * @return boolean
	 */
	public static final boolean detect(Circle circle1, Transform transform1, Circle circle2, Transform transform2, Penetration penetration) {
		// get their world centers
		Vector2 ce1 = transform1.getTransformed(circle1.getCenter());
		Vector2 ce2 = transform2.getTransformed(circle2.getCenter());
		// create a vector from one center to the other
		Vector2 v = ce2.subtract(ce1);
		// check the magnitude against the sum of the radii
		double radii = circle1.getRadius() + circle2.getRadius();
		// get the magnitude squared
		double mag = v.getMagnitude();
		// check difference
		if (mag < radii) {
			// then we have a collision
			penetration.normal = v;
			penetration.depth = radii - v.normalize();
			return true;
		}
		return false;
	}
	
	/**
	 * Fast method for determining a collision between two {@link Circle}s.
	 * <p>
	 * Returns true if the given {@link Circle}s are intersecting.
	 * @param circle1 the first {@link Circle}
	 * @param transform1 the first {@link Circle}'s {@link Transform}
	 * @param circle2 the second {@link Circle}
	 * @param transform2 the second {@link Circle}'s {@link Transform}
	 * @return boolean true if the two circles intersect
	 */
	public static final boolean detect(Circle circle1, Transform transform1, Circle circle2, Transform transform2) {
		// get their world centers
		Vector2 ce1 = transform1.getTransformed(circle1.getCenter());
		Vector2 ce2 = transform2.getTransformed(circle2.getCenter());
		// create a vector from one center to the other
		Vector2 v = ce2.subtract(ce1);
		// check the magnitude against the sum of the radii
		double radii = circle1.getRadius() + circle2.getRadius();
		// get the magnitude squared
		double mag = v.getMagnitude();
		// check difference
		if (mag < radii) {
			// then we have a collision
			return true;
		}
		return false;
	}
	
	/**
	 * Fast method for determining the distance between two {@link Circle}s.
	 * <p>
	 * Returns true if the given {@link Circle}s are separated and places the
	 * separating vector and distance in the given {@link Separation} object.
	 * @param circle1 the first {@link Circle}
	 * @param transform1 the first {@link Circle}'s {@link Transform}
	 * @param circle2 the second {@link Circle}
	 * @param transform2 the second {@link Circle}'s {@link Transform}
	 * @param separation the {@link Separation} object to fill
	 * @return boolean
	 */
	public static final boolean distance(Circle circle1, Transform transform1, Circle circle2, Transform transform2, Separation separation) {
		// get their world centers
		Vector2 ce1 = transform1.getTransformed(circle1.getCenter());
		Vector2 ce2 = transform2.getTransformed(circle2.getCenter());
		// get the radii
		double r1 = circle1.getRadius();
		double r2 = circle2.getRadius();
		// create a vector from one center to the other
		Vector2 v = ce1.to(ce2);
		// check the magnitude against the sum of the radii
		double radii = r1 + r2;
		// get the magnitude squared
		double mag = v.getMagnitude();
		// check difference
		if (mag >= radii) {
			// then the circles are separated
			separation.normal = v;
			separation.distance = v.normalize() - radii;
			separation.point1 = ce1.add(v.x * r1, v.y * r1);
			separation.point2 = ce2.add(-v.x * r2, -v.y * r2);
			return true;
		}
		return false;
	}
	
	/**
	 * Performs a ray cast against the given circle.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum ray length
	 * @param circle the {@link Circle}
	 * @param transform the {@link Circle}'s {@link Transform}
	 * @param raycast the {@link Raycast} result
	 * @return boolean true if the ray intersects the circle
	 * @since 2.0.0
	 */
	public static final boolean raycast(Ray ray, double maxLength, Circle circle, Transform transform, Raycast raycast) {
		// solve the problem algebraically
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirectionVector();
		Vector2 ce = transform.getTransformed(circle.getCenter());
		double r = circle.getRadius();
		
		// make sure the start of the ray is not contained in the circle
		if (circle.contains(s, transform)) return false;
		
		// any point on a ray can be found by the parametric equation:
		// P = tD + S
		// any point on a circle can be found by:
		// (x - h)^2 + (y - k)^2 = r^2 where h and k are the x and y center coordinates
		// substituting the first equation into the second yields a quadratic equation:
		// |D|^2t^2 + 2D.dot(S - C)t + (S - C)^2 - r^2 = 0
		// using the quadratic equation we can solve for t where
		// a = |D|^2
		// b = 2D.dot(S - C)
		// c = (S - C)^2 - r^2
		Vector2 sMinusC = s.difference(ce);
		
		// mag(d)^2
		double a = d.dot(d);
		// 2d.dot(s - c)
		double b = 2 * d.dot(sMinusC);
		// (s - c)^2 - r^2
		double c = sMinusC.dot(sMinusC) - r * r;
		
		// precompute
		double inv2a = 1.0 / (2.0 * a);
		double b24ac = b * b - 4 * a * c;
		// check for negative inside the sqrt
		if (b24ac < 0.0) {
			// if the computation inside the sqrt is
			// negative then this indicates that the
			// ray is parallel to the circle
			return false;
		}
		double sqrt = Math.sqrt(b24ac);
		// compute the two values of t
		double t0 = (-b + sqrt) * inv2a;
		double t1 = (-b - sqrt) * inv2a;
		
		// find the correct t
		// t cannot be negative since this would make the point
		// in the opposite direction of the ray's direction
		double t = 0.0;
		// check for negative value
		if (t0 < 0.0) {
			// check for negative value
			if (t1 < 0.0) {
				// return the ray does not intersect the circle
				return false;
			} else {
				// t1 is the answer
				t = t1;
			}
		} else {
			// check for negative value
			if (t1 < 0.0) {
				// t0 is the answer
				t = t0;
			} else if (t0 < t1) {
				// t0 is the answer
				t = t0;
			} else {
				// t1 is the answer
				t = t1;
			}
		}
		
		// check the value of t
		if (maxLength > 0.0 && t > maxLength) {
			// if the smallest non-negative t is larger
			// than the maximum length then return false
			return false;
		}
		
		// compute the hit point
		Vector2 p = d.product(t).add(s);
		// compute the normal
		Vector2 n = ce.to(p); n.normalize();
		
		// populate the raycast result
		raycast.point = p;
		raycast.normal = n;
		raycast.distance = t;
		
		// return success
		return true;
	}
}
