package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Rewriting an image through a Symja function, and cutting it up. */
public class ImageStructureTest extends AbstractTestCase {

  @Test
  public void imageApplyRewritesEverySample() {
    check("ImageData(ImageApply(1-#&,Image({{0.0,1.0}})),\"Byte\")", "{{255,0}}");
    check("ImageData(ImageApply(0.5&,Image({{0.0,1.0}})),\"Byte\")", "{{128,128}}");
  }

  /**
   * The result has as many channels as f returns: a color for every gray sample widens the image
   * to RGB, Max of every color narrows it to gray.
   */
  @Test
  public void imageApplyTakesItsChannelsFromTheResult() {
    check("ImageData(ImageApply(If(#==0,{1,1,1},{1,0,0})&, Image({{0,1},{1,0}})))", //
        "{{{1.0,1.0,1.0},{1.0,0.0,0.0}},{{1.0,0.0,0.0},{1.0,1.0,1.0}}}");
    check("ImageChannels(ImageApply(If(#==0,{1,1,1},{1,0,0})&, Image({{0,1},{1,0}})))", //
        "3");
    check("ImageData(ImageApply(Max, Image({{{1,0,0},{0,1,0}}})))", //
        "{{1.0,1.0}}");
    check("ImageChannels(ImageApply(Max, Image({{{1,0,0},{0,1,0}}})))", //
        "1");
    // a masked out gray pixel is repeated into r, g and b
    check("ImageData(ImageApply({#,0,0}&, Image({{0.0,1.0}}), Masking->{{1,0}}),\"Byte\")", //
        "{{{0,0,0},{255,255,255}}}");
    // two channels are no image
    check("Head(ImageApply({#,#}&, Image({{0.0,1.0}})))", //
        "ImageApply");
  }

  @Test
  public void imageApplyOnAColorImageGetsAChannelList() {
    check("ImageData(ImageApply(Reverse,Image({{{1.0,0.5,0.0}}})),\"Byte\")", //
        "{{{0,128,255}}}");
  }

  /**
   * <code>Masking</code> applies the function where the mask is positive and passes every other
   * pixel through. A smaller mask is centred on the image, and a graphic is drawn at the image's
   * size, its drawn pixels being the included ones.
   */
  @Test
  public void imageApplyMasking() {
    check("ImageData(ImageApply(1-#&,Image({{0.25,0.5},{0.75,0.0}}),"
        + "Masking->Image({{1,0},{0,1}})),\"Byte\")", //
        "{{191,128},{191,255}}");
    check("ImageData(ImageApply(1-#&,Image({{0.25,0.5},{0.75,0.0}}),Masking->{{1,0},{0,1}}),"
        + "\"Byte\")", //
        "{{191,128},{191,255}}");
    // All and None restrict nothing
    check("ImageData(ImageApply(1-#&,Image({{0.25,0.5}}),Masking->All),\"Byte\")", //
        "{{191,127}}");
    check("ImageData(ImageApply(1-#&,Image({{0.25,0.5}}),Masking->None),\"Byte\")", //
        "{{191,127}}");
    // a one pixel mask is the centre pixel
    check("ImageData(ImageApply(1-#&,Image({{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}}),"
        + "Masking->Image({{1}})),\"Byte\")", //
        "{{0,0,0},{0,255,0},{0,0,0}}");
    // a colour pixel left out keeps its channels
    check("ImageData(ImageApply(Reverse,Image({{{1.0,0.5,0.0},{1.0,0.5,0.0}}}),"
        + "Masking->Image({{1,0}})),\"Byte\")", //
        "{{{0,128,255},{255,128,0}}}");
    // a disk changes the middle of the image and leaves its corners
    check("d=ImageData(ImageApply(1-#&,ConstantImage(0.0,{11,11}),"
        + "Masking->Graphics(Disk({0,0},1))),\"Byte\");{d[[1,1]],d[[11,11]],d[[6,6]]}", //
        "{0,0,255}");
    check("ImageApply(1-#&,Image({{0.5}}),Foo->1)//Head", //
        "ImageApply");
  }

  /** <code>ConstantImage(v, size)</code> - every pixel the same, grey or coloured. */
  @Test
  public void constantImage() {
    check("ImageDimensions(ConstantImage(Red,{3,2}))", //
        "{3,2}");
    check("ImageData(ConstantImage(Red,2),\"Byte\")", //
        "{{{255,0,0},{255,0,0}},{{255,0,0},{255,0,0}}}");
    check("ImageData(ConstantImage(0.5,{2,1}),\"Byte\")", //
        "{{128,128}}");
    check("ImageData(ConstantImage({0.0,1.0,0.0},1),\"Byte\")", //
        "{{{0,255,0}}}");
  }

  /** The neighbourhood is a matrix, extended past the border by repeating the edge sample. */
  @Test
  public void imageFilterGetsTheNeighbourhood() {
    check("ImageData(ImageFilter(Max(Flatten(#))&,Image({{0.0,1.0},{0.0,0.0}}),1),\"Byte\")", //
        "{{255,255},{255,255}}");
    check("ImageData(ImageFilter(Min(Flatten(#))&,Image({{1.0,1.0},{1.0,0.0}}),1),\"Byte\")", //
        "{{0,0},{0,0}}");
  }

  @Test
  public void imageFilterOfRadiusZeroIsImageApply() {
    check("ImageData(ImageFilter(1-#[[1,1]]&,Image({{0.0,1.0}}),0),\"Byte\")", //
        "{{255,0}}");
  }

  /** <code>ImageScan</code> is run for its side effects and returns Null. */
  @Test
  public void imageScanReturnsNull() {
    // note: not "total", which relaxed syntax resolves to the built-in Total
    check("acc=0;ImageScan((acc+=#)&,Image({{0.0,1.0}}));acc", "1.0");
  }

  @Test
  public void imagePartitionCutsIntoBlocks() {
    check("Dimensions(ImagePartition(Image(ConstantArray(0.0,{4,6})),2))", "{2,3}");
    check("ImageDimensions(ImagePartition(Image(ConstantArray(0.0,{4,6})),2)[[1,1]])", "{2,2}");
  }

  /** A block that would run past the right or bottom edge is dropped. */
  @Test
  public void aPartialBlockIsDropped() {
    check("Dimensions(ImagePartition(Image(ConstantArray(0.0,{5,5})),2))", "{2,2}");
  }

  @Test
  public void imagePartitionTakesAnOffset() {
    check("Dimensions(ImagePartition(Image(ConstantArray(0.0,{4,4})),2,1))", "{3,3}");
  }

  @Test
  public void imageAssembleJoinsTheBlocks() {
    check("ImageDimensions(ImageAssemble({{Image({{0.0,0.0}}),Image({{1.0,1.0}})}}))", //
        "{4,1}");
    check("ImageData(ImageAssemble({{Image({{0.0}}),Image({{1.0}})}}),\"Byte\")", //
        "{{0,255}}");
    check("ImageDimensions(ImageAssemble({{Image({{0.0}})},{Image({{1.0}})}}))", //
        "{1,2}");
  }

  /** A flat list of images is one row. */
  @Test
  public void aFlatListIsARow() {
    check("ImageDimensions(ImageAssemble({Image({{0.0}}),Image({{1.0}})}))", "{2,1}");
  }

  @Test
  public void blocksThatDoNotLineUpDoNotAssemble() {
    check("ImageAssemble({{Image({{0.0}}),Image({{1.0},{1.0}})}})", //
        "ImageAssemble({{Image(Dimensions: 1,1 Transparency: 1),Image(Dimensions: 1,2 Transparency: 1)}})");
  }

  /**
   * An image goes out to a front end as <code>Image(data, "type", options)</code>, whose pixels can
   * be read: its string form is only a summary, and the WLJS notebook could neither show it nor
   * texture a plot with it.
   */
  @Test
  public void testImageIsExportedWithItsPixels() {
    check("json=ExportString(Texture(Image({{0,1},{1,0}},\"Bit\")),\"ExpressionJSON\",\"Compact\"->True);"
        + "{StringContainsQ(json,\"'Bit'\"),StringContainsQ(json,\"Dimensions\")}", //
        "{True,False}");
  }

  /**
   * What the WLJS notebook asks of an image before it shows one: its properties through
   * <code>Information</code>, the same picture as a <code>"Byte"</code> image, and a
   * <code>MakeBoxes</code> rule written for <code>Image(data, type, ...)</code> applied to the
   * image object.
   */
  @Test
  public void testTheNotebookCanShowAnImage() {
    check("Information(Image({{0,1},{1,0}},\"Bit\"),\"DataType\")", //
        "Bit");
    check("Information(Image({{0,1},{1,0}},\"Bit\"))[\"Dimensions\"]", //
        "{2,2}");
    check("ImageType(Image(Image({{0,1},{1,0}},\"Bit\"),\"Byte\",Interleaving->True))", //
        "Byte");
    check("Unprotect(Image);Image /: MakeBoxes(i:Image(_,t_,___),StandardForm) := t;"
        + "ToBoxes(Image({{0,1},{1,0}},\"Bit\"),StandardForm)", //
        "Bit");
    check("Image /: MakeBoxes(i:Image(_,t_,___),StandardForm) =.;"
        + "ToBoxes(Image({{0,1},{1,0}},\"Bit\"),StandardForm)===\"Bit\"", //
        "False");
  }

}
