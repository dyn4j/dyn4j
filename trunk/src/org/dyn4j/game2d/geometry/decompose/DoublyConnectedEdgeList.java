/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
import java.util.List;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Highly specialized DCEL class used to store vertices of a simple polygon.
 * <p>
 * Upon creation, the {@link #vertices}, {@link #edges}, and {@link #faces} lists are
 * populated.  The {@link #vertices} list will have the same indexing as the source {@link Vector2}[].
 * The {@link #edges} list will have edges with the same indexing as the source {@link Vector2}[]
 * with the exception that twin vertices are stored in odd indices.
 * <p>
 * Since this implementation only handles simple polygons, only one {@link Face} will be created
 * when the DCEL is created.  As more {@link HalfEdge}s are added the number of faces will
 * increase.
 * <p>
 * {@link HalfEdge}s are added to the DCEL via the {@link #addHalfEdges(Vertex, Vertex)} method.
 * It's the responsibility of the calling class(es) to store references to the DCEL vertices.  This
 * can be achieved since the indexing of the {@link #vertices} list is the same as the source {@link Vector2}[].
 * <p>
 * This class contains one method to decompose the DCEL into y-monotone polygons; {@link #decompose()}.
 * The method makes no attempt at verifying the monoticity of the polygons generated, but simply takes
 * each {@link Face} of the DCEL and creates a polygon from it.  Use the {@link MonotonePolygon#isValid()}
 * method to test if the created polygon.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class DoublyConnectedEdgeList {
	/**
	 * Represents a node in the Doubly-Connected Edge List.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	public class Vertex {
		/** The comparable data for this node */
		protected Vector2 point;
		
		/** The the leaving edge */
		protected HalfEdge leaving;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("VERTEX[")
			.append(this.point)
			.append("]");
			return sb.toString();
		}
		
		/**
		 * Returns the comparable data for this node.
		 * @return T
		 */
		public Vector2 getPoint() {
			return this.point;
		}
		
		/**
		 * Returns the leaving edge for this node.
		 * @return {@link DoublyConnectedEdgeList.HalfEdge}
		 */
		public HalfEdge getLeaving() {
			return this.leaving;
		}
		
		/**
		 * Returns the edge from this node to the given node.
		 * @param node the node to find an edge to
		 * @return {@link DoublyConnectedEdgeList.HalfEdge}
		 */
		public HalfEdge getEdgeTo(Vertex node) {
			if (leaving != null) {
				if (leaving.twin.origin == node) {
					return leaving;
				} else {
					HalfEdge edge = leaving.twin.next;
					while (edge != leaving) {
						if (edge.twin.origin == node) {
							return edge;
						} else {
							edge = edge.twin.next;
						}
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * Represents a half edge of the Doubly-Connected Edge List.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	public class HalfEdge {
		/** The half edge origin */
		protected Vertex origin;
		
		/** The adjacent twin of this half edge */
		protected HalfEdge twin;
		
		/** The adjacent edge next in the list having the same face */
		protected HalfEdge next;
		
		/** The adjacent face of this half edge */
		protected Face face;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("HALF_EDGE[")
			.append(this.origin).append("|")
			.append(this.next.origin)
			.append("]");
			return sb.toString();
		}
		
		/**
		 * Returns this half edge's origin.
		 * @return {@link DoublyConnectedEdgeList.Vertex}
		 */
		public Vertex getOrigin() {
			return this.origin;
		}
		
		/**
		 * Returns this half edge's destination.
		 * @return {@link DoublyConnectedEdgeList.Vertex}
		 */
		public Vertex getDestination() {
			return this.next.origin;
		}
		
		/**
		 * Returns this half edge's twin half edge.
		 * @return {@link DoublyConnectedEdgeList.HalfEdge}
		 */
		public HalfEdge getTwin() {
			return twin;
		}
		
		/**
		 * Returns this half edge's next half edge.
		 * @return {@link DoublyConnectedEdgeList.HalfEdge}
		 */
		public HalfEdge getNext() {
			return next;
		}
		
		/**
		 * Returns the previous half edge having the same
		 * face as this half edge.
		 * @return {@link DoublyConnectedEdgeList.HalfEdge}
		 */
		public HalfEdge getPrevious() {
			HalfEdge edge = twin.next.twin;
			// walk around the face
			while (edge.next != this) {
				edge = edge.next.twin;
			}
			return edge;
		}
		
		/**
		 * Returns this half edge's face.
		 * @return {@link DoublyConnectedEdgeList.Face}
		 */
		public Face getFace() {
			return face;
		}
	}
	
	/**
	 * Represents a face in the Doubly-Connected Edge List.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	public class Face {
		/** An edge of the edge list enclosing this face */
		protected HalfEdge edge;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FACE[")
			.append(this.edge)
			.append("]");
			return sb.toString();
		}
		
		/**
		 * Returns the number of edges on this face.
		 * @return int
		 */
		public int getEdgeCount() {
			HalfEdge edge = this.edge;
			int count = 0;
			if (edge != null) {
				count++;
				while (edge.next != this.edge) {
					count++;
					edge = edge.next;
				}
			}
			return count;
		}
	}
	
	/** The list of nodes */
	protected List<Vertex> vertices = new ArrayList<Vertex>();
	
	/** The list of half edges */
	protected List<HalfEdge> edges = new ArrayList<HalfEdge>();
	
	/** The list of faces */
	protected List<Face> faces = new ArrayList<Face>();
	
	/**
	 * Full constructor.
	 * @param points the points of the simple polygon
	 */
	public DoublyConnectedEdgeList(Vector2[] points) {
		this.initialize(points);
	}
	
	/**
	 * Initializes the DCEL class given the points of the polygon.
	 * @param points the points of the polygon
	 */
	protected void initialize(Vector2[] points) {
		// get the number of points
		int size = points.length;
		
		// we will always have exactly one face at the beginning
		Face face = new Face();
		faces.add(face);
		
		HalfEdge prevLeftEdge = null;
		HalfEdge prevRightEdge = null;
		
		// loop over the points creating the vertices and
		// half edges for the data structure
		for (int i = 0; i < size; i++) {
			Vector2 point = points[i];
			
			Vertex vertex = new Vertex();
			HalfEdge left = new HalfEdge();
			HalfEdge right = new HalfEdge();
			
			// create and populate the left
			// and right half edges
			left.face = face;
			left.next = null;
			left.origin = vertex;
			left.twin = right;
			
			right.face = null;
			right.next = prevRightEdge;
			right.origin = null;
			right.twin = left;
			
			// add the edges the edge list
			edges.add(left);
			edges.add(right);
			
			// populate the vertex
			vertex.leaving = left;
			vertex.point = point;
			
			// add the vertex to the vertices list
			vertices.add(vertex);
			
			// set the previous next edge to this left edge
			if (prevLeftEdge != null) {
				prevLeftEdge.next = left;
			}
			
			// set the previous right edge origin to this vertex
			if (prevRightEdge != null) {
				prevRightEdge.origin = vertex;
			}
			
			// set the new previous edges
			prevLeftEdge = left;
			prevRightEdge = right;
		}
		
		// set the last left edge's next pointer to the
		// first left edge we created
		HalfEdge firstLeftEdge = edges.get(0);
		prevLeftEdge.next = firstLeftEdge;
		
		// set the first right edge's next pointer
		// to the last right edge we created
		// (note that right edges are at odd indices)
		HalfEdge firstRightEdge = edges.get(1);
		firstRightEdge.next = prevRightEdge;
		// set the last right edge's origin to the first
		// vertex in the list
		prevRightEdge.origin = vertices.get(0);
		
		// set the edge of the only face to the first
		// left edge
		// (note that the interior of each face has CCW winding)
		face.edge = firstLeftEdge;
	}
	
	/**
	 * Adds two half edges to this DCEL object given the vertices to connect.
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 */
	public void addHalfEdges(Vertex v1, Vertex v2) {
		// adding an edge splits the current face into two faces
		// so we need to create a new face
		Face face = new Face();
		
		// create the new half edges for the new edge
		HalfEdge left = new HalfEdge();
		HalfEdge right = new HalfEdge();
		
		HalfEdge prev1 = v1.leaving.twin.next.twin;
		HalfEdge prev2 = v2.leaving.twin.next.twin;
		
		face.edge = left;
		v1.leaving.face.edge = right;
		
		left.face = face;
		left.next = prev2.next;
		left.origin = v1;
		left.twin = right;
		
		right.face = v1.leaving.face;
		right.next = prev1.next;
		right.origin = v2;
		right.twin = left;
		
		prev1.next = left;
		prev2.next = right;
		
		// set the new face for all the edges in the left list
		HalfEdge curr = left.next;
		while (curr != left) {
			curr.face = face;
			curr = curr.next;
		}
		
		this.edges.add(left);
		this.edges.add(right);
		this.faces.add(face);
	}
	
	/**
	 * Decomposes this DCEL object into an array of y-monotone polygons.
	 * <p>
	 * This method does not check the faces to determine if they are y-monotone.
	 * @return {@link MonotonePolygon}[]
	 */
	public MonotonePolygon[] decompose() {
		// get the number of faces
		int fSize = this.faces.size();
		
		// create an array to hold the y-monotone polygons
		MonotonePolygon[] polygons = new MonotonePolygon[fSize];
		
		// create a y-monotone polygon for each face
		for (int i = 0; i < fSize; i++) {
			// get the face
			Face face = this.faces.get(i);
			
			// get the number of Edges ( = the number of vertices) on this face
			int size = face.getEdgeCount();
			
			// get the reference edge of the face
			DoublyConnectedEdgeList.HalfEdge left = face.edge;
			
			// create the first vertex
			MonotoneVertex root = new MonotoneVertex();
			root.point = left.origin.point;
			
			// move to the next origin
			left = left.next;
			
			// build the doubly linked list of vertices
			MonotoneVertex prev = root;
			MonotoneVertex curr = null;
			MonotoneVertex max  = root;
			while (left != face.edge) {
				// create a new vertex
				curr = new MonotoneVertex();
				curr.point = left.origin.point;
				curr.prev = prev;
				
				prev.next = curr;
				
				// set the previous to the current
				prev = curr;
				
				// find the point with maximum y
				Vector2 p = curr.point;
				Vector2 q = max.point;
				// compare the y values
				double diff = p.y - q.y;
				if (Math.abs(diff) < Epsilon.E) {
					// if they are near zero then
					// compare the x values
					diff = p.x - q.x;
					if (diff < 0) {
						max = curr;
					}
				} else if (diff > 0.0) {
					max = curr;
				}
				
				// move to the next point
				left = left.next;
			}
			
			// wire up the last and first vertices
			root.prev = curr;
			curr.next = root;
			
			// create a sorted array of Vertices
			MonotoneVertex[] sorted = new MonotoneVertex[size];
			
			// the first point is the vertex with maximum y
			sorted[0] = max;
			// default the location to the left chain
			max.chain = MonotoneChain.Type.LEFT;
			
			// perform a O(n) sorting routine starting from the
			// maximum y vertex
			MonotoneVertex currLeft = max.next;
			MonotoneVertex currRight = max.prev;
			int j = 1;
			while (j < size) {
				// get the left and right chain points
				Vector2 l = currLeft.point;
				Vector2 r = currRight.point;
				
				// which has the smaller y?
				if (l.y > r.y) {
					sorted[j] = currLeft;
					currLeft.chain = MonotoneChain.Type.LEFT;
					currLeft = currLeft.next;
				} else {
					sorted[j] = currRight;
					currRight.chain = MonotoneChain.Type.RIGHT;
					currRight = currRight.prev;
				}
				
				j++;
			}
			// set the last point's chain to the right
			sorted[size - 1].chain = MonotoneChain.Type.RIGHT;
			
			// create the y-monotone polygon
			MonotonePolygon yMono = new MonotonePolygon(MonotonePolygon.Type.Y, sorted);
			// add it to the array
			polygons[i] = yMono;
		}
		
		// return all the y-monotone polygons
		return polygons;
	}
}
