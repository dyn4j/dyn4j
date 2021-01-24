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
package org.dyn4j.world.result;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.world.CoefficientMixer;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link CoefficientMixer} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class DetectResultTest {
	/**
	 * Tests the empty constructors.
	 */
	@Test
	public void createSuccess() {
		DetectResult<Body, BodyFixture> dr = new DetectResult<Body, BodyFixture>();
		ConvexCastResult<Body, BodyFixture> ccr = new ConvexCastResult<Body, BodyFixture>();
		ConvexDetectResult<Body, BodyFixture> cdr = new ConvexDetectResult<Body, BodyFixture>();
		RaycastResult<Body, BodyFixture> rr = new RaycastResult<Body, BodyFixture>();
		
		// the body-fixture will be null
		// but the other detail is final and non-null
		
		TestCase.assertNull(dr.getBody());
		TestCase.assertNull(dr.getFixture());
		
		TestCase.assertNull(ccr.getBody());
		TestCase.assertNull(ccr.getFixture());
		TestCase.assertNotNull(ccr.getTimeOfImpact());
		
		TestCase.assertNull(cdr.getBody());
		TestCase.assertNull(cdr.getFixture());
		TestCase.assertNotNull(cdr.getPenetration());
		
		TestCase.assertNull(rr.getBody());
		TestCase.assertNull(rr.getFixture());
		TestCase.assertNotNull(rr.getRaycast());
	}
	
	/**
	 * Tests the set methods.
	 */
	@Test
	public void set() {
		Body body = new Body();
		BodyFixture bf = new BodyFixture(Geometry.createCircle(0.5));
		
		DetectResult<Body, BodyFixture> dr = new DetectResult<Body, BodyFixture>();
		ConvexCastResult<Body, BodyFixture> ccr = new ConvexCastResult<Body, BodyFixture>();
		ConvexDetectResult<Body, BodyFixture> cdr = new ConvexDetectResult<Body, BodyFixture>();
		RaycastResult<Body, BodyFixture> rr = new RaycastResult<Body, BodyFixture>();
		
		// NOTE: the goal of this test is not to test the xxx.copy(xxx) methods of
		// the constituent objects, so we're just setting a value to test that
		// the result.setXXX method worked
		TimeOfImpact toi = new TimeOfImpact(); toi.setTime(0.5);
		Penetration pen = new Penetration(); pen.setDepth(5.0);
		Raycast ray = new Raycast(); ray.setDistance(4.0);

		dr.setBody(body);
		dr.setFixture(bf);
		
		ccr.setBody(body);
		ccr.setFixture(bf);
		ccr.setTimeOfImpact(toi);
		
		cdr.setBody(body);
		cdr.setFixture(bf);
		cdr.setPenetration(pen);
		
		rr.setBody(body);
		rr.setFixture(bf);
		rr.setRaycast(ray);
		
		TestCase.assertEquals(dr.getBody(), body);
		TestCase.assertEquals(dr.getFixture(), bf);
		
		TestCase.assertEquals(ccr.getBody(), body);
		TestCase.assertEquals(ccr.getFixture(), bf);
		TestCase.assertEquals(ccr.getTimeOfImpact().getTime(), toi.getTime());
		
		TestCase.assertEquals(cdr.getBody(), body);
		TestCase.assertEquals(cdr.getFixture(), bf);
		TestCase.assertEquals(cdr.getPenetration().getDepth(), pen.getDepth());
		
		TestCase.assertEquals(rr.getBody(), body);
		TestCase.assertEquals(rr.getFixture(), bf);
		TestCase.assertEquals(rr.getRaycast().getDistance(), ray.getDistance());
	}

	/**
	 * Tests the copy methods.
	 */
	@Test
	public void copy() {
		Body body = new Body();
		BodyFixture bf = new BodyFixture(Geometry.createCircle(0.5));
		
		DetectResult<Body, BodyFixture> dr = new DetectResult<Body, BodyFixture>();
		ConvexCastResult<Body, BodyFixture> ccr = new ConvexCastResult<Body, BodyFixture>();
		ConvexDetectResult<Body, BodyFixture> cdr = new ConvexDetectResult<Body, BodyFixture>();
		RaycastResult<Body, BodyFixture> rr = new RaycastResult<Body, BodyFixture>();
		
		// NOTE: the goal of this test is not to test the xxx.copy(xxx) methods of
		// the constituent objects, so we're just setting a value to test that
		// the result.setXXX method worked
		TimeOfImpact toi = new TimeOfImpact(); toi.setTime(0.5);
		Penetration pen = new Penetration(); pen.setDepth(5.0);
		Raycast ray = new Raycast(); ray.setDistance(4.0);

		dr.setBody(body);
		dr.setFixture(bf);
		
		ccr.setBody(body);
		ccr.setFixture(bf);
		ccr.setTimeOfImpact(toi);
		
		cdr.setBody(body);
		cdr.setFixture(bf);
		cdr.setPenetration(pen);
		
		rr.setBody(body);
		rr.setFixture(bf);
		rr.setRaycast(ray);
		
		// test standard copy
		
		DetectResult<Body, BodyFixture> dr2 = dr.copy();
		ConvexCastResult<Body, BodyFixture> ccr2 = ccr.copy();
		ConvexDetectResult<Body, BodyFixture> cdr2 = cdr.copy();
		RaycastResult<Body, BodyFixture> rr2 = rr.copy();
		
		TestCase.assertEquals(dr2.getBody(), body);
		TestCase.assertEquals(dr2.getFixture(), bf);
		
		TestCase.assertEquals(ccr2.getBody(), body);
		TestCase.assertEquals(ccr2.getFixture(), bf);
		TestCase.assertEquals(ccr2.getTimeOfImpact().getTime(), toi.getTime());
		TestCase.assertTrue(ccr2.getTimeOfImpact() != toi);
		
		TestCase.assertEquals(cdr2.getBody(), body);
		TestCase.assertEquals(cdr2.getFixture(), bf);
		TestCase.assertEquals(cdr2.getPenetration().getDepth(), pen.getDepth());
		TestCase.assertTrue(cdr2.getPenetration() != pen);
		
		TestCase.assertEquals(rr2.getBody(), body);
		TestCase.assertEquals(rr2.getFixture(), bf);
		TestCase.assertEquals(rr2.getRaycast().getDistance(), ray.getDistance());
		TestCase.assertTrue(rr2.getRaycast() != ray);
		
		// test updates to the copy don't update the original

		ccr.getTimeOfImpact().setTime(0.7);
		cdr.getPenetration().setDepth(1.0);
		rr.getRaycast().setDistance(2.0);
		
		TestCase.assertEquals(ccr2.getTimeOfImpact().getTime(), toi.getTime());
		TestCase.assertEquals(cdr2.getPenetration().getDepth(), pen.getDepth());
		TestCase.assertEquals(rr2.getRaycast().getDistance(), ray.getDistance());
		
		// test copy(other)
		
		dr2 = new DetectResult<Body, BodyFixture>();
		ccr2 = new ConvexCastResult<Body, BodyFixture>();
		cdr2 = new ConvexDetectResult<Body, BodyFixture>();
		rr2 = new RaycastResult<Body, BodyFixture>();
		
		dr2.copy(dr);
		ccr2.copy(ccr);
		cdr2.copy(cdr);
		rr2.copy(rr);
		
		TestCase.assertEquals(dr2.getBody(), body);
		TestCase.assertEquals(dr2.getFixture(), bf);
		
		TestCase.assertEquals(ccr2.getBody(), body);
		TestCase.assertEquals(ccr2.getFixture(), bf);
		TestCase.assertEquals(ccr2.getTimeOfImpact().getTime(), ccr.getTimeOfImpact().getTime());
		TestCase.assertTrue(ccr2.getTimeOfImpact() != ccr.getTimeOfImpact());
		
		TestCase.assertEquals(cdr2.getBody(), body);
		TestCase.assertEquals(cdr2.getFixture(), bf);
		TestCase.assertEquals(cdr2.getPenetration().getDepth(), cdr.getPenetration().getDepth());
		TestCase.assertTrue(cdr2.getPenetration() != cdr.getPenetration());
		
		TestCase.assertEquals(rr2.getBody(), body);
		TestCase.assertEquals(rr2.getFixture(), bf);
		TestCase.assertEquals(rr2.getRaycast().getDistance(), rr.getRaycast().getDistance());
		TestCase.assertTrue(rr2.getRaycast() != rr.getRaycast());
	}
	
	/**
	 * Tests the compareTo methods.
	 */
	@Test
	public void compareTo() {
		Body body = new Body();
		BodyFixture bf = new BodyFixture(Geometry.createCircle(0.5));
		
		ConvexCastResult<Body, BodyFixture> ccr = new ConvexCastResult<Body, BodyFixture>();
		RaycastResult<Body, BodyFixture> rr = new RaycastResult<Body, BodyFixture>();
		
		TimeOfImpact toi = new TimeOfImpact(); toi.setTime(0.5);
		Raycast ray = new Raycast(); ray.setDistance(4.0);

		ccr.setBody(body);
		ccr.setFixture(bf);
		ccr.setTimeOfImpact(toi);
		
		rr.setBody(body);
		rr.setFixture(bf);
		rr.setRaycast(ray);
		
		// test compareto
		
		ConvexCastResult<Body, BodyFixture> ccr2 = ccr.copy();
		RaycastResult<Body, BodyFixture> rr2 = rr.copy();
		
		ccr2.getTimeOfImpact().setTime(6.0);
		rr2.getRaycast().setDistance(2.0);
		
		TestCase.assertTrue(ccr.compareTo(ccr2) < 0);
		TestCase.assertTrue(rr.compareTo(rr2) > 0);
	}
}
