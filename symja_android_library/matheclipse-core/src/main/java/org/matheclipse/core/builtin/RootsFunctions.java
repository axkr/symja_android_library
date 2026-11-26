package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.evalExpandAll;
import java.util.function.Function;
import java.util.List;
import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.reduce.SparseRealRoots;
import org.matheclipse.core.reflection.system.Root;
import org.matheclipse.core.polynomials.RealRootIsolation;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.Rectangle;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;
import jakarta.annotation.Nonnull;

public class RootsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.NRoots.setEvaluator(new NRoots());
      S.Roots.setEvaluator(new Roots());
      S.RootIntervals.setEvaluator(new RootIntervals());
    }
  }

  /** Determine complex root intervals of a univariate polynomial */
  private static class RootIntervals extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return croots(ast.arg1(), false, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    /**
     * Complex numeric roots intervals.
     *
     * @param arg
     * @param numeric if <code>true</code> create a numerically evaluated result. Otherwise return a
     *        symbolic result.
     */
    public static IAST croots(final IExpr arg, boolean numeric, EvalEngine engine) {

      try {
        VariablesSet eVar = new VariablesSet(arg);
        if (!eVar.isSize(1)) {
          // `1` is not a univariate polynomial with rational number coefficients
          return Errors.printMessage(S.RootIntervals, "nupr", F.List(arg), engine);
        }
        IExpr expr = F.evalExpandAll(arg);
        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // List<IExpr> varList = r;
        // List<IExpr> varList = eVar.getVarList().copyTo();

        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        JASConvert<Complex<BigRational>> jas =
            new JASConvert<Complex<BigRational>>(eVar.getVarList(), cfac);
        GenPolynomial<Complex<BigRational>> poly = jas.numericExpr2JAS(expr);

        if (poly != null) {
          Squarefree<Complex<BigRational>> squarefreeEngine =
              SquarefreeFactory.<Complex<BigRational>>getImplementation(cfac);
          poly = squarefreeEngine.squarefreePart(poly);

          List<Rectangle<BigRational>> roots = cr.complexRoots(poly);

          BigRational len = new BigRational(1, 100000L);

          IASTAppendable resultList = F.ListAlloc(roots.size());

          if (numeric) {
            for (Rectangle<BigRational> root : roots) {
              Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
              resultList.append(chopRoot(JASConvert.jas2Numeric(refine.getCenter(), 0.0)));
            }
          } else {
            IASTAppendable rectangleList;
            for (Rectangle<BigRational> root : roots) {
              rectangleList = F.ListAlloc(4);

              Rectangle<BigRational> refine = cr.complexRootRefinement(root, poly, len);
              rectangleList.append(JASConvert.jas2Complex(refine.getNW()));
              rectangleList.append(JASConvert.jas2Complex(refine.getSW()));
              rectangleList.append(JASConvert.jas2Complex(refine.getSE()));
              rectangleList.append(JASConvert.jas2Complex(refine.getNE()));
              resultList.append(rectangleList);
            }
          }
          EvalAttributes.sort(resultList);
          return resultList;
        }
      } catch (IllegalArgumentException | InvalidBoundaryException | JASConversionException e) {
        //
      }
      // Illegal arguments: \"`1`\" in `2`
      return Errors.printMessage(S.RootIntervals, "argillegal", F.List(arg), engine);
    }
  }

  /**
   *
   *
   * <pre>
   * NRoots(poly)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the numerical roots of polynomial <code>poly</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NRoots(x^3-4*x^2+x+6)
   * {2.9999999999999996,-1.0000000000000002,1.9999999999999998}
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>,
   * <a href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>,
   * <a href="Solve.md">Solve</a>
   */
  private static class NRoots extends AbstractFunctionEvaluator {
    /**
     * Determine the numerical roots of a univariate polynomial
     *
     * <p>
     * See Wikipedia entries for:
     * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
     * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
     * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
     *
     * @see Roots
     */
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();

      IAST variables;
      if (ast.size() == 2) {
        VariablesSet eVar = new VariablesSet(ast.arg1());
        if (!eVar.isSize(1)) {
          // `1` is not a valid variable.
          return Errors.printMessage(ast.topHead(), "ivar", F.List(ast.arg1()), engine);
        }
        variables = eVar.getVarList();
      } else {
        variables = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (variables.isNIL()) {
          return F.NIL;
        }
      }
      if (variables.size() <= 1) {
        return F.NIL;
      }

      if (arg1.isEqual()) {
        IAST equalAST = (IAST) arg1;
        if (equalAST.arg2().isZero()) {
          arg1 = equalAST.arg1();
        } else {
          arg1 = engine.evaluate(F.Subtract(equalAST.arg1(), equalAST.arg2()));
        }
      } else {
        // an equation like 1==0 has already evaluated to False, which is no polynomial in x
        if ((!arg1.isPolynomialStruct() && !arg1.isPolynomial(variables)) || arg1.isTrue()
            || arg1.isFalse()) {
          // `1` is expected to be a polynomial equation in the variable `2` with numeric
          // coefficients.
          return Errors.printMessage(ast.topHead(), "nnumeq",
              F.List(arg1, variables.argSize() == 1 ? variables.arg1() : variables), engine);
        }
      }

      IAST values = numericRoots(arg1, variables, engine);
      if (values.isNIL() || ast.isAST1()) {
        return values;
      }
      IExpr variable = variables.arg1();
      return F.mapFunction(S.Or, values, t -> F.Equal(variable, t));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

  }

  /** The largest value of the option <code>Multiplicity</code>. */
  private static final int MAX_MULTIPLICITY = 1 << 16;

  /** The highest degree of an exact polynomial which is factored before its roots are computed. */
  private static final int MAX_FACTORED_DEGREE = 32;

  /**
   * The numerical roots of a polynomial, each with its multiplicity. An exact polynomial is split
   * into its factors first: <code>(z-1)^3*(z^2+1)</code> has the root <code>1.0</code> three times,
   * which the numerical root finders only reach approximately.
   *
   * @param polynomial the polynomial or a rational function of it
   * @param variables the list of the variable
   * @return the list of the numbers or {@link F#NIL}
   */
  public static IAST numericRoots(IExpr polynomial, IAST variables, EvalEngine engine) {
    IExpr roots = factoredComplexRoots(polynomial, variables, engine);
    if (roots.isNIL()) {
      roots = complexRoots(polynomial, variables, engine);
    }
    if (!roots.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) roots;
    return F.mapRange(1, list.size(), i -> engine.evalN(list.get(i)));
  }

  /**
   * The roots of each factor of a polynomial with rational coefficients.
   *
   * @return {@link F#NIL} if <code>polynomial</code> isn't such a polynomial or doesn't split
   */
  private static IAST factoredComplexRoots(IExpr polynomial, IAST variables, EvalEngine engine) {
    if (variables.argSize() != 1 || !variables.arg1().isSymbol()) {
      return F.NIL;
    }
    IExpr variable = variables.arg1();
    IExpr expanded = evalExpandAll(polynomial, engine);
    if (!expanded.isPolynomial(variables)
        || S.Exponent.of(engine, expanded, variable).toIntDefault() > MAX_FACTORED_DEGREE) {
      return F.NIL;
    }
    IExpr[] coefficients = Root.polynomialCoefficients(F.subst(expanded, variable, F.Slot1));
    if (coefficients == null || !isRationalCoefficients(coefficients)) {
      return F.NIL;
    }
    return rootsByFactors(expanded, variable, 1, factor -> complexRoots(factor, variables, engine),
        engine);
  }

  /**
   * The roots of each factor of a polynomial, repeated with the multiplicity of the factor times
   * <code>multiplicity</code>, or once for a <code>multiplicity</code> of 0.
   *
   * @param expanded the expanded polynomial
   * @param factorRoots the roots of one factor
   * @return {@link F#NIL} if the roots of a factor are unknown, or if <code>multiplicity</code> is
   *         at most 1 and the polynomial doesn't split
   */
  private static IAST rootsByFactors(IExpr expanded, IExpr variable, int multiplicity,
      Function<IExpr, IExpr> factorRoots, EvalEngine engine) {
    IExpr factors = engine.evalQuiet(F.FactorList(expanded));
    if (!factors.isList()) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(8);
    int parts = 0;
    for (IExpr factor : (IAST) factors) {
      int exponent = factor.second().toIntDefault();
      if (factor.first().isFree(variable)) {
        continue;
      }
      IExpr roots = factorRoots.apply(factor.first());
      if (!roots.isList() || exponent < 1) {
        return F.NIL;
      }
      parts += exponent;
      for (long i = multiplicity == 0 ? 1L : (long) exponent * multiplicity; i > 0; i--) {
        engine.checkTimeLimit();
        result.appendArgs((IAST) roots);
      }
    }
    if (multiplicity <= 1 && parts < 2) {
      return F.NIL;
    }
    EvalAttributes.sort(result);
    return result;
  }

  /**
   *
   *
   * <pre>
   * Roots(polynomial - equation, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * determine the roots of a univariate polynomial equation with respect to the variable <code>
   * var</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Roots(3*x^3-5*x^2+5*x-2==0,x)
   * x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)
   * </pre>
   */
  private static class Roots extends AbstractFunctionOptionEvaluator {

    /**
     * Determine the roots of a univariate polynomial
     *
     * <p>
     * See Wikipedia entries for:
     * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>,
     * <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and
     * <a href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
     */
    @Override
    public @AllowNIL IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      boolean cubicsInRadicals = options[0].isTrue();
      boolean quarticsInRadicals = options[1].isTrue();
      IExpr modulus = options[2];
      // a multiplicity which is not an integer is the default 1
      int multiplicity = options[3].isInteger() ? options[3].toIntDefault() : 1;
      // the left-hand side of the equations of the roots
      final IExpr equatedTo = options[4];
      if (multiplicity < 0 || multiplicity > MAX_MULTIPLICITY) {
        return F.NIL;
      }
      if (argSize > 0 && argSize < ast.argSize()) {
        ast = ast.copyUntil(argSize + 1);
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isEqual()) {
        IAST equalAST = (IAST) arg1;
        if (equalAST.arg2().isZero()) {
          arg1 = equalAST.arg1();
        } else {
          arg1 = engine.evaluate(F.Subtract(equalAST.arg1(), equalAST.arg2()));
        }
      } else {
        // `1` is not an equation.
        return Errors.printMessage(S.Roots, "eqn", F.List(arg1));
      }
      VariablesSet eVar = null;
      if (ast.arg2().isList()) {
        eVar = new VariablesSet(ast.arg2());
      } else {
        eVar = new VariablesSet();
        eVar.add(ast.arg2());
      }
      if (!eVar.isSize(1)) {
        // factorization only possible for univariate polynomials

        // LOGGER.log(engine.getLogLevel(),
        // "{}: factorization only possible for univariate polynomials at position 2 instead of {}",
        // ast.topHead(), ast.arg2());
        return F.NIL;
      }
      IAST variables = eVar.getVarList();
      IExpr variable = variables.arg1();
      if (options[1].isFalse() && isQuadraticInSquare(arg1, variable, engine)) {
        // a quartic of this form has a very simple form
        quarticsInRadicals = true;
      }
      if (modulus.isInteger() && !modulus.isZero()) {
        // the residues which solve the equation; a composite modulus has no field of residues
        IExpr residues =
            engine.evaluate(F.Reduce(F.Equal(arg1, F.C0), variable, F.Rule(S.Modulus, modulus)));
        return residues.isAST(S.Reduce) ? F.NIL : residues;
      }
      IAST list = rootsOfFactors(arg1, variables, multiplicity, cubicsInRadicals,
          quarticsInRadicals, engine);
      if (list.isNIL()) {
        list = roots(arg1, false, variables, true, true, cubicsInRadicals, quarticsInRadicals,
            engine);
      }
      if (list.isPresent()) {
        final IExpr lhs = equatedTo == S.Null ? variable : equatedTo;
        return F.mapFunction(S.Or, list, t -> F.Equal(lhs, t));
      }
      return F.NIL;
    }

    /**
     * The roots of a product factor by factor, each repeated with its multiplicity times
     * <code>multiplicity</code>. The factors of a polynomial with symbolic coefficients are of low
     * degree, where the expanded product would be solved by the general formulas.
     *
     * @return {@link F#NIL} if <code>multiplicity</code> is 1 and the polynomial has numeric
     *         coefficients or doesn't split
     */
    private static IAST rootsOfFactors(IExpr polynomial, IAST variables, int multiplicity,
        boolean cubicsInRadicals, boolean quarticsInRadicals, EvalEngine engine) {
      IExpr variable = variables.arg1();
      IExpr expanded = evalExpandAll(polynomial, engine);
      if (!expanded.isPolynomial(variables)
          || (multiplicity <= 1 && new VariablesSet(expanded).isSize(1)
              && (multiplicity == 0 || !hasRepeatedFactor(expanded, engine)))) {
        return F.NIL;
      }
      return rootsByFactors(expanded, variable, multiplicity,
          factor -> roots(factor, false, variables, true, true, cubicsInRadicals,
              quarticsInRadicals, engine),
          engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // `Cubics` and `Quartics` default to `True` here: `Roots` is the radical solver, and the
      // callers which want the inert form ask for it explicitly
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.Cubics, S.Quartics, S.Modulus, S.Multiplicity, S.EquatedTo}, //
          new IExpr[] {S.True, S.True, F.C0, F.C1, S.Null});
    }
  }

  public static IAST complexRoots(final IExpr arg1, IAST variables, EvalEngine engine) {
    if (variables.size() != 2) {
      // factor only possible for univariate polynomials
      // LOGGER.log(engine.getLogLevel(),
      // "NRoots: factorization only possible for univariate polynomials");
      return F.NIL;
    }
    IExpr expr = evalExpandAll(arg1, engine);

    IExpr variable = variables.arg1();
    double[] coefficients = Expr2Object.toPolynomial(expr, variable, engine);
    if (coefficients != null) {
      try {
        IASTMutable list;
        if (coefficients.length <= 4) {
          IASTAppendable p = F.PlusAlloc(coefficients.length);
          for (int i = 0; i < coefficients.length; i++) {
            if (coefficients[i] == 0.0) {
              // a small coefficient is a coefficient: 1.0*^-20*x^2 has two roots
              continue;
            }
            if (i == 0) {
              p.append(F.num(coefficients[i]));
            } else {
              p.append(F.Times(F.num(coefficients[i]), F.Power(variable, i)));
            }
          }
          expr = engine.evaluate(p);
          list = QuarticSolver.solve(p, variables.arg1());
          for (int i = 1; i < list.size(); i++) {
            expr = engine.evaluate(list.get(i));
            if (!expr.isNumber() && expr.isNumericFunction(true)) {
              // a formula of the roots, Sqrt(-10.0): its residue is set to 0 as a number
              expr = engine.evalN(expr);
            }
            if (expr.isInexactNumber()) {
              list.set(i, chopRoot(expr));
            }
          }
          int degree = coefficients.length - 1;
          if (list.argSize() < degree && coefficients[degree] != 0.0) {
            // the formulas give a multiple root once: (x-2.5)^2*(x+1) has the roots -1, 2.5, 2.5
            org.hipparchus.complex.Complex[] roots = multipleRootsMerged(coefficients, true);
            if (roots != null && roots.length == degree) {
              for (int i = 0; i < roots.length; i++) {
                roots[i] = chopRoot(roots[i]);
              }
              list = Object2Expr.convertComplex(true, roots);
            }
          }
          if (list.isEmptyList()) {
            return F.NIL;
          }
        } else {
          org.hipparchus.complex.Complex[] roots = multipleRootsMerged(coefficients, true);
          if (roots == null) {
            return F.NIL;
          }
          for (int i = 0; i < roots.length; i++) {
            roots[i] = chopRoot(roots[i]);
          }
          list = Object2Expr.convertComplex(true, roots);
        }
        EvalAttributes.sort(list);
        return list;
      } catch (org.hipparchus.exception.MathRuntimeException mrex) {
        // LOGGER.debug("RootsFunctions.roots() failed", mrex);
        return Errors.printMessage(S.NRoots, mrex, engine);
      }
    }
    IExpr denom = F.C1;
    if (expr.isAST()) {
      expr = AlgebraUtil.together((IAST) expr, engine);

      // split expr into numerator and denominator
      denom = engine.evaluate(F.Denominator(expr));
      if (!denom.isOne()) {
        // search roots for the numerator expression
        expr = engine.evaluate(F.Numerator(expr));
      }
    }
    return rootsOfVariable(expr, denom);
  }

  private static IAST rootsOfVariable(final IExpr expr, final IExpr denom) {

    IAST resultList = RootIntervals.croots(expr, true, EvalEngine.get());
    if (resultList.isPresent()) {
      // IAST result = F.list();
      // if (resultList.size() > 0) {
      // result.appendArgs(resultList);
      // }
      // return result;
      return resultList;
    }
    return F.NIL;
  }

  public static IAST roots(final IExpr arg1, boolean numericSolutions, IAST variables,
      EvalEngine engine) {
    return roots(arg1, numericSolutions, variables, true, true, engine);
  }

  public static IAST roots(final IExpr arg1, boolean numericSolutions, IAST variables,
      boolean createSet, boolean sort, EvalEngine engine) {
    return roots(arg1, numericSolutions, variables, createSet, sort, true, true, engine);
  }

  /**
   * @param cubicsInRadicals the value of the {@link S#Cubics} option
   * @param quarticsInRadicals the value of the {@link S#Quartics} option
   */
  public static IAST roots(final IExpr arg1, boolean numericSolutions, IAST variables,
      boolean createSet, boolean sort, boolean cubicsInRadicals, boolean quarticsInRadicals,
      EvalEngine engine) {

    IExpr expr = evalExpandAll(arg1, engine);

    IExpr denom = F.C1;
    if (expr.isAST()) {
      expr = AlgebraUtil.together((IAST) expr, engine);

      // split expr into numerator and denominator
      denom = S.Denominator.of(engine, expr);
      if (!denom.isOne()) {
        // search roots for the numerator expression
        expr = S.Numerator.of(engine, expr);
      }
    }
    IAST result = rootsOfVariable(expr, denom, variables, numericSolutions, createSet, sort, true,
        cubicsInRadicals, quarticsInRadicals, engine);
    if (result.isPresent()) {
      result = (IAST) engine.evaluate(result);
    }
    return result;
  }

  /**
   * Given a set of polynomial coefficients, compute the roots of the polynomial. Depending on the
   * polynomial being considered the roots may contain complex numbers. When complex numbers are
   * present, they will come in pairs of complex conjugate's. Implements the Laguerre's Method for
   * root finding of real coefficient polynomials
   *
   * @param coefficients coefficients of the polynomial.
   * @return the roots of the polynomial or {@link F#NIL} if an exception occurs
   */
  protected static IAST findRoots(double... coefficients) {
    try {
      org.hipparchus.complex.Complex[] complexRoots = multipleRootsMerged(coefficients);
      if (complexRoots == null) {
        return F.NIL;
      }
      return F.mapRange(0, complexRoots.length,
          i -> chopRoot(F.complexNum(complexRoots[i].getReal(), complexRoots[i].getImaginary())));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // solveAllComplex may throw MathIllegalArgumentException, NullArgumentException,
      // MathIllegalStateException
      Errors.printMessage(S.Roots, rex, EvalEngine.get());
    }
    return F.NIL;
  }

  /**
   * Compute a set of polynomial coefficients and the roots for the polynomial coefficients.
   * Depending on the polynomial being considered the roots may contain complex numbers. When
   * complex numbers are present they will come in pairs of complex conjugate's.
   * 
   * @param polynomialExpr
   * @param variables
   * @return the roots of the polynomial or {@link F#NIL} if an exception occurs
   */
  /**
   * Test if the polynomial is a binomial <code>a*x^n + b*x^m</code> in the given variable, i.e. one
   * of the &quot;very simple forms&quot; which
   * {@link #unitPolynomial(int, org.matheclipse.core.polynomials.longexponent.ExprPolynomial)}
   * solves in radicals whatever the {@link S#Cubics} and {@link S#Quartics} options request.
   *
   * @param polynomial an expanded polynomial
   * @param variable the polynomial variable
   */
  private static boolean isBinomialInVariable(IExpr polynomial, IExpr variable) {
    if (!polynomial.isPlus()) {
      // a single monomial
      return true;
    }
    return polynomial.argSize() <= 2;
  }

  /** Test if an exact <code>polynomial</code> has a factor of a power above 1. */
  private static boolean hasRepeatedFactor(IExpr polynomial, EvalEngine engine) {
    if (!polynomial.isFree(IExpr::isInexactNumber, false)) {
      return false;
    }
    IExpr factors = engine.evalQuiet(F.FactorList(polynomial));
    return factors.isList() && ((IAST) factors)
        .exists(factor -> factor.isList2() && factor.second().toIntDefault() > 1);
  }

  /** Test if <code>polynomial</code> is a quartic with only even powers of <code>variable</code>. */
  private static boolean isQuadraticInSquare(IExpr polynomial, IExpr variable, EvalEngine engine) {
    IExpr coefficients = engine.evaluate(F.CoefficientList(polynomial, variable));
    return coefficients.isList() && coefficients.argSize() == 5 && coefficients.getAt(2).isZero()
        && coefficients.getAt(4).isZero();
  }

  /**
   * Represent a general cubic or quartic factor by inert {@link S#Root} objects instead of solving
   * it with the explicit radical formulas of Cardano and Ferrari.
   *
   * <p>
   * <code>Cubics-&gt;False</code> and <code>Quartics-&gt;False</code> ask for the roots of cubics
   * and quartics &quot;that do not have very simple forms&quot; to be given implicitly. The simple
   * forms are the binomials, which {@link #isBinomialInVariable(IExpr, IExpr)} keeps out of this
   * path, and the reducible polynomials, whose factors reach this method one at a time with a lower
   * degree.
   *
   * @param polynomial an irreducible polynomial factor in <code>variable</code>
   * @param variable the polynomial variable
   * @param cubicsInRadicals the value of the {@link S#Cubics} option
   * @param quarticsInRadicals the value of the {@link S#Quartics} option
   * @param numericSolutions <code>true</code> if the caller asked for numerical solutions
   * @param allowRootObjects <code>false</code> if the caller cannot use inert objects
   * @param engine the evaluation engine
   * @return the {@link S#Root} objects of the factor, or {@link F#NIL} if it should be solved in
   *         radicals after all
   */
  private static IAST generalRootObjects(IExpr polynomial, IExpr variable, boolean cubicsInRadicals,
      boolean quarticsInRadicals, boolean numericSolutions, boolean allowRootObjects,
      EvalEngine engine) {
    if ((cubicsInRadicals && quarticsInRadicals) || !allowRootObjects || numericSolutions) {
      return F.NIL;
    }
    if (isBinomialInVariable(polynomial, variable)) {
      return F.NIL;
    }
    int degree = S.Exponent.of(engine, polynomial, variable).toIntDefault();
    if ((degree == 3 && !cubicsInRadicals) || (degree == 4 && !quarticsInRadicals)) {
      return rootObjects(polynomial, variable, degree, numericSolutions, engine);
    }
    return F.NIL;
  }

  /**
   * Represent the roots of a polynomial which has no closed radical form as inert {@link S#Root}
   * objects <code>Root(f&amp;, k, 0)</code> for <code>k = 1, ..., degree</code>.
   *
   * <p>
   * By Abel-Ruffini a general polynomial of degree five or higher cannot be solved in radicals, so
   * the only alternatives are an exact but inert symbolic representation or a numerical
   * approximation. Return the exact <code>Root</code> objects, which stay usable in further exact
   * computations and can be evaluated to any precision on demand with <code>N(...)</code>;
   * returning floating point numbers instead silently turns an exact computation into an
   * approximate one.
   *
   * <p>
   * This is only applied to a factor which the radical solvers already declined: binomials such as
   * <code>x^5-2</code> are resolved earlier by
   * {@link #unitPolynomial(int, org.matheclipse.core.polynomials.longexponent.ExprPolynomial)} and
   * everything of degree <code>&lt;= 4</code> by {@link QuarticSolver}, so those keep their radical
   * form. Since the caller iterates over the irreducible factors, the <code>Root</code> objects
   * refer to the factor rather than to the original polynomial - e.g. <code>x^6+2*x+1</code>
   * factorizes into <code>(1+x)*(1+x-x^2+x^3-x^4+x^5)</code> and yields the exact root
   * <code>-1</code> plus five <code>Root</code> objects of the quintic factor.
   *
   * @param polynomial an irreducible polynomial factor in <code>variable</code>
   * @param variable the polynomial variable
   * @param numericSolutions if <code>true</code> the caller (e.g. {@link S#NSolve}) asked for
   *        numerical solutions, so no inert objects may be returned
   * @param engine the evaluation engine
   * @return the list of <code>Root(f&amp;, k, 0)</code> objects, or {@link F#NIL} if
   *         <code>polynomial</code> isn't an exact polynomial of degree <code>&gt;= 5</code>
   */
  private static IAST rootObjects(IExpr polynomial, IExpr variable, boolean numericSolutions,
      EvalEngine engine) {
    return rootObjects(polynomial, variable, 5, numericSolutions, engine);
  }

  /**
   * @param minDegree the lowest degree which is represented by {@link S#Root} objects. Degree
   *        <code>5</code> is the Abel-Ruffini bound; the {@link S#Cubics} and {@link S#Quartics}
   *        options lower it to <code>3</code> or <code>4</code> to request the inert form for the
   *        general cubics and quartics as well.
   */
  private static IAST rootObjects(IExpr polynomial, IExpr variable, int minDegree,
      boolean numericSolutions, EvalEngine engine) {
    if (numericSolutions || !variable.isSymbol() || polynomial.isNumericMode()) {
      return F.NIL;
    }
    if (((ISymbol) variable).getContext() == Context.DUMMY) {
      // An internal variable introduced by Decompose() (Solve#solveViaDecomposition) or by
      // PolynomialHomogenization, which will be substituted away again. A Root object names the
      // polynomial of *its own* variable, so an inner-layer Root would end up wrapped in the
      // radicals of the back-substitution - technically exact, but an unusable answer. Leave those
      // layers to the numerical solver.
      return F.NIL;
    }
    if (!polynomial.isPolynomial(F.list(variable))) {
      return F.NIL;
    }
    int degree = S.Exponent.of(engine, polynomial, variable).toIntDefault();
    if (degree < minDegree || degree > Config.MAX_POLYNOMIAL_DEGREE) {
      return F.NIL;
    }
    // the Root object identifies the polynomial by a pure function of Slot1
    IAST function = F.Function(F.subst(polynomial, variable, F.Slot1));
    return F.mapRange(1, degree + 1, k -> F.ternaryAST3(S.Root, function, F.ZZ(k), F.C0));
  }

  /**
   * From this degree on the real roots of a polynomial equation over the reals are isolated
   * directly, see {@link #realRootsOfHighDegree(IExpr, IExpr, EvalEngine)}.
   */
  private static final int REAL_ROOTS_ONLY_DEGREE = 50;

  /**
   * The integer coefficients of the square-free part of a polynomial with rational coefficients,
   * the coefficient of <code>x^i</code> at index <code>i</code>.
   *
   * @param squareFree receives the square-free part itself at index 0, if it is not
   *        <code>null</code>
   * @return <code>null</code> if <code>polynomial</code> is no such polynomial in
   *         <code>variable</code>
   */
  private static java.math.BigInteger[] squareFreeIntegerCoefficients(IExpr polynomial,
      IExpr variable, IExpr[] squareFree, EvalEngine engine) {
    if (!variable.isSymbol() || !polynomial.isPolynomial(F.list(variable))) {
      return null;
    }
    IExpr[] coefficients = Root.polynomialCoefficients(F.subst(polynomial, variable, F.Slot1));
    if (coefficients == null || !isRationalCoefficients(coefficients)) {
      return null;
    }
    IExpr gcd = engine.evalQuiet(F.PolynomialGCD(polynomial, F.D(polynomial, variable)));
    if (!gcd.isFree(variable)) {
      polynomial = engine.evalQuiet(F.PolynomialQuotient(polynomial, gcd, variable));
      coefficients = Root.polynomialCoefficients(F.subst(polynomial, variable, F.Slot1));
      if (coefficients == null || !isRationalCoefficients(coefficients)) {
        return null;
      }
    }
    java.math.BigInteger denominator = java.math.BigInteger.ONE;
    for (IExpr coefficient : coefficients) {
      java.math.BigInteger d = ((IRational) coefficient).toBigDenominator();
      denominator = denominator.multiply(d).divide(denominator.gcd(d));
    }
    java.math.BigInteger[] integers = new java.math.BigInteger[coefficients.length];
    for (int i = 0; i < coefficients.length; i++) {
      IRational r = (IRational) coefficients[i];
      integers[i] = r.toBigNumerator().multiply(denominator.divide(r.toBigDenominator()));
    }
    if (squareFree != null) {
      squareFree[0] = polynomial;
    }
    return integers;
  }

  /**
   * A polynomial whose real roots are found without factoring it: the degree is at least
   * {@link #REAL_ROOTS_ONLY_DEGREE}, it has three or more terms (a binomial has roots in radicals)
   * and it is no polynomial in a power <code>x^m</code> (which is solved at the lower degree).
   *
   * @param coefficients the coefficient of <code>x^i</code> at index <code>i</code>
   */
  public static boolean isHighDegreeGeneric(IExpr[] coefficients) {
    if (coefficients.length - 1 < REAL_ROOTS_ONLY_DEGREE) {
      return false;
    }
    int terms = 0;
    int gcd = 0;
    for (int i = 0; i < coefficients.length; i++) {
      if (!coefficients[i].isZero()) {
        terms++;
        gcd = java.math.BigInteger.valueOf(gcd).gcd(java.math.BigInteger.valueOf(i)).intValue();
      }
    }
    return terms >= 3 && gcd == 1;
  }

  /** Whether <code>n/d</code> is a root of the polynomial with the integer coefficients. */
  private static boolean isRationalRoot(java.math.BigInteger[] coefficients, IRational value) {
    java.math.BigInteger n = value.toBigNumerator();
    java.math.BigInteger d = value.toBigDenominator();
    if (d.bitLength() > 64) {
      return false;
    }
    // d^degree * p(n/d) by Horner's rule
    final int degree = coefficients.length - 1;
    java.math.BigInteger sum = coefficients[degree];
    java.math.BigInteger power = java.math.BigInteger.ONE;
    for (int i = degree - 1; i >= 0; i--) {
      power = power.multiply(d);
      sum = sum.multiply(n).add(coefficients[i].multiply(power));
    }
    return sum.signum() == 0;
  }

  private static boolean isRationalCoefficients(IExpr[] coefficients) {
    for (IExpr coefficient : coefficients) {
      if (!coefficient.isRational()) {
        return false;
      }
    }
    return true;
  }

  /**
   * The real roots of a square-free polynomial with rational coefficients as machine numbers in
   * ascending order. They come from an exact isolation of the roots, so their number does not
   * depend on a tolerance.
   *
   * @return <code>null</code> if <code>polynomial</code> is no polynomial with rational
   *         coefficients in <code>variable</code>, or it has a multiple root
   */
  public static double[] isolatedRealRoots(IExpr polynomial, IExpr variable, EvalEngine engine) {
    try {
      IExpr[] squareFree = new IExpr[1];
      java.math.BigInteger[] integers =
          squareFreeIntegerCoefficients(polynomial, variable, squareFree, engine);
      if (integers == null || squareFree[0] != polynomial) {
        // a multiple root is counted with its multiplicity by the numeric root finder
        return null;
      }
      return RealRootIsolation.realRoots(integers);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  /**
   * The real roots of a polynomial of a high degree with rational coefficients, in ascending
   * order: a root which is found exactly as a number, and <code>Root(f, k, 0)</code> of the
   * square-free part otherwise. The polynomial is not factored into its irreducible factors, which
   * at such a degree can take much longer than finding its real roots.
   *
   * @return {@link F#NIL} if the degree is below {@link #REAL_ROOTS_ONLY_DEGREE}, the polynomial
   *         has fewer than three terms (a binomial has roots in radicals) or its coefficients are
   *         not rational
   */
  public static IAST realRootsOfHighDegree(IExpr polynomial, IExpr variable, EvalEngine engine) {
    return realRoots(polynomial, variable, true, engine);
  }

  /**
   * The real roots of a polynomial with rational coefficients, in ascending order, as numbers
   * where a root is rational and as <code>Root(f, k, 0)</code> of the square-free part otherwise.
   * This is the answer for a polynomial whose roots in radicals are not recognized as real: the
   * three real roots of a cubic are sums of cube roots of complex numbers.
   *
   * @return {@link F#NIL} if the coefficients are not rational
   */
  public static IAST realRootsExact(IExpr polynomial, IExpr variable, EvalEngine engine) {
    return realRoots(polynomial, variable, false, engine);
  }

  private static IAST realRoots(IExpr polynomial, IExpr variable, boolean highDegreeOnly,
      EvalEngine engine) {
    try {
      if (!variable.isSymbol() || ((ISymbol) variable).getContext() == Context.DUMMY
          || !polynomial.isPolynomial(F.list(variable))) {
        return F.NIL;
      }
      IExpr body = F.subst(polynomial, variable, F.Slot1);
      SparseRealRoots.Terms terms = SparseRealRoots.of(body);
      if (terms != null && terms.size() >= 3 && terms.degree() >= SPARSE_DEGREE) {
        // a few terms of a high degree: the dense isolation would work on every coefficient. A
        // multiple root, which the sparse method can't separate, is left to the dense isolation
        IAST sparse = sparseRealRoots(terms, engine);
        if (sparse.isPresent()) {
          return sparse;
        }
      }
      IExpr[] coefficients = Root.polynomialCoefficients(body);
      if (coefficients == null || (highDegreeOnly && !isHighDegreeGeneric(coefficients))) {
        return F.NIL;
      }
      IExpr[] squareFree = new IExpr[1];
      java.math.BigInteger[] integers =
          squareFreeIntegerCoefficients(polynomial, variable, squareFree, engine);
      if (integers == null) {
        return F.NIL;
      }
      List<RealRootIsolation.RootInterval> intervals = RealRootIsolation.isolate(integers);
      if (intervals == null) {
        return F.NIL;
      }
      // the Root object names the polynomial with integer coefficients
      IASTAppendable plus = F.PlusAlloc(integers.length);
      for (int i = 0; i < integers.length; i++) {
        if (integers[i].signum() != 0) {
          plus.append(i == 0 ? F.ZZ(integers[i])
              : F.Times(F.ZZ(integers[i]), F.Power(F.Slot1, F.ZZ(i))));
        }
      }
      IAST function = F.Function(engine.evaluate(plus));
      IASTAppendable roots = F.ListAlloc(intervals.size());
      for (int k = 1; k <= intervals.size(); k++) {
        RealRootIsolation.RootInterval interval = intervals.get(k - 1);
        if (interval.isExact()) {
          roots.append(engine.evaluate(F.QQ(interval.lowerNumerator(), interval.denominator())));
          continue;
        }
        // a root which is a rational number with a small denominator is that number
        double value = RealRootIsolation.refine(integers, interval).doubleValue();
        IExpr candidate = engine.evalQuiet(F.Rationalize(F.num(value), F.num(1.0e-12)));
        if (candidate.isRational() && isRationalRoot(integers, (IRational) candidate)) {
          roots.append(candidate);
        } else {
          roots.append(F.ternaryAST3(S.Root, function, F.ZZ(k), F.C0));
        }
      }
      return roots;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** From this degree on a polynomial of a few terms has its real roots found term by term. */
  public static final int SPARSE_DEGREE = 200;

  /**
   * The real roots of a sparse polynomial in ascending order: <code>0</code> and the rational
   * roots as numbers, the others as <code>Root(f, k, 0)</code> of the polynomial without the
   * factor <code>x^e0</code>.
   *
   * @return {@link F#NIL} if a root seems to be multiple
   */
  private static IAST sparseRealRoots(SparseRealRoots.Terms terms, EvalEngine engine) {
    List<org.apfloat.Apfloat> nonzero = SparseRealRoots.nonzeroRealRoots(terms);
    if (nonzero == null) {
      return F.NIL;
    }
    IAST function = terms.function(true, engine);
    List<IExpr> radicals = powerRadicals(terms, engine);
    IASTAppendable roots = F.ListAlloc(nonzero.size() + 1);
    boolean zero = terms.zeroMultiplicity() > 0;
    for (int k = 1; k <= nonzero.size(); k++) {
      org.apfloat.Apfloat root = nonzero.get(k - 1);
      if (zero && root.signum() > 0) {
        roots.append(F.C0);
        zero = false;
      }
      double value = root.doubleValue();
      IExpr radical = F.NIL;
      for (IExpr candidate : radicals) {
        if (Math.abs(candidate.evalf() - value) <= 1.0e-10 * (1.0 + Math.abs(value))) {
          radical = candidate;
          break;
        }
      }
      if (radical.isPresent()) {
        roots.append(radical);
        continue;
      }
      IExpr candidate = engine.evalQuiet(F.Rationalize(F.num(value), F.num(1.0e-12)));
      if (candidate.isRational() && terms.isRoot((IRational) candidate)) {
        roots.append(candidate);
      } else {
        roots.append(F.ternaryAST3(S.Root, function, F.ZZ(k), F.C0));
      }
    }
    if (zero) {
      roots.append(F.C0);
    }
    return roots;
  }

  /**
   * The real roots in radicals of a polynomial of degree at most 4 in <code>y = x^g</code>: the
   * real <code>g</code>-th roots of its real roots in <code>y</code>, both signs for an even
   * <code>g</code>. Empty if the polynomial has no such form.
   */
  private static List<IExpr> powerRadicals(SparseRealRoots.Terms terms, EvalEngine engine) {
    List<IExpr> radicals = new java.util.ArrayList<IExpr>();
    long g = terms.exponentGcd();
    if (g < 2 || terms.degreeInPower() > 4) {
      return radicals;
    }
    ISymbol y = F.Dummy("y");
    IExpr q = engine.evaluate(F.subst(terms.function(true, engine).first(), F.Slot1,
        F.Power(y, F.QQ(1, g))));
    IExpr solutions = engine.evalQuiet(F.Solve(F.Equal(q, F.C0), y, S.Reals));
    if (!solutions.isListOfLists()) {
      return radicals;
    }
    IExpr exponent = F.QQ(1, g);
    for (IExpr solution : (IAST) solutions) {
      IExpr c = solution.first().second();
      if (!c.isRealResult()) {
        continue;
      }
      int sign = engine.evaluate(F.Sign(c)).toIntDefault();
      if (sign > 0) {
        IExpr r = engine.evaluate(F.Power(c, exponent));
        radicals.add(r);
        if (g % 2 == 0) {
          radicals.add(engine.evaluate(F.Negate(r)));
        }
      } else if (sign < 0 && g % 2 == 1) {
        radicals.add(engine.evaluate(F.Negate(F.Power(F.Negate(c), exponent))));
      }
    }
    return radicals;
  }

  public static IAST findRoots(IExpr polynomialExpr, final IAST variables) {
    double[] coefficients = coefficients(polynomialExpr, (ISymbol) variables.arg1());
    if (coefficients == null) {
      return F.NIL;
    }
    return findRoots(coefficients);
  }

  public static IASTMutable rootsOfExprPolynomial(final IExpr expr, IAST varList, boolean createSet,
      boolean rootsOfQuartic) {
    IASTMutable result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      if (ePoly.degree(0) >= Integer.MAX_VALUE) {
        return F.NIL;
      }
      if (ePoly.degree(0) >= 3) {
        result = unitPolynomial((int) ePoly.degree(0), ePoly);
        if (result.isNIL() && ePoly.degree(0) >= SHIFTED_BINOMIAL_DEGREE && varList.argSize() == 1) {
          result = shiftedBinomialRoots((int) ePoly.degree(0), ePoly, ring);
        }
        if (result.isPresent()) {
          result = QuarticSolver.sortASTArguments(result);
          return result;
        }
      }
      if (!rootsOfQuartic && ePoly.degree(0) > 2) {
        return F.NIL;
      }
      result = rootsOfQuarticPolynomial(ePoly, createSet);
      if (result.isPresent()) {
        if (expr.isNumericMode()) {
          for (int i = 1; i < result.size(); i++) {
            result.set(i, chopRoot(result.get(i)));
          }
        }
        result = QuarticSolver.sortASTArguments(result);
        return result;
      }
    } catch (JASConversionException e2) {
      // LOGGER.debug("RootsFunctions.rootsOfExprPolynomial() failed", e2);
    }
    return F.NIL;
  }

  /**
   * Solve a polynomial with degree &lt;= 2.
   *
   * @param expr
   * @param varList
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  private static IAST rootsOfQuadraticExprPolynomial(final IExpr expr, IAST varList) {
    IASTMutable result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = rootsOfQuadraticPolynomial(ePoly);
      if (result.isPresent() && expr.isNumericMode()) {
        for (int i = 1; i < result.size(); i++) {
          result.set(i, chopRoot(result.get(i)));
        }
      }
      result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (JASConversionException e2) {
      // LOGGER.debug("RootsFunctions.rootsOfQuadraticExprPolynomial() failed", e2);
    }
    return result;
  }

  /**
   * Solve a polynomial with degree &lt;= 4.
   *
   * @param polynomial the polynomial
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  private static IASTAppendable rootsOfQuarticPolynomial(ExprPolynomial polynomial,
      boolean createSet) {
    long varDegree = polynomial.degree(0);

    if (polynomial.isConstant()) {
      return F.ListAlloc(0);
    }

    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 4) {
      // solve quartic equation:
      IExpr[] coefficients = QuarticSolver.coefficients(polynomial);
      if (coefficients == null) {
        return F.NIL;
      }
      a = coefficients[4];
      b = coefficients[3];
      c = coefficients[2];
      d = coefficients[1];
      e = coefficients[0];
      IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e, createSet, true);
      if (result.isPresent()) {
        return (IASTAppendable) QuarticSolver.sortASTArguments(result);
      }
    }

    return F.NIL;
  }

  /**
   * The roots of a polynomial <code>a*(x+s)^n + r</code> of degree <code>n &gt;= 5</code> with
   * exact numbers <code>a, s, r</code>, in radicals - before a decomposition writes them as nested
   * radicals of its layers.
   *
   * @param polynomial the polynomial
   * @param variable the variable <code>x</code>
   * @return the roots or {@link F#NIL} if the polynomial isn't of this form
   */
  public static IAST shiftedBinomialRoots(IExpr polynomial, IExpr variable) {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, F.list(variable));
      ExprPolynomial ePoly = ring.create(polynomial, false, false, false);
      long degree = ePoly.degree(0);
      if (degree < SHIFTED_BINOMIAL_DEGREE || degree > Integer.MAX_VALUE / 2) {
        return F.NIL;
      }
      return shiftedBinomialRoots((int) degree, ePoly, ring);
    } catch (JASConversionException e) {
      return F.NIL;
    }
  }

  /**
   * The lowest degree of a binomial with a shifted variable which
   * {@link #shiftedBinomialRoots(int, ExprPolynomial, ExprPolynomialRing)} solves; the quartic
   * solvers take the lower degrees.
   */
  private static final int SHIFTED_BINOMIAL_DEGREE = 5;

  /**
   * Solve a polynomial <code>a*(x+s)^n + r</code> with exact numbers <code>a, s, r</code> - the
   * expanded form of a binomial in <code>x+s</code>, like <code>41300*(1+x/3)^13-7</code> - by the
   * roots of the binomial <code>a*y^n + r</code>, shifted by <code>-s</code>.
   *
   * @param n the degree of the polynomial
   * @param polynomial the polynomial in one variable
   * @param ring the ring of the polynomial
   * @return {@link F#NIL} if the polynomial isn't of this form
   */
  private static IASTAppendable shiftedBinomialRoots(int n, ExprPolynomial polynomial,
      ExprPolynomialRing ring) {
    final IExpr[] coefficients = new IExpr[n + 1];
    java.util.Arrays.fill(coefficients, F.C0);
    for (ExprMonomial monomial : polynomial) {
      IExpr coefficient = monomial.coefficient();
      if (!coefficient.isNumber() || coefficient.isInexactNumber()) {
        return F.NIL;
      }
      coefficients[(int) monomial.exponent().getVal(0)] = coefficient;
    }
    if (coefficients[n - 1].isZero()) {
      // no shift: a binomial is solved as such, a polynomial with more terms isn't of this form
      return F.NIL;
    }
    final EvalEngine engine = EvalEngine.get();
    final IExpr a = coefficients[n];
    // the coefficient of x^(n-1) is a*n*s
    final IExpr shift = engine.evaluate(F.Divide(coefficients[n - 1], F.Times(a, F.ZZ(n))));
    for (int k = n - 2; k >= 1; k--) {
      // the coefficient of x^k is a*Binomial(n,k)*s^(n-k)
      IExpr expected =
          engine.evaluate(F.Times(a, F.Binomial(F.ZZ(n), F.ZZ(k)), F.Power(shift, F.ZZ(n - k))));
      if (!expected.equals(coefficients[k])) {
        return F.NIL;
      }
    }
    final IExpr rest = engine.evaluate(F.Subtract(coefficients[0], F.Times(a, F.Power(shift, F.ZZ(n)))));
    if (rest.isZero()) {
      // the n-fold root -s
      return F.NIL;
    }
    final IExpr variable = ring.getVars().arg1();
    ExprPolynomial binomial = ring.create(F.Plus(F.Times(a, F.Power(variable, F.ZZ(n))), rest),
        false, false, false);
    IASTAppendable roots = unitPolynomial(n, binomial);
    if (roots.isNIL()) {
      return F.NIL;
    }
    for (int i = 1; i < roots.size(); i++) {
      roots.set(i, engine.evaluate(F.Subtract(roots.get(i), shift)));
    }
    return roots;
  }

  /**
   * Solve polynomials of the form <code>a * x^n + b * x^m == 0</code>, i.e. a binomial optionally
   * multiplied by a common <code>x^m</code> monomial factor.
   *
   * <p>
   * If the lowest-degree term has exponent <code>m &gt; 0</code>, the common factor
   * <code>x^m</code> is peeled off (contributing the root <code>0</code>) and the remaining
   * binomial <code>a * x^(n-m) + b</code> is solved in roots-of-unity form.
   *
   * @param varDegree the degree <code>n</code> of the leading term
   * @param polynomial
   * @return {@link F#NIL} if the polynomial is not of the required shape
   */
  private static IASTAppendable unitPolynomial(int varDegree, ExprPolynomial polynomial) {
    IExpr a = C0;
    IExpr b = C0;
    int lowExp = -1;
    for (ExprMonomial monomial : polynomial) {
      IExpr coeff = monomial.coefficient();
      long lExp = monomial.exponent().getVal(0);
      if (lExp == varDegree) {
        a = coeff;
      } else if (lowExp < 0) {
        b = coeff;
        lowExp = (int) lExp;
      } else {
        return F.NIL;
      }
    }
    if (a.isZero() || b.isZero()) {
      return F.NIL;
    }
    // a*x^varDegree + b*x^lowExp = x^lowExp * (a*x^reducedDegree + b): peel the common x^lowExp
    // monomial factor (which contributes the root 0) and solve the reduced binomial.
    final int reducedDegree = varDegree - lowExp;
    if (lowExp > 0 && reducedDegree < 3) {
      // leave linear/quadratic reductions to the existing (quartic) solvers
      return F.NIL;
    }

    boolean isNegative = false;
    final EvalEngine engine = EvalEngine.get();
    IExpr rhsNumerator = engine.evaluate(b.negate());
    IExpr rhsDenominator = a;
    if (a.isNumber() && b.isNumber()) {
      // one number -b/a: the roots of -2*I-2*I*t^6 are (-1)^(1/6) times roots of unity, not
      // (I*2)^(1/6)/(-I*2)^(1/6)
      rhsNumerator = engine.evaluate(F.Divide(rhsNumerator, a));
      rhsDenominator = F.C1;
    }
    IASTAppendable result;
    if ((reducedDegree & 0x0001) == 0x0001) {
      // odd
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IAST numerator = (IAST) rhsNumerator;
        if (numerator.first().isNegative()) {
          isNegative = true;
          numerator = numerator.setAtCopy(1, numerator.first().negate());
        }
        zNumerator = engine.evaluate(numerator.map(x -> powerOrExprMapper(x, 1, reducedDegree)));
      } else {
        if (rhsNumerator.isNegative()) {
          isNegative = true;
          rhsNumerator = rhsNumerator.negate();
        }
        zNumerator = engine.evaluate(powerOrExprMapper(rhsNumerator, 1, reducedDegree));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        if (rhsDenominator.first().isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = ((IAST) rhsDenominator).setAtCopy(1, rhsDenominator.first().negate());
        }
        zDenominator = engine
            .evaluate(((IAST) rhsDenominator).map(x -> powerOrExprMapper(x, -1, reducedDegree)));
      } else {
        if (rhsDenominator.isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = rhsDenominator.negate();
        }
        zDenominator = engine.evaluate(powerOrExprMapper(rhsDenominator, -1, reducedDegree));
      }
      final int increment = isNegative ? 1 : 0;
      result = F.mapRange(0, reducedDegree, i -> //
      F.Times(F.Power(F.CN1, i + increment), F.Power(-1, F.QQ(i, reducedDegree)), zNumerator,
          zDenominator));
    } else {
      // even
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IExpr temp = ((IAST) rhsNumerator).map(x -> powerOrExprMapper(x, 1, reducedDegree));
        zNumerator = engine.evaluate(temp);
      } else {
        IExpr temp = powerOrExprMapper(rhsNumerator, 1, reducedDegree);
        zNumerator = engine.evaluate(temp);
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        IExpr temp = ((IAST) rhsDenominator).map(x -> powerOrExprMapper(x, -1, reducedDegree));
        zDenominator = engine.evaluate(temp);
      } else {
        IExpr temp = powerOrExprMapper(rhsDenominator, -1, reducedDegree);
        zDenominator = engine.evaluate(temp);
      }

      result = F.ListAlloc(reducedDegree);
      long size = reducedDegree / 2;
      int k = 0; // isNegative?1:0;
      for (int i = 1; i <= size; i++) {
        IExpr power = engine.evaluate(F.Power(-1, F.QQ(k, reducedDegree)));
        IAST times1 = F.Times(F.CN1, power, zNumerator, zDenominator);
        IAST times2 = F.Times(power, zNumerator, zDenominator);
        result.append(engine.evaluate(times1));
        result.append(engine.evaluate(times2));
        k += 2;
      }
    }
    if (lowExp > 0) {
      // the peeled x^lowExp factor contributes the root 0
      result.append(C0);
    }
    return result;
  }

  /**
   * Calculate <code>x ^ (numerator/denominator)</code>. If <code>x</code> is a {@link S#Power}
   * expression with rational exponent, try to merge the exponents into one rational number
   * 
   * @param x
   * @param numerator
   * @param denominator
   * @return
   */
  private static IExpr powerOrExprMapper(IExpr x, int numerator, int denominator) {
    if (x.isPower() && x.exponent().isRational()) {
      IAST power = (IAST) x;
      return F.Power(power.base(),
          ((IRational) power.exponent()).multiply(F.QQ(numerator, denominator)));
    }
    return F.Power(x, F.QQ(numerator, denominator));
  }

  /**
   * Solve a polynomial with degree &lt;= 2.
   *
   * @param polynomial the polynomial
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  private static IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial) {
    long varDegree = polynomial.degree(0);

    if (polynomial.isConstant()) {
      return F.ListAlloc(1);
    }
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 2) {
      IEvalStepListener listener = EvalEngine.get().getStepListener();
      if (listener != null) {
        IASTAppendable temp = listener.rootsOfQuadraticPolynomial(polynomial);
        if (temp.isPresent()) {
          return temp;
        }
      }
      // solve quadratic equation:
      IExpr[] coefficients = QuarticSolver.coefficients(polynomial);
      if (coefficients == null) {
        throw new ArithmeticException("Roots::Unexpected exponent value");
      }
      a = coefficients[4];
      b = coefficients[3];
      c = coefficients[2];
      d = coefficients[1];
      e = coefficients[0];
      IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
      if (result.isPresent()) {
        result = (IASTAppendable) QuarticSolver.sortASTArguments(result);
        return result;
      }
    }

    return F.NIL;
  }

  /**
   * @param expr
   * @param denominator
   * @param variables
   * @param numericSolutions
   * @param engine
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator,
      final IAST variables, boolean numericSolutions, EvalEngine engine) {
    return rootsOfVariable(expr, denominator, variables, numericSolutions, true, engine);
  }

  /**
   * @param allowRootObjects if <code>false</code>, a polynomial which cannot be solved in radicals
   *        is approximated numerically instead of being represented by inert {@link S#Root}
   *        objects. {@link S#NSolve} passes <code>false</code>: it is asked for numbers, and
   *        evaluating one <code>Root</code> object per root would run the numerical root finder
   *        once per root instead of once for the whole polynomial.
   */
  public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator,
      final IAST variables, boolean numericSolutions, boolean allowRootObjects, EvalEngine engine) {
    return rootsOfVariable(expr, denominator, variables, numericSolutions, true, true,
        allowRootObjects, engine);
  }

  /**
   * @param expr
   * @param denominator
   * @param variables
   * @param numericSolutions
   * @param engine
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator,
      final IAST variables, boolean numericSolutions, boolean createSet, boolean sort,
      EvalEngine engine) {
    return rootsOfVariable(expr, denominator, variables, numericSolutions, createSet, sort, true,
        engine);
  }

  /**
   * @param allowRootObjects if <code>false</code>, a polynomial which cannot be solved in radicals
   *        is approximated numerically instead of being represented by inert {@link S#Root} objects
   * @see #rootsOfVariable(IExpr, IExpr, IAST, boolean, boolean, EvalEngine)
   */
  public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator,
      final IAST variables, boolean numericSolutions, boolean createSet, boolean sort,
      boolean allowRootObjects, EvalEngine engine) {
    return rootsOfVariable(expr, denominator, variables, numericSolutions, createSet, sort,
        allowRootObjects, true, true, engine);
  }

  /**
   * @param cubicsInRadicals the value of the {@link S#Cubics} option: if <code>false</code>, a
   *        general cubic is represented by inert {@link S#Root} objects instead of the explicit
   *        radicals of the Cardano formula
   * @param quarticsInRadicals the value of the {@link S#Quartics} option, likewise for the Ferrari
   *        formula
   * @see #rootsOfVariable(IExpr, IExpr, IAST, boolean, boolean, boolean, boolean, EvalEngine)
   */
  public static IAST rootsOfVariable(final IExpr expr, final IExpr denominator,
      final IAST variables, boolean numericSolutions, boolean createSet, boolean sort,
      boolean allowRootObjects, boolean cubicsInRadicals, boolean quarticsInRadicals,
      EvalEngine engine) {
    IASTMutable result = F.NIL;
    // List<IExpr> varList = variables.copyTo();
    try {
      IAST list = rootsOfQuadraticExprPolynomial(expr, variables);
      if (list.isPresent()) {
        return list;
      }

      JASConvert<BigRational> jas = new JASConvert<BigRational>(variables, BigRational.ZERO);
      GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numericSolutions);
      if (polyRat == null) {
        result = rootsOfExprPolynomial(expr, variables, createSet, true);
        if (result.isNIL() && numericSolutions && variables.argSize() == 1
            && variables.arg1().isSymbol()) {
          // complex coefficients of a degree above four: the roots numerically
          result = complexCoefficientRoots(expr, (ISymbol) variables.arg1());
        }
        if (result.isPresent()) {
          return rootsOfVariableEndProcessing(result, variables, denominator, createSet, sort,
              engine);
        }
        return F.NIL;
      }
      // if (polyRat.degree(0) <= 2) {
      result = rootsOfExprPolynomial(expr, variables, createSet, false);
      if (result.isPresent()) {
        return result;
      }
      // }
      IASTAppendable newResult = F.ListAlloc(8);
      IAST factorRational = AlgebraUtil.factorRational(polyRat, jas, S.List);
      if (factorRational.isNIL()) {
        factorRational = F.Times(expr);
      }
      for (int i = 1; i < factorRational.size(); i++) {
        IExpr factor = factorRational.get(i);
        IExpr temp = F.evalExpand(factor);
        IAST inertRoots = generalRootObjects(temp, variables.arg1(), cubicsInRadicals,
            quarticsInRadicals, numericSolutions, allowRootObjects, engine);
        if (inertRoots.isPresent()) {
          newResult.appendArgs(inertRoots);
          continue;
        }
        IAST quarticResultList = QuarticSolver.solve(temp, variables.arg1(), false, true);
        if (quarticResultList.isPresent()) {
          appendRoots(newResult, quarticResultList, numericSolutions, engine);
        } else {
          polyRat = jas.expr2JAS(temp, numericSolutions);
          if (polyRat == null) {
            result = rootsOfExprPolynomial(expr, variables, true, true);
            if (result.isPresent()) {
              return rootsOfVariableEndProcessing(result, variables, denominator, createSet, sort,
                  engine);
            }
            return F.NIL;
          }
          IAST factorComplex = AlgebraUtil.factorRational(polyRat, jas, S.List);
          if (factorComplex.isNIL()) {
            factorComplex = F.Times(expr);
          }
          for (int k = 1; k < factorComplex.size(); k++) {
            temp = F.evalExpand(factorComplex.get(k));
            quarticResultList = QuarticSolver.solve(temp, variables.arg1());
            if (quarticResultList.isPresent()) {
              appendRoots(newResult, quarticResultList, numericSolutions, engine);
            } else {
              IAST rootObjects = allowRootObjects //
                  ? rootObjects(temp, variables.arg1(), numericSolutions, engine)
                  : F.NIL;
              if (rootObjects.isPresent()) {
                newResult.appendArgs(rootObjects);
              } else {
                IAST resultList = findRoots(temp, variables);
                if (resultList.isNIL() || (resultList.argSize() == 0
                    && !engine.evaluate(F.Exponent(temp, variables.arg1())).isZero())) {
                  // a factor whose roots weren't found: leaving it out would give too few roots,
                  // and none at all claims that the polynomial has no root
                  return F.NIL;
                }
                newResult.appendArgs(resultList);
              }
            }
          }
        }
      }
      if (createSet) {
        return QuarticSolver.createSet(newResult);
      }
      return QuarticSolver.evalAndSort(newResult, sort);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // JAS or "findRoots" may throw RuntimeExceptions
      result = rootsOfExprPolynomial(expr, variables, true, true);
    }
    if (result.isPresent()) {
      return rootsOfVariableEndProcessing(result, variables, denominator, createSet, sort, engine);
    }
    return F.NIL;
  }

  private static IAST rootsOfVariableEndProcessing(IASTMutable result, final IAST variables,
      final IExpr denominator, boolean createSet, boolean sort, EvalEngine engine) {
    if (!denominator.isNumber()) {
      // eliminate roots from the result list, which occur in the denominator
      int i = 1;
      IASTAppendable appendable = F.NIL;
      while (i < result.size()) {
        IExpr temp = denominator.replaceAll(F.Rule(variables.arg1(), result.get(i)));
        if (temp.isPresent() && engine.evaluate(temp).isZero()) {
          if (appendable.isNIL()) {
            appendable = result.removeAtClone(i);
            continue;
          }
          appendable.remove(i);
          continue;
        }
        i++;
      }
    }
    IASTAppendable newResult = result.copyAppendable();
    if (createSet) {
      return QuarticSolver.createSet(newResult);
    }
    return QuarticSolver.evalAndSort(newResult, sort);
  }

  /**
   * <p>
   * Implements the <a href="http://mathworld.wolfram.com/LaguerresMethod.html"> Laguerre's
   * Method</a> for root finding of real coefficient polynomials.
   * <p>
   * Laguerre's method is global in the sense that it can start with any initial approximation and
   * be able to solve all roots from that point. The algorithm requires a bracketing condition.
   * 
   * @param coefficients Polynomial coefficients.
   * @return the points at which the function value is zero or <code>null</code> if the solver
   *         couldn't find a solution.
   */
  public static org.hipparchus.complex.Complex[] allComplexRootsLaguerre(
      @Nonnull double[] coefficients) {

    if (coefficients.length > Config.MAX_POLYNOMIAL_DEGREE_LAGUERRE_SOLVER) {
      // PolynomialDegreeLimitExceeded.throwIt(coefficients.length);
      return null;
    }

    for (int j = 0; j < coefficients.length; j++) {
      if (!Double.isFinite(coefficients[j])) {
        return null;
      }
    }
    try {
      // the solver's tests are absolute: the variable is scaled so that the roots are of the size
      // 1 on average, and the coefficients so that the largest is of the size 1. Both factors are
      // powers of 2, which scale without rounding.
      int scaleExponent = rootScaleExponent(coefficients);
      double[] scaled = scaledCoefficients(coefficients, scaleExponent);
      if (scaled == null) {
        // the scaling over- or underflows: solve the polynomial as it is
        scaleExponent = 0;
        scaled = coefficients;
      }
      LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
      // see https://github.com/Hipparchus-Math/hipparchus/issues/177 for initial value
      // https://stackoverflow.com/q/65960318
      org.hipparchus.complex.Complex[] roots = solver.solveAllComplex(scaled, 10_000, 1.0);
      if (roots == null) {
        return null;
      }
      if (scaleExponent != 0) {
        for (int i = 0; i < roots.length; i++) {
          roots[i] = new org.hipparchus.complex.Complex(
              Math.scalb(roots[i].getReal(), scaleExponent),
              Math.scalb(roots[i].getImaginary(), scaleExponent));
        }
      }
      polishRoots(coefficients, roots);
      symmetricConjugates(roots);
      return roots;
    } catch (MathRuntimeException mre) {
      // mre.printStackTrace();
      // org.hipparchus.exception.MathIllegalStateException: maximal count (100,000) exceeded
    }
    return null;
  }

  /**
   * The coefficients of the polynomial in <code>y</code> with <code>x == 2^scaleExponent*y</code>,
   * divided by the power of 2 which makes the largest one of the size 1.
   *
   * @return <code>null</code> if a coefficient over- or underflows
   */
  private static double[] scaledCoefficients(double[] coefficients, int scaleExponent) {
    final double[] scaled = new double[coefficients.length];
    int maxExponent = Integer.MIN_VALUE;
    for (int k = 0; k < coefficients.length; k++) {
      scaled[k] = Math.scalb(coefficients[k], k * scaleExponent);
      if (scaled[k] != 0.0) {
        maxExponent = Math.max(maxExponent, Math.getExponent(scaled[k]));
      }
    }
    for (int k = 0; k < scaled.length; k++) {
      if (maxExponent != Integer.MIN_VALUE) {
        scaled[k] = Math.scalb(scaled[k], -maxExponent);
      }
      if (!Double.isFinite(scaled[k]) || (scaled[k] == 0.0) != (coefficients[k] == 0.0)
          || (scaled[k] != 0.0 && Math.getExponent(scaled[k]) < Double.MIN_EXPONENT)) {
        return null;
      }
    }
    return scaled;
  }

  /**
   * The exponent <code>e</code> of the power of 2 nearest to the geometric mean of the moduli of
   * the roots: the polynomial in <code>y</code> with <code>x == 2^e*y</code> has roots of the size 1
   * on average. The roots at <code>0</code> do not count.
   *
   * @param coefficients the coefficients, the constant one first
   * @return <code>0</code> if the polynomial has no nonzero root
   */
  private static int rootScaleExponent(double[] coefficients) {
    int high = coefficients.length - 1;
    while (high >= 0 && coefficients[high] == 0.0) {
      high--;
    }
    int low = 0;
    while (low < high && coefficients[low] == 0.0) {
      low++;
    }
    if (high <= low) {
      return 0;
    }
    // the product of the nonzero roots is the quotient of the lowest and the highest coefficient
    double log2 = (Math.log(Math.abs(coefficients[low])) - Math.log(Math.abs(coefficients[high])))
        / Math.log(2.0) / (high - low);
    if (!Double.isFinite(log2)) {
      return 0;
    }
    return (int) Math.round(log2);
  }

  /** The most Newton steps which <code>polishRoots</code> takes for a root. */
  private static final int MAX_POLISH_STEPS = 8;

  /**
   * The roots of a polynomial as the eigenvalues of its companion matrix.
   *
   * @param coefficients the coefficients, the constant one first; the leading one isn't 0
   * @return <code>null</code> if the decomposition fails
   */
  private static org.hipparchus.complex.Complex[] companionMatrixRoots(double[] coefficients) {
    int n = coefficients.length - 1;
    if (n < 1 || coefficients[n] == 0.0) {
      return null;
    }
    double leading = coefficients[n];
    double[][] companion = new double[n][n];
    for (int i = 0; i < n; i++) {
      companion[0][i] = -coefficients[n - 1 - i] / leading;
      if (i > 0) {
        companion[i][i - 1] = 1.0;
      }
    }
    try {
      org.hipparchus.complex.Complex[] eigenvalues =
          new org.hipparchus.linear.EigenDecompositionNonSymmetric(
              new org.hipparchus.linear.Array2DRowRealMatrix(companion, false)).getEigenvalues();
      if (eigenvalues == null || eigenvalues.length != n) {
        return null;
      }
      for (org.hipparchus.complex.Complex z : eigenvalues) {
        if (z.isNaN() || z.isInfinite()) {
          return null;
        }
      }
      return eigenvalues;
    } catch (MathRuntimeException mre) {
      return null;
    }
  }

  private static boolean isFinite(double[] coefficients) {
    for (double c : coefficients) {
      if (!Double.isFinite(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Improve the roots by Newton steps with the polynomial itself. The roots of the solver are
   * found one by one in the quotients of the polynomial by the roots found before, and its tests
   * for convergence are absolute: a small root like <code>10^-12</code> of <code>x-10^-12</code>
   * came out with only the absolute accuracy of the starting value <code>1</code>. A step is taken
   * only while it decreases the value of the polynomial and stays closer to its root than to any
   * other, so that it doesn't jump to a neighbouring root of a cluster.
   *
   * @param coefficients the coefficients, the constant one first
   * @param roots the roots, which are replaced by the improved ones
   */
  private static void polishRoots(double[] coefficients, org.hipparchus.complex.Complex[] roots) {
    final int n = coefficients.length - 1;
    if (n < 1) {
      return;
    }
    for (int i = 0; i < roots.length; i++) {
      double nearest = Double.POSITIVE_INFINITY;
      for (int j = 0; j < roots.length; j++) {
        if (j != i) {
          nearest = Math.min(nearest, roots[i].subtract(roots[j]).norm());
        }
      }
      org.hipparchus.complex.Complex z = roots[i];
      org.hipparchus.complex.Complex[] values = hornerWithDerivative(coefficients, z);
      double residual = values[0].norm();
      for (int step = 0; step < MAX_POLISH_STEPS && residual > 0.0; step++) {
        org.hipparchus.complex.Complex dz = values[0].divide(values[1]);
        double size = dz.norm();
        if (!Double.isFinite(size) || size == 0.0 || size >= 0.5 * nearest) {
          break;
        }
        org.hipparchus.complex.Complex next = z.subtract(dz);
        org.hipparchus.complex.Complex[] nextValues = hornerWithDerivative(coefficients, next);
        double nextResidual = nextValues[0].norm();
        if (!(nextResidual < residual)) {
          break;
        }
        z = next;
        values = nextValues;
        residual = nextResidual;
      }
      roots[i] = z;
    }
  }

  /**
   * The roots of {@link #allComplexRootsLaguerre(double[])} as the solutions of a polynomial
   * equation: the approximations of a multiple root are replaced by the root, each repeated with
   * its multiplicity. <code>Root</code> objects number the approximations and keep them apart.
   *
   * @return <code>null</code> if the solver couldn't find the roots
   */
  private static org.hipparchus.complex.Complex[] multipleRootsMerged(double[] coefficients) {
    return multipleRootsMerged(coefficients, false);
  }

  /**
   * @param withEigenvalues if Laguerre's method fails, the roots of a polynomial with machine
   *        number coefficients are the eigenvalues of its companion matrix
   */
  private static org.hipparchus.complex.Complex[] multipleRootsMerged(double[] coefficients,
      boolean withEigenvalues) {
    org.hipparchus.complex.Complex[] roots = allComplexRootsLaguerre(coefficients);
    if (roots == null && withEigenvalues
        && coefficients.length <= Config.MAX_POLYNOMIAL_DEGREE_LAGUERRE_SOLVER
        && isFinite(coefficients)) {
      // Laguerre's method can stall next to a root of high multiplicity, like the 8-fold root
      // of (x-0.4)^8: the eigenvalues of the companion matrix are the roots as well
      roots = companionMatrixRoots(coefficients);
      if (roots != null) {
        polishRoots(coefficients, roots);
      }
    }
    if (roots != null) {
      mergeMultipleRoots(coefficients, roots);
      symmetricConjugates(roots);
    }
    return roots;
  }

  /**
   * The relative distance within which roots are first gathered into a candidate cluster of a
   * multiple root; a cluster which isn't one is split again with a tenth of the distance.
   */
  private static final double CLUSTER_DISTANCE = 0.05;

  /** The smallest relative distance a candidate cluster is split with. */
  private static final double MIN_CLUSTER_DISTANCE = 1.0e-7;

  /**
   * How much wider than the spread rounding errors cause a cluster of roots may be and still be
   * taken as one multiple root.
   */
  private static final double CLUSTER_SPREAD_FACTOR = 20.0;

  /**
   * Replace the approximations of a multiple root by the root itself. Rounding errors of the size
   * <code>eps</code> in the coefficients split an <code>m</code>-fold root <code>c</code> into
   * <code>m</code> roots on a circle of the radius <code>(eps*S/|p^(m)(c)/m!|)^(1/m)</code> around
   * it, where <code>S</code> is the sum of the moduli of the terms of the polynomial at
   * <code>c</code>: about <code>10^-8</code> for a double root, <code>10^-2</code> for a ninefold
   * one. Neighbouring roots are gathered into candidate clusters, and a cluster whose spread is of
   * that size is one multiple root; it is a simple root of the <code>(m-1)</code>-th derivative,
   * which a few Newton steps from the centroid of the cluster find to full accuracy. A cluster
   * which is wider, like the distinct roots <code>1</code> and <code>1.001</code>, is split again.
   *
   * @param coefficients the coefficients, the constant one first
   * @param roots the roots, of which the approximations of a multiple root are replaced
   */
  private static void mergeMultipleRoots(double[] coefficients,
      org.hipparchus.complex.Complex[] roots) {
    if (roots.length < 2 || coefficients.length != roots.length + 1) {
      return;
    }
    java.util.List<Integer> all = new java.util.ArrayList<>(roots.length);
    for (int i = 0; i < roots.length; i++) {
      all.add(i);
    }
    mergeClusters(coefficients, roots, all, CLUSTER_DISTANCE);
  }

  /**
   * Gather the roots with the indices <code>members</code> into clusters of roots closer than
   * <code>distance</code> relative to their size, and replace each cluster which is a multiple
   * root by that root.
   */
  private static void mergeClusters(double[] coefficients, org.hipparchus.complex.Complex[] roots,
      java.util.List<Integer> members, double distance) {
    final int n = members.size();
    // single-linkage clusters: the component of each member
    int[] component = new int[n];
    java.util.Arrays.fill(component, -1);
    int components = 0;
    for (int i = 0; i < n; i++) {
      if (component[i] >= 0) {
        continue;
      }
      component[i] = components;
      java.util.ArrayDeque<Integer> queue = new java.util.ArrayDeque<>();
      queue.add(i);
      while (!queue.isEmpty()) {
        int a = queue.poll();
        org.hipparchus.complex.Complex ra = roots[members.get(a)];
        for (int b = 0; b < n; b++) {
          if (component[b] < 0) {
            org.hipparchus.complex.Complex rb = roots[members.get(b)];
            double scale = Math.max(ra.norm(), rb.norm());
            if (ra.subtract(rb).norm() <= distance * scale) {
              component[b] = components;
              queue.add(b);
            }
          }
        }
      }
      components++;
    }
    for (int k = 0; k < components; k++) {
      java.util.List<Integer> cluster = new java.util.ArrayList<>();
      for (int i = 0; i < n; i++) {
        if (component[i] == k) {
          cluster.add(members.get(i));
        }
      }
      if (cluster.size() < 2) {
        continue;
      }
      if (!mergeCluster(coefficients, roots, cluster) && distance > MIN_CLUSTER_DISTANCE) {
        mergeClusters(coefficients, roots, cluster, distance / 10.0);
      }
    }
  }

  /**
   * Replace the roots with the indices <code>cluster</code> by one multiple root, if their spread
   * is the one rounding errors cause.
   *
   * @return <code>true</code> if the cluster was a multiple root
   */
  private static boolean mergeCluster(double[] coefficients, org.hipparchus.complex.Complex[] roots,
      java.util.List<Integer> cluster) {
    final int m = cluster.size();
    double re = 0.0;
    double im = 0.0;
    for (int i : cluster) {
      re += roots[i].getReal();
      im += roots[i].getImaginary();
    }
    org.hipparchus.complex.Complex centroid = new org.hipparchus.complex.Complex(re / m, im / m);
    double radius = 0.0;
    for (int i : cluster) {
      radius = Math.max(radius, roots[i].subtract(centroid).norm());
    }
    if (Math.abs(centroid.getImaginary()) <= radius) {
      // a cluster around a real multiple root of a polynomial with real coefficients
      centroid = new org.hipparchus.complex.Complex(centroid.getReal());
    }
    // the Taylor coefficients p^(j)(c)/j! at the centroid
    final int degree = coefficients.length - 1;
    org.hipparchus.complex.Complex[] taylor = new org.hipparchus.complex.Complex[degree + 1];
    for (int k = 0; k <= degree; k++) {
      taylor[k] = new org.hipparchus.complex.Complex(coefficients[k]);
    }
    for (int j = 0; j <= m; j++) {
      // synthetic division by (x - c): taylor[j] becomes p^(j)(c)/j!
      for (int k = degree - 1; k >= j; k--) {
        taylor[k] = taylor[k].add(taylor[k + 1].multiply(centroid));
      }
    }
    double size = 0.0;
    double modulus = centroid.norm();
    for (int k = degree; k >= 0; k--) {
      size = size * modulus + Math.abs(coefficients[k]);
    }
    double leading = taylor[m].norm();
    if (!(leading > 0.0) || !Double.isFinite(size)) {
      return false;
    }
    double noise = Math.pow(4.0 * degree * Math.ulp(1.0) * size / leading, 1.0 / m);
    if (!(radius <= CLUSTER_SPREAD_FACTOR * noise)) {
      return false;
    }
    // a simple root of the (m-1)-th derivative
    double[] derivative = coefficients;
    for (int j = 1; j < m; j++) {
      double[] next = new double[derivative.length - 1];
      for (int k = 1; k < derivative.length; k++) {
        next[k - 1] = k * derivative[k];
      }
      derivative = next;
    }
    org.hipparchus.complex.Complex z = centroid;
    org.hipparchus.complex.Complex[] values = hornerWithDerivative(derivative, z);
    double residual = values[0].norm();
    for (int step = 0; step < 2 * MAX_POLISH_STEPS && residual > 0.0; step++) {
      org.hipparchus.complex.Complex dz = values[0].divide(values[1]);
      if (!Double.isFinite(dz.norm())) {
        break;
      }
      org.hipparchus.complex.Complex next = z.subtract(dz);
      if (centroid.getImaginary() == 0.0) {
        next = new org.hipparchus.complex.Complex(next.getReal());
      }
      org.hipparchus.complex.Complex[] nextValues = hornerWithDerivative(derivative, next);
      double nextResidual = nextValues[0].norm();
      if (!(nextResidual < residual)) {
        break;
      }
      z = next;
      values = nextValues;
      residual = nextResidual;
    }
    if (!(z.subtract(centroid).norm() <= 2.0 * radius + Math.ulp(modulus))) {
      z = centroid;
    }
    for (int i : cluster) {
      roots[i] = z;
    }
    return true;
  }

  /** The relative distance of two roots which are taken as a pair of conjugates. */
  private static final double CONJUGATE_TOLERANCE = 1.0e-8;

  /**
   * The non-real roots of a polynomial with real coefficients are pairs of conjugates: the two
   * approximations of a pair, which were found and improved one by one, get the mean of their
   * real parts and of the moduli of their imaginary parts.
   *
   * @param roots the roots, which are replaced by the symmetric ones
   */
  private static void symmetricConjugates(org.hipparchus.complex.Complex[] roots) {
    final boolean[] paired = new boolean[roots.length];
    for (int i = 0; i < roots.length; i++) {
      if (paired[i] || !(roots[i].getImaginary() > 0.0)) {
        continue;
      }
      final org.hipparchus.complex.Complex conjugate = roots[i].conjugate();
      int partner = -1;
      double distance = CONJUGATE_TOLERANCE * roots[i].norm();
      for (int j = 0; j < roots.length; j++) {
        if (j != i && !paired[j] && roots[j].getImaginary() < 0.0) {
          double d = roots[j].subtract(conjugate).norm();
          if (d <= distance) {
            distance = d;
            partner = j;
          }
        }
      }
      if (partner >= 0) {
        final double re = 0.5 * (roots[i].getReal() + roots[partner].getReal());
        final double im = 0.5 * (roots[i].getImaginary() - roots[partner].getImaginary());
        roots[i] = new org.hipparchus.complex.Complex(re, im);
        roots[partner] = new org.hipparchus.complex.Complex(re, -im);
        paired[i] = true;
        paired[partner] = true;
      }
    }
  }

  /**
   * The value and the derivative of the polynomial at <code>z</code>.
   *
   * @param coefficients the coefficients, the constant one first
   */
  private static org.hipparchus.complex.Complex[] hornerWithDerivative(double[] coefficients,
      org.hipparchus.complex.Complex z) {
    final int n = coefficients.length - 1;
    org.hipparchus.complex.Complex value = new org.hipparchus.complex.Complex(coefficients[n]);
    org.hipparchus.complex.Complex derivative = org.hipparchus.complex.Complex.ZERO;
    for (int k = n - 1; k >= 0; k--) {
      derivative = derivative.multiply(z).add(value);
      value = value.multiply(z).add(coefficients[k]);
    }
    return new org.hipparchus.complex.Complex[] {value, derivative};
  }

  /**
   * The roots of <code>c0 + c1*x + c2*x^2</code> with <code>c0 != 0</code> and <code>c2 != 0</code>.
   * A discriminant within the rounding error of <code>c1^2</code> and <code>4*c0*c2</code> is a
   * double root; an iteration would split it by about the square root of the machine epsilon. The
   * root of the larger size is taken without cancellation, the other one as the quotient by it.
   */
  private static org.hipparchus.complex.Complex[] quadraticRoots(double c0, double c1, double c2) {
    final double scale = c1 * c1 + Math.abs(4.0 * c0 * c2);
    double discriminant = c1 * c1 - 4.0 * c0 * c2;
    if (QuarticSolver.isRoundingNoise(discriminant, scale)) {
      double root = -c1 / (2.0 * c2);
      return new org.hipparchus.complex.Complex[] {new org.hipparchus.complex.Complex(root),
          new org.hipparchus.complex.Complex(root)};
    }
    if (discriminant > 0.0) {
      double q = -0.5 * (c1 + Math.copySign(Math.sqrt(discriminant), c1));
      return new org.hipparchus.complex.Complex[] {new org.hipparchus.complex.Complex(q / c2),
          new org.hipparchus.complex.Complex(c0 / q)};
    }
    double re = -c1 / (2.0 * c2);
    double im = Math.sqrt(-discriminant) / (2.0 * Math.abs(c2));
    return new org.hipparchus.complex.Complex[] {new org.hipparchus.complex.Complex(re, -im),
        new org.hipparchus.complex.Complex(re, im)};
  }

  /**
   * Determine all complex roots of the univariate polynomial <code>polynomial</code> numerically.
   *
   * <p>
   * In contrast to {@link #roots(IExpr, boolean, IAST, EvalEngine)} the roots are <b>not</b>
   * collected in a set: a root of multiplicity <code>k</code> is returned <code>k</code> times, as
   * <code>NSolve</code> and <code>Solve</code> do. The roots are ordered by their real part and,
   * for equal real parts, by their imaginary part.
   *
   * <p>
   * A root at the origin is split off exactly instead of being approximated: for
   * <code>x^3-4*x^2</code> Laguerre's method returns <code>1.27*10^-6</code> and
   * <code>3.999999999999596</code> for the double root at <code>0</code> and the simple root at
   * <code>4</code>, while dividing out <code>x^2</code> first leaves the exactly solvable linear
   * factor <code>x-4</code>.
   *
   * @param polynomial a univariate polynomial with numeric coefficients
   * @param variable the polynomial's variable
   * @param engine the evaluation engine
   * @return the roots as machine precision numbers or {@link F#NIL} if <code>polynomial</code>
   *         isn't a univariate polynomial with numeric coefficients or the solver failed
   */
  public static IAST allNumericRoots(IExpr polynomial, ISymbol variable, EvalEngine engine) {
    double[] coefficients;
    IExpr expanded = evalExpandAll(polynomial, engine);
    try {
      coefficients = coefficients(expanded, variable);
    } catch (JASConversionException jce) {
      return F.NIL;
    }
    if (coefficients == null) {
      // complex coefficients
      return complexCoefficientRoots(expanded, variable);
    }
    if (coefficients.length < 2) {
      return F.NIL;
    }
    for (int i = 0; i < coefficients.length; i++) {
      if (!Double.isFinite(coefficients[i])) {
        return F.NIL;
      }
    }
    // the degree of the polynomial: trailing zero coefficients don't contribute
    int degree = coefficients.length - 1;
    while (degree >= 0 && coefficients[degree] == 0.0) {
      degree--;
    }
    if (degree < 1) {
      return F.NIL;
    }
    // x^zeroMultiplicity divides the polynomial: 0 is a root of that multiplicity
    int zeroMultiplicity = 0;
    while (coefficients[zeroMultiplicity] == 0.0) {
      zeroMultiplicity++;
    }
    org.hipparchus.complex.Complex[] roots;
    if (degree - zeroMultiplicity > 0) {
      double[] deflated = new double[degree - zeroMultiplicity + 1];
      System.arraycopy(coefficients, zeroMultiplicity, deflated, 0, deflated.length);
      roots = deflated.length == 3 ? quadraticRoots(deflated[0], deflated[1], deflated[2])
          : multipleRootsMerged(deflated);
      if (roots == null) {
        return F.NIL;
      }
    } else {
      roots = new org.hipparchus.complex.Complex[0];
    }
    org.hipparchus.complex.Complex[] allRoots =
        new org.hipparchus.complex.Complex[zeroMultiplicity + roots.length];
    for (int i = 0; i < zeroMultiplicity; i++) {
      allRoots[i] = org.hipparchus.complex.Complex.ZERO;
    }
    for (int i = 0; i < roots.length; i++) {
      allRoots[zeroMultiplicity + i] = chopRoot(roots[i]);
    }
    java.util.Arrays.sort(allRoots, RootsFunctions::compareRoots);
    return F.mapRange(0, allRoots.length, i -> {
      org.hipparchus.complex.Complex root = allRoots[i];
      return root.getImaginary() == 0.0 //
          ? F.num(root.getReal()) //
          : F.complexNum(root.getReal(), root.getImaginary());
    });
  }

  /**
   * Append the <code>roots</code> to <code>result</code>, numerically evaluated and chopped if
   * <code>numericSolutions</code> is set.
   */
  private static void appendRoots(IASTAppendable result, IAST roots, boolean numericSolutions,
      EvalEngine engine) {
    for (int j = 1; j < roots.size(); j++) {
      if (numericSolutions) {
        result.append(chopRoot(engine.evalN(roots.get(j))));
      } else {
        result.append(roots.get(j));
      }
    }
  }

  /**
   * A numerically determined root with a part which is below
   * {@link Config#DEFAULT_ROOTS_CHOP_DELTA} replaced by <code>0</code>: the imaginary part which a
   * formula leaves on a real root. For a root smaller than 1 the delta is relative to it:
   * <code>1.0*^-10</code> is a root and not what is left of <code>0</code>.
   */
  private static IExpr chopRoot(IExpr root) {
    if (!root.isInexactNumber()) {
      return root;
    }
    final INumber number = (INumber) root;
    final double size = Math.hypot(number.reDoubleValue(), number.imDoubleValue());
    if (!Double.isFinite(size)) {
      return root;
    }
    return F.chopNumber(number, Config.DEFAULT_ROOTS_CHOP_DELTA * Math.min(1.0, size));
  }

  /**
   * Set the real or the imaginary part of a numerically determined root to <code>0</code> if it is
   * negligible against the root's modulus, so that a root which is real or purely imaginary is
   * recognized as such. The rounding residue of a real root of <code>x^4-1</code> for example is
   * <code>-8.6*10^-18</code> against a modulus of <code>1</code>.
   *
   * @param root a numerically determined root
   * @return the root with its negligible parts replaced by <code>0</code>
   */
  private static org.hipparchus.complex.Complex chopRoot(org.hipparchus.complex.Complex root) {
    double real = root.getReal();
    double imaginary = root.getImaginary();
    // relative to the root: 1.0*^-10 is a root and not what is left of 0
    double delta = Config.DEFAULT_ROOTS_CHOP_DELTA * root.norm();
    if (Math.abs(real) <= delta) {
      real = 0.0;
    }
    if (Math.abs(imaginary) <= delta) {
      imaginary = 0.0;
    }
    return new org.hipparchus.complex.Complex(real, imaginary);
  }

  /**
   * Order two numerically determined roots by their real part and, for equal real parts, by their
   * imaginary part.
   *
   * <p>
   * The comparison uses the values rounded to {@link #ROOT_ORDER_PRECISION} significant digits, so
   * that a conjugate pair whose real parts differ only in their last bits - Laguerre's method
   * returns <code>0.18123244446987535</code> and <code>0.1812324444698754</code> for
   * <code>x^5-x-1</code> - is ordered by the imaginary part rather than by that rounding residue.
   * Rounding to a fixed grid keeps the comparison transitive, which a plain tolerance wouldn't be.
   */
  private static int compareRoots(org.hipparchus.complex.Complex root1,
      org.hipparchus.complex.Complex root2) {
    int result = Double.compare(significantDigits(root1.getReal()), //
        significantDigits(root2.getReal()));
    if (result != 0) {
      return result;
    }
    result = Double.compare(significantDigits(root1.getImaginary()), //
        significantDigits(root2.getImaginary()));
    if (result != 0) {
      return result;
    }
    result = Double.compare(root1.getReal(), root2.getReal());
    if (result != 0) {
      return result;
    }
    return Double.compare(root1.getImaginary(), root2.getImaginary());
  }

  /** The number of significant digits which {@link #compareRoots} considers reliable. */
  private static final int ROOT_ORDER_PRECISION = 12;

  private static double significantDigits(double value) {
    if (value == 0.0 || !Double.isFinite(value)) {
      // normalize -0.0 to 0.0, so that it doesn't sort before 0.0
      return value == 0.0 ? 0.0 : value;
    }
    return new java.math.BigDecimal(value).round(new java.math.MathContext(ROOT_ORDER_PRECISION))
        .doubleValue();
  }

  /**
   * The roots of a polynomial with complex machine coefficients, as a list of numbers.
   *
   * @return <code>F.NIL</code> if the expression is no such polynomial
   */
  private static IASTMutable complexCoefficientRoots(IExpr polynomial, ISymbol variable) {
    org.hipparchus.complex.Complex[] coefficients;
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(F.list(variable));
      IAST list = ring.create(polynomial).coefficientList();
      coefficients = new org.hipparchus.complex.Complex[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        IExpr value = list.get(i).evalNumber();
        if (value == null || !value.isNumber()) {
          return F.NIL;
        }
        coefficients[i - 1] = new org.hipparchus.complex.Complex(((INumber) value).reDoubleValue(),
            ((INumber) value).imDoubleValue());
      }
    } catch (RuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      return F.NIL;
    }
    org.hipparchus.complex.Complex[] roots = allComplexRootsAberth(coefficients);
    if (roots == null) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(roots.length);
    for (org.hipparchus.complex.Complex root : roots) {
      result.append(chopRoot(F.complexNum(root.getReal(), root.getImaginary())));
    }
    return result;
  }

  /**
   * All roots of the polynomial <code>c[0] + c[1]*x + ... + c[n]*x^n</code> with complex
   * coefficients, by the simultaneous iteration of Aberth and Ehrlich followed by Newton steps on
   * each root.
   *
   * @return <code>null</code> if a coefficient is not finite or the iteration does not settle
   */
  public static org.hipparchus.complex.Complex[] allComplexRootsAberth(
      org.hipparchus.complex.Complex[] c) {
    int n = c.length - 1;
    while (n > 0 && c[n].isZero()) {
      n--;
    }
    if (n < 1) {
      return null;
    }
    for (int i = 0; i <= n; i++) {
      if (c[i].isNaN() || c[i].isInfinite()) {
        return null;
      }
    }
    // roots at zero
    int zeros = 0;
    while (zeros < n && c[zeros].isZero()) {
      zeros++;
    }
    final int degree = n - zeros;
    org.hipparchus.complex.Complex[] a = new org.hipparchus.complex.Complex[degree + 1];
    for (int i = 0; i <= degree; i++) {
      a[i] = c[i + zeros].divide(c[n]);
    }
    org.hipparchus.complex.Complex[] z = new org.hipparchus.complex.Complex[degree];
    // start on a circle of the Cauchy-like radius, at angles which avoid symmetric positions
    double radius = 0.0;
    for (int k = 0; k < degree; k++) {
      radius = Math.max(radius, Math.pow(a[k].norm(), 1.0 / (degree - k)));
    }
    radius = Math.max(radius, 1.0e-3);
    for (int k = 0; k < degree; k++) {
      double angle = 2.0 * Math.PI * k / degree + 0.4;
      z[k] = new org.hipparchus.complex.Complex(radius * Math.cos(angle), radius * Math.sin(angle));
    }
    boolean converged = false;
    for (int iteration = 0; iteration < 1000 && !converged; iteration++) {
      converged = true;
      for (int i = 0; i < degree; i++) {
        org.hipparchus.complex.Complex[] value = evaluateWithDerivative(a, z[i]);
        if (value[0].isZero()) {
          continue;
        }
        org.hipparchus.complex.Complex ratio = value[0].divide(value[1]);
        org.hipparchus.complex.Complex sum = org.hipparchus.complex.Complex.ZERO;
        for (int j = 0; j < degree; j++) {
          if (j != i) {
            sum = sum.add(z[i].subtract(z[j]).reciprocal());
          }
        }
        org.hipparchus.complex.Complex step =
            ratio.divide(org.hipparchus.complex.Complex.ONE.subtract(ratio.multiply(sum)));
        if (step.isNaN() || step.isInfinite()) {
          return null;
        }
        z[i] = z[i].subtract(step);
        if (step.norm() > 1.0e-15 * Math.max(1.0, z[i].norm())) {
          converged = false;
        }
      }
    }
    if (!converged && !hasSmallResiduals(a, z)) {
      // near a multiple root the steps stay at the size of the rounding errors, so the iteration
      // doesn't settle although the roots are as accurate as machine numbers allow
      return null;
    }
    for (int i = 0; i < degree; i++) {
      // polish each root with Newton steps while they make the residual smaller
      for (int k = 0; k < 3; k++) {
        org.hipparchus.complex.Complex[] value = evaluateWithDerivative(a, z[i]);
        if (value[1].isZero()) {
          break;
        }
        org.hipparchus.complex.Complex next = z[i].subtract(value[0].divide(value[1]));
        if (!(evaluateWithDerivative(a, next)[0].norm() < value[0].norm())) {
          break;
        }
        z[i] = next;
      }
    }
    org.hipparchus.complex.Complex[] roots = new org.hipparchus.complex.Complex[n];
    for (int i = 0; i < zeros; i++) {
      roots[i] = org.hipparchus.complex.Complex.ZERO;
    }
    System.arraycopy(z, 0, roots, zeros, degree);
    return roots;
  }

  /**
   * Whether every <code>z[i]</code> is a root of the polynomial with the coefficients
   * <code>a</code> up to a relative backward error of <code>1e-10</code>.
   */
  private static boolean hasSmallResiduals(org.hipparchus.complex.Complex[] a,
      org.hipparchus.complex.Complex[] z) {
    for (org.hipparchus.complex.Complex x : z) {
      double modulus = x.norm();
      double scale = 0.0;
      for (int k = a.length - 1; k >= 0; k--) {
        scale = scale * modulus + a[k].norm();
      }
      double residual = evaluateWithDerivative(a, x)[0].norm();
      if (!(residual <= 1.0e-10 * scale)) {
        return false;
      }
    }
    return true;
  }

  /** <code>{p(x), p'(x)}</code> by Horner's scheme. */
  private static org.hipparchus.complex.Complex[] evaluateWithDerivative(
      org.hipparchus.complex.Complex[] a, org.hipparchus.complex.Complex x) {
    org.hipparchus.complex.Complex p = a[a.length - 1];
    org.hipparchus.complex.Complex derivative = org.hipparchus.complex.Complex.ZERO;
    for (int i = a.length - 2; i >= 0; i--) {
      derivative = derivative.multiply(x).add(p);
      p = p.multiply(x).add(a[i]);
    }
    return new org.hipparchus.complex.Complex[] {p, derivative};
  }

  /**
   * Get the coefficient list of a univariate polynomial.
   *
   * @param polynomial
   * @param variable
   * @return <code>null</code> if the list couldn't be evaluated.
   */
  public static double[] coefficients(IExpr polynomial, final ISymbol variable)
      throws JASConversionException {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(F.list(variable));
      ExprPolynomial poly = ring.create(polynomial);

      IAST list = poly.coefficientList();
      int degree = list.size() - 2;
      double[] result = new double[degree + 1];
      for (int i = 1; i < list.size(); i++) {
        IReal temp = list.get(i).evalReal();
        if (temp != null) {
          result[i - 1] = temp.doubleValue();
        } else {
          return null;
        }
      }
      return result;
    } catch (RuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      // Polynomial expected!
      return null;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private RootsFunctions() {}
}
