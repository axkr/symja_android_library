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
		if (s.contains("Windows")) {
			ObjectNode messageJSON = Pods.createResult("?#?", formats);
			final String jsonStr = messageJSON.toPrettyString();
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
		if (s.contains("Windows")) {
			ObjectNode messageJSON = Pods.createResult("Sin", formats);
			final String jsonStr = messageJSON.toPrettyString();
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 1,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"documentation\",\r\n" + 
					"      \"scanner\" : \"help\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"markdown\" : \"## Sin\\n\\nSin(expr)\\n\\n> returns the sine of `expr` (measured in radians).\\n \\n`Sin(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.\\n\\nSee\\n* [Wikipedia - Sine](https://en.wikipedia.org/wiki/Sine)\\n\\n### Examples\\n\\n>> Sin(0)\\n0\\n\\n>> Sin(0.5)\\n0.479425538604203\\n\\n>> Sin(3*Pi)\\n0\\n\\n>> Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361\\n \\n\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testInteger17() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			ObjectNode messageJSON = Pods.createResult("17", formats);
			final String jsonStr = messageJSON.toPrettyString();
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
	public void testComplexPlot3D() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			ObjectNode messageJSON = Pods.createResult("ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})", formats);
			final String jsonStr = messageJSON.toPrettyString();
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 1,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Function\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"mathcell\" : \"MathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2)))); }\\n\\nvar p1 = parametric( (re,im) => [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
}
