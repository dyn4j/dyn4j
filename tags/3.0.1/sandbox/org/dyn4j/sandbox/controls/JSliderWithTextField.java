package org.dyn4j.sandbox.controls;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.sandbox.listeners.SelectTextFocusListener;

/**
 * Represents a slider control with an attached text field.
 * <p>
 * Changes from either the slider or text field will change the underlying value of this control.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class JSliderWithTextField extends JPanel implements PropertyChangeListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 1397687871572398181L;

	/** The slider control */
	private JSlider slider;
	
	/** The text field control */
	private JFormattedTextField textField;
	
	/** The minimum value */
	private int min;
	
	/** The maximum value */
	private int max;
	
	/** The value scale (used to allow not int values) */
	private double scale;
	
	/** Pre-inverted scale value */
	private double invScale;
	
	/** The format used by the text field */
	private NumberFormat format;
	
	/**
	 * Optional constructor.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param initialValue the initial value
	 * @see #JSliderWithTextField(int, int, int, double, DecimalFormat)
	 */
	public JSliderWithTextField(int min, int max, int initialValue) {
		this(min, max, initialValue, 1.0, null);
	}
	
	/**
	 * Full constructor.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param initialValue the initial value
	 * @param scale the scale factor between 0.0 and 1.0
	 * @param format the decimal format
	 */
	public JSliderWithTextField(int min, int max, int initialValue, double scale, NumberFormat format) {
		this.min = min;
		this.max = max;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.slider = new JSlider(JSlider.HORIZONTAL, min, max, initialValue);
		this.slider.addChangeListener(this);
		if (scale == 1.0) {
			this.textField = new JFormattedTextField(NumberFormat.getIntegerInstance());
			this.textField.setValue(initialValue);
		} else {
			this.textField = new JFormattedTextField(format);
			this.textField.setValue(initialValue * scale);
		}
		this.textField.addPropertyChangeListener(this);
		this.textField.addFocusListener(new SelectTextFocusListener(this.textField));
		this.textField.setMaximumSize(this.textField.getPreferredSize());
		this.format = format;
		this.scale = scale;
		this.invScale = 1.0 / scale;
		this.add(this.slider);
		this.add(this.textField);
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if ("value".equals(event.getPropertyName())) {
			Number value = (Number)event.getNewValue();
			if (value != null) {
				int val = 0;
				if (this.scale != 1.0) {
					val = (int)(value.doubleValue() * this.invScale);
				} else {
					val = value.intValue();
				}
				// check the value against the min and max
				if (this.max >= val) {
					if (this.min <= val) {
						this.slider.setValue(val);
					} else {
						if (this.scale == 1.0) {
							this.textField.setValue(this.min);
						} else {
							this.textField.setValue(this.min * this.scale);
						}
					}
				} else {
					if (this.scale == 1.0) {
						this.textField.setValue(this.max);
					} else {
						this.textField.setValue(this.max * this.scale);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		JSlider slider = (JSlider)event.getSource();
		double value = (double)slider.getValue() * this.scale;
		// update the text box
		if (!slider.getValueIsAdjusting()) {
			this.textField.setValue(value);
		} else {
			if (this.format != null) {
				this.textField.setText(this.format.format(value));
			} else {
				this.textField.setText(String.valueOf((int)value));
			}
		}
		// forward an event to the change listeners
		ChangeListener[] listeners = this.getListeners(ChangeListener.class);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].stateChanged(new ChangeEvent(this));
		}
	}
	
	/**
	 * Adds a change listener to this component to be notified when the value changes.
	 * @param changeListener the change listener to add
	 */
	public void addChangeListener(ChangeListener changeListener) {
		this.listenerList.add(ChangeListener.class, changeListener);
	}
	
	/**
	 * Sets the number of columns for the text field.
	 * @param columns the number of columns
	 */
	public void setColumns(int columns) {
		this.textField.setColumns(columns);
	}
	
	/**
	 * Returns the raw value of this component.
	 * @return int
	 */
	public int getValue() {
		return this.slider.getValue();
	}
	
	/**
	 * Returns the scaled value of this component.
	 * @return double 
	 */
	public double getScaledValue() {
		return this.slider.getValue() * this.scale;
	}
	
	/**
	 * Sets the current value of this component.
	 * @param value the value
	 */
	public void setValue(int value) {
		this.slider.setValue(value);
	}
	
	/**
	 * Returns the scale.
	 * @return double
	 */
	public double getScale() {
		return this.scale;
	}
	
	/**
	 * Sets the scale.
	 * @param scale the scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
		this.invScale = 1.0 / scale;
	}
	
	/**
	 * Sets the number format used by the text field.
	 * @param format the format
	 */
	public void setNumberFormat(NumberFormat format) {
		this.format = format;
	}
	
	/**
	 * Returns the number format used by the text field.
	 * @return NumberFormat
	 */
	public NumberFormat getNumberFormat() {
		return this.format;
	}
	
	/**
	 * Returns the maximum value the slider will accept.
	 * @return int
	 */
	public int getMaximum() {
		return this.slider.getMaximum();
	}
	
	/**
	 * Sets the maximum value the slider will accept.
	 * @param value the maximum value
	 */
	public void setMaximum(int value) {
		this.slider.setMaximum(value);
	}
	
	/**
	 * Returns the maximum value the slider will accept.
	 * @return int
	 */
	public int getMinimum() {
		return this.slider.getMinimum();
	}
	
	/**
	 * Sets the minimum value the slider will accept.
	 * @param value the minimum value
	 */
	public void setMinimum(int value) {
		this.slider.setMinimum(value);
	}
	
	/**
	 * Returns the major tick spacing.
	 * @return int
	 */
	public int getMajorTickSpacing() {
		return this.slider.getMajorTickSpacing();
	}
	
	/**
	 * Sets the major tick spacing.
	 * @param spacing the spacing between major tick marks
	 */
	public void setMajorTickSpacing(int spacing) {
		this.slider.setMajorTickSpacing(spacing);
	}
	
	/**
	 * Returns the minor tick spacing.
	 * @return int
	 */
	public int getMinorTickSpacing() {
		return this.slider.getMinorTickSpacing();
	}
	
	/**
	 * Sets the minor tick spacing.
	 * @param spacing the spacing between minor tick marks
	 */
	public void setMinorTickSpacing(int spacing) {
		this.slider.setMinorTickSpacing(spacing);
	}
	
	/**
	 * Returns true if the labels should be shown.
	 * @return boolean
	 */
	public boolean getPaintLabels() {
		return this.slider.getPaintLabels();
	}
	
	/**
	 * Sets the paint labels property of the slider.
	 * @param flag true if the labels should be drawn
	 */
	public void setPaintLabels(boolean flag) {
		this.slider.setPaintLabels(flag);
	}
	
	/**
	 * Returns true if the ticks should be shown.
	 * @return boolean
	 */
	public boolean getPaintTicks() {
		return this.slider.getPaintTicks();
	}
	
	/**
	 * Sets the paint ticks property of the slider.
	 * @param flag true if the ticks should be drawn
	 */
	public void setPaintTicks(boolean flag) {
		this.slider.setPaintTicks(flag);
	}
	
	/**
	 * Returns true if the track should be shown.
	 * @return boolean
	 */
	public boolean getPaintTrack() {
		return this.slider.getPaintTrack();
	}
	
	/**
	 * Sets the paint track property of the slider.
	 * @param flag true if the track should be drawn
	 */
	public void setPaintTrack(boolean flag) {
		this.slider.setPaintTrack(flag);
	}
	
	/**
	 * Returns true if the slider should snap to tick values.
	 * @return boolean
	 */
	public boolean getSnapToTicks() {
		return this.slider.getSnapToTicks();
	}
	
	/**
	 * Sets the snap to ticks property of the slider.
	 * @param flag true if the slider should snap to ticks
	 */
	public void setSnapToTicks(boolean flag) {
		this.slider.setSnapToTicks(flag);
	}
}
