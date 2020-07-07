/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Version} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class VersionTest {
	/**
	 * Tests the get version number methods.
	 */
	@Test
	public void versions() {
		// get the version array
		int[] version = Version.getVersionNumbers();
		TestCase.assertEquals(Version.getMajorNumber(), version[0]);
		TestCase.assertEquals(Version.getMinorNumber(), version[1]);
		TestCase.assertEquals(Version.getRevisionNumber(), version[2]);
	}
	
	/**
	 * Tests the get version method.
	 */
	@Test
	public void getVersionString() {
		String version = Version.getVersion();
		TestCase.assertNotNull(version);
		TestCase.assertTrue(version.length() > 0);
	}
	
	/**
	 * Tests the main method printing the version.
	 * @throws UnsupportedEncodingException if an error occurs building the stream that redirects System.out for testing
	 */
	@Test
	public void main() throws UnsupportedEncodingException {
	    final PrintStream original = System.out;
	    
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    final String utf8 = StandardCharsets.UTF_8.name();
	    final PrintStream ps = new PrintStream(baos, true, utf8);
	    
	    System.setOut(ps);
	    Version.main(null);
	    System.setOut(original);
	    
	    String version = baos.toString(utf8);
	    
		TestCase.assertNotNull(version);
		TestCase.assertTrue(version.length() > 0);
	}
}
