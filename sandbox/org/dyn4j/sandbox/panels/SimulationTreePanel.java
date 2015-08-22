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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.NullBounds;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.SandboxRay;
import org.dyn4j.sandbox.Simulation;
import org.dyn4j.sandbox.dialogs.AddBodyDialog;
import org.dyn4j.sandbox.dialogs.AddConvexFixtureDialog;
import org.dyn4j.sandbox.dialogs.AddConvexHullFixtureDialog;
import org.dyn4j.sandbox.dialogs.AddJointDialog;
import org.dyn4j.sandbox.dialogs.AddNonConvexFixtureDialog;
import org.dyn4j.sandbox.dialogs.AddRayDialog;
import org.dyn4j.sandbox.dialogs.ApplyForceAtPointDialog;
import org.dyn4j.sandbox.dialogs.ApplyForceDialog;
import org.dyn4j.sandbox.dialogs.ApplyTorqueDialog;
import org.dyn4j.sandbox.dialogs.EditBodyDialog;
import org.dyn4j.sandbox.dialogs.EditFixtureDialog;
import org.dyn4j.sandbox.dialogs.EditJointDialog;
import org.dyn4j.sandbox.dialogs.EditRayDialog;
import org.dyn4j.sandbox.dialogs.EditWorldDialog;
import org.dyn4j.sandbox.dialogs.SetBoundsDialog;
import org.dyn4j.sandbox.dialogs.SettingsDialog;
import org.dyn4j.sandbox.dialogs.ShiftWorldDialog;
import org.dyn4j.sandbox.events.BodyActionEvent;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to display and manage the World object using a JTree interface.
 * @author William Bittle
 * @version 1.0.7
 * @since 1.0.0
 */
public class SimulationTreePanel extends JPanel implements MouseListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4557154805670204181L;
	
	/** The simulation object */
	private Simulation simulation;
	
	/** The JTree containing the nodes */
	private JTree tree;
	
	/** The JTree scroller for large trees */
	private JScrollPane scroller;
	
	/** The JTree root node */
	private DefaultMutableTreeNode root;
	
	/** The JTree bounds node */
	private DefaultMutableTreeNode bounds;
	
	/** The JTree body folder node */
	private DefaultMutableTreeNode bodyFolder;
	
	/** The JTree joint folder node */
	private DefaultMutableTreeNode jointFolder;

	/** The JTree ray folder node */
	private DefaultMutableTreeNode rayFolder;
	
	/** The JTree model used to insert/remove/update nodes */
	private DefaultTreeModel model;
	
	/** The popup menu for the world node */
	private JPopupMenu popWorld;
	
	/** The popup menu for the bounds node */
	private JPopupMenu popBounds;
	
	/** The popup menu for the body folder node */
	private JPopupMenu popBodyFolder;
	
	/** The popup menu for the joint folder node */
	private JPopupMenu popJointFolder;
	
	/** The popup menu for the ray folder node */
	private JPopupMenu popRayFolder;
	
	/** The popup menu for body nodes */
	private JPopupMenu popBody;
	
	/** The popup menu for fixture nodes */
	private JPopupMenu popFixture;
	
	/** The popup menu for joint nodes */
	private JPopupMenu popJoint;

	/** The popup menu for ray nodes */
	private JPopupMenu popRay;
	
	/**
	 * Default constructor.
	 */
	public SimulationTreePanel() {
		this.root = new DefaultMutableTreeNode(null, true);
		this.model = new DefaultTreeModel(this.root);
		this.tree = new JTree(this.model);
		this.tree.setCellRenderer(new Renderer());
		this.tree.setEditable(false);
		this.tree.setShowsRootHandles(true);
		this.tree.setBorder(new EmptyBorder(5, 0, 0, 0));
		
		// the bounds
		this.bounds = new DefaultMutableTreeNode(new NullBounds());
		this.model.insertNodeInto(this.bounds, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.bounds).getParentPath());
		
		// folder to contain the bodies
		this.bodyFolder = new DefaultMutableTreeNode(Messages.getString("panel.tree.bodyFolder"));
		this.model.insertNodeInto(this.bodyFolder, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.bodyFolder.getPath()).getParentPath());
		
		// folder to contain the joints
		this.jointFolder = new DefaultMutableTreeNode(Messages.getString("panel.tree.jointFolder"));
		this.model.insertNodeInto(this.jointFolder, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.jointFolder.getPath()).getParentPath());
		
		// folder to contain the rays
		this.rayFolder = new DefaultMutableTreeNode(Messages.getString("panel.tree.rayFolder"));
		this.model.insertNodeInto(this.rayFolder, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.rayFolder.getPath()).getParentPath());
		
		this.scroller = new JScrollPane(this.tree);
		this.scroller.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		this.setLayout(new BorderLayout());
		this.add(this.scroller);
		
		this.tree.addMouseListener(this);
		
		this.createContextMenus();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		// disable the tree control
		this.tree.setEnabled(flag);
	}
	
	/**
	 * Sets the simulation of this simulation tree panel.
	 * <p>
	 * This method will create the nodes for all objects.
	 * @param simulation the current simulation
	 */
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		
		World world = simulation.getWorld();
		List<SandboxRay> rays = simulation.getRays();
		
		this.root.setUserObject(world);
		
		// reset everything
		this.bounds.setUserObject(new NullBounds());
		this.bodyFolder.removeAllChildren();
		this.jointFolder.removeAllChildren();
		this.rayFolder.removeAllChildren();
		
		// set the bounds
		if (world.getBounds() != null) {
			this.bounds.setUserObject(world.getBounds());
		}
		
		this.model.reload();
		
		// add all the bodies
		int bSize = world.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i);
			// add the body node
			DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode(body);
			// insert into the tree
			this.model.insertNodeInto(bodyNode, this.bodyFolder, this.bodyFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(bodyNode.getPath()).getParentPath());
			
			// add all the fixture nodes
			int fSize = body.getFixtureCount();
			for (int j = 0; j < fSize; j++) {
				BodyFixture fixture = body.getFixture(j);
				// add the node to the tree
				DefaultMutableTreeNode fixtureNode = new DefaultMutableTreeNode(fixture);
				this.model.insertNodeInto(fixtureNode, bodyNode, bodyNode.getChildCount());
			}
		}
		
		// add all the joints
		int jSize = world.getJointCount();
		for (int i = 0; i < jSize; i++) {
			Joint joint = world.getJoint(i);
			// add the joint node
			DefaultMutableTreeNode jointNode = new DefaultMutableTreeNode(joint);
			// insert into the tree
			this.model.insertNodeInto(jointNode, this.jointFolder, this.jointFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(jointNode.getPath()).getParentPath());
		}
		
		// add all the rays
		int rSize = rays.size();
		for (int i = 0; i < rSize; i++) {
			SandboxRay ray = rays.get(i);
			// add the ray node
			DefaultMutableTreeNode rayNode = new DefaultMutableTreeNode(ray);
			// insert into the tree
			this.model.insertNodeInto(rayNode, this.rayFolder, this.rayFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(rayNode.getPath()).getParentPath());
		}
	}
	
	/**
	 * Creates the context menus for the different item types.
	 */
	private void createContextMenus() {
		
		// create the world popup menu
		
		this.popWorld = new JPopupMenu();
		
		JMenuItem mnuShiftWorld = new JMenuItem(Messages.getString("menu.context.world.shift"));
		mnuShiftWorld.setActionCommand("shiftWorld");
		mnuShiftWorld.addActionListener(this);
		mnuShiftWorld.setIcon(Icons.SHIFT);
		
		JMenuItem mnuEditWorld = new JMenuItem(Messages.getString("menu.context.world.edit"));
		mnuEditWorld.setActionCommand("editWorld");
		mnuEditWorld.addActionListener(this);
		mnuEditWorld.setIcon(Icons.EDIT_WORLD);
		
		JMenuItem mnuClearWorld = new JMenuItem(Messages.getString("menu.context.world.removeAll"));
		mnuClearWorld.setActionCommand("clear-all");
		mnuClearWorld.addActionListener(this);
		mnuClearWorld.setIcon(Icons.REMOVE);
		
		JMenuItem mnuSettings = new JMenuItem(Messages.getString("menu.context.world.settings"));
		mnuSettings.setActionCommand("settings");
		mnuSettings.addActionListener(this);
		mnuSettings.setIcon(Icons.SETTINGS);
		
		this.popWorld.add(mnuEditWorld);
		this.popWorld.add(mnuSettings);
		this.popWorld.add(mnuShiftWorld);
		this.popWorld.addSeparator();
		this.popWorld.add(mnuClearWorld);
		
		// create the bounds popup menu
		
		this.popBounds = new JPopupMenu();
		
		JMenuItem mnuSetBounds = new JMenuItem(Messages.getString("menu.context.bounds.set"));
		mnuSetBounds.setActionCommand("set-bounds");
		mnuSetBounds.addActionListener(this);
		mnuSetBounds.setIcon(Icons.SET_BOUNDS);
		
		JMenuItem mnuUnsetBounds = new JMenuItem(Messages.getString("menu.context.bounds.unset"));
		mnuUnsetBounds.setActionCommand("unset-bounds");
		mnuUnsetBounds.addActionListener(this);
		mnuUnsetBounds.setIcon(Icons.UNSET_BOUNDS);
		
		this.popBounds.add(mnuSetBounds);
		this.popBounds.add(mnuUnsetBounds);
		
		// create the body folder popup
		
		this.popBodyFolder = new JPopupMenu();
		
		JMenuItem mnuAddbody = new JMenuItem(Messages.getString("menu.context.bodyFolder.addBody"));
		JMenuItem mnuRemoveAllBodies = new JMenuItem(Messages.getString("menu.context.bodyFolder.removeAll"));
		
		mnuAddbody.setActionCommand("addBody");
		mnuRemoveAllBodies.setActionCommand("removeAllBodies");
		
		mnuAddbody.addActionListener(this);
		mnuRemoveAllBodies.addActionListener(this);
		
		mnuAddbody.setIcon(Icons.ADD_BODY);
		mnuRemoveAllBodies.setIcon(Icons.REMOVE);
		
		this.popBodyFolder.add(mnuAddbody);
		this.popBodyFolder.addSeparator();
		this.popBodyFolder.add(mnuRemoveAllBodies);
		
		// create the joint folder popup
		
		this.popJointFolder = new JPopupMenu();
		
		JMenuItem mnuAddAngleJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addAngleJoint"));
		JMenuItem mnuAddDistanceJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addDistanceJoint"));
		JMenuItem mnuAddFrictionJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addFrictionJoint"));
		JMenuItem mnuAddPrismaticJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addPrismaticJoint"));
		JMenuItem mnuAddPulleyJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addPulleyJoint"));
		JMenuItem mnuAddRevoluteJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addRevoluteJoint"));
		JMenuItem mnuAddRopeJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addRopeJoint"));
		JMenuItem mnuAddWeldJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addWeldJoint"));
		JMenuItem mnuAddWheelJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addWheelJoint"));
		JMenuItem mnuAddMotorJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addMotorJoint"));
		JMenuItem mnuAddMouseJoint = new JMenuItem(Messages.getString("menu.context.jointFolder.addMouseJoint"));
		JMenuItem mnuRemoveAllJoints = new JMenuItem(Messages.getString("menu.context.jointFolder.removeAll"));
		
		mnuAddAngleJoint.setIcon(Icons.ADD_ANGLE_JOINT);
		mnuAddDistanceJoint.setIcon(Icons.ADD_DISTANCE_JOINT);
		mnuAddFrictionJoint.setIcon(Icons.ADD_FRICTION_JOINT);
		mnuAddMouseJoint.setIcon(Icons.ADD_MOUSE_JOINT);
		mnuAddPrismaticJoint.setIcon(Icons.ADD_PRISMATIC_JOINT);
		mnuAddPulleyJoint.setIcon(Icons.ADD_PULLEY_JOINT);
		mnuAddRevoluteJoint.setIcon(Icons.ADD_REVOLUTE_JOINT);
		mnuAddRopeJoint.setIcon(Icons.ADD_ROPE_JOINT);
		mnuAddWeldJoint.setIcon(Icons.ADD_WELD_JOINT);
		mnuAddWheelJoint.setIcon(Icons.ADD_WHEEL_JOINT);
		mnuAddMotorJoint.setIcon(Icons.ADD_MOTOR_JOINT);
		mnuRemoveAllJoints.setIcon(Icons.REMOVE);
		
		mnuAddAngleJoint.setActionCommand("addAngleJoint");
		mnuAddDistanceJoint.setActionCommand("addDistanceJoint");
		mnuAddFrictionJoint.setActionCommand("addFrictionJoint");
		mnuAddPrismaticJoint.setActionCommand("addPrismaticJoint");
		mnuAddPulleyJoint.setActionCommand("addPulleyJoint");
		mnuAddRevoluteJoint.setActionCommand("addRevoluteJoint");
		mnuAddRopeJoint.setActionCommand("addRopeJoint");
		mnuAddWeldJoint.setActionCommand("addWeldJoint");
		mnuAddWheelJoint.setActionCommand("addWheelJoint");
		mnuAddMouseJoint.setActionCommand("addMouseJoint");
		mnuAddMotorJoint.setActionCommand("addMotorJoint");
		mnuRemoveAllJoints.setActionCommand("removeAllJoints");
		
		mnuAddAngleJoint.addActionListener(this);
		mnuAddDistanceJoint.addActionListener(this);
		mnuAddFrictionJoint.addActionListener(this);
		mnuAddPrismaticJoint.addActionListener(this);
		mnuAddPulleyJoint.addActionListener(this);
		mnuAddRevoluteJoint.addActionListener(this);
		mnuAddRopeJoint.addActionListener(this);
		mnuAddWeldJoint.addActionListener(this);
		mnuAddWheelJoint.addActionListener(this);
		mnuAddMouseJoint.addActionListener(this);
		mnuAddMotorJoint.addActionListener(this);
		mnuRemoveAllJoints.addActionListener(this);
		
		this.popJointFolder.add(mnuAddDistanceJoint);
		this.popJointFolder.add(mnuAddMouseJoint);
		this.popJointFolder.add(mnuAddPrismaticJoint);
		this.popJointFolder.add(mnuAddPulleyJoint);
		this.popJointFolder.add(mnuAddRevoluteJoint);
		this.popJointFolder.add(mnuAddRopeJoint);
		this.popJointFolder.add(mnuAddWeldJoint);
		this.popJointFolder.add(mnuAddWheelJoint);
		this.popJointFolder.addSeparator();
		this.popJointFolder.add(mnuAddAngleJoint);
		this.popJointFolder.add(mnuAddFrictionJoint);
		this.popJointFolder.add(mnuAddMotorJoint);
		this.popJointFolder.addSeparator();
		this.popJointFolder.add(mnuRemoveAllJoints);
		
		// create the body node popup menu
		
		this.popBody = new JPopupMenu();
		
		JMenuItem mnuEditBody = new JMenuItem(Messages.getString("menu.context.body.edit"));
		JMenuItem mnuRemoveBody = new JMenuItem(Messages.getString("menu.context.body.remove"));
		JMenuItem mnuAddCircle = new JMenuItem(Messages.getString("menu.context.body.addCircleFixture"));
		JMenuItem mnuAddRectangle = new JMenuItem(Messages.getString("menu.context.body.addRectangleFixture"));
		JMenuItem mnuAddPolygon = new JMenuItem(Messages.getString("menu.context.body.addConvexPolygonFixture"));
		JMenuItem mnuAddSegment = new JMenuItem(Messages.getString("menu.context.body.addSegmentFixture"));
		JMenuItem mnuAddHull = new JMenuItem(Messages.getString("menu.context.body.addConvexHullFixture"));
		JMenuItem mnuAddDecompose = new JMenuItem(Messages.getString("menu.context.body.addNonConvexPolygonFixtures"));
		JMenuItem mnuRemoveAllFixtures = new JMenuItem(Messages.getString("menu.context.body.removeAll"));
		JMenuItem mnuApplyForce = new JMenuItem(Messages.getString("menu.context.body.applyForce"));
		JMenuItem mnuApplyTorque = new JMenuItem(Messages.getString("menu.context.body.applyTorque"));
		JMenuItem mnuApplyForceAtPoint = new JMenuItem(Messages.getString("menu.context.body.applyForceAtPoint"));
		JMenuItem mnuClearForce = new JMenuItem(Messages.getString("menu.context.body.clearAccumulatedForce"));
		JMenuItem mnuClearTorque = new JMenuItem(Messages.getString("menu.context.body.clearAccumulatedTorque"));
		JMenuItem mnuCenterOnOrigin = new JMenuItem(Messages.getString("menu.context.body.centerOnOrigin"));
		
		mnuEditBody.setIcon(Icons.EDIT_BODY);
		mnuRemoveBody.setIcon(Icons.REMOVE_BODY);
		mnuAddCircle.setIcon(Icons.ADD_CIRCLE);
		mnuAddRectangle.setIcon(Icons.ADD_RECTANGLE);
		mnuAddPolygon.setIcon(Icons.ADD_POLYGON);
		mnuAddSegment.setIcon(Icons.ADD_SEGMENT);
		mnuAddHull.setIcon(Icons.ADD_CONVEX_HULL);
		mnuAddDecompose.setIcon(Icons.ADD_NON_CONVEX_POLYGON);
		mnuRemoveAllFixtures.setIcon(Icons.REMOVE);
		mnuApplyForce.setIcon(Icons.FORCE);
		mnuApplyTorque.setIcon(Icons.TORQUE);
		mnuApplyForceAtPoint.setIcon(Icons.FORCE_AT_POINT);
		mnuClearForce.setIcon(Icons.CLEAR_ALL);
		mnuClearTorque.setIcon(Icons.CLEAR_ALL);
		mnuCenterOnOrigin.setIcon(Icons.CENTER_ON_ORIGIN);
		
		mnuEditBody.setActionCommand("editBody");
		mnuRemoveBody.setActionCommand("removeBody");
		mnuAddCircle.setActionCommand("addCircleFixture");
		mnuAddRectangle.setActionCommand("addRectangleFixture");
		mnuAddPolygon.setActionCommand("addPolygonFixture");
		mnuAddSegment.setActionCommand("addSegmentFixture");
		mnuAddHull.setActionCommand("addHullFixture");
		mnuAddDecompose.setActionCommand("addDecompose");
		mnuRemoveAllFixtures.setActionCommand("removeAllFixtures");
		mnuApplyForce.setActionCommand("applyForce");
		mnuApplyTorque.setActionCommand("applyTorque");
		mnuApplyForceAtPoint.setActionCommand("applyForceAtPoint");
		mnuClearForce.setActionCommand("clearForce");
		mnuClearTorque.setActionCommand("clearTorque");
		mnuCenterOnOrigin.setActionCommand("centerOnOrigin");
		
		mnuEditBody.addActionListener(this);
		mnuRemoveBody.addActionListener(this);
		mnuAddCircle.addActionListener(this);
		mnuAddRectangle.addActionListener(this);
		mnuAddPolygon.addActionListener(this);
		mnuAddSegment.addActionListener(this);
		mnuAddHull.addActionListener(this);
		mnuAddDecompose.addActionListener(this);
		mnuRemoveAllFixtures.addActionListener(this);
		mnuApplyForce.addActionListener(this);
		mnuApplyTorque.addActionListener(this);
		mnuApplyForceAtPoint.addActionListener(this);
		mnuClearForce.addActionListener(this);
		mnuClearTorque.addActionListener(this);
		mnuCenterOnOrigin.addActionListener(this);
		
		this.popBody.add(mnuEditBody);
		this.popBody.add(mnuRemoveBody);
		this.popBody.addSeparator();
		this.popBody.add(mnuAddCircle);
		this.popBody.add(mnuAddRectangle);
		this.popBody.add(mnuAddPolygon);
		this.popBody.add(mnuAddSegment);
		this.popBody.addSeparator();
		this.popBody.add(mnuAddHull);
		this.popBody.add(mnuAddDecompose);
		this.popBody.addSeparator();
		this.popBody.add(mnuRemoveAllFixtures);
		this.popBody.addSeparator();
		this.popBody.add(mnuApplyForce);
		this.popBody.add(mnuApplyTorque);
		this.popBody.add(mnuApplyForceAtPoint);
		this.popBody.addSeparator();
		this.popBody.add(mnuClearForce);
		this.popBody.add(mnuClearTorque);
		this.popBody.addSeparator();
		this.popBody.add(mnuCenterOnOrigin);
		
		// create the fixture node popup menu
		
		this.popFixture = new JPopupMenu();
		
		JMenuItem mnuEditFixture = new JMenuItem(Messages.getString("menu.context.fixture.edit"));
		JMenuItem mnuRemoveFixture = new JMenuItem(Messages.getString("menu.context.fixture.remove"));
		
		// add default icons so that the menu is the correct size initially
		mnuEditFixture.setIcon(Icons.EDIT_POLYGON);
		mnuRemoveFixture.setIcon(Icons.REMOVE_POLYGON);
		
		mnuEditFixture.setActionCommand("editFixture");
		mnuRemoveFixture.setActionCommand("removeFixture");
		
		mnuEditFixture.addActionListener(this);
		mnuRemoveFixture.addActionListener(this);
		
		this.popFixture.add(mnuEditFixture);
		this.popFixture.add(mnuRemoveFixture);
		
		// create the joint node popup menu
		
		this.popJoint = new JPopupMenu();
		
		JMenuItem mnuEditJoint = new JMenuItem(Messages.getString("menu.context.joint.edit"));
		JMenuItem mnuRemoveJoint = new JMenuItem(Messages.getString("menu.context.joint.remove"));
		
		// add default icons so that the menu is the correct size initially
		mnuEditJoint.setIcon(Icons.EDIT_ANGLE_JOINT);
		mnuRemoveJoint.setIcon(Icons.REMOVE_ANGLE_JOINT);
		
		mnuEditJoint.setActionCommand("editJoint");
		mnuRemoveJoint.setActionCommand("removeJoint");
		
		mnuEditJoint.addActionListener(this);
		mnuRemoveJoint.addActionListener(this);
		
		this.popJoint.add(mnuEditJoint);
		this.popJoint.add(mnuRemoveJoint);
		
		// create the ray folder popup menu
		
		this.popRayFolder = new JPopupMenu();
		
		JMenuItem mnuAddRay = new JMenuItem(Messages.getString("menu.context.ray.add"));
		JMenuItem mnuRemoveAllRays = new JMenuItem(Messages.getString("menu.context.ray.removeAll"));
		
		mnuAddRay.setIcon(Icons.ADD_RAY);
		mnuRemoveAllRays.setIcon(Icons.REMOVE);
		
		mnuAddRay.setActionCommand("addRay");
		mnuRemoveAllRays.setActionCommand("removeAllRays");
		
		mnuAddRay.addActionListener(this);
		mnuRemoveAllRays.addActionListener(this);
		
		this.popRayFolder.add(mnuAddRay);
		this.popRayFolder.addSeparator();
		this.popRayFolder.add(mnuRemoveAllRays);
		
		// create the ray popup menu
		
		this.popRay = new JPopupMenu();
		
		JMenuItem mnuEditRay = new JMenuItem(Messages.getString("menu.context.ray.edit"));
		JMenuItem mnuRemoveRay = new JMenuItem(Messages.getString("menu.context.ray.remove"));
		
		mnuEditRay.setIcon(Icons.EDIT_RAY);
		mnuRemoveRay.setIcon(Icons.REMOVE_RAY);
		
		mnuEditRay.setActionCommand("editRay");
		mnuRemoveRay.setActionCommand("removeRay");
		
		mnuEditRay.addActionListener(this);
		mnuRemoveRay.addActionListener(this);
		
		this.popRay.add(mnuEditRay);
		this.popRay.add(mnuRemoveRay);
	}
	
	/**
	 * Adds an action listener to this class to listen for events.
	 * @param actionListener the action listener
	 */
	public void addActionListener(ActionListener actionListener) {
		this.listenerList.add(ActionListener.class, actionListener);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
	 */
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		this.scroller.setPreferredSize(preferredSize);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setMinimumSize(java.awt.Dimension)
	 */
	@Override
	public void setMinimumSize(Dimension minimumSize) {
		super.setMinimumSize(minimumSize);
		this.scroller.setMinimumSize(minimumSize);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setMaximumSize(java.awt.Dimension)
	 */
	@Override
	public void setMaximumSize(Dimension maximumSize) {
		super.setMaximumSize(maximumSize);
		this.scroller.setMaximumSize(maximumSize);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// different OSes will call either this event or the mouseReleased event
		if (e.isPopupTrigger()) this.showContextMenu(e);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// different OSes will call either this event or the mousePressed event
		if (e.isPopupTrigger()) this.showContextMenu(e);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JPanel#updateUI()
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		// also update the context menus
		if (this.popBody != null) SwingUtilities.updateComponentTreeUI(this.popBody);
		if (this.popBodyFolder != null) SwingUtilities.updateComponentTreeUI(this.popBodyFolder);
		if (this.popBounds != null) SwingUtilities.updateComponentTreeUI(this.popBounds);
		if (this.popFixture != null) SwingUtilities.updateComponentTreeUI(this.popFixture);
		if (this.popJoint != null) SwingUtilities.updateComponentTreeUI(this.popJoint);
		if (this.popJointFolder != null) SwingUtilities.updateComponentTreeUI(this.popJointFolder);
		if (this.popWorld != null) SwingUtilities.updateComponentTreeUI(this.popWorld);
		if (this.popRayFolder != null) SwingUtilities.updateComponentTreeUI(this.popRayFolder);
		if (this.popRay != null) SwingUtilities.updateComponentTreeUI(this.popRay);
		// reset the cell renderer
		if (this.tree != null) this.tree.setCellRenderer(new Renderer());
	}
	
	/**
	 * Shows a context menu at the location of the click inside the JTree.
	 * @param event the event
	 */
	private void showContextMenu(MouseEvent event) {
		// only show the context menu if this control is enabled
		if (this.isEnabled()) {
			// find the location in the tree
			int x = event.getX();
			int y = event.getY();
			TreePath path = this.tree.getPathForLocation(x, y);
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				// select the node
				this.tree.setSelectionPath(path);
				Object userData = node.getUserObject();
				
				if (userData instanceof World) {
					// show the world context menu
					this.popWorld.show(this.tree, x, y);
				} else if (userData instanceof Bounds) {
					this.popBounds.show(this.tree, x, y);
				} else if (node == this.bodyFolder) {
					// show the body folder context menu
					this.popBodyFolder.show(this.tree, x, y);
				} else if (node == this.jointFolder) {
					// show the joint folder context menu
					this.popJointFolder.show(this.tree, x, y);
				} else if (node == this.rayFolder) {
					// show the joint folder context menu
					this.popRayFolder.show(this.tree, x, y);
				} else if (userData instanceof Body) {
					// show the body context menu
					this.popBody.show(this.tree, x, y);
				} else if (userData instanceof BodyFixture) {
					// show the fixture context menu
					this.popFixture.show(this.tree, x, y);
					// get the type of fixture
					Convex convex = ((BodyFixture)userData).getShape();
					// check the shape
					if (convex instanceof Circle) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_CIRCLE);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_CIRCLE);
					} else if (convex instanceof Rectangle) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_RECTANGLE);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_RECTANGLE);
					} else if (convex instanceof Polygon) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_POLYGON);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_POLYGON);
					} else if (convex instanceof Segment) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_SEGMENT);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_SEGMENT);
					}
				} else if (userData instanceof Joint) {
					// show the fixture context menu
					this.popJoint.show(this.tree, x, y);
					// get the type of fixture
					Joint joint = ((Joint)userData);
					// check the shape
					if (joint instanceof AngleJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_ANGLE_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_ANGLE_JOINT);
					} else if (joint instanceof DistanceJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_DISTANCE_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_DISTANCE_JOINT);
					} else if (joint instanceof FrictionJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_FRICTION_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_FRICTION_JOINT);
					} else if (joint instanceof MotorJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_MOTOR_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_MOTOR_JOINT);
					} else if (joint instanceof PinJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_MOUSE_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_MOUSE_JOINT);
					} else if (joint instanceof PrismaticJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_PRISMATIC_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_PRISMATIC_JOINT);
					} else if (joint instanceof PulleyJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_PULLEY_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_PULLEY_JOINT);
					} else if (joint instanceof RevoluteJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_REVOLUTE_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_REVOLUTE_JOINT);
					} else if (joint instanceof RopeJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_ROPE_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_ROPE_JOINT);
					} else if (joint instanceof WeldJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_WELD_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_WELD_JOINT);
					} else if (joint instanceof WheelJoint) {
						((JMenuItem)this.popJoint.getComponent(0)).setIcon(Icons.EDIT_WHEEL_JOINT);
						((JMenuItem)this.popJoint.getComponent(1)).setIcon(Icons.REMOVE_WHEEL_JOINT);
					}
				} else if (userData instanceof Ray) {
					// show the ray context menu
					this.popRay.show(this.tree, x, y);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if ("clear-all".equals(command)) {
			this.clearAllAction();
		} else if ("editWorld".equals(command)) {
			this.editWorldAction();
		} else if ("shiftWorld".equals(command)) {
			this.shiftWorldAction();
		} else if ("set-bounds".equals(command)) {
			this.setBoundsAction();
		} else if ("unset-bounds".equals(command)) {
			this.unsetBoundsAction();
		} else if ("addBody".equals(command)) {
			this.addBodyAction();
		} else if ("editBody".equals(command)) {
			this.editBodyAction();
		} else if ("removeBody".equals(command)) {
			this.removeBodyAction();
		} else if ("addCircleFixture".equals(command)) {
			this.addFixtureAction(Icons.ADD_CIRCLE.getImage(), new CirclePanel());
		} else if ("addRectangleFixture".equals(command)) {
			this.addFixtureAction(Icons.ADD_RECTANGLE.getImage(), new RectanglePanel());
		} else if ("addSegmentFixture".equals(command)) {
			this.addFixtureAction(Icons.ADD_SEGMENT.getImage(), new SegmentPanel());
		} else if ("addPolygonFixture".equals(command)) {
			this.addFixtureAction(Icons.ADD_POLYGON.getImage(), new ConvexPolygonPanel());
		} else if ("addHullFixture".equals(command)) {
			this.addHullFixtureAction();
		} else if ("addDecompose".equals(command)) {
			this.addDecomposablePolygonAction();
		} else if ("removeAllFixtures".equals(command)) {
			this.removeAllFixtures();
		} else if ("editFixture".equals(command)) {
			this.editFixtureAction();
		} else if ("removeFixture".equals(command)) {
			this.removeFixtureAction();
		} else if ("centerOnOrigin".equals(command)) {
			this.centerOnOriginAction();
		} else if ("addAngleJoint".equals(command)) {
			this.addJointAction(AngleJoint.class);
		} else if ("addDistanceJoint".equals(command)) {
			this.addJointAction(DistanceJoint.class);
		} else if ("addFrictionJoint".equals(command)) {
			this.addJointAction(FrictionJoint.class);
		} else if ("addPrismaticJoint".equals(command)) {
			this.addJointAction(PrismaticJoint.class);
		} else if ("addPulleyJoint".equals(command)) {
			this.addJointAction(PulleyJoint.class);
		} else if ("addRevoluteJoint".equals(command)) {
			this.addJointAction(RevoluteJoint.class);
		} else if ("addRopeJoint".equals(command)) {
			this.addJointAction(RopeJoint.class);
		} else if ("addWeldJoint".equals(command)) {
			this.addJointAction(WeldJoint.class);
		} else if ("addWheelJoint".equals(command)) {
			this.addJointAction(WheelJoint.class);
		} else if ("addMotorJoint".equals(command)) {
			this.addJointAction(MotorJoint.class);
		} else if ("addMouseJoint".equals(command)) {
			this.addJointAction(PinJoint.class);
		} else if ("editJoint".equals(command)) {
			this.editJointAction();
		} else if ("removeJoint".equals(command)) {
			this.removeJointAction();
		} else if ("applyForce".equals(command)) {
			this.applyForceAction();
		} else if ("applyTorque".equals(command)) {
			this.applyTorqueAction();
		} else if ("applyForceAtPoint".equals(command)) {
			this.applyForceAtPointAction();
		} else if ("clearForce".equals(command)) {
			this.clearForceAction();
		} else if ("clearTorque".equals(command)) {
			this.clearTorqueAction();
		} else if ("removeAllBodies".equals(command)) {
			this.removeAllBodiesAction();
		} else if ("removeAllJoints".equals(command)) {
			this.removeAllJointsAction();
		} else if ("addRay".equals(command)) {
			this.addRayAction();
		} else if ("removeAllRays".equals(command)) {
			this.removeAllRaysAction();
		} else if ("editRay".equals(command)) {
			this.editRayAction();
		} else if ("removeRay".equals(command)) {
			this.removeRayAction();
		} else if ("settings".equals(command)) {
			this.editSettingsAction();
		}
	}
	
	/**
	 * Gets an array of the current bodies in the world.
	 * <p>
	 * Returns null if there are zero.
	 * @return {@link SandboxBody}[]
	 */
	private SandboxBody[] getBodies() {
		// get the current list of bodies
		SandboxBody[] bodies = null;
		World world = this.simulation.getWorld();
		int size = world.getBodyCount();
		if (size != 0) {
			bodies = new SandboxBody[size];
			for (int i = 0; i < size; i++) {
				bodies[i] = (SandboxBody)world.getBody(i);
			}
			return bodies;
		} else {
			return null;
		}
	}
	
	/**
	 * Notifies all the action listeners of the event.
	 * @param command the command
	 */
	private void notifyActionListeners(String command) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		int size = listeners.length;
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
		for (int i = 0; i < size; i++) {
			listeners[i].actionPerformed(event);
		}
	}
	
	/**
	 * Notifies all the action listeners of the event.
	 * @param command the command
	 * @param body the effected body
	 */
	private void notifyActionListeners(String command, SandboxBody body) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		int size = listeners.length;
		BodyActionEvent event = new BodyActionEvent(this, ActionEvent.ACTION_PERFORMED, command, body);
		for (int i = 0; i < size; i++) {
			listeners[i].actionPerformed(event);
		}
	}
	
	// context menu actions
	
	/**
	 * Clears all the joints and bodies from the world.
	 */
	private void clearAllAction() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(
				ControlUtilities.getParentWindow(this), 
				Messages.getString("menu.context.removeAll.warning"), 
				Messages.getString("menu.context.removeAll.warning.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// remove it all from the world
			synchronized (Simulation.LOCK) {
				// remove everything
				this.simulation.getWorld().removeAllBodiesAndJoints(false);
				// clear the contact listener
				this.simulation.getContactCounter().clear();
			}
			// remove the nodes from the tree
			this.bodyFolder.removeAllChildren();
			this.jointFolder.removeAllChildren();
			// notify that they were removed
			this.model.reload(this.bodyFolder);
			this.model.reload(this.jointFolder);
			
			this.notifyActionListeners("clear-all");
		}
	}
	
	/**
	 * Shows an edit world dialog.
	 */
	private void editWorldAction() {
		synchronized (Simulation.LOCK) {
			EditWorldDialog.show(ControlUtilities.getParentWindow(this), this.simulation.getWorld());
		}
		
		this.model.nodeChanged(this.root);
	}
	
	/**
	 * Shows a shift world dialog.
	 */
	private void shiftWorldAction() {
		Vector2 shift = ShiftWorldDialog.show(ControlUtilities.getParentWindow(this));
		if (shift != null) {
			synchronized (Simulation.LOCK) {
				this.simulation.getWorld().shift(shift);
			}
		}
	}
	
	/**
	 * Shows a Rectangular bounds dialog.
	 * <p>
	 * If the user clicks the set button the new bounds are set on the world object.
	 */
	private void setBoundsAction() {
		Bounds bounds = null;
		synchronized (Simulation.LOCK) {
			bounds = this.simulation.getWorld().getBounds();
		}
		AxisAlignedBounds b = null;
		if (bounds instanceof NullBounds) {
			b = SetBoundsDialog.show(ControlUtilities.getParentWindow(this), null);
		} else {
			b = SetBoundsDialog.show(ControlUtilities.getParentWindow(this), (AxisAlignedBounds)bounds);
		}
		if (b != null) {
			synchronized (Simulation.LOCK) {
				this.simulation.getWorld().setBounds(b);
			}
			this.bounds.setUserObject(b);
			this.model.reload(this.bounds);
		}
	}
	
	/**
	 * Sets the bounds on the world object to unset.
	 */
	private void unsetBoundsAction() {
		NullBounds bounds = new NullBounds();
		synchronized (Simulation.LOCK) {
			this.simulation.getWorld().setBounds(bounds);
		}
		this.bounds.setUserObject(bounds);
		this.model.reload(this.bounds);
	}
	
	/**
	 * Shows the add new body dialog.
	 * <p>
	 * If the user clicks the add button, then a new body is created and added to the world.
	 */
	private void addBodyAction() {
		// create the body by showing the dialogs
		SandboxBody body = AddBodyDialog.show(ControlUtilities.getParentWindow(this));
		// make sure the user didn't cancel the operation
		if (body != null) {
			// add the body to the world
			synchronized (Simulation.LOCK) {
				this.simulation.getWorld().addBody(body);
			}
			// add the body and the fixtures to the root node
			DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode(body);
			// insert into the tree
			this.model.insertNodeInto(bodyNode, this.bodyFolder, this.bodyFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(bodyNode.getPath()).getParentPath());
		}
	}
	
	/**
	 * Shows an edit body dialog.
	 * <p>
	 * If the user clicks the save button, the selected body is updated with the new values.
	 */
	private void editBodyAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a body that is selected
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// pass the body to the edit dialog
				synchronized (Simulation.LOCK) {
					EditBodyDialog.show(ControlUtilities.getParentWindow(this), body);
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation dialog asking the user if they really want to perform the action.
	 * <p>
	 * IF the user clicks yes, then the selected body is deleted.
	 */
	private void removeBodyAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a body that is selected
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this),
						MessageFormat.format(Messages.getString("menu.context.body.remove.warning"), body.getName()), 
						Messages.getString("menu.context.body.remove.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (Simulation.LOCK) {
						this.simulation.getWorld().removeBody(body);
					}
					// remove the node from the tree
					this.model.removeNodeFromParent(node);
					
					this.notifyActionListeners("remove-body", body);
				}
			}
		}
	}
	
	/**
	 * Shows the add new fixture dialog with the given shape panel added.
	 * <p>
	 * If the user clicks the add button, the shape and fixture are created and added to the selected body.
	 * @param icon the icon image
	 * @param shapePanel the shape panel
	 */
	private void addFixtureAction(Image icon, ConvexShapePanel shapePanel) {
		// the current selection should have the body to add the fixture to
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// create the fixture by showing the dialogs (don't show the local transform panel if its the first fixture)
				BodyFixture fixture = AddConvexFixtureDialog.show(ControlUtilities.getParentWindow(this), icon, shapePanel);
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure the user didnt cancel the operation
				if (fixture != null) {
					// add the fixture to the body
					synchronized (Simulation.LOCK) {
						body.addFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.updateMass();
						}
					}
					// add the node to the tree
					DefaultMutableTreeNode fixtureNode = new DefaultMutableTreeNode(fixture);
					this.model.insertNodeInto(fixtureNode, node, node.getChildCount());
					// expand the path to the new node
					this.tree.expandPath(new TreePath(fixtureNode.getPath()).getParentPath());
				}
			}
		}
	}

	/**
	 * Shows the add new convex hull fixture dialog.
	 * <p>
	 * If the user clicks the add button, the shape and fixture are created and added to the selected body.
	 */
	private void addHullFixtureAction() {
		// the current selection should have the body to add the fixture to
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// create the fixture by showing the dialogs (don't show the local transform panel if its the first fixture)
				BodyFixture fixture = AddConvexHullFixtureDialog.show(ControlUtilities.getParentWindow(this));
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure the user didnt cancel the operation
				if (fixture != null) {
					// add the fixture to the body
					synchronized (Simulation.LOCK) {
						body.addFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.updateMass();
						}
					}
					// add the node to the tree
					DefaultMutableTreeNode fixtureNode = new DefaultMutableTreeNode(fixture);
					this.model.insertNodeInto(fixtureNode, node, node.getChildCount());
					// expand the path to the new node
					this.tree.expandPath(new TreePath(fixtureNode.getPath()).getParentPath());
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation dialog asking the user if they really want to perform the action.
	 * <p>
	 * If the user clicks yes, then the selected fixture is deleted.
	 */
	private void removeFixtureAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a body that is selected
			if (node.getUserObject() instanceof BodyFixture) {
				// get the parent node (a body)
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
				// get the body from the parent node
				SandboxBody body = (SandboxBody)parent.getUserObject();
				// get the fixture from the node
				BodyFixture fixture = (BodyFixture)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this),
						MessageFormat.format(Messages.getString("menu.context.fixture.remove.warning"), fixture.getUserData(), body.getName()), 
						Messages.getString("menu.context.fixture.remove.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (Simulation.LOCK) {
						// remove the fixture
						body.removeFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.updateMass();
						}
					}
					// remove the node from the tree
					this.model.removeNodeFromParent(node);
				}
			}
		}
	}
	
	/**
	 * Called when the user clicks the Center On Origin menu item on a body.
	 */
	private void centerOnOriginAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				synchronized (Simulation.LOCK) {
					// re-center the body
					body.translateToOrigin();
				}
			}
			
		}
	}
	
	/**
	 * Shows a confirmation dialog asking the user if they really want to perform the action.
	 * <p>
	 * If the user clicks yes, then all the fixtures of the selected body are removed.
	 */
	private void removeAllFixtures() {
		// the current selection should have the body to add the fixture to
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this), 
						MessageFormat.format(Messages.getString("menu.context.body.removeAll.warning"), body.getName()), 
						Messages.getString("menu.context.body.removeAll.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (Simulation.LOCK) {
						// remove the fixture
						body.removeAllFixtures();
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.updateMass();
						}
					}
					// remove the nodes
					node.removeAllChildren();
					// tell the model to update it visually
					this.model.reload(node);
				}
			}
		}
	}
	
	/**
	 * Shows a dialog with fixture options that can be changed.
	 */
	private void editFixtureAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a body that is selected
			if (node.getUserObject() instanceof BodyFixture) {
				// get the parent node (a body)
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
				// get the body from the parent node
				SandboxBody body = (SandboxBody)parent.getUserObject();
				// get the fixture from the node
				BodyFixture fixture = (BodyFixture)node.getUserObject();
				// show the edit dialog
				synchronized (Simulation.LOCK) {
					Convex convex = fixture.getShape();
					Image icon;
					if (convex instanceof Circle) {
						icon = Icons.EDIT_CIRCLE.getImage();
					} else if (convex instanceof Rectangle) {
						icon = Icons.EDIT_RECTANGLE.getImage();
					} else if (convex instanceof Segment) {
						icon = Icons.EDIT_SEGMENT.getImage();
					} else {
						icon = Icons.EDIT_POLYGON.getImage();
					}
					EditFixtureDialog.show(ControlUtilities.getParentWindow(this), icon, body, fixture);
				}
			}
		}
	}
	
	/**
	 * Shows an add new joint dialog using the given joint panel.
	 * <p>
	 * If the user closes or cancels, nothing is modified.  If the user clicks the add button
	 * a new joint is created and added to the world.
	 * @param clazz the joint class
	 */
	private void addJointAction(Class<? extends Joint> clazz) {
		synchronized (Simulation.LOCK) {
			SandboxBody[] bodies = this.getBodies();
			// check the joint class type
			if (bodies == null || bodies.length == 0 || (clazz != PinJoint.class && bodies.length == 1)) {
				JOptionPane.showMessageDialog(ControlUtilities.getParentWindow(this),
						Messages.getString("menu.context.joint.add.warning"), 
						Messages.getString("menu.context.joint.add.warning.title"), 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Joint joint = AddJointDialog.show(ControlUtilities.getParentWindow(this), bodies, clazz);
			if (joint != null) {
				// add the joint to the world
				this.simulation.getWorld().addJoint(joint);
				// add the joint to the root node
				DefaultMutableTreeNode jointNode = new DefaultMutableTreeNode(joint);
				// insert into the tree
				this.model.insertNodeInto(jointNode, this.jointFolder, this.jointFolder.getChildCount());
				// expand the path to the new node
				this.tree.expandPath(new TreePath(jointNode.getPath()).getParentPath());
			}
		}
	}
	
	/**
	 * Shows a dialog with joint options that can be changed.
	 */
	private void editJointAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a joint that is selected
			if (node.getUserObject() instanceof Joint) {
				// get the joint from the node
				Joint joint = (Joint)node.getUserObject();
				// show the right dialog
				synchronized (Simulation.LOCK) {
					EditJointDialog.show(ControlUtilities.getParentWindow(this), joint);
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation dialog asking the user if they really want to perform the action.
	 * <p>
	 * If the user clicks yes, then the selected joint is deleted.
	 */
	private void removeJointAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a body that is selected
			if (node.getUserObject() instanceof Joint) {
				// get the joint from the node
				Joint joint = (Joint)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this), 
						MessageFormat.format(Messages.getString("menu.context.joint.remove.warning"), joint.getUserData()), 
						Messages.getString("menu.context.joint.remove.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the joint from the world
					synchronized (Simulation.LOCK) {
						// remove the joint
						this.simulation.getWorld().removeJoint(joint);
					}
					// remove the node from the tree
					this.model.removeNodeFromParent(node);
				}
			}
		}
	}
	
	/**
	 * Shows a decompose dialog.
	 * <p>
	 * Adds a fixtures for each shape from the decomposed polygon if the user clicks add.
	 */
	private void addDecomposablePolygonAction() {
		// the current selection should have the body to add the fixture to
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// show a dialog to create the fixtures
				List<BodyFixture> fixtures = AddNonConvexFixtureDialog.show(ControlUtilities.getParentWindow(this), Icons.ADD_NON_CONVEX_POLYGON.getImage());
				// make sure the user didnt cancel the operation
				if (fixtures != null) {
					// add the fixture to the body
					synchronized (Simulation.LOCK) {
						// add all the fixtures
						for (BodyFixture fixture : fixtures) {
							body.addFixture(fixture);
						}
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.updateMass();
						}
					}
					for (BodyFixture fixture : fixtures) {
						// add the node to the tree
						DefaultMutableTreeNode fixtureNode = new DefaultMutableTreeNode(fixture);
						this.model.insertNodeInto(fixtureNode, node, node.getChildCount());
					}
					// expand the path to the new node
					this.tree.expandPath(path);
				}
			}
		}
	}
	
	/**
	 * Applies a force to the given body if the user accepts the input.
	 */
	private void applyForceAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// show the force input dialog
				Vector2 f = ApplyForceDialog.show(ControlUtilities.getParentWindow(this));
				// make sure the user accepted the input
				if (f != null) {
					synchronized (Simulation.LOCK) {
						body.applyForce(f);
					}
				}
			}
		}
	}
	
	/**
	 * Applies a torque to the given body if the user accepts the input.
	 */
	private void applyTorqueAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// show the torque input dialog
				double torque = ApplyTorqueDialog.show(ControlUtilities.getParentWindow(this));
				// check if it was cancelled
				if (torque != 0.0) {
					// apply it to the body
					synchronized (Simulation.LOCK) {
						body.applyTorque(torque);
					}
				}
			}
		}
	}
	
	/**
	 * Applies a forces at a point to the given body if the user accepts the input.
	 */
	private void applyForceAtPointAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// show the torque input dialog
				Vector2[] forcePoint = ApplyForceAtPointDialog.show(ControlUtilities.getParentWindow(this));
				// check if it was cancelled
				if (forcePoint != null) {
					// apply it to the body
					synchronized (Simulation.LOCK) {
						body.applyForce(forcePoint[0], forcePoint[1]);
					}
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, the force accumulator is cleared.
	 */
	private void clearForceAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this), 
						MessageFormat.format(Messages.getString("menu.context.body.clearForce.warning"), body.getName()), 
						Messages.getString("menu.context.body.clearForce.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// clear only the force accumulator
					synchronized (Simulation.LOCK) {
						body.clearAccumulatedForce();
					}
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, the torque accumulator is cleared.
	 */
	private void clearTorqueAction() {
		// the current selection should have the body selected
		TreePath path = this.tree.getSelectionPath();
		// make sure that something is selected
		if (path != null) {
			// get the currently selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure the selected node is a body
			if (node.getUserObject() instanceof SandboxBody) {
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this), 
						MessageFormat.format(Messages.getString("menu.context.body.clearTorque.warning"), body.getName()), 
						Messages.getString("menu.context.body.clearTorque.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// clear only the force accumulator
					synchronized (Simulation.LOCK) {
						body.clearAccumulatedTorque();
					}
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, all bodies are removed.
	 */
	private void removeAllBodiesAction() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(
				ControlUtilities.getParentWindow(this), 
				Messages.getString("menu.context.bodyFolder.removeAll.warning"), 
				Messages.getString("menu.context.bodyFolder.removeAll.warning.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// clear only the force accumulator
			synchronized (Simulation.LOCK) {
				// clear all the bodies
				this.simulation.getWorld().removeAllBodies();
				// clear the contacts
				this.simulation.getContactCounter().clear();
			}
			// refresh the tree
			this.bodyFolder.removeAllChildren();
			this.jointFolder.removeAllChildren();
			this.model.reload(this.bodyFolder);
			this.model.reload(this.jointFolder);
			
			// notify action listeners to remove selection
			this.notifyActionListeners("clear-all");
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, all joints are removed.
	 */
	private void removeAllJointsAction() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(
				ControlUtilities.getParentWindow(this), 
				Messages.getString("menu.context.jointFolder.removeAll.warning"), 
				Messages.getString("menu.context.jointFolder.removeAll.warning.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// clear only the force accumulator
			synchronized (Simulation.LOCK) {
				// clear all the joints
				this.simulation.getWorld().removeAllJoints();
			}
			// refresh the tree
			this.jointFolder.removeAllChildren();
			this.model.reload(this.jointFolder);
		}
	}
	
	/**
	 * Shows a dialog allowing the user to create a ray to cast against the world.
	 */
	private void addRayAction() {
		// create the body by showing the dialogs
		SandboxRay ray = AddRayDialog.show(ControlUtilities.getParentWindow(this));
		// make sure the user didn't cancel the operation
		if (ray != null) {
			// add the ray to the simulation
			synchronized (Simulation.LOCK) {
				this.simulation.getRays().add(ray);
			}
			// add ray to the rayFolder node
			DefaultMutableTreeNode rayNode = new DefaultMutableTreeNode(ray);
			// insert into the tree
			this.model.insertNodeInto(rayNode, this.rayFolder, this.rayFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(rayNode.getPath()).getParentPath());
		}
	}
	
	/**
	 * Shows a confirmation dialog to the user to make sure they want to remove all rays.
	 * <p>
	 * If the user accepts, all rays in the Rays folder will be removed.
	 */
	private void removeAllRaysAction() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(
				ControlUtilities.getParentWindow(this), 
				Messages.getString("menu.context.rayFolder.removeAll.warning"), 
				Messages.getString("menu.context.rayFolder.removeAll.warning.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// clear all the rays
			synchronized (Simulation.LOCK) {
				this.simulation.getRays().clear();
			}
			// refresh the tree
			this.rayFolder.removeAllChildren();
			this.model.reload(this.rayFolder);
		}
	}
	
	/**
	 * Shows the a dialog allowing the user to edit the ray.
	 */
	private void editRayAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a ray that is selected
			if (node.getUserObject() instanceof SandboxRay) {
				// get the ray from the node
				SandboxRay ray = (SandboxRay)node.getUserObject();
				// show the right dialog
				synchronized (Simulation.LOCK) {
					SandboxRay nRay = EditRayDialog.show(ControlUtilities.getParentWindow(this), ray);
					this.simulation.getRays().remove(ray);
					this.simulation.getRays().add(nRay);
					node.setUserObject(nRay);
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation dialog to the user to make sure they want to remove the selected ray.
	 * <p>
	 * If the user accepts, the selected ray is removed
	 */
	private void removeRayAction() {
		// get the currently selected body
		TreePath path = this.tree.getSelectionPath();
		// make sure something is selected
		if (path != null) {
			// get the selected node
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// make sure its a ray that is selected
			if (node.getUserObject() instanceof SandboxRay) {
				// get the ray from the node
				SandboxRay ray = (SandboxRay)node.getUserObject();
				// make sure they are sure
				int choice = JOptionPane.showConfirmDialog(
						ControlUtilities.getParentWindow(this), 
						MessageFormat.format(Messages.getString("menu.context.ray.remove.warning"), ray.getName()), 
						Messages.getString("menu.context.ray.remove.warning.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the joint from the world
					synchronized (Simulation.LOCK) {
						this.simulation.getRays().remove(ray);
					}
					// remove the node from the tree
					this.model.removeNodeFromParent(node);
				}
			}
		}
	}
	
	/**
	 * Shows a dialog to edit simulation settings.
	 * @since 1.0.4
	 */
	private void editSettingsAction() {
		SettingsDialog.show(ControlUtilities.getParentWindow(this), this.simulation.getWorld().getSettings());
	}
	
	/**
	 * Customer tree node renderer for the world tree.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static class Renderer extends DefaultTreeCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -873122899854000873L;
		
		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object data = node.getUserObject();
			
			// set the icon and text depending on the type of user object
			
			if (data instanceof Body) {
				this.setIcon(Icons.BODY);
				this.setDisabledIcon(Icons.DISABLED_BODY);
				this.setText(((SandboxBody)data).getName());
			} else if (data instanceof Fixture) {
				Fixture fixture = (Fixture)data;
				Shape shape = fixture.getShape();
				if (shape instanceof Rectangle) {
					this.setIcon(Icons.RECTANGLE);
					this.setDisabledIcon(Icons.DISABLED_RECTANGLE);
				} else if (shape instanceof Polygon) {
					this.setIcon(Icons.POLYGON);
					this.setDisabledIcon(Icons.DISABLED_POLYGON);
				} else if (shape instanceof Circle) {
					this.setIcon(Icons.CIRCLE);
					this.setDisabledIcon(Icons.DISABLED_CIRCLE);
				} else if (shape instanceof Segment) {
					this.setIcon(Icons.SEGMENT);
					this.setDisabledIcon(Icons.DISABLED_SEGMENT);
				}
				this.setText((String)fixture.getUserData());
			} else if (data instanceof World) {
				World world = (World)data;
				this.setIcon(Icons.WORLD);
				this.setDisabledIcon(Icons.DISABLED_WORLD);
				this.setText(world.getUserData() + " [" + world.getBodyCount() + ", " + world.getJointCount() + "]");
			} else if (data instanceof Joint) {
				// set the text
				this.setText((String)((Joint)data).getUserData());
				// set the icon
				if (data instanceof AngleJoint) {
					this.setIcon(Icons.ANGLE_JOINT);
					this.setDisabledIcon(Icons.DISABLED_ANGLE_JOINT);
				} else if (data instanceof DistanceJoint) {
					this.setIcon(Icons.DISTANCE_JOINT);
					this.setDisabledIcon(Icons.DISABLED_DISTANCE_JOINT);
				} else if (data instanceof FrictionJoint) {
					this.setIcon(Icons.FRICTION_JOINT);
					this.setDisabledIcon(Icons.DISABLED_FRICTION_JOINT);
				} else if (data instanceof MotorJoint) {
					this.setIcon(Icons.MOTOR_JOINT);
					this.setDisabledIcon(Icons.DISABLED_MOTOR_JOINT);
				} else if (data instanceof PinJoint) {
					this.setIcon(Icons.MOUSE_JOINT);
					this.setDisabledIcon(Icons.DISABLED_MOUSE_JOINT);
				} else if (data instanceof PrismaticJoint) {
					this.setIcon(Icons.PRISMATIC_JOINT);
					this.setDisabledIcon(Icons.DISABLED_PRISMATIC_JOINT);
				} else if (data instanceof PulleyJoint) {
					this.setIcon(Icons.PULLEY_JOINT);
					this.setDisabledIcon(Icons.DISABLED_PULLEY_JOINT);
				} else if (data instanceof RevoluteJoint) {
					this.setIcon(Icons.REVOLUTE_JOINT);
					this.setDisabledIcon(Icons.DISABLED_REVOLUTE_JOINT);
				} else if (data instanceof RopeJoint) {
					this.setIcon(Icons.ROPE_JOINT);
					this.setDisabledIcon(Icons.DISABLED_ROPE_JOINT);
				} else if (data instanceof WeldJoint) {
					this.setIcon(Icons.WELD_JOINT);
					this.setDisabledIcon(Icons.DISABLED_WELD_JOINT);
				} else if (data instanceof WheelJoint) {
					this.setIcon(Icons.WHEEL_JOINT);
					this.setDisabledIcon(Icons.DISABLED_WHEEL_JOINT);
				}
			} else if (data instanceof Bounds) {
				this.setIcon(Icons.BOUNDS);
				this.setDisabledIcon(Icons.DISABLED_BOUNDS);
				if (data instanceof NullBounds) {
					this.setText(Messages.getString("panel.tree.noBounds"));
				} else if (data instanceof AxisAlignedBounds) {
					AxisAlignedBounds bounds = (AxisAlignedBounds)data;
					this.setText("[" + bounds.getWidth() + ", " + bounds.getHeight() + "]");
				} else {
					this.setText(data.getClass().getSimpleName());
				}
			} else if (data instanceof Ray) {
				this.setIcon(Icons.RAY);
				this.setDisabledIcon(Icons.DISABLED_RAY);
				this.setText(((SandboxRay)data).getName());
			} else {
				// just show folder icon
				if (!expanded) {
					this.setIcon(Icons.FOLDER_CLOSED);
					this.setDisabledIcon(Icons.DISABLED_FOLDER_CLOSED);
				} else {
					this.setIcon(Icons.FOLDER_OPEN);
					this.setDisabledIcon(Icons.DISABLED_FOLDER_OPEN);
				}
			}
			
			return this;
		}
	}
}
