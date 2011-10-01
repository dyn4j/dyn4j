package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.dyn4j.sandbox.utilities.ExceptionUtilities;

/**
 * Dialog used to show exceptions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionDialog extends JDialog implements MouseListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 4223515214480697820L;
	
	/** Text area to display the stack trace */
	private JTextArea txtStackTrace;

	/** The copy/select popup menu */
	private JPopupMenu copyMenu;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 */
	private ExceptionDialog(Window owner, String title, String message, Exception e) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		this.setIconImage(null);
		// set the size
		this.setPreferredSize(new Dimension(500, 400));
		this.setResizable(false);
		
		// add the logo to the top
		JLabel lblMessage = new JLabel(message, UIManager.getIcon("OptionPane.errorIcon"), JLabel.LEFT);
		JLabel lblStackTrace = new JLabel("Stack Trace:");
		
		// add the about text section with clickable links
		this.txtStackTrace = new JTextArea();
		this.txtStackTrace.setEditable(false);
		this.txtStackTrace.setTabSize(4);
		this.txtStackTrace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		this.txtStackTrace.setText(ExceptionUtilities.getStackTrace(e));
		// set the cursor position to the top so that the scroller is at the top
		this.txtStackTrace.setCaretPosition(0);
		this.txtStackTrace.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		this.txtStackTrace.addMouseListener(this);
		// wrap the text pane in a scroll pane just in case
		JScrollPane scroller = new JScrollPane(this.txtStackTrace);
		
		JButton btnOk = new JButton("OK");
		btnOk.setActionCommand("close");
		btnOk.addActionListener(this);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblMessage)
				.addComponent(lblStackTrace)
				.addComponent(scroller)
				.addComponent(btnOk));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblMessage)
				.addComponent(lblStackTrace)
				.addComponent(scroller)
				.addComponent(btnOk));
		
		this.pack();
		
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
			this.copyMenu.show(this.txtStackTrace, event.getX(), event.getY());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("close".equals(event.getActionCommand())) {
			this.setVisible(false);
		} else if ("copy".equals(event.getActionCommand())) {
			// copy the selected text into the clipboard
			this.txtStackTrace.copy();
		} else {
			// select all
			this.txtStackTrace.selectAll();
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
	
	/**
	 * Shows a new exception dialog.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param message the message
	 * @param e the exception
	 */
	public static final void show(Window owner, String title, String message, Exception e) {
		// create the dialog
		ExceptionDialog dialog = new ExceptionDialog(owner, title, message, e);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
	}
}
