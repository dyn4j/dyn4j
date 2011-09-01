package org.dyn4j.sandbox;

import org.dyn4j.collision.AbstractBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.Collidable;

/**
 * Simple class to flag null bounds inside the WorldTreePanel for icon and text usage.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NullBounds extends AbstractBounds implements Bounds {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Bounds#isOutside(org.dyn4j.collision.Collidable)
	 */
	@Override
	public boolean isOutside(Collidable collidable) {
		// a null bounds object lets everything through
		return false;
	}
}
