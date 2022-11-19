package org.matheclipse.api;

import static org.junit.Assert.assertEquals;
import static org.matheclipse.api.TestPods.assertEqualsWithFloatLSFDelta;
import org.junit.Test;
import org.matheclipse.api.parser.FuzzyParserFactory;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestPodsStrict {

  static int formatsMATHML = 0;

  static int formatsTEX = 0;

  static int formatsHTML = 0;

  static {
    ToggleFeature.COMPILE = false;
    ToggleFeature.COMPILE_PRINT = true;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    Config.FUZZY_PARSER = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    Config.JAS_NO_THREADS = false;
    Config.MATHML_TRIG_LOWERCASE = false;
    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    EvalEngine.get().setPackageMode(true);
    F.initSymbols();
    FuzzyParserFactory.initialize();

    formatsMATHML = Pods.internFormat(new String[] {"mathml", "plaintext"});
    formatsTEX = Pods.internFormat(new String[] {"latex", "plaintext", "sinput"});
    formatsHTML = Pods.internFormat(new String[] {"html", "plaintext", "sinput"});
  }

  @Test
  public void testSyntaxError001() {
    ObjectNode messageJSON = TestPods.createJUnitResult("?#?", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + //
            "  \"queryresult\" : {\n" + //
            "    \"success\" : \"false\",\n" + //
            "    \"error\" : \"false\",\n" + //
            "    \"numpods\" : 0,\n" + //
            "    \"version\" : \"0.1\"\n" + //
            "  }\n" + //
            "}"); //
  }

  @Test
  public void testMarkdownHelp() {
    ObjectNode messageJSON = Pods.createResult("Sin", formatsHTML, true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Sin\",\n"
            + "        \"sinput\" : \"Sin\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Evaluated result\",\n" + "      \"scanner\" : \"Expression\",\n"
            + "      \"error\" : \"true\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Sin\",\n"
            + "        \"sinput\" : \"Sin\"\n" + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testTeXParser() {
    ObjectNode messageJSON = TestPods.createJUnitResult("\\sin 30 ^ { \\circ }", formatsHTML);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 3,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Sin(30*Degree)\",\n"
            + "        \"sinput\" : \"Sin(30*Degree)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Exact result\",\n" + "      \"scanner\" : \"Rational\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1/2\",\n"
            + "        \"sinput\" : \"Sin(30*Degree)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Decimal form\",\n" + "      \"scanner\" : \"Numeric\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"0.5\",\n"
            + "        \"sinput\" : \"N(Sin(30*Degree))\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testSoundexHelp() {
    ObjectNode messageJSON = TestPods.createJUnitResult("Cs", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 34,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
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
    ObjectNode messageJSON = TestPods.createJUnitResult("17", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 9,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"17\",\n"
            + "        \"sinput\" : \"17\",\n" + "        \"latex\" : \"17\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Number name\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"seventeen\",\n"
            + "        \"sinput\" : \"IntegerName(17,\\\"Words\\\")\",\n"
            + "        \"latex\" : \"\\\\textnormal{seventeen}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Roman numerals\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"XVII\",\n"
            + "        \"sinput\" : \"RomanNumeral(17)\",\n"
            + "        \"latex\" : \"\\\\textnormal{XVII}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Binary form\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"10001_2\",\n"
            + "        \"sinput\" : \"BaseForm(17,2)\",\n"
            + "        \"latex\" : \"{\\\\textnormal{10001}}_{2}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Prime factorization\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"17 is a prime number.\",\n"
            + "        \"sinput\" : \"FactorInteger(17)\",\n"
            + "        \"latex\" : \"\\\\left(\\n\\\\begin{array}{cc}\\n17 & 1 \\\\\\n\\\\\\\\\\n\\\\end{array}\\n\\\\right) \"\n"
            + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Residues modulo small integers\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"html\" : \"&lt;table style=\\\"border:solid 1px;\\\"&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;m&lt;/th&gt;&lt;th&gt;2&lt;/th&gt;&lt;th&gt;3&lt;/th&gt;&lt;th&gt;4&lt;/th&gt;&lt;th&gt;5&lt;/th&gt;&lt;th&gt;6&lt;/th&gt;&lt;th&gt;7&lt;/th&gt;&lt;th&gt;8&lt;/th&gt;&lt;th&gt;9&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;17 mod m&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;5&lt;/td&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;8&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;\",\n"
            + "        \"sinput\" : \"Mod(17,{2,3,4,5,6,7,8,9})\",\n"
            + "        \"latex\" : \"\\\\{1,2,1,2,5,3,1,8\\\\}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Properties\",\n" + "      \"scanner\" : \"Integer\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 2,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"17 is an odd number.\",\n"
            + "        \"sinput\" : \"OddQ(17)\"\n" + "      }, {\n"
            + "        \"plaintext\" : \"17 is the 7th prime number.\",\n"
            + "        \"sinput\" : \"PrimePi(17)\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Quadratic residues modulo 17\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{0,1,2,4,8,9,13,15,16}\",\n"
            + "        \"sinput\" : \"Union(PowerMod(Range(0,17/2),2,17))\",\n"
            + "        \"latex\" : \"\\\\{0,1,2,4,8,9,13,15,16\\\\}\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Primitive roots modulo 17\",\n"
            + "      \"scanner\" : \"Integer\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{3,5,6,7,10,11,12,14}\",\n"
            + "        \"sinput\" : \"Select(Range(16),MultiplicativeOrder(#1,17)==EulerPhi(17)&amp;)\",\n"
            + "        \"latex\" : \"\\\\{3,5,6,7,10,11,12,14\\\\}\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testRationalHalf() {
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + //
            "  \"queryresult\" : {\n" + //
            "    \"success\" : \"true\",\n" + //
            "    \"error\" : \"false\",\n" + //
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

  @Test
  public void testRationalPlus() {
    ObjectNode messageJSON = TestPods.createJUnitResult("1/2+3/4", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

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
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(Sin(x), {x, 0, 6*Pi} )", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" //
            + "  \"queryresult\" : {\n" //
            + "    \"success\" : \"true\",\n" //
            + "    \"error\" : \"false\",\n" //
            + "    \"numpods\" : 2,\n" //
            + "    \"version\" : \"0.1\",\n" //
            + "    \"pods\" : [ {\n" //
            + "      \"title\" : \"Input\",\n" //
            + "      \"scanner\" : \"Identity\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
            + "        \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
            + "        \"latex\" : \"\\\\text{Plot}(\\\\sin (x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Function\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"Plot(Sin(x),{x,0,6*Pi})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.492477796076938,1.65,20.342033717615696,-1.65]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0, (18.84955592153876)],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.492477796076938,1.65,20.342033717615696,-1.65]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, 0, (18.84955592153876)],{strokecolor:'#5e81b5'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testPlot002() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods
        .createJUnitResult("Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}) // JSForm", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\n"
            + "        \"sinput\" : \"JSForm(Plot({Sin(x),Cos(x),Tan(x)},{x,-2*Pi,2*Pi}))\",\n"
            + "        \"latex\" : \"\\\\text{JSForm}(\\\\text{Plot}(\\\\{\\\\sin (x),\\\\cos (x),\\\\tan (x)\\\\},\\\\{x,\\\\left( -2\\\\right) \\\\cdot \\\\pi,2\\\\cdot \\\\pi\\\\}))\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Result\",\n"
            + "      \"scanner\" : \"String form\",\n" + "      \"error\" : \"false\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"html\" : \"&lt;iframe srcdoc=\\\"&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot;?&amp;gt;\\n\\n&amp;lt;!DOCTYPE html PUBLIC\\n  &amp;quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&amp;quot;\\n  &amp;quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&amp;quot;&amp;gt;\\n\\n&amp;lt;html xmlns=&amp;quot;http://www.w3.org/1999/xhtml&amp;quot; style=&amp;quot;width: 100%; height: 100%; margin: 0; padding: 0&amp;quot;&amp;gt;\\n&amp;lt;head&amp;gt;\\n&amp;lt;meta charset=&amp;quot;utf-8&amp;quot;&amp;gt;\\n&amp;lt;title&amp;gt;Highlight&amp;lt;/title&amp;gt;\\n\\n&amp;lt;link rel=&amp;quot;stylesheet&amp;quot; type=&amp;quot;text/css&amp;quot; href=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/styles/default.min.css&amp;quot; /&amp;gt;\\n  &amp;lt;script type=&amp;quot;text/javascript&amp;quot; src=&amp;quot;https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/highlight.min.js&amp;quot;&amp;gt;&amp;lt;/script&amp;gt;\\n&amp;lt;script&amp;gt;hljs.initHighlightingOnLoad();&amp;lt;/script&amp;gt;&amp;lt;/head&amp;gt;\\n&amp;lt;body style=&amp;quot;width: 100%; height: 100%; margin: 0; padding: 0&amp;quot;&amp;gt;\\n\\n&amp;lt;div id=&amp;quot;highlight&amp;quot; style=&amp;quot;width: 600px; height: 800px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden&amp;quot;&amp;gt;\\n&amp;lt;pre&amp;gt;&amp;lt;code class=&amp;quot;javascript&amp;quot;&amp;gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-7.461503837897545,18.033999328253977,7.461503837897545,-18.033999328251273]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [sin(x)];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [cos(x)];} catch(e) { return Number.NaN;} }\\nfunction $f3(x) { try { return [tan(x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f3, (-6.283185307179586), (6.283185307179586)],{strokecolor:'#8fb032'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&amp;lt;/code&amp;gt;&amp;lt;/pre&amp;gt;\\n&amp;lt;/div&amp;gt;\\n&amp;lt;/body&amp;gt;\\n&amp;lt;/html&amp;gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" &gt;&lt;/iframe&gt;\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testPlotF() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult("Plot(f(x), {x, 0, 6*Pi} )", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
            + "        \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
            + "        \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Manipulate: Cannot convert to JavaScript. Function head: f\",\n"
            + "        \"sinput\" : \"Plot(f(x),{x,0,6*Pi})\",\n"
            + "        \"latex\" : \"\\\\text{Plot}(f(x),\\\\{x,0,6\\\\cdot \\\\pi\\\\})\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testSin() {
    String s = System.getProperty("os.name");
    ObjectNode messageJSON = Pods.createResult("Sin(Pi+1/2) // N", formatsTEX, true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"N(Sin(Pi+1/2))\",\n"
            + "        \"sinput\" : \"N(Sin(Pi + 1/2))\",\n"
            + "        \"latex\" : \"N(\\\\sin (\\\\pi+\\\\frac{1}{2}))\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-0.479426\",\n"
            + "        \"sinput\" : \"N(Sin(Pi + 1/2))\",\n" + "        \"latex\" : \"-0.479426\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testPolynomialQuotientRemainder() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = TestPods.createJUnitResult(" x**2-4,x-2", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
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
            + "        \"plaintext\" : \"{-4+x^2,-2+x}\",\n" //
            + "        \"sinput\" : \"{-4 + x^2,-2 + x}\",\n" //
            + "        \"latex\" : \"\\\\{-4+{x}^{2},-2+x\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Polynomial quotient and remainder\",\n" //
            + "      \"scanner\" : \"Polynomial\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{2+x,0}\",\n" //
            + "        \"sinput\" : \"PolynomialQuotientRemainder(-4 + x^2,-2 + x,x)\",\n" //
            + "        \"latex\" : \"\\\\{2+x,0\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Function\",\n" //
            + "      \"scanner\" : \"Plotter\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"sinput\" : \"Plot({-4 + x^2,-2 + x},{x,-7.0`,7.0`})\",\n" //
            + "        \"jsxgraph\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;JSXGraph&lt;/title&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;link rel=&quot;stylesheet&quot; type=&quot;text/css&quot; href=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraph.css&quot;/&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&quot;&gt;&lt;/script&gt;\\n\\n&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,48.25,8.25,-12.25]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [add(-4,pow(x,2))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [add(-2,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0, 7.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div id=&quot;jxgbox&quot; class=&quot;jxgbox&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/div&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-8.25,48.25,8.25,-12.25]});\\nboard.suspendUpdate();\\n\\nfunction $f1(x) { try { return [add(-4,pow(x,2))];} catch(e) { return Number.NaN;} }\\nfunction $f2(x) { try { return [add(-2,x)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f1, -7.0, 7.0],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f2, -7.0, 7.0],{strokecolor:'#e19c24'});\\n\\n\\nboard.unsuspendUpdate();\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js,https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js,https://cdn.jsdelivr.net/npm/json2d_jsxgraph/drawGraphics2d.min.js&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Derivative\",\n" //
            + "      \"scanner\" : \"Derivative\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{2*x,1}\",\n" //
            + "        \"sinput\" : \"D({-4 + x^2,-2 + x},x)\",\n" //
            + "        \"latex\" : \"\\\\{2\\\\cdot x,1\\\\}\"\n" //
            + "      } ]\n" //
            + "    }, {\n" //
            + "      \"title\" : \"Indefinite integral\",\n" //
            + "      \"scanner\" : \"Integral\",\n" //
            + "      \"error\" : \"false\",\n" //
            + "      \"numsubpods\" : 1,\n" //
            + "      \"subpods\" : [ {\n" //
            + "        \"plaintext\" : \"{-4*x+x^3/3,-2*x+x^2/2}\",\n" //
            + "        \"sinput\" : \"Integrate({-4 + x^2,-2 + x},x)\",\n" //
            + "        \"latex\" : \"\\\\{\\\\left( -4\\\\right) \\\\cdot x+\\\\frac{{x}^{3}}{3},\\\\left( -2\\\\right) \\\\cdot x+\\\\frac{{x}^{2}}{2}\\\\}\"\n" //
            + "      } ]\n" //
            + "    } ]\n" //
            + "  }\n" //
            + "}"); //
  }

  @Test
  public void testSinXY() {
    ObjectNode messageJSON = TestPods.createJUnitResult("Sin(x*y)", formatsTEX);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
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
            + "        \"mathcell\" : \"<iframe srcdoc=\\\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;\\n\\n&lt;!DOCTYPE html PUBLIC\\n  &quot;-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN&quot;\\n  &quot;http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd&quot;&gt;\\n\\n&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;head&gt;\\n&lt;meta charset=&quot;utf-8&quot;&gt;\\n&lt;title&gt;MathCell&lt;/title&gt;\\n&lt;/head&gt;\\n\\n&lt;body style=&quot;width: 100%; height: 100%; margin: 0; padding: 0&quot;&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.0/build/mathcell.js&quot;&gt;&lt;/script&gt;\\n&lt;script src=&quot;https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&quot;&gt;&lt;/script&gt;\\n\\n&lt;div class=&quot;mathcell&quot; style=&quot;display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden&quot;&gt;\\n&lt;script&gt;\\nvar parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n\\n&lt;/script&gt;\\n&lt;form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'&gt;\\n&lt;button type='submit' style='background-color:lightblue;'&gt;JSFiddle&lt;/button&gt;\\n&lt;textarea name='html' style='display:none;'&gt;&lt;div class=&quot;mathcell&quot; style=&quot;width:600px; height:400px;&quot;&gt;&lt;/textarea&gt;\\n&lt;textarea name='js' style='display:none;'&gt;var parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(x,y) { return [ x, y, sin(mul(x,y)) ]; }\\n\\nvar p1 = parametric( z1, [-3.5, 3.5], [-3.5, 3.5], { colormap: 'hot' } );\\n\\n  var config = { type: 'threejs' };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n&lt;/textarea&gt;\\n&lt;textarea name='resources' style='display:none;'&gt;https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js,https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.0/build/mathcell.js,https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML&lt;/textarea&gt;\\n&lt;/form&gt;\\n&lt;/div&gt;\\n&lt;/body&gt;\\n&lt;/html&gt;\\\" style=\\\"display: block; width: 100%; height: 100%; border: none;\\\" ></iframe>\"\n"
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
    // ObjectNode messageJSON = Pods.createResult("TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))",
    // formatsTEX);
    ObjectNode messageJSON = Pods.createResult( //
        "Yellow", //
        formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Yellow\",\n"
            + "        \"sinput\" : \"Yellow\",\n" + "        \"latex\" : \"Yellow\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"RGBColor(1.0,1.0,0.0)\",\n"
            + "        \"sinput\" : \"Yellow\",\n"
            + "        \"latex\" : \"\\\\text{RGBColor}(1.0,1.0,0.0)\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testComplexPlot3D() {
    ObjectNode messageJSON = Pods.createResult(
        "ComplexPlot3D((z^2 + 1)/(z^2 - 1),  {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3})",
        formatsMATHML, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"ComplexPlot3D((1+z^2)/(-1+z^2),{z,-2+(-2)*I,2+I*2},PlotRange-&gt;{0,3})\",\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mi>ComplexPlot3D</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mfrac><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>+</mo><mn>1</mn></mrow><mrow><msup><mi>z</mi><mn>2</mn></msup><mo>-</mo><mn>1</mn></mrow></mfrac><mo>,</mo><mrow><mo>{</mo><mrow><mi>z</mi><mo>,</mo><mrow><mrow><mrow><mo>(</mo><mn>-2</mn><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>&#x2148;</mi></mrow></mrow><mo>-</mo><mn>2</mn></mrow><mo>,</mo><mrow><mrow><mrow><mi>&#x2148;</mi></mrow><mo>&#0183;</mo><mn>2</mn></mrow><mo>+</mo><mn>2</mn></mrow></mrow><mo>}</mo></mrow><mo>,</mo><mrow><mi>PlotRange</mi><mo>-&gt;</mo><mrow><mo>{</mo><mrow><mn>0</mn><mo>,</mo><mn>3</mn></mrow><mo>}</mo></mrow></mrow></mrow><mo>)</mo></mrow></mrow></math>\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSFormData(var parent = document.currentScript.parentNode;\\nvar id = generateId();\\nparent.id = id;\\nMathCell( id, [  ] );\\n\\nparent.update = function( id ) {\\n\\n\\nfunction z1(z) { try { return  mul(add(1,pow(z,2)),inv(add(-1,pow(z,2))));}catch(e){return complex(Number.NaN);} }\\n\\nvar p1 = parametric( (re,im) =&gt; [ re, im, z1(complex(re,im)) ], [-2.0, 2.0], [-2.0, 2.0], { complexFunction: 'abs', colormap: 'complexArgument' } );\\n\\n  var config = { type: 'threejs', aspectRatio: [1.0,1,1], zMin: 0, zMax: 3 };\\n  var data = [p1];\\nevaluate( id, data, config );\\n\\n}\\nparent.update( id );\\n,mathcell)\",\n"
            + "        \"mathml\" : \"<math xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><mrow><mi>JSFormData</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mtext>var&nbsp;parent&nbsp;=&nbsp;document.currentScript.parentNode;</mtext><mspace linebreak='newline' /><mtext>var&nbsp;id&nbsp;=&nbsp;generateId();</mtext><mspace linebreak='newline' /><mtext>parent.id&nbsp;=&nbsp;id;</mtext><mspace linebreak='newline' /><mtext>MathCell(&nbsp;id,&nbsp;[&nbsp;&nbsp;]&nbsp;);</mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext>parent.update&nbsp;=&nbsp;function(&nbsp;id&nbsp;)&nbsp;{</mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext>function&nbsp;z1(z)&nbsp;{&nbsp;try&nbsp;{&nbsp;return&nbsp;&nbsp;mul(add(1,pow(z,2)),inv(add(-1,pow(z,2))));}catch(e){return&nbsp;complex(Number.NaN);}&nbsp;}</mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext>var&nbsp;p1&nbsp;=&nbsp;parametric(&nbsp;(re,im)&nbsp;=&gt;&nbsp;[&nbsp;re,&nbsp;im,&nbsp;z1(complex(re,im))&nbsp;],&nbsp;[-2.0,&nbsp;2.0],&nbsp;[-2.0,&nbsp;2.0],&nbsp;{&nbsp;complexFunction:&nbsp;'abs',&nbsp;colormap:&nbsp;'complexArgument'&nbsp;}&nbsp;);</mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext>&nbsp;&nbsp;var&nbsp;config&nbsp;=&nbsp;{&nbsp;type:&nbsp;'threejs',&nbsp;aspectRatio:&nbsp;[1.0,1,1],&nbsp;zMin:&nbsp;0,&nbsp;zMax:&nbsp;3&nbsp;};</mtext><mspace linebreak='newline' /><mtext>&nbsp;&nbsp;var&nbsp;data&nbsp;=&nbsp;[p1];</mtext><mspace linebreak='newline' /><mtext>evaluate(&nbsp;id,&nbsp;data,&nbsp;config&nbsp;);</mtext><mspace linebreak='newline' /><mtext></mtext><mspace linebreak='newline' /><mtext>}</mtext><mspace linebreak='newline' /><mtext>parent.update(&nbsp;id&nbsp;);</mtext><mspace linebreak='newline' /><mo>,</mo><mtext>mathcell</mtext></mrow><mo>)</mo></mrow></mrow></math>\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testHistogram() {
    ObjectNode messageJSON =
        Pods.createResult("Histogram(RandomVariate(NormalDistribution(0, 1), 200))", formatsMATHML, //
            true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);;
    // if (s.contains("Windows")) {
    // RandomVariate gives random results

    // }
  }

  @Test
  public void testList() {
    ObjectNode messageJSON = Pods.createResult("1,2,3", formatsMATHML, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"false\",\n" + "    \"error\" : {\n"
            + "      \"code\" : \"0\",\n"
            + "      \"msg\" : \"Syntax error in line: 1 - End-of-file not reached.\\n1,2,3\\n ^\"\n"
            + "    },\n" + "    \"numpods\" : 0,\n" + "    \"version\" : \"0.1\"\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testSolve001() {
    ObjectNode messageJSON = Pods.createResult("3+x==10", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"3+x==10\",\n"
            + "        \"sinput\" : \"3 + x==10\",\n" + "        \"latex\" : \"3+x == 10\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"x==7\",\n" + "        \"sinput\" : \"3 + x==10\",\n"
            + "        \"latex\" : \"x == 7\"\n" + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testSolve002() {
    ObjectNode messageJSON = Pods.createResult("x^2+1==0", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1+x^2==0\",\n"
            + "        \"sinput\" : \"1 + x^2==0\",\n" + "        \"latex\" : \"1+{x}^{2} == 0\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"x^2==-1\",\n" + "        \"sinput\" : \"1 + x^2==0\",\n"
            + "        \"latex\" : \"{x}^{2} == -1\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testSolve003() {
    ObjectNode messageJSON = Pods.createResult("Solve(x+3==10,x)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Solve(3+x==10,x)\",\n"
            + "        \"sinput\" : \"Solve(3 + x==10,x)\",\n"
            + "        \"latex\" : \"\\\\text{Solve}(3+x == 10,x)\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{{x-&gt;7}}\",\n"
            + "        \"sinput\" : \"Solve(3 + x==10,x)\",\n"
            + "        \"latex\" : \"\\\\{\\\\{x\\\\to 7\\\\}\\\\}\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testInteger4294967295() {
    ObjectNode messageJSON = Pods.createResult("2^32-1", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"-1+2^32\",\n"
            + "        \"sinput\" : \"-1 + 2^32\",\n" + "        \"latex\" : \"-1+{2}^{32}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"4294967295\",\n" + "        \"sinput\" : \"-1 + 2^32\",\n"
            + "        \"latex\" : \"4294967295\"\n" + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testNormalDistribution() {
    ObjectNode messageJSON = Pods.createResult("NormalDistribution(a,b)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"NormalDistribution(a,b)\",\n"
            + "        \"sinput\" : \"NormalDistribution(a,b)\",\n"
            + "        \"latex\" : \"\\\\text{NormalDistribution}(a,b)\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"NormalDistribution(a,b)\",\n"
            + "        \"sinput\" : \"NormalDistribution(a,b)\",\n"
            + "        \"latex\" : \"\\\\text{NormalDistribution}(a,b)\"\n" + "      } ]\n"
            + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testLogic001() {
    ObjectNode messageJSON = Pods.createResult("a&&b||c", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\n"
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\n"
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"(a&amp;&amp;b)||c\",\n"
            + "        \"sinput\" : \"(a&amp;&amp;b)||c\",\n"
            + "        \"latex\" : \"\\\\left( a \\\\land b\\\\right)  \\\\lor c\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testLogic002() {
    ObjectNode messageJSON = Pods.createResult("a&b|c", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"b*(a&amp;)|c\",\n"
            + "        \"sinput\" : \"b*(a&amp;)|c\",\n"
            + "        \"latex\" : \"b\\\\,a\\\\&\\\\text{|}c\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Evaluated result\",\n" + "      \"scanner\" : \"Expression\",\n"
            + "      \"error\" : \"true\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"b*(a&amp;)|c\",\n"
            + "        \"sinput\" : \"b*(a&amp;)|c\",\n"
            + "        \"latex\" : \"b\\\\,a\\\\&\\\\text{|}c\"\n" + "      } ]\n" + "    } ]\n"
            + "  }\n" + "}"); //
  }

  @Test
  public void testTimes() {
    ObjectNode messageJSON = Pods.createResult("10*11*12", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"10*11*12\",\n"
            + "        \"sinput\" : \"10*11*12\",\n"
            + "        \"latex\" : \"10\\\\cdot 11\\\\cdot 12\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Evaluated result\",\n" + "      \"scanner\" : \"Expression\",\n"
            + "      \"error\" : \"true\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"1320\",\n"
            + "        \"sinput\" : \"10*11*12\",\n" + "        \"latex\" : \"1320\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testSimplify() {
    ObjectNode messageJSON = Pods.createResult("FullSimplify(Sqrt(9-4*Sqrt(5)))", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"FullSimplify(Sqrt(9-4*Sqrt(5)))\",\n"
            + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\n"
            + "        \"latex\" : \"\\\\text{FullSimplify}(\\\\sqrt{\\\\left( 9 - 4\\\\cdot \\\\sqrt{5}\\\\right) })\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-2+Sqrt(5)\",\n"
            + "        \"sinput\" : \"FullSimplify(Sqrt(9 - 4*Sqrt(5)))\",\n"
            + "        \"latex\" : \"-2+\\\\sqrt{5}\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testIntegrate001() {
    ObjectNode messageJSON = Pods.createResult("Integrate(Sin(x),x)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"Integrate(Sin(x),x)\",\n"
            + "        \"sinput\" : \"Integrate(Sin(x),x)\",\n"
            + "        \"latex\" : \"\\\\int  \\\\sin (x)\\\\,\\\\mathrm{d}x\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-Cos(x)\",\n"
            + "        \"sinput\" : \"Integrate(Sin(x),x)\",\n"
            + "        \"latex\" : \" - \\\\cos (x)\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testIntegrate002() {
    ObjectNode messageJSON = Pods.createResult("integrate(Tan(x)*Cos(x)*Pi,x)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"latex\" : \"\\\\int  \\\\pi\\\\,\\\\cos (x)\\\\,\\\\tan (x)\\\\,\\\\mathrm{d}x\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"-Pi*Cos(x)\",\n"
            + "        \"sinput\" : \"Integrate(Pi*Cos(x)*Tan(x),x)\",\n"
            + "        \"latex\" : \" - \\\\pi\\\\,\\\\cos (x)\"\n" + "      } ]\n" + "    } ]\n"
            + "  }\n" + "}"); //
  }

  @Test
  public void testDerivative001() {
    ObjectNode messageJSON = Pods.createResult("D(tan(x^3),x)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"D(Tan(x^3),x)\",\n"
            + "        \"sinput\" : \"D(Tan(x^3),x)\",\n"
            + "        \"latex\" : \"\\\\frac{\\\\partial \\\\tan ({x}^{3})}{\\\\partial x}\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"3*x^2*Sec(x^3)^2\",\n"
            + "        \"sinput\" : \"D(Tan(x^3),x)\",\n"
            + "        \"latex\" : \"3\\\\cdot {x}^{2}\\\\cdot {\\\\sec ({x}^{3})}^{2}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testListPlot001() {
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = Pods.createResult("ListPlot({3,Sin(1),Pi,3/4,42,1.2})", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"ListPlot({3,Sin(1),Pi,3/4,42,1.2})\",\n"
            + "        \"sinput\" : \"ListPlot({3,Sin(1),Pi,3/4,42,1.2`})\",\n"
            + "        \"latex\" : \"\\\\text{ListPlot}(\\\\{3,\\\\sin (1),\\\\pi,\\\\frac{3}{4},42,1.2\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSFormData(var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.9,44.6125,7.9,-1.8624999999999998]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 3;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return (0.8414709848078965);}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return Math.PI;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 3/4;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 42;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 6;},function() {return 1.2;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n,jsxgraph)\",\n"
            + "        \"sinput\" : \"ListPlot({3,Sin(1),Pi,3/4,42,1.2`})\",\n"
            + "        \"latex\" : \"\\\\text{JSFormData}(\\\\textnormal{var board = JXG.JSXGraph.initBoard('jxgbox', \\\\{axis:true,boundingbox:[-0.9,44.6125,7.9,-1.8624999999999998]\\\\});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() \\\\{return 1;\\\\},function() \\\\{return 3;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 2;\\\\},function() \\\\{return (0.8414709848078965);\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 3;\\\\},function() \\\\{return Math.PI;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 4;\\\\},function() \\\\{return 3/4;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 5;\\\\},function() \\\\{return 42;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 6;\\\\},function() \\\\{return 1.2;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\n\\n\\nboard.unsuspendUpdate();\\n},\\\\textnormal{jsxgraph})\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testListPlot002() {
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON =
        Pods.createResult("Table({Sin(t*0.33), Cos(t*1.1)}, {t, 100})", formatsTEX, //
            true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEqualsWithFloatLSFDelta(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Table({Sin(t*0.33),Cos(t*1.1)},{t,100})\",\n"
            + "        \"sinput\" : \"Table({Sin(t*0.33`),Cos(t*1.1`)},{t,100})\",\n"
            + "        \"latex\" : \"\\\\text{Table}(\\\\{\\\\sin (t\\\\cdot 0.33),\\\\cos (t\\\\cdot 1.1)\\\\},\\\\{t,100\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"{{0.32404302839486837,0.4535961214255773},{0.6131168519734338,-0.5885011172553458},{0.8360259786005205,-0.9874797699088649},{0.9687151001182652,-0.30733286997841935},{0.9968650284539189,0.70866977429126},{0.9174379552818098,0.9502325919585293},{0.7390052780594708,0.15337386203786346},{0.48082261498864826,-0.811093014061656},{0.17075182895114532,-0.8891911526253609},{-0.15774569414324865,0.004425697988050785},{-0.4692200412887275,0.8932061115093233},{-0.7300583608392995,0.8058839576404497},{-0.9121122039130803,-0.16211443649971827},{-0.9957351730622453,-0.9529529168871809},{-0.9719030694018208,-0.7023970575027135},{-0.8431877418564167,0.31574375491924334},{-0.6234795452786853,0.9888373426941465},{-0.3364883584585042,0.5813218118144357},{-0.013184925133521251,-0.46146670441591253},{0.3115413635133787,-0.9999608263946371},{0.602647568421973,-0.44569000044433316},{0.8287188723898359,0.5956343152752115},{0.965358719901792,0.9860448308379632},{0.9978215790530743,0.2988979063644682},{0.9226042102393402,-0.7148869687796675},{0.7478237193548898,-0.9474378189567576},{0.49234159776988917,-0.14462127116171977},{0.183728278586583,0.8162385236075724},{-0.14471213527691454,0.8851065280947882},{-0.4575358937753214,-0.013276747223059479},{-0.7209845231142057,-0.897151090185845},{-0.9066278820139979,-0.8006117624589936},{-0.9944322093031953,0.17084230974765666},{-0.9749220735246146,0.9555985806128415},{-0.8502029170863663,0.6960693098638897},{-0.6337338467854989,-0.3241299022175636},{-0.34887519008606005,-0.990117442831766},{-0.026367558070356484,-0.5740969614310336},{0.2989855372260583,0.4693011327771151},{0.5920735147072245,0.9998433086476912},{0.8212676935633646,0.43774896089470705},{0.9618345122584528,-0.6027208470078607},{0.9986046585635748,-0.9845326379049143},{0.9276100706332453,-0.2904395249332599},{0.756512151641241,0.7210481538680871},{0.5037749870595187,0.9445688168445349},{0.19667278709629893,0.1358573496123707},{-0.13165341823383273,-0.8213200831418752},{-0.4457722037352182,-0.8809525579365433},{-0.711785342369123,0.022126756261962838},{-0.900985943032865,0.901025779576851},{-0.9929563636967662,0.7952768415790757},{-0.9777715876333635,-0.17955679797714888},{-0.857070284703512,-0.9581693758551366},{-0.6438779737855393,-0.6896870271361613},{-0.36120136982925244,0.3324906548421391},{-0.03954560701231674,0.9913199700294487},{0.2863777323608796,0.5668271321520202},{0.5813965291263834,-0.47709879270357103},{0.8136737375071054,-0.99964745596635},{0.9581430898710656,-0.42977362493499033},{0.9992141308471991,0.6097601572433005},{0.9324546661956634,0.9829433095858163},{0.7650690644362526,0.281958388375392},{0.515120795165023,-0.7271528468448446},{0.20958310407999373,-0.9416258104001715},{-0.11857181326943754,-0.1270827840186229},{-0.433931016283655,0.8263372945385548},{-0.7024624178798466,0.876729567602604},{-0.8951873678196818,-0.03097503173121646},{-0.9913078928184317,-0.9048298761112383},{-0.9804511163405908,-0.7898796129768653},{-0.8637886508173204,0.18825721843235974},{-0.6539101627242901,0.9606651011994307},{-0.3734647547841147,0.6832507093535931},{-0.052716780958143236,-0.3408253577513085},{0.2737201407822824,-0.9924448300725429},{0.5706184678713274,-0.5595128935482332},{0.805938324428851,0.48485907327037797},{0.954285094492698,0.9993732836951247},{0.9996498899473084,0.4217646174105228},{0.9371371546945932,-0.6167516944712085},{0.7734929701222879,-0.9812769704001121},{0.5263770496198482,-0.27345516116425417},{0.2224569850815534,0.7332005694242952},{-0.1054695946182271,0.9386090302000182},{-0.42201439000878305,0.118298261843216},{-0.6930173704349996,-0.8312897647130846},{-0.889233164455629,-0.8724378879524822},{-0.9894870832545356,0.039820880393153096},{-0.9829601938107485,0.9085630817486479},{-0.8703568474411396,0.784420499510169},{-0.6638286695076421,-0.19694288945960042},{-0.38566321296353945,-0.9630855611126041},{-0.0658787901017895,-0.6767608607837051},{0.26101496301011606,0.3491333579443536},{0.5597412047059207,0.9934919348314017},{0.7980627991286724,0.5521548186698774},{0.9502611968351016,-0.4925813664811991},{0.9999118601072672,-0.9990208133146474}}\",\n"
            + "        \"sinput\" : \"Table({Sin(t*0.33`),Cos(t*1.1`)},{t,100})\",\n"
            + "        \"latex\" : \"\\\\{\\\\{0.324043,0.453596\\\\},\\\\{0.613117,-0.588501\\\\},\\\\{0.836026,-0.98748\\\\},\\\\{0.968715,-0.307333\\\\},\\\\{0.996865,0.70867\\\\},\\\\{0.917438,0.950233\\\\},\\\\{0.739005,0.153374\\\\},\\\\{0.480823,-0.811093\\\\},\\\\{0.170752,-0.889191\\\\},\\\\{-0.157746,0.0044257\\\\},\\\\{-0.46922,0.893206\\\\},\\\\{-0.730058,0.805884\\\\},\\\\{-0.912112,-0.162114\\\\},\\\\{-0.995735,-0.952953\\\\},\\\\{-0.971903,-0.702397\\\\},\\\\{-0.843188,0.315744\\\\},\\\\{-0.62348,0.988837\\\\},\\\\{-0.336488,0.581322\\\\},\\\\{-0.0131849,-0.461467\\\\},\\\\{0.311541,-0.999961\\\\},\\\\{0.602648,-0.44569\\\\},\\\\{0.828719,0.595634\\\\},\\\\{0.965359,0.986045\\\\},\\\\{0.997822,0.298898\\\\},\\\\{0.922604,-0.714887\\\\},\\\\{0.747824,-0.947438\\\\},\\\\{0.492342,-0.144621\\\\},\\\\{0.183728,0.816239\\\\},\\\\{-0.144712,0.885107\\\\},\\\\{-0.457536,-0.0132767\\\\},\\\\{-0.720985,-0.897151\\\\},\\\\{-0.906628,-0.800612\\\\},\\\\{-0.994432,0.170842\\\\},\\\\{-0.974922,0.955599\\\\},\\\\{-0.850203,0.696069\\\\},\\\\{-0.633734,-0.32413\\\\},\\\\{-0.348875,-0.990117\\\\},\\\\{-0.0263676,-0.574097\\\\},\\\\{0.298986,0.469301\\\\},\\\\{0.592074,0.999843\\\\},\\\\{0.821268,0.437749\\\\},\\\\{0.961835,-0.602721\\\\},\\\\{0.998605,-0.984533\\\\},\\\\{0.92761,-0.29044\\\\},\\\\{0.756512,0.721048\\\\},\\\\{0.503775,0.944569\\\\},\\\\{0.196673,0.135857\\\\},\\\\{-0.131653,-0.82132\\\\},\\\\{-0.445772,-0.880953\\\\},\\\\{-0.711785,0.0221268\\\\},\\\\{-0.900986,0.901026\\\\},\\\\{-0.992956,0.795277\\\\},\\\\{-0.977772,-0.179557\\\\},\\\\{-0.85707,-0.958169\\\\},\\\\{-0.643878,-0.689687\\\\},\\\\{-0.361201,0.332491\\\\},\\\\{-0.0395456,0.99132\\\\},\\\\{0.286378,0.566827\\\\},\\\\{0.581397,-0.477099\\\\},\\\\{0.813674,-0.999647\\\\},\\\\{0.958143,-0.429774\\\\},\\\\{0.999214,0.60976\\\\},\\\\{0.932455,0.982943\\\\},\\\\{0.765069,0.281958\\\\},\\\\{0.515121,-0.727153\\\\},\\\\{0.209583,-0.941626\\\\},\\\\{-0.118572,-0.127083\\\\},\\\\{-0.433931,0.826337\\\\},\\\\{-0.702462,0.87673\\\\},\\\\{-0.895187,-0.030975\\\\},\\\\{-0.991308,-0.90483\\\\},\\\\{-0.980451,-0.78988\\\\},\\\\{-0.863789,0.188257\\\\},\\\\{-0.65391,0.960665\\\\},\\\\{-0.373465,0.683251\\\\},\\\\{-0.0527168,-0.340825\\\\},\\\\{0.27372,-0.992445\\\\},\\\\{0.570618,-0.559513\\\\},\\\\{0.805938,0.484859\\\\},\\\\{0.954285,0.999373\\\\},\\\\{0.99965,0.421765\\\\},\\\\{0.937137,-0.616752\\\\},\\\\{0.773493,-0.981277\\\\},\\\\{0.526377,-0.273455\\\\},\\\\{0.222457,0.733201\\\\},\\\\{-0.10547,0.938609\\\\},\\\\{-0.422014,0.118298\\\\},\\\\{-0.693017,-0.83129\\\\},\\\\{-0.889233,-0.872438\\\\},\\\\{-0.989487,0.0398209\\\\},\\\\{-0.98296,0.908563\\\\},\\\\{-0.870357,0.78442\\\\},\\\\{-0.663829,-0.196943\\\\},\\\\{-0.385663,-0.963086\\\\},\\\\{-0.0658788,-0.676761\\\\},\\\\{0.261015,0.349133\\\\},\\\\{0.559741,0.993492\\\\},\\\\{0.798063,0.552155\\\\},\\\\{0.950261,-0.492581\\\\},\\\\{0.999912,-0.999021\\\\}\\\\}\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testListPlot004() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON =
        Pods.createResult("Histogram({1, 2, 3, None, 3, 5, f(), 2, 1, foo, 2, 3})", formatsTEX, //
            true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Histogram({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\n"
            + "        \"sinput\" : \"Histogram({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\n"
            + "        \"latex\" : \"\\\\text{Histogram}(\\\\{1,2,3,None,3,5,f(),2,1,foo,2,3\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSFormData(        var target_plotly = document.getElementById('plotly');\\n        var layout = {\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n};\\n\\nvar trace0 =\\n{\\nx: [\\\"1.0\\\",\\\"2.0\\\",\\\"3.0\\\",\\\"3.0\\\",\\\"5.0\\\",\\\"2.0\\\",\\\"1.0\\\",\\\"2.0\\\",\\\"3.0\\\"],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target_plotly, data, layout);        ,plotly)\",\n"
            + "        \"sinput\" : \"Histogram({1,2,3,None,3,5,f(),2,1,foo,2,3})\",\n"
            + "        \"latex\" : \"\\\\text{JSFormData}(\\\\textnormal{        var target\\\\_plotly = document.getElementById('plotly');\\n        var layout = \\\\{\\n    title: 'Histogram',\\n    height: 400,\\n    width: 600,\\n    autosize: true,\\n\\n\\n\\\\};\\n\\nvar trace0 =\\n\\\\{\\nx: [\\\"1.0\\\",\\\"2.0\\\",\\\"3.0\\\",\\\"3.0\\\",\\\"5.0\\\",\\\"2.0\\\",\\\"1.0\\\",\\\"2.0\\\",\\\"3.0\\\"],\\nopacity: '1.0',\\nnbinsx: 0,\\nautobinx: false,\\nnbinsy: 0,\\nautobiny: false,\\n    histnorm: '',\\n    histfunc: 'count',\\nxaxis: 'x',\\nyaxis: 'y',\\ntype: 'histogram',\\nname: '',\\n\\\\};\\n\\n        var data = [ trace0];\\nPlotly.newPlot(target\\\\_plotly, data, layout);        },\\\\textnormal{plotly})\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testListPlot005() {
    EvalEngine.resetModuleCounter4JUnit();

    ObjectNode messageJSON = Pods.createResult("ListPlot({1, 2, 3, 4, 5})", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"ListPlot({1,2,3,4,5})\",\n"
            + "        \"sinput\" : \"ListPlot({1,2,3,4,5})\",\n"
            + "        \"latex\" : \"\\\\text{ListPlot}(\\\\{1,2,3,4,5\\\\})\"\n" + "      } ]\n"
            + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSFormData(var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.85,5.75,6.85,0.25]});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() {return 1;},function() {return 1;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 2;},function() {return 2;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 3;},function() {return 3;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 4;},function() {return 4;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\nboard.create('point', [function() {return 5;},function() {return 5;}],  {color:'#5e81b5' ,name:'', face:'o', size: 2 } );\\n\\n\\nboard.unsuspendUpdate();\\n,jsxgraph)\",\n"
            + "        \"sinput\" : \"ListPlot({1,2,3,4,5})\",\n"
            + "        \"latex\" : \"\\\\text{JSFormData}(\\\\textnormal{var board = JXG.JSXGraph.initBoard('jxgbox', \\\\{axis:true,boundingbox:[-0.85,5.75,6.85,0.25]\\\\});\\nboard.suspendUpdate();\\n\\nboard.create('point', [function() \\\\{return 1;\\\\},function() \\\\{return 1;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 2;\\\\},function() \\\\{return 2;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 3;\\\\},function() \\\\{return 3;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 4;\\\\},function() \\\\{return 4;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\nboard.create('point', [function() \\\\{return 5;\\\\},function() \\\\{return 5;\\\\}],  \\\\{color:'\\\\#5e81b5' ,name:'', face:'o', size: 2 \\\\} );\\n\\n\\nboard.unsuspendUpdate();\\n},\\\\textnormal{jsxgraph})\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testListPlot003() {
    EvalEngine.resetModuleCounter4JUnit();
    ObjectNode messageJSON = Pods.createResult("plot({x,x^2,x^3,x^4},{x,-5,5})", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);
    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"Plot({x,x^2,x^3,x^4},{x,-5,5})\",\n"
            + "        \"sinput\" : \"Plot({x,x^2,x^3,x^4},{x,-5,5})\",\n"
            + "        \"latex\" : \"\\\\text{Plot}(\\\\{x,{x}^{2},{x}^{3},{x}^{4}\\\\},\\\\{x,-5,5\\\\})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"JSFormData(var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-6.05,499.5430040000006,6.05,-136.47972400000012]});\\nboard.suspendUpdate();\\n\\nfunction $f5(x) { try { return [x];} catch(e) { return Number.NaN;} }\\nfunction $f6(x) { try { return [pow(x,2)];} catch(e) { return Number.NaN;} }\\nfunction $f7(x) { try { return [pow(x,3)];} catch(e) { return Number.NaN;} }\\nfunction $f8(x) { try { return [pow(x,4)];} catch(e) { return Number.NaN;} }\\nboard.create('functiongraph',[$f5, -5, 5],{strokecolor:'#5e81b5'});\\nboard.create('functiongraph',[$f6, -5, 5],{strokecolor:'#e19c24'});\\nboard.create('functiongraph',[$f7, -5, 5],{strokecolor:'#8fb032'});\\nboard.create('functiongraph',[$f8, -5, 5],{strokecolor:'#eb6235'});\\n\\n\\nboard.unsuspendUpdate();\\n,jsxgraph)\",\n"
            + "        \"sinput\" : \"Plot({x,x^2,x^3,x^4},{x,-5,5})\",\n"
            + "        \"latex\" : \"\\\\text{JSFormData}(\\\\textnormal{var board = JXG.JSXGraph.initBoard('jxgbox', \\\\{axis:true,boundingbox:[-6.05,499.5430040000006,6.05,-136.47972400000012]\\\\});\\nboard.suspendUpdate();\\n\\nfunction \\\\$f5(x) \\\\{ try \\\\{ return [x];\\\\} catch(e) \\\\{ return Number.NaN;\\\\} \\\\}\\nfunction \\\\$f6(x) \\\\{ try \\\\{ return [pow(x,2)];\\\\} catch(e) \\\\{ return Number.NaN;\\\\} \\\\}\\nfunction \\\\$f7(x) \\\\{ try \\\\{ return [pow(x,3)];\\\\} catch(e) \\\\{ return Number.NaN;\\\\} \\\\}\\nfunction \\\\$f8(x) \\\\{ try \\\\{ return [pow(x,4)];\\\\} catch(e) \\\\{ return Number.NaN;\\\\} \\\\}\\nboard.create('functiongraph',[\\\\$f5, -5, 5],\\\\{strokecolor:'\\\\#5e81b5'\\\\});\\nboard.create('functiongraph',[\\\\$f6, -5, 5],\\\\{strokecolor:'\\\\#e19c24'\\\\});\\nboard.create('functiongraph',[\\\\$f7, -5, 5],\\\\{strokecolor:'\\\\#8fb032'\\\\});\\nboard.create('functiongraph',[\\\\$f8, -5, 5],\\\\{strokecolor:'\\\\#eb6235'\\\\});\\n\\n\\nboard.unsuspendUpdate();\\n},\\\\textnormal{jsxgraph})\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testGraph001() {
    ObjectNode messageJSON = Pods.createResult( //
        "Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)})", //
        formatsTEX, //
        true);

    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
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
    ObjectNode messageJSON = Pods.createResult("HornerForm(x^2+x^3+2*x^14)", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"HornerForm(x^2+x^3+2*x^14)\",\n"
            + "        \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\n"
            + "        \"latex\" : \"\\\\text{HornerForm}({x}^{2}+{x}^{3} + 2\\\\cdot {x}^{14})\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"x^2*(1+x*(1+2*x^11))\",\n"
            + "        \"sinput\" : \"HornerForm(x^2 + x^3 + 2*x^14)\",\n"
            + "        \"latex\" : \"{x}^{2}\\\\,\\\\left( 1 + x\\\\,\\\\left( 1 + 2\\\\cdot {x}^{11}\\\\right) \\\\right) \"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testETimesPi001() {
    ObjectNode messageJSON = Pods.createResult( //
        "E*Pi", //
        formatsTEX, //
        true);

    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"E*Pi\",\n"
            + "        \"sinput\" : \"E*Pi\",\n" + "        \"latex\" : \"e\\\\,\\\\pi\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"E*Pi\",\n" + "        \"sinput\" : \"E*Pi\",\n"
            + "        \"latex\" : \"e\\\\,\\\\pi\"\n" + "      } ]\n" + "    } ]\n" + "  }\n"
            + "}"); //
  }

  @Test
  public void testCoshIntegral001() {
    ObjectNode messageJSON = Pods.createResult( //
        "CoshIntegral", //
        formatsTEX, //
        true);

    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"CoshIntegral\",\n"
            + "        \"sinput\" : \"CoshIntegral\",\n" + "        \"latex\" : \"CoshIntegral\"\n"
            + "      } ]\n" + "    }, {\n" + "      \"title\" : \"Evaluated result\",\n"
            + "      \"scanner\" : \"Expression\",\n" + "      \"error\" : \"true\",\n"
            + "      \"numsubpods\" : 1,\n" + "      \"subpods\" : [ {\n"
            + "        \"plaintext\" : \"CoshIntegral\",\n"
            + "        \"sinput\" : \"CoshIntegral\",\n" + "        \"latex\" : \"CoshIntegral\"\n"
            + "      } ]\n" + "    } ]\n" + "  }\n" + "}"); //
  }

  @Test
  public void testPolynomial001() {
    ObjectNode messageJSON = Pods.createResult("-x^2 + 4*x + 4", formatsTEX, //
        true);
    final String jsonStr = TestPods.toPrettyStringNormalizingNewline(messageJSON);

    assertEquals(jsonStr, //
        "{\n" + "  \"queryresult\" : {\n" + "    \"success\" : \"true\",\n"
            + "    \"error\" : \"false\",\n" + "    \"numpods\" : 2,\n"
            + "    \"version\" : \"0.1\",\n" + "    \"pods\" : [ {\n"
            + "      \"title\" : \"Input\",\n" + "      \"scanner\" : \"Identity\",\n"
            + "      \"error\" : \"false\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"4+4*x-x^2\",\n"
            + "        \"sinput\" : \"4 + 4*x - x^2\",\n"
            + "        \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\n" + "      } ]\n" + "    }, {\n"
            + "      \"title\" : \"Evaluated result\",\n" + "      \"scanner\" : \"Expression\",\n"
            + "      \"error\" : \"true\",\n" + "      \"numsubpods\" : 1,\n"
            + "      \"subpods\" : [ {\n" + "        \"plaintext\" : \"4+4*x-x^2\",\n"
            + "        \"sinput\" : \"4 + 4*x - x^2\",\n"
            + "        \"latex\" : \"4 + 4\\\\cdot x - {x}^{2}\"\n" + "      } ]\n" + "    } ]\n"
            + "  }\n" + "}"); //
  }
}
