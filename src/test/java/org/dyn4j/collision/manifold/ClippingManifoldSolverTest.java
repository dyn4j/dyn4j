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
package org.dyn4j.collision.manifold;

import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Transform;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link ManifoldPointId} interface.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class ClippingManifoldSolverTest {
	/**
	 * Rectangle/Circle - single point manifold
	 */
	@Test
	public void scenario1() {
		NarrowphaseDetector detector = new Gjk();
		ClippingManifoldSolver solver = new ClippingManifoldSolver();
		
		Convex c1 = Geometry.createRectangle(1.0, 0.5);
		Convex c2 = Geometry.createCircle(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(0.5, 0.5);
		
		Penetration penetration = new Penetration();
		detector.detect(c1, tx1, c2, tx2, penetration);
		
		Manifold manifold = new Manifold();
		boolean isManifoldCollision = solver.getManifold(penetration, c1, tx1, c2, tx2, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(-1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(1, manifold.getPoints().size());
		TestCase.assertEquals(0.250, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(0.500, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(0.000, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		
		// try the reverse order
		penetration.clear();
		detector.detect(c2, tx2, c1, tx1, penetration);
		
		manifold.clear();
		isManifoldCollision = solver.getManifold(penetration, c2, tx2, c1, tx1, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(1, manifold.getPoints().size());
		TestCase.assertEquals(0.250, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(0.500, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(0.000, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
	}
	
	/**
	 * Rectangle/Square - two point manifold
	 */
	@Test
	public void scenario2() {
		NarrowphaseDetector detector = new Gjk();
		ClippingManifoldSolver solver = new ClippingManifoldSolver();
		
		Convex c1 = Geometry.createRectangle(1.0, 0.5);
		Convex c2 = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(-0.5, -0.35);
		
		Penetration penetration = new Penetration();
		detector.detect(c1, tx1, c2, tx2, penetration);
		
		Manifold manifold = new Manifold();
		boolean isManifoldCollision = solver.getManifold(penetration, c1, tx1, c2, tx2, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.150, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.100, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.150, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.100, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
		
		// try the reverse order
		penetration.clear();
		detector.detect(c2, tx2, c1, tx1, penetration);
		
		manifold.clear();
		isManifoldCollision = solver.getManifold(penetration, c2, tx2, c1, tx1, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(-1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.150, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.150, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
	}
	
	/**
	 * Rectangle/Square - two point manifold - part 2 (slightly rotated)
	 */
	@Test
	public void scenario3() {
		NarrowphaseDetector detector = new Gjk();
		ClippingManifoldSolver solver = new ClippingManifoldSolver();
		
		Convex c1 = Geometry.createRectangle(1.0, 0.5);
		Convex c2 = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.rotate(Math.toRadians(10.0));
		tx2.translate(-0.5, -0.5);
		
		Penetration penetration = new Penetration();
		detector.detect(c1, tx1, c2, tx2, penetration);
		
		Manifold manifold = new Manifold();
		boolean isManifoldCollision = solver.getManifold(penetration, c1, tx1, c2, tx2, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.039, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.297, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.210, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.003, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.246, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
		
		// try the reverse order
		penetration.clear();
		detector.detect(c2, tx2, c1, tx1, penetration);
		
		manifold.clear();
		isManifoldCollision = solver.getManifold(penetration, c2, tx2, c1, tx1, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(-1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.039, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.297, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.210, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.003, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.246, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
	}
	
	/**
	 * Rectangle/Square - two point manifold - part 3 (significantly rotated)
	 */
	@Test
	public void scenario4() {
		NarrowphaseDetector detector = new Gjk();
		ClippingManifoldSolver solver = new ClippingManifoldSolver();
		
		Convex c1 = Geometry.createRectangle(1.0, 0.5);
		Convex c2 = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.rotate(Math.toRadians(60.0));
		tx2.translate(-0.5, -0.5);
		
		Penetration penetration = new Penetration();
		detector.detect(c1, tx1, c2, tx2, penetration);
		
		Manifold manifold = new Manifold();
		boolean isManifoldCollision = solver.getManifold(penetration, c1, tx1, c2, tx2, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.500, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(0.866, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(1, manifold.getPoints().size());
		TestCase.assertEquals(0.033, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		
		// try the reverse order
		penetration.clear();
		detector.detect(c2, tx2, c1, tx1, penetration);
		
		manifold.clear();
		isManifoldCollision = solver.getManifold(penetration, c2, tx2, c1, tx1, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(-0.500, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(-0.866, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(1, manifold.getPoints().size());
		TestCase.assertEquals(0.033, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.500, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
	}
	
	/**
	 * Rectangle/Square - two point manifold
	 */
	@Test
	public void scenario5() {
		NarrowphaseDetector detector = new Gjk();
		ClippingManifoldSolver solver = new ClippingManifoldSolver();
		
		Convex c1 = Geometry.createRectangle(1.0, 0.5);
		Convex c2 = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(0.0, -0.35);
		
		Penetration penetration = new Penetration();
		detector.detect(c1, tx1, c2, tx2, penetration);
		
		Manifold manifold = new Manifold();
		boolean isManifoldCollision = solver.getManifold(penetration, c1, tx1, c2, tx2, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.150, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(0.250, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.100, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.150, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.100, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
		
		// try the reverse order
		penetration.clear();
		detector.detect(c2, tx2, c1, tx1, penetration);
		
		manifold.clear();
		isManifoldCollision = solver.getManifold(penetration, c2, tx2, c1, tx1, manifold);
		
		TestCase.assertTrue(isManifoldCollision);
		TestCase.assertEquals(0.000, manifold.getNormal().x, 1.0e-3);
		TestCase.assertEquals(-1.000, manifold.getNormal().y, 1.0e-3);
		TestCase.assertEquals(2, manifold.getPoints().size());
		TestCase.assertEquals(0.150, manifold.getPoints().get(0).getDepth(), 1.0e-3);
		TestCase.assertEquals(0.250, manifold.getPoints().get(0).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(0).getPoint().y, 1.0e-3);
		TestCase.assertEquals(0.150, manifold.getPoints().get(1).getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(1).getPoint().x, 1.0e-3);
		TestCase.assertEquals(-0.250, manifold.getPoints().get(1).getPoint().y, 1.0e-3);
	}
}
