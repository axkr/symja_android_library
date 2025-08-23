package org.matheclipse.core.sympy.series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.Expr;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.sympy.simplify.Powsimp;
import org.matheclipse.core.sympy.simplify.Simplify;

public class Gruntz {

  public static class SubsSet extends HashMap<IExpr, ISymbol> {
    public Map<ISymbol, IExpr> rewrites;

    public SubsSet() {
      this.rewrites = new HashMap<>();
    }

    @Override
    public String toString() {
      return super.toString() + ", " + rewrites.toString();
    }

    @Override
    public ISymbol get(Object key) {
      if (!containsKey(key)) {
        put((IExpr) key, F.Dummy());
      }
      return super.get(key);
    }

    public IExpr doSubs(IExpr e) {
      for (Map.Entry<IExpr, ISymbol> entry : entrySet()) {
        e = e.xreplace(entry.getValue(), entry.getKey());
      }
      return e;
    }

    public boolean meets(SubsSet s2) {
      for (IExpr key : s2.keySet()) {
        if (rewrites.get(key) != null) {
          return true;
        }
      }
      return false;
    }

    public SubsSet union(SubsSet s2) {
      return (SubsSet) union(s2, F.NIL)[0];
    }

    public Object[] union(SubsSet s2, IExpr exps) {
      SubsSet res = this.copy();
      Map<ISymbol, ISymbol> tr = new HashMap<>();

      for (Map.Entry<IExpr, ISymbol> entry : s2.entrySet()) {
        IExpr expr = entry.getKey();
        ISymbol var = entry.getValue();

        if (containsKey(expr)) {
          if (exps.isPresent()) {
            exps = exps.xreplace(var, res.get(expr));
          }
          tr.put(var, res.get(expr));
        } else {
          res.put(expr, var);
        }
      }

      for (Map.Entry<ISymbol, IExpr> entry : s2.rewrites.entrySet()) {
        ISymbol var = entry.getKey();
        IExpr rewr = entry.getValue();
        res.rewrites.put(var, rewr.replaceAll(tr));
      }

      return new Object[] {res, exps};
    }

    public SubsSet copy() {
      SubsSet r = new SubsSet();
      r.rewrites = new HashMap<>(this.rewrites);
      for (Map.Entry<IExpr, ISymbol> entry : entrySet()) {
        r.put(entry.getKey(), entry.getValue());
      }
      return r;
    }
  }

  public static Object[] mrv(IExpr e, ISymbol x) {
    e = Powsimp.powsimp(e, true, "exp");

    // if (!(e instanceof IAST)) {
    // throw new IllegalArgumentException("e should be an instance of IAST");
    // }

    if (!e.has(x)) {
      return new Object[] {new SubsSet(), e};
    } else if (e.equals(x)) {
      SubsSet s = new SubsSet();
      return new Object[] {s, s.get(x)};
    } else if (e.isTimes() || e.isPlus()) {
      Pair independent = Expr.asIndependent(e, x.makeList());
      IExpr i = independent.first();
      IExpr d = independent.second();

      if (d.topHead() != e.topHead()) {
        Object[] result = mrv(d, x);
        SubsSet s = (SubsSet) result[0];
        IExpr expr = (IExpr) result[1];
        return new Object[] {s, F.binaryAST2(e.topHead(), i, expr)};
      }

      // IExpr[] terms = d.asTwoTerms();
      IExpr a = d.first();
      IExpr b = d.rest().eval();

      Object[] result1 = mrv(a, x);
      SubsSet s1 = (SubsSet) result1[0];
      IExpr e1 = (IExpr) result1[1];

      Object[] result2 = mrv(b, x);
      SubsSet s2 = (SubsSet) result2[0];
      IExpr e2 = (IExpr) result2[1];
      // return mrv_max1(s1, s2, e.func(i, e1, e2), x)
      return mrvMax1(s1, s2, F.ternaryAST3(e.topHead(), i, e1, e2), x);
    } else if (e.isPower() && !e.base().equals(S.E)) {
      // e1 = S.One
      // while e.is_Pow:
      // b1 = e.base
      // e1 *= e.exp
      // e = b1
      // if b1 == 1:
      // return SubsSet(), b1
      // if e1.has(x):
      // return mrv(exp(e1*log(b1)), x)
      // else:
      // s, expr = mrv(b1, x)
      // return s, expr**e1
      EvalEngine engine = EvalEngine.get();
      IExpr e1 = F.C1;
      IExpr b1 = F.C0;
      while (e.isPower()) {
        b1 = e.base();
        e1 = engine.evaluate(F.Times(e1, e.exponent()));
        e = b1;
      }
      if (b1.isOne()) {
        return new Object[] {new SubsSet(), e};
      }
      if (e1.has(x)) {
        IExpr temp = engine.evaluate(F.Exp(F.Times(e1, F.Log(b1))));
        return mrv(temp, x);
      } else {
        Object[] result = mrv(e, x);
        SubsSet s = (SubsSet) result[0];
        IExpr expr = (IExpr) result[1];
        return new Object[] {s, expr.power(e1)};
      }
    } else if (e.isLog()) {
      Object[] result = mrv(e.first(), x);
      SubsSet s = (SubsSet) result[0];
      IExpr expr = (IExpr) result[1];
      return new Object[] {s, F.Log(expr)};
    } else if (e.isExp()) { // || (e.isPower() && e.base().equals(S.E))) {
      if (e.exponent().isLog()) {
        return mrv(e.exponent().first(), x);
      }

      IExpr li = limitinf(e.exponent(), x);
      if (li.isInfinite()) {
        SubsSet s1 = new SubsSet();
        ISymbol e1 = s1.get(e);
        Object[] result = mrv(e.exponent(), x);
        SubsSet s2 = (SubsSet) result[0];
        IExpr e2 = (IExpr) result[1];
        SubsSet su = s1.union(s2);
        su.rewrites.put(e1, F.Exp(e2));
        return mrvMax3(s1, e1, s2, F.Exp(e2), su, e1, x);
      } else {
        Object[] result = mrv(e.exponent(), x);
        SubsSet s = (SubsSet) result[0];
        IExpr expr = (IExpr) result[1];
        return new Object[] {s, F.Exp(expr)};
      }
    } else if (e.isNumericFunction(false)) {// isFunction()) {
      IAST f = (IAST) e;
      List<Object[]> l = new ArrayList<Object[]>();
      for (IExpr a : f) {
        l.add(mrv(a, x));
      }

      List<SubsSet> l2 = new ArrayList<>();
      for (Object[] pair : l) {
        SubsSet s = (SubsSet) pair[0];
        if (!s.isEmpty()) {
          l2.add(s);
        }
      }
      if (l2.size() != 1) {
        throw new UnsupportedOperationException(
            "MRV set computation for functions in several variables not implemented.");
      }

      SubsSet s = l2.get(0);
      SubsSet ss = new SubsSet();

      IASTAppendable func = F.ast(f.head(), l.size());
      for (Object[] pair : l) {
        func.append(ss.doSubs((IExpr) pair[1]));
      }

      return new Object[] {s, func};
    } else if (e.isDerivative() != null) {
      throw new UnsupportedOperationException(
          "MRV set computation for derivatives not implemented yet.");
    }

    throw new UnsupportedOperationException("Don't know how to calculate the mrv of '" + e + "'");
  }

  public static Object[] mrvMax1(SubsSet f, SubsSet g, IExpr exps, ISymbol x) {
    Object[] unionResult = f.union(g, exps);
    SubsSet u = (SubsSet) unionResult[0];
    IExpr b = (IExpr) unionResult[1];

    return mrvMax3(f, f.doSubs(exps), g, g.doSubs(exps), u, b, x);
  }

  public static int sign(IExpr e, ISymbol x) {
    // if (!(e instanceof IExpr)) {
    // throw new IllegalArgumentException("e should be an instance of IExpr");
    // }

    if (e.isPositive()) {
      return 1;
    } else if (e.isNegative()) {
      return -1;
    } else if (e.isZero()) {
      return 0;
    } else if (!e.has(x)) {
      e = Simplify.logCombine(e);
      return F.Sign(e).eval().toIntDefault();
    } else if (e.equals(x)) {
      return 1;
    } else if (e.isTimes()) {
      // IExpr[] terms = e.asArgs();
      int sa = sign(e.first(), x);
      if (sa == 0) {
        return 0;
      }
      return sa * sign(e.rest(), x);
    } else if (e.isExp()) {
      return 1;
    } else if (e.isPower()) {
      IExpr base = e.base();
      IExpr exponent = e.exponent();
      if (base.equals(S.E)) {
        return 1;
      }
      int s = sign(base, x);
      if (s == 1) {
        return 1;
      }
      if (exponent.isInteger()) {
        return (int) Math.pow(s, exponent.toIntDefault());
      }
    } else if (e.isLog() && e.first().isPositive()) {
      return sign(e.first().subtract(F.C1), x);
    }

    // if all else fails, do it the hard way
    IPair leadTerm = mrvLeadTerm(e, x);
    return sign(leadTerm.first(), x);
  }

  public static IPair mrvLeadTerm(IExpr e, ISymbol x) {
    SubsSet Omega = new SubsSet();

    if (!e.has(x)) {
      return F.pair(e, F.C0);
    }

    if (Omega.isEmpty()) {
      Object[] mrvResult = mrv(e, x);
      Omega = (SubsSet) mrvResult[0];
      e = (IExpr) mrvResult[1];
    }

    if (Omega.isEmpty()) {
      return F.pair(e, F.C0);
    }

    if (Omega.containsKey(x)) {
      SubsSet OmegaUp = moveup2(Omega, x);
      IExpr expsUp = e.xreplace(x, F.Exp(x));
      Omega = OmegaUp;
      e = expsUp;
    }
    ISymbol w = F.Dummy("w", F.Greater(F.Slot1, F.C0));
    IExpr[] rewriteResult = rewrite(e, Omega, x, w);
    IExpr f = rewriteResult[0];
    IExpr logw = rewriteResult[1];

    // TODO
    // Ensure expressions of the form exp(log(...)) don't get simplified automatically in the
    // previous steps. See: https://github.com/sympy/sympy/issues/15323#issuecomment-478639399
    // f = f.replace(lambda f: f.is_Pow and f.has(x), lambda f: exp(log(f.base)*f.exp))
    // f = f.replaceAll(f1 -> f1.isPower() && f1.has(x),
    // f1 -> F.Exp(F.Times(F.Log(f1.base()), f1.exponent())));

    try {
      return f.leadTerm(w, logw, 0);
    } catch (Exception ex) {
      // int n0 = 1;
      IExpr _series = F.O(F.C1);
      IInteger incr = F.C1;

      while (_series.isOrder()) {
        // _series = f._eval_nseries(w, n=n0+incr, logx=logw)
        IInteger n = incr.add(1);
        int ni = n.toIntDefault();
        if (!F.isPresent(ni)) {
          return F.NIL;
        }
        _series = ASTSeriesData.simpleSeries(_series, w, F.C0, ni, 1, EvalEngine.get());
        incr = incr.multiply(2);
      }

      IExpr series = F.Expand(_series).removeO();

      try {
        return series.leadTerm(w, logw, 0);
      } catch (Exception ex2) {
        IPair coeffExp = e.asCoeffExponent(w);
        if (coeffExp.first().has(w)) {
          IPair baseExp = e.asBaseExp();
          IExpr base = baseExp.first().asCoeffExponent(w).first();
          IExpr exp = baseExp.second();
          return F.pair(F.Power(base, exp), F.Times(baseExp.second(), exp));
        }
        return F.pair(coeffExp.first(), coeffExp.second());
      }
    }
  }

  public static Object[] mrvMax3(SubsSet f, IExpr expsf, SubsSet g, IExpr expsg, SubsSet union,
      IExpr expsboth, ISymbol x) {
    // instanceof will always return true here
    // if (!(f instanceof SubsSet)) {
    // throw new IllegalArgumentException("f should be an instance of SubsSet");
    // }
    // if (!(g instanceof SubsSet)) {
    // throw new IllegalArgumentException("g should be an instance of SubsSet");
    // }
    if (f.isEmpty()) {
      return new Object[] {g, expsg};
    } else if (g.isEmpty()) {
      return new Object[] {f, expsf};
    } else if (f.meets(g)) {
      return new Object[] {union, expsboth};
    }

     IExpr f0 = f.keySet().iterator().next();
     IExpr g0 = g.keySet().iterator().next();
    char c = compare(f0, g0, x);
    if (c == '>') {
      return new Object[] {f, expsf};
    } else if (c == '<') {
      return new Object[] {g, expsg};
    } else {
      if (c != '=') {
        throw new IllegalArgumentException("c should be =");
      }
      return new Object[] {union, expsboth};
    }
  }

  public static SubsSet moveup2(SubsSet s, ISymbol x) {
    SubsSet r = new SubsSet();

    for (Map.Entry<IExpr, ISymbol> entry : s.entrySet()) {
      IExpr expr = entry.getKey();
      ISymbol var = entry.getValue();
      r.put(expr.xreplace(x, F.Exp(x)), var);
    }

    for (Map.Entry<ISymbol, IExpr> entry : s.rewrites.entrySet()) {
      ISymbol var = entry.getKey();
      IExpr expr = entry.getValue();
      r.rewrites.put(var, expr.xreplace(x, F.Exp(x)));
    }

    return r;
  }

  /**
   * Returns &quot;&lt;&quot; if a&lt;b, &quot;=&quot; for a == b, &quot;&gt;&quot; for a&gt;b
   * 
   * @param a
   * @param b
   * @param x
   * @return
   */
  public static char compare(IExpr a, IExpr b, ISymbol x) {
    // log(exp(...)) must always be simplified here for termination
    IExpr la = F.eval(F.Log(a));
    IExpr lb = F.eval(F.Log(b));
    // if isinstance(a, Basic) and (isinstance(a, exp) or (a.is_Pow and a.base == S.Exp1)):
    // la = a.exp
    // if isinstance(b, Basic) and (isinstance(b, exp) or (b.is_Pow and b.base == S.Exp1)):
    // lb = b.exp
    if (a.isExp()) {
      la = a.exponent();
    }
    if (b.isExp()) {
      lb = b.exponent();
    }
    IExpr div = F.eval(F.Divide(la, lb));
    IExpr c = limitinf(div, x);
    if (c.isZero()) {
      return '<';
    } else if (c.isInfinity()) {
      return '>';
    } else {
      return '=';
    }
  }

  /**
   * Limit e(x) for x-> oo.
   */
  public static IExpr limitinf(IExpr e, ISymbol x) {
    // rewrite e in terms of tractable functions only
    IExpr old = e;
    if (!e.has(x)) {
      return e; // e is a constant
    }

    EvalEngine engine = EvalEngine.get();

    // Handle Order terms - if e.has(Order): e = e.expand().removeO()
    if (e.isAST(S.O)) { // Check if expression contains Order terms
      e = F.Expand(e).eval(engine);
      // Remove Order terms - in Symja, Order terms are typically removed during expansion
      // Additional Order removal logic could be added here if needed
    }

    // Ensure x has proper assumptions for the algorithm
    if (!x.isPositive() || x.isInteger()) {
      // We make sure that x.is_positive is True and x.is_integer is None
      // so we get all the correct mathematical behavior from the expression.
      // We need a fresh variable.
      ISymbol p = F.Dummy("p"); // Create a positive dummy variable
      // In Symja, we cannot directly set assumptions like SymPy, but we use a fresh dummy
      e = e.subs(x, p);
      x = p;
    }

    // Rewrite to tractable functions - this is a critical step in SymPy
    // e = e.rewrite('tractable', deep=True, limitvar=x)
    // Since Symja doesn't have direct 'tractable' rewrite, we approximate with expansions
    e = F.Expand(e).eval(engine);
    e = F.PowerExpand(e).eval(engine);

    // Apply power simplification similar to powdenest in SymPy
    e = Powsimp.powsimp(e, true, "exp");

    // TODO: Handle AccumBounds if implemented in Symja
    // if isinstance(e, AccumBounds):
    // if mrv_leadterm(e.min, x) != mrv_leadterm(e.max, x):
    // raise NotImplementedError
    // c0, e0 = mrv_leadterm(e.min, x)
    // else:
    // c0, e0 = mrv_leadterm(e, x)

    // Get the leading term using proper mrv_leadterm function
    IPair leadTermResult;
    try {
      leadTermResult = mrvLeadTerm(e, x);
    } catch (Exception ex) {
      // If mrv_leadterm fails, try with simplified expression
      IExpr simplified = F.Simplify(e).eval(engine);
      try {
        leadTermResult = mrvLeadTerm(simplified, x);
      } catch (Exception ex2) {
        // Last resort: return original expression if we can't analyze it
        return old;
      }
    }

    IExpr c0 = leadTermResult.first();
    IExpr e0 = leadTermResult.second();

    // Determine the sign of the exponent
    int sig = sign(e0, x);

    if (sig == 1) {
      return F.C0; // e0>0: lim f = 0
    } else if (sig == -1) { // e0<0: lim f = +-oo (the sign depends on the sign of c0)
      // Handle complex numbers - check if c0 matches I*Wild("a", exclude=[I])
      // This is more sophisticated than the previous basic isComplex() check
      if (c0.isImaginaryUnit() || (c0.isTimes() && c0.first().isImaginaryUnit())) {
        return F.Times(c0, F.oo).eval(engine); // return c0*oo for pure imaginary coefficients
      }

      int s = sign(c0, x);
      // the leading term shouldn't be 0:
      if (s == 0) {
        throw new ValueError("Leading term should not be 0");
      }
      return F.Times(s, F.oo).eval(engine); // return s*oo
    } else if (sig == 0) { // e0=0: lim f = lim c0
      if (c0.equals(old)) {
        // Apply cancellation like SymPy's c0.cancel()
        c0 = F.Cancel(c0).eval(engine);
        // Additional simplification to ensure we make progress
        if (c0.equals(old)) {
          c0 = F.Simplify(c0).eval(engine);
        }
      }
      return limitinf(c0, x); // Recursive call with coefficient
    } else {
      throw new ValueError(sig + " could not be evaluated");
    }
  }

  // public static void main(String[] args) {
  // ExprEvaluator evaluator = new ExprEvaluator();
  // try {
  // IExpr a1 = evaluator.parse("x^2");
  // IExpr b1 = evaluator.parse("x^3");
  // IExpr x = evaluator.parse("x");
  // System.out.println(compare(a1, b1, x)); // Expected: "<"
  //
  // IExpr a2 = evaluator.parse("2^x");
  // IExpr b2 = evaluator.parse("3^x");
  // System.out.println(compare(a2, b2, x)); // Expected: "=" because ratio of logs tends to 1
  //
  // IExpr a3 = evaluator.parse("x");
  // IExpr b3 = evaluator.parse("Log(x)");
  // System.out.println(compare(a3, b3, x)); // Expected: ">"
  //
  // IExpr a4 = evaluator.parse("Exp(x)");
  // IExpr b4 = evaluator.parse("Exp(x)");
  // System.out.println(compare(a4, b4, x)); // Expected: "="
  //
  // IExpr a5 = evaluator.parse("Exp(x^2)");
  // IExpr b5 = evaluator.parse("Exp(x)");
  // System.out.println(compare(a5, b5, x)); // Expected: ">"
  //
  // IExpr a6 = evaluator.parse("x^x");
  // IExpr b6 = evaluator.parse("Exp(x*Log(x))");
  // System.out.println(compare(a6, b6, x)); // Expected: "="
  //
  // IExpr a7 = evaluator.parse("Log(x^2)");
  // IExpr b7 = evaluator.parse("Log(x)");
  // System.out.println(compare(a7, b7, x)); // Expected: "="
  //
  // IExpr a8 = evaluator.parse("x^2");
  // IExpr b8 = evaluator.parse("2*x^2");
  // System.out.println(compare(a8, b8, x)); // Expected: "="
  //
  // } catch (SyntaxError e) {
  // // catch parser syntax errors
  // System.out.println(e.getMessage());
  // } catch (MathException me) {
  // // catch math errors like "Divide by Zero"
  // System.out.println(me.getMessage());
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }

  public static IExpr[] rewrite(IExpr e, SubsSet Omega, ISymbol x, IExpr wsym) {
    if (Omega.isEmpty()) {
      throw new ValueError("Omega cannot be empty");
    }

    for (IExpr t : Omega.keySet()) {
      if (!t.isExp()) {
        throw new ValueError("All items in Omega must be exponentials");
      }
    }

    Map<ISymbol, IExpr> rewrites = Omega.rewrites;
    List<Map.Entry<IExpr, ISymbol>> omegaList = new ArrayList<>(Omega.entrySet());

    Map<ISymbol, Node> nodes = buildExpressionTree(omegaList, rewrites);
    omegaList.sort(
        (o1, o2) -> Integer.compare(nodes.get(o2.getValue()).ht(), nodes.get(o1.getValue()).ht()));

    int sig = 0;
    IExpr g = null;
    for (Map.Entry<IExpr, ISymbol> pair : omegaList) {
      g = pair.getKey();
      sig = sign(g.exponent(), x);
      if (sig != 1 && sig != -1) { // AccumBounds not handled
        throw new UnsupportedOperationException("Result depends on the sign of " + sig);
      }
    }

    if (sig == 1) {
      wsym = wsym.power(F.CN1); // if g goes to oo, substitute 1/w
    }

    List<Pair> O2 = new ArrayList<>();
    List<IExpr> denominators = new ArrayList<>();
    for (Map.Entry<IExpr, ISymbol> pair : omegaList) {
      IExpr f = pair.getKey();
      ISymbol var = pair.getValue();
      IExpr c = limitinf(f.exponent().divide(g.exponent()), x);
      if (c.isRational()) {
        denominators.add(((IRational) c).denominator());
      }
      IExpr arg = f.exponent();
      if (rewrites.containsKey(var)) {
        IExpr rewriteExpr = rewrites.get(var);
        if (!rewriteExpr.isExp()) {
          throw new ValueError("Rewrite expression must be an exponential");
        }
        arg = rewriteExpr.exponent();
      }
      O2.add(F.pair(var,
          // exp((arg - c*g.exp))*wsym**c
          F.Times(F.Exp(F.Plus(arg, F.Times(F.CN1, c, g.exponent()))), F.Power(wsym, c))
              .eval()));
    }

    IExpr f = Powsimp.powsimp(e, true, "exp");
    for (Pair pair : O2) {
      f = f.xreplace(pair.first(), pair.second());
    }

    for (Map.Entry<IExpr, ISymbol> pair : omegaList) {
      if (f.has(pair.getValue())) {
        throw new AssertionError(
            "Rewrite failed: variable " + pair.getValue() + " still present in expression");
      }
    }

    IExpr logw = g.exponent();
    if (sig == 1) {
      logw = logw.negate();
    }

    // TODO: ilcm for non-integer exponents
    // int exponent = ilcm(denominators);
    // f = f.subs(wsym, F.Power(wsym, exponent));
    // logw = F.Divide(logw, exponent);

    // f = bottomUp(f, w -> w.normal());
    return new IExpr[] {f.eval(), logw};
  }

  /**
   * Compute the limit of e(z) at the point z0 using the Gruntz algorithm.
   *
   * <p>
   * z0 can be any expression, including oo and -oo.
   *
   * <p>
   * For dir="+" (default) it calculates the limit from the right (z->z0+) and for dir="-" the limit
   * from the left (z->z0-). For infinite z0 (oo or -oo), the dir argument does not matter.
   *
   * <p>
   * This algorithm is fully described in the module docstring in the gruntz.py file. It relies
   * heavily on the series expansion. Most frequently, gruntz() is only used if the faster limit()
   * function (which uses heuristics) fails.
   */
  public static IExpr gruntz(IExpr e, ISymbol z, IExpr z0, String dir) {
    if (!z.isSymbol()) {
      throw new UnsupportedOperationException("Second argument must be a Symbol");
    }

    // convert all limits to the limit z->oo; sign of z is handled in limitinf
    IExpr e0;
    if (z0.equals(F.oo) || z0.equals(F.CIInfinity)) {
      e0 = e;
    } else if (z0.equals(F.CNInfinity) || z0.equals(F.CNIInfinity)) {
      e0 = e.subs(z, F.Negate(z));
    } else {
      if ("-".equals(dir)) {
        e0 = e.subs(z, F.Subtract(z0, F.Power(z, F.CN1)));
      } else if ("+".equals(dir)) {
        e0 = e.subs(z, F.Plus(z0, F.Power(z, F.CN1)));
      } else {
        throw new UnsupportedOperationException("dir must be '+' or '-'");
      }
    }

    IExpr r = limitinf(e0, z);

    // This is a bit of a heuristic for nice results... we always rewrite
    // tractable functions in terms of familiar intractable ones.
    // It might be nicer to rewrite the exactly to what they were initially,
    // but that would take some work to implement.
    // return r.rewrite('intractable', deep=True)
    // Symja does not have a direct equivalent of rewrite('intractable').
    // This step is for presentation. The main result is in 'r'.
    return r;
  }

  private static Map<ISymbol, Node> buildExpressionTree(List<Map.Entry<IExpr, ISymbol>> omega,
      Map<ISymbol, IExpr> rewrites) {
    Map<ISymbol, Node> nodes = new HashMap<>();
    for (Map.Entry<IExpr, ISymbol> entry : omega) {
      Node n = new Node();
      n.var = entry.getValue();
      n.expr = entry.getKey();
      nodes.put(n.var, n);
    }
    for (Map.Entry<IExpr, ISymbol> entry : omega) {
      ISymbol v = entry.getValue();
      if (rewrites.containsKey(v)) {
        Node n = nodes.get(v);
        IExpr r = rewrites.get(v);
        for (Map.Entry<IExpr, ISymbol> entry2 : omega) {
          ISymbol v2 = entry2.getValue();
          if (r.has(v2)) {
            n.before.add(nodes.get(v2));
          }
        }
      }
    }
    return nodes;
  }

  private static class Node {
    List<Node> before = new ArrayList<>();
    IExpr expr;
    ISymbol var;

    int ht() {
      return before.stream().mapToInt(Node::ht).sum() + 1;
    }
  }
}
