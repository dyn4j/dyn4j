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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.dyn4j.sandbox.Preferences;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.dialogs.ColorDialog;
import org.dyn4j.sandbox.utilities.ColorUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to configure the application preferences.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class PreferencesPanel extends JPanel implements MouseListener {
	/** The version id */
	private static final long serialVersionUID = 8860821268837359120L;
	
	// flags
	
	/** The check box for random body colors */
	private JCheckBox chkRandomColors;
	
	/** The check box for body stenciling */
	private JCheckBox chkStenciling;
	
	/** The check box for body labels */
	private JCheckBox chkBodyLabels;
	
	/** The check box for fixture labels */
	private JCheckBox chkFixtureLabels;
	
	/** The check box for anti-aliasing */
	private JCheckBox chkAntiAliasing;
	
	/** The check box for vertical sync */
	private JCheckBox chkVerticalSync;
	
	/** The check box for the origin label */
	private JCheckBox chkOriginLabel;
	
	/** The check box for the bounds */
	private JCheckBox chkBounds;
	
	/** The check box for sleeping bodies */
	private JCheckBox chkSleepingBodyColor;
	
	/** The check box for inactive bodies */
	private JCheckBox chkInActiveBodyColor;
	
	/** The check box for body centers */
	private JCheckBox chkBodyCenter;
	
	/** The check box for the pixel/meter scale */
	private JCheckBox chkScale;
	
	/** The check box for body AABBs */
	private JCheckBox chkBodyAABB;
	
	/** The check box for body normals */
	private JCheckBox chkBodyNormal;
	
	/** The check box for body rotation discs */
	private JCheckBox chkBodyRotationDisc;
	
	/** The check box for body velocities */
	private JCheckBox chkBodyVelocity;
	
	/** The check box for contact points */
	private JCheckBox chkContacts;
	
	/** The check box for contact pairs */
	private JCheckBox chkContactPairs;
	
	/** The check box for contact impulses */
	private JCheckBox chkContactImpulses;
	
	/** The check box for friction impulses */
	private JCheckBox chkFrictionImpulses;
	
	// coloring
	
	/** The panel to preview the selected body/fixture color */
	private JPanel pnlSelectedColor;
	
	/** The panel to preview the bounds color */
	private JPanel pnlBoundsColor;
	
	/** The panel to preview the sleeping body color */
	private JPanel pnlSleepingBodyColor;
	
	/** The panel to preview the inactive body color */
	private JPanel pnlInActiveBodyColor;
	
	/** The panel to preview the body center color */
	private JPanel pnlBodyCenterColor;
	
	/** The panel to preview the body AABB color */
	private JPanel pnlBodyAABBColor;
	
	/** The panel to preview the body normal color */
	private JPanel pnlBodyNormalColor;
	
	/** The panel to preview the body rotation disc color */
	private JPanel pnlBodyRotationDiscColor;
	
	/** The panel to preview the body velocity color */
	private JPanel pnlBodyVelocityColor;
	
	/** The panel to preview the contact color */
	private JPanel pnlContactColor;
	
	/** The panel to preview the contact pair color */
	private JPanel pnlContactPairColor;
	
	/** The panel to preview the contact impulse color */
	private JPanel pnlContactImpulseColor;
	
	/** The panel to preview the friction impulse color */
	private JPanel pnlFrictionImpulseColor;
	
	// on hover change the border
	
	/** The original border of the currently hovered over panel */
	private Border originalBorder;
	
	/**
	 * Default constructor.
	 */
	public PreferencesPanel() {
		// create the size for the color panels
		Dimension size = new Dimension(13, 13);
		Dimension msize = new Dimension(200, 0);
		
		JLabel lblRandomColors = new JLabel(Resources.getString("panel.preferences.bodyColor"), Icons.INFO, JLabel.LEFT);
		lblRandomColors.setToolTipText(Resources.getString("panel.preferences.bodyColor.tooltip"));
		lblRandomColors.setMinimumSize(msize);
		this.chkRandomColors = new JCheckBox();
		this.chkRandomColors.setSelected(Preferences.isBodyColorRandom());
		
		JLabel lblStenciling = new JLabel(Resources.getString("panel.preferences.bodyStenciling"), Icons.INFO, JLabel.LEFT);
		lblStenciling.setToolTipText(Resources.getString("panel.preferences.bodyStenciling.tooltip"));
		lblStenciling.setMinimumSize(msize);
		this.chkStenciling = new JCheckBox();
		this.chkStenciling.setSelected(Preferences.isBodyStenciled());
		
		JLabel lblBodyLabels = new JLabel(Resources.getString("panel.preferences.bodyLabels"), Icons.INFO, JLabel.LEFT);
		lblBodyLabels.setToolTipText(Resources.getString("panel.preferences.bodyLabels.tooltip"));
		lblBodyLabels.setMinimumSize(msize);
		this.chkBodyLabels = new JCheckBox();
		this.chkBodyLabels.setSelected(Preferences.isBodyLabeled());
		
		JLabel lblFixtureLabels = new JLabel(Resources.getString("panel.preferences.fixtureLabels"), Icons.INFO, JLabel.LEFT);
		lblFixtureLabels.setToolTipText(Resources.getString("panel.preferences.fixtureLabels.tooltip"));
		lblFixtureLabels.setMinimumSize(msize);
		this.chkFixtureLabels = new JCheckBox();
		this.chkFixtureLabels.setSelected(Preferences.isFixtureLabeled());
		
		JLabel lblAntiAliasing = new JLabel(Resources.getString("panel.preferences.aa"), Icons.INFO, JLabel.LEFT);
		lblAntiAliasing.setToolTipText(Resources.getString("panel.preferences.aa.tooltip"));
		lblAntiAliasing.setMinimumSize(msize);
		this.chkAntiAliasing = new JCheckBox();
		this.chkAntiAliasing.setSelected(Preferences.isAntiAliasingEnabled());
		
		JLabel lblVerticalSync = new JLabel(Resources.getString("panel.preferences.verticalSync"), Icons.INFO, JLabel.LEFT);
		lblVerticalSync.setToolTipText(Resources.getString("panel.preferences.verticalSync.tooltip"));
		lblVerticalSync.setMinimumSize(msize);
		this.chkVerticalSync = new JCheckBox();
		this.chkVerticalSync.setSelected(Preferences.isVerticalSyncEnabled());
		
		JLabel lblOriginLabel = new JLabel(Resources.getString("panel.preferences.originLabel"), Icons.INFO, JLabel.LEFT);
		lblOriginLabel.setToolTipText(Resources.getString("panel.preferences.originLabel.tooltip"));
		lblOriginLabel.setMinimumSize(msize);
		this.chkOriginLabel = new JCheckBox();
		this.chkOriginLabel.setSelected(Preferences.isOriginLabeled());
		
		JLabel lblBounds = new JLabel(Resources.getString("panel.preferences.worldBounds"), Icons.INFO, JLabel.LEFT);
		lblBounds.setToolTipText(Resources.getString("panel.preferences.worldBounds.tooltip"));
		lblBounds.setMinimumSize(msize);
		this.chkBounds = new JCheckBox();
		this.chkBounds.setSelected(Preferences.isBoundsEnabled());
		this.pnlBoundsColor = new JPanel();
		this.pnlBoundsColor.addMouseListener(this);
		this.pnlBoundsColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBoundsColor.setPreferredSize(size);
		this.pnlBoundsColor.setBackground(ColorUtilities.convertColor(Preferences.getBoundsColor()));
		this.pnlBoundsColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblSleepingBody = new JLabel(Resources.getString("panel.preferences.bodySleepColor"), Icons.INFO, JLabel.LEFT);
		lblSleepingBody.setToolTipText(Resources.getString("panel.preferences.bodySleepColor.tooltip"));
		lblSleepingBody.setMinimumSize(msize);
		this.chkSleepingBodyColor = new JCheckBox();
		this.chkSleepingBodyColor.setSelected(Preferences.isBodyAsleepColorEnabled());
		this.pnlSleepingBodyColor = new JPanel();
		this.pnlSleepingBodyColor.addMouseListener(this);
		this.pnlSleepingBodyColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlSleepingBodyColor.setPreferredSize(size);
		this.pnlSleepingBodyColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyAsleepColor()));
		this.pnlSleepingBodyColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblInActiveBody = new JLabel(Resources.getString("panel.preferences.bodyInactiveColor"), Icons.INFO, JLabel.LEFT);
		lblInActiveBody.setToolTipText(Resources.getString("panel.preferences.bodyInactiveColor.tooltip"));
		lblInActiveBody.setMinimumSize(msize);
		this.chkInActiveBodyColor = new JCheckBox();
		this.chkInActiveBodyColor.setSelected(Preferences.isBodyInActiveColorEnabled());
		this.pnlInActiveBodyColor = new JPanel();
		this.pnlInActiveBodyColor.addMouseListener(this);
		this.pnlInActiveBodyColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlInActiveBodyColor.setPreferredSize(size);
		this.pnlInActiveBodyColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyInActiveColor()));
		this.pnlInActiveBodyColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblBodyCenter = new JLabel(Resources.getString("panel.preferences.bodyCenter"), Icons.INFO, JLabel.LEFT);
		lblBodyCenter.setToolTipText(Resources.getString("panel.preferences.bodyCenter.tooltip"));
		lblBodyCenter.setMinimumSize(msize);
		this.chkBodyCenter = new JCheckBox();
		this.chkBodyCenter.setSelected(Preferences.isBodyCenterEnabled());
		this.pnlBodyCenterColor = new JPanel();
		this.pnlBodyCenterColor.addMouseListener(this);
		this.pnlBodyCenterColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBodyCenterColor.setPreferredSize(size);
		this.pnlBodyCenterColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyCenterColor()));
		this.pnlBodyCenterColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblScale = new JLabel(Resources.getString("panel.preferences.scale"), Icons.INFO, JLabel.LEFT);
		lblScale.setToolTipText(MessageFormat.format(Resources.getString("panel.preferences.scale.tooltip"), Resources.getString("unit.length.singular")));
		lblScale.setMinimumSize(msize);
		this.chkScale = new JCheckBox();
		this.chkScale.setSelected(Preferences.isScaleEnabled());
		
		JLabel lblSelectedColor = new JLabel(Resources.getString("panel.preferences.selectedColor"), Icons.INFO, JLabel.LEFT);
		lblSelectedColor.setToolTipText(Resources.getString("panel.preferences.selectedColor.tooltip"));
		lblSelectedColor.setMinimumSize(msize);
		this.pnlSelectedColor = new JPanel();
		this.pnlSelectedColor.addMouseListener(this);
		this.pnlSelectedColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlSelectedColor.setPreferredSize(size);
		this.pnlSelectedColor.setBackground(ColorUtilities.convertColor(Preferences.getSelectedColor()));
		this.pnlSelectedColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblBodyAABBs = new JLabel(Resources.getString("panel.preferences.bodyAABBs"), Icons.INFO, JLabel.LEFT);
		lblBodyAABBs.setToolTipText(Resources.getString("panel.preferences.bodyAABBs.tooltip"));
		lblBodyAABBs.setMinimumSize(msize);
		this.chkBodyAABB = new JCheckBox();
		this.chkBodyAABB.setSelected(Preferences.isBodyAABBEnabled());
		this.pnlBodyAABBColor = new JPanel();
		this.pnlBodyAABBColor.addMouseListener(this);
		this.pnlBodyAABBColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBodyAABBColor.setPreferredSize(size);
		this.pnlBodyAABBColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyAABBColor()));
		this.pnlBodyAABBColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblBodyNormals = new JLabel(Resources.getString("panel.preferences.fixtureNormals"), Icons.INFO, JLabel.LEFT);
		lblBodyNormals.setToolTipText(Resources.getString("panel.preferences.fixtureNormals.tooltip"));
		lblBodyNormals.setMinimumSize(msize);
		this.chkBodyNormal = new JCheckBox();
		this.chkBodyNormal.setSelected(Preferences.isBodyNormalEnabled());
		this.pnlBodyNormalColor = new JPanel();
		this.pnlBodyNormalColor.addMouseListener(this);
		this.pnlBodyNormalColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBodyNormalColor.setPreferredSize(size);
		this.pnlBodyNormalColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyNormalColor()));
		this.pnlBodyNormalColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblBodyRotationDiscs = new JLabel(Resources.getString("panel.preferences.bodyRotationDiscs"), Icons.INFO, JLabel.LEFT);
		lblBodyRotationDiscs.setToolTipText(Resources.getString("panel.preferences.bodyRotationDiscs.tooltip"));
		lblBodyRotationDiscs.setMinimumSize(msize);
		this.chkBodyRotationDisc = new JCheckBox();
		this.chkBodyRotationDisc.setSelected(Preferences.isBodyRotationDiscEnabled());
		this.pnlBodyRotationDiscColor = new JPanel();
		this.pnlBodyRotationDiscColor.addMouseListener(this);
		this.pnlBodyRotationDiscColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBodyRotationDiscColor.setPreferredSize(size);
		this.pnlBodyRotationDiscColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyRotationDiscColor()));
		this.pnlBodyRotationDiscColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblBodyVelocities = new JLabel(Resources.getString("panel.preferences.bodyVelocities"), Icons.INFO, JLabel.LEFT);
		lblBodyVelocities.setToolTipText(Resources.getString("panel.preferences.bodyVelocities.tooltip"));
		lblBodyVelocities.setMinimumSize(msize);
		this.chkBodyVelocity = new JCheckBox();
		this.chkBodyVelocity.setSelected(Preferences.isBodyVelocityEnabled());
		this.pnlBodyVelocityColor = new JPanel();
		this.pnlBodyVelocityColor.addMouseListener(this);
		this.pnlBodyVelocityColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlBodyVelocityColor.setPreferredSize(size);
		this.pnlBodyVelocityColor.setBackground(ColorUtilities.convertColor(Preferences.getBodyVelocityColor()));
		this.pnlBodyVelocityColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblContacts = new JLabel(Resources.getString("panel.preferences.contacts"), Icons.INFO, JLabel.LEFT);
		lblContacts.setToolTipText(Resources.getString("panel.preferences.contacts.tooltip"));
		lblContacts.setMinimumSize(msize);
		this.chkContacts = new JCheckBox();
		this.chkContacts.setSelected(Preferences.isContactPointEnabled());
		this.pnlContactColor = new JPanel();
		this.pnlContactColor.addMouseListener(this);
		this.pnlContactColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlContactColor.setPreferredSize(size);
		this.pnlContactColor.setBackground(ColorUtilities.convertColor(Preferences.getContactPointColor()));
		this.pnlContactColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblContactImpulses = new JLabel(Resources.getString("panel.preferences.contactImpulses"), Icons.INFO, JLabel.LEFT);
		lblContactImpulses.setToolTipText(Resources.getString("panel.preferences.contactImpulses.tooltip"));
		lblContactImpulses.setMinimumSize(msize);
		this.chkContactImpulses = new JCheckBox();
		this.chkContactImpulses.setSelected(Preferences.isContactImpulseEnabled());
		this.pnlContactImpulseColor = new JPanel();
		this.pnlContactImpulseColor.addMouseListener(this);
		this.pnlContactImpulseColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlContactImpulseColor.setPreferredSize(size);
		this.pnlContactImpulseColor.setBackground(ColorUtilities.convertColor(Preferences.getContactImpulseColor()));
		this.pnlContactImpulseColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblFrictionImpulses = new JLabel(Resources.getString("panel.preferences.frictionImpulses"), Icons.INFO, JLabel.LEFT);
		lblFrictionImpulses.setToolTipText(Resources.getString("panel.preferences.frictionImpulses.tooltip"));
		lblFrictionImpulses.setMinimumSize(msize);
		this.chkFrictionImpulses = new JCheckBox();
		this.chkFrictionImpulses.setSelected(Preferences.isFrictionImpulseEnabled());
		this.pnlFrictionImpulseColor = new JPanel();
		this.pnlFrictionImpulseColor.addMouseListener(this);
		this.pnlFrictionImpulseColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlFrictionImpulseColor.setPreferredSize(size);
		this.pnlFrictionImpulseColor.setBackground(ColorUtilities.convertColor(Preferences.getFrictionImpulseColor()));
		this.pnlFrictionImpulseColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		JLabel lblContactPairs = new JLabel(Resources.getString("panel.preferences.contactPairs"), Icons.INFO, JLabel.LEFT);
		lblContactPairs.setToolTipText(Resources.getString("panel.preferences.contactPairs.tooltip"));
		lblContactPairs.setMinimumSize(msize);
		this.chkContactPairs = new JCheckBox();
		this.chkContactPairs.setSelected(Preferences.isContactPairEnabled());
		this.pnlContactPairColor = new JPanel();
		this.pnlContactPairColor.addMouseListener(this);
		this.pnlContactPairColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pnlContactPairColor.setPreferredSize(size);
		this.pnlContactPairColor.setBackground(ColorUtilities.convertColor(Preferences.getContactPairColor()));
		this.pnlContactPairColor.setToolTipText(Resources.getString("panel.preferences.color.tooltip"));
		
		// layout
		GroupLayout layout;
		
		// create the general section
		
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
						.addComponent(lblAntiAliasing)
						.addComponent(lblVerticalSync)
						.addComponent(lblOriginLabel)
						.addComponent(lblScale)
						.addComponent(lblBounds)
						.addComponent(lblContactPairs)
						.addComponent(lblContacts)
						.addComponent(lblContactImpulses)
						.addComponent(lblFrictionImpulses))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkAntiAliasing)
						.addComponent(this.chkVerticalSync)
						.addComponent(this.chkOriginLabel)
						.addComponent(this.chkScale)
						.addComponent(this.chkBounds)
						.addComponent(this.chkContactPairs)
						.addComponent(this.chkContacts)
						.addComponent(this.chkContactImpulses)
						.addComponent(this.chkFrictionImpulses))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.pnlBoundsColor)
						.addComponent(this.pnlContactPairColor)
						.addComponent(this.pnlContactColor)
						.addComponent(this.pnlContactImpulseColor)
						.addComponent(this.pnlFrictionImpulseColor)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAntiAliasing)
						.addComponent(this.chkAntiAliasing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblVerticalSync)
						.addComponent(this.chkVerticalSync, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblOriginLabel)
						.addComponent(this.chkOriginLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblScale)
						.addComponent(this.chkScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBounds)
						.addComponent(this.chkBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBoundsColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblContactPairs)
						.addComponent(this.chkContactPairs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlContactPairColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblContacts)
						.addComponent(this.chkContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlContactColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblContactImpulses)
						.addComponent(this.chkContactImpulses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlContactImpulseColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblFrictionImpulses)
						.addComponent(this.chkFrictionImpulses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlFrictionImpulseColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// layout the body section
		
		JPanel pnlBody = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Resources.getString("panel.preferences.section.body"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlBody.setBorder(border);
		
		layout = new GroupLayout(pnlBody);
		pnlBody.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRandomColors)
						.addComponent(lblStenciling)
						.addComponent(lblBodyLabels)
						.addComponent(lblFixtureLabels)
						.addComponent(lblBodyCenter)
						.addComponent(lblSleepingBody)
						.addComponent(lblInActiveBody)
						.addComponent(lblSelectedColor)
						.addComponent(lblBodyAABBs)
						.addComponent(lblBodyNormals)
						.addComponent(lblBodyRotationDiscs)
						.addComponent(lblBodyVelocities))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkRandomColors)
						.addComponent(this.chkStenciling)
						.addComponent(this.chkBodyLabels)
						.addComponent(this.chkFixtureLabels)
						.addComponent(this.chkBodyCenter)
						.addComponent(this.chkSleepingBodyColor)
						.addComponent(this.chkInActiveBodyColor)
						.addComponent(this.chkBodyAABB)
						.addComponent(this.chkBodyNormal)
						.addComponent(this.chkBodyRotationDisc)
						.addComponent(this.chkBodyVelocity))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.pnlBodyCenterColor)
						.addComponent(this.pnlSleepingBodyColor)
						.addComponent(this.pnlInActiveBodyColor)
						.addComponent(this.pnlSelectedColor)
						.addComponent(this.pnlBodyAABBColor)
						.addComponent(this.pnlBodyNormalColor)
						.addComponent(this.pnlBodyRotationDiscColor)
						.addComponent(this.pnlBodyVelocityColor)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRandomColors)
						.addComponent(this.chkRandomColors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblStenciling)
						.addComponent(this.chkStenciling, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBodyLabels)
						.addComponent(this.chkBodyLabels, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblFixtureLabels)
						.addComponent(this.chkFixtureLabels, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBodyCenter)
						.addComponent(this.chkBodyCenter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBodyCenterColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblSleepingBody)
						.addComponent(this.chkSleepingBodyColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlSleepingBodyColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblInActiveBody)
						.addComponent(this.chkInActiveBodyColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlInActiveBodyColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblSelectedColor)
						.addComponent(this.pnlSelectedColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBodyAABBs)
						.addComponent(this.chkBodyAABB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBodyAABBColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBodyNormals)
						.addComponent(this.chkBodyNormal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBodyNormalColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBodyRotationDiscs)
						.addComponent(this.chkBodyRotationDisc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBodyRotationDiscColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblBodyVelocities)
						.addComponent(this.chkBodyVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnlBodyVelocityColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// layout the sections
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlBody));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlBody));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		JPanel panel = (JPanel)e.getSource();
		// save the border
		this.originalBorder = panel.getBorder();
		// create a new border to show highlight
		Border hoverBorder = BorderFactory.createLineBorder(Color.WHITE);
		// set the border
		panel.setBorder(hoverBorder);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		JPanel panel = (JPanel)e.getSource();
		// set the border back
		panel.setBorder(this.originalBorder);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		JPanel panel = (JPanel)e.getSource();
		Color color = ColorDialog.show(this, panel.getBackground(), false);
		if (color != null) {
			panel.setBackground(color);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	/**
	 * Sets the preferences the currently configured settings.
	 */
	public void setPreferences() {
		Preferences.setAntiAliasingEnabled(this.chkAntiAliasing.isSelected());
		Preferences.setBodyAsleepColor(ColorUtilities.convertColor(this.pnlSleepingBodyColor.getBackground()));
		Preferences.setBodyAsleepColorEnabled(this.chkSleepingBodyColor.isSelected());
		Preferences.setBodyCenterColor(ColorUtilities.convertColor(this.pnlBodyCenterColor.getBackground()));
		Preferences.setBodyCenterEnabled(this.chkBodyCenter.isSelected());
		Preferences.setBodyColorRandom(this.chkRandomColors.isSelected());
		Preferences.setBodyInActiveColor(ColorUtilities.convertColor(this.pnlInActiveBodyColor.getBackground()));
		Preferences.setBodyInActiveColorEnabled(this.chkInActiveBodyColor.isSelected());
		Preferences.setBodyLabeled(this.chkBodyLabels.isSelected());
		Preferences.setBodyStenciled(this.chkStenciling.isSelected());
		Preferences.setBoundsColor(ColorUtilities.convertColor(this.pnlBoundsColor.getBackground()));
		Preferences.setBoundsEnabled(this.chkBounds.isSelected());
		Preferences.setFixtureLabeled(this.chkFixtureLabels.isSelected());
		Preferences.setOriginLabeled(this.chkOriginLabel.isSelected());
		Preferences.setScaleEnabled(this.chkScale.isSelected());
		Preferences.setSelectedColor(ColorUtilities.convertColor(this.pnlSelectedColor.getBackground()));
		Preferences.setVerticalSyncEnabled(this.chkVerticalSync.isSelected());
		Preferences.setBodyAABBColor(ColorUtilities.convertColor(this.pnlBodyAABBColor.getBackground()));
		Preferences.setBodyAABBEnabled(this.chkBodyAABB.isSelected());
		Preferences.setBodyNormalColor(ColorUtilities.convertColor(this.pnlBodyNormalColor.getBackground()));
		Preferences.setBodyNormalEnabled(this.chkBodyNormal.isSelected());
		Preferences.setBodyRotationDiscColor(ColorUtilities.convertColor(this.pnlBodyRotationDiscColor.getBackground()));
		Preferences.setBodyRotationDiscEnabled(this.chkBodyRotationDisc.isSelected());
		Preferences.setBodyVelocityColor(ColorUtilities.convertColor(this.pnlBodyVelocityColor.getBackground()));
		Preferences.setBodyVelocityEnabled(this.chkBodyVelocity.isSelected());
		Preferences.setContactImpulseColor(ColorUtilities.convertColor(this.pnlContactImpulseColor.getBackground()));
		Preferences.setContactImpulseEnabled(this.chkContactImpulses.isSelected());
		Preferences.setContactPairColor(ColorUtilities.convertColor(this.pnlContactPairColor.getBackground()));
		Preferences.setContactPairEnabled(this.chkContactPairs.isSelected());
		Preferences.setContactPointColor(ColorUtilities.convertColor(this.pnlContactColor.getBackground()));
		Preferences.setContactPointEnabled(this.chkContacts.isSelected());
		Preferences.setFrictionImpulseColor(ColorUtilities.convertColor(this.pnlFrictionImpulseColor.getBackground()));
		Preferences.setFrictionImpulseEnabled(this.chkFrictionImpulses.isSelected());
	}
}
