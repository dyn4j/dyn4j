package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an friction joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FrictionJointPanel extends JointPanel implements InputPanel {
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
	
	// anchor point
	
	/** The anchor point label */
	private JLabel lblAnchor;
	
	/** The x label for the anchor point */
	private JLabel lblX;
	
	/** The y label for the anchor point */
	private JLabel lblY;
	
	/** The anchor's x text field */
	private JFormattedTextField txtX;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY;
	
	// max force and max torque
	
	/** The max force label */
	private JLabel lblMaxForce;
	
	/** The max torque label */
	private JLabel lblMaxTorque;
	
	/** The max force text field */
	private JFormattedTextField txtMaxForce;
	
	/** The max torque text field */
	private JFormattedTextField txtMaxTorque;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public FrictionJointPanel(FrictionJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 a1 = joint.getAnchor1();
		double mf = joint.getMaxForce();
		double mt = joint.getMaxTorque();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		this.lblBody1 = new JLabel("Body 1", Icons.INFO, JLabel.LEFT);
		this.lblBody2 = new JLabel("Body 2", Icons.INFO, JLabel.LEFT);
		this.lblBody1.setToolTipText("The first body participating in the joint.");
		this.lblBody2.setToolTipText("The second body participating in the joint.");
		
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		this.lblAnchor = new JLabel("Anchor", Icons.INFO, JLabel.LEFT);
		this.lblAnchor.setToolTipText("The anchor point for the bodies.");
		
		this.lblX = new JLabel("x");
		this.lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.setColumns(7);
		
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.setColumns(7);
		
		this.lblMaxForce = new JLabel("Maximum Force", Icons.INFO, JLabel.LEFT);
		this.lblMaxForce.setToolTipText("The maximum force the joint can apply in Newtons.");
		this.txtMaxForce = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxForce.addFocusListener(new SelectTextFocusListener(this.txtMaxForce));
		
		this.lblMaxTorque = new JLabel("Maximum Torque", Icons.INFO, JLabel.LEFT);
		this.lblMaxTorque.setToolTipText("The maximum torque the joint can apply in Newton-Meters.");
		this.txtMaxTorque = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxTorque.addFocusListener(new SelectTextFocusListener(this.txtMaxTorque));
		
		// set defaults
		
		this.txtX.setValue(a1.x);
		this.txtY.setValue(a1.y);
		
		this.txtMaxForce.setValue(mf);
		this.txtMaxTorque.setValue(mt);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtX.setEnabled(false);
			this.txtY.setEnabled(false);
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
						.addComponent(this.lblAnchor)
						.addComponent(this.lblMaxForce)
						.addComponent(this.lblMaxTorque))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.chkCollision)
						.addComponent(this.cmbBody1)
						.addComponent(this.cmbBody2)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(this.lblX)
								.addComponent(this.txtY)
								.addComponent(this.lblY))
						.addComponent(this.txtMaxForce)
						.addComponent(this.txtMaxTorque)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAnchor)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaxForce)
						.addComponent(this.txtMaxForce, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaxTorque)
						.addComponent(this.txtMaxTorque, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A friction joint is used to drive linear and angular speeds between the bodies to zero.  " +
			   "The joint is intented to be used with other joints.  " +
			   "The maximum force and torque values make sure that the joint doesn't over or under compensate.  ";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof FrictionJoint) {
			FrictionJoint fj = (FrictionJoint)joint;
			// set the super class properties
			fj.setUserData(this.txtName.getText());
			fj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			fj.setMaxForce(this.getDoubleValue(this.txtMaxForce));
			fj.setMaxTorque(this.getDoubleValue(this.txtMaxTorque));
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
				this.getDoubleValue(this.txtX),
				this.getDoubleValue(this.txtY));
		
		FrictionJoint fj = new FrictionJoint(body1, body2, a);
		// set the super class properties
		fj.setUserData(this.txtName.getText());
		fj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		fj.setMaxForce(this.getDoubleValue(this.txtMaxForce));
		fj.setMaxTorque(this.getDoubleValue(this.txtMaxTorque));
		
		return fj;
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
		// check the maximia
		if (this.getDoubleValue(this.txtMaxForce) < 0.0) {
			return false;
		}
		if (this.getDoubleValue(this.txtMaxTorque) < 0.0) {
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
		// check the maximia
		if (this.getDoubleValue(this.txtMaxForce) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum force must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		if (this.getDoubleValue(this.txtMaxTorque) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum torque must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
