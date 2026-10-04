package org.matheclipse.core.eval;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.RealNumberComparison;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;
import org.matheclipse.parser.client.ParserConfig;

public class CompareUtil {
  public static IComparatorFunction CONST_EQUAL;
  public static ITernaryComparator CONST_GREATER;
  public static ITernaryComparator CONST_LESS;
  public static ITernaryComparator CONST_GREATER_EQUAL;
  public static ITernaryComparator CONST_LESS_EQUAL;

  /** {@link #compareNumeric(IExpr, IExpr, EvalEngine)} has no answer for the two operands. */
  public static final int NOT_COMPARABLE = Integer.MIN_VALUE;

  /**
   * {@link #numericSign(IExpr, EvalEngine)} found a real value which it could not tell from zero
   * with the precision it uses.
   */
  public static final int UNCERTAIN = Integer.MIN_VALUE + 1;

  /** The two precisions at which a difference of two exact operands is examined. */
  private static final long LOW_DIGITS = 70;

  private static final long HIGH_DIGITS = 140;

  /**
   * Compare two real numeric operands as <code>Less</code>, <code>Greater</code> and
   * <code>Equal</code> do. Every relational built-in derives its answer from this one result, so
   * that <code>a&lt;b</code>, <code>a==b</code> and <code>a&gt;b</code> never contradict each
   * other.
   * <ul>
   * <li>next to an inexact number the other operand is taken at the precision of that number, and
   * inexact numbers which differ only in their last 7 bits are equal
   * ({@link RealNumberComparison#compareWithTolerance(IReal, IReal)})
   * <li>two exact operands are compared at machine precision first. Where the machine values cannot
   * tell - they are equal, differ by a residue only, or an intermediate value left the machine
   * range - the operands are evaluated with arbitrary precision.
   * </ul>
   * <code>General::munfl</code> is not printed for an operand, because its machine value isn't the
   * result of the comparison.
   *
   * @return <code>-1, 0, 1</code> or {@link #NOT_COMPARABLE} if the operands aren't two real
   *         numeric values
   */
  public static int compareNumeric(final IExpr a0, final IExpr a1, EvalEngine engine) {
    return compareNumeric(a0, a1, false, engine);
  }

  /**
   * The sign of a real numeric expression: <code>-1, 0, 1</code>, {@link #NOT_COMPARABLE} if it
   * isn't a real numeric value, or {@link #UNCERTAIN} if it cannot be told from zero with the
   * precision used. In contrast to a comparison, which calls such a value equal to zero, a sign is
   * only answered when it is certain.
   */
  public static int numericSign(final IExpr x, EvalEngine engine) {
    if (x.isReal()) {
      return x.isNaN() ? NOT_COMPARABLE : ((IReal) x).complexSign();
    }
    return compareNumeric(x, F.C0, true, engine);
  }

  /**
   * The order of two real numeric operands for a selection like <code>Min</code> or
   * <code>Max</code>: as {@link #compareNumeric(IExpr, IExpr, EvalEngine)}, but two numbers are
   * ordered by their value without a tolerance, so that the smaller one of two nearly equal numbers
   * can be told.
   */
  public static int orderNumeric(final IExpr a0, final IExpr a1, EvalEngine engine) {
    if (a0.isReal() && a1.isReal()) {
      return a0.isNaN() || a1.isNaN() ? NOT_COMPARABLE
          : RealNumberComparison.order((IReal) a0, (IReal) a1);
    }
    return compareNumeric(a0, a1, engine);
  }

  /**
   * Whether <code>x</code> is zero for a geometric test: an inexact number of the size of a
   * rounding error counts as zero, where <code>Equal</code> takes only zero for equal to zero.
   */
  public static boolean isNumericZero(final IExpr x, EvalEngine engine) {
    if (x.isInexactNumber()) {
      return ((INumber) x).isZero(Config.DEFAULT_EQUALS_TOLERANCE);
    }
    return engine.evaluate(F.Equal(x, F.C0)).isTrue();
  }

  /**
   * Whether two numbers, at least one of them complex, are equal as <code>Equal</code> sees them:
   * the real parts and the imaginary parts are compared separately, each with the tolerance of real
   * numbers. So <code>1.+2.^-48*I</code> is not equal to <code>1.</code>, because zero is equal to
   * zero only.
   */
  public static boolean isEqualWithTolerance(final INumber a, final INumber b) {
    return RealNumberComparison.compareWithTolerance(a.re(), b.re()) == 0
        && RealNumberComparison.compareWithTolerance(a.im(), b.im()) == 0;
  }

  private static int compareNumeric(final IExpr a0, final IExpr a1, boolean certain,
      EvalEngine engine) {
    final boolean function0 = !a0.isNumber() && a0.isNumericFunction(true);
    final boolean function1 = !a1.isNumber() && a1.isNumericFunction(true);
    if (!(function0 || a0.isReal()) || !(function1 || a1.isReal())) {
      return NOT_COMPARABLE;
    }
    final boolean exact0 = function0 ? a0.isFree(x -> x.isInexactNumber(), false) : a0.isRational();
    final boolean exact1 = function1 ? a1.isFree(x -> x.isInexactNumber(), false) : a1.isRational();
    // N(expr, digits) leaves its digits as the number of figures to display
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try (MachineUnderflow.Quiet quiet = engine.machineUnderflow().quiet()) {
      IExpr v0 = function0 ? numericValue(a0, exact0, a1, engine) : a0;
      IExpr v1 = function1 ? numericValue(a1, exact1, v0, engine) : a1;
      if (function0 && v1 instanceof ApfloatNum && !(v0 instanceof ApfloatNum) && v0.isReal()) {
        // the partner turned out to be an arbitrary precision number
        v0 = numericValue(a0, exact0, v1, engine);
      }
      if (!v0.isReal() || !v1.isReal() || v0.isNaN() || v1.isNaN()) {
        return NOT_COMPARABLE;
      }
      int c = RealNumberComparison.compareWithTolerance((IReal) v0, (IReal) v1);
      if ((function0 || function1) && exact0 && exact1
          && (c == 0 || isResidue(a0, function0, v0, a1, function1, v1, engine))) {
        if (!a1.isZero()) {
          // the difference may be simpler than its operands: Pi^1000+1 against Pi^1000
          IExpr difference = engine.evaluate(F.Subtract(a0, a1));
          if (difference.isRational()) {
            return ((IRational) difference).complexSign();
          }
          if (difference.isFree(x -> x.isInexactNumber(), false)) {
            int d = compareNumeric(difference, F.C0, certain, engine);
            if (d != NOT_COMPARABLE) {
              return d;
            }
          }
        }
        return comparePrecise(a0, function0, a1, function1, certain, engine);
      }
      return c;
    } finally {
      if (function0 || function1) {
        engine.setNumericMode(oldNumericMode, oldPrecision, oldSignificantFigures);
      }
    }
  }

  /**
   * The numeric value of an operand of a comparison: at the precision of an arbitrary precision
   * partner, else at machine precision. If an intermediate machine value of an exact operand
   * underflowed, as in <code>E^(-1000)*Pi^1000</code>, the machine result is not the value of the
   * operand and it is evaluated with arbitrary precision.
   */
  private static IExpr numericValue(IExpr a, boolean exact, IExpr partner, EvalEngine engine) {
    if (partner instanceof ApfloatNum) {
      long precision = ((ApfloatNum) partner).precision();
      if (precision <= Config.MAX_PRECISION_APFLOAT) {
        return engine.evalN(a, Math.max(precision, ParserConfig.MACHINE_PRECISION));
      }
    }
    final long underflows = engine.machineUnderflow().count();
    IExpr value = engine.evalN(a);
    if (exact && engine.machineUnderflow().count() != underflows) {
      IExpr precise = engine.evalN(a, LOW_DIGITS);
      if (precise.isReal()) {
        return precise;
      }
    }
    return value;
  }

  /**
   * Whether the machine values of two exact operands differ by no more than the rounding error of a
   * cancellation can be: relative to the largest term of a sum, as in
   * <code>(E+Pi)^2-E^2-Pi^2-2*E*Pi</code> whose machine value is <code>-1.4*10^-14</code>.
   */
  private static boolean isResidue(IExpr a0, boolean function0, IExpr v0, IExpr a1,
      boolean function1, IExpr v1, EvalEngine engine) {
    final double d0 = ((IReal) v0).doubleValue();
    final double d1 = ((IReal) v1).doubleValue();
    final double difference = Math.abs(d0 - d1);
    if (!Double.isFinite(difference)) {
      // an operand beyond the double range: no cancellation can be read from machine values
      return false;
    }
    if (difference < Config.DEFAULT_EQUALS_TOLERANCE) {
      return true;
    }
    double scale = Math.max(Math.abs(d0), Math.abs(d1));
    if (function0) {
      scale = Math.max(scale, largestTerm(a0, engine));
    }
    if (function1) {
      scale = Math.max(scale, largestTerm(a1, engine));
    }
    return difference <= 1.0e-10 * scale;
  }

  /** The largest absolute machine value of the terms of a sum, <code>0.0</code> for no sum. */
  private static double largestTerm(IExpr a, EvalEngine engine) {
    double scale = 0.0;
    if (a.isPlus()) {
      IAST plus = (IAST) a;
      for (int i = 1; i < plus.size(); i++) {
        IExpr term = engine.evalNumericFunction(plus.get(i));
        if (term.isNumber()) {
          double abs = ((INumber) term).abs().evalf();
          if (abs > scale) {
            scale = abs;
          }
        }
      }
    }
    return scale;
  }

  /**
   * Compare two exact operands with arbitrary precision. A difference which shrinks with the
   * precision is the rounding error of a cancellation and the operands are equal:
   * <code>Sqrt(2)*Sqrt(3)==Sqrt(6)</code>. A difference which keeps its size is real, however
   * small: <code>Pi^(-100)&lt;E^(-100)</code>.
   *
   * @param certain answer {@link #NOT_COMPARABLE} instead of <code>0</code>, if the operands could
   *        not be told apart
   */
  private static int comparePrecise(IExpr a0, boolean function0, IExpr a1, boolean function1,
      boolean certain, EvalEngine engine) {
    final int same = certain ? UNCERTAIN : 0;
    IExpr low0 = function0 ? engine.evalN(a0, LOW_DIGITS) : a0;
    IExpr low1 = function1 ? engine.evalN(a1, LOW_DIGITS) : a1;
    IExpr high0 = function0 ? engine.evalN(a0, HIGH_DIGITS) : a0;
    IExpr high1 = function1 ? engine.evalN(a1, HIGH_DIGITS) : a1;
    if (!isPrecise(low0) || !isPrecise(low1) || !isPrecise(high0) || !isPrecise(high1)) {
      // no arbitrary precision value: a machine residue counts as zero
      return same;
    }
    int c = RealNumberComparison.compareWithTolerance((IReal) high0, (IReal) high1);
    if (c == 0) {
      return same;
    }
    Apfloat low = RealNumberComparison.absoluteDifference((IReal) low0, (IReal) low1, HIGH_DIGITS);
    Apfloat high =
        RealNumberComparison.absoluteDifference((IReal) high0, (IReal) high1, 2 * HIGH_DIGITS);
    if (low.signum() == 0 || high.signum() == 0 || high.scale() < low.scale() - 5) {
      return same;
    }
    return c;
  }

  private static boolean isPrecise(IExpr value) {
    return value instanceof ApfloatNum || value instanceof IRational;
  }

  private static final Set<ISymbol> LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));

  private static final Set<ISymbol> PLUS_LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));

  private static final Set<ISymbol> LIST_LOGIC_EQUATION_HEADS =
      Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>(29));

  static {
    ISymbol[] logicEquationHeads = {S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies,
        S.Equivalent, S.Equal, S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual};
    for (int i = 0; i < logicEquationHeads.length; i++) {
      CompareUtil.LOGIC_EQUATION_HEADS.add(logicEquationHeads[i]);
    }
    CompareUtil.PLUS_LOGIC_EQUATION_HEADS.addAll(CompareUtil.LOGIC_EQUATION_HEADS);
    CompareUtil.PLUS_LOGIC_EQUATION_HEADS.add(S.Plus);
    CompareUtil.LIST_LOGIC_EQUATION_HEADS.addAll(CompareUtil.LOGIC_EQUATION_HEADS);
    CompareUtil.LIST_LOGIC_EQUATION_HEADS.add(S.List);
  }

  /**
   * Transform the {@link S#Inequality} AST to an {@link S#And} expression.
   *
   * @param ast an {@link S#Inequality} AST with <code>size() &gt;= 4</code>.
   * @return
   */
  public static IAST inequality2And(final IAST ast) {
    IASTAppendable result = F.And();
    for (int i = 3; i < ast.size(); i += 2) {
      result.append(F.binaryAST2(ast.get(i - 1), ast.get(i - 2), ast.get(i)));
    }
    return result;
  }

  /**
   * Test if <code>Complex(re, im)</code> inserted into the arguments of the function and evaluated
   * approximates <code>0</code>.
   *
   * <ul>
   * <li><code>IExpr.COMPARE_TERNARY.TRUE</code> if the result approximates <code>0</code>
   * <li><code>IExpr.COMPARE_TERNARY.FALSE</code> if the result is a number and doesn't approximate
   * <code>0</code>
   * <li><code>IExpr.COMPARE_TERNARY.UNDECIDABLE</code> if the result isn't a number
   * </ul>
   *
   * @param function the function which should be evaluate for the <code>variables</code>
   * @param variables variables the symbols which will be replaced by <code>Complex(re, im)</code>
   *        to evaluate <code>function</code>
   * @param engine
   * @return
   */
  public static IExpr.COMPARE_TERNARY isPossibeZero(IAST function, IAST variables,
      EvalEngine engine) {
    final ThreadLocalRandom tlr = ThreadLocalRandom.current();
    IASTAppendable listOfRules =
        F.mapList(variables, t -> CompareUtil.randomRuleComplex100(t, tlr));
    IExpr temp = function.replaceAll(listOfRules);
    return CompareUtil.isPossibleZeroApproximate(temp, engine);
  }

  public static IExpr.COMPARE_TERNARY isPossibeZeroFixedValues(INumber number, IAST function,
      IAST variables, EvalEngine engine) {
    IASTAppendable listOfRules = F.mapList(variables, t -> F.Rule(t, number));
    IExpr temp = function.replaceAll(listOfRules);
    return isPossibleZeroExact(temp, engine);
  }

  public static IExpr.COMPARE_TERNARY isPossibleZeroApproximate(IExpr temp, EvalEngine engine) {
    try {
      if (temp.isPresent()) {
        IExpr result = engine.evalQuiet(F.N(temp));
        if (result.isZero()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        }
        if (result.isNumber() && !result.isZero()) {
          double realPart = ((INumber) result).reDoubleValue();
          double imaginaryPart = ((INumber) result).imDoubleValue();
          if (!(F.isZero(realPart, Config.SPECIAL_FUNCTIONS_TOLERANCE)
              && F.isZero(imaginaryPart, Config.SPECIAL_FUNCTIONS_TOLERANCE))) {
            if (Double.isNaN(realPart) || Double.isNaN(imaginaryPart) || Double.isInfinite(realPart)
                || Double.isInfinite(imaginaryPart)) {
              return IExpr.COMPARE_TERNARY.UNDECIDABLE;
            }
            return IExpr.COMPARE_TERNARY.FALSE;
          }
          return IExpr.COMPARE_TERNARY.TRUE;
        }
        if (result.isDirectedInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  public static IExpr.COMPARE_TERNARY isPossibleZeroExact(IExpr temp, EvalEngine engine) {
    try {
      if (temp.isPresent()) {
        IExpr result = engine.evalQuiet(temp);
        if (result.isNumber()) {
          return result.isZero() ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
        }
        if (result.isDirectedInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }

        // if (isZeroTogether(result, engine)) {
        // return IExpr.COMPARE_TERNARY.TRUE;
        // }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  /**
   * Checks if the given function is a possible zero function, i.e. it returns <code>true</code>
   * 
   * @param function
   * @param fastTest
   * @param tolerance typically {@link Config#SPECIAL_FUNCTIONS_TOLERANCE} for the numeric
   *        {@link F#isZero(double, double)} check
   * @param engine
   */
  public static boolean isPossibleZeroQ(IAST function, boolean fastTest, double tolerance,
      EvalEngine engine) {
    if (function.isConditionalExpression()) {
      IExpr arg1 = function.arg1();
      if (arg1.isZero()) {
        return true;
      }
      if (arg1.isPossibleZero(fastTest, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return true;
      }
      return false;
    }
    try {
      VariablesSet varSet = new VariablesSet(function);
      if (varSet.isEmpty()) {
        INumber num = function.isNumericFunction(true) ? function.evalNumber() : null;
        if (num instanceof ApfloatNum && num.reDoubleValue() == 0.0
            && ((ApfloatNum) num).apfloatValue().signum() != 0) {
          // an exact value below the machine range, like Pi^(-1000)
          return false;
        }
        if (num == null || !(F.isZero(num.reDoubleValue(), tolerance)
            && F.isZero(num.imDoubleValue(), tolerance))) {
          return false;
        }
        return true;
      }

      if (function.leafCount() < Config.MAX_POSSIBLE_ZERO_LEAFCOUNT / 5) {
        IExpr expr;
        if (function.hasTrigonometricFunction()) {
          expr = S.TrigToExp.of(engine, function);
          // Simplify() asks whether numerator-denominator is zero for every Times it visits, so
          // while it is running this must stay a test and not become a second simplifier: each
          // nested simplifyStep() runs the whole pipeline again, and those nestings stack — up to
          // four deep on FullSimplify(Cosh(x)/(b*Cosh(x)+c*Sinh(x))). The exponential form that
          // TrigToExp() just produced is what decides the test; evaluating it is enough.
          if (engine.getSimplifyDepth() == 0
              && expr.leafCount() < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
            expr = SimplifyUtil.simplifyStep(expr, expr, !fastTest, true, engine);
          } else {
            expr = engine.evaluate(expr);
          }
        } else {
          expr = F.evalExpandAll(function);
          if (expr.isZero()) {
            return true;
          }
        }

        if (!expr.isAST()) {
          return expr.isZero();
        }
        function = (IAST) expr;
      }

      if (function.isNumericFunction(varSet)) {
        IAST variables = varSet.getVarList();
        if (variables.argSize() == 1) {
          IExpr derived = engine.evaluate(F.D(function, variables.get(1)));
          if (derived.isNumericFunction()) {
            if (!derived.isNumber()) {
              derived = engine.evalN(derived);
            }
            if (derived.isNumber()) {
              if (derived.isZero()) {
                COMPARE_TERNARY possibeZero =
                    isPossibeZeroFixedValues(F.C0, function, variables, engine);
                if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
                  return true;
                }
                if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
                  return false;
                }
              } else {
                return false;
              }
            }
          }
        }

        if (function.isFreeAST(h -> isSpecialNumericFunction(h))) {
          int trueCounter = 0;

          // 1. step test some special complex numeric values
          COMPARE_TERNARY possibeZero = isPossibeZeroFixedValues(F.C0, function, variables, engine);
          if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
            return false;
          }
          if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
            trueCounter++;
          }
          possibeZero = isPossibeZeroFixedValues(F.C1, function, variables, engine);
          if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
            return false;
          }
          if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
            trueCounter++;
          }
          possibeZero = isPossibeZeroFixedValues(F.CN1, function, variables, engine);
          if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
            return false;
          }
          if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
            trueCounter++;
          }
          possibeZero = isPossibeZeroFixedValues(F.CI, function, variables, engine);
          if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
            return false;
          }
          if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
            trueCounter++;
          }
          possibeZero = isPossibeZeroFixedValues(F.CNI, function, variables, engine);
          if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
            return false;
          }
          if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
            trueCounter++;
          }

          if (trueCounter == 5) {
            // 2. step test some random complex numeric values
            for (int i = 0; i < 36; i++) {
              possibeZero = isPossibeZero(function, variables, engine);
              if (possibeZero == IExpr.COMPARE_TERNARY.FALSE) {
                return false;
              }
              if (possibeZero == IExpr.COMPARE_TERNARY.TRUE) {
                trueCounter++;
              }
            }
            if (trueCounter > 28) {
              return true;
            }
          }
          if (fastTest) {
            return false;
          }
        }
      }


      IExpr temp = numericSubstitution(function);
      if (temp.isPresent()) {
        temp = engine.evaluate(temp);
        if (temp.isZero()) {
          return true;
        }
      }
      return isZeroTogether(function, engine);
    } catch (ValidateException ve) {
      Errors.printMessage(S.PossibleZeroQ, ve, engine);
    }
    return false;
  }

  /**
   * Substitute every numeric (sub-)expression of <code>expr</code> by its {@link INumber} value.
   *
   * <p>
   * Arguments which the {@link ISymbol#NHOLDFIRST} / {@link ISymbol#NHOLDREST} attributes of their
   * head protect are left alone. For those heads a number is a structural index and not a value, so
   * numericalizing it doesn't produce a numeric approximation but an invalid expression:
   * <code>Derivative(1)[y][x]</code> would become <code>Derivative(1.0)[y][x]</code> and
   * <code>Surd(x,3)</code> would become <code>Surd(x,3.0)</code>, both of which their evaluator
   * rejects with an error message.
   *
   * @return {@link F#NIL} if nothing was substituted
   */
  private static IExpr numericSubstitution(IExpr expr) {
    if (expr.isNumericFunction(true)) {
      IExpr number = IExpr.ofNullable(expr.evalNumber());
      if (number.isPresent()) {
        return number;
      }
    }
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    final int attributes =
        ast.head().isSymbol() ? ((ISymbol) ast.head()).getAttributes() : ISymbol.NOATTRIBUTE;
    if (Attribute.NHOLDALL.isSetIn(attributes)) {
      return F.NIL;
    }
    IASTMutable result = F.NIL;
    // start at the head, because it can be a `Derivative(1)[y]` like expression itself
    for (int i = 0; i < ast.size(); i++) {
      if (isNHold(attributes, i)) {
        continue;
      }
      IExpr temp = numericSubstitution(ast.get(i));
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        if (i == 0) {
          // The loop starts at the head on purpose, see above, but the fixed size AST classes -
          // B1, B2 and the rest - refuse a write to index 0: another head means another expression
          // class, so there is nowhere to put it. setAtCopy() rebuilds them instead, where set()
          // only answers an IndexOutOfBoundsException.
          result = result.setAtCopy(0, temp);
        } else {
          result.set(i, temp);
        }
      }
    }
    return result;
  }

  /**
   * Test if the argument at <code>position</code> is protected from numericalization by the
   * {@link ISymbol#NHOLDFIRST} / {@link ISymbol#NHOLDREST} attributes of its head.
   *
   * @param attributes the attributes bitmask of the heads symbol
   * @param position <code>0</code> is the head itself, which is never held
   */
  private static boolean isNHold(int attributes, int position) {
    if (position == 1) {
      return Attribute.NHOLDFIRST.isAnySetIn(attributes);
    }
    if (position > 1) {
      return Attribute.NHOLDREST.isAnySetIn(attributes);
    }
    return false;
  }

  public static boolean isSpecialNumericFunction(IExpr head) {
    if (head.isPower()) {
      if (!head.exponent().isNumber()) {
        return false;
      }
      return true;
    }
    int h = head.headID();

    return h == ID.AppellF1 || h == ID.Clip
    // || h == ID.Cosh
        || h == ID.Csch || h == ID.Cot || h == ID.Csc || h == ID.Gamma || h == ID.HankelH1
        || h == ID.HankelH2 || h == ID.Hypergeometric0F1 || h == ID.Hypergeometric1F1
        || h == ID.Hypergeometric2F1 || h == ID.Hypergeometric1F1Regularized
        || h == ID.HypergeometricPFQ || h == ID.HypergeometricPFQRegularized
        || h == ID.HypergeometricU || h == ID.JacobiAmplitude || h == ID.JacobiCD
        || h == ID.JacobiCN || h == ID.JacobiDC || h == ID.JacobiDN || h == ID.JacobiNC
        || h == ID.JacobiND || h == ID.JacobiSC || h == ID.JacobiSD || h == ID.JacobiSN
        || h == ID.JacobiZeta || h == ID.KleinInvariantJ || h == ID.Log || h == ID.Piecewise
        // || h == ID.Power
        || h == ID.ProductLog
        // || h == ID.Sinh
        || h == ID.StruveH || h == ID.StruveL || h == ID.Tan || h == ID.WeierstrassHalfPeriods
        || h == ID.WeierstrassInvariants || h == ID.WeierstrassP || h == ID.WeierstrassPPrime
        || h == ID.InverseWeierstrassP;
  }

  public static boolean isZeroTogether(IExpr expr, EvalEngine engine) {
    // expr = F.expandAll(expr, true, true);
    // expr = engine.evaluate(expr);
    // if (expr.isZero()) {
    // return true;
    // }
    long leafCount = expr.leafCount();
    if (leafCount > Config.MAX_POSSIBLE_ZERO_LEAFCOUNT) {
      return false;
    }
    if (expr.isPlusTimesPower()) {
      if (leafCount > (Config.MAX_POSSIBLE_ZERO_LEAFCOUNT / 4)) {
        return false;
      }
      expr = engine.evaluate(F.Together(expr));
      if (expr.isNumber()) {
        return expr.isZero();
      }
      if (expr.isTimes()) {
        IExpr denominator = engine.evalN(F.Denominator(expr));
        if (!denominator.isZero() //
            && !denominator.isOne()) {
          IExpr numerator = engine.evaluate(F.Numerator(expr));
          if (numerator.isAST()) {
            return CompareUtil.isPossibleZeroQ((IAST) numerator, false,
                Config.SPECIAL_FUNCTIONS_TOLERANCE, engine);
          }
        }
      }
    }
    return false;
  }

  /**
   * Create a rule <code>variable -> complex-number</code> with real and imaginary part randomly
   * between -100.0 and 100.0
   * 
   * @param variable
   * @param tlr
   * @return
   */
  public static IExpr randomRuleComplex100(IExpr variable, ThreadLocalRandom tlr) {
    double re = tlr.nextDouble(-100, 100);
    double im = tlr.nextDouble(-100, 100);
    return F.Rule(variable, F.complexNum(re, im));
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.List S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadListLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (CompareUtil.LIST_LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (CompareUtil.LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  /**
   * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>
   * replacement</code> is an IAST where the argument at the given position will be replaced by the
   * currently mapped element. Thread over the following headers: <code>
   * S.Plus, S.And, S.Or, S.Xor, S.Nand, S.Nor, S.Not, S.Implies, S.Equivalent, S.Equal,S.Unequal, S.Less, S.Greater, S.LessEqual, S.GreaterEqual
   * </code>
   *
   * @param expr typically the first element of <code>replacement</code> ast.
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return
   */
  public static IAST threadPlusLogicEquationOperators(IExpr expr, IAST replacement, int position) {
    if (expr.size() > 1 && expr.isAST()) {
      IAST ast = (IAST) expr;
      if (CompareUtil.PLUS_LOGIC_EQUATION_HEADS.contains(ast.head())) {
        // IASTMutable copy = replacement.setAtCopy(position, null);
        return ast.mapThread(replacement, position);
      }
    }
    return F.NIL;
  }

  private CompareUtil() {
    // private constructor to avoid instantiation
  }
}
