// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The general implementation {@link ImageResize#of(IAST, IExpr)} uses
 * {@link Image#SCALE_AREA_AVERAGING} with emphasis on quality.
 * 
 */
public class ImageResize {

  /**
   * The function is particularly suitable for down-sizing a given image to a smaller resolution.
   * The implementation uses the SCALE_AREA_AVERAGING algorithm.
   * 
   * @param bufferedImage
   * @param width of rescaled image
   * @param height of rescaled image
   * @return scaled instance of given buffered image with given dimensions and type either
   *         BufferedImage.TYPE_BYTE_GRAY or BufferedImage.TYPE_INT_ARGB
   */
  public static BufferedImage of(BufferedImage bufferedImage, int width, int height) {
    BufferedImage result =
        new BufferedImage(width, height, StaticHelper.type(bufferedImage.getType()));
    Graphics graphics = result.createGraphics();
    Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
    graphics.drawImage(image, 0, 0, null);
    graphics.dispose();
    return result;
  }

  /**
   * @param tensor of rank 2 or 3
   * @param dim0 height of image
   * @param dim1 width of image
   * @return
   */
  public static IAST of(IAST tensor, int dim0, int dim1) {
    return ImageFormat.from(of(ImageFormat.toIntARGB(tensor), dim1, dim0));
  }

  /**
   * Remark: for a factor of one the width and height of the image remain identical
   * 
   * @param tensor of rank 2 or 3
   * @param factor
   * @return image scaled by given factor
   */
  // public static IAST of(IAST tensor, IExpr factor) {
  // List<Integer> list = Dimensions.of(tensor);
  // return of(tensor, //
  // Round.intValueExact(RealScalar.of(list.get(0)).multiply(factor)), //
  // Round.intValueExact(RealScalar.of(list.get(1)).multiply(factor)));
  // }

  /**
   * @param tensor of rank 2 or 3
   * @param dimension
   * @return tensor that is a resized version of given tensor to given dimension
   */
  public static IAST of(IAST tensor, Dimension dimension) {
    return of(tensor, dimension.height, dimension.width);
  }

  /**
   * function uses nearest neighbor interpolation
   * 
   * @param tensor of rank 2 or 3
   * @param factor positive integer
   * @return
   */
  // public static IAST nearest(IAST tensor, int factor) {
  // return nearest(tensor, factor, factor);
  // }

  /**
   * function uses nearest neighbor interpolation
   * 
   * @param tensor of rank 2 or 3
   * @param fx positive scaling along x axis
   * @param fy positive scaling along y axis
   * @return
   * @throws Exception if either fx or fy is zero or negative
   */
  // public static IAST nearest(IAST tensor, int fx, int fy) {
  // int dim0 = tensor.argSize();
  // int dim1 = Unprotect.dimension1(tensor);
  // // precomputation of indices
  // int[] ix = IntStream.range(0, dim0 * fx).map(i -> i / fx).toArray();
  // int[] iy = IntStream.range(0, dim1 * fy).map(i -> i / fy).toArray();
  // return Tensors.matrix((i, j) -> tensor.get(ix[i], iy[j]), //
  // dim0 * fx, //
  // dim1 * fy);
  // }
}
