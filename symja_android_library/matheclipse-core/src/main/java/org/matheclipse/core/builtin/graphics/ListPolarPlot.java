package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ListPolarPlot extends ListPlot {

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final IBuiltInSymbol[] optionSymbols, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(optionSymbols, options, engine);
    graphicsOptions.setYFunction(y -> F.Log10(y));
    graphicsOptions.setYScale("Log10");
    return graphicsOptions;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    ast = withQuantityMagnitudes(withDatasetRows(ast), originalAST, engine);
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    PlotWrapper outerWrapper = PlotWrapper.of(ast.arg1());
    if (outerWrapper.datum.isList()) {
      IAST list = (IAST) outerWrapper.datum;
      IASTAppendable table = createTable(engine, list, 0);
      if (table.isPresent()) {
        // a wrapper around the whole argument labels what it holds, so it is put back on the
        // converted table rather than lost with the polar coordinates; ListPlot already knows how
        // to read a wrapped dataset
        IASTMutable listPlot = ast.setAtCopy(1, outerWrapper.decorate(table));

        GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
        // PlotMarkers and Mesh are family options appended after the positional block, so they
        // are read from the call rather than by index
        graphicsOptions
            .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
        graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
        graphicsOptions.readColorFunction(originalAST);
        graphicsOptions.applyPlotTheme(originalAST);
        IAST graphicsPrimitives = listPlot(listPlot, options, graphicsOptions, engine);
        if (graphicsPrimitives.isPresent()) {
          graphicsOptions.addPadding();
          return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
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
        // the wrapper has to come off before the pair is read as {theta, r}: it is the argument
        // of the wrapper that holds the angle, not the wrapper itself
        PlotWrapper wrapper = PlotWrapper.of(list.get(i));
        IAST elem = (IAST) wrapper.datum;
        IExpr xValue = engine.evaluate(F.Times(elem.arg2(), F.Cos(elem.arg1())));
        IExpr yValue = engine.evaluate(F.Times(elem.arg2(), F.Sin(elem.arg1())));
        // the cartesian point is put back into the wrapper it came out of, so the label travels
        // with the point it belongs to through the ordinary ListPlot path
        table.append(wrapper.decorate(F.List(xValue, yValue)));
      }
      return table;
    } else if (list.isListOfLists()) {
      if (level != 0) {
        return F.NIL;
      }
      IASTAppendable listOfTables = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        PlotWrapper wrapper = PlotWrapper.of(list.get(i));
        if (!wrapper.datum.isList()) {
          continue;
        }
        IAST subList = (IAST) wrapper.datum;
        IASTAppendable subTable = createTable(engine, subList, 1);
        if (subTable.isPresent()) {
          listOfTables.append(wrapper.decorate(subTable));
        }
      }
      return listOfTables;
    } else {
      IASTAppendable table = F.ListAlloc(list.argSize());
      final double step = (Math.PI * 2.0) / n;
      double x = 0.0;
      for (int i = 1; i < list.size(); i++) {
        PlotWrapper wrapper = PlotWrapper.of(list.get(i));
        IExpr elem = wrapper.datum;
        // if (isNonReal(elem)) {
        // table.append(F.List(S.None, S.None));
        // } else {
        IExpr xValue = engine.evaluate(F.Times(elem, F.Cos(F.num(x))));
        IExpr yValue = engine.evaluate(F.Times(elem, F.Sin(F.num(x))));
        table.append(wrapper.decorate(F.List(xValue, yValue)));
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
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // a polar curve reaches its own extent in every direction, so clipping it to the plot range
    // would cut the outermost points off
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.listPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
