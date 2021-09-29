package org.matheclipse.core.eval.interfaces;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates
 * to the <code>evaluate()</code>
 */
public abstract class AbstractFunctionEvaluator extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Check if the expression has a complex number factor I.
   *
   * @param expression
   * @param factor
   * @return the negated negative expression or <code>null</code> if a negative expression couldn't
   *     be extracted.
   */
  public static IExpr extractFactorFromExpression(final IExpr expression, INumber factor) {
    return extractFactorFromExpression(expression, factor, true);
  }

  /**
   * Check if the expression has a complex number factor I.
   *
   * @param expression
   * @param factor
   * @param checkTimes check <code>Times(...)</code> expressions
   * @return the negated negative expression or <code>F.NIL</code> if a negative expression couldn't
   *     be extracted.
   */
  public static IExpr extractFactorFromExpression(
      final IExpr expression, INumber factor, boolean checkTimes) {
    if (expression.isNumber()) {
      if (((INumber) expression).equals(factor)) {
        return F.C1;
      }
    } else {
      if (expression.isAST()) {
        if (checkTimes && expression.isTimes()) {
          IAST timesAST = ((IAST) expression);
          IExpr arg1 = timesAST.arg1();
          if (arg1.isNumber()) {
            if (((INumber) arg1).isImaginaryUnit()) {
              return timesAST.rest().oneIdentity(factor);
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Check if the expression is canonical negative.
   *
   * @param expression
   * @return the negated negative expression or <code>F.NIL</code> if a negative expression couldn't
   *     be extracted.
   */
  public static IExpr getNormalizedNegativeExpression(final IExpr expression) {
    return getNormalizedNegativeExpression(expression, true);
  }

  /**
   * Check if the expression is canonical negative.
   *
   * @param expression
   * @param checkTimesPlus check <code>Times(...)</code> and <code>Plus(...)</code> expressions
   * @return the negated negative expression or <code>F.NIL</code> if a negative expression couldn't
   *     be extracted.
   */
  public static IExpr getNormalizedNegativeExpression(
      final IExpr expression, boolean checkTimesPlus) {
    IASTMutable result = F.NIL;
    if (expression.isNumber()) {
      if (((INumber) expression).complexSign() < 0) {
        return ((INumber) expression).negate();
      }
      return F.NIL;
    } else if (expression.isAST()) {
      if (checkTimesPlus && expression.isTimes()) {
        IAST timesAST = ((IAST) expression);
        IExpr arg1 = timesAST.arg1();
        // see github #110: checking for arg1.isNegative() will trigger infinite recursion!
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            IExpr negNum = ((INumber) arg1).negate();
            if (negNum.isOne()) {
              return timesAST.rest().oneIdentity1();
            }
            return timesAST.setAtCopy(1, negNum);
          }
        } else if (arg1.isNegativeInfinity()) {
          return timesAST.setAtCopy(1, F.CInfinity);
        }
      } else if (checkTimesPlus && expression.isPlus()) {
        IAST plusAST = ((IAST) expression);
        IExpr arg1 = plusAST.arg1();
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            result = plusAST.copy();
            result.set(1, arg1.negate());
            for (int i = 2; i < plusAST.size(); i++) {
              result.set(i, plusAST.get(i).negate());
            }
            return result;
          }
        } else if (arg1.isNegativeInfinity()) {
          result = plusAST.copy();
          result.set(1, F.CInfinity);
          for (int i = 2; i < plusAST.size(); i++) {
            result.set(i, plusAST.get(i).negate());
          }
          return result;
        } else if (arg1.isTimes()) {
          IExpr arg1Negated = getNormalizedNegativeExpression(arg1, checkTimesPlus);
          if (arg1Negated.isPresent()) {
            // int positiveElementsCounter = 0;
            result = plusAST.copy();
            result.set(1, arg1Negated);
            for (int i = 2; i < plusAST.size(); i++) {
              IExpr temp = plusAST.get(i);
              // if (!temp.isTimes() && !temp.isPower()) {
              // return F.NIL;
              // }

              // arg1Negated = getNormalizedNegativeExpression(temp, checkTimesPlus);
              // if (arg1Negated.isPresent()) {
              // result.set(i, arg1Negated);
              // } else {

              // positiveElementsCounter++;
              // if (positiveElementsCounter * 2 > plusAST.argSize()) {
              // number of positive elements is greater
              // than number of negative elements
              // return F.NIL;
              // }
              result.set(i, temp.negate());

              // }
            }
            return result;
          }
        }
        // } else if (expression.isNegativeInfinity()) {
        // return F.CInfinity;
      } else if (expression.isDirectedInfinity() && expression.isAST1()) {
        IExpr arg1 = expression.first();
        if (arg1.isMinusOne()) {
          return F.CInfinity;
        }
        if (arg1.isNegativeImaginaryUnit()) {
          return F.DirectedInfinity(F.CI);
        }
      }
    }
    // if (expression.isNegativeResult()) {
    // // doesn't work because of recursion calls
    // return F.eval(F.Negate(expression));
    // }
    return F.NIL;
  }

  /**
   * Return <code>true</code> if the number of negative terms of the <code>ast</code> expression
   * <code>countWeight</code> is greater than the half of the number of the arguments of <code>ast
   * </code>. I.e. <code>int halfSize = ast.size() / 2; return countWeight > halfSize;</code>
   *
   * @param ast
   * @param checkTimesPlus check <code>Times(...)</code> and <code>Plus(...)</code> expressions
   * @return
   */
  public static boolean isNegativeWeighted(final IAST ast, boolean checkTimesPlus) {
    return isNegativeWeighted(ast, checkTimesPlus, ast.size() / 2);
  }

  /**
   * Return the number of negative terms of the <code>ast</code> expression.
   *
   * @param ast
   * @param checkTimesPlus check <code>Times(...)</code> and <code>Plus(...)</code> expressions
   * @param maxNegativeExpr maximum number of negative valued terms which have to be found before
   *     returning <code>true</code>
   * @return
   */
  public static boolean isNegativeWeighted(
      final IAST ast, boolean checkTimesPlus, int maxNegativeExpr) {
    int count = maxNegativeExpr - 1;
    for (int i = 1; i < ast.size(); i++) {
      if (isNegativeValued(ast.get(i), checkTimesPlus)) {
        if (--count < 0) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Return <code>true</code> if the <code>expression</code> is considered having a <i>negative
   * value</i>
   *
   * @param expression
   * @param checkTimesPlus check <code>Times(...)</code> and <code>Plus(...)</code> expressions
   * @return
   */
  private static boolean isNegativeValued(final IExpr expression, boolean checkTimesPlus) {
    if (expression.isNumber()) {
      return ((INumber) expression).complexSign() < 0;
    } else if (expression.isAST()) {
      if (checkTimesPlus && expression.isTimes()) {
        IExpr arg1 = expression.first();
        // see github #110: checking for arg1.isNegative() will trigger infinite recursion!
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            return true;
          }
        } else if (arg1.isNegativeInfinity()) {
          return true;
        }
      } else if (checkTimesPlus && expression.isPlus()) {
        IAST plusAST = ((IAST) expression);
        IExpr arg1 = plusAST.arg1();
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            return true;
          }
        } else if (arg1.isNegativeInfinity()
            || (arg1.isTimes() && isNegativeValued(arg1, checkTimesPlus))) {
          return true;
        }
      } else if (expression.isDirectedInfinity() && expression.isAST1()) {
        IExpr arg1 = expression.first();
        if (arg1.isMinusOne() || arg1.isNegativeImaginaryUnit()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Try to split a periodic part from the expression: <code>
   * expr == part.arg1() + part.arg2() * period</code>
   *
   * @param expr
   * @param period
   * @return <code>F.NIL</code> if no periodicity was found or the rest at argument 1 and the factor
   *     of the period at argument 2
   */
  public static IAST getPeriodicParts(final IExpr expr, final IExpr period) {
    // IExpr[] result = new IExpr[2];
    // result[0] = F.C0;
    // result[1] = F.C1;
    IASTMutable result = F.binaryAST2(S.List, F.C0, F.C1);
    if (expr.equals(period)) {
      return result;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isTimes()) {
        for (int i = 1; i < ast.size(); i++) {
          if (ast.get(i).equals(period)) {
            result.set(2, ast.splice(i).oneIdentity1());
            return result;
          }
        }
        return F.NIL;
      }
      if (ast.isPlus()) {
        for (int i = 1; i < ast.size(); i++) {
          IAST temp = getPeriodicParts(ast.get(i), period);
          if (temp.isPresent() && temp.arg1().isZero()) {
            result.set(1, ast.splice(i).oneIdentity0());
            result.set(2, temp.arg2());
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Split plusAST into two parts, a "rest" and a multiple of Pi/2. This assumes plusAST to be an
   * Plus() expression. The multiple of Pi/2 returned in the second position is always a IRational
   * number.
   *
   * @param plusAST
   * @param engine
   * @return <code>F.NIL</code> if no multiple is found.
   */
  public static IAST peelOff(final IAST plusAST, final EvalEngine engine) {
    IRational k = null;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr temp = plusAST.get(i);
      if (temp.equals(S.Pi)) {
        k = F.C1;
        break;
      }
      if (temp.isTimes2()) {
        if (temp.first().isRational() && temp.second().equals(S.Pi)) {
          k = (IRational) temp.first();
          break;
        }
      }
    }
    if (k != null) {
      IASTMutable result = F.binaryAST2(S.List, plusAST, F.C0);
      IExpr m1 = F.Times(k.mod(F.C1D2), S.Pi);
      IExpr m2 = S.Subtract.of(engine, F.Times(k, S.Pi), m1);
      result.set(1, S.Subtract.of(plusAST, m2));
      result.set(2, m2);
      return result;
    }
    return F.NIL;
  }

  /**
   * This method assumes plusAST to be a Plus() expression. The multiple of Pi returned is a
   * IRational number or assumed to be an expression with Integer result.
   *
   * @param plusAST
   * @param engine
   * @return <code>F.NIL</code> if no multiple is found.
   */
  public static IExpr peelOffPlusRational(final IAST plusAST, final EvalEngine engine) {
    IExpr k = null;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr temp = plusAST.get(i);
      if (temp.equals(S.Pi)) {
        k = F.C1;
        return k;
      }
      if (temp.isTimes()) {
        IExpr peeled = peelOfTimes((IAST) temp, S.Pi);
        if (peeled.isPresent()) {
          if (peeled.isRational() || peeled.isIntegerResult()) {
            // k = temp.first();
            return peeled;
          }
        }
      }
    }
    return null;
  }

  /**
   * This assumes plusAST to be an Plus() expression. The multiple of Pi returned is a IRational
   * number or assumed to be an expression with Integer result.
   *
   * @param plusAST
   * @param engine
   * @return <code>F.NIL</code> if no multiple is found, otherwise return <code>
   *     List(rational-number, argument)</code>
   */
  public static IAST peelOffPlusI(final IAST plusAST, final EvalEngine engine) {
    for (int i = 1; i < plusAST.size(); i++) {
      final IExpr arg = plusAST.get(i);
      if (arg.isTimes()) {
        IExpr peeled = peelOfTimes((IAST) arg, S.Pi);
        if (peeled.isPresent()) {
          IExpr x = S.Times.of(F.CNI, peeled);
          if (x.isRational() || x.isIntegerResult()) {
            return F.List(x, arg);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Try to split a periodic part from the Times() expression: <code>result == timesAST / period
   * </code>
   *
   * @param astTimes
   * @param period
   * @return <code>F.NIL</code> if no periodicity was found
   */
  public static IExpr peelOfTimes(final IAST astTimes, final IExpr period) {
    for (int i = 1; i < astTimes.size(); i++) {
      if (astTimes.get(i).equals(period)) {
        return astTimes.splice(i).oneIdentity1();
      }
    }
    return F.NIL;
  }

  /**
   * Try to split a periodic part from the Times() expression: <code>result == timesAST / period
   * </code>
   *
   * @param astTimes
   * @param period1
   * @param period2
   * @return <code>F.NIL</code> if no periodicity was found
   */
  public static IExpr peelOfTimes(final IAST astTimes, final IExpr period1, final IExpr period2) {
    IASTAppendable result = F.NIL;
    for (int i = 1; i < astTimes.size(); i++) {
      if (astTimes.get(i).equals(period1)) {
        result = astTimes.copyAppendable();
        result.remove(i);
        for (int j = 1; j < result.size(); j++) {
          if (result.get(j).equals(period2)) {
            result.remove(j);
            return result;
          }
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  /**
   * Check if the expression is canonical negative.
   *
   * @param expression
   * @param checkTimesPlus check <code>Times(...)</code> and <code>Plus(...)</code> expressions
   * @return the negated negative expression or <code>F.NIL</code> if a negative expression couldn't
   *     be extracted.
   */
  public static IExpr getPowerNegativeExpression(final IExpr expression, boolean checkTimesPlus) {
    IASTMutable result = F.NIL;
    if (expression.isNumber()) {
      if (((INumber) expression).complexSign() < 0) {
        return ((INumber) expression).negate();
      }
      return F.NIL;
    }
    if (expression.isAST()) {
      if (checkTimesPlus && expression.isTimes()) {
        IAST timesAST = ((IAST) expression);
        IExpr arg1 = timesAST.arg1();
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            IExpr negNum = ((INumber) arg1).negate();
            if (negNum.isOne()) {
              return timesAST.rest().oneIdentity1();
            }
            return timesAST.setAtCopy(1, negNum);
          }
        } else if (arg1.isNegativeInfinity()) {
          return timesAST.setAtCopy(1, F.CInfinity);
        } else if (arg1.isNegative()) {
          IExpr negNum = arg1.negate();
          return timesAST.setAtCopy(1, negNum);
        }
      } else if (checkTimesPlus && expression.isPlus()) {
        IAST plusAST = ((IAST) expression);
        IExpr arg1 = plusAST.arg1();
        if (arg1.isNumber()) {
          if (((INumber) arg1).complexSign() < 0) {
            result = plusAST.copy();
            result.set(1, arg1.negate());
            for (int i = 2; i < plusAST.size(); i++) {
              result.set(i, plusAST.get(i).negate());
            }
            return result;
          }
        } else if (arg1.isNegativeInfinity()) {
          result = plusAST.copy();
          result.set(1, F.CInfinity);
          for (int i = 2; i < plusAST.size(); i++) {
            result.set(i, plusAST.get(i).negate());
          }
          return result;
        } else if (arg1.isTimes()) {
          IExpr arg1Negated = getPowerNegativeExpression(arg1, checkTimesPlus);
          if (arg1Negated.isPresent()) {
            int positiveElementsCounter = 0;
            result = plusAST.copy();
            result.set(1, arg1Negated);
            for (int i = 2; i < plusAST.size(); i++) {
              IExpr temp = plusAST.get(i);
              if (!temp.isTimes() && !temp.isPower()) {
                return F.NIL;
              }
              arg1Negated = getPowerNegativeExpression(temp, checkTimesPlus);
              if (arg1Negated.isPresent()) {
                result.set(i, arg1Negated);
              } else {
                positiveElementsCounter++;
                if (positiveElementsCounter * 2 > plusAST.argSize()) {
                  // number of positive elements is greater
                  // than number of negative elements
                  return F.NIL;
                }
                result.set(i, temp.negate());
              }
            }
            return result;
          }
        }
        // } else if (expression.isNegativeInfinity()) {
        // return F.CInfinity;
      } else if (expression.isDirectedInfinity() && expression.isAST1()) {
        IExpr arg1 = expression.first();
        if (arg1.isMinusOne()) {
          return F.CInfinity;
        }
        if (arg1.isNegativeImaginaryUnit()) {
          return F.DirectedInfinity(F.CI);
        }
      }
    }
    // if (expression.isNegativeResult()) {
    // return F.eval(F.Negate(expression));
    // }
    return F.NIL;
  }

  /**
   * Check if <code>expr</code> is a pure imaginary number without a real part.
   *
   * @param expr
   * @return <code>F.NIL</code>, if <code>expr</code> is not a pure imaginary number.
   */
  public static IExpr getPureImaginaryPart(final IExpr expr) {
    IExpr temp = pureImaginaryPart(expr);
    if (temp.isPresent()) {
      return temp;
    } else if (expr.isPlus()) {
      IAST plus = ((IAST) expr);
      IExpr arg = pureImaginaryPart(plus.arg1());
      if (arg.isPresent()) {
        IASTMutable result = plus.setAtCopy(1, arg);
        for (int i = 2; i < plus.size(); i++) {
          arg = pureImaginaryPart(plus.get(i));
          if (!arg.isPresent()) {
            return F.NIL;
          }
          result.set(i, arg);
        }
        return result;
      }
    }
    return F.NIL;
  }

  public static IExpr getComplexExpr(final IExpr expr, IExpr factor) {
    if (expr.isComplex() && (expr.re().isZero() || expr.re().isNegative())) {
      return F.Times(factor, expr);
    } else {
      if (expr.isTimes() && expr.first().isComplex()) {
        IComplex arg1 = (IComplex) expr.first();
        if (arg1.re().isZero() || arg1.re().isNegative()) {
          return F.Times(factor, expr);
        }
      } else if (expr.isPlus()) {
        IExpr arg1 = expr.first();
        if (arg1.isComplex() && (arg1.re().isZero() || arg1.re().isNegative())) {
          // distribute the factor over the Plus() args
          return F.Distribute(F.Times(factor, expr));
        }
        if (arg1.isTimes() && arg1.first().isComplex()) {
          arg1 = arg1.first();
          if (arg1.re().isZero() || arg1.re().isNegative()) {
            // distribute the factor over the Plus() args
            return F.Distribute(F.Times(factor, expr));
          }
        }
      }
    }
    return F.NIL;
  }

  public static IExpr imaginaryPart(final IExpr expr, boolean unequalsZero) {
    IExpr imPart = S.Im.of(expr);
    if (unequalsZero) {
      if (imPart.isZero()) {
        return F.NIL;
      }
    }
    if (imPart.isNumber() || imPart.isFree(S.Im)) {
      return imPart;
    }
    return F.NIL;
  }

  public static IExpr realPart(final IExpr expr, boolean unequalsZero) {
    IExpr rePart = S.Re.of(expr);
    if (unequalsZero) {
      if (rePart.isZero()) {
        return F.NIL;
      }
    }
    if (rePart.isNumber() || rePart.isFree(S.Re)) {
      return rePart;
    }
    return F.NIL;
  }

  /**
   * Initialize the serialized Rubi integration rules from ressource <code>/ser/integrate.ser</code>
   * .
   *
   * @param symbol
   */
  public static void initSerializedRules(final ISymbol symbol) {
    EvalEngine engine = EvalEngine.get();
    boolean oldPackageMode = engine.isPackageMode();
    boolean oldTraceMode = engine.isTraceMode();

    engine.setPackageMode(true);
    engine.setTraceMode(false);
    try (
        InputStream in = AbstractFunctionEvaluator.class.getResourceAsStream(
            "/ser/" + symbol.getSymbolName().toLowerCase(Locale.ENGLISH) + ".ser");
        ObjectInputStream ois = new ObjectInputStream(in);) {
      // InputStream in = new FileInputStream("c:\\temp\\ser\\" +
      // symbol.getSymbolName() + ".ser");
      // read files with BufferedInputStream to improve performance
      // ObjectInputStream ois = new ObjectInputStream(new
      // BufferedInputStream(in));
      symbol.readRules(ois);
    } catch (IOException | ClassNotFoundException e) {
      LOGGER.error("AbstractFunctionEvaluator.initSerializedRules() failed", e);
    } finally {
      engine.setPackageMode(oldPackageMode);
      engine.setTraceMode(oldTraceMode);
    }
  }

  private static IExpr pureImaginaryPart(final IExpr expr) {
    if (expr.isComplex() && ((IComplex) expr).re().isZero()) {
      IComplex compl = (IComplex) expr;
      return compl.im();
    }
    if (expr.isTimes()) {
      IAST times = ((IAST) expr);
      IExpr arg1 = times.arg1();
      if (arg1.isComplex() && ((IComplex) arg1).re().isZero()) {
        return times.setAtCopy(1, ((IComplex) arg1).im());
      }
    }
    return F.NIL;
  }

  /**
   * Create a rule which invokes the method name in this class instance.
   *
   * @param symbol
   * @param patternString
   * @param methodName
   */
  public void createRuleFromMethod(ISymbol symbol, String patternString, String methodName) {
    PatternMatcherAndInvoker pm = new PatternMatcherAndInvoker(patternString, this, methodName);
    symbol.putDownRule(pm);
  }

  /** {@inheritDoc} */
  @Override
  public abstract IExpr evaluate(final IAST ast, final EvalEngine engine);

  /**
   * Get the predefined rules for this function symbol.
   *
   * @return <code>null</code> if no rules are defined
   */
  public IAST getRuleAST() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void setUp(final ISymbol newSymbol) {

    if (getRuleAST() != null) {
      // don't call EvalEngine#addRules() here!
      // the rules should add themselves
      // EvalEngine.get().addRules(ruleList);
    }

    // F.SYMBOL_OBSERVER.createPredefinedSymbol(newSymbol.toString());
    if (Config.SERIALIZE_SYMBOLS && newSymbol.containsRules()) {
      try (
          FileOutputStream out =
              new FileOutputStream("c:\\temp\\ser\\" + newSymbol.getSymbolName() + ".ser");
          ObjectOutputStream oos = new ObjectOutputStream(out);) {
        newSymbol.writeRules(oos);
      } catch (IOException e) {
        LOGGER.error("AbstractFunctionEvaluator.setUp() failed", e);
      }
    }
  }

  /**
   * Determine the options from the last arguments of <code>ast</code> or from the default options.
   *
   * @param options the result array of options and their (possibly default) values.
   * @param ast
   * @param argSize
   * @param expectedArgSize
   * @param optionSymbol
   * @param engine
   * @return
   */
  protected static int determineOptions(
      IExpr[] options,
      IAST ast,
      int argSize,
      int[] expectedArgSize,
      IBuiltInSymbol[] optionSymbol,
      EvalEngine engine) {
    argSize = determineArgumentOptions(options, ast, argSize, expectedArgSize, optionSymbol);

    determineDefaultOptions(options, ast, optionSymbol, engine);
    return argSize;
  }

  /**
   * Determine the options only from the last arguments of <code>ast</code>. Possibly additional
   * options are <code>null</code> if no matching option is found in the arguments of the <code>ast
   * </code>.
   *
   * @param options
   * @param ast
   * @param argSize
   * @param expectedArgSize
   * @param optionSymbol
   * @return
   */
  private static int determineArgumentOptions(
      IExpr[] options,
      IAST ast,
      int argSize,
      int[] expectedArgSize,
      IBuiltInSymbol[] optionSymbol) {
    int minNumberOfArgs = 1;
    if (expectedArgSize != null) {
      // the ast function must at least contain the minimum number of arguments
      minNumberOfArgs = expectedArgSize[0];
      if (minNumberOfArgs < 1) {
        minNumberOfArgs = 1;
      }
    }

    int counter = 0;
    boolean evaled = true;
    while (argSize > minNumberOfArgs && evaled && counter < optionSymbol.length) {
      IExpr arg = ast.get(argSize);

      // check that arg has the correct options format:
      if (arg.isRule()) {
        evaled = false;
        for (int i = 0; i < optionSymbol.length; i++) {
          if (optionSymbol[i].equals(arg.first())) {
            options[i] = arg.second();
            argSize--;
            counter++;
            evaled = true;
            break;
          }
        }
      } else if (arg.isListOfRules(true) && !arg.isEmptyList()) {
        IAST listOfRules = (IAST) arg;
        for (int j = 1; j < listOfRules.size(); j++) {
          IAST rule = (IAST) listOfRules.get(j);
          evaled = false;
          for (int i = 0; i < optionSymbol.length; i++) {
            if (optionSymbol[i].equals(rule.first())) {
              options[i] = rule.second();
              argSize--;
              counter++;
              evaled = true;
              break;
            }
          }
        }
      } else {
        evaled = false;
        break;
      }
    }
    return argSize;
  }

  /**
   * Replace the options which are <code>null</code> only from the default options of the head
   * symbol.
   *
   * @param options
   * @param ast
   * @param optionSymbol
   * @param engine
   */
  private static void determineDefaultOptions(
      IExpr[] options, IAST ast, IBuiltInSymbol[] optionSymbol, EvalEngine engine) {
    int optionNullStart = -1;
    for (int i = 0; i < options.length; i++) {
      if (options[i] == null) {
        optionNullStart = i;
        break;
      }
    }
    if (optionNullStart >= 0) {
      final IExpr temp = engine.evaluate(F.Options(ast.topHead()));
      if (temp.isList() && temp.size() > 1) {
        IAST list = (IAST) temp;
        for (int i = optionNullStart; i < options.length; i++) {
          if (options[i] == null) {
            options[i] = S.None;
            for (int j = 1; j < list.size(); j++) {
              IAST rule = (IAST) list.get(j);
              if (optionSymbol[i].equals(rule.first())) {
                options[i] = rule.second();
                break;
              }
            }
          }
        }
      }
    }
  }
}
