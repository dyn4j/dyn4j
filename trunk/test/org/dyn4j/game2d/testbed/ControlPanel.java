/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.testbed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;
import javax.swing.AbstractSpinnerModel;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.dynamics.Settings;

/**
 * The JFrame that controls the TestBed.
 * @author William Bittle
 * @version 1.2.0
 * @since 1.0.0
 */
public class ControlPanel extends JFrame {
	/** the version id */
	private static final long serialVersionUID = -461371622259288105L;
	
	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(ControlPanel.class.getName());
	
	/** Resource bundle containing the tests to load */
	private static ResourceBundle TESTS_BUNDLE = ResourceBundle.getBundle("org.dyn4j.game2d.testbed.tests");
	
	/** The controls for all tests */
	private static final String[][] CONTROLS = new String[][] {
		{"Esc or e", "Exits simulation"},
		{"Pause/Break or p", "Pauses simulation"},
		{"+ or Mouse Wheel Up", "Zooms in"},
		{"- or Mouse Wheel Down", "Zooms out"},
		{"Left", "Pans left"},
		{"Right", "Pans right"},
		{"Up", "Pans up"},
		{"Down", "Pans down"},
		{"Home or h", "Centers the camera"},
		{"c", "Opens the Test Bed Control Panel"},
		{"r", "Resets the current test"},
		{"Space", "Toggles step mode"},
		{"s", "Performs 1 step when in step mode"},
		{"Left Mouse Button", "Click and hold to create a MouseJoint with a shape."},
		{"Right Mouse Button", "Click and hold to select a shape."},
		{"Move Mouse", "Move to translate the selected shape."},
		{"z", "Hold to rotate the selected shape."},
		{"o", "Outputs all the bodies current state to std out."},
		{"b", "Launches a bomb from the left side."},
		{"i", "Increases the metrics update rate."},
		{"d", "Decreases the metrics update rate."}
		};
	
	/** Map of available test to run */
	private List<Test> tests;
	
	/** The current test */
	private Test test;
	
	/** The panel containing the test controls */
	private JPanel pnlTestControls = null;
	
	/** The panel for the controls tab */
	private JPanel pnlControls = null;
	
	/** The combo box for selecting a test */
	private JComboBox cmbTests = null;
	
	/** The description field for the selected test */
	private JTextPane panTestDescription = null;

	/** The combo box for selecting a broad-phase CD algorithm */
	private JComboBox cmbBPCDAlgo = null;
		
	/** The combo box for selecting a narrow-phase CD algorithm */
	private JComboBox cmbNPCDAlgo = null;

	/** The combo box for selecting a manifold solving algorithm */
	private JComboBox cmbMSAlgo = null;
	
	/** The combo box for selecting a continuous collision detection algorithm */
	private JComboBox cmbTOIAlgo = null;
	
	/** The selected broad-phase collision detection algorithm */
	private String selectedBPCDAlgo = "Sap";
	
	/** The selected narrow-phase collision detection algorithm */
	private String selectedNPCDAlgo = "Gjk";
	
	/** The selected manifold solving algorithm */
	private String selectedMSAlgo = "Clip";
	
	/** The selected manifold solving algorithm */
	private String selectedTOIAlgo = "CA";
	
	/** The image icon to show for help */
	private Icon helpIcon = null;
	
	/**
	 * Default constructor.
	 * @throws ConfigurationException if the tests.properties is missing or not configured
	 */
	public ControlPanel() throws ConfigurationException {
		super("Test Bed Control Panel");
		
		// load the help icon
		this.helpIcon = new ImageIcon(this.getClass().getResource("/help.gif"), "Hover for help");
		
		// initialize the list of tests
		this.tests = new ArrayList<Test>();
		Test defaultTest = null;
		
		// get the default test
		String defaultTestKey = TESTS_BUNDLE.getString("default.test");
		// read in all the tests
		Enumeration<String> keys = TESTS_BUNDLE.getKeys();
		
		// loop through the keys
		while (keys.hasMoreElements()) {
			// get the key
			String key = keys.nextElement();
			// skip keys with "."s in them
			if (key.contains(".")) continue;
			// get the value (the test class name)
			String className = TESTS_BUNDLE.getString(key);
			try {
				// attempt to load the class
				Class<?> clazz = Class.forName(className);
				// attempt to create an instance of it
				Test test = (Test) clazz.newInstance();
				// initialize the test
				test.initialize();
				// add it to the test map
				this.tests.add(test);
				// is this test the default test?
				if (defaultTestKey.equals(key)) {
					defaultTest = test;
				}
			} catch (ClassNotFoundException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (ClassCastException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (IllegalAccessException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (InstantiationException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			}
		}
		// make sure the map has at least one test
		if (this.tests.size() == 0) {
			throw new ConfigurationException("At least one test must be configured in the tests.properties file.");
		}
		
		// sort the tests
		Collections.sort(this.tests);
		
		// verify the test was found
		if (defaultTest != null) {
			this.setTest(defaultTest);
		} else {
			// otherwise randomize the initial test
			int size = this.tests.size();
			int index = (int) Math.floor(Math.random() * (size - 1));
			this.setTest(this.tests.get(index));
		}
		
		// create the GUI
		this.createGUI();
	}
	
	/**
	 * Sets the current test.
	 * @param test the test to run
	 */
	private void setTest(Test test) {
		// set the new test
		this.test = test;
	}
	
	/**
	 * Returns the current {@link Test}.
	 * @return {@link Test} the current {@link Test}
	 */
	public Test getTest() {
		return this.test;
	}
	
	/**
	 * Returns the current {@link NarrowphaseDetector} name.
	 * @return String the current {@link NarrowphaseDetector} name
	 */
	public String getNPCDAlgorithm() {
		return this.selectedNPCDAlgo;
	}
	
	/**
	 * Returns the current {@link BroadphaseDetector} name.
	 * @return String the current {@link BroadphaseDetector} name
	 */
	public String getBPCDAlgorithm() {
		return this.selectedBPCDAlgo;
	}
	
	/**
	 * Returns the current {@link ManifoldSolver} name.
	 * @return String the current {@link ManifoldSolver} name
	 */
	public String getMSAlgorithm() {
		return this.selectedMSAlgo;
	}
	
	/**
	 * Returns the current {@link TimeOfImpactDetector} name.
	 * @return String the current {@link TimeOfImpactDetector} name
	 */
	public String getTOIAlgorithm() {
		return this.selectedTOIAlgo;
	}
	
	/**
	 * Creates the GUI for all configuration.
	 */
	private void createGUI() {
		// create the frame
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// create a tabbed pane
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(new EmptyBorder(5, 5, 5, 5));

		// create the container for the control listing tab
		this.pnlControls = this.createControlsPanel();
		// create a tab from the panel
		tabs.addTab(" Controls ", null, this.pnlControls, "View the list of controls.");
		
		// create a container for the tests selection tab
		JPanel pnlTest = this.createSelectTestPanel();
		// create the tab from the panel
		tabs.addTab(" Tests ", null, pnlTest, "Select the test to run.");
		
		// create a container for the tests selection tab
		JPanel pnlDraw = this.createDrawingOptionsPanel();
		// create the tab from the panel
		tabs.addTab(" Drawing Options ", null, pnlDraw, "Select drawing options.");

		JPanel pnlSettings = this.createSimulationSettingsPanel();
		// create a tab from the panel
		tabs.addTab(" Simulation Settings ", null, pnlSettings, "Set simulation settings.");

		// add the tabs to the frame
		this.add(tabs, BorderLayout.CENTER);
		
		// set the preferred width
		this.setPreferredSize(new Dimension(450, 710));
		
		// pack the layout
		this.pack();
	}
	
	/**
	 * Creates the panel for the controls tab.
	 * @return JPanel the controls tab panel
	 */
	private JPanel createControlsPanel() {
		// create the container for the control listing tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// controls group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlControls = new JPanel();
		pnlControls.setBorder(new TitledBorder("Controls"));
		pnlControls.setLayout(new GridBagLayout());
		
		int size = CONTROLS.length;
		int row = 0;
		// create all the labels for the standard controls
		for (String[] control : CONTROLS) {
			// create the labels
			JLabel lblKey = new JLabel(control[0]); // key
			JLabel lblDes = new JLabel(control[1]); // description
			
			// add them to the panel
			pnlControls.add(lblKey, new GridBagConstraints(
					0, row, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			pnlControls.add(lblDes, new GridBagConstraints(
					1, row, 1, 1, 1, (row + 1 == size ? 1 : 0), GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			row++;
		}
		
		panel.add(pnlControls);
		
		//////////////////////////////////////////////////
		// test controls group
		//////////////////////////////////////////////////
		// create the panel
		this.pnlTestControls = new JPanel();
		this.pnlTestControls.setBorder(new TitledBorder("Test Specific Controls"));
		this.pnlTestControls.setLayout(new GridBagLayout());
		
		// check for controls
		if (this.test.getControls().length > 0) {
			// add the controls to it
			this.addTestControls(this.pnlTestControls, this.test.getControls());
		}
		// add it to the panel
		panel.add(this.pnlTestControls);

		// return the panel
		return panel;
	}
	
	/**
	 * Creates the panel where the user selects the current test.
	 * @return JPanel where the user selects the current test
	 */
	private JPanel createSelectTestPanel() {
		// create a container for the tests selection tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// test group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlTest = new JPanel();
		pnlTest.setBorder(new TitledBorder("Test Selection"));
		pnlTest.setLayout(new GridBagLayout());

		JLabel lblTest = new JLabel("Tests", this.helpIcon, JLabel.LEFT);
		lblTest.setToolTipText("After selecting a test and clicking Run, check the controls tab for any test specific controls.");
		pnlTest.add(lblTest, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create a combo box for the test selection
		cmbTests = new JComboBox(this.tests.toArray());
		// set the selected item
		cmbTests.setSelectedItem(this.test);
		// add it to the panel
		pnlTest.add(cmbTests, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.VERTICAL, insets, 0, 0));
		// create a button to save the setting
		JButton btnT = new JButton("Run");
		// add a listener to it to save the setting
		btnT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected test
				setTest((Test) cmbTests.getSelectedItem());
				// remove all the controls
				pnlTestControls.removeAll();
				// update the test specific controls panel
				if (test.getControls().length > 0) {
					// add all the new ones
					addTestControls(pnlTestControls, test.getControls());
				}
			}
		});
		// add the button to the panel
		pnlTest.add(btnT, new GridBagConstraints(
				2, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JLabel lblDesc = new JLabel("Test description:");
		pnlTest.add(lblDesc, new GridBagConstraints(
				0, 1, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create a description field
		panTestDescription = new JTextPane();
		panTestDescription.setBorder(new CompoundBorder(new LineBorder(Color.black), border));
		panTestDescription.setEditable(false);
		panTestDescription.setText(test.getDescription());
		// once the label is created set the action listener for the combo box
		cmbTests.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Test test = ((Test) ((JComboBox) e.getSource()).getSelectedItem());
				// set the description
				panTestDescription.setText(test.getDescription());
			}
		});
		// add the label to the panel
		pnlTest.add(panTestDescription, new GridBagConstraints(
				0, 2, 3, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.BOTH, insets, 0, 0));
		
		panel.add(pnlTest);
		
		return panel;
	}
	
	/**
	 * Creates the panel for the drawing options tab.
	 * @return JPanel the panel for the drawing options tab
	 */
	private JPanel createDrawingOptionsPanel() {
		// create a container for the drawing tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		// get the drawing settings instance
		Draw draw = Draw.getInstance();
		
		//////////////////////////////////////////////////
		// drawing options group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlDraw = new JPanel();
		pnlDraw.setBorder(new TitledBorder("Drawing Options"));
		pnlDraw.setLayout(new GridBagLayout());

		// draw centers of mass
		JLabel lblCenter = new JLabel("Center of Mass");
		pnlDraw.add(lblCenter, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkCenter = new JCheckBox();
		chkCenter.setSelected(draw.drawCenter());
		chkCenter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawCenter(!draw.drawCenter());
			}
		});
		pnlDraw.add(chkCenter, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw velocity vectors
		JLabel lblVelocity = new JLabel("Velocity Vector");
		pnlDraw.add(lblVelocity, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkVelocity = new JCheckBox();
		chkVelocity.setSelected(draw.drawVelocityVectors());
		chkVelocity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawVelocityVectors(!draw.drawVelocityVectors());
			}
		});
		pnlDraw.add(chkVelocity, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact points
		JLabel lblContacts = new JLabel("Contact Points");
		pnlDraw.add(lblContacts, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContacts = new JCheckBox();
		chkContacts.setSelected(draw.drawContacts());
		chkContacts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContacts(!draw.drawContacts());
			}
		});
		pnlDraw.add(chkContacts, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact forces
		JLabel lblContactForces = new JLabel("Contact Impulses");
		pnlDraw.add(lblContactForces, new GridBagConstraints(
				0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContactForces = new JCheckBox();
		chkContactForces.setSelected(draw.drawContactForces());
		chkContactForces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContactForces(!draw.drawContactForces());
			}
		});
		pnlDraw.add(chkContactForces, new GridBagConstraints(
				1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw friction forces
		JLabel lblFrictionForces = new JLabel("Friction Impulses");
		pnlDraw.add(lblFrictionForces, new GridBagConstraints(
				0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkFrictionForces = new JCheckBox();
		chkFrictionForces.setSelected(draw.drawFrictionForces());
		chkFrictionForces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawFrictionForces(!draw.drawFrictionForces());
			}
		});
		pnlDraw.add(chkFrictionForces, new GridBagConstraints(
				1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact pairs
		JLabel lblContactPairs = new JLabel("Contact Pairs");
		pnlDraw.add(lblContactPairs, new GridBagConstraints(
				0, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContactPairs = new JCheckBox();
		chkContactPairs.setSelected(draw.drawContactPairs());
		chkContactPairs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContactPairs(!draw.drawContactPairs());
			}
		});
		pnlDraw.add(chkContactPairs, new GridBagConstraints(
				1, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw joints
		JLabel lblJoints = new JLabel("Joints");
		pnlDraw.add(lblJoints, new GridBagConstraints(
				0, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkJoints = new JCheckBox();
		chkJoints.setSelected(draw.drawJoints());
		chkJoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawJoints(!draw.drawJoints());
			}
		});
		pnlDraw.add(chkJoints, new GridBagConstraints(
				1, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw world bounds
		JLabel lblBounds = new JLabel("World Bounds");
		pnlDraw.add(lblBounds, new GridBagConstraints(
				0, 7, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkBounds = new JCheckBox();
		chkBounds.setSelected(draw.drawBounds());
		chkBounds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawBounds(!draw.drawBounds());
			}
		});
		pnlDraw.add(chkBounds, new GridBagConstraints(
				1, 7, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw text
		JLabel lblText = new JLabel("Information Panel");
		pnlDraw.add(lblText, new GridBagConstraints(
				0, 8, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkText = new JCheckBox();
		chkText.setSelected(draw.drawPanel());
		chkText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawPanel(!draw.drawPanel());
			}
		});
		pnlDraw.add(chkText, new GridBagConstraints(
				1, 8, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));

		// fill shapes?
		JLabel lblFill = new JLabel("Shape Fill");
		pnlDraw.add(lblFill, new GridBagConstraints(
				0, 9, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkFill = new JCheckBox();
		chkFill.setSelected(draw.drawFill());
		chkFill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawFill(!draw.drawFill());
			}
		});
		pnlDraw.add(chkFill, new GridBagConstraints(
				1, 9, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw outlines?
		JLabel lblOutline = new JLabel("Shape Outlines");
		pnlDraw.add(lblOutline, new GridBagConstraints(
				0, 10, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkOutline = new JCheckBox();
		chkOutline.setSelected(draw.drawOutline());
		chkOutline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawOutline(!draw.drawOutline());
			}
		});
		pnlDraw.add(chkOutline, new GridBagConstraints(
				1, 10, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw normals?
		JLabel lblNormals = new JLabel("Edge Normals");
		pnlDraw.add(lblNormals, new GridBagConstraints(
				0, 11, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkNormals = new JCheckBox();
		chkNormals.setSelected(draw.drawNormals());
		chkNormals.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawNormals(!draw.drawNormals());
			}
		});
		pnlDraw.add(chkNormals, new GridBagConstraints(
				1, 11, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		panel.add(pnlDraw);
		
		return panel;
	}
	
	/**
	 * Creates the panel for the simulation settings tab.
	 * @return JPanel the simulation settings panel
	 */
	private JPanel createSimulationSettingsPanel() {
		// get the current settings
		Settings settings = Settings.getInstance();
		
		// create a container for the settings tab
		JPanel panel = new JPanel();
		// create a border
		Border border = new EmptyBorder(5, 5, 5, 5);
		
		// set the layout
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);
		
		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// general group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(new TitledBorder("General Settings"));
		// set the layout
		pnlGeneral.setLayout(new GridBagLayout());
		
		// broad-phase
		JLabel lblBPCDAlgo = new JLabel("Broad-phase Collision Detection Algorithm", this.helpIcon, JLabel.LEFT);
		lblBPCDAlgo.setToolTipText("Specifies the algorithm used to handle broad-phase collision detection.");
		pnlGeneral.add(lblBPCDAlgo, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbBPCDAlgo = new JComboBox(new String[] {"Sap"});
		cmbBPCDAlgo.setSelectedItem(this.selectedBPCDAlgo);
		// add it to the panel
		pnlGeneral.add(cmbBPCDAlgo, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnBPCDAlgo = new JButton("Set");
		btnBPCDAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedBPCDAlgo = (String) cmbBPCDAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnBPCDAlgo, new GridBagConstraints(
				2, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// narrow-phase
		JLabel lblCDAlgo = new JLabel("Narrow-phase Collision Detection Algorithm", this.helpIcon, JLabel.LEFT);
		lblCDAlgo.setToolTipText("Specifies the algorithm used to handle narrow-phase collision detection.");
		pnlGeneral.add(lblCDAlgo, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbNPCDAlgo = new JComboBox(new String[] {"Gjk", "Sat"});
		cmbNPCDAlgo.setSelectedItem(this.selectedNPCDAlgo);
		// add it to the panel
		pnlGeneral.add(cmbNPCDAlgo, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnCDAlgo = new JButton("Set");
		btnCDAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedNPCDAlgo = (String) cmbNPCDAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnCDAlgo, new GridBagConstraints(
				2, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// manifold
		JLabel lblMSAlgo = new JLabel("Manifold Solving Algorithm", this.helpIcon, JLabel.LEFT);
		lblMSAlgo.setToolTipText("Specifies the algorithm used to create collision manifolds.");
		pnlGeneral.add(lblMSAlgo, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbMSAlgo = new JComboBox(new String[] {"Clip"});
		cmbMSAlgo.setSelectedItem(this.selectedMSAlgo);
		// add it to the panel
		pnlGeneral.add(cmbMSAlgo, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnMSAlgo = new JButton("Set");
		btnMSAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedMSAlgo = (String) cmbMSAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnMSAlgo, new GridBagConstraints(
				2, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// continuous collision detection
		JLabel lblTOIAlgo = new JLabel("Time Of Impact Detection Algorithm", this.helpIcon, JLabel.LEFT);
		lblTOIAlgo.setToolTipText("Specifies the time of impact algorithm used for continuous collision detection.");
		pnlGeneral.add(lblTOIAlgo, new GridBagConstraints(
				0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down
		cmbTOIAlgo = new JComboBox(new String[] {"CA"});
		cmbTOIAlgo.setSelectedItem(this.selectedTOIAlgo);
		// add it to the panel
		pnlGeneral.add(cmbTOIAlgo, new GridBagConstraints(
				1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnTOIAlgo = new JButton("Set");
		btnTOIAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedTOIAlgo = (String) cmbTOIAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnTOIAlgo, new GridBagConstraints(
				2, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// step frequency
		JLabel lblStep = new JLabel("Step Fequency", this.helpIcon, JLabel.LEFT);
		lblStep.setToolTipText("Specifies the number of updates the dynamics engine will attempt to perform per second.");
		pnlGeneral.add(lblStep, new GridBagConstraints(
				0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnStep = new JSpinner(new SpinnerNumberModel(1.0 / settings.getStepFrequency(), 30.0, 999.0, 5.0));
		spnStep.setEditor(new JSpinner.NumberEditor(spnStep, "0"));
		((JSpinner.DefaultEditor)spnStep.getEditor()).getTextField().setColumns(3);
		spnStep.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double hz = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setStepFrequency(hz);
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnStep, new GridBagConstraints(
				1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblStepUnit = new JLabel("<html>second<sup>-1</sup></html>");
		pnlGeneral.add(lblStepUnit, new GridBagConstraints(
				2, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// max velocity
		JLabel lblMaxV = new JLabel("Maximum Translation", this.helpIcon, JLabel.LEFT);
		lblMaxV.setToolTipText("Specifies the maximum translation a body can have in one time step.");
		pnlGeneral.add(lblMaxV, new GridBagConstraints(
				0, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnMaxV = new JSpinner(new SpinnerNumberModel(settings.getMaxTranslation(), 0.0, 10.0, 0.1));
		spnMaxV.setEditor(new JSpinner.NumberEditor(spnMaxV, "0.0"));
		((JSpinner.DefaultEditor)spnMaxV.getEditor()).getTextField().setColumns(4);
		spnMaxV.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double t = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxTranslation(t);
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnMaxV, new GridBagConstraints(
				1, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblMaxVUnit = new JLabel("meters");
		pnlGeneral.add(lblMaxVUnit, new GridBagConstraints(
				2, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// max angular velocity
		JLabel lblMaxAv = new JLabel("Maximum Rotation", this.helpIcon, JLabel.LEFT);
		lblMaxAv.setToolTipText("Specifies the maximum rotation a body can have in one time step.");
		pnlGeneral.add(lblMaxAv, new GridBagConstraints(
				0, 6, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnMaxAv = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getMaxRotation()), 0.0, 3600.0, 1.0));
		spnMaxAv.setEditor(new JSpinner.NumberEditor(spnMaxAv, "0.0"));
		((JSpinner.DefaultEditor)spnMaxAv.getEditor()).getTextField().setColumns(4);
		spnMaxAv.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double r = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxRotation(Math.toRadians(r));
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnMaxAv, new GridBagConstraints(
				1, 6, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblMaxAvUnit = new JLabel("degrees");
		pnlGeneral.add(lblMaxAvUnit, new GridBagConstraints(
				2, 6, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the panel to the overall panel
		panel.add(pnlGeneral);
		
		//////////////////////////////////////////////////
		// sleep group
		//////////////////////////////////////////////////
		
		// create the sleep panel
		JPanel pnlSleep = new JPanel();
		// create the sleep panel border
		pnlSleep.setBorder(new TitledBorder("Sleep Settings"));
		// set the layout
		pnlSleep.setLayout(new GridBagLayout());
		
		JLabel lblAllowSleep = new JLabel("Allow bodies to sleep?", this.helpIcon, JLabel.LEFT);
		lblAllowSleep.setToolTipText("Sleeping allows the physics system to save cycles by avoiding unnecessary work for bodies who are not in motion.");
		pnlSleep.add(lblAllowSleep, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JCheckBox chkAllowSleep = new JCheckBox();
		chkAllowSleep.setSelected(settings.isAutoSleepingEnabled());
		chkAllowSleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings settings = Settings.getInstance();
				settings.setAutoSleepingEnabled(!settings.isAutoSleepingEnabled());
			}
		});
		// add the checkbox to the panel
		pnlSleep.add(chkAllowSleep, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep time
		JLabel lblSleepTime = new JLabel("Sleep time", this.helpIcon, JLabel.LEFT);
		lblSleepTime.setToolTipText("Specifies the required amount of time a body must be at rest before being put to sleep.");
		// add the label to the layout
		pnlSleep.add(lblSleepTime, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepTime = new JSpinner(new SpinnerNumberModel(settings.getSleepTime(), 0.0, 9.9, 0.1));
		spnSleepTime.setEditor(new JSpinner.NumberEditor(spnSleepTime, "0.0"));
		((JSpinner.DefaultEditor)spnSleepTime.getEditor()).getTextField().setColumns(3);
		spnSleepTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double time = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepTime(time);
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepTime, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepTimeUnit = new JLabel("seconds");
		pnlSleep.add(lblSleepTimeUnit, new GridBagConstraints(
				2, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep max velocity
		JLabel lblSleepMaxV = new JLabel("Maximum velocity", this.helpIcon, JLabel.LEFT);
		lblSleepMaxV.setToolTipText("Specifies the maximum velocity used to determine whether a body is at rest or not.");
		// add the label to the layout
		pnlSleep.add(lblSleepMaxV, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepMaxV = new JSpinner(new SpinnerNumberModel(settings.getSleepVelocity(), 0.0, 9.99, 0.01));
		spnSleepMaxV.setEditor(new JSpinner.NumberEditor(spnSleepMaxV, "0.00"));
		((JSpinner.DefaultEditor)spnSleepMaxV.getEditor()).getTextField().setColumns(5);
		spnSleepMaxV.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepVelocity(v);
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepMaxV, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepMaxVUnit = new JLabel("meters / second");
		pnlSleep.add(lblSleepMaxVUnit, new GridBagConstraints(
				2, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep max av
		JLabel lblSleepMaxAv = new JLabel("Maximum angular velocity", this.helpIcon, JLabel.LEFT);
		lblSleepMaxAv.setToolTipText("Specifies the maximum angular velocity used to determine whether a body is at rest or not.");
		// add the label to the layout
		pnlSleep.add(lblSleepMaxAv, new GridBagConstraints(
				0, 3, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepMaxAv = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getSleepAngularVelocity()), 0.0, 999.9, 0.5));
		spnSleepMaxAv.setEditor(new JSpinner.NumberEditor(spnSleepMaxAv, "0.0"));
		((JSpinner.DefaultEditor)spnSleepMaxAv.getEditor()).getTextField().setColumns(5);
		spnSleepMaxAv.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepAngularVelocity(Math.toRadians(v));
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepMaxAv, new GridBagConstraints(
				1, 3, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepMaxAvUnit = new JLabel("degrees / second");
		pnlSleep.add(lblSleepMaxAvUnit, new GridBagConstraints(
				2, 3, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the sleep panel to the over all panel
		panel.add(pnlSleep);
		
		//////////////////////////////////////////////////
		// Constraint solver group
		//////////////////////////////////////////////////
		
		// create the constraint panel
		JPanel pnlConstraint = new JPanel();
		// create the sleep panel border
		pnlConstraint.setBorder(new TitledBorder("Constraint Solver Settings"));
		// set the layout
		pnlConstraint.setLayout(new GridBagLayout());
		
		int y = 0;
		
		// velocity constraint solver iterations
		JLabel lblVelIter = new JLabel("Velocity Iterations", this.helpIcon, JLabel.LEFT);
		lblVelIter.setToolTipText("Specifies the accuracy of the velocity contraint solver.  Increasing this value increases the accuracy but lowers performance.");
		pnlConstraint.add(lblVelIter, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the slider for the si solver iterations
		JSpinner spnVelIter = new JSpinner(new SpinnerNumberModel(settings.getVelocityConstraintSolverIterations(), 5, 999, 1));
		spnVelIter.setEditor(new JSpinner.NumberEditor(spnVelIter, "0"));
		((JSpinner.DefaultEditor)spnVelIter.getEditor()).getTextField().setColumns(3);
		spnVelIter.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				int iter = ((SpinnerNumberModel) spnr.getModel()).getNumber().intValue();
				Settings settings = Settings.getInstance();
				settings.setVelocityConstraintSolverIterations(iter);
			}
		});
		// add it to the panel
		pnlConstraint.add(spnVelIter, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// position constraint solver iterations
		y++;
		JLabel lblPosIter = new JLabel("Position Iterations", this.helpIcon, JLabel.LEFT);
		lblPosIter.setToolTipText("Specifies the accuracy of the position contraint solver.  Increasing this value increases the accuracy but lowers performance.");
		pnlConstraint.add(lblPosIter, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the slider for the si solver iterations
		JSpinner spnPosIter = new JSpinner(new SpinnerNumberModel(settings.getPositionConstraintSolverIterations(), 5, 999, 1));
		spnPosIter.setEditor(new JSpinner.NumberEditor(spnPosIter, "0"));
		((JSpinner.DefaultEditor)spnPosIter.getEditor()).getTextField().setColumns(3);
		spnPosIter.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				int iter = ((SpinnerNumberModel) spnr.getModel()).getNumber().intValue();
				Settings settings = Settings.getInstance();
				settings.setPositionConstraintSolverIterations(iter);
			}
		});
		// add it to the panel
		pnlConstraint.add(spnPosIter, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// warm start distance
		y++;
		JLabel lblWarm = new JLabel("Warm start distance", this.helpIcon, JLabel.LEFT);
		lblWarm.setToolTipText("Specifies the distance between two iteration's contact points to determine whether to warm start or not.  " +
				"Set this value to to zero to turn off warm starting.  Warm starting provides better performance and accuracy.");
		pnlConstraint.add(lblWarm, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));

		JSpinner spnWarm = new JSpinner(new MultiplicativeSpinnerNumberModel(settings.getWarmStartDistance(), 1.0E-9, 1.0, 10.0));
		spnWarm.setEditor(new MultiplicativeSpinnerModelEditor(spnWarm));
		spnWarm.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double warm = ((MultiplicativeSpinnerNumberModel) spnr.getModel()).getValue();
				Settings settings = Settings.getInstance();
				settings.setWarmStartDistance(warm);
			}
		});
		pnlConstraint.add(spnWarm, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblWarmUnit = new JLabel("meters");
		pnlConstraint.add(lblWarmUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// restitution velocity
		y++;
		JLabel lblRest = new JLabel("Restitution velocity", this.helpIcon, JLabel.LEFT);
		lblRest.setToolTipText("Specifies at what relative velocity objects should bounce or attempt to come to rest.");
		pnlConstraint.add(lblRest, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnRest = new JSpinner(new SpinnerNumberModel(settings.getRestitutionVelocity(), 0.0, 9.9, 0.1));
		spnRest.setEditor(new JSpinner.NumberEditor(spnRest, "0.0"));
		((JSpinner.DefaultEditor)spnRest.getEditor()).getTextField().setColumns(3);
		spnRest.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double r = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setRestitutionVelocity(r);
			}
		});
		pnlConstraint.add(spnRest, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblRestUnit = new JLabel("meters / second");
		pnlConstraint.add(lblRestUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// linear tolerance
		y++;
		JLabel lblLinTol = new JLabel("Linear tolerance", this.helpIcon, JLabel.LEFT);
		lblLinTol.setToolTipText("Specifies the linear tolerance. This setting is used to control jitter.");
		pnlConstraint.add(lblLinTol, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnLinTol = new JSpinner(new SpinnerNumberModel(settings.getLinearTolerance(), 0.000, 9.995, 0.005));
		spnLinTol.setEditor(new JSpinner.NumberEditor(spnLinTol, "0.000"));
		((JSpinner.DefaultEditor)spnLinTol.getEditor()).getTextField().setColumns(5);
		spnLinTol.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double tol = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setLinearTolerance(tol);
			}
		});
		pnlConstraint.add(spnLinTol, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblLinTolUnit = new JLabel("meters");
		pnlConstraint.add(lblLinTolUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// angular tolerance
		y++;
		JLabel lblAngTol = new JLabel("Angular tolerance", this.helpIcon, JLabel.LEFT);
		lblAngTol.setToolTipText("Specifies the angular tolerance. This setting is used to control jitter.");
		pnlConstraint.add(lblAngTol, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnAngTol = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getAngularTolerance()), 0.0, 90.0, 1.0));
		spnAngTol.setEditor(new JSpinner.NumberEditor(spnAngTol, "0.0"));
		((JSpinner.DefaultEditor)spnAngTol.getEditor()).getTextField().setColumns(4);
		spnAngTol.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double tol = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setAngularTolerance(Math.toRadians(tol));
			}
		});
		pnlConstraint.add(spnAngTol, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblAngTolUnit = new JLabel("degrees");
		pnlConstraint.add(lblAngTolUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// linear correction
		y++;
		JLabel lblLinear = new JLabel("Maximum linear correction", this.helpIcon, JLabel.LEFT);
		lblLinear.setToolTipText("Specifies the maximum amount of linear correction to perform in position solving.  This is used to avoid large position corrections.");
		pnlConstraint.add(lblLinear, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnLinear = new JSpinner(new SpinnerNumberModel(settings.getMaxLinearCorrection(), 0.0, 1.0, 0.05));
		spnLinear.setEditor(new JSpinner.NumberEditor(spnLinear, "0.00"));
		((JSpinner.DefaultEditor)spnLinear.getEditor()).getTextField().setColumns(4);
		spnLinear.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double lin = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxLinearCorrection(lin);
			}
		});
		pnlConstraint.add(spnLinear, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblLinearUnit = new JLabel("meters");
		pnlConstraint.add(lblLinearUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// angular correction
		y++;
		JLabel lblAngular = new JLabel("Maximum angular correction", this.helpIcon, JLabel.LEFT);
		lblAngular.setToolTipText("Specifies the maximum amount of angular correction to perform in position solving.  This is used to avoid large position corrections.");
		pnlConstraint.add(lblAngular, new GridBagConstraints(
				0, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnAngular = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getMaxAngularCorrection()), 0.0, 90.0, 1.0));
		spnAngular.setEditor(new JSpinner.NumberEditor(spnAngular, "0.0"));
		((JSpinner.DefaultEditor)spnAngular.getEditor()).getTextField().setColumns(4);
		spnAngular.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double ang = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxAngularCorrection(Math.toRadians(ang));
			}
		});
		pnlConstraint.add(spnAngular, new GridBagConstraints(
				1, y, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblAngularUnit = new JLabel("degrees");
		pnlConstraint.add(lblAngularUnit, new GridBagConstraints(
				2, y, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// baumgarte
		y++;
		JLabel lblBaum = new JLabel("Baumgarte", this.helpIcon, JLabel.LEFT);
		lblBaum.setToolTipText("Specifies the rate at which the position constraints are solved.");
		pnlConstraint.add(lblBaum, new GridBagConstraints(
				0, y, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JSpinner spnBaum = new JSpinner(new SpinnerNumberModel(settings.getBaumgarte(), 0.0, 1.0, 0.05));
		spnBaum.setEditor(new JSpinner.NumberEditor(spnBaum, "0.00"));
		((JSpinner.DefaultEditor)spnBaum.getEditor()).getTextField().setColumns(4);
		spnBaum.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double baum = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setBaumgarte(baum);
			}
		});
		pnlConstraint.add(spnBaum, new GridBagConstraints(
				1, y, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the sleep panel to the over all panel
		panel.add(pnlConstraint);
		
		//////////////////////////////////////////////////
		// CCD group
		//////////////////////////////////////////////////
		
		// create the constraint panel
		JPanel pnlCCD = new JPanel();
		// create the sleep panel border
		pnlCCD.setBorder(new TitledBorder("Continuous Collision Detection"));
		// set the layout
		pnlCCD.setLayout(new GridBagLayout());
		
		JLabel lblCCDEnabled = new JLabel("Enabled", this.helpIcon, JLabel.LEFT);
		lblCCDEnabled.setToolTipText("If enabled, tests dynamic bodies for tunneling.");
		pnlCCD.add(lblCCDEnabled, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkCCDEnabled = new JCheckBox();
		chkCCDEnabled.setSelected(settings.isContinuousCollisionDetectionEnabled());
		chkCCDEnabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings settings = Settings.getInstance();
				settings.setContinuousCollisionDetectionEnabled(!settings.isContinuousCollisionDetectionEnabled());
			}
		});
		pnlCCD.add(chkCCDEnabled, new GridBagConstraints(
				1, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the CCD panel to the over all panel
		panel.add(pnlCCD);
		
		// this button is for grabbing the size of the window when
		// the number of things change
//		JButton btnSize = new JButton("Get Size");
//		btnSize.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.out.println(getSize());
//			}
//		});
//		panel.add(btnSize);
		
		return panel;
	}
	
	/**
	 * Adds all the test specific controls to the given panel.
	 * @param panel the panel to add the test controls to
	 * @param controls the test specific controls
	 */
	private void addTestControls(JPanel panel, String[][] controls) {
		// create some insets for all the labels
		Insets insets = new Insets(2, 2, 2, 2);

		int size = controls.length;
		int row = 0;
		for (String[] control : controls) {
			// create the labels
			JLabel lblKey = new JLabel(control[0]); // key
			JLabel lblDes = new JLabel(control[1]); // description
			
			// add them to the panel
			panel.add(lblKey, new GridBagConstraints(
					0, row, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			panel.add(lblDes, new GridBagConstraints(
					1, row, 1, 1, 1, (row + 1 == size ? 1 : 0), GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			row++;
		}
	}
	
	/**
	 * A main method for testing the settings frame stand alone.
	 * @param args command line arguments - none
	 */
	public static void main(String[] args) {
		ControlPanel sf;
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (UnsupportedLookAndFeelException e) {
			}
			sf = new ControlPanel();
			sf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			sf.setVisible(true);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Number model that performs multiplication/division instead of addition/subtraction
	 * @author William Bittle
	 * @version 1.0.3
	 * @since 1.0.0
	 */
	private class MultiplicativeSpinnerNumberModel extends AbstractSpinnerModel {
		/** The serializable id */
		private static final long serialVersionUID = 2898451773505206196L;
		
		/** The current/initial value */
		private double value;
		
		/** The minimum value */
		private double minimum;
		
		/** The maximum value */
		private double maximum;
		
		/** The increment */
		private double increment;
		
		/**
		 * Full constructor.
		 * @param value the initial value
		 * @param minimum the minimum value
		 * @param maximum the maximum value
		 * @param increment the increment amount
		 */
		public MultiplicativeSpinnerNumberModel(double value, double minimum, double maximum, double increment) {
			super();
			this.value = value;
			this.minimum = minimum;
			this.maximum = maximum;
			this.increment = increment;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SpinnerNumberModel#getNextValue()
		 */
		@Override
		public Object getNextValue() {
			if (this.value < this.maximum) {
				return this.value * this.increment;
			}
			return this.value;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SpinnerNumberModel#getPreviousValue()
		 */
		@Override
		public Object getPreviousValue() {
			if (this.value > this.minimum) {
				return this.value / this.increment;
			}
			return this.value;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SpinnerModel#getValue()
		 */
		@Override
		public Double getValue() {
			return this.value;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SpinnerNumberModel#setValue(java.lang.Object)
		 */
		@Override
		public void setValue(Object value) {
			if ((value == null) || !(value instanceof Double  )) {}
			if (!value.equals(this.value)) {
				this.value = (Double) value;
				fireStateChanged();
			}
		}
		
		/**
		 * Returns the minimum value allowed.
		 * @return double
		 */
		public double getMinimum() {
			return minimum;
		}
		
		/**
		 * Returns the maximum value allowed.
		 * @return double
		 */
		public double getMaximum() {
			return maximum;
		}
	}
	
	/**
	 * Spinner editor for the {@link MultiplicativeSpinnerNumberModel}.
	 * @author William Bittle
	 * @version 1.0.3
	 * @since 1.0.0
	 */
	private class MultiplicativeSpinnerModelEditor extends JFormattedTextField implements ChangeListener, PropertyChangeListener {
		/** The serializable id */
		private static final long serialVersionUID = -7174664815660393176L;
		
		/** The spinner model */
		private MultiplicativeSpinnerNumberModel model;
		
		/**
		 * Full constructor.
		 * @param spinner the spinner to attach to
		 */
        public MultiplicativeSpinnerModelEditor(JSpinner spinner) {
        	super(new DecimalFormat("0E0"));
            // get the current value
            this.model = (MultiplicativeSpinnerNumberModel)(spinner.getModel());
            this.setValue(this.model.getValue());
            
            // add this as a change listener
            spinner.addChangeListener(this);
            
            // set the text box size
            this.setColumns(4);
            this.setHorizontalAlignment(JTextField.RIGHT);
            
            // look for changes on the value property
            this.addPropertyChangeListener("value", this);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e) {
            JSpinner spinner = (JSpinner)(e.getSource());
            MultiplicativeSpinnerNumberModel model = (MultiplicativeSpinnerNumberModel)(spinner.getModel());
            this.setValue(model.getValue());
        }
        
        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent e) {
        	double max = this.model.getMaximum();
        	double min = this.model.getMinimum();
        	double value = Double.parseDouble(this.getText());
        	// check if the value is in range
        	if (value >= min && value <= max) {
        		this.model.setValue(value);
        	} else {
        		// reset the text field to the current value
        		this.setValue(this.model.getValue());
        	}
        }
    }
}
