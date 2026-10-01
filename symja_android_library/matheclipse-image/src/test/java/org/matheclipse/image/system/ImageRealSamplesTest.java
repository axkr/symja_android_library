package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * Images derived from an image of real samples keep the samples: an operation which moves pixels
 * around doesn't round them to the <code>1/255</code> steps of the bitmap an image is shown with.
 */
public class ImageRealSamplesTest extends AbstractTestCase {

  @Test
  public void testImageReflect() {
    check(
        "a = Image({{1, 2, 3}, {4, 5, 6}}/10.); "
            + "ImageData(ImageReflect(a)) == {{4, 5, 6}, {1, 2, 3}}/10.", //
        "True");
    check("ImageData(ImageReflect(a, Left)) == {{3, 2, 1}, {6, 5, 4}}/10.", //
        "True");
    check("ImageData(ImageReflect(a, Top -> Left)) == {{1, 4}, {2, 5}, {3, 6}}/10.", //
        "True");
    check("{ImageType(ImageReflect(a)), ImageDimensions(ImageReflect(a, Top -> Left))}", //
        "{Real32,{2,3}}");
  }

  @Test
  public void testImagePad() {
    check(
        "a = Image({{1, 2, 3}, {4, 5, 6}}/10.); "
            + "ImageData(ImagePad(a, 1)) == {{0, 0, 0, 0, 0}, {0, 1, 2, 3, 0}, {0, 4, 5, 6, 0}, "
            + "{0, 0, 0, 0, 0}}/10.", //
        "True");
    // "Fixed" repeats the pixels of the border
    check(
        "ImageData(ImagePad(a, 1, \"Fixed\")) == {{1, 1, 2, 3, 3}, {1, 1, 2, 3, 3}, "
            + "{4, 4, 5, 6, 6}, {4, 4, 5, 6, 6}}/10.", //
        "True");
    check(
        "ImageData(ImagePad(a, 1, 0.5)) == {{5, 5, 5, 5, 5}, {5, 1, 2, 3, 5}, {5, 4, 5, 6, 5}, "
            + "{5, 5, 5, 5, 5}}/10.", //
        "True");
    check(
        "ImageData(ImagePad(a, {{1, 0}, {0, 0}}, \"Periodic\")) == "
            + "{{3, 1, 2, 3}, {6, 4, 5, 6}}/10.", //
        "True");
    // a Byte image stays one
    check(
        "ImageData(ImagePad(Image({{10, 20}}, \"Byte\"), {{1, 1}, {0, 0}}, \"Fixed\"), "
            + "\"Byte\")", //
        "{{10,10,20,20}}");
  }

  @Test
  public void testImageCrop() {
    check(
        "a = Image({{1, 2, 3}, {4, 5, 6}}/10.); "
            + "ImageData(ImageCrop(ImagePad(a, 2), ImageDimensions(a))) == ImageData(a)", //
        "True");
    // {{0., 0.5}}
    check("ImageData(ImageCrop(Image({{1, 1, 1, 1}, {1, 0, 0.5, 1}, {1, 1, 1, 1}}))) // InputForm", //
        "{{0.0`,0.5`}}");
    check("ImageData(ImageTake(a, {2, 2}, {2, 3})) == {{5, 6}}/10.", //
        "True");
  }

  @Test
  public void testImageDataOfRealImage() {
    // {{1., 0., 0.5}} - an image of reals has real samples
    check("ImageData(Image({{1, 0, 0.5}})) // InputForm", //
        "{{1.0`,0.0`,0.5`}}");
  }

  @Test
  public void testPlanarImage() {
    // an image which stores its channels as planes has the same samples pixel by pixel
    check(
        "pl = Image({{{0.1, 0.2}, {0.3, 0.4}}, {{0.5, 0.6}, {0.7, 0.8}}, "
            + "{{0.9, 1.0}, {0.0, 0.1}}}, Interleaving -> False); "
            + "ImageData(pl, Interleaving -> True) == "
            + "{{{0.1, 0.5, 0.9}, {0.2, 0.6, 1.0}}, {{0.3, 0.7, 0.0}, {0.4, 0.8, 0.1}}}", //
        "True");
    check(
        "ImageData(Image({{{0.1, 0.5, 0.9}, {0.2, 0.6, 1.0}}}), Interleaving -> False) == "
            + "{{{0.1, 0.2}}, {{0.5, 0.6}}, {{0.9, 1.0}}}", //
        "True");
    check(
        "ImageData(ImageReflect(pl), Interleaving -> True) == "
            + "{{{0.3, 0.7, 0.0}, {0.4, 0.8, 0.1}}, {{0.1, 0.5, 0.9}, {0.2, 0.6, 1.0}}}", //
        "True");
    // the filters read it pixel by pixel as well
    check(
        "ImageData(Erosion(pl, 1), Interleaving -> True) == "
            + "{{{0.1, 0.5, 0.0}, {0.1, 0.5, 0.0}}, {{0.1, 0.5, 0.0}, {0.1, 0.5, 0.0}}}", //
        "True");
  }

  @Test
  public void testImageResizeShrink() {
    // {{0.5, 0.5}, {0.5, 0.5}}
    check(
        "ImageData(ImageResize(Image(Table(Mod(x + y, 2) + 0., {y, 4}, {x, 4})), {2, 2})) == "
            + "{{0.5, 0.5}, {0.5, 0.5}}", //
        "True");
  }

  @Test
  public void testAlphaChannel() {
    // has an image of 2 channels here, {{{1., 0.5}, {1., 0.5}}}; an image with an alpha
    // channel has the three colour channels in Symja
    check(
        "half = SetAlphaChannel(Image({{1., 1.}}), 0.5); "
            + "{ImageType(half), ImageData(half) == {{{1., 1., 1., 0.5}, {1., 1., 1., 0.5}}}}", //
        "{Real32,True}");
    // RemoveAlphaChannel::invcolor - a number is no colour
    check("RemoveAlphaChannel(half, 0.)", //
        "RemoveAlphaChannel(Image(Dimensions: 2,1 Transparency: 3),0.0)");
  }

  @Test
  public void testImageAssemble() {
    check(
        "ImageData(ImageAssemble({{Image({{0.1}}), Image({{0.3}})}, "
            + "{Image({{0.7}}), Image({{0.9}})}})) == {{0.1, 0.3}, {0.7, 0.9}}", //
        "True");
    // ImageAssemble::row: Expecting images of the same height in one row.
    check("p = Image({{1., 1.}, {1., 1.}}); " + "Head(ImageAssemble({{p, p}, {p, Image({{1.}})}}))", //
        "ImageAssemble");
    check("ImageDimensions(ImageAssemble({{p, p}, {p, p}}))", //
        "{4,4}");
  }
}
