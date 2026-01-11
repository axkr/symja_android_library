package org.matheclipse.image.expression.data;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.DocumentLimits;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.FloatSize;

public class SVG2BufferedImage {
  /**
   * Converts an SVG string into a BufferedImage.
   *
   * @param svgContent The raw SVG XML string.
   * @return A BufferedImage containing the rendered SVG, or null if parsing fails.
   */
  public static BufferedImage createBufferedImage(String svgContent) {
    SVGLoader loader = new SVGLoader();

    DocumentLimits limits = new DocumentLimits(//
        DocumentLimits.DEFAULT_MAX_NESTING_DEPTH, //
        DocumentLimits.DEFAULT_MAX_USE_NESTING_DEPTH, //
        100000);

    LoaderContext context = LoaderContext.builder().documentLimits(limits).build();

    // Load the document using the context
    SVGDocument doc =
        loader.load(new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8)), //
            null, context);

    if (doc == null) {
      return null;
    }

    // Get the intrinsic size
    FloatSize size = doc.size();
    int width = (int) Math.ceil(size.width);
    int height = (int) Math.ceil(size.height);

    // Fallback for missing dimensions
    if (width <= 0)
      width = 500;
    if (height <= 0)
      height = 500;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    try {
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

      doc.render(null, g2d);
    } finally {
      g2d.dispose();
    }

    return image;
  }
}
