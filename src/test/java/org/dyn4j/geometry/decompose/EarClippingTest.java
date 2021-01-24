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
package org.dyn4j.geometry.decompose;

import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link EarClipping} class.
 * @author William Bittle
 * @version 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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
	 * @since 3.4.0
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

	/**
	 * Tests the triangulation with a sample provided by nsoft.
	 * @since 3.4.0
	 */
	@Test
	public void nsoftTriangulateFailure() {
		// degenerate ploygon
		Vector2[] vertices = new Vector2[] {
			new Vector2(9.761856, 2.894968),
			new Vector2(9.814371999999999, 2.941328),
			new Vector2(9.879956, 2.9670609999999997),
			new Vector2(9.950168999999999, 2.975665),
			new Vector2(10.020947, 2.972896),
			new Vector2(10.090999, 2.962284),
			new Vector2(10.160001, 2.946294),
			new Vector2(10.224352, 2.917453),
			new Vector2(10.287756, 2.8861149999999998),
			new Vector2(10.354289, 2.882955),
			new Vector2(10.42306, 2.89995),
			new Vector2(10.491831, 2.916946),
			new Vector2(10.560603, 2.933941),
			new Vector2(10.629374, 2.9509369999999997),
			new Vector2(10.698146, 2.967932),
			new Vector2(10.766917, 2.984928),
			new Vector2(10.835689, 3.0019229999999997),
			new Vector2(10.902652999999999, 3.023749),
			new Vector2(10.966056, 3.055087),
			new Vector2(11.02946, 3.0864249999999998),
			new Vector2(11.092863999999999, 3.1177639999999998),
			new Vector2(11.156269, 3.149102),
			new Vector2(11.219673, 3.18044),
			new Vector2(11.283076999999999, 3.211778),
			new Vector2(11.346480999999999, 3.2431159999999997),
			new Vector2(11.409885, 3.274454),
			new Vector2(11.470938, 3.309624),
			new Vector2(11.528354, 3.350723),
			new Vector2(11.585768999999999, 3.391823),
			new Vector2(11.643184, 3.432922),
			new Vector2(11.700598999999999, 3.474021),
			new Vector2(11.758014, 3.51512),
			new Vector2(11.815429, 3.5562199999999997),
			new Vector2(11.872845, 3.5973189999999997),
			new Vector2(11.930259999999999, 3.6384179999999997),
			new Vector2(11.987675, 3.6795169999999997),
			new Vector2(12.04509, 3.720617),
			new Vector2(12.102504999999999, 3.761716),
			new Vector2(12.159920999999999, 3.802815),
			new Vector2(12.217335, 3.843914),
			new Vector2(12.27475, 3.885014),
			new Vector2(12.332165999999999, 3.926113),
			new Vector2(12.389581, 3.967212),
			new Vector2(12.446995, 4.008311),
			new Vector2(12.495809999999999, 4.057666),
			new Vector2(12.53567, 4.115612),
			new Vector2(12.575531999999999, 4.173558),
			new Vector2(12.615393, 4.231504999999999),
			new Vector2(12.655254, 4.289451),
			new Vector2(12.695115, 4.347397),
			new Vector2(12.734976, 4.405343),
			new Vector2(12.774837, 4.46329),
			new Vector2(12.814696999999999, 4.521236),
			new Vector2(12.854559, 4.579181999999999),
			new Vector2(12.894419, 4.637129),
			new Vector2(12.934280999999999, 4.695075),
			new Vector2(12.974141, 4.7530209999999995),
			new Vector2(13.014002999999999, 4.810967),
			new Vector2(13.053863999999999, 4.868914),
			new Vector2(13.10245, 4.9170609999999995),
			new Vector2(13.159697, 4.9580649999999995),
			new Vector2(13.204635, 5.0120059999999995),
			new Vector2(13.235349999999999, 5.074993999999999),
			new Vector2(13.253463, 5.142634),
			new Vector2(13.26161, 5.212177),
			new Vector2(13.262213, 5.282213),
			new Vector2(13.256893, 5.352073),
			new Vector2(13.281281, 5.416812999999999),
			new Vector2(13.310070999999999, 5.480848),
			new Vector2(13.338861999999999, 5.544881999999999),
			new Vector2(13.367652, 5.608917),
			new Vector2(13.396441999999999, 5.672950999999999),
			new Vector2(13.425232999999999, 5.736986),
			new Vector2(13.454023999999999, 5.8010209999999995),
			new Vector2(13.482813, 5.865056),
			new Vector2(13.511604, 5.929091),
			new Vector2(13.540618, 5.993009),
			new Vector2(13.575342, 6.054065),
			new Vector2(13.605383, 6.117508),
			new Vector2(13.630118999999999, 6.183152),
			new Vector2(13.650364999999999, 6.250286),
			new Vector2(13.666599999999999, 6.3184819999999995),
			new Vector2(13.678803, 6.387498),
			new Vector2(13.687422999999999, 6.457040999999999),
			new Vector2(13.692998, 6.526886999999999),
			new Vector2(13.695791, 6.5968979999999995),
			new Vector2(13.696048, 6.666963),
			new Vector2(13.693999999999999, 6.737000999999999),
			new Vector2(13.689857, 6.8069489999999995),
			new Vector2(13.683812, 6.8767629999999995),
			new Vector2(13.672563, 6.945861),
			new Vector2(13.658662, 7.014575),
			new Vector2(13.644758999999999, 7.083288),
			new Vector2(13.630856, 7.1520019999999995),
			new Vector2(13.616954999999999, 7.2207159999999995),
			new Vector2(13.5985, 7.288234),
			new Vector2(13.576082999999999, 7.354711999999999),
			new Vector2(13.553666999999999, 7.421189999999999),
			new Vector2(13.53125, 7.4876689999999995),
			new Vector2(13.508833, 7.5541469999999995),
			new Vector2(13.47304, 7.614159),
			new Vector2(13.433719, 7.672464),
			new Vector2(13.394397, 7.730769),
			new Vector2(13.355075999999999, 7.789073999999999),
			new Vector2(13.315754, 7.847379999999999),
			new Vector2(13.276432999999999, 7.905685),
			new Vector2(13.237110999999999, 7.963991),
			new Vector2(13.193520999999999, 8.019065),
			new Vector2(13.146841, 8.071802),
			new Vector2(13.100159999999999, 8.124537),
			new Vector2(13.053479999999999, 8.177273999999999),
			new Vector2(13.006801, 8.23001),
			new Vector2(12.960121, 8.282746999999999),
			new Vector2(12.91344, 8.335483),
			new Vector2(12.86676, 8.388219),
			new Vector2(12.820081, 8.440954999999999),
			new Vector2(12.773401, 8.493692),
			new Vector2(12.72672, 8.546427999999999),
			new Vector2(12.660881, 8.571221),
			new Vector2(12.594036, 8.594543),
			new Vector2(12.527189, 8.617865),
			new Vector2(12.460341999999999, 8.641187),
			new Vector2(12.393495, 8.66451),
			new Vector2(12.326647999999999, 8.687832),
			new Vector2(12.259801, 8.711153999999999),
			new Vector2(12.192955999999999, 8.734475999999999),
			new Vector2(12.126109, 8.757798),
			new Vector2(12.059261999999999, 8.781120999999999),
			new Vector2(11.992415, 8.804442),
			new Vector2(11.925568, 8.827764),
			new Vector2(11.858723, 8.851087),
			new Vector2(11.791876, 8.874409),
			new Vector2(11.725029, 8.897731),
			new Vector2(11.658182, 8.921052999999999),
			new Vector2(11.591334999999999, 8.944374999999999),
			new Vector2(11.524488999999999, 8.967698),
			new Vector2(11.454464999999999, 8.977869),
			new Vector2(11.384172, 8.986932999999999),
			new Vector2(11.31388, 8.995996),
			new Vector2(11.243587, 9.005059),
			new Vector2(11.173295, 9.014123),
			new Vector2(11.103002, 9.023185999999999),
			new Vector2(11.03271, 9.032249),
			new Vector2(10.962417, 9.041312999999999),
			new Vector2(10.892123999999999, 9.050376),
			new Vector2(10.821831999999999, 9.05944),
			new Vector2(10.751539, 9.068503),
			new Vector2(10.681084, 9.075819),
			new Vector2(10.610294999999999, 9.079500999999999),
			new Vector2(10.539505, 9.083185),
			new Vector2(10.468715, 9.086867),
			new Vector2(10.397924999999999, 9.090551),
			new Vector2(10.327135, 9.094233),
			new Vector2(10.256345, 9.097916999999999),
			new Vector2(10.185554999999999, 9.1016),
			new Vector2(10.114765, 9.105283)
		};
		
		// decompose the poly
		List<? extends Convex> result = this.algo.triangulate(vertices);
		
		// the result should have n - 2 triangles shapes
		TestCase.assertEquals(vertices.length - 2, result.size());
	}
}
