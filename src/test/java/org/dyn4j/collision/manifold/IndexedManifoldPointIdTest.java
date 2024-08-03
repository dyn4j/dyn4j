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
package org.dyn4j.collision.manifold;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link IndexedManifoldPointId} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public class IndexedManifoldPointIdTest {
	/**
	 * Tests the constructors.
	 */
	@Test
	public void create() {
		IndexedManifoldPointId id = new IndexedManifoldPointId(0, 1, 0);
		
		TestCase.assertEquals(0, id.referenceEdgeIndex);
		TestCase.assertEquals(1, id.incidentEdgeIndex);
		TestCase.assertEquals(0, id.incidentVertexIndex);
		TestCase.assertEquals(false, id.flipped);
		
		TestCase.assertEquals(0, id.getReferenceEdgeIndex());
		TestCase.assertEquals(1, id.getIncidentEdgeIndex());
		TestCase.assertEquals(0, id.getIncidentVertexIndex());
		TestCase.assertEquals(false, id.isFlipped());
		
		id = new IndexedManifoldPointId(1, 2, 3, true);
		
		TestCase.assertEquals(1, id.referenceEdgeIndex);
		TestCase.assertEquals(2, id.incidentEdgeIndex);
		TestCase.assertEquals(3, id.incidentVertexIndex);
		TestCase.assertEquals(true, id.flipped);
		
		TestCase.assertEquals(1, id.getReferenceEdgeIndex());
		TestCase.assertEquals(2, id.getIncidentEdgeIndex());
		TestCase.assertEquals(3, id.getIncidentVertexIndex());
		TestCase.assertEquals(true, id.isFlipped());
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equals() {
		IndexedManifoldPointId id1 = new IndexedManifoldPointId(0, 1, 0);
		IndexedManifoldPointId id2 = new IndexedManifoldPointId(0, 1, 0);
		
		TestCase.assertEquals(id1, id1);
		TestCase.assertEquals(id1, id2);
		TestCase.assertNotSame(id1, id2);
		
		IndexedManifoldPointId id3 = new IndexedManifoldPointId(0, 1, 2);
		
		TestCase.assertFalse(id3.equals(id2));
		
		IndexedManifoldPointId id4 = new IndexedManifoldPointId(0, 0, 0);
		
		TestCase.assertFalse(id4.equals(id2));
		
		IndexedManifoldPointId id5 = new IndexedManifoldPointId(1, 0, 0);
		
		TestCase.assertFalse(id5.equals(id2));
		
		IndexedManifoldPointId id6 = new IndexedManifoldPointId(0, 1, 0, true);
		
		TestCase.assertFalse(id6.equals(id2));
		
		// test null
		TestCase.assertFalse(id1.equals(null));
		
		// test different object type
		TestCase.assertFalse(id1.equals(new Object()));
	}
	
	/**
	 * Tests the hashCode method.
	 */
	@Test
	public void hashcode() {
		IndexedManifoldPointId id1 = new IndexedManifoldPointId(0, 1, 0);
		IndexedManifoldPointId id2 = new IndexedManifoldPointId(0, 1, 0);
		
		TestCase.assertEquals(id1.hashCode(), id2.hashCode());
		
		IndexedManifoldPointId id3 = new IndexedManifoldPointId(0, 1, 2);
		
		TestCase.assertFalse(id3.hashCode() == id1.hashCode());
		
		IndexedManifoldPointId id4 = new IndexedManifoldPointId(0, 1, 0, true);
		
		TestCase.assertFalse(id4.hashCode() == id1.hashCode());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		IndexedManifoldPointId id = new IndexedManifoldPointId(0, 1, 0);
		TestCase.assertNotNull(id.toString());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		IndexedManifoldPointId id = new IndexedManifoldPointId(0, 1, 0);
		IndexedManifoldPointId copy = id.copy();
		
		TestCase.assertEquals(id.incidentEdgeIndex, copy.incidentEdgeIndex);
		TestCase.assertEquals(id.incidentVertexIndex, copy.incidentVertexIndex);
		TestCase.assertEquals(id.referenceEdgeIndex, copy.referenceEdgeIndex);
		TestCase.assertEquals(id.flipped, copy.flipped);
	}
}
