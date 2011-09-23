package org.dyn4j.sandbox.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
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
 * Panel used to create an isosceles triangle shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class IsoscelesTrianglePanel extends ConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -2219164534123186833L;

	/** The default width */
	private static final double DEFAULT_WIDTH = 0.5;
	
	/** The default height */
	private static final double DEFAULT_HEIGHT = 1.0;
	
	/** The default shape */
	private static final Triangle DEFAULT_TRIANGLE = Geometry.createIsoscelesTriangle(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	
	/** The width of the rectangle */
	private double width = DEFAULT_WIDTH;
	
	/** The height of the rectangle */
	private double height = DEFAULT_HEIGHT;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/**
	 * Default constructor.
	 */
	public IsoscelesTrianglePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblWidth = new JLabel("Width", Icons.INFO, JLabel.LEFT);
		lblWidth.setToolTipText("The width of the base of the isosceles triangle.");
		JLabel lblHeight = new JLabel("Height", Icons.INFO, JLabel.LEFT);
		lblHeight.setToolTipText("The height of the isosceles triangle.");
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
				try {
					pnlPreview.setShape(Geometry.createIsoscelesTriangle(width, height));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		txtHeight.addFocusListener(new SelectTextFocusListener(txtHeight));
		txtHeight.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				height = number.doubleValue();
				try {
					pnlPreview.setShape(Geometry.createIsoscelesTriangle(width, height));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		
		JLabel lblPreview = new JLabel("Preview", Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText("Shows a preview of the current shape.");
		this.pnlPreview = new PreviewPanel(new Dimension(150, 150), Geometry.createIsoscelesTriangle(this.width, this.height));
		this.pnlPreview.setBackground(Color.WHITE);
		this.pnlPreview.setBorder(BorderFactory.createEtchedBorder());
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblWidth)
						.addComponent(lblHeight)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtWidth)
						.addComponent(txtHeight)
						.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblWidth)
						.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblHeight)
						.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPreview)
						.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
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
		return Geometry.createIsoscelesTriangle(this.width, this.height);
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
			JOptionPane.showMessageDialog(owner, "An isosceles triangle must have a width and height greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
