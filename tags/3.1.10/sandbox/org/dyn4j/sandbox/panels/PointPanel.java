/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to input a point or vector.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class PointPanel extends JPanel implements ActionListener, PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = 5446710351912720509L;
	
	/** The text field for the x value */
	private JFormattedTextField txtX;
	
	/** The text field for the y value */
	private JFormattedTextField txtY;
	
	/** The button to remove the point */
	protected JButton btnRemove;
	
	/** The button to add the point */
	protected JButton btnAdd;
	
	/**
	 * Default constructor.
	 */
	public PointPanel() {
		this(0.0, 0.0);
	}
	
	/**
	 * Full constructor.
	 * @param x the initial x value
	 * @param y the initial y value
	 */
	public PointPanel(double x, double y) {
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		this.txtX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.point.format")));
		this.txtY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.point.format")));
		
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		
		this.txtX.setValue(x);
		this.txtY.setValue(y);
		
		this.txtX.setColumns(8);
		this.txtY.setColumns(8);
		
		this.txtX.addPropertyChangeListener("value", this);
		this.txtY.addPropertyChangeListener("value", this);
		
		this.btnAdd = new JButton();
		this.btnAdd.setIcon(Icons.ADD);
		this.btnAdd.setToolTipText(Messages.getString("panel.point.add.tooltip"));
		this.btnAdd.addActionListener(this);
		this.btnAdd.setActionCommand("add");
		
		this.btnRemove = new JButton();
		this.btnRemove.setIcon(Icons.REMOVE);
		this.btnRemove.setToolTipText(Messages.getString("panel.point.remove.tooltip"));
		this.btnRemove.addActionListener(this);
		this.btnRemove.setActionCommand("remove");
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(this.txtX)
				.addComponent(lblX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtY)
				.addComponent(lblY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnRemove, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY)
						.addComponent(this.btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnRemove, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Adds an action listener to listen for button events.
	 * @param actionListener the action listener to add
	 */
	public void addActionListener(ActionListener actionListener) {
		this.listenerList.add(ActionListener.class, actionListener);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		// set the source to this
		e.setSource(this);
		// forward the event to the listeners on this class
		for (ActionListener listener : listeners) {
			listener.actionPerformed(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		ActionListener[] listeners = this.getListeners(ActionListener.class);
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
		// forward the event to the listeners on this class
		for (ActionListener listener : listeners) {
			listener.actionPerformed(event);
		}
	}
	
	/**
	 * Returns the x value of the point.
	 * @return double
	 */
	public double getValueX() {
		Number number = (Number)this.txtX.getValue();
		return number.doubleValue();
	}
	
	/**
	 * Returns the y value of the point.
	 * @return double
	 */
	public double getValueY() {
		Number number = (Number)this.txtY.getValue();
		return number.doubleValue();
	}
}
