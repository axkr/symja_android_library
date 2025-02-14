package org.matheclipse.api;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hipparchus.util.FastMath;
import org.junit.Ignore;
import org.junit.Test;
import org.matheclipse.api.client.JSONQueryResult;
import org.matheclipse.core.eval.EvalEngine;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

public class TestPods {
  static int formatsMATHML = 0;

  static int formatsTEX = 0;

  static int formatsHTML = 0;

  static {
    // ToggleFeature.COMPILE = false;
    // ToggleFeature.COMPILE_PRINT = true;
    // ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    // Config.FUZZY_PARSER = true;
    // Config.UNPROTECT_ALLOWED = false;
    // Config.USE_MANIPULATE_JS = true;
    // Config.JAS_NO_THREADS = false;
    // Config.MATHML_TRIG_LOWERCASE = false;
    // Config.MAX_AST_SIZE = 10000;
    // Config.MAX_OUTPUT_SIZE = 10000;
    // Config.MAX_BIT_LENGTH = 200000;
    // Config.MAX_POLYNOMIAL_DEGREE = 100;
    // Config.MAX_INPUT_LEAVES = 100L;
    // Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    // EvalEngine.get().setPackageMode(true);
    // F.initSymbols();
    // FuzzyParserFactory.initialize();
    SymjaServer.initAPI();
    formatsMATHML = Pods.internFormat(new String[] {"mathml", "plaintext"});
    formatsTEX = Pods.internFormat(new String[] {"latex", "plaintext", "sinput"});
    formatsHTML = Pods.internFormat(new String[] {"html", "plaintext", "sinput"});
  }

  public static String toPrettyStringNormalizingNewline(ObjectNode messageJSON) {
    return messageJSON.toPrettyString().replace("\r\n", "\n").replace("\\r\\n", "\\n");
  }

  private static final Pattern FLOATING_POINT_NUMBER = Pattern.compile("\\d+\\.\\d+");
  private static final int LEAST_SIGNIFICANT_FIGURE_DELTA = 10;

  public static void assertEqualsWithFloatLSFDelta(String actual, String expected) {
    // Slide over each string with a matcher that matches floating-point numbers.
    // In-between floats the strings must be equals. The floats them self must be equals before the
    // lest significant figure but some deviation at the least significant digits are allowed
    int ePrefixStart = 0;
    int aPrefixStart = 0;
    Matcher eMatcher = FLOATING_POINT_NUMBER.matcher(expected);
    Matcher aMatcher = FLOATING_POINT_NUMBER.matcher(actual);
    while (eMatcher.find()) {
      if (!aMatcher.find()) {
        break;
      }

      // assert parts after the last float until the float number are equal
      String eNonFloatPrefix = expected.substring(ePrefixStart, eMatcher.start());
      String aNonFloatPrefix = actual.substring(aPrefixStart, aMatcher.start());
      assertEquals(eNonFloatPrefix, aNonFloatPrefix);

      String eFloat = eMatcher.group();
      String aFloat = aMatcher.group();
      assertLSFDeltaEqualFloats(eFloat, aFloat);

      ePrefixStart = eMatcher.end();
      aPrefixStart = aMatcher.end();
    }
    // assert remaining parts after last number are equal
    assertEquals(expected.substring(ePrefixStart), actual.substring(aPrefixStart));
  }

  private static void assertLSFDeltaEqualFloats(String expectedFloat, String actualFloat) {
    if (expectedFloat.equals(actualFloat)) {
      return; // floats are fully equal
    }
    int commonLength = Strings.commonPrefix(expectedFloat, actualFloat).length();
    if (expectedFloat.indexOf('.') <= commonLength) {
      // figures before the decimal are equals

      int longestLength = FastMath.max(expectedFloat.length(), actualFloat.length());
      int eDifferingFigures = getUnifiedUncommonFigures(expectedFloat, commonLength, longestLength);
      int aDifferingFigures = getUnifiedUncommonFigures(actualFloat, commonLength, longestLength);

      if (FastMath.abs(aDifferingFigures - eDifferingFigures) < LEAST_SIGNIFICANT_FIGURE_DELTA) {
        return;
      }
    }
    assertEquals(expectedFloat, actualFloat); // fail with meaningful message
  }

  private static int getUnifiedUncommonFigures(String number, int commonLength, int longestLength) {
    String differingFigures = Strings.padEnd(number, longestLength, '0').substring(commonLength);
    return Integer.parseInt(differingFigures);
  }

  public static ObjectNode createJUnitResult(String inputStr, int formats) {
    try {
      return Pods.createResult(inputStr, formats, false, null);
    } catch (RuntimeException rex) {
      rex.printStackTrace();
      return Pods.errorJSON("0", "JSON Export Failed");
    }
  }

  // @Test
  // public void testAsk001() {
  // ObjectNode messageJSON =
  // TestPods.createJUnitResult("what is ten pounds to milligrams", formatsHTML);
  // final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
  //
  // assertEquals(
  // jsonStr, //
  // "{\n"
  // + " \"queryresult\" : {\n"
  // + " \"success\" : \"true\",\n"
  // + " \"error\" : \"false\",\n"
  // + " \"numpods\" : 2,\n"
  // + " \"version\" : \"0.1\",\n"
  // + " \"pods\" : [ {\n"
  // + " \"title\" : \"Input\",\n"
  // + " \"scanner\" : \"Identity\",\n"
  // + " \"error\" : \"false\",\n"
  // + " \"numsubpods\" : 1,\n"
  // + " \"subpods\" : [ {\n"
  // + " \"plaintext\" : \"conv(10,pound,milligrams)\",\n"
  // + " \"sinput\" : \"conv(10,pound,milligrams)\"\n"
  // + " } ]\n"
  // + " }, {\n"
  // + " \"title\" : \"Evaluated result\",\n"
  // + " \"scanner\" : \"Expression\",\n"
  // + " \"error\" : \"false\",\n"
  // + " \"numsubpods\" : 1,\n"
  // + " \"subpods\" : [ {\n"
  // + " \"plaintext\" : \"conv(10,pound,milligrams)\",\n"
  // + " \"sinput\" : \"conv(10,pound,milligrams)\"\n"
  // + " } ]\n"
  // + " } ]\n"
  // + " }\n"
  // + "}"); //
  // }

  // @Test
  // public void testAsk002() {
  //
  // ObjectNode messageJSON = TestPods.createJUnitResult("solve x^3+sqrt(x^3+3)=10",
  // formatsHTML);
  // final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
  //
  // assertEquals(
  // jsonStr, //
  // ""); //
  // }

  @Test
  public void testSyntaxError001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("?#?", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertEquals(queryResult.isError(), false);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + //
            "  \"queryresult\" : {\n" + //
            "    \"success\" : \"false\",\n" + //
            "    \"numpods\" : 0,\n" + //
            "    \"version\" : \"0.1\"\n" + //
            "  }\n" + //
            "}"); //
  }

  @Test
  public void testMarkdownHelp() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin", formatsHTML);

    JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 3,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Sin\",\n" //
    // + " \"sinput\" : \"Sin\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Plot\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" :
    // \"Manipulate(Plot(Sin(a*x),{x,-10.0`,10.0`},PlotRange-&gt;{-2.0`,2.0`}),{a,1,10})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-11.55,2.75,11.55,-2.75]});\\nboard.suspendUpdate();\\nvar a =
    // board.create('slider',[[-9.24,2.2],[9.24,2.2],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) {
    // try { return [sin(mul(a.Value(),x))];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -10.0,
    // 10.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-11.55,2.75,11.55,-2.75]});\\nboard.suspendUpdate();\\nvar a =
    // board.create('slider',[[-9.24,2.2],[9.24,2.2],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) {
    // try { return [sin(mul(a.Value(),x))];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -10.0,
    // 10.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Documentation\",\n" //
    // + " \"scanner\" : \"help\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"markdown\" : \"## Sin\\n\\n```\\nSin(expr)\\n```\\n\\n> returns the sine of `expr`
    // (measured in radians).\\n \\n`Sin(expr)` will evaluate automatically in the case `expr` is a
    // multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.\\n\\nSee\\n* [Wikipedia -
    // Sine](https://en.wikipedia.org/wiki/Sine)\\n* [Fungrim -
    // Sine](http://fungrim.org/topic/Sine/)\\n* [Wikipedia -
    // Radian](https://en.wikipedia.org/wiki/Radian)\\n* [Wikipedia -
    // Degree](https://en.wikipedia.org/wiki/Degree_(angle))\\n\\n### Examples\\n\\n```\\n>>
    // Sin(0)\\n0\\n\\n>> Sin(0.5)\\n0.479425538604203\\n\\n>> Sin(3*Pi)\\n0\\n\\n>> Sin(1.0 +
    // I)\\n1.2984575814159773+I*0.6349639147847361\\n```\\n \\n\\n### Github\\n\\n* [Implementation
    // of
    // Sin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2730)
    // \\n[Github
    // master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2861)\\n\\n\",\n"
    // //
    // + " \"html\" :
    // \"<h2>Sin</h2>\\n<pre><code>Sin(expr)\\n</code></pre>\\n<blockquote>\\n<p>returns the sine of
    // <code>expr</code> (measured in radians).</p>\\n</blockquote>\\n<p><code>Sin(expr)</code> will
    // evaluate automatically in the case <code>expr</code> is a multiple of <code>Pi, Pi/2, Pi/3,
    // Pi/4</code> and <code>Pi/6</code>.</p>\\n<p>See</p>\\n<ul>\\n<li><a
    // href=\\\"https://en.wikipedia.org/wiki/Sine\\\">Wikipedia - Sine</a></li>\\n<li><a
    // href=\\\"http://fungrim.org/topic/Sine/\\\">Fungrim - Sine</a></li>\\n<li><a
    // href=\\\"https://en.wikipedia.org/wiki/Radian\\\">Wikipedia - Radian</a></li>\\n<li><a
    // href=\\\"https://en.wikipedia.org/wiki/Degree_(angle)\\\">Wikipedia -
    // Degree</a></li>\\n</ul>\\n<h3>Examples</h3>\\n<pre><code>&gt;&gt; Sin(0)\\n0\\n\\n&gt;&gt;
    // Sin(0.5)\\n0.479425538604203\\n\\n&gt;&gt; Sin(3*Pi)\\n0\\n\\n&gt;&gt; Sin(1.0 +
    // I)\\n1.2984575814159773+I*0.6349639147847361\\n</code></pre>\\n<h3>Github</h3>\\n<ul>\\n<li><a
    // href=\\\"https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2730\\\">Implementation
    // of Sin</a>\\n<a
    // href=\\\"https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2861\\\">Github
    // master</a></li>\\n</ul>\\n\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testTeXParser() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("\\sin( 30 ^ { \\circ } )", formatsHTML);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 3,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Sin(30*Degree)\",\n" //
            + "        \"sinput\" : \"Sin(30*Degree)\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Exact result\",\n" //
            + "      \"scanner\" : \"Rational\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"1/2\",\n" //
            + "        \"sinput\" : \"Sin(30*Degree)\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Decimal form\",\n" //
            + "      \"scanner\" : \"Numeric\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"0.5\",\n" //
            + "        \"sinput\" : \"N(Sin(30*Degree))\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testSoundexHelp() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Cs", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"numpods\" : 34,\n" + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"cs\",\n"
            + "        \"sinput\" : \"cs\",\n" + "        \"latex\" : \"cs\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Standard Name\",\n"
            + "      \"scanner\" : \"Data\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Caesium\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"StandardName\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{Caesium}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Atomic Number\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"55\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicNumber\\\")\",\n"
            + "        \"latex\" : \"55\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Abbreviation\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Cs\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Abbreviation\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{Cs}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Absolute Boiling Point\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"944\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AbsoluteBoilingPoint\\\")\",\n"
            + "        \"latex\" : \"944\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Absolute Melting Point\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"301.7\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AbsoluteMeltingPoint\\\")\",\n"
            + "        \"latex\" : \"301.7\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Atomic Radius\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"260\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicRadius\\\")\",\n"
            + "        \"latex\" : \"260\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Atomic Weight\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"132.91\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicWeight\\\")\",\n"
            + "        \"latex\" : \"132.91\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Block\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"s\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Block\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{s}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Boiling Point\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"670.85\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BoilingPoint\\\")\",\n"
            + "        \"latex\" : \"670.85\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Brinell Hardness\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"0.14\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BrinellHardness\\\")\",\n"
            + "        \"latex\" : \"0.14\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Bulk Modulus\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1.6\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BulkModulus\\\")\",\n"
            + "        \"latex\" : \"1.6\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Covalent Radius\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"225\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"CovalentRadius\\\")\",\n"
            + "        \"latex\" : \"225\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Crust Abundance\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"0\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"CrustAbundance\\\")\",\n"
            + "        \"latex\" : \"0\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Density\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1873\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Density\\\")\",\n"
            + "        \"latex\" : \"1873\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Discovery Year\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1860\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"DiscoveryYear\\\")\",\n"
            + "        \"latex\" : \"1860\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Electro Negativity\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"0.79\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectroNegativity\\\")\",\n"
            + "        \"latex\" : \"0.79\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Electron Affinity\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"45.51\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronAffinity\\\")\",\n"
            + "        \"latex\" : \"45.51\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Electron Configuration\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"[Xe] 6s1\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronConfiguration\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{[Xe] 6s1}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Electron Configuration String\",\n"
            + "      \"scanner\" : \"Data\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{2,8,18,18,8,1}\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronConfigurationString\\\")\",\n"
            + "        \"latex\" : \"\\\\{2,8,18,18,8,1\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Fusion Heat\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"2.09\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"FusionHeat\\\")\",\n"
            + "        \"latex\" : \"2.09\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Group\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Group\\\")\",\n"
            + "        \"latex\" : \"1\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Ionization Energies\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"{375.7,2234.3,3400}\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"IonizationEnergies\\\")\",\n"
            + "        \"latex\" : \"\\\\{375.7,2234.3,3400\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Liquid Density\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1843\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"LiquidDensity\\\")\",\n"
            + "        \"latex\" : \"1843\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Melting Point\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"28.55\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"MeltingPoint\\\")\",\n"
            + "        \"latex\" : \"28.55\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Mohs Hardness\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"0.2\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"MohsHardness\\\")\",\n"
            + "        \"latex\" : \"0.2\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Name\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"caesium\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Name\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{caesium}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Period\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"6\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Period\\\")\",\n"
            + "        \"latex\" : \"6\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Series\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"AlkaliMetal\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Series\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{AlkaliMetal}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Shear Modulus\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{{2},{2,6},{2,6,10},{2,6,10},{2,6},{1}}\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ShearModulus\\\")\",\n"
            + "        \"latex\" : \"\\\\{\\\\{2\\\\},\\\\{2,6\\\\},\\\\{2,6,10\\\\},\\\\{2,6,10\\\\},\\\\{2,6\\\\},\\\\{1\\\\}\\\\}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Specific Heat\",\n"
            + "      \"scanner\" : \"Data\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"242\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"SpecificHeat\\\")\",\n"
            + "        \"latex\" : \"242\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Thermal Conductivity\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"35.9\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ThermalConductivity\\\")\",\n"
            + "        \"latex\" : \"35.9\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Vaporization Heat\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"63.9\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"VaporizationHeat\\\")\",\n"
            + "        \"latex\" : \"63.9\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Young Modulus\",\n" + "      \"scanner\" : \"Data\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"37\",\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"YoungModulus\\\")\",\n"
            + "        \"latex\" : \"37\"\n" + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testInteger17() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("17", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 9,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"17\",\n" //
            + "        \"sinput\" : \"17\",\n" //
            + "        \"latex\" : \"17\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Number name\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"seventeen\",\n" //
            + "        \"sinput\" : \"IntegerName(17,\\\"Words\\\")\",\n" //
            + "        \"latex\" : \"\\\\textnormal{seventeen}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Roman numerals\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"XVII\",\n" //
            + "        \"sinput\" : \"RomanNumeral(17)\",\n" //
            + "        \"latex\" : \"\\\\textnormal{XVII}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Binary form\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"10001_2\",\n" //
            + "        \"sinput\" : \"BaseForm(17,2)\",\n" //
            + "        \"latex\" : \"{\\\\textnormal{10001}}_{2}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Prime factorization\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"17 is a prime number.\",\n" //
            + "        \"sinput\" : \"FactorInteger(17)\",\n" //
            + "        \"latex\" : \"\\\\{\\\\{17,1\\\\}\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Residues modulo small integers\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;17 mod m&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;5&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;8&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n" //
            + "        \"sinput\" : \"Mod(17,{2,3,4,5,6,7,8,9})\",\n" //
            + "        \"latex\" : \"\\\\{1,2,1,2,5,3,1,8\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Properties\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 2,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"17 is an odd number.\",\n" //
            + "        \"sinput\" : \"OddQ(17)\"\n" //
            + "      }, {\n" //
            + "        \"plaintext\" : \"17 is the 7th prime number.\",\n" //
            + "        \"sinput\" : \"PrimePi(17)\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Quadratic residues modulo 17\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{0,1,2,4,8,9,13,15,16}\",\n" //
            + "        \"sinput\" : \"Union(PowerMod(Range(0,17/2),2,17))\",\n" //
            + "        \"latex\" : \"\\\\{0,1,2,4,8,9,13,15,16\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Primitive roots modulo 17\",\n" //
            + "      \"scanner\" : \"Integer\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{3,5,6,7,10,11,12,14}\",\n" //
            + "        \"sinput\" : \"Select(Range(16),MultiplicativeOrder(#1,17)==EulerPhi(17)&amp;)\",\n" //
            + "        \"latex\" : \"\\\\{3,5,6,7,10,11,12,14\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testRationalHalf() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + //
            "  \"queryresult\" : {\n" + //
            "    \"success\" : \"true\",\n" + //
            "    \"numpods\" : 3,\n" + //
            "    \"version\" : \"0.1\",\n" + //
            "    \"pods\" : [ {\n" + //
            "      \"title\" : \"Input\",\n" + //
            "      \"scanner\" : \"Identity\",\n" + //
            "      \"error\" : \"false\",\n" + //
            "      \"numsubpods\" : 1,\n" + //
            "      \"subpods\" : [ {\n" + //
            "        \"plaintext\" : \"1/2\",\n" + //
            "        \"sinput\" : \"1/2\",\n" + //
            "        \"latex\" : \"\\\\frac{1}{2}\"\n" + //
            "      } ]\n" + //
            "    }, {\n" + //
            "      \"title\" : \"Exact result\",\n" + //
            "      \"scanner\" : \"Rational\",\n" + //
            "      \"error\" : \"false\",\n" + //
            "      \"numsubpods\" : 1,\n" + //
            "      \"subpods\" : [ {\n" + //
            "        \"plaintext\" : \"1/2\",\n" + //
            "        \"sinput\" : \"1/2\",\n" + //
            "        \"latex\" : \"\\\\frac{1}{2}\"\n" + //
            "      } ]\n" + //
            "    }, {\n" + //
            "      \"title\" : \"Decimal form\",\n" + //
            "      \"scanner\" : \"Numeric\",\n" + //
            "      \"error\" : \"false\",\n" + //
            "      \"numsubpods\" : 1,\n" + //
            "      \"subpods\" : [ {\n" + //
            "        \"plaintext\" : \"0.5\",\n" + //
            "        \"sinput\" : \"N(1/2)\",\n" + //
            "        \"latex\" : \"0.5\"\n" + //
            "      } ]\n" + //
            "    } ]\n" + //
            "  }\n" + //
            "}"); //
  }

  @Ignore
  public void testRationalPlus() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2+3/4", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 5,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1/2+3/4\",\n"
            + "        \"sinput\" : \"1/2 + 3/4\",\n"
            + "        \"latex\" : \"\\\\frac{1}{2}+\\\\frac{3}{4}\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Exact result\",\n"
            + "      \"scanner\" : \"Rational\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"5/4\",\n" + "        \"sinput\" : \"1/2 + 3/4\",\n"
            + "        \"latex\" : \"\\\\frac{5}{4}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Decimal form\",\n" + "      \"scanner\" : \"Numeric\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1.25\",\n"
            + "        \"sinput\" : \"N(1/2 + 3/4)\",\n" + "        \"latex\" : \"1.25\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Mixed fraction\",\n"
            + "      \"scanner\" : \"Rational\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"1 1/4\",\n"
            + "        \"sinput\" : \"{IntegerPart(5/4),FractionalPart(5/4)}\",\n"
            + "        \"latex\" : \"\\\\{1,\\\\frac{1}{4}\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Continued fraction\",\n"
            + "      \"scanner\" : \"ContinuedFraction\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"[1; 4]\",\n"
            + "        \"sinput\" : \"ContinuedFraction(5/4)\",\n"
            + "        \"latex\" : \"\\\\{1,4\\\\}\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testPlotSin() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(Sin(x), {x, 0, 6*Pi} )", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 2,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
    // + " \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
    // + " \"latex\" : \"\\\\text{Plot}(\\\\sin (x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-1.492477796076938,1.65,20.342033717615696,-1.65]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, 0,
    // (18.84955592153876)],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-1.492477796076938,1.65,20.342033717615696,-1.65]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, 0,
    // (18.84955592153876)],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testPlot002() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods
        .createJUnitResult("Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}) // JSForm", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" + " \"queryresult\" : {\n" + " \"success\" : \"true\",\n"
    // + " \"error\" : \"false\",\n" + " \"numpods\" : 2,\n"
    // + " \"version\" : \"0.1\",\n" + " \"pods\" : [ {\n"
    // + " \"title\" : \"Input\",\n" + " \"scanner\" : \"Identity\",\n"
    // + " \"error\" : \"false\",\n" + " \"numsubpods\" : 1,\n"
    // + " \"subpods\" : [ {\n"
    // + " \"plaintext\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\n"
    // + " \"sinput\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\n"
    // + " \"latex\" : \"\\\\text{JSForm}(\\\\text{Plot}(\\\\{\\\\sin (x),\\\\cos (x),\\\\tan
    // (x)\\\\},\\\\{x,\\\\left( -2\\\\right) \\\\cdot \\\\pi,2\\\\cdot \\\\pi\\\\}))\"\n"
    // + " } ]\n" + " }, {\n" + " \"title\" : \"Result\",\n"
    // + " \"scanner\" : \"String form\",\n" + " \"error\" : \"false\",\n"
    // + " \"numsubpods\" : 1,\n" + " \"subpods\" : [ {\n"
    // + " \"html\" : \"&lt;iframe srcdoc=\\\"&amp;lt;?xml version=&amp;quot;1.0&amp;quot;
    // encoding=&amp;quot;UTF-8&amp;quot;?&amp;gt;\\n\\n&amp;lt;!DOCTYPE html PUBLIC\\n
    // &amp;quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&amp;quot;\\n
    // &amp;quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&amp;quot;&amp;gt;\\n\\n&amp;lt;html
    // xmlns=&amp;quot;http://www.w3.org/1999/xhtml&amp;quot; style=&amp;quot;width: 100%; height:
    // 100%; margin: 0; padding: 0&amp;quot;&amp;gt;\\n&amp;lt;head&amp;gt;\\n&amp;lt;meta
    // charset=&amp;quot;utf-8&amp;quot;&amp;gt;\\n&amp;lt;title&amp;gt;Highlight&amp;lt;/title&amp;gt;\\n\\n&amp;lt;link
    // rel=&amp;quot;stylesheet&amp;quot; type=&amp;quot;text/css&amp;quot;
    // href=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/styles/default.min.css&amp;quot;
    // /&amp;gt;\\n &amp;lt;script type=&amp;quot;text/javascript&amp;quot;
    // src=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/highlight.min.js&amp;quot;&amp;gt;&amp;lt;/script&amp;gt;\\n&amp;lt;script&amp;gt;hljs.initHighlightingOnLoad();&amp;lt;/script&amp;gt;&amp;lt;/head&amp;gt;\\n&amp;lt;body
    // style=&amp;quot;width: 100%; height: 100%; margin: 0; padding:
    // 0&amp;quot;&amp;gt;\\n\\n&amp;lt;div id=&amp;quot;highlight&amp;quot; style=&amp;quot;width:
    // 600px; height: 800px; margin: 0; padding: .25in .5in .5in .5in; flex-direction: column;
    // overflow: hidden&amp;quot;&amp;gt;\\n&amp;lt;pre&amp;gt;&amp;lt;code
    // class=&amp;quot;javascript&amp;quot;&amp;gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-7.461503837897545,18.033999328253977,7.461503837897545,-18.033999328251273]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try {
    // return [cos(x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return
    // [tan(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1,
    // (-6.283185307179586),
    // (6.283185307179586)],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2,
    // (-6.283185307179586),
    // (6.283185307179586)],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3,
    // (-6.283185307179586),
    // (6.283185307179586)],{strokecolor:'#8fb032'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&amp;lt;/code&amp;gt;&amp;lt;/pre&amp;gt;\\n&amp;lt;/div&amp;gt;\\n&amp;lt;/body&amp;gt;\\n&amp;lt;/html&amp;gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\"
    // &gt;&lt;/iframe&gt;\"\n"
    // + " } ]\n" + " } ]\n" + " }\n" + "}"); //
  }

  @Test
  public void testPlotF() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(f(x), {x, 0, 6*Pi} )", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" + " \"queryresult\" : {\n" + " \"success\" : \"true\",\n"
    // + " \"error\" : \"false\",\n" + " \"numpods\" : 2,\n"
    // + " \"version\" : \"0.1\",\n" + " \"pods\" : [ {\n"
    // + " \"title\" : \"Input\",\n" + " \"scanner\" : \"Identity\",\n"
    // + " \"error\" : \"false\",\n" + " \"numsubpods\" : 1,\n"
    // + " \"subpods\" : [ {\n" + " \"plaintext\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
    // + " \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
    // + " \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n"
    // + " } ]\n" + " }, {\n" + " \"title\" : \"Evaluated result\",\n"
    // + " \"scanner\" : \"Expression\",\n" + " \"error\" : \"true\",\n"
    // + " \"numsubpods\" : 1,\n" + " \"subpods\" : [ {\n"
    // + " \"plaintext\" : \"Manipulate: Cannot convert to JavaScript. Function head: f\",\n"
    // + " \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
    // + " \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n"
    // + " } ]\n" + " } ]\n" + " }\n" + "}"); //
  }

  @Test
  public void testPlotBessel() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods
        .createJUnitResult("Plot(Evaluate(Table(BesselJ(n, x), {n, 4})), {x, 0, 10})", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 2,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\n" //
    // + " \"sinput\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\n" //
    // + " \"latex\" :
    // \"\\\\text{Plot}(\\\\text{Evaluate}(\\\\text{Table}(J_n(x),\\\\{n,4\\\\})),\\\\{x,0,10\\\\})\"\n"
    // //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-1.05,1.1778908410078661,11.05,-0.9423347230773148]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [besselJ(1,x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) {
    // try { return [besselJ(2,x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try {
    // return [besselJ(3,x)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return
    // [besselJ(4,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0,
    // 10],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, 0,
    // 10],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, 0,
    // 10],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, 0,
    // 10],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-1.05,1.1778908410078661,11.05,-0.9423347230773148]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [besselJ(1,x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) {
    // try { return [besselJ(2,x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try {
    // return [besselJ(3,x)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return
    // [besselJ(4,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0,
    // 10],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, 0,
    // 10],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, 0,
    // 10],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, 0,
    // 10],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testSin() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin(x)", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 5,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Sin(x)\",\n" //
    // + " \"sinput\" : \"Sin(x)\",\n" //
    // + " \"latex\" : \"\\\\sin (x)\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot(Sin(x),{x,-7.0`,7.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,1.6494784136660268,8.25,-1.6494784136660272]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,1.6494784136660268,8.25,-1.6494784136660272]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Alternate form\",\n" //
    // + " \"scanner\" : \"Simplification\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"(I*1/2)/E^(I*x)-I*1/2*E^(I*x)\",\n" //
    // + " \"sinput\" : \"TrigToExp(Sin(x))\",\n" //
    // + " \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\cdot x}} + \\\\left(
    // \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i \\\\cdot x}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Derivative\",\n" //
    // + " \"scanner\" : \"Derivative\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Cos(x)\",\n" //
    // + " \"sinput\" : \"D(Sin(x),x)\",\n" //
    // + " \"latex\" : \"\\\\cos (x)\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Indefinite integral\",\n" //
    // + " \"scanner\" : \"Integral\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"-Cos(x)\",\n" //
    // + " \"sinput\" : \"Integrate(Sin(x),x)\",\n" //
    // + " \"latex\" : \" - \\\\cos (x)\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testPolynomialQuotientRemainder() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult(" x**2-4,x-2", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 5,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{-4+x^2,-2+x}\",\n" //
    // + " \"sinput\" : \"{-4 + x^2,-2 + x}\",\n" //
    // + " \"latex\" : \"\\\\{-4+{x}^{2},-2+x\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Polynomial quotient and remainder\",\n" //
    // + " \"scanner\" : \"Polynomial\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{2+x,0}\",\n" //
    // + " \"sinput\" : \"PolynomialQuotientRemainder(-4 + x^2,-2 + x,x)\",\n" //
    // + " \"latex\" : \"\\\\{2+x,0\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot({-4 + x^2,-2 + x},{x,-7.0`,7.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,48.25,8.25,-12.25]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [add(-4,pow(x,2))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x)
    // { try { return [add(-2,x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0,
    // 7.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,48.25,8.25,-12.25]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [add(-4,pow(x,2))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x)
    // { try { return [add(-2,x)];} catch(e) { return Number.NaN;}
    // }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0,
    // 7.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Derivative\",\n" //
    // + " \"scanner\" : \"Derivative\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{2*x,1}\",\n" //
    // + " \"sinput\" : \"D({-4 + x^2,-2 + x},x)\",\n" //
    // + " \"latex\" : \"\\\\{2\\\\cdot x,1\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Indefinite integral\",\n" //
    // + " \"scanner\" : \"Integral\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{-4*x+x^3/3,-2*x+x^2/2}\",\n" //
    // + " \"sinput\" : \"Integrate({-4 + x^2,-2 + x},x)\",\n" //
    // + " \"latex\" : \"\\\\{\\\\left( -4\\\\right) \\\\cdot x+\\\\frac{{x}^{3}}{3},\\\\left(
    // -2\\\\right) \\\\cdot x+\\\\frac{{x}^{2}}{2}\\\\}\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Ignore
  public void testSinXY() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin(x*y)", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Sin(x*y)\",\n"
            + "        \"sinput\" : \"Sin(x*y)\",\n" + "        \"latex\" : \"\\\\sin (x\\\\,y)\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"3D plot\",\n"
            + "      \"scanner\" : \"Plot\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"sinput\" : \"Plot3D(Sin(x*y),{x,-3.5`,3.5`},{y,-3.5`,3.5`})\",\n"
            + "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.2/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div class=&quot;mathcell&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.2/build/mathcell.js,https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Alternate form\",\n"
            + "      \"scanner\" : \"Simplification\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"(I*1/2)/E^(I*x*y)-I*1/2*E^(I*x*y)\",\n"
            + "        \"sinput\" : \"TrigToExp(Sin(x*y))\",\n"
            + "        \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\cdot x\\\\cdot y}} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i \\\\cdot x\\\\cdot y}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testColor001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));

    // ObjectNode messageJSON = Pods.createResult("TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))",
    // formatsTEX);
    ObjectNode messageJSON = TestPods.createJUnitResult( //
        "Yellow", //
        formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"numpods\" : 2,\n" + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Yellow\",\n"
            + "        \"sinput\" : \"Yellow\",\n" + "        \"latex\" : \"Yellow\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Result\",\n"
            + "      \"scanner\" : \"Identity\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"RGBColor(1.0,1.0,0.0)\",\n"
            + "        \"sinput\" : \"Yellow\",\n"
            + "        \"latex\" : \"\\\\text{RGBColor}(1.0,1.0,0.0)\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testComplexPlot3D() {

    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult(
        "ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})",
        formatsMATHML);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 2,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"ComplexPlot3D((1+z^2)/(-1+z^2),{z,-2+(-2)*I,2+I*2},PlotRange-&gt;{0,3})\",\n" //
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mi>ComplexPlot3D</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mfrac><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>+</mo><mn>1</mn></mrow><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>-</mo><mn>1</mn></mrow></mfrac><mo>,</mo><mrow><mo>{</mo><mrow><mi>z</mi><mo>,</mo><mrow><mrow><mrow><mo>(</mo><mn>-2</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mo>-</mo><mn>2</mn></mrow><mo>,</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow><mo>&#0183;</mo><mn>2</mn></mrow><mo>+</mo><mn>2</mn></mrow></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mi>PlotRange</mi><mo>-&gt;</mo><mrow><mo>{</mo><mrow><mn>0</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></mrow></mrow><mo>)</mo></mrow></mrow></math>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Function\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"ComplexPlot3D((1 + z^2)/(-1 + z^2),{z,-2 + (-2)*I,2 + I*2},PlotRange-&gt;{0,3})\",\n" //
            + "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.2/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; padding: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { try { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2))));}catch(e){return complex(Number.NaN);} }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', aspectRatio: [1.0,1,1], zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div class=&quot;mathcell&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { try { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2))));}catch(e){return complex(Number.NaN);} }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', aspectRatio: [1.0,1,1], zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.2/build/mathcell.js,https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testHistogram() {

    ObjectNode messageJSON = TestPods.createJUnitResult(
        "Histogram(RandomVariate(NormalDistribution(0, 1), 200))", formatsMATHML);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertFalse(queryResult.isError());
    assertTrue(queryResult.containsPodsScanner(new String[] {"Plotter"}));

  }

  @Ignore
  @Test
  public void testList() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("1,2,3", formatsMATHML);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertEquals(queryResult.isError(), false);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"error\" : \"false\",\n" //
            + "    \"numpods\" : 5,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{1,2,3}\",\n" //
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></math>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Total\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"6\",\n" //
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>6</mn></math>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Vector length\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"3.74166\",\n" //
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>3.74166</mn></math>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Normalized vector\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)}\",\n" //
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mfrac><mn>1</mn><msqrt><mn>14</mn></msqrt></mfrac><mo>,</mo><msqrt><mrow><mfrac><mn>2</mn><mn>7</mn></mfrac></mrow></msqrt><mo>,</mo><mfrac><mn>3</mn><msqrt><mn>14</mn></msqrt></mfrac></mrow><mo>}</mo></mrow></math>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Plot points\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"ListPlot({1.0,2.0,3.0})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.75,3.65,4.75,0.35]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.75,3.65,4.75,0.35]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Ignore
  public void testMatrix001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1, 17 + 4*I\n17 - 4*I, 10", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertEquals(queryResult.isError(), false);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{{1,17+I*4},{17+(-4)*I,10}}\",\n"
            + "        \"sinput\" : \"{{1,17 + I*4},{17 + (-4)*I,10}}\",\n"
            + "        \"latex\" : \"\\\\{\\\\{1,17 + i \\\\cdot 4\\\\},\\\\{17 - 4\\\\cdot i ,10\\\\}\\\\}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Properties\",\n"
            + "      \"scanner\" : \"Matrix\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"The matrix is hermitian (self-adjoint).\",\n"
            + "        \"sinput\" : \"HermitianMatrixQ({{1,17 + I*4}, {17 - I*4,10}})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Result\",\n"
            + "      \"scanner\" : \"Identity\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{{1,17+I*4},\\n {17-I*4,10}}\",\n"
            + "        \"sinput\" : \"{{1,17 + I*4},{17 + (-4)*I,10}}\",\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n1 & 17 + 4\\\\,i  \\\\\\\\\\n17 - 4\\\\,i  & 10 \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  @Test
  public void testMatrix002() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON = TestPods.createJUnitResult("1,3\n3,4", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"error\" : \"false\",\n" //
            + "    \"numpods\" : 5,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{{1,3},{3,4}}\",\n" //
            + "        \"sinput\" : \"{{1,3},{3,4}}\",\n" //
            + "        \"latex\" : \"\\\\{\\\\{1,3\\\\},\\\\{3,4\\\\}\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Properties\",\n" //
            + "      \"scanner\" : \"Matrix\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"The matrix is symmetric.\",\n" //
            + "        \"sinput\" : \"SymmetricMatrixQ({{1,3}, {3,4}})\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Properties\",\n" //
            + "      \"scanner\" : \"Matrix\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"The determinant of the matrix is -5\",\n" //
            + "        \"sinput\" : \"Det({{1,3}, {3,4}})\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Inverse of matrix\",\n" //
            + "      \"scanner\" : \"Matrix\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{{-4/5,3/5},\\n {3/5,-1/5}}\",\n" //
            + "        \"sinput\" : \"Inverse({{1,3}, {3,4}})\",\n" //
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n\\\\frac{-4}{5} & \\\\frac{3}{5} \\\\\\\\\\n\\\\frac{3}{5} & \\\\frac{-1}{5} \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Plot points\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"ListPlot({{1.0,3.0},\\n {3.0,4.0}})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[0.35,4.6,3.65,2.4]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1.0;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3.0;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[0.35,4.6,3.65,2.4]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1.0;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3.0;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testSolve001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("3+x=10", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 4,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"3+x==10\",\n" //
    // + " \"sinput\" : \"3 + x==10\",\n" //
    // + " \"latex\" : \"3+x == 10\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot({x,7},{x,-20.0`,20.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-22.55,22.54999999999999,22.55,-22.55]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return
    // [7];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -20.0,
    // 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -20.0,
    // 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-22.55,22.54999999999999,22.55,-22.55]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return
    // [7];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -20.0,
    // 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -20.0,
    // 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Alternate form\",\n" //
    // + " \"scanner\" : \"Simplification\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"-7+x==0\",\n" //
    // + " \"sinput\" : \"-7 + x==0\",\n" //
    // + " \"latex\" : \"-7+x == 0\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Solution\",\n" //
    // + " \"scanner\" : \"Reduce\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{{x-&gt;7}}\",\n" //
    // + " \"sinput\" : \"Solve(x==7,{x})\",\n" //
    // + " \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testSolve002() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("x^2+1=0", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 4,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"1+x^2==0\",\n" //
    // + " \"sinput\" : \"1 + x^2==0\",\n" //
    // + " \"latex\" : \"1+{x}^{2} == 0\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot({x^2,-1},{x,-20.0`,20.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-22.55,407.8972000000001,22.55,-39.25720000000001]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f3(x) { try { return [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try {
    // return [-1];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f3, -20.0,
    // 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f4, -20.0,
    // 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-22.55,407.8972000000001,22.55,-39.25720000000001]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f3(x) { try { return [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try {
    // return [-1];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f3, -20.0,
    // 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f4, -20.0,
    // 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Alternate form\",\n" //
    // + " \"scanner\" : \"Simplification\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"1+x^2==0\",\n" //
    // + " \"sinput\" : \"1 + x^2==0\",\n" //
    // + " \"latex\" : \"1+{x}^{2} == 0\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Solution\",\n" //
    // + " \"scanner\" : \"Reduce\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{{x-&gt;-I},{x-&gt;I}}\",\n" //
    // + " \"sinput\" : \"Solve(x^2==-1,{x})\",\n" //
    // + " \"latex\" : \"\\\\{\\\\{x\\\\to - i \\\\},\\\\{x\\\\to i \\\\}\\\\}\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Ignore
  public void testSolve003() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Solver x+3=10", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Solve(3+x==10)\",\n"
            + "        \"sinput\" : \"Solve(3 + x==10)\",\n"
            + "        \"latex\" : \"\\\\text{Solve}(3+x == 10)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Solve equation\",\n" + "      \"scanner\" : \"Solver\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"{{x-&gt;7}}\",\n"
            + "        \"sinput\" : \"Solve(3 + x==10,{x})\",\n"
            + "        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  public void testInteger4294967295() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("2**32-1", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 7,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"-1+2^32\",\n"
            + "        \"sinput\" : \"-1 + 2^32\",\n" + "        \"latex\" : \"-1+{2}^{32}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Result\",\n"
            + "      \"scanner\" : \"Simplification\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"4294967295\",\n" + "        \"sinput\" : \"-1 + 2^32\",\n"
            + "        \"latex\" : \"4294967295\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Number name\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five\",\n"
            + "        \"sinput\" : \"IntegerName(4294967295,\\\"Words\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Binary form\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"11111111111111111111111111111111_2\",\n"
            + "        \"sinput\" : \"BaseForm(4294967295,2)\",\n"
            + "        \"latex\" : \"{\\\\textnormal{11111111111111111111111111111111}}_{2}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Prime factorization\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"3*5*17*257*65537\",\n"
            + "        \"sinput\" : \"FactorInteger(4294967295)\",\n"
            + "        \"latex\" : \"3\\\\cdot 5\\\\cdot 17\\\\cdot 257\\\\cdot 65537\"\n"
            + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Residues modulo small integers\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;4294967295 mod m&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;7&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n"
            + "        \"sinput\" : \"Mod(4294967295,{2,3,4,5,6,7,8,9})\",\n"
            + "        \"latex\" : \"\\\\{1,0,3,0,3,3,7,3\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Properties\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"4294967295 is an odd number.\",\n"
            + "        \"sinput\" : \"OddQ(4294967295)\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Ignore
  public void testNormalDistribution() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("NormalDistribution(a,b)", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 4,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"NormalDistribution(a,b)\",\n"
            + "        \"sinput\" : \"NormalDistribution(a,b)\",\n"
            + "        \"latex\" : \"\\\\text{NormalDistribution}(a,b)\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Statistical properties\",\n"
            + "      \"scanner\" : \"Statistics\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;mean&lt;/td&gt;&lt;td&gt;a&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;standard deviation&lt;/td&gt;&lt;td&gt;b&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;variance&lt;/td&gt;&lt;td&gt;b^2&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;skewness&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n"
            + "        \"sinput\" : \"{}\",\n"
            + "        \"latex\" : \"\\\\{\\\\text{Mean}(\\\\text{NormalDistribution}(a,b)),\\\\text{StandardDeviation}(\\\\text{NormalDistribution}(a,b)),\\\\text{Variance}(\\\\text{NormalDistribution}(a,b)),\\\\text{Skewness}(\\\\text{NormalDistribution}(a,b))\\\\}\"\n"
            + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Probability density function (PDF)\",\n"
            + "      \"scanner\" : \"Statistics\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"1/(b*E^((-a+x)^2/(2*b^2))*Sqrt(2*Pi))\",\n"
            + "        \"sinput\" : \"PDF(NormalDistribution(a,b),x)\",\n"
            + "        \"latex\" : \"\\\\frac{1}{b\\\\,{e}^{\\\\frac{{\\\\left(  - a+x\\\\right) }^{2}}{2\\\\cdot {b}^{2}}}\\\\,\\\\sqrt{\\\\left( 2\\\\cdot \\\\pi\\\\right) }}\"\n"
            + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Cumulative distribution function (CDF)\",\n"
            + "      \"scanner\" : \"Statistics\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Erfc((a-x)/(Sqrt(2)*b))/2\",\n"
            + "        \"sinput\" : \"CDF(NormalDistribution(a,b),x)\",\n"
            + "        \"latex\" : \"\\\\frac{\\\\text{erfc}(\\\\frac{a - x}{\\\\sqrt{2}\\\\,b})}{2}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testNumber001() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("Sqrt(2)", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 4,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Sqrt(2)\",\n" //
            + "        \"sinput\" : \"Sqrt(2)\",\n" //
            + "        \"latex\" : \"\\\\sqrt{2}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Decimal form\",\n" //
            + "      \"scanner\" : \"Numeric\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"1.41421\",\n" //
            + "        \"sinput\" : \"N(Sqrt(2))\",\n" //
            + "        \"latex\" : \"1.41421\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Rational form\",\n" //
            + "      \"scanner\" : \"Numeric\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Rationalize(1.41421)\",\n" //
            + "        \"sinput\" : \"Rationalize(1.4142135623730951`)\",\n" //
            + "        \"latex\" : \"\\\\text{Rationalize}(1.41421)\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Continued fraction\",\n" //
            + "      \"scanner\" : \"Numeric\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{1,{2}}\",\n" //
            + "        \"sinput\" : \"ContinuedFraction(Sqrt(2))\",\n" //
            + "        \"latex\" : \"\\\\{1,\\\\{2\\\\}\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}");
  }

  @Test
  public void testNumber002() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("0.33", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
    // assertEquals(jsonStr, //
    // "");
  }

  @Test
  public void testLogic001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("a&&b||c", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 3,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\n" //
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\n" //
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Truth table\",\n" //
            + "      \"scanner\" : \"Boolean\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;th&gt;(a&amp;&amp;b)||c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n" //
            + "        \"sinput\" : \"BooleanTable(Append({a,b,c},(a&amp;&amp;b)||c),{a,b,c})\",\n" //
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cccc}\\nTrue & True & True & True \\\\\\\\\\nTrue & True & False & True \\\\\\\\\\nTrue & False & True & True \\\\\\\\\\nTrue & False & False & False \\\\\\\\\\nFalse & True & True & True \\\\\\\\\\nFalse & True & False & False \\\\\\\\\\nFalse & False & True & True \\\\\\\\\\nFalse & False & False & False \\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Satisfiability instance\",\n" //
            + "      \"scanner\" : \"Boolean\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n" //
            + "        \"sinput\" : \"SatisfiabilityInstances((a&amp;&amp;b)||c,{a,b,c},1)\",\n" //
            + "        \"latex\" : \"\\\\{\\\\{False,False,True\\\\}\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testLogic002() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("a&b|c", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 3,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\n" //
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\n" //
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Truth table\",\n" //
            + "      \"scanner\" : \"Boolean\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;th&gt;(a&amp;&amp;b)||c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n" //
            + "        \"sinput\" : \"BooleanTable(Append({a,b,c},(a&amp;&amp;b)||c),{a,b,c})\",\n" //
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cccc}\\nTrue & True & True & True \\\\\\\\\\nTrue & True & False & True \\\\\\\\\\nTrue & False & True & True \\\\\\\\\\nTrue & False & False & False \\\\\\\\\\nFalse & True & True & True \\\\\\\\\\nFalse & True & False & False \\\\\\\\\\nFalse & False & True & True \\\\\\\\\\nFalse & False & False & False \\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Satisfiability instance\",\n" //
            + "      \"scanner\" : \"Boolean\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n" //
            + "        \"sinput\" : \"SatisfiabilityInstances((a&amp;&amp;b)||c,{a,b,c},1)\",\n" //
            + "        \"latex\" : \"\\\\{\\\\{False,False,True\\\\}\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testQuantity() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("convert 111 cm in m", formatsHTML);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 2,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"111*cm*convert*In*m\",\n" //
            + "        \"sinput\" : \"111*cm*convert*In*m\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Evaluated result\",\n" //
            + "      \"scanner\" : \"Expression\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"111*cm*convert*In*m\",\n" //
            + "        \"sinput\" : \"111*cm*convert*In*m\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Ignore
  public void testTimes() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("10*11*12", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 8,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"10*11*12\",\n"
            + "        \"sinput\" : \"10*11*12\",\n"
            + "        \"latex\" : \"10\\\\cdot 11\\\\cdot 12\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Result\",\n" + "      \"scanner\" : \"Simplification\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1320\",\n"
            + "        \"sinput\" : \"10*11*12\",\n" + "        \"latex\" : \"1320\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Number name\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"one thousand three hundred twenty\",\n"
            + "        \"sinput\" : \"IntegerName(1320,\\\"Words\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{one thousand three hundred twenty}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Roman numerals\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"MCCCXX\",\n"
            + "        \"sinput\" : \"RomanNumeral(1320)\",\n"
            + "        \"latex\" : \"\\\\textnormal{MCCCXX}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Binary form\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"10100101000_2\",\n"
            + "        \"sinput\" : \"BaseForm(1320,2)\",\n"
            + "        \"latex\" : \"{\\\\textnormal{10100101000}}_{2}\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Prime factorization\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"2^3*3*5*11\",\n"
            + "        \"sinput\" : \"FactorInteger(1320)\",\n"
            + "        \"latex\" : \"{2}^{3}\\\\cdot 3\\\\cdot 5\\\\cdot 11\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Residues modulo small integers\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;1320 mod m&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;4&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;6&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n"
            + "        \"sinput\" : \"Mod(1320,{2,3,4,5,6,7,8,9})\",\n"
            + "        \"latex\" : \"\\\\{0,0,0,0,0,4,0,6\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Properties\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1320 is an even number.\",\n"
            + "        \"sinput\" : \"EvenQ(1320)\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testTransliterate() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Transliterate(\"Càfé\")", formatsTEX);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"numpods\" : 3,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Transliterate(Càfé)\",\n" //
            + "        \"sinput\" : \"Transliterate(\\\"Càfé\\\")\",\n" //
            + "        \"latex\" : \"\\\\text{Transliterate}(\\\\textnormal{Càfé})\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"String form\",\n" //
            + "      \"scanner\" : \"String\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Cafe\",\n" //
            + "        \"sinput\" : \"Transliterate(\\\"Càfé\\\")\",\n" //
            + "        \"latex\" : \"\\\\textnormal{Cafe}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Documentation\",\n" //
            + "      \"scanner\" : \"help\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"markdown\" : \"## Chop\\n\\n```\\nChop(numerical-expr)\\n```\\n\\n> replaces numerical values in the `numerical-expr` which are close to zero with symbolic value `0`. \\n\\n```\\nChop(numerical-expr, delta)\\n```\\n\\n> uses a tolerance of `delta`. The default tolerance is `10^-10`.\\n      \\n### Examples\\n\\n```\\n>> Chop(0.00000000001)\\n0\\n```\\n\\n\\n### Implementation status\\n\\n* &#x2705; - full supported\\n\\n### Github\\n\\n* [Implementation of Chop](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L706) \\n[Github master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L699)\\n\\n\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Ignore
  public void testSimplify() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON =
        TestPods.createJUnitResult("simplificate Sqrt(9-4*Sqrt(5))", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\n"
            + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\n"
            + "        \"latex\" : \"\\\\text{FullSimplify}(\\\\sqrt{\\\\left( 9 - 4\\\\cdot \\\\sqrt{5}\\\\right) })\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Decimal form\",\n"
            + "      \"scanner\" : \"Numeric\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"0.236068\",\n"
            + "        \"sinput\" : \"N(FullSimplify(Sqrt(9 - 4*Sqrt(5))))\",\n"
            + "        \"latex\" : \"0.236068\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Result\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"-2+Sqrt(5)\",\n"
            + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\n"
            + "        \"latex\" : \"-2+\\\\sqrt{5}\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Ignore
  public void testIntegrate001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Integral Sin(x)", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Integrate(Sin(x))\",\n"
            + "        \"sinput\" : \"Integrate(Sin(x))\",\n"
            + "        \"latex\" : \"\\\\text{Integrate}(\\\\sin (x))\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Integration\",\n"
            + "      \"scanner\" : \"Integral\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-Cos(x)\",\n"
            + "        \"sinput\" : \"Integrate(Sin(x),x)\",\n"
            + "        \"latex\" : \" - \\\\cos (x)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Alternate form\",\n"
            + "      \"scanner\" : \"Simplification\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-1/(2*E^(I*x))-E^(I*x)/2\",\n"
            + "        \"sinput\" : \"TrigToExp(-Cos(x))\",\n"
            + "        \"latex\" : \"\\\\frac{-1}{2\\\\cdot {e}^{i \\\\cdot x}}-\\\\frac{{e}^{i \\\\cdot x}}{2}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  public void testIntegrate002() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("integrate Tan(x)*Cos(x)*Pi x", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"latex\" : \"\\\\int  \\\\pi\\\\,\\\\cos (x)\\\\,\\\\tan (x)\\\\,\\\\mathrm{d}x\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Integration\",\n"
            + "      \"scanner\" : \"Integral\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-Pi*Cos(x)\",\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"latex\" : \" - \\\\pi\\\\,\\\\cos (x)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Alternate form\",\n"
            + "      \"scanner\" : \"Simplification\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-(1/(2*E^(I*x))+E^(I*x)/2)*Pi\",\n"
            + "        \"sinput\" : \"TrigToExp(-Pi*Cos(x))\",\n"
            + "        \"latex\" : \" - \\\\frac{1}{2\\\\cdot {e}^{i \\\\cdot x}}+\\\\frac{{e}^{i \\\\cdot x}}{2}\\\\,\\\\pi\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  public void testDerivative001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("derive tan(x^3)", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"D(Tan(x^3))\",\n"
            + "        \"sinput\" : \"D(Tan(x^3))\",\n"
            + "        \"latex\" : \"D(\\\\tan ({x}^{3}))\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Derivative\",\n" + "      \"scanner\" : \"Derivative\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"3*x^2*Sec(x^3)^2\",\n"
            + "        \"sinput\" : \"D(Tan(x^3),x)\",\n"
            + "        \"latex\" : \"3\\\\cdot {x}^{2}\\\\cdot {\\\\sec ({x}^{3})}^{2}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Alternate form\",\n"
            + "      \"scanner\" : \"Simplification\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"(12*x^2)/(E^(-I*x^3)+E^(I*x^3))^2\",\n"
            + "        \"sinput\" : \"TrigToExp(3*x^2*Sec(x^3)^2)\",\n"
            + "        \"latex\" : \"\\\\frac{12\\\\cdot {x}^{2}}{{\\\\left( {e}^{\\\\left(  - i \\\\right) \\\\cdot {x}^{3}}+{e}^{i \\\\cdot {x}^{3}}\\\\right) }^{2}}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  @Test
  public void testListPlot001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("3,Sin(1),Pi,3/4,42,1.2", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"error\" : \"false\",\n" //
            + "    \"numpods\" : 8,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{3,Sin(1),Pi,3/4,42,1.2}\",\n" //
            + "        \"sinput\" : \"{3,Sin(1),Pi,3/4,42,1.2`}\",\n" //
            + "        \"latex\" : \"\\\\{3,\\\\sin (1),\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Total\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"50.93306\",\n" //
            + "        \"sinput\" : \"Total({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\n" //
            + "        \"latex\" : \"50.93306\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Vector length\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"42.25613\",\n" //
            + "        \"sinput\" : \"Norm({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\n" //
            + "        \"latex\" : \"42.25613\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Normalized vector\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{0.0709956233478567,0.01991358569852382,0.07434644291555152,0.017748905836964174,0.9939387268699937,0.028398249339142676}\",\n" //
            + "        \"sinput\" : \"Normalize({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\n" //
            + "        \"latex\" : \"\\\\{0.0709956,0.0199136,0.0743464,0.0177489,0.993939,0.0283982\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Plot points\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"ListPlot({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9,44.6125,7.9,-1.8624999999999998]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 0.8414709848078965;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.141592653589793;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 0.75;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 42.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 6;},function() {return 1.2;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9,44.6125,7.9,-1.8624999999999998]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 0.8414709848078965;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.141592653589793;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 0.75;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 42.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 6;},function() {return 1.2;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Five-number summary\",\n" //
            + "      \"scanner\" : \"Statistics\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{0.75,0.8414709848078965,2.1,3.141592653589793,42.0}\",\n" //
            + "        \"sinput\" : \"FiveNum({3,Sin(1),Pi,3/4,42,1.2`})\",\n" //
            + "        \"latex\" : \"\\\\{0.75,0.841471,2.1,3.14159,42.0\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Histogram\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"Histogram({3,Sin(1),Pi,3/4,42,1.2`})\",\n" //
            + "        \"plotly\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;Plotly&lt;/title&gt;\\n&lt;script src=&quot;https://cdn.plot.ly/plotly-latest.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;/head&gt;\\n&lt;body&gt;\\n&lt;div id='plotly' style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n        var target_plotly = document.getElementById('plotly');\\n        var layout = {\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n};\\n\\nvar trace0 =\\n{\\nx: [&quot;3.0&quot;,&quot;0.8414709848078965&quot;,&quot;3.141592653589793&quot;,&quot;0.75&quot;,&quot;42.0&quot;,&quot;1.2&quot;],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target_plotly, data, layout);        \\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id='plotly' style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;        var target_plotly = document.getElementById('plotly');\\n        var layout = {\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n};\\n\\nvar trace0 =\\n{\\nx: [&quot;3.0&quot;,&quot;0.8414709848078965&quot;,&quot;3.141592653589793&quot;,&quot;0.75&quot;,&quot;42.0&quot;,&quot;1.2&quot;],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target_plotly, data, layout);        &lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.plot.ly/plotly-latest.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Alternate form\",\n" //
            + "      \"scanner\" : \"Simplification\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{3,(I*1/2)/E^I-I*1/2*E^I,Pi,3/4,42,1.2}\",\n" //
            + "        \"sinput\" : \"TrigToExp({3,Sin(1),Pi,3/4,42,1.2`})\",\n" //
            + "        \"latex\" : \"\\\\{3,\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i }} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i },\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Ignore
  public void testListPlot004() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON =
        TestPods.createJUnitResult("1, 2, 3, None, 3, 5, f(), 2, 1, foo, 2, 3", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{1,2,3,None,3,5,f(),2,1,foo,2,3}\",\n"
            + "        \"sinput\" : \"{1,2,3,None,3,5,f(),2,1,foo,2,3}\",\n"
            + "        \"latex\" : \"\\\\{1,2,3,None,3,5,f(),2,1,foo,2,3\\\\}\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Five-number summary\",\n"
            + "      \"scanner\" : \"Statistics\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{Min(1,foo,None,f()),5/2,5/2,1/2*(2-f())+f(),Max(5,foo,None,f())}\",\n"
            + "        \"sinput\" : \"FiveNum({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\n"
            + "        \"latex\" : \"\\\\{\\\\text{Min}(1,foo,None,f()),\\\\frac{5}{2},\\\\frac{5}{2},\\\\frac{2 - f()}{2}+f(),\\\\text{Max}(5,foo,None,f())\\\\}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Histogram\",\n"
            + "      \"scanner\" : \"Plotter\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"sinput\" : \"Histogram({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\n"
            + "        \"plotly\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;Plotly&lt;/title&gt;\\n&lt;script src=&quot;https://cdn.plot.ly/plotly-latest.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;/head&gt;\\n&lt;body&gt;\\n&lt;div id='plotly' style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n        var target_plotly = document.getElementById('plotly');\\n        var layout = {\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n};\\n\\nvar trace0 =\\n{\\nx: [&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;,&quot;3.0&quot;,&quot;5.0&quot;,&quot;2.0&quot;,&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target_plotly, data, layout);        \\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id='plotly' style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;        var target_plotly = document.getElementById('plotly');\\n        var layout = {\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n};\\n\\nvar trace0 =\\n{\\nx: [&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;,&quot;3.0&quot;,&quot;5.0&quot;,&quot;2.0&quot;,&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target_plotly, data, layout);        &lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.plot.ly/plotly-latest.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Ignore
  @Test
  public void testListPlot005() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("1, 2, 3, 4, 5", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"error\" : \"false\",\n" //
            + "    \"numpods\" : 5,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{1,2,3,4,5}\",\n" //
            + "        \"sinput\" : \"{1,2,3,4,5}\",\n" //
            + "        \"latex\" : \"\\\\{1,2,3,4,5\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Total\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"15\",\n" //
            + "        \"sinput\" : \"Total({1,2,3,4,5})\",\n" //
            + "        \"latex\" : \"15\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Vector length\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"7.4162\",\n" //
            + "        \"sinput\" : \"N(Norm({1,2,3,4,5}))\",\n" //
            + "        \"latex\" : \"7.4162\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Normalized vector\",\n" //
            + "      \"scanner\" : \"List\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{1/Sqrt(55),2/Sqrt(55),3/Sqrt(55),4/Sqrt(55),Sqrt(5/11)}\",\n" //
            + "        \"sinput\" : \"Normalize({1,2,3,4,5})\",\n" //
            + "        \"latex\" : \"\\\\{\\\\frac{1}{\\\\sqrt{55}},\\\\frac{2}{\\\\sqrt{55}},\\\\frac{3}{\\\\sqrt{55}},\\\\frac{4}{\\\\sqrt{55}},\\\\sqrt{\\\\frac{5}{11}}\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Plot points\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"ListPlot({1.0,2.0,3.0,4.0,5.0})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.85,5.75,6.85,0.25]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 5.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.85,5.75,6.85,0.25]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 5.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testListPlot003() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("plot ([x,x**2,x**3,x**4])", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 5,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"Plot({x,x^2,x^3,x^4})\",\n" //
    // + " \"sinput\" : \"Plot({x,x^2,x^3,x^4})\",\n" //
    // + " \"latex\" : \"\\\\text{Plot}(\\\\{x,{x}^{2},{x}^{3},{x}^{4}\\\\})\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Result\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{x,x^2,x^3,x^4}\",\n" //
    // + " \"sinput\" : \"Plot({x,x^2,x^3,x^4})\",\n" //
    // + " \"latex\" : \"\\\\{x,{x}^{2},{x}^{3},{x}^{4}\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot({x,x^2,x^3,x^4},{x,-7.0`,7.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,1911.6055445184015,8.25,-399.3420551104002]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return
    // [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [pow(x,3)];}
    // catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return [pow(x,4)];} catch(e) {
    // return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0,
    // 7.0],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, -7.0,
    // 7.0],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, -7.0,
    // 7.0],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,1911.6055445184015,8.25,-399.3420551104002]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return
    // [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [pow(x,3)];}
    // catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return [pow(x,4)];} catch(e) {
    // return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0,
    // 7.0],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, -7.0,
    // 7.0],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, -7.0,
    // 7.0],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Derivative\",\n" //
    // + " \"scanner\" : \"Derivative\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{1,2*x,3*x^2,4*x^3}\",\n" //
    // + " \"sinput\" : \"D({x,x^2,x^3,x^4},x)\",\n" //
    // + " \"latex\" : \"\\\\{1,2\\\\cdot x,3\\\\cdot {x}^{2},4\\\\cdot {x}^{3}\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Indefinite integral\",\n" //
    // + " \"scanner\" : \"Integral\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"{x^2/2,x^3/3,x^4/4,x^5/5}\",\n" //
    // + " \"sinput\" : \"Integrate({x,x^2,x^3,x^4},x)\",\n" //
    // + " \"latex\" :
    // \"\\\\{\\\\frac{{x}^{2}}{2},\\\\frac{{x}^{3}}{3},\\\\frac{{x}^{4}}{4},\\\\frac{{x}^{5}}{5}\\\\}\"\n"
    // //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testGraph001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    // ObjectNode messageJSON = Pods.createResult("TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))",
    // formatsTEX);
    ObjectNode messageJSON = TestPods.createJUnitResult( //
        "Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)})", //
        formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"numpods\" : 2,\n" + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Graph({1-&gt;2,2-&gt;3,3-&gt;1,3-&gt;4,4-&gt;5,5-&gt;3})\",\n"
            + "        \"sinput\" : \"Graph({1-&gt;2,2-&gt;3,3-&gt;1,3-&gt;4,4-&gt;5,5-&gt;3})\",\n"
            + "        \"latex\" : \"\\\\text{Graph}(\\\\{1\\\\to 2,2\\\\to 3,3\\\\to 1,3\\\\to 4,4\\\\to 5,5\\\\to 3\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Graph data\",\n"
            + "      \"scanner\" : \"Graph\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"visjs\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;VIS-NetWork&lt;/title&gt;\\n\\n  &lt;script type=&quot;text/javascript&quot; src=&quot;https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n\\n&lt;div id=&quot;vis&quot; style=&quot;width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script type=&quot;text/javascript&quot;&gt;\\n&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;VIS-NetWork&lt;/title&gt;\\n\\n  &lt;script type=&quot;text/javascript&quot; src=&quot;https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n\\n&lt;div id=&quot;vis&quot; style=&quot;width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script type=&quot;text/javascript&quot;&gt;\\nvar nodes = new vis.DataSet([\\n  {id: 1, label: '1'}\\n, {id: 2, label: '2'}\\n, {id: 3, label: '3'}\\n, {id: 4, label: '4'}\\n, {id: 5, label: '5'}\\n]);\\nvar edges = new vis.DataSet([\\n  {from: 1, to: 2 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 2, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 3, to: 1 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 3, to: 4 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 4, to: 5 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 5, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n]);\\n\\n  var container = document.getElementById('vis');\\n  var data = {\\n    nodes: nodes,\\n    edges: edges\\n  };\\n  var options = { };\\n\\n  var network = new vis.Network(container, data, options);\\n&lt;/script&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\n  var container = document.getElementById('vis');\\n  var data = {\\n    nodes: nodes,\\n    edges: edges\\n  };\\n  var options = {\\n\\t\\t  edges: {\\n              smooth: {\\n                  type: 'cubicBezier',\\n                  forceDirection:  'vertical',\\n                  roundness: 0.4\\n              }\\n          },\\n          layout: {\\n              hierarchical: {\\n                  direction: &quot;UD&quot;\\n              }\\n          },\\n          nodes: {\\n            shape: 'box'\\n          },\\n          physics:false\\n      }; \\n  var network = new vis.Network(container, data, options);\\n&lt;/script&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  //
  @Test
  public void testHornerForm() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    String s = System.getProperty("os.name");
    ObjectNode messageJSON = TestPods.createJUnitResult("HornerForm(x^2+x^3+2*x^14)", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 7,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"HornerForm(x^2+x^3+2*x^14)\",\n" //
    // + " \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\n" //
    // + " \"latex\" : \"\\\\text{HornerForm}({x}^{2}+{x}^{3} + 2\\\\cdot {x}^{14})\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Result\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"x^2*(1+x*(1+2*x^11))\",\n" //
    // + " \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\n" //
    // + " \"latex\" : \"{x}^{2}\\\\,\\\\left( 1 + x\\\\,\\\\left( 1 + 2\\\\cdot {x}^{11}\\\\right)
    // \\\\right) \"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot(x^2*(1 + x*(1 + 2*x^11)),{x,-7.0`,7.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,6.303108521964081E11,8.25,-5.989379138567678E10]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [mul(pow(x,2),add(1,mul(x,add(1,mul(2,pow(x,11))))))];} catch(e) {
    // return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,6.303108521964081E11,8.25,-5.989379138567678E10]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [mul(pow(x,2),add(1,mul(x,add(1,mul(2,pow(x,11))))))];} catch(e) {
    // return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Factor\",\n" //
    // + " \"scanner\" : \"Polynomial\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"x^2*(1+x+2*x^12)\",\n" //
    // + " \"sinput\" : \"Factor(x^2*(1 + x*(1 + 2*x^11)))\",\n" //
    // + " \"latex\" : \"{x}^{2}\\\\,\\\\left( 1+x + 2\\\\cdot {x}^{12}\\\\right) \"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"GlobalExtrema\",\n" //
    // + " \"scanner\" : \"GlobalMinimum\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"min{x^2*(1+x*(1+2*x^11))} = 0 at x = 0\",\n" //
    // + " \"sinput\" : \"Minimize(x^2*(1 + x*(1 + 2*x^11)),x)\",\n" //
    // + " \"latex\" : \"\\\\{0,\\\\{x\\\\to 0\\\\}\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Derivative\",\n" //
    // + " \"scanner\" : \"Derivative\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"x^2*(1+24*x^11)+2*x*(1+x*(1+2*x^11))\",\n" //
    // + " \"sinput\" : \"D(x^2*(1 + x*(1 + 2*x^11)),x)\",\n" //
    // + " \"latex\" : \"{x}^{2}\\\\,\\\\left( 1 + 24\\\\cdot {x}^{11}\\\\right) + 2\\\\cdot
    // x\\\\cdot \\\\left( 1 + x\\\\,\\\\left( 1 + 2\\\\cdot {x}^{11}\\\\right) \\\\right) \"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Indefinite integral\",\n" //
    // + " \"scanner\" : \"Integral\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"x^3/3+x^4/4+2/15*x^15\",\n" //
    // + " \"sinput\" : \"Integrate(x^2*(1 + x*(1 + 2*x^11)),x)\",\n" //
    // + " \"latex\" : \"\\\\frac{{x}^{3}}{3}+\\\\frac{{x}^{4}}{4}+\\\\frac{2\\\\cdot
    // {x}^{15}}{15}\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Ignore
  public void testETimesPi001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON = TestPods.createJUnitResult( //
        "E*Pi", //
        formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"E*Pi\",\n"
            + "        \"sinput\" : \"E*Pi\",\n" + "        \"latex\" : \"e\\\\,\\\\pi\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Decimal form\",\n"
            + "      \"scanner\" : \"Numeric\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"8.53973\",\n" + "        \"sinput\" : \"N(E*Pi)\",\n"
            + "        \"latex\" : \"8.53973\"\n" + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testCoshIntegral001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    String s = System.getProperty("os.name");
    ObjectNode messageJSON = TestPods.createJUnitResult( //
        "CoshIntegral", //
        formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 3,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"CoshIntegral\",\n" //
    // + " \"sinput\" : \"CoshIntegral\",\n" //
    // + " \"latex\" : \"CoshIntegral\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Plot\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" :
    // \"Manipulate(Plot({Re(CoshIntegral(a*x)),Im(CoshIntegral(a*x))},{x,-2.0`,2.0`},PlotRange-&gt;{-5.0`,5.0`}),{a,1,10})\",\n"
    // //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-2.75,6.05,2.75,-6.05]});\\nboard.suspendUpdate();\\nvar a =
    // board.create('slider',[[-2.2,4.84],[2.2,4.84],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) {
    // try { return [re(coshIntegral(mul(a.Value(),x)))];} catch(e) { return Number.NaN;}
    // }\\nfunction $f2(x) { try { return [im(coshIntegral(mul(a.Value(),x)))];} catch(e) { return
    // Number.NaN;} }\\nboard.create('functiongraph',[$f1, -2.0,
    // 2.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -2.0,
    // 2.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-2.75,6.05,2.75,-6.05]});\\nboard.suspendUpdate();\\nvar a =
    // board.create('slider',[[-2.2,4.84],[2.2,4.84],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) {
    // try { return [re(coshIntegral(mul(a.Value(),x)))];} catch(e) { return Number.NaN;}
    // }\\nfunction $f2(x) { try { return [im(coshIntegral(mul(a.Value(),x)))];} catch(e) { return
    // Number.NaN;} }\\nboard.create('functiongraph',[$f1, -2.0,
    // 2.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -2.0,
    // 2.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Documentation\",\n" //
    // + " \"scanner\" : \"help\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"markdown\" : \"## CoshIntegral\\n\\n```\\nCoshIntegral(expr)\\n```\\n\\n> returns the
    // hyperbolic cosine integral of `expr`.\\n \\nSee\\n* [Wikipedia - Trigonometric
    // integral](https://en.wikipedia.org/wiki/Trigonometric_integral)\\n\\n###
    // Examples\\n\\n```\\n>> CoshIntegral(0)\\n-Infinity\\n```\\n \\n\\n### Github\\n\\n*
    // [Implementation of
    // CoshIntegral](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L237)
    // \\n[Github
    // master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L239)\\n\\n\"\n"
    // //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }

  @Test
  public void testPolynomial001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("-x^2 + 4*x + 4", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);

    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    // assertEquals(jsonStr, //
    // "{\n" //
    // + " \"queryresult\" : {\n" //
    // + " \"success\" : \"true\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numpods\" : 6,\n" //
    // + " \"version\" : \"0.1\",\n" //
    // + " \"pods\" : [ {\n" //
    // + " \"title\" : \"Input\",\n" //
    // + " \"scanner\" : \"Identity\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"4+4*x-x^2\",\n" //
    // + " \"sinput\" : \"4 + 4*x - x^2\",\n" //
    // + " \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Function\",\n" //
    // + " \"scanner\" : \"Plotter\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"sinput\" : \"Plot(4 + 4*x - x^2,{x,-7.0`,7.0`})\",\n" //
    // + " \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot;
    // encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n &quot;-//W3C//DTD XHTML 1.1
    // plus MathML 2.0 plus SVG 1.1//EN&quot;\\n
    // &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html
    // xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin:
    // 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta
    // charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body
    // style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link
    // rel=&quot;stylesheet&quot; type=&quot;text/css&quot;
    // href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script
    // src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div
    // id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%;
    // height: 100%; margin: 0; flex-direction: column; overflow:
    // hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,12.598320000000001,8.25,-77.59992]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [add(add(4,mul(4,x)),mul(-1,pow(x,2)))];} catch(e) { return
    // Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form
    // method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/'
    // target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit'
    // style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html'
    // style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot;
    // style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea
    // name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox',
    // {axis:true,boundingbox:[-8.25,12.598320000000001,8.25,-77.59992]});\\nboard.suspendUpdate();\\n\\nfunction
    // $f1(x) { try { return [add(add(4,mul(4,x)),mul(-1,pow(x,2)))];} catch(e) { return
    // Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0,
    // 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea
    // name='resources'
    // style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.10/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\"
    // style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Factor\",\n" //
    // + " \"scanner\" : \"Polynomial\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"4+4*x-x^2\",\n" //
    // + " \"sinput\" : \"Factor(4 + 4*x - x^2)\",\n" //
    // + " \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"GlobalExtrema\",\n" //
    // + " \"scanner\" : \"GlobalMaximum\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"max{4+4*x-x^2} = 8 at x = 2\",\n" //
    // + " \"sinput\" : \"Maximize(4 + 4*x - x^2,x)\",\n" //
    // + " \"latex\" : \"\\\\{8,\\\\{x\\\\to 2\\\\}\\\\}\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Derivative\",\n" //
    // + " \"scanner\" : \"Derivative\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"4-2*x\",\n" //
    // + " \"sinput\" : \"D(4 + 4*x - x^2,x)\",\n" //
    // + " \"latex\" : \"4 - 2\\\\cdot x\"\n" //
    // + " } ]\n" //
    // + " }, {\n" //
    // + " \"title\" : \"Indefinite integral\",\n" //
    // + " \"scanner\" : \"Integral\",\n" //
    // + " \"error\" : \"false\",\n" //
    // + " \"numsubpods\" : 1,\n" //
    // + " \"subpods\" : [ {\n" //
    // + " \"plaintext\" : \"4*x+2*x^2-x^3/3\",\n" //
    // + " \"sinput\" : \"Integrate(4 + 4*x - x^2,x)\",\n" //
    // + " \"latex\" : \"4\\\\cdot x + 2\\\\cdot {x}^{2}-\\\\frac{{x}^{3}}{3}\"\n" //
    // + " } ]\n" //
    // + " } ]\n" //
    // + " }\n" //
    // + "}"); //
  }


  // @Test
  // public void testRationalized001() {
  // EvalEngine.resetModuleCounter4JUnit();
  // ObjectNode messageJSON = TestPods.createJUnitResult("1.6*10^-27", formatsTEX);
  // final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);
  //
  // JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
  // assertEquals(queryResult.isError(), false);
  //
  // assertEquals(jsonStr, //
  // "{\n" //
  // + " \"queryresult\" : {\n" //
  // + " \"success\" : \"true\",\n" //
  // + " \"numpods\" : 4,\n" //
  // + " \"version\" : \"0.1\",\n" //
  // + " \"pods\" : [ {\n" //
  // + " \"title\" : \"Input\",\n" //
  // + " \"scanner\" : \"Identity\",\n" //
  // + " \"error\" : \"false\",\n" //
  // + " \"numsubpods\" : 1,\n" //
  // + " \"subpods\" : [ {\n" //
  // + " \"plaintext\" : \"1.6/10^27\",\n" //
  // + " \"sinput\" : \"1.6`/10^27\",\n" //
  // + " \"latex\" : \"\\\\frac{1.6}{{10}^{27}}\"\n" //
  // + " } ]\n" //
  // + " }, {\n" //
  // + " \"title\" : \"Decimal form\",\n" //
  // + " \"scanner\" : \"Numeric\",\n" //
  // + " \"error\" : \"false\",\n" //
  // + " \"numsubpods\" : 1,\n" //
  // + " \"subpods\" : [ {\n" //
  // + " \"plaintext\" : \"1.6*10^-27\",\n" //
  // + " \"sinput\" : \"N(1.6`/10^27)\",\n" //
  // + " \"latex\" : \"0.0\"\n" //
  // + " } ]\n" //
  // + " }, {\n" //
  // + " \"title\" : \"Rational form\",\n" //
  // + " \"scanner\" : \"Numeric\",\n" //
  // + " \"error\" : \"false\",\n" //
  // + " \"numsubpods\" : 1,\n" //
  // + " \"subpods\" : [ {\n" //
  // + " \"plaintext\" : \"0\",\n" //
  // + " \"sinput\" : \"Rationalize(1.6000000000000001`*^-27)\",\n" //
  // + " \"latex\" : \"0\"\n" //
  // + " } ]\n" //
  // + " }, {\n" //
  // + " \"title\" : \"Continued fraction\",\n" //
  // + " \"scanner\" : \"Numeric\",\n" //
  // + " \"error\" : \"false\",\n" //
  // + " \"numsubpods\" : 1,\n" //
  // + " \"subpods\" : [ {\n" //
  // + " \"plaintext\" : \"{0}\",\n" //
  // + " \"sinput\" : \"ContinuedFraction(1.6000000000000001`*^-27)\",\n" //
  // + " \"latex\" : \"\\\\{0\\\\}\"\n" //
  // + " } ]\n" //
  // + " } ]\n" //
  // + " }\n" //
  // + "}"); //
  // }

  @Test
  public void testRound001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("1/0", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertEquals(queryResult.isError(), false);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"numpods\" : 2,\n" + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1/0\",\n"
            + "        \"sinput\" : \"1/0\",\n" + "        \"latex\" : \"\\\\frac{1}{0}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Result\",\n"
            + "      \"scanner\" : \"Identity\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"ComplexInfinity\",\n" + "        \"sinput\" : \"1/0\",\n"
            + "        \"latex\" : \"ComplexInfinity\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testNRoots001() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();
    // ObjectNode messageJSON =
    // TestPods.createJUnitResult("1+2*x+3*x^2+4*x^3", formatsTEX);

    ObjectNode messageJSON =
        TestPods.createJUnitResult("x^2+2*x-3/(2*x^2)-5", formatsTEX);
    final String jsonStr = toPrettyStringNormalizingNewline(messageJSON);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    assertEquals(queryResult.isError(), false);

    // assertEquals(jsonStr, //
    // ""); //
  }

  @Test
  public void testHelloWorld() {
    // assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.resetModuleCounter4JUnit();

    // parser gets internally a syntax error
    ObjectNode messageJSON = TestPods.createJUnitResult("Hello world.", formatsTEX);

    JSONQueryResult queryResult = JSONQueryResult.queryResult(messageJSON);
    // but result gives no error
    assertEquals(queryResult.isError(), false);
  }
}
