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
package org.dyn4j.collision;

import java.util.Iterator;
import java.util.List;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class to test the {@link AbstractCollisionBody} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public class AbstractCollisionBodyTest {
	private class TestBody extends AbstractCollisionBody<Fixture> {
		private Vector2 center = new Vector2();
		
		public TestBody() {
			super();
		}

		public TestBody(int fixtureCount) {
			super(fixtureCount);
		}
		
		protected TestBody(TestBody body) {
			super(body);
		}

		@Override
		public Fixture addFixture(Convex convex) {
			Fixture fixture = new Fixture(convex);
			this.addFixture(fixture);
			return fixture;
		}

		@Override
		public Vector2 getLocalCenter() {
			return this.center;
		}
		
		@Override
		public TestBody copy() {
			return new TestBody(this);
		}
	}
	
	private class TestFixture extends Fixture {
		public TestFixture(Convex shape) {
			super(shape);
		}
	}
	
	private class FixtureModificationCounter implements FixtureModificationHandler<Fixture> {
		private int added;
		private int removed;
		private boolean allRemoved;

		@Override
		public void onFixtureAdded(Fixture fixture) {
			this.added++;
		}

		@Override
		public void onFixtureRemoved(Fixture fixture) {
			this.removed++;
		}

		@Override
		public void onAllFixturesRemoved() {
			this.allRemoved = true;
		}
		
		public void reset() {
			this.added = 0;
			this.removed = 0;
			this.allRemoved = false;
		}
	}
	
	/**
	 * Tests the constructors.
	 */
	@Test
	public void create() {
		TestBody b = new TestBody();
		TestCase.assertNull(b.owner);
		TestCase.assertNull(b.userData);
		TestCase.assertTrue(b.enabled);
		TestCase.assertNull(b.fixtureModificationHandler);
		TestCase.assertNotNull(b.fixtures);
		TestCase.assertNotNull(b.fixturesUnmodifiable);
		TestCase.assertEquals(0.0, b.radius);
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.transform0);
		
		b = new TestBody(5);
		TestCase.assertNull(b.owner);
		TestCase.assertNull(b.userData);
		TestCase.assertTrue(b.enabled);
		TestCase.assertNull(b.fixtureModificationHandler);
		TestCase.assertNotNull(b.fixtures);
		TestCase.assertNotNull(b.fixturesUnmodifiable);
		TestCase.assertEquals(0.0, b.radius);
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.transform0);
		
		new TestBody(-1);
	}

	/**
	 * Tests adding a valid fixture.
	 */
	@Test
	public void addFixture() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		TestCase.assertEquals(1, b.getFixtureCount());
		
		b.addFixture(new Fixture(Geometry.createEquilateralTriangle(2.0)));
		TestCase.assertEquals(2, b.getFixtureCount());
	}
	
	/**
	 * Tests adding a fixture using a null convex shape.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullConvex() {
		TestBody b = new TestBody();
		b.addFixture((Convex) null);
	}
	
	/**
	 * Tests adding a null fixture.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullFixture() {
		TestBody b = new TestBody();
		b.addFixture((Fixture)null);
	}
	
	/**
	 * Tests the contains(Vector2) method.
	 * @since 3.1.5
	 */
	@Test
	public void containsPoint() {
		TestBody b = new TestBody();
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 1.0);
		c1.translate(0.75, 0.0);
		b.addFixture(c1);
		b.addFixture(c2);
		
		TestCase.assertTrue(b.contains(new Vector2(0.0, 0.0)));
		TestCase.assertTrue(b.contains(new Vector2(0.5, 0.0)));
		TestCase.assertTrue(b.contains(new Vector2(0.55, 0.25)));
		TestCase.assertFalse(b.contains(new Vector2(0.52, 0.45)));
		TestCase.assertTrue(b.contains(new Vector2(0.70, 0.3)));
	}

	/**
	 * Tests the containment of a fixture.
	 */
	@Test
	public void containsFixture() {
		TestBody b = new TestBody();
		Fixture f = b.addFixture(Geometry.createCircle(1.0));
		TestCase.assertEquals(1, b.getFixtureCount());
		TestCase.assertTrue(b.containsFixture(f));
		
		Fixture f2 = new Fixture(Geometry.createCircle(0.5));
		TestCase.assertFalse(b.containsFixture(f2));
	}

	/**
	 * Tests the create AABB method.
	 * @since 3.0.2
	 */
	@Test
	public void createAABB() {
		TestBody b = new TestBody();
		
		// create an aabb from an empty body (no fixtures)
		AABB aabb = b.createAABB();
		TestCase.assertEquals(0.0, aabb.getMaxX());
		TestCase.assertEquals(0.0, aabb.getMaxY());
		TestCase.assertEquals(0.0, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
		
		// create an aabb from just one fixture
		b.addFixture(Geometry.createCircle(0.5));
		aabb = b.createAABB();
		TestCase.assertEquals(0.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		
		// create an aabb from more than one fixture
		Fixture bf = b.addFixture(Geometry.createRectangle(1.0, 1.0));
		bf.getShape().translate(-0.5, 0.0);
		aabb = b.createAABB();
		TestCase.assertEquals(0.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		TestCase.assertEquals(-1.0, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		
		// create an aabb from more than one fixture + a different transform
		Transform tx = new Transform();
		tx.translate(0.2, 0.5);
		aabb = b.createAABB(tx);
		TestCase.assertEquals(0.7, aabb.getMaxX());
		TestCase.assertEquals(1.0, aabb.getMaxY());
		TestCase.assertEquals(-0.8, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
	}

	/**
	 * Tests getting fixtures using a world space point.
	 * @since 3.1.8
	 */
	@Test
	public void getFixtureByPoint() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		Fixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test not in body
		bf = b.getFixture(new Vector2(-1.0, -1.0));
		TestCase.assertNull(bf);
		
		// confirm there are two fixtures at this location
		TestCase.assertEquals(2, b.getFixtures(new Vector2(0.5, 0.25)).size());
		
		// test getting the first one
		bf = b.getFixture(new Vector2(0.5, 0.25));
		TestCase.assertNotNull(bf);
		TestCase.assertTrue(bf.getShape() instanceof Circle);
		
		// test not in body
		List<Fixture> bfs = b.getFixtures(new Vector2(-1.0, -1.0));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(0, bfs.size());
		
		// test in body remove one
		bfs = b.getFixtures(new Vector2(1.25, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(1, bfs.size());
		TestCase.assertTrue(bfs.get(0).getShape() instanceof Polygon);
		
		// test in body remove both
		bfs = b.getFixtures(new Vector2(0.75, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(2, bfs.size());
	}
	
	/**
	 * Tests getting fixtures at a specified index.
	 * @since 4.0.0
	 */
	@Test
	public void getFixtureByIndex() {
		TestBody b = new TestBody();
		Fixture f1 = b.addFixture(Geometry.createCircle(1.0));
		Fixture f2 = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		Fixture f3 = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		
		TestCase.assertEquals(3, b.fixtures.size());
		TestCase.assertEquals(f1, b.getFixture(0));
		TestCase.assertEquals(f2, b.getFixture(1));
		TestCase.assertEquals(f3, b.getFixture(2));
	}
	
	/**
	 * Tests getting the fixture count.
	 * @since 4.0.0
	 */
	@Test
	public void getFixtureCount() {
		TestBody b = new TestBody();
		Fixture f1 = b.addFixture(Geometry.createCircle(1.0));
		b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		
		TestCase.assertEquals(3, b.getFixtureCount());
		
		b.removeFixture(f1);
		
		TestCase.assertEquals(2, b.getFixtureCount());
		
		b.removeAllFixtures();
		
		TestCase.assertEquals(0, b.getFixtureCount());
	}

	/**
	 * Tests the fixture iterator.
	 */
	@Test
	public void getFixtureIterator() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.addFixture(Geometry.createCircle(0.4));
		b.addFixture(Geometry.createCircle(0.3));
		b.addFixture(Geometry.createCircle(0.2));
		
		// test iteration
		Iterator<Fixture> it = b.getFixtureIterator();
		while (it.hasNext()) {
			it.next();
		}
		
		// test iteration w/ removal
		it = b.getFixtureIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		
		TestCase.assertEquals(0, b.getFixtureCount());
	}
	
	/**
	 * Tests the getFixtureTterator method failure cases.
	 */
	@Test
	public void getFixtureIteratorFailures() {
		TestBody b = new TestBody();

		Iterator<Fixture> it = b.getFixtureIterator();
		TestCase.assertNotNull(it);
		TestCase.assertFalse(it.hasNext());
		
		b.addFixture(Geometry.createCircle(0.5));
		b.addFixture(Geometry.createRectangle(1.0, 0.5));
		
		// test overflow
		it = b.getFixtureIterator();
		try {
			it.next();
			it.next();
			it.next();
			TestCase.fail();
		} catch (IndexOutOfBoundsException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test remove
		it = b.getFixtureIterator();
		try {
			it.remove();
			TestCase.fail();
		} catch (IllegalStateException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test second remove
		it = b.getFixtureIterator();
		try {
			it.next();
			it.remove();
			it.remove();
			TestCase.fail();
		} catch (IllegalStateException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test source modification before remove
		it = b.getFixtureIterator();
		try {
			it.next();
			b.removeAllFixtures();
			it.remove();
			TestCase.fail();
		} catch (IndexOutOfBoundsException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}

	/**
	 * Tests the get/set FixtureModificationHandler methods.
	 */
	@Test
	public void getSetFixtureModificationHandler() {
		TestBody b = new TestBody();
		Fixture f1 = b.addFixture(Geometry.createCircle(0.5));
		Fixture f2 = b.addFixture(Geometry.createCircle(0.4));

		TestCase.assertNull(b.fixtureModificationHandler);
		TestCase.assertNull(b.getFixtureModificationHandler());
		
		FixtureModificationCounter fmc = new FixtureModificationCounter();
		b.setFixtureModificationHandler(fmc);
		
		TestCase.assertNotNull(b.fixtureModificationHandler);
		TestCase.assertNotNull(b.getFixtureModificationHandler());
		
		b.addFixture(Geometry.createCircle(0.3));
		b.addFixture(Geometry.createCircle(0.2));
		
		TestCase.assertEquals(2, fmc.added);
		
		b.removeFixture(0);
		
		TestCase.assertEquals(1, fmc.removed);
		
		b.removeAllFixtures();
		
		TestCase.assertTrue(fmc.allRemoved);
		
		fmc.reset();
		b.addFixture(f1);
		b.removeFixture(f1);
		
		TestCase.assertEquals(1, fmc.removed);
		
		fmc.reset();
		b.addFixture(f1);
		b.addFixture(f2);
		b.removeFixture(new Vector2());
		
		TestCase.assertEquals(1, fmc.removed);
		
		fmc.reset();
		b.addFixture(f1);
		b.removeFixtures(new Vector2());
		
		TestCase.assertEquals(2, fmc.removed);
	}
	
	/**
	 * Tests the getFixtures method.
	 */
	@Test
	public void getFixtures() {
		TestBody b = new TestBody();
		
		TestCase.assertNotNull(b.getFixtures());
		
		Fixture f1 = b.addFixture(Geometry.createCircle(0.5));
		List<Fixture> fixtures = b.getFixtures();
		TestCase.assertEquals(1, fixtures.size());
		TestCase.assertEquals(f1, fixtures.get(0));
		
		Fixture f2 = b.addFixture(Geometry.createCircle(0.5));
		fixtures = b.getFixtures();
		TestCase.assertEquals(2, fixtures.size());
		TestCase.assertEquals(f1, fixtures.get(0));
		TestCase.assertEquals(f2, fixtures.get(1));
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getFixturesAndAdd() {
		TestBody b = new TestBody();
		b.getFixtures().add(new Fixture(Geometry.createCircle(0.5)));
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getFixturesAndRemove() {
		TestBody b = new TestBody();
		b.getFixtures().remove(0);
	}
	
	/**
	 * Tests getting fixtures by a world space point.
	 * @since 4.0.0
	 */
	@Test
	public void getFixturesByPoint() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		Fixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test not in body
		List<Fixture> fixtures = b.getFixtures(new Vector2(-1.0, -1.0));
		TestCase.assertNotNull(fixtures);
		TestCase.assertEquals(0, fixtures.size());
		
		// test on top of both
		fixtures = b.getFixtures(new Vector2(0.5, 0.25));
		TestCase.assertEquals(2, fixtures.size());
		
		// test in body remove one
		fixtures = b.getFixtures(new Vector2(1.25, 0.10));
		TestCase.assertNotNull(fixtures);
		TestCase.assertEquals(1, fixtures.size());
		TestCase.assertTrue(fixtures.get(0).getShape() instanceof Polygon);
	}
	
	/**
	 * Tests the getLocalPoint method.
	 * @since 4.0.0
	 */
	@Test
	public void getLocalPoint() {
		TestBody b = new TestBody();
		
		Vector2 wsp = new Vector2();
		Vector2 lsp = b.getLocalPoint(wsp);
		
		// test without transform
		TestCase.assertEquals(0.0, lsp.x);
		TestCase.assertEquals(0.0, lsp.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		lsp = b.getLocalPoint(wsp);
		TestCase.assertEquals(-1.0, lsp.x);
		TestCase.assertEquals(-2.0, lsp.y);
		
		// test with transform + non-origin point
		wsp.set(1, 1);
		lsp = b.getLocalPoint(wsp);
		TestCase.assertEquals(0.0, lsp.x);
		TestCase.assertEquals(-1.0, lsp.y);
		
		// test destination variation
		b.transform.identity();
		wsp.zero();
		Vector2 dest = new Vector2(1, 1);
		b.getLocalPoint(wsp, dest);
		
		// test without transform
		TestCase.assertEquals(0.0, wsp.x);
		TestCase.assertEquals(0.0, wsp.y);
		TestCase.assertEquals(0.0, dest.x);
		TestCase.assertEquals(0.0, dest.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		b.getLocalPoint(wsp, dest);
		TestCase.assertEquals(0.0, wsp.x);
		TestCase.assertEquals(0.0, wsp.y);
		TestCase.assertEquals(-1.0, dest.x);
		TestCase.assertEquals(-2.0, dest.y);
		
		// test with transform + non-origin point
		wsp.set(1, 1);
		b.getLocalPoint(wsp, dest);
		TestCase.assertEquals(1.0, wsp.x);
		TestCase.assertEquals(1.0, wsp.y);
		TestCase.assertEquals(0.0, dest.x);
		TestCase.assertEquals(-1.0, dest.y);
	}

	/**
	 * Tests the getLocalVector method.
	 * @since 4.0.0
	 */
	@Test
	public void getLocalVector() {
		TestBody b = new TestBody();
		
		Vector2 wsv = new Vector2(1.0, 1.0);
		Vector2 lsv = b.getLocalVector(wsv);
		
		// test without transform
		TestCase.assertEquals(1.0, lsv.x);
		TestCase.assertEquals(1.0, lsv.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		lsv = b.getLocalVector(wsv);
		TestCase.assertEquals(1.0, lsv.x);
		TestCase.assertEquals(1.0, lsv.y);
		
		// test with transform + non-origin point
		b.rotate(Math.toRadians(90));
		lsv = b.getLocalVector(wsv);
		TestCase.assertEquals(1.0, lsv.x, 1e-8);
		TestCase.assertEquals(-1.0, lsv.y, 1e-8);
		
		// test the destination variants
		b.transform.identity();
		wsv.set(1, 1);
		Vector2 dest = new Vector2(2, 2);
		b.getLocalVector(wsv, dest);
		
		// test without transform
		TestCase.assertEquals(1.0, wsv.x);
		TestCase.assertEquals(1.0, wsv.y);
		TestCase.assertEquals(1.0, dest.x);
		TestCase.assertEquals(1.0, dest.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		b.getLocalVector(wsv, dest);
		TestCase.assertEquals(1.0, wsv.x);
		TestCase.assertEquals(1.0, wsv.y);
		TestCase.assertEquals(1.0, dest.x);
		TestCase.assertEquals(1.0, dest.y);
		
		// test with transform + non-origin point
		b.rotate(Math.toRadians(90));
		b.getLocalVector(wsv, dest);
		TestCase.assertEquals(1.0, wsv.x);
		TestCase.assertEquals(1.0, wsv.y);
		TestCase.assertEquals(1.0, dest.x, 1e-8);
		TestCase.assertEquals(-1.0, dest.y, 1e-8);
	}
	
	/**
	 * Tests the get/set of ownership.
	 */
	@Test
	public void getSetOwner() {
		TestBody b = new TestBody();
		
		TestCase.assertNull(b.getOwner());
		TestCase.assertNull(b.owner);
		
		Object o = new Object();
		b.setOwner(o);
		TestCase.assertNotNull(b.getOwner());
		TestCase.assertNotNull(b.owner);
		TestCase.assertEquals(o, b.owner);
		TestCase.assertEquals(o, b.getOwner());
		
		b.setOwner(null);
		TestCase.assertNull(b.getOwner());
		TestCase.assertNull(b.owner);
	}
	
	/**
	 * Tests the get/set RotationDiscRadius methods.
	 */
	@Test
	public void getSetRotationDiscRadius() {
		TestBody b = new TestBody();
		
		b.setRotationDiscRadius(b.center);
		TestCase.assertEquals(0.0, b.radius);
		TestCase.assertEquals(0.0, b.getRotationDiscRadius());
		
		Fixture f1 = b.addFixture(Geometry.createCircle(0.5));
		Fixture f2 = b.addFixture(Geometry.createCircle(0.4));
		
		b.setRotationDiscRadius(b.center);
		
		TestCase.assertEquals(0.5, b.radius);
		TestCase.assertEquals(0.5, b.getRotationDiscRadius());
		
		f1.getShape().translate(1.0, 0.0);
		f2.getShape().translate(0.0, 1.0);
		b.setRotationDiscRadius(b.center);
		
		TestCase.assertEquals(1.5, b.radius);
		TestCase.assertEquals(1.5, b.getRotationDiscRadius());
	}
	
	/**
	 * Tests the get/set Transform methods.
	 */
	@Test
	public void getSetTransform() {
		TestBody b = new TestBody();
		
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.getTransform());
		
		b.setTransform(null);
		
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.getTransform());
		
		Transform tx = new Transform();
		b.setTransform(tx);
		
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.getTransform());
		TestCase.assertFalse(tx == b.getTransform());
		
		tx.translate(2.0, 1.0);
		b.setTransform(tx);
		
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.getTransform());
		TestCase.assertFalse(tx == b.getTransform());
		TestCase.assertEquals(tx.getTranslationX(), b.getTransform().getTranslationX());
		TestCase.assertEquals(tx.getTranslationY(), b.getTransform().getTranslationY());
	}

	/**
	 * Tests the get/set of the previous transform
	 */
	@Test
	public void getSetPreviousTransform() {
		TestBody b = new TestBody();
		
		// test the default
		TestCase.assertNotNull(b.getPreviousTransform());
		TestCase.assertTrue(b.getPreviousTransform().isIdentity());
		
		b.translate(1.0, -1.0);
		
		TestCase.assertNotNull(b.getPreviousTransform());
		TestCase.assertTrue(b.getPreviousTransform().isIdentity());
		
		b.getPreviousTransform().set(b.getTransform());
		
		TestCase.assertNotNull(b.getPreviousTransform());
		TestCase.assertFalse(b.getPreviousTransform().isIdentity());
		TestCase.assertEquals(1.0, b.getPreviousTransform().getTranslationX());
		TestCase.assertEquals(-1.0, b.getPreviousTransform().getTranslationY());
	}
	
	/**
	 * Make sure the user data is stored.
	 */
	@Test
	public void getSetUserData() {
		String obj = "hello";
		TestBody b = new TestBody();
		
		TestCase.assertNull(b.getUserData());
		
		b.setUserData(obj);
		TestCase.assertNotNull(b.getUserData());
		TestCase.assertEquals(obj, b.getUserData());
		
		b.setUserData(null);
		TestCase.assertNull(b.getUserData());
	}
	
	/**
	 * Tests the getWorldCenter method.
	 */
	@Test
	public void getWorldCenter() {
		TestBody b = new TestBody();
		
		Vector2 wc = b.getWorldCenter();
		TestCase.assertEquals(0.0, wc.x);
		TestCase.assertEquals(0.0, wc.y);
		
		b.translate(2.0, 0.5);
		wc = b.getWorldCenter();
		TestCase.assertEquals(2.0, wc.x);
		TestCase.assertEquals(0.5, wc.y);
	}

	/**
	 * Tests the getWorldPoint method.
	 * @since 4.0.0
	 */
	@Test
	public void getWorldPoint() {
		TestBody b = new TestBody();
		
		Vector2 lsp = new Vector2();
		Vector2 wsp = b.getWorldPoint(lsp);
		
		// test without transform
		TestCase.assertEquals(0.0, wsp.x);
		TestCase.assertEquals(0.0, wsp.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		wsp = b.getWorldPoint(lsp);
		TestCase.assertEquals(1.0, wsp.x);
		TestCase.assertEquals(2.0, wsp.y);
		
		// test with transform + non-origin point
		lsp.set(1, 1);
		wsp = b.getWorldPoint(lsp);
		TestCase.assertEquals(2.0, wsp.x);
		TestCase.assertEquals(3.0, wsp.y);
		
		// test the destination methods
		
		// reset test data
		b.translateToOrigin();
		lsp.set(0 ,0);
		Vector2 dest = new Vector2(1, 1);
		b.getWorldPoint(lsp, dest);
		
		// test without transform
		TestCase.assertEquals(0.0, dest.x);
		TestCase.assertEquals(0.0, dest.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		b.getWorldPoint(lsp, dest);
		TestCase.assertEquals(0.0, lsp.x);
		TestCase.assertEquals(0.0, lsp.y);
		TestCase.assertEquals(1.0, dest.x);
		TestCase.assertEquals(2.0, dest.y);
		
		// test with transform + non-origin point
		lsp.set(1, 1);
		b.getWorldPoint(lsp, dest);
		TestCase.assertEquals(1.0, lsp.x);
		TestCase.assertEquals(1.0, lsp.y);
		TestCase.assertEquals(2.0, dest.x);
		TestCase.assertEquals(3.0, dest.y);
	}

	/**
	 * Tests the getWorldVector method.
	 * @since 4.0.0
	 */
	@Test
	public void getWorldVector() {
		TestBody b = new TestBody();
		
		Vector2 lsv = new Vector2(1.0, 1.0);
		Vector2 wsv = b.getWorldVector(lsv);
		
		// test without transform
		TestCase.assertEquals(1.0, wsv.x);
		TestCase.assertEquals(1.0, wsv.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		wsv = b.getWorldVector(lsv);
		TestCase.assertEquals(1.0, wsv.x);
		TestCase.assertEquals(1.0, wsv.y);
		
		// test with transform + non-origin point
		b.rotate(Math.toRadians(90));
		wsv = b.getWorldVector(lsv);
		TestCase.assertEquals(-1.0, wsv.x, 1e-8);
		TestCase.assertEquals(1.0, wsv.y, 1e-8);

		// test the destination methods
		
		// reset test data
		b.transform.setRotation(0);
		b.transform.setTranslation(0, 0);
		lsv.set(1 ,1);
		Vector2 dest = new Vector2(0, 0);
		b.getWorldPoint(lsv, dest);
		
		// test without transform
		TestCase.assertEquals(1.0, dest.x);
		TestCase.assertEquals(1.0, dest.y);
		
		// test with transform
		b.translate(1.0, 2.0);
		b.getWorldPoint(lsv, dest);
		TestCase.assertEquals(1.0, lsv.x);
		TestCase.assertEquals(1.0, lsv.y);
		TestCase.assertEquals(2.0, dest.x);
		TestCase.assertEquals(3.0, dest.y);
		
		// test with transform + non-origin point
		lsv.set(2, 2);
		b.rotateAboutCenter(Math.toRadians(90));
		b.getWorldPoint(lsv, dest);
		TestCase.assertEquals(2.0, lsv.x);
		TestCase.assertEquals(2.0, lsv.y);
		TestCase.assertEquals(-1.000, dest.x, 1e-8);
		TestCase.assertEquals(4.0, dest.y);
	}
	
	/**
	 * Tests the set/is enabled methods.
	 */
	@Test
	public void getSetEnabled() {
		TestBody b = new TestBody();
		
		TestCase.assertTrue(b.enabled);
		TestCase.assertTrue(b.isEnabled());
		
		b.setEnabled(false);
		
		TestCase.assertFalse(b.enabled);
		TestCase.assertFalse(b.isEnabled());
		
		b.setEnabled(true);
		
		TestCase.assertTrue(b.enabled);
		TestCase.assertTrue(b.isEnabled());
	}

	/**
	 * Tests the removal of all fixtures.
	 * @since 3.0.2
	 */
	@Test
	public void removeAllFixtures() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		b.addFixture(Geometry.createRectangle(1.0, 0.5));
		b.addFixture(Geometry.createSegment(new Vector2(1.0, -2.0)));
		
		TestCase.assertEquals(3, b.getFixtureCount());
		
		b.removeAllFixtures();
	
		TestCase.assertEquals(0, b.getFixtureCount());
	}
	
	/**
	 * Tests the remove fixture methods.
	 */
	@Test
	public void removeFixture() {
		TestBody b = new TestBody();
		Fixture f = b.addFixture(Geometry.createCircle(1.0));
		
		// test removing the fixture
		boolean success = b.removeFixture(f);
		TestCase.assertEquals(0, b.getFixtures().size());
		TestCase.assertTrue(success);
		
		b.addFixture(f);
		Fixture f2 = b.addFixture(Geometry.createSquare(0.5));
		success = b.removeFixture(f);
		TestCase.assertEquals(1, b.getFixtures().size());
		TestCase.assertTrue(f2 == b.getFixtures().get(0));
		TestCase.assertTrue(success);
		
		// test removing by index
		f = b.addFixture(Geometry.createEquilateralTriangle(0.5));
		b.addFixture(Geometry.createRectangle(1.0, 2.0));
		f2 = b.removeFixture(1);
		
		TestCase.assertEquals(2, b.getFixtures().size());
		TestCase.assertTrue(f2 == f);
	}
	
	/**
	 * Tests the remove fixture method failure cases.
	 */
	@Test
	public void removeFixtureNotFound() {
		TestBody b = new TestBody();
		
		// test null fixture
		boolean success = b.removeFixture((Fixture)null);
		TestCase.assertFalse(success);
		
		// test not found fixture
		b.addFixture(Geometry.createCircle(1.0));
		success = b.removeFixture(new Fixture(Geometry.createRightTriangle(0.5, 0.3)));
		TestCase.assertFalse(success);
		TestCase.assertEquals(1, b.fixtures.size());
	}
	
	/**
	 * Tests removing fixtures by a world space point.
	 * @since 3.1.8
	 */
	@Test
	public void removeFixtureByPoint() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		Fixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test not in body
		bf = b.removeFixture(new Vector2(-1.0, -1.0));
		TestCase.assertNull(bf);
		TestCase.assertEquals(2, b.getFixtures().size());
		
		// confirm there are two fixtures at this location
		TestCase.assertEquals(2, b.getFixtures(new Vector2(0.5, 0.25)).size());
		// test remove the first one
		bf = b.removeFixture(new Vector2(0.5, 0.25));
		TestCase.assertNotNull(bf);
		TestCase.assertTrue(bf.getShape() instanceof Circle);
		TestCase.assertEquals(1, b.getFixtures().size());
		
		// add the fixture back
		bf = b.addFixture(Geometry.createCircle(1.0));
		
		// test not in body
		List<Fixture> bfs = b.removeFixtures(new Vector2(-1.0, -1.0));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(0, bfs.size());
		TestCase.assertEquals(2, b.getFixtures().size());
		
		// test in body remove one
		bfs = b.removeFixtures(new Vector2(1.25, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(1, bfs.size());
		TestCase.assertTrue(bfs.get(0).getShape() instanceof Polygon);
		TestCase.assertEquals(1, b.getFixtures().size());
		
		// add the fixture back
		bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test in body remove both
		bfs = b.removeFixtures(new Vector2(0.75, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(2, bfs.size());
		TestCase.assertEquals(0, b.getFixtures().size());
	}
	
	/**
	 * Tests receiving index out of bounds exceptions.
	 * @since 2.2.3
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeFixtureByIndexOutOfBounds() {
		TestBody b = new TestBody();
		// test index with empty fixture list
		b.removeFixture(0);
	}
	
	/**
	 * Tests receiving index out of bounds exceptions.
	 * @since 2.2.3
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeFixtureIndexNegative() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		
		// test negative index
		b.removeFixture(-2);
	}
	
	/**
	 * Tests the rotate methods.
	 * @since 4.0.0
	 */
	@Test
	public void rotate() {
		TestBody b = new TestBody();
		
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle());
		
		double r = Math.toRadians(30);
		
		b.rotate(r);
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, b.getTransform().getTranslationY());
		
		b.rotate(new Rotation(r));
		TestCase.assertEquals(2.0 * r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, b.getTransform().getTranslationY());

		b.getTransform().identity();
		b.rotate(r, 1.0, 1.0);
		
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.6339745962155612, b.getTransform().getTranslationX());
		TestCase.assertEquals(-0.3660254037844386, b.getTransform().getTranslationY());
		
		Vector2 rp = new Vector2(1.0, 1.0);
		b.getTransform().identity();
		b.rotate(r, rp);
		
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.6339745962155612, b.getTransform().getTranslationX());
		TestCase.assertEquals(-0.3660254037844386, b.getTransform().getTranslationY());

		b.getTransform().identity();
		b.rotate(new Rotation(r), 1.0, 1.0);
		
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.6339745962155612, b.getTransform().getTranslationX());
		TestCase.assertEquals(-0.3660254037844386, b.getTransform().getTranslationY());
		
		b.getTransform().identity();
		b.rotate(new Rotation(r), rp);
		
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(0.6339745962155612, b.getTransform().getTranslationX());
		TestCase.assertEquals(-0.3660254037844386, b.getTransform().getTranslationY());
		
		b.getTransform().identity();
		b.getTransform().translate(1.0, 1.0);
		b.rotateAboutCenter(r);
		TestCase.assertEquals(r, b.getTransform().getRotationAngle(), 1e-15);
		TestCase.assertEquals(1.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(1.0, b.getTransform().getTranslationY());
	}
	
	/**
	 * Tests the shiftCoordinates method.
	 */
	@Test
	public void shiftCoordinates() {
		TestBody b = new TestBody();
		
		b.shift(new Vector2(-2.0, 1.0));
		
		// it just translates the transform
		Vector2 tx = b.getTransform().getTranslation();
		TestCase.assertEquals(-2.0, tx.x, 1.0e-3);
		TestCase.assertEquals(1.0, tx.y, 1.0e-3);
	}
	
	/**
	 * Tests the translate methods.
	 */
	@Test
	public void translate() {
		TestBody b = new TestBody();
		
		TestCase.assertEquals(0.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, b.getTransform().getTranslationY());
		
		b.translate(1.0, 1.0);
		
		TestCase.assertEquals(1.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(1.0, b.getTransform().getTranslationY());
		
		Vector2 tx = new Vector2(2.0, -1.0);
		b.translate(tx);
		TestCase.assertEquals(3.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, b.getTransform().getTranslationY());
		
		b.translateToOrigin();
		
		TestCase.assertEquals(0.0, b.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, b.getTransform().getTranslationY());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		TestBody tb = new TestBody();
		tb.setEnabled(true);
		tb.setOwner(new Object());
		tb.setUserData(new Object());
		tb.addFixture(Geometry.createCircle(0.5));
		tb.addFixture(Geometry.createSquare(1));
		tb.fixtures.get(0).setFilter(new CategoryFilter(1, 3));
		tb.fixtures.get(0).setSensor(true);
		tb.fixtures.get(0).setUserData(new Object());
		tb.fixtures.get(1).setFilter(new CategoryFilter(2, 5));
		tb.fixtures.get(1).setSensor(true);
		tb.fixtures.get(1).setUserData(new Object());
		tb.setRotationDiscRadius(new Vector2(0, 0));
		tb.rotate(Math.toRadians(30), new Vector2(1, 1));
		tb.translate(2, 1);
		tb.getPreviousTransform().set(tb.getTransform());
		
		TestBody copy = tb.copy();
		
		TestCase.assertNotSame(tb, copy);
		TestCase.assertNotSame(tb.fixtures, copy.fixtures);
		TestCase.assertNotSame(tb.fixturesUnmodifiable, copy.fixturesUnmodifiable);
		TestCase.assertNotSame(tb.transform, copy.transform);
		TestCase.assertNotSame(tb.transform0, copy.transform0);
		TestCase.assertNotSame(copy.transform, copy.transform0);
		TestCase.assertNull(copy.owner);
		TestCase.assertNull(copy.userData);
		TestCase.assertNull(copy.fixtureModificationHandler);
		
		TestCase.assertEquals(tb.enabled, copy.enabled);
		TestCase.assertEquals(tb.radius, copy.radius);
		
		TestCase.assertEquals(tb.transform.getCost(), copy.transform.getCost());
		TestCase.assertEquals(tb.transform.getSint(), copy.transform.getSint());
		TestCase.assertEquals(tb.transform.getTranslationX(), copy.transform.getTranslationX());
		TestCase.assertEquals(tb.transform.getTranslationY(), copy.transform.getTranslationY());
		
		TestCase.assertEquals(tb.transform0.getCost(), copy.transform0.getCost());
		TestCase.assertEquals(tb.transform0.getSint(), copy.transform0.getSint());
		TestCase.assertEquals(tb.transform0.getTranslationX(), copy.transform0.getTranslationX());
		TestCase.assertEquals(tb.transform0.getTranslationY(), copy.transform0.getTranslationY());
		
		TestCase.assertEquals(tb.fixtures.size(), copy.fixtures.size());
		for (int i = 0; i < tb.fixtures.size(); i++) {
			Fixture fo = tb.getFixture(i);
			Fixture fc = copy.getFixture(i);
			
			TestCase.assertNotSame(fo, fc);
			TestCase.assertNotSame(fo.filter, fc.filter);
			TestCase.assertNotSame(fo.shape, fc.shape);
			TestCase.assertNull(fc.userData);
			
			TestCase.assertEquals(fo.sensor, fc.sensor);
		}
	}
	
	/**
	 * Tests the copy method with a fixture class that doesn't override copy.
	 */
	@Test(expected = ClassCastException.class)
	public void copyNotOverridden() {
		TestBody tb = new TestBody();
		tb.addFixture(new TestFixture(Geometry.createCircle(1.0)));
		
		tb.copy();
	}
}
