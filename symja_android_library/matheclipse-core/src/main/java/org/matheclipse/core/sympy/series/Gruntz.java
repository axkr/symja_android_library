package org.matheclipse.core.sympy.series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.Expr;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.sympy.simplify.Powsimp;

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
      Set<IExpr> intersection = keySet();
      intersection.retainAll(s2.keySet());
      return !intersection.isEmpty();
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
      IExpr b = d.rest();

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
    } else if (e.isExp()) { // || (e.isPower() && e.getAt(0).equals(S.E))) {
      if (e.second().isLog()) {
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
        Object[] result = mrv(e.getAt(1), x);
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

    char c = compare(f.keySet().iterator().next(), g.keySet().iterator().next(), x);
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
  public static char compare(IExpr a, IExpr b, IExpr x) {
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
   * 
   * @param e
   * @param x
   * @return
   */
  public static IExpr limitinf(IExpr e, IExpr x) {
    // rewrite e in terms of tractable functions only
    IExpr old = e;
    if (!e.has(x)) {
      return e; // e is a constant
    }
    EvalEngine engine = EvalEngine.get();
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      // if e.has(Order):
      // e = e.expand().removeO()
      if (!x.isPositive() || x.isInteger()) {
        // We make sure that x.is_positive is True and x.is_integer is None
        // so we get all the correct mathematical behavior from the expression.
        // We need a fresh variable.
        ISymbol p = F.Dummy('p');
        IAssumptions.assign(p, F.Greater(p, F.C0), oldAssumptions, engine);
        e = F.subs(e, x, p);
        x = p;
      }
      // e = e.rewrite('tractable', deep=True, limitvar=x)
      // e = powdenest(e);
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
    throw new ValueError("not implemented");
  }

  public static IExpr[] rewrite(IExpr e, SubsSet Omega, ISymbol x, ISymbol wsym) {
    // if (!(Omega instanceof SubsSet)) {
    // throw new IllegalArgumentException("Omega should be an instance of SubsSet");
    // }
    // if (Omega.isEmpty()) {
    // throw new IllegalArgumentException("Length cannot be 0");
    // }
    //
    // // all items in Omega must be exponentials
    // for (IExpr t : Omega.keySet()) {
    // if (!(t instanceof Exp)) {
    // throw new IllegalArgumentException("Value should be exp");
    // }
    // }
    //
    // Map<ISymbol, IExpr> rewrites = Omega.rewrites;
    // List<IExpr[]> OmegaList = new ArrayList<>(Omega.items());
    //
    // // Build expression tree and sort Omega
    // List<Node> nodes = buildExpressionTree(OmegaList, rewrites);
    // Collections.sort(OmegaList, Comparator.comparingInt(o -> nodes.get(o[1].hashCode()).ht()));
    //
    // // Determine the sign of each exp() term
    // int sig = 0;
    // IExpr g = null;
    // for (IExpr[] pair : OmegaList) {
    // g = pair[0];
    // sig = sign(g.getAt(1), x);
    // if (sig != 1 && sig != -1 && !(sig instanceof AccumBounds)) {
    // throw new UnsupportedOperationException("Result depends on the sign of " + sig);
    // }
    // }
    // if (sig == 1) {
    // wsym = F.Power(wsym, -1); // if g goes to oo, substitute 1/w
    // }
    //
    // // Rewrite each item in Omega using "w"
    // List<IExpr[]> O2 = new ArrayList<>();
    // List<Integer> denominators = new ArrayList<>();
    // for (IExpr[] pair : OmegaList) {
    // IExpr f = pair[0];
    // ISymbol var = (ISymbol) pair[1];
    // IExpr c = limitinf(F.Divide(f.getAt(1), g.getAt(1)), x);
    // if (c.isRational()) {
    // denominators.add(c.toRational().denominator().intValue());
    // }
    // IExpr arg = f.getAt(1);
    // if (rewrites.contains(var)) {
    // if (!(rewrites.get(var) instanceof Exp)) {
    // throw new IllegalArgumentException("Value should be exp");
    // }
    // arg = rewrites.get(var).getAt(0);
    // }
    // O2.add(
    // new IExpr[] {var, F.Times(F.Exp(F.Plus(arg, F.Times(c, g.getAt(1)))), F.Power(wsym, c))});
    // }
    //
    // // Substitute subexpressions in "e" with rewritten expressions
    // IExpr f = Powsimp.powsimp(e, true, "exp");
    // for (IExpr[] pair : O2) {
    // f = f.subs(pair[0], pair[1]);
    // }
    //
    // for (IExpr[] pair : OmegaList) {
    // ISymbol var = (ISymbol) pair[1];
    // if (f.has(var)) {
    // throw new AssertionError();
    // }
    // }
    //
    // // Compute the logarithm of w (logw)
    // IExpr logw = g.getAt(1);
    // if (sig == 1) {
    // logw = F.Negate(logw); // log(w)->log(1/w)=-log(w)
    // }
    //
    // // Improve series expansions with non-integral exponents
    // int exponent = ilcm(denominators);
    // f = f.subs(wsym, F.Power(wsym, exponent));
    // logw = F.Divide(logw, exponent);
    //
    // // Simplify the expression
    // f = bottomUp(f, w -> w.normal());
    //
    // return new IExpr[] {f, logw};
    return new IExpr[] {F.NIL, F.NIL};
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
