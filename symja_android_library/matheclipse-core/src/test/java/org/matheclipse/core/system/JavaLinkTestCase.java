package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

public class JavaLinkTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testJavaNew001() {

    check("loc= JavaNew[\"java.util.Locale\",\"US\"];" //
        + "ds =  JavaNew[\"java.text.DecimalFormatSymbols\", loc];"
        + "dm = JavaNew[\"java.text.DecimalFormat\", \"#.00\", ds]", //
        "JavaObject[class java.text.DecimalFormat]");
    check("dm@format[0.815]", //
        ".81");
  }

  @Test
  public void testInstanceOf001() {
    check("InstanceOf[-0.8, \"\"]", //
        "False");

    check("loc= JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");

    // message InstanceOf: ClassNotFoundException: "".
    check("InstanceOf[loc, \"\"]", //
        "False");

    check("InstanceOf[loc, \"java.util.Locale\"]", //
        "True");
    check("InstanceOf[loc, \"java.io.Serializable\"]", //
        "True");
    check("InstanceOf[loc, \"test\"]", //
        "False");
  }

  @Test
  public void testLoadJavaClass001() {

    check("clazz= LoadJavaClass[\"java.lang.Math\"]", //
        "JavaClass[java.lang.Math]");
    check("Math`sin[0.5]", //
        "0.479426");
  }

  // will only work if JSoup is on classpath
  // @Test
  // public void testLoadJavaClass002() {
  // check("clazz= LoadJavaClass[\"org.jsoup.Jsoup\"]", //
  // "JavaClass[org.jsoup.Jsoup]");
  // check("conn=Jsoup`connect[\"https://jsoup.org/\"];", //
  // "Null");
  // check("doc=conn@get[ ];", //
  // "Null");
  // check("Print[doc@title[ ]];", //
  // "Null");
  // }

  @Test
  public void testJavaObjectQ001() {

    check("loc = JavaNew[\"java.util.Locale\",\"US\"]", //
        "JavaObject[class java.util.Locale]");
    check("JavaObjectQ[loc]", //
        "True");
  }

  @Test
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

  @Test
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
  public void setUp() {
    super.setUp();
    try {
      ToggleFeature.COMPILE = true;
      ToggleFeature.COMPILE_PRINT = true;
      Config.SHORTEN_STRING_LENGTH = 80;
      Config.MAX_AST_SIZE = 20000;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 100;
      Config.FILESYSTEM_ENABLED = true;
      // if you need MMA syntax set relaxedSyntax = false;
      boolean relaxedSyntax = true;
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = relaxedSyntax;
      F.await();

      EvalEngine engine = new EvalEngine(relaxedSyntax);
      EvalEngine.set(engine);
      engine.init();
      engine.setRecursionLimit(512);
      engine.setIterationLimit(500);
      engine.setOutListDisabled(false, (short) 10);

      evaluator = new ExprEvaluator(engine, false, (short) 100);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }
}
