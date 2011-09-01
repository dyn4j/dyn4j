package org.dyn4j.sandbox.panels;

import java.awt.Color;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.JSliderWithTextField;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ColorUtilities;

/**
 * Panel for editing a Body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BodyPanel extends WindowSpawningPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -4580826229518550082L;
	
	/** The body being edited */
	private SandboxBody body;
	
	// name
	
	/** The name label */
	private JLabel lblName;
	
	/** The name text field */
	private JTextField txtName;
	
	// color controls
	
	/** The outline color */
	private JLabel lblOutlineColor;
	
	/** The outline color change button */
	private JButton btnOutlineColor;
	
	/** The fill color label */
	private JLabel lblFillColor;
	
	/** The fill color change button */
	private JButton btnFillColor;
	
	// mass type controls
	
	/** The mass type label */
	private JLabel lblMassType;
	
	/** The normal mass type radio button */
	private JRadioButton rdoNormal;
	
	/** The infinite mass type radio button */
	private JRadioButton rdoInfinite;
	
	/** The fixed linear velocity mass type radio button */
	private JRadioButton rdoFixedLinearVelocity;
	
	/** The fixed angular velocity mass type radio button */
	private JRadioButton rdoFixedAngularVelocity;
	
	// damping controls
	
	/** The linear damping label */
	private JLabel lblLinearDamping;
	
	/** The linear damping slider and text field */
	private JSliderWithTextField sldLinearDamping;
	
	/** The angular damping label */
	private JLabel lblAngularDamping;
	
	/** The angular damping slider and text field */
	private JSliderWithTextField sldAngularDamping;
	
	// velocity controls
	
	/** The velocity label */
	private JLabel lblVelocity;
	
	/** The velocity x value label */
	private JLabel lblVelocityX;
	
	/** The velocity y value label */
	private JLabel lblVelocityY;
	
	/** The velocity x value text box */
	private JFormattedTextField txtVelocityX;
	
	/** The velocity y value text box */
	private JFormattedTextField txtVelocityY;
	
	/** The angular velocity label */
	private JLabel lblAngularVelocity;
	
	/** The angular velocity text field */
	private JFormattedTextField txtAngularVelocity;
	
	// gravity scale controls
	
	/** The gravity scale label */
	private JLabel lblGravityScale;
	
	/** The gravity scale slider and text field */
	private JSliderWithTextField sldGravityScale;
	
	// state controls
	
	/** The state label */
	private JLabel lblState;
	
	/** The inactive checkbox */
	private JCheckBox chkInactive;
	
	/** The asleep checkbox */
	private JCheckBox chkAsleep;
	
	// other property controls
	
	/** The allow auto sleep label */
	private JLabel lblAllowAutoSleep;
	
	/** The allow auto sleep checkbox */
	private JCheckBox chkAllowAutoSleep;
	
	/** The is bullet label */
	private JLabel lblBullet;
	
	/** The is bullet checkbox */
	private JCheckBox chkBullet;
	
	/**
	 * Full constructor.
	 * @param parent the parent window, frame or dialog 
	 * @param body the body to edit
	 */
	public BodyPanel(Window parent, SandboxBody body) {
		super(parent);
		this.body = body;
		this.initialize();
	}
	
	/**
	 * Sets up the panel with all the controls.
	 */
	public void initialize() {
		// get the color values
		Color initialOutlineColor = ColorUtilities.convertColor(body.getOutlineColor());
		Color initialFillColor = ColorUtilities.convertColor(body.getFillColor());
		
		// get the other properties
		Mass.Type massType = body.getMass().getType();
		double linearDamping = body.getLinearDamping();
		double angularDamping = body.getAngularDamping();
		Vector2 velocity = body.getVelocity().copy();
		double angularVelocity = body.getAngularVelocity();
		double gravityScale = body.getGravityScale();
		boolean inactive = !body.isActive();
		boolean asleep = body.isAsleep();
		boolean autoSleep = body.isAutoSleepingEnabled();
		boolean bullet = body.isBullet();
		String name = body.getName();
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		// name
		this.lblName = new JLabel("Name");
		this.txtName = new JTextField(name);
		
		this.txtName.addFocusListener(new SelectTextFocusListener(this.txtName));
		this.txtName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent event) {
				body.setName(txtName.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent event) {
				body.setName(txtName.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent event) {}
		});
		
		// outline color
		this.lblOutlineColor = new JLabel("Outline Color");
		this.btnOutlineColor = new JButton("Select");
		this.btnOutlineColor.setBackground(initialOutlineColor);
		this.btnOutlineColor.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(initialOutlineColor));
		this.btnOutlineColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color c = ColorUtilities.convertColor(body.getOutlineColor());
				
				ColorPanel cep = new ColorPanel(c, false);
				JDialog dialog = new JDialog(getParentWindow(), "Choose an outline color", ModalityType.APPLICATION_MODAL);
				dialog.add(cep);
				dialog.pack();
				dialog.setVisible(true);
				
				// get the new color
				Color nc = cep.getColor();
				
				if (nc != null) {
					float[] color = ColorUtilities.convertColor(nc);
					body.setOutlineColor(color);
					ColorUtilities.convertColor(nc, color);
					JButton btn = (JButton)event.getSource();
					Color dc = new Color(nc.getRed(), nc.getGreen(), nc.getBlue());
					btn.setBackground(dc);
					Color tc = ColorUtilities.getForegroundColorFromBackgroundColor(dc);
					btn.setForeground(tc);
				}
			}
		});
		
		// fill color
		this.lblFillColor = new JLabel("Fill Color");
		this.btnFillColor = new JButton("Select");
		this.btnFillColor.setBackground(initialFillColor);
		this.btnFillColor.setForeground(ColorUtilities.getForegroundColorFromBackgroundColor(initialFillColor));
		this.btnFillColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color c = ColorUtilities.convertColor(body.getFillColor());
				
				ColorPanel cep = new ColorPanel(c, false);
				JDialog dialog = new JDialog(getParentWindow(), "Choose a fill color", ModalityType.APPLICATION_MODAL);
				dialog.add(cep);
				dialog.pack();
				dialog.setVisible(true);
				
				// get the new color
				Color nc = cep.getColor();
				
				if (nc != null) {
					float[] color = ColorUtilities.convertColor(nc);
					body.setFillColor(color);
					ColorUtilities.convertColor(nc, color);
					JButton btn = (JButton)event.getSource();
					Color dc = new Color(nc.getRed(), nc.getGreen(), nc.getBlue());
					btn.setBackground(dc);
					Color tc = ColorUtilities.getForegroundColorFromBackgroundColor(dc);
					btn.setForeground(tc);
				}
			}
		});
		
		// mass type
		this.lblMassType = new JLabel("Mass Type");
		this.rdoNormal = new JRadioButton("Normal");
		this.rdoInfinite = new JRadioButton("Infinite");
		this.rdoInfinite.setToolTipText("<html>Neither the linear or angular velocity can change due to<br />due to interaction with other bodies.</html>");
		this.rdoFixedLinearVelocity = new JRadioButton("Fixed Linear Velocity");
		this.rdoFixedLinearVelocity.setToolTipText("<html>The body's linear velocity will not be changed.<br />The body's angular velocity can change.</html>");
		this.rdoFixedAngularVelocity = new JRadioButton("Fixed Angular Velocity");
		this.rdoFixedAngularVelocity.setToolTipText("<html>The body's angular velocity will not be changed.<br />The body's linear velocity can change.</html>");
		
		this.rdoNormal.setActionCommand(Mass.Type.NORMAL.toString());
		this.rdoInfinite.setActionCommand(Mass.Type.INFINITE.toString());
		this.rdoFixedLinearVelocity.setActionCommand(Mass.Type.FIXED_LINEAR_VELOCITY.toString());
		this.rdoFixedAngularVelocity.setActionCommand(Mass.Type.FIXED_ANGULAR_VELOCITY.toString());
		
		if (massType == Mass.Type.NORMAL) {
			this.rdoNormal.setSelected(true);
		} else if (massType == Mass.Type.INFINITE) {
			this.rdoInfinite.setSelected(true);
		} else if (massType == Mass.Type.FIXED_LINEAR_VELOCITY) {
			this.rdoFixedLinearVelocity.setSelected(true);
		} else if (massType == Mass.Type.FIXED_ANGULAR_VELOCITY) {
			this.rdoFixedAngularVelocity.setSelected(true);
		} 
		
		ButtonGroup bgMassType = new ButtonGroup();
		bgMassType.add(this.rdoNormal);
		bgMassType.add(this.rdoInfinite);
		bgMassType.add(this.rdoFixedLinearVelocity);
		bgMassType.add(this.rdoFixedAngularVelocity);
		
		this.rdoNormal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				if (radio.isSelected()) {
					body.setMassType(Mass.Type.NORMAL);
				}
			}
		});
		this.rdoInfinite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				if (radio.isSelected()) {
					body.setMassType(Mass.Type.INFINITE);
				}
			}
		});
		this.rdoFixedLinearVelocity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				if (radio.isSelected()) {
					body.setMassType(Mass.Type.FIXED_LINEAR_VELOCITY);
				}
			}
		});
		this.rdoFixedAngularVelocity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				if (radio.isSelected()) {
					body.setMassType(Mass.Type.FIXED_ANGULAR_VELOCITY);
				}
			}
		});
		
		// linear damping
		this.lblLinearDamping = new JLabel("Linear Damping");
		this.lblLinearDamping.setToolTipText("<html>Specifies a drag like coefficient for linear motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldLinearDamping = new JSliderWithTextField(0, 100, (int)(linearDamping * 100.0), 0.01, new DecimalFormat("0.00"));
		this.sldLinearDamping.setToolTipText("<html>Specifies a drag like coefficient for linear motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldLinearDamping.setColumns(4);
		this.sldLinearDamping.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setLinearDamping(value);
			}
		});
		
		// angular damping
		this.lblAngularDamping = new JLabel("Angular Damping");
		this.lblAngularDamping.setToolTipText("<html>Specifies a drag like coefficient for angular motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldAngularDamping = new JSliderWithTextField(0, 100, (int)(angularDamping * 100.0), 0.01, new DecimalFormat("0.00"));
		this.sldAngularDamping.setToolTipText("<html>Specifies a drag like coefficient for angular motion.<br />Valid values are between 0 and 1 inclusive.</html>");
		this.sldAngularDamping.setColumns(4);
		this.sldAngularDamping.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setAngularDamping(value);
			}
		});
		
		// initial velocity
		this.lblVelocity = new JLabel("Velocity");
		this.lblVelocity.setToolTipText("The linear velocity in Meters/Second");
		this.lblVelocityX = new JLabel("x");
		this.lblVelocityX.setToolTipText("The x component of the linear velocity in Meters/Second");
		this.lblVelocityY = new JLabel("y");
		this.lblVelocityY.setToolTipText("The y component of the linear velocity in Meters/Second");
		this.txtVelocityX = new JFormattedTextField(new DecimalFormat("##0.000"));
		this.txtVelocityY = new JFormattedTextField(new DecimalFormat("##0.000"));
		this.txtVelocityX.setValue(velocity.x);
		this.txtVelocityY.setValue(velocity.y);
		this.txtVelocityX.setColumns(7);
		this.txtVelocityY.setColumns(7);
		this.txtVelocityX.setMaximumSize(this.txtVelocityX.getPreferredSize());
		this.txtVelocityY.setMaximumSize(this.txtVelocityY.getPreferredSize());
		this.txtVelocityX.addFocusListener(new SelectTextFocusListener(this.txtVelocityX));
		this.txtVelocityY.addFocusListener(new SelectTextFocusListener(this.txtVelocityY));
		this.txtVelocityX.setToolTipText("The x component of the linear velocity in Meters/Second");
		this.txtVelocityY.setToolTipText("The y component of the linear velocity in Meters/Second");
		
		this.txtVelocityX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.getVelocity().x = number.doubleValue();
			}
		});
		this.txtVelocityY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.getVelocity().y = number.doubleValue();
			}
		});
		
		// initial anuglar velocity
		this.lblAngularVelocity = new JLabel("Angular Velocity");
		this.lblAngularVelocity.setToolTipText("The angular velocity in Degrees/Second");
		this.txtAngularVelocity = new JFormattedTextField(new DecimalFormat("##0.000"));
		this.txtAngularVelocity.setToolTipText("The angular velocity in Degrees/Second");
		this.txtAngularVelocity.setValue(Math.toDegrees(angularVelocity));
		this.txtAngularVelocity.setColumns(7);
		this.txtAngularVelocity.setMaximumSize(this.txtAngularVelocity.getPreferredSize());
		this.txtAngularVelocity.addFocusListener(new SelectTextFocusListener(this.txtAngularVelocity));
		this.txtAngularVelocity.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				body.setAngularVelocity(Math.toRadians(number.doubleValue()));
			}
		});
		
		// gravity scale
		this.lblGravityScale = new JLabel("Gravity Scale");
		this.lblGravityScale.setToolTipText("<html>A scalar to apply less or more gravity to a specific body.<br />Valid values are zero or greater.</html>");
		this.sldGravityScale = new JSliderWithTextField(0, 1000, (int)(gravityScale * 100.0), 0.01, new DecimalFormat("#0.00"));
		this.sldGravityScale.setToolTipText("<html>A scalar to apply less or more gravity to a specific body.<br />Valid values are zero or greater.</html>");
		this.sldGravityScale.setColumns(4);
		this.sldGravityScale.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JSliderWithTextField slider = (JSliderWithTextField)event.getSource();
				double value = slider.getScaledValue();
				body.setGravityScale(value);
			}
		});
		
		// initial state (Active/asleep)
		this.lblState = new JLabel("State");
		this.chkInactive = new JCheckBox("Inactive");
		this.chkAsleep = new JCheckBox("Asleep");
		this.chkInactive.setSelected(inactive);
		this.chkAsleep.setSelected(asleep);
		this.chkInactive.setToolTipText("An inactive body doesn't participate in world.");
		this.chkAsleep.setToolTipText("<html>An asleep body has come to rest and only participates in<br />the world when awoken by another body or joint.</html>");
		
		this.chkInactive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setActive(false);
				} else {
					body.setActive(true);
				}
			}
		});
		
		this.chkAsleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setAsleep(true);
				} else {
					body.setAsleep(false);
				}
			}
		});
		
		// allow auto sleep
		this.lblAllowAutoSleep = new JLabel("Allow Auto-Sleeping");
		this.lblAllowAutoSleep.setToolTipText("<html>Auto-sleeping allows the World to identify bodies who have come to rest and<br />skip steps with those bodies to provide better performance.</html>");
		this.chkAllowAutoSleep = new JCheckBox();
		this.chkAllowAutoSleep.setToolTipText("<html>Auto-sleeping allows the World to identify bodies who have come to rest and<br />skip steps with those bodies to provide better performance.</html>");
		this.chkAllowAutoSleep.setSelected(autoSleep);
		
		this.chkAllowAutoSleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setAutoSleepingEnabled(true);
				} else {
					body.setAutoSleepingEnabled(false);
				}
			}
		});
		
		// is bullet
		this.lblBullet = new JLabel("Bullet");
		this.lblBullet.setToolTipText("A body flagged as a bullet require more processing, but can avoid tunneling.");
		this.chkBullet = new JCheckBox();
		this.chkBullet.setToolTipText("A body flagged as a bullet require more processing, but can avoid tunneling.");
		this.chkBullet.setSelected(bullet);
		
		this.chkBullet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JCheckBox check = (JCheckBox)event.getSource();
				if (check.isSelected()) {
					body.setBullet(true);
				} else {
					body.setBullet(false);
				}
			}
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblOutlineColor)
						.addComponent(this.lblFillColor)
						.addComponent(this.lblMassType)
						.addComponent(this.lblVelocity)
						.addComponent(this.lblAngularVelocity)
						.addComponent(this.lblLinearDamping)
						.addComponent(this.lblAngularDamping)
						.addComponent(this.lblGravityScale)
						.addComponent(this.lblState)
						.addComponent(this.lblAllowAutoSleep)
						.addComponent(this.lblBullet))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.txtName)
						.addComponent(this.btnOutlineColor)
						.addComponent(this.btnFillColor)
						.addComponent(this.rdoNormal)
						.addComponent(this.rdoInfinite)
						.addComponent(this.rdoFixedLinearVelocity)
						.addComponent(this.rdoFixedAngularVelocity)
						.addGroup(
								layout.createSequentialGroup()
								.addComponent(this.txtVelocityX)
								.addComponent(this.lblVelocityX))
						.addGroup(
								layout.createSequentialGroup()
								.addComponent(this.txtVelocityY)
								.addComponent(this.lblVelocityY))
						.addComponent(this.txtAngularVelocity)
						.addComponent(this.sldLinearDamping)
						.addComponent(this.sldAngularDamping)
						.addComponent(this.sldGravityScale)
						.addComponent(this.chkInactive)
						.addComponent(this.chkAsleep)
						.addComponent(this.chkAllowAutoSleep)
						.addComponent(this.chkBullet)));
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblOutlineColor)
						.addComponent(this.btnOutlineColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblFillColor)
						.addComponent(this.btnFillColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblMassType)
						.addGroup(
								layout.createSequentialGroup()
								.addComponent(this.rdoNormal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.rdoInfinite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.rdoFixedLinearVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.rdoFixedAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblVelocity)
						.addGroup(
								layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup()
										.addComponent(this.txtVelocityX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.lblVelocityX))
								.addGroup(
										layout.createParallelGroup()
										.addComponent(this.txtVelocityY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.lblVelocityY))))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblAngularVelocity)
						.addComponent(this.txtAngularVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblLinearDamping)
						.addComponent(this.sldLinearDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblAngularDamping)
						.addComponent(this.sldAngularDamping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblGravityScale)
						.addComponent(this.sldGravityScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblState)
						.addGroup(
								layout.createSequentialGroup()
								.addComponent(this.chkInactive, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.chkAsleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblAllowAutoSleep)
						.addComponent(this.chkAllowAutoSleep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(this.lblBullet)
						.addComponent(this.chkBullet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
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
