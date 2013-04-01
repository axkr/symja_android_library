package org.matheclipse.generic.interfaces;

public interface IPositionConverter<T> {
	/**
	 * Convert the integer position number >= 0 into an object
	 *
	 */
	T toObject(int position);

	/**
	 * Convert the object into an integer number >= 0
	 *
	 * @param position the object which should be converted
	 * @return -1 if the conversion is not pssible
	 */
	int toInt(T position);
}
