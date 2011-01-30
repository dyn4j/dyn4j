/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.geometry;

/**
 * Represents a geometric {@link Shape}.
 * <p>
 * {@link Shape}s are {@link Transformable}, however, in general a {@link Transform} object should
 * be used instead of directly transforming the {@link Shape}.  Doing so will allow reuse of
 * the same {@link Shape} object in multiple places, where only the {@link Transform} differs.
 * <p>
 * If a class extends this class they must contain a single static final member called TYPE that
 * specifies the type of shape.  The member must be static and final because the {@link Shape.Type}
 * class method <code>is</code> only performs a reference comparison. When creating the type make 
 * sure to pass the super type {@link Shape.Type}.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public interface Shape extends Transformable {	
	/**
	 * Represents a {@link Shape} type.
	 * <p>
	 * The type of a shape is static and doesn't
	 * change therefore the comparison of shape 
	 * types only does a reference comparison.
	 * <p>
	 * Shape types are also hierarchical in nature.
	 * For example, performing a check like:
	 * <pre>
	 * Rectangle r = new Rectangle(1.0, 1.0);
	 * r.isType(Polygon.TYPE);
	 * // or
	 * Rectangle.TYPE.is(Polygon.TYPE);
	 * </pre>
	 * will return true.
	 * @author William Bittle
	 * @version 2.2.3
	 * @since 1.0.0
	 */
	public class Type {
		/** The parent shape type */
		private Shape.Type parent;
		
		/** The type name */
		private String name;
		
		/**
		 * Default constructor.
		 * <p>
		 * Creates a shape type with no parent
		 * shape type associated with it.
		 * @param name the name of the type
		 */
		public Type(String name) {
			this.name = name;
		}
		
		/**
		 * Full constructor.
		 * <p>
		 * Creates a shape type using the parent
		 * shape type.
		 * @param parent the parent shape type
		 * @param name the name of the type
		 */
		public Type(Shape.Type parent, String name) {
			this.parent = parent;
			this.name = name;
		}
		
		/**
		 * Returns true if this shape type is a type
		 * like the one given.
		 * <p>
		 * This method will search recursively up the
		 * parents to determine if this type matches
		 * the given type.
		 * @param type the type to test
		 * @return boolean
		 */
		public boolean is(Shape.Type type) {
			// check if the given type is null
			if (type == null) return false;
			// check if this type is the same object
			if (this == type) return true;
			// recursively check the type
			if (this.parent != null) {
				return this.parent.is(type);
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name);
			if (this.parent != null) {
				sb.append("->").append(this.parent.toString());
			}
			return sb.toString();
		}
		
		/**
		 * Returns the name of this type.
		 * @return String
		 */
		public String getName() {
			return this.name;
		}
	}
	
	/**
	 * Returns the {@link Shape.Type}.
	 * @return {@link Shape.Type}
	 */
	public abstract Shape.Type getType();
	
	/**
	 * Convenience method to test the type of {@link Shape}.
	 * @param type the type to test for
	 * @return boolean
	 */
	public abstract boolean isType(Shape.Type type);
	
	/**
	 * Returns the unique identifier for this shape instance.
	 * @return String
	 */
	public abstract String getId();
	
	/**
	 * Returns the center/centroid of the {@link Shape} in local coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getCenter();
	
	/**
	 * Returns the maximum radius of the shape from the center.
	 * @return double
	 * @since 2.0.0
	 */
	public abstract double getRadius();
	
	/**
	 * Returns the user data.
	 * @return Object
	 */
	public abstract Object getUserData();
	
	/**
	 * Sets the user data.
	 * @param userData the user data
	 */
	public abstract void setUserData(Object userData);
	
	/**
	 * Rotates the {@link Shape} about it's center.
	 * @param theta the rotation angle in radians
	 */
	public abstract void rotate(double theta);
	
	/**
	 * Returns the {@link Interval} of this {@link Shape} projected onto the given {@link Vector2} 
	 * given the {@link Transform}.
	 * @param n {@link Vector2} to project onto
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return {@link Interval}
	 */
	public abstract Interval project(Vector2 n, Transform transform);

	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * @param point world space point
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return boolean
	 */
	public abstract boolean contains(Vector2 point, Transform transform);
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Shape} and the given density.
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Shape}
	 */
	public abstract Mass createMass(double density);
}
