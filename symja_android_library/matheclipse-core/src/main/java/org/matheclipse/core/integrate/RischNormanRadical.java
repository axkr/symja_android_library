package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Parallel (Risch-Norman) integration over a tower of <code>Log</code>, <code>Exp</code> and
 * <code>Tan</code> generators with one square root <code>y = Sqrt(q)</code> in it, after S. Blake,
 * <i>Parallel Integration over Simple Radical Extensions</i> (I and II: mixed towers).
 *
 * <p>
 * The integrand is rewritten innermost first: every transcendental building block becomes a
 * generator <code>t</code> with a derivative rational in the generators below it; a root whose
 * radicand is linear in one generator with a constant coefficient (<code>Sqrt(Log(x))</code>,
 * <code>(x + Log(x))^(1/3)</code>) is flattened, i.e. replaces that generator; and the square roots
 * of one remaining polynomial radicand <code>q</code> become powers of <code>y</code>, with
 * <code>y^2 = q</code> and <code>D(y) = D(q)/(2*y)</code>. The radical may sit anywhere in the
 * tower: a generator above it, such as <code>Log(x + Sqrt(x^2 + 1))</code>, differentiates through
 * <code>y</code>.
 *
 * <p>
 * The ansatz is <code>(P0 + P1*y)/C + sum(b_j*Log(L_j)) + sum(a_k*ArcTan(M_k))</code> with the
 * denominator <code>C</code> of the integrand, written as <code>(A + B*y)/C</code>, and logands from
 * the factors of <code>C</code>, the generators, and the factors which are norms
 * <code>a^2 &plusmn; b^2*q</code>. Its derivative minus the integrand is reduced modulo <code>y^2 - q</code>; the parts
 * free of <code>y</code> and in <code>y</code> must vanish separately, which is one linear system.
 * A solution is differentiated back before it is returned: the method is a heuristic, and a wrong
 * answer is never emitted.
 */
final class RischNormanRadical {

  /** At most this many generators, flattened roots included. */
  private static final int MAX_GENERATORS = 4;

  /** Maximum number of undetermined coefficients. */
  private static final int MAX_UNKNOWNS = 400;

  /** Largest size, in bits, of an entry of the linear system during the elimination. */
  private static final int MAX_BITS = 2048;

  /** A generator of the tower: its symbol, what it stands for, and its derivative. */
  private static final class Generator {
    final ISymbol symbol;
    /** the surface expression in x */
    IExpr surface;
    /** D(symbol) in terms of x, the generators and (still unreplaced) roots */
    IExpr derivative;
    /** the head of the building block: Log, Exp or Tan, or null for a flattened root */
    final ISymbol kind;

    Generator(ISymbol symbol, IExpr surface, IExpr derivative, ISymbol kind) {
      this.symbol = symbol;
      this.surface = surface;
      this.derivative = derivative;
      this.kind = kind;
    }
  }

  /** Signals that the integrand is outside the class, so the method declines. */
  private static final class Decline extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Decline() {
      super(null, null, false, false);
    }
  }

  private final IExpr x;
  private final EvalEngine engine;
  private final long deadline;
  private final List<Generator> generators = new ArrayList<Generator>();
  private int symbolCounter = 0;

  private RischNormanRadical(IExpr x, EvalEngine engine, long deadline) {
    this.x = x;
    this.engine = engine;
    this.deadline = deadline;
  }

  /** Does <code>f</code> have a root depending on <code>x</code>, so this method applies? */
  static boolean hasRoot(IExpr f, IExpr x) {
    return !f.isFree(e -> e.isPower() && !e.exponent().isInteger() && e.exponent().isRational()
        && !e.base().isFree(x, true), true);
  }

  /**
   * @return the antiderivative, verified by differentiation, or {@link F#NIL}
   */
  static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine, long deadline) {
    IExpr key = F.List(integrand, x);
    java.util.Map<IExpr, Boolean> declined = DECLINED.get();
    if (declined.containsKey(key)) {
      return F.NIL;
    }
    IExpr result;
    try {
      result = new RischNormanRadical(x, engine, deadline).integrate(integrand);
    } catch (Decline d) {
      result = F.NIL;
    }
    if (result.isNIL() && System.currentTimeMillis() <= deadline
        && !Thread.currentThread().isInterrupted()) {
      // a decline which did not come from the clock is a property of the integrand
      declined.put(key, Boolean.TRUE);
    }
    return result;
  }

  /** Integrands this thread has already seen the method decline, most recent last. */
  private static final ThreadLocal<java.util.Map<IExpr, Boolean>> DECLINED =
      ThreadLocal.withInitial(() -> new java.util.LinkedHashMap<IExpr, Boolean>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<IExpr, Boolean> eldest) {
          return size() > 64;
        }
      });

  private IExpr integrate(IExpr integrand) {
    IExpr g = convert(commonExponentials(integrand));
    // flatten the roots whose radicand is linear in a generator
    boolean flattened = true;
    while (flattened) {
      checkTime();
      flattened = flattenOneRoot(g);
      if (flattened) {
        g = flattenedIntegrand;
      }
    }
    // the square roots of one remaining radicand become powers of y
    IExpr q = F.NIL;
    ISymbol y = null;
    List<IExpr> roots = new ArrayList<IExpr>();
    collectRoots(g, roots);
    for (Generator gen : generators) {
      collectRoots(gen.derivative, roots);
    }
    if (!roots.isEmpty()) {
      for (IExpr root : roots) {
        IRational e = (IRational) root.exponent();
        if (!e.denominator().equals(F.C2)) {
          throw new Decline();
        }
        IExpr base = engine.evaluate(F.Expand(root.base()));
        if (q.isNIL()) {
          q = base;
        } else if (!q.equals(base)) {
          // more than one radical: outside the class
          throw new Decline();
        }
      }
      if (!isPolynomialInVariables(q) || isPerfectSquare(q)) {
        throw new Decline();
      }
      y = F.Dummy("rny");
      g = replaceRoots(g, q, y);
      for (Generator gen : generators) {
        gen.derivative = replaceRoots(gen.derivative, q, y);
      }
    }
    if (generators.isEmpty() || generators.size() > MAX_GENERATORS) {
      throw new Decline();
    }
    IExpr solution = solve(g, q, y);
    if (solution.isNIL()) {
      return F.NIL;
    }
    // back to the surface: y -> Sqrt(q), the generators -> their building blocks
    IASTAppendable rules = F.ListAlloc(generators.size() + 1);
    if (y != null) {
      rules.append(F.Rule(y, F.Sqrt(surface(q))));
    }
    for (Generator gen : generators) {
      rules.append(F.Rule(gen.symbol, gen.surface));
    }
    IExpr result = engine.evaluate(F.ReplaceAll(solution, rules));
    return verify(result, integrand) ? result : F.NIL;
  }

  // ---------------------------------------------------------------------------------------------
  // the tower

  /**
   * Write exponentials whose exponents are rational multiples of each other as integer powers of
   * one of them - <code>E^(2*x)</code> as <code>(E^x)^2</code> - so that they are one generator:
   * the generators of the tower must be algebraically independent.
   */
  private IExpr commonExponentials(IExpr f) {
    List<IExpr> exponents = new ArrayList<IExpr>();
    collectExponents(f, exponents);
    if (exponents.size() < 2) {
      return f;
    }
    java.util.Map<IExpr, IExpr> rules = new java.util.HashMap<IExpr, IExpr>();
    boolean[] done = new boolean[exponents.size()];
    for (int i = 0; i < exponents.size(); i++) {
      if (done[i]) {
        continue;
      }
      IExpr first = exponents.get(i);
      List<Integer> group = new ArrayList<Integer>();
      List<IRational> ratios = new ArrayList<IRational>();
      for (int j = i; j < exponents.size(); j++) {
        IExpr ratio = engine.evaluate(F.Together(F.Divide(exponents.get(j), first)));
        if (!done[j] && ratio.isRational()) {
          group.add(j);
          ratios.add((IRational) ratio);
        }
      }
      if (group.size() < 2) {
        continue;
      }
      // the base exponent: first * gcd of the ratios
      IExpr numeratorGcd = F.C0;
      IExpr denominatorLcm = F.C1;
      for (IRational r : ratios) {
        numeratorGcd = engine.evaluate(F.GCD(numeratorGcd, r.numerator()));
        denominatorLcm = engine.evaluate(F.LCM(denominatorLcm, r.denominator()));
      }
      IExpr unit = engine.evaluate(F.Divide(numeratorGcd, denominatorLcm));
      IExpr base = engine.evaluate(F.Times(first, unit));
      for (int k = 0; k < group.size(); k++) {
        int j = group.get(k);
        done[j] = true;
        IExpr n = engine.evaluate(F.Divide(ratios.get(k), unit));
        rules.put(F.Power(S.E, exponents.get(j)), F.Power(F.Power(S.E, base), n));
      }
    }
    if (rules.isEmpty()) {
      return f;
    }
    // held: Power(Power(E, base), n) must not evaluate back into E^(n*base)
    ISymbol hold = F.Dummy("rnexp");
    IExpr replaced = f.replaceAll(e -> {
      IExpr r = rules.get(e);
      return r == null ? F.NIL : F.Power(F.unaryAST1(hold, r.base().exponent()), r.exponent());
    }).orElse(f);
    expHold = hold;
    return replaced;
  }

  /** The placeholder head of a held E^u, see {@link #commonExponentials(IExpr)}. */
  private ISymbol expHold;

  private static void collectExponents(IExpr e, List<IExpr> exponents) {
    if (e.isPower() && e.base().isE()) {
      if (!exponents.contains(e.exponent())) {
        exponents.add(e.exponent());
      }
    }
    if (e.isAST()) {
      IAST ast = (IAST) e;
      for (int i = 1; i < ast.size(); i++) {
        collectExponents(ast.get(i), exponents);
      }
    }
  }

  /** The integrand in terms of x and the generators, innermost first; roots stay as powers. */
  private IExpr convert(IExpr expr) {
    if (expr.isFree(x, true)) {
      return expr;
    }
    if (expr.equals(x)) {
      return x;
    }
    if (expr.isPlus() || expr.isTimes()) {
      IAST ast = (IAST) expr;
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      for (int i = 1; i < ast.size(); i++) {
        result.append(convert(ast.get(i)));
      }
      return result;
    }
    if (expr.isPower()) {
      IExpr base = expr.base();
      IExpr exponent = expr.exponent();
      if (base.isE()) {
        return generator(expr, S.Exp, convert(exponent));
      }
      if (!exponent.isFree(x, true)) {
        throw new Decline();
      }
      if ((base.isAST(S.Sec, 2) || base.isAST(S.Cos, 2)) && exponent.isInteger()
          && ((org.matheclipse.core.interfaces.IInteger) exponent).isEven()) {
        // Sec(u)^(2k) = (1 + Tan(u)^2)^k and Cos(u)^(2k) = (1 + Tan(u)^2)^(-k)
        int k = exponent.toIntDefault() / 2;
        IExpr tan = convert(F.Tan(base.first()));
        return F.Power(F.Plus(F.C1, F.Sqr(tan)), F.ZZ(base.isAST(S.Sec, 2) ? k : -k));
      }
      if (exponent.isInteger() || exponent.isRational()) {
        return F.Power(convert(base), exponent);
      }
      throw new Decline();
    }
    if (expHold != null && expr.isAST(expHold, 2)) {
      IExpr surface = F.Power(S.E, expr.first());
      return generator(surface, S.Exp, convert(expr.first()));
    }
    if (expr.isAST(S.Log, 2)) {
      return generator(expr, S.Log, convert(expr.first()));
    }
    if (expr.isAST(S.Tan, 2)) {
      return generator(expr, S.Tan, convert(expr.first()));
    }
    throw new Decline();
  }

  /** The generator for a building block with the converted argument <code>u</code>. */
  private ISymbol generator(IExpr surface, ISymbol kind, IExpr u) {
    for (Generator gen : generators) {
      if (gen.surface.equals(surface)) {
        return gen.symbol;
      }
    }
    if (generators.size() >= MAX_GENERATORS) {
      throw new Decline();
    }
    ISymbol t = F.Dummy("rnt" + symbolCounter++);
    IExpr du = derivative(u);
    IExpr dt;
    if (kind == S.Log) {
      dt = F.Divide(du, u);
    } else if (kind == S.Exp) {
      dt = F.Times(t, du);
    } else {
      dt = F.Times(F.Plus(F.C1, F.Sqr(t)), du);
    }
    generators.add(new Generator(t, surface, engine.evaluate(dt), kind));
    return t;
  }

  /** The derivation of the tower, by the chain rule through x and every generator. */
  private IExpr derivative(IExpr e) {
    IASTAppendable sum = F.PlusAlloc(generators.size() + 1);
    sum.append(F.D(e, x));
    for (Generator gen : generators) {
      if (!e.isFree(gen.symbol)) {
        sum.append(F.Times(F.D(e, gen.symbol), gen.derivative));
      }
    }
    return engine.evaluate(sum);
  }

  private IExpr flattenedIntegrand;

  /**
   * Replace one generator <code>t</code> by a new one <code>u = (c*t + r)^(1/m)</code> when a root
   * of <code>c*t + r</code> occurs, with <code>c</code> constant and <code>r</code> free of
   * <code>t</code>: then <code>t = (u^m - r)/c</code> and the root is a power of <code>u</code>.
   */
  private boolean flattenOneRoot(IExpr g) {
    List<IExpr> roots = new ArrayList<IExpr>();
    collectRoots(g, roots);
    for (Generator gen : generators) {
      collectRoots(gen.derivative, roots);
    }
    for (IExpr root : roots) {
      IExpr base = engine.evaluate(F.Expand(root.base()));
      for (Generator gen : generators) {
        ISymbol t = gen.symbol;
        if (base.isFree(t)) {
          continue;
        }
        IExpr c = engine.evaluate(F.Coefficient(base, t, F.C1));
        IExpr r = engine.evaluate(F.Expand(F.Subtract(base, F.Times(c, t))));
        if (!isConstant(c) || c.isZero() || !r.isFree(t)) {
          continue;
        }
        // the denominator of all exponents of this radicand
        long m = 1;
        List<IExpr> sameBase = new ArrayList<IExpr>();
        collectRoots(g, sameBase);
        for (Generator other : generators) {
          collectRoots(other.derivative, sameBase);
        }
        for (IExpr other : sameBase) {
          if (engine.evaluate(F.Expand(other.base())).equals(base)) {
            long d = ((IRational) other.exponent()).denominator().toLong();
            m = lcm(m, d);
          }
        }
        ISymbol u = F.Dummy("rnu" + symbolCounter++);
        IExpr tOfU = F.Divide(F.Subtract(F.Power(u, F.ZZ(m)), r), c);
        // D(u) = D(base)/(m*u^(m-1)), with D(base) = c*D(t) + D(r)
        IExpr dBase = F.Plus(F.Times(c, gen.derivative), derivative(r));
        IExpr du = F.Divide(dBase, F.Times(F.ZZ(m), F.Power(u, F.ZZ(m - 1))));
        IExpr surfaceBase = surface(base);
        final long mm = m;
        java.util.function.Function<IExpr, IExpr> rewrite = e -> {
          IExpr replaced = e.replaceAll(p -> {
            if (p.isPower() && p.exponent().isRational() && !p.exponent().isInteger()
                && engine.evaluate(F.Expand(p.base())).equals(base)) {
              IRational ex = (IRational) p.exponent();
              return F.Power(u, ex.multiply(F.ZZ(mm)));
            }
            return F.NIL;
          }).orElse(e);
          return engine.evaluate(F.subst(replaced, t, tOfU));
        };
        IExpr newIntegrand = rewrite.apply(g);
        IExpr newDu = rewrite.apply(du);
        for (Generator other : generators) {
          if (other != gen) {
            other.derivative = rewrite.apply(other.derivative);
          }
        }
        generators.remove(gen);
        generators.add(new Generator(u, F.Power(surfaceBase, F.QQ(1, mm)), newDu, null));
        flattenedIntegrand = newIntegrand;
        return true;
      }
    }
    return false;
  }

  private static long lcm(long a, long b) {
    long g = java.math.BigInteger.valueOf(a).gcd(java.math.BigInteger.valueOf(b)).longValue();
    return a / g * b;
  }

  private static void collectRoots(IExpr e, List<IExpr> roots) {
    if (e.isPower() && e.exponent().isRational() && !e.exponent().isInteger()) {
      roots.add(e);
    }
    if (e.isAST()) {
      IAST ast = (IAST) e;
      for (int i = 1; i < ast.size(); i++) {
        collectRoots(ast.get(i), roots);
      }
    }
  }

  private IExpr replaceRoots(IExpr e, IExpr q, ISymbol y) {
    IExpr replaced = e.replaceAll(p -> {
      if (p.isPower() && p.exponent().isRational() && !p.exponent().isInteger()
          && engine.evaluate(F.Expand(p.base())).equals(q)) {
        IRational ex = (IRational) p.exponent();
        return F.Power(y, ex.multiply(F.C2));
      }
      return F.NIL;
    }).orElse(e);
    return engine.evaluate(replaced);
  }

  /** An expression in x and the generator symbols, written back in x. */
  private IExpr surface(IExpr e) {
    IASTAppendable rules = F.ListAlloc(generators.size());
    for (Generator gen : generators) {
      rules.append(F.Rule(gen.symbol, gen.surface));
    }
    return engine.evaluate(F.ReplaceAll(e, rules));
  }

  private boolean isConstant(IExpr e) {
    if (!e.isFree(x, true)) {
      return false;
    }
    for (Generator gen : generators) {
      if (!e.isFree(gen.symbol)) {
        return false;
      }
    }
    return true;
  }

  private IAST variables() {
    IASTAppendable vars = F.ListAlloc(generators.size() + 1);
    vars.append(x);
    for (Generator gen : generators) {
      vars.append(gen.symbol);
    }
    return vars;
  }

  private boolean isPolynomialInVariables(IExpr e) {
    return engine.evaluate(F.PolynomialQ(e, variables())).isTrue();
  }

  private boolean isPerfectSquare(IExpr e) {
    return squareRoot(e).isPresent();
  }

  /** <code>b</code> with <code>b^2 == e</code>, for a polynomial <code>e</code>, or NIL. */
  private IExpr squareRoot(IExpr e) {
    if (e.isZero()) {
      return F.C0;
    }
    IExpr list = engine.evaluate(F.FactorSquareFreeList(e));
    if (!list.isList()) {
      return F.NIL;
    }
    IASTAppendable root = F.TimesAlloc(list.argSize());
    for (IExpr pair : (IAST) list) {
      if (!pair.isList2()) {
        return F.NIL;
      }
      IExpr factor = pair.first();
      int exponent = pair.second().toIntDefault();
      if (factor.isNumber()) {
        IExpr sqrt = engine.evaluate(F.Sqrt(F.Power(factor, F.ZZ(exponent))));
        if (!sqrt.isRational()) {
          return F.NIL;
        }
        root.append(sqrt);
      } else {
        if (exponent % 2 != 0) {
          return F.NIL;
        }
        root.append(F.Power(factor, F.ZZ(exponent / 2)));
      }
    }
    IExpr b = engine.evaluate(root.oneIdentity1());
    // checked, so that a factorization which loses a sign cannot make -t^2 a square
    return engine.evaluate(F.Expand(F.Subtract(F.Sqr(b), e))).isZero() ? b : F.NIL;
  }

  // ---------------------------------------------------------------------------------------------
  // the ansatz

  /** <code>(u + v*y)/w</code> with polynomials <code>u, v, w</code> and <code>w</code> free of y. */
  private static final class Quotient {
    final IExpr u;
    final IExpr v;
    final IExpr w;

    Quotient(IExpr u, IExpr v, IExpr w) {
      this.u = u;
      this.v = v;
      this.w = w;
    }
  }

  /** Write <code>e</code> as <code>(u + v*y)/w</code>, using <code>y^2 = q</code>. */
  private Quotient rationalize(IExpr e, IExpr q, ISymbol y) {
    IExpr together = engine.evaluate(F.Together(e));
    IExpr numerator = engine.evaluate(F.Expand(F.Numerator(together)));
    IExpr denominator = engine.evaluate(F.Expand(F.Denominator(together)));
    if (y != null) {
      numerator = reduce(numerator, q, y);
      denominator = reduce(denominator, q, y);
      if (!denominator.isFree(y)) {
        IExpr d0 = engine.evaluate(F.Coefficient(denominator, y, F.C0));
        IExpr d1 = engine.evaluate(F.Coefficient(denominator, y, F.C1));
        numerator = reduce(engine.evaluate(F.Expand(F.Times(numerator, F.Subtract(d0,
            F.Times(d1, y))))), q, y);
        denominator = engine.evaluate(F.Expand(F.Subtract(F.Sqr(d0), F.Times(F.Sqr(d1), q))));
      }
      return new Quotient(engine.evaluate(F.Coefficient(numerator, y, F.C0)),
          engine.evaluate(F.Coefficient(numerator, y, F.C1)), denominator);
    }
    return new Quotient(numerator, F.C0, denominator);
  }

  /** A polynomial in y reduced modulo <code>y^2 - q</code>, so of degree one at most. */
  private IExpr reduce(IExpr polynomial, IExpr q, ISymbol y) {
    if (polynomial.isFree(y)) {
      return polynomial;
    }
    return engine.evaluate(F.PolynomialRemainder(polynomial, F.Subtract(F.Sqr(y), q), y));
  }

  /** <code>a/b</code> for polynomials of which <code>b</code> divides <code>a</code>. */
  private IExpr exactQuotient(IExpr a, IExpr b) {
    return engine.evaluate(F.Expand(F.Cancel(F.Divide(a, b))));
  }

  private IExpr lcm(IExpr a, IExpr b) {
    if (a.isOne()) {
      return b;
    }
    if (b.isOne()) {
      return a;
    }
    return engine.evaluate(F.Expand(F.binaryAST2(S.PolynomialLCM, a, b)));
  }

  private IExpr solve(IExpr g, IExpr q, ISymbol y) {
    IExpr result = solve(g, q, y, 1);
    if (result.isNIL()) {
      // a denominator with a generator in it can need a numerator of a higher degree
      result = solve(g, q, y, 2);
    }
    return result;
  }

  private IExpr solve(IExpr g, IExpr q, ISymbol y, int extraDegree) {
    IAST vars = variables();
    // the integrand (A + B*y)/C and the derivations of the generators and of y in the same form
    Quotient integrand = rationalize(g, q, y);
    IExpr c = integrand.w;
    Quotient[] derivations = new Quotient[generators.size()];
    for (int i = 0; i < derivations.length; i++) {
      derivations[i] = rationalize(generators.get(i).derivative, q, y);
    }
    // D(y) = D(q)/(2*y) = D(q)*y/(2*q)
    Quotient dy = y == null ? null
        : rationalize(F.Divide(F.Times(derivative(q), y), F.Times(F.C2, q)), q, y);
    if (!isPolynomialInVariables(c) || !isPolynomialInVariables(integrand.u)
        || !isPolynomialInVariables(integrand.v)) {
      throw new Decline();
    }
    checkTime();

    // degree bounds: above the integrand in every variable
    int[] bounds = new int[vars.argSize()];
    long count = y == null ? 1 : 2;
    for (int v = 0; v < bounds.length; v++) {
      IExpr var = vars.get(v + 1);
      int bound = Math.max(degree(c, var),
          Math.max(degree(integrand.u, var), degree(integrand.v, var)));
      if (bound < 0) {
        throw new Decline();
      }
      bounds[v] = bound + extraDegree;
      count *= bounds[v] + 1;
      if (count > MAX_UNKNOWNS) {
        throw new Decline();
      }
    }
    // the monomials of the numerator of the rational part: P0 + P1*y
    List<IExpr> monomials = new ArrayList<IExpr>();
    buildMonomials(vars, bounds, new int[bounds.length], 0, monomials);
    if (y != null) {
      int n = monomials.size();
      for (int k = 0; k < n; k++) {
        monomials.add(engine.evaluate(F.Times(monomials.get(k), y)));
      }
    }
    List<IExpr> terms = logarithmicTerms(c, q, y);
    if (monomials.size() + terms.size() > MAX_UNKNOWNS) {
      throw new Decline();
    }

    // every part of the equation is multiplied by the common denominator W
    // L = lcm of the denominators of the derivations, W0 = C^2*L, W = lcm(W0, term denominators)
    IExpr l = F.C1;
    for (Quotient d : derivations) {
      l = lcm(l, d.w);
    }
    if (dy != null) {
      l = lcm(l, dy.w);
    }
    List<Quotient> termDerivatives = new ArrayList<Quotient>();
    for (IExpr term : terms) {
      termDerivatives.add(rationalize(derivative(term, y, dy), q, y));
    }
    IExpr w0 = engine.evaluate(F.Expand(F.Times(F.Sqr(c), l)));
    IExpr w = w0;
    for (Quotient d : termDerivatives) {
      w = lcm(w, d.w);
    }
    IExpr wOverW0 = exactQuotient(w, w0);
    // the derivation times L, as factors of d/dt_i: (u_i + v_i*y)*L/w_i
    IExpr[] factors = new IExpr[derivations.length];
    for (int i = 0; i < derivations.length; i++) {
      factors[i] = engine.evaluate(F.Expand(F.Times(withY(derivations[i], y),
          exactQuotient(l, derivations[i].w))));
    }
    IExpr yFactor = dy == null ? F.C0
        : engine.evaluate(F.Expand(F.Times(withY(dy, y), exactQuotient(l, dy.w))));
    // D(C)*L
    IASTAppendable dcl = F.PlusAlloc(derivations.length + 1);
    dcl.append(F.Times(F.D(c, x), l));
    for (int i = 0; i < derivations.length; i++) {
      dcl.append(F.Times(F.D(c, generators.get(i).symbol), factors[i]));
    }
    IExpr dcL = engine.evaluate(F.Expand(dcl));
    checkTime();

    // the linear system: one column per unknown, one row per monomial of x, the generators and y
    SparseSystem system = new SparseSystem();
    int column = 0;
    for (IExpr m : monomials) {
      // W/W0 * (D(m)*L*C - m*D(C)*L), and D(P/C) = (D(P)*L*C - P*D(C)*L)/(C^2*L)
      IASTAppendable dml = F.PlusAlloc(derivations.length + 2);
      dml.append(F.Times(F.D(m, x), l));
      for (int i = 0; i < derivations.length; i++) {
        dml.append(F.Times(F.D(m, generators.get(i).symbol), factors[i]));
      }
      if (y != null) {
        dml.append(F.Times(F.D(m, y), yFactor));
      }
      IExpr contribution =
          F.Times(wOverW0, F.Subtract(F.Times(dml, c), F.Times(m, dcL)));
      system.addColumn(column++, polynomialRules(contribution, vars, q, y));
      checkTime();
    }
    for (Quotient d : termDerivatives) {
      IExpr contribution = F.Times(withY(d, y), exactQuotient(w, d.w));
      system.addColumn(column++, polynomialRules(contribution, vars, q, y));
    }
    // the right-hand side: the integrand times W
    system.addRightHandSide(
        polynomialRules(F.Times(withY(integrand, y), exactQuotient(w, c)), vars, q, y));
    checkTime();
    IRational[] solution = system.solve(column, deadline);
    if (solution == null) {
      return F.NIL;
    }
    IASTAppendable numerator = F.PlusAlloc(monomials.size());
    for (int k = 0; k < monomials.size(); k++) {
      if (!solution[k].isZero()) {
        numerator.append(F.Times(solution[k], monomials.get(k)));
      }
    }
    IASTAppendable result = F.PlusAlloc(terms.size() + 1);
    result.append(rationalPart(engine.evaluate(F.Expand(numerator.oneIdentity0())), c, q, y));
    for (int j = 0; j < terms.size(); j++) {
      IRational b = solution[monomials.size() + j];
      if (!b.isZero()) {
        result.append(F.Times(b, terms.get(j)));
      }
    }
    IExpr resolved = engine.evaluate(result.oneIdentity0());
    return resolved.isZero() ? F.NIL : resolved;
  }

  /**
   * <code>N/C</code>; for <code>N = N1*y</code> the smallest of <code>N1*y/C</code> and, when
   * <code>q</code> divides <code>C</code>, <code>N1/((C/q)*y)</code>: <code>E^x*Log(x)/Sqrt(1+x+x^2)</code> rather than
   * <code>E^x*Sqrt(1+x+x^2)*Log(x)/(1+x+x^2)</code>.
   */
  private IExpr rationalPart(IExpr numerator, IExpr c, IExpr q, ISymbol y) {
    IExpr plain = engine.evaluate(F.Cancel(F.Divide(numerator, c)));
    if (y != null && !numerator.isZero()
        && engine.evaluate(F.Coefficient(numerator, y, F.C0)).isZero()) {
      IExpr n1 = engine.evaluate(F.Coefficient(numerator, y, F.C1));
      // N1*y/C, or N1/((C/q)*y) when q divides C: the smaller of the two
      IExpr times = engine.evaluate(F.Times(F.Cancel(F.Divide(n1, c)), y));
      IExpr best = times.leafCount() < plain.leafCount() ? times : plain;
      IExpr cOverQ = engine.evaluate(F.Cancel(F.Divide(c, q)));
      if (isPolynomialInVariables(cOverQ)) {
        IExpr over = engine.evaluate(F.Divide(F.Cancel(F.Divide(n1, cOverQ)), y));
        if (over.leafCount() < best.leafCount()) {
          best = over;
        }
      }
      return best;
    }
    return plain;
  }

  private static IExpr withY(Quotient q, ISymbol y) {
    return y == null || q.v.isZero() ? q.u : F.Plus(q.u, F.Times(q.v, y));
  }

  /**
   * The coefficients of the expanded polynomial <code>e</code> in the variables and
   * <code>y</code>, with <code>y^2</code> replaced by <code>q</code>; keyed by the exponent vector.
   *
   * @throws Decline if a coefficient is not rational
   */
  private java.util.Map<String, IRational> polynomialRules(IExpr e, IAST vars, IExpr q,
      ISymbol y) {
    IExpr expanded = engine.evaluate(F.Expand(e));
    IAST all = vars;
    if (y != null) {
      if (!expanded.isFree(y)) {
        // y^n -> q^(n/2) or q^((n-1)/2)*y
        expanded = engine.evaluate(F.Expand(expanded.replaceAll(p -> {
          if (p.isPower() && p.base().equals(y) && p.exponent().isInteger()) {
            int n = p.exponent().toIntDefault();
            if (n >= 2) {
              return n % 2 == 0 ? F.Power(q, F.ZZ(n / 2))
                  : F.Times(F.Power(q, F.ZZ(n / 2)), y);
            }
          }
          return F.NIL;
        }).orElse(expanded)));
      }
      all = append(vars, y);
    }
    java.util.Map<String, IRational> result = new java.util.HashMap<String, IRational>();
    if (expanded.isZero()) {
      return result;
    }
    IExpr rules = engine.evaluate(F.CoefficientRules(expanded, all));
    if (!rules.isList()) {
      throw new Decline();
    }
    for (IExpr rule : (IAST) rules) {
      if (!rule.isRuleAST() || !rule.second().isRational()) {
        throw new Decline();
      }
      if (!rule.second().isZero()) {
        result.put(rule.first().toString(), (IRational) rule.second());
      }
    }
    return result;
  }

  /** A sparse linear system over the rationals, solved by Gauss-Jordan elimination. */
  private static final class SparseSystem {
    private final java.util.Map<String, java.util.Map<Integer, IRational>> rows =
        new java.util.LinkedHashMap<String, java.util.Map<Integer, IRational>>();
    private final java.util.Map<String, IRational> rhs = new java.util.HashMap<String, IRational>();

    void addColumn(int column, java.util.Map<String, IRational> entries) {
      for (java.util.Map.Entry<String, IRational> entry : entries.entrySet()) {
        rows.computeIfAbsent(entry.getKey(), k -> new java.util.HashMap<Integer, IRational>())
            .put(column, entry.getValue());
      }
    }

    void addRightHandSide(java.util.Map<String, IRational> entries) {
      for (java.util.Map.Entry<String, IRational> entry : entries.entrySet()) {
        rows.computeIfAbsent(entry.getKey(), k -> new java.util.HashMap<Integer, IRational>());
        rhs.put(entry.getKey(), entry.getValue());
      }
    }

    int rowCount() {
      return rows.size();
    }

    /** A solution, with the free unknowns zero, or <code>null</code> if there is none. */
    IRational[] solve(int columns, long deadline) {
      List<java.util.Map<Integer, IRational>> matrix =
          new ArrayList<java.util.Map<Integer, IRational>>();
      List<IRational> right = new ArrayList<IRational>();
      for (java.util.Map.Entry<String, java.util.Map<Integer, IRational>> row : rows.entrySet()) {
        matrix.add(new java.util.HashMap<Integer, IRational>(row.getValue()));
        IRational r = rhs.get(row.getKey());
        right.add(r == null ? F.C0 : r);
      }
      int[] pivotRow = new int[columns];
      java.util.Arrays.fill(pivotRow, -1);
      boolean[] used = new boolean[matrix.size()];
      for (int col = 0; col < columns; col++) {
        if (System.currentTimeMillis() > deadline) {
          return null;
        }
        int pivot = -1;
        for (int r = 0; r < matrix.size(); r++) {
          if (!used[r] && matrix.get(r).containsKey(col)) {
            pivot = r;
            break;
          }
        }
        if (pivot < 0) {
          continue;
        }
        used[pivot] = true;
        pivotRow[col] = pivot;
        java.util.Map<Integer, IRational> p = matrix.get(pivot);
        IRational inverse = p.get(col).inverse();
        for (java.util.Map.Entry<Integer, IRational> entry : p.entrySet()) {
          entry.setValue(entry.getValue().multiply(inverse));
        }
        right.set(pivot, right.get(pivot).multiply(inverse));
        if (Thread.currentThread().isInterrupted()) {
          return null;
        }
        for (int r = 0; r < matrix.size(); r++) {
          if (r == pivot) {
            continue;
          }
          java.util.Map<Integer, IRational> row = matrix.get(r);
          IRational factor = row.get(col);
          if (factor == null) {
            continue;
          }
          for (java.util.Map.Entry<Integer, IRational> entry : p.entrySet()) {
            int k = entry.getKey();
            IRational value = row.getOrDefault(k, F.C0).subtract(factor.multiply(entry.getValue()));
            if (value.toBigNumerator().bitLength() + value.toBigDenominator().bitLength() > MAX_BITS) {
              // the entries swell: this system is not one the heuristic should grind through
              return null;
            }
            if (value.isZero()) {
              row.remove(k);
            } else {
              row.put(k, value);
            }
          }
          right.set(r, right.get(r).subtract(factor.multiply(right.get(pivot))));
        }
      }
      for (int r = 0; r < matrix.size(); r++) {
        if (matrix.get(r).isEmpty() && !right.get(r).isZero()) {
          // inconsistent: no integral of this form
          return null;
        }
      }
      IRational[] solution = new IRational[columns];
      for (int col = 0; col < columns; col++) {
        solution[col] = pivotRow[col] < 0 ? F.C0 : right.get(pivotRow[col]);
      }
      return solution;
    }
  }

  /** The derivation of an expression in x, the generators and y. */
  private IExpr derivative(IExpr e, ISymbol y, Quotient dy) {
    IExpr d = derivative(e);
    if (y != null && !e.isFree(y)) {
      d = F.Plus(d, F.Times(F.D(e, y), F.Divide(F.Plus(dy.u, F.Times(dy.v, y)), dy.w)));
    }
    return engine.evaluate(d);
  }

  private static IAST append(IAST vars, IExpr v) {
    IASTAppendable result = vars.copyAppendable();
    result.append(v);
    return result;
  }

  /**
   * The logarithmic parts of the ansatz: <code>Log</code> of the factors of the denominator and of
   * the natural logands of the generators and the radical, and for a factor <code>p</code> which is
   * a norm <code>a^2 - b^2*q</code> (or <code>a^2 + b^2*q</code>) the term
   * <code>Log((a + b*y)/(a - b*y))</code> (or <code>ArcTan(b*y/a)</code>).
   */
  private List<IExpr> logarithmicTerms(IExpr denominator, IExpr q, ISymbol y) {
    Set<IExpr> terms = new LinkedHashSet<IExpr>();
    List<IExpr> factors = new ArrayList<IExpr>();
    if (!denominator.isFree(x, true) || !isConstant(denominator)) {
      IExpr list = engine.evaluate(F.FactorList(denominator));
      if (list.isList()) {
        for (IExpr pair : (IAST) list) {
          if (pair.isList2() && !pair.first().isNumber()) {
            factors.add(pair.first());
            terms.add(F.Log(pair.first()));
          }
        }
      }
    }
    for (Generator gen : generators) {
      if (gen.kind == S.Log) {
        terms.add(F.Log(gen.symbol));
      } else if (gen.kind == S.Tan) {
        terms.add(F.Log(F.Plus(F.C1, F.Sqr(gen.symbol))));
      }
    }
    if (y != null) {
      // Log(2*c*y + D(q)) for a radicand quadratic in x with a square leading coefficient
      if (isFreeOfGenerators(q) && degree(q, x) == 2) {
        IExpr leading = engine.evaluate(F.Coefficient(q, x, F.C2));
        IExpr c = engine.evaluate(F.Sqrt(leading));
        if (c.isRational() && ((IRational) c).isPositive()) {
          terms.add(F.Log(F.Plus(F.Times(F.C2, c, y), engine.evaluate(F.D(q, x)))));
        }
      }
      for (Generator gen : generators) {
        terms.add(F.Log(F.Plus(gen.symbol, y)));
      }
      for (IExpr p : factors) {
        // p = r + k*q; p is a norm when r = sign*a^2 and k = -sign*s*b^2 for signs sign, s:
        // p = sign*(a^2 - s*b^2*q)
        IExpr k = engine.evaluate(F.PolynomialQuotient(p, q, x));
        if (k.isZero()) {
          continue;
        }
        IExpr r = engine.evaluate(F.Expand(F.Subtract(p, F.Times(k, q))));
        for (int sign = 1; sign >= -1; sign -= 2) {
          IExpr a = r.isZero() ? F.NIL : squareRoot(engine.evaluate(F.Times(F.ZZ(sign), r)));
          if (a.isNIL() || a.isZero()) {
            continue;
          }
          IExpr b = squareRoot(engine.evaluate(F.Times(F.ZZ(-sign), k)));
          if (b.isPresent() && !b.isZero()) {
            // p = sign*(a + b*y)*(a - b*y); with Log(p) in the ansatz Log(a + b*y) spans the rest
            terms.add(F.Log(F.Plus(a, F.Times(b, y))));
            break;
          }
          b = squareRoot(engine.evaluate(F.Times(F.ZZ(sign), k)));
          if (b.isPresent() && !b.isZero()) {
            // p = sign*(a^2 + b^2*q)
            terms.add(F.ArcTan(F.Divide(F.Times(b, y), a)));
            break;
          }
        }
      }
    }
    return new ArrayList<IExpr>(terms);
  }

  private boolean isFreeOfGenerators(IExpr e) {
    for (Generator gen : generators) {
      if (!e.isFree(gen.symbol)) {
        return false;
      }
    }
    return true;
  }

  private int degree(IExpr e, IExpr var) {
    IExpr exponent = engine.evaluate(F.Exponent(e, var));
    int degree = exponent.toIntDefault();
    if (degree == Integer.MIN_VALUE || degree < 0) {
      return e.isFree(var, true) ? 0 : -1;
    }
    return degree;
  }

  private static void buildMonomials(IAST vars, int[] bounds, int[] exponents, int index,
      List<IExpr> monomials) {
    if (index == bounds.length) {
      IASTAppendable monomial = F.TimesAlloc(bounds.length);
      for (int v = 0; v < bounds.length; v++) {
        if (exponents[v] > 0) {
          monomial.append(F.Power(vars.get(v + 1), F.ZZ(exponents[v])));
        }
      }
      monomials.add(monomial.oneIdentity1());
      return;
    }
    for (int e = 0; e <= bounds[index]; e++) {
      exponents[index] = e;
      buildMonomials(vars, bounds, exponents, index + 1, monomials);
    }
    exponents[index] = 0;
  }

  // ---------------------------------------------------------------------------------------------

  private void checkTime() {
    if (System.currentTimeMillis() > deadline) {
      throw new Decline();
    }
  }

  /**
   * <code>D(result) == integrand</code>, checked at three points: numerically, with a relative
   * tolerance, where both sides are defined.
   */
  private boolean verify(IExpr result, IExpr integrand) {
    if (result.isNIL() || !result.isFree(S.Integrate, true)) {
      return false;
    }
    IExpr difference = engine.evaluate(F.Subtract(F.D(result, x), integrand));
    double[] points = {1.37, 2.19, 0.61};
    int checked = 0;
    for (double point : points) {
      IExpr value = engine.evaluate(F.N(F.subst(difference, x, F.num(point))));
      IExpr scale = engine.evaluate(F.N(F.subst(integrand, x, F.num(point))));
      double d = engine.evaluate(F.Abs(value)).evalfNaN();
      double s = engine.evaluate(F.Abs(scale)).evalfNaN();
      if (Double.isNaN(d) || Double.isNaN(s) || Double.isInfinite(s)) {
        continue;
      }
      if (d > 1e-8 * (1.0 + s)) {
        return false;
      }
      checked++;
    }
    return checked >= 2;
  }
}
