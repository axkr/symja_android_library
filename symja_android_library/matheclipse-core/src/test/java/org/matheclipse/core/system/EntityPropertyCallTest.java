package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>Entity(type, name)[property]</code> is the value <code>EntityValue</code> gives. */
public class EntityPropertyCallTest extends ExprEvaluatorTestCase {

  @Test
  public void testEntityAppliedToAProperty() {
    check("Entity(\"Element\", \"Gold\")[\"AtomicNumber\"]", //
        "79");
    check("Entity(\"Element\", \"Gold\")[\"AtomicNumber\"] == "
        + "EntityValue(Entity(\"Element\", \"Gold\"), \"AtomicNumber\")", //
        "True");
  }

  @Test
  public void testUnknownTypeStaysUnevaluated() {
    check("Entity(\"NoSuchType\", \"X\")[\"Y\"]", //
        "Entity(NoSuchType,X)[Y]");
    check("Entity(\"Element\", \"Gold\")[1, 2]", //
        "Entity(Element,Gold)[1,2]");
  }
}
