package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.MachineProfile;

/**
 * Arbitrary precision values of Catalan's constant, of <code>Zeta</code> at integers and of
 * <code>PolyLog</code> of an integer order with <code>|z| &lt;= 3/4</code>, which come from fast
 * series, and the exact comparisons with close rationals which need them.
 */
public class ConstantSeriesPrecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testValues() {
    check("N(Catalan,50)", //
        "0.91596559417721901505460351493238411077414937428167");
    check("N(Zeta(9),40)", //
        "1.002008392826082214417852769232412060486");
    check("N(Zeta(3)-1,60)", //
        "0.202056903159594285399738161511449990764986292340498881792272");
    check("N(PolyLog(2,1/4),40)", //
        "0.2676526390827326069191838284878115758199");
    check("N(PolyLog(2,-7/10),40)", //
        "-0.6051584023377052839744268875770931750819");
    check("N(PolyLog(3,-1/2+I/4),40)", //
        "-0.4779454498775332125336783240303034569059+I*0.2239221594085281913324151578787446755767");
  }

  @Test
  public void testSpeed() {
    // the general algorithms took 5 s for these three values, the series take milliseconds
    long start = System.nanoTime();
    check("N(Catalan,250);N(Zeta(3),250);N(PolyLog(2,2/7),250);True", //
        "True");
    long millis = (System.nanoTime() - start) / 1_000_000L;
    assertTrue(millis < MachineProfile.millis(2000L), "took " + millis + " ms");
  }

  @Test
  public void testComparisonWithCloseRationals() {
    check("{Catalan>915965594177/1000000000000,Catalan==915965594177/1000000000000}", //
        "{True,False}");
    check("{Zeta(3)<1202056903160/1000000000000,Zeta(7)>1008349277381/1000000000000}", //
        "{True,True}");
    check("{Sign(PolyLog(2,1/4)-267652639082/1000000000000),PolyLog(2,-7/10)<-59732/100000}", //
        "{1,True}");
  }
}
