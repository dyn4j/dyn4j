package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Vector2;

final class RTreeLeaf extends RTreeNode {
	// the segment

	Vector2 point1;
	Vector2 point2;

	int index1;
	int index2;
	
	/**
	 * Minimal constructor.
	 * @param item the collision item
	 */
	public RTreeLeaf(Vector2 point1, Vector2 point2, int index1, int index2) {
		AABB.setFromPoints(point1, point2, this.aabb);
		this.point1 = point1;
		this.point2 = point2;
		this.index1 = index1;
		this.index2 = index2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RTreeLeaf[P1=").append(this.point1)
		  .append("|P2=").append(this.point2)
		  .append("|AABB=").append(this.aabb.toString())
		  .append("|Height=").append(this.height)
		  .append("]");
		return sb.toString();
	}
}