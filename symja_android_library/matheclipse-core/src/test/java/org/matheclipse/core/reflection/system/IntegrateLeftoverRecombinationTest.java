package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Terms of a sum which stay unintegrated one by one, while their sum has an antiderivative: the
 * leftovers are taken together once more.
 */
public class IntegrateLeftoverRecombinationTest extends ExprEvaluatorTestCase {

  @Test
  public void testLeftoversWithElementarySum() {
    // c*E^(2*t)/(t+E^t*t)+c*E^(3*t)/(t+E^t*t) is c*E^(2*t)/t
    check("Integrate((c*E^(2*t)+c*E^(3*t)+t)/((1+E^t)*t),t)", //
        "t+c*ExpIntegralEi(2*t)-Log(1+E^t)");
  }
}
