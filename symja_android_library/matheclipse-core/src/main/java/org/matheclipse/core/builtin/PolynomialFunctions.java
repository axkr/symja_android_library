package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
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
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.IMatch;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.polynomials.PolynomialsUtils;
import org.matheclipse.core.polynomials.longexponent.ExpVectorLong;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.polynomials.longexponent.ExprTermOrder;
import org.matheclipse.core.polynomials.symbolicexponent.ExpVectorSymbolic;
import org.matheclipse.core.polynomials.symbolicexponent.SymbolicPolynomial;
import org.matheclipse.core.polynomials.symbolicexponent.SymbolicPolynomialRing;
import com.google.common.math.LongMath;
import edu.jas.application.GBAlgorithmBuilder;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;

public class PolynomialFunctions {

  // public static final Logger LOGGER = LogManager.getLogger(PolynomialFunctions.class);

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
      // TODO
      // S.CoefficientArrays.setEvaluator(new CoefficientArrays());
      S.CoefficientList.setEvaluator(new CoefficientList());
      S.CoefficientRules.setEvaluator(new CoefficientRules());
      S.Cyclotomic.setEvaluator(new Cyclotomic());
      S.Discriminant.setEvaluator(new Discriminant());
      S.Exponent.setEvaluator(new Exponent());
      S.GroebnerBasis.setEvaluator(new GroebnerBasis());
      S.HermiteH.setEvaluator(new HermiteH());
      S.JacobiP.setEvaluator(new JacobiP());
      S.LaguerreL.setEvaluator(new LaguerreL());
      S.LegendreP.setEvaluator(new LegendreP());
      S.LegendreQ.setEvaluator(new LegendreQ());
      S.MonomialList.setEvaluator(new MonomialList());
      S.Resultant.setEvaluator(new Resultant());
      S.SphericalHarmonicY.setEvaluator(new SphericalHarmonicY());
      S.ZernikeR.setEvaluator(new ZernikeR());
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
   * <p>
   * get the coefficient of <code>variable^exponent</code> in <code>polynomial</code>.
   * 
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia - Coefficient Coefficient</a>
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
      IExpr cached = engine.getCache(ast);
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
      // if (a1.isPowerInteger()) {
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
        IAST subst =
            Algebra.substituteVariablesInPolynomial(expr, listOfVariables, "§Coefficient", false);
        if (subst.isPresent()) {
          expr = subst.arg1();
          listOfVariables = (IASTAppendable) subst.arg2();
          SymbolicPolynomialRing ring =
              new SymbolicPolynomialRing(ExprRingFactory.CONST, listOfVariables);
          SymbolicPolynomial poly = ring.create(expr, true, false, false);
          IExpr temp = poly.coefficient(expArr);
          engine.putCache(ast, temp);
          return temp;
        }
        return F.NIL;
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException ae) {
        Errors.rethrowsInterruptException(ae);
        // LOGGER.debug("Coefficient.evaluate() failed", ae);
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

  /**
   * TODO currently not implemented
   *
   * @deprecated
   */
  @Deprecated
  private static class CoefficientArrays extends AbstractFunctionEvaluator {

    /**
     * TODO currently not implemented
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {

      IExpr expr = F.evalExpandAll(ast.arg1(), engine);
      VariablesSet eVar;
      IAST symbolList;

      if (ast.isAST1()) {
        // extract all variables from the polynomial expression
        eVar = new VariablesSet(ast.arg1());
        symbolList = eVar.getVarList();
      } else {
        symbolList = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (symbolList.isNIL()) {
          return F.NIL;
        }
        List<IExpr> varList = new ArrayList<IExpr>(symbolList.argSize());
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
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.CoefficientArrays, rex, engine);
        // LOGGER.debug("CoefficientArrays.evaluate() failed", rex);
      }

      return F.list(F.Rule(F.mapRange(1, symbolList.size(), i -> F.C0), expr));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /**
   * <pre>
   * <code>CoefficientList(polynomial, variable)
   * </code>
   * </pre>
   * 
   * <p>
   * get the coefficient list of a <code>polynomial</code>.
   * </p>
   * 
   * <p>
   * See:
   * </p>
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia - Coefficient</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; CoefficientList(a+b*x, x)
   * {a,b}
   * 
   * &gt;&gt; CoefficientList(a+b*x+c*x^2, x)
   * {a,b,c}
   * 
   * &gt;&gt; CoefficientList(a+c*x^2, x)
   * {a,0,c}
   * 
   * &gt;&gt; CoefficientList((x + y)^3, z)
   * {(x+y)^3}
   *         
   * &gt;&gt; CoefficientList(Series(2*x, {x, 0, 9}), x)
   * {0,2}
   * </code>
   * </pre>
   * <p>
   * In the next line <code>Coefficient</code> returns the coefficient of a particular term of a
   * polynomial. In this case <code>(-210*c^2 * x^2*y*z^2)</code> is a term of
   * <code>(c*x-2*y+z)^7</code> after it's expanded.
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; poly=(c*x-2*y+z)^7
   * (c*x-2*y+z)^7
   * 
   * &gt;&gt; Coefficient(poly, x^2*y*z^4)
   * -210*c^2
   * </code>
   * </pre>
   * <p>
   * <code>CoefficientList</code> gets the same information as a list of coefficients. In the line
   * below <code>Part(coeff, 3,2,5)</code> returns the coefficient of
   * <code>x^(3-1)*y^(2-1)*z^(5-1)</code>. In general if we say
   * <code>lst=CoefficientList(poly,{x1,x2,x3,...})</code> then
   * <code>Part(lst,  n1, n2, ,n3, ...)</code> will be the coefficient of
   * <code>x1^(n1-1)*x2^(n2-1)*x3^(n3-1)...</code>.
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; coeff=CoefficientList(poly,{x,y,z}); Part(coeff, 3,2,5)
   * -210*c^2
   * </code>
   * </pre>
   * <p>
   * The next line gives the coefficient of <code>x^5</code>. As expected there is more than one
   * term of poly with <code>x^5</code> as a factor.
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; Coefficient(poly, x^5)
   * 84*c^5*y^2-84*c^5*y*z+21*c^5*z^2
   * </code>
   * </pre>
   * <p>
   * We can get the same result as the previous example if we use the next line.
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; Coefficient(poly, x, 5)
   * 84*c^5*y^2-84*c^5*y*z+21*c^5*z^2
   * </code>
   * </pre>
   * <p>
   * One can't get the result above directly from CoefficientList. Instead pieces of the above
   * result are included in the result of <code>CoefficientList(poly,{x,y,z})</code>. The line below
   * can be used to get pieces of the result above. Specifically <code>coeff[[6]]</code> contains
   * all coefficients of x^5 (including those that are zero).
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; coeff[[6]]
   * {{0,0,21*c^5,0,0,0,0,0},{0,-84*c^5,0,0,0,0,0,0},{84*c^5,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}}
   * </code>
   * </pre>
   * <ul>
   * <li><code>Part(coeff,6,1,3)</code> is <code>21*c^5</code> the coefficient of
   * <code>(x^(6 - 1) * y^(1 - 1) * z^(3 - 1)) = (x^5 * z^2)</code> in poly.</li>
   * <li><code>Part(coeff,6,2,2)</code> is <code>(-84*c^5)</code> the coefficient of
   * <code>(x^(6 - 1) * y^(2 - 1) * z^(2 - 1)) = (x^5 * y * z)</code> in poly.</li>
   * <li><code>Part(coeff,6,3,1)</code> is <code>(84*c^5)</code> the coeficient of
   * <code>(x^(6 - 1) * y^(3 - 1) * z^(1 - 1)) = (x^5 * y^2)</code> in poly.</li>
   * </ul>
   * <p>
   * All other coefficients under <code>coeff[[6]]</code> are zero which agrees with the result of
   * <code>Coefficient(poly, x^5)</code>.
   * </p>
   * 
   */
  private static class CoefficientList extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isList()) {
        return arg1.mapThread(ast, 1);
      }
      IAST list = ast.arg2().makeList();
      if (arg1 instanceof ASTSeriesData && list.argSize() == 1) {
        ASTSeriesData series = (ASTSeriesData) arg1;
        if (series.getX().equals(list.arg1())) {
          return series.coefficientList();
        }
      }

      IExpr expr = arg1.normal(false);
      if (expr.isAST() && !expr.isFree(x -> list.indexOf(x) > 0, true)) {
        expr = F.evalExpandAll(expr, engine);
        // expr = engine.evaluate(expr.normal(false));
      }

      return ASTSeriesData.coefficientList(expr, list);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Get exponent vectors and coefficients of monomials of a polynomial expression.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
   */
  private static class CoefficientRules extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isList()) {
        return arg1.mapThread(ast, 1);
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
        if (symbolList.isNIL()) {
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
          if (option.isInteger()) {
            try {
              return coefficientRulesModulus(expr, varList, termOrder, option);
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.CoefficientRules, rex, engine);
              // toInt() conversion failed
              // LOGGER.debug("CoefficientRules.evaluate() failed", rex);
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
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.CoefficientRules, rex, engine);
        // LOGGER.debug("CoefficientRules.evaluate() failed", rex);
      }
      // default mapping
      IASTAppendable ruleList = F.mapRange(1, symbolList.size(), i -> F.C0);
      return F.list(F.Rule(ruleList, expr));
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
    private static IAST coefficientRulesModulus(IExpr polynomial, List<IExpr> variablesList,
        final TermOrder termOrder, IExpr option) throws JASConversionException {
      try {
        // found "Modulus" option => use ModIntegerRing
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
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
        Errors.printMessage(S.CoefficientRules, ae);
        // LOGGER.debug("CoefficientRules.coefficientRulesModulus() failed", ae);
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
   * <p>
   * returns the Cyclotomic polynomial <code>C_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Cyclotomic_polynomial">Wikipedia - Cyclotomic
   * polynomial</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Cyclotomic(25,x)
   * 1+x^5+x^10+x^15+x^20
   * </pre>
   *
   * <p>
   * The case of the 105-th cyclotomic polynomial is interesting because 105 is the lowest integer
   * that is the product of three distinct odd prime numbers and this polynomial is the first one
   * that has a coefficient other than <code>1, 0</code> or <code>−1</code>:
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
        Errors.printMessage(ast.topHead(), "intnm", F.list(F.C1, ast), engine);
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
          return F.sumRational(i -> ((IRational) x).powerRational(i), 0, n - 1);
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
      return F.Together(F.intIterator(S.Times, d -> F.Power(F.Plus(F.C1, F.Negate(F.Power(x, d))),
          F.MoebiusMu(F.Times(F.Power(d, -1), ni))), divisorList));
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
   * <p>
   * computes the discriminant of the polynomial <code>poly</code> with respect to the variable
   * <code>var</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Discriminant">Wikipedia - Discriminant</a>
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
    private static final IExpr CUBIC = F.Plus(F.Times(F.Sqr(F.b), F.Sqr(F.c)),
        F.Times(F.CN4, F.a, F.Power(F.c, F.C3)), F.Times(F.CN4, F.Power(F.b, F.C3), F.d),
        F.Times(F.ZZ(18L), F.a, F.b, F.c, F.d), F.Times(F.ZZ(-27L), F.Sqr(F.a), F.Sqr(F.d)));

    // Page 405
    // http://books.google.com/books?id=-gGzjSnNnR0C&lpg=PA402&vq=quartic&hl=de&pg=PA405#v=snippet&q=quartic&f=false

    // b^2*c^2*d^2 - 4*a*c^3*d^2 - 4*b^3*d^3 + 18*a*b*c*d^3 - 27*a^2*d^4 -
    // 4*b^2*c^3*e + 16*a*c^4*e + 18*b^3*c*d*e - 80*a*b*c^2*d*e - 6*a*b^2*d^2*e +
    // 144*a^2*c*d^2*e - 27*b^4*e^2 + 144*a*b^2*c*e^2 - 128*a^2*c^2*e^2 -
    // 192*a^2*b*d*e^2 + 256*a^3*e^3
    private static final IExpr QUARTIC = F.Plus(F.Times(F.Sqr(F.b), F.Sqr(F.c), F.Sqr(F.d)),
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
        F.Plus(F.Times(F.Sqr(F.b), F.Sqr(F.c), F.Sqr(F.d), F.Sqr(F.e)),
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
        IAST univariateVariables = F.list(arg2);
        ExprPolynomialRing ring = new ExprPolynomialRing(univariateVariables);
        ExprPolynomial poly = ring.create(expr);

        long n = poly.degree();
        if (n >= 2L && n <= 5L) {
          IAST result = poly.coefficientList();
          IASTAppendable rules = F.mapList(result, (arg, i) -> F.Rule(vars[i - 1], arg));
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
        return F.Divide(F.Times(F.Power(F.CN1, (n * (n - 1) / 2)),
            F.Resultant(poly.getExpr(), polyDiff.getExpr(), arg2)), fN);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Discriminant, rex, engine);
        // LOGGER.log(engine.getLogLevel(), "{}: polynomial expected at position 1 instead of {}",
        // ast.topHead(), ast.arg1());
        return F.NIL;
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
   * <p>
   * gives the maximum power with which <code>x</code> appears in the expanded form of <code>
   * polynomial</code>.
   *
   * </p>
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
      IExpr cached = engine.getCache(ast);
      if (cached != null) {
        return cached;
      }

      IExpr form = engine.evalPattern(ast.arg2());
      if (form.isList()) {
        return form.mapThread(ast, 2);
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
      // if (expr.isNIL()) {
      // expr = a1;
      // }
      // }
      IExpr expr = F.evalExpandAll(ast.arg1(), engine).normal(false);
      IAST subst = Algebra.substituteVariablesInPolynomial(expr, F.list(form), "§Exponent", false);
      if (subst.isPresent()) {
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
        IASTAppendable result = F.ast(sym, collector);
        engine.putCache(ast, result);
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    private static IExpr powerExponent(IAST powerAST, final IExpr form,
        final IPatternMatcher matcher, EvalEngine engine) {
      if (matcher.test(powerAST.base(), engine)) {
        return powerAST.exponent();
      }
      if (matcher.isRuleWithoutPatterns() && form.isPower() && form.base().equals(powerAST.base())
          && form.exponent().isRational()) {
        return form.exponent().reciprocal().times(powerAST.exponent());
      }
      return F.C0;
    }

    private static void timesExponent(IAST timesAST, IExpr form, final IPatternMatcher matcher,
        Set<IExpr> collector, EvalEngine engine) {
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
   * <p>
   * computes the resultant of the polynomials <code>polynomial1</code> and <code>polynomial2
   * </code> with respect to the variable <code>var</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Resultant">Wikipedia - Resultant</a>
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
      IExpr arg3 = Validate.checkIsVariable(ast, 3, engine);
      if (arg3.isPresent()) {
        IExpr x = arg3;
        IExpr a = F.evalExpandAll(arg1, engine);
        IExpr b = F.evalExpandAll(arg2, engine);
        ExprPolynomialRing ring = new ExprPolynomialRing(F.list(x));
        try {
          // check if a is a polynomial otherwise check ArithmeticException, ClassCastException
          ring.create(a);
        } catch (RuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
          // Polynomial expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "polynomial", F.list(ast.get(1), F.C1), engine);
        }
        try {
          // check if b is a polynomial otherwise check ArithmeticException, ClassCastException
          ring.create(b);
          IExpr resultant = resultant(a, b, x, engine);
          if (resultant.isPresent()) {
            return F.Together(resultant);
          }
        } catch (RuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
          // Polynomial expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "polynomial", F.list(ast.get(2), F.C2), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    private IExpr resultant(IExpr a, IExpr b, IExpr x, EvalEngine engine) {
      IExpr aExp = S.Exponent.ofNIL(engine, a, x);
      IExpr bExp = S.Exponent.ofNIL(engine, b, x);
      if (aExp.isPresent() && bExp.isPresent()) {
        // if (b.isFree(x)) {
        // return F.Power(b, aExp);
        // }
        // IExpr abExp = aExp.times(bExp);
        if (S.Less.ofQ(engine, aExp, bExp)) {
          IExpr resultant = resultant(b, a, x, engine);
          if (resultant.isNIL()) {
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

    /** Use JAS to compute the resultant. */
    private static IExpr jasResultant(IExpr a, IExpr b, IExpr x, EvalEngine engine) {
      VariablesSet eVar = new VariablesSet();
      eVar.addVarList(x);

      try {
        List<IExpr> varList = eVar.getVarList().copyTo();
        JASConvert<edu.jas.arith.BigInteger> jas =
            new JASConvert<edu.jas.arith.BigInteger>(varList, edu.jas.arith.BigInteger.ZERO);
        GenPolynomial<edu.jas.arith.BigInteger> p1 = jas.expr2JAS(a, false);
        if (p1 == null) {
          return resultantExpr(a, b, eVar);
        }
        GenPolynomial<edu.jas.arith.BigInteger> p2 = jas.expr2JAS(b, false);
        if (p2 == null) {
          return resultantExpr(a, b, eVar);
        }
        GreatestCommonDivisorAbstract<edu.jas.arith.BigInteger> factory =
            GCDFactory.getImplementation(edu.jas.arith.BigInteger.ZERO);
        p1 = factory.resultant(p1, p2);
        return jas.integerPoly2Expr(p1);
      } catch (ClassCastException | JASConversionException e) {
        return resultantExpr(a, b, eVar);
      }
    }

    private static IExpr resultantExpr(IExpr a, IExpr b, VariablesSet eVar) {
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

        GreatestCommonDivisor<IExpr> factory = GCDFactory.getImplementation(ExprRingFactory.CONST);
        p1 = factory.resultant(p1, p2);
        return jas.exprPoly2Expr(p1);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Resultant, rex);
        // LOGGER.debug("Resultant.jasResultant() failed", rex);
      }
      return F.NIL;
    }

    // public static IExpr resultant(IAST result, IAST resultListDiff) {
    // // create sylvester matrix
    // IAST sylvester = F.list();
    // IAST row = F.list();
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
    // srow = F.list();
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
    // row = F.list();
    // for (int i = resultListDiff.argSize(); i > 0; i--) {
    // row.add(resultListDiff.get(i));
    // }
    // for (int i = n; i < n2; i++) {
    // // for each row
    // srow = F.list();
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
    // return F.eval(F.Det(sylvester));
    // }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }



  private static final class SphericalHarmonicY extends AbstractFunctionEvaluator
      implements IMatch {
    @Override
    public IExpr match5(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return SphericalHarmonicYRules.match5(ast, engine);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr l = ast.arg1();
      IExpr m = ast.arg2();
      IExpr t = ast.arg3();
      IExpr p = ast.arg4();

      IExpr temp = integerArgsSphericalHarmonicY(l, m, t, p, engine);
      if (temp.isPresent()) {
        return temp;
      }

      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 4) {
        IInexactNumber l = (IInexactNumber) ast.arg1();
        IInexactNumber m = (IInexactNumber) ast.arg2();
        IInexactNumber t = (IInexactNumber) ast.arg3();
        IInexactNumber p = (IInexactNumber) ast.arg4();
        IExpr temp = integerArgsSphericalHarmonicY(l, m, t, p, engine);
        if (temp.isPresent()) {
          return temp;
        }
        if (m.isZero()) {
          // (Sqrt(1+2*l)*LegendreP(l,Cos(t)))/(2*Sqrt(Pi))
          return F.Times(F.Sqrt(F.Plus(F.C1, F.Times(F.C2, l))),
              F.Power(F.Times(F.C2, F.CSqrtPi), F.CN1), F.LegendreP(l, F.Cos(t)));
        }
        if (m.isOne()) {
          // (-E^(I*p)*l*(1+l)*Sqrt(1+2*l)*Sqrt(Gamma(l))*Hypergeometric2F1(1-l,2+l,2,Sin(t/2)^2)*Sin(t))/(4*Sqrt(Pi)*Sqrt(Gamma(2+l)))
          INumber v1 = F.C2.plus(l);
          INumber v2 = F.C1.subtract(l);
          IAST hypergeometric2f1 =
              F.Hypergeometric2F1(v2, v1, F.C2, F.Sqr(F.Sin(F.Times(F.C1D2, t))));
          IAST sphericalHarmonicY = F.Times(F.C1D4, F.Exp(F.Times(F.CI, p)), F.CN1.subtract(l), l,
              F.Sqrt(F.Plus(F.C1, F.Times(F.C2, l))), F.Power(F.Pi, F.CN1D2), F.Sqrt(F.Gamma(l)),
              F.Power(F.Gamma(v1), F.CN1D2), hypergeometric2f1, F.Sin(t));
          return engine.evaluate(sphericalHarmonicY);
        }

        // (E^(I*m*p)*Sqrt(1+2*l)*Sqrt(Gamma(1+l-m))*Hypergeometric2F1(-l,1+l,1-m,Sin(t/2)^2)*Sin(t)^m)/((1-Cos(t))^m*2*Sqrt(Pi)*Gamma(1-m)*Sqrt(Gamma(1+l+m)))
        INumber v1 = F.C1.subtract(m);
        INumber v2 = m.negate();
        IAST hypergeometric2f1 =
            F.Hypergeometric2F1(l.negate(), F.C1.plus(l), v1, F.Sqr(F.Sin(F.Times(F.C1D2, t))));
        IAST sphericalHarmonicY =
            F.Times(F.C1D2, F.Exp(F.Times(F.CI, m, p)), F.Sqrt(F.Plus(F.C1, F.Times(F.C2, l))),
                F.Power(F.Pi, F.CN1D2), F.Power(F.Subtract(F.C1, F.Cos(t)), v2),
                F.Power(F.Gamma(F.Plus(F.C1, l, m)), F.CN1D2), F.Power(F.Gamma(v1), F.CN1),
                F.Sqrt(F.Gamma(F.Plus(l, v1))), hypergeometric2f1, F.Power(F.Sin(t), m));
        return engine.evaluate(sphericalHarmonicY);

      }
      return F.NIL;
    }

    /**
     * Check if <code>l</code> and <code>m</code> are mathematical integers and call {@link S#Sum}
     * formula for evaluation.
     * 
     * @param l
     * @param m
     * @param t
     * @param p
     * @return
     */
    private IExpr integerArgsSphericalHarmonicY(IExpr l, IExpr m, IExpr t, IExpr p,
        EvalEngine engine) {
      if (l.isMathematicalIntegerNegative()) {
        l = l.negate().subtract(F.C1);
      }

      final IExpr ll = l;
      final boolean conjugate;
      if (m.isMathematicalIntegerNegative()) {
        m = m.negate();
        if (m.greater(ll).isTrue()) {
          return F.C0;
        }
        conjugate = true;
      } else {
        conjugate = false;
      }
      if (ll.isMathematicalIntegerNonNegative() && m.isMathematicalIntegerNonNegative()) {
        int n = ll.toIntDefault();
        if (n >= 0) {
          n /= 2;
          if (m.lessEqual(ll).isTrue()) {
            // ((-1)^m/2^(1-l)*E^(I*p*m)*Sqrt(((2*l+1)*(l-m)!)/(l+m)!)*(Sin(t)^2)^(m/2)*Sum(((-
            // 1)^k*Gamma(-k+l+1/2))/(Cos(t)^(2*k-l)*2^(2*k)*k!*Gamma(-2*k+l-m+1)),{k,0,Floor(l/
            // 2)}))/(Pi*Cos(t)^m)
            IExpr plusSubexpr = ll.plus(m.negate()).plus(F.C1);
            IExpr cosT = F.Cos(t);
            IExpr sum = F.sum(
                k -> F.Times(F.Power(cosT, F.Plus(F.Times(F.CN2, k), ll)), F.Power(F.CN1, k),
                    F.Gamma(F.Plus(k.negate(), ll, F.C1D2)),
                    F.Power(F.Times(F.Power(F.C2, F.Times(F.C2, k)), F.Factorial(k),
                        F.Gamma(F.Plus(F.Times(F.CN2, k), plusSubexpr))), F.CN1)), //
                0, n);
            IAST exp = conjugate ? F.Exp(F.Times(F.CNI, p, m)) : F.Exp(F.Times(F.CI, p, m));
            IExpr condonShortlyPhase = engine.evaluate(F.Power(F.CN1, m));
            IAST sphericalHarmonicY = F.Times(condonShortlyPhase, F.Power(F.C2, F.Plus(F.CN1, ll)),
                exp, F.Power(F.Times(F.Pi, F.Power(cosT, m)), F.CN1),
                F.Sqrt(F.Times(F.Plus(F.Times(F.C2, ll), F.C1), F.Factorial(F.Subtract(ll, m)),
                    F.Power(F.Factorial(F.Plus(ll, m)), F.CN1))),
                F.Power(F.Sin(t), m), //
                sum);
            return engine.evaluate(sphericalHarmonicY);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
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
   * <p>
   * returns the Chebyshev polynomial of the first kind <code>T_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev
   * polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ChebyshevT(8, x)
   * 1-32*x^2+160*x^4-256*x^6+128*x^8
   * </pre>
   */
  private static final class ChebyshevT extends AbstractFunctionEvaluator
      implements IFunctionExpand {
    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        if (n.isIntegerResult()) {
          return true;
        }
        IExpr z = ast.arg2();
        return z.greaterEqual(F.CN1).isTrue();
      }
      return false;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        // Cos(n*ArcCos(z))
        return F.Cos(F.Times(n, F.ArcCos(z)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();

      int degree = n.toIntDefault();
      if (F.isPresent(degree)) {
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();
        return n.chebyshevT(z);
        // (n, z) => Cos(n*ArcCos(z))
        // return F.Cos.of(engine, F.Times(n, F.ArcCos(z)));
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
   * <p>
   * returns the Chebyshev polynomial of the second kind <code>U_n(x)</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Chebyshev_polynomials">Wikipedia - Chebyshev
   * polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ChebyshevU(8, x)
   * 1-40*x^2+240*x^4-448*x^6+256*x^8
   * </pre>
   */
  private static final class ChebyshevU extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        if (n.isIntegerResult()) {
          return true;
        }
        IExpr z = ast.arg2();
        return z.greater(F.CN1).isTrue();
      }
      return false;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        // Sin((1+n)*ArcCos(z))/(Sqrt(1-z)*Sqrt(1+z))
        return F.Times(
            F.Power(F.Times(F.Sqrt(F.Subtract(F.C1, z)), F.Sqrt(F.Plus(F.C1, z))), F.CN1),
            F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(z))));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();

      int degree = n.toIntDefault();
      if (F.isPresent(degree)) {
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
        final IExpr zDoubled = F.C2.times(z);
        if (degree == 1) {
          return zDoubled;
        }
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }

        // (n, z) => Sum(((-1)^k*(n - k)!*(2*z)^(n - 2*k))/(k!*(n - 2*k)!), {k, 0, Floor(n/2)})
        return F
            .sum(k -> F.Times(F.Power(F.CN1, k), F.Power(zDoubled, F.Plus(F.Times(F.CN2, k), n)),
                F.Power(F.Times(F.Factorial(k), F.Factorial(F.Plus(F.Times(F.CN2, k), n))), -1),
                F.Factorial(F.Plus(F.Negate(k), n))), 0, degree / 2);
      }

      if (n.isNumEqualRational(F.CN1D2)) {
        // (-1/2, z) => 1/(Sqrt(2)* Sqrt(1 + z))
        return F.Times(F.C1DSqrt2, F.Power(F.Plus(F.C1, z), F.CN1D2));
      }
      if (n.isNumEqualRational(F.C1D2)) {
        final IExpr zDoubled = F.C2.times(z);
        // (1/2, z) => (1 + 2*z)/(Sqrt(2)* Sqrt(1 + z))
        return F.Times(F.C1DSqrt2, F.Plus(F.C1, zDoubled), F.Power(F.Plus(F.C1, z), F.CN1D2));
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();
        return n.chebyshevU(z);
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
   * <p>
   * the second kind of Bell polynomials (incomplete Bell polynomials).
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bell_polynomials">Wikipedia - Bell polynomials</a>
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
        // int[] dim = arg1.isMatrix(false);
        // if (dim == null || dim.length != 2 || dim[0] != dim[1]) {
        // return F.NIL;
        // }
        // final int size = dim[0];
        // return bellYMatrix((IAST) arg1, size, ast, engine);
        return F.NIL;
      }
      if (ast.isAST2()) {
        return F.NIL;
      }
      if (arg1.isInteger() && ast.arg2().isInteger()) {
        int n = arg1.toIntDefault();
        int k = ast.arg2().toIntDefault();
        if (n < 0 || k < 0) {
          return F.NIL;
        }

        IExpr arg3 = ast.arg3();
        int[] dim = arg3.isMatrix(false);
        if (dim == null || dim.length != 2) {
          IExpr listOfVariables = arg3.normal(false);
          if (!listOfVariables.isList()) {
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
            return bellY(n, k, (IAST) listOfVariables, ast, engine);
          }
          return F.NIL;
        }


        final int rowSize = dim[0];
        final int colSize = dim[1];
        if (n > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(n);
        }
        return bellYMatrix(n, k, (IAST) arg3, rowSize, colSize, ast, engine);


      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
   * <p>
   * returns a Gröbner basis for the <code>polynomial-list</code> and <code>variable-list</code>.
   *
   * </p>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gröbner_basis">Wikipedia - Gröbner basis</a>
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
     * <p>
     * See
     *
     * <ul>
     * <li><a href="https://en.wikipedia.org/wiki/Gr%C3%B6bner_basis">EN-Wikipedia: Gröbner
     * basis</a>
     * <li><a href="https://de.wikipedia.org/wiki/Gr%C3%B6bnerbasis">DE-Wikipedia: Gröbner basis</a>
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
     *         and one of the polynomials in <code>listOfPolynomials</code> are not convertible to
     *         JAS polynomials
     */
    private static IAST computeGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables,
        TermOrder termOrder) {
      List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.argSize());
      String[] pvars = new String[listOfVariables.argSize()];

      for (int i = 1; i < listOfVariables.size(); i++) {
        if (!listOfVariables.get(i).isSymbol()) {
          return F.NIL;
        }
        varList.add((ISymbol) listOfVariables.get(i));
        pvars[i - 1] = listOfVariables.get(i).toString();
      }

      List<GenPolynomial<BigRational>> polyList =
          new ArrayList<GenPolynomial<BigRational>>(listOfPolynomials.argSize());
      JASConvert<BigRational> jas =
          new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
      for (int i = 1; i < listOfPolynomials.size(); i++) {
        IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
        try {
          GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
          if (poly == null) {
            return F.NIL;
          }
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
      return F.mapRange(0, list.size(), i -> {
        GenPolynomial<BigRational> p = list.get(i);
        return jas
            .integerPoly2Expr((GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(p)[2]);
      });
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
   * <p>
   * returns the Hermite polynomial <code>H_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Wikipedia - Hermite
   * polynomials</a>
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
    public boolean evalIsReal(IAST ast) {
      return (ast.argSize() == 2) && ast.arg1().isRealResult() && ast.arg2().isRealResult();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return hermiteHZero(n);
      }
      int degree = n.toIntDefault();
      if (degree >= 0) {
        return PolynomialsUtils.createHermitePolynomial(degree, z);
      }

      return F.NIL;
    }

    private static IExpr hermiteHZero(IExpr n) {
      // https://functions.wolfram.com/Polynomials/HermiteH/03/01/01/0001/
      // (2^n * Sqrt(Pi))/Gamma((1 - n)/2)
      return F.Times(F.Power(F.C2, n), F.CSqrtPi,
          F.Power(F.Gamma(F.Times(F.C1D2, F.Subtract(F.C1, n))), F.CN1));
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2()) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();
        return n.hermiteH(z);
        // if (z.isZero()) {
        // IExpr hermiteH = hermiteHZero(n);
        // return engine.evaluate(hermiteH);
        // }
        // // https://functions.wolfram.com/Polynomials/HermiteH/26/01/02/0001/
        // //
        // 2^n*Sqrt(Pi)*((-2*z*Hypergeometric1F1(1/2*(1-n),3/2,z^2))/Gamma((-1)*1/2*n)+Hypergeometric1F1((-1)*1/2*n,1/2,z^2)/Gamma(1/2*(1-n)))
        // IExpr v1 = F.Times(F.C1D2, F.Subtract(F.C1, n));
        // IExpr v2 = F.Times(F.CN1, F.C1D2, n);
        // IExpr v3 = F.Sqr(z);
        // IAST hermiteH = F.Times(F.Power(F.C2, n), F.CSqrtPi,
        // F.Plus(
        // F.Times(F.CN2, z, F.Power(F.Gamma(v2), F.CN1),
        // F.Hypergeometric1F1(v1, F.QQ(3L, 2L), v3)),
        // F.Times(F.Power(F.Gamma(v1), F.CN1), F.Hypergeometric1F1(v2, F.C1D2, v3))));
        // return engine.evaluate(hermiteH);
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION | ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  private static final class JacobiP extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // }
      IExpr n = ast.arg1();
      IExpr a = ast.arg2();
      IExpr b = ast.arg3();
      IExpr z = ast.arg4();
      if (n.isZero()) {
        return F.C1;
      }
      if (a.isZero() && b.isZero()) {
        return F.LegendreP(n, z);
      }
      IExpr negZ = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negZ.isPresent()) {
        // (-1)^n*JacobiP(n,b,a,z)
        return F.Times(F.Power(F.CN1, n), F.JacobiP(n, b, a, negZ));
      }
      if (n.isInteger()) {
        return jacobiPSum((INumber) n, a, b, z, engine);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 4) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber a = (IInexactNumber) ast.arg2();
        IInexactNumber b = (IInexactNumber) ast.arg3();
        IInexactNumber z = (IInexactNumber) ast.arg4();
        if (n.isMathematicalIntegerNonNegative()) {
          return jacobiPSum(n, a, b, z, engine);
        }
        return n.jacobiP(a, b, z);
        // INumber aNegate = a.negate();
        // if (!aNegate.isMathematicalIntegerNonNegative()) {
        // // https://functions.wolfram.com/Polynomials/JacobiP/26/01/02/0001/
        // // Gamma(a+n+1)/(Gamma(n+1)*Gamma(a+1))*Hypergeometric2F1(-n,a+b+n+1,a+1,1/2*(1-z))
        // IExpr v1 = F.Plus(a, F.C1);
        // IAST jacobiP = F.Times(F.Power(F.Gamma(F.Plus(F.C1, n)), F.CN1),
        // F.Power(F.Gamma(v1), F.CN1), F.Gamma(F.Plus(n, v1)), F.Hypergeometric2F1(F.Negate(n),
        // F.Plus(b, n, v1), v1, F.Times(F.C1D2, F.Subtract(F.C1, z))));
        // return engine.evaluate(jacobiP);
        // }

      }

      return F.NIL;
    }

    private IExpr jacobiPSum(INumber n, IExpr a, IExpr b, IExpr z, final EvalEngine engine) {
      // https://functions.wolfram.com/Polynomials/JacobiP/03/01/04/0008/
      // Sum((Binomial(a+n,k)*Binomial(b+n,-k+n)*(z+1)^k)/(-1+z)^(k-n),{k,0,n})/2^n
      int ni = n.toIntDefault();
      if (ni >= 0) {
        if (z.isZero()) {
          // https://functions.wolfram.com/Polynomials/JacobiP/03/01/01/0001/
          // Gamma(1+a+n)/(2^n*Gamma(1+a)*n!)*Hypergeometric2F1(-b-n,-n,1+a,-1)
          IExpr v2 = F.Negate(n);
          IExpr v1 = F.Plus(F.C1, a);
          return F.Times(F.Power(F.C2, v2), F.Power(F.Factorial(n), F.CN1),
              F.Power(F.Gamma(v1), F.CN1), F.Gamma(F.Plus(n, v1)),
              F.Hypergeometric2F1(v2, F.Plus(F.Negate(b), v2), v1, F.CN1));
        }
        if (z.isOne()) {
          // https://functions.wolfram.com/Polynomials/JacobiP/03/01/01/0002/
          // Gamma(a+n+1)/(n!*Gamma(a+1))
          return F.Times(F.Power(F.Times(F.Factorial(n), F.Gamma(F.Plus(a, F.C1))), F.CN1),
              F.Gamma(F.Plus(a, n, F.C1)));
        }
        if (z.isMinusOne()) {
          // https://functions.wolfram.com/Polynomials/JacobiP/03/01/01/0003/
          // Gamma(-b)/(Gamma(-b-n)*n!)
          IExpr v1 = F.Negate(b);
          return F.Times(F.Power(F.Factorial(n), F.CN1), F.Gamma(v1),
              F.Power(F.Gamma(F.Plus(F.Negate(n), v1)), F.CN1));
        }
        return jacobiP(ni, a, b, z, engine);
      }
      if (F.isPresent(ni) && a.isInteger() && a.isPositive()) {
        if (ni == -1) {
          return jacobiP(ni, a, b, z, engine);
        }
      }
      return F.NIL;
    }

    private IExpr jacobiP(int ni, IExpr a, IExpr b, IExpr z, final EvalEngine engine) {
      // https://functions.wolfram.com/Polynomials/JacobiP/03/01/04/0008/
      // Sum((Binomial(a+n,k)*Binomial(b+n,-k+n)*(z+1)^k)/(-1+z)^(k-n),{k,0,n})/2^n
      IInteger n = F.ZZ(ni);
      IExpr sum = F.sum(
          k -> F.Times(F.Binomial(F.Plus(a, n), k), F.Binomial(F.Plus(b, n), F.Plus(k.negate(), n)),
              F.Power(F.Plus(z, F.C1), k), F.Power(F.Plus(F.CN1, z), F.Subtract(n, k))), //
          0, ni);
      IAST jacobiP = F.Times(F.Power(F.Power(F.C2, n), F.CN1), sum);
      return engine.evaluate(jacobiP);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
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
   * LaguerreL(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Laguerre polynomial <code>L_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Laguerre_polynomials">Wikipedia - Laguerre
   * polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LaguerreL(8, x)
   * 1-8*x+14*x^2-28/3*x^3+35/12*x^4-7/15*x^5+7/180*x^6-x^7/630+x^8/40320
   * </pre>
   */
  private static final class LaguerreL extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public boolean evalIsReal(IAST ast) {
      return (ast.argSize() == 2) && ast.arg1().isRealResult() && ast.arg2().isRealResult();
    }

    @Override
    public IExpr functionExpand(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        if (n.isNumEqualRational(F.CN1D2)) {
          // E^(z/2)*BesselI(0,z/2)
          IExpr v1 = F.Times(F.C1D2, z);
          return F.Times(F.Exp(v1), F.BesselI(F.C0, v1));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      int degree = n.toIntDefault();
      if (degree >= 0) {
        if (degree > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(degree);
        }
        if (ast.isAST3()) {
          IExpr l = ast.arg2();
          IExpr z = ast.arg3();
          return laguerreLRecursive(n, degree, l, z, engine);
        }
        if (degree == 0) {
          return F.C1;
        }
        if (degree > 0) {
          IExpr z = ast.arg2();
          return laguerreL(degree, z);
        }
      } else if (F.isPresent(degree)) {
        // degree < 0
        if (ast.isAST2()) {
          // E^z*LaguerreL(-degree - 1, -z);
          IExpr z = ast.arg2();
          return F.Times(F.Exp(z), laguerreL(-degree - 1, z.negate()));
        }
      }
      return F.NIL;
    }

    /**
     * Create a Laguerre polynomial of <code>degree >= 0</code> with variable <code>z
     * </code>.
     *
     * @param nonNegativeDegree the <code>degree >= 0</code> of the Laguerre polynomial
     * @param z the variable of the Laguerre polynomial
     * @return the Laguerre polynomial <code>L_n(z)</code>
     */
    private static IAST laguerreL(int nonNegativeDegree, IExpr z) {
      return PolynomialsUtils.createLaguerrePolynomial(nonNegativeDegree, z);
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
        IExpr result =
            // [$ (2 + 3*l + l^2 - 4*z - 2*l*z + z^2) $]
            F.Times(F.Plus(F.C2, F.Times(F.C3, l), F.Sqr(l), F.Times(F.CN4, z),
                F.Times(F.CN2, l, z), F.Sqr(z))); // $$;
        return F.Times(F.C1D2, F.evalExpandAll(result, engine));
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
        IExpr laguerre1 = laguerreLRecursive(F.ZZ(degree - 2), degree - 2, l, z, engine); // F.LaguerreL.of(engine,
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
        IExpr laguerre2 = laguerreLRecursive(F.ZZ(degree - 1), degree - 1, l, z, engine); // F.LaguerreL.of(engine,
        // F.Plus(F.CN1, n),
        // l, z);
        if (laguerre2.isIndeterminate()) {
          throw new ArgumentTypeException("Indeterminate expression detected");
        }
        long leafCount2 = leafCount1 + laguerre2.leafCount();
        if (leafCount2 > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(leafCount2);
        }
        return F.Times(F.Power(n, F.CN1), F.Plus(F.Times(F.CN1, F.Plus(F.CN1, l, n), laguerre1),
            F.Times(F.Plus(F.CN1, l, F.Times(F.C2, n), F.Negate(z)), laguerre2)));
      } finally {
        engine.decRecursionCounter();
      }
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2() || ast.isAST3()) {
        final IInexactNumber n = (IInexactNumber) ast.arg1();
        final IInexactNumber l;
        final IInexactNumber z;
        IExpr temp = F.NIL;
        if (ast.isAST2()) {
          l = F.CD0;
          z = (IInexactNumber) ast.arg2();
          temp = n.laguerreL(z);
        } else {
          l = (IInexactNumber) ast.arg2();
          z = (IInexactNumber) ast.arg3();
          temp = n.laguerreL(l, z);
        }
        if (temp.isPresent()) {
          return temp;
        }
        if (l.isZero()) {
          // https://en.wikipedia.org/wiki/Laguerre_polynomials
          // evaluating E^z/n! * D(z^n/E^z,{z,n})
          // gives (E^z*Piecewise({{(n!*Hypergeometric1F1(-n,1,z))/E^z,n>=1}},z^n/E^z))/n!
          IExpr expZ = z.exp();
          return F
              .Times(
                  expZ, F.Power(F.Factorial(n), F.CN1), F
                      .Piecewise(
                          F.list(F.list(
                              F.Times(F.Power(expZ, F.CN1), F.Factorial(n),
                                  F.Hypergeometric1F1(n.negate(), F.C1, z)),
                              F.GreaterEqual(n, F.C1))),
                          F.Times(F.Power(expZ, F.CN1), F.Power(z, n))));

        }
        // https://en.wikipedia.org/wiki/Laguerre_polynomials#Physicist_Scaling_Convention
        // (Gamma(l+n+1)*Hypergeometric1F1(-n,l+1,z))/(Gamma(l+1)*n!)
        INumber nNumber = n;
        IExpr v1 = l.plus(F.C1);
        return F.Times(F.Power(F.Factorial(n), F.CN1), F.Power(F.Gamma(v1), F.CN1),
            F.Gamma(F.Plus(n, v1)), F.Hypergeometric1F1(nNumber.negate(), v1, z));

      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
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
   * LegendreP(n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Legendre polynomial <code>P_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre
   * polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LegendreP(4, x)
   * 3/8-15/4*x^2+35/8*x^4
   * </pre>
   */
  private static final class LegendreP extends AbstractFunctionEvaluator
      implements IMatch, IFunctionExpand {
    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        if (n.isIntegerResult() && z.isRealResult()) {
          return true;
        }
        if (z.greater(F.CN1).isTrue() && n.isRealResult()) {
          return true;
        }
      } else if (ast.argSize() == 3) {
        IExpr n = ast.arg1();
        IExpr m = ast.arg2();
        if (n.isRealResult() && m.isRealResult()) {
          IExpr z = ast.arg3();
          if (z.greater(F.CN1).isTrue() && z.less(F.C1).isTrue()) {
            return true;
          }
          // TODO add more conditions
        }
      }
      return false;
    }

    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return LegendrePRules.match3(ast, engine);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int argSize = ast.argSize();
      if (argSize == 2) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        if (z.isOne()) {
          // LegendreP(x_, 1) = 1
          return F.C1;
        }
        if (z.isMinusOne()) {
          // LegendreP(x_?IntegerQ, -1) := (-1)^x
          return F.Power(F.CN1, z);
        }

        if (n.isRationalValue(F.CN1D2)) {
          // LegendreP(-(1/2), z_) := (2*EllipticK(1/2*(1-z)))/Pi
          return F.Times(F.C2, F.Power(F.Pi, F.CN1),
              F.EllipticK(F.Times(F.C1D2, F.Subtract(F.C1, z))));
        }
        if (z.isZero() && !engine.isNumericMode() && n.isNumericFunction()) {
          // Sqrt(Pi)/(Gamma(1/2-n/2)*Gamma(1+n/2))
          return F.Times(F.CSqrtPi, F.Power(F.Times(F.Gamma(F.Plus(F.C1D2, F.Times(F.CN1D2, n))),
              F.Gamma(F.Plus(F.C1, F.Times(F.C1D2, n)))), F.CN1));

        }
      } else if (argSize == 3) {
        // LegendreP(x_, 0, z_) = LegendreP(x, z)
        if (ast.arg2().isZero()) {
          return F.LegendreP(ast.arg1(), ast.arg3());
        }

      }
      int degree = arg1.toIntDefault();
      final IExpr arg2 = ast.arg2();
      if (F.isPresent(degree)) {
        degree = degree < 0 ? -degree - 1 : degree;

        if (argSize == 4) {
          IExpr arg3 = ast.arg3();
          IExpr z = ast.arg4();
          if (ast.arg3().isOne()) {
            return legendreP(ast.arg1(), arg2, z, 1, engine);
          }
          if (ast.arg3().isNumEqualInteger(F.C2)) {
            return legendreP(ast.arg1(), arg2, z, 2, engine);
          }
          if (ast.arg3().isNumEqualInteger(F.C3)) {
            // TODO
            // return legendreP(ast.arg1(), arg2, z, 3, engine);
            return F.NIL;
          }
          if (arg3.isNumber()) {
            // Legendre type `1` is expected to be 1, 2, 3.
            return Errors.printMessage(S.LegendreP, "ltype", F.List());
          }
          return F.NIL;
        }
        if (argSize == 2) {
          return legendreP(degree, arg2);
        }
        if (argSize == 3) {
          IExpr arg3 = ast.arg3();
          return legendreP(arg1, arg2, arg3, 1, engine);
        }
      }
      return F.NIL;
    }

    private static IExpr legendreP(IExpr n, final IExpr m, IExpr z, int type, EvalEngine engine) {
      int ni = n.toIntDefault();
      if (F.isPresent(ni)) {
        int mi = m.toIntDefault();
        if (F.isPresent(mi)) {
          int miAbs = mi < 0 ? -mi : mi;
          ni = ni < 0 ? -ni - 1 : ni;
          if (miAbs > ni) {
            return F.C0;
          }

          ISymbol dummyZ = F.Dummy("$z");
          IExpr legendreP = legendreP(ni, dummyZ);
          // (-1)^miAbs*(1-z^2)^(miAbs/2)*D(legendreP,{z,miAbs})
          IExpr mAbs = F.ZZ(miAbs);
          IExpr typeFactor = getTypeFactor(type, miAbs, dummyZ, engine);
          IExpr diff = engine.evaluate(F.D(legendreP, F.list(dummyZ, mAbs)));
          IExpr resultForPositiveM =
              engine.evaluate(F.Times(F.Power(F.CN1, mAbs), typeFactor, diff));

          resultForPositiveM = F.subst(resultForPositiveM, dummyZ, z);
          // apply the identity for negative m if necessary
          if (mi < 0) {
            // (-1)^mAbs*(-mAbs+ni)!/(ni+mAbs)!*resultForPositiveM
            IExpr nEff = F.ZZ(ni);
            return F.Times(F.Power(F.CN1, mAbs), F.Power(F.Factorial(F.Plus(nEff, mAbs)), F.CN1),
                F.Factorial(F.Plus(F.Negate(mAbs), nEff)), resultForPositiveM);
          }
          return resultForPositiveM;
        }
      }
      return F.NIL;
    }


    private static IExpr legendreP(int n, IExpr z) {
      if (n > Config.MAX_POLYNOMIAL_DEGREE) {
        PolynomialDegreeLimitExceeded.throwIt(n);
      }
      return PolynomialsUtils.createLegendrePolynomial(n, z);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      int argSize = ast.argSize();
      if (argSize == 4) {
        if (ast.arg3().isNumEqualInteger(F.C2)) {
          return functionExpand(ast, engine);
        }
      } else if (argSize == 2 || argSize == 3) {
        final IInexactNumber n = (IInexactNumber) ast.arg1();
        final IInexactNumber z;
        IExpr temp = F.NIL;
        if (ast.isAST2()) {
          z = (IInexactNumber) ast.arg2();
          temp = n.legendreP(z);
        } else {
          final IInexactNumber m = (IInexactNumber) ast.arg2();
          z = (IInexactNumber) ast.arg3();
          if (m.isZero()) {
            temp = n.legendreQ(z);
          } else {
            temp = n.legendreP(m, z);
          }
        }
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION | ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr functionExpand(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 4 //
          && ast.arg3().isNumEqualInteger(F.C2)) {
        IExpr m = ast.arg1();
        IExpr n = ast.arg2();
        IExpr z = ast.arg4();
        // ((1+z)^(m/2)*Hypergeometric2F1(-n,1+n,1-m,1/2*(1-z)))/((1-z)^(m/2)*Gamma(1-m))
        IExpr v3 = F.Subtract(F.C1, z);
        IExpr v2 = F.Times(F.C1D2, m);
        IExpr v1 = F.Subtract(F.C1, m);
        return F.Times(F.Power(v3, F.Times(F.CN1D2, m)), F.Power(F.Plus(F.C1, z), v2),
            F.Power(F.Gamma(v1), F.CN1),
            F.Hypergeometric2F1(F.Negate(n), F.Plus(F.C1, n), v1, F.Times(F.C1D2, v3)));

      }
      return F.NIL;
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
   * <p>
   * returns the Legendre functions of the second kind <code>Q_n(x)</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Legendre_polynomials">Wikipedia - Legendre
   * polynomials</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Expand(LegendreQ(4,z))
   * 55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)-15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)
   * </pre>
   */
  static final class LegendreQ extends AbstractFunctionEvaluator
      implements IMatch, IFunctionExpand {
    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 2) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        if (z.greater(F.CN1).isTrue() && z.less(F.C1).isTrue()) {
          if (n.isNonNegative()) {
            return true;
          }
          if (n.isReal() && !n.isInteger()) {
            return true;
          }
        }
      } else if (ast.argSize() == 3) {
        IExpr n = ast.arg1();
        IExpr m = ast.arg2();
        if (n.isRealResult() && m.isRealResult()) {
          IExpr z = ast.arg3();
          if (z.greater(F.CN1).isTrue() && z.less(F.C1).isTrue()) {
            IExpr temp = m.plus(n);
            if (temp.isNonNegative()) {
              return true;
            }
            if (temp.isReal() && !temp.isInteger()) {
              return true;
            }
          }
          // TODO add more conditions
        }
      }
      return false;
    }

    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return LegendreQRules.match3(ast, engine);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      IExpr n = ast.arg1();

      if (argSize == 2) {
        IExpr z = ast.arg2();
        if (z.isOne()) {
          // LegendreQ(x_, 1) = ComplexInfinity,
          return F.CComplexInfinity;
        }
        if (z.isMinusOne()) {
          if (n.isRationalValue(F.CN1D2)) {
            return F.CPiHalf;
          }
          // LegendreQ(x_, -1) = ComplexInfinity,
          return F.CComplexInfinity;
        }
        int ni = n.toIntDefault();
        if (ni >= 0) {
          return legendreQ(ni, z);
        } else if (F.isPresent(ni)) {
          // LegendreQ(x_Integer, z_) := ComplexInfinity /; x<0,
          return F.CComplexInfinity;
        }
      } else if (argSize == 3) {
        IExpr m = ast.arg2();
        IExpr z = ast.arg3();

        if (m.isZero()) {
          // LegendreQ(x_, 0, z_) = LegendreQ(x, z)
          return F.LegendreQ(n, z);
        }
        int ni = n.toIntDefault();
        if (ni >= 0) {
          return legendreQ(n, m, z, 1, engine);
        }
      }
      if (argSize == 4) {
        IExpr m = ast.arg2();
        IExpr arg3 = ast.arg3();
        IExpr z = ast.arg4();
        int ni = n.toIntDefault();
        if (ni >= 0) {
          if (ast.arg3().isOne()) {
            return legendreQ(n, m, z, 1, engine);
          }
          if (ast.arg3().isNumEqualInteger(F.C2)) {
            return legendreQ(n, m, z, 2, engine);
          }
          if (ast.arg3().isNumEqualInteger(F.C3)) {
            // TODO
            // return legendreQ(n, m, z, 3, engine);
            return F.NIL;
          }
          if (arg3.isNumber()) {
            // Legendre type `1` is expected to be 1, 2, 3.
            return Errors.printMessage(S.LegendreQ, "ltype", F.List());
          }
        }
        return F.NIL;
      }
      return F.NIL;
    }

    /**
     * Legendre function of the 2nd kind for integer n and m==0 (Christoffels 2nd formula für m==0)
     */
    private static IExpr legendreQ(int ni, IExpr z) {
      if (ni == 0) {
        // -Log(1-z)/2+Log(1+z)/2
        return F.Plus(F.Times(F.CN1D2, F.Log(F.Subtract(F.C1, z))),
            F.Times(F.C1D2, F.Log(F.Plus(F.C1, z))));
      }

      // 1/2*(-Log(1-z)+Log(1+z))*LegendreP(n,z)-Sum((-1-4*k+2*n)/((2*k+1)*(-k+n))*LegendreP(-
      // 1-2*k+n,z),{k,0,Floor(1/2*(-1+n))})
      IInteger n = F.ZZ(ni);
      int sumNi = (ni - 1) / 2;
      // polynomial w_{n-1}(z)
      IExpr w = F.sum(k -> F.Times(F.Plus(F.CN1, F.Times(F.CN4, k), F.Times(F.C2, n)),
          F.Power(F.Times(F.Plus(F.Times(F.C2, k), F.C1), F.Plus(F.Negate(k), n)), F.CN1),
          F.LegendreP(F.Plus(F.CN1, F.Times(F.CN2, k), n), z)), 0, sumNi);
      return F.Subtract(F.Times(F.C1D2,
          F.Plus(F.Negate(F.Log(F.Subtract(F.C1, z))), F.Log(F.Plus(F.C1, z))), F.LegendreP(n, z)),
          w);
    }

    private static IExpr legendreQ(IExpr n, final IExpr m, IExpr z, int type, EvalEngine engine) {
      int ni = n.toIntDefault();
      if (F.isPresent(ni)) {
        int mi = m.toIntDefault();
        if (F.isPresent(mi) && mi >= 0) {
          if (mi == 0) {
            return legendreQ(ni, z);
          }
          ISymbol dummyZ = F.Dummy("$z");
          IExpr legendreQ = engine.evaluate(legendreQ(ni, dummyZ));
          IInteger mAbs = F.ZZ(mi);
          IExpr diff = engine.evaluate(F.D(legendreQ, F.list(dummyZ, mAbs)));
          IExpr typeFactor = getTypeFactor(type, mi, dummyZ, engine);
          IExpr result = F.Times(F.Power(F.CN1, mAbs), typeFactor, diff);
          result = F.subst(result, dummyZ, z);
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2() || ast.isAST3()) {
        final IInexactNumber n = (IInexactNumber) ast.arg1();
        final IInexactNumber z;
        IExpr temp = F.NIL;
        if (ast.isAST2()) {
          z = (IInexactNumber) ast.arg2();
          temp = n.legendreQ(z);
        } else {
          final IInexactNumber m = (IInexactNumber) ast.arg2();
          z = (IInexactNumber) ast.arg3();
          if (m.isZero()) {
            temp = n.legendreQ(z);
          } else {
            temp = n.legendreQ(m, z);
          }
        }
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      if (ast != null && ast.size() > 1) {
        int arg1 = ast.arg1().toIntDefault();
        if (Math.abs(arg1) > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(arg1);
        }
      }
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION | ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr functionExpand(IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      if (argSize == 2 && ast.arg2().isZero()) {
        IExpr l = ast.arg1();
        // (-Pi^(3/2)*Tan(1/2*l*Pi))/(l*Gamma(1/2-l/2)*Gamma(l/2))
        return F.Times(
            F.CN1, F.Power(F.Pi, F.QQ(3L, 2L)), F.Power(F.Times(l,
                F.Gamma(F.Plus(F.C1D2, F.Times(F.CN1D2, l))), F.Gamma(F.Times(F.C1D2, l))), F.CN1),
            F.Tan(F.Times(F.C1D2, l, F.Pi)));

      }

      if (argSize == 3) {
        IExpr l = ast.arg1();
        IExpr m = ast.arg2();
        IExpr z = ast.arg3();
        // (-Pi*Csc(m*Pi)*Gamma(1+l+m)*LegendreP(l,-m,z))/(2*Gamma(1+l-m))+1/2*Pi*Cot(m*Pi)*LegendreP(l,m,z)
        return F.Plus(
            F.Times(F.CN1, F.Pi, F.Csc(F.Times(m, F.Pi)),
                F.Power(F.Times(F.C2, F.Gamma(F.Plus(F.C1, l, F.Negate(m)))), F.CN1),
                F.Gamma(F.Plus(F.C1, l, m)), F.LegendreP(l, F.Negate(m), z)),
            F.Times(F.C1D2, F.Pi, F.Cot(F.Times(m, F.Pi)), F.LegendreP(l, m, z)));

      }
      return F.NIL;
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
   * <p>
   * get the list of monomials of a <code>polynomial</code> expression, with respect to the
   * <code>list-of-variables</code>.
   *
   * </p>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial</a><br>
   * </ul>
   */
  private static final class MonomialList extends AbstractFunctionEvaluator {
    /**
     * Get the list of monomials of a polynomial expression.
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
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
        if (symbolList.isNIL()) {
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
          if (option.isInteger()) {
            try {
              return monomialListModulus(expr, varList, termOrder, option);
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.MonomialList, rex, engine);
              // LOGGER.debug("MonomialList.evaluate() failed", rex);
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
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.MonomialList, rex, engine);
        // LOGGER.debug("MonomialList.evaluate() failed", rex);
      }
      // default mapping
      return F.list(expr);
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
    private static IAST monomialListModulus(IExpr polynomial, List<IExpr> variablesList,
        final TermOrder termOrder, IExpr option) throws JASConversionException {
      try {
        // found "Modulus" option => use ModIntegerRing
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
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
        Errors.printMessage(S.MonomialList, ae);
        // LOGGER.debug("MonomialList.monomialListModulus() failed", ae);
      }
      return F.NIL;
    }
  }


  private static final class ZernikeR extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 3) {
        IExpr n = ast.arg1();
        IExpr m = ast.arg2();
        IExpr r = ast.arg3();
        return functionExpand(n, m, r);
      }
      return F.NIL;
    }

    private static IExpr functionExpand(IExpr n, IExpr m, IExpr r) {
      // r^m*Cos(1/2*(-m+n)*Pi)*JacobiP(1/2*(-m+n),m,0,1-2*r^2)
      return F.Times(F.Power(r, m), F.Cos(F.Times(F.C1D2, F.Plus(F.Negate(m), n), F.Pi)),
          F.JacobiP(F.Times(F.C1D2, F.Plus(F.Negate(m), n)), m, F.C0,
              F.Plus(F.C1, F.Times(F.CN2, F.Sqr(r)))));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr m = ast.arg2();
      IExpr r = ast.arg3();

      if (n.isInteger() && m.isInteger()) {
        return F.evalExpand(functionExpand(n, m, r));
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 3) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber m = (IInexactNumber) ast.arg2();
        IInexactNumber r = (IInexactNumber) ast.arg3();
        return functionExpand(n, m, r);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

  }

  public static IAST computeBellY(IAST matrix, final int size, IAST originalAST,
      EvalEngine engine) {

    IASTAppendable result = F.ListAlloc(size);
    // initializeCombinationsCache(size);

    for (int n = 1; n <= size; n++) {
      IASTAppendable newRow = F.constantArray(F.C0, size);
      for (int k = 1; k <= n; k++) {
        int varsSize = n - k + 1;
        IASTAppendable vars = F.ListAlloc(varsSize);
        for (int i = 0; i < varsSize; i++) {
          vars.append(matrix.getPart(i + k, k));
        }
        newRow.set(k, bellY(n, k, vars, originalAST, engine));
      }
      result.append(newRow);
    }
    return result;
  }

  /**
   * Get the type factor for the associated Legendre polynomial.
   *
   * @param type the type (1, 2, or 3)
   * @param m the absolute value of m
   * @param z the variable
   * @param engine the evaluation engine
   * @return the type factor
   */
  private static IExpr getTypeFactor(int type, int m, ISymbol z, EvalEngine engine) {
    IAST oneMinusSqrZ = F.Subtract(F.C1, F.Sqr(z));
    IAST onePlusZ = F.Plus(F.C1, z);
    IRational mHalf = F.QQ(m, 2);
    IRational mimusMHalf = F.QQ(-m, 2);
    IAST oneSubtractZ = F.Subtract(F.C1, z);
    IExpr typeFactor;
    switch (type) {
      case 2:
        typeFactor = engine.evaluate( //
            F.Times(F.Power(onePlusZ, mHalf), F.Power(oneSubtractZ, mHalf))//
        );
        break;
      case 3:
        IAST minusOnePlusZ = F.Plus(F.CN1, z);
        typeFactor = engine.evaluate( //
            F.Times(F.Power(onePlusZ, mHalf), //
                F.Power(minusOnePlusZ, mimusMHalf))//
        );
        break;
      default:
        typeFactor = engine.evaluate( //
            F.Power(oneMinusSqrZ, mHalf) //
        );
    }
    return typeFactor;
  }

  private static IExpr bellYMatrix(int n, int k, IAST matrix, int rowSize, int colSize,
      IAST originalAST, EvalEngine engine) {
    // Generalized partial Bell polynomial for a matrix argument.
    // See:
    // https://en.wikipedia.org/wiki/Bell_polynomials#Multivariate_Bell_polynomials
    if (n < 0 || k < 0) {
      return F.C0;
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

    IExpr result = F.C0;
    IExpr partitionsExpr = engine.evaluate(F.IntegerPartitions(F.ZZ(n), F.ZZ(k)));
    IAST partitions = (IAST) partitionsExpr;
    for (int i = 1; i < partitions.size(); i++) {
      IAST partition = (IAST) partitions.get(i);
      IASTAppendable counts = ListFunctions.tally(partition);
      IInteger term = F.ZZ(n).factorial();
      IInteger den = F.ZZ(k).factorial();

      IASTAppendable vars = F.ListAlloc(k);
      for (int j = 1; j < counts.size(); j++) {
        IAST countPair = (IAST) counts.get(j);
        IInteger part = (IInteger) countPair.arg1();
        IInteger count = (IInteger) countPair.arg2();
        if (part.isPositive() && part.toIntDefault() == k) {
          vars.append(false);
        }
        // den = den.multiply(count.factorial());
        // if (part.isPositive() && part.toIntDefault() < matrix.size()) {
        // IExpr matrixElement = matrix.get(part.toIntDefault());
        // term = term.times(F.Power(matrixElement, count));
        // }
        // den = den.multiply(F.Power(F.Factorial(part), count));
      }
      result = result.plus(F.Divide(term, den));
    }

    return engine.evaluate(result);
  }

  /**
   * Calculate the partial Bell polynomial recursively.
   * 
   * @param n
   * @param k
   * @param symbols
   * @param originalAST
   * @param engine
   * @return
   * 
   */
  public static IExpr bellY(int n, int k, IAST symbols, IAST originalAST, EvalEngine engine) {
    final int recursionLimit = engine.getRecursionLimit();
    try {
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(counter, originalAST);
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
        IterationLimitExceeded.throwIt(max, originalAST);
      }
      for (int m = 1; m < max; m++) {
        if ((m < symbols.size()) && !symbols.get(m).isZero()) {
          IExpr bellY = bellY(n - m, k - 1, symbols, originalAST, engine);
          if (bellY.isPlus()) {
            bellY = bellY.mapThread(F.Times(a, F.Slot1, symbols.get(m)), 2);
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
   * Used in <code>Solve()</code> function to reduce the polynomial list of equations.
   *
   * @param listOfPolynomials a list of polynomials
   * @param listOfVariables a list of variable symbols
   * @return <code>F.NIL</code> if <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
   *         one of the polynomials in <code>listOfPolynomials</code> are not convertible to JAS
   *         polynomials
   */
  public static IASTAppendable solveGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables) {
    List<IExpr> varList = new ArrayList<IExpr>(listOfVariables.argSize());
    for (int i = 1; i < listOfVariables.size(); i++) {
      // if (!listOfVariables.get(i).isSymbol() ) {
      // return F.NIL;
      // }
      varList.add(listOfVariables.get(i));
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
        if (poly == null) {
          rest.append(expr);
        } else {
          polyList.add(poly);
        }
      } catch (JASConversionException e) {
        rest.append(expr);
      }
    }

    if (polyList.size() == 0) {
      return F.NIL;
    }
    GroebnerBaseAbstract<BigRational> engine =
        GBAlgorithmBuilder.<BigRational>polynomialRing(jas.getPolynomialRingFactory())
            .fractionFree().syzygyPairlist().build();
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
