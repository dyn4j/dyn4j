/*
 * Copyright (c) 2009, William Bittle
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
 *   * Neither the name of William Bittle nor the names of its contributors may be used to endorse or 
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
package org.dyn4j.game2d.testbed;

/**
 * Represents a timer to keep track of elapsed time.
 * @author William Bittle
 * @since $Revision: 356 $
 */
public class Timer {
	/**
	 * Enumeration of timing resolutions.
	 * @author William Bittle
	 * @version $Revision: 356 $
	 */
	public static enum Resolution {
		/** accurate to the millisecond */
		MILLISECOND,
		/** accurate to the nanosecond */
		NANOSECOND
	}
	
	/** The start time */
	private long startTime = 0;
	
	/** The last update time */
	private long updateTime = 0;
	
	/** The timer resolution */
	private Timer.Resolution resolution = null;
	
	/**
	 * Default constructor.
	 * <p>
	 * Defaults to using nanosecond resolution.
	 */
	public Timer() {
		this(Timer.Resolution.NANOSECOND);
	}
	
	/**
	 * Full constructor.
	 * @param resolution the timing resolution
	 */
	public Timer(Timer.Resolution resolution) {
		super();
		this.resolution = resolution;
	}

	/**
	 * Returns the current time.
	 * @return long
	 */
	public long getCurrentTime() {
		if (this.resolution == Timer.Resolution.MILLISECOND) {
			return System.currentTimeMillis();
		} else {
			return System.nanoTime();
		}
	}
	
	/**
	 * Returns the {@link Timer}s {@link Timer.Resolution}.
	 * @return {@link Timer.Resolution}
	 */
	public Timer.Resolution getResolution() {
		return this.resolution;
	}
	
	/**
	 * Returns the elapsed time given the current time.
	 * @param currentTime the current time
	 * @return long
	 */
	public long getElapsedTime(long currentTime) {
		return currentTime - this.updateTime;
	}

	/**
	 * Returns the elapsed time assuming now as the current time and will
	 * update this {@link Timer} with the current time.
	 * @return long
	 */
	public long getElapsedTime() {
		long currentTime = this.getCurrentTime();
		long elapsedTime = currentTime - this.updateTime;
		this.update(currentTime);
		return elapsedTime;
	}
	
	/**
	 * Returns the elapsed time from the start of the {@link Timer} given the current time.
	 * @param currentTime the current time
	 * @return long
	 */
	public long getElapsedTimeFromStart(long currentTime) {
		return currentTime - this.startTime;
	}

	/**
	 * Returns the start time of the {@link Timer}.
	 * @return long
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Returns the last update time.
	 * @return long
	 */
	public long getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * Resets the {@link Timer}.
	 */
	public void reset() {
		this.startTime = this.getCurrentTime();
		this.updateTime = this.startTime;
	}

	/**
	 * Sets the start time.
	 * @param startTime the start time
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Updates the {@link Timer}.
	 * @param currentTime the current time
	 */
	public void update(long currentTime) {
		this.updateTime = currentTime;
	}
}
