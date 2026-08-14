package org.matheclipse.core.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Guards the head-symbol cache in {@link ExprOperator}.
 *
 * <p>
 * The cache replaced a per-node {@code F.$s(getFunctionName())} call with a lookup by integer id.
 * That is only sound if the two resolve to the same symbol instance, and - because the cached value
 * outlives any single parse - if the id lookup is insensitive to
 * {@link ParserConfig#PARSER_USE_LOWERCASE_SYMBOLS}, which several test classes flip at runtime.
 * Both properties are asserted here rather than assumed.
 */
public class ExprOperatorTest {

  /**
   * Operator function names which are sentinels rather than symbols. They are handled by an
   * overridden {@code createFunction} and never reach {@link ExprOperator#headSymbol()}, so they
   * have no entry in ID.java and must not get one.
   */
  private static final Set<String> SENTINEL_FUNCTION_NAMES =
      new HashSet<>(Arrays.asList("//", "§TILDE§", "PreMinus", "PrePlus"));

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
    ExprParserFactory.initialize();
  }

  @Test
  public void headSymbolMatchesNameResolution() {
    for (Operator operator : operators()) {
      String functionName = operator.getFunctionName();
      if (SENTINEL_FUNCTION_NAMES.contains(functionName)) {
        continue;
      }
      ISymbol byName = F.$s(functionName);
      ISymbol byId = ((ExprOperator) operator).headSymbol();
      assertSame(byName, byId, "head symbol for operator '" + operator.getOperatorString() + "' ("
          + functionName + ") differs from F.$s resolution");
    }
  }

  /**
   * The cached head must not depend on the lowercase-symbols switch, because it is resolved once
   * and then reused for the lifetime of the operator table.
   */
  @Test
  public void headSymbolIsIndependentOfLowercaseSymbolsFlag() {
    boolean saved = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS;
    try {
      for (Operator operator : operators()) {
        String functionName = operator.getFunctionName();
        if (SENTINEL_FUNCTION_NAMES.contains(functionName)) {
          continue;
        }
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
        ISymbol withLowercase = ((ExprOperator) operator).headSymbol();
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
        ISymbol withoutLowercase = ((ExprOperator) operator).headSymbol();
        assertSame(withLowercase, withoutLowercase,
            "head symbol for '" + operator.getOperatorString() + "' depends on the flag");
        // Identity against the id table, not getSymbolName(): the printed name is itself
        // flag-dependent - S.AddTo prints as "addto" while the flag is set - so comparing names
        // would assert the wrong property.
        assertSame(S.symbol(ID.STRING_TO_ID_MAP.get(functionName)), withLowercase,
            "head symbol for '" + operator.getOperatorString() + "' is not the built-in for "
                + functionName);
      }
    } finally {
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = saved;
    }
  }

  /**
   * Every non-sentinel operator head has an id, so none of them falls back to the slow path. If
   * this fails, an operator was added whose head is missing from ID.java.
   */
  @Test
  public void everyOperatorHeadHasAnIdExceptTheSentinels() {
    Set<String> withoutId = new TreeSet<>();
    for (Operator operator : operators()) {
      if (!ID.STRING_TO_ID_MAP.containsKey(operator.getFunctionName())) {
        withoutId.add(operator.getFunctionName());
      }
    }
    assertEquals(new TreeSet<>(SENTINEL_FUNCTION_NAMES), withoutId,
        "operator function names without an ID.java constant");
  }

  /**
   * The same alignment invariant as {@code OperatorTableConsistencyTest} applies to the core table,
   * which is built from its own copy of the three index-aligned arrays.
   *
   * <p>
   * Both surviving rows are collisions rather than typos, and neither can be fixed by editing the
   * name alone. Three operators are named "Apply" so the last write wins; and the "NotEqual" entry
   * is also the key used to look the {@code ≠} character up in
   * {@code Characters.NamedCharactersMap}, so renaming it to "Unequal" would stop that spelling
   * being registered. Both go away when the table stops being three parallel arrays.
   */
  @Test
  public void identifierMapIsAlignedExceptForTheKnownRows() {
    // Empty, and it has to stay empty: this factory is built from OperatorTable, where a row's
    // head is the operator's own function name by construction. The two entries that used to be
    // here - "Apply" holding the MapApply operator, "NotEqual" holding Unequal - were artefacts of
    // the three index-aligned arrays this replaced.
    Map<String, String> expected = new TreeMap<>();

    Map<String, String> misaligned = new TreeMap<>();
    for (Map.Entry<String, ? extends Operator> entry : ExprParserFactory.MMA_STYLE_FACTORY
        .getIdentifier2OperatorMap().entrySet()) {
      String storedName = entry.getValue().getFunctionName();
      if (!entry.getKey().equals(storedName)) {
        misaligned.put(entry.getKey(), storedName);
      }
    }
    assertEquals(expected, misaligned,
        "operator table rows whose name key does not match the operator's function name");
  }

  /**
   * The two hand-maintained tables are meant to describe the same language. Every operator name in
   * one has to appear in the other, or an operator parses in one parser and not the other - which
   * also means it has no operator form on output, because OutputFormFactory resolves through
   * ASTNodeFactory.
   */
  @Test
  public void bothOperatorTablesCoverTheSameOperatorNames() {
    Set<String> exprNames =
        new TreeSet<>(ExprParserFactory.MMA_STYLE_FACTORY.getIdentifier2OperatorMap().keySet());
    Set<String> nodeNames =
        new TreeSet<>(ASTNodeFactory.MMA_STYLE_FACTORY.getIdentifier2OperatorMap().keySet());

    Set<String> onlyInExpr = new TreeSet<>(exprNames);
    onlyInExpr.removeAll(nodeNames);
    Set<String> onlyInNode = new TreeSet<>(nodeNames);
    onlyInNode.removeAll(exprNames);

    // No divergence: both factories are built from the same OperatorTable rows. Until they were,
    // this listed Information, Conditioned and MapApply on one side and NotEqual on the other.
    assertEquals(Collections.emptySet(), onlyInExpr,
        "operators known to ExprParserFactory but missing from ASTNodeFactory");
    // "NotEqual" is the mirror image of the MapApply row above: the node table still files the
    // Unequal operator under that name, and the core table no longer does. Both entries disappear
    // when ASTNodeFactory is built from OperatorTable.
    assertEquals(Collections.emptySet(), onlyInNode,
        "operators known to ASTNodeFactory but missing from ExprParserFactory");
  }

  @Test
  public void headSymbolIsCachedAcrossCalls() {
    for (Operator operator : operators()) {
      if (SENTINEL_FUNCTION_NAMES.contains(operator.getFunctionName())) {
        continue;
      }
      ExprOperator exprOperator = (ExprOperator) operator;
      ISymbol first = exprOperator.headSymbol();
      assertNotNull(first);
      assertSame(first, exprOperator.headSymbol());
    }
  }

  private static Iterable<Operator> operators() {
    Iterable<Operator> all =
        ExprParserFactory.MMA_STYLE_FACTORY.getIdentifier2OperatorMap().values();
    int count = 0;
    for (Operator operator : all) {
      assertTrue(operator instanceof ExprOperator,
          operator.getClass() + " is not an ExprOperator - it cannot cache its head");
      count++;
    }
    assertTrue(count > 50, "operator table looks empty: " + count);
    return all;
  }
}
