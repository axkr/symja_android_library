package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Rational extends AbstractConverter {

  /** constructor will be called by reflection */
  public Rational() {
  }

  /**
   * Converts a given function into the corresponding MathML output
   *
   *@param  buf  StringBuilder for MathML output
   *@param  f    The math function which should be converted to MathML
   */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
    if (f.size() != 3) {
      return false;
    }
    
//  <mfrac><mi>k</mi><mn>2</mn></mfrac>
    fFactory.tagStart(buf, "mfrac");
    fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
    fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false);
    fFactory.tagEnd(buf, "mfrac");
     
    return true;
  }
}