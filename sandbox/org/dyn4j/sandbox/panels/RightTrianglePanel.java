package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a right triangle shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RightTrianglePanel extends ShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -885888985126355874L;

	/** The default width */
	private static final double DEFAULT_WIDTH = 0.5;
	
	/** The default height */
	private static final double DEFAULT_HEIGHT = 0.5;
	
	/** The default shape */
	private static final Triangle DEFAULT_TRIANGLE = Geometry.createRightTriangle(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	
	/** The width of the rectangle */
	private double width = DEFAULT_WIDTH;
	
	/** The height of the rectangle */
	private double height = DEFAULT_HEIGHT;
	
	/**
	 * Default constructor.
	 */
	public RightTrianglePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblWidth = new JLabel("Width", Icons.INFO, JLabel.LEFT);
		lblWidth.setToolTipText("The width of the base of the right triangle.");
		JLabel lblHeight = new JLabel("Height", Icons.INFO, JLabel.LEFT);
		lblHeight.setToolTipText("The height of the vertical side of the right triangle.");
		JFormattedTextField txtWidth = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtHeight = new JFormattedTextField(new DecimalFormat("0.000"));
		txtWidth.setValue(DEFAULT_WIDTH);
		txtHeight.setValue(DEFAULT_HEIGHT);
		
		txtWidth.addFocusListener(new SelectTextFocusListener(txtWidth));
		txtWidth.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				width = number.doubleValue();
			}
		});
		txtHeight.addFocusListener(new SelectTextFocusListener(txtHeight));
		txtHeight.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				height = number.doubleValue();
			}
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(lblWidth)
						.addComponent(lblHeight))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(txtWidth)
						.addComponent(txtHeight)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(lblWidth)
						.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(lblHeight)
						.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_TRIANGLE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return Geometry.createRightTriangle(this.width, this.height);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (width <= 0.0 || height <= 0.0) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (this.isValidInput()) {
			JOptionPane.showMessageDialog(owner, "A right triangle must have a width and height greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
