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
	public static int MAX_POLYNOMIAL = 4;

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
		if (dim==null){
			return new double[0][0];
		}
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

	public static double[] toPolynomial(IExpr expr, ISymbol sym) {
		double[] array = new double[MAX_POLYNOMIAL];
		Map<Integer, Double> map = toPolynomialMap(expr, sym);
		if (map == null) {
			return null;
		}
		int k;
		for (Map.Entry<Integer, Double> entry : map.entrySet()) {
			k = entry.getKey();
			if (k <= 4) {
				array[k] = entry.getValue();
			} else {
				return null;
			}
		}
		return array;
	}

	/**
	 * 
	 * @param expr
	 * @param sym
	 * @return <code>null</code> if the expression couldn't be converted to a
	 *         polynomial.
	 */
	public static Map<Integer, Double> toPolynomialMap(IExpr expr, ISymbol sym) {
		try {
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			if (expr.isPlus()) {
				IAST plus = (IAST) expr;
				for (int i = 1; i < plus.size(); i++) {
					if (plus.get(i).isTimes()) {
						IAST times = (IAST) plus.get(i);
						double coeff = 0;
						int exp = -1;
						for (int j = 1; j < times.size(); j++) {
							if (times.get(j).isPower()) {
								IAST power = (IAST) times.get(j);
								if (power.get(1).equals(sym)) {
									if (exp != (-1)) {
										return null;
									}
									IExpr res = F.evaln(power.arg2());
									if (!(res instanceof INum)) {
										return null;
									}
									exp = DoubleMath.roundToInt(((INum) res).doubleValue(), RoundingMode.UNNECESSARY);
									if (exp < 0) {
										return null;
									}
									continue;
								}
							} else if (times.get(j).isSymbol()) {
								if (times.get(j).equals(sym)) {
									if (exp != (-1)) {
										return null;
									}
									exp = 1;
									continue;
								}
							}
							if (times.get(j) instanceof INum) {
								coeff += ((INum) times.get(j)).doubleValue();
								continue;
							}
							IExpr res = F.evaln(times.get(j));
							if (!(res instanceof INum)) {
								return null;
							}
							coeff += ((INum) res).doubleValue();
						}
						if (exp == (-1)) {
							exp = 0;
						}
						addCoefficient(map, coeff, exp);
						continue;
					} else if (plus.get(i).isPower()) {
						IAST power = (IAST) plus.get(i);
						if (power.get(1).equals(sym)) {
							IExpr res = F.evaln(power.arg2());
							if (!(res instanceof INum)) {
								return null;
							}
							int exp = DoubleMath.roundToInt(((INum) res).doubleValue(), RoundingMode.UNNECESSARY);
							if (exp < 0) {
								return null;
							}
							addCoefficient(map, 1.0, exp);
							continue;
						}
						return null;
					} else if (plus.get(i).isSymbol()) {
						if (plus.equalsAt(i, sym)) {
							addCoefficient(map, 1.0, 1);
							continue;
						}
						return null;
					}
					if (plus.get(i) instanceof INum) {
						addCoefficient(map, ((INum) plus.get(i)).doubleValue(), 0);
						continue;
					}
					IExpr res = F.evaln(plus.get(i));
					if (!(res instanceof INum)) {
						return null;
					}
					addCoefficient(map, ((INum) res).doubleValue(), 0);
				}
				return map;
			}
		} catch (Exception ex) {

		}
		return null;
	}

	private static void addCoefficient(Map<Integer, Double> map, double v, int k) {
		Integer key = Integer.valueOf(k);
		Double value = map.get(key);
		if (value == null) {
			map.put(Integer.valueOf(k), Double.valueOf(v));
		} else {
			map.put(Integer.valueOf(k), Double.valueOf(value.doubleValue() + v));
		}
	}
}
