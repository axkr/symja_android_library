// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.matheclipse.core.bridge.awt.RenderQuality;
import org.matheclipse.core.bridge.lang.Unicode;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.Integers;
import org.matheclipse.core.tensor.img.ImageFormat;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

public class BarLegend {
  public static BarLegend of(Function<IExpr, IExpr> colorDataGradient, Clip clip, Set<IExpr> set) {
    return new BarLegend(colorDataGradient, clip,
        set.stream().collect(Collectors.toMap(s -> s, Unicode::valueOf)));
  }

  private final Function<IExpr, IExpr> colorDataGradient;
  private final Clip clip;
  private final Map<IExpr, String> map;
  public int space = 2;
  public Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
  public Color color = Color.DARK_GRAY;

  public BarLegend(Function<IExpr, IExpr> colorDataGradient, Clip clip, Map<IExpr, String> map) {
    this.colorDataGradient = colorDataGradient;
    this.clip = clip;
    this.map = map;
  }

  /**
   * @param dimension of color gradient to which space is added for the labels
   * @return
   */
  public BufferedImage createImage(Dimension dimension) {
    int width = dimension.width;
    int height = dimension.height;
    FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
    OptionalInt optionalInt = map.values().stream().mapToInt(fontMetrics::stringWidth).max();
    int maxWidth = optionalInt.isPresent() //
        ? space + optionalInt.getAsInt()
        : 0;
    BufferedImage bufferedImage =
        new BufferedImage(width + maxWidth, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    EvalEngine engine = EvalEngine.get();
    IAST subdivide = (IAST) S.Subdivide.of(engine, Clips.unit(), height - 1);
    graphics.drawImage( //
        ImageFormat.toIntARGB(
            subdivide.mapReverse(org.matheclipse.core.expression.F::ListAlloc)
                .map(colorDataGradient)), //
        0, //
        0, //
        width, //
        height, null);
    graphics.setFont(font);
    graphics.setColor(color);
    RenderQuality.setQuality(graphics);
    int ascent = fontMetrics.getAscent();
    for (Entry<IExpr, String> entry : map.entrySet()) {
      IExpr rescale = F.C1.subtract(clip.rescale(entry.getKey()));
      int piy = (int) (height * rescale.evalDouble() + ascent / 2);
      piy = Integers.clip(ascent, height).applyAsInt(piy);
      // piy = Math.min(Math.max(ascent, piy), height);
      graphics.drawString(entry.getValue(), width + space, piy);
    }
    graphics.dispose();
    return bufferedImage;
  }
}
