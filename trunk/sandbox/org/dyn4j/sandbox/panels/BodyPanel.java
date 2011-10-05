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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.JSliderWithTextField;
import org.dyn4j.sandbox.dialogs.ColorDialog;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ColorUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel for editing a Body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BodyPanel extends WindowSpawningPanel implements InputPanel {
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
	 * @param parent the parent window, frame or dialog 
	 * @param body the body to edit
	 */
	public BodyPanel(Window parent, SandboxBody body) {
		super(parent);
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
		Mass.Type massType = mass.getType();
		double linearDamping = body.getLinearDamping();
		double angularDamping = body.getAngularDamping();
		Vector2 velocity = body.getVelocity().copy();
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
		this.lblName = new JLabel("Name", Icons.INFO, JLabel.LEFT);
		this.lblName.setToolTipText("The name of the body.");
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
		this.lblSample = new JLabel("Sample");
		this.lblSample.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(initialFillColor));
		this.lblSample.setHorizontalAlignment(JLabel.CENTER);
		this.pnlColor = new JPanel();
		this.pnlColor.setBackground(initialFillColor);
		this.pnlColor.setBorder(BorderFactory.createLineBorder(initialOutlineColor, 4));
		this.pnlColor.setPreferredSize(new Dimension(150, 50));
		this.pnlColor.setLayout(new BorderLayout());
		this.pnlColor.add(this.lblSample, BorderLayout.CENTER);
		
		// outline color
		this.lblOutlineColor = new JLabel("Outline Color", Icons.INFO, JLabel.LEFT);
		this.lblOutlineColor.setToolTipText("The color used when drawing an outline of this body.");
		this.btnOutlineColor = new JButton("Select");
		this.btnOutlineColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color c = ColorUtilities.convertColor(body.getOutlineColor());
				Color nc = ColorDialog.show(getParentWindow(), c, false);
				if (nc != null) {
					// set the outline color
					float[] color = ColorUtilities.convertColor(nc);
					body.setOutlineColor(color);
					// set the outline color of the color panel
					// dont copy alpha values
					Color dc = new Color(nc.getRed(), nc.getGreen(), nc.getBlue());
					pnlColor.setBorder(BorderFactory.createLineBorder(dc, 4));
					// set the foreground color of the label
					lblSample.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(dc));
				}
			}
		});
		
		// fill color
		this.lblFillColor = new JLabel("Fill Color", Icons.INFO, JLabel.LEFT);
		this.lblFillColor.setToolTipText("The color used when filling this body.");
		this.btnFillColor = new JButton("Select");
		this.btnFillColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color c = ColorUtilities.convertColor(body.getFillColor());
				Color nc = ColorDialog.show(getParentWindow(), c, false);
				if (nc != null) {
					// set the body fill color
					float[] color = ColorUtilities.convertColor(nc);
					body.setFillColor(color);
					// set the fill color of the color panel
					// dont copy alpha values
					Color dc = new Color(nc.getRed(), nc.getGreen(), nc.getBlue());
					pnlColor.setBackground(dc);
					// set the foreground color of the label
					lblSample.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(dc));
				}
			}
		});
		
		// mass type
		this.lblMassType = new JLabel("Mass Type", Icons.INFO, JLabel.LEFT);
		this.lblMassType.setToolTipText(
				"<html>Normal: The body's linear and angular velocity will be affected by interactions with other bodies.<br />" +
				"Infinite: The body's linear and angular velocity are not affected by interactions.<br />" +
				"Fixed Linear Velocity: The body's linear velocity remains unaffected.<br />" +
				"Fixed Angular Velocity: The body's angular velocity remains unaffected.</html>");
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
		if (mass.getType() != Mass.Type.NORMAL) {
			showMass = new Mass(mass);
			showMass.setType(Mass.Type.NORMAL);
		}
		
		this.lblMassExplicit = new JLabel("Manual", Icons.INFO, JLabel.LEFT);
		this.lblMassExplicit.setToolTipText("Allows the mass to be set instead of calculated from the fixtures.");
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
					// recompute the mass
					body.setMass(body.getMass().getType());
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
		
		this.lblCenter = new JLabel("Local Center", Icons.INFO, JLabel.LEFT);
		this.lblCenter.setToolTipText("<html>The <b>local</b> center of mass of the body.</html>");
		JLabel lblX = new JLabel("x");
		JLabel lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
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
		
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
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
		
		this.lblMass = new JLabel("Mass", Icons.INFO, JLabel.LEFT);
		this.lblMass.setToolTipText("<html>The total mass of the body in Kilograms/Meter<sup>2</sup>.<br />" +
				"Specifies the body's resistance to change in its velocity.<br />" +
				"The mass is computed automatically from the fixtures attached to this body.</html>");
		this.txtMass = new JFormattedTextField(new DecimalFormat("0.000"));
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
		
		this.lblInertia = new JLabel("Inertia", Icons.INFO, JLabel.LEFT);
		this.lblInertia.setToolTipText("<html>The inertia tensor of the body in Kilogram-Meters<sup>2</sup>.<br />" +
				"Specifies the body's resistance to change in its angular velocity.<br />" +
				"The inertia is computed automatically from the fixtures attached to this body.</html>");
		this.txtInertia = new JFormattedTextField(new DecimalFormat("0.000"));
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
		this.lblLinearDamping = new JLabel("Linear Damping", Icons.INFO, JLabel.LEFT);
		this.lblLinearDamping.setToolTipText("<html>Specifies a drag like coefficient for linear motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldLinearDamping = new JSliderWithTextField(0, 100, (int)(linearDamping * 100.0), 0.01, new DecimalFormat("0.00"));
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
		this.lblAngularDamping = new JLabel("Angular Damping", Icons.INFO, JLabel.LEFT);
		this.lblAngularDamping.setToolTipText("<html>Specifies a drag like coefficient for angular motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldAngularDamping = new JSliderWithTextField(0, 100, (int)(angularDamping * 100.0), 0.01, new DecimalFormat("0.00"));
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
		this.lblVelocity = new JLabel("Velocity", Icons.INFO, JLabel.LEFT);
		this.lblVelocity.setToolTipText("The linear velocity in Meters/Second.");
		JLabel lblVelocityX = new JLabel("x");
		JLabel lblVelocityY = new JLabel("y");
		this.txtVelocityX = new JFormattedTextField(new DecimalFormat("##0.000"));
		this.txtVelocityY = new JFormattedTextField(new DecimalFormat("##0.000"));
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
				body.getVelocity().x = number.doubleValue();
			}
		});
		this.txtVelocityY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.getVelocity().y = number.doubleValue();
			}
		});
		
		// initial anuglar velocity
		this.lblAngularVelocity = new JLabel("Angular Velocity", Icons.INFO, JLabel.LEFT);
		this.lblAngularVelocity.setToolTipText("The angular velocity in Degrees/Second.");
		this.txtAngularVelocity = new JFormattedTextField(new DecimalFormat("##0.000"));
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
		JLabel lblForceX = new JLabel("x");
		JLabel lblForceY = new JLabel("y");
		this.lblForce = new JLabel("Accumulated Force", Icons.INFO, JLabel.LEFT);
		this.lblForce.setToolTipText("<html>The total accumulated force.</html>");
		
		this.txtForceX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtForceX.setValue(force.x);
		this.txtForceX.setColumns(7);
		this.txtForceX.setMaximumSize(this.txtForceX.getPreferredSize());
		this.txtForceX.setEditable(false);
		
		this.txtForceY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtForceY.setValue(force.y);
		this.txtForceY.setColumns(7);
		this.txtForceY.setMaximumSize(this.txtForceY.getPreferredSize());
		this.txtForceY.setEditable(false);
		
		this.lblTorque = new JLabel("Accumulated Torque", Icons.INFO, JLabel.LEFT);
		this.lblTorque.setToolTipText("<html>The total accumulated torque.</html>");
		
		this.txtTorque = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtTorque.setValue(torque);
		this.txtTorque.setColumns(7);
		this.txtTorque.setMaximumSize(this.txtTorque.getPreferredSize());
		this.txtTorque.setEditable(false);
		
		// gravity scale
		this.lblGravityScale = new JLabel("Gravity Scale", Icons.INFO, JLabel.LEFT);
		this.lblGravityScale.setToolTipText("<html>A scalar to apply less or more gravity to a specific body.<br />Valid values are zero or greater.</html>");
		this.sldGravityScale = new JSliderWithTextField(0, 1000, (int)(gravityScale * 100.0), 0.01, new DecimalFormat("#0.00"));
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
		this.lblState = new JLabel("State", Icons.INFO, JLabel.LEFT);
		this.lblState.setToolTipText(
				"<html>The current state of the body.<br />" +
				"Inactive: The body does not participate in the world.<br />" +
				"Asleep: The body has come to rest and only participates when awoken by another body or joint.</html>");
		this.chkInactive = new JCheckBox("Inactive");
		this.chkAsleep = new JCheckBox("Asleep");
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
		this.lblAllowAutoSleep = new JLabel("Allow Auto-Sleeping", Icons.INFO, JLabel.LEFT);
		this.lblAllowAutoSleep.setToolTipText(
				"<html>Auto-sleeping allows the World to identify bodies who have come to rest and<br />" +
				"skip steps with those bodies to provide better performance by putting them to sleep.</html>");
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
		this.lblBullet = new JLabel("Bullet", Icons.INFO, JLabel.LEFT);
		this.lblBullet.setToolTipText("A body flagged as a bullet require more processing, but can avoid tunneling.");
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
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " General "));
		
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
								.addComponent(this.pnlColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.lblOutlineColor)
										.addComponent(this.btnOutlineColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.lblFillColor)
										.addComponent(this.btnFillColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(this.pnlColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the mass section
		JPanel pnlMass = new JPanel();
		pnlMass.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Mass "));
		
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
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMassType)
						.addComponent(this.cmbMassType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMassExplicit)
						.addComponent(this.chkMassExplicit))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCenter)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMass)
						.addComponent(this.txtMass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblInertia)
						.addComponent(this.txtInertia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the state section
		JPanel pnlProperties = new JPanel();
		pnlProperties.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Properties "));
		
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
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblVelocity)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtVelocityX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblVelocityX)
										.addComponent(this.txtVelocityY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblVelocityY))))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAngularVelocity)
						.addComponent(this.txtAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblForce)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtForceX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblForceX)
										.addComponent(this.txtForceY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblForceY))))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblTorque)
						.addComponent(this.txtTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLinearDamping)
						.addComponent(this.sldLinearDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAngularDamping)
						.addComponent(this.sldAngularDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblGravityScale)
						.addComponent(this.sldGravityScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblState)
						.addComponent(this.chkInactive, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkAsleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the flags section
		JPanel pnlFlags = new JPanel();
		pnlFlags.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Flags "));
		
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
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAllowAutoSleep)
						.addComponent(this.chkAllowAutoSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
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
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {}
	
	/** The normal mass type option */
	private static final MassTypeItem NORMAL = new MassTypeItem("Normal", Mass.Type.NORMAL);
	
	/** The infinite mass type option */
	private static final MassTypeItem INFINITE = new MassTypeItem("Infinite", Mass.Type.INFINITE);
	
	/** The fixed linear velocity option */
	private static final MassTypeItem FIXED_LINEAR_VELOCITY = new MassTypeItem("Fixed Linear Velocity", Mass.Type.FIXED_LINEAR_VELOCITY);
	
	/** The fixed angular velocity option */
	private static final MassTypeItem FIXED_ANGULAR_VELOCITY = new MassTypeItem("Fixed Angular Velocity", Mass.Type.FIXED_ANGULAR_VELOCITY);
	
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
	private MassTypeItem getMassTypeItem(Mass.Type type) {
		for (MassTypeItem item : ITEMS) {
			if (item.type == type) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * List item class to store a name for a Mass.Type.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static final class MassTypeItem {
		/** The display name */
		public String name;
		
		/** The mass type */
		public Mass.Type type;
		
		/**
		 * Full constructor.
		 * @param name the display name of the mass type
		 * @param type the mass type
		 */
		public MassTypeItem(String name, Mass.Type type) {
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
