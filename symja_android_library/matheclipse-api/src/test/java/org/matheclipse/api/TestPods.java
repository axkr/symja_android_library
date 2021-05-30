package org.matheclipse.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.matheclipse.api.parser.FuzzyParserFactory;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.FEConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestPods {

  static int formatsMATHML = 0;

  static int formatsTEX = 0;

  static int formatsHTML = 0;

  static {
    ToggleFeature.COMPILE = false;
    FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    Config.FUZZY_PARSER = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    Config.JAS_NO_THREADS = false;
    // Config.THREAD_FACTORY = com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
    Config.MATHML_TRIG_LOWERCASE = false;
    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    EvalEngine.get().setPackageMode(true);
    F.initSymbols(null, null, false); // new SymbolObserver(), false);
    FuzzyParserFactory.initialize();

    formatsMATHML = Pods.internFormat(new String[] {"mathml", "plaintext"});
    formatsTEX = Pods.internFormat(new String[] {"latex", "plaintext", "sinput"});
    formatsHTML = Pods.internFormat(new String[] {"html", "plaintext", "sinput"});
  }

  public static ObjectNode createJUnitResult(String inputStr, int formats) {
    try {
      return Pods.createResult(inputStr, formats, false);
    } catch (RuntimeException rex) {
      rex.printStackTrace();
      return Pods.errorJSON("0", "JSON Export Failed");
    }
  }

  @Test
  public void testSyntaxError001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("?#?", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + //
            "  \"queryresult\" : {\r\n"
            + //
            "    \"success\" : \"false\",\r\n"
            + //
            "    \"error\" : \"false\",\r\n"
            + //
            "    \"numpods\" : 0,\r\n"
            + //
            "    \"version\" : \"0.1\"\r\n"
            + //
            "  }\r\n"
            + //
            "}"); //
  }

  @Test
  public void testMarkdownHelp() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin", formatsHTML);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Sin\",\r\n"
        + "        \"sinput\" : \"Sin\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Plot\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Manipulate(Plot(Sin(a*x),{x,-10.0`,10.0`},PlotRange-&gt;{-2.0`,2.0`}),{a,1,10})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-11.55,2.75,11.55,-2.75]});\\nboard.suspendUpdate();\\nvar a = board.create('slider',[[-9.24,2.2],[9.24,2.2],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) { try { return [sin(mul(a.Value(),x))];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -10.0, 10.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Documentation\",\r\n"
        + "      \"scanner\" : \"help\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"markdown\" : \"## Sin\\n\\n```\\nSin(expr)\\n```\\n\\n> returns the sine of `expr` (measured in radians).\\n \\n`Sin(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.\\n\\nSee\\n* [Wikipedia - Sine](https://en.wikipedia.org/wiki/Sine)\\n* [Fungrim - Sine](http://fungrim.org/topic/Sine/)\\n\\n### Examples\\n\\n```\\n>> Sin(0)\\n0\\n\\n>> Sin(0.5)\\n0.479425538604203\\n\\n>> Sin(3*Pi)\\n0\\n\\n>> Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361\\n```\\n \\n\\n### Github\\n\\n* [Implementation of Sin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2718) \\n[Github master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2718)\\n\\n\",\r\n"
        + "        \"html\" : \"<h2>Sin</h2>\\n<pre><code>Sin(expr)\\n</code></pre>\\n<blockquote>\\n<p>returns the sine of <code>expr</code> (measured in radians).</p>\\n</blockquote>\\n<p><code>Sin(expr)</code> will evaluate automatically in the case <code>expr</code> is a multiple of <code>Pi, Pi/2, Pi/3, Pi/4</code> and <code>Pi/6</code>.</p>\\n<p>See</p>\\n<ul>\\n<li><a href=\\\"https://en.wikipedia.org/wiki/Sine\\\">Wikipedia - Sine</a></li>\\n<li><a href=\\\"http://fungrim.org/topic/Sine/\\\">Fungrim - Sine</a></li>\\n</ul>\\n<h3>Examples</h3>\\n<pre><code>&gt;&gt; Sin(0)\\n0\\n\\n&gt;&gt; Sin(0.5)\\n0.479425538604203\\n\\n&gt;&gt; Sin(3*Pi)\\n0\\n\\n&gt;&gt; Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361\\n</code></pre>\\n<h3>Github</h3>\\n<ul>\\n<li><a href=\\\"https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2718\\\">Implementation of Sin</a>\\n<a href=\\\"https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2718\\\">Github master</a></li>\\n</ul>\\n\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testTeXParser() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("\\sin 30 ^ { \\circ }", formatsHTML);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Sin(30*Degree)\",\r\n"
            + "        \"sinput\" : \"Sin(30*Degree)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Exact result\",\r\n"
            + "      \"scanner\" : \"Rational\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1/2\",\r\n"
            + "        \"sinput\" : \"Sin(30*Degree)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Decimal form\",\r\n"
            + "      \"scanner\" : \"Numeric\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"0.5\",\r\n"
            + "        \"sinput\" : \"N(Sin(30*Degree))\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testSoundexHelp() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Cs", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 34,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"cs\",\r\n"
            + "        \"sinput\" : \"cs\",\r\n"
            + "        \"latex\" : \"cs\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Standard Name\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Caesium\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"StandardName\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{Caesium}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Atomic Number\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"55\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicNumber\\\")\",\r\n"
            + "        \"latex\" : \"55\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Abbreviation\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Cs\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Abbreviation\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{Cs}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Absolute Boiling Point\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"944\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AbsoluteBoilingPoint\\\")\",\r\n"
            + "        \"latex\" : \"944\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Absolute Melting Point\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"301.7\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AbsoluteMeltingPoint\\\")\",\r\n"
            + "        \"latex\" : \"301.7\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Atomic Radius\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"260\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicRadius\\\")\",\r\n"
            + "        \"latex\" : \"260\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Atomic Weight\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"132.91\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"AtomicWeight\\\")\",\r\n"
            + "        \"latex\" : \"132.91\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Block\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"s\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Block\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{s}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Boiling Point\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"670.85\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BoilingPoint\\\")\",\r\n"
            + "        \"latex\" : \"670.85\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Brinell Hardness\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"0.14\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BrinellHardness\\\")\",\r\n"
            + "        \"latex\" : \"0.14\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Bulk Modulus\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1.6\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"BulkModulus\\\")\",\r\n"
            + "        \"latex\" : \"1.6\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Covalent Radius\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"225\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"CovalentRadius\\\")\",\r\n"
            + "        \"latex\" : \"225\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Crust Abundance\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"0\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"CrustAbundance\\\")\",\r\n"
            + "        \"latex\" : \"0\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Density\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1873\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Density\\\")\",\r\n"
            + "        \"latex\" : \"1873\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Discovery Year\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1860\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"DiscoveryYear\\\")\",\r\n"
            + "        \"latex\" : \"1860\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Electro Negativity\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"0.79\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectroNegativity\\\")\",\r\n"
            + "        \"latex\" : \"0.79\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Electron Affinity\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"45.51\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronAffinity\\\")\",\r\n"
            + "        \"latex\" : \"45.51\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Electron Configuration\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"[Xe] 6s1\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronConfiguration\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{[Xe] 6s1}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Electron Configuration String\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{2,8,18,18,8,1}\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ElectronConfigurationString\\\")\",\r\n"
            + "        \"latex\" : \"\\\\{2,8,18,18,8,1\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Fusion Heat\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"2.09\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"FusionHeat\\\")\",\r\n"
            + "        \"latex\" : \"2.09\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Group\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Group\\\")\",\r\n"
            + "        \"latex\" : \"1\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Ionization Energies\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{375.7,2234.3,3400}\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"IonizationEnergies\\\")\",\r\n"
            + "        \"latex\" : \"\\\\{375.7,2234.3,3400\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Liquid Density\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1843\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"LiquidDensity\\\")\",\r\n"
            + "        \"latex\" : \"1843\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Melting Point\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"28.55\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"MeltingPoint\\\")\",\r\n"
            + "        \"latex\" : \"28.55\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Mohs Hardness\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"0.2\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"MohsHardness\\\")\",\r\n"
            + "        \"latex\" : \"0.2\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Name\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"caesium\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Name\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{caesium}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Period\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"6\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Period\\\")\",\r\n"
            + "        \"latex\" : \"6\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Series\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"AlkaliMetal\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"Series\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{AlkaliMetal}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Shear Modulus\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{{2},{2,6},{2,6,10},{2,6,10},{2,6},{1}}\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ShearModulus\\\")\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\{2\\\\},\\\\{2,6\\\\},\\\\{2,6,10\\\\},\\\\{2,6,10\\\\},\\\\{2,6\\\\},\\\\{1\\\\}\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Specific Heat\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"242\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"SpecificHeat\\\")\",\r\n"
            + "        \"latex\" : \"242\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Thermal Conductivity\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"35.9\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"ThermalConductivity\\\")\",\r\n"
            + "        \"latex\" : \"35.9\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Vaporization Heat\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"63.9\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"VaporizationHeat\\\")\",\r\n"
            + "        \"latex\" : \"63.9\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Young Modulus\",\r\n"
            + "      \"scanner\" : \"Data\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"37\",\r\n"
            + "        \"sinput\" : \"ElementData(\\\"Caesium\\\",\\\"YoungModulus\\\")\",\r\n"
            + "        \"latex\" : \"37\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testInteger17() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("17", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 9,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"17\",\r\n"
            + "        \"sinput\" : \"17\",\r\n"
            + "        \"latex\" : \"17\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Number name\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"seventeen\",\r\n"
            + "        \"sinput\" : \"IntegerName(17,\\\"Words\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{seventeen}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Roman numerals\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"XVII\",\r\n"
            + "        \"sinput\" : \"RomanNumeral(17)\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{XVII}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Binary form\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"10001_2\",\r\n"
            + "        \"sinput\" : \"BaseForm(17,2)\",\r\n"
            + "        \"latex\" : \"{\\\\textnormal{10001}}_{2}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Prime factorization\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"17 is a prime number.\",\r\n"
            + "        \"sinput\" : \"FactorInteger(17)\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n17 & 1 \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Residues modulo small integers\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;17 mod m&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;5&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;8&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"Mod(17,{2,3,4,5,6,7,8,9})\",\r\n"
            + "        \"latex\" : \"\\\\{1,2,1,2,5,3,1,8\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Properties\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 2,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"17 is an odd number.\",\r\n"
            + "        \"sinput\" : \"OddQ(17)\"\r\n"
            + "      }, {\r\n"
            + "        \"plaintext\" : \"17 is the 7th prime number.\",\r\n"
            + "        \"sinput\" : \"PrimePi(17)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Quadratic residues modulo 17\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{0,1,2,4,8,9,13,15,16}\",\r\n"
            + "        \"sinput\" : \"Union(PowerMod(Range(0,17/2),2,17))\",\r\n"
            + "        \"latex\" : \"\\\\{0,1,2,4,8,9,13,15,16\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Primitive roots modulo 17\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{3,5,6,7,10,11,12,14}\",\r\n"
            + "        \"sinput\" : \"Select(Range(16),MultiplicativeOrder(#1,17)==EulerPhi(17)&amp;)\",\r\n"
            + "        \"latex\" : \"\\\\{3,5,6,7,10,11,12,14\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testRationalHalf() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + //
            "  \"queryresult\" : {\r\n"
            + //
            "    \"success\" : \"true\",\r\n"
            + //
            "    \"error\" : \"false\",\r\n"
            + //
            "    \"numpods\" : 3,\r\n"
            + //
            "    \"version\" : \"0.1\",\r\n"
            + //
            "    \"pods\" : [ {\r\n"
            + //
            "      \"title\" : \"Input\",\r\n"
            + //
            "      \"scanner\" : \"Identity\",\r\n"
            + //
            "      \"error\" : \"false\",\r\n"
            + //
            "      \"numsubpods\" : 1,\r\n"
            + //
            "      \"subpods\" : [ {\r\n"
            + //
            "        \"plaintext\" : \"1/2\",\r\n"
            + //
            "        \"sinput\" : \"1/2\",\r\n"
            + //
            "        \"latex\" : \"\\\\frac{1}{2}\"\r\n"
            + //
            "      } ]\r\n"
            + //
            "    }, {\r\n"
            + //
            "      \"title\" : \"Exact result\",\r\n"
            + //
            "      \"scanner\" : \"Rational\",\r\n"
            + //
            "      \"error\" : \"false\",\r\n"
            + //
            "      \"numsubpods\" : 1,\r\n"
            + //
            "      \"subpods\" : [ {\r\n"
            + //
            "        \"plaintext\" : \"1/2\",\r\n"
            + //
            "        \"sinput\" : \"1/2\",\r\n"
            + //
            "        \"latex\" : \"\\\\frac{1}{2}\"\r\n"
            + //
            "      } ]\r\n"
            + //
            "    }, {\r\n"
            + //
            "      \"title\" : \"Decimal form\",\r\n"
            + //
            "      \"scanner\" : \"Numeric\",\r\n"
            + //
            "      \"error\" : \"false\",\r\n"
            + //
            "      \"numsubpods\" : 1,\r\n"
            + //
            "      \"subpods\" : [ {\r\n"
            + //
            "        \"plaintext\" : \"0.5\",\r\n"
            + //
            "        \"sinput\" : \"N(1/2)\",\r\n"
            + //
            "        \"latex\" : \"0.5\"\r\n"
            + //
            "      } ]\r\n"
            + //
            "    } ]\r\n"
            + //
            "  }\r\n"
            + //
            "}"); //
  }

  @Test
  public void testRationalPlus() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2+3/4", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 5,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1/2+3/4\",\r\n"
            + "        \"sinput\" : \"1/2 + 3/4\",\r\n"
            + "        \"latex\" : \"\\\\frac{1}{2}+\\\\frac{3}{4}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Exact result\",\r\n"
            + "      \"scanner\" : \"Rational\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"5/4\",\r\n"
            + "        \"sinput\" : \"1/2 + 3/4\",\r\n"
            + "        \"latex\" : \"\\\\frac{5}{4}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Decimal form\",\r\n"
            + "      \"scanner\" : \"Numeric\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1.25\",\r\n"
            + "        \"sinput\" : \"N(1/2 + 3/4)\",\r\n"
            + "        \"latex\" : \"1.25\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Mixed fraction\",\r\n"
            + "      \"scanner\" : \"Rational\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1 1/4\",\r\n"
            + "        \"sinput\" : \"{IntegerPart(5/4),FractionalPart(5/4)}\",\r\n"
            + "        \"latex\" : \"\\\\{1,\\\\frac{1}{4}\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Continued fraction\",\r\n"
            + "      \"scanner\" : \"ContinuedFraction\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"[1; 4]\",\r\n"
            + "        \"sinput\" : \"ContinuedFraction(5/4)\",\r\n"
            + "        \"latex\" : \"\\\\{1,4\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testPlotSin() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(Sin(x), {x, 0, 6*Pi} )", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Plot(Sin(x),{x,0,6*Pi})\",\r\n"
            + "        \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\r\n"
            + "        \"latex\" : \"\\\\text{Plot}(\\\\sin (x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Function\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\r\n"
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.492477796076938,1.65,20.342033717615696,-1.65]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0, (18.84955592153876)],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testPlot002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON =
        TestPods.createJUnitResult(
            "Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}) // JSForm", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\r\n"
            + "        \"sinput\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\r\n"
            + "        \"latex\" : \"\\\\text{JSForm}(\\\\text{Plot}(\\\\{\\\\sin (x),\\\\cos (x),\\\\tan (x)\\\\},\\\\{x,\\\\left( -2\\\\right) \\\\cdot \\\\pi,2\\\\cdot \\\\pi\\\\}))\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Result\",\r\n"
            + "      \"scanner\" : \"String form\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;iframe srcdoc=\\\"&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot;?&amp;gt;\\n\\n&amp;lt;!DOCTYPE html PUBLIC\\n  &amp;quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&amp;quot;\\n  &amp;quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&amp;quot;&amp;gt;\\n\\n&amp;lt;html xmlns=&amp;quot;http://www.w3.org/1999/xhtml&amp;quot; style=&amp;quot;width: 100%; height: 100%; margin: 0; padding: 0&amp;quot;&amp;gt;\\n&amp;lt;head&amp;gt;\\n&amp;lt;meta charset=&amp;quot;utf-8&amp;quot;&amp;gt;\\n&amp;lt;title&amp;gt;Highlight&amp;lt;/title&amp;gt;\\n\\n&amp;lt;link rel=&amp;quot;stylesheet&amp;quot; type=&amp;quot;text/css&amp;quot; href=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/styles/default.min.css&amp;quot; /&amp;gt;\\n  &amp;lt;script type=&amp;quot;text/javascript&amp;quot; src=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/highlight.min.js&amp;quot;&amp;gt;&amp;lt;/script&amp;gt;\\n&amp;lt;script&amp;gt;hljs.initHighlightingOnLoad();&amp;lt;/script&amp;gt;&amp;lt;/head&amp;gt;\\n&amp;lt;body style=&amp;quot;width: 100%; height: 100%; margin: 0; padding: 0&amp;quot;&amp;gt;\\n\\n&amp;lt;div id=&amp;quot;highlight&amp;quot; style=&amp;quot;width: 600px; height: 800px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&amp;quot;&amp;gt;\\n&amp;lt;pre&amp;gt;&amp;lt;code class=&amp;quot;javascript&amp;quot;&amp;gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-7.461503837897545,18.033999328253977,7.461503837897545,-18.033999328251273]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [cos(x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [tan(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#8fb032'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&amp;lt;/code&amp;gt;&amp;lt;/pre&amp;gt;\\n&amp;lt;/div&amp;gt;\\n&amp;lt;/body&amp;gt;\\n&amp;lt;/html&amp;gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" &gt;&lt;/iframe&gt;\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testPlotF() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(f(x), {x, 0, 6*Pi} )", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Plot(f(x),{x,0,6*Pi})\",\r\n"
            + "        \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\r\n"
            + "        \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Evaluated result\",\r\n"
            + "      \"scanner\" : \"Expression\",\r\n"
            + "      \"error\" : \"true\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Manipulate: Cannot convert to JavaScript. Function head: f\",\r\n"
            + "        \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\r\n"
            + "        \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testPlotBessel() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON =
        TestPods.createJUnitResult(
            "Plot(Evaluate(Table(BesselJ(n, x), {n, 4})), {x, 0, 10})", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\r\n"
            + "        \"sinput\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\r\n"
            + "        \"latex\" : \"\\\\text{Plot}(\\\\text{Evaluate}(\\\\text{Table}(\\\\text{BesselJ}(n,x),\\\\{n,4\\\\})),\\\\{x,0,10\\\\})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Function\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"Plot(Evaluate(Table(BesselJ(n,x),{n,4})),{x,0,10})\",\r\n"
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.05,1.1778908410078661,11.05,-0.9423347230773148]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [besselJ(1,x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [besselJ(2,x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [besselJ(3,x)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return [besselJ(4,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0, 10],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, 0, 10],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, 0, 10],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, 0, 10],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testSin() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin(x)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 5,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Sin(x)\",\r\n"
        + "        \"sinput\" : \"Sin(x)\",\r\n"
        + "        \"latex\" : \"\\\\sin (x)\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot(Sin(x),{x,-7.0`,7.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,1.6494784136660268,8.25,-1.6494784136660272]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Alternate form\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"(I*1/2)/E^(I*x)-I*1/2*E^(I*x)\",\r\n"
        + "        \"sinput\" : \"TrigToExp(Sin(x))\",\r\n"
        + "        \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\cdot x}} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i \\\\cdot x}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Derivative\",\r\n"
        + "      \"scanner\" : \"Derivative\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Cos(x)\",\r\n"
        + "        \"sinput\" : \"D(Sin(x),x)\",\r\n"
        + "        \"latex\" : \"\\\\cos (x)\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Indefinite integral\",\r\n"
        + "      \"scanner\" : \"Integral\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"-Cos(x)\",\r\n"
        + "        \"sinput\" : \"Integrate(Sin(x),x)\",\r\n"
        + "        \"latex\" : \" - \\\\cos (x)\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testPolynomialQuotientRemainder() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult(" x**2-4,x-2", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 5,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{-4+x^2,-2+x}\",\r\n"
        + "        \"sinput\" : \"{-4 + x^2,-2 + x}\",\r\n"
        + "        \"latex\" : \"\\\\{-4+{x}^{2},-2+x\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Polynomial quotient and remainder\",\r\n"
        + "      \"scanner\" : \"Polynomial\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{2+x,0}\",\r\n"
        + "        \"sinput\" : \"PolynomialQuotientRemainder(-4 + x^2,-2 + x,x)\",\r\n"
        + "        \"latex\" : \"\\\\{2+x,0\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot({-4 + x^2,-2 + x},{x,-7.0`,7.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,48.25,8.25,-12.25]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [add(-4,pow(x,2))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [add(-2,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0, 7.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Derivative\",\r\n"
        + "      \"scanner\" : \"Derivative\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{2*x,1}\",\r\n"
        + "        \"sinput\" : \"D({-4 + x^2,-2 + x},x)\",\r\n"
        + "        \"latex\" : \"\\\\{2\\\\cdot x,1\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Indefinite integral\",\r\n"
        + "      \"scanner\" : \"Integral\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{-4*x+x^3/3,-2*x+x^2/2}\",\r\n"
        + "        \"sinput\" : \"Integrate({-4 + x^2,-2 + x},x)\",\r\n"
        + "        \"latex\" : \"\\\\{\\\\left( -4\\\\right) \\\\cdot x+\\\\frac{{x}^{3}}{3},\\\\left( -2\\\\right) \\\\cdot x+\\\\frac{{x}^{2}}{2}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testSinXY() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON = TestPods.createJUnitResult("Sin(x*y)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Sin(x*y)\",\r\n"
        + "        \"sinput\" : \"Sin(x*y)\",\r\n"
        + "        \"latex\" : \"\\\\sin (x\\\\,y)\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"3D plot\",\r\n"
        + "      \"scanner\" : \"Plot\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot3D(Sin(x*y),{x,-3.5`,3.5`},{y,-3.5`,3.5`})\",\r\n"
        + "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.9.2/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n\\nvar parent = document.currentScript.parentNode;\\n\\nvar id = generateId();\\nparent.id = id;\\n\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\n\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Alternate form\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"(I*1/2)/E^(I*x*y)-I*1/2*E^(I*x*y)\",\r\n"
        + "        \"sinput\" : \"TrigToExp(Sin(x*y))\",\r\n"
        + "        \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\cdot x\\\\cdot y}} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i \\\\cdot x\\\\cdot y}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testColor001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));

    // ObjectNode messageJSON = Pods.createResult("TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))",
    // formatsTEX);
    ObjectNode messageJSON =
        TestPods.createJUnitResult( //
            "Yellow", //
            formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Yellow\",\r\n"
            + "        \"sinput\" : \"Yellow\",\r\n"
            + "        \"latex\" : \"Yellow\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Result\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"RGBColor(1.0,1.0,0.0)\",\r\n"
            + "        \"sinput\" : \"Yellow\",\r\n"
            + "        \"latex\" : \"\\\\text{RGBColor}(1.0,1.0,0.0)\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testComplexPlot3D() {

    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON =
        TestPods.createJUnitResult(
            "ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})",
            formatsMATHML);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"ComplexPlot3D((1+z^2)/(-1+z^2),{z,-2+(-2)*I,2+I*2},PlotRange-&gt;{0,3})\",\r\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mi>ComplexPlot3D</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mfrac><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>+</mo><mn>1</mn></mrow><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>-</mo><mn>1</mn></mrow></mfrac><mo>,</mo><mrow><mo>{</mo><mrow><mi>z</mi><mo>,</mo><mrow><mrow><mrow><mo>(</mo><mn>-2</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mo>-</mo><mn>2</mn></mrow><mo>,</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow><mo>&#0183;</mo><mn>2</mn></mrow><mo>+</mo><mn>2</mn></mrow></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mi>PlotRange</mi><mo>-&gt;</mo><mrow><mo>{</mo><mrow><mn>0</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></mrow></mrow><mo>)</mo></mrow></mrow></math>\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Function\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"ComplexPlot3D((1 + z^2)/(-1 + z^2),{z,-2 + (-2)*I,2 + I*2},PlotRange-&gt;{0,3})\",\r\n"
            + "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.9.2/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n\\nvar parent = document.currentScript.parentNode;\\n\\nvar id = generateId();\\nparent.id = id;\\n\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { try { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2))));}catch(e){return complex(Number.NaN);} }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\n\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testHistogram() {

    ObjectNode messageJSON =
        TestPods.createJUnitResult(
            "Histogram(RandomVariate(NormalDistribution(0, 1), 200))", formatsMATHML);
    final String jsonStr = messageJSON.toPrettyString();
    // if (s.contains("Windows")) {
    // RandomVariate gives random results

    // }
  }

  @Test
  public void testList() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("1,2,3", formatsMATHML);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 5,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{1,2,3}\",\r\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></math>\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Total\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"6\",\r\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>6</mn></math>\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Vector length\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"3.7416573867739413\",\r\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>3.74166</mn></math>\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Normalized vector\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)}\",\r\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mfrac><mn>1</mn><msqrt><mn>14</mn></msqrt></mfrac><mo>,</mo><msqrt><mrow><mfrac><mn>2</mn><mn>7</mn></mfrac></mrow></msqrt><mo>,</mo><mfrac><mn>3</mn><msqrt><mn>14</mn></msqrt></mfrac></mrow><mo>}</mo></mrow></math>\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Plot points\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"ListPlot({1.0,2.0,3.0})\",\r\n"
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.75,3.65,4.75,0.35]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testMatrix001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("1, 17 + 4*I\n17 - 4*I, 10", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{{1,17+I*4},{17+(-4)*I,10}}\",\r\n"
        + "        \"sinput\" : \"{{1,17 + I*4},{17 + (-4)*I,10}}\",\r\n"
        + "        \"latex\" : \"\\\\{\\\\{1,17 + i \\\\cdot 4\\\\},\\\\{17 - 4\\\\cdot i ,10\\\\}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Properties\",\r\n"
        + "      \"scanner\" : \"Matrix\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"The matrix is hermitian (self-adjoint).\",\r\n"
        + "        \"sinput\" : \"HermitianMatrixQ({{1,17 + I*4}, {17 - I*4,10}})\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{{1,17+I*4},\\n {17-I*4,10}}\",\r\n"
        + "        \"sinput\" : \"{{1,17 + I*4},{17 + (-4)*I,10}}\",\r\n"
        + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n1 & 17 + 4\\\\,i  \\\\\\\\\\n17 - 4\\\\,i  & 10 \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testMatrix002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON = TestPods.createJUnitResult("1,3\n3,4", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 5,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{{1,3},{3,4}}\",\r\n"
            + "        \"sinput\" : \"{{1,3},{3,4}}\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\{1,3\\\\},\\\\{3,4\\\\}\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Properties\",\r\n"
            + "      \"scanner\" : \"Matrix\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"The matrix is symmetric.\",\r\n"
            + "        \"sinput\" : \"SymmetricMatrixQ({{1,3}, {3,4}})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Properties\",\r\n"
            + "      \"scanner\" : \"Matrix\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"The determinant of the matrix is -5\",\r\n"
            + "        \"sinput\" : \"Det({{1,3}, {3,4}})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Inverse of matrix\",\r\n"
            + "      \"scanner\" : \"Matrix\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{{-4/5,3/5},\\n {3/5,-1/5}}\",\r\n"
            + "        \"sinput\" : \"Inverse({{1,3}, {3,4}})\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n\\\\frac{-4}{5} & \\\\frac{3}{5} \\\\\\\\\\n\\\\frac{3}{5} & \\\\frac{-1}{5} \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Plot points\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"ListPlot({{1.0,3.0},\\n {3.0,4.0}})\",\r\n"
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[0.35,4.6,3.65,2.4]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1.0;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3.0;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testSolve001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("3+x=10", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 4,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"3+x==10\",\r\n"
        + "        \"sinput\" : \"3 + x==10\",\r\n"
        + "        \"latex\" : \"3+x == 10\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot({x,7},{x,-20.0`,20.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-22.55,22.54999999999999,22.55,-22.55]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [7];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -20.0, 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -20.0, 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Alternate form\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"-7+x==0\",\r\n"
        + "        \"sinput\" : \"-7 + x==0\",\r\n"
        + "        \"latex\" : \"-7+x == 0\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Solution\",\r\n"
        + "      \"scanner\" : \"Reduce\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{{x-&gt;7}}\",\r\n"
        + "        \"sinput\" : \"Solve(x==7,{x})\",\r\n"
        + "        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testSolve002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("x^2+1=0", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 4,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"1+x^2==0\",\r\n"
        + "        \"sinput\" : \"1 + x^2==0\",\r\n"
        + "        \"latex\" : \"1+{x}^{2} == 0\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot({x^2,-1},{x,-20.0`,20.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-22.55,407.8972000000001,22.55,-39.25720000000001]});\\nboard.suspendUpdate();\\n\\nfunction $f3(x) { try { return [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return [-1];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f3, -20.0, 20.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f4, -20.0, 20.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Alternate form\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"1+x^2==0\",\r\n"
        + "        \"sinput\" : \"1 + x^2==0\",\r\n"
        + "        \"latex\" : \"1+{x}^{2} == 0\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Solution\",\r\n"
        + "      \"scanner\" : \"Reduce\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{{x-&gt;-I},{x-&gt;I}}\",\r\n"
        + "        \"sinput\" : \"Solve(x^2==-1,{x})\",\r\n"
        + "        \"latex\" : \"\\\\{\\\\{x\\\\to  - i \\\\},\\\\{x\\\\to i \\\\}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testSolve003() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Solver x+3=10", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Solve(3+x==10)\",\r\n"
            + "        \"sinput\" : \"Solve(3 + x==10)\",\r\n"
            + "        \"latex\" : \"\\\\text{Solve}(3+x == 10)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Solve equation\",\r\n"
            + "      \"scanner\" : \"Solver\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{{x-&gt;7}}\",\r\n"
            + "        \"sinput\" : \"Solve(3 + x==10,{x})\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testInteger4294967295() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("2**32-1", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 7,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"-1+2^32\",\r\n"
        + "        \"sinput\" : \"-1 + 2^32\",\r\n"
        + "        \"latex\" : \"-1+{2}^{32}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4294967295\",\r\n"
        + "        \"sinput\" : \"-1 + 2^32\",\r\n"
        + "        \"latex\" : \"4294967295\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Number name\",\r\n"
        + "      \"scanner\" : \"Integer\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five\",\r\n"
        + "        \"sinput\" : \"IntegerName(4294967295,\\\"Words\\\")\",\r\n"
        + "        \"latex\" : \"\\\\textnormal{four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Binary form\",\r\n"
        + "      \"scanner\" : \"Integer\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"11111111111111111111111111111111_2\",\r\n"
        + "        \"sinput\" : \"BaseForm(4294967295,2)\",\r\n"
        + "        \"latex\" : \"{\\\\textnormal{11111111111111111111111111111111}}_{2}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Prime factorization\",\r\n"
        + "      \"scanner\" : \"Integer\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"3*5*17*257*65537\",\r\n"
        + "        \"sinput\" : \"FactorInteger(4294967295)\",\r\n"
        + "        \"latex\" : \"3\\\\cdot 5\\\\cdot 17\\\\cdot 257\\\\cdot 65537\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Residues modulo small integers\",\r\n"
        + "      \"scanner\" : \"Integer\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;4294967295 mod m&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;7&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
        + "        \"sinput\" : \"Mod(4294967295,{2,3,4,5,6,7,8,9})\",\r\n"
        + "        \"latex\" : \"\\\\{1,0,3,0,3,3,7,3\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Properties\",\r\n"
        + "      \"scanner\" : \"Integer\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4294967295 is an odd number.\",\r\n"
        + "        \"sinput\" : \"OddQ(4294967295)\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testNormalDistribution() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("NormalDistribution(a,b)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 4,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"NormalDistribution(a,b)\",\r\n"
            + "        \"sinput\" : \"NormalDistribution(a,b)\",\r\n"
            + "        \"latex\" : \"\\\\text{NormalDistribution}(a,b)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Statistical properties\",\r\n"
            + "      \"scanner\" : \"Statistics\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;mean&lt;/td&gt;&lt;td&gt;a&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;standard deviation&lt;/td&gt;&lt;td&gt;b&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;variance&lt;/td&gt;&lt;td&gt;b^2&lt;/td&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;skewness&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"{}\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\text{Mean}(\\\\text{NormalDistribution}(a,b)),\\\\text{StandardDeviation}(\\\\text{NormalDistribution}(a,b)),\\\\text{Variance}(\\\\text{NormalDistribution}(a,b)),\\\\text{Skewness}(\\\\text{NormalDistribution}(a,b))\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Probability density function (PDF)\",\r\n"
            + "      \"scanner\" : \"Statistics\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1/(b*E^((-a+x)^2/(2*b^2))*Sqrt(2*Pi))\",\r\n"
            + "        \"sinput\" : \"PDF(NormalDistribution(a,b),x)\",\r\n"
            + "        \"latex\" : \"\\\\frac{1}{b\\\\,{e}^{\\\\frac{{\\\\left(  - a+x\\\\right) }^{2}}{2\\\\cdot {b}^{2}}}\\\\,\\\\sqrt{\\\\left( 2\\\\cdot \\\\pi\\\\right) }}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Cumulative distribution function (CDF)\",\r\n"
            + "      \"scanner\" : \"Statistics\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Erfc((a-x)/(Sqrt(2)*b))/2\",\r\n"
            + "        \"sinput\" : \"CDF(NormalDistribution(a,b),x)\",\r\n"
            + "        \"latex\" : \"\\\\frac{\\\\text{Erfc}(\\\\frac{a - x}{\\\\sqrt{2}\\\\,b})}{2}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testLogic001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("a&&b||c", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\r\n"
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\r\n"
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Truth table\",\r\n"
            + "      \"scanner\" : \"Boolean\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;th&gt;(a&amp;&amp;b)||c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"BooleanTable(Append({a,b,c},(a&amp;&amp;b)||c),{a,b,c})\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cccc}\\nTrue & True & True & True \\\\\\\\\\nTrue & True & False & True \\\\\\\\\\nTrue & False & True & True \\\\\\\\\\nTrue & False & False & False \\\\\\\\\\nFalse & True & True & True \\\\\\\\\\nFalse & True & False & False \\\\\\\\\\nFalse & False & True & True \\\\\\\\\\nFalse & False & False & False \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Satisfiability instance\",\r\n"
            + "      \"scanner\" : \"Boolean\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"SatisfiabilityInstances((a&amp;&amp;b)||c,{a,b,c},1)\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{ccc}\\nFalse & False & True \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testLogic002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("a&b|c", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\r\n"
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\r\n"
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Truth table\",\r\n"
            + "      \"scanner\" : \"Boolean\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;th&gt;(a&amp;&amp;b)||c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"BooleanTable(Append({a,b,c},(a&amp;&amp;b)||c),{a,b,c})\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cccc}\\nTrue & True & True & True \\\\\\\\\\nTrue & True & False & True \\\\\\\\\\nTrue & False & True & True \\\\\\\\\\nTrue & False & False & False \\\\\\\\\\nFalse & True & True & True \\\\\\\\\\nFalse & True & False & False \\\\\\\\\\nFalse & False & True & True \\\\\\\\\\nFalse & False & False & False \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Satisfiability instance\",\r\n"
            + "      \"scanner\" : \"Boolean\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;a&lt;/th&gt;&lt;th&gt;b&lt;/th&gt;&lt;th&gt;c&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;tr&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;F&lt;/td&gt;&lt;td&gt;T&lt;/td&gt;&lt;/tr&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"SatisfiabilityInstances((a&amp;&amp;b)||c,{a,b,c},1)\",\r\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{ccc}\\nFalse & False & True \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testQuantity() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("convert 111 cm in m", formatsHTML);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"111*cm*convert*In*m\",\r\n"
            + "        \"sinput\" : \"111*cm*convert*In*m\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Evaluated result\",\r\n"
            + "      \"scanner\" : \"Expression\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"111*cm*convert*In*m\",\r\n"
            + "        \"sinput\" : \"111*cm*convert*In*m\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testTimes() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("10*11*12", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 8,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"10*11*12\",\r\n"
            + "        \"sinput\" : \"10*11*12\",\r\n"
            + "        \"latex\" : \"10\\\\cdot 11\\\\cdot 12\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Result\",\r\n"
            + "      \"scanner\" : \"Simplification\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1320\",\r\n"
            + "        \"sinput\" : \"10*11*12\",\r\n"
            + "        \"latex\" : \"1320\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Number name\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"one thousand three hundred twenty\",\r\n"
            + "        \"sinput\" : \"IntegerName(1320,\\\"Words\\\")\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{one thousand three hundred twenty}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Roman numerals\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"MCCCXX\",\r\n"
            + "        \"sinput\" : \"RomanNumeral(1320)\",\r\n"
            + "        \"latex\" : \"\\\\textnormal{MCCCXX}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Binary form\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"10100101000_2\",\r\n"
            + "        \"sinput\" : \"BaseForm(1320,2)\",\r\n"
            + "        \"latex\" : \"{\\\\textnormal{10100101000}}_{2}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Prime factorization\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"2^3*3*5*11\",\r\n"
            + "        \"sinput\" : \"FactorInteger(1320)\",\r\n"
            + "        \"latex\" : \"{2}^{3}\\\\cdot 3\\\\cdot 5\\\\cdot 11\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Residues modulo small integers\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;1320 mod m&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;4&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;td&gt;6&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\r\n"
            + "        \"sinput\" : \"Mod(1320,{2,3,4,5,6,7,8,9})\",\r\n"
            + "        \"latex\" : \"\\\\{0,0,0,0,0,4,0,6\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Properties\",\r\n"
            + "      \"scanner\" : \"Integer\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1320 is an even number.\",\r\n"
            + "        \"sinput\" : \"EvenQ(1320)\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testTransliterate() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Transliterate(\"Càfé\")", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Transliterate(Càfé)\",\r\n"
        + "        \"sinput\" : \"Transliterate(\\\"Càfé\\\")\",\r\n"
        + "        \"latex\" : \"\\\\text{Transliterate}(\\\\textnormal{Càfé})\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"String form\",\r\n"
        + "      \"scanner\" : \"String\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Cafe\",\r\n"
        + "        \"sinput\" : \"Transliterate(\\\"Càfé\\\")\",\r\n"
        + "        \"latex\" : \"\\\\textnormal{Cafe}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Documentation\",\r\n"
        + "      \"scanner\" : \"help\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"markdown\" : \"## Chop\\n\\n```\\nChop(numerical-expr)\\n```\\n\\n> replaces numerical values in the `numerical-expr` which are close to zero with symbolic value `0`.\\n\\n### Examples\\n\\n```\\n>> Chop(0.00000000001)\\n0\\n```\\n\\n### Github\\n\\n* [Implementation of Chop](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L686) \\n[Github master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L686)\\n\\n\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testSimplify() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON =
        TestPods.createJUnitResult("simplificate Sqrt(9-4*Sqrt(5))", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\r\n"
        + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\r\n"
        + "        \"latex\" : \"\\\\text{FullSimplify}(\\\\sqrt{\\\\left( 9 - 4\\\\cdot \\\\sqrt{5}\\\\right) })\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Decimal form\",\r\n"
        + "      \"scanner\" : \"Numeric\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"0.2360679774997898\",\r\n"
        + "        \"sinput\" : \"N(FullSimplify(Sqrt(9 - 4*Sqrt(5))))\",\r\n"
        + "        \"latex\" : \"0.236068\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"-2+Sqrt(5)\",\r\n"
        + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\r\n"
        + "        \"latex\" : \"-2+\\\\sqrt{5}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testIntegrate001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("Integral Sin(x)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Integrate(Sin(x))\",\r\n"
            + "        \"sinput\" : \"Integrate(Sin(x))\",\r\n"
            + "        \"latex\" : \"\\\\text{Integrate}(\\\\sin (x))\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Integration\",\r\n"
            + "      \"scanner\" : \"Integral\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"-Cos(x)\",\r\n"
            + "        \"sinput\" : \"Integrate(Sin(x),x)\",\r\n"
            + "        \"latex\" : \" - \\\\cos (x)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Alternate form\",\r\n"
            + "      \"scanner\" : \"Simplification\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"-1/(2*E^(I*x))-E^(I*x)/2\",\r\n"
            + "        \"sinput\" : \"TrigToExp(-Cos(x))\",\r\n"
            + "        \"latex\" : \"\\\\frac{-1}{2\\\\cdot {e}^{i \\\\cdot x}}+\\\\frac{ - {e}^{i \\\\cdot x}}{2}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testIntegrate002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("integrate Tan(x)*Cos(x)*Pi x", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n"
            + "        \"latex\" : \"\\\\int  \\\\pi\\\\,\\\\cos (x)\\\\,\\\\tan (x)\\\\,\\\\mathrm{d}x\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Integration\",\r\n"
            + "      \"scanner\" : \"Integral\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"-Pi*Cos(x)\",\r\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n"
            + "        \"latex\" : \" - \\\\pi\\\\,\\\\cos (x)\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Alternate form\",\r\n"
            + "      \"scanner\" : \"Simplification\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"-(1/(2*E^(I*x))+E^(I*x)/2)*Pi\",\r\n"
            + "        \"sinput\" : \"TrigToExp(-Pi*Cos(x))\",\r\n"
            + "        \"latex\" : \" - \\\\frac{1}{2\\\\cdot {e}^{i \\\\cdot x}}+\\\\frac{{e}^{i \\\\cdot x}}{2}\\\\,\\\\pi\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testDerivative001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    ObjectNode messageJSON = TestPods.createJUnitResult("derive tan(x^3)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"D(Tan(x^3))\",\r\n"
            + "        \"sinput\" : \"D(Tan(x^3))\",\r\n"
            + "        \"latex\" : \"D(\\\\tan ({x}^{3}))\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Derivative\",\r\n"
            + "      \"scanner\" : \"Derivative\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"3*x^2*Sec(x^3)^2\",\r\n"
            + "        \"sinput\" : \"D(Tan(x^3),x)\",\r\n"
            + "        \"latex\" : \"3\\\\cdot {x}^{2}\\\\cdot {\\\\sec ({x}^{3})}^{2}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Alternate form\",\r\n"
            + "      \"scanner\" : \"Simplification\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"(12*x^2)/(E^(-I*x^3)+E^(I*x^3))^2\",\r\n"
            + "        \"sinput\" : \"TrigToExp(3*x^2*Sec(x^3)^2)\",\r\n"
            + "        \"latex\" : \"\\\\frac{12\\\\cdot {x}^{2}}{{\\\\left( {e}^{\\\\left(  - i \\\\right) \\\\cdot {x}^{3}}+{e}^{i \\\\cdot {x}^{3}}\\\\right) }^{2}}\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testListPlot001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("3,Sin(1),Pi,3/4,42,1.2", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 8,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{3,Sin(1),Pi,3/4,42,1.2}\",\r\n"
        + "        \"sinput\" : \"{3,Sin(1),Pi,3/4,42,1.2`}\",\r\n"
        + "        \"latex\" : \"\\\\{3,\\\\sin (1),\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Total\",\r\n"
        + "      \"scanner\" : \"List\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"50.93306363839769\",\r\n"
        + "        \"sinput\" : \"Total({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n"
        + "        \"latex\" : \"50.93306\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Vector length\",\r\n"
        + "      \"scanner\" : \"List\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"42.256125920620825\",\r\n"
        + "        \"sinput\" : \"Norm({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n"
        + "        \"latex\" : \"42.25613\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Normalized vector\",\r\n"
        + "      \"scanner\" : \"List\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{0.0709956233478567,0.01991358569852382,0.07434644291555152,0.017748905836964174,0.9939387268699937,0.028398249339142676}\",\r\n"
        + "        \"sinput\" : \"Normalize({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n"
        + "        \"latex\" : \"\\\\{0.0709956,0.0199136,0.0743464,0.0177489,0.993939,0.0283982\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Plot points\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"ListPlot({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9,44.6125,7.9,-1.8624999999999998]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 0.8414709848078965;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.141592653589793;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 0.75;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 42.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 6;},function() {return 1.2;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Five-number summary\",\r\n"
        + "      \"scanner\" : \"Statistics\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{0.75,0.8414709848078965,2.1,3.141592653589793,42.0}\",\r\n"
        + "        \"sinput\" : \"FiveNum({3,Sin(1),Pi,3/4,42,1.2`})\",\r\n"
        + "        \"latex\" : \"\\\\{0.75,0.841471,2.1,3.14159,42.0\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Histogram\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Histogram({3,Sin(1),Pi,3/4,42,1.2`})\",\r\n"
        + "        \"plotly\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;Plotly&lt;/title&gt;\\n\\n   &lt;script src=&quot;https://cdn.plot.ly/plotly-latest.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n&lt;div id='plotly' &gt;&lt;/div&gt;\\n    &lt;script&gt;\\r\\n        var target_plotly = document.getElementById('plotly');\\r\\n        var layout = {\\r\\n    autosize: true,\\r\\n\\r\\n\\r\\n};\\r\\n\\r\\nvar trace0 =\\r\\n{\\r\\nx: [&quot;3.0&quot;,&quot;0.8414709848078965&quot;,&quot;3.141592653589793&quot;,&quot;0.75&quot;,&quot;42.0&quot;,&quot;1.2&quot;],\\r\\nopacity: '1.0',\\r\\nnbinsx: 0,\\r\\nautobinx: false,\\r\\nnbinsy: 0,\\r\\nautobiny: false,\\r\\n    histnorm: '',\\r\\n    histfunc: 'count',\\r\\nxaxis: 'x',\\r\\nyaxis: 'y',\\r\\ntype: 'histogram',\\r\\nname: '',\\r\\n};\\r\\n\\r\\n        var data = [ trace0];\\r\\nPlotly.newPlot(target_plotly, data, layout);            &lt;/script&gt;\\r\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Alternate form\",\r\n"
        + "      \"scanner\" : \"Simplification\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{3,(I*1/2)/E^I-I*1/2*E^I,Pi,3/4,42,1.2}\",\r\n"
        + "        \"sinput\" : \"TrigToExp({3,Sin(1),Pi,3/4,42,1.2`})\",\r\n"
        + "        \"latex\" : \"\\\\{3,\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i }} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\cdot {e}^{i },\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testListPlot002() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON =
        TestPods.createJUnitResult("Table({Sin(t*0.33), Cos(t*1.1)}, {t, 100})", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Table({Sin(t*0.33),Cos(t*1.1)},{t,100})\",\r\n"
        + "        \"sinput\" : \"Table({Sin(t*0.33`),Cos(t*1.1`)},{t,100})\",\r\n"
        + "        \"latex\" : \"\\\\text{Table}(\\\\{\\\\sin (t\\\\cdot 0.33),\\\\cos (t\\\\cdot 1.1)\\\\},\\\\{t,100\\\\})\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Plot points\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"ListPlot({{0.32404302839486837,0.4535961214255773},\\n {0.6131168519734338,-0.5885011172553458},\\n {0.8360259786005205,-0.9874797699088649},\\n {0.9687151001182652,-0.30733286997841935},\\n {0.9968650284539189,0.70866977429126},\\n {0.9174379552818098,0.9502325919585293},\\n {0.7390052780594708,0.15337386203786346},\\n {0.48082261498864826,-0.811093014061656},\\n {0.17075182895114532,-0.8891911526253609},\\n {-0.15774569414324865,0.004425697988050785},\\n {-0.4692200412887275,0.8932061115093233},\\n {-0.7300583608392995,0.8058839576404497},\\n {-0.9121122039130803,-0.16211443649971827},\\n {-0.9957351730622453,-0.9529529168871809},\\n {-0.9719030694018208,-0.7023970575027135},\\n {-0.8431877418564167,0.31574375491924334},\\n {-0.6234795452786853,0.9888373426941465},\\n {-0.3364883584585042,0.5813218118144357},\\n {-0.013184925133521251,-0.46146670441591253},\\n {0.3115413635133787,-0.9999608263946371},\\n {0.602647568421973,-0.44569000044433316},\\n {0.8287188723898359,0.5956343152752115},\\n {0.965358719901792,0.9860448308379632},\\n {0.9978215790530743,0.2988979063644682},\\n {0.9226042102393402,-0.7148869687796675},\\n {0.7478237193548898,-0.9474378189567576},\\n {0.49234159776988917,-0.14462127116171977},\\n {0.183728278586583,0.8162385236075724},\\n {-0.14471213527691454,0.8851065280947882},\\n {-0.4575358937753214,-0.013276747223059479},\\n {-0.7209845231142057,-0.897151090185845},\\n {-0.9066278820139979,-0.8006117624589936},\\n {-0.9944322093031953,0.17084230974765666},\\n {-0.9749220735246146,0.9555985806128415},\\n {-0.8502029170863663,0.6960693098638897},\\n {-0.6337338467854989,-0.3241299022175636},\\n {-0.34887519008606005,-0.990117442831766},\\n {-0.026367558070356484,-0.5740969614310336},\\n {0.2989855372260583,0.4693011327771151},\\n {0.5920735147072245,0.9998433086476912},\\n {0.8212676935633646,0.43774896089470705},\\n {0.9618345122584528,-0.6027208470078607},\\n {0.9986046585635748,-0.9845326379049143},\\n {0.9276100706332453,-0.2904395249332599},\\n {0.756512151641241,0.7210481538680871},\\n {0.5037749870595187,0.9445688168445349},\\n {0.19667278709629893,0.1358573496123707},\\n {-0.13165341823383273,-0.8213200831418752},\\n {-0.4457722037352182,-0.8809525579365433},\\n {-0.711785342369123,0.022126756261962838},\\n {-0.900985943032865,0.901025779576851},\\n {-0.9929563636967662,0.7952768415790757},\\n {-0.9777715876333635,-0.17955679797714888},\\n {-0.857070284703512,-0.9581693758551366},\\n {-0.6438779737855393,-0.6896870271361613},\\n {-0.36120136982925244,0.3324906548421391},\\n {-0.03954560701231674,0.9913199700294487},\\n {0.2863777323608796,0.5668271321520202},\\n {0.5813965291263834,-0.47709879270357103},\\n {0.8136737375071054,-0.99964745596635},\\n {0.9581430898710656,-0.42977362493499033},\\n {0.9992141308471991,0.6097601572433005},\\n {0.9324546661956634,0.9829433095858163},\\n {0.7650690644362526,0.281958388375392},\\n {0.515120795165023,-0.7271528468448446},\\n {0.20958310407999373,-0.9416258104001715},\\n {-0.11857181326943754,-0.1270827840186229},\\n {-0.433931016283655,0.8263372945385548},\\n {-0.7024624178798466,0.876729567602604},\\n {-0.8951873678196818,-0.03097503173121646},\\n {-0.9913078928184317,-0.9048298761112383},\\n {-0.9804511163405908,-0.7898796129768653},\\n {-0.8637886508173204,0.18825721843235974},\\n {-0.6539101627242901,0.9606651011994307},\\n {-0.3734647547841147,0.6832507093535931},\\n {-0.052716780958143236,-0.3408253577513085},\\n {0.2737201407822824,-0.9924448300725429},\\n {0.5706184678713274,-0.5595128935482332},\\n {0.805938324428851,0.48485907327037797},\\n {0.954285094492698,0.9993732836951247},\\n {0.9996498899473084,0.4217646174105228},\\n {0.9371371546945932,-0.6167516944712085},\\n {0.7734929701222879,-0.9812769704001121},\\n {0.5263770496198482,-0.27345516116425417},\\n {0.2224569850815534,0.7332005694242952},\\n {-0.1054695946182271,0.9386090302000182},\\n {-0.42201439000878305,0.118298261843216},\\n {-0.6930173704349996,-0.8312897647130846},\\n {-0.889233164455629,-0.8724378879524822},\\n {-0.9894870832545356,0.039820880393153096},\\n {-0.9829601938107485,0.9085630817486479},\\n {-0.8703568474411396,0.784420499510169},\\n {-0.6638286695076421,-0.19694288945960042},\\n {-0.38566321296353945,-0.9630855611126041},\\n {-0.0658787901017895,-0.6767608607837051},\\n {0.26101496301011606,0.3491333579443536},\\n {0.5597412047059207,0.9934919348314017},\\n {0.7980627991286724,0.5521548186698774},\\n {0.9502611968351016,-0.4925813664811991},\\n {0.9999118601072672,-0.9990208133146474}})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.6455175247207208,1.6498335153998076,1.6496942117657427,-1.6499510331467535]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 0.32404302839486837;},function() {return 0.4535961214255773;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.6131168519734338;},function() {return -0.5885011172553458;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8360259786005205;},function() {return -0.9874797699088649;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9687151001182652;},function() {return -0.30733286997841935;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9968650284539189;},function() {return 0.70866977429126;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9174379552818098;},function() {return 0.9502325919585293;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7390052780594708;},function() {return 0.15337386203786346;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.48082261498864826;},function() {return -0.811093014061656;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.17075182895114532;},function() {return -0.8891911526253609;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.15774569414324865;},function() {return 0.004425697988050785;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.4692200412887275;},function() {return 0.8932061115093233;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7300583608392995;},function() {return 0.8058839576404497;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9121122039130803;},function() {return -0.16211443649971827;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9957351730622453;},function() {return -0.9529529168871809;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9719030694018208;},function() {return -0.7023970575027135;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8431877418564167;},function() {return 0.31574375491924334;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6234795452786853;},function() {return 0.9888373426941465;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.3364883584585042;},function() {return 0.5813218118144357;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.013184925133521251;},function() {return -0.46146670441591253;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.3115413635133787;},function() {return -0.9999608263946371;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.602647568421973;},function() {return -0.44569000044433316;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8287188723898359;},function() {return 0.5956343152752115;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.965358719901792;},function() {return 0.9860448308379632;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9978215790530743;},function() {return 0.2988979063644682;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9226042102393402;},function() {return -0.7148869687796675;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7478237193548898;},function() {return -0.9474378189567576;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.49234159776988917;},function() {return -0.14462127116171977;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.183728278586583;},function() {return 0.8162385236075724;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.14471213527691454;},function() {return 0.8851065280947882;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.4575358937753214;},function() {return -0.013276747223059479;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7209845231142057;},function() {return -0.897151090185845;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9066278820139979;},function() {return -0.8006117624589936;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9944322093031953;},function() {return 0.17084230974765666;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9749220735246146;},function() {return 0.9555985806128415;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8502029170863663;},function() {return 0.6960693098638897;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6337338467854989;},function() {return -0.3241299022175636;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.34887519008606005;},function() {return -0.990117442831766;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.026367558070356484;},function() {return -0.5740969614310336;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2989855372260583;},function() {return 0.4693011327771151;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5920735147072245;},function() {return 0.9998433086476912;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8212676935633646;},function() {return 0.43774896089470705;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9618345122584528;},function() {return -0.6027208470078607;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9986046585635748;},function() {return -0.9845326379049143;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9276100706332453;},function() {return -0.2904395249332599;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.756512151641241;},function() {return 0.7210481538680871;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5037749870595187;},function() {return 0.9445688168445349;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.19667278709629893;},function() {return 0.1358573496123707;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.13165341823383273;},function() {return -0.8213200831418752;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.4457722037352182;},function() {return -0.8809525579365433;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.711785342369123;},function() {return 0.022126756261962838;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.900985943032865;},function() {return 0.901025779576851;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9929563636967662;},function() {return 0.7952768415790757;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9777715876333635;},function() {return -0.17955679797714888;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.857070284703512;},function() {return -0.9581693758551366;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6438779737855393;},function() {return -0.6896870271361613;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.36120136982925244;},function() {return 0.3324906548421391;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.03954560701231674;},function() {return 0.9913199700294487;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2863777323608796;},function() {return 0.5668271321520202;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5813965291263834;},function() {return -0.47709879270357103;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8136737375071054;},function() {return -0.99964745596635;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9581430898710656;},function() {return -0.42977362493499033;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9992141308471991;},function() {return 0.6097601572433005;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9324546661956634;},function() {return 0.9829433095858163;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7650690644362526;},function() {return 0.281958388375392;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.515120795165023;},function() {return -0.7271528468448446;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.20958310407999373;},function() {return -0.9416258104001715;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.11857181326943754;},function() {return -0.1270827840186229;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.433931016283655;},function() {return 0.8263372945385548;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7024624178798466;},function() {return 0.876729567602604;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8951873678196818;},function() {return -0.03097503173121646;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9913078928184317;},function() {return -0.9048298761112383;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9804511163405908;},function() {return -0.7898796129768653;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8637886508173204;},function() {return 0.18825721843235974;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6539101627242901;},function() {return 0.9606651011994307;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.3734647547841147;},function() {return 0.6832507093535931;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.052716780958143236;},function() {return -0.3408253577513085;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2737201407822824;},function() {return -0.9924448300725429;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5706184678713274;},function() {return -0.5595128935482332;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.805938324428851;},function() {return 0.48485907327037797;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.954285094492698;},function() {return 0.9993732836951247;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9996498899473084;},function() {return 0.4217646174105228;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9371371546945932;},function() {return -0.6167516944712085;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7734929701222879;},function() {return -0.9812769704001121;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5263770496198482;},function() {return -0.27345516116425417;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2224569850815534;},function() {return 0.7332005694242952;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.1054695946182271;},function() {return 0.9386090302000182;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.42201439000878305;},function() {return 0.118298261843216;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6930173704349996;},function() {return -0.8312897647130846;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.889233164455629;},function() {return -0.8724378879524822;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9894870832545356;},function() {return 0.039820880393153096;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9829601938107485;},function() {return 0.9085630817486479;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8703568474411396;},function() {return 0.784420499510169;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6638286695076421;},function() {return -0.19694288945960042;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.38566321296353945;},function() {return -0.9630855611126041;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.0658787901017895;},function() {return -0.6767608607837051;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.26101496301011606;},function() {return 0.3491333579443536;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5597412047059207;},function() {return 0.9934919348314017;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7980627991286724;},function() {return 0.5521548186698774;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9502611968351016;},function() {return -0.4925813664811991;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9999118601072672;},function() {return -0.9990208133146474;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{{0.32404302839486837,0.4535961214255773},\\n {0.6131168519734338,-0.5885011172553458},\\n {0.8360259786005205,-0.9874797699088649},\\n {0.9687151001182652,-0.30733286997841935},\\n {0.9968650284539189,0.70866977429126},\\n {0.9174379552818098,0.9502325919585293},\\n {0.7390052780594708,0.15337386203786346},\\n {0.48082261498864826,-0.811093014061656},\\n {0.17075182895114532,-0.8891911526253609},\\n {-0.15774569414324865,0.004425697988050785},\\n {-0.4692200412887275,0.8932061115093233},\\n {-0.7300583608392995,0.8058839576404497},\\n {-0.9121122039130803,-0.16211443649971827},\\n {-0.9957351730622453,-0.9529529168871809},\\n {-0.9719030694018208,-0.7023970575027135},\\n {-0.8431877418564167,0.31574375491924334},\\n {-0.6234795452786853,0.9888373426941465},\\n {-0.3364883584585042,0.5813218118144357},\\n {-0.013184925133521251,-0.46146670441591253},\\n {0.3115413635133787,-0.9999608263946371},\\n {0.602647568421973,-0.44569000044433316},\\n {0.8287188723898359,0.5956343152752115},\\n {0.965358719901792,0.9860448308379632},\\n {0.9978215790530743,0.2988979063644682},\\n {0.9226042102393402,-0.7148869687796675},\\n {0.7478237193548898,-0.9474378189567576},\\n {0.49234159776988917,-0.14462127116171977},\\n {0.183728278586583,0.8162385236075724},\\n {-0.14471213527691454,0.8851065280947882},\\n {-0.4575358937753214,-0.013276747223059479},\\n {-0.7209845231142057,-0.897151090185845},\\n {-0.9066278820139979,-0.8006117624589936},\\n {-0.9944322093031953,0.17084230974765666},\\n {-0.9749220735246146,0.9555985806128415},\\n {-0.8502029170863663,0.6960693098638897},\\n {-0.6337338467854989,-0.3241299022175636},\\n {-0.34887519008606005,-0.990117442831766},\\n {-0.026367558070356484,-0.5740969614310336},\\n {0.2989855372260583,0.4693011327771151},\\n {0.5920735147072245,0.9998433086476912},\\n {0.8212676935633646,0.43774896089470705},\\n {0.9618345122584528,-0.6027208470078607},\\n {0.9986046585635748,-0.9845326379049143},\\n {0.9276100706332453,-0.2904395249332599},\\n {0.756512151641241,0.7210481538680871},\\n {0.5037749870595187,0.9445688168445349},\\n {0.19667278709629893,0.1358573496123707},\\n {-0.13165341823383273,-0.8213200831418752},\\n {-0.4457722037352182,-0.8809525579365433},\\n {-0.711785342369123,0.022126756261962838},\\n {-0.900985943032865,0.901025779576851},\\n {-0.9929563636967662,0.7952768415790757},\\n {-0.9777715876333635,-0.17955679797714888},\\n {-0.857070284703512,-0.9581693758551366},\\n {-0.6438779737855393,-0.6896870271361613},\\n {-0.36120136982925244,0.3324906548421391},\\n {-0.03954560701231674,0.9913199700294487},\\n {0.2863777323608796,0.5668271321520202},\\n {0.5813965291263834,-0.47709879270357103},\\n {0.8136737375071054,-0.99964745596635},\\n {0.9581430898710656,-0.42977362493499033},\\n {0.9992141308471991,0.6097601572433005},\\n {0.9324546661956634,0.9829433095858163},\\n {0.7650690644362526,0.281958388375392},\\n {0.515120795165023,-0.7271528468448446},\\n {0.20958310407999373,-0.9416258104001715},\\n {-0.11857181326943754,-0.1270827840186229},\\n {-0.433931016283655,0.8263372945385548},\\n {-0.7024624178798466,0.876729567602604},\\n {-0.8951873678196818,-0.03097503173121646},\\n {-0.9913078928184317,-0.9048298761112383},\\n {-0.9804511163405908,-0.7898796129768653},\\n {-0.8637886508173204,0.18825721843235974},\\n {-0.6539101627242901,0.9606651011994307},\\n {-0.3734647547841147,0.6832507093535931},\\n {-0.052716780958143236,-0.3408253577513085},\\n {0.2737201407822824,-0.9924448300725429},\\n {0.5706184678713274,-0.5595128935482332},\\n {0.805938324428851,0.48485907327037797},\\n {0.954285094492698,0.9993732836951247},\\n {0.9996498899473084,0.4217646174105228},\\n {0.9371371546945932,-0.6167516944712085},\\n {0.7734929701222879,-0.9812769704001121},\\n {0.5263770496198482,-0.27345516116425417},\\n {0.2224569850815534,0.7332005694242952},\\n {-0.1054695946182271,0.9386090302000182},\\n {-0.42201439000878305,0.118298261843216},\\n {-0.6930173704349996,-0.8312897647130846},\\n {-0.889233164455629,-0.8724378879524822},\\n {-0.9894870832545356,0.039820880393153096},\\n {-0.9829601938107485,0.9085630817486479},\\n {-0.8703568474411396,0.784420499510169},\\n {-0.6638286695076421,-0.19694288945960042},\\n {-0.38566321296353945,-0.9630855611126041},\\n {-0.0658787901017895,-0.6767608607837051},\\n {0.26101496301011606,0.3491333579443536},\\n {0.5597412047059207,0.9934919348314017},\\n {0.7980627991286724,0.5521548186698774},\\n {0.9502611968351016,-0.4925813664811991},\\n {0.9999118601072672,-0.9990208133146474}}\",\r\n"
        + "        \"sinput\" : \"Table({Sin(t*0.33`),Cos(t*1.1`)},{t,100})\",\r\n"
        + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n0.324043 & 0.453596 \\\\\\\\\\n0.613117 & -0.588501 \\\\\\\\\\n0.836026 & -0.98748 \\\\\\\\\\n0.968715 & -0.307333 \\\\\\\\\\n0.996865 & 0.70867 \\\\\\\\\\n0.917438 & 0.950233 \\\\\\\\\\n0.739005 & 0.153374 \\\\\\\\\\n0.480823 & -0.811093 \\\\\\\\\\n0.170752 & -0.889191 \\\\\\\\\\n-0.157746 & 0.0044257 \\\\\\\\\\n-0.46922 & 0.893206 \\\\\\\\\\n-0.730058 & 0.805884 \\\\\\\\\\n-0.912112 & -0.162114 \\\\\\\\\\n-0.995735 & -0.952953 \\\\\\\\\\n-0.971903 & -0.702397 \\\\\\\\\\n-0.843188 & 0.315744 \\\\\\\\\\n-0.62348 & 0.988837 \\\\\\\\\\n-0.336488 & 0.581322 \\\\\\\\\\n-0.0131849 & -0.461467 \\\\\\\\\\n0.311541 & -0.999961 \\\\\\\\\\n0.602648 & -0.44569 \\\\\\\\\\n0.828719 & 0.595634 \\\\\\\\\\n0.965359 & 0.986045 \\\\\\\\\\n0.997822 & 0.298898 \\\\\\\\\\n0.922604 & -0.714887 \\\\\\\\\\n0.747824 & -0.947438 \\\\\\\\\\n0.492342 & -0.144621 \\\\\\\\\\n0.183728 & 0.816239 \\\\\\\\\\n-0.144712 & 0.885107 \\\\\\\\\\n-0.457536 & -0.0132767 \\\\\\\\\\n-0.720985 & -0.897151 \\\\\\\\\\n-0.906628 & -0.800612 \\\\\\\\\\n-0.994432 & 0.170842 \\\\\\\\\\n-0.974922 & 0.955599 \\\\\\\\\\n-0.850203 & 0.696069 \\\\\\\\\\n-0.633734 & -0.32413 \\\\\\\\\\n-0.348875 & -0.990117 \\\\\\\\\\n-0.0263676 & -0.574097 \\\\\\\\\\n0.298986 & 0.469301 \\\\\\\\\\n0.592074 & 0.999843 \\\\\\\\\\n0.821268 & 0.437749 \\\\\\\\\\n0.961835 & -0.602721 \\\\\\\\\\n0.998605 & -0.984533 \\\\\\\\\\n0.92761 & -0.29044 \\\\\\\\\\n0.756512 & 0.721048 \\\\\\\\\\n0.503775 & 0.944569 \\\\\\\\\\n0.196673 & 0.135857 \\\\\\\\\\n-0.131653 & -0.82132 \\\\\\\\\\n-0.445772 & -0.880953 \\\\\\\\\\n-0.711785 & 0.0221268 \\\\\\\\\\n-0.900986 & 0.901026 \\\\\\\\\\n-0.992956 & 0.795277 \\\\\\\\\\n-0.977772 & -0.179557 \\\\\\\\\\n-0.85707 & -0.958169 \\\\\\\\\\n-0.643878 & -0.689687 \\\\\\\\\\n-0.361201 & 0.332491 \\\\\\\\\\n-0.0395456 & 0.99132 \\\\\\\\\\n0.286378 & 0.566827 \\\\\\\\\\n0.581397 & -0.477099 \\\\\\\\\\n0.813674 & -0.999647 \\\\\\\\\\n0.958143 & -0.429774 \\\\\\\\\\n0.999214 & 0.60976 \\\\\\\\\\n0.932455 & 0.982943 \\\\\\\\\\n0.765069 & 0.281958 \\\\\\\\\\n0.515121 & -0.727153 \\\\\\\\\\n0.209583 & -0.941626 \\\\\\\\\\n-0.118572 & -0.127083 \\\\\\\\\\n-0.433931 & 0.826337 \\\\\\\\\\n-0.702462 & 0.87673 \\\\\\\\\\n-0.895187 & -0.030975 \\\\\\\\\\n-0.991308 & -0.90483 \\\\\\\\\\n-0.980451 & -0.78988 \\\\\\\\\\n-0.863789 & 0.188257 \\\\\\\\\\n-0.65391 & 0.960665 \\\\\\\\\\n-0.373465 & 0.683251 \\\\\\\\\\n-0.0527168 & -0.340825 \\\\\\\\\\n0.27372 & -0.992445 \\\\\\\\\\n0.570618 & -0.559513 \\\\\\\\\\n0.805938 & 0.484859 \\\\\\\\\\n0.954285 & 0.999373 \\\\\\\\\\n0.99965 & 0.421765 \\\\\\\\\\n0.937137 & -0.616752 \\\\\\\\\\n0.773493 & -0.981277 \\\\\\\\\\n0.526377 & -0.273455 \\\\\\\\\\n0.222457 & 0.733201 \\\\\\\\\\n-0.10547 & 0.938609 \\\\\\\\\\n-0.422014 & 0.118298 \\\\\\\\\\n-0.693017 & -0.83129 \\\\\\\\\\n-0.889233 & -0.872438 \\\\\\\\\\n-0.989487 & 0.0398209 \\\\\\\\\\n-0.98296 & 0.908563 \\\\\\\\\\n-0.870357 & 0.78442 \\\\\\\\\\n-0.663829 & -0.196943 \\\\\\\\\\n-0.385663 & -0.963086 \\\\\\\\\\n-0.0658788 & -0.676761 \\\\\\\\\\n0.261015 & 0.349133 \\\\\\\\\\n0.559741 & 0.993492 \\\\\\\\\\n0.798063 & 0.552155 \\\\\\\\\\n0.950261 & -0.492581 \\\\\\\\\\n0.999912 & -0.999021 \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testListPlot004() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON =
        TestPods.createJUnitResult("1, 2, 3, None, 3, 5, f(), 2, 1, foo, 2, 3", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 3,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{1,2,3,None,3,5,f(),2,1,foo,2,3}\",\r\n"
            + "        \"sinput\" : \"{1,2,3,None,3,5,f(),2,1,foo,2,3}\",\r\n"
            + "        \"latex\" : \"\\\\{1,2,3,None,3,5,f(),2,1,foo,2,3\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Five-number summary\",\r\n"
            + "      \"scanner\" : \"Statistics\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{Min(1,foo,None,f()),5/2,5/2,1/2*(2-f())+f(),Max(5,foo,None,f())}\",\r\n"
            + "        \"sinput\" : \"FiveNum({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\text{Min}(1,foo,None,f()),\\\\frac{5}{2},\\\\frac{5}{2},\\\\frac{2 - f()}{2}+f(),\\\\text{Max}(5,foo,None,f())\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Histogram\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"Histogram({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\r\n"
            + "        \"plotly\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;Plotly&lt;/title&gt;\\n\\n   &lt;script src=&quot;https://cdn.plot.ly/plotly-latest.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n&lt;div id='plotly' &gt;&lt;/div&gt;\\n    &lt;script&gt;\\r\\n        var target_plotly = document.getElementById('plotly');\\r\\n        var layout = {\\r\\n    autosize: true,\\r\\n\\r\\n\\r\\n};\\r\\n\\r\\nvar trace0 =\\r\\n{\\r\\nx: [&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;,&quot;3.0&quot;,&quot;5.0&quot;,&quot;2.0&quot;,&quot;1.0&quot;,&quot;2.0&quot;,&quot;3.0&quot;],\\r\\nopacity: '1.0',\\r\\nnbinsx: 0,\\r\\nautobinx: false,\\r\\nnbinsy: 0,\\r\\nautobiny: false,\\r\\n    histnorm: '',\\r\\n    histfunc: 'count',\\r\\nxaxis: 'x',\\r\\nyaxis: 'y',\\r\\ntype: 'histogram',\\r\\nname: '',\\r\\n};\\r\\n\\r\\n        var data = [ trace0];\\r\\nPlotly.newPlot(target_plotly, data, layout);            &lt;/script&gt;\\r\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testListPlot005() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();

    ObjectNode messageJSON = TestPods.createJUnitResult("1, 2, 3, 4, 5", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 5,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{1,2,3,4,5}\",\r\n"
            + "        \"sinput\" : \"{1,2,3,4,5}\",\r\n"
            + "        \"latex\" : \"\\\\{1,2,3,4,5\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Total\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"15\",\r\n"
            + "        \"sinput\" : \"Total({1,2,3,4,5})\",\r\n"
            + "        \"latex\" : \"15\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Vector length\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"7.416198487095663\",\r\n"
            + "        \"sinput\" : \"N(Norm({1,2,3,4,5}))\",\r\n"
            + "        \"latex\" : \"7.4162\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Normalized vector\",\r\n"
            + "      \"scanner\" : \"List\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"{1/Sqrt(55),2/Sqrt(55),3/Sqrt(55),4/Sqrt(55),Sqrt(5/11)}\",\r\n"
            + "        \"sinput\" : \"Normalize({1,2,3,4,5})\",\r\n"
            + "        \"latex\" : \"\\\\{\\\\frac{1}{\\\\sqrt{55}},\\\\frac{2}{\\\\sqrt{55}},\\\\frac{3}{\\\\sqrt{55}},\\\\frac{4}{\\\\sqrt{55}},\\\\sqrt{\\\\frac{5}{11}}\\\\}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Plot points\",\r\n"
            + "      \"scanner\" : \"Plotter\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"sinput\" : \"ListPlot({1.0,2.0,3.0,4.0,5.0})\",\r\n"
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.85,5.75,6.85,0.25]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 4.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 5.0;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testListPlot003() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("plot ([x,x**2,x**3,x**4])", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 5,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"Plot({x,x^2,x^3,x^4})\",\r\n"
        + "        \"sinput\" : \"Plot({x,x^2,x^3,x^4})\",\r\n"
        + "        \"latex\" : \"\\\\text{Plot}(\\\\{x,{x}^{2},{x}^{3},{x}^{4}\\\\})\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{x,x^2,x^3,x^4}\",\r\n"
        + "        \"sinput\" : \"Plot({x,x^2,x^3,x^4})\",\r\n"
        + "        \"latex\" : \"\\\\{x,{x}^{2},{x}^{3},{x}^{4}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot({x,x^2,x^3,x^4},{x,-7.0`,7.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,1911.6055445184015,8.25,-399.3420551104002]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [pow(x,3)];} catch(e) { return Number.NaN;} }\\nfunction $f4(x) { try { return [pow(x,4)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0, 7.0],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, -7.0, 7.0],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f4, -7.0, 7.0],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Derivative\",\r\n"
        + "      \"scanner\" : \"Derivative\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{1,2*x,3*x^2,4*x^3}\",\r\n"
        + "        \"sinput\" : \"D({x,x^2,x^3,x^4},x)\",\r\n"
        + "        \"latex\" : \"\\\\{1,2\\\\cdot x,3\\\\cdot {x}^{2},4\\\\cdot {x}^{3}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Indefinite integral\",\r\n"
        + "      \"scanner\" : \"Integral\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"{x^2/2,x^3/3,x^4/4,x^5/5}\",\r\n"
        + "        \"sinput\" : \"Integrate({x,x^2,x^3,x^4},x)\",\r\n"
        + "        \"latex\" : \"\\\\{\\\\frac{{x}^{2}}{2},\\\\frac{{x}^{3}}{3},\\\\frac{{x}^{4}}{4},\\\\frac{{x}^{5}}{5}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testGraph001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    // ObjectNode messageJSON = Pods.createResult("TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))",
    // formatsTEX);
    ObjectNode messageJSON =
        TestPods.createJUnitResult( //
            "Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)})", //
            formatsTEX);

    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"Graph({1-&gt;2,2-&gt;3,3-&gt;1,3-&gt;4,4-&gt;5,5-&gt;3})\",\r\n"
            + "        \"sinput\" : \"Graph({1-&gt;2,2-&gt;3,3-&gt;1,3-&gt;4,4-&gt;5,5-&gt;3})\",\r\n"
            + "        \"latex\" : \"\\\\text{Graph}(\\\\{1\\\\to 2,2\\\\to 3,3\\\\to 1,3\\\\to 4,4\\\\to 5,5\\\\to 3\\\\})\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Graph data\",\r\n"
            + "      \"scanner\" : \"Graph\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"visjs\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;VIS-NetWork&lt;/title&gt;\\n\\n  &lt;script type=&quot;text/javascript&quot; src=&quot;https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n\\n&lt;div id=&quot;vis&quot; style=&quot;width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script type=&quot;text/javascript&quot;&gt;\\n&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;VIS-NetWork&lt;/title&gt;\\n\\n  &lt;script type=&quot;text/javascript&quot; src=&quot;https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js&quot;&gt;&lt;/script&gt;\\n&lt;/head&gt;\\n&lt;body&gt;\\n\\n&lt;div id=&quot;vis&quot; style=&quot;width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script type=&quot;text/javascript&quot;&gt;\\nvar nodes = new vis.DataSet([\\n  {id: 1, label: '1'}\\n, {id: 2, label: '2'}\\n, {id: 3, label: '3'}\\n, {id: 4, label: '4'}\\n, {id: 5, label: '5'}\\n]);\\nvar edges = new vis.DataSet([\\n  {from: 1, to: 2 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 2, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 3, to: 1 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 3, to: 4 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 4, to: 5 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n, {from: 5, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\\n]);\\n\\n  var container = document.getElementById('vis');\\n  var data = {\\n    nodes: nodes,\\n    edges: edges\\n  };\\n  var options = { };\\n\\n  var network = new vis.Network(container, data, options);\\n&lt;/script&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\n  var container = document.getElementById('vis');\\n  var data = {\\n    nodes: nodes,\\n    edges: edges\\n  };\\n  var options = {\\n\\t\\t  edges: {\\n              smooth: {\\n                  type: 'cubicBezier',\\n                  forceDirection:  'vertical',\\n                  roundness: 0.4\\n              }\\n          },\\n          layout: {\\n              hierarchical: {\\n                  direction: &quot;UD&quot;\\n              }\\n          },\\n          nodes: {\\n            shape: 'box'\\n          },\\n          physics:false\\n      }; \\n  var network = new vis.Network(container, data, options);\\n&lt;/script&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  //
  @Test
  public void testHornerForm() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    String s = System.getProperty("os.name");
    ObjectNode messageJSON = TestPods.createJUnitResult("HornerForm(x^2+x^3+2*x^14)", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 7,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"HornerForm(x^2+x^3+2*x^14)\",\r\n"
        + "        \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\r\n"
        + "        \"latex\" : \"\\\\text{HornerForm}({x}^{2}+{x}^{3} + 2\\\\cdot {x}^{14})\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Result\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"x^2*(1+x*(1+2*x^11))\",\r\n"
        + "        \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\r\n"
        + "        \"latex\" : \"{x}^{2}\\\\,\\\\left( 1 + x\\\\,\\\\left( 1 + 2\\\\cdot {x}^{11}\\\\right) \\\\right) \"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot(x^2*(1 + x*(1 + 2*x^11)),{x,-7.0`,7.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,6.303108521964081E11,8.25,-5.989379138567678E10]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [mul(pow(x,2),add(1,mul(x,add(1,mul(2,pow(x,11))))))];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Factor\",\r\n"
        + "      \"scanner\" : \"Polynomial\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"x^2*(1+x+2*x^12)\",\r\n"
        + "        \"sinput\" : \"Factor(x^2*(1 + x*(1 + 2*x^11)))\",\r\n"
        + "        \"latex\" : \"{x}^{2}\\\\,\\\\left( 1+x + 2\\\\cdot {x}^{12}\\\\right) \"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"GlobalExtrema\",\r\n"
        + "      \"scanner\" : \"GlobalMinimum\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"min{x^2*(1+x*(1+2*x^11))} = 0 at x = 0\",\r\n"
        + "        \"sinput\" : \"Minimize(x^2*(1 + x*(1 + 2*x^11)),x)\",\r\n"
        + "        \"latex\" : \"\\\\{0,\\\\{x\\\\to 0\\\\}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Derivative\",\r\n"
        + "      \"scanner\" : \"Derivative\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"x^2*(1+24*x^11)+2*x*(1+x*(1+2*x^11))\",\r\n"
        + "        \"sinput\" : \"D(x^2*(1 + x*(1 + 2*x^11)),x)\",\r\n"
        + "        \"latex\" : \"{x}^{2}\\\\,\\\\left( 1 + 24\\\\cdot {x}^{11}\\\\right)  + 2\\\\cdot x\\\\cdot \\\\left( 1 + x\\\\,\\\\left( 1 + 2\\\\cdot {x}^{11}\\\\right) \\\\right) \"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Indefinite integral\",\r\n"
        + "      \"scanner\" : \"Integral\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"x^3/3+x^4/4+2/15*x^15\",\r\n"
        + "        \"sinput\" : \"Integrate(x^2*(1 + x*(1 + 2*x^11)),x)\",\r\n"
        + "        \"latex\" : \"\\\\frac{{x}^{3}}{3}+\\\\frac{{x}^{4}}{4}+\\\\frac{2\\\\cdot {x}^{15}}{15}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testETimesPi001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));

    ObjectNode messageJSON =
        TestPods.createJUnitResult( //
            "E*Pi", //
            formatsTEX);

    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"E*Pi\",\r\n"
            + "        \"sinput\" : \"E*Pi\",\r\n"
            + "        \"latex\" : \"e\\\\,\\\\pi\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Decimal form\",\r\n"
            + "      \"scanner\" : \"Numeric\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"8.539734222673566\",\r\n"
            + "        \"sinput\" : \"N(E*Pi)\",\r\n"
            + "        \"latex\" : \"8.53973\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }

  @Test
  public void testCoshIntegral001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    String s = System.getProperty("os.name");
    ObjectNode messageJSON =
        TestPods.createJUnitResult( //
            "CoshIntegral", //
            formatsTEX);

    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 3,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"CoshIntegral\",\r\n"
        + "        \"sinput\" : \"CoshIntegral\",\r\n"
        + "        \"latex\" : \"CoshIntegral\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Plot\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Manipulate(Plot({Re(CoshIntegral(a*x)),Im(CoshIntegral(a*x))},{x,-2.0`,2.0`},PlotRange-&gt;{-5.0`,5.0`}),{a,1,10})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-2.75,6.05,2.75,-6.05]});\\nboard.suspendUpdate();\\nvar a = board.create('slider',[[-2.2,4.84],[2.2,4.84],[1,1,10]],{name:'a'});\\n\\nfunction $f1(x) { try { return [re(coshIntegral(mul(a.Value(),x)))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [im(coshIntegral(mul(a.Value(),x)))];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -2.0, 2.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -2.0, 2.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Documentation\",\r\n"
        + "      \"scanner\" : \"help\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"markdown\" : \"## CoshIntegral\\n\\n```\\nCoshIntegral(expr)\\n```\\n\\n> returns the hyperbolic cosine integral of `expr`.\\n  \\nSee\\n* [Wikipedia - Trigonometric integral](https://en.wikipedia.org/wiki/Trigonometric_integral)\\n\\n### Examples\\n\\n```\\n>> CoshIntegral(0)\\n-Infinity\\n```\\n \\n\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testPolynomial001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    String s = System.getProperty("os.name");
    ObjectNode messageJSON = TestPods.createJUnitResult("-x^2 + 4*x + 4", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
        + "  \"queryresult\" : {\r\n"
        + "    \"success\" : \"true\",\r\n"
        + "    \"error\" : \"false\",\r\n"
        + "    \"numpods\" : 6,\r\n"
        + "    \"version\" : \"0.1\",\r\n"
        + "    \"pods\" : [ {\r\n"
        + "      \"title\" : \"Input\",\r\n"
        + "      \"scanner\" : \"Identity\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4+4*x-x^2\",\r\n"
        + "        \"sinput\" : \"4 + 4*x - x^2\",\r\n"
        + "        \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Function\",\r\n"
        + "      \"scanner\" : \"Plotter\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"sinput\" : \"Plot(4 + 4*x - x^2,{x,-7.0`,7.0`})\",\r\n"
        + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraph.min.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.4/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/jsxgraphcore.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.2/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,12.598320000000001,8.25,-77.59992]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [add(add(4,mul(4,x)),mul(-1,pow(x,2)))];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Factor\",\r\n"
        + "      \"scanner\" : \"Polynomial\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4+4*x-x^2\",\r\n"
        + "        \"sinput\" : \"Factor(4 + 4*x - x^2)\",\r\n"
        + "        \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"GlobalExtrema\",\r\n"
        + "      \"scanner\" : \"GlobalMaximum\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"max{4+4*x-x^2} = 8 at x = 2\",\r\n"
        + "        \"sinput\" : \"Maximize(4 + 4*x - x^2,x)\",\r\n"
        + "        \"latex\" : \"\\\\{8,\\\\{x\\\\to 2\\\\}\\\\}\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Derivative\",\r\n"
        + "      \"scanner\" : \"Derivative\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4-2*x\",\r\n"
        + "        \"sinput\" : \"D(4 + 4*x - x^2,x)\",\r\n"
        + "        \"latex\" : \"4 - 2\\\\cdot x\"\r\n"
        + "      } ]\r\n"
        + "    }, {\r\n"
        + "      \"title\" : \"Indefinite integral\",\r\n"
        + "      \"scanner\" : \"Integral\",\r\n"
        + "      \"error\" : \"false\",\r\n"
        + "      \"numsubpods\" : 1,\r\n"
        + "      \"subpods\" : [ {\r\n"
        + "        \"plaintext\" : \"4*x+2*x^2-x^3/3\",\r\n"
        + "        \"sinput\" : \"Integrate(4 + 4*x - x^2,x)\",\r\n"
        + "        \"latex\" : \"4\\\\cdot x + 2\\\\cdot {x}^{2}+\\\\frac{ - {x}^{3}}{3}\"\r\n"
        + "      } ]\r\n"
        + "    } ]\r\n"
        + "  }\r\n"
        + "}"); //
  }

  @Test
  public void testRound001() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    EvalEngine.get().resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("1/0", formatsTEX);
    final String jsonStr = messageJSON.toPrettyString();

    assertEquals(
        jsonStr, //
        "{\r\n"
            + "  \"queryresult\" : {\r\n"
            + "    \"success\" : \"true\",\r\n"
            + "    \"error\" : \"false\",\r\n"
            + "    \"numpods\" : 2,\r\n"
            + "    \"version\" : \"0.1\",\r\n"
            + "    \"pods\" : [ {\r\n"
            + "      \"title\" : \"Input\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"1/0\",\r\n"
            + "        \"sinput\" : \"1/0\",\r\n"
            + "        \"latex\" : \"\\\\frac{1}{{0}^{1}}\"\r\n"
            + "      } ]\r\n"
            + "    }, {\r\n"
            + "      \"title\" : \"Result\",\r\n"
            + "      \"scanner\" : \"Identity\",\r\n"
            + "      \"error\" : \"false\",\r\n"
            + "      \"numsubpods\" : 1,\r\n"
            + "      \"subpods\" : [ {\r\n"
            + "        \"plaintext\" : \"ComplexInfinity\",\r\n"
            + "        \"sinput\" : \"1/0\",\r\n"
            + "        \"latex\" : \"ComplexInfinity\"\r\n"
            + "      } ]\r\n"
            + "    } ]\r\n"
            + "  }\r\n"
            + "}"); //
  }
}
