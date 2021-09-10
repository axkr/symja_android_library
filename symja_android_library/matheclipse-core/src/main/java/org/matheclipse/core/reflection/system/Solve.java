package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.CreamConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.QuarticSolver;

/**
 *
 *
 * <pre>
 * Solve(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>attempts to solve <code>equations</code> for the variables <code>vars</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Solve(equations, vars, domain)
 * </pre>
 *
 * <blockquote>
 *
 * <p>attempts to solve <code>equations</code> for the variables <code>vars</code> in the given
 * <code>domain</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Solve({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;2,y-&gt;2},{x-&gt;2,y-&gt;-2},{x-&gt;-2,y-&gt;2*2^(1/2)},{x-&gt;-2,y-&gt;(-2)*2^(1/2)}}
 *
 * &gt;&gt; Solve({2 x + 3*y == 4, 3*x - 4*y &lt;= 5,x - 2*y &gt; -21}, {x,  y}, Integers)
 * {{x-&gt;-7,y-&gt;6},{x-&gt;-4,y-&gt;4},{x-&gt;-1,y-&gt;2}}
 *
 * &gt;&gt; Solve(Xor(a, b, c, d) &amp;&amp; (a || b) &amp;&amp; ! (c || d), {a, b, c, d}, Booleans)
 * {{a-&gt;False,b-&gt;True,c-&gt;False,d-&gt;False},{a-&gt;True,b-&gt;False,c-&gt;False,d-&gt;False}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p><a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>, <a
 * href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>, <a
 * href="NRoots.md">NRoots</a>
 */
public class Solve extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Analyze an expression, if it has linear, polynomial or other form. */
  protected static class ExprAnalyzer implements Comparable<ExprAnalyzer> {

    /** A linear expression for the given variables. */
    public static final int LINEAR = 0;
    /** A polynomial expression for the given variables. */
    public static final int POLYNOMIAL = 1;
    /** Others type of expression for the given variables. */
    public static final int OTHERS = 2;

    /** LINEAR, POLYNOMIAL or OTHERS */
    private int fEquationType;

    /** The expression which should be <code>0</code>. */
    private IExpr fExpr;

    /** The original expression if unequal <code>null</code>. */
    private IExpr fOriginalExpr = null;

    /** The numerator of the expression */
    private IExpr fNumerator;

    /** The denominator of the expression */
    private IExpr fDenominator;

    /**
     * The number of leaves in an expression, used as an indicator for the complexity of the
     * expression.
     */
    private long fLeafCount;

    private HashSet<ISymbol> fSymbolSet;
    private IASTAppendable fMatrixRow;
    private IASTAppendable fPlusAST;

    final IAST fListOfVariables;
    final EvalEngine fEngine;

    public ExprAnalyzer(IExpr expr, IAST listOfVariables, EvalEngine engine) {
      super();
      this.fEngine = engine;
      this.fExpr = expr;
      this.fNumerator = expr;
      this.fDenominator = F.C1;
      if (this.fExpr.isAST()) {
        splitNumeratorDenominator((IAST) this.fExpr);
      }
      this.fListOfVariables = listOfVariables;
      this.fSymbolSet = new HashSet<ISymbol>();
      this.fLeafCount = 0;
      reset();
    }

    /** Analyze an expression, if it has linear, polynomial or other form. */
    private void analyze(IExpr eqExpr) {
      if (eqExpr.isFree(Predicates.in(fListOfVariables), true)) {
        fLeafCount++;
        fPlusAST.append(eqExpr);
      } else if (eqExpr.isPlus()) {
        fLeafCount++;
        IAST plusAST = (IAST) eqExpr;
        IExpr expr;
        for (int i = 1; i < plusAST.size(); i++) {
          expr = plusAST.get(i);
          if (expr.isFree(Predicates.in(fListOfVariables), true)) {
            fLeafCount++;
            fPlusAST.append(expr);
          } else {
            getPlusArgumentEquationType(expr);
          }
        }
      } else {
        getPlusArgumentEquationType(eqExpr);
      }
    }

    @Override
    public int compareTo(ExprAnalyzer o) {
      if (fSymbolSet.size() != o.fSymbolSet.size()) {
        return (fSymbolSet.size() < o.fSymbolSet.size()) ? -1 : 1;
      }
      if (fEquationType != o.fEquationType) {
        return (fEquationType < o.fEquationType) ? -1 : 1;
      }
      if (fLeafCount != o.fLeafCount) {
        return (fLeafCount < o.fLeafCount) ? -1 : 1;
      }
      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ExprAnalyzer other = (ExprAnalyzer) obj;
      if (fDenominator == null) {
        if (other.fDenominator != null) return false;
      } else if (!fDenominator.equals(other.fDenominator)) return false;
      if (fEquationType != other.fEquationType) return false;
      if (fExpr == null) {
        if (other.fExpr != null) return false;
      } else if (!fExpr.equals(other.fExpr)) return false;
      if (fLeafCount != other.fLeafCount) return false;
      if (fMatrixRow == null) {
        if (other.fMatrixRow != null) return false;
      } else if (!fMatrixRow.equals(other.fMatrixRow)) return false;
      if (fNumerator == null) {
        if (other.fNumerator != null) return false;
      } else if (!fNumerator.equals(other.fNumerator)) return false;
      if (fPlusAST == null) {
        if (other.fPlusAST != null) return false;
      } else if (!fPlusAST.equals(other.fPlusAST)) return false;
      if (fSymbolSet == null) {
        if (other.fSymbolSet != null) return false;
      } else if (!fSymbolSet.equals(other.fSymbolSet)) return false;
      if (fListOfVariables == null) {
        if (other.fListOfVariables != null) return false;
      } else if (!fListOfVariables.equals(other.fListOfVariables)) return false;
      return true;
    }

    public IExpr getDenominator() {
      return fDenominator;
    }

    /** @return the expr */
    public IExpr getExpr() {
      return fExpr;
    }

    public int getNumberOfVars() {
      return fSymbolSet.size();
    }

    public IExpr getNumerator() {
      return fNumerator;
    }

    /**
     * Get the argument type.
     *
     * @param eqExpr
     */
    private void getPlusArgumentEquationType(IExpr eqExpr) {
      if (eqExpr.isTimes()) {
        ISymbol sym = null;
        fLeafCount++;
        IAST arg = (IAST) eqExpr;
        IExpr expr;
        for (int i = 1; i < arg.size(); i++) {
          expr = arg.get(i);
          if (expr.isFree(Predicates.in(fListOfVariables), true)) {
            fLeafCount++;
          } else if (expr.isSymbol()) {
            fLeafCount++;
            for (int j = 1; j < fListOfVariables.size(); j++) {
              if (fListOfVariables.get(j).equals(expr)) {
                fSymbolSet.add((ISymbol) expr);
                if (sym != null) {
                  if (fEquationType == LINEAR) {
                    fEquationType = POLYNOMIAL;
                  }
                } else {
                  sym = (ISymbol) expr;
                  if (fEquationType == LINEAR) {
                    IAST cloned = arg.splice(i);
                    fMatrixRow.set(j, F.Plus(fMatrixRow.get(j), cloned));
                  }
                }
              }
            }
          } else if (expr.isPower()
              && (expr.base().isInteger() || expr.exponent().isNumIntValue())) {
            if (fEquationType == LINEAR) {
              fEquationType = POLYNOMIAL;
            }
            getTimesArgumentEquationType(expr.base());
          } else {
            fLeafCount += eqExpr.leafCount();
            if (fEquationType <= POLYNOMIAL) {
              fEquationType = OTHERS;
            }
          }
        }
        if (fEquationType == LINEAR && sym == null) {
          // should never happen??
          System.err.println("sym == null???");
        }
      } else {
        getTimesArgumentEquationType(eqExpr);
      }
    }

    /** @return the row */
    public IAST getRow() {
      return fMatrixRow;
    }

    /** @return the symbolSet */
    public Set<ISymbol> getSymbolSet() {
      return fSymbolSet;
    }

    private void getTimesArgumentEquationType(IExpr expr) {
      if (expr.isSymbol()) {
        fLeafCount++;
        int position = fListOfVariables.indexOf(expr);
        if (position > 0) {
          fSymbolSet.add((ISymbol) expr);
          if (fEquationType == LINEAR) {
            fMatrixRow.set(position, F.Plus(fMatrixRow.get(position), F.C1));
          }
        }
        return;
      }
      if (expr.isFree(Predicates.in(fListOfVariables), true)) {
        fLeafCount++;
        fPlusAST.append(expr);
        return;
      }
      if (expr.isPower()) {
        IExpr base = expr.base();
        IExpr exponent = expr.exponent();
        if (exponent.isInteger()) {
          if (fEquationType == LINEAR) {
            fEquationType = POLYNOMIAL;
          }
          getTimesArgumentEquationType(base);
          return;
        }
        if (exponent.isNumIntValue()) {
          if (fEquationType == LINEAR) {
            fEquationType = POLYNOMIAL;
          }
          getTimesArgumentEquationType(base);
          return;
        }
      }
      fLeafCount += expr.leafCount();
      if (fEquationType <= POLYNOMIAL) {
        fEquationType = OTHERS;
      }
    }

    /** @return the value */
    public IExpr getValue() {
      return fPlusAST.oneIdentity0();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fDenominator == null) ? 0 : fDenominator.hashCode());
      result = prime * result + fEquationType;
      result = prime * result + ((fExpr == null) ? 0 : fExpr.hashCode());
      result = prime * result + (int) (fLeafCount ^ (fLeafCount >>> 32));
      result = prime * result + ((fMatrixRow == null) ? 0 : fMatrixRow.hashCode());
      result = prime * result + ((fNumerator == null) ? 0 : fNumerator.hashCode());
      result = prime * result + ((fPlusAST == null) ? 0 : fPlusAST.hashCode());
      result = prime * result + ((fSymbolSet == null) ? 0 : fSymbolSet.hashCode());
      result = prime * result + ((fListOfVariables == null) ? 0 : fListOfVariables.hashCode());
      return result;
    }

    /**
     * Return <code>true</code> if the expression is linear.
     *
     * @return <code>true</code> if the expression is linear
     */
    public boolean isLinear() {
      return fEquationType == LINEAR;
    }

    public boolean isLinearOrPolynomial() {
      return fEquationType == LINEAR || fEquationType == POLYNOMIAL;
    }

    /**
     * Check every rule in the <code>listOfResultRules</code> if it's valid in the original
     * expression.
     *
     * @param listOfResultRules list of possible solution rules.
     * @return
     */
    public IAST mapOnOriginal(IAST listOfResultRules) {
      if (fOriginalExpr != null) {
        IASTAppendable list2 = F.ListAlloc(listOfResultRules.size());
        for (int i = 1; i < listOfResultRules.size(); i++) {
          IExpr temp = fOriginalExpr.replaceAll((IAST) listOfResultRules.get(i));
          if (temp.isPresent()) {
            temp = fEngine.evaluate(temp);
            if (temp.isZero()) {
              list2.append(listOfResultRules.get(i));
            }
          }
        }
        return list2;
      }
      return listOfResultRules;
    }

    public void reset() {
      int size = fListOfVariables.size();
      this.fMatrixRow = F.constantArray(F.C0, size - 1);
      // for (int i = 1; i < size; i++) {
      // fMatrixRow.append(F.C0);
      // }
      this.fPlusAST = F.PlusAlloc(8);
      this.fEquationType = LINEAR;
    }

    /**
     * Check for an applicable inverse function at the given <code>position</code> in the <code>
     * Plus(..., ,...)</code> expression.
     *
     * @param ast
     * @param arg1
     * @return
     */
    private IExpr rewriteInverseFunction(IAST ast, IExpr arg1) {
      if (ast.isAbs() || ast.isAST(S.RealAbs, 2)) {
        return fEngine.evaluate(
            F.Expand(
                F.Times(
                    F.Subtract(ast.arg1(), F.Times(F.CN1, arg1)), F.Subtract(ast.arg1(), arg1))));
      } else if (ast.isAST1()) {
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast, true);
        if (inverseFunction.isPresent()) {
          LOGGER.log(fEngine.getLogLevel(), "Using inverse functions may omit some solutions.");
          // rewrite fNumer
          inverseFunction.append(arg1);
          return fEngine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
        }

      } else if (ast.isPower() && ast.base().isSymbol() && ast.exponent().isNumber()) {
        int position = fListOfVariables.indexOf(ast.base());
        if (position > 0) {
          LOGGER.log(fEngine.getLogLevel(), "Using inverse functions may omit some solutions.");
          IAST inverseFunction = F.Power(arg1, ast.exponent().inverse());
          return fEngine.evaluate(F.Subtract(ast.base(), inverseFunction));
        }
      } else if (ast.isTimes()
          && ast.size() == 3
          && ast.first().isNumericFunction(true)
          && ast.second().isAST1()) {
        IAST timesArg2 = (IAST) ast.second();
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(timesArg2, true);
        if (inverseFunction.isPresent()) {
          LOGGER.log(fEngine.getLogLevel(), "Using inverse functions may omit some solutions.");
          // rewrite fNumer
          inverseFunction.append(F.Divide(arg1, ast.first()));
          return fEngine.evaluate(F.Subtract(timesArg2.arg1(), inverseFunction));
        }
      }
      return F.NIL;
    }

    /**
     * Check for an applicable inverse function at the given <code>position</code> in the <code>
     * Plus(..., ,...)</code> expression.
     *
     * @param plusAST the <code>Plus(..., ,...)</code> expression
     * @param position
     * @return <code>F.NIL</code> if no inverse function was found, otherwise return the rewritten
     *     expression
     */
    private IExpr rewriteInverseFunction(IAST plusAST, int position) {
      IAST ast = (IAST) plusAST.get(position);
      IExpr plus = plusAST.splice(position).oneIdentity0();
      if (ast.isAbs() || ast.isAST(S.RealAbs, 2)) {
        if (plus.isNegative() || plus.isZero()) {
          if (plus.isFree(Predicates.in(fListOfVariables), true)) {
            return rewriteInverseFunction(ast, F.Negate(plus));
          }
        }
        return F.NIL;
      }
      if (plus.isFree(Predicates.in(fListOfVariables), true)) {
        return rewriteInverseFunction(ast, F.Negate(plus));
      }
      return F.NIL;
    }

    /**
     * Try to rewrite a <code>Plus(...,f(x), ...)</code> function which contains an invertible
     * function argument <code>f(x)</code>.
     */
    private IExpr rewritePlusWithInverseFunctions(IAST plusAST) {
      IExpr expr;
      for (int i = 1; i < plusAST.size(); i++) {
        expr = plusAST.get(i);
        if (expr.isFree(Predicates.in(fListOfVariables), true)) {
          continue;
        }

        if (expr.isAST()) {
          IAST function = (IAST) expr;
          IAST inverseFunction = InverseFunction.getUnaryInverseFunction(function, true);
          if (inverseFunction.isPresent()  ) {
            IExpr temp = rewriteInverseFunction(plusAST, i);
            if (temp.isPresent()) {
              return temp;
            }
          } else if (function.isPower()) {
            // function is Power(x, fraction)
            return rewritePowerFractions(plusAST, i, F.C1, function.base(), function.exponent());
          } else if (function.isTimes()
              && function.size() == 3
              && function.arg1().isNumericFunction(true)) {
            if (function.arg2().isPower()) {
              // function is num*Power(x, fraction)
              IAST power = (IAST) function.arg2();
              IExpr temp =
                  rewritePowerFractions(
                      plusAST, i, function.arg1(), power.base(), power.exponent());
              if (temp.isPresent()) {
                return fEngine.evaluate(temp);
              }
            } else if (function.arg2().isAST1()) {
              IAST inverseTimesFunction =
                  InverseFunction.getUnaryInverseFunction((IAST) function.arg2(), true);
              if (inverseTimesFunction.isPresent()) {
                IExpr temp = rewriteInverseFunction(plusAST, i);
                if (temp.isPresent()) {
                  return temp;
                }
              }
            }
          } else if (function.isAST(S.GammaRegularized, 3)) {
            IAST temp = plusAST.removeAtCopy(i);
            int position = fListOfVariables.indexOf(function.arg2());
            if (position > 0
                && function.arg1().isFree(fListOfVariables)
                && temp.isFree(fListOfVariables)) {
              LOGGER.log(fEngine.getLogLevel(), "Using inverse functions may omit some solutions.");
              return fEngine.evaluate(F.InverseGammaRegularized(function.arg1(), temp.negate()));
            }
          }
        }
      }
      return F.NIL;
    }

    /**
     * Rewrite <code>num*base^exponent</code> at position <code>i</code> in <code>plusAST</code> if
     * the <code>exponent</code> is a fraction.
     *
     * @param plusAST
     * @param i
     * @param num
     * @param base
     * @param exponent
     * @return
     */
    private IExpr rewritePowerFractions(
        IAST plusAST, int i, IExpr num, IExpr base, IExpr exponent) {
      if (exponent.isFraction() || (exponent.isReal() && !exponent.isNumIntValue())) {
        ISignedNumber arg2 = (ISignedNumber) exponent;
        if (arg2.isPositive()) {
          IExpr plus = plusAST.splice(i).oneIdentity0();
          if (plus.isPositiveResult()) {
            // no solution possible
            return NO_EQUATION_SOLUTION;
          }
          fOriginalExpr = plusAST;
          if (num.isOne()) {
            return fEngine.evaluate(
                F.Subtract(F.Expand(F.Power(F.Negate(plus), arg2.inverse())), base));
          }
          return fEngine.evaluate(
              F.Subtract(
                  base, F.Expand(F.Power(F.Times(num.inverse(), F.Negate(plus)), arg2.inverse()))));
        }
      } else if (base.isSymbol() && base.equals(exponent)) {
        // rewrite num * x^x as ProductLog() (Lambert W function)
        IExpr plus = plusAST.splice(i).oneIdentity0().negate().divide(num);
        // Log(arg1)/ProductLog(Log(arg1))
        IAST inverseFunction =
            F.Plus(base, F.Times(F.Log(plus).negate(), F.Power(F.ProductLog(F.Log(plus)), F.CN1)));
        return fEngine.evaluate(inverseFunction);
      }
      if (fListOfVariables.size() == 2) {
        IExpr variable = fListOfVariables.arg1();
        return rewritePower2ProductLog(plusAST, i, num, base, exponent, variable);
      }
      return F.NIL;
    }

    /**
     * Rewrite <code>base^variable + a*variable + b</code> as <code>
     * variable +  ((b)*Log(x) + a * ProductLog(-(Log(x)/(x^(b/a)*a))) ) / (a*Log(x))
     * </code>. ProductLog is the Lambert W function.
     *
     * <p>See: <a
     * href="https://en.wikipedia.org/wiki/Lambert_W_function#Solving_equations">Wikipedia - Lambert
     * W function - Solving equations</a>
     *
     * @param plusAST a <code>Plus(...)</code> expression
     * @param i the index in <code>plusAST</code> where the argument equals F<code>base ^ exponent
     *     </code>
     * @param num a factor for <code>factor*base^variable</code>
     * @param base must be free of <code>variable</code>
     * @param exponent must be equal to <code>variable</code>
     * @param variable the variable for which <code>plusAST</code> should be solved
     * @return {@link F#NIL} if no solution was found
     */
    private IExpr rewritePower2ProductLog(
        IAST plusAST, int i, IExpr num, IExpr base, IExpr exponent, IExpr variable) {
      if (variable.equals(exponent) && base.isFree(variable)) {
        IExpr restOfPlus = plusAST.splice(i).oneIdentity0();
        IExpr a = F.NIL;
        IExpr b = F.C0;
        if (restOfPlus.isPlus()) {
          int indx = restOfPlus.indexOf(x -> !x.isFree(variable));
          if (indx > 0) {
            IExpr restOfPlus2 = ((IAST) restOfPlus).splice(indx).oneIdentity1();
            if (restOfPlus2.isFree(variable)) {
              a = determineFactor(((IAST) restOfPlus).get(i), variable);
              b = restOfPlus2;
            }
          }
        } else {
          a = determineFactor(restOfPlus, variable);
        }
        if (a.isPresent()) {
          // variable +  ((b)*Log(x) + a * ProductLog(-(Log(x)/(x^(b/a)*a))) )/(a*Log(x))
          IAST inverseFunction =
              F.Plus(
                  variable,
                  F.Times(
                      F.Plus(
                          F.Times(b, F.Log(base)),
                          F.Times(
                              a,
                              F.ProductLog(
                                  F.Times(
                                      num,
                                      F.Log(base),
                                      F.Power(F.Times(a, F.Power(base, F.Divide(b, a))), F.CN1))))),
                      F.Power(F.Times(a, F.Log(base)), F.CN1)));
          return fEngine.evaluate(inverseFunction);
        }
      }
      return F.NIL;
    }

    private static IExpr determineFactor(IExpr restOfPlus, IExpr variable) {
      if (restOfPlus.equals(variable)) {
        return F.C1;
      } else if (restOfPlus.isTimes()) {
        int indx = restOfPlus.indexOf(variable);
        if (indx > 0) {
          IExpr restOfTimes = ((IAST) restOfPlus).splice(indx).oneIdentity1();
          if (restOfTimes.isFree(variable)) {
            return restOfTimes;
          }
        }
      }
      return F.NIL;
    }

    /**
     * Try to rewrite a <code>Times(...,f(x), ...)</code> expression which may contain an invertible
     * function argument <code>f(x)</code> as subexpression.
     */
    private IExpr rewriteTimesWithInverseFunctions(IAST times) {
      IASTAppendable result = F.NIL;
      int j = 1;
      // remove constant sub-expressions from Times() expression
      for (int i = 1; i < times.size(); i++) {
        if (times.get(i).isFree(Predicates.in(fListOfVariables), true)
            && times.get(i).isNumericFunction(true)) {
          if (!result.isPresent()) {
            result = times.copyAppendable();
          }
          result.remove(j);
          continue;
        }
        j++;
      }
      if (!result.isPresent()) {
        return rewriteInverseFunction(times, F.C0);
      }
      IExpr temp0 = result.oneIdentity1();
      if (temp0.isAST()) {
        return rewriteInverseFunction((IAST) temp0, F.C0).orElse(temp0);
      }
      return temp0;
    }

    /**
     * If possible simplify the numerator expression. After that analyze the numerator expression,
     * if it has linear, polynomial or other form.
     */
    protected void simplifyAndAnalyze() {
      IExpr temp = F.NIL;
      if (fNumerator.isPlus()) {
        temp = rewritePlusWithInverseFunctions((IAST) fNumerator);
      } else if (fNumerator.isTimes()
          && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
        temp = rewriteTimesWithInverseFunctions((IAST) fNumerator);
      } else if (fNumerator.isAST() && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
        temp = rewriteInverseFunction((IAST) fNumerator, F.C0);
      }
      if (temp.isPresent()) {
        if (temp.isAST() && fDenominator.isOne()) {
          splitNumeratorDenominator((IAST) temp);
        } else {
          fNumerator = temp;
        }
      }

      analyze(fNumerator);
    }

    public void splitNumeratorDenominator(IAST ast) {
      IExpr[] result = Algebra.getNumeratorDenominator(ast, fEngine);
      this.fNumerator = result[0];
      this.fDenominator = result[1];
      this.fExpr = result[2];
    }
  }

  /** Check an expression, if it's an allowed object. */
  protected static final class IsWrongSolveExpression implements Predicate<IExpr> {
    IExpr wrongExpr;

    public IsWrongSolveExpression() {
      wrongExpr = null;
    }

    public IExpr getWrongExpr() {
      return wrongExpr;
    }

    @Override
    public boolean test(IExpr input) {
      if (input.isDirectedInfinity() || input.isIndeterminate()) {
        // input is representing a DirectedInfinity() or Indeterminate
        // object
        wrongExpr = input;
        return true;
      }
      return false;
    }
  }

  @SuppressWarnings("serial")
  protected static class NoSolution extends Exception {
    /** Solution couldn't be found. */
    public static final int NO_SOLUTION_FOUND = 1;

    /** Definitely wrong solution. */
    public static final int WRONG_SOLUTION = 0;

    final int solType;

    public NoSolution(int solType) {
      super();
      this.solType = solType;
    }

    public int getType() {
      return solType;
    }
  }

  /**
   * Use <code>Infinity</code> as an equation expression for which we get no solution (i.e. <code>
   * (-1)==0  =>  False</code>)
   */
  private static IExpr NO_EQUATION_SOLUTION = F.CInfinity;

  /**
   * Recursively solve the list of analyzers.
   *
   * @param analyzerList list of analyzers, which determine, if an expression has linear, polynomial
   *     or other form
   * @param variables the list of variables
   * @param resultList the list of result values as rules assigned to each variable
   * @param maximumNumberOfResults the maximum number of results in <code>resultList</code>: <code>0
   *     </code> gives all results.
   * @param matrix
   * @param vector
   * @param engine
   * @return throws NoSolution
   */
  protected static IASTAppendable analyzeSublist(
      ArrayList<ExprAnalyzer> analyzerList,
      IAST variables,
      IASTAppendable resultList,
      int maximumNumberOfResults,
      IASTAppendable matrix,
      IASTAppendable vector,
      EvalEngine engine)
      throws NoSolution {
    ExprAnalyzer exprAnalyzer;
    Collections.sort(analyzerList);
    int currEquation = 0;
    while (currEquation < analyzerList.size()) {
      exprAnalyzer = analyzerList.get(currEquation);
      if (exprAnalyzer.getNumberOfVars() == 0) {
        // check if the equation equals zero.
        IExpr expr = exprAnalyzer.getNumerator();
        if (!expr.isZero()) {
          if (expr.isNumber() || expr.isInfinity() || expr.isNegativeInfinity()) {
            throw new NoSolution(NoSolution.WRONG_SOLUTION);
          }
          if (!S.PossibleZeroQ.ofQ(engine, expr)) {
            throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
          }
        }
      } else if (exprAnalyzer.getNumberOfVars() == 1 && exprAnalyzer.isLinearOrPolynomial()) {
        IAST listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer, engine);
        if (listOfRules.isPresent()) {
          listOfRules = exprAnalyzer.mapOnOriginal(listOfRules);
          boolean evaled = false;
          ++currEquation;
          for (int k = 1; k < listOfRules.size(); k++) {
            if (currEquation >= analyzerList.size()) {
              resultList.append(F.List(listOfRules.getAST(k)));
              if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
                return resultList;
              }
              evaled = true;
            } else {
              // collect linear and univariate polynomial
              // equations:
              IAST kListOfSolveRules = listOfRules.getAST(k);
              ArrayList<ExprAnalyzer> subAnalyzerList =
                  substituteRulesInSubAnalyzerList(
                      kListOfSolveRules, analyzerList, currEquation, variables, engine);
              try {
                IAST subResultList =
                    analyzeSublist(
                        subAnalyzerList,
                        variables,
                        F.ListAlloc(),
                        maximumNumberOfResults,
                        matrix,
                        vector,
                        engine);
                if (subResultList.isPresent()) {
                  evaled = true;
                  IASTAppendable tempResult =
                      addSubResultsToResultsList(
                          resultList, subResultList, kListOfSolveRules, maximumNumberOfResults);
                  if (tempResult.isPresent()) {
                    return tempResult;
                  }
                }
              } catch (NoSolution e) {
                if (e.getType() == NoSolution.WRONG_SOLUTION) {
                  evaled = true;
                }
              }
            }
          }
          if (evaled) {
            return resultList;
          }
        }
        throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
      } else if (exprAnalyzer.isLinear()) {
        matrix.append(engine.evaluate(exprAnalyzer.getRow()));
        vector.append(engine.evaluate(F.Negate(exprAnalyzer.getValue())));
      } else {
        throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
      }
      currEquation++;
    }
    return resultList;
  }

  /**
   * Add the sub-results to the results list. If <code>maximumNumberOfResults</code> is reached
   * return the resultList, otherwise return <code>F#NIL</code>.
   *
   * @param resultList
   * @param subResultList
   * @param kListOfSolveRules
   * @param maximumNumberOfResults
   * @return if <code>maximumNumberOfResults</code> is reached return the resultList, otherwiaw
   *     return <code>F#NIL</code>.
   */
  private static IASTAppendable addSubResultsToResultsList(
      IASTAppendable resultList,
      IAST subResultList,
      IAST kListOfSolveRules,
      int maximumNumberOfResults) {
    for (IExpr expr : subResultList) {
      if (expr.isList()) {
        IASTAppendable list;
        if (expr instanceof IASTAppendable) {
          list = (IASTAppendable) expr;
        } else {
          list = ((IAST) expr).copyAppendable();
        }
        list.append(1, kListOfSolveRules);
        resultList.append(list);
        if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
          return resultList;
        }
      } else {
        resultList.append(expr);
        if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
          return resultList;
        }
      }
    }
    return F.NIL;
  }

  /**
   * For all analyzers in <code>analyzerList</code> from position to the last element substitute the
   * variables by the rules in <code>kListOfSolveRules</code> and create a new (sub-)analyzer list.
   *
   * @param kListOfSolveRules
   * @param analyzerList
   * @param position
   * @param variables
   * @param engine
   * @return
   */
  private static ArrayList<ExprAnalyzer> substituteRulesInSubAnalyzerList(
      IAST kListOfSolveRules,
      ArrayList<ExprAnalyzer> analyzerList,
      int position,
      IAST variables,
      EvalEngine engine) {
    ExprAnalyzer exprAnalyzer;
    ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
    for (int i = position; i < analyzerList.size(); i++) {
      IExpr expr = analyzerList.get(i).getExpr();
      IExpr temp = expr.replaceAll(kListOfSolveRules);
      if (temp.isPresent()) {
        expr = engine.evaluate(temp);
        exprAnalyzer = new ExprAnalyzer(expr, variables, engine);
        exprAnalyzer.simplifyAndAnalyze();
      } else {
        // reuse old analyzer; expression hasn't
        // changed
        exprAnalyzer = analyzerList.get(i);
      }
      subAnalyzerList.add(exprAnalyzer);
    }
    return subAnalyzerList;
  }

  /**
   * Evaluate the roots of a univariate polynomial with the Roots() function.
   *
   * @param exprAnalyzer
   * @param engine
   * @return
   */
  private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
    IExpr numerator = exprAnalyzer.getNumerator();
    IExpr denominator = exprAnalyzer.getDenominator();
    // try to solve the expr for a symbol in the symbol set
    for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
      IExpr temp = F.NIL;
      if (numerator.isNumericMode() && denominator.isOne()) {
        temp = RootsFunctions.roots(numerator, F.List(sym), engine);
      }
      if (!temp.isPresent()) {
        temp =
            RootsFunctions.rootsOfVariable(
                numerator, denominator, F.List(sym), numerator.isNumericMode(), engine);
      }
      if (temp.isPresent()) {
        if (temp.isSameHeadSizeGE(S.List, 2)) {
          IAST rootsList = (IAST) temp;
          IASTAppendable resultList = F.ListAlloc(rootsList.size());
          for (IExpr root : rootsList) {
            resultList.append(F.Rule(sym, root));
          }
          return QuarticSolver.sortASTArguments(resultList);
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  /**
   * Return an immutable <code>List[numerator, denominator]</code> of the given expression. Uses
   * <code>Numerator[] and Denominator[]</code> functions.
   *
   * @param expr
   * @param engine
   * @param evalTogether evaluate <code>Together[expr]</code> before determining numerator and
   *     denominator of the expression.
   * @return <code>List[numerator, denominator]</code>
   */
  private static IAST splitNumeratorDenominator(
      IAST expr, EvalEngine engine, boolean evalTogether) {
    IExpr numerator, denominator;
    if (evalTogether) {
      numerator = Algebra.together(expr, engine);
    } else {
      numerator = expr;
    }
    // split expr into numerator and denominator
    denominator = engine.evaluate(F.Denominator(numerator));
    if (!denominator.isOne()) {
      // search roots for the numerator expression
      numerator = engine.evaluate(F.Numerator(numerator));
    } else {
      numerator = expr;
    }
    return F.binaryAST2(S.List, numerator, denominator);
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return of(ast, false, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  /**
   * @param ast the <code>Solve(...)</code> ast
   * @param numeric if true, try to find a numerically solution
   * @param engine
   * @return
   */
  public static IExpr of(final IAST ast, boolean numeric, EvalEngine engine) {
    boolean[] isNumeric = new boolean[] {false};
    try {
      if (ast.arg1().isEmptyList()) {
        return F.List(F.CEmptyList);
      }
      IAST userDefinedVariables =
          Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
      if (userDefinedVariables.isPresent()) {
        IAST equationVariables = VariablesSet.getVariables(ast.arg1());
        if (userDefinedVariables.isEmpty()) {
          userDefinedVariables = equationVariables;
        }

        ISymbol domain = S.Complexes;
        if (ast.isAST3()) {
          if (!ast.arg3().isSymbol()) {
            LOGGER.log(engine.getLogLevel(),
                "{}: domain definition expected at position 3 instead of {}", ast.topHead(),
                ast.arg3());
            return F.NIL;
          }
          domain = (ISymbol) ast.arg3();
          if (domain.equals(S.Booleans)) {
            return BooleanFunctions.solveInstances(
                ast.arg1(), userDefinedVariables, Integer.MAX_VALUE);
          }
          if (domain.equals(S.Integers)) {
            if (!userDefinedVariables.isEmpty()) {
              IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast, 1);
              if (equationsAndInequations.isEmpty()) {
                return F.NIL;
              }
              try {
                //                if (ToggleFeature.CHOCO_SOLVER) {
                //                  // try calling choco solver
                //                  if (equationsAndInequations.isFreeAST(x -> x.equals(S.Power))) {
                //                    try {
                //                      IAST resultList =
                //                          ChocoConvert.integerSolve(
                //                              equationsAndInequations,
                //                              equationVariables,
                //                              userDefinedVariables,
                //                              engine);
                //                      if (resultList.isPresent()) {
                //                        EvalAttributes.sort((IASTMutable) resultList);
                //                        return resultList;
                //                      }
                //                    } catch (RuntimeException rex) {
                //                      // try 2nd solver
                //                    }
                //                  }
                //                }
                // call cream solver
                CreamConvert converter = new CreamConvert();
                IAST resultList =
                    converter.integerSolve(
                        equationsAndInequations, equationVariables, userDefinedVariables, engine);
                if (resultList.isPresent()) {
                  EvalAttributes.sort((IASTMutable) resultList);
                  return resultList;
                }
              } catch (LimitException le) {
                if (Config.SHOW_STACKTRACE) {
                  le.printStackTrace();
                }
                throw le;
              } catch (RuntimeException rex) {
                LOGGER.log(engine.getLogLevel(), "Integers solution not found", rex);
                return F.NIL;
              }
            }
            return F.NIL;
          }

          //          if (domain.equals(S.Reals)) {
          //            if (!userDefinedVariables.isEmpty()) {
          //              IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast,
          // 1);
          //              if (!equationsAndInequations.isEmpty()) {
          //                try {
          //                  // call choco solver
          //                  try {
          //                    IAST resultList =
          //                        ChocoConvert.realSolve(
          //                            equationsAndInequations,
          //                            equationVariables,
          //                            userDefinedVariables,
          //                            engine);
          //                    if (resultList.isPresent()) {
          //                      EvalAttributes.sort((IASTMutable) resultList);
          //                      return resultList;
          //                    }
          //                  } catch (RuntimeException rex) {
          //                    // try 2nd solver
          //                    rex.printStackTrace();
          //                  }
          //                } catch (LimitException le) {
          //                  if (Config.SHOW_STACKTRACE) {
          //                    le.printStackTrace();
          //                  }
          //                  throw le;
          //                } catch (RuntimeException rex) {
          //                  if (Config.SHOW_STACKTRACE) {
          //                    rex.printStackTrace();
          //                  }
          //                  return engine.printMessage(
          //                      "Solve: " + "Reals solution not found: " + rex.getMessage());
          //                }
          //              }
          //            }
          //          }

          if (!domain.equals(S.Reals) && !domain.equals(S.Complexes)) {
            Level level = engine.getLogLevel();
            LOGGER.log(level, "{}: domain definition expected at position 3 instead of {}",
                ast.topHead(), domain);
            return F.NIL;
          }
        }
        IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
        IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, isNumeric);
        boolean numericFlag = isNumeric[0] || numeric;
        if (lists[2].isPresent()) {
          IExpr result = solveNumeric(lists[2], numericFlag, engine);
          if (!result.isPresent()) {
            return IOFunctions.printMessage(ast.topHead(), "nsmet", F.List(ast.topHead()), engine);
          }
          return checkDomain(result, domain);
        }
        IASTMutable termsEqualZeroList = lists[0];
        IExpr result =
            solveRecursive(termsEqualZeroList, lists[1], numericFlag, userDefinedVariables, engine);
        if (!result.isPresent()) {
          return IOFunctions.printMessage(ast.topHead(), "nsmet", F.List(ast.topHead()), engine);
        }
        return checkDomain(result, domain);
      }
    } catch (LimitException | ValidateException e) {
      LOGGER.log(engine.getLogLevel(), S.Solve, e);
    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    return F.NIL;
  }

  /**
   * Check if all solutions are in the given domain (currently only <code>Reals</code> is checked).
   *
   * @param expr
   * @param domain
   * @return
   */
  private static IExpr checkDomain(IExpr expr, ISymbol domain) {
    if (expr.isList() && domain.equals(S.Reals)) {
      if (expr.isListOfLists()) {
        IASTAppendable result = F.ListAlloc(expr.size());
        IASTAppendable appendable;
        for (int i = 1; i < expr.size(); i++) {
          IAST listOfRules = (IAST) ((IAST) expr).get(i);
          appendable = listOfRules.copyAppendable();
          if (!isComplex(listOfRules)) {
            result.append(appendable);
          }
        }
        return result;
      } else {
        if (!isComplex(((IAST) expr))) {
          return expr;
        }
        return F.CEmptyList;
      }
    }
    return expr;
  }

  /**
   * Check if all rules in the list return a real result.
   *
   * @param listOfRules a list of rules <code>Rule(variable, value)</code>
   * @return
   */
  private static boolean isComplex(IAST listOfRules) {
    if (listOfRules.isListOfRules(false)) {
      return listOfRules.exists(x -> !x.second().isRealResult());
    }
    return false;
  }

  /**
   * Solve the list of equations recursively. Return a list of rules <code>
   * {var1->expr1, var1->expr2, ...}</code> (typically for NSolve function) or return a &quot;list
   * of list of rules&quot; (typically for Solve function) <code>
   * {{var1->expr11, var1->expr12,...}, {var1->expr21, var1->expr22,...}, ...}</code>. The method
   * solves for the first variable from the <code>variables</code> list and inserts the solution
   * back in the remaining equations and calls the method recursively again with this new system.
   *
   * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
   * @param inequationsList a list of inequality constraints
   * @param numericFlag if <code>true</code>, try to find a numeric solution
   * @param variables the variables for which the equations should be solved
   * @param engine
   * @return a list of rules (typically NSolve) or a list of list of rules (typically Solve) of the
   *     solutions, <code>F.NIL</code> otherwise.
   */
  private static IExpr solveRecursive(
      IASTMutable termsEqualZeroList,
      IASTMutable inequationsList,
      boolean numericFlag,
      IAST variables,
      EvalEngine engine) {
    IASTMutable temp =
        solveTimesEquationsRecursively(
            termsEqualZeroList, inequationsList, numericFlag, variables, engine);
    if (temp.isPresent()) {
      return solveNumeric(QuarticSolver.sortASTArguments(temp), numericFlag, engine);
    }

    if (inequationsList.isEmpty() && termsEqualZeroList.size() == 2 && variables.size() == 2) {
      IExpr firstVariable = variables.arg1();
      IExpr res = eliminateOneVariable(termsEqualZeroList, firstVariable, engine);
      if (!res.isPresent()) {
        if (numericFlag) {
          // find numerically find start value 0
          res = S.FindRoot.of(engine, termsEqualZeroList.arg1(), F.List(firstVariable, F.C0));
        }
      }
      if (!res.isList() || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
        return F.NIL;
      }
      return solveNumeric(res, numericFlag, engine);
    }

    if (termsEqualZeroList.size() > 2 && variables.size() >= 3) {
      // expensive recursion try
      IExpr firstEquation = termsEqualZeroList.arg1();
      IExpr firstVariable = variables.arg1();
      IAST[] reduced =
          Eliminate.eliminateOneVariable(
              F.List(F.Equal(firstEquation, F.C0)), firstVariable, engine);
      if (reduced != null) {
        variables = variables.splice(1);
        termsEqualZeroList = termsEqualZeroList.removeAtCopy(1);
        // oneVariableRule = ( firstVariable -> reducedExpression )
        IAST oneVariableRule = reduced[1];
        IExpr replaced = termsEqualZeroList.replaceAll(oneVariableRule);
        if (replaced.isList()) {
          IExpr subResult =
              solveRecursive(
                  (IASTMutable) replaced, inequationsList, numericFlag, variables, engine);
          if (subResult.isListOfLists()) {
            IASTAppendable result = F.ListAlloc(subResult.size());
            IASTAppendable appendable;
            for (int i = 1; i < subResult.size(); i++) {
              IAST listOfRules = (IAST) subResult.getAt(i);
              replaced = oneVariableRule.second().replaceAll(listOfRules);
              if (replaced.isPresent()) {
                replaced = S.Simplify.of(engine, replaced);
                appendable = listOfRules.copyAppendable();
                appendable.append(F.Rule(firstVariable, replaced));
                result.append(appendable);
              }
            }
            return result;
          } else if (subResult.isList()) { // important for NSolve
            replaced = oneVariableRule.second().replaceAll((IAST) subResult);
            if (replaced.isPresent()) {
              IASTAppendable appendable = ((IAST) subResult).copyAppendable();
              appendable.append(F.Rule(firstVariable, replaced));
              return appendable;
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * if <code>isNumeric == true</code> do a numeric calculation
   *
   * @param expr
   * @param isNumeric
   * @param engine
   * @return
   */
  private static IExpr solveNumeric(IExpr expr, boolean isNumeric, EvalEngine engine) {
    return expr.isPresent() ? isNumeric ? engine.evalN(expr) : expr : F.NIL;
  }

  /**
   * Use the <code>Eliminate()</code> function to extract one variable.
   *
   * @param termsEqualZeroList a list of expressions which equals zero.
   * @param variable the variable which should be eliminated in the term
   * @return
   */
  private static IAST eliminateOneVariable(
      IAST termsEqualZeroList, IExpr variable, EvalEngine engine) {
    if (!termsEqualZeroList
        .arg1()
        .isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
      return F.NIL;
    }
    // copy the termsEqualZeroList back to a list of F.Equal(...) expressions
    // because Eliminate() operates on equations.
    IAST equalsASTList = termsEqualZeroList.mapThread(F.Equal(F.Slot1, F.C0), 1);
    IAST[] tempAST = Eliminate.eliminateOneVariable(equalsASTList, variable, engine);
    if (tempAST != null
        && tempAST[1] != null
        && tempAST[1].isRule()
        && tempAST[1].second().isTrue()) {
      return F.CEmptyList;
    }
    if (tempAST != null && tempAST[1] != null) {
      return F.List(F.List(tempAST[1]));
    }
    return F.NIL;
  }

  /**
   * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
   * @param variables the variables for which the equations should be solved
   * @param maximumNumberOfResults the maximum number of results which should be returned
   * @param engine the evaluation engine
   * @return a &quot;list of rules list&quot; which solves the equations, or an empty list if no
   *     solution exists, or <code>F.NIL</code> if the equations are not solvable by this algorithm.
   */
  protected static IASTMutable solveEquations(
      IASTMutable termsEqualZeroList,
      IAST inequationsList,
      IAST variables,
      int maximumNumberOfResults,
      EvalEngine engine) {
    try {
      IASTMutable list = PolynomialFunctions.solveGroebnerBasis(termsEqualZeroList, variables);
      if (list.isPresent()) {
        termsEqualZeroList = list;
      }
    } catch (JASConversionException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
    }

    // rewrite some special expressions
    for (int i = 1; i < termsEqualZeroList.size(); i++) {
      IExpr equationTerm = termsEqualZeroList.get(i);
      if (equationTerm.isPlus()) {
        IExpr eq = S.Equal.of(equationTerm, F.C0);
        if (eq.isEqual()) {
          IExpr arg1 = eq.first();
          if (arg1.isPlus2()) {
            if (arg1.first().isSqrtExpr() && arg1.second().isSqrtExpr()) {
              // Sqrt() + Sqrt() == constant
              termsEqualZeroList.set(
                  i,
                  S.Subtract.of(
                      S.Expand.of(F.Sqr(arg1.second())),
                      S.Expand.of(F.Sqr(F.Subtract(eq.second(), arg1.first())))));
            }
          }
        }
      }
    }

    ExprAnalyzer exprAnalyzer;
    ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();
    IsWrongSolveExpression predicate = new IsWrongSolveExpression();
    // collect linear and univariate polynomial equations:
    for (IExpr expr : termsEqualZeroList) {
      if (expr.has(predicate, true)) {
        LOGGER.log(engine.getLogLevel(), "Solve: the system contains the wrong object: {}",
            predicate.getWrongExpr());
        throw new NoEvalException();
      }
      exprAnalyzer = new ExprAnalyzer(expr, variables, engine);
      exprAnalyzer.simplifyAndAnalyze();
      analyzerList.add(exprAnalyzer);
    }
    IASTAppendable matrix = F.ListAlloc();
    IASTAppendable vector = F.ListAlloc();
    try {
      IASTAppendable resultList = F.ListAlloc();
      resultList =
          analyzeSublist(
              analyzerList, variables, resultList, maximumNumberOfResults, matrix, vector, engine);
      if (vector.size() > 1) {
        // solve a linear equation <code>matrix.x == vector</code>
        FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix(matrix, vector);
        if (augmentedMatrix != null) {
          IAST subSolutionList =
              LinearAlgebra.rowReduced2RulesList(augmentedMatrix, variables, resultList, engine);
          return solveInequations(
              (IASTMutable) subSolutionList, inequationsList, maximumNumberOfResults, engine);
        }
        return F.NIL;
      }
      return solveInequations(resultList, inequationsList, maximumNumberOfResults, engine);
      // return sortASTArguments(resultList);
    } catch (NoSolution e) {
      if (e.getType() == NoSolution.WRONG_SOLUTION) {
        return F.ListAlloc();
      }
      return F.NIL;
    }
  }

  protected static IASTMutable solveInequations(
      IASTMutable subSolutionList,
      IAST inequationsList,
      int maximumNumberOfResults,
      EvalEngine engine) {
    if (inequationsList.isEmpty()) {
      return QuarticSolver.sortASTArguments(subSolutionList);
    }

    if (subSolutionList.isListOfLists()) {
      IASTAppendable result = F.ListAlloc(subSolutionList.size());
      for (int i = 1; i < subSolutionList.size(); i++) {
        IASTMutable list = (IASTMutable) subSolutionList.get(i);
        IExpr temp = F.subst(inequationsList, list);
        boolean[] isNumeric = new boolean[] {false};
        temp = engine.evalQuiet(temp);
        if (temp.isAST()) {
          IASTMutable[] lists = SolveUtils.filterSolveLists((IASTMutable) temp, list, isNumeric);
          if (lists[2].isPresent()) {
            if (!lists[2].isEmptyList()) {
              result.append(lists[2]);
            }
          }
        }
      }
      return result;
    }

    // TODO solve inequations here?

    return F.NIL;
  }

  /**
   * Analyze the <code>termsEqualZeroList</code> if it contains a <code>Times[..., ,...]</code>
   * expression. If true, set the factors equal to <code>0</code> and solve the equations
   * recursively.
   *
   * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
   * @param numericFlag
   * @param variables the variables for which the equations should be solved
   * @param engine the evaluation engine
   * @return
   */
  private static IASTMutable solveTimesEquationsRecursively(
      IASTMutable termsEqualZeroList,
      IAST inequationsList,
      boolean numericFlag,
      IAST variables,
      EvalEngine engine) {
    try {
      IASTMutable resultList =
          solveEquations(termsEqualZeroList, inequationsList, variables, 0, engine);
      if (resultList.isPresent() && !resultList.isEmpty()) {
        return resultList;
      }
      Set<IExpr> subSolutionSet = new TreeSet<IExpr>();
      for (int i = 1; i < termsEqualZeroList.size(); i++) {
        IExpr termEQZero = termsEqualZeroList.get(i);
        if (termEQZero.isTimes()) {
          solveTimesAST(
              (IAST) termEQZero,
              termsEqualZeroList,
              inequationsList,
              numericFlag,
              variables,
              engine,
              subSolutionSet,
              i);

        } else {
          if (termEQZero.isAST()) {
            // try factoring
            termEQZero = S.Factor.of(engine, termEQZero);

            if (termEQZero.isTimes()) {
              solveTimesAST(
                  (IAST) termEQZero,
                  termsEqualZeroList,
                  inequationsList,
                  numericFlag,
                  variables,
                  engine,
                  subSolutionSet,
                  i);
            }
          }
        }
      }
      if (subSolutionSet.size() > 0) {
        return F.ListAlloc(subSolutionSet);
      }
      return resultList;
    } catch (LimitException le) {
      if (Config.SHOW_STACKTRACE) {
        le.printStackTrace();
      }
      throw le;
    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    return F.NIL;
  }

  private static void solveTimesAST(
      IAST times,
      IAST termsEqualZeroList,
      IAST inequationsList,
      boolean numericFlag,
      IAST variables,
      EvalEngine engine,
      Set<IExpr> subSolutionSet,
      int i) {
    IAST temp;
    for (int j = 1; j < times.size(); j++) {
      if (!times.get(j).isFree(Predicates.in(variables), true)) {
        // try to get a solution from this Times() factor
        IASTMutable clonedEqualZeroList = termsEqualZeroList.setAtCopy(i, times.get(j));
        temp = solveEquations(clonedEqualZeroList, inequationsList, variables, 0, engine);
        if (temp.size() > 1) {
          for (int k = 1; k < temp.size(); k++) {
            IExpr solution = temp.get(k);
            IExpr replaceAll = engine.evaluate(F.ReplaceAll(times, solution));
            IExpr zeroCrossCheck = engine.evalN(replaceAll);
            if (zeroCrossCheck.isZero()) {
              subSolutionSet.add(solution);
            } else {
              if (replaceAll.isPlusTimesPower()
                  && //
                  S.PossibleZeroQ.ofQ(engine, replaceAll)) {
                subSolutionSet.add(solution);
              }
            }
          }
        } else {

          if (clonedEqualZeroList.size() == 2 && variables.size() == 2) {
            IExpr firstVariable = variables.arg1();
            IExpr res = eliminateOneVariable(clonedEqualZeroList, firstVariable, engine);
            if (!res.isPresent()) {
              if (numericFlag) {
                // find numerically with start value 0
                res =
                    S.FindRoot.ofNIL(
                        engine, clonedEqualZeroList.arg1(), F.List(firstVariable, F.C0));
              }
            }
            if (!res.isList()
                || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
              continue;
            }
            IAST subResult = (IAST) res;
            for (int k = 1; k < subResult.size(); k++) {
              subSolutionSet.add(solveNumeric(subResult.get(i), numericFlag, engine));
            }
          }
        }
      }
    }
  }
}
