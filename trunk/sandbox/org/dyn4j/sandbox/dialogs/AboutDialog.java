/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.dyn4j.Version;
import org.dyn4j.sandbox.Sandbox;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog showing the about information.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class AboutDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = -5188464720880815365L;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private AboutDialog(Window owner) {
		super(owner, Resources.getString("dialog.about.title"), ModalityType.APPLICATION_MODAL);
		// set the icon
		this.setIconImage(Icons.ABOUT.getImage());
		// set the size
		this.setPreferredSize(new Dimension(450, 500));
		
		Container container = this.getContentPane();
		
		// set the layout
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// add the logo to the top
		JLabel icon = new JLabel();
		icon.setIcon(Icons.SANDBOX_128);
		icon.setText(MessageFormat.format(Resources.getString("dialog.about.text"), Sandbox.VERSION, Version.getVersion()));
		
		// add the about text section with clickable links
		JTextPane text = new JTextPane();
		text.setEditable(false);
		try {
			text.setPage(this.getClass().getResource(Resources.getString("dialog.about.html")));
		} catch (IOException e) {
			// if the file is not found then just set the text to empty
			text.setText(Resources.getString("dialog.about.html.error"));
		}
		// add a hyperlink listener to open links in the default browser
		text.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				// make sure the hyperlink event is a "onclick"
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					// make sure accessing the desktop is supported
					if (Desktop.isDesktopSupported()) {
						// get the current desktop
						Desktop desktop = Desktop.getDesktop();
						// make sure that browsing is supported
						if (desktop.isSupported(Desktop.Action.BROWSE)) {
							// if so then attempt to load the page
							// in the default browser
							try {
								URI uri = e.getURL().toURI();
								desktop.browse(uri);
							} catch (URISyntaxException ex) {
								// this shouldn't happen
								System.err.println(MessageFormat.format(Resources.getString("dialog.about.uri.error"), e.getURL()));
							} catch (IOException ex) {
								// this shouldn't happen either since
								// most desktops have a default program to
								// open urls
								System.err.println(Resources.getString("dialog.about.navigate.error"));
							}
						}
					}
				}
			}
		});
		// wrap the text pane in a scroll pane just in case
		JScrollPane scroller = new JScrollPane(text);
		
		container.add(icon);
		container.add(scroller);
		
		this.pack();
	}
	
	/**
	 * Shows the about dialog.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		// create the dialog
		AboutDialog dialog = new AboutDialog(owner);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
	}
}
