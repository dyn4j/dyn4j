package org.dyn4j.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.FixtureModificationHandler;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.LinkPostProcessor;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.result.ConvexCastResult;
import org.dyn4j.world.result.ConvexDetectResult;
import org.dyn4j.world.result.DetectResult;
import org.dyn4j.world.result.RaycastResult;

public abstract class AbstractCollisionWorld<T extends CollisionBody<E>, E extends Fixture, V extends CollisionData<T, E>> implements CollisionWorld<T, E, V>, Shiftable {
	
	// algorithms
	
	/** The world {@link Bounds} */
	protected Bounds bounds;
	
	/** The {@link BroadphaseDetector} */
	protected BroadphaseDetector<T, E> broadphaseDetector;
	
	/** The {@link BroadphaseFilter} for detection */
	protected BroadphaseFilter<T, E> detectBroadphaseFilter;
	
	/** The {@link NarrowphaseDetector} */
	protected NarrowphaseDetector narrowphaseDetector;
	
	/** The {@link NarrowphasePostProcessor} */
	protected NarrowphasePostProcessor narrowphasePostProcessor;
	
	/** The {@link ManifoldSolver} */
	protected ManifoldSolver manifoldSolver;
	
	/** The {@link RaycastDetector} */
	protected RaycastDetector raycastDetector;

	/** The {@link TimeOfImpactDetector} */
	protected TimeOfImpactDetector timeOfImpactDetector;
	
	// members
	
	/** The list of all bodies in the world */
	protected final List<T> bodies;
	
	// collision tracking
	
	/** The full set of tracked collision data */
	protected final Map<CollisionPair<T, E>, V> collisionData;
	
	/** An unmodifiable view of the collision data */
	protected final Map<CollisionPair<T, E>, V> unmodifiableCollisionData;
	
	// listeners
	
	protected final List<CollisionListener<T, E>> collisionListeners;
	
	protected final List<BoundsListener<T, E>> boundsListeners;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses the {@link CollisionWorld#DEFAULT_BODY_COUNT} as the initial capacity.
	 */
	public AbstractCollisionWorld() {
		this(DEFAULT_BODY_COUNT);
	}
	
	/**
	 * Optional constructor.
	 * @param initialBodyCapacity the default initial body capacity
	 */
	public AbstractCollisionWorld(int initialBodyCapacity) {
		this.bounds = null;
		this.broadphaseDetector = new DynamicAABBTree<T, E>(initialBodyCapacity);
		this.detectBroadphaseFilter = new DefaultBroadphaseFilter<T, E>();
		this.narrowphaseDetector = new Gjk();
		this.narrowphasePostProcessor = new LinkPostProcessor();
		this.manifoldSolver = new ClippingManifoldSolver();
		this.raycastDetector = new Gjk();
		this.timeOfImpactDetector = new ConservativeAdvancement();
		
		this.bodies = new ArrayList<T>(initialBodyCapacity);
		
		this.collisionData = new LinkedHashMap<CollisionPair<T, E>, V>(Collisions.getEstimatedCollisionPairs(initialBodyCapacity));
		this.unmodifiableCollisionData = Collections.unmodifiableMap(this.collisionData);
		
		this.collisionListeners = new ArrayList<CollisionListener<T,E>>();
		this.boundsListeners = new ArrayList<BoundsListener<T,E>>();
	}

	@Override
	public void addBody(T body) {
		// check for null body
		if (body == null) throw new NullPointerException(Messages.getString("dynamics.world.addNullBody"));

		FixtureModificationHandler<E> handler = body.getFixtureModificationHandler();
		// dont allow a body that already is assigned to another world
		if (handler != null) throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
		// dont allow adding it twice
		// TODO remove this or no?
//		if (handler. == this) throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
		// add it to the world
		this.bodies.add(body);
		// set the world property on the body
//		body.world = this;
		body.setFixtureModificationHandler(new BodyModificationHandler(body));
		// add it to the broadphase
		this.broadphaseDetector.add(body);
	}

	@Override
	public void removeAllBodies() {
		this.removeAllBodies(false);
	}

	public boolean containsBody(T body) {
		return this.bodies.contains(body);
	}
	
	public boolean removeBody(int index) {
		return this.removeBody(index, false);
	}

	public boolean removeBody(int index, boolean notify) {
		T body = this.bodies.get(index);
		return this.removeBody(body, notify);
	}
	
	public boolean removeBody(T body) {
		return this.removeBody(body, false);
	}
	
	@Override
	public List<T> getBodies() {
		return Collections.unmodifiableList(this.bodies);
	}
	
	@Override
	public T getBody(int index) {
		return this.bodies.get(index);
	}
	
	@Override
	public int getBodyCount() {
		return this.bodies.size();
	}
	
	@Override
	public boolean isEmpty() {
		return this.bodies.isEmpty();
	}
	
	@Override
	public Iterator<T> getBodyIterator() {
		return new BodyIterator();
	}
	
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	public Bounds getBounds() {
		return this.bounds;
	}
	
	public void setBroadphaseDetector(BroadphaseDetector<T, E> broadphaseDetector) {
		if (broadphaseDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullBroadphaseDetector"));
		// set the new broadphase
		this.broadphaseDetector = broadphaseDetector;
		
		// re-add all bodies to the broadphase
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			this.broadphaseDetector.add(this.bodies.get(i));
		}
	}

	public BroadphaseDetector<T, E> getBroadphaseDetector() {
		return this.broadphaseDetector;
	}
	
	public void setDetectBroadphaseFilter(BroadphaseFilter<T, E> filter) {
		if (filter == null) {
			this.detectBroadphaseFilter = new DefaultBroadphaseFilter<T, E>();
		} else {
			this.detectBroadphaseFilter = filter;
		}
	}
	
	public BroadphaseFilter<T, E> getDetectBroadphaseFilter() {
		return this.detectBroadphaseFilter;
	}
	
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
		if (narrowphaseDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullNarrowphaseDetector"));
		this.narrowphaseDetector = narrowphaseDetector;
	}
	
	public NarrowphaseDetector getNarrowphaseDetector() {
		return this.narrowphaseDetector;
	}
	
	public void setManifoldSolver(ManifoldSolver manifoldSolver) {
		if (manifoldSolver == null) throw new NullPointerException(Messages.getString("dynamics.world.nullManifoldSolver"));
		this.manifoldSolver = manifoldSolver;
	}
	
	public ManifoldSolver getManifoldSolver() {
		return this.manifoldSolver;
	}

	public void setRaycastDetector(RaycastDetector raycastDetector) {
		if (raycastDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullRaycastDetector"));
		this.raycastDetector = raycastDetector;
	}
	
	public RaycastDetector getRaycastDetector() {
		return this.raycastDetector;
	}

	public void setTimeOfImpactDetector(TimeOfImpactDetector timeOfImpactDetector) {
		if (timeOfImpactDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullTimeOfImpactDetector"));
		this.timeOfImpactDetector = timeOfImpactDetector;
	}

	public TimeOfImpactDetector getTimeOfImpactDetector() {
		return this.timeOfImpactDetector;
	}
	
	@Override
	public void shift(Vector2 shift) {
		// update the broadphase
		this.broadphaseDetector.shift(shift);
		
		// update the bounds
		if (this.bounds != null) {
			this.bounds.shift(shift);
		}
		
		// update the bodies
		int bSize = this.bodies.size();
		for (int i = 0; i < bSize; i++) {
			T body = this.bodies.get(i);
			body.shift(shift);
		}
		
		// update the cached data
		for (V item : this.collisionData.values()) {
			item.shift(shift);
		}
	}
	
	@Override
	public Collection<V> getCollisionData() {
		return this.unmodifiableCollisionData.values();
	}
	
	@Override
	public Iterator<V> getCollisionDataIterator() {
		return this.unmodifiableCollisionData.values().iterator();
	}

	@Override
	public List<CollisionListener<T, E>> getCollisionListeners() {
		return this.collisionListeners;
	}
	
	@Override
	public List<BoundsListener<T, E>> getBoundsListeners() {
		return this.boundsListeners;
	}
	
	protected void detect() {
		// get the bounds listeners
		List<BoundsListener<T, E>> boundsListeners = this.boundsListeners;
		
		int blSize = boundsListeners.size();
		int bSize = this.bodies.size();
		
		// TODO batch broadphase/LazyAABBTree
		
		// update all AABBs in the broadphase (can skip those who's transforms are identical?  i don't think so because they could change the shape of the colliable)
		CollisionItemAdapter<T, E> bAdapter = new CollisionItemAdapter<T, E>();
		for (int i = 0; i < bSize; i++) {
			T body = this.bodies.get(i);
			
			// skip if already not active
			if (!body.isEnabled()) continue;
			
			// update the broadphase with the new position/orientation
			// depending on the broadphase implementation this may or
			// may not update it for this body.
			this.broadphaseDetector.update(body);
			
			// instead of building an AABB for the whole body, let's check
			// each fixture AABB so that we can exit early (in most cases
			// one fixture will be within bounds). This also saves an allocation
			// TODO is this faster or slower than the old process?
			if (this.bounds != null) {
				boolean withinBounds = false;
				
				int fSize = body.getFixtureCount();
				for (int k = 0; k < fSize; k++) {
					E fixture = body.getFixture(k);
					bAdapter.set(body, fixture);
					if (!this.bounds.isOutside(this.broadphaseDetector.getAABB(bAdapter))) {
						withinBounds = true;
						break;
					}
				}
				
				if (!withinBounds) {
					// set the body to inactive
					body.setEnabled(false);
					// if so, notify via the listeners
					for (int j = 0; j < blSize; j++) {
						BoundsListener<T, E> bl = boundsListeners.get(j);
						bl.outside(body);
					}
				}
			}
		}
		
		// detect broadphase pairs
		Iterator<CollisionPair<T, E>> broadphasePairIterator = this.broadphaseDetector.detectIterator(false);
		while(broadphasePairIterator.hasNext()) {
			// NOTE: since the broadphase reuses the pair object, make sure to make a copy of it
			CollisionPair<T, E> pair = broadphasePairIterator.next().copy();
			if (!this.collisionData.containsKey(pair)) {
				this.collisionData.put(pair, this.createCollisionData(pair));
			}
		}
		
		this.detectCollisions(new DetectIterator());
		
		this.broadphaseDetector.clearUpdates();
	}

	protected abstract V createCollisionData(CollisionPair<T, E> pair);
	
	protected abstract void detectCollisions(Iterator<V> iterator);
	
	// TODO comments
	
	// AABB
	
	@Override
	public List<DetectResult<T, E>> detect(AABB aabb, DetectFilter<T, E> filter) {
		List<DetectResult<T, E>> results = new ArrayList<DetectResult<T, E>>();
		
		Iterator<DetectResult<T, E>> iterator = this.detectIterator(aabb, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}

	@Override
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, DetectFilter<T, E> filter) {
		return new AABBDetectIterator(aabb, filter);
	}
	
	@Override
	public List<DetectResult<T, E>> detect(AABB aabb, T body, DetectFilter<T, E> filter) {
		List<DetectResult<T, E>> results = new ArrayList<DetectResult<T, E>>();
		
		Iterator<DetectResult<T, E>> iterator = this.detectIterator(aabb, body, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	@Override
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, T body, DetectFilter<T, E> filter) {
		return new AABBBodyDetectIterator(aabb, body, filter);
	}
	
	// convex
	
	@Override
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, DetectFilter<T, E> filter) {
		List<ConvexDetectResult<T, E>> results = new ArrayList<ConvexDetectResult<T, E>>();
		
		Iterator<ConvexDetectResult<T, E>> iterator = this.detectIterator(convex, transform, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	@Override
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter) {
		return new ConvexDetectIterator(convex, transform, filter);
	}
	
	@Override
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
		List<ConvexDetectResult<T, E>> results = new ArrayList<ConvexDetectResult<T, E>>();
		
		Iterator<ConvexDetectResult<T, E>> iterator = this.detectIterator(convex, transform, body, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	@Override
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
		return new ConvexBodyDetectIterator(convex, transform, body, filter);
	}
	
	// raycast
	
	@Override
	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		List<RaycastResult<T, E>> results = new ArrayList<RaycastResult<T, E>>();
		
		Iterator<RaycastResult<T, E>> iterator = this.raycastIterator(ray, maxLength, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}

	@Override
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		return new RaycastDetectIterator(ray, maxLength, filter);
	}
	
	@Override
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		// check for the desired length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		
		// create a raycast result
		RaycastResult<T, E> result = null;
		Raycast raycast = new Raycast();

		// filter using the broadphase first
		Iterator<CollisionItem<T, E>> iterator = this.broadphaseDetector.detectIterator(ray, maxLength);
		
		while (iterator.hasNext()) {
			CollisionItem<T, E> item = iterator.next();
			T body = item.getBody();
			E fixture = item.getFixture();
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the convex shape
			Transform transform = body.getTransform();
			Convex convex = fixture.getShape();
			
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				if (result == null) {
					result = new RaycastResult<T, E>(body, fixture, raycast);
				} else {
					result.setBody(body);
					result.setFixture(fixture);
					result.setRaycast(raycast);
				}
				// we are only looking for the closest so
				// set the new maximum
				max = raycast.getDistance();
			}
		}
		
		return result;
	}
	
	@Override
	public RaycastResult<T, E> raycast(Ray ray, double maxLength, T body, DetectFilter<T, E> filter) {
		// set the maximum length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		
		// get the number of fixtures
		int size = body.getFixtureCount();
		// get the body transform
		Transform transform = body.getTransform();
		
		// create a raycast object to store the result
		Raycast raycast = new Raycast();
		RaycastResult<T, E> result = null;
		
		// loop over the fixtures finding the closest one
		for (int i = 0; i < size; i++) {
			// get the fixture
			E fixture = body.getFixture(i);
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the convex shape
			Convex convex = fixture.getShape();
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				if (result == null) {
					result = new RaycastResult<T, E>();
				}
				// if the raycast detected a collision then set the new
				// maximum distance
				max = raycast.getDistance();
				// assign the fixture
				result.setBody(body);
				result.setFixture(fixture);
				result.setRaycast(raycast);
				// the last raycast will always be the minimum raycast
				// flag that we did get a successful raycast
			}
		}
		
		return result;
	}
	
	// convex cast

	@Override
	public ConvexCastResult<T, E> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, T body, DetectFilter<T, E> filter) {
		ConvexCastResult<T, E> result = null;
		
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;

		// find the minimum time of impact for the given convex
		// and the current body
		int bSize = body.getFixtureCount();
		Transform bodyTransform = body.getTransform();
		
		// loop through all the body fixtures until we find
		// a the fixture that has the smallest time of impact
		for (int i = 0; i < bSize; i++) {
			E fixture = body.getFixture(i);
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the time of impact
			Convex c = fixture.getShape();
			TimeOfImpact toi = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, t2, toi)) {
				// set the new maximum time
				t2 = toi.getTime();
				
				if (result == null) {
					result = new ConvexCastResult<T, E>();
				}
				
				// save the min time of impact
				result.setBody(body);
				result.setFixture(fixture);
				result.setTimeOfImpact(toi);
			}
		}
		
		return result;
	}
	
	@Override
	public List<ConvexCastResult<T, E>> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		List<ConvexCastResult<T, E>> results = new ArrayList<ConvexCastResult<T, E>>();
		
		Iterator<ConvexCastResult<T, E>> iterator = this.convexCastIterator(convex, transform, deltaPosition, deltaAngle, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	@Override
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		// compute a conservative AABB for the motion of the convex
		double radius = convex.getRadius();
		Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
		AABB startAABB = new AABB(startWorldCenter, radius);
		
		// linearlly interpolate to get the final transform given the
		// change in position and angle
		Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
		// get the end AABB
		Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
		AABB endAABB = new AABB(endWorldCenter, radius);
		// union the AABBs to get the swept AABB
		AABB aabb = startAABB.getUnion(endAABB);
		
		ConvexCastResult<T, E> min = null;
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;
		
		// use the broadphase to filter first
		Iterator<CollisionItem<T, E>> iterator = this.broadphaseDetector.detectIterator(aabb);
		// loop over the potential collisions
		while (iterator.hasNext()) {
			CollisionItem<T, E> item = iterator.next();
			T body = item.getBody();
			E fixture = item.getFixture();
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// only get the minimum fixture
			double ft2 = t2;
			Transform bodyTransform = body.getTransform();
			
			// get the time of impact
			Convex fixtureShape = fixture.getShape();
			TimeOfImpact timeOfImpact = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(
					convex, transform, deltaPosition, deltaAngle, 
					fixtureShape, bodyTransform, dp2, 0.0, 
					0.0, ft2, timeOfImpact)) {
				// only save the minimum
				if (min == null || timeOfImpact.getTime() < min.getTimeOfImpact().getTime()) {
					ft2 = timeOfImpact.getTime();
					min.setBody(body);
					min.setFixture(fixture);
					min.setTimeOfImpact(timeOfImpact);
				}
			}
		}
		
		return min;
	}
	
	@Override
	public Iterator<ConvexCastResult<T, E>> convexCastIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		return new ConvexCastDetectIterator(convex, transform, deltaPosition, deltaAngle, filter);
	}
	
	// iterators
	
	private final class DetectIterator implements Iterator<V> {
		private final Iterator<V> iterator;
		private final List<CollisionListener<T, E>> listeners;
		private final int clSize;
		
		private final CollisionItemAdapter<T, E> adapter1 = new CollisionItemAdapter<T, E>();
		private final CollisionItemAdapter<T, E> adapter2 = new CollisionItemAdapter<T, E>();
		
		public DetectIterator() {
			this.iterator = AbstractCollisionWorld.this.collisionData.values().iterator();
			this.listeners = AbstractCollisionWorld.this.collisionListeners;
			this.clSize = this.listeners.size();
		}
		
		private final boolean isAllowedBroadphase(BroadphaseCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		private final boolean isAllowedNarrowphase(NarrowphaseCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		private final boolean isAllowedManifold(ManifoldCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public V next() {
			V collision = this.iterator.next();
			
			// get the bodies/fixtures
			T body1 = collision.getBody1();
			T body2 = collision.getBody2();
			E fixture1 = collision.getFixture1();
			E fixture2 = collision.getFixture2();
			
			collision.reset();
			
			// since the broadphase is a new-overlap-only detection
			// we need to check every item in the stored set of collisions:
			//		1. check if they were updated
			// 		2. if so, then check if their AABBs still overlap
			this.adapter1.set(body1, fixture1);
			this.adapter2.set(body2, fixture2);
			if (AbstractCollisionWorld.this.broadphaseDetector.isUpdated(this.adapter1) || AbstractCollisionWorld.this.broadphaseDetector.isUpdated(this.adapter2)) {
				// then we need to verify the pair is still valid
				AABB aabb1 = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.adapter1);
				AABB aabb2 = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.adapter2);
				if (!aabb1.overlaps(aabb2)) {
					// remove the collision from the set of collisions
					this.iterator.remove();
					// always report back the collision because we may need to send
					// notifications of "end" contacts
					return collision;
				}
			}
			
			// check broadphase filter conditions
			if (!AbstractCollisionWorld.this.detectBroadphaseFilter.isAllowed(body1, fixture1, body2, fixture2)) {
				return collision;
			}
			
			// check listeners
			if (!this.isAllowedBroadphase(collision)) {
				return collision;
			}
			
			// it's a legit broadphase collision now
			collision.setBroadphaseCollision(true);
			
			// get the body/fixture data needed for the narrowphase
			Transform transform1 = body1.getTransform();
			Transform transform2 = body2.getTransform();
			Convex convex2 = fixture2.getShape();
			Convex convex1 = fixture1.getShape();

			// TODO would be nice to seed the narrowphase with the last separation normal (when they aren't overlapping), if we store the last separation normal, we don't want to clear/reset that
			Penetration penetration = collision.getPenetration();
			if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration)) {
				// check for zero penetration
				if (penetration.getDepth() == 0.0) {
					// this should only happen if numerical error occurs
					return collision;
				}
				
				// perform post processing
				if (AbstractCollisionWorld.this.narrowphasePostProcessor != null) {
					AbstractCollisionWorld.this.narrowphasePostProcessor.process(convex1, transform1, convex2, transform2, penetration);
				}
				
				// notify of the narrow-phase collision
				if (!isAllowedNarrowphase(collision)) {
					return collision;
				}

				// it's a legit narrowphase collision now
				collision.setNarrowphaseCollision(true);
				
				// if there is penetration then find a contact manifold
				// using the filled in penetration object
				Manifold manifold = collision.getManifold();
				if (AbstractCollisionWorld.this.manifoldSolver.getManifold(penetration, convex1, transform1, convex2, transform2, manifold)) {
					// check for zero points
					if (manifold.getPoints().size() == 0) {
						// this should only happen if numerical error occurs
						return collision;
					}
					
					// notify of the manifold solving result
					if (!isAllowedManifold(collision)) {
						return collision;
					}
					
					// it's a legit manifold collision now
					collision.setManifoldCollision(true);
				}
			}
			
			return collision;
		}
	}
	
	private final class BodyIterator implements Iterator<T> {
		/** The current index */
		private int index;
		
		/**
		 * Minimal constructor.
		 */
		public BodyIterator() {
			this.index = -1;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.index + 1 < AbstractCollisionWorld.this.bodies.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (this.index >= AbstractCollisionWorld.this.bodies.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				this.index++;
				T body = AbstractCollisionWorld.this.bodies.get(this.index);
				return body;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			if (this.index < 0) {
				throw new IllegalStateException();
			}
			if (this.index >= AbstractCollisionWorld.this.bodies.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				AbstractCollisionWorld.this.removeBody(this.index);
				this.index--;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
	
	private final class AABBDetectIterator implements Iterator<DetectResult<T, E>>  {
		private final DetectFilter<T, E> filter;
		private final DetectResult<T, E> reusableResult;
		
		private final Iterator<CollisionItem<T, E>> iterator;
		
		public AABBDetectIterator(AABB aabb, DetectFilter<T, E> filter) {
			this.filter = filter;
			this.reusableResult = new DetectResult<T, E>();
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(aabb);
		}
		
		@Override
		public boolean hasNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				this.reusableResult.setBody(body);
				this.reusableResult.setFixture(fixture);
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public DetectResult<T, E> next() {
			return this.reusableResult;
		}
		
	}
	
	private final class AABBBodyDetectIterator implements Iterator<DetectResult<T, E>>  {
		private final AABB aabb;
		private final T body;
		private final DetectFilter<T, E> filter;
		private final DetectResult<T, E> reusableResult;
		
		private final Iterator<E> iterator;
		
		public AABBBodyDetectIterator(AABB aabb, T body, DetectFilter<T, E> filter) {
			this.aabb = aabb;
			this.body = body;
			this.filter = filter;
			this.reusableResult = new DetectResult<T, E>();
			this.iterator = body.getFixtureIterator();
		}
		
		@Override
		public boolean hasNext() {
			while (this.iterator.hasNext()) {
				E fixture = this.iterator.next();
				
				if (this.filter != null && !this.filter.isAllowed(this.body, fixture)) {
					continue;
				}
				
				AABB aabb = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.body, fixture);
				
				if (this.aabb.overlaps(aabb)) {
					this.reusableResult.setBody(body);
					this.reusableResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public DetectResult<T, E> next() {
			return this.reusableResult;
		}
	}
	
	private final class ConvexDetectIterator implements Iterator<ConvexDetectResult<T, E>>  {
		private final Convex convex;
		private final Transform transform;
		private final AABB aabb;
		private final DetectFilter<T, E> filter;
		private final ConvexDetectResult<T, E> reusableResult;
		
		private final Iterator<CollisionItem<T, E>> iterator;
		
		public ConvexDetectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.filter = filter;
			this.reusableResult = new ConvexDetectResult<T, E>();
			this.reusableResult.setPenetration(new Penetration());
			
			this.aabb = convex.createAABB(transform);
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(this.aabb);
		}
		
		@Override
		public boolean hasNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				Convex convex1 = fixture.getShape();
				Transform transform1 = body.getTransform();
				
				if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, this.convex, this.transform, this.reusableResult.getPenetration())) {
					this.reusableResult.setBody(body);
					this.reusableResult.setFixture(fixture);
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public ConvexDetectResult<T, E> next() {
			return this.reusableResult;
		}
	}
	
	private final class ConvexBodyDetectIterator implements Iterator<ConvexDetectResult<T, E>>  {
		private final Convex convex;
		private final Transform transform;
		private final T body;
		private final AABB aabb;
		private final DetectFilter<T, E> filter;
		private final ConvexDetectResult<T, E> reusableResult;
		
		private final Iterator<E> iterator;
		
		public ConvexBodyDetectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.body = body;
			this.filter = filter;
			this.reusableResult = new ConvexDetectResult<T, E>();
			this.reusableResult.setPenetration(new Penetration());
			
			this.aabb = convex.createAABB(transform);
			this.iterator = body.getFixtureIterator();
		}
		
		@Override
		public boolean hasNext() {
			while (this.iterator.hasNext()) {
				E fixture = this.iterator.next();
				
				if (this.filter != null && !this.filter.isAllowed(this.body, fixture)) {
					continue;
				}
				
				AABB aabb = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.body, fixture);
				if (this.aabb.overlaps(aabb)) {
					Convex convex1 = fixture.getShape();
					Transform transform1 = this.body.getTransform();
					
					if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, this.convex, this.transform, this.reusableResult.getPenetration())) {
						this.reusableResult.setBody(this.body);
						this.reusableResult.setFixture(fixture);
					}
					
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public ConvexDetectResult<T, E> next() {
			return this.reusableResult;
		}
	}
	
	private final class RaycastDetectIterator implements Iterator<RaycastResult<T, E>> {
		private final Ray ray;
		private final DetectFilter<T, E> filter;
		
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final RaycastResult<T, E> reusableResult;
		private double max;
		
		public RaycastDetectIterator(Ray ray, double maxLength, DetectFilter<T, E> filter) {
			this.ray = ray;
			this.filter = filter;
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(ray, maxLength);
			
			this.reusableResult = new RaycastResult<T, E>();
			this.reusableResult.setRaycast(new Raycast());
			
			double max = 0.0;
			if (maxLength > 0.0) {
				max = maxLength;
			}
			this.max = max;
		}
		
		@Override
		public boolean hasNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				// get the convex shape
				Transform transform = body.getTransform();
				Convex convex = fixture.getShape();
				
				// perform the raycast
				if (AbstractCollisionWorld.this.raycastDetector.raycast(this.ray, this.max, convex, transform, this.reusableResult.getRaycast())) {
					// we found a collision to report
					this.reusableResult.setBody(body);
					this.reusableResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public RaycastResult<T, E> next() {
			return this.reusableResult;
		}
	}

	private final class ConvexCastDetectIterator implements Iterator<ConvexCastResult<T, E>> {
		private final Convex convex;
		private final Transform transform;
		private final Vector2 deltaPosition;
		private final double deltaAngle;
		private final DetectFilter<T, E> filter;
		
		private final AABB aabb;
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final ConvexCastResult<T, E> reusableResult;
		
		public ConvexCastDetectIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.deltaPosition = deltaPosition;
			this.deltaAngle = deltaAngle;
			this.filter = filter;
			
			// compute a conservative AABB for the motion of the convex
			double radius = convex.getRadius();
			Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
			AABB startAABB = new AABB(startWorldCenter, radius);
			
			// linearlly interpolate to get the final transform given the
			// change in position and angle
			Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
			// get the end AABB
			Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
			AABB endAABB = new AABB(endWorldCenter, radius);
			// union the AABBs to get the swept AABB
			this.aabb = startAABB.getUnion(endAABB);
			
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(this.aabb);
			
			this.reusableResult = new ConvexCastResult<T, E>();
			this.reusableResult.setTimeOfImpact(new TimeOfImpact());
		}
		
		@Override
		public boolean hasNext() {
			final Vector2 dp2 = new Vector2();
			
			// loop over the potential collisions
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				Transform bodyTransform = body.getTransform();
				
				// get the time of impact
				Convex c = fixture.getShape();
				// we pass the zero vector and 0 for the change in position and angle for the body
				// since we assume that it is not moving since this is a static test
				if (AbstractCollisionWorld.this.timeOfImpactDetector.getTimeOfImpact(
						this.convex, this.transform, this.deltaPosition, this.deltaAngle, 
						c, bodyTransform, dp2, 0.0, 
						0.0, 1.0, this.reusableResult.getTimeOfImpact())) {
					this.reusableResult.setBody(body);
					this.reusableResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public ConvexCastResult<T, E> next() {
			return this.reusableResult;
		}
	}

	private final class BodyModificationHandler implements FixtureModificationHandler<E> {
		private final T body;
		
		public BodyModificationHandler(T body) {
			this.body = body;
		}

		@Override
		public void onFixtureAdded(E fixture) {
			AbstractCollisionWorld.this.broadphaseDetector.add(this.body, fixture);
		}
		
		@Override
		public void onFixtureRemoved(E fixture) {
			AbstractCollisionWorld.this.broadphaseDetector.remove(this.body, fixture);
		}
		
		@Override
		public void onAllFixturesRemoved() {
			AbstractCollisionWorld.this.broadphaseDetector.remove(this.body);
		}
	}
}
