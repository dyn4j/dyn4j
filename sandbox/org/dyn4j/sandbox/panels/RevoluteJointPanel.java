package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;

/**
 * Panel used to create or edit an revolute joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RevoluteJointPanel extends JointPanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body 1 drop down label */
	private JLabel lblBody1;
	
	/** The body 2 drop down label */
	private JLabel lblBody2;
	
	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;
	
	// anchor points
	
	/** The anchor label */
	private JLabel lblAnchor;
	
	/** The x label for the anchor point */
	private JLabel lblX1;
	
	/** The y label for the anchor point */
	private JLabel lblY1;
	
	/** The anchor's x text field */
	private JFormattedTextField txtX1;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY1;
	
	/** The button to set anchor1 to body1's center of mass */
	private JButton btnUseCenter1;
	
	/** The button to set anchor2 to body2's center of mass */
	private JButton btnUseCenter2;
	
	// limits

	/** The limit enabled label */
	private JLabel lblLimitEnabled;
	
	/** The limit enable check box */
	private JCheckBox chkLimitEnabled;
	
	/** The upper limit label */
	private JLabel lblUpperLimit;
	
	/** The lower limit label */
	private JLabel lblLowerLimit;
	
	/** The upper limit text field */
	private JFormattedTextField txtUpperLimit;
	
	/** The lower limit text field */
	private JFormattedTextField txtLowerLimit;
	
	// motor

	/** The motor enabled label */
	private JLabel lblMotorEnabled;
	
	/** The motor enabled check box */
	private JCheckBox chkMotorEnabled;
	
	/** The motor speed label */
	private JLabel lblMotorSpeed;
	
	/** The motor speed text field */
	private JFormattedTextField txtMotorSpeed;
	
	/** The max motor force label */
	private JLabel lblMaxMotorTorque;
	
	/** The max motor force text field */
	private JFormattedTextField txtMaxMotorTorque;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public RevoluteJointPanel(RevoluteJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 an = joint.getAnchor1();
		boolean limit = joint.isLimitEnabled();
		boolean motor = joint.isMotorEnabled();
		double ul = joint.getUpperLimit();
		double ll = joint.getLowerLimit();
		double ms = joint.getMotorSpeed();
		double mt = joint.getMaxMotorTorque();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		this.lblBody1 = new JLabel("Body 1");
		this.lblBody2 = new JLabel("Body 2");
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		this.lblAnchor = new JLabel("Anchor");
		this.lblAnchor.setToolTipText("The anchor point to restrict rotation.");
		
		this.lblX1 = new JLabel("x");
		this.lblY1 = new JLabel("y");
		
		this.txtX1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX1.addFocusListener(new SelectTextFocusListener(this.txtX1));
		this.txtX1.setColumns(7);
		
		this.txtY1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY1.addFocusListener(new SelectTextFocusListener(this.txtY1));
		this.txtY1.setColumns(7);
		
		this.btnUseCenter1 = new JButton("Use Center");
		this.btnUseCenter1.setToolTipText("Set the anchor to the center of mass of body 1.");
		this.btnUseCenter1.setActionCommand("use-com1");
		this.btnUseCenter1.addActionListener(this);
		
		this.btnUseCenter2 = new JButton("Use Center");
		this.btnUseCenter2.setToolTipText("Set the anchor to the center of mass of body 2.");
		this.btnUseCenter2.setActionCommand("use-com2");
		this.btnUseCenter2.addActionListener(this);
		
		this.lblLimitEnabled = new JLabel("Limits Enabled");
		this.chkLimitEnabled = new JCheckBox();
		
		this.lblUpperLimit = new JLabel("Upper Limit");
		this.lblUpperLimit.setToolTipText("The upper limit of the joint in Meters.");
		this.txtUpperLimit = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtUpperLimit.addFocusListener(new SelectTextFocusListener(this.txtUpperLimit));
		this.txtUpperLimit.setToolTipText("The upper limit of the joint in Degrees.");
		this.txtUpperLimit.setColumns(8);
		
		this.lblLowerLimit = new JLabel("Lower Limit");
		this.lblLowerLimit.setToolTipText("The lower limit of the joint in Meters.");
		this.txtLowerLimit = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtLowerLimit.addFocusListener(new SelectTextFocusListener(this.txtLowerLimit));
		this.txtLowerLimit.setToolTipText("The lower limit of the joint in Degrees.");
		this.txtLowerLimit.setColumns(8);
		
		this.lblMotorEnabled = new JLabel("Motor Enabled");
		this.chkMotorEnabled = new JCheckBox();
		
		this.lblMotorSpeed = new JLabel("Motor Speed");
		this.lblMotorSpeed.setToolTipText("The motor speed in Degrees/Second.");
		this.txtMotorSpeed = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMotorSpeed.addFocusListener(new SelectTextFocusListener(this.txtMotorSpeed));
		this.txtMotorSpeed.setToolTipText("The motor speed in Degrees/Second.");
		
		this.lblMaxMotorTorque = new JLabel("Maximum Motor Torque");
		this.lblMaxMotorTorque.setToolTipText("The maximum torque the motor can apply in Newton-Meters.");
		this.txtMaxMotorTorque = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxMotorTorque.addFocusListener(new SelectTextFocusListener(this.txtMaxMotorTorque));
		this.txtMaxMotorTorque.setToolTipText("The maximum torque the motor can apply in Newton-Meters.");
		
		// set defaults

		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		this.txtX1.setValue(an.x);
		this.txtY1.setValue(an.y);
		
		this.chkLimitEnabled.setSelected(limit);
		this.txtUpperLimit.setValue(Math.toDegrees(ul));
		this.txtLowerLimit.setValue(Math.toDegrees(ll));
		
		this.chkMotorEnabled.setSelected(motor);
		this.txtMaxMotorTorque.setValue(mt);
		this.txtMotorSpeed.setValue(Math.toDegrees(ms));
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtX1.setEnabled(false);
			this.txtY1.setEnabled(false);
			this.btnUseCenter1.setEnabled(false);
			this.btnUseCenter2.setEnabled(false);
		}
		
		// setup the sections
		
		GroupLayout layout;
		
		// setup the general section
		
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " General "));
		
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblCollision)
						.addComponent(this.lblBody1)
						.addComponent(this.lblBody2)
						.addComponent(this.lblAnchor))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbBody1)
								.addComponent(this.btnUseCenter1))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbBody2)
								.addComponent(this.btnUseCenter2))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX1)
								.addComponent(this.lblX1)
								.addComponent(this.txtY1)
								.addComponent(this.lblY1))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAnchor)
						.addComponent(this.txtX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX1)
						.addComponent(this.txtY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY1)));
		
		// setup the limits section
		
		JPanel pnlLimits = new JPanel();
		pnlLimits.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Limits "));
		
		layout = new GroupLayout(pnlLimits);
		pnlLimits.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.lblUpperLimit)
						.addComponent(this.lblLowerLimit))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLimitEnabled)
						.addComponent(this.txtUpperLimit)
						.addComponent(this.txtLowerLimit)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLimitEnabled)
						.addComponent(this.chkLimitEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblUpperLimit)
						.addComponent(this.txtUpperLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLowerLimit)
						.addComponent(this.txtLowerLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the motor section
		
		JPanel pnlMotor = new JPanel();
		pnlMotor.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Motor "));
		
		layout = new GroupLayout(pnlMotor);
		pnlMotor.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMotorEnabled)
						.addComponent(this.lblMotorSpeed)
						.addComponent(this.lblMaxMotorTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkMotorEnabled)
						.addComponent(this.txtMotorSpeed)
						.addComponent(this.txtMaxMotorTorque)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMotorEnabled)
						.addComponent(this.chkMotorEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMotorSpeed)
						.addComponent(this.txtMotorSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaxMotorTorque)
						.addComponent(this.txtMaxMotorTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the layout of the sections
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlMotor));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits)
				.addComponent(pnlMotor));
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A revolute joint joins two bodies together at a single point.  The bodies cannot translate" +
				"relative to one another but can freely rotate about the anchor point.  The limits are angular " +
				"limits and work identically to the Angle Joint's limits.  The motor can be used to force a rotation " +
				"about the anchor point between the two bodies.";
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("use-com1".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody1.getSelectedItem()).getWorldCenter();
			this.txtX1.setValue(c.x);
			this.txtY1.setValue(c.y);
		} else if ("use-com2".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody2.getSelectedItem()).getWorldCenter();
			this.txtX1.setValue(c.x);
			this.txtY1.setValue(c.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof RevoluteJoint) {
			RevoluteJoint rj = (RevoluteJoint)joint;
			// set the super class properties
			rj.setUserData(this.txtName.getText());
			rj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			rj.setLimitEnabled(this.chkLimitEnabled.isSelected());
			rj.setLimits(
					Math.toRadians(this.getDoubleValue(this.txtLowerLimit)), 
					Math.toRadians(this.getDoubleValue(this.txtUpperLimit)));
			rj.setMaxMotorTorque(this.getDoubleValue(this.txtMaxMotorTorque));
			rj.setMotorEnabled(this.chkMotorEnabled.isSelected());
			rj.setMotorSpeed(Math.toRadians(this.getDoubleValue(this.txtMotorSpeed)));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getJoint()
	 */
	@Override
	public Joint getJoint() {
		// get the selected bodies
		SandboxBody body1 = (SandboxBody)this.cmbBody1.getSelectedItem();
		SandboxBody body2 = (SandboxBody)this.cmbBody2.getSelectedItem();
		
		// get the anchor points
		Vector2 a = new Vector2(
				this.getDoubleValue(this.txtX1),
				this.getDoubleValue(this.txtY1));
		
		RevoluteJoint rj = new RevoluteJoint(body1, body2, a);
		// set the super class properties
		rj.setUserData(this.txtName.getText());
		rj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		rj.setLimitEnabled(this.chkLimitEnabled.isSelected());
		rj.setLimits(
				Math.toRadians(this.getDoubleValue(this.txtLowerLimit)),
				Math.toRadians(this.getDoubleValue(this.txtUpperLimit)));
		rj.setMaxMotorTorque(this.getDoubleValue(this.txtMaxMotorTorque));
		rj.setMotorEnabled(this.chkMotorEnabled.isSelected());
		rj.setMotorSpeed(Math.toRadians(this.getDoubleValue(this.txtMotorSpeed)));
		
		return rj;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// must have some name
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			return false;
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			return false;
		}
		// check the limit
		if (this.getDoubleValue(this.txtLowerLimit) > this.getDoubleValue(this.txtUpperLimit)) {
			return false;
		}
		// check the maximum motor torque
		if (this.getDoubleValue(this.txtMaxMotorTorque) < 0.0) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		String name = this.txtName.getText();
		if (name == null || name.isEmpty()) {
			JOptionPane.showMessageDialog(owner, "You must specify a name for the joint.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// they can't be the same body
		if (this.cmbBody1.getSelectedItem() == this.cmbBody2.getSelectedItem()) {
			JOptionPane.showMessageDialog(owner, "You must select two different bodies.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// check the limit
		if (this.getDoubleValue(this.txtLowerLimit) > this.getDoubleValue(this.txtUpperLimit)) {
			JOptionPane.showMessageDialog(owner, "The lower limit must be less than or equal to the upper limit.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// check the maximum motor force
		if (this.getDoubleValue(this.txtMaxMotorTorque) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum motor torque must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
