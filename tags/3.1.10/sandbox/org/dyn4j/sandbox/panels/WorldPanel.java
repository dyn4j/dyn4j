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

import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.SapBruteForce;
import org.dyn4j.collision.broadphase.SapIncremental;
import org.dyn4j.collision.broadphase.SapTree;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.controls.ComboItem;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to configure a world object.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class WorldPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 8324974622465094443L;

	/** The list of available broadphase algorithms */
	private static final ComboItem[] BROADPHASE_ALGORITHMS = new ComboItem[] {
		new ComboItem(Messages.getString("panel.world.broad.algorithm.sapBruteForce"), SapBruteForce.class),
		new ComboItem(Messages.getString("panel.world.broad.algorithm.sapIncremental"), SapIncremental.class),
		new ComboItem(Messages.getString("panel.world.broad.algorithm.sapTree"), SapTree.class),
		new ComboItem(Messages.getString("panel.world.broad.algorithm.dynamicAABBTree"), DynamicAABBTree.class),
	};
	
	/** The list of available narrowphase algorithms */
	private static final ComboItem[] NARROWPHASE_ALGORITHMS = new ComboItem[] {
		new ComboItem(Messages.getString("panel.world.narrow.algorithm.sat"), Sat.class),
		new ComboItem(Messages.getString("panel.world.narrow.algorithm.gjk"), Gjk.class)
	};
	
	/** The list of available manifold solver algorithms */
	private static final ComboItem[] MANIFOLD_SOLVER_ALGORITHMS = new ComboItem[] {
		new ComboItem(Messages.getString("panel.world.manifold.algorithm.clip"), ClippingManifoldSolver.class)
	};
	
	/** The list of available time of impact algorithms */
	private static final ComboItem[] TIME_OF_IMPACT_ALGORITHMS = new ComboItem[] {
		new ComboItem(Messages.getString("panel.world.toi.algorithm.conservativeAdvancement"), ConservativeAdvancement.class)
	};
	
	/** The world name */
	private JTextField txtName;
	
	/** The broadphase combo box */
	private JComboBox cmbBroadphase;
	
	/** The narrowphase combo box */
	private JComboBox cmbNarrowphase;
	
	/** The manifold solver combo box */
	private JComboBox cmbManifoldSolver;
	
	/** The time of impact combo box */
	private JComboBox cmbToiDetector;
	
	/** The gravity x value text box */
	private JFormattedTextField txtGravityX;
	
	/** The gravity y value text box */
	private JFormattedTextField txtGravityY;
	
	/**
	 * Full constructor.
	 * @param world the current world object
	 */
	public WorldPanel(World world) {
		JLabel lblName = new JLabel(Messages.getString("panel.world.name"), Icons.INFO, JLabel.LEFT);
		lblName.setToolTipText(Messages.getString("panel.world.name.tooltip"));
		this.txtName = new JTextField();
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		this.txtName.setText((String)world.getUserData());
		
		JLabel lblBroadphase = new JLabel(Messages.getString("panel.world.broad.algorithm"), Icons.INFO, JLabel.LEFT);
		lblBroadphase.setToolTipText(Messages.getString("panel.world.broad.algorithm.tooltip"));
		
		JLabel lblNarrowphase = new JLabel(Messages.getString("panel.world.narrow.algorithm"), Icons.INFO, JLabel.LEFT);
		lblNarrowphase.setToolTipText(Messages.getString("panel.world.narrow.algorithm.tooltip"));
		
		JLabel lblManifoldSolver = new JLabel(Messages.getString("panel.world.manifold.algorithm"), Icons.INFO, JLabel.LEFT);
		lblManifoldSolver.setToolTipText(Messages.getString("panel.world.manifold.algorithm.tooltip"));
		
		JLabel lblToiSolver = new JLabel(Messages.getString("panel.world.toi.algorithm"), Icons.INFO, JLabel.LEFT);
		lblToiSolver.setToolTipText(Messages.getString("panel.world.toi.algorithm.tooltip"));
		
		this.cmbBroadphase = new JComboBox(BROADPHASE_ALGORITHMS);
		this.cmbBroadphase.setSelectedItem(getItem(world.getBroadphaseDetector()));
		this.cmbNarrowphase = new JComboBox(NARROWPHASE_ALGORITHMS);
		this.cmbNarrowphase.setSelectedItem(getItem(world.getNarrowphaseDetector()));
		this.cmbManifoldSolver = new JComboBox(MANIFOLD_SOLVER_ALGORITHMS);
		this.cmbManifoldSolver.setSelectedItem(getItem(world.getManifoldSolver()));
		this.cmbToiDetector = new JComboBox(TIME_OF_IMPACT_ALGORITHMS);
		this.cmbToiDetector.setSelectedItem(getItem(world.getTimeOfImpactDetector()));
		
		JLabel lblGravity = new JLabel(Messages.getString("panel.world.gravity"), Icons.INFO, JLabel.LEFT);
		lblGravity.setToolTipText(Messages.getString("panel.world.gravity.tooltip"));
		
		this.txtGravityX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.world.gravity.format")));
		this.txtGravityY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.world.gravity.format")));
		this.txtGravityX.addFocusListener(new SelectTextFocusListener(this.txtGravityX));
		this.txtGravityY.addFocusListener(new SelectTextFocusListener(this.txtGravityY));
		this.txtGravityX.setColumns(7);
		this.txtGravityY.setColumns(7);
		
		this.txtGravityX.setValue(world.getGravity().x);
		this.txtGravityY.setValue(world.getGravity().y);
		
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		GroupLayout layout;
		
		JPanel pnlWorld = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.world.section.world"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlWorld.setBorder(border);
		layout = new GroupLayout(pnlWorld);
		pnlWorld.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblName)
						.addComponent(lblGravity))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtGravityX)
								.addComponent(lblX)
								.addComponent(this.txtGravityY)
								.addComponent(lblY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblGravity)
						.addComponent(this.txtGravityX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtGravityY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY)));
		
		JPanel pnlAlgorithms = new JPanel();
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("panel.world.section.algorithms"));
		border.setTitlePosition(TitledBorder.TOP);
		pnlAlgorithms.setBorder(border);
		layout = new GroupLayout(pnlAlgorithms);
		pnlAlgorithms.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBroadphase)
						.addComponent(lblNarrowphase)
						.addComponent(lblManifoldSolver)
						.addComponent(lblToiSolver))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbBroadphase)
						.addComponent(this.cmbNarrowphase)
						.addComponent(this.cmbManifoldSolver)
						.addComponent(this.cmbToiDetector)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBroadphase)
						.addComponent(this.cmbBroadphase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblNarrowphase)
						.addComponent(this.cmbNarrowphase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblManifoldSolver)
						.addComponent(this.cmbManifoldSolver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblToiSolver)
						.addComponent(this.cmbToiDetector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlWorld)
				.addComponent(pnlAlgorithms));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlWorld)
				.addComponent(pnlAlgorithms));
	}
	
	/**
	 * Sets the configured properties on the given world object.
	 * @param world the world object to modify
	 */
	public void setWorld(World world) {
		world.setUserData(this.txtName.getText());
		// set the broadphase algorithm
		ComboItem item = (ComboItem)this.cmbBroadphase.getSelectedItem();
		Class<?> clazz = (Class<?>)item.getValue();
		if (clazz == SapBruteForce.class) {
			world.setBroadphaseDetector(new SapBruteForce<Body>());
		} else if (clazz == SapIncremental.class) {
			world.setBroadphaseDetector(new SapIncremental<Body>());
		} else if (clazz == SapTree.class) {
			world.setBroadphaseDetector(new SapTree<Body>());
		} else if (clazz == DynamicAABBTree.class) {
			world.setBroadphaseDetector(new DynamicAABBTree<Body>());
		}
		
		// set the narrowphase algorithm
		item = (ComboItem)this.cmbNarrowphase.getSelectedItem();
		clazz = (Class<?>)item.getValue();
		if (clazz == Sat.class) {
			world.setNarrowphaseDetector(new Sat());
		} else if (clazz == Gjk.class) {
			world.setNarrowphaseDetector(new Gjk());
		}
		
		// the other algorithms cannot change since there is only one implementation
		// for each type (manifold solver & toi solver)
		
		// set the gravity vector
		Vector2 g = new Vector2(
				ControlUtilities.getDoubleValue(this.txtGravityX),
				ControlUtilities.getDoubleValue(this.txtGravityY));
		world.setGravity(g);
	}
	
	/**
	 * Returns the item in combo box for the given broadphase detector.
	 * @param broadphaseDetector the broadphase detector
	 * @return ComboItem
	 */
	private static final ComboItem getItem(BroadphaseDetector<?> broadphaseDetector) {
		for (ComboItem item : BROADPHASE_ALGORITHMS) {
			Class<?> clazz = (Class<?>)item.getValue();
			if (clazz == broadphaseDetector.getClass()) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Returns the item in combo box for the given narrowphase detector.
	 * @param narrowphaseDetector the narrowphase detector
	 * @return ComboItem
	 */
	private static final ComboItem getItem(NarrowphaseDetector narrowphaseDetector) {
		for (ComboItem item : NARROWPHASE_ALGORITHMS) {
			Class<?> clazz = (Class<?>)item.getValue();
			if (clazz == narrowphaseDetector.getClass()) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Returns the item in combo box for the given manifold solver.
	 * @param manifoldSolver the manifold solver
	 * @return ComboItem
	 */
	private static final ComboItem getItem(ManifoldSolver manifoldSolver) {
		for (ComboItem item : MANIFOLD_SOLVER_ALGORITHMS) {
			Class<?> clazz = (Class<?>)item.getValue();
			if (clazz == manifoldSolver.getClass()) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Returns the item in combo box for the given time of impact detector.
	 * @param timeOfImpactDetector the time of impact detector
	 * @return ComboItem
	 */
	private static final ComboItem getItem(TimeOfImpactDetector timeOfImpactDetector) {
		for (ComboItem item : TIME_OF_IMPACT_ALGORITHMS) {
			Class<?> clazz = (Class<?>)item.getValue();
			if (clazz == timeOfImpactDetector.getClass()) {
				return item;
			}
		}
		return null;
	}
}
