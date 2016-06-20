/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.panels.AngleJointPanel;
import org.dyn4j.sandbox.panels.DistanceJointPanel;
import org.dyn4j.sandbox.panels.FrictionJointPanel;
import org.dyn4j.sandbox.panels.JointPanel;
import org.dyn4j.sandbox.panels.MotorJointPanel;
import org.dyn4j.sandbox.panels.MouseJointPanel;
import org.dyn4j.sandbox.panels.PrismaticJointPanel;
import org.dyn4j.sandbox.panels.PulleyJointPanel;
import org.dyn4j.sandbox.panels.RevoluteJointPanel;
import org.dyn4j.sandbox.panels.RopeJointPanel;
import org.dyn4j.sandbox.panels.WeldJointPanel;
import org.dyn4j.sandbox.panels.WheelJointPanel;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Dialog used to create a joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class EditJointDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 2762180698167078099L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The joint panel */
	private JointPanel pnlJoint;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param joint the joint
	 */
	private EditJointDialog(Window owner, Joint joint) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		SandboxBody[] bodies = new SandboxBody[]{b1, b2};
		

		JTextPane pneInfo = new JTextPane();
		pneInfo.setContentType("text/html");
		pneInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pneInfo.setPreferredSize(new Dimension(400, 200));
		pneInfo.setEditable(false);
		
		if (joint instanceof AngleJoint) {
			this.pnlJoint = new AngleJointPanel((AngleJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_ANGLE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.angle.title"));
			pneInfo.setText(Messages.getString("panel.joint.angle.description"));
		} else if (joint instanceof DistanceJoint) {
			this.pnlJoint = new DistanceJointPanel((DistanceJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_DISTANCE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.distance.title"));
			pneInfo.setText(Messages.getString("panel.joint.distance.description"));
		} else if (joint instanceof FrictionJoint) {
			this.pnlJoint = new FrictionJointPanel((FrictionJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_FRICTION_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.friction.title"));
			pneInfo.setText(Messages.getString("panel.joint.friction.description"));
		} else if (joint instanceof PinJoint) {
			this.pnlJoint = new MouseJointPanel((PinJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_MOUSE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.mouse.title"));
			pneInfo.setText(Messages.getString("panel.joint.mouse.description"));
		} else if (joint instanceof MotorJoint) {
			this.pnlJoint = new MotorJointPanel((MotorJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_MOTOR_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.motor.title"));
			pneInfo.setText(Messages.getString("panel.joint.motor.description"));
		} else if (joint instanceof PrismaticJoint) {
			this.pnlJoint = new PrismaticJointPanel((PrismaticJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_PRISMATIC_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.prismatic.title"));
			pneInfo.setText(Messages.getString("panel.joint.prismatic.description"));
		} else if (joint instanceof PulleyJoint) {
			this.pnlJoint = new PulleyJointPanel((PulleyJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_PULLEY_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.pulley.title"));
			pneInfo.setText(Messages.getString("panel.joint.pulley.description"));
		} else if (joint instanceof RevoluteJoint) {
			this.pnlJoint = new RevoluteJointPanel((RevoluteJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_REVOLUTE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.revolute.title"));
			pneInfo.setText(Messages.getString("panel.joint.revolute.description"));
		} else if (joint instanceof RopeJoint) {
			this.pnlJoint = new RopeJointPanel((RopeJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_ROPE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.rope.title"));
			pneInfo.setText(Messages.getString("panel.joint.rope.description"));
		} else if (joint instanceof WeldJoint) {
			this.pnlJoint = new WeldJointPanel((WeldJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_WELD_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.weld.title"));
			pneInfo.setText(Messages.getString("panel.joint.weld.description"));
		} else if (joint instanceof WheelJoint) {
			this.pnlJoint = new WheelJointPanel((WheelJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_WHEEL_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.edit.wheel.title"));
			pneInfo.setText(Messages.getString("panel.joint.wheel.description"));
		}
		
		JPanel pnlInfo = new JPanel();
		pnlInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlInfo.setLayout(new BorderLayout(5, 5));
		pnlInfo.add(pneInfo);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabbedPane.addTab(Messages.getString("dialog.joint.tab.joint"), this.pnlJoint);
		tabbedPane.addTab(Messages.getString("dialog.joint.tab.information"), pnlInfo);
		
		JButton btnCancel = new JButton(Messages.getString("button.cancel"));
		JButton btnSave = new JButton(Messages.getString("button.save"));
		btnCancel.setActionCommand("cancel");
		btnSave.setActionCommand("save");
		btnCancel.addActionListener(this);
		btnSave.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnSave);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(tabbedPane, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
		
		this.pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// check the action command
		if ("cancel".equals(event.getActionCommand())) {
			// if its canceled then set the canceled flag and
			// close the dialog
			this.setVisible(false);
			this.canceled = true;
		} else {
			// check the joint input
			if (this.pnlJoint.isValidInput()) {
				// if its valid then close the dialog
				this.canceled = false;
				this.setVisible(false);
			} else {
				this.pnlJoint.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows a create joint dialog with the given joint panel and returns a new Joint
	 * object if the user entered valid input and clicked the create button.
	 * <p>
	 * Returns null if the dialog was canceled.
	 * @param owner the dialog owner
	 * @param joint the joint to edit
	 */
	public static void show(Window owner, Joint joint) {
		EditJointDialog dialog = new EditJointDialog(owner, joint);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			// set the properties
			dialog.pnlJoint.setJoint(joint);
		}
	}
}
