package org.matheclipse.core.config;

import junit.framework.TestCase;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.trie.SuggestTree;

import java.util.Locale;

public class LocaleTest extends TestCase {

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
