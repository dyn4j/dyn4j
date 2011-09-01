package org.dyn4j.sandbox.utilities;

import java.text.DecimalFormat;

/**
 * Utility class for calculating the frames per second.
 * <p>
 * Before the first fps calculation the fps is -1.
 * @author William Bittle
 * @version 1.0.0
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
	protected DecimalFormat format = new DecimalFormat("00.0");
	
	/**
	 * Default constructor.
	 */
	public Fps() {
		super();
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
			synchronized (this) {
				// recalculate the fps
				this.fps = this.frames * Fps.ONE_SECOND_IN_NANOSECONDS / this.time;
			}
			// reset frames
			this.frames = 0;
			// reset the time
			this.time = 0;
		}
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
