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

import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an friction joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class FrictionJointPanel extends JointPanel implements InputPanel {
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
	
	// anchor point
	
	/** The anchor point label */
	private JLabel lblAnchor;
	
	/** The x label for the anchor point */
	private JLabel lblX;
	
	/** The y label for the anchor point */
	private JLabel lblY;
	
	/** The anchor's x text field */
	private JFormattedTextField txtX;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY;
	
	// max force and max torque
	
	/** The max force label */
	private JLabel lblMaxForce;
	
	/** The max torque label */
	private JLabel lblMaxTorque;
	
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
	public FrictionJointPanel(FrictionJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 a1 = joint.getAnchor1();
		double mf = joint.getMaximumForce();
		double mt = joint.getMaximumTorque();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		this.lblBody1 = new JLabel("Body 1", Icons.INFO, JLabel.LEFT);
		this.lblBody2 = new JLabel("Body 2", Icons.INFO, JLabel.LEFT);
		this.lblBody1.setToolTipText("The first body participating in the joint.");
		this.lblBody2.setToolTipText("The second body participating in the joint.");
		
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		this.lblAnchor = new JLabel("Anchor", Icons.INFO, JLabel.LEFT);
		this.lblAnchor.setToolTipText("The anchor point for the bodies.");
		
		this.lblX = new JLabel("x");
		this.lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.setColumns(7);
		
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.setColumns(7);
		
		this.lblMaxForce = new JLabel("Maximum Force", Icons.INFO, JLabel.LEFT);
		this.lblMaxForce.setToolTipText("The maximum force the joint can apply in Newtons.");
		this.txtMaxForce = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxForce.addFocusListener(new SelectTextFocusListener(this.txtMaxForce));
		
		this.lblMaxTorque = new JLabel("Maximum Torque", Icons.INFO, JLabel.LEFT);
		this.lblMaxTorque.setToolTipText("The maximum torque the joint can apply in Newton-Meters.");
		this.txtMaxTorque = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxTorque.addFocusListener(new SelectTextFocusListener(this.txtMaxTorque));
		
		// set defaults
		
		this.txtX.setValue(a1.x);
		this.txtY.setValue(a1.y);
		
		this.txtMaxForce.setValue(mf);
		this.txtMaxTorque.setValue(mt);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtX.setEnabled(false);
			this.txtY.setEnabled(false);
		}
		
		// setup the sections
		
		GroupLayout layout;
		
		// setup the general section
		
		JPanel pnlGeneral = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " General ");
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
						.addComponent(this.lblAnchor))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(this.lblX)
								.addComponent(this.txtY)
								.addComponent(this.lblY))));
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
						.addComponent(this.lblAnchor)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY)));
		
		// setup the maximums section
		
		JPanel pnlMaximums = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Maximums ");
		border.setTitlePosition(TitledBorder.TOP);
		pnlMaximums.setBorder(border);
		
		layout = new GroupLayout(pnlMaximums);
		pnlMaximums.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaxForce)
						.addComponent(this.lblMaxTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtMaxForce)
						.addComponent(this.txtMaxTorque)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMaxForce)
						.addComponent(this.txtMaxForce, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMaxTorque)
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
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A friction joint is used to drive linear and angular speeds between the bodies to zero.  " +
			   "The joint is intented to be used with other joints.  " +
			   "The maximum force and torque values make sure that the joint doesn't over or under compensate.  ";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof FrictionJoint) {
			FrictionJoint fj = (FrictionJoint)joint;
			// set the super class properties
			fj.setUserData(this.txtName.getText());
			fj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			fj.setMaximumForce(ControlUtilities.getDoubleValue(this.txtMaxForce));
			fj.setMaximumTorque(ControlUtilities.getDoubleValue(this.txtMaxTorque));
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
				ControlUtilities.getDoubleValue(this.txtX),
				ControlUtilities.getDoubleValue(this.txtY));
		
		FrictionJoint fj = new FrictionJoint(body1, body2, a);
		// set the super class properties
		fj.setUserData(this.txtName.getText());
		fj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		fj.setMaximumForce(ControlUtilities.getDoubleValue(this.txtMaxForce));
		fj.setMaximumTorque(ControlUtilities.getDoubleValue(this.txtMaxTorque));
		
		return fj;
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
		// check the maximia
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
			JOptionPane.showMessageDialog(owner, "You must specify a name for the joint.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			JOptionPane.showMessageDialog(owner, "You must select two different bodies.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// check the maximia
		if (ControlUtilities.getDoubleValue(this.txtMaxForce) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum force must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxTorque) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum torque must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
