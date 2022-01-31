package org.matheclipse.core.graphics;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

public class Show3D2ThreeJS {
  private static final Logger LOGGER = LogManager.getLogger();


  /**
   * A 3D Graphics command like
   *
   * <pre>
   *     Graphics3D(Polygon({{0,0,0}, {0,1,1}, {1,0,0}}))
   * </pre>
   *
   * will be converted to:
   *
   * <pre>
   * &lt;graphics3d data="{&quot;viewpoint&quot;: [1.3, -2.4, 2.0], &quot;elements&quot;: [{&quot;coords&quot;:......
   * </pre>
   *
   * <p>
   * It's a bit messy because of all the HTML escaping. What we are interested in is the data field.
   * It's a JSON dict describing the 3D graphics in terms of graphics primitives. This JSON can be
   * used in <a href="http://threejs.org/">threejs.org</a> to construct a 3D div.
   *
   * @param ast
   * @param buf
   * @throws IOException
   * @deprecated
   */
  @Deprecated
  private static void graphics3dToSVG(IAST ast, StringBuilder buf) {
    EvalEngine engine = EvalEngine.get();
    IAST numericAST = (IAST) engine.evalN(ast);
    double[] viewpoints = new double[] {1.3, -2.4, 2.0};
    if (numericAST.size() > 2) {
      final OptionArgs options = new OptionArgs(numericAST.topHead(), numericAST, 2, engine);
      optionViewPoint(options, viewpoints);
    }
    int width = 400;
    int height = 200;
    Dimensions2D dim = new Dimensions2D(width, height);
    buf.append("<graphics3d data=\"{");

    StringBuilder builder = new StringBuilder(1024);
    appendDoubleArray(builder, "viewpoint", viewpoints);
    try {
      for (int i = 1; i < numericAST.size(); i++) {
        // if (numericAST.get(i).isASTSizeGE(F.Line, 2)) {
        // lineToSVG(numericAST.getAST(i), buf, dim);
        // } else
        if (numericAST.get(i).isSameHeadSizeGE(S.Polygon, 2)) {
          elements("polygon", numericAST.getAST(i), builder, dim);
        } else if (numericAST.get(i).isSameHeadSizeGE(S.Point, 2)) {
          elements("point", numericAST.getAST(i), builder, dim);
        }
      }
    } finally {
      builder.append("\"lighting\": [{\"color\": [0.3, 0.2, 0.4], \"type\": \"Ambient\"}, "
          + "{\"color\": [0.8, 0.0, 0.0], \"position\": [2.0, 0.0, 2.0], \"type\": \"Directional\"}, "
          + "{\"color\": [0.0, 0.8, 0.0], \"position\": [2.0, 2.0, 2.0], \"type\": \"Directional\"}, "
          + "{\"color\": [0.0, 0.0, 0.8], \"position\": [0.0, 2.0, 2.0], \"type\": \"Directional\"}], "
          + "\"axes\": {\"hasaxes\": [false, false, false], "
          + "\"ticks\": [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [\"0.0\", \"0.2\", \"0.4\", \"0.6\", \"0.8\", \"1.0\"]], [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [\"0.0\", \"0.2\", \"0.4\", \"0.6\", \"0.8\", \"1.0\"]], [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], [\"0.0\", \"0.2\", \"0.4\", \"0.6\", \"0.8\", \"1.0\"]]]}, "
          + "\"extent\": {\"zmax\": 1.0, \"ymax\": 1.0, \"zmin\": 0.0, \"xmax\": 1.0, \"xmin\": 0.0, \"ymin\": 0.0}");
      Escaper escaper = HtmlEscapers.htmlEscaper();
      buf.append(escaper.escape(builder.toString()));
      buf.append("}\" />");
    }
  }

  /**
   * Determine the viewpoint in space from which a three‐dimensional object can be viewed.
   *
   * @param options
   * @param viewpoints
   */
  static void optionViewPoint(final OptionArgs options, double[] viewpoints) {
    IExpr option = options.getOption(S.ViewPoint);
    if (option.isPresent()) {
      if (option.isSymbol()) {
        String viewpoint = option.toString().toLowerCase();
        if (viewpoint.equals("above")) {
          viewpoints[0] = 0.0;
          viewpoints[1] = 0.0;
          viewpoints[2] = 2.0;
        } else if (viewpoint.equals("below")) {
          viewpoints[0] = 0.0;
          viewpoints[1] = 0.0;
          viewpoints[2] = -2.0;
        } else if (viewpoint.equals("front")) {
          viewpoints[0] = 0.0;
          viewpoints[1] = -2.0;
          viewpoints[2] = 0.0;
        } else if (viewpoint.equals("back")) {
          viewpoints[0] = 0.0;
          viewpoints[1] = 2.0;
          viewpoints[2] = 0.0;
        } else if (viewpoint.equals("left")) {
          viewpoints[0] = -2.0;
          viewpoints[1] = 0.0;
          viewpoints[2] = 0.0;
        } else if (viewpoint.equals("right")) {
          viewpoints[0] = 2.0;
          viewpoints[1] = 0.0;
          viewpoints[2] = 0.0;
        }
      } else if (option.isList() && option.isAST3()) {
        IAST list = (IAST) option;
        for (int i = 1; i < list.size(); i++) {
          viewpoints[i - 1] = ((ISignedNumber) list.getAt(i)).doubleValue();
        }
      }
    }
  }

  private static void appendDoubleArray(StringBuilder builder, String tag, double[] viewpoints) {
    builder.append("\"");
    builder.append(tag);
    builder.append("\": [");
    for (int i = 0; i < viewpoints.length; i++) {
      builder.append(Show2SVG.FORMATTER.format(viewpoints[i]));
      if (i < viewpoints.length - 1) {
        builder.append(", ");
      }
    }
    builder.append("], ");
  }

  static void elements(String type, IAST ast, StringBuilder buf, Dimensions2D dim) {
    try {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfLists()) {
        IAST list = (IAST) ast.arg1();
        if (list.size() > 1) {
          try {
            buf.append("\"elements\": [{\"coords\": [");
            for (int i = 1; i < list.size(); i++) {
              IAST point = list.getAST(i);
              addCoordsToElements(buf, point);
              if (i < list.argSize()) {
                buf.append(", ");
              }
            }
            buf.append("], \"type\": \"");
            buf.append(type);
            buf.append("\", \"faceColor\": [1, 1, 1, 1]");
          } finally {
            buf.append("}], ");
          }
        }
      } else if (arg1.isList() && ((IAST) arg1).isAST3()) {
        try {
          buf.append("\"elements\": [{\"coords\": [");
          addCoordsToElements(buf, ((IAST) arg1));
          buf.append("], \"type\": \"");
          buf.append(type);
          buf.append("\", \"faceColor\": [1, 1, 1, 1]");
        } finally {
          buf.append("}], ");
        }
      }
    } catch (RuntimeException ex) {
      // catch cast exceptions for example
      LOGGER.error("Show3D2ThreeJS.elements() failed", ex);
    }
  }

  private static void addCoordsToElements(StringBuilder buf, IAST point) {
    if (point.isList() && point.isAST3()) {
      // [{"coords": [[[0.0, 0.0, 0.0], null], [[0.0, 1.0, 1.0], null], [[1.0, 0.0, 0.0],
      // null]]
      buf.append("[[");
      for (int j = 1; j < point.size(); j++) {
        buf.append(Show2SVG.FORMATTER.format(((ISignedNumber) point.getAt(j)).doubleValue()));
        if (j < point.argSize()) {
          buf.append(", ");
        }
      }
      buf.append("], null]");
    }
  }
}
