package org.matheclipse.core.convert;

import static org.matheclipse.core.expression.F.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.JavaObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.parser.ExprParser;
import com.google.common.util.concurrent.AtomicDouble;

/** Converts objects into an IExpr expression */
public class Object2Expr {

  /**
   * Convert a Java object with the mapped {@link Function} into a Symja {@link IExpr}.
   */
  public static IdentityHashMap<Class<? extends Object>, Function<Object, IExpr>> CAST_MAP =
      new IdentityHashMap<Class<? extends Object>, Function<Object, IExpr>>(64);

  static {
    // don't insert interfaces here; only real classes are allowed
    CAST_MAP.put(Integer.class, n -> F.ZZ(((Integer) n).longValue()));
    CAST_MAP.put(Double.class, n -> F.num(((Double) n)));
    CAST_MAP.put(Long.class, n -> F.ZZ(((Long) n)));
    CAST_MAP.put(BigInteger.class, n -> F.ZZ((BigInteger) n));
    CAST_MAP.put(BigDecimal.class, n -> F.num(new Apfloat((BigDecimal) n)));
    CAST_MAP.put(Apfloat.class, n -> F.num((Apfloat) n));
    CAST_MAP.put(Apcomplex.class, n -> F.complexNum((Apcomplex) n));
    CAST_MAP.put(Float.class, n -> F.num(((Float) n).doubleValue()));
    CAST_MAP.put(AtomicInteger.class, n -> F.ZZ(((AtomicInteger) n).longValue()));
    CAST_MAP.put(AtomicDouble.class, n -> F.num(((AtomicDouble) n).doubleValue()));
    CAST_MAP.put(AtomicLong.class, n -> F.ZZ(((AtomicLong) n).longValue()));
    CAST_MAP.put(Boolean.class, b -> ((Boolean) b) ? S.True : S.False);
    CAST_MAP.put(org.hipparchus.fraction.Fraction.class, f -> {
      org.hipparchus.fraction.Fraction frac = (org.hipparchus.fraction.Fraction) f;
      return F.fraction(frac.getNumerator(), frac.getDenominator());
    });
    CAST_MAP.put(org.hipparchus.fraction.BigFraction.class, f -> {
      org.hipparchus.fraction.BigFraction frac = (org.hipparchus.fraction.BigFraction) f;
      return F.fraction(frac.getNumerator(), frac.getDenominator());
    });
    CAST_MAP.put(org.hipparchus.complex.Complex.class, c -> {
      org.hipparchus.complex.Complex cmp = (org.hipparchus.complex.Complex) c;
      return F.complexNum(cmp.getReal(), cmp.getImaginary());
    });
    CAST_MAP.put(LocalDateTime.class, ldt -> DateObjectExpr.newInstance((LocalDateTime) ldt));
    CAST_MAP.put(LocalTime.class, lt -> TimeObjectExpr.newInstance((LocalTime) lt));
  }

  /**
   * <p>
   * Converts the following Java objects into an IExpr expression.
   * <p>
   * {@link #CAST_MAP} is used for converting non-arrays expressions into a Symja object.
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
   *        as a Symja expression
   * @param javaObject if <code>true</code> return a wrapper instance of {@link JavaObjectExpr} if
   *        no other conversion was found
   */
  public static IExpr convert(Object obj) {
    return convert(obj, false, false);
  }

  /**
   * <p>
   * Converts the following Java objects into an IExpr expression.
   * <p>
   * {@link #CAST_MAP} is used for converting non-arrays expressions into a Symja object.
   * 
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
   *        as a Symja expression
   * @param javaObject if <code>true</code> return a wrapper instance of {@link JavaObjectExpr} if
   *        no other conversion was found
   */
  public static IExpr convert(Object obj, boolean parseString, boolean javaObject) {
    if (obj == null) {
      return S.Null;
    }
    if (obj instanceof IExpr) {
      return (IExpr) obj;
    }
    Function<Object, IExpr> function = CAST_MAP.get(obj.getClass());
    if (function != null) {
      return function.apply(obj);
    }
    if (obj instanceof String) {
      if (parseString) {
        final ExprParser parser = new ExprParser(EvalEngine.get());
        return parser.parse((String) obj);
      }
      return F.stringx((String) obj);
    }
    if (obj instanceof Number) {
      return F.num(((Number) obj).doubleValue());
    }
    if (obj instanceof java.util.Collection) {
      java.util.Collection<?> collection = (java.util.Collection<?>) obj;
      return convertList(collection, parseString, javaObject);
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
      return F.mapRange(0, length, i -> convert(array[i], parseString, javaObject));
    }
    if (obj instanceof boolean[]) {
      return convertBooleanArray((boolean[]) obj);
    }
    if (obj instanceof long[]) {
      return convertLongArray((long[]) obj);
    }
    if (javaObject) {
      return JavaObjectExpr.newInstance(obj);
    }
    return F.$str(obj.toString());
  }

  public static IExpr[] convertArray(Object[] array) {
    return convertArray(array, false, false);
  }

  public static IExpr[] convertArray(Object[] array, boolean parseString, boolean javaObject) {
    IExpr[] result = new IExpr[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = convert(array[i], parseString, javaObject);
    }
    return result;
  }

  public static IExpr convertBooleanArray(boolean[] array) {
    final IASTAppendable list = F.ListAlloc(array.length);
    for (int i = 0; i < array.length; i++) {
      list.append(array[i] ? S.True : S.False);
    }
    return list;
  }

  public static IASTMutable convertComplex(boolean evalComplex,
      org.hipparchus.complex.Complex[] array) {
    return F.ast(S.List, evalComplex, array);
  }

  /**
   * @param collection
   * @param parseString if <code>true</code> and <code>obj instanceof String</code> parse the string
   *        as a Symja expression
   * @param javaObject if <code>true</code> return a wrapper instanceof {@link JavaObjectExpr} if no
   *        other conversion was found
   * @return
   */
  public static IExpr convertList(java.util.Collection<?> collection, boolean parseString,
      boolean javaObject) {
    final int size = collection.size();
    if (size == 0) {
      return List();
    } else {
      IASTAppendable list = F.ast(S.List, size);
      for (Object element : collection) {
        list.append(convert(element, parseString, javaObject));
      }
      return list;
    }
  }

  public static IExpr convertLongArray(long[] array) {
    final IASTAppendable list = F.ListAlloc(array.length);
    for (int i = 0; i < array.length; i++) {
      list.append(F.ZZ(array[i]));
    }
    return list;
  }

  /**
   * If <code>obj instanceof String</code> return a Symja string object. Otherwise call
   * {@link #convert(Object, boolean, boolean)}.
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
}
