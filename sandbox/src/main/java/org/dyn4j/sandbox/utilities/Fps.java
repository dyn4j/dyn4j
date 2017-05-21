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
package org.dyn4j.sandbox.utilities;

import java.text.DecimalFormat;

import org.dyn4j.sandbox.resources.Messages;

/**
 * Utility class for calculating the frames per second.
 * <p>
 * Before the first fps calculation the fps is -1.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class Fps {
	/** One second in nano seconds */
	protected static final long ONE_SECOND_IN_NANOSECONDS = 1000000000;
	
	/** The amount of time since last fps calculation */
	protected long time = 0;
	
	/** The number of frames */
	protected long frames = 0;
	
	/** The calculated frames per second */
	protected double fps = 0;
	
	/** The format to use */
	protected DecimalFormat format = new DecimalFormat(Messages.getString("fps.format"));
	
	/**
	 * Default constructor.
	 */
	public Fps() {
		super();
	}
	
	/**
	 * Updates the fps.
	 * @param elapsedTime the elapsed time since the last update; in nanoseconds
	 * @return boolean true if the FPS was updated
	 */
	public boolean update(long elapsedTime) {
		// increment the frame count
		this.frames++;
		// increment the time
		this.time += elapsedTime;
		// check for one second of time passed
		if (this.time >= Fps.ONE_SECOND_IN_NANOSECONDS) {
			synchronized (this) {
				// recalculate the fps
				this.fps = this.frames * Fps.ONE_SECOND_IN_NANOSECONDS / this.time;
			}
			// reset frames
			this.frames = 0;
			// reset the time
			this.time = 0;
			// we updated
			return true;
		}
		// we didnt update
		return false;
	}
	
	/**
	 * Returns the current frames per second.
	 * @return double
	 */
	public synchronized double getFps() {
		return fps;
	}
	
	/**
	 * Returns the current frames per second.
	 * @return String
	 */
	public synchronized String getFpsString() {
		return format.format(fps);
	}
}
