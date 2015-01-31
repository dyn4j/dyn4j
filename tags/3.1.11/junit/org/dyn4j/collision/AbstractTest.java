/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import java.util.List;

import org.dyn4j.collision.broadphase.AbstractAABBDetector;
import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.SapBruteForce;
import org.dyn4j.collision.broadphase.SapIncremental;
import org.dyn4j.collision.broadphase.SapTree;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract test for all shape - shape test classes.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 */
public abstract class AbstractTest {
	/** Abstract AABB detector */
	protected AbstractAABBDetector<CollidableTest> aabb = new AbstractAABBDetector<CollidableTest>() {
		@Override
		public void add(CollidableTest collidable) {}
		@Override
		public List<BroadphasePair<CollidableTest>> detect() { return null;	}
		@Override
		public void remove(CollidableTest collidable) {}
		@Override
		public void update(CollidableTest collidable) {}
		@Override
		public List<CollidableTest> detect(AABB aabb) { return null; }
		@Override
		public void clear() {}
		@Override
		public AABB getAABB(CollidableTest collidable) { return null; }
		@Override
		public List<CollidableTest> raycast(Ray ray, double length) { return null; }
		@Override
		public void shiftCoordinates(Vector2 shift) {}
	};
	
	/** The SAT algorithm */
	protected Sat sat = new Sat();
	
	/** The GJK/EPA algorithm */
	protected Gjk gjk = new Gjk();
	
	/** The sap incremental (using a list) algorithm */
	protected SapIncremental<CollidableTest> sapI = new SapIncremental<CollidableTest>();
	
	/** The sap brute force algorithm */
	protected SapBruteForce<CollidableTest> sapBF = new SapBruteForce<CollidableTest>();
	
	/** The sap incremental (using a tree) algorithm */
	protected SapTree<CollidableTest> sapT = new SapTree<CollidableTest>();
	
	/** The dynamic aabb algorithm */
	protected DynamicAABBTree<CollidableTest> dynT = new DynamicAABBTree<CollidableTest>();
	
	/** The clipping manifold algorithm */
	protected ClippingManifoldSolver cmfs = new ClippingManifoldSolver();
}
