package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.transform.DftNormalization;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NFourierTransform extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public NFourierTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr expr = ast.arg1();
    try {
      IExpr symbol = Validate.checkSymbolType(ast, 2, engine);
      if (symbol.isPresent()) {
        // IExpr omega = ast.arg3();
        if (ast.size() > 4) {
          // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
          // IExpr optionFourierParameters = options.getOption(F.FourierParameters);
          // if (optionFourierParameters.isList()) {
          // // analyze the parameters, if they are correct
          // }
        }

        UnivariateFunction f = new UnaryNumerical(expr, (ISymbol) symbol, engine);
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        org.hipparchus.complex.Complex[] result =
            fft.transform(f, -1.0, 1.0, 8, TransformType.FORWARD);
        return Object2Expr.convertComplex(true, result);
      }
    } catch (ValidateException ve) {
      LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDFIRST);
  }
}
