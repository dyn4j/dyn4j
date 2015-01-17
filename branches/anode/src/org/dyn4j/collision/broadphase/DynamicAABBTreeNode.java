package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Represents a node in the tree.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public class DynamicAABBTreeNode<E extends Collidable<T>, T extends Fixture> {
	/** The left child */
	protected DynamicAABBTreeNode<E, T> left;
	
	/** The right child */
	protected DynamicAABBTreeNode<E, T> right;
	
	/** The parent node */
	protected DynamicAABBTreeNode<E, T> parent;
	
	/** The height of this subtree */
	protected int height;
	
	/** The collidable; null if this node is not a leaf node */
	protected E collidable;
	
	protected T fixture;
	
	/** The aabb containing all children */
	protected AABB aabb;
	
	/** Flag used to determine if a node has been tested before */
	protected boolean tested = false;
	
	/**
	 * Returns true if this node is a leaf node.
	 * @return boolean true if this node is a leaf node
	 */
	public boolean isLeaf() {
		return left == null;
	}
}