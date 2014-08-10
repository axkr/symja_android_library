package org.matheclipse.core.convert;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

public class Lists {
	/**
	 * Returns the size of a array, returning zero if the array is
	 * <code>null</code>.
	 * 
	 * @param p_array
	 *          the array for which to return the size.
	 * @return the size of the array, or <code>0</code> if the array is
	 *         <code>null</code>.
	 */
	public static <T> int sizeOf(T... p_array) {
		if (p_array == null) {
			return 0;
		}
		return p_array.length;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of boolean
	 * {@link org.matheclipse.core.interfaces.ISymbol}s from an array of
	 * <code>boolean</code>s.
	 * 
	 * @param p_booleans
	 *          the array of <code>boolean</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *                 {@org.matheclipse.core.interfaces.ISymbol
	 * 
	 * } objects.
	 */
	public static IAST asAST(boolean... p_booleans) {
		IAST ast = F.ast(F.List);
		if (p_booleans != null) {
			for (int i = 0, t = p_booleans.length; i < t; i++) {
				if (p_booleans[i]) {
					ast.add(F.True);
				} else {
					ast.add(F.False);
				}
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.INum}s from an array of
	 * <code>double</code>s.
	 * 
	 * @param p_doubles
	 *          the array of <code>double</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.INum} objects.
	 */
	public static IAST asList(double... p_doubles) {
		IAST ast = F.ast(F.List);
		if (p_doubles != null) {
			for (int i = 0, t = p_doubles.length; i < t; i++) {
				ast.add(F.num(p_doubles[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.INum}s from an array of
	 * <code>float</code>s.
	 * 
	 * @param p_doubles
	 *          the array of <code>float</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.INum} objects.
	 */
	public static IAST asList(float... p_doubles) {
		IAST ast = F.ast(F.List);
		if (p_doubles != null) {
			for (int i = 0, t = p_doubles.length; i < t; i++) {
				ast.add(F.num(p_doubles[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.IStringX}s from an array of
	 * <code>String</code>s.
	 * 
	 * @param p_strings
	 *          the array of <code>String</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.IStringX} objects.
	 */
	public static IAST asList(String... p_strings) {
		IAST ast = F.ast(F.List);
		if (p_strings != null) {
			for (int i = 0, t = p_strings.length; i < t; i++) {
				ast.add(F.stringx(p_strings[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.ISymbol}s from an array of
	 * <code>String</code>s.
	 * 
	 * @param p_symbols
	 *          the array of <code>String</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.ISymbol} objects.
	 */
	public static IAST asListSymbols(String... p_symbols) {
		IAST ast = F.ast(F.List);
		if (p_symbols != null) {
			for (int i = 0, t = p_symbols.length; i < t; i++) {

				ast.add(F.$s(p_symbols[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.IInteger}s from an array of
	 * <code>short</code>s.
	 * 
	 * @param p_shorts
	 *          the array of <code>short</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.IInteger} objects.
	 */
	public static IAST asList(short... p_shorts) {
		IAST ast = F.ast(F.List);
		if (p_shorts != null) {
			for (int i = 0, t = p_shorts.length; i < t; i++) {
				ast.add(F.integer(p_shorts[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.IInteger}s from an array of
	 * <code>int</code>s.
	 * 
	 * @param p_ints
	 *          the array of <code>int</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.IInteger} objects.
	 */
	public static IAST asList(int... p_ints) {
		IAST ast = F.ast(F.List);
		if (p_ints != null) {
			for (int i = 0, t = p_ints.length; i < t; i++) {
				ast.add(F.integer(p_ints[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.IInteger}s from an array of
	 * <code>long</code>s.
	 * 
	 * @param p_longs
	 *          the array of <code>long</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.IInteger} objects.
	 */
	public static IAST asList(long... p_longs) {
		IAST ast = F.ast(F.List);
		if (p_longs != null) {
			for (int i = 0, t = p_longs.length; i < t; i++) {
				ast.add(F.integer(p_longs[i]));
			}
		}
		return ast;
	}

	/**
	 * Returns a {@link org.matheclipse.core.interfaces.IAST} of
	 * {@link org.matheclipse.core.interfaces.IExpr}s from an array of
	 * <code>Object</code>s.
	 * 
	 * @param p_objects
	 *          the array of <code>Object</code> values to be converted to a
	 *          {@link org.matheclipse.core.interfaces.IAST}.
	 * 
	 * @return a {@link org.matheclipse.core.interfaces.IAST} of
	 *         {@link org.matheclipse.core.interfaces.IExpr} objects.
	 */
	public static IAST asList(Object... p_objects) {
		IAST ast = F.ast(F.List);
		if (p_objects != null) {
			for (int i = 0, t = p_objects.length; i < t; i++) {
				ast.add(Object2Expr.CONST.convert(p_objects[i]));
			}
		}
		return ast;
	}
}
