package org.matheclipse.core.builtin;

import java.util.Locale;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.AlgebraicNumberUtils;

public class AssumptionFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Arrays.setEvaluator(new Arrays());
      S.Matrices.setEvaluator(new Matrices());
      S.Vectors.setEvaluator(new Vectors());
      S.Assuming.setEvaluator(new Assuming());
      S.Element.setEvaluator(new Element());
      S.NotElement.setEvaluator(new NotElement());
      S.Refine.setEvaluator(new Refine());
    }
  }

  private static final class Arrays extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.size() == 2 && ast.arg1().isAST()) {
        return F.Arrays((IAST) ast.arg1());
      }
      if (ast.size() == 3 && ast.arg1().isAST() && ast.arg2().isSymbol()) {
        return F.Arrays((IAST) ast.arg1(), (ISymbol) ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NHOLDALL);
    }
  }

  /**
   * <code>Matrices({d1,d2})</code> - the domain of the <code>d1</code> x <code>d2</code> matrices.
   * The component domain defaults to {@link S#Complexes}.
   */
  private static final class Matrices extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dimensions = ast.arg1();
      if (!dimensions.isList() || dimensions.argSize() != 2) {
        // The list `1` of dimensions `3` must have length `2`.
        return Errors.printMessage(S.Matrices, "rankl",
            F.List(dimensions, F.C2, F.stringx("for a matrix")), engine);
      }
      // Matrices(dims) and Matrices(dims, domain) both canonicalize to the 3-argument form with
      // an explicit empty symmetry {}:
      // Matrices({2,3},Reals) prints back as Matrices({2,3},Reals,{}).
      if (ast.isAST1()) {
        return F.Matrices((IAST) dimensions, S.Complexes);
      }
      if (ast.isAST2() && ast.arg2().isSymbol()) {
        return F.Matrices((IAST) dimensions, (ISymbol) ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NHOLDALL);
    }
  }

  /**
   * <code>Vectors(d)</code> - the domain of the vectors of length <code>d</code>. The component
   * domain defaults to {@link S#Complexes}.
   */
  private static final class Vectors extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dimension = ast.arg1();
      if (dimension.isList()) {
        if (dimension.argSize() != 1) {
          // The list `1` of dimensions `3` must have length `2`.
          return Errors.printMessage(S.Vectors, "rankl",
              F.List(dimension, F.C1, F.stringx("for a vector")), engine);
        }
        dimension = dimension.first();
      }
      if (ast.isAST1()) {
        return F.Vectors(dimension, S.Complexes);
      }
      if (!ast.arg1().equals(dimension)) {
        return F.Vectors(dimension, ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NHOLDALL);
    }
  }

  private static final class Assuming extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr oldValue = S.$Assumptions.assignedValue();
      IExpr value = S.True;
      if (oldValue == null) {
        value = ast.arg1().makeList();
      } else {
        value = oldValue;
        if (value.isList()) {
          value = ((IAST) value).appendClone(ast.arg1());
        } else {
          value = F.ListAlloc(value, ast.arg1());
        }
      }

      try {
        S.$Assumptions.assignValue(value);
        IExpr temp = engine.evaluate(ast.arg2());
        return temp;
      } finally {
        S.$Assumptions.assignValue(oldValue);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * Element(symbol, dom)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * assume (or test) that the <code>symbol</code> is in the domain <code>dom</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Domain_of_a_function">Wikipedia - Domain of a
   * function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Refine(Sin(k*Pi), Element(k, Integers))
   * 0
   * </pre>
   */
  private static class Element extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      if (arg1.isUndefined()) {
        return S.Undefined;
      }
      final IExpr arg2 = ast.arg2();

      if (arg2.isAST(S.Vectors) || arg2.isAST(S.Matrices) || arg2.isAST(S.Arrays)) {
        return SymbolicArrayFunctions.elementOfArrayDomain(arg1, (IAST) arg2, engine);
      }
      if (arg2.isSymbol()) {
        final ISymbol domain = (ISymbol) arg2;
        if (arg1.isAST()) {
          IAST arg1AST = (IAST) arg1;
          if (arg1.isList() || arg1.isAST(S.Alternatives)) {
            if (arg1AST.size() == 1) {
              return S.True;
            }
            if (arg1AST.size() == 2) {
              return F.Element(arg1AST.first(), domain);
            }
            // Element({a, b}, dom) is Element(a | b, dom)
            IASTAppendable result = F.ast(S.Alternatives, arg1AST.size());
            boolean evaled = arg1.isList();
            for (int i = 1; i < arg1AST.size(); i++) {
              final IExpr arg = arg1AST.get(i);
              IExpr assumeDomain = assumeDomain(arg, domain, engine);
              if (assumeDomain.isFalse()) {
                evaled = true;
                return S.False;
              }
              if (assumeDomain.isTrue()) {
                evaled = true;
                continue;
              }
              result.append(arg);
            }
            return evaled ? F.Element(result, domain) : F.NIL;
          }
        }
        return assumeDomain(arg1, domain, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    /**
     * Return {@link S#True} or {@link S#False} if <code>expr</code> is assumed to be in the
     * <code>domain</code> or not to be in the <code>domain</code>.
     *
     * @param expr
     * @param domain
     * @return S.True or S.False if expr is assumed to be in the <code>domain</code> or not to be in
     *         the <code>domain</code>. In all other cases return {@link F#NIL}.
     */
    private IExpr assumeDomain(final IExpr expr, final ISymbol domain, EvalEngine engine) {
      if (expr.isAST(S.Indexed, 3) && expr.first() instanceof IArraySymbol) {
        // a component of a symbolic array lies in the element domain the array declares
        return SymbolicArrayFunctions.domainSubset(((IArraySymbol) expr.first()).getDomain(),
            domain) ? S.True : F.NIL;
      }
      if (domain.isBuiltInSymbol()) {
        ISymbol truthValue;
        final int symbolID = domain.ordinal();
        switch (symbolID) {
          case ID.Algebraics:
            truthValue = AbstractAssumptions.assumeAlgebraic(expr);
            if (truthValue != null) {
              return truthValue;
            }
            if (isTranscendentalConstant(expr, engine)) {
              return S.False;
            }
            // structural test for a compound expression, e.g. Sqrt(2)+2^(1/3) is algebraic
            return AlgebraicNumberUtils.isExplicitAlgebraicNumber(expr) ? S.True : F.NIL;
          case ID.Arrays:
            truthValue = AbstractAssumptions.assumeArray(expr);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Booleans:
            truthValue = AbstractAssumptions.assumeBoolean(expr);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Complexes:
            truthValue = AbstractAssumptions.assumeComplex(expr);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Integers:
            // the numeric test first: a value away from the integers needs no minimal polynomial
            truthValue = AbstractAssumptions.assumeInteger(expr);
            if (truthValue != null) {
              return truthValue;
            }
            if (isTranscendentalConstant(expr, engine)) {
              return S.False;
            }
            if (!expr.isNumber() && AlgebraicNumberUtils.isExplicitAlgebraicNumber(expr)) {
              IExpr rational = algebraicRational(expr, engine);
              if (rational.isPresent()) {
                return F.booleSymbol(rational.isInteger());
              }
            }
            // structural test for a compound expression, e.g. 2*k^3 is an integer if k is one
            return expr.isIntegerResult() ? S.True : F.NIL;
          case ID.Primes:
            return AbstractAssumptions.assumePrime(expr);
          case ID.Rationals:
            // the numeric and structural tests first: a complex value or a transcendental
            // constant needs no minimal polynomial
            truthValue = AbstractAssumptions.assumeRational(expr);
            if (truthValue != null) {
              return truthValue;
            }
            if (isTranscendentalConstant(expr, engine)) {
              return S.False;
            }
            if (!expr.isNumber() && AlgebraicNumberUtils.isExplicitAlgebraicNumber(expr)) {
              IExpr rational = algebraicRational(expr, engine);
              if (rational.isPresent()) {
                return F.booleSymbol(rational.isRational());
              }
            }
            return F.NIL;
          case ID.Reals:
            truthValue = AbstractAssumptions.assumeReal(expr);
            if (truthValue != null) {
              return truthValue;
            }
            // structural test for a compound expression, e.g. x+Gamma(x) is real if x is real
            return expr.isRealResult() ? S.True : F.NIL;
          case ID.NegativeReals:
            return signDomain(expr, -1, false, engine);
          case ID.NonNegativeReals:
            return signDomain(expr, 1, true, engine);
          case ID.NonPositiveReals:
            return signDomain(expr, -1, true, engine);
          case ID.PositiveReals:
            return signDomain(expr, 1, false, engine);
          default:
            break;
        }
      }
      if (domain.isSymbol()) {
        String domainString = domain.toString().toLowerCase(Locale.US);
        if (domainString.equals("real") //
            || domainString.equals("prime") //
            || domainString.equals("integer") //
            || domainString.equals("rational") //
            || domainString.equals("algebraic") //
            || domainString.equals("complex") //
            || domainString.equals("boolean") //
        ) {
          // print warning:
          // The second argument `1` of Element should be one of: Primes, Integers, Rationals,
          // Algebraics, Reals, Complexes or Booleans.
          return Errors.printMessage(S.Element, "bset", F.List(domain), engine);
        }
      }
      return F.NIL;
    }
  }

  /**
   * The rational value of an explicit algebraic number from its minimal polynomial: the root of a
   * polynomial of degree 1, else {@link S#Indeterminate} for an irrational number.
   *
   * @return {@link F#NIL} if the minimal polynomial isn't found
   */
  private static IExpr algebraicRational(IExpr expr, EvalEngine engine) {
    if (expr.leafCount() > 200L) {
      return F.NIL;
    }
    ISymbol x = F.Dummy("x");
    IExpr polynomial = engine.evalQuiet(
        F.TimeConstrained(F.binaryAST2(S.MinimalPolynomial, expr, x), F.C2));
    if (!polynomial.isPolynomial(x)) {
      return F.NIL;
    }
    IExpr degree = engine.evaluate(F.Exponent(polynomial, x));
    if (degree.isOne()) {
      IExpr c0 = engine.evaluate(F.Coefficient(polynomial, x, F.C0));
      IExpr c1 = engine.evaluate(F.Coefficient(polynomial, x, F.C1));
      IExpr root = engine.evaluate(F.Negate(F.Divide(c0, c1)));
      return root.isRational() ? root : F.NIL;
    }
    if (degree.isInteger() && ((IInteger) degree).isGT(F.C1)) {
      return S.Indeterminate;
    }
    return F.NIL;
  }

  /**
   * Test if <code>expr</code> is <code>c0 + c1 T^k</code> with explicit algebraic numbers
   * <code>c0</code> and <code>c1 != 0</code>, a rational <code>k != 0</code> and one of the
   * transcendental numbers <code>T = Pi, E, Log(r), E^r</code>, <code>r</code> rational (and
   * positive and not <code>1</code> for <code>Log(r)</code>): a transcendental number by the
   * theorems of Lindemann and Hermite, which is in none of the domains Integers, Rationals and
   * Algebraics.
   */
  private static boolean isTranscendentalConstant(IExpr expr, EvalEngine engine) {
    IExpr term = expr;
    if (expr.isPlus()) {
      term = null;
      for (IExpr summand : (IAST) expr) {
        if (!AlgebraicNumberUtils.isExplicitAlgebraicNumber(summand)) {
          if (term != null) {
            return false;
          }
          term = summand;
        }
      }
      if (term == null) {
        return false;
      }
    }
    IExpr transcendental = term;
    IASTAppendable coefficient = F.TimesAlloc(4);
    if (term.isTimes()) {
      transcendental = null;
      for (IExpr factor : (IAST) term) {
        if (AlgebraicNumberUtils.isExplicitAlgebraicNumber(factor)) {
          coefficient.append(factor);
        } else if (transcendental != null) {
          return false;
        } else {
          transcendental = factor;
        }
      }
      if (transcendental == null) {
        return false;
      }
    }
    if (transcendental.isPower() && transcendental.base() != S.E) {
      IExpr exponent = transcendental.exponent();
      if (!exponent.isRational() || exponent.isZero()) {
        return false;
      }
      transcendental = transcendental.base();
    }
    if (!isTranscendentalBase(transcendental)) {
      return false;
    }
    if (coefficient.argSize() > 0) {
      // c1 != 0
      IExpr value = engine.evalN(coefficient.oneIdentity1());
      return value.isNumber() && ((org.matheclipse.core.interfaces.INumber) value).abs()
          .evalf() > 1.0e-6;
    }
    return true;
  }

  /** Test if <code>t</code> is <code>Pi, Degree, E, Log(r)</code> or <code>E^r</code>. */
  private static boolean isTranscendentalBase(IExpr t) {
    if (t == S.Pi || t == S.Degree || t == S.E) {
      return true;
    }
    if (t.isAST(S.Log, 2)) {
      IExpr r = t.first();
      return r.isRational() && r.isPositive() && !r.isOne();
    }
    if (t.isPower() && t.base() == S.E) {
      IExpr r = t.exponent();
      return r.isRational() && !r.isZero();
    }
    return false;
  }

  /**
   * Test if the numeric <code>expr</code> has the given sign or, if allowed, is zero.
   *
   * @param sign <code>1</code> or <code>-1</code>
   * @param zero whether <code>0</code> belongs to the domain
   * @return {@link S#True}, {@link S#False}, or {@link F#NIL} if the membership is undecided
   */
  private static IExpr signDomain(IExpr expr, int sign, boolean zero, EvalEngine engine) {
    if (expr.isZero()) {
      return F.booleSymbol(zero);
    }
    if (expr.isNumber() && !expr.isReal()) {
      return S.False;
    }
    if (expr.isNumericFunction()) {
      IExpr value = engine.evaluate(F.N(expr));
      if (value.isReal() && !value.isZero()) {
        return F.booleSymbol(value.isPositive() == sign > 0);
      }
    }
    return F.NIL;
  }

  private static class NotElement extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      if (arg1.isUndefined()) {
        return S.Undefined;
      }
      final IExpr arg2 = ast.arg2();
      if (arg2.isSymbol()) {
        if (arg1.isAST(S.Alternatives)) {
          boolean[] evaled = new boolean[] {false};
          IAST alternatives = (IAST) arg1;
          IASTAppendable andList = F.And();
          alternatives.forEach(x -> {
            IExpr temp = notElement(x, arg2, engine);
            if (temp.isPresent()) {
              evaled[0] = true;
              andList.append(temp);
            } else {
              andList.append(F.NotElement(x, arg2));
            }
          });
          return evaled[0] == true ? andList : F.NIL;
        }
        return notElement(arg1, arg2, engine);
      }
      return F.NIL;
    }

    private static IExpr notElement(final IExpr arg1, final IExpr arg2, final EvalEngine engine) {
      IExpr element = engine.evaluate(F.Element(arg1, arg2));
      if (element.isTrue()) {
        return S.False;
      } else if (element.isFalse()) {
        return S.True;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Refine(expression, assumptions)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluate the <code>expression</code> for the given <code>assumptions</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Refine(Abs(n+Abs(m)), n&gt;=0)
   * Abs(m)+n
   *
   * &gt;&gt; Refine(-Infinity&lt;x, x&gt;0)
   * True
   *
   * &gt;&gt; Refine(Max(Infinity,x,y), x&gt;0)
   * Max(Infinity,y)
   *
   * &gt;&gt; Refine(Sin(k*Pi), Element(k, Integers))
   * 0
   *
   * &gt;&gt; Sin(k*Pi)
   * Sin(k*Pi)
   * </pre>
   */
  private static class Refine extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(S.Refine, ast, 2, engine);
      }
      final IAssumptions assumptions;
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        assumptions =
            org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr, engine);
      } else {
        assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance();
      }
      // Don't wrap the expression in Simplify(): Refine() only applies the assumption driven
      // rewrites of a normal evaluation, it isn't supposed to reshape the users expression.
      // Simplify() itself calls refineAssumptions(), so wrapping would evaluate twice.
      return refineAssumptions(ast.arg1(), assumptions, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
      setOptions(newSymbol, F.list(F.Rule(S.Assumptions, S.$Assumptions)));
    }

  }

  public static IExpr refineAssumptions(final IExpr expr, IAssumptions assumptions,
      EvalEngine engine) {
    if (assumptions != null) {
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        engine.setAssumptions(assumptions);
        IExpr result = engine.evalWithoutNumericReset(unevaluatedCopy(expr));
        IAST pointValues = assumptions.pointValues();
        if (pointValues.argSize() > 0) {
          // a variable which is assumed to be equal to a number, e.g. Refine(x, x==0) -> 0
          IExpr substituted = F.subst(result, pointValues);
          if (substituted != result) {
            result = engine.evalWithoutNumericReset(substituted);
          }
        }
        result = org.matheclipse.core.sympy.assumptions.Refine.refine(result, engine);
        IExpr rooted = rootsOfProducts(result);
        if (rooted.isPresent()) {
          result = engine.evalWithoutNumericReset(rooted);
        }
        return decideRelation(result, assumptions, engine).orElse(result);
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    }
    return engine.evalWithoutNumericReset(expr);
  }

  /**
   * A copy of the tree without the marks of an earlier evaluation: the evaluator would otherwise
   * skip an expression which it evaluated before the assumptions were known.
   */
  private static IExpr unevaluatedCopy(IExpr expr) {
    if (!expr.isAST() || expr.isAssociation()) {
      return expr;
    }
    IASTMutable copy = ((IAST) expr).copy();
    for (int i = 1; i < copy.size(); i++) {
      copy.set(i, unevaluatedCopy(copy.get(i)));
    }
    return copy;
  }

  /**
   * Takes the factors out of a root which leave it as a whole power:
   * <code>(k^4*x)^(1/2)</code> is <code>k^2*x^(1/2)</code> for a positive <code>k</code>,
   * <code>(x^2*y)^(1/2)</code> is <code>-x*y^(1/2)</code> for a negative <code>x</code>, and an
   * even power of a real base leaves a root as an even power. The factors of the denominator are
   * taken out in the same way. A factor which would leave the root as a root again stays inside.
   *
   * @return {@link F#NIL} if <code>expr</code> is not such a root, or no factor leaves it
   */
  private static IExpr rootsOfProducts(IExpr expr) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    if ((ast.topHead().getAttributes() & (ISymbol.HOLDALL | ISymbol.HOLDALLCOMPLETE)) != 0) {
      // what is held is not rewritten
      return F.NIL;
    }
    IExpr root = rootOfProduct(ast);
    if (root.isPresent()) {
      return root;
    }
    IASTMutable copy = null;
    for (int i = 1; i < ast.size(); i++) {
      IExpr part = rootsOfProducts(ast.get(i));
      if (part.isPresent()) {
        if (copy == null) {
          copy = ast.copy();
        }
        copy.set(i, part);
      }
    }
    return copy == null ? F.NIL : copy;
  }

  private static IExpr rootOfProduct(IExpr expr) {
    if (!expr.isPower() || !expr.exponent().isFraction() || !expr.exponent().isPositive()) {
      return F.NIL;
    }
    IFraction root = (IFraction) expr.exponent();
    IExpr base = expr.base();
    IAST factors = base.isTimes() ? (IAST) base : F.list(base);
    IASTAppendable outside = F.TimesAlloc(factors.argSize());
    IASTAppendable inside = F.TimesAlloc(factors.argSize());
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      IExpr whole = F.NIL;
      if (factor.isPower() && factor.exponent().isInteger() && !factor.base().isNumber()) {
        IExpr f = factor.base();
        IInteger e = (IInteger) factor.exponent();
        IExpr power = e.multiply(root);
        if (power.isInteger()) {
          if (f.isPositiveResult() || AbstractAssumptions.assumePositive(f)) {
            whole = F.Power(f, power);
          } else if (e.isEven()
              && (f.isNegativeResult() || AbstractAssumptions.assumeNegative(f))) {
            whole = F.Power(F.Negate(f), power);
          } else if (e.isEven() && ((IInteger) power).isEven() && f.isRealResult()) {
            whole = F.Power(f, power);
          } else if (e.isEven() && f.isRealResult()) {
            // the root of an even power of a real base, which has no sign: (f^2)^(1/2) == Abs(f)
            whole = F.Power(F.Abs(f), power);
          }
        }
      }
      if (whole.isPresent()) {
        outside.append(whole);
      } else {
        inside.append(factor);
      }
    }
    if (outside.argSize() == 0) {
      return F.NIL;
    }
    if (inside.argSize() > 0) {
      outside.append(F.Power(inside.oneIdentity1(), root));
    }
    return outside.oneIdentity1();
  }

  /** At most this many relational assumptions are combined pairwise in {@link #keySum}. */
  private static final int MAX_KEYS = 12;

  /**
   * Decide a relation which the evaluation left open with the assumptions: the sign of
   * <code>lhs-rhs</code> decides it, e.g. <code>x^3 &gt; 0</code> for <code>x&gt;0</code> and
   * <code>a &gt; c</code> for <code>a&gt;b &amp;&amp; b&gt;c</code>. An inequality assumption makes
   * its variables real: <code>Element(x, Reals)</code> holds for <code>x^2&lt;1</code>.
   *
   * @return {@link F#NIL} if the relation isn't decided
   */
  private static IExpr decideRelation(IExpr expr, IAssumptions assumptions, EvalEngine engine) {
    if (expr.isAST(S.Element, 3) && expr.second() == S.Reals && expr.first().isVariable()) {
      IExpr x = expr.first();
      return assumptions.relationalKeys().exists(key -> !key.isFree(x)) ? S.True : F.NIL;
    }
    if (!expr.isAST2()) {
      return F.NIL;
    }
    IExpr head = expr.head();
    if (head != S.Greater && head != S.Less && head != S.GreaterEqual && head != S.LessEqual
        && head != S.Equal && head != S.Unequal) {
      return F.NIL;
    }
    IExpr d = engine.evaluate(F.Subtract(expr.first(), expr.second()));
    if (d.isNumber()) {
      return F.NIL;
    }
    IExpr minusD = engine.evaluate(F.Negate(d));
    boolean positive = d.isPositiveResult() || keySum(d, true, assumptions, engine);
    boolean negative =
        !positive && (minusD.isPositiveResult() || keySum(minusD, true, assumptions, engine));
    boolean nonNegative =
        positive || d.isNonNegativeResult() || keySum(d, false, assumptions, engine);
    boolean nonPositive =
        negative || d.isNonPositiveResult() || keySum(minusD, false, assumptions, engine);
    if (head == S.Greater) {
      return positive ? S.True : nonPositive ? S.False : F.NIL;
    }
    if (head == S.Less) {
      return negative ? S.True : nonNegative ? S.False : F.NIL;
    }
    if (head == S.GreaterEqual) {
      return nonNegative ? S.True : negative ? S.False : F.NIL;
    }
    if (head == S.LessEqual) {
      return nonPositive ? S.True : positive ? S.False : F.NIL;
    }
    if (positive || negative) {
      return head == S.Equal ? S.False : S.True;
    }
    return F.NIL;
  }

  /**
   * <code>d</code> is one or the sum of two relational assumptions which are non negative (and at
   * least one of them positive if <code>strict</code>), plus a non negative rest.
   */
  private static boolean keySum(IExpr d, boolean strict, IAssumptions assumptions,
      EvalEngine engine) {
    IAST keys = assumptions.relationalKeys();
    if (keys.argSize() > MAX_KEYS) {
      return false;
    }
    for (int i = 1; i < keys.size(); i++) {
      IExpr k1 = keys.get(i);
      boolean strict1 = assumptions.isPositive(k1);
      if (!strict1 && !assumptions.isNonNegative(k1)) {
        continue;
      }
      for (int j = i; j < keys.size(); j++) {
        IExpr k2 = keys.get(j);
        boolean strict2 = assumptions.isPositive(k2);
        if (!strict2 && !assumptions.isNonNegative(k2)) {
          continue;
        }
        if (strict && !strict1 && !strict2) {
          continue;
        }
        IExpr rest = engine.evaluate(F.Expand(F.Subtract(d, i == j ? k1 : F.Plus(k1, k2))));
        if (rest.isZero() || (i != j && rest.isNonNegativeResult())) {
          return true;
        }
      }
    }
    return false;
  }

  public static void initialize() {
    Initializer.init();
  }

  private AssumptionFunctions() {}
}
