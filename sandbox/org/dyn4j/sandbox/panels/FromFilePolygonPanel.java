package org.dyn4j.sandbox.panels;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a polygon from a file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FromFilePolygonPanel extends ShapePanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6390926955631356349L;
	
	/** The default polygon */
	private static final Polygon DEFAULT_POLYGON = Geometry.createUnitCirclePolygon(5, 0.5);
	
	/** The polygon read in */
	private Polygon polygon;
	
	/** The text field to show the selected file path */
	private JTextField txtFile;
	
	/**
	 * Default constructor.
	 */
	public FromFilePolygonPanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblFile = new JLabel("File", Icons.INFO, JLabel.LEFT);
		lblFile.setToolTipText("The file to load containing the points of the polygon.");
		
		this.txtFile = new JTextField();
		this.txtFile.setEditable(false);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setToolTipText("Browse the file system for a file.");
		btnBrowse.setActionCommand("browse");
		btnBrowse.addActionListener(this);
		
		JButton btnGenerate = new JButton("Generate Sample File");
		btnGenerate.setToolTipText("Shows a sample polygon file.");
		btnGenerate.setActionCommand("generate");
		btnGenerate.addActionListener(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addGroup(
					layout.createSequentialGroup()
					.addComponent(lblFile)
					.addComponent(this.txtFile)
					.addComponent(btnBrowse))
				.addComponent(btnGenerate));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(lblFile)
						.addComponent(this.txtFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(btnGenerate));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("browse".equals(event.getActionCommand())) {
			// show a jfile chooser to choose a file
			JFileChooser fileBrowser = new JFileChooser();
			int option = fileBrowser.showOpenDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				// a file was chosen so we need to read it in and parse it
				File file = fileBrowser.getSelectedFile();
				// load it up
				try {
					List<Vector2> points = new ArrayList<Vector2>();
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					while ((line = br.readLine()) != null) {
						// check for comment line
						if (!line.startsWith("#") && !line.isEmpty()) {
							// split by any white space character
							String[] coords = line.split("\\s+");
							points.add(new Vector2(
									Double.parseDouble(coords[0]),
									Double.parseDouble(coords[1])));
						}
					}
					// create the polygon
					Vector2[] vertices = new Vector2[points.size()];
					points.toArray(vertices);
					
					this.polygon = new Polygon(vertices);
					// show a success message
					JOptionPane.showMessageDialog(this, "File loaded successfully.  " + vertices.length + " points.", "Notice", JOptionPane.INFORMATION_MESSAGE);
					// set the text of the text field to the file path
					this.txtFile.setText(file.getAbsolutePath());
				} catch (NumberFormatException e) {
					// file data incorrect
					JOptionPane.showMessageDialog(this, "The flie is not the right format.  Non-numeric characters " +
							"cannot exist exception oin comment(#) lines.", "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException e) {
					// file data incorrect
					JOptionPane.showMessageDialog(this, "The file does not contain a valid polygon.  A valid polygon must be convex, " +
							"have counter-clockwise winding, and cannot contain coincident vertices.", "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (ArrayIndexOutOfBoundsException e) {
					// file format not correct
					JOptionPane.showMessageDialog(this, "The file is not the right format.  Each line should contain two " +
							"numbers separated by one or many whitespace characters.", "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException e) {
					// file not found
					JOptionPane.showMessageDialog(this, "Could not find the specified file: " + file.getAbsolutePath(), "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					// failure to read
					JOptionPane.showMessageDialog(this, "An IO exception occurred while reading the file.", "Notice", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			SampleFileDialog dialog = new SampleFileDialog(this);
			dialog.setVisible(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_POLYGON;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return this.polygon;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (this.polygon != null) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (!this.isValidInput()) {
			JOptionPane.showMessageDialog(this, "You must specify a file containing points for a polygon.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Dialog used to display a sample polygon file.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class SampleFileDialog extends JDialog implements MouseListener, ActionListener {
		/** The version id */
		private static final long serialVersionUID = 7413682412938169769L;

		/** The copy/select popup menu */
		private JPopupMenu copyMenu;
		
		/** The text area containing the text */
		private JTextArea txtFile;
		
		/**
		 * Full constructor.
		 * @param parent the component displaying this dialog
		 */
		public SampleFileDialog(Component parent) {
			super(JOptionPane.getFrameForComponent(parent), "Sample Polygon File", ModalityType.APPLICATION_MODAL);
			
			this.txtFile = new JTextArea();
			this.txtFile.setText(
					"# Sample convex polygon with counter-clockwise winding and no coincident vertices\n" +
					"# the # character must be the first character on the line to be flagged as a comment\n" +
					"\n" +
					"# Any number of blank lines can exist\n" +
					"\n" +
					"# You can use any whitespace character to separate the x and y values (space, tab, multiple spaces, etc)\n" +
					"1.0 -5.0\n" +
					"2.0\t2.0\n" +
					"\n" +
					"1.0     5.0\n");
			this.txtFile.setEditable(false);
			this.txtFile.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.txtFile.addMouseListener(this);
			
			JScrollPane scroller = new JScrollPane(this.txtFile);
			
			Container container = this.getContentPane();
			GroupLayout layout = new GroupLayout(container);
			container.setLayout(layout);
			
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addComponent(scroller));
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(scroller));
			
			this.pack();
			
			// right click copy menu
			
			this.copyMenu = new JPopupMenu();
			
			JMenuItem mnuCopy = new JMenuItem("Copy");
			mnuCopy.setActionCommand("copy");
			mnuCopy.addActionListener(this);
			
			JMenuItem mnuSelectAll = new JMenuItem("Select All");
			mnuSelectAll.setActionCommand("selectall");
			mnuSelectAll.addActionListener(this);
			
			this.copyMenu.add(mnuCopy);
			this.copyMenu.add(mnuSelectAll);
		}
		
		/**
		 * Shows the popup menu wherever the user clicked if the user clicked
		 * the popup trigger mouse key.
		 * @param event the mouse event
		 */
		private void showPopup(MouseEvent event) {
			if (event.isPopupTrigger()) {
				this.copyMenu.show(this.txtFile, event.getX(), event.getY());
			}
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			if ("copy".equals(event.getActionCommand())) {
				// copy the selected text into the clipboard
				this.txtFile.copy();
			} else {
				// select all
				this.txtFile.selectAll();
			}
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			this.showPopup(e);
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			this.showPopup(e);
		}
		
		// mouse events not used
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
