package org.dyn4j.geometry.simplify;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Vector2;

// TODO this is the simple Douglas-Peucker algorithm that doesn't handle self-intersection
public final class DouglasPeucker extends AbstractSimplifier implements Simplifier {
	// TODO this should be an input
	private final double clusterTolerance;
	private final double e;
	
	public DouglasPeucker(double clusterTolerance, double epsilon) {
		this.clusterTolerance = clusterTolerance;
		this.e = epsilon;
	}
	
	public List<Vector2> simplify(List<Vector2> vertices) {
		if (vertices == null) {
			return vertices;
		}
		
		if (vertices.size() < 4) {
			return vertices;
		}
		
		List<Vector2> result = new ArrayList<Vector2>();
		
		// 0. first reduce any clustered vertices in the polygon
		vertices = this.simplifyClusteredVertices(vertices, this.clusterTolerance);
		
		// 1. find two points to split the poly into two halves
		// for example:
		// 0-------------0        A-------------0  |                0
		// |              \       |                |                 \
		// |               \  =>  |                &                  \
		// |                \     |                |                   \
		// 0-----------------0    0                |  0-----------------B
		int startIndex = 0;
		int endIndex = this.getFartherestVertexFromVertex(startIndex, vertices);
		
		// 2. split into two polylines to simplify
		List<Vector2> aReduced = this.douglasPeucker(vertices.subList(startIndex, endIndex + 1));
		List<Vector2> bReduced = this.douglasPeucker(vertices.subList(endIndex, vertices.size()));
		
		// 3. merge the two polylines back together
		result.addAll(aReduced.subList(0, aReduced.size() - 1));
		result.addAll(bReduced);
		
		return result;
	}
	
	/**
	 * Recursively sub-divide the given polyline performing the douglas Peucker algorithm.
	 * <p>
	 * O(mn) in worst case, O(n log m) in best case, where n is the number of vertices in the
	 * original polyline and m is the number of vertices in the reduced polyline.
	 * @param polyline
	 * @return
	 */
	private List<Vector2> douglasPeucker(List<Vector2> polyline) {
		int size = polyline.size();

		// get the start/end vertices of the polyline
		Vector2 start = polyline.get(0);
		Vector2 end = polyline.get(size - 1);
		
		// get the farthest vertex from the line created from the start to the end
		// vertex on the polyline
		FarthestVertex fv = this.getFarthestVertexFromLine(start, end, polyline);
		
		// check the farthest point's distance - if it's higher than the minimum
		// distance epsilon, then we need to subdivide the polyline since we can't
		// reduce here (we might be able to reduce elsewhere)
		List<Vector2> result = new ArrayList<Vector2>();
		if (fv.distance > e) {
			// sub-divide and run the algo on each half
			List<Vector2> aReduced = this.douglasPeucker(polyline.subList(0, fv.index + 1));
			List<Vector2> bReduced = this.douglasPeucker(polyline.subList(fv.index, size));
			
			// recombine the reduced polylines
			result.addAll(aReduced.subList(0, aReduced.size() - 1));
			result.addAll(bReduced);
		} else {
			// just use the start/end vertices
			// as the new polyline
			result.add(start);
			result.add(end);
		}
		
		return result;
	}
	
	/**
	 * Returns the vertex farthest from the given vertex.
	 * <p>
	 * O(n)
	 * @param index
	 * @param polygon
	 * @return
	 */
	private int getFartherestVertexFromVertex(int index, List<Vector2> polygon) {
		double dist2 = 0.0;
		int max = -1;
		int size = polygon.size();
		Vector2 vertex = polygon.get(index);
		for (int i = 0; i < size; i++) {
			Vector2 vert = polygon.get(i);
			double test = vertex.distanceSquared(vert);
			if (test > dist2) {
				dist2 = test;
				max = i;
			}
		}
		
		return max;
	}
	
	/**
	 * Returns the farthest vertex in the polyline from the line created by lineVertex1 and lineVertex2.
	 * <p>
	 * O(n)
	 * @param lineVertex1
	 * @param lineVertex2
	 * @param polyline
	 * @return
	 */
	private FarthestVertex getFarthestVertexFromLine(Vector2 lineVertex1, Vector2 lineVertex2, List<Vector2> polyline) {
		FarthestVertex max = new FarthestVertex();
		int size = polyline.size();
		Vector2 line = lineVertex1.to(lineVertex2);
		Vector2 lineNormal = line.getLeftHandOrthogonalVector();
		lineNormal.normalize();
		for (int i = 0; i < size; i++) {
			Vector2 vert = polyline.get(i);
			double test = Math.abs(lineVertex1.to(vert).dot(lineNormal));
			if (test > max.distance) {
				max.distance = test;
				max.index = i;
			}
		}
		return max;
	}
	
	private class FarthestVertex {
		int index;
		double distance;
	}
}
