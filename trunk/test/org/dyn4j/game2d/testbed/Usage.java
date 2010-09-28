/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.testbed;

/**
 * Class used to track various usage information for the TestBed.
 * <p>
 * The percentages are updated every second.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class Usage {
	/** One second in nanoseconds */
	private static final long ONE_SECOND_IN_NANOSECONDS = 1000000000;
	
	/** The refresh rate in nanoseconds */
	private long refreshRate;
	
	/** The elapsed time since the last evaluation */
	private long elapsedTime;
	
	/** The time used for rendering */
	private long renderTime;
	
	/** The time used for input polling */
	private long inputTime;
	
	/** The time used for updating */
	private long updateTime;
	
	/** The time used by the system */
	private long systemTime;
	
	/** The total memory */
	private long totalMemoryAcc;
	
	/** The free memory */
	private long freeMemoryAcc;
	
	/** The number of iterations */
	private long iterations;
	
	/** Last second's render to total time percentage */
	private double renderTimePercentage;
	
	/** Last second's update to total time percentage */
	private double updateTimePercentage;
	
	/** Last second's input to total time percentage */
	private double inputTimePercentage;
	
	/** Last second's system to total time percentage */
	private double systemTimePercentage;
	
	/** Last second's used memory percentage */
	private double usedMemoryPercentage;
	
	/** Last second's free memory percentage */
	private double freeMemoryPercentage;
	
	/** Last second's total memory */
	private double totalMemory;
	
	/**
	 * Default constructor.
	 * <p>
	 * Refreshes every second.
	 */
	public Usage() {
		this(Usage.ONE_SECOND_IN_NANOSECONDS);
	}
	
	/**
	 * Full constructor.
	 * @param refreshRate the rate in nanoseconds
	 */
	public Usage(long refreshRate) {
		this.refreshRate = refreshRate;
	}
	
	/**
	 * Updates the elapsed time and performs an evaluation of
	 * usage at one second intervals.
	 * @param elapsedTime the elapsed time since the last update in nanoseconds
	 */
	public void update(long elapsedTime) {
		// increment the total time
		this.elapsedTime += elapsedTime;
		// increment the total and free memory
		Runtime runtime = Runtime.getRuntime();
		this.totalMemoryAcc += runtime.totalMemory();
		this.freeMemoryAcc += runtime.freeMemory();
		this.iterations++;
		// has it been a second?
		if (this.elapsedTime >= this.refreshRate) {
			// calculate the system time
			this.systemTime = this.elapsedTime - (this.renderTime + this.inputTime + this.updateTime);
			// calculate the percentages of the total time
			this.renderTimePercentage = (double) this.renderTime / (double) this.elapsedTime;
			this.updateTimePercentage = (double) this.updateTime / (double) this.elapsedTime;
			this.inputTimePercentage = (double) this.inputTime / (double) this.elapsedTime;
			if (this.systemTime > 0) {
				this.systemTimePercentage = (double) this.systemTime / (double) this.elapsedTime;
			} else {
				this.systemTimePercentage = 0;
			}
			// calculate the percentage of memory free/used
			this.totalMemory = (double) this.totalMemoryAcc / (double) this.iterations;
			double freeMem = (double) this.freeMemoryAcc / (double) this.iterations;
			double usedMem = this.totalMemory - freeMem;
			this.freeMemoryPercentage = freeMem / this.totalMemory;
			this.usedMemoryPercentage = usedMem / this.totalMemory;
			this.elapsedTime = 0;
			this.renderTime = 0;
			this.updateTime = 0;
			this.inputTime = 0;
		}
	}
	
	/**
	 * Sets the refresh rate.
	 * @param refreshRate the rate in nanoseconds
	 */
	public void setRefreshRate(long refreshRate) {
		this.refreshRate = refreshRate;
	}
	
	/**
	 * Returns the refresh rate.
	 * @return long the refresh rate in nanoseconds
	 */
	public long getRefreshRate() {
		return this.refreshRate;
	}
	
	/**
	 * Increments the total render time by the given
	 * elapsed time in nanoseconds.
	 * @param elapsedTime the elapsed time in nanoseconds
	 */
	public void setRender(long elapsedTime) {
		this.renderTime += elapsedTime;
	}
	
	/**
	 * Increments the total input time by the given
	 * elapsed time in nanoseconds.
	 * @param elapsedTime the elapsed time in nanoseconds
	 */
	public void setInput(long elapsedTime) {
		this.inputTime += elapsedTime;
	}
	
	/**
	 * Increments the total update time by the given
	 * elapsed time in nanoseconds.
	 * @param elapsedTime the elapsed time in nanoseconds
	 */
	public void setUpdate(long elapsedTime) {
		this.updateTime += elapsedTime;
	}
	
	/**
	 * Returns the render time as a percentage of the total time.
	 * @return double
	 */
	public double getRenderTimePercentage() {
		return this.renderTimePercentage;
	}
	
	/**
	 * Returns the input time as a percentage of the total time.
	 * @return double
	 */
	public double getInputTimePercentage() {
		return this.inputTimePercentage;
	}
	
	/**
	 * Returns the update time as a percentage of the total time.
	 * @return double
	 */
	public double getUpdateTimePercentage() {
		return this.updateTimePercentage;
	}
	
	/**
	 * Returns the system time as a percentage of the total time.
	 * @return double
	 */
	public double getSystemTimePercentage() {
		return this.systemTimePercentage;
	}
	
	/**
	 * Returns the free memory as a percentage of the total memory available.
	 * @return double
	 */
	public double getFreeMemoryPercentage() {
		return this.freeMemoryPercentage;
	}
	
	/**
	 * Returns the used memory as a percentage of the total memory available.
	 * @return double
	 */
	public double getUsedMemoryPercentage() {
		return this.usedMemoryPercentage;
	}
	
	/**
	 * Returns the total memory.
	 * @return double
	 */
	public double getTotalMemory() {
		return this.totalMemory;
	}
}
