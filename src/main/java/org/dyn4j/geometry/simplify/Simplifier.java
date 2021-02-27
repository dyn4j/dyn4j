package org.dyn4j.geometry.simplify;

import java.util.List;

import org.dyn4j.geometry.Vector2;

public interface Simplifier {
	public List<Vector2> simplify(List<Vector2> vertices);
}
