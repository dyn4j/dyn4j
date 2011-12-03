/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.utilities.SystemUtilities;

/**
 * Panel showing the system information.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class SystemPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 6078435493627945050L;

	/**
	 * Default constructor
	 */
	public  SystemPanel() {
		JLabel lblJavaVersion = new JLabel(Resources.getString("panel.system.java"));
		JLabel lblJavaVendor = new JLabel(Resources.getString("panel.system.vendor"));
		JLabel lblOperatingSystem = new JLabel(Resources.getString("panel.system.os"));
		JLabel lblArchitecture = new JLabel(Resources.getString("panel.system.architecture"));
		JLabel lblNumberOfCpus = new JLabel(Resources.getString("panel.system.cpus"));
		JLabel lblLocale = new JLabel(Resources.getString("panel.system.locale"));
		
		JTextField valJavaVersion = new JTextField(SystemUtilities.getJavaVersion());
		JTextField valJavaVendor = new JTextField(SystemUtilities.getJavaVendor());
		JTextField valOperatingSystem = new JTextField(SystemUtilities.getOperatingSystem());
		JTextField valArchitecture = new JTextField(SystemUtilities.getArchitecture());
		JTextField valNumberOfCpus = new JTextField(String.valueOf(Runtime.getRuntime().availableProcessors()));
		JTextField valLocale = new JTextField(Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
		
		valJavaVersion.setEditable(false);
		valJavaVendor.setEditable(false);
		valOperatingSystem.setEditable(false);
		valArchitecture.setEditable(false);
		valNumberOfCpus.setEditable(false);
		valLocale.setEditable(false);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblJavaVersion)
						.addComponent(lblJavaVendor)
						.addComponent(lblOperatingSystem)
						.addComponent(lblArchitecture)
						.addComponent(lblNumberOfCpus)
						.addComponent(lblLocale))
				.addGroup(layout.createParallelGroup()
						.addComponent(valJavaVersion)
						.addComponent(valJavaVendor)
						.addComponent(valOperatingSystem)
						.addComponent(valArchitecture)
						.addComponent(valNumberOfCpus)
						.addComponent(valLocale)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblJavaVersion)
						.addComponent(valJavaVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblJavaVendor)
						.addComponent(valJavaVendor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblOperatingSystem)
						.addComponent(valOperatingSystem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblArchitecture)
						.addComponent(valArchitecture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblNumberOfCpus)
						.addComponent(valNumberOfCpus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLocale)
						.addComponent(valLocale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
}
