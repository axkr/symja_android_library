package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

/*
 * Copyright 2005-2022 Axel Kramer (axel.kramer@gmail.com)
 *
 * This file is part of Symja.
 *
 * Symja is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Symja is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Symja. If not, see
 * <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.DefaultDict;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.ExprTools;
import org.matheclipse.core.sympy.core.ExprTools.Factors;
import org.matheclipse.core.sympy.core.Operations;
import org.matheclipse.core.sympy.core.Traversal;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.sympy.ntheory.Factor;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.core.visit.VisitorReplaceAll;

/**
 *
 *
 * <pre>
 * TrigSimplify(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * simplifies a trigonometric expression.
 *
 * </blockquote>
 *
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/List_of_trigonometric_identities">List of
 * trigonometric identities</a>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * >> TrigSimplify(Sin(x)^2+Cos(x)^2)
 * 1
 * </pre>
 */
public class TrigSimplifyFu extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  private static class PowerVisitor extends VisitorExpr {
    final Function<IExpr, IExpr> f;
    IExpr result;

    public PowerVisitor(Function<IExpr, IExpr> f) {
      super();
      this.f = f;
      this.result = F.NIL;
    }

    @Override
    public IExpr visitAST(IAST ast) {
      if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (exponent.isInteger() && ((IInteger) exponent).isPositive()) {
          return f.apply(ast);
        }
      }
      return F.NIL;
    }

    public IExpr getResult() {
      return result;
    }
  }

  public TrigSimplifyFu() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
    if (tempAST.isPresent()) {
      return tempAST;
    }

    IExpr assumptionExpr = F.NIL;
    IExpr complexityFunctionHead = F.NIL;
    if (ast.size() > 2) {
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        complexityFunctionHead = options.getOptionAutomatic(S.ComplexityFunction);
      }
      assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
    }
    // arg1 = engine.evaluate(F.ExpandAll(arg1));
    if (assumptionExpr.isPresent()) {
      if (assumptionExpr.isAST()) {
        IAssumptions oldAssumptions = engine.getAssumptions();
        IAssumptions assumptions;
        if (oldAssumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        } else {
          assumptions = oldAssumptions.copy();
          assumptions = assumptions.addAssumption(assumptionExpr);
        }
        if (assumptions != null) {
          try {
            engine.setAssumptions(assumptions);
            return simplifyFu(arg1, complexityFunctionHead, engine);
          } finally {
            engine.setAssumptions(oldAssumptions);
          }
        }
      }
    }

    return simplifyFu(arg1, complexityFunctionHead, engine);
  }

  final static Function<IExpr, IExpr> TR0 = TrigSimplifyFu::tr0;
  final static Function<IExpr, IExpr> TR5 = TrigSimplifyFu::tr5;
  final static Function<IExpr, IExpr> TR6 = TrigSimplifyFu::tr6;
  final static Function<IExpr, IExpr> TR10 = TrigSimplifyFu::tr10;
  final static Function<IExpr, IExpr> TR11 = TrigSimplifyFu::tr11;

  private static IExpr ctr1(IExpr rv, Function<IExpr, Long> measure) {
    // [(TR5, TR0), (TR6, TR0), identity]
    IExpr rv1 = tr0(tr5(rv));
    IExpr rv2 = tr0(tr6(rv));
    if (measure.apply(rv1) < measure.apply(rv)) {
      rv = rv1;
    }
    if (measure.apply(rv2) < measure.apply(rv)) {
      rv = rv2;
    }
    return rv;
  }

  private static IExpr ctr2(IExpr rv, Function<IExpr, Long> measure) {
    IExpr tr11 = tr11(rv);
    IExpr rv0 = tr0(tr11);
    IExpr rv1 = F.eval(ctr1(tr11, measure));
    if (measure.apply(rv0) < measure.apply(tr11)) {
      tr11 = rv0;
    }
    if (measure.apply(rv1) < measure.apply(tr11)) {
      tr11 = rv1;
    }
    return tr11;
  }

  private static IExpr ctr3(IExpr rv, Function<IExpr, Long> measure) {
    // TODO
    IExpr rv1 = tr0(tr8(trMorrie(rv)));
    IExpr rv2 = tr0(tr10i(tr8(trMorrie(rv))));
    if (measure.apply(rv1) < measure.apply(rv)) {
      rv = rv1;
    }
    if (measure.apply(rv2) < measure.apply(rv)) {
      rv = rv2;
    }
    return rv;
  }

  private static IExpr ctr4(IExpr rv, Function<IExpr, Long> measure) {
    IExpr tr4 = tr10i(tr4(rv));
    if (measure.apply(tr4) < measure.apply(rv)) {
      rv = tr4;
    }
    return rv;
  }

  private static IExpr rl1(IExpr rv) {
    return tr0(tr4(tr13(tr4(tr12(tr4(tr3(tr4(rv))))))));
  }

  private static IExpr rl2(IExpr rv, Function<IExpr, Long> measure) {
    IExpr rl21 = tr11(tr3(tr4(tr10(tr3(tr4(rv))))));
    IExpr rl22 = tr4(tr11(tr7(tr5(rv))));
    IExpr rl23 = ctr4(tr9(tr9(tr4(ctr2(tr9(ctr1(ctr3(rv, measure), measure)), measure)))), measure);
    IExpr rl24 = tr10i(rv);
    if (measure.apply(rl24) < measure.apply(rv)) {
      rv = rl24;
    }
    if (measure.apply(rl22) < measure.apply(rv)) {
      rv = rl22;
    }
    if (measure.apply(rl21) < measure.apply(rv)) {
      rv = rl21;
    }
    if (measure.apply(rl23) < measure.apply(rv)) {
      rv = rl23;
    }
    return rv;
  }
  // private static IExpr rl2(IExpr rv, Function<IExpr, Long> measure) {
  // IExpr rl21 = tr11(tr3(tr4(tr10(tr3(tr4(rv))))));
  // IExpr rl22 = tr4(tr11(tr7(tr5(rv))));
  // IExpr rl23 = ctr4(tr9(tr9(tr4(ctr2(tr9(ctr1(ctr3(rv, measure), measure)), measure)))),
  // measure);
  // if (measure.apply(rl22) < measure.apply(rv)) {
  // rv = rl22;
  // }
  // if (measure.apply(rl21) < measure.apply(rv)) {
  // rv = rl21;
  // }
  // if (measure.apply(rl23) < measure.apply(rv)) {
  // rv = rl23;
  // }
  // return rv;
  // }

  /**
   * See: <a href=
   * "https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/simplify/fu.py#L1569">sympy/simplify/fu.py#L1569</a>
   *
   * @param expr
   * @param complexityFunctionHead
   * @param engine
   * @return
   */
  private IExpr simplifyFu(IExpr expr, IExpr complexityFunctionHead, EvalEngine engine) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST was = (IAST) expr;
    Function<IExpr, Long> measure = createComplexityFunction(complexityFunctionHead, engine);

    IExpr rv = tr1(was);
    if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
      IExpr rv1 = rl1(rv);
      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
        rv = tr2(rv);
      }
    }

    if (rv.has(x -> x.isSin() || x.isCos(), true)) {
      IExpr rv1 = rl2(rv, measure);
      IExpr trMorrie = trMorrie(rv1);
      IExpr rv2 = tr8(trMorrie, true);
      if (measure.apply(was) < measure.apply(rv)) {
        rv = was;
      }
      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (measure.apply(rv2) < measure.apply(rv)) {
        rv = rv2;
      }
    }

    IExpr rv3 = tr2i(rv, false);
    if (measure.apply(rv3) < measure.apply(rv)) {
      rv = rv3;
    }
    return rv;
  }

  public static Function<IExpr, Long> createComplexityFunction(IExpr complexityFunctionHead,
      EvalEngine engine) {
    Function<IExpr, Long> complexityFunction = x -> {
      if (x.isIndeterminate() || x.isComplexInfinity()) {
        // Tan(Pi/2) and similar expressions in some sub-steps, create non-wanted results
        return Long.MAX_VALUE;
      }
      return x.leafCountSimplify();
    };
    if (complexityFunctionHead.isPresent()) {
      final IExpr head = complexityFunctionHead;
      complexityFunction = x -> {
        IExpr temp = engine.evaluate(F.unaryAST1(head, x));
        if (temp.isInteger() && !temp.isNegative()) {
          return ((IInteger) temp).toLong();
        }
        return Long.MAX_VALUE;
      };
    }
    return complexityFunction;
  }

  /**
   * Evaluate the expression. if the expression is an IAST factor and then expand the expression.
   *
   * @param expr
   * @return
   */
  private static IExpr tr0(IExpr expr) {
    if (expr.isAST()) {
      IExpr factor = Algebra.factor(expr, EvalEngine.get());
      return F.eval(F.Expand(factor));
    }
    return F.eval(expr);
  }

  public static IExpr tr1(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr1Step(x));
  }

  private static IExpr tr1Step(IExpr expr) {
    if (expr.isAST(S.Sec, 2)) {
      IExpr arg1 = expr.first();
      return F.Power(F.Cos(arg1), F.CN1);
    }
    if (expr.isAST(S.Csc, 2)) {
      IExpr arg1 = expr.first();
      return F.Power(F.Sin(arg1), F.CN1);
    }
    return F.NIL;
  }

  public static IExpr tr2(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr2Step(x));
  }

  /**
   * Replace tan and cot with sin/cos and cos/sin.
   *
   * @param expr
   * @return
   */
  private static IExpr tr2Step(IExpr expr) {
    if (expr.isTan()) {
      IExpr arg1 = expr.first();
      return F.Divide(F.Sin(arg1), F.Cos(arg1));
    }
    if (expr.isAST(S.Cot, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(F.Cos(arg1), F.Sin(arg1));
    }
    return F.NIL;
  }

  private static boolean ok(IExpr k, IExpr e, boolean half) {
    return (e.isIntegerResult() || k.isPositive()) //
        && (k.isCos() || k.isSin())
        || (half && k.isPlus() && (k.argSize() >= 2) && k.indexOf(x -> x.isCos()) > 0);
  }

  public static void factorize(DefaultDict<IExpr> d, IASTAppendable ddone, boolean half) {
    IASTAppendable newk = F.ListAlloc();
    for (IExpr k : d.keySet()) {
      if (k.isPlus()) {
        IExpr knew = half ? S.Factor.of(k) : S.FactorTerms.of(k);
        if (!knew.equals(k)) {
          newk.append(F.List(k, knew));
        }
      }
    }

    if (!newk.isEmpty()) {
      for (int i = 1; i < newk.size(); i++) {
        IAST pair = (IAST) newk.get(i);
        IExpr k = pair.first();
        IExpr knew = pair.second();
        d.remove(k);
        newk.set(i, knew);
      }
      DefaultDict<IExpr> newkPowersDict = newk.setAtClone(0, S.Times).asPowersDict();
      for (IExpr k : newkPowersDict.keySet()) {
        IExpr v = d.getValue(k).plus(newkPowersDict.getValue(k));
        if (ok(k, v, half)) {
          d.put(k, v);
        } else {
          ddone.append(F.List(k, v));
        }
      }
    }
  }

  public static IExpr tr2i(IExpr expr, boolean half) {
    return Traversal.bottomUp(expr, x -> tr2iStep(x, half));
  }

  /**
   * <p>
   * Converts ratios involving sin and cos as follows:
   *
   * <pre>
   *        sin(x)/cos(x) -> tan(x)
   *        sin(x)/(cos(x) + 1) -> tan(x/2) if half=True
   * </pre>
   *
   * @param expr
   * @return
   */
  private static IExpr tr2iStep(IExpr expr, boolean half) {
    if (expr.isTimes()) {
      Pair asNumerDenom = expr.asNumerDenom();
      IExpr numer = asNumerDenom.first();
      IExpr denom = asNumerDenom.second();
      if (numer.isAST() && denom.isAST()) {
        IAST n = (IAST) numer;
        IAST d = (IAST) denom;
        DefaultDict<IExpr> nDict = n.asPowersDict();
        IASTAppendable ndone = F.ListAlloc();

        IASTAppendable toBeRemoved = F.ListAlloc(nDict.size());
        for (IExpr k : nDict.keySet()) {
          IExpr value = nDict.getValue(k);
          if (!ok(k, value, half)) {
            toBeRemoved.append(k);
            ndone.append(F.List(k, value));
          }
        }
        for (int i = 1; i < toBeRemoved.size(); i++) {
          nDict.remove(toBeRemoved.get(i));
        }
        if (nDict.isEmpty()) {
          return F.NIL;
        }

        DefaultDict<IExpr> dDict = d.asPowersDict();
        IASTAppendable ddone = F.ListAlloc();
        toBeRemoved = F.ListAlloc(dDict.size());
        for (IExpr k : dDict.keySet()) {
          IExpr value = dDict.getValue(k);
          if (!ok(k, value, half)) {
            toBeRemoved.append(k);
            ddone.append(F.List(k, value));
          }
        }
        for (int i = 1; i < toBeRemoved.size(); i++) {
          dDict.remove(toBeRemoved.get(i));
        }
        if (dDict.isEmpty()) {
          return F.NIL;
        }

        factorize(nDict, ndone, half);
        factorize(dDict, ddone, half);

        IASTAppendable t = F.ListAlloc();
        for (IExpr k : nDict.keySet()) {
          if (k.isSin()) {
            IExpr a = F.Cos(k.first());
            if (dDict.containsKey(a) && dDict.getValue(a).equals(nDict.getValue(k))) {
              t.append(F.Power(F.Tan(k.first()), nDict.getValue(k)));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            } else if (half) {
              IExpr a1 = a.plus(F.C1);
              if (dDict.containsKey(a1) && dDict.getValue(a1).equals(nDict.getValue(k))) {
                t.append(F.Power(F.Tan(k.first().divide(F.C2)), nDict.getValue(k)));
                nDict.put(k, F.NIL);
                dDict.put(a1, F.NIL);
              }
            }
          } else if (k.isCos()) {
            IExpr a = F.Sin(k.first());
            if (dDict.containsKey(a) && dDict.getValue(a).equals(nDict.getValue(k))) {
              t.append(F.Power(F.Tan(k.first()), nDict.getValue(k).negate()));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            }
          } else if (half && k.isPlus() && k.first().isOne() && k.second().isCos()) {
            IExpr a = F.Sin(k.second().first());
            if (dDict.containsKey(a) && dDict.getValue(a).equals(nDict.getValue(k))
                && (dDict.getValue(a).isIntegerResult() || a.isPositive())) {
              t.append(F.Power(F.Tan(a.first().divide(F.C2)), nDict.getValue(k).negate()));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            }
          }
        }

        if (!t.isEmpty()) {
          // IExpr rv = F.Times();
          IAST testt;
          IASTAppendable mul1 = F.TimesAlloc(nDict.size() + 1);
          mul1.appendArgs(t);
          mul1 =
              nDict.forEach(mul1, (b, e) -> (e.isPresent() && !e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul2 = F.TimesAlloc(dDict.size());
          mul2 =
              dDict.forEach(mul2, (b, e) -> (e.isPresent() && !e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul3 = F.TimesAlloc(ndone.argSize());
          mul3 =
              ndone.forEach(mul3, (b, e) -> (e.isPresent() && !e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul4 = F.TimesAlloc(ddone.argSize());
          mul4 =
              ddone.forEach(mul4, (b, e) -> (e.isPresent() && !e.isZero()) ? F.Power(b, e) : F.NIL);
          return F.Times(F.Divide(mul1.oneIdentity1(), mul2.oneIdentity1()),
              F.Divide(mul3.oneIdentity1(), mul4.oneIdentity1()));
        }
      }
    }
    return F.NIL;
  }

  public static IExpr tr3(IExpr expr) {
    return Traversal.bottomUp(expr, TrigSimplifyFu::tr3Step);
  }

  private static boolean tr3IsPositive(IExpr arg) {
    return AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg).isNIL();
  }

  private static IExpr tr3Step(IExpr expr) {
    if (expr.isAST1()) {
      IExpr arg1 = expr.first();
      int headID = expr.headID();
      if (headID >= 0) {
        IBuiltInSymbol newHead = null;
        switch (headID) {
          case ID.Cos:
            newHead = S.Sin;
            break;
          case ID.Sin:
            newHead = S.Cos;
            break;
          case ID.Cot:
            newHead = S.Tan;
            break;
          case ID.Tan:
            newHead = S.Cot;
            break;
          case ID.Csc:
            newHead = S.Sec;
            break;
          case ID.Sec:
            newHead = S.Csc;
            break;
          default:
            break;
        }
        if (newHead != null) {
          if (tr3IsPositive(arg1.subtract(F.CPiQuarter))
              || tr3IsPositive(F.CPiHalf.subtract(arg1))) {
            return F.unaryAST1(newHead, F.CPiHalf.subtract(arg1));
          }
        }
      }
    }
    return F.NIL;
  }

  private static IExpr tr4(IExpr expr) {
    // special values at 0, pi/6, pi/4, pi/3, pi/2 already handled
    return expr;
  }

  public static IExpr tr5(IExpr expr) {
    return tr5(expr, F.C4, false);
  }

  public static IExpr tr5(IExpr expr, IInteger max, boolean pow) {
    return tr56(expr, S.Sin, S.Cos, x -> F.C1.subtract(x), max, pow);
  }

  /**
   * <p>
   * Replacement of sin^2 with 1 - cos(x)^2.
   *
   * <p>
   * Examples:
   *
   * <pre>
   * >> TR5(sin(x)^2)
   * 1 - cos(x)^2
   * >> TR5(sin(x)^-2)  # unchanged
   * sin(x)^(-2)
   * >> TR5(sin(x)^4)
   * (1 - cos(x)^2)^^ 2
   * </pre>
   *
   * @param expr
   * @return
   */
  // private static IExpr tr5Step(IExpr expr) {
  // if (expr.isPresent()) {
  // if (expr.isPower() && expr.first().isSin() && expr.second().isInteger()) {
  // IAST sinExpr = (IAST) expr.base();
  // IInteger exponent = (IInteger) expr.exponent();
  // if (exponent.isPositive() && exponent.isEven()) {
  // IInteger div2 = exponent.div(F.C2);
  // if (div2.isOne()) {
  // return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))));
  // }
  // return EvalEngine.get()
  // .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))), div2));
  // }
  // }
  // }
  // return F.NIL;
  // }

  public static IExpr tr6(IExpr expr) {
    return tr6(expr, F.C4, false);
  }

  public static IExpr tr6(IExpr expr, IInteger max, boolean pow) {
    return tr56(expr, S.Cos, S.Sin, x -> F.C1.subtract(x), max, pow);
  }

  /**
   * <p>
   * Replacement of cos^2 with 1 - sin(x)^2.
   *
   * <p>
   * Examples:
   *
   * <pre>
   * >> TR6(cos(x)^2)
   * 1 - sin(x)^2
   * >> TR&(cos(x)^-2)  # unchanged
   * cos(x)^(-2)
   * >> TR6(cos(x)^4)
   * (1 - sin(x)^2)^^ 2
   * </pre>
   *
   * @param expr
   * @return
   */
  // private static IExpr tr6Step(IExpr expr) {
  // if (expr.isPower() && expr.first().isCos() && expr.second().isInteger()) {
  // IAST cosExpr = (IAST) expr.base();
  // IInteger exponent = (IInteger) expr.exponent();
  // if (exponent.isPositive() && exponent.isEven()) {
  // IInteger div2 = exponent.div(F.C2);
  // if (div2.isOne()) {
  // return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))));
  // }
  // return EvalEngine.get()
  // .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))), div2));
  // }
  // }
  // return F.NIL;
  // }

  public static IExpr tr56(IExpr expr, IExpr f, IExpr g, Function<IExpr, IExpr> h, IReal max,
      boolean pow) {
    return Traversal.bottomUp(expr, x -> tr56Step(x, f, g, h, max, pow));
  }

  private static IExpr tr56Step(IExpr rv, IExpr f, IExpr g, Function<IExpr, IExpr> h, IReal max,
      boolean pow) {
    if (!rv.isPower() || !rv.base().head().equals(f)) {
      return F.NIL;
    }

    if (!rv.exponent().isReal()) {
      return F.NIL;
    }
    IReal exp = (IReal) rv.exponent();
    if (exp.isNegative()) {
      return F.NIL;
    }
    if (exp.isNegative()) {
      return F.NIL;
    }
    if (exp.isGT(max)) {
      return F.NIL;
    }
    if (exp.isOne()) {
      return F.NIL;
    }
    if (exp.isNumEqualInteger(F.C2)) {
      return h.apply(F.unaryAST1(g, rv.base().first()).pow(2));
    }

    IExpr e;
    if (exp.isInteger() && ((IInteger) exp).isOdd()) {
      e = ((IInteger) exp).iquo(F.C2);
      return F.unaryAST1(f, rv.base().first()).pow(2)
          .times(h.apply(F.unaryAST1(g, rv.base().first()).pow(2)).pow(e));
    } else if (exp.isNumEqualInteger(F.C4)) {
      e = F.C2;
    } else if (!pow) {
      if (exp.isInteger() && ((IInteger) exp).isOdd()) {
        return F.NIL;
      }
      e = ((IInteger) exp).iquo(F.C2);
    } else {
      IExpr p = Factor.perfectPower(exp);
      if (p.isNIL()) {
        return F.NIL;
      }
      e = ((IInteger) exp).iquo(F.C2);
    }
    return h.apply(F.unaryAST1(g, rv.base().first()).pow(2)).pow(e);
  }

  public static IExpr tr7(IExpr expr) {
    return Traversal.bottomUp(expr, TrigSimplifyFu::tr7Step);
  }

  private static IExpr tr7Step(IExpr expr) {
    if (expr.isPower() && expr.base().isCos() && expr.exponent() == F.C2) {
      // 1 + cos(2*rv.base.args[0]))/2
      return F.Plus(F.C1D2, F.Times(F.C1D2, F.Cos(F.Times(2, expr.base().first()))));
    }
    return F.NIL;
  }

  public static IExpr tr8(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr8Step(x, true));
  }

  public static IExpr tr8(IExpr expr, boolean first) {
    return Traversal.bottomUp(expr, x -> tr8Step(x, first));
  }

  /**
   * @param expr
   * @param first TODO
   * @return
   */
  public static IExpr tr8Step(IExpr expr, boolean first) {
    if (expr.isTimes() || (expr.isPower() && (expr.base().isSin() || expr.base().isCos())
        && (expr.exponent().isIntegerResult() || expr.base().isPositive()))) {

      if (first) {
        EvalEngine engine = EvalEngine.get();
        Pair numerDenom = expr.asNumerDenom();
        IExpr n = numerDenom.first();
        IExpr d = numerDenom.second();
        IExpr newn = tr8(TrigReduce.trigReduce(n, engine), false);
        IExpr newd = tr8(TrigReduce.trigReduce(d, engine), false);
        if (!newn.equals(n) || !newd.equals(d)) {
          if (newd.isOne()) {
            return newn;
          }
          IExpr rv = ExprTools.gcdTerms(F.Divide(newn, newd));
          if (rv.isTimes() && rv.first().isRational() && rv.argSize() == 2
              && rv.second().isPlus()) {
            Pair asCoeffMul = rv.asCoeffMul();
            IASTAppendable result = F.TimesAlloc(3);
            result.append(asCoeffMul.first());
            result.appendArgs((IAST) asCoeffMul.second());
            return result;
          }
          return rv;
        }
        return F.NIL;
      }

      DefaultDict<IASTAppendable> args = new DefaultDict<IASTAppendable>();
      args.getValue(S.Cos);
      args.getValue(S.Sin);

      IASTMutable argsList = Operations.makeArgs(S.Times, expr);
      IASTAppendable argsResult = F.TimesAlloc(argsList.argSize() + 5);
      argsList.sortInplace();
      for (int i = 1; i < argsList.size(); i++) {
        IExpr a = argsList.get(i);
        if (a.isCos() || a.isSin()) {
          args.getValue(a.head()).append(a.first());
        } else if (a.isPower() && a.exponent().isIntegerResult() && a.exponent().isPositive()
            && (a.base().isCos() || a.base().isSin())) {
          args.getValue(a.base().head()).append(a.base().first().times(a.exponent()));
        } else {
          argsResult.append(a);
        }
      }
      IASTAppendable c = args.getValue(S.Cos);
      IASTAppendable s = args.getValue(S.Sin);
      if (!(c.argSize() > 1 || s.argSize() > 1)) {
        return F.NIL;
      }

      int n = Math.min(c.argSize(), s.argSize());
      for (int i = 0; i < n; i++) {
        IExpr a1 = s.pop();
        IExpr a2 = c.pop();
        argsResult.append(F.Divide(F.Plus(F.Sin(F.Plus(a1, a2)), F.Sin(F.Subtract(a1, a2))), 2));
      }
      while (c.argSize() > 1) {
        IExpr a1 = c.pop();
        IExpr a2 = c.pop();
        argsResult.append(F.Divide(F.Plus(F.Cos(F.Plus(a1, a2)), F.Cos(F.Subtract(a1, a2))), 2));
      }
      if (c.argSize() > 0) {
        argsResult.append(F.Cos(c.pop()));
      }
      while (s.argSize() > 1) {
        IExpr a1 = s.pop();
        IExpr a2 = s.pop();
        argsResult
            .append(F.Divide(F.Subtract(F.Cos(F.Subtract(a1, a2)), F.Cos(F.Plus(a1, a2))), 2));
      }
      if (s.argSize() > 0) {
        argsResult.append(F.Sin(s.pop()));
      }

      IExpr evalExpandAll = F.evalExpandAll(argsResult);
      return tr8(evalExpandAll, true);
    }
    return F.NIL;
  }

  public static IExpr tr9(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr9Step(x));
  }

  private static IExpr tr9Step(IExpr expr) {
    // TODO implement missing methods
    if (expr.isPlus()) {
      return processCommonAddends((IAST) expr, TrigSimplifyFu::tr9DoIt, null, true);
    }
    return F.NIL;
  }

  public static IExpr tr9DoIt(IExpr rv) {
    // # cos(a)+/-cos(b) can be combined into a product of cosines and
    // # sin(a)+/-sin(b) can be combined into a product of cosine and
    // # sine.
    // #
    // # If there are more than two args, the pairs which "work" will
    // # have a gcd extractable and the remaining two terms will have
    // # the above structure -- all pairs must be checked to find the
    // # ones that work. args that don't have a common set of symbols
    // # are skipped since this doesn't lead to a simpler formula and
    // # also has the arbitrariness of combining, for example, the x
    // # and y term instead of the y and z term in something like
    // # cos(x) + cos(y) + cos(z).
    if (rv.isPlus()) {
      IASTMutable args = ((IAST) rv).copy();
      if (args.argSize() != 2) {
        boolean hit = false;
        for (int i = 1; i < args.size(); i++) {
          IExpr ai = args.get(i);
          if (ai == S.None) {
            continue;
          }
          for (int j = i + 1; j < args.size(); j++) {
            IExpr aj = args.get(j);
            if (aj == S.None) {
              continue;
            }
            IExpr was = ai.plus(aj);
            IExpr newDoIt = tr9DoIt(was);
            if (newDoIt.isPresent() && !newDoIt.equals(was)) {
              args.set(i, newDoIt);
              args.set(j, S.None);
              hit = true;
              break; // go to next i
            }
          }
        }

        if (hit) {
          IASTAppendable plusAST = F.PlusAlloc(args.size());
          for (int i = 1; i < args.size(); i++) {
            IExpr _f = args.get(i);
            if (_f != S.None) {
              plusAST.append(_f);
            }
          }
          rv = plusAST.oneIdentity0();
          if (rv.isPlus()) {
            rv = tr9DoIt(rv).orElse(rv);
          }
          return rv;
        }

        return F.NIL;
      }

      // two args Plus(a1, a2)
      IAST split = trigSplit(args.arg1(), args.arg2());
      if (split.isNIL()) {
        return rv;
      }
      IExpr gcd = split.arg1();
      IExpr n1 = split.arg2();
      IExpr n2 = split.arg3();
      IExpr a = split.get(4);
      IExpr b = split.get(5);
      IExpr isCos = split.get(6);

      // application of rule if possible
      if (isCos.isTrue()) {
        if (n1.equals(n2)) {
          // gcd*n1*2*cos((a + b)/2)*cos((a - b)/2)
          return F.eval(F.Times(F.C2, gcd, n1, F.Cos(F.Times(F.C1D2, F.Plus(a, b))),
              F.Cos(F.Times(F.C1D2, F.Subtract(a, b)))));
        } else if (n1.isNegative()) {
          IExpr temp = a;
          a = b;
          b = temp;
        }
        // -2*gcd*sin((a + b)/2)*sin((a - b)/2)
        return F.eval(F.Times(F.CN2, gcd, F.Sin(F.Times(F.C1D2, F.Plus(a, b))),
            F.Sin(F.Times(F.C1D2, F.Subtract(a, b)))));
      }
      if (n1.equals(n2)) {
        // gcd*n1*2*sin((a + b)/2)*cos((a - b)/2)
        return F.eval(F.Times(F.C2, gcd, n1, F.Sin(F.Times(F.C1D2, F.Plus(a, b))),
            F.Cos(F.Times(F.C1D2, F.Subtract(a, b)))));
      } else if (n1.isNegative()) {
        IExpr temp = a;
        a = b;
        b = temp;
      }
      // 2*gcd*cos((a + b)/2)*sin((a - b)/2)
      return F.eval(F.Times(F.C2, gcd, F.Cos(F.Times(F.C1D2, F.Plus(a, b))),
          F.Sin(F.Times(F.C1D2, F.Subtract(a, b)))));
    }
    return F.NIL;
  }

  public static IAST trigSplit(IExpr a, IExpr b) {
    return trigSplit(a, b, false);
  }

  public static IAST trigSplit(IExpr a, IExpr b, boolean two) {
    // TODO
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/simplify/fu.py#L1716

    Factors aFactors = new Factors(a);
    Factors bFactors = new Factors(b);
    Factors[] normal = aFactors.normal(bFactors);
    Factors ua = normal[0];
    Factors ub = normal[1];
    IExpr gcd = aFactors.gcd(bFactors).asExpr();
    IExpr n1 = F.C1;
    IExpr n2 = F.C1;
    IExpr temp = ua.factorsMap().get(F.CN1);
    if (temp != null) {
      ua = ua.quo(F.CN1);
      n1 = n1.negate();
    } else {
      temp = ub.factorsMap().get(F.CN1);
      if (temp != null) {
        ub = ub.quo(F.CN1);
        n2 = n2.negate();
      }
    }
    a = ua.asExpr();
    b = ub.asExpr();

    // get the parts
    IAST m = powCosSin(a, two);
    if (m.isNIL()) {
      return F.NIL;
    }
    IExpr coa = m.arg1();
    IExpr ca = m.arg2();
    IExpr sa = m.arg3();

    m = powCosSin(b, two);
    if (m.isNIL()) {
      return F.NIL;
    }
    IExpr cob = m.arg1();
    IExpr cb = m.arg2();
    IExpr sb = m.arg3();

    // check them

    if (ca.isNIL() && cb.isPresent() || ca.isSin()) {
      // coa, ca, sa, cob, cb, sb = cob, cb, sb, coa, ca, sa
      temp = coa;
      coa = cob;
      cob = temp;

      temp = ca;
      ca = cb;
      cb = temp;

      temp = sa;
      sa = sb;
      sb = temp;
      // n1, n2 = n2, n1
      temp = n1;
      n1 = n2;
      n2 = temp;
    }

    if (!two) {
      IExpr c = ca.isPresent() ? ca : sa;
      IExpr s = cb.isPresent() ? cb : sb;
      if (!c.isAST(s.head())) {
        return F.NIL;
      }
      return F.List(gcd, n1, n2, c.first(), s.first(), F.booleSymbol(c.isCos()));
    }

    if (coa.isNIL() && cob.isNIL()) {
      if (ca.isPresent() && cb.isPresent() && sa.isPresent() && sb.isPresent()) {
        if (ca.isAST(sa.head()) && !cb.isAST(sb.head())) {
          return F.NIL;
        }
        IASTAppendable args = F.ListAlloc();
        args.appendArgs((IAST) ca);
        args.appendArgs((IAST) sa);
        if (!(((IAST) cb).forAll(x -> args.indexOf(x) > 0)
            && ((IAST) sb).forAll(x -> args.indexOf(x) > 0))) {
          return F.NIL;
        }
        // if not all(i.args in args for i in (cb, sb)):
        // return
        return F.List(gcd, n1, n2, ca.first(), sa.first(), F.booleSymbol(ca.isAST(sa.head())));
      }
    }
    if (ca.isPresent() && sa.isPresent() || cb.isPresent() && sb.isPresent() //
        || two && (ca.isNIL() && sa.isNIL() || cb.isNIL() && sb.isNIL())) {
      return F.NIL;
    }
    IExpr c = ca.isPresent() ? ca : sa;
    IExpr s = cb.isPresent() ? cb : sb;
    if (!c.equalsArgs(s)) {
      return F.NIL;
    }
    if (coa.isNIL()) {
      coa = F.C1;
    }
    if (cob.isNIL()) {
      cob = F.C1;
    }
    if (coa.equals(cob)) {
      gcd = gcd.times(F.CSqrt2);
      return F.List(gcd, n1, n2, c.first(), F.CPiQuarter, S.False);
    }
    temp = coa.divide(cob);
    if (temp.equals(F.CSqrt3)) {
      gcd = gcd.times(F.C2.times(cob));
      return F.List(gcd, n1, n2, c.first(), F.CPiThird, S.False);
    }
    if (temp.equals(F.C1DSqrt3)) {
      gcd = gcd.times(F.C2.times(coa));
      return F.List(gcd, n1, n2, c.first(), F.Times(F.C1D6, S.Pi), S.False);
    }
    return F.NIL;
  }

  private static IAST powCosSin(IExpr a, boolean two) {
    IExpr c = F.NIL;
    IExpr s = F.NIL;
    IExpr co = F.C1;
    if (a.isTimes()) {
      Pair p = a.asCoeffMul();
      co = p.first();
      a = p.second();
      if (a.argSize() > 2 || !two) {
        return F.NIL;
      }
      final IAST timesAST;
      if (a.isTimes()) {
        timesAST = (IAST) a;
        a = timesAST.arg1();
      } else {
        timesAST = F.Times(a);
      }
      if (a.isCos()) {
        c = a;
      } else if (a.isSin()) {
        s = a;
      } else if (a.isPower() //
          && (a.exponent().equals(F.C1D2) || a.exponent().equals(F.CN1D2))) {
        co = co.times(a);
      } else {
        return F.NIL;
      }

      if (timesAST.argSize() > 1) {
        IExpr b = timesAST.arg2();
        if (b.isCos()) {
          if (c.isPresent()) {
            s = b;
          } else {
            c = b;
          }
        } else if (b.isSin()) {
          if (s.isPresent()) {
            c = b;
          } else {
            s = b;
          }
        } else if (b.isPower() //
            && (b.exponent().equals(F.C1D2) || b.exponent().equals(F.CN1D2))) {
          co = co.times(b);
        } else {
          return F.NIL;
        }
        if (!co.isOne()) {
          return F.List(co, c, s);
        }
        return F.List(F.NIL, c, s);
      }
    } else if (a.isCos()) {
      c = a;
    } else if (a.isSin()) {
      s = a;
    }
    if (c.isNIL() && s.isNIL()) {
      return F.NIL;
    }
    if (co.isOne()) {
      co = F.NIL;
    }
    return F.List(co, c, s);
  }

  private static IExpr processCommonAddends(IAST rv, Function<IExpr, IExpr> doIt,
      Function<IExpr, IExpr> key2, boolean key1) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/simplify/fu.py#L1660

    // """Apply ``doIt`` to addends of ``rv`` that (if ``key1=True``) share at least
    // a common absolute value of their coefficient and the value of ``key2`` when
    // applied to the argument. If ``key1`` is False ``key2`` must be supplied and
    // will be the only key applied.
    // """

    // collect by absolute value of coefficient and key2
    DefaultDict<IASTAppendable> absc = new DefaultDict<IASTAppendable>(() -> F.PlusAlloc(8));
    if (key1) {
      for (int i = 1; i < rv.size(); i++) {
        IExpr arg = rv.get(i);
        Pair asCoeffMul = arg.asCoeffMul();
        IExpr c = asCoeffMul.first();
        IExpr a = asCoeffMul.second();
        if (c.isNegative()) {
          // put the sign on `a`
          c = c.negate();
          a = a.negate();
        }
        absc.getValue(F.pair(c, (key2 != null) ? key2.apply(a) : F.C1)).append(a);
      }
    } else if (key2 != null) {
      for (int i = 1; i < rv.size(); i++) {
        IExpr arg = rv.get(i);
        absc.getValue(F.pair(F.C1, key2.apply(arg))).append(arg);
      }
    } else {
      throw new ValueError("must have at least one key");
    }

    IASTAppendable plusAST = F.PlusAlloc(absc.size());
    boolean hit = false;
    for (IExpr k : absc.keySet()) {
      IASTAppendable v = absc.getValue(k);
      IExpr c = k.first();
      // IExpr _c = k;
      if (v.argSize() > 1) {
        IAST e = v;
        IExpr newExpr = doIt.apply(e);
        if (newExpr.isPresent() && !newExpr.equals(e)) {
          e = (IAST) newExpr;
          hit = true;
        }
        plusAST.append(c.times(e));
      } else {
        plusAST.append(c.times(v.first()));
      }
    }

    if (hit) {
      return plusAST.oneIdentity0();
    }

    return F.NIL;
  }

  public static IExpr tr10(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr10Step(x));
  }

  private static IExpr tr10Step(IExpr expr) {
    if ((expr.isSin() || expr.isCos())) {
      IExpr temp = TrigExpand.TRIG_EXPAND_FUNCTION.apply(expr);
      if (temp.isPresent()) {
        IExpr result = temp;
        do {
          temp = Traversal.bottomUpNIL(result, x -> tr10Step(x));
          if (temp.isPresent()) {
            result = temp;
          }
        } while (temp.isPresent());
        return result;
      }
    }
    return F.NIL;
  }

  // Insert this helper near other helpers in the class
  private static IExpr tryCombineIntoCosPlusSin(IAST expr) {
    // Try to match an expression of the shape:
    // sin(a)*(cos(b)-sin(b)) + cos(a)*(sin(b)+cos(b))
    // More generally: sin(a)*(p*Cos(b)+q*Sin(b)) + cos(a)*(r*Cos(b)+s*Sin(b))
    // If coefficients satisfy sc == cs and ss == -cc (see explanation below),
    // rewrite as alpha*Cos(a+b) + beta*Sin(a+b)
    // alpha corresponds to cc, beta corresponds to cs (see derivation).
    // This implementation attempts to collect coefficients for basis:
    // cos(a)*cos(b), cos(a)*sin(b), sin(a)*cos(b), sin(a)*sin(b)

    // flatten sum
    List<IExpr> terms = new ArrayList<>();
    if (expr.isAST(S.Plus)) {
      IAST ast = expr;
      for (int i = 1; i < ast.size(); i++) {
        terms.add(ast.get(i));
      }
    } else {
      terms.add(expr);
    }

    // coefficients initialized to zero
    IExpr cc = F.C0, cs = F.C0, sc = F.C0, ss = F.C0;
    ISymbol symA = null, symB = null;

    // walk terms and try to extract numeric coefficient and trigonometric factors
    for (IExpr t : terms) {
      IExpr coef = F.C1;
      IExpr core = t;
      if (core.isAST(S.Times)) {
        // separate numeric coefficient if present
        IAST times = (IAST) core;
        List<IExpr> nonNum = new ArrayList<>();
        for (int i = 1; i < times.size(); i++) {
          IExpr f = times.get(i);
          if (f.isNumber()) {
            coef = coef.times(f);
          } else {
            nonNum.add(f);
          }
        }
        // rebuild core from non-numeric factors
        if (nonNum.isEmpty()) {
          core = coef; // pure number
        } else if (nonNum.size() == 1) {
          core = nonNum.get(0);
        } else {
          core = F.Times(nonNum.toArray(new IExpr[0]));
        }
      }

      // core should be a product of two trig atoms like Sin(a)*Cos(b) or Cos(a)*Sin(b) etc.
      IExpr f1 = null, f2 = null;
      if (core.isAST(S.Times)) {
        IAST times = (IAST) core;
        if (times.size() == 3) { // head + 2 args
          f1 = times.get(1);
          f2 = times.get(2);
        } else {
          // not matching expected pattern
          return F.NIL;
        }
      } else if (core.isAST()) {
        // single trig atom — not enough to match the 2-variable pattern
        return F.NIL;
      } else {
        return F.NIL;
      }

      // ensure f1 and f2 are Sin or Cos
      if (!(f1.isAST(S.Sin) || f1.isAST(S.Cos)) || !(f2.isAST(S.Sin) || f2.isAST(S.Cos))) {
        return F.NIL;
      }
      if (!(f1.first().isSymbol() || f2.first().isSymbol())) {
        return F.NIL;
      }

      ISymbol aCand = (ISymbol) ((IAST) f1).get(1);
      ISymbol bCand = (ISymbol) ((IAST) f2).get(1);
      // Accept either order: trig(a)*trig(b) or trig(b)*trig(a)
      if (symA == null && symB == null) {
        symA = aCand;
        symB = bCand;
      } else {
        // if order reversed, swap to keep a->first arg and b->second
        if (!aCand.equals(symA) && !aCand.equals(symB)) {
          return F.NIL; // inconsistent symbols
        }
        if (!bCand.equals(symB) && !bCand.equals(symA)) {
          return F.NIL;
        }
      }

      boolean f1IsSin = f1.isSin();
      boolean f1IsCos = f1.isCos();
      boolean f2IsSin = f2.isSin();
      boolean f2IsCos = f2.isCos();

      // Determine which basis term this is: cos(a)*cos(b), cos(a)*sin(b), sin(a)*cos(b),
      // sin(a)*sin(b)
      // We want a to be the 'a' symbol (first) and b to be 'b' (second).
      // If detected swapped, adjust mapping.
      ISymbol detectedA = (ISymbol) ((IAST) f1).get(1);
      ISymbol detectedB = (ISymbol) ((IAST) f2).get(1);
      boolean swapped = false;
      if (!detectedA.equals(symA) && detectedA.equals(symB) && detectedB.equals(symA)) {
        // factors are (trig(b), trig(a)) — swap roles
        swapped = true;
        boolean tmp = f1IsSin;
        f1IsSin = f2IsSin;
        f2IsSin = tmp;
        tmp = f1IsCos;
        f1IsCos = f2IsCos;
      }

      // map to coefficients
      if (f1IsCos && f2IsCos) {
        cc = cc.plus(coef);
      } else if (f1IsCos && f2IsSin) {
        cs = cs.plus(coef);
      } else if (f1IsSin && f2IsCos) {
        sc = sc.plus(coef);
      } else if (f1IsSin && f2IsSin) {
        ss = ss.plus(coef);
      } else {
        return F.NIL;
      }
    } // end for terms

    // Now check if expression can be represented as alpha*Cos(a+b) + beta*Sin(a+b)
    // Expansion gives:
    // alpha*Cos(a+b) + beta*Sin(a+b)
    // => cc = alpha
    // cs = beta
    // sc = beta
    // ss = -alpha
    // So require sc == cs and ss == -cc
    if (sc.equals(cs) && ss.equals(cc.negate())) {
      IExpr alpha = cc; // may be zero
      IExpr beta = cs;
      IExpr a = symA;
      IExpr b = symB;
      IExpr combined =
          F.Plus(F.Times(alpha, F.Cos(F.Plus(a, b))), F.Times(beta, F.Sin(F.Plus(a, b))));
      return combined;
    }

    return F.NIL;
  }

  public static IExpr tr10i(IExpr expr) {
    // if (expr.isPlus()) {
    // IExpr tryCombined = tryCombineIntoCosPlusSin((IAST) expr);
    // if (tryCombined != null && !tryCombined.isNIL()) {
    // return tryCombined;
    // }
    // }
    return Traversal.bottomUp(expr, x -> tr10iStep(x));
  }

  private static IExpr tr10iStep(IExpr rv) {
    if (!rv.isPlus()) {
      return F.NIL;
    }

    rv = processCommonAddends((IAST) rv, //
        TrigSimplifyFu::tr10iDoIt, //
        x -> new VariablesSet(x).getVarList(), //
        true);
    if (rv.isPresent()) {
      while (rv.isPlus()) {
        IAST plusAST = (IAST) rv;
        DefaultDict<IASTAppendable> byrad = new DefaultDict<IASTAppendable>();
        for (int i = 1; i < plusAST.size(); i++) {
          IExpr a = plusAST.get(i);
          boolean hit = false;
          if (a.isTimes()) {
            IAST timesAST = (IAST) a;
            for (int j = 1; j < timesAST.size(); j++) {
              IExpr ai = timesAST.get(j);
              if (ai.isPower() && ai.exponent().equals(F.C1D2) && ai.base().isInteger()) {
                byrad.getValue(ai).append(a);
                hit = true;
                break;
              }
            }
          }
          if (!hit) {
            byrad.getValue(F.C1).append(a);
          }
        }

        // no need to check all pairs -- just check for the ones
        // that have the right ratio
        IASTAppendable args = F.PlusAlloc(byrad.size());
        Iterator<Entry<IExpr, IExpr>> it = byrad.entrySet().iterator();
        while (it.hasNext()) {
          Entry<IExpr, IExpr> a = it.next();
          IExpr k = a.getKey();
          IASTAppendable aVal = (IASTAppendable) a.getValue();
          IExpr testKey = F.CSqrt3.times(k);
          IASTAppendable b = byrad.getNull(testKey);
          if (b != null) {
            tr10iStepRoots(aVal, b, testKey, args);
          }
          testKey = F.C1DSqrt3;
          b = byrad.getNull(testKey);
          if (b != null) {
            tr10iStepRoots(aVal, b, testKey, args);
          }
        }
        if (args.argSize() > 0) {
          IASTAppendable res = F.PlusAlloc(args.argSize() + byrad.size());
          res.appendArgs(args);
          for (IASTAppendable v : byrad.values()) {
            for (int i = 1; i < v.size(); i++) {
              IExpr f = v.get(i);
              if (f != S.None) {
                res.append(f);
              }
            }
          }
          return res;
        } else {
          return tr10iDoIt(plusAST).orElse(plusAST);
        }
      }
      return rv;
    }

    return F.NIL;
  }

  private static void tr10iStepRoots(IASTAppendable aVal, IASTAppendable bVal, IExpr b,
      IASTAppendable args) {
    if (bVal.argSize() > 0) {
      for (int i = 1; i < aVal.size(); i++) {
        IExpr ai = aVal.get(i);
        if (ai == S.None) {
          continue;
        }
        for (int j = 1; j < bVal.size(); j++) {
          IExpr bj = bVal.get(j);
          if (bj == S.None) {
            continue;
          }
          IExpr was = F.Plus(ai, bj);
          IExpr newExpr = tr10iDoIt(was);
          if (newExpr.isPresent() && !newExpr.equals(was)) {
            args.append(newExpr);
            aVal.set(i, S.None);
            bVal.set(j, S.None);
            break;
          }
        }
      }
    }
  }

  public static IExpr tr10iDoIt(IExpr rv) {
    return tr10iDoIt(rv, false);
  }

  public static IExpr tr10iDoIt(IExpr expr, boolean first) {
    if (!expr.isPlus()) {
      if (expr.isTan() || expr.isAST(S.Cot, 2)) {
        IExpr arg = expr.first();
        if (arg.isPlus() && arg.argSize() == 2) {
          IExpr a = arg.first();
          IExpr b = arg.second();
          if (expr.isTan()) {
            return F.Divide(F.Plus(F.Tan(a), F.Tan(b)),
                F.Subtract(F.C1, F.Times(F.Tan(a), F.Tan(b))));
          } else { // Cot
            return F.Divide(F.Subtract(F.Times(F.Cot(a), F.Cot(b)), F.C1),
                F.Plus(F.Cot(a), F.Cot(b)));
          }
        }
      }
      return F.NIL;
    }
    IASTMutable rv = ((IAST) expr).copy();
    if (rv.argSize() != 2) {
      boolean hit = false;
      for (int i = 1; i < rv.size(); i++) {
        IExpr ai = rv.get(i);
        if (ai == S.None) {
          continue;
        }
        for (int j = i + 1; j < rv.size(); j++) {
          IExpr aj = rv.get(j);
          if (aj == S.None) {
            continue;
          }
          IExpr was = ai.plus(aj);
          IExpr newExpr = tr10iDoIt(was);
          if (newExpr.isPresent() && !newExpr.equals(was)) {
            rv.set(i, newExpr);
            rv.set(j, S.None);
            hit = true;
            break;
          }
        }
      }
      IExpr res = F.NIL;
      if (hit) {
        rv = rv.remove(x -> x == S.None);
        if (rv.isPlus()) {
          res = tr10iDoIt(rv).orElse(rv);
        } else {
          res = rv;
        }
      }
      return F.eval(res);
    }

    // two-arg Plus
    IAST split = trigSplit(rv.arg1(), rv.arg2(), true);
    if (split.isNIL()) {
      return F.NIL;
    }
    IExpr gcd = split.arg1();
    IExpr n1 = split.arg2();
    IExpr n2 = split.arg3();
    IExpr a = split.get(4);
    IExpr b = split.get(5);
    IExpr same = split.get(6);

    // identify and get c1 to be cos then apply rule if possible
    if (same.isTrue()) {
      // coscos, sinsin
      gcd = n1.times(gcd);
      if (n1.equals(n2)) {
        return gcd.times(F.Cos(a.subtract(b)));
      }
      return gcd.times(F.Cos(a.plus(b)));
    } else {
      // cossin, cossin
      gcd = n1.times(gcd);
      if (n1.equals(n2)) {
        return gcd.times(F.Sin(a.plus(b)));
      }
      return gcd.times(F.Sin(b.subtract(a)));
    }
  }

  public static IExpr tr11(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr11Step(x));
  }

  private static IExpr tr11Step(IExpr expr) {
    if ((expr.isSin() || expr.isCos()) && expr.first().isTimes()) {
      IPair pair = expr.first().asCoeffMul();
      if (pair.isAST2() && pair.first().isInteger()) {
        IInteger n = (IInteger) pair.first();
        IExpr x = pair.second();
        if (n.isPositive() && n.isGT(F.C1)) {
          IExpr result = F.NIL;
          if (expr.isSin()) {
            if (n.isEven()) {
              IInteger nHalf = n.div(F.C2);
              IExpr sinCosArg = nHalf.times(x);
              result = F.Times(F.C2, F.Sin(sinCosArg), F.Cos(sinCosArg));
            } else { // odd
              if (n.equals(F.C3)) {
                result = F.Plus(F.Times(F.C3, F.Sin(x)), F.Times(F.CN4, F.Power(F.Sin(x), F.C3)));
              } else {
                IInteger nMinus1Half = n.subtract(F.C1).div(F.C2);
                IInteger nPlus1Half = n.add(F.C1).div(F.C2);
                result =
                    F.Plus(F.Times(F.C2, F.Sin(nMinus1Half.times(x)), F.Cos(nPlus1Half.times(x))),
                        F.Sin(x));
              }
            }
          } else { // Cos
            if (n.isEven()) {
              IInteger nHalf = n.div(F.C2);
              IExpr sinArg = nHalf.times(x);
              result = F.Subtract(F.C1, F.Times(F.C2, F.Power(F.Sin(sinArg), F.C2)));
            } else { // odd
              if (n.equals(F.C3)) {
                result = F.Plus(F.Times(F.CN3, F.Cos(x)), F.Times(F.C4, F.Power(F.Cos(x), F.C3)));
              } else {
                IInteger nMinus1Half = n.subtract(F.C1).div(F.C2);
                IInteger nPlus1Half = n.add(F.C1).div(F.C2);
                result = F.Subtract(
                    F.Times(F.C2, F.Cos(nMinus1Half.times(x)), F.Cos(nPlus1Half.times(x))),
                    F.Cos(x));
              }
            }
          }

          if (result.isPresent()) {
            IExpr temp;
            do {
              temp = Traversal.bottomUpNIL(result, y -> tr11Step(y));
              if (temp.isPresent()) {
                result = temp;
              }
            } while (temp.isPresent());
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  public static IExpr tr12(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr12Step(x));
  }

  private static IExpr tr12Step(IExpr rv) {
    if (rv.isTan() || rv.isAST(S.Cot, 2)) {
      IExpr arg = rv.first();
      if (arg.isPlus()) {
        IAST plus = (IAST) arg;
        IExpr a = plus.arg1();
        IExpr b = plus.rest().oneIdentity0();
        final IExpr tb, ta;
        if (rv.isTan()) {
          ta = F.Tan(a);
          tb = b.isPlus() ? tr12Step(F.Tan(b)) : F.Tan(b);
          // (tan(a) + tb)/(1 - tan(a)*tb)
          return F.Divide(F.Plus(ta, tb), F.Subtract(F.C1, F.Times(ta, tb)));
        } else { // Cot
          ta = F.Cot(a);
          tb = b.isPlus() ? tr12Step(F.Cot(b)) : F.Cot(b);
          // (ta*tb-1)/(ta+tb)
          return F.Divide(F.Subtract(F.Times(ta, tb), F.C1), F.Plus(ta, tb));
        }
      }
    }
    return F.NIL;
  }

  public static IExpr tr13(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr13Step(x));
  }

  private static IExpr tr13Step(IExpr rv) {
    if (rv.isTimes()) {
      IAST times = (IAST) rv;
      DefaultDict<IASTAppendable> args = new DefaultDict<IASTAppendable>();
      args.put(S.None, F.TimesAlloc(times.argSize()));
      for (int i = 1; i < times.size(); i++) {
        IExpr a = times.get(i);
        if (a.isTan() || a.isAST(S.Cot, 2)) {
          args.getValue(a.head()).append(a.first());
        } else {
          args.getValue(S.None).append(a);
        }
      }
      IASTAppendable t = args.getValue(S.Tan);
      IASTAppendable c = args.getValue(S.Cot);
      if (t.argSize() < 2 && c.argSize() < 2) {
        return F.NIL;
      }
      IASTAppendable timesResult = args.getValue(S.None);
      while (t.argSize() > 1) {
        IExpr t1 = t.pop();
        IExpr t2 = t.pop();
        // 1 - (tan(t1)/tan(t1 + t2) + tan(t2)/tan(t1 + t2))
        IExpr t1Pt2 = F.Tan(F.Plus(t1, t2));
        timesResult.append(
            F.Subtract(F.C1, F.Plus(F.Divide(F.Tan(t1), t1Pt2), F.Divide(F.Tan(t2), t1Pt2))));
      }
      if (t.argSize() > 0) {
        timesResult.append(F.Tan(t.pop()));
      }
      while (c.argSize() > 1) {
        IExpr t1 = c.pop();
        IExpr t2 = c.pop();
        // 1 + cot(t1)*cot(t1 + t2) + cot(t2)*cot(t1 + t2)
        IExpr t1Pt2 = F.Cot(F.Plus(t1, t2));
        timesResult.append(F.Plus(F.C1, F.Times(F.Cot(t1), t1Pt2), F.Times(F.Cot(t2), t1Pt2)));
      }
      if (c.argSize() > 0) {
        timesResult.append(F.Cot(c.pop()));
      }
      return timesResult.oneIdentity0();
    }
    return F.NIL;
  }

  public static IExpr tr14(IExpr expr) {
    return tr14(expr, false);
  }

  public static IExpr tr14(IExpr expr, boolean first) {
    return Traversal.bottomUp(expr, x -> tr14Step(x, first));
  }

  private static IExpr tr14Step(IExpr rv, boolean first) {
    if (!rv.isTimes()) {
      return F.NIL;
    }
    if (first) {
      Pair p = rv.asNumerDenom();
      IExpr n = p.first();
      IExpr d = p.second();
      if (!d.isOne()) {
        IExpr newn = tr14(n, false);
        IExpr newd = tr14(d, false);
        if (newn.isPresent() && newd.isPresent() //
            && (!newn.equals(n) || !newd.equals(d))) {
          return newn.divide(newd);
        }
        return F.NIL;
      }
    }

    IAST timesAST = (IAST) rv;
    IASTAppendable other = F.TimesAlloc(16);
    IASTAppendable process = F.ListAlloc();
    IExpr g, t, f, si, a, e;

    for (int i = 1; i < timesAST.size(); i++) {
      a = timesAST.get(i);
      IPair p = a.asBaseExp();
      IExpr b = p.first();
      e = p.second();
      if (!e.isInteger() || b.isPositive()) {
        other.append(a);
        continue;
      }
      a = b;

      IAST m = asFSign1(a);
      if (m.isNIL() //
          || (!m.arg2().isCos() && !m.arg2().isSin())) {
        if (e.isOne()) {
          other.append(a);
        } else {
          other.append(F.Power(a, e));
        }
        continue;
      }
      g = m.arg1();
      f = m.arg2();
      si = m.arg3();
      process.append(F.List(g, F.booleSymbol(e.isNumber()), e, f, si, a));
    }
    // sort them to get like terms next to each other
    process.sortInplace();

    int nother = other.argSize();

    int gn = 1, en = 3, fn = 4, sn = 5, an = 6;

    while (process.argSize() > 0) {
      IAST A = (IAST) process.remove(1);
      if (process.argSize() > 0) {
        IAST B = (IAST) process.first();
        if (A.get(en).isReal() && B.get(en).isReal()) {
          IReal aen = (IReal) A.get(en);
          if (A.get(fn).equals(B.get(fn))) {
            if (!A.get(sn).equals(B.get(sn))) {
              B = (IAST) process.remove(1);
              IReal ben = (IReal) B.get(en);
              IReal take = aen.min(ben);
              // reinsert any remainder
              IASTMutable rem;
              if (!ben.equals(take)) {
                rem = B.copy();
                rem.set(en, rem.get(en).subtract(take));
                process.append(1, rem);
              } else if (!aen.equals(take)) {
                rem = A.copy();
                rem.set(en, rem.get(en).subtract(take));
                process.append(1, rem);
              }

              t = A.get(fn).isCos() ? S.Sin : S.Cos;
              IAST tFunction = F.unaryAST1(t, A.get(fn).first());
              other.append(
                  F.Power(F.Times(A.get(gn).negate(), B.get(gn), F.Power(tFunction, F.C2)), take));
              continue;
            }
          }
        } else if (A.get(en).equals(B.get(en))) {
          // both exponents are equal symbols
          if (A.get(fn).equals(B.get(fn))) {
            if (!A.get(sn).equals(B.get(sn))) {
              B = (IAST) process.remove(1);
              IExpr take = A.get(en);
              t = A.get(fn).isCos() ? S.Sin : S.Cos;
              IAST tFunction = F.unaryAST1(t, A.get(fn).first());
              other.append(
                  F.Power(F.Times(A.get(gn).negate(), B.get(gn), F.Power(tFunction, F.C2)), take));
              continue;
            }
          }
        }
      }
      other.append(F.Power(A.get(an), A.get(en)));
    }
    if (other.argSize() != nother) {
      return other;
    }
    return F.NIL;
  }

  public static IExpr tr15(IExpr expr) {
    return tr15(expr, false);
  }

  public static IExpr tr15(IExpr expr, boolean pow) {
    return Traversal.bottomUp(expr, x -> tr15Step(x, pow));
  }

  private static IExpr tr15Step(IExpr rv, boolean pow) {
    if (!rv.isPower()) {
      return F.NIL;
    }
    IExpr b = rv.base();
    IExpr e = rv.exponent();
    if (e.isInteger() && ((IInteger) e).isOdd()) {
      IExpr step = tr15Step(F.Power(b, e.plus(F.C1)), pow);
      if (step.isPresent()) {
        return F.Divide(step, b);
      }
    }

    IExpr बदला = osborne(rv);
    if (बदला.isPresent() && !बदला.equals(rv)) {
      return बदला;
    }
    return F.NIL;
  }

  private static IExpr osborne(IExpr expr) {
    VisitorReplaceAll vra = new VisitorReplaceAll(x -> {
      if (x.isSin())
        return F.Times(F.CI, F.Sinh(F.Times(F.CNI, x.first())));
      if (x.isCos())
        return F.Cosh(F.Times(F.CNI, x.first()));
      return F.NIL;
    });
    IExpr rv = expr.accept(vra);
    if (rv.isPresent()) {
      vra = new VisitorReplaceAll(x -> {
        if (x.isSinh())
          return F.Times(F.CNI, F.Sin(F.Times(F.CI, x.first())));
        if (x.isCosh())
          return F.Cos(F.Times(F.CI, x.first()));
        return F.NIL;
      });
      rv = rv.accept(vra);
      if (rv.isPresent()) {
        return rv;
      }
    }
    return expr;
  }

  private static IAST asFSign1(IExpr e) {
    if (!e.isPlus() || e.argSize() != 2) {
      return F.NIL;
    }
    IExpr a = e.first();
    IExpr b = e.second();

    if (a.isOne() || a.isMinusOne()) {
      IExpr g = F.C1;
      if (b.isTimes() && b.first().isNumber() && b.first().isNegative()) {
        a = a.negate();
        b = b.negate();
        g = g.negate();
      }
      return F.List(g, b, a);
    }

    IPair pa = a.asCoeffMul();
    IPair pb = b.asCoeffMul();
    IExpr ca = pa.first();
    IExpr ma = pa.second();
    IExpr cb = pb.first();
    IExpr mb = pb.second();

    if (ma.equals(F.C1)) {
      IExpr temp = ma;
      ma = mb;
      mb = temp;
      temp = ca;
      ca = cb;
      cb = temp;
    }

    if (mb.isOne() && ca instanceof IInteger && cb instanceof IInteger) {
      IInteger gcd = ((IInteger) ca).gcd((IInteger) cb);
      IExpr g = gcd;
      IExpr s1 = ca.divide(g);
      IExpr s2 = cb.divide(g);
      if (s1.isNegative()) {
        g = g.negate();
        s1 = s1.negate();
        s2 = s2.negate();
      }
      if (s1.isOne()) {
        return F.List(g, ma, s2);
      }
    }
    return F.NIL;
  }

  public static IExpr trMorrie(IExpr rv) {
    return Traversal.bottomUp(rv, x -> trMorrieStep(x, true));
  }

  /**
   * @param expr
   * @return
   */
  public static IExpr trMorrieStep(IExpr rv, boolean first) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/simplify/fu.py#L1104
    if (!rv.isTimes()) {
      return F.NIL;
    }
    if (first) {
      Pair numerDenom = rv.asNumerDenom();
      IExpr numer = trMorrieStep(numerDenom.first(), false).orElse(numerDenom.first());
      IExpr denom = trMorrieStep(numerDenom.second(), false).orElse(numerDenom.second());
      return F.Divide(numer, denom);
    }

    IAST times = (IAST) rv;
    IASTAppendable other = F.ListAlloc(times.size());
    Map<IExpr, IExpr> coss = new HashMap<IExpr, IExpr>();
    DefaultDict<IASTAppendable> args = new DefaultDict<IASTAppendable>();
    for (int i = 1; i < times.size(); i++) {
      IExpr c = times.get(i);
      Pair asBaseExp = c.asBaseExp();
      IExpr b = asBaseExp.first();
      IExpr e = asBaseExp.second();
      if (b.isCos() && e.isInteger()) {
        Pair asCoeffMul = b.first().asCoeffMul(first);
        IExpr co = asCoeffMul.first();
        IExpr a = asCoeffMul.second();
        args.getValue(a).append(co);
        coss.put(b, e);
      } else {
        other.append(c);
      }
    }

    IASTAppendable result = F.ListAlloc(times.size());
    for (IExpr a : args.keySet()) {
      IASTAppendable c = args.getValue(a);
      c.sortInplace();
      while (!c.isEmpty()) {
        int k = 0;
        IExpr cc = c.arg1();
        IExpr ci = cc;
        for (int i = 1; i < c.size(); i++) {
          if (cc.equals(c.get(i))) {
            k += 1;
            cc = cc.multiply(2);
          }
        }
        if (k > 1) {
          // sin(2**k*ci*a)/2**k/sin(ci*a)
          IExpr newarg = F.Divide(F.Sin(F.Times(F.Power(F.C2, k), ci, a)),
              F.Times(F.Power(F.C2, k), F.Sin(F.Times(ci, a))));
          IExpr take = F.NIL;
          IASTAppendable ccs = F.ListAlloc();
          for (int i = 0; i < k; i++) {
            cc = cc.divide(F.C2);
            IExpr key = F.Cos(a.times(cc));
            ccs.append(cc);
            take = min(coss.get(key), take.isPresent() ? take : coss.get(key));
          }
          // update exponent counts
          for (int i = 0; i < k; i++) {
            cc = ccs.pop();
            IExpr key = F.Cos(a.times(cc));
            coss.put(key, coss.get(key).minus(take));
            if (coss.get(key).isZero()) {
              c.remove(cc);
            }
          }
          result.append(take.isOne() ? newarg : F.Power(newarg, take));
        } else {
          IExpr pop0 = c.remove(1);
          IExpr b = F.Cos(pop0.times(a));
          IExpr exponent = coss.get(b);
          other.append(exponent.isOne() ? b : F.Power(b, exponent));
        }
      }
    }
    if (!result.isEmpty()) {
      IASTAppendable listOfCos = F.ListAlloc(args.size());
      for (IExpr a : args.keySet()) {
        IASTAppendable list = args.getValue(a);
        for (int i = 1; i < list.size(); i++) {
          listOfCos.append(F.Cos(F.Times(list.get(i), a)));
        }
      }
      IASTAppendable joinedTimes =
          F.TimesAlloc(result.argSize() + other.argSize() + listOfCos.argSize());
      joinedTimes.appendArgs(result);
      joinedTimes.appendArgs(other);
      joinedTimes.appendArgs(listOfCos);
      return F.eval(joinedTimes);
    }
    return F.NIL;
  }

  private static IExpr min(IExpr a, IExpr b) {
    if (F.Less(a, b).isTrue()) {
      return a;
    }
    return b;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        F.list(F.Rule(S.Assumptions, S.$Assumptions), //
            F.Rule(S.ComplexityFunction, S.Automatic)));
  }
}
