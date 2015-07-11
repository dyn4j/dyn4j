/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.sandbox.controls.ComboItem;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

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
		new ComboItem(Messages.getString("ccd.mode.all"), ContinuousDetectionMode.ALL),
		new ComboItem(Messages.getString("ccd.mode.bulletsOnly"), ContinuousDetectionMode.BULLETS_ONLY),
		new ComboItem(Messages.getString("ccd.mode.none"), ContinuousDetectionMode.NONE)
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
		
		JLabel lblStepFrequency = new JLabel(Messages.getString("panel.settings.frequency"), Icons.INFO, JLabel.LEFT);
		lblStepFrequency.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.frequency.tooltip"), Messages.getString("unit.time.singular")));
		this.txtStepFrequency = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.frequency.format")));
		this.txtStepFrequency.addFocusListener(new SelectTextFocusListener(this.txtStepFrequency));
		this.txtStepFrequency.setColumns(5);
		this.txtStepFrequency.setValue(1.0 / settings.getStepFrequency());
		
		JLabel lblMaxTranslation = new JLabel(Messages.getString("panel.settings.maximumTranslation"), Icons.INFO, JLabel.LEFT);
		lblMaxTranslation.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.maximumTranslation.tooltip"), Messages.getString("unit.length")));
		this.txtMaxTranslation = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.maximumTranslation.format")));
		this.txtMaxTranslation.addFocusListener(new SelectTextFocusListener(this.txtMaxTranslation));
		this.txtMaxTranslation.setColumns(5);
		this.txtMaxTranslation.setValue(settings.getMaximumTranslation());
		
		JLabel lblMaxRotation = new JLabel(Messages.getString("panel.settings.maximumRotation"), Icons.INFO, JLabel.LEFT);
		lblMaxRotation.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.maximumRotation.tooltip"), Messages.getString("unit.rotation")));
		this.txtMaxRotation = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.maximumRotation.format")));
		this.txtMaxRotation.addFocusListener(new SelectTextFocusListener(this.txtMaxRotation));
		this.txtMaxRotation.setColumns(5);
		this.txtMaxRotation.setValue(Math.toDegrees(settings.getMaximumRotation()));
		
		JLabel lblCCDMode = new JLabel(Messages.getString("panel.settings.ccd"), Icons.INFO, JLabel.LEFT);
		lblCCDMode.setToolTipText(Messages.getString("panel.settings.ccd.tooltip"));
		this.cmbCCDMode = new JComboBox(CCD_MODES);
		this.cmbCCDMode.setSelectedItem(this.getItem(settings.getContinuousDetectionMode()));
		
		// sleep
		
		JLabel lblAutoSleep = new JLabel(Messages.getString("panel.settings.autoSleeping"), Icons.INFO, JLabel.LEFT);
		lblAutoSleep.setToolTipText(Messages.getString("panel.settings.autoSleeping.tooltip"));
		this.chkAutoSleep = new JCheckBox();
		this.chkAutoSleep.setSelected(settings.isAutoSleepingEnabled());
		
		JLabel lblSleepTime = new JLabel(Messages.getString("panel.settings.sleepTime"), Icons.INFO, JLabel.LEFT);
		lblSleepTime.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.sleepTime.tooltip"), Messages.getString("unit.time")));
		this.txtSleepTime = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.sleepTime.format")));
		this.txtSleepTime.addFocusListener(new SelectTextFocusListener(this.txtSleepTime));
		this.txtSleepTime.setColumns(5);
		this.txtSleepTime.setValue(settings.getSleepTime());
		
		JLabel lblSleepLinearVelocity = new JLabel(Messages.getString("panel.settings.sleepLinearVelocity"), Icons.INFO, JLabel.LEFT);
		lblSleepLinearVelocity.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.sleepLinearVelocity.tooltip"), Messages.getString("unit.velocity.linear")));
		this.txtSleepLinearVelocity = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.sleepLinearVelocity.format")));
		this.txtSleepLinearVelocity.addFocusListener(new SelectTextFocusListener(this.txtSleepLinearVelocity));
		this.txtSleepLinearVelocity.setColumns(7);
		this.txtSleepLinearVelocity.setValue(settings.getSleepLinearVelocity());
		
		JLabel lblSleepAngularVelocity = new JLabel(Messages.getString("panel.settings.sleepAngularVelocity"), Icons.INFO, JLabel.LEFT);
		lblSleepAngularVelocity.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.sleepAngularVelocity.tooltip"), Messages.getString("unit.velocity.angular")));
		this.txtSleepAngularVelocity = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.sleepAngularVelocity.format")));
		this.txtSleepAngularVelocity.addFocusListener(new SelectTextFocusListener(this.txtSleepAngularVelocity));
		this.txtSleepAngularVelocity.setColumns(5);
		this.txtSleepAngularVelocity.setValue(Math.toDegrees(settings.getSleepAngularVelocity()));
		
		// solver
		
		JLabel lblVelocityIterations = new JLabel(Messages.getString("panel.settings.velocityIterations"), Icons.INFO, JLabel.LEFT);
		lblVelocityIterations.setToolTipText(Messages.getString("panel.settings.velocityIterations.tooltip"));
		this.txtVelocityIterations = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.velocityIterations.format")));
		this.txtVelocityIterations.addFocusListener(new SelectTextFocusListener(this.txtVelocityIterations));
		this.txtVelocityIterations.setColumns(3);
		this.txtVelocityIterations.setValue(settings.getVelocityConstraintSolverIterations());
		
		JLabel lblPositionIterations = new JLabel(Messages.getString("panel.settings.positionIterations"), Icons.INFO, JLabel.LEFT);
		lblPositionIterations.setToolTipText(Messages.getString("panel.settings.positionIterations.tooltip"));
		this.txtPositionIterations = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.positionIterations.format")));
		this.txtPositionIterations.addFocusListener(new SelectTextFocusListener(this.txtPositionIterations));
		this.txtPositionIterations.setColumns(3);
		this.txtPositionIterations.setValue(settings.getPositionConstraintSolverIterations());
		
		JLabel lblWarmStartDistance = new JLabel(Messages.getString("panel.settings.warmStartDistance"), Icons.INFO, JLabel.LEFT);
		lblWarmStartDistance.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.warmStartDistance.tooltip"), Messages.getString("unit.length")));
		this.txtWarmStartDistance = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.warmStartDistance.format")));
		this.txtWarmStartDistance.addFocusListener(new SelectTextFocusListener(this.txtWarmStartDistance));
		this.txtWarmStartDistance.setColumns(7);
		this.txtWarmStartDistance.setValue(settings.getWarmStartDistance());
		
		JLabel lblRestitutionVelocity = new JLabel(Messages.getString("panel.settings.restVelocity"), Icons.INFO, JLabel.LEFT);
		lblRestitutionVelocity.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.restVelocity.tooltip"), Messages.getString("unit.velocity.linear")));
		this.txtRestitutionVelocity = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.restVelocity.format")));
		this.txtRestitutionVelocity.addFocusListener(new SelectTextFocusListener(this.txtRestitutionVelocity));
		this.txtRestitutionVelocity.setColumns(5);
		this.txtRestitutionVelocity.setValue(settings.getRestitutionVelocity());
		
		JLabel lblLinearTolerance = new JLabel(Messages.getString("panel.settings.linearTolerance"), Icons.INFO, JLabel.LEFT);
		lblLinearTolerance.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.linearTolerance.tooltip"), Messages.getString("unit.length")));
		this.txtLinearTolerance = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.linearTolerance.format")));
		this.txtLinearTolerance.addFocusListener(new SelectTextFocusListener(this.txtLinearTolerance));
		this.txtLinearTolerance.setColumns(7);
		this.txtLinearTolerance.setValue(settings.getLinearTolerance());
		
		JLabel lblAngularTolerance = new JLabel(Messages.getString("panel.settings.angularTolerance"), Icons.INFO, JLabel.LEFT);
		lblAngularTolerance.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.angularTolerance.tooltip"), Messages.getString("unit.rotation")));
		this.txtAngularTolerance = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.angularTolerance.format")));
		this.txtAngularTolerance.addFocusListener(new SelectTextFocusListener(this.txtAngularTolerance));
		this.txtAngularTolerance.setColumns(5);
		this.txtAngularTolerance.setValue(Math.toDegrees(settings.getAngularTolerance()));
		
		JLabel lblMaxLinearCorrection = new JLabel(Messages.getString("panel.settings.maximumLinearCorrection"), Icons.INFO, JLabel.LEFT);
		lblMaxLinearCorrection.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.maximumLinearCorrection.tooltip"), Messages.getString("unit.length")));
		this.txtMaxLinearCorrection = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.maximumLinearCorrection.format")));
		this.txtMaxLinearCorrection.addFocusListener(new SelectTextFocusListener(this.txtMaxLinearCorrection));
		this.txtMaxLinearCorrection.setColumns(5);
		this.txtMaxLinearCorrection.setValue(settings.getMaximumLinearCorrection());
		
		JLabel lblMaxAngularCorrection = new JLabel(Messages.getString("panel.settings.maximumAngularCorrection"), Icons.INFO, JLabel.LEFT);
		lblMaxAngularCorrection.setToolTipText(MessageFormat.format(Messages.getString("panel.settings.maximumAngularCorrection.tooltip"), Messages.getString("unit.rotation")));
		this.txtMaxAngularCorrection = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.maximumAngularCorrection.format")));
		this.txtMaxAngularCorrection.addFocusListener(new SelectTextFocusListener(this.txtMaxAngularCorrection));
		this.txtMaxAngularCorrection.setColumns(5);
		this.txtMaxAngularCorrection.setValue(Math.toDegrees(settings.getMaximumAngularCorrection()));
		
		JLabel lblBaumgarte = new JLabel(Messages.getString("panel.settings.baumgarte"), Icons.INFO, JLabel.LEFT);
		lblBaumgarte.setToolTipText(Messages.getString("panel.settings.baumgarte.tooltip"));
		this.txtBaumgarte = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.settings.baumgarte.format")));
		this.txtBaumgarte.addFocusListener(new SelectTextFocusListener(this.txtBaumgarte));
		this.txtBaumgarte.setColumns(5);
		this.txtBaumgarte.setValue(settings.getBaumgarte());
		
		// create the layout
		
		// general section
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
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.settings.section.sleep"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlSleep.setBorder(border);
		
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
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.settings.section.solver"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlSolver.setBorder(border);
		
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
		settings.setStepFrequency(1.0 / ControlUtilities.getDoubleValue(this.txtStepFrequency));
		settings.setMaximumTranslation(ControlUtilities.getDoubleValue(this.txtMaxTranslation));
		settings.setMaximumRotation(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMaxRotation)));
		ComboItem item = ((ComboItem)this.cmbCCDMode.getSelectedItem());
		ContinuousDetectionMode cdm = (ContinuousDetectionMode)item.getValue();
		settings.setContinuousDetectionMode(cdm);
		
		// sleeping
		settings.setAutoSleepingEnabled(this.chkAutoSleep.isSelected());
		settings.setSleepTime(ControlUtilities.getDoubleValue(this.txtSleepTime));
		settings.setSleepLinearVelocity(ControlUtilities.getDoubleValue(this.txtSleepLinearVelocity));
		settings.setSleepAngularVelocity(Math.toRadians(ControlUtilities.getDoubleValue(this.txtSleepAngularVelocity)));
		
		// solver
		settings.setVelocityConstraintSolverIterations(ControlUtilities.getIntValue(this.txtVelocityIterations));
		settings.setPositionConstraintSolverIterations(ControlUtilities.getIntValue(this.txtPositionIterations));
		settings.setWarmStartDistance(ControlUtilities.getDoubleValue(this.txtWarmStartDistance));
		settings.setRestitutionVelocity(ControlUtilities.getDoubleValue(this.txtRestitutionVelocity));
		settings.setLinearTolerance(ControlUtilities.getDoubleValue(this.txtLinearTolerance));
		settings.setAngularTolerance(Math.toRadians(ControlUtilities.getDoubleValue(this.txtAngularTolerance)));
		settings.setMaximumLinearCorrection(ControlUtilities.getDoubleValue(this.txtMaxLinearCorrection));
		settings.setMaximumAngularCorrection(Math.toRadians(ControlUtilities.getDoubleValue(this.txtMaxAngularCorrection)));
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
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidFrequency"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxTranslation) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidMaximumTranslation"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxRotation) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidMaximumRotation"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepTime) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidSleepTime"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepLinearVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidSleepLinearVelocity"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtSleepAngularVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidSleepAngularVelocity"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getIntValue(this.txtVelocityIterations) < 5) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidVelocityIterations"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getIntValue(this.txtPositionIterations) < 5) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidPositionIterations"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtWarmStartDistance) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidWarmStartDistance"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtRestitutionVelocity) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidRestVelocity"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtLinearTolerance) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidLinearTolerance"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtAngularTolerance) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidAngularTolerance"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxLinearCorrection) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidLinearCorrection"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtMaxAngularCorrection) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidAngularCorrection"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		} else if (ControlUtilities.getDoubleValue(this.txtBaumgarte) < 0.0) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.settings.invalidBaumgarte"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
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
