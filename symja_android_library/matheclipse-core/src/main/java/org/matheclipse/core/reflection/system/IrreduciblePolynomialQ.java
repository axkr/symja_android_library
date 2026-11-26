package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASModular;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.AlgebraicNumberUtils;
import edu.jas.application.GBAlgorithmBuilder;
import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrderByName;

public class IrreduciblePolynomialQ extends AbstractFunctionOptionEvaluator {

  public IrreduciblePolynomialQ() {}


  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr poly = ast.arg1();
    if (options[2].isInteger() && JASModular.isModpMessage(S.IrreduciblePolynomialQ, options[2])) {
      return F.NIL;
    }

    // Find the variables of the expression
    VariablesSet variablesSet = new VariablesSet(poly);
    IAST variables = variablesSet.getVarList();

    // Constants are never irreducible polynomials (units / zero / non-unit integers all map to
    // False).
    if (variables.argSize() == 0) {
      return S.False;
    }

    // Check if the given expression is structurally a polynomial
    IExpr isPoly = engine.evaluate(F.PolynomialQ(poly, variables));
    if (!isPoly.isTrue()) {
      return S.False;
    }

    // Resolve the Extension option (None / Automatic / All / explicit list / single algebraic
    // number) into an explicit list of generators, then split off the imaginary unit (which is
    // supported by Factor via the GaussianIntegers option) from the remaining algebraic generators
    // (which Factor does not currently honor and which we therefore handle via a root-based or
    // Trager-style reducibility test below).
    IAST extensionGenerators = F.CEmptyList;
    boolean gaussianFromExtension = false;
    IExpr extensionOption = options[0];
    if (extensionOption == S.Automatic) {
      extensionGenerators = AlgebraicNumberUtils.extractAlgebraicGenerators(poly);
    } else if (extensionOption == S.All) {
      return absolutelyIrreducible(poly, variables, engine);
    } else if (extensionOption.isList()) {
      extensionGenerators = (IAST) extensionOption;
    } else if (extensionOption != S.None) {
      // Single algebraic number, e.g. Extension -> Sqrt(3); treat as singleton list
      extensionGenerators = F.List(extensionOption);
    }
    if (extensionGenerators.argSize() > 0) {
      IASTAppendable nonI = F.ListAlloc(extensionGenerators.argSize());
      for (int i = 1; i <= extensionGenerators.argSize(); i++) {
        IExpr g = extensionGenerators.get(i);
        if (g.isImaginaryUnit()) {
          gaussianFromExtension = true;
        } else {
          nonI.append(g);
        }
      }
      extensionGenerators = nonI;
    }

    // Root-based reducibility check for non-trivial Extension generators (univariate only). If any
    // root of poly lies in Q(generators [+ I]) then poly is reducible over the extension.
    if (extensionGenerators.argSize() > 0 && variables.argSize() == 1) {
      IAST allGens;
      if (gaussianFromExtension) {
        IASTAppendable tmp = F.ListAlloc(extensionGenerators.argSize() + 1);
        tmp.appendArgs(extensionGenerators);
        tmp.append(S.I);
        allGens = tmp;
      } else {
        allGens = extensionGenerators;
      }
      IExpr variable = variables.arg1();
      IExpr solveResult = engine.evaluate(F.SolveDistinct(F.Equal(poly, F.C0), variable));
      if (solveResult.isList()) {
        IAST solutions = (IAST) solveResult;
        for (int i = 1; i <= solutions.argSize(); i++) {
          IExpr sol = solutions.get(i);
          if (sol.isList()) {
            IAST rules = (IAST) sol;
            for (int j = 1; j <= rules.argSize(); j++) {
              IExpr rule = rules.get(j);
              if (rule.isRule() && rule.first().equals(variable)) {
                IExpr root = rule.second();
                if (AlgebraicNumberUtils.isInExtension(root, allGens)) {
                  return S.False;
                }
              }
            }
          }
        }
      }
    }

    // Multivariate fallback for non-trivial Extension generators: Trager-style resultant test.
    // Substitute the single non-I generator alpha with a fresh symbol t whose minimal polynomial is
    // m(t). For increasing shifts s = 0, 1, ... compute R = Resultant_t(m(t), p[v -> v + s*t], t)
    // over the first variable v, until R is square-free. Then poly is irreducible over Q(alpha)
    // iff R has exactly one (non-constant) irreducible factor over Q.
    if (extensionGenerators.argSize() > 0 && variables.argSize() >= 2
        && hasRepeatedFactor(poly, variables, engine)) {
      // the resultant test below needs a square-free polynomial: the norm of
      // (x+Sqrt(3)*y)^2 is never square-free, whatever the shift
      return S.False;
    }
    if (extensionGenerators.argSize() == 1 && variables.argSize() >= 2) {
      IExpr alpha = extensionGenerators.arg1();
      ISymbol t = F.Dummy("$ipqT");
      IExpr m = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, alpha, t));
      if (m.isPresent() && !m.isAST(S.MinimalPolynomial)) {
        IExpr pT = engine.evaluate(F.subst(poly, alpha, t));
        IExpr v = variables.arg1();
        final int maxShift = 5;
        for (int s = 0; s <= maxShift; s++) {
          IExpr pShifted = pT;
          if (s > 0) {
            pShifted = engine.evaluate(F.subst(pT, v, F.Plus(v, F.Times(F.ZZ(s), t))));
          }
          IExpr resultant = engine.evaluate(F.Resultant(m, pShifted, t));
          if (!resultant.isPresent() || resultant.isZero()) {
            continue;
          }
          IExpr factored = engine.evaluate(F.Factor(resultant));
          int[] info = analyzeFactored(factored, variables);
          int count = info[0];
          boolean squarefree = info[1] == 1;
          if (count < 0 || !squarefree) {
            // Factor could not decide or factorization not yet squarefree -> try a larger shift
            continue;
          }
          return count >= 2 ? S.False : S.True;
        }
      }
    }

    // Build the delegated Factor[...] call. Rewrite Extension -> Automatic / {..., I, ...} so that
    // the I component becomes GaussianIntegers -> True (which Factor supports) and pass through all
    // other options unchanged.
    IASTAppendable factorAST = F.ast(S.Factor);
    factorAST.append(poly);
    boolean gaussianOptionAlreadyPresent = false;
    for (int i = 2; i <= originalAST.argSize(); i++) {
      IExpr arg = originalAST.get(i);
      if (arg.isRule()) {
        IExpr lhs = arg.first();
        if (lhs == S.Extension) {
          // Drop the Extension rule; if we still need to pass I to Factor we add
          // GaussianIntegers -> True below.
          continue;
        }
        if (lhs == S.GaussianIntegers) {
          gaussianOptionAlreadyPresent = true;
          if (gaussianFromExtension) {
            factorAST.append(F.Rule(S.GaussianIntegers, S.True));
          } else {
            factorAST.append(arg);
          }
          continue;
        }
      }
      factorAST.append(arg);
    }
    if (gaussianFromExtension && !gaussianOptionAlreadyPresent) {
      factorAST.append(F.Rule(S.GaussianIntegers, S.True));
    }

    IExpr factored = engine.evaluate(factorAST);
    int[] info = analyzeFactored(factored, variables);
    if (info[0] < 0) {
      // Factor could not evaluate the polynomial (e.g., unsupported extension)
      return F.NIL;
    }
    // An irreducible polynomial should only have 1 non-constant square-free factor
    return info[1] == 1 && info[0] == 1 ? S.True : S.False;
  }

  /**
   * Is <code>poly</code> irreducible over the algebraic numbers? Decided where a rule applies:
   * <ul>
   * <li>a polynomial which is reducible over the rationals or the Gaussian rationals is reducible
   * <li>a univariate polynomial, and a homogeneous one in two variables, which is a product of
   * linear forms, is irreducible only with degree <code>1</code>
   * <li>a polynomial of one monomial, <code>q(x^2*y^2)</code>, is reducible from degree
   * <code>2</code> of <code>q</code> on
   * <li>a polynomial of total degree <code>2</code> is reducible if the symmetric matrix of its
   * homogenized quadratic form has a rank of at most <code>2</code>
   * <li>a polynomial of degree <code>1</code> in one of its variables is irreducible if it is so
   * over the rationals: the content in that variable is the same over every field
   * </ul>
   *
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if no rule applies
   */
  private IExpr absolutelyIrreducible(IExpr poly, IAST variables, EvalEngine engine) {
    int[] overQ = analyzeFactored(engine.evaluate(F.Factor(poly)), variables);
    if (overQ[0] < 0) {
      return F.NIL;
    }
    if (overQ[0] != 1 || overQ[1] != 1) {
      return S.False;
    }
    int[] overQI =
        analyzeFactored(engine.evaluate(F.binaryAST2(S.Factor, poly, F.Rule(S.GaussianIntegers, S.True))),
            variables);
    if (overQI[0] >= 0 && (overQI[0] != 1 || overQI[1] != 1)) {
      return S.False;
    }
    final int n = variables.argSize();
    IExpr rules = engine.evaluate(F.CoefficientRules(poly, variables));
    if (!rules.isList() || ((IAST) rules).isEmpty()) {
      return F.NIL;
    }
    IAST monomials = (IAST) rules;
    long[][] exponents = new long[monomials.argSize()][n];
    long totalDegree = 0;
    boolean homogeneous = true;
    long firstDegree = -1;
    for (int i = 1; i <= monomials.argSize(); i++) {
      IExpr exponentList = monomials.get(i).first();
      long degree = 0;
      for (int j = 0; j < n; j++) {
        IExpr e = exponentList.getAt(j + 1);
        if (!e.isInteger()) {
          return F.NIL;
        }
        exponents[i - 1][j] = ((IInteger) e).toLong();
        degree += exponents[i - 1][j];
      }
      totalDegree = Math.max(totalDegree, degree);
      if (firstDegree < 0) {
        firstDegree = degree;
      } else if (degree != firstDegree) {
        homogeneous = false;
      }
    }
    if (n == 1 || (n == 2 && homogeneous)) {
      return totalDegree == 1 ? S.True : S.False;
    }
    if (singleMonomialPower(exponents) >= 2) {
      return S.False;
    }
    if (totalDegree == 2) {
      // the symmetric matrix of the quadratic form in the variables and a homogenizing one
      IASTAppendable matrix = F.ListAlloc(n + 1);
      for (int i = 0; i <= n; i++) {
        IASTAppendable row = F.ListAlloc(n + 1);
        for (int j = 0; j <= n; j++) {
          row.append(quadraticFormEntry(monomials, exponents, n, i, j));
        }
        matrix.append(row);
      }
      IExpr rank = engine.evaluate(F.MatrixRank(matrix));
      if (rank.isInteger()) {
        return ((IInteger) rank).toLong() <= 2 ? S.False : S.True;
      }
      return F.NIL;
    }
    for (int j = 0; j < n; j++) {
      long degree = 0;
      for (long[] e : exponents) {
        degree = Math.max(degree, e[j]);
      }
      if (degree == 1) {
        return S.True;
      }
    }
    if (n == 2 && newtonPolygonIndecomposable(exponents)) {
      return S.True;
    }
    return smoothProjectiveClosure(monomials, exponents, variables, totalDegree, engine);
  }

  /**
   * Gao's criterion for a polynomial in two variables without a monomial factor: it is absolutely
   * irreducible if its Newton polygon is no Minkowski sum of two lattice polygons. That holds for a
   * segment without interior lattice points, like the one of <code>x^2+y^3</code>, and for a
   * triangle with a vertex <code>v0</code> whose differences <code>v1-v0, v2-v0</code> to the other
   * vertices have the coordinate gcd <code>1</code>.
   *
   * @param exponents the exponent vectors of the monomials
   */
  private static boolean newtonPolygonIndecomposable(long[][] exponents) {
    for (int j = 0; j < 2; j++) {
      long min = Long.MAX_VALUE;
      for (long[] e : exponents) {
        min = Math.min(min, e[j]);
      }
      if (min != 0) {
        // a monomial factor
        return false;
      }
    }
    long[][] points = exponents.clone();
    java.util.Arrays.sort(points,
        (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
    // the convex hull by the monotone chain, without points inside of an edge
    List<long[]> hull = new ArrayList<>();
    for (int pass = 0; pass < 2; pass++) {
      final int start = hull.size();
      for (int k = 0; k < points.length; k++) {
        long[] point = points[pass == 0 ? k : points.length - 1 - k];
        while (hull.size() >= start + 2 && cross(hull.get(hull.size() - 2),
            hull.get(hull.size() - 1), point) <= 0) {
          hull.remove(hull.size() - 1);
        }
        hull.add(point);
      }
      hull.remove(hull.size() - 1);
    }
    if (hull.size() == 2) {
      return gcd(hull.get(1)[0] - hull.get(0)[0], hull.get(1)[1] - hull.get(0)[1]) == 1L;
    }
    if (hull.size() == 3) {
      for (int k = 0; k < 3; k++) {
        long[] v0 = hull.get(k);
        long[] v1 = hull.get((k + 1) % 3);
        long[] v2 = hull.get((k + 2) % 3);
        if (gcd(gcd(v1[0] - v0[0], v1[1] - v0[1]), gcd(v2[0] - v0[0], v2[1] - v0[1])) == 1L) {
          return true;
        }
      }
    }
    return false;
  }

  private static long cross(long[] o, long[] a, long[] b) {
    return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
  }

  private static long gcd(long a, long b) {
    return java.math.BigInteger.valueOf(a).gcd(java.math.BigInteger.valueOf(b)).longValue();
  }

  /** Most variables and the highest degree of {@link #smoothProjectiveClosure}. */
  private static final int SMOOTH_TEST_MAX_VARIABLES = 3;

  private static final long SMOOTH_TEST_MAX_DEGREE = 8;

  /**
   * <code>True</code> if the projective closure of the hypersurface of a polynomial in two or more
   * variables is smooth: the partial derivatives of the homogenized polynomial vanish together only
   * at the origin. Two components of a reducible hypersurface would meet in singular points, so it
   * is absolutely irreducible, like <code>x^4+y^4+1</code>.
   *
   * @return {@link F#NIL} if there is a singular point or it can't be decided
   */
  private static IExpr smoothProjectiveClosure(IAST monomials, long[][] exponents, IAST variables,
      long degree, EvalEngine engine) {
    final int n = variables.argSize();
    if (n > SMOOTH_TEST_MAX_VARIABLES || degree > SMOOTH_TEST_MAX_DEGREE) {
      return F.NIL;
    }
    ISymbol z = F.Dummy("$ipqZ");
    IASTAppendable homogeneous = F.PlusAlloc(monomials.argSize());
    for (int i = 1; i <= monomials.argSize(); i++) {
      IASTAppendable term = F.TimesAlloc(n + 2);
      term.append(monomials.get(i).second());
      long termDegree = 0;
      for (int j = 0; j < n; j++) {
        term.append(F.Power(variables.get(j + 1), F.ZZ(exponents[i - 1][j])));
        termDegree += exponents[i - 1][j];
      }
      term.append(F.Power(z, F.ZZ(degree - termDegree)));
      homogeneous.append(term);
    }
    IASTAppendable allVariables = F.ListAlloc(n + 1);
    allVariables.appendArgs(variables);
    allVariables.append(z);
    try {
      JASConvert<BigRational> jas =
          new JASConvert<BigRational>(allVariables, BigRational.ZERO, TermOrderByName.IGRLEX);
      List<GenPolynomial<BigRational>> partials = new ArrayList<>(n + 1);
      for (int k = 1; k <= n + 1; k++) {
        IExpr derivative = engine.evaluate(F.Expand(F.D(homogeneous, allVariables.get(k))));
        GenPolynomial<BigRational> partial = jas.expr2JAS(derivative, false);
        if (partial == null) {
          return F.NIL;
        }
        partials.add(partial);
      }
      GroebnerBaseAbstract<BigRational> gb =
          GBAlgorithmBuilder.<BigRational>polynomialRing(jas.getPolynomialRingFactory())
              .fractionFree().syzygyPairlist().build();
      // dimension 0 of the homogeneous ideal: the origin is the only common zero
      if (gb.commonZeroTest(gb.GB(partials)) <= 0) {
        return S.True;
      }
    } catch (JASConversionException jce) {
      // a coefficient which isn't rational
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * The largest <code>k</code> with which every monomial is the <code>k</code>-th power of one
   * monomial <code>m</code> to some power, <code>poly = q(m)</code>, or <code>0</code> if there is
   * no such monomial.
   */
  private static long singleMonomialPower(long[][] exponents) {
    long[] base = null;
    long gcd = 0;
    for (long[] e : exponents) {
      long g = 0;
      for (long v : e) {
        g = java.math.BigInteger.valueOf(g).gcd(java.math.BigInteger.valueOf(v)).longValue();
      }
      if (g == 0) {
        // the constant term
        continue;
      }
      long[] direction = new long[e.length];
      for (int j = 0; j < e.length; j++) {
        direction[j] = e[j] / g;
      }
      if (base == null) {
        base = direction;
      } else if (!java.util.Arrays.equals(base, direction)) {
        return 0;
      }
      gcd = java.math.BigInteger.valueOf(gcd).gcd(java.math.BigInteger.valueOf(g)).longValue();
    }
    if (base == null) {
      return 0;
    }
    long maxPower = 0;
    for (long[] e : exponents) {
      for (int j = 0; j < e.length; j++) {
        if (base[j] != 0) {
          maxPower = Math.max(maxPower, e[j] / base[j]);
          break;
        }
      }
    }
    return maxPower;
  }

  /**
   * The entry <code>(i, j)</code> of the symmetric matrix of a quadratic polynomial homogenized
   * with the variable of index <code>n</code>: the coefficient of <code>x_i^2</code> on the
   * diagonal, half the coefficient of <code>x_i*x_j</code> beside it.
   */
  private static IExpr quadraticFormEntry(IAST monomials, long[][] exponents, int n, int i,
      int j) {
    for (int k = 0; k < exponents.length; k++) {
      long[] e = exponents[k];
      long degree = 0;
      for (long v : e) {
        degree += v;
      }
      // the exponents with the homogenizing variable at index n
      long[] full = new long[n + 1];
      System.arraycopy(e, 0, full, 0, n);
      full[n] = 2 - degree;
      boolean match;
      if (i == j) {
        match = full[i] == 2;
      } else {
        match = full[i] == 1 && full[j] == 1;
      }
      if (match) {
        IExpr coefficient = monomials.get(k + 1).second();
        return i == j ? coefficient : F.Times(F.C1D2, coefficient);
      }
    }
    return F.C0;
  }

  /**
   * Test if the discriminant of <code>poly</code> with respect to one of its variables of degree
   * at least <code>2</code> vanishes identically. Then <code>poly</code> has a repeated factor over
   * the field of its coefficients and is reducible.
   */
  private static boolean hasRepeatedFactor(IExpr poly, IAST variables, EvalEngine engine) {
    for (int i = 1; i <= variables.argSize(); i++) {
      IExpr variable = variables.get(i);
      IExpr degree = engine.evaluate(F.Exponent(poly, variable));
      if (degree.isInteger() && ((IInteger) degree).isGT(F.C1)) {
        IExpr discriminant =
            engine.evalQuiet(F.Expand(F.Discriminant(poly, variable)));
        return discriminant.isZero();
      }
    }
    return false;
  }

  /**
   * Checks if the given factor expression contains any of the polynomial variables.
   *
   * @param expr the factor expression to check
   * @param variables the list of variables present in the original polynomial
   * @return true if the expression depends on any variable, false otherwise
   */
  private boolean hasVariables(IExpr expr, IAST variables) {
    for (int i = 1; i <= variables.argSize(); i++) {
      if (!expr.isFree(variables.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Inspect a {@code Factor}-result expression and return a two-element {@code int[]}:
   * <ul>
   * <li>index {@code 0}: the number of distinct non-constant irreducible factors, or {@code -1}
   * when {@code Factor} returned its input unchanged;</li>
   * <li>index {@code 1}: {@code 1} when the factorization is square-free (no factor with integer
   * exponent &ge; 2), {@code 0} otherwise.</li>
   * </ul>
   */
  private int[] analyzeFactored(IExpr factored, IAST variables) {
    int count = 0;
    boolean squarefree = true;
    if (factored.isAST()) {
      IAST astFactored = (IAST) factored;
      IExpr head = astFactored.head();
      if (head.isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) head).ordinal();
        if (id == ID.Factor) {
          return new int[] {-1, 1};
        }
        if (id == ID.Times) {
          for (int i = 1; i <= astFactored.argSize(); i++) {
            IExpr arg = astFactored.get(i);
            if (hasVariables(arg, variables)) {
              if (arg.isPower()) {
                IExpr exp = arg.getAt(2);
                if (exp.isInteger() && !exp.isOne()) {
                  squarefree = false;
                }
              }
              count++;
            }
          }
          return new int[] {count, squarefree ? 1 : 0};
        }
        if (id == ID.Power) {
          IExpr base = astFactored.getAt(1);
          IExpr exp = astFactored.getAt(2);
          if (hasVariables(base, variables)) {
            if (exp.isInteger() && !exp.isOne()) {
              squarefree = false;
            }
            count = 1;
          }
          return new int[] {count, squarefree ? 1 : 0};
        }
      }
    }
    if (hasVariables(factored, variables)) {
      count = 1;
    }
    return new int[] {count, 1};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
    // Set default options: Extension -> None, GaussianIntegers -> False, Modulus -> 0
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Extension, S.GaussianIntegers, S.Modulus},
        new IExpr[] {S.None, S.False, F.C0});
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}
