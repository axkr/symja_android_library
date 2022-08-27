package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.bridge.awt.RenderQuality;
import org.matheclipse.core.bridge.gfx.GeometricLayer;
import org.matheclipse.core.bridge.gfx.GfxMatrix;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.J;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.core.tensor.img.ImageFormat;
import org.matheclipse.core.tensor.sca.Clips;

/* package */ class ConvexHullShow {

  private static IAST image(int seed) {
    IAST points = (IAST) F.eval(J.randomVariate(J.normalDistribution(0.5, .28), F.List(30, 2)));

    points = points.mapLeaf(S.List, Clips.unit());
    // .map(Clips.unit());
    // System.err.println(points.toString());
    IAST hull = (IAST) S.ConvexHullMesh.of(points);
    GeometricLayer geometricLayer = new GeometricLayer(StaticHelper.SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    RenderQuality.setQuality(graphics);
    {
      graphics.setColor(Color.BLUE);
      Path2D path2d = geometricLayer.toPath2D(hull);
      path2d.closePath();
      graphics.draw(path2d);
    }
    graphics.setColor(Color.RED);
    for (IExpr p : points) {
      IAST point = (IAST) p;
      geometricLayer.pushMatrix(GfxMatrix.translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return ImageFormat.from(bufferedImage);
    // return new ImageExpr(bufferedImage, null, null);
  }

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    File folder = HomeDirectory.Pictures(ConvexHullShow.class.getSimpleName());
    folder.mkdir();
    for (int seed = 0; seed < 51; ++seed) {
      IAST image = image(seed);
      S.Export.of(F.stringx(folder.getPath() + "\\" + String.format("%03d.png", seed)), image);
    }
    S.Export.of(F.stringx(folder.getPath() + "\\" + ConvexHullShow.class.getSimpleName() + ".png"),
        image(3));
  }
}
