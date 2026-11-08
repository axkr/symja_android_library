package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The canonical order of associations: after the numbers and before the strings, and among
 * themselves rule by rule.
 */
public class AssociationOrderTest extends ExprEvaluatorTestCase {

  @Test
  public void testAssociationBeforeStringsAndSymbols() {
    check("Order(<|\"k\"->1|>, \"abc\")", //
        "1");
    check("Order(\"abc\", <|\"k\"->1, \"m\"->2|>)", //
        "-1");
    check("Order(<|\"k\"->1|>, w)", //
        "1");
    check("Order(<|\"k\"->1|>, g(w))", //
        "1");
    check("Order(<|\"k\"->1|>, 17/3)", //
        "-1");
    check("Sort({g(w), \"st\", <|\"k\"->7|>, w, 5, <||>})", //
        "{5,<||>,<|k->7|>,st,w,g(w)}");
  }

  @Test
  public void testAssociationsRuleByRule() {
    // the values decide when the keys are equal; a beginning comes before its extension
    check("Order(<|\"k\"->5|>, <|\"k\"->4, \"m\"->1|>)", //
        "-1");
    check("Order(<|\"k\"->4|>, <|\"k\"->4, \"m\"->1|>)", //
        "1");
    check("Order(<|\"k\"->7, \"m\"->1|>, <|\"k\"->2, \"q\"->1|>)", //
        "-1");
    // a delayed rule comes first
    check("Order(<|\"b\"->1|>, <|\"y\":>1|>)", //
        "-1");
    check("Order(<|\"b\":>1|>, <|\"b\":>1|>)", //
        "0");
    check("Order(<|2->1|>, <|\"b\"->1|>)", //
        "1");
  }
}
