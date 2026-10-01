package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * An image which was copied out of a notebook as text: the box escape
 * <code>\!\(\*GraphicsBox[TagBox[RasterBox[...], BoxForm`ImageTag[...]]]\)</code> is read as the
 * image it is the box of.
 */
public class ImageBoxInputTest extends AbstractTestCase {

  /**
   * A 20 x 16 "Bit" image - a ring with a gap and three dots - written the way a notebook writes
   * the cell of an image, with its line breaks.
   */
  private static final String CELL = "\\!\\(\\*\nGraphicsBox[\nTagBox[RasterBox[CompressedData[\"\n"
      + "1:eJxTTMoPSmNiYGAo5gASQYnljkVFiZXBAkBOaF5xZnpeaopnXklqemqRRR\n"
      + "JIGUhChIFEwAgGECaKECMjqiokigGFjSSIood4MUbc6jDtwOYWrG7G6jdiAA\n" + "BMBAy+\n"
      + "\"], {{0, 16}, {20, 0}}, {0, 1},\nColorFunction->GrayLevel],\n"
      + "BoxForm`ImageTag[\"Bit\", ColorSpace -> Automatic, Interleaving -> None],\n"
      + "Selectable->False],\nBaseStyle->\"ImageGraphics\",\nImageSizeRaw->{20, 16},\n"
      + "PlotRange->{{0, 20}, {0, 16}}]\\)";

  @Test
  public void testPastedImage() {
    check("img = " + CELL + "; "
        + "{ImageDimensions(img), ImageType(img), ImageChannels(img), Total(Flatten(ImageData(img)))}", //
        "{{20,16},Bit,1,63.0}");
    check("ImageData(img)[[3]] == {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0}", //
        "True");
    // the image is an argument like any other one; the closing shuts the gap of the ring
    check(
        "c = Closing(" + CELL + ", DiskMatrix(2)); "
            + "{ImageDimensions(c), ImageType(c), Total(Flatten(ImageData(c)))}", //
        "{{20,16},Bit,109.0}");
    check(
        "{Total(Flatten(ImageData(Dilation(img, DiskMatrix(2))))), "
            + "Total(Flatten(ImageData(Erosion(img, {{1, 1}})))), "
            + "Total(Flatten(ImageData(Opening(img, {{1, 1}, {1, 1}}))))}", //
        "{260.0,44.0,58.0}");
  }

  @Test
  public void testImageBoxes() {
    // the pixels of an image are bytes whatever its type is
    check(
        "ImageData(\\!\\(\\*GraphicsBox[TagBox[RasterBox[CompressedData[\"1:eJxTTMoPSmNiYGAo5gASQ"
            + "YnljkVFiZXBzECOU2ZJEkgGxGYBYgZGRiACEgyMDAAX8Qc4\"], {{0, 3}, {4, 0}}, {0, 1}, "
            + "ColorFunction->GrayLevel], BoxForm`ImageTag[\"Bit\", ColorSpace -> Automatic, "
            + "Interleaving -> None], Selectable->False], BaseStyle->\"ImageGraphics\", "
            + "ImageSizeRaw->{4, 3}, PlotRange->{{0, 4}, {0, 3}}]\\)) == "
            + "{{0, 1, 1, 0}, {1, 1, 1, 1}, {0, 0, 1, 0}}", //
        "True");
    // a list of pixels instead of the compressed ones
    check(
        "ImageData(\\!\\(\\*GraphicsBox[TagBox[RasterBox[{{{1, 2, 3}, {4, 5, 6}}}, "
            + "{{0, 1}, {2, 0}}, {0, 255}, ColorFunction->RGBColor], BoxForm`ImageTag[\"Byte\", "
            + "ColorSpace -> \"RGB\", Interleaving -> True]], ImageSizeRaw->{2, 1}]\\), \"Byte\")", //
        "{{{1,2,3},{4,5,6}}}");
    // the rectangle of an image is turned over; one which isn't has the rows from the bottom
    check(
        "ImageData(\\!\\(\\*GraphicsBox[TagBox[RasterBox[{{0, 1}, {2, 3}}, {{0, 0}, {2, 2}}, "
            + "{0, 255}], BoxForm`ImageTag[\"Byte\"]]]\\), \"Byte\") == {{2, 3}, {0, 1}}", //
        "True");
  }

  @Test
  public void testImageOfNumericArray() {
    // the type of the array is the type of the image
    check("ImageType(Image(NumericArray({{1, 2}}, \"UnsignedInteger8\")))", //
        "Byte");
    check("ImageData(Image(NumericArray({{0, 255}}, \"UnsignedInteger8\")))", //
        "{{0.0,1.0}}");
    check("ImageType(Image(NumericArray({{0.25, 0.5}}, \"Real32\")))", //
        "Real32");
    // unless one is given
    check("ImageType(Image(NumericArray({{0, 1}}, \"UnsignedInteger8\"), \"Bit\"))", //
        "Bit");
    // Writes an image this way in InputForm
    check(
        "img = Image(NumericArray({{0.25, 0.5}}, \"Real32\"), \"Real32\", "
            + "ColorSpace -> Automatic, Interleaving -> None); "
            + "{ImageType(img), ImageChannels(img), ImageData(img) == {{0.25, 0.5}}}", //
        "{Real32,1,True}");
    check(
        "ImageDimensions(Image(Uncompress(\"1:eJxTTMoPSmNiYGAo5gASQYnljkVFiZXBAkBOaF5xZnpe"
            + "aopnXklqemqRRRJIGQgzAzFDw///DQwA2FgPXg==\"), \"Byte\"))", //
        "{3,2}");
  }
}
