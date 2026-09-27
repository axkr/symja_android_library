package org.matheclipse.core.sympy.simplify;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.sympy.core.Traversal;

/**
 * Simplify expressions with gamma functions or functions with non-integer arguments which are
 * converted to gamma functions. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/gammasimp.py">sympy/simplify/gammasimp.py</a>
 *
 * <p>
 * The algorithm works by rewriting all combinatorial functions as gamma functions and applying
 * several identities in order to simplify the expression:
 * <ul>
 * <li>Reduces the number of gammas by applying the reflection theorem
 * <code>Gamma(x)*Gamma(1-x) == Pi/Sin(Pi*x)</code>.
 * <li>Reduces the number of gammas by applying the multiplication theorem
 * <code>Gamma(x)*Gamma(x+1/n)*...*Gamma(x+(n-1)/n) == C*Gamma(n*x)</code>.
 * <li>Reduces the number of prefactors by absorbing them into gammas, where possible.
 * </ul>
 */
public class GammaSimp {

  /** Powers with a greater exponent aren't splitted into single factors */
  private static final int MAX_EXPONENT = 8;

  private GammaSimp() {}

  private static IExpr eval(IExpr expr) {
    return EvalEngine.get().evaluate(expr);
  }

  /**
   * Rewrite the combinatorial functions as gamma functions.
   */
  private static IExpr rewriteGamma(IExpr expr) {
    // expr = expr.rewrite(gamma)
    return F.subst(expr, x -> {
      if (x.isAST()) {
        IAST ast = (IAST) x;
        if (ast.isAST(S.Factorial, 2)) {
          return F.Gamma(F.Plus(F.C1, ast.arg1()));
        }
        if (ast.isAST(S.Binomial, 3)) {
          IExpr n = ast.arg1();
          IExpr k = ast.arg2();
          // Gamma(n+1)/(Gamma(k+1)*Gamma(n-k+1))
          return F.Times(F.Gamma(F.Plus(F.C1, n)), F.Power(F.Gamma(F.Plus(F.C1, k)), F.CN1),
              F.Power(F.Gamma(F.Plus(F.C1, n, F.Negate(k))), F.CN1));
        }
        if (ast.isAST(S.Pochhammer, 3)) {
          // Gamma(a+n)/Gamma(a)
          return F.Times(F.Gamma(F.Plus(ast.arg1(), ast.arg2())),
              F.Power(F.Gamma(ast.arg1()), F.CN1));
        }
        if (ast.isAST(S.Beta, 3)) {
          // Gamma(a)*Gamma(b)/Gamma(a+b)
          return F.Times(F.Gamma(ast.arg1()), F.Gamma(ast.arg2()),
              F.Power(F.Gamma(F.Plus(ast.arg1(), ast.arg2())), F.CN1));
        }
      }
      return F.NIL;
    });
  }

  /**
   * Test if the expression contains at least two gamma or combinatorial functions, which are the
   * candidates for a simplification.
   */
  public static boolean isCandidate(IExpr expr) {
    return countCombinatorial(expr) >= 2;
  }

  private static int countCombinatorial(IExpr expr) {
    if (!expr.isAST()) {
      return 0;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    int result = (head == S.Gamma || head == S.Factorial || head == S.Binomial
        || head == S.Pochhammer || head == S.Beta) ? 1 : 0;
    for (int i = 1; i < ast.size() && result < 2; i++) {
      result += countCombinatorial(ast.get(i));
    }
    return result;
  }

  private static boolean hasCombinatorialFunction(IExpr expr) {
    return !expr.isFree(x -> x == S.Gamma || x == S.Factorial || x == S.Binomial
        || x == S.Pochhammer || x == S.Beta, true);
  }

  /**
   * Simplify expressions with gamma functions.
   *
   * @return the simplified expression or the unchanged <code>expr</code>
   */
  public static IExpr gammasimp(IExpr expr) {
    // >>> gammasimp(gamma(x)/gamma(x - 3))
    // (x - 3)*(x - 2)*(x - 1)
    // >>> gammasimp(gamma(n + 3))
    // gamma(n + 3)
    if (!hasCombinatorialFunction(expr)) {
      // avoid side effects like factoring
      return expr;
    }
    try {
      IExpr rewritten = eval(rewriteGamma(expr));
      if (rewritten.isFree(S.Gamma, true)) {
        return rewritten;
      }
      IExpr was = rewritten;
      IExpr result = Traversal.bottomUp(was, x -> {
        if (x.isTimes() || isGammaPower(x)) {
          return ruleGamma(x, true);
        }
        if (x.isPlus()) {
          return ruleGammaPlus((IAST) x);
        }
        return x;
      });
      result = eval(result);
      if (!result.equals(was)) {
        IExpr factored = eval(F.Factor(result));
        if (factored.leafCount() <= result.leafCount()) {
          result = factored;
        }
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return expr;
  }

  /**
   * Simplify combinatorial expressions. The result is expressed with factorials, if the expression
   * contains no gamma functions.
   */
  public static IExpr combsimp(IExpr expr) {
    // >>> combsimp(factorial(n)/factorial(n - 3))
    // n*(n - 2)*(n - 1)
    // >>> combsimp(binomial(n+1, k+1)/binomial(n, k))
    // (n + 1)/(k + 1)
    final boolean hasGamma = !expr.isFree(S.Gamma, true);
    IExpr result = gammasimp(expr);
    if (!hasGamma && !result.isFree(S.Gamma, true)) {
      // expr = expr.rewrite(factorial)
      result = eval(F.subst(result,
          x -> x.isAST(S.Gamma, 2) ? F.Factorial(F.Plus(F.CN1, x.first())) : F.NIL));
    }
    return result;
  }

  private static boolean isGammaPower(IExpr x) {
    return x.isPower() && x.base().isAST(S.Gamma, 2) && x.exponent().isInteger();
  }

  /** Split the argument into <code>{c, resid}</code> with a rational number <code>c</code> */
  private static IExpr[] asCoeffAdd(IExpr arg) {
    if (arg.isRational()) {
      return new IExpr[] {arg, F.C0};
    }
    if (arg.isPlus() && arg.first().isRational()) {
      return new IExpr[] {arg.first(), ((IAST) arg).rest().oneIdentity0()};
    }
    return new IExpr[] {F.C0, arg};
  }

  /**
   * Add the factors of <code>factor^exponent</code> to the numerator or denominator lists.
   */
  private static void explicate(IExpr factor, List<IExpr> numerGammas, List<IExpr> denomGammas,
      List<IExpr> numerOthers, List<IExpr> denomOthers) {
    IExpr base = factor;
    int exponent = 1;
    if (factor.isPower() && factor.exponent().isInteger()) {
      int e = factor.exponent().toIntDefault();
      if (e != Integer.MIN_VALUE && Math.abs(e) <= MAX_EXPONENT) {
        base = factor.base();
        exponent = e;
      }
    }
    if (base.isOne()) {
      return;
    }
    final boolean isGamma = base.isAST(S.Gamma, 2);
    final IExpr item = isGamma ? eval(F.Expand(base.first())) : base;
    List<IExpr> list;
    if (exponent > 0) {
      list = isGamma ? numerGammas : numerOthers;
    } else {
      list = isGamma ? denomGammas : denomOthers;
    }
    for (int i = 0; i < Math.abs(exponent); i++) {
      list.add(item);
    }
  }

  /**
   * Normalize the arguments of the gamma functions with the recurrence
   * <code>Gamma(x+1)==x*Gamma(x)</code>, so that the rational number part <code>c</code> in the
   * arguments is in the range <code>1&lt;=c&lt;2</code>.
   */
  private static void normalize(List<IExpr> gammas, List<IExpr> numer, List<IExpr> denom) {
    for (int i = 0; i < gammas.size(); i++) {
      IExpr g = gammas.get(i);
      IExpr[] split = asCoeffAdd(g);
      if (split[1].isZero()) {
        // numeric argument
        continue;
      }
      IRational c = (IRational) split[0];
      int n = c.floor().toIntDefault() - 1;
      if (n == Integer.MIN_VALUE + 1 || Math.abs(n) > 64) {
        continue;
      }
      if (n > 0) {
        // Gamma(x+n) = Gamma(x)*x*(x+1)*...*(x+n-1)
        IExpr x = eval(F.Subtract(g, F.ZZ(n)));
        for (int k = 0; k < n; k++) {
          numer.add(eval(F.Plus(x, F.ZZ(k))));
        }
        gammas.set(i, x);
      } else if (n < 0) {
        // Gamma(x-n) = Gamma(x)/((x-1)*(x-2)*...*(x-n))
        IExpr x = eval(F.Subtract(g, F.ZZ(n)));
        for (int k = 1; k <= -n; k++) {
          denom.add(eval(F.Subtract(x, F.ZZ(k))));
        }
        gammas.set(i, x);
      }
    }
  }

  /** Remove the gamma functions which occur in the numerator and denominator */
  private static void cancel(List<IExpr> numer, List<IExpr> denom) {
    for (int i = numer.size() - 1; i >= 0; i--) {
      int j = denom.indexOf(numer.get(i));
      if (j >= 0) {
        denom.remove(j);
        numer.remove(i);
      }
    }
  }

  /**
   * Try to reduce the number of gamma factors by applying the reflection formula
   * <code>gamma(x)*gamma(1-x) = pi/sin(pi*x)</code>
   */
  private static void reflection(List<IExpr> gammas, List<IExpr> numer, List<IExpr> denom) {
    List<IExpr> result = new ArrayList<IExpr>();
    while (!gammas.isEmpty()) {
      IExpr g1 = gammas.remove(gammas.size() - 1);
      if (g1.isIntegerResult()) {
        result.add(g1);
        continue;
      }
      boolean found = false;
      for (int i = 0; i < gammas.size(); i++) {
        IExpr g2 = gammas.get(i);
        IExpr n = eval(F.Expand(F.Plus(g1, g2, F.CN1)));
        if (!n.isInteger()) {
          continue;
        }
        int ni = n.toIntDefault();
        if (ni == Integer.MIN_VALUE || Math.abs(ni) > 64) {
          continue;
        }
        numer.add(S.Pi);
        // Sin(Pi*(g+m)) == (-1)^m * Sin(Pi*g)
        IExpr sinArg = g1;
        int m = ((IRational) asCoeffAdd(g1)[0]).floor().toIntDefault();
        if (m != Integer.MIN_VALUE && m != 0) {
          sinArg = eval(F.Subtract(g1, F.ZZ(m)));
          if ((m & 1) == 1) {
            numer.add(F.CN1);
          }
        }
        denom.add(F.Sin(F.Times(S.Pi, sinArg)));
        gammas.remove(i);
        if (ni > 0) {
          // numer.extend(1 - g1 + k for k in range(n))
          for (int k = 0; k < ni; k++) {
            numer.add(eval(F.Plus(F.C1, F.Negate(g1), F.ZZ(k))));
          }
        } else if (ni < 0) {
          // denom.extend(-g1 - k for k in range(-n))
          for (int k = 0; k < -ni; k++) {
            denom.add(eval(F.Subtract(F.Negate(g1), F.ZZ(k))));
          }
        }
        found = true;
        break;
      }
      if (!found) {
        result.add(g1);
      }
    }
    gammas.addAll(result);
  }

  /**
   * Try to reduce the number of gammas by using the duplication theorem to cancel an upper and
   * lower: <code>gamma(2*s)/gamma(s) = 2**(2*s + 1)/(4*sqrt(pi))*gamma(s + 1/2)</code>.
   */
  private static void duplication(List<IExpr> ng, List<IExpr> dg, List<IExpr> no,
      List<IExpr> doList) {
    int counter = 0;
    while (counter++ < 64) {
      IExpr x = null;
      IExpr y = null;
      int n = 0;
      search: for (IExpr xi : ng) {
        for (IExpr yi : dg) {
          IExpr difference = eval(F.Expand(F.Subtract(xi, F.Times(F.C2, yi))));
          if (difference.isInteger()) {
            int ni = difference.toIntDefault();
            if (ni != Integer.MIN_VALUE && Math.abs(ni) <= 64) {
              x = xi;
              y = yi;
              n = ni;
              break search;
            }
          }
        }
      }
      if (x == null) {
        break;
      }
      ng.remove(x);
      dg.remove(y);
      if (n > 0) {
        // no.extend(2*y + k for k in range(n))
        for (int k = 0; k < n; k++) {
          no.add(eval(F.Plus(F.Times(F.C2, y), F.ZZ(k))));
        }
      } else if (n < 0) {
        // do.extend(2*y - 1 - k for k in range(-n))
        for (int k = 0; k < -n; k++) {
          doList.add(eval(F.Plus(F.Times(F.C2, y), F.CN1, F.ZZ(-k))));
        }
      }
      ng.add(eval(F.Plus(y, F.C1D2)));
      no.add(F.Power(F.C2, F.Plus(F.Times(F.C2, y), F.CN1)));
      doList.add(F.Sqrt(S.Pi));
    }
  }

  /**
   * Find runs in coeffs such that the difference in terms (mod 1) of t1, t2, ..., tn is 1/n
   *
   * @return <code>null</code> if no run was found, otherwise the list with the elements
   *         <code>n, t1, t2, ..., tn</code>; the terms are removed from <code>coeffs</code>
   */
  private static List<IRational> run(List<IRational> coeffs) {
    List<IRational> u = new ArrayList<IRational>();
    for (IRational c : coeffs) {
      if (!u.contains(c)) {
        u.add(c);
      }
    }
    for (int i = 0; i < u.size(); i++) {
      // dj = ([((u[j] - u[i]) % 1, j) for j in range(i + 1, len(u))])
      List<IRational> dj = new ArrayList<IRational>();
      for (int j = i + 1; j < u.size(); j++) {
        IRational difference = u.get(j).subtract(u.get(i));
        dj.add(difference.subtract(difference.floor()));
      }
      for (IRational one : dj) {
        if (one.numerator().isOne() && !one.denominator().isOne()) {
          int n = one.denominator().toIntDefault();
          if (n < 2 || n > 64) {
            continue;
          }
          List<Integer> got = new ArrayList<Integer>();
          got.add(i);
          List<Integer> get = new ArrayList<Integer>();
          for (int k = 1; k < n; k++) {
            get.add(k);
          }
          for (int j = 0; j < dj.size() && !get.isEmpty(); j++) {
            IRational m = dj.get(j).multiply(F.ZZ(n));
            if (m.isInteger()) {
              Integer value = m.toIntDefault();
              if (get.remove(value)) {
                got.add(i + 1 + j);
              }
            }
          }
          if (!get.isEmpty()) {
            continue;
          }
          List<IRational> result = new ArrayList<IRational>();
          result.add(F.ZZ(n));
          for (int index : got) {
            IRational c = u.get(index);
            coeffs.remove(c);
            result.add(c);
          }
          return result;
        }
      }
    }
    return null;
  }

  /**
   * Try to reduce the number of gamma factors by applying the multiplication theorem (used when n
   * gammas with args differing by 1/n mod 1 are encountered).
   */
  private static void multiplicationTheorem(List<IExpr> gammas, List<IExpr> numer) {
    // run of 2 with args differing by 1/2
    //
    // >>> gammasimp(gamma(x)*gamma(x+S.Half))
    // 2*sqrt(2)*2**(-2*x - 1/2)*sqrt(pi)*gamma(2*x)
    //
    // run of 3 args differing by 1/3 (mod 1)
    //
    // >>> gammasimp(gamma(x)*gamma(x+S(1)/3)*gamma(x+S(2)/3))
    // 6*3**(-3*x - 1/2)*pi*gamma(3*x)

    // pull off and analyze the leading coefficient from each gamma arg
    // looking for runs in those Rationals
    // expr -> coeff + resid -> rats[resid] = coeff
    Map<IExpr, List<IRational>> rats = new LinkedHashMap<IExpr, List<IRational>>();
    for (IExpr g : gammas) {
      IExpr[] split = asCoeffAdd(g);
      rats.computeIfAbsent(split[1], k -> new ArrayList<IRational>()).add((IRational) split[0]);
    }
    List<IExpr> result = new ArrayList<IExpr>();
    // look for runs in Rationals for each resid
    for (Map.Entry<IExpr, List<IRational>> entry : rats.entrySet()) {
      IExpr resid = entry.getKey();
      List<IRational> coeffs = entry.getValue();
      coeffs.sort((x, y) -> x.compareTo(y));
      List<IExpr> created = new ArrayList<IExpr>();
      while (!resid.isZero()) {
        List<IRational> run = run(coeffs);
        if (run == null) {
          break;
        }
        // process the sequence that was found:
        // 1) convert all the gamma functions to have the right
        // argument (could be off by an integer)
        // 2) append the factors corresponding to the theorem
        // 3) append the new gamma function
        int n = run.get(0).toIntDefault();
        IRational ui = run.get(1);
        // (1)
        for (int i = 2; i < run.size(); i++) {
          IRational u = run.get(i);
          IExpr con = eval(F.Plus(resid, u, F.CN1));
          int steps = u.subtract(ui).floor().toIntDefault();
          for (int k = 0; k < steps; k++) {
            numer.add(eval(F.Subtract(con, F.ZZ(k))));
          }
        }
        // for (2) and (3)
        IExpr con = eval(F.Expand(F.Times(F.ZZ(n), F.Plus(resid, ui))));
        // (2)
        numer.add(F.Times(F.Power(F.Times(F.C2, S.Pi), F.QQ(n - 1, 2)),
            F.Power(F.ZZ(n), F.Subtract(F.C1D2, con))));
        // (3)
        created.add(con);
      }
      // restore resid to coeffs
      for (IRational c : coeffs) {
        result.add(eval(F.Plus(resid, c)));
      }
      result.addAll(created);
    }
    gammas.clear();
    gammas.addAll(result);
  }

  /**
   * Find an element in the list which is equal to <code>x</code> up to a numeric factor.
   */
  private static IExpr findFuzzy(List<IExpr> list, IExpr x) {
    if (x.isNumber()) {
      return null;
    }
    for (IExpr y : list) {
      if (y.isNumber()) {
        continue;
      }
      if (y.equals(x)) {
        return y;
      }
      IExpr quotient = eval(F.Cancel(F.Times(x, F.Power(y, F.CN1))));
      if (quotient.isRational() && !quotient.isZero()) {
        return y;
      }
    }
    return null;
  }

  /**
   * Try to absorb factors into the gammas: <code>x*gamma(x) -&gt; gamma(x + 1)</code> and
   * <code>gamma(x)/(x - 1) -&gt; gamma(x - 1)</code>
   */
  private static void absorbFactors(List<IExpr> gammas, List<IExpr> numer, List<IExpr> denom) {
    List<IExpr> result = new ArrayList<IExpr>();
    while (!gammas.isEmpty()) {
      IExpr g = gammas.remove(gammas.size() - 1);
      boolean cont = true;
      int counter = 0;
      while (cont && counter++ < 128) {
        cont = false;
        IExpr y = findFuzzy(numer, g);
        if (y != null) {
          numer.remove(y);
          if (!y.equals(g)) {
            numer.add(eval(F.Cancel(F.Times(y, F.Power(g, F.CN1)))));
          }
          g = eval(F.Plus(g, F.C1));
          cont = true;
        }
        IExpr gMinus1 = eval(F.Plus(g, F.CN1));
        y = findFuzzy(denom, gMinus1);
        if (y != null) {
          denom.remove(y);
          if (!y.equals(gMinus1)) {
            numer.add(eval(F.Cancel(F.Times(gMinus1, F.Power(y, F.CN1)))));
          }
          g = gMinus1;
          cont = true;
        }
      }
      result.add(g);
    }
    gammas.addAll(result);
  }

  private static int countGamma(IExpr expr) {
    if (!expr.isAST()) {
      return 0;
    }
    IAST ast = (IAST) expr;
    int result = ast.isAST(S.Gamma, 2) ? 1 : 0;
    for (int i = 0; i < ast.size(); i++) {
      result += countGamma(ast.get(i));
    }
    return result;
  }

  /**
   * Simplify a sum of terms with gamma functions. The gamma functions in the terms are normalized
   * to a common argument, so that the common gamma functions can be factored out.
   */
  private static IExpr ruleGammaPlus(IAST plus) {
    final int gammas = countGamma(plus);
    if (gammas < 2) {
      return plus;
    }
    IASTAppendable sum = F.PlusAlloc(plus.argSize());
    for (int i = 1; i < plus.size(); i++) {
      sum.append(ruleGamma(plus.get(i), false));
    }
    IExpr factored = eval(F.Factor(sum));
    if (factored.isTimes() || isGammaPower(factored)) {
      factored = ruleGamma(factored, true);
    }
    return countGamma(factored) < gammas ? factored : plus;
  }

  /**
   * Simplify a product of gamma functions and other factors.
   *
   * @param absorb if <code>true</code> try to absorb factors into the gamma functions
   */
  private static IExpr ruleGamma(IExpr expr, boolean absorb) {
    if (expr.isFree(S.Gamma, true)) {
      return expr;
    }
    IAST factors = expr.isTimes() ? (IAST) expr : F.Times(expr);
    List<IExpr> numerGammas = new ArrayList<IExpr>();
    List<IExpr> denomGammas = new ArrayList<IExpr>();
    List<IExpr> numerOthers = new ArrayList<IExpr>();
    List<IExpr> denomOthers = new ArrayList<IExpr>();
    for (int i = 1; i < factors.size(); i++) {
      explicate(factors.get(i), numerGammas, denomGammas, numerOthers, denomOthers);
    }
    if (numerGammas.isEmpty() && denomGammas.isEmpty()) {
      return expr;
    }

    // gamma(x + n) -> gamma(x)*x*(x + 1)*...
    normalize(numerGammas, numerOthers, denomOthers);
    normalize(denomGammas, denomOthers, numerOthers);
    cancel(numerGammas, denomGammas);

    // =========== level 2 work: pure gamma manipulation =========
    reflection(numerGammas, numerOthers, denomOthers);
    reflection(denomGammas, denomOthers, numerOthers);

    duplication(numerGammas, denomGammas, numerOthers, denomOthers);
    duplication(denomGammas, numerGammas, denomOthers, numerOthers);

    multiplicationTheorem(numerGammas, numerOthers);
    multiplicationTheorem(denomGammas, denomOthers);
    cancel(numerGammas, denomGammas);

    // =========== level >= 2 work: factor absorption =========
    cancel(numerOthers, denomOthers);
    if (absorb) {
      absorbFactors(numerGammas, numerOthers, denomOthers);
      absorbFactors(denomGammas, denomOthers, numerOthers);
    }

    // =========== rebuild expr ==================================
    IASTAppendable result = F.TimesAlloc(
        numerGammas.size() + denomGammas.size() + numerOthers.size() + denomOthers.size());
    for (IExpr g : numerGammas) {
      result.append(F.Gamma(g));
    }
    for (IExpr g : denomGammas) {
      result.append(F.Power(F.Gamma(g), F.CN1));
    }
    for (IExpr x : numerOthers) {
      result.append(x);
    }
    for (IExpr x : denomOthers) {
      result.append(F.Power(x, F.CN1));
    }
    return eval(result);
  }
}
