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

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to create or edit an revolute joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class RevoluteJointPanel extends JointPanel implements InputPanel, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body 1 drop down label */
	private JLabel lblBody1;
	
	/** The body 2 drop down label */
	private JLabel lblBody2;
	
	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;

	// reference angle
	
	/** The reference angle label */
	private JLabel lblReferenceAngle;
	
	/** The reference angle text field */
	private JFormattedTextField txtReferenceAngle;

	/** The reference angle auto compute button */
	private JToggleButton tglReferenceAngle;
	
	/** The button used to reset the reference angle (only used in edit mode) */
	private JButton btnResetReferenceAngle;
	
	// anchor points
	
	/** The anchor label */
	private JLabel lblAnchor;
	
	/** The x label for the anchor point */
	private JLabel lblX1;
	
	/** The y label for the anchor point */
	private JLabel lblY1;
	
	/** The anchor's x text field */
	private JFormattedTextField txtX1;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY1;
	
	/** The button to set anchor1 to body1's center of mass */
	private JButton btnUseCenter1;
	
	/** The button to set anchor2 to body2's center of mass */
	private JButton btnUseCenter2;
	
	// limits

	/** The limit enabled label */
	private JLabel lblLimitEnabled;
	
	/** The limit enable check box */
	private JCheckBox chkLimitEnabled;
	
	/** The upper limit label */
	private JLabel lblUpperLimit;
	
	/** The lower limit label */
	private JLabel lblLowerLimit;
	
	/** The upper limit text field */
	private JFormattedTextField txtUpperLimit;
	
	/** The lower limit text field */
	private JFormattedTextField txtLowerLimit;
	
	// motor

	/** The motor enabled label */
	private JLabel lblMotorEnabled;
	
	/** The motor enabled check box */
	private JCheckBox chkMotorEnabled;
	
	/** The motor speed label */
	private JLabel lblMotorSpeed;
	
	/** The motor speed text field */
	private JFormattedTextField txtMotorSpeed;
	
	/** The max motor force label */
	private JLabel lblMaxMotorTorque;
	
	/** The max motor force text field */
	private JFormattedTextField txtMaxMotorTorque;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public RevoluteJointPanel(RevoluteJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 an = joint.getAnchor1();
		boolean limit = joint.isLimitEnabled();
		boolean motor = joint.isMotorEnabled();
		double ul = joint.getUpperLimit();
		double ll = joint.getLowerLimit();
		double ms = joint.getMotorSpeed();
		double mt = joint.getMaximumMotorTorque();
		double ref = joint.getReferenceAngle();
		
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
		
		this.lblAnchor = new JLabel(Messages.getString("panel.joint.anchor"), Icons.INFO, JLabel.LEFT);
		this.lblAnchor.setToolTipText(Messages.getString("panel.joint.revolute.anchor.tooltip"));
		
		this.lblX1 = new JLabel(Messages.getString("x"));
		this.lblY1 = new JLabel(Messages.getString("y"));
		
		this.txtX1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.anchor.format")));
		this.txtX1.addFocusListener(new SelectTextFocusListener(this.txtX1));
		this.txtX1.setColumns(7);
		
		this.txtY1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.anchor.format")));
		this.txtY1.addFocusListener(new SelectTextFocusListener(this.txtY1));
		this.txtY1.setColumns(7);
		
		this.btnUseCenter1 = new JButton(Messages.getString("panel.joint.useCenter"));
		this.btnUseCenter1.setToolTipText(Messages.getString("panel.joint.useCenter.tooltip"));
		this.btnUseCenter1.setActionCommand("use-com1");
		this.btnUseCenter1.addActionListener(this);
		
		this.btnUseCenter2 = new JButton(Messages.getString("panel.joint.useCenter"));
		this.btnUseCenter2.setToolTipText(Messages.getString("panel.joint.useCenter.tooltip"));
		this.btnUseCenter2.setActionCommand("use-com2");
		this.btnUseCenter2.addActionListener(this);

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
		
		this.lblLimitEnabled = new JLabel(Messages.getString("panel.joint.limitsEnabled"), Icons.INFO, JLabel.LEFT);
		this.lblLimitEnabled.setToolTipText(Messages.getString("panel.joint.limitsEnabled.tooltip"));
		this.chkLimitEnabled = new JCheckBox();
		
		this.lblUpperLimit = new JLabel(Messages.getString("panel.joint.upperLimit"), Icons.INFO, JLabel.LEFT);
		this.lblUpperLimit.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.upperLimit.tooltip"), Messages.getString("unit.rotation")));
		this.txtUpperLimit = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.revolute.upperLimit.format")));
		this.txtUpperLimit.addFocusListener(new SelectTextFocusListener(this.txtUpperLimit));
		this.txtUpperLimit.setColumns(8);
		
		this.lblLowerLimit = new JLabel(Messages.getString("panel.joint.lowerLimit"), Icons.INFO, JLabel.LEFT);
		this.lblLowerLimit.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.lowerLimit.tooltip"), Messages.getString("unit.rotation")));
		this.txtLowerLimit = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.revolute.lowerLimit.format")));
		this.txtLowerLimit.addFocusListener(new SelectTextFocusListener(this.txtLowerLimit));
		this.txtLowerLimit.setColumns(8);
		
		this.lblMotorEnabled = new JLabel(Messages.getString("panel.joint.motorEnabled"), Icons.INFO, JLabel.LEFT);
		this.lblMotorEnabled.setToolTipText(Messages.getString("panel.joint.revolute.motorEnabled.tooltip"));
		this.chkMotorEnabled = new JCheckBox();
		
		this.lblMotorSpeed = new JLabel(Messages.getString("panel.joint.motorSpeed"), Icons.INFO, JLabel.LEFT);
		this.lblMotorSpeed.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.motorSpeed.tooltip"), Messages.getString("unit.velocity.angular")));
		this.txtMotorSpeed = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.revolute.motorSpeed.format")));
		this.txtMotorSpeed.addFocusListener(new SelectTextFocusListener(this.txtMotorSpeed));
		
		this.lblMaxMotorTorque = new JLabel(Messages.getString("panel.joint.motorMaximumTorque"), Icons.INFO, JLabel.LEFT);
		this.lblMaxMotorTorque.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.motorMaximumTorque.tooltip"), Messages.getString("unit.torque")));
		this.txtMaxMotorTorque = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.revolute.motorMaximumTorque.format")));
		this.txtMaxMotorTorque.addFocusListener(new SelectTextFocusListener(this.txtMaxMotorTorque));
		
		// set defaults

		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		this.txtX1.setValue(an.x);
		this.txtY1.setValue(an.y);
		
		this.chkLimitEnabled.setSelected(limit);
		this.txtUpperLimit.setValue(Math.toDegrees(ul));
		this.txtLowerLimit.setValue(Math.toDegrees(ll));
		
		this.chkMotorEnabled.setSelected(motor);
		this.txtMaxMotorTorque.setValue(mt);
		this.txtMotorSpeed.setValue(Math.toDegrees(ms));
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtX1.setEnabled(false);
			this.txtY1.setEnabled(false);
			this.btnUseCenter1.setEnabled(false);
			this.btnUseCenter2.setEnabled(false);
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
						.addComponent(this.lblAnchor)
						.addComponent(this.lblReferenceAngle))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbBody1)
								.addComponent(this.btnUseCenter1))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbBody2)
								.addComponent(this.btnUseCenter2))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX1)
								.addComponent(this.lblX1)
								.addComponent(this.txtY1)
								.addComponent(this.lblY1))
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
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblAnchor)
						.addComponent(this.txtX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX1)
						.addComponent(this.txtY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY1))
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
						.addComponent(this.lblLowerLimit)
						.addComponent(this.lblUpperLimit))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLimitEnabled)
						.addComponent(this.txtLowerLimit)
						.addComponent(this.txtUpperLimit)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.chkLimitEnabled))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLowerLimit)
						.addComponent(this.txtLowerLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblUpperLimit)
						.addComponent(this.txtUpperLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the motor section
		
		JPanel pnlMotor = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.joint.section.motor"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlMotor.setBorder(border);
		
		layout = new GroupLayout(pnlMotor);
		pnlMotor.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMotorEnabled)
						.addComponent(this.lblMotorSpeed)
						.addComponent(this.lblMaxMotorTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkMotorEnabled)
						.addComponent(this.txtMotorSpeed)
						.addComponent(this.txtMaxMotorTorque)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMotorEnabled)
						.addComponent(this.chkMotorEnabled))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMotorSpeed)
						.addComponent(this.txtMotorSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMaxMotorTorque)
						.addComponent(this.txtMaxMotorTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the layout of the sections
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlMotor));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlMotor));
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
	public void actionPerformed(ActionEvent e) {
		if ("use-com1".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody1.getSelectedItem()).getWorldCenter();
			this.txtX1.setValue(c.x);
			this.txtY1.setValue(c.y);
		} else if ("use-com2".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody2.getSelectedItem()).getWorldCenter();
			this.txtX1.setValue(c.x);
			this.txtY1.setValue(c.y);
		} else if ("reset-reference-angle".equals(e.getActionCommand())) {
			this.txtReferenceAngle.setValue(Math.toDegrees(this.computeReferenceAngle()));
		} else if ("toggle-auto-compute".equals(e.getActionCommand())) {
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
		if (joint instanceof RevoluteJoint) {
			RevoluteJoint rj = (RevoluteJoint)joint;
			// set the super class properties
			rj.setUserData(this.txtName.getText());
			rj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			rj.setLimitEnabled(this.chkLimitEnabled.isSelected());
			rj.setLimits(
					Math.toRadians(ControlUtilities.getDoubleValue(this.txtLowerLimit)), 
					Math.toRadians(ControlUtilities.getDoubleValue(this.txtUpperLimit)));
			rj.setMaximumMotorTorque(ControlUtilities.getDoubleValue(this.txtMaxMotorTorque));
			rj.setMotorEnabled(this.chkMotorEnabled.isSelected());
			rj.setMotorSpeed(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMotorSpeed)));
			rj.setReferenceAngle(Math.toRadians(ControlUtilities.getDoubleValue(this.txtReferenceAngle)));
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
		
		// get the anchor points
		Vector2 a = new Vector2(
				ControlUtilities.getDoubleValue(this.txtX1),
				ControlUtilities.getDoubleValue(this.txtY1));
		
		RevoluteJoint rj = new RevoluteJoint(body1, body2, a);
		// set the super class properties
		rj.setUserData(this.txtName.getText());
		rj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		rj.setLimitEnabled(this.chkLimitEnabled.isSelected());
		rj.setLimits(
				Math.toRadians(ControlUtilities.getDoubleValue(this.txtLowerLimit)),
				Math.toRadians(ControlUtilities.getDoubleValue(this.txtUpperLimit)));
		rj.setMaximumMotorTorque(ControlUtilities.getDoubleValue(this.txtMaxMotorTorque));
		rj.setMotorEnabled(this.chkMotorEnabled.isSelected());
		rj.setMotorSpeed(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMotorSpeed)));
		rj.setReferenceAngle(Math.toRadians(ControlUtilities.getDoubleValue(this.txtReferenceAngle)));
		
		return rj;
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
		if (ControlUtilities.getDoubleValue(this.txtLowerLimit) > ControlUtilities.getDoubleValue(this.txtUpperLimit)) {
			return false;
		}
		// check the maximum motor torque
		if (ControlUtilities.getDoubleValue(this.txtMaxMotorTorque) < 0.0) {
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
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.missingName"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.sameBody"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// check the limit
		if (ControlUtilities.getDoubleValue(this.txtLowerLimit) > ControlUtilities.getDoubleValue(this.txtUpperLimit)) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.invalidLimits"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// check the maximum motor force
		if (ControlUtilities.getDoubleValue(this.txtMaxMotorTorque) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.invalidMaximumMotorTorque"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
