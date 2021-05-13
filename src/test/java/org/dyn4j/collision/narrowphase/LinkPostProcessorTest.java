/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link LinkPostProcessor} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.1.0
 */
public class LinkPostProcessorTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		new LinkPostProcessor();
	}
	
	/**
	 * Tests the type detection when the pair should be ignored.
	 */
	@Test
	public void typeDetectionIgnore() {
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createCircle(1.0);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
		p.setDepth(2.0);
		p.setNormal(new Vector2(-1.0, 2.0));
		
		lpp.process(c1, tx1, c2, tx2, p);
		
		// confirm nothing is modified
		
		TestCase.assertEquals(2.0, p.getDepth());
		TestCase.assertEquals(-1.0, p.getNormal().x);
		TestCase.assertEquals(2.0, p.getNormal().y);
	}
	
	/**
	 * Tests the type detection when the pair should be processed.
	 */
	//@Test
	public void convexConcave() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		//             o
		//            /
		//   o---0---o
		//  /
		// o
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2(-2.0, -1.0));
		vertices.add(new Vector2(-1.0, 0.0));
		vertices.add(new Vector2(1.0, 0.0));
		vertices.add(new Vector2(2.0, 1.0));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex c1 = links.get(1);
		Convex c2 = Geometry.createSquare(0.5);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
		tx2.translate(-1.25, 0.1);
		
		gjk.detect(c1, tx1, c2, tx2, p);
		lpp.process(c1, tx1, c2, tx2, p);
		
		// confirm that it was modified
		
		TestCase.assertEquals(0.25, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().y, 1e-3);
		
		p.setDepth(0.25);
		// NOTE: normal should be from a->b
		p.setNormal(new Vector2(1.0, 0.0));
		lpp.process(c2, tx2, c1, tx1, p);
		
		// confirm that it was modified
		// NOTE: normal should be from a->b
		
		TestCase.assertEquals(0.25, p.getDepth(), 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Tests the type detection when the pair should be processed.
	 */
	//@Test
	public void concaveConvex() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// o
		//  \
		//   o---0---o
		//            \
		//             o
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2(-2.0, 1.0));
		vertices.add(new Vector2(-1.0, 0.0));
		vertices.add(new Vector2(1.0, 0.0));
		vertices.add(new Vector2(2.0, -1.0));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex c1 = links.get(1);
		Convex c2 = Geometry.createSquare(0.5);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
		tx2.translate(1.20, 0.1);
		
		gjk.detect(c1, tx1, c2, tx2, p);
		lpp.process(c1, tx1, c2, tx2, p);
		
		// confirm that it was modified
		
		TestCase.assertEquals(0.25, p.getDepth(), 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().y, 1e-3);
		
		p.setDepth(0.25);
		// NOTE: normal should be from a->b
		p.setNormal(new Vector2(-1.0, 0.0));
		lpp.process(c2, tx2, c1, tx1, p);
		
		// confirm that it was modified
		// NOTE: normal should be from a->b
		
		TestCase.assertEquals(0.25, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Tests the modification of the normal based on a specific
	 * failure scenario with the two-way Link interaction.
	 * <p>
	 * This issue was fixed in 4.2.0 by forcing Links to be
	 * one-way.
	 */
	@Test
	public void failureWidthTwoWayLinksInVersionsPriorTo_4_2_0() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// Tests when the center of the other object is on the wrong
		// side of the link shape, so it chooses the wrong normal
		//   o
		//   |
		//   o---0---o---o
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2(5.0, 0.0));
		vertices.add(new Vector2(4.0, 0.0));
		vertices.add(new Vector2(0.0, 0.0));
		vertices.add(new Vector2(0.0, 0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(1);
		Convex rect = Geometry.createRectangle(0.1, 2.0);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.rotate(Math.toRadians(30));
 		tx2.translate(-0.1, 0.85);
		
		gjk.detect(link, tx1, rect, tx2, p);
		lpp.process(link, tx1, rect, tx2, p);
		
		TestCase.assertEquals( 0.041, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);
		
		// reverse the order of the shapes
		
		gjk.detect(rect, tx2, link, tx1, p);
		lpp.process(rect, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.041, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
	}

	/**
	 * Collision with middle segment convex-convex.
	 */
	@Test
	public void caseMiddleConvexConvex() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();

		// middle segment convex-convex
		//   0----0
		//  /++++++\
		// 0++++++++0
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, -0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, -0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(1);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// try the other side
		
		tx2.identity();
		tx2.translate(-0.749, 0.0);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// try it lower to get a different normal
		
		tx2.identity();
		tx2.translate(0.73, -0.24);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Collision with middle segment concave-concave.
	 */
	@Test
	public void caseMiddleConcaveConcave() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// middle segment concave-concave
		// 0        0
		// +\      /+
		// ++0----0++
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, 0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, 0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(1);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		// try the other side
		
		tx2.identity();
		tx2.translate(0.749, 0.0);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);

		// try it lower to get a different normal
		
		tx2.identity();
		tx2.translate(0.73, -0.24);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// try the other side
		
		tx2.identity();
		tx2.rotate(Math.toRadians(45));
		tx2.translate(-0.60, -0.20);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.037, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Collision with middle segment convex-concave.
	 */
	@Test
	public void caseMiddleConvexConcave() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// middle segment convex-concave
		// 0
		// +\
		// ++0----0
		// ++++++++\
		// +++++++++0
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, -0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, 0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(1);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		// try the other side
		
		tx2.identity();
		tx2.translate(0.749, 0.0);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// try it lower to get a different normal
		
		tx2.identity();
		tx2.translate(0.73, -0.24);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}

	/**
	 * Collision with middle segment concave-convex.
	 */
	@Test
	public void caseMiddleConcaveConvex() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// middle segment concave-convex
		//          0
		//         /+
		//   0----0++
		//  /++++++++
		// 0+++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, 0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, -0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(1);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// try the other side
		
		tx2.identity();
		tx2.translate(0.749, 0.0);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		// try it lower to get a different normal
		
		tx2.identity();
		tx2.translate(-0.73, -0.24);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		// should be skipped
		TestCase.assertEquals( 0.000, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Collision with right concave.
	 */
	@Test
	public void caseRightConcave() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// 0        0
		// +\      /+
		// ++0----0++
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, 0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, 0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(0);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(0.26, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// confirm that it was modified
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
		
		// try to get a different normal
		
		tx2.identity();
		tx2.translate(1.24, 0.5);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// confirm that it was left alone since there's no
		// previous segment
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}

	/**
	 * Collision with left concave.
	 */
	@Test
	public void caseLeftConcave() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();
		
		// 0        0
		// +\      /+
		// ++0----0++
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, 0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, 0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(2);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.26, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals(0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// confirm that it was modified
		
		TestCase.assertEquals(0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(0.707, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
		
		// try to get a different normal
		
		tx2.identity();
		tx2.translate(-1.24, 0.5);
		
		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// confirm that it was left alone since there's no
		// previous segment
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Collision with right convex.
	 */
	@Test
	public void caseRightConvex() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();

		//   0----0
		//  /++++++\
		// 0++++++++0
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, -0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, -0.5));
		List<Link> links = Geometry.createLinks(vertices, false);

		Convex link = links.get(0);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().y, 1e-3);

		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);
		
		// try at the bottom
		
		tx2.identity();
 		tx2.translate(1.23, -0.74);
		
 		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// nothing should change because the previous segment
		// doesn't exist
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);
		
		// try at the top
		
		tx2.identity();
 		tx2.translate(0.73, 0.24);
		
 		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// nothing should change because the previous segment
		// doesn't exist
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
	}

	/**
	 * Collision with left convex.
	 */
	@Test
	public void caseLeftConvex() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();

		//   0----0
		//  /++++++\
		// 0++++++++0
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 1.0, -0.5));
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		vertices.add(new Vector2(-1.0, -0.5));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(2);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().y, 1e-3);
		
		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.177, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.707, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-0.707, p.getNormal().y, 1e-3);

		// try at the bottom
		
		tx2.identity();
 		tx2.translate(-1.23, -0.74);
		
 		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// nothing should change because the previous segment
		// doesn't exist
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);
		
		// try at the top
		
		tx2.identity();
 		tx2.translate(-0.73, 0.24);
		
 		p.clear();
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// nothing should change because the previous segment
		// doesn't exist
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 1.000, p.getNormal().y, 1e-3);

		// reverse the shape order
		p.clear();
		gjk.detect(square, tx2, link, tx1, p);
		lpp.process(square, tx2, link, tx1, p);
		
		TestCase.assertEquals( 0.010, p.getDepth(), 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().y, 1e-3);
	}
	
	/**
	 * Tests with a link with no previous or next segments.
	 */
	@Test
	public void noPreviousOrNext() {
		Gjk gjk = new Gjk();
		LinkPostProcessor lpp = new LinkPostProcessor();

		//   0----0
		//  /++++++\
		// 0++++++++0
		// ++++++++++
		
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2( 0.5, 0.0));
		vertices.add(new Vector2(-0.5, 0.0));
		List<Link> links = Geometry.createLinks(vertices, false);
		
		Convex link = links.get(0);
		Convex square = Geometry.createSquare(0.5);
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		Penetration p = new Penetration();
		
 		tx2.translate(-0.749, 0.0);
		
		gjk.detect(link, tx1, square, tx2, p);
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
		
		lpp.process(link, tx1, square, tx2, p);
		
		// should be left unchanged
		
		TestCase.assertEquals( 0.001, p.getDepth(), 1e-3);
		TestCase.assertEquals(-1.000, p.getNormal().x, 1e-3);
		TestCase.assertEquals( 0.000, p.getNormal().y, 1e-3);
	}
}
