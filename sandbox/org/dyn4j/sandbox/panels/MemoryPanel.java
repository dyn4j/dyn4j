/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel showing the memory information.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class MemoryPanel extends JPanel implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 6078435493627945050L;
	
	/** The runtime */
	private static final Runtime RUNTIME = Runtime.getRuntime();
	
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
	
	/**
	 * Default constructor
	 */
	public  MemoryPanel() {
		JLabel lblTotal = new JLabel(Messages.getString("panel.memory.total"));
		JLabel lblUsed = new JLabel(Messages.getString("panel.memory.used"));
		JLabel lblFree = new JLabel(Messages.getString("panel.memory.free"));
		
		this.txtTotal = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.memory.format")));
		this.txtUsed = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.memory.format")));
		this.txtUsedPercent = new JFormattedTextField(DecimalFormat.getPercentInstance());
		this.txtFree = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.memory.format")));
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
		
		this.pnlGraph = new LineGraphPanel(20, Messages.getString("panel.memory.format.axis"));
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
		
		Timer timer = new Timer(1000, this);
		timer.start();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// convert to MB
		double total = RUNTIME.totalMemory() / 1000000.0;
		double free = RUNTIME.freeMemory() / 1000000.0;
		double used = total - free;
		
		txtTotal.setValue(total);
		txtFree.setValue(free);
		txtFreePercent.setValue(free / total);
		txtUsed.setValue(used);
		txtUsedPercent.setValue(used / total);
		
		pnlGraph.setMaximumValue(total + 3);
		pnlGraph.addDataPoint(used, 0);
		pnlGraph.addDataPoint(total, 1);
		pnlGraph.repaint();
	}
}
