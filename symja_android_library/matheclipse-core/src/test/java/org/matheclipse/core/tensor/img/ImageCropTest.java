// code by jph
package org.matheclipse.core.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageCrop;
import org.matheclipse.core.tensor.io.ResourceData;

class ImageCropTest {
  // @Test
  // void testSerialization() throws ClassNotFoundException, IOException {
  // TensorUnaryOperator tensorUnaryOperator = //
  // ImageCrop.eq(Tensors.vector(255, 255, 255, 255));
  // Serialization.copy(tensorUnaryOperator);
  // }

  @Test
  void testGrayscale() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(F.C0);
    IAST image = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 0, 0));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.C1)));
  }

  @Test
  void testGrayscale2() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(F.C0);
    IAST image = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 2, 0), F.List(0, 0, 0));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.C1), F.List(F.C2)));
  }

  @Test
  void testColumn() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(F.C0);
    IAST image = F.List(F.List(0, 1, 0), F.List(0, 2, 0), F.List(0, 3, 0));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.C1), F.List(F.C2), F.List(F.C3))); // Tensors.fromString("{{1},
                                                                            // {2}, {3}}"));
  }

  // @Test
  // void testGrayscaleEmpty() {
  // TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(F.C0);
  // IAST image = Array.zeros(5, 6);
  // IAST result = tensorUnaryOperator.apply(image);
  // assertEquals(result, Tensors.empty());
  // }

  @Test
  void testColor() throws ClassNotFoundException {
    IAST image = F.List(F.List(0, 0, 0), F.List(0, 1, 0), F.List(0, 0, 0));
    ColorDataGradients colorGradient = ColorDataGradients.CLASSIC;
    image = image.mapLeaf(S.List, colorGradient);
    // image = Raster.of(image, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq((IAST) image.getPart(1, 1));
    IAST result = tensorUnaryOperator.apply(image);
    assertEquals(result, F.List(F.List(F.List(255, 237, 237, 255)))); // Tensors.fromString("{{{255,237,
                                                                      // 237, 255}}}"));"
  }

  @Test
  void testNoCropGrayscale() {
    TensorUnaryOperator imagecrop = ImageCrop.eq(F.ZZ(123));
    IAST tensor = ResourceData.of("/img/gray15x9.png");
    IAST result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testNoCropRgba() {
    TensorUnaryOperator imagecrop = ImageCrop.eq(F.List(1, 2, 3, 4));
    IAST tensor = ResourceData.of("/img/rgba15x33.png");
    IAST result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testVectorFail() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(F.C1);
    assertThrows(IllegalArgumentException.class, () -> tensorUnaryOperator.apply(F.List(1, 2, 3)));
  }
}
