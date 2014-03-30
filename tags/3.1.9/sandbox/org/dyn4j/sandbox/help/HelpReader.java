/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.help;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dyn4j.sandbox.resources.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Xml reader for the Help.xml document.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.4
 */
public class HelpReader extends DefaultHandler {
	/**
	 * Returns a new simulation object from the given file.
	 * @param file the file to read from
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static HelpNode fromXml(File file) throws ParserConfigurationException, SAXException, IOException {
		return HelpReader.fromXml(new InputSource(new FileReader(file)));
	}
	
	/**
	 * Returns a new simulation object from the given string.
	 * @param xml the string containing the XML to read from
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static HelpNode fromXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		return HelpReader.fromXml(new InputSource(new StringReader(xml)));
	}
	
	/**
	 * Returns a new simulation object from the given stream.
	 * @param stream the input stream containing the xml
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static HelpNode fromXml(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		return HelpReader.fromXml(new InputSource(stream));
	}
	
	/**
	 * Returns a new simulation object from the given input source.
	 * @param source the source containing the XML
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	private static HelpNode fromXml(InputSource source) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		HelpReader reader = new HelpReader();
		
		parser.parse(source, reader);
		
		return reader.root;
	}
	
	/** The root node */
	private HelpNode root;
	
	/** The parent node stack */
	private Deque<HelpNode> stack = new ArrayDeque<HelpNode>();
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// check for root node
		if (qName.equals("Help")) {
			this.root = new HelpNode(Messages.getString(attributes.getValue("Name")), attributes.getValue("Path"));
			this.stack.push(this.root);
		} else if (qName.equals("Item")) {
			HelpNode node = new HelpNode(Messages.getString(attributes.getValue("Name")), attributes.getValue("Path"));
			HelpNode parent = this.stack.peek();
			parent.nodes.add(node);
			
			this.stack.push(node);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("Item")) {
			this.stack.pop();
		}
	}
}
