package org.matheclipse.generic.util;

public class MutableInteger {
	int fValue;

	public MutableInteger(int i) {
		fValue = i;
	}

	public int intValue() {
		return fValue;
	}

	/**
	 * Increment the value
	 * 
	 * @return the incremented value
	 */
	public int inc() {
		return ++fValue;
	}

	/**
	 * Decrement the value
	 * 
	 * @return the decremented value
	 */
	public int dec() {
		return --fValue;
	}

	/**
	 * Returns a hash code for this <code>Integer</code>.
	 * 
	 * @return a hash code value for this object, equal to the primitive
	 *         <code>int</code> value represented by this <code>Integer</code>
	 *         object.
	 */
	public int hashCode() {
		return fValue;
	}

	/**
	 * Compares this object to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>
	 * and is an <code>MutuableInteger</code> object that contains the same
	 * <code>int</code> value as this object.
	 * 
	 * @param obj
	 *          the object to compare with.
	 * @return <code>true</code> if the objects are the same; <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof MutableInteger) {
			return fValue == ((MutableInteger) obj).intValue();
		}
		return false;
	}

	/**
	 * Compares two <code>MutuableInteger</code> objects numerically.
	 * 
	 * @param anotherInteger
	 *          the <code>MutuableInteger</code> to be compared.
	 * @return the value <code>0</code> if this <code>MutuableInteger</code> is equal
	 *         to the argument <code>MutuableInteger</code>; a value less than
	 *         <code>0</code> if this <code>MutuableInteger</code> is numerically less
	 *         than the argument <code>MutuableInteger</code>; and a value greater than
	 *         <code>0</code> if this <code>MutuableInteger</code> is numerically
	 *         greater than the argument <code>MutuableInteger</code> (signed
	 *         comparison).
	 */
	public int compareTo(MutableInteger anotherInteger) {
		int thisVal = this.fValue;
		int anotherVal = anotherInteger.fValue;
		return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
	}
}
