package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.evalExpandAll;
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
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
              resultList.append(
                  JASConvert.jas2Numeric(refine.getCenter(), Config.DEFAULT_ROOTS_CHOP_DELTA));
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
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
        if (!arg1.isPolynomialStruct()) {
          // `1` is expected to be a polynomial equation in the variable `2` with numeric
          // coefficients.
          return Errors.printMessage(ast.topHead(), "nnumeq", F.List(arg1, variables), engine);
        }
      }

      IExpr temp = complexRoots(arg1, variables, engine);
      if (!temp.isList()) {
        return F.NIL;
      }
      IAST list = (IAST) temp;
      int size = list.size();
      return F.mapRange(1, size, i -> engine.evalN(list.get(i)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

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
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      boolean cubicsInRadicals = options[0].isTrue();
      boolean quarticsInRadicals = options[1].isTrue();
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
      IAST list =
          roots(arg1, false, variables, true, true, cubicsInRadicals, quarticsInRadicals, engine);
      if (list.isPresent()) {
        return F.mapFunction(S.Or, list, t -> F.Equal(variable, t));
      }
      return F.NIL;
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
          new IBuiltInSymbol[] {S.Cubics, S.Quartics}, //
          new IExpr[] {S.True, S.True});
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
            if (F.isZero(coefficients[i])) {
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
            if (expr.isInexactNumber()) {
              list.set(i, F.chopNumber((INumber) expr, Config.DEFAULT_ROOTS_CHOP_DELTA));
            }
          }
          if (list.isEmptyList()) {
            return F.NIL;
          }
        } else {
          org.hipparchus.complex.Complex[] roots = allComplexRootsLaguerre(coefficients);
          if (roots == null) {
            return F.NIL;
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
      denom = S.Denominator.funEval(engine, expr);
      if (!denom.isOne()) {
        // search roots for the numerator expression
        expr = S.Numerator.funEval(engine, expr);
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
      org.hipparchus.complex.Complex[] complexRoots = allComplexRootsLaguerre(coefficients);
      if (complexRoots == null) {
        return F.NIL;
      }
      return F.mapRange(0, complexRoots.length,
          i -> F.chopExpr(F.complexNum(complexRoots[i].getReal(), complexRoots[i].getImaginary()),
              Config.DEFAULT_ROOTS_CHOP_DELTA));
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
   * of the &quot;very simple forms&quot; which {@link #unitPolynomial(int,
   * org.matheclipse.core.polynomials.longexponent.ExprPolynomial)} solves in radicals whatever the
   * {@link S#Cubics} and {@link S#Quartics} options request.
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
  private static IAST generalRootObjects(IExpr polynomial, IExpr variable,
      boolean cubicsInRadicals, boolean quarticsInRadicals, boolean numericSolutions,
      boolean allowRootObjects, EvalEngine engine) {
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
            result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
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
          result.set(i, F.chopExpr(result.get(i), Config.DEFAULT_ROOTS_CHOP_DELTA));
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
      a = C0;
      b = C0;
      c = C0;
      d = C0;
      e = C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          return F.NIL;
        }
      }
      IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e, createSet, true);
      if (result.isPresent()) {
        return (IASTAppendable) QuarticSolver.sortASTArguments(result);
      }
    }

    return F.NIL;
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
    IASTAppendable result;
    if ((reducedDegree & 0x0001) == 0x0001) {
      // odd
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IASTMutable temp =
            rhsNumerator.mapThread(F.Power(F.Slot1, F.QQ(1, reducedDegree)), 1);
        if (rhsNumerator.first().isNegative()) {
          isNegative = true;
          temp.set(1, rhsNumerator.first().negate());
        }
        zNumerator = engine.evaluate(temp);
      } else {
        if (rhsNumerator.isNegative()) {
          isNegative = true;
          rhsNumerator = rhsNumerator.negate();
        }
        zNumerator = engine.evaluate(F.Power(rhsNumerator, F.QQ(1, reducedDegree)));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        if (rhsDenominator.first().isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = ((IAST) rhsDenominator).setAtCopy(1, rhsDenominator.first().negate());
        }
        IASTMutable temp =
            rhsDenominator.mapThread(F.Power(F.Slot1, F.QQ(-1, reducedDegree)), 1);
        zDenominator = engine.evaluate(temp);
      } else {
        if (rhsDenominator.isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = rhsDenominator.negate();
        }
        zDenominator = engine.evaluate(F.Power(rhsDenominator, F.QQ(-1, reducedDegree)));
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
      a = C0;
      b = C0;
      c = C0;
      d = C0;
      e = C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Roots::Unexpected exponent value: " + lExp);
        }
      }
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
          for (int j = 1; j < quarticResultList.size(); j++) {
            if (numericSolutions) {
              newResult.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
                  Config.DEFAULT_ROOTS_CHOP_DELTA));
            } else {
              newResult.append(quarticResultList.get(j));
            }
          }
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
              for (int j = 1; j < quarticResultList.size(); j++) {
                if (numericSolutions) {
                  newResult.append(F.chopExpr(engine.evalN(quarticResultList.get(j)),
                      Config.DEFAULT_ROOTS_CHOP_DELTA));
                } else {
                  newResult.append(quarticResultList.get(j));
                }
              }
            } else {
              IAST rootObjects = allowRootObjects //
                  ? rootObjects(temp, variables.arg1(), numericSolutions, engine)
                  : F.NIL;
              if (rootObjects.isPresent()) {
                newResult.appendArgs(rootObjects);
              } else {
                IAST resultList = findRoots(temp, variables);
                if (resultList.size() > 0) {
                  newResult.appendArgs(resultList);
                }
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
      LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
      // see https://github.com/Hipparchus-Math/hipparchus/issues/177 for initial value
      // https://stackoverflow.com/q/65960318
      return solver.solveAllComplex(coefficients, 10_000, 1.0);
    } catch (MathRuntimeException mre) {
      // mre.printStackTrace();
      // org.hipparchus.exception.MathIllegalStateException: maximal count (100,000) exceeded
    }
    return null;
  }

  /**
   * Determine all complex roots of the univariate polynomial <code>polynomial</code> numerically.
   *
   * <p>
   * In contrast to {@link #roots(IExpr, boolean, IAST, EvalEngine)} the roots are <b>not</b>
   * collected in a set: a root of multiplicity <code>k</code> is returned <code>k</code> times, as
   * <code>NSolve</code> and <code>Solve</code> do in
   * <a href="https://reference.wolfram.com/language/ref/NSolve.html">Wolfram Language</a>. The
   * roots are ordered by their real part and, for equal real parts, by their imaginary part.
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
    try {
      coefficients = coefficients(evalExpandAll(polynomial, engine), variable);
    } catch (JASConversionException jce) {
      return F.NIL;
    }
    if (coefficients == null || coefficients.length < 2) {
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
      roots = allComplexRootsLaguerre(deflated);
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
    double delta = Config.DEFAULT_ROOTS_CHOP_DELTA * Math.max(1.0, root.norm());
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
