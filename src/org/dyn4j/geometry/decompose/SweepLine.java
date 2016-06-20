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
package org.dyn4j.geometry.decompose;

import java.util.List;
import java.util.PriorityQueue;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of the Sweep convex decomposition algorithm for simple polygons.
 * <p>
 * This algorithm first decomposes the polygon into y-monotone polygons, then decomposes the y-monotone
 * polygons into triangles, finally using the Hertel-Mehlhorn algorithm to recombine the triangles
 * into convex pieces.
 * <p>
 * This algorithm is O(n log n) complexity in the y-monotone decomposition phase and O(n) in the
 * triangulation phase yielding a total complexity of O(n log n).
 * <p>
 * After triangulation, the Hertel-Mehlhorn algorithm is used to reduce the number of convex
 * pieces.  This is performed in O(n) time.
 * <p>
 * This algorithm total complexity is O(n log n).
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
public class SweepLine implements Decomposer, Triangulator {
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.decompose.Decomposer#decompose(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public List<Convex> decompose(Vector2... points) {
		// triangulate
		DoubleEdgeList dcel = this.createTriangulation(points);
		
		// the DCEL now contains a valid triangulation
		// next we perform the Hertel-Mehlhorn algorithm to
		// remove unnecessary edges
		dcel.hertelMehlhorn();
		
		// the DCEL now contains a valid convex decompostion
		// convert the dcel into a list of convex shapes
		return dcel.getConvexDecomposition();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.decompose.Triangulator#triangulate(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public List<Triangle> triangulate(Vector2... points) {
		// triangulate
		DoubleEdgeList dcel = this.createTriangulation(points);
		// return the triangulation
		return dcel.getTriangulation();
	}
	
	/**
	 * Creates a triangulation of the given simple polygon and places it in the
	 * returned doubly-connected edge list (DCEL).
	 * @param points the vertices of the simple polygon to triangulate
	 * @return {@link DoubleEdgeList}
	 * @since 3.1.9
	 */
	final DoubleEdgeList createTriangulation(Vector2... points) {
		// check for a null list
		if (points == null) throw new NullPointerException(Messages.getString("geometry.decompose.nullArray"));
		// get the number of points
		int size = points.length;
		// check the size
		if (size < 4) throw new IllegalArgumentException(Messages.getString("geometry.decompose.invalidSize"));
		
		// get the winding order
		double winding = Geometry.getWinding(points);
		
		// reverse the array if the points are in clockwise order
		if (winding < 0.0) {
			Geometry.reverseWinding(points);
		}
		
		// create a new sweep state
		// this is the container for the algorithms acceleration structures
		SweepLineState sweepstate = new SweepLineState();
		
		// create the priority queue (sorted queue by largest y value) and
		// the cyclical lists
		PriorityQueue<SweepLineVertex> queue = sweepstate.initialize(points);
		
		// Find all edges that need to be added to the polygon
		// to create a y-monotone decomposition
		while (!queue.isEmpty()) {
			SweepLineVertex vertex = queue.poll();
			if (vertex.type == SweepLineVertexType.START) {
				this.start(vertex, sweepstate);
			} else if (vertex.type == SweepLineVertexType.END) {
				this.end(vertex, sweepstate);
			} else if (vertex.type == SweepLineVertexType.SPLIT) {
				this.split(vertex, sweepstate);
			} else if (vertex.type == SweepLineVertexType.MERGE) {
				this.merge(vertex, sweepstate);
			} else if (vertex.type == SweepLineVertexType.REGULAR) {
				this.regular(vertex, sweepstate);
			}
		}
		
		// the DCEL now contains a valid y-monotone polygon decomposition
		// next we need to triangulate all the y-monotone polygons
		sweepstate.dcel.triangulateYMonotonePolygons();
		
		// return the triangulation
		return sweepstate.dcel;
	}
	
	/**
	 * Handles a {@link SweepLineVertexType#START} event.
	 * @param vertex the vertex
	 * @param sweepstate the current state of the SweepLine algorithm
	 */
	final void start(SweepLineVertex vertex, SweepLineState sweepstate) {
		// we need to add the edge to the left to the tree
		// since the line in the next event may be intersecting it
		SweepLineEdge leftEdge = vertex.left;
		// set the reference y to the current vertex's y
		sweepstate.referenceY.value = vertex.point.y;
		sweepstate.tree.insert(leftEdge);
		// set the left edge's helper to this vertex
		leftEdge.helper = vertex;
	}
	
	/**
	 * Handles a {@link SweepLineVertexType#END} event.
	 * @param vertex the vertex
	 * @param sweepstate the current state of the SweepLine algorithm
	 */
	final void end(SweepLineVertex vertex, SweepLineState sweepstate) {
		// if the vertex type is an end vertex then we
		// know that we need to remove the right edge
		// since the sweep line no longer intersects it
		SweepLineEdge rightEdge = vertex.right;
		// before we remove the edge we need to make sure
		// that we don't forget to link up MERGE vertices
		if (rightEdge.helper.type == SweepLineVertexType.MERGE) {
			// connect v to v.right.helper
			sweepstate.dcel.addHalfEdges(vertex.index, rightEdge.helper.index);
		}
		// set the reference y to the current vertex's y
		sweepstate.referenceY.value = vertex.point.y;
		// remove v.right from T
		sweepstate.tree.remove(rightEdge);
	}
	
	/**
	 * Handles a {@link SweepLineVertexType#SPLIT} event.
	 * @param vertex the vertex
	 * @param sweepstate the current state of the SweepLine algorithm
	 */
	final void split(SweepLineVertex vertex, SweepLineState sweepstate) {
		// if we have a split vertex then we can find
		// the closest edge to the left side of the vertex
		// and attach its helper to this vertex
		SweepLineEdge ej = sweepstate.tree.search(new ClosestEdgeToVertexSearchCriteria(vertex)).closest;
		
		// connect v to ej.helper
		sweepstate.dcel.addHalfEdges(vertex.index, ej.helper.index);
		
		// set the new helper for the edge
		ej.helper = vertex;
		// set the reference y to the current vertex's y
		sweepstate.referenceY.value = vertex.point.y;
		// insert the edge to the left of this vertex
		sweepstate.tree.insert(vertex.left);
		// set the left edge's helper
		vertex.left.helper = vertex;
	}
	
	/**
	 * Handles a {@link SweepLineVertexType#MERGE} event.
	 * @param vertex the vertex
	 * @param sweepstate the current state of the SweepLine algorithm
	 */
	final void merge(SweepLineVertex vertex, SweepLineState sweepstate) {
		// get the previous edge
		SweepLineEdge eiPrev = vertex.right;
		// check if its helper is a merge vertex
		if (eiPrev.helper.type == SweepLineVertexType.MERGE) {
			// connect v to v.right.helper
			sweepstate.dcel.addHalfEdges(vertex.index, eiPrev.helper.index);
		}
		// set the reference y to the current vertex's y
		sweepstate.referenceY.value = vertex.point.y;
		// remove the previous edge since the sweep 
		// line no longer intersects with it
		sweepstate.tree.remove(eiPrev);
		// find the edge closest to the given vertex
		SweepLineEdge ej = sweepstate.tree.search(new ClosestEdgeToVertexSearchCriteria(vertex)).closest;
		// is the edge's helper a merge vertex
		if (ej.helper.type == SweepLineVertexType.MERGE) {
			// connect v to ej.helper
			sweepstate.dcel.addHalfEdges(vertex.index, ej.helper.index);
		}
		// set the closest edge's helper to this vertex
		ej.helper = vertex;
	}
	
	/**
	 * Handles a {@link SweepLineVertexType#MERGE} event.
	 * @param vertex the vertex
	 * @param sweepstate the current state of the SweepLine algorithm
	 */
	final void regular(SweepLineVertex vertex, SweepLineState sweepstate) {
		// check if the interior is to the right of this vertex
		if (vertex.isInteriorRight()) {
			// if so, check the previous edge's helper to see
			// if its a merge vertex
			if (vertex.right.helper.type == SweepLineVertexType.MERGE) {
				// connect v to v.right.helper
				sweepstate.dcel.addHalfEdges(vertex.index, vertex.right.helper.index);
			}
			// set the reference y to the current vertex's y
			sweepstate.referenceY.value = vertex.point.y;
			// remove the previous edge since the sweep 
			// line no longer intersects with it
			sweepstate.tree.remove(vertex.right);
			// add the next edge
			sweepstate.tree.insert(vertex.left);
			// set the helper
			vertex.left.helper = vertex;
		} else {
			// otherwise find the closest edge
			SweepLineEdge ej = sweepstate.tree.search(new ClosestEdgeToVertexSearchCriteria(vertex)).closest;
			// check the helper type
			if (ej.helper.type == SweepLineVertexType.MERGE) {
				// connect v to ej.helper
				sweepstate.dcel.addHalfEdges(vertex.index, ej.helper.index);
			}
			// set the new helper
			ej.helper = vertex;
		}
	}
}
