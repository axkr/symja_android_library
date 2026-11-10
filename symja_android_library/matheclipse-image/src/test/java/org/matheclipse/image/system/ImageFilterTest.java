package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * The filters of an image, the enlargement by whole factors and the grey level of real samples.
 */
public class ImageFilterTest extends AbstractTestCase {

  @Test
  public void testGaussianFilter() {
    // {{0,0,0,0,0},{0,0.00987648,0.0796275,0.00987648,0},{0,0.0796275,0.641984,0.0796275,0},...}
    check(
        "d = Image(ReplacePart(Table(0., {5}, {5}), {3, 3} -> 1.)); "
            + "Max(Abs(ImageData(GaussianFilter(d, 1)) - {{0,0,0,0,0}, "
            + "{0,0.00987648032605648,0.07962752133607864,0.00987648032605648,0}, "
            + "{0,0.07962752133607864,0.6419840455055237,0.07962752133607864,0}, "
            + "{0,0.00987648032605648,0.07962752133607864,0.00987648032605648,0}, "
            + "{0,0,0,0,0}})) < 10^-7", //
        "True");
    check(
        "Max(Abs(ImageData(GaussianFilter(d, 1))[[2 ;; 4, 2 ;; 4]] - GaussianMatrix(1))) < 10^-15", //
        "True");
    check("{ImageType(GaussianFilter(d, 1)), ImageDimensions(GaussianFilter(d, 1))}", //
        "{Real32,{5,5}}");
    // every channel of a colour image is filtered
    check(
        "ImageChannels(GaussianFilter(Image({{{1., 0., 0.}, {0., 1., 0.}}, "
            + "{{0., 0., 1.}, {1., 1., 1.}}}), 1))", //
        "3");
  }

  @Test
  public void testLaplacianFilter() {
    // the impulse response is the kernel: the axis taps 2*(-1)^(k+1)/k^2 and -2*Pi^2/3 in the
    // centre, shifted by one constant so that the 3x3 block sums to zero
    check("k = ImageData(LaplacianFilter(Image(ReplacePart(Table(0., {7}, {7}), {4, 4} -> 1.)), 1)); "
        + "c = -(8 - 2*Pi^2/3)/9; "
        + "Max(Abs(k[[3 ;; 5, 3 ;; 5]] - ({{0, 2, 0}, {2, -2*Pi^2/3, 2}, {0, 2, 0}} + c))) < 10^-6", //
        "True");
    // a constant image has no curvature
    check("Max(Abs(ImageData(LaplacianFilter(Image(Table(0.3, {6}, {4})), 2))))  < 10^-6", //
        "True");
  }

  @Test
  public void testImageEffect() {
    // Solarization inverts the pixels above the threshold, a colour pixel by the mean of its
    // channels
    check("Chop(ImageData(ImageEffect(Image({{0.2, 0.45, 0.55, 0.8}}), \"Solarization\")) - "
        + "{{0.2, 0.45, 0.45, 0.2}}, 10^-6)", //
        "{{0,0,0,0}}");
    check("Chop(ImageData(ImageEffect(Image({{0.2, 0.45, 0.55, 0.8}}), {\"Solarization\", 0.25})) - "
        + "{{0.2, 0.55, 0.45, 0.2}}, 10^-6)", //
        "{{0,0,0,0}}");
    check("Chop(ImageData(ImageEffect(Image({{{0.3, 0.7, 0.8}, {0.6, 0.2, 0.4}}}), {\"Solarization\", 0.5})) - "
        + "{{{0.7, 0.3, 0.2}, {0.6, 0.2, 0.4}}}, 10^-6)", //
        "{{{0,0,0},{0,0,0}}}");
    // OilPainting: the most frequent value of the block inside the image, the pixel's own on a tie
    check("Round(5*ImageData(ImageEffect(Image(Table(Mod(i + 2*j, 5)/5., {i, 4}, {j, 4})), "
        + "{\"OilPainting\", 1})))", //
        "{{3,3,0,4},{0,0,0,4},{1,0,0,0},{1,0,2,2}}");
    check("Max(#) - Min(#) &(ImageData(ImageEffect(Image({{0.3, 0.3, 0.3}, {0.3, 0.8, 0.3}, {0.3, 0.3, 0.3}}), "
        + "{\"OilPainting\", 1})))", //
        "0.0");
    check("{ImageType(#), ImageColorSpace(#)}&(ImageEffect(Image({{0.1, 0.9}}), \"Solarization\"))", //
        "{Real32,Grayscale}");
    check("Head(ImageEffect(Image({{0.5}}), \"NoSuchEffect\"))", //
        "ImageEffect");
  }

  @Test
  public void testBinarizeFunctionAndDistanceTransformGraphics() {
    // the function gets the value of a gray pixel and the channels of a colour pixel
    check("ImageData(Binarize(Image({{0.2, 0.5, 0.7}}), # >= 0.5 &))", //
        "{{0,1,1}}");
    check("ImageData(Binarize(Image({{{0.2, 0.9, 0}, {0.8, 0.1, 0}}}), #[[2]] > 0.5 &))", //
        "{{1,0}}");
    // graphics are rasterized first
    check("g = Graphics({Black, Rectangle()}); "
        + "ImageDimensions(DistanceTransform(g)) == ImageDimensions(Rasterize(g))", //
        "True");
  }

  @Test
  public void testConvolveAndCorrelate1() {
    // the convolution reflects the kernel, the correlation doesn't
    check("delta = Image({{0., 1., 0.}}); {ImageData(ImageConvolve(delta, {{1, 2, 3}})), "
        + "ImageData(ImageCorrelate(delta, {{1, 2, 3}}))} == " + "{{{1., 2., 3.}}, {{3., 2., 1.}}}", //
        "True");
    // the border continues with its own pixels, so a constant image stays constant
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.3, 0.3, 0.3}, {0.3, 0.3, 0.3}}), "
            + "{{0.25, 0.5, 0.25}})) - 0.3)) < 10^-15", //
        "True");
  }

  @Test
  public void testConvolveAndCorrelate2() {
    // {{0.4, 0.7, 1.}} - the border is continued with its own pixels
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.1, 0.2, 0.4}}), {{1, 1, 1}})) "
            + "- {{0.4, 0.7, 1.}})) < 10^-7", //
        "True");
    // a kernel of even length: centres the convolution on its first element of the two in the
    // middle and the correlation on the second one
    // {{0.3, 0.4, 0.8}}
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.1, 0.2, 0.4}}), {{1, 2}})) "
            + "- {{0.3, 0.4, 0.8}})) < 10^-7", //
        "True");
    // {{0.3, 0.5, 1.}}
    check(
        "Max(Abs(ImageData(ImageCorrelate(Image({{0.1, 0.2, 0.4}}), {{1, 2}})) "
            + "- {{0.3, 0.5, 1.}})) < 10^-7", //
        "True");
  }

  @Test
  public void testFilterTypes() {
    // {Real32,Byte,Byte,Byte,Real32,Real32} - the linear filters return reals, the filters
    // of a block keep the type
    check(
        "b = Image({{0, 128, 255}, {64, 192, 32}}, \"Byte\"); "
            + "ImageType /@ {GaussianFilter(b, 1), MeanFilter(b, 1), MedianFilter(b, 1), "
            + "MinFilter(b, 1), ImageConvolve(b, {{1}}), ImageCorrelate(b, {{1}})}", //
        "{Real32,Byte,Byte,Byte,Real32,Real32}");
    // {{19,131,223},{70,160,67}}
    check("ImageData(GaussianFilter(b, 1), \"Byte\")", //
        "{{19,131,223},{70,160,67}}");
  }

  @Test
  public void testRankAndMeanFilter1() {
    // the median removes an outlier
    check(
        "spike = Image({{0.2, 0.2, 0.2}, {0.2, 1., 0.2}, {0.2, 0.2, 0.2}}); "
            + "Union(Flatten(ImageData(MedianFilter(spike, 1))))", //
        "{0.2}");
    check(
        "{Union(Flatten(ImageData(MaxFilter(Image({{0., 1., 0.}, {0., 0., 0.}}), 1)))), "
            + "Union(Flatten(ImageData(MinFilter(Image({{1., 0., 1.}, {1., 1., 1.}}), 1))))}", //
        "{{1.0},{0.0}}");
    // a list is still filtered by the list filter
    check("MeanFilter({0., 0., 1., 0., 0.}, 1)", //
        "{0.0,0.333333,0.333333,0.333333,0.0}");
  }

  @Test
  public void testRankAndMeanFilter2() {
    // at the border the block is cut off, the image isn't continued:
    // {{0.375, 0.375}, {0.375, 0.375}}
    check(
        "ImageData(MeanFilter(Image({{0.1, 0.2}, {0.4, 0.8}}), 1)) == "
            + "{{0.375, 0.375}, {0.375, 0.375}}", //
        "True");
    // the median of an even number of samples is the upper one of the two in the middle:
    // {{0.4, 0.4}, {0.4, 0.4}}
    check(
        "ImageData(MedianFilter(Image({{0.1, 0.2}, {0.4, 0.8}}), 1)) == "
            + "{{0.4, 0.4}, {0.4, 0.4}}", //
        "True");
  }

  @Test
  public void testImageResizeWholeFactors() {
    // every pixel four times, exactly
    check(
        "ImageData(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), {6, 4})) == "
            + "{{0.1,0.1,0.2,0.2,0.3,0.3}, {0.1,0.1,0.2,0.2,0.3,0.3}, "
            + "{0.4,0.4,0.5,0.5,0.6,0.6}, {0.4,0.4,0.5,0.5,0.6,0.6}}", //
        "True");
    check(
        "ImageData(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), {6, 4}, "
            + "Resampling -> \"Nearest\")) == "
            + "{{0.1,0.1,0.2,0.2,0.3,0.3}, {0.1,0.1,0.2,0.2,0.3,0.3}, "
            + "{0.4,0.4,0.5,0.5,0.6,0.6}, {0.4,0.4,0.5,0.5,0.6,0.6}}", //
        "True");
    check("ImageDimensions(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), 12))", //
        "{12,8}");
  }

  @Test
  public void testImageResizeFit() {
    // {{wmax}, {hmax}}: as large as fits inside, with the aspect ratio kept
    check("ImageDimensions(ImageResize(Image(Table(0.1, {30}, {60})), {{20}, {20}}))", //
        "{20,10}");
    check("ImageDimensions(ImageResize(Image(Table(0.1, {30}, {60})), {{200}, {15}}))", //
        "{30,15}");
    check("ImageDimensions(ImageResize(Image(Table(0.1, {30}, {60})), {{120}, {120}}))", //
        "{120,60}");
  }

  @Test
  public void testImageEffectMotionBlur() {
    // the mean along a line through each pixel leaves a constant image as it is
    check("ImageData(ImageEffect(Image(Table(0.75, {3}, {5})), {\"MotionBlur\", 2, 45})) == "
        + "Table(0.75, {3}, {5})", //
        "True");
    // a vertical line for 90 degrees, the border pixels continue the image
    check("ImageData(ImageEffect(Image({{0., 0., 0.}, {0., 0.9, 0.}, {0., 0., 0.}}), "
        + "{\"MotionBlur\", 1, 90}))", //
        "{{0.0,0.3,0.0},\n {0.0,0.3,0.0},\n {0.0,0.3,0.0}}");
    check("ImageData(ImageEffect(Image({{0., 0.9, 0.}}), {\"MotionBlur\", 1}))", //
        "{{0.3,0.3,0.3}}");
  }

  @Test
  public void testGrayscaleOfRealSamples() {
    // {{0.299, 0.587}, {0.114, 1.}}
    check("rgb = Image({{{1., 0., 0.}, {0., 1., 0.}}, {{0., 0., 1.}, {1., 1., 1.}}}); "
        + "Max(Abs(ImageData(ColorConvert(rgb, \"Grayscale\")) - {{0.299, 0.587}, {0.114, 1.}})) "
        + "< 10^-15", //
        "True");
    check("ImageChannels(ColorConvert(rgb, \"Grayscale\"))", //
        "1");
    // a Byte image stays on its 8 bit scale
    check("ImageData(ColorConvert(Image({{{255, 0, 0}}}, \"Byte\"), \"Grayscale\"), \"Byte\")", //
        "{{76}}");
  }

  @Test
  public void testGeometricMeanFilter() {
    // a list is filtered by the list filter, on the part of the window inside the list
    check("GeometricMeanFilter({1, 4, 16, 2}, 1)", //
        "{2,4,4*2^(1/3),4*Sqrt(2)}");
    check("GeometricMeanFilter({{1., 4.}, {2., 8.}}, 1)", //
        "{{2.82843,2.82843},{2.82843,2.82843}}");
    // the geometric mean of the part of the 3 x 3 block inside the image
    check("Max(Abs(ImageData(GeometricMeanFilter(Image({{0.2, 0.8, 0.4}, {0.1, 0.5, 0.25}}), 1)) "
        + "- {{(0.2*0.8*0.1*0.5)^(1/4), (0.2*0.8*0.4*0.1*0.5*0.25)^(1/6), (0.8*0.4*0.5*0.25)^(1/4)}, "
        + "{(0.2*0.8*0.1*0.5)^(1/4), (0.2*0.8*0.4*0.1*0.5*0.25)^(1/6), (0.8*0.4*0.5*0.25)^(1/4)}})) "
        + "< 10^-7", //
        "True");
    check("ImageType(GeometricMeanFilter(Image({{0.2, 0.8}, {0.1, 0.5}}), 1))", //
        "Real32");
  }

  @Test
  public void testInpaintDiffusion() {
    // a single hole becomes the mean of its eight neighbours
    check("img = Image({{0.2, 0.4, 0.6}, {0.8, 0., 1.}, {0.1, 0.3, 0.5}}); "
        + "m = Image({{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}); "
        + "ImageData(Inpaint(img, m, Method -> \"Diffusion\"))[[2, 2]]", //
        "0.4875");
    // the filled pixels are harmonic: each is the mean of its neighbours, filled ones included
    check("img = Image(Table(N(i*j/25), {i, 5}, {j, 5})); "
        + "m = Table(Boole(2 <= i <= 4 && 2 <= j <= 3), {i, 5}, {j, 5}); "
        + "d = ImageData(Inpaint(img, m, Method -> \"Diffusion\")); "
        + "Max(Table(Abs(d[[i, j]] - (Total(Flatten(d[[i - 1 ;; i + 1, j - 1 ;; j + 1]])) - d[[i, j]])/8), "
        + "{i, 2, 4}, {j, 2, 3})) < 10^-9", //
        "True");
    // the known pixels stay
    check("ImageData(Inpaint(img, m, Method -> \"Diffusion\"))[[1]] == ImageData(img)[[1]]", //
        "True");
  }

  /** Of equally common samples that of the pixel itself, else the first one row by row. */
  @Test
  public void commonestFilter() {
    check("ImageData(CommonestFilter(Image({{0.5, 0.25, 0.25, 0.75, 0.75}}), -1.5))", //
        "{{0.25,0.25,0.25,0.75,0.75}}");
    check("ImageData(CommonestFilter(Image({{0.5, 0.25, 0.75}}), 1))", //
        "{{0.5,0.25,0.75}}");
    check("ImageData(CommonestFilter(Image({{0.25, 0.25, 0.5}}), 1), Real32) === "
        + "ImageData(CommonestFilter(Image({{0.25, 0.25, 0.5}}), 0.5), Real32)", //
        "True");
    check("{ImageData(CommonestFilter(Image({{10, 10, 20, 30, 30}}, \"Byte\"), 1), \"Byte\"), "
        + "ImageType(CommonestFilter(Image({{10, 10}}, \"Byte\"), 1))}", //
        "{{{10,10,20,30,30}},Byte}");
  }

  /** The samples of an image of reals are single precision ones. */
  @Test
  public void commonestFilterIsSinglePrecision() {
    check("InputForm(ImageData(CommonestFilter(Image({{0.3, 0.3}}), 1)))", //
        "{{0.30000001192092896`,0.30000001192092896`}}");
  }
  @Test
  public void testImageDeconvolve() {
    // an image of at least 3 x 3 pixels with a kernel of odd size is deconvolved like its data
    check("img = Image({{0.3, 0.8, 0.1, 0.6, 0.2}, {0.9, 0.4, 0.7, 0.2, 0.5}, "
        + "{0.1, 0.6, 0.3, 0.8, 0.4}, {0.5, 0.2, 0.9, 0.1, 0.7}}); "
        + "ker = {{1, 2, 1}, {2, 6, 2}, {1, 2, 1}}/18.; "
        + "Max(Abs(ImageData(ImageDeconvolve(img, ker)) - ListDeconvolve(ker, ImageData(img)))) "
        + "< 10^-6", //
        "True");
    check("{ImageType(ImageDeconvolve(img, ker)), ImageDimensions(ImageDeconvolve(img, ker))}", //
        "{Real32,{5,4}}");
    // a narrow image is padded by one pixel
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.3, 0.6}}), {{1, 1}}/2)) - "
        + "{{0.15, 0.45}})) < 10^-6", //
        "True");
    // the parameter search ends at the bound of its interval, which the reference reaches
    // slightly differently
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6, 0.8}}), {{1, 2, 1}}/4)) - "
        + "{{0.22463607788085938, 0.3921552300453186, 0.7245229482650757, 1.042121171951294}})) "
        + "< 10^-3", //
        "True");
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6, 0.8}}), {{1, 2, 1}}/4, "
        + "Padding -> 0)) - {{0.10580554604530334, 0.29642319679260254, 0.4963168799877167, "
        + "0.5940519571304321}})) < 10^-3", //
        "True");
    // a kernel with a dimension of one element is made even: a zero before that dimension, the
    // other one loses its last element
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6, 0.8}}), {{1, 2, 3}}/6, "
        + "Method -> {\"Tikhonov\", 0.1})) - {{0.25067800283432007, 0.628144383430481, "
        + "1.065895438194275, 1.315256118774414}})) < 10^-6", //
        "True");
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6, 0.8, 0.3}}), "
        + "{{1, 2, 3, 2, 1}}/9, Method -> {\"Tikhonov\", 0.1})) - {{0.3623536229133606, "
        + "-0.3080054521560669, 1.3966586589813232, 0.9617049694061279, 0.04895016551017761}})) "
        + "< 10^-6", //
        "True");
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2}, {0.4}, {0.6}, {0.8}}), "
        + "{{1}, {2}, {1}}/4, Method -> {\"Tikhonov\", 0.1})) - {{0.17783278226852417}, "
        + "{0.42080453038215637}, {0.7364920973777771}, {0.8790464997291565}})) < 10^-6", //
        "True");
    // a single element on an image which is not padded
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.3, 0.8, 0.1, 0.6}, {0.9, 0.4, 0.7, 0.2}, "
        + "{0.1, 0.6, 0.3, 0.8}, {0.5, 0.2, 0.9, 0.1}}), {{1}}, Method -> {\"Tikhonov\", 0.1})) - "
        + "{{0.3, 0.8, 0.1, 0.6}, {0.9, 0.4, 0.7, 0.2}, {0.1, 0.6, 0.3, 0.8}, {0.5, 0.2, 0.9, 0.1}}"
        + "/1.01)) < 10^-6", //
        "True");
    // with an explicit parameter an image of 3 x 3 pixels is deconvolved like its data
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6}, {0.8, 0.1, 0.3}, "
        + "{0.5, 0.7, 0.9}}), {{0, 1/4., 0}, {1/4., 0, 1/4.}, {0, 1/4., 0}}, "
        + "Method -> {\"DampedLS\", 0.5})) - {{0.3288981020450592, 0.3507276177406311, "
        + "0.4212057590484619}, {0.4430353045463562, 0.48918914794921875, 0.5353429913520813}, "
        + "{0.6058211326599121, 0.6276506781578064, 0.6981288194656372}})) < 10^-6", //
        "True");
    // a single element on a padded image of two rows
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.2, 0.4, 0.6, 0.8}, {0.1, 0.3, 0.5, 0.7}}), "
        + "{{1}}, Method -> {\"Tikhonov\", 0.1})) - "
        + "{{0.2, 0.4, 0.6, 0.8}, {0.1, 0.3, 0.5, 0.7}}/1.01)) < 10^-6", //
        "True");
    // a kernel of two rows and columns: the image is padded, the rows are centred on the first
    // middle row and the columns on the second
    check("img4 = Image({{0.3, 0.8, 0.1, 0.6}, {0.9, 0.4, 0.7, 0.2}, {0.1, 0.6, 0.3, 0.8}, "
        + "{0.5, 0.2, 0.9, 0.1}}); "
        + "Max(Abs(ImageData(ImageDeconvolve(img4, {{1, 2}, {0, 0}}/3, "
        + "Method -> {\"Tikhonov\", 0.1})) - {{-0.15353578329086304, 1.2238737344741821, "
        + "-0.11122274398803711, 0.5591879487037659}, {1.2422360181808472, 0.17538154125213623, "
        + "0.861417829990387, 0.33230987191200256}, {-0.2521369457244873, 0.8147175908088684, "
        + "0.12868107855319977, 0.6577891707420349}, {0.8815309405326843, -0.2992388904094696, "
        + "1.230031967163086, 0.16880714893341064}})) < 10^-6", //
        "True");
    check("Max(Abs(ImageData(ImageDeconvolve(img4, {{1, 0}, {2, 0}}/3, "
        + "Method -> {\"Tikhonov\", 0.1})) - {{1.3519740104675293, 1.3519741296768188, "
        + "0.24062669277191162, 0.9789725542068481}, {-0.07568679749965668, -0.07568676769733429, "
        + "0.7245500683784485, 0.0908229798078537}, {0.4906488060951233, 0.4906487762928009, "
        + "0.31539371609687805, 0.7398828268051147}, {0.4769853949546814, 0.4769855737686157, "
        + "-0.027226924896240234, 1.1761749982833862}})) < 10^-6", //
        "True");
    check("Max(Abs(ImageData(ImageDeconvolve(Image({{0.3, 0.8, 0.1, 0.6}, {0.9, 0.4, 0.7, 0.2}, "
        + "{0.1, 0.6, 0.3, 0.8}}), {{1, 2}, {3, 4}}/10, Method -> {\"Tikhonov\", 0.1})) - "
        + "{{1.7467577457427979, 0.23413310945034027, 0.7015299201011658, 0.7426826357841492}, "
        + "{0.058899667114019394, 0.5812889933586121, 0.4316929876804352, 0.331560343503952}, "
        + "{-0.7907137274742126, 0.901678204536438, 0.041472792625427246, 0.715474545955658}})) "
        + "< 10^-6", //
        "True");
    // an integer type keeps its type
    check("b = Image({{2, 0, 3, 1}, {0, 4, 1, 2}, {1, 1, 0, 3}, {3, 0, 2, 1}}, \"Byte\"); "
        + "k3 = {{0, 1, 0}, {1, 4, 1}, {0, 1, 0}}/8.; "
        + "{ImageType(ImageDeconvolve(b, k3)), ImageData(ImageDeconvolve(b, k3), \"Byte\") == "
        + "Clip(Round(ListDeconvolve(k3, ImageData(b, \"Byte\"))), {0, 255})}", //
        "{Byte,True}");
    // a vector is no kernel of an image
    check("Check(ImageDeconvolve(Image({{0.2, 0.7}}), {1, 2, 1}), failed, "
        + "ImageDeconvolve::klinv)", //
        "failed");
    check("ImageDeconvolve(7, {{1}})", //
        "ImageDeconvolve(7,{{1}})");
  }
}
