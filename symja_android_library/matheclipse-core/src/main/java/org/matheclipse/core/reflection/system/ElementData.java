package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.NIL;

import java.util.HashMap;

import org.matheclipse.core.data.ElementData1;
import org.matheclipse.core.data.ElementData2;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *  
 */
public class ElementData extends AbstractFunctionEvaluator {

	private static java.util.Map<IExpr, IExpr> MAP_NUMBER_NAME = new HashMap<IExpr, IExpr>();

	private static java.util.Map<IExpr, IAST> MAP_NAME_DATA = new HashMap<IExpr, IAST>();

	public ElementData() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2) {
			if (ast.arg1().isInteger()) {
				IExpr temp = MAP_NUMBER_NAME.get(ast.arg1());
				if (temp != null) {
					return temp;
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

	private IExpr dataPoint(final IAST ast, IAST propertyList) {
		String propertyStr = ast.arg2().toString();
		if (propertyStr.equals("AtomicNumber")) {
			return propertyList.get(1);
		}
		if (propertyStr.equals("Abbreviation")) {
			return propertyList.get(2);
		}
		if (propertyStr.equals("StandardName")) {
			return propertyList.get(3);
		}
		if (propertyStr.equals("Name")) {
			return propertyList.get(4);
		}
		if (propertyStr.equals("AtomicWeight")) {
			return propertyList.get(9);
		}
		// if (propertyStr.equals("MeltingPoint")) {
		// return propertyList.get(14);
		// }
		if (propertyStr.equals("BoilingPoint")) {
			return propertyList.get(16);
		}

//		if (propertyStr.equals("AtomicRadius")) {
//			return propertyList.get(25);
//		}
		if (propertyStr.equals("VanDerWaalsRadius")) {
			return propertyList.get(26);
		}
//		if (propertyStr.equals("CovalentRadius")) {
//			return propertyList.get(27);
//		}
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
			return propertyList.get(33);
		}
		if (propertyStr.equals("ShearModulus")) {
			return propertyList.get(34);
		}
		if (propertyStr.equals("ElectronConfiguration")) {
			return propertyList.get(35);
		}
		if (propertyStr.equals("ElectronConfigurationString")) {
			return propertyList.get(36);
		}
		if (propertyStr.equals("ElectronShellConfiguration")) {
			return propertyList.get(37);
		}
		return NIL;
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
			MAP_NUMBER_NAME.put(list[i].arg1(), list[i].arg3());
			IAST subList = F.List();
			for (int j = 1; j < list[i].size(); j++) {
				subList.append(list[i].get(j));
			}
			MAP_NAME_DATA.put(list[i].arg1(), subList);
			MAP_NAME_DATA.put(list[i].arg2(), subList);
			MAP_NAME_DATA.put(list[i].arg3(), subList);
		}
		list = ElementData2.ELEMENTS;
		for (int i = 0; i < list.length; i++) {
			MAP_NUMBER_NAME.put(list[i].arg1(), list[i].arg3());
			IAST subList = F.List();
			for (int j = 1; j < list[i].size(); j++) {
				subList.append(list[i].get(j));
			}
			MAP_NAME_DATA.put(list[i].arg1(), subList);
			MAP_NAME_DATA.put(list[i].arg2(), subList);
			MAP_NAME_DATA.put(list[i].arg3(), subList);
		}
	}

}
