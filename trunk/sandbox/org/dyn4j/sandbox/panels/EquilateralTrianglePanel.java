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
 * Panel used to create an equilateral triangle shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EquilateralTrianglePanel extends ConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 4314268465643754706L;
	
	/** The default height */
	private static final double DEFAULT_HEIGHT = 0.5;
	
	/** The default shape */
	private static final Triangle DEFAULT_TRIANGLE = Geometry.createEquilateralTriangle(DEFAULT_HEIGHT);
	
	/** The height of the rectangle */
	private double height = DEFAULT_HEIGHT;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/**
	 * Default constructor.
	 */
	public EquilateralTrianglePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblHeight = new JLabel("Height", Icons.INFO, JLabel.LEFT);
		lblHeight.setToolTipText("The height of the equilateral triangle.");
		JFormattedTextField txtHeight = new JFormattedTextField(new DecimalFormat("0.000"));
		txtHeight.setValue(DEFAULT_HEIGHT);
		
		txtHeight.addFocusListener(new SelectTextFocusListener(txtHeight));
		txtHeight.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				height = number.doubleValue();
				try {
					pnlPreview.setShape(Geometry.createEquilateralTriangle(height));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		
		JLabel lblPreview = new JLabel("Preview", Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText("Shows a preview of the current shape.");
		this.pnlPreview = new PreviewPanel(new Dimension(150, 150), Geometry.createEquilateralTriangle(height));
		this.pnlPreview.setBackground(Color.WHITE);
		this.pnlPreview.setBorder(BorderFactory.createEtchedBorder());
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblHeight)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtHeight)
						.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
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
		return Geometry.createEquilateralTriangle(this.height);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (height <= 0.0) {
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
			JOptionPane.showMessageDialog(owner, "An equilateral triangle must have a height greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
