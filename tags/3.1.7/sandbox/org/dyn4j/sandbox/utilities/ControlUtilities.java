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
package org.dyn4j.sandbox.utilities;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

/**
 * Utility class to help working with controls.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ControlUtilities {
	/**
	 * Returns the double value of the number stored in the given text field.
	 * @param field the text field
	 * @return double the double value
	 */
	public static final double getDoubleValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.doubleValue();
	}
	
	/**
	 * Returns the int value of the number stored in the given text field.
	 * @param field the text field
	 * @return int the integer value
	 */
	public static final int getIntValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.intValue();
	}
	
	/**
	 * Returns the parent window for the given component.
	 * @param component the component
	 * @return Window
	 * @since 1.0.1
	 */
	public static final Window getParentWindow(Component component) {
		// get the parent frame
		Frame frame = JOptionPane.getFrameForComponent(component);
		// first check for a parent dialog component
		while (component != null) {
			component = component.getParent();
			if (component instanceof Dialog) {
				return (Dialog) component;
			}
		}
		// if nothing was found, then use the frame for the component
		return frame;
	}
}
