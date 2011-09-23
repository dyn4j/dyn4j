package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to apply a torque to a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TorquePanel extends JPanel implements InputPanel {	
	/** The version id */
	private static final long serialVersionUID = -8783145037910466714L;
	
	/** The torque input */
	private JFormattedTextField txtT;
	
	/**
	 * Default constructor.
	 */
	public TorquePanel() {
		JLabel lblTorque = new JLabel("Torque", Icons.INFO, JLabel.LEFT);
		lblTorque.setToolTipText("The torque to apply to the body in Newton-Meters.");
		
		this.txtT = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtT.addFocusListener(new SelectTextFocusListener(this.txtT));
		this.txtT.setColumns(7);
		this.txtT.setValue(0.0);
		
		// setup the torque only panel
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtT)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblTorque)
						.addComponent(this.txtT, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Returns the double value of the number stored in the given text field.
	 * @param field the text field
	 * @return double the double value
	 */
	protected double getDoubleValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.doubleValue();
	}
	
	/**
	 * Returns the torque given by the user.
	 * @return double
	 */
	public double getTorque() {
		return this.getDoubleValue(this.txtT);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {}
}
