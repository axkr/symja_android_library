package org.matheclipse.core.graphics;

import org.matheclipse.core.graphics.svg.SvgGraphics2D;
import org.matheclipse.core.interfaces.IAST;
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
}
