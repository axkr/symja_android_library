package org.matheclipse.core.form.output;

import org.matheclipse.core.interfaces.IAST;

/** 
 * General conversion interface
 */
public interface IConverter {
  /**
   * Converts a given function into the corresponding MathML output
   * 
   *@param  buffer    StringBuffer for MathML output
   *@param  function  the math function which should be converted to MathML
   */
  public boolean convert(StringBuffer buffer, IAST function);
}
