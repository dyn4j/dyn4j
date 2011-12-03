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

import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.decompose.Bayazit;
import org.dyn4j.geometry.decompose.Decomposer;
import org.dyn4j.geometry.decompose.EarClipping;
import org.dyn4j.geometry.decompose.SweepLine;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.controls.ComboItem;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create fixtures from a decomposable polygon.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class NonConvexPolygonPanel extends NonConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -6622003790931052412L;

	/** The bayazit algorithm item */
	private static final ComboItem BAYAZIT = new ComboItem(Resources.getString("panel.concave.polygon.algorithm.bayazit"), new Bayazit());
	
	/** The ear clipping algorithm item */
	private static final ComboItem EAR_CLIPPING = new ComboItem(Resources.getString("panel.concave.polygon.algorithm.earClipping"), new EarClipping());
	
	/** The sweep line algorithm item */
	private static final ComboItem SWEEP_LINE = new ComboItem(Resources.getString("panel.concave.polygon.algorithm.sweepLine"), new SweepLine());
	
	/** The array of algorithms */
	private static final ComboItem[] ITEMS = new ComboItem[] {
		BAYAZIT,
		EAR_CLIPPING,
		SWEEP_LINE
	};
	
	/** The default decomposition algorithm */
	private static final ComboItem DEFAULT_ALGORITHM = BAYAZIT;
	
	// polygon panels
	
	/** The arbitrary polygon panel */
	private ArbitraryNonConvexPolygonPanel pnlArbitraryPolygon;
	
	/** The from file polygon panel */
	private FromFileNonConvexPolygonPanel pnlFromFilePolygon;
	
	// polygon radio buttons
	
	/** The arbitrary polygon radio button */
	private JRadioButton rdoArbitrary;
	
	/** The from file polygon radio button */
	private JRadioButton rdoFromFile;
	
	// decomposition algorithms
	
	/** The algorithm combo box */
	private JComboBox cmbAlgorithms;
	
	/** The panel to show/hide sources */
	private JPanel pnlSource;
	
	/**
	 * Default constructor.
	 */
	public NonConvexPolygonPanel() {
		JLabel lblAlgorithm = new JLabel(Resources.getString("panel.concave.polygon.algorithm"), Icons.INFO, JLabel.LEFT);
		lblAlgorithm.setToolTipText(Resources.getString("panel.concave.polygon.algorithm.tooltip"));
		JLabel lblSource = new JLabel(Resources.getString("panel.concave.polygon.source"), Icons.INFO, JLabel.LEFT);
		lblSource.setToolTipText(Resources.getString("panel.concave.polygon.source.tooltip"));
		
		this.cmbAlgorithms = new JComboBox(ITEMS);
		this.cmbAlgorithms.setSelectedItem(DEFAULT_ALGORITHM);
		this.cmbAlgorithms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ComboItem item = (ComboItem)cmbAlgorithms.getSelectedItem();
				Decomposer decomposer = (Decomposer)item.getValue();
				
				pnlArbitraryPolygon.setDecomposer(decomposer);
				pnlFromFilePolygon.setDecomposer(decomposer);
			}
		});
		
		Decomposer decomposer = (Decomposer)DEFAULT_ALGORITHM.getValue();
		
		this.pnlArbitraryPolygon = new ArbitraryNonConvexPolygonPanel(decomposer);
		this.pnlFromFilePolygon = new FromFileNonConvexPolygonPanel(decomposer);
		
		this.rdoArbitrary = new JRadioButton(Resources.getString("panel.concave.polygon.source.arbitrary"));
		this.rdoFromFile = new JRadioButton(Resources.getString("panel.concave.polygon.source.file"));
		
		// set arbitrary as the current one
		this.rdoArbitrary.setSelected(true);
		
		this.rdoArbitrary.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "arbitrary");
				} else {
					cl.show(pnlSource, "fromFile");
				}
			}
		});
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.rdoFromFile);
		bg.add(this.rdoArbitrary);
		
		this.pnlSource = new JPanel();
		this.pnlSource.setLayout(new CardLayout());
		this.pnlSource.add(this.pnlArbitraryPolygon, "arbitrary");
		this.pnlSource.add(this.pnlFromFilePolygon, "fromFile");
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblAlgorithm)
								.addComponent(lblSource))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.cmbAlgorithms)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.rdoFromFile)
										.addComponent(this.rdoArbitrary))))
				.addComponent(this.pnlSource));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAlgorithm)
						.addComponent(this.cmbAlgorithms, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSource)
						.addComponent(this.rdoFromFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.rdoArbitrary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.pnlSource));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.NonConvexShapePanel#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		// return the polygon for the selected radio button
		if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.getShapes();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.getShapes();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// return the polygon for the selected radio button
		if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.isValidInput();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.isValidInput();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		// return the polygon for the selected radio button
		if (this.rdoArbitrary.isSelected()) {
			this.pnlArbitraryPolygon.showInvalidInputMessage(owner);
		} else if (this.rdoFromFile.isSelected()) {
			this.pnlFromFilePolygon.showInvalidInputMessage(owner);
		}
	}
}
