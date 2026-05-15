package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ReduceVariableEqual;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Forward and backward substitutions of expressions for polynomials. See <a href=
 * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
 * Homogenization</a>
 */
public class PolynomialHomogenization {
  class TanhSechTransform implements Function<IExpr, IExpr> {
    @Override
    public IExpr apply(IExpr x) {
      // Rewrite Sech(u)^(2*n) -> (1 - Tanh(u)^2)^n
      // Rewrite Csch(u)^(2*n) -> (Coth(u)^2 - 1)^n
      if (x.isPower()) {
        IExpr base = x.base();
        IExpr exp = x.exponent();
        if (base.isAST1() && base.head().equals(S.Sech)) {
          int exponent = exp.toIntDefault();
          if (F.isPresent(exponent) && exponent > 0 && (exponent % 2) == 0) {
            IExpr arg = base.first();
            IExpr oneMinusTanh2 = F.Plus(F.C1, F.Negate(F.Power(F.Tanh(arg), F.C2)));
            if (exponent == 2) {
              return oneMinusTanh2;
            }
            return F.Power(oneMinusTanh2, F.ZZ(exponent / 2));
          }
        } else if (base.isAST1() && base.head().equals(S.Csch)) {
          int exponent = exp.toIntDefault();
          if (F.isPresent(exponent) && exponent > 0 && (exponent % 2) == 0) {
            IExpr arg = base.first();
            IExpr coth2MinusOne = F.Plus(F.Power(F.Coth(arg), F.C2), F.CN1);
            if (exponent == 2) {
              return coth2MinusOne;
            }
            return F.Power(coth2MinusOne, F.ZZ(exponent / 2));
          }
        }
      }
      return F.NIL;
    }
  }

  class CosSinTransform {
    private static final int DECISION = 0;
    private static final int SIN_ODD = 1;
    private static final int SIN_EVEN = 2;
    private static final int COS_ODD = 3;
    private static final int COS_EVEN = 4;

    private final Map<IExpr, int[]> statsMap;

    public CosSinTransform() {
      statsMap = new HashMap<>();
    }

    private int[] getStats(IExpr arg) {
      return statsMap.computeIfAbsent(arg, k -> new int[5]);
    }

    /**
     * If decision is 1 rewrite to Sin expression. If decision is -1 rewrite to Cos expression.
     */
    private void decideTransform() {
      statsMap.forEach((x, a) -> {
        a[DECISION] = 1;
        if (a[SIN_ODD] == 0) {
          if (a[COS_ODD] != 0) {
            a[DECISION] = -1;
          }
        }
      });
    }

    private void determineStatsRecursive(IExpr expr) {
      if (expr.isPower()) {
        int exponent = expr.exponent().toIntDefault();
        if (F.isPresent(exponent)) {
          IExpr base = expr.base();
          if (base.isCos() || base.isSin()) {
            addStatistics((IAST) base, exponent);
            // Skip power base recursion to prevent duplicate odd/even counting
            // over the same trig node. Recurse directly into arguments.
            determineStatsRecursive(((IAST) base).arg1());
            determineStatsRecursive(expr.exponent());
            return;
          }
        }
      }

      if (expr.isCos() || expr.isSin()) {
        addStatistics((IAST) expr, 1);
        determineStatsRecursive(((IAST) expr).arg1());
        return;
      }

      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        for (int i = 1; i <= ast.argSize(); i++) {
          determineStatsRecursive(ast.get(i));
        }
      }
    }

    /**
     * Add statistics entry for base ^ exponent * @param base
     * 
     * @param exponent
     */
    private void addStatistics(IAST base, int exponent) {
      if (base.isCos()) {
        int[] stats = getStats(base.arg1());
        if ((exponent % 2) == 0) {
          stats[COS_EVEN]++;
        } else {
          stats[COS_ODD]++;
        }
      } else if (base.isSin()) {
        int[] stats = getStats(base.arg1());
        if ((exponent % 2) == 0) {
          stats[SIN_EVEN]++;
        } else {
          stats[SIN_ODD]++;
        }
      }
    }

    private IExpr rewriteEvenCosSinFunctions(IExpr x) {
      if (x.isPowerInteger() && x.base().isAST1()) {
        IAST baseAST = (IAST) x.base();
        int[] array = statsMap.get(baseAST.arg1());
        if (array != null) {
          boolean rewriteToSin = array[DECISION] > 0;
          int exponent = x.exponent().toIntDefault();
          if (F.isPresent(exponent) && (exponent % 2) == 0) {
            if (rewriteToSin && baseAST.isCos()) {
              IExpr cosArg = baseAST.arg1();
              IExpr oneMinusSin2 = F.Plus(F.C1, F.Negate(F.Power(F.Sin(cosArg), F.C2)));
              if (exponent > 2) {
                return F.Power(oneMinusSin2, F.ZZ(exponent / 2));
              }
              return oneMinusSin2;
            }
            if (!rewriteToSin && baseAST.isSin()) {
              IExpr sinArg = baseAST.arg1();
              IExpr oneMinusCos2 = F.Plus(F.C1, F.Negate(F.Power(F.Cos(sinArg), F.C2)));
              if (exponent > 2) {
                return F.Power(oneMinusCos2, F.ZZ(exponent / 2));
              }
              return oneMinusCos2;
            }
          }
        }
      }
      return F.NIL;
    }

    public IExpr applyGlobal(IExpr x) {
      IExpr trigExpand = F.TrigExpand.of(x);
      if (trigExpand.isNIL()) {
        trigExpand = x;
      }
      determineStatsRecursive(trigExpand);
      decideTransform();
      return F.subst(trigExpand, f -> rewriteEvenCosSinFunctions(f));
    }
  }

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward
   * substitution).
   */
  private java.util.Map<ISymbol, IExpr> substitutedVariables =
      new IdentityHashMap<ISymbol, IExpr>();

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward
   * substitution).
   */
  private java.util.Map<ISymbol, IASTAppendable> variablesLCMAST = null;

  private java.util.Map<ISymbol, IInteger> variablesLCM = null;

  /**
   * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward
   * substitution).
   */
  private java.util.HashMap<IExpr, ISymbol> substitutedExpr = new HashMap<IExpr, ISymbol>();

  final private EvalEngine engine;
  final private VariablesSet originalVariables;

  /**
   * Forward and backward substitutions of expressions for polynomials. See <a href=
   * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
   * Homogenization</a> (page 112)
   *
   * @param listOfVariables names for the variables.
   * @param engine the evaluation engine
   */
  public PolynomialHomogenization(VariablesSet variablesSet, EvalEngine engine) {
    this.engine = engine;
    this.originalVariables = variablesSet;
  }

  /**
   * Lazy initialization for map symbol -> list-of-least-common-multiple-factors.
   *
   * @return
   */
  private Map<ISymbol, IASTAppendable> getSymbol2IntegerAST() {
    if (variablesLCMAST == null) {
      variablesLCMAST = new IdentityHashMap<ISymbol, IASTAppendable>();
    }
    return variablesLCMAST;
  }

  /**
   * Lazy initialization for map symbol -> least-common-multiple-factors.
   *
   * @return
   */
  private Map<ISymbol, IInteger> getSymbol2LCM() {
    if (variablesLCM == null) {
      variablesLCM = new IdentityHashMap<ISymbol, IInteger>();
    }
    return variablesLCM;
  }

  /**
   * Determine the least-common-multiple-factor associated with a symbol.
   *
   * @param x
   * @return
   */
  public IInteger getLCM(IExpr x) {
    if (variablesLCM == null) {
      return F.C1;
    }
    IInteger i = variablesLCM.get(x);
    if (i == null) {
      return F.C1;
    }
    return i;
  }

  /**
   * Forward substitution - transforming the expression into a polynomial expression by introducing
   * substitution variables. After transforming the polynomial expression may be solvable by a
   * polynomial factorization.
   *
   * @param expression
   * @return the polynomial expression
   */
  public IExpr replaceForward(final IExpr expression) {
    IExpr expr;
    if (expression.isFree(x -> x.isTrigFunction(), false)) {
      expr = F.subst(expression, PolynomialHomogenization::unifyIntegerPowers);
      // Normalize Log(v)/Log(c) -> Log(c,v) so Log-based homogenization works
      // even when Symja auto-evaluates Log(c,v) to Log(v)/Log(c) beforehand.
      expr = F.subst(expr, this::normalizeLogBase);
    } else {
      if (expression.isFree(x -> x.isCos() || x.isSin() || x.isAST(S.Sech, 2) || x.isAST(S.Csch, 2),
          false)) {
        expr = expression;
      } else if (!expression.isFree(x -> x.isCos() || x.isSin(), false)) {
        CosSinTransform transform = new CosSinTransform();
        expr = transform.applyGlobal(expression);
        expr = F.evalExpandAll(expr, engine);
      } else {
        expr = F.subst(expression, new TanhSechTransform());
        expr = F.evalExpandAll(expr, engine);
      }
    }
    determineLCM(expr);
    if (variablesLCMAST != null) {
      for (Map.Entry<ISymbol, IASTAppendable> entry : variablesLCMAST.entrySet()) {
        IASTAppendable denominatorLCMAST = entry.getValue();
        IInteger denominatorLCM = F.C1;
        if (denominatorLCMAST.isAST0()) {
        } else if (denominatorLCMAST.isAST1()) {
          denominatorLCM = (IInteger) denominatorLCMAST.arg1();
        } else {
          denominatorLCM = (IInteger) engine.evaluate(denominatorLCMAST);
        }
        if (!denominatorLCM.isOne()) {
          getSymbol2LCM().put(entry.getKey(), denominatorLCM);
        }
      }
    }
    return replaceForwardRecursive(expr);
  }

  /**
   * Unify powers in two steps like 3^(2+2*x) to 3^2 * 3^(2*x). Merge powers like 2^(2*x)*3^(2*x) to
   * 6^(2*x) * @param x
   * 
   * @return
   */
  private static IExpr unifyIntegerPowers(IExpr x) {
    if (x.isTimes()) {
      IAST timesAST = (IAST) x;
      boolean evaled = false;
      // first step
      IASTAppendable times = F.TimesAlloc(timesAST.argSize());
      for (int i = 1; i <= timesAST.argSize(); i++) {
        IExpr arg = timesAST.get(i);
        if (arg.isPower() && arg.base().isInteger()) {
          IInteger base = (IInteger) arg.base();
          if (base.isPositive()) {
            IExpr exp = arg.exponent();

            if (exp.isPlus() && exp.first().isInteger() && exp.first().isPositive()) {
              evaled = true;
              IExpr rest = exp.rest().oneIdentity1();
              times.append(base.pow(exp.first()));
              times.append(F.Power(base, rest));
              continue;
            }
          }
        }
        times.append(arg);
      }

      // second step
      Map<IExpr, IInteger> exponentMap = new TreeMap<IExpr, IInteger>();
      IASTAppendable timesMapped = F.TimesAlloc(times.argSize());
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isPower() && arg.base().isInteger() && arg.base().isPositive()) {
          evaled = true;
          IExpr exponent = arg.exponent();
          IInteger value = exponentMap.get(exponent);
          if (value != null) {
            value = value.multiply((IInteger) arg.base());
          } else {
            value = (IInteger) arg.base();
          }
          exponentMap.put(exponent, value);
        } else {
          timesMapped.append(arg);
        }
      }
      if (evaled) {
        for (Map.Entry<IExpr, IInteger> entry : exponentMap.entrySet()) {
          IExpr key = entry.getKey();
          IInteger val = entry.getValue();
          timesMapped.append(F.Power(val, key));
        }
        timesMapped.sortInplace();
        return timesMapped.oneIdentity1();
      }
    }
    return F.NIL;
  }

  /**
   * Forward substitution - transforming the numerator and denominator expression into polynomial
   * expressions by introducing substitution variables. After transforming the polynomial expression
   * may be solvable by a polynomial factorization.
   *
   * @param numerator
   * @param denominator
   * @return polynomial numerator at index '0'; and polynomial denominator at index '1'
   */
  public IExpr[] replaceForward(final IExpr numerator, final IExpr denominator) {
    determineLCM(numerator);
    determineLCM(denominator);
    if (variablesLCMAST != null) {
      for (Map.Entry<ISymbol, IASTAppendable> entry : variablesLCMAST.entrySet()) {
        IASTAppendable denominatorLCMAST = entry.getValue();
        IInteger denominatorLCM = F.C1;
        if (denominatorLCMAST.isAST0()) {
        } else if (denominatorLCMAST.isAST1()) {
          denominatorLCM = (IInteger) denominatorLCMAST.arg1();
        } else {
          denominatorLCM = (IInteger) engine.evaluate(denominatorLCMAST);
        }
        if (!denominatorLCM.isOne()) {
          getSymbol2LCM().put(entry.getKey(), denominatorLCM);
        }
      }
    }

    IExpr[] result = new IExpr[2];
    result[0] = replaceForwardRecursive(numerator);
    result[1] = replaceForwardRecursive(denominator);
    return result;
  }

  private void determineLCM(final IExpr expression) {
    if (expression instanceof IAST) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        for (int i = 1; i <= ast.argSize(); i++) {
          determineLCM(ast.get(i));
        }
        return;
      } else if (ast.isPower()) {
        IExpr exp = ast.exponent();
        IExpr base = ast.base();
        if (exp.isReal()) {

          IInteger lcm = F.C1;
          IRational rat = ((IReal) exp).rationalFactor();
          if (rat == null) {
            return;
          }
          if (!rat.isInteger()) {
            IInteger denominator = rat.denominator();
            if (denominator.isNegative()) {
              denominator = denominator.negate();
            }
            lcm = denominator;
          }
          replaceExpressionLCM(base, lcm);
          return;
        }
        if (exp.isTimes()) {
          determineTimes(ast, base, (IAST) exp);
          return;
        } else if (exp.isPlus()) {
          IAST plusAST = (IAST) exp;
          if (plusAST.first().isInteger()) {
            determineLCM(S.Power.of(base, plusAST.first()));
            determineLCM(S.Power.of(base, plusAST.rest().oneIdentity0()));
            return;
          }
        }
        replaceExpressionLCM(ast, F.C1);
        return;
      }
      replaceExpressionLCM(expression, F.C1);
      return;
    }
    if (expression instanceof ISymbol) {
      replaceExpressionLCM(expression, F.C1);
    }
  }

  private void determineTimes(final IAST ast, final IExpr base, IAST timesExponent) {
    IExpr first = timesExponent.first();
    if (first.isComplex() && ((IComplex) first).reRational().isZero()) {
      IRational pureImPart = ((IComplex) first).imRational();
      int exponent = pureImPart.toIntDefault();
      if (F.isNotPresent(exponent)) {
        replaceExpressionLCM(ast, F.C1);
        return;
      } else if (exponent > 0) {
        IASTMutable restExponent = timesExponent.setAtCopy(1, F.CI);
        replaceExpressionLCM(base.power(restExponent), F.C1);
        return;
      }
      replaceExpressionLCM(ast, F.C1);
      return;
    }
    int exponent = first.toIntDefault();
    if (F.isNotPresent(exponent)) {
      replaceExpressionLCM(ast, F.C1);
      return;
    } else if (exponent > 0 || base.isNumericFunction()) {
      IExpr rest = timesExponent.rest().oneIdentity1();
      replaceExpressionLCM(base.power(rest), F.C1);
      return;
    }
    replaceExpressionLCM(ast, F.C1);
  }

  /**
   * Forward substitution - transforming the expression into a polynomial expression by introducing
   * substitution variables. After transforming the polynomial expression may be solvable by a
   * polynomial factorization.
   *
   * @param expression
   * @return
   * @throws ArithmeticException
   * @throws ClassCastException
   */
  private IExpr replaceForwardRecursive(final IExpr expression)
      throws ArithmeticException, ClassCastException {
    if (expression instanceof IAST) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        IASTAppendable newAST = F.ast(ast.head(), ast.argSize() + 1);
        IExpr temp = replaceForwardRecursive(ast.arg1());
        if (temp.isNIL()) {
          return F.NIL;
        }
        newAST.append(temp);
        for (int i = 2; i <= ast.argSize(); i++) {
          temp = replaceForwardRecursive(ast.get(i));
          if (temp.isNIL()) {
            return F.NIL;
          }
          newAST.append(temp);
        }
        return newAST;
      } else if (ast.isPower()) {
        final IExpr power = replaceExpression(ast);
        if (power.isPresent()) {
          return power;
        }
        final IExpr b = ast.base();
        IExpr exp = ast.exponent();
        if (exp.isReal()) {
          IExpr base = replacePower(b, (IReal) exp);
          if (base.isPresent()) {
            return base;
          }
        }
        IExpr base = b;

        if (exp.isTimes()) {
          return replaceTimes(ast, base, exp);
        } else if (exp.isPlus()) {
          IAST plusAST = (IAST) exp;
          if (plusAST.first().isInteger()) {
            IExpr coefficient = S.Power.of(base, plusAST.first());
            IExpr a1 = replaceForwardRecursive(coefficient);
            if (a1.isNIL()) {
              return F.NIL;
            }
            IExpr a2 = replaceForwardRecursive(F.Power(base, plusAST.rest().oneIdentity0()));
            if (a2.isNIL()) {
              return F.NIL;
            }
            return F.Times(a1, a2);
          }
        }

        return ast;
      }
      return replaceExpression(expression);
    }
    if (expression.isSymbol()) {
      return replaceExpression(expression).orElse(expression);
    }
    return expression;
  }

  private IExpr replaceTimes(final IAST ast, final IExpr base, IExpr exp) {
    IExpr first = exp.first();
    if (first.isComplex() && ((IComplex) first).reRational().isZero()) {
      IRational imPart = ((IComplex) first).imRational();
      int exponent = imPart.toIntDefault();
      if (F.isNotPresent(exponent)) {
        return replaceExpression(ast).orElse(ast);
      } else if (exponent > 0) {
        IASTMutable restExponent = ((IAST) exp).setAtCopy(1, F.CI);
        return F.Power(replaceExpression(base.power(restExponent)), exponent);
      }
      return replaceExpression(ast);
    }
    int exponent = first.toIntDefault();
    if (F.isNotPresent(exponent)) {
      return replaceExpression(ast);
    }
    if (exponent > 0 || base.isNumericFunction()) {
      final IExpr rest = exp.rest().oneIdentity1();
      return F.Power(replaceExpression(base.power(rest)), exponent);
    }
    return replaceExpression(ast).orElse(ast);
  }

  private IExpr replaceExpressionLCM(final IExpr exprPoly, IInteger lcm) {
    if (exprPoly.isAST() || exprPoly.isSymbol()) {
      ISymbol newSymbol = substitutedExpr.get(exprPoly);
      if (newSymbol != null) {
        if (!lcm.isOne()) {
          IASTAppendable ast = getSymbol2IntegerAST().get(newSymbol);
          if (ast == null) {
            IASTAppendable list = F.ast(S.LCM);
            list.append(lcm);
            getSymbol2IntegerAST().put(newSymbol, list);
          } else {
            ast.append(lcm);
          }
        }
        return newSymbol;
      }
      newSymbol = F.Dummy(EvalEngine.uniqueName("jas$"));
      substitutedVariables.put(newSymbol, exprPoly);
      substitutedExpr.put(exprPoly, newSymbol);

      if (!lcm.isOne()) {
        IASTAppendable list = F.ast(S.LCM);
        list.append(lcm);
        getSymbol2IntegerAST().put(newSymbol, list);
      }

      return newSymbol;
    }
    return exprPoly;
  }

  private IExpr replaceExpression(final IExpr exprPoly) {
    ISymbol symbol = substitutedExpr.get(exprPoly);
    if (symbol != null) {
      IInteger lcm = getLCM(symbol);
      if (lcm.isOne()) {
        return symbol;
      }
      return F.Power(symbol, lcm);
    }
    return F.NIL;
  }

  private IExpr replacePower(final IExpr exprPoly, IReal exp) {
    ISymbol symbol = substitutedExpr.get(exprPoly);
    if (symbol != null) {
      IInteger lcm = getLCM(symbol);
      if (lcm.isOne()) {
        lcm = F.C1;
      }
      if (lcm.isOne() && exp.isInteger()) {
        return F.Power(symbol, exp);
      }

      IRational rat = exp.rationalFactor();
      if (rat != null) {
        IInteger intExp = rat.multiply(lcm).numerator();
        int exponent = intExp.toIntDefault();
        if (F.isPresent(exponent)) {
          if (exponent == 1) {
            return symbol;
          }
          return F.Power(symbol, exponent);
        }
      }
      return F.Power(symbol, F.Times(lcm, exp));
    }
    return F.NIL;
  }

  /**
   * Backward substitution - transforming the expression back by replacing the introduced
   * substitution variables.
   *
   * @param expression
   * @return
   * @see #replaceForward(IExpr)
   */
  public IExpr replaceBackward(final IExpr expression) {
    IExpr temp = F.subst(expression, x -> {
      if (x.isSymbol()) {
        IExpr t = substitutedVariables.get(x);
        if (t != null) {
          IInteger denominatorLCM = getLCM(x);
          if (denominatorLCM.isOne()) {
            return t;
          }
          return F.Power(t, F.fraction(F.C1, denominatorLCM));
        }
      }
      return F.NIL;
    });
    return engine.evaluate(temp);
  }

  /**
   * Backward substitution - transforming the symbol back by reading the denominator LCM and
   * returning factor ^ denominatorLCM * @param symbol this symbol must be replaced by another
   * symbol and associated denominator LCM
   * 
   * @param resultValue
   * @return
   */
  public IExpr replaceDenominatorBackwardLCM(final ISymbol symbol, IExpr resultValue) {
    final IExpr t = substitutedVariables.get(symbol);
    if (t != null) {
      final IInteger denominatorLCM = getLCM(symbol);
      if (t.isSymbol()) {
        if (denominatorLCM.isOne()) {
          return resultValue;
        }
        return F.Power(resultValue, denominatorLCM);
      } else {
        final VariablesSet varSet = new VariablesSet(t);
        if (varSet.size() == 1) {
          IExpr solveVar = varSet.firstVariable();
          if (t.isAST1() && t.head().isSymbol() && t.first().equals(solveVar)) {
            final IExpr reduced =
                ReduceVariableEqual.reduce(F.Equal(t, resultValue), solveVar, false);
            if (reduced.isPresent()) {
              if (denominatorLCM.isOne()) {
                return reduced;
              }
              return F.Power(reduced, denominatorLCM);
            }
          }
        }
      }
    }
    return F.NIL;

  }

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward substitution)
   * returned in a IdentityHashMap.
   */
  public java.util.Map<ISymbol, IExpr> substitutedVariables() {
    return substitutedVariables;
  }

  public java.util.Set<ISymbol> substitutedVariablesSet() {
    return substitutedVariables.keySet();
  }

  public int size() {
    return substitutedVariables.size();
  }

  /**
   * Return a list of rules containing the backward substitutions, of the dummy variables
   *
   * @return
   */
  public IASTAppendable listOfBackwardSubstitutions() {
    Map<ISymbol, IExpr> map = substitutedVariables();
    IASTAppendable list = F.ListAlloc(size());
    for (Map.Entry<ISymbol, IExpr> x : map.entrySet()) {
      IExpr value = x.getValue();
      if (value != null) {
        IExpr key = x.getKey();
        IInteger denominatorLCM = getLCM(key);
        if (denominatorLCM.isOne()) {
          list.append(F.Rule(key, value));
        } else {
          list.append(F.Rule(key, F.Power(value, F.fraction(F.C1, denominatorLCM))));
        }
      }
    }
    return list;
  }

  /**
   * Normalize change-of-base log forms within a Times expression:
   * <ul>
   * <li>{@code Log(v) * Log(c)^(-1)  ->  Log(c, v)} (i.e. log_c(v))</li>
   * <li>{@code Log(c) * Log(v)^(-1)  ->  Log(c, v)^(-1)} (i.e. 1/log_c(v))</li>
   * </ul>
   * where {@code c} is a numeric constant and {@code v} depends on the variables.
   *
   * @param expr a node from the expression tree
   * @return the rewritten node, or {@link F#NIL} if no rewrite applied
   */
  private IExpr normalizeLogBase(IExpr expr) {
    if (!expr.isTimes()) {
      return F.NIL;
    }
    final IAST timesAST = (IAST) expr;
    int varLogIdx = -1;
    int constLogInvIdx = -1;
    int constLogIdx = -1;
    int varLogInvIdx = -1;

    for (int i = 1; i <= timesAST.argSize(); i++) {
      IExpr arg = timesAST.get(i);
      if (arg.isAST(S.Log, 2)) {
        IExpr logArg = arg.first();
        if (logArg.isNumericFunction()) {
          if (constLogIdx < 0)
            constLogIdx = i;
        } else {
          if (varLogIdx < 0)
            varLogIdx = i;
        }
      }
      else if (arg.isPower() && arg.exponent().isMinusOne() && arg.base().isAST(S.Log, 2)) {
        IExpr logArg = arg.base().first();
        if (logArg.isNumericFunction()) {
          if (constLogInvIdx < 0)
            constLogInvIdx = i;
        } else {
          if (varLogInvIdx < 0)
            varLogInvIdx = i;
        }
      }
    }

    if (varLogIdx >= 0 && constLogInvIdx >= 0) {
      final IExpr varExpr = timesAST.get(varLogIdx).first();
      final IExpr constExpr = timesAST.get(constLogInvIdx).base().first();
      IASTAppendable newTimes = F.TimesAlloc(timesAST.argSize() - 1);
      for (int i = 1; i <= timesAST.argSize(); i++) {
        if (i != varLogIdx && i != constLogInvIdx) {
          newTimes.append(timesAST.get(i));
        }
      }
      newTimes.append(F.Log(constExpr, varExpr));
      return newTimes.oneIdentity1();
    }

    if (constLogIdx >= 0 && varLogInvIdx >= 0) {
      final IExpr constExpr = timesAST.get(constLogIdx).first();
      final IExpr varExpr = timesAST.get(varLogInvIdx).base().first();
      IASTAppendable newTimes = F.TimesAlloc(timesAST.argSize() - 1);
      for (int i = 1; i <= timesAST.argSize(); i++) {
        if (i != constLogIdx && i != varLogInvIdx) {
          newTimes.append(timesAST.get(i));
        }
      }
      newTimes.append(F.Power(F.Log(constExpr, varExpr), F.CN1));
      return newTimes.oneIdentity1();
    }

    return F.NIL;
  }

}