package org.dyn4j.sandbox.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for exception handling.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionUtilities {
	/**
	 * Returns the stack trace of the given exception in a string.
	 * @param e the exception
	 * @return String
	 */
	public static final String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
