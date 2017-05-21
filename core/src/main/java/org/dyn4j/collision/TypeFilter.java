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
package org.dyn4j.collision;

/**
 * A base implementation of a class hierarchy {@link Fixture} {@link Filter}.
 * <p>
 * This class is not used directly but instead extended by marker classes (see example below).
 * <p>
 * This filter is designed to allow a <strong>hierarchy</strong> of categories where each category
 * can collide with it's parents and it's descendants, but not it's siblings.
 * <p>
 * For example, imagine we have the following hierarchy of categories:
 * <pre>
 *             Root Category
 *             /           \
 *        Category1     Category2
 *        /      \
 * Category 3  Category 4
 * </pre>
 * This hierarchy allows the following:
 * <ul>
 * <li>A fixture with the Root category can collide with anything.</li>
 * <li>A fixture with the Category1 category can collide with the Category1, Category3, Category4, and Root categories. (i.e. not Category2)</li>
 * <li>A fixture with the Category2 category can collide with the Category2 and Root categories only.</li>
 * <li>A fixture with the Category3 category can collide with the Category3, Category1, and Root categories.</li>
 * <li>A fixture with the Category4 category can collide with the Category4, Category1, and Root categories.</li>
 * </ul>
 * <p>
 * To implement this you create a class for each category (you could put these in
 * their own class file, its done this way for brevity):
 * <pre>
 * public final class Categories {
 *	private static class Root extends TypeFilter {}
 *	private static class Category1 extends Root {}
 *	private static class Category2 extends Root {}
 *	private static class Category3 extends Category1 {}
 *	private static class Category4 extends Category1 {}
 *	
 *	public static final TypeFilter ROOT = new Root();
 *	public static final TypeFilter CATEGORY1 = new Category1();
 *	public static final TypeFilter CATEGORY2 = new Category2();
 *	public static final TypeFilter CATEGORY3 = new Category3();
 *	public static final TypeFilter CATEGORY4 = new Category4();
 * }
 * // then set the filter on the fixtures
 * fixture.setFilter(Categories.ROOT);
 * // or
 * fixture.setFilter(Categories.CATEGORY1);
 * </pre>
 * @author William Bittle
 * @version 3.0.2
 * @since 3.0.2
 */
public abstract class TypeFilter implements Filter {
	/**
	 * Returns true under the following conditions:
	 * <ol>
	 * <li>If this filter is the same type as the given filter.</li>
	 * <li>If this filter type is a descendant of the given filter's type.</li>
	 * <li>If the given filter's type is a descendant of this filter's type.</li>
	 * </ol>
	 * If the given filter is not of type {@link TypeFilter} then false is returned.
	 * <p>
	 * If the given filter is null then false is returned.
	 * @param filter the other filter
	 */
	@Override
	public boolean isAllowed(Filter filter) {
		// if its null then just return
		if (filter == null) return false;
		// check for the same instance
		if (this == filter) return true;
		// make sure the given filter is a TypeFilter
		if (filter instanceof TypeFilter) {
			// because the TypeFilter class is abstract, this.getClass() should never return
			// TypeFilter, but should return the type of the class that extends TypeFilter
			
			// then check the types
			if (this.getClass().isInstance(filter) || filter.getClass().isInstance(this)) {
				// if they are the same type then return true
				// if the given filter is a descendant type of this filter type then return true
				// if this type is a descendant of the given filter's type then return true
				return true;
			}
		}
		// otherwise return false
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TypeFilter[ClassName=").append(this.getClass().getName()).append("]");
		return sb.toString();
	}
}
