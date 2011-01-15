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

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * Represents a one line piece of text that a height and width can be determined.
 * @author William Bittle
 * @version 2.2.1
 * @since 2.2.1
 */
public class SimpleText {
	/** The attributed character iterator */
	protected AttributedCharacterIterator text;
	
	/**
	 * Default constructor.
	 * @param text the text string
	 */
	public SimpleText(String text) {
		this.text = new AttributedString(text).getIterator();
	}
	
	/**
	 * Optional constructor.
	 * @param text the pre-formated attributed string
	 */
	public SimpleText(AttributedString text) {
		this.text = text.getIterator();
	}
	
	/**
	 * Returns the width of this text given the graphics object.
	 * <p>
	 * This method uses the current font rendering context of the given
	 * graphics object.
	 * @param g the graphics object
	 * @return double
	 */
	public double getWidth(Graphics2D g) {
		TextLayout layout = new TextLayout(this.text, g.getFontRenderContext());
		return layout.getVisibleAdvance();
	}
	
	/**
	 * Returns the height of this text given the graphics object.
	 * <p>
	 * This method uses the current font rendering context of the given
	 * graphics object.
	 * @param g the graphics object
	 * @return double
	 */
	public double getHeight(Graphics2D g) {
		TextLayout layout = new TextLayout(this.text, g.getFontRenderContext());
		return layout.getLeading() + layout.getAscent() + layout.getDescent();
	}
	
	/**
	 * Renders this text to the given graphics object at the given coordinates.
	 * @param g the graphics objec to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void render(Graphics2D g, double x, double y) {
		TextLayout layout = new TextLayout(this.text, g.getFontRenderContext());
		layout.draw(g, (float)x, (float)y + layout.getAscent());
	}
}
