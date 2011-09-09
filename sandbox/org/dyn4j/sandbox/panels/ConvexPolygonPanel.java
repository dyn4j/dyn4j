package org.dyn4j.sandbox.panels;

import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.geometry.Convex;

/**
 * Panel used to create a polygon shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConvexPolygonPanel extends ConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -6622003790931052412L;
	
	// polygon panels
	
	/** The right triangle panel */
	private RightTrianglePanel pnlRightTriangle;
	
	/** The isosceles triangle panel */
	private IsoscelesTrianglePanel pnlIsoscelesTriangle;
	
	/** The equilateral triangle panel */
	private EquilateralTrianglePanel pnlEquilateralTriangle;
	
	/** The unit circle polygon panel */
	private UnitCirclePolygonPanel pnlUnitCirclePolygon;
	
	/** The arbitrary polygon panel */
	private ArbitraryConvexPolygonPanel pnlArbitraryPolygon;
	
	/** The from file polygon panel */
	private FromFileConvexPolygonPanel pnlFromFilePolygon;
	
	// polygon radio buttons
	
	/** The right triangle radio button */
	private JRadioButton rdoRightTriangle;
	
	/** The isosceles triangle radio button */
	private JRadioButton rdoIsoscelesTriangle;
	
	/** The equilateral triangle radio button */
	private JRadioButton rdoEquilateralTriangle;
	
	/** The unit circle polygon radio button */
	private JRadioButton rdoUnitCircle;
	
	/** The arbitrary polygon radio button */
	private JRadioButton rdoArbitrary;
	
	/** The from file polygon radio button */
	private JRadioButton rdoFromFile;
	
	/**
	 * Default constructor.
	 */
	public ConvexPolygonPanel() {
		this.pnlRightTriangle = new RightTrianglePanel();
		this.pnlIsoscelesTriangle = new IsoscelesTrianglePanel();
		this.pnlEquilateralTriangle = new EquilateralTrianglePanel();
		this.pnlUnitCirclePolygon = new UnitCirclePolygonPanel();
		this.pnlArbitraryPolygon = new ArbitraryConvexPolygonPanel();
		this.pnlFromFilePolygon = new FromFileConvexPolygonPanel();
		
		this.rdoRightTriangle = new JRadioButton("Right Triangle");
		this.rdoIsoscelesTriangle = new JRadioButton("Isosceles Triangle");
		this.rdoEquilateralTriangle = new JRadioButton("Equilateral Triangle");
		this.rdoUnitCircle = new JRadioButton("Unit Circle Polygon");
		this.rdoArbitrary = new JRadioButton("Arbitrary Polygon");
		this.rdoFromFile = new JRadioButton("From File");
		
		this.rdoRightTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoRightTriangle.isSelected()) {
					pnlRightTriangle.setVisible(true);
				} else {
					pnlRightTriangle.setVisible(false);
				}
			}
		});
		
		this.rdoIsoscelesTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoIsoscelesTriangle.isSelected()) {
					pnlIsoscelesTriangle.setVisible(true);
				} else {
					pnlIsoscelesTriangle.setVisible(false);
				}
			}
		});
		
		this.rdoEquilateralTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoEquilateralTriangle.isSelected()) {
					pnlEquilateralTriangle.setVisible(true);
				} else {
					pnlEquilateralTriangle.setVisible(false);
				}
			}
		});
		
		this.rdoUnitCircle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoUnitCircle.isSelected()) {
					pnlUnitCirclePolygon.setVisible(true);
				} else {
					pnlUnitCirclePolygon.setVisible(false);
				}
			}
		});
		
		this.rdoArbitrary.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoArbitrary.isSelected()) {
					pnlArbitraryPolygon.setVisible(true);
				} else {
					pnlArbitraryPolygon.setVisible(false);
				}
			}
		});
		
		this.rdoFromFile.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (rdoFromFile.isSelected()) {
					pnlFromFilePolygon.setVisible(true);
				} else {
					pnlFromFilePolygon.setVisible(false);
				}
			}
		});
		
		// set the unit circle one as the current one
		this.rdoArbitrary.setSelected(true);
		this.pnlRightTriangle.setVisible(false);
		this.pnlIsoscelesTriangle.setVisible(false);
		this.pnlEquilateralTriangle.setVisible(false);
		this.pnlUnitCirclePolygon.setVisible(false);
		this.pnlFromFilePolygon.setVisible(false);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.rdoRightTriangle);
		bg.add(this.rdoIsoscelesTriangle);
		bg.add(this.rdoEquilateralTriangle);
		bg.add(this.rdoUnitCircle);
		bg.add(this.rdoFromFile);
		bg.add(this.rdoArbitrary);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.rdoRightTriangle)
				.addComponent(this.rdoIsoscelesTriangle)
				.addComponent(this.rdoEquilateralTriangle)
				.addComponent(this.rdoUnitCircle)
				.addComponent(this.rdoFromFile)
				.addComponent(this.rdoArbitrary)
				
				.addComponent(this.pnlRightTriangle)
				.addComponent(this.pnlIsoscelesTriangle)
				.addComponent(this.pnlEquilateralTriangle)
				.addComponent(this.pnlUnitCirclePolygon)
				.addComponent(this.pnlFromFilePolygon)
				.addComponent(this.pnlArbitraryPolygon));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.rdoRightTriangle)
				.addComponent(this.rdoIsoscelesTriangle)
				.addComponent(this.rdoEquilateralTriangle)
				.addComponent(this.rdoUnitCircle)
				.addComponent(this.rdoFromFile)
				.addComponent(this.rdoArbitrary)
				
				.addComponent(this.pnlRightTriangle)
				.addComponent(this.pnlIsoscelesTriangle)
				.addComponent(this.pnlEquilateralTriangle)
				.addComponent(this.pnlUnitCirclePolygon)
				.addComponent(this.pnlFromFilePolygon)
				.addComponent(this.pnlArbitraryPolygon));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		// return the default polygon for the selected radio button
		if (this.rdoRightTriangle.isSelected()) {
			return this.pnlRightTriangle.getDefaultShape();
		} else if (this.rdoIsoscelesTriangle.isSelected()) {
			return this.pnlIsoscelesTriangle.getDefaultShape();
		} else if (this.rdoEquilateralTriangle.isSelected()) {
			return this.pnlEquilateralTriangle.getDefaultShape();
		} else if (this.rdoUnitCircle.isSelected()) {
			return this.pnlUnitCirclePolygon.getDefaultShape();
		} else if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.getDefaultShape();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.getDefaultShape();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		// return the polygon for the selected radio button
		if (this.rdoRightTriangle.isSelected()) {
			return this.pnlRightTriangle.getShape();
		} else if (this.rdoIsoscelesTriangle.isSelected()) {
			return this.pnlIsoscelesTriangle.getShape();
		} else if (this.rdoEquilateralTriangle.isSelected()) {
			return this.pnlEquilateralTriangle.getShape();
		} else if (this.rdoUnitCircle.isSelected()) {
			return this.pnlUnitCirclePolygon.getShape();
		} else if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.getShape();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.getShape();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// return the polygon for the selected radio button
		if (this.rdoRightTriangle.isSelected()) {
			return this.pnlRightTriangle.isValidInput();
		} else if (this.rdoIsoscelesTriangle.isSelected()) {
			return this.pnlIsoscelesTriangle.isValidInput();
		} else if (this.rdoEquilateralTriangle.isSelected()) {
			return this.pnlEquilateralTriangle.isValidInput();
		} else if (this.rdoUnitCircle.isSelected()) {
			return this.pnlUnitCirclePolygon.isValidInput();
		} else if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.isValidInput();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.isValidInput();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		// return the polygon for the selected radio button
		if (this.rdoRightTriangle.isSelected()) {
			this.pnlRightTriangle.showInvalidInputMessage(owner);
		} else if (this.rdoIsoscelesTriangle.isSelected()) {
			this.pnlIsoscelesTriangle.showInvalidInputMessage(owner);
		} else if (this.rdoEquilateralTriangle.isSelected()) {
			this.pnlEquilateralTriangle.showInvalidInputMessage(owner);
		} else if (this.rdoUnitCircle.isSelected()) {
			this.pnlUnitCirclePolygon.showInvalidInputMessage(owner);
		} else if (this.rdoArbitrary.isSelected()) {
			this.pnlArbitraryPolygon.showInvalidInputMessage(owner);
		} else if (this.rdoFromFile.isSelected()) {
			this.pnlFromFilePolygon.showInvalidInputMessage(owner);
		}
	}
}
