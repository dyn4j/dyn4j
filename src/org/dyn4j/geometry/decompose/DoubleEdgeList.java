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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Highly specialized Doubly Connected Edge List (DCEL) used to store vertices of a simple polygon and then be used
 * to create and store triangulations and convex decompositions of that same polygon.
 * <p>
 * Upon creation and initialization, the {@link #vertices}, {@link #edges}, and {@link #faces} lists are
 * populated.  The {@link #vertices} list will have the same indexing as the source {@link Vector2}[].
 * The {@link #edges} list will have edges with the same indexing as the source {@link Vector2}[]
 * with the exception that twin vertices are stored in odd indices.
 * <p>
 * Since this implementation only handles simple polygons, only one {@link DoubleEdgeListFace} will be created
 * when the DCEL is created.  As more {@link DoubleEdgeListHalfEdge}s are added the number of faces will
 * increase.
 * <p>
 * {@link DoubleEdgeListHalfEdge}s are added to the DCEL via the {@link #addHalfEdges(DoubleEdgeListVertex, DoubleEdgeListVertex)} method.
 * It's the responsibility of the calling class(es) to store references to the DCEL vertices.  This
 * can be achieved since the indexing of the {@link #vertices} list is the same as the source {@link Vector2}[].
 * No check is performed to ensure that a pair of {@link DoubleEdgeListHalfEdge}s are added that already exist.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
final class DoubleEdgeList {
	/** The list of nodes */
	final List<DoubleEdgeListVertex> vertices;
	
	/** The list of half edges */
	final List<DoubleEdgeListHalfEdge> edges;
	
	/** The list of faces */
	final List<DoubleEdgeListFace> faces;
	
	/**
	 * Full constructor.
	 * @param points the points of the simple polygon
	 */
	public DoubleEdgeList(Vector2[] points) {
		this.vertices = new ArrayList<DoubleEdgeListVertex>();
		this.edges = new ArrayList<DoubleEdgeListHalfEdge>();
		this.faces = new ArrayList<DoubleEdgeListFace>();
		this.initialize(points);
	}
	
	/**
	 * Initializes the DCEL class given the points of the polygon.
	 * @param points the points of the polygon
	 */
	public void initialize(Vector2[] points) {
		// get the number of points
		int size = points.length;
		
		// we will always have exactly one face at the beginning
		DoubleEdgeListFace face = new DoubleEdgeListFace();
		this.faces.add(face);
		
		DoubleEdgeListHalfEdge prevLeftEdge = null;
		DoubleEdgeListHalfEdge prevRightEdge = null;
		
		// loop over the points creating the vertices and
		// half edges for the data structure
		for (int i = 0; i < size; i++) {
			Vector2 point = points[i];
			
			DoubleEdgeListVertex vertex = new DoubleEdgeListVertex(point);
			DoubleEdgeListHalfEdge left = new DoubleEdgeListHalfEdge();
			DoubleEdgeListHalfEdge right = new DoubleEdgeListHalfEdge();
			
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
			this.edges.add(left);
			this.edges.add(right);
			
			// populate the vertex
			vertex.leaving = left;
			
			// add the vertex to the vertices list
			this.vertices.add(vertex);
			
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
		DoubleEdgeListHalfEdge firstLeftEdge = this.edges.get(0);
		prevLeftEdge.next = firstLeftEdge;
		
		// set the first right edge's next pointer
		// to the last right edge we created
		// (note that right edges are at odd indices)
		DoubleEdgeListHalfEdge firstRightEdge = this.edges.get(1);
		firstRightEdge.next = prevRightEdge;
		// set the last right edge's origin to the first
		// vertex in the list
		prevRightEdge.origin = this.vertices.get(0);
		
		// set the edge of the only face to the first
		// left edge
		// (note that the interior of each face has CCW winding)
		face.edge = firstLeftEdge;
	}
	
	/**
	 * Adds two half edges to this DCEL object given the vertices to connect.
	 * <p>
	 * This method assumes that no crossing edges will be added.
	 * @param i the first vertex index
	 * @param j the second vertex index
	 */
	public void addHalfEdges(int i, int j) {
		DoubleEdgeListVertex vertex1 = this.vertices.get(i);
		DoubleEdgeListVertex vertex2 = this.vertices.get(j);
		this.addHalfEdges(vertex1, vertex2);
	}
	
	/**
	 * Adds two half edges to this DCEL object given the vertices to connect.
	 * <p>
	 * This method assumes that no crossing edges will be added.
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 */
	final void addHalfEdges(DoubleEdgeListVertex v1, DoubleEdgeListVertex v2) {
		// adding an edge splits the current face into two faces
		// so we need to create a new face
		DoubleEdgeListFace face = new DoubleEdgeListFace();
		
		// create the new half edges for the new edge
		DoubleEdgeListHalfEdge left = new DoubleEdgeListHalfEdge();
		DoubleEdgeListHalfEdge right = new DoubleEdgeListHalfEdge();
		
		// find the reference face for these two vertices
		// the reference face is the face on which both the given
		// vertices are on
		DoubleEdgeListFace referenceDoubleEdgeListFace = this.getReferenceFace(v1, v2);
		
		// get the previous edges for these vertices that are on
		// the reference face
		DoubleEdgeListHalfEdge prev1 = this.getPreviousEdge(v1, referenceDoubleEdgeListFace);
		DoubleEdgeListHalfEdge prev2 = this.getPreviousEdge(v2, referenceDoubleEdgeListFace);
		
		face.edge = left;
		referenceDoubleEdgeListFace.edge = right;
		
		// setup both half edges
		left.face = face;
		left.next = prev2.next;
		left.origin = v1;
		left.twin = right;
		
		right.face = referenceDoubleEdgeListFace;
		right.next = prev1.next;
		right.origin = v2;
		right.twin = left;
		
		// set the previous edge's next pointers to the new half edges
		prev1.next = left;
		prev2.next = right;
		
		// set the new face for all the edges in the left list
		DoubleEdgeListHalfEdge curr = left.next;
		while (curr != left) {
			curr.face = face;
			curr = curr.next;
		}
		
		// add the new edges to the list
		this.edges.add(left);
		this.edges.add(right);
		
		// add the new face to the list
		this.faces.add(face);
	}
	
	/**
	 * Walks around the given face and finds the previous edge
	 * for the given vertex.
	 * <p>
	 * This method assumes that the given vertex will be on the given face.
	 * @param vertex the vertex to find the previous edge for
	 * @param face the face the edge should lie on
	 * @return {@link DoubleEdgeListHalfEdge} the previous edge
	 */
	final DoubleEdgeListHalfEdge getPreviousEdge(DoubleEdgeListVertex vertex, DoubleEdgeListFace face) {
		// find the vertex on the given face and return the
		// edge that points to it
		DoubleEdgeListHalfEdge twin = vertex.leaving.twin;
		DoubleEdgeListHalfEdge edge = vertex.leaving.twin.next.twin;
		// look at all the edges that have their
		// destination as this vertex
		while (edge != twin) {
			// we can't use the getPrevious method on the leaving
			// edge since this doesn't give us the right previous edge
			// in all cases.  The real criteria is to find the edge that
			// has this vertex as the destination and has the same face
			// as the given face
			if (edge.face == face) {
				return edge;
			}
			edge = edge.next.twin;
		}
		// if we get here then its the last edge
		return edge;
	}
	
	/**
	 * Finds the face that both vertices are on.  
	 * <p>
	 * If the given vertices are connected then the first common face is returned.
	 * <p>
	 * If the given vertices do not have a common face the first vertex's leaving
	 * edge's face is returned.
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return {@link DoubleEdgeListFace} the face on which both vertices lie
	 */
	final DoubleEdgeListFace getReferenceFace(DoubleEdgeListVertex v1, DoubleEdgeListVertex v2) {
		// find the face that both vertices are on
		
		// if the leaving edge faces are already the same then just return
		if (v1.leaving.face == v2.leaving.face) return v1.leaving.face;
		
		// loop over all the edges whose destination is the first vertex (constant time)
		DoubleEdgeListHalfEdge e1 = v1.leaving.twin.next.twin;
		while (e1 != v1.leaving.twin) {
			// loop over all the edges whose destination is the second vertex (constant time)
			DoubleEdgeListHalfEdge e2 = v2.leaving.twin.next.twin;
			while (e2 != v2.leaving.twin) {
				// if we find a common face, that must be the reference face
				if (e1.face == e2.face) return e1.face;
				e2 = e2.next.twin;
			}
			e1 = e1.next.twin;
		}
		
		// if we don't find a common face then return v1.leaving.face
		return v1.leaving.face;
	}
	
	/**
	 * Removes the half edges specified by the given interior edge index.
	 * <p>
	 * This method removes both halves of the edge.
	 * @param index the index of the interior half edge to remove
	 */
	public void removeHalfEdges(int index) {
		DoubleEdgeListHalfEdge e = this.edges.get(index);
		this.removeHalfEdges(index, e);
	}
	
	/**
	 * Removes the given half edge and its twin.
	 * @param edge the half edge to remove
	 */
	final void removeHalfEdges(DoubleEdgeListHalfEdge edge) {
		int index = this.edges.indexOf(edge);
		this.removeHalfEdges(index, edge);
	}
	
	/**
	 * Removes the given half edge and its twin.
	 * @param index the index of the given edge
	 * @param edge the half edge to remove
	 */
	final void removeHalfEdges(int index, DoubleEdgeListHalfEdge edge) {
		// wire up the two end points to remove the edge
		DoubleEdgeListFace face = edge.twin.face;
		
		// we only need to re-wire the internal edges
		DoubleEdgeListHalfEdge ePrev = edge.getPrevious();
		DoubleEdgeListHalfEdge tPrev = edge.twin.getPrevious();
		DoubleEdgeListHalfEdge eNext = edge.next;
		DoubleEdgeListHalfEdge tNext = edge.twin.next;
		
		ePrev.next = tNext;
		tPrev.next = eNext;
		
		face.edge = eNext;
		
		// set the face
		DoubleEdgeListHalfEdge te = eNext;
		while (te != tNext) {
			te.face = face;
			te = te.next;
		}
		
		// remove the unneeded face
		this.faces.remove(edge.face);
		
		// remove the edges
		this.edges.remove(index); // the edge
		this.edges.remove(index); // the edge's twin
	}
	
	/**
	 * Returns the convex decomposition of this DCEL assuming that the remaining
	 * faces are all convex polygons.
	 * @return List&lt;{@link Convex}&gt;
	 */
	public List<Convex> getConvexDecomposition() {
		List<Convex> convexes = new ArrayList<Convex>();
		
		// get the number of faces
		int fSize = this.faces.size();
		
		// create a y-monotone polygon for each face
		for (int i = 0; i < fSize; i++) {
			// get the face
			DoubleEdgeListFace face = this.faces.get(i);
			
			// get the number of Edges ( = the number of vertices) on this face
			int size = face.getEdgeCount();
			
			// get the reference edge of the face
			DoubleEdgeListHalfEdge left = face.edge;
			
			Vector2[] vertices = new Vector2[size];
			vertices[0] = left.origin.point;
			
			left = left.next;
			
			int j = 1;
			while (left != face.edge) {
				vertices[j++] = left.origin.point;
				left = left.next;
			}
			
			if (vertices.length < 3) {
				throw new IllegalArgumentException(Messages.getString("geometry.decompose.crossingEdges"));
			}
			
			Polygon p = Geometry.createPolygon(vertices);
			convexes.add(p);
		}
		
		return convexes;
	}
	
	/**
	 * Returns the triangulation of this DCEL assuming that the remaining
	 * faces are all triangles.
	 * @return List&lt;{@link Triangle}&gt;
	 * @since 3.1.9
	 */
	public List<Triangle> getTriangulation() {
		List<Triangle> triangles = new ArrayList<Triangle>();
		
		// get the number of faces
		int fSize = this.faces.size();
		
		// create a y-monotone polygon for each face
		for (int i = 0; i < fSize; i++) {
			// get the face
			DoubleEdgeListFace face = this.faces.get(i);
			
			// get the number of Edges ( = the number of vertices) on this face
			int size = face.getEdgeCount();
			
			// get the reference edge of the face
			DoubleEdgeListHalfEdge left = face.edge;
			
			Vector2[] vertices = new Vector2[size];
			vertices[0] = left.origin.point;
			
			left = left.next;
			
			int j = 1;
			while (left != face.edge) {
				vertices[j++] = left.origin.point;
				left = left.next;
			}
			
			// the vertices should form a triangle
			if (vertices.length != 3) {
				throw new IllegalArgumentException(Messages.getString("geometry.decompose.crossingEdges"));
			}
			
			// add the triangle
			Triangle t = Geometry.createTriangle(vertices[0], vertices[1], vertices[2]);
			triangles.add(t);
		}
		
		return triangles;
	}
	
	/**
	 * Performs a triangulation of the DCEL assuming all faces are Monotone Y polygons.
	 */
	public void triangulateYMonotonePolygons() {
		List<MonotonePolygon<DoubleEdgeListVertex>> monotonePolygons = this.getYMonotonePolygons();
		int size = monotonePolygons.size();
		for (int i = 0; i < size; i++) {
			this.triangulateYMonotonePolygon(monotonePolygons.get(i));
		}
	}
	
	/**
	 * Triangulates the given y-monotone polygon adding the new diagonals to this DCEL.
	 * @param monotonePolygon the monotone polygon (x or y) to triangulate
	 */
	final void triangulateYMonotonePolygon(MonotonePolygon<DoubleEdgeListVertex> monotonePolygon) {
		// create a stack to support triangulation
		List<MonotoneVertex<DoubleEdgeListVertex>> stack = new ArrayList<MonotoneVertex<DoubleEdgeListVertex>>();
		
		// get the sorted monotone vertices
		List<MonotoneVertex<DoubleEdgeListVertex>> vertices = monotonePolygon.vertices;
		
		// a monotone polygon can be triangulated in O(n) time
		
		// push the first two onto the stack
		// push
		stack.add(vertices.get(0));
		stack.add(vertices.get(1));
		
		int i = 2;
		while (!stack.isEmpty()) {
			// get the next vertex in the sorted list
			MonotoneVertex<DoubleEdgeListVertex> v = vertices.get(i);
			
			// get the bottom and top elements of the stack
			MonotoneVertex<DoubleEdgeListVertex> vBot = stack.get(0);
			MonotoneVertex<DoubleEdgeListVertex> vTop = stack.get(stack.size() - 1);
			
			// is the current vertex adjacent to the bottom element
			// but not to the top element?
			if (v.isAdjacent(vBot) && !v.isAdjacent(vTop)) {
				// create the triangles and pop all the points
				while (stack.size() > 1) {
					// pop
					MonotoneVertex<DoubleEdgeListVertex> vt = stack.remove(stack.size() - 1);
					// create diagonal
					this.addHalfEdges(v.data, vt.data);
				}
				// clear the bottom point
				stack.clear();
				
				// push the remaining edge
				stack.add(vTop);
				stack.add(v);
			} else if (v.isAdjacent(vTop) && !v.isAdjacent(vBot)) {
				double cross = 0;
				
				int sSize = stack.size();
				while (sSize > 1) {
					MonotoneVertex<DoubleEdgeListVertex> vt = stack.get(sSize - 1);
					MonotoneVertex<DoubleEdgeListVertex> vt1 = stack.get(sSize - 2);
					
					Vector2 p1 = v.data.point;
					Vector2 p2 = vt.data.point;
					Vector2 p3 = vt1.data.point;
					
					// what chain is the current vertex on
					if (v.chainType == MonotoneChainType.LEFT || v.chainType == MonotoneChainType.BOTTOM) {
						Vector2 v1 = p2.to(p3);
						Vector2 v2 = p2.to(p1);
						cross = v1.cross(v2);
					} else {
						Vector2 v1 = p1.to(p2);
						Vector2 v2 = p3.to(p2);
						cross = v1.cross(v2);
					}
					
					// make sure the angle is less than pi before we create
					// a triangle from the points
					// epsilon is to handle near colinearity
					if (cross < Epsilon.E) {
						// add the half edges
						this.addHalfEdges(v.data, vt1.data);
						// remove the top element
						// pop
						stack.remove(sSize - 1);
						sSize--;
					} else {
						// once we find an angle that is greater than pi then
						// we can quit and move to the next vertex in the sorted list
						break;
					}
				}
				stack.add(v);
			} else if (v.isAdjacent(vTop) && v.isAdjacent(vBot)) {
				// create the triangles and pop all the points
				// pop
				stack.remove(stack.size() - 1);
				while (stack.size() > 1) {
					// pop
					MonotoneVertex<DoubleEdgeListVertex> vt = stack.remove(stack.size() - 1);
					// create diagonal
					this.addHalfEdges(v.data, vt.data);
				}
				// we are done
				break;
			}
			i++;
		}
	}

	/**
	 * Returns a list of y-monotone polygons from the faces of this DCEL.
	 * <p>
	 * This method assumes that all faces within this DCEL are y-monotone and does not
	 * perform any verification of this assumption.
	 * @return List&lt;{@link MonotonePolygon}&gt;
	 */
	final List<MonotonePolygon<DoubleEdgeListVertex>> getYMonotonePolygons() {
		// get the number of faces
		int fSize = this.faces.size();
		
		// create a list to store the y-monotone polygons
		List<MonotonePolygon<DoubleEdgeListVertex>> yMonotonePolygons = new ArrayList<MonotonePolygon<DoubleEdgeListVertex>>(fSize);
		
		// create a y-monotone polygon for each face
		for (int i = 0; i < fSize; i++) {
			// get the face
			DoubleEdgeListFace face = this.faces.get(i);
			
			// Each face contains a y-monotone polygon.  We need to obtain a sorted
			// doubly-linked list of the vertices to triangulate easily.  We can create
			// the doubly-linked list while finding the maximum vertex in O(n) time.  
			// We can sort the list in O(n) time using the doubly-linked list we just
			// created.
			
			// get the number of Edges ( = the number of vertices) on this face
			int size = face.getEdgeCount();
			
			// get the reference edge of the face
			DoubleEdgeListHalfEdge left = face.edge;
			
			// create the first vertex
			MonotoneVertex<DoubleEdgeListVertex> root = new MonotoneVertex<DoubleEdgeListVertex>(left.origin);
			
			// move to the next origin
			left = left.next;
			
			// build the doubly linked list of vertices
			MonotoneVertex<DoubleEdgeListVertex> prev = root;
			MonotoneVertex<DoubleEdgeListVertex> curr = null;
			MonotoneVertex<DoubleEdgeListVertex> max  = root;
			while (left != face.edge) {
				// create a new vertex
				curr = new MonotoneVertex<DoubleEdgeListVertex>(left.origin);
				curr.previous = prev;
				
				// set the previous vertex's next pointer to the new one
				prev.next = curr;
				
				// find the point with maximum y
				Vector2 p = curr.data.point;
				Vector2 q = max.data.point;
				// compare the y values
				double diff = p.y - q.y;
				if (diff == 0.0) {
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
				
				// set the previous to the current
				prev = curr;
			}
			
			// wire up the last and first vertices
			root.previous = curr;
			curr.next = root;
			
			// create a sorted array of Vertices
			List<MonotoneVertex<DoubleEdgeListVertex>> sorted = new ArrayList<MonotoneVertex<DoubleEdgeListVertex>>(size);
			
			// the first point is the vertex with maximum y
			sorted.add(max);
			// default the location to the left chain
			max.chainType = MonotoneChainType.LEFT;
			
			// perform a O(n) sorting routine starting from the
			// maximum y vertex
			MonotoneVertex<DoubleEdgeListVertex> currLeft = max.next;
			MonotoneVertex<DoubleEdgeListVertex> currRight = max.previous;
			int j = 1;
			while (j < size) {
				// get the left and right chain points
				Vector2 l = currLeft.data.point;
				Vector2 r = currRight.data.point;
				
				// which has the smaller y?
				if (l.y > r.y) {
					sorted.add(currLeft);
					currLeft.chainType = MonotoneChainType.LEFT;
					currLeft = currLeft.next;
				} else {
					sorted.add(currRight);
					currRight.chainType = MonotoneChainType.RIGHT;
					currRight = currRight.previous;
				}
				
				j++;
			}
			// set the last point's chain to the right
			sorted.get(size - 1).chainType = MonotoneChainType.RIGHT;
			
			// add a new y-monotone polygon to the list
			yMonotonePolygons.add(new MonotonePolygon<DoubleEdgeListVertex>(MonotonePolygonType.Y, sorted));
		}
		
		return yMonotonePolygons;
	}
	
	/**
	 * Performs the Hertel-Mehlhorn algorithm on the given DCEL assuming that
	 * it is a valid triangulation.
	 * <p>
	 * This method will remove unnecessary diagonals and remove faces that get merged
	 * leaving a convex decomposition.
	 * <p>
	 * This method is guaranteed to produce a convex decomposition with no more than
	 * 4 times the minimum number of convex pieces.
	 */
	public void hertelMehlhorn() {
		// loop over all the edges and see which we can remove
		int vSize = this.vertices.size();
		
		// This method will remove any unnecessary diagonals (those that do not
		// form reflex vertices when removed).  This method is O(n) where n is the
		// number of diagonals added to the original DCEL.  We can start processing
		// diagonals after all the initial diagonals (the initial diagonals are the
		// edges of the original polygon).  We can also skip every other half edge
		// since each edge is stored with its twin in the next index.
		
		int i = vSize * 2;
		while (i < this.edges.size()) {
			
			// see if removing this edge creates a reflex vertex at the end points
			DoubleEdgeListHalfEdge e = this.edges.get(i);
			
			// test the first end point
			DoubleEdgeListVertex v1 = e.origin;
			DoubleEdgeListVertex v0 = e.getPrevious().origin;
			DoubleEdgeListVertex v2 = e.twin.next.next.origin;
			
			// check if removing this half edge creates a reflex vertex at the
			// origin vertex of this half edge
			if (isReflex(v0, v1, v2)) {
				// if it did, then we cannot remove this edge
				// so skip the next one and continue
				i+=2;
				continue;
			}
			
			// test the other end point
			v1 = e.twin.origin;
			v0 = e.twin.getPrevious().origin;
			v2 = e.next.next.origin;
			
			// check if removing this half edge creates a reflex vertex at the
			// origin of this half edge's twin
			if (isReflex(v0, v1, v2)) {
				// if it did, then we cannot remove this edge
				// so skip the next one and continue
				i+=2;
				continue;
			}
			
			// otherwise we can remove this edge
			this.removeHalfEdges(i, e);
		}
	}
	
	/**
	 * Returns true if the given vertices create a reflex vertex.
	 * @param v0 the previous vertex
	 * @param v1 the vertex
	 * @param v2 the next vertex
	 * @return boolean
	 */
	boolean isReflex(DoubleEdgeListVertex v0, DoubleEdgeListVertex v1, DoubleEdgeListVertex v2) {
		Vector2 p0 = v0.point;
		Vector2 p1 = v1.point;
		Vector2 p2 = v2.point;
		
		Vector2 e1 = p0.to(p1);
		Vector2 e2 = p1.to(p2);
		
		// get the angle between the two edges (we assume CCW winding)
		double cross = e1.cross(e2);
		
		if (cross < 0) return true;
		
		return false;
	}
}
