package org.matheclipse.core.graphics;

import org.matheclipse.core.graphics.svg.SvgGraphics2D;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import j2html.tags.ContainerTag;

/**
 * 2D Graphics to SVG converter.
 *
 * <p>
 * The conversion itself lives in {@link org.matheclipse.core.graphics.svg}, which collects the
 * graphic into typed primitives before drawing anything. This class stays as the entry point the
 * rest of the code base already uses.
 */
public class SVGGraphics {

  /**
   * The responsive style the {@code <svg>} root carries. It is right for markup embedded in an
   * HTML page, where the picture should scale to its container, and wrong for a standalone
   * {@code .svg} document: {@code height: auto} on the root element lets the viewport collapse
   * to zero height in stricter renderers, and the picture shows up blank.
   */
  private static final String EMBEDDING_STYLE = " style=\"max-width: 100%; height: auto;\"";

  private static final String XML_DECLARATION =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";

  private final SvgGraphics2D delegate;

  public SVGGraphics() {
    this.delegate = new SvgGraphics2D();
  }

  public SVGGraphics(double width, double height) {
    this.delegate = new SvgGraphics2D(width, height);
  }

  /** The size of the rendered image, in pixels. */
  public double[] getImageSize() {
    return delegate.getImageSize();
  }

  /** Render to a complete SVG document. */
  public String toSVG(IAST graphicsExpr) {
    return delegate.toSVG(graphicsExpr);
  }

  /**
   * @param withSVGTag when false the {@code <svg>} root is omitted and only its children are
   *        returned, for callers that supply their own root element
   */
  public String toSVG(IAST graphicsExpr, boolean withSVGTag) {
    return delegate.toSVG(graphicsExpr, withSVGTag);
  }

  /** The SVG element tree, for callers that want to embed it rather than render a string. */
  public ContainerTag<?> buildSVGTag(IAST graphicsExpr) {
    return delegate.buildSVGTag(graphicsExpr);
  }

  /**
   * Render a <code>Graphics</code> or <code>Graphics3D</code> expression as a <b>standalone</b>
   * SVG document: an XML declaration, and no responsive root style. This is what
   * <code>Export["f.svg", g]</code> and <code>ExportString[g, "SVG"]</code> hand out, so the
   * file can be opened directly in a viewer.
   *
   * <p>
   * Markup that is going to be embedded inside an HTML page must not use this - use
   * {@link #toSVG(IAST, boolean)} there, since an XML declaration is illegal mid-document and
   * the responsive style is wanted.
   *
   * @return the SVG document, or <code>null</code> when the expression is not a graphic
   */
  public static String svgDocument(IExpr graphics) {
    String svg;
    if (graphics.isGraphicsObject()) {
      svg = new SVGGraphics(360, 360).toSVG((IAST) graphics, true);
    } else if (graphics.isAST(S.Graphics3D)) {
      svg = SVGGraphics3D.toSVG((IAST) graphics);
    } else {
      return null;
    }
    if (svg == null || svg.isEmpty()) {
      return null;
    }
    svg = svg.replace(EMBEDDING_STYLE, "");
    if (!svg.startsWith("<?xml")) {
      svg = XML_DECLARATION + svg;
    }
    return svg.endsWith("\n") ? svg : svg + "\n";
  }
}
