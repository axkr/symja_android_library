package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** <code>ImageLines</code> and <code>HighlightImage</code>. */
public class ImageFeaturesTest extends AbstractTestCase {

  /**
   * A line of pixels is found as the line through their centres, cut off at the border of the
   * image: row 2 of an image 12 pixels high is at the height 12 - 2 + 1/2.
   */
  @Test
  public void imageLinesFindsTheDominantLines() {
    check("ImageLines(Image(Table(If(x==3||y==2,1,0),{y,12},{x,16})))", //
        "{Line({{0.0,10.5},{16.0,10.5}}),Line({{2.5,12.0},{2.5,0.0}})}");
    check("ImageLines(Image(Table(If(x==3||y==2||x==y,1,0),{y,12},{x,16})))", //
        "{Line({{0.0,10.5},{16.0,10.5}}),Line({{0.0,12.0},{12.0,0.0}}),Line({{2.5,12.0},{2.5,0.0}})}");
    // a line three pixels wide is one line
    check("ImageLines(Image(Table(If(6<=y<=8,1,0),{y,14},{x,30})))", //
        "{Line({{0.0,7.5},{30.0,7.5}})}");
    // a grey line is weaker, but still a line
    check("ImageLines(Image(Table(If(x==6,0.5,0),{y,11},{x,11})))", //
        "{Line({{5.5,11.0},{5.5,0.0}})}");
  }

  /**
   * The second argument is the part of the diagonal of the image a line has to cover;
   * <code>MaxFeatures</code> keeps the strongest lines.
   */
  @Test
  public void imageLinesThresholdAndMaxFeatures() {
    check("img = Image(Table(If(x==3||y==2,1,0),{y,12},{x,16})); "
        + "{ImageLines(img, MaxFeatures->1), ImageLines(img, 0.9), Length(ImageLines(img, 0.5)), "
        + "Length(ImageLines(img, 0.5, MaxFeatures->All))}", //
        "{{Line({{0.0,10.5},{16.0,10.5}})},{},2,2}");
    check("ImageLines(Image(ConstantArray(0,{7,9})))", //
        "{}");
    check("ImageLines(Image(ConstantArray(1,{1,1})))", //
        "{}");
  }

  /** The primitives are drawn in red onto an RGB copy of the image with an opacity channel. */
  @Test
  public void highlightImage() {
    check("h = HighlightImage(Image(ConstantArray(0.25,{4,6})), Line({{0,3.5},{6,3.5}})); "
        + "{ImageDimensions(h), ImageChannels(h), ImageType(h), ImageData(h)[[1,3]], "
        + "ImageData(h)[[2,3]]}", //
        "{{6,4},4,Byte,{1.0,0.0,0.0,1.0},{0.25098,0.25098,0.25098,1.0}}");
    // a colour draws the primitives after it in its list
    check("ImageData(HighlightImage(Image(ConstantArray(0,{5,5})), "
        + "{Point({0.5,0.5}), {Green, Point({2.5,2.5})}}))[[All,All,2]]", //
        "{{0.0,0.0,0.0,0.0,0.0},{0.0,0.0,1.0,0.0,0.0},{0.0,1.0,1.0,1.0,0.0}," //
            + "{0.0,0.0,1.0,0.0,0.0},{0.0,0.0,0.0,0.0,0.0}}");
    check("ImageData(HighlightImage(Image(ConstantArray(0,{3,4})), "
        + "Rectangle({0,0},{4,3})))[[All,All,1]]", //
        "{{1.0,1.0,1.0,1.0},{1.0,0.0,0.0,1.0},{1.0,1.0,1.0,1.0}}");
    check("ImageData(HighlightImage(Image(ConstantArray(0,{5,5})), Circle({2.5,2.5},2)))"
        + "[[All,All,1]]", //
        "{{0.0,1.0,1.0,1.0,0.0},{1.0,1.0,0.0,1.0,1.0},{1.0,0.0,0.0,0.0,1.0}," //
            + "{1.0,1.0,0.0,1.0,1.0},{0.0,1.0,1.0,1.0,0.0}}");
    // the colour of a directive, or of a Hue
    check("ImageData(HighlightImage(Image(ConstantArray(0,{3,3})), "
        + "{{Directive(Blue, Thick), Point({1.5,1.5})}, {Hue(1/3), Point({0.5,0.5})}}))"
        + "[[{2,3},{2,1}]]", //
        "{{{0.0,0.0,1.0,1.0},{0.0,1.0,0.0,1.0}},{{0.0,1.0,0.0,1.0},{0.0,1.0,0.0,1.0}}}");
    // the lines ImageLines finds can be drawn
    check("Head(HighlightImage(#, ImageLines(#)))&(Image(Table(If(x+y==9,1,0),{y,8},{x,8})))", //
        "Image");
    check("ImageData(HighlightImage(Image(ConstantArray(0.5,{1,2})), {}))", //
        "{{{0.501961,0.501961,0.501961,1.0},{0.501961,0.501961,0.501961,1.0}}}");
  }
}
