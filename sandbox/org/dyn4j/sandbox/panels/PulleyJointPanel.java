package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;

/**
 * Panel used to create or edit an pulley joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PulleyJointPanel extends JointPanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body 1 drop down */
	private JComboBox cmbBody1;
	
	/** The body 2 drop down */
	private JComboBox cmbBody2;
	
	// anchor points
	
	/** The first body anchor's x text field */
	private JFormattedTextField txtBX1;
	
	/** The second body anchor's x text field */
	private JFormattedTextField txtBX2;
	
	/** The first body anchor's y text field */
	private JFormattedTextField txtBY1;
	
	/** The second body anchor's y text field */
	private JFormattedTextField txtBY2;

	/** The first pulley anchor's x text field */
	private JFormattedTextField txtPX1;
	
	/** The second pulley anchor's x text field */
	private JFormattedTextField txtPX2;
	
	/** The first pulley anchor's y text field */
	private JFormattedTextField txtPY1;
	
	/** The second pulley anchor's y text field */
	private JFormattedTextField txtPY2;
	
	/** The button to set body anchor 1 to body1's center of mass */
	private JButton btnUseCenter1;
	
	/** The button to set body anchor 2 to body2's center of mass */
	private JButton btnUseCenter2;
	
	// ratio
	
	/** The frequency text field */
	private JFormattedTextField txtRatio;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public PulleyJointPanel(PulleyJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		boolean collision = joint.isCollisionAllowed();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		Vector2 a1 = joint.getAnchor1();
		Vector2 a2 = joint.getAnchor2();
		Vector2 p1 = joint.getPulleyAnchor1();
		Vector2 p2 = joint.getPulleyAnchor2();
		double r = joint.getRatio();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		this.chkCollision.setSelected(collision);
		
		JLabel lblBody1 = new JLabel("Body 1");
		JLabel lblBody2 = new JLabel("Body 2");
		this.cmbBody1 = new JComboBox(bodies);
		this.cmbBody2 = new JComboBox(bodies);
		
		JLabel lblPulleyAnchor1 = new JLabel("Pulley Anchor 1");
		lblPulleyAnchor1.setToolTipText("The anchor point for the first pulley.");
		JLabel lblPulleyAnchor2 = new JLabel("Pulley Anchor 2");
		lblPulleyAnchor2.setToolTipText("The anchor point on the second pulley.");
		
		JLabel lblBodyAnchor1 = new JLabel("Body Anchor 1");
		lblBodyAnchor1.setToolTipText("The anchor point on the first body.");
		JLabel lblBodyAnchor2 = new JLabel("Body Anchor 2");
		lblBodyAnchor2.setToolTipText("The anchor point on the second body.");
		
		JLabel lblPX1 = new JLabel("x");
		JLabel lblPX2 = new JLabel("x");
		JLabel lblPY1 = new JLabel("y");
		JLabel lblPY2 = new JLabel("y");
		JLabel lblBX1 = new JLabel("x");
		JLabel lblBX2 = new JLabel("x");
		JLabel lblBY1 = new JLabel("y");
		JLabel lblBY2 = new JLabel("y");
		
		this.txtPX1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPX1.addFocusListener(new SelectTextFocusListener(this.txtPX1));
		this.txtPX1.setColumns(7);
		
		this.txtPX2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPX2.addFocusListener(new SelectTextFocusListener(this.txtPX2));
		this.txtPX2.setColumns(7);
		
		this.txtPY1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPY1.addFocusListener(new SelectTextFocusListener(this.txtPY1));
		this.txtPY1.setColumns(7);
		
		this.txtPY2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtPY2.addFocusListener(new SelectTextFocusListener(this.txtPY2));
		this.txtPY2.setColumns(7);
		
		this.txtBX1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtBX1.addFocusListener(new SelectTextFocusListener(this.txtBX1));
		this.txtBX1.setColumns(7);
		
		this.txtBX2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtBX2.addFocusListener(new SelectTextFocusListener(this.txtBX2));
		this.txtBX2.setColumns(7);
		
		this.txtBY1 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtBY1.addFocusListener(new SelectTextFocusListener(this.txtBY1));
		this.txtBY1.setColumns(7);
		
		this.txtBY2 = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtBY2.addFocusListener(new SelectTextFocusListener(this.txtBY2));
		this.txtBY2.setColumns(7);

		this.btnUseCenter1 = new JButton("Use Center");
		this.btnUseCenter1.setToolTipText("Set anchor 1 to the center of mass of body 1.");
		this.btnUseCenter1.setActionCommand("use-com1");
		this.btnUseCenter1.addActionListener(this);
		
		this.btnUseCenter2 = new JButton("Use Center");
		this.btnUseCenter2.setToolTipText("Set anchor 2 to the center of mass of body 2.");
		this.btnUseCenter2.setActionCommand("use-com2");
		this.btnUseCenter2.addActionListener(this);
		
		JLabel lblRatio = new JLabel("Ratio");
		lblRatio.setToolTipText("Determines the pulley ratio between the two bodies.");
		this.txtRatio = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtRatio.addFocusListener(new SelectTextFocusListener(this.txtRatio));
		this.txtRatio.setToolTipText("Determines the pulley ratio between the two bodies.");
		
		// set defaults
		
		// disable the auto calculate while we set defaults
		this.txtBX1.setValue(a1.x);
		this.txtBX2.setValue(a2.x);
		this.txtBY1.setValue(a1.y);
		this.txtBY2.setValue(a2.y);
		this.txtPX1.setValue(p1.x);
		this.txtPX2.setValue(p2.x);
		this.txtPY1.setValue(p1.y);
		this.txtPY2.setValue(p2.y);
		
		this.txtRatio.setValue(r);
		
		this.cmbBody1.setSelectedItem(b1);
		this.cmbBody2.setSelectedItem(b2);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody1.setEnabled(false);
			this.cmbBody2.setEnabled(false);
			this.txtBX1.setEnabled(false);
			this.txtBX2.setEnabled(false);
			this.txtBY1.setEnabled(false);
			this.txtBY2.setEnabled(false);
			this.txtPX1.setEnabled(false);
			this.txtPX2.setEnabled(false);
			this.txtPY1.setEnabled(false);
			this.txtPY2.setEnabled(false);
			this.btnUseCenter1.setEnabled(false);
			this.btnUseCenter2.setEnabled(false);
		}
		
		// setup the sections
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblCollision)
						.addComponent(lblBody1)
						.addComponent(lblBody2)
						.addComponent(lblBodyAnchor1)
						.addComponent(lblBodyAnchor2)
						.addComponent(lblPulleyAnchor1)
						.addComponent(lblPulleyAnchor2)
						.addComponent(lblRatio))
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
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtBX1)
										.addComponent(this.txtBX2)
										.addComponent(this.txtPX1)
										.addComponent(this.txtPX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(lblBX1)
										.addComponent(lblBX2)
										.addComponent(lblPX1)
										.addComponent(lblPX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtBY1)
										.addComponent(this.txtBY2)
										.addComponent(this.txtPY1)
										.addComponent(this.txtPY2))
								.addGroup(layout.createParallelGroup()
										.addComponent(lblBY1)
										.addComponent(lblBY2)
										.addComponent(lblPY1)
										.addComponent(lblPY2)))
						.addComponent(this.txtRatio)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblCollision)
						.addComponent(this.chkCollision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBody1)
						.addComponent(this.cmbBody1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBody2)
						.addComponent(this.cmbBody2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBodyAnchor1)
						.addComponent(this.txtBX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBX1)
						.addComponent(this.txtBY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBY1))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblBodyAnchor2)
						.addComponent(this.txtBX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBX2)
						.addComponent(this.txtBY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBY2))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPulleyAnchor1)
						.addComponent(this.txtPX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPX1)
						.addComponent(this.txtPY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPY1))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPulleyAnchor2)
						.addComponent(this.txtPX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPX2)
						.addComponent(this.txtPY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPY2))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRatio)
						.addComponent(this.txtRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("use-com1".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody1.getSelectedItem()).getWorldCenter();
			this.txtBX1.setValue(c.x);
			this.txtBY1.setValue(c.y);
		} else if ("use-com2".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody2.getSelectedItem()).getWorldCenter();
			this.txtBX2.setValue(c.x);
			this.txtBY2.setValue(c.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A pulley joint is used to connect two body with a pulley.  The pulley can represent a block-and-tackle if " +
				"the ratio is set to something other than 1.0.  The pulley joint computes the initial 'rope length' from the " +
				"pulley and body anchor points.  The bodies are allowed to freely rotate about the body anchors and translate " +
				"about the pulley anchors.  The pulley system will maintain the initial length.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof PulleyJoint) {
			PulleyJoint pj = (PulleyJoint)joint;
			// set the super class properties
			pj.setUserData(this.txtName.getText());
			pj.setCollisionAllowed(this.chkCollision.isSelected());
			// set the properties that can change
			pj.setRatio(this.getDoubleValue(this.txtRatio));
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
				this.getDoubleValue(this.txtBX1),
				this.getDoubleValue(this.txtBY1));
		Vector2 a2 = new Vector2(
				this.getDoubleValue(this.txtBX2),
				this.getDoubleValue(this.txtBY2));
		Vector2 p1 = new Vector2(
				this.getDoubleValue(this.txtPX1),
				this.getDoubleValue(this.txtPY1));
		Vector2 p2 = new Vector2(
				this.getDoubleValue(this.txtPX2),
				this.getDoubleValue(this.txtPY2));
		
		PulleyJoint pj = new PulleyJoint(body1, body2, p1, p2, a1, a2);
		// set the super class properties
		pj.setUserData(this.txtName.getText());
		pj.setCollisionAllowed(this.chkCollision.isSelected());
		// set the other properties
		pj.setRatio(this.getDoubleValue(this.txtRatio));
		
		return pj;
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
	}
}
