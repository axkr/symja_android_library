package org.matheclipse.core.convert;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.math.DoubleMath;

public class Expr2Object {
	public static double[] toDoubleVector(IAST ast) throws WrongArgumentType {
		double[] result = new double[ast.size() - 1];
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isSignedNumber()) {
				result[i - 1] = ((ISignedNumber) ast.get(i)).doubleValue();
			} else {
				throw new WrongArgumentType(ast, ast.get(i), i, "Conversion into a vector of double values not possible!");
			}
		}
		return result;
	}

	public static double[][] toDoubleMatrix(IAST ast) throws WrongArgumentType {
		int[] dim = ast.isMatrix();
		double[][] result = new double[dim[0]][dim[1]];
		for (int i = 1; i <= dim[0]; i++) {
			IAST row = (IAST) ast.get(i);
			for (int j = 1; i <= dim[1]; j++) {
				if (row.get(j).isSignedNumber()) {
					result[i - 1][j - 1] = ((ISignedNumber) row.get(j)).doubleValue();
				} else {
					throw new WrongArgumentType(ast, ast.get(i), i, "Conversion into a matrix of double values not possible!");
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param expr
	 * @param sym
	 * @return <code>null</code> if the expression couldn't be converted to a
	 *         polynomial.
	 */
	public static Map<Integer, Double> toPolynomialFunction(IExpr expr, ISymbol sym) {
		try {
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			if (expr.isPlus()) {
				IAST plus = (IAST) expr;
				for (int i = 1; i < plus.size(); i++) {
					if (plus.get(i).isTimes()) {
						IAST times = (IAST) plus.get(i);
						for (int j = 1; j < times.size(); j++) {

						}
					} else if (plus.get(i).isPower()) {
						IAST power = (IAST) plus.get(i);
						if (power.get(1).equals(sym)) {
							IExpr res = F.evaln(power.get(2));
							if (!(res instanceof INum)) {
								return null;
							}
							int exp = DoubleMath.roundToInt(((INum) res).doubleValue(), RoundingMode.UNNECESSARY);
							map.put(Integer.valueOf(exp), Double.valueOf(1.0));
							continue;
						}
						return null;
					} else if (plus.get(i).isSymbol()) {
						if (plus.get(i).equals(sym)) {
							map.put(Integer.valueOf(1), Double.valueOf(1.0));
							continue;
						}
						return null;
					}
					IExpr res = F.evaln(plus.get(i));
					if (!(res instanceof INum)) {
						return null;
					}
					map.put(Integer.valueOf(0), Double.valueOf(((INum) res).doubleValue()));
				}
				return map;
			}
		} catch (Exception ex) {

		}
		return null;
	}
}
