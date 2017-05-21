/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j;

/**
 * The version of the engine.
 * @author William Bittle
 * @version 3.2.4
 * @since 1.0.0
 */
public final class Version {
	/** The major version number; high impact changes; major API changes, major enhancements, etc. */
	private static final int MAJOR = 3;
	
	/** The minor version number; medium impact changes; minor API changes, minor enhancements, major bug fixes, etc. */
	private static final int MINOR = 2;
	
	/** The revision number; low impact changes; deprecating API changes, minor bug fixes, etc. */
	private static final int REVISION = 4;
	
	/**
	 * Hide the constructor.
	 */
	private Version() {}
	
	/**
	 * Returns the version as a string.
	 * @return String
	 */
	public static final String getVersion() {
		return MAJOR + "." + MINOR + "." + REVISION;
	}
	
	/**
	 * Returns the version numbers in an array of ints.
	 * <p>
	 * The array is of length 3 and has the major, minor, and
	 * revision numbers in that order.
	 * @return int[] the major, minor, and revision numbers
	 * @since 3.1.0
	 */
	public static final int[] getVersionNumbers() {
		return new int[] { MAJOR, MINOR, REVISION };
	}
	
	/**
	 * Returns the major version number.
	 * @return int
	 * @since 3.1.0
	 */
	public static final int getMajorNumber() {
		return MAJOR;
	}
	
	/**
	 * Returns the minor version number.
	 * @return int
	 * @since 3.1.0
	 */
	public static final int getMinorNumber() {
		return MINOR;
	}
	
	/**
	 * Returns the revision number.
	 * @return int
	 * @since 3.1.0
	 */
	public static final int getRevisionNumber() {
		return REVISION;
	}
	
	/**
	 * Main class to print the version to the console.
	 * @param args command line arguments (none accepted)
	 */
	public static final void main(String[] args) {
		System.out.println("dyn4j v" + Version.getVersion());
	}
}
