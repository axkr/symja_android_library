package org.matheclipse.core.sympy.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.sympy.utilities.Iterables;
import com.google.common.collect.Sets;

public class Expr {

  /**
   * 
   * @param that
   * @return a pair of {@link IASTAppendable}
   */
  public static Pair argsCnc(IExpr that) {
    return argsCnc(that, false, true);
  }

  /**
   * 
   * @param that
   * @param split_1
   * @return a pair of {@link IASTAppendable}
   */
  public static Pair argsCnc(IExpr that, boolean warn, boolean split_1) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/expr.py#L1280
    final IAST times;
    if (that.isTimes()) {
      times = (IAST) that;
    } else {
      if (that.isEmptyList()) {
        times = F.Times();
      } else {
        times = F.Times(that);
      }
    }

    IASTAppendable commutative = F.ListAlloc(times.argSize());
    IASTAppendable nonCommutative = F.ListAlloc();
    times.forEach(x -> {
      if (x.isAST(S.NonCommutativeMultiply)) {
        nonCommutative.appendArgs((IAST) x);
      } else {
        commutative.append(x);
      }
    });

    int sign = 0;
    if (commutative.argSize() > 0 //
        && split_1 //
        && !commutative.first().equals(F.CN1)) {
      IExpr arg1 = commutative.first();
      if (arg1.isNumber()) {
        sign = ((INumber) arg1).complexSign();
      } else if (arg1.isDirectedInfinity() && arg1.isNegativeResult()) {
        sign = -1;
      }
      if (sign == -1) {
        commutative.set(1, arg1.negate());
        commutative.append(1, F.CN1);
      }
    }
    // if (cset) {
    // // TODO
    // int clen = commutative.argSize();
    // Set<IExpr> cSet = commutative.asSet();
    // if (clen > 0 && warn && clen != cSet.size()) {
    // throw new ValueError("repeated commutative arguments");
    // // [ci for ci in c if list(self.args).count(ci) > 1])
    // }
    // }
    return F.pair(commutative, nonCommutative);

  }

  private static boolean asIndependentHas(IExpr e, IASTAppendable other, Set<IExpr> sym) {
    boolean hasOther = e.has(x -> other.indexOf(x) >= 0, true);
    if (sym.isEmpty()) {
      return hasOther;
    }
    VariablesSet vs = new VariablesSet(e);
    Set<IExpr> intersection = Sets.intersection(sym, vs.getVariablesSet());
    return hasOther || e.has(intersection::contains, true);
  }

  public static Pair asIndependent(IExpr self, IAST deps) {
    return asIndependent(self, deps, null);
  }

  /**
   * A mostly naive separation of a Times or Plus into arguments that are not dependent on deps.
   * 
   * @param self
   * @param deps
   * @param hint can be set to <code>null</code>, <code>Map.of("as_Add", S.True)</code> or
   *        code>Map.of("as_Add", S.False)</code>. To force the expression to be treated as an Add,
   *        use the hint <code>Map.of("as_Add", S.True)</code>.
   * @return
   */
  public static Pair asIndependent(IExpr self, IAST deps, Map<String, IExpr> hint) {
    if (self.isZero()) {
      return F.pair(self, self);
    }

    IExpr func = self.head();
    IBuiltInSymbol want = S.Times;
    if (hint == null) {
      if (self.isPlus()) {
        want = S.Plus;
      }
    } else {
      IExpr asAdd = hint.get("as_Add");
      if ((asAdd == null && self.isPlus()) //
          || (asAdd != null && asAdd.isTrue())) {
        want = S.Plus;
      }
    }
    Set<IExpr> sym = new HashSet<IExpr>();
    IASTAppendable other = F.ListAlloc();
    if (deps.isPresent()) {
      for (int i = 1; i < deps.size(); i++) {
        IExpr d = deps.get(i);
        if (d.isSymbol()) {
          sym.add(d);
        } else {
          other.append(d);
        }
      }
    }

    if (want != func || S.Plus != func && S.Times != func) {
      IExpr wantIdentity = want == S.Times ? F.C1 : F.C0;
      if (asIndependentHas(self, other, sym)) {
        return F.pair(wantIdentity, self);
      }
      return F.pair(self, wantIdentity);
    }
    IAST args = F.NIL;
    IAST nc = F.NIL;
    if (func == S.Plus) {
      args = ((IAST) self).apply(S.List);
    } else {
      Pair p = argsCnc(self);
      args = (IAST) p.first();
      nc = (IAST) p.second();
    }

    Pair d = Iterables.siftBinary(args, x -> asIndependentHas(x, other, sym));
    IASTAppendable depend = (IASTAppendable) d.first();
    IASTAppendable indep = (IASTAppendable) d.second();
    if (func == S.Plus) {
      // IASTAppendable result = F.PlusAlloc(depend.argSize() + indep.argSize());
      // result.appendArgs(indep);
      // result.appendArgs(depend);
      // return F.pair(F.C1, result.apply(S.Plus).oneIdentity0());

      return F.pair(indep.apply(S.Plus).oneIdentity0(), depend.apply(S.Plus).oneIdentity0());
    }

    // handle noncommutative by stopping at first dependent term
    for (int i = 1; i < nc.size(); i++) {
      IExpr n = nc.get(i);
      if (asIndependentHas(n, other, sym)) {
        // TODO depend.extend(nc[i:])
        depend.appendArgs(nc.copyFrom(i));
        break;
      }
      indep.append(n);
    }

    IExpr first = F.eval(indep.apply(S.Times));
    IExpr second = depend.apply(S.Times).oneIdentity1();
    return F.pair(first, second);
  }


  /**
   * helper for extractAdditively
   * 
   * @param eq
   * @param c
   * @return
   */
  private static Pair _corem(IExpr eq, IExpr c) {
    // return co, diff from co*c + diff
    IASTAppendable co = F.PlusAlloc(16);
    IASTAppendable non = F.PlusAlloc(16);
    IAST plusAST = makeArgs(S.Plus, eq);
    for (int j = 1; j < plusAST.size(); j++) {
      IExpr i = plusAST.get(j);
      IExpr ci = coeff(i, c);
      if (ci.isZero()) {
        non.append(i);
      } else {
        co.append(ci);
      }
    }
    return F.pair(F.eval(co), F.eval(non));
  }

  private static IAST makeArgs(IExpr head, IExpr eq) {
    return eq.isAST(head) ? (IAST) eq : F.unaryAST1(head, eq);
  }

  /**
   * Return self - c if it's possible to subtract c from self and make all matching coefficients
   * move towards zero, else return {@link F#NIL}.
   * 
   * @param self
   * @param c
   * @return {@link F#NIL} if the extraction isn't possible
   */
  public static IExpr extractAdditively(IExpr self, IExpr c) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/expr.py#L2332
    if (self.isIndeterminate()) {
      return F.NIL;
    }
    if (c.isZero()) {
      return self;
    } else if (c.equals(self)) {
      return F.C0;
    } else if (self.isZero()) {
      return F.NIL;
    }

    if (self.isNumber()) {
      if (!c.isNumber()) {
        return F.NIL;
      }
      IExpr co = self;
      IExpr diff = co.subtract(c);
      if ((co.isPositive() && diff.isPositive() && S.Less.of(diff, co).isTrue()) //
          || (co.isNegative() && diff.isNegative() && S.Greater.of(diff, co).isTrue())) {
        return diff;
      }
      return F.NIL;
    }

    if (c.isNumber()) {
      Pair p = self.asCoeffAdd();
      IExpr co = p.first();
      IExpr t = p.second();
      IExpr xa = extractAdditively(co, c);
      if (xa.isNIL()) {
        return F.NIL;
      }
      return xa.plus(((IAST) t).oneIdentity0());
    }

    // handle the args[0].is_Number case separately
    // since we will have trouble looking for the coeff of
    // a number.
    if (c.isASTSizeGE(S.Plus, 2) && c.first().isNumber()) {
      // whole term as a term factor
      IExpr co = coeff(self, c);
      IExpr xa0 = extractAdditively(co, F.C1).orElse(F.C0).times(c);
      if (xa0.isPresent() && !xa0.isZero()) {
        IExpr diff = self.subtract(co.times(c));
        return xa0.plus(extractAdditively(diff, c).orElse(diff));
      }
      // term-wise
      Pair pc = c.asCoeffAdd();
      IExpr h = pc.first();
      IExpr t = pc.second();
      Pair ps = self.asCoeffAdd();
      IExpr sh = ps.first();
      IExpr st = ps.second();
      IExpr xa = extractAdditively(sh, h);
      if (xa.isNIL()) {
        return F.NIL;
      }
      IExpr xa2 = extractAdditively(st, t);
      if (xa2.isNIL()) {
        return F.NIL;
      }
      return xa.plus(xa2);
    }

    // whole term as a term factor
    Pair p = _corem(self, c);
    IExpr pco = p.first();
    IExpr diff = p.second();
    IExpr xa0 = extractAdditively(pco, F.C1).orElse(F.C0).times(c);
    if (xa0.isPresent() && !xa0.isZero()) {
      return xa0.plus(extractAdditively(diff, c).orElse(diff));
    }
    // term-wise
    IAST plusAST = makeArgs(S.Plus, c);
    IASTAppendable coeffs = F.PlusAlloc(plusAST.argSize());
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr a = plusAST.get(i);
      Pair pa = a.asCoeffMul();
      IExpr ac = pa.first();
      IExpr at = pa.second();
      IExpr co = coeff(self, at);
      if (co.isNIL()) {
        return F.NIL;
      }
      Pair pc = co.asCoeffAdd();
      IExpr coc = pc.first();
      IExpr cot = pc.second();
      IExpr xa = extractAdditively(coc, ac);
      if (xa.isNIL()) {
        return F.NIL;
      }
      self = self.subtract(co.times(at));
      coeffs.append(cot.plus(xa).times(at));
    }
    coeffs.append(self);
    return F.eval(coeffs);
  }

  public static IExpr coeff(IExpr self, IExpr x) {
    return coeff(self, x, 1, false, true);
  }

  public static IExpr coeff(IExpr self, IExpr x, int n) {
    return coeff(self, x, n, false, true);
  }

  public static IExpr coeff(IExpr self, final IExpr x0, int n, boolean right, boolean _first) {
    // https://github.com/sympy/sympy/blob/b9af885473ad7e34b5b0826cb424dd26d8934670/sympy/core/expr.py#L1346
    if (x0 instanceof IDataExpr) {
      return F.C0;
    }
    // int n = nExpr.toIntDefault();
    // if (n == Integer.MIN_VALUE) {
    // // Machine-sized integer expected at position `2` in `1`.
    // IOFunctions.printMessage(S.Coefficient, "intm", F.List(nExpr, F.C3), EvalEngine.get());
    // return F.NIL;
    // }

    if (x0.isZero()) {
      return F.C0;
    }

    if (x0.equals(self)) {
      if (n == 1) {
        return F.C1;
      }
      return F.C0;
    }

    if (x0.isOne()) {
      IASTAppendable result = F.PlusAlloc(8);
      IAST plusAST = makeArgs(S.Plus, self);
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr a = plusAST.get(i);
        if (a.asCoeffMul().first().isOne()) {
          result.append(a);
        }
      }
      return result.oneIdentity0();
    }

    if (n == 0) {
      if (x0.isPlus() && self.isPlus()) {
        IExpr c = coeff(self, x0, 1, right, true);
        if (c.isZero()) {
          return F.C0;
        }
        IAST plusAST = makeArgs(S.Plus, c);
        if (!right) {
          return self.subtract(plusAST.mapExpr(a -> F.Times(a, x0)));
        }
        return self.subtract(plusAST.mapExpr(a -> F.Times(x0, a)));
      }
      return asIndependent(self, F.List(x0), Map.of("as_Add", S.True)).first();
    }

    // TODO

    // continue with the full method, looking for this power of x:
    IExpr x = x0.pow(n);


    IASTAppendable co = F.TimesAlloc(8);
    IAST args = self.isAST(S.Plus) ? (IAST) self : F.Plus(self);
    boolean self_c = true;
    boolean x_c = true;
    if (self_c && !x_c) {
      return F.C0;
    }
    if (_first && self.isPlus() && !self_c && !x_c) {
      // get the part that depends on x exactly
      // TODO
    }
    boolean one_c = self_c || x_c;
    // xargs, nx = x.args_cnc(cset=True, warn=bool(not x_c))
    Set<IExpr> xargs = ((IAST) argsCnc(x).first()).asSortedSet();
    for (int i = 1; i < args.size(); i++) {
      IExpr a = args.get(i);
      Set<IExpr> margs = ((IAST) argsCnc(a).first()).asSortedSet();
      if (xargs.size() > margs.size()) {
        continue;
      }
      // resid = margs.difference(xargs)
      // if len(resid) + len(xargs) == len(margs):
      Set<IExpr> resid = Sets.difference(margs, xargs);
      if (resid.size() + xargs.size() == margs.size()) {
        if (one_c) {
          IASTAppendable timesAST = F.TimesAlloc(resid.size());
          timesAST.append(resid, y -> y);
          co.append(timesAST.oneIdentity1());
          // co.append(Mul(*(list(resid) + nc)))
        } else {
          co.append(resid, y -> y);
          co.append(argsCnc(a).second());
          // co.append((resid, nc));
        }
      }
    }
    if (one_c) {
      if (co.argSize() > 0) {
        co.sortInplace();
      }
      return co.oneIdentity0();
    }
    return F.NIL;
  }

  /**
   * Removes the additive O(..) order symbol if there is one
   * 
   * @param self
   * @return
   */
  public static IExpr removeO(IExpr self) {
    if (self.isPlus()) {
      IAST selected = ((IAST) self).select(x -> !x.isAST(S.O, 2));
      return selected;
    }
    return self;
  }

  /**
   * Returns the additive O(..) order symbol if there is one, else {@link F#NIL}
   * 
   * @param self
   * @return
   */
  public static IExpr getO(IExpr self) {
    if (self.isPlus()) {
      IAST selected = ((IAST) self).select(x -> x.isAST(S.O, 2));
      if (selected.argSize() > 0) {
        return selected;
      }
    }
    return F.NIL;
  }

  /**
   * Returns the order of the expression.
   * 
   * @param self
   * @return
   * @throws UnsupportedOperationException
   */
  public static IExpr getN(IExpr self) throws UnsupportedOperationException {
    // Returns the order of the expression.
    //
    // Explanation
    // ===========
    //
    // The order is determined either from the O(...) term. If there
    // is no O(...) term, it returns None.
    //
    // Examples
    // ========
    //
    // >>> from sympy import O
    // >>> from sympy.abc import x
    // >>> (1 + x + O(x**2)).getn()
    // 2
    // >>> (1 + x).getn()
    IExpr o = getO(self);
    if (o.isNIL()) {
      return F.NIL;
    }
    if (o.isAST(S.O, 2)) {
      o = o.first();
      if (o.isOne()) {
        return F.C0;
      }
      if (o.isSymbol()) {
        return F.C1;
      }
      if (o.isPower()) {
        return o.second();
      }
      if (o.isTimes()) {
        IAST timesAST = (IAST) o;
        for (int i = 1; i < timesAST.size(); i++) {
          IExpr oi = timesAST.get(i);
          if (oi.isSymbol()) {
            return F.C1;
          }
          if (oi.isPower()) {
            IASTAppendable syms = Basic.atoms((IAST) oi, x -> x.isSymbol());
            if (syms.argSize() == 1) {
              IExpr x = syms.remove(1);
              oi = oi.subs(x, F.symbol("x", F.Greater(F.Slot1, F.C0)));
              if (oi.base().isSymbol() && oi.exponent().isRational()) {
                return oi.exponent().abs();
              }
            }

          }
        }
      }
    }
    throw new UnsupportedOperationException("not sure of order of " + o);
  }

}
