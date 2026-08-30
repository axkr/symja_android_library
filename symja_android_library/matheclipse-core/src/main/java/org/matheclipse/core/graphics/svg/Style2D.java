package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The graphics directives in force at the point a primitive is collected.
 *
 * <p>
 * An instance is captured by value into every {@link Prim2D}, so a later directive can never change
 * the appearance of a primitive that was already collected. Scoping (a nested list, or the second
 * argument of {@code Style}) is done by cloning.
 */
public final class Style2D implements Cloneable {

  /** Stroke colour, including its alpha channel. */
  public Color strokeColor = Color.BLACK;

  /** Fill colour used when no explicit {@code FaceForm} is in force. */
  public Color fillColor = Color.BLACK;

  /** {@code FaceForm} override, or {@code null} when unset. */
  public Color faceColor = null;

  /** {@code EdgeForm} colour, or {@code null} for "no edge". */
  public Color edgeColor = null;

  /** True once an {@code EdgeForm} directive has been seen, even {@code EdgeForm[]}. */
  public boolean edgeFormSet = false;

  /** Stroke width in pixels. */
  public double strokeWidth = 1.0;

  /** {@code Opacity} directive, multiplied into the colour alpha at render time. */
  public double opacity = 1.0;

  /**
   * The transparency of the edge an {@code EdgeForm} draws, which is its own and not the face's.
   *
   * <p>
   * {@code Opacity} tints a face and leaves the outline around it alone, so
   * {@code {Opacity[0.2], EdgeForm[Black], Rectangle[]}} is a solid black frame around a barely
   * visible fill. Only an {@code Opacity} written inside the {@code EdgeForm} itself fades the
   * edge.
   */
  public double edgeOpacity = 1.0;

  /** Radius of a {@code Point} in pixels. */
  public double pointRadius = 3.0;

  /** SVG {@code stroke-dasharray} value, or {@code "none"}. */
  public String dashArray = "none";

  public String lineCap = "butt";
  public String lineJoin = "miter";

  /** Arrowhead size as a fraction of the image width. */
  public double arrowHeadScale = 0.05;

  /** One arrowhead of an {@code Arrowheads} directive. */
  public static final class ArrowHead {
    /** Size as a fraction of the image width. */
    public final double size;
    /** Where along the path the head sits, 0 at the tail and 1 at the tip. */
    public final double position;
    /** True when the head points back along the path, as a negative size asks for. */
    public final boolean reversed;

    public ArrowHead(double size, double position, boolean reversed) {
      this.size = size;
      this.position = position;
      this.reversed = reversed;
    }
  }

  /**
   * The heads an arrow carries. {@code null} means the default of a single head at the tip; an
   * empty list means {@code Arrowheads[None]}, which draws none at all.
   */
  public List<ArrowHead> arrowHeads = null;

  /**
   * The label of an enclosing {@code Tooltip}, or {@code null} when there is none.
   *
   * <p>
   * A tooltip scopes like a directive rather than wrapping a single primitive: everything collected
   * inside it carries the label, which is what lets a cell built from several primitives answer as
   * one.
   */
  public String tooltip = null;

  public String fontFamily = "sans-serif";
  public double fontSize = 12.0;
  public String fontWeight = "normal";
  public String fontStyle = "normal";
  public String textDecoration = null;

  @Override
  public Style2D clone() {
    try {
      Style2D copy = (Style2D) super.clone();
      if (arrowHeads != null) {
        copy.arrowHeads = new ArrayList<>(arrowHeads);
      }
      return copy;
    } catch (CloneNotSupportedException e) {
      return new Style2D();
    }
  }

  /** The colour a filled primitive is painted with. */
  public Color effectiveFill() {
    return faceColor != null ? faceColor : fillColor;
  }

  /** Set both stroke and fill, as a bare colour directive does. */
  public void setColor(Color c) {
    strokeColor = c;
    fillColor = c;
  }
}
