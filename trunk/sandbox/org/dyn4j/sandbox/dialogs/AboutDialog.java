package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.dyn4j.Version;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog showing the about information.
 * @author William Bittle
 * @version 1.0.0
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
		super(owner, "About", ModalityType.APPLICATION_MODAL);
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
		icon.setText("<html>Sandbox - A testing application for dyn4j<br />Sandbox is using dyn4j v" + Version.getVersion() + "</html>");
		
		// add the about text section with clickable links
		JTextPane text = new JTextPane();
		text.setEditable(false);
		try {
			text.setPage(this.getClass().getResource("/org/dyn4j/sandbox/resources/about.html"));
		} catch (IOException e) {
			// if the file is not found then just set the text to empty
			text.setText("Was unable to load the about.html file.");
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
								System.err.println("A link in the about.html is not correct: " + e.getURL());
							} catch (IOException ex) {
								// this shouldn't happen either since
								// most desktops have a default program to
								// open urls
								System.err.println("Cannot navigate to link since a default program is not set or does not exist.");
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
		// show the dialog
		dialog.setVisible(true);
	}
}
