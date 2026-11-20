package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Overflow()</code> and <code>Underflow()</code> are approximate numbers whose magnitude is
 * beyond the range of numbers: above every number, resp. between 0 and every positive number. Their
 * sign is lost.
 */
public class OutOfRangeNumbersTest extends ExprEvaluatorTestCase {

  @Test
  public void testPredicates() {
    check("{NumberQ(Overflow()), NumericQ(Underflow()), InexactNumberQ(Overflow()), "
        + "ExactNumberQ(Underflow())}", //
        "{True,True,True,False}");
  }

  @Test
  public void testComparisons() {
    check("{Overflow()>7/3, Overflow()<=10^1000, Underflow()>-4, Underflow()<1/10^1000, "
        + "Underflow()>=0.0}", //
        "{True,False,True,True,True}");
    check("{Overflow()==12, Underflow()!=0, Underflow()==Overflow()}", //
        "{False,True,False}");
    // real numeric constants and infinities
    check("{Overflow()>E^2, Underflow()<Sqrt(5)/1000, Underflow()>-Pi, Overflow()<Infinity, "
        + "Underflow()>-Infinity, Underflow()<Overflow()}", //
        "{True,True,True,True,True,True}");
    check("{Max(Overflow(), 9), Min(-1, Underflow())}", //
        "{Overflow(),-1}");
  }

  @Test
  public void testSignAbsChop() {
    check("{Sign(Overflow()), Sign(Underflow()), Abs(Overflow()), Abs(Underflow())}", //
        "{1,0,Overflow(),Underflow()}");
    check("{Chop(Underflow()), Chop(x+Underflow())}", //
        "{0,x}");
  }

  @Test
  public void testPowersAndInfinities() {
    // Overflow() is a huge positive magnitude: a base below 1 makes it tiny
    check("{3^Underflow(), (2/7)^Overflow(), 5^Overflow()}", //
        "{1+Underflow(),Underflow(),Overflow()}");
    check("{-Infinity*Overflow(), Infinity+Underflow(), Underflow()-Underflow(), "
        + "ComplexInfinity*Underflow()}", //
        "{-Infinity,Infinity,Underflow(),Indeterminate}");
  }
}
