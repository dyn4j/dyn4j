package org.dyn4j.sandbox.panels;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dyn4j.sandbox.utilities.SystemUtilities;

/**
 * Panel showing the system information.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SystemPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 6078435493627945050L;

	/**
	 * Default constructor
	 */
	public  SystemPanel() {
		JLabel lblJavaVersion = new JLabel("Java");
		JLabel lblJavaVendor = new JLabel("Vendor");
		JLabel lblOperatingSystem = new JLabel("OS");
		JLabel lblArchitecture = new JLabel("Arch.");
		JLabel lblNumberOfCpus = new JLabel("CPUs");
		
		JTextField valJavaVersion = new JTextField(SystemUtilities.getJavaVersion());
		JTextField valJavaVendor = new JTextField(SystemUtilities.getJavaVendor());
		JTextField valOperatingSystem = new JTextField(SystemUtilities.getOperatingSystem());
		JTextField valArchitecture = new JTextField(SystemUtilities.getArchitecture());
		JTextField valNumberOfCpus = new JTextField(String.valueOf(Runtime.getRuntime().availableProcessors()));
		
		valJavaVersion.setEditable(false);
		valJavaVendor.setEditable(false);
		valOperatingSystem.setEditable(false);
		valArchitecture.setEditable(false);
		valNumberOfCpus.setEditable(false);
		
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
						.addComponent(lblNumberOfCpus))
				.addGroup(layout.createParallelGroup()
						.addComponent(valJavaVersion)
						.addComponent(valJavaVendor)
						.addComponent(valOperatingSystem)
						.addComponent(valArchitecture)
						.addComponent(valNumberOfCpus)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblJavaVersion)
						.addComponent(valJavaVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblJavaVendor)
						.addComponent(valJavaVendor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblOperatingSystem)
						.addComponent(valOperatingSystem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblArchitecture)
						.addComponent(valArchitecture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblNumberOfCpus)
						.addComponent(valNumberOfCpus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
}
