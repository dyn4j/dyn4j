package org.dyn4j.sandbox.utilities;

/**
 * Utility class used to get system related information.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SystemUtilities {
	/**
	 * Returns the current runtime version.
	 * @return String
	 */
	public static final String getJavaVersion() {
		try{
			return System.getProperty("java.version");
		} catch (SecurityException e) {
			return "Access Denied";
		}
	}
	
	/**
	 * Returns the current runtime vendor.
	 * @return String
	 */
	public static final String getJavaVendor() {
		try{
			return System.getProperty("java.vendor");
		} catch (SecurityException e) {
			return "Access Denied";
		}
	}
	
	/**
	 * Returns the current operating system name.
	 * @return String
	 */
	public static final String getOperatingSystem() {
		try{
			return System.getProperty("os.name");
		} catch (SecurityException e) {
			return "Access Denied";
		}
	}
	
	/**
	 * Returns the current architecture.
	 * @return String
	 */
	public static final String getArchitecture() {
		try{
			return System.getProperty("os.arch");
		} catch (SecurityException e) {
			return "Access Denied";
		}
	}
}
