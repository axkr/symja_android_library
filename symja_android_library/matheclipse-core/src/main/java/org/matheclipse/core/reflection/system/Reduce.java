package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.eval.util.InverseFunctionExpander;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reduce.IntegerReduceEngine;

public class Reduce extends AbstractFunctionOptionEvaluator {
  // Internal signal to indicate successful absorption into the variable interval
  private static final ISymbol REDUCE_CONTINUE = F.Dummy("$Continue");

  static class ReduceComparison {

    final IExpr variable;
    final Map<IExpr, IExpr> domainMap;
    final SolveOptions options;

    /**
     * Implements value interval for <code>variable</code> as the interval: <code>
     * xMin (minType) variable (maxType) xMax</code>.
     *
     * <p>
     * <code>minType</code> and <code>maxType</code> define if it is an open interval (value == 1
     * (LessThan) ) or a closed interval (value == 2 (LessEqualThan)
     */
    private class VariableInterval {

      final IExpr variable;

      IAST intervalData;

      /**
       * Empty interval. * @param variable
       */
      public VariableInterval(IExpr variable) {
        this.variable = variable;
        this.intervalData = F.IntervalData();
      }

      public VariableInterval(IExpr min, IBuiltInSymbol minType, IExpr variable,
          IBuiltInSymbol maxType, IExpr max) {
        this.variable = variable;
        this.intervalData = F.IntervalData(F.List(min, minType, maxType, F.CInfinity));
      }

      public void set(VariableInterval cd) {
        this.intervalData = cd.intervalData.copy();
      }

      public boolean reduceOr(final VariableInterval cd) {
        return reduceOr(cd.intervalData);
      }

      /**
       * Unite the interval of the variable with the given interval set.
       *
       * @return <code>false</code> if the union couldn't be computed
       */
      public boolean reduceOr(final IAST otherIntervalData) {
        IAST union = IntervalDataSym.union(intervalData, otherIntervalData, EvalEngine.get());
        if (union.isPresent()) {
          this.intervalData = union;
          return true;
        }
        return false;
      }

      boolean isInitial() {
        return intervalData.argSize() == 4//
            && intervalData.arg1().isNegativeInfinity()//
            && intervalData.arg4().isInfinity();
      }

      private IExpr toExpr() {
        return IntervalDataSym.intervalToOr(intervalData, variable);
      }

      @Override
      public String toString() {
        return variable + "|" + intervalData.toString() + "|";
      }

      /**
       * Intersect the interval of the variable with the given interval set.
       *
       * @return <code>false</code> if the intersection couldn't be computed
       */
      public boolean reduceAnd(IAST otherIntervalData) {
        IAST intersection =
            IntervalDataSym.intersection(intervalData, otherIntervalData, EvalEngine.get());
        if (intersection.isPresent()) {
          this.intervalData = intersection;
          return true;
        }
        return false;
      }

      public IExpr reduceAnd(int headID, IExpr lhs, IExpr rhs) throws ArgumentTypeException {
        try {
          IExpr domain = domainMap.get(lhs);
          if (domain == S.Reals) {
            Complex c = rhs.evalfc();
            if (c != null && !F.isZero(c.getImaginary())) {
              // complex values are not allowed in intervals
              return S.False;
            }
          }
        } catch (ArgumentTypeException ate) {
          // fall through
        }

        IAST newIntervalData = IntervalDataSym.relationToIntervalSet(headID, rhs);
        IAST intersection =
            IntervalDataSym.intersection(intervalData, newIntervalData, EvalEngine.get());
        if (intersection.isPresent()) {
          if (intersection.isAST0()) {
            return S.False;
          }
          this.intervalData = intersection;
          return REDUCE_CONTINUE;
        }
        return F.NIL;
      }
    }

    public ReduceComparison(IExpr variable, Map<IExpr, IExpr> domainMap, SolveOptions options) {
      this.variable = variable;
      this.domainMap = domainMap;
      this.options = options;
    }

    protected IExpr evaluate(IExpr logicalExpand) throws ArgumentTypeException {
      return reduceAndOr(logicalExpand);
    }

    private IExpr reduceAndOr(IExpr expr) throws ArgumentTypeException {
      if (expr.isAST(S.And)) {
        VariableInterval cd =
            new VariableInterval(F.CNInfinity, S.Less, variable, S.Less, F.CInfinity);
        IExpr temp = reduceAnd((IAST) expr, cd);
        if (temp.isPresent()) {
          if (temp == REDUCE_CONTINUE) {
            return cd.toExpr();
          }
          return temp;
        }
        return F.NIL;
      } else if (expr.isAST(S.Or)) {
        VariableInterval cd = new VariableInterval(variable);
        IExpr temp = reduceOr((IAST) expr, cd);
        if (temp.isPresent()) {
          if (temp == REDUCE_CONTINUE) {
            return cd.toExpr();
          }
          return temp;
        }
        return F.NIL;
      }

      return F.NIL;
    }

    private IExpr reduceOr(IAST orAST, VariableInterval cd) throws ArgumentTypeException {
      if (orAST.isAST0()) {
        throw new ArgumentTypeException("Or: size == 0");
      }
      if (orAST.isAST1()) {
        return orAST.arg1();
      }
      IASTAppendable orResult = F.ast(S.Or, orAST.argSize());
      boolean orEvaled = false;
      boolean cdEvaled = false;
      for (int i = 1; i < orAST.size(); i++) {
        final IExpr arg = orAST.get(i);
        if (arg.isAST(S.And)) {
          VariableInterval andCD =
              new VariableInterval(F.CNInfinity, S.Less, variable, S.Less, F.CInfinity);
          IExpr temp = reduceAnd((IAST) arg, andCD);
          if (temp.isPresent()) {
            if (temp == REDUCE_CONTINUE) {
              if (cd.isInitial()) {
                cd.set(andCD);
              } else {
                if (!cd.reduceOr(andCD)) {
                  return F.NIL;
                }
              }
              cdEvaled = true;
              continue;
            }
            orEvaled = true;
            orResult.append(temp);
            continue;
          }
          orResult.append(arg);
        } else {
          IExpr rewritten = rewriteVariableValue(cd, arg);
          if (rewritten.isTrue()) {
            // one alternative which always holds decides the whole disjunction
            return S.True;
          }
          if (rewritten.isFalse()) {
            // an alternative which never holds adds nothing to the disjunction
            orEvaled = true;
            continue;
          }
          if (!rewritten.equals(arg)) {
            // a solved boolean combination like `(x>-1&&x<0)||x>1` is united with the interval of
            // the other alternatives instead of being kept beside it
            IAST intervals = solvedFormToIntervals(rewritten, variable);
            if (intervals.isPresent() && cd.reduceOr(intervals)) {
              cdEvaled = true;
              continue;
            }
          }
          IExpr temp = rewritten;
          if (!temp.isAST2() || !temp.first().equals(variable)) {
            temp = S.Simplify.of(arg);
          }
          if (temp.isAST2() && temp.first().equals(variable)) {
            IExpr rhs = temp.second();
            if (!(domainMap.get(variable) == S.Reals && isComplexNonReal(rhs))
                && !rhs.isRealResult()) {
              // a complex (non-real) value cannot be merged into a real interval; keep the
              // term unchanged in the Or result
              orEvaled = true;
              orResult.append(arg);
              continue;
            }
            VariableInterval comparatorCD =
                new VariableInterval(F.CNInfinity, S.Less, variable, S.Less, F.CInfinity);
            temp = comparatorCD.reduceAnd(temp.headID(), temp.first(), temp.second());
            if (temp != REDUCE_CONTINUE) {
              if (temp.isTrue()) {
                continue;
              }
              if (temp.isFalse()) {
                // // TODO ignore "Or" term
                // return S.False;
                orEvaled = true;
                continue;
              }
              return temp;
            } else {
              if (cd.isInitial()) {
                cd.set(comparatorCD);
              } else {
                if (!cd.reduceOr(comparatorCD)) {
                  return F.NIL;
                }
              }
              cdEvaled = true;
            }
            if (arg.isEqual()) {
              orEvaled = true;
              orResult.append(arg);
            }
          } else {
            // the term is no relation of the variable and cannot be merged into the interval;
            // keeping it in the Or result avoids silently dropping it from the disjunction
            orEvaled = true;
            orResult.append(arg);
          }
        }
      }
      if (orEvaled) {
        if (cdEvaled && cd.intervalData.argSize() > 0) {
          // don't lose the interval data accumulated from other Or terms, but avoid
          // duplicating equalities which were already appended to the Or result
          IExpr intervalExpr = EvalEngine.get().evaluate(cd.toExpr());
          if (intervalExpr.isOr()) {
            IAST intervalOr = (IAST) intervalExpr;
            for (int j = 1; j < intervalOr.size(); j++) {
              IExpr term = intervalOr.get(j);
              if (!orResult.contains(term)) {
                orResult.append(term);
              }
            }
          } else if (!intervalExpr.isFalse() && !orResult.contains(intervalExpr)) {
            orResult.append(intervalExpr);
          }
        }
        return orResult;
      }

      return REDUCE_CONTINUE;
    }

    /**
     * Try to reduce an {@link S#And} AST.
     *
     * @param andExpr the {@link S#And} AST
     * @param variableInterval
     * @return {@link #REDUCE_CONTINUE}, if all condition terms could be reduced (evaluated) in
     *         <code>
     * variableInterval
     * </code>, an {@link S#And} AST if some parts could be evaluated, {@link F#NIL} otherwise
     * @throws ArgumentTypeException
     */
    public IExpr reduceAnd(IAST andExpr, VariableInterval variableInterval)
        throws ArgumentTypeException {
      if (andExpr.isAST0()) {
        throw new ArgumentTypeException("And: size == 0");
      }

      boolean andEvaled = false;
      boolean variableInternalContinued = true;
      boolean prePassChanged = false;

      // Pre-pass: rewrite and perfectly flatten nested Ands generated from rewritten bounds
      IASTAppendable flatAnd = F.ast(S.And, andExpr.argSize());
      boolean prePassAbsorbed = false;
      for (int i = 1; i < andExpr.size(); i++) {
        IExpr rewritten = rewriteVariableValue(variableInterval, andExpr.get(i));
        if (rewritten.isFalse()) {
          // a condition which no value fulfills falsifies the whole conjunction
          return S.False;
        }
        if (rewritten.isTrue()) {
          // a condition which every value fulfills doesn't constrain the variable
          prePassChanged = true;
          continue;
        }
        if (rewritten.isAnd() || rewritten.isOr()) {
          // a solved boolean combination like `(x>-1&&x<0)||x>1` is intersected into the interval
          // of the variable as a whole instead of being kept beside it
          IAST intervals = solvedFormToIntervals(rewritten, variableInterval.variable);
          if (intervals.isPresent() && variableInterval.reduceAnd(intervals)) {
            if (variableInterval.intervalData.isAST0()) {
              return S.False;
            }
            prePassChanged = true;
            prePassAbsorbed = true;
            continue;
          }
        }
        if (rewritten.isAnd()) {
          flatAnd.appendArgs((IAST) rewritten);
          prePassChanged = true;
        } else {
          if (!rewritten.equals(andExpr.get(i))) {
            prePassChanged = true;
          }
          flatAnd.append(rewritten);
        }
      }

      IASTMutable andAST = flatAnd;

      if (andAST.isAST0()) {
        // everything was absorbed into the interval of the variable
        return prePassAbsorbed ? REDUCE_CONTINUE : S.True;
      }

      // conditions which are completely absorbed into the interval of the variable; every other
      // condition has to be kept in the result, otherwise the reduction silently drops it
      boolean[] absorbed = new boolean[andAST.size()];
      boolean absorbedAny = prePassAbsorbed;

      IExpr lastArg = andAST.arg1();
      int lastIndex = 1;
      IExpr temp = F.NIL;

      if (lastArg.isAST2() && lastArg.first().equals(variableInterval.variable)) {
        temp = variableInterval.reduceAnd(lastArg.headID(), lastArg.first(), lastArg.second());
        if (temp.isPresent()) {
          if (temp.isFalse()) {
            return S.False;
          }
          if (temp != REDUCE_CONTINUE) {
            andAST.set(1, temp);
            andEvaled = true;
            lastArg = temp;
          } else {
            absorbed[1] = true;
            absorbedAny = true;
          }
        }
      }
      if (temp != REDUCE_CONTINUE) {
        variableInternalContinued = false;
      }

      for (int i = 2; i < andAST.size(); i++) {
        IExpr arg = andAST.get(i);
        IExpr reducedArg = F.NIL;
        if (arg.isAST2() && arg.first().equals(variableInterval.variable)) {
          reducedArg = variableInterval.reduceAnd(arg.headID(), arg.first(), arg.second());
          if (reducedArg.isPresent()) {
            if (reducedArg.isFalse()) {
              return S.False;
            }
            if (reducedArg != REDUCE_CONTINUE) {
              andAST.set(i, reducedArg);
              andEvaled = true;
              arg = reducedArg;
            } else {
              absorbed[i] = true;
              absorbedAny = true;
            }
          }
          if (reducedArg != REDUCE_CONTINUE) {
            variableInternalContinued = false;
          }

          IASTMutable orAST = F.NIL;
          boolean evaled = false;
          if (arg.isComparatorFunction() && lastArg.isAST(S.Or)) {
            orAST = ((IAST) lastArg).copy();
            evaled = mapOrReduced(arg, orAST);
          } else if (lastArg.isComparatorFunction() && arg.isAST(S.Or)) {
            orAST = ((IAST) arg).copy();
            evaled = mapOrReduced(lastArg, orAST);
          }

          if (evaled) {
            temp = EvalEngine.get().evaluate(orAST);
            andAST.set(lastIndex, S.True);
            lastArg = temp;
            lastIndex = i;
            andAST.set(i, lastArg);
            andEvaled = true;
            // the merged disjunction replaces this position, so it is no longer represented by the
            // interval alone
            absorbed[i] = false;
          }
        } else {
          // a condition which isn't a relation `variable OP value` cannot be absorbed into the
          // interval of the variable - it has to be kept in the result
          variableInternalContinued = false;
        }
      }

      // If everything cleanly absorbed into the VariableInterval, signal CONTINUE
      // so reduceAndOr will substitute the accumulated interval bounds.
      if (variableInternalContinued) {
        return REDUCE_CONTINUE;
      }

      if (absorbedAny) {
        // combine the interval which the absorbed conditions describe with the conditions which
        // weren't absorbed into it
        IExpr intervalExpr = EvalEngine.get().evaluate(variableInterval.toExpr());
        if (intervalExpr.isFalse()) {
          return S.False;
        }
        IASTAppendable result = F.ast(S.And, andAST.argSize() + 1);
        if (!intervalExpr.isTrue()) {
          result.append(intervalExpr);
        }
        for (int i = 1; i < andAST.size(); i++) {
          if (absorbed[i]) {
            continue;
          }
          IExpr arg = andAST.get(i);
          if (arg.isTrue()) {
            continue;
          }
          if (arg.isFalse()) {
            return S.False;
          }
          result.append(arg);
        }
        if (result.isAST0()) {
          return S.True;
        }
        return result.isAST1() ? result.arg1() : result;
      }

      // If some terms didn't absorb completely, but we modified elements (either in pre-pass or
      // Or-distribution),
      // return the modified AST.
      if (andEvaled || prePassChanged) {
        return andAST.isAST1() ? andAST.arg1() : andAST;
      }

      return F.NIL;
    }

    private boolean mapOrReduced(IExpr arg, IASTMutable orAST) {
      boolean evaled = false;
      for (int j = 1; j < orAST.size(); j++) {
        IExpr r = reduceAndBinary(arg, orAST.get(j));
        if (r.isPresent()) {
          orAST.set(j, r);
          evaled = true;
        }
      }
      return evaled;
    }

    /**
     * Rewrite a relation of the reduced variable into the solved form
     * <code>variable OP value</code> (or a boolean combination of such relations), which
     * {@link VariableInterval#reduceAnd(int, IExpr, IExpr)} can absorb into the interval of the
     * variable.
     *
     * <p>
     * A relation which cannot be solved is returned unchanged; the caller keeps it in the result
     * instead of dropping it.
     *
     * @param cd the interval of the reduced variable
     * @param lastArg the relation to rewrite
     */
    private IExpr rewriteVariableValue(VariableInterval cd, IExpr lastArg) {
      if (!lastArg.isAST2() || !lastArg.isComparatorFunction()) {
        return lastArg;
      }
      if (lastArg.first().equals(cd.variable) && lastArg.second().isFree(cd.variable, true)) {
        // already in the solved form `variable OP value`
        return lastArg;
      }
      if (lastArg.isFree(cd.variable, true)) {
        return lastArg;
      }
      EvalEngine engine = EvalEngine.get();
      final boolean isEquation = lastArg.isEqual() || lastArg.isAST(S.Unequal, 3);
      if (!isEquation || domainMap.get(cd.variable) == S.Reals) {
        // An ordering relation is real valued in every domain, so its sign analysis always
        // applies. An equation is only equivalent to a set of real intervals if the variable is
        // real - over the complexes `x^2==-1` has the solutions `-I` and `I`.
        IAST intervals = realAtomToIntervals(lastArg, cd.variable, options, engine);
        if (intervals.isPresent()) {
          return intervalsToExpr(intervals, cd.variable, engine);
        }
      }
      if (lastArg.isEqual()) {
        // over the complexes all roots of a polynomial equation are solutions
        IExpr difference = engine.evaluate(F.Subtract(lastArg.first(), lastArg.second()));
        if (difference.isPolynomial(cd.variable)) {
          IExpr roots = rootsOf(F.Equal(difference, F.C0), cd.variable, options, engine);
          if (roots.isPresent() && roots.isFree(S.Roots)) {
            if (domainMap.get(cd.variable) != S.Reals) {
              return roots;
            }
            // over the reals a root which isn't real is no solution, and one whose reality depends
            // on the parameters must not be asserted - the relation is kept unreduced instead
            IExpr realRoots = keepRealRoots(roots, cd.variable);
            if (realRoots.isPresent()) {
              return realRoots;
            }
          }
        }
      }
      return lastArg;
    }

    private IExpr reduceAndBinary(IExpr arg, IExpr orArg) {
      ReduceComparison rcAnd = new ReduceComparison(variable, domainMap, options);
      IExpr reduced = rcAnd.evaluate(F.And(arg, orArg));
      if (reduced.isPresent()) {
        if (reduced == REDUCE_CONTINUE || reduced.isAST(S.And)) {
          return F.NIL;
        }
        return reduced;
      }
      return F.NIL;
    }
  }

  /**
   * Drop the roots which are provably non-real from a solved form.
   *
   * @param roots the solved form <code>variable==value</code> or a disjunction of those
   * @param variable the reduced variable
   * @return the real roots, {@link S#False} if none is real, or {@link F#NIL} if the reality of one
   *         of them isn't decided - asserting it over the reals would answer a statement which only
   *         holds for some values of the parameters
   */
  private static IExpr keepRealRoots(IExpr roots, IExpr variable) {
    IAST disjuncts = roots.isOr() ? (IAST) roots : F.list(roots);
    IASTAppendable realRoots = F.OrAlloc(disjuncts.size());
    for (int i = 1; i < disjuncts.size(); i++) {
      IExpr disjunct = disjuncts.get(i);
      if (!disjunct.isEqual() || !disjunct.first().equals(variable)) {
        return F.NIL;
      }
      IExpr value = disjunct.second();
      if (isComplexNonReal(value)) {
        continue;
      }
      if (!value.isRealResult()) {
        return F.NIL;
      }
      realRoots.append(disjunct);
    }
    if (realRoots.isAST0()) {
      return S.False;
    }
    return realRoots.isAST1() ? realRoots.arg1() : realRoots;
  }

  /**
   * Test if the expression evaluates numerically to a complex number with non-zero imaginary part.
   * Such values cannot be represented in a real {@link S#IntervalData} set.
   *
   * @param expr the expression to test
   * @return <code>true</code> if the value is complex and not real
   */
  private static boolean isComplexNonReal(IExpr expr) {
    try {
      Complex c = expr.evalfc();
      return c != null && !F.isZero(c.getImaginary());
    } catch (ArgumentTypeException ate) {
      // not numerically evaluable - assume symbolic/real handling as before
      return false;
    }
  }

  /**
   * Generate a case analysis for a univariate polynomial equation with parametric (symbolic)
   * coefficients. For example <code>a*x^2+b*x+c==0</code> is reduced to
   * <code>(a!=0&&(x==(-b-Sqrt(b^2-4*a*c))/(2*a)||x==(-b+Sqrt(b^2-4*a*c))/(2*a)))
   * ||(a==0&&b!=0&&x==-c/b)||(a==0&&b==0&&c==0)</code>.
   *
   * @return the case analysis or {@link F#NIL} if the leading coefficient cannot vanish
   *         symbolically (then the standard reduction applies)
   */
  private static IExpr reduceParametricPolynomialEquation(IAST equation, IExpr variable,
      EvalEngine engine) {
    IExpr f = engine.evaluate(F.ExpandAll(F.Subtract(equation.arg1(), equation.arg2())));
    if (f.isFree(variable) || !f.isPolynomial(variable)) {
      return F.NIL;
    }
    IExpr temp = S.CoefficientList.ofNIL(engine, f, variable);
    if (!temp.isList() || ((IAST) temp).argSize() < 2) {
      // a constant is no equation in the variable, so there is nothing to analyze
      return F.NIL;
    }
    IAST coefficientList = (IAST) temp;
    int degree = coefficientList.argSize() - 1;
    IExpr leading = coefficientList.get(degree + 1);
    if (leading.isNumericFunction()) {
      // leading coefficient cannot vanish symbolically - no case analysis necessary
      return F.NIL;
    }
    return parametricCases(coefficientList, degree, variable, engine);
  }

  /**
   * Reduce a univariate polynomial equation with parametric coefficients over the {@link S#Reals}.
   *
   * <p>
   * The roots of a parametric equation aren't real for every value of the parameters, so asserting
   * them over the reals would answer a statement which doesn't hold: <code>x^2==a</code> has no
   * real solution for a negative <code>a</code>. Every case of the leading-coefficient analysis is
   * therefore guarded by the condition under which its roots are real - the discriminant of a
   * quadratic, nothing for a linear equation. A degree for which that condition isn't known leaves
   * the equation unevaluated.
   *
   * @param equation an {@link S#Equal} equation
   * @param variable the variable to solve for
   * @param options the options of this {@code Reduce} call
   * @param engine the evaluation engine
   * @return the case analysis, or {@link F#NIL} if the equation has no parameters (the sign
   *         analysis reduces it exactly then) or its real roots aren't known
   */
  private static IExpr reduceRealParametricEquation(IAST equation, IExpr variable,
      SolveOptions options, EvalEngine engine) {
    IExpr f = engine.evaluate(F.ExpandAll(F.Subtract(equation.arg1(), equation.arg2())));
    if (f.isFree(variable) || !f.isPolynomial(variable)) {
      return F.NIL;
    }
    VariablesSet variables = new VariablesSet(f);
    if (variables.size() < 2) {
      // without parameters the sign analysis determines the real roots exactly
      return F.NIL;
    }
    IExpr temp = S.CoefficientList.ofNIL(engine, f, variable);
    if (!temp.isList() || ((IAST) temp).argSize() < 2) {
      // a constant is no equation in the variable, so there is nothing to analyze
      return F.NIL;
    }
    IAST coefficientList = (IAST) temp;
    // over the reals the parameters are real too, which decides a trivially true discriminant
    IExpr assumptions = realAssumptions(variables, variable);
    IExpr cases = realParametricCases(coefficientList, coefficientList.argSize() - 1, variable,
        assumptions, options, engine);
    if (cases.isNIL()) {
      return F.NIL;
    }
    return tidyParameterConditions(engine.evaluate(cases), variable, engine);
  }

  /**
   * The assumption that every parameter of the reduction is a real number.
   *
   * @param variables the variables of the equation
   * @param variable the reduced variable, which isn't a parameter
   */
  private static IExpr realAssumptions(VariablesSet variables, IExpr variable) {
    IAST variableList = variables.getVarList();
    IASTAppendable result = F.ast(S.And, variableList.size());
    for (int i = 1; i < variableList.size(); i++) {
      IExpr parameter = variableList.get(i);
      if (!parameter.equals(variable)) {
        result.append(F.Element(parameter, S.Reals));
      }
    }
    return result.isAST1() ? result.arg1() : result;
  }

  /**
   * Recursively build <code>(c_k!=0 &amp;&amp; realCondition &amp;&amp; roots) ||
   * (c_k==0 &amp;&amp; lower degree cases)</code> for the polynomial given by
   * <code>coefficientList</code> truncated at <code>degree</code>.
   */
  private static IExpr realParametricCases(IAST coefficientList, int degree, IExpr variable,
      IExpr assumptions, SolveOptions options, EvalEngine engine) {
    IExpr ck = coefficientList.get(degree + 1);
    if (degree == 0) {
      return F.Equal(ck, F.C0);
    }
    if (ck.isZero()) {
      // this coefficient vanishes identically - skip to the lower degree
      return realParametricCases(coefficientList, degree - 1, variable, assumptions, options,
          engine);
    }
    IExpr branch =
        realRootsOfDegree(coefficientList, degree, variable, assumptions, options, engine);
    if (branch.isNIL()) {
      return F.NIL;
    }
    if (ck.isNumericFunction()) {
      // a non-zero numeric leading coefficient - no further case analysis
      return branch;
    }
    IExpr lowerCases =
        realParametricCases(coefficientList, degree - 1, variable, assumptions, options, engine);
    if (lowerCases.isNIL()) {
      return F.NIL;
    }
    IASTAppendable result = F.OrAlloc(degree + 1);
    if (!branch.isFalse()) {
      result.append(F.And(F.Unequal(ck, F.C0), branch));
    }
    IExpr zeroCondition = F.Equal(ck, F.C0);
    if (lowerCases.isOr()) {
      // distribute the zero condition over the lower degree cases
      IAST orCases = (IAST) lowerCases;
      for (int j = 1; j < orCases.size(); j++) {
        result.append(F.And(zeroCondition, orCases.get(j)));
      }
    } else {
      result.append(F.And(zeroCondition, lowerCases));
    }
    return result;
  }

  /**
   * The roots of the polynomial with the given coefficients, guarded by the condition under which
   * they are real.
   *
   * @return {@link S#False} if the polynomial has no real root at all, or {@link F#NIL} if the
   *         condition isn't known for this degree
   */
  private static IExpr realRootsOfDegree(IAST coefficientList, int degree, IExpr variable,
      IExpr assumptions, SolveOptions options, EvalEngine engine) {
    IASTAppendable poly = F.PlusAlloc(degree + 1);
    for (int i = 0; i <= degree; i++) {
      poly.append(F.Times(coefficientList.get(i + 1), F.Power(variable, F.ZZ(i))));
    }
    IExpr roots = rootsOf(F.Equal(engine.evaluate(poly), F.C0), variable, options, engine);
    if (roots.isNIL() || !roots.isFree(S.Roots)) {
      return F.NIL;
    }
    if (roots.isFalse()) {
      return S.False;
    }

    // drop the roots which are provably non-real; the others need the condition unless every one of
    // them is provably real
    boolean allReal = true;
    IAST disjuncts = roots.isOr() ? (IAST) roots : F.list(roots);
    IASTAppendable realRoots = F.OrAlloc(disjuncts.size());
    for (int i = 1; i < disjuncts.size(); i++) {
      IExpr disjunct = disjuncts.get(i);
      if (!disjunct.isEqual() || !disjunct.first().equals(variable)) {
        return F.NIL;
      }
      IExpr value = disjunct.second();
      if (isComplexNonReal(value)) {
        continue;
      }
      if (!value.isRealResult()) {
        allReal = false;
      }
      realRoots.append(disjunct);
    }
    if (realRoots.isAST0()) {
      return S.False;
    }
    IExpr rootSet = realRoots.isAST1() ? realRoots.arg1() : realRoots;
    if (allReal) {
      return rootSet;
    }

    IExpr condition = realityCondition(coefficientList, degree, assumptions, engine);
    if (condition.isNIL()) {
      return F.NIL;
    }
    if (condition.isTrue()) {
      return rootSet;
    }
    if (condition.isFalse()) {
      return S.False;
    }
    return F.And(condition, rootSet);
  }

  /**
   * The condition under which the roots of a polynomial with real coefficients and a non-zero
   * leading coefficient are real:
   *
   * <ul>
   * <li>a linear equation always has a real root</li>
   * <li>the roots of a quadratic are real iff its discriminant is non negative</li>
   * </ul>
   *
   * @param coefficientList the coefficients of the polynomial
   * @param degree the degree at which the polynomial is truncated
   * @param assumptions the assumption that the parameters are real
   * @param engine the evaluation engine
   * @return the condition, or {@link F#NIL} if it isn't known for this degree
   */
  private static IExpr realityCondition(IAST coefficientList, int degree, IExpr assumptions,
      EvalEngine engine) {
    if (degree == 1) {
      return S.True;
    }
    if (degree != 2) {
      return F.NIL;
    }
    IExpr a0 = coefficientList.arg1();
    IExpr a1 = coefficientList.arg2();
    IExpr a2 = coefficientList.arg3();
    IExpr discriminant = F.Subtract(F.Sqr(a1), F.Times(F.C4, a2, a0));
    IExpr condition = engine.evalQuiet(F.Refine(F.GreaterEqual(discriminant, F.C0), assumptions));
    return condition.isNIL() ? engine.evaluate(F.GreaterEqual(discriminant, F.C0)) : condition;
  }

  /**
   * Reduce the conditions on the parameters of every case of a parametric reduction, so that
   * <code>a!=0&amp;&amp;a&gt;=0&amp;&amp;(...)</code> is reported as
   * <code>a&gt;0&amp;&amp;(...)</code>.
   *
   * @param cases the case analysis
   * @param variable the reduced variable
   * @param engine the evaluation engine
   */
  private static IExpr tidyParameterConditions(IExpr cases, IExpr variable, EvalEngine engine) {
    if (cases.isOr()) {
      IAST or = (IAST) cases;
      IASTAppendable result = F.OrAlloc(or.size());
      for (int i = 1; i < or.size(); i++) {
        result.append(tidyParameterConditions(or.get(i), variable, engine));
      }
      return engine.evaluate(result);
    }
    if (!cases.isAnd()) {
      return cases;
    }
    IAST and = (IAST) cases;
    IASTAppendable conditions = F.ast(S.And, and.size());
    IASTAppendable rest = F.ast(S.And, and.size());
    for (int i = 1; i < and.size(); i++) {
      IExpr arg = and.get(i);
      if (arg.isFree(variable, true)) {
        conditions.append(arg);
      } else {
        rest.append(arg);
      }
    }
    if (conditions.argSize() < 2) {
      return cases;
    }
    IExpr simplified = simplifyParameterCondition(conditions, engine);
    if (simplified.isFalse()) {
      return S.False;
    }
    IASTAppendable result = F.ast(S.And, rest.size() + 1);
    if (!simplified.isTrue()) {
      result.append(simplified);
    }
    result.appendArgs(rest);
    return engine.evaluate(result);
  }

  /**
   * Reduce a condition on the parameters of the reduction, so that
   * <code>a!=0&amp;&amp;a&gt;=0</code> is reported as <code>a&gt;0</code>.
   *
   * @param condition the condition on the parameters
   * @param engine the evaluation engine
   */
  private static IExpr simplifyParameterCondition(IExpr condition, EvalEngine engine) {
    if (condition.isTrue() || condition.isFalse()) {
      return condition;
    }
    IAST parameters = new VariablesSet(condition).getVarList();
    if (!parameters.isAST1()) {
      return condition;
    }
    IExpr reduced = engine.evalQuiet(F.Reduce(condition, parameters.arg1(), S.Reals));
    return reduced.isPresent() && reduced.isFree(S.Reduce) ? reduced : condition;
  }

  /**
   * Recursively build <code>(c_k!=0 && roots) || (c_k==0 && lower degree cases)</code> for the
   * polynomial given by <code>coefficientList</code> truncated at <code>degree</code>.
   */
  private static IExpr parametricCases(IAST coefficientList, int degree, IExpr variable,
      EvalEngine engine) {
    IExpr ck = coefficientList.get(degree + 1);
    if (degree == 0) {
      return F.Equal(ck, F.C0);
    }
    if (ck.isZero()) {
      // this coefficient vanishes identically - skip to the lower degree
      return parametricCases(coefficientList, degree - 1, variable, engine);
    }
    IASTAppendable poly = F.PlusAlloc(degree + 1);
    for (int i = 0; i <= degree; i++) {
      poly.append(F.Times(coefficientList.get(i + 1), F.Power(variable, F.ZZ(i))));
    }
    IExpr roots = S.Roots.ofNIL(engine, F.Equal(engine.evaluate(poly), F.C0), variable);
    if (roots.isNIL() || !roots.isFree(S.Roots)) {
      return F.NIL;
    }
    if (ck.isNumericFunction()) {
      // non-zero numeric leading coefficient - no further case analysis
      return roots;
    }
    IExpr lowerCases = parametricCases(coefficientList, degree - 1, variable, engine);
    if (lowerCases.isNIL()) {
      return F.NIL;
    }
    IASTAppendable result = F.OrAlloc(degree + 1);
    result.append(F.And(F.Unequal(ck, F.C0), roots));
    IExpr zeroCondition = F.Equal(ck, F.C0);
    if (lowerCases.isOr()) {
      // distribute the zero condition over the lower degree cases
      IAST orCases = (IAST) lowerCases;
      for (int j = 1; j < orCases.size(); j++) {
        result.append(F.And(zeroCondition, orCases.get(j)));
      }
    } else {
      result.append(F.And(zeroCondition, lowerCases));
    }
    return result;
  }


  /**
   * The parsed form of a single periodic term <code>amplitude*head(c1*variable+c0)</code> (or
   * <code>amplitude*base^(c1*variable+c0)</code>) plus the variable free <code>rest</code> of the
   * equation.
   */
  private static final class PeriodicTerm {
    /** the factor in front of the periodic function */
    final IExpr amplitude;
    /** the head of the periodic function, or {@link F#NIL} for a {@link S#Power} term */
    final IExpr head;
    /** the base of a {@link S#Power} term, or {@link F#NIL} for a function term */
    final IExpr base;
    /** the constant part of the (linear) argument of the periodic function */
    final IExpr c0;
    /** the coefficient of the variable in the argument of the periodic function */
    final IExpr c1;
    /** the variable free rest of the equation */
    final IExpr rest;

    PeriodicTerm(IExpr amplitude, IExpr head, IExpr base, IExpr c0, IExpr c1, IExpr rest) {
      this.amplitude = amplitude;
      this.head = head;
      this.base = base;
      this.c0 = c0;
      this.c1 = c1;
      this.rest = rest;
    }

    boolean isPower() {
      return base.isPresent();
    }
  }

  /**
   * Parse <code>f</code> as <code>amplitude*periodicFunction(c1*variable+c0) + rest</code> where
   * <code>amplitude</code>, <code>c0</code>, <code>c1</code> and <code>rest</code> are free of
   * <code>variable</code>. The periodic function is a trigonometric/hyperbolic function or an
   * exponential <code>base^(c1*variable+c0)</code>.
   *
   * @param f the left-hand side of the equation <code>f == 0</code>
   * @param variable the variable to solve for
   * @return the parsed term or <code>null</code> if <code>f</code> doesn't have this shape
   */
  private static PeriodicTerm parsePeriodicTerm(IExpr f, IExpr variable) {
    if (f.isFree(variable) || f.isPolynomial(variable)) {
      return null;
    }
    IAST plusAST = f.isPlus() ? (IAST) f : F.Plus(f);

    // split into the single variable-dependent term and the (variable-free) rest
    IExpr variableTerm = F.NIL;
    IASTAppendable restParts = F.PlusAlloc(plusAST.size());
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      if (term.isFree(variable)) {
        restParts.append(term);
      } else if (variableTerm.isNIL()) {
        variableTerm = term;
      } else {
        // more than one variable-dependent term is not supported here
        return null;
      }
    }
    if (variableTerm.isNIL()) {
      return null;
    }
    IExpr rest = restParts.oneIdentity0();

    // parse variableTerm as amplitude * periodicFunction(innerArg)
    IExpr amplitude = F.C1;
    IExpr periodicFunction = variableTerm;
    if (variableTerm.isTimes()) {
      IAST times = (IAST) variableTerm;
      IASTAppendable constParts = F.TimesAlloc(times.size());
      IExpr functionCandidate = F.NIL;
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.isFree(variable)) {
          constParts.append(factor);
        } else if (functionCandidate.isNIL()) {
          functionCandidate = factor;
        } else {
          return null;
        }
      }
      amplitude = constParts.oneIdentity1();
      periodicFunction = functionCandidate;
    }

    IExpr head = F.NIL;
    IExpr base = F.NIL;
    IExpr innerArg;
    if (periodicFunction.isPower() && periodicFunction.base().isFree(variable)) {
      // an exponential base^(c1*variable+c0) has the complex logarithm as its inverse, which
      // generates the same kind of periodic C(k) family as the trigonometric functions
      base = periodicFunction.base();
      if (base.isZero() || base.isOne() || base.isMinusOne()) {
        return null;
      }
      innerArg = periodicFunction.exponent();
    } else if (periodicFunction.isAST1() && periodicFunction.head().isBuiltInSymbol()
        && isForwardPeriodicFunction(((IBuiltInSymbol) periodicFunction.head()).ordinal())) {
      head = periodicFunction.head();
      innerArg = periodicFunction.first();
    } else {
      return null;
    }

    // the inner argument must be linear in the variable: c1*variable + c0
    IExpr[] linear = innerArg.linear(variable);
    if (linear == null) {
      return null;
    }
    if (linear[1].isZero()) {
      return null;
    }
    return new PeriodicTerm(amplitude, head, base, linear[0], linear[1], rest);
  }

  /**
   * The inverse branches of an exponential <code>base^u == value</code>:
   * <code>u == (Log(value) + 2*I*Pi*C(k))/Log(base)</code>.
   *
   * @return a {@link S#List} with the single {@link S#ConditionalExpression} branch,
   *         {@link S#False} if the equation has no solution at all, or {@link F#NIL} if it
   *         isn't supported
   */
  private static IExpr expandExponentialInverse(IExpr base, IExpr value, EvalEngine engine) {
    if (value.isZero()) {
      // a power of a non zero base is never zero
      return S.False;
    }
    IAST cN = F.C(engine.incConstantCounter());
    try {
      IExpr logValue = F.Plus(F.Log(value), F.Times(F.C2, F.CI, S.Pi, cN));
      IExpr inverse = base.isE() ? logValue : F.Divide(logValue, F.Log(base));
      return F.List(F.ConditionalExpression(engine.evaluate(inverse), F.Element(cN, S.Integers)));
    } finally {
      engine.decConstantCounter();
    }
  }

  /**
   * Reduce a single periodic/transcendental equation like <code>Sin(a*x)+b==0</code> to its
   * complete solution set using integer constants <code>C(k)</code>, e.g.
   * <code>(a==0&amp;&amp;b==0)||(C(1)&#8712;Integers&amp;&amp;a!=0&amp;&amp;(x==(-ArcSin(b)+2*Pi*C(1))/a||x==(Pi+ArcSin(b)+2*Pi*C(1))/a))</code>.
   *
   * <p>
   * The equation must consist of a single variable-dependent term
   * <code>amplitude*periodicFunction(c1*variable+c0)</code> where <code>amplitude</code>,
   * <code>c0</code> and <code>c1</code> are free of <code>variable</code>. A generalized parametric
   * case analysis is generated whenever the leading coefficient <code>c1</code> can vanish
   * symbolically.
   *
   * <p>
   * Over the {@link S#Reals} only the real members of the complex solution set are kept: a family
   * with a real period is dropped if its offset isn't real (<code>Sin(x)==2</code> has none), and a
   * family with an imaginary period contributes only its <code>C(k)==0</code> member if that one is
   * real (<code>Sinh(x)==1</code> has the single real solution <code>ArcSinh(1)</code>).
   *
   * @param equation an {@link S#Equal} equation
   * @param variable the variable to solve for
   * @param domain the reduction domain ({@link S#Reals} or {@link S#Complexes})
   * @param engine the evaluation engine
   * @return the reduced expression or {@link F#NIL} if the equation isn't a supported single
   *         periodic-function equation
   */
  private static IExpr reducePeriodicEquation(IAST equation, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    IExpr f = engine.evaluate(F.Subtract(equation.arg1(), equation.arg2()));
    PeriodicTerm term = parsePeriodicTerm(f, variable);
    if (term == null) {
      return F.NIL;
    }
    final IExpr c0 = term.c0;
    final IExpr c1 = term.c1;

    // periodicFunction(innerArg) == -rest/amplitude
    IExpr rhsValue = engine.evaluate(F.Divide(F.Negate(term.rest), term.amplitude));
    IExpr branches;
    if (term.isPower()) {
      branches = expandExponentialInverse(term.base, rhsValue, engine);
      if (branches.isFalse()) {
        return S.False;
      }
    } else {
      branches =
          InverseFunctionExpander.expandPeriodicInverse((IBuiltInSymbol) term.head, rhsValue);
    }
    if (branches.isNIL()) {
      return F.NIL;
    }
    IAST branchList = branches.isList() ? (IAST) branches : F.List(branches);
    // WMA Reduce lists the principal inverse branch first (e.g. ArcSin before
    // Pi-ArcSin). Symja's shared expander lists the Pi-shifted branch first for these functions,
    // so reverse the two branches here (Solve keeps the expander's original order).
    if (branchList.argSize() == 2 && !term.isPower()
        && isPiShiftedFirstFunction(((IBuiltInSymbol) term.head).ordinal())) {
      branchList = F.List(branchList.arg2(), branchList.arg1());
    }

    final boolean realDomain = domain == S.Reals;
    Set<IExpr> integerConditions = new LinkedHashSet<IExpr>();
    IASTAppendable orEqualities = F.OrAlloc(branchList.size());
    for (int i = 1; i < branchList.size(); i++) {
      IExpr branch = branchList.get(i);
      if (!branch.isConditionalExpression()) {
        return F.NIL;
      }
      IExpr value = branch.first();
      IExpr condition = branch.second();
      Set<IExpr> branchConditions = new LinkedHashSet<IExpr>();
      if (!collectIntegerConditions(condition, branchConditions)) {
        // not a periodic (C(k) integer) branch
        return F.NIL;
      }
      // solve c1*variable + c0 == value => variable == (value - c0)/c1
      IExpr root = engine.evaluate(F.Divide(F.Subtract(value, c0), c1));
      if (c1.isNumber() && !c1.isOne()) {
        // a numeric coefficient is divided into the offset and the period of the family, so that
        // `(Pi+2*Pi*C(1))/2` reads as `Pi/2+Pi*C(1)`; a symbolic one is kept factored out
        root = engine.evaluate(F.Expand(root));
      }
      if (realDomain) {
        IExpr realValue = realFamilyMember(value, branchConditions, engine);
        if (realValue.isNIL()) {
          // the reality of this family cannot be decided - don't guess
          return F.NIL;
        }
        if (realValue.isFalse()) {
          // this family has no real member
          continue;
        }
        if (realValue.equals(value)) {
          // the whole family is real
          integerConditions.addAll(branchConditions);
        } else {
          root = engine.evaluate(F.Divide(F.Subtract(realValue, c0), c1));
        }
        if (isComplexNonReal(root)) {
          // the coefficients of the argument make the (real) family member non-real
          continue;
        }
      } else {
        integerConditions.addAll(branchConditions);
      }
      orEqualities.append(F.Equal(variable, root));
    }
    if (orEqualities.isAST0()) {
      // every solution of the equation is non-real
      return realDomain ? degenerateOrFalse(term, variable, c1, engine) : F.NIL;
    }
    if (!realDomain && integerConditions.isEmpty()) {
      return F.NIL;
    }
    IExpr orExpr = orEqualities.isAST1() ? orEqualities.arg1() : orEqualities;

    boolean c1MayBeZero = !engine.evalTrue(F.Unequal(c1, F.C0));

    IASTAppendable genericAnd = F.ast(S.And, integerConditions.size() + 2);
    for (IExpr condition : integerConditions) {
      genericAnd.append(condition);
    }
    if (c1MayBeZero) {
      genericAnd.append(F.Unequal(c1, F.C0));
    }
    genericAnd.append(orExpr);
    IExpr genericPart = genericAnd.isAST1() ? genericAnd.arg1() : genericAnd;

    if (c1MayBeZero) {
      IExpr degeneratePart = degeneratePart(term, c1, engine);
      if (!degeneratePart.isFalse()) {
        return engine.evaluate(F.Or(degeneratePart, genericPart));
      }
    }
    return engine.evaluate(genericPart);
  }

  /**
   * Keep the real members of the solution family <code>value</code>, which contains the integer
   * parameters of <code>branchConditions</code>.
   *
   * <ul>
   * <li>a real period and a real offset describe a family of real solutions - the family is
   * returned unchanged</li>
   * <li>a real period and a non-real offset means the family has no real member at all</li>
   * <li>a non-real period leaves at most one real member, the one whose imaginary part cancels</li>
   * </ul>
   *
   * @param value the value of one solution branch, linear in the integer parameters
   * @param branchConditions the {@code Element(C(k), Integers)} conditions of the branch
   * @return the real member (or the unchanged family), {@link S#False} if the family has no real
   *         member, or {@link F#NIL} if the reality of the family cannot be decided
   */
  private static IExpr realFamilyMember(IExpr value, Set<IExpr> branchConditions,
      EvalEngine engine) {
    if (branchConditions.isEmpty()) {
      return realOrFalse(value);
    }
    if (branchConditions.size() != 1) {
      // more than one integer parameter isn't analyzed
      return F.NIL;
    }
    IExpr parameter = branchConditions.iterator().next().first();
    IExpr[] linear = value.linear(parameter);
    if (linear == null) {
      return F.NIL;
    }
    IExpr offset = linear[0];
    IExpr period = linear[1];
    double periodImaginary = imaginaryPart(period);
    if (Double.isNaN(periodImaginary)) {
      return F.NIL;
    }
    if (periodImaginary == 0.0) {
      // a real period keeps the whole family iff its offset is real
      return realOrFalse(offset).isFalse() ? S.False : value;
    }
    // a non-real period: `offset + k*period` is real for at most one integer k
    double offsetImaginary = imaginaryPart(offset);
    if (Double.isNaN(offsetImaginary)) {
      return F.NIL;
    }
    double k = -offsetImaginary / periodImaginary;
    long rounded = Math.round(k);
    if (Math.abs(k - rounded) > 1.0e-8) {
      return S.False;
    }
    IExpr member = engine.evaluate(F.subst(value, parameter, F.ZZ(rounded)));
    return realOrFalse(member);
  }

  /**
   * @return <code>expr</code> if it isn't a non-real complex number, {@link S#False} otherwise
   */
  private static IExpr realOrFalse(IExpr expr) {
    return isComplexNonReal(expr) ? S.False : expr;
  }

  /**
   * The imaginary part of the numerical value of <code>expr</code>.
   *
   * @return {@link Double#NaN} if <code>expr</code> cannot be evaluated numerically
   */
  private static double imaginaryPart(IExpr expr) {
    try {
      Complex c = expr.evalfc();
      return c == null ? Double.NaN : c.getImaginary();
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
  }

  /**
   * The degenerate case <code>c1==0</code> of a periodic equation, in which the argument of the
   * periodic function collapses to the constant <code>c0</code>.
   *
   * @return {@link S#False} if the collapsed equation has no solution
   */
  private static IExpr degeneratePart(PeriodicTerm term, IExpr c1, EvalEngine engine) {
    IExpr collapsed = term.isPower() //
        ? F.Power(term.base, term.c0) //
        : F.unaryAST1(term.head, term.c0);
    IExpr degenerateEquation =
        engine.evaluate(F.Equal(F.Plus(F.Times(term.amplitude, collapsed), term.rest), F.C0));
    if (degenerateEquation.isTrue()) {
      return F.Equal(c1, F.C0);
    }
    if (degenerateEquation.isFalse()) {
      return S.False;
    }
    return F.And(F.Equal(c1, F.C0), degenerateEquation);
  }

  /**
   * The result of a periodic equation whose generic case has no solution in the requested domain:
   * only the degenerate case <code>c1==0</code> can still contribute.
   */
  private static IExpr degenerateOrFalse(PeriodicTerm term, IExpr variable, IExpr c1,
      EvalEngine engine) {
    if (!engine.evalTrue(F.Unequal(c1, F.C0))) {
      IExpr degeneratePart = degeneratePart(term, c1, engine);
      if (!degeneratePart.isFalse()) {
        return engine.evaluate(degeneratePart);
      }
    }
    return S.False;
  }

  /** Maximum number of cases which a {@link S#Piecewise} case split may generate. */
  private static final int MAX_PIECEWISE_CASES = 64;

  /**
   * The heads whose value depends on the region the (real) variable lies in, so that a relation
   * which contains them is reduced by a case split.
   *
   * @param expr the expression to test
   * @param variable the variable of the reduction
   */
  private static boolean containsPiecewiseFunction(IExpr expr, IExpr variable) {
    if (!expr.isAST() || expr.isFree(variable, true)) {
      return false;
    }
    if (isPiecewiseHead(expr)) {
      return true;
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      if (containsPiecewiseFunction(ast.get(i), variable)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The innermost {@link S#Piecewise} sub-expression of the expression, or {@link F#NIL} if it
   * contains none. The innermost one is split first, so that the conditions of the branches of an
   * outer {@link S#Piecewise} are free of {@link S#Piecewise} when it is split in turn.
   */
  private static IExpr findPiecewise(IExpr expr) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      IExpr found = findPiecewise(ast.get(i));
      if (found.isPresent()) {
        return found;
      }
    }
    return expr.isPiecewise() != null ? expr : F.NIL;
  }

  /**
   * Rewrite a relation whose sides are {@link S#Piecewise} expressions into the disjunction of its
   * cases: <code>Piecewise({{v,c}}, d) REL r</code> becomes
   * <code>(c &amp;&amp; v REL r) || (!c &amp;&amp; d REL r)</code>. The cases of a
   * {@link S#Piecewise} are tested in order, so the condition of a case excludes the conditions of
   * the cases before it.
   *
   * @param relation the relation to split
   * @param engine the evaluation engine
   * @param budget the remaining number of cases; the split declines when it is exhausted
   * @return the disjunction of the cases or {@link F#NIL} if the relation contains no
   *         {@link S#Piecewise} or the budget is exhausted
   */
  private static IExpr splitPiecewiseRelation(IExpr relation, EvalEngine engine, int[] budget) {
    IExpr piecewise = findPiecewise(relation);
    if (piecewise.isNIL()) {
      return F.NIL;
    }
    IAST piecewiseAST = (IAST) piecewise;
    IAST rows = (IAST) piecewiseAST.arg1();
    IExpr defaultValue = piecewiseAST.isAST2() ? piecewiseAST.arg2() : F.C0;
    budget[0] -= rows.argSize();
    if (budget[0] < 0) {
      return F.NIL;
    }
    IASTAppendable orResult = F.OrAlloc(rows.size());
    IASTAppendable negations = F.ast(S.And, rows.size());
    for (int i = 1; i < rows.size(); i++) {
      IAST row = (IAST) rows.get(i);
      IExpr condition = row.arg2();
      IExpr caseRelation = F.subst(relation, piecewise, row.arg1());
      IExpr split = splitPiecewiseRelation(caseRelation, engine, budget);
      if (split.isNIL() && !findPiecewise(caseRelation).isNIL()) {
        return F.NIL;
      }
      IASTAppendable caseAnd = F.ast(S.And, negations.size() + 2);
      caseAnd.appendArgs(negations);
      caseAnd.append(condition);
      caseAnd.append(split.orElse(caseRelation));
      orResult.append(caseAnd);
      negations.append(engine.evaluate(F.Not(condition)));
    }
    IExpr defaultRelation = F.subst(relation, piecewise, defaultValue);
    IExpr defaultSplit = splitPiecewiseRelation(defaultRelation, engine, budget);
    if (defaultSplit.isNIL() && !findPiecewise(defaultRelation).isNIL()) {
      return F.NIL;
    }
    IASTAppendable defaultAnd = F.ast(S.And, negations.size() + 1);
    defaultAnd.appendArgs(negations);
    defaultAnd.append(defaultSplit.orElse(defaultRelation));
    orResult.append(defaultAnd);
    return orResult;
  }

  /**
   * Rewrite a condition which contains a piecewise defined function of the (real) variable - like
   * {@link S#Abs}, {@link S#Max} or {@link S#UnitStep} - into the disjunction of its cases, so that
   * every case is a relation which the standard reduction can solve. For example
   * <code>Abs(x)==1</code> becomes <code>(x&lt;0&amp;&amp;-x==1)||(x&gt;=0&amp;&amp;x==1)</code>.
   *
   * @param expr the condition of the reduction
   * @param variable the (single, real) variable
   * @param engine the evaluation engine
   * @return the case analysis or {@link F#NIL} if the condition contains no piecewise defined
   *         function of the variable, or if the split doesn't apply
   */
  private static IExpr piecewiseCaseSplit(IExpr expr, IExpr variable, EvalEngine engine) {
    if (!containsPiecewiseFunction(expr, variable)) {
      return F.NIL;
    }
    if (new VariablesSet(expr).size() != 1) {
      // the case conditions of the branches would constrain the other variables too
      return F.NIL;
    }
    int[] budget = new int[] {MAX_PIECEWISE_CASES};
    IExpr result = piecewiseCaseSplitRecursive(expr, variable, engine, budget);
    if (result.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(result);
  }

  /** Apply the piecewise case split to every relation of a boolean combination. */
  private static IExpr piecewiseCaseSplitRecursive(IExpr expr, IExpr variable, EvalEngine engine,
      int[] budget) {
    if (expr.isAnd() || expr.isOr() || expr.isNot() || expr.isList()) {
      IAST booleanAST = (IAST) expr;
      IASTMutable result = F.NIL;
      for (int i = 1; i < booleanAST.size(); i++) {
        IExpr split = piecewiseCaseSplitRecursive(booleanAST.get(i), variable, engine, budget);
        if (split.isPresent()) {
          if (result.isNIL()) {
            result = booleanAST.copy();
          }
          result.set(i, split);
        }
      }
      return result;
    }
    if (!expr.isRelational() || expr.isFree(variable, true)) {
      return F.NIL;
    }
    IAST relation = (IAST) expr;
    IASTMutable expanded = F.NIL;
    for (int i = 1; i < relation.size(); i++) {
      IExpr side = expandPiecewiseFunctions(relation.get(i), variable, engine);
      if (side.isNIL()) {
        continue;
      }
      if (expanded.isNIL()) {
        expanded = relation.copy();
      }
      expanded.set(i, side);
    }
    if (expanded.isNIL()) {
      return F.NIL;
    }
    return splitPiecewiseRelation(expanded, engine, budget);
  }

  /**
   * Rewrite every piecewise defined function of the variable into the {@link S#Piecewise} form of
   * its branches, innermost first, so that a function which doesn't distribute over
   * {@link S#Piecewise} (like <code>Sqrt</code>) keeps the {@link S#Piecewise} as its argument.
   *
   * @param expr the expression to rewrite
   * @param variable the (real) variable of the reduction
   * @param engine the evaluation engine
   * @return the rewritten expression or {@link F#NIL} if it contains no piecewise defined function
   *         of the variable
   */
  private static IExpr expandPiecewiseFunctions(IExpr expr, IExpr variable, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(variable, true)) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    IASTMutable rewritten = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = expandPiecewiseFunctions(ast.get(i), variable, engine);
      if (argument.isPresent()) {
        if (rewritten.isNIL()) {
          rewritten = ast.copy();
        }
        rewritten.set(i, argument);
      }
    }
    IExpr current = rewritten.orElse(expr);
    if (isPiecewiseHead(current)) {
      IExpr piecewise = engine.evalQuiet(F.binaryAST2(S.PiecewiseExpand, current, S.Reals));
      if (piecewise.isPresent() && piecewise.isPiecewise() != null) {
        return piecewise;
      }
    }
    return rewritten;
  }

  /** Test whether the head of the expression defines its value branch by branch. */
  private static boolean isPiecewiseHead(IExpr expr) {
    if (!expr.isAST()) {
      return false;
    }
    switch (expr.headID()) {
      case ID.Abs:
      case ID.Boole:
      case ID.Clip:
      case ID.If:
      case ID.Max:
      case ID.Min:
      case ID.Piecewise:
      case ID.Ramp:
      case ID.RealAbs:
      case ID.RealSign:
      case ID.Sign:
      case ID.UnitBox:
      case ID.UnitStep:
      case ID.Unitize:
      case ID.Which:
        return true;
      default:
        return false;
    }
  }

  /**
   * Convert a boolean combination of already solved relations <code>variable OP value</code> into
   * the {@link S#IntervalData} set it describes.
   *
   * @param expr the (rewritten) condition
   * @param variable the reduced variable
   * @return the interval set or {@link F#NIL} if <code>expr</code> isn't such a combination
   */
  private static IAST solvedFormToIntervals(IExpr expr, IExpr variable) {
    if (expr.isTrue()) {
      return IntervalDataSym.reals();
    }
    if (expr.isFalse()) {
      return F.IntervalData();
    }
    if (expr.isAnd() || expr.isOr()) {
      final boolean isAnd = expr.isAnd();
      IAST booleanAST = (IAST) expr;
      IAST result = isAnd ? IntervalDataSym.reals() : F.IntervalData();
      for (int i = 1; i < booleanAST.size(); i++) {
        IAST part = solvedFormToIntervals(booleanAST.get(i), variable);
        if (part.isNIL()) {
          return F.NIL;
        }
        result = isAnd //
            ? IntervalDataSym.intersection(result, part, EvalEngine.get()) //
            : IntervalDataSym.union(result, part, EvalEngine.get());
        if (result.isNIL()) {
          return F.NIL;
        }
      }
      return result;
    }
    if (expr.isAST(S.Element, 3) && expr.first().equals(variable) && expr.second() == S.Reals) {
      return IntervalDataSym.reals();
    }
    if (expr.isRelational() && !expr.isFree(variable, true)) {
      IAST relation = (IAST) expr;
      for (int i = 1; i < relation.size(); i++) {
        IExpr arg = relation.get(i);
        if (arg.equals(variable) || arg.isBuiltInSymbol()) {
          continue;
        }
        if (!arg.isFree(variable, true)) {
          // the relation isn't solved for the variable
          return F.NIL;
        }
        if (!arg.isRealResult() && isComplexNonReal(arg)) {
          // a complex value cannot be represented in a real interval set
          return F.NIL;
        }
      }
      return IntervalDataSym.relationToIntervalSet(relation, variable);
    }
    return F.NIL;
  }

  /**
   * Reduce a single non polynomial equation in one variable with {@link S#Solve}, which knows the
   * inverse functions the reduction itself doesn't, e.g. <code>Sqrt(x)==x-2</code> or
   * <code>Log(x)^2==1</code>.
   *
   * <p>
   * {@code Solve} may lose or add solutions, so its answer is only accepted if every value it
   * returns fulfills the equation. An empty solution list proves nothing (over the reals
   * {@code Solve} returns it for a periodic equation it declines), so it stays unevaluated.
   *
   * @param equation an {@link S#Equal} equation
   * @param variable the variable to solve for
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return the solution set or {@link F#NIL} if the equation isn't solved this way
   */
  private static IExpr reduceEquationBySolve(IAST equation, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    if (!equation.isAST2()) {
      return F.NIL;
    }
    IExpr f = engine.evaluate(F.Subtract(equation.arg1(), equation.arg2()));
    if (f.isFree(variable) || f.isPolynomial(variable)) {
      // a polynomial equation is solved exactly by `Roots`
      return F.NIL;
    }
    if (new VariablesSet(f).size() != 1) {
      // a parametric equation needs conditions on the parameters which `Solve` doesn't give
      return F.NIL;
    }
    if (domain != S.Reals && containsPiecewiseFunction(f, variable)) {
      // `Solve` returns the real solutions of a piecewise defined function like `Abs`; over the
      // complexes `Abs(x)==1` describes the whole unit circle, so the reduction declines instead
      return F.NIL;
    }
    // `Solve` returns an empty list for a periodic equation over the reals, so the complete
    // complex solution set is computed and filtered for its real members here
    IExpr solutions = engine.evalQuiet(F.Solve(equation, variable, S.Complexes));
    if (!solutions.isListOfLists() || solutions.isEmptyList()) {
      return F.NIL;
    }
    IAST solutionList = (IAST) solutions;
    Set<IExpr> integerConditions = new LinkedHashSet<IExpr>();
    IASTAppendable orEqualities = F.OrAlloc(solutionList.size());
    for (int i = 1; i < solutionList.size(); i++) {
      IExpr solution = solutionList.get(i);
      if (!solution.isList1() || !solution.first().isRule()) {
        return F.NIL;
      }
      IAST rule = (IAST) solution.first();
      if (!rule.first().equals(variable)) {
        return F.NIL;
      }
      IExpr value = rule.second();
      Set<IExpr> branchConditions = new LinkedHashSet<IExpr>();
      if (value.isConditionalExpression()) {
        if (!collectIntegerConditions(value.second(), branchConditions)) {
          return F.NIL;
        }
        value = value.first();
      }
      if (domain == S.Reals) {
        IExpr realValue = realFamilyMember(value, branchConditions, engine);
        if (realValue.isNIL()) {
          return F.NIL;
        }
        if (realValue.isFalse()) {
          continue;
        }
        if (!realValue.equals(value)) {
          branchConditions.clear();
        }
        value = realValue;
      }
      if (!verifiesEquation(f, variable, value, branchConditions, engine)) {
        return F.NIL;
      }
      integerConditions.addAll(branchConditions);
      orEqualities.append(F.Equal(variable, value));
    }
    if (orEqualities.isAST0()) {
      return domain == S.Reals ? S.False : F.NIL;
    }
    IExpr orExpr = orEqualities.isAST1() ? orEqualities.arg1() : orEqualities;
    if (integerConditions.isEmpty()) {
      return engine.evaluate(orExpr);
    }
    IASTAppendable result = F.ast(S.And, integerConditions.size() + 1);
    for (IExpr condition : integerConditions) {
      result.append(condition);
    }
    result.append(orExpr);
    return engine.evaluate(result);
  }

  /**
   * Verify that the value solves the equation <code>f == 0</code>. A solution family is verified at
   * the members with the integer parameters set to <code>0</code> and <code>1</code>.
   *
   * @param f the left-hand side of <code>f == 0</code>
   * @param variable the variable of the equation
   * @param value the value (or family) to verify
   * @param branchConditions the {@code Element(C(k), Integers)} conditions of the family
   * @param engine the evaluation engine
   */
  private static boolean verifiesEquation(IExpr f, IExpr variable, IExpr value,
      Set<IExpr> branchConditions, EvalEngine engine) {
    if (branchConditions.isEmpty()) {
      return valueSolvesEquation(f, variable, value, engine);
    }
    for (int k = 0; k <= 1; k++) {
      IExpr member = value;
      for (IExpr condition : branchConditions) {
        member = engine.evaluate(F.subst(member, condition.first(), F.ZZ(k)));
      }
      if (!valueSolvesEquation(f, variable, member, engine)) {
        return false;
      }
    }
    return true;
  }

  /** Test whether <code>f</code> vanishes at <code>variable == value</code>. */
  private static boolean valueSolvesEquation(IExpr f, IExpr variable, IExpr value,
      EvalEngine engine) {
    IExpr substituted = engine.evalQuiet(F.subst(f, variable, value));
    if (substituted.isZero()) {
      return true;
    }
    IExpr simplified = engine.evalQuiet(F.PossibleZeroQ(substituted));
    if (simplified.isTrue()) {
      return true;
    }
    if (simplified.isFalse()) {
      return false;
    }
    try {
      org.hipparchus.complex.Complex numeric = substituted.evalfc();
      return numeric != null && numeric.norm() < 1.0e-8;
    } catch (RuntimeException rex) {
      return false;
    }
  }

  /**
   * Test whether the given function head has no poles, so that the sign of
   * <code>head(u) REL c</code> only changes where the two sides are equal.
   *
   * @param head the head of the periodic function
   */
  private static boolean isPoleFreeTrigFunction(IExpr head) {
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Cos:
      case ID.Cosh:
      case ID.Sech:
      case ID.Sin:
      case ID.Sinh:
      case ID.Tanh:
        return true;
      default:
        return false;
    }
  }

  /**
   * The solution set of a conjunction which bounds the reduced variable, as an interval set.
   *
   * @param bounds the conjuncts which bound the variable
   * @param variable the reduced variable
   * @param options the options of this {@code Reduce} call
   * @param engine the evaluation engine
   * @return the interval set or {@link F#NIL} if one of the bounds isn't a real relation of the
   *         variable
   */
  private static IAST boundsToIntervals(IAST bounds, IExpr variable, SolveOptions options,
      EvalEngine engine) {
    IAST window = IntervalDataSym.reals();
    for (int i = 1; i < bounds.size(); i++) {
      IAST interval = realAtomToIntervals(bounds.get(i), variable, options, engine);
      if (interval.isNIL()) {
        return F.NIL;
      }
      window = IntervalDataSym.intersection(window, interval, engine);
      if (window.isNIL()) {
        return F.NIL;
      }
    }
    return window;
  }

  /**
   * Split the periodic solution family which {@code reducePeriodicEquation} returns into its
   * <code>Element(C(k), Integers)</code> conditions and the values of its branches.
   *
   * @param family the reduced periodic equation
   * @param variable the reduced variable
   * @param conditions collects the integer domain conditions of the family
   * @param values collects the value of every branch
   * @return <code>false</code> if the family doesn't have this shape
   */
  private static boolean splitPeriodicFamily(IExpr family, IExpr variable,
      IASTAppendable conditions, IASTAppendable values) {
    IExpr roots = family;
    if (family.isAnd()) {
      IAST and = (IAST) family;
      IExpr rest = F.NIL;
      for (int i = 1; i < and.size(); i++) {
        IExpr arg = and.get(i);
        if (arg.isAST(S.Element, 3) && arg.second() == S.Integers) {
          conditions.append(arg);
        } else if (rest.isNIL()) {
          rest = arg;
        } else {
          return false;
        }
      }
      if (rest.isNIL()) {
        return false;
      }
      roots = rest;
    }
    IAST disjuncts = roots.isOr() ? (IAST) roots : F.list(roots);
    for (int i = 1; i < disjuncts.size(); i++) {
      IExpr disjunct = disjuncts.get(i);
      if (!disjunct.isEqual() || !disjunct.first().equals(variable)) {
        return false;
      }
      values.append(disjunct.second());
    }
    return values.argSize() > 0;
  }

  /**
   * Enumerate the members of the periodic solution family which lie in the given window.
   *
   * @param values the values of the branches of the family
   * @param conditions the <code>Element(C(k), Integers)</code> conditions of the family
   * @param window one bounded sub-interval <code>{min, minType, maxType, max}</code>
   * @param engine the evaluation engine
   * @return the members sorted ascending, or <code>null</code> if the family cannot be enumerated
   */
  private static List<IExpr> periodicMembersInWindow(IAST values, IAST conditions, IAST window,
      EvalEngine engine) {
    IExpr min = window.arg1();
    IExpr max = window.arg4();
    IBuiltInSymbol minType = (IBuiltInSymbol) window.arg2();
    IBuiltInSymbol maxType = (IBuiltInSymbol) window.arg3();
    List<IExpr> exacts = new ArrayList<IExpr>();
    List<Double> numerics = new ArrayList<Double>();
    for (int i = 1; i < values.size(); i++) {
      // the enumeration reads the offset and the period of the family off the value, so a factored
      // form like `(Pi+2*Pi*C(1))/2` has to be expanded into `Pi/2+Pi*C(1)` first
      IExpr value = engine.evaluate(F.Expand(values.get(i)));
      IASTAppendable collector = F.ListAlloc(8);
      if (conditions.isAST0()) {
        // a single solution instead of a family
        if (engine.evalTrue(F.And(F.binaryAST2(minType, min, value), //
            F.binaryAST2(maxType, value, max)))) {
          collector.append(value);
        }
      } else {
        IExpr conditionalValue = F.ConditionalExpression(value, //
            conditions.isAST1() ? conditions.arg1() : conditions.setAtCopy(0, S.And));
        SolveUtils.collectConstants(conditionalValue, min, max, minType, maxType, collector,
            engine);
      }
      for (int k = 1; k < collector.size(); k++) {
        IExpr candidate = engine.evaluate(collector.get(k));
        double d;
        try {
          d = candidate.evalDouble();
        } catch (RuntimeException rex) {
          return null;
        }
        if (Double.isNaN(d) || Double.isInfinite(d)) {
          return null;
        }
        insertSortedDistinct(exacts, numerics, candidate, d);
      }
      if (exacts.size() > MAX_INTEGER_INTERVAL) {
        return null;
      }
    }
    return exacts;
  }

  /**
   * Test if the interval set is a single sub-interval with finite, numerically comparable bounds.
   */
  private static boolean isBoundedWindow(IAST window) {
    if (!window.isAST1()) {
      return false;
    }
    IAST interval = (IAST) window.arg1();
    if (interval.argSize() != 4) {
      return false;
    }
    return isFiniteBound(interval.arg1()) && isFiniteBound(interval.arg4());
  }

  /** Test if the interval bound is a finite real value. */
  private static boolean isFiniteBound(IExpr bound) {
    if (bound.isInfinity() || bound.isNegativeInfinity() || bound.isDirectedInfinity()) {
      return false;
    }
    try {
      double d = bound.evalDouble();
      return !Double.isNaN(d) && !Double.isInfinite(d);
    } catch (RuntimeException rex) {
      return false;
    }
  }

  /**
   * Reduce a conjunction of one periodic equation and inequalities which bound the variable, e.g.
   * <code>Sin(x)==1/2 &amp;&amp; 0&lt;x&lt;2*Pi</code> to <code>x==Pi/6||x==5/6*Pi</code>.
   *
   * <p>
   * The equation is solved into its periodic families, every family is enumerated over the bounded
   * window, and only the candidates which fulfill the original conjunction exactly are kept. An
   * unbounded window cannot be enumerated - the family is returned beside the bounds instead, which
   * is a sound (if not closed) form.
   *
   * @param conjunction the {@link S#And} of the periodic equation and the bounds
   * @param variable the reduced variable
   * @param options the options of this {@code Reduce} call
   * @param engine the evaluation engine
   * @return the reduced expression or {@link F#NIL} if the conjunction doesn't have this shape
   */
  private static IExpr reducePeriodicRegion(IAST conjunction, IExpr variable, SolveOptions options,
      EvalEngine engine) {
    // `And` is flat, so evaluating the expansion of a chained inequality flattens it back into a
    // conjunction of binary relations
    IExpr expanded = engine.evaluate(expandComparators(conjunction));
    if (!expanded.isAnd()) {
      return F.NIL;
    }
    IAST and = (IAST) expanded;
    IExpr equation = F.NIL;
    IASTAppendable bounds = F.ast(S.And, and.argSize());
    for (int i = 1; i < and.size(); i++) {
      IExpr arg = and.get(i);
      if (!arg.isAST2() || !arg.isComparatorFunction() || arg.isFree(variable, true)) {
        return F.NIL;
      }
      IExpr difference = engine.evaluate(F.Subtract(arg.first(), arg.second()));
      if (arg.isEqual() && !difference.isPolynomial(variable)) {
        if (equation.isPresent()) {
          // more than one transcendental equation isn't supported here
          return F.NIL;
        }
        equation = arg;
      } else {
        bounds.append(arg);
      }
    }
    if (equation.isNIL() || bounds.isAST0()) {
      return F.NIL;
    }

    IAST window = boundsToIntervals(bounds, variable, options, engine);
    if (window.isNIL()) {
      return F.NIL;
    }
    if (window.isAST0()) {
      return S.False;
    }
    IExpr family = reducePeriodicEquation((IAST) equation, variable, S.Reals, engine);
    if (family.isNIL()) {
      family = reduceEquationBySolve((IAST) equation, variable, S.Reals, engine);
    }
    if (family.isNIL()) {
      return F.NIL;
    }
    if (family.isFalse()) {
      return S.False;
    }
    IASTAppendable conditions = F.ListAlloc(2);
    IASTAppendable values = F.ListAlloc(4);
    if (!splitPeriodicFamily(family, variable, conditions, values)) {
      return F.NIL;
    }
    List<IExpr> members;
    if (conditions.isAST0()) {
      // the equation has finitely many solutions - every one of them is a candidate
      members = new ArrayList<IExpr>();
      for (int i = 1; i < values.size(); i++) {
        members.add(values.get(i));
      }
    } else if (isBoundedWindow(window)) {
      members = periodicMembersInWindow(values, conditions, (IAST) window.arg1(), engine);
      if (members == null) {
        return F.NIL;
      }
    } else {
      // an infinite family cannot be enumerated over an unbounded window - keep it beside the
      // bounds, which is sound although it isn't a closed form
      return engine.evaluate(F.And(family, IntervalDataSym.intervalToOr(window, variable)));
    }
    IASTAppendable result = F.OrAlloc(members.size());
    for (IExpr member : members) {
      IExpr verified = engine.evaluate(F.subst(conjunction, variable, member));
      if (verified.isTrue()) {
        result.append(F.Equal(variable, member));
      } else if (!verified.isFalse()) {
        // the candidate cannot be decided - don't guess
        return F.NIL;
      }
    }
    if (result.isAST0()) {
      return S.False;
    }
    return result.isAST1() ? result.arg1() : result;
  }

  /**
   * Reduce a conjunction of one trigonometric/hyperbolic inequality and inequalities which bound
   * the variable, e.g. <code>Sin(x)&gt;1/2 &amp;&amp; 0&lt;x&lt;2*Pi</code> to
   * <code>x&gt;Pi/6&amp;&amp;x&lt;5/6*Pi</code>.
   *
   * <p>
   * The zeros of <code>lhs-rhs</code> in the bounded window cut it into cells of constant sign;
   * every cell is decided by one sample and every cell boundary by an exact evaluation of the
   * original conjunction. Only pole free heads are supported - a pole is a sign change which isn't
   * a zero.
   *
   * @param conjunction the {@link S#And} of the inequality and the bounds
   * @param variable the reduced variable
   * @param options the options of this {@code Reduce} call
   * @param engine the evaluation engine
   * @return the reduced expression or {@link F#NIL} if the conjunction doesn't have this shape
   */
  private static IExpr reduceTrigInequalityRegion(IAST conjunction, IExpr variable,
      SolveOptions options, EvalEngine engine) {
    IExpr expanded = engine.evaluate(expandComparators(conjunction));
    if (!expanded.isAnd()) {
      return F.NIL;
    }
    IAST and = (IAST) expanded;
    IExpr relation = F.NIL;
    IExpr residual = F.NIL;
    IASTAppendable bounds = F.ast(S.And, and.argSize());
    for (int i = 1; i < and.size(); i++) {
      IExpr arg = and.get(i);
      if (!arg.isAST2() || !arg.isComparatorFunction() || arg.isFree(variable, true)) {
        return F.NIL;
      }
      IExpr f = engine.evaluate(F.Subtract(arg.first(), arg.second()));
      PeriodicTerm term = parsePeriodicTerm(f, variable);
      if (term != null) {
        if (relation.isPresent() || term.isPower() || !isPoleFreeTrigFunction(term.head)) {
          return F.NIL;
        }
        relation = arg;
        residual = f;
      } else {
        bounds.append(arg);
      }
    }
    if (relation.isNIL() || relation.isEqual() || bounds.isAST0()) {
      return F.NIL;
    }

    IAST window = boundsToIntervals(bounds, variable, options, engine);
    if (window.isNIL() || !isBoundedWindow(window)) {
      return F.NIL;
    }
    IAST windowInterval = (IAST) window.arg1();

    // the zeros of the residual cut the window into cells of constant sign
    IExpr zeros = reducePeriodicEquation(F.Equal(residual, F.C0), variable, S.Reals, engine);
    if (zeros.isNIL()) {
      return F.NIL;
    }
    List<IExpr> breakPoints = new ArrayList<IExpr>();
    List<Double> breakValues = new ArrayList<Double>();
    if (!zeros.isFalse()) {
      IASTAppendable conditions = F.ListAlloc(2);
      IASTAppendable values = F.ListAlloc(4);
      if (!splitPeriodicFamily(zeros, variable, conditions, values)) {
        return F.NIL;
      }
      // the zeros are collected over the closed window, so that a zero on an edge becomes a
      // breakpoint too
      IAST closedWindow =
          F.List(windowInterval.arg1(), S.LessEqual, S.LessEqual, windowInterval.arg4());
      List<IExpr> members = periodicMembersInWindow(values, conditions, closedWindow, engine);
      if (members == null) {
        return F.NIL;
      }
      for (IExpr member : members) {
        double d;
        try {
          d = member.evalDouble();
        } catch (RuntimeException rex) {
          return F.NIL;
        }
        insertSortedDistinct(breakPoints, breakValues, member, d);
      }
    }
    // the edges of the window are breakpoints as well
    double minValue;
    double maxValue;
    try {
      minValue = windowInterval.arg1().evalDouble();
      maxValue = windowInterval.arg4().evalDouble();
    } catch (RuntimeException rex) {
      return F.NIL;
    }
    insertSortedDistinct(breakPoints, breakValues, windowInterval.arg1(), minValue);
    insertSortedDistinct(breakPoints, breakValues, windowInterval.arg4(), maxValue);

    final int n = breakPoints.size();
    boolean[] regionInSolution = new boolean[n + 1];
    for (int k = 1; k < n; k++) {
      // only the cells inside the window can contribute
      double sample = (breakValues.get(k - 1).doubleValue() + breakValues.get(k).doubleValue()) / 2;
      int sign = polynomialSignAt(residual, variable, sample, engine);
      if (sign == 0) {
        return F.NIL;
      }
      regionInSolution[k] = relationHolds(relation.headID(), sign);
    }
    boolean[] pointInSolution = new boolean[n];
    for (int k = 0; k < n; k++) {
      IExpr verified = engine.evaluate(F.subst(conjunction, variable, breakPoints.get(k)));
      if (!verified.isTrue() && !verified.isFalse()) {
        return F.NIL;
      }
      pointInSolution[k] = verified.isTrue();
    }
    IAST solution = mergeRegions(breakPoints, regionInSolution, pointInSolution);
    IAST restricted = IntervalDataSym.intersection(solution, window, engine);
    if (restricted.isNIL()) {
      return F.NIL;
    }
    return intervalsToExpr(restricted, variable, engine);
  }

  /**
   * Collect {@code Element(C(k), Integers)} constraints from a {@link S#ConditionalExpression}
   * condition.
   *
   * @param condition the condition of a {@link S#ConditionalExpression}
   * @param collector the set to collect integer-domain constraints into
   * @return <code>false</code> if the condition doesn't contain an integer-domain constraint
   */
  private static boolean collectIntegerConditions(IExpr condition, Set<IExpr> collector) {
    if (condition.isAST(S.Element, 3) && condition.second() == S.Integers) {
      collector.add(condition);
      return true;
    }
    if (condition.isAnd()) {
      IAST and = (IAST) condition;
      boolean found = false;
      for (int i = 1; i < and.size(); i++) {
        if (collectIntegerConditions(and.get(i), collector)) {
          found = true;
        }
      }
      return found;
    }
    return false;
  }

  /**
   * Test whether the given function head id refers to a forward periodic (invertible) trigonometric
   * or hyperbolic function whose inverse produces infinitely many periodic branches.
   *
   * @param headID the {@link ID} of the function head
   * @return <code>true</code> for {@link S#Sin}, {@link S#Cos}, {@link S#Tan}, {@link S#Cot},
   *         {@link S#Sec}, {@link S#Csc} and their hyperbolic counterparts
   */
  private static boolean isForwardPeriodicFunction(int headID) {
    switch (headID) {
      case ID.Cos:
      case ID.Cosh:
      case ID.Cot:
      case ID.Coth:
      case ID.Csc:
      case ID.Csch:
      case ID.Sec:
      case ID.Sech:
      case ID.Sin:
      case ID.Sinh:
      case ID.Tan:
      case ID.Tanh:
        return true;
      default:
        return false;
    }
  }

  /**
   * Test whether the two-branch inverse expansion of the given function lists the
   * <code>Pi</code>-shifted branch first (as produced by the shared inverse-function expander). For
   * these functions the branches are reversed so that {@link S#Reduce} lists the principal branch
   * first, matching the reference ordering.
   *
   * @param headID the {@link ID} of the function head
   * @return <code>true</code> for {@link S#Sin}, {@link S#Csc}, {@link S#Sinh}, {@link S#Csch}
   */
  private static boolean isPiShiftedFirstFunction(int headID) {
    switch (headID) {
      case ID.Sin:
      case ID.Csc:
      case ID.Sinh:
      case ID.Csch:
        return true;
      default:
        return false;
    }
  }


  public Reduce() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    SolveOptions solveOptions = SolveOptions.of(SolveOptions.REDUCE_KEYS, options);
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    long precision = Solve.workingPrecision(ast, solveOptions.workingPrecision(), engine);
    if (precision == Solve.INVALID_PRECISION) {
      return F.NIL;
    }

    IExpr result = reduce(ast, solveOptions, engine);
    if (result.isNIL()) {
      return F.NIL;
    }
    result = renameGeneratedParameters(result, solveOptions.generatedParameters(), engine);
    if (precision != Solve.MACHINE_PRECISION_REQUESTED) {
      // the reduction itself is exact; the requested precision is applied to its result
      result = engine.evaluate(F.N(result, F.ZZ(precision)));
    }
    return result;
  }

  /**
   * Determine the roots of an equation with the {@link S#Cubics} and {@link S#Quartics} options of
   * this {@code Reduce} call, so that a general cubic or quartic is represented by inert
   * {@link S#Root} objects unless the caller asked for the explicit radicals.
   *
   * @param equation the equation to solve
   * @param variable the variable to solve for
   * @param options the options of this call
   * @param engine the evaluation engine
   */
  private static IExpr rootsOf(IExpr equation, IExpr variable, SolveOptions options,
      EvalEngine engine) {
    IExpr[] rootsOptions = options.rootsOptions();
    return S.Roots.ofNIL(engine, equation, variable, rootsOptions[0], rootsOptions[1]);
  }

  /**
   * Rename the parameters which a solution generated from {@link S#C} to the head which the
   * {@link S#GeneratedParameters} option names, so that <code>C(1)</code> becomes
   * <code>head(1)</code>.
   *
   * @param expr the reduced expression
   * @param head the value of the {@link S#GeneratedParameters} option
   * @param engine the evaluation engine
   */
  private static IExpr renameGeneratedParameters(IExpr expr, IExpr head, EvalEngine engine) {
    if (head == S.C) {
      return expr;
    }
    IExpr renamed = F.subst(expr, //
        x -> x.isAST(S.C, 2) ? F.unaryAST1(head, x.first()) : F.NIL);
    return renamed.isPresent() ? engine.evaluate(renamed) : expr;
  }

  /**
   * Test if the second argument of {@code Reduce} names the variables of the reduction: a single
   * symbol or a non empty list of symbols.
   *
   * @param variableSpecification the second argument of the {@code Reduce(...)} ast
   */
  private static boolean isVariableSpecification(IExpr variableSpecification) {
    if (variableSpecification.isSymbol()) {
      return !variableSpecification.isBuiltInSymbol();
    }
    if (variableSpecification.isList()) {
      IAST list = (IAST) variableSpecification;
      if (list.isEmpty()) {
        return false;
      }
      return list.forAll(x -> x.isSymbol() && !x.isBuiltInSymbol());
    }
    return false;
  }

  /**
   * Reduce the condition of a {@code Reduce(...)} call over the requested domain.
   *
   * @param ast the {@code Reduce(...)} ast without its options
   * @param solveOptions the options of this call
   * @param engine the evaluation engine
   */
  private static IExpr reduce(final IAST ast, SolveOptions solveOptions, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isTrue() || arg1.isFalse()) {
      return arg1;
    }

    if (ast.isAST3() && ast.arg3() == S.Integers && ast.arg2().isPresent()) {
      // Cooper elimination decides a quantified integer condition exactly. It has to run before
      // the `Resolve` route below, whose strategies reason over a continuum: they answer
      // `Exists(y, x == 2*y + 1)` with True instead of the parity condition on `x`.
      IExpr integers = IntegerReduceEngine.reduce(arg1, ast.arg2().makeList(),
          (ISymbol) ast.arg3(), engine);
      if (integers.isPresent()) {
        return integers;
      }
    }

    // quantifier elimination is implemented in `Resolve`
    if (arg1.isAST(S.ForAll) || arg1.isAST(S.Exists)) {
      ISymbol quantifierDomain = null;
      if (ast.isAST3() && ast.arg3().isSymbol()) {
        quantifierDomain = (ISymbol) ast.arg3();
      }
      IExpr quantified = Resolve.resolveQuantifier((IAST) arg1, quantifierDomain, engine);
      if (quantified.isPresent()) {
        if (quantified.isTrue() || quantified.isFalse() || (!ast.isAST2() && !ast.isAST3())) {
          return quantified;
        }
        // the elimination leaves a condition on the free variables, which is reduced in turn:
        // `ForAll(y, x^2+y^2>=1)` becomes `x^2>=1` and then `x<=-1||x>=1`
        IExpr reduced = engine.evaluate(ast.setAtCopy(1, quantified));
        return reduced.isFree(S.Reduce) ? reduced : quantified;
      }
    }

    final IAST vars;
    ISymbol domain = S.Complexes;
    if (ast.isAST3()) {
      if (ast.arg3().isSymbol()) {
        domain = (ISymbol) ast.arg3();
      } else {
        return F.NIL;
      }
    }

    // extract Element(var, domain) constraints from the input; a discrete domain
    // (Integers/Primes) overrides the requested domain for those variables
    Map<IExpr, IExpr> elementDomains = new HashMap<IExpr, IExpr>();
    IExpr strippedArg1 = extractElementDomains(arg1, elementDomains);
    if (!elementDomains.isEmpty()) {
      for (IExpr elementDomain : elementDomains.values()) {
        if (elementDomain == S.Integers || elementDomain == S.Primes) {
          domain = (ISymbol) elementDomain;
        }
      }
      arg1 = strippedArg1;
    }

    // rewrite list valued relations like `{x,y}=={1,2}` into their logical form `x==1&&y==2`, so
    // that the following steps reduce a system of scalar relations
    IExpr expandedLists = expandListRelations(arg1, engine);
    if (expandedLists.isPresent()) {
      if (expandedLists.isTrue() || expandedLists.isFalse()) {
        return expandedLists;
      }
      arg1 = expandedLists;
    }

    if (ast.isAST2() || ast.isAST3()) {
      IExpr variableSpecification = ast.arg2();
      if (!isVariableSpecification(variableSpecification)) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.Reduce, "ivar", F.list(variableSpecification), engine);
      }
      vars = variableSpecification.makeList();
    } else {
      // compute the variables from the (Element-stripped) condition
      VariablesSet eVar = new VariablesSet(arg1);
      vars = eVar.getVarList();
    }

    if (domain != S.Reals && domain != S.Complexes && domain != S.Integers && domain != S.Primes
        && domain != S.Booleans && domain != S.Rationals) {
      return F.NIL;
    }
    try {
      IExpr modulus = solveOptions.modulus();
      if (!modulus.isZero()) {
        // a non zero modulus reduces in the residue class ring instead of the requested domain
        return reduceModulus(arg1, vars, modulus, engine);
      }
      if (domain == S.Booleans) {
        return reduceBooleans(arg1, engine).orElse(F.NIL);
      }
      if (domain == S.Integers || domain == S.Primes || domain == S.Rationals) {
        // stays unevaluated (F.NIL) if no integer/rational method applies
        return reduceIntegers(arg1, vars, domain, engine);
      }

      if (!vars.isList1()) {
        IExpr multivariate = reduceMultivariate(arg1, vars, domain, solveOptions, engine);
        if (multivariate.isPresent()) {
          return multivariate;
        }
        // a multivariate system which none of the strategies reduces stays unevaluated - `Reduce`
        // never answers a statement it cannot justify
        return F.NIL;
      }

      final IExpr variable = vars.arg1();
      IExpr expr = arg1;

      // relations which don't contain the variable are conditions on the parameters of the
      // reduction; they are kept unreduced instead of being folded into the variable's interval
      IExpr withSideConditions = reduceWithSideConditions(arg1, variable, domain, engine);
      if (withSideConditions.isPresent()) {
        return withSideConditions;
      }

      if ((domain == S.Reals || domain == S.Complexes) && (expr.isAnd() || expr.isList())) {
        IAST conjunction = expr.isList() ? ((IAST) expr).setAtCopy(0, S.And) : (IAST) expr;
        // a periodic equation restricted to a bounded window has finitely many solutions, and a
        // periodic inequality restricted to one has a solution set of finitely many intervals.
        // Both have to be reduced before the interval engine sees them: it would keep only the
        // principal branch of the equation and drop the inequality.
        IExpr region = reducePeriodicRegion(conjunction, variable, solveOptions, engine);
        if (region.isNIL()) {
          region = reduceTrigInequalityRegion(conjunction, variable, solveOptions, engine);
        }
        if (region.isPresent()) {
          return region;
        }
      }

      if ((domain == S.Complexes || domain == S.Reals) && countEquations(expr, variable) > 1) {
        // more than one equation in the variable determines the parameters of the reduction too,
        // e.g. `a*x==1 && x==2` gives `x==2 && a==1/2`, so the system is solved for the parameters
        // as well - the reduced variable stays the first one of the solved form
        IExpr system =
            reduceMultivariate(expr, withParameters(vars, expr), domain, solveOptions, engine);
        if (system.isPresent() && system.isFree(S.Reduce)) {
          return system;
        }
      }

      if (expr.isEqual() && (domain == S.Complexes || domain == S.Reals)) {
        // a non polynomial equation which `Solve` can invert, e.g. `Sqrt(x)==x-2`
        IExpr solved = reduceEquationBySolve((IAST) expr, variable, domain, engine);
        if (solved.isPresent()) {
          return solved;
        }
      }

      if (domain == S.Reals || elementDomains.get(variable) == S.Reals
          || containsOrderRelation(expr, variable)) {
        // a piecewise defined function of a real variable - Abs, Max, UnitStep, ... - is reduced
        // by the case analysis of its branches
        IExpr caseSplit = piecewiseCaseSplit(expr, variable, engine);
        if (caseSplit.isPresent()) {
          if (caseSplit.isTrue() || caseSplit.isFalse()) {
            return caseSplit;
          }
          arg1 = caseSplit;
          expr = caseSplit;
        }
      }

      if (expr.isEqual() && domain == S.Complexes) {
        // case analysis for univariate polynomial equations with parametric coefficients,
        // e.g. a*x^2+b*x+c==0 for variable x with parameters a,b,c
        IExpr parametric = reduceParametricPolynomialEquation((IAST) expr, variable, engine);
        if (parametric.isPresent()) {
          return parametric;
        }
      }

      if (expr.isEqual() && domain == S.Reals) {
        // the same case analysis over the reals, where every case additionally carries the
        // condition under which its roots are real - `x^2==a` has none for a negative `a`
        IExpr parametric =
            reduceRealParametricEquation((IAST) expr, variable, solveOptions, engine);
        if (parametric.isPresent()) {
          return parametric;
        }
      }

      if (expr.isEqual() && (domain == S.Complexes || domain == S.Reals)) {
        // complete solution set for a single periodic/transcendental equation,
        // e.g. Sin(a*x)+b==0 for variable x using integer constants C(k)
        IExpr periodic = reducePeriodicEquation((IAST) expr, variable, domain, engine);
        if (periodic.isPresent()) {
          return periodic;
        }
      }

      if (arg1.isList()) {
        expr = ((IAST) expr).setAtCopy(0, S.And);
      } else if (!expr.isBooleanFunction()) {
        if (!expr.isComparatorFunction()) {
          expr = F.And(expr);
        }
      }

      expr = expandComparators(expr);

      Map<IExpr, IExpr> domainMap = new VariablesSet(expr).toMap(domain);
      // an `Element(variable, domain)` declaration of the input overrides the requested domain for
      // that variable, e.g. `Element(x, Reals) && x != 1` reduces x over the reals
      domainMap.putAll(elementDomains);
      setInequalityDomainsRecursive(expr, domainMap);

      if (domain == S.Reals || domain == S.Complexes) {
        // Inequalities are inherently real-valued, so the following two reductions are applied
        // independent of the requested domain.

        // try to decide a single univariate (polynomial) inequality globally with the help of the
        // symbolic optimizers Minimize/Maximize (e.g. x^2 + 1 > 0 is always True).
        IExpr decided = decideInequalityByExtrema(arg1, variable, engine);
        if (decided.isPresent()) {
          return decided;
        }

        // solve a single univariate polynomial inequality by a sign analysis of the real roots,
        // e.g. 4*x^3-4*x>0 reduces to the interval set (-1<x<0)||x>1
        IExpr solved = reducePolynomialInequalityReals(arg1, variable, solveOptions, engine);
        if (solved.isPresent()) {
          return solved;
        }
      }

      IExpr logicalExpand = S.LogicalExpand.of(engine, expr);
      if (logicalExpand.isTrue() || logicalExpand.isFalse()) {
        return logicalExpand;
      }
      if (!logicalExpand.isBooleanFunction()) {
        logicalExpand = F.And(logicalExpand);
      }

      if (logicalExpand.isAST(S.And) || logicalExpand.isAST(S.Or)) {
        IAST andAST = (IAST) logicalExpand;
        IASTMutable andResult = andAST.copy();
        for (int i = 1; i < andAST.size(); i++) {
          IExpr arg = andAST.get(i);
          // `Roots` returns `False` for every equation it cannot solve - an equation which doesn't
          // contain the variable as well as a non polynomial one like `Sqrt(x)==2`. Only a
          // polynomial equation in the variable is handed to it, so that a `False` really means
          // "no root" and doesn't wrongly falsify the whole conjunction.
          if (arg.isEqual() && arg.isAST2() && !arg.isFree(variable, true)) {
            IExpr difference = engine.evaluate(F.Subtract(arg.first(), arg.second()));
            if (difference.isPolynomial(variable)) {
              IExpr roots = rootsOf(arg, variable, solveOptions, engine);
              if (roots.isPresent()) {
                if (domainMap.get(variable) == S.Reals) {
                  // over the reals a non-real root is no solution, and a root whose reality
                  // depends on the parameters must not be asserted - the equation is then kept
                  // unreduced instead
                  roots = keepRealRoots(roots, variable);
                }
                if (roots.isPresent()) {
                  andResult.set(i, roots);
                }
              }
            }
          }
        }

        logicalExpand = S.LogicalExpand.of(engine, andResult);
        if (logicalExpand.isTrue() || logicalExpand.isFalse()) {
          return logicalExpand;
        }
      }

      // Re-wrap bare comparators in an And AST so ReduceComparison can process them
      if (logicalExpand.isComparatorFunction()) {
        logicalExpand = F.And(logicalExpand);
      }

      if (domainMap.get(variable) != S.Reals) {
        // `x == value` and `x != value` describe a set of points, which the real interval
        // reduction cannot represent if the variable isn't real
        IExpr pointSet = reducePointSet(logicalExpand, variable, engine);
        if (pointSet.isPresent()) {
          return pointSet;
        }
      }

      ReduceComparison rc = new ReduceComparison(variable, domainMap, solveOptions);
      // may throw ArgumentTypeException
      IExpr reduced = rc.evaluate(logicalExpand);
      if (reduced.isPresent() && domainMap.get(variable) != S.Reals
          && containsOrderRelation(reduced, variable)) {
        // `ReduceComparison` reduces with real intervals, so it renders `x != value` as
        // `x < value || x > value`. That ordering is only valid if the variable is real - and a
        // variable which the input compares with `<`/`>` is real by `setInequalityDomainsRecursive`.
        // The interval reasoning itself stays sound, so a decided result (`True`/`False`, or roots
        // without an ordering) is kept and only the ordered rendering is dropped.
        return logicalExpand;
      }
      return reduced.orElse(logicalExpand);
    } catch (ArgumentTypeException ate) {
      // the interval reduction signals a condition it cannot represent - leave the input
      // unevaluated instead of reporting an internal error
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Reduce, rex, engine);
    }
    return F.NIL;
  }

  /** Maximum number of enumerated solutions for bounded integer/Diophantine problems. */
  private static final int MAX_DIOPHANTINE_RESULTS = 20;

  /** Maximum number of integer points enumerated within a bounded interval. */
  private static final int MAX_INTEGER_INTERVAL = 1000;

  /**
   * The reduced variables followed by the parameters of the condition, so that a system which
   * determines a parameter solves it too.
   *
   * @param vars the variables of the reduction
   * @param expr the condition of the reduction
   */
  private static IAST withParameters(IAST vars, IExpr expr) {
    IAST allVariables = new VariablesSet(expr).getVarList();
    IASTAppendable result = F.ListAlloc(allVariables.size() + vars.argSize());
    result.appendArgs(vars);
    for (int i = 1; i < allVariables.size(); i++) {
      IExpr variable = allVariables.get(i);
      if (!vars.contains(variable)) {
        result.append(variable);
      }
    }
    return result;
  }

  /**
   * Count the equations of a conjunction which contain the reduced variable.
   *
   * @param expr the condition of the reduction
   * @param variable the reduced variable
   */
  private static int countEquations(IExpr expr, IExpr variable) {
    if (!expr.isAnd() && !expr.isList()) {
      return 0;
    }
    IAST and = (IAST) expr;
    int count = 0;
    for (int i = 1; i < and.size(); i++) {
      IExpr arg = and.get(i);
      if (arg.isEqual() && arg.isAST2() && !arg.isFree(variable, true)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Reduce a boolean combination of <code>variable == value</code> and
   * <code>variable != value</code> relations over a domain in which the variable isn't known to be
   * real. Such a condition describes a set of points and its complement, which the real
   * {@link S#IntervalData} reduction cannot represent - it would order the values.
   *
   * <p>
   * The relations are compared pairwise, which decides the whole fragment: in a conjunction two
   * different values contradict each other, in a disjunction two different excluded values cover
   * every value.
   *
   * @param expr the condition, a boolean combination of relations
   * @param variable the variable to reduce
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the condition isn't such a combination of relations
   */
  private static IExpr reducePointSet(IExpr expr, IExpr variable, EvalEngine engine) {
    if (expr.isAnd()) {
      return reducePointSetAnd((IAST) expr, variable, engine);
    }
    if (expr.isOr()) {
      IAST or = (IAST) expr;
      IASTAppendable relations = F.ast(S.Or, or.argSize());
      for (int i = 1; i < or.size(); i++) {
        IExpr alternative = or.get(i);
        if (alternative.isAnd()) {
          // the conjunctions of the disjunctive normal form are reduced first
          IExpr reduced = reducePointSetAnd((IAST) alternative, variable, engine);
          if (reduced.isNIL() || reduced.isAnd()) {
            return F.NIL;
          }
          if (reduced.isTrue()) {
            return S.True;
          }
          if (reduced.isFalse()) {
            continue;
          }
          alternative = reduced;
        }
        relations.append(alternative);
      }
      if (relations.isAST0()) {
        return S.False;
      }
      return reducePointSetOr(relations, variable, engine);
    }
    return F.NIL;
  }

  /**
   * Reduce a conjunction of <code>variable == value</code> / <code>variable != value</code>
   * relations. The variable cannot have two different values, and a value which is excluded by an
   * inequation cannot be the value of an equation.
   *
   * @return {@link F#NIL} if the conjunction contains another relation
   */
  private static IExpr reducePointSetAnd(IAST and, IExpr variable, EvalEngine engine) {
    IASTAppendable equalValues = F.ListAlloc(and.size());
    IASTAppendable unequalValues = F.ListAlloc(and.size());
    if (!collectPointValues(and, variable, equalValues, unequalValues)) {
      return F.NIL;
    }
    for (int i = 1; i < equalValues.size(); i++) {
      for (int j = i + 1; j < equalValues.size(); j++) {
        if (engine.evalTrue(F.Unequal(equalValues.get(i), equalValues.get(j)))) {
          // the variable cannot be two different values at once
          return S.False;
        }
      }
    }

    IASTAppendable result = F.ast(S.And, and.argSize());
    for (int i = 1; i < equalValues.size(); i++) {
      result.append(F.Equal(variable, equalValues.get(i)));
    }
    for (int i = 1; i < unequalValues.size(); i++) {
      IExpr value = unequalValues.get(i);
      boolean redundant = false;
      for (int j = 1; j < equalValues.size(); j++) {
        if (engine.evalTrue(F.Equal(equalValues.get(j), value))) {
          // the equation requires exactly the excluded value
          return S.False;
        }
        if (engine.evalTrue(F.Unequal(equalValues.get(j), value))) {
          // the equation already excludes the value
          redundant = true;
        }
      }
      if (!redundant) {
        result.append(F.Unequal(variable, value));
      }
    }
    if (result.isAST0()) {
      return S.True;
    }
    return engine.evaluate(result);
  }

  /**
   * Reduce a disjunction of <code>variable == value</code> / <code>variable != value</code>
   * relations. Two different excluded values cover every value of the variable, and a value which
   * an inequation already contains adds nothing to the disjunction.
   *
   * @return {@link F#NIL} if the disjunction contains another relation
   */
  private static IExpr reducePointSetOr(IAST or, IExpr variable, EvalEngine engine) {
    IASTAppendable equalValues = F.ListAlloc(or.size());
    IASTAppendable unequalValues = F.ListAlloc(or.size());
    if (!collectPointValues(or, variable, equalValues, unequalValues)) {
      return F.NIL;
    }
    for (int i = 1; i < unequalValues.size(); i++) {
      for (int j = i + 1; j < unequalValues.size(); j++) {
        if (engine.evalTrue(F.Unequal(unequalValues.get(i), unequalValues.get(j)))) {
          // the variable is different from at least one of two different values
          return S.True;
        }
      }
    }

    IASTAppendable result = F.ast(S.Or, or.argSize());
    for (int i = 1; i < equalValues.size(); i++) {
      IExpr value = equalValues.get(i);
      boolean redundant = false;
      for (int j = 1; j < unequalValues.size(); j++) {
        if (engine.evalTrue(F.Equal(unequalValues.get(j), value))) {
          // the value is either the excluded one or a different one
          return S.True;
        }
        if (engine.evalTrue(F.Unequal(unequalValues.get(j), value))) {
          // the inequation already contains the value
          redundant = true;
        }
      }
      if (!redundant) {
        result.append(F.Equal(variable, value));
      }
    }
    for (int i = 1; i < unequalValues.size(); i++) {
      result.append(F.Unequal(variable, unequalValues.get(i)));
    }
    if (result.isAST0()) {
      return S.False;
    }
    return engine.evaluate(result);
  }

  /**
   * Sort the relations into the values of the <code>variable == value</code> equations and the
   * values of the <code>variable != value</code> inequations.
   *
   * @return <code>false</code> if one of the relations is neither of the two
   */
  private static boolean collectPointValues(IAST relations, IExpr variable,
      IASTAppendable equalValues, IASTAppendable unequalValues) {
    for (int i = 1; i < relations.size(); i++) {
      IExpr relation = relations.get(i);
      boolean isEqual = relation.isEqual();
      if ((!isEqual && !relation.isAST(S.Unequal, 3)) //
          || !relation.first().equals(variable) //
          || !relation.second().isFree(variable, true)) {
        return false;
      }
      if (isEqual) {
        equalValues.append(relation.second());
      } else {
        unequalValues.append(relation.second());
      }
    }
    return true;
  }

  /**
   * Test if the expression orders the given variable with <code>&lt;</code>, <code>&lt;=</code>,
   * <code>&gt;</code> or <code>&gt;=</code>. Such an ordering implies that the variable is real.
   *
   * @param expr the expression to test
   * @param variable the variable of the reduction
   */
  private static boolean containsOrderRelation(IExpr expr, IExpr variable) {
    if (expr.isFunctionID(ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual)) {
      return !expr.isFree(variable, true);
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        if (containsOrderRelation(ast.get(i), variable)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Remove {@code Element(var, domain)} constraints from the given expression and collect the
   * variable to domain assignments in <code>elementDomains</code>.
   *
   * @param expr the input expression
   * @param elementDomains a map which will be filled with <code>variable -&gt; domain</code>
   *        assignments
   * @return the expression with all recognized {@code Element(var, domain)} terms removed
   */
  private static IExpr extractElementDomains(IExpr expr, Map<IExpr, IExpr> elementDomains) {
    if (expr.isAST(S.Element, 3)) {
      IExpr var = expr.first();
      IExpr dom = expr.second();
      if (var.isSymbol() && dom.isSymbol()) {
        elementDomains.put(var, dom);
        return S.True;
      }
      return expr;
    }
    if (expr.isAnd()) {
      IAST and = (IAST) expr;
      IASTAppendable rest = F.ast(S.And, and.argSize());
      boolean changed = false;
      for (int i = 1; i < and.size(); i++) {
        IExpr arg = and.get(i);
        if (arg.isAST(S.Element, 3) && arg.first().isSymbol() && arg.second().isSymbol()) {
          elementDomains.put(arg.first(), arg.second());
          changed = true;
        } else {
          rest.append(arg);
        }
      }
      if (!changed) {
        return expr;
      }
      if (rest.isAST0()) {
        return S.True;
      }
      return rest.isAST1() ? rest.arg1() : rest;
    }
    return expr;
  }

  /**
   * Rewrite list valued {@link S#Equal} / {@link S#Unequal} relations into their logical form, so
   * that the following reduction steps see a system of scalar relations. <code>{x,y}=={1,2}</code>
   * becomes <code>x==1&amp;&amp;y==2</code> and <code>{x,y}!={1,2}</code> becomes
   * <code>x!=1||y!=2</code>. Nested lists are expanded recursively.
   *
   * <p>
   * Lists of different lengths are left unchanged, because {@link S#Equal} already evaluates such a
   * relation to {@link S#False}.
   *
   * @param expr the (Element stripped) condition of {@code Reduce}
   * @param engine the evaluation engine
   * @return {@link F#NIL} if <code>expr</code> contains no list valued relation
   */
  private static IExpr expandListRelations(IExpr expr, EvalEngine engine) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    if (ast.isEqual() || ast.isAST(S.Unequal, 3)) {
      IAST components = Validate.splitListRelation(ast);
      if (components.isNIL()) {
        return F.NIL;
      }
      // every component has to hold for `Equal`, one of them for `Unequal`
      IASTAppendable logicalResult =
          ast.isEqual() ? F.AndAlloc(components.size()) : F.OrAlloc(components.size());
      logicalResult.appendArgs(components);
      return engine.evaluate(logicalResult);
    }
    if (ast.isList() || ast.isAnd() || ast.isOr() || ast.isNot()) {
      IASTMutable result = F.NIL;
      for (int i = 1; i < ast.size(); i++) {
        IExpr temp = expandListRelations(ast.get(i), engine);
        if (temp.isPresent()) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
      if (result.isPresent()) {
        return engine.evaluate(result);
      }
    }
    return F.NIL;
  }

  /**
   * Reduce a condition which mixes relations of the reduced <code>variable</code> with relations
   * that don't contain it. Only the relations of the variable are reduced; the others are
   * conditions on the parameters of the reduction and are kept as they are, e.g.
   * <code>Reduce(x==1&amp;&amp;y==2, x)</code> is <code>x==1&amp;&amp;y==2</code> and not
   * <code>x==1</code>.
   *
   * <p>
   * Without this split the single variable reduction folds every relation into the interval of
   * <code>variable</code> - regardless of which variable the relation actually constrains - and so
   * drops the conditions on the parameters or even falsifies the whole condition.
   *
   * @param arg1 the (Element stripped) condition of {@code Reduce}
   * @param variable the variable to reduce
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return {@link F#NIL} if every relation contains the variable, so that nothing has to be split
   *         off and the standard reduction applies
   */
  private static IExpr reduceWithSideConditions(IExpr arg1, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    if (arg1.isAnd() || arg1.isList()) {
      // a list of relations is a conjunction of relations
      IAST and = (IAST) arg1;
      IASTAppendable variableTerms = F.ast(S.And, and.argSize());
      IASTAppendable sideConditions = F.ast(S.And, and.argSize());
      for (int i = 1; i < and.size(); i++) {
        if (and.get(i).isFree(variable, true)) {
          sideConditions.append(and.get(i));
        } else {
          variableTerms.append(and.get(i));
        }
      }
      if (sideConditions.isAST0()) {
        return F.NIL;
      }
      if (variableTerms.isAST0()) {
        // nothing constrains the variable
        return engine.evaluate(sideConditions);
      }
      IExpr reduced = engine.evaluate(F.Reduce(variableTerms, variable, domain));
      if (!reduced.isFree(S.Reduce)) {
        return F.NIL;
      }
      if (reduced.isFalse()) {
        return S.False;
      }
      IASTAppendable result = F.ast(S.And, sideConditions.size());
      result.append(reduced);
      result.appendArgs(sideConditions);
      return engine.evaluate(result);
    }

    if (arg1.isOr()) {
      IAST or = (IAST) arg1;
      boolean hasSideCondition = false;
      for (int i = 1; i < or.size(); i++) {
        IExpr alternative = or.get(i);
        if (alternative.isFree(variable, true)) {
          hasSideCondition = true;
          break;
        }
        if (alternative.isAnd()) {
          IAST and = (IAST) alternative;
          for (int j = 1; j < and.size(); j++) {
            if (and.get(j).isFree(variable, true)) {
              hasSideCondition = true;
              break;
            }
          }
        }
      }
      if (!hasSideCondition) {
        // every alternative constrains the variable; the standard reduction can merge their
        // intervals, which this alternative-by-alternative reduction couldn't
        return F.NIL;
      }
      IASTAppendable orResult = F.ast(S.Or, or.argSize());
      for (int i = 1; i < or.size(); i++) {
        IExpr reduced = engine.evaluate(F.Reduce(or.get(i), variable, domain));
        if (!reduced.isFree(S.Reduce)) {
          return F.NIL;
        }
        orResult.append(reduced);
      }
      return engine.evaluate(orResult);
    }

    return F.NIL;
  }

  /**
   * Reduce a condition in the residue class ring of the {@link S#Modulus} option. The solutions are
   * the residue tuples which fulfill the equations, so the reduced form is the disjunction of the
   * conjunctions <code>variable == residue</code>, e.g. <code>x^2==1</code> with
   * <code>Modulus-&gt;8</code> reduces to <code>x==1||x==3||x==5||x==7</code>.
   *
   * @param arg1 the (Element stripped) condition of {@code Reduce}
   * @param vars the variables to reduce
   * @param modulus the value of the {@link S#Modulus} option
   * @param engine the evaluation engine
   * @return {@link S#False} if no residue fulfills the condition, {@link F#NIL} if the condition
   *         has no meaning in a residue class ring
   */
  private static IExpr reduceModulus(IExpr arg1, IAST vars, IExpr modulus, EvalEngine engine) {
    IAST termsList;
    try {
      termsList = Validate.checkEquationsAndInequations(F.unaryAST1(S.Reduce, arg1), 1);
    } catch (ArgumentTypeException ate) {
      return F.NIL;
    }
    IExpr solutions = SolveUtils.solveModulus(termsList, vars, modulus, S.Reduce, engine);
    if (!solutions.isList()) {
      return F.NIL;
    }
    IAST solutionList = (IAST) solutions;
    IASTAppendable branches = F.ListAlloc(solutionList.argSize());
    for (int i = 1; i < solutionList.size(); i++) {
      IExpr solution = solutionList.get(i);
      if (!solution.isListOfRules(false)) {
        return F.NIL;
      }
      IAST rules = (IAST) solution;
      branches.append(F.mapList(rules, rule -> F.Equal(rule.first(), rule.second())));
    }
    return branchesToOr(branches, vars, engine);
  }

  /**
   * Reduce a boolean formula over the {@link S#Booleans} domain. Tautologies reduce to
   * {@link S#True}, contradictions to {@link S#False}, everything else is minimized with
   * {@link S#BooleanMinimize}.
   *
   * @param expr the boolean formula (or a list of formulas which is treated as {@link S#And})
   * @param engine the evaluation engine
   * @return the reduced boolean formula
   */
  private static IExpr reduceBooleans(IExpr expr, EvalEngine engine) {
    IExpr formula = expr.isList() ? ((IAST) expr).setAtCopy(0, S.And) : expr;
    IExpr tautology = S.TautologyQ.of(engine, formula);
    if (tautology.isTrue()) {
      return S.True;
    }
    IExpr satisfiable = S.SatisfiableQ.of(engine, formula);
    if (satisfiable.isFalse()) {
      return S.False;
    }
    IExpr minimized = S.BooleanMinimize.of(engine, formula);
    return minimized.isPresent() ? minimized : formula;
  }

  /**
   * Reduce equations/inequalities over the {@link S#Integers} or {@link S#Primes} domain.
   *
   * @param arg1 the condition (equation, inequality, list or {@link S#And})
   * @param vars the list of variables
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @param engine the evaluation engine
   * @return the reduced expression, or {@link F#NIL} if no integer method applies
   */
  private static IExpr reduceIntegers(IExpr arg1, IAST vars, ISymbol domain, EvalEngine engine) {
    IExpr expr = arg1.isList() ? ((IAST) arg1).setAtCopy(0, S.And) : arg1;
    // the exact engine first: it parametrizes a linear system instead of enumerating a prefix of
    // its infinitely many solutions, and it decides congruences and quantifiers
    IExpr exact = IntegerReduceEngine.reduce(expr, vars, domain, engine);
    if (exact.isPresent()) {
      return exact;
    }
    if (vars.isList1()) {
      return reduceIntegersUnivariate(expr, vars.arg1(), domain, engine);
    }
    if (domain != S.Rationals && vars.isList2() && expr.isEqual()) {
      IExpr twoVar = reduceIntegersTwoVar((IAST) expr, vars, domain, engine);
      if (twoVar.isPresent()) {
        return twoVar;
      }
    }
    return reduceIntegerSystem(expr, vars, domain, engine);
  }

  /**
   * Reduce a system of relations over a discrete domain by enumerating the solutions which
   * {@link S#Solve} finds for it.
   *
   * @param expr the condition of the reduction
   * @param vars the variables to reduce
   * @param domain {@link S#Integers}, {@link S#Primes} or {@link S#Rationals}
   * @param engine the evaluation engine
   * @return the disjunction of the solutions, or {@link F#NIL} if the system isn't solved this way
   */
  private static IExpr reduceIntegerSystem(IExpr expr, IAST vars, ISymbol domain,
      EvalEngine engine) {
    IExpr solutions = engine.evalQuiet(F.Solve(expr, vars, domain));
    if (!solutions.isListOfLists()) {
      return F.NIL;
    }
    IAST solutionList = (IAST) solutions;
    IASTAppendable branches = F.ListAlloc(solutionList.argSize());
    for (int i = 1; i < solutionList.size(); i++) {
      IExpr solution = solutionList.get(i);
      if (!solution.isListOfRules(false)) {
        return F.NIL;
      }
      IAST rules = (IAST) solution;
      IASTAppendable branch = F.ListAlloc(rules.size());
      for (int j = 1; j < rules.size(); j++) {
        IExpr rule = rules.get(j);
        IExpr value = rule.second();
        if (value.isConditionalExpression()) {
          // a parametric family carries its `Element(C(k), Integers)` condition
          branch.append(F.Equal(rule.first(), value.first()));
          branch.append(value.second());
        } else {
          branch.append(F.Equal(rule.first(), value));
        }
      }
      branches.append(branch);
    }
    return branchesToOr(branches, vars, engine);
  }

  /**
   * Reduce a univariate condition over the integers/primes: polynomial equations are solved for
   * their integer roots, bounded inequalities are enumerated.
   */
  private static IExpr reduceIntegersUnivariate(IExpr expr, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    if (expr.isEqual()) {
      IExpr roots = S.Roots.ofNIL(engine, expr, variable);
      if (roots.isPresent()) {
        return filterIntegerEqualities(roots, variable, domain, engine);
      }
      return F.NIL;
    }
    if (domain == S.Rationals) {
      // the rational solutions of an inequality are dense, so only equations are reduced
      return F.NIL;
    }
    // inequalities: reduce over the reals first, then enumerate the integer points
    IExpr realReduced = engine.evaluate(F.Reduce(expr, F.list(variable), S.Reals));
    if (realReduced.isFree(S.Reduce)) {
      return enumerateIntegerInterval(realReduced, variable, domain, engine);
    }
    return F.NIL;
  }

  /**
   * From an {@link S#Or} of <code>variable == value</code> equalities (or a single equality) keep
   * only those whose value lies in the requested integer/prime domain.
   */
  private static IExpr filterIntegerEqualities(IExpr rootsExpr, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    IASTAppendable result = F.OrAlloc(4);
    if (rootsExpr.isOr()) {
      IAST or = (IAST) rootsExpr;
      for (int i = 1; i < or.size(); i++) {
        appendIfDomainValue(or.get(i), variable, domain, result, engine);
      }
    } else {
      appendIfDomainValue(rootsExpr, variable, domain, result, engine);
    }
    if (result.isAST0()) {
      return S.False;
    }
    return result.isAST1() ? result.arg1() : result;
  }

  /**
   * Append <code>Equal(variable, value)</code> to <code>result</code> if the equation determines an
   * integer (or prime) value for <code>variable</code>.
   */
  private static void appendIfDomainValue(IExpr equation, IExpr variable, ISymbol domain,
      IASTAppendable result, EvalEngine engine) {
    if (equation.isEqual() && equation.first().equals(variable)) {
      IExpr value = engine.evaluate(equation.second());
      if (domain == S.Rationals) {
        if (value.isRational()) {
          result.append(F.Equal(variable, value));
        }
        return;
      }
      if (value.isInteger()) {
        if (domain == S.Primes) {
          if (value.isPositive() && ((IInteger) value).isProbablePrime()) {
            result.append(F.Equal(variable, value));
          }
        } else {
          result.append(F.Equal(variable, value));
        }
      }
    }
  }

  /**
   * Enumerate the integer (or prime) points of a real solution set. A bounded interval contributes
   * its points, a one-sided interval the ray of integers beyond its bound, e.g.
   * <code>x&gt;0</code> over the {@link S#Integers} is <code>x&gt;=1</code>.
   *
   * @param realReduced the solution set over the {@link S#Reals}
   * @param variable the reduced variable
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @param engine the evaluation engine
   * @return an {@link S#Or} of equalities and rays, {@link S#False} if empty, or {@link F#NIL} if
   *         the set isn't enumerable
   */
  private static IExpr enumerateIntegerInterval(IExpr realReduced, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    IAST intervals = solvedFormToIntervals(realReduced, variable);
    if (intervals.isNIL()) {
      return F.NIL;
    }
    if (intervals.isAST0()) {
      return S.False;
    }
    IASTAppendable result = F.OrAlloc(intervals.size());
    boolean unbounded = false;
    for (int i = 1; i < intervals.size(); i++) {
      IAST interval = (IAST) intervals.get(i);
      if (interval.argSize() != 4) {
        return F.NIL;
      }
      IExpr min = interval.arg1();
      IExpr max = interval.arg4();
      final boolean lowerStrict = interval.arg2() == S.Less;
      final boolean upperStrict = interval.arg3() == S.Less;
      final boolean lowerInfinite = min.isNegativeInfinity();
      final boolean upperInfinite = max.isInfinity();
      if (lowerInfinite && upperInfinite) {
        // every integer is a solution
        return F.Element(variable, domain);
      }
      if (lowerInfinite || upperInfinite) {
        if (domain == S.Primes) {
          // an unbounded set of primes cannot be described by a ray
          return F.NIL;
        }
        unbounded = true;
        if (upperInfinite) {
          IExpr lo = ceilBound(engine.evaluate(min), lowerStrict, engine);
          if (lo.isNIL()) {
            return F.NIL;
          }
          result.append(F.GreaterEqual(variable, lo));
        } else {
          IExpr hi = floorBound(engine.evaluate(max), upperStrict, engine);
          if (hi.isNIL()) {
            return F.NIL;
          }
          result.append(F.LessEqual(variable, hi));
        }
        continue;
      }
      IExpr lo = ceilBound(engine.evaluate(min), lowerStrict, engine);
      IExpr hi = floorBound(engine.evaluate(max), upperStrict, engine);
      if (lo.isNIL() || hi.isNIL()) {
        return F.NIL;
      }
      long start = lo.toLongDefault();
      long end = hi.toLongDefault();
      if (F.isNotPresent(start) || F.isNotPresent(end)) {
        return F.NIL;
      }
      if (end - start > MAX_INTEGER_INTERVAL) {
        return F.NIL;
      }
      for (long k = start; k <= end; k++) {
        IInteger candidate = F.ZZ(k);
        if (domain == S.Primes && !(candidate.isPositive() && candidate.isProbablePrime())) {
          // a prime is positive by definition, even though the primality test accepts a negative
          continue;
        }
        result.append(F.Equal(variable, candidate));
      }
    }
    if (result.isAST0()) {
      return S.False;
    }
    IExpr solutionSet = result.isAST1() ? result.arg1() : result;
    if (unbounded) {
      // a ray describes integers only together with the domain membership; a finite set names its
      // members and needs none
      return F.And(F.Element(variable, domain), solutionSet);
    }
    return solutionSet;
  }

  /**
   * Smallest integer satisfying the lower bound, or {@link F#NIL} if it isn't determined.
   */
  private static IExpr ceilBound(IExpr value, boolean strict, EvalEngine engine) {
    IExpr ceil = engine.evaluate(F.Ceiling(value));
    if (!ceil.isInteger()) {
      return F.NIL;
    }
    // a strict lower bound `x > a` with an integer `a` excludes `a` itself
    return (strict && value.isInteger()) ? ((IInteger) ceil).inc() : ceil;
  }

  /** Largest integer satisfying the upper bound, or {@link F#NIL} if it isn't determined. */
  private static IExpr floorBound(IExpr value, boolean strict, EvalEngine engine) {
    IExpr floor = engine.evaluate(F.Floor(value));
    if (!floor.isInteger()) {
      return F.NIL;
    }
    // a strict upper bound `x < b` with an integer `b` excludes `b` itself
    return (strict && value.isInteger()) ? ((IInteger) floor).dec() : floor;
  }

  /**
   * Reduce a two-variable equation over the integers/primes. Linear equations produce a parametric
   * <code>C[1]</code> solution family; general quadratic equations are enumerated via
   * {@link NumberTheory#diophantinePolynomial(IExpr, IAST, int)}.
   */
  private static IExpr reduceIntegersTwoVar(IAST equation, IAST vars, ISymbol domain,
      EvalEngine engine) {
    IExpr x = vars.arg1();
    IExpr y = vars.arg2();
    IExpr poly = engine.evaluate(F.Expand(F.Subtract(equation.arg1(), equation.arg2())));
    if (domain == S.Integers) {
      IExpr linear = reduceLinearDiophantine(poly, x, y, engine);
      if (linear.isPresent()) {
        return linear;
      }
    }
    IAST solutions = NumberTheory.diophantinePolynomial(poly, vars, MAX_DIOPHANTINE_RESULTS);
    if (solutions.isPresent()) {
      return diophantineSolutionsToOr(solutions, domain);
    }
    return F.NIL;
  }

  /**
   * Solve a linear two-variable Diophantine equation <code>d*x + e*y + f == 0</code> and return the
   * parametric solution family
   * <code>Element(C[1], Integers) &amp;&amp; x == x0 + (e/g)*C[1] &amp;&amp;
   * y == y0 - (d/g)*C[1]</code>.
   *
   * @return the parametric solution, {@link S#False} if there is no integer solution, or
   *         {@link F#NIL} if the polynomial is not linear with integer coefficients
   */
  private static IExpr reduceLinearDiophantine(IExpr poly, IExpr x, IExpr y, EvalEngine engine) {
    IExpr dCoeff = engine.evaluate(F.Coefficient(poly, x, F.C1));
    IExpr eCoeff = engine.evaluate(F.Coefficient(poly, y, F.C1));
    IExpr constTerm = engine.evaluate(F.subst(poly, F.List(F.Rule(x, F.C0), F.Rule(y, F.C0))));
    if (!dCoeff.isInteger() || !eCoeff.isInteger() || !constTerm.isInteger()) {
      return F.NIL;
    }
    // verify the polynomial is exactly the linear form d*x + e*y + f
    IExpr check = engine.evaluate(
        F.Expand(F.Subtract(poly, F.Plus(F.Times(dCoeff, x), F.Times(eCoeff, y), constTerm))));
    if (!check.isZero()) {
      return F.NIL;
    }
    BigInteger d = ((IInteger) dCoeff).toBigNumerator();
    BigInteger e = ((IInteger) eCoeff).toBigNumerator();
    BigInteger f = ((IInteger) constTerm).toBigNumerator();
    if (d.signum() == 0 && e.signum() == 0) {
      // 0 == f : no solution when f != 0, otherwise not a genuine constraint
      return f.signum() == 0 ? F.NIL : S.False;
    }
    BigInteger g = d.gcd(e);
    if (f.mod(g.abs()).signum() != 0) {
      return S.False;
    }
    BigInteger dp = d.divide(g);
    BigInteger ep = e.divide(g);
    BigInteger fp = f.divide(g);
    BigInteger[] ext = extendedEuclid(dp, ep);
    BigInteger factor = fp.negate().multiply(ext[0]);
    BigInteger x0 = factor.multiply(ext[1]);
    BigInteger y0 = factor.multiply(ext[2]);
    IExpr c1 = F.C(1);
    IExpr xExpr = engine.evaluate(F.Plus(F.ZZ(x0), F.Times(F.ZZ(ep), c1)));
    IExpr yExpr = engine.evaluate(F.Plus(F.ZZ(y0), F.Times(F.ZZ(dp.negate()), c1)));
    return F.And(F.Element(c1, S.Integers), F.Equal(x, xExpr), F.Equal(y, yExpr));
  }

  /**
   * Extended Euclidean algorithm.
   *
   * @return an array <code>{r, s, t}</code> with <code>a*s + b*t == r</code> where <code>r</code>
   *         is the (signed) gcd produced by the iteration
   */
  private static BigInteger[] extendedEuclid(BigInteger a, BigInteger b) {
    BigInteger prevR = a;
    BigInteger r = b;
    BigInteger prevS = BigInteger.ONE;
    BigInteger s = BigInteger.ZERO;
    BigInteger prevT = BigInteger.ZERO;
    BigInteger t = BigInteger.ONE;
    while (r.signum() != 0) {
      BigInteger quotient = prevR.divide(r);
      BigInteger tempR = r;
      r = prevR.subtract(quotient.multiply(r));
      prevR = tempR;
      BigInteger tempS = s;
      s = prevS.subtract(quotient.multiply(s));
      prevS = tempS;
      BigInteger tempT = t;
      t = prevT.subtract(quotient.multiply(t));
      prevT = tempT;
    }
    return new BigInteger[] {prevR, prevS, prevT};
  }

  /**
   * Convert a list of solution rule-lists <code>{{x-&gt;v,y-&gt;w},...}</code> into an {@link S#Or}
   * of {@link S#And} of equalities. For the {@link S#Primes} domain only solutions with prime
   * values are kept.
   */
  private static IExpr diophantineSolutionsToOr(IAST solutions, ISymbol domain) {
    if (solutions.isEmptyList()) {
      return S.False;
    }
    IASTAppendable orResult = F.OrAlloc(solutions.argSize());
    for (int i = 1; i < solutions.size(); i++) {
      IExpr sol = solutions.get(i);
      if (!sol.isList()) {
        return F.NIL;
      }
      IAST rules = (IAST) sol;
      IASTAppendable and = F.ast(S.And, rules.argSize());
      boolean ok = true;
      for (int j = 1; j < rules.size(); j++) {
        IExpr rule = rules.get(j);
        if (!rule.isRule()) {
          ok = false;
          break;
        }
        IExpr value = rule.second();
        if (domain == S.Primes //
            && !(value.isInteger() && value.isPositive()
                && ((IInteger) value).isProbablePrime())) {
          ok = false;
          break;
        }
        and.append(F.Equal(rule.first(), value));
      }
      if (ok && and.argSize() > 0) {
        orResult.append(and.isAST1() ? and.arg1() : and);
      }
    }
    if (orResult.isAST0()) {
      return S.False;
    }
    return orResult.isAST1() ? orResult.arg1() : orResult;
  }

  /**
   * Reduce a multivariate system of relations over {@link S#Reals}/{@link S#Complexes}. A
   * disjunction is reduced alternative by alternative, a conjunction which contains at least one
   * equation is handed to {@link #reduceEquationSystem(IAST, IAST, ISymbol, EvalEngine)}. Anything
   * else returns {@link F#NIL} so the caller can leave the expression unevaluated.
   */
  private static IExpr reduceMultivariate(IExpr arg1, IAST vars, ISymbol domain,
      SolveOptions options, EvalEngine engine) {
    if (arg1.isOr()) {
      IAST orAST = (IAST) arg1;
      IASTAppendable orResult = F.ast(S.Or, orAST.argSize());
      for (int i = 1; i < orAST.size(); i++) {
        IExpr alternative = orAST.get(i);
        IExpr reduced = reduceMultivariate(alternative, vars, domain, options, engine);
        if (reduced.isNIL()) {
          if (!isSolvedRelation(alternative, vars)) {
            return F.NIL;
          }
          // the alternative is its own reduced form
          reduced = alternative;
        }
        orResult.append(reduced);
      }
      return engine.evaluate(orResult);
    }

    IAST conditions;
    if (arg1.isList()) {
      conditions = (IAST) arg1;
    } else if (arg1.isAnd()) {
      conditions = ((IAST) arg1).setAtCopy(0, S.List);
    } else if (arg1.isAST2() && arg1.isComparatorFunction()) {
      conditions = F.list(arg1);
    } else {
      return F.NIL;
    }

    boolean hasEquation = false;
    for (int i = 1; i < conditions.size(); i++) {
      IExpr condition = conditions.get(i);
      if (condition.isEqual()) {
        hasEquation = true;
      } else if (!condition.isAST2() || !condition.isComparatorFunction()) {
        return F.NIL;
      }
    }
    if (!hasEquation) {
      // without an equation there is nothing to solve for, but a system whose relations each
      // constrain a single variable reduces variable by variable
      IExpr perVariable = reducePerVariable(conditions, vars, domain, engine);
      if (perVariable.isPresent()) {
        return perVariable;
      }
      // a system which already is in the solved form `variable OP value` is its own reduced form
      return isSolvedRelation(arg1, vars) ? arg1 : F.NIL;
    }
    return reduceEquationSystem(conditions, vars, domain, options, engine);
  }

  /**
   * Reduce a system whose relations each constrain a single variable by reducing the relations of
   * every variable on their own. A contradiction in one variable falsifies the whole system.
   *
   * @param conditions the relations of the system
   * @param vars the variables of the reduction
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return the conjunction of the reduced relations, or {@link F#NIL} if one relation constrains
   *         more than one variable
   */
  private static IExpr reducePerVariable(IAST conditions, IAST vars, ISymbol domain,
      EvalEngine engine) {
    Map<IExpr, IASTAppendable> relationsOfVariable = new LinkedHashMap<IExpr, IASTAppendable>();
    for (int i = 1; i < conditions.size(); i++) {
      IExpr condition = conditions.get(i);
      IAST variables = new VariablesSet(condition).getVarList();
      if (!variables.isAST1()) {
        return F.NIL;
      }
      IExpr variable = variables.arg1();
      IASTAppendable relations = relationsOfVariable.get(variable);
      if (relations == null) {
        relations = F.ast(S.And, 4);
        relationsOfVariable.put(variable, relations);
      }
      relations.append(condition);
    }
    IASTAppendable result = F.ast(S.And, relationsOfVariable.size() + 1);
    for (Map.Entry<IExpr, IASTAppendable> entry : relationsOfVariable.entrySet()) {
      IASTAppendable relations = entry.getValue();
      IExpr reduced = engine
          .evaluate(F.Reduce(relations.isAST1() ? relations.arg1() : relations, entry.getKey(),
              domain));
      if (!reduced.isFree(S.Reduce)) {
        return F.NIL;
      }
      if (reduced.isFalse()) {
        return S.False;
      }
      if (!reduced.isTrue()) {
        result.append(reduced);
      }
    }
    if (result.isAST0()) {
      return S.True;
    }
    return engine.evaluate(result);
  }

  /**
   * Solve a system of relations which contains at least one equation and convert the solution
   * branches into the disjunctive normal form which {@code Reduce} returns, e.g.
   * <code>{x^2==4, y==2}</code> becomes <code>(x==-2&amp;&amp;y==2)||(x==2&amp;&amp;y==2)</code>.
   *
   * @param conditions the equations and inequations of the system
   * @param vars the variables to solve for
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return {@link S#False} if the system has no solution, {@link F#NIL} if it cannot be solved
   */
  private static IExpr reduceEquationSystem(IAST conditions, IAST vars, ISymbol domain,
      SolveOptions options, EvalEngine engine) {
    IASTAppendable equations = F.ListAlloc(conditions.size());
    IASTAppendable constraints = F.ListAlloc(conditions.size());
    for (int i = 1; i < conditions.size(); i++) {
      IExpr condition = conditions.get(i);
      if (condition.isEqual()) {
        equations.append(condition);
      } else {
        constraints.append(condition);
      }
    }

    IAST branches = solveSystemRecursive(equations, constraints, vars, domain, options, engine);
    if (branches.isNIL()) {
      return F.NIL;
    }
    return branchesToOr(branches, vars, engine);
  }

  /**
   * Render the solution branches of a system as the disjunctive normal form which {@code Reduce}
   * returns: one conjunction of relations per branch, each ordered by the requested variables.
   *
   * @param branches the solution branches, each a {@link S#List} of relations
   * @param vars the variables of the reduction
   * @param engine the evaluation engine
   * @return {@link S#False} if there is no branch, i.e. the system is not satisfiable
   */
  private static IExpr branchesToOr(IAST branches, IAST vars, EvalEngine engine) {
    if (branches.isAST0()) {
      return S.False;
    }
    IASTAppendable orResult = F.ast(S.Or, branches.argSize());
    for (int i = 1; i < branches.size(); i++) {
      IAST branch = (IAST) branches.get(i);
      orResult.append(branch.isAST0() ? S.True : engine.evaluate(sortByVariables(branch, vars)));
    }
    return engine.evaluate(orResult);
  }

  /**
   * Solve a system of relations by eliminating one variable at a time. One equation is solved for
   * one of the variables and every one of its roots opens a solution branch in which the root is
   * substituted into the remaining relations. Because the choice of the (variable, equation) pair
   * decides whether the remaining system stays solvable, all pairs are tried until one of them
   * reduces the whole system.
   *
   * @param equations the {@link S#Equal} relations of the system
   * @param constraints the remaining relations (inequations) of the system
   * @param vars the variables which are not substituted yet
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return the list of solution branches - every branch is a {@link S#List} of relations - or
   *         {@link F#NIL} if the system cannot be solved
   */
  private static IAST solveSystemRecursive(IAST equations, IAST constraints, IAST vars,
      ISymbol domain, SolveOptions options, EvalEngine engine) {
    if (vars.isAST0() || equations.isAST0()) {
      // every variable is substituted, or the remaining variables are not determined by an equation
      // anymore; the remaining relations are either decided or they are conditions on the free
      // variables and parameters of the system
      IASTAppendable branch = F.ListAlloc(equations.argSize() + constraints.argSize());
      for (int i = 1; i < equations.size(); i++) {
        if (equations.get(i).isFalse()) {
          return F.CEmptyList;
        }
        if (!equations.get(i).isTrue()) {
          branch.append(equations.get(i));
        }
      }
      for (int i = 1; i < constraints.size(); i++) {
        if (constraints.get(i).isFalse()) {
          return F.CEmptyList;
        }
        if (!constraints.get(i).isTrue()) {
          branch.append(constraints.get(i));
        }
      }
      return F.list(branch);
    }

    // A (variable, equation) pair whose equation is linear in the variable is eliminated first: it
    // has a single root, so it neither multiplies the solution branches nor pushes a radical into
    // the remaining relations - a radical which the back substitution of the other variables would
    // collapse, mapping different branches onto the same one.
    for (int pass = 1; pass <= 3; pass++) {
      for (int v = 1; v < vars.size(); v++) {
        IExpr variable = vars.get(v);
        for (int e = 1; e < equations.size(); e++) {
          IExpr equation = equations.get(e);
          if (equation.isFree(variable, true)) {
            continue;
          }
          if (pass < 3 && !isLinearEquation(equation, variable, engine)) {
            continue;
          }
          if (pass == 1 && !isParameterFree(equation, vars)) {
            // an equation without parameters determines the variable outright, so eliminating it
            // first keeps the parameters for the equations which really constrain them
            continue;
          }
          IAST roots = variableBranches(equation, variable, domain, options, engine);
          if (roots.isNIL()) {
            continue;
          }
          IAST solved = substituteBranches(roots, variable, equations.removeAtCopy(e), constraints,
              vars.removeAtCopy(v), domain, options, engine);
          if (solved.isPresent()) {
            return solved;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Test if every symbol of the relation is one of the variables of the system, so that it contains
   * no parameters.
   *
   * @param relation the relation to test
   * @param vars the variables of the system
   */
  private static boolean isParameterFree(IExpr relation, IAST vars) {
    IAST variables = new VariablesSet(relation).getVarList();
    for (int i = 1; i < variables.size(); i++) {
      if (!vars.contains(variables.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test if the equation is linear in the given variable, so that it determines the variable
   * uniquely.
   *
   * @param equation an {@link S#Equal} relation
   * @param variable the variable to solve for
   * @param engine the evaluation engine
   */
  private static boolean isLinearEquation(IExpr equation, IExpr variable, EvalEngine engine) {
    if (!equation.isAST2()) {
      return false;
    }
    IExpr difference = engine.evaluate(F.Subtract(equation.first(), equation.second()));
    return difference.isPolynomial(variable) && difference.linear(variable) != null;
  }

  /**
   * Substitute every root of one variable into the remaining relations and solve the remaining
   * system in each of the resulting branches.
   *
   * @param roots the branches of the eliminated variable as returned by
   *        {@link #variableBranches(IExpr, IExpr, ISymbol, EvalEngine)}
   * @param variable the eliminated variable
   * @param equations the equations which are not used for the elimination
   * @param constraints the remaining relations (inequations) of the system
   * @param vars the variables which are not substituted yet
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return the list of solution branches or {@link F#NIL} if one of the branches leaves a system
   *         which cannot be solved
   */
  private static IAST substituteBranches(IAST roots, IExpr variable, IAST equations,
      IAST constraints, IAST vars, ISymbol domain, SolveOptions options, EvalEngine engine) {
    IASTAppendable result = F.ListAlloc(roots.argSize());
    // the dependent form of every emitted branch, in the same order
    List<IExpr> dependentValues = new ArrayList<IExpr>();
    boolean blowsUp = false;
    for (int i = 1; i < roots.size(); i++) {
      IAST root = (IAST) roots.get(i);
      IExpr value = root.arg1();
      if (domain == S.Reals && isComplexNonReal(value)) {
        // a non-real root is no solution over the reals
        continue;
      }
      IAST substRule = F.list(F.Rule(variable, value));
      IASTAppendable substEquations = F.ListAlloc(equations.size());
      IASTAppendable substConstraints = F.ListAlloc(constraints.size());
      if (!substituteRelations(equations, substRule, substEquations, substConstraints, engine)
          || !substituteRelations(constraints, substRule, substEquations, substConstraints,
              engine)) {
        // the root contradicts one of the remaining relations
        continue;
      }

      IAST solved =
          solveSystemRecursive(substEquations, substConstraints, vars, domain, options, engine);
      if (solved.isNIL()) {
        return F.NIL;
      }
      for (int j = 1; j < solved.size(); j++) {
        IAST subBranch = (IAST) solved.get(j);
        // the values of the remaining variables are back substituted into this root
        IAST backSubstitution = equalityRules(subBranch);
        IExpr rootValue = value;
        if (!backSubstitution.isAST0()) {
          IExpr substituted = engine.evaluate(F.subst(value, backSubstitution));
          // the substitution stacks the fractions of the eliminated variables
          substituted = S.Together.of(engine, substituted);
          blowsUp = blowsUp || duplicatesInertRoot(value, substituted);
          rootValue = substituted;
        }
        IASTAppendable branch = F.ListAlloc(subBranch.argSize() + root.argSize() + 1);
        branch.append(F.Equal(variable, rootValue));
        branch.appendArgs(subBranch);
        // the side conditions of the root, e.g. `C(1) ∈ Integers`
        for (int k = 2; k < root.size(); k++) {
          branch.append(root.get(k));
        }
        result.append(branch);
        dependentValues.add(value);
      }
    }

    if (blowsUp && !options.isBacksubstitution()) {
      // one decision for the whole elimination: giving the variable explicitly in one branch of a
      // result and through another variable in the next one would be inconsistent
      for (int i = 1; i < result.size(); i++) {
        ((IASTMutable) result.get(i)).set(1, F.Equal(variable, dependentValues.get(i - 1)));
      }
    }
    return result;
  }

  /**
   * Test whether substituting the values of the remaining variables into <code>value</code> pulls
   * an inert {@link S#Root} object into it, so that the {@link S#Backsubstitution} default keeps
   * the dependent form instead.
   *
   * <p>
   * Substituting the value of another variable usually shortens the result - <code>1-y</code> with
   * <code>y==-1</code> becomes <code>2</code> - and then the explicit form is the better answer. A
   * {@link S#Root} object is the case where it doesn't: it has no closed form to collapse into, so
   * substituting only duplicates it into every variable which refers to it, and
   * <code>x==1+y</code> is easier to read than <code>x==1+Root(-1-#1+#1^5&amp;,1,0)</code>.
   *
   * @param value the value of the variable, still referring to the other variables
   * @param substituted the same value after the other variables were substituted
   */
  private static boolean duplicatesInertRoot(IExpr value, IExpr substituted) {
    return !substituted.isFree(S.Root) && value.isFree(S.Root);
  }

  /**
   * Substitute <code>substRule</code> into every relation of <code>relations</code> and sort the
   * evaluated results into <code>equations</code> and <code>constraints</code>. Relations which
   * evaluate to {@link S#True} are dropped.
   *
   * @return <code>false</code> if one of the relations evaluates to {@link S#False}
   */
  private static boolean substituteRelations(IAST relations, IAST substRule,
      IASTAppendable equations, IASTAppendable constraints, EvalEngine engine) {
    for (int i = 1; i < relations.size(); i++) {
      IExpr relation = engine.evaluate(F.subst(relations.get(i), substRule));
      if (relation.isFalse()) {
        return false;
      }
      if (relation.isTrue()) {
        continue;
      }
      if (relation.isEqual()) {
        equations.append(relation);
      } else {
        constraints.append(relation);
      }
    }
    return true;
  }

  /**
   * Determine all values of <code>variable</code> which solve the given equation. {@link S#Roots}
   * covers the polynomial equations; everything else is delegated to the univariate reduction of
   * {@code Reduce} itself, which also solves periodic equations like <code>Sin(x)==0</code>.
   *
   * @param equation the equation to solve
   * @param variable the variable to solve for
   * @param domain {@link S#Reals} or {@link S#Complexes}
   * @param engine the evaluation engine
   * @return a list of <code>{value, sideCondition...}</code> entries or {@link F#NIL} if the
   *         equation cannot be solved for <code>variable</code>
   */
  private static IAST variableBranches(IExpr equation, IExpr variable, ISymbol domain,
      SolveOptions options, EvalEngine engine) {
    IExpr roots = rootsOf(equation, variable, options, engine);
    if (roots.isPresent() && roots.isFree(S.Roots)) {
      IAST branches = branchesFromSolvedForm(roots, variable, F.CEmptyList);
      if (branches.isPresent()) {
        return branches;
      }
    }

    // `Roots` only handles polynomial equations - the univariate reduction of `Reduce` also solves
    // e.g. periodic equations. A single variable never reaches the multivariate reduction again.
    IExpr reduced = engine.evalQuiet(F.Reduce(equation, variable, domain));
    if (reduced.isFree(S.Reduce)) {
      IExpr solvedForm = reduced;
      IAST sideConditions = F.CEmptyList;
      if (reduced.isAnd()) {
        // split `C(1) ∈ Integers && (x==... || x==...)` into its side conditions and the roots
        IAST and = (IAST) reduced;
        IASTAppendable conditions = F.ListAlloc(and.size());
        IASTAppendable rest = F.ListAlloc(and.size());
        for (int i = 1; i < and.size(); i++) {
          if (and.get(i).isFree(variable, true)) {
            conditions.append(and.get(i));
          } else {
            rest.append(and.get(i));
          }
        }
        if (!rest.isAST1()) {
          return F.NIL;
        }
        solvedForm = rest.arg1();
        sideConditions = conditions;
      }
      IAST branches = branchesFromSolvedForm(solvedForm, variable, sideConditions);
      if (branches.isPresent()) {
        return branches;
      }
    }
    return F.NIL;
  }

  /**
   * Convert a solved form <code>variable==value</code> or
   * <code>variable==value1||variable==value2||...</code> into a list of
   * <code>{value, sideCondition...}</code> entries.
   *
   * @return {@link F#NIL} if the expression isn't a disjunction of <code>variable==value</code>
   *         equations
   */
  private static IAST branchesFromSolvedForm(IExpr solvedForm, IExpr variable,
      IAST sideConditions) {
    IAST disjuncts = solvedForm.isOr() ? (IAST) solvedForm : F.list(solvedForm);
    IASTAppendable branches = F.ListAlloc(disjuncts.argSize());
    for (int i = 1; i < disjuncts.size(); i++) {
      IExpr disjunct = disjuncts.get(i);
      if (!disjunct.isEqual() || !disjunct.first().equals(variable)
          || !disjunct.second().isFree(variable, true)) {
        return F.NIL;
      }
      IASTAppendable branch = F.ListAlloc(sideConditions.argSize() + 1);
      branch.append(disjunct.second());
      branch.appendArgs(sideConditions);
      branches.append(branch);
    }
    return branches;
  }

  /**
   * Collect the <code>variable == value</code> relations of a solution branch as
   * <code>variable -&gt; value</code> rules, so that they can be substituted back into the value of
   * an earlier eliminated variable.
   */
  private static IAST equalityRules(IAST branch) {
    IASTAppendable rules = F.ListAlloc(branch.size());
    for (int i = 1; i < branch.size(); i++) {
      IExpr relation = branch.get(i);
      if (relation.isEqual() && relation.first().isSymbol()) {
        rules.append(F.Rule(relation.first(), relation.second()));
      }
    }
    return rules;
  }

  /**
   * Convert a solution branch into an {@link S#And} expression. Domain conditions like
   * <code>C(1) ∈ Integers</code> are listed first, the relations of the requested variables follow
   * in the order of the variables, conditions on the parameters of the system come last.
   *
   * @param branch the relations of one solution branch
   * @param vars the variables of the reduction
   */
  private static IAST sortByVariables(IAST branch, IAST vars) {
    IASTAppendable andResult = F.ast(S.And, branch.argSize());
    boolean[] appended = new boolean[branch.size()];
    for (int i = 1; i < branch.size(); i++) {
      IExpr relation = branch.get(i);
      if (!relation.isComparatorFunction()) {
        appended[i] = true;
        andResult.append(relation);
      }
    }
    for (int v = 1; v < vars.size(); v++) {
      for (int i = 1; i < branch.size(); i++) {
        if (!appended[i] && branch.get(i).first().equals(vars.get(v))) {
          appended[i] = true;
          andResult.append(branch.get(i));
        }
      }
    }
    for (int i = 1; i < branch.size(); i++) {
      if (!appended[i]) {
        andResult.append(branch.get(i));
      }
    }
    return andResult;
  }

  /**
   * Test if the given expression is already in the reduced form <code>variable OP value</code> (or
   * a boolean {@link S#And}/{@link S#Or} combination of such relations), where
   * <code>variable</code> is one of <code>vars</code> and <code>value</code> is free of all
   * <code>vars</code>.
   *
   * @param expr the expression to test
   * @param vars the variables of the reduction
   */
  private static boolean isSolvedRelation(IExpr expr, IAST vars) {
    if (expr.isAnd() || expr.isOr()) {
      IAST booleanAST = (IAST) expr;
      for (int i = 1; i < booleanAST.size(); i++) {
        if (!isSolvedRelation(booleanAST.get(i), vars)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isAST2() && expr.isComparatorFunction()) {
      return vars.contains(expr.first()) && expr.second().isFree(x -> vars.contains(x), true);
    }
    return false;
  }

  /**
   * Try to globally decide a single univariate inequality <code>lhs OP rhs</code> in
   * <code>variable</code> over the reals using the symbolic optimizers
   * {@link S#Minimize}/{@link S#Maximize}. For example <code>x^2 + 1 &gt; 0</code> holds for every
   * real <code>x</code> (because <code>Minimize(x^2+1) == 1 &gt; 0</code>), so the solution set is
   * <code>Element(x, Reals)</code>.
   *
   * <p>
   * To avoid deep mutual recursion (the optimizers internally call {@code Solve}, which can call
   * {@code Reduce} again) the delegation only happens at optimizer reentrancy depth <code>0</code>,
   * guarded by {@link EvalEngine#incOptimizeExpressionDepth()}.
   *
   * @param arg1 the first argument of {@code Reduce}
   * @param variable the (single) variable
   * @param engine the evaluation engine
   * @return an <code>Element(variable, Reals)</code> AST if the inequality holds for every real
   *         value, {@link S#False} if it holds for no value, {@link F#NIL} otherwise
   */
  private static IExpr decideInequalityByExtrema(IExpr arg1, IExpr variable, EvalEngine engine) {
    if (engine.getOptimizeExpressionDepth() != 0) {
      return F.NIL;
    }
    int headID = arg1.headID();
    if (headID != ID.Less && headID != ID.LessEqual && headID != ID.Greater
        && headID != ID.GreaterEqual) {
      return F.NIL;
    }
    IAST comparator = (IAST) arg1;
    if (comparator.argSize() != 2) {
      return F.NIL;
    }
    IExpr f = engine.evaluate(F.Subtract(comparator.arg1(), comparator.arg2()));
    if (f.isFree(variable)) {
      return F.NIL;
    }
    if (!f.isPolynomial(variable)) {
      // the optimizers only handle polynomials; the range of the function decides the rest
      return decideInequalityByRange(headID, f, variable, engine);
    }

    final boolean quietMode = engine.isQuietMode();
    engine.setQuietMode(true);
    engine.incOptimizeExpressionDepth();
    try {
      IExpr minValue = extremumValue(S.Minimize.of(engine, f, variable));
      IExpr maxValue = extremumValue(S.Maximize.of(engine, f, variable));
      switch (headID) {
        case ID.Greater: // f > 0
          if (minValue.isPresent() && minValue.isPositiveResult()) {
            return F.Element(variable, S.Reals);
          }
          if (maxValue.isPresent() && maxValue.isNegativeResult()) {
            return S.False;
          }
          break;
        case ID.GreaterEqual: // f >= 0
          if (minValue.isPresent() && !minValue.isNegativeResult()) {
            return F.Element(variable, S.Reals);
          }
          if (maxValue.isPresent() && maxValue.isNegativeResult()) {
            return S.False;
          }
          break;
        case ID.Less: // f < 0
          if (maxValue.isPresent() && maxValue.isNegativeResult()) {
            return F.Element(variable, S.Reals);
          }
          if (minValue.isPresent() && minValue.isPositiveResult()) {
            return S.False;
          }
          break;
        case ID.LessEqual: // f <= 0
          if (maxValue.isPresent() && !maxValue.isPositiveResult()) {
            return F.Element(variable, S.Reals);
          }
          if (minValue.isPresent() && minValue.isPositiveResult()) {
            return S.False;
          }
          break;
        default:
          break;
      }
    } finally {
      engine.decOptimizeExpressionDepth();
      engine.setQuietMode(quietMode);
    }
    return F.NIL;
  }

  /**
   * Try to decide a univariate inequality <code>f REL 0</code> whose left-hand side isn't a
   * polynomial from the range of values which <code>f</code> attains, e.g.
   * <code>Sin(x)&lt;=1</code> holds for every real <code>x</code> because the range of
   * <code>Sin</code> is <code>[-1,1]</code>.
   *
   * <p>
   * The refutation is sound for every function: if no attained value fulfills the relation, no
   * argument does. The universal direction additionally needs a function which is defined for every
   * real argument - <code>1/x^2&gt;0</code> holds for every value the function attains, but not at
   * <code>x==0</code>.
   *
   * @param headID the {@link ID} of the relation
   * @param f the left-hand side of <code>f REL 0</code>
   * @param variable the (single) variable
   * @param engine the evaluation engine
   * @return {@code Element(variable, Reals)}, {@link S#False}, or {@link F#NIL} if the range
   *         doesn't decide the relation
   */
  private static IExpr decideInequalityByRange(int headID, IExpr f, IExpr variable,
      EvalEngine engine) {
    if (new VariablesSet(f).size() != 1) {
      return F.NIL;
    }
    ISymbol rangeVariable = F.Dummy("range");
    IExpr range = engine.evalQuiet(F.FunctionRange(f, variable, rangeVariable));
    if (range.isNIL() || !range.isFree(S.FunctionRange, true)) {
      return F.NIL;
    }
    IAST rangeIntervals = solvedFormToIntervals(range, rangeVariable);
    if (rangeIntervals.isNIL() || rangeIntervals.isAST0()) {
      return F.NIL;
    }
    IAST relationIntervals = IntervalDataSym.relationToIntervalSet(headID, F.C0);
    if (relationIntervals.isNIL()) {
      return F.NIL;
    }
    IAST intersection = IntervalDataSym.intersection(rangeIntervals, relationIntervals, engine);
    if (intersection.isNIL()) {
      return F.NIL;
    }
    if (intersection.isAST0()) {
      // no attained value fulfills the relation
      return S.False;
    }
    if (intersection.equals(rangeIntervals) && isTotalRealFunction(f, variable)) {
      // every attained value fulfills the relation, and every real argument is attained
      return F.Element(variable, S.Reals);
    }
    return F.NIL;
  }

  /**
   * Test whether the expression is built only from functions which are defined and real valued for
   * every real value of the variable, so that its range describes its values on all of
   * <code>Reals</code>.
   *
   * @param expr the expression to test
   * @param variable the variable of the reduction
   */
  private static boolean isTotalRealFunction(IExpr expr, IExpr variable) {
    if (expr.equals(variable)) {
      return true;
    }
    if (expr.isFree(variable, true)) {
      return expr.isRealResult();
    }
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (ast.isPower()) {
      IExpr base = ast.base();
      IExpr exponent = ast.exponent();
      if (exponent.isFree(variable, true)) {
        // `u^n` is defined everywhere for a non negative integer `n`
        return exponent.isInteger() && !exponent.isNegativeResult()
            && isTotalRealFunction(base, variable);
      }
      if (base.isFree(variable, true)) {
        // `b^u` is defined everywhere for a positive base `b`
        return base.isPositiveResult() && isTotalRealFunction(exponent, variable);
      }
      return false;
    }
    switch (ast.headID()) {
      case ID.Plus:
      case ID.Times:
      case ID.Abs:
      case ID.ArcSinh:
      case ID.ArcTan:
      case ID.Cos:
      case ID.Cosh:
      case ID.Erf:
      case ID.Exp:
      case ID.Max:
      case ID.Min:
      case ID.Sign:
      case ID.Sin:
      case ID.Sinh:
      case ID.Tanh:
      case ID.UnitStep:
        break;
      default:
        return false;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (!isTotalRealFunction(ast.get(i), variable)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Reduce a single univariate relation <code>lhs OP rhs</code> over the {@link S#Reals} into the
   * set of real values which fulfill it, represented as an {@link S#IntervalData} set.
   *
   * <p>
   * The sign of a real rational function <code>f = (lhs-rhs)</code> can only change at its real
   * roots and at its poles, so the real line is split there and the sign of <code>f</code> is
   * sampled in each open region. The regions which fulfill the relation are merged into a maximal
   * interval set; a root is part of the solution for the non-strict relations and for
   * {@link S#Equal}, a pole never is (<code>f</code> isn't even defined there).
   *
   * @param relation a binary relation ({@link S#Equal}, {@link S#Unequal}, {@link S#Less},
   *        {@link S#LessEqual}, {@link S#Greater}, {@link S#GreaterEqual})
   * @param variable the (single) variable
   * @param options the options of this {@code Reduce} call
   * @param engine the evaluation engine
   * @return the {@link S#IntervalData} set of the solutions (possibly empty), or {@link F#NIL} if
   *         the relation isn't a rational one in <code>variable</code> or its roots couldn't be
   *         determined numerically
   */
  private static IAST realAtomToIntervals(IExpr relation, IExpr variable, SolveOptions options,
      EvalEngine engine) {
    final int headID = relation.headID();
    switch (headID) {
      case ID.Equal:
      case ID.Unequal:
      case ID.Less:
      case ID.LessEqual:
      case ID.Greater:
      case ID.GreaterEqual:
        break;
      default:
        return F.NIL;
    }
    IAST comparator = (IAST) relation;
    if (comparator.argSize() != 2) {
      return F.NIL;
    }

    // rewrite `lhs OP rhs` as `f OP 0` with f = lhs - rhs
    IExpr f = engine.evaluate(F.ExpandAll(F.Subtract(comparator.arg1(), comparator.arg2())));
    if (f.isFree(variable)) {
      IExpr decided = engine.evaluate(relation);
      if (decided.isTrue()) {
        return IntervalDataSym.reals();
      }
      if (decided.isFalse()) {
        return F.IntervalData();
      }
      return F.NIL;
    }
    // the sign analysis samples the function numerically, so it must not contain parameters
    if (new VariablesSet(f).size() != 1) {
      return F.NIL;
    }

    IExpr numerator = f;
    IExpr denominator = F.C1;
    if (!f.isPolynomial(variable)) {
      if (!f.isAST()) {
        return F.NIL;
      }
      IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) f, true, engine);
      numerator = parts[0];
      denominator = parts[1];
      if (!numerator.isPolynomial(variable) || !denominator.isPolynomial(variable)) {
        return F.NIL;
      }
    }

    // the breakpoints of the sign: the real roots of the numerator and the poles
    List<IExpr> breakExacts = new ArrayList<IExpr>();
    List<Double> breakValues = new ArrayList<Double>();
    List<Boolean> breakIsPole = new ArrayList<Boolean>();
    if (!collectRealRoots(numerator, variable, false, options, breakExacts, breakValues,
        breakIsPole, engine)) {
      return F.NIL;
    }
    if (!denominator.isFree(variable) //
        && !collectRealRoots(denominator, variable, true, options, breakExacts, breakValues,
            breakIsPole, engine)) {
      return F.NIL;
    }

    final boolean rootInSolution = headID == ID.LessEqual || headID == ID.GreaterEqual //
        || headID == ID.Equal;
    final int n = breakExacts.size();
    // sign of f on each of the n+1 open regions between/around the breakpoints
    boolean[] regionInSolution = new boolean[n + 1];
    for (int k = 0; k <= n; k++) {
      final double sample;
      if (n == 0) {
        sample = 0.0;
      } else if (k == 0) {
        sample = breakValues.get(0) - 1.0;
      } else if (k == n) {
        sample = breakValues.get(n - 1) + 1.0;
      } else {
        sample = (breakValues.get(k - 1) + breakValues.get(k)) / 2.0;
      }
      int sign = polynomialSignAt(f, variable, sample, engine);
      if (sign == 0) {
        return F.NIL;
      }
      regionInSolution[k] = relationHolds(headID, sign);
    }
    boolean[] pointInSolution = new boolean[n];
    for (int k = 0; k < n; k++) {
      // f isn't defined at a pole, so no relation holds there
      pointInSolution[k] = rootInSolution && !breakIsPole.get(k).booleanValue();
    }
    return mergeRegions(breakExacts, regionInSolution, pointInSolution);
  }

  /**
   * Test whether a value with the given sign fulfills the relation <code>f OP 0</code>.
   *
   * @param headID the {@link ID} of the relation
   * @param sign the sign of <code>f</code>
   */
  private static boolean relationHolds(int headID, int sign) {
    switch (headID) {
      case ID.Equal:
        return false;
      case ID.Unequal:
        return true;
      case ID.Less:
      case ID.LessEqual:
        return sign < 0;
      default:
        return sign > 0;
    }
  }

  /**
   * Collect the distinct real roots of the polynomial into the parallel lists of exact values,
   * numeric values and pole flags, keeping them sorted ascending.
   *
   * @param polynomial the polynomial whose real roots are the breakpoints
   * @param variable the variable of the reduction
   * @param isPole <code>true</code> if the roots are poles of the reduced function
   * @return <code>false</code> if the real roots couldn't be determined
   */
  private static boolean collectRealRoots(IExpr polynomial, IExpr variable, boolean isPole,
      SolveOptions options, List<IExpr> exacts, List<Double> values, List<Boolean> poles,
      EvalEngine engine) {
    if (polynomial.isFree(variable)) {
      return true;
    }
    IExpr roots = rootsOf(F.Equal(polynomial, F.C0), variable, options, engine);
    if (roots.isNIL() || !roots.isFree(S.Roots)) {
      return false;
    }
    if (roots.isFalse()) {
      // the polynomial has no root at all
      return true;
    }
    IAST branches = branchesFromSolvedForm(roots, variable, F.CEmptyList);
    if (branches.isNIL()) {
      return false;
    }
    for (int i = 1; i < branches.size(); i++) {
      IExpr rootValue = engine.evaluate(((IAST) branches.get(i)).arg1());
      if (isComplexNonReal(rootValue)) {
        // `Roots` returns the complex roots too; they don't split the real line
        continue;
      }
      double d;
      try {
        d = rootValue.evalDouble();
      } catch (ArgumentTypeException aex) {
        return false;
      }
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        return false;
      }
      insertSortedDistinct(exacts, values, poles, rootValue, d, isPole);
    }
    return true;
  }

  /**
   * Merge the regions and breakpoints which fulfill a relation into a maximal
   * {@link S#IntervalData} set. The atoms are traversed left to right: even index <code>t</code> is
   * the region <code>t/2</code>, odd index <code>t</code> is the breakpoint <code>(t-1)/2</code>.
   *
   * @param breakPoints the breakpoints, sorted ascending
   * @param regionInSolution which of the <code>breakPoints.size()+1</code> open regions fulfill the
   *        relation
   * @param pointInSolution which of the breakpoints fulfill the relation
   */
  private static IAST mergeRegions(List<IExpr> breakPoints, boolean[] regionInSolution,
      boolean[] pointInSolution) {
    final int n = breakPoints.size();
    IASTAppendable intervalData = F.IntervalDataAlloc(n + 2);
    boolean open = false;
    IExpr min = F.NIL;
    IExpr minType = S.Less;
    IExpr max = F.NIL;
    IExpr maxType = S.Less;
    for (int t = 0; t <= 2 * n; t++) {
      final boolean isRegion = (t & 1) == 0;
      final int index = t / 2;
      final boolean inSolution = isRegion ? regionInSolution[index] : pointInSolution[index];
      final IExpr leftValue;
      final IExpr rightValue;
      final IBuiltInSymbol edgeType;
      if (isRegion) {
        leftValue = index == 0 ? F.CNInfinity : breakPoints.get(index - 1);
        rightValue = index == n ? F.CInfinity : breakPoints.get(index);
        edgeType = S.Less;
      } else {
        leftValue = breakPoints.get(index);
        rightValue = leftValue;
        edgeType = S.LessEqual;
      }
      if (inSolution) {
        if (!open) {
          open = true;
          min = leftValue;
          minType = edgeType;
        }
        max = rightValue;
        maxType = edgeType;
      } else if (open) {
        intervalData.append(F.List(min, minType, maxType, max));
        open = false;
      }
    }
    if (open) {
      intervalData.append(F.List(min, minType, maxType, max));
    }
    return intervalData;
  }

  /**
   * Render an {@link S#IntervalData} set of the solutions of a univariate reduction as the
   * corresponding logical expression.
   *
   * @param intervalData the solution set
   * @param variable the variable of the reduction
   * @param engine the evaluation engine
   * @return {@link S#False} for the empty set, {@code Element(variable, Reals)} for the whole real
   *         line, otherwise the {@link S#Or} of the comparators of the intervals
   */
  private static IExpr intervalsToExpr(IAST intervalData, IExpr variable, EvalEngine engine) {
    if (intervalData.isAST0()) {
      return S.False;
    }
    if (intervalData.isAST1()) {
      IAST only = (IAST) intervalData.arg1();
      if (only.arg1().isNegativeInfinity() && only.arg4().isInfinity()) {
        return F.Element(variable, S.Reals);
      }
    }
    return engine.evaluate(IntervalDataSym.intervalToOr(intervalData, variable));
  }

  /**
   * Reduce a single univariate inequality <code>lhs OP rhs</code> (with <code>OP</code> one of
   * {@link S#Less}, {@link S#LessEqual}, {@link S#Greater}, {@link S#GreaterEqual}) over the
   * {@link S#Reals} by the sign analysis of {@link #realAtomToIntervals(IExpr, IExpr, SolveOptions,
   * EvalEngine)}. For example <code>4*x^3-4*x&gt;0</code> reduces to
   * <code>(-1&lt;x&lt;0)||x&gt;1</code>.
   *
   * <p>
   * Inequalities are inherently real-valued, so this reduction is applied independent of the
   * requested domain ({@link S#Reals} as well as the default {@link S#Complexes}).
   *
   * @param arg1 the first argument of {@code Reduce}
   * @param variable the (single) variable
   * @param engine the evaluation engine
   * @return the reduced solution set, {@link S#False} if it is empty,
   *         {@code Element(variable, Reals)} if it is the whole real line, or {@link F#NIL} if the
   *         relation couldn't be reduced
   */
  private static IExpr reducePolynomialInequalityReals(IExpr arg1, IExpr variable,
      SolveOptions options, EvalEngine engine) {
    int headID = arg1.headID();
    if (headID != ID.Less && headID != ID.LessEqual && headID != ID.Greater
        && headID != ID.GreaterEqual) {
      return F.NIL;
    }
    IAST intervalData = realAtomToIntervals(arg1, variable, options, engine);
    if (intervalData.isNIL()) {
      return F.NIL;
    }
    return intervalsToExpr(intervalData, variable, engine);
  }

  /**
   * Insert <code>value</code>/<code>numericValue</code> into the parallel lists
   * <code>exacts</code>/<code>values</code> keeping them sorted ascending by numeric value and
   * skipping numeric duplicates.
   */
  private static void insertSortedDistinct(List<IExpr> exacts, List<Double> values, IExpr value,
      double numericValue) {
    insertSortedDistinct(exacts, values, null, value, numericValue, false);
  }

  /**
   * Insert <code>value</code>/<code>numericValue</code> into the parallel lists
   * <code>exacts</code>/<code>values</code>/<code>poles</code> keeping them sorted ascending by
   * numeric value and skipping numeric duplicates. A breakpoint which is a pole stays a pole even
   * if it is a root of the numerator too - the function isn't defined there.
   */
  private static void insertSortedDistinct(List<IExpr> exacts, List<Double> values,
      List<Boolean> poles, IExpr value, double numericValue, boolean isPole) {
    int pos = 0;
    while (pos < values.size() && values.get(pos).doubleValue() < numericValue) {
      pos++;
    }
    if (pos < values.size() && values.get(pos).doubleValue() == numericValue) {
      // duplicate real root - keep the first representation
      if (poles != null && isPole) {
        poles.set(pos, Boolean.TRUE);
      }
      return;
    }
    exacts.add(pos, value);
    values.add(pos, Double.valueOf(numericValue));
    if (poles != null) {
      poles.add(pos, Boolean.valueOf(isPole));
    }
  }

  /**
   * Numerically evaluate the sign of the polynomial <code>f</code> at
   * <code>variable == sample</code> .
   *
   * @return <code>1</code> / <code>-1</code> for a positive / negative value, <code>0</code> if the
   *         value is zero or cannot be evaluated to a real number
   */
  private static int polynomialSignAt(IExpr f, IExpr variable, double sample, EvalEngine engine) {
    IExpr value = engine.evaluate(F.subst(f, variable, F.num(sample)));
    double d;
    try {
      d = value.evalDouble();
    } catch (ArgumentTypeException aex) {
      return 0;
    }
    if (d > 0.0) {
      return 1;
    }
    if (d < 0.0) {
      return -1;
    }
    return 0;
  }

  /**
   * Expands chained comparator functions (e.g., Less(a, b, c)) and Inequality ASTs into binary And
   * expressions (e.g., And(Less(a, b), Less(b, c))) so they can be processed as AST2 elements by
   * ReduceComparison.
   */
  private static IExpr expandComparators(IExpr expr) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;

      // Handle mixed inequalities like Inequality(0, Less, x, LessEqual, 2). `Inequality` is a
      // comparator function too, so it has to be recognized before the chained comparators - its
      // arguments alternate between values and relation heads.
      if (ast.isAST(S.Inequality)) {
        IASTAppendable andAST = F.AndAlloc(ast.argSize() / 2);
        for (int i = 1; i < ast.size() - 2; i += 2) {
          andAST.append(F.binaryAST2(ast.get(i + 1), ast.get(i), ast.get(i + 2)));
        }
        return andAST;
      }
      // Handle chained comparators like Less(0, x, 2)
      else if (ast.isComparatorFunction() && ast.argSize() > 2) {
        IASTAppendable andAST = F.AndAlloc(ast.argSize());
        for (int i = 1; i < ast.size() - 1; i++) {
          andAST.append(F.binaryAST2(ast.head(), ast.get(i), ast.get(i + 1)));
        }
        return andAST;
      }
      // Recursively process logical wrappers
      else if (ast.isFunctionID(ID.And, ID.Or, ID.Not, ID.List)) {
        IASTAppendable result = F.ast(ast.head(), ast.argSize());
        for (int i = 1; i < ast.size(); i++) {
          result.append(expandComparators(ast.get(i)));
        }
        return result;
      }
    }
    return expr;
  }

  /**
   * Extract the (finite) extremum value from a {@code Minimize}/{@code Maximize} result
   * <code>{value, {var -> p}}</code>. Returns {@link F#NIL} if the value is infinite /
   * indeterminate or the result isn't a determined extremum.
   *
   * @param result the optimizer result
   * @return the extremum value or {@link F#NIL}
   */
  private static IExpr extremumValue(IExpr result) {
    if (!result.isList2()) {
      return F.NIL;
    }
    IExpr value = ((IAST) result).first();
    if (!value.isFree(S.Minimize) || !value.isFree(S.Maximize)) {
      return F.NIL;
    }
    if (value.isIndeterminate() || value.isInfinity() || value.isNegativeInfinity()
        || value.isDirectedInfinity() || value.isAST(S.Piecewise)) {
      return F.NIL;
    }
    return value;
  }

  /**
   * Setting the domains of variables appearing in inequalities with head
   * {@link S#Less},{@link S#LessEqual},{@link S#Greater},{@link S#GreaterEqual} to {@link S#Reals}
   * if the variable is currently set to {@link S#Complexes}. The logical expressions with head
   * {@link S#And}, {@link S#Or}, {@link S#Not} call this method recursively for their arguments.
   * * @param expr
   * 
   * @param domainMap
   */
  private static void setInequalityDomainsRecursive(IExpr expr, Map<IExpr, IExpr> domainMap) {
    if (expr.isFunctionID(ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual)) {
      VariablesSet vs = new VariablesSet(expr);
      Set<IExpr> set = vs.toSet();
      for (IExpr variable : set) {
        IExpr domain = domainMap.get(variable);
        if (domain == S.Complexes) {
          domainMap.put(variable, S.Reals);
        }
      }
      return;
    }
    if (expr.isFunctionID(ID.And, ID.Not, ID.Or)) {
      ((IAST) expr).forEach(x -> setInequalityDomainsRecursive(x, domainMap));
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_3;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.REDUCE_KEYS, SolveOptions.REDUCE_DEFAULTS);
  }

  /** {@inheritDoc} */

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

}
