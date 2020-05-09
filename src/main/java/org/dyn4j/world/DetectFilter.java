package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;

public class DetectFilter<T extends CollisionBody<E>, E extends Fixture> {
	private final boolean ignoreSensors; 
	private final boolean ignoreInactive;
	private final Filter filter;

	public DetectFilter(boolean ignoreSensors, boolean ignoreInactive, Filter filter) {
		super();
		this.ignoreSensors = ignoreSensors;
		this.ignoreInactive = ignoreInactive;
		this.filter = filter;
	}

	public boolean isAllowed(T body, E fixture) {
		// check for inactive
		if (this.ignoreInactive && !body.isEnabled()) {
			return false;
		}
		
		// check for sensor
		if (this.ignoreSensors && fixture.isSensor()) {
			return false;
		}
		
		// check against the filter
		if (this.filter != null && !this.filter.isAllowed(fixture.getFilter())) {
			return false;
		}
		
		return true;
	}
}
