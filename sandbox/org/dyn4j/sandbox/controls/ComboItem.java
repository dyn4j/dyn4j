package org.dyn4j.sandbox.controls;

/**
 * Class used by a JComboBox to store an object with a specific name.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ComboItem {
	/** The name of the item */
	private String name;
	
	/** The value of the item */
	private Object value;
	
	/**
	 * Full constructor.
	 * @param name the name of the item
	 * @param value the value of the item
	 */
	public ComboItem(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name of the item.
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the value of the item.
	 * @return Object
	 */
	public Object getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
