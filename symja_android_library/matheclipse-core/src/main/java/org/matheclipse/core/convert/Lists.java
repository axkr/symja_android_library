package org.matheclipse.core.convert;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

public class Lists {
	/**
	 * Returns the size of a array, returning zero if the array is <code>null</code>.
	 * 
	 * @param p_array
	 *            the array for which to return the size.
	 * @return the size of the array, or <code>0</code> if the array is <code>null</code>.
	 */
	public static <T> int sizeOf(T... p_array) {
		if (p_array == null) {
			return 0;
		}
		return p_array.length;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of boolean
	 * {@link org.matheclipse.core.interfaces.ISymbol}s from an array of <code>boolean</code>s.
	 * 
	 * @param p_booleans
	 *            the array of <code>boolean</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@org.matheclipse.core.interfaces.ISymbol
	 * 
	 * 		} objects.
	 */
	public static IAST asAST(boolean... p_booleans) {
		if (p_booleans != null) {
			IASTAppendable ast = F.ListAlloc(p_booleans.length);
			for (int i = 0, t = p_booleans.length; i < t; i++) {
				if (p_booleans[i]) {
					ast.append(F.True);
				} else {
					ast.append(F.False);
				}
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.INum}s from an
	 * array of <code>double</code>s.
	 * 
	 * @param p_doubles
	 *            the array of <code>double</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.INum} objects.
	 */
	public static IAST asList(double... p_doubles) {

		if (p_doubles != null) {
			IASTAppendable ast = F.ListAlloc(p_doubles.length);
			for (int i = 0, t = p_doubles.length; i < t; i++) {
				ast.append(F.num(p_doubles[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.INum}s from an
	 * array of <code>float</code>s.
	 * 
	 * @param p_doubles
	 *            the array of <code>float</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.INum} objects.
	 */
	public static IAST asList(float... p_doubles) {
		if (p_doubles != null) {
			IASTAppendable ast = F.ListAlloc(p_doubles.length);
			for (int i = 0, t = p_doubles.length; i < t; i++) {
				ast.append(F.num(p_doubles[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IStringX}s from
	 * an array of <code>String</code>s.
	 * 
	 * @param p_strings
	 *            the array of <code>String</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IStringX}
	 *         objects.
	 */
	public static IAST asList(String... p_strings) {
		
		if (p_strings != null) {
			IASTAppendable ast = F.ListAlloc(p_strings.length);
			for (int i = 0, t = p_strings.length; i < t; i++) {
				ast.append(F.$str(p_strings[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.ISymbol}s from
	 * an array of <code>String</code>s.
	 * 
	 * @param p_symbols
	 *            the array of <code>String</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.ISymbol}
	 *         objects.
	 */
	public static IAST asListSymbols(String... p_symbols) {
		if (p_symbols != null) {
			IASTAppendable ast = F.ListAlloc(p_symbols.length);
			for (int i = 0, t = p_symbols.length; i < t; i++) {

				ast.append(F.symbol(p_symbols[i]));
			}
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}s from
	 * an array of <code>short</code>s.
	 * 
	 * @param p_shorts
	 *            the array of <code>short</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}
	 *         objects.
	 */
	public static IAST asList(short... p_shorts) {
		if (p_shorts != null) {
			IASTAppendable ast = F.ListAlloc(p_shorts.length);
			for (int i = 0, t = p_shorts.length; i < t; i++) {
				ast.append(F.integer(p_shorts[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}s from
	 * an array of <code>int</code>s.
	 * 
	 * @param p_ints
	 *            the array of <code>int</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}
	 *         objects.
	 */
	public static IAST asList(int... p_ints) {
		if (p_ints != null) {
			IASTAppendable ast = F.ListAlloc(p_ints.length);
			for (int i = 0, t = p_ints.length; i < t; i++) {
				ast.append(F.integer(p_ints[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}s from
	 * an array of <code>long</code>s.
	 * 
	 * @param p_longs
	 *            the array of <code>long</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IInteger}
	 *         objects.
	 */
	public static IAST asList(long... p_longs) {
		if (p_longs != null) {
			IASTAppendable ast = F.ListAlloc(p_longs.length);
			for (int i = 0, t = p_longs.length; i < t; i++) {
				ast.append(F.integer(p_longs[i]));
			}
			return ast;
		}
		return F.List();
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IExpr}s from an
	 * array of <code>Object</code>s.
	 * 
	 * @param p_objects
	 *            the array of <code>Object</code> values to be converted to a
	 *            {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of {@link org.matheclipse.core.interfaces.IExpr} objects.
	 */
	public static IAST asList(Object... p_objects) {
		if (p_objects != null) {
			IASTAppendable ast = F.ListAlloc(p_objects.length);
			for (int i = 0, t = p_objects.length; i < t; i++) {
				ast.append(Object2Expr.convert(p_objects[i]));
			}
			return ast;
		}
		return F.List();
	}
}
