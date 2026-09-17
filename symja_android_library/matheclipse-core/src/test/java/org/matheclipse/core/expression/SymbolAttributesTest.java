package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A refused attribute change leaves the symbol as it was (issue #1501).
 *
 * <p>
 * {@link Symbol#addAttributes(int)}, {@link Symbol#clearAttributes(int)} and
 * {@link Symbol#setAttributes(int)} used to assign the new attribute word before asking
 * {@link Symbol#isLocked()}, and there is nothing to roll back to: a symbol of the
 * <code>System`</code> or <code>Rubi`</code> context is one JVM-wide instance which every
 * {@link EvalEngine} shares, so the rejected call changed the symbol for every other thread.
 */
public class SymbolAttributesTest {

  @BeforeEach
  public void setUp() throws Exception {
    F.await();
  }

  @Test
  public void testRefusedAttributeChangeLeavesTheSymbolUnchanged() {
    withoutPackageMode(() -> {
      final int attributes = S.Sin.getAttributes();
      assertTrue(S.Sin.isLocked());

      assertThrows(RuleCreationError.class, () -> S.Sin.clearAttributes(ISymbol.PROTECTED));
      assertEquals(attributes, S.Sin.getAttributes());
      assertTrue(S.Sin.hasProtectedAttribute());

      assertThrows(RuleCreationError.class, () -> S.Sin.addAttributes(ISymbol.FLAT));
      assertEquals(attributes, S.Sin.getAttributes());

      assertThrows(RuleCreationError.class, () -> S.Sin.setAttributes(ISymbol.NOATTRIBUTE));
      assertEquals(attributes, S.Sin.getAttributes());
    });
  }

  /** In package mode - the default - a built-in's attributes can still be changed. */
  @Test
  public void testPackageModeStillChangesAttributes() {
    final int attributes = S.Sin.getAttributes();
    try {
      // Unprotect(Sin)
      S.Sin.clearAttributes(ISymbol.PROTECTED);
      assertFalse(S.Sin.hasProtectedAttribute());
    } finally {
      S.Sin.setAttributes(attributes);
    }
    assertEquals(attributes, S.Sin.getAttributes());
  }

  private static void withoutPackageMode(Runnable runnable) {
    EvalEngine engine = EvalEngine.get();
    boolean packageMode = engine.isPackageMode();
    engine.setPackageMode(false);
    try {
      runnable.run();
    } finally {
      engine.setPackageMode(packageMode);
    }
  }
}
