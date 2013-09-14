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
package org.dyn4j.sandbox.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;
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
 * Dialog used to add a new joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class AddJointDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 2762180698167078099L;

	/** Map containing counters for the joint types */
	private static Map<Class<? extends Joint>, Integer> N = new HashMap<Class<? extends Joint>, Integer>();
	
	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The joint panel */
	private JointPanel pnlJoint;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param bodies the list of bodies
	 * @param clazz the joint class
	 */
	private AddJointDialog(Window owner, SandboxBody[] bodies, Class<? extends Joint> clazz) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		SandboxBody b1 = bodies[0];
		SandboxBody b2 = null;
		if (bodies.length > 1) {
			b2 = bodies[1];
		}
		
		JTextPane pneInfo = new JTextPane();
		pneInfo.setContentType("text/html");
		pneInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pneInfo.setPreferredSize(new Dimension(400, 200));
		pneInfo.setEditable(false);
		
		if (clazz == AngleJoint.class) {
			// create the joint
			AngleJoint aj = new AngleJoint(b1, b2);
			// set the name
			aj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.angle.name.default"), this.getCounter(clazz)));
			aj.setLimits(0);
			// create the panel
			this.pnlJoint = new AngleJointPanel(aj, bodies, false);
			// set the icon and title and description
			this.setIconImage(Icons.ADD_ANGLE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.angle.title"));
			pneInfo.setText(Messages.getString("panel.joint.angle.description"));
		} else if (clazz == DistanceJoint.class) {
			// create the joint
			DistanceJoint dj = new DistanceJoint(b1, b2, b1.getWorldCenter(), b2.getWorldCenter());
			// set the name
			dj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.distance.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new DistanceJointPanel(dj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_DISTANCE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.distance.title"));
			pneInfo.setText(Messages.getString("panel.joint.distance.description"));
		} else if (clazz == FrictionJoint.class) {
			// create the joint
			FrictionJoint fj = new FrictionJoint(b1, b2, b1.getWorldCenter());
			// set the name
			fj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.friction.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new FrictionJointPanel(fj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_FRICTION_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.friction.title"));
			pneInfo.setText(Messages.getString("panel.joint.friction.description"));
		} else if (clazz == MouseJoint.class) {
			// create the joint
			MouseJoint mj = new MouseJoint(b1, b1.getWorldCenter(), 8.0, 0.3, 100.0);
			// set the name
			mj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.mouse.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new MouseJointPanel(mj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_MOUSE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.mouse.title"));
			pneInfo.setText(Messages.getString("panel.joint.mouse.description"));
		} else if (clazz == MotorJoint.class) {
			// create the joint
			MotorJoint mj = new MotorJoint(b1, b2);
			// set the name
			mj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.motor.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new MotorJointPanel(mj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_MOTOR_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.motor.title"));
			pneInfo.setText(Messages.getString("panel.joint.motor.description"));
		} else if (clazz == PrismaticJoint.class) {
			// create the joint
			PrismaticJoint pj = new PrismaticJoint(b1, b2, b1.getWorldCenter(), new Vector2(1.0, 0.0));
			// set the name
			pj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.prismatic.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new PrismaticJointPanel(pj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_PRISMATIC_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.prismatic.title"));
			pneInfo.setText(Messages.getString("panel.joint.prismatic.description"));
		} else if (clazz == PulleyJoint.class) {
			// create the joint
			PulleyJoint pj = new PulleyJoint(b1, b2, new Vector2(), new Vector2(), b1.getWorldCenter(), b2.getWorldCenter());
			// set the name
			pj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.pulley.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new PulleyJointPanel(pj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_PULLEY_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.pulley.title"));
			pneInfo.setText(Messages.getString("panel.joint.pulley.description"));
		} else if (clazz == RevoluteJoint.class) {
			// create the joint
			RevoluteJoint rj = new RevoluteJoint(b1, b2, b1.getWorldCenter());
			// set the name
			rj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.revolute.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new RevoluteJointPanel(rj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_REVOLUTE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.revolute.title"));
			pneInfo.setText(Messages.getString("panel.joint.revolute.description"));
		} else if (clazz == RopeJoint.class) {
			// create the joint
			RopeJoint rj = new RopeJoint(b1, b2, b1.getWorldCenter(), b2.getWorldCenter());
			// set the name
			rj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.rope.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new RopeJointPanel(rj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_ROPE_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.rope.title"));
			pneInfo.setText(Messages.getString("panel.joint.rope.description"));
		} else if (clazz == WeldJoint.class) {
			// create the joint
			WeldJoint wj = new WeldJoint(b1, b2, b1.getWorldCenter());
			// set the name
			wj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.weld.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new WeldJointPanel(wj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_WELD_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.weld.title"));
			pneInfo.setText(Messages.getString("panel.joint.weld.description"));
		} else if (clazz == WheelJoint.class) {
			// create the joint
			WheelJoint wj = new WheelJoint(b1, b2, b1.getWorldCenter(), new Vector2(0.0, 1.0));
			// set the name
			wj.setUserData(MessageFormat.format(Messages.getString("dialog.joint.add.wheel.name.default"), this.getCounter(clazz)));
			// create the panel
			this.pnlJoint = new WheelJointPanel(wj, bodies, false);
			// set the icon and title
			this.setIconImage(Icons.ADD_WHEEL_JOINT.getImage());
			this.setTitle(Messages.getString("dialog.joint.add.wheel.title"));
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
		JButton btnAdd = new JButton(Messages.getString("button.add"));
		btnCancel.setActionCommand("cancel");
		btnAdd.setActionCommand("add");
		btnCancel.addActionListener(this);
		btnAdd.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnAdd);

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
	 * Returns the counter for default joint names for the given joint type.
	 * @param clazz the joint class
	 * @return int
	 */
	private int getCounter(Class<? extends Joint> clazz) {
		Integer n = N.get(clazz);
		if (n == null) {
			return 1;
		} else {
			return n.intValue();
		}
	}
	
	/**
	 * Increments the counter for default joint names for the given joint type.
	 * @param clazz the joint class
	 */
	private void incrementCounter(Class<? extends Joint> clazz) {
		Integer n = N.get(clazz);
		if (n == null) {
			N.put(clazz, 2);
		} else {
			N.put(clazz, n.intValue() + 1);
		}
	}
	
	/**
	 * Shows an add new joint dialog with the given joint panel and returns a new Joint
	 * object if the user entered valid input and clicked the add button.
	 * <p>
	 * Returns null if the dialog was canceled.
	 * @param owner the dialog owner
	 * @param bodies the list of bodies
	 * @param clazz the joint class type
	 * @return Joint
	 */
	public static Joint show(Window owner, SandboxBody[] bodies, Class<? extends Joint> clazz) {
		AddJointDialog dialog = new AddJointDialog(owner, bodies, clazz);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			dialog.incrementCounter(clazz);
			return dialog.pnlJoint.getJoint();
		} else {
			return null;
		}
	}
}
