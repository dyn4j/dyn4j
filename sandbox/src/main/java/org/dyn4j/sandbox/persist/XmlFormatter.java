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
package org.dyn4j.sandbox.persist;

/**
 * Class used to format a valid XML document with indents and newlines.
 * <p>
 * Simplified from: http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java/2920419#2920419
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class XmlFormatter {
	/** The new line separator */
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	/** The number of characters to indent */
	private int indentNumChars;
	
	/** True if the current line is a single line element */
	private boolean singleLine;
	
	/**
	 * Full constructor.
	 * @param indentNumChars the number of characters to indent
	 */
	public XmlFormatter(int indentNumChars) {
		this.indentNumChars = indentNumChars;
		this.singleLine = false;
	}

	/**
	 * Formats the string (xml).
	 * @param s the xml string
	 * @return String
	 */
	public String format(String s) {
		int indent = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char currentChar = s.charAt(i);
			if (currentChar == '<') {
				char nextChar = s.charAt(i + 1);
				if (nextChar == '/')
					indent -= indentNumChars;
				if (!singleLine)
		            sb.append(this.createIndentation(indent));
				if (nextChar != '?' && nextChar != '!' && nextChar != '/')
					indent += indentNumChars;
				singleLine = false;
			}
			sb.append(currentChar);
			if (currentChar == '>') {
				if (s.charAt(i - 1) == '/') {
					indent -= indentNumChars;
					sb.append(NEW_LINE);
				} else {
					int nextStartElementPos = s.indexOf('<', i);
					if (nextStartElementPos > i + 1) {
						String textBetweenElements = s.substring(i + 1, nextStartElementPos);
						// If the space between elements is solely newlines,
						// let them through to preserve additional newlines
						// in source document.
						if (textBetweenElements.replaceAll("(\n|\r\n|\r)", "").length() == 0) {
							sb.append(textBetweenElements + NEW_LINE);
						}
						// Put tags and text on a single line if the text is
						// short.
						else {
							sb.append(textBetweenElements);
							singleLine = true;
						}
						i = nextStartElementPos - 1;
					} else {
						sb.append(NEW_LINE);
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Adds spaces to create indentation.
	 * @param numChars the number of spaces
	 * @return String
	 */
	private String createIndentation(int numChars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numChars; i++)
			sb.append(" ");
		return sb.toString();
	}
}