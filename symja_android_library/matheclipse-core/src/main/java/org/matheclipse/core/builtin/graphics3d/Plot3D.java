package org.matheclipse.core.builtin.graphics3d;

import static org.matheclipse.core.expression.F.Rule;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Plot3D extends AbstractFunctionOptionEvaluator {

  public static final Plot3D CONST = new Plot3D();
  private static final IExpr DEFAULT_COLOR = F.RGBColor(F.num(1.0), F.num(0.8), F.num(0.4));
  private static final int NUMBER_OF_DIVISIONS = 41;

  public Plot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, originalAST);
      if (temp.headID() == ID.JSFormData)
        return temp;
      return F.NIL;
    }

    if ((ast.size() >= 4) && ast.arg2().isList() && ast.arg3().isList()) {
      return createGraphicsComplex(ast, engine);
    }
    return F.NIL;
  }

  private static IExpr createGraphicsComplex(IAST ast, final EvalEngine engine) {
    try {
      final IAST lst1 = (IAST) ast.arg2();
      final IAST lst2 = (IAST) ast.arg3();
      if (lst1.isAST3() && lst2.isAST3()) {
        final IExpr xMin = engine.evalN(lst1.arg2());
        final IExpr xMax = engine.evalN(lst1.arg3());
        final IExpr yMin = engine.evalN(lst2.arg2());
        final IExpr yMax = engine.evalN(lst2.arg3());

        if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || (!(yMin instanceof INum))
            || (!(yMax instanceof INum)))
          return F.NIL;

        final double xMinD = ((INum) xMin).getRealPart();
        final double xMaxD = ((INum) xMax).getRealPart();
        final double yMinD = ((INum) yMin).getRealPart();
        final double yMaxD = ((INum) yMax).getRealPart();
        if (xMaxD <= xMinD || yMaxD <= yMinD)
          return F.NIL;

        final ISymbol xVar = (ISymbol) lst1.arg1();
        final ISymbol yVar = (ISymbol) lst2.arg1();
        final IExpr function = ast.arg1();

        final int gridPoints = NUMBER_OF_DIVISIONS;
        final double xStep = (xMaxD - xMinD) / (gridPoints - 1);
        final double yStep = (yMaxD - yMinD) / (gridPoints - 1);

        final BinaryNumerical hbn = new BinaryNumerical(function, xVar, yVar, engine);

        final IASTAppendable pointsList = F.ListAlloc(gridPoints * gridPoints);
        for (int i = 0; i < gridPoints; i++) {
          double x = xMinD + i * xStep;
          for (int j = 0; j < gridPoints; j++) {
            double y = yMinD + j * yStep;
            double z = hbn.value(x, y);
            pointsList.append(F.List(F.num(x), F.num(y), F.num(z)));
          }
        }

        final IASTAppendable faces = F.ListAlloc((gridPoints - 1) * (gridPoints - 1));
        for (int i = 0; i < gridPoints - 1; i++) {
          for (int j = 0; j < gridPoints - 1; j++) {
            int p1 = (i * gridPoints) + j + 1;
            int p2 = p1 + 1;
            int p3 = p2 + gridPoints;
            int p4 = p1 + gridPoints;
            faces.append(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4)));
          }
        }

        IASTAppendable group = F.ListAlloc(2);
        group.append(DEFAULT_COLOR);
        group.append(F.Polygon(faces));

        final IExpr graphicsComplex = F.GraphicsComplex(pointsList, F.List(group));
        final IASTAppendable result = F.ast(S.Graphics3D);
        result.append(graphicsComplex);
        result.append(Rule(S.PlotRange, S.Automatic));
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Plot3D, rex, engine);
    }
    return F.NIL;
  }

  // Stubs for interface compliance
  private static IExpr createSurfaceGraphics(IAST ast, final EvalEngine engine) {
    return F.NIL;
  }

  public static IExpr plotArray(double xMin, double xMax, double yMin, double yMax, IExpr function,
      ISymbol xVar, ISymbol yVar, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.JSForm, S.PlotRange, S.ColorFunction, S.DataRange},
        new IExpr[] {S.False, S.Automatic, S.Automatic, S.Automatic});
  }
}
