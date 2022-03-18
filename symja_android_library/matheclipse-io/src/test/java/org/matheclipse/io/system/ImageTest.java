package org.matheclipse.io.system;

public class ImageTest extends AbstractTestCase {

  public ImageTest(String name) {
    super(name);
  }

  public void testImageGray001() {
    check(
        "Image({{0.1,0.2,0.3},{0.4,0.5,0.6},{0.7,0.8,0.9}})", //
        "Image(Dimensions: 3,3 Transparency: 1)");
  }

  public void testImageGrayNormalDistribution() {
    check(
        "Image(RandomVariate(NormalDistribution(.5, .1), {100, 100}))", //
        "Image(Dimensions: 100,100 Transparency: 1)");
  }

  public void testImageRGB001() {
    check(
        "Image({{{0.1,0.6,0.0},{0.4,0.1,0.8},{0.7,0.9,0.7}}," //
            + "{{1.0,0.0,0.9},{0.6,0.6,1.0},{1.0,0.8,0.3}}},"
            + "ColorSpace->RGB)",
        "Image(Dimensions: 3,2 Transparency: 3)");
  }

  public void testImageRGB002() {
    check("Image(RandomReal(1, {4, 5, 3}))", //
        "Image(Dimensions: 5,4 Transparency: 3)");
  }
}
