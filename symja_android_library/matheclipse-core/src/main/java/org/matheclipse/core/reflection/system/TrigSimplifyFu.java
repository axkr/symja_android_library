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
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.sympy.DefaultDict;
import org.matheclipse.core.expression.sympy.ExprTools;
import org.matheclipse.core.expression.sympy.Operations;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href=
 * "https://github.com/sympy/sympy/blob/master/sympy/simplify/fu.py">sympy/simplify/fu.py</a>
 *
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
    Function<IExpr, Long> measure =
        SimplifyFunctions.createComplexityFunction(complexityFunctionHead, engine);

    // CTR1 = [(TR5, TR0), (TR6, TR0), identity]
    Function<IExpr, IExpr>[] CTR1 = new Function[2];
    CTR1[0] = TR5.andThen(TR0);
    CTR1[1] = TR6.andThen(TR0);

    // CTR2 = (TR11, [(TR5, TR0), (TR6, TR0), TR0])
    Function<IExpr, IExpr>[] CTR2 = new Function[1];
    CTR2[0] = new Chain(TR11, CTR1, measure, engine);

    Function<IExpr, IExpr> RL1 = new Chain(CTR1, CTR2, measure, engine);
    Function<IExpr, IExpr> RL2 = new Chain(CTR1, CTR2, measure, engine);


    IExpr rv = tr1(expr).orElse(expr);
    if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
      IExpr rv1 = RL1.apply(rv);
      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
        rv = tr2(rv).orElse(rv);
      }
    }

    if (rv.has(x -> x.isSin() || x.isCos(), true)) {
      IExpr rv1 = RL2.apply(rv).orElse(rv);
      IExpr trMorrie = trMorrie(rv1);
      IExpr rv2 = tr8(trMorrie, true).orElse(trMorrie);

      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (measure.apply(rv2) < measure.apply(rv)) {
        rv = rv2;
      }
    }

    if (!rv.isPresent()) {
      rv = expr;
    }
    IExpr rv3 = tr2i(rv, false).orElse(expr);
    if (measure.apply(rv3) < measure.apply(rv)) {
      rv = rv3;
    }
    return rv.orElse(expr);

  }

  private static IExpr tr0(IExpr expr) {
    if (expr.isAST()) {
      return EvalEngine.get().evaluate(F.Expand(F.Factor(expr)));
    }
    return F.NIL;
  }

  private static IExpr tr1(IExpr expr) {
    if (expr.isAST(S.Sec, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(1, F.Cos(arg1));
    }
    if (expr.isAST(S.Csc, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(1, F.Sin(arg1));
    }
    return F.NIL;
  }

  private static IExpr tr2(IExpr expr) {

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
    return (e.isInteger() || k.isPositive())//
        && (k.isCos() || k.isSin());
    // TODO
    // || (half && k.isPlus()&&k.argSize()>=2&&));
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
  public static IExpr tr2i(IExpr expr, boolean half) {
    if (expr.isTimes()) {
      IExpr[] asNumerDenom = expr.asNumerDenom();
      IExpr numer = asNumerDenom[0];
      IExpr denom = asNumerDenom[1];
      if (numer.isAST() && denom.isAST()) {
        IAST n = (IAST) numer;
        IAST d = (IAST) denom;
        DefaultDict<IExpr> nDict = n.asPowersDict();
        IASTAppendable ndone = F.ListAlloc();
        for (IExpr k : nDict.keySet()) {
          if (!ok(k, nDict.get(k), half)) {
            ndone.append(F.List(k, nDict.remove(k)));
          }
        }
        if (!nDict.isEmpty()) {
          return F.NIL;
        }

        DefaultDict<IExpr> dDict = d.asPowersDict();
        IASTAppendable ddone = F.ListAlloc();
        for (IExpr k : dDict.keySet()) {
          if (!ok(k, dDict.get(k), half)) {
            ddone.append(F.List(k, dDict.remove(k)));
          }
        }
        if (!dDict.isEmpty()) {
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
              nDict.put(k, S.None);
              dDict.put(a, S.None);
            } else if (half) {
              IExpr a1 = a.plus(F.C1);
              if (dDict.containsKey(a1) && dDict.get(a1).equals(nDict.get(k))) {
                t.append(F.Power(F.Tan(k.first().divide(F.C2)), nDict.get(k)));
                nDict.put(k, S.None);
                dDict.put(a1, S.None);
              }
            }
          } else if (k.isCos()) {
            IExpr a = F.Sin(k.first());
            if (dDict.containsKey(a) && dDict.get(a).equals(nDict.get(k))) {
              t.append(F.Power(F.Tan(k.first()), nDict.get(k).negate()));
              nDict.put(k, S.None);
              dDict.put(a, S.None);
            }
          } else if (half && k.isPlus() && k.first().isOne() && k.second().isCos()) {
            IExpr a = F.Sin(k.second().first());
            if (dDict.containsKey(a) && dDict.get(a).equals(nDict.get(k))
                && (dDict.get(a).isInteger() || a.isPositive())) {
              t.append(F.Power(F.Tan(a.first().divide(F.C2)), nDict.get(k).negate()));
              nDict.put(k, S.None);
              dDict.put(a, S.None);
            }
          }
        }

        if (!t.isEmpty()) {
          IExpr rv = F.Times();
          IAST testt;
          IASTAppendable mul1 = F.TimesAlloc(nDict.size() + 1);
          mul1.append(t);
          mul1 = nDict.forEach(mul1, (b, e) -> (!e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul2 = F.TimesAlloc(dDict.size());
          mul2 = dDict.forEach(mul2, (b, e) -> (!e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul3 = F.TimesAlloc(ndone.argSize());
          mul3 = ndone.forEach(mul3, (b, e) -> (!e.isZero()) ? F.Power(b, e) : F.NIL);
          IASTAppendable mul4 = F.TimesAlloc(ddone.argSize());
          mul4 = ddone.forEach(mul4, (b, e) -> (!e.isZero()) ? F.Power(b, e) : F.NIL);
          return F.Times(F.Divide(mul1, mul2), F.Divide(mul3, mul4));
        }
      }
    }
    return F.NIL;
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
  private static IExpr tr5(IExpr expr) {
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
  private static IExpr tr6(IExpr expr) {
    if (expr.isPresent()) {
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
    }
    return F.NIL;
  }

  /**
   * 
   * @param expr
   * @param first TODO
   * @return
   */
  public static IExpr tr8(IExpr expr, boolean first) {
    if (expr.isTimes() || (expr.isPower() && (expr.base().isSin() || expr.base().isCos())
        && (expr.exponent().isInteger() || expr.base().isPositive()))) {

      if (first) {
        IExpr[] numerDenom = expr.asNumerDenom();
        IExpr n = numerDenom[0];
        IExpr d = numerDenom[1];
        IExpr newn = tr8(n, false).orElse(n);
        IExpr newd = tr8(d, false).orElse(d);
        if (!newn.equals(n) || !newd.equals(d)) {
          if (d.isOne()) {
            return newn;
          }
          IExpr rv = ExprTools.gcdTerms(F.Divide(newn, newd));
          if (rv.isTimes() && rv.first().isRational() && rv.argSize() == 2
              && rv.second().isPlus()) {
            IAST asCoeffMul = rv.asCoeffMul();
            IASTAppendable result = F.TimesAlloc(3);
            result.append(asCoeffMul.first());
            result.appendArgs((IAST) asCoeffMul.second());
            return result;
          }
          return rv;
        }
        return expr;
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
      if (c.argSize() == 0 && s.argSize() == 0) {
        return expr;
      }
      IASTAppendable argsResult = F.TimesAlloc(8);
      argsResult.appendArgs(args.get(S.None));

      int n = Math.min(c.argSize(), s.argSize());
      for (int i = 0; i < n; i++) {
        IExpr a1 = s.remove(1);
        IExpr a2 = c.remove(1);
        argsResult.append(F.Divide(F.Plus(F.Sin(F.Plus(a1, a2)), F.Sin(F.Subtract(a1, a2))), 2));
      }
      while (c.argSize() > 1) {
        IExpr a1 = c.remove(1);
        IExpr a2 = c.remove(1);
        argsResult.append(F.Divide(F.Plus(F.Cos(F.Plus(a1, a2)), F.Cos(F.Subtract(a1, a2))), 2));
      }
      if (c.argSize() > 0) {
        IExpr a1 = c.remove(1);
        argsResult.append(F.Cos(a1));
      }
      while (s.argSize() > 1) {
        IExpr a1 = s.remove(1);
        IExpr a2 = s.remove(1);
        argsResult
            .append(F.Divide(F.Subtract(F.Cos(F.Subtract(a1, a2)), F.Cos(F.Plus(a1, a2))), 2));
      }
      if (s.argSize() > 0) {
        IExpr a1 = s.remove(1);
        argsResult.append(F.Sin(a1));
      }

      IExpr evalExpandAll = F.evalExpandAll(argsResult);
      return tr8(evalExpandAll, true).orElse(evalExpandAll);
    }
    return F.NIL;
  }

  private static IExpr tr10(IExpr expr) {
    if ((expr.isSin() || expr.isCos())) {
      return EvalEngine.get().evaluate(F.TrigExpand(expr));
    }
    return F.NIL;
  }

  private static IExpr tr11(IExpr expr) {
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

  /**
   * 
   * 
   * @param expr
   * @return
   */
  public static IExpr trMorrie(IExpr rv) {
    if (!rv.isTimes()) {
      return F.NIL;
    }
    IAST times = (IAST) rv;
    IASTAppendable other = F.ListAlloc(times.size());
    Map<IExpr, IExpr> coss = new HashMap<IExpr, IExpr>();
    DefaultDict<IASTAppendable> args = new DefaultDict<IASTAppendable>();
    for (int i = 1; i < times.size(); i++) {
      IExpr c = times.get(i);
      IExpr b = c;
      IExpr e = F.C1;
      if (c.isPower()) {
        b = c.base();
        e = c.exponent();
      }
      if (b.isCos() && e.isInteger()) {
        IExpr a = b.first();
        if (a.isTimes() && a.first().isInteger()) {
          IExpr co = a.first();
          a = a.rest().oneIdentity1();
          args.get(a).append(co);
          coss.put(b, e);
        } else {
          args.get(a).append(F.C1);
          coss.put(b, F.C1);
        }
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
        IExpr cc = c.get(1);
        IExpr ci = cc;
        for (int i = 1; i < c.size(); i++) {
          if (cc.equals(c.get(i))) {
            k += 1;
            cc = cc.multiply(2);
          }
        }
        if (k > 1) {
          // sin(2**k*ci*a)/2**k/sin(ci*a)
          IAST newarg = F.Divide(F.Sin(F.Times(F.Power(F.C2, k), ci, a)),
              F.Times(F.Power(F.C2, k), F.Sin(F.Times(ci, a))));
          IExpr take = F.NIL;
          IASTAppendable ccs = F.ListAlloc();
          for (int i = 0; i < k; i++) {
            cc = cc.divide(F.C2);
            IExpr key = F.Cos(F.Times(a, cc));
            ccs.append(cc);
            take = min(coss.get(key), take.isPresent() ? take : coss.get(key));
          }
          for (int i = 0; i < k; i++) {
            cc = ccs.remove(1);
            IExpr key = F.Cos(F.eval(F.Times(a, cc)));
            coss.put(key, coss.get(key).minus(take));
            if (coss.get(key).isZero()) {
              c.remove(cc);
            }
          }
          result.append(F.Power(newarg, take));
        } else {
          IExpr pop0 = c.remove(1);
          IExpr b = F.Cos(F.Times(pop0, a));
          other.append(F.Power(b, coss.get(b)));
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
    return rv;

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
