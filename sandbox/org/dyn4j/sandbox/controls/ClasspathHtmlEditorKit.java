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
package org.dyn4j.sandbox.controls;

import java.net.URL;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

/**
 * Html editor kit that handles classpath resources.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClasspathHtmlEditorKit extends HTMLEditorKit {
	/** The version id */
	private static final long serialVersionUID = -4557600154901548699L;

	/* (non-Javadoc)
	 * @see javax.swing.text.html.HTMLEditorKit#getViewFactory()
	 */
	@Override
	public ViewFactory getViewFactory() {
		// return a class path view factory
		return new ClasspathViewFactory();
	}
	
	/**
	 * View factory used to return view classes for the HTML tags.
	 * <p>
	 * This factory will handle img tags with src attributes of "classpath://".
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ClasspathViewFactory extends HTMLEditorKit.HTMLFactory {
		/* (non-Javadoc)
		 * @see javax.swing.text.html.HTMLEditorKit.HTMLFactory#create(javax.swing.text.Element)
		 */
		@Override
		public View create(Element elem) {
			Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag) {
				HTML.Tag kind = (HTML.Tag)o;
				if (kind == HTML.Tag.IMG) {
					return new ClasspathImageView(elem);
				}
			}
			return super.create(elem);
		}
	}

	/**
	 * Image view used to show an image in an HTML document that is loaded from the classpath.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ClasspathImageView extends ImageView {
		/**
		 * Full constructor.
		 * @param elem the HTML element
		 */
		public ClasspathImageView(Element elem) {
			super(elem);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.text.html.ImageView#getImageURL()
		 */
		@Override
		public URL getImageURL() {
			// get the src attribute
			String src = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
			// if its null just return
			if (src == null) return null;
			// if it starts with class:// then we need to look in the classpath
			if (src.startsWith("classpath://")) {
				// prepare the path
				String path = src.replace("classpath:/", "");
				// return the url to the classpath
				return this.getClass().getResource(path);
			} else {
				return super.getImageURL();
			}
		}
	}
}