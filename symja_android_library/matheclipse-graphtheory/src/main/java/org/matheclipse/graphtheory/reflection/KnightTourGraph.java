package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>KnightTourGraph(m, n)</code> - the graph of the knight moves on a board of <code>m</code>
 * rows and <code>n</code> columns: the square in row <code>r</code> and column <code>c</code>
 * (counted from 0) is the vertex <code>r*n + c + 1</code>, two squares are joined if a knight jumps
 * from one to the other. The edges are sorted pairs; the graph is drawn on the grid of the board.
 */
public class KnightTourGraph extends AbstractFunctionEvaluator {

  private static final int[][] MOVES =
      {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int m = ast.arg1().toIntDefault();
    int n = ast.arg2().toIntDefault();
    if (m <= 0 || n <= 0) {
      return F.NIL;
    }
    // every square has at most 8 moves, every edge is counted from both ends
    GraphUtil.checkGraphSize((long) m * n, 4L * m * n);
    List<int[]> pairs = new ArrayList<int[]>();
    for (int r = 0; r < m; r++) {
      for (int c = 0; c < n; c++) {
        int from = r * n + c + 1;
        for (int[] move : MOVES) {
          int row = r + move[0];
          int column = c + move[1];
          if (row >= 0 && row < m && column >= 0 && column < n) {
            int to = row * n + column + 1;
            if (from < to) {
              pairs.add(new int[] {from, to});
            }
          }
        }
      }
    }
    IASTAppendable options = F.ListAlloc(1);
    IAST grid = F.list(F.stringx("GridEmbedding"),
        F.Rule(F.stringx("Dimension"), F.list(F.ZZ(n), F.ZZ(m))));
    options.append(F.Rule(S.GraphLayout, F.list(F.Rule(F.stringx("VertexLayout"), grid),
        F.Rule(F.stringx("PackingLayout"), S.None))));
    return GraphExpr.newInstance(GraphUtil.sortedPairGraph(m * n, pairs).toData(), options);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
