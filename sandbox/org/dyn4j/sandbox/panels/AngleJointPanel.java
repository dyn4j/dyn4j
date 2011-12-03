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
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an angle joint.
 * @author William Bittle
 * @version 1.0.1
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
		
		this.lblBody1 = new JLabel(Resources.getString("panel.joint.body1"), Icons.INFO, JLabel.LEFT);
		this.lblBody2 = new JLabel(Resources.getString("panel.joint.body2"), Icons.INFO, JLabel.LEFT);
		this.lblBody1.setToolTipText(Resources.getString("panel.joint.body1.tooltip"));
		this.lblBody2.setToolTipText(Resources.getString("panel.joint.body2.tooltip"));
		
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		this.lblLimitEnabled = new JLabel(Resources.getString("panel.joint.limitsEnabled"), Icons.INFO, JLabel.LEFT);
		this.lblLimitEnabled.setToolTipText(Resources.getString("panel.joint.limitsEnabled.tooltip"));
		this.chkLimitEnabled = new JCheckBox();
		this.chkLimitEnabled.setSelected(limitEnabled);
		
		this.lblMinimum = new JLabel(Resources.getString("panel.joint.lowerLimit"), Icons.INFO, JLabel.LEFT);
		this.lblMinimum.setToolTipText(MessageFormat.format(Resources.getString("panel.joint.lowerLimit.tooltip"), Resources.getString("unit.rotation")));
		this.txtMinimum = new JFormattedTextField(new DecimalFormat(Resources.getString("panel.joint.angle.lowerLimit.format")));
		this.txtMinimum.addFocusListener(new SelectTextFocusListener(this.txtMinimum));
		this.txtMinimum.setValue(Math.toDegrees(lower));
		
		this.lblMaximum = new JLabel(Resources.getString("panel.joint.upperLimit"), Icons.INFO, JLabel.LEFT);
		this.lblMaximum.setToolTipText(MessageFormat.format(Resources.getString("panel.joint.upperLimit.tooltip"), Resources.getString("unit.rotation")));
		this.txtMaximum = new JFormattedTextField(new DecimalFormat(Resources.getString("panel.joint.angle.upperLimit.format")));
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
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Resources.getString("panel.section.general"));
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
						.addComponent(this.lblBody2))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)));
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
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the limits section
		
		JPanel pnlLimits = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Resources.getString("panel.joint.section.limits"));
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
			JOptionPane.showMessageDialog(owner, Resources.getString("panel.joint.missingName"), Resources.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else {
			// they can't be the same body
			if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
				JOptionPane.showMessageDialog(owner, Resources.getString("panel.joint.sameBody"), Resources.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
