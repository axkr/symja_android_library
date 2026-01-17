package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of ListPointPlot3D. Generates a Graphics3D object containing a GraphicsComplex.
 * Supports explicit coordinates {{x,y,z}...} and height arrays {{z...}...}.
 */
public class ListPointPlot3D extends AbstractFunctionOptionEvaluator {

  public ListPointPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr data = ast.arg1();
    if (!data.isList()) {
      return F.NIL;
    }

    // --- Option Parsing ---
    IExpr plotStyleOpt = S.Automatic;
    IExpr dataRangeOpt = S.Automatic;

    if (options.length > 0) {
      // options are pre-parsed by the engine into the array based on setUp() order
      // 0: PlotStyle, 1: DataRange
      if (options.length > 0)
        plotStyleOpt = options[0];
      if (options.length > 1)
        dataRangeOpt = options[1];
    }

    // --- Data Normalization ---
    // We need to determine if 'data' is:
    // 1. A single set of points {{x,y,z}, ...}
    // 2. A single height map {{z,z...}, {z,z...}}
    // 3. Multiple sets of points {{{x,y,z}...}, {{x,y,z}...}}
    // 4. Multiple height maps

    // Heuristic: Check the first element
    boolean isMultiDataset = false;
    boolean isHeightMap = false; // true if {z, z...}, false if {x,y,z}

    IAST listData = (IAST) data;
    if (listData.isEmpty()) {
      return F.Graphics3D(F.CEmptyList);
    }

    IExpr first = listData.arg1();
    if (first.isList()) {
      IAST firstList = (IAST) first;
      // If the first element is a coordinate {x,y,z}, then the whole data is one dataset of points.
      // If the first element is a list of coordinates {{x,y,z}...} or list of numbers {z,z...},
      // then 'data' contains multiple items or rows.

      if (isCoordinate(firstList)) {
        // Case: {{x,y,z}, {x,y,z}} -> Single Dataset, Explicit Coords
        isMultiDataset = false;
        isHeightMap = false;
      } else {
        // It's a list of lists.
        // Check content of firstList to distinguish "Multiple Datasets" from "Single Height Map"
        // If firstList contains numbers -> Single Height Map (rows of z)
        // If firstList contains coordinates -> Multiple Datasets of coords

        if (!firstList.isEmpty() && firstList.arg1().isList()) {
          // {{{x,y,z}...}, ...}
          isMultiDataset = true;
          isHeightMap = false;
        } else if (!firstList.isEmpty() && firstList.arg1().isNumber()) {
          // {{z1, z2...}, ...} -> Single Height Map
          isMultiDataset = false;
          isHeightMap = true;
        } else {
          // Fallback/Edge cases (e.g. empty lists)
          // Assume MultiDataset if structure suggests depth 3
          isMultiDataset = true;
        }
      }
    }

    // --- Processing ---

    // Master list of all points for GraphicsComplex
    IASTAppendable allPoints = F.ListAlloc();
    // List of primitives (Point[...]) to be added to GraphicsComplex
    IASTAppendable primitives = F.ListAlloc();

    int pointCounter = 0; // Tracks global index in allPoints (0-based for logic, 1-based for
                          // Mathematica)

    // Helper to wrap single dataset logic
    // If not multi-dataset, we treat 'data' as a list containing 1 dataset
    IAST datasets = isMultiDataset ? listData : F.List(listData);

    // Cycle styles if multiple datasets
    IAST styles = null;
    if (plotStyleOpt.isList()) {
      styles = (IAST) plotStyleOpt;
    }

    for (int i = 1; i < datasets.size(); i++) {
      IExpr currentData = datasets.get(i);
      if (!currentData.isList())
        continue;

      IAST datasetList = (IAST) currentData;
      IASTAppendable pointIndices = F.ListAlloc();

      if (isHeightMap) {
        // Process Grid: datasetList is {{z11, z12...}, {z21...}...}
        // Need DataRange for x, y mapping
        double xMin = 1.0, xMax = datasetList.argSize(); // Cols (inner size approx)
        double yMin = 1.0, yMax = datasetList.size() - 1; // Rows

        // Try Parse DataRange -> {{xmin, xmax}, {ymin, ymax}}
        if (dataRangeOpt.isList() && ((IAST) dataRangeOpt).size() == 3) {
          IAST dr = (IAST) dataRangeOpt;
          if (dr.get(1).isList() && dr.get(2).isList()) {
            // simple numeric eval
            try {
              IExpr xr = dr.get(1);
              IExpr yr = dr.get(2);
              xMin = ((IAST) xr).get(1).evalf();
              xMax = ((IAST) xr).get(2).evalf();
              yMin = ((IAST) yr).get(1).evalf();
              yMax = ((IAST) yr).get(2).evalf();
            } catch (RuntimeException rex) {
              Errors.printMessage(S.ListPlot3D, rex);
              return F.NIL;
            } // Fallback to defaults
          }
        }

        int rowCount = datasetList.size() - 1;
        for (int r = 1; r <= rowCount; r++) {
          IExpr rowExpr = datasetList.get(r);
          if (!rowExpr.isList())
            continue;
          IAST row = (IAST) rowExpr;
          int colCount = row.size() - 1;

          for (int c = 1; c <= colCount; c++) {
            IExpr zVal = row.get(c);
            if (zVal.isNumber()) {
              // Map indices to range
              // x corresponds to column index c
              // y corresponds to row index r
              double x = (colCount > 1) ? xMin + (c - 1) * (xMax - xMin) / (colCount - 1) : xMin;
              double y = (rowCount > 1) ? yMin + (r - 1) * (yMax - yMin) / (rowCount - 1) : yMin;

              allPoints.append(F.List(F.num(x), F.num(y), zVal));
              pointCounter++;
              pointIndices.append(F.ZZ(pointCounter));
            }
          }
        }

      } else {
        // Process Explicit Coordinates: datasetList is {{x,y,z}, ...}
        for (int k = 1; k < datasetList.size(); k++) {
          IExpr pt = datasetList.get(k);
          if (isCoordinate(pt)) {
            allPoints.append(pt);
            pointCounter++;
            pointIndices.append(F.ZZ(pointCounter));
          }
        }
      }

      // Create Primitive for this dataset
      if (pointIndices.size() > 1) {
        // Add Style Directive if present
        if (styles != null && styles.size() > 1) {
          // Cyclic indexing
          int styleIdx = (i - 1) % (styles.size() - 1) + 1;
          primitives.append(styles.get(styleIdx));
        } else if (plotStyleOpt.isAST() && !plotStyleOpt.isList()) {
          // Single directive for all (if passed as non-list, though typically PlotStyle is a list
          // or option)
          // If simple option passed like PlotStyle->Red (not in list), apply it.
          // Note: AbstractFunctionOptionEvaluator usually puts options in array.
          // If user said PlotStyle->Red, options[0] is Red.
          if (i == 1 && !plotStyleOpt.equals(S.Automatic))
            primitives.append(plotStyleOpt);
        }

        primitives.append(F.Point(pointIndices));
      }
    }

    // --- Result Construction ---
    IExpr graphicsComplex = F.GraphicsComplex(allPoints, primitives);
    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);

    // Append standard options
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(0.4)))); // Default for
                                                                                // ListPointPlot3D
    result.append(F.Rule(S.Axes, S.True));

    return result;
  }

  /**
   * Checks if an expression is a valid 3D coordinate {x, y, z}.
   */
  private boolean isCoordinate(IExpr expr) {
    return expr.isList() && ((IAST) expr).size() == 4 && // Head + 3 args
        ((IAST) expr).get(1).isNumber() && ((IAST) expr).get(2).isNumber()
        && ((IAST) expr).get(3).isNumber();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // Define options: PlotStyle, DataRange
    setOptions(newSymbol, new IBuiltInSymbol[] {S.PlotStyle, S.DataRange},
        new IExpr[] {S.Automatic, S.Automatic});
  }
}
