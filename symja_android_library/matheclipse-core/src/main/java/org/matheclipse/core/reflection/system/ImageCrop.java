package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;

public class ImageCrop extends AbstractEvaluator {

  public ImageCrop() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1 instanceof ImageExpr) {
      IExpr arg2 = F.C0;
      if (ast.isAST2()) {
        arg2 = ast.arg2();
      }

      ImageExpr image = (ImageExpr) arg1;
      TensorUnaryOperator eqOperator = org.matheclipse.core.tensor.img.ImageCrop.eq(arg2);
      IAST resultImage = eqOperator.apply(image.getMatrix());
      if (resultImage.isPresent()) {
        ImageExpr imageExpr = ImageExpr.toImageExpr(resultImage);
        if (imageExpr != null) {
          return imageExpr;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
