package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.builtin.SimplifyFunctions;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.sympy.DefaultDict;
import org.matheclipse.core.expression.sympy.ExprTools;
import org.matheclipse.core.expression.sympy.Operations;
import org.matheclipse.core.expression.sympy.Traversal;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See:
 * <ul>
 * <li><a href=
 * "https://github.com/sympy/sympy/blob/master/sympy/simplify/fu.py">sympy/simplify/fu.py</a>
 * <li><a href= "https://www.sciencedirect.com/science/article/pii/S0895717706001609">Automated and
 * readable simplification of trigonometric expressions (Hongguang Fu, XiuqinZhong, Zhenbing
 * Zeng)</a>
 * </ul>
 */
public class TrigSimplifyFu extends AbstractFunctionEvaluator {

  private static class Chain implements Function<IExpr, IExpr> {
    Function<IExpr, IExpr>[] alternative1;
    Function<IExpr, IExpr>[] alternative2;
    Function<IExpr, Long> complexityFunction;
    EvalEngine engine;

    public Chain(Function<IExpr, IExpr>[] alternative1, Function<IExpr, IExpr>[] alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      this.alternative1 = alternative1;
      this.alternative2 = alternative2;
    }

    public Chain(Function<IExpr, IExpr> alternative1, Function<IExpr, IExpr>[] alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      Function<IExpr, IExpr>[] f1 = new Function[1];
      f1[0] = alternative1;
      this.alternative1 = f1;
      this.alternative2 = alternative2;;
    }

    public Chain(Function<IExpr, IExpr>[] alternative1, Function<IExpr, IExpr> alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      this.alternative1 = alternative1;
      Function<IExpr, IExpr>[] f2 = new Function[1];
      f2[0] = alternative2;
      this.alternative2 = f2;
    }

    @Override
    public IExpr apply(IExpr expr) {
      SimplifyFunctions.SimplifiedResult simplifiedResult =
          new SimplifyFunctions.SimplifiedResult(F.NIL, expr, complexityFunction);

      for (int i = 0; i < alternative1.length; i++) {
        IExpr temp = expr.replaceAll(alternative1[i]);
        if (temp.isPresent()) {
          temp = engine.evaluate(F.evalExpandAll(temp));
          if (simplifiedResult.checkLess(temp)) {
          }
        }
      }
      if (simplifiedResult.getResult().isPresent()) {
        expr = simplifiedResult.getResult();
      }
      for (int i = 0; i < alternative2.length; i++) {
        IExpr temp = expr.replaceAll(alternative2[i]);
        if (temp.isPresent()) {
          temp = engine.evaluate(temp);
          simplifiedResult.checkLess(temp);
        }
      }

      return simplifiedResult.getResult();

    }
  }

  public TrigSimplifyFu() {}


  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IAST tempAST = StructureFunctions.threadListLogicEquationOperators(arg1, ast, 1);
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
   * Evaluate the expression. if th expression is an IAST factor and then expand the expression.
   * 
   * @param expr
   * @return
   */
  private static IExpr tr0(IExpr expr) {
    if (expr.isAST()) {
      return F.eval(F.Expand(F.Factor(expr)));
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
    return (e.isIntegerResult() || k.isPositive())//
        && (k.isCos() || k.isSin())
        || (half && k.isPlus() && (k.argSize() >= 2) && ((IAST) k).indexOf(x -> x.isCos()) > 0);
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
        IExpr v = d.get(k).plus(newkPowersDict.get(k));
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
        sin(x)/cos(x) -> tan(x)
        sin(x)/(cos(x) + 1) -> tan(x/2) if half=True
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
          IExpr value = nDict.get(k);
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
          IExpr value = dDict.get(k);
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
            if (dDict.containsKey(a) && dDict.get(a).equals(nDict.get(k))) {
              t.append(F.Power(F.Tan(k.first()), nDict.get(k)));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            } else if (half) {
              IExpr a1 = a.plus(F.C1);
              if (dDict.containsKey(a1) && dDict.get(a1).equals(nDict.get(k))) {
                t.append(F.Power(F.Tan(k.first().divide(F.C2)), nDict.get(k)));
                nDict.put(k, F.NIL);
                dDict.put(a1, F.NIL);
              }
            }
          } else if (k.isCos()) {
            IExpr a = F.Sin(k.first());
            if (dDict.containsKey(a) && dDict.get(a).equals(nDict.get(k))) {
              t.append(F.Power(F.Tan(k.first()), nDict.get(k).negate()));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            }
          } else if (half && k.isPlus() && k.first().isOne() && k.second().isCos()) {
            IExpr a = F.Sin(k.second().first());
            if (dDict.containsKey(a) && dDict.get(a).equals(nDict.get(k))
                && (dDict.get(a).isIntegerResult() || a.isPositive())) {
              t.append(F.Power(F.Tan(a.first().divide(F.C2)), nDict.get(k).negate()));
              nDict.put(k, F.NIL);
              dDict.put(a, F.NIL);
            }
          }
        }

        if (!t.isEmpty()) {
          IExpr rv = F.Times();
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
    return !AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg).isPresent();
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
    return Traversal.bottomUp(expr, TrigSimplifyFu::tr5Step);
  }

  /**
   * <p>
   * Replacement of sin^2 with 1 - cos(x)^2.
   * 
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
   * 
   * @param expr
   * @return
   */
  private static IExpr tr5Step(IExpr expr) {
    if (expr.isPresent()) {
      if (expr.isPower() && expr.first().isSin() && expr.second().isInteger()) {
        IAST sinExpr = (IAST) expr.base();
        IInteger exponent = (IInteger) expr.exponent();
        if (exponent.isPositive() && exponent.isEven()) {
          IInteger div2 = exponent.div(F.C2);
          if (div2.isOne()) {
            return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))));
          }
          return EvalEngine.get()
              .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))), div2));
        }
      }
    }
    return F.NIL;
  }

  public static IExpr tr6(IExpr expr) {
    return Traversal.bottomUp(expr, TrigSimplifyFu::tr6Step);
  }

  /**
   * <p>
   * Replacement of cos^2 with 1 - sin(x)^2.
   * 
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
   * 
   * @param expr
   * @return
   */
  private static IExpr tr6Step(IExpr expr) {
    if (expr.isPower() && expr.first().isCos() && expr.second().isInteger()) {
      IAST cosExpr = (IAST) expr.base();
      IInteger exponent = (IInteger) expr.exponent();
      if (exponent.isPositive() && exponent.isEven()) {
        IInteger div2 = exponent.div(F.C2);
        if (div2.isOne()) {
          return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))));
        }
        return EvalEngine.get()
            .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))), div2));
      }
    }
    return F.NIL;
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
   * 
   * @param expr
   * @param first TODO
   * @return
   */
  public static IExpr tr8Step(IExpr expr, boolean first) {
    if (expr.isTimes() || (expr.isPower() && (expr.base().isSin() || expr.base().isCos())
        && (expr.exponent().isInteger() || expr.base().isPositive()))) {

      if (first) {
        EvalEngine engine = EvalEngine.get();
        Pair numerDenom = expr.asNumerDenom();
        IExpr n = numerDenom.first();
        IExpr d = numerDenom.second();
        IExpr newn = tr8(TrigReduce.trigReduce(n, engine), false);
        IExpr newd = tr8(TrigReduce.trigReduce(d, engine), false);
        if (!newn.equals(n) || !newd.equals(d)) {
          if (d.isOne()) {
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
      args.get(S.Cos);
      args.get(S.Sin);
      args.get(S.None);
      IASTMutable argsList = Operations.makeArgs(S.Times, expr);
      argsList.sortInplace();
      for (int i = 1; i < argsList.size(); i++) {
        IExpr a = argsList.get(i);
        if (a.isCos() || a.isSin()) {
          args.get(a.head()).append(a.first());
        } else if (a.isPower() && a.exponent().isInteger() && a.exponent().isPositive()
            && (a.base().isCos() || a.base().isSin())) {
          args.get(a.base().head()).append(a.base().first().times(a.exponent()));
        } else {
          args.get(S.None).append(a);
        }
      }
      IASTAppendable c = args.get(S.Cos);
      IASTAppendable s = args.get(S.Sin);
      if (!(c.argSize() > 1 || s.argSize() > 1)) {
        return F.NIL;
      }
      IASTAppendable argsResult = F.TimesAlloc(8);
      argsResult.appendArgs(args.get(S.None));

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

  private static IExpr tr9(IExpr expr) {
    // TODO
    return expr;
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

  public static IExpr tr10i(IExpr expr) {
    // TODO
    return expr;
  }

  public static IExpr tr11(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr11Step(x));
  }

  private static IExpr tr11SingleStep(IExpr expr) {
    if ((expr.isSin() || expr.isCos()) && expr.first().isTimes()
        && expr.first().first().isInteger()) {
      IInteger times1 = (IInteger) expr.first().first();
      if (times1.isEven()) {
        if (expr.isSin()) {
          IExpr times1Half = times1.div(2);
          IExpr rest = expr.first().rest();
          return EvalEngine.get().evaluate(
              F.Times(F.C2, Sin(F.Times(times1Half, rest)), Cos(F.Times(times1Half, rest))));
        }
        if (expr.isCos()) {
          IExpr times1Half = times1.div(2);
          IExpr rest = expr.first().rest();
          return EvalEngine.get().evaluate(
              F.Subtract(F.C1, F.Times(F.C2, Power(Sin(F.Times(times1Half, rest)), F.C2))));
        }
      }
    }
    return F.NIL;
  }

  private static IExpr tr11Step(IExpr expr) {
    if ((expr.isSin() || expr.isCos())) {
      IExpr temp = tr11SingleStep(expr);
      if (temp.isPresent()) {
        IExpr result = temp;
        do {
          temp = Traversal.bottomUpNIL(result, x -> tr11Step(x));
          if (temp.isPresent()) {
            result = temp;
          }
        } while (temp.isPresent());
        return result;
      }
    }
    return F.NIL;
  }

  public static IExpr tr12(IExpr expr) {
    return Traversal.bottomUp(expr, x -> tr12Step(x));
  }


  private static IExpr tr12Step(IExpr rv) {
    if (rv.isTan()) {
      IExpr arg = rv.first();
      if (arg.isPlus()) {
        IAST plus = (IAST) arg;
        IExpr a = plus.arg1();
        IExpr b = plus.rest().oneIdentity0();
        final IExpr tb;
        if (b.isPlus()) {
          tb = tr12Step(F.Tan(b));
        } else {
          tb = F.Tan(b);
        }
        // (tan(a) + tb)/(1 - tan(a)*tb)
        IExpr temp = F.Divide(F.Plus(F.Tan(a), tb), //
            F.Subtract(F.C1, F.Times(F.Tan(a), tb)));
        return temp;
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
          args.get(a.head()).append(a.first());
        } else {
          args.get(S.None).append(a);
        }
      }
      IASTAppendable t = args.get(S.Tan);
      IASTAppendable c = args.get(S.Cot);
      if (t.argSize() < 2 && c.argSize() < 2) {
        return F.NIL;
      }
      IASTAppendable timesResult = args.get(S.None);
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

  public static IExpr trMorrie(IExpr rv) {
    return Traversal.bottomUp(rv, x -> trMorrieStep(x, true));
  }

  /**
   * 
   * 
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
        args.get(a).append(co);
        coss.put(b, e);
      } else {
        other.append(c);
      }
    }

    IASTAppendable result = F.ListAlloc(times.size());
    for (IExpr a : args.keySet()) {
      IASTAppendable c = args.get(a);
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
        IASTAppendable list = args.get(a);
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
