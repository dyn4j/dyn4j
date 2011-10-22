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
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.decompose.Decomposer;
import org.dyn4j.sandbox.dialogs.SampleFileDialog;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a polygon from a file.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class FromFileNonConvexPolygonPanel extends NonConvexShapePanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6390926955631356349L;
	
	/** The text field to show the selected file path */
	private JTextField txtFile;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;

	/** The decomposotion algorithm */
	private Decomposer decomposer;

	/** The read-in vertices */
	private Vector2[] vertices;
	
	/** The decomposed polygon */
	private List<Convex> decomposition;
	
	/**
	 * Full constructor.
	 * @param decomposer the decomposition algorithm
	 */
	public FromFileNonConvexPolygonPanel(Decomposer decomposer) {
		this.decomposer = decomposer;
		
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
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225));
		
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
					this.vertices = new Vector2[points.size()];
					points.toArray(this.vertices);
					
					this.vertices = Geometry.cleanse(this.vertices);
					this.decomposition = this.decomposer.decompose(this.vertices);
					
					// set the preview panel to the new points
					this.pnlPreview.setDecomposition(this.decomposition);
					// set the text of the text field to the file path
					this.txtFile.setText(file.getAbsolutePath());
				} catch (NumberFormatException e) {
					// file data incorrect
					JOptionPane.showMessageDialog(this, "The flie is not the right format.  Non-numeric characters " +
							"cannot exist exception on comment(#) lines.", "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException e) {
					// the file didnt contain something correctly
					JOptionPane.showMessageDialog(this, "The file does not contain a simple polygon without holes and crossing edges.", "Notice", JOptionPane.ERROR_MESSAGE);
					this.vertices = null;
				} catch (ArrayIndexOutOfBoundsException e) {
					// file format not correct
					JOptionPane.showMessageDialog(this, "The file is not the right format.  Each line should contain two " +
							"numbers separated by one or many whitespace characters.", "Notice", JOptionPane.ERROR_MESSAGE);
					this.vertices = null;
				} catch (FileNotFoundException e) {
					// file not found
					JOptionPane.showMessageDialog(this, "Could not find the specified file: " + file.getAbsolutePath(), "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					// failure to read
					JOptionPane.showMessageDialog(this, "An IO exception occurred while reading the file.", "Notice", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					// some other failure
					// get the message from the exception
					String message = e.getMessage();
					if (message == null || message.isEmpty()) {
						message = "A simple polygon cannot have crossing edges.";
					}
					JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.ERROR_MESSAGE);
					this.vertices = null;
				}
			}
		} else {
			SampleFileDialog.show(
					this,
					"# Sample non-convex simple polygon (without holes and crossed edges)\n" +
					"# the # character must be the first character on the line to be flagged as a comment\n" +
					"\n" +
					"# Any number of blank lines can exist\n" +
					"\n" +
					"# You can use any whitespace character to separate the x and y values (space, tab, multiple spaces)\n" +
					"-0.5 -0.5\n" +
					"0.5\t-0.5\n" +
					"\n" +
					"0.5     0.5\n" +
					"0.0   0.0\n" +
					"-0.5\t0.5\n");
		}
	}

	/**
	 * Sets the decomposition algorithm.
	 * @param decomposer the decomposition algorithm
	 */
	public void setDecomposer(Decomposer decomposer) {
		this.decomposer = decomposer;
		// rerun the decomposition on the current point set
		try {
			// attempt to refresh the decomposition
			this.decomposition = this.decomposer.decompose(this.vertices);
			// show it in the preview panel
			this.pnlPreview.setDecomposition(this.decomposition);
		} catch (Exception e) {}
	}
	
	/**
	 * Returns the current decomposition algorithm.
	 * @return Decomposer
	 */
	public Decomposer getDecomposer() {
		return this.decomposer;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.NonConvexShapePanel#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		return this.decomposition;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (this.decomposition != null) {
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
			JOptionPane.showMessageDialog(this, "You must specify a file containing points for a decomposable polygon.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
