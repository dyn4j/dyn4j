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
package org.dyn4j.game2d.testbed;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class used to load a shader program from the classpath.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.3
 */
public class Shader {
	/** The length of the source and lengths arrays */
	public int length;
	
	/** The array string containing the lines of the shader */
	public String[] source;
	
	/** The array containing the lengths of the lines of the shader */
	public int[] lengths;
	
	/**
	 * Can't instantiate directly.
	 */
	private Shader() {}
	
	/**
	 * Loads a shader program (text file) from the classpath and returns a shader object
	 * containing the data necessary to create an OpenGL shader.
	 * @param path the text file path; must be on the classpath
	 * @return Shader a new shader
	 * @throws IOException thrown if an error occurs while reading the file
	 * @throws FileNotFoundException thrown if the given file is not found on the classpath
	 */
	public static final Shader load(String path) throws IOException, FileNotFoundException {
		// open a stream to the file
		InputStream stream = Shader.class.getResourceAsStream(path);
		// check for null
		if (stream == null) throw new FileNotFoundException(path + " not found in classpath.");
		// use a buffered reader to read in the contents
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		// create a list to store the lines
		List<String> lines = new ArrayList<String>();
		
		// read the buffer line by line
		String line;
		while ((line = br.readLine()) != null) {
			// skip comment lines
			if (!line.startsWith("/")) {
				lines.add(line);
			}
		}
		
		// create a new shader object to store the data
		Shader shader = new Shader();
		// set the length
		shader.length = lines.size();
		// set the source for the shader
		shader.source = lines.toArray(new String[0]);
		
		// determine the lengths of the lines in the source
		int[] lengths = new int[shader.length];
		for (int i = 0; i < shader.length; i++) {
			// get the length of each line
			lengths[i] = lines.get(i).length();
		}
		// set the lengths
		shader.lengths = lengths;
		
		// attempt to close the stream
		try { br.close(); } catch (IOException e) {}
		
		// return the shader
		return shader;
	}
}
