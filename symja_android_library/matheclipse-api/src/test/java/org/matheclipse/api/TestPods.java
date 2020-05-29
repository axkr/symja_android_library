package org.matheclipse.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.matheclipse.api.Pods;
import org.matheclipse.core.basic.Config;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestPods {

	final static String[] formatsMATHML = new String[] { "mathml", "plaintext" };

	final static String[] formatsTEX = new String[] { "latex", "plaintext", "sinput" };
	
	final static String[] formatsHTML = new String[] { "html", "plaintext", "sinput" };

	static {
		Config.FUZZY_PARSER = true;
	}

	@Test
	public void testSyntaxError001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("?#?", formatsTEX);
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
		ObjectNode messageJSON = Pods.createResult("Sin", formatsHTML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
					"  \"queryresult\" : {\r\n" + //
					"    \"success\" : \"true\",\r\n" + //
					"    \"error\" : \"false\",\r\n" + //
					"    \"numpods\" : 2,\r\n" + //
					"    \"version\" : \"0.1\",\r\n" + //
					"    \"pods\" : [ {\r\n" + //
					"      \"title\" : \"Input\",\r\n" + //
					"      \"scanner\" : \"Identity\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"plaintext\" : \"Sin\",\r\n" + //
					"        \"sinput\" : \"Sin\"\r\n" + //
					"      } ]\r\n" + //
					"    }, {\r\n" + //
					"      \"title\" : \"documentation\",\r\n" + //
					"      \"scanner\" : \"help\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"markdown\" : \"## Sin\\n\\n```\\nSin(expr)\\n```\\n\\n> returns the sine of `expr` (measured in radians).\\n \\n`Sin(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.\\n\\nSee\\n* [Wikipedia - Sine](https://en.wikipedia.org/wiki/Sine)\\n\\n### Examples\\n\\n```\\n>> Sin(0)\\n0\\n\\n>> Sin(0.5)\\n0.479425538604203\\n\\n>> Sin(3*Pi)\\n0\\n\\n>> Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361\\n```\\n \\n\",\r\n" + //
					"        \"html\" : \"<h2>Sin</h2>\\n<pre><code>Sin(expr)\\n</code></pre>\\n<blockquote>\\n<p>returns the sine of <code>expr</code> (measured in radians).</p>\\n</blockquote>\\n<p><code>Sin(expr)</code> will evaluate automatically in the case <code>expr</code> is a multiple of <code>Pi, Pi/2, Pi/3, Pi/4</code> and <code>Pi/6</code>.</p>\\n<p>See</p>\\n<ul>\\n<li><a href=\\\"https://en.wikipedia.org/wiki/Sine\\\">Wikipedia - Sine</a></li>\\n</ul>\\n<h3>Examples</h3>\\n<pre><code>&gt;&gt; Sin(0)\\n0\\n\\n&gt;&gt; Sin(0.5)\\n0.479425538604203\\n\\n&gt;&gt; Sin(3*Pi)\\n0\\n\\n&gt;&gt; Sin(1.0 + I)\\n1.2984575814159773+I*0.6349639147847361\\n</code></pre>\\n\"\r\n" + //
					"      } ]\r\n" + //
					"    } ]\r\n" + //
					"  }\r\n" + //
					"}");//
		}
	}

	@Test
	public void testTeXParser() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("\\sin 30 ^ { \\circ }", formatsHTML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + // 
					"  \"queryresult\" : {\r\n" + // 
					"    \"success\" : \"true\",\r\n" + // 
					"    \"error\" : \"false\",\r\n" + // 
					"    \"numpods\" : 3,\r\n" + // 
					"    \"version\" : \"0.1\",\r\n" + // 
					"    \"pods\" : [ {\r\n" + // 
					"      \"title\" : \"Input\",\r\n" + // 
					"      \"scanner\" : \"Identity\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"Sin(30*Degree)\",\r\n" + // 
					"        \"sinput\" : \"Sin(30*Degree)\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Exact result\",\r\n" + // 
					"      \"scanner\" : \"Rational\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"1/2\",\r\n" + // 
					"        \"sinput\" : \"Sin(30*Degree)\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Decimal form\",\r\n" + // 
					"      \"scanner\" : \"Numeric\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"0.5\",\r\n" + // 
					"        \"sinput\" : \"N(1/2)\"\r\n" + // 
					"      } ]\r\n" + // 
					"    } ]\r\n" + // 
					"  }\r\n" + // 
					"}");//
		}
	}
	
	@Test
	public void testSoundexHelp() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Cs", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 34,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"cs\",\r\n" + 
					"        \"sinput\" : \"cs\",\r\n" + 
					"        \"latex\" : \"cs\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Standard Name\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Caesium\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,StandardName)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{Caesium}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Atomic Number\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"55\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,AtomicNumber)\",\r\n" + 
					"        \"latex\" : \"55\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Abbreviation\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Cs\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Abbreviation)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{Cs}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Absolute Boiling Point\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"944\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,AbsoluteBoilingPoint)\",\r\n" + 
					"        \"latex\" : \"944\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Absolute Melting Point\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"301.7\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,AbsoluteMeltingPoint)\",\r\n" + 
					"        \"latex\" : \"301.7\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Atomic Radius\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"260\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,AtomicRadius)\",\r\n" + 
					"        \"latex\" : \"260\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Atomic Weight\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"132.91\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,AtomicWeight)\",\r\n" + 
					"        \"latex\" : \"132.91\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Block\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"s\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Block)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{s}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Boiling Point\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"670.85\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,BoilingPoint)\",\r\n" + 
					"        \"latex\" : \"670.85\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Brinell Hardness\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"0.14\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,BrinellHardness)\",\r\n" + 
					"        \"latex\" : \"0.14\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Bulk Modulus\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1.6\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,BulkModulus)\",\r\n" + 
					"        \"latex\" : \"1.6\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Covalent Radius\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"225\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,CovalentRadius)\",\r\n" + 
					"        \"latex\" : \"225\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Crust Abundance\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"0\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,CrustAbundance)\",\r\n" + 
					"        \"latex\" : \"0\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Density\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1873\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Density)\",\r\n" + 
					"        \"latex\" : \"1873\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Discovery Year\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1860\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,DiscoveryYear)\",\r\n" + 
					"        \"latex\" : \"1860\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Electro Negativity\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"0.79\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ElectroNegativity)\",\r\n" + 
					"        \"latex\" : \"0.79\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Electron Affinity\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"45.51\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ElectronAffinity)\",\r\n" + 
					"        \"latex\" : \"45.51\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Electron Configuration\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"[Xe] 6s1\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ElectronConfiguration)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{[Xe] 6s1}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Electron Configuration String\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{2,8,18,18,8,1}\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ElectronConfigurationString)\",\r\n" + 
					"        \"latex\" : \"\\\\{2,8,18,18,8,1\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Fusion Heat\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"2.09\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,FusionHeat)\",\r\n" + 
					"        \"latex\" : \"2.09\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Group\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Group)\",\r\n" + 
					"        \"latex\" : \"1\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Ionization Energies\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{375.7,2234.3,3400}\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,IonizationEnergies)\",\r\n" + 
					"        \"latex\" : \"\\\\{375.7,2234.3,3400\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Liquid Density\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1843\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,LiquidDensity)\",\r\n" + 
					"        \"latex\" : \"1843\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Melting Point\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"28.55\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,MeltingPoint)\",\r\n" + 
					"        \"latex\" : \"28.55\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Mohs Hardness\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"0.2\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,MohsHardness)\",\r\n" + 
					"        \"latex\" : \"0.2\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Name\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"caesium\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Name)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{caesium}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Period\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"6\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Period)\",\r\n" + 
					"        \"latex\" : \"6\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Series\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"AlkaliMetal\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,Series)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{AlkaliMetal}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Shear Modulus\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{{2},{2,6},{2,6,10},{2,6,10},{2,6},{1}}\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ShearModulus)\",\r\n" + 
					"        \"latex\" : \"\\\\{\\\\{2\\\\},\\\\{2,6\\\\},\\\\{2,6,10\\\\},\\\\{2,6,10\\\\},\\\\{2,6\\\\},\\\\{1\\\\}\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Specific Heat\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"242\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,SpecificHeat)\",\r\n" + 
					"        \"latex\" : \"242\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Thermal Conductivity\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"35.9\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,ThermalConductivity)\",\r\n" + 
					"        \"latex\" : \"35.9\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Vaporization Heat\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"63.9\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,VaporizationHeat)\",\r\n" + 
					"        \"latex\" : \"63.9\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Young Modulus\",\r\n" + 
					"      \"scanner\" : \"Data\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"37\",\r\n" + 
					"        \"sinput\" : \"ElementData(Caesium,YoungModulus)\",\r\n" + 
					"        \"latex\" : \"37\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testInteger17() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("17", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 9,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"17\",\r\n" + 
					"        \"sinput\" : \"17\",\r\n" + 
					"        \"latex\" : \"17\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Number name\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"seventeen\",\r\n" + 
					"        \"sinput\" : \"IntegerName(17,Words)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{seventeen}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Roman numerals\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"XVII\",\r\n" + 
					"        \"sinput\" : \"RomanNumeral(17)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{XVII}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Binary form\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"10001_2\",\r\n" + 
					"        \"sinput\" : \"BaseForm(17,2)\",\r\n" + 
					"        \"latex\" : \"{\\\\textnormal{10001}}_{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Prime factorization\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"17 is a prime number.\",\r\n" + 
					"        \"sinput\" : \"FactorInteger(17)\",\r\n" + 
					"        \"latex\" : \"\\\\begin{pmatrix}\\n 17 & 1 \\\\\\\\\\n\\\\end{pmatrix}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Residues modulo small integers\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"m | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9\\n17 mod m | 1 | 2 | 1 | 2 | 5 | 3 | 1 | 8\",\r\n" + 
					"        \"sinput\" : \"Mod(17,{2,3,4,5,6,7,8,9})\",\r\n" + 
					"        \"latex\" : \"\\\\{1,2,1,2,5,3,1,8\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Properties\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 2,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"17 is an odd number.\"\r\n" + 
					"      }, {\r\n" + 
					"        \"plaintext\" : \"17 is the 7th prime number.\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Quadratic residues modulo 17\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{0,1,2,4,8,9,13,15,16}\",\r\n" + 
					"        \"sinput\" : \"Union(PowerMod(Range(0,17/2),2,17))\",\r\n" + 
					"        \"latex\" : \"\\\\{0,1,2,4,8,9,13,15,16\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Primitive roots modulo 17\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{3,5,6,7,10,11,12,14}\",\r\n" + 
					"        \"sinput\" : \"Select(Range(16),MultiplicativeOrder(#1,17)==EulerPhi(17)&)\",\r\n" + 
					"        \"latex\" : \"\\\\{3,5,6,7,10,11,12,14\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testRationalHalf() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("1/2", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
							"  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"true\",\r\n" + //
							"    \"error\" : \"false\",\r\n" + //
							"    \"numpods\" : 3,\r\n" + //
							"    \"version\" : \"0.1\",\r\n" + //
							"    \"pods\" : [ {\r\n" + //
							"      \"title\" : \"Input\",\r\n" + //
							"      \"scanner\" : \"Identity\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"1/2\",\r\n" + //
							"        \"sinput\" : \"1/2\",\r\n" + //
							"        \"latex\" : \"\\\\frac{1}{2}\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Exact result\",\r\n" + //
							"      \"scanner\" : \"Rational\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"1/2\",\r\n" + //
							"        \"sinput\" : \"1/2\",\r\n" + //
							"        \"latex\" : \"\\\\frac{1}{2}\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Decimal form\",\r\n" + //
							"      \"scanner\" : \"Numeric\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"0.5\",\r\n" + //
							"        \"sinput\" : \"N(1/2)\",\r\n" + //
							"        \"latex\" : \"0.5\"\r\n" + //
							"      } ]\r\n" + //
							"    } ]\r\n" + //
							"  }\r\n" + //
							"}");//
		}
	}

	@Test
	public void testRationalPlus() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("1/2+3/4", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
							"  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"true\",\r\n" + //
							"    \"error\" : \"false\",\r\n" + //
							"    \"numpods\" : 5,\r\n" + //
							"    \"version\" : \"0.1\",\r\n" + //
							"    \"pods\" : [ {\r\n" + //
							"      \"title\" : \"Input\",\r\n" + //
							"      \"scanner\" : \"Identity\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"1/2+3/4\",\r\n" + //
							"        \"sinput\" : \"1/2+3/4\",\r\n" + //
							"        \"latex\" : \"\\\\frac{1}{2}+\\\\frac{3}{4}\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Exact result\",\r\n" + //
							"      \"scanner\" : \"Rational\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"5/4\",\r\n" + //
							"        \"sinput\" : \"1/2+3/4\",\r\n" + //
							"        \"latex\" : \"\\\\frac{5}{4}\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Decimal form\",\r\n" + //
							"      \"scanner\" : \"Numeric\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"1.25\",\r\n" + //
							"        \"sinput\" : \"N(5/4)\",\r\n" + //
							"        \"latex\" : \"1.25\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Mixed fraction\",\r\n" + //
							"      \"scanner\" : \"Rational\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"1 1/4\",\r\n" + //
							"        \"sinput\" : \"{IntegerPart(5/4),FractionalPart(5/4)}\",\r\n" + //
							"        \"latex\" : \"\\\\{1,\\\\frac{1}{4}\\\\}\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Continued fraction\",\r\n" + //
							"      \"scanner\" : \"ContinuedFraction\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"[1; 4]\",\r\n" + //
							"        \"sinput\" : \"ContinuedFraction(5/4)\",\r\n" + //
							"        \"latex\" : \"\\\\{1,4\\\\}\"\r\n" + //
							"      } ]\r\n" + //
							"    } ]\r\n" + //
							"  }\r\n" + //
							"}");//
		}
	}

	@Test
	public void testPlotSin() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Plot(Sin(x), {x, 0, 6*Pi} )", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
					"  \"queryresult\" : {\r\n" + //
					"    \"success\" : \"true\",\r\n" + //
					"    \"error\" : \"false\",\r\n" + //
					"    \"numpods\" : 2,\r\n" + //
					"    \"version\" : \"0.1\",\r\n" + //
					"    \"pods\" : [ {\r\n" + //
					"      \"title\" : \"Input\",\r\n" + //
					"      \"scanner\" : \"Identity\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"plaintext\" : \"Plot(Sin(x),{x,0,6*Pi})\",\r\n" + //
					"        \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\r\n" + //
					"        \"latex\" : \"\\\\text{Plot}(\\\\sin (x),\\\\{x,0,6\\\\,\\\\pi\\\\})\"\r\n" + //
					"      } ]\r\n" + //
					"    }, {\r\n" + //
					"      \"title\" : \"Function\",\r\n" + //
					"      \"scanner\" : \"Plotter\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9424777960769379,1.1,19.792033717615695,-1.1]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return sin(x); }\\nvar p1 = board.create('functiongraph',[z1, 0, (18.84955592153876)]);\\nvar data = [ p1 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + //
					"      } ]\r\n" + //
					"    } ]\r\n" + //
					"  }\r\n" + //
					"}");//
		}
	}

	@Test
	public void testSin() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Sin(x)", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
							"  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"true\",\r\n" + //
							"    \"error\" : \"false\",\r\n" + //
							"    \"numpods\" : 5,\r\n" + //
							"    \"version\" : \"0.1\",\r\n" + //
							"    \"pods\" : [ {\r\n" + //
							"      \"title\" : \"Input\",\r\n" + //
							"      \"scanner\" : \"Identity\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"Sin(x)\",\r\n" + //
							"        \"sinput\" : \"Sin(x)\",\r\n" + //
							"        \"latex\" : \"\\\\sin (x)\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Function\",\r\n" + //
							"      \"scanner\" : \"Plotter\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-7.7,1.1,7.7,-1.1]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return sin(x); }\\nvar p1 = board.create('functiongraph',[z1, -7.0, 7.0]);\\nvar data = [ p1 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
							+ //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Alternate form\",\r\n" + //
							"      \"scanner\" : \"Simplification\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"(I*1/2)/E^(I*x)-I*1/2*E^(I*x)\",\r\n" + //
							"        \"sinput\" : \"TrigToExp(Sin(x))\",\r\n" + //
							"        \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\,x}} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\,{e}^{i \\\\,x}\"\r\n"
							+ //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Derivative\",\r\n" + //
							"      \"scanner\" : \"Derivative\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"Cos(x)\",\r\n" + //
							"        \"sinput\" : \"D(Sin(x),x)\",\r\n" + //
							"        \"latex\" : \"\\\\cos (x)\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Indefinite integral\",\r\n" + //
							"      \"scanner\" : \"Integral\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"-Cos(x)\",\r\n" + //
							"        \"sinput\" : \"Integrate(Sin(x),x)\",\r\n" + //
							"        \"latex\" : \" - \\\\cos (x)\"\r\n" + //
							"      } ]\r\n" + //
							"    } ]\r\n" + //
							"  }\r\n" + //
							"}");//
		}
	}

	@Test
	public void testPolynomialQuotientRemainder() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult(" x**2-4,x-2", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 5,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{-4+x^2,-2+x}\",\r\n" + 
					"        \"sinput\" : \"{-4+x^2,-2+x}\",\r\n" + 
					"        \"latex\" : \"\\\\{-4+{x}^{2},-2+x\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Polynomial quotient and remainder\",\r\n" + 
					"      \"scanner\" : \"Polynomial\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{2+x,0}\",\r\n" + 
					"        \"sinput\" : \"PolynomialQuotientRemainder(-4+x^2,-2+x,x)\",\r\n" + 
					"        \"latex\" : \"\\\\{2+x,0\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Function\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-7.7,5.45,7.7,-4.45]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return add(-4,pow(x,2)); }\\nfunction z2(x) { return add(-2,x); }\\nvar p1 = board.create('functiongraph',[z1, -7.0, 7.0]);\\nvar p2 = board.create('functiongraph',[z2, -7.0, 7.0]);\\nvar data = [ p1, p2 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Derivative\",\r\n" + 
					"      \"scanner\" : \"Derivative\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{2*x,1}\",\r\n" + 
					"        \"sinput\" : \"D({-4+x^2,-2+x},x)\",\r\n" + 
					"        \"latex\" : \"\\\\{2\\\\,x,1\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Indefinite integral\",\r\n" + 
					"      \"scanner\" : \"Integral\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{-4*x+x^3/3,-2*x+x^2/2}\",\r\n" + 
					"        \"sinput\" : \"Integrate({-4+x^2,-2+x},x)\",\r\n" + 
					"        \"latex\" : \"\\\\{\\\\left( -4\\\\right) \\\\,x+\\\\frac{{x}^{3}}{3},\\\\left( -2\\\\right) \\\\,x+\\\\frac{{x}^{2}}{2}\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testSinXY() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Sin(x*y)", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + //
							"  \"queryresult\" : {\r\n" + //
							"    \"success\" : \"true\",\r\n" + //
							"    \"error\" : \"false\",\r\n" + //
							"    \"numpods\" : 3,\r\n" + //
							"    \"version\" : \"0.1\",\r\n" + //
							"    \"pods\" : [ {\r\n" + //
							"      \"title\" : \"Input\",\r\n" + //
							"      \"scanner\" : \"Identity\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"Sin(x*y)\",\r\n" + //
							"        \"sinput\" : \"Sin(x*y)\",\r\n" + //
							"        \"latex\" : \"\\\\sin (x\\\\,y)\"\r\n" + //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"3D plot\",\r\n" + //
							"      \"scanner\" : \"Plot\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.8.8/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n\\nvar parent = document.scripts[ document.scripts.length - 1 ].parentNode;\\n\\nvar id = generateId();\\nparent.id = id;\\n\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\n\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n"
							+ //
							"      } ]\r\n" + //
							"    }, {\r\n" + //
							"      \"title\" : \"Alternate form\",\r\n" + //
							"      \"scanner\" : \"Simplification\",\r\n" + //
							"      \"error\" : \"false\",\r\n" + //
							"      \"numsubpods\" : 1,\r\n" + //
							"      \"subpods\" : [ {\r\n" + //
							"        \"plaintext\" : \"(I*1/2)/E^(I*x*y)-I*1/2*E^(I*x*y)\",\r\n" + //
							"        \"sinput\" : \"TrigToExp(Sin(x*y))\",\r\n" + //
							"        \"latex\" : \"\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i \\\\,x\\\\,y}} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\,{e}^{i \\\\,x\\\\,y}\"\r\n"
							+ //
							"      } ]\r\n" + //
							"    } ]\r\n" + //
							"  }\r\n" + //
							"}");//
		}
	}

	@Test
	public void testComplexPlot3D() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult(
				"ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})", formatsMATHML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 2,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"ComplexPlot3D((1+z^2)/(-1+z^2),{z,-2+(-2)*I,2+I*2},PlotRange->{0,3})\",\r\n" + 
					"        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mi>ComplexPlot3D</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mfrac><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>+</mo><mn>1</mn></mrow><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>-</mo><mn>1</mn></mrow></mfrac><mo>,</mo><mrow><mo>{</mo><mrow><mi>z</mi><mo>,</mo><mrow><mrow><mrow><mo>(</mo><mn>-2</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mo>-</mo><mn>2</mn></mrow><mo>,</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow><mo>&#0183;</mo><mn>2</mn></mrow><mo>+</mo><mn>2</mn></mrow></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mi>PlotRange</mi><mo>-&gt;</mo><mrow><mo>{</mo><mrow><mn>0</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></mrow></mrow><mo>)</mo></mrow></mrow></math>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Function\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.8.8/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\n\\nvar parent = document.scripts[ document.scripts.length - 1 ].parentNode;\\n\\nvar id = generateId();\\nparent.id = id;\\n\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2)))); }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\n\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testHistogram() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Histogram(RandomVariate(NormalDistribution(0, 1), 200))",
				formatsMATHML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			// RandomVariate gives random results

		}
	}

	@Test
	public void testList() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("1,2,3", formatsMATHML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 5,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{1,2,3}\",\r\n" + 
					"        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mn>1</mn><mo>,</mo><mn>2</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></math>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Total\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"6\",\r\n" + 
					"        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>6</mn></math>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Vector length\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"3.7416573867739413\",\r\n" + 
					"        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mn>3.74166</mn></math>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Normalized vector\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)}\",\r\n" + 
					"        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mo>{</mo><mrow><mfrac><mn>1</mn><msqrt><mn>14</mn></msqrt></mfrac><mo>,</mo><msqrt><mrow><mfrac><mn>2</mn><mn>7</mn></mfrac></mrow></msqrt><mo>,</mo><mfrac><mn>3</mn><msqrt><mn>14</mn></msqrt></mfrac></mrow><mo>}</mo></mrow></math>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Plot points\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.2,3.1,4.2,0.9]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1.0;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2.0;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.0;}],  {name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}

	@Test
	public void testSolve001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("3+x=10", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + // 
					"  \"queryresult\" : {\r\n" + // 
					"    \"success\" : \"true\",\r\n" + // 
					"    \"error\" : \"false\",\r\n" + // 
					"    \"numpods\" : 4,\r\n" + // 
					"    \"version\" : \"0.1\",\r\n" + // 
					"    \"pods\" : [ {\r\n" + // 
					"      \"title\" : \"Input\",\r\n" + // 
					"      \"scanner\" : \"Identity\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"3+x==10\",\r\n" + // 
					"        \"sinput\" : \"3+x==10\",\r\n" + // 
					"        \"latex\" : \"3+x == 10\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Function\",\r\n" + // 
					"      \"scanner\" : \"Plotter\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-22.0,5.5,22.0,-5.5]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return x; }\\nfunction z2(x) { return 7; }\\nvar p1 = board.create('functiongraph',[z1, -20.0, 20.0]);\\nvar p2 = board.create('functiongraph',[z2, -20.0, 20.0]);\\nvar data = [ p1, p2 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Alternate form\",\r\n" + // 
					"      \"scanner\" : \"Simplification\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"-7+x==0\",\r\n" + // 
					"        \"sinput\" : \"-7+x==0\",\r\n" + // 
					"        \"latex\" : \"-7+x == 0\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Solution\",\r\n" + // 
					"      \"scanner\" : \"Reduce\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"{{x->7}}\",\r\n" + // 
					"        \"sinput\" : \"Solve(x==7,{x})\",\r\n" + // 
					"        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\r\n" + // 
					"      } ]\r\n" + // 
					"    } ]\r\n" + // 
					"  }\r\n" + // 
					"}");//
		}
	}

	@Test
	public void testSolve002() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("x^2+1=0", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {//
			assertEquals(jsonStr, //
					"{\r\n" + //
					"  \"queryresult\" : {\r\n" + //
					"    \"success\" : \"true\",\r\n" + //
					"    \"error\" : \"false\",\r\n" + //
					"    \"numpods\" : 4,\r\n" + //
					"    \"version\" : \"0.1\",\r\n" + //
					"    \"pods\" : [ {\r\n" + //
					"      \"title\" : \"Input\",\r\n" + //
					"      \"scanner\" : \"Identity\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"plaintext\" : \"1+x^2==0\",\r\n" + //
					"        \"sinput\" : \"1+x^2==0\",\r\n" + //
					"        \"latex\" : \"1+{x}^{2} == 0\"\r\n" + //
					"      } ]\r\n" + //
					"    }, {\r\n" + //
					"      \"title\" : \"Function\",\r\n" + //
					"      \"scanner\" : \"Plotter\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-22.0,5.25,22.0,-0.25]});\\nboard.suspendUpdate();\\n\\nfunction z1(x) { return pow(x,2); }\\nfunction z2(x) { return -1; }\\nvar p1 = board.create('functiongraph',[z1, -20.0, 20.0]);\\nvar p2 = board.create('functiongraph',[z2, -20.0, 20.0]);\\nvar data = [ p1, p2 ];\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + //
					"      } ]\r\n" + //
					"    }, {\r\n" + //
					"      \"title\" : \"Alternate form\",\r\n" + //
					"      \"scanner\" : \"Simplification\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"plaintext\" : \"1+x^2==0\",\r\n" + //
					"        \"sinput\" : \"1+x^2==0\",\r\n" + //
					"        \"latex\" : \"1+{x}^{2} == 0\"\r\n" + //
					"      } ]\r\n" + //
					"    }, {\r\n" + //
					"      \"title\" : \"Solution\",\r\n" + //
					"      \"scanner\" : \"Reduce\",\r\n" + //
					"      \"error\" : \"false\",\r\n" + //
					"      \"numsubpods\" : 1,\r\n" + //
					"      \"subpods\" : [ {\r\n" + //
					"        \"plaintext\" : \"{{x->-I},{x->I}}\",\r\n" + //
					"        \"sinput\" : \"Solve(x^2==-1,{x})\",\r\n" + //
					"        \"latex\" : \"\\\\{\\\\{x\\\\to  - i \\\\},\\\\{x\\\\to i \\\\}\\\\}\"\r\n" + //
					"      } ]\r\n" + //
					"    } ]\r\n" + //
					"  }\r\n" + //
					"}");//
		}
	}

	@Test
	public void testSolve003() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Solver x+3=10", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 2,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Solve(3+x==10)\",\r\n" + 
					"        \"sinput\" : \"Solve(3+x==10)\",\r\n" + 
					"        \"latex\" : \"\\\\text{Solve}(3+x == 10)\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Solve equation\",\r\n" + 
					"      \"scanner\" : \"Solver\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{{x->7}}\",\r\n" + 
					"        \"sinput\" : \"Solve(3+x==10,{x})\",\r\n" + 
					"        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testInteger4294967295() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("2**32-1", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 7,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-1+2^32\",\r\n" + 
					"        \"sinput\" : \"-1+2^32\",\r\n" + 
					"        \"latex\" : \"-1+{2}^{32}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Result\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"4294967295\",\r\n" + 
					"        \"sinput\" : \"-1+2^32\",\r\n" + 
					"        \"latex\" : \"4294967295\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Number name\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five\",\r\n" + 
					"        \"sinput\" : \"IntegerName(4294967295,Words)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{four billion two hundred ninety-four million nine hundred sixty-seven thousand two hundred ninety-five}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Binary form\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"11111111111111111111111111111111_2\",\r\n" + 
					"        \"sinput\" : \"BaseForm(4294967295,2)\",\r\n" + 
					"        \"latex\" : \"{\\\\textnormal{11111111111111111111111111111111}}_{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Prime factorization\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"3*5*17*257*65537\",\r\n" + 
					"        \"sinput\" : \"FactorInteger(4294967295)\",\r\n" + 
					"        \"latex\" : \"\\\\begin{pmatrix}\\n 3 & 1 \\\\\\\\\\n 5 & 1 \\\\\\\\\\n 17 & 1 \\\\\\\\\\n 257 & 1 \\\\\\\\\\n 65537 & 1 \\\\\\\\\\n\\\\end{pmatrix}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Residues modulo small integers\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"m | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9\\n4294967295 mod m | 1 | 0 | 3 | 0 | 3 | 3 | 7 | 3\",\r\n" + 
					"        \"sinput\" : \"Mod(4294967295,{2,3,4,5,6,7,8,9})\",\r\n" + 
					"        \"latex\" : \"\\\\{1,0,3,0,3,3,7,3\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Properties\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"4294967295 is an odd number.\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testNormalDistribution() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("NormalDistribution(a,b)", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 4,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"NormalDistribution(a,b)\",\r\n" + 
					"        \"sinput\" : \"NormalDistribution(a,b)\",\r\n" + 
					"        \"latex\" : \"\\\\text{NormalDistribution}(a,b)\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Statistical properties\",\r\n" + 
					"      \"scanner\" : \"Statistics\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"mean | a\\nstandard deviation | b\\nvariance | b^2\\nskewness | 0\",\r\n" + 
					"        \"sinput\" : \"{{mean,a},\\n {standard deviation,b},\\n {variance,b^2},\\n {skewness,0}}\",\r\n" + 
					"        \"latex\" : \"\\\\begin{pmatrix}\\n \\\\textnormal{mean} & a \\\\\\\\\\n \\\\textnormal{standard deviation} & b \\\\\\\\\\n \\\\textnormal{variance} & {b}^{2} \\\\\\\\\\n \\\\textnormal{skewness} & 0 \\\\\\\\\\n\\\\end{pmatrix}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Probability density function (PDF)\",\r\n" + 
					"      \"scanner\" : \"Statistics\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1/(b*E^((-a+x)^2/(2*b^2))*Sqrt(2*Pi))\",\r\n" + 
					"        \"sinput\" : \"PDF(NormalDistribution(a,b),x)\",\r\n" + 
					"        \"latex\" : \"\\\\frac{1}{b\\\\,{e}^{\\\\frac{{\\\\left(  - a+x\\\\right) }^{2}}{2\\\\,{b}^{2}}}\\\\,\\\\sqrt{\\\\left( 2\\\\,\\\\pi\\\\right) }}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Cumulative distribution function (CDF)\",\r\n" + 
					"      \"scanner\" : \"Statistics\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Erfc((a-x)/(Sqrt(2)*b))/2\",\r\n" + 
					"        \"sinput\" : \"CDF(NormalDistribution(a,b),x)\",\r\n" + 
					"        \"latex\" : \"\\\\frac{\\\\text{Erfc}(\\\\frac{a - x}{\\\\sqrt{2}\\\\,b})}{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testLogic001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult(
				"a&&b||c", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + // 
					"  \"queryresult\" : {\r\n" + // 
					"    \"success\" : \"true\",\r\n" + // 
					"    \"error\" : \"false\",\r\n" + // 
					"    \"numpods\" : 2,\r\n" + // 
					"    \"version\" : \"0.1\",\r\n" + // 
					"    \"pods\" : [ {\r\n" + // 
					"      \"title\" : \"Input\",\r\n" + // 
					"      \"scanner\" : \"Identity\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"(a&&b)||c\",\r\n" + // 
					"        \"sinput\" : \"(a&&b)||c\",\r\n" + // 
					"        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\r\n" + // 
					"      } ]\r\n" + // 
					"    }, {\r\n" + // 
					"      \"title\" : \"Truth table\",\r\n" + // 
					"      \"scanner\" : \"Boolean\",\r\n" + // 
					"      \"error\" : \"false\",\r\n" + // 
					"      \"numsubpods\" : 1,\r\n" + // 
					"      \"subpods\" : [ {\r\n" + // 
					"        \"plaintext\" : \"a | b | c | (a&&b)||c\\nT | T | T | T\\nT | T | F | T\\nT | F | T | T\\nT | F | F | F\\nF | T | T | T\\nF | T | F | F\\nF | F | T | T\\nF | F | F | F\",\r\n" + // 
					"        \"sinput\" : \"BooleanTable(Append({a,b,c},(a&&b)||c),{a,b,c})\",\r\n" + // 
					"        \"latex\" : \"\\\\begin{pmatrix}\\n True & True & True & True \\\\\\\\\\n True & True & False & True \\\\\\\\\\n True & False & True & True \\\\\\\\\\n True & False & False & False \\\\\\\\\\n False & True & True & True \\\\\\\\\\n False & True & False & False \\\\\\\\\\n False & False & True & True \\\\\\\\\\n False & False & False & False \\\\\\\\\\n\\\\end{pmatrix}\"\r\n" + // 
					"      } ]\r\n" + // 
					"    } ]\r\n" + // 
					"  }\r\n" + // 
					"}");//
		}
	}
	
	@Test
	public void testQuantity() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("convert 111 cm in m", formatsHTML);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 2,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"UnitConvert(111[cm],m)\",\r\n" + 
					"        \"sinput\" : \"UnitConvert(111[cm],m)\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Decimal form\",\r\n" + 
					"      \"scanner\" : \"Numeric\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1.11[m]\",\r\n" + 
					"        \"sinput\" : \"N(111/100[m])\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testTimes() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("10*11*12", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 8,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"10*11*12\",\r\n" + 
					"        \"sinput\" : \"10*11*12\",\r\n" + 
					"        \"latex\" : \"10\\\\cdot 11\\\\cdot 12\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Result\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1320\",\r\n" + 
					"        \"sinput\" : \"10*11*12\",\r\n" + 
					"        \"latex\" : \"1320\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Number name\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"one thousand three hundred twenty\",\r\n" + 
					"        \"sinput\" : \"IntegerName(1320,Words)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{one thousand three hundred twenty}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Roman numerals\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"MCCCXX\",\r\n" + 
					"        \"sinput\" : \"RomanNumeral(1320)\",\r\n" + 
					"        \"latex\" : \"\\\\textnormal{MCCCXX}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Binary form\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"10100101000_2\",\r\n" + 
					"        \"sinput\" : \"BaseForm(1320,2)\",\r\n" + 
					"        \"latex\" : \"{\\\\textnormal{10100101000}}_{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Prime factorization\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"2^3*3*5*11\",\r\n" + 
					"        \"sinput\" : \"FactorInteger(1320)\",\r\n" + 
					"        \"latex\" : \"\\\\begin{pmatrix}\\n 2 & 3 \\\\\\\\\\n 3 & 1 \\\\\\\\\\n 5 & 1 \\\\\\\\\\n 11 & 1 \\\\\\\\\\n\\\\end{pmatrix}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Residues modulo small integers\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"m | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9\\n1320 mod m | 0 | 0 | 0 | 0 | 0 | 4 | 0 | 6\",\r\n" + 
					"        \"sinput\" : \"Mod(1320,{2,3,4,5,6,7,8,9})\",\r\n" + 
					"        \"latex\" : \"\\\\{0,0,0,0,0,4,0,6\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Properties\",\r\n" + 
					"      \"scanner\" : \"Integer\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"1320 is an even number.\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	
	@Test
	public void testSimplify() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("simplificate Sqrt(9-4*Sqrt(5))", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 2,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\r\n" + 
					"        \"sinput\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\r\n" + 
					"        \"latex\" : \"\\\\text{FullSimplify}(\\\\sqrt{\\\\left( 9 - 4\\\\,\\\\sqrt{5}\\\\right) })\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Evaluated result\",\r\n" + 
					"      \"scanner\" : \"Expreesion\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-2+Sqrt(5)\",\r\n" + 
					"        \"sinput\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\r\n" + 
					"        \"latex\" : \"-2+\\\\sqrt{5}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testIntegrate001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Integral Sin(x)", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 3,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Integrate(Sin(x))\",\r\n" + 
					"        \"sinput\" : \"Integrate(Sin(x))\",\r\n" + 
					"        \"latex\" : \"\\\\text{Integrate}(\\\\sin (x))\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Integration\",\r\n" + 
					"      \"scanner\" : \"Integral\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-Cos(x)\",\r\n" + 
					"        \"sinput\" : \"Integrate(Sin(x),x)\",\r\n" + 
					"        \"latex\" : \" - \\\\cos (x)\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Alternate form\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-1/(2*E^(I*x))-E^(I*x)/2\",\r\n" + 
					"        \"sinput\" : \"TrigToExp(-Cos(x))\",\r\n" + 
					"        \"latex\" : \"\\\\frac{-1}{2\\\\,{e}^{i \\\\,x}}+\\\\frac{ - {e}^{i \\\\,x}}{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testIntegrate002() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("integrate Tan(x)*Cos(x)*Pi x", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 3,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n" + 
					"        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n" + 
					"        \"latex\" : \"\\\\int  \\\\pi\\\\,\\\\cos (x)\\\\,\\\\tan (x)\\\\,\\\\mathrm{d}x\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Integration\",\r\n" + 
					"      \"scanner\" : \"Integral\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-Pi*Cos(x)\",\r\n" + 
					"        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\r\n" + 
					"        \"latex\" : \" - \\\\pi\\\\,\\\\cos (x)\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Alternate form\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"-(1/(2*E^(I*x))+E^(I*x)/2)*Pi\",\r\n" + 
					"        \"sinput\" : \"TrigToExp(-Pi*Cos(x))\",\r\n" + 
					"        \"latex\" : \" - \\\\frac{1}{2\\\\,{e}^{i \\\\,x}}+\\\\frac{{e}^{i \\\\,x}}{2}\\\\,\\\\pi\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testDerivative001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("derive tan(x^3)", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 3,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"D(Tan(x^3))\",\r\n" + 
					"        \"sinput\" : \"D(Tan(x^3))\",\r\n" + 
					"        \"latex\" : \"D(\\\\tan ({x}^{3}))\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Derivative\",\r\n" + 
					"      \"scanner\" : \"Derivative\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"3*x^2*Sec(x^3)^2\",\r\n" + 
					"        \"sinput\" : \"D(Tan(x^3),x)\",\r\n" + 
					"        \"latex\" : \"3\\\\,{x}^{2}\\\\,{\\\\sec ({x}^{3})}^{2}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Alternate form\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"(12*x^2)/(E^(-I*x^3)+E^(I*x^3))^2\",\r\n" + 
					"        \"sinput\" : \"TrigToExp(3*x^2*Sec(x^3)^2)\",\r\n" + 
					"        \"latex\" : \"\\\\frac{12\\\\,{x}^{2}}{{\\\\left( {e}^{\\\\left(  - i \\\\right) \\\\,{x}^{3}}+{e}^{i \\\\,{x}^{3}}\\\\right) }^{2}}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test 
	public void testListPlot001() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("3,Sin(1),Pi,3/4,42,1.2", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 6,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{3,Sin(1),Pi,3/4,42,1.2}\",\r\n" + 
					"        \"sinput\" : \"{3,Sin(1),Pi,3/4,42,1.2}\",\r\n" + 
					"        \"latex\" : \"\\\\{3,\\\\sin (1),\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Total\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"50.93306363839769\",\r\n" + 
					"        \"sinput\" : \"Total({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n" + 
					"        \"latex\" : \"50.93306\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Vector length\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"42.256125920620825\",\r\n" + 
					"        \"sinput\" : \"Norm({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n" + 
					"        \"latex\" : \"42.25613\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Normalized vector\",\r\n" + 
					"      \"scanner\" : \"List\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{0.0709956233478567,0.01991358569852382,0.07434644291555152,0.017748905836964174,0.9939387268699937,0.028398249339142676}\",\r\n" + 
					"        \"sinput\" : \"Normalize({3.0,0.8414709848078965,3.141592653589793,0.75,42.0,1.2})\",\r\n" + 
					"        \"latex\" : \"\\\\{0.0709956,0.0199136,0.0743464,0.0177489,0.993939,0.0283982\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Plot points\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.35,44.0625,7.35,-1.3125]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 3.0;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 0.8414709848078965;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3.141592653589793;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 0.75;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 42.0;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 6;},function() {return 1.2;}],  {name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Alternate form\",\r\n" + 
					"      \"scanner\" : \"Simplification\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"{3,(I*1/2)/E^I-I*1/2*E^I,Pi,3/4,42,1.2}\",\r\n" + 
					"        \"sinput\" : \"TrigToExp({3,Sin(1),Pi,3/4,42,1.2})\",\r\n" + 
					"        \"latex\" : \"\\\\{3,\\\\frac{\\\\frac{1}{2}\\\\,i }{{e}^{i }} + \\\\left( \\\\frac{-1}{2}\\\\,i \\\\right) \\\\,{e}^{i },\\\\pi,\\\\frac{3}{4},42,1.2\\\\}\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
	
	@Test
	public void testListPlot002() {
		String s = System.getProperty("os.name");
		ObjectNode messageJSON = Pods.createResult("Table({Sin(t*0.33), Cos(t*1.1)}, {t, 100})", formatsTEX);
		final String jsonStr = messageJSON.toPrettyString();
		System.out.println(jsonStr);
		if (s.contains("Windows")) {
			assertEquals(jsonStr, //
					"{\r\n" + 
					"  \"queryresult\" : {\r\n" + 
					"    \"success\" : \"true\",\r\n" + 
					"    \"error\" : \"false\",\r\n" + 
					"    \"numpods\" : 2,\r\n" + 
					"    \"version\" : \"0.1\",\r\n" + 
					"    \"pods\" : [ {\r\n" + 
					"      \"title\" : \"Input\",\r\n" + 
					"      \"scanner\" : \"Identity\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"plaintext\" : \"Table({Sin(t*0.33),Cos(t*1.1)},{t,100})\",\r\n" + 
					"        \"sinput\" : \"Table({Sin(t*0.33),Cos(t*1.1)},{t,100})\",\r\n" + 
					"        \"latex\" : \"\\\\text{Table}(\\\\{\\\\sin (t\\\\,0.33),\\\\cos (t\\\\,1.1)\\\\},\\\\{t,100\\\\})\"\r\n" + 
					"      } ]\r\n" + 
					"    }, {\r\n" + 
					"      \"title\" : \"Plot points\",\r\n" + 
					"      \"scanner\" : \"Plotter\",\r\n" + 
					"      \"error\" : \"false\",\r\n" + 
					"      \"numsubpods\" : 1,\r\n" + 
					"      \"subpods\" : [ {\r\n" + 
					"        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.css&quot; /&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.2.8/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/jsxgraphcore.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/0.99.7/geonext.min.js&quot;\\n        type=&quot;text/javascript&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.095517524720721,1.0998335153998076,1.099694211765743,-1.0999510331467535]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 0.32404302839486837;},function() {return 0.4535961214255773;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.6131168519734338;},function() {return -0.5885011172553458;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8360259786005205;},function() {return -0.9874797699088649;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9687151001182652;},function() {return -0.30733286997841935;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9968650284539189;},function() {return 0.70866977429126;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9174379552818098;},function() {return 0.9502325919585293;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7390052780594708;},function() {return 0.15337386203786346;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.48082261498864826;},function() {return -0.811093014061656;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.17075182895114532;},function() {return -0.8891911526253609;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.15774569414324865;},function() {return 0.004425697988050785;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.4692200412887275;},function() {return 0.8932061115093233;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7300583608392995;},function() {return 0.8058839576404497;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9121122039130803;},function() {return -0.16211443649971827;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9957351730622453;},function() {return -0.9529529168871809;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9719030694018208;},function() {return -0.7023970575027135;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8431877418564168;},function() {return 0.31574375491924334;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6234795452786853;},function() {return 0.9888373426941465;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.3364883584585042;},function() {return 0.5813218118144357;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.013184925133521251;},function() {return -0.46146670441591253;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.3115413635133787;},function() {return -0.9999608263946371;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.602647568421973;},function() {return -0.44569000044433316;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8287188723898358;},function() {return 0.5956343152752115;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.965358719901792;},function() {return 0.9860448308379632;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9978215790530743;},function() {return 0.2988979063644682;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9226042102393402;},function() {return -0.7148869687796676;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7478237193548898;},function() {return -0.9474378189567576;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.49234159776988917;},function() {return -0.14462127116171977;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.183728278586583;},function() {return 0.8162385236075724;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.14471213527691454;},function() {return 0.8851065280947882;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.45753589377532133;},function() {return -0.013276747223059479;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7209845231142057;},function() {return -0.897151090185845;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9066278820139979;},function() {return -0.8006117624589936;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9944322093031953;},function() {return 0.17084230974765666;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9749220735246146;},function() {return 0.9555985806128415;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8502029170863663;},function() {return 0.6960693098638897;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6337338467854989;},function() {return -0.3241299022175636;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.34887519008606005;},function() {return -0.990117442831766;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.026367558070356484;},function() {return -0.5740969614310336;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2989855372260583;},function() {return 0.4693011327771151;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5920735147072245;},function() {return 0.9998433086476912;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8212676935633646;},function() {return 0.43774896089470705;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9618345122584528;},function() {return -0.6027208470078605;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9986046585635748;},function() {return -0.9845326379049143;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9276100706332453;},function() {return -0.2904395249332599;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.756512151641241;},function() {return 0.7210481538680871;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5037749870595187;},function() {return 0.9445688168445349;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.19667278709629893;},function() {return 0.1358573496123707;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.13165341823383273;},function() {return -0.8213200831418752;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.4457722037352182;},function() {return -0.8809525579365433;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.711785342369123;},function() {return 0.022126756261962838;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.900985943032865;},function() {return 0.901025779576851;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9929563636967662;},function() {return 0.7952768415790757;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9777715876333635;},function() {return -0.17955679797714888;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.857070284703512;},function() {return -0.9581693758551366;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6438779737855393;},function() {return -0.6896870271361611;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.36120136982925244;},function() {return 0.3324906548421391;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.03954560701231674;},function() {return 0.9913199700294487;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2863777323608796;},function() {return 0.5668271321520203;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5813965291263834;},function() {return -0.47709879270357103;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.8136737375071054;},function() {return -0.99964745596635;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9581430898710656;},function() {return -0.42977362493499033;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9992141308471991;},function() {return 0.6097601572433003;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9324546661956634;},function() {return 0.9829433095858163;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7650690644362524;},function() {return 0.281958388375392;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.515120795165023;},function() {return -0.7271528468448446;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.20958310407999373;},function() {return -0.9416258104001715;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.11857181326943754;},function() {return -0.1270827840186229;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.433931016283655;},function() {return 0.8263372945385548;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.7024624178798466;},function() {return 0.876729567602604;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8951873678196818;},function() {return -0.03097503173121646;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9913078928184317;},function() {return -0.9048298761112383;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9804511163405908;},function() {return -0.7898796129768653;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8637886508173204;},function() {return 0.18825721843235974;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6539101627242901;},function() {return 0.9606651011994307;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.3734647547841147;},function() {return 0.6832507093535931;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.052716780958143236;},function() {return -0.3408253577513085;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2737201407822824;},function() {return -0.9924448300725429;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5706184678713274;},function() {return -0.5595128935482332;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.805938324428851;},function() {return 0.48485907327037797;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.954285094492698;},function() {return 0.9993732836951247;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9996498899473084;},function() {return 0.4217646174105228;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9371371546945932;},function() {return -0.6167516944712085;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7734929701222879;},function() {return -0.9812769704001121;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5263770496198482;},function() {return -0.27345516116425417;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.2224569850815534;},function() {return 0.7332005694242952;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.1054695946182271;},function() {return 0.9386090302000182;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.42201439000878305;},function() {return 0.118298261843216;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6930173704349996;},function() {return -0.8312897647130846;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.889233164455629;},function() {return -0.8724378879524822;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9894870832545356;},function() {return 0.039820880393153096;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.9829601938107485;},function() {return 0.9085630817486479;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.8703568474411396;},function() {return 0.784420499510169;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.6638286695076421;},function() {return -0.19694288945960042;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.38566321296353945;},function() {return -0.9630855611126041;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return -0.0658787901017895;},function() {return -0.6767608607837051;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.26101496301011606;},function() {return 0.3491333579443536;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.5597412047059207;},function() {return 0.9934919348314017;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.7980627991286724;},function() {return 0.5521548186698774;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9502611968351016;},function() {return -0.4925813664811991;}],  {name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 0.9999118601072672;},function() {return -0.9990208133146474;}],  {name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;/div&gt;\\n\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\r\n" + 
					"      } ]\r\n" + 
					"    } ]\r\n" + 
					"  }\r\n" + 
					"}");//
		}
	}
}
