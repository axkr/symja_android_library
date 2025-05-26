package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots x/y functions */
public class PolarPlot extends AbstractFunctionOptionEvaluator {
  /** Constructor for the singleton */
  public static final PolarPlot CONST = new PolarPlot();

  public PolarPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || !ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      IExpr arg2 = argSize >= 2 ? ast.arg2() : F.CEmptyString;
      return Errors.printMessage(S.PolarPlot, "pllim", F.list(arg2), engine);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue() || Config.USE_MANIPULATE_JS) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(true, true));
  }
}
