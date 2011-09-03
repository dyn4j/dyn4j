package org.dyn4j.sandbox.panels;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or modify a joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class JointPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 2476535326313105580L;
	
	/** The name label */
	protected JLabel lblName;
	
	/** The name text field */
	protected JTextField txtName;
	
	/** The collision enabled label */
	protected JLabel lblCollision;
	
	/** The collision enabled checkbox */
	protected JCheckBox chkCollision;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates the name and collision enabled fields and labels.
	 */
	protected JointPanel() {
		this.lblName = new JLabel("Name", Icons.INFO, JLabel.LEFT);
		this.lblName.setToolTipText("The name of the joint.");
		this.txtName = new JTextField("");
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		
		this.lblCollision = new JLabel("Collision Enabled", Icons.INFO, JLabel.LEFT);
		this.lblCollision.setToolTipText("Check to allow collision between the joined bodies.");
		this.chkCollision = new JCheckBox();
	}
	
	/**
	 * Sets the given joint's properties to the settings the user selected.
	 * @param joint the joint to modify
	 */
	public abstract void setJoint(Joint joint);
	
	/**
	 * Returns the currently configured joint.
	 * <p>
	 * If nothing has been set, the default joint is returned.
	 * @return Joint
	 */
	public abstract Joint getJoint();
	
	/**
	 * Returns a string explaining the joint.
	 * @return String
	 */
	public abstract String getDescription();

	/**
	 * Returns the double value of the number stored in the given text field.
	 * @param field the text field
	 * @return double the double value
	 */
	protected double getDoubleValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.doubleValue();
	}
}
