/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.Transform;

/**
 * Represents a leaf node in a {@link LazyAABBTree}.
 * <p>
 * The leaf nodes in a {@link LazyAABBTree} are the nodes that contain the {@link Fixture} AABBs.
 * 
 * @author Manolis Tsamis
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @version 3.4.0
 * @since 3.4.0
 * @deprecated Deprecated in 4.0.0.
 */
@Deprecated
final class LazyAABBTreeLeaf<T extends CollisionBody<E>, E extends Fixture> extends LazyAABBTreeNode {
	/** The {@link CollisionBody} */
	T body;
	
	/** The {@link Fixture} */
	E fixture;
	
	/** Flag storing whether this leaf is in the tree currently */
	private boolean onTree = false;
	
	/** Mark for removal flag */
	private boolean removed = false;
	
	/**
	 * Minimal constructor.
	 * @param body the body
	 * @param fixture the fixture
	 */
	public LazyAABBTreeLeaf(T body, E fixture) {
		this.body = body;
		this.fixture = fixture;
		
		// calculate the initial AABB
		this.updateAABB();
	}
	
	/**
	 * Updates the AABB of this leaf
	 */
	public void updateAABB() {
		Transform transform = body.getTransform();
		this.aabb = fixture.getShape().createAABB(transform);
	}
	
	/**
	 * @return true if this leaf has been marked for removal, false otherwise
	 */
	public boolean mustRemove() {
		return this.removed;
	}
	
	/**
	 * Marks that this leaf must be removed
	 */
	public void markForRemoval() {
		this.removed = true;
	}
	
	/**
	 * Change the flag denoting if this leaf is on the tree or not
	 * @param onTree the new flag value
	 */
	public void setOnTree(boolean onTree) {
		this.onTree = onTree;
		
		if (!onTree) {
			// Clear possible leftovers
			this.left = null;
			this.right = null;
			this.parent = null;
		}
	}
	
	/**
	 * @return true if this leaf is on the tree, false otherwise
	 */
	public boolean isOnTree() {
		return this.onTree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof LazyAABBTreeLeaf) {
			LazyAABBTreeLeaf<?, ?> leaf = (LazyAABBTreeLeaf<?, ?>)obj;
			if (leaf.body == this.body &&
				leaf.fixture == this.fixture) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.body.hashCode();
		hash = hash * 31 + this.fixture.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LazyAABBTreeLeaf[Body=").append(this.body.hashCode())
		  .append("|Fixture=").append(this.fixture.hashCode())
		  .append("|AABB=").append(this.aabb.toString())
		  .append("|OnTree=").append(this.onTree)
		  .append("]");
		return sb.toString();
	}
}