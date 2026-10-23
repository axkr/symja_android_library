package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.sympy.simplify.GammaSimp;

/**
 * Identities of special functions for <code>Simplify</code>.
 *
 * <p>
 * Calls of one function whose parameter differs by integers are rewritten through the
 * recurrence of that parameter onto one of them (a first order recurrence like
 * <code>Gamma(x+1) == x*Gamma(x)</code>) or onto two neighbouring ones (a three term recurrence
 * like <code>BesselJ(n+1,x) == 2*n/x*BesselJ(n,x)-BesselJ(n-1,x)</code>). Afterwards pairs of
 * terms which form a known combination - a Casoratian like
 * <code>BesselJ(n+1,x)*BesselY(n,x)-BesselJ(n,x)*BesselY(n+1,x) == 2/(Pi*x)</code>, a reflection
 * or a transformation formula - are replaced by its value. The caller keeps the result only if it
 * is simpler.
 */
public final class SpecialFunctionIdentities {

  /** The largest distance of two parameters which are brought together. */
  private static final int MAX_SPAN = 8;

  /**
   * The step of a recurrence at the parameter value <code>nu</code> of <code>call</code>. For a
   * first order recurrence <code>{m, c}</code> with <code>f(nu+1) == m*f(nu) + c</code>, for a
   * three term recurrence <code>{a, b, c}</code> with
   * <code>f(nu+1) == a*f(nu) + b*f(nu-1) + c</code>.
   */
  @FunctionalInterface
  private interface Step {
    IExpr[] at(IExpr nu, IAST call);
  }

  /** The calls <code>head(...)</code> with <code>argSize</code> arguments, shifted in one. */
  private static final class Family {
    final IBuiltInSymbol head;
    final int argSize;
    final int position;
    final boolean secondOrder;
    final Step step;

    Family(IBuiltInSymbol head, int argSize, int position, boolean secondOrder, Step step) {
      this.head = head;
      this.argSize = argSize;
      this.position = position;
      this.secondOrder = secondOrder;
      this.step = step;
    }

    boolean matches(IExpr expr) {
      return expr.isAST(head, argSize + 1) && applies((IAST) expr);
    }

    /** The PolyGamma family needs an explicit nonnegative integer order. */
    boolean applies(IAST call) {
      if (head == S.PolyGamma) {
        return call.arg1().isInteger() && !call.arg1().isNegative();
      }
      return true;
    }
  }

  /**
   * A combination of two terms: <code>r*x + s*r*y == r*value</code> for the products
   * <code>x</code> and <code>y</code> given by a call in <code>x</code>.
   */
  @FunctionalInterface
  private interface PairRule {
    /** <code>{x, y, s, value}</code> for the call <code>seed</code>, or <code>null</code>. */
    IExpr[] instantiate(IAST seed);
  }

  private static final List<Family> FAMILIES = new ArrayList<Family>();

  private static final Map<IBuiltInSymbol, List<PairRule>> PAIR_RULES =
      new IdentityHashMap<IBuiltInSymbol, List<PairRule>>();

  /** The heads of the families, of the pair rules and of the other functions in them. */
  private static final Map<IBuiltInSymbol, Boolean> KNOWN_HEADS =
      new IdentityHashMap<IBuiltInSymbol, Boolean>();

  private static IExpr frac(IExpr numerator, IExpr denominator) {
    return F.Times(numerator, F.Power(denominator, F.CN1));
  }

  private static void family(IBuiltInSymbol head, int argSize, int position, boolean secondOrder,
      Step step) {
    FAMILIES.add(new Family(head, argSize, position, secondOrder, step));
  }

  private static void pair(IBuiltInSymbol seedHead, PairRule rule) {
    PAIR_RULES.computeIfAbsent(seedHead, k -> new ArrayList<PairRule>()).add(rule);
  }

  static {
    final IExpr zero = F.C0;
    // first order recurrences {m, c}: f(nu+1) == m*f(nu) + c
    family(S.Gamma, 1, 1, false, (nu, call) -> new IExpr[] {nu, zero});
    family(S.Gamma, 2, 1, false, (nu, call) -> new IExpr[] {nu,
        F.Times(F.Power(call.arg2(), nu), F.Power(S.E, F.Negate(call.arg2())))});
    family(S.Factorial, 1, 1, false, (nu, call) -> new IExpr[] {F.Plus(nu, F.C1), zero});
    family(S.Pochhammer, 2, 2, false,
        (nu, call) -> new IExpr[] {F.Plus(call.arg1(), nu), zero});
    family(S.Pochhammer, 2, 1, false,
        (nu, call) -> new IExpr[] {frac(F.Plus(nu, call.arg2()), nu), zero});
    family(S.Binomial, 2, 2, false, (nu, call) -> new IExpr[] {
        frac(F.Subtract(call.arg1(), nu), F.Plus(nu, F.C1)), zero});
    family(S.Binomial, 2, 1, false, (nu, call) -> new IExpr[] {
        frac(F.Plus(nu, F.C1), F.Subtract(F.Plus(nu, F.C1), call.arg2())), zero});
    family(S.Beta, 2, 1, false,
        (nu, call) -> new IExpr[] {frac(nu, F.Plus(nu, call.arg2())), zero});
    family(S.Beta, 2, 2, false,
        (nu, call) -> new IExpr[] {frac(nu, F.Plus(call.arg1(), nu)), zero});
    family(S.BarnesG, 1, 1, false, (nu, call) -> new IExpr[] {F.Gamma(nu), zero});
    family(S.LogGamma, 1, 1, false, (nu, call) -> new IExpr[] {F.C1, F.Log(nu)});
    family(S.PolyGamma, 2, 2, false, (nu, call) -> {
      // PolyGamma(m, nu+1) == PolyGamma(m, nu) + (-1)^m*m!/nu^(m+1)
      IInteger m = (IInteger) call.arg1();
      return new IExpr[] {F.C1, F.Times(F.Power(F.CN1, m), F.Factorial(m),
          F.Power(nu, F.Negate(F.Plus(m, F.C1))))};
    });
    family(S.HarmonicNumber, 1, 1, false,
        (nu, call) -> new IExpr[] {F.C1, F.Power(F.Plus(nu, F.C1), F.CN1)});
    family(S.HurwitzZeta, 2, 2, false,
        (nu, call) -> new IExpr[] {F.C1, F.Negate(F.Power(nu, F.Negate(call.arg1())))});
    family(S.Zeta, 2, 2, false, (nu, call) -> new IExpr[] {F.C1,
        F.Negate(F.Power(F.Sqr(nu), F.Times(F.CN1D2, call.arg1())))});
    family(S.LerchPhi, 3, 3, false, (nu, call) -> new IExpr[] {F.Power(call.arg1(), F.CN1),
        F.Negate(frac(F.Power(nu, F.Negate(call.arg2())), call.arg1()))});
    family(S.QPochhammer, 3, 3, false, (nu, call) -> new IExpr[] {
        F.Subtract(F.C1, F.Times(call.arg1(), F.Power(call.arg2(), nu))), zero});

    // three term recurrences {a, b, c}: f(nu+1) == a*f(nu) + b*f(nu-1) + c
    final Step besselJ =
        (nu, call) -> new IExpr[] {frac(F.Times(F.C2, nu), call.arg2()), F.CN1, zero};
    family(S.BesselJ, 2, 1, true, besselJ);
    family(S.BesselY, 2, 1, true, besselJ);
    family(S.HankelH1, 2, 1, true, besselJ);
    family(S.HankelH2, 2, 1, true, besselJ);
    family(S.BesselI, 2, 1, true,
        (nu, call) -> new IExpr[] {frac(F.Times(F.CN2, nu), call.arg2()), F.C1, zero});
    family(S.BesselK, 2, 1, true,
        (nu, call) -> new IExpr[] {frac(F.Times(F.C2, nu), call.arg2()), F.C1, zero});
    family(S.StruveH, 2, 1, true,
        (nu, call) -> new IExpr[] {frac(F.Times(F.C2, nu), call.arg2()), F.CN1,
            struveTerm(nu, call.arg2())});
    family(S.StruveL, 2, 1, true,
        (nu, call) -> new IExpr[] {frac(F.Times(F.CN2, nu), call.arg2()), F.C1,
            F.Negate(struveTerm(nu, call.arg2()))});
    final Step legendre = (nu, call) -> new IExpr[] {
        frac(F.Times(F.Plus(F.Times(F.C2, nu), F.C1), call.arg2()), F.Plus(nu, F.C1)),
        F.Negate(frac(nu, F.Plus(nu, F.C1))), zero};
    family(S.LegendreP, 2, 1, true, legendre);
    family(S.LegendreQ, 2, 1, true, legendre);
    final Step chebyshev =
        (nu, call) -> new IExpr[] {F.Times(F.C2, call.arg2()), F.CN1, zero};
    family(S.ChebyshevT, 2, 1, true, chebyshev);
    family(S.ChebyshevU, 2, 1, true, chebyshev);
    family(S.HermiteH, 2, 1, true, (nu, call) -> new IExpr[] {F.Times(F.C2, call.arg2()),
        F.Times(F.CN2, nu), zero});
    family(S.LaguerreL, 2, 1, true, (nu, call) -> new IExpr[] {
        frac(F.Subtract(F.Plus(F.Times(F.C2, nu), F.C1), call.arg2()), F.Plus(nu, F.C1)),
        F.Negate(frac(nu, F.Plus(nu, F.C1))), zero});
    family(S.LaguerreL, 3, 1, true, (nu, call) -> new IExpr[] {
        frac(F.Subtract(F.Plus(F.Times(F.C2, nu), F.C1, call.arg2()), call.arg3()),
            F.Plus(nu, F.C1)),
        F.Negate(frac(F.Plus(nu, call.arg2()), F.Plus(nu, F.C1))), zero});
    family(S.GegenbauerC, 3, 1, true, (nu, call) -> new IExpr[] {
        frac(F.Times(F.C2, F.Plus(nu, call.arg2()), call.arg3()), F.Plus(nu, F.C1)),
        F.Negate(frac(F.Plus(nu, F.Times(F.C2, call.arg2()), F.CN1), F.Plus(nu, F.C1))),
        zero});
    family(S.Fibonacci, 1, 1, true, (nu, call) -> new IExpr[] {F.C1, F.C1, zero});
    family(S.Fibonacci, 2, 1, true, (nu, call) -> new IExpr[] {call.arg2(), F.C1, zero});
    family(S.LucasL, 1, 1, true, (nu, call) -> new IExpr[] {F.C1, F.C1, zero});
    family(S.LucasL, 2, 1, true, (nu, call) -> new IExpr[] {call.arg2(), F.C1, zero});
    // (c-a)*F(a-1) + (2*a-c+(b-a)*z)*F(a) + a*(z-1)*F(a+1) == 0
    family(S.Hypergeometric2F1, 4, 1, true, (nu, call) -> {
      IExpr b = call.arg2();
      IExpr c = call.arg3();
      IExpr z = call.arg4();
      IExpr d = F.Times(nu, F.Subtract(F.C1, z));
      return new IExpr[] {
          frac(F.Plus(F.Times(F.C2, nu), F.Negate(c), F.Times(F.Subtract(b, nu), z)), d),
          frac(F.Subtract(c, nu), d), zero};
    });
    family(S.Hypergeometric2F1, 4, 2, true, (nu, call) -> {
      IExpr a = call.arg1();
      IExpr c = call.arg3();
      IExpr z = call.arg4();
      IExpr d = F.Times(nu, F.Subtract(F.C1, z));
      return new IExpr[] {
          frac(F.Plus(F.Times(F.C2, nu), F.Negate(c), F.Times(F.Subtract(a, nu), z)), d),
          frac(F.Subtract(c, nu), d), zero};
    });
    // c*(c-1)*(z-1)*F(c-1) + c*(c-1-(2*c-a-b-1)*z)*F(c) + (c-a)*(c-b)*z*F(c+1) == 0
    family(S.Hypergeometric2F1, 4, 3, true, (nu, call) -> {
      IExpr a = call.arg1();
      IExpr b = call.arg2();
      IExpr z = call.arg4();
      IExpr d = F.Times(F.Subtract(nu, a), F.Subtract(nu, b), z);
      IExpr fc = F.Times(nu, F.Subtract(F.Subtract(nu, F.C1),
          F.Times(F.Subtract(F.Subtract(F.Times(F.C2, nu), a), F.Plus(b, F.C1)), z)));
      IExpr fcm1 = F.Times(nu, F.Subtract(nu, F.C1), F.Subtract(z, F.C1));
      return new IExpr[] {F.Negate(frac(fc, d)), F.Negate(frac(fcm1, d)), zero};
    });
    // (b-a)*M(a-1) + (2*a-b+z)*M(a) - a*M(a+1) == 0
    family(S.Hypergeometric1F1, 3, 1, true, (nu, call) -> {
      IExpr b = call.arg2();
      IExpr z = call.arg3();
      return new IExpr[] {frac(F.Plus(F.Times(F.C2, nu), F.Negate(b), z), nu),
          frac(F.Subtract(b, nu), nu), zero};
    });
    // b*(b-1)*M(b-1) + b*(1-b-z)*M(b) + z*(b-a)*M(b+1) == 0
    family(S.Hypergeometric1F1, 3, 2, true, (nu, call) -> {
      IExpr a = call.arg1();
      IExpr z = call.arg3();
      IExpr d = F.Times(z, F.Subtract(nu, a));
      return new IExpr[] {
          F.Negate(frac(F.Times(nu, F.Subtract(F.Subtract(F.C1, nu), z)), d)),
          F.Negate(frac(F.Times(nu, F.Subtract(nu, F.C1)), d)), zero};
    });
    // (b-1)*G(b-1) - (b-1)*G(b) - z/b*G(b+1) == 0
    family(S.Hypergeometric0F1, 2, 1, true, (nu, call) -> {
      IExpr factor = frac(F.Times(nu, F.Subtract(nu, F.C1)), call.arg2());
      return new IExpr[] {F.Negate(factor), factor, zero};
    });
    // U(a-1) + (b-2*a-z)*U(a) + a*(a-b+1)*U(a+1) == 0
    family(S.HypergeometricU, 3, 1, true, (nu, call) -> {
      IExpr b = call.arg2();
      IExpr z = call.arg3();
      IExpr d = F.Times(nu, F.Plus(F.Subtract(nu, b), F.C1));
      return new IExpr[] {F.Negate(frac(F.Subtract(F.Subtract(b, F.Times(F.C2, nu)), z), d)),
          F.Negate(frac(F.C1, d)), zero};
    });
    // W(k+1) + (2*k-z)*W(k) - (m-k+1/2)*(m+k-1/2)*W(k-1) == 0
    family(S.WhittakerW, 3, 1, true, (nu, call) -> {
      IExpr m = call.arg2();
      IExpr z = call.arg3();
      return new IExpr[] {F.Subtract(z, F.Times(F.C2, nu)),
          F.Times(F.Plus(F.Subtract(m, nu), F.C1D2), F.Subtract(F.Plus(m, nu), F.C1D2)), zero};
    });
    // (2*m+2*k+1)*M(k+1) + 2*(z-2*k)*M(k) - (2*m-2*k+1)*M(k-1) == 0
    family(S.WhittakerM, 3, 1, true, (nu, call) -> {
      IExpr m = call.arg2();
      IExpr z = call.arg3();
      IExpr d = F.Plus(F.Times(F.C2, m), F.Times(F.C2, nu), F.C1);
      return new IExpr[] {F.Negate(frac(F.Times(F.C2, F.Subtract(z, F.Times(F.C2, nu))), d)),
          frac(F.Plus(F.Times(F.C2, m), F.Times(F.CN2, nu), F.C1), d), zero};
    });

    // Casoratians of two solutions of one recurrence
    pair(S.BesselJ, seed -> casoratian(seed, S.BesselY, F.CN1,
        nu -> frac(F.C2, F.Times(S.Pi, seed.arg2()))));
    pair(S.BesselI, seed -> casoratian(seed, S.BesselK, F.C1, nu -> F.Power(seed.arg2(), F.CN1)));
    pair(S.LegendreP, seed -> casoratian(seed, S.LegendreQ, F.CN1,
        nu -> F.Power(F.Plus(nu, F.C1), F.CN1)));
    pair(S.Fibonacci, seed -> seed.argSize() != 1 ? null
        : casoratian(seed, S.LucasL, F.CN1, nu -> F.Times(F.C2, F.Cos(F.Times(S.Pi, nu)))));
    // Wronskian AiryAi(z)*AiryBiPrime(z) - AiryAiPrime(z)*AiryBi(z) == 1/Pi
    pair(S.AiryAi, seed -> seed.argSize() != 1 ? null
        : new IExpr[] {F.Times(seed, F.unaryAST1(S.AiryBiPrime, seed.arg1())),
            F.Times(F.unaryAST1(S.AiryAiPrime, seed.arg1()), F.unaryAST1(S.AiryBi, seed.arg1())),
            F.CN1, F.Power(S.Pi, F.CN1)});
    // PolyGamma(0,u) - PolyGamma(0,1-u) == -Pi*Cot(Pi*u)
    pair(S.PolyGamma, seed -> {
      if (seed.argSize() != 2 || !seed.arg1().isZero() && !seed.arg1().isOne()) {
        return null;
      }
      IExpr u = seed.arg2();
      IExpr reflected = F.PolyGamma(seed.arg1(), F.Subtract(F.C1, u));
      if (seed.arg1().isZero()) {
        return new IExpr[] {seed, reflected, F.CN1, F.Negate(F.Times(S.Pi, F.Cot(F.Times(S.Pi, u))))};
      }
      // PolyGamma(1,u) + PolyGamma(1,1-u) == Pi^2*Csc(Pi*u)^2
      return new IExpr[] {seed, reflected, F.C1,
          F.Times(F.Sqr(S.Pi), F.Sqr(F.Csc(F.Times(S.Pi, u))))};
    });
    // Erfc(u) - Erfc(-u) == -2*Erf(u)
    pair(S.Erfc, seed -> seed.argSize() != 1 ? null
        : new IExpr[] {seed, F.Erfc(F.Negate(seed.arg1())), F.CN1,
            F.Times(F.CN2, F.Erf(seed.arg1()))});
    // LogIntegral(u) - ExpIntegralEi(Log(u)) == 0
    pair(S.LogIntegral, seed -> seed.argSize() != 1 ? null
        : new IExpr[] {seed, F.ExpIntegralEi(F.Log(seed.arg1())), F.CN1, F.C0});
    // HarmonicNumber(u) - PolyGamma(0,u+1) == EulerGamma
    pair(S.HarmonicNumber, seed -> seed.argSize() != 1 ? null
        : new IExpr[] {seed, F.PolyGamma(F.C0, F.Plus(seed.arg1(), F.C1)), F.CN1, S.EulerGamma});
    // Kummer: Hypergeometric1F1(a,b,z) - E^z*Hypergeometric1F1(b-a,b,-z) == 0
    pair(S.Hypergeometric1F1, seed -> seed.argSize() != 3 ? null
        : new IExpr[] {seed,
            F.Times(F.Power(S.E, seed.arg3()), F.Hypergeometric1F1(
                F.Subtract(seed.arg2(), seed.arg1()), seed.arg2(), F.Negate(seed.arg3()))),
            F.CN1, F.C0});
    // HypergeometricU(a,b,z) - z^(1-b)*HypergeometricU(a-b+1,2-b,z) == 0
    pair(S.HypergeometricU, seed -> seed.argSize() != 3 ? null
        : new IExpr[] {seed,
            F.Times(F.Power(seed.arg3(), F.Subtract(F.C1, seed.arg2())),
                F.HypergeometricU(F.Plus(F.Subtract(seed.arg1(), seed.arg2()), F.C1),
                    F.Subtract(F.C2, seed.arg2()), seed.arg3())),
            F.CN1, F.C0});
  }

  static {
    for (Family family : FAMILIES) {
      KNOWN_HEADS.put(family.head, Boolean.TRUE);
    }
    for (IBuiltInSymbol head : PAIR_RULES.keySet()) {
      KNOWN_HEADS.put(head, Boolean.TRUE);
    }
    // the other functions of the pair rules and the reflections
    for (IBuiltInSymbol head : new IBuiltInSymbol[] {S.BesselY, S.BesselK, S.LegendreQ, S.LucasL,
        S.AiryBi, S.AiryAiPrime, S.AiryBiPrime, S.EllipticE, S.EllipticK, S.ExpIntegralEi,
        S.HypergeometricPFQ}) {
      KNOWN_HEADS.put(head, Boolean.TRUE);
    }
  }

  /** <code>(z/2)^nu/(Sqrt(Pi)*Gamma(nu+3/2))</code>, the inhomogeneous part of Struve. */
  private static IExpr struveTerm(IExpr nu, IExpr z) {
    return frac(F.Power(F.Times(F.C1D2, z), nu),
        F.Times(F.Sqrt(S.Pi), F.Gamma(F.Plus(nu, F.C3D2))));
  }

  /**
   * <code>A(nu+1)*B(nu) + s*A(nu)*B(nu+1) == value(nu)</code> for the seed <code>A(nu+1)</code>.
   */
  private static IExpr[] casoratian(IAST seed, IBuiltInSymbol other, IExpr s,
      java.util.function.Function<IExpr, IExpr> value) {
    IExpr nu = F.Subtract(seed.arg1(), F.C1);
    IAST a0 = seed.setAtCopy(1, nu);
    IAST b0 = seed.setAtCopy(1, nu).setAtCopy(0, other);
    IAST b1 = seed.setAtCopy(0, other);
    return new IExpr[] {F.Times(seed, b0), F.Times(a0, b1), s, value.apply(nu)};
  }

  private SpecialFunctionIdentities() {}

  /** The calls of the expression which the identities know, down to the factors of the terms. */
  private static void collectCalls(IExpr expr, List<IAST> calls, int depth) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol() && isKnownHead((IBuiltInSymbol) head)) {
        calls.add(ast);
        return;
      }
      if (depth < 3 && (ast.isPlus() || ast.isTimes() || ast.isPower())) {
        for (int i = 1; i < ast.size(); i++) {
          collectCalls(ast.get(i), calls, depth + 1);
        }
      }
    }
  }

  private static boolean isKnownHead(IBuiltInSymbol head) {
    return KNOWN_HEADS.containsKey(head);
  }

  /**
   * The largest leaf count of an expression the identities are searched for. Every candidate is
   * expanded and brought over a common denominator, which for a large sum costs more than the
   * identities can win.
   */
  private static final int MAX_LEAF_COUNT = 2 * org.matheclipse.core.basic.Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT;

  /**
   * <code>true</code> if the sum or product has at least two calls the identities know, which is
   * what any identity needs, and is small enough to be searched.
   */
  public static boolean isCandidate(IExpr expr) {
    if (!expr.isPlus() && !expr.isTimes() || expr.leafCount() > MAX_LEAF_COUNT) {
      return false;
    }
    List<IAST> calls = new ArrayList<IAST>();
    collectCalls(expr, calls, 0);
    return calls.size() >= 2;
  }

  /**
   * The simplest form the identities give for <code>expr</code>.
   *
   * @return {@link F#NIL} if no identity applies
   */
  public static IExpr simplify(IExpr expr, EvalEngine engine) {
    if (!isCandidate(expr)) {
      return F.NIL;
    }
    IExpr best = F.NIL;
    long bestScore = score(expr);
    // replaceAll() answers NIL when nothing was replaced
    IExpr reflected = reflections(expr, engine);
    List<IExpr> starts = new ArrayList<IExpr>();
    starts.add(expr);
    if (reflected.isPresent()) {
      // a reflection may join two groups or separate them, so both are tried
      starts.add(engine.evaluate(reflected));
    }
    IExpr aligned = alignEvenOrders(expr, engine);
    if (aligned.isPresent()) {
      starts.add(engine.evaluate(aligned));
    }
    // the shifted forms already examined: most starts and orders arrive at the same one
    java.util.Set<IExpr> examined = new java.util.HashSet<IExpr>();
    for (IExpr start : starts) {
      // a call with two shifted parameters, like Binomial(n,k), reaches the common basis only if
      // its families are applied one after the other, and the order can matter
      for (int order = 0; order < 4; order++) {
        final boolean lowest = order < 2;
        final boolean reversed = (order & 1) == 1;
        engine.checkTimeLimit();
        IExpr shifted = start;
        for (int round = 0; round < 6; round++) {
          IExpr next = shiftFamilies(shifted, lowest, reversed, engine);
          if (next.isNIL()) {
            break;
          }
          shifted = next;
        }
        if (!examined.add(shifted)) {
          continue;
        }
        for (IExpr candidate : candidates(shifted, engine)) {
          long candidateScore = score(candidate);
          if (candidateScore < bestScore) {
            best = candidate;
            bestScore = candidateScore;
          }
        }
      }
    }
    if (expr.isTimes() && GammaSimp.isCandidate(expr)) {
      // products and quotients of Gamma functions: reflection, duplication and multiplication
      try {
        IExpr temp = engine.evaluate(GammaSimp.combsimp(expr));
        if (score(temp) < bestScore) {
          best = temp;
          bestScore = score(temp);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    return best;
  }

  /**
   * The size of an expression, where each call of a special function counts more than its
   * leaves: of two forms of the same size the one with fewer such calls is simpler.
   */
  private static long score(IExpr expr) {
    List<IAST> calls = new ArrayList<IAST>();
    collectCalls(expr, calls, 0);
    return expr.leafCountSimplify() + 3L * calls.size();
  }

  /**
   * <code>true</code> if <code>nu</code> is a negative number or the negation makes it smaller:
   * <code>-n</code> is reflected to <code>n</code>, but <code>n-1</code> is not reflected to
   * <code>1-n</code>.
   */
  private static boolean isReflectable(IExpr nu, EvalEngine engine) {
    if (nu.isNumber()) {
      return nu.isNegativeResult();
    }
    return nu.isNegativeSigned()
        && engine.evaluate(F.Negate(nu)).leafCount() < nu.leafCount();
  }

  /** The evaluated forms which are compared: as it is, with the pair identities, collected. */
  private static List<IExpr> candidates(IExpr expr, EvalEngine engine) {
    List<IExpr> result = new ArrayList<IExpr>();
    result.add(expr);
    IExpr expanded = engine.evaluate(F.Expand(expr));
    IExpr paired = pairIdentities(expanded, engine);
    if (paired.isPresent()) {
      expanded = paired;
      result.add(paired);
    }
    List<IAST> calls = new ArrayList<IAST>();
    collectCalls(expanded, calls, 0);
    if (!calls.isEmpty() && expanded.isPlus()) {
      IASTAppendable variables = F.ListAlloc(calls.size());
      for (IAST call : calls) {
        if (!variables.contains(call)) {
          variables.append(call);
        }
      }
      result.add(engine.evaluate(F.ternaryAST3(S.Collect, expanded, variables, S.Together)));
    }
    result.add(engine.evaluate(F.Together(expanded)));
    return result;
  }

  /** Reflections which bring a parameter to its usual sign. */
  private static IExpr reflections(IExpr expr, EvalEngine engine) {
    return expr.replaceAll(x -> {
      if (!x.isAST() || x.argSize() < 1) {
        return F.NIL;
      }
      IAST call = (IAST) x;
      IExpr nu = call.arg1();
      if (call.isAST(S.BesselK, 3) && isReflectable(nu, engine)) {
        // BesselK is even in its order
        return F.BesselK(F.Negate(nu), call.arg2());
      }
      if (call.isAST(S.LegendreP, 3) && nu.isNegativeSigned()) {
        // LegendreP(-n-1, x) == LegendreP(n, x)
        IExpr reflected = engine.evaluate(F.Subtract(F.Negate(nu), F.C1));
        if (reflected.leafCount() < nu.leafCount()) {
          return F.LegendreP(reflected, call.arg2());
        }
      }
      if (call.isAST(S.ChebyshevT, 3) && isReflectable(nu, engine)) {
        return F.ChebyshevT(F.Negate(nu), call.arg2());
      }
      if (call.isAST(S.ChebyshevU, 3) && isReflectable(nu, engine)) {
        // ChebyshevU(-n, x) == -ChebyshevU(n-2, x)
        return F.Negate(F.ChebyshevU(F.Subtract(F.Negate(nu), F.C2), call.arg2()));
      }
      if ((call.isAST(S.BesselJ, 3) || call.isAST(S.BesselY, 3) || call.isAST(S.BesselI, 3))
          && !nu.isNumber() && isReflectable(nu, engine) && nu.isIntegerResult()) {
        // an integer order: J(-n) == (-1)^n*J(n), Y(-n) == (-1)^n*Y(n), I(-n) == I(n)
        IExpr n = F.Negate(nu);
        IAST positive = call.setAtCopy(1, n);
        return call.isAST(S.BesselI) ? positive : F.Times(F.Power(F.CN1, n), positive);
      }
      if (call.isAST(S.HermiteH, 3) && isReflectable(call.arg2(), engine)
          && nu.isIntegerResult()) {
        // HermiteH(n, -x) == (-1)^n*HermiteH(n, x)
        return F.Times(F.Power(F.CN1, nu), F.HermiteH(nu, F.Negate(call.arg2())));
      }
      if (call.isAST(S.HypergeometricPFQ, 4) && call.arg1().isList() && call.arg2().isList()) {
        int p = call.arg1().argSize();
        int q = call.arg2().argSize();
        if (q == 1 && p == 2) {
          return F.Hypergeometric2F1(call.arg1().first(), call.arg1().second(),
              call.arg2().first(), call.arg3());
        }
        if (q == 1 && p == 1) {
          return F.Hypergeometric1F1(call.arg1().first(), call.arg2().first(), call.arg3());
        }
      }
      return F.NIL;
    });
  }

  /**
   * <code>BesselK(nu, x) == BesselK(-nu, x)</code>: write an order with its other sign where only
   * that sign makes it differ by an integer from the order of another <code>BesselK</code> call.
   *
   * @return {@link F#NIL} if no order was changed
   */
  private static IExpr alignEvenOrders(IExpr expr, EvalEngine engine) {
    List<IAST> calls = new ArrayList<IAST>();
    collectCalls(expr, calls, 0);
    Map<IExpr, IExpr> replacements = new HashMap<IExpr, IExpr>();
    for (IAST call : calls) {
      if (!call.isAST(S.BesselK, 3)) {
        continue;
      }
      Object[] own = splitOffset(call.arg1(), engine);
      Object[] other = splitOffset(engine.evaluate(F.Negate(call.arg1())), engine);
      if (own == null || other == null) {
        continue;
      }
      boolean ownShared = false;
      boolean otherShared = false;
      for (IAST partner : calls) {
        if (partner == call || !partner.isAST(S.BesselK, 3)
            || !partner.arg2().equals(call.arg2())) {
          continue;
        }
        Object[] split = splitOffset(partner.arg1(), engine);
        if (split != null) {
          ownShared |= split[0].equals(own[0]);
          otherShared |= split[0].equals(other[0]);
        }
      }
      if (otherShared && !ownShared) {
        replacements.put(call, F.BesselK(engine.evaluate(F.Negate(call.arg1())), call.arg2()));
      }
    }
    return replacements.isEmpty() ? F.NIL : expr.replaceAll(replacements);
  }

  /** <code>{base, k}</code> with <code>parameter == base + k</code> for an integer k. */
  private static Object[] splitOffset(IExpr parameter, EvalEngine engine) {
    IExpr number = F.C0;
    IExpr rest = parameter;
    if (parameter.isRational()) {
      number = parameter;
      rest = F.C0;
    } else if (parameter.isPlus() && parameter.first().isRational()) {
      number = parameter.first();
      rest = ((IAST) parameter).removeAtCopy(1).oneIdentity0();
    }
    IInteger k = ((IRational) number).floorFraction();
    int offset = k.toIntDefault();
    if (offset == Integer.MIN_VALUE || Math.abs(offset) > 1000) {
      return null;
    }
    IExpr base = engine.evaluate(F.Plus(rest, F.Subtract(number, k)));
    return new Object[] {base, Integer.valueOf(offset)};
  }

  /**
   * Rewrite each group of calls of the first family (in the given order) which has a group whose
   * parameters differ by integers onto the lowest (or the highest) of them.
   *
   * @return {@link F#NIL} if no group has two different parameters
   */
  private static IExpr shiftFamilies(IExpr expr, boolean lowest, boolean reversed,
      EvalEngine engine) {
    List<IAST> calls = new ArrayList<IAST>();
    collectCalls(expr, calls, 0);
    Map<IExpr, IExpr> replacements = new HashMap<IExpr, IExpr>();
    for (int index = 0; index < FAMILIES.size() && replacements.isEmpty(); index++) {
      Family family = FAMILIES.get(reversed ? FAMILIES.size() - 1 - index : index);
      // the calls by their parameter's non-integer part and the other arguments
      Map<IExpr, TreeSet<Integer>> groups = new LinkedHashMap<IExpr, TreeSet<Integer>>();
      for (IAST call : calls) {
        if (!family.matches(call) || replacements.containsKey(call)) {
          continue;
        }
        Object[] split = splitOffset(call.get(family.position), engine);
        if (split == null) {
          continue;
        }
        IExpr key = call.setAtCopy(family.position, (IExpr) split[0]);
        groups.computeIfAbsent(key, k -> new TreeSet<Integer>()).add((Integer) split[1]);
      }
      for (Map.Entry<IExpr, TreeSet<Integer>> group : groups.entrySet()) {
        TreeSet<Integer> offsets = group.getValue();
        int min = offsets.first();
        int max = offsets.last();
        if (offsets.size() < 2 || max - min > MAX_SPAN) {
          continue;
        }
        if (family.secondOrder && max - min < 2) {
          // two neighbours are a basis already
          continue;
        }
        IAST key = (IAST) group.getKey();
        IExpr base = key.get(family.position);
        if (family.secondOrder) {
          shiftSecondOrder(family, key, base, offsets, lowest, replacements, engine);
        } else {
          shiftFirstOrder(family, key, base, offsets, replacements, engine);
        }
      }
    }
    if (replacements.isEmpty()) {
      return F.NIL;
    }
    IExpr replaced = expr.replaceAll(replacements);
    return replaced.isPresent() ? engine.evaluate(replaced) : F.NIL;
  }

  private static IAST callAt(Family family, IAST key, IExpr base, int offset,
      EvalEngine engine) {
    return key.setAtCopy(family.position, engine.evaluate(F.Plus(base, F.ZZ(offset))));
  }

  /** <code>f(base+k) == M*f(base+min) + A</code> for every offset <code>k</code>. */
  private static void shiftFirstOrder(Family family, IAST key, IExpr base,
      TreeSet<Integer> offsets, Map<IExpr, IExpr> replacements, EvalEngine engine) {
    int min = offsets.first();
    IAST basis = callAt(family, key, base, min, engine);
    IExpr factor = F.C1;
    IExpr addend = F.C0;
    for (int j = min; j < offsets.last(); j++) {
      IExpr nu = engine.evaluate(F.Plus(base, F.ZZ(j)));
      IExpr[] step = family.step.at(nu, callAt(family, key, base, j, engine));
      factor = engine.evaluate(F.Times(step[0], factor));
      addend = engine.evaluate(F.Plus(F.Times(step[0], addend), step[1]));
      if (offsets.contains(j + 1)) {
        replacements.put(callAt(family, key, base, j + 1, engine),
            F.Plus(F.Times(factor, basis), addend));
      }
    }
  }

  /**
   * <code>f(base+k) == A*f(base+m) + B*f(base+m+1) + C</code> on the two lowest (or highest)
   * neighbours.
   */
  private static void shiftSecondOrder(Family family, IAST key, IExpr base,
      TreeSet<Integer> offsets, boolean lowest, Map<IExpr, IExpr> replacements,
      EvalEngine engine) {
    int min = offsets.first();
    int max = offsets.last();
    if (lowest) {
      IAST f0 = callAt(family, key, base, min, engine);
      IAST f1 = callAt(family, key, base, min + 1, engine);
      // coefficients {of f0, of f1, constant} of f(j-1) and f(j)
      IExpr[] previous = {F.C1, F.C0, F.C0};
      IExpr[] current = {F.C0, F.C1, F.C0};
      for (int j = min + 1; j < max; j++) {
        IExpr nu = engine.evaluate(F.Plus(base, F.ZZ(j)));
        IExpr[] step = family.step.at(nu, callAt(family, key, base, j, engine));
        IExpr[] next = new IExpr[3];
        for (int i = 0; i < 3; i++) {
          next[i] = engine.evaluate(F.Plus(F.Times(step[0], current[i]),
              F.Times(step[1], previous[i]), i == 2 ? step[2] : F.C0));
        }
        previous = current;
        current = next;
        if (offsets.contains(j + 1)) {
          replacements.put(callAt(family, key, base, j + 1, engine),
              F.Plus(F.Times(current[0], f0), F.Times(current[1], f1), current[2]));
        }
      }
    } else {
      IAST g0 = callAt(family, key, base, max, engine);
      IAST g1 = callAt(family, key, base, max - 1, engine);
      // f(j-1) == (f(j+1) - a*f(j) - c)/b, coefficients {of g0, of g1, constant}
      IExpr[] upper = {F.C1, F.C0, F.C0};
      IExpr[] current = {F.C0, F.C1, F.C0};
      for (int j = max - 1; j > min; j--) {
        IExpr nu = engine.evaluate(F.Plus(base, F.ZZ(j)));
        IExpr[] step = family.step.at(nu, callAt(family, key, base, j, engine));
        if (engine.evaluate(step[1]).isZero()) {
          return;
        }
        IExpr[] next = new IExpr[3];
        for (int i = 0; i < 3; i++) {
          next[i] = engine.evaluate(F.Times(F.Power(step[1], F.CN1),
              F.Subtract(F.Subtract(upper[i], F.Times(step[0], current[i])),
                  i == 2 ? step[2] : F.C0)));
        }
        upper = current;
        current = next;
        if (offsets.contains(j - 1)) {
          replacements.put(callAt(family, key, base, j - 1, engine),
              F.Plus(F.Times(current[0], g0), F.Times(current[1], g1), current[2]));
        }
      }
    }
  }

  /** The factors of a term. */
  private static IAST factors(IExpr term) {
    return term.isTimes() ? (IAST) term : F.list(term);
  }

  /** <code>true</code> if every factor of <code>part</code> is a factor of <code>term</code>. */
  private static boolean containsFactors(IExpr term, IExpr part) {
    IAST termFactors = factors(term);
    for (IExpr factor : factors(part)) {
      if (!termFactors.contains(factor)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Replace two terms <code>r*x</code> and <code>s*r*y</code> of an expanded sum by
   * <code>r*value</code>, and the three terms of Legendre's relation of the complete elliptic
   * integrals.
   *
   * @return {@link F#NIL} if no pair was found
   */
  private static IExpr pairIdentities(IExpr expanded, EvalEngine engine) {
    if (!expanded.isPlus()) {
      return F.NIL;
    }
    List<IExpr> terms = new ArrayList<IExpr>();
    for (int i = 1; i < expanded.size(); i++) {
      terms.add(((IAST) expanded).get(i));
    }
    boolean changed = false;
    boolean found = true;
    while (found) {
      found = false;
      engine.checkTimeLimit();
      search: for (int i = 0; i < terms.size(); i++) {
        IExpr term = terms.get(i);
        for (IExpr factor : factors(term)) {
          if (!factor.isAST() || !factor.head().isBuiltInSymbol()) {
            continue;
          }
          IExpr legendre = legendreRelation(terms, i, (IAST) factor, engine);
          if (legendre.isPresent()) {
            changed = found = true;
            break search;
          }
          List<PairRule> rules = PAIR_RULES.get(factor.head());
          if (rules == null) {
            continue;
          }
          for (PairRule rule : rules) {
            IExpr[] instance = rule.instantiate((IAST) factor);
            if (instance == null) {
              continue;
            }
            IExpr x = engine.evaluate(instance[0]);
            if (!containsFactors(term, x)) {
              continue;
            }
            IExpr r = engine.evaluate(F.Times(term, F.Power(x, F.CN1)));
            IExpr expected = engine.evaluate(F.Times(instance[2], r, instance[1]));
            for (int j = 0; j < terms.size(); j++) {
              if (j != i && terms.get(j).equals(expected)) {
                IExpr replacement = engine.evaluate(F.Times(r, instance[3]));
                terms.set(i, replacement);
                terms.remove(j);
                changed = found = true;
                break search;
              }
            }
          }
        }
      }
    }
    if (!changed) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(terms.size());
    terms.forEach(sum::append);
    return engine.evaluate(sum);
  }

  /**
   * <code>r*E(m)*K(1-m) + r*E(1-m)*K(m) - r*K(m)*K(1-m) == r*Pi/2</code> for the factor
   * <code>EllipticE(m)</code> of the term at <code>i</code>; the three terms are replaced in
   * <code>terms</code>.
   */
  private static IExpr legendreRelation(List<IExpr> terms, int i, IAST factor,
      EvalEngine engine) {
    if (!factor.isAST(S.EllipticE, 2)) {
      return F.NIL;
    }
    IExpr m = factor.arg1();
    IExpr m1 = engine.evaluate(F.Subtract(F.C1, m));
    IExpr x = engine.evaluate(F.Times(factor, F.EllipticK(m1)));
    IExpr term = terms.get(i);
    if (!containsFactors(term, x)) {
      return F.NIL;
    }
    IExpr r = engine.evaluate(F.Times(term, F.Power(x, F.CN1)));
    IExpr second = engine.evaluate(F.Times(r, F.EllipticE(m1), F.EllipticK(m)));
    IExpr third = engine.evaluate(F.Times(F.CN1, r, F.EllipticK(m), F.EllipticK(m1)));
    int j = terms.indexOf(second);
    int k = terms.indexOf(third);
    if (j < 0 || k < 0 || j == i || k == i || j == k) {
      return F.NIL;
    }
    terms.set(i, engine.evaluate(F.Times(r, F.C1D2, S.Pi)));
    terms.remove(Math.max(j, k));
    terms.remove(Math.min(j, k));
    return S.True;
  }
}
