package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
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

public class Reduce extends AbstractEvaluator {
  // Internal signal to indicate successful absorption into the variable interval
  private static final ISymbol REDUCE_CONTINUE = F.Dummy("$Continue");

  static class ReduceComparison {

    final IExpr variable;
    final Map<IExpr, IExpr> domainMap;

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
        IAST intersection = IntervalDataSym.union(intervalData, cd.intervalData, EvalEngine.get());
        if (intersection.isPresent()) {
          this.intervalData = intersection;
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

    public ReduceComparison(IExpr variable, Map<IExpr, IExpr> domainMap) {
      this.variable = variable;
      this.domainMap = domainMap;
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
          // TODO check Simplify result
          IExpr temp = arg;
          if (!arg.isAST2() || !arg.first().equals(variable)) {
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
     * @return {@link #REDUCE_CONTINUE}, if all condition terms could be reduced (evaluated) in <code>
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
      for (int i = 1; i < andExpr.size(); i++) {
        IExpr rewritten = rewriteVariableValue(variableInterval, andExpr.get(i));
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
        return S.True;
      }

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
          }
        }
      }

      // If everything cleanly absorbed into the VariableInterval, signal CONTINUE
      // so reduceAndOr will substitute the accumulated interval bounds.
      if (variableInternalContinued) {
        return REDUCE_CONTINUE;
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

    private IExpr rewriteVariableValue(VariableInterval cd, IExpr lastArg) {
      if (lastArg.isEqual()) {
        IAST[] reduced =
            Eliminate.eliminateOneVariable(F.list(lastArg), cd.variable, false, false, EvalEngine.get());
        if (reduced != null && reduced[0].isEmptyList() && reduced[1].isRule()) {
          IAST rule = reduced[1];
          lastArg = F.Equal(variable, rule.second());
        }
      } else if (lastArg.isAST2() && lastArg.isComparatorFunction()) {
        EvalEngine engine = EvalEngine.get();
        // Expand to polynomial form: diff = lhs - rhs
        IExpr diff = engine.evaluate(F.ExpandAll(F.Subtract(lastArg.first(), lastArg.second())));

        if (diff.isPolynomial(cd.variable)) {
          IExpr coeffs = S.CoefficientList.ofNIL(engine, diff, cd.variable);

          if (coeffs != null && coeffs.isList()) {
            IAST coeffList = (IAST) coeffs;

            if (coeffList.argSize() == 2) {
              IExpr a0 = coeffList.arg1();
              IExpr a1 = coeffList.arg2();

              if (a1.isNumericFunction()) {
                IExpr sign = engine.evaluate(F.Sign(a1));

                if (sign.isOne() || sign.isMinusOne()) {
                  IExpr root = engine.evaluate(F.Divide(F.Negate(a0), a1));
                  IExpr head = lastArg.head();

                  if (head.isBuiltInSymbol()) {
                    IBuiltInSymbol sym = (IBuiltInSymbol) head;

                    // Flip the comparator operator if divided by a negative coefficient
                    if (sign.isMinusOne()) {
                      switch (sym.ordinal()) {
                        case ID.Less:
                          sym = S.Greater;
                          break;
                        case ID.LessEqual:
                          sym = S.GreaterEqual;
                          break;
                        case ID.Greater:
                          sym = S.Less;
                          break;
                        case ID.GreaterEqual:
                          sym = S.LessEqual;
                          break;
                      }
                    }

                    IASTAppendable result = F.ast(sym, 3);
                    result.append(cd.variable);
                    result.append(root);
                    return result;
                  }
                }
              }
            } else if (coeffList.argSize() == 3) {
              // Quadratic inequality logic: a2*x^2 + a1*x + a0 OP 0
              IExpr a0 = coeffList.arg1();
              IExpr a1 = coeffList.arg2();
              IExpr a2 = coeffList.arg3();

              if (a2.isNumericFunction() && a1.isNumericFunction() && a0.isNumericFunction()) {
                IExpr delta = engine.evaluate(F.Subtract(F.Sqr(a1), F.Times(F.C4, a2, a0)));
                IExpr isNegDelta = engine.evaluate(F.Less(delta, F.C0));

                if (isNegDelta.isTrue()) {
                  IExpr a2Sign = engine.evaluate(F.Sign(a2));
                  boolean isPosPoly = a2Sign.isOne();
                  IExpr head = lastArg.head();
                  if (head == S.Less || head == S.LessEqual)
                    return isPosPoly ? S.False : S.True;
                  if (head == S.Greater || head == S.GreaterEqual)
                    return isPosPoly ? S.True : S.False;
                }

                IExpr isPosDelta = engine.evaluate(F.Greater(delta, F.C0));
                if (isPosDelta.isTrue()) {
                  IExpr sqrtDelta = engine.evaluate(F.Sqrt(delta));
                  IExpr r1 = engine
                      .evaluate(F.Divide(F.Subtract(F.Negate(a1), sqrtDelta), F.Times(F.C2, a2)));
                  IExpr r2 =
                      engine.evaluate(F.Divide(F.Plus(F.Negate(a1), sqrtDelta), F.Times(F.C2, a2)));

                  // Ensure r1 < r2
                  IExpr r1LessR2 = engine.evaluate(F.Less(r1, r2));
                  if (r1LessR2.isFalse()) {
                    IExpr tmp = r1;
                    r1 = r2;
                    r2 = tmp;
                  }

                  IExpr a2Sign = engine.evaluate(F.Sign(a2));
                  boolean isPosPoly = a2Sign.isOne();
                  IExpr head = lastArg.head();

                  if (isPosPoly) {
                    if (head == S.Less)
                      return F.And(F.Greater(cd.variable, r1), F.Less(cd.variable, r2));
                    if (head == S.LessEqual)
                      return F.And(F.GreaterEqual(cd.variable, r1), F.LessEqual(cd.variable, r2));
                    if (head == S.Greater)
                      return F.Or(F.Less(cd.variable, r1), F.Greater(cd.variable, r2));
                    if (head == S.GreaterEqual)
                      return F.Or(F.LessEqual(cd.variable, r1), F.GreaterEqual(cd.variable, r2));
                  } else {
                    if (head == S.Greater)
                      return F.And(F.Greater(cd.variable, r1), F.Less(cd.variable, r2));
                    if (head == S.GreaterEqual)
                      return F.And(F.GreaterEqual(cd.variable, r1), F.LessEqual(cd.variable, r2));
                    if (head == S.Less)
                      return F.Or(F.Less(cd.variable, r1), F.Greater(cd.variable, r2));
                    if (head == S.LessEqual)
                      return F.Or(F.LessEqual(cd.variable, r1), F.GreaterEqual(cd.variable, r2));
                  }
                }

                IExpr isZeroDelta = engine.evaluate(F.Equal(delta, F.C0));
                if (isZeroDelta.isTrue()) {
                  IExpr r = engine.evaluate(F.Divide(F.Negate(a1), F.Times(F.C2, a2)));
                  IExpr a2Sign = engine.evaluate(F.Sign(a2));
                  boolean isPosPoly = a2Sign.isOne();
                  IExpr head = lastArg.head();

                  if (isPosPoly) {
                    if (head == S.Less)
                      return S.False;
                    if (head == S.LessEqual)
                      return F.Equal(cd.variable, r);
                    if (head == S.Greater)
                      return F.Unequal(cd.variable, r);
                    if (head == S.GreaterEqual)
                      return S.True;
                  } else {
                    if (head == S.Greater)
                      return S.False;
                    if (head == S.GreaterEqual)
                      return F.Equal(cd.variable, r);
                    if (head == S.Less)
                      return F.Unequal(cd.variable, r);
                    if (head == S.LessEqual)
                      return S.True;
                  }
                }
              }
            }
          }
        }

        // lastArg = S.Simplify.of(lastArg, cd.variable);

        // Post-simplify fallback for symmetric even-power inequalities (e.g. x^4 < 16)
        if (lastArg.isAST2() && lastArg.isComparatorFunction()) {
          IExpr lhs = lastArg.first();
          IExpr rhs = lastArg.second();

          if (lhs.isPower() && lhs.first().equals(cd.variable)) {
            IExpr exponent = lhs.second();
            if (exponent.isInteger() && exponent.isEvenResult()
                && ((IInteger) exponent).isPositiveResult()) {
              IExpr root = engine.evaluate(F.Power(rhs, F.Divide(F.C1, exponent)));

              IExpr isNeg = engine.evaluate(F.Less(rhs, F.C0));
              if (isNeg.isTrue()) {
                if (lastArg.isFunctionID(ID.Less, ID.LessEqual))
                  return S.False;
                if (lastArg.isFunctionID(ID.Greater, ID.GreaterEqual))
                  return S.True;
              }

              IExpr isZero = engine.evaluate(F.Equal(rhs, F.C0));
              if (isZero.isTrue()) {
                if (lastArg.isFunctionID(ID.Less))
                  return S.False;
                if (lastArg.isFunctionID(ID.LessEqual))
                  return F.Equal(cd.variable, F.C0);
                if (lastArg.isFunctionID(ID.Greater))
                  return F.Unequal(cd.variable, F.C0);
                if (lastArg.isFunctionID(ID.GreaterEqual))
                  return S.True;
              }

              IExpr isPos = engine.evaluate(F.Greater(rhs, F.C0));
              if (isPos.isTrue()) {
                if (lastArg.isFunctionID(ID.Less)) {
                  return F.And(F.Greater(cd.variable, F.Negate(root)), F.Less(cd.variable, root));
                }
                if (lastArg.isFunctionID(ID.LessEqual)) {
                  return F.And(F.GreaterEqual(cd.variable, F.Negate(root)),
                      F.LessEqual(cd.variable, root));
                }
                if (lastArg.isFunctionID(ID.Greater)) {
                  return F.Or(F.Less(cd.variable, F.Negate(root)), F.Greater(cd.variable, root));
                }
                if (lastArg.isFunctionID(ID.GreaterEqual)) {
                  return F.Or(F.LessEqual(cd.variable, F.Negate(root)),
                      F.GreaterEqual(cd.variable, root));
                }
              }
            }
          }
        }
      } else {
        // lastArg = S.Simplify.of(lastArg, cd.variable);
      }
      return lastArg;
    }

    private IExpr reduceAndBinary(IExpr arg, IExpr orArg) {
      ReduceComparison rcAnd = new ReduceComparison(variable, domainMap);
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
    if (!temp.isList() || ((IAST) temp).argSize() < 3) {
      // degree < 2 needs no case analysis here
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
    if (f.isFree(variable) || f.isPolynomial(variable)) {
      return F.NIL;
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
        return F.NIL;
      }
    }
    if (variableTerm.isNIL()) {
      return F.NIL;
    }
    IExpr rest = restParts.oneIdentity0();

    // parse variableTerm as amplitude * periodicHead(innerArg)
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
          return F.NIL;
        }
      }
      amplitude = constParts.oneIdentity1();
      periodicFunction = functionCandidate;
    }
    if (!periodicFunction.isAST1() || !periodicFunction.head().isBuiltInSymbol()) {
      return F.NIL;
    }
    IBuiltInSymbol periodicHead = (IBuiltInSymbol) periodicFunction.head();
    if (!isForwardPeriodicFunction(periodicHead.ordinal())) {
      return F.NIL;
    }
    IExpr innerArg = periodicFunction.first();

    // the inner argument must be linear in the variable: c1*variable + c0
    IExpr[] linear = innerArg.linear(variable);
    if (linear == null) {
      return F.NIL;
    }
    IExpr c0 = linear[0];
    IExpr c1 = linear[1];
    if (c1.isZero()) {
      return F.NIL;
    }

    // periodicHead(innerArg) == -rest/amplitude
    IExpr rhsValue = engine.evaluate(F.Divide(F.Negate(rest), amplitude));
    IExpr branches = InverseFunctionExpander.expandPeriodicInverse(periodicHead, rhsValue);
    if (branches.isNIL()) {
      return F.NIL;
    }
    IAST branchList = branches.isList() ? (IAST) branches : F.List(branches);
    // WMA Reduce lists the principal inverse branch first (e.g. ArcSin before
    // Pi-ArcSin). Symja's shared expander lists the Pi-shifted branch first for these functions,
    // so reverse the two branches here (Solve keeps the expander's original order).
    if (branchList.argSize() == 2 && isPiShiftedFirstFunction(periodicHead.ordinal())) {
      branchList = F.List(branchList.arg2(), branchList.arg1());
    }

    Set<IExpr> integerConditions = new LinkedHashSet<IExpr>();
    IASTAppendable orEqualities = F.OrAlloc(branchList.size());
    for (int i = 1; i < branchList.size(); i++) {
      IExpr branch = branchList.get(i);
      if (!branch.isConditionalExpression()) {
        return F.NIL;
      }
      IExpr value = branch.first();
      IExpr condition = branch.second();
      if (!collectIntegerConditions(condition, integerConditions)) {
        // not a periodic (C(k) integer) branch
        return F.NIL;
      }
      // solve c1*variable + c0 == value => variable == (value - c0)/c1
      IExpr root = engine.evaluate(F.Divide(F.Subtract(value, c0), c1));
      orEqualities.append(F.Equal(variable, root));
    }
    if (integerConditions.isEmpty() || orEqualities.isAST0()) {
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
    IExpr genericPart = genericAnd;

    if (c1MayBeZero) {
      // degenerate case c1==0: the inner argument collapses to the constant c0
      IExpr degenerateEquation = engine
          .evaluate(F.Equal(F.Plus(F.Times(amplitude, F.unaryAST1(periodicHead, c0)), rest), F.C0));
      IExpr degeneratePart;
      if (degenerateEquation.isTrue()) {
        degeneratePart = F.Equal(c1, F.C0);
      } else if (degenerateEquation.isFalse()) {
        degeneratePart = S.False;
      } else {
        degeneratePart = F.And(F.Equal(c1, F.C0), degenerateEquation);
      }
      if (!degeneratePart.isFalse()) {
        return engine.evaluate(F.Or(degeneratePart, genericPart));
      }
    }
    return engine.evaluate(genericPart);
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
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isTrue() || arg1.isFalse()) {
      return arg1;
    }

    // handle quantifiers ForAll / Exists
    if (arg1.isAST(S.ForAll) || arg1.isAST(S.Exists)) {
      IExpr quantified = reduceQuantifier((IAST) arg1, engine);
      if (quantified.isPresent()) {
        return quantified;
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

    if (ast.isAST2() || ast.isAST3()) {
      vars = ast.arg2().makeList();
    } else {
      // compute the variables from the (Element-stripped) condition
      VariablesSet eVar = new VariablesSet(arg1);
      vars = eVar.getVarList();
    }

    if (domain != S.Reals && domain != S.Complexes && domain != S.Integers && domain != S.Primes
        && domain != S.Booleans) {
      return F.NIL;
    }
    try {
      if (domain == S.Booleans) {
        return reduceBooleans(arg1, engine).orElse(F.NIL);
      }
      if (domain == S.Integers || domain == S.Primes) {
        // stays unevaluated (F.NIL) if no integer/Diophantine method applies
        return reduceIntegers(arg1, vars, domain, engine);
      }

      if (!vars.isList1()) {
        IExpr multivariate = reduceMultivariate(arg1, vars, engine);
        if (multivariate.isPresent()) {
          return multivariate;
        }
        // `1`.
        return Errors.printMessage(S.Reduce, "error",
            F.list(F.stringx("Reduce is only implemented to reduce one variable")), engine);
      }

      final IExpr variable = vars.arg1();
      IExpr expr = arg1;

      if (expr.isEqual() && domain == S.Complexes) {
        // case analysis for univariate polynomial equations with parametric coefficients,
        // e.g. a*x^2+b*x+c==0 for variable x with parameters a,b,c
        IExpr parametric = reduceParametricPolynomialEquation((IAST) expr, variable, engine);
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
        IExpr solved = reducePolynomialInequalityReals(arg1, variable, engine);
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

      if (logicalExpand.isAST(S.And)) {
        IAST andAST = (IAST) logicalExpand;
        IASTMutable andResult = andAST.copy();
        for (int i = 1; i < andAST.size(); i++) {
          IExpr arg = andAST.get(i);
          if (arg.isEqual()) {
            IExpr roots = S.Roots.ofNIL(engine, arg, variable);
            if (roots.isPresent()) {
              andResult.set(i, roots);
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

      ReduceComparison rc = new ReduceComparison(variable, domainMap);
      // may throw ArgumentTypeException
      IExpr reduced = rc.evaluate(logicalExpand);
      return reduced.orElse(logicalExpand);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      rex.printStackTrace();
    }
    return F.NIL;
  }

  /** Maximum number of enumerated solutions for bounded integer/Diophantine problems. */
  private static final int MAX_DIOPHANTINE_RESULTS = 20;

  /** Maximum number of integer points enumerated within a bounded interval. */
  private static final int MAX_INTEGER_INTERVAL = 1000;

  /**
   * Reduce a {@link S#ForAll} or {@link S#Exists} quantified expression over the {@link S#Reals}.
   *
   * @param quant the quantifier AST (<code>ForAll</code> or <code>Exists</code>)
   * @param engine the evaluation engine
   * @return {@link S#True}, {@link S#False}, or {@link F#NIL} if the quantifier cannot be decided
   */
  private static IExpr reduceQuantifier(IAST quant, EvalEngine engine) {
    final boolean forAll = quant.isAST(S.ForAll);
    if (!quant.isAST2() && !quant.isAST3()) {
      return F.NIL;
    }
    IAST boundVars = quant.arg1().makeList();
    IExpr condition;
    if (quant.isAST3()) {
      // ForAll(vars, cond, expr) => cond => expr ; Exists(vars, cond, expr) => cond && expr
      condition = forAll ? F.Implies(quant.arg2(), quant.arg3()) //
          : F.And(quant.arg2(), quant.arg3());
    } else {
      condition = quant.arg2();
    }
    IExpr reduced = engine.evaluate(F.Reduce(condition, boundVars, S.Reals));
    if (reduced.isTrue()) {
      return S.True;
    }
    if (reduced.isFalse()) {
      return S.False;
    }
    if (forAll) {
      // holds for every value iff the reduced solution set is the whole domain
      return isFullDomain(reduced, boundVars) ? S.True : F.NIL;
    }
    // Exists: a concrete, non-False solution set was found
    return reduced.isFree(S.Reduce) ? S.True : F.NIL;
  }

  /**
   * Test whether a reduced solution set covers the whole domain (i.e. the quantified condition
   * holds for every value of the bound variables).
   */
  private static boolean isFullDomain(IExpr reduced, IAST vars) {
    if (reduced.isTrue()) {
      return true;
    }
    if (reduced.isAST(S.Element, 3)) {
      IExpr dom = reduced.second();
      return (dom == S.Reals || dom == S.Complexes) && vars.contains(reduced.first());
    }
    if (reduced.isAnd()) {
      IAST and = (IAST) reduced;
      for (int i = 1; i < and.size(); i++) {
        if (!isFullDomain(and.get(i), vars)) {
          return false;
        }
      }
      return true;
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
    if (vars.isList1()) {
      return reduceIntegersUnivariate(expr, vars.arg1(), domain, engine);
    }
    if (vars.isList2() && expr.isEqual()) {
      return reduceIntegersTwoVar((IAST) expr, vars, domain, engine);
    }
    return F.NIL;
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
      if (value.isInteger()) {
        if (domain == S.Primes) {
          if (((IInteger) value).isProbablePrime()) {
            result.append(F.Equal(variable, value));
          }
        } else {
          result.append(F.Equal(variable, value));
        }
      }
    }
  }

  /**
   * Enumerate the integer (or prime) points of a bounded real solution set. Supports single
   * equalities and two-sided bounded intervals given as an {@link S#And} of comparators.
   *
   * @return an {@link S#Or} of equalities, {@link S#False} if empty, or {@link F#NIL} if the range
   *         is unbounded or not enumerable
   */
  private static IExpr enumerateIntegerInterval(IExpr realReduced, IExpr variable, ISymbol domain,
      EvalEngine engine) {
    if (realReduced.isEqual()) {
      IASTAppendable single = F.OrAlloc(1);
      appendIfDomainValue(realReduced, variable, domain, single, engine);
      if (single.isAST0()) {
        return S.False;
      }
      return single.isAST1() ? single.arg1() : single;
    }
    IAST comparators;
    if (realReduced.isAnd()) {
      comparators = (IAST) realReduced;
    } else if (realReduced.isComparatorFunction()) {
      comparators = F.And(realReduced);
    } else {
      return F.NIL;
    }
    IExpr lower = F.NIL;
    IExpr upper = F.NIL;
    boolean lowerStrict = false;
    boolean upperStrict = false;
    for (int i = 1; i < comparators.size(); i++) {
      IExpr c = comparators.get(i);
      if (!c.isAST2() || !c.first().equals(variable)) {
        return F.NIL;
      }
      IExpr bound = c.second();
      switch (c.headID()) {
        case ID.Greater:
          lower = bound;
          lowerStrict = true;
          break;
        case ID.GreaterEqual:
          lower = bound;
          lowerStrict = false;
          break;
        case ID.Less:
          upper = bound;
          upperStrict = true;
          break;
        case ID.LessEqual:
          upper = bound;
          upperStrict = false;
          break;
        default:
          return F.NIL;
      }
    }
    if (lower.isNIL() || upper.isNIL()) {
      // unbounded range - not enumerable
      return F.NIL;
    }
    int lo = ceilBound(engine.evaluate(lower), lowerStrict, engine);
    int hi = floorBound(engine.evaluate(upper), upperStrict, engine);
    if (lo == Integer.MIN_VALUE || hi == Integer.MIN_VALUE
        || (long) hi - (long) lo > MAX_INTEGER_INTERVAL) {
      return F.NIL;
    }
    IASTAppendable result = F.OrAlloc(Math.max(4, hi - lo + 1));
    for (int k = lo; k <= hi; k++) {
      IInteger candidate = F.ZZ(k);
      if (domain == S.Primes && !candidate.isProbablePrime()) {
        continue;
      }
      result.append(F.Equal(variable, candidate));
    }
    if (result.isAST0()) {
      return S.False;
    }
    return result.isAST1() ? result.arg1() : result;
  }

  /**
   * Smallest integer satisfying the lower bound, or {@link Integer#MIN_VALUE} if not determined.
   */
  private static int ceilBound(IExpr value, boolean strict, EvalEngine engine) {
    if (!value.isReal()) {
      return Integer.MIN_VALUE;
    }
    IExpr ceil = engine.evaluate(F.Ceiling(value));
    int c = ceil.toIntDefault();
    if (c == Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    // strict lower bound "x > a" with integer a excludes a itself
    return (strict && value.isInteger()) ? c + 1 : c;
  }

  /** Largest integer satisfying the upper bound, or {@link Integer#MIN_VALUE} if not determined. */
  private static int floorBound(IExpr value, boolean strict, EvalEngine engine) {
    if (!value.isReal()) {
      return Integer.MIN_VALUE;
    }
    IExpr floor = engine.evaluate(F.Floor(value));
    int f = floor.toIntDefault();
    if (f == Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    // strict upper bound "x < b" with integer b excludes b itself
    return (strict && value.isInteger()) ? f - 1 : f;
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
            && !(value.isInteger() && ((IInteger) value).isProbablePrime())) {
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
   * Reduce a multivariate equation system over {@link S#Reals}/{@link S#Complexes} by successively
   * eliminating variables. Only fully determined equation systems are handled; anything else
   * returns {@link F#NIL} so the caller can leave the expression unevaluated.
   */
  private static IExpr reduceMultivariate(IExpr arg1, IAST vars, EvalEngine engine) {
    IAST equationList;
    if (arg1.isList()) {
      equationList = (IAST) arg1;
    } else if (arg1.isAnd()) {
      equationList = ((IAST) arg1).setAtCopy(0, S.List);
    } else if (arg1.isEqual()) {
      equationList = F.list(arg1);
    } else {
      return F.NIL;
    }
    for (int i = 1; i < equationList.size(); i++) {
      if (!equationList.get(i).isEqual()) {
        return F.NIL;
      }
    }
    return reduceEquationSystem(equationList, vars, engine);
  }

  /**
   * Recursively solve a determined equation system by eliminating the first variable and
   * back-substituting the solution of the remaining variables.
   */
  private static IExpr reduceEquationSystem(IAST equations, IAST vars, EvalEngine engine) {
    if (vars.isList1()) {
      if (equations.argSize() == 1 && equations.arg1().isEqual()) {
        IExpr roots = S.Roots.ofNIL(engine, equations.arg1(), vars.arg1());
        if (roots.isPresent() && roots.isFree(S.Roots)) {
          return roots;
        }
      }
      return F.NIL;
    }
    if (equations.argSize() < vars.argSize()) {
      // underdetermined system - not handled here
      return F.NIL;
    }
    IExpr firstVar = vars.arg1();
    IAST[] eliminated = Eliminate.eliminateOneVariable(equations, firstVar, false, false, engine);
    if (eliminated == null || eliminated[1] == null || !eliminated[1].isRule()) {
      return F.NIL;
    }
    IAST remainingEquations = eliminated[0];
    IAST rule = eliminated[1];
    IAST remainingVars = vars.removeAtCopy(1);
    IExpr rest = reduceEquationSystem(remainingEquations, remainingVars, engine);
    if (rest.isNIL()) {
      return F.NIL;
    }
    List<IAST> substRules = new ArrayList<IAST>();
    if (!extractEqualityRules(rest, substRules)) {
      return F.NIL;
    }
    IExpr value = rule.second();
    if (!substRules.isEmpty()) {
      IASTAppendable substList = F.ListAlloc(substRules.size());
      for (IAST substRule : substRules) {
        substList.append(substRule);
      }
      value = engine.evaluate(F.subst(value, substList));
    }
    IASTAppendable result = F.ast(S.And, remainingVars.size() + 1);
    result.append(F.Equal(firstVar, value));
    if (rest.isAnd()) {
      result.appendArgs((IAST) rest);
    } else {
      result.append(rest);
    }
    return result;
  }

  /**
   * Collect <code>symbol == value</code> equalities from an expression (a single equality or an
   * {@link S#And} of equalities) into <code>rules</code> as <code>symbol -&gt; value</code> rules.
   *
   * @return <code>true</code> if the expression consists only of such equalities
   */
  private static boolean extractEqualityRules(IExpr expr, List<IAST> rules) {
    if (expr.isEqual()) {
      if (expr.first().isSymbol()) {
        rules.add(F.Rule(expr.first(), expr.second()));
        return true;
      }
      return false;
    }
    if (expr.isAnd()) {
      IAST and = (IAST) expr;
      for (int i = 1; i < and.size(); i++) {
        if (!extractEqualityRules(and.get(i), rules)) {
          return false;
        }
      }
      return true;
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
    if (f.isFree(variable) || !f.isPolynomial(variable)) {
      return F.NIL;
    }

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
    }
    return F.NIL;
  }

  /**
   * Reduce a single univariate polynomial inequality <code>lhs OP rhs</code> (with
   * <code>OP</code> one of {@link S#Less}, {@link S#LessEqual}, {@link S#Greater},
   * {@link S#GreaterEqual}) over the {@link S#Reals} by a sign analysis of the polynomial
   * <code>f = lhs - rhs</code>. The sign of a real polynomial can only change at its real roots, so
   * the real line is split at the distinct real roots and the sign of <code>f</code> is sampled in
   * each open region. The satisfied regions (and, for the non-strict relations, the roots
   * themselves) are merged into a maximal {@link S#IntervalData} set and returned as the
   * corresponding {@link S#Or} of comparators. For example <code>4*x^3-4*x&gt;0</code> reduces to
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
   *         real roots of <code>f</code> could not be determined numerically
   */
  private static IExpr reducePolynomialInequalityReals(IExpr arg1, IExpr variable,
      EvalEngine engine) {
    int headID = arg1.headID();
    if (headID != ID.Less && headID != ID.LessEqual && headID != ID.Greater
        && headID != ID.GreaterEqual) {
      return F.NIL;
    }
    IAST comparator = (IAST) arg1;
    if (comparator.argSize() != 2) {
      return F.NIL;
    }
    // f(root) == 0 satisfies only the non-strict relations `<=` / `>=`
    final boolean rootInSolution = headID == ID.LessEqual || headID == ID.GreaterEqual;
    final boolean greaterDirection = headID == ID.Greater || headID == ID.GreaterEqual;

    // rewrite `lhs OP rhs` as `f OP 0` with f = lhs - rhs
    IExpr f = engine.evaluate(F.ExpandAll(F.Subtract(comparator.arg1(), comparator.arg2())));
    if (f.isFree(variable) || !f.isPolynomial(variable)) {
      return F.NIL;
    }

    // the sign of f can only change at its distinct real roots
    IExpr solved = S.Solve.of(engine, F.Equal(f, F.C0), variable, S.Reals);
    if (!solved.isList() || !solved.isFree(S.Solve)) {
      return F.NIL;
    }
    IAST solutions = (IAST) solved;
    List<IExpr> rootExacts = new ArrayList<IExpr>(solutions.argSize());
    List<Double> rootValues = new ArrayList<Double>(solutions.argSize());
    for (int i = 1; i < solutions.size(); i++) {
      IExpr solution = solutions.get(i);
      if (!solution.isList1() || !solution.first().isRule()
          || !solution.first().first().equals(variable)) {
        return F.NIL;
      }
      IExpr rootValue = engine.evaluate(solution.first().second());
      double d;
      try {
        d = rootValue.evalDouble();
      } catch (ArgumentTypeException aex) {
        return F.NIL;
      }
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        return F.NIL;
      }
      insertSortedDistinct(rootExacts, rootValues, rootValue, d);
    }

    final int n = rootExacts.size();
    // sign of f on each of the n+1 open regions between/around the roots
    boolean[] regionInSolution = new boolean[n + 1];
    for (int k = 0; k <= n; k++) {
      final double sample;
      if (n == 0) {
        sample = 0.0;
      } else if (k == 0) {
        sample = rootValues.get(0) - 1.0;
      } else if (k == n) {
        sample = rootValues.get(n - 1) + 1.0;
      } else {
        sample = (rootValues.get(k - 1) + rootValues.get(k)) / 2.0;
      }
      int sign = polynomialSignAt(f, variable, sample, engine);
      if (sign == 0) {
        return F.NIL;
      }
      regionInSolution[k] = greaterDirection ? sign > 0 : sign < 0;
    }

    // merge the satisfied regions (and included roots) into maximal intervals. The atoms are
    // traversed left to right: even index t == region t/2, odd index t == root (t-1)/2.
    IASTAppendable intervalData = F.IntervalDataAlloc(n + 2);
    boolean open = false;
    IExpr min = F.NIL;
    IExpr minType = S.Less;
    IExpr max = F.NIL;
    IExpr maxType = S.Less;
    for (int t = 0; t <= 2 * n; t++) {
      final boolean isRegion = (t & 1) == 0;
      final int index = t / 2;
      final boolean inSolution = isRegion ? regionInSolution[index] : rootInSolution;
      final IExpr leftValue;
      final IExpr rightValue;
      final IBuiltInSymbol edgeType;
      if (isRegion) {
        leftValue = index == 0 ? F.CNInfinity : rootExacts.get(index - 1);
        rightValue = index == n ? F.CInfinity : rootExacts.get(index);
        edgeType = S.Less;
      } else {
        leftValue = rootExacts.get(index);
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
   * Insert <code>value</code>/<code>numericValue</code> into the parallel lists
   * <code>exacts</code>/<code>values</code> keeping them sorted ascending by numeric value and
   * skipping numeric duplicates.
   */
  private static void insertSortedDistinct(List<IExpr> exacts, List<Double> values, IExpr value,
      double numericValue) {
    int pos = 0;
    while (pos < values.size() && values.get(pos).doubleValue() < numericValue) {
      pos++;
    }
    if (pos < values.size() && values.get(pos).doubleValue() == numericValue) {
      // duplicate real root - keep the first representation
      return;
    }
    exacts.add(pos, value);
    values.add(pos, Double.valueOf(numericValue));
  }

  /**
   * Numerically evaluate the sign of the polynomial <code>f</code> at <code>variable == sample</code>
   * .
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

      // Handle chained comparators like Less(0, x, 2)
      if (ast.isComparatorFunction() && ast.argSize() > 2) {
        IASTAppendable andAST = F.AndAlloc(ast.argSize());
        for (int i = 1; i < ast.size() - 1; i++) {
          andAST.append(F.binaryAST2(ast.head(), ast.get(i), ast.get(i + 1)));
        }
        return andAST;
      }
      // Handle mixed inequalities like Inequality(0, Less, x, LessEqual, 2)
      else if (ast.isAST(S.Inequality)) {
        IASTAppendable andAST = F.AndAlloc(ast.argSize() / 2);
        for (int i = 1; i < ast.size() - 2; i += 2) {
          andAST.append(F.binaryAST2(ast.get(i + 1), ast.get(i), ast.get(i + 2)));
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

  /** {@inheritDoc} */

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    //
  }
}
