package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Images and colours. */
public class CImageTest extends AbstractTestCase {

  @Test
  public void testRasterizeImageResolution() {
    // a size in printer points covers dpi/72 pixels each
    check(
        "ImageDimensions(Rasterize(Graphics(Disk(), ImageSize -> {50, 30}), "
            + "ImageResolution -> 144))", //
        "{100,60}");
    check(
        "ImageDimensions(Rasterize(Graphics(Disk(), ImageSize -> {50, 30}), "
            + "ImageResolution -> 50))", //
        "{35,21}");
    // a raster size outranks the resolution
    check(
        "ImageDimensions(Rasterize(Graphics(Disk(), ImageSize -> {50, 30}), "
            + "ImageResolution -> 300, RasterSize -> 20))", //
        "{20,12}");
    // RasterSize -> Automatic keeps the drawing's own size, scaled by the resolution
    check(
        "ImageDimensions(Rasterize(Graphics(Disk(), ImageSize -> {50, 30}), "
            + "RasterSize -> Automatic, ImageResolution -> 144))", //
        "{100,60}");
  }

  @Test
  public void testColorConvertLCHColor() {
    check("ColorConvert(LCHColor(0.5, 0.4, 0.25), \"LAB\")", //
        "LABColor(0.5,0.0,0.4)");
    // no chroma is a grey
    check("Chop(List @@ ColorConvert(LCHColor(0.5, 0., 0.3), \"RGB\") - 0.466327, 10^-6)", //
        "{0,0,0}");
    check("ColorConvert(LCHColor(0.7, 0.2, 0.1, 0.5), \"RGB\")", //
        "RGBColor(0.804869,0.628183,0.590564,0.5)");
    check("ColorConvert(RGBColor(0, 0, 1), \"LCH\")", //
        "LCHColor(0.295679,1.31207,0.837135)");
  }

  @Test
  public void testImageEffectPoissonNoise() {
    // a negative or zero value is zero
    check("ImageData(ImageEffect(Image({{0., -0.25}, {0., 0.}}), {\"PoissonNoise\", 0.5}))", //
        "{{0.0,0.0},\n" + " {0.0,0.0}}");
    // a scale outside of 0 < s < 1 is an invalid parameter
    check("Map(Head(Quiet(ImageEffect(Image(Table({0.1, 0.4, 0.9}, {2}, {3})), "
        + "{\"PoissonNoise\", #}))) &, {0., -0.25, 0, 1, 3/2, z})", //
        "{ImageEffect,ImageEffect,ImageEffect,ImageEffect,ImageEffect,ImageEffect}");
    check("Check(ImageEffect(Image({{0.2}}), {\"PoissonNoise\", -0.75}), failed)", //
        "failed");
    // the mean is kept and the variance is v*s/(100*(1-s)), here 0.3/100
    check("i = ImageEffect(Image(Table(0.3, {60}, {60})), {\"PoissonNoise\", 0.5}); "
        + "d = Flatten(ImageData(i)); " + "{ImageType(i), Min(d) >= 0, Abs(Mean(d) - 0.3) < 0.005, "
        + "Abs(Variance(d) - 0.003) < 0.0005}", //
        "{Real32,True,True,True}");
    check("ImageDimensions(ImageEffect(Image(Table(0.6, {3}, {7})), \"PoissonNoise\"))", //
        "{7,3}");
  }
}
