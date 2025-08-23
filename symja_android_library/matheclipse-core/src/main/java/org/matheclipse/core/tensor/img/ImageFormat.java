// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class ImageFormat {

  private ImageFormat() {}

  /**
   * Encode image as {@link IAST} Dimensions of output are [height x width] for grayscale images of
   * type BufferedImage.TYPE_BYTE_GRAY [height x width x 4] for color images
   *
   * @param bufferedImage
   * @return list encoding the color values of given bufferedImage
   */
  public static IAST from(BufferedImage bufferedImage) {
    switch (bufferedImage.getType()) {
      case BufferedImage.TYPE_BYTE_GRAY:
        return fromGrayscale(bufferedImage);
      default:
        return F.matrix((y, x) -> ColorFormat.toVector(bufferedImage.getRGB(x, y)),
            bufferedImage.getHeight(), bufferedImage.getWidth());
    }
  }

  /**
   * @param bufferedImage grayscale image with dimensions [width x height]
   * @return tensor with dimensions [height x width]
   */
  private static IAST fromGrayscale(BufferedImage bufferedImage) {
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    ByteBuffer byteBuffer = ByteBuffer.wrap(dataBufferByte.getData());
    return F.matrix((i, j) -> F.ZZ(byteBuffer.get() & 0xff), bufferedImage.getHeight(),
        bufferedImage.getWidth());
  }

  /**
   * Create a BufferedImage from the matrix data
   * 
   * @param matrix
   * @return image of type BufferedImage.TYPE_BYTE_GRAY or BufferedImage.TYPE_INT_ARGB or
   *         <code>null</code> if conversion wasn't possible
   */
  public static BufferedImage toIntARGB(IAST matrix) throws IllegalArgumentException {
    return toIntFormat(matrix, BufferedImage.TYPE_INT_ARGB);
  }

  /**
   * Functionality for exporting a color image to BMP and JPG format
   *
   * @param matrix
   * @return image of type BufferedImage.TYPE_BYTE_GRAY or BufferedImage.TYPE_INT_BGR or
   *         <code>null</code> if conversion wasn't possible
   */
  public static BufferedImage toIntBGRImage(IAST matrix) throws IllegalArgumentException {
    return toIntFormat(matrix, BufferedImage.TYPE_INT_BGR);
  }

  /**
   * 
   * @param matrix
   * @param imageType
   * @return <code>null</code> if conversion wasn't possible
   * @throws UnsupportedOperationException
   */
  public static BufferedImage toIntFormat(IAST matrix, int imageType)
      throws IllegalArgumentException {
    IntArrayList dimensions = LinearAlgebraUtil.dimensions(matrix);
    int width = dimensions.getInt(1);
    int height = dimensions.getInt(0);
    if (dimensions.size() == 2) {
      return toTYPE_BYTE_GRAY(matrix, width, height);
    }
    if (dimensions.size() == 3) {
      return toINT(matrix, width, height, imageType);
    }
    throw new UnsupportedOperationException();
  }

  /**
   * 
   * @param matrix
   * @param width
   * @param height
   * @return <code>null</code> if conversion wasn't possible
   */
  private static BufferedImage toTYPE_BYTE_GRAY(IAST matrix, int width, int height)
      throws IllegalArgumentException {
    // https://stackoverflow.com/questions/37362753/creating-grayscale-bitmap-from-array-of-0-255-gray-values-in-java
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    double[][] doubleMatrix = matrix.toDoubleMatrix(true);
    if (doubleMatrix != null) {
      for (int i = 0; i < doubleMatrix.length; i++) {
        for (int j = 0; j < doubleMatrix[i].length; j++) {
          byteBuffer.put((byte) (255.0 * doubleMatrix[i][j]));
        }
      }
      return bufferedImage;
    }
    return null;
  }


  /**
   * 
   * @param tensor
   * @param width
   * @param height
   * @param imageType
   * @return <code>null</code> if conversion wasn't possible
   */
  private static BufferedImage toTYPE_INT(IAST tensor, int width, int height, int imageType)
      throws IllegalArgumentException {
    // fast extraction of color information to buffered image
    BufferedImage bufferedImage = new BufferedImage(width, height, imageType);

    int indx = 0;

    IExpr flattenedArray = S.Flatten.of(tensor, F.C1);
    if (flattenedArray.isList()) {
      int[] rgbArray = new int[width * height];
      RGBColor color;
      double[][] doubleMatrix = flattenedArray.toDoubleMatrix(true);

      for (int i = 0; i < doubleMatrix.length; i++) {
        int red = (int) (255.0 * doubleMatrix[i][0]);
        int green = (int) (255.0 * doubleMatrix[i][1]);
        int blue = (int) (255.0 * doubleMatrix[i][2]);
        color = new RGBColor(red, green, blue);
        rgbArray[indx++] = color.getRGB();
      }
      bufferedImage.setRGB(0, 0, width, height, rgbArray, 0, width);
      return bufferedImage;
    }
    return null;
  }

  /**
   * 
   * @param tensor
   * @param width
   * @param height
   * @param imageType
   * @return <code>null</code> if conversion wasn't possible
   */
  private static BufferedImage toINT(IAST tensor, int width, int height, int imageType) {
    // fast extraction of color information to buffered image
    BufferedImage bufferedImage = new BufferedImage(width, height, imageType);

    int indx = 0;

    IExpr flattenedArray = S.Flatten.of(tensor, F.C1);
    if (flattenedArray.isList()) {
      int[] rgbArray = new int[width * height];
      RGBColor color;
      double[][] doubleMatrix = flattenedArray.toDoubleMatrix(true);

      for (int i = 0; i < doubleMatrix.length; i++) {
        int red = (int) (doubleMatrix[i][0]);
        int green = (int) (doubleMatrix[i][1]);
        int blue = (int) (doubleMatrix[i][2]);
        color = new RGBColor(red, green, blue);
        rgbArray[indx++] = color.getRGB();
      }
      bufferedImage.setRGB(0, 0, width, height, rgbArray, 0, width);
      return bufferedImage;
    }
    return null;
  }
}
