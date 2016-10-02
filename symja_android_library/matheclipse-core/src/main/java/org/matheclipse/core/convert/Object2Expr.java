package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Converts objects into an IExpr expression
 * 
 */
public class Object2Expr {

	/**
	 * Converts the following J<va objects into an IExpr expression
	 * 
	 * <pre>
	 * Java Object     -&gt; MathEclipse object
	 * -------------------------------------
	 * null object          Null symbol
	 * IExpr                IExpr type
	 * Boolean              True or False symbol
	 * BigInteger           Integer value  
	 * java.math.BigInteger Integer value  
	 * BigDecimal           Double with doubleValue() value
	 * Double               Double with doubleValue() value
	 * Float                Double with doubleValue() value
	 * Number               Integer with longValue() value
	 * java.util.List       0-th element of the list gives the head of the function 
	 *                      1..nth element of the list give the arguments of the function
	 * Object[]             a list of converted objects  
	 * int[]                a list of <code>IntegerSym</code>Integer values
	 * double[]             a list of <code>Num</code> values
	 * double[][]           a matrix (i.e. nested lists) of Double values
	 * Complex[]            a list of <code>ComplexNum</code> values
	 * boolean[]            a list of True or False symbols
	 * 
	 * </pre>
	 * 
	 */
	public static IExpr convert(Object obj) throws ConversionException {
		if (obj == null) {
			return F.Null;
		}
		if (obj instanceof IExpr) {
			return (IExpr) obj;
		}
		if (obj instanceof Boolean) {
			if (((Boolean) obj).booleanValue()) {
				return F.True;
			}
			return F.False;
		}
		if (obj instanceof BigInteger) {
			return F.integer((BigInteger) obj);
		}
		if (obj instanceof java.math.BigInteger) {
			return F.integer((java.math.BigInteger) obj);
		}
		if (obj instanceof Number) {
			if (obj instanceof BigDecimal) {
				return F.num(((BigDecimal) obj).doubleValue());
			}

			if (obj instanceof Double) {
				return F.num(((Double) obj).doubleValue());
			}
			if (obj instanceof Float) {
				return F.num(((Float) obj).doubleValue());
			}
			return F.integer(((Number) obj).longValue());
		}
		if (obj instanceof java.util.List) {
			final java.util.List<?> lst = (java.util.List<?>) obj;
			IAST list = F.NIL;
			if (lst.size() == 0) {
				list = List();
			} else {
				final ISymbol head = F.$s(lst.get(0).toString());
				int size = lst.size();
				list = F.ast(head, size, false);

				for (int i = 1; i < size; i++) {
					list.append(convert(lst.get(i)));
				}
			}
			return list;
		}
		if (obj instanceof Object[]) {
			final Object[] array = (Object[]) obj;
			final IAST list = F.ListC(array.length);
			for (int i = 0; i < array.length; i++) {
				list.append(convert(array[i]));
			}
			return list;
		}
		if (obj instanceof int[]) {
			return AST.newInstance(F.List, (int[]) obj);
		}
		if (obj instanceof double[]) {
			return AST.newInstance(F.List, (double[]) obj);
		}
		if (obj instanceof double[][]) {
			final double[][] dd = (double[][]) obj;
			final IAST list = F.ListC(dd.length);
			for (int i = 0; i < dd.length; i++) {
				final IAST row = F.ListC(dd[i].length);
				for (int j = 0; j < dd[i].length; j++) {
					row.append(F.num(dd[i][j]));
				}
				list.append(row);
			}
			return list;
		}
		if (obj instanceof org.apache.commons.math3.complex.Complex[]) {
			return AST.newInstance(F.List, (org.apache.commons.math3.complex.Complex[]) obj);
		}
		if (obj instanceof boolean[]) {
			final boolean[] array = (boolean[]) obj;
			final IAST list = F.ListC(array.length);
			for (int i = 0; i < array.length; i++) {
				if (array[i]) {
					list.append(F.True);
				} else {
					list.append(F.False);
				}
			}
			return list;
		}
		return F.$str(obj.toString());
	}

	public static IAST convertComplex(org.apache.commons.math3.complex.Complex[] array) throws ConversionException {
		return AST.newInstance(F.List, array);
	}
}
