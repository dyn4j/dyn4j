package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;

/**
 * Panel used to capture translation and rotation.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransformPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -5389987241883097903L;

	/** The translation */
	private Vector2 translation;
	
	/** The rotation (in degrees) */
	private double rotation;
	
	/** The translation label */
	private JLabel lblT;
	
	/** The label for the translation along the x-axis */
	private JLabel lblX;
	
	/** The label for the translation along the y-axis */
	private JLabel lblY;
	
	/** The label for the rotation */
	private JLabel lblR;
	
	/** The text box for the translation along the x-axis */
	private JFormattedTextField txtX;
	
	/** The text box for the translation along the y-axis */
	private JFormattedTextField txtY;
	
	/** The text box for the rotation */
	private JFormattedTextField txtR;
	
	/**
	 * Default contructor.
	 */
	public TransformPanel() {
		this(new Vector2(), 0.0, null);
	}
	
	/**
	 * Optional contructor.
	 * @param header the header component of the panel
	 */
	public TransformPanel(JComponent header) {
		this(new Vector2(), 0.0, header);
	}
	
	/**
	 * Optional constructor.
	 * @param transform initial transform values
	 */
	public TransformPanel(Transform transform) {
		this(transform.getTranslation(), transform.getRotation(), null);
	}
	
	/**
	 * Full constructor.
	 * @param transform initial transform values
	 * @param header the header component of the panel
	 */
	public TransformPanel(Transform transform, JComponent header) {
		this(transform.getTranslation(), transform.getRotation(), header);
	}
	
	/**
	 * Optional constructor.
	 * @param tx the initial translation
	 * @param rot the initial rotation
	 */
	public TransformPanel(Vector2 tx, double rot) {
		this(tx, rot, null);
	}
	
	/**
	 * Full constructor.
	 * @param tx the initial translation
	 * @param rot the initial rotation
	 * @param header the header component of the panel
	 */
	public TransformPanel(Vector2 tx, double rot, JComponent header) {
		this.translation = tx.copy();
		this.rotation = rot;
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		if (header == null) header = new JLabel();
		
		this.lblT = new JLabel("Translation");
		this.lblX = new JLabel("x");
		this.lblY = new JLabel("y");
		this.lblR = new JLabel("Rotation");
		
		this.lblX.setToolTipText("The translation along the x axis in Meters.");
		this.lblY.setToolTipText("The translation along the y axis in Meters.");
		this.lblR.setToolTipText("The rotation in Degrees.");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtR = new JFormattedTextField(new DecimalFormat("0.000"));
		
		this.txtX.setToolTipText("The translation along the x axis in Meters.");
		this.txtY.setToolTipText("The translation along the y axis in Meters.");
		this.txtR.setToolTipText("The rotation in Degrees.");
		
		this.txtX.setColumns(7);
		this.txtY.setColumns(7);
		this.txtR.setColumns(7);
		
		this.txtX.setValue(this.translation.x);
		this.txtY.setValue(this.translation.y);
		this.txtR.setValue(Math.toDegrees(this.rotation));
		
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				translation.x = number.doubleValue();
			}
		});
		
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				translation.y = number.doubleValue();
			}
		});
		
		this.txtR.addFocusListener(new SelectTextFocusListener(this.txtR));
		this.txtR.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				rotation = number.doubleValue();
			}
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(header)
				.addGroup(
						layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblT)
								.addComponent(this.lblR))
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.txtX)
										.addComponent(this.lblX))
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.txtY)
										.addComponent(this.lblY))
								.addComponent(this.txtR))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(header)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblT)
						.addGroup(layout.createSequentialGroup()
								// dont allow vertical resizing
								.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.lblX)
								.addComponent(this.lblY)))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblR)
						// dont allow vertical resizing
						.addComponent(this.txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Returns the selected translation.
	 * @return Vector2
	 */
	public Vector2 getTranslation() {
		return this.translation.copy();
	}
	
	/**
	 * Returns the selected rotation (in degrees).
	 * @return double
	 */
	public double getRotation() {
		return Math.toRadians(this.rotation);
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
