package org.matheclipse.core.graphics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Show2SVG {

  private static final DecimalFormatSymbols US_SYMBOLS = new DecimalFormatSymbols(Locale.US);
  public static final DecimalFormat FORMATTER = new DecimalFormat("0.0####", US_SYMBOLS);



  // private static void elementToSVG(IAST ast, StringBuilder buf, Dimensions2D dim) {
  // if (ast.size() > 1) {
  // IExpr arg1 = ast.arg1();
  // if (!arg1.isList()) {
  // arg1 = F.List(arg1);
  // }
  // GraphicsFunctions.exportGraphicsSVG(buf, (IAST) arg1, dim);
  // }
  // }

  // public static void toSVG(IAST ast, StringBuilder buf) {
  // if (ast.size() > 1 && ast.arg1().isSameHeadSizeGE(S.Graphics, 2)) {
  // GraphicsFunctions.graphicsToSVG((IAST) ast.arg1(), buf);
  // }
  // }
}
