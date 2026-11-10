package org.matheclipse.core.data;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The standard atomic weights of the elements in unified atomic mass units, written to the digits
 * they are known to.
 *
 * <p>
 * An element whose isotopic composition varies in nature has its conventional value (carbon
 * <code>12.011</code>), one with a single stable isotope or a fixed composition its full value
 * (fluorine <code>18.998403163</code>), and an element without stable isotopes the mass number of
 * its longest-lived isotope (technetium <code>97</code>). The number of digits written, trailing
 * zeros included, is the precision of the value.
 */
public final class StandardAtomicWeights {

  /** The weights by atomic number, the first one being hydrogen's. */
  private static final String[] WEIGHTS = { //
      "1.0080", "4.002602", "6.94", "9.0121831", "10.81", // H - B
      "12.011", "14.007", "15.999", "18.998403162", "20.1797", // C - Ne
      "22.98976928", "24.305", "26.9815384", "28.085", "30.973761998", // Na - P
      "32.06", "35.45", "39.95", "39.0983", "40.078", // S - Ca
      "44.955907", "47.867", "50.9415", "51.9961", "54.938043", // Sc - Mn
      "55.845", "58.933194", "58.6934", "63.546", "65.38", // Fe - Zn
      "69.723", "72.630", "74.921595", "78.971", "79.904", // Ga - Br
      "83.798", "85.4678", "87.62", "88.905838", "91.224", // Kr - Zr
      "92.90637", "95.95", "97", "101.07", "102.90549", // Nb - Rh
      "106.42", "107.8682", "112.414", "114.818", "118.710", // Pd - Sn
      "121.760", "127.60", "126.90447", "131.293", "132.90545196", // Sb - Cs
      "137.327", "138.90547", "140.116", "140.90766", "144.242", // Ba - Nd
      "145", "150.36", "151.964", "157.25", "158.925354", // Pm - Tb
      "162.500", "164.930329", "167.259", "168.934219", "173.045", // Dy - Yb
      "174.9668", "178.486", "180.94788", "183.84", "186.207", // Lu - Re
      "190.23", "192.217", "195.084", "196.966570", "200.592", // Os - Hg
      "204.38", "207.2", "208.98040", "209", "210", // Tl - At
      "222", "223", "226", "227", "232.0377", // Rn - Th
      "231.03588", "238.02891", "237", "244", "243", // Pa - Am
      "247", "247", "251", "252", "257", // Cm - Fm
      "258", "259", "262", "267", "268", // Md - Db
      "269", "270", "269", "277", "281", // Sg - Ds
      "282", "285", "286", "290", "290", // Rg - Mc
      "293", "294", "294" // Lv - Og
  };

  private StandardAtomicWeights() {}

  /**
   * The standard atomic weight of the element as a number with the precision of its digits.
   *
   * @param atomicNumber the atomic number of the element
   * @return {@link F#NIL} if there is no element with this atomic number
   */
  public static IExpr of(int atomicNumber) {
    String weight = digits(atomicNumber);
    if (weight == null) {
      return F.NIL;
    }
    return F.num(weight, (long) significantDigits(weight));
  }

  /**
   * The standard atomic weight of the element as written in the table.
   *
   * @param atomicNumber the atomic number of the element
   * @return <code>null</code> if there is no element with this atomic number
   */
  public static String digits(int atomicNumber) {
    return atomicNumber >= 1 && atomicNumber <= WEIGHTS.length ? WEIGHTS[atomicNumber - 1] : null;
  }

  /** The number of significant digits of a decimal number, trailing zeros included. */
  private static int significantDigits(String weight) {
    return new java.math.BigDecimal(weight).precision();
  }
}
