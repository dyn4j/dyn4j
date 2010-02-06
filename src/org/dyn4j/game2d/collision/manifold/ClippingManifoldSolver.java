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
package org.dyn4j.game2d.collision.manifold;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Feature;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents an {@link ManifoldSolver} that uses a clipping method to determine
 * the contact manifold.
 * @author William Bittle
 */
public class ClippingManifoldSolver implements ManifoldSolver {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.manifold.ManifoldSolver#getManifold(org.dyn4j.game2d.collision.narrowphase.Penetration, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.collision.manifold.Manifold)
	 */
	@Override
	public boolean getManifold(Penetration p, Convex c1, Transform t1, Convex c2, Transform t2, Manifold m) {
		// check the points array
		if (m.points == null) {
			m.points = new ArrayList<ManifoldPoint>(2);
		} else {
			m.points.clear();
		}
		
		// get the penetration normal
		Vector n = p.getNormal();
		
        // get the centers in world coordinates
		Vector center1 = t1.getTransformed(c1.getCenter());
		Vector center2 = t2.getTransformed(c2.getCenter());
        
		// make sure the axis is pointing from object2 to object1
		Vector cToc = center1.to(center2);
		if (cToc.dot(n) > 0) {
			n.negate();
		}
        
		// get the farthest feature on convex1
		Feature f1 = c1.getFarthestFeature(n.getNegative(), t1);
		// check for vertex feature
		if (f1.isVertex()) {
			// if its a vertex feature we can immediately exit with the vertex as
			// the one collision point
			ManifoldPoint mp = new ManifoldPoint(f1.max, p.getDepth());
			m.points.add(mp);
			m.normal = n;
			return true;
		}
		// get the farthest feature on convex2
		Feature f2 = c2.getFarthestFeature(n, t2);
		// check for vertex feature
		if (f2.isVertex()) {
			// if its a vertex feature we can immediately exit with the vertex as
			// the one collision point
			ManifoldPoint mp = new ManifoldPoint(f2.max, p.getDepth());
			m.points.add(mp);
			m.normal = n;
			return true;
		}
		// otherwise we have edge-edge and we need to perform a clipping method
		// to obtain the correct collision points
		
		// choose the reference/incident edge
		Feature ref = null;
		Feature inc = null;
		// create edge1 and edge2
		Vector e1 = f1.edge[0].to(f1.edge[1]);
		Vector e2 = f2.edge[0].to(f2.edge[1]);
		boolean flip = false;
		// which edge is more perpendicular to the normal?
		if (Math.abs(e1.dot(n)) < Math.abs(e2.dot(n))) {
			ref = f1;
			inc = f2;
		} else {
			ref = f2;
			inc = f1;
			// set flip to true since the opposing edge's normal
			// is more perpendicular to the collision normal
			flip = true;
		}
		// get the vector that represents the reference edge
		Vector rEdge = ref.edge[0].to(ref.edge[1]);
		rEdge.normalize();
		// find the offsets of the reference edge end points along the reference edge
		double offset1 = -rEdge.dot(ref.edge[0]);
		double offset2 = rEdge.dot(ref.edge[1]);
		// clip the incident edge by the reference edge's left edge
		List<Vector> clip1 = this.clip(inc.edge[0], inc.edge[1], rEdge.getNegative(), offset1);
		// check the number of points
		if (clip1.size() < 2) {
			return false;
		}
		// clip the clip1 edge by the reference edge's right edge
		List<Vector> clip2 = this.clip(clip1.get(0), clip1.get(1), rEdge, offset2);
		// check the number of points
		if (clip2.size() < 2) {
			return false;
		}
		
		// we need to change the normal to the reference edge's normal
		// since they may not have been the same
		Vector frontNormal = rEdge.cross(1.0);
		double frontOffset = frontNormal.dot(ref.max);
		// negate the normal if shape 2's edge was more perpendicular
		m.normal = flip ? frontNormal.getNegative() : frontNormal;
		
		// test if the clip points are behind the reference edge
		for (Vector point : clip2) {
			double depth = frontNormal.dot(point) - frontOffset;
			if (depth >= 0.0) {
				ManifoldPoint mp = new ManifoldPoint(point, depth);
				m.points.add(mp);
			}
		}
		// return the clipped points
		return true;
	}
	
	/**
	 * Clips the segment given by s1 and s2 by n.
	 * @param v1 the first vertex of the segment to be clipped
	 * @param v2 the second vertex of the segment to be clipped
	 * @param n the clipping plane/line
	 * @param offset the offset of the end point of the segment to be clipped
	 * @return List&lt;{@link Vector}&gt; the clipped segment
	 */
	protected List<Vector> clip(Vector v1, Vector v2, Vector n, double offset) {
		List<Vector> points = new ArrayList<Vector>(2);
		Vector e = v1.to(v2);
		
		// calculate the distance between the end points of the edge and the clip line
		double d1 = n.dot(v1) - offset;
		double d2 = n.dot(v2) - offset;
		
		// add the points if they are behind the line
		if (d1 <= 0.0) points.add(v1);
		if (d2 <= 0.0) points.add(v2);
		
		// check if they are on opposing sides of the line
		if (d1 * d2 < 0.0) {
			// clip to obtain another point
			double u = d1 / (d1 - d2);
			e.multiply(u);
			e.add(v1);
			points.add(e);
		}
		return points;
	}
}