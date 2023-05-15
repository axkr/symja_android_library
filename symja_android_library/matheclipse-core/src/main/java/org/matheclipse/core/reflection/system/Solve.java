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
import org.matheclipse.core.convert.ChocoConvert;
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
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IReal;
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
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Solve(equations, vars, domain)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code> in the given
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
 * <p>
 * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>,
 * <a href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>,
 * <a href="NRoots.md">NRoots</a>
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

    private HashSet<IExpr> fVariableSet;
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
      this.fVariableSet = new HashSet<IExpr>();
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
      if (fVariableSet.size() != o.fVariableSet.size()) {
        return (fVariableSet.size() < o.fVariableSet.size()) ? -1 : 1;
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ExprAnalyzer other = (ExprAnalyzer) obj;
      if (fDenominator == null) {
        if (other.fDenominator != null)
          return false;
      } else if (!fDenominator.equals(other.fDenominator))
        return false;
      if (fEquationType != other.fEquationType)
        return false;
      if (fExpr == null) {
        if (other.fExpr != null)
          return false;
      } else if (!fExpr.equals(other.fExpr))
        return false;
      if (fLeafCount != other.fLeafCount)
        return false;
      if (fMatrixRow == null) {
        if (other.fMatrixRow != null)
          return false;
      } else if (!fMatrixRow.equals(other.fMatrixRow))
        return false;
      if (fNumerator == null) {
        if (other.fNumerator != null)
          return false;
      } else if (!fNumerator.equals(other.fNumerator))
        return false;
      if (fPlusAST == null) {
        if (other.fPlusAST != null)
          return false;
      } else if (!fPlusAST.equals(other.fPlusAST))
        return false;
      if (fVariableSet == null) {
        if (other.fVariableSet != null)
          return false;
      } else if (!fVariableSet.equals(other.fVariableSet))
        return false;
      if (fListOfVariables == null) {
        if (other.fListOfVariables != null)
          return false;
      } else if (!fListOfVariables.equals(other.fListOfVariables))
        return false;
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
      return fVariableSet.size();
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
        IExpr variable = null;
        fLeafCount++;
        IAST arg = (IAST) eqExpr;
        IExpr expr;
        for (int i = 1; i < arg.size(); i++) {
          expr = arg.get(i);
          if (expr.isFree(Predicates.in(fListOfVariables), true)) {
            fLeafCount++;
          } else if (expr.isVariable()) {
            fLeafCount++;
            for (int j = 1; j < fListOfVariables.size(); j++) {
              if (fListOfVariables.get(j).equals(expr)) {
                fVariableSet.add(expr);
                if (variable != null) {
                  if (fEquationType == LINEAR) {
                    fEquationType = POLYNOMIAL;
                  }
                } else {
                  variable = expr;
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
            if (expr.exponent().isNumIntValue()) {
              getTimesArgumentEquationType(expr.base());
            } else {
              getTimesArgumentEquationType(expr.exponent());
            }
          } else {
            fLeafCount += eqExpr.leafCount();
            if (fEquationType <= POLYNOMIAL) {
              fEquationType = OTHERS;
            }
          }
        }
        if (fEquationType == LINEAR && variable == null) {
          // should never happen??
          LOGGER.error("sym == null???");
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
    public Set<IExpr> getVariableSet() {
      return fVariableSet;
    }

    private void getTimesArgumentEquationType(IExpr expr) {
      if (expr.isVariable()) {
        fLeafCount++;
        int position = fListOfVariables.indexOf(expr);
        if (position > 0) {
          fVariableSet.add(expr);
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
        if (exponent.isVariable()) {
          int position = fListOfVariables.indexOf(exponent);
          if (position > 0) {
            if (fEquationType <= POLYNOMIAL) {
              fEquationType = OTHERS;
            }
            fVariableSet.add(exponent);
          }
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
      result = prime * result + ((fVariableSet == null) ? 0 : fVariableSet.hashCode());
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
        return F.mapList(listOfResultRules, element -> {
          IExpr temp = fOriginalExpr.replaceAll((IAST) element);
          if (temp.isPresent()) {
            temp = fEngine.evaluate(temp);
            if (temp.isZero()) {
              return element;
            }
          }
          return F.NIL;
        });
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
        return fEngine.evaluate(F.Expand(
            F.Times(F.Subtract(ast.arg1(), F.Times(F.CN1, arg1)), F.Subtract(ast.arg1(), arg1))));
      } else if (ast.isAST1()) {
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast, true);
        if (inverseFunction.isPresent()) {
          IOFunctions.printIfunMessage(S.InverseFunction);
          // rewrite fNumer
          inverseFunction.append(arg1);
          return fEngine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
        }

      } else if (ast.isPower() && ast.base().isSymbol() && ast.exponent().isNumber()) {
        int position = fListOfVariables.indexOf(ast.base());
        if (position > 0) {
          IOFunctions.printIfunMessage(S.InverseFunction);
          IAST inverseFunction = F.Power(arg1, ast.exponent().inverse());
          return fEngine.evaluate(F.Subtract(ast.base(), inverseFunction));
        }
      } else if (ast.isTimes() && ast.size() == 3 && ast.first().isNumericFunction(true)
          && ast.second().isAST1()) {
        IAST timesArg2 = (IAST) ast.second();
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(timesArg2, true);
        if (inverseFunction.isPresent()) {
          IOFunctions.printIfunMessage(S.InverseFunction);
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
     *         expression
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
          if (inverseFunction.isPresent()) {
            IExpr temp = rewriteInverseFunction(plusAST, i);
            if (temp.isPresent()) {
              return temp;
            }
          } else if (function.isPower()) {
            // function is Power(x, fraction)
            return rewritePowerFractions(plusAST, i, F.C1, function.base(), function.exponent());
          } else if (function.isTimes() && function.size() == 3
              && function.arg1().isNumericFunction(true)) {
            if (function.arg2().isPower()) {
              // function is num*Power(x, fraction)
              IAST power = (IAST) function.arg2();
              IExpr temp = rewritePowerFractions(plusAST, i, function.arg1(), power.base(),
                  power.exponent());
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
            if (position > 0 && function.arg1().isFree(fListOfVariables)
                && temp.isFree(fListOfVariables)) {
              IOFunctions.printIfunMessage(S.InverseFunction);
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
    private IExpr rewritePowerFractions(IAST plusAST, int i, IExpr num, IExpr base,
        IExpr exponent) {
      if (exponent.isFraction() || (exponent.isReal() && !exponent.isNumIntValue())) {
        IReal arg2 = (IReal) exponent;
        if (arg2.isPositive()) {
          IExpr plus = plusAST.splice(i).oneIdentity0();
          if (plus.isPositiveResult()) {
            // no solution possible
            return NO_EQUATION_SOLUTION;
          }
          fOriginalExpr = plusAST;
          if (num.isOne()) {
            return fEngine
                .evaluate(F.Subtract(F.Expand(F.Power(F.Negate(plus), arg2.inverse())), base));
          }
          return fEngine.evaluate(F.Subtract(base,
              F.Expand(F.Power(F.Times(num.inverse(), F.Negate(plus)), arg2.inverse()))));
        }
      } else if (base.isVariable() && base.equals(exponent)) {
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
     * <p>
     * See: <a href="https://en.wikipedia.org/wiki/Lambert_W_function#Solving_equations">Wikipedia -
     * Lambert W function - Solving equations</a>
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
    private IExpr rewritePower2ProductLog(IAST plusAST, int i, IExpr num, IExpr base,
        IExpr exponent, IExpr variable) {
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
          // variable + ((b)*Log(x) + a * ProductLog(-(Log(x)/(x^(b/a)*a))) )/(a*Log(x))
          IAST inverseFunction = F.Plus(variable,
              F.Times(
                  F.Plus(F.Times(b, F.Log(base)),
                      F.Times(a,
                          F.ProductLog(F.Times(num, F.Log(base),
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
          if (result.isNIL()) {
            result = times.copyAppendable();
          }
          result.remove(j);
          continue;
        }
        j++;
      }
      if (result.isNIL()) {
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
      IExpr[] result = Algebra.getNumeratorDenominator(ast, fEngine, true);
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

  protected static class NoSolution extends Exception {
    private static final long serialVersionUID = -8578380756971796776L;

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
   *        or other form
   * @param variables the list of variables
   * @param resultList the list of result values as rules assigned to each variable
   * @param maximumNumberOfResults the maximum number of results in <code>resultList</code>: <code>0
   *     </code> gives all results.
   * @param matrix
   * @param vector
   * @param engine
   * @return throws NoSolution
   */
  protected static IASTAppendable analyzeSublist(ArrayList<ExprAnalyzer> analyzerList,
      IAST variables, IASTAppendable resultList, int maximumNumberOfResults, IASTAppendable matrix,
      IASTAppendable vector, EvalEngine engine) throws NoSolution {
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
              resultList.append(F.list(listOfRules.getAST(k)));
              if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
                return resultList;
              }
              evaled = true;
            } else {
              // collect linear and univariate polynomial
              // equations:
              IAST kListOfSolveRules = listOfRules.getAST(k);
              ArrayList<ExprAnalyzer> subAnalyzerList = substituteRulesInSubAnalyzerList(
                  kListOfSolveRules, analyzerList, currEquation, variables, engine);
              try {
                IAST subResultList = analyzeSublist(subAnalyzerList, variables, F.ListAlloc(),
                    maximumNumberOfResults, matrix, vector, engine);
                if (subResultList.isPresent()) {
                  evaled = true;
                  IASTAppendable tempResult = addSubResultsToResultsList(resultList, subResultList,
                      kListOfSolveRules, maximumNumberOfResults);
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
   *         return <code>F#NIL</code>.
   */
  private static IASTAppendable addSubResultsToResultsList(IASTAppendable resultList,
      IAST subResultList, IAST kListOfSolveRules, int maximumNumberOfResults) {
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
  private static ArrayList<ExprAnalyzer> substituteRulesInSubAnalyzerList(IAST kListOfSolveRules,
      ArrayList<ExprAnalyzer> analyzerList, int position, IAST variables, EvalEngine engine) {
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
    // try to solve the expr for one of the variables in the symbol set
    for (IExpr variable : exprAnalyzer.getVariableSet()) {
      IAST temp = rootsOfUnivariatePolynomial(numerator, denominator, variable, engine);
      if (temp.isPresent()) {
        return temp;
      }
    }
    return F.NIL;
  }

  public static IAST rootsOfUnivariatePolynomial(IExpr numerator, IExpr denominator, IExpr variable,
      EvalEngine engine) {
    IExpr temp = F.NIL;

    if (numerator.isNumericMode() && denominator.isOne()) {
      temp = RootsFunctions.roots(numerator, F.list(variable), engine);
    }
    if (temp.isNIL()) {
      temp = RootsFunctions.rootsOfVariable(numerator, denominator, F.list(variable),
          numerator.isNumericMode(), engine);
    }
    if (temp.isPresent()) {
      if (temp.isSameHeadSizeGE(S.List, 2)) {
        IAST rootsList = (IAST) temp;
        IASTAppendable resultList = F.mapList(rootsList, root -> F.Rule(variable, root));
        return QuarticSolver.sortASTArguments(resultList);
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    boolean isNumericArgument = ast.arg1().isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG);
    return of(ast, isNumericArgument, engine);
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
    boolean[] isNumeric = new boolean[] {numeric};
    try {
      if (ast.arg1().isEmptyList()) {
        return F.list(F.CEmptyList);
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
            return BooleanFunctions.solveInstances(ast.arg1(), userDefinedVariables,
                Integer.MAX_VALUE);
          }
          if (domain.equals(S.Integers)) {
            return solveIntegers(ast, equationVariables, userDefinedVariables, Integer.MAX_VALUE,
                engine);
          }

          if (!domain.equals(S.Reals) && !domain.equals(S.Complexes)) {
            Level level = engine.getLogLevel();
            LOGGER.log(level, "{}: domain definition expected at position 3 instead of {}",
                ast.topHead(), domain);
            return F.NIL;
          }

        }

        IAssumptions oldAssumptions = engine.getAssumptions();
        try {
          IAssumptions assum = setVariablesReals(userDefinedVariables, domain);
          if (assum != null) {
            engine.setAssumptions(assum);
          }
          IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
          IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, isNumeric);
          boolean numericFlag = isNumeric[0] || numeric;
          if (lists[2].isPresent()) {
            IExpr result = solveNumeric(lists[2], numericFlag, engine);
            if (result.isNIL()) {
              // The system cannot be solved with the methods available to Solve.
              return IOFunctions.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()),
                  engine);
            }
            return checkDomain(result, domain);
          }
          IASTMutable termsEqualZeroList = lists[0];
          IExpr result = solveRecursive(termsEqualZeroList, lists[1], numericFlag,
              userDefinedVariables, engine);
          if (result.isNIL()) {
            // The system cannot be solved with the methods available to Solve.)
            return IOFunctions.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
          }
          return checkDomain(result, domain);
        } finally {
          engine.setAssumptions(oldAssumptions);
        }
      }
    } catch (ValidateException ve) {
      return IOFunctions.printMessage(S.Solve, ve, engine);
    } catch (LimitException e) {
      LOGGER.log(engine.getLogLevel(), S.Solve, e);
    } catch (RuntimeException rex) {
      LOGGER.debug("Solve.of() failed() failed", rex);
    }
    return F.NIL;
  }

  /**
   * If <code>domain</code> is {@link S#Reals} create the {@link F#Element(IExpr, Reals)} assumption
   * for each variable.
   * 
   * @param userDefinedVariables
   * @param domain
   * @return <code>null</code> if no assumption was created
   */
  private static IAssumptions setVariablesReals(IAST userDefinedVariables, ISymbol domain) {
    if (domain.equals(S.Reals)) {
      return Assumptions.getInstance(F.mapList(userDefinedVariables, t -> F.Element(t, domain)));
    }
    return null;
  }

  public static IExpr solveIntegers(final IAST ast, IAST equationVariables,
      IAST userDefinedVariables, int maximumNumberOfResults, EvalEngine engine) {
    if (!userDefinedVariables.isEmpty()) {
      IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast, 1);
      if (equationsAndInequations.isEmpty()) {
        return F.NIL;
      }
      try {
        if (equationsAndInequations.isFreeAST(x -> chocoSolver(x))) {
          // choco-solver doesn't handle Power() expressions very well at the moment!
          try {
            LOGGER.debug("Choco solver");
            IAST resultList = ChocoConvert.integerSolve(equationsAndInequations, equationVariables,
                userDefinedVariables, maximumNumberOfResults, engine);
            if (resultList.isPresent()) {
              EvalAttributes.sort((IASTMutable) resultList);
              return resultList;
            }
          } catch (RuntimeException rex) {
            // try 2nd solver
            // if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
            // }
          }
        } else {
          // call cream solver
          LOGGER.debug("Cream solver");
          CreamConvert converter = new CreamConvert();
          IAST resultList = converter.integerSolve(equationsAndInequations, equationVariables,
              userDefinedVariables, maximumNumberOfResults, engine);
          if (resultList.isPresent()) {
            EvalAttributes.sort((IASTMutable) resultList);
            return resultList;
          }
        }
      } catch (LimitException le) {
        LOGGER.debug("Solve.of() failed", le);
        throw le;
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), "Integers solution not found", rex);
        return F.NIL;
      }
    }
    return F.NIL;
  }

  private static boolean chocoSolver(IExpr x) {
    return x.isPower() && (!x.second().isInteger() || x.second().greaterEqualThan(3).isTrue());
  }

  /**
   * Check if all solutions are in the given domain (currently only <code>Reals</code> is checked).
   *
   * @param expr
   * @param domain
   * @return
   */
  private static IExpr checkDomain(IExpr expr, ISymbol domain) {
    if (expr.isListOfRules() && expr.argSize() > 0) {
      expr = F.list(expr);
    }
    if (expr.isList() && domain.equals(S.Reals)) {
      if (expr.isListOfLists()) {
        return F.mapList((IAST) expr, x -> {
          final IAST listOfRules = (IAST) x;
          if (!isComplex(listOfRules)) {
            return listOfRules;
          }
          return F.NIL;
        });
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

  // /**
  // * Check if all rules in the list return a real result. Try to get a real solution from
  // * <code>ConditionalExpression</code>s.
  // *
  // * @param listOfRules a list of rules <code>Rule(variable, value)</code>
  // * @return
  // */
  // private static boolean isReal(IASTAppendable listOfRules) {
  // if (listOfRules.isListOfRules(false)) {
  // for (int i = 1; i < listOfRules.size(); i++) {
  // IAST rule = listOfRules.getAST(i);
  // IExpr rhs = rule.second();
  // if (!rhs.isRealResult()) {
  // try {
  // IExpr res = rhs.replaceAll(x -> rewriteConditionalExpressionIfReal(x));
  // if (res.isPresent()) {
  // listOfRules.set(i, rule.setAtCopy(2, res));
  // continue;
  // }
  // return false;
  // } catch (NoEvalException noeex) {
  // return false;
  // }
  // }
  // }
  // return true;
  // }
  // return false;
  // }

  // /**
  // * If <code>expr</code> is a <code>ConditionalExpression</code> try to rewrite <code>expr</code>
  // * as a real expression.
  // *
  // * @param expr
  // * @return {@link F#NIL} if no <code>ConditionalExpression</code> was matched
  // * @throws NoEvalException if <code>ConditionalExpression</code> can not be rewritten as a real
  // * result.
  // */
  // private static IExpr rewriteConditionalExpressionIfReal(IExpr expr) throws NoEvalException {
  // if (expr.isConditionalExpression()) {
  // IExpr function = expr.first();
  // IExpr condition = expr.last();
  // IExpr result = EvalEngine.get().evaluate(F.Refine(F.Re(function), condition));
  // if (result.isRealResult()) {
  // return result;
  // }
  // throw NoEvalException.CONST;
  // }
  // return F.NIL;
  // }

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
   *         solutions, <code>F.NIL</code> otherwise.
   */
  public static IExpr solveRecursive(IASTMutable termsEqualZeroList, IAST inequationsList,
      boolean numericFlag, IAST variables, EvalEngine engine) {
    IASTMutable temp = solveTimesEquationsRecursively(termsEqualZeroList, inequationsList,
        numericFlag, variables, true, engine);
    if (temp.isPresent()) {
      return solveNumeric(QuarticSolver.sortASTArguments(temp), numericFlag, engine);
    }

    if (inequationsList.isEmpty() && termsEqualZeroList.size() == 2 && variables.size() == 2) {
      IExpr firstVariable = variables.arg1();
      IExpr res =
          eliminateOneVariable(termsEqualZeroList, firstVariable, true, numericFlag, engine);
      if (res.isNIL()) {
        if (numericFlag) {
          // find numerically with start value 0
          res =
              engine.evalQuiet(F.FindRoot(termsEqualZeroList.arg1(), F.list(firstVariable, F.C0)));
        }
      }
      if (!res.isList() || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
        return F.NIL;
      }
      IASTAppendable resultList = F.ListAlloc(1);
      resultList.append(res);
      IASTMutable crossChecking = crossChecking(termsEqualZeroList, resultList, engine);
      if (crossChecking.argSize() != 1) {
        return F.CEmptyList;
      }
      return solveNumeric(res, numericFlag, engine);
    }

    if (termsEqualZeroList.size() > 2 && variables.size() >= 3) {
      // expensive recursion try
      IExpr firstEquation = termsEqualZeroList.arg1();
      IExpr firstVariable = variables.arg1();
      IAST[] reduced = Eliminate.eliminateOneVariable(F.list(F.Equal(firstEquation, F.C0)),
          firstVariable, true, engine);
      if (reduced != null) {
        variables = variables.splice(1);
        termsEqualZeroList = termsEqualZeroList.removeAtCopy(1);
        // oneVariableRule = ( firstVariable -> reducedExpression )
        IAST oneVariableRule = reduced[1];
        IExpr replaced = termsEqualZeroList.replaceAll(oneVariableRule);
        if (replaced.isList()) {
          IExpr subResult = solveRecursive((IASTMutable) replaced, inequationsList, numericFlag,
              variables, engine);
          if (subResult.isListOfLists()) {
            return F.mapList((IAST) subResult, t -> {
              final IAST listOfRules = (IAST) t;
              IExpr replaceAllExpr = oneVariableRule.second().replaceAll(listOfRules);
              if (replaceAllExpr.isPresent()) {
                replaceAllExpr = S.Simplify.of(engine, replaceAllExpr);
                return listOfRules.appendClone(F.Rule(firstVariable, replaceAllExpr));
              }
              return F.NIL;
            });
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
   * @param numeric evaluate in numericMode
   * @param engine
   * @return
   */
  private static IAST eliminateOneVariable(IAST termsEqualZeroList, IExpr variable,
      boolean multipleValues, boolean numeric, EvalEngine engine) {
    if (!termsEqualZeroList.arg1().isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(),
        true)) {
      return F.NIL;
    }
    // copy the termsEqualZeroList back to a list of F.Equal(...) expressions
    // because Eliminate() operates on equations.
    IAST equalsASTList = termsEqualZeroList.mapThread(F.Equal(F.Slot1, F.C0), 1);
    IAST[] tempAST =
        Eliminate.eliminateOneVariable(equalsASTList, variable, multipleValues, engine);
    if (tempAST != null) {
      IAST lastRuleUsedForVariableElimination = tempAST[1];
      if (lastRuleUsedForVariableElimination != null) {
        if (lastRuleUsedForVariableElimination.isRule()
            && lastRuleUsedForVariableElimination.second().isTrue()) {
          return F.CEmptyList;
        }
        if (numeric && lastRuleUsedForVariableElimination.arg2().isConditionalExpression()) {
          // evaluate numerically
          IAST conditionalExpression = (IAST) lastRuleUsedForVariableElimination.arg2();
          if (conditionalExpression.arg2().isAST(S.Element, 3)) {
            IAST element = (IAST) conditionalExpression.arg2();
            IExpr constantSymbol = element.arg1();
            IExpr domain = element.arg2();
            if (constantSymbol.isAST(S.C, 2) //
                && (domain == S.Integers || domain == S.Reals || domain == S.Complexes)) {
              // try constant value = 0.0
              IAST temp = substituteConstantSymbolByValue(conditionalExpression.arg1(),
                  constantSymbol, F.CD0, lastRuleUsedForVariableElimination, engine);
              if (temp.isPresent()) {
                lastRuleUsedForVariableElimination = temp;
              } else {
                // try constant value = 1.0
                lastRuleUsedForVariableElimination =
                    substituteConstantSymbolByValue(conditionalExpression.arg1(), constantSymbol,
                        F.CD1, lastRuleUsedForVariableElimination, engine)
                            .orElse(lastRuleUsedForVariableElimination);
              }
            }
          }

        }

        if (lastRuleUsedForVariableElimination.isList()) {
          IAST list = lastRuleUsedForVariableElimination;
          return F.mapList(list, x -> F.list(x));
        }
        return F.list(F.list(lastRuleUsedForVariableElimination));
      }
    }
    return F.NIL;
  }

  /**
   * Substitute all (sub-) expressions <code>constantSymbol</code> in <code>expr</code> with
   * <code>numericValue</code>. If the substitution result is no number, the method returns
   * {@link F#NIL}
   * 
   * @param expr
   * @param constantSymbol
   * @param numericValue
   * @param lastRuleUsedForVariableElimination
   * @param engine
   * @return {@link F#NIL} if the substitution result is no number
   */
  private static IAST substituteConstantSymbolByValue(IExpr expr, IExpr constantSymbol,
      IExpr numericValue, IAST lastRuleUsedForVariableElimination, EvalEngine engine) {
    IExpr numericResult = engine.evalN(F.subs(expr, constantSymbol, numericValue));
    if (numericResult.isNumber()) {
      // Inverse functions are being used. Values may be lost for multivalued inverses.
      IOFunctions.printMessage(S.Solve, "ifun", F.List());
      return lastRuleUsedForVariableElimination.setAtCopy(2, numericResult);
    }
    return F.NIL;
  }

  /**
   * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
   * @param variables the variables for which the equations should be solved
   * @param maximumNumberOfResults the maximum number of results which should be returned
   * @param engine the evaluation engine
   * @return a &quot;list of rules list&quot; which solves the equations, or an empty list if no
   *         solution exists, or <code>F.NIL</code> if the equations are not solvable by this
   *         algorithm.
   */
  protected static IASTMutable solveEquations(IASTMutable termsEqualZeroList, IAST inequationsList,
      IAST variables, int maximumNumberOfResults, EvalEngine engine) {
    try {
      IASTMutable list = PolynomialFunctions.solveGroebnerBasis(termsEqualZeroList, variables);
      if (list.isPresent()) {
        termsEqualZeroList = list;
      }
    } catch (JASConversionException e) {
      LOGGER.debug("Solve.solveEquations() failed", e);
    }

    // rewrite some special expressions
    for (int i = 1; i < termsEqualZeroList.size(); i++) {
      IExpr equationTerm = termsEqualZeroList.get(i);
      if (equationTerm.isPlus()) {
        IExpr eq = S.Equal.of(equationTerm, F.C0);
        if (eq.isEqual()) {
          IExpr arg1 = eq.first();
          if (arg1.isPlus2()) {
            IPair p1 = arg1.first().isSqrtExpr();
            IPair p2 = arg1.second().isSqrtExpr();
            if (p1.isPresent() && p2.isPresent()) {
              // +/- Sqrt(...) +/- Sqrt() == constant
              IExpr squared = S.Expand.of(engine, F.Sqr(arg1.second()));
              IExpr expandFirstAndSqr =
                  S.Expand.of(engine, F.Sqr(F.Subtract(eq.second(), arg1.first())));
              IExpr subtractFirstAndSqr = S.Subtract.of(engine, squared, //
                  expandFirstAndSqr);
              termsEqualZeroList.set(i, //
                  subtractFirstAndSqr);
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
      resultList = analyzeSublist(analyzerList, variables, resultList, maximumNumberOfResults,
          matrix, vector, engine);
      if (vector.size() > 1) {
        // solve a linear equation <code>matrix.x == vector</code>
        FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix(matrix, vector);
        if (augmentedMatrix != null) {
          IAST subSolutionList =
              LinearAlgebra.rowReduced2RulesList(augmentedMatrix, variables, resultList, engine);
          return solveInequations((IASTMutable) subSolutionList, inequationsList, engine);
        }
        return F.NIL;
      }
      return solveInequations(resultList, inequationsList, engine);
      // return sortASTArguments(resultList);
    } catch (NoSolution e) {
      if (e.getType() == NoSolution.WRONG_SOLUTION) {
        return F.ListAlloc();
      }
      return F.NIL;
    }
  }

  protected static IASTMutable solveInequations(IASTMutable subSolutionList, IAST inequationsList,
      EvalEngine engine) {
    if (inequationsList.isEmpty()) {
      return QuarticSolver.sortASTArguments(subSolutionList);
    }

    if (subSolutionList.isListOfLists()) {
      final boolean[] isNumeric = new boolean[] {false};
      return F.mapList(subSolutionList, t -> {
        IASTMutable list = (IASTMutable) t;
        IExpr temp = F.subst(inequationsList, list);
        temp = engine.evalQuiet(temp);
        if (temp.isAST()) {
          IASTMutable[] lists = SolveUtils.filterSolveLists((IASTMutable) temp, list, isNumeric);
          if (lists[2].isPresent()) {
            if (!lists[2].isEmptyList()) {
              return lists[2];
            }
          }
        }
        return F.NIL;
      });
    }

    // TODO solve inequations here?

    return F.NIL;
  }

  /**
   * Analyze the <code>termsEqualZeroList</code> if it contains a <code>Times[..., ,...]</code>
   * expression. If true, set the factors equal to <code>0</code> and solve the equations
   * recursively.
   *
   * @param termsEqualZero the list of expressions, which should equal <code>0</code>
   * @param numericFlag
   * @param variables the variables for which the equations should be solved
   * @param engine the evaluation engine
   * @return
   */
  private static IASTMutable solveTimesEquationsRecursively(IASTMutable termsEqualZero,
      IAST inequationsList, boolean numericFlag, IAST variables, boolean multipleValues,
      EvalEngine engine) {
    IASTMutable originalTermsEqualZero = termsEqualZero.copy();
    try {
      IASTMutable resultList =
          solveEquations(termsEqualZero, inequationsList, variables, 0, engine);
      if (resultList.isPresent() && !resultList.isEmpty()) {
        return resultList;
      }
      Set<IExpr> subSolutionSet = new TreeSet<IExpr>();
      for (int i = 1; i < termsEqualZero.size(); i++) {
        IExpr termEQZero = termsEqualZero.get(i);
        if (termEQZero.isTimes()) {
          solveTimesAST((IAST) termEQZero, termsEqualZero, inequationsList, numericFlag, variables,
              multipleValues, engine, subSolutionSet, i);
        } else {
          if (termEQZero.isAST()) {
            // try factoring
            if (variables.argSize() == 1) {
              IExpr variable = variables.arg1();
              IExpr temp = Algebra.Factor.evaluateSolve(termEQZero, engine);
              if (temp.isList()) {
                IAST listOfValues = (IAST) temp;
                IASTAppendable listOfLists = F.ListAlloc(listOfValues.argSize());
                listOfValues.forEach(x -> listOfLists.append(F.List(F.Rule(variable, x))));
                solveInequations(listOfLists, inequationsList, engine)
                    .forEach(x -> subSolutionSet.add(x));
                continue;
              }
            }
            termEQZero = S.Factor.of(engine, termEQZero);
            if (termEQZero.isTimes()) {
              solveTimesAST((IAST) termEQZero, termsEqualZero, inequationsList, numericFlag,
                  variables, multipleValues, engine, subSolutionSet, i);
            }

          }
        }
      }
      if (subSolutionSet.size() > 0) {
        return crossChecking(originalTermsEqualZero, subSolutionSet, engine);
      }
      return resultList;
    } catch (LimitException le) {
      LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", le);
      throw le;
    } catch (RuntimeException rex) {
      LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", rex);
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    return F.NIL;
  }

  /**
   * After finding a possible solution, the process of cross-checking involves substituting the
   * values of the variables into each equation in the system and checking to see if both sides of
   * each equation are equal.
   * 
   * @param termsEqualZero terms which should be equal to <code>0</code>
   * @param subSolutionSet a set of rules which should solve the terms
   * @param engine
   * @return
   */
  private static IASTMutable crossChecking(IASTMutable termsEqualZero, Set<IExpr> subSolutionSet,
      EvalEngine engine) {
    IASTAppendable result = F.ListAlloc(subSolutionSet);
    return crossChecking(termsEqualZero, result, engine);
  }

  /**
   * After finding a possible solution, the process of cross-checking involves substituting the
   * values of the variables into each equation in the system and checking to see if both sides of
   * each equation are equal.
   * 
   * @param termsEqualZero terms which should be equal to <code>0</code>
   * @param engine
   * @param result list of result values which should be cross checked
   * @return
   */
  private static IASTMutable crossChecking(IASTMutable termsEqualZero, IASTAppendable result,
      EvalEngine engine) {
    int[] removedPositions = new int[result.size()];
    int untilPosition = 0;
    for (int j = 1; j < result.size(); j++) {
      IExpr expr = result.get(j);
      // if (expr.isFree(S.ConditionalExpression, true)) {
      // TODO cross checking for ConditionalExpression
      for (int i = 1; i < termsEqualZero.size(); i++) {
        IExpr termEQZero = termsEqualZero.get(i);
        IExpr replaceAll = termEQZero.replaceAll((IAST) expr);
        if (replaceAll.isNumericFunction()) {
          IExpr possibleZero = engine.evaluate(replaceAll);
          if (possibleZero.isNumber()) {
            if (!((INumber) possibleZero).isZero(Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              removedPositions[untilPosition++] = j;
              break;
            }
          } else {
            if (!engine.evalTrue(F.PossibleZeroQ(replaceAll))) {
              removedPositions[untilPosition++] = j;
              break;
            }
          }
        }
      }
    }
    if (untilPosition > 0) {
      return result.removePositionsAtCopy(removedPositions, untilPosition);
    }

    return result;
  }

  private static void solveTimesAST(IAST times, IAST termsEqualZeroList, IAST inequationsList,
      boolean numericFlag, IAST variables, boolean multipleValues, EvalEngine engine,
      Set<IExpr> subSolutionSet, int i) {
    IAST temp;
    for (int j = 1; j < times.size(); j++) {
      if (!times.get(j).isFree(Predicates.in(variables), true)) {
        // try to get a solution from this Times() factor
        IASTMutable clonedEqualZeroList = termsEqualZeroList.setAtCopy(i, times.get(j));
        temp = solveEquations(clonedEqualZeroList, inequationsList, variables, 0, engine);
        if (temp.size() > 1) {
          for (int k = 1; k < temp.size(); k++) {
            IExpr solution = temp.get(k);
            IExpr replaceAll = engine.evalQuiet(F.ReplaceAll(times, solution));
            IExpr zeroCrossCheck = engine.evalN(replaceAll);
            if (zeroCrossCheck.isZero()) {
              subSolutionSet.add(solution);
            } else {
              if (replaceAll.isPlusTimesPower() && //
                  S.PossibleZeroQ.ofQ(engine, replaceAll)) {
                subSolutionSet.add(solution);
              }
            }
          }
        } else {

          if (clonedEqualZeroList.size() == 2 && variables.size() == 2) {
            IExpr firstVariable = variables.arg1();
            IExpr res = eliminateOneVariable(clonedEqualZeroList, firstVariable, multipleValues,
                numericFlag, engine);
            if (res.isNIL()) {
              if (numericFlag) {
                // find numerically with start value 0
                res = S.FindRoot.ofNIL(engine, clonedEqualZeroList.arg1(),
                    F.list(firstVariable, F.C0));
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
