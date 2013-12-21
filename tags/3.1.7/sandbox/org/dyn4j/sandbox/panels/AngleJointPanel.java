/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to create or edit an angle joint.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.0
 */
public class AngleJointPanel extends JointPanel implements InputPanel, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = -7252187387231409737L;

	/** The body 1 drop down label */
	private JLabel lblBody1;
	
	/** The body 2 drop down label */
	private JLabel lblBody2;
	
	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;
	
	/** The reference angle label */
	private JLabel lblReferenceAngle;
	
	/** The reference angle text field */
	private JFormattedTextField txtReferenceAngle;

	/** The reference angle auto compute button */
	private JToggleButton tglReferenceAngle;
	
	/** The button used to reset the reference angle (only used in edit mode) */
	private JButton btnResetReferenceAngle;
	
	/** The limit enabled label */
	private JLabel lblLimitEnabled;
	
	/** The limit enabled checkbox */
	private JCheckBox chkLimitEnabled;
	
	/** The limit minimum label */
	private JLabel lblMinimum;
	
	/** The limit maximum label */
	private JLabel lblMaximum;
	
	/** The limit minimum text field */
	private JFormattedTextField txtMinimum;
	
	/** The limit maximum text field */
	private JFormattedTextField txtMaximum;
	
	/** The ratio label */
	private JLabel lblRatio;
	
	/** The ratio text field */
	private JFormattedTextField txtRatio;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public AngleJointPanel(AngleJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		boolean limitEnabled = joint.isLimitEnabled();
		double lower = joint.getLowerLimit();
		double upper = joint.getUpperLimit();
		double ref = joint.getReferenceAngle();
		double ratio = joint.getRatio();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		this.lblBody1 = new JLabel(Messages.getString("panel.joint.body1"), Icons.INFO, JLabel.LEFT);
		this.lblBody2 = new JLabel(Messages.getString("panel.joint.body2"), Icons.INFO, JLabel.LEFT);
		this.lblBody1.setToolTipText(Messages.getString("panel.joint.body1.tooltip"));
		this.lblBody2.setToolTipText(Messages.getString("panel.joint.body2.tooltip"));
		
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		this.lblLimitEnabled = new JLabel(Messages.getString("panel.joint.limitsEnabled"), Icons.INFO, JLabel.LEFT);
		this.lblLimitEnabled.setToolTipText(Messages.getString("panel.joint.limitsEnabled.tooltip"));
		this.chkLimitEnabled = new JCheckBox();
		this.chkLimitEnabled.setSelected(limitEnabled);
		
		this.lblMinimum = new JLabel(Messages.getString("panel.joint.lowerLimit"), Icons.INFO, JLabel.LEFT);
		this.lblMinimum.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.lowerLimit.tooltip"), Messages.getString("unit.rotation")));
		this.txtMinimum = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.angle.lowerLimit.format")));
		this.txtMinimum.addFocusListener(new SelectTextFocusListener(this.txtMinimum));
		this.txtMinimum.setValue(Math.toDegrees(lower));
		
		this.lblMaximum = new JLabel(Messages.getString("panel.joint.upperLimit"), Icons.INFO, JLabel.LEFT);
		this.lblMaximum.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.upperLimit.tooltip"), Messages.getString("unit.rotation")));
		this.txtMaximum = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.angle.upperLimit.format")));
		this.txtMaximum.addFocusListener(new SelectTextFocusListener(this.txtMaximum));
		this.txtMaximum.setValue(Math.toDegrees(upper));
		
		this.lblReferenceAngle = new JLabel(Messages.getString("panel.joint.referenceAngle"), Icons.INFO, JLabel.LEFT);
		this.lblReferenceAngle.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.referenceAngle.tooltip"), Messages.getString("unit.rotation")));
		this.txtReferenceAngle = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.referenceAngle.format")));
		this.txtReferenceAngle.addFocusListener(new SelectTextFocusListener(this.txtReferenceAngle));
		this.txtReferenceAngle.setValue(Math.toDegrees(ref));
		
		this.tglReferenceAngle = new JToggleButton(Messages.getString("panel.joint.referenceAngle.autoCompute"));
		this.tglReferenceAngle.setToolTipText(Messages.getString("panel.joint.referenceAngle.autoCompute.tooltip"));
		this.tglReferenceAngle.setActionCommand("toggle-auto-compute");
		this.tglReferenceAngle.setSelected(true);
		
		this.btnResetReferenceAngle = new JButton(Messages.getString("panel.joint.referenceAngle.reset"));
		this.btnResetReferenceAngle.setToolTipText(Messages.getString("panel.joint.referenceAngle.reset.tooltip"));
		this.btnResetReferenceAngle.setActionCommand("reset-reference-angle");
		
		this.lblRatio = new JLabel(Messages.getString("panel.joint.angle.ratio"), Icons.INFO, JLabel.LEFT);
		this.lblRatio.setToolTipText(Messages.getString("panel.joint.angle.ratio.tooltip"));
		this.txtRatio = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.angle.ratio.format")));
		this.txtRatio.addFocusListener(new SelectTextFocusListener(this.txtRatio));
		this.txtRatio.setValue(ratio);
		
		if (edit) {
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.tglReferenceAngle.setVisible(false);
		} else {
			this.btnResetReferenceAngle.setVisible(false);
		}
		
		// add listeners after all the values have been set
		// this will preserve the initial values
		this.cmbBody1.addItemListener(this);
		this.cmbBody2.addItemListener(this);
		this.tglReferenceAngle.addActionListener(this);
		this.btnResetReferenceAngle.addActionListener(this);
		
		// setup the sections
		
		GroupLayout layout;
		
		// setup the general section
		
		JPanel pnlGeneral = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.section.general"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlGeneral.setBorder(border);
		
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblCollision)
						.addComponent(this.lblBody1)
						.addComponent(this.lblBody2)
						.addComponent(this.lblReferenceAngle))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtReferenceAngle)
								.addComponent(this.tglReferenceAngle)
								.addComponent(this.btnResetReferenceAngle))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblReferenceAngle)
						.addComponent(this.txtReferenceAngle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.tglReferenceAngle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnResetReferenceAngle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the limits section
		
		JPanel pnlLimits = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.joint.section.limits"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlLimits.setBorder(border);
		
		layout = new GroupLayout(pnlLimits);
		pnlLimits.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.lblMinimum)
						.addComponent(this.lblMaximum))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLimitEnabled)
						.addComponent(this.txtMinimum)
						.addComponent(this.txtMaximum)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.chkLimitEnabled))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMinimum)
						.addComponent(this.txtMinimum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMaximum)
						.addComponent(this.txtMaximum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the other section
		
		JPanel pnlOther = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.joint.section.other"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlOther.setBorder(border);
		
		layout = new GroupLayout(pnlOther);
		pnlOther.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(this.lblRatio)
				.addComponent(this.txtRatio));
		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(this.lblRatio)
				.addComponent(this.txtRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		// setup the overall layout
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlOther));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlOther));
	}
	
	/**
	 * Returns the computed reference angle between the two bodies.
	 * @return double
	 */
	private double computeReferenceAngle() {
		double r1 = ((SandboxBody)this.cmbBody1.getSelectedItem()).getTransform().getRotation();
		double r2 = ((SandboxBody)this.cmbBody2.getSelectedItem()).getTransform().getRotation();
		return r1 - r2;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("reset-reference-angle".equals(event.getActionCommand())) {
			this.txtReferenceAngle.setValue(Math.toDegrees(this.computeReferenceAngle()));
		} else if ("toggle-auto-compute".equals(event.getActionCommand())) {
			// if the state of the toggle button changes, check if its selected now, if so
			// then recompute the reference angle
			if (this.tglReferenceAngle.isSelected()) {
				this.txtReferenceAngle.setValue(Math.toDegrees(this.computeReferenceAngle()));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// when the items change in either drop down, check if the auto compute button is
		// selected, if so, then compute the reference angle
		if (this.tglReferenceAngle.isSelected()) {
			this.txtReferenceAngle.setValue(Math.toDegrees(this.computeReferenceAngle()));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof AngleJoint) {
			AngleJoint aj = (AngleJoint)joint;
			
			aj.setUserData(this.txtName.getText());
			aj.setCollisionAllowed(this.chkCollision.isSelected());
			
			aj.setLimitEnabled(this.chkLimitEnabled.isSelected());
			// get the min and max
			Number min = (Number)this.txtMinimum.getValue();
			Number max = (Number)this.txtMaximum.getValue();
			aj.setLimits(Math.toRadians(min.doubleValue()), Math.toRadians(max.doubleValue()));
			
			double ref = ControlUtilities.getDoubleValue(this.txtReferenceAngle);
			aj.setReferenceAngle(Math.toRadians(ref));
			
			double ratio = ControlUtilities.getDoubleValue(this.txtRatio);
			aj.setRatio(ratio);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getJoint()
	 */
	@Override
	public Joint getJoint() {
		// get the selected bodies
		SandboxBody body1 = (SandboxBody)this.cmbBody1.getSelectedItem();
		SandboxBody body2 = (SandboxBody)this.cmbBody2.getSelectedItem();
		// get the min and max
		Number min = (Number)this.txtMinimum.getValue();
		Number max = (Number)this.txtMaximum.getValue();
		// the angle joint
		AngleJoint aj = new AngleJoint(body1, body2);
		aj.setUserData(this.txtName.getText());
		aj.setCollisionAllowed(this.chkCollision.isSelected());
		
		aj.setLimitEnabled(this.chkLimitEnabled.isSelected());
		aj.setLimits(Math.toRadians(min.doubleValue()), Math.toRadians(max.doubleValue()));
		
		double ref = ControlUtilities.getDoubleValue(this.txtReferenceAngle);
		aj.setReferenceAngle(Math.toRadians(ref));
		
		double ratio = ControlUtilities.getDoubleValue(this.txtRatio);
		aj.setRatio(ratio);
		
		return aj;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// must have some name
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			return false;
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			return false;
		}
		// check the limit
		if (ControlUtilities.getDoubleValue(this.txtMinimum) > ControlUtilities.getDoubleValue(this.txtMaximum)) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		// must have some name
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.missingName"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.sameBody"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// check the limit
		if (ControlUtilities.getDoubleValue(this.txtMinimum) > ControlUtilities.getDoubleValue(this.txtMaximum)) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.invalidLimits"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
