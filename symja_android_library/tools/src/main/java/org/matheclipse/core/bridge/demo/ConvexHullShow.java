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
import org.matheclipse.core.io.ImageFormat;

/* package */ class ConvexHullShow {

  private static IAST image(int seed) {
    IAST points = (IAST) F.eval(J.randomVariate(J.normalDistribution(0.5, .28), F.List(30, 2)));

    // clip the coordinates to the unit interval
    points = points.mapLeaf(S.List, x -> F.num(Math.min(1.0, Math.max(0.0, x.evalf()))));
    // System.err.println(points.toString());
    // ConvexHullMesh returns a BoundaryMeshRegion whose vertices are listed in input order;
    // ConvexHullRegion gives the Polygon with its corners in boundary order
    IAST hull = (IAST) ((IAST) S.ConvexHullRegion.of(points)).arg1();
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
    File folder = new File(new File(System.getProperty("user.home"), "Pictures"),
        ConvexHullShow.class.getSimpleName());
    folder.mkdirs();
    for (int seed = 0; seed < 51; ++seed) {
      IAST image = image(seed);
      S.Export.of(F.stringx(new File(folder, String.format("%03d.png", seed)).getPath()), image);
    }
    S.Export.of(
        F.stringx(new File(folder, ConvexHullShow.class.getSimpleName() + ".png").getPath()),
        image(3));
  }
}
