/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision.narrowphase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;

/**
 * Represents a delegating {@link NarrowphaseDetector} that uses a primary {@link NarrowphaseDetector} and
 * fallback {@link NarrowphaseDetector}.
 * <p>
 * The fallback {@link NarrowphaseDetector} is used when <strong>any</strong> of the {@link FallbackCondition}s 
 * added have been met.
 * <p>
 * {@link FallbackCondition}s will be checked in order and will stop on the first matched condition. By default the conditions
 * are ordered in the order they are added unless a condition specifies a sortIndex.
 * <p>
 * For example, when the {@link Sat} algorithm is used, some shapes are not supported. A {@link TypedFallbackCondition} can be
 * used to fallback to the {@link Gjk} algorithm:
 * <pre>
 * FallbackNarrowphaseDetector detector = new FallbackNarrowphaseDetector(new Sat(), new Gjk());
 * // any Slice collisions will be handled by Gjk instead of Sat
 * detector.addCondition(new SingleTypedFallbackCondition(Slice.class));</pre>
 * New condition types can be added by implementing the {@link FallbackCondition} interface. Doing so can lead to
 * interesting options like custom collision detectors for specific cases or custom shapes.
 * <p>
 * The primary and fallback detectors can also be {@link FallbackNarrowphaseDetector}s as well allowing for a chain of 
 * fallbacks.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public class FallbackNarrowphaseDetector implements NarrowphaseDetector {
	/** The primary {@link NarrowphaseDetector} */
	protected final NarrowphaseDetector primaryNarrowphaseDetector;
	
	/** The fallback {@link NarrowphaseDetector} */
	protected final NarrowphaseDetector fallbackNarrowphaseDetector;
	
	/** The conditions for when to use the fallback {@link NarrowphaseDetector} */
	protected final List<FallbackCondition> fallbackConditions;
	
	/**
	 * Minimal constructor.
	 * @param primaryNarrowphaseDetector the primary {@link NarrowphaseDetector}
	 * @param fallbackNarrowphaseDetector the fallback {@link NarrowphaseDetector}
	 * @throws NullPointerException if either the primary or fallback {@link NarrowphaseDetector}s are null
	 */
	public FallbackNarrowphaseDetector(NarrowphaseDetector primaryNarrowphaseDetector, NarrowphaseDetector fallbackNarrowphaseDetector) {
		this(primaryNarrowphaseDetector, fallbackNarrowphaseDetector, new ArrayList<FallbackCondition>());
	}
	
	/**
	 * Full constructor.
	 * @param primaryNarrowphaseDetector the primary {@link NarrowphaseDetector}
	 * @param fallbackNarrowphaseDetector the fallback {@link NarrowphaseDetector}
	 * @param conditions the fallback conditions
	 * @throws NullPointerException if either the primary or fallback {@link NarrowphaseDetector}s are null
	 */
	public FallbackNarrowphaseDetector(NarrowphaseDetector primaryNarrowphaseDetector, NarrowphaseDetector fallbackNarrowphaseDetector, List<FallbackCondition> conditions) {
		if (primaryNarrowphaseDetector == null) throw new NullPointerException();
		if (fallbackNarrowphaseDetector == null) throw new NullPointerException();
		this.primaryNarrowphaseDetector = primaryNarrowphaseDetector;
		this.fallbackNarrowphaseDetector = fallbackNarrowphaseDetector;
		if (conditions != null) {
			this.fallbackConditions = conditions;
		} else {
			this.fallbackConditions = new ArrayList<FallbackCondition>();
		}
	}
	
	/**
	 * Adds the given condition to the list of fallback conditions.
	 * @param condition the condition
	 */
	public void addCondition(FallbackCondition condition) {
		this.fallbackConditions.add(condition);
		Collections.sort(this.fallbackConditions);
	}
	
	/**
	 * Removes the given condition to the list of fallback conditions and
	 * returns true if the operation was successful.
	 * @param condition the condition
	 * @return boolean
	 */
	public boolean removeCondition(FallbackCondition condition) {
		return this.fallbackConditions.remove(condition);
	}
	
	/**
	 * Returns true if the given condition is contained in this detector.
	 * @param condition the fallback condition
	 * @return boolean
	 */
	public boolean containsCondition(FallbackCondition condition) {
		return this.fallbackConditions.contains(condition);
	}
	
	/**
	 * Returns the number of fallback conditions.
	 * @return int
	 */
	public int getConditionCount() {
		return this.fallbackConditions.size();
	}
	
	/**
	 * Returns the fallback condition at the given index.
	 * @param index the index
	 * @return {@link FallbackCondition}
	 * @throws IndexOutOfBoundsException if index is not between 0 and {@link #getConditionCount()}
	 */
	public FallbackCondition getCondition(int index) {
		return this.fallbackConditions.get(index);
	}
	
	/**
	 * Returns true if the fallback {@link NarrowphaseDetector} should be used rather
	 * than the primary.
	 * @param convex1 the first convex
	 * @param convex2 the second convex
	 * @return boolean
	 */
	public boolean isFallbackRequired(Convex convex1, Convex convex2) {
		int size = this.fallbackConditions.size();
		for (int i = 0; i < size; i++) {
			FallbackCondition condition = this.fallbackConditions.get(i);
			if (condition != null && condition.isMatch(convex1, convex2)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		if (this.isFallbackRequired(convex1, convex2)) {
			return this.fallbackNarrowphaseDetector.detect(convex1, transform1, convex2, transform2);
		}
		return this.primaryNarrowphaseDetector.detect(convex1, transform1, convex2, transform2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Penetration)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Penetration penetration) {
		if (this.isFallbackRequired(convex1, convex2)) {
			return this.fallbackNarrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration);
		}
		return this.primaryNarrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration);
	}
	
	/**
	 * Returns the primary {@link NarrowphaseDetector}.
	 * @return {@link NarrowphaseDetector}
	 */
	public NarrowphaseDetector getPrimaryNarrowphaseDetector() {
		return this.primaryNarrowphaseDetector;
	}
	
	/**
	 * Returns the fallback {@link NarrowphaseDetector}.
	 * @return {@link NarrowphaseDetector}
	 */
	public NarrowphaseDetector getFallbackNarrowphaseDetector() {
		return this.fallbackNarrowphaseDetector;
	}
}
