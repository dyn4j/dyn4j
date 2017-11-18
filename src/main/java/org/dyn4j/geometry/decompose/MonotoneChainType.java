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
package org.dyn4j.geometry.decompose;

/**
 * Enumeration of monotone chain types.
 * <p>
 * A monotone chain is a group of vertices that are monotone with respect to
 * some axis.  Typically x or y monotone chains are created.
 * <p>
 * A monotone polygon will always contain two monotone chains.  For a y-monotone
 * polygon, a {@link MonotoneChainType#LEFT} and {@link MonotoneChainType#RIGHT}.  For
 * a x-monotone polygon, a {@link MonotoneChainType#TOP} and {@link MonotoneChainType#BOTTOM}.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
enum MonotoneChainType {
	/** Indicates that the vertex is on the left chain of a y-monotone polygon */
	LEFT,
	/** Indicates that the vertex is on the right chain of a y-monotone polygon */
	RIGHT,
	/** Indicates that the vertex is on the top chain of a x-monotone polygon */
	TOP,
	/** Indicates that the vertex is on the bottom chain of a x-monotone polygon */
	BOTTOM
}
