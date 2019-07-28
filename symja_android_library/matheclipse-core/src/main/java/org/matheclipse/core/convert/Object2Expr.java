package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apfloat.Apfloat;
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
	 * BigInteger           Symja Integer value  
	 * BigDecimal           Symja <code>Num</code> with doubleValue() value
	 * Double               Symja <code>Num</code> with doubleValue() value
	 * Float                Symja <code>Num</code> with doubleValue() value
	 * Integer              Symja Integer with longValue() value
	 * Long                 Symja Integer with longValue() value
	 * Number               Symja <code>Num</code> with doubleValue() value
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
			return convert((Number) obj);
		}
		if (obj instanceof String) {
			final ExprParser parser = new ExprParser(EvalEngine.get());
			return parser.parse((String) obj);
		}
		if (obj instanceof java.util.Collection) {
			return convertList((java.util.Collection<?>) obj);
		}
		if (obj instanceof org.hipparchus.complex.Complex) {
			org.hipparchus.complex.Complex cmp = (org.hipparchus.complex.Complex) obj;
			return F.complexNum(cmp.getReal(), cmp.getImaginary());
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
		if (obj instanceof Object[]) {
			final Object[] array = (Object[]) obj;
			int length = array.length;
			final IASTAppendable list = F.ListAlloc(length);
			return list.appendArgs(0, length, i -> convert(array[i]));
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

	private static IExpr convert(Number n) {
		if (n instanceof Integer) {
			return F.ZZ(((Integer) n).longValue());
		}
		if (n instanceof Double) {
			return F.num(((Double) n).doubleValue());
		}
		if (n instanceof Long) {
			return F.ZZ(((Long) n).longValue());
		}
		if (n instanceof BigInteger) {
			return F.integer((BigInteger) n);
		}
		if (n instanceof BigDecimal) {
			return F.num(new Apfloat(((BigDecimal) n).doubleValue()));
		}
		if (n instanceof Float) {
			return F.num(((Float) n).doubleValue());
		}
		if (n instanceof AtomicDouble) {
			return F.num(((AtomicDouble) n).doubleValue());
		}
		if (n instanceof AtomicInteger) {
			return F.ZZ(((AtomicInteger) n).longValue());
		}
		if (n instanceof AtomicLong) {
			return F.ZZ(((AtomicLong) n).longValue());
		}
		return F.num(((Number) n).doubleValue());
	}

	public static IExpr convertList(java.util.Collection<?> lst) {
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

	public static IAST convertComplex(boolean evalComplex, org.hipparchus.complex.Complex[] array)
			throws ConversionException {
		return AST.newInstance(F.List, evalComplex, array);
	}
}
