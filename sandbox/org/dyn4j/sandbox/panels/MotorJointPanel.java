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
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to create or edit an motor joint.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public class MotorJointPanel extends JointPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;
	
	// linear target
	
	/** The anchor's x text field */
	private JFormattedTextField txtX;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY;
	
	// angular target
	
	/** The angular target */
	private JFormattedTextField txtA;
	
	// correction factor
	
	/** The correction factor */
	private JFormattedTextField txtCorrectionFactor;
	
	// max force and max torque
	
	/** The max force text field */
	private JFormattedTextField txtMaxForce;
	
	/** The max torque text field */
	private JFormattedTextField txtMaxTorque;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public MotorJointPanel(MotorJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 lt = joint.getLinearTarget();
		double at = joint.getAngularTarget();
		double cf = joint.getCorrectionFactor();
		double mf = joint.getMaximumForce();
		double mt = joint.getMaximumTorque();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		JLabel lblBody1 = new JLabel(Messages.getString("panel.joint.body1"), Icons.INFO, JLabel.LEFT);
		JLabel lblBody2 = new JLabel(Messages.getString("panel.joint.body2"), Icons.INFO, JLabel.LEFT);
		lblBody1.setToolTipText(Messages.getString("panel.joint.body1.tooltip"));
		lblBody2.setToolTipText(Messages.getString("panel.joint.body2.tooltip"));
		
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		JLabel lblLinearTarget = new JLabel(Messages.getString("panel.joint.motor.linearTarget"), Icons.INFO, JLabel.LEFT);
		lblLinearTarget.setToolTipText(Messages.getString("panel.joint.motor.linearTarget.tooltip"));
		JLabel lblAngularTarget = new JLabel(Messages.getString("panel.joint.motor.angularTarget"), Icons.INFO, JLabel.LEFT);
		lblAngularTarget.setToolTipText(Messages.getString("panel.joint.motor.angularTarget.tooltip"));
		
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		this.txtX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.motor.linearTarget.format")));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.setColumns(7);
		
		this.txtY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.motor.linearTarget.format")));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.setColumns(7);
		
		this.txtA = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.motor.angularTarget.format")));
		this.txtA.addFocusListener(new SelectTextFocusListener(this.txtA));
		this.txtA.setColumns(7);
		
		JLabel lblCorrectionFactor = new JLabel(Messages.getString("panel.joint.motor.correctionFactor"), Icons.INFO, JLabel.LEFT);
		lblCorrectionFactor.setToolTipText(Messages.getString("panel.joint.motor.correctionFactor.tooltip"));
		
		this.txtCorrectionFactor = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.motor.correctionFactor.format")));
		this.txtCorrectionFactor.addFocusListener(new SelectTextFocusListener(this.txtCorrectionFactor));
		this.txtCorrectionFactor.setColumns(7);
		
		JLabel lblMaxForce = new JLabel(Messages.getString("panel.joint.force.maximum"), Icons.INFO, JLabel.LEFT);
		lblMaxForce.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.force.maximum.tooltip"), Messages.getString("unit.force")));
		this.txtMaxForce = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.force.maximum.format")));
		this.txtMaxForce.addFocusListener(new SelectTextFocusListener(this.txtMaxForce));
		
		JLabel lblMaxTorque = new JLabel(Messages.getString("panel.joint.torque.maximum"), Icons.INFO, JLabel.LEFT);
		lblMaxTorque.setToolTipText(MessageFormat.format(Messages.getString("panel.joint.torque.maximum.tooltip"), Messages.getString("unit.torque")));
		this.txtMaxTorque = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.torque.maximum.format")));
		this.txtMaxTorque.addFocusListener(new SelectTextFocusListener(this.txtMaxTorque));
		
		// set defaults
		
		this.txtX.setValue(lt.x);
		this.txtY.setValue(lt.y);
		
		this.txtA.setValue(at);
		
		this.txtCorrectionFactor.setValue(cf);
		
		this.txtMaxForce.setValue(mf);
		this.txtMaxTorque.setValue(mt);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
		}
		
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
						.addComponent(lblBody1)
						.addComponent(lblBody2)
						.addComponent(lblLinearTarget)
						.addComponent(lblAngularTarget)
						.addComponent(lblCorrectionFactor))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(lblX)
								.addComponent(this.txtY)
								.addComponent(lblY))
						.addComponent(this.txtA)
						.addComponent(this.txtCorrectionFactor)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLinearTarget)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAngularTarget)
						.addComponent(this.txtA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblCorrectionFactor)
						.addComponent(this.txtCorrectionFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the maximums section
		
		JPanel pnlMaximums = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.joint.section.maximums"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlMaximums.setBorder(border);
		
		layout = new GroupLayout(pnlMaximums);
		pnlMaximums.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblMaxForce)
						.addComponent(lblMaxTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtMaxForce)
						.addComponent(this.txtMaxTorque)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxForce)
						.addComponent(this.txtMaxForce, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxTorque)
						.addComponent(this.txtMaxTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// create the overall layout
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlMaximums));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlMaximums));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof MotorJoint) {
			MotorJoint mj = (MotorJoint)joint;
			// set the super class properties
			mj.setUserData(this.txtName.getText());
			mj.setCollisionAllowed(this.chkCollision.isSelected());
			// set other properties
			mj.setLinearTarget(new Vector2(
					ControlUtilities.getDoubleValue(this.txtX),
					ControlUtilities.getDoubleValue(this.txtY)));
			mj.setAngularTarget(ControlUtilities.getDoubleValue(this.txtA));
			mj.setCorrectionFactor(ControlUtilities.getDoubleValue(this.txtCorrectionFactor));
			// set the properties that can change
			mj.setMaximumForce(ControlUtilities.getDoubleValue(this.txtMaxForce));
			mj.setMaximumTorque(ControlUtilities.getDoubleValue(this.txtMaxTorque));
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
		
		MotorJoint mj = new MotorJoint(body1, body2);
		// set the super class properties
		mj.setUserData(this.txtName.getText());
		mj.setCollisionAllowed(this.chkCollision.isSelected());
		// set other properties
		mj.setLinearTarget(new Vector2(
				ControlUtilities.getDoubleValue(this.txtX),
				ControlUtilities.getDoubleValue(this.txtY)));
		mj.setAngularTarget(ControlUtilities.getDoubleValue(this.txtA));
		mj.setCorrectionFactor(ControlUtilities.getDoubleValue(this.txtCorrectionFactor));
		// set the other properties
		mj.setMaximumForce(ControlUtilities.getDoubleValue(this.txtMaxForce));
		mj.setMaximumTorque(ControlUtilities.getDoubleValue(this.txtMaxTorque));
		
		return mj;
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
		// check the correction factor
		double cf = ControlUtilities.getDoubleValue(this.txtCorrectionFactor);
		if (cf < 0.0 || cf > 1.0) {
			return false;
		}
		// check the maxima
		if (ControlUtilities.getDoubleValue(this.txtMaxForce) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxTorque) < 0.0) {
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
		// check the correction factor
		double cf = ControlUtilities.getDoubleValue(this.txtCorrectionFactor);
		if (cf < 0.0 || cf > 1.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.motor.invalidCorrectionFactor"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		// check the maximia
		if (ControlUtilities.getDoubleValue(this.txtMaxForce) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.lessThanZeroMaximumForce"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxTorque) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.lessThanZeroMaximumTorque"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
