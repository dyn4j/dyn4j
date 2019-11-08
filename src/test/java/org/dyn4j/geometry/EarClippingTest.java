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
package org.dyn4j.geometry;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.geometry.decompose.EarClipping;
import org.junit.Test;

/**
 * Test case for the {@link EarClipping} class.
 * @author William Bittle
 * @version 3.3.1
 * @since 2.2.0
 */
public class EarClippingTest extends AbstractDecomposeTest {
	/** The ear clipping algorithm */
	private EarClipping algo = new EarClipping();
	
	/**
	 * Tests passing a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void nullArray() {
		this.algo.decompose((Vector2[])null);
	}
	
	/**
	 * Tests passing an array of vertices with less than 4 elements.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void lessThan4Vertices() {
		Vector2[] vertices = new Vector2[3];
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests passing an array of vertices that contains a null vertex.
	 */
	@Test(expected = NullPointerException.class)
	public void nullVertex() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 2.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = null;
		vertices[3] = new Vector2(-1.0, 0.5);
		vertices[4] = new Vector2(0.5, -1.0);
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests passing an array of vertices that contains two vertices that
	 * are coincident.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void coincidentVertex() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 2.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = new Vector2(-1.0, 2.0);
		vertices[3] = new Vector2(-1.0, 0.5);
		vertices[4] = new Vector2(0.5, -1.0);
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests the implementation against a 10 vertex
	 * non-convex polygon.
	 */
	@Test
	public void success1() {
		Vector2[] vertices = new Vector2[10];
		vertices[0] = new Vector2(2.0, 0.5);
		vertices[1] = new Vector2(1.0, 1.0);
		vertices[2] = new Vector2(-0.25, 0.25);
		vertices[3] = new Vector2(-0.75, 1.5);
		vertices[4] = new Vector2(-1.0, 2.0);
		vertices[5] = new Vector2(-1.0, 0.0);
		vertices[6] = new Vector2(-0.5, -0.75);
		vertices[7] = new Vector2(0.25, -0.4);
		vertices[8] = new Vector2(1.0, 0.3);
		vertices[9] = new Vector2(0.25, -0.5);
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against a 10 vertex
	 * non-convex polygon.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccess1() {
		Vector2[] vertices = new Vector2[10];
		vertices[0] = new Vector2(2.0, 0.5);
		vertices[1] = new Vector2(1.0, 1.0);
		vertices[2] = new Vector2(-0.25, 0.25);
		vertices[3] = new Vector2(-0.75, 1.5);
		vertices[4] = new Vector2(-1.0, 2.0);
		vertices[5] = new Vector2(-1.0, 0.0);
		vertices[6] = new Vector2(-0.5, -0.75);
		vertices[7] = new Vector2(0.25, -0.4);
		vertices[8] = new Vector2(1.0, 0.3);
		vertices[9] = new Vector2(0.25, -0.5);
		
		List<Triangle> triangulation = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, triangulation.size());
	}
	
	/**
	 * Tests the implementation against the 1st polygon data file.
	 */
	@Test
	public void success2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon1.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the 1st polygon data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccess2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon1.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the 2nd polygon data file.
	 */
	@Test
	public void success3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon2.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the 2nd polygon data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccess3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon2.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the 3rd polygon data file.
	 */
	@Test
	public void success4() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon3.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the 3rd polygon data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccess4() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon3.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the 4th polygon data file.
	 */
	@Test
	public void success5() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon4.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the 4th polygon data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccess5() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/polygon4.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the bird data file.
	 */
	@Test
	public void successBird() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/bird.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the bird data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessBird() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/bird.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the tank data file.
	 */
	@Test
	public void successTank() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tank.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the tank data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessTank() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tank.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the nazca_monkey data file.
	 */
	@Test
	public void successNazcaMonkey() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/nazca_monkey.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the nazca_monkey data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessNazcaMonkey() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/nazca_monkey.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the nazca_heron data file.
	 */
	@Test
	public void successNazcaHeron() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/nazca_heron.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the nazca_monkey data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessNazcaHeron() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/nazca_heron.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom(forum) data file 1.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom1() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom1.dat"));
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}

	/**
	 * Tests the triangulation implementation against the zoom1 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom1() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom1.dat"));
		
		// decompose the poly
		List<Triangle> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the triangulation implementation against the zoom1 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom2.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom1 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom2.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom3 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom3.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom3 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom3.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom4 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom4() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom4.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom4 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom4() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom4.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom5 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom5() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom5.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom5 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom5() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom5.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom6 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom6() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom6.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom6 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom6() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom6.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the zoom7 data file.
	 * @since 3.1.9
	 */
	@Test
	public void successZoom7() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom7.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the zoom7 data file.
	 * @since 3.1.9
	 */
	@Test
	public void triangulateSuccessZoom7() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/zoom7.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}

	/**
	 * Tests the implementation against the tridol1 data file.
	 * @since 3.1.10
	 */
	@Test
	public void successTridol1() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol1.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the tridol1 data file.
	 * @since 3.1.10
	 */
	@Test
	public void triangulateSuccessTridol1() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol1.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the tridol2 data file.
	 * @since 3.1.10
	 */
	@Test
	public void successTridol2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol2.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the tridol2 data file.
	 * @since 3.1.10
	 */
	@Test
	public void triangulateSuccessTridol2() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol2.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
	
	/**
	 * Tests the implementation against the tridol2 data file.
	 * @since 3.1.10
	 */
	@Test
	public void successTridol3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol3.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.decompose(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertTrue(result.size() <= vertices.length - 2);
	}
	
	/**
	 * Tests the triangulation implementation against the tridol2 data file.
	 * @since 3.1.10
	 */
	@Test
	public void triangulateSuccessTridol3() {
		Vector2[] vertices = this.load(EarClippingTest.class.getResourceAsStream("/org/dyn4j/data/tridol3.dat"));
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}

	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection1() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(-0.07792188619765694, 0.10364292899125216),
			new Vector2(0.1, -0.2),
			new Vector2(0.15, 0.0),
			new Vector2(0.2, 0.07),
			new Vector2(0.21037640391727175, 0.06289919008100842),
			new Vector2(0.3079072605141815, -0.20863138522549773)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
	
	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection2() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(-0.07792188619765694, 0.10364292899125216),
			new Vector2(0.2412466770151972, -0.3145214553981004),
			new Vector2(0.21037640391727175, 0.06289919008100842),
			new Vector2(0.3079072605141815, -0.20863138522549773)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
	
	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection3() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(-0.07792188619765694, 0.10364292899125216),
			new Vector2(0.1, -0.2),
			new Vector2(0.2412466770151972, -0.3145214553981004),
			new Vector2(0.21037640391727175, 0.06289919008100842),
			new Vector2(0.3079072605141815, -0.20863138522549773)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
	
	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection4() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(-0.22574647794211955, 0.3562272754868271),
			new Vector2(-0.24724056392833493, -0.06552204150010887),
			new Vector2(0.2551995234048088, -0.4678431592201415),
			new Vector2(-0.11272047497863902, -0.40936273068655504)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}

	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection5() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(0.187521000630546, -0.2171227524343904),
			new Vector2(-0.05418163781638374, -0.4552384293706746),
			new Vector2(-0.12615265827683775, 0.08842525905551823),
			new Vector2(-0.4197343412893181, -0.45293439849558936)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
	
	/**
	 * Tests the triangulation to confirm it fails properly on self-intersecting edges.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailSelfIntersection6() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(0.1595990921676319, 0.20158036631684495),
			new Vector2(0.3627243978540108, -0.2125801642934565),
			new Vector2(0.4972213824759445, -0.2197501458724339),
			new Vector2(-0.17530050402164232, -0.10202036313267437)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
	
	/**
	 * Tests the triangulation to confirm it fails properly on degenerate data.
	 * @since 3.3.1
	 */
	@Test(expected = IllegalArgumentException.class)
	public void triangulateFailureDegenerateGusAsf() {
		// degenerate ploygon
		Vector2[] vertices = new Vector2[] {
				new Vector2(70.5, 360.0),
				new Vector2(70.947212,360.89444),
				new Vector2(71.394424,361.78884899999997),
				new Vector2(71.158356,361.316711),
				new Vector2(70.71114299999999,360.422302)
		};
		
		// decompose the poly
		this.algo.triangulate(vertices);
	}
}
