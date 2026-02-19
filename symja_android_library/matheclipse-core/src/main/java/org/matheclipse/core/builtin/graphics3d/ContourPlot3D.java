package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ColorFormat;

public class ContourPlot3D extends AbstractFunctionOptionEvaluator {

  public ContourPlot3D() {}

  private static int DEFAULT_POINTS = 20;
  // Vertices:
  // 0:000, 1:100, 2:110, 3:010 (Bottom Ring)
  // 4:001, 5:101, 6:111, 7:011 (Top Ring)
  private static final int[][] CORNERS =
      {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}};

  // Standard Marching Cubes Tables
  private static final int[][] EDGE_CONNECTIONS = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, // Bottom
      {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Top
      {0, 4}, {1, 5}, {2, 6}, {3, 7} // Vertical
  };

  // Vertex offsets (x,y,z) for 0..7
  private static final int[][] VERTEX_OFFSETS =
      {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}};

  // Table of edges to intersect for each of the 256 cube configurations
  // -1 indicates end of list
  private static final int[][] TRI_TABLE =
      {{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
          {3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
          {3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
          {3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
          {9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
          {1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
          {9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
          {2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
          {8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
          {9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
          {4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
          {3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
          {1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
          {4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
          {4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
          {1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
          {5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
          {2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
          {9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
          {0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
          {2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
          {10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
          {4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
          {5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
          {5, 4, 8, 5, 8, 11, 5, 11, 10, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 8, 8, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {9, 3, 0, 5, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 5, 7, 8, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 8, 8, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
          {10, 1, 2, 9, 3, 0, 5, 7, 8, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 10, 0, 10, 2, 8, 5, 7, -1, -1, -1, -1, -1, -1, -1},
          {7, 5, 3, 3, 5, 10, 3, 10, 2, -1, -1, -1, -1, -1, -1, -1},
          {2, 3, 11, 9, 5, 8, 8, 5, 7, -1, -1, -1, -1, -1, -1, -1},
          {9, 8, 5, 5, 8, 7, 11, 2, 3, 11, 0, 2, -1, -1, -1, -1},
          {2, 3, 11, 0, 1, 5, 5, 7, 8, -1, -1, -1, -1, -1, -1, -1},
          {1, 5, 2, 5, 11, 2, 5, 7, 11, 7, 8, 11, -1, -1, -1, -1},
          {9, 5, 8, 8, 5, 7, 10, 1, 3, 3, 1, 11, -1, -1, -1, -1},
          {1, 3, 10, 3, 11, 10, 9, 8, 0, 8, 7, 0, -1, -1, -1, -1},
          {0, 1, 10, 0, 10, 11, 0, 11, 3, 8, 5, 7, -1, -1, -1, -1},
          {7, 5, 11, 5, 10, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {8, 0, 3, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 8, 8, 3, 1, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {2, 10, 1, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1},
          {3, 0, 8, 2, 10, 1, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {2, 10, 9, 9, 0, 2, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {2, 10, 9, 9, 8, 2, 8, 3, 2, 13, 15, 14, 14, 12, 13},
          {3, 11, 2, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 11, 2, 8, 11, 0, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {9, 0, 1, 11, 2, 3, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {1, 11, 2, 1, 9, 11, 9, 8, 11, 13, 15, 14, 14, 12, 13},
          {3, 10, 1, 11, 10, 3, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {1, 0, 10, 0, 8, 10, 8, 11, 10, 13, 15, 14, 14, 12, 13},
          {0, 9, 3, 3, 9, 11, 11, 9, 10, 14, 12, 13, 13, 15, 14},
          {10, 11, 8, 8, 9, 10, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {4, 7, 8, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1, -1, -1, -1},
          {4, 3, 0, 7, 3, 4, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {9, 8, 4, 4, 1, 9, 4, 7, 1, 14, 15, 13, 13, 12, 14},
          {7, 3, 1, 4, 7, 1, 9, 4, 1, 14, 12, 13, 13, 15, 14},
          {2, 10, 1, 4, 7, 8, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {4, 7, 3, 4, 3, 0, 2, 10, 1, 14, 15, 13, 13, 12, 14},
          {2, 10, 9, 2, 9, 0, 4, 7, 8, 13, 15, 14, 14, 12, 13},
          {7, 9, 4, 7, 2, 9, 7, 3, 2, 7, 10, 3, 14, 12, 13, 13, 15, 14},
          {3, 11, 2, 4, 7, 8, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {7, 11, 4, 11, 2, 4, 2, 0, 4, 14, 15, 13, 13, 12, 14},
          {9, 0, 1, 11, 2, 3, 7, 8, 4, 14, 12, 13, 13, 15, 14},
          {7, 11, 4, 11, 9, 4, 11, 2, 9, 2, 1, 9, 14, 15, 13, 13, 12, 14},
          {3, 10, 1, 3, 11, 10, 4, 7, 8, 13, 15, 14, 14, 12, 13},
          {11, 10, 1, 11, 1, 4, 4, 1, 0, 4, 7, 11, 13, 15, 14, 14, 12, 13},
          {4, 7, 8, 11, 0, 3, 11, 9, 0, 11, 10, 9, 14, 12, 13, 13, 15, 14},
          {9, 10, 11, 9, 11, 4, 4, 11, 7, 14, 15, 13, 13, 12, 14},
          {12, 13, 14, 14, 13, 15, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
          {5, 4, 9, 0, 8, 3, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {1, 5, 4, 1, 0, 5, 12, 13, 14, 14, 13, 15, -1, -1, -1, -1},
          {5, 4, 8, 5, 8, 3, 5, 3, 1, 13, 15, 14, 14, 12, 13},
          {2, 10, 1, 5, 4, 9, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {1, 2, 10, 0, 8, 3, 4, 9, 5, 13, 15, 14, 14, 12, 13},
          {4, 0, 5, 0, 2, 5, 2, 10, 5, 13, 15, 14, 14, 12, 13},
          {5, 4, 8, 5, 8, 3, 5, 3, 2, 5, 2, 10, 14, 12, 13, 13, 15, 14},
          {5, 4, 9, 2, 3, 11, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {0, 11, 2, 0, 8, 11, 4, 9, 5, 14, 15, 13, 13, 12, 14},
          {2, 3, 11, 4, 0, 5, 0, 1, 5, 14, 12, 13, 13, 15, 14},
          {11, 2, 8, 2, 5, 8, 2, 1, 5, 11, 8, 5, 13, 15, 14, 14, 12, 13},
          {10, 3, 11, 10, 1, 3, 5, 4, 9, 14, 15, 13, 13, 12, 14},
          {4, 9, 5, 1, 8, 10, 8, 0, 1, 8, 11, 10, 13, 15, 14, 14, 12, 13},
          {0, 3, 11, 0, 11, 5, 5, 11, 10, 5, 10, 4, 14, 12, 13, 13, 15, 14},
          {5, 4, 8, 5, 8, 10, 10, 8, 11, 14, 15, 13, 13, 12, 14},
          {12, 13, 14, 14, 13, 15, 8, 5, 9, 8, 7, 5, -1, -1, -1, -1},
          {3, 0, 9, 5, 7, 8, 13, 15, 14, 14, 12, 13, -1, -1, -1, -1},
          {7, 8, 5, 0, 1, 5, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {7, 5, 3, 3, 5, 1, 14, 15, 13, 13, 12, 14, -1, -1, -1, -1},
          {5, 9, 8, 5, 8, 7, 2, 10, 1, 14, 12, 13, 13, 15, 14},
          {3, 0, 9, 8, 5, 7, 10, 1, 2, 13, 15, 14, 14, 12, 13},
          {2, 10, 1, 0, 8, 5, 8, 7, 5, 13, 15, 14, 14, 12, 13},
          {5, 3, 10, 5, 10, 2, 5, 7, 3, 13, 15, 14, 14, 12, 13},
          {2, 3, 11, 5, 9, 8, 5, 8, 7, 13, 15, 14, 14, 12, 13},
          {5, 8, 7, 5, 9, 8, 11, 2, 0, 2, 11, 3, 13, 15, 14, 14, 12, 13},
          {0, 1, 5, 2, 3, 11, 5, 7, 8, 13, 15, 14, 14, 12, 13},
          {1, 5, 2, 2, 5, 11, 11, 5, 7, 11, 7, 8, 14, 12, 13, 13, 15, 14},
          {5, 9, 8, 5, 8, 7, 1, 3, 10, 3, 11, 10, 13, 15, 14, 14, 12, 13},
          {0, 8, 9, 3, 11, 10, 10, 1, 3, 5, 8, 7, 6, 14, 15, -1},
          {1, 0, 10, 10, 0, 11, 11, 0, 8, 5, 7, 8, 6, 14, 15, -1},
          {11, 10, 5, 5, 7, 11, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 9, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 8, 3, 1, 9, 8, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {2, 10, 1, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, 1, 2, 10, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 2, 9, 2, 10, 9, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {2, 8, 3, 2, 10, 8, 8, 10, 9, 6, 14, 15, -1, -1, -1, -1},
          {3, 11, 2, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 11, 2, 0, 8, 11, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {9, 0, 1, 11, 2, 3, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 11, 2, 1, 9, 11, 11, 9, 8, 6, 14, 15, -1, -1, -1, -1},
          {1, 3, 10, 10, 3, 11, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 10, 1, 0, 8, 10, 10, 8, 11, 6, 14, 15, -1, -1, -1, -1},
          {0, 3, 9, 9, 3, 11, 11, 10, 9, 6, 14, 15, -1, -1, -1, -1},
          {8, 10, 11, 8, 9, 10, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {4, 7, 8, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {4, 3, 0, 4, 7, 3, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 7, 8, 4, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 4, 1, 4, 7, 1, 7, 3, 6, 14, 15, -1, -1, -1, -1},
          {1, 2, 10, 7, 8, 4, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {4, 7, 3, 4, 3, 0, 1, 2, 10, 6, 14, 15, -1, -1, -1, -1},
          {2, 10, 9, 2, 9, 0, 4, 7, 8, 6, 14, 15, -1, -1, -1, -1},
          {7, 9, 4, 7, 2, 9, 7, 3, 2, 7, 10, 3, 6, 14, 15, -1},
          {3, 11, 2, 7, 8, 4, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {2, 4, 11, 4, 7, 11, 4, 0, 2, 6, 14, 15, -1, -1, -1, -1},
          {1, 9, 0, 2, 3, 11, 4, 7, 8, 6, 14, 15, -1, -1, -1, -1},
          {7, 11, 4, 11, 9, 4, 11, 2, 9, 9, 2, 1, 6, 14, 15, -1},
          {1, 3, 10, 10, 3, 11, 4, 7, 8, 6, 14, 15, -1, -1, -1, -1},
          {1, 4, 11, 1, 11, 10, 1, 0, 4, 4, 11, 7, 6, 14, 15, -1},
          {0, 11, 9, 9, 11, 10, 9, 3, 0, 8, 4, 7, 6, 14, 15, -1},
          {4, 7, 11, 4, 11, 9, 9, 11, 10, 6, 14, 15, -1, -1, -1, -1},
          {9, 5, 4, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 4, 0, 8, 3, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 1, 5, 5, 4, 0, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {5, 4, 8, 5, 8, 3, 5, 3, 1, 6, 14, 15, -1, -1, -1, -1},
          {1, 2, 10, 9, 5, 4, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, 1, 2, 10, 4, 9, 5, 6, 14, 15, -1, -1, -1, -1},
          {0, 2, 10, 0, 10, 5, 5, 10, 4, 6, 14, 15, -1, -1, -1, -1},
          {2, 5, 3, 3, 5, 4, 3, 4, 8, 5, 2, 10, 6, 14, 15, -1},
          {9, 5, 4, 2, 3, 11, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {0, 11, 2, 0, 8, 11, 4, 9, 5, 6, 14, 15, -1, -1, -1, -1},
          {0, 1, 5, 5, 4, 0, 2, 3, 11, 6, 14, 15, -1, -1, -1, -1},
          {11, 2, 8, 2, 5, 8, 2, 1, 5, 8, 5, 4, 6, 14, 15, -1},
          {1, 3, 10, 10, 3, 11, 4, 9, 5, 6, 14, 15, -1, -1, -1, -1},
          {4, 9, 5, 0, 8, 1, 1, 8, 10, 10, 8, 11, 6, 14, 15, -1},
          {5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, 6, 14, 15, -1},
          {5, 4, 8, 5, 8, 11, 5, 11, 10, 6, 14, 15, -1, -1, -1, -1},
          {9, 5, 8, 8, 5, 7, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {9, 3, 0, 5, 7, 8, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {5, 7, 8, 0, 1, 5, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {1, 5, 3, 3, 5, 7, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {9, 5, 8, 8, 5, 7, 10, 1, 2, 6, 14, 15, -1, -1, -1, -1},
          {10, 1, 2, 9, 3, 0, 5, 7, 8, 6, 14, 15, -1, -1, -1, -1},
          {0, 1, 10, 0, 10, 2, 8, 5, 7, 6, 14, 15, -1, -1, -1, -1},
          {7, 5, 3, 3, 5, 10, 3, 10, 2, 6, 14, 15, -1, -1, -1, -1},
          {2, 3, 11, 9, 5, 8, 8, 5, 7, 6, 14, 15, -1, -1, -1, -1},
          {8, 7, 5, 8, 5, 9, 11, 2, 3, 0, 2, 11, 6, 14, 15, -1},
          {0, 1, 5, 5, 7, 8, 2, 3, 11, 6, 14, 15, -1, -1, -1, -1},
          {11, 5, 7, 11, 2, 5, 2, 1, 5, 7, 5, 8, 6, 14, 15, -1},
          {9, 5, 8, 8, 5, 7, 1, 3, 10, 10, 3, 11, 6, 14, 15, -1},
          {0, 8, 9, 3, 11, 10, 10, 1, 3, 5, 8, 7, 6, 14, 15, -1},
          {1, 0, 10, 10, 0, 11, 11, 0, 8, 5, 7, 8, 6, 14, 15, -1},
          {11, 10, 5, 5, 7, 11, 6, 14, 15, -1, -1, -1, -1, -1, -1, -1},
          {6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {15, 14, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {8, 3, 1, 8, 1, 9, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {1, 2, 10, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {3, 0, 8, 1, 2, 10, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {2, 10, 9, 2, 9, 0, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {3, 2, 8, 8, 2, 9, 2, 10, 9, 6, 15, 14, -1, -1, -1, -1},
          {11, 2, 3, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {2, 11, 0, 0, 11, 8, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 11, 2, 3, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 11, 11, 9, 8, 11, 2, 1, 6, 15, 14, -1, -1, -1, -1},
          {3, 10, 1, 3, 11, 10, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {1, 0, 10, 10, 0, 8, 10, 8, 11, 6, 15, 14, -1, -1, -1, -1},
          {0, 9, 3, 3, 9, 11, 11, 9, 10, 6, 15, 14, -1, -1, -1, -1},
          {9, 10, 11, 9, 11, 8, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {8, 4, 7, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {4, 3, 0, 4, 7, 3, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {1, 9, 0, 4, 7, 8, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {9, 4, 1, 1, 4, 7, 1, 7, 3, 6, 15, 14, -1, -1, -1, -1},
          {1, 2, 10, 4, 7, 8, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {3, 0, 4, 4, 0, 1, 1, 2, 10, 3, 4, 7, 6, 15, 14, -1},
          {9, 0, 2, 2, 10, 9, 8, 4, 7, 6, 15, 14, -1, -1, -1, -1},
          {9, 7, 4, 9, 2, 7, 9, 10, 2, 7, 2, 3, 6, 15, 14, -1},
          {11, 2, 3, 8, 4, 7, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {11, 2, 4, 4, 2, 0, 4, 7, 11, 6, 15, 14, -1, -1, -1, -1},
          {11, 2, 3, 9, 0, 1, 8, 4, 7, 6, 15, 14, -1, -1, -1, -1},
          {11, 2, 1, 1, 9, 11, 11, 9, 4, 11, 4, 7, 6, 15, 14, -1},
          {1, 3, 10, 10, 3, 11, 8, 4, 7, 6, 15, 14, -1, -1, -1, -1},
          {10, 1, 4, 4, 1, 0, 4, 11, 10, 4, 7, 11, 6, 15, 14, -1},
          {3, 0, 11, 11, 0, 9, 11, 9, 10, 8, 4, 7, 6, 15, 14, -1},
          {9, 10, 11, 9, 11, 4, 4, 11, 7, 6, 15, 14, -1, -1, -1, -1},
          {6, 15, 14, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {0, 8, 3, 6, 15, 14, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
          {1, 5, 6, 1, 6, 14, 1, 14, 0, 14, 15, 0, -1, -1, -1, -1},
          {3, 1, 5, 5, 8, 3, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {4, 9, 5, 10, 1, 2, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {2, 10, 1, 3, 0, 8, 5, 4, 9, 6, 15, 14, -1, -1, -1, -1},
          {5, 6, 15, 5, 15, 0, 0, 15, 14, 5, 0, 2, 2, 10, 5},
          {2, 5, 3, 3, 5, 4, 3, 4, 8, 6, 15, 14, -1, -1, -1, -1},
          {11, 2, 3, 4, 9, 5, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {11, 2, 0, 0, 2, 5, 5, 2, 4, 4, 2, 9, 0, 5, 8, 6, 15, 14},
          {11, 2, 3, 0, 1, 5, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {2, 8, 11, 8, 2, 5, 5, 2, 1, 6, 15, 14, -1, -1, -1, -1},
          {5, 4, 9, 3, 10, 1, 10, 3, 11, 6, 15, 14, -1, -1, -1, -1},
          {1, 8, 10, 8, 1, 5, 5, 1, 4, 1, 0, 4, 8, 5, 11, 6, 15, 14},
          {5, 0, 1, 5, 3, 0, 5, 11, 3, 5, 10, 11, 6, 15, 14, -1},
          {8, 5, 10, 10, 5, 11, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {7, 8, 5, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
          {5, 7, 8, 3, 0, 9, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {7, 6, 15, 7, 15, 5, 5, 15, 0, 0, 15, 14, 5, 0, 1},
          {5, 7, 3, 3, 7, 6, 3, 6, 15, 3, 15, 14, 3, 14, 1},
          {1, 2, 10, 7, 8, 5, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {5, 7, 8, 1, 2, 10, 3, 0, 9, 6, 15, 14, -1, -1, -1, -1},
          {2, 10, 1, 7, 6, 15, 15, 14, 7, 7, 14, 5, 5, 14, 0, 0, 8, 5},
          {5, 3, 10, 5, 10, 2, 5, 7, 3, 7, 6, 3, 3, 6, 15, 3, 15, 14},
          {11, 2, 3, 7, 8, 5, 6, 15, 14, -1, -1, -1, -1, -1, -1, -1},
          {3, 11, 2, 0, 9, 5, 5, 9, 8, 8, 7, 5, 6, 15, 14, -1},
          {5, 7, 6, 6, 15, 5, 5, 15, 1, 1, 15, 14, 1, 14, 0, 2, 3, 11},
          {5, 7, 11, 11, 7, 6, 11, 6, 15, 11, 15, 14, 11, 14, 1, 1, 2, 11},
          {8, 5, 7, 3, 10, 1, 10, 3, 11, 6, 15, 14, -1, -1, -1, -1},
          {10, 1, 3, 11, 10, 3, 0, 9, 5, 5, 9, 8, 5, 8, 7, 6, 15, 14},
          {11, 5, 7, 5, 11, 10, 5, 10, 1, 5, 1, 0, 6, 15, 14, -1},
          {5, 7, 6, 5, 6, 15, 5, 15, 14, 5, 14, 10, 10, 14, 11, 10, 11, 9}};

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    // ContourPlot3D[f, {x, min, max}, {y, min, max}, {z, min, max}]
    if (argSize < 4)
      return F.NIL;

    IExpr funcExpr = ast.arg1();
    IExpr xRange = ast.arg2();
    IExpr yRange = ast.arg3();
    IExpr zRange = ast.arg4();

    if (!xRange.isList() || !yRange.isList() || !zRange.isList())
      return F.NIL;

    // Handle Equation f == g -> f - g
    if (funcExpr.isAST(S.Equal, 3)) {
      IExpr lhs = funcExpr.getAt(1);
      IExpr rhs = funcExpr.getAt(2);
      funcExpr = F.Subtract(lhs, rhs);
    }

    ISymbol xVar = (ISymbol) ((IAST) xRange).arg1();
    ISymbol yVar = (ISymbol) ((IAST) yRange).arg1();
    ISymbol zVar = (ISymbol) ((IAST) zRange).arg1();

    double xMin = ((IAST) xRange).arg2().evalDouble();
    double xMax = ((IAST) xRange).arg3().evalDouble();
    double yMin = ((IAST) yRange).arg2().evalDouble();
    double yMax = ((IAST) yRange).arg3().evalDouble();
    double zMin = ((IAST) zRange).arg2().evalDouble();
    double zMax = ((IAST) zRange).arg3().evalDouble();

    int plotPoints = DEFAULT_POINTS;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    }

    IExpr colorFunction = options[2];
    if (colorFunction.equals(S.Automatic)) {
      colorFunction = F.stringx("Sunset");
    }

    // Generate Scalar Field
    int resX = plotPoints;
    int resY = plotPoints;
    int resZ = plotPoints;

    double[][][] scalarField = new double[resX][resY][resZ];
    double dx = (xMax - xMin) / (resX - 1);
    double dy = (yMax - yMin) / (resY - 1);
    double dz = (zMax - zMin) / (resZ - 1);

    // Evaluate function on grid
    for (int i = 0; i < resX; i++) {
      double x = xMin + i * dx;
      for (int j = 0; j < resY; j++) {
        double y = yMin + j * dy;
        for (int k = 0; k < resZ; k++) {
          double z = zMin + k * dz;

          // Eval f(x,y,z)
          IExpr subst = F.subst(funcExpr,
              F.List(F.Rule(xVar, F.num(x)), F.Rule(yVar, F.num(y)), F.Rule(zVar, F.num(z))));
          // Use evalN for purely numerical result
          try {
            scalarField[i][j][k] = engine.evalN(subst).evalDouble();
          } catch (Exception e) {
            scalarField[i][j][k] = Double.NaN;
          }
        }
      }
    }

    // Marching Cubes
    return marchingCubes(scalarField, xMin, xMax, yMin, yMax, zMin, zMax, dx, dy, dz, 0.0,
        colorFunction, engine);
  }

  private IExpr marchingCubes(double[][][] field, double xMin, double xMax, double yMin, double yMax,
      double zMin, double zMax, double dx, double dy, double dz, double isoLevel,
      IExpr colorFunction, EvalEngine engine) {

    List<IExpr> vertices = new ArrayList<>();
    List<IExpr> vertexColors = new ArrayList<>();
    Map<String, Integer> vertexMap = new HashMap<>(); // "ix,iy,iz,edgeIndex" -> index
    IASTAppendable polygons = F.ListAlloc();

    int dimX = field.length;
    int dimY = field[0].length;
    int dimZ = field[0][0].length;

    // Resolve gradient if string
    ColorDataGradients gradient = null;
    if (colorFunction.isString()) {
      try {
        String name = colorFunction.toString();
        gradient = ColorDataGradients.valueOf(name.toUpperCase(Locale.US));
      } catch (IllegalArgumentException e) {
        // Fallback or custom string
        try {
          String name = colorFunction.toString().replace(" ", "").replace("_", "")
              .toUpperCase(Locale.US);
          for (ColorDataGradients g : ColorDataGradients.values()) {
            String gName = g.name().replace("_", "");
            if (gName.equals(name)) {
              gradient = g;
              break;
            }
          }
        } catch (Exception ex) {

        }
      }
    }

    for (int i = 0; i < dimX - 1; i++) {
      for (int j = 0; j < dimY - 1; j++) {
        for (int k = 0; k < dimZ - 1; k++) {

          // Extract values in order v0..v7 using proper CORNERS
          double[] vals = new double[8];
          for (int v = 0; v < 8; v++) {
            vals[v] = field[i + CORNERS[v][0]][j + CORNERS[v][1]][k + CORNERS[v][2]];
          }

          // Determine cube index
          int cubeIndex = 0;
          if (vals[0] < isoLevel)
            cubeIndex |= 1;
          if (vals[1] < isoLevel)
            cubeIndex |= 2;
          if (vals[2] < isoLevel)
            cubeIndex |= 4;
          if (vals[3] < isoLevel)
            cubeIndex |= 8;
          if (vals[4] < isoLevel)
            cubeIndex |= 16;
          if (vals[5] < isoLevel)
            cubeIndex |= 32;
          if (vals[6] < isoLevel)
            cubeIndex |= 64;
          if (vals[7] < isoLevel)
            cubeIndex |= 128;

          if (cubeIndex == 0 || cubeIndex == 255)
            continue; // Completely inside or outside

          // Lookup edges
          int[] triEdges = TRI_TABLE[cubeIndex];

          // Generate triangles
          for (int t = 0; t < 15; t += 3) {
            if (triEdges[t] == -1) {
              break;
            }

            int idx1 = getOrCreateVertex(i, j, k, triEdges[t], vals, xMin, xMax, yMin, yMax, zMin,
                zMax, dx, dy, dz, isoLevel, vertices, vertexColors, vertexMap, gradient,
                colorFunction, engine);
            int idx2 = getOrCreateVertex(i, j, k, triEdges[t + 1], vals, xMin, xMax, yMin, yMax,
                zMin, zMax, dx, dy, dz, isoLevel, vertices, vertexColors, vertexMap, gradient,
                colorFunction, engine);
            int idx3 = getOrCreateVertex(i, j, k, triEdges[t + 2], vals, xMin, xMax, yMin, yMax,
                zMin, zMax, dx, dy, dz, isoLevel, vertices, vertexColors, vertexMap, gradient,
                colorFunction, engine);

            if (idx1 != -1 && idx2 != -1 && idx3 != -1) {
              polygons.append(F.List(F.ZZ(idx1), F.ZZ(idx2), F.ZZ(idx3)));
            }
          }
        }
      }
    }

    // Convert vertices list to IAST
    IASTAppendable pointsList = F.ListAlloc(vertices.size());
    for (IExpr v : vertices) {
      pointsList.append(v);
    }

    // Convert colors list if available
    IASTAppendable colorsList = null;
    if (vertexColors.size() == vertices.size()) {
      colorsList = F.ListAlloc(vertexColors.size());
      for (IExpr c : vertexColors) {
        colorsList.append(c);
      }
    }

    // Group pointing to polygons.
    // If we have vertex colors we don't need a default face color if GraphicsComplex uses VertexColors
    IASTAppendable polyGroup = F.ListAlloc(2);
    if (colorsList == null) {
      polyGroup.append(F.RGBColor(F.num(0.6), F.num(0.8), F.num(1.0)));
    }
    polyGroup.append(F.Polygon(polygons));

    IExpr graphicsComplex;
    if (colorsList != null) {
      graphicsComplex =
          F.GraphicsComplex(pointsList, F.List(polyGroup), F.Rule(S.VertexColors, colorsList));
    } else {
      graphicsComplex = F.GraphicsComplex(pointsList, F.List(polyGroup));
    }
    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(1))));
    result.append(F.Rule(S.Axes, S.True));

    return result;
  }

  private int getOrCreateVertex(int i, int j, int k, int edgeIndex, double[] vals, double xMin,
      double xMax, double yMin, double yMax, double zMin, double zMax, double dx, double dy,
      double dz, double iso, List<IExpr> vertices, List<IExpr> vertexColors,
      Map<String, Integer> map, ColorDataGradients gradient, IExpr colorFunction,
      EvalEngine engine) {

    // Safety check for invalid edge index
    if (edgeIndex < 0 || edgeIndex >= 12)
      return -1;

    // Unique key for edge: cell coord + edge index
    String key = i + "," + j + "," + k + ":" + edgeIndex;
    if (map.containsKey(key)) {
      return map.get(key);
    }

    int v1 = EDGE_CONNECTIONS[edgeIndex][0];
    int v2 = EDGE_CONNECTIONS[edgeIndex][1];

    double val1 = vals[v1];
    double val2 = vals[v2];

    // Linear interpolation
    double mu = 0.5;
    if (Math.abs(val2 - val1) > 1e-6) {
      mu = (iso - val1) / (val2 - val1);
    }

    int[] c1 = CORNERS[v1];
    int[] c2 = CORNERS[v2];

    double gx = c1[0] + mu * (c2[0] - c1[0]);
    double gy = c1[1] + mu * (c2[1] - c1[1]);
    double gz = c1[2] + mu * (c2[2] - c1[2]);

    double px = xMin + (i + gx) * dx;
    double py = yMin + (j + gy) * dy;
    double pz = zMin + (k + gz) * dz;

    vertices.add(F.List(F.num(px), F.num(py), F.num(pz)));

    // Calculate color
    IExpr color = null;
    double nx = (px - xMin) / (xMax - xMin);
    double ny = (py - yMin) / (yMax - yMin);
    double nz = (pz - zMin) / (zMax - zMin);

    try {
      if (gradient != null) {
        // Gradient maps [0,1] -> Color. Use nz (Z-coordinate) by default standard
        IExpr rgbaExpr = gradient.apply(F.num(nz));
        if (rgbaExpr.isAST() && rgbaExpr.size() >= 4) {
          IAST rgba = (IAST) rgbaExpr;
          // Convert 0..255 to 0..1 for RGBColor
          org.matheclipse.core.convert.RGBColor c = ColorFormat.toColor(rgba);
          color = F.RGBColor(F.num(c.getRed() / 255.0), F.num(c.getGreen() / 255.0),
              F.num(c.getBlue() / 255.0));
        }
      } else if (colorFunction != null && !colorFunction.equals(S.None)) {
        // Evaluate ColorFunction[x, y, z]
        // Note: Check ColorFunctionScaling options if needed, here assuming Scaled (0..1)
        IExpr res = engine.evaluate(F.function(colorFunction, F.num(nx), F.num(ny), F.num(nz)));
        if (res.isAST() && (res.head().equals(S.RGBColor) || res.head().equals(S.Hue)
            || res.head().equals(S.GrayLevel))) {
          color = res;
        }
      }
    } catch (Exception e) {
    }

    if (color != null) {
      vertexColors.add(color);
    } else {
      // Fallback
      vertexColors.add(F.RGBColor(F.num(0.6), F.num(0.8), F.num(1.0)));
    }

    int newIndex = vertices.size(); // 1-based
    map.put(key, newIndex);
    return newIndex;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_4_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic});
  }

}
