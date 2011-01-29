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
package org.dyn4j.game2d.collision;

/**
 * {@link Filter} for categorized objects.
 * <p>
 * By default the {@link CategoryFilter} will be set to category 1 and
 * have a mask of all category bits.
 * <p>
 * Basically the filter from <a href="http://www.box2d.org">Box2d</a>.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class CategoryFilter implements Filter {
	/** The category this object is in */
	protected int category;
	
	/** The categories this object can collide with */
	protected int mask;
	
	/**
	 * Default constructor.
	 * <p>
	 * By default the category is 1 and the mask is all categories.
	 */
	public CategoryFilter() {
		this.category = 1;
		this.mask = Integer.MAX_VALUE;
	}
	
	/**
	 * Full constructor.
	 * @param category the category bits
	 * @param mask the mask bits
	 */
	public CategoryFilter(int category, int mask) {
		super();
		this.category = category;
		this.mask = mask;
	}
	
	/**
	 * Returns true if the given {@link Filter} and this {@link Filter}
	 * allow the objects to interact.
	 * <p>
	 * If the given {@link Filter} is not the same type as this {@link Filter}
	 * then a value of true is returned.
	 * <p>
	 * If the given {@link Filter} is null, a value of true is returned.
	 * @param filter the other {@link Filter}
	 * @return boolean
	 */
	@Override
	public boolean isAllowed(Filter filter) {
		// make sure the given filter is not null
		if (filter == null) return true;
		// check the type
		if (filter instanceof CategoryFilter) {
			// cast the filter
			CategoryFilter cf = (CategoryFilter) filter;
			// perform the check
			return (this.category & cf.mask) > 0 && (cf.category & this.mask) > 0;
		}
		// if its not of right type always return true
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CATEGORY_FILTER[")
		.append(this.category).append("|")
		.append(this.mask).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the category bits.
	 * @return int the category bits
	 */
	public int getCategory() {
		return this.category;
	}
	
	/**
	 * Returns the mask bits.
	 * @return int the mask bits
	 */
	public int getMask() {
		return this.mask;
	}
	
	/**
	 * Sets the category bits.
	 * @param category the category bits
	 */
	public void setCategory(int category) {
		this.category = category;
	}
	
	/**
	 * Sets the mask bits.
	 * @param mask the mask bits
	 */
	public void setMask(int mask) {
		this.mask = mask;
	}
}
