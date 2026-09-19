package org.matheclipse.jsgraphics;

import org.matheclipse.core.graphics.svg.Scene2D;

/**
 * Turns a laid out 2D graphic into the JavaScript that draws it with one plotting library.
 *
 * <p>
 * A renderer produces only the script. The page around it - the element it draws into and the
 * library loaded from its CDN - is {@link org.matheclipse.jsgraphics.page.JSPageBuilder}'s.
 */
public interface JSRenderer {

  /** The library name, as the second argument of <code>JSFormData[js, type]</code>. */
  String type();

  /**
   * What in {@code scene} this library cannot draw, or <code>null</code> when it can draw all of
   * it. The name is reported to the user, e.g. "Raster".
   */
  String unsupported(Scene2D scene);

  /** The script that draws {@code scene}. */
  String toJavaScript(Scene2D scene);
}
