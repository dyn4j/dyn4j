/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.xml.parsers.ParserConfigurationException;

import org.dyn4j.Epsilon;
import org.dyn4j.Version;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.actions.MoveAction;
import org.dyn4j.sandbox.actions.MoveWorldAction;
import org.dyn4j.sandbox.actions.RotateAction;
import org.dyn4j.sandbox.actions.SelectAction;
import org.dyn4j.sandbox.controls.MouseLocationTextField;
import org.dyn4j.sandbox.dialogs.AboutDialog;
import org.dyn4j.sandbox.dialogs.ExceptionDialog;
import org.dyn4j.sandbox.dialogs.HelpDialog;
import org.dyn4j.sandbox.dialogs.PreferencesDialog;
import org.dyn4j.sandbox.events.BodyActionEvent;
import org.dyn4j.sandbox.export.CodeExporter;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.input.Keyboard;
import org.dyn4j.sandbox.input.Mouse;
import org.dyn4j.sandbox.panels.ContactPanel;
import org.dyn4j.sandbox.panels.MemoryPanel;
import org.dyn4j.sandbox.panels.SimulationTreePanel;
import org.dyn4j.sandbox.panels.SystemPanel;
import org.dyn4j.sandbox.persist.XmlFormatter;
import org.dyn4j.sandbox.persist.XmlGenerator;
import org.dyn4j.sandbox.persist.XmlReader;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.tests.CompiledSimulation;
import org.dyn4j.sandbox.utilities.Fps;
import org.dyn4j.sandbox.utilities.RenderUtilities;
import org.xml.sax.SAXException;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Main class for the Sandbox application.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.0
 */
public class Sandbox extends JFrame implements GLEventListener, ActionListener, WindowListener {
	/** The version id */
	private static final long serialVersionUID = -7050279589455803564L;

	/** The conversion factor from nano to base */
	private static final double NANO_TO_BASE = 1.0e9;
	
	/** The sandbox version */
	public static final String VERSION = "1.0.4";

	/** The origin label (pulled into a local variable for fast rendering) */
	private static final String ORIGIN_LABEL = Messages.getString("canvas.originLabel");
	
	/** The scale conversion label (pulled into a local variable for fast rendering) for values greater than 1 */
	private static final String SCALE_CONVERSION_WHOLE = Messages.getString("canvas.scale.conversion.whole");
	
	/** The scale conversion label (pulled into a local variable for fast rendering) for values less than 1*/
	private static final String SCALE_CONVERSION_FRACTION = Messages.getString("canvas.scale.conversion.fraction");
	
	/** The scale length (pulled into a local variable for fast rendering) */
	private static final String SCALE_LENGTH = Messages.getString("canvas.scale.length");

	// the simulation
	
	/** The current simulation */
	private Simulation simulation;
	
	// data
	
	/** The canvas to draw to */
	private NewtCanvasAWT canvas;
	
	/** The canvas size */
	private Dimension canvasSize;
	
	/** The OpenGL animator */
	private Animator animator;
	
	/** The OpenGL text renderer (sans-serif, plain, 12) */
	private TextRenderer textRenderer;
	
	/** The time stamp for the last iteration */
	private long last;
	
	/** The paused flag */
	private boolean paused = true;
	
	/** The number of steps to perform (single steps from the single step button) */
	private int steps;
	
	/** The keyboard to accept and store key events */
	private Keyboard keyboard;
	
	/** The mouse to accept and store mouse events */
	private Mouse mouse;
	
	/** The frames per second monitor */
	private Fps fps;
	
	/** The last path the user chose from either the load or save dialog */
	private File directory;
	
	/** The last file name opened or saved */
	private String currentFileName;
	
	/** The map to store snapshots of the simulation */
	private Map<String, String> snapshots = new HashMap<String, String>();
	
	// controls
	
	// The menu bar
	
	/** The main menu bar */
	private JMenuBar barMenu;
	
	/** The file menu */
	private JMenu mnuFile;
	
	/** The File_New menu option */
	private JMenuItem mnuNew;
	
	/** The File_Open menu option */
	private JMenuItem mnuOpen;
	
	/** The File_Save As... menu option */
	private JMenuItem mnuSaveAs;
	
	/** The File_Export menu option */
	private JMenu mnuExport;
	
	/** The snapshot menu */
	private JMenu mnuSnapshot;
	
	/** The tests menu */
	private JMenu mnuTests;
	
	/** The help menu */
	private JMenu mnuHelp;
	
	/** The window menu */
	private JMenu mnuWindow;
	
	/** The look and feel menu */
	private JMenu mnuLookAndFeel;
	
	// The world tree panel
	
	/** The world tree control */
	private SimulationTreePanel pnlSimulation;
	
	// bottom left panels
	
	/** The panel to show the contacts */
	private ContactPanel pnlContacts;
	
	/** The panel to show system information */
	private SystemPanel pnlSystem;
	
	/** The panel to show memory usage */
	private MemoryPanel pnlMemory;
	
	// Simulation toolbar
	
	/** The start button for the simulation */
	private JButton btnStart;
	
	/** The single step button for the simulation */
	private JButton btnStep;
	
	/** The stop button for the simulation */
	private JButton btnStop;
	
	/** The reset button (only for compiled simulations) */
	private JButton btnReset;
	
	/** Label to show the frames per second */
	private JTextField lblFps;
	
	// Preferences toolbar
	
	/** The anti-aliasing toggle button */
	private JToggleButton tglAntiAliasing;
	
	/** The vertical sync toggle button */
	private JToggleButton tglVerticalSync;
	
	/** The label origin toggle button */
	private JToggleButton tglOriginLabel;
	
	/** The bounds toggle button */
	private JToggleButton tglBounds;
	
	/** The scale toggle button */
	private JToggleButton tglScale;
	
	/** The contact pairs toggle button */
	private JToggleButton tglContactPairs;
	
	/** The contact points toggle button */
	private JToggleButton tglContactPoints;
	
	/** The contact impulses toggle button */
	private JToggleButton tglContactImpulses;
	
	/** The friction impulses toggle button */
	private JToggleButton tglFrictionImpulses;
	
	/** The random color toggle button */
	private JToggleButton tglRandomColor;
	
	/** The stencil toggle button */
	private JToggleButton tglStencil;
	
	/** The show body labels toggle button */
	private JToggleButton tglBodyLabels;
	
	/** The show fixture labels toggle button */
	private JToggleButton tglFixtureLabels;
	
	/** The body center toggle button */
	private JToggleButton tglBodyCenter;
	
	/** The body AABBs toggle button */
	private JToggleButton tglBodyAABBs;
	
	/** The body normals toggle button */
	private JToggleButton tglBodyNormals;
	
	/** The body rotation disc toggle button */
	private JToggleButton tglBodyRotationDiscs;
	
	/** The body velocities toggle button */
	private JToggleButton tglBodyVelocities;
	
	// Camera toolbar
	
	/** The zoom in button */
	private JButton btnZoomIn;
	
	/** The zoom out button */
	private JButton btnZoomOut;
	
	/** The move camera to origin button */
	private JButton btnToOrigin;
	
	// Mouse location toolbar
	
	/** The mouse location label */
	private MouseLocationTextField lblMouseLocation;
	
	// Actions performed on the OpenGL canvas
	
	// Actions performed on bodies
	
	/** The select body action */
	private SelectAction<SandboxBody> selectBodyAction = new SelectAction<SandboxBody>();
	
	/** The move body action */
	private MoveAction moveBodyAction = new MoveAction();
	
	/** The joint used to move bodies when the simulation is running */
	private MouseJoint selectedBodyJoint;
	
	/** The rotate body action */
	private RotateAction rotateBodyAction = new RotateAction();
	
	// Actions performed on fixtures
	
	/** The edit body action (used to move and rotate fixtures) */
	private SelectAction<SandboxBody> editBodyAction = new SelectAction<SandboxBody>();
	
	/** The select fixture action */
	private SelectAction<BodyFixture> selectFixtureAction = new SelectAction<BodyFixture>();
	
	/** The move fixture action */
	private MoveAction moveFixtureAction = new MoveAction();
	
	/** The rotate fixture action */
	private RotateAction rotateFixtureAction = new RotateAction();
	
	// Actions performed on the world
	
	/** The move world action */
	private MoveWorldAction moveWorldAction = new MoveWorldAction();
	
	/**
	 * Default constructor.
	 */
	private Sandbox() {
		super();
		
		// let the methods in this class handle closing the window
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		
		// set the window title
		this.setTitle(this.getWindowTitle());
		
		// make sure tooltips and menus show up on top of the heavy weight canvas
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		// setup a default simulation
		this.simulation = new Simulation();
		
		// create the contact panel
		this.pnlContacts = new ContactPanel(this.simulation.getContactCounter());
		this.pnlSystem = new SystemPanel();
		this.pnlSystem.setPreferredSize(new Dimension(205, 100));
		this.pnlMemory = new MemoryPanel();
		
		// create the keyboard and mouse
		this.keyboard = new Keyboard();
		this.mouse = new Mouse();
		this.fps = new Fps();
		
		// create the world tree
		Dimension size = new Dimension(220, 400);
		this.pnlSimulation = new SimulationTreePanel();
		this.pnlSimulation.setSimulation(this.simulation);
		this.pnlSimulation.setPreferredSize(size);
		this.pnlSimulation.setMinimumSize(size);
		this.pnlSimulation.addActionListener(this);
		
		// create the main menu bar
		
		this.barMenu = new JMenuBar();
		
		// file menu
		this.mnuFile = new JMenu(Messages.getString("menu.file"));
		
		this.mnuNew = new JMenuItem(Messages.getString("menu.file.new"));
		this.mnuNew.setIcon(Icons.NEW_SIMULATION);
		this.mnuNew.setActionCommand("new");
		this.mnuNew.addActionListener(this);
		
		this.mnuSaveAs = new JMenuItem(Messages.getString("menu.file.save"));
		this.mnuSaveAs.setIcon(Icons.SAVE);
		this.mnuSaveAs.setActionCommand("save");
		this.mnuSaveAs.addActionListener(this);
		
		this.mnuOpen = new JMenuItem(Messages.getString("menu.file.open"));
		this.mnuOpen.setIcon(Icons.OPEN);
		this.mnuOpen.setActionCommand("open");
		this.mnuOpen.addActionListener(this);
		
		this.mnuExport = new JMenu(Messages.getString("menu.file.export"));
		
		JMenuItem mnuExportJava = new JMenuItem(Messages.getString("menu.file.export.java"));
		mnuExportJava.setIcon(Icons.EXPORT_JAVA);
		mnuExportJava.setActionCommand("export-java");
		mnuExportJava.addActionListener(this);
		mnuExport.add(mnuExportJava);
		
		JMenuItem mnuExit = new JMenuItem(Messages.getString("menu.file.exit"));
		mnuExit.setActionCommand("exit");
		mnuExit.addActionListener(this);
		
		this.mnuFile.add(this.mnuNew);
		this.mnuFile.add(this.mnuOpen);
		this.mnuFile.addSeparator();
		this.mnuFile.add(this.mnuSaveAs);
		this.mnuFile.add(this.mnuExport);
		this.mnuFile.addSeparator();
		this.mnuFile.add(mnuExit);
		
		// snapshot menu
		this.mnuSnapshot = new JMenu(Messages.getString("menu.snapshot"));
		
		JMenuItem mnuTakeSnapshot = new JMenuItem(Messages.getString("menu.snapshot.take"));
		mnuTakeSnapshot.setActionCommand("snapshotTake");
		mnuTakeSnapshot.addActionListener(this);
		mnuTakeSnapshot.setIcon(Icons.SNAPSHOT_TAKE);
		
		JMenuItem mnuClearSnapshots = new JMenuItem(Messages.getString("menu.snapshot.clear"));
		mnuClearSnapshots.setActionCommand("snapshotClearAll");
		mnuClearSnapshots.addActionListener(this);
		mnuClearSnapshots.setIcon(Icons.SNAPSHOT_REMOVE);
		
		this.mnuSnapshot.add(mnuTakeSnapshot);
		this.mnuSnapshot.add(mnuClearSnapshots);
		this.mnuSnapshot.addSeparator();
		
		// tests menu
		this.mnuTests = new JMenu(Messages.getString("menu.tests"));
		this.createTestMenuItems(this.mnuTests);
		
		// window menu
		this.mnuWindow = new JMenu(Messages.getString("menu.window"));
		
		JMenuItem mnuPreferences = new JMenuItem(Messages.getString("menu.window.preferences"));
		mnuPreferences.setIcon(Icons.PREFERENCES);
		mnuPreferences.setActionCommand("preferences");
		mnuPreferences.addActionListener(this);
		
		this.mnuWindow.add(mnuPreferences);
		
		// look and feel menu
		this.mnuLookAndFeel = new JMenu(Messages.getString("menu.window.laf"));
		this.createLookAndFeelMenuItems(this.mnuLookAndFeel);
		this.mnuWindow.add(this.mnuLookAndFeel);
		
		// help menu
		this.mnuHelp = new JMenu(Messages.getString("menu.help"));
		
		JMenuItem mnuAbout = new JMenuItem(Messages.getString("menu.help.about"));
		mnuAbout.setActionCommand("about");
		mnuAbout.addActionListener(this);
		
		JMenuItem mnuHelpContents = new JMenuItem(Messages.getString("menu.help.contents"));
		mnuHelpContents.setIcon(Icons.HELP);
		mnuHelpContents.setActionCommand("helpContents");
		mnuHelpContents.addActionListener(this);
		
		this.mnuHelp.add(mnuHelpContents);
		this.mnuHelp.add(mnuAbout);
		
		this.barMenu.add(this.mnuFile);
		this.barMenu.add(this.mnuSnapshot);
		this.barMenu.add(this.mnuTests);
		this.barMenu.add(this.mnuWindow);
		this.barMenu.add(this.mnuHelp);
		
		this.setJMenuBar(this.barMenu);
		
		// create the simulation tool bar
		
		JToolBar barSimulation = new JToolBar(Messages.getString("toolbar.simulation"), JToolBar.HORIZONTAL);
		barSimulation.setFloatable(false);
		
		this.btnStart = new JButton(Icons.START);
		this.btnStart.addActionListener(this);
		this.btnStart.setActionCommand("start");
		this.btnStart.setToolTipText(Messages.getString("toolbar.simulation.start"));
		
		this.btnStep = new JButton(Icons.STEP);
		this.btnStep.addActionListener(this);
		this.btnStep.setActionCommand("step");
		this.btnStep.setToolTipText(Messages.getString("toolbar.simulation.step"));
		
		this.btnStop = new JButton(Icons.STOP);
		this.btnStop.addActionListener(this);
		this.btnStop.setActionCommand("stop");
		this.btnStop.setToolTipText(Messages.getString("toolbar.simulation.stop"));
		
		this.btnReset = new JButton(Icons.RESET);
		this.btnReset.addActionListener(this);
		this.btnReset.setActionCommand("reset");
		this.btnReset.setToolTipText(Messages.getString("toolbar.simulation.reset"));
		
		this.btnStart.setEnabled(true);
		this.btnStop.setEnabled(false);
		this.btnReset.setEnabled(false);
		
		barSimulation.add(this.btnStart);
		barSimulation.add(this.btnStep);
		barSimulation.add(this.btnStop);
		barSimulation.add(this.btnReset);
		
		this.lblFps = new JTextField();
		this.lblFps.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.lblFps.setHorizontalAlignment(JTextField.RIGHT);
		this.lblFps.setColumns(7);
		this.lblFps.setEditable(false);
		this.lblFps.setToolTipText(Messages.getString("toolbar.simulation.fps"));
		
		barSimulation.add(this.lblFps);
		barSimulation.addSeparator();

		this.btnZoomIn = new JButton(Icons.ZOOM_IN);
		this.btnZoomIn.setToolTipText(Messages.getString("toolbar.simulation.zoomIn"));
		this.btnZoomIn.setActionCommand("zoom-in");
		this.btnZoomIn.addActionListener(this);
		
		this.btnZoomOut = new JButton(Icons.ZOOM_OUT);
		this.btnZoomOut.setToolTipText(Messages.getString("toolbar.simulation.zoomOut"));
		this.btnZoomOut.setActionCommand("zoom-out");
		this.btnZoomOut.addActionListener(this);
		
		this.btnToOrigin = new JButton(Icons.TO_ORIGIN);
		this.btnToOrigin.setToolTipText(Messages.getString("toolbar.simulation.toOrigin"));
		this.btnToOrigin.setActionCommand("to-origin");
		this.btnToOrigin.addActionListener(this);
		
		barSimulation.add(this.btnZoomIn);
		barSimulation.add(this.btnZoomOut);
		barSimulation.add(this.btnToOrigin);
		barSimulation.addSeparator();
		
		this.lblMouseLocation = new MouseLocationTextField();
		this.lblMouseLocation.setHorizontalAlignment(JTextField.RIGHT);
		this.lblMouseLocation.setColumns(20);
		this.lblMouseLocation.setEditable(false);
		this.lblMouseLocation.setToolTipText(Messages.getString("toolbar.simulation.mouseLocation"));
		
		barSimulation.add(this.lblMouseLocation);
		
		// create the preferences toolbar
		
		JToolBar barPreferences = new JToolBar(Messages.getString("toolbar.preferences"), JToolBar.HORIZONTAL);
		barPreferences.setFloatable(false);
		
		this.tglAntiAliasing = new JToggleButton(Icons.AA);
		this.tglAntiAliasing.setToolTipText(Messages.getString("toolbar.preferences.antialiasing"));
		this.tglAntiAliasing.setActionCommand("aa");
		this.tglAntiAliasing.addActionListener(this);
		this.tglAntiAliasing.setSelected(Preferences.isAntiAliasingEnabled());
		
		this.tglVerticalSync = new JToggleButton(Icons.SYNC);
		this.tglVerticalSync.setToolTipText(Messages.getString("toolbar.preferences.verticalSync"));
		this.tglVerticalSync.setActionCommand("vertical-sync");
		this.tglVerticalSync.addActionListener(this);
		this.tglVerticalSync.setSelected(Preferences.isVerticalSyncEnabled());
		
		this.tglBounds = new JToggleButton(Icons.BOUNDS);
		this.tglBounds.setToolTipText(Messages.getString("toolbar.preferences.worldBounds"));
		this.tglBounds.setActionCommand("bounds");
		this.tglBounds.addActionListener(this);
		this.tglBounds.setSelected(Preferences.isBoundsEnabled());

		this.tglOriginLabel = new JToggleButton(Icons.ORIGIN);
		this.tglOriginLabel.setToolTipText(Messages.getString("toolbar.preferences.originLabel"));
		this.tglOriginLabel.setActionCommand("origin");
		this.tglOriginLabel.addActionListener(this);
		this.tglOriginLabel.setSelected(Preferences.isOriginLabeled());
		
		this.tglScale = new JToggleButton(Icons.SCALE);
		this.tglScale.setToolTipText(Messages.getString("toolbar.preferences.scale"));
		this.tglScale.setActionCommand("scale");
		this.tglScale.addActionListener(this);
		this.tglScale.setSelected(Preferences.isScaleEnabled());
		
		this.tglBodyCenter = new JToggleButton(Icons.BODY_CENTER);
		this.tglBodyCenter.setToolTipText(Messages.getString("toolbar.preferences.bodyCenters"));
		this.tglBodyCenter.setActionCommand("bodyCenter");
		this.tglBodyCenter.addActionListener(this);
		this.tglBodyCenter.setSelected(Preferences.isBodyCenterEnabled());
		
		this.tglRandomColor = new JToggleButton(Icons.COLOR);
		this.tglRandomColor.setToolTipText(Messages.getString("toolbar.preferences.bodyColor"));
		this.tglRandomColor.setActionCommand("color");
		this.tglRandomColor.addActionListener(this);
		this.tglRandomColor.setSelected(Preferences.isBodyColorRandom());
		
		this.tglStencil = new JToggleButton(Icons.STENCIL);
		this.tglStencil.setToolTipText(Messages.getString("toolbar.preferences.bodyStenciling"));
		this.tglStencil.setActionCommand("stencil");
		this.tglStencil.addActionListener(this);
		this.tglStencil.setSelected(Preferences.isBodyStenciled());
		
		this.tglBodyLabels = new JToggleButton(Icons.BODY_LABEL);
		this.tglBodyLabels.setToolTipText(Messages.getString("toolbar.preferences.bodyLabels"));
		this.tglBodyLabels.setActionCommand("bodyLabel");
		this.tglBodyLabels.addActionListener(this);
		this.tglBodyLabels.setSelected(Preferences.isBodyLabeled());
		
		this.tglFixtureLabels = new JToggleButton(Icons.FIXTURE_LABEL);
		this.tglFixtureLabels.setToolTipText(Messages.getString("toolbar.preferences.fixtureLabels"));
		this.tglFixtureLabels.setActionCommand("fixtureLabel");
		this.tglFixtureLabels.addActionListener(this);
		this.tglFixtureLabels.setSelected(Preferences.isFixtureLabeled());
		
		this.tglContactPairs = new JToggleButton(Icons.CONTACT_PAIR);
		this.tglContactPairs.setToolTipText(Messages.getString("toolbar.preferences.contactPairs"));
		this.tglContactPairs.setActionCommand("contactPair");
		this.tglContactPairs.addActionListener(this);
		this.tglContactPairs.setSelected(Preferences.isContactPairEnabled()); 
		
		this.tglContactPoints = new JToggleButton(Icons.CONTACT);
		this.tglContactPoints.setToolTipText(Messages.getString("toolbar.preferences.contactPoints"));
		this.tglContactPoints.setActionCommand("contactPoint");
		this.tglContactPoints.addActionListener(this);
		this.tglContactPoints.setSelected(Preferences.isContactPointEnabled()); 
		
		this.tglContactImpulses = new JToggleButton(Icons.CONTACT_IMPULSE);
		this.tglContactImpulses.setToolTipText(Messages.getString("toolbar.preferences.contactImpulses"));
		this.tglContactImpulses.setActionCommand("contactImpulse");
		this.tglContactImpulses.addActionListener(this);
		this.tglContactImpulses.setSelected(Preferences.isContactImpulseEnabled());
		
		this.tglFrictionImpulses = new JToggleButton(Icons.FRICTION_IMPULSE);
		this.tglFrictionImpulses.setToolTipText(Messages.getString("toolbar.preferences.contactFrictionImpulses"));
		this.tglFrictionImpulses.setActionCommand("frictionImpulse");
		this.tglFrictionImpulses.addActionListener(this);
		this.tglFrictionImpulses.setSelected(Preferences.isFrictionImpulseEnabled()); 
		
		this.tglBodyAABBs = new JToggleButton(Icons.AABB);
		this.tglBodyAABBs.setToolTipText(Messages.getString("toolbar.preferences.bodyAABB"));
		this.tglBodyAABBs.setActionCommand("aabb");
		this.tglBodyAABBs.addActionListener(this);
		this.tglBodyAABBs.setSelected(Preferences.isBodyAABBEnabled());
		
		this.tglBodyNormals = new JToggleButton(Icons.NORMAL);
		this.tglBodyNormals.setToolTipText(Messages.getString("toolbar.preferences.fixtureNormals"));
		this.tglBodyNormals.setActionCommand("normals");
		this.tglBodyNormals.addActionListener(this);
		this.tglBodyNormals.setSelected(Preferences.isBodyNormalEnabled());
		
		this.tglBodyRotationDiscs = new JToggleButton(Icons.ROTATION_DISC);
		this.tglBodyRotationDiscs.setToolTipText(Messages.getString("toolbar.preferences.bodyRotationDisc"));
		this.tglBodyRotationDiscs.setActionCommand("rotationDisc");
		this.tglBodyRotationDiscs.addActionListener(this);
		this.tglBodyRotationDiscs.setSelected(Preferences.isBodyRotationDiscEnabled());
		
		this.tglBodyVelocities = new JToggleButton(Icons.VELOCITY);
		this.tglBodyVelocities.setToolTipText(Messages.getString("toolbar.preferences.bodyVelocity"));
		this.tglBodyVelocities.setActionCommand("velocity");
		this.tglBodyVelocities.addActionListener(this);
		this.tglBodyVelocities.setSelected(Preferences.isBodyVelocityEnabled());
		
		// general
		barPreferences.add(this.tglAntiAliasing);
		barPreferences.add(this.tglVerticalSync);
		barPreferences.add(this.tglOriginLabel);
		barPreferences.add(this.tglScale);
		barPreferences.add(this.tglBounds);
		barPreferences.add(this.tglContactPairs);
		barPreferences.add(this.tglContactPoints);
		barPreferences.add(this.tglContactImpulses);
		barPreferences.add(this.tglFrictionImpulses);
		// body stuff
		barPreferences.add(this.tglRandomColor);
		barPreferences.add(this.tglStencil);
		barPreferences.add(this.tglBodyLabels);
		barPreferences.add(this.tglFixtureLabels);
		barPreferences.add(this.tglBodyCenter);
		barPreferences.add(this.tglBodyAABBs);
		barPreferences.add(this.tglBodyNormals);
		barPreferences.add(this.tglBodyRotationDiscs);
		barPreferences.add(this.tglBodyVelocities);
		
		// add the toolbar to the layout
		
		JPanel pnlToolBar = new JPanel();
		pnlToolBar.setLayout(new GridLayout(2, 1));
		pnlToolBar.add(barSimulation);
		pnlToolBar.add(barPreferences);
		
		// attempt to set the icon
		this.setIconImage(Icons.SANDBOX_48.getImage());
		
		// setup OpenGL capabilities
		if (!GLProfile.isAvailable(GLProfile.GL2)) {
			throw new GLException(Messages.getString("exception.opengl.version"));
		}
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		// setup the stencil buffer to outline shapes
		caps.setStencilBits(1);
		// setting multisampling allows for better looking body outlines
		caps.setSampleBuffers(true);
		caps.setNumSamples(2);
		caps.setHardwareAccelerated(true);
		
		// create a NEWT window
		GLWindow window = GLWindow.create(caps);
		window.setUndecorated(true);
		window.addGLEventListener(this);
		
		this.canvasSize = new Dimension(800, 600);
		// Use the newt/awt bridge to allow us to use swing gui elements
		// and the fast rendering capabilities of NEWT
		this.canvas = new NewtCanvasAWT(window);
		// create a canvas to paint to 
		this.canvas.setPreferredSize(this.canvasSize);
		this.canvas.setMinimumSize(new Dimension(600, 600));
		this.canvas.setIgnoreRepaint(true);
		
		// placing the GLCanvas in a JPanel allows the JSplitPane
		// to not cause an exception when the user moves the split
		JPanel pnlTest = new JPanel();
		pnlTest.setLayout(new BorderLayout());
		pnlTest.add(this.canvas);
		window.addKeyListener(this.keyboard);
		window.addMouseListener(this.mouse);
		// use the JFrames mouse wheel event processing because
		// using the GLWindow's requires it to have focus
		this.addMouseWheelListener(this.mouse);
		
		// create a tabbed pane below the world tree
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabs.addTab(Messages.getString("tab.contacts"), this.pnlContacts);
		tabs.addTab(Messages.getString("tab.system"), this.pnlSystem);
		tabs.addTab(Messages.getString("tab.memory"), this.pnlMemory);
		
		JPanel pnlLeft = new JPanel();
		pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
		pnlLeft.add(this.pnlSimulation);
		pnlLeft.add(tabs);
		
		setCompiledSimulation(false);
		
		// add a split pane
		JSplitPane pneSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlTest);
		
		// setup the layout
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlToolBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pneSplit));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pneSplit));
		
		// size everything
		this.pack();
		
		// move from (0, 0) since this hides some of the window frame
		this.setLocation(10, 10);
		
		// show the window
		this.setVisible(true);
		
		// initialize the last update time
		this.last = System.nanoTime();
		
		// create an animator to animated the canvas
		this.animator = new Animator(window);
		this.animator.setRunAsFastAsPossible(false);
		this.animator.start();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		int choice = JOptionPane.showConfirmDialog(this, Messages.getString("dialog.exit.text"), Messages.getString("dialog.exit.title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice == JOptionPane.YES_OPTION) {
			this.dispose();
			System.exit(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		// World tree panel events
		if ("clear-all".equals(command)) {
			// clear the current selection
			this.endAllActions();
		} else if ("remove-body".equals(command)) {
			// check if it was this body
			BodyActionEvent bae = (BodyActionEvent)event;
			SandboxBody body = bae.getBody();
			// check the select/edit body actions
			if (body == this.selectBodyAction.getObject()
			 || body == this.editBodyAction.getObject()) {
				// if the body is selected or being edited then end the actions
				this.endAllActions();
			}
		}
		
		// Sandbox events
		if ("new".equals(command)) {
			// make sure they are sure
			int choice = JOptionPane.showConfirmDialog(this, 
					Messages.getString("dialog.new.text"), 
					Messages.getString("dialog.new.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (choice == JOptionPane.YES_OPTION) {
				// clear the snapshots
				this.clearAllSnapshots();
				// clear the last file name
				this.currentFileName = null;
				// create a new empty simulation
				synchronized (Simulation.LOCK) {
					this.simulation = new Simulation();
					// update the simulation tree
					this.pnlSimulation.setSimulation(this.simulation);
					// update the contact panel
					this.pnlContacts.setContactCounter(this.simulation.getContactCounter());
					// disable/enable stuff because of the compiled test
					this.setCompiledSimulation(false);
				}
				// set the window title
				this.setTitle(this.getWindowTitle());
				// stop all actions
				this.endAllActions();
			}
		} else if ("save".equals(command)) {
			try {
				this.saveSimulationAction();
			} catch (IOException e) {
				ExceptionDialog.show(this, 
						Messages.getString("dialog.save.error.title"), 
						Messages.getString("dialog.save.error.text"), 
						e);
			}
		} else if ("open".equals(command)) {
			try {
				this.openSimulationAction();
				// stop all actions
				this.endAllActions();
			} catch (Exception e) {
				ExceptionDialog.show(this, 
						Messages.getString("dialog.open.error.title"), 
						Messages.getString("dialog.open.error.text"), 
						e);
			}
		} else if ("export-java".equals(command)) {
			try {
				this.exportToJavaCode();
			} catch (Exception e) {
				ExceptionDialog.show(this, 
						Messages.getString("dialog.export.java.error.title"), 
						Messages.getString("dialog.export.java.error.text"), 
						e);
			}
		} else if ("exit".equals(command)) {
			int choice = JOptionPane.showConfirmDialog(this, Messages.getString("dialog.exit.text"), Messages.getString("dialog.exit.title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				// dispose of all resources
				this.dispose();
				// let the jvm exit
				System.exit(0);
			}
		} else if ("start".equals(command)) {
			if (isPaused()) {
				// stop all actions
				this.endAllActions();
				// take a snapshot of the current simulation
				// if its not a compiled simulation
				synchronized (Simulation.LOCK) {
					if (!(this.simulation instanceof CompiledSimulation)) {
						this.takeSnapshot(true);
						// disable the simulation editor
						this.pnlSimulation.setEnabled(false);
					}
				}
				this.btnStart.setEnabled(false);
				this.btnStep.setEnabled(false);
				this.btnStop.setEnabled(true);
				this.btnReset.setEnabled(false);
				this.mnuNew.setEnabled(false);
				this.mnuOpen.setEnabled(false);
				this.mnuSaveAs.setEnabled(false);
				this.mnuExport.setEnabled(false);
				this.mnuSnapshot.setEnabled(false);
				this.mnuTests.setEnabled(false);
				setPaused(false);
			}
		} else if ("step".equals(command)) {
			// immediately performs a simulation step
			synchronized (this.btnStep) {
				this.steps++;
			}
		} else if ("stop".equals(command)) {
			if (!isPaused()) {
				// stop all actions
				this.endAllActions();
				// clear the mouse joint
				this.selectedBodyJoint = null;
				
				// enable the world editor if its not a compiled simulation
				synchronized (Simulation.LOCK) {
					if (!(this.simulation instanceof CompiledSimulation)) {
						// enable the simulation editor
						this.pnlSimulation.setEnabled(true);
						// enable the snapshots menu
						this.mnuSnapshot.setEnabled(true);
						this.mnuSaveAs.setEnabled(true);
						this.mnuExport.setEnabled(true);
					} else {
						// only re-enable reset if its a compiled simulation
						this.btnReset.setEnabled(true);
					}
				}
				this.btnStart.setEnabled(true);
				this.btnStep.setEnabled(true);
				this.btnStop.setEnabled(false);
				this.mnuNew.setEnabled(true);
				this.mnuOpen.setEnabled(true);
				this.mnuTests.setEnabled(true);
				setPaused(true);
			}
		} else if ("reset".equals(command)) {
			// reset the compiled simulation
			synchronized (Simulation.LOCK) {
				if (this.simulation instanceof CompiledSimulation) {
					CompiledSimulation simulation = (CompiledSimulation)this.simulation;
					simulation.reset();
					// make sure we update the simulation panel
					this.pnlSimulation.setSimulation(this.simulation);
				}
			}
		} else if ("color".equals(command)) {
			Preferences.setBodyColorRandom(!Preferences.isBodyColorRandom());
		} else if ("stencil".equals(command)) {
			Preferences.setBodyStenciled(!Preferences.isBodyStenciled());
		} else if ("bodyLabel".equals(command)) {
			Preferences.setBodyLabeled(!Preferences.isBodyLabeled());
		} else if ("fixtureLabel".equals(command)) {
			Preferences.setFixtureLabeled(!Preferences.isFixtureLabeled());
		} else if ("aa".equals(command)) {
			Preferences.setAntiAliasingEnabled(!Preferences.isAntiAliasingEnabled());
		} else if ("vertical-sync".equals(command)) {
			Preferences.setVerticalSyncEnabled(!Preferences.isVerticalSyncEnabled());
		} else if ("origin".equals(command)) {
			Preferences.setOriginLabeled(!Preferences.isOriginLabeled());
		} else if ("bounds".equals(command)) {
			Preferences.setBoundsEnabled(!Preferences.isBoundsEnabled());
		} else if ("scale".equals(command)) {
			Preferences.setScaleEnabled(!Preferences.isScaleEnabled());
		} else if ("contactPair".equals(command)) {
			Preferences.setContactPairEnabled(!Preferences.isContactPairEnabled());
		} else if ("contactPoint".equals(command)) {
			Preferences.setContactPointEnabled(!Preferences.isContactPointEnabled());
		} else if ("contactImpulse".equals(command)) {
			Preferences.setContactImpulseEnabled(!Preferences.isContactImpulseEnabled());
		} else if ("frictionImpulse".equals(command)) {
			Preferences.setFrictionImpulseEnabled(!Preferences.isFrictionImpulseEnabled());
		} else if ("bodyCenter".equals(command)) {
			Preferences.setBodyCenterEnabled(!Preferences.isBodyCenterEnabled());
		} else if ("aabb".equals(command)) {
			Preferences.setBodyAABBEnabled(!Preferences.isBodyAABBEnabled());
		} else if ("normals".equals(command)) {
			Preferences.setBodyNormalEnabled(!Preferences.isBodyNormalEnabled());
		} else if ("rotationDisc".equals(command)) {
			Preferences.setBodyRotationDiscEnabled(!Preferences.isBodyRotationDiscEnabled());
		} else if ("velocity".equals(command)) {
			Preferences.setBodyVelocityEnabled(!Preferences.isBodyVelocityEnabled());
		} else if ("zoom-in".equals(command)) {
			synchronized (Simulation.LOCK) {
				this.simulation.getCamera().zoomIn();
			}
		} else if ("zoom-out".equals(command)) {
			synchronized (Simulation.LOCK) {
				this.simulation.getCamera().zoomOut();
			}
		} else if ("to-origin".equals(command)) {
			synchronized (Simulation.LOCK) {
				this.simulation.getCamera().toOrigin();
			}
		} else if ("about".equals(command)) {
			AboutDialog.show(this);
		} else if ("helpContents".equals(command)) {
			HelpDialog.show(this);
		} else if ("snapshotTake".equals(command)) {
			this.takeSnapshot(false);
		} else if ("snapshotClearAll".equals(command)) {
			// make sure they are sure
			int choice = JOptionPane.showConfirmDialog(this, 
					Messages.getString("dialog.snapshot.clear.text"), 
					Messages.getString("dialog.snapshot.clear.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (choice == JOptionPane.YES_OPTION) {
				// clear the snapshots
				this.clearAllSnapshots();
			}
		} else if ("snapshotRestore".equals(command)) {
			JMenuItem item = (JMenuItem)event.getSource();
			String key = item.getText();
			
			// make sure they are sure
			int choice = JOptionPane.showConfirmDialog(this, 
					MessageFormat.format(Messages.getString("dialog.snapshot.load.text"), key), 
					Messages.getString("dialog.snapshot.load.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (choice == JOptionPane.YES_OPTION) {
				try {
					this.restoreSnapshot(key);
					// stop all actions
					this.endAllActions();
				} catch (Exception e) {
					ExceptionDialog.show(this, 
							Messages.getString("dialog.snapshot.load.error.title"), 
							MessageFormat.format(Messages.getString("dialog.snapshot.load.error.text"), key), 
							e);
				}
			}
		} else if ("preferences".equals(command)) {
			PreferencesDialog.show(this);
			// set the state of the toggle buttons on the preferences toolbar
			this.tglAntiAliasing.setSelected(Preferences.isAntiAliasingEnabled());
			this.tglBodyLabels.setSelected(Preferences.isBodyLabeled());
			this.tglFixtureLabels.setSelected(Preferences.isFixtureLabeled());
			this.tglOriginLabel.setSelected(Preferences.isOriginLabeled());
			this.tglRandomColor.setSelected(Preferences.isBodyColorRandom());
			this.tglStencil.setSelected(Preferences.isBodyStenciled());
			this.tglVerticalSync.setSelected(Preferences.isVerticalSyncEnabled());
			this.tglBounds.setSelected(Preferences.isBoundsEnabled());
			this.tglScale.setSelected(Preferences.isScaleEnabled());
			this.tglBodyCenter.setSelected(Preferences.isBodyCenterEnabled());
			this.tglContactPairs.setSelected(Preferences.isContactPairEnabled());
			this.tglContactPoints.setSelected(Preferences.isContactPointEnabled());
			this.tglContactImpulses.setSelected(Preferences.isContactImpulseEnabled());
			this.tglFrictionImpulses.setSelected(Preferences.isFrictionImpulseEnabled());
			this.tglBodyAABBs.setSelected(Preferences.isBodyAABBEnabled());
			this.tglBodyNormals.setSelected(Preferences.isBodyNormalEnabled());
			this.tglBodyRotationDiscs.setSelected(Preferences.isBodyRotationDiscEnabled());
			this.tglBodyVelocities.setSelected(Preferences.isBodyVelocityEnabled());
		} else if (command.startsWith("test+")) {
			// load the test up
			if (command.endsWith(".xml")) {
				// load a declarative test
				String name = command.substring(command.lastIndexOf("/") + 1, command.length() - 4);
				String file = command.replace("test+", "");
				try {
					this.openTestAction(file, name);
					// stop all actions
					this.endAllActions();
				} catch (Exception e) {
					ExceptionDialog.show(this, 
							Messages.getString("dialog.test.open.error.title"), 
							Messages.getString("dialog.test.open.error.text"), 
							e);
				}
			} else {
				// load a compiled test
				String className = command.replace("test+", "");
				try {
					this.openTestAction(className);
					// stop all actions
					this.endAllActions();
				} catch (Exception e) {
					ExceptionDialog.show(this, 
							Messages.getString("dialog.test.open.error.title"), 
							Messages.getString("dialog.test.open.error.text"), 
							e);
				}
			}
		} else if (command.startsWith("laf+")) {
			// make sure they are sure
			int choice = JOptionPane.showConfirmDialog(this, 
					Messages.getString("dialog.laf.warning.text"), 
					Messages.getString("dialog.laf.warning.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// check the user's choice
			if (choice == JOptionPane.YES_OPTION) {
				// parse out the LAF class name
				String className = command.replace("laf+", "");
				try {
					// attempt to set the look and feel
					UIManager.setLookAndFeel(className);
					// get the current windows open by this application
					Window windows[] = Frame.getWindows();
					// update the ui
			        for(Window window : windows) {
			            SwingUtilities.updateComponentTreeUI(window);
			        }
			        // we need to pack since certain look and feels may have different component
			        // gaps which can cause stuff not to be shown
			        this.pack();
			        // find the item in the menu to set the current one
			        for (Component component : this.mnuLookAndFeel.getPopupMenu().getComponents()) {
			        	JMenuItem item = (JMenuItem)component;
			        	// set the newly selected LAF to have a checked icon
			        	// and the rest to have no icon
			        	if (item.getActionCommand().equalsIgnoreCase(command)) {
			        		item.setIcon(Icons.CHECK);
			        	} else {
			        		item.setIcon(null);
			        	}
			        }
				} catch (Exception e) {
					ExceptionDialog.show(this, 
							Messages.getString("dialog.laf.error.title"), 
							Messages.getString("dialog.laf.error.text"), 
							e);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		
		int[] temp = new int[1];
		gl.glGetIntegerv(GL.GL_STENCIL_BITS, temp, 0);
		if (temp[0] <= 0) {
			// disable the stencil button
			this.tglStencil.setEnabled(false);
		}
		
		// set the matrix mode to projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// initialize the matrix
		gl.glLoadIdentity();
		// set the view to a 2D view
		gl.glOrtho(-400, 400, -300, 300, 0, 1);
		
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix
		gl.glLoadIdentity();
		
		// set the clear color to white
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		// set the stencil clear value
		gl.glClearStencil(0);
		
		// disable depth testing since we are working in 2D
		gl.glDisable(GL.GL_DEPTH_TEST);
		// we dont need lighting either
		gl.glDisable(GL2.GL_LIGHTING);
		
		// enable blending for translucency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				
		// set the swap interval to vertical-sync
		gl.setSwapInterval(1);
		
		// create the text renderer
		this.textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();

		// turn on/off multi-sampling
		if (Preferences.isAntiAliasingEnabled()) {
			gl.glEnable(GL.GL_MULTISAMPLE);
		} else {
			gl.glDisable(GL.GL_MULTISAMPLE);
		}
		
		// turn on/off vertical sync
		if (Preferences.isVerticalSyncEnabled()) {
			gl.setSwapInterval(1);
		} else {
			gl.setSwapInterval(0);
		}
		
		// clear the screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix (0,0) is in the center of the window
		gl.glLoadIdentity();
		
		// the main loop
		synchronized (Simulation.LOCK) {
			// perform other operations at the end (it really
			// doesn't matter if its done at the start or end)
			this.update();
			
			// render the scene
			this.render(gl);
			
			// look for input
			this.poll();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		// nothing to dispose from OpenGL right now
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		
		// resize the ortho view
		
		// set the matrix mode to projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// initialize the matrix
		gl.glLoadIdentity();
		// set the view to a 2D view
		gl.glOrtho(-width / 2.0, width / 2.0, -height / 2.0, height / 2.0, 0, 1);
		
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix
		gl.glLoadIdentity();
		
		// set the size
		this.canvasSize = new Dimension(width, height);
	}

	/**
	 * Updates the world.
	 */
	private void update() {
		// get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
        
        // get the number of single steps
        int steps = 0;
        synchronized (this.btnStep) {
        	steps = this.steps;
        	this.steps = 0;
		}
        
		// check if the state is paused
		if (!this.isPaused()) {
	    	// convert from nanoseconds to seconds
	    	double elapsedTime = (double)diff / NANO_TO_BASE;
	        // update the world with the elapsed time
	    	boolean stepped = this.simulation.getWorld().update(elapsedTime);
	    	// see if its a compiled simulation
	    	if (this.simulation instanceof CompiledSimulation) {
	    		// its compiled
	    		CompiledSimulation cs = (CompiledSimulation)this.simulation;
	    		cs.update(elapsedTime, stepped);
	    	}
		} else if (steps > 0) {
			// if there are some steps to perform then do so
			this.simulation.getWorld().step(steps);
			// see if its a compiled simulation
	    	if (this.simulation instanceof CompiledSimulation) {
	    		// its compiled
	    		CompiledSimulation cs = (CompiledSimulation)this.simulation;
	    		cs.update(steps * this.simulation.getWorld().getStep().getDeltaTime(), true);
	    	}
		}
		
		// see if something changed in the simulation to warrant an update to
		// the simulation JTree panel
		if (this.simulation instanceof CompiledSimulation) {
			CompiledSimulation cs = (CompiledSimulation)this.simulation;
			// we don't want to use isUpdateRequired here since initially (before a step is performed)
			// the isUpdateRequired method will always return true.
			if (cs.isChanged()) {
				this.pnlSimulation.setSimulation(cs);
				// see if the currently selected body still exists
				if (this.selectBodyAction.isActive()) {
					Body b = this.selectBodyAction.getObject();
					boolean exists = false;
					int bSize = cs.getWorld().getBodyCount();
					for (int i = 0; i < bSize; i++) {
						Body q = cs.getWorld().getBody(i);
						if (b == q) {
							exists = true;
							break;
						}
					}
					if (!exists) {
						this.selectBodyAction.end();
						this.selectedBodyJoint = null;
					}
				}
			}
		}
		
		// update the fps text box
		// this will update the Swing components on the EDT internally
		this.updateFps(diff);
	}
	
	/**
	 * Renders the world.
	 * @param gl the OpenGL surface
	 */
	private void render(GL2 gl) {
		World world = this.simulation.getWorld();
		List<SandboxRay> rays = this.simulation.getRays();
		
		Dimension size = this.canvasSize;
		Vector2 offset = this.simulation.getCamera().getTranslation();
		double scale = this.simulation.getCamera().getScale();
		
		// apply a scaling transformation
		gl.glPushMatrix();
		gl.glScaled(scale, scale, scale);
		gl.glTranslated(offset.x, offset.y, 0.0);
		
		// draw all the bodies and the bounds
		
		// draw the bounds
		if (Preferences.isBoundsEnabled()) {
			gl.glColor4fv(Preferences.getBoundsColor(), 0);
			RenderUtilities.drawBounds(gl, world.getBounds());
		}
		
		// render all the bodies in the world
		int bSize = world.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i);
			// dont draw the selected body
			if (body == this.selectBodyAction.getObject()) continue;
			if (body == this.editBodyAction.getObject()) continue;
			
			this.renderBody(gl, body);
		}
		
		// draw all AABBs so that they are on top of all shapes (except for the selected one)
		if (Preferences.isBodyAABBEnabled()) {
			for (int i = 0; i < bSize; i++) {
				SandboxBody body = (SandboxBody)world.getBody(i);
				// dont draw the selected body
				if (body == this.selectBodyAction.getObject()) continue;
				if (body == this.editBodyAction.getObject()) continue;
				
				this.renderAABB(gl, body);
			}
		}
		
		// render the joints
		int jSize = world.getJointCount();
		for (int i = 0; i < jSize; i++) {
			Joint joint = world.getJoint(i);
			// dont draw the mouse joint used during simulation since its drawn again
			if (joint == this.selectedBodyJoint) continue;
			// otherwise draw the joint normally
			RenderUtilities.drawJoint(gl, joint, world.getStep().getInverseDeltaTime());
		}

		// the selected body is rendered by itself
		if (this.selectBodyAction.isActive()) {
			this.renderSelectedBody(gl, this.selectBodyAction.getObject());
		}
		
		// the selected body is rendered by itself
		if (this.editBodyAction.isActive()) {
			this.renderEditingBody(gl, this.editBodyAction.getObject());
		}
		
		// draw the mouse joint last always
		if (this.selectedBodyJoint != null) {
			RenderUtilities.drawMouseJoint(gl, this.selectedBodyJoint, this.simulation.getWorld().getStep().getInverseDeltaTime());
		}
		
		// render contacts, contact impulses, friction impulses, and pairs
		if (Preferences.isContactPairEnabled()
		 || Preferences.isContactImpulseEnabled()
		 || Preferences.isContactPointEnabled()
		 || Preferences.isFrictionImpulseEnabled()) {
			// get the contact counter
			ContactCounter cc = this.simulation.getContactCounter();
			// get the contacts from the counter
			List<ContactPoint> contacts = cc.getContacts();
			
			// loop over the contacts
			int cSize = contacts.size();
			for (int i = 0; i < cSize; i++) {
				// draw the contacts
				ContactPoint cp = contacts.get(i);
				// get the world space contact point
				Vector2 c = cp.getPoint();
				// draw the contact pairs
				if (Preferences.isContactPairEnabled()) {
					// set the color
					gl.glColor4fv(Preferences.getContactPairColor(), 0);
					// get the world space points
					Vector2 p1 = cp.getBody1().getTransform().getTransformed(cp.getFixture1().getShape().getCenter());
					Vector2 p2 = cp.getBody2().getTransform().getTransformed(cp.getFixture2().getShape().getCenter());
					// draw line between the shapes
					RenderUtilities.drawLineSegment(gl, p1, p2, false);
				}
				
				// draw the contact
				if (Preferences.isContactPointEnabled()) {
					// set the color
					gl.glColor4fv(Preferences.getContactPointColor(), 0);
					// draw the contact points
					RenderUtilities.fillRectangleFromCenter(gl, c.x, c.y, 0.05, 0.05);
				}
				
				// check if the contact is a solved contact
				if (cp instanceof SolvedContactPoint) {
					// get the solved contact point to show the impulses applied
					SolvedContactPoint scp = (SolvedContactPoint) cp;
					Vector2 n = scp.getNormal();
					Vector2 t = n.cross(1.0);
					double j = scp.getNormalImpulse();
					double jt = scp.getTangentialImpulse();
					
					// draw the contact forces
					if (Preferences.isContactImpulseEnabled()) {
						// set the color
						gl.glColor4fv(Preferences.getContactImpulseColor(), 0);
						RenderUtilities.drawLineSegment(
								gl, 
								c.x, c.y, 
								c.x + n.x * j, c.y + n.y * j,
								false);
					}
					
					// draw the friction forces
					if (Preferences.isFrictionImpulseEnabled()) {
						// set the color
						gl.glColor4fv(Preferences.getFrictionImpulseColor(), 0);
						RenderUtilities.drawLineSegment(
								gl, 
								c.x, c.y, 
								c.x + t.x * jt, c.y + t.y * jt,
								false);
					}
				}
			}
		}
		
		// draw rays & results
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		for (int i = 0; i < rays.size(); i++) {
			SandboxRay ray = rays.get(i);
			// draw the ray
			// get the ray attributes (world coordinates)
			Vector2 s = ray.getStart();
			Vector2 d = ray.getDirectionVector();
			
			// compute the maximum length (this is the length required
			// to show the ray extending past the screen always to emulate
			// it having infinite length)
			double x = size.getWidth() / scale * 0.5 - (s.x + offset.x);
			double y = size.getHeight() / scale * 0.5 - (s.y + offset.y);
			// we must use the square root to get an accurate length
			double ml = Math.sqrt(x * x + y * y);
			double l = ray.length > 0.0 ? ray.length : ml;
			gl.glColor4f(1.0f, 0.0f, 0.0f, 0.8f);
			// draw the line from the start to the end, along d, l distance
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2d(s.x, s.y);
				gl.glVertex2d(s.x + d.x * l, s.y + d.y * l);
			gl.glEnd();
			
			// perform the raycast
			if (world.raycast(ray, ray.length, ray.sensors, ray.all, results)) {
				// draw the raycast results
				// get the number of results
				int rSize = results.size();
				gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
				// loop over the results
				for (int j = 0; j < rSize; j++) {
					// should always contain at least one result
					RaycastResult result = results.get(j);
					Raycast raycast = result.getRaycast();
					
					// draw the normal and point
					Vector2 point = raycast.getPoint();
					Vector2 normal = raycast.getNormal();
					
					RenderUtilities.fillRectangleFromCenter(gl, point.x, point.y, 0.02, 0.02);
					
					gl.glBegin(GL.GL_LINES);
						gl.glVertex2d(point.x, point.y);
						gl.glVertex2d(point.x + normal.x, point.y + normal.y);
					gl.glEnd();
				}
			}
		}
		
		// draw compiled simulation stuff
    	if (this.simulation instanceof CompiledSimulation) {
    		// its compiled
    		CompiledSimulation cs = (CompiledSimulation) this.simulation;
    		cs.render(gl);
    	}
		
		gl.glPopMatrix();
		
		// draw other stuff
		
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		// draw origin label
		
		if (Preferences.isOriginLabeled()) {
			double ox = offset.x * scale;
			double oy = offset.y * scale;
			
			this.textRenderer.beginRendering(size.width, size.height);
			this.textRenderer.setColor(0.0f, 0.0f, 0.0f, 0.8f);
			this.textRenderer.draw(ORIGIN_LABEL, (int)Math.floor(ox) + size.width / 2 + 3, (int)Math.floor(oy) + size.height / 2 - 12);
			this.textRenderer.endRendering();
			
			gl.glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
			RenderUtilities.fillRectangleFromCenter(gl, ox, oy, 3, 3);
		}

		// draw labels over the origin label
		boolean bodyLabels = Preferences.isBodyLabeled();
		boolean fixtureLabels = Preferences.isFixtureLabeled();
		if ((bodyLabels || fixtureLabels) && !this.editBodyAction.isActive()) {
			// begin rendering text
			this.textRenderer.beginRendering(size.width, size.height);
			// set the color
			this.textRenderer.setColor(0.0f, 0.0f, 0.0f, 0.8f);
			
			// render all the bodies in the world
			bSize = world.getBodyCount();
			for (int i = 0; i < bSize; i++) {
				SandboxBody body = (SandboxBody)world.getBody(i);
				// get the center point
				Vector2 c = body.getWorldCenter();
				
				int x, y;
				if (bodyLabels) {
					// compute the screen coordinates
					x = (int)Math.floor((c.x + offset.x) * scale) + size.width / 2 + 3;
					y = (int)Math.floor((c.y + offset.y) * scale) + size.height / 2 - 12;
					
					this.textRenderer.draw(body.getName(), x, y);
					this.textRenderer.draw(RenderUtilities.formatVector2(c), x, y - 16);
				}
				
				if (fixtureLabels) {
					Transform tx = body.getTransform();
					int fSize = body.getFixtureCount();
					for (int j = 0; j < fSize; j++) {
						BodyFixture bf = body.getFixture(j);
						Vector2 lc = bf.getShape().getCenter();
						Vector2 wc = tx.getTransformed(lc);
						
						x = (int)Math.floor((wc.x + offset.x) * scale) + size.width / 2 + 3;
						y = (int)Math.floor((wc.y + offset.y) * scale) + size.height / 2 - 12;
						
						this.textRenderer.draw((String)bf.getUserData(), x, y);
						this.textRenderer.draw(RenderUtilities.formatVector2(wc), x, y - 16);
					}
				}
			}
			
			this.textRenderer.endRendering();
		}
		
		// draw HUD like stuff
		
		if (Preferences.isScaleEnabled()) {
			// translate (0, 0) to the bottom left corner
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslated(-size.getWidth() * 0.5, -size.getHeight() * 0.5, 0.0);
			
			final int x = 5;
			final int y = 18;
			// line width
			final int lw = 3;
			final int w = 100;
			final int h = 15;
			// text height
			final int th = 7;
			
			// set the line width
			float olw = RenderUtilities.setLineWidth(gl, lw);
			
			// compute the offset due to the line width
			final int o = (lw - 1) / 2;
			double d = (double)w / scale;
			
			// draw a line downward
			gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2i(x + o, y - h - o + th);
				gl.glVertex2i(x + o, y);
				
				gl.glVertex2i(x + o, y - o);
				gl.glVertex2i(x + w - o, y - o);
				
				gl.glVertex2i(x + w - o, y);
				gl.glVertex2i(x + w - o, y - h - o + th);
			gl.glEnd();
			
			// reset the line width back to what it was
			RenderUtilities.setLineWidth(gl, olw);
			
			// show the bounding box for testing
			//gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			//RenderUtilities.drawRectangleFromTopLeft(gl, x, y, w, h, false);
			
			gl.glPopMatrix();
			
			this.textRenderer.beginRendering(size.width, size.height);
			this.textRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			
			// show the number of pixels per meter
			if (scale < 1.0) {
				this.textRenderer.draw(MessageFormat.format(SCALE_CONVERSION_FRACTION, scale), x + (2 * o) + 8, y + 4);
			} else {
				this.textRenderer.draw(MessageFormat.format(SCALE_CONVERSION_WHOLE, scale), x + (2 * o) + 8, y + 4);
			}
			
			// show the number of meters per scale
			this.textRenderer.draw(MessageFormat.format(SCALE_LENGTH, d), x + (2 * o) + 8, y - (2 * o) - 12);
			
			this.textRenderer.endRendering();
		}
		
		gl.glPopMatrix();
	}
	
	/**
	 * Renders a body and related state normally.
	 * @param gl the OpenGL context
	 * @param body the body to render
	 */
	private void renderBody(GL2 gl, SandboxBody body) {
		// apply the transform
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, body.getTransform());
		
		// render the body
		// check for multiple fixtures
		if (body.getFixtureCount() > 1) {
			// check for stenciling
			if (Preferences.isBodyStenciled()) {
				body.stencil(gl);
			} else {
				body.render(gl);
			}
		} else {
			// render normally
			body.render(gl);
		}

		// render any edge normals
		if (Preferences.isBodyNormalEnabled()) {
			body.renderNormals(gl);
		}
		
		// render any edge normals
		if (Preferences.isBodyRotationDiscEnabled()) {
			body.renderRotationDisc(gl);
		}
		
		// render the center of mass
		if (Preferences.isBodyCenterEnabled()) {
			body.renderCenter(gl);
		}

		RenderUtilities.popTransform(gl);
		
		// render the velocities
		if (Preferences.isBodyVelocityEnabled()) {
			body.renderVelocity(gl);
		}
	}
	
	/**
	 * Renders a selected body and related state.
	 * @param gl the OpenGL context
	 * @param body the body to render
	 */
	private void renderSelectedBody(GL2 gl, SandboxBody body) {
		double scale = this.simulation.getCamera().getScale();
		
		// render the aabb
		if (Preferences.isBodyAABBEnabled()) {
			this.renderAABB(gl, body);
		}
		
		// apply the transform
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, body.getTransform());
		
		// render the body
		// check for multiple fixtures
		if (body.getFixtureCount() > 1) {
			// check for stenciling
			if (Preferences.isBodyStenciled()) {
				// stenciling requires a larger radius
				RenderUtilities.outlineShapes(gl, body, 6, Preferences.getSelectedColor(), scale);
				body.setFillColor(gl);
				body.fill(gl);
			} else {
				RenderUtilities.outlineShapes(gl, body, 4, Preferences.getSelectedColor(), scale);
				body.render(gl);
			}
		} else {
			RenderUtilities.outlineShapes(gl, body, 4, Preferences.getSelectedColor(), scale);
			body.render(gl);
		}

		// render any edge normals
		if (Preferences.isBodyNormalEnabled()) {
			body.renderNormals(gl);
		}
		
		// render any edge normals
		if (Preferences.isBodyRotationDiscEnabled()) {
			body.renderRotationDisc(gl);
		}
		
		// render the center of mass
		if (Preferences.isBodyCenterEnabled()) {
			body.renderCenter(gl);
		}

		RenderUtilities.popTransform(gl);
		
		// render the velocities
		if (Preferences.isBodyVelocityEnabled()) {
			body.renderVelocity(gl);
		}
	}
	
	/**
	 * Renders an editing body and related state.
	 * @param gl the OpenGL context
	 * @param body the body to render
	 */
	private void renderEditingBody(GL2 gl, SandboxBody body) {
		Dimension size = this.canvasSize;

		// overlay everything but this shape
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		RenderUtilities.fillRectangleFromTopLeft(gl, -size.width * 0.5, size.height * 0.5, size.width, size.height);
		gl.glPopMatrix();
		
		// render the aabb
		if (Preferences.isBodyAABBEnabled()) {
			this.renderAABB(gl, body);
		}
		
		// apply the transform
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, body.getTransform());
		
		// redraw the shape on top of the overlay
		body.render(gl);
		
		// see if a fixture is selected
		if (this.selectFixtureAction.isActive()) {
			// render the shape being selected only
			BodyFixture bf = this.selectFixtureAction.getObject();
			Convex convex = bf.getShape();
			RenderUtilities.outlineShape(gl, convex, 4, Preferences.getSelectedColor());
			gl.glColor4fv(body.getFillColor(), 0);
			RenderUtilities.fillShape(gl, convex);
			gl.glColor4fv(body.getOutlineColor(), 0);
			RenderUtilities.drawShape(gl, convex, false);
		}

		// render any edge normals
		if (Preferences.isBodyNormalEnabled()) {
			body.renderNormals(gl);
		}
		
		// render any edge normals
		if (Preferences.isBodyRotationDiscEnabled()) {
			body.renderRotationDisc(gl);
		}
		
		// render the center of mass
		if (Preferences.isBodyCenterEnabled()) {
			body.renderCenter(gl);
		}

		RenderUtilities.popTransform(gl);

		// render the velocities
		if (Preferences.isBodyVelocityEnabled()) {
			body.renderVelocity(gl);
		}
	}
	
	/**
	 * Renders the AABB for the given body.
	 * @param gl the OpenGL context
	 * @param body the body
	 */
	private void renderAABB(GL2 gl, SandboxBody body) {
		gl.glColor4fv(Preferences.getBodyAABBColor(), 0);
		AABB aabb = this.simulation.getWorld().getBroadphaseDetector().getAABB(body);
		if (aabb != null) {
			RenderUtilities.drawRectangleFromStartToEnd(
					gl,
					aabb.getMinX(), aabb.getMinY(),
					aabb.getMaxX(), aabb.getMaxY(),
					false);
		}
	}
	
	/**
	 * Polls for input from the user.
	 */
	private void poll() {
		Camera camera = this.simulation.getCamera();
		World world = this.simulation.getWorld();
		
		Dimension size = this.canvasSize;
		Vector2 offset = camera.getTranslation();
		double scale = camera.getScale();
		
		// see if the user has zoomed in or not
		if (this.mouse.hasScrolled()) {
			// get the scroll amount
			int scroll = this.mouse.getScrollAmount();
			// zoom in or out
			if (scroll < 0) {
				camera.zoomOut();
			} else {
				camera.zoomIn();
			}
		}
		
		// get the mouse location
		Point p = this.mouse.getLocation();
		if (p == null) {
			p = new Point();
		}
		Vector2 pw = this.screenToWorld(p, size, offset, scale);
		
		// update the mouse location
		if (this.mouse.hasMoved()) {
			this.lblMouseLocation.update(pw);
		}
		
		// the mouse button 1 or 3 was clicked
		if (this.mouse.wasClicked(MouseEvent.BUTTON1) || this.mouse.wasClicked(MouseEvent.BUTTON3)) {
			// check if a body is already clicked
			if (this.selectBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.selectBodyAction.getObject();
				// get the fixture
				BodyFixture fixture = this.getFixtureAtPoint(body, pw);
				// make sure the click was inside the same body
				if (fixture == null) {
					// otherwise de-select the body
					this.selectBodyAction.end();
					this.editBodyAction.end();
				}
			} else if (this.editBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.editBodyAction.getObject();
				// get the fixture
				BodyFixture fixture = this.getFixtureAtPoint(body, pw);
				// see if a fixture was clicked
				if (fixture != null) {
					// start the select fixture action
					this.selectFixtureAction.begin(fixture);
				} else {
					// end the edit body action
					this.editBodyAction.end();
					this.selectFixtureAction.end();
				}
			} else {
				// dont allow selection of any kind unless its mouse button 1 (creates
				// a mouse joint if running) or the simulation is paused
				if (this.mouse.wasClicked(MouseEvent.BUTTON1) || this.isPaused()) {
					// otherwise see if a body is being selected
					SandboxBody body = this.getBodyAtPoint(world, pw);
					// check for null
					if (body != null) {
						// begin the select body action
						this.selectBodyAction.begin(body);
					}
				}
			}
		}
		
		// the mouse button 1 is pressed and being held
		if (this.mouse.isPressed(MouseEvent.BUTTON1)) {
			// check if a body is already selected
			if (this.selectBodyAction.isActive() && this.moveBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.selectBodyAction.getObject();
				// check if the world is running
				if (this.isPaused()) {
					// get the difference
					Vector2 tx = pw.difference(this.moveBodyAction.getBeginPosition());
					// move the body with the mouse
					body.translate(tx);
					// update the broadphase to update the AABB
					// this is only done here to show a valid AABB to the user while they are editing
					// the AABB is updated every frame so typically you wouldnt need to do this
					world.getBroadphaseDetector().update(body);
					// update the action
					this.moveBodyAction.update(pw);
				} else {
					// update the mouse joint's target point
					if (this.selectedBodyJoint == null) {
						// get the mass of the body
						double mass = body.getMass().getMass();
						if (mass <= Epsilon.E) {
							// if the mass is zero, attempt to use the inertia
							mass = body.getMass().getInertia();
						}
						this.selectedBodyJoint = new MouseJoint(body, pw, 4.0, 0.7, 1000.0 * mass);
						synchronized (Simulation.LOCK) {
							world.addJoint(this.selectedBodyJoint);
						}
					} else {
						this.selectedBodyJoint.setTarget(pw);
					}
				}
			} else if (this.moveWorldAction.isActive()) {
				// then translate the offset
				// we need to get the current position in world coordinates using the old offset
				Vector2 pwt = this.screenToWorld(p, size, this.moveWorldAction.getOffset(), scale);
				// compute the difference in the new position to get the offset
				Vector2 tx = pwt.difference(this.moveWorldAction.getBeginPosition());
				// apply it to the offset
				camera.translate(tx);
				// update the new mouse position
				this.moveWorldAction.update(pwt);
			} else if (this.editBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.editBodyAction.getObject();
				// see if the move fixture action is active
				if (this.selectFixtureAction.isActive() && this.moveFixtureAction.isActive()) {
					// get the fixture
					BodyFixture bf = this.selectFixtureAction.getObject();
					// get the local pw
					Vector2 lpw = body.getTransform().getInverseTransformed(pw);
					// get the difference
					Vector2 tx = lpw.difference(this.moveFixtureAction.getBeginPosition());
					// move the body with the mouse
					bf.getShape().translate(tx);
					// update the mass since the inertia, COM, and rotation disc may change
					body.setMass(body.getMass().getType());
					// update the broadphase to update the AABB
					// this is only done here to show a valid AABB to the user while they are editing
					// the AABB is updated every frame so typically you wouldnt need to do this
					world.getBroadphaseDetector().update(body);
					// update the action
					this.moveFixtureAction.update(lpw);
				} else {
					// get the current fixture that the mouse is on
					BodyFixture fixture = this.getFixtureAtPoint(body, pw);
					// see if a fixture was clicked
					if (fixture != null) {
						// start the select fixture action
						this.selectFixtureAction.begin(fixture);
						this.moveFixtureAction.begin(body.getTransform().getInverseTransformed(pw));
					} else {
						// end the edit body action
						this.editBodyAction.end();
						this.selectFixtureAction.end();
					}
				}
			} else {
				// otherwise see if a body is being selected
				SandboxBody body = this.getBodyAtPoint(world, pw);
				// check for null
				if (body != null) {
					// begin the select body action
					this.selectBodyAction.begin(body);
					// begin the move body action
					this.moveBodyAction.begin(pw);
					this.editBodyAction.end();
					// set the body to awake and active
					body.setAsleep(false);
					body.setActive(true);
				} else {
					// then assume the user wants to move the world
					this.moveWorldAction.begin(offset.copy(), pw, this.canvas);
				}
			}
		}
		
		// the mouse button 1 was double clicked
		if (this.mouse.wasDoubleClicked(MouseEvent.BUTTON1)) {
			// dont allow editing during simulation
			if (this.isPaused()) {
				SandboxBody body = this.getBodyAtPoint(world, pw);
				// check for null
				if (body != null) {
					// end the body selection action
					this.selectBodyAction.end();
					// begin the edit body action
					this.editBodyAction.begin(body);
					// set the body to awake and active
					body.setAsleep(false);
					body.setActive(true);
				} else {
					// then assume the user is done editing fixtures on the body
					this.editBodyAction.end();
				}
			}
		}
		
		// the mouse button 3 is pressed and being held
		if (this.mouse.isPressed(MouseEvent.BUTTON3)) {
			// check if a body is already selected
			if (this.selectBodyAction.isActive() && this.rotateBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.selectBodyAction.getObject();
				// get the rotation
				Vector2 c = body.getWorldCenter();
				Vector2 v1 = c.to(this.rotateBodyAction.getBeginPosition());
				Vector2 v2 = c.to(pw);
				double theta = v1.getAngleBetween(v2);
				// move the body with the mouse
				body.rotate(theta, c);
				// update the broadphase to update the AABB
				// this is only done here to show a valid AABB to the user while they are editing
				// the AABB is updated every frame so typically you wouldnt need to do this
				world.getBroadphaseDetector().update(body);
				// update the action
				this.rotateBodyAction.update(pw);
			} else if (this.editBodyAction.isActive()) {
				// get the body
				SandboxBody body = this.editBodyAction.getObject();
				// see if the move fixture action is active
				if (this.selectFixtureAction.isActive() && this.rotateFixtureAction.isActive()) {
					// get the fixture
					BodyFixture bf = this.selectFixtureAction.getObject();
					Convex convex = bf.getShape();
					Vector2 c = convex.getCenter();
					Vector2 lpw = body.getTransform().getInverseTransformed(pw);
					// get the rotation
					Vector2 v1 = c.to(this.rotateFixtureAction.getBeginPosition());
					Vector2 v2 = c.to(lpw);
					double theta = v1.getAngleBetween(v2);
					// rotate the fixture about the local center
					bf.getShape().rotate(theta, convex.getCenter());
					// update the mass since the inertia, COM, and rotation disc may change
					body.setMass(body.getMass().getType());
					// update the broadphase to update the AABB
					// this is only done here to show a valid AABB to the user while they are editing
					// the AABB is updated every frame so typically you wouldnt need to do this
					world.getBroadphaseDetector().update(body);
					// update the action
					this.rotateFixtureAction.update(lpw);
				} else {
					// get the current fixture that the mouse is on
					BodyFixture fixture = this.getFixtureAtPoint(body, pw);
					// see if a fixture was clicked
					if (fixture != null) {
						// start the select fixture action
						this.selectFixtureAction.begin(fixture);
						this.rotateFixtureAction.begin(body.getTransform().getInverseTransformed(pw));
					} else {
						// end the edit body action
						this.editBodyAction.end();
						this.selectFixtureAction.end();
					}
				}
			} else {
				// dont allow rotating while simulating
				if (this.isPaused()) {
					// otherwise see if a body is being selected
					SandboxBody body = this.getBodyAtPoint(world, pw);
					// check for null
					if (body != null) {
						// begin the select body action
						this.selectBodyAction.begin(body);
						// begin the move body action
						this.rotateBodyAction.begin(pw);
						// set the body to awake and active
						body.setAsleep(false);
						body.setActive(true);
					}
				}
			}
		}
		
		// check if the mouse button 1 was released
		if (this.mouse.wasReleased(MouseEvent.BUTTON1)) {
			if (this.moveBodyAction.isActive()) {
				// end the action
				this.moveBodyAction.end();
				// end the move body joint
				if (this.selectedBodyJoint != null) {
					world.removeJoint(this.selectedBodyJoint);
					this.selectedBodyJoint = null;
				}
			}
			if (this.moveFixtureAction.isActive()) {
				SandboxBody body = this.editBodyAction.getObject();
				// recompute the mass if the position changes
				body.setMass(body.getMass().getType());
				this.moveFixtureAction.end();
			}
			if (this.moveWorldAction.isActive()) {
				// end the action
				this.moveWorldAction.end(this.canvas);
			}
		}
		
		// check if the mouse button 3 was released
		if (this.mouse.wasReleased(MouseEvent.BUTTON3)) {
			if (this.rotateBodyAction.isActive()) {
				// end the action
				this.rotateBodyAction.end();
			}
			if (this.rotateFixtureAction.isActive()) {
				this.rotateFixtureAction.end();
			}
		}
		
		this.mouse.clear();
	}
	
	/**
	 * Returns true if the simulation is paused.
	 * @return boolean
	 */
	private synchronized boolean isPaused() {
		return this.paused;
	}
	
	/**
	 * Sets the paused flag of the simulation.
	 * @param flag true if the simulation should be paused
	 */
	private synchronized void setPaused(boolean flag) {
		this.paused = flag;
		if (!flag) {
			// if the simulation is being unpaused then
			// reset the last time
			this.last = System.nanoTime();
		}
	}
	
	/**
	 * Returns the window title containing the sandbox and dyn4j versions.
	 * @return String
	 */
	private String getWindowTitle() {
		return MessageFormat.format(Messages.getString("title"), VERSION, Version.getVersion());
	}
	
	/**
	 * Returns the first body in the world's body list to contain the given point.
	 * <p>
	 * If no body is found at the given point, null is returned.
	 * @param world the world
	 * @param point the world space point
	 * @return {@link SandboxBody}
	 */
	private SandboxBody getBodyAtPoint(World world, Vector2 point) {
		int bSize = world.getBodyCount();
		// check if there is already a selected body
		if (this.selectBodyAction.isActive()) {
			// if so, then check that body first
			SandboxBody body = this.selectBodyAction.getObject();
			if (this.contains(body, point)) {
				return body;
			}
		}
		// if the selected body doesnt contain the point
		// then check the other bodies in the world
		// loop over all the bodies in the world
		for (int i = bSize - 1; i >= 0; i--) {
			Body body = world.getBody(i);
			// does the body contain the point
			if (contains(body, point)) {
				return (SandboxBody) body;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the given body contains the given point.
	 * @param point the world space point
	 * @param body the body
	 * @return boolean
	 */
	private boolean contains(Body body, Vector2 point) {
		Transform transform = body.getTransform();
		int fSize = body.getFixtureCount();
		// loop over the body fixtures
		for (int j = fSize - 1; j >= 0; j--) {
			BodyFixture bodyFixture = body.getFixture(j);
			Convex convex = bodyFixture.getShape();
			if (contains(convex, transform, point)) {
				// return the first body who contains a fixture
				// that contains the given point
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the fixture of the given body that the given point is inside.
	 * @param point the world space point
	 * @param body the body
	 * @return BodyFixture
	 */
	private BodyFixture getFixtureAtPoint(Body body, Vector2 point) {
		Transform transform = body.getTransform();
		int fSize = body.getFixtureCount();
		// check if a fixture is already selected
		if (this.selectFixtureAction.isActive()) {
			// if so, then check the selected fixture first
			BodyFixture bodyFixture = this.selectFixtureAction.getObject();
			Convex convex = bodyFixture.getShape();
			if (contains(convex, transform, point)) {
				return bodyFixture;
			}
		}
		// otherwise check the other fixtures on the body
		// loop over the body fixtures
		for (int j = fSize - 1; j >= 0; j--) {
			BodyFixture bodyFixture = body.getFixture(j);
			Convex convex = bodyFixture.getShape();
			if (contains(convex, transform, point)) {
				// return the first body who contains a fixture
				// that contains the given point
				return bodyFixture;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the given convex shape contains the given point.
	 * <p>
	 * This method uses a radial expansion for segment shapes.
	 * @param convex the convex point
	 * @param transform the convex's transform
	 * @param point the point
	 * @return boolean
	 */
	private boolean contains(Convex convex, Transform transform, Vector2 point) {
		double scale = this.simulation.getCamera().getScale();
		if (convex instanceof Segment) {
			Segment segment = (Segment)convex;
			return segment.contains(point, transform, 0.2 * 32.0 / scale);
		}
		return convex.contains(point, transform);
	}
	
	/**
	 * Converts from screen coordinates to world coordinates.
	 * @param p the screen point
	 * @param size the canvas size
	 * @param offset the canvas offset
	 * @param scale the screen to world scale factor
	 * @return Vector2 the world point
	 */
	private Vector2 screenToWorld(Point p, Dimension size, Vector2 offset, double scale) {
		Vector2 v = new Vector2();
		double x = p.x;
		double y = p.y;
		double w = size.getWidth();
		double h = size.getHeight();
		double ox = offset.x;
		double oy = offset.y;
		v.x = (x - w * 0.5) / scale - ox;
		v.y = -((y - h * 0.5) / scale + oy);
		return v;
	}
	
	/**
	 * Clears all the saved snapshots.
	 */
	private void clearAllSnapshots() {
		// remove the entries from the map
		this.snapshots.clear();
		// remove all components after the third one
		while (this.mnuSnapshot.getItemCount() > 3) {
			this.mnuSnapshot.remove(3);
		}
	}
	
	/**
	 * Saves the current simulation state to an xml file.
	 * @throws IOException thrown if an IO error occurs
	 */
	private void saveSimulationAction() throws IOException {
		String xml = "";
		String name = Simulation.DEFAULT_SIMULATION_NAME;
		synchronized (Simulation.LOCK) {
			xml = XmlGenerator.toXml(this.simulation);
			name = this.simulation.getWorld().getUserData().toString();
		}
		XmlFormatter formatter = new XmlFormatter(2);
		xml = formatter.format(xml);
		
		// create a class to show a "are you sure" message when over writing an existing file
		JFileChooser fileBrowser = new JFileChooser() {
			/** The version id */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see javax.swing.JFileChooser#approveSelection()
			 */
			@Override
			public void approveSelection() {
				if (getDialogType() == SAVE_DIALOG) {
					File selectedFile = getSelectedFile();
					if ((selectedFile != null) && selectedFile.exists()) {
						int response = JOptionPane.showConfirmDialog(
										this,
										MessageFormat.format(Messages.getString("dialog.save.warning.text"), selectedFile.getName()),
										Messages.getString("dialog.save.warning.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
						if (response != JOptionPane.YES_OPTION)
							return;
					}
				}

				super.approveSelection();
			}
		};
		fileBrowser.setMultiSelectionEnabled(false);
		fileBrowser.setDialogTitle(Messages.getString("dialog.save.title"));
		if (this.directory != null) {
			fileBrowser.setCurrentDirectory(this.directory);
		}
		if (this.currentFileName != null) {
			fileBrowser.setSelectedFile(new File(this.currentFileName));
		} else {
			fileBrowser.setSelectedFile(new File(Messages.getString("dialog.save.defaultFileName")));
		}
		int option = fileBrowser.showSaveDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileBrowser.getSelectedFile();
			this.directory = file.getParentFile();
			this.currentFileName = file.getName();
			// see if its a new one or it already exists
			if (file.exists()) {
				// overwrite the file
				FileWriter fw = new FileWriter(file);
				fw.write(xml);
				fw.close();
				// set the window title
				this.setTitle(this.getWindowTitle() + " - " + name);
				JOptionPane.showMessageDialog(this, 
						Messages.getString("dialog.save.success.text"), 
						Messages.getString("dialog.save.success.title"), 
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				// create a new file
				if (file.createNewFile()) {
					FileWriter fw = new FileWriter(file);
					fw.write(xml);
					fw.close();
					// set the window title
					this.setTitle(this.getWindowTitle() + " - " + name);
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.save.success.text"), 
							Messages.getString("dialog.save.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	
	/**
	 * Attempts to open and load the simulation state from the user selected file.
	 * @throws ParserConfigurationException thrown if an error occurs in configuring the SAX parser
	 * @throws SAXException thrown if any parsing error is encountered
	 * @throws IOException thrown if an IO error occurs  
	 */
	private void openSimulationAction() throws ParserConfigurationException, SAXException, IOException {
		JFileChooser fileBrowser = new JFileChooser();
		fileBrowser.setDialogTitle(Messages.getString("dialog.open.title"));
		fileBrowser.setMultiSelectionEnabled(false);
		if (this.directory != null) {
			fileBrowser.setCurrentDirectory(this.directory);
		}
		int option = fileBrowser.showOpenDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			// get the selected file
			File file = fileBrowser.getSelectedFile();
			// make sure it exists and its a file
			if (file.exists() && file.isFile()) {
				// if it exists and its a file then save the location
				// and make sure they really want to open the file
				this.directory = file.getParentFile();
				this.currentFileName = file.getName();
				// make sure they are sure
				option = JOptionPane.showConfirmDialog(this, 
						Messages.getString("dialog.open.warning.text"), 
						MessageFormat.format(Messages.getString("dialog.open.warning.title"), file.getName()), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (option == JOptionPane.YES_OPTION) {
					// set the name to the default name for now
					String name = Simulation.DEFAULT_SIMULATION_NAME;
					// read the file
					Simulation simulation = XmlReader.fromXml(file);
					synchronized (Simulation.LOCK) {
						// assign the simulation
						this.simulation = simulation;
						// get the world name
						name = this.simulation.getWorld().getUserData().toString();
						// update the simulation tree
						this.pnlSimulation.setSimulation(this.simulation);
						// update the contact panel
						this.pnlContacts.setContactCounter(this.simulation.getContactCounter());
					}
					// clear the snapshots
					this.clearAllSnapshots();
					// set the window title
					this.setTitle(this.getWindowTitle() + " - " + name);
					// disable/enable stuff because of the compiled test
					this.setCompiledSimulation(false);
				}
			} else {
				JOptionPane.showMessageDialog(this, 
						Messages.getString("dialog.open.selection.error.text"), 
						Messages.getString("dialog.open.selection.error.title"), 
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Attempts to open and load the simulation state from the user selected test.
	 * @param file the path of the test
	 * @param name the name of the test
	 * @throws ParserConfigurationException thrown if an error occurs in configuring the SAX parser
	 * @throws SAXException thrown if any parsing error is encountered
	 * @throws IOException thrown if an IO error occurs  
	 */
	private void openTestAction(String file, String name) throws ParserConfigurationException, SAXException, IOException {
		// if it exists and its a file then save the location
		// and make sure they really want to open the file
		this.currentFileName = name + ".xml";
		// make sure they are sure
		int option = JOptionPane.showConfirmDialog(this, 
				Messages.getString("dialog.test.open.warning.text"),
				MessageFormat.format(Messages.getString("dialog.test.open.warning.title"), name), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (option == JOptionPane.YES_OPTION) {
			// read the file into a stream
			String simName = Simulation.DEFAULT_SIMULATION_NAME;
			synchronized (Simulation.LOCK) {
				// read the file
				this.simulation = XmlReader.fromXml(this.getClass().getResourceAsStream(file));
				// get the world name
				simName = this.simulation.getWorld().getUserData().toString();
				// update the simulation tree
				this.pnlSimulation.setSimulation(this.simulation);
				// update the contact panel
				this.pnlContacts.setContactCounter(this.simulation.getContactCounter());
			}
			// clear the snapshots
			this.clearAllSnapshots();
			// set the window title
			this.setTitle(this.getWindowTitle() + " - " + simName);
			// disable/enable stuff because of the compiled test
			this.setCompiledSimulation(false);
		}
	}
	
	/**
	 * Called when a compiled tests is to be opened.
	 * @param className the class name of the compiled test
	 * @throws ClassNotFoundException thrown if the given class is not found
	 * @throws InstantiationException thrown if the given class could not be instantiated
	 * @throws IllegalAccessException thrown if the class is private
	 */
	private void openTestAction(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String name = className.substring(className.lastIndexOf(".") + 1);
		// make sure they are sure
		int option = JOptionPane.showConfirmDialog(this, 
				Messages.getString("dialog.test.open.warning.text"),
				MessageFormat.format(Messages.getString("dialog.test.open.warning.title"), name), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (option == JOptionPane.YES_OPTION) {
			// attempt to load the class
			Class<?> clazz = Class.forName(className);
			// attempt to create an instance of it
			CompiledSimulation simulation = (CompiledSimulation) clazz.newInstance();
			synchronized (Simulation.LOCK) {
				// set the simulation
				this.simulation = simulation;
				// update the simulation tree
				this.pnlSimulation.setSimulation(this.simulation);
				// update the contact panel
				this.pnlContacts.setContactCounter(this.simulation.getContactCounter());
			}
			// clear the snapshots
			this.clearAllSnapshots();
			// set the window title
			this.setTitle(this.getWindowTitle() + " - " + name);
			// disable/enable stuff because of the compiled test
			this.setCompiledSimulation(true);
		}
	}
	
	/**
	 * Saves a snapshot of the simulation.
	 * @param auto true if the snapshot is an automatic snapshot
	 */
	private void takeSnapshot(boolean auto) {
		// get the xml for the current simulation
		String xml = "";
		synchronized (Simulation.LOCK) {
			xml = XmlGenerator.toXml(this.simulation);
		}
		// save it in the snapshot map using the timestamp as the key
		Date date = new Date();
		String key;
		if (auto) {
			key = MessageFormat.format(Messages.getString("menu.snapshot.auto"), date);
		} else {
			key = MessageFormat.format(Messages.getString("menu.snapshot.manual"), date);
		}
		// add it to the map
		this.snapshots.put(key, xml);
		
		JMenuItem mnuShot = new JMenuItem(key);
		mnuShot.setActionCommand("snapshotRestore");
		mnuShot.addActionListener(this);
		mnuShot.setIcon(Icons.SNAPSHOT);
		this.mnuSnapshot.add(mnuShot);
	}
	
	/**
	 * Restores the given snapshot.
	 * @param snapshot the snapshot key
	 * @throws ParserConfigurationException thrown if an error occurs in configuring the SAX parser
	 * @throws SAXException thrown if any parsing error is encountered
	 * @throws IOException thrown if an IO error occurs  
	 */
	private void restoreSnapshot(String snapshot) throws ParserConfigurationException, SAXException, IOException {
		String xml = this.snapshots.get(snapshot);
		// read the file into a stream
		String name = Simulation.DEFAULT_SIMULATION_NAME;
		synchronized (Simulation.LOCK) {
			// save the old camera
			Camera oldCamera = this.simulation.getCamera();
			// read the previous snapshot
			Simulation simulation = XmlReader.fromXml(xml);
			// set the older camera
			simulation.getCamera().setScale(oldCamera.getScale());
			simulation.getCamera().setTranslation(oldCamera.getTranslation());
			// set the simulation
			this.simulation = simulation;
			// get the world name
			name = this.simulation.getWorld().getUserData().toString();
			// update the simulation tree
			this.pnlSimulation.setSimulation(this.simulation);
			// update the contact panel
			this.pnlContacts.setContactCounter(this.simulation.getContactCounter());
		}
		// set the window title
		this.setTitle(this.getWindowTitle() + " - " + name);
	}
	
	/**
	 * Saves the current simulation to a java file.
	 * @throws IOException thrown if an IO error occurs
	 */
	private void exportToJavaCode() throws IOException {
		// create a class to show a "are you sure" message when over writing an existing file
		JFileChooser fileBrowser = new JFileChooser() {
			/** The version id */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see javax.swing.JFileChooser#approveSelection()
			 */
			@Override
			public void approveSelection() {
				if (getDialogType() == SAVE_DIALOG) {
					File selectedFile = getSelectedFile();
					if (selectedFile != null) {
						if (selectedFile.exists()) {
							int response = JOptionPane.showConfirmDialog(
											this,
											MessageFormat.format(Messages.getString("dialog.save.warning.text"), selectedFile.getName()),
											Messages.getString("dialog.save.warning.title"),
											JOptionPane.YES_NO_OPTION,
											JOptionPane.WARNING_MESSAGE);
							// if they're not ok with overwriting the file
							if (response != JOptionPane.YES_OPTION)	return;
						}
						// check the file name
						
						// remove the .java from the name
						String name = selectedFile.getName();
						if (name.contains(".")) {
							name = name.substring(0, name.lastIndexOf('.'));
						}
						
						// check the name
						boolean valid = true;
						if (!Character.isJavaIdentifierStart(name.codePointAt(0))) {
							// the first character is not valid
							valid = false;
						}
						// loop through the rest of the name
						for (int i = 1; i < name.length(); i++) {
							if (!Character.isJavaIdentifierPart(name.codePointAt(i))) {
								valid = false;
								break;
							}
						}
						if (!valid) {
							JOptionPane.showMessageDialog(
									this,
									MessageFormat.format(Messages.getString("dialog.export.java.invalidName.text"), selectedFile.getName()),
									Messages.getString("dialog.export.java.invalidName.title"),
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}

				super.approveSelection();
			}
		};
		fileBrowser.setMultiSelectionEnabled(false);
		fileBrowser.setDialogTitle(Messages.getString("dialog.export.java.title"));
		if (this.directory != null) {
			fileBrowser.setCurrentDirectory(this.directory);
		}
		fileBrowser.setSelectedFile(new File(Messages.getString("dialog.export.java.defaultFileName")));
		int option = fileBrowser.showSaveDialog(this);
		// check the option
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileBrowser.getSelectedFile();
			this.directory = file.getParentFile();
			// remove the .java from the name
			String name = file.getName();
			if (name.contains(".")) {
				name = name.substring(0, name.lastIndexOf('.'));
			}
			// get the class name from the file name
			String contents;
			synchronized (Simulation.LOCK) {
				contents = CodeExporter.export(name, this.simulation.getWorld());
			}
			// see if its a new one or it already exists
			if (file.exists()) {
				// overwrite the file
				FileWriter fw = new FileWriter(file);
				fw.write(contents);
				fw.close();
				JOptionPane.showMessageDialog(this, 
						Messages.getString("dialog.save.success.text"), 
						Messages.getString("dialog.save.success.title"), 
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				// create a new file
				if (file.createNewFile()) {
					FileWriter fw = new FileWriter(file);
					fw.write(contents);
					fw.close();
					JOptionPane.showMessageDialog(this, 
							Messages.getString("dialog.save.success.text"), 
							Messages.getString("dialog.save.success.title"), 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	
	/**
	 * Ends all actions (excluding the moveWorldAction).
	 */
	private void endAllActions() {
		this.selectBodyAction.end();
		this.editBodyAction.end();
		this.selectFixtureAction.end();
		this.moveBodyAction.end();
		this.rotateBodyAction.end();
		this.moveFixtureAction.end();
		this.rotateFixtureAction.end();
	}
	
	/**
	 * Used to toggle declarative vs. compiled simulation controls.
	 * @param flag true if the current simulation is a compiled simulation
	 */
	private void setCompiledSimulation(boolean flag) {
		// if its a compiled simulation, we need to disable
		// the simulation tree and snapshots
		this.pnlSimulation.setEnabled(!flag);
		this.mnuSnapshot.setEnabled(!flag);
		this.mnuExport.setEnabled(!flag);
		this.mnuSaveAs.setEnabled(!flag);
		this.btnReset.setEnabled(flag);
	}
	
	/**
	 * Creates the test menu items and attaches them to the
	 * given menu.
	 * @param menu the menu to attach the menu items
	 */
	private void createTestMenuItems(JMenu menu) {
		JMenu dTests = new JMenu(Messages.getString("menu.tests.declarative"));
		JMenu cTests = new JMenu(Messages.getString("menu.tests.compiled"));
		menu.add(dTests);
		menu.add(cTests);
		// get the test listing file
		ResourceBundle bundle = ResourceBundle.getBundle("org.dyn4j.sandbox.tests.list");
		// read in all the tests
		List<String> keys = Collections.list(bundle.getKeys());
		// sort the keys
		Collections.sort(keys);
		// loop through the keys
		for (String key : keys) {
			String value = bundle.getString(key);
			JMenuItem mnuTest = new JMenuItem(key);
			mnuTest.setActionCommand("test+" + value);
			mnuTest.addActionListener(this);
			if (value.contains(".xml")) {
				dTests.add(mnuTest);
			} else {
				cTests.add(mnuTest);
			}
		}
	}
	
	/**
	 * Updates the fps and attached text box.
	 * @param diff the elapsed time in nanoseconds
	 */
	private void updateFps(long diff) {
		// update the frames per second
		boolean updated = this.fps.update(diff);
		// if it was updated (since it only updates every second)
		// then updated the text box
		if (updated) {
			// update the fps text box
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblFps.setText(fps.getFpsString());
				}
			});
		}
	}
	
	/**
	 * Adds menu items to the given menu for each look and feel
	 * installed in the running vm.
	 * @param menu the menu to add the items to
	 */
	private void createLookAndFeelMenuItems(JMenu menu) {
		LookAndFeel current = UIManager.getLookAndFeel();
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			JMenuItem mnuLaF = new JMenuItem(info.getName());
			if (current.getClass().getName().equals(info.getClassName())) {
				mnuLaF.setIcon(Icons.CHECK);
			}
			mnuLaF.setActionCommand("laf+" + info.getClassName());
			mnuLaF.addActionListener(this);
			menu.add(mnuLaF);
		}
	}
	
	/**
	 * The main method; uses zero arguments in the args array.
	 * @param args the command line arguments
	 */
	public static final void main(String[] args) {
		// attempt to use the nimbus look and feel
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			// completely ignore the error and just use the default look and feel
		}
		
		// the compatible version
		final int major = 3;
		final int minor = 1;
		final int revision = 1;
		final String vnr = major + "." + minor + "." + revision;
		
		// check the version of dyn4j
		boolean isValidVersion = false;
		try {
			if (Version.getMajorNumber() >= major && Version.getMinorNumber() >= minor && Version.getRevisionNumber() >= revision) {
				isValidVersion = true;
			}
		} catch (Throwable t) {
			// this can happen if the getXXXNumber methods don't exist (which is the case before 3.1.0)
			throw new VersionException(MessageFormat.format(Messages.getString("dyn4j.version.exception"), Sandbox.VERSION, vnr, Version.getVersion()), t);
		}
		// is the version valid?
		if (!isValidVersion) {
			throw new VersionException(MessageFormat.format(Messages.getString("dyn4j.version.exception"), Sandbox.VERSION, vnr, Version.getVersion()));
		}
		
		// show the GUI on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Sandbox();
			}
		});
	}
}