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
package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link DouglasPeucker} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class VertexClusterReductionTest extends AbstractSimplifyTest {
	/**
	 * Tests no change due to configuration values.
	 */
	@Test
	public void noChange() {
		Simplifier simplifier = new VertexClusterReduction(0);
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/bird.dat"));
		Vector2[] simplified = simplifier.simplify(vertices);
		
		TestCase.assertEquals(vertices.length, simplified.length);
	}
	
	/**
	 * Tests passing a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void nullArray() {
		Simplifier simplifier = new VertexClusterReduction(0);
		simplifier.simplify((Vector2[])null);
	}
	
	/**
	 * Tests passing an empty array.
	 */
	@Test
	public void emptyArray() {
		Vector2[] vertices = new Vector2[0];
		Simplifier simplifier = new VertexClusterReduction(0);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(0, vertices.length);
	}
	
	/**
	 * Tests passing null elements
	 */
	@Test
	public void nullElements() {
		Vector2[] vertices = new Vector2[] {
			null,
			new Vector2(),
			null
		};
		Simplifier simplifier = new VertexClusterReduction(0);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(1, vertices.length);
	}
	
	/**
	 * Tests passing identical elements.
	 */
	@Test
	public void coincidentElements() {
		Vector2[] vertices = new Vector2[] {
			null,
			new Vector2(),
			new Vector2(),
			new Vector2(1, 1),
			null
		};
		Simplifier simplifier = new VertexClusterReduction(0);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(2, vertices.length);
	}
	
	/**
	 * Tests passing close elements.
	 */
	@Test
	public void closeElements() {
		Vector2[] vertices = new Vector2[] {
			null,
			new Vector2(1.1, 0.0),
			new Vector2(1.1, 0.0),
			new Vector2(1.2, 0.0),
			new Vector2(1.25, 0.0),
			new Vector2(1.4, 0.0),
			new Vector2(1.4, 0.0),
			new Vector2(1.7, 0.0),
			null
		};
		Simplifier simplifier = new VertexClusterReduction(0.11);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(4, vertices.length);
	}
	
	/**
	 * Tests with the bird dataset.
	 */
	@Test
	public void successBird() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/bird.dat"));
		Simplifier simplifier = new VertexClusterReduction(1.0);
		vertices = simplifier.simplify(vertices);
		// 68% reduction
		TestCase.assertEquals(88, vertices.length);
	}
	
	/**
	 * Tests with the tank dataset.
	 */
	@Test
	public void successTank() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/tank.dat"));
		Simplifier simplifier = new VertexClusterReduction(10.0);
		vertices = simplifier.simplify(vertices);
		// the original shape is so optimized we can't get much more out of it
		TestCase.assertEquals(41, vertices.length);
	}
	
	/**
	 * Tests with a dataset that would produce self-intersection if there wasn't a prevention measure in place.
	 */
	@Test
	public void successSelfIntersection() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(-2.058,-3.576),
				new Vector2(1.066,-3.422),
				new Vector2(0.626,-1.816),
				new Vector2(0.758,-1.09),
				new Vector2(1.946,-0.87),
				new Vector2(3.134,-1.992),
				new Vector2(0.802,-1.838),
				new Vector2(1.11,-2.674),
				new Vector2(3.442,-3.246),
				new Vector2(2.364,-6.81),
				new Vector2(-3.092,-5.05),
		};
		Simplifier simplifier = new VertexClusterReduction(2.0);
		vertices = simplifier.simplify(vertices);
		// this would generate a self-intersection, but it doesn't because we're preventing them
		TestCase.assertEquals(8, vertices.length);
	}
	
	/**
	 * Tests with the nazca_monkey dataset.
	 */
	@Test
	public void successNazcaMonkey() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/nazca_monkey.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(1197, vertices.length);
	}
	
	/**
	 * Tests with the nazca_heron dataset.
	 */
	@Test
	public void successNazcaHeron() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/nazca_heron.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(950, vertices.length);
	}

	/**
	 * Tests with the zoom1 dataset.
	 */
	@Test
	public void successZoom1() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom1.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(66, vertices.length);
	}

	/**
	 * Tests with the zoom2 dataset.
	 */
	@Test
	public void successZoom2() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom2.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(62, vertices.length);
	}

	/**
	 * Tests with the zoom3 dataset.
	 */
	@Test
	public void successZoom3() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom3.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(59, vertices.length);
	}

	/**
	 * Tests with the zoom4 dataset.
	 */
	@Test
	public void successZoom4() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom4.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(40, vertices.length);
	}

	/**
	 * Tests with the zoom5 dataset.
	 */
	@Test
	public void successZoom5() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom5.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(58, vertices.length);
	}

	/**
	 * Tests with the zoom6 dataset.
	 */
	@Test
	public void successZoom6() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom6.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(78, vertices.length);
	}

	/**
	 * Tests with the zoom7 dataset.
	 */
	@Test
	public void successZoom7() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/zoom7.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(22, vertices.length);
	}
	
	/**
	 * Tests with the tridol1 dataset.
	 */
	@Test
	public void successTridol1() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/tridol1.dat"));
		Simplifier simplifier = new VertexClusterReduction(1.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(18, vertices.length);
	}
	
	/**
	 * Tests with the tridol2 dataset.
	 */
	@Test
	public void successTridol2() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/tridol2.dat"));
		Simplifier simplifier = new VertexClusterReduction(1.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(11, vertices.length);
	}
	
	/**
	 * Tests with the tridol3 dataset.
	 */
	@Test
	public void successTridol3() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/tridol3.dat"));
		Simplifier simplifier = new VertexClusterReduction(1.5);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(18, vertices.length);
	}
	
	/**
	 * Tests with the nsoft1 dataset.
	 */
	@Test
	public void successNsoft1() {
		Vector2[] vertices = this.load(VertexClusterReductionTest.class.getResourceAsStream("/org/dyn4j/data/nsoft1.dat"));
		Simplifier simplifier = new VertexClusterReduction(0.1);
		vertices = simplifier.simplify(vertices);
		TestCase.assertEquals(78, vertices.length);
	}
}
