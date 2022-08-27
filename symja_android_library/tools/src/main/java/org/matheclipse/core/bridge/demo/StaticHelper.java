package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/* package */ class StaticHelper {

  static final IAST SE2 = F.List(F.List(180, 0, 6), F.List(0, -180, 186), F.List(0, 0, 1));
  static final IAST POINT = (IAST) S.CirclePoints.of(10).multiply(F.num(0.015));
  // static final IAST SE2_2 =
  // Tensors.fromString("{{180*2, 0, 6*2}, {0, -180*2, 186*2}, {0, 0, 1}}").unmodifiable();

  static BufferedImage createWhite(int size) {
    BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, size, size);
    return bufferedImage;
  }

  static BufferedImage createWhite() {
    return createWhite(192);
  }
}
