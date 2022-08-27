// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.qty;

import org.matheclipse.core.tensor.io.ResourceData;

/** singleton instance of built-in SI unit system */
/* package */ enum BuiltIn {
  SI;

  // ---
  static final UnitSystem unitSystem =
      SimpleUnitSystem.from(ResourceData.properties("/unit/si.properties"));
  static final UnitConvert unitConvert = new UnitConvert(unitSystem);
  static final QuantityMagnitude quantityMagnitude = new QuantityMagnitude(unitSystem);
}
