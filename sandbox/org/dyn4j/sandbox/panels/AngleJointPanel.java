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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an angle joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class AngleJointPanel extends JointPanel implements InputPanel {
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
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		this.lblLimitEnabled = new JLabel("Limit Enabled", Icons.INFO, JLabel.LEFT);
		this.lblLimitEnabled.setToolTipText("Check to enable the limits for this joint.");
		this.chkLimitEnabled = new JCheckBox();
		this.chkLimitEnabled.setSelected(limitEnabled);
		
		this.lblMinimum = new JLabel("Lower Limit", Icons.INFO, JLabel.LEFT);
		this.lblMinimum.setToolTipText("The lower limit in Degrees.");
		this.txtMinimum = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMinimum.addFocusListener(new SelectTextFocusListener(this.txtMinimum));
		this.txtMinimum.setValue(Math.toDegrees(lower));
		
		this.lblMaximum = new JLabel("Upper Limit", Icons.INFO, JLabel.LEFT);
		this.lblMaximum.setToolTipText("The upper limit in Degrees.");
		this.txtMaximum = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaximum.addFocusListener(new SelectTextFocusListener(this.txtMaximum));
		this.txtMaximum.setValue(Math.toDegrees(upper));
		
		if (edit) {
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
		}
		
		// setup the sections
		
		GroupLayout layout;
		
		// setup the general section
		
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " General "));
		
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblCollision)
						.addComponent(this.lblBody1)
						.addComponent(this.lblBody2))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the limits section
		
		JPanel pnlLimits = new JPanel();
		pnlLimits.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Limits "));
		
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
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.chkLimitEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMinimum)
						.addComponent(this.txtMinimum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaximum)
						.addComponent(this.txtMaximum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "An angle joint is used to limit the angle between two bodies.  " +
			   "This joint is typically used with other joints and not by itself.  " +
			   "If used by itself, its recomended that one of the joined bodies is static (has infinite mass).";
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
			
			aj.setLimits(min.doubleValue(), max.doubleValue());
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
		aj.setLimits(min.doubleValue(), max.doubleValue());
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
			JOptionPane.showMessageDialog(owner, "You must specify a name for the joint.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else {
			// they can't be the same body
			if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
				JOptionPane.showMessageDialog(owner, "You must select two different bodies.", "Notice", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
