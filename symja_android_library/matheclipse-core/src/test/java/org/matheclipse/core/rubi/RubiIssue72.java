package org.matheclipse.core.rubi;

// Integrate((x^2+2*x+3)^(-1),x) gives wrong result. See https://github.com/axkr/symja_android_library/issues/72
public class RubiIssue72 extends AbstractRubiTestCase {

	public RubiIssue72(String name) {
		super(name, false);
	}

	public void test0001() {
		check("Integrate::AbsurdNumberFactors[1]", "1");
	}

	public void test0002() {
		check("Integrate::AbsurdNumberFactors[2]", "2");
	}

	public void test0003() {
		check("Integrate::AbsurdNumberFactors[x]", "1");
	}

	public void test0004() {
		check("Integrate::AbsurdNumberFactors[2*x]", "2");
	}

	public void test0005() {
		check("Integrate::AbsurdNumberGCD[1]", "1");
	}

	public void test0006() {
		check("Integrate::AbsurdNumberGCD[2]", "2");
	}

	public void test0007() {
		check("Integrate::AbsurdNumberGCD[1, 1]", "1");
	}

	public void test0008() {
		check("Integrate::AbsurdNumberGCD[2, 2]", "2");
	}

	public void test0009() {
		check("Integrate::AbsurdNumberGCDList[Integrate::FactorAbsurdNumber[First[{1, 1}]], Integrate::FactorAbsurdNumber[Integrate::AbsurdNumberGCD @@ Rest[{1, 1}]]]",
				"1");
	}

	public void test0010() {
		check("Integrate::AbsurdNumberGCDList[Integrate::FactorAbsurdNumber[First[{2, 2}]], Integrate::FactorAbsurdNumber[Integrate::AbsurdNumberGCD @@ Rest[{2, 2}]]]",
				"2");
	}

	public void test0011() {
		check("Integrate::AbsurdNumberGCDList[{}, {}]", "1");
	}

	public void test0012() {
		check("Integrate::AbsurdNumberGCDList[{{1, 1}}, {{1, 1}}]", "1");
	}

	public void test0013() {
		check("Integrate::AbsurdNumberGCDList[{{2, 1}}, {{2, 1}}]", "2");
	}

	public void test0014() {
		check("Integrate::AbsurdNumberQ[1]", "True");
	}

	public void test0015() {
		check("Integrate::AbsurdNumberQ[2]", "True");
	}

	public void test0016() {
		check("Integrate::AbsurdNumberQ[x]", "False");
	}

	public void test0017() {
		check("Integrate::AbsurdNumberQ[2*x]", "False");
	}

	public void test0018() {
		check("Integrate::BinomialParts[ArcTan[x/(2*Sqrt[2])], x]", "False");
	}

	public void test0019() {
		check("Integrate::BinomialParts[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2]), x]", "False");
	}

	public void test0020() {
		check("Integrate::BinomialParts[ArcTan[x/(2*Sqrt[2])]/Sqrt[2], x]", "False");
	}

	public void test0021() {
		check("Integrate::CalculusQ[x/(2*Sqrt[2])]", "False");
	}

	public void test0022() {
		check("Integrate::CalculusQ[ArcTan[x/(2*Sqrt[2])]]", "False");
	}

	public void test0023() {
		check("Integrate::CalculusQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
	}

	public void test0024() {
		check("Integrate::Coeff[(2 + 2*x)/(2*Sqrt[2]), x, 0]", "1/Sqrt[2]");
	}

	public void test0025() {
		check("Integrate::Coeff[(2 + 2*x)/(2*Sqrt[2]), x, 1]", "1/Sqrt[2]");
	}

	public void test0026() {
		check("Integrate::CommonFactors[{1, x}]", "{1, 1, x}");
	}

	public void test0027() {
		check("Integrate::CommonFactors[{2, 2*x}]", "{2, 1, x}");
	}

	public void test0028() {
		check("Integrate::ComplexNumberQ[1]", "False");
	}

	public void test0029() {
		check("Integrate::ComplexNumberQ[x]", "False");
	}

	public void test0030() {
		check("Integrate::ContentFactor[1 + x]", "1 + x");
	}

	public void test0031() {
		check("Integrate::ContentFactor[2 + 2*x]", "2*(1 + x)");
	}

	public void test0032() {
		check("Integrate::ContentFactorAux[1 + x]", "1 + x");
	}

	public void test0033() {
		check("Integrate::ContentFactorAux[2 + 2*x]", "2*(1 + x)");
	}

	public void test0034() {
		check("Integrate::EqQ[0, 0]", "True");
	}

	public void test0035() {
		check("Integrate::EqQ[1/4, 1]", "False");
	}

	public void test0036() {
		check("Integrate::EqQ[1, 1]", "True");
	}

	public void test0037() {
		check("Integrate::EqQ[1/Sqrt[2], 1/2]", "False");
	}

	public void test0038() {
		check("Integrate::EqQ[1 + x, 0]", "False");
	}

	public void test0039() {
		check("Integrate::EqQ[2 + x, 0]", "False");
	}

	public void test0040() {
		check("Integrate::EqQ[Sqrt[2] + x, 0]", "False");
	}

	public void test0041() {
		check("Integrate::EqQ[2 + x^2, 0]", "False");
	}

	public void test0042() {
		check("Integrate::EveryQ[Integrate::LogQ[#1] && IntegerQ[First[#1]] && First[#1] > 0 & , {1, x}]", "False");
	}

	public void test0043() {
		check("Integrate::EveryQ[#1 === 1 & , {1, 1}]", "True");
	}

	public void test0044() {
		check("Integrate::ExpandToSum[(2 + 2*x)/(2*Sqrt[2]), x]", "1/Sqrt[2] + x/Sqrt[2]");
	}

	public void test0045() {
		check("Together[(2 + 2*x)/(2*Sqrt[2])]", "(1+x)/Sqrt[2]");
		check("ExpandAll[(1+x)/Sqrt[2] ]", "1/Sqrt(2)+x/Sqrt(2)");
		check("Exponent[(1+x)/Sqrt[2],x]", "1");
		check("Exponent[(1+x)/2,x, List]", "{0, 1}");
		check("Exponent[(1+x)/Sqrt[2],x, List]", "{0, 1}");
		check("Integrate::Expon[(2 + 2*x)/(2*Sqrt[2]), x, List]", "{0, 1}");
	}

	public void test0046() {
		check("Integrate::FactorAbsurdNumber[1]", "{{1, 1}}");
	}

	public void test0047() {
		check("Integrate::FactorAbsurdNumber[2]", "{{2, 1}}");
	}

	public void test0048() {
		check("Integrate::FactorNumericGcd[2]", "2");
	}

	public void test0049() {
		check("Integrate::FactorNumericGcd[1/Sqrt[2]]", "1/Sqrt[2]");
	}

	public void test0050() {
		check("Integrate::FactorNumericGcd[Sqrt[2]]", "Sqrt[2]");
	}

	public void test0051() {
		check("Integrate::FactorNumericGcd[x]", "x");
	}

	public void test0052() {
		check("Integrate::FactorNumericGcd[x^2]", "x^2");
	}

	public void test0053() {
		check("Integrate::FactorNumericGcd[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
	}

	public void test0054() {
		check("Integrate::FactorOrder[1, 1]", "0");
	}

	public void test0055() {
		check("Integrate::FactorOrder[x, 1]", "1");
	}

	public void test0056() {
		check("Integrate::FalseQ[False]", "True");
	}

	public void test0057() {
		check("Integrate::FixSimplify[1/Sqrt[2]]", "1/Sqrt[2]");
	}

	public void test0058() {
		check("Integrate::FixSimplify[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
	}

	public void test0059() {
		check("Integrate::FractionalPowerOfSquareQ[-1/2]", "False");
	}

	public void test0060() {
		check("Integrate::FractionalPowerOfSquareQ[1/2]", "False");
	}

	public void test0061() {
		check("Integrate::FractionalPowerOfSquareQ[2]", "False");
	}

	public void test0062() {
		check("Integrate::FractionalPowerOfSquareQ[1/Sqrt[2]]", "False");
	}

	public void test0063() {
		check("Integrate::FractionalPowerOfSquareQ[Sqrt[2]]", "False");
	}

	public void test0064() {
		check("Integrate::FractionalPowerOfSquareQ[x]", "False");
	}

	public void test0065() {
		check("Integrate::FractionalPowerOfSquareQ[x^2]", "False");
	}

	public void test0066() {
		check("Integrate::FractionalPowerOfSquareQ[Sqrt[2]*x^2]", "False");
	}

	public void test0067() {
		check("Integrate::FractionQ[-1/2]", "True");
	}

	public void test0068() {
		check("Integrate::FractionQ[1/2]", "True");
	}

	public void test0069() {
		check("Integrate::FractionQ[2]", "False");
	}

	public void test0070() {
		check("Integrate::FreeFactors[x/Sqrt[2], x]", "1/Sqrt[2]");
	}

	public void test0071() {
		check("Integrate::HeldFormQ[x/(2*Sqrt[2])]", "False");
	}

	public void test0072() {
		check("Integrate::HeldFormQ[ArcTan[x/(2*Sqrt[2])]]", "False");
	}

	public void test0073() {
		check("Integrate::HeldFormQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
	}

	public void test0074() {
		check("Integrate::LeadBase[1]", "1");
	}

	public void test0075() {
		check("Integrate::LeadBase[x]", "x");
	}

	public void test0076() {
		check("Integrate::LeadDegree[1]", "1");
	}

	public void test0077() {
		check("Integrate::LeadDegree[x]", "1");
	}

	public void test0078() {
		check("Integrate::LeadFactor[1]", "1");
	}

	public void test0079() {
		check("Integrate::LeadFactor[x]", "x");
	}

	public void test0080() {
		check("Integrate::LinearQ[x/(2*Sqrt[2]), x]", "True");
	}

	public void test0081() {
		check("Integrate::LinearQ[2 + 2*x, x]", "True");
	}

	public void test0082() {
		check("Integrate::LogQ[1]", "False");
	}

	public void test0083() {
		check("Integrate::MergeableFactorQ[2, -1/2, x]", "False");
	}

	public void test0084() {
		check("Integrate::MergeFactors[1/Sqrt[2], x]", "x/Sqrt[2]");
	}

	public void test0085() {
		check("Integrate::MostMainFactorPosition[{1, x}]", "2");
	}

	public void test0086() {
		check("Integrate::NeQ[2, 0]", "True");
	}

	public void test0087() {
		check("Integrate::NeQ[1/(2*Sqrt[2]), 0]", "True");
	}

	public void test0088() {
		check("Integrate::NonabsurdNumberFactors[1]", "1");
	}

	public void test0089() {
		check("Integrate::NonabsurdNumberFactors[2]", "1");
	}

	public void test0090() {
		check("Integrate::NonabsurdNumberFactors[x]", "x");
	}

	public void test0091() {
		check("Integrate::NonabsurdNumberFactors[2*x]", "x");
	}

	public void test0092() {
		check("Integrate::NonfreeFactors[x/Sqrt[2], x]", "x");
	}

	public void test0093() {
		check("Integrate::NonnumericFactors[1/2]", "1");
	}

	public void test0094() {
		check("Integrate::NonnumericFactors[2]", "1");
	}

	public void test0095() {
		check("1/Sqrt[2]", "1/Sqrt(2)");
		// TODO answer has equivalent form:
		// check("Integrate::NonnumericFactors[1/Sqrt[2]]", //
		// "Sqrt[2]");
		check("Integrate::NonnumericFactors[1/Sqrt[2]]", //
				"2/Sqrt[2]");
	}

	public void test0096() {
		check("Integrate::NonnumericFactors[x]", "x");
	}

	public void test0097() {
		check("Integrate::NonnumericFactors[1 + x]", "1 + x");
	}

	public void test0098() {
		check("Integrate::NonnumericFactors[2*(1 + x)]", "1 + x");
	}

	public void test0099() {
		check("Integrate::NonnumericFactors[2 + 2*x]", "1 + x");
	}

	public void test0100() {
		// TODO answer has equivalent form:
		// check("Integrate::NonnumericFactors[(2 + 2*x)/(2*Sqrt[2])]", //
		// "Sqrt[2]*(1 + x)");
		check("Integrate::NonnumericFactors[(2 + 2*x)/(2*Sqrt[2])]", //
				"(2*(1+x))/Sqrt[2]");
	}

	public void test0101() {
		check("Integrate::NormalizeSumFactors[-1/2]", "-1/2");
	}

	public void test0102() {
		check("Integrate::NormalizeSumFactors[2]", "2");
	}

	public void test0103() {
		check("Integrate::NormalizeSumFactors[1/Sqrt[2]]", "1/Sqrt[2]");
	}

	public void test0104() {
		check("Integrate::NormalizeSumFactors[x]", "x");
	}

	public void test0105() {
		check("Integrate::NormalizeSumFactors[x/Sqrt[2]]", "x/Sqrt[2]");
	}

	public void test0106() {
		check("Integrate::NormalizeSumFactors[1/Sqrt[2] + x/Sqrt[2]]", "1/Sqrt[2] + x/Sqrt[2]");
	}

	public void test0107() {
		check("Integrate::NumericFactor[1/2]", "1/2");
	}

	public void test0108() {
		check("Integrate::NumericFactor[2]", "2");
	}

	public void test0109() {
		check("Integrate::NumericFactor[1/Sqrt[2]]", "1/2");
	}

	public void test0110() {
		check("Integrate::NumericFactor[x]", "1");
	}

	public void test0111() {
		check("Integrate::NumericFactor[1 + x]", "1");
	}

	public void test0112() {
		check("Integrate::NumericFactor[2*(1 + x)]", "2");
	}

	public void test0113() {
		check("Integrate::NumericFactor[2 + 2*x]", "2");
	}

	public void test0114() {
		check("Integrate::NumericFactor[(2 + 2*x)/(2*Sqrt[2])]", "1/2");
	}

	public void test0115() {
		check("Integrate::PolyQ[(2 + 2*x)/(2*Sqrt[2]), x]", "True");
	}

	public void test0116() {
		check("Integrate::PolyQ[-ArcTan[(1 + x)/Sqrt[2]]/(2*Sqrt[2]), x]", "False");
	}

	public void test0117() {
		check("Integrate::PolyQ[x/(2*Sqrt[2]), x, 1]", "True");
	}

	public void test0118() {
		check("Integrate::PolyQ[2 + 2*x, x, 1]", "True");
	}

	public void test0119() {
		check("Integrate::PowerQ[1]", "False");
	}

	public void test0120() {
		check("Integrate::PowerQ[2]", "False");
	}

	public void test0121() {
		check("Integrate::PowerQ[1/Sqrt[2]]", "True");
	}

	public void test0122() {
		check("Integrate::PowerQ[Sqrt[2]]", "True");
	}

	public void test0123() {
		check("Integrate::PowerQ[x]", "False");
	}

	public void test0124() {
		check("Integrate::PowerQ[x/(2*Sqrt[2])]", "False");
	}

	public void test0125() {
		check("Integrate::PowerQ[x^2]", "True");
	}

	public void test0126() {
		check("Integrate::PowerQ[Sqrt[2]*x^2]", "False");
	}

	public void test0127() {
		check("Integrate::PowerQ[1 + x]", "False");
	}

	public void test0128() {
		check("Integrate::PowerQ[2*(1 + x)]", "False");
	}

	public void test0129() {
		check("Integrate::PowerQ[2 + 2*x]", "False");
	}

	public void test0130() {
		check("Integrate::PowerQ[(2 + 2*x)/(2*Sqrt[2])]", "False");
	}

	public void test0131() {
		check("Integrate::PowerQ[ArcTan[x/(2*Sqrt[2])]]", "False");
	}

	public void test0132() {
		check("Integrate::PowerQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
	}

	public void test0133() {
		check("Integrate::PowerQ[ArcTan[x/(2*Sqrt[2])]/Sqrt[2]]", "False");
	}

	public void test0134() {
		check("Integrate::ProductQ[1]", "False");
	}

	public void test0135() {
		check("Integrate::ProductQ[2]", "False");
	}

	public void test0136() {
		check("Integrate::ProductQ[1/Sqrt[2]]", "False");
	}

	public void test0137() {
		check("Integrate::ProductQ[x]", "False");
	}

	public void test0138() {
		check("Integrate::ProductQ[2*x]", "True");
	}

	public void test0139() {
		check("Integrate::ProductQ[x/Sqrt[2]]", "True");
	}

	public void test0140() {
		check("Integrate::ProductQ[x^2]", "False");
	}

	public void test0141() {
		check("Integrate::ProductQ[Sqrt[2]*x^2]", "True");
	}

	public void test0142() {
		check("Integrate::ProductQ[1 + x]", "False");
	}

	public void test0143() {
		check("Integrate::ProductQ[2*(1 + x)]", "True");
	}

	public void test0144() {
		check("Integrate::ProductQ[2 + 2*x]", "False");
	}

	public void test0145() {
		check("Integrate::ProductQ[(2 + 2*x)/(2*Sqrt[2])]", "True");
	}

	public void test0146() {
		check("Integrate::ProductQ[1/Sqrt[2] + x/Sqrt[2]]", "False");
	}

	public void test0147() {
		check("Integrate::ProductQ[ArcTan[x/(2*Sqrt[2])]]", "False");
	}

	public void test0148() {
		check("Integrate::ProductQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "True");
	}

	public void test0149() {
		check("Integrate::ProductQ[ArcTan[x/(2*Sqrt[2])]/Sqrt[2]]", "True");
	}

	public void test0150() {
		check("Integrate::RationalQ[-1/2]", "True");
	}

	public void test0151() {
		check("Integrate::RationalQ[1/2]", "True");
	}

	public void test0152() {
		check("Integrate::RationalQ[1]", "True");
	}

	public void test0153() {
		check("Integrate::RationalQ[2]", "True");
	}

	public void test0154() {
		check("Integrate::RationalQ[1/Sqrt[2]]", "False");
	}

	public void test0155() {
		check("Integrate::RationalQ[Sqrt[2]]", "False");
	}

	public void test0156() {
		check("Integrate::RationalQ[x]", "False");
	}

	public void test0157() {
		check("Integrate::RationalQ[x/Sqrt[2]]", "False");
	}

	public void test0158() {
		check("Integrate::RationalQ[x^2]", "False");
	}

	public void test0159() {
		check("Integrate::RemainingFactors[x]", "1");
	}

	public void test0160() {
		check("Integrate::SignOfFactor[1/Sqrt[2]]", "{1, 1/Sqrt[2]}");
	}

	public void test0161() {
		check("Integrate::SignOfFactor[x]", "{1, x}");
	}

	public void test0162() {
		check("Integrate::SignOfFactor[x/Sqrt[2]]", "{1, x/Sqrt[2]}");
	}

	public void test0163() {
		check("Integrate::Simp[1/Sqrt[2] + x/Sqrt[2], x]", //
				"1/Sqrt[2] + x/Sqrt[2]");
	}

	public void test0164() {
		check("Integrate::Simp[Plus @@ (Integrate::Coeff[(2 + 2*x)/(2*Sqrt[2]), x, #1]*x^#1 & ) /@ Integrate::Expon[(2 + 2*x)/(2*Sqrt[2]), x, List], x]", //
				"1/Sqrt[2] + x/Sqrt[2]");
	}

	public void test0165() {
		check("Integrate::SimpFixFactor[1/Sqrt[2], x]", "1/Sqrt[2]");
	}

	public void test0166() {
		check("Integrate::SimpFixFactor[x, x]", "x");
	}

	public void test0169() {
		check("Integrate::SimpHelp[x, x]", "x");
	}

	public void test0170() {
		check("Integrate::SimpHelp[x/Sqrt[2], x]", "x/Sqrt[2]");
	}

	public void test0171() {
		check("Integrate::SimpHelp[1/Sqrt[2] + x/Sqrt[2], x]", "1/Sqrt[2] + x/Sqrt[2]");
	}

	public void test0172() {
		check("Integrate::SmartSimplify[1/Sqrt[2]]", "1/Sqrt[2]");
	}

	public void test0173() {
		check("Integrate::SmartSimplify[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
	}

	public void test0174() {
		check("Integrate::SqrtNumberSumQ[x]", "False");
	}

	public void test0175() {
		check("Integrate::SqrtNumberSumQ[x^2]", "False");
	}

	public void test0176() {
		check("Integrate::StopFunctionQ[1/Sqrt[2]]", "False");
	}

	public void test0177() {
		check("Integrate::StopFunctionQ[x/Sqrt[2]]", "False");
	}

	public void test0178() {
		check("Integrate::StopFunctionQ[1/Sqrt[2] + x/Sqrt[2]]", "False");
	}

	public void test0179() {
		check("Integrate::SubstAux[-1/2, x, 2 + 2*x, True]", "-1/2");
	}

	public void test0180() {
		check("Integrate::SubstAux[1/2, x, 2 + 2*x, True]", "1/2");
	}

	public void test0181() {
		check("Integrate::SubstAux[1/Sqrt[2], x, 2 + 2*x, True]", "1/Sqrt[2]");
	}

	public void test0182() {
		check("Integrate::SubstAux[x, x, 2 + 2*x, True]", "2 + 2*x");
	}

	public void test0183() {
		check("Integrate::SubstAux[x/(2*Sqrt[2]), x, 2 + 2*x, True]", "(1 + x)/Sqrt[2]");
	}

	public void test0184() {
		check("Integrate::SubstAux[ArcTan[x/(2*Sqrt[2])], x, 2 + 2*x, True]", "ArcTan[(1 + x)/Sqrt[2]]");
	}

	public void test0185() {
		check("Integrate::SubstAux[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2]), x, 2 + 2*x, True]",
				"-ArcTan[(1 + x)/Sqrt[2]]/(2*Sqrt[2])");
	}

	public void test0186() {
		check("Integrate::SumQ[2]", "False");
	}

	public void test0187() {
		check("Integrate::SumQ[1/Sqrt[2]]", "False");
	}

	public void test0188() {
		check("Integrate::SumQ[x]", "False");
	}

	public void test0189() {
		check("Integrate::SumQ[x/Sqrt[2]]", "False");
	}

	public void test0190() {
		check("Integrate::SumQ[x^2]", "False");
	}

	public void test0191() {
		check("Integrate::SumQ[1 + x]", "True");
	}

	public void test0192() {
		check("Integrate::SumQ[2*(1 + x)]", "False");
	}

	public void test0193() {
		check("Integrate::SumQ[2 + 2*x]", "True");
	}

	public void test0194() {
		check("Integrate::SumQ[1/Sqrt[2] + x/Sqrt[2]]", "True");
	}

	public void test0195() {
		check("Integrate::SumQ[ArcTan[x/(2*Sqrt[2])]]", "False");
	}
	
	public void test0196() {
		check("Integrate[(x^2+2*x+3)^(-1),x]", "ArcTan[(1+x)/Sqrt[2]]/Sqrt[2]");
	}
}