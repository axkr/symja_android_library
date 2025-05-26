package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ListPolarPlot extends ListPlot {

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(options, engine);
    graphicsOptions.setYFunction(y -> F.Log10(y));
    graphicsOptions.setYScale("Log10");
    return graphicsOptions;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (ast.arg1().isList()) {
      IAST list = (IAST) ast.arg1();
      IASTAppendable table = createTable(engine, list, 0);
      if (table.isPresent()) {
        IASTMutable listPlot = ast.setAtCopy(1, table);
        GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
        IAST graphicsPrimitives = listPlot(listPlot, options, graphicsOptions, engine);
        if (graphicsPrimitives.isPresent()) {
          graphicsOptions.addPadding();
          return createGraphicsFunction(graphicsPrimitives, graphicsOptions);
        }
      }
    }
    return F.NIL;
  }

  private IASTAppendable createTable(EvalEngine engine, IAST list, int level) {
    int n = list.argSize();
    if (list.isListOfPoints(2)) {
      IASTAppendable table = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IAST elem = (IAST) list.get(i);
        IExpr xValue = engine.evaluate(F.Times(elem.arg2(), F.Cos(elem.arg1())));
        IExpr yValue = engine.evaluate(F.Times(elem.arg2(), F.Sin(elem.arg1())));
        table.append(F.List(xValue, yValue));
      }
      return table;
    } else if (list.isListOfLists()) {
      if (level != 0) {
        return F.NIL;
      }
      IASTAppendable listOfTables = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IAST subList = (IAST) list.get(i);
        IASTAppendable subTable = createTable(engine, subList, 1);
        if (subTable.isPresent()) {
          listOfTables.append(subTable);
        }
      }
      return listOfTables;
    } else {
      IASTAppendable table = F.ListAlloc(list.argSize());
      final double step = (Math.PI * 2.0) / n;
      double x = 0.0;
      for (int i = 1; i < list.size(); i++) {
        IExpr elem = list.get(i);
        // if (isNonReal(elem)) {
        // table.append(F.List(S.None, S.None));
        // } else {
        IExpr xValue = engine.evaluate(F.Times(elem, F.Cos(F.num(x))));
        IExpr yValue = engine.evaluate(F.Times(elem, F.Sin(F.num(x))));
        table.append(F.List(xValue, yValue));
        // }
        x += step;
      }
      return table;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(true, false));
  }
}
