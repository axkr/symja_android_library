package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.Attribute;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DifferenceRootExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.reflection.system.rulesets.SumRules;
import com.google.common.base.Suppliers;

public class Sum extends ListFunctions.Table implements SumRules {

  /**
   * Closed-form evaluation of infinite sums of a hypergeometric term
   * <code>Sum(term, {var, from, Infinity})</code>.
   *
   * <p>
   * The summand is recognized as hypergeometric through its term ratio <code>t(var+1)/t(var)</code>
   * (see {@link Sum#hypergeometricRatio}). After shifting the summation index to start at
   * <code>0</code> the ratio is factored into
   * <code>z * Product(var+a_i) / ((var+1) * Product(var+b_j))</code> to read off the upper
   * parameters <code>a_i</code>, the lower parameters <code>b_j</code> and the argument
   * <code>z</code> of a generalized hypergeometric function. The result is
   * <code>t(from) * pFq(a; b; z)</code>, which is handed to the
   * {@code Hypergeometric0F1/1F1/2F1/PFQ} evaluators for a closed form. A result is only returned
   * if it is free of unevaluated hypergeometric/sum heads and passes a numeric check.
   */
  private final static class SumHypergeometric {

    private SumHypergeometric() {}

    /**
     * Evaluate <code>Sum(term, {var, from, Infinity})</code> for a hypergeometric <code>term</code>
     * and a non-negative integer <code>from</code>.
     *
     * @return the closed form or {@link F#NIL}
     */
    public static IExpr sumToInfinity(IExpr term, ISymbol var, IExpr from, EvalEngine engine) {
      try {
        if (!from.isInteger() || from.isNegative()) {
          return F.NIL;
        }
        IExpr[] ratio = Sum.hypergeometricRatio(term, var, engine);
        if (ratio == null) {
          return F.NIL;
        }
        // shift the summation index to start at 0: ratio(from + var)
        IExpr num = engine.evaluate(F.Expand(F.xreplace(ratio[0], var, F.Plus(from, var))));
        IExpr den = engine.evaluate(F.Expand(F.xreplace(ratio[1], var, F.Plus(from, var))));
        int degNum = degreeOf(num, var, engine);
        int degDen = degreeOf(den, var, engine);
        if (degNum < 0 || degDen < 0) {
          return F.NIL;
        }
        IExpr lcNum = engine.evaluate(F.Coefficient(num, var, F.ZZ(degNum)));
        IExpr lcDen = engine.evaluate(F.Coefficient(den, var, F.ZZ(degDen)));
        if (lcDen.isZero()) {
          return F.NIL;
        }
        IExpr z = engine.evaluate(F.Divide(lcNum, lcDen));

        List<IExpr> upperRoots = roots(num, var, engine);
        List<IExpr> lowerRoots = roots(den, var, engine);
        if (upperRoots == null || lowerRoots == null) {
          return F.NIL;
        }

        // upper parameters a_i = -root_i of the numerator
        IASTAppendable aList = F.ListAlloc(upperRoots.size() + 1);
        for (IExpr r : upperRoots) {
          aList.append(engine.evaluate(F.Negate(r)));
        }
        // the (var+1) factor accounts for the 1/n! of the hypergeometric series
        boolean removedFactorial = false;
        IASTAppendable bList = F.ListAlloc(lowerRoots.size());
        for (IExpr r : lowerRoots) {
          if (!removedFactorial && engine.evaluate(F.Plus(r, F.C1)).isZero()) {
            removedFactorial = true;
            continue;
          }
          bList.append(engine.evaluate(F.Negate(r)));
        }
        if (!removedFactorial) {
          // multiply numerator and denominator by (var+1): adds an upper parameter 1
          aList.append(F.C1);
        }

        // --- CONVERGENCE CHECK ---
        int p = aList.argSize();
        int q = bList.argSize();
        if (p == q + 1) {
          IExpr absZ = engine.evaluate(F.Abs(z));
          // Diverges strictly for |z| > 1
          if (engine.evalTrue(F.Greater(absZ, F.C1))) {
            Errors.printMessage(S.Sum, "div", F.List(), engine);
            return F.NIL;
          }
          // Pure geometric series (p=1, q=0) also diverges strictly on the boundary |z| >= 1
          if (p == 1 && q == 0 && engine.evalTrue(F.GreaterEqual(absZ, F.C1))) {
            Errors.printMessage(S.Sum, "div", F.List(), engine);
            return F.NIL;
          }
        } else if (p > q + 1) {
          // Radius of convergence is 0
          if (engine.evalTrue(F.Unequal(z, F.C0))) {
            Errors.printMessage(S.Sum, "div", F.List(), engine);
            return F.NIL;
          }
        }

        IExpr hyper = buildHypergeometric(aList, bList, z, engine);
        if (hyper.isNIL()) {
          return F.NIL;
        }
        IExpr firstTerm = engine.evaluate(F.xreplace(term, var, from));
        IExpr result = engine.evaluate(F.Times(firstTerm, hyper));
        if (!isClosedForm(result)) {
          return F.NIL;
        }
        if (!numericallyVerify(term, var, from, result, engine)) {
          return F.NIL;
        }
        return result;
      } catch (RuntimeException rex) {
        return F.NIL;
      }
    }

    /**
     * Build the most specific generalized hypergeometric function for the given parameter lists.
     */
    private static IExpr buildHypergeometric(IAST aList, IAST bList, IExpr z, EvalEngine engine) {
      int na = aList.argSize();
      int nb = bList.argSize();
      if (na == 0 && nb == 1) {
        return engine.evaluate(F.Hypergeometric0F1(bList.arg1(), z));
      }
      if (na == 1 && nb == 0) {
        // 1F0(a; ; z) = (1-z)^(-a)
        return engine.evaluate(F.Power(F.Subtract(F.C1, z), F.Negate(aList.arg1())));
      }
      if (na == 1 && nb == 1) {
        return engine.evaluate(F.Hypergeometric1F1(aList.arg1(), bList.arg1(), z));
      }
      if (na == 2 && nb == 1) {
        return engine.evaluate(F.Hypergeometric2F1(aList.arg1(), aList.arg2(), bList.arg1(), z));
      }
      return engine.evaluate(F.HypergeometricPFQ(aList, bList, z));
    }

    /** A result counts as a closed form only if free of unevaluated sum/hypergeometric heads. */
    private static boolean isClosedForm(IExpr result) {
      return result.isPresent() //
          && result.isFreeAST(S.Sum) //
          && result.isFreeAST(S.HypergeometricPFQ) //
          && result.isFreeAST(S.Hypergeometric0F1) //
          && result.isFreeAST(S.Hypergeometric1F1) //
          && result.isFreeAST(S.Hypergeometric2F1);
    }

    /**
     * The multiset of roots of <code>poly</code> in <code>var</code>, or <code>null</code> if the
     * polynomial cannot be split completely into linear factors over the roots found by
     * {@code Solve}.
     */
    private static List<IExpr> roots(IExpr poly, ISymbol var, EvalEngine engine) {
      List<IExpr> result = new ArrayList<>();
      int d = degreeOf(poly, var, engine);
      if (d <= 0) {
        return result;
      }
      IExpr p = poly;
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(poly, F.C0), var));
      if (!solutions.isList()) {
        return null;
      }
      for (IExpr sol : (IAST) solutions) {
        if (sol.isList() && sol.size() >= 2 && sol.first().isRuleAST()) {
          IExpr root = engine.evaluate(sol.first().second());
          IExpr factor = F.Subtract(var, root);
          while (true) {
            IExpr remainder = engine.evaluate(F.PolynomialRemainder(p, factor, var));
            if (!remainder.isZero()) {
              break;
            }
            result.add(root);
            p = engine.evaluate(F.PolynomialQuotient(p, factor, var));
          }
        }
      }
      if (degreeOf(p, var, engine) > 0) {
        // could not fully factor -> not a nice hypergeometric term
        return null;
      }
      return result;
    }

    /**
     * Confirm <code>Sum(term, {var, from, from+N})</code> approaches <code>result</code>
     * numerically (free parameters set to a small value that keeps the series convergent).
     */
    private static boolean numericallyVerify(IExpr term, ISymbol var, IExpr from, IExpr result,
        EvalEngine engine) {
      try {
        IAST variables = new VariablesSet(term).getVarList();
        IASTAppendable rules = F.ListAlloc(variables.size());
        for (IExpr v : variables) {
          if (v.equals(var)) {
            continue;
          }
          rules.append(F.Rule(v, F.num(0.3)));
        }
        int fromInt = from.toIntDefault();
        if (fromInt == Integer.MIN_VALUE) {
          return false;
        }
        double partial = 0.0;
        for (int k = fromInt; k <= fromInt + 120; k++) {
          IExpr summand = engine.evaluate(F.N(F.ReplaceAll(F.xreplace(term, var, F.ZZ(k)), rules)));
          double summandValue = summand.evalfNaN();
          if (Double.isNaN(summandValue)) {
            return false;
          }
          partial += summandValue;
        }
        double closed = engine.evaluate(F.N(F.ReplaceAll(result, rules))).evalfNaN();
        if (Double.isNaN(closed)) {
          return false;
        }
        return Math.abs(partial - closed) < 1.0e-6;
      } catch (RuntimeException rex) {
        return false;
      }
    }
  }


  private static com.google.common.base.Supplier<Matcher> MATCHER1;

  public Sum() {}

  private static Matcher matcher1() {
    return MATCHER1.get();
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    // Optional Method -> "Polynomial" | "Geometric" | "Gosper" as last argument forces one
    // summation algorithm strictly.
    String forcedMethod = null;
    if (ast.argSize() >= 2 && ast.last().isRuleAST() && ast.last().first() == S.Method) {
      forcedMethod = methodName(ast.last().second());
      if (forcedMethod == null) {
        return F.NIL;
      }
      ast = ast.removeAtCopy(ast.argSize());
    }
    for (int i = 2; i < ast.size(); i++) {
      IExpr iterator = ast.get(i);
      if (!iterator.isList() && !iterator.isSymbol()) {
        // An iterator is either a list, {i, imax} or {i, imin, imax}, or the bare symbol of an
        // indefinite sum. A number is neither: Sum inherits its unrolling from Table, where a
        // count is a valid specification - Table[x, 3] is {x, x, x} - and so answered
        // Sum(x, 3) with 3*x. Mathematica leaves it alone.
        return F.NIL;
      }
    }
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      // Sum({f,g},{i,1,n}) is the list of the two sums
      return arg1.mapThread(ast, 1);
    }
    if (arg1.isAST()) {
      arg1 = F.expand(arg1, false, false, false);
    }
    if (arg1.isPlus()) {
      return arg1.mapThread(ast, 1);
    }
    IAST preevaledSum = engine.preevalForwardBackwardAST(ast, 1);
    return evaluateSum(preevaledSum, forcedMethod, engine);
  }

  /**
   * Map a <code>Method-&gt;...</code> option value to one of the supported algorithm names
   * <code>"Polynomial"</code>, <code>"Geometric"</code> or <code>"Gosper"</code>.
   *
   * @return the canonical method name or <code>null</code> if not recognized
   */
  private static String methodName(IExpr methodValue) {
    String name = null;
    if (methodValue.isString() || methodValue.isSymbol()) {
      name = methodValue.toString();
    }
    if (name != null) {
      if (name.equalsIgnoreCase("Polynomial")) {
        return "Polynomial";
      }
      if (name.equalsIgnoreCase("Geometric")) {
        return "Geometric";
      }
      if (name.equalsIgnoreCase("Gosper")) {
        return "Gosper";
      }
    }
    return null;
  }

  protected static IExpr evaluateSum(final IAST preevaledSum, EvalEngine engine) {
    return evaluateSum(preevaledSum, null, engine);
  }

  protected static IExpr evaluateSum(final IAST preevaledSum, String forcedMethod,
      EvalEngine engine) {
    if (forcedMethod != null) {
      // strictly use the requested algorithm and don't fall back to the heuristic rules
      return forcedSummation(preevaledSum, forcedMethod, engine);
    }
    if (preevaledSum.argSize() >= 2) {
      try {
        IExpr lastArg = preevaledSum.last();
        final IAST list = lastArg.makeList();
        if (list.isAST1()) {
          // indefinite sum case
          IExpr variable = list.arg1();
          if (preevaledSum.arg1().isFree(variable) && variable.isVariable()) {
            return indefiniteSum(preevaledSum, variable);
          }
        }

        IAST sumForm = preevaledSum;
        IAST lastList = list;
        if (list.isAST2()) {
          // Sum(f(x),..., {x, a}) ==> Sum(f(x),..., {x, 1, a})
          lastList = F.List(list.arg1(), F.C1, list.arg2());
          sumForm = sumForm.setAtCopy(sumForm.argSize(), lastList);
        }
        if (preevaledSum.argSize() > 2) {
          // Multiple iterators: reduce the innermost sum with the full engine, not just with the
          // rule table, and splice the result back. Only a closed form is spliced; a partially
          // evaluated sum, or a DifferenceRoot recurrence, would be pushed into the outer iterator
          // in a shape no outer summation can work with.
          IAST reducedSumForm = F.Sum(preevaledSum.arg1(), lastList);
          IExpr reducedResult = engine.evalQuietNIL(reducedSumForm);
          if (reducedResult.isPresent() && !reducedResult.equals(reducedSumForm)
              && reducedResult.isFreeAST(S.Sum) && reducedResult.isFreeAST(S.DifferenceRoot)) {
            IASTMutable result = sumForm.removeAtCopy(sumForm.argSize());
            result.set(1, reducedResult);
            return result;
          }
        } else {
          IExpr result = matcher1().apply(sumForm);
          if (result.isPresent()) {
            return result;
          }
        }

        IExpr temp = evaluateTableThrow(preevaledSum, Plus(), Plus(), engine);
        if (temp.isPresent()) {
          return temp;
        }
        VariablesSet variablesSet = determineIteratorExprVariables(preevaledSum);
        IAST varList = variablesSet.getVarList();
        IIterator<IExpr> iterator = null;
        if (lastArg.isList()) {
          lastArg = evalBlockWithoutReap(lastArg, varList);
          if (lastArg.isList()) {
            iterator = Iterator.create((IAST) lastArg, preevaledSum.argSize(), engine);
          } else {
            if (lastArg.isReal()) {
              iterator = Iterator.create(F.list(lastArg), preevaledSum.argSize(), engine);
            } else {
              // Non-list iterator `1` at position `2` does not evaluate to a real numeric value.
              return Errors.printMessage(preevaledSum.topHead(), "nliter",
                  F.list(lastArg, F.ZZ(preevaledSum.argSize())), engine);
            }
          }
        }
        IExpr arg1 = preevaledSum.arg1();
        if (arg1.isTimes()) {
          if (variablesSet.size() > 0) {
            temp = collectConstantFactors(preevaledSum, (IAST) arg1, variablesSet);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }

        if (iterator != null) {
          if (arg1.isZero()) {
            // Sum(0, {k, n, m})
            return F.C0;
          }
          // A list style iterator like {e, {2,1,1,1}} has no "upper limit" ({@link IIterator}
          // returns null for it); such iterators are already handled by evaluateTableThrow above.
          if (iterator.isValidVariable() && iterator.getUpperLimit() != null
              && iterator.getUpperLimit().isInfinity()) {
            if (arg1.isPositiveResult() && arg1.isIntegerResult()) {
              // Sum(n, {k, a, Infinity}) ;n is positive integer
              return F.CInfinity;
            }
            if (arg1.isNegativeResult() && arg1.isIntegerResult()) {
              // Sum(n, {k, a, Infinity}) ;n is negative integer
              return F.CNInfinity;
            }
          }

          if (iterator.isValidVariable() && iterator.isNumericFunction()) {
            IAST resultList = Plus();
            temp = evaluateLast(preevaledSum.arg1(), iterator, resultList, F.C0, S.Sum);
            if (temp.isNIL() || temp.equals(resultList)) {
              return F.NIL;
            }
            if (preevaledSum.isAST2()) {
              return temp;
            } else {
              IASTAppendable result = preevaledSum.removeAtClone(preevaledSum.argSize());
              result.set(1, temp);
              return result;
            }
          }

          // The symbolic summation below needs real limits and a step. A list style iterator has
          // none of them ({@link IIterator} returns null), so skip it here.
          if (iterator.isValidVariable() && !iterator.isNumericFunction()
              && iterator.getLowerLimit() != null && iterator.getUpperLimit() != null
              && iterator.getStep() != null) {
            if (iterator.getStep().isOne()) {
              if (iterator.getUpperLimit().isDirectedInfinity()) {
                temp = definiteSumInfinity(arg1, iterator, (IAST) lastArg, engine);
              } else {
                temp = definiteSum(arg1, iterator, (IAST) lastArg, engine);
                if (temp.isNIL()) {
                  // Polynomial -> Geometric -> Gosper algorithm cascade
                  temp = summationByMethod(arg1, iterator.getVariable(), iterator.getLowerLimit(),
                      iterator.getUpperLimit(), null, engine);
                }
                if (temp.isNIL()) {
                  // partial fractions -> PolyGamma / HarmonicNumber; after Gosper, so that a
                  // telescoping sum keeps its elementary closed form
                  temp = rationalSum(arg1, iterator.getVariable(), iterator.getLowerLimit(),
                      iterator.getUpperLimit(), engine);
                }
                if (temp.isNIL()) {
                  // DIFFERENCE ROOT FALLBACK
                  // Holonomic sequence encoding using DifferenceRoot
                  temp = differenceRootFallback(arg1, iterator.getVariable(),
                      iterator.getLowerLimit(), iterator.getUpperLimit(), engine);
                }
              }
            } else {
              // A stepped iterator is reindexed onto a step of 1 and summed again; the engines
              // above all assume a step of 1.
              temp = steppedSum(arg1, iterator, engine);
            }
            if (temp.isPresent()) {
              if (preevaledSum.isAST2()) {
                return temp;
              }
              IASTAppendable result = preevaledSum.removeAtClone(preevaledSum.argSize());
              result.set(1, temp);
              return result;
            }
          }

        } else if (lastArg.isSymbol()) {
          temp = indefiniteSum(arg1, (ISymbol) lastArg);
          if (temp.isNIL()) {
            // Polynomial -> Geometric -> Gosper algorithm cascade for the indefinite sum
            temp = summationByMethod(arg1, (ISymbol) lastArg, F.NIL, F.NIL, null, engine);
          }
          if (temp.isPresent()) {
            if (preevaledSum.isAST2()) {
              return temp;
            } else {
              IASTAppendable result = preevaledSum.removeAtClone(preevaledSum.argSize());
              result.set(1, temp);
              return result;
            }
          }
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(preevaledSum.topHead(), ve, engine);
      }
    }
    return F.NIL;
  }

  /**
   * Create a new Sum() by removing last iterator or return result of indefinite sum case for Sum(a,
   * x)
   *
   * @param ast
   * @param variable the iterator variable
   * @return
   */
  private static IExpr indefiniteSum(final IAST ast, IExpr variable) {
    IExpr result = F.Times(ast.arg1(), variable);
    int argSize = ast.argSize();
    if (argSize == 2) {
      return result;
    }
    IASTAppendable newSum = ast.removeAtClone(argSize);
    newSum.set(1, result);
    return newSum;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  private static IExpr collectConstantFactors(final IAST ast, IAST prod,
      VariablesSet variablesSet) {
    IASTAppendable filterAST = F.TimesAlloc(16);
    IASTAppendable restAST = F.TimesAlloc(16);
    prod.filter(filterAST, restAST, VariablesSet.isFree(variablesSet));
    if (filterAST.size() > 1) {
      IASTMutable reducedSum = ast.setAtCopy(1, restAST.oneIdentity1());
      return F.Times(filterAST.oneIdentity0(), reducedSum);
    }
    return F.NIL;
  }

  /**
   * Evaluate the definite sum: <code>Sum(arg1, {var, from, to})</code>
   *
   * @param expr the first argument of the <code>Sum()</code> function.
   * @param iterator the current iterator definition for which the Sum should be evaluated
   * @param list constructed as <code>{Symbol: var, Integer: from, Symbol: to}</code>
   * @param engine the evaluation engine
   * @return
   */
  private static IExpr definiteSum(final IExpr expr, final IIterator<IExpr> iterator, IAST list,
      EvalEngine engine) {
    final ISymbol var = iterator.getVariable();
    final IExpr from = iterator.getLowerLimit();
    final IExpr to = iterator.getUpperLimit();

    if (expr.isFree(var, true)) {
      if (from.isOne()) {
        return F.Times(to, expr);
      }
      if (from.isZero()) {
        return F.Times(Plus(to, C1), expr);
      }
      if (!F.C1.greater(from).isTrue() && !from.greater(to).isTrue()) {
        return F.Times(Plus(C1, F.Negate(from), to), expr);
      }
    } else {
      if (expr.isTimes()) {
        // Sum( Times(a,b,c,...), {var, from, to} )
        IASTAppendable filterCollector = F.TimesAlloc(16);
        IASTAppendable restCollector = F.TimesAlloc(16);
        ((IAST) expr).filter(filterCollector, restCollector, new Predicate<IExpr>() {
          @Override
          public boolean test(IExpr input) {
            return input.isFree(var, true);
          }
        });
        if (filterCollector.size() > 1) {
          IExpr temp = engine.evalQuiet(F.Sum(restCollector.oneIdentity1(), list));
          if (temp.isFreeAST(S.Sum)) {
            filterCollector.append(temp);
            return filterCollector;
          }
        }
      }

      if (expr.equals(var)) {
        if ((from.isVariable() && !from.equals(var)) || (to.isVariable() && !to.equals(var))) {
          // Sum(i, {i, from, to})
          return Times(C1D2, Plus(Subtract(to, from), C1), Plus(from, to));
        }
      }

      if (!engine.evalGreater(C0, from) && !engine.evalGreater(from, to)) {
        IExpr temp = F.NIL;
        if (expr.isPower()) {
          temp = sumPower((IAST) expr, var, from, to);
        } else if (expr.equals(var)) {
          temp = sumPowerFormula(var, F.C1, from, to);
        }
        if (temp.isPresent()) {
          return temp;
        }
      }

      if (expr.isPower() && !engine.evalGreater(C1, from) && !engine.evalGreater(from, to)) {
        IAST powAST = (IAST) expr;
        if (powAST.equalsAt(1, var) && powAST.arg2().isFree(var) && to.isFree(var)) {
          IExpr exponent = powAST.arg2();
          if (from.isOne()) {
            // i^(k),{i,1,n}) ==> HarmonicNumber(n,-k)
            return F.HarmonicNumber(to, exponent.negate());
          }
          if (exponent.isNumber() || from.isInteger()) {
            // i^k,{i,n,m} ==> HarmonicNumber(m,-k)-HarmonicNumber(n-1,-k)
            //
            // The HurwitzZeta form below has a pole for the exponent -1: for Sum(1/i,{i,n,m}) it
            // is HurwitzZeta(1,n)-HurwitzZeta(1,1+m), which is ComplexInfinity-ComplexInfinity.
            return F.Subtract(F.HarmonicNumber(to, exponent.negate()),
                F.HarmonicNumber(F.Subtract(from, F.C1), exponent.negate()));
          }
          // i^k,{i,n,m} ==> HurwitzZeta(-k, n)-HurwitzZeta(-k,1+m)
          return F.Subtract(F.HurwitzZeta(F.Negate(exponent), from),
              F.HurwitzZeta(F.Negate(exponent), F.Plus(1, to)));
        }
      }

      try {
        IAST resultList = Plus();
        IExpr temp = evaluateLast(expr, iterator, resultList, F.NIL, S.Sum);
        if (temp.isPresent() && !temp.equals(resultList)) {
          return temp;
        }
      } catch (RecursionLimitExceeded rle) {
        return Errors.printMessage(S.Sum, rle, engine);
      }
    }
    if (from.isPositive() && !isRationalInVar(expr, var, true, engine)) {
      // A rational summand is left to rationalSum(): shifting the range to 0 would split its
      // closed form into two PolyGamma() differences around the pole at var == 0.
      IExpr temp1 = engine.evalQuiet(F.Sum(expr, F.list(var, C0, from.minus(F.C1))));
      if (!temp1.isComplexInfinity() && temp1.isFreeAST(S.Sum)) {
        IExpr temp2 = engine.evalQuietNIL(F.Sum(expr, F.list(var, C0, to)));
        if (temp2.isPresent() && !temp2.isComplexInfinity()) {
          return F.Subtract(temp2, temp1);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Evaluate the definite sum: <code>Sum(arg1, {var, from, Infinity})</code>
   *
   * @param expr the first argument of the <code>Sum()</code> function.
   * @param iterator the current iterator definition for which the Sum should be evaluated
   * @param list constructed as <code>{Symbol: var, Integer: from, Symbol: to}</code>
   * @param engine the evaluation engine
   * @return
   */
  private static IExpr definiteSumInfinity(final IExpr expr, final IIterator<IExpr> iterator,
      IAST list, EvalEngine engine) {
    final ISymbol var = iterator.getVariable();
    final IExpr from = iterator.getLowerLimit();
    final IExpr to = iterator.getUpperLimit();

    if (expr.isZero()) {
      return F.C0;
    }
    if (from.isInteger() && !from.isOne()) {
      IExpr subSum = engine.evaluateNIL(F.Sum(expr, F.list(var, C1, to)));
      if (subSum.isPresent()) {
        if (S.Less.ofQ(engine, from, C1)) {
          IExpr subSum2 = engine.evaluateNIL(F.Sum(expr, F.list(var, from, C0)));
          if (subSum2.isPresent()) {
            subSum2 = subSum2.plus(subSum);
            if (subSum2.isPlus()) {
              return F.Together(subSum2);
            }
            return subSum2;
          }
          return F.NIL;
        }
        if (from.greater(F.C1).isTrue()) {
          return F.Subtract(subSum, F.Sum(expr, F.list(var, C1, from.minus(F.C1))));
        }
      }
    }
    // rational summand over [from, Infinity) -> PolyGamma closed form
    IExpr rational = rationalSum(expr, var, from, to, engine);
    if (rational.isPresent()) {
      return rational;
    }
    // hypergeometric summand over [from, Infinity) -> HypergeometricPFQ closed form. Used as a last
    // resort so that summands handled by the rules/reduction above keep their established form.
    if (from.isInteger() && !from.isNegative()) {
      IExpr hyper = SumHypergeometric.sumToInfinity(expr, var, from, engine);
      if (hyper.isPresent()) {
        return hyper;
      }
    }
    return F.NIL;
  }

  /**
   * Evaluate the indefinite sum: <code>Sum(arg1, var)</code>
   *
   * @param arg1
   * @param var
   * @return
   */
  private static IExpr indefiniteSum(IExpr arg1, final ISymbol var) {
    if (arg1.isTimes()) {
      // Sum( Times(a,b,c,...), var )
      IASTAppendable filterCollector = F.TimesAlloc(16);
      IASTAppendable restCollector = F.TimesAlloc(16);
      ((IAST) arg1).filter(filterCollector, restCollector, new Predicate<IExpr>() {
        @Override
        public boolean test(IExpr input) {
          return input.isFree(var, true);
        }
      });
      if (filterCollector.size() > 1) {
        if (restCollector.isAST1()) {
          filterCollector.append(F.Sum(restCollector.arg1(), var));
        } else {
          filterCollector.append(F.Sum(restCollector, var));
        }
        return filterCollector;
      }
    } else if (arg1.isPower()) {
      return sumPower((IAST) arg1, var, F.C0, var.dec());
    } else if (arg1.equals(var)) {
      // evaluate as Sum(var, {var, F.C0, var-1)
      return sumPowerFormula(var, F.C1, F.C0, var.dec());
    }
    return F.NIL;
  }

  /**
   * See
   * <a href= "http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions">
   * Wikipedia - Summation#Some_summations_of_polynomial_expressions</a>.
   *
   * @param powAST an AST of the form <code>Power(var, i_Integer)</code>
   * @param var
   * @param from TODO
   * @param to
   */
  public static IExpr sumPower(final IAST powAST, final ISymbol var, IExpr from, final IExpr to) {
    if (powAST.equalsAt(1, var) && powAST.arg2().isInteger()) {
      IInteger p = (IInteger) powAST.arg2();
      if (p.isPositive()) {
        return sumPowerFormula(var, p, from, to);
      }
    }
    return F.NIL;
  }

  /**
   * Sum of {@code k^p} and {@code k} in the range {@code [from,to]}. The formula assumes that
   * {@code 0 <= from <= to}
   * <p>
   * See <a href= "https://en.wikipedia.org/wiki/Faulhaber%27s_formula"> Wikipedia - Faulhaber's
   * formula</a>. * @param k the base of the power {@code k^p}
   * 
   * @param p the exponent of the power {@code k^p}
   * @param from the from value (included) of the range {@code [from,to]}
   * @param to the to value (included) of the range {@code [from,to]}
   */
  public static IExpr sumPowerFormula(final ISymbol k, IInteger p, IExpr from, final IExpr to) {
    // assuming 0 < from < to
    IInteger pInc1 = p.inc();
    IExpr term1 =
        F.Times(F.Divide(1, pInc1), F.Subtract(F.BernoulliB(pInc1, to.inc()), F.BernoulliB(pInc1)));
    if (from.isZero() || from.isOne()) {
      return term1;
    }

    IExpr term2 =
        F.Times(F.Divide(1, pInc1), F.Subtract(F.BernoulliB(pInc1, from), F.BernoulliB(pInc1)));
    return F.Subtract(term1, term2);
  }

  /**
   * Sum over an iterator whose step differs from <code>1</code> by reindexing it onto a step of
   * <code>1</code>, see {@link ListFunctions.Table#reindexStepIterator(IExpr, IIterator)}.
   *
   * @return the closed form or {@link F#NIL} if the reindexed sum has none either
   */
  private static IExpr steppedSum(IExpr expr, IIterator<IExpr> iterator, EvalEngine engine) {
    IAST reindexed = reindexStepIterator(expr, iterator);
    if (reindexed.isNIL()) {
      return F.NIL;
    }
    IExpr result = engine.evalQuietNIL(F.Sum(reindexed.arg1(), reindexed.arg2()));
    if (result.isPresent() && result.isFreeAST(S.Sum) && result.isFreeAST(S.DifferenceRoot)) {
      return result;
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------------------
  // Rational summands: partial fractions -> PolyGamma / HarmonicNumber
  // ---------------------------------------------------------------------------

  /**
   * Test if <code>term</code> is a rational function of <code>var</code>, that is the quotient of
   * two polynomials in <code>var</code>.
   *
   * @param properOnly only accept a denominator which really depends on <code>var</code>; a
   *        polynomial summand is summed by Faulhaber's formula and is not a case for
   *        {@link #rationalSum(IExpr, ISymbol, IExpr, IExpr, EvalEngine)}
   */
  private static boolean isRationalInVar(IExpr term, ISymbol var, boolean properOnly,
      EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(term));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (properOnly && denominator.isFree(var, true)) {
      return false;
    }
    return engine.evalTrue(F.PolynomialQ(numerator, var))
        && engine.evalTrue(F.PolynomialQ(denominator, var));
  }

  /**
   * Sum a rational summand in closed form by splitting it into partial fractions. Each part has to
   * be of the shape <code>c/(b*var+a)^m</code>; the sum of <code>1/(var+a)^m</code> over
   * <code>[from,to]</code> is
   * <code>(-1)^m/(m-1)! * (PolyGamma(m-1,from+a) - PolyGamma(m-1,to+1+a))</code>, and over
   * <code>[from,Infinity)</code> it is <code>(-1)^m/(m-1)! * PolyGamma(m-1,from+a)</code> once the
   * divergent parts of the first order terms have cancelled.
   *
   * @param term the summand
   * @param var the summation variable
   * @param from the lower limit
   * @param to the upper limit, may be {@link F#CInfinity}
   * @return the closed form or {@link F#NIL}
   */
  private static IExpr rationalSum(IExpr term, ISymbol var, IExpr from, IExpr to,
      EvalEngine engine) {
    try {
      IExpr together = engine.evaluate(F.Together(term));
      IExpr numerator = engine.evaluate(F.Numerator(together));
      IExpr denominator = engine.evaluate(F.Denominator(together));
      if (denominator.isZero() || denominator.isFree(var, true)
          || !engine.evalTrue(F.PolynomialQ(numerator, var))
          || !engine.evalTrue(F.PolynomialQ(denominator, var))) {
        // a polynomial summand is summed by Faulhaber's formula, not here
        return F.NIL;
      }
      final boolean infinite = to.isInfinity();

      // Split off the polynomial part first: Apart() does not do it for a parametric denominator,
      // see Apart(x/(a+b*x),x)
      IExpr polynomialPart = engine.evaluate(F.PolynomialQuotient(numerator, denominator, var));
      IExpr remainder = engine.evaluate(F.PolynomialRemainder(numerator, denominator, var));
      IExpr apart = engine.evaluate(F.Apart(F.Divide(remainder, denominator), var));
      IAST parts = apart.isPlus() ? (IAST) apart : F.Plus(apart);

      IASTAppendable result = F.PlusAlloc(parts.size() + 1);
      IASTAppendable firstOrderCoefficients = F.PlusAlloc(parts.size());
      for (int i = 1; i < parts.size(); i++) {
        IExpr part = parts.get(i);
        if (part.isZero()) {
          continue;
        }
        if (part.isFree(var, true) || engine.evalTrue(F.PolynomialQ(part, var))) {
          // Apart() can leave a polynomial piece behind
          polynomialPart = engine.evaluate(F.Plus(polynomialPart, part));
          continue;
        }
        IExpr[] shiftedPower = linearDenominatorPower(part, var, engine);
        if (shiftedPower == null) {
          // not of the shape c/(b*var+a)^m, for example an irreducible quadratic denominator
          return F.NIL;
        }
        IExpr coefficient = shiftedPower[0];
        IExpr shift = shiftedPower[1];
        int power = shiftedPower[2].toIntDefault();
        if (power < 1) {
          return F.NIL;
        }
        IExpr lower = engine.evaluate(F.Plus(from, shift));
        if (isPolyGammaPole(lower)) {
          // the summation range runs through the pole of this part
          return F.NIL;
        }
        IExpr factor = F.Times(coefficient, polyGammaTailFactor(power));
        if (power == 1) {
          firstOrderCoefficients.append(coefficient);
        }
        if (infinite) {
          result.append(F.Times(factor, F.PolyGamma(F.ZZ(power - 1), lower)));
        } else {
          IExpr upper = engine.evaluate(F.Plus(to, C1, shift));
          if (isPolyGammaPole(upper)) {
            return F.NIL;
          }
          if (shift.isInteger()) {
            // HarmonicNumber reads better than PolyGamma for an integer shift
            result.append(F.Times(coefficient,
                F.Subtract(F.HarmonicNumber(F.Plus(to, shift), F.ZZ(power)),
                    F.HarmonicNumber(F.Plus(from, shift, F.CN1), F.ZZ(power)))));
          } else {
            result.append(F.Times(factor, F.Subtract(F.PolyGamma(F.ZZ(power - 1), lower),
                F.PolyGamma(F.ZZ(power - 1), upper))));
          }
        }
      }

      if (infinite) {
        if (!engine.evaluate(polynomialPart).isZero()) {
          // a polynomial part diverges
          Errors.printMessage(S.Sum, "div", F.List(), engine);
          return F.NIL;
        }
        // Each first order part contributes a divergent Log(); they cancel out exactly when the
        // first order coefficients sum to zero, and what is left is -c*PolyGamma(0, from+a).
        IExpr coefficientSum = engine.evaluate(F.Together(firstOrderCoefficients));
        if (!coefficientSum.isZero()) {
          coefficientSum = engine.evaluate(F.Simplify(coefficientSum));
        }
        if (!coefficientSum.isZero()) {
          if (coefficientSum.isNumber()) {
            Errors.printMessage(S.Sum, "div", F.List(), engine);
          }
          return F.NIL;
        }
      } else if (!engine.evaluate(polynomialPart).isZero()) {
        IExpr antidifference = polynomialAntidifference(polynomialPart, var, engine);
        if (antidifference.isNIL()) {
          return F.NIL;
        }
        // FunctionExpand resolves the FactorialPower() basis the antidifference is built in
        result.append(engine.evaluate(F.Expand(F.FunctionExpand(
            F.Subtract(F.xreplace(antidifference, var, F.Plus(to, C1)),
                F.xreplace(antidifference, var, from))))));
      }
      if (result.isAST0()) {
        return F.NIL;
      }
      if (infinite) {
        // the first order parts cancel against each other, which Together() carries out
        return engine.evaluate(F.Together(result));
      }
      return engine.evaluate(result);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Decompose a partial fraction <code>part</code> into <code>coefficient/(var+shift)^power</code>.
   *
   * @return the array <code>{coefficient, shift, power}</code> or <code>null</code> if
   *         <code>part</code> does not have that shape
   */
  private static IExpr[] linearDenominatorPower(IExpr part, ISymbol var, EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(part));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (!numerator.isFree(var, true)) {
      return null;
    }
    int power = engine.evaluate(F.Exponent(denominator, var)).toIntDefault();
    if (power < 1) {
      return null;
    }
    IExpr leadingCoefficient = engine.evaluate(F.Coefficient(denominator, var, F.ZZ(power)));
    if (leadingCoefficient.isZero() || !leadingCoefficient.isFree(var, true)) {
      return null;
    }
    // For denominator == leadingCoefficient*(var-root)^power the coefficient of var^(power-1) is
    // -power*root*leadingCoefficient, which determines the root without solving anything.
    IExpr root = engine.evaluate(F.Divide(F.Coefficient(denominator, var, F.ZZ(power - 1)),
        F.Times(F.ZZ(-power), leadingCoefficient)));
    IExpr check = engine.evaluate(F.Expand(F.Subtract(denominator,
        F.Times(leadingCoefficient, F.Power(F.Subtract(var, root), F.ZZ(power))))));
    if (!check.isZero()) {
      return null;
    }
    return new IExpr[] {engine.evaluate(F.Divide(numerator, leadingCoefficient)),
        engine.evaluate(F.Negate(root)), F.ZZ(power)};
  }

  /**
   * The factor <code>(-1)^m/(m-1)!</code> which relates <code>PolyGamma(m-1,z)</code> to the tail
   * <code>Sum(1/(z+k)^m, {k,0,Infinity})</code>.
   */
  private static IExpr polyGammaTailFactor(int power) {
    IInteger factorial = C1;
    for (int i = 2; i < power; i++) {
      factorial = factorial.multiply(F.ZZ(i));
    }
    IExpr factor = F.QQ(C1, factorial);
    return (power % 2 == 0) ? factor : factor.negate();
  }

  /** Test if <code>expr</code> is a pole of {@code PolyGamma}, i.e. a non positive integer. */
  private static boolean isPolyGammaPole(IExpr expr) {
    return expr.isInteger() && !expr.isPositive();
  }

  // ---------------------------------------------------------------------------
  // Indefinite/symbolic summation engine: Polynomial -> Geometric -> Gosper
  // ---------------------------------------------------------------------------

  /**
   * Strictly evaluate a single-iterator or indefinite <code>Sum</code> with a forced algorithm
   * (<code>Method-&gt;"Polynomial" | "Geometric" | "Gosper"</code>).
   */
  private static IExpr forcedSummation(IAST sum, String method, EvalEngine engine) {
    if (!sum.isAST2()) {
      return F.NIL;
    }
    IExpr term = sum.arg1();
    IExpr spec = sum.arg2();
    if (spec.isSymbol() && spec.isVariable()) {
      return summationByMethod(term, (ISymbol) spec, F.NIL, F.NIL, method, engine);
    }
    IAST list = spec.makeList();
    if (list.isAST1() && list.arg1().isVariable()) {
      return summationByMethod(term, (ISymbol) list.arg1(), F.NIL, F.NIL, method, engine);
    }
    if (list.size() >= 3 && list.arg1().isVariable()) {
      ISymbol var = (ISymbol) list.arg1();
      IExpr from;
      IExpr to;
      if (list.isAST2()) {
        from = F.C1;
        to = list.arg2();
      } else if (list.isAST3()) {
        from = list.arg2();
        to = list.arg3();
      } else {
        return F.NIL;
      }
      if (to.isDirectedInfinity()) {
        return F.NIL;
      }
      return summationByMethod(term, var, from, to, method, engine);
    }
    return F.NIL;
  }

  /**
   * Compute an indefinite (<code>to.isNIL()</code>) or definite symbolic sum by computing the
   * antidifference <code>T</code> of <code>term</code> (so that
   * <code>T(var+1)-T(var) == term</code> ) via the cascade
   * <code>Polynomial -&gt; Geometric -&gt; Gosper</code> and verifying it.
   *
   * @param term the summand
   * @param var the summation variable
   * @param from lower limit for the definite case (or <code>F.NIL</code>)
   * @param to upper limit for the definite case (or <code>F.NIL</code> for the indefinite sum)
   * @param forcedMethod one of <code>"Polynomial"</code>, <code>"Geometric"</code>,
   *        <code>"Gosper"</code> or <code>null</code> to run the full cascade
   * @return the closed form or <code>F.NIL</code>
   */
  private static IExpr summationByMethod(IExpr term, ISymbol var, IExpr from, IExpr to,
      String forcedMethod, EvalEngine engine) {
    if (term.isFree(var, true)) {
      return F.NIL;
    }
    IExpr antidiff = F.NIL;
    if (forcedMethod == null || forcedMethod.equals("Polynomial")) {
      antidiff = polynomialAntidifference(term, var, engine);
    }
    if (antidiff.isNIL() && (forcedMethod == null || forcedMethod.equals("Geometric"))) {
      antidiff = geometricAntidifference(term, var, engine);
    }
    if (antidiff.isNIL() && (forcedMethod == null || forcedMethod.equals("Gosper"))) {
      antidiff = gosperAntidifference(term, var, engine);
    }
    if (antidiff.isNIL()) {
      return F.NIL;
    }
    if (!verifyAntidifference(antidiff, term, var, engine)) {
      return F.NIL;
    }
    if (to.isNIL()) {
      return antidiff;
    }
    IExpr upper = F.xreplace(antidiff, var, F.Plus(to, F.C1));
    IExpr lower = F.xreplace(antidiff, var, from);
    return engine.evaluate(F.Subtract(upper, lower));
  }

  /**
   * Check that <code>antidiff(var+1) - antidiff(var) == term</code>, i.e. that
   * {@code DifferenceDelta} of the antidifference returns the summand (left inverse property). A
   * symbolic <code>Simplify</code> is tried first; if that does not close to zero (e.g.
   * <code>(q1*q2)^i</code> vs. <code>q1^i*q2^i</code>) the identity is confirmed by numeric
   * sampling.
   */
  private static boolean verifyAntidifference(IExpr antidiff, IExpr term, ISymbol var,
      EvalEngine engine) {
    try {
      IExpr shifted = F.xreplace(antidiff, var, F.Plus(var, F.C1));
      IExpr residual = F.Subtract(F.Subtract(shifted, antidiff), term);
      if (engine.evaluate(F.Simplify(residual)).isZero()) {
        return true;
      }
      return numericallyZero(residual, F.List(shifted, antidiff, term), var, engine);
    } catch (RuntimeException rex) {
      return false;
    }
  }

  /**
   * Confirm that <code>residual</code> vanishes by evaluating it at several integer points for
   * <code>var</code> with the remaining free symbols set to distinct sample values. Every sample
   * point has to be decided; a point which does not evaluate to a number leaves the identity
   * unconfirmed and fails the check.
   *
   * @param residual the expression which has to vanish
   * @param scaleParts the terms whose cancellation produces <code>residual</code>; their magnitude
   *        is the scale against which an inexact residual is compared
   * @param var the summation variable
   */
  private static boolean numericallyZero(IExpr residual, IAST scaleParts, ISymbol var,
      EvalEngine engine) {
    IAST variables = new VariablesSet(residual).getVarList();
    // The free parameters are sampled above every sample point of `var`, so that a factor like
    // Binomial(parameter, var) in the residual is not trivially zero at every one of them.
    int[] parameterValues = {11, 13, 17, 19, 23, 29, 31, 37, 41, 43};
    int[] varPoints = {5, 6, 7, 8};
    for (int varValue : varPoints) {
      IASTAppendable rules = F.ListAlloc(variables.size());
      rules.append(F.Rule(var, F.ZZ(varValue)));
      int p = 0;
      for (IExpr v : variables) {
        if (v.equals(var)) {
          continue;
        }
        if (p >= parameterValues.length) {
          // too many free parameters to sample
          return false;
        }
        rules.append(F.Rule(v, F.ZZ(parameterValues[p++])));
      }
      IExpr value = engine.evaluate(F.ReplaceAll(residual, rules));
      if (value.isNumber()) {
        if (!value.isZero()) {
          return false;
        }
        continue;
      }
      // Inexact fallback: compare the residual against the size of the terms which had to cancel.
      // An absolute Chop() would reject a correct antidifference as soon as those terms grow.
      double residualValue = numericAbs(value, engine);
      if (Double.isNaN(residualValue)) {
        return false;
      }
      double scale = 1.0;
      for (IExpr part : scaleParts) {
        double partValue = numericAbs(engine.evaluate(F.ReplaceAll(part, rules)), engine);
        if (Double.isNaN(partValue)) {
          return false;
        }
        scale = Math.max(scale, partValue);
      }
      if (residualValue > 1.0e-8 * scale) {
        return false;
      }
    }
    return true;
  }

  /**
   * The absolute value of <code>expr</code> as a <code>double</code>, or <code>Double.NaN</code> if
   * it does not evaluate to a finite real number.
   */
  private static double numericAbs(IExpr expr, EvalEngine engine) {
    try {
      IExpr value = engine.evalQuietNIL(F.N(F.Abs(expr)));
      if (value.isPresent() && value.isReal()) {
        double d = value.evalf();
        return Double.isFinite(d) ? d : Double.NaN;
      }
    } catch (RuntimeException rex) {
      //
    }
    return Double.NaN;
  }

  /**
   * Polynomial antidifference using Newton forward differences in the falling-factorial basis (no
   * Bernoulli numbers). For a polynomial <code>p(var)</code> of degree <code>d</code> the
   * antidifference is <code>Sum_{m=0..d} (Delta^m p)(0)/(m+1) * FactorialPower(var, m+1)</code>.
   */
  private static IExpr polynomialAntidifference(IExpr term, ISymbol var, EvalEngine engine) {
    if (!engine.evalTrue(F.PolynomialQ(term, var))) {
      return F.NIL;
    }
    int d = degreeOf(term, var, engine);
    if (d < 0) {
      return F.NIL;
    }
    IASTAppendable result = F.PlusAlloc(d + 2);
    for (int m = 0; m <= d; m++) {
      // forward difference Delta^m p at 0 : Sum_{j=0..m} (-1)^(m-j) Binomial(m,j) p(j)
      IASTAppendable cm = F.PlusAlloc(m + 1);
      for (int j = 0; j <= m; j++) {
        IExpr pj = F.xreplace(term, var, F.ZZ(j));
        cm.append(F.Times(F.Power(F.CN1, F.ZZ(m - j)), F.Binomial(F.ZZ(m), F.ZZ(j)), pj));
      }
      IExpr cmValue = engine.evaluate(cm);
      if (cmValue.isZero()) {
        continue;
      }
      result.append(F.Times(F.Times(cmValue, F.Power(F.ZZ(m + 1), F.CN1)),
          F.FactorialPower(var, F.ZZ(m + 1))));
    }
    return engine.evaluate(result);
  }

  /**
   * Antidifference of <code>p(var) * r^var</code> with polynomial <code>p</code> and <code>r</code>
   * free of <code>var</code> (the bases of several <code>r^var</code> factors are combined). The
   * antidifference <code>q(var) r^var</code> is found from
   * <code>r*q(var+1) - q(var) == p(var)</code> by undetermined coefficients.
   */
  private static IExpr geometricAntidifference(IExpr term, ISymbol var, EvalEngine engine) {
    try {
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      IExpr rho = F.C1;
      IASTAppendable polyFactors = F.TimesAlloc(factors.size());
      for (IExpr f : factors) {
        if (f.isPower()) {
          IExpr base = f.base();
          IExpr expo = f.exponent();
          if (base.isFree(var, true)) {
            IExpr[] lin = expo.linear(var);
            if (lin != null && !lin[1].isZero()) {
              rho = F.Times(rho, F.Power(base, lin[1]));
              polyFactors.append(F.Power(base, lin[0]));
              continue;
            }
          }
        }
        if (f.isFree(var, true)) {
          polyFactors.append(f);
          continue;
        }
        if (engine.evalTrue(F.PolynomialQ(f, var))) {
          polyFactors.append(f);
          continue;
        }
        return F.NIL;
      }
      rho = engine.evaluate(rho);
      if (rho.isOne()) {
        // pure polynomial -> handled by the Polynomial method
        return F.NIL;
      }
      IExpr p = engine.evaluate(polyFactors);
      if (!engine.evalTrue(F.PolynomialQ(p, var))) {
        return F.NIL;
      }
      int d = degreeOf(p, var, engine);
      if (d < 0) {
        return F.NIL;
      }
      ISymbol[] coeff = new ISymbol[d + 1];
      IASTAppendable q = F.PlusAlloc(d + 1);
      IASTAppendable unknowns = F.ListAlloc(d + 1);
      for (int j = 0; j <= d; j++) {
        coeff[j] = F.Dummy("geo$" + j);
        q.append(F.Times(coeff[j], F.Power(var, F.ZZ(j))));
        unknowns.append(coeff[j]);
      }
      IExpr qVar = q;
      IExpr qShift = F.xreplace(qVar, var, F.Plus(var, F.C1));
      IExpr equation = F.Subtract(F.Subtract(F.Times(rho, qShift), qVar), p);
      IExpr solution = solveForUnknowns(equation, var, unknowns, engine);
      if (solution.isNIL()) {
        return F.NIL;
      }
      IExpr qSolved = substituteAndZero(qVar, solution, coeff, engine);
      return engine.evaluate(F.Times(qSolved, F.Power(rho, var)));
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /**
   * Gosper's algorithm for indefinite summation of a hypergeometric term: term-ratio rationality
   * test, Gosper&ndash;Petkov&scaron;ek normal form via dispersion (resultant) and gcd peeling, and
   * the degree-bounded key equation solved by undetermined coefficients. Returns
   * <code>T = (b(var-1)/c(var)) * x(var) * term</code> with <code>T(var+1)-T(var) == term</code>.
   */
  private static IExpr gosperAntidifference(IExpr term, ISymbol var, EvalEngine engine) {
    try {
      if (term.isZero()) {
        return F.C0;
      }
      // term-ratio test: compute t(var+1)/t(var) deterministically per factor so that
      // factorial/polynomial-power ratios reduce exactly to rational functions of var
      IExpr[] ratioND = hypergeometricRatio(term, var, engine);
      if (ratioND == null) {
        return F.NIL;
      }
      // Gosper-Petkovsek normal form: ratio = (a(var)/b(var)) * c(var+1)/c(var)
      IExpr a = ratioND[0];
      IExpr b = ratioND[1];
      IExpr c = F.C1;
      ISymbol h = F.Dummy("gosH");
      IExpr bShiftH = F.xreplace(b, var, F.Plus(var, h));
      IExpr resultant = engine.evaluate(F.Resultant(a, bShiftH, var));
      for (int j : nonNegativeIntegerRoots(resultant, h, engine)) {
        while (true) {
          IExpr bShiftJ = F.xreplace(b, var, F.Plus(var, F.ZZ(j)));
          IExpr g = engine.evaluate(F.PolynomialGCD(a, bShiftJ));
          if (degreeOf(g, var, engine) <= 0) {
            break;
          }
          a = engine.evaluate(F.PolynomialQuotient(a, g, var));
          IExpr gShiftMinusJ = F.xreplace(g, var, F.Subtract(var, F.ZZ(j)));
          b = engine.evaluate(F.PolynomialQuotient(b, gShiftMinusJ, var));
          IASTAppendable prod = F.TimesAlloc(j + 1);
          prod.append(c);
          for (int i = 1; i <= j; i++) {
            prod.append(F.xreplace(g, var, F.Subtract(var, F.ZZ(i))));
          }
          c = engine.evaluate(prod);
        }
      }
      // key equation: a(var) x(var+1) - b(var-1) x(var) = c(var)
      IExpr bShiftM1 = engine.evaluate(F.xreplace(b, var, F.Subtract(var, F.C1)));
      int maxDeg = Math.max(Math.max(degreeOf(a, var, engine), degreeOf(b, var, engine)),
          degreeOf(c, var, engine)) + 2;
      if (maxDeg > 24) {
        maxDeg = 24;
      }
      for (int n = 0; n <= maxDeg; n++) {
        ISymbol[] coeff = new ISymbol[n + 1];
        IASTAppendable xpoly = F.PlusAlloc(n + 1);
        IASTAppendable unknowns = F.ListAlloc(n + 1);
        for (int j = 0; j <= n; j++) {
          coeff[j] = F.Dummy("gosX$" + j);
          xpoly.append(F.Times(coeff[j], F.Power(var, F.ZZ(j))));
          unknowns.append(coeff[j]);
        }
        IExpr xVar = xpoly;
        IExpr xShift = F.xreplace(xVar, var, F.Plus(var, F.C1));
        IExpr equation = F.Subtract(F.Subtract(F.Times(a, xShift), F.Times(bShiftM1, xVar)), c);
        IExpr solution = solveForUnknowns(equation, var, unknowns, engine);
        if (solution.isNIL()) {
          continue;
        }
        IExpr xSolved = substituteAndZero(xVar, solution, coeff, engine);
        if (xSolved.isZero()) {
          continue;
        }
        return engine.evaluate(F.Together(F.Times(bShiftM1, xSolved, F.Power(c, F.CN1), term)));
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /**
   * The term ratio <code>t(var+1)/t(var)</code> of a hypergeometric term as numerator/denominator
   * polynomials <code>{num, den}</code> in <code>var</code>, or <code>null</code> if the term is
   * not hypergeometric. The ratio is built factor-by-factor so that <code>Factorial</code> and
   * polynomial-power factors reduce exactly to rational functions.
   */
  static IExpr[] hypergeometricRatio(IExpr term, ISymbol var, EvalEngine engine) {
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    IExpr ratio = F.C1;
    for (IExpr f : factors) {
      IExpr factorRatio = factorRatio(f, var, engine);
      if (!factorRatio.isPresent()) {
        return null;
      }
      ratio = F.Times(ratio, factorRatio);
    }
    ratio = engine.evaluate(F.Together(ratio));
    IExpr num = engine.evaluate(F.Cancel(F.Numerator(ratio)));
    IExpr den = engine.evaluate(F.Cancel(F.Denominator(ratio)));
    if (den.isZero() || !engine.evalTrue(F.PolynomialQ(num, var))
        || !engine.evalTrue(F.PolynomialQ(den, var))) {
      return null;
    }
    return new IExpr[] {num, den};
  }

  /**
   * Constructs a {@code DifferenceRoot} representation for sums of hypergeometric terms when
   * symbolic upper limits prevent a closed-form Gosper/Geometric evaluation.
   */
  private static IExpr differenceRootFallback(IExpr term, ISymbol var, IExpr from, IExpr to,
      EvalEngine engine) {
    if (from.isInteger()) {

      // Guard: Do not use DifferenceRoot for pure rational functions.
      // Gosper's algorithm and rationalSum() fully handle rational summations. Wrapping a failed
      // rational sum into a DifferenceRoot sequence just creates unsimplifiable noise.
      if (isRationalInVar(term, var, false, engine)) {
        return F.NIL;
      }

      IExpr[] ratio = hypergeometricRatio(term, var, engine);
      if (ratio != null) {
        IExpr ratioNum = ratio[0];
        IExpr ratioDen = ratio[1];

        ISymbol y = F.y;
        ISymbol k = F.n;

        IExpr numK = F.subst(ratioNum, var, k);
        IExpr denK = F.subst(ratioDen, var, k);

        IExpr yK = F.unaryAST1(y, k);
        IExpr yKp1 = F.unaryAST1(y, F.Plus(k, F.C1));
        IExpr yKp2 = F.unaryAST1(y, F.Plus(k, F.C2));

        IExpr term1 = F.Times(denK, yKp2);
        IExpr term2 = F.Times(F.Plus(denK, numK), yKp1);
        IExpr term3 = F.Times(numK, yK);

        IExpr homoEq =
            F.Equal(engine.evaluate(F.Expand(F.Plus(term1, F.Negate(term2), term3))), F.C0);

        int a = from.toIntDefault();
        if (a != Integer.MIN_VALUE) {
          IExpr ic1 = F.Equal(F.unaryAST1(y, F.ZZ(a)), F.C0);
          IExpr f_a = engine.evaluate(F.subst(term, var, F.ZZ(a)));
          IExpr ic2 = F.Equal(F.unaryAST1(y, F.ZZ(a + 1)), f_a);

          IAST fun = F.Function(F.List(y, k), F.List(homoEq, ic1, ic2));

          IExpr dre = engine.evaluate(F.DifferenceRoot(fun));

          if (dre instanceof DifferenceRootExpr) {
            IExpr targetIndex = engine.evaluate(F.Plus(to, F.C1));
            return F.unaryAST1(dre, targetIndex);
          }
        }
      }
    }
    return F.NIL;
  }

  /** The ratio <code>f(var+1)/f(var)</code> of a single factor, or {@link F#NIL}. */
  private static IExpr factorRatio(IExpr f, ISymbol var, EvalEngine engine) {
    if (f.isFree(var, true)) {
      return F.C1;
    }
    // Factorial/Gamma/Binomial factors (also raised to an integer power) reduce exactly to a
    // rational function of var via the shift ratio of their arguments.
    {
      IExpr base = f;
      IExpr expo = F.C1;
      if (f.isPower() && f.exponent().isInteger()) {
        base = f.base();
        expo = f.exponent();
      }
      IExpr special = specialHeadRatio(base, var, engine);
      if (special.isPresent()) {
        return engine.evaluate(F.Power(special, expo));
      }
    }
    if (f.isAST(S.Factorial, 2)) {
      IExpr arg = f.first();
      IExpr[] lin = arg.linear(var);
      if (lin != null) {
        int a = lin[1].toIntDefault();
        if (a >= 1 && a <= 8) {
          // (M+a)!/M! = (M+1)(M+2)...(M+a) with M = arg
          IASTAppendable prod = F.TimesAlloc(a);
          for (int i = 1; i <= a; i++) {
            prod.append(F.Plus(arg, F.ZZ(i)));
          }
          return engine.evaluate(prod);
        }
      }
      return F.NIL;
    }
    if (f.isPower()) {
      IExpr base = f.base();
      IExpr expo = f.exponent();
      if (base.isFree(var, true)) {
        IExpr[] lin = expo.linear(var);
        if (lin != null && !lin[1].isZero()) {
          return engine.evaluate(F.Power(base, lin[1]));
        }
        return F.NIL;
      }
      if (expo.isInteger() && engine.evalTrue(F.PolynomialQ(base, var))) {
        IExpr baseShift = F.xreplace(base, var, F.Plus(var, F.C1));
        return engine.evaluate(F.Power(F.Divide(baseShift, base), expo));
      }
      return F.NIL;
    }
    if (engine.evalTrue(F.PolynomialQ(f, var))) {
      IExpr fShift = F.xreplace(f, var, F.Plus(var, F.C1));
      return engine.evaluate(F.Divide(fShift, f));
    }
    // generic fallback for other hypergeometric factors
    IExpr fShift = F.xreplace(f, var, F.Plus(var, F.C1));
    IExpr r = engine.evaluate(F.Together(F.Simplify(F.Divide(fShift, f))));
    IExpr rn = engine.evaluate(F.Cancel(F.Numerator(r)));
    IExpr rd = engine.evaluate(F.Cancel(F.Denominator(r)));
    if (engine.evalTrue(F.PolynomialQ(rn, var)) && engine.evalTrue(F.PolynomialQ(rd, var))) {
      return r;
    }
    return F.NIL;
  }

  /**
   * The shift ratio <code>g(var+1)/g(var)</code> as a rational function of <code>var</code> for the
   * "hypergeometric" heads <code>Factorial</code>, <code>Gamma</code> and <code>Binomial</code>
   * with arguments that are linear in <code>var</code> with an integer slope, or {@link F#NIL}
   */
  private static IExpr specialHeadRatio(IExpr base, ISymbol var, EvalEngine engine) {
    if (base.isAST(S.Factorial, 2)) {
      IExpr arg = base.first();
      Integer s = shiftSlope(arg, var);
      if (s == null) {
        return F.NIL;
      }
      // arg! = Gamma(arg+1)
      return gammaShiftRatio(F.Plus(arg, F.C1), s, engine);
    }
    if (base.isAST(S.Gamma, 2)) {
      IExpr arg = base.first();
      Integer s = shiftSlope(arg, var);
      if (s == null) {
        return F.NIL;
      }
      return gammaShiftRatio(arg, s, engine);
    }
    if (base.isAST(S.Binomial, 3)) {
      IExpr n = base.first();
      IExpr m = base.second();
      Integer sn = shiftSlope(n, var);
      Integer sm = shiftSlope(m, var);
      if (sn == null || sm == null) {
        return F.NIL;
      }
      // Binomial(n,m) = Gamma(n+1)/(Gamma(m+1)*Gamma(n-m+1))
      IExpr g1 = gammaShiftRatio(F.Plus(n, F.C1), sn, engine);
      IExpr g2 = gammaShiftRatio(F.Plus(m, F.C1), sm, engine);
      IExpr g3 = gammaShiftRatio(F.Plus(F.Subtract(n, m), F.C1), sn - sm, engine);
      return engine.evaluate(F.Times(g1, F.Power(g2, F.CN1), F.Power(g3, F.CN1)));
    }
    return F.NIL;
  }

  /**
   * The integer slope <code>c</code> of a linear argument <code>c*var + d</code>, or
   * <code>null</code> if the argument is not linear with an integer slope in <code>var</code>.
   */
  private static Integer shiftSlope(IExpr arg, ISymbol var) {
    IExpr[] lin = arg.linear(var);
    if (lin == null) {
      return null;
    }
    int s = lin[1].toIntDefault();
    if (s == Integer.MIN_VALUE) {
      return null;
    }
    return Integer.valueOf(s);
  }

  /**
   * <code>Gamma(x+s)/Gamma(x)</code> expanded as a rational function using rising/falling
   * factorials for the integer shift <code>s</code>.
   */
  private static IExpr gammaShiftRatio(IExpr x, int s, EvalEngine engine) {
    if (s == 0) {
      return F.C1;
    }
    IASTAppendable prod = F.TimesAlloc(Math.abs(s));
    if (s > 0) {
      // Gamma(x+s)/Gamma(x) = x*(x+1)*...*(x+s-1)
      for (int i = 0; i < s; i++) {
        prod.append(F.Plus(x, F.ZZ(i)));
      }
      return engine.evaluate(prod);
    }
    // Gamma(x+s)/Gamma(x) = 1/((x-1)*(x-2)*...*(x+s))
    for (int i = 1; i <= -s; i++) {
      prod.append(F.Plus(x, F.ZZ(-i)));
    }
    return engine.evaluate(F.Power(prod, F.CN1));
  }

  /**
   * Solve the polynomial identity <code>equation(var) == 0</code> (which must hold for all
   * <code>var</code>) for the given <code>unknowns</code>. The {@link SolveAlways} builtin is used
   * first; if it solves for free parameters of the summand instead of the requested coefficients
   * the method falls back to an explicit linear system on the coefficients of <code>var</code>.
   * Returns the first solution (a list of rules) or <code>F.NIL</code>.
   */
  private static IExpr solveForUnknowns(IExpr equation, ISymbol var, IAST unknowns,
      EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(equation));

    // 1) SolveAlways(equation == 0, var): values of the parameters making the identity hold for all
    // values of var.
    IExpr solveAlways = engine.evaluate(solveAlways(F.Equal(expanded, F.C0), var));
    if (solveAlways.isList() && solveAlways.size() >= 2 && solveAlways.first().isList()
        && rulesCover(solveAlways.first(), unknowns)) {
      return solveAlways.first();
    }

    // 2) Fallback: set every coefficient of var to zero and solve the linear system for the
    // unknowns explicitly (keeps free summand parameters symbolic).
    IExpr coefficients = engine.evaluate(F.CoefficientList(expanded, var));
    if (!coefficients.isList()) {
      return F.NIL;
    }
    IAST coeffList = (IAST) coefficients;
    IASTAppendable equations = F.ListAlloc(coeffList.argSize());
    for (IExpr cf : coeffList) {
      equations.append(F.Equal(cf, F.C0));
    }
    IExpr solutions = engine.evaluate(F.Solve(equations, unknowns));
    if (solutions.isList() && solutions.size() >= 2 && solutions.first().isList()) {
      return solutions.first();
    }
    return F.NIL;
  }

  /** Build a <code>SolveAlways(equation, var)</code> expression. */
  private static IExpr solveAlways(IExpr equation, ISymbol var) {
    IASTAppendable ast = F.ast(S.SolveAlways);
    ast.append(equation);
    ast.append(var);
    return ast;
  }

  /** Check that the solution <code>ruleList</code> contains a rule for every requested unknown. */
  private static boolean rulesCover(IExpr ruleList, IAST unknowns) {
    if (!ruleList.isList()) {
      return false;
    }
    for (IExpr unknown : unknowns) {
      boolean found = false;
      for (IExpr rule : (IAST) ruleList) {
        if (rule.isRuleAST() && rule.first().equals(unknown)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  /**
   * Apply a solution rule list to <code>template</code>, then set any still unresolved
   * <code>unknowns</code> to zero (the homogeneous freedom).
   */
  private static IExpr substituteAndZero(IExpr template, IExpr solution, ISymbol[] unknowns,
      EvalEngine engine) {
    IExpr substituted = engine.evaluate(F.ReplaceAll(template, solution));
    IASTAppendable zeroRules = F.ListAlloc(unknowns.length);
    for (ISymbol unknown : unknowns) {
      zeroRules.append(F.Rule(unknown, F.C0));
    }
    return engine.evaluate(F.ReplaceAll(substituted, zeroRules));
  }

  /**
   * The (integer) degree of <code>poly</code> in <code>var</code> or a negative number if it cannot
   * be determined.
   */
  private static int degreeOf(IExpr poly, ISymbol var, EvalEngine engine) {
    int d = engine.evaluate(F.Exponent(poly, var)).toIntDefault();
    return d == Integer.MIN_VALUE ? -1 : d;
  }

  /**
   * Non-negative integer roots <code>j</code> (descending, always including <code>0</code>) of the
   * dispersion polynomial <code>poly(h)</code>.
   */
  private static int[] nonNegativeIntegerRoots(IExpr poly, ISymbol h, EvalEngine engine) {
    java.util.TreeSet<Integer> roots = new java.util.TreeSet<>();
    roots.add(0);
    if (!poly.isZero() && !poly.isFree(h, true)) {
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(poly, F.C0), h));
      if (solutions.isList()) {
        for (IExpr s : (IAST) solutions) {
          if (s.isList() && s.size() >= 2 && s.first().isRuleAST()) {
            int value = engine.evaluate(s.first().second()).toIntDefault();
            if (value != Integer.MIN_VALUE && value >= 0) {
              roots.add(value);
            }
          }
        }
      }
    }
    int[] result = new int[roots.size()];
    int index = 0;
    for (Integer value : roots.descendingSet()) {
      result[index++] = value;
    }
    return result;
  }

  /** Evaluate built-in rules and define Attributes for a function. */
  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
    MATCHER1 = Suppliers.memoize(SumRules::init1);
  }
}
