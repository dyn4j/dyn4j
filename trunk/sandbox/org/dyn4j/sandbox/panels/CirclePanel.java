package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a circle shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CirclePanel extends ShapePanel implements InputPanel {
	/** the version id */
	private static final long serialVersionUID = 764471083027867558L;

	/** The default circle radius */
	private static final double DEFAULT_RADIUS = 0.5;
	
	/** The default circle shape */
	private static final Circle DEFAULT_SHAPE = new Circle(DEFAULT_RADIUS);
	
	/** The circle radius */
	private double radius = DEFAULT_RADIUS;
	
	/**
	 * Default constructor.
	 */
	public CirclePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblRadius = new JLabel("Radius", Icons.INFO, JLabel.LEFT);
		lblRadius.setToolTipText("The radius of the circle in Meters.");
		JFormattedTextField txtRadius = new JFormattedTextField(new DecimalFormat("0.000"));
		txtRadius.setValue(DEFAULT_RADIUS);
		
		txtRadius.addFocusListener(new SelectTextFocusListener(txtRadius));
		txtRadius.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				radius = number.doubleValue();
			}
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(lblRadius)
						.addComponent(txtRadius))
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRadius)
						// dont allow the radius 
						.addComponent(txtRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_SHAPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return new Circle(radius);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		return this.radius > 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (!this.isValidInput()) {
			JOptionPane.showMessageDialog(owner, "A circle requires a radius greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
