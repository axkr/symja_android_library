package org.matheclipse.generic.util;


/**
 * An implementation for <code>CharSequence</code>s
 * 
 * @param <S>
 */
public class CharSequenceElement<S extends CharSequence>{

	protected S fString;

	public CharSequenceElement(final S sequ) {
		fString = sequ;
	}

	public char charAt(final int index) {
		return fString.charAt(index);
	}

	public int length() {
		return fString.length();
	}

	public CharSequence subSequence(final int start, final int end) {
		return fString.subSequence(start, end);
	}

	@Override
	public String toString() {
		return fString.toString();
	}

}
