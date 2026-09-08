package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * Guards the built-in symbol table which {@link ID} and {@link S} share.
 *
 * <p>
 * {@link ID#STRING_TO_ID_MAP} is built by walking {@link ID#FUNCTION_NAMES} and using the array
 * <em>index</em> as the id, so the name at index <code>i</code> has to be the name of the symbol
 * whose <code>ID</code> constant is <code>i</code>. Nothing in the compiler enforces that: inserting
 * a symbol without renumbering the constants after it, or without inserting into
 * {@link ID#FUNCTION_NAMES} and {@link ID#LINE_NUMBER_OF_JAVA_CLASS} at the same index, still
 * compiles and then silently resolves names to the wrong symbols.
 *
 * <p>
 * The class comment in <code>ID.java</code> points at a generator
 * (<code>org.matheclipse.core.preprocessor.FunctionIDGenerator</code>) which isn't part of this
 * repository, so the tables are maintained by hand and the invariants are asserted here.
 */
public class SymbolTableTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void parallelTablesHaveTheSameLength() {
    assertEquals(ID.FUNCTION_NAMES.length, ID.LINE_NUMBER_OF_JAVA_CLASS.length,
        "FUNCTION_NAMES and LINE_NUMBER_OF_JAVA_CLASS are indexed by the same id");
  }

  @Test
  public void everyIdResolvesToTheSymbolOfThatName() {
    for (int id = 0; id < ID.FUNCTION_NAMES.length; id++) {
      IBuiltInSymbol symbol = S.symbol(id);
      assertNotNull(symbol, "no symbol for id " + id + " (" + ID.FUNCTION_NAMES[id] + ")");
      assertEquals(ID.FUNCTION_NAMES[id], symbol.toString(),
          "FUNCTION_NAMES[" + id + "] doesn't name the symbol with that id");
      assertEquals(id, symbol.ordinal(),
          "the ordinal of " + ID.FUNCTION_NAMES[id] + " doesn't match its position");
    }
  }

  @Test
  public void nameLookupRoundTrips() {
    for (int id = 0; id < ID.FUNCTION_NAMES.length; id++) {
      String name = ID.FUNCTION_NAMES[id];
      Integer mapped = ID.STRING_TO_ID_MAP.get(name);
      assertNotNull(mapped, name + " is missing from STRING_TO_ID_MAP");
      assertEquals(id, mapped.intValue(), "STRING_TO_ID_MAP maps " + name + " to the wrong id");
      assertSame(S.symbol(id), S.symbol(mapped.intValue()));
    }
    assertEquals(ID.FUNCTION_NAMES.length, ID.STRING_TO_ID_MAP.size(),
        "duplicate names in FUNCTION_NAMES collapse entries of STRING_TO_ID_MAP");
  }

  /**
   * The table is sorted case-insensitively. That is what makes the insertion point of a new symbol
   * unambiguous - appending at the end instead of inserting in order would pass every other
   * assertion here but break the convention the table is maintained by.
   */
  @Test
  public void namesAreSortedCaseInsensitively() {
    for (int id = 1; id < ID.FUNCTION_NAMES.length; id++) {
      String previous = ID.FUNCTION_NAMES[id - 1];
      String current = ID.FUNCTION_NAMES[id];
      assertTrue(previous.compareToIgnoreCase(current) <= 0,
          "FUNCTION_NAMES is out of order at index " + id + ": " + previous + " > " + current);
    }
  }

  @Test
  public void builtInSymbolTableHoldsEveryId() {
    assertTrue(S.BUILT_IN_SYMBOLS.length > ID.FUNCTION_NAMES.length,
        "BUILT_IN_SYMBOLS is sized from ID.ZTransform and must cover every id");
  }

  /**
   * The option symbols of the <code>Solve</code> / <code>Reduce</code> family. They carry no
   * evaluator, so nothing else would fail if a regeneration of the table dropped them.
   */
  @Test
  public void solverOptionSymbolsAreDefined() {
    List<IBuiltInSymbol> optionSymbols = Arrays.asList(S.Backsubstitution, S.Cubics, S.Quartics,
        S.InverseFunctions, S.MaxExtraConditions, S.VerifySolutions);
    for (IBuiltInSymbol symbol : optionSymbols) {
      String name = symbol.toString();
      assertSame(symbol, S.symbol(symbol.ordinal()), name + " isn't registered under its own id");
      assertSame(symbol, F.$s(name), name + " doesn't resolve by name");
    }
  }
}
