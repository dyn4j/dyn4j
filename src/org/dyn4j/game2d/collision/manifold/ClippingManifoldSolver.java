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
		
		m.flip = false;
		
		// get the penetration normal
		Vector n = p.getNormal();
		
        // get the centers in world coordinates
		Vector center1 = t1.getTransformed(c1.getCenter());
		Vector center2 = t2.getTransformed(c2.getCenter());
        
		Convex refConvex, incConvex;
		Transform refTransform, incTransform;
		
		// get the direction of object1 to object2
		Vector cToc = center1.to(center2);
		// which direction is the normal in?
		if (cToc.dot(n) > 0) {
			refConvex = c1;
			refTransform = t1;
			incConvex = c2;
			incTransform = t2;
		} else {
			// then the normal must be pointing from object2 to object1
			refConvex = c2;
			refTransform = t2;
			incConvex = c1;
			incTransform = t1;
			m.flip = true;
		}
        
		// get the reference edge
		Feature refFeature = refConvex.getFarthestFeature(n, refTransform);
		// check for vertex
		if (refFeature.isVertex()) {
			// if the maximum
			Feature.Vertex vertex = (Feature.Vertex) refFeature;
			ManifoldPoint mp = new ManifoldPoint(vertex.getPoint(), p.getDepth(), 0);
			m.points.add(mp);
			m.normal = n.negate();
			return true;
		}
		
		// get the incident edge
		Feature incFeature = incConvex.getFarthestFeature(n.getNegative(), incTransform);
		// check for vertex
		if (incFeature.isVertex()) {
			Feature.Vertex vertex = (Feature.Vertex) incFeature;
			ManifoldPoint mp = new ManifoldPoint(vertex.getPoint(), p.getDepth(), 0);
			m.points.add(mp);
			m.normal = n.negate();
			return true;
		}
		
		// else both features are edge features
		Feature.Edge refEdge = (Feature.Edge) refFeature;
		Feature.Edge incEdge = (Feature.Edge) incFeature;
		
		// get the reference and incident edge vertices
		Vector[] refVertices = refEdge.getVertices();
		Vector[] incVertices = incEdge.getVertices();
		
		// create the reference edge vector
		Vector refev = refVertices[0].to(refVertices[1]);
		// normalize it
		refev.normalize();
		
		// compute the offestes of the reference edge points along the reference edge
		double offset1 = -refev.dot(refVertices[0]);
		double offset2 = refev.dot(refVertices[1]);
		
		// clip the incident edge by the reference edge's left edge
		List<Vector> clip1 = this.clip(incVertices[0], incVertices[1], refev.getNegative(), offset1);
		// check the number of points
		if (clip1.size() < 2) {
			return false;
		}
		
		// clip the clip1 edge by the reference edge's right edge
		List<Vector> clip2 = this.clip(clip1.get(0), clip1.get(1), refev, offset2);
		// check the number of points
		if (clip2.size() < 2) {
			return false;
		}
		
		// we need to change the normal to the reference edge's normal
		// since they may not have been the same
		Vector frontNormal = refev.cross(1.0);
		// also get the maximum point's depth
		double frontOffset = frontNormal.dot(refEdge.getMaximum());
		
		// negate the normal if n was pointing from object2 to object1
		m.normal = m.flip ? frontNormal.getNegative() : frontNormal;
		
		// set the feature indices
		m.referenceIndex = refEdge.getIndex();
		m.incidentIndex = incEdge.getIndex();
		
		// test if the clip points are behind the reference edge
		for (int i = 0; i < clip2.size(); i++) {
			Vector point = clip2.get(i);
			double depth = frontNormal.dot(point) - frontOffset;
			if (depth >= 0.0) {
				ManifoldPoint mp = new ManifoldPoint(point, depth, 0);
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