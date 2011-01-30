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

import java.util.List;
import java.util.PriorityQueue;

import org.dyn4j.game2d.BinarySearchTree;
import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Performs the Sweep Line algorithm to decompose the given polygon into y-monotone pieces which are
 * then used to triangulate the original polygon.
 * <p>
 * This algorithm is O(n log n) complexity in the y-monotone decomposition phase and O(n) in the
 * triangulation phase yielding a total complexity of O(n log n).
 * <p>
 * After triangulation, the Hertel-Mehlhorn algorithm is used to reduce the number of convex
 * pieces.  This is performed in O(n) time.
 * <p>
 * This algorithm total complexity is O(n log n).
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 */
public class SweepLine implements Decomposer {
	/**
	 * Represents a vertex on a polygon that stores information
	 * about the left and right edges and left and right vertices.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	protected static class Vertex implements Comparable<Vertex> {
		/**
		 * Enumeration of the {@link Vertex} types.
		 * @author William Bittle
		 * @version 2.2.0
		 * @since 2.2.0
		 */
		protected enum Type {
			/** Vertex above both its neighbors and the internal angle is less than &pi; */
			START,
			/** Vertex above both its neighbors and the internal angle is greater than &pi; */
			SPLIT,
			/** Vertex below both its neighbors and the internal angle is less than &pi; */
			END,
			/** Vertex below both its neighbors and the internal angle is greater than &pi; */
			MERGE,
			/** Vertex between both of its neighbors */
			REGULAR,
		}
		
		/** The vertex point */
		protected Vector2 point;
		
		/** The vertex type */
		protected Type type;
		
		/** The next vertex in Counter-Clockwise order */
		protected Vertex next;
		
		/** The previous vertex in Counter-Clockwise order */
		protected Vertex prev;
		
		/** The next edge in Counter-Clockwise order */
		protected Edge left;
		
		/** The previous edge in Counter-Clockwise order */
		protected Edge right;
		
		/** The reference to the vertex in the DCEL */
		protected DoublyConnectedEdgeList.Vertex dcelReference;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Vertex other) {
			// sort by the y first then by x if the y's are equal
			Vector2 p = this.point;
			Vector2 q = other.point;
			double diff = q.y - p.y;
			if (Math.abs(diff) < Epsilon.E) {
				// if the difference is near equal then compare the x values
				return (int) Math.signum(p.x - q.x);
			} else {
				return (int) Math.signum(diff);
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("VERTEX[")
			.append(point).append("|")
			.append(type).append("]");
			return sb.toString();
		}
		
		/**
		 * Returns true if this {@link Vertex} is left of the given {@link Edge}.
		 * @param edge the {@link Edge}
		 * @return boolean true if this {@link Vertex} is to the left of the given {@link Edge}
		 */
		public boolean isLeft(Edge edge) {
			// attempt the simple comparison first
			if (this.point.x < edge.getMinX()) {
				return true;
			} else if (this.point.x > edge.getMaxX()) {
				return false;
			}
			// its in between the min and max x so we need to 
			// do a side of line test
			double location = Segment.getLocation(this.point, edge.v0.point, edge.v1.point);
			if (location < 0.0) {
				return true;
			} else {
				return false;
			}
		}
		
		/**
		 * Returns true if the interior is to the right of this vertex.
		 * <p>
		 * The left edge of this vertex is used to determine where the
		 * interior of the polygon is.
		 * @return boolean
		 */
		public boolean isInteriorRight() {
			return this.left.isInteriorRight();
		}
	}
	
	/**
	 * Represents an edge of a polygon storing the next and previous edges
	 * and the vertices that make up this edge.
	 * <p>
	 * The edge also stores a helper vertex which is used during y-monotone
	 * decomposition.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	protected static class Edge implements Comparable<Edge> {
		/** The next edge in Counter-Clockwise order */
		protected Edge next;
		
		/** The previous edge in Counter-Clockwise order */
		protected Edge prev;
		
		/** The first vertex of the edge in Counter-Clockwise order */
		protected Vertex v0;
		
		/** The second vertex of the edge in Counter-Clockwise order */
		protected Vertex v1;
		
		/** The helper vertex of this edge */
		protected Vertex helper;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("EDGE[")
			.append(v0).append("|")
			.append(v1).append("]");
			return sb.toString();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Edge o) {
			// check for reference equality first
			if (this == o) return 0;
			// first sort by the minimum x value
			double value = this.getMinX() - o.getMinX();
			if (Math.abs(value) < Epsilon.E) {
				// if they are near zero sort by the minimum y
				value = o.getMinY() - this.getMinY();
			}
			return (int) Math.signum(value);
		}
		
		/**
		 * Returns the minimum x value of this edge.
		 * @return double
		 */
		public double getMinX() {
			return Math.min(v0.point.x, v1.point.x);
		}
		
		/**
		 * Returns the maximum x value of this edge.
		 * @return double
		 */
		public double getMaxX() {
			return Math.max(v0.point.x, v1.point.x);
		}
		
		/**
		 * Returns the minimum y value of this edge.
		 * @return double
		 */
		public double getMinY() {
			return Math.min(v0.point.y, v1.point.y);
		}
		
		/**
		 * Returns the maximum y value of this edge.
		 * @return double
		 */
		public double getMaxY() {
			return Math.max(v0.point.y, v1.point.y);
		}
		
		/**
		 * Returns true if the interior of the polygon is
		 * to the right of this edge.
		 * <p>
		 * Given that the polygon's vertex winding is Counter-
		 * Clockwise, if the vertices that make this edge
		 * decrease along the y axis then the interior of the
		 * polygon is to the right, otherwise its to the
		 * left.
		 * @return boolean
		 */
		public boolean isInteriorRight() {
			double diff = v0.point.y - v1.point.y;
			// check if the points have nearly the
			// same x value
			if (Math.abs(diff) < Epsilon.E) {
				// if they do, is the vector of the
				// two points to the right or to the left
				if (v0.point.x < v1.point.x) {
					return true;
				} else {
					return false;
				}
			// otherwise just compare the y values
			} else if (diff > 0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Class to extend the {@link BinarySearchTree} to add a method for
	 * finding the {@link Edge} closest to a given {@link Vertex}.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	protected static class EdgeBinaryTree extends BinarySearchTree<Edge> {
		/**
		 * Performs a search to find the right most {@link Edge}
		 * who is left of the given {@link Vertex}.
		 * <p>
		 * If the tree is empty null is returned.
		 * @param vertex the {@link Vertex}
		 * @return {@link Edge} the closest edge
		 */
		public Edge findClosest(Vertex vertex) {
			// check for a null root node
			if (this.root == null) return null;
			// set the current node to the root
			Node<Edge> node = this.root;
			// initialize the best edge to the root
			Node<Edge> best = node;
			// loop until the current node is null
			while (node != null) {
				// get the left edge
				Edge edge = node.getComparable();
				if (vertex.isLeft(edge)) {
					// if e is left of the current edge then go left in the tree
					node = node.getLeft();
				} else {
					// otherwise e is right of the current edge so go right
					// and save the current edge as the best
					best = node;
					node = node.getRight();
				}
			}
			// return the best node's comparable (edge)
			return best.getComparable();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.decompose.Decomposer#decompose(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public List<Convex> decompose(Vector2... points) {
		// check for a null list
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
		
		// create a DCEL to efficiently store the resulting y-monotone polygons
		DoublyConnectedEdgeList dcel = new DoublyConnectedEdgeList(points);
		
		// create the priority queue (sorted queue by largest y value) and
		// the cyclical lists
		PriorityQueue<Vertex> queue = this.initialize(points, dcel);
		
		// Find all edges that need to be added to the polygon
		// to create a y-monotone decomposition
		EdgeBinaryTree tree = new EdgeBinaryTree();
		while (!queue.isEmpty()) {
			Vertex v = queue.poll();
			if (v.type == Vertex.Type.START) {
				this.start(v, tree);
			} else if (v.type == Vertex.Type.END) {
				this.end(v, tree, dcel);
			} else if (v.type == Vertex.Type.SPLIT) {
				this.split(v, tree, dcel);
			} else if (v.type == Vertex.Type.MERGE) {
				this.merge(v, tree, dcel);
			} else if (v.type == Vertex.Type.REGULAR) {
				this.regular(v, tree, dcel);
			}
		}
		
		// the DCEL now contains a valid y-monotone polygon decomposition
		// next we need to triangulate all the y-monotone polygons
		List<MonotonePolygon<DoublyConnectedEdgeList.Vertex>> polygons = dcel.getYMonotonePolygons();
		
		// triangulate each monotone polygon
		int ympSize = polygons.size();
		for (int i = 0; i < ympSize; i++) {
			dcel.triangulateMonotoneY(polygons.get(i));
		}
		
		// the DCEL now contains a valid triangulation
		// next we perform the Hertel-Mehlhorn algorithm to
		// remove unnecessary edges
		dcel.hertelMehlhorn();
		
		// the DCEL now contains a valid convex decompostion
		// convert the dcel into a list of convex shapes
		return dcel.getConvexDecomposition();
	}
	
	/**
	 * Returns a priority queue of the points in the given array.
	 * <p>
	 * This method also builds the cyclic doubly-linked list of vertices
	 * and edges used to neighbor features in constant time.
	 * @param points the array of polygon points
	 * @param dcel the DCEL object to add references to the priority queue vertices
	 * @return PriorityQueue&lt;{@link Vertex}&gt;
	 */
	protected PriorityQueue<Vertex> initialize(Vector2[] points, DoublyConnectedEdgeList dcel) {
		// get the number points
		int size = points.length;
		
		// create a priority queue for the vertices
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(size);
		
		Vertex rootVertex = null;
		Vertex prevVertex = null;
		
		Edge rootEdge = null;
		Edge prevEdge = null;
		
		// build the vertices and edges
		for (int i = 0; i < size; i++) {
			// get this vertex point
			Vector2 point = points[i];
			
			// create the vertex for this point
			Vertex vertex = new Vertex();
			vertex.point = point;
			// default the type to regular
			vertex.type = Vertex.Type.REGULAR;
			vertex.prev = prevVertex;
			vertex.dcelReference = dcel.vertices.get(i);			
			
			// set the previous vertex's next pointer
			if (prevVertex != null) {
				prevVertex.next = vertex;
			}
			
			// make sure we save the first vertex so we
			// can wire up the last and first to create
			// a cyclic list
			if (rootVertex == null) {
				rootVertex = vertex;
			}
			
			// get the neighboring points
			Vector2 point1 = points[i + 1 == size ? 0 : i + 1];
			Vector2 point0 = points[i == 0 ? size - 1 : i - 1];
			
			// get the vertex type
			vertex.type = this.getType(point0, point, point1);
			
			// set the previous vertex to this vertex
			prevVertex = vertex;
			// add the vertex to the priority queue
			queue.offer(vertex);
			
			// create the next edge
			Edge e = new Edge();
			e.prev = prevEdge;
			// the first vertex is this vertex
			e.v0 = vertex;
			
			// set the previous edge's end vertex and
			// next edge pointers
			if (prevEdge != null) {
				prevEdge.v1 = vertex;
				prevEdge.next = e;
			}
			
			// make sure we save the first edge so we
			// can wire up the last and first to create
			// a cyclic list
			if (rootEdge == null) {
				rootEdge = e;
			}
			
			// set the vertex's left and right edges
			vertex.left = e;
			vertex.right = prevEdge;
			
			// set the previous edge to this edge
			prevEdge = e;
		}
		
		// finally complete the cyclical lists
		
		// connect the first edge's previous pointer
		// to the last edge we created
		rootEdge.prev = prevEdge;
		// set the last edge's next pointer to the
		// first edge
		prevEdge.next = rootEdge;
		// set the last edge's end vertex pointer to
		// the first edge's start vertex
		prevEdge.v1 = rootEdge.v0;
		
		// set the previous edge of the first vertex
		rootVertex.right = prevEdge;
		// set the previous vertex of the first vertex
		rootVertex.prev = prevVertex;
		// set the last vertex's next pointer to the
		// first vertex
		prevVertex.next = rootVertex;
		
		// return the priority queue
		return queue;
	}
	
	/**
	 * Returns the vertex type given the previous and next points.
	 * @param point0 the previous point
	 * @param point the vertex point
	 * @param point1 the next point
	 * @return {@link Vertex.Type}
	 */
	protected Vertex.Type getType(Vector2 point0, Vector2 point, Vector2 point1) {
		// create the edge vectors
		Vector2 v1 = point0.to(point);
		Vector2 v2 = point.to(point1);
		
		// get the angle between the two edges (we assume CCW winding)
		double cross = v1.cross(v2);
		
		boolean pBelowP0 = this.isBelow(point, point0);
		boolean pBelowP1 = this.isBelow(point, point1);
		
		// where is p relative to its neighbors?
		if (pBelowP0 && pBelowP1) {
			// then check if the 
			// if its below both of them then we need
			// to check the interior angle
			if (cross > 0.0) {
				// if the cross product is greater than zero
				// this indicates that the angle is < pi
				
				// this vertex is an end vertex
				return Vertex.Type.END;
			} else {
				// this indicates that the angle is pi or greater
				
				// this vertex is a merge vertex
				return Vertex.Type.MERGE;
			}
		} else if (!pBelowP0 && !pBelowP1) {
			// if its above both of them then we need
			// to check the interior angle
			if (cross > 0.0) {
				// if the cross product is greater than zero
				// this indicates that the angle is < pi
				
				// this vertex is a start vertex
				return Vertex.Type.START;
			} else {
				// this indicates that the angle is pi or greater
				
				// this vertex is a split vertex
				return Vertex.Type.SPLIT;
			}
		}
		
		return Vertex.Type.REGULAR;
	}
	
	/**
	 * Returns true if the given point p is below the given point q.
	 * <p>
	 * If the point p and q form a horizontal line then p is considered
	 * below if its x coordinate is greater than q's x coordinate.
	 * @param p the point
	 * @param q another point
	 * @return boolean true if p is below q; false if p is above q
	 */
	protected boolean isBelow(Vector2 p, Vector2 q) {
		double diff = p.y - q.y;
		if (Math.abs(diff) < Epsilon.E) {
			if (p.x > q.x) {
				return true;
			} else {
				return false;
			}
		} else {
			if (diff < 0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Handles a {@link Vertex.Type#START} event.
	 * @param vertex the vertex
	 * @param tree the tree holding the current edges intersecting the sweep line
	 */
	protected void start(Vertex vertex, EdgeBinaryTree tree) {
		// we need to add the edge to the left to the tree
		// since the line in the next event may be intersecting it
		Edge leftEdge = vertex.left;
		tree.insert(leftEdge);
		// set the left edge's helper to this vertex
		leftEdge.helper = vertex;
	}
	
	/**
	 * Handles a {@link Vertex.Type#END} event.
	 * @param vertex the vertex
	 * @param tree the tree holding the current edges intersecting the sweep line
	 * @param dcel the DCEL object to add edges to if necessary
	 */
	protected void end(Vertex vertex, EdgeBinaryTree tree, DoublyConnectedEdgeList dcel) {
		// if the vertex type is an end vertex then we
		// know that we need to remove the right edge
		// since the sweep line no longer intersects it
		Edge rightEdge = vertex.right;
		// before we remove the edge we need to make sure
		// that we don't forget to link up MERGE vertices
		if (rightEdge.helper.type == Vertex.Type.MERGE) {
			// connect v to v.right.helper
			dcel.addHalfEdges(vertex.dcelReference, rightEdge.helper.dcelReference);
		}
		// remove v.right from T
		tree.remove(rightEdge);
	}
	
	/**
	 * Handles a {@link Vertex.Type#SPLIT} event.
	 * @param vertex the vertex
	 * @param tree the tree holding the current edges intersecting the sweep line
	 * @param dcel the DCEL object to add edges to if necessary
	 */
	protected void split(Vertex vertex, EdgeBinaryTree tree, DoublyConnectedEdgeList dcel) {
		// if we have a split vertex then we can find
		// the closest edge to the left side of the vertex
		// and attach its helper to this vertex
		Edge ej = tree.findClosest(vertex);
		
		// connect v to ej.helper
		dcel.addHalfEdges(vertex.dcelReference, ej.helper.dcelReference);
		
		// set the new helper for the edge
		ej.helper = vertex;
		// insert the edge to the left of this vertex
		tree.insert(vertex.left);
		// set the left edge's helper
		vertex.left.helper = vertex;
	}
	
	/**
	 * Handles a {@link Vertex.Type#MERGE} event.
	 * @param vertex the vertex
	 * @param tree the tree holding the current edges intersecting the sweep line
	 * @param dcel the DCEL object to add edges to if necessary
	 */
	protected void merge(Vertex vertex, EdgeBinaryTree tree, DoublyConnectedEdgeList dcel) {
		// get the previous edge
		Edge eiPrev = vertex.right;
		// check if its helper is a merge vertex
		if (eiPrev.helper.type == Vertex.Type.MERGE) {
			// connect v to v.right.helper
			dcel.addHalfEdges(vertex.dcelReference, eiPrev.helper.dcelReference);
		}
		// remove the previous edge since the sweep 
		// line no longer intersects with it
		tree.remove(eiPrev);
		// find the edge closest to the given vertex
		Edge ej = tree.findClosest(vertex);
		// is the edge's helper a merge vertex
		if (ej.helper.type == Vertex.Type.MERGE) {
			// connect v to ej.helper
			dcel.addHalfEdges(vertex.dcelReference, ej.helper.dcelReference);
		}
		// set the closest edge's helper to this vertex
		ej.helper = vertex;
	}
	
	/**
	 * Handles a {@link Vertex.Type#MERGE} event.
	 * @param vertex the vertex
	 * @param tree the tree holding the current edges intersecting the sweep line
	 * @param dcel the DCEL object to add edges to if necessary
	 */
	protected void regular(Vertex vertex, EdgeBinaryTree tree, DoublyConnectedEdgeList dcel) {
		// check if the interior is to the right of this vertex
		if (vertex.isInteriorRight()) {
			// if so, check the previous edge's helper to see
			// if its a merge vertex
			if (vertex.right.helper.type == Vertex.Type.MERGE) {
				// connect v to v.right.helper
				dcel.addHalfEdges(vertex.dcelReference, vertex.right.helper.dcelReference);
			}
			// remove the previous edge since the sweep 
			// line no longer intersects with it
			tree.remove(vertex.right);
			// add the next edge
			tree.insert(vertex.left);
			// set the helper
			vertex.left.helper = vertex;
		} else {
			// otherwise find the closest edge
			Edge ej = tree.findClosest(vertex);
			// check the helper type
			if (ej.helper.type == Vertex.Type.MERGE) {
				// connect v to ej.helper
				dcel.addHalfEdges(vertex.dcelReference, ej.helper.dcelReference);
			}
			// set the new helper
			ej.helper = vertex;
		}
	}
}
