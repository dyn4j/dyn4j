package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.Vector2;

/**
 * Represents a vertex of a simple polygon with a linked list
 * running through it.  It also contains the area produced by this
 * vertex and the adjacent vertices. We also use it to track the
 * vertex index (for comparing adjacent segments) and the adjacent
 * segments.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
class SimplePolygonVertex {
	/** The index of the vertex in the original simple polygon */
	final int index;

	/** The vertex point */
	final Vector2 point;

	/** The prev vertex */
	SimplePolygonVertex prev;
	
	/** The next vertex */
	SimplePolygonVertex next;
	
	/** The previous segment */
	SegmentTreeLeaf prevSegment;
	
	/** The next segment */
	SegmentTreeLeaf nextSegment;
	
	/**
	 * Minimal constructor.
	 * @param index the vertex index
	 * @param point the vertex point
	 */
	public SimplePolygonVertex(int index, Vector2 point) {
		this.index = index;
		this.point = point;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.point.toString();
	}
}