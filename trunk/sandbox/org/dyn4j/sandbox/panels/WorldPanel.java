package org.dyn4j.sandbox.panels;

import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to configure a world object.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class WorldPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 8324974622465094443L;

	/** The list of available broadphase algorithms */
	private static final ComboItem[] BROADPHASE_ALGORITHMS = new ComboItem[] {
		new ComboItem("Sweep And Prune - Brute Force", SapBruteForce.class),
		new ComboItem("Sweep And Prune - Incremental", SapIncremental.class),
		new ComboItem("Sweep And Prune - Tree", SapTree.class),
		new ComboItem("Dynamic AABB Tree", DynamicAABBTree.class),
	};
	
	/** The list of available narrowphase algorithms */
	private static final ComboItem[] NARROWPHASE_ALGORITHMS = new ComboItem[] {
		new ComboItem("Separating Axis Theorem", Sat.class),
		new ComboItem("Gilbert-Johnson-Keerthi", Gjk.class)
	};
	
	/** The list of available manifold solver algorithms */
	private static final ComboItem[] MANIFOLD_SOLVER_ALGORITHMS = new ComboItem[] {
		new ComboItem("Clipping Manifold Solver", ClippingManifoldSolver.class)
	};
	
	/** The list of available time of impact algorithms */
	private static final ComboItem[] TIME_OF_IMPACT_ALGORITHMS = new ComboItem[] {
		new ComboItem("Conservative Advancement", ConservativeAdvancement.class)
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
		JLabel lblName = new JLabel("Name", Icons.INFO, JLabel.LEFT);
		lblName.setToolTipText("The name of the world.");
		this.txtName = new JTextField();
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		this.txtName.setText((String)world.getUserData());
		
		JLabel lblBroadphase = new JLabel("Broadphase", Icons.INFO, JLabel.LEFT);
		lblBroadphase.setToolTipText("The algorithm used to test if two bodies could be colliding.");
		
		JLabel lblNarrowphase = new JLabel("Narrowphase", Icons.INFO, JLabel.LEFT);
		lblNarrowphase.setToolTipText("The algorithm used to test if two bodies are colliding.");
		
		JLabel lblManifoldSolver = new JLabel("Manifold Solver", Icons.INFO, JLabel.LEFT);
		lblManifoldSolver.setToolTipText("The algorithm used to obtain a collision manifold from a collision.");
		
		JLabel lblToiSolver = new JLabel("Time Of Impact Solver", Icons.INFO, JLabel.LEFT);
		lblToiSolver.setToolTipText("The algorithm used to detect tunneling collisions.");
		
		this.cmbBroadphase = new JComboBox(BROADPHASE_ALGORITHMS);
		this.cmbBroadphase.setSelectedItem(getItem(world.getBroadphaseDetector()));
		this.cmbNarrowphase = new JComboBox(NARROWPHASE_ALGORITHMS);
		this.cmbNarrowphase.setSelectedItem(getItem(world.getNarrowphaseDetector()));
		this.cmbManifoldSolver = new JComboBox(MANIFOLD_SOLVER_ALGORITHMS);
		this.cmbManifoldSolver.setSelectedItem(getItem(world.getManifoldSolver()));
		this.cmbToiDetector = new JComboBox(TIME_OF_IMPACT_ALGORITHMS);
		this.cmbToiDetector.setSelectedItem(getItem(world.getTimeOfImpactDetector()));
		
		JLabel lblGravity = new JLabel("Gravity", Icons.INFO, JLabel.LEFT);
		lblGravity.setToolTipText("The gravity vector for the world.");
		
		this.txtGravityX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtGravityY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtGravityX.addFocusListener(new SelectTextFocusListener(this.txtGravityX));
		this.txtGravityY.addFocusListener(new SelectTextFocusListener(this.txtGravityY));
		this.txtGravityX.setColumns(7);
		this.txtGravityY.setColumns(7);
		
		this.txtGravityX.setValue(world.getGravity().x);
		this.txtGravityY.setValue(world.getGravity().y);
		
		JLabel lblX = new JLabel("x");
		JLabel lblY = new JLabel("y");
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblName)
						.addComponent(lblBroadphase)
						.addComponent(lblNarrowphase)
						.addComponent(lblManifoldSolver)
						.addComponent(lblToiSolver)
						.addComponent(lblGravity))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.cmbBroadphase)
						.addComponent(this.cmbNarrowphase)
						.addComponent(this.cmbManifoldSolver)
						.addComponent(this.cmbToiDetector)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtGravityX)
								.addComponent(lblX)
								.addComponent(this.txtGravityY)
								.addComponent(lblY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBroadphase)
						.addComponent(this.cmbBroadphase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblNarrowphase)
						.addComponent(this.cmbNarrowphase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblManifoldSolver)
						.addComponent(this.cmbManifoldSolver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblToiSolver)
						.addComponent(this.cmbToiDetector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblGravity)
						.addComponent(this.txtGravityX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtGravityY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY)));
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
