/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.sandbox.panels;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.geometry.Convex;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to create a polygon shape.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ConvexPolygonPanel extends ConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -6622003790931052412L;
	
	/** panel to contain the hidden and shown polygon panels */
	private JPanel pnlSource;
	
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
		
		this.rdoRightTriangle = new JRadioButton(Messages.getString("panel.convex.triangle.right"));
		this.rdoIsoscelesTriangle = new JRadioButton(Messages.getString("panel.convex.triangle.isosceles"));
		this.rdoEquilateralTriangle = new JRadioButton(Messages.getString("panel.convex.triangle.equilateral"));
		this.rdoUnitCircle = new JRadioButton(Messages.getString("panel.convex.polygon.unitCircle"));
		this.rdoArbitrary = new JRadioButton(Messages.getString("panel.convex.polygon.arbitrary"));
		this.rdoFromFile = new JRadioButton(Messages.getString("panel.convex.polygon.fromFile"));
		
		this.pnlSource = new JPanel();
		this.pnlSource.setLayout(new CardLayout());
		this.pnlSource.add(this.pnlRightTriangle, "rightTriangle");
		this.pnlSource.add(this.pnlIsoscelesTriangle, "isoscelesTriangle");
		this.pnlSource.add(this.pnlEquilateralTriangle, "equilateralTriangle");
		this.pnlSource.add(this.pnlUnitCirclePolygon, "unitCirclePolygon");
		this.pnlSource.add(this.pnlArbitraryPolygon, "arbitraryPolygon");
		this.pnlSource.add(this.pnlFromFilePolygon, "fromFilePolygon");
		
		
		this.rdoRightTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "rightTriangle");
				}
			}
		});
		
		this.rdoIsoscelesTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "isoscelesTriangle");
				}
			}
		});
		
		this.rdoEquilateralTriangle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "equilateralTriangle");
				}
			}
		});
		
		this.rdoUnitCircle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "unitCirclePolygon");
				}
			}
		});
		
		this.rdoArbitrary.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "arbitraryPolygon");
				}
			}
		});
		
		this.rdoFromFile.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "fromFilePolygon");
				}
			}
		});
		
		// set the unit circle one as the current one
		this.rdoArbitrary.setSelected(true);
		((CardLayout) this.pnlSource.getLayout()).show(this.pnlSource, "arbitraryPolygon");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.rdoRightTriangle);
		bg.add(this.rdoIsoscelesTriangle);
		bg.add(this.rdoEquilateralTriangle);
		bg.add(this.rdoUnitCircle);
		bg.add(this.rdoFromFile);
		bg.add(this.rdoArbitrary);
		
		JPanel pnlRadio = new JPanel();
		pnlRadio.setLayout(new GridLayout(3, 2));
		pnlRadio.add(this.rdoRightTriangle);
		pnlRadio.add(this.rdoUnitCircle);
		pnlRadio.add(this.rdoIsoscelesTriangle);
		pnlRadio.add(this.rdoFromFile);
		pnlRadio.add(this.rdoEquilateralTriangle);
		pnlRadio.add(this.rdoArbitrary);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlRadio)
				.addComponent(this.pnlSource));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlRadio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlSource));
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
