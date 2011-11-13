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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.NullBounds;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.dialogs.AddBodyDialog;
import org.dyn4j.sandbox.dialogs.AddConvexFixtureDialog;
import org.dyn4j.sandbox.dialogs.AddConvexHullFixtureDialog;
import org.dyn4j.sandbox.dialogs.AddJointDialog;
import org.dyn4j.sandbox.dialogs.AddNonConvexFixtureDialog;
import org.dyn4j.sandbox.dialogs.ApplyForceAtPointDialog;
import org.dyn4j.sandbox.dialogs.ApplyForceDialog;
import org.dyn4j.sandbox.dialogs.ApplyTorqueDialog;
import org.dyn4j.sandbox.dialogs.EditBodyDialog;
import org.dyn4j.sandbox.dialogs.EditFixtureDialog;
import org.dyn4j.sandbox.dialogs.EditJointDialog;
import org.dyn4j.sandbox.dialogs.EditWorldDialog;
import org.dyn4j.sandbox.dialogs.SetBoundsDialog;
import org.dyn4j.sandbox.events.BodyActionEvent;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to display and manage the World object using a JTree interface.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class WorldTreePanel extends JPanel implements MouseListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4557154805670204181L;
	
	/** The world object */
	private World world;
	
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
	
	/** The popup menu for body nodes */
	private JPopupMenu popBody;
	
	/** The popup menu for fixture nodes */
	private JPopupMenu popFixture;
	
	/** The popup menu for joint nodes */
	private JPopupMenu popJoint;
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a JTree inside a JPanel that is used to interact with the objects in the world.
	 * @param world the world
	 */
	public WorldTreePanel(World world) {
		this.world = world;
		
		this.root = new DefaultMutableTreeNode(world, true);
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
		this.bodyFolder = new DefaultMutableTreeNode("Bodies");
		this.model.insertNodeInto(this.bodyFolder, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.bodyFolder.getPath()).getParentPath());
		
		// folder to contain the joints
		this.jointFolder = new DefaultMutableTreeNode("Joints");
		this.model.insertNodeInto(this.jointFolder, this.root, this.root.getChildCount());
		this.tree.expandPath(new TreePath(this.jointFolder.getPath()).getParentPath());
		
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
	 * Sets the world of this world tree panel.
	 * <p>
	 * This method will create the nodes for all objects.
	 * @param world the world
	 */
	public void setWorld(World world) {
		this.world = world;
		this.root.setUserObject(world);
		
		// reset everything
		this.bounds.setUserObject(new NullBounds());
		this.bodyFolder.removeAllChildren();
		this.jointFolder.removeAllChildren();
		
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
				// expand the path to the new node
				this.tree.expandPath(new TreePath(fixtureNode.getPath()).getParentPath());
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
	}
	
	/**
	 * Creates the context menus for the different item types.
	 */
	private void createContextMenus() {
		
		// create the world popup menu
		
		this.popWorld = new JPopupMenu();
		
		JMenuItem mnuEditWorld = new JMenuItem("Edit");
		mnuEditWorld.setActionCommand("editWorld");
		mnuEditWorld.addActionListener(this);
		mnuEditWorld.setIcon(Icons.EDIT_WORLD);
		
		JMenuItem mnuClearWorld = new JMenuItem("Remove All Bodies And Joints");
		mnuClearWorld.setActionCommand("clear-all");
		mnuClearWorld.addActionListener(this);
		mnuClearWorld.setIcon(Icons.REMOVE);
		
		this.popWorld.add(mnuEditWorld);
		this.popWorld.addSeparator();
		this.popWorld.add(mnuClearWorld);
		
		// create the bounds popup menu
		
		this.popBounds = new JPopupMenu();
		
		JMenuItem mnuSetBounds = new JMenuItem("Set Bounds");
		mnuSetBounds.setActionCommand("set-bounds");
		mnuSetBounds.addActionListener(this);
		mnuSetBounds.setIcon(Icons.SET_BOUNDS);
		
		JMenuItem mnuUnsetBounds = new JMenuItem("Unset Bounds");
		mnuUnsetBounds.setActionCommand("unset-bounds");
		mnuUnsetBounds.addActionListener(this);
		mnuUnsetBounds.setIcon(Icons.UNSET_BOUNDS);
		
		this.popBounds.add(mnuSetBounds);
		this.popBounds.add(mnuUnsetBounds);
		
		// create the body folder popup
		
		this.popBodyFolder = new JPopupMenu();
		
		JMenuItem mnuAddbody = new JMenuItem("Add Body");
		JMenuItem mnuRemoveAllBodies = new JMenuItem("Remove All Bodies");
		
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
		
		JMenuItem mnuAddAngleJoint = new JMenuItem("Add Angle Joint");
		JMenuItem mnuAddDistanceJoint = new JMenuItem("Add Distance Joint");
		JMenuItem mnuAddFrictionJoint = new JMenuItem("Add Friction Joint");
		JMenuItem mnuAddPrismaticJoint = new JMenuItem("Add Prismatic Joint");
		JMenuItem mnuAddPulleyJoint = new JMenuItem("Add Pulley Joint");
		JMenuItem mnuAddRevoluteJoint = new JMenuItem("Add Revolute Joint");
		JMenuItem mnuAddRopeJoint = new JMenuItem("Add Rope Joint");
		JMenuItem mnuAddWeldJoint = new JMenuItem("Add Weld Joint");
		JMenuItem mnuAddWheelJoint = new JMenuItem("Add Wheel Joint");
		JMenuItem mnuAddMouseJoint = new JMenuItem("Add Mouse Joint");
		JMenuItem mnuRemoveAllJoints = new JMenuItem("Remove All Joints");
		
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
		this.popJointFolder.addSeparator();
		this.popJointFolder.add(mnuRemoveAllJoints);
		
		// create the body node popup menu
		
		this.popBody = new JPopupMenu();
		
		JMenuItem mnuEditBody = new JMenuItem("Edit");
		JMenuItem mnuRemoveBody = new JMenuItem("Remove");
		JMenuItem mnuAddCircle = new JMenuItem("Add Circle Fixture");
		JMenuItem mnuAddRectangle = new JMenuItem("Add Rectangle Fixture");
		JMenuItem mnuAddPolygon = new JMenuItem("Add Convex Polygon Fixture");
		JMenuItem mnuAddSegment = new JMenuItem("Add Segment Fixture");
		JMenuItem mnuAddHull = new JMenuItem("Add Convex Hull Fixture");
		JMenuItem mnuAddDecompose = new JMenuItem("Add Non-Convex Polygon Fixtures");
		JMenuItem mnuRemoveAllFixtures = new JMenuItem("Remove All Fixtures");
		JMenuItem mnuApplyForce = new JMenuItem("Apply Force");
		JMenuItem mnuApplyTorque = new JMenuItem("Apply Torque");
		JMenuItem mnuApplyForceAtPoint = new JMenuItem("Apply Force At Point");
		JMenuItem mnuClearForce = new JMenuItem("Clear Accumulated Force");
		JMenuItem mnuClearTorque = new JMenuItem("Clear Accumulated Torque");
		
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
		
		// create the fixture node popup menu
		
		this.popFixture = new JPopupMenu();
		
		JMenuItem mnuEditFixture = new JMenuItem("Edit");
		JMenuItem mnuRemoveFixture = new JMenuItem("Remove");
		
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
		
		JMenuItem mnuEditJoint = new JMenuItem("Edit");
		JMenuItem mnuRemoveJoint = new JMenuItem("Remove");
		
		// add default icons so that the menu is the correct size initially
		mnuEditJoint.setIcon(Icons.EDIT_ANGLE_JOINT);
		mnuRemoveJoint.setIcon(Icons.REMOVE_ANGLE_JOINT);
		
		mnuEditJoint.setActionCommand("editJoint");
		mnuRemoveJoint.setActionCommand("removeJoint");
		
		mnuEditJoint.addActionListener(this);
		mnuRemoveJoint.addActionListener(this);
		
		this.popJoint.add(mnuEditJoint);
		this.popJoint.add(mnuRemoveJoint);
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
				} else if (userData instanceof Body) {
					// show the body context menu
					this.popBody.show(this.tree, x, y);
				} else if (userData instanceof BodyFixture) {
					// show the fixture context menu
					this.popFixture.show(this.tree, x, y);
					// get the type of fixture
					Convex convex = ((BodyFixture)userData).getShape();
					// check the shape
					if (convex.isType(Circle.TYPE)) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_CIRCLE);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_CIRCLE);
					} else if (convex.isType(Rectangle.TYPE)) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_RECTANGLE);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_RECTANGLE);
					} else if (convex.isType(Polygon.TYPE)) {
						((JMenuItem)this.popFixture.getComponent(0)).setIcon(Icons.EDIT_POLYGON);
						((JMenuItem)this.popFixture.getComponent(1)).setIcon(Icons.REMOVE_POLYGON);
					} else if (convex.isType(Segment.TYPE)) {
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
					} else if (joint instanceof MouseJoint) {
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
		} else if ("addMouseJoint".equals(command)) {
			this.addJointAction(MouseJoint.class);
		} else if ("editJoint".equals(command)) {
			this.editJointAction();
		} else if ("removeJoint".equals(command)) {
			this.removeJointAction();
		} else if ("applyForce".equals(command)) {
			this.applyForce();
		} else if ("applyTorque".equals(command)) {
			this.applyTorque();
		} else if ("applyForceAtPoint".equals(command)) {
			this.applyForceAtPoint();
		} else if ("clearForce".equals(command)) {
			this.clearForce();
		} else if ("clearTorque".equals(command)) {
			this.clearTorque();
		} else if ("removeAllBodies".equals(command)) {
			this.removeAllBodies();
		} else if ("removeAllJoints".equals(command)) {
			this.removeAllJoints();
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
		synchronized (this.world) {
			int size = this.world.getBodyCount();
			if (size != 0) {
				bodies = new SandboxBody[size];
				for (int i = 0; i < size; i++) {
					bodies[i] = (SandboxBody)this.world.getBody(i);
				}
				return bodies;
			} else {
				return null;
			}
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
		int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove all bodies and joints?", "Clear Bodies and Joints", JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// remove it all from the world
			synchronized (this.world) {
				this.world.removeAll();
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
		EditWorldDialog.show(ControlUtilities.getParentWindow(this), this.world);
		
		this.model.nodeChanged(this.root);
	}
	
	/**
	 * Shows a Rectangular bounds dialog.
	 * <p>
	 * If the user clicks the set button the new bounds are set on the world object.
	 */
	private void setBoundsAction() {
		Bounds bounds = null;
		synchronized (this.world) {
			bounds = this.world.getBounds();
		}
		RectangularBounds b = null;
		if (bounds instanceof NullBounds) {
			b = SetBoundsDialog.show(ControlUtilities.getParentWindow(this), null);
		} else {
			b = SetBoundsDialog.show(ControlUtilities.getParentWindow(this), (RectangularBounds)bounds);
		}
		if (b != null) {
			synchronized (this.world) {
				this.world.setBounds(b);
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
		synchronized (this.world) {
			this.world.setBounds(bounds);
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
		SandboxBody body = AddBodyDialog.show(ControlUtilities.getParentWindow(this), "Add New Body");
		// make sure the user didn't cancel the operation
		if (body != null) {
			// add the body to the world
			synchronized (this.world) {
				this.world.add(body);
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
				EditBodyDialog.show(ControlUtilities.getParentWindow(this), "Edit Body", body);
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove " + body.getName() + "?", "Remove Body", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (this.world) {
						this.world.remove(body);
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
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// create the fixture by showing the dialogs (don't show the local transform panel if its the first fixture)
				BodyFixture fixture = AddConvexFixtureDialog.show(ControlUtilities.getParentWindow(this), icon, "Add New Fixture", shapePanel);
				// make sure the user didnt cancel the operation
				if (fixture != null) {
					// add the fixture to the body
					synchronized (this.world) {
						body.addFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.setMass(body.getMass().getType());
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
				// get the body from the node
				SandboxBody body = (SandboxBody)node.getUserObject();
				// create the fixture by showing the dialogs (don't show the local transform panel if its the first fixture)
				BodyFixture fixture = AddConvexHullFixtureDialog.show(ControlUtilities.getParentWindow(this));
				// make sure the user didnt cancel the operation
				if (fixture != null) {
					// add the fixture to the body
					synchronized (this.world) {
						body.addFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.setMass(body.getMass().getType());
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove " + fixture.getUserData() + " from " + body.getName() + "?", "Remove Fixture", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (this.world) {
						// remove the fixture
						body.removeFixture(fixture);
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.setMass(body.getMass().getType());
						}
					}
					// remove the node from the tree
					this.model.removeNodeFromParent(node);
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove all the fixtures from " + body.getName() + "?", "Remove All Fixtures", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the body from the world
					synchronized (this.world) {
						// remove the fixture
						body.removeAllFixtures();
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.setMass(body.getMass().getType());
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
				Convex convex = fixture.getShape();
				Image icon;
				if (convex.isType(Circle.TYPE)) {
					icon = Icons.EDIT_CIRCLE.getImage();
				} else if (convex.isType(Rectangle.TYPE)) {
					icon = Icons.EDIT_RECTANGLE.getImage();
				} else if (convex.isType(Segment.TYPE)) {
					icon = Icons.EDIT_SEGMENT.getImage();
				} else {
					icon = Icons.EDIT_POLYGON.getImage();
				}
				// make sure they are sure
				EditFixtureDialog.show(ControlUtilities.getParentWindow(this), icon, "Edit Fixture", body, fixture);
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
		SandboxBody[] bodies = this.getBodies();
		// check the joint class type
		if (bodies == null || bodies.length == 0 || (clazz != MouseJoint.class && bodies.length == 1)) {
			JOptionPane.showMessageDialog(ControlUtilities.getParentWindow(this),
					"The world must contain at least 1 body" +
					"\nbefore a mouse joint can be added and" +
					"\nat least 2 bodies for all other joints.", "Notice", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Joint joint = AddJointDialog.show(ControlUtilities.getParentWindow(this), bodies, clazz);
		if (joint != null) {
			// add the joint to the world
			synchronized (this.world) {
				this.world.add(joint);
			}
			// add the joint to the root node
			DefaultMutableTreeNode jointNode = new DefaultMutableTreeNode(joint);
			// insert into the tree
			this.model.insertNodeInto(jointNode, this.jointFolder, this.jointFolder.getChildCount());
			// expand the path to the new node
			this.tree.expandPath(new TreePath(jointNode.getPath()).getParentPath());
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
				EditJointDialog.show(ControlUtilities.getParentWindow(this), joint);
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove " + joint.getUserData() + "?", "Remove Joint", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// remove the joint from the world
					synchronized (this.world) {
						// remove the joint
						this.world.remove(joint);
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
				List<BodyFixture> fixtures = AddNonConvexFixtureDialog.show(ControlUtilities.getParentWindow(this), Icons.ADD_NON_CONVEX_POLYGON.getImage(), "Add Non-Convex Polygon Fixtures");
				// make sure the user didnt cancel the operation
				if (fixtures != null) {
					// add the fixture to the body
					synchronized (this.world) {
						// add all the fixtures
						for (BodyFixture fixture : fixtures) {
							body.addFixture(fixture);
						}
						// check if the mass is set explicitly or not
						if (!body.isMassExplicit()) {
							// reset the mass using the type it was before
							body.setMass(body.getMass().getType());
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
	private void applyForce() {
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
					synchronized (this.world) {
						body.apply(f);
					}
				}
			}
		}
	}
	
	/**
	 * Applies a torque to the given body if the user accepts the input.
	 */
	private void applyTorque() {
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
					synchronized (this.world) {
						body.apply(torque);
					}
				}
			}
		}
	}
	
	/**
	 * Applies a foces at a point to the given body if the user accepts the input.
	 */
	private void applyForceAtPoint() {
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
					synchronized (this.world) {
						body.apply(forcePoint[0], forcePoint[1]);
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
	private void clearForce() {
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to clear the force accumulator for " + body.getName() + "?", "Clear Force Accumulator", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// clear only the force accumulator
					body.clearAccumulatedForce();
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, the torque accumulator is cleared.
	 */
	private void clearTorque() {
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
				int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to clear the torque accumulator for " + body.getName() + "?", "Clear Torque Accumulator", JOptionPane.YES_NO_CANCEL_OPTION);
				// check the user's choice
				if (choice == JOptionPane.YES_OPTION) {
					// clear only the force accumulator
					body.clearAccumulatedTorque();
				}
			}
		}
	}
	
	/**
	 * Shows a confirmation message to make sure the user wants to proceed.
	 * <p>
	 * If the user accepts, all bodies are removed.
	 */
	private void removeAllBodies() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove all bodies?\n(This will remove all joints as well)", "Remove All Bodies", JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// clear only the force accumulator
			synchronized (this.world) {
				// clear all the bodies
				this.world.removeAllBodies();
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
	private void removeAllJoints() {
		// make sure they are sure
		int choice = JOptionPane.showConfirmDialog(ControlUtilities.getParentWindow(this), "Are you sure you want to remove all joints?", "Remove All Joints", JOptionPane.YES_NO_CANCEL_OPTION);
		// check the user's choice
		if (choice == JOptionPane.YES_OPTION) {
			// clear only the force accumulator
			synchronized (this.world) {
				// clear all the joints
				this.world.removeAllJoints();
			}
			// refresh the tree
			this.jointFolder.removeAllChildren();
			this.model.reload(this.jointFolder);
		}
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
				this.setText(((SandboxBody)data).getName());
			} else if (data instanceof Fixture) {
				Fixture fixture = (Fixture)data;
				Shape shape = fixture.getShape();
				if (shape.isType(Rectangle.TYPE)) {
					this.setIcon(Icons.RECTANGLE);
				} else if (shape.isType(Polygon.TYPE)) {
					this.setIcon(Icons.POLYGON);
				} else if (shape.isType(Circle.TYPE)) {
					this.setIcon(Icons.CIRCLE);
				} else if (shape.isType(Segment.TYPE)) {
					this.setIcon(Icons.SEGMENT);
				}
				this.setText((String)fixture.getUserData());
			} else if (data instanceof World) {
				World world = (World)data;
				this.setIcon(Icons.WORLD);
				this.setText(world.getUserData() + " [" + world.getBodyCount() + ", " + world.getJointCount() + "]");
			} else if (data instanceof Joint) {
				// set the text
				this.setText((String)((Joint)data).getUserData());
				// set the icon
				if (data instanceof AngleJoint) {
					this.setIcon(Icons.ANGLE_JOINT);
				} else if (data instanceof DistanceJoint) {
					this.setIcon(Icons.DISTANCE_JOINT);
				} else if (data instanceof FrictionJoint) {
					this.setIcon(Icons.FRICTION_JOINT);
				} else if (data instanceof MouseJoint) {
					this.setIcon(Icons.MOUSE_JOINT);
				} else if (data instanceof PrismaticJoint) {
					this.setIcon(Icons.PRISMATIC_JOINT);
				} else if (data instanceof PulleyJoint) {
					this.setIcon(Icons.PULLEY_JOINT);
				} else if (data instanceof RevoluteJoint) {
					this.setIcon(Icons.REVOLUTE_JOINT);
				} else if (data instanceof RopeJoint) {
					this.setIcon(Icons.ROPE_JOINT);
				} else if (data instanceof WeldJoint) {
					this.setIcon(Icons.WELD_JOINT);
				} else if (data instanceof WheelJoint) {
					this.setIcon(Icons.WHEEL_JOINT);
				}
			} else if (data instanceof Bounds) {
				this.setIcon(Icons.BOUNDS);
				if (data instanceof NullBounds) {
					this.setText("No bounds set");
				} else if (data instanceof RectangularBounds) {
					RectangularBounds bounds = (RectangularBounds)data;
					Rectangle r = bounds.getBounds();
					this.setText("[" + r.getWidth() + ", " + r.getHeight() + "]");
				} else {
					this.setText(data.getClass().getSimpleName());
				}
			} else {
				// just show folder icon
				if (!expanded) {
					this.setIcon(Icons.FOLDER_CLOSED);
				} else {
					this.setIcon(Icons.FOLDER_OPEN);
				}
			}
			
			return this;
		}
	}
}
