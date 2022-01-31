package org.matheclipse.core.convert;

import java.math.RoundingMode;
import org.hipparchus.util.OpenIntToDoubleHashMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import com.google.common.math.DoubleMath;

public class Expr2Object {

  public static double[] toPolynomial(IExpr expr, IExpr sym) {
    OpenIntToDoubleHashMap map = toPolynomialMap(expr, sym);
    if (map == null) {
      return null;
    }
    int max = 5;
    int k;

    for (OpenIntToDoubleHashMap.Iterator iterator = map.iterator(); iterator.hasNext();) {
      iterator.advance();
      k = iterator.key();
      if (k > max) {
        max = k;
      }
    }
    double[] array = new double[max + 1];
    for (OpenIntToDoubleHashMap.Iterator iterator = map.iterator(); iterator.hasNext();) {
      iterator.advance();
      k = iterator.key();
      // if (k <= 4) {
      array[k] = iterator.value();
      // } else {
      // return null;
      // }
    }

    int n = array.length;
    while ((n > 1) && (array[n - 1] == 0.0)) {
      --n;
    }
    if (n < array.length) {
      double[] coefficients = new double[n];
      System.arraycopy(array, 0, coefficients, 0, n);
      return coefficients;
    }

    return array;
  }

  /**
   * @param expr
   * @param variable
   * @return <code>null</code> if the expression couldn't be converted to a polynomial.
   */
  private static OpenIntToDoubleHashMap toPolynomialMap(IExpr expr, IExpr variable) {
    try {
      OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
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
                if (power.base().equals(variable)) {
                  if (exp != (-1)) {
                    return null;
                  }
                  IExpr res = F.evaln(power.exponent());
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
                if (times.get(j).equals(variable)) {
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
            if (power.arg1().equals(variable)) {
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
            if (plus.equalsAt(i, variable)) {
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
    } catch (RuntimeException ex) {
      // roundToInt() throws ArithmeticException
    }
    return null;
  }

  private static void addCoefficient(OpenIntToDoubleHashMap map, double v, int k) {
    double value = map.get(k);
    if (Double.isNaN(value)) {
      map.put(k, v);
    } else {
      map.put(k, value + v);
    }
  }
}
