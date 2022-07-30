package org.matheclipse.core.img;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.io.ResourceData;

/**
 * Color data lists.
 * 
 * @see ColorDataGradients
 */
public enum ColorDataLists {
  /** 2 colors: black and white */
  _000,
  /** 16 colors */
  _001,
  /** 10 colors */
  _003,
  /** 14 colors */
  _058,
  /** 9 colors */
  _061,
  /** 16 colors */
  _063,
  /** 10 colors */
  _074,
  /** 16 colors */
  _094,
  /** 16 colors */
  _096,
  /** 16 colors, Mathematica default */
  _097,
  /** 16 colors */
  _098,
  /** 16 colors */
  _099,
  /** 16 colors */
  _100,
  /** 16 colors */
  _103,
  /** 16 colors */
  _104,
  /** 16 colors */
  _106,
  /** 16 colors */
  _108,
  /** 16 colors */
  _109,
  /** 16 colors */
  _110,
  /** 16 colors */
  _112,
  /** hue palette with 13 colors normalized according to brightness
   * tensor library default */
  _250, // luma
  /** hue palette with 13 colors */
  _251, //
  ;

  private final IAST tensor =
      ResourceData.of("/img/colorlist/" + name().substring(1) + ".csv");
  private final ColorDataIndexed cyclic = new CyclicColorDataIndexed(tensor);
  private final ColorDataIndexed strict = new StrictColorDataIndexed(tensor);

  /** @return */
  public ColorDataIndexed cyclic() {
    return cyclic;
  }

  /** @return */
  public ColorDataIndexed strict() {
    return strict;
  }
}
