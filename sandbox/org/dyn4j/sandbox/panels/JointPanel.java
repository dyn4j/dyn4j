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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or modify a joint.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public abstract class JointPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 2476535326313105580L;
	
	/** The name label */
	protected JLabel lblName;
	
	/** The name text field */
	protected JTextField txtName;
	
	/** The collision enabled label */
	protected JLabel lblCollision;
	
	/** The collision enabled checkbox */
	protected JCheckBox chkCollision;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates the name and collision enabled fields and labels.
	 */
	protected JointPanel() {
		this.lblName = new JLabel(Resources.getString("panel.joint.name"), Icons.INFO, JLabel.LEFT);
		this.lblName.setToolTipText(Resources.getString("panel.joint.name.tooltip"));
		this.txtName = new JTextField("");
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		
		this.lblCollision = new JLabel(Resources.getString("panel.joint.collisionEnabled"), Icons.INFO, JLabel.LEFT);
		this.lblCollision.setToolTipText(Resources.getString("panel.joint.collisionEnabled.tooltip"));
		this.chkCollision = new JCheckBox();
	}
	
	/**
	 * Sets the given joint's properties to the settings the user selected.
	 * @param joint the joint to modify
	 */
	public abstract void setJoint(Joint joint);
	
	/**
	 * Returns the currently configured joint.
	 * <p>
	 * If nothing has been set, the default joint is returned.
	 * @return Joint
	 */
	public abstract Joint getJoint();
}
