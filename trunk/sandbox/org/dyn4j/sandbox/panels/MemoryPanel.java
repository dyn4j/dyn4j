package org.dyn4j.sandbox.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel showing the memory information.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MemoryPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 6078435493627945050L;
	
	/** The total graph width */
	private static final int GRAPH_WIDTH = 200;
	
	/** The total graph height */
	private static final int GRAPH_HEIGHT = 100;
	
	/** The text box for the total heap memory */
	private JFormattedTextField txtTotal;
	
	/** The text box for the free memory in the heap */
	private JFormattedTextField txtFree;
	
	/** The text box for the percent free memory in the heap */
	private JFormattedTextField txtFreePercent;
	
	/** The text box for the used memory in the heap */
	private JFormattedTextField txtUsed;
	
	/** The text box for the percent used memory in the heap */
	private JFormattedTextField txtUsedPercent;
	
	/** The graph panel */
	private LineGraphPanel pnlGraph;
	
	/** The last time the panel was updated */
	private long lastUpdate;
	
	/**
	 * Default constructor
	 */
	public  MemoryPanel() {
		JLabel lblTotal = new JLabel("Total");
		JLabel lblUsed = new JLabel("Used");
		JLabel lblFree = new JLabel("Free");
		
		this.txtTotal = new JFormattedTextField(new DecimalFormat("0 MB"));
		this.txtUsed = new JFormattedTextField(new DecimalFormat("0 MB"));
		this.txtUsedPercent = new JFormattedTextField(DecimalFormat.getPercentInstance());
		this.txtFree = new JFormattedTextField(new DecimalFormat("0 MB"));
		this.txtFreePercent = new JFormattedTextField(DecimalFormat.getPercentInstance());
		
		this.txtTotal.setHorizontalAlignment(JTextField.RIGHT);
		this.txtUsed.setHorizontalAlignment(JTextField.RIGHT);
		this.txtUsedPercent.setHorizontalAlignment(JTextField.RIGHT);
		this.txtFree.setHorizontalAlignment(JTextField.RIGHT);
		this.txtFreePercent.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txtTotal.setEditable(false);
		this.txtUsed.setEditable(false);
		this.txtUsedPercent.setEditable(false);
		this.txtFree.setEditable(false);
		this.txtFreePercent.setEditable(false);
		
		Dimension size = new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT);
		this.pnlGraph = new LineGraphPanel(20);
		this.pnlGraph.setStaticSize(size);
		this.pnlGraph.addSeries(new Color(32, 171, 217));
		this.pnlGraph.addSeries(new Color(241, 154, 42));
		this.pnlGraph.setBorder(BorderFactory.createEtchedBorder());
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblTotal)
								.addComponent(lblUsed)
								.addComponent(lblFree))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtTotal)
								.addComponent(this.txtUsed)
								.addComponent(this.txtFree))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtUsedPercent)
								.addComponent(this.txtFreePercent)))
				.addComponent(this.pnlGraph));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblTotal)
						.addComponent(this.txtTotal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblUsed)
						.addComponent(this.txtUsed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtUsedPercent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblFree)
						.addComponent(this.txtFree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtFreePercent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.pnlGraph));
		
		this.lastUpdate = System.nanoTime();
	}
	
	/**
	 * Updates the memory panel to get the new results.
	 */
	public void update() {
		// check if its time to update
		long time = System.nanoTime();
		long diff = time - this.lastUpdate;
		// update only every second
		if (diff > 1000000000) {
			Runtime runtime = Runtime.getRuntime();
			// convert to MB
			double total = runtime.totalMemory() / 1000000.0;
			double free = runtime.freeMemory() / 1000000.0;
			double used = total - free;
			
			this.txtTotal.setValue(total);
			this.txtFree.setValue(free);
			this.txtFreePercent.setValue(free / total);
			this.txtUsed.setValue(used);
			this.txtUsedPercent.setValue(used / total);
			
			this.pnlGraph.setMaximumValue(total + 3);
			this.pnlGraph.addDataPoint(used, 0);
			this.pnlGraph.addDataPoint(total, 1);
			this.pnlGraph.repaint();
			
			this.lastUpdate = time;
		}
	}
}
