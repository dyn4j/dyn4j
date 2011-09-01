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
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;

/**
 * Panel used to create a rectangle shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RectanglePanel extends ShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 3354693723172871704L;

	/** The default width */
	private static final double DEFAULT_WIDTH = 1.0;
	
	/** The default height */
	private static final double DEFAULT_HEIGHT = 1.0;
	
	/** The default shape */
	private static final Rectangle DEFAULT_SHAPE = new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	
	/** The width of the rectangle */
	private double width;
	
	/** The height of the rectangle */
	private double height;
	
	/**
	 * Default constructor.
	 */
	public RectanglePanel() {
		this(null);
	}
	
	/**
	 * Full constructor.
	 * @param rectangle the initial rectangle
	 */
	public RectanglePanel(Rectangle rectangle) {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		
		if (rectangle != null) {
			this.width = rectangle.getWidth();
			this.height = rectangle.getHeight();
		}
		
		JLabel lblWidth = new JLabel("Width");
		JLabel lblHeight = new JLabel("Height");
		JFormattedTextField txtWidth = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtHeight = new JFormattedTextField(new DecimalFormat("0.000"));
		txtWidth.setValue(this.width);
		txtHeight.setValue(this.height);
		
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
		return DEFAULT_SHAPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return new Rectangle(width, height);
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
			JOptionPane.showMessageDialog(owner, "A rectangle must have a width and height greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
