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
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to create or edit an pulley joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class PulleyJointPanel extends JointPanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;
	
	// anchor points
	
	/** The first body anchor's x text field */
	private JFormattedTextField txtBX1;
	
	/** The second body anchor's x text field */
	private JFormattedTextField txtBX2;
	
	/** The first body anchor's y text field */
	private JFormattedTextField txtBY1;
	
	/** The second body anchor's y text field */
	private JFormattedTextField txtBY2;

	/** The first pulley anchor's x text field */
	private JFormattedTextField txtPX1;
	
	/** The second pulley anchor's x text field */
	private JFormattedTextField txtPX2;
	
	/** The first pulley anchor's y text field */
	private JFormattedTextField txtPY1;
	
	/** The second pulley anchor's y text field */
	private JFormattedTextField txtPY2;
	
	/** The button to set body anchor 1 to body1's center of mass */
	private JButton btnUseCenter1;
	
	/** The button to set body anchor 2 to body2's center of mass */
	private JButton btnUseCenter2;
	
	// ratio
	
	/** The frequency text field */
	private JFormattedTextField txtRatio;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public PulleyJointPanel(PulleyJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 a1 = joint.getAnchor1();
		Vector2 a2 = joint.getAnchor2();
		Vector2 p1 = joint.getPulleyAnchor1();
		Vector2 p2 = joint.getPulleyAnchor2();
		double r = joint.getRatio();
		
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
		
		JLabel lblPulleyAnchor1 = new JLabel(Messages.getString("panel.joint.pulley.anchor.pulley1"), Icons.INFO, JLabel.LEFT);
		lblPulleyAnchor1.setToolTipText(Messages.getString("panel.joint.pulley.anchor.pulley1.tooltip"));
		JLabel lblPulleyAnchor2 = new JLabel(Messages.getString("panel.joint.pulley.anchor.pulley2"), Icons.INFO, JLabel.LEFT);
		lblPulleyAnchor2.setToolTipText(Messages.getString("panel.joint.pulley.anchor.pulley2.tooltip"));
		
		JLabel lblBodyAnchor1 = new JLabel(Messages.getString("panel.joint.pulley.anchor.body1"), Icons.INFO, JLabel.LEFT);
		lblBodyAnchor1.setToolTipText(Messages.getString("panel.joint.anchor1.tooltip"));
		JLabel lblBodyAnchor2 = new JLabel(Messages.getString("panel.joint.pulley.anchor.body2"), Icons.INFO, JLabel.LEFT);
		lblBodyAnchor2.setToolTipText(Messages.getString("panel.joint.anchor2.tooltip"));
		
		JLabel lblPX1 = new JLabel(Messages.getString("x"));
		JLabel lblPX2 = new JLabel(Messages.getString("x"));
		JLabel lblPY1 = new JLabel(Messages.getString("y"));
		JLabel lblPY2 = new JLabel(Messages.getString("y"));
		JLabel lblBX1 = new JLabel(Messages.getString("x"));
		JLabel lblBX2 = new JLabel(Messages.getString("x"));
		JLabel lblBY1 = new JLabel(Messages.getString("y"));
		JLabel lblBY2 = new JLabel(Messages.getString("y"));
		
		this.txtPX1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.pulley.format")));
		this.txtPX1.addFocusListener(new SelectTextFocusListener(this.txtPX1));
		this.txtPX1.setColumns(7);
		
		this.txtPX2 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.pulley.format")));
		this.txtPX2.addFocusListener(new SelectTextFocusListener(this.txtPX2));
		this.txtPX2.setColumns(7);
		
		this.txtPY1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.pulley.format")));
		this.txtPY1.addFocusListener(new SelectTextFocusListener(this.txtPY1));
		this.txtPY1.setColumns(7);
		
		this.txtPY2 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.pulley.format")));
		this.txtPY2.addFocusListener(new SelectTextFocusListener(this.txtPY2));
		this.txtPY2.setColumns(7);
		
		this.txtBX1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.body.format")));
		this.txtBX1.addFocusListener(new SelectTextFocusListener(this.txtBX1));
		this.txtBX1.setColumns(7);
		
		this.txtBX2 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.body.format")));
		this.txtBX2.addFocusListener(new SelectTextFocusListener(this.txtBX2));
		this.txtBX2.setColumns(7);
		
		this.txtBY1 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.body.format")));
		this.txtBY1.addFocusListener(new SelectTextFocusListener(this.txtBY1));
		this.txtBY1.setColumns(7);
		
		this.txtBY2 = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.anchor.body.format")));
		this.txtBY2.addFocusListener(new SelectTextFocusListener(this.txtBY2));
		this.txtBY2.setColumns(7);

		this.btnUseCenter1 = new JButton(Messages.getString("panel.joint.useCenter"));
		this.btnUseCenter1.setToolTipText(Messages.getString("panel.joint.useCenter.tooltip1"));
		this.btnUseCenter1.setActionCommand("use-com1");
		this.btnUseCenter1.addActionListener(this);
		
		this.btnUseCenter2 = new JButton(Messages.getString("panel.joint.useCenter"));
		this.btnUseCenter2.setToolTipText(Messages.getString("panel.joint.useCenter.tooltip2"));
		this.btnUseCenter2.setActionCommand("use-com2");
		this.btnUseCenter2.addActionListener(this);
		
		JLabel lblRatio = new JLabel(Messages.getString("panel.joint.pulley.ratio"), Icons.INFO, JLabel.LEFT);
		lblRatio.setToolTipText(Messages.getString("panel.joint.pulley.ratio.tooltip"));
		this.txtRatio = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.joint.pulley.ratio.format")));
		this.txtRatio.addFocusListener(new SelectTextFocusListener(this.txtRatio));
		
		// set defaults
		
		// disable the auto calculate while we set defaults
		this.txtBX1.setValue(a1.x);
		this.txtBX2.setValue(a2.x);
		this.txtBY1.setValue(a1.y);
		this.txtBY2.setValue(a2.y);
		this.txtPX1.setValue(p1.x);
		this.txtPX2.setValue(p2.x);
		this.txtPY1.setValue(p1.y);
		this.txtPY2.setValue(p2.y);
		
		this.txtRatio.setValue(r);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtBX1.setEnabled(false);
			this.txtBX2.setEnabled(false);
			this.txtBY1.setEnabled(false);
			this.txtBY2.setEnabled(false);
			this.txtPX1.setEnabled(false);
			this.txtPX2.setEnabled(false);
			this.txtPY1.setEnabled(false);
			this.txtPY2.setEnabled(false);
			this.btnUseCenter1.setEnabled(false);
			this.btnUseCenter2.setEnabled(false);
		}
		
		// setup the sections
		
		// setup the general section
		
		JPanel pnlGeneral = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.section.general"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlGeneral.setBorder(border);
		
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblCollision)
						.addComponent(lblBody1)
						.addComponent(lblBody2)
						.addComponent(lblBodyAnchor1)
						.addComponent(lblBodyAnchor2)
						.addComponent(lblPulleyAnchor1)
						.addComponent(lblPulleyAnchor2))
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
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtBX1)
										.addComponent(this.txtBX2)
										.addComponent(this.txtPX1)
										.addComponent(this.txtPX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(lblBX1)
										.addComponent(lblBX2)
										.addComponent(lblPX1)
										.addComponent(lblPX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtBY1)
										.addComponent(this.txtBY2)
										.addComponent(this.txtPY1)
										.addComponent(this.txtPY2))
								.addGroup(layout.createParallelGroup()
										.addComponent(lblBY1)
										.addComponent(lblBY2)
										.addComponent(lblPY1)
										.addComponent(lblPY2)))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBodyAnchor1)
						.addComponent(this.txtBX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBX1)
						.addComponent(this.txtBY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBY1))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBodyAnchor2)
						.addComponent(this.txtBX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBX2)
						.addComponent(this.txtBY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBY2))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPulleyAnchor1)
						.addComponent(this.txtPX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPX1)
						.addComponent(this.txtPY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPY1))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPulleyAnchor2)
						.addComponent(this.txtPX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPX2)
						.addComponent(this.txtPY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPY2)));
		
		// setup the block and tackle section
		
		JPanel pnlBlockAndTackle = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.joint.pulley.section.blockAndTackle"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlBlockAndTackle.setBorder(border);
		
		layout = new GroupLayout(pnlBlockAndTackle);
		pnlBlockAndTackle.setLayout(layout);
				
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblRatio)
				.addComponent(this.txtRatio));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRatio)
						.addComponent(this.txtRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the overall layout
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlBlockAndTackle));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlBlockAndTackle));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("use-com1".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody1.getSelectedItem()).getWorldCenter();
			this.txtBX1.setValue(c.x);
			this.txtBY1.setValue(c.y);
		} else if ("use-com2".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody2.getSelectedItem()).getWorldCenter();
			this.txtBX2.setValue(c.x);
			this.txtBY2.setValue(c.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof PulleyJoint) {
			PulleyJoint pj = (PulleyJoint)joint;
			// set the super class properties
			pj.setUserData(this.txtName.getText());
			pj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			pj.setRatio(ControlUtilities.getDoubleValue(this.txtRatio));
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
		Vector2 a1 = new Vector2(
				ControlUtilities.getDoubleValue(this.txtBX1),
				ControlUtilities.getDoubleValue(this.txtBY1));
		Vector2 a2 = new Vector2(
				ControlUtilities.getDoubleValue(this.txtBX2),
				ControlUtilities.getDoubleValue(this.txtBY2));
		Vector2 p1 = new Vector2(
				ControlUtilities.getDoubleValue(this.txtPX1),
				ControlUtilities.getDoubleValue(this.txtPY1));
		Vector2 p2 = new Vector2(
				ControlUtilities.getDoubleValue(this.txtPX2),
				ControlUtilities.getDoubleValue(this.txtPY2));
		
		PulleyJoint pj = new PulleyJoint(body1, body2, p1, p2, a1, a2);
		// set the super class properties
		pj.setUserData(this.txtName.getText());
		pj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		pj.setRatio(ControlUtilities.getDoubleValue(this.txtRatio));
		
		return pj;
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
		if (ControlUtilities.getDoubleValue(this.txtRatio) <= 0.0) {
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
		if (ControlUtilities.getDoubleValue(this.txtRatio) <= 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.joint.pulley.invalidRatio"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
