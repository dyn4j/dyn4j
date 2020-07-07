package org.dyn4j.world;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.LinkPostProcessor;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.BoundsListenerAdapter;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.listener.CollisionListenerAdapter;
import org.dyn4j.world.result.ConvexDetectResult;
import org.dyn4j.world.result.DetectResult;
import org.junit.Test;

import junit.framework.TestCase;

public class CollisionWorldTest {
	private class TestWorld extends AbstractCollisionWorld<Body, BodyFixture, WorldCollisionData<Body>> {

		public TestWorld() {
			super();
		}

		public TestWorld(int initialBodyCapacity) {
			super(initialBodyCapacity);
		}

		@Override
		protected WorldCollisionData<Body> createCollisionData(CollisionPair<Body, BodyFixture> pair) {
			return new WorldCollisionData<Body>(pair);
		}

		@Override
		protected void detectCollisions(Iterator<WorldCollisionData<Body>> iterator) {
			while (iterator.hasNext()) {
				WorldCollisionData<Body> collision = iterator.next();
				
			}
		}
	}
	
	/**
	 * Tests the successful creation of a world object.
	 */
	@Test
	public void createSuccess() {
		TestWorld w = new TestWorld();
		TestCase.assertNotNull(w.bodies);
		TestCase.assertNotNull(w.bodiesUnmodifiable);
		TestCase.assertNull(w.bounds);
		TestCase.assertNotNull(w.boundsListeners);
		TestCase.assertNotNull(w.broadphaseDetector);
		TestCase.assertNotNull(w.collisionData);
		TestCase.assertNotNull(w.collisionListeners);
		TestCase.assertNotNull(w.broadphaseFilter);
		TestCase.assertNotNull(w.manifoldSolver);
		TestCase.assertNotNull(w.narrowphaseDetector);
		TestCase.assertNotNull(w.narrowphasePostProcessor);
		TestCase.assertNotNull(w.raycastDetector);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertNull(w.userData);
		
		// test create with initial size
		w = new TestWorld(16);
		w = new TestWorld(-16);
		w = new TestWorld(0);
	}
	
	/**
	 * Tests the add body method.
	 */
	@Test
	public void addBody() {
		TestWorld w = new TestWorld();
		
		Body b = new Body();
		b.addFixture(Geometry.createCapsule(1.0, 0.5));
		
		TestCase.assertNull(b.getOwner());
		TestCase.assertNull(b.getFixtureModificationHandler());
		TestCase.assertEquals(0, w.bodies.size());
		
		w.addBody(b);
		TestCase.assertEquals(1, w.bodies.size());
		
		// make sure the body's world reference is there
		TestCase.assertNotNull(b.getOwner());
		TestCase.assertEquals(w, b.getOwner());
		TestCase.assertNotNull(b.getFixtureModificationHandler());
		
		// make sure it was added to the broadphase
		TestCase.assertTrue(w.broadphaseDetector.contains(b));
		TestCase.assertTrue(w.broadphaseDetector.contains(b, b.getFixture(0)));
	}

	/**
	 * Tests the add body method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullBody() {
		TestWorld w = new TestWorld();
		w.addBody((Body) null);
	}
	
	/**
	 * Tests the add body method attempting to add the
	 * same body more than once.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addSameBodyToSameWorld() {
		TestWorld w = new TestWorld();
		Body b1 = new Body();
		w.addBody(b1);
		w.addBody(b1);
	}

	/**
	 * Tests the add body method attempting to add the
	 * same body more than once.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addBodyFromOtherWorld() {
		TestWorld w1 = new TestWorld();
		TestWorld w2 = new TestWorld();
		Body b1 = new Body();
		w1.addBody(b1);
		w2.addBody(b1);
	}
	
	/**
	 * Tests the removeAllBodies method.
	 */
	@Test
	public void removeAllBodies() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		TestCase.assertNull(b1.getOwner());
		TestCase.assertNull(b1.getFixtureModificationHandler());
		TestCase.assertNull(b2.getOwner());
		TestCase.assertNull(b2.getFixtureModificationHandler());
		TestCase.assertEquals(0, w.bodies.size());
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		w.detect();
		
		TestCase.assertNotNull(b1.getOwner());
		TestCase.assertNotNull(b1.getFixtureModificationHandler());
		TestCase.assertNotNull(b2.getOwner());
		TestCase.assertNotNull(b2.getFixtureModificationHandler());
		TestCase.assertEquals(2, w.bodies.size());
		
		TestCase.assertTrue(w.broadphaseDetector.contains(b1));
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, b1.getFixture(0)));
		TestCase.assertTrue(w.broadphaseDetector.contains(b2));
		TestCase.assertTrue(w.broadphaseDetector.contains(b2, b2.getFixture(0)));
		
		TestCase.assertEquals(1, w.collisionData.size());
		
		w.removeAllBodies();
		
		TestCase.assertNull(b1.getOwner());
		TestCase.assertNull(b1.getFixtureModificationHandler());
		TestCase.assertNull(b2.getOwner());
		TestCase.assertNull(b2.getFixtureModificationHandler());
		TestCase.assertEquals(0, w.bodies.size());
		
		TestCase.assertFalse(w.broadphaseDetector.contains(b1));
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, b1.getFixture(0)));
		TestCase.assertFalse(w.broadphaseDetector.contains(b2));
		TestCase.assertFalse(w.broadphaseDetector.contains(b2, b2.getFixture(0)));
		
		TestCase.assertEquals(0, w.collisionData.size());
	}
	
	/**
	 * Tests the containsBody method.
	 */
	@Test
	public void containsBody() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);

		TestCase.assertTrue(w.containsBody(b1));
		TestCase.assertTrue(w.containsBody(b2));
		TestCase.assertFalse(w.containsBody(null));
		TestCase.assertFalse(w.containsBody(new Body()));
	}

	/**
	 * Tests the removeBody method w/ a negative index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeBodyNegativeIndex() {
		TestWorld w = new TestWorld();
		w.removeBody(-1);
	}
	
	/**
	 * Tests the removeBody method w/ a non-existent index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeBodyBadIndex() {
		TestWorld w = new TestWorld();
		w.removeBody(2);
	}
	
	/**
	 * Tests the removeBody method.
	 */
	@Test
	public void removeBody() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		w.detect();
		
		// test removing a null body
		TestCase.assertFalse(w.removeBody((Body) null));
		
		// test removing a body not in the list
		TestCase.assertFalse(w.removeBody(new Body()));
		
		TestCase.assertTrue(w.removeBody(b1));
		
		TestCase.assertNull(b1.getOwner());
		TestCase.assertNull(b1.getFixtureModificationHandler());
		TestCase.assertNotNull(b2.getOwner());
		TestCase.assertNotNull(b2.getFixtureModificationHandler());
		TestCase.assertEquals(1, w.bodies.size());
		
		TestCase.assertFalse(w.broadphaseDetector.contains(b1));
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, b1.getFixture(0)));
		TestCase.assertTrue(w.broadphaseDetector.contains(b2));
		TestCase.assertTrue(w.broadphaseDetector.contains(b2, b2.getFixture(0)));
		
		// NOTE: collision data will be removed in the next iteration
		TestCase.assertEquals(1, w.collisionData.size());
		
		w.detect();
		
		// it should be removed now
		TestCase.assertEquals(0, w.collisionData.size());
		
		TestCase.assertTrue(w.removeBody(0));
		TestCase.assertNull(b2.getOwner());
		TestCase.assertNull(b2.getFixtureModificationHandler());
		TestCase.assertEquals(0, w.bodies.size());
		
		TestCase.assertFalse(w.broadphaseDetector.contains(b2));
		TestCase.assertFalse(w.broadphaseDetector.contains(b2, b2.getFixture(0)));
	}
	
	/**
	 * Tests the getBodies method.
	 */
	@Test
	public void getBodies() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		TestCase.assertEquals(2, w.getBodies().size());
	}
	
	/**
	 * Tests the getBodies method when trying to remove an item.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodiesAndRemove() {
		TestWorld w = new TestWorld();
		w.getBodies().remove(0);
	}
	
	/**
	 * Tests the getBody w/ index method.
	 */
	@Test
	public void getBodiesAtIndex() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		TestCase.assertEquals(b1, w.getBody(0));
		TestCase.assertEquals(b2, w.getBody(1));
	}
	
	/**
	 * Tests the getBody method w/ a negative index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getBodiesAtNegativeIndex() {
		TestWorld w = new TestWorld();
		w.getBody(-1);
	}
	
	/**
	 * Tests the getBody method w/ a non-existent index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getBodyBadIndex() {
		TestWorld w = new TestWorld();
		w.getBody(2);
	}
	
	/**
	 * Tests the getBodies method.
	 */
	@Test
	public void getBodyCount() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		TestCase.assertEquals(2, w.getBodyCount());
	}
	
	/**
	 * Tests the isEmpty method.
	 */
	@Test
	public void isEmpty() {
		TestWorld w = new TestWorld();
		
		TestCase.assertTrue(w.isEmpty());
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		TestCase.assertFalse(w.isEmpty());
	}
	
	/**
	 * Tests the getBodyIterator method.
	 */
	@Test
	public void getBodyIterator() {
		TestWorld w = new TestWorld();
		
		TestCase.assertNotNull(w.getBodyIterator());
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		TestCase.assertNotNull(w.getBodyIterator());
		
		int i = 0;
		Iterator<Body> it = w.getBodyIterator();
		while (it.hasNext()) {
			TestCase.assertNotNull(it.next());
			i++;
		}
		
		TestCase.assertEquals(w.getBodyCount(), i);
		
		i = 0;
		it = w.getBodyIterator();
		while (it.hasNext()) {
			i++;
			it.next();
			it.remove();
			TestCase.assertEquals(2 - i, w.getBodyCount());
		}
	}
	
	/**
	 * Tests the getBodyIterator method.
	 */
	@Test
	public void getBodyIteratorFailure() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// test overflow
		Iterator<Body> it = w.getBodyIterator();
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
		it = w.getBodyIterator();
		try {
			it.remove();
			TestCase.fail();
		} catch (IllegalStateException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test second remove
		it = w.getBodyIterator();
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
		it = w.getBodyIterator();
		try {
			it.next();
			w.removeAllBodies();
			it.remove();
			TestCase.fail();
		} catch (IndexOutOfBoundsException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}
	
	/**
	 * Tests the getBounds and setBounds methods.
	 */
	@Test
	public void getAndSetBounds() {
		TestWorld w = new TestWorld();
		TestCase.assertNull(w.getBounds());
		
		AxisAlignedBounds bounds = new AxisAlignedBounds(10, 10);
		w.setBounds(bounds);
		
		TestCase.assertNotNull(w.getBounds());
		TestCase.assertEquals(bounds, w.getBounds());
		
		w.setBounds(null);
	}
	
	/**
	 * Tests the set broadphase detector method.
	 */
	@Test
	public void getAndSetBroadphaseDetector() {
		TestWorld w = new TestWorld();
		
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		w.addBody(b);
		
		BroadphaseDetector<Body, BodyFixture> original = w.getBroadphaseDetector();
		TestCase.assertNotNull(original);
		
		BroadphaseDetector<Body, BodyFixture> bd = new Sap<Body, BodyFixture>();
		w.setBroadphaseDetector(bd);
		TestCase.assertSame(bd, w.getBroadphaseDetector());
		TestCase.assertNotSame(original, w.getBroadphaseDetector());
		
		// test bodies are re-added
		TestCase.assertTrue(w.broadphaseDetector.contains(b));
		TestCase.assertTrue(w.broadphaseDetector.contains(b, b.getFixture(0)));
	}
	
	/**
	 * Tests the set broadphase detector method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBroadphaseDetector() {
		TestWorld w = new TestWorld();
		w.setBroadphaseDetector(null);
	}
	
	/**
	 * Tests the get/set broadphase filter methods.
	 */
	@Test
	public void getAndSetBroadphaseFilter() {
		TestWorld w = new TestWorld();
		
		BroadphaseFilter<Body, BodyFixture> original = w.getBroadphaseFilter();
		TestCase.assertNotNull(original);
		
		CollisionBodyBroadphaseFilter<Body, BodyFixture> filter = new CollisionBodyBroadphaseFilter<Body, BodyFixture>();
		w.setBroadphaseFilter(filter);
		
		TestCase.assertSame(filter, w.getBroadphaseFilter());
		TestCase.assertNotSame(original, w.getBroadphaseFilter());
	}
	
	/**
	 * Tests the set broadphase filter method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBroadphaseFilter() {
		TestWorld w = new TestWorld();
		w.setBroadphaseFilter(null);
	}
	
	/**
	 * Tests the set narrowphase detector method.
	 */
	@Test
	public void getAndSetNarrowphaseDetector() {
		TestWorld w = new TestWorld();
		
		NarrowphaseDetector original = w.getNarrowphaseDetector();
		TestCase.assertNotNull(original);
		
		NarrowphaseDetector nd = new Gjk();
		w.setNarrowphaseDetector(nd);
		
		TestCase.assertSame(nd, w.getNarrowphaseDetector());
		TestCase.assertNotSame(original, w.getNarrowphaseDetector());
	}
	
	/**
	 * Tests the set narrowphase detector method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullNarrowphaseDetector() {
		TestWorld w = new TestWorld();
		w.setNarrowphaseDetector(null);
	}

	/**
	 * Tests the get/set narrowphase post processor methods.
	 */
	@Test
	public void getAndSetNarrowphasePostProcessor() {
		TestWorld w = new TestWorld();
		
		NarrowphasePostProcessor original = w.getNarrowphasePostProcessor();
		TestCase.assertNotNull(original);
		
		NarrowphasePostProcessor npp = new LinkPostProcessor();
		w.setNarrowphasePostProcessor(npp);
		
		TestCase.assertSame(npp, w.getNarrowphasePostProcessor());
		TestCase.assertNotSame(original, w.getNarrowphasePostProcessor());
	}
	
	/**
	 * Tests the set narrowphase post processor method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullNarrowphasePostProcessor() {
		TestWorld w = new TestWorld();
		w.setNarrowphasePostProcessor(null);
	}
	
	/**
	 * Tests the set manifold solver method.
	 */
	@Test
	public void setManifoldSolver() {
		TestWorld w = new TestWorld();
		
		ManifoldSolver original = w.getManifoldSolver();
		TestCase.assertNotNull(original);
		
		ManifoldSolver ms = new ClippingManifoldSolver();
		w.setManifoldSolver(ms);
		
		TestCase.assertSame(ms, w.getManifoldSolver());
		TestCase.assertNotSame(original, w.getManifoldSolver());
	}
	
	/**
	 * Tests the set manifold solver method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullManifoldSolver() {
		TestWorld w = new TestWorld();
		w.setManifoldSolver(null);
	}
	
	/**
	 * Tests the set time of impact detector method.
	 * @since 1.2.0
	 */
	@Test
	public void setTimeOfImpactDetector() {
		TestWorld w = new TestWorld();
		
		TimeOfImpactDetector original = w.getTimeOfImpactDetector();
		TestCase.assertNotNull(original);
		
		TimeOfImpactDetector toid = new ConservativeAdvancement();
		w.setTimeOfImpactDetector(toid);
		
		TestCase.assertSame(toid, w.getTimeOfImpactDetector());
		TestCase.assertNotSame(original, w.getTimeOfImpactDetector());
	}
	
	/**
	 * Tests the set time of impact detector method passing a null value.
	 * @since 1.2.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTimeOfImpactDetector() {
		TestWorld w = new TestWorld();
		w.setTimeOfImpactDetector(null);
	}
	
	/**
	 * Tests the set raycast detector method.
	 * @since 2.0.0
	 */
	@Test
	public void setRaycastDetector() {
		TestWorld w = new TestWorld();
		w.setRaycastDetector(new Gjk());
		
		RaycastDetector original = w.getRaycastDetector();
		TestCase.assertNotNull(original);
		
		RaycastDetector rd = new Gjk();
		w.setRaycastDetector(rd);
		
		TestCase.assertSame(rd, w.getRaycastDetector());
		TestCase.assertNotSame(original, w.getRaycastDetector());
	}
	
	/**
	 * Tests the set raycast detector method passing a null value.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullRaycastDetector() {
		TestWorld w = new TestWorld();
		w.setRaycastDetector(null);
	}
	
	/**
	 * Test the shift method.
	 */
	@Test
	public void shift() {
		TestWorld w = new TestWorld();
		AxisAlignedBounds bounds = new AxisAlignedBounds(20, 20);
		w.setBounds(bounds);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		AABB aabb1 = w.broadphaseDetector.getAABB(b1, b1.getFixture(0)).copy();
		AABB aabb2 = w.broadphaseDetector.getAABB(b2, b2.getFixture(0)).copy();
		
		w.detect();
		
		WorldCollisionData<Body> data = w.getCollisionData(b1, b1.getFixture(0), b2, b2.getFixture(0));
		Manifold m0 = data.getManifold().copy();
		
		w.shift(new Vector2(1.0, 1.0));
		
		// shift bodies
		TestCase.assertEquals(1.0, b1.getWorldCenter().x);
		TestCase.assertEquals(1.0, b1.getWorldCenter().y);
		TestCase.assertEquals(1.0, b2.getWorldCenter().x);
		TestCase.assertEquals(1.0, b2.getWorldCenter().y);
		
		// shift AABBs in broadphase
		TestCase.assertEquals(aabb1.getMinX() + 1.0, w.broadphaseDetector.getAABB(b1, b1.getFixture(0)).getMinX());
		TestCase.assertEquals(aabb1.getMinY() + 1.0, w.broadphaseDetector.getAABB(b1, b1.getFixture(0)).getMinY());
		TestCase.assertEquals(aabb1.getMaxX() + 1.0, w.broadphaseDetector.getAABB(b1, b1.getFixture(0)).getMaxX());
		TestCase.assertEquals(aabb1.getMaxY() + 1.0, w.broadphaseDetector.getAABB(b1, b1.getFixture(0)).getMaxY());
		TestCase.assertEquals(aabb2.getMinX() + 1.0, w.broadphaseDetector.getAABB(b2, b2.getFixture(0)).getMinX());
		TestCase.assertEquals(aabb2.getMinY() + 1.0, w.broadphaseDetector.getAABB(b2, b2.getFixture(0)).getMinY());
		TestCase.assertEquals(aabb2.getMaxX() + 1.0, w.broadphaseDetector.getAABB(b2, b2.getFixture(0)).getMaxX());
		TestCase.assertEquals(aabb2.getMaxY() + 1.0, w.broadphaseDetector.getAABB(b2, b2.getFixture(0)).getMaxY());
		
		// shift bounds
		TestCase.assertEquals(1.0, bounds.getTranslation().x);
		TestCase.assertEquals(1.0, bounds.getTranslation().y);
		
		// shift manifolds
		TestCase.assertEquals(m0.getPoints().size(), data.getManifold().getPoints().size());
		TestCase.assertEquals(m0.getPoints().get(0).getPoint().x + 1.0, data.getManifold().getPoints().get(0).getPoint().x);
		TestCase.assertEquals(m0.getPoints().get(0).getPoint().y + 1.0, data.getManifold().getPoints().get(0).getPoint().y);
	}
	
	/**
	 * Test the getCollisionData method.
	 */
	@Test
	public void getCollisionData() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Convex c3 = Geometry.createCircle(2.0);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		Body b3 = new Body(); b3.addFixture(c3); b3.setMass(MassType.INFINITE); b3.translate(-5.0, 0.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		w.addBody(b3);
		
		w.detect();
		
		WorldCollisionData<Body> data = w.getCollisionData(b1, b1.getFixture(0), b2, b2.getFixture(0));
		TestCase.assertNotNull(data);
		TestCase.assertNotNull(data.getPair());
		TestCase.assertTrue(data.getBody1() == b1 || data.getBody1() == b2);
		TestCase.assertTrue(data.getBody2() == b1 || data.getBody2() == b2);
		TestCase.assertTrue(data.getFixture1() == b1.getFixture(0) || data.getFixture1() == b2.getFixture(0));
		TestCase.assertTrue(data.getFixture2() == b1.getFixture(0) || data.getFixture2() == b2.getFixture(0));
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertNotNull(data.getPenetration());
		TestCase.assertNotNull(data.getManifold());
		TestCase.assertTrue(data.getManifold().getNormal().x != 0 || data.getManifold().getNormal().y != 0);
		TestCase.assertEquals(1, data.getManifold().getPoints().size());
		
		// test non-existent collision
		data = w.getCollisionData(b1, b1.getFixture(0), b3, b3.getFixture(0));
		TestCase.assertNull(data);
		
		// test body/fixture not in world
		data = w.getCollisionData(new Body(), b1.getFixture(0), b2, b2.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(b1, b1.getFixture(0), new Body(), b2.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(b1, new BodyFixture(Geometry.createCircle(0.5)), b2, b2.getFixture(0));
		TestCase.assertNull(data);
		
		// test null
		data = w.getCollisionData(null, b1.getFixture(0), new Body(), b2.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(b1, b1.getFixture(0), null, b2.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(null, b1.getFixture(0), null, b2.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(null, null, null, b3.getFixture(0));
		TestCase.assertNull(data);
		data = w.getCollisionData(null, null, null, null);
		TestCase.assertNull(data);
	}

	/**
	 * Test the getCollisionDataIterator method.
	 */
	@Test
	public void getCollisionDataIterator() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		w.detect();
		
		// test the iterator
		Iterator<WorldCollisionData<Body>> it = w.getCollisionDataIterator();
		
		TestCase.assertTrue(it.hasNext());
		
		WorldCollisionData<Body> data = it.next();
		TestCase.assertNotNull(data);
		TestCase.assertNotNull(data.getPair());
		TestCase.assertTrue(data.getBody1() == b1 || data.getBody1() == b2);
		TestCase.assertTrue(data.getBody2() == b1 || data.getBody2() == b2);
		TestCase.assertTrue(data.getFixture1() == b1.getFixture(0) || data.getFixture1() == b2.getFixture(0));
		TestCase.assertTrue(data.getFixture2() == b1.getFixture(0) || data.getFixture2() == b2.getFixture(0));
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertNotNull(data.getPenetration());
		TestCase.assertNotNull(data.getManifold());
		TestCase.assertTrue(data.getManifold().getNormal().x != 0 || data.getManifold().getNormal().y != 0);
		TestCase.assertEquals(1, data.getManifold().getPoints().size());
		
		// test with a broadphase only collision
		Convex c3 = Geometry.createCircle(1.0);
		Body b3 = new Body(); b3.addFixture(c3); b3.setMass(MassType.INFINITE); b3.translate(-5.0, 0.0);
		Convex c4 = Geometry.createCircle(1.0);
		Body b4 = new Body(); b4.addFixture(c4); b4.setMass(MassType.INFINITE); b4.translate(-3.5, 1.5);
		
		w.addBody(b3);
		w.addBody(b4);
		
		w.detect();
		
		it = w.getCollisionDataIterator();
		
		TestCase.assertTrue(it.hasNext());
		
		data = it.next();
		TestCase.assertNotNull(data);
		TestCase.assertNotNull(data.getPair());
		TestCase.assertTrue(data.getBody1() == b1 || data.getBody1() == b2);
		TestCase.assertTrue(data.getBody2() == b1 || data.getBody2() == b2);
		TestCase.assertTrue(data.getFixture1() == b1.getFixture(0) || data.getFixture1() == b2.getFixture(0));
		TestCase.assertTrue(data.getFixture2() == b1.getFixture(0) || data.getFixture2() == b2.getFixture(0));
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertNotNull(data.getPenetration());
		TestCase.assertNotNull(data.getManifold());
		TestCase.assertTrue(data.getManifold().getNormal().x != 0 || data.getManifold().getNormal().y != 0);
		TestCase.assertEquals(1, data.getManifold().getPoints().size());
		
		TestCase.assertTrue(it.hasNext());
		
		data = it.next();
		TestCase.assertNotNull(data);
		TestCase.assertNotNull(data.getPair());
		TestCase.assertTrue(data.getBody1() == b3 || data.getBody1() == b4);
		TestCase.assertTrue(data.getBody2() == b3 || data.getBody2() == b4);
		TestCase.assertTrue(data.getFixture1() == b3.getFixture(0) || data.getFixture1() == b4.getFixture(0));
		TestCase.assertTrue(data.getFixture2() == b3.getFixture(0) || data.getFixture2() == b4.getFixture(0));
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertFalse(data.isNarrowphaseCollision());
		TestCase.assertFalse(data.isManifoldCollision());
		
		// test removal and filtering
		w.removeBody(b3);
		
		TestCase.assertEquals(2, w.collisionData.size());
		
		data = w.getCollisionData(b3, b3.getFixture(0), b4, b4.getFixture(0));
		TestCase.assertNull(data);
		
		it = w.getCollisionDataIterator();
		TestCase.assertTrue(it.hasNext());
		data = it.next();
		TestCase.assertFalse(it.hasNext());
		
		w.addBody(b3);
		w.detect();
		w.removeBody(b4);
		
		TestCase.assertEquals(2, w.collisionData.size());
		
		data = w.getCollisionData(b3, b3.getFixture(0), b4, b4.getFixture(0));
		TestCase.assertNull(data);
		
		it = w.getCollisionDataIterator();
		TestCase.assertTrue(it.hasNext());
		data = it.next();
		TestCase.assertFalse(it.hasNext());
	}
	
	/**
	 * Test the getCollisionDataIterator remove method.
	 */
	@Test
	public void getCollisionDataIteratorFailure() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		w.detect();
		
		Iterator<WorldCollisionData<Body>> it = w.getCollisionDataIterator();
		it.next();
		
		// test remove
		try {
			it.remove();
			TestCase.fail();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test next-ing after completion
		try {
			it.next();
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}

	/**
	 * Test the fixture modification handler.
	 */
	@Test
	public void fixtureModification() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);

		Body b1 = new Body(); 
		BodyFixture f1 = b1.addFixture(c1);
		
		// add them to the world
		w.addBody(b1);
		
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f1));
		
		BodyFixture f2 = b1.addFixture(c2);
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f2));
		
		b1.removeFixture(f1);
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f2));
		
		b1.addFixture(f1);
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f2));
		
		b1.removeAllFixtures();
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f2));
	}
	
	/**
	 * Test the various listener methods (add, remove, remove all, get).
	 */
	@Test
	public void listeners() {
		TestWorld w = new TestWorld();
		
		BoundsListener<Body, BodyFixture> bl = new BoundsListenerAdapter<Body, BodyFixture>();
		CollisionListener<Body, BodyFixture> cl = new CollisionListenerAdapter<Body, BodyFixture>();
		
		w.addBoundsListener(bl);
		w.addCollisionListener(cl);
		
		TestCase.assertEquals(1, w.boundsListeners.size());
		TestCase.assertEquals(1, w.collisionListeners.size());
		
		List<BoundsListener<Body, BodyFixture>> bls = w.getBoundsListeners();
		List<CollisionListener<Body, BodyFixture>> cls = w.getCollisionListeners();
		
		TestCase.assertEquals(1, bls.size());
		TestCase.assertEquals(1, cls.size());
		
		TestCase.assertEquals(bl, bls.get(0));
		TestCase.assertEquals(cl, cls.get(0));
		
		// confirm duplicates are allowed
		w.addBoundsListener(bl);
		w.addCollisionListener(cl);
		
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		
		TestCase.assertEquals(2, bls.size());
		TestCase.assertEquals(2, cls.size());
		
		w.removeBoundsListener(bl);
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		TestCase.assertEquals(1, bls.size());
		TestCase.assertEquals(2, cls.size());
		
		w.removeCollisionListener(cl);
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		TestCase.assertEquals(1, bls.size());
		TestCase.assertEquals(1, cls.size());		
		
		w.removeAllBoundsListeners();
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		TestCase.assertEquals(0, bls.size());
		TestCase.assertEquals(1, cls.size());
		
		w.removeAllCollisionListeners();
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		TestCase.assertEquals(0, bls.size());
		TestCase.assertEquals(0, cls.size());
		
		w.addBoundsListener(bl);
		w.addBoundsListener(bl);
		w.addCollisionListener(cl);
		w.removeAllListeners();
		bls = w.getBoundsListeners();
		cls = w.getCollisionListeners();
		TestCase.assertEquals(0, bls.size());
		TestCase.assertEquals(0, cls.size());
	}
	
	/**
	 * Tests the get/set of user data;
	 */
	@Test
	public void getSetUserData() {
		TestWorld w = new TestWorld();
		
		TestCase.assertNull(w.userData);
		
		Object obj = new Object();
		w.setUserData(obj);
		
		TestCase.assertNotNull(w.getUserData());
		TestCase.assertEquals(obj, w.getUserData());
		
		w.setUserData(null);
		
		TestCase.assertNull(w.getUserData());
	}
	
	/**
	 * Tests the detect AABB methods.
	 */
	@Test
	public void detectAABB() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		long m1 = 1;
		long m2 = 2;
		long m3 = 4;
		CategoryFilter cf = new CategoryFilter(m1, m1 | m2);
		b1.getFixture(0).setFilter(cf);
		b2.getFixture(0).setFilter(cf);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				null);
		
		// test standard detect
		AABB aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		List<DetectResult<Body, BodyFixture>> results = w.detect(aabb, filter);
		TestCase.assertEquals(2, results.size());
		
		// test something disabled
		b1.setEnabled(false);
		results = w.detect(aabb, filter);
		TestCase.assertEquals(1, results.size());
		
		// test a sensor fixture
		b2.getFixture(0).setSensor(true);
		results = w.detect(aabb, filter);
		TestCase.assertEquals(0, results.size());
		
		b1.setEnabled(true);
		b2.getFixture(0).setSensor(false);
		
		// test a smaller AABB region
		aabb = new AABB(-2.0, 1.0, -1.0, 2.0);
		results = w.detect(aabb, filter);
		TestCase.assertEquals(1, results.size());
		
		// test filter
		CategoryFilter cf2 = new CategoryFilter(m3, m3);
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf2);
		
		aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		results = w.detect(aabb, filter);
		TestCase.assertEquals(0, results.size());
	}
	
	/**
	 * Tests the detect AABB (against a body) methods.
	 */
	@Test
	public void detectAABBvsBody() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		long m1 = 1;
		long m2 = 2;
		long m3 = 4;
		CategoryFilter cf = new CategoryFilter(m1, m1 | m2);
		b1.getFixture(0).setFilter(cf);
		b2.getFixture(0).setFilter(cf);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				null);
		
		// test standard detect
		AABB aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		List<DetectResult<Body, BodyFixture>> results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(1, results.size());
		
		// test something disabled
		b1.setEnabled(false);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(0, results.size());
		b1.setEnabled(true);
		
		// test a sensor fixture
		b1.getFixture(0).setSensor(true);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(0, results.size());
		b1.getFixture(0).setSensor(false);
		
		// test a smaller AABB region
		aabb = new AABB(-2.0, 1.0, -1.0, 2.0);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(1, results.size());
		
		// test a smaller AABB region 2
		aabb = new AABB(0.0, -2.0, 2.0, 0.0);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(0, results.size());
		
		// test filter
		CategoryFilter cf2 = new CategoryFilter(m3, m3);
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf2);
		
		aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(0, results.size());
		
		// test filter 2
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf);
		
		aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		results = w.detect(aabb, b1, filter);
		TestCase.assertEquals(1, results.size());
	}

	/**
	 * Tests the detect AABB iterator method where we call hasNext many times 
	 * before calling the next method to ensure that the next() method is the
	 * only way to advance the iteration.
	 */
	@Test
	public void detectAABBIterator() {
		TestWorld w = new TestWorld();
		AABB aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		
		// test getting an iterator for an empty world
		Iterator<DetectResult<Body, BodyFixture>> it = w.detectIterator(aabb, null);
		TestCase.assertFalse(it.hasNext());
		
		it = w.detectIterator(aabb, new Body(), null);
		TestCase.assertFalse(it.hasNext());
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// since the list based methods are built on top of the iterator methods
		// we only need to test the failure cases for the iterator methods
		
		it = w.detectIterator(aabb, null);
		for (int i = 0; i < 20; i++) {
			it.hasNext();
		}
		
		it.next();
		
		it = w.detectIterator(aabb, b1, null);
		for (int i = 0; i < 20; i++) {
			it.hasNext();
		}
		
		it.next();
	}

	/**
	 * Tests the detect AABB iterator method.
	 */
	@Test
	public void detectAABBIteratorFailures() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// since the list based methods are built on top of the iterator methods
		// we only need to test the failure cases for the iterator methods
		
		AABB aabb = new AABB(-2.0, -2.0, 2.0, 2.0);
		Iterator<DetectResult<Body, BodyFixture>> it = w.detectIterator(aabb, null);
		
		// test remove method
		try {
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			TestCase.fail();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test body remove method
		it = w.detectIterator(aabb, b1, null);
		try {
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			TestCase.fail();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test not checking hasNext
		it = w.detectIterator(aabb, null);
		try {
			for (int i = 0; i < 10; i++) {
				it.next();
			}
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test body not checking hasNext
		it = w.detectIterator(aabb, b1, null);
		try {
			for (int i = 0; i < 10; i++) {
				it.next();
			}
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}
	
	/**
	 * Tests the detect convex methods.
	 */
	@Test
	public void detectConvex() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		long m1 = 1;
		long m2 = 2;
		long m3 = 4;
		CategoryFilter cf = new CategoryFilter(m1, m1 | m2);
		b1.getFixture(0).setFilter(cf);
		b2.getFixture(0).setFilter(cf);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				null);
		
		Convex convex = Geometry.createCircle(0.5);
		Transform tx = new Transform();
		tx.translate(-0.5, 0.0);
		
		// test standard detect
		List<ConvexDetectResult<Body, BodyFixture>> results = w.detect(convex, tx, filter);
		TestCase.assertEquals(2, results.size());
		
		// test something disabled
		b1.setEnabled(false);
		results = w.detect(convex, tx, filter);
		TestCase.assertEquals(1, results.size());
		
		// test a sensor fixture
		b2.getFixture(0).setSensor(true);
		results = w.detect(convex, tx, filter);
		TestCase.assertEquals(0, results.size());
		
		b1.setEnabled(true);
		b2.getFixture(0).setSensor(false);
		
		// test filter
		CategoryFilter cf2 = new CategoryFilter(m3, m3);
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf2);
		
		results = w.detect(convex, tx, filter);
		TestCase.assertEquals(0, results.size());

		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				null);
		
		// test a smaller AABB region
		convex = Geometry.createCircle(0.25);
		tx.identity();
		results = w.detect(convex, tx, filter);
		TestCase.assertEquals(1, results.size());
	}
	
	
	/**
	 * Tests the detect convex (against a body) methods.
	 */
	@Test
	public void detectConvexvsBody() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		long m1 = 1;
		long m2 = 2;
		long m3 = 4;
		CategoryFilter cf = new CategoryFilter(m1, m1 | m2);
		b1.getFixture(0).setFilter(cf);
		b2.getFixture(0).setFilter(cf);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				null);
		
		Convex convex = Geometry.createCircle(0.5);
		Transform tx = new Transform();
		tx.translate(-0.5, 0.0);
		
		// test standard detect
		List<ConvexDetectResult<Body, BodyFixture>> results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(1, results.size());
		
		// test something disabled
		b1.setEnabled(false);
		results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(0, results.size());
		b1.setEnabled(true);
		
		// test a sensor fixture
		b1.getFixture(0).setSensor(true);
		results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(0, results.size());
		b1.getFixture(0).setSensor(false);
		
		// test a smaller AABB region
		convex = Geometry.createCircle(0.25);
		results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(0, results.size());
		
		// test a smaller AABB region 2
		tx.identity();
		results = w.detect(convex, tx, b2, filter);
		TestCase.assertEquals(1, results.size());
		
		// test filter
		CategoryFilter cf2 = new CategoryFilter(m3, m3);
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf2);
		
		convex = Geometry.createCircle(0.5);
		results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(0, results.size());
		
		// test filter 2
		filter = new DetectFilter<Body, BodyFixture>(
				true,
				true,
				cf);
		
		results = w.detect(convex, tx, b1, filter);
		TestCase.assertEquals(1, results.size());
	}

	/**
	 * Tests the detect AABB iterator method where we call hasNext many times 
	 * before calling the next method to ensure that the next() method is the
	 * only way to advance the iteration.
	 */
	@Test
	public void detectContexIterator() {
		TestWorld w = new TestWorld();
		
		Convex convex = Geometry.createCircle(0.5);
		Transform tx = new Transform();
		tx.translate(-0.5, 0.0);
		
		// test getting an iterator for an empty world
		Iterator<ConvexDetectResult<Body, BodyFixture>> it = w.detectIterator(convex, tx, null);
		TestCase.assertFalse(it.hasNext());
		
		it = w.detectIterator(convex, tx, new Body(), null);
		TestCase.assertFalse(it.hasNext());
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// since the list based methods are built on top of the iterator methods
		// we only need to test the failure cases for the iterator methods
		
		it = w.detectIterator(convex, tx, null);
		for (int i = 0; i < 20; i++) {
			it.hasNext();
		}
		
		it.next();
		
		it = w.detectIterator(convex, tx, b1, null);
		for (int i = 0; i < 20; i++) {
			it.hasNext();
		}
		
		it.next();
	}

	/**
	 * Tests the detect AABB iterator method.
	 */
	@Test
	public void detectConvexIteratorFailures() {
		TestWorld w = new TestWorld();

		// setup the bodies
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 0.5);

		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		b1.translate(-1.0, 1.0);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// since the list based methods are built on top of the iterator methods
		// we only need to test the failure cases for the iterator methods
		
		Convex convex = Geometry.createCircle(0.5);
		Transform tx = new Transform();
		tx.translate(-0.5, 0.0);
		
		Iterator<ConvexDetectResult<Body, BodyFixture>> it = w.detectIterator(convex, tx, null);
		
		// test remove method
		try {
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			TestCase.fail();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test body remove method
		it = w.detectIterator(convex, tx, b1, null);
		try {
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			TestCase.fail();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test not checking hasNext
		it = w.detectIterator(convex, tx, null);
		try {
			for (int i = 0; i < 10; i++) {
				it.next();
			}
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test body not checking hasNext
		it = w.detectIterator(convex, tx, b1, null);
		try {
			for (int i = 0; i < 10; i++) {
				it.next();
			}
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}
	
	
	// TODO detection methods
}
