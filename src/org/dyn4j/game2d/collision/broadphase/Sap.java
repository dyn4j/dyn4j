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
package org.dyn4j.game2d.collision.broadphase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;

/**
 * Implementation of the Sweep and Prune broad-phase collision detection algorithm.
 * <p>
 * Projects all {@link Collidable}s on both the x and y axes and performs overlap checks
 * on all the projections to test for possible collisions (AABB tests).
 * <p>
 * The overlap checks are performed faster when the {@link Interval}s created by the projections
 * are sorted by their minimum value.  Doing so will allow the detector to ignore any projections
 * after the first {@link Interval} that does not overlap.
 * <p>
 * If a {@link Collidable} is made up of more than one {@link Shape} and the {@link Shape}s 
 * are not connected, this detection algorithm may cause false hits.  For example,
 * if your {@link Collidable} consists of the following geometry: (the line below the {@link Shape}s
 * is the projection that will be used for the broad-phase)
 * <pre>
 * +--------+     +--------+  |
 * | body1  |     | body1  |  |
 * | shape1 |     | shape2 |  | y-axis projection
 * |        |     |        |  |
 * +--------+     +--------+  |
 * 
 * -------------------------
 *     x-axis projection
 * </pre>
 * So if following configuration is encountered it will generate a hit:
 * <pre>
 *             +--------+               |
 * +--------+  | body2  |  +--------+   | |
 * | body1  |  | shape1 |  | body1  |   | |
 * | shape1 |  |        |  | shape2 |   | | y-axis projection
 * |        |  |        |  |        |   | |
 * +--------+  |        |  +--------+   | |
 *             +--------+               |
 * 
 *             ----------
 * ----------------------------------
 *         x-axis projection
 * </pre>
 * These cases are OK since the {@link NarrowphaseDetector}s will handle these cases.
 * However, allowing this causes more work for the {@link NarrowphaseDetector}s whose
 * algorithms are more complex.  These situations should be avoided for maximum performance.
 * @author William Bittle
 */
public class Sap extends AbstractAABBDetector implements BroadphaseDetector {
	/**
	 * Represents a projection of a {@link Collidable} on an axis.
	 * @author William Bittle
	 * @version $Revision: 489 $
	 */
	protected class Projection implements Comparable<Projection> {
		/** The interval of the projection */
		protected Interval interval;
		
		/** The object id */
		protected int id;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Projection projection) {
			if (projection.interval.getMin() < this.interval.getMin()) {
				return 1;
			} else if (projection.interval.getMin() > this.interval.getMin()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.broadphase.BroadphaseDetector#detect(java.util.List)
	 */
	@Override
	public <E extends Collidable> List<BroadphasePair<E>> detect(List<E> collidables) {
		// get the size of the list
		int size = collidables.size();
		// create a upper packed storage mode array to store our
		// upper triangular matrix
		boolean[] matrix = new boolean[size * (size + 1) / 2];
		// to get an element its:
		// matrix[i + (j * (j - 1) / 2)] = aij, where j >= i
		
		// create a list to hold the collisions
		ArrayList<BroadphasePair<E>> collisions = new ArrayList<BroadphasePair<E>>( size * size );
		
		// create two lists to hold the projections on the x and y axes
		List<Projection> xProjections = new ArrayList<Projection>(size);
		List<Projection> yProjections = new ArrayList<Projection>(size);
		
		// loop through all the objects and project them on both the
		// y-axis and the x-axis and assign each object a unique id
		for (int i = 0; i < size; i++) {
			E c = collidables.get(i);
			Transform tx = c.getTransform();
			List<Convex> shapes = c.getShapes();
			int sSize = shapes.size();
			
			// prime the intervals
			Convex s = shapes.get(0);
			Interval x = s.project(AbstractAABBDetector.X_AXIS, tx);
			Interval y = s.project(AbstractAABBDetector.Y_AXIS, tx);
			
			// loop over the remaining shapes
			for (int j = 1; j < sSize; j++) {
				s = shapes.get(j);
				x.union(s.project(AbstractAABBDetector.X_AXIS, tx));
				y.union(s.project(AbstractAABBDetector.Y_AXIS, tx));
			}
			
			// add the x projection
			Projection px = new Projection();
			px.interval = x;
			px.id = i;
			xProjections.add(px);
			
			// add the y projection
			Projection py = new Projection();
			py.interval = y;
			py.id = i;
			yProjections.add(py);
		}

		// sort the lists of projections
		// this ended up being faster than both the insertion sort
		// and binary search insertion sort
		Collections.sort(xProjections);
		Collections.sort(yProjections);
		
		// so at this point we now have two sorted lists of projections
		// loop through the x list and find overlapping pairs
		// O(n^2) = (n - 1) + n(n - 1) /2
		for (int i = 0; i < xProjections.size(); i++) {
			Projection current = xProjections.get(i);
			// dont test the object against itself
			for (int j = i + 1; j < xProjections.size(); j++) {
				Projection test = xProjections.get(j);
				if (current.interval.overlaps(test.interval)) {
					// add it as a possible collision
					if (current.id > test.id) {
						matrix[test.id + (current.id * (current.id - 1) / 2)] = true;
					} else {
						matrix[current.id + (test.id * (test.id - 1) / 2)] = true;
					}
				} else {
					// since the intervals are sorted we know that the first
					// interval to not overlap signifies that no other subsequent
					// intervals can overlap so break from this loop and continue
					break;
				}
			}
		}
		
		// loop through the y list and find overlapping pairs
		// O(n^2) = (n - 1) + n(n - 1) /2
		for (int i = 0; i < yProjections.size(); i++) {
			Projection current = yProjections.get(i);
			for (int j = i + 1; j < yProjections.size(); j++) {
				Projection test = yProjections.get(j);
				if (current.interval.overlaps(test.interval)) {
					// if they overlap on this axis then check the other axis
					// add it as a possible collision
					boolean xCollide = false; 
					if (current.id > test.id) {
						xCollide = matrix[test.id + (current.id * (current.id - 1) / 2)];
					} else {
						xCollide = matrix[current.id + (test.id * (test.id - 1) / 2)];
					}
					
					if (xCollide) {
						E object1 = collidables.get(current.id);
						E object2 = collidables.get(test.id);
						collisions.add(new BroadphasePair<E>(object1, object2));
					}
				} else {
					// since the intervals are sorted we know that the first
					// interval to not overlap signifies that no other subsequent
					// intervals can overlap so break from this loop and continue
					break;
				}
			}
		}
		
		// trim the collection
		collisions.trimToSize();
		
		return collisions;
	}
}
