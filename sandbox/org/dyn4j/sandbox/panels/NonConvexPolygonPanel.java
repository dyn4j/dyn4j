package org.dyn4j.sandbox.panels;

import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.decompose.Bayazit;
import org.dyn4j.geometry.decompose.Decomposer;
import org.dyn4j.geometry.decompose.EarClipping;
import org.dyn4j.geometry.decompose.SweepLine;
import org.dyn4j.sandbox.controls.ComboItem;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create fixtures from a decomposable polygon.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NonConvexPolygonPanel extends NonConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -6622003790931052412L;

	/** The bayazit algorithm item */
	private static final ComboItem BAYAZIT = new ComboItem("Bayazit", new Bayazit());
	
	/** The ear clipping algorithm item */
	private static final ComboItem EAR_CLIPPING = new ComboItem("Ear Clipping", new EarClipping());
	
	/** The sweep line algorithm item */
	private static final ComboItem SWEEP_LINE = new ComboItem("Sweep Line", new SweepLine());
	
	/** The array of algorithms */
	private static final ComboItem[] ITEMS = new ComboItem[] {
		BAYAZIT,
		EAR_CLIPPING,
		SWEEP_LINE
	};
	
	/** The default decomposition algorithm */
	private static final ComboItem DEFAULT_ALGORITHM = BAYAZIT;
	
	// polygon panels
	
	/** The arbitrary polygon panel */
	private ArbitraryNonConvexPolygonPanel pnlArbitraryPolygon;
	
	/** The from file polygon panel */
	private FromFileNonConvexPolygonPanel pnlFromFilePolygon;
	
	// polygon radio buttons
	
	/** The arbitrary polygon radio button */
	private JRadioButton rdoArbitrary;
	
	/** The from file polygon radio button */
	private JRadioButton rdoFromFile;
	
	// decomposition algorithms
	
	/** The algorithm combo box */
	private JComboBox cmbAlgorithms;
	
	/** The panel to show/hide sources */
	private JPanel pnlSource;
	
	/**
	 * Default constructor.
	 */
	public NonConvexPolygonPanel() {
		JLabel lblAlgorithm = new JLabel("Algorithm", Icons.INFO, JLabel.LEFT);
		lblAlgorithm.setToolTipText("The decomposition algorithm to use.");
		JLabel lblSource = new JLabel("Source", Icons.INFO, JLabel.LEFT);
		lblSource.setToolTipText("The source of the polygon points.");
		
		this.cmbAlgorithms = new JComboBox(ITEMS);
		this.cmbAlgorithms.setSelectedItem(DEFAULT_ALGORITHM);
		this.cmbAlgorithms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ComboItem item = (ComboItem)cmbAlgorithms.getSelectedItem();
				Decomposer decomposer = (Decomposer)item.getValue();
				
				pnlArbitraryPolygon.setDecomposer(decomposer);
				pnlFromFilePolygon.setDecomposer(decomposer);
			}
		});
		
		Decomposer decomposer = (Decomposer)DEFAULT_ALGORITHM.getValue();
		
		this.pnlArbitraryPolygon = new ArbitraryNonConvexPolygonPanel(decomposer);
		this.pnlFromFilePolygon = new FromFileNonConvexPolygonPanel(decomposer);
		
		this.rdoArbitrary = new JRadioButton("Arbitrary Polygon");
		this.rdoFromFile = new JRadioButton("From File");
		
		// set arbitrary as the current one
		this.rdoArbitrary.setSelected(true);
		
		this.rdoArbitrary.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				JRadioButton radio = (JRadioButton)event.getSource();
				CardLayout cl = (CardLayout)pnlSource.getLayout();
				if (radio.isSelected()) {
					cl.show(pnlSource, "arbitrary");
				} else {
					cl.show(pnlSource, "fromFile");
				}
			}
		});
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.rdoFromFile);
		bg.add(this.rdoArbitrary);
		
		this.pnlSource = new JPanel();
		this.pnlSource.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Details "));
		this.pnlSource.setLayout(new CardLayout());
		this.pnlSource.add(this.pnlArbitraryPolygon, "arbitrary");
		this.pnlSource.add(this.pnlFromFilePolygon, "fromFile");
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblAlgorithm)
								.addComponent(lblSource))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.cmbAlgorithms)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.rdoFromFile)
										.addComponent(this.rdoArbitrary))))
				.addComponent(this.pnlSource));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAlgorithm)
						.addComponent(this.cmbAlgorithms, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblSource)
						.addComponent(this.rdoFromFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.rdoArbitrary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.pnlSource));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.NonConvexShapePanel#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		// return the polygon for the selected radio button
		if (this.rdoArbitrary.isSelected()) {
			return this.pnlArbitraryPolygon.getShapes();
		} else if (this.rdoFromFile.isSelected()) {
			return this.pnlFromFilePolygon.getShapes();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		// return the polygon for the selected radio button
		if (this.rdoArbitrary.isSelected()) {
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
		if (this.rdoArbitrary.isSelected()) {
			this.pnlArbitraryPolygon.showInvalidInputMessage(owner);
		} else if (this.rdoFromFile.isSelected()) {
			this.pnlFromFilePolygon.showInvalidInputMessage(owner);
		}
	}
}
