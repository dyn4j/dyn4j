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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * A default implementation of the the {@link BroadphaseDetectorDecorator} interface.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 * @param <T> the object type
 */
public class BroadphaseDetectorDecoratorAdapter<T> implements BroadphaseDetectorDecorator<T>, BroadphaseDetector<T> {
	/** The wrapped detector */
	protected final BroadphaseDetector<T> detector;

	/**
	 * Minimal constructor.
	 * @param detector the detector to delegate to
	 */
	public BroadphaseDetectorDecoratorAdapter(BroadphaseDetector<T> detector) {
		this.detector = detector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetectorDecorator#getDecoratedBroadphaseDetector()
	 */
	public BroadphaseDetector<T> getDecoratedBroadphaseDetector() {
		return this.detector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		this.detector.shift(shift);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(java.lang.Object)
	 */
	@Override
	public void add(T object) {
		this.detector.add(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(T object) {
		return this.detector.remove(object);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update()
	 */
	@Override
	public void update() {
		this.detector.update();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(java.lang.Object)
	 */
	@Override
	public void update(T object) {
		this.detector.update(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(java.lang.Object)
	 */
	@Override
	public void setUpdated(T object) {
		this.detector.setUpdated(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(java.lang.Object)
	 */
	@Override
	public boolean isUpdated(T object) {
		return this.detector.isUpdated(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		this.detector.clearUpdates();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(java.lang.Object)
	 */
	@Override
	public AABB getAABB(T object) {
		return this.detector.getAABB(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T object) {
		return this.detector.contains(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.detector.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.detector.size();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<CollisionPair<T>> detect() {
		return this.detector.detect();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(boolean)
	 */
	@Override
	public List<CollisionPair<T>> detect(boolean forceFullDetection) {
		return this.detector.detect(forceFullDetection);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator()
	 */
	@Override
	public Iterator<CollisionPair<T>> detectIterator() {
		return this.detector.detectIterator();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(boolean)
	 */
	@Override
	public Iterator<CollisionPair<T>> detectIterator(boolean forceFullDetection) {
		return this.detector.detectIterator(forceFullDetection);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<T> detect(AABB shape) {
		return this.detector.detect(shape);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.AABB)
	 */
	@Override
	public Iterator<T> detectIterator(AABB shape) {
		return this.detector.detectIterator(shape);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<T> raycast(Ray ray, double length) {
		return this.detector.raycast(ray, length);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycastIterator(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public Iterator<T> raycastIterator(Ray ray, double length) {
		return this.detector.raycastIterator(ray, length);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean detect(T a, T b) {
		return this.detector.detect(a, b);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		return this.detector.detect(convex1, transform1, convex2, transform2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingSupported()
	 */
	@Override
	public boolean isUpdateTrackingSupported() {
		return this.detector.isUpdateTrackingSupported();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingEnabled()
	 */
	@Override
	public boolean isUpdateTrackingEnabled() {
		return this.detector.isUpdateTrackingEnabled();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdateTrackingEnabled(boolean)
	 */
	@Override
	public void setUpdateTrackingEnabled(boolean flag) {
		this.detector.setUpdateTrackingEnabled(flag);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBProducer()
	 */
	@Override
	public AABBProducer<T> getAABBProducer() {
		return this.detector.getAABBProducer();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBExpansionMethod()
	 */
	@Override
	public AABBExpansionMethod<T> getAABBExpansionMethod() {
		return this.detector.getAABBExpansionMethod();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getBroadphaseFilter()
	 */
	@Override
	public BroadphaseFilter<T> getBroadphaseFilter() {
		return this.detector.getBroadphaseFilter();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#optimize()
	 */
	@Override
	public void optimize() {
		this.detector.optimize();
	}
}