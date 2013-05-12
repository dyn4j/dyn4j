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
package org.dyn4j.sandbox.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;

import org.dyn4j.sandbox.controls.ClasspathHtmlEditorKit;
import org.dyn4j.sandbox.help.HelpNode;
import org.dyn4j.sandbox.help.HelpReader;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.resources.Messages;
import org.xml.sax.SAXException;

/**
 * Help contents dialog.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.4
 */
public class HelpDialog extends JDialog implements MouseListener, MouseMotionListener {
	/** The version id */
	private static final long serialVersionUID = 409567824656922047L;
	
	/** The help contents tree */
	private JTree tree;
	
	/** The help html pane */
	private JEditorPane text;
	
	/**
	 * Default constructor.
	 */
	public HelpDialog() {
		super(null, Messages.getString("dialog.help.title"), ModalityType.MODELESS);
		
		HelpNode root = null;
		// have a tree of help items
		// get the node tree
		try {
			root = HelpReader.fromXml(HelpDialog.class.getResourceAsStream("/org/dyn4j/sandbox/help/Help.xml"));
		} catch (ParserConfigurationException e) {
			// just eat these exceptions
		} catch (SAXException e) {
			// just eat these exceptions
		} catch (IOException e) {
			// just eat these exceptions
		}
		
		this.tree = this.buildJTree(root);
		this.tree.setCellRenderer(new Renderer());
		this.tree.addMouseListener(this);
		this.tree.addMouseMotionListener(this);
		JScrollPane treeScroller = new JScrollPane(this.tree);
		treeScroller.setPreferredSize(new Dimension(200, 500));
		
		this.text = new JEditorPane();
		this.text.setEditorKit(new ClasspathHtmlEditorKit());
		this.text.setEditable(false);
		try {
			this.text.setPage(HelpDialog.class.getResource("/org/dyn4j/sandbox/help/general.html"));
		} catch (IOException e) {
			// just eat this one
		}
		JScrollPane textScroller = new JScrollPane(this.text);
		textScroller.setPreferredSize(new Dimension(600, 500));
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(treeScroller);
		split.setRightComponent(textScroller);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(split, BorderLayout.CENTER);
		
		this.pack();
		// have a jeditorpane for current help item
	}
	
	/**
	 * Builds a new JTree from the given HelpNode tree.
	 * @param root the root HelpNode
	 * @return JTree
	 */
	private JTree buildJTree(HelpNode root) {
		if (root == null) return new JTree();
		
		MutableTreeNode rn = new DefaultMutableTreeNode(root);
		DefaultTreeModel model = new DefaultTreeModel(rn);
		JTree tree = new JTree(model);
		
		for (int i = 0; i < root.nodes.size(); i++) {
			addHelpNodesToTree(root.nodes.get(i), model, rn);
		}
		
		// expand all nodes
		this.expandAllNodes(tree);
		
		return tree;
	}
	
	/**
	 * Recursive method to add nodes to the JTree.
	 * @param node the node to process
	 * @param model the model (used to add to the JTree)
	 * @param parent the parent node of the node to process
	 */
	private void addHelpNodesToTree(HelpNode node, DefaultTreeModel model, MutableTreeNode parent) {
		MutableTreeNode n = new DefaultMutableTreeNode(node);
		int index = model.getChildCount(parent);
		model.insertNodeInto(n, parent, index);
		
		for (int i = 0; i < node.nodes.size(); i++) {
			addHelpNodesToTree(node.nodes.get(i), model, n);
		}
	}
	
	/**
	 * Expands all the nodes of the given JTree.
	 * @param tree the tree
	 */
	private void expandAllNodes(JTree tree) {
		TreeNode root = (TreeNode)tree.getModel().getRoot();
		expandAllNodes(tree, new TreePath(root));
	}
	
	/**
	 * Recursive method to expand all the nodes of a tree.
	 * @param tree the tree
	 * @param parentPath the parent path
	 */
	private void expandAllNodes(JTree tree, TreePath parentPath) {
		TreeNode node = (TreeNode)parentPath.getLastPathComponent();
		
		for (int i = 0; i < node.getChildCount(); i++) {
			expandAllNodes(tree, parentPath.pathByAddingChild(node.getChildAt(i)));
		}
		
		tree.expandPath(parentPath);
	}
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
		TreePath path = this.tree.getPathForLocation(event.getX(), event.getY());
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			HelpNode n = (HelpNode)node.getUserObject();
			if (node != null && n != null) {
				if (n.path != null) {
					// load the path into the text pane
					try {
						this.text.setPage(HelpDialog.class.getResource(n.path));
					} catch (IOException e) {
						ExceptionDialog.show(
								this, 
								Messages.getString("dialog.help.exception.url.title"), 
								Messages.getString("dialog.help.exception.url.text"), 
								e);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent event) {}
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent event) {
		this.tree.setCursor(Cursor.getDefaultCursor());
	}
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent event) {}
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseReleased(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent event) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent event) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent event) {
		TreePath path = this.tree.getPathForLocation(event.getX(), event.getY());
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			HelpNode n = (HelpNode)node.getUserObject();
			if (node != null && n != null) {
				if (n.path != null) {
					this.tree.setCursor(new Cursor(Cursor.HAND_CURSOR));
					return;
				}
			}
		}
		this.tree.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Customer tree node renderer for the help tree.
	 * @author William Bittle
	 * @version 1.0.4
	 * @since 1.0.4
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
			HelpNode data = (HelpNode)node.getUserObject();
			
			// set the icon and text depending on the type of user object
			if (data.nodes != null && data.nodes.size() > 0) {
				// just show folder icon
				if (!expanded) {
					this.setIcon(Icons.HELP_CLOSED);
				} else {
					this.setIcon(Icons.HELP_OPEN);
				}
			} else {
				this.setIcon(Icons.HELP_LEAF);
			}
			
			if (data.path != null) {
				String text;
				if (sel || hasFocus) {
					text = "<html><u>" + data.name + "</u></html>";
				} else {
					text = "<html><u>" + data.name + "</u></html>";
				}
				this.setText(text);
			} else {
				this.setText(data.name);
			}
			
			return this;
		}
	}
	
	/**
	 * Shows a new {@link HelpDialog} using the given owner.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		HelpDialog dialog = new HelpDialog();
		dialog.setLocationRelativeTo(owner);
		dialog.setIconImage(Icons.HELP.getImage());
		dialog.setVisible(true);
	}
}
