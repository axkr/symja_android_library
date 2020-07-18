package org.matheclipse.core.tensor.qty;

import org.matheclipse.core.tensor.io.ResourceData;

/** singleton instance of built-in SI unit system */
/* package */ enum BuiltIn {
  SI;
  // ---
  final static UnitSystem unitSystem = SimpleUnitSystem.from(ResourceData.properties("/unit/si.properties"));
  final static UnitConvert unitConvert = new UnitConvert(unitSystem);
  final static QuantityMagnitude quantityMagnitude = new QuantityMagnitude(unitSystem);
}
