package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Invariants over the hand-maintained operator table in {@link ASTNodeFactory}.
 *
 * <p>
 * The table is built from three index-aligned arrays - {@code HEADER_STRINGS[i]},
 * {@code OPERATOR_STRINGS[i]} and {@code OPERATORS[i]} - so an entry inserted into one array and
 * not the others silently shifts every following row. Nothing checked that alignment, and two rows
 * are already wrong. These tests pin the damage: they do not fail today, but they fail as soon as
 * the set of broken rows changes, in either direction.
 */
public class OperatorTableConsistencyTest {

  /**
   * The rows where {@code fOperatorMap} files an operator under a name that is not the operator's
   * own function name, mapped to the function name actually stored.
   *
   * <ul>
   * <li><b>Apply</b> - {@code HEADER_STRINGS} names three consecutive operators "Apply" ({@code @},
   * {@code @@}, {@code @@@}) while the third operator is {@code MapApply}, so the last write wins
   * and {@code get("Apply")} hands back the {@code @@@} operator. {@code OutputFormFactory} works
   * around this by naming {@code ASTNodeFactory.APPLY_OPERATOR} and
   * {@code ASTNodeFactory.MAPAPPLY_OPERATOR} explicitly rather than looking either up by name.
   * <li><b>NotEqual</b> - the {@code HEADER_STRINGS} entry for the {@code ≠} row says
   * "NotEqual" while the operator built for it is {@code Unequal}. Parsing is unaffected, since
   * that goes through the token table; the name-keyed entry is simply unreachable.
   * </ul>
   *
   * <p>
   * Both disappear when the table stops being three parallel arrays. Until then, do not "fix" this
   * test by widening it - a new entry here means a new misalignment.
   */
  /**
   * Empty, and it must stay empty. Both factories are built from {@code OperatorTable}, where a
   * row's head is the operator's own function name by construction, so the two entries this used to
   * carry - "Apply" holding the MapApply operator because three rows were named "Apply", and
   * "NotEqual" holding Unequal - cannot come back.
   */
  private static final Map<String, String> KNOWN_MISALIGNED_ENTRIES = new TreeMap<>();

  @Test
  public void identifierMapIsAlignedExceptForTheKnownRows() {
    Map<String, String> misaligned = new TreeMap<>();
    for (Map.Entry<String, ? extends Operator> entry : ASTNodeFactory.MMA_STYLE_FACTORY
        .getIdentifier2OperatorMap().entrySet()) {
      String storedName = entry.getValue().getFunctionName();
      if (!entry.getKey().equals(storedName)) {
        misaligned.put(entry.getKey(), storedName);
      }
    }
    assertEquals(KNOWN_MISALIGNED_ENTRIES, misaligned,
        "operator table rows whose name key does not match the operator's function name");
  }

  /** Every registered token resolves to at least one operator, and the table is not empty. */
  @Test
  public void everyTokenHasAnOperator() {
    Map<String, ? extends java.util.List<Operator>> tokens =
        ASTNodeFactory.MMA_STYLE_FACTORY.getOperator2ListMap();
    assertTrue(tokens.size() > 90, "operator token table looks too small: " + tokens.size());
    for (Map.Entry<String, ? extends java.util.List<Operator>> entry : tokens.entrySet()) {
      assertTrue(entry.getValue() != null && !entry.getValue().isEmpty(),
          "no operator registered for token '" + entry.getKey() + "'");
    }
  }

  /**
   * A token resolves either to an operator spelled the same way, or to one reached through a
   * unicode alias - the alias registers the very same operator instance under a second token, which
   * is why an operator cannot tell which spelling was scanned. That is the cause of the
   * <code>a≤b≤c</code> inconsistency noted in {@code ExprParser}.
   */
  @Test
  public void tokensAreEitherTheOperatorSpellingOrAnAlias() {
    java.util.Set<String> aliases = new TreeSet<>();
    for (Map.Entry<String, ? extends java.util.List<Operator>> entry : ASTNodeFactory.MMA_STYLE_FACTORY
        .getOperator2ListMap().entrySet()) {
      for (Operator operator : entry.getValue()) {
        if (!operator.getOperatorString().equals(entry.getKey())) {
          aliases.add(entry.getKey() + " -> " + operator.getOperatorString());
        }
      }
    }
    // Aliases are expected; this asserts only that they are all genuine second spellings of an
    // operator that also carries its own token, never a token with no operator of its own.
    for (String alias : aliases) {
      String canonical = alias.substring(alias.indexOf("-> ") + 3);
      assertTrue(ASTNodeFactory.MMA_STYLE_FACTORY.getOperatorList(canonical) != null,
          "alias " + alias + " points at a token which is not itself registered");
    }
  }
}
