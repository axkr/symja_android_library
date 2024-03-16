package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.evalExpandAll;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
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

public class RootsFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

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
     * @return
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
        List<IExpr> varList = eVar.getVarList().copyTo();

        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
        GenPolynomial<Complex<BigRational>> poly = jas.numericExpr2JAS(expr);

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
      } catch (IllegalArgumentException | InvalidBoundaryException | JASConversionException e) {
        // Illegal arguments: \"`1`\" in `2`
        return Errors.printMessage(S.RootIntervals, "argillegal", F.List(arg), engine);
      }
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

    /**
     * @param coefficients
     * @return <code>F.NIL</code> if the result couldn't be evaluated
     */
    private static IAST rootsUp2Degree3(double[] coefficients) {
      if (coefficients.length == 0) {
        return F.NIL;
      }
      if (coefficients.length == 1) {
        return quadratic(0.0, 0.0, coefficients[0]);
      }
      if (coefficients.length == 2) {
        return quadratic(0.0, coefficients[1], coefficients[0]);
      }
      if (coefficients.length == 3) {
        return quadratic(coefficients[2], coefficients[1], coefficients[0]);
      }
      IAST result = F.NIL;
      if (coefficients.length == 4) {
        result = cubic(coefficients[3], coefficients[2], coefficients[1], coefficients[0]);
      }
      return result;
    }

    private static IAST quadratic(double a, double b, double c) {
      IASTAppendable result = F.ListAlloc(2);
      double discriminant = (b * b - (4 * a * c));
      if (F.isZero(discriminant)) {
        double bothEqual = ((-b / (2.0 * a)));
        result.append(bothEqual);
        result.append(bothEqual);
      } else if (discriminant < 0.0) {
        // two complex roots
        double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
        double realPart = (-b / (2.0 * a));
        result.append(F.complex(realPart, imaginaryPart));
        result.append(F.complex(realPart, -imaginaryPart));
      } else {
        // two real roots
        double real1 = ((-b + Math.sqrt(discriminant)) / (2.0 * a));
        double real2 = ((-b - Math.sqrt(discriminant)) / (2.0 * a));
        result.append(real1);
        result.append(real2);
      }
      return result;
    }

    /**
     * See <a href= "http://stackoverflow.com/questions/13328676/c-solving-cubic-equations" > http
     * ://stackoverflow.com/questions/13328676/c-solving-cubic-equations</a>
     *
     * @param a
     * @param b
     * @param c
     * @param d
     */
    private static IAST cubic(double a, double b, double c, double d) {
      if (F.isZero(a)) {
        return F.NIL;
      }
      if (F.isZero(d)) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(3);
      b /= a;
      c /= a;
      d /= a;

      double q = (3.0 * c - (b * b)) / 9.0;
      double r = -(27.0 * d) + b * (9.0 * c - 2.0 * (b * b));
      r /= 54.0;
      double discriminant = q * q * q + r * r;

      double term1 = (b / 3.0);
      if (discriminant > 0) {
        // one root real, two are complex
        double s = r + Math.sqrt(discriminant);
        s = ((s < 0) ? -Math.pow(-s, (1.0 / 3.0)) : Math.pow(s, (1.0 / 3.0)));
        double t = r - Math.sqrt(discriminant);
        t = ((t < 0) ? -Math.pow(-t, (1.0 / 3.0)) : Math.pow(t, (1.0 / 3.0)));
        result.append(-term1 + s + t);
        term1 += (s + t) / 2.0;
        double realPart = -term1;
        term1 = Math.sqrt(3.0) * (-t + s) / 2;
        result.append(F.complex(realPart, term1));
        result.append(F.complex(realPart, -term1));
        return result;
      }

      // The remaining options are all real
      double r13;
      if (F.isZero(discriminant)) {
        // All roots real, at least two are equal.
        r13 = ((r < 0) ? -Math.pow(-r, (1.0 / 3.0)) : Math.pow(r, (1.0 / 3.0)));
        result.append(-term1 + 2.0 * r13);
        result.append(-(r13 + term1));
        result.append(-(r13 + term1));
        return result;
      }

      // Only option left is that all roots are real and unequal (to get here,
      // q < 0)
      q = -q;
      double dum1 = q * q * q;
      dum1 = Math.acos(r / Math.sqrt(dum1));
      r13 = 2.0 * Math.sqrt(q);
      result.append(-term1 + r13 * Math.cos(dum1 / 3.0));
      result.append(-term1 + r13 * Math.cos((dum1 + 2.0 * Math.PI) / 3.0));
      result.append(-term1 + r13 * Math.cos((dum1 + 4.0 * Math.PI) / 3.0));
      return result;
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
  private static class Roots extends AbstractFunctionEvaluator {

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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isEqual()) {
        IAST equalAST = (IAST) arg1;
        if (equalAST.arg2().isZero()) {
          arg1 = equalAST.arg1();
        } else {
          arg1 = engine.evaluate(F.Subtract(equalAST.arg1(), equalAST.arg2()));
        }
      } else {
        LOGGER.log(engine.getLogLevel(),
            "{}: Equal() expression expected at position 1 instead of {}", ast.topHead(),
            ast.arg1());
        return F.NIL;
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

        LOGGER.log(engine.getLogLevel(),
            "{}: factorization only possible for univariate polynomials at position 2 instead of {}",
            ast.topHead(), ast.arg2());
        return F.NIL;
      }
      IAST variables = eVar.getVarList();
      IExpr variable = variables.arg1();
      IAST list = roots(arg1, false, variables, engine);
      if (list.isPresent()) {
        return F.mapFunction(S.Or, list, t -> F.Equal(variable, t));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static IAST complexRoots(final IExpr arg1, IAST variables, EvalEngine engine) {
    if (variables.size() != 2) {
      // factor only possible for univariate polynomials
      LOGGER.log(engine.getLogLevel(),
          "NRoots: factorization only possible for univariate polynomials");
      return F.NIL;
    }
    IExpr expr = evalExpandAll(arg1, engine);

    IExpr variable = variables.arg1();
    double[] coefficients = Expr2Object.toPolynomial(expr, variable);
    if (coefficients != null) {
      try {
        IASTMutable list;
        if (coefficients.length <= 4) {
          IASTAppendable p = F.PlusAlloc(coefficients.length);
          for (int i = 0; i < coefficients.length; i++) {
            p.append(F.Times(F.num(coefficients[i]), F.Power(variable, i)));
          }
          expr = engine.evaluate(p);
          list = QuarticSolver.solve(p, variables.arg1());
          for (int i = 1; i < list.size(); i++) {
            expr = engine.evaluate(list.get(i));
            if (expr.isInexactNumber()) {
              list.set(i, F.chopNumber((INumber) expr, Config.DEFAULT_ROOTS_CHOP_DELTA));
            }
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
        LOGGER.debug("RootsFunctions.roots() failed", mrex);
        return F.NIL;
      }
    }
    IExpr denom = F.C1;
    if (expr.isAST()) {
      expr = Algebra.together((IAST) expr, engine);

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

    IExpr expr = evalExpandAll(arg1, engine);

    IExpr denom = F.C1;
    if (expr.isAST()) {
      expr = Algebra.together((IAST) expr, engine);

      // split expr into numerator and denominator
      denom = S.Denominator.of(engine, expr);
      if (!denom.isOne()) {
        // search roots for the numerator expression
        expr = S.Numerator.of(engine, expr);
      }
    }
    IAST result =
        rootsOfVariable(expr, denom, variables, numericSolutions, createSet, sort, engine);
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
  public static IAST findRoots(IExpr polynomialExpr, final IAST variables) {
    double[] coefficients = coefficients(polynomialExpr, (ISymbol) variables.arg1());
    if (coefficients == null) {
      return F.NIL;
    }
    return findRoots(coefficients);
  }

  public static IASTMutable rootsOfExprPolynomial(final IExpr expr, IAST varList,
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
      result = rootsOfQuarticPolynomial(ePoly);
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
      LOGGER.debug("RootsFunctions.rootsOfExprPolynomial() failed", e2);
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
      LOGGER.debug("RootsFunctions.rootsOfQuadraticExprPolynomial() failed", e2);
    }
    return result;
  }

  /**
   * Solve a polynomial with degree &lt;= 4.
   *
   * @param polynomial the polynomial
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  private static IASTAppendable rootsOfQuarticPolynomial(ExprPolynomial polynomial) {
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
      IASTAppendable result = QuarticSolver.quarticSolve(a, b, c, d, e);
      if (result.isPresent()) {
        return (IASTAppendable) QuarticSolver.sortASTArguments(result);
      }
    }

    return F.NIL;
  }

  /**
   * Solve polynomials of the form <code>a * x^n + b == 0</code>.
   *
   * @param n
   * @param polynomial
   * @return
   */
  private static IASTAppendable nthComplexRoot(int n, ExprPolynomial polynomial) {
    IExpr coefficientN = C0;
    IExpr coefficient0 = C0;
    for (ExprMonomial monomial : polynomial) {
      IExpr coefficient = monomial.coefficient();
      long lExp = monomial.exponent().getVal(0);
      if (lExp == n) {
        coefficientN = coefficient;
      } else if (lExp == 0) {
        coefficient0 = coefficient;
      } else {
        return F.NIL;
      }
    }
    if (coefficientN.isZero() || coefficient0.isZero()) {
      return F.NIL;
    }

    EvalEngine engine = EvalEngine.get();
    IExpr a = engine.evaluate(F.Divide(F.Negate(coefficient0), coefficientN));
    if (a.isNumber()) {
      // z = r*(Cos(θ)+I*Sin(θ))
      IAST z = ((INumber) a).toPolarCoordinates();
      IExpr r = z.arg1();
      IExpr theta = z.arg2();

      IRational fraction = F.fraction(1, n);
      IExpr f1 = F.Power(r, fraction);
      return F.mapRange(0, n, k -> {
        IAST argCosSin = F.Times(fraction, F.Plus(theta, F.Times(F.ZZ(k + k), S.Pi)));
        IAST f2 = F.Plus(F.Cos(argCosSin), F.Times(F.CI, F.Sin(argCosSin)));
        return F.Times(f1, f2);
      });
    }
    return F.NIL;
  }

  /**
   * Solve polynomials of the form <code>a * x^varDegree + b == 0</code>
   *
   * @param varDegree
   * @param polynomial
   * @return
   */
  private static IASTAppendable unitPolynomial(int varDegree, ExprPolynomial polynomial) {
    IExpr a = C0;
    IExpr b = C0;
    for (ExprMonomial monomial : polynomial) {
      IExpr coeff = monomial.coefficient();
      long lExp = monomial.exponent().getVal(0);
      if (lExp == varDegree) {
        a = coeff;
      } else if (lExp == 0) {
        b = coeff;
      } else {
        return F.NIL;
      }
    }
    if (a.isZero() || b.isZero()) {
      return F.NIL;
    }

    boolean isNegative = false;
    IExpr rhsNumerator = EvalEngine.get().evaluate(b.negate());
    IExpr rhsDenominator = a;
    if ((varDegree & 0x0001) == 0x0001) {
      // odd
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IASTMutable temp = rhsNumerator.mapThread(F.Power(F.Slot1, F.fraction(1, varDegree)), 1);
        if (rhsNumerator.first().isNegative()) {
          isNegative = true;
          temp.set(1, rhsNumerator.first().negate());
        }
        zNumerator = EvalEngine.get().evaluate(temp);
      } else {
        if (rhsNumerator.isNegative()) {
          isNegative = true;
          rhsNumerator = rhsNumerator.negate();
        }
        zNumerator = EvalEngine.get().evaluate(F.Power(rhsNumerator, F.QQ(1, varDegree)));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        if (rhsDenominator.first().isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = ((IAST) rhsDenominator).setAtCopy(1, rhsDenominator.first().negate());
        }
        IASTMutable temp = rhsDenominator.mapThread(F.Power(F.Slot1, F.QQ(-1, varDegree)), 1);
        zDenominator = EvalEngine.get().evaluate(temp);
      } else {
        if (rhsDenominator.isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = rhsDenominator.negate();
        }
        zDenominator = EvalEngine.get().evaluate(F.Power(rhsDenominator, F.QQ(-1, varDegree)));
      }
      final int increment = isNegative ? 1 : 0;
      return F.mapRange(0, varDegree, i -> //
      F.Times(F.Power(F.CN1, i + increment), F.Power(F.CN1, F.QQ(i, varDegree)), zNumerator,
          zDenominator));
    } else {
      // even
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IExpr temp = rhsNumerator.mapThread(F.Power(F.Slot1, F.QQ(1, varDegree)), 1);
        zNumerator = EvalEngine.get().evaluate(temp);
      } else {
        zNumerator = EvalEngine.get().evaluate(F.Power(rhsNumerator, F.QQ(1, varDegree)));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        IExpr temp = rhsDenominator.mapThread(F.Power(F.Slot1, F.QQ(-1, varDegree)), 1);
        zDenominator = EvalEngine.get().evaluate(temp);
      } else {
        zDenominator = EvalEngine.get().evaluate(F.Power(rhsDenominator, F.QQ(-1, varDegree)));
      }

      IASTAppendable result = F.ListAlloc(varDegree);
      long size = varDegree / 2;
      int k = 0; // isNegative?1:0;
      for (int i = 1; i <= size; i++) {
        result.append(
            F.Times(F.CN1, F.Power(F.CN1, F.fraction(k, varDegree)), zNumerator, zDenominator));
        result.append(F.Times(F.Power(F.CN1, F.fraction(k, varDegree)), zNumerator, zDenominator));
        k += 2;
      }
      return result;
    }
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
    return rootsOfVariable(expr, denominator, variables, numericSolutions, true, true, engine);
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
    IASTMutable result = F.NIL;
    List<IExpr> varList = variables.copyTo();
    try {
      IAST list = rootsOfQuadraticExprPolynomial(expr, variables);
      if (list.isPresent()) {
        return list;
      }

      JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
      GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numericSolutions);
      // if (polyRat.degree(0) <= 2) {
      result = rootsOfExprPolynomial(expr, variables, false);
      if (result.isPresent()) {
        return result;
      }
      // }
      IASTAppendable newResult = F.ListAlloc(8);
      IAST factorRational = Algebra.factorRational(polyRat, jas, S.List);
      if (factorRational.isNIL()) {
        factorRational = F.Times(expr);
      }
      for (int i = 1; i < factorRational.size(); i++) {
        IExpr factor = factorRational.get(i);
        IExpr temp = F.evalExpand(factor);
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
          IAST factorComplex = Algebra.factorRational(polyRat, jas, S.List);
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
              IAST resultList = findRoots(temp, variables);
              if (resultList.size() > 0) {
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
      // JAS or "findRoots" may throw RuntimeExceptions
      result = rootsOfExprPolynomial(expr, variables, true);
    }
    if (result.isPresent()) {
      if (!denominator.isNumber()) {
        // eliminate roots from the result list, which occur in the
        // denominator
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
    return F.NIL;
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
  private static org.hipparchus.complex.Complex[] allComplexRootsLaguerre(
      @Nonnull double[] coefficients) {
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
      // Polynomial expected!
      return null;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private RootsFunctions() {}
}
