/*
 * @(#)Sudoku.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.DefaultSolver;
import jp.ac.kobe_u.cs.cream.IntVariable;
import jp.ac.kobe_u.cs.cream.Network;
import jp.ac.kobe_u.cs.cream.NotEquals;
import jp.ac.kobe_u.cs.cream.Solution;
import jp.ac.kobe_u.cs.cream.Solver;

/**
 * Sudoku puzzle.
 *
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Sudoku {
  /*
   * The following problems 1..9 are made by Hirofumi Fujiwara.
   * http://www.pro.or.jp/~fuji/java/puzzle/numplace/index.html
   */
  static int[][][] ex = {
      // 0
      {{1, 2, 3, 0, 0, 0, 0, 0, 0}, {4, 5, 6, 0, 0, 0, 0, 0, 0}, {7, 8, 9, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}},
      // 1
      {{0, 9, 0, 0, 0, 0, 0, 1, 0}, {8, 0, 4, 0, 2, 0, 3, 0, 7}, {0, 6, 0, 9, 0, 7, 0, 2, 0},
          {0, 0, 5, 0, 3, 0, 1, 0, 0}, {0, 7, 0, 5, 0, 1, 0, 3, 0}, {0, 0, 3, 0, 9, 0, 8, 0, 0},
          {0, 2, 0, 8, 0, 5, 0, 6, 0}, {1, 0, 7, 0, 6, 0, 4, 0, 9}, {0, 3, 0, 0, 0, 0, 0, 8, 0}},
      // 2
      {{3, 7, 0, 1, 0, 0, 8, 2, 0}, {0, 0, 0, 7, 0, 8, 0, 0, 0}, {0, 8, 4, 0, 0, 9, 0, 5, 7},
          {9, 0, 0, 8, 7, 0, 1, 0, 0}, {5, 0, 7, 0, 0, 0, 4, 0, 3}, {0, 0, 8, 0, 1, 5, 0, 0, 2},
          {6, 3, 0, 5, 0, 0, 2, 4, 0}, {0, 0, 0, 4, 0, 3, 0, 0, 0}, {0, 4, 5, 0, 0, 1, 0, 7, 9}},
      // 3
      {{0, 0, 2, 7, 0, 0, 0, 0, 6}, {9, 0, 0, 0, 0, 0, 7, 2, 4}, {8, 4, 0, 0, 5, 6, 0, 0, 0},
          {0, 0, 1, 0, 0, 0, 0, 5, 9}, {2, 0, 4, 8, 0, 0, 0, 0, 0}, {0, 0, 0, 6, 9, 7, 2, 0, 0},
          {7, 5, 0, 0, 8, 0, 0, 3, 0}, {0, 8, 0, 3, 0, 0, 1, 0, 0}, {0, 0, 0, 0, 0, 0, 6, 9, 0}},
      // 4
      {{6, 3, 1, 0, 0, 8, 0, 0, 0}, {5, 0, 0, 0, 0, 7, 0, 0, 0}, {9, 0, 0, 4, 1, 2, 0, 0, 0},
          {0, 0, 8, 0, 0, 0, 1, 2, 3}, {0, 0, 2, 0, 0, 0, 4, 0, 0}, {3, 4, 5, 0, 0, 0, 6, 0, 0},
          {0, 0, 0, 5, 8, 6, 0, 0, 4}, {0, 0, 0, 1, 0, 0, 0, 0, 8}, {0, 0, 0, 7, 0, 0, 3, 6, 1}},
      // 5
      {{7, 0, 0, 0, 3, 0, 0, 0, 1}, {0, 4, 0, 7, 0, 6, 0, 3, 0}, {0, 0, 1, 0, 0, 0, 2, 0, 0},
          {0, 3, 0, 2, 0, 7, 0, 6, 0}, {2, 0, 0, 0, 6, 0, 0, 0, 9}, {0, 8, 0, 5, 0, 3, 0, 2, 0},
          {0, 0, 4, 0, 0, 0, 7, 0, 0}, {0, 1, 0, 3, 0, 9, 0, 8, 0}, {9, 0, 0, 0, 2, 0, 0, 0, 6}},
      // 6
      {{0, 0, 0, 1, 2, 6, 0, 0, 0}, {0, 0, 9, 0, 0, 0, 1, 0, 0}, {0, 5, 1, 0, 0, 0, 4, 2, 0},
          {4, 0, 0, 8, 0, 9, 0, 0, 3}, {1, 0, 0, 0, 6, 0, 0, 0, 2}, {9, 0, 0, 5, 1, 2, 0, 0, 8},
          {0, 8, 4, 0, 5, 0, 2, 3, 0}, {0, 0, 0, 0, 8, 0, 0, 0, 0}, {0, 0, 3, 2, 4, 1, 8, 0, 0}},
      // 7
      {{0, 9, 0, 0, 0, 0, 0, 0, 6}, {0, 1, 0, 4, 0, 3, 0, 0, 9}, {2, 0, 0, 6, 0, 0, 7, 4, 0},
          {7, 0, 5, 0, 0, 4, 0, 0, 0}, {0, 0, 0, 0, 0, 7, 0, 0, 2}, {0, 4, 0, 0, 0, 1, 0, 0, 0},
          {0, 5, 6, 8, 0, 0, 9, 0, 3}, {0, 3, 0, 7, 5, 0, 0, 1, 0}, {4, 0, 9, 0, 0, 0, 8, 2, 0}},
      // 8
      {{0, 0, 0, 0, 0, 0, 1, 5, 0}, {0, 0, 0, 0, 0, 1, 0, 0, 4}, {0, 2, 8, 0, 0, 7, 0, 0, 6},
          {6, 0, 0, 4, 0, 0, 9, 7, 0}, {8, 0, 0, 6, 0, 9, 0, 0, 5}, {0, 1, 4, 0, 0, 5, 0, 0, 3},
          {3, 0, 0, 9, 0, 0, 2, 8, 0}, {4, 0, 0, 2, 0, 0, 0, 0, 0}, {0, 5, 7, 0, 0, 0, 0, 0, 0}},
      // 9
      {{4, 5, 0, 0, 0, 0, 0, 3, 0}, {0, 0, 0, 3, 0, 0, 1, 4, 5}, {0, 0, 6, 1, 4, 0, 0, 0, 0},
          {6, 0, 0, 0, 0, 0, 8, 0, 0}, {0, 0, 2, 0, 0, 7, 5, 9, 0}, {0, 4, 8, 6, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 8, 0, 0, 7}, {0, 9, 0, 0, 6, 1, 4, 0, 0}, {8, 1, 7, 0, 0, 0, 0, 0, 0}},
      // 10
      {{8, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 7, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 1, 0, 0},
          {0, 6, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 9, 0, 0, 2, 0}, {0, 0, 0, 0, 0, 0, 6, 0, 0},
          {0, 0, 0, 0, 0, 4, 0, 0, 0}, {5, 0, 0, 0, 0, 0, 0, 0, 4}, {0, 0, 3, 0, 0, 0, 0, 0, 0}}};

  public static void sudoku(int[][] v0) {
    Network net = new Network();
    int n = 9;
    IntVariable[][] v = new IntVariable[n][n];
    IntVariable[] vs = new IntVariable[n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (v0[i][j] == 0)
          v[i][j] = new IntVariable(net, 1, n);
        else
          v[i][j] = new IntVariable(net, v0[i][j]);
      }
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++)
        vs[j] = v[i][j];
      new NotEquals(net, vs);
    }
    for (int j = 0; j < n; j++) {
      for (int i = 0; i < n; i++)
        vs[i] = v[i][j];
      new NotEquals(net, vs);
    }
    for (int i0 = 0; i0 < n; i0 += 3) {
      for (int j0 = 0; j0 < n; j0 += 3) {
        int k = 0;
        for (int i = i0; i < i0 + 3; i++)
          for (int j = j0; j < j0 + 3; j++)
            vs[k++] = v[i][j];
        new NotEquals(net, vs);
      }
    }
    Solver solver = new DefaultSolver(net);
    for (solver.start(); solver.waitNext(); solver.resume()) {
      Solution solution = solver.getSolution();
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++)
          System.out.print(solution.getIntValue(v[i][j]) + " ");
        System.out.println();
      }
      System.out.println();
    }
    solver.stop();
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java -classpath .:cream.jar Sudoku n");
      System.out.println(" n : problem number 0.." + (ex.length - 1));
      return;
    }
    for (int i = 0; i < args.length; ++i) {
      int m = Integer.parseInt(args[i]);
      if (0 <= m && m < ex.length) {
        System.out.println("***** Problem " + m);
        sudoku(ex[m]);
      }
    }
  }
}
