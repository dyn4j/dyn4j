package org.dyn4j.geometry.simplify;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Vector2;

public abstract class AbstractSimplifier implements Simplifier {
	/**
	 * Reduces the number of vertices by inspecting the distance between them.  If the
	 * distance between an adjacent vertex and the current vertex is less than the given 
	 * tolerance, it's ignored.
	 * <p>
	 * This algorithm is typically used to preprocess a simple polygon before another
	 * simplification algorithm is used.
	 * <p>
	 * This algorithm has O(n) complexity where n is the number of vertices in the source
	 * polygon.
	 * <p>
	 * This method does not require the polygon to have any defined winding, but does assume
	 * that it does not have holes and is not self-intersecting.
	 * <p>
	 * This method handles null/empty lists, null elements, and all null elements.  In these
	 * cases it's possible the returned list will be empty or have less than 3 vertices. 
	 * @param vertices the vertices of the simple polygon
	 * @param tolerance the minimum distance between vertices
	 * @return List&lt;{@link Vector2}&gt;
	 * @see <a href="http://geomalgorithms.com/a16-_decimate-1.html">Vertex Cluster Reduction</a>
	 */
	protected final List<Vector2> simplifyClusteredVertices(List<Vector2> vertices, double tolerance) {
		if (tolerance < 0) throw new IllegalArgumentException();  // TODO message
		
		List<Vector2> reduced = new ArrayList<Vector2>();
		
		// check for null vertices list
		if (vertices == null) return reduced;
		
		int size = vertices.size();
		
		// check for 0 vertices
		if (size == 0) return vertices;
		
		// find the first non-null vertex
		Vector2 start = null;
		int startIndex = 0;
		for (int i = 0; i < size; i++) {
			Vector2 v = vertices.get(i);
			if (v != null) {
				start = v;
				startIndex = i;
				reduced.add(start);
				break;
			}
		}
		
		// start will be null if the list has only null elements
		if (start == null) {
			return reduced;
		}
		
		// start at the next index past the start index and do the simplification
		final double toleranceSquared = tolerance * tolerance;
		for (int i = startIndex + 1; i < size; i++) {
			Vector2 v = vertices.get(i);
			
			// ignore any null elements
			if (v == null) continue;
			
			// get the squared distance between the vertices
			double dist2 = start.distanceSquared(v);
			if (dist2 < toleranceSquared) {
				// if it's close enough, then ignore it
				continue;
			}
			
			// otherwise add it to the result
			// and start comparing against the
			// new one
			reduced.add(v);
			start = v;
		}
		
		return reduced;
	}
}
