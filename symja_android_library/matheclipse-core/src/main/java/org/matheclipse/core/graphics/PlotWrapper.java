package org.matheclipse.core.graphics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The display wrappers a plot's data may be dressed in, taken off and remembered.
 *
 * <p>
 * Lets data be wrapped in {@code Style}, {@code Labeled}, {@code Tooltip}, {@code Annotation},
 * {@code StatusArea} or {@code Legended}, at any of four levels - around one value, around one
 * {@code {x, y}} point, around a whole dataset, or around a collection of datasets - and lets those
 * nest. Each says something about how the datum is shown and nothing about what it is.
 *
 * <p>
 * Every plot used to test for two of these by hand, in its own way, and treat the rest as data.
 * That is why a single {@code Tooltip} in a list did not merely lose its decoration: the list
 * stopped counting as a list of points, and the plot read the coordinates as bare heights. This is
 * the one place that knows the list.
 *
 * <p>
 * The wrapper applied last is the one that shows, so when the same wrapper appears twice the
 * outermost wins.
 */
public final class PlotWrapper {

  /** The payload with every wrapper taken off. Never itself a display wrapper. */
  public final IExpr datum;

  /** The directives of an enclosing {@code Style}, or {@link F#NIL}. */
  public final IExpr style;

  /** The label of an enclosing {@code Labeled}, or {@link F#NIL}. */
  public final IExpr label;

  /** The placement argument of a three argument {@code Labeled}, or {@link F#NIL}. */
  public final IExpr labelPlacement;

  /**
   * The label of an enclosing {@code Tooltip}, or {@link F#NIL}.
   *
   * <p>
   * A one argument {@code Tooltip} labels its own contents, so this is already resolved: what the
   * reader sees on hover, not what was written.
   */
  public final IExpr tooltip;

  /** The label of an enclosing {@code Legended}, or {@link F#NIL}. */
  public final IExpr legend;

  /**
   * Whether the tooltip came from a one argument {@code Tooltip}, so the label is the datum itself.
   *
   * <p>
   * A collection wrapped that way means something different from one given a label: {@code
   * Tooltip({f, g})} asks for {@code f} over {@code f} and {@code g} over {@code g}, where {@code
   * Tooltip({f, g}, "both")} puts one label on each. Only the wrapper knows which was written.
   */
  public final boolean tooltipLabelsItself;

  private PlotWrapper(IExpr datum, IExpr style, IExpr label, IExpr labelPlacement, IExpr tooltip,
      IExpr legend, boolean tooltipLabelsItself) {
    this.datum = datum;
    this.style = style;
    this.label = label;
    this.labelPlacement = labelPlacement;
    this.tooltip = tooltip;
    this.legend = legend;
    this.tooltipLabelsItself = tooltipLabelsItself;
  }

  /**
   * Take the wrappers off one datum and remember what they said.
   *
   * @return never {@code null}; a plain datum yields a wrapper that reports {@link #isPlain()}
   */
  public static PlotWrapper of(IExpr item) {
    if (item == null || !item.isPresent()) {
      return new PlotWrapper(F.NIL, F.NIL, F.NIL, F.NIL, F.NIL, F.NIL, false);
    }
    IExpr datum = item;
    IExpr style = F.NIL;
    IExpr label = F.NIL;
    IExpr labelPlacement = F.NIL;
    IExpr tooltip = F.NIL;
    IExpr legend = F.NIL;
    boolean tooltipLabelsItself = false;

    // outermost first, and an inner repeat of the same wrapper does not overwrite it
    for (int depth = 0; depth < 8; depth++) {
      if (!datum.isAST() || !IExpr.isDisplayWrapperHead(datum.head())) {
        break;
      }
      IAST wrapper = (IAST) datum;
      if (wrapper.argSize() < 1) {
        break;
      }
      if (wrapper.isAST(S.Style) && wrapper.argSize() >= 2 && !style.isPresent()) {
        // Style(expr, a, b) carries several directives; they travel together as one list
        style = wrapper.argSize() == 2 ? wrapper.arg2()
            : F.ListAlloc(wrapper.argSize() - 1).appendArgs(2, wrapper.size(), wrapper::get);
      } else if (wrapper.isAST(S.Labeled) && wrapper.argSize() >= 2 && !label.isPresent()) {
        label = wrapper.arg2();
        if (wrapper.argSize() >= 3) {
          labelPlacement = wrapper.arg3();
        }
      } else if (wrapper.isAST(S.Tooltip) && !tooltip.isPresent()) {
        // Tooltip(expr) shows the expression itself, which is what makes a table of bare values
        // worth wrapping at all
        tooltipLabelsItself = wrapper.argSize() < 2;
        tooltip = tooltipLabelsItself ? wrapper.arg1() : wrapper.arg2();
      } else if (wrapper.isAST(S.Legended) && wrapper.argSize() >= 2 && !legend.isPresent()) {
        legend = wrapper.arg2();
      }
      datum = wrapper.arg1();
    }
    return new PlotWrapper(datum, style, label, labelPlacement, tooltip, legend,
        tooltipLabelsItself);
  }

  /** The payload alone, for a caller that has no use for what the wrappers said. */
  public static IExpr strip(IExpr item) {
    return item == null || !item.isPresent() ? F.NIL : item.stripDisplayWrappers();
  }

  /** Whether this item is dressed in a wrapper at all. */
  public static boolean isWrapper(IExpr item) {
    return item != null && item.isAST() && IExpr.isDisplayWrapperHead(item.head());
  }

  /**
   * The text a {@code Tooltip} label reads as.
   *
   * <p>
   * One definition for the two collectors, so a label cannot mean one thing in a flat picture and
   * another in a solid one.
   *
   * @return the text, or {@code null} when the label has nothing to say
   */
  public static String tooltipLabel(IExpr label) {
    if (label == null || !label.isPresent() || label.isNone()) {
      return null;
    }
    String text = label.toString().replace("\"", "");
    return text.isEmpty() ? null : text;
  }

  /**
   * The tooltip one element of this collection ends up with.
   *
   * <p>
   * An element's own label wins; otherwise it inherits the collection's, except that a collection
   * which labelled itself asks each element to name itself instead of sharing one label. That last
   * case is the whole difference between {@code Tooltip({f, g})} and {@code Tooltip({f, g}, "x")},
   * and it is easy to get wrong separately in each plot, so it is written once here.
   *
   * @param element the wrapper of one element, already taken apart
   */
  public IExpr tooltipOf(PlotWrapper element) {
    if (element.hasTooltip()) {
      return element.tooltip;
    }
    if (!hasTooltip()) {
      return F.NIL;
    }
    return tooltipLabelsItself ? element.datum : tooltip;
  }

  /** Whether nothing was taken off, so the caller's existing path applies unchanged. */
  public boolean isPlain() {
    return !style.isPresent() && !label.isPresent() && !tooltip.isPresent() && !legend.isPresent();
  }

  public boolean hasTooltip() {
    return tooltip.isPresent();
  }

  public boolean hasStyle() {
    return style.isPresent();
  }

  public boolean hasLabel() {
    return label.isPresent();
  }

  /** {@code Tooltip(primitive, label)} when there is a tooltip, otherwise the primitive itself. */
  public IExpr wrapTooltip(IExpr primitive) {
    return tooltip.isPresent() ? F.binaryAST2(S.Tooltip, primitive, tooltip) : primitive;
  }

  /** {@code Style(primitive, directives)} when there is a style, otherwise the primitive itself. */
  public IExpr wrapStyle(IExpr primitive) {
    return style.isPresent() ? F.binaryAST2(S.Style, primitive, style) : primitive;
  }

  /**
   * Both, with the tooltip outermost.
   *
   * <p>
   * That is the order the collectors scope them in: a tooltip covers everything drawn inside it, so
   * it has to enclose the style rather than sit under it.
   */
  public IExpr decorate(IExpr primitive) {
    return wrapTooltip(wrapStyle(primitive));
  }

  /**
   * The curves of a plot's first argument, taken apart, with the label each carries.
   *
   * <p>
   * Every plot that takes a function or a list of functions has to answer the same question - which
   * of the four wrapper levels was written, and therefore which label belongs to which curve - and
   * the answer is not obvious in the one place it differs: {@code Tooltip({f, g})} labels each
   * curve with its own expression, while {@code Tooltip({f, g}, "both")} puts the one label on
   * both. This is that answer, once, so that a polar plot cannot disagree with a cartesian one.
   */
  public static final class Curves {

    /** The functions with their wrappers taken off, indexed from 1 as the plots index them. */
    public final IAST functions;

    private final IExpr[] tooltips;

    private Curves(IAST functions, IExpr[] tooltips) {
      this.functions = functions;
      this.tooltips = tooltips;
    }

    /** How many curves there are. */
    public int size() {
      return functions.argSize();
    }

    /** The function of curve {@code i}, counted from 1. */
    public IExpr function(int i) {
      return functions.get(i);
    }

    /** The tooltip of curve {@code i}, counted from 1, or {@link F#NIL}. */
    public IExpr tooltip(int i) {
      return i >= 1 && i < tooltips.length && tooltips[i] != null ? tooltips[i] : F.NIL;
    }

    /** Whether any curve carries one, so a caller can keep its existing path when none does. */
    public boolean anyTooltip() {
      for (int i = 1; i < tooltips.length; i++) {
        if (tooltips[i] != null && tooltips[i].isPresent()) {
          return true;
        }
      }
      return false;
    }

    /**
     * The finished primitive of curve {@code i}, wrapped as a caller could have typed it.
     *
     * <p>
     * Handing on {@code Tooltip(curve, label)} rather than reaching into the renderer is what lets
     * a plot support tooltips without any tooltip machinery of its own.
     */
    public IExpr decorate(int i, IExpr primitive) {
      IExpr tooltip = tooltip(i);
      return tooltip.isPresent() && primitive.isPresent()
          ? F.binaryAST2(S.Tooltip, primitive, tooltip)
          : primitive;
    }
  }

  /**
   * Split a plot's function argument into curves and work out the label of each.
   *
   * @param functionOrListOfFunctions the first argument of the plot, wrapped or not
   */
  public static Curves curves(IExpr functionOrListOfFunctions) {
    PlotWrapper outer = PlotWrapper.of(functionOrListOfFunctions);
    IAST list = outer.datum.makeList();
    int size = list.size();
    IExpr[] tooltips = new IExpr[size];
    IASTAppendable stripped = F.ListAlloc(size - 1);
    for (int i = 1; i < size; i++) {
      PlotWrapper each = PlotWrapper.of(list.get(i));
      stripped.append(each.datum);
      tooltips[i] = outer.tooltipOf(each);
    }
    return new Curves(stripped, tooltips);
  }
}
