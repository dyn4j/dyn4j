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