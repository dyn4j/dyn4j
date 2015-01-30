/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import java.awt.Window;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxRay;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to create or edit a ray.
 * @author William Bittle
 * @version 1.0.7
 * @since 1.0.1
 */
public class RayPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 498818000863671269L;

	/** The name label */
	protected JLabel lblName;
	
	/** The name text field */
	protected JTextField txtName;
	
	/** The start point label */
	private JLabel lblStart;
	
	/** The direction label */
	private JLabel lblDirection;
	
	/** The start point x text field */
	private JFormattedTextField txtSX;
	
	/** The start point y text field */
	private JFormattedTextField txtSY;
	
	/** The direction text field */
	private JFormattedTextField txtD;

	/** The length label */
	private JLabel lblLength;
	
	/** The length text field */
	private JFormattedTextField txtLength;
	
	/** The infinite length label */
	private JLabel lblInfiniteLength;
	
	/** The infinite length checkbox */
	private JCheckBox chkInfiniteLength;

	/** The ignore sensors label */
	private JLabel lblIgnoreSensors;
	
	/** The ignore sensors checkbox */
	private JCheckBox chkIgnoreSensors;

	/** The test all label */
	private JLabel lblTestAll;
	
	/** The test all checkbox */
	private JCheckBox chkTestAll;
	
	/**
	 * Full constructor.
	 * @param ray the ray if editing
	 */
	public RayPanel(SandboxRay ray) {
		super();
		
		// get initial values
		String name = ray.getName();
		Vector2 s = ray.getStart();
		double d = ray.getDirection();
		double l = ray.getLength();
		boolean infinite = ray.getLength() == 0.0;
		boolean sensors = ray.isIgnoreSensors();
		boolean all = ray.isAll();
		
		// fix the direction to be from [0, 2pi] instead of [-pi, pi]
		if (d < 0.0) {
			d += 2.0 * Math.PI;
		}
		
		this.lblName = new JLabel(Messages.getString("panel.ray.name"), Icons.INFO, JLabel.LEFT);
		this.lblName.setToolTipText(Messages.getString("panel.ray.name.tooltip"));
		this.txtName = new JTextField(name);
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		
		this.lblStart = new JLabel(Messages.getString("panel.ray.start"), Icons.INFO, JLabel.LEFT);
		this.lblStart.setToolTipText(Messages.getString("panel.ray.start.tooltip"));
		
		this.txtSX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.ray.start.format")));
		this.txtSX.addFocusListener(new SelectTextFocusListener(this.txtSX));
		this.txtSX.setValue(s.x);
		this.txtSX.setColumns(6);
		
		this.txtSY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.ray.start.format")));
		this.txtSY.addFocusListener(new SelectTextFocusListener(this.txtSY));
		this.txtSY.setValue(s.y);
		this.txtSY.setColumns(6);
		
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		this.lblDirection = new JLabel(Messages.getString("panel.ray.direction"), Icons.INFO, JLabel.LEFT);
		this.lblDirection.setToolTipText(MessageFormat.format(Messages.getString("panel.ray.direction.tooltip"), Messages.getString("unit.rotation")));
		this.txtD = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.ray.direction.format")));
		this.txtD.addFocusListener(new SelectTextFocusListener(this.txtD));
		this.txtD.setValue(Math.toDegrees(d));
		this.txtD.setColumns(6);
		
		this.lblInfiniteLength = new JLabel(Messages.getString("panel.ray.infiniteLength"), Icons.INFO, JLabel.LEFT);
		this.lblInfiniteLength.setToolTipText(Messages.getString("panel.ray.infiniteLength.tooltip"));
		this.chkInfiniteLength = new JCheckBox();
		this.chkInfiniteLength.setSelected(infinite);
		
		this.lblLength = new JLabel(Messages.getString("panel.ray.length"), Icons.INFO, JLabel.LEFT);
		this.lblLength.setToolTipText(MessageFormat.format(Messages.getString("panel.ray.length.tooltip"), Messages.getString("unit.length")));
		this.txtLength = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.ray.length.format")));
		this.txtLength.addFocusListener(new SelectTextFocusListener(this.txtLength));
		this.txtLength.setValue(l);
		this.txtLength.setEnabled(false);
		
		this.lblIgnoreSensors = new JLabel(Messages.getString("panel.ray.ignoreSensors"), Icons.INFO, JLabel.LEFT);
		this.lblIgnoreSensors.setToolTipText(Messages.getString("panel.ray.ignoreSensors.tooltip"));
		this.chkIgnoreSensors = new JCheckBox();
		this.chkIgnoreSensors.setSelected(sensors);
		
		this.lblTestAll = new JLabel(Messages.getString("panel.ray.testAll"), Icons.INFO, JLabel.LEFT);
		this.lblTestAll.setToolTipText(Messages.getString("panel.ray.testAll.tooltip"));
		this.chkTestAll = new JCheckBox();
		this.chkTestAll.setSelected(all);
		
		this.chkInfiniteLength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (chkInfiniteLength.isSelected()) {
					txtLength.setValue(0.0);
					txtLength.setEnabled(false);
				} else {
					txtLength.setEnabled(true);
				}
			}
		});
		
		// setup the sections
		
		GroupLayout layout;
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblStart)
						.addComponent(this.lblDirection)
						.addComponent(this.lblInfiniteLength)
						.addComponent(this.lblLength)
						.addComponent(this.lblIgnoreSensors)
						.addComponent(this.lblTestAll))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtSX)
								.addComponent(lblX)
								.addComponent(this.txtSY)
								.addComponent(lblY))
						.addComponent(this.txtD)
						.addComponent(this.chkInfiniteLength)
						.addComponent(this.txtLength)
						.addComponent(this.chkIgnoreSensors)
						.addComponent(this.chkTestAll)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblStart)
						.addComponent(this.txtSX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtSY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblDirection)
						.addComponent(this.txtD, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblInfiniteLength)
						.addComponent(this.chkInfiniteLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLength)
						.addComponent(this.txtLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblIgnoreSensors)
						.addComponent(this.chkIgnoreSensors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblTestAll)
						.addComponent(this.chkTestAll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Returns a new ray with the user's selections.
	 * @return {@link SandboxRay}
	 */
	public SandboxRay getRay() {
		SandboxRay ray = new SandboxRay(
				// the ray name
				this.txtName.getText(),
				// the start point
				new Vector2(ControlUtilities.getDoubleValue(this.txtSX), ControlUtilities.getDoubleValue(this.txtSY)),
				// the direction
				Math.toRadians(ControlUtilities.getDoubleValue(this.txtD)));
		// set the other fields
		if (this.chkInfiniteLength.isSelected()) {
			ray.setLength(SandboxRay.INFINITE);
		} else {
			ray.setLength(ControlUtilities.getDoubleValue(this.txtLength));
		}
		ray.setIgnoreSensors(this.chkIgnoreSensors.isSelected());
		ray.setAll(this.chkTestAll.isSelected());
		return ray;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// the name can't be empty
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			return false;
		}
		// the length cannot be negative
		if (!this.chkInfiniteLength.isSelected() && ControlUtilities.getDoubleValue(this.txtLength) <= 0.0) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.ray.missingName"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// the length cannot be negative
		if (!this.chkInfiniteLength.isSelected() && ControlUtilities.getDoubleValue(this.txtLength) <= 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.ray.invalidLength"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
