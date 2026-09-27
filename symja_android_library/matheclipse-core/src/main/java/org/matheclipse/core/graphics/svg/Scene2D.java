package org.matheclipse.core.graphics.svg;

import java.util.Collections;
import java.util.List;
import org.matheclipse.core.interfaces.IAST;

/**
 * A 2D {@code Graphics} expression after options, primitives and plot range have been worked out,
 * but before anything is drawn.
 *
 * <p>
 * This is the part of {@link SvgGraphics2D} that does not depend on SVG, handed out so that a
 * renderer for another output (a JavaScript plotting library, say) sees exactly the primitives,
 * range and options the SVG renderer would have drawn, rather than walking the expression a second
 * time and disagreeing with it.
 */
public final class Scene2D {

  public final GraphicsOptions2D options;

  /** The primitives of the picture, in drawing order. */
  public final List<Prim2D> primitives;

  /** {@code Prolog} and {@code Epilog} content; drawn, but never part of the plot range. */
  public final List<Prim2D> prolog;
  public final List<Prim2D> epilog;

  /** The bounding box of {@link #primitives}, after the automatic y range was narrowed. */
  public final Bounds2D bounds;

  /** The plot range the picture is shown with, and its mapping to pixels. */
  public final Viewport2D viewport;

  Scene2D(GraphicsOptions2D options, List<Prim2D> primitives, List<Prim2D> prolog,
      List<Prim2D> epilog, Bounds2D bounds, Viewport2D viewport) {
    this.options = options;
    this.primitives = Collections.unmodifiableList(primitives);
    this.prolog = Collections.unmodifiableList(prolog);
    this.epilog = Collections.unmodifiableList(epilog);
    this.bounds = bounds;
    this.viewport = viewport;
  }

  /**
   * Lay out one {@code Graphics} expression, peeling the same display wrappers
   * ({@code Legended}, {@code Tooltip}, ...) the SVG renderer peels.
   *
   * @param width the width to lay out at, in pixels
   * @param height a starting height, which the aspect ratio may override
   * @return {@code null} for a layout of several pictures ({@code GraphicsRow}, {@code GraphicsGrid},
   *         a list, {@code Overlay}), which has no single plot range
   */
  public static Scene2D of(IAST graphics, double width, double height) {
    return new SvgGraphics2D(width, height).buildScene(graphics);
  }
}
