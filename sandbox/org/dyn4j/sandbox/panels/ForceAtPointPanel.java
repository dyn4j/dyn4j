package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to apply a force to a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ForceAtPointPanel extends JPanel implements InputPanel {	
	/** The version id */
	private static final long serialVersionUID = 2036550577745400369L;

	/** The x value of the force input */
	private JFormattedTextField txtFX;
	
	/** The y value of the force input */
	private JFormattedTextField txtFY;
	
	/** The x value of the application point input */
	private JFormattedTextField txtPX;
	
	/** The y value of the application point input */
	private JFormattedTextField txtPY;
	
	/**
	 * Default constructor.
	 */
	public ForceAtPointPanel() {
		JLabel lblPoint = new JLabel("Point", Icons.INFO, JLabel.LEFT);
		lblPoint.setToolTipText("The application point of the force in world coordinates.");
		JLabel lblPX = new JLabel("x");
		JLabel lblPY = new JLabel("y");
		
		this.txtPX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPX.addFocusListener(new SelectTextFocusListener(this.txtPX));
		this.txtPY.addFocusListener(new SelectTextFocusListener(this.txtPY));
		this.txtPX.setColumns(7);
		this.txtPY.setColumns(7);
		this.txtPX.setValue(0.0);
		this.txtPY.setValue(0.0);
		
		JLabel lblForce = new JLabel("Force", Icons.INFO, JLabel.LEFT);
		lblForce.setToolTipText("The force to apply to the point of the body in Newtons.");
		JLabel lblFX = new JLabel("x");
		JLabel lblFY = new JLabel("y");
		
		this.txtFX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtFY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtFX.addFocusListener(new SelectTextFocusListener(this.txtFX));
		this.txtFY.addFocusListener(new SelectTextFocusListener(this.txtFY));
		this.txtFX.setColumns(7);
		this.txtFY.setColumns(7);
		this.txtFX.setValue(0.0);
		this.txtFY.setValue(0.0);
		
		// setup the force at point panel
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce)
						.addComponent(lblPoint))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFX)
								.addComponent(lblFX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFY)
								.addComponent(lblFY))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtPX)
								.addComponent(lblPX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtPY)
								.addComponent(lblPY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtFX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblFX))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtFY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblFY))))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPoint)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtPX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPX))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtPY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPY)))));
	}
	
	/**
	 * Returns the force given by the user.
	 * @return Vector2
	 */
	public Vector2 getForce() {
		Vector2 f = new Vector2(
				ControlUtilities.getDoubleValue(this.txtFX),
				ControlUtilities.getDoubleValue(this.txtFY));
		return f;
	}
	
	/**
	 * Returns the point given by the user.
	 * @return Vector2
	 */
	public Vector2 getPoint() {
		Vector2 p = new Vector2(
				ControlUtilities.getDoubleValue(this.txtPX),
				ControlUtilities.getDoubleValue(this.txtPY));
		return p;
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
