package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to apply a force to a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ForcePanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 2359226860458900772L;

	/** The x value of the force input */
	private JFormattedTextField txtX;
	
	/** The y value of the force input */
	private JFormattedTextField txtY;
	
	/**
	 * Default constructor.
	 */
	public ForcePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblForce = new JLabel("Force", Icons.INFO, JLabel.LEFT);
		lblForce.setToolTipText("The force in Newtons to apply to the center of the body.");
		JLabel lblX = new JLabel("x");
		JLabel lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtX.setColumns(7);
		this.txtY.setColumns(7);
		this.txtX.setValue(0.0);
		this.txtY.setValue(0.0);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(lblX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtY)
								.addComponent(lblY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(lblX)
								.addComponent(lblY))));
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
	 * Returns the force given by the user.
	 * @return Vector2
	 */
	public Vector2 getForce() {
		Vector2 f = new Vector2(
				this.getDoubleValue(this.txtX),
				this.getDoubleValue(this.txtY));
		return f;
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
