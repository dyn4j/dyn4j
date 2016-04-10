package org.dyn4j.geometry;

import org.dyn4j.DataContainer;

public class Link extends Segment implements Convex, Wound, Shape, Transformable, DataContainer {
	final Vector2 point0;
	final Vector2 point3;
	
	public Link(Vector2 point0, Vector2 point1, Vector2 point2, Vector2 point3) {
		super(point1, point2);
		this.point0 = point0;
		this.point3 = point3;
	}
	
	public Vector2 getPoint0() {
		return this.point0;
	}
	
	public Vector2 getPoint3() {
		return this.point3;
	}
	
	// for now, local rotation and translation is not supported
	
	@Override
	public void rotate(double theta) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void rotate(double theta, double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void rotate(double theta, Vector2 point) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void rotateAboutCenter(double theta) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void translate(double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void translate(Vector2 vector) {
		throw new UnsupportedOperationException();
	}
}
