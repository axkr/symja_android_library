package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Image3D</code>: a volume of voxels, its properties, the filters with one more direction,
 * and the box escape a notebook copies a 3D image as.
 */
public class Image3DTest extends AbstractTestCase {

  /**
   * The cell of a 12 x 10 x 8 "Byte" volume - two blocks of grey with a gap between them - the
   * way a notebook writes the cell of a 3D image:
   * <code>\!\(\*Graphics3DBox[TagBox[Raster3DBox[CompressedData["..."], ...], ...]]\)</code>.
   */
  private static final String BYTE_CELL = //
      "\\!\\(\\*\n"
      + "Graphics3DBox[\n"
      + "TagBox[Raster3DBox[CompressedData[\"\n"
      + "1:eJxTTMoPSmNiYGAo5gASQYnljkVFiZXBAkBOaF5xZnpeaopnXklqemqRRR\n"
      + "IzUBCkiguIeRgGIQgAAmqwSQUpQIDK3rl+6WxS2OSBCiBAZUPMJJ5NHugBAl\n"
      + "Q2xEzi2eSBBUCAyoaYSTybPLAFCKjBHmQAAJFGgBo=\n"
      + "\"], {{0, 10, 8}, {12, 0, 0}}, {0, 255},\n"
      + "ColorFunction->\"GrayLevelDefaultColorFunction\",\n"
      + "Method->{\"FastRendering\" -> True}],\n"
      + "BoxForm`ImageTag[\n"
      + "     \"Byte\", ColorSpace -> \"Grayscale\", Interleaving -> None],\n"
      + "Selectable->False],\n"
      + "AxesStyle->{},\n"
      + "Background->None,\n"
      + "BoxRatios->Automatic,\n"
      + "Boxed->False,\n"
      + "DefaultBaseStyle->\"Image3DGraphics3D\",\n"
      + "ImageSizeRaw->12,\n"
      + "PlotRange->{{0, 12}, {0, 10}, {0, 8}}]\\)";

  /**
   * The cell of a 7 x 7 x 7 volume of reals - three lines through its centre. It names the type
   * <code>"Real"</code>, which is <code>"Real64"</code>, and holds the samples as a packed array
   * of reals.
   */
  private static final String REAL_CELL = //
      "\\!\\(\\*\n"
      + "Graphics3DBox[\n"
      + "TagBox[Raster3DBox[CompressedData[\"\n"
      + "1:eJxTTMoPSmVmYGBgR8LDA3ywH2gXjAIQGI2HwQGGezyQ6z9a6YPJk0qTa9\n"
      + "9g0TdUwHD331ABo/EwOMDQjwcAU9oYPw==\n"
      + "\"], {{0, 7, 7}, {7, 0, 0}}, {0., 1.},\n"
      + "ColorFunction->\"GrayLevelDefaultColorFunction\",\n"
      + "Method->{\"FastRendering\" -> True}],\n"
      + "BoxForm`ImageTag[\n"
      + "     \"Real\", ColorSpace -> Automatic, Interleaving -> None],\n"
      + "Selectable->False],\n"
      + "AxesStyle->{},\n"
      + "Background->None,\n"
      + "BaseStyle->\"Image3DGraphics3D\",\n"
      + "BoxRatios->Automatic,\n"
      + "Boxed->False,\n"
      + "ImageSize->150,\n"
      + "ImageSizeRaw->7,\n"
      + "PlotRange->{{0, 7}, {0, 7}, {0, 7}}]\\)";

  @Test
  public void testImage3D() {
    // the data is {slice, row, column}; the dimensions are {width, depth, height}
    check("v = Image3D({{{0, 0, 0}, {0, 1, 0}}, {{0, 0, 0}, {0, 0, 0}}}); "
        + "{Head(v), ImageQ(v), ImageDimensions(v), ImageType(v), ImageChannels(v), "
        + "ImageColorSpace(v)}", //
        "{Image3D,True,{3,2,2},Bit,1,Grayscale}");
    check("ImageData(v) == {{{0, 0, 0}, {0, 1, 0}}, {{0, 0, 0}, {0, 0, 0}}}", //
        "True");
    check("ImageType /@ {Image3D({{{0, 128}}}), Image3D({{{0.5, 1}}}), "
        + "Image3D({{{0, 1}}}, \"Byte\")}", //
        "{Byte,Real32,Byte}");
    // the samples on the scale of another type
    check("ImageData(Image3D({{{0, 51}, {255, 102}}}, \"Byte\")) == {{{0, 0.2}, {1, 0.4}}}", //
        "True");
    check("ImageData(Image3D({{{0., 0.2}, {1., 0.4}}}), \"Byte\")", //
        "{{{0,51},{255,102}}}");
    // a voxel of three channels
    check("c = Image3D({{{{1, 0, 0}, {0, 1, 0}}}}); "
        + "{ImageDimensions(c), ImageChannels(c), ImageColorSpace(c)}", //
        "{{2,1,1},3,RGB}");
    // the type of a NumericArray is the type of the image
    check("n = Image3D(NumericArray({{{1, 2}, {3, 4}}}, \"UnsignedInteger8\")); "
        + "{ImageType(n), ImageDimensions(n), ImageData(n, \"Byte\")}", //
        "{Byte,{2,2,1},{{{1,2},{3,4}}}}");
    // data of no volume
    check("Image3D({1, 2})", //
        "Image3D({1,2})");
    check("Image3D({{{1, 2}, {3}}})", //
        "Image3D({{{1,2},{3}}})");
  }

  @Test
  public void testMorphology3D() {
    // a voxel grows into the cube around it, cut off at the border of the volume
    check("d = Image3D(ReplacePart(ConstantArray(0, {5, 5, 5}), {3, 3, 3} -> 1)); "
        + "{Total(Flatten(ImageData(Dilation(d, 1)))), ImageType(Dilation(d, 1)), "
        + "ImageData(Erosion(Dilation(d, 1), 1)) == ImageData(d), "
        + "ImageData(Closing(d, 1)) == ImageData(d), Total(Flatten(ImageData(Opening(d, 1))))}", //
        "{27.0,Bit,True,True,0.0}");
    check("Total(Flatten(ImageData(Dilation(Image3D(ReplacePart(ConstantArray(0, {4, 4, 4}), "
        + "{1, 1, 1} -> 1)), 1))))", //
        "8.0");
    // an array of rank 3 is a structuring element; a matrix is one of a single slice
    check("ImageData(Dilation(Image3D({{{0., 0., 1., 0., 0.}}}), {{{1, 1, 0}}})) == "
        + "{{{0, 0, 1, 1, 0}}}", //
        "True");
    check("ImageData(Erosion(Image3D({{{0., 0., 1., 1., 1.}}}), {{1, 1, 0}})) == "
        + "{{{0, 0, 0, 1, 1}}}", //
        "True");
    check("Total(Flatten(ImageData(Dilation(d, ConstantArray(1, {3, 3, 3})))))", //
        "27.0");
  }

  @Test
  public void testFilters3D() {
    check("d = Image3D(ReplacePart(ConstantArray(0., {5, 5, 5}), {3, 3, 3} -> 1.)); "
        + "{Total(Flatten(ImageData(MaxFilter(d, 1)))), Total(Flatten(ImageData(MinFilter(d, 1)))), "
        + "Total(Flatten(ImageData(MedianFilter(d, 1)))), "
        + "Abs(Total(Flatten(ImageData(MeanFilter(d, 1)))) - 1) < 10^-12, "
        + "Abs(ImageData(MeanFilter(d, 1))[[2, 2, 2]] - 1/27) < 10^-15}", //
        "{27.0,0.0,0.0,True,True}");
    // the Gaussian filter is the one of an image along each of the three directions
    check("g = ImageData(GaussianFilter(d, 1)); k = GaussianMatrix(1)[[2]]; "
        + "k = k/Total(k); {Abs(g[[3, 3, 3]] - k[[2]]^3) < 10^-12, "
        + "Abs(g[[2, 3, 4]] - k[[1]]*k[[2]]*k[[3]]) < 10^-12, Abs(Total(Flatten(g)) - 1) < 10^-12}", //
        "{True,True,True}");
    // the linear filter gives reals, the filters of a block keep the type
    check("b = Image3D({{{0, 128}, {255, 64}}}, \"Byte\"); ImageType /@ {GaussianFilter(b, 1), "
        + "MeanFilter(b, 1), MedianFilter(b, 1), MinFilter(b, 1), MaxFilter(b, 1), Closing(b, 1)}", //
        "{Real32,Byte,Byte,Byte,Byte,Byte}");
    check("ImageData(MeanFilter(b, 1), \"Byte\")", //
        "{{{112,112},{112,112}}}");
  }

  @Test
  public void testImage3DBox() {
    // the bytes of a 3 x 2 x 2 volume
    check("img = \\!\\(\\*Graphics3DBox[TagBox[Raster3DBox[CompressedData[\"1:eJxTTMoPSmNiYGAo5gA"
        + "SQYnljkVFiZXBAkBOaF5xZnpeaopnXklqemqRRRIzUJAJikFsBi4ROQ0jG7eAqJT/AFWjD4g=\"], "
        + "{{0, 2, 2}, {3, 0, 0}}, {0, 255}], BoxForm`ImageTag[\"Byte\", "
        + "ColorSpace -> \"Grayscale\", Interleaving -> None], Selectable->False], "
        + "Boxed->False, ImageSizeRaw->3]\\); {ImageDimensions(img), ImageType(img), "
        + "ImageColorSpace(img), ImageData(img, \"Byte\")}", //
        "{{3,2,2},Byte,Grayscale,{{{0,10,20},{30,40,50}},{{60,70,80},{90,100,255}}}}");
  }

  @Test
  public void testPastedImage3D() {
    check("img = " + BYTE_CELL + "; {ImageQ(img), ImageDimensions(img), ImageType(img), "
        + "ImageChannels(img), Dimensions(ImageData(img)), "
        + "Total(ImageData(img, \"Byte\"), 3)}", //
        "{True,{12,10,8},Byte,1,{8,10,12},29600}");
    // slice 3, row 4: the two blocks and the gap
    check("ImageData(img, \"Byte\")[[3, 4]]", //
        "{0,100,100,100,100,0,0,185,175,165,155,0}");
    check("c = Closing(" + BYTE_CELL + ", 1); {Head(c), ImageDimensions(c), ImageType(c), "
        + "Total(ImageData(c, \"Byte\"), 3)}", //
        "{Image3D,{12,10,8},Byte,48720}");
    check("{Total(ImageData(Dilation(img, 1), \"Byte\"), 3), "
        + "Total(ImageData(Erosion(img, 1), \"Byte\"), 3), "
        + "Total(ImageData(Opening(img, 1), \"Byte\"), 3), "
        + "Total(ImageData(Closing(img, 2), \"Byte\"), 3)}", //
        "{92520,4800,27680,90000}");
  }

  /**
   * A 7 x 7 x 7 volume of reals - three lines through its centre; its cell names the type
   * <code>"Real"</code>, which is <code>"Real64"</code>, and holds the samples as a packed array
   * of reals.
   */
  @Test
  public void testPastedRealImage3D() {
    check("img = " + REAL_CELL + "; {ImageDimensions(img), ImageType(img), "
        + "ImageChannels(img), Total(ImageData(img), 3)}", //
        "{{7,7,7},Real64,1,19.0}");
    check("ImageData(img)[[4, 4]] == {1, 1, 1, 1, 1, 1, 1}", //
        "True");
    check("d = Dilation(" + REAL_CELL + ", 1); "
        + "{Head(d), ImageDimensions(d), ImageType(d), Total(ImageData(d), 3)}", //
        "{Image3D,{7,7,7},Real64,135.0}");
  }

  @Test
  public void testRealImageType() {
    // "Real" is the name of "Real64" in the box of an image
    check("{ImageType(Image({{0.1, 0.2}}, \"Real\")), ImageType(Image3D({{{0.1, 0.2}}}, \"Real\")), "
        + "ImageData(Image({{0, 255}}, \"Byte\"), \"Real\")}", //
        "{Real64,Real64,{{0.0,1.0}}}");
  }
}
