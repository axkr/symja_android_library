package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class SudokuSolve extends AbstractEvaluator {

  private final static class Sudoku {
    final private int board[][];
    private int boardSize;
    private int boxSize;
    private boolean rowSubset[][];
    private boolean columnSubset[][];
    private boolean boxSubset[][];

    public Sudoku(int intialBoard[][]) {
      board = intialBoard;
      boardSize = board.length;
      boxSize = (int) Math.sqrt(boardSize);
      initSubsets();
    }

    private void initSubsets() {
      rowSubset = new boolean[boardSize][boardSize];
      columnSubset = new boolean[boardSize][boardSize];
      boxSubset = new boolean[boardSize][boardSize];
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board.length; j++) {
          int value = board[i][j];
          if (value != 0) {
            setSubsetValue(i, j, value, true);
          }
        }
      }
    }

    private void setSubsetValue(int i, int j, int value, boolean present) {
      rowSubset[i][value - 1] = present;
      columnSubset[j][value - 1] = present;
      boxSubset[computeBoxNo(i, j)][value - 1] = present;
    }

    public boolean solve() {
      return solveRecursive(0, 0);
    }

    private boolean solveRecursive(int x, int y) {
      if (x == boardSize) {
        x = 0;
        if (++y == boardSize) {
          return true;
        }
      }
      if (board[x][y] != 0) {
        return solveRecursive(x + 1, y);
      }
      for (int value = 1; value <= boardSize; value++) {
        if (isValid(x, y, value)) {
          board[x][y] = value;
          setSubsetValue(x, y, value, true);
          if (solveRecursive(x + 1, y)) {
            return true;
          }
          setSubsetValue(x, y, value, false);
        }
      }

      board[x][y] = 0;
      return false;
    }

    private boolean isValid(int i, int j, int val) {
      val--;
      boolean isPresent =
          rowSubset[i][val] || columnSubset[j][val] || boxSubset[computeBoxNo(i, j)][val];
      return !isPresent;
    }

    private int computeBoxNo(int i, int j) {
      int boxRow = i / boxSize;
      int boxCol = j / boxSize;
      return boxRow * boxSize + boxCol;
    }

    public IAST getSolution() {
      IASTAppendable matrix9x9 = F.ListAlloc(9);
      matrix9x9.setEvalFlags(IAST.IS_MATRIX);
      for (int i = 0; i < boardSize; i++) {
        IASTAppendable row = F.ListAlloc(9);
        for (int j = 0; j < boardSize; j++) {
          row.append(board[i][j]);
        }
        matrix9x9.append(row);
      }

      return matrix9x9;
    }

  }

  public SudokuSolve() {
    // default ctor
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int[] dim = ast.arg1().isMatrix();
    if (ast.arg1().isAST() && dim != null && dim[0] == 9 && dim[1] == 9) {
      IAST matrix = (IAST) ast.arg1();
      int[][] board = new int[9][9];
      for (int i = 0; i < 9; i++) {
        IAST row = matrix.getAST(i + 1);
        for (int j = 0; j < 9; j++) {
          int value = row.get(j + 1).toIntDefault();
          if (value < 1 || value > 9) {
            board[i][j] = 0;
          } else {
            board[i][j] = value;
          }
        }
      }
      Sudoku sudoku = new Sudoku(board);
      if (sudoku.solve()) {
        return sudoku.getSolution();
      }
      // `1`.
      return Errors.printMessage(S.SudokuSolve, "error", F.List("The Sudoku is not solvable."),
          engine);
    }
    return F.NIL;
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

}
