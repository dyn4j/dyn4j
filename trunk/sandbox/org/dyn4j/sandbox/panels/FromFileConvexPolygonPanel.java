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
package org.dyn4j.sandbox.panels;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.dialogs.SampleFileDialog;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a polygon from a file.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class FromFileConvexPolygonPanel extends ConvexShapePanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6390926955631356349L;
	
	/** The default polygon */
	private static final Polygon DEFAULT_POLYGON = Geometry.createUnitCirclePolygon(5, 0.5);
	
	/** The polygon read in */
	private Polygon polygon;
	
	/** The text field to show the selected file path */
	private JTextField txtFile;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/**
	 * Default constructor.
	 */
	public FromFileConvexPolygonPanel() {
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
		
		JButton btnGenerate = new JButton("View Sample File");
		btnGenerate.setToolTipText("Shows a sample polygon file.");
		btnGenerate.setActionCommand("generate");
		btnGenerate.addActionListener(this);
		
		JLabel lblPreview = new JLabel("Preview", Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText("Shows a preview of the current shape.");
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225), (Vector2[])null);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblFile)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFile)
								.addComponent(btnBrowse))
						.addComponent(this.pnlPreview)
						.addComponent(btnGenerate)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFile)
						.addComponent(this.txtFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblPreview)
						.addComponent(this.pnlPreview))
				.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
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
					
					try {
						this.polygon = Geometry.createPolygon(vertices);
					} catch (IllegalArgumentException e) {
						// the polygon is not valid
						JOptionPane.showMessageDialog(this, "The file contains a polygon that does not meet the requirements.", "Notice", JOptionPane.INFORMATION_MESSAGE);
						// set the current polygon to null
						this.polygon = null;
					}
					// set the preview panel to the new points
					this.pnlPreview.setPoints(vertices);
					// set the text of the text field to the file path
					this.txtFile.setText(file.getAbsolutePath());
				} catch (NumberFormatException e) {
					// file data incorrect
					JOptionPane.showMessageDialog(this, "The flie is not the right format.  Non-numeric characters " +
							"cannot exist exception on comment(#) lines.", "Notice", JOptionPane.ERROR_MESSAGE);
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
			SampleFileDialog.show(
					this,
					"# Sample convex polygon with counter-clockwise winding and no coincident vertices\n" +
					"# the # character must be the first character on the line to be flagged as a comment\n" +
					"\n" +
					"# Any number of blank lines can exist\n" +
					"\n" +
					"# You can use any whitespace character to separate the x and y values (space, tab, multiple spaces)\n" +
					"1.0 -5.0\n" +
					"2.0\t2.0\n" +
					"\n" +
					"1.0     5.0\n");
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
			JOptionPane.showMessageDialog(this, "You must specify a file containing valid points for a polygon.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
