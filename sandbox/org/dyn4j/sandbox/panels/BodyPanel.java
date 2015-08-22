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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.JSliderWithTextField;
import org.dyn4j.sandbox.dialogs.ColorDialog;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ColorUtilities;

/**
 * Panel for editing a Body.
 * @author William Bittle
 * @version 1.0.6
 * @since 1.0.0
 */
public class BodyPanel extends JPanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4580826229518550082L;
	
	/** The body being edited */
	private SandboxBody body;
	
	// name
	
	/** The name label */
	private JLabel lblName;
	
	/** The name text field */
	private JTextField txtName;
	
	// color controls
	
	/** The outline color */
	private JLabel lblOutlineColor;
	
	/** The outline color change button */
	private JButton btnOutlineColor;
	
	/** The fill color label */
	private JLabel lblFillColor;
	
	/** The fill color change button */
	private JButton btnFillColor;
	
	/** The sample text for the color panel */
	private JLabel lblSample;
	
	/** A panel to show the selected colors */
	private JPanel pnlColor;
	
	// mass controls
	
	/** The local center label */
	private JLabel lblCenter;
	
	/** The mass label */
	private JLabel lblMass;
	
	/** The inertia label */
	private JLabel lblInertia;
	
	/** The local center of mass x text field */
	private JFormattedTextField txtX;

	/** The local center of mass y text field */
	private JFormattedTextField txtY;
	
	/** The mass text box */
	private JFormattedTextField txtMass;
	
	/** The inertia text box */
	private JFormattedTextField txtInertia;
	
	/** The label for the explicit mass check box */
	private JLabel lblMassExplicit;
	
	/** The check box to manually set the mass */
	private JCheckBox chkMassExplicit;
	
	// mass type controls
	
	/** The mass type label */
	private JLabel lblMassType;
	
	/** The mass type combo box */
	private JComboBox cmbMassType;
	
	// damping controls
	
	/** The linear damping label */
	private JLabel lblLinearDamping;
	
	/** The linear damping slider and text field */
	private JSliderWithTextField sldLinearDamping;
	
	/** The angular damping label */
	private JLabel lblAngularDamping;
	
	/** The angular damping slider and text field */
	private JSliderWithTextField sldAngularDamping;
	
	// velocity controls
	
	/** The velocity label */
	private JLabel lblVelocity;
	
	/** The velocity x value text box */
	private JFormattedTextField txtVelocityX;
	
	/** The velocity y value text box */
	private JFormattedTextField txtVelocityY;
	
	/** The angular velocity label */
	private JLabel lblAngularVelocity;
	
	/** The angular velocity text field */
	private JFormattedTextField txtAngularVelocity;
	
	// force and torque controls
	
	/** The force label */
	private JLabel lblForce;
	
	/** The force x text box */
	private JFormattedTextField txtForceX;
	
	/** The force y text box */
	private JFormattedTextField txtForceY;
	
	/** The torque label */
	private JLabel lblTorque;
	
	/** The torque text box */
	private JFormattedTextField txtTorque;
	
	// gravity scale controls
	
	/** The gravity scale label */
	private JLabel lblGravityScale;
	
	/** The gravity scale slider and text field */
	private JSliderWithTextField sldGravityScale;
	
	// state controls
	
	/** The state label */
	private JLabel lblState;
	
	/** The inactive checkbox */
	private JCheckBox chkInactive;
	
	/** The asleep checkbox */
	private JCheckBox chkAsleep;
	
	// other property controls
	
	/** The allow auto sleep label */
	private JLabel lblAllowAutoSleep;
	
	/** The allow auto sleep checkbox */
	private JCheckBox chkAllowAutoSleep;
	
	/** The is bullet label */
	private JLabel lblBullet;
	
	/** The is bullet checkbox */
	private JCheckBox chkBullet;
	
	/**
	 * Full constructor.
	 * @param body the body to edit
	 */
	public BodyPanel(SandboxBody body) {
		this.body = body;
		this.initialize();
	}
	
	/**
	 * Sets up the panel with all the controls.
	 */
	public void initialize() {
		// get the color values
		Color initialOutlineColor = ColorUtilities.convertColor(body.getOutlineColor());
		Color initialFillColor = ColorUtilities.convertColor(body.getFillColor());
		
		// get the other properties
		Mass mass = body.getMass();
		MassType massType = mass.getType();
		double linearDamping = body.getLinearDamping();
		double angularDamping = body.getAngularDamping();
		Vector2 velocity = body.getLinearVelocity().copy();
		double angularVelocity = body.getAngularVelocity();
		double gravityScale = body.getGravityScale();
		boolean inactive = !body.isActive();
		boolean asleep = body.isAsleep();
		boolean autoSleep = body.isAutoSleepingEnabled();
		boolean bullet = body.isBullet();
		String name = body.getName();
		Vector2 force = body.getAccumulatedForce();
		double torque = body.getAccumulatedTorque();
		
		// name
		this.lblName = new JLabel(Messages.getString("panel.body.name"), Icons.INFO, JLabel.LEFT);
		this.lblName.setToolTipText(Messages.getString("panel.body.name.tooltip"));
		this.txtName = new JTextField(name);
		
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		this.txtName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent event) {
				body.setName(txtName.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent event) {
				body.setName(txtName.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent event) {}
		});
		
		// the color panel
		this.lblSample = new JLabel(Messages.getString("panel.body.color.sample"));
		this.lblSample.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(initialFillColor));
		this.lblSample.setHorizontalAlignment(JLabel.CENTER);
		this.pnlColor = new JPanel();
		this.pnlColor.setBackground(initialFillColor);
		this.pnlColor.setBorder(BorderFactory.createLineBorder(initialOutlineColor, 4));
		this.pnlColor.setPreferredSize(new Dimension(150, 50));
		this.pnlColor.setLayout(new BorderLayout());
		this.pnlColor.add(this.lblSample, BorderLayout.CENTER);
		
		// outline color
		this.lblOutlineColor = new JLabel(Messages.getString("panel.body.color.outline"), Icons.INFO, JLabel.LEFT);
		this.lblOutlineColor.setToolTipText(Messages.getString("panel.body.color.outline.tooltip"));
		this.btnOutlineColor = new JButton(Messages.getString("button.select"));
		this.btnOutlineColor.setActionCommand("outlineColor");
		this.btnOutlineColor.addActionListener(this);
		
		// fill color
		this.lblFillColor = new JLabel(Messages.getString("panel.body.color.fill"), Icons.INFO, JLabel.LEFT);
		this.lblFillColor.setToolTipText(Messages.getString("panel.body.color.fill.tooltip"));
		this.btnFillColor = new JButton(Messages.getString("button.select"));
		this.btnFillColor.setActionCommand("fillColor");
		this.btnFillColor.addActionListener(this);
		
		// mass type
		this.lblMassType = new JLabel(Messages.getString("panel.body.mass.type"), Icons.INFO, JLabel.LEFT);
		this.lblMassType.setToolTipText(Messages.getString("panel.body.mass.type.tooltip"));
		this.cmbMassType = new JComboBox(ITEMS);
		this.cmbMassType.setSelectedItem(this.getMassTypeItem(massType));
		this.cmbMassType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MassTypeItem type = (MassTypeItem)cmbMassType.getSelectedItem();
				body.setMassType(type.type);
			}
		});
		
		// mass
		Mass showMass = mass;
		if (mass.getType() != MassType.NORMAL) {
			showMass = new Mass(mass);
			showMass.setType(MassType.NORMAL);
		}
		
		this.lblMassExplicit = new JLabel(Messages.getString("panel.body.mass.manual"), Icons.INFO, JLabel.LEFT);
		this.lblMassExplicit.setToolTipText(Messages.getString("panel.body.mass.manual.tooltip"));
		this.chkMassExplicit = new JCheckBox();
		this.chkMassExplicit.setSelected(body.isMassExplicit());
		this.chkMassExplicit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkMassExplicit.isSelected()) {
					body.setMassExplicit(true);
					// enable the controls
					txtX.setEditable(true);
					txtY.setEditable(true);
					txtMass.setEditable(true);
					txtInertia.setEditable(true);
				} else {
					body.setMassExplicit(false);
					// recompute the mass and set the current mass type
					body.updateMass();
					// set the new values after computing the mass
					Mass m = body.getMass();
					txtX.setValue(m.getCenter().x);
					txtY.setValue(m.getCenter().y);
					txtMass.setValue(m.getMass());
					txtInertia.setValue(m.getInertia());
					// disable the controls
					txtX.setEditable(false);
					txtY.setEditable(false);
					txtMass.setEditable(false);
					txtInertia.setEditable(false);
				}
			}
		});
		
		this.lblCenter = new JLabel(Messages.getString("panel.body.mass.center"), Icons.INFO, JLabel.LEFT);
		this.lblCenter.setToolTipText(Messages.getString("panel.body.mass.center.tooltip"));
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		this.txtX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.mass.center.format")));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.setValue(showMass.getCenter().x);
		this.txtX.setEditable(body.isMassExplicit());
		this.txtX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chkMassExplicit.isSelected()) {
					Number number = (Number)txtX.getValue();
					Mass cm = body.getMass();
					// create a new mass using the new x value
					Mass nm = new Mass(new Vector2(number.doubleValue(), cm.getCenter().y), cm.getMass(), cm.getInertia());
					nm.setType(((MassTypeItem)cmbMassType.getSelectedItem()).type);
					body.setMass(nm);
				}
			}
		});
		
		this.txtY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.mass.center.format")));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.setValue(showMass.getCenter().y);
		this.txtY.setEditable(body.isMassExplicit());
		this.txtY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chkMassExplicit.isSelected()) {
					Number number = (Number)txtY.getValue();
					Mass cm = body.getMass();
					// create a new mass using the new y value
					Mass nm = new Mass(new Vector2(cm.getCenter().x, number.doubleValue()), cm.getMass(), cm.getInertia());
					nm.setType(((MassTypeItem)cmbMassType.getSelectedItem()).type);
					body.setMass(nm);
				}
			}
		});
		
		this.lblMass = new JLabel(Messages.getString("panel.body.mass"), Icons.INFO, JLabel.LEFT);
		this.lblMass.setToolTipText(MessageFormat.format(Messages.getString("panel.body.mass.tooltip"), Messages.getString("unit.mass")));
		this.txtMass = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.mass.format")));
		this.txtMass.addFocusListener(new SelectTextFocusListener(this.txtMass));
		this.txtMass.setValue(showMass.getMass());
		this.txtMass.setEditable(body.isMassExplicit());
		this.txtMass.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chkMassExplicit.isSelected()) {
					Number number = (Number)txtMass.getValue();
					Mass cm = body.getMass();
					// create a new mass using the new mass value
					Mass nm = new Mass(cm.getCenter(), number.doubleValue(), cm.getInertia());
					nm.setType(((MassTypeItem)cmbMassType.getSelectedItem()).type);
					body.setMass(nm);
				}
			}
		});
		
		this.lblInertia = new JLabel(Messages.getString("panel.body.inertia"), Icons.INFO, JLabel.LEFT);
		this.lblInertia.setToolTipText(MessageFormat.format(Messages.getString("panel.body.inertia.tooltip"), Messages.getString("unit.inertia")));
		this.txtInertia = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.inertia.format")));
		this.txtInertia.addFocusListener(new SelectTextFocusListener(this.txtInertia));
		this.txtInertia.setValue(showMass.getInertia());
		this.txtInertia.setEditable(body.isMassExplicit());
		this.txtInertia.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chkMassExplicit.isSelected()) {
					Number number = (Number)txtInertia.getValue();
					Mass cm = body.getMass();
					// create a new mass using the new inertia value
					Mass nm = new Mass(cm.getCenter(), cm.getMass(), number.doubleValue());
					nm.setType(((MassTypeItem)cmbMassType.getSelectedItem()).type);
					body.setMass(nm);
				}
			}
		});
		
		// linear damping
		this.lblLinearDamping = new JLabel(Messages.getString("panel.body.damping.linear"), Icons.INFO, JLabel.LEFT);
		this.lblLinearDamping.setToolTipText(Messages.getString("panel.body.damping.linear.tooltip"));
		this.sldLinearDamping = new JSliderWithTextField(0, 100, (int)(linearDamping * 100.0), 0.01, new DecimalFormat(Messages.getString("panel.body.damping.linear.format")));
		this.sldLinearDamping.setColumns(4);
		this.sldLinearDamping.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setLinearDamping(value);
			}
		});
		
		// angular damping
		this.lblAngularDamping = new JLabel(Messages.getString("panel.body.damping.angular"), Icons.INFO, JLabel.LEFT);
		this.lblAngularDamping.setToolTipText(Messages.getString("panel.body.damping.angular.tooltip"));
		this.sldAngularDamping = new JSliderWithTextField(0, 100, (int)(angularDamping * 100.0), 0.01, new DecimalFormat(Messages.getString("panel.body.damping.angular.format")));
		this.sldAngularDamping.setColumns(4);
		this.sldAngularDamping.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setAngularDamping(value);
			}
		});
		
		// initial velocity
		this.lblVelocity = new JLabel(Messages.getString("panel.body.velocity.linear"), Icons.INFO, JLabel.LEFT);
		this.lblVelocity.setToolTipText(MessageFormat.format(Messages.getString("panel.body.velocity.linear.tooltip"), Messages.getString("unit.velocity.linear")));
		JLabel lblVelocityX = new JLabel(Messages.getString("x"));
		JLabel lblVelocityY = new JLabel(Messages.getString("y"));
		this.txtVelocityX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.velocity.linear.format")));
		this.txtVelocityY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.velocity.linear.format")));
		this.txtVelocityX.setValue(velocity.x);
		this.txtVelocityY.setValue(velocity.y);
		this.txtVelocityX.setColumns(7);
		this.txtVelocityY.setColumns(7);
		this.txtVelocityX.setMaximumSize(this.txtVelocityX.getPreferredSize());
		this.txtVelocityY.setMaximumSize(this.txtVelocityY.getPreferredSize());
		this.txtVelocityX.addFocusListener(new SelectTextFocusListener(this.txtVelocityX));
		this.txtVelocityY.addFocusListener(new SelectTextFocusListener(this.txtVelocityY));
		
		this.txtVelocityX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.getLinearVelocity().x = number.doubleValue();
			}
		});
		this.txtVelocityY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.getLinearVelocity().y = number.doubleValue();
			}
		});
		
		// initial anuglar velocity
		this.lblAngularVelocity = new JLabel(Messages.getString("panel.body.velocity.angular"), Icons.INFO, JLabel.LEFT);
		this.lblAngularVelocity.setToolTipText(MessageFormat.format(Messages.getString("panel.body.velocity.angular.tooltip"), Messages.getString("unit.velocity.angular")));
		this.txtAngularVelocity = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.velocity.angular.format")));
		this.txtAngularVelocity.setValue(Math.toDegrees(angularVelocity));
		this.txtAngularVelocity.setColumns(7);
		this.txtAngularVelocity.setMaximumSize(this.txtAngularVelocity.getPreferredSize());
		this.txtAngularVelocity.addFocusListener(new SelectTextFocusListener(this.txtAngularVelocity));
		this.txtAngularVelocity.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.setAngularVelocity(Math.toRadians(number.doubleValue()));
			}
		});
		
		// force and torque
		JLabel lblForceX = new JLabel(Messages.getString("x"));
		JLabel lblForceY = new JLabel(Messages.getString("x"));
		this.lblForce = new JLabel(Messages.getString("panel.body.force"), Icons.INFO, JLabel.LEFT);
		this.lblForce.setToolTipText(Messages.getString("panel.body.force.tooltip"));
		
		this.txtForceX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.force.format")));
		this.txtForceX.setValue(force.x);
		this.txtForceX.setColumns(7);
		this.txtForceX.setMaximumSize(this.txtForceX.getPreferredSize());
		this.txtForceX.setEditable(false);
		
		this.txtForceY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.force.format")));
		this.txtForceY.setValue(force.y);
		this.txtForceY.setColumns(7);
		this.txtForceY.setMaximumSize(this.txtForceY.getPreferredSize());
		this.txtForceY.setEditable(false);
		
		this.lblTorque = new JLabel(Messages.getString("panel.body.torque"), Icons.INFO, JLabel.LEFT);
		this.lblTorque.setToolTipText(Messages.getString("panel.body.torque.tooltip"));
		
		this.txtTorque = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.body.torque.format")));
		this.txtTorque.setValue(torque);
		this.txtTorque.setColumns(7);
		this.txtTorque.setMaximumSize(this.txtTorque.getPreferredSize());
		this.txtTorque.setEditable(false);
		
		// gravity scale
		this.lblGravityScale = new JLabel(Messages.getString("panel.body.gravityScale"), Icons.INFO, JLabel.LEFT);
		this.lblGravityScale.setToolTipText(Messages.getString("panel.body.gravityScale.tooltip"));
		this.sldGravityScale = new JSliderWithTextField(0, 1000, (int)(gravityScale * 100.0), 0.01, new DecimalFormat(Messages.getString("panel.body.gravityScale.format")));
		this.sldGravityScale.setColumns(4);
		this.sldGravityScale.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setGravityScale(value);
			}
		});
		
		// initial state (Active/asleep)
		this.lblState = new JLabel(Messages.getString("panel.body.state"), Icons.INFO, JLabel.LEFT);
		this.lblState.setToolTipText(Messages.getString("panel.body.state.tooltip"));
		this.chkInactive = new JCheckBox(Messages.getString("panel.body.state.inactive"));
		this.chkAsleep = new JCheckBox(Messages.getString("panel.body.state.asleep"));
		this.chkInactive.setSelected(inactive);
		this.chkAsleep.setSelected(asleep);
		
		this.chkInactive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setActive(false);
				} else {
					body.setActive(true);
				}
			}
		});
		
		this.chkAsleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setAsleep(true);
				} else {
					body.setAsleep(false);
				}
			}
		});
		
		// allow auto sleep
		this.lblAllowAutoSleep = new JLabel(Messages.getString("panel.body.autoSleeping"), Icons.INFO, JLabel.LEFT);
		this.lblAllowAutoSleep.setToolTipText(Messages.getString("panel.body.autoSleeping.tooltip"));
		this.chkAllowAutoSleep = new JCheckBox();
		this.chkAllowAutoSleep.setSelected(autoSleep);
		
		this.chkAllowAutoSleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setAutoSleepingEnabled(true);
				} else {
					body.setAutoSleepingEnabled(false);
				}
			}
		});
		
		// is bullet
		this.lblBullet = new JLabel(Messages.getString("panel.body.bullet"), Icons.INFO, JLabel.LEFT);
		this.lblBullet.setToolTipText(Messages.getString("panel.body.bullet.tooltip"));
		this.chkBullet = new JCheckBox();
		this.chkBullet.setSelected(bullet);
		
		this.chkBullet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setBullet(true);
				} else {
					body.setBullet(false);
				}
			}
		});
		
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
						.addComponent(this.lblOutlineColor)
						.addComponent(this.lblFillColor))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.btnOutlineColor)
										.addComponent(this.btnFillColor))
								.addComponent(this.pnlColor))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(this.lblOutlineColor)
										.addComponent(this.btnOutlineColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(this.lblFillColor)
										.addComponent(this.btnFillColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(this.pnlColor)));
		
		// setup the mass section
		JPanel pnlMass = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.body.section.mass"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlMass.setBorder(border);
		
		layout = new GroupLayout(pnlMass);
		pnlMass.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMassType)
						.addComponent(this.lblMassExplicit)
						.addComponent(this.lblCenter)
						.addComponent(this.lblMass)
						.addComponent(this.lblInertia))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbMassType)
						.addComponent(this.chkMassExplicit)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(lblX)
								.addComponent(this.txtY)
								.addComponent(lblY))
						.addComponent(this.txtMass)
						.addComponent(this.txtInertia)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMassType)
						.addComponent(this.cmbMassType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMassExplicit)
						.addComponent(this.chkMassExplicit))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblCenter)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblMass)
						.addComponent(this.txtMass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblInertia)
						.addComponent(this.txtInertia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the state section
		JPanel pnlProperties = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.section.properties"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlProperties.setBorder(border);
		
		layout = new GroupLayout(pnlProperties);
		pnlProperties.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblVelocity)
						.addComponent(this.lblAngularVelocity)
						.addComponent(this.lblForce)
						.addComponent(this.lblTorque)
						.addComponent(this.lblLinearDamping)
						.addComponent(this.lblAngularDamping)
						.addComponent(this.lblGravityScale)
						.addComponent(this.lblState))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtVelocityX)
								.addComponent(lblVelocityX)
								.addComponent(this.txtVelocityY)
								.addComponent(lblVelocityY))
						.addComponent(this.txtAngularVelocity)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtForceX)
								.addComponent(lblForceX)
								.addComponent(this.txtForceY)
								.addComponent(lblForceY))
						.addComponent(this.txtTorque)
						.addComponent(this.sldLinearDamping)
						.addComponent(this.sldAngularDamping)
						.addComponent(this.sldGravityScale)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.chkInactive)
								.addComponent(this.chkAsleep))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblVelocity)
						.addComponent(this.txtVelocityX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblVelocityX)
						.addComponent(this.txtVelocityY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblVelocityY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblAngularVelocity)
						.addComponent(this.txtAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblForce)
						.addComponent(this.txtForceX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblForceX)
						.addComponent(this.txtForceY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblForceY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblTorque)
						.addComponent(this.txtTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLinearDamping)
						.addComponent(this.sldLinearDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblAngularDamping)
						.addComponent(this.sldAngularDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblGravityScale)
						.addComponent(this.sldGravityScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblState)
						.addComponent(this.chkInactive, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkAsleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the flags section
		JPanel pnlFlags = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.section.flags"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlFlags.setBorder(border);
		
		layout = new GroupLayout(pnlFlags);
		pnlFlags.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAllowAutoSleep)
						.addComponent(this.lblBullet))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkAllowAutoSleep)
						.addComponent(this.chkBullet)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblAllowAutoSleep)
						.addComponent(this.chkAllowAutoSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBullet)
						.addComponent(this.chkBullet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the sections
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlMass)
				.addComponent(pnlProperties)
				.addComponent(pnlFlags, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlMass)
				.addComponent(pnlProperties)
				.addComponent(pnlFlags));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		// check the commands
		if (command.equalsIgnoreCase("outlineColor") || command.equalsIgnoreCase("fillColor")) {
			// get the current color
			Color c;
			if (command.equalsIgnoreCase("outlineColor")) {
				c = ColorUtilities.convertColor(this.body.getOutlineColor());
			} else {
				c = ColorUtilities.convertColor(this.body.getFillColor());
			}
			
			// show the color selection dialog
			Color nc = ColorDialog.show(this, c, false);
			// make sure it wasnt canceled
			if (nc != null) {
				// get the color components
				float[] color = ColorUtilities.convertColor(nc);
				// dont copy alpha values
				Color dc = new Color(nc.getRed(), nc.getGreen(), nc.getBlue());
				// check for the action command
				if (command.equalsIgnoreCase("outlineColor")) {
					this.body.setOutlineColor(color);
					// set the outline color of the color panel
					this.pnlColor.setBorder(BorderFactory.createLineBorder(dc, 4));
				} else {
					this.body.setFillColor(color);
					// set the fill color of the color panel
					this.pnlColor.setBackground(dc);
					// set the foreground color of the label
					this.lblSample.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(dc));
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {}
	
	/** The normal mass type option */
	private static final MassTypeItem NORMAL = new MassTypeItem(Messages.getString("mass.type.normal"), MassType.NORMAL);
	
	/** The infinite mass type option */
	private static final MassTypeItem INFINITE = new MassTypeItem(Messages.getString("mass.type.infinite"), MassType.INFINITE);
	
	/** The fixed linear velocity option */
	private static final MassTypeItem FIXED_LINEAR_VELOCITY = new MassTypeItem(Messages.getString("mass.type.fixedLinearVelocity"), MassType.FIXED_LINEAR_VELOCITY);
	
	/** The fixed angular velocity option */
	private static final MassTypeItem FIXED_ANGULAR_VELOCITY = new MassTypeItem(Messages.getString("mass.type.fixedAngularVelocity"), MassType.FIXED_ANGULAR_VELOCITY);
	
	/** The list of mass type items for the mass type drop down */
	private static final MassTypeItem[] ITEMS = new MassTypeItem[] {
		NORMAL,
		INFINITE,
		FIXED_LINEAR_VELOCITY,
		FIXED_ANGULAR_VELOCITY
	};
	
	/**
	 * Returns the mass type item for the given mass type.
	 * @param type the mass type
	 * @return MassTypeItem null if the mass type is not found
	 */
	private MassTypeItem getMassTypeItem(MassType type) {
		for (MassTypeItem item : ITEMS) {
			if (item.type == type) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * List item class to store a name for a MassType.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static final class MassTypeItem {
		/** The display name */
		public String name;
		
		/** The mass type */
		public MassType type;
		
		/**
		 * Full constructor.
		 * @param name the display name of the mass type
		 * @param type the mass type
		 */
		public MassTypeItem(String name, MassType type) {
			this.name = name;
			this.type = type;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name;
		}
	}
}
