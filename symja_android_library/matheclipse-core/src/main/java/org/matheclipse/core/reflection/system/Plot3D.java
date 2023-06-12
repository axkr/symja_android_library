package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;
import static org.matheclipse.core.expression.F.SurfaceGraphics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Represents the Plot3D function
 *
 * @see org.matheclipse.core.eval.util.OptionArgs
 */
public class Plot3D extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Constructor for the singleton */
  public static final Plot3D CONST = new Plot3D();

  private static final int NUMBER_OF_DIVISIONS = 21;

  public Plot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    // ISymbol optionsArray[] = new ISymbol[] { f.BoxRatios, f.PlotRange };
    if ((ast.size() >= 4) && ast.arg2().isList() && ast.arg3().isList()) {
      try {
        final IASTAppendable graphics = SurfaceGraphics();
        IExpr temp;
        final IAST lst1 = (IAST) ast.arg2(); // x-Range
        final IAST lst2 = (IAST) ast.arg3(); // y-Range
        if (lst1.isAST3() && lst2.isAST3()) {
          // final Options hOptions = new Options(F.Plot3D, ast, 4);
          // IAST allOptions = List();
          // for (int i = 0; i < optionsArray.length; i++) {
          // allOptions.add(optionsArray[i]);
          // }
          // allOptions = hOptions.replaceAll(allOptions);
          // final ISymbol x = (ISymbol) lst1.arg1();
          final IExpr xMin = engine.evalN(lst1.arg2());
          final IExpr xMax = engine.evalN(lst1.arg3());
          // final ISymbol y = (ISymbol) lst2.arg1();
          final IExpr yMin = engine.evalN(lst2.arg2());
          final IExpr yMax = engine.evalN(lst2.arg3());
          if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || (!(yMin instanceof INum))
              || (!(yMax instanceof INum))) {
            return F.NIL;
          }
          final double xMinD = ((INum) xMin).getRealPart();
          final double xMaxD = ((INum) xMax).getRealPart();
          final double yMinD = ((INum) yMin).getRealPart();
          final double yMaxD = ((INum) yMax).getRealPart();
          if (xMaxD <= xMinD) {
            return F.NIL;
          }
          if (yMaxD <= yMinD) {
            return F.NIL;
          }
          // double y0d = -10.0f;
          // double y1d = 10.0f;
          // double params[] = {ad, bd, cd, dd, -10.0, 10.0};

          temp = plotArray(xMinD, xMaxD, yMinD, yMaxD, ast.arg1(), (ISymbol) lst1.arg1(),
              (ISymbol) lst2.arg1(), engine);
          graphics.append(temp);

          final IASTAppendable optionsList = F.ListAlloc();
          // for (int i = 0; i < optionsArray.length; i++) {
          // optionsList.add(Rule(optionsArray[i], allOptions.get(i)));
          // }
          optionsList.append(Rule(S.PlotRange, S.Automatic));
          optionsList.append(Rule(S.DataRange, F.list(F.list(xMin, xMax), F.List(yMin, yMax))));

          graphics.appendAll(optionsList, 1, optionsList.size());
          return Show(graphics);
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("Plot3D.evaluate() failed", rex);
      }
    }
    return F.NIL;
  }

  /**
   * @param xMin the minimum x-range value
   * @param xMax the maximum x-range value
   * @param yMin the minimum y-range value
   * @param yMax the maximum x-range value
   * @param function the function which should be plotted
   * @param xVar the x variable
   * @param yVar the y variable
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> is no conversion of the data into an <code>IExpr</code> was possible
   */
  public static IExpr plotArray(final double xMin, final double xMax, final double yMin,
      final double yMax, final IExpr function, final ISymbol xVar, final ISymbol yVar,
      final EvalEngine engine) {
    final double xStep = (xMax - xMin) / NUMBER_OF_DIVISIONS;
    final double yStep = (yMax - yMin) / NUMBER_OF_DIVISIONS;

    final BinaryNumerical hbn = new BinaryNumerical(function, xVar, yVar, engine);

    final double data[][] = new double[NUMBER_OF_DIVISIONS + 1][NUMBER_OF_DIVISIONS + 1];
    double x = xMin;
    double y;
    double z;
    // double zmin = Double.MAX_VALUE;
    // double zmax = Double.MAX_VALUE;

    for (int i = 0; i < NUMBER_OF_DIVISIONS; i++) {
      y = yMin;
      for (int j = 0; j < NUMBER_OF_DIVISIONS; j++) {
        z = hbn.value(x, y);
        // if (Double.isNaN(z)) {
        // TODO
        // }
        data[i][j] = z;
        y += yStep;
      }
      x += xStep;
    }
    // return Convert.objectToExpr(data);
    return Object2Expr.convert(data, true, false);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.JSForm, S.PlotRange, S.ColorFunction, S.DataRange},
        new IExpr[] {S.False, S.Automatic, S.Automatic, S.Automatic});
  }
}
