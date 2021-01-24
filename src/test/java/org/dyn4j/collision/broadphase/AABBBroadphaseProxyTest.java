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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.AABB;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link AABBBroadphaseProxy} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class AABBBroadphaseProxyTest {
	/**
	 * Tests the helper classes.
	 */
	@Test
	public void helpers() {
		AABBBroadphaseProxy<TestCollisionBody> proxy = new AABBBroadphaseProxy<TestCollisionBody>(new TestCollisionBody());
		TestCase.assertNotNull(proxy.toString());
	}
	
	/**
	 * Tests the compareTo method.
	 */
	@Test
	public void compareTo() {
		AABBBroadphaseProxy<TestCollisionBody> proxy1 = new AABBBroadphaseProxy<TestCollisionBody>(null);
		AABBBroadphaseProxy<TestCollisionBody> proxy2 = new AABBBroadphaseProxy<TestCollisionBody>(null);
		AABBBroadphaseProxy<TestCollisionBody> proxy3 = new AABBBroadphaseProxy<TestCollisionBody>(null);
		AABBBroadphaseProxy<TestCollisionBody> proxy4 = new AABBBroadphaseProxy<TestCollisionBody>(null);
		AABBBroadphaseProxy<TestCollisionBody> proxy5 = new AABBBroadphaseProxy<TestCollisionBody>(null);
		
		proxy1.aabb.set(new AABB(0, 0, 10, 10));
		proxy2.aabb.set(new AABB(1, 1, 2, 2));
		proxy3.aabb.set(new AABB(0, 0, 5, 5));
		proxy4.aabb.set(new AABB(-1, 0, 5, 5));
		proxy5.aabb.set(new AABB(0, 1, 5, 5));
		
		// larger minx
		TestCase.assertEquals(-1, proxy1.compareTo(proxy2));
		
		// equal minx/miny
		TestCase.assertEquals(0, proxy1.compareTo(proxy3));
		
		// smaller minx
		TestCase.assertEquals(1, proxy1.compareTo(proxy4));
		
		// same minx, but different miny
		TestCase.assertEquals(-1, proxy1.compareTo(proxy5));
	}
}
