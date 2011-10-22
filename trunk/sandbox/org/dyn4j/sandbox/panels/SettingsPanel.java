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

import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.Settings.ContinuousDetectionMode;
import org.dyn4j.sandbox.controls.ComboItem;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to set global dynamics settings.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class SettingsPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -5243643510080091210L;
	
	/** The list of continuous collision detection modes */
	private static final ComboItem[] CCD_MODES = new ComboItem[] {
		new ComboItem("All", Settings.ContinuousDetectionMode.ALL),
		new ComboItem("Bullets Only", Settings.ContinuousDetectionMode.BULLETS_ONLY),
		new ComboItem("None", Settings.ContinuousDetectionMode.NONE)
	};
	
	// general
	
	/** The text box for the step frequency */
	private JFormattedTextField txtStepFrequency;
	
	/** The text box for the maximum translation */
	private JFormattedTextField txtMaxTranslation;
	
	/** The text box for the maximum rotation */
	private JFormattedTextField txtMaxRotation;
	
	/** The combo box for the continuous collision detection mode */
	private JComboBox cmbCCDMode;
	
	// sleep
	
	/** The check box for auto sleepting */
	private JCheckBox chkAutoSleep;
	
	/** The text box for the sleep time */
	private JFormattedTextField txtSleepTime;
	
	/** The text box for the sleep linear velocity */
	private JFormattedTextField txtSleepLinearVelocity;
	
	/** The text box for the sleep angular velocity */
	private JFormattedTextField txtSleepAngularVelocity;
	
	// solver
	
	/** The text box for the number of velocity iterations */
	private JFormattedTextField txtVelocityIterations;
	
	/** The text box for the number of position iterations */
	private JFormattedTextField txtPositionIterations;
	
	/** The text box for the warm start distance */
	private JFormattedTextField txtWarmStartDistance;
	
	/** The text box for the restitution velocity */
	private JFormattedTextField txtRestitutionVelocity;
	
	/** The text box for the linear tolerance */
	private JFormattedTextField txtLinearTolerance;
	
	/** The text box for the angular tolerance */
	private JFormattedTextField txtAngularTolerance;
	
	/** The text box for the maximum linear correction */
	private JFormattedTextField txtMaxLinearCorrection;
	
	/** The text box for the maximum angular correction */
	private JFormattedTextField txtMaxAngularCorrection;
	
	/** The text box for the baumgarte*/
	private JFormattedTextField txtBaumgarte;
	
	/**
	 * Full constructor.
	 * @param settings the current settings
	 */
	public SettingsPanel(Settings settings) {
		
		// general
		
		JLabel lblStepFrequency = new JLabel("Step Frequency", Icons.INFO, JLabel.LEFT);
		lblStepFrequency.setToolTipText(
				"<html>Specifies how many times the world is updated in one second." +
				"<br />Larger values provide for a more accurate and stable simulation at the cost of performance." +
				"<br />Must be 30 or greater.</html>");
		this.txtStepFrequency = new JFormattedTextField(new DecimalFormat("0"));
		this.txtStepFrequency.addFocusListener(new SelectTextFocusListener(this.txtStepFrequency));
		this.txtStepFrequency.setColumns(5);
		this.txtStepFrequency.setValue(1.0 / settings.getStepFrequency());
		
		JLabel lblMaxTranslation = new JLabel("Maximum Translation", Icons.INFO, JLabel.LEFT);
		lblMaxTranslation.setToolTipText(
				"<html>The maximum distance a body can travel within one time step in Meters." +
				"<br />This helps avoid really fast bodies." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtMaxTranslation = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtMaxTranslation.addFocusListener(new SelectTextFocusListener(this.txtMaxTranslation));
		this.txtMaxTranslation.setColumns(5);
		this.txtMaxTranslation.setValue(settings.getMaxTranslation());
		
		JLabel lblMaxRotation = new JLabel("Maximum Rotation", Icons.INFO, JLabel.LEFT);
		lblMaxRotation.setToolTipText(
				"<html>The maximum rotation a body can perform within one time step in Degrees." +
				"<br />This helps avoid really fast spinning bodies." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtMaxRotation = new JFormattedTextField(new DecimalFormat("0"));
		this.txtMaxRotation.addFocusListener(new SelectTextFocusListener(this.txtMaxRotation));
		this.txtMaxRotation.setColumns(5);
		this.txtMaxRotation.setValue(Math.toDegrees(settings.getMaxRotation()));
		
		JLabel lblCCDMode = new JLabel("Continuous Collision Detection Mode", Icons.INFO, JLabel.LEFT);
		lblCCDMode.setToolTipText("<html>Determines the bodies that are checked for tunneling.</html>");
		this.cmbCCDMode = new JComboBox(CCD_MODES);
		this.cmbCCDMode.setSelectedItem(this.getItem(settings.getContinuousDetectionMode()));
		
		// sleep
		
		JLabel lblAutoSleep = new JLabel("Auto Sleeping Enabled", Icons.INFO, JLabel.LEFT);
		lblAutoSleep.setToolTipText(
				"<html>Determines whether bodies are automatically put to sleep when they come to rest." +
				"<br />Putting bodies to sleep when they come to rest allows the dynamics solvers to " +
				"<br />save time thereby increase performance.</html>");
		this.chkAutoSleep = new JCheckBox();
		this.chkAutoSleep.setSelected(settings.isAutoSleepingEnabled());
		
		JLabel lblSleepTime = new JLabel("Sleep Time", Icons.INFO, JLabel.LEFT);
		lblSleepTime.setToolTipText(
				"<html>The time, in Seconds, that a body must be at rest before being put to sleep." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtSleepTime = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtSleepTime.addFocusListener(new SelectTextFocusListener(this.txtSleepTime));
		this.txtSleepTime.setColumns(5);
		this.txtSleepTime.setValue(settings.getSleepTime());
		
		JLabel lblSleepLinearVelocity = new JLabel("Sleep Linear Velocity", Icons.INFO, JLabel.LEFT);
		lblSleepLinearVelocity.setToolTipText(
				"<html>The maximum linear velocity a body can have before being flagged as 'at rest' in Meters/Second." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtSleepLinearVelocity = new JFormattedTextField(new DecimalFormat("0.00"));
		this.txtSleepLinearVelocity.addFocusListener(new SelectTextFocusListener(this.txtSleepLinearVelocity));
		this.txtSleepLinearVelocity.setColumns(7);
		this.txtSleepLinearVelocity.setValue(settings.getSleepVelocity());
		
		JLabel lblSleepAngularVelocity = new JLabel("Sleep Angular Velocity", Icons.INFO, JLabel.LEFT);
		lblSleepAngularVelocity.setToolTipText(
				"<html>The maximum angular velocity a body can have before being flagged as 'at rest' in Degrees/Second." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtSleepAngularVelocity = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtSleepAngularVelocity.addFocusListener(new SelectTextFocusListener(this.txtSleepAngularVelocity));
		this.txtSleepAngularVelocity.setColumns(5);
		this.txtSleepAngularVelocity.setValue(Math.toDegrees(settings.getSleepAngularVelocity()));
		
		// solver
		
		JLabel lblVelocityIterations = new JLabel("Velocity Iterations", Icons.INFO, JLabel.LEFT);
		lblVelocityIterations.setToolTipText(
				"<html>The number of solver iterations for velocity constraints." +
				"<br />Larger values increase accuracy and stability but decrease performance." +
				"<br />Must be greater than or equal to 5.</html>");
		this.txtVelocityIterations = new JFormattedTextField(new DecimalFormat("0"));
		this.txtVelocityIterations.addFocusListener(new SelectTextFocusListener(this.txtVelocityIterations));
		this.txtVelocityIterations.setColumns(3);
		this.txtVelocityIterations.setValue(settings.getVelocityConstraintSolverIterations());
		
		JLabel lblPositionIterations = new JLabel("Position Iterations", Icons.INFO, JLabel.LEFT);
		lblPositionIterations.setToolTipText(
				"<html>The number of solver iterations for position constraints." +
				"<br />Larger values increase accuracy and stability but decrease performance." +
				"<br />Position constraints are used to combat drift in the velocity constraints." +
				"<br />Must be greater than or equal to 5.</html>");
		this.txtPositionIterations = new JFormattedTextField(new DecimalFormat("0"));
		this.txtPositionIterations.addFocusListener(new SelectTextFocusListener(this.txtPositionIterations));
		this.txtPositionIterations.setColumns(3);
		this.txtPositionIterations.setValue(settings.getPositionConstraintSolverIterations());
		
		JLabel lblWarmStartDistance = new JLabel("Warm Start Distance", Icons.INFO, JLabel.LEFT);
		lblWarmStartDistance.setToolTipText(
				"<html>The maximum distance between iterations that a contact can be warm started in Meters." +
				"<br />Warm started contacts help improve performance and accuracy." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtWarmStartDistance = new JFormattedTextField(new DecimalFormat("0.00"));
		this.txtWarmStartDistance.addFocusListener(new SelectTextFocusListener(this.txtWarmStartDistance));
		this.txtWarmStartDistance.setColumns(7);
		this.txtWarmStartDistance.setValue(settings.getWarmStartDistance());
		
		JLabel lblRestitutionVelocity = new JLabel("Restitution Velocity", Icons.INFO, JLabel.LEFT);
		lblRestitutionVelocity.setToolTipText(
				"<html>The minimum <b>relative</b> velocity between bodies required to treat a collision as elastic in Meters/Second." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtRestitutionVelocity = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtRestitutionVelocity.addFocusListener(new SelectTextFocusListener(this.txtRestitutionVelocity));
		this.txtRestitutionVelocity.setColumns(5);
		this.txtRestitutionVelocity.setValue(settings.getRestitutionVelocity());
		
		JLabel lblLinearTolerance = new JLabel("Linear Tolerance", Icons.INFO, JLabel.LEFT);
		lblLinearTolerance.setToolTipText(
				"<html>The allowed penetration between bodies and the allowed separation of joints in Meters." +
				"<br />This value should be small enough to not be seen but large enough to maintain stability." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtLinearTolerance = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtLinearTolerance.addFocusListener(new SelectTextFocusListener(this.txtLinearTolerance));
		this.txtLinearTolerance.setColumns(7);
		this.txtLinearTolerance.setValue(settings.getLinearTolerance());
		
		JLabel lblAngularTolerance = new JLabel("Angular Tolerance", Icons.INFO, JLabel.LEFT);
		lblAngularTolerance.setToolTipText(
				"<html>The allowed tolerance in angular joint limits in Degrees." +
				"<br />This value should be small enough to not be seen but large enough to maintain stability." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtAngularTolerance = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtAngularTolerance.addFocusListener(new SelectTextFocusListener(this.txtAngularTolerance));
		this.txtAngularTolerance.setColumns(5);
		this.txtAngularTolerance.setValue(Math.toDegrees(settings.getAngularTolerance()));
		
		JLabel lblMaxLinearCorrection = new JLabel("Maximum Linear Correction", Icons.INFO, JLabel.LEFT);
		lblMaxLinearCorrection.setToolTipText(
				"<html>The maximum linear correction applied in one time step during the position" +
				"<br />constraint solver to compensate for joint separation in Meters." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtMaxLinearCorrection = new JFormattedTextField(new DecimalFormat("0.00"));
		this.txtMaxLinearCorrection.addFocusListener(new SelectTextFocusListener(this.txtMaxLinearCorrection));
		this.txtMaxLinearCorrection.setColumns(5);
		this.txtMaxLinearCorrection.setValue(settings.getMaxLinearCorrection());
		
		JLabel lblMaxAngularCorrection = new JLabel("Maximum Angular Correction", Icons.INFO, JLabel.LEFT);
		lblMaxAngularCorrection.setToolTipText(
				"<html>The maximum angular correction applied in one time step during the position" +
				"<br />constraint solver to compensate for angular joint limit separation in Degrees." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtMaxAngularCorrection = new JFormattedTextField(new DecimalFormat("0.0"));
		this.txtMaxAngularCorrection.addFocusListener(new SelectTextFocusListener(this.txtMaxAngularCorrection));
		this.txtMaxAngularCorrection.setColumns(5);
		this.txtMaxAngularCorrection.setValue(Math.toDegrees(settings.getMaxAngularCorrection()));
		
		JLabel lblBaumgarte = new JLabel("Baumgarte", Icons.INFO, JLabel.LEFT);
		lblBaumgarte.setToolTipText(
				"<html>Specifies the rate at which contact position constraints are solved." +
				"<br />Must be greater than or equal to zero.</html>");
		this.txtBaumgarte = new JFormattedTextField(new DecimalFormat("0.00"));
		this.txtBaumgarte.addFocusListener(new SelectTextFocusListener(this.txtBaumgarte));
		this.txtBaumgarte.setColumns(5);
		this.txtBaumgarte.setValue(settings.getBaumgarte());
		
		// create the layout
		
		// general section
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " General "));
		
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblStepFrequency)
						.addComponent(lblMaxTranslation)
						.addComponent(lblMaxRotation)
						.addComponent(lblCCDMode))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtStepFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtMaxTranslation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtMaxRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbCCDMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblStepFrequency)
						.addComponent(this.txtStepFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxTranslation)
						.addComponent(this.txtMaxTranslation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxRotation)
						.addComponent(this.txtMaxRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblCCDMode)
						.addComponent(this.cmbCCDMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// sleep section
		JPanel pnlSleep = new JPanel();
		pnlSleep.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Sleep "));
		
		layout = new GroupLayout(pnlSleep);
		pnlSleep.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAutoSleep)
						.addComponent(lblSleepTime)
						.addComponent(lblSleepLinearVelocity)
						.addComponent(lblSleepAngularVelocity))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkAutoSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSleepTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSleepLinearVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSleepAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAutoSleep)
						.addComponent(this.chkAutoSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSleepTime)
						.addComponent(this.txtSleepTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSleepLinearVelocity)
						.addComponent(this.txtSleepLinearVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSleepAngularVelocity)
						.addComponent(this.txtSleepAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// solver section
		JPanel pnlSolver = new JPanel();
		pnlSolver.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Solver "));
		
		layout = new GroupLayout(pnlSolver);
		pnlSolver.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblVelocityIterations)
						.addComponent(lblPositionIterations)
						.addComponent(lblWarmStartDistance)
						.addComponent(lblRestitutionVelocity)
						.addComponent(lblLinearTolerance)
						.addComponent(lblAngularTolerance)
						.addComponent(lblMaxLinearCorrection)
						.addComponent(lblMaxAngularCorrection)
						.addComponent(lblBaumgarte))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtVelocityIterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtPositionIterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtWarmStartDistance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtRestitutionVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtLinearTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtAngularTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtMaxLinearCorrection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtMaxAngularCorrection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtBaumgarte, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblVelocityIterations)
						.addComponent(this.txtVelocityIterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPositionIterations)
						.addComponent(this.txtPositionIterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblWarmStartDistance)
						.addComponent(this.txtWarmStartDistance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRestitutionVelocity)
						.addComponent(this.txtRestitutionVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLinearTolerance)
						.addComponent(this.txtLinearTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAngularTolerance)
						.addComponent(this.txtAngularTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxLinearCorrection)
						.addComponent(this.txtMaxLinearCorrection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMaxAngularCorrection)
						.addComponent(this.txtMaxAngularCorrection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBaumgarte)
						.addComponent(this.txtBaumgarte, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// layout out the settings panel
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlSolver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlSleep)
				.addComponent(pnlSolver));
	}
	
	/**
	 * Sets the given settings to the user's input.
	 * @param settings the settings to set
	 */
	public void setSettings(Settings settings) {
		// general
		settings.setStepFrequency(ControlUtilities.getDoubleValue(this.txtStepFrequency));
		settings.setMaxTranslation(ControlUtilities.getDoubleValue(this.txtMaxTranslation));
		settings.setMaxRotation(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMaxRotation)));
		ComboItem item = ((ComboItem)this.cmbCCDMode.getSelectedItem());
		ContinuousDetectionMode cdm = (ContinuousDetectionMode)item.getValue();
		settings.setContinuousDetectionMode(cdm);
		
		// sleeping
		settings.setAutoSleepingEnabled(this.chkAutoSleep.isSelected());
		settings.setSleepTime(ControlUtilities.getDoubleValue(this.txtSleepTime));
		settings.setSleepVelocity(ControlUtilities.getDoubleValue(this.txtSleepLinearVelocity));
		settings.setSleepAngularVelocity(Math.toRadians(ControlUtilities.getDoubleValue(this.txtSleepAngularVelocity)));
		
		// solver
		settings.setVelocityConstraintSolverIterations(ControlUtilities.getIntValue(this.txtVelocityIterations));
		settings.setPositionConstraintSolverIterations(ControlUtilities.getIntValue(this.txtPositionIterations));
		settings.setWarmStartDistance(ControlUtilities.getDoubleValue(this.txtWarmStartDistance));
		settings.setRestitutionVelocity(ControlUtilities.getDoubleValue(this.txtRestitutionVelocity));
		settings.setLinearTolerance(ControlUtilities.getDoubleValue(this.txtLinearTolerance));
		settings.setAngularTolerance(Math.toRadians(ControlUtilities.getDoubleValue(this.txtAngularTolerance)));
		settings.setMaxLinearCorrection(ControlUtilities.getDoubleValue(this.txtMaxLinearCorrection));
		settings.setMaxAngularCorrection(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMaxAngularCorrection)));
		settings.setBaumgarte(ControlUtilities.getDoubleValue(this.txtBaumgarte));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (ControlUtilities.getDoubleValue(this.txtStepFrequency) < 30.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxTranslation) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxRotation) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtSleepTime) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtSleepLinearVelocity) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtSleepAngularVelocity) < 0.0) {
			return false;
		}
		if (ControlUtilities.getIntValue(this.txtVelocityIterations) < 5) {
			return false;
		}
		if (ControlUtilities.getIntValue(this.txtPositionIterations) < 5) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtWarmStartDistance) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtRestitutionVelocity) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtLinearTolerance) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtAngularTolerance) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxLinearCorrection) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtMaxAngularCorrection) < 0.0) {
			return false;
		}
		if (ControlUtilities.getDoubleValue(this.txtBaumgarte) < 0.0) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (ControlUtilities.getDoubleValue(this.txtStepFrequency) < 30.0) {
			JOptionPane.showMessageDialog(owner, "The step frequency must be 30 or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxTranslation) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum translation must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxRotation) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum rotation must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepTime) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The sleep time must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepLinearVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The sleep linear velocity must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepAngularVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The sleep angular velocity must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getIntValue(this.txtVelocityIterations) < 5) {
			JOptionPane.showMessageDialog(owner, "The velocity iterations must be 5 or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getIntValue(this.txtPositionIterations) < 5) {
			JOptionPane.showMessageDialog(owner, "The position iterations must be 5 or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtWarmStartDistance) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The warm start distance must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtRestitutionVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The restitution velocity must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtLinearTolerance) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The linear tolerance must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtAngularTolerance) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The angular tolerance must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxLinearCorrection) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum linear correction must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxAngularCorrection) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum angular correction must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtBaumgarte) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The baumgarte must be zero or greater.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Returns the combo item for the given mode.
	 * @param mode the mode
	 * @return ComboItem
	 */
	private ComboItem getItem(ContinuousDetectionMode mode) {
		for (ComboItem item : CCD_MODES) {
			ContinuousDetectionMode cdm = (ContinuousDetectionMode)item.getValue();
			if (cdm == mode) {
				return item;
			}
		}
		return null;
	}
}
