package org.matheclipse.core.builtin;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

public class ConstantPhysicsDefinitions {

  private static class Initializer {

    private static void init() {
      // physical constants as Quantity ASTs with canonical unit names (see units.json)
      F.ISet(S.UniverseAge, F.Quantity(F.num(1.3787E10), F.stringx("JulianYears")));
      F.ISet(S.BohrRadius, F.Quantity(F.num(0.0529177210903), F.stringx("Nanometers")));
      F.ISet(S.AvogadroConstant,
          F.Quantity(F.ZZ("602214076000000000000000", 10), F.Power(F.stringx("Moles"), F.CN1)));
    }
  }

  public static void initialize() {
    Initializer.init();
  }

}
