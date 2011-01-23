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
 * Utility class for calculating the frames per second.
 * <p>
 * This class also supports frame rate restricting, however, the restricting method
 * is not accurate.
 * <p>
 * Before the first fps calculation the fps is -1.
 * @author William Bittle
 * @version $Revision: 356 $
 */
public class Fps {
	/** One second in nano seconds */
	protected static final long ONE_SECOND_IN_NANOSECONDS = 1000000000;
	
	/** The amount of time since last fps calculation */
	protected long time = 0;
	
	/** The number of frames */
	protected long frames = 0;
	
	/** The calculated frames per second */
	protected double fps = -1;
	
	/** Whether to restrict the frame rate */
	protected boolean restrict = false;
	
	/** The time allocated to one frame given the desired frames per second */
	protected double frameTime = 0;
	
	/**
	 * Default constructor.
	 */
	public Fps() {
		super();
	}
	
	/**
	 * Constructor to limit the fps.
	 * @param desiredFps the desired frames per second
	 */
	public Fps(int desiredFps) {
		super();
		this.restrict = true;
		this.frameTime = Fps.ONE_SECOND_IN_NANOSECONDS / desiredFps;
	}
	
	/**
	 * Updates the fps.
	 * @param elapsedTime the elapsed time since the last update; in nanoseconds
	 */
	public void update(long elapsedTime) {
		// increment the frame count
		this.frames++;
		// increment the time
		this.time += elapsedTime;
		// check for one second of time passed
		if (this.time >= Fps.ONE_SECOND_IN_NANOSECONDS) {
			// recalculate the fps
			this.fps = this.frames * Fps.ONE_SECOND_IN_NANOSECONDS / this.time;
			// reset frames
			this.frames = 0;
			// reset the time
			this.time = 0;
		}
		
		// restrict the fps
		if (this.restrict) {
			// recalculate the sleep time
			long sleepTime = (long)(this.frameTime - elapsedTime) / 1000000;
			// make sure its greater than zero
			if (sleepTime > 0) {
				try {
					// sleep the thread
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// if the thread is interrupted
					// just eat the exception and continue
				}
			}
		}
	}
	
	/**
	 * Returns the current frames per second.
	 * @return double
	 */
	public double getFps() {
		return fps;
	}
}
