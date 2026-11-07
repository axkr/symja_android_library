package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Plots of complex valued functions which are drawn by the real plots: <code>ReImPlot</code> and
 * <code>AbsArgPlot</code> by <code>Plot</code>, <code>ComplexContourPlot</code> by
 * <code>ContourPlot</code>.
 */
public class ComplexFunctionPlots {

  /** A plot iterator <code>{x, min, max}</code> whose variable is a symbol. */
  private static boolean isIterator(IExpr iterator) {
    return iterator.isList3() && iterator.first().isSymbol();
  }

  /**
   * <code>ReImPlot[f, {x, xmin, xmax}]</code> - the real and the imaginary part of <code>f</code>,
   * or of each function of a list, as the curves of <code>Plot</code>. Unless a
   * <code>PlotStyle</code> is given both parts of a function are drawn in its colour, the
   * imaginary part dashed.
   */
  public static class ReImPlot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || !isIterator(ast.arg2())) {
        return F.NIL;
      }
      IExpr body = ast.arg1();
      IAST functions = body.isList() ? (IAST) body : F.list(body);
      IASTAppendable parts = F.ListAlloc(2 * functions.argSize());
      for (IExpr f : functions) {
        parts.append(F.Re(f));
        parts.append(F.Im(f));
      }
      IASTAppendable plot = F.ast(S.Plot, ast.size() + 1);
      plot.append(parts);
      plot.appendArgs(2, ast.size(), ast::get);
      if (!VectorPlot.hasOption(ast, S.PlotStyle)) {
        IASTAppendable styles = F.ListAlloc(2 * functions.argSize());
        for (int k = 0; k < functions.argSize(); k++) {
          IAST color = GraphicsOptions.plotStyleColorExpr(k, F.NIL);
          styles.append(color);
          styles.append(F.Directive(color, S.Dashed));
        }
        plot.append(F.Rule(S.PlotStyle, styles));
      }
      IExpr result = engine.evaluate(plot);
      return result.isAST(S.Graphics) ? result : F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * <code>AbsArgPlot[f, {x, xmin, xmax}]</code> - the curve of <code>Abs[f]</code> coloured by
   * <code>Arg[f]</code> on a cyclic hue: red where <code>f</code> is positive, cyan where it is
   * negative.
   */
  public static class AbsArgPlot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || !isIterator(ast.arg2()) || ast.arg1().isList()) {
        return F.NIL;
      }
      IExpr f = ast.arg1();
      ISymbol x = (ISymbol) ast.arg2().first();
      ISymbol height = F.Dummy("$y");
      // the argument as a share of a full turn, 0 for the positive reals
      IExpr hue = F.N(F.Mod(F.Times(F.Arg(f), F.Power(F.Times(F.C2, S.Pi), F.CN1)), F.C1));
      IExpr colorFunction =
          F.Function(F.list(x, height), F.Hue(hue, F.num(0.75), F.num(0.9)));
      IASTAppendable plot = F.ast(S.Plot, ast.size() + 2);
      plot.append(F.Abs(f));
      plot.appendArgs(2, ast.size(), ast::get);
      if (!VectorPlot.hasOption(ast, S.ColorFunction)) {
        plot.append(F.Rule(S.ColorFunction, colorFunction));
        plot.append(F.Rule(S.ColorFunctionScaling, S.False));
      }
      IExpr result = engine.evaluate(plot);
      return result.isAST(S.Graphics) ? result : F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * <code>ComplexContourPlot[f, {z, zmin, zmax}]</code> - the contours of the real function
   * <code>f</code>, or of each function or equation of a list, over the rectangle of the complex
   * plane with the corners <code>zmin</code> and <code>zmax</code>: the <code>ContourPlot</code> of
   * <code>f</code> with <code>z = x + I*y</code>.
   */
  public static class ComplexContourPlot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || !isIterator(ast.arg2())) {
        return F.NIL;
      }
      IAST iterator = (IAST) ast.arg2();
      IExpr from = engine.evalN(iterator.arg2());
      IExpr to = engine.evalN(iterator.arg3());
      if (!from.isNumber() || !to.isNumber()) {
        return F.NIL;
      }
      double re0 = ((INumber) from).reDoubleValue();
      double im0 = ((INumber) from).imDoubleValue();
      double re1 = ((INumber) to).reDoubleValue();
      double im1 = ((INumber) to).imDoubleValue();
      if (!(re1 > re0) || !(im1 > im0)) {
        return F.NIL;
      }
      ISymbol z = (ISymbol) iterator.arg1();
      ISymbol x = F.Dummy("$x");
      ISymbol y = F.Dummy("$y");
      IExpr body = F.subst(ast.arg1(), z, F.Plus(x, F.Times(F.CI, y)));
      IASTAppendable plot = F.ast(S.ContourPlot, ast.size() + 1);
      plot.append(body);
      plot.append(F.list(x, F.num(re0), F.num(re1)));
      plot.append(F.list(y, F.num(im0), F.num(im1)));
      plot.appendArgs(3, ast.size(), ast::get);
      IExpr result = engine.evaluate(plot);
      return result.isAST(S.Graphics) ? result : F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }
}
