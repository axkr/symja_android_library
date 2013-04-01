package org.matheclipse.core.interfaces;

/**
 * 
 * @deprecated 
 * @see ISymbol#FLAT, ISymbol#ORDERLESS,...
 */
public enum Attributes {

	/**
	 * ISymbol attribute which means that no attribute is set
	 */
	NOATTRIBUTE(0x0000),

	/**
	 * ISymbol attribute to indicate that a symbol or function is constant
	 */
	CONSTANT(0x0002),

	/**
	 * ISymbol attribute for an associative function transformation The evaluation
	 * of the function will flatten the arguments list
	 *
	 */
	FLAT(0x0008),

	/**
	 * ISymbol attribute for a function, where the first argument should not be
	 * evaluated
	 *
	 */
	HOLDFIRST(0x0020),

	/**
	 * ISymbol attribute for a function, where only the first argument should be
	 * evaluated
	 *
	 */
	HOLDREST(0x0040),

	/**
	 * ISymbol attribute for a function, where no argument should be evaluated
	 *
	 */
	HOLDALL(0x0020 | 0x0040),

	/**
	 * ISymbol attribute for a function with lists as arguments
	 *
	 */
	LISTABLE(0x0080),

	/**
	 * ISymbol attribute for a function, which should not be evaluated numerically
	 *
	 */
	NHOLDALL(0x2000),

	/**
	 * ISymbol attribute for a function, where the first argument should not be
	 * evaluated numerically
	 *
	 */
	NHOLDFIRST(0x4000),

	/**
	 * ISymbol attribute for a function, where the rest of the arguments should
	 * not be evaluated numerically
	 *
	 */
	NHOLDREST(0x8000),

	/**
	 * Description of the Field
	 */
	NUMERICFUNCTION(0x0400),

	/**
	 * ISymbol attribute for a function transformation: f(x) ==> x
	 */
	ONEIDENTITY(0x0001),

	/**
	 * ISymbol attribute for a commutative function transformation The evaluation
	 * of the function will sort the arguments
	 *
	 */
	ORDERLESS(0x0004),

	/**
	 * ISymbol attribute combination (ISymbol.FLAT and ISymbol.ORDERLESS)
	 *
	 */
	FLATORDERLESS(0x0008 | 0x0004);

	private final int fValue;

	Attributes(final int value) {
		fValue = value;
	}
}
