package org.matheclipse.core.bridge.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.bridge.awt.RenderQuality;
import org.matheclipse.core.bridge.gfx.GeometricLayer;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.core.tensor.img.ColorDataIndexed;
import org.matheclipse.core.tensor.img.ColorDataLists;
import org.matheclipse.core.tensor.img.ImageFormat;

public class WindowFunctionImage {

  private static IAST image() {
    EvalEngine engine = EvalEngine.get();
    final IAST SE2 = F.List(F.List(180, 0, 96), F.List(0, -180, 186), F.List(0, 0, 1));
    GeometricLayer geometricLayer = new GeometricLayer(SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    RenderQuality.setQuality(graphics);
    graphics.setColor(Color.RED);
    IAST domain = F.subdivide(-0.5, 0.5, 180);
    graphics.setStroke(new BasicStroke(1.5f));
    ColorDataIndexed colorDataIndexedF = ColorDataLists._097.cyclic();
    ColorDataIndexed colorDataIndexedP = colorDataIndexedF.deriveWithAlpha(128 + 64);
    int piy = 10;
    int index = 0;
    WindowFunctions.WindowFunction[] smoothingKernels = new WindowFunctions.WindowFunction[] { //
        WindowFunctions.GAUSSIAN, //
        WindowFunctions.HAMMING, //
        WindowFunctions.BLACKMAN, //
        WindowFunctions.NUTTALL, //
    };
    graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
    for (WindowFunctions.WindowFunction smoothingKernel : smoothingKernels) {
      graphics.setColor(colorDataIndexedP.getColor(index));
      IAST tensor = domain.map(smoothingKernel.get());
      IAST points = (IAST) S.Transpose.of(engine, F.List(domain, tensor));
      Path2D path2d = geometricLayer.toPath2D(points);
      graphics.draw(path2d);
      graphics.setColor(colorDataIndexedF.getColor(index));
      graphics.drawString(smoothingKernel.name(), 0, piy);
      piy += 10;
      ++index;
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    File folder = HomeDirectory.Pictures();
    S.Export.of(
        F.stringx(folder.getPath() + "\\" + WindowFunctionImage.class.getSimpleName() + ".png"),
        image());
  }
}
