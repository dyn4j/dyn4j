package org.dyn4j.sandbox.utilities;

import java.awt.Color;

/**
 * Utility class to handle colors for both OpenGL and Java.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColorUtilities {
	/**
	 * Returns an array of color components for the given Color object.
	 * @param color the color object
	 * @return float[]
	 */
	public static final float[] convertColor(Color color) {
		return color.getRGBComponents(null);
	}
	
	/**
	 * Returns a new Color object given the color components in the given array.
	 * @param color the color components in RGB or RGBA
	 * @return Color
	 */
	public static final Color convertColor(float[] color) {
		if (color.length == 3) {
			return new Color(color[0], color[1], color[2]);
		} else if (color.length == 4) {
			return new Color(color[0], color[1], color[2], color[3]);
		} else {
			throw new IllegalArgumentException("A color must have 3 or 4 components.");
		}
	}
	
	/**
	 * Places the RGBA values from the given Color object into the destination array.
	 * @param color the color to convert
	 * @param destination the array to hold the RGBA values; length 4
	 */
	public static final void convertColor(Color color, float[] destination) {
		color.getRGBComponents(destination);
	}
	
	/**
	 * Uses the method described at http://alienryderflex.com/hsp.html to get
	 * the <u>perceived</u> brightness of a color.
	 * @param color the color
	 * @return int brightness on the scale of 0 to 255
	 */
	public static final int getBrightness(Color color) {
		// original coefficients
		final double cr = 0.241;
		final double cg = 0.691;
		final double cb = 0.068;
		// another set of coefficients
//		final double cr = 0.299;
//		final double cg = 0.587;
//		final double cb = 0.114;
		
		double r, g, b;
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
		
		// compute the weighted distance
		double result = Math.sqrt(cr * r * r + cg * g * g + cb * b * b);
		
		return (int)result;
	}
	
	/**
	 * Returns a foreground color (for text) given a background color by examining
	 * the brightness of the background color.
	 * @param color the foreground color
	 * @return Color
	 */
	public static final Color getForegroundColorFromBackgroundColor(Color color) {
		int brightness = ColorUtilities.getBrightness(color);
		if (brightness < 130) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}
	
	/**
	 * Returns a random color given the offset and alpha values.
	 * @param offset the offset between 0.0 and 1.0
	 * @param alpha the alpha value between 0.0 and 1.0
	 * @return Color
	 */
	public static final Color getRandomColor(float offset, float alpha) {
		final float max = 1.0f;
		final float min = 0.0f;
		// make sure the offset is valid
		if (offset > max) offset = min;
		if (offset < min) offset = min;
		// use the offset to calculate the color
		float multiplier = max - offset;
		// compute the rgb values
		float r = (float)Math.random() * multiplier + offset;
		float g = (float)Math.random() * multiplier + offset;
		float b = (float)Math.random() * multiplier + offset;
		
		return new Color(r, g, b, alpha);
	}
}
