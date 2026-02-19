package org.matheclipse.core.eval;

import java.util.Arrays;
import java.util.Locale;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.IGraphics2D;
import org.matheclipse.core.graphics.IGraphics3D;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphicsUtil {

  /**
   * Automatic y-plot range determination based on robust percentiles (10th-90th) to handle
   * singularities and exponential growth gracefully.
   *
   * @param function the current function
   * @param values current y-values of the current function curve
   * @param yMinMax y-plot range which will be updated [min, max]
   */
  public static void automaticPlotRange2D(final IExpr function, final double[] values,
      double[] yMinMax) {
    if (values == null || values.length == 0) {
      return; // No data to analyze
    }

    if (function != null) {
      int headID = function.headID();
      switch (headID) {
        case ID.Cot:
        case ID.Csc:
        case ID.Sec:
        case ID.Tan:
          setYRange(-7.0, 7.0, yMinMax);
          return;
      }
    }

    // Filter and Sort Data
    // We need a sorted list of finite values to determine percentiles.
    double[] sorted = Arrays.stream(values).filter(Double::isFinite).sorted().toArray();
    int n = sorted.length;

    if (n == 0) {
      return; // All NaNs
    }

    // Identify Core Distribution (10th to 90th percentile)
    // This ignores the extreme tails (asymptotes or exponential explosions).
    double p10 = sorted[(int) (n * 0.10)];
    double p90 = sorted[(int) (n * 0.90)];
    double median = sorted[n / 2];

    double bodyRange = p90 - p10;

    // Handle flat functions (e.g., y=5)
    if (bodyRange < 1.0e-9) {
      double margin = Math.abs(median) * 0.1;
      if (margin < 1.0e-9)
        margin = 1.0;
      setYRange(median - margin, median + margin, yMinMax);
      return;
    }

    // Define "Reasonable" Visual Bounds
    // Expand the core body by a factor (1.5x) to include "interesting" variation
    // but cut off extreme outliers found in the top/bottom 10%.
    double expansionFactor = 1.5;
    double proposedMin = p10 - (bodyRange * expansionFactor);
    double proposedMax = p90 + (bodyRange * expansionFactor);

    // Clamp to Actual Data Limits
    // We never want to show a range *larger* than the actual data exists (empty whitespace).
    // But we *do* want to show a range *smaller* than data if data has singularities.
    double actualMin = sorted[0];
    double actualMax = sorted[n - 1];

    // If proposed bound extends beyond actual data, clamp it.
    // If proposed bound is inside actual data (cutting off singularity), keep it.
    double finalMin = Math.max(proposedMin, actualMin);
    double finalMax = Math.min(proposedMax, actualMax);

    if (actualMin >= 0 && finalMin < 0) {
      finalMin = actualMin;
    }

    setYRange(finalMin, finalMax, yMinMax);
  }

  /**
   * Compute the visible plot range of a sorted list of values.
   *
   * @param values unsorted values
   * @return the min and max value (possibly clipped to exclude outliers)
   */
  public static double[] automaticPlotRange3D(double[] values) {
    if (values == null || values.length == 0) {
      return new double[] {0.0, 1.0};
    }
    Arrays.sort(values);
    int size = values.length;
    double min = values[0];
    double max = values[size - 1];
    if (size > 10) {
      double q1 = values[size / 4];
      double q3 = values[(size * 3) / 4];
      double iqr = q3 - q1;
      if (iqr > 10.0 * Math.ulp(q3)) {
        // Upper fence. 2.0 * IQR is a heuristic to include most data but cut massive poles
        double upperFence = q3 + 2.0 * iqr;
        if (max > upperFence) {
          int idx = Arrays.binarySearch(values, upperFence);
          if (idx < 0) {
            idx = -idx - 1;
          }
          if (idx < size) {
            max = values[idx];
            // Ensure we don't accidentally clamp below the fence if binary search lands oddly
            if (max < upperFence) {
              max = upperFence;
            }
          }
        }
      }
    }
    return new double[] {min, max};
  }

  public static boolean export2DRecursive(ArrayNode arrayNode, IAST list, int startPosition,
      int endPosition, GraphicsOptions graphicsOptions) {
    for (int i = startPosition; i < endPosition; i++) {
      IExpr arg = list.get(i);
      if (arg.isAST()) {
        IAST ast = (IAST) arg;
        if (ast.isList()) {
          if (export2DRecursive(arrayNode, ast, 1, ast.size(), graphicsOptions)) {
          }
        } else if (ast.isASTSizeGE(S.Style, 2)) {
          GraphicsOptions styledGraphicsOptions = graphicsOptions.copy();
          if (export2DRecursive(arrayNode, ast, 2, ast.size(), styledGraphicsOptions)) {
            if (export2DRecursive(arrayNode, ast, 1, 2, styledGraphicsOptions)) {
            }
          }
        } else if (ast.isASTSizeGE(S.EdgeForm, 2)) {
          IAST edgeFormList = ast.arg1().makeList();
          ObjectNode edgeForm = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
          ObjectNode edgeList = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
          for (int j = 1; j < edgeFormList.size(); j++) {
            IExpr expr = edgeFormList.get(i);
            if (expr.isAST(S.Opacity, 2)) {
              ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
              double opacity = expr.toDoubleDefault(1.0);
              edgeList.put("opacity", opacity);
            } else if (expr.isAST(S.RGBColor)) {
              ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
              if (expr.isAST(S.RGBColor, 4, 5)) {
                double red = ((IAST) expr).arg1().toDoubleDefault(0.0);
                double green = ((IAST) expr).arg2().toDoubleDefault(0.0);
                double blue = ((IAST) expr).arg3().toDoubleDefault(0.0);
                ArrayNode array = g.arrayNode();
                array.add(red);
                array.add(green);
                array.add(blue);
                edgeList.set("color", array);
              } else if (expr.isAST(S.RGBColor, 1) && expr.first().isList3()) {
                IAST list4 = (IAST) expr.first();
                double red = list4.arg1().toDoubleDefault(0.0);
                double green = list4.arg2().toDoubleDefault(0.0);
                double blue = list4.arg3().toDoubleDefault(0.0);
                ArrayNode array = g.arrayNode();
                array.add(red);
                array.add(green);
                array.add(blue);
                edgeList.set("color", array);
              }
            } else if (expr.isAST(S.GrayLevel, 2, 3)) {
              RGBColor rgb = null;
              IAST grayLevel = (IAST) expr;
              if (grayLevel.isAST1() || grayLevel.isAST2()) {
                ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
                rgb = RGBColor.getGrayLevel((float) grayLevel.arg1().evalf());
                ArrayNode array = g.arrayNode();
                array.add(rgb.getRed() / 255.0);
                array.add(rgb.getGreen() / 255.0);
                array.add(rgb.getBlue() / 255.0);
                edgeList.set("color", array);

                if (grayLevel.isAST2()) {
                  g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
                  double opacity = grayLevel.arg2().toDoubleDefault(1.0);
                  edgeList.put("opacity", opacity);
                }
              }
            }
          }
          edgeForm.set("edgeForm", edgeList);
          arrayNode.add(edgeForm);
        } else if (ast.isAST(S.Hue, 2, 5)) {
          ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
          if (graphicsOptions.setHueColor(arrayNode, ast)) {
            arrayNode.add(g);
          }
        } else if (ast.isAST(S.GrayLevel, 2, 3)) {
          ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
          if (GraphicsOptions.setGrayLevel(g, ast)) {
            arrayNode.add(g);
          }
          double opacity = 1.0;
          if (ast.isAST2()) {
            opacity = ast.arg2().evalf();
          }
          GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        } else if (ast.isRGBColor()) {
          // ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
          GraphicsOptions.setColorOption(arrayNode, ast);
          // arrayNode.add(g);
        } else if (ast.isAST(S.Opacity, 2)) {
          double opacity = graphicsOptions.opacity();
          try {
            opacity = ast.arg1().evalf();
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
          GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        } else if (ast.isAST(S.PointSize, 2)) {
          double pointSize = graphicsOptions.pointSize(ast);
          GraphicsOptions.optionDouble(arrayNode, "pointSize", pointSize);
        } else if (ast.isAST(S.Dashing, 2) && ast.arg1().isList()) {
          IAST dashingList = (IAST) ast.arg1();
          // TODO
        } else if (ast.isBuiltInFunction()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
          IEvaluator evaluator = symbol.getEvaluator();
          if (evaluator instanceof IGraphics2D) {
            if (((IGraphics2D) evaluator).graphics2D(arrayNode, ast, graphicsOptions)) {
            }
          }
        }
      }
    }
    return true;
  }

  public static boolean exportGraphics2D(ObjectNode objectNode, ArrayNode arrayNode, IAST data2D,
      GraphicsOptions graphicsOptions) {
    if (data2D.isList()) {
      // GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
      // graphicsOptions.setOptions(options);
      GraphicsOptions.optionDouble(arrayNode, "opacity", graphicsOptions.opacity());
      GraphicsOptions.optionDouble(arrayNode, "pointSize", graphicsOptions.pointSize());
      // TODO delete textSize because of renaming to fontSize in next version
      // GraphicsOptions.optionInt(arrayNode, "textSize", graphicsOptions.fontSize());
      GraphicsOptions.optionInt(arrayNode, "fontSize", graphicsOptions.fontSize());
      // ObjectNode blackJSON = JSON_OBJECT_MAPPER.createObjectNode();
      GraphicsOptions.setColorOption(arrayNode, GraphicsOptions.BLACK);
      // arrayNode.add(blackJSON);

      // graphicsOptions.graphics2DScalingFunctions(arrayNode);
      graphicsOptions.graphics2DAxes(objectNode);
      GraphicsUtil.graphics2DAspectRatio(arrayNode, graphicsOptions.options());
      graphicsOptions.graphics2DFilling(arrayNode, graphicsOptions.options());

      IAST list = data2D;
      return GraphicsUtil.export2DRecursive(arrayNode, list, 1, data2D.size(), graphicsOptions);
    }
    return false;

  }

  public static boolean exportGraphics3DRecursive(ArrayNode arrayNode, IAST data3D) {
    if (data3D.isList()) {
      // boolean first = true;
      IAST rgbColor = F.RGBColor(1.0, 0.5, 0.0);
      IExpr opacity = F.num(1.0);
      IAST list = data3D;
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST()) {
          IAST ast = (IAST) arg;
          if (ast.isList()) {
            if (exportGraphics3DRecursive(arrayNode, ast)) {
            }
          } else if (ast.isAST(S.Hue, 2, 5)) {
            IAST hueColor = ast;
            RGBColor rgb = RGBColor.hueToRGB(hueColor);
            if (hueColor.argSize() == 4) {
              opacity = ast.arg4();
            }
            if (rgb != null) {
              rgbColor =
                  F.RGBColor(rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
            }
          } else if (ast.isRGBColor()) {
            rgbColor = ast;
          } else if (ast.isAST(S.Opacity, 2)) {
            opacity = ast.arg1();
          } else if (ast.isBuiltInFunction()) {
            IGraphics3D graphics3DEvaluator = ast.headInstanceOf(IGraphics3D.class);
            if (graphics3DEvaluator != null) {
              ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
              if (graphics3DEvaluator.graphics3D(g, ast, rgbColor, opacity)) {
                arrayNode.add(g);
              }
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  public static void graphics2DAspectRatio(ArrayNode arrayNode, OptionArgs options) {
    IExpr option;
    option = options.getOption(S.AspectRatio);
    if (option.isPresent()) {
      ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
      IReal value = option.evalReal();
      if (value != null) {
        g.put("factor", option.evalf());
      } else {
        g.put("symbol", option.toString().toLowerCase(Locale.US));
      }
      ObjectNode aspectRatio = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
      aspectRatio.set("aspectRatio", g);
      arrayNode.add(aspectRatio);
    } else {
      ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
      g.put("symbol", "automatic");
      ObjectNode aspectRatio = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
      aspectRatio.set("aspectRatio", g);
      arrayNode.add(aspectRatio);
    }
  }

  public static boolean graphics2DJSON(StringBuilder graphics2DBuffer, IExpr data2D,
      OptionArgs options, boolean javaScript, boolean prettyPrint) {
    ObjectNode json = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode arrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
    GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
    graphicsOptions.setOptions(options);
    IExpr plotRange = options.getOption(S.PlotRange);

    if (GraphicsUtil.exportGraphics2D(json, arrayNode, (IAST) data2D, graphicsOptions)) {
      try {
        if (javaScript) {
          graphics2DBuffer.append("drawGraphics2d(\"graphics2d\",\n");
        }
        json.set("elements", arrayNode);

        options = graphicsOptions.options();
        ObjectNode objectNode = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();

        // IExpr coordinateBounds = S.CoordinateBounds.ofNIL(EvalEngine.get(), listOfCoords);
        IExpr option = options.getOption(S.PlotRange);
        int[] matrix = option.isMatrix();
        if (matrix != null && matrix[0] == 2 && matrix[1] == 2) {
          if (graphicsOptions.graphicsExtent2D(objectNode, (IAST) option)) {
            json.set("extent", objectNode);
          }
        } else {
          if (graphicsOptions.graphicsExtent2D(objectNode)) {
            json.set("extent", objectNode);
          } else {
            // return false;
            // fall through?
          }
        }

        if (plotRange.isPresent()
            && graphicsOptions.graphicsExtent2D(objectNode, (IAST) plotRange)) {
          json.set("extent", objectNode);
        }

        // if (options.size() > 0) {
        // IAST optionsList = F.List();
        // // lighting = options.getOption(S.Lighting).orElse(lighting);
        // optionsList = options.getCurrentOptionsList();
        //
        // for (int i = 1; i < optionsList.size(); i++) {
        // IExpr arg = optionsList.get(i);
        // if (arg.isRule()) {
        // IAST rule = (IAST) arg;
        // IExpr lhs = rule.arg1();
        // IExpr rhs = rule.arg2();
        //
        // if (lhs == S.Axes) {
        // axesDefined = graphics2DAxes(json, options);
        // }
        // }
        // }
        //
        // arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
        // if (GraphicsFunctions.exportGraphics2DOptions(arrayNode, optionsList)) {
        // if (arrayNode.size() > 0) {
        // json.set("options", arrayNode);
        // }
        // } else {
        // return false;
        // }
        // }
        if (prettyPrint) {
          graphics2DBuffer.append(json.toPrettyString());
        } else {
          graphics2DBuffer.append(json.toString());
        }
        // graphics2DBuffer.append("{");
        // // graphics2DBuffer.append("\naxes: {},");
        // graphics2DBuffer.append("elements: [");
        // graphics2DBuffer.append(arrayNode.toString());
        // graphics2DBuffer.append("]");
        // // graphics3DLigthing(graphics2DBuffer, lighting);
        // // graphics2DBuffer.append("\nviewpoint: [1.3, -2.4, 2.0]");
        // graphics2DBuffer.append("}");
        if (javaScript) {
          graphics2DBuffer.append("\n);");
        }
        return true;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Graphics, rex, EvalEngine.get());
      }
    }
    return false;
  }

  public static boolean graphics3DCoordsOrListOfCoords(ObjectNode json, IAST coordsOrListOfCoords,
      String coordStr) {
    ArrayNode arrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
    if (coordsOrListOfCoords.isListOfLists()) {
      final int size = coordsOrListOfCoords.size();
      for (int i = 1; i < size; i++) {
        ArrayNode subArrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
        IAST subList = (IAST) coordsOrListOfCoords.get(i);
        for (int j = 1; j < subList.size(); j++) {
          subArrayNode.add(subList.get(j).evalf());
        }
        arrayNode.add(subArrayNode);
      }
    } else if (coordsOrListOfCoords.isList()) {
      ArrayNode subArrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
      for (int i = 1; i < coordsOrListOfCoords.size(); i++) {
        subArrayNode.add(coordsOrListOfCoords.get(i).evalf());
      }
      arrayNode.add(subArrayNode);
    } else {
      return false;
    }
    json.set(coordStr, arrayNode);
    return true;
  }

  public static boolean graphics3DJSON(StringBuilder graphics3DBuffer, IExpr lighting, IExpr data3D,
      boolean javaScript) {
    ObjectNode json = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode arrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
    if (GraphicsUtil.exportGraphics3DRecursive(arrayNode, (IAST) data3D)) {
      try {
        if (javaScript) {
          graphics3DBuffer.append("drawGraphics3d(document.getElementById('graphics3d'),\n");
        }
        json.set("elements", arrayNode);
        GraphicsUtil.graphics3DLigthing(json, lighting);
        ArrayNode vp = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
        vp.add(1.3);
        vp.add(-2.4);
        vp.add(2.0);
        json.set("viewpoint", vp);
        graphics3DBuffer.append(json.toString());
        // graphics3DBuffer.append("{");
        // graphics3DBuffer.append("\naxes: {},");
        // graphics3DBuffer.append("\nelements: [");
        // graphics3DBuffer.append(jsonPrimitives.toString());
        // graphics3DBuffer.append("],");
        // graphics3DLigthing(graphics3DBuffer, lighting);
        // graphics3DBuffer.append("\nviewpoint: [1.3, -2.4, 2.0]");
        // graphics3DBuffer.append("}");
        if (javaScript) {
          graphics3DBuffer.append("\n);");
        }
        return true;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Graphics3D, rex, EvalEngine.get());
      }
    }
    return false;
  }

  public static void graphics3DLigthing(ObjectNode json, IExpr lighting) {
    IAST automatic = F.List(F.AmbientLight(F.RGBColor(0.4, 0.2, 0.2)),
        F.DirectionalLight(F.RGBColor(0., 0.18, 0.5), F.List(2, 0, 2)),
        F.DirectionalLight(F.RGBColor(0.18, 0.5, 0.18), F.List(2, 2, 3)),
        F.DirectionalLight(F.RGBColor(0.5, 0.18, 0.), F.List(0, 2, 2)),
        F.DirectionalLight(F.RGBColor(0., 0., 0.18), F.List(0, 0, 2)));
    IAST result = F.NIL;
    if (lighting.equals(S.Automatic)) {
      result = automatic;
    } else if (lighting.equals(F.$str("Neutral"))) {
      result = F.List(F.AmbientLight(F.RGBColor(0.35, 0.35, 0.35)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(2, 0, 2)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(2, 2, 3)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(0, 2, 2)));
    } else if (lighting.isAST()) {
      result = (IAST) lighting;
    }

    boolean lightingDone = false;
    // graphics3DBuffer.append("\nlighting: [");
    ArrayNode arrayNode = GraphicsOptions.JSON_OBJECT_MAPPER.createArrayNode();
    if (result.isPresent()) {
      if (result.isList()) {
        for (int i = 1; i < result.size(); i++) {
          if (result.get(i).isAST()) {
            // if (lightingDone) {
            // graphics3DBuffer.append(",");
            // }
            ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
            if (graphics3DSingleLight(g, (IAST) result.get(i))) {
              arrayNode.add(g);
              lightingDone = true;
            }
          }
        }
      } else {
        ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
        if (graphics3DSingleLight(g, result)) {
          arrayNode.add(g);
          lightingDone = true;
        }
      }
    }

    if (!lightingDone) {
      ObjectNode g = GraphicsOptions.JSON_OBJECT_MAPPER.createObjectNode();
      lightingDone = graphics3DSingleLight(g, automatic);
      arrayNode.add(g);
    }
    json.set("lighting", arrayNode);
  }

  public static boolean graphics3DSingleLight(ObjectNode json, IAST result) {
    if (result.isAST1()) {
      IExpr color = result.arg1();
      if (color.isRGBColor()) {
        if (result.head().equals(S.AmbientLight)) {
          json.put("type", "ambient");
          GraphicsOptions.setColor(json, (IAST) color, F.RGBColor(F.C1, F.C1, F.C1), true);
          return true;
        }
      }
    } else if (result.isAST2()) {
      String name = result.arg1().toString();
      if (name.equals("Ambient")) {
        IExpr color = result.arg2();
        if (color.isRGBColor()) {
          json.put("type", "ambient");
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
          return true;
        }
      } else {
        IExpr color = result.arg1();
        if (color.isRGBColor() && result.arg2().isList()) {
          IAST list = (IAST) result.arg2();
          if (result.head().equals(S.DirectionalLight) && list.isList()) {
            json.put("type", "directional");
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
            GraphicsUtil.graphics3DCoordsOrListOfCoords(json, list, "coords");
            return true;
          } else if (result.head().equals(S.PointLight) && list.isList3()) {
            json.put("type", "point");
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
            GraphicsUtil.graphics3DCoordsOrListOfCoords(json, list, "coords");
            return true;
          } else if (result.head().equals(S.SpotLight) && list.isList2() && list.isListOfLists()) {
            IAST coords = (IAST) list.arg1();
            IAST target = (IAST) list.arg2();
            if (coords.isList3() && target.isList3()) {
              double angle = 1.0;
              if (result.size() == 5) {
                angle = result.arg4().toDoubleDefault(1.0);
              }
              json.put("type", "spot");
              json.put("angle", angle);
              GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
              GraphicsUtil.graphics3DCoordsOrListOfCoords(json, coords, "coords");
              GraphicsUtil.graphics3DCoordsOrListOfCoords(json, target, "target");
              return true;
            }
          }
        }
      }
    } else if (result.isAST(S.List, 4, 5) && result.arg3().isList() && result.size() > 2) {
      String name = result.arg1().toString();

      IExpr color = result.arg2();
      IAST list = (IAST) result.arg3();
      if (color.isRGBColor()) {
        if (name.equals("Directional") && list.isList()) {
          json.put("type", "directional");
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
          GraphicsUtil.graphics3DCoordsOrListOfCoords(json, list, "coords");
          return true;
        } else if (name.equals("Point") && list.isList3()) {
          json.put("type", "point");
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
          GraphicsUtil.graphics3DCoordsOrListOfCoords(json, list, "coords");
          return true;
        } else if (name.equals("Spot") && list.isList2() && list.isListOfLists()) {
          IAST coords = (IAST) list.arg1();
          IAST target = (IAST) list.arg2();
          if (coords.isList3() && target.isList3()) {
            double angle = 1.0;
            if (result.size() == 5) {
              angle = result.arg4().toDoubleDefault(1.0);
            }
            json.put("type", "spot");
            json.put("angle", angle);
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
            GraphicsUtil.graphics3DCoordsOrListOfCoords(json, coords, "coords");
            GraphicsUtil.graphics3DCoordsOrListOfCoords(json, target, "target");
            return true;
          }
        }
      }
    }
    return false;

  }

  public static boolean renderGraphics2D(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      boolean javaScript, boolean prettyPrint, EvalEngine engine) {
    IAST arg1 = graphics2DAST.first().makeList();
    // IExpr lighting = S.Automatic;
    final OptionArgs options =
        new OptionArgs(graphics2DAST.topHead(), graphics2DAST, 2, engine, true);
    if (arg1.isBuiltInFunction()
        && GraphicsUtil.graphics2DJSON(graphics2DBuffer, arg1, options, javaScript, prettyPrint)) {
      return true;
    }
    return false;
  }

  public static boolean renderGraphics2D(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      EvalEngine engine) {
    return GraphicsUtil.renderGraphics2D(graphics2DBuffer, graphics2DAST, true, false, engine);
  }

  public static boolean renderGraphics2DSVG(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      boolean withSVGTag, EvalEngine engine) {
    SVGGraphics svg = new SVGGraphics(600, 400);
    String svgString = svg.toSVG(graphics2DAST, withSVGTag);
    graphics2DBuffer.append(svgString);
    return true;
  }

  public static boolean renderGraphics2DSVG(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      EvalEngine engine) {
    return renderGraphics2DSVG(graphics2DBuffer, graphics2DAST, false, engine);
  }

  public static boolean renderGraphics3D(StringBuilder graphics3DBuffer, IAST graphics3DAST,
      boolean javaScript, EvalEngine engine) {
    IExpr arg1 = graphics3DAST.first();
    if (!arg1.isList()) {
      arg1 = F.list(arg1);
    }
    IExpr lighting = S.Automatic; // .List(F.$str("Auto"), F.RGBColor(F.C1, F.C1, F.C1));
    OptionArgs options = OptionArgs.createOptionArgs(graphics3DAST, engine);
    if (options != null) {
      lighting = options.getOption(S.Lighting).orElse(lighting);

      // if (option.isList1() && option.first().isList() && option.first().first().isString()) {
      // lighting = option.first();
      // }
    }

    if (arg1.isBuiltInFunction()
        && GraphicsUtil.graphics3DJSON(graphics3DBuffer, lighting, arg1, javaScript)) {
      return true;
    }
    return false;
  }

  public static boolean renderGraphics3D(StringBuilder graphics3DBuffer, IAST graphics3DAST,
      EvalEngine engine) {
    return GraphicsUtil.renderGraphics3D(graphics3DBuffer, graphics3DAST, true, engine);
  }

  public static void setYRange(double vmin, double vmax, double[] yMinMax) {
    if (vmin < yMinMax[0]) {
      yMinMax[0] = vmin;
    }
    if (vmax > yMinMax[1]) {
      yMinMax[1] = vmax;
    }
  }

  private GraphicsUtil() {
    // private constructor to avoid instantiation
  }

}
