package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create or edit an mouse joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MouseJointPanel extends JointPanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 8812128051146951491L;

	/** The body drop down label */
	private JLabel lblBody;
	
	/** The body drop down */
	private JComboBox cmbBody;
	
	// anchor points
	
	/** The anchor label */
	private JLabel lblAnchor;
	
	/** The target label */
	private JLabel lblTarget;
	
	/** The x label for the anchor point */
	private JLabel lblX1;
	
	/** The x label for the target point */
	private JLabel lblX2;
	
	/** The y label for the anchor point */
	private JLabel lblY1;
	
	/** The y label for the target point */
	private JLabel lblY2;
	
	/** The anchor's x text field */
	private JFormattedTextField txtX1;
	
	/** The target's x text field */
	private JFormattedTextField txtX2;
	
	/** The anchor's y text field */
	private JFormattedTextField txtY1;
	
	/** The target's y text field */
	private JFormattedTextField txtY2;
	
	/** The button to set anchor to the body's center of mass */
	private JButton btnUseCenter;
	
	// frequency, ratio, and max force
	
	/** The frequency label */
	private JLabel lblFrequency;
	
	/** The ratio label */
	private JLabel lblRatio;
	
	/** The frequency text field */
	private JFormattedTextField txtFrequency;
	
	/** The ratio text field */
	private JFormattedTextField txtRatio;
	
	/** The max force label */
	private JLabel lblMaxForce;
	
	/** The max force text field */
	private JFormattedTextField txtMaxForce;
	
	/**
	 * Full constructor.
	 * @param joint the original joint; null if creating
	 * @param bodies the list of bodies to choose from
	 * @param edit true if the joint is being edited
	 */
	public MouseJointPanel(MouseJoint joint, SandboxBody[] bodies, boolean edit) {
		super();
		
		// get initial values
		String name = (String)joint.getUserData();
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		// anchor2 stores the anchor point
		Vector2 a = joint.getAnchor2();
		Vector2 t = joint.getTarget();
		double mf = joint.getMaxForce();
		double f = joint.getFrequency();
		double r = joint.getDampingRatio();
		
		// set the super classes defaults
		this.txtName.setText(name);
		this.txtName.setColumns(15);
		
		this.lblBody = new JLabel("Body", Icons.INFO, JLabel.LEFT);
		this.lblBody.setToolTipText("The body participating in the joint.");
		this.cmbBody = new JComboBox(bodies);
		
		this.lblAnchor = new JLabel("Anchor", Icons.INFO, JLabel.LEFT);
		this.lblAnchor.setToolTipText("The anchor point on the body.");
		
		this.lblTarget = new JLabel("Target", Icons.INFO, JLabel.LEFT);
		this.lblTarget.setToolTipText(
				"<html>The target point." +
				"<br />The point that the anchor point will attempt to approach.</html>");
		
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

		this.btnUseCenter = new JButton("Use Center");
		this.btnUseCenter.setToolTipText("Set the anchor to the center of mass of the body.");
		this.btnUseCenter.setActionCommand("use-com");
		this.btnUseCenter.addActionListener(this);
		
		this.lblMaxForce = new JLabel("Maximum Force", Icons.INFO, JLabel.LEFT);
		this.lblMaxForce.setToolTipText("The maximum force the joint can apply in Newtons.");
		this.txtMaxForce = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtMaxForce.addFocusListener(new SelectTextFocusListener(this.txtMaxForce));
		this.txtMaxForce.setColumns(8);
		
		this.lblFrequency = new JLabel("Frequency", Icons.INFO, JLabel.LEFT);
		this.lblFrequency.setToolTipText("<html>Determines how fast the spring should oscillate in hertz (Seconds<sup>-1</sup>).</html>");
		this.txtFrequency = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtFrequency.addFocusListener(new SelectTextFocusListener(this.txtFrequency));
		
		this.lblRatio = new JLabel("Ratio", Icons.INFO, JLabel.LEFT);
		this.lblRatio.setToolTipText("Determines how fast the spring is dampened from 0.0 to 1.0.");
		this.txtRatio = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtRatio.addFocusListener(new SelectTextFocusListener(this.txtRatio));
		
		// set defaults
		
		// disable the auto calculate while we set defaults
		
		this.txtX1.setValue(a.x);
		this.txtX2.setValue(t.x);
		this.txtY1.setValue(a.y);
		this.txtY2.setValue(t.y);
		
		this.txtMaxForce.setValue(mf);
		this.txtFrequency.setValue(f);
		this.txtRatio.setValue(r);
		
		this.cmbBody.setSelectedItem(b1);
		
		// setup edit mode if necessary
		
		if (edit) {
			// disable/hide certain controls
			this.cmbBody.setEnabled(false);
			this.txtX1.setEnabled(false);
			this.txtY1.setEnabled(false);
			this.btnUseCenter.setEnabled(false);
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
						.addComponent(this.lblBody)
						.addComponent(this.lblAnchor)
						.addComponent(this.lblTarget)
						.addComponent(this.lblFrequency)
						.addComponent(this.lblRatio)
						.addComponent(this.lblMaxForce))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbBody)
								.addComponent(this.btnUseCenter))
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtX1)
										.addComponent(this.txtX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.lblX1)
										.addComponent(this.lblX2))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.txtY1)
										.addComponent(this.txtY2))
								.addGroup(layout.createParallelGroup()
										.addComponent(this.lblY1)
										.addComponent(this.lblY2)))
						.addComponent(this.txtFrequency)
						.addComponent(this.txtRatio)
						.addComponent(this.txtMaxForce)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblBody)
						.addComponent(this.cmbBody, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnUseCenter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAnchor)
						.addComponent(this.txtX1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX1)
						.addComponent(this.txtY1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY1))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblTarget)
						.addComponent(this.txtX2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX2)
						.addComponent(this.txtY2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY2))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblFrequency)
						.addComponent(this.txtFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblRatio)
						.addComponent(this.txtRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblMaxForce)
						.addComponent(this.txtMaxForce, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
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
		return "A mouse joint can be used to fix a body to a specific point (which can be changed) using a spring/damper.  " +
			   "This joint was originally used to connect a body and a user's mouse for moving bodies but has been " +
			   "adapted for other purposes.  Unlike the other joints, this joint only requires one body.";
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("use-com".equals(e.getActionCommand())) {
			Vector2 c = ((SandboxBody)this.cmbBody.getSelectedItem()).getWorldCenter();
			this.txtX1.setValue(c.x);
			this.txtY1.setValue(c.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#setJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void setJoint(Joint joint) {
		if (joint instanceof MouseJoint) {
			MouseJoint mj = (MouseJoint)joint;
			// set the super class properties
			mj.setUserData(this.txtName.getText());
			// set the properties that can change
			Vector2 target = new Vector2(
					this.getDoubleValue(this.txtX2),
					this.getDoubleValue(this.txtY2));
			mj.setTarget(target);
			mj.setDampingRatio(this.getDoubleValue(this.txtRatio));
			mj.setFrequency(this.getDoubleValue(this.txtFrequency));
			mj.setMaxForce(this.getDoubleValue(this.txtMaxForce));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.JointPanel#getJoint()
	 */
	@Override
	public Joint getJoint() {
		// get the selected bodies
		SandboxBody body = (SandboxBody)this.cmbBody.getSelectedItem();
		
		// get the anchor points
		Vector2 a = new Vector2(
				this.getDoubleValue(this.txtX1),
				this.getDoubleValue(this.txtY1));
		Vector2 t = new Vector2(
				this.getDoubleValue(this.txtX2),
				this.getDoubleValue(this.txtY2));
		
		double f = this.getDoubleValue(this.txtFrequency);
		double d = this.getDoubleValue(this.txtRatio);
		double mf = this.getDoubleValue(this.txtMaxForce);
		
		MouseJoint mj = new MouseJoint(body, a, f, d, mf);
		// set the super class properties
		mj.setUserData(this.txtName.getText());
		// set the target
		mj.setTarget(t);
		return mj;
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
		// the max force must be greater than zero
		if (this.getDoubleValue(this.txtMaxForce) < 0.0) {
			return false;
		}
		// check the damping ratio
		double dr = this.getDoubleValue(this.txtRatio);
		if (dr < 0.0 || dr > 1.0) {
			return false;
		}
		// check the frequency
		double f = this.getDoubleValue(this.txtFrequency);
		if (f < 0.0) {
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
		// the max force must be greater than zero
		if (this.getDoubleValue(this.txtMaxForce) < 0.0) {
			JOptionPane.showMessageDialog(owner, "The maximum force must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// check the damping ratio
		double dr = this.getDoubleValue(this.txtRatio);
		if (dr < 0.0 || dr > 1.0) {
			JOptionPane.showMessageDialog(owner, "The damping ratio must be between 0 and 1 inclusive.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
		// check the frequency
		double f = this.getDoubleValue(this.txtFrequency);
		if (f < 0.0) {
			JOptionPane.showMessageDialog(owner, "The frequency must be greater than or equal to zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
