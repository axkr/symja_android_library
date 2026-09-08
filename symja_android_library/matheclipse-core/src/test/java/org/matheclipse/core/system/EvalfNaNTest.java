package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests for the non-throwing numeric conversions {@link IExpr#evalfNaN()} and
 * {@link IExpr#evalfcNaN()}.
 */
public class EvalfNaNTest {

  @BeforeEach
  public void setUp() throws Exception {
    F.await();
  }

  @Test
  public void testRealFastPath() {
    assertEquals(0.5, F.C1D2.evalfNaN(), 0.0);
    assertEquals(-3.5, F.num(-3.5).evalfNaN(), 0.0);
    assertEquals(42.0, F.ZZ(42).evalfNaN(), 0.0);
  }

  @Test
  public void testNumericEvaluation() {
    assertEquals(0.8414709848078965, F.Sin(F.C1).evalfNaN(), 1E-15);
    assertEquals(Math.PI, S.Pi.evalfNaN(), 1E-15);
  }

  @Test
  public void testInfinity() {
    assertEquals(Double.POSITIVE_INFINITY, F.CInfinity.evalfNaN(), 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, F.CNInfinity.evalfNaN(), 0.0);
  }

  /** A free symbol has no numeric value - {@link Double#NaN} instead of an exception. */
  @Test
  public void testFreeSymbolReturnsNaN() {
    assertTrue(Double.isNaN(S.x.evalfNaN()));
    assertTrue(Double.isNaN(F.Sin(S.x).evalfNaN()));
  }

  @Test
  public void testNonNumericReturnsNaN() {
    assertTrue(Double.isNaN(F.stringx("abc").evalfNaN()));
    assertTrue(Double.isNaN(S.Indeterminate.evalfNaN()));
    assertTrue(Double.isNaN(F.List(F.C1, F.C2).evalfNaN()));
  }

  /** A complex value with a non-zero imaginary part isn't machine-sized double convertible. */
  @Test
  public void testComplexReturnsNaN() {
    assertTrue(Double.isNaN(F.CI.evalfNaN()));
    assertTrue(Double.isNaN(F.complexNum(1.0, 2.0).evalfNaN()));
    // ... but the complex conversion succeeds
    Complex c = F.complexNum(1.0, 2.0).evalfcNaN();
    assertEquals(1.0, c.getReal(), 0.0);
    assertEquals(2.0, c.getImaginary(), 0.0);
  }

  @Test
  public void testEvalfcNaN() {
    assertEquals(new Complex(0.0, 1.0), F.CI.evalfcNaN());
    assertEquals(new Complex(0.5), F.C1D2.evalfcNaN());
    assertTrue(F.stringx("abc").evalfcNaN().isNaN());
    assertTrue(S.x.evalfcNaN().isNaN());
  }

  /** The substitution overload mirrors {@link IExpr#evalf(java.util.function.Function)}. */
  @Test
  public void testEvalfNaNWithFunction() {
    IExpr function = F.Sin(S.x);
    assertEquals(0.0, function.evalfNaN(e -> e == S.x ? F.C0 : F.NIL), 1E-15);
    assertTrue(Double.isNaN(function.evalfNaN(e -> F.NIL)));
  }

  /** {@link IExpr#evalf()} keeps throwing - it is not replaced by the NaN variant. */
  @Test
  public void testEvalfStillThrows() {
    assertThrows(ArgumentTypeException.class, () -> S.x.evalf());
    assertThrows(ArgumentTypeException.class, () -> F.stringx("abc").evalf());
  }

  /**
   * Flow control exceptions must not be swallowed and converted into {@link Double#NaN}, otherwise
   * <code>Catch[]</code> semantics and the evaluation limits would break.
   */
  @Test
  public void testThrowExceptionIsRethrown() {
    assertThrows(ThrowException.class, () -> F.Throw(F.ZZ(42)).evalfNaN());
    assertThrows(ThrowException.class, () -> F.Throw(F.ZZ(42)).evalfcNaN());
  }

  @Test
  public void testAbortExceptionIsRethrown() {
    assertThrows(AbortException.class, () -> F.headAST0(S.Abort).evalfNaN());
    assertThrows(AbortException.class, () -> F.headAST0(S.Abort).evalfcNaN());
  }

  /**
   * {@link IExpr#toDoubleDefault(double)} is documented to return the default value for
   * non-convertible expressions - it must not propagate an exception either.
   */
  @Test
  public void testToDoubleDefaultDoesNotThrow() {
    assertEquals(-1.0, F.stringx("abc").toDoubleDefault(-1.0), 0.0);
    assertEquals(-1.0, S.x.toDoubleDefault(-1.0), 0.0);
    assertEquals(0.5, F.C1D2.toDoubleDefault(-1.0), 0.0);
  }

  /** {@link Double#NaN} as default value keeps the throwing behaviour of the 3-arg overload. */
  @Test
  public void testToDoubleDefaultNaNStillThrows() {
    assertThrows(ArgumentTypeException.class, () -> S.x.toDoubleDefault(Double.NaN));
  }

  @Test
  public void testQuietModeIsRestored() {
    org.matheclipse.core.eval.EvalEngine engine = org.matheclipse.core.eval.EvalEngine.get();
    boolean quietMode = engine.isQuietMode();
    assertTrue(Double.isNaN(F.Sin(S.x).evalfNaN()));
    assertEquals(quietMode, engine.isQuietMode());
    assertFalse(engine.isQuietMode());
  }
}
