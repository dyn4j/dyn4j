/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j;

import org.dyn4j.exception.CopyException;

/**
 * The version of the engine.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public final class Unsafe {
	private Unsafe() {}
	
	/**
	 * Copies the given {@link Copyable} and attempts to cast it to T.
	 * <p>
	 * This should be safe for all dyn4j objects that implement the {@link Copyable}
	 * interface. Where this will not be safe is for any user classes that extend
	 * from dyn4j classes that implement {@link Copyable}.  The guidance is that if
	 * you extend a dyn4j class, override the copy method and ensure you return
	 * an object of the same type.
	 * @param <T> the type to cast it to
	 * @param copyable the copyable to copy
	 * @return T
	 * @throws ClassCastException if the copy method on type T doesn't return an object of type T
	 */
	@SuppressWarnings("unchecked")
	public final static <T extends Copyable<?>> T copy(T copyable) {
		Object copy = copyable.copy();
		if (!copy.getClass().equals(copyable.getClass())) {
			throw new CopyException(copyable.getClass(), copy.getClass());
		}
		return (T)copy;
	}
}
