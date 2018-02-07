package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * Converts objects into an IExpr expression
 */
public class Object2Expr {

	/**
	 * Converts the following Java objects into an IExpr expression
	 * 
	 * <pre>
	 * Java Object       -&gt; Symja object
	 * -------------------------------------
	 * null object          F.Null symbol
	 * IExpr                IExpr type
	 * Boolean              True or False symbol
	 * BigInteger           Integer value  
	 * BigDecimal           <code>Num</code> with doubleValue() value
	 * Double               <code>Num</code> with doubleValue() value
	 * Float                <code>Num</code> with doubleValue() value
	 * Number               Integer with longValue() value
	 * java.util.Collection list of elements 
	 *                      1..nth element of the list give the elements of the List()
	 * Object[]             a list of converted objects  
	 * int[]                a list of <code>IntegerSym</code> integer values
	 * double[]             a vector ASTRealVector of <code>double</code> values
	 * double[][]           a matrix ASTRealMatrix of <code>double</code> values
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
			return ((Boolean) obj).booleanValue() ? F.True : F.False;
		}
		if (obj instanceof Number) {
			if (obj instanceof BigInteger) {
				return F.integer((BigInteger) obj);
			}
			if (obj instanceof BigDecimal) {
				return F.num(((BigDecimal) obj).doubleValue());
			}
			if (obj instanceof Double) {
				return F.num(((Double) obj).doubleValue());
			}
			if (obj instanceof AtomicDouble) {
				return F.num(((AtomicDouble) obj).doubleValue());
			}
			if (obj instanceof Float) {
				return F.num(((Float) obj).doubleValue());
			}
			return F.integer(((Number) obj).longValue());
		}
		if (obj instanceof String) {
			final ExprParser parser = new ExprParser(EvalEngine.get());
			return parser.parse((String) obj);
		}
		if (obj instanceof java.util.Collection) {
			final java.util.Collection<?> lst = (java.util.Collection<?>) obj;
			if (lst.size() == 0) {
				return List();
			} else {
				int size = lst.size();
				IASTAppendable list = F.ast(F.List, size, false);
				for (Object element : lst) {
					list.append(convert(element));
				}
				return list;
			}
		}
		if (obj instanceof org.hipparchus.complex.Complex) {
			org.hipparchus.complex.Complex cmp = (org.hipparchus.complex.Complex) obj;
			return F.complexNum(cmp.getReal(), cmp.getImaginary());
		}
		if (obj instanceof Object[]) {
			final Object[] array = (Object[]) obj;
			int length = array.length;
			final IASTAppendable list = F.ListAlloc(length);
			return list.appendArgs(0, length, i -> convert(array[i]));
		}
		if (obj instanceof int[]) {
			return AST.newInstance(F.List, (int[]) obj);
		}
		if (obj instanceof double[]) {
			return new ASTRealVector((double[]) obj, true);
			// return AST.newInstance(F.List, (double[]) obj);
		}
		if (obj instanceof double[][]) {
			return new ASTRealMatrix((double[][]) obj, true);
			// final IASTAppendable list = F.ListAlloc(dd.length);
			// for (int i = 0; i < dd.length; i++) {
			// final IASTAppendable row = F.ListAlloc(dd[i].length);
			// for (int j = 0; j < dd[i].length; j++) {
			// row.append(F.num(dd[i][j]));
			// }
			// list.append(row);
			// }
			// return list;
		}
		if (obj instanceof org.hipparchus.complex.Complex[]) {
			return AST.newInstance(F.List, false, (org.hipparchus.complex.Complex[]) obj);
		}
		if (obj instanceof boolean[]) {
			final boolean[] array = (boolean[]) obj;
			final IASTAppendable list = F.ListAlloc(array.length);
			for (int i = 0; i < array.length; i++) {
				list.append(array[i] ? F.True : F.False);
			}
			return list;
		}
		return F.$str(obj.toString());
	}

	public static IAST convertComplex(boolean evalComplex, org.hipparchus.complex.Complex[] array)
			throws ConversionException {
		return AST.newInstance(F.List, evalComplex, array);
	}
}
