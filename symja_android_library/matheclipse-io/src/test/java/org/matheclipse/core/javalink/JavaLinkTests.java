package org.matheclipse.core.javalink;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class JavaLinkTests extends AbstractRubiTestCase {

  public JavaLinkTests(String name) {
    super(name, false);
  }

  public void testJavaNew001() {

    check(
        "loc= JavaNew[\"java.util.Locale\",\"US\"];" //
            + "ds =  JavaNew[\"java.text.DecimalFormatSymbols\", loc];"
            + "dm = JavaNew[\"java.text.DecimalFormat\", \"#.00\", ds]", //
        "JavaObject[class java.text.DecimalFormat]");
    check(
        "dm@format[0.815]", //
        "\".81\"");
  }

  public void testLoadJavaClass001() {

    check(
        "clazz= LoadJavaClass[\"java.lang.Math\"]", //
        "JavaClass[java.lang.Math]");
    check(
        "Math`sin[0.5]", //
        "0.479425538604203");
  }

  public void testLoadJavaClass002() {
    check(
        "clazz= LoadJavaClass[\"org.jsoup.Jsoup\"]", //
        "JavaClass[org.jsoup.Jsoup]");
    check(
        "conn=Jsoup`connect[\"https://jsoup.org/\"];", //
        "Null");
    check(
        "doc=conn@get[ ];", //
        "Null");
    check(
        "Print[doc@title[ ]];", //
        "Null");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    Config.BUILTIN_PROTECTED = ISymbol.NOATTRIBUTE;
    super.setUp();
    fSeconds = 600;
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
