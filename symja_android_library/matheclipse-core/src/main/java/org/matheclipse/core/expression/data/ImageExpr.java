package org.matheclipse.core.expression.data;

import java.awt.image.BufferedImage;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;

public class ImageExpr extends DataExpr<BufferedImage> {

  private static final long serialVersionUID = 6782059289137844206L;

  final IAST matrix;
  final Extension format;

  public ImageExpr(final BufferedImage buffer, IAST matrix, Extension format) {
    super(S.Image, buffer);
    this.matrix = matrix;
    this.format = format;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ImageExpr) {
      return fData.equals(((ImageExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 3761 : 3761 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return IMAGEID;
  }

  @Override
  public IExpr copy() {
    BufferedImage bufferedImage =
        new BufferedImage(fData.getWidth(), fData.getHeight(), fData.getType());
    bufferedImage.setData(fData.getRaster());
    return new ImageExpr(bufferedImage, matrix, format);
  }

  @Override
  public String toString() {
    return "Image(Dimensions: " + fData.getWidth() + "," + fData.getHeight() + " Transparency: "
        + fData.getTransparency() + ")";
  }

}
