package org.dyn4j.sandbox.dialogs;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Dialog containing a sample file to show the formatting allowed.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SampleFileDialog extends JDialog implements MouseListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 7413682412938169769L;

	/** The copy/select popup menu */
	private JPopupMenu copyMenu;
	
	/** The text area containing the text */
	private JTextArea txtFile;
	
	/**
	 * Full constructor.
	 * @param parent the component displaying this dialog
	 * @param contents the file contents
	 */
	private SampleFileDialog(Component parent, String contents) {
		super(JOptionPane.getFrameForComponent(parent), "Sample File", ModalityType.APPLICATION_MODAL);
		
		this.txtFile = new JTextArea();
		this.txtFile.setText(contents);
		this.txtFile.setEditable(false);
		this.txtFile.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.txtFile.addMouseListener(this);
		
		JScrollPane scroller = new JScrollPane(this.txtFile);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scroller));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scroller));
		
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
			this.copyMenu.show(this.txtFile, event.getX(), event.getY());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if ("copy".equals(event.getActionCommand())) {
			// copy the selected text into the clipboard
			this.txtFile.copy();
		} else {
			// select all
			this.txtFile.selectAll();
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
	 * Shows a new dialog.
	 * @param parent the component who is showing this dialog
	 * @param contents the file contents
	 */
	public static final void show(Component parent, String contents) {
		SampleFileDialog dialog = new SampleFileDialog(parent, contents);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
}