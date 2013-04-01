package org.matheclipse.generic.util;

/**
 * 
 * 
 */
public class IntegerElement implements IElement, Comparable<IntegerElement> {
	/* package private */final Integer fValue;

	public IntegerElement() {
		this(0);
	}

	public IntegerElement(int number) {
		fValue = Integer.valueOf(number);
	}

	public Integer getValue() {
		return fValue;
	}

	public byte byteValue() {
		return fValue.byteValue();
	}

	public int compareTo(IntegerElement anotherInteger) {
		return fValue.compareTo(anotherInteger.fValue);
	}

	public double doubleValue() {
		return fValue.doubleValue();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntegerElement) {
			return fValue.equals(((IntegerElement) obj).fValue);
		}
		return false;
	}

	public float floatValue() {
		return fValue.floatValue();
	}

	public int hashCode() {
		return fValue.hashCode();
	}

	public int intValue() {
		return fValue.intValue();
	}

	public long longValue() {
		return fValue.longValue();
	}

	public short shortValue() {
		return fValue.shortValue();
	}

	public String toString() {
		return fValue.toString();
	}

}
