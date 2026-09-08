package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Rewriting an image through a Symja function, and cutting it up. */
public class ImageStructureTest extends AbstractTestCase {

  @Test
  public void imageApplyRewritesEverySample() {
    check("ImageData(ImageApply(1-#&,Image({{0.0,1.0}})),\"Byte\")", "{{255,0}}");
    check("ImageData(ImageApply(0.5&,Image({{0.0,1.0}})),\"Byte\")", "{{128,128}}");
  }

  @Test
  public void imageApplyOnAColorImageGetsAChannelList() {
    check("ImageData(ImageApply(Reverse,Image({{{1.0,0.5,0.0}}})),\"Byte\")", //
        "{{{0,128,255}}}");
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
}
