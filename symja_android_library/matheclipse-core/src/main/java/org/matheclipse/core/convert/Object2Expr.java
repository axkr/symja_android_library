package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.JavaObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.parser.ExprParser;

import com.google.common.util.concurrent.AtomicDouble;

/** Converts objects into an IExpr expression */
public class Object2Expr {

  /**
   * Converts the following Java objects into an IExpr expression
   *
   * <pre>
   * Java Object       -&gt; Symja object
   * -------------------------------------
   * null object          {@link S#Null} symbol
   * IExpr                {@link IExpr} type
   * Boolean              {@link S#True} or {@link S#False} symbol
   * BigInteger           {@link IInteger} value
   * BigDecimal           {@link INum} with {@link Apfloat#Apfloat(java.math.BigDecimal)} value
   * Double               {@link INum}  with doubleValue() value
   * Float                {@link INum}  with doubleValue() value
   * Integer              {@link IInteger} with intValue() value
   * Long                 {@link IInteger} with longValue() value
   * Number               {@link INum} with doubleValue() value
   * java.util.Collection list of elements
   *                      1..nth element of the list give the elements of the List()
   * Object[]             a list of converted objects
   * int[]                a list of {@link IInteger} integer values
   * double[]             a vector ASTRealVector of <code>double</code> values
   * double[][]           a matrix ASTRealMatrix of <code>double</code> values
   * Complex[]            a list of {@link IComplexNum} values
   * boolean[]            a list of {@link S#True} or {@link S#False} symbols
   *
   * </pre>
   *
   * @param parseString if <code>true</code> and <code>obj instanceof String</code> parse the string
   *     as a Symja expression
   * @param javaObject if <code>true</code> return a wrapper instanceof {@link JavaObjectExpr} if no
   *     other conversion was found
   */
  public static IExpr convert(Object obj, boolean parseString, boolean javaObject) {
    if (obj == null) {
      return S.Null;
    }
    if (obj instanceof IExpr) {
      return (IExpr) obj;
    }
    if (obj instanceof String) {
      if (parseString) {
        final ExprParser parser = new ExprParser(EvalEngine.get());
        return parser.parse((String) obj);
      } else {
        return F.stringx((String) obj);
      }
    }
    if (obj instanceof Boolean) {
      return ((Boolean) obj).booleanValue() ? S.True : S.False;
    }
    if (obj instanceof Number) {
      if (obj instanceof org.hipparchus.fraction.Fraction) {
        org.hipparchus.fraction.Fraction frac = (org.hipparchus.fraction.Fraction) obj;
        return F.fraction(frac.getNumerator(), frac.getDenominator());
      }
      return convert((Number) obj);
    }
    if (obj instanceof java.util.Collection) {
      return convertList((java.util.Collection<?>) obj, parseString, javaObject);
    }
    if (obj instanceof org.hipparchus.complex.Complex) {
      org.hipparchus.complex.Complex cmp = (org.hipparchus.complex.Complex) obj;
      return F.complexNum(cmp.getReal(), cmp.getImaginary());
    }
    if (obj instanceof int[]) {
      return F.ast(S.List, (int[]) obj);
    }
    if (obj instanceof double[]) {
      return new ASTRealVector((double[]) obj, true);
      // return AST.newInstance(F.List, (double[]) obj);
    }
    if (obj instanceof double[][]) {
      return new ASTRealMatrix((double[][]) obj, true);
    }
    if (obj instanceof org.hipparchus.complex.Complex[]) {
      return F.ast(S.List, (org.hipparchus.complex.Complex[]) obj);
    }
    if (obj instanceof Object[]) {
      final Object[] array = (Object[]) obj;
      int length = array.length;
      final IASTAppendable list = F.ListAlloc(length);
      return list.appendArgs(0, length, i -> convert(array[i], parseString, javaObject));
    }
    if (obj instanceof boolean[]) {
      final boolean[] array = (boolean[]) obj;
      final IASTAppendable list = F.ListAlloc(array.length);
      for (int i = 0; i < array.length; i++) {
        list.append(array[i] ? S.True : S.False);
      }
      return list;
    }
    if (obj instanceof LocalDateTime) {
      return DateObjectExpr.newInstance((LocalDateTime) obj);
    }
    if (obj instanceof LocalTime) {
      return TimeObjectExpr.newInstance((LocalTime) obj);
    }
    if (javaObject) {
      return JavaObjectExpr.newInstance(obj);
    }
    return F.$str(obj.toString());
  }

  /**
   * If <code>obj instanceof String</code> return a Symja string object. Otherwise call {@link
   * #convert(Object, boolean, boolean)}.
   *
   * @param obj the object which should be converted to a Symja object
   * @return
   */
  public static IExpr convertString(Object obj) {
    if (obj instanceof String) {
      return F.stringx((String) obj);
    }
    return convert(obj, true, false);
  }

  /**
   * Converts the following Java objects into an IExpr expression
   *
   * <pre>
   * Java Object       -&gt; Symja object
   * -------------------------------------
   * BigInteger           {@link IInteger} value
   * BigDecimal           {@link INum} with {@link Apfloat#Apfloat(java.math.BigDecimal)} value
   * Double               {@link INum}  with doubleValue() value
   * Float                {@link INum}  with doubleValue() value
   * Integer              {@link IInteger} with intValue() value
   * Long                 {@link IInteger} with longValue() value
   * Number               {@link INum} with doubleValue() value
   *
   * </pre>
   */
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
      return F.ZZ((BigInteger) n);
    }
    if (n instanceof BigDecimal) {
      return F.num(new Apfloat((BigDecimal) n));
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
    return F.num(n.doubleValue());
  }

  /**
   * @param lst
   * @param parseString if <code>true</code> and <code>obj instanceof String</code> parse the string
   *     as a Symja expression
   * @param javaObject if <code>true</code> return a wrapper instanceof {@link JavaObjectExpr} if no
   *     other conversion was found
   * @return
   */
  public static IExpr convertList(
      java.util.Collection<?> lst, boolean parseString, boolean javaObject) {
    if (lst.size() == 0) {
      return List();
    } else {
      int size = lst.size();
      IASTAppendable list = F.ast(S.List, size);
      for (Object element : lst) {
        list.append(convert(element, parseString, javaObject));
      }
      return list;
    }
  }

  public static IAST convertComplex(boolean evalComplex, org.hipparchus.complex.Complex[] array) {
    return F.ast(S.List, evalComplex, array);
  }
}
