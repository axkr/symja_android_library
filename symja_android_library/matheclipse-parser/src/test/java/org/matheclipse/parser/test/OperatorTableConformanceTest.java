package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.OperatorTable;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;

/**
 * Checks {@link ASTNodeFactory} against {@link OperatorTable}.
 *
 * <p>
 * The table is the specification; this test is what makes it one. Until both factories are built
 * from it, each keeps its own copy of the operator list, and the only thing stopping them drifting
 * apart again is a test that reads the two and compares them.
 *
 * <p>
 * The rows this factory is missing are listed explicitly rather than tolerated in bulk: each one is
 * an operator that {@code ExprParser} accepts and {@code Parser} rejects, and - because
 * {@code OutputFormFactory} resolves operator forms through this factory - an operator that has no
 * output form either.
 */
public class OperatorTableConformanceTest {

  /**
   * Rows present in {@link OperatorTable} but not in {@link ASTNodeFactory}, as
   * {@code token -> head/affix}.
   *
   * <p>
   * {@code ?} and {@code ??} (Information) and {@code } (Conditioned) are operators the core
   * table has and this one never gained. The rest are unicode spellings: this factory registers the
   * unicode alias only for the heads which {@code Characters.NamedCharactersMap} happens to key,
   * and the core table registers more of them.
   */
  private static final List<String> KNOWN_MISSING_ROWS = new ArrayList<>();

  // Empty, and it must stay empty: this factory is now built from OperatorTable, so it registers
  // exactly the rows the table lists. It previously lacked Information (? and ??) and Conditioned,
  // which meant those operators parsed with ExprParser, were rejected by Parser, and had no
  // operator form on output.

  /** Unicode spellings the table carries which this factory never registers. */
  private static final List<String> KNOWN_MISSING_ALIASES = new ArrayList<>();

  // Empty: every alias the table carries is registered by this factory too. It briefly held the
  // PlusMinus row's "\u001b" alias, which turned out to be a typo in the old table rather than a
  // spelling, and is no longer in the table at all.

  @Test
  public void factoryMatchesTheOperatorTable() {
    Map<String, String> tableRows = new TreeMap<>();
    for (OperatorTable.Row row : OperatorTable.ROWS) {
      tableRows.put(key(row.token, row.head, row.affix.name()), describe(row));
    }

    Map<String, String> factoryRows = new TreeMap<>();
    for (Map.Entry<String, ArrayList<Operator>> entry : ASTNodeFactory.MMA_STYLE_FACTORY
        .getOperator2ListMap().entrySet()) {
      for (Operator operator : entry.getValue()) {
        if (!entry.getKey().equals(operator.getOperatorString())) {
          continue; // an alias; checked separately below
        }
        String affix = affixOf(operator);
        factoryRows.put(key(operator.getOperatorString(), operator.getFunctionName(), affix),
            operator.getOperatorString() + " " + operator.getFunctionName() + "/" + affix + " "
                + operator.getPrecedence() + " " + groupingOf(operator));
      }
    }

    List<String> missing = new ArrayList<>();
    List<String> mismatched = new ArrayList<>();
    for (Map.Entry<String, String> row : tableRows.entrySet()) {
      String actual = factoryRows.get(row.getKey());
      if (actual == null) {
        missing.add(row.getKey());
      } else if (!actual.equals(row.getValue())) {
        mismatched.add("  " + row.getKey() + "\n    table:   " + row.getValue()
            + "\n    factory: " + actual);
      }
    }
    List<String> extra = new ArrayList<>(factoryRows.keySet());
    extra.removeAll(tableRows.keySet());

    assertEquals("", String.join("\n", mismatched),
        "operators whose precedence, affix or grouping differs from the table");
    assertEquals(new TreeSet<>(KNOWN_MISSING_ROWS), new TreeSet<>(missing),
        "operators in the table which this factory does not register");
    assertEquals(new ArrayList<String>(), extra,
        "operators this factory registers which are not in the table");
  }

  /**
   * Every alias in the table must resolve to the <em>same</em> operator instance as its token. The
   * parser decides whether a chain flattens by comparing operator identity, so a second instance
   * would make the unicode spelling of a flat operator nest where the ASCII one flattens.
   */
  @Test
  public void aliasesShareTheOperatorInstanceWithTheirToken() {
    Map<Operator, TreeSet<String>> tokensByInstance = new IdentityHashMap<>();
    for (Map.Entry<String, ArrayList<Operator>> entry : ASTNodeFactory.MMA_STYLE_FACTORY
        .getOperator2ListMap().entrySet()) {
      for (Operator operator : entry.getValue()) {
        tokensByInstance.computeIfAbsent(operator, k -> new TreeSet<>()).add(entry.getKey());
      }
    }
    List<String> split = new ArrayList<>();
    List<String> unregistered = new ArrayList<>();
    for (OperatorTable.Row row : OperatorTable.ROWS) {
      for (String alias : row.aliases) {
        List<Operator> forAlias = ASTNodeFactory.MMA_STYLE_FACTORY.getOperatorList(alias);
        List<Operator> forToken = ASTNodeFactory.MMA_STYLE_FACTORY.getOperatorList(row.token);
        if (forAlias == null || forToken == null) {
          unregistered.add(row.head + " " + row.token + " alias");
          continue;
        }
        boolean shared = false;
        for (Operator operator : forAlias) {
          if (forToken.contains(operator) && tokensByInstance.get(operator).contains(row.token)) {
            shared = true;
          }
        }
        if (!shared) {
          split.add("  " + row.head + ": alias does not share an instance with '" + row.token + "'");
        }
      }
    }
    assertEquals("", String.join("\n", split), "aliases registered as separate operator instances");
    // Spellings the core table accepts and this one does not - "a \u2227 b" parses with ExprParser
    // and not with Parser. Pinned rather than tolerated, so the list cannot grow unnoticed.
    assertEquals(new TreeSet<>(KNOWN_MISSING_ALIASES), new TreeSet<>(unregistered),
        "aliases in the table which this factory does not register");
  }

  private static String key(String token, String head, String affix) {
    return token + " -> " + head + "/" + affix;
  }

  private static String describe(OperatorTable.Row row) {
    return row.token + " " + row.head + "/" + row.affix.name() + " " + row.precedence + " "
        + row.grouping.name();
  }

  private static String affixOf(Operator operator) {
    if (operator instanceof PrefixOperator) {
      return "PREFIX";
    }
    if (operator instanceof PostfixOperator) {
      return "POSTFIX";
    }
    return "INFIX";
  }

  private static String groupingOf(Operator operator) {
    if (!(operator instanceof InfixOperator)) {
      return OperatorTable.Grouping.FLAT.name();
    }
    switch (((InfixOperator) operator).getGrouping()) {
      case InfixOperator.LEFT_ASSOCIATIVE:
        return OperatorTable.Grouping.LEFT.name();
      case InfixOperator.RIGHT_ASSOCIATIVE:
        return OperatorTable.Grouping.RIGHT.name();
      default:
        return OperatorTable.Grouping.FLAT.name();
    }
  }
}
