package org.matheclipse.core.eval;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;

public class CompareUtil {
  public static IComparatorFunction CONST_EQUAL;
  public static ITernaryComparator CONST_GREATER;
  public static ITernaryComparator CONST_LESS;
  public static ITernaryComparator CONST_GREATER_EQUAL;
  public static ITernaryComparator CONST_LESS_EQUAL;

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
   * @return
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
      IAST variables = varSet.getVarList();

      if (function.leafCount() < Config.MAX_POSSIBLE_ZERO_LEAFCOUNT / 5) {
        IExpr expr;
        if (function.hasTrigonometricFunction()) {
          expr = S.TrigToExp.of(engine, function);
          if (expr.leafCount() < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
            expr = SimplifyUtil.simplifyStep(expr, expr, !fastTest, true, engine);
          } else {
            expr = engine.evaluate(expr);
          }
        } else {
          // expr = S.TrigExpand.of(engine, function);
          expr = F.evalExpandAll(function);
          if (expr.isZero()) {
            return true;
          }
          // expr = engine.evaluate(expr);
        }

        if (!expr.isAST()) {
          return expr.isZero();
        }
        function = (IAST) expr;
      }

      if (variables.isEmpty()) {
        INumber num = function.isNumericFunction(true) ? function.evalNumber() : null;

        // if (num == null
        // || !(F.isZero(num.reDoubleValue(), 1.0e-9) && F.isZero(num.imDoubleValue(), 1.0e-9))) {
        // return false;
        // }
        if (num == null || !(F.isZero(num.reDoubleValue(), tolerance)
            && F.isZero(num.imDoubleValue(), tolerance))) {
          return false;
        }
        return true;
      } else {
        if (function.isNumericFunction(varSet)) {
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
            COMPARE_TERNARY possibeZero =
                isPossibeZeroFixedValues(F.C0, function, variables, engine);
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
      }

      IExpr temp = function.replaceAll(x -> x.isNumericFunction(true) //
          ? IExpr.ofNullable(x.evalNumber())
          : F.NIL);
      if (temp.isPresent()) {
        temp = engine.evaluate(temp);
        if (temp.isZero()) {
          return true;
        }
      }

      // if (function.isPlus()) {
      // IExpr[] commonFactors = InternalFindCommonFactorPlus.findCommonFactors(function,
      // true);
      // if (commonFactors != null) {
      // temp = S.Simplify.of(engine, F.Times(commonFactors[0], commonFactors[1]));
      // if (temp.isNumber()) {
      // return temp.isZero();
      // }
      // temp = temp.evalNumber();
      // if (temp != null) {
      // if (temp.isZero()) {
      // return true;
      // }
      // }
      // }
      // }

      return isZeroTogether(function, engine);
    } catch (ValidateException ve) {
      Errors.printMessage(S.PossibleZeroQ, ve, engine);
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
