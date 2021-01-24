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

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Represents a {@link BroadphaseDetector} specifically used with {@link CollisionBody}-{@link Fixture} 
 * {@link CollisionItem} pairs.
 * <p>
 * This interface introduces a number of helper methods to work with {@link CollisionItem}s while
 * allowing the use of any {@link BroadphaseDetector}.
 * <b>
 * NOTE: Special care must be taken when removing fixtures from a body.  Be sure to call the 
 * {@link #remove(CollisionBody, Fixture)} method to make sure its removed from the broad-phase.
 * This class makes no attempt to remove fixtures that no longer exist on the body</b>
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public interface CollisionItemBroadphaseDetector<T extends CollisionBody<E>, E extends Fixture> extends BroadphaseDetectorDecorator<CollisionItem<T, E>>, BroadphaseDetector<CollisionItem<T, E>> {
	/**
	 * Adds a new {@link CollisionBody} to the broad-phase.
	 * <p>
	 * This will add all the given {@link CollisionBody}'s {@link Fixture}s to the broad-phase.
	 * <p>
	 * If the body has no fixtures, nothing will be added to this broad-phase.
	 * <p>
	 * If the {@link CollisionBody}'s {@link Fixture}s have already been added to this broad-phase
	 * they will be updated instead.
	 * <p>
	 * If a {@link Fixture} has been added to the {@link CollisionBody} and the {@link CollisionBody}
	 * has already been added to this broad-phase, any new {@link Fixture}s will be added, and the
	 * existing ones will be updated.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 */
	public void add(T body);
	
	/**
	 * Adds a new {@link Fixture} for the given {@link CollisionBody} to
	 * the broad-phase.
	 * @param body the body
	 * @param fixture the fixture to add
	 */
	public void add(T body, E fixture);
	
	/**
	 * Returns true if all the {@link Fixture}s on the given {@link CollisionBody}
	 * have been added to this broad-phase.
	 * <p>
	 * If a body is added without any fixtures, this method will return
	 * false.
	 * @param body the {@link CollisionBody}
	 * @return boolean
	 */
	public boolean contains(T body);
	
	/**
	 * Returns true if the given {@link Fixture} on the given {@link CollisionBody}
	 * has been added to this broad-phase.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return boolean
	 */
	public boolean contains(T body, E fixture);
	
	/**
	 * Returns true if the given bodies overlap using their broad-phase representation.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @return boolean
	 */
	public boolean detect(T body1, T body2);
	
	/**
	 * Returns true if the given body-fixture pairs overlap using their broad-phase representation.
	 * @param body1 the first body
	 * @param fixture1 the first fixture
	 * @param body2 the second body
	 * @param fixture2 the second fixture
	 * @return boolean
	 */
	public boolean detect(T body1, E fixture1, T body2, E fixture2);

	/**
	 * Returns the AABB for the given {@link CollisionBody} {@link Fixture}.
	 * <p>
	 * <b>NOTE</b>: Some {@link BroadphaseDetector}s use modified (expanded for example)
	 * AABBs rather than tight fitting AABBs as a performance enhancement.  This
	 * method returns the AABB used by this detector, and therefore, the modified
	 * AABB.
	 * <p>
	 * <b>NOTE</b>: The {@link AABB} returned from this method should not be modified.
	 * Instead use the {@link AABB#copy()} method to create a new instance to 
	 * modify.
	 * <p>
	 * If the given body-fixture has no shape, this method could return a degenerate
	 * AABB.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return {@link AABB}
	 */
	public AABB getAABB(T body, E fixture);
	
	/**
	 * Returns an AABB for the given {@link CollisionBody} by taking the union of all 
	 * the fixture AABBs attached to the body.
	 * <p>
	 * If the given body has no fixtures, this method returns a degenerate AABB.
	 * @param body the {@link CollisionBody}
	 * @return {@link AABB}
	 */
	public AABB getAABB(T body);
	
	/**
	 * Returns true if any of the {@link Fixture}s on the given {@link CollisionBody}
	 * are included in the updated list.
	 * <p>
	 * If {@link #isUpdateTrackingEnabled()} is false, this method will always return true.
	 * <p>
	 * Returns false if the given body is not part of this broad-phase.
	 * @param body the {@link CollisionBody}
	 * @return boolean
	 */
	public boolean isUpdated(T body);
	
	/**
	 * Returns true if the given {@link Fixture} is included in the updated list.
	 * <p>
	 * If {@link #isUpdateTrackingEnabled()} is false, this method will always return true.
	 * <p>
	 * Returns false if the given body-fixture pair is not part of this broad-phase.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return boolean
	 */
	public boolean isUpdated(T body, E fixture);
	
	/**
	 * Removes the given {@link CollisionBody} from the broad-phase.
	 * <p>
	 * Returns true if all fixtures on the given {@link CollisionBody} were
	 * removed.
	 * <p>
	 * This method removes all the {@link Fixture}s attached to the
	 * given {@link CollisionBody} from the broad-phase.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 * @return boolean
	 */
	public boolean remove(T body);
	
	/**
	 * Removes the given {@link Fixture} for the given {@link CollisionBody} from
	 * the broad-phase and returns true if it was found.
	 * @param body the body
	 * @param fixture the fixture to remove
	 * @return boolean true if the fixture was found and removed
	 */
	public boolean remove(T body, E fixture);
	
	/**
	 * This method is intended to force the broad-phase to include
	 * this {@link CollisionBody}'s {@link Fixture}s in the updated list to ensure
	 * they are checked in the updated-only detection routine.
	 * <p>
	 * The {@link #update(CollisionBody)} method will only mark a {@link CollisionBody}
	 * as updated if it's fixtures have moved enough to change the internally
	 * stored AABB.
	 * @param body the {@link CollisionBody}
	 */
	public void setUpdated(T body);
	
	/**
	 * This method is intended to force the broad-phase to include
	 * the {@link Fixture} in the updated list to ensure
	 * they are checked in the updated-only detection routine.
	 * <p>
	 * The {@link #update(CollisionBody, Fixture)} method will only mark the 
	 * {@link Fixture} as updated if the {@link Fixture} has 
	 * moved enough to change the internally stored AABB.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 */
	public void setUpdated(T body, E fixture);
	
	/**
	 * Updates all the {@link Fixture}s on the given {@link CollisionBody}.
	 * <p>
	 * Used when the body or its fixtures have moved or rotated or when fixtures
	 * have been added.
	 * <p>
	 * This method updates all the {@link Fixture}s attached to the
	 * given {@link CollisionBody} from the broad-phase, if they exist. If the 
	 * fixtures on the given body do not exist in the broad-phase, they are
	 * added.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 */
	public void update(T body);
	
	/**
	 * Updates the given {@link CollisionBody}'s {@link Fixture}.
	 * <p>
	 * Used when a fixture on a {@link CollisionBody} has moved or rotated.
	 * <p>
	 * This method will add the {@link Fixture} if it doesn't currently exist in
	 * this broad-phase.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture} that has moved
	 */
	public void update(T body, E fixture);
}
