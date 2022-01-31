package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

// Integrate((x^2+2*x+3)^(-1),x) gives wrong result. See
// https://github.com/axkr/symja_android_library/issues/72
public class RubiIssue72 extends AbstractRubiTestCase {

  public RubiIssue72(String name) {
    super(name, false);
  }

  public void test0001() {
    check("Rubi`AbsurdNumberFactors[1]", "1");
  }

  public void test0002() {
    check("Rubi`AbsurdNumberFactors[2]", "2");
  }

  public void test0003() {
    check("Rubi`AbsurdNumberFactors[x]", "1");
  }

  public void test0004() {
    check("Rubi`AbsurdNumberFactors[2*x]", "2");
  }

  public void test0005() {
    check("Rubi`AbsurdNumberGCD[1]", "1");
  }

  public void test0006() {
    check("Rubi`AbsurdNumberGCD[2]", "2");
  }

  public void test0007() {
    check("Rubi`AbsurdNumberGCD[1, 1]", "1");
  }

  public void test0008() {
    check("Rubi`AbsurdNumberGCD[2, 2]", "2");
  }

  public void test0009() {
    check(
        "Rubi`AbsurdNumberGCDList[Rubi`FactorAbsurdNumber[First[{1, 1}]], Rubi`FactorAbsurdNumber[Rubi`AbsurdNumberGCD @@ Rest[{1, 1}]]]",
        "1");
  }

  public void test0010() {
    check(
        "Rubi`AbsurdNumberGCDList[Rubi`FactorAbsurdNumber[First[{2, 2}]], Rubi`FactorAbsurdNumber[Rubi`AbsurdNumberGCD @@ Rest[{2, 2}]]]",
        "2");
  }

  public void test0011() {
    check("Rubi`AbsurdNumberGCDList[{}, {}]", "1");
  }

  public void test0012() {
    check("Rubi`AbsurdNumberGCDList[{{1, 1}}, {{1, 1}}]", "1");
  }

  public void test0013() {
    check("Rubi`AbsurdNumberGCDList[{{2, 1}}, {{2, 1}}]", "2");
  }

  public void test0014() {
    check("Rubi`AbsurdNumberQ[1]", "True");
  }

  public void test0015() {
    check("Rubi`AbsurdNumberQ[2]", "True");
  }

  public void test0016() {
    check("Rubi`AbsurdNumberQ[x]", "False");
  }

  public void test0017() {
    check("Rubi`AbsurdNumberQ[2*x]", "False");
  }

  public void test0018() {
    check("Rubi`BinomialParts[ArcTan[x/(2*Sqrt[2])], x]", "False");
  }

  public void test0019() {
    check("Rubi`BinomialParts[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2]), x]", "False");
  }

  public void test0020() {
    check("Rubi`BinomialParts[ArcTan[x/(2*Sqrt[2])]/Sqrt[2], x]", "False");
  }

  public void test0021() {
    check("Rubi`CalculusQ[x/(2*Sqrt[2])]", "False");
  }

  public void test0022() {
    check("Rubi`CalculusQ[ArcTan[x/(2*Sqrt[2])]]", "False");
  }

  public void test0023() {
    check("Rubi`CalculusQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
  }

  public void test0024() {
    check("Rubi`Coeff[(2 + 2*x)/(2*Sqrt[2]), x, 0]", "1/Sqrt[2]");
  }

  public void test0025() {
    check("Rubi`Coeff[(2 + 2*x)/(2*Sqrt[2]), x, 1]", "1/Sqrt[2]");
  }

  public void test0026() {
    check("Rubi`CommonFactors[{1, x}]", "{1, 1, x}");
  }

  public void test0027() {
    check("Rubi`CommonFactors[{2, 2*x}]", "{2, 1, x}");
  }

  public void test0028() {
    check("Rubi`ComplexNumberQ[1]", "False");
  }

  public void test0029() {
    check("Rubi`ComplexNumberQ[x]", "False");
  }

  public void test0030() {
    check("Rubi`ContentFactor[1 + x]", "1 + x");
  }

  public void test0031() {
    check("Rubi`ContentFactor[2 + 2*x]", "2*(1 + x)");
  }

  public void test0032() {
    check("Rubi`ContentFactorAux[1 + x]", "1 + x");
  }

  public void test0033() {
    check("Rubi`ContentFactorAux[2 + 2*x]", "2*(1 + x)");
  }

  public void test0034() {
    check("Rubi`EqQ[0, 0]", "True");
  }

  public void test0035() {
    check("Rubi`EqQ[1/4, 1]", "False");
  }

  public void test0036() {
    check("Rubi`EqQ[1, 1]", "True");
  }

  public void test0037() {
    check("Rubi`EqQ[1/Sqrt[2], 1/2]", "False");
  }

  public void test0038() {
    check("Rubi`EqQ[1 + x, 0]", "False");
  }

  public void test0039() {
    check("Rubi`EqQ[2 + x, 0]", "False");
  }

  public void test0040() {
    check("Rubi`EqQ[Sqrt[2] + x, 0]", "False");
  }

  public void test0041() {
    check("Rubi`EqQ[2 + x^2, 0]", "False");
  }

  public void test0042() {
    check("Rubi`EveryQ[rubi`LogQ[#1] && IntegerQ[First[#1]] && First[#1] > 0 & , {1, x}]", "False");
  }

  public void test0043() {
    check("Rubi`EveryQ[#1 === 1 & , {1, 1}]", "True");
  }

  public void test0044() {
    check("Rubi`ExpandToSum[(2 + 2*x)/(2*Sqrt[2]), x]", "1/Sqrt[2] + x/Sqrt[2]");
  }

  public void test0045() {
    check("ExpandAll[(1+x)/Sqrt[2] ]", "1/Sqrt(2)+x/Sqrt(2)");
    check("Exponent[(1+x)/Sqrt[2],x]", "1");
    check("Exponent[(1+x)/2,x, List]", "{0, 1}");
    check("Exponent[(1+x)/Sqrt[2],x, List]", "{0, 1}");
    check("Rubi`Expon[(2 + 2*x)/(2*Sqrt[2]), x, List]", "{0, 1}");
  }

  public void test0046() {
    check("Rubi`FactorAbsurdNumber[1]", "{{1, 1}}");
  }

  public void test0047() {
    check("Rubi`FactorAbsurdNumber[2]", "{{2, 1}}");
  }

  public void test0048() {
    check("Rubi`FactorNumericGcd[2]", "2");
  }

  public void test0049() {
    check("Rubi`FactorNumericGcd[1/Sqrt[2]]", "1/Sqrt[2]");
  }

  public void test0050() {
    check("Rubi`FactorNumericGcd[Sqrt[2]]", "Sqrt[2]");
  }

  public void test0051() {
    check("Rubi`FactorNumericGcd[x]", "x");
  }

  public void test0052() {
    check("Rubi`FactorNumericGcd[x^2]", "x^2");
  }

  public void test0053() {
    check("Rubi`FactorNumericGcd[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
  }

  public void test0054() {
    check("Rubi`FactorOrder[1, 1]", "0");
  }

  public void test0055() {
    check("Rubi`FactorOrder[x, 1]", "1");
  }

  public void test0056() {
    check("Rubi`FalseQ[False]", "True");
  }

  public void test0057() {
    check("Rubi`FixSimplify[1/Sqrt[2]]", "1/Sqrt[2]");
  }

  public void test0058() {
    check("Rubi`FixSimplify[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
  }

  public void test0059() {
    check("Rubi`FractionalPowerOfSquareQ[-1/2]", "False");
  }

  public void test0060() {
    check("Rubi`FractionalPowerOfSquareQ[1/2]", "False");
  }

  public void test0061() {
    check("Rubi`FractionalPowerOfSquareQ[2]", "False");
  }

  public void test0062() {
    check("Rubi`FractionalPowerOfSquareQ[1/Sqrt[2]]", "False");
  }

  public void test0063() {
    check("Rubi`FractionalPowerOfSquareQ[Sqrt[2]]", "False");
  }

  public void test0064() {
    check("Rubi`FractionalPowerOfSquareQ[x]", "False");
  }

  public void test0065() {
    check("Rubi`FractionalPowerOfSquareQ[x^2]", "False");
  }

  public void test0066() {
    check("Rubi`FractionalPowerOfSquareQ[Sqrt[2]*x^2]", "False");
  }

  public void test0067() {
    check("Rubi`FractionQ[-1/2]", "True");
  }

  public void test0068() {
    check("Rubi`FractionQ[1/2]", "True");
  }

  public void test0069() {
    check("Rubi`FractionQ[2]", "False");
  }

  public void test0070() {
    check("Rubi`FreeFactors[x/Sqrt[2], x]", "1/Sqrt[2]");
  }

  public void test0071() {
    check("Rubi`HeldFormQ[x/(2*Sqrt[2])]", "False");
  }

  public void test0072() {
    check("Rubi`HeldFormQ[ArcTan[x/(2*Sqrt[2])]]", "False");
  }

  public void test0073() {
    check("Rubi`HeldFormQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
  }

  public void test0074() {
    check("Rubi`LeadBase[1]", "1");
  }

  public void test0075() {
    check("Rubi`LeadBase[x]", "x");
  }

  public void test0076() {
    check("Rubi`LeadDegree[1]", "1");
  }

  public void test0077() {
    check("Rubi`LeadDegree[x]", "1");
  }

  public void test0078() {
    check("Rubi`LeadFactor[1]", "1");
  }

  public void test0079() {
    check("Rubi`LeadFactor[x]", "x");
  }

  public void test0080() {
    check("Rubi`LinearQ[x/(2*Sqrt[2]), x]", "True");
  }

  public void test0081() {
    check("Rubi`LinearQ[2 + 2*x, x]", "True");
  }

  public void test0082() {
    check("Rubi`LogQ[1]", "False");
  }

  public void test0083() {
    check("Rubi`MergeableFactorQ[2, -1/2, x]", "False");
  }

  public void test0084() {
    check("Rubi`MergeFactors[1/Sqrt[2], x]", "x/Sqrt[2]");
  }

  public void test0085() {
    check("Rubi`MostMainFactorPosition[{1, x}]", "2");
  }

  public void test0086() {
    check("Rubi`NeQ[2, 0]", "True");
  }

  public void test0087() {
    check("Rubi`NeQ[1/(2*Sqrt[2]), 0]", "True");
  }

  public void test0088() {
    check("Rubi`NonabsurdNumberFactors[1]", "1");
  }

  public void test0089() {
    check("Rubi`NonabsurdNumberFactors[2]", "1");
  }

  public void test0090() {
    check("Rubi`NonabsurdNumberFactors[x]", "x");
  }

  public void test0091() {
    check("Rubi`NonabsurdNumberFactors[2*x]", "x");
  }

  public void test0092() {
    check("Rubi`NonfreeFactors[x/Sqrt[2], x]", "x");
  }

  public void test0093() {
    check("Rubi`NonnumericFactors[1/2]", "1");
  }

  public void test0094() {
    check("Rubi`NonnumericFactors[2]", "1");
  }

  public void test0095() {
    check("1/Sqrt[2]", "1/Sqrt(2)");
    // TODO answer has equivalent form:
    // check("Rubi`NonnumericFactors[1/Sqrt[2]]", //
    // "Sqrt[2]");
    check("Rubi`NonnumericFactors[1/Sqrt[2]]", //
        "2/Sqrt[2]");
  }

  public void test0096() {
    check("Rubi`NonnumericFactors[x]", "x");
  }

  public void test0097() {
    check("Rubi`NonnumericFactors[1 + x]", "1 + x");
  }

  public void test0098() {
    check("Rubi`NonnumericFactors[2*(1 + x)]", "1 + x");
  }

  public void test0099() {
    check("Rubi`NonnumericFactors[2 + 2*x]", "1 + x");
  }

  public void test0100() {
    // TODO answer has equivalent form:
    // check("Rubi`NonnumericFactors[(2 + 2*x)/(2*Sqrt[2])]", //
    // "Sqrt[2]*(1 + x)");
    check("Rubi`NonnumericFactors[(2 + 2*x)/(2*Sqrt[2])]", //
        "(2*(1+x))/Sqrt[2]");
  }

  public void test0101() {
    check("Rubi`NormalizeSumFactors[-1/2]", "-1/2");
  }

  public void test0102() {
    check("Rubi`NormalizeSumFactors[2]", "2");
  }

  public void test0103() {
    check("Rubi`NormalizeSumFactors[1/Sqrt[2]]", "1/Sqrt[2]");
  }

  public void test0104() {
    check("Rubi`NormalizeSumFactors[x]", "x");
  }

  public void test0105() {
    check("Rubi`NormalizeSumFactors[x/Sqrt[2]]", "x/Sqrt[2]");
  }

  public void test0106() {
    check("Rubi`NormalizeSumFactors[1/Sqrt[2] + x/Sqrt[2]]", "1/Sqrt[2] + x/Sqrt[2]");
  }

  public void test0107() {
    check("Rubi`NumericFactor[1/2]", "1/2");
  }

  public void test0108() {
    check("Rubi`NumericFactor[2]", "2");
  }

  public void test0109() {
    check("Rubi`NumericFactor[1/Sqrt[2]]", "1/2");
  }

  public void test0110() {
    check("Rubi`NumericFactor[x]", "1");
  }

  public void test0111() {
    check("Rubi`NumericFactor[1 + x]", "1");
  }

  public void test0112() {
    check("Rubi`NumericFactor[2*(1 + x)]", "2");
  }

  public void test0113() {
    check("Rubi`NumericFactor[2 + 2*x]", "2");
  }

  public void test0114() {
    check("Rubi`NumericFactor[(2 + 2*x)/(2*Sqrt[2])]", "1/2");
  }

  public void test0115() {
    check("Rubi`PolyQ[(2 + 2*x)/(2*Sqrt[2]), x]", "True");
  }

  public void test0116() {
    check("Rubi`PolyQ[-ArcTan[(1 + x)/Sqrt[2]]/(2*Sqrt[2]), x]", "False");
  }

  public void test0117() {
    check("Rubi`PolyQ[x/(2*Sqrt[2]), x, 1]", "True");
  }

  public void test0118() {
    check("Rubi`PolyQ[2 + 2*x, x, 1]", "True");
  }

  public void test0119() {
    check("Rubi`PowerQ[1]", "False");
  }

  public void test0120() {
    check("Rubi`PowerQ[2]", "False");
  }

  public void test0121() {
    check("Rubi`PowerQ[1/Sqrt[2]]", "True");
  }

  public void test0122() {
    check("Rubi`PowerQ[Sqrt[2]]", "True");
  }

  public void test0123() {
    check("Rubi`PowerQ[x]", "False");
  }

  public void test0124() {
    check("Rubi`PowerQ[x/(2*Sqrt[2])]", "False");
  }

  public void test0125() {
    check("Rubi`PowerQ[x^2]", "True");
  }

  public void test0126() {
    check("Rubi`PowerQ[Sqrt[2]*x^2]", "False");
  }

  public void test0127() {
    check("Rubi`PowerQ[1 + x]", "False");
  }

  public void test0128() {
    check("Rubi`PowerQ[2*(1 + x)]", "False");
  }

  public void test0129() {
    check("Rubi`PowerQ[2 + 2*x]", "False");
  }

  public void test0130() {
    check("Rubi`PowerQ[(2 + 2*x)/(2*Sqrt[2])]", "False");
  }

  public void test0131() {
    check("Rubi`PowerQ[ArcTan[x/(2*Sqrt[2])]]", "False");
  }

  public void test0132() {
    check("Rubi`PowerQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "False");
  }

  public void test0133() {
    check("Rubi`PowerQ[ArcTan[x/(2*Sqrt[2])]/Sqrt[2]]", "False");
  }

  public void test0134() {
    check("Rubi`ProductQ[1]", "False");
  }

  public void test0135() {
    check("Rubi`ProductQ[2]", "False");
  }

  public void test0136() {
    check("Rubi`ProductQ[1/Sqrt[2]]", "False");
  }

  public void test0137() {
    check("Rubi`ProductQ[x]", "False");
  }

  public void test0138() {
    check("Rubi`ProductQ[2*x]", "True");
  }

  public void test0139() {
    check("Rubi`ProductQ[x/Sqrt[2]]", "True");
  }

  public void test0140() {
    check("Rubi`ProductQ[x^2]", "False");
  }

  public void test0141() {
    check("Rubi`ProductQ[Sqrt[2]*x^2]", "True");
  }

  public void test0142() {
    check("Rubi`ProductQ[1 + x]", "False");
  }

  public void test0143() {
    check("Rubi`ProductQ[2*(1 + x)]", "True");
  }

  public void test0144() {
    check("Rubi`ProductQ[2 + 2*x]", "False");
  }

  public void test0145() {
    check("Rubi`ProductQ[(2 + 2*x)/(2*Sqrt[2])]", "True");
  }

  public void test0146() {
    check("Rubi`ProductQ[1/Sqrt[2] + x/Sqrt[2]]", "False");
  }

  public void test0147() {
    check("Rubi`ProductQ[ArcTan[x/(2*Sqrt[2])]]", "False");
  }

  public void test0148() {
    check("Rubi`ProductQ[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])]", "True");
  }

  public void test0149() {
    check("Rubi`ProductQ[ArcTan[x/(2*Sqrt[2])]/Sqrt[2]]", "True");
  }

  public void test0150() {
    check("Rubi`RationalQ[-1/2]", "True");
  }

  public void test0151() {
    check("Rubi`RationalQ[1/2]", "True");
  }

  public void test0152() {
    check("Rubi`RationalQ[1]", "True");
  }

  public void test0153() {
    check("Rubi`RationalQ[2]", "True");
  }

  public void test0154() {
    check("Rubi`RationalQ[1/Sqrt[2]]", "False");
  }

  public void test0155() {
    check("Rubi`RationalQ[Sqrt[2]]", "False");
  }

  public void test0156() {
    check("Rubi`RationalQ[x]", "False");
  }

  public void test0157() {
    check("Rubi`RationalQ[x/Sqrt[2]]", "False");
  }

  public void test0158() {
    check("Rubi`RationalQ[x^2]", "False");
  }

  public void test0159() {
    check("Rubi`RemainingFactors[x]", "1");
  }

  public void test0160() {
    check("Rubi`SignOfFactor[1/Sqrt[2]]", "{1, 1/Sqrt[2]}");
  }

  public void test0161() {
    check("Rubi`SignOfFactor[x]", "{1, x}");
  }

  public void test0162() {
    check("Rubi`SignOfFactor[x/Sqrt[2]]", "{1, x/Sqrt[2]}");
  }

  public void test0163() {
    check("Rubi`Simp[1/Sqrt[2] + x/Sqrt[2], x]", //
        "1/Sqrt[2] + x/Sqrt[2]");
  }

  public void test0164() {
    check(
        "Rubi`Simp[Plus @@ (Rubi`Coeff[(2 + 2*x)/(2*Sqrt[2]), x, #1]*x^#1 & ) /@ Rubi`Expon[(2 + 2*x)/(2*Sqrt[2]), x, List], x]", //
        "1/Sqrt[2] + x/Sqrt[2]");
  }

  public void test0165() {
    check("Rubi`SimpFixFactor[1/Sqrt[2], x]", "1/Sqrt[2]");
  }

  public void test0166() {
    check("Rubi`SimpFixFactor[x, x]", "x");
  }

  public void test0169() {
    check("Rubi`SimpHelp[x, x]", "x");
  }

  public void test0170() {
    check("Rubi`SimpHelp[x/Sqrt[2], x]", "x/Sqrt[2]");
  }

  public void test0171() {
    check("Rubi`SimpHelp[1/Sqrt[2] + x/Sqrt[2], x]", "1/Sqrt[2] + x/Sqrt[2]");
  }

  public void test0172() {
    check("Rubi`SmartSimplify[1/Sqrt[2]]", "1/Sqrt[2]");
  }

  public void test0173() {
    check("Rubi`SmartSimplify[Sqrt[2]*x^2]", "Sqrt[2]*x^2");
  }

  public void test0174() {
    check("Rubi`SqrtNumberSumQ[x]", "False");
  }

  public void test0175() {
    check("Rubi`SqrtNumberSumQ[x^2]", "False");
  }

  public void test0176() {
    check("Rubi`StopFunctionQ[1/Sqrt[2]]", "False");
  }

  public void test0177() {
    check("Rubi`StopFunctionQ[x/Sqrt[2]]", "False");
  }

  public void test0178() {
    check("Rubi`StopFunctionQ[1/Sqrt[2] + x/Sqrt[2]]", "False");
  }

  public void test0179() {
    check("Rubi`SubstAux[-1/2, x, 2 + 2*x, True]", "-1/2");
  }

  public void test0180() {
    check("Rubi`SubstAux[1/2, x, 2 + 2*x, True]", "1/2");
  }

  public void test0181() {
    check("Rubi`SubstAux[1/Sqrt[2], x, 2 + 2*x, True]", "1/Sqrt[2]");
  }

  public void test0182() {
    check("Rubi`SubstAux[x, x, 2 + 2*x, True]", "2 + 2*x");
  }

  public void test0183() {
    check("Rubi`SubstAux[x/(2*Sqrt[2]), x, 2 + 2*x, True]", "(1 + x)/Sqrt[2]");
  }

  public void test0184() {
    check("Rubi`SubstAux[ArcTan[x/(2*Sqrt[2])], x, 2 + 2*x, True]", "ArcTan[(1 + x)/Sqrt[2]]");
  }

  public void test0185() {
    check("Rubi`SubstAux[-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2]), x, 2 + 2*x, True]",
        "-ArcTan[(1 + x)/Sqrt[2]]/(2*Sqrt[2])");
  }

  public void test0186() {
    check("Rubi`SumQ[2]", "False");
  }

  public void test0187() {
    check("Rubi`SumQ[1/Sqrt[2]]", "False");
  }

  public void test0188() {
    check("Rubi`SumQ[x]", "False");
  }

  public void test0189() {
    check("Rubi`SumQ[x/Sqrt[2]]", "False");
  }

  public void test0190() {
    check("Rubi`SumQ[x^2]", "False");
  }

  public void test0191() {
    check("Rubi`SumQ[1 + x]", "True");
  }

  public void test0192() {
    check("Rubi`SumQ[2*(1 + x)]", "False");
  }

  public void test0193() {
    check("Rubi`SumQ[2 + 2*x]", "True");
  }

  public void test0194() {
    check("Rubi`SumQ[1/Sqrt[2] + x/Sqrt[2]]", "True");
  }

  public void test0195() {
    check("Rubi`SumQ[ArcTan[x/(2*Sqrt[2])]]", "False");
  }

  public void test0196() {
    check("Integrate[(x^2+2*x+3)^(-1),x]", "ArcTan[(1+x)/Sqrt[2]]/Sqrt[2]");
  }
}
