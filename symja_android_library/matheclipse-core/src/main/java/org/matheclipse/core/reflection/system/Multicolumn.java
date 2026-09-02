package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * Multicolumn(list, cols)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * lay <code>list</code> out in <code>cols</code> columns.
 *
 * </blockquote>
 *
 * <pre>
 * Multicolumn(list, {rows, Automatic})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * lay <code>list</code> out in <code>rows</code> rows, in as many columns as that takes.
 *
 * </blockquote>
 *
 * <pre>
 * Multicolumn(list)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * lay <code>list</code> out in a roughly square arrangement.
 *
 * </blockquote>
 *
 * <p>
 * The list is read down the columns: the first column is filled, then the second, and so on.
 * <code>Appearance -&gt; "Horizontal"</code> reads it across the rows instead.
 *
 * <p>
 * The result is a {@link S#Grid} of the arrangement, which is where the shape lives - a
 * <code>Multicolumn</code> says nothing that a <code>Grid</code> of the same cells does not.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Multicolumn({1, 2, 3, 4, 5, 6}, 3)
 * Grid({{1,3,5},{2,4,6}})
 *
 * &gt;&gt; Multicolumn({1, 2, 3, 4, 5, 6}, 3, Appearance -&gt; "Horizontal")
 * Grid({{1,2,3},{4,5,6}})
 *
 * &gt;&gt; Multicolumn({1, 2, 3, 4})
 * Grid({{1,3},{2,4}})
 * </pre>
 */
public class Multicolumn extends AbstractFunctionEvaluator {

  /** A cell with nothing in it, where the arrangement is wider than the list is long. */
  private static final IExpr EMPTY_CELL = F.$str("");

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // a Dataset lays its rows out - see IASTDataset#onDatasetRows. The result is a Grid and not a
    // collection, so restoreDataset leaves it alone
    IExpr onRows = IASTDataset.onDatasetRows(ast, engine);
    if (onRows.isPresent()) {
      return onRows;
    }
    if (!ast.arg1().isList()) {
      return F.NIL;
    }
    final IAST list = (IAST) ast.arg1();
    final int size = list.argSize();

    boolean horizontal = false;
    int last = ast.argSize();
    while (last >= 2 && ast.get(last).isRuleAST() && ast.get(last).first() == S.Appearance) {
      horizontal = ast.get(last).second().toString().equalsIgnoreCase("Horizontal");
      last--;
    }

    int rows;
    int columns;
    if (last >= 2) {
      IExpr spec = ast.get(2);
      if (spec.isList2()) {
        // {rows, cols} - and {rows, Automatic}, where the columns are however many that takes
        rows = spec.first().toIntDefault();
        columns = spec.second().isAutomatic() ? -1 : spec.second().toIntDefault();
        if (rows <= 0) {
          return F.NIL;
        }
        if (columns <= 0) {
          columns = ceilDivide(size, rows);
        } else if (rows * columns < size) {
          // more elements than cells. Widening the arrangement shows all of them, where honouring
          // both numbers would drop the ones that did not fit - and silently losing what the
          // caller passed in is the worse of the two
          columns = ceilDivide(size, rows);
        }
      } else {
        columns = spec.toIntDefault();
        if (columns <= 0) {
          return F.NIL;
        }
        rows = ceilDivide(size, columns);
      }
    } else {
      // "roughly square"
      columns = (int) Math.ceil(Math.sqrt(size));
      if (columns <= 0) {
        columns = 1;
      }
      rows = ceilDivide(size, columns);
    }
    if (rows <= 0) {
      // nothing to lay out - still a Grid, so that the result has one shape whatever it holds
      return F.unaryAST1(S.Grid, F.CEmptyList);
    }

    IASTAppendable grid = F.ListAlloc(rows);
    for (int row = 0; row < rows; row++) {
      IASTAppendable cells = F.ListAlloc(columns);
      for (int column = 0; column < columns; column++) {
        // down the columns, unless Appearance asked for across the rows
        int index = horizontal ? row * columns + column : column * rows + row;
        cells.append(index < size ? list.get(index + 1) : EMPTY_CELL);
      }
      grid.append(cells);
    }
    return F.unaryAST1(S.Grid, grid);
  }

  private static int ceilDivide(int size, int parts) {
    return parts <= 0 ? 0 : (size + parts - 1) / parts;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
