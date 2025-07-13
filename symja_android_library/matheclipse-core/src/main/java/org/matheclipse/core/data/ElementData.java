package org.matheclipse.core.data;

import static org.matheclipse.core.expression.F.NIL;
import java.util.HashMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * ElementData("name", "property")
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the value of the property for the chemical specified by name.
 *
 * </blockquote>
 *
 * <pre>
 * ElementData(n, "property")
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the value of the property for the nth chemical element.
 *
 * </blockquote>
 *
 * <p>
 * <code>ElementData</code> uses data from
 * <a href= "https://en.wikipedia.org/wiki/List_of_data_references_for_chemical_elements">Wikipedia
 * - List of data references for chemical elements</a>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; ElementData(74)
 * "Tungsten"
 *
 * &gt;&gt; ElementData("He", "AbsoluteBoilingPoint")
 * 4.22
 *
 * &gt;&gt; ElementData("Carbon", "IonizationEnergies")
 * {1086.5,2352.6,4620.5,6222.7,37831,47277.0}
 *
 * &gt;&gt; ElementData(16, "ElectronConfigurationString")
 * "[Ne] 3s2 3p4"
 *
 * &gt;&gt; ElementData(73, "ElectronConfiguration")
 * {{2},{2,6},{2,6,10},{2,6,10,14},{2,6,3},{2}}
 * </pre>
 *
 * <p>
 * Some properties are not appropriate for certain elements:
 *
 * <pre>
 * &gt;&gt; ElementData("He", "ElectroNegativity")
 * Missing(NotApplicable)
 * </pre>
 *
 * <p>
 * Some data is missing:
 *
 * <pre>
 * &gt;&gt; ElementData("Tc", "SpecificHeat")
 * Missing(NotAvailable)
 * </pre>
 *
 * <p>
 * All the known properties:
 *
 * <pre>
 * &gt;&gt; ElementData("Properties")
 * {"Abbreviation","AbsoluteBoilingPoint","AbsoluteMeltingPoint","AtomicNumber","AtomicRadius","AtomicWeight","Block",
 * "BoilingPoint","BrinellHardness","BulkModulus","CovalentRadius","CrustAbundance","Density","DiscoveryYear","ElectroNegativity",
 * "ElectronAffinity","ElectronConfiguration","ElectronConfigurationString","ElectronShellConfiguration","FusionHeat",
 * "Group","IonizationEnergies","LiquidDensity","MeltingPoint","MohsHardness","Name","Period","PoissonRatio","Series",
 * "ShearModulus","SpecificHeat","StandardName","ThermalConductivity","VanDerWaalsRadius","VaporizationHeat","VickersHardness",
 * "YoungModulus"}
 * </pre>
 */
public class ElementData extends AbstractFunctionEvaluator {

  public static final String[] PROPERTIES_DATA = {"StandardName", "AtomicNumber", "Abbreviation",
      "AbsoluteBoilingPoint", "AbsoluteMeltingPoint", "AtomicRadius", "AtomicWeight", "Block",
      "BoilingPoint", "BrinellHardness", "BulkModulus", "CovalentRadius", "CrustAbundance",
      "Density", "DiscoveryYear", "ElectroNegativity", "ElectronAffinity", "ElectronConfiguration",
      "ElectronConfigurationString", "ElectronShellConfiguration", "FusionHeat", "Group",
      "IonizationEnergies", "LiquidDensity", "MeltingPoint", "MohsHardness", "Name", "Period",
      "PoissonRatio", "Series", "ShearModulus", "SpecificHeat", "ThermalConductivity",
      "VanDerWaalsRadius", "VaporizationHeat", "VickersHardness", "YoungModulus"};

  private static java.util.Map<IExpr, IExpr> MAP_NUMBER_NAME = new HashMap<IExpr, IExpr>();

  private static java.util.Map<IExpr, IAST> MAP_NAME_DATA = new HashMap<IExpr, IAST>();

  public ElementData() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.size() == 2) {
      if (ast.arg1().isInteger()) {
        IExpr temp = MAP_NUMBER_NAME.get(ast.arg1());
        if (temp != null) {
          return temp;
        }
      }
      if (ast.arg1() instanceof IStringX) {
        if (ast.arg1().toString().equals("Properties")) {
          return F.mapRange(0, PROPERTIES_DATA.length, i -> F.$str(PROPERTIES_DATA[i]));
        }
      }
    } else {
      if (ast.arg1() instanceof IStringX || ast.arg1().isInteger()) {
        IAST temp = MAP_NAME_DATA.get(ast.arg1());
        if (temp != null) {
          return dataPoint(ast, temp);
        }
      }
    }
    return NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  private IExpr dataPoint(final IAST ast, IAST propertyList) {
    String propertyStr = ast.arg2().toString();
    if (propertyStr.equals("AtomicNumber")) {
      return propertyList.arg1();
    }
    if (propertyStr.equals("Abbreviation")) {
      return propertyList.arg2();
    }
    if (propertyStr.equals("StandardName")) {
      return propertyList.arg3();
    }
    if (propertyStr.equals("Name")) {
      return propertyList.arg4();
    }
    if (propertyStr.equals("Block")) {
      return propertyList.arg5();
    }
    if (propertyStr.equals("Group")) {
      return propertyList.get(6);
    }
    if (propertyStr.equals("Period")) {
      return propertyList.get(7);
    }
    if (propertyStr.equals("Series")) {
      return propertyList.get(8);
    }
    if (propertyStr.equals("AtomicWeight")) {
      return propertyList.get(9);
    }
    if (propertyStr.equals("DiscoveryYear")) {
      return propertyList.get(10);
    }
    if (propertyStr.equals("LiquidDensity")) {
      return propertyList.get(11);
    }
    if (propertyStr.equals("Density")) {
      return propertyList.get(12);
    }
    if (propertyStr.equals("AbsoluteMeltingPoint")) {
      return propertyList.get(13);
    }
    if (propertyStr.equals("MeltingPoint")) {
      return propertyList.get(14);
    }
    if (propertyStr.equals("AbsoluteBoilingPoint")) {
      return propertyList.get(15);
    }
    if (propertyStr.equals("BoilingPoint")) {
      return propertyList.get(16);
    }
    if (propertyStr.equals("SpecificHeat")) {
      return propertyList.get(17);
    }
    if (propertyStr.equals("FusionHeat")) {
      return propertyList.get(18);
    }
    if (propertyStr.equals("VaporizationHeat")) {
      return propertyList.get(19);
    }
    if (propertyStr.equals("ElectroNegativity")) {
      return propertyList.get(20);
    }
    if (propertyStr.equals("CrustAbundance")) {
      return propertyList.get(21);
    }
    if (propertyStr.equals("MohsHardness")) {
      return propertyList.get(22);
    }
    if (propertyStr.equals("VickersHardness")) {
      return propertyList.get(23);
    }
    if (propertyStr.equals("BrinellHardness")) {
      return propertyList.get(24);
    }
    if (propertyStr.equals("AtomicRadius")) {
      return propertyList.get(25);
    }
    if (propertyStr.equals("VanDerWaalsRadius")) {
      return propertyList.get(26);
    }
    if (propertyStr.equals("CovalentRadius")) {
      return propertyList.get(27);
    }
    if (propertyStr.equals("IonizationEnergies")) {
      return propertyList.get(28);
    }
    if (propertyStr.equals("ElectronAffinity")) {
      return propertyList.get(29);
    }
    if (propertyStr.equals("ThermalConductivity")) {
      return propertyList.get(30);
    }
    if (propertyStr.equals("YoungModulus")) {
      return propertyList.get(31);
    }
    if (propertyStr.equals("PoissonRatio")) {
      return propertyList.get(32);
    }
    if (propertyStr.equals("BulkModulus")) {
      if (propertyList.size() > 33) {
        return propertyList.get(33);
      }
      return F.Missing(S.NotAvailable);
    }
    if (propertyStr.equals("ShearModulus")) {
      if (propertyList.size() > 34) {
        return propertyList.get(34);
      }
      return F.Missing(S.NotAvailable);
    }
    if (propertyStr.equals("ElectronConfiguration")) {
      if (propertyList.size() > 35) {
        return propertyList.get(35);
      }
      return F.Missing(S.NotAvailable);
    }
    if (propertyStr.equals("ElectronConfigurationString")) {
      if (propertyList.size() > 36) {
        return propertyList.get(36);
      }
      return F.Missing(S.NotAvailable);
    }
    if (propertyStr.equals("ElectronShellConfiguration")) {
      if (propertyList.size() > 37) {
        return propertyList.get(37);
      }
      return F.Missing(S.NotAvailable);
    }
    return F.Missing(S.NotAvailable);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // 1: "AtomicNumber"
    // 2: "Abbreviation"
    // 3: "StandardName"
    // 4: "Name"
    // 5: "Block"
    // 6: "Group"
    // 7: "Period"
    // 8: "Series"
    // 9: "AtomicWeight"
    // 10: "DiscoveryYear"
    // 11: "LiquidDensity"
    // 12: "Density"
    // 13: "AbsoluteMeltingPoint"
    // 14: "MeltingPoint"
    // 15: "AbsoluteBoilingPoint"
    // 16: "BoilingPoint"
    // 17: "SpecificHeat"
    // 18: "FusionHeat"
    // 19: "VaporizationHeat"
    // 20: "ElectroNegativity"
    // 21: "CrustAbundance"
    // 22: "MohsHardness"
    // 23: "VickersHardness"
    // 24: "BrinellHardness"
    // 25: "AtomicRadius"
    // 26: "VanDerWaalsRadius"
    // 27: "CovalentRadius"
    // 28: "IonizationEnergies"
    // 29: "ElectronAffinity"
    // 30: "ThermalConductivity"
    // 31: "YoungModulus"
    // 32: "PoissonRatio"
    // 33: "BulkModulus"
    // 34: "ShearModulus"
    // 35: "ElectronConfiguration"
    // 36: "ElectronConfigurationString"
    // 37: "ElectronShellConfiguration"
    IAST[] list = ElementData1.ELEMENTS;
    for (int i = 0; i < list.length; i++) {
      final IAST iList = list[i];
      MAP_NUMBER_NAME.put(iList.arg1(), iList.arg3());
      IASTAppendable subList = F.mapRange(1, iList.size(), j -> iList.get(j));
      MAP_NAME_DATA.put(iList.arg1(), subList);
      MAP_NAME_DATA.put(iList.arg2(), subList);
      MAP_NAME_DATA.put(iList.arg3(), subList);
    }
    list = ElementData2.ELEMENTS;
    for (int i = 0; i < list.length; i++) {
      final IAST iList = list[i];
      MAP_NUMBER_NAME.put(iList.arg1(), iList.arg3());
      IASTAppendable subList = F.mapRange(1, iList.size(), j -> iList.get(j));
      MAP_NAME_DATA.put(iList.arg1(), subList);
      MAP_NAME_DATA.put(iList.arg2(), subList);
      MAP_NAME_DATA.put(iList.arg3(), subList);
    }
  }
}
