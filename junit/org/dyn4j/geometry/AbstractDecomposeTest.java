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
package org.dyn4j.geometry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Test case for the decomposition classes.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public class AbstractDecomposeTest {
	/**
	 * Loads the given resource from the file system and attempts to
	 * interpret the contents.
	 * <p>
	 * If any exception occurs, null is returned.
	 * @param stream the stream to load
	 * @return {@link Vector2}[] the points in the file
	 */
	protected Vector2[] load(InputStream stream) {
		if (stream == null) return null;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		return parse(br);
	}
	
	/**
	 * Parses the contents of the buffered reader.
	 * <p>
	 * If any exception occurs, null is returned.
	 * @param reader the buffered reader to read from
	 * @return {@link Vector2}[] the points
	 */
	protected Vector2[] parse(BufferedReader reader) {
		String line;
		int i = 0;
		Vector2[] points = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				if (line.startsWith("#")) continue;
				if (i == 0) {
					// the first line contains the number of vertices
					int size = Integer.parseInt(line.trim());
					points = new Vector2[size];
				} else {
					// otherwise its a line containing a point
					String[] xy = line.split("\\s");
					double x = Double.parseDouble(xy[0].trim());
					double y = Double.parseDouble(xy[1].trim());
					Vector2 p = new Vector2(x, y);
					points[i - 1] = p;
				}
				i++;
			}
			
			return points;
		} catch (IOException e) {
			// just show the error on the console
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// just show the error on the console
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			// just show the error on the console
			e.printStackTrace();
		}
		return null;
	}
}
