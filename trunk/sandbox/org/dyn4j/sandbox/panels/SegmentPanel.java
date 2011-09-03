package org.dyn4j.sandbox.panels;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a segment shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SegmentPanel extends ShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 9034797908902106167L;

	/** The default start point for the default shape */
	private static final Vector2 DEFAULT_START = new Vector2(-0.5, 0.0);
	
	/** The default end point for the default shape */
	private static final Vector2 DEFAULT_END = new Vector2(0.5, 0.0);
	
	/** The default segment shape */
	private static final Segment DEFAULT_SEGMENT = new Segment(DEFAULT_START, DEFAULT_END);
	
	/** The start point */
	private Vector2 start = DEFAULT_START.copy();
	
	/** The end point */
	private Vector2 end = DEFAULT_END.copy();
	
	/**
	 * Default constructor.
	 */
	public SegmentPanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblStart = new JLabel("Start", Icons.INFO, JLabel.LEFT);
		lblStart.setToolTipText("The start point of the line segment.");
		JLabel lblEnd = new JLabel("End", Icons.INFO, JLabel.LEFT);
		lblEnd.setToolTipText("The end point of the line segment.");
		JLabel lblSX = new JLabel("x");
		JLabel lblSY = new JLabel("y");
		JLabel lblEX = new JLabel("x");
		JLabel lblEY = new JLabel("y");
		
		JFormattedTextField txtSX = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtSY = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtEX = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtEY = new JFormattedTextField(new DecimalFormat("0.000"));
		
		txtSX.setValue(DEFAULT_START.x);
		txtSY.setValue(DEFAULT_START.y);
		txtEX.setValue(DEFAULT_END.x);
		txtEY.setValue(DEFAULT_END.y);
		
		txtSX.addFocusListener(new SelectTextFocusListener(txtSX));
		txtSX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				start.x = number.doubleValue();
			}
		});
		txtSY.addFocusListener(new SelectTextFocusListener(txtSY));
		txtSY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				start.y = number.doubleValue();
			}
		});
		
		txtEX.addFocusListener(new SelectTextFocusListener(txtEX));
		txtEX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				end.x = number.doubleValue();
			}
		});
		txtEY.addFocusListener(new SelectTextFocusListener(txtEY));
		txtEY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				end.y = number.doubleValue();
			}
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblStart)
						.addComponent(lblEnd))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtSX)
								.addComponent(lblSX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtSY)
								.addComponent(lblSY))
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtEX)
								.addComponent(lblEX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtEY)
								.addComponent(lblEY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblStart)
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtSX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtSY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(lblSX)
								.addComponent(lblSY)))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblEnd)
						.addGroup(layout.createSequentialGroup()
								.addComponent(txtEX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtEY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(lblEX)
								.addComponent(lblEY))));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_SEGMENT;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return new Segment(this.start, this.end);
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
