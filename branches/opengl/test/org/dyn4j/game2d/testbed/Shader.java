package org.dyn4j.game2d.testbed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

public class Shader {
	protected String[] source;
	protected int[] lengths;
	protected int id;
	
	public static final Shader load(String path) {
		// open a stream to the file
		InputStream stream = Shader.class.getResourceAsStream(path);
		// use a buffered reader to read in the contents
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		// read line by line
		List<String> lines = new ArrayList<String>();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("/")) {
					lines.add(line);
				}
			}
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
		
		Shader shader = new Shader();
		// set the source for the shader
		shader.source = lines.toArray(new String[0]);
		
		// determine the lengths of the lines in the source
		int[] lengths = new int[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			lengths[i] = lines.get(i).length();
		}
		shader.lengths = lengths;
		
		return shader;
	}

	public String[] getSource() {
		return source;
	}

	public int[] getLengths() {
		return lengths;
	}
}
