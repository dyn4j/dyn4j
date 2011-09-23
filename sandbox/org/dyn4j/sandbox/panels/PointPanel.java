package org.dyn4j.sandbox.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to input a point or vector.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PointPanel extends JPanel implements ActionListener, PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = 5446710351912720509L;
	
	/** The text field for the x value */
	private JFormattedTextField txtX;
	
	/** The text field for the y value */
	private JFormattedTextField txtY;
	
	/** The button to remove the point */
	protected JButton btnRemove;
	
	/** The button to add the point */
	protected JButton btnAdd;
	
	/**
	 * Default constructor.
	 */
	public PointPanel() {
		this(0.0, 0.0);
	}
	
	/**
	 * Full constructor.
	 * @param x the initial x value
	 * @param y the initial y value
	 */
	public PointPanel(double x, double y) {
		JLabel lblX = new JLabel("x");
		JLabel lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		
		this.txtX.setValue(x);
		this.txtY.setValue(y);
		
		this.txtX.addPropertyChangeListener("value", this);
		this.txtY.addPropertyChangeListener("value", this);
		
		this.btnAdd = new JButton();
		this.btnAdd.setIcon(Icons.ADD);
		this.btnAdd.setToolTipText("Add a new point after this point.");
		this.btnAdd.addActionListener(this);
		this.btnAdd.setActionCommand("add");
		
		this.btnRemove = new JButton();
		this.btnRemove.setIcon(Icons.REMOVE);
		this.btnRemove.setToolTipText("Remove this point.");
		this.btnRemove.addActionListener(this);
		this.btnRemove.setActionCommand("remove");
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblX)
				.addComponent(this.txtX)
				.addComponent(lblY)
				.addComponent(this.txtY)
				.addComponent(this.btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnRemove, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblX)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnRemove, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Adds an action listener to listen for button events.
	 * @param actionListener the action listener to add
	 */
	public void addActionListener(ActionListener actionListener) {
		this.listenerList.add(ActionListener.class, actionListener);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		// set the source to this
		e.setSource(this);
		// forward the event to the listeners on this class
		for (ActionListener listener : listeners) {
			listener.actionPerformed(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
		// forward the event to the listeners on this class
		for (ActionListener listener : listeners) {
			listener.actionPerformed(event);
		}
	}
	
	/**
	 * Returns the x value of the point.
	 * @return double
	 */
	public double getValueX() {
		Number number = (Number)this.txtX.getValue();
		return number.doubleValue();
	}
	
	/**
	 * Returns the y value of the point.
	 * @return double
	 */
	public double getValueY() {
		Number number = (Number)this.txtY.getValue();
		return number.doubleValue();
	}
}
