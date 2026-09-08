package org.matheclipse.chem.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.matheclipse.chem.CdkLoggingBridge;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * CDK must not log behind the application's back.
 *
 * <p>
 * Left to itself CDK falls back to a logger that writes straight to {@link System#err}, because the
 * two implementations it prefers live in artifacts this module does not depend on. The visible
 * symptom was a hundred <code>IteratingSDFReader</code> errors at the first
 * <code>MoleculePlot3D</code> of a session.
 */
public class CdkLoggingBridgeTest extends AbstractTestCase {

  @Test
  public void testCdkLogsThroughSlf4j() {
    // setUp() has run ChemInit.init()
    assertSame(CdkLoggingBridge.class, LoggingToolFactory.getLoggingToolClass());
  }

  @Test
  public void testBuildingA3DModelStaysQuiet() {
    // the call that loads CDK's ring templates, and used to spray the console while doing it
    check("Head(MoleculePlot3D(Molecule(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\")))", //
        "Graphics3D");
    assertEquals(CdkLoggingBridge.class, LoggingToolFactory.getLoggingToolClass());
  }
}
