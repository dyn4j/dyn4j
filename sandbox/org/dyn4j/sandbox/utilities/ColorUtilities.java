/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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

import java.awt.Color;

import org.dyn4j.sandbox.resources.Messages;

/**
 * Utility class to handle colors for both OpenGL and Java.
 * @author William Bittle
 * @version 1.0.1
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
			throw new IllegalArgumentException(Messages.getString("exception.color.notEnoughComponents"));
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
	
	/**
	 * Returns a new color that is darker or lighter than the given color
	 * by the given factor.
	 * @param color the color to modify
	 * @param factor 0.0 &le; factor &le; 1.0 darkens; 1.0 &lt; factor brightens
	 * @return Color
	 * @since 1.0.1
	 */
	public static final Color getColor(Color color, float factor) {
		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		hsb[2] = hsb[2] * factor;
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor));
	}
}
