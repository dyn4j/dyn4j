/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Feature}, {@link EdgeFeature}, and {@link PointFeature} classes.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public class FeatureTest {
	/**
	 * Tests standard creation of features.
	 */
	@Test
	public void create() {
		PointFeature pf1 = new PointFeature(new Vector2(1.0, 1.0), 1);
		PointFeature pf2 = new PointFeature(new Vector2(1.0, 2.0), 0);
		EdgeFeature ef = new EdgeFeature(pf1, pf2, pf1, pf1.point.to(pf2.point), 2);
		
		TestCase.assertEquals(1.0, pf1.getPoint().x);
		TestCase.assertEquals(1.0, pf1.getPoint().y);
		TestCase.assertEquals(1, pf1.getIndex());
		TestCase.assertNotNull(pf1.toString());
		
		TestCase.assertEquals(1.0, pf2.getPoint().x);
		TestCase.assertEquals(2.0, pf2.getPoint().y);
		TestCase.assertEquals(0, pf2.getIndex());
		TestCase.assertNotNull(pf2.toString());
		
		TestCase.assertEquals(pf1, ef.getMaximum());
		TestCase.assertEquals(pf1, ef.getVertex1());
		TestCase.assertEquals(pf2, ef.getVertex2());
		TestCase.assertEquals(0.0, ef.getEdge().x);
		TestCase.assertEquals(1.0, ef.getEdge().y);
		TestCase.assertEquals(2, ef.getIndex());
		TestCase.assertNotNull(ef.toString());
	}
	
	/**
	 * Tests the creation with a non-indexed feature (for curved shapes)
	 */
	@Test
	public void createNonIndexed() {
		PointFeature pf1 = new PointFeature(new Vector2(1.0, 1.0), Feature.NOT_INDEXED);
		
		TestCase.assertEquals(1.0, pf1.getPoint().x);
		TestCase.assertEquals(1.0, pf1.getPoint().y);
		TestCase.assertEquals(Feature.NOT_INDEXED, pf1.getIndex());
		TestCase.assertNotNull(pf1.toString());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		PointFeature pf = new PointFeature(new Vector2(5, -4), 1);
		PointFeature cpf = pf.copy();
		
		TestCase.assertNotSame(pf, cpf);
		TestCase.assertNotSame(pf.point, cpf.point);
		TestCase.assertEquals(pf.index, cpf.index);
		TestCase.assertEquals(pf.point.x, cpf.point.x);
		TestCase.assertEquals(pf.point.y, cpf.point.y);
		
		PointFeature pf1 = new PointFeature(new Vector2(5, -4), 1);
		PointFeature pf2 = new PointFeature(new Vector2(5, -4), 2);
		EdgeFeature ef = new EdgeFeature(pf1, pf2, pf1, new Vector2(-1, 0), 1);
		EdgeFeature cef = ef.copy();
		
		TestCase.assertNotSame(ef, cef);
		TestCase.assertNotSame(ef.vertex1, cef.vertex1);
		TestCase.assertNotSame(ef.vertex1.point, cef.vertex1.point);
		TestCase.assertEquals(ef.vertex1.index, cef.vertex1.index);
		TestCase.assertEquals(ef.vertex1.point.x, cef.vertex1.point.x);
		TestCase.assertEquals(ef.vertex1.point.y, cef.vertex1.point.y);
		TestCase.assertNotSame(ef.vertex2, cef.vertex2);
		TestCase.assertNotSame(ef.vertex2.point, cef.vertex2.point);
		TestCase.assertEquals(ef.vertex2.index, cef.vertex2.index);
		TestCase.assertEquals(ef.vertex2.point.x, cef.vertex2.point.x);
		TestCase.assertEquals(ef.vertex2.point.y, cef.vertex2.point.y);
		TestCase.assertNotSame(ef.max, cef.max);
		TestCase.assertNotSame(ef.max.point, cef.max.point);
		TestCase.assertEquals(ef.max.index, cef.max.index);
		TestCase.assertEquals(ef.max.point.x, cef.max.point.x);
		TestCase.assertEquals(ef.max.point.y, cef.max.point.y);
		TestCase.assertNotSame(ef.edge, cef.edge);
		TestCase.assertEquals(ef.edge.x, cef.edge.x);
		TestCase.assertEquals(ef.edge.y, cef.edge.y);
		TestCase.assertEquals(ef.index, cef.index);
	}
}
