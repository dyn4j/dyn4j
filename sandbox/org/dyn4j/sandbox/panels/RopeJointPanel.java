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
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an rope joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RopeJointPanel extends JointPanel implements InputPanel, ActionListener {
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
	
	/** The anchor point 1 label */
	private JLabel lblAnchor1;
	
	/** The anchor point 2 label */
	private JLabel lblAnchor2;
	
	/** The x label for the first anchor point */
	private JLabel lblX1;
	
	/** The x label for the second anchor point */
	private JLabel lblX2;
	
	/** The y label for the first anchor point */
	private JLabel lblY1;
	
	/** The y label for the second anchor point */
	private JLabel lblY2;
	
	/** The first anchor's x text field */
	private JFormattedTextField txtX1;
	
	/** The second anchor's x text field */
	private JFormattedTextField txtX2;
	
	/** The first anchor's y text field */
	private JFormattedTextField txtY1;
	
	/** The second anchor's y text field */
	private JFormattedTextField txtY2;
	
	/** The button to set anchor1 to body1's center of mass */
	private JButton btnUseCenter1;
	
	/** The button to set anchor2 to body2's center of mass */
	private JButton btnUseCenter2;
	
	// limits

	/** The limit enabled label */
	private JLabel lblUpperLimitEnabled;
	
	/** The limit enable check box */
	private JCheckBox chkUpperLimitEnabled;
	
	/** The limit enabled label */
	private JLabel lblLowerLimitEnabled;
	
	/** The limit enable check box */
	private JCheckBox chkLowerLimitEnabled;
	
	/** The upper limit label */
	private JLabel lblUpperLimit;
	
	/** The lower limit label */
	private JLabel lblLowerLimit;
	
	/** The upper limit text field */
	private JFormattedTextField txtUpperLimit;
	
	/** The lower limit text field */
	private JFormattedTextField txtLowerLimit;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public RopeJointPanel(RopeJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 a1 = joint.getAnchor1();
		Vector2 a2 = joint.getAnchor2();
		boolean ule = joint.isMaximumEnabled();
		boolean lle = joint.isMinimumEnabled();
		double ul = joint.getMaximumDistance();
		double ll = joint.getMinimumDistance();
		
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
		
		this.lblAnchor1 = new JLabel("Anchor 1", Icons.INFO, JLabel.LEFT);
		this.lblAnchor1.setToolTipText("The anchor point on the first body.");
		
		this.lblAnchor2 = new JLabel("Anchor 2", Icons.INFO, JLabel.LEFT);
		this.lblAnchor2.setToolTipText("The anchor point on the second body.");
		
		this.lblX1 = new JLabel("x");
		this.lblX2 = new JLabel("x");
		this.lblY1 = new JLabel("y");
		this.lblY2 = new JLabel("y");
		
		this.txtX1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX1.addFocusListener(new SelectTextFocusListener(this.txtX1));
		this.txtX1.setColumns(7);
		
		this.txtX2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX2.addFocusListener(new SelectTextFocusListener(this.txtX2));
		this.txtX2.setColumns(7);
		
		this.txtY1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY1.addFocusListener(new SelectTextFocusListener(this.txtY1));
		this.txtY1.setColumns(7);
		
		this.txtY2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY2.addFocusListener(new SelectTextFocusListener(this.txtY2));
		this.txtY2.setColumns(7);

		this.btnUseCenter1 = new JButton("Use Center");
		this.btnUseCenter1.setToolTipText("Set anchor 1 to the center of mass of body 1.");
		this.btnUseCenter1.setActionCommand("use-com1");
		this.btnUseCenter1.addActionListener(this);
		
		this.btnUseCenter2 = new JButton("Use Center");
		this.btnUseCenter2.setToolTipText("Set anchor 2 to the center of mass of body 2.");
		this.btnUseCenter2.setActionCommand("use-com2");
		this.btnUseCenter2.addActionListener(this);
		
		this.lblLowerLimitEnabled = new JLabel("Lower Limit Enabled", Icons.INFO, JLabel.LEFT);
		this.lblLowerLimitEnabled.setToolTipText("Check to enable the lower limit.");
		this.chkLowerLimitEnabled = new JCheckBox();
		
		this.lblLowerLimit = new JLabel("Lower Limit", Icons.INFO, JLabel.LEFT);
		this.lblLowerLimit.setToolTipText("The lower limit in Meters.");
		this.txtLowerLimit = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtLowerLimit.addFocusListener(new SelectTextFocusListener(this.txtLowerLimit));
		this.txtLowerLimit.setColumns(7);
		
		this.lblUpperLimitEnabled = new JLabel("Upper Limit Enabled", Icons.INFO, JLabel.LEFT);
		this.lblUpperLimitEnabled.setToolTipText("Check to enable the upper limit.");
		this.chkUpperLimitEnabled = new JCheckBox();
		
		this.lblUpperLimit = new JLabel("Upper Limit", Icons.INFO, JLabel.LEFT);
		this.lblUpperLimit.setToolTipText("The upper limit in Meters.");
		this.txtUpperLimit = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtUpperLimit.addFocusListener(new SelectTextFocusListener(this.txtUpperLimit));
		this.txtUpperLimit.setColumns(7);
		
		// set defaults
		
		this.txtX1.setValue(a1.x);
		this.txtX2.setValue(a2.x);
		this.txtY1.setValue(a1.y);
		this.txtY2.setValue(a2.y);
		
		this.chkLowerLimitEnabled.setSelected(lle);
		this.chkUpperLimitEnabled.setSelected(ule);
		
		this.txtLowerLimit.setValue(ll);
		this.txtUpperLimit.setValue(ul);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtX1.setEnabled(false);
			this.txtX2.setEnabled(false);
			this.txtY1.setEnabled(false);
			this.txtY2.setEnabled(false);
			this.btnUseCenter1.setEnabled(false);
			this.btnUseCenter2.setEnabled(false);
		}
		
		// setup the sections
		
		GroupLayout layout;
		
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
						.addComponent(this.lblAnchor1)
						.addComponent(this.lblAnchor2))
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
								.addComponent(this.lblY1))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX2)
								.addComponent(this.lblX2)
								.addComponent(this.txtY2)
								.addComponent(this.lblY2))));
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
						.addComponent(this.lblAnchor1)
						.addComponent(this.txtX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX1)
						.addComponent(this.txtY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY1))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAnchor2)
						.addComponent(this.txtX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX2)
						.addComponent(this.txtY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY2)));
		
		JPanel pnlLimits = new JPanel();
		pnlLimits.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Limits "));
		
		layout = new GroupLayout(pnlLimits);
		pnlLimits.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLowerLimitEnabled)
						.addComponent(this.lblLowerLimit)
						.addComponent(this.lblUpperLimitEnabled)
						.addComponent(this.lblUpperLimit))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLowerLimitEnabled)
						.addComponent(this.txtLowerLimit)
						.addComponent(this.chkUpperLimitEnabled)
						.addComponent(this.txtUpperLimit)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLowerLimitEnabled)
						.addComponent(this.chkLowerLimitEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblLowerLimit)
						.addComponent(this.txtLowerLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblUpperLimitEnabled)
						.addComponent(this.chkUpperLimitEnabled))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblUpperLimit)
						.addComponent(this.txtUpperLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// setup the layout of the sections
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(pnlLimits));
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
			this.txtX2.setValue(c.x);
			this.txtY2.setValue(c.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A rope joint is a distance joint that does not have the spring/damper capabilities.  However, " +
				"it does have minimum and maximum limits.  The limits are on the distance between the given anchor " +
				"points.  If the lower and upper limits are equal, this joint acts identical to the distance joint.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof RopeJoint) {
			RopeJoint rj = (RopeJoint)joint;
			// set the super class properties
			rj.setUserData(this.txtName.getText());
			rj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			rj.setMinimumEnabled(this.chkLowerLimitEnabled.isSelected());
			rj.setMaximumEnabled(this.chkUpperLimitEnabled.isSelected());
			rj.setMinimumMaximum(
					this.getDoubleValue(this.txtLowerLimit),
					this.getDoubleValue(this.txtUpperLimit));
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
		Vector2 a1 = new Vector2(
				this.getDoubleValue(this.txtX1),
				this.getDoubleValue(this.txtY1));
		Vector2 a2 = new Vector2(
				this.getDoubleValue(this.txtX2),
				this.getDoubleValue(this.txtY2));
		
		RopeJoint rj = new RopeJoint(body1, body2, a1, a2);
		// set the super class properties
		rj.setUserData(this.txtName.getText());
		rj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		rj.setMinimumEnabled(this.chkLowerLimitEnabled.isSelected());
		rj.setMaximumEnabled(this.chkUpperLimitEnabled.isSelected());
		rj.setMinimumMaximum(
				this.getDoubleValue(this.txtLowerLimit),
				this.getDoubleValue(this.txtUpperLimit));
		
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
		// the limits must be correct
		if (this.getDoubleValue(this.txtLowerLimit) > this.getDoubleValue(this.txtUpperLimit)) {
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
		// the limits must be correct
		if (this.getDoubleValue(this.txtLowerLimit) > this.getDoubleValue(this.txtUpperLimit)) {
			JOptionPane.showMessageDialog(owner, "The upper limit must be greater than or equal to the lower limit.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
