package org.matheclipse.ap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.matheclipse.api.Pods;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestPods {
	final static String[] formats = new String[] { "mathml", "plaintext" };

	@Test
	public void testSyntaxError001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("?#?", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
							"  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"false\",\r\n" + //
							"    \"error\" : \"false\",\r\n" + //
							"    \"numpods\" : 0,\r\n" + //
							"    \"version\" : \"0.1\"\r\n" + //
							"  }\r\n" + //
							"}");//
		}
	}

	@Test
	public void testMarkdownHelp() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Sin", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
					"  \"queryresult\" : {\r\n" + //
					"    \"success\" : \"true\",\r\n" + //
					"    \"error\" : \"false\",\r\n" + //
					"    \"numpods\" : 1,\r\n" + //
					"    \"version\" : \"0.1\",\r\n" + //
					"    \"pods\" : [ {\r\n" + //
					"      \"title\" : \"documentation\",\r\n" + //
					"      \"scanner\" : \"help\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"html\" : \"<h2>Sin</h2>\\n<p>Sin(expr)</p>\\n<blockquote>\\n<p>returns the sine of <code>expr</code> (measured in radians).</p>\\n</blockquote>\\n<p><code>Sin(expr)</code> will evaluate automatically in the case <code>expr</code> is a multiple of <code>Pi, Pi/2, Pi/3, Pi/4</code> and <code>Pi/6</code>.</p>\\n<p>See</p>\\n<ul>\\n<li><a href=\\\"https://en.wikipedia.org/wiki/Sine\\\">Wikipedia - Sine</a></li>\\n</ul>\\n<h3>Examples</h3>\\n<blockquote>\\n<blockquote>\\n<p>Sin(0)\\n0</p>\\n</blockquote>\\n</blockquote>\\n<blockquote>\\n<blockquote>\\n<p>Sin(0.5)\\n0.479425538604203</p>\\n</blockquote>\\n</blockquote>\\n<blockquote>\\n<blockquote>\\n<p>Sin(3*Pi)\\n0</p>\\n</blockquote>\\n</blockquote>\\n<blockquote>\\n<blockquote>\\n<p>Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361</p>\\n</blockquote>\\n</blockquote>\\n\"\r\n" + //
					"      } ]\r\n" + //
					"    } ]\r\n" + //
					"  }\r\n" + //
					"}");//
		}
	}

	@Test
	public void testInteger17() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("17", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + "  \"queryresult\" : {\r\n" + "    \"success\" : \"true\",\r\n"
							+ "    \"error\" : \"false\",\r\n" + "    \"numpods\" : 6,\r\n"
							+ "    \"version\" : \"0.1\",\r\n" + "    \"pods\" : [ {\r\n"
							+ "      \"title\" : \"Binary form\",\r\n" + "      \"scanner\" : \"Integer\",\r\n"
							+ "      \"error\" : \"false\",\r\n" + "      \"numsubpods\" : 1,\r\n"
							+ "      \"subpods\" : [ {\r\n" + "        \"plaintext\" : \"Subscript(10001,2)\",\r\n"
							+ "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><msub><mtext>10001</mtext><mn>2</mn></msub></math>\"\r\n"
							+ "      } ]\r\n" + "    }, {\r\n" + "      \"title\" : \"Prime factorization\",\r\n"
							+ "      \"scanner\" : \"Integer\",\r\n" + "      \"error\" : \"false\",\r\n"
							+ "      \"numsubpods\" : 1,\r\n" + "      \"subpods\" : [ {\r\n"
							+ "        \"plaintext\" : \"{{17,1}}\",\r\n"
							+ "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mrow><mo>{</mo><mrow><mn>17</mn><mo>,</mo><mn>1</mn></mrow><mo>}</mo></mrow></mrow><mo>}</mo></mrow></math>\"\r\n"
							+ "      } ]\r\n" + "    }, {\r\n"
							+ "      \"title\" : \"Residues modulo small integers\",\r\n"
							+ "      \"scanner\" : \"Integer\",\r\n" + "      \"error\" : \"false\",\r\n"
							+ "      \"numsubpods\" : 1,\r\n" + "      \"subpods\" : [ {\r\n"
							+ "        \"plaintext\" : \"{1,2,1,2,5,3,1,8}\",\r\n"
							+ "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>5</mn><mo>,</mo><mn>3</mn><mo>,</mo><mn>1</mn><mo>,</mo><mn>8</mn></mrow><mo>}</mo></mrow></math>\"\r\n"
							+ "      } ]\r\n" + "    }, {\r\n" + "      \"title\" : \"Properties\",\r\n"
							+ "      \"scanner\" : \"Integer\",\r\n" + "      \"error\" : \"false\",\r\n"
							+ "      \"numsubpods\" : 2,\r\n" + "      \"subpods\" : [ {\r\n"
							+ "        \"plaintext\" : \"17 is an odd number.\"\r\n" + "      }, {\r\n"
							+ "        \"plaintext\" : \"17 the 7th prime number.\"\r\n" + "      } ]\r\n"
							+ "    }, {\r\n" + "      \"title\" : \"Quadratic residues modulo 17\",\r\n"
							+ "      \"scanner\" : \"Integer\",\r\n" + "      \"error\" : \"false\",\r\n"
							+ "      \"numsubpods\" : 1,\r\n" + "      \"subpods\" : [ {\r\n"
							+ "        \"plaintext\" : \"{0,1,2,4,8,9,13,15,16}\",\r\n"
							+ "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>0</mn><mo>,</mo><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>4</mn><mo>,</mo><mn>8</mn><mo>,</mo><mn>9</mn><mo>,</mo><mn>13</mn><mo>,</mo><mn>15</mn><mo>,</mo><mn>16</mn></mrow><mo>}</mo></mrow></math>\"\r\n"
							+ "      } ]\r\n" + "    }, {\r\n" + "      \"title\" : \"Primitive roots modulo 17\",\r\n"
							+ "      \"scanner\" : \"Integer\",\r\n" + "      \"error\" : \"false\",\r\n"
							+ "      \"numsubpods\" : 1,\r\n" + "      \"subpods\" : [ {\r\n"
							+ "        \"plaintext\" : \"{3,5,6,7,10,11,12,14}\",\r\n"
							+ "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>3</mn><mo>,</mo><mn>5</mn><mo>,</mo><mn>6</mn><mo>,</mo><mn>7</mn><mo>,</mo><mn>10</mn><mo>,</mo><mn>11</mn><mo>,</mo><mn>12</mn><mo>,</mo><mn>14</mn></mrow><mo>}</mo></mrow></math>\"\r\n"
							+ "      } ]\r\n" + "    } ]\r\n" + "  }\r\n" + "}");//
		}
	}

	@Test
	public void testPlotSin() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Plot(Sin(x), {x, 0, 6*Pi} )", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + "  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"true\",\r\n" + "    \"error\" : \"false\",\r\n"
							+ "    \"numpods\" : 1,\r\n" + "    \"version\" : \"0.1\",\r\n" + "    \"pods\" : [ {\r\n"
							+ "      \"title\" : \"Function\",\r\n" + "      \"scanner\" : \"Plotter\",\r\n"
							+ "      \"error\" : \"false\",\r\n" + "      \"numsubpods\" : 1,\r\n"
							+ "      \"subpods\" : [ {\r\n"
							+ "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.7/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9424777960769379,1.1,19.792033717615695,-1.1]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return sin(x); }\\nvar p1 = board.create('functiongraph',[z1, 0, (18.84955592153876)]);\\nvar data = [ p1 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
							+ "      } ]\r\n" + "    } ]\r\n" + "  }\r\n" + "}");//
		}
	}

	@Test
	public void testComplexPlot3D() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods
				.createResult("ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + "  \"queryresult\" : {\r\n" + "    \"success\" : \"true\",\r\n"
							+ "    \"error\" : \"false\",\r\n" + "    \"numpods\" : 1,\r\n"
							+ "    \"version\" : \"0.1\",\r\n" + "    \"pods\" : [ {\r\n"
							+ "      \"title\" : \"Function\",\r\n" + "      \"scanner\" : \"Plotter\",\r\n"
							+ "      \"error\" : \"false\",\r\n" + "      \"numsubpods\" : 1,\r\n"
							+ "      \"subpods\" : [ {\r\n"
							+ "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.7/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.8.8/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n\\nvar parent = document.scripts[ document.scripts.length - 1 ].parentNode;\\n\\nvar id = generateId();\\nparent.id = id;\\n\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2)))); }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\n\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
							+ "      } ]\r\n" + "    } ]\r\n" + "  }\r\n" + "}");//
		}
	}

	@Test
	public void testHistogram() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Histogram(RandomVariate(NormalDistribution(0, 1), 200))", formats);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			// RandomVariate gives random results

		}
	}
}
