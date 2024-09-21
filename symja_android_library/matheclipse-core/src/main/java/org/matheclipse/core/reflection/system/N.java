package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;


/**
 *
 *
 * <pre>
 * N(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the numerical value of <code>expr</code>.<br>
 *
 * </blockquote>
 *
 * <pre>
 * N(expr, precision)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * evaluates <code>expr</code> numerically with a precision of <code>prec</code> digits.<br>
 *
 * </blockquote>
 *
 * <p>
 * <strong>Note</strong>: the upper case identifier <code>N</code> is different from the lower case
 * identifier <code>n</code>.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; N(Pi)
 * 3.141592653589793
 *
 * &gt;&gt; N(Pi, 50)
 * 3.1415926535897932384626433832795028841971693993751
 *
 * &gt;&gt; N(1/7)
 * 0.14285714285714285
 *
 * &gt;&gt; N(1/7, 5)
 * 1.4285714285714285714e-1
 * </pre>
 */
public final class N extends AbstractCoreFunctionEvaluator {

  public N() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return numericEval(ast, engine);
  }

  @Override
  public IExpr numericEval(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isListOrAssociation() || arg1.isRuleAST()) {
      return ((IAST) arg1).mapThread(ast, 1);
    }
    if (ast.isAST1()) {
      return org.matheclipse.core.reflection.system.N.evalN1(arg1, engine);
    }
    IExpr arg2 = ast.arg2();
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldDigitPrecision = engine.getNumericPrecision();
    try {
      long nDigitPrecision = oldDigitPrecision;
      arg2 = engine.evaluateNonNumeric(arg2);
      nDigitPrecision = arg2.toIntDefault();
      if (nDigitPrecision <= 0) {
        // Requested precision `1` is smaller than `2`.
        return Errors.printMessage(S.N, "precsm", F.list(arg2, F.C1), engine);
      }
      if (nDigitPrecision > Config.MAX_PRECISION_APFLOAT) {
        // Requested precision `1` is greater than `2`.
        return Errors.printMessage(S.N, "precgt", F.list(arg2, F.ZZ(Config.MAX_PRECISION_APFLOAT)),
            engine);
      }
      return org.matheclipse.core.reflection.system.N.evalN2(arg1, nDigitPrecision, engine);
    } finally {
      engine.setNumericMode(oldNumericMode);
      engine.setNumericPrecision(oldDigitPrecision);
    }
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}


  public static IExpr evalN1(IExpr expr, EvalEngine engine) {
    return N.evalN1(expr, Config.USE_EXTENDED_PRECISION_IN_N, engine);
  }

  public static IExpr evalN1(IExpr expr, boolean useExtendedPrecision, EvalEngine engine) {
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldDigitPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try {
      long numericPrecision = oldDigitPrecision; // Config.MACHINE_PRECISION;
      // if (expr.isNumericFunction(true)) {
      // engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
      // IExpr temp = engine.evalWithoutNumericReset(expr);
      // if (temp.isListOrAssociation() || temp.isRuleAST()) {
      // return ((IAST) temp).mapThread(F.N(F.Slot1), 1);
      // }
      // return temp;
      // }
      expr = engine.evaluate(expr);
      if (expr.isInexactNumber()) {
        return expr;
      }
      if (expr.isListOrAssociation() || expr.isRuleAST()) {
        return ((IAST) expr).mapThread(F.N(F.Slot1), 1);
      }
      if (useExtendedPrecision) {
        engine.setDeterminePrecision(expr, false);
        engine.setNumericMode(true);
      } else {
        engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
      }
      if (expr.isAST()) {
        ISymbol topSymbol = expr.topHead();
        expr = engine.evalArgs((IAST) expr, topSymbol.getAttributes(), true).orElse(expr);
      }

      IExpr result = engine.evalWithoutNumericReset(expr);
      if (result instanceof ApfloatNum) {
        return F.num(result.evalf());
      }
      if (result instanceof ApcomplexNum) {
        return F.complexNum(result.evalfc());
      }
      return result;
    } finally {
      engine.setNumericMode(oldNumericMode);
      engine.setNumericPrecision(oldDigitPrecision);
    }
  }

  public static IExpr evalN2(IExpr expr, long nDigitPrecision, EvalEngine engine) {
    // first try symbolic evaluation
    expr = engine.evaluate(expr);
    if (expr.isInexactNumber()) {
      return expr;
    }
    if (expr.isListOrAssociation() || expr.isRuleAST()) {
      return ((IAST) expr).mapThread(F.N(F.Slot1, F.ZZ(nDigitPrecision)), 1);
    }
    final int maxSize =
        (Config.MAX_OUTPUT_SIZE > Short.MAX_VALUE) ? Short.MAX_VALUE : Config.MAX_OUTPUT_SIZE;
    int significantFigures = (nDigitPrecision > maxSize) ? maxSize : (int) nDigitPrecision;
    if (nDigitPrecision < ParserConfig.MACHINE_PRECISION) {
      nDigitPrecision = ParserConfig.MACHINE_PRECISION;
    }

    // after symbolic evaluation do numeric evaluation with n-digit precision
    engine.setNumericMode(true, nDigitPrecision, significantFigures);
    return engine.evalWithoutNumericReset(expr);
  }

}
