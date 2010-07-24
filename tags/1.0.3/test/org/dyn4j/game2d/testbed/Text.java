/*
 * Copyright (c) 2010, William Bittle
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
import java.awt.GraphicsConfiguration;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an <code>AttributedString</code> to provide convenience 
 * methods for rendering.
 * <p>
 * The desired width of the text may not be the real width of the text.  This happens
 * if there is a string of characters that spans for longer than the desired width.  If this
 * happens, the real width can be retrieved by the {@link #getWidth()} method.
 * <p>
 * After calling the {@link #generate()} method, one can still call any of the set methods.  The
 * caller should take note that some set methods require that the {@link #generate()} method be
 * called again to update the object.
 * <p>
 * The {@link #generate()} method honors '\n' characters as new lines.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class Text {
	/** The new line character = {@value #NEW_LINE_CHARACTER} */
	public static final char NEW_LINE_CHARACTER = '\n';
	
	/** Double representing that the layout is unbounded */
	public static final double UNBOUNDED = -1;
	
	/** The default font render context */
	protected static final FontRenderContext DEFAULT_FONT_RENDER_CONTEXT = new FontRenderContext(null, false, true);
	
	/**
	 * Enumeration to represent the 3 different alignments
	 * when rendering text to an image or graphics object.
	 * @author William Bittle
	 * @version 1.0.3
	 * @since 1.0.0
	 */
	public static enum Alignment {
		/** Text align left */
		LEFT,
		/** Text align right */
		RIGHT,
		/** Text align center */
		CENTER
	}
	
	/** The text created from the given iterator */
	protected AttributedString attributedString = null;
	
	/** The font render context */
	protected FontRenderContext fontRenderContext = Text.DEFAULT_FONT_RENDER_CONTEXT;

	/** The desired width */
	protected double desiredWidth = Text.UNBOUNDED;
	
	/** The desired alignment */
	protected Text.Alignment alignment = Text.Alignment.LEFT;

	/** The layouts used to render */
	protected List<TextLayout> layouts = new ArrayList<TextLayout>();
	
	/** The width of the text block */
	protected double width = 0;
	
	/** The height of the text block */
	protected double height = 0;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Defaults to an unbounded width with left aligned text.
	 * @param attributedString the string to render
	 */
	public Text(AttributedString attributedString) {
		super();
		this.attributedString = attributedString;
	}

	/**
	 * Returns the font render context.
	 * @return FontRenderContext
	 */
	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}
	
	/**
	 * Sets the font render context.
	 * <p>
	 * This method requires a call to {@link #generate()} to update.
	 * @param fontRenderContext the font render context
	 */
	public void setFontRenderContext(FontRenderContext fontRenderContext) {
		this.fontRenderContext = fontRenderContext;
	}
	
	/**
	 * Returns the current text alignment.
	 * @return {@link Text.Alignment}
	 */
	public Text.Alignment getAlignment() {
		return alignment;
	}
	
	/**
	 * Sets the text alignment.
	 * @param alignment the text alignment
	 */
	public void setAlignment(Text.Alignment alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * Returns the height of this text.
	 * <p>
	 * Returns zero until {@link #generate()} is called. 
	 * @return double
	 */
	public double getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the width of this text.
	 * <p>
	 * If the given text was unable to fit the {@link #desiredWidth}
	 * then this will return the width that the text needed.
	 * <p>
	 * Returns zero until {@link #generate()} is called. 
	 * @return double
	 */
	public double getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the desired width of this text.
	 * @return double
	 */
	public double getDesiredWidth() {
		return this.desiredWidth;
	}
	
	/**
	 * Sets the desired width.
	 * <p>
	 * This method requires a call to {@link #generate()} to update.
	 * @param width the desired width
	 */
	public void setDesiredWidth(double width) {
		this.desiredWidth = width;
	}
	
	/**
	 * Generates the layouts required to fit the text in the
	 * desired width.
	 * <p>
	 * This method honors '\n' characters as new lines be default.
	 * <p>
	 * If the text cannot fit within the desired width, the {@link #getWidth()} method
	 * can be used to get the full width of the text.
	 */
	public void generate() {
		AttributedCharacterIterator iterator = this.attributedString.getIterator();

		if (this.desiredWidth == Text.UNBOUNDED) {
			int beginIndex = 0;
			int endIndex = 0;
		    for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {
		        if (c == Text.NEW_LINE_CHARACTER) {
		        	this.layouts.add(new TextLayout(this.attributedString.getIterator(null, beginIndex, endIndex), this.fontRenderContext));
		        	beginIndex = endIndex + 1;
		        }
		        endIndex++;
		    }
		    this.layouts.add(new TextLayout(this.attributedString.getIterator(null, beginIndex, endIndex), this.fontRenderContext));
		} else {
		    // look for new line characters and record their index
		    List<Integer> newLineLocations = new ArrayList<Integer>();
		    for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {
		        if (c == Text.NEW_LINE_CHARACTER) {
		        	newLineLocations.add(iterator.getIndex());
		        }
		    }
		    
		    // create a line break measurer
		    LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, this.fontRenderContext);
		    int i = 0;
		    // loop through the iterator generating the layouts
		    while (measurer.getPosition() < iterator.getEndIndex()) {
		    	// declare the layout
		    	TextLayout layout = null;
		    	// determine if we need to to an indexed layout to account for new line characters
		    	if (i < newLineLocations.size()) {
		    		// if we havent made it to all the new line characters yet then make a 
		    		// layout based on the location of the next new line character
		    		layout = measurer.nextLayout((float) this.width, newLineLocations.get(i) + 1, false);
		    		// if the current position is now at a new line character then
		    		// increment the number of new line character's we have encountered
			        if (measurer.getPosition() == newLineLocations.get(i) + 1) {
			        	i++;
			        }
		    	} else {
		    		// once we have passed all new line characters just do the normal 
		    		// next layout call
		    		layout = measurer.nextLayout((float) this.width);
		    	}
		    	// add the layout to the layout list
		        this.layouts.add(layout);
		    }
		}
	    // set the bounds
		for (TextLayout layout : layouts) {
			this.width = Math.max(this.width, layout.getVisibleAdvance());
			this.height += (layout.getAscent() + layout.getDescent() + layout.getLeading());
		}
	}

	/**
	 * Renders this text to the given image.
	 * @param image the image to render to
	 * @param x the x position to render from
	 * @param y the y position to render from
	 */
	public void render(BufferedImage image, double x, double y) {
		this.render((Graphics2D) image.getGraphics(), x, y);
	}

	/**
	 * Creates a new <code>BufferedImage</code> image object and renders this text to it.
	 * @param graphicsConfiguration the graphics configuration
	 * @param transparency the alpha value
	 * @return BufferedImage
	 */
	public BufferedImage render(GraphicsConfiguration graphicsConfiguration, int transparency) {
		BufferedImage image = graphicsConfiguration.createCompatibleImage(
				(int) Math.ceil(this.height),
				(int) Math.ceil(this.width),
				transparency);
		this.render(image, 0, 0);
		return image;
	}
	
	/**
	 * Renders the text to the given graphics object at the given x,y coordinates.
	 * <p>
	 * The x,y coordinates are the top left corner of the box that contains this text.
	 * @param graphics the graphics object to render to
	 * @param x the x coordinate to start rendering from
	 * @param y the y coordinate to start rendering from
	 */
	public void render(Graphics2D graphics, double x, double y) {
		double xPos = x;
		double yPos = y;
		for (TextLayout layout : this.layouts) {
			xPos = x;
			yPos += layout.getAscent();
			if (this.alignment == Text.Alignment.LEFT) {
				xPos += layout.isLeftToRight() ? 0 : this.width - layout.getAdvance();
			} else if (this.alignment == Text.Alignment.RIGHT) {
				xPos += layout.isLeftToRight() ? this.width - layout.getAdvance() : 0;
			} else {
				xPos += (this.width - layout.getVisibleAdvance()) / 2.0f;
			}
			layout.draw(graphics, (float) xPos, (float) yPos);
			yPos += layout.getDescent() + layout.getLeading();
		}
	}
}
