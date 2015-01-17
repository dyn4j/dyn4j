package org.dyn4j.dynamics;

import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.BroadphaseFilter;

public class BodyBroadphaseFilter implements BroadphaseFilter<Body, BodyFixture> {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseFilter#isAllowed(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture, org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isAllowed(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2) {
		// inactive objects don't have collision detection/response
		if (!body1.isActive() || !body2.isActive()) return false;
		// one body must be dynamic
		if (!body1.isDynamic() && !body2.isDynamic()) return false;
		// check for connected pairs who's collision is not allowed
		if (body1.isConnected(body2, false)) return false;
		
		Filter filter1 = fixture1.getFilter();
		Filter filter2 = fixture2.getFilter();
		return filter1.isAllowed(filter2);
	}

}
