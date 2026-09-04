package org.matheclipse.core.graphics.webgl;

import java.awt.Color;

/**
 * The graphics directives in force at the point a 3D primitive is collected.
 *
 * <p>
 * An instance is captured by value into every emitted element, so a directive that appears later in
 * the same list can never change a primitive that was already collected. Scoping (a nested list, or
 * the second argument of {@code Style}) is done by cloning, which mirrors {@code Style2D} in the
 * SVG package.
 *
 * <p>
 * Sizes come in two flavours: {@code Thickness}/{@code PointSize}/ {@code Dashing} measure in
 * fractions of the image, {@code AbsoluteThickness} and friends measure in printer's points. Both
 * are kept, and the renderer resolves whichever is set against the scene's bounding box, so a plot
 * keeps its proportions no matter what coordinate range it covers.
 */
public final class Style3D implements Cloneable {

  /** A length written either as a fraction of the image or in printer's points. */
  public static final class Size {
    /** Fraction of the scene diagonal, or {@code NaN} when the absolute form is in force. */
    public double scaled;
    /** Printer's points, or {@code NaN} when the scaled form is in force. */
    public double absolute;

    Size(double scaled, double absolute) {
      this.scaled = scaled;
      this.absolute = absolute;
    }

    public static Size ofScaled(double fraction) {
      return new Size(fraction, Double.NaN);
    }

    public static Size ofAbsolute(double points) {
      return new Size(Double.NaN, points);
    }

    public boolean isScaled() {
      return !Double.isNaN(scaled);
    }
  }

  /**
   * Face and line colour, including the alpha channel a colour directive may carry.
   *
   * <p>
   * An unstyled solid is white. That looks like no choice at all until you see what the default
   * lights do with it: the warm, magenta and blue faces of a plain {@code Cuboid[]} are the lights,
   * not the paint, and giving the box a colour of its own would tint all three of them the same way
   * and flatten it.
   */
  public Color color = Color.WHITE;

  /**
   * Whether a colour directive has actually been seen.
   *
   * <p>
   * Text follows the current colour when one is in force and is black otherwise, so it cannot
   * simply take {@link #color}: the default there is the blue that unstyled solids are drawn in,
   * and a label in that colour reads as part of the picture rather than as writing on it.
   */
  public boolean colorSet = false;

  /**
   * The colour of a face seen from behind, as {@code FaceForm[front, back]} asks for, or
   * {@code null} when the same colour is used whichever side is being looked at.
   */
  public Color backFaceColor = null;

  /** {@code FaceForm} override for filled primitives, or {@code null} when unset. */
  public Color faceColor = null;

  /** {@code Opacity} directive, multiplied into the colour alpha when the element is written. */
  public double opacity = 1.0;

  /**
   * Whether polygon edges are drawn.
   *
   * <p>
   * Wolfram outlines a face whether or not an {@code EdgeForm} was asked for - a bare
   * {@code Polygon} and each of a {@code Cuboid}'s quads come with a dark outline - so the default
   * is "on". Every surface Symja's plot builtins emit carries an explicit {@code EdgeForm[None]},
   * which is what keeps a plotted surface clean; its mesh is drawn as {@code Line} primitives
   * instead.
   */
  public boolean showEdges = true;

  /** {@code EdgeForm} colour, or {@code null} to let the renderer pick a contrasting default. */
  public Color edgeColor = null;

  /** {@code EdgeForm} opacity. */
  public double edgeOpacity = 1.0;

  /**
   * The dihedral angle in degrees above which the join between two facets counts as an edge.
   *
   * <p>
   * The outline follows the shape rather than the tessellation: a {@code Cuboid} shows its twelve
   * creases, a {@code Cylinder} the two circles where its caps meet the barrel, and a
   * {@code Sphere} nothing at all. An explicit {@code EdgeForm} means the user asked for the mesh
   * itself, so it drops the angle to a hair above zero and every facet that is not coplanar with
   * its neighbour is outlined.
   */
  public double edgeAngle = DEFAULT_EDGE_ANGLE;

  /** The crease angle of the outline every face carries when no {@code EdgeForm} was given. */
  public static final double DEFAULT_EDGE_ANGLE = 30.0;

  /** The crease angle of an {@code EdgeForm} the user asked for: the whole mesh, bar seams. */
  public static final double EXPLICIT_EDGE_ANGLE = 1.0;

  /** {@code EdgeForm} line width in printer's points. */
  public double edgeThickness = 1.0;

  /** Line width. */
  public Size thickness = Size.ofAbsolute(1.0);

  /** Point diameter. */
  public Size pointSize = Size.ofScaled(0.01);

  /**
   * Dash pattern as alternating on/off lengths, or {@code null} for a solid line. Entries are
   * fractions of the scene diagonal when {@link #dashingScaled} is set, printer's points otherwise.
   */
  public double[] dashing = null;

  public boolean dashingScaled = true;

  /**
   * {@code Specularity} exponent-weighted highlight strength in 0..1, or {@code NaN} when unset.
   */
  public double specularity = Double.NaN;

  /** {@code Specularity[s, n]} highlight sharpness. */
  public double specularExponent = 30.0;

  /** {@code Glow} colour, which lights a surface independently of the scene lights. */
  public Color glow = null;

  /** {@code Arrowheads} size as a fraction of the scene diagonal. */
  public double arrowheadSize = 0.04;

  public String fontFamily = "Arial, sans-serif";
  public double fontSize = 12.0;
  public String fontWeight = "normal";
  public String fontStyle = "normal";

  /** Text colour, which {@code Style} may set independently of the surface colour. */
  public Color textColor = null;

  /**
   * The label of an enclosing {@code Tooltip}, or {@code null} when there is none.
   *
   * <p>
   * Scopes like a directive, the same way the flat pipeline treats one: everything built inside
   * the tooltip carries the label, so a shape made of several elements answers as one.
   */
  public String tooltip = null;

  @Override
  public Style3D clone() {
    try {
      Style3D copy = (Style3D) super.clone();
      if (dashing != null) {
        copy.dashing = dashing.clone();
      }
      return copy;
    } catch (CloneNotSupportedException e) {
      return new Style3D();
    }
  }

  /** The colour a filled primitive is painted with. */
  public Color effectiveFace() {
    return faceColor != null ? faceColor : color;
  }

  /** The colour text is drawn in. */
  public Color effectiveText() {
    if (textColor != null) {
      return textColor;
    }
    return colorSet ? color : Color.BLACK;
  }

  /**
   * The colour a line or a point is drawn in.
   *
   * <p>
   * These are not lit, so the white that makes a solid read well would leave them invisible on the
   * white background; unstyled, they are black.
   */
  public Color effectiveLine() {
    return colorSet ? color : Color.BLACK;
  }

  /** The alpha of a colour combined with the {@code Opacity} directive, in 0..1. */
  public double alphaOf(Color c) {
    if (c == null) {
      return 0.0;
    }
    double a = c.getAlpha() / 255.0 * opacity;
    return Math.max(0.0, Math.min(1.0, a));
  }
}
