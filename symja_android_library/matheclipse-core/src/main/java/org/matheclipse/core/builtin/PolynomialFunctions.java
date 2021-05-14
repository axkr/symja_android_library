package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.evalExpandAll;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.polynomials.PolynomialsUtils;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.polynomials.longexponent.ExpVectorLong;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.polynomials.longexponent.ExprTermOrder;
import org.matheclipse.core.polynomials.symbolicexponent.ExpVectorSymbolic;
import org.matheclipse.core.polynomials.symbolicexponent.SymbolicPolynomial;
import org.matheclipse.core.polynomials.symbolicexponent.SymbolicPolynomialRing;
import org.matheclipse.core.reflection.system.rules.LegendrePRules;
import org.matheclipse.core.reflection.system.rules.LegendreQRules;
import org.matheclipse.core.reflection.system.rules.SphericalHarmonicYRules;
import org.matheclipse.parser.client.FEConfig;

import com.google.common.math.LongMath;

import edu.jas.application.GBAlgorithmBuilder;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.Rectangle;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;

public class PolynomialFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BellY.setEvaluator(new BellY());
      S.ChebyshevT.setEvaluator(new ChebyshevT());
      S.ChebyshevU.setEvaluator(new ChebyshevU());
      S.Coefficient.setEvaluator(new Coefficient());
//      S.CoefficientArrays.setEvaluator(new CoefficientArrays());
      S.CoefficientList.setEvaluator(new CoefficientList());
      S.CoefficientRules.setEvaluator(new CoefficientRules());
      S.Cyclotomic.setEvaluator(new Cyclotomic());
      S.Discriminant.setEvaluator(new Discriminant());
      S.Exponent.setEvaluator(new Exponent());
      S.GroebnerBasis.setEvaluator(new GroebnerBasis());
      S.HermiteH.setEvaluator(new HermiteH());
      S.LaguerreL.setEvaluator(new LaguerreL());
      S.LegendreP.setEvaluator(new LegendreP());
      S.LegendreQ.setEvaluator(new LegendreQ());
      S.MonomialList.setEvaluator(new MonomialList());
      S.NRoots.setEvaluator(new NRoots());
      S.Resultant.setEvaluator(new Resultant());
      S.RootIntervals.setEvaluator(new RootIntervals());
      S.Roots.setEvaluator(new Roots());
      S.SphericalHarmonicY.setEvaluator(new SphericalHarmonicY());
    }
  }

  /**
   *
   *
   * <pre>
   * Coefficient(polynomial, variable, exponent)
   * </pre>
   *
   * <blockquote>
   *
   * <p>get the coefficient of <code>variable^exponent</code> in <code>polynomial</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia - Coefficient
   *       Coefficient</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; Coefficient(10(x^2)+2(y^2)+2*x, x, 2)
   * 10
   * </pre>
   */
  private static class Coefficient extends AbstractFunctionEvaluator {
    private boolean setExponent(IAST list, IExpr expr, long[] exponents, long value) {
      for (int j = 1; j < list.size(); j++) {
        if (list.get(j).equals(expr)) {
          int ix = ExpVectorLong.indexVar(expr, list);
          exponents[ix] = value;
          return true;
        }
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr cached = F.REMEMBER_AST_CACHE.getIfPresent(ast);
      if (cached != null) {
        return cached;
      }

      IExpr arg2 = ast.arg2();
      // list of variable expressions extracted from the second argument
      IASTAppendable listOfVariables = null;
      // array of corresponding exponents for the list of variables
      IExpr[] exponents = null;

      // if (arg2.isTimes()) {
      // // Times(x, y^a,...)
      // IAST arg2AST = (IAST) arg2;
      // VariablesSet eVar = new VariablesSet(arg2AST);
      // listOfVariables = eVar.getVarList();
      // exponents = new long[listOfVariables.argSize()];
      // for (int i = 0; i < exponents.length; i++) {
      // exponents[i] = 0L;
      // }
      // for (int i = 1; i < arg2AST.size(); i++) {
      // long value = 1L;
      // IExpr a1 = arg2AST.get(i);
      // if (a1.isPower() && a1.exponent().isInteger()) {
      // a1 = arg2AST.get(i).base();
      // IInteger ii = (IInteger) arg2AST.get(i).exponent();
      // try {
      // value = ii.toLong();
      // } catch (ArithmeticException ae) {
      // return F.NIL;
      // }
      // }
      //
      // if (!setExponent(listOfVariables, a1, exponents, value)) {
      // return F.NIL;
      // }
      // }
      // } else {
      listOfVariables = F.ListAlloc();
      listOfVariables.append(arg2);
      exponents = new IExpr[1];
      exponents[0] = F.C1;
      // }

      try {
        IExpr n = F.C1;
        if (ast.isAST3()) {
          if (ast.arg3().isNegativeInfinity()) {
            return F.C0;
          }
          // n = Validate.checkLongType(ast.arg3());
          n = ast.arg3();
          for (int i = 0; i < exponents.length; i++) {
            exponents[i] = exponents[i].multiply(n);
          }
        }
        ExpVectorSymbolic expArr = new ExpVectorSymbolic(exponents);
        IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal(false);
        IAST subst = Algebra.substituteVariablesInPolynomial(expr, listOfVariables, "§Coefficient");
        expr = subst.arg1();
        listOfVariables = (IASTAppendable) subst.arg2();
        SymbolicPolynomialRing ring =
            new SymbolicPolynomialRing(ExprRingFactory.CONST, listOfVariables);
        SymbolicPolynomial poly = ring.create(expr, true, false, false);
        IExpr temp = poly.coefficient(expArr);
        F.REMEMBER_AST_CACHE.put(ast, temp);
        return temp;
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException ae) {
        if (FEConfig.SHOW_STACKTRACE) {
          ae.printStackTrace();
        }
        return F.C0;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class CoefficientArrays extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {

      IExpr expr = F.evalExpandAll(ast.arg1(), engine);
      VariablesSet eVar;
      IAST symbolList;
      List<IExpr> varList;
      if (ast.isAST1()) {
        // extract all variables from the polynomial expression
        eVar = new VariablesSet(ast.arg1());
        varList = eVar.getArrayList();
        symbolList = eVar.getVarList();
      } else {
        symbolList = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (!symbolList.isPresent()) {
          return F.NIL;
        }
        varList = new ArrayList<IExpr>(symbolList.argSize());
        symbolList.forEach(x -> varList.add(x));
      }
      TermOrder termOrder = TermOrderByName.Lexicographic;

      if (ast.size() > 3) {
        if (ast.arg3().isSymbol()) {
          termOrder = JASIExpr.monomialOrder((ISymbol) ast.arg3(), termOrder);
        }
      }

      try {
        ExprPolynomialRing ring =
            new ExprPolynomialRing(symbolList, new ExprTermOrder(termOrder.getEvord()));
        ExprPolynomial poly = ring.create(expr, false, true, true);
        return poly.coefficientArrays((int) poly.degree());
      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      // default mapping
      IASTAppendable ruleList = F.ListAlloc(symbolList.size());
      for (int j = 1; j < symbolList.size(); j++) {
        ruleList.append(F.C0);
      }
      return F.List(F.Rule(ruleList, expr));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /** */
  private static class CoefficientList extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isList()) {
        return ((IAST) arg1).mapThread(ast, 1);
      }
      IExpr expr = F.evalExpandAll(arg1, engine).normal(false);
      IAST list = ast.arg2().orNewList();
      return coefficientList(expr, list);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Get exponent vectors and coefficients of monomials of a polynomial expression.
   *
   * <p>See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
   */
  private static class CoefficientRules extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isList()) {
        return ((IAST) arg1).mapThread(ast, 1);
      }
      IExpr expr = F.evalExpandAll(ast.arg1(), engine);
      VariablesSet eVar;
      IAST symbolList;
      List<IExpr> varList;
      if (ast.isAST1()) {
        // extract all variables from the polynomial expression
        eVar = new VariablesSet(ast.arg1());
        varList = eVar.getArrayList();
        symbolList = eVar.getVarList();
      } else {
        symbolList = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (!symbolList.isPresent()) {
          return F.NIL;
        }
        varList = new ArrayList<IExpr>(symbolList.argSize());
        symbolList.forEach(x -> varList.add(x));
      }
      TermOrder termOrder = TermOrderByName.Lexicographic;

      if (ast.size() > 3) {

        if (ast.arg3().isSymbol()) {
          termOrder = JASIExpr.monomialOrder((ISymbol) ast.arg3(), termOrder);
        } else {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
          IExpr option = options.getOption(S.Modulus);
          if (option.isPresent()) {
            try {
              if (option.isInteger()) {
                return coefficientRulesModulus(expr, varList, termOrder, option);
              }
            } catch (RuntimeException rex) {
              // toInt() conversion failed
              if (FEConfig.SHOW_STACKTRACE) {
                rex.printStackTrace();
              }
            }
          }
          return F.NIL;
        }
      }

      try {
        ExprPolynomialRing ring =
            new ExprPolynomialRing(symbolList, new ExprTermOrder(termOrder.getEvord()));
        ExprPolynomial poly = ring.create(expr, false, true, true);
        return poly.coefficientRules();
      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      // default mapping
      IASTAppendable ruleList = F.ListAlloc(symbolList.size());
      for (int j = 1; j < symbolList.size(); j++) {
        ruleList.append(F.C0);
      }
      return F.List(F.Rule(ruleList, expr));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    /**
     * Get exponent vectors and coefficients of monomials of a polynomial expression.
     *
     * @param polynomial
     * @param variablesList
     * @param termOrder the JAS term ordering
     * @return the list of monomials of the univariate polynomial.
     */
    // public static IAST coefficientRules(IExpr polynomial, final List<IExpr> variablesList,
    // final TermOrder termOrder) throws JASConversionException {
    // JASIExpr jas = new JASIExpr(variablesList, ExprRingFactory.CONST, termOrder, false);
    // GenPolynomial<IExpr> polyExpr = jas.expr2IExprJAS(polynomial);
    // IASTAppendable resultList = F.ListAlloc(polyExpr.length());
    // for (Monomial<IExpr> monomial : polyExpr) {
    //
    // IExpr coeff = monomial.coefficient();
    // ExpVector exp = monomial.exponent();
    // int len = exp.length();
    // IASTAppendable ruleList = F.ListAlloc(len);
    // for (int i = 0; i < len; i++) {
    // ruleList.append(exp.getVal(len - i - 1));
    // }
    // resultList.append(F.Rule(ruleList, coeff));
    // }
    // return resultList;
    // }

    /**
     * Get exponent vectors and coefficients of monomials of a polynomial expression.
     *
     * @param polynomial
     * @param variablesList
     * @param termOrder the JAS term ordering
     * @param option the &quot;Modulus&quot; option
     * @return the list of monomials of the univariate polynomial.
     */
    private static IAST coefficientRulesModulus(
        IExpr polynomial, List<IExpr> variablesList, final TermOrder termOrder, IExpr option)
        throws JASConversionException {
      try {
        // found "Modulus" option => use ModIntegerRing
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
        JASModInteger jas = new JASModInteger(variablesList, modIntegerRing);
        GenPolynomial<ModLong> polyExpr = jas.expr2JAS(polynomial);
        IASTAppendable resultList = F.ListAlloc(polyExpr.length());
        for (Monomial<ModLong> monomial : polyExpr) {
          ModLong coeff = monomial.coefficient();
          ExpVector exp = monomial.exponent();
          int len = exp.length();
          IASTAppendable ruleList = F.ListAlloc(len);
          for (int i = 0; i < len; i++) {
            ruleList.append(exp.getVal(len - i - 1));
          }
          resultList.append(F.Rule(ruleList, F.ZZ(coeff.getVal())));
        }
        return resultList;
      } catch (ArithmeticException ae) {
        // toInt() conversion failed
        if (Config.DEBUG) {
          ae.printStackTrace();
        }
      }
      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * Cyclotomic(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Cyclotomic polynomial <code>C_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Cyclotomic_polynomial">Wikipedia - Cyclotomic
   *       polynomial</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Cyclotomic(25,x)
   * 1+x^5+x^10+x^15+x^20
   * </pre>
   *
   * <p>The case of the 105-th cyclotomic polynomial is interesting because 105 is the lowest
   * integer that is the product of three distinct odd prime numbers and this polynomial is the
   * first one that has a coefficient other than <code>1, 0</code> or <code>−1</code>:
   *
   * <pre>
   * &gt;&gt; Cyclotomic(105, x)
   * 1+x+x^2-x^5-x^6-2*x^7-x^8-x^9+x^12+x^13+x^14+x^15+x^16+x^17-x^20-x^22-x^24-x^26-x^28+x^31+x^32+x^33+x^34+x^35+x^36-x^39-x^40-2*x^41-x^42-x^43+x^46+x^47+x^48
   * </pre>
   */
  private static final class Cyclotomic extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg1().toIntDefault(-1);
      if (n >= 0) {
        if (n / 100 > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(n);
        }
        return cyclotomic(n, ast.arg2());
      }
      if (ast.arg1().isNumber()) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        IOFunctions.printMessage(ast.topHead(), "intnm", F.List(F.C1, ast), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static IExpr cyclotomic(int n, final IExpr x) {
      switch (n) {
        case 0:
          return F.C1;
        case 1:
          return F.Plus(F.CN1, x);
        case 2:
          return F.Plus(F.C1, x);
        case 3:
          return F.Plus(F.C1, x, F.Sqr(x));
      }
      // precondition n > 2
      if (x.isZero()) {
        return F.C1;
      }
      if (LongMath.isPrime(n)) {
        if (x.isRational()) {
          return F.sumRational(i -> ((IRational) x).pow(i), 0, n - 1);
        }
        return F.sum(i -> x.power(i), 0, n - 1);
      }
      if ((n & 0x00000001) == 0x00000000) {
        // n is even
        int nHalf = n / 2;
        if ((nHalf & 0x00000001) == 0x00000001) {
          // nHalf is odd
          if (LongMath.isPrime(nHalf)) {
            return F.sum(i -> x.negate().power(i), 0, nHalf - 1);
          }
          return cyclotomic(nHalf, x.negate());
        }
      }
      BigInteger bigN = BigInteger.valueOf(n);
      Object[] primePower = Primality.primePower(bigN);
      if (primePower != null) {
        int p = ((BigInteger) primePower[0]).intValue();
        int pPowerm = n / p;
        return cyclotomic(p, x.power(F.ZZ(pPowerm)));
      }
      IInteger ni = F.ZZ(n);
      IAST divisorList = ni.divisors();
      // Product((1 - x^d)^MoebiusMu(n/d), {d, divisorList) // Together
      return F.Together(
          F.intIterator(
              S.Times,
              d ->
                  F.Power(
                      F.Plus(F.C1, F.Negate(F.Power(x, d))),
                      F.MoebiusMu(F.Times(F.Power(d, -1), ni))),
              divisorList));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * Discriminant(poly, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>computes the discriminant of the polynomial <code>poly</code> with respect to the variable
   * <code>var</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Discriminant">Wikipedia - Discriminant</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Discriminant(a*x^2+b*x+c,x)
   * b^2-4*a*c
   * </pre>
   */
  private static class Discriminant extends AbstractFunctionEvaluator {
    // b^2 - 4*a*c
    private static final IExpr QUADRATIC = F.Plus(F.Sqr(F.b), F.Times(F.CN4, F.a, F.c));

    // b^2*c^2 - 4*a*c^3 - 4*b^3*d + 18*a*b*c*d - 27*a^2*d^2
    private static final IExpr CUBIC =
        F.Plus(
            F.Times(F.Sqr(F.b), F.Sqr(F.c)),
            F.Times(F.CN4, F.a, F.Power(F.c, F.C3)),
            F.Times(F.CN4, F.Power(F.b, F.C3), F.d),
            F.Times(F.ZZ(18L), F.a, F.b, F.c, F.d),
            F.Times(F.ZZ(-27L), F.Sqr(F.a), F.Sqr(F.d)));

    // Page 405
    // http://books.google.com/books?id=-gGzjSnNnR0C&lpg=PA402&vq=quartic&hl=de&pg=PA405#v=snippet&q=quartic&f=false

    // b^2*c^2*d^2 - 4*a*c^3*d^2 - 4*b^3*d^3 + 18*a*b*c*d^3 - 27*a^2*d^4 -
    // 4*b^2*c^3*e + 16*a*c^4*e + 18*b^3*c*d*e - 80*a*b*c^2*d*e - 6*a*b^2*d^2*e +
    // 144*a^2*c*d^2*e - 27*b^4*e^2 + 144*a*b^2*c*e^2 - 128*a^2*c^2*e^2 -
    // 192*a^2*b*d*e^2 + 256*a^3*e^3
    private static final IExpr QUARTIC =
        F.Plus(
            F.Times(F.Sqr(F.b), F.Sqr(F.c), F.Sqr(F.d)),
            F.Times(F.CN4, F.a, F.Power(F.c, F.C3), F.Sqr(F.d)),
            F.Times(F.CN4, F.Power(F.b, F.C3), F.Power(F.d, F.C3)),
            F.Times(F.ZZ(18L), F.a, F.b, F.c, F.Power(F.d, F.C3)),
            F.Times(F.ZZ(-27L), F.Sqr(F.a), F.Power(F.d, F.C4)),
            F.Times(F.CN4, F.Sqr(F.b), F.Power(F.c, F.C3), F.e),
            F.Times(F.ZZ(16L), F.a, F.Power(F.c, F.C4), F.e),
            F.Times(F.ZZ(18L), F.Power(F.b, F.C3), F.c, F.d, F.e),
            F.Times(F.ZZ(-80L), F.a, F.b, F.Sqr(F.c), F.d, F.e),
            F.Times(F.CN6, F.a, F.Sqr(F.b), F.Sqr(F.d), F.e),
            F.Times(F.ZZ(144L), F.Sqr(F.a), F.c, F.Sqr(F.d), F.e),
            F.Times(F.ZZ(-27L), F.Power(F.b, F.C4), F.Sqr(F.e)),
            F.Times(F.ZZ(144L), F.a, F.Sqr(F.b), F.c, F.Sqr(F.e)),
            F.Times(F.ZZ(-128L), F.Sqr(F.a), F.Sqr(F.c), F.Sqr(F.e)),
            F.Times(F.ZZ(-192L), F.Sqr(F.a), F.b, F.d, F.Sqr(F.e)),
            F.Times(F.ZZ(256L), F.Power(F.a, F.C3), F.Power(F.e, F.C3)));

    // b^2*c^2*d^2*e^2 - 4*a*c^3*d^2*e^2 - 4*b^3*d^3*e^2 + 18*a*b*c*d^3*e^2 -
    // 27*a^2*d^4*e^2 - 4*b^2*c^3*e^3 + 16*a*c^4*e^3 + 18*b^3*c*d*e^3 -
    // 80*a*b*c^2*d*e^3 - 6*a*b^2*d^2*e^3 + 144*a^2*c*d^2*e^3 - 27*b^4*e^4 +
    // 144*a*b^2*c*e^4 - 128*a^2*c^2*e^4 - 192*a^2*b*d*e^4 + 256*a^3*e^5 -
    // 4*b^2*c^2*d^3*f + 16*a*c^3*d^3*f + 16*b^3*d^4*f - 72*a*b*c*d^4*f +
    // 108*a^2*d^5*f + 18*b^2*c^3*d*e*f - 72*a*c^4*d*e*f - 80*b^3*c*d^2*e*f +
    // 356*a*b*c^2*d^2*e*f + 24*a*b^2*d^3*e*f - 630*a^2*c*d^3*e*f -
    // 6*b^3*c^2*e^2*f + 24*a*b*c^3*e^2*f + 144*b^4*d*e^2*f -
    // 746*a*b^2*c*d*e^2*f + 560*a^2*c^2*d*e^2*f + 1020*a^2*b*d^2*e^2*f -
    // 36*a*b^3*e^3*f + 160*a^2*b*c*e^3*f - 1600*a^3*d*e^3*f - 27*b^2*c^4*f^2 +
    // 108*a*c^5*f^2 + 144*b^3*c^2*d*f^2 - 630*a*b*c^3*d*f^2 - 128*b^4*d^2*f^2 +
    // 560*a*b^2*c*d^2*f^2 + 825*a^2*c^2*d^2*f^2 - 900*a^2*b*d^3*f^2 -
    // 192*b^4*c*e*f^2 + 1020*a*b^2*c^2*e*f^2 - 900*a^2*c^3*e*f^2 +
    // 160*a*b^3*d*e*f^2 - 2050*a^2*b*c*d*e*f^2 + 2250*a^3*d^2*e*f^2 -
    // 50*a^2*b^2*e^2*f^2 + 2000*a^3*c*e^2*f^2 + 256*b^5*f^3 - 1600*a*b^3*c*f^3 +
    // 2250*a^2*b*c^2*f^3 + 2000*a^2*b^2*d*f^3 - 3750*a^3*c*d*f^3 -
    // 2500*a^3*b*e*f^3 + 3125*a^4*f^4
    private static final IExpr QUINTIC =
        F.Plus(
            F.Times(F.Sqr(F.b), F.Sqr(F.c), F.Sqr(F.d), F.Sqr(F.e)),
            F.Times(F.CN4, F.a, F.Power(F.c, F.C3), F.Sqr(F.d), F.Sqr(F.e)),
            F.Times(F.CN4, F.Power(F.b, F.C3), F.Power(F.d, F.C3), F.Sqr(F.e)),
            F.Times(F.ZZ(18L), F.a, F.b, F.c, F.Power(F.d, F.C3), F.Sqr(F.e)),
            F.Times(F.ZZ(-27L), F.Sqr(F.a), F.Power(F.d, F.C4), F.Sqr(F.e)),
            F.Times(F.CN4, F.Sqr(F.b), F.Power(F.c, F.C3), F.Power(F.e, F.C3)),
            F.Times(F.ZZ(16L), F.a, F.Power(F.c, F.C4), F.Power(F.e, F.C3)),
            F.Times(F.ZZ(18L), F.Power(F.b, F.C3), F.c, F.d, F.Power(F.e, F.C3)),
            F.Times(F.ZZ(-80L), F.a, F.b, F.Sqr(F.c), F.d, F.Power(F.e, F.C3)),
            F.Times(F.CN6, F.a, F.Sqr(F.b), F.Sqr(F.d), F.Power(F.e, F.C3)),
            F.Times(F.ZZ(144L), F.Sqr(F.a), F.c, F.Sqr(F.d), F.Power(F.e, F.C3)),
            F.Times(F.ZZ(-27L), F.Power(F.b, F.C4), F.Power(F.e, F.C4)),
            F.Times(F.ZZ(144L), F.a, F.Sqr(F.b), F.c, F.Power(F.e, F.C4)),
            F.Times(F.ZZ(-128L), F.Sqr(F.a), F.Sqr(F.c), F.Power(F.e, F.C4)),
            F.Times(F.ZZ(-192L), F.Sqr(F.a), F.b, F.d, F.Power(F.e, F.C4)),
            F.Times(F.ZZ(256L), F.Power(F.a, F.C3), F.Power(F.e, F.C5)),
            F.Times(F.CN4, F.Sqr(F.b), F.Sqr(F.c), F.Power(F.d, F.C3), F.f),
            F.Times(F.ZZ(16L), F.a, F.Power(F.c, F.C3), F.Power(F.d, F.C3), F.f),
            F.Times(F.ZZ(16L), F.Power(F.b, F.C3), F.Power(F.d, F.C4), F.f),
            F.Times(F.ZZ(-72L), F.a, F.b, F.c, F.Power(F.d, F.C4), F.f),
            F.Times(F.ZZ(108L), F.Sqr(F.a), F.Power(F.d, F.C5), F.f),
            F.Times(F.ZZ(18L), F.Sqr(F.b), F.Power(F.c, F.C3), F.d, F.e, F.f),
            F.Times(F.ZZ(-72L), F.a, F.Power(F.c, F.C4), F.d, F.e, F.f),
            F.Times(F.ZZ(-80L), F.Power(F.b, F.C3), F.c, F.Sqr(F.d), F.e, F.f),
            F.Times(F.ZZ(356L), F.a, F.b, F.Sqr(F.c), F.Sqr(F.d), F.e, F.f),
            F.Times(F.ZZ(24L), F.a, F.Sqr(F.b), F.Power(F.d, F.C3), F.e, F.f),
            F.Times(F.ZZ(-630L), F.Sqr(F.a), F.c, F.Power(F.d, F.C3), F.e, F.f),
            F.Times(F.CN6, F.Power(F.b, F.C3), F.Sqr(F.c), F.Sqr(F.e), F.f),
            F.Times(F.ZZ(24L), F.a, F.b, F.Power(F.c, F.C3), F.Sqr(F.e), F.f),
            F.Times(F.ZZ(144L), F.Power(F.b, F.C4), F.d, F.Sqr(F.e), F.f),
            F.Times(F.ZZ(-746L), F.a, F.Sqr(F.b), F.c, F.d, F.Sqr(F.e), F.f),
            F.Times(F.ZZ(560L), F.Sqr(F.a), F.Sqr(F.c), F.d, F.Sqr(F.e), F.f),
            F.Times(F.ZZ(1020L), F.Sqr(F.a), F.b, F.Sqr(F.d), F.Sqr(F.e), F.f),
            F.Times(F.ZZ(-36L), F.a, F.Power(F.b, F.C3), F.Power(F.e, F.C3), F.f),
            F.Times(F.ZZ(160L), F.Sqr(F.a), F.b, F.c, F.Power(F.e, F.C3), F.f),
            F.Times(F.ZZ(-1600L), F.Power(F.a, F.C3), F.d, F.Power(F.e, F.C3), F.f),
            F.Times(F.ZZ(-27L), F.Sqr(F.b), F.Power(F.c, F.C4), F.Sqr(F.f)),
            F.Times(F.ZZ(108L), F.a, F.Power(F.c, F.C5), F.Sqr(F.f)),
            F.Times(F.ZZ(144L), F.Power(F.b, F.C3), F.Sqr(F.c), F.d, F.Sqr(F.f)),
            F.Times(F.ZZ(-630L), F.a, F.b, F.Power(F.c, F.C3), F.d, F.Sqr(F.f)),
            F.Times(F.ZZ(-128L), F.Power(F.b, F.C4), F.Sqr(F.d), F.Sqr(F.f)),
            F.Times(F.ZZ(560L), F.a, F.Sqr(F.b), F.c, F.Sqr(F.d), F.Sqr(F.f)),
            F.Times(F.ZZ(825L), F.Sqr(F.a), F.Sqr(F.c), F.Sqr(F.d), F.Sqr(F.f)),
            F.Times(F.ZZ(-900L), F.Sqr(F.a), F.b, F.Power(F.d, F.C3), F.Sqr(F.f)),
            F.Times(F.ZZ(-192L), F.Power(F.b, F.C4), F.c, F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(1020L), F.a, F.Sqr(F.b), F.Sqr(F.c), F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(-900L), F.Sqr(F.a), F.Power(F.c, F.C3), F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(160L), F.a, F.Power(F.b, F.C3), F.d, F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(-2050L), F.Sqr(F.a), F.b, F.c, F.d, F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(2250L), F.Power(F.a, F.C3), F.Sqr(F.d), F.e, F.Sqr(F.f)),
            F.Times(F.ZZ(-50L), F.Sqr(F.a), F.Sqr(F.b), F.Sqr(F.e), F.Sqr(F.f)),
            F.Times(F.ZZ(2000L), F.Power(F.a, F.C3), F.c, F.Sqr(F.e), F.Sqr(F.f)),
            F.Times(F.ZZ(256L), F.Power(F.b, F.C5), F.Power(F.f, F.C3)),
            F.Times(F.ZZ(-1600L), F.a, F.Power(F.b, F.C3), F.c, F.Power(F.f, F.C3)),
            F.Times(F.ZZ(2250L), F.Sqr(F.a), F.b, F.Sqr(F.c), F.Power(F.f, F.C3)),
            F.Times(F.ZZ(2000L), F.Sqr(F.a), F.Sqr(F.b), F.d, F.Power(F.f, F.C3)),
            F.Times(F.ZZ(-3750L), F.Power(F.a, F.C3), F.c, F.d, F.Power(F.f, F.C3)),
            F.Times(F.ZZ(-2500L), F.Power(F.a, F.C3), F.b, F.e, F.Power(F.f, F.C3)),
            F.Times(F.ZZ(3125L), F.Power(F.a, F.C4), F.Power(F.f, F.C4)));

    private ISymbol[] vars = {F.a, F.b, F.c, F.d, F.e, F.f};

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isSymbol()) {
        return F.NIL;
      }
      IExpr expr = F.evalExpandAll(ast.arg1(), engine);
      try {
        IAST univariateVariables = F.List(arg2);
        ExprPolynomialRing ring = new ExprPolynomialRing(univariateVariables);
        ExprPolynomial poly = ring.create(expr);

        long n = poly.degree();
        if (n >= 2L && n <= 5L) {
          IAST result = poly.coefficientList();
          IASTAppendable rules = F.ListAlloc(result.size());
          rules.appendArgs(result.size(), i -> F.Rule(vars[i - 1], result.get(i)));
          switch ((int) n) {
            case 2:
              return QUADRATIC.replaceAll(rules);
            case 3:
              return CUBIC.replaceAll(rules);
            case 4:
              return QUARTIC.replaceAll(rules);
            case 5:
              return QUINTIC.replaceAll(rules);
          }
        }
        IExpr fN = poly.leadingBaseCoefficient(); // coefficient(n);
        ExprPolynomial polyDiff = poly.derivativeUnivariate();
        // see:
        // http://en.wikipedia.org/wiki/Discriminant#Discriminant_of_a_polynomial
        return F.Divide(
            F.Times(
                F.Power(F.CN1, (n * (n - 1) / 2)),
                F.Resultant(poly.getExpr(), polyDiff.getExpr(), arg2)),
            fN);
      } catch (RuntimeException ex) {
        return engine.printMessage(
            ast.topHead()
                + ": polynomial expected at position 1 instead of "
                + ast.arg1().toString());
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol symbol) {
      symbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Exponent(polynomial, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>gives the maximum power with which <code>x</code> appears in the expanded form of <code>
   * polynomial</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Exponent(1+x^2+a*x^3, x)
   * 3
   * </pre>
   */
  private static class Exponent extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr cached = F.REMEMBER_AST_CACHE.getIfPresent(ast);
      if (cached != null) {
        return cached;
      }

      IExpr form = engine.evalPattern(ast.arg2());
      if (form.isList()) {
        return ((IAST) form).mapThread(ast, 2);
      }

      IExpr sym = S.Max;
      if (ast.isAST3()) {
        final IExpr arg3 = engine.evaluate(ast.arg3());
        // if (arg3.isSymbol()) {
        sym = arg3;
        // }
      }
      Set<IExpr> collector = new TreeSet<IExpr>();
      // final IExpr a1 = engine.evaluate(ast.arg1());
      // IExpr expr = a1;
      // if (a1.isAST()) {
      // expr = Algebra.expandAll((IAST) a1, null, true, true, engine);
      // if (!expr.isPresent()) {
      // expr = a1;
      // }
      // }
      IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal(false);
      IAST subst = Algebra.substituteVariablesInPolynomial(expr, F.List(form), "§Exponent");
      expr = subst.arg1();
      form = subst.arg2().first();
      // if (expr.isTimes()) {
      // expr =F.Distribute.of(expr);
      // }
      if (expr.isZero()) {
        collector.add(F.CNInfinity);
      } else if (expr.isAST()) {
        IAST arg1 = (IAST) expr;
        // final IPatternMatcher matcher = new PatternMatcherEvalEngine(form, engine);
        final IPatternMatcher matcher = engine.evalPatternMatcher(form);
        if (arg1.isPower()) {
          IExpr pEx = powerExponent(arg1, form, matcher, engine);
          collector.add(pEx);
        } else if (arg1.isPlus()) {
          for (int i = 1; i < arg1.size(); i++) {
            if (arg1.get(i).isAtom()) {
              if (arg1.get(i).isSymbol()) {
                if (matcher.test(arg1.get(i), engine)) {
                  collector.add(F.C1);
                } else {
                  collector.add(F.C0);
                }
              } else {
                collector.add(F.C0);
              }
            } else if (arg1.get(i).isPower()) {
              // IAST pow = (IAST) arg1.get(i);
              IExpr pEx = powerExponent((IAST) arg1.get(i), form, matcher, engine);
              collector.add(pEx);
              // if (matcher.test(pow.base(), engine)) {
              // collector.add(pow.exponent());
              // } else {
              // collector.add(F.C0);
              // }
            } else if (arg1.get(i).isTimes()) {
              timesExponent((IAST) arg1.get(i), form, matcher, collector, engine);
            } else {
              collector.add(F.C0);
            }
          }
        } else if (arg1.isTimes()) {
          timesExponent(arg1, form, matcher, collector, engine);
        }

      } else if (expr.isSymbol()) {
        final IPatternMatcher matcher = engine.evalPatternMatcher(form);
        if (matcher.test(expr)) {
          collector.add(F.C1);
        } else {
          collector.add(F.C0);
        }
      } else {
        collector.add(F.C0);
      }

      if (collector.size() == 0) {
        collector.add(F.C0);
      }
      IASTAppendable result = F.ast(sym, collector.size(), false);
      result.appendAll(collector);
      // for (IExpr exponent : collector) {
      // result.append(exponent);
      // }
      F.REMEMBER_AST_CACHE.put(ast, result);
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    private static IExpr powerExponent(
        IAST powerAST, final IExpr form, final IPatternMatcher matcher, EvalEngine engine) {
      if (matcher.test(powerAST.base(), engine)) {
        return powerAST.exponent();
      }
      if (matcher.isRuleWithoutPatterns()
          && form.isPower()
          && form.base().equals(powerAST.base())
          && form.exponent().isRational()) {
        return form.exponent().reciprocal().times(powerAST.exponent());
      }
      return F.C0;
    }

    private static void timesExponent(
        IAST timesAST,
        IExpr form,
        final IPatternMatcher matcher,
        Set<IExpr> collector,
        EvalEngine engine) {
      boolean evaled = false;
      IExpr argi;
      for (int i = 1; i < timesAST.size(); i++) {
        argi = timesAST.get(i);
        if (argi.isPower()) {
          IExpr pEx = powerExponent((IAST) argi, form, matcher, engine);
          if (!pEx.isZero()) {
            collector.add(pEx);
            evaled = true;
            break;
          }
          // if (matcher.test(argi.base(), engine)) {
          // collector.add(argi.exponent());
          // evaled = true;
          // break;
          // }
        } else if (argi.isSymbol()) {
          if (matcher.test(argi, engine)) {
            collector.add(F.C1);
            evaled = true;
            break;
          }
        }
      }
      if (!evaled) {
        collector.add(F.C0);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Resultant(polynomial1, polynomial2, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>computes the resultant of the polynomials <code>polynomial1</code> and <code>polynomial2
   * </code> with respect to the variable <code>var</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Resultant">Wikipedia - Resultant</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Resultant((x-y)^2-2 , y^3-5, y)
   * 17-60*x+12*x^2-10*x^3-6*x^4+x^6
   * </pre>
   */
  private static class Resultant extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // TODO allow multinomials
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isZero() || arg2.isZero()) {
        return F.C0;
      }
      IExpr arg3 = Validate.checkSymbolType(ast, 3, engine);
      if (arg3.isPresent()) {
        ISymbol x = (ISymbol) arg3;
        IExpr a = F.evalExpandAll(arg1, engine);
        IExpr b = F.evalExpandAll(arg2, engine);
        ExprPolynomialRing ring = new ExprPolynomialRing(F.List(x));
        try {
          // check if a is a polynomial otherwise check ArithmeticException, ClassCastException
          ring.create(a);
        } catch (RuntimeException ex) {
          // Polynomial expected at position `1` in `2`.
          return IOFunctions.printMessage(
              ast.topHead(), "polynomial", F.List(ast.get(1), F.C1), engine);
        }
        try {
          // check if b is a polynomial otherwise check ArithmeticException, ClassCastException
          ring.create(b);
        } catch (RuntimeException ex) {
          return IOFunctions.printMessage(
              ast.topHead(), "polynomial", F.List(ast.get(2), F.C2), engine);
        }
        IExpr resultant = resultant(a, b, x, engine);
        if (resultant.isPresent()) {
          return F.Together(resultant);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    private IExpr resultant(IExpr a, IExpr b, ISymbol x, EvalEngine engine) {
      IExpr aExp = S.Exponent.ofNIL(engine, a, x);
      IExpr bExp = S.Exponent.ofNIL(engine, b, x);
      if (aExp.isPresent() && bExp.isPresent()) {
        //        if (b.isFree(x)) {
        //          return F.Power(b, aExp);
        //        }
        //        IExpr abExp = aExp.times(bExp);
        if (S.Less.ofQ(engine, aExp, bExp)) {
          IExpr resultant = resultant(b, a, x, engine);
          if (!resultant.isPresent()) {
            return F.NIL;
          }
          return resultant;
        }

        IExpr r = jasResultant(a, b, x, engine);
        if (r.isPresent()) {
          return r;
        }
      }
      return F.NIL;
    }

    private static IExpr jasResultant(IExpr a, IExpr b, ISymbol x, EvalEngine engine) {
      VariablesSet eVar = new VariablesSet();
      eVar.addVarList(x);

      try {
        List<IExpr> varList = eVar.getVarList().copyTo();
        JASConvert<edu.jas.arith.BigInteger> jas =
            new JASConvert<edu.jas.arith.BigInteger>(varList, edu.jas.arith.BigInteger.ZERO);
        GenPolynomial<edu.jas.arith.BigInteger> p1 = jas.expr2JAS(a, false);
        GenPolynomial<edu.jas.arith.BigInteger> p2 = jas.expr2JAS(b, false);
        GreatestCommonDivisorAbstract<edu.jas.arith.BigInteger> factory =
            GCDFactory.getImplementation(edu.jas.arith.BigInteger.ZERO);
        p1 = factory.resultant(p1, p2);
        return jas.integerPoly2Expr(p1);
      } catch (ClassCastException | JASConversionException e) {
        try {
          if (eVar.size() == 0) {
            return F.NIL;
          }
          IAST vars = eVar.getVarList();
          ExprPolynomialRing ring = new ExprPolynomialRing(vars);
          ExprPolynomial pol1 = ring.create(a);
          ExprPolynomial pol2 = ring.create(b);
          List<IExpr> varList = eVar.getVarList().copyTo();
          JASIExpr jas = new JASIExpr(varList, true);
          GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
          GenPolynomial<IExpr> p2 = jas.expr2IExprJAS(pol2);

          GreatestCommonDivisor<IExpr> factory =
              GCDFactory.getImplementation(ExprRingFactory.CONST);
          p1 = factory.resultant(p1, p2);
          return jas.exprPoly2Expr(p1);
        } catch (RuntimeException rex) {
          if (Config.DEBUG) {
            rex.printStackTrace();
          }
        }
      }
      return F.NIL;
    }

    // public static IExpr resultant(IAST result, IAST resultListDiff) {
    // // create sylvester matrix
    // IAST sylvester = F.List();
    // IAST row = F.List();
    // IAST srow;
    // final int n = resultListDiff.size() - 2;
    // final int m = result.size() - 2;
    // final int n2 = m + n;
    //
    // for (int i = result.argSize(); i > 0; i--) {
    // row.add(result.get(i));
    // }
    // for (int i = 0; i < n; i++) {
    // // for each row
    // srow = F.List();
    // int j = 0;
    // while (j < n2) {
    // if (j < i) {
    // srow.add(F.C0);
    // j++;
    // } else if (i == j) {
    // for (int j2 = 1; j2 < row.size(); j2++) {
    // srow.add(row.get(j2));
    // j++;
    // }
    // } else {
    // srow.add(F.C0);
    // j++;
    // }
    // }
    // sylvester.add(srow);
    // }
    //
    // row = F.List();
    // for (int i = resultListDiff.argSize(); i > 0; i--) {
    // row.add(resultListDiff.get(i));
    // }
    // for (int i = n; i < n2; i++) {
    // // for each row
    // srow = F.List();
    // int j = 0;
    // int k = n;
    // while (j < n2) {
    // if (k < i) {
    // srow.add(F.C0);
    // j++;
    // k++;
    // } else if (i == k) {
    // for (int j2 = 1; j2 < row.size(); j2++) {
    // srow.add(row.get(j2));
    // j++;
    // k++;
    // }
    // } else {
    // srow.add(F.C0);
    // j++;
    // k++;
    // }
    // }
    // sylvester.add(srow);
    // }
    //
    // if (sylvester.isAST0()) {
    // return null;
    // }
    // // System.out.println(sylvester);
    // return F.eval(F.Det(sylvester));
    // }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
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
   * <p>gives the numerical roots of polynomial <code>poly</code>.
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
   * <p><a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>, <a
   * href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>, <a
   * href="Solve.md">Solve</a>
   */
  private static class NRoots extends AbstractFunctionEvaluator {
    /**
     * Determine the numerical roots of a univariate polynomial
     *
     * <p>See Wikipedia entries for: <a
     * href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>, <a
     * href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and <a
     * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
     *
     * @see Roots
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
        if (!arg1.isPolynomialStruct()) {
          return engine.printMessage(
              ast.topHead()
                  + ": Equal() expression expected at position 1 instead of "
                  + ast.arg1().toString());
        }
      }
      IAST variables;
      if (ast.size() == 2) {
        VariablesSet eVar = new VariablesSet(ast.arg1());
        if (!eVar.isSize(1)) {
          // factor only possible for univariate polynomials
          return engine.printMessage(
              "NRoots: factorization only possible for univariate polynomials");
        }
        variables = eVar.getVarList();
      } else {
        variables = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (!variables.isPresent()) {
          return F.NIL;
        }
      }
      if (variables.size() <= 1) {
        return F.NIL;
      }
      IExpr temp = roots(arg1, variables, engine);
      if (!temp.isList()) {
        return F.NIL;
      }
      IAST list = (IAST) temp;
      int size = list.size();
      IASTAppendable result = F.ListAlloc(size);
      return result.appendArgs(size, i -> engine.evalN(list.get(i)));
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

  /** Determine complex root intervals of a univariate polynomial */
  private static class RootIntervals extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return croots(ast.arg1(), false);
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
     *     symbolic result.
     * @return
     */
    public static IASTAppendable croots(final IExpr arg, boolean numeric) {

      try {
        VariablesSet eVar = new VariablesSet(arg);
        if (!eVar.isSize(1)) {
          // only possible for univariate polynomials
          return F.NIL;
        }
        IExpr expr = F.evalExpandAll(arg);
        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // List<IExpr> varList = r;
        List<IExpr> varList = eVar.getVarList().copyTo();

        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
        GenPolynomial<Complex<BigRational>> poly = jas.numericExpr2JAS(expr);

        Squarefree<Complex<BigRational>> engine =
            SquarefreeFactory.<Complex<BigRational>>getImplementation(cfac);
        poly = engine.squarefreePart(poly);

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
            // System.out.println("refine = " + refine);

          }
        }
        return resultList;
      } catch (InvalidBoundaryException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
      } catch (JASConversionException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
      }
      return F.NIL;
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
   * <p>determine the roots of a univariate polynomial equation with respect to the variable <code>
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
     * <p>See Wikipedia entries for: <a
     * href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation </a>, <a
     * href="http://en.wikipedia.org/wiki/Cubic_function">Cubic function</a> and <a
     * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
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
        return engine.printMessage(
            ast.topHead()
                + ": Equal() expression expected at position 1 instead of "
                + ast.arg1().toString());
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

        return engine.printMessage(
            ast.topHead()
                + ": factorization only possible for univariate polynomials at position 2 instead of "
                + ast.arg2().toString());
      }
      IAST variables = eVar.getVarList();
      IExpr variable = variables.arg1();
      IAST list = roots(arg1, false, variables, engine);
      if (list.isPresent()) {
        IASTAppendable or = F.ast(S.Or, list.size(), false);
        for (int i = 1; i < list.size(); i++) {
          or.append(F.Equal(variable, list.get(i)));
        }
        return or;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class SphericalHarmonicY extends AbstractFunctionEvaluator
      implements SphericalHarmonicYRules {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      //      int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
      //      if (degree >= 0) {
      //        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
      //          PolynomialDegreeLimitExceeded.throwIt(degree);
      //        }
      //        return PolynomialsUtils.createLegendrePolynomial(degree, ast.arg2());
      //      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION | ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }
  /**
   *
   *
   * <pre>
   * ChebyshevT(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Chebyshev polynomial of the first kind <code>T_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ChebyshevT(8, x)
   * 1-32*x^2+160*x^4-256*x^6+128*x^8
   * </pre>
   */
  private static final class ChebyshevT extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
        // (n, z) => Cos(n*ArcCos(z))
        return F.Cos(F.Times(n, F.ArcCos(z)));
      }

      int degree = n.toIntDefault(Integer.MIN_VALUE);
      if (degree != Integer.MIN_VALUE) {
        if (degree < 0) {
          degree *= -1;
        }
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }
        return PolynomialsUtils.createChebyshevPolynomial(degree, ast.arg2());
      }
      if (n.isNumEqualRational(F.C1D2) || n.isNumEqualRational(F.CN1D2)) {
        // (1/2, z) => Cos(ArcCos(z)/2)
        // (-1/2, z) => Cos(ArcCos(z)/2)
        return F.Cos(F.Times(F.C1D2, F.ArcCos(z)));
      }
      if (z.isZero()) {
        // Cos(Pi*n*(1/2))
        return F.Cos(F.Times(F.C1D2, S.Pi, n));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * ChebyshevU(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Chebyshev polynomial of the second kind <code>U_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ChebyshevU(8, x)
   * 1-40*x^2+240*x^4-448*x^6+256*x^8
   * </pre>
   */
  private static final class ChebyshevU extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (engine.isNumericMode() && n.isNumber() && z.isNumber()) {
        // Sin((n + 1)*ArcCos(z))/Sqrt(1 - z^2)
        return F.Times(
            F.Power(F.Plus(F.C1, F.Negate(F.Sqr(z))), F.CN1D2),
            F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(z))));
      }

      int degree = n.toIntDefault(Integer.MIN_VALUE);
      if (degree != Integer.MIN_VALUE) {
        if (degree < 0) {
          if (degree == (-1)) {
            return F.C0;
          }
          if (degree == (-2)) {
            return F.CN1;
          }
          return F.NIL;
        }
        if (degree == 0) {
          return F.C1;
        }
        if (degree == 1) {
          return F.Times(F.C2, z);
        }
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }
        // (n, z) => Sum(((-1)^k*(n - k)!*(2*z)^(n - 2*k))/(k!*(n - 2*k)!), {k, 0, Floor(n/2)})
        return F.sum(
            k ->
                F.Times(
                    F.Power(F.CN1, k),
                    F.Power(F.Times(F.C2, z), F.Plus(F.Times(F.CN2, k), n)),
                    F.Power(F.Times(F.Factorial(k), F.Factorial(F.Plus(F.Times(F.CN2, k), n))), -1),
                    F.Factorial(F.Plus(F.Negate(k), n))),
            0,
            degree / 2);
      }

      if (n.isNumEqualRational(F.CN1D2)) {
        // (-1/2, z) => 1/(Sqrt(2)* Sqrt(1 + z))
        return F.Times(F.C1DSqrt2, F.Power(F.Plus(F.C1, z), F.CN1D2));
      }
      if (n.isNumEqualRational(F.C1D2)) {
        // (1/2, z) => (1 + 2*z)/(Sqrt(2)* Sqrt(1 + z))
        return F.Times(
            F.C1DSqrt2, F.Plus(F.C1, F.Times(F.C2, z)), F.Power(F.Plus(F.C1, z), F.CN1D2));
      }
      if (z.isZero()) {
        // Cos((Pi*n)/2)
        return F.Cos(F.Times(F.C1D2, n, S.Pi));
      }
      if (z.isOne()) {
        return F.Plus(F.C1, n);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * BellY(n, k, {x1, x2, ... , xN})
   * </pre>
   *
   * <blockquote>
   *
   * <p>the second kind of Bell polynomials (incomplete Bell polynomials).
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Bell_polynomials">Wikipedia - Bell polynomials</a>
   * </ul>
   *
   * <pre>
   * &gt;&gt; BellY(6, 2, {x1, x2, x3, x4, x5})
   * 10*x3^2+15*x2*x4+6*x1*x5
   * </pre>
   */
  private static final class BellY extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        int[] dim = arg1.isMatrix();
        if (dim != null && arg1.isAST()) {
          if (dim[0] == 0 && dim[1] == 0) {
            return F.C0;
          }
          IAST matrixArg1 = (IAST) arg1;

          if (dim[0] == 1) {
            if (dim[1] == 1) {
              IAST row = (IAST) matrixArg1.arg1();
              return row.arg1();
            } else if (dim[1] >= 2) {
              IAST row = (IAST) matrixArg1.arg1();
              return row.apply(S.Times);
            }
          }
        }
        return F.NIL;
      }
      if (ast.isAST2()) {
        return F.NIL;
      }
      if (arg1.isInteger() && ast.arg2().isInteger()) {
        int n = arg1.toIntDefault(Integer.MIN_VALUE);
        int k = ast.arg2().toIntDefault(Integer.MIN_VALUE);
        if (n < 0
            || k < 0
            || //
            !ast.arg3().isList()
            || //
            ast.arg3().isMatrix() != null) {
          return F.NIL;
        }
        if (n == 0 && k == 0) {
          return F.C1;
        }
        if (n == 0 || k == 0) {
          return F.C0;
        }
        if (n < k) {
          return F.C0;
        }
        if (n > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(n);
        }
        int max = n - k + 2;
        if (max >= 0) {
          return bellY(n, k, (IAST) ast.arg3(), ast, engine);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * GroebnerBasis({polynomial-list},{variable-list})
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns a Gröbner basis for the <code>polynomial-list</code> and <code>variable-list</code>.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Gröbner_basis">Wikipedia - Gröbner basis</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {y, x})
   * {-2*x^2+x^3,-2*y+x*y,-x^2+2*y^2}
   *
   * &gt;&gt; GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {x, y})
   * {-2*y+y^3,-2*y+x*y,x^2-2*y^2}
   * </pre>
   */
  private static final class GroebnerBasis extends AbstractFunctionEvaluator {

    /**
     * Compute the Groebner basis for a list of polynomials.
     *
     * <p>See
     *
     * <ul>
     *   <li><a href="https://en.wikipedia.org/wiki/Gr%C3%B6bner_basis">EN-Wikipedia: Gröbner
     *       basis</a>
     *   <li><a href="https://de.wikipedia.org/wiki/Gr%C3%B6bnerbasis">DE-Wikipedia: Gröbner
     *       basis</a>
     * </ul>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() >= 3) {

        if (!ast.arg1().isList() || !ast.arg2().isList()) {
          return F.NIL;
        }
        TermOrder termOrder = TermOrderByName.Lexicographic;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
          termOrder = JASIExpr.monomialOrder(options, termOrder);
        }

        IAST polys = (IAST) ast.arg1();
        IAST vars = (IAST) ast.arg2();
        if (vars.size() <= 1) {
          return F.NIL;
        }

        return computeGroebnerBasis(polys, vars, termOrder);
      }
      return F.NIL;
    }

    /**
     * @param listOfPolynomials a list of polynomials
     * @param listOfVariables a list of variable symbols
     * @param termOrder the term order
     * @return <code>F.NIL</code> if <code>stopUnevaluatedOnPolynomialConversionError==true</code>
     *     and one of the polynomials in <code>listOfPolynomials</code> are not convertible to JAS
     *     polynomials
     */
    private static IAST computeGroebnerBasis(
        IAST listOfPolynomials, IAST listOfVariables, TermOrder termOrder) {
      List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.argSize());
      String[] pvars = new String[listOfVariables.argSize()];

      for (int i = 1; i < listOfVariables.size(); i++) {
        if (!listOfVariables.get(i).isSymbol()) {
          return F.NIL;
        }
        varList.add((ISymbol) listOfVariables.get(i));
        pvars[i - 1] = ((ISymbol) listOfVariables.get(i)).toString();
      }

      List<GenPolynomial<BigRational>> polyList =
          new ArrayList<GenPolynomial<BigRational>>(listOfPolynomials.argSize());
      JASConvert<BigRational> jas =
          new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
      for (int i = 1; i < listOfPolynomials.size(); i++) {
        IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
        try {
          GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
          polyList.add(poly);
        } catch (JASConversionException e) {
          return F.NIL;
        }
      }

      if (polyList.size() == 0) {
        return F.NIL;
      }
      GroebnerBasePartial<BigRational> gbp = new GroebnerBasePartial<BigRational>();
      OptimizedPolynomialList<BigRational> opl = gbp.partialGB(polyList, pvars);
      List<GenPolynomial<BigRational>> list = OrderedPolynomialList.sort(opl.list);
      IASTAppendable resultList = F.ListAlloc(list.size());
      for (GenPolynomial<BigRational> p : list) {
        // convert rational to integer coefficients and add
        // polynomial to result list
        resultList.append(
            jas.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(p)[2]));
      }

      return resultList;
    }
  }

  /**
   *
   *
   * <pre>
   * HermiteH(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Hermite polynomial <code>H_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Wikipedia - Hermite
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; HermiteH(8, x)
   * 1680-13440*x^2+13440*x^4-3584*x^6+256*x^8
   *
   * &gt;&gt; HermiteH(3, 1 + I)
   * -28+I*4
   * </pre>
   */
  private static final class HermiteH extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
      if (degree >= 0) {
        return PolynomialsUtils.createHermitePolynomial(degree, ast.arg2());
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
   * LaguerreL(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Laguerre polynomial <code>L_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Laguerre_polynomials">Wikipedia - Laguerre
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LaguerreL(8, x)
   * 1-8*x+14*x^2-28/3*x^3+35/12*x^4-7/15*x^5+7/180*x^6-x^7/630+x^8/40320
   * </pre>
   */
  private static final class LaguerreL extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
      if (degree != Integer.MIN_VALUE) {
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }
        if (ast.size() == 4) {
          IExpr n = ast.arg1();
          IExpr l = ast.arg2();
          IExpr z = ast.arg3();
          return laguerreLRecursive(n, degree, l, z, engine);
        }
        if (degree == 0) {
          return F.C1;
        }
        if (degree > 0) {
          IExpr z = ast.arg2();
          return PolynomialsUtils.createLaguerrePolynomial(degree, z);
        }
      }
      return F.NIL;
    }

    private IExpr laguerreLRecursive(IExpr n, int degree, IExpr l, IExpr z, EvalEngine engine) {
      if (degree == 0) {
        return F.C1;
      }
      if (degree == 1) {
        // -z + l + 1
        return F.Plus(F.C1, l, F.Negate(z));
      }
      if (degree == 2) {
        return
        // [$ (1/2)*(2 + 3*l + l^2 - 4*z - 2*l*z + z^2) $]
        F.Times(
            F.C1D2,
            F.Plus(
                F.C2,
                F.Times(F.C3, l),
                F.Sqr(l),
                F.Times(F.CN4, z),
                F.Times(F.CN2, l, z),
                F.Sqr(z))); // $$;
      }
      if (degree < 0) {
        return F.NIL;
      }
      try {
        int recursionCounter = engine.incRecursionCounter();
        int recursionLimit = engine.getRecursionLimit();
        if (recursionCounter > recursionLimit) {
          RecursionLimitExceeded.throwIt(recursionCounter, S.LaguerreL);
        }

        // Recurrence relation for LaguerreL polynomials
        // (1/n) * (((2*n + l - z - 1) )*LaguerreL(n - 1, l, z) - ((n + l - 1) )*LaguerreL(n - 2, l,
        // z))
        IExpr laguerre1 =
            laguerreLRecursive(
                F.ZZ(degree - 2), degree - 2, l, z, engine); // F.LaguerreL.of(engine,
        // F.Plus(F.CN2, n),
        // l,
        // z);
        if (laguerre1.isIndeterminate()) {
          throw new ArgumentTypeException("Indeterminate expression detected");
        }
        long leafCount1 = laguerre1.leafCount();
        if (leafCount1 > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(leafCount1);
        }
        IExpr laguerre2 =
            laguerreLRecursive(
                F.ZZ(degree - 1), degree - 1, l, z, engine); // F.LaguerreL.of(engine,
        // F.Plus(F.CN1, n),
        // l, z);
        if (laguerre2.isIndeterminate()) {
          throw new ArgumentTypeException("Indeterminate expression detected");
        }
        long leafCount2 = leafCount1 + laguerre2.leafCount();
        if (leafCount2 > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(leafCount2);
        }
        return F.Times(
            F.Power(n, F.CN1),
            F.Plus(
                F.Times(F.CN1, F.Plus(F.CN1, l, n), laguerre1),
                F.Times(F.Plus(F.CN1, l, F.Times(F.C2, n), F.Negate(z)), laguerre2)));
      } finally {
        engine.decRecursionCounter();
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * LegendreP(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Legendre polynomial <code>P_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LegendreP(4, x)
   * 3/8-15/4*x^2+35/8*x^4
   * </pre>
   */
  private static final class LegendreP extends AbstractFunctionEvaluator implements LegendrePRules {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
      if (degree >= 0) {
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }
        return PolynomialsUtils.createLegendrePolynomial(degree, ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * LegendreQ(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the Legendre functions of the second kind <code>Q_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre
   *       polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Expand(LegendreQ(4,z))
   * 55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)-15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)
   * </pre>
   */
  static final class LegendreQ extends AbstractFunctionEvaluator implements LegendreQRules {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      if (ast != null && ast.size() > 1) {
        int arg1 = ast.arg1().toIntDefault();
        if (Math.abs(arg1) > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(arg1);
        }
      }
      return ARGS_2_3;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * MonomialList(polynomial, list - of - variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>get the list of monomials of a <code>polynomial</code> expression, with respect to the
   * <code>list-of-variables</code>.
   *
   * </blockquote>
   *
   * <p>See:<br>
   *
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial</a><br>
   * </ul>
   */
  private static final class MonomialList extends AbstractFunctionEvaluator {
    /**
     * Get the list of monomials of a polynomial expression.
     *
     * <p>See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
     */
    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {
      IExpr expr = F.evalExpandAll(ast.arg1(), engine);
      VariablesSet eVar;
      IAST symbolList;
      List<IExpr> varList;
      if (ast.isAST1()) {
        // extract all variables from the polynomial expression
        eVar = new VariablesSet(ast.arg1());
        // eVar.appendToList(symbolList);
        varList = eVar.getArrayList();
        symbolList = eVar.getVarList();
      } else {
        symbolList = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (!symbolList.isPresent()) {
          return F.NIL;
        }
        varList = new ArrayList<IExpr>(symbolList.argSize());
        symbolList.forEach(x -> varList.add(x));
      }
      TermOrder termOrder = TermOrderByName.Lexicographic;

      if (ast.size() > 3) {
        if (ast.arg3().isSymbol()) {
          // String orderStr = ast.arg3().toString(); // NegativeLexicographic
          termOrder = JASIExpr.monomialOrder((ISymbol) ast.arg3(), termOrder);
        } else {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
          IExpr option = options.getOption(S.Modulus);
          if (option.isPresent()) {
            try {
              if (option.isInteger()) {
                return monomialListModulus(expr, varList, termOrder, option);
              }
            } catch (RuntimeException rex) {
              if (FEConfig.SHOW_STACKTRACE) {
                rex.printStackTrace();
              }
            }
          }
          return F.NIL;
        }
      }

      try {
        ExprPolynomialRing ring =
            new ExprPolynomialRing(symbolList, new ExprTermOrder(termOrder.getEvord()));
        ExprPolynomial poly = ring.create(expr, false, true, true);
        return poly.monomialList();
      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      // default mapping
      return F.List(expr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    /**
     * Get the monomial list of a univariate polynomial with coefficients reduced by a modulo value.
     *
     * @param polynomial a polynomial expression
     * @param variablesList list of variables
     * @param termOrder the JAS term ordering
     * @param option the &quot;Modulus&quot; option
     * @return the list of monomials of the univariate polynomial.
     */
    private static IAST monomialListModulus(
        IExpr polynomial, List<IExpr> variablesList, final TermOrder termOrder, IExpr option)
        throws JASConversionException {
      try {
        // found "Modulus" option => use ModIntegerRing
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
        JASModInteger jas = new JASModInteger(variablesList, modIntegerRing);
        GenPolynomial<ModLong> polyExpr = jas.expr2JAS(polynomial);
        IASTAppendable list = F.ListAlloc(polyExpr.length());
        for (Monomial<ModLong> monomial : polyExpr) {
          ModLong coeff = monomial.coefficient();
          ExpVector exp = monomial.exponent();
          IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
          jas.monomialToExpr(F.ZZ(coeff.getVal()), exp, monomTimes);
          list.append(monomTimes);
        }
        return list;
      } catch (ArithmeticException ae) {
        // toInt() conversion failed
        if (Config.DEBUG) {
          ae.printStackTrace();
        }
      }
      return F.NIL;
    }
  }

  public static IExpr bellY(int n, int k, IAST symbols, IAST ast, EvalEngine engine) {
    final int recursionLimit = engine.getRecursionLimit();
    try {
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(counter, ast);
        }
      }
      if (n == 0 && k == 0) {
        return F.C1;
      }
      if (n == 0 || k == 0) {
        return F.C0;
      }
      IExpr s = F.C0;
      int a = 1;
      int max = n - k + 2;

      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= max) {
        IterationLimitExceeded.throwIt(max, ast);
      }
      for (int m = 1; m < max; m++) {
        if ((m < symbols.size()) && !symbols.get(m).isZero()) {
          IExpr bellY = bellY(n - m, k - 1, symbols, ast, engine);
          if (bellY.isPlus()) {
            bellY = ((IAST) bellY).mapThread(F.Times(a, F.Slot1, symbols.get(m)), 2);
          } else {
            bellY = F.Times(a, bellY, symbols.get(m));
          }
          s = s.plus(bellY);
        }
        a = a * (n - m) / m;
      }
      return s;
    } finally {
      if (recursionLimit > 0) {
        engine.decRecursionCounter();
      }
    }
  }

  /**
   * Get the coefficient list of a univariate polynomial.
   *
   * @param polynomial
   * @param variable
   * @return <code>null</code> if the list couldn't be evaluated.
   */
  private static double[] coefficients(IExpr polynomial, final ISymbol variable)
      throws JASConversionException {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(F.List(variable));
      ExprPolynomial poly = ring.create(polynomial);

      IAST list = poly.coefficientList();
      int degree = list.size() - 2;
      double[] result = new double[degree + 1];
      for (int i = 1; i < list.size(); i++) {
        ISignedNumber temp = list.get(i).evalReal();
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

  public static IAST coefficientList(IExpr expr, IAST listOfVariables) {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(listOfVariables);
      ExprPolynomial poly = ring.create(expr, true, false, true);
      if (poly.isZero()) {
        return F.List();
      }
      return poly.coefficientList();
    } catch (LimitException le) {
      throw le;
    } catch (ClassCastException ex) {
      // org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing.create()
      if (FEConfig.SHOW_STACKTRACE) {
        ex.printStackTrace();
      }
    } catch (RuntimeException ex) {
      if (FEConfig.SHOW_STACKTRACE) {
        ex.printStackTrace();
      }
    }
    if (listOfVariables.argSize() > 0) {
      return F.Nest(S.List, expr, listOfVariables.argSize());
    }
    return F.NIL;
  }

  public static IAST roots(final IExpr arg1, IAST variables, EvalEngine engine) {
    if (variables.size() != 2) {
      // factor only possible for univariate polynomials
      return engine.printMessage("NRoots: factorization only possible for univariate polynomials");
    }
    IExpr expr = evalExpandAll(arg1, engine);

    IExpr variable = variables.arg1();
    double[] coefficients = Expr2Object.toPolynomial(expr, variable);
    if (coefficients != null) {
      try {
        LaguerreSolver solver = new LaguerreSolver(Config.DEFAULT_ROOTS_CHOP_DELTA);
        org.hipparchus.complex.Complex[] roots = solver.solveAllComplex(coefficients, 0);
        return Object2Expr.convertComplex(true, roots);
      } catch (org.hipparchus.exception.MathRuntimeException mrex) {
        if (FEConfig.SHOW_STACKTRACE) {
          mrex.printStackTrace();
        }
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

    IASTAppendable resultList = RootIntervals.croots(expr, true);
    if (resultList.isPresent()) {
      // IAST result = F.List();
      // if (resultList.size() > 0) {
      // result.appendArgs(resultList);
      // }
      // return result;
      return resultList;
    }
    return F.NIL;
  }

  protected static IAST roots(
      final IExpr arg1, boolean numericSolutions, IAST variables, EvalEngine engine) {

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
    return rootsOfVariable(expr, denom, variables, numericSolutions, engine);
  }

  /**
   * Given a set of polynomial coefficients, compute the roots of the polynomial. Depending on the
   * polynomial being considered the roots may contain complex number. When complex numbers are
   * present they will come in pairs of complex conjugates.
   *
   * @param coefficients coefficients of the polynomial.
   * @return the roots of the polynomial
   * @throws RuntimeException
   */
  private static IAST findRoots(double... coefficients) {
    int N = coefficients.length - 1;

    // Construct the companion matrix
    RealMatrix c = new Array2DRowRealMatrix(N, N);

    double a = coefficients[N];
    for (int i = 0; i < N; i++) {
      c.setEntry(i, N - 1, -coefficients[i] / a);
    }
    for (int i = 1; i < N; i++) {
      c.setEntry(i, i - 1, 1);
    }

    EigenDecomposition ed = new EigenDecomposition(c);

    double[] realValues = ed.getRealEigenvalues();
    double[] imagValues = ed.getImagEigenvalues();

    IASTAppendable roots = F.ListAlloc(N);
    return roots.appendArgs(
        0,
        N,
        i ->
            F.chopExpr(
                F.complexNum(realValues[i], imagValues[i]), Config.DEFAULT_ROOTS_CHOP_DELTA));
    // for (int i = 0; i < N; i++) {
    // roots.append(F.chopExpr(F.complexNum(realValues[i], imagValues[i]),
    // Config.DEFAULT_ROOTS_CHOP_DELTA));
    // }
    // return roots;

  }

  public static IASTMutable rootsOfExprPolynomial(
      final IExpr expr, IAST varList, boolean rootsOfQuartic) {
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
      if (FEConfig.SHOW_STACKTRACE) {
        e2.printStackTrace();
      }
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
      if (FEConfig.SHOW_STACKTRACE) {
        e2.printStackTrace();
      }
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
      IASTAppendable result = F.ListAlloc(n);
      for (int k = 0; k < n; k++) {
        IAST argCosSin = F.Times(fraction, F.Plus(theta, F.Times(F.ZZ(k + k), S.Pi)));
        IAST f2 = F.Plus(F.Cos(argCosSin), F.Times(F.CI, F.Sin(argCosSin)));
        result.append(F.Times(f1, f2));
      }
      return result;
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
    IExpr rhsDenominator = a; // EvalEngine.get().evaluate(F.Divide(F.C1, a));
    if ((varDegree & 0x0001) == 0x0001) {
      // odd
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IASTMutable temp =
            ((IAST) rhsNumerator).mapThread(F.Power(F.Slot1, F.fraction(1, varDegree)), 1);
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
        zNumerator = EvalEngine.get().evaluate(F.Power(rhsNumerator, F.fraction(1, varDegree)));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        IASTMutable temp =
            ((IAST) rhsDenominator).mapThread(F.Power(F.Slot1, F.fraction(-1, varDegree)), 1);
        if (rhsDenominator.first().isNegative()) {
          isNegative = !isNegative;
          temp.set(1, rhsDenominator.first().negate());
        }
        zDenominator = EvalEngine.get().evaluate(temp);
      } else {
        if (rhsDenominator.isNegative()) {
          isNegative = !isNegative;
          rhsDenominator = rhsDenominator.negate();
        }
        zDenominator =
            EvalEngine.get().evaluate(F.Power(rhsDenominator, F.fraction(-1, varDegree)));
      }
      IASTAppendable result = F.ListAlloc(varDegree);
      for (int i = 0; i < varDegree; i++) {
        if (isNegative) {
          result.append(
              F.Times(
                  F.Power(F.CN1, i + 1),
                  F.Power(F.CN1, F.fraction(i, varDegree)),
                  zNumerator,
                  zDenominator));
        } else {
          result.append(
              F.Times(
                  F.Power(F.CN1, i),
                  F.Power(F.CN1, F.fraction(i, varDegree)),
                  zNumerator,
                  zDenominator));
        }
      }
      //      IExpr temp = EvalEngine.get().evaluate(result);
      //      System.out.println(temp.toString());
      return result;
    } else {
      // even
      IExpr zNumerator;
      if (rhsNumerator.isTimes()) {
        IExpr temp = ((IAST) rhsNumerator).mapThread(F.Power(F.Slot1, F.fraction(1, varDegree)), 1);
        zNumerator = EvalEngine.get().evaluate(temp);
      } else {
        zNumerator = EvalEngine.get().evaluate(F.Power(rhsNumerator, F.fraction(1, varDegree)));
      }
      IExpr zDenominator;
      if (rhsDenominator.isTimes()) {
        IExpr temp =
            ((IAST) rhsDenominator).mapThread(F.Power(F.Slot1, F.fraction(-1, varDegree)), 1);
        zDenominator = EvalEngine.get().evaluate(temp);
      } else {
        zDenominator =
            EvalEngine.get().evaluate(F.Power(rhsDenominator, F.fraction(-1, varDegree)));
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
      // IExpr temp = EvalEngine.get().evaluate(result);
      // System.out.println(temp.toString());
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
  public static IAST rootsOfVariable(
      final IExpr expr,
      final IExpr denominator,
      final IAST variables,
      boolean numericSolutions,
      EvalEngine engine) {
    IASTMutable result = F.NIL;
    // ASTRange r = new ASTRange(variables, 1);
    // List<IExpr> varList = r;
    List<IExpr> varList = variables.copyTo();
    try {
      IExpr temp;
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
      for (int i = 1; i < factorRational.size(); i++) {
        temp = F.evalExpand(factorRational.get(i));
        IAST quarticResultList = QuarticSolver.solve(temp, variables.arg1());
        if (quarticResultList.isPresent()) {
          for (int j = 1; j < quarticResultList.size(); j++) {
            if (numericSolutions) {
              newResult.append(
                  F.chopExpr(
                      engine.evalN(quarticResultList.get(j)), Config.DEFAULT_ROOTS_CHOP_DELTA));
            } else {
              newResult.append(quarticResultList.get(j));
            }
          }
        } else {
          polyRat = jas.expr2JAS(temp, numericSolutions);
          IAST factorComplex = Algebra.factorRational(polyRat, jas, S.List);
          for (int k = 1; k < factorComplex.size(); k++) {
            temp = F.evalExpand(factorComplex.get(k));
            quarticResultList = QuarticSolver.solve(temp, variables.arg1());
            if (quarticResultList.isPresent()) {
              for (int j = 1; j < quarticResultList.size(); j++) {
                if (numericSolutions) {
                  newResult.append(
                      F.chopExpr(
                          engine.evalN(quarticResultList.get(j)), Config.DEFAULT_ROOTS_CHOP_DELTA));
                } else {
                  newResult.append(quarticResultList.get(j));
                }
              }
            } else {
              double[] coefficients =
                  PolynomialFunctions.coefficients(temp, (ISymbol) variables.arg1());
              if (coefficients == null) {
                return F.NIL;
              }
              IAST resultList = findRoots(coefficients);
              // IAST resultList = RootIntervals.croots(temp,
              // true);
              if (resultList.size() > 0) {
                newResult.appendArgs(resultList);
              }
            }
          }
        }
      }
      newResult = QuarticSolver.createSet(newResult);
      return newResult;
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
            if (!appendable.isPresent()) {
              appendable = result.removeAtClone(i);
              continue;
            }
            appendable.remove(i);
            continue;
          }
          i++;
        }
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Used in <code>Solve()</code> function to reduce the polynomial list of equations.
   *
   * @param listOfPolynomials a list of polynomials
   * @param listOfVariables a list of variable symbols
   * @return <code>F.NIL</code> if <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
   *     one of the polynomials in <code>listOfPolynomials</code> are not convertible to JAS
   *     polynomials
   */
  public static IASTAppendable solveGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables) {
    List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.argSize());
    for (int i = 1; i < listOfVariables.size(); i++) {
      if (!listOfVariables.get(i).isSymbol()) {
        return F.NIL;
      }
      varList.add((ISymbol) listOfVariables.get(i));
    }

    List<GenPolynomial<BigRational>> polyList =
        new ArrayList<GenPolynomial<BigRational>>(listOfPolynomials.argSize());
    TermOrder termOrder = TermOrderByName.IGRLEX;
    JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
    IASTAppendable rest = F.ListAlloc(8);
    for (int i = 1; i < listOfPolynomials.size(); i++) {
      IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
      try {
        GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
        polyList.add(poly);
      } catch (JASConversionException e) {
        rest.append(expr);
      }
    }

    if (polyList.size() == 0) {
      return F.NIL;
    }
    GroebnerBaseAbstract<BigRational> engine =
        GBAlgorithmBuilder.<BigRational>polynomialRing(jas.getPolynomialRingFactory())
            .fractionFree()
            .syzygyPairlist()
            .build();
    List<GenPolynomial<BigRational>> opl = engine.GB(polyList);
    IASTAppendable resultList = F.ListAlloc(opl.size() + rest.size());
    // convert rational to integer coefficients and add
    // polynomial to result list
    for (GenPolynomial<BigRational> p : opl) {
      resultList.append(
          jas.integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(p)[2]));
    }
    resultList.appendArgs(rest);
    return resultList;
  }

  public static void initialize() {
    Initializer.init();
  }

  private PolynomialFunctions() {}
}
