package org.dyn4j;

public class Tuple<E, T> {
	public final E x;
	public final T y;
	public Tuple(E x, T y) {
		if (x == null) throw new NullPointerException();
		if (y == null) throw new NullPointerException();
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Tuple) {
			Tuple<?, ?> o = (Tuple<?, ?>)obj;
			return x.equals(o.x) && y.equals(o.y);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 23 + this.x.hashCode();
		hash = hash * 23 + this.y.hashCode();
		return hash;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(this.x).append(",").append(this.y).append(")");
		return sb.toString();
	}
}
