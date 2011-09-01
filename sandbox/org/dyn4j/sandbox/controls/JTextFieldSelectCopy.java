package org.dyn4j.sandbox.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 * JTextField with a context menu with select all and copy text options.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class JTextFieldSelectCopy extends JTextField implements ActionListener, MouseListener {
	/** The version id */
	private static final long serialVersionUID = -1887762514142150929L;
	
	/** The popup context menu */
	private JPopupMenu copyMenu;
	
	/**
	 * Default constructor.
	 */
	public JTextFieldSelectCopy() {
		this.addMouseListener(this);
		
		// right click copy menu
		
		this.copyMenu = new JPopupMenu();
		
		JMenuItem mnuCopy = new JMenuItem("Copy");
		mnuCopy.setActionCommand("copy");
		mnuCopy.addActionListener(this);
		
		JMenuItem mnuSelectAll = new JMenuItem("Select All");
		mnuSelectAll.setActionCommand("selectall");
		mnuSelectAll.addActionListener(this);
		
		this.copyMenu.add(mnuCopy);
		this.copyMenu.add(mnuSelectAll);
	}
	
	/**
	 * Shows the popup menu wherever the user clicked if the user clicked
	 * the popup trigger mouse key.
	 * @param event the mouse event
	 */
	private void showPopup(MouseEvent event) {
		if (event.isPopupTrigger()) {
			this.copyMenu.show(this, event.getX(), event.getY());
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("copy".equals(event.getActionCommand())) {
			// copy the selected text into the clipboard
			this.copy();
		} else {
			// select all
			this.selectAll();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		this.showPopup(e);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		this.showPopup(e);
	}
	
	// mouse events not used
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
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
}
