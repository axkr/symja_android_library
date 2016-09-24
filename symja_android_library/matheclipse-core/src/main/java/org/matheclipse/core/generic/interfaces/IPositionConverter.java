package org.matheclipse.core.generic.interfaces;

public interface IPositionConverter<T> {
	/**
	 * Convert the integer position number >= 0 into an object
	 *
	 * @param position
	 *            which should be converted to an object
	 * @return
	 */
	T toObject(int position);

	/**
	 * Convert the object into an integer number >= 0
	 *
	 * @param position
	 *            the object which should be converted
	 * @return -1 if the conversion is not possible
	 */
	int toInt(T position);
}
