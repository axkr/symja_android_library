package org.matheclipse.core.javalink;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.rubi.AbstractRubiTestCase;
import org.matheclipse.io.IOInit;

public class JavaLinkTests extends AbstractRubiTestCase {

  public JavaLinkTests(String name) {
    super(name, false);
  }

  public void testJavaNew001() {

    check("loc= JavaNew[\"java.util.Locale\",\"US\"];" //
        + "ds =  JavaNew[\"java.text.DecimalFormatSymbols\", loc];"
        + "dm = JavaNew[\"java.text.DecimalFormat\", \"#.00\", ds]", //
        "JavaObject[class java.text.DecimalFormat]");
    check("dm@format[0.815]", //
        "\".81\"");
  }

  public void testInstanceOf001() {

    check("loc= JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");
    check("InstanceOf[loc, \"java.util.Locale\"]", //
        "True");
    check("InstanceOf[loc, \"java.io.Serializable\"]", //
        "True");
    check("InstanceOf[loc, \"test\"]", //
        "False");
  }

  public void testLoadJavaClass001() {

    check("clazz= LoadJavaClass[\"java.lang.Math\"]", //
        "JavaClass[java.lang.Math]");
    check("Math`sin[0.5]", //
        "0.479425538604203");
  }

  public void testLoadJavaClass002() {
    check("clazz= LoadJavaClass[\"org.jsoup.Jsoup\"]", //
        "JavaClass[org.jsoup.Jsoup]");
    check("conn=Jsoup`connect[\"https://jsoup.org/\"];", //
        "Null");
    check("doc=conn@get[ ];", //
        "Null");
    check("Print[doc@title[ ]];", //
        "Null");
  }

  public void testJavaObjectQ001() {

    check("loc = JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");
    check("JavaObjectQ[loc]", //
        "True");
  }

  public void testSameObject001() {

    check("loc1= JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");
    check("loc2= JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");
    check("SameObjectQ[loc1, loc2]", //
        "False");
    check("SameObjectQ[loc1, loc1]", //
        "True");
  }

  public void testJavaShow001() {
    // check(
    // "frame= JavaNew[ \"javax.swing.JFrame\", \"Simple JFrame Demo\"];", //
    // "Null");
    // check(
    // "loc= JavaShow[frame ];Pause[10]", //
    // "");
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
    IOInit.init();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
