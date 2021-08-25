package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
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
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Forward and backward substitutions of expressions for polynomials. See <a
 * href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
 * Homogenization</a>
 */
public class PolynomialHomogenization {

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

  private EvalEngine engine;

  /**
   * Forward and backward substitutions of expressions for polynomials. See <a
   * href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
   * Homogenization</a> (page 112)
   *
   * @param listOfVariables names for the variables.
   * @param engine the evaluation engine
   */
  public PolynomialHomogenization(IAST listOfVariables, EvalEngine engine) {
    this.engine = engine;
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
    determineLCM(expression);
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
    return replaceForwardRecursive(expression);
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
          IRational rat = ((ISignedNumber) exp).rationalFactor();
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
      int exponent = pureImPart.toIntDefault(Integer.MIN_VALUE);
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
    int exponent = first.toIntDefault(Integer.MIN_VALUE);
    if (exponent == Integer.MIN_VALUE) {
      replaceExpressionLCM(ast, F.C1);
      return;
    } else if (exponent > 0) {
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
        IASTAppendable newAST = F.ast(ast.head(), ast.size(), false);
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
          IExpr base = replacePower(b, (ISignedNumber) exp);
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
            return F.Times(
                replaceForwardRecursive(coefficient),
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
      int exponent = imPart.toIntDefault(Integer.MIN_VALUE);
      if (exponent == Integer.MIN_VALUE) {
        return replaceExpression(ast).orElse(ast);
      } else if (exponent > 0) {
        IASTMutable restExponent = ((IAST) exp).setAtCopy(1, F.CI);
        return F.Power(replaceExpression(base.power(restExponent)), exponent);
      }
      return replaceExpression(ast);
    }
    int exponent = first.toIntDefault(Integer.MIN_VALUE);
    if (exponent == Integer.MIN_VALUE) {
      return replaceExpression(ast);
    } else if (exponent > 0) {
      IExpr rest = exp.rest().oneIdentity1();
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
      newSymbol = F.Dummy(engine.uniqueName("jas$"));
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

  private IExpr replacePower(final IExpr exprPoly, ISignedNumber exp) {
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
        int exponent = intExp.toIntDefault(Integer.MIN_VALUE);
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
   * Backward substitution - transforming the expression back by replacing the introduce
   * &quot;substitution variables&quot;.
   *
   * @param expression
   * @return
   * @see #replaceForward(IExpr)
   */
  public IExpr replaceBackward(final IExpr expression) {
    IExpr temp =
        F.subst(
            expression,
            x -> {
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
