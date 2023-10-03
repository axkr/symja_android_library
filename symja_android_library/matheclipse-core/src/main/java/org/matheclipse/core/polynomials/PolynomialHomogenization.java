package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
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

  private class CosSinTransform implements Function<IExpr, IExpr> {
    private static final int DECISION = 0;
    private static final int SIN_ODD = 1;
    private static final int SIN_EVEN = 2;
    private static final int COS_ODD = 3;
    private static final int COS_EVEN = 4;

    Map<IExpr, int[]> statsMap;

    public CosSinTransform() {
      statsMap = toMap(originalVariables.getVariablesSet());
    }

    private Map<IExpr, int[]> toMap(Set<IExpr> set) {
      Map<IExpr, int[]> map = new HashMap<IExpr, int[]>();
      set.forEach(t -> {
        int[] statsArray = new int[5];
        map.put(t, statsArray);
      });
      return map;
    }

    /**
     * If decision is <code>1</code> rewrite to <code>Sin</code> expressio. If decision is
     * <code>-1</code> rewrite to <code>Cos</code> expression.
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
      if (expr.isAST()) {
        if (expr.isPower()) {
          int exponent = expr.exponent().toIntDefault();
          if (exponent != Integer.MIN_VALUE) {
            IExpr base = expr.base();
            if (base.isAST1()) {
              addStatistics((IAST) base, exponent);
            }
          }
        } else if (expr.isCos() || expr.isSin()) {
          addStatistics((IAST) expr, 1);
        } else {
          IAST ast = (IAST) expr;
          for (int i = 1; i < ast.size(); i++) {
            determineStatsRecursive(ast.get(i));
          }
        }
      }
    }

    /**
     * Add statistics entry for <code>base ^ exponent</code>
     * 
     * @param base
     * @param exponent
     */
    private void addStatistics(IAST base, int exponent) {
      int[] stats = statsMap.get(base.arg1());
      if (stats != null) {
        if ((exponent % 2) == 0) {
          // even exponent number
          if (base.isCos()) {
            stats[COS_EVEN]++;
          } else if (base.isSin()) {
            stats[SIN_EVEN]++;
          }
        } else {
          // odd exponent number
          if (base.isCos()) {
            stats[COS_ODD]++;
          } else if (base.isSin()) {
            stats[SIN_ODD]++;
          }
        }
      }
    }

    private IExpr rewriteEvenCosSinFunctions(IExpr x) {
      if (x.isPower() && x.exponent().isInteger() && x.base().isAST1()) {
        int[] array = statsMap.get(x.base().first());
        if (array != null) {
          boolean rewriteToSin = array[DECISION] > 0;
          if (rewriteToSin && x.base().isCos()) {
            // rewrite to Sin expression
            int exponent = x.exponent().toIntDefault();
            if (exponent != Integer.MIN_VALUE && (exponent % 2) == 0) {
              IAST cosAST = (IAST) x.base();
              IExpr cosArg = cosAST.arg1();
              if (exponent > 2) {
                return F.Power(F.Plus(F.C1, F.Negate(F.Power(F.Sin(cosArg), F.C2))),
                    F.ZZ(exponent / 2));
              }
              return F.Plus(F.C1, F.Negate(F.Power(F.Sin(cosArg), F.C2)));
            }
          }
          if (!rewriteToSin && x.base().isSin()) {
            // rewrite to Cos expression
            int exponent = x.exponent().toIntDefault();
            if (exponent != Integer.MIN_VALUE && (exponent % 2) == 0) {
              IAST sinAST = (IAST) x.base();
              IExpr sinArg = sinAST.arg1();
              if (exponent > 2) {
                return F.Power(F.Plus(F.C1, F.Negate(F.Power(F.Cos(sinArg), F.C2))),
                    F.ZZ(exponent / 2));
              }
              return F.Plus(F.C1, F.Negate(F.Power(F.Cos(sinArg), F.C2)));
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr apply(IExpr x) {
      if (x.isTrigFunction()) {
        IExpr trigExpand = F.TrigExpand.of(x);
        if (!trigExpand.equals(x)) {
          determineStatsRecursive(trigExpand);
          decideTransform();
          IExpr subst = F.subst(trigExpand, f -> rewriteEvenCosSinFunctions(f));
          return subst;
        }
      }
      return F.NIL;
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
   * Lazy initialization for map <code>symbol -> list-of-least-common-multiple-factors</code>.
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
   * Lazy initialization for map <code>symbol -> least-common-multiple-factors</code>.
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
   * Determine the <code>least-common-multiple-factor </code> associated with a symbol.
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
   * &quot;substitution variables&quot;. After transforming the polynomial expression may be
   * solvable by a polynomial factorization.
   *
   * @param expression
   * @return the polynomial expression
   */
  public IExpr replaceForward(final IExpr expression) {
    IExpr expr;
    if (expression.isFree(x -> x.isTrigFunction(), false)) {
      expr = F.subst(expression, PolynomialHomogenization::unifyIntegerPowers);
    } else {
      if (expression.isFree(x -> x.isCos() || x.isSin(), false)) {
        // expr = F.eval(F.TrigToExp(expression));
        expr = expression;
      } else {
        expr = F.subst(expression, new CosSinTransform());
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
   * Unify powers in two steps like <code>3^(2+2*x)</code> to <code>3^2 * 3^(2*x)</code>. Merge
   * powers like <code>2^(2*x)*3^(2*x)</code> to <code>6^(2*x)</code>
   * 
   * @param x
   * @return
   */
  private static IExpr unifyIntegerPowers(IExpr x) {
    if (x.isTimes()) {
      IAST timesAST = (IAST) x;
      boolean evaled = false;
      // first step
      IASTAppendable times = F.TimesAlloc(timesAST.argSize());
      for (int i = 1; i < timesAST.size(); i++) {
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
      for (int i = 1; i < times.size(); i++) {
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
   * expressions by introducing &quot;substitution variables&quot;. After transforming the
   * polynomial expression may be solvable by a polynomial factorization.
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
        for (int i = 1; i < ast.size(); i++) {
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
          // if (base.isTimes()) {
          //
          // }
          replaceExpressionLCM(base, lcm);
          return;
        }
        if (exp.isTimes()) {
          determineTimes(ast, base, (IAST) exp);
          // ((IAST) exp).forEach(x -> determineLCM(F.Power(base, x)));
          return;
        } else if (exp.isPlus()) { // && base.isExactNumber()) {
          // ex: 4^(2*x+3)
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
      if (exponent == Integer.MIN_VALUE) {
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
    if (exponent == Integer.MIN_VALUE) {
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
   * &quot;substitution variables&quot;. After transforming the polynomial expression may be
   * solvable by a polynomial factorization.
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
        IASTAppendable newAST = F.ast(ast.head(), ast.size());
        IExpr temp = ast.arg1();
        newAST.append(replaceForwardRecursive(temp));
        for (int i = 2; i < ast.size(); i++) {
          temp = ast.get(i);
          newAST.append(replaceForwardRecursive(temp));
        }
        return newAST;
      } else if (ast.isPower()) {
        IExpr power = replaceExpression(ast);
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
        } else if (exp.isPlus()) { // && base.isExactNumber()) {
          // ex: 4^(2*x+3)
          IAST plusAST = (IAST) exp;
          if (plusAST.first().isInteger()) {
            IExpr coefficient = S.Power.of(base, plusAST.first());
            return F.Times(replaceForwardRecursive(coefficient),
                replaceForwardRecursive(F.Power(base, plusAST.rest().oneIdentity0())));
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
      if (exponent == Integer.MIN_VALUE) {
        return replaceExpression(ast).orElse(ast);
      } else if (exponent > 0) {
        IASTMutable restExponent = ((IAST) exp).setAtCopy(1, F.CI);
        return F.Power(replaceExpression(base.power(restExponent)), exponent);
      }
      return replaceExpression(ast);
    }
    int exponent = first.toIntDefault();
    if (exponent == Integer.MIN_VALUE) {
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
        if (exponent != Integer.MIN_VALUE) {
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
   * &quot;substitution variables&quot;.
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
   * returning <code>factor ^ denominatorLCM</code>
   * 
   * @param symbol this symbol must be replaced by another symbol and associated denominator LCM
   * @param resultValue
   * @return
   */
  public IExpr replaceDenominatorBackwardLCM(final ISymbol symbol, IExpr resultValue) {
    IExpr t = substitutedVariables.get(symbol);
    if (t != null && t.isSymbol()) {
      IInteger denominatorLCM = getLCM(symbol);
      if (denominatorLCM.isOne()) {
        return resultValue;
      }
      return F.Power(resultValue, denominatorLCM);
    }
    return F.NIL;
  }

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward substitution)
   * returned in a <code>IdentityHashMap</code>.
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
   * Return a list of rules containing the backward substitutions, of the &quot;dummy
   * variables&quot;
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
}
