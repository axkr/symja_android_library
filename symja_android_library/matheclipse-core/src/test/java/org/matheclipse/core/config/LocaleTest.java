package org.matheclipse.core.config;

import java.util.Locale;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.system.ExprEvaluatorTestCase;
import org.matheclipse.parser.trie.SuggestTree;

public class LocaleTest extends ExprEvaluatorTestCase {

  public LocaleTest(String name) {
    super(name);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Locale.setDefault(Locale.US);
  }

  public void test001() {
    Locale turkishLang = Locale.forLanguageTag("tr");
    Locale.setDefault(turkishLang);
    F.initSymbols();
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    SuggestTree.Iterator iterator = suggestTree.iterator();
    while (iterator.hasNext()) {
      String term = iterator.next().getTerm();
      assertFalse(term.contains("ı"));
    }
  }

}
