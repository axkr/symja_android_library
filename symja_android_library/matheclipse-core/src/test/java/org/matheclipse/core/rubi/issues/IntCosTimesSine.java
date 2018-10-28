package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

//Int[Cos[a*x]*Sin[b*x],x,x]
public class IntCosTimesSine extends AbstractRubiTestCase {

public IntCosTimesSine(String name) { super(name, false); }

	public void test0001() {
		check("Rubi`AbsurdNumberFactors[-1]", "-1");
	}

	public void test0002() {
		check("Rubi`AbsurdNumberFactors[a]", "1");
	}

	public void test0003() {
		check("Rubi`AbsurdNumberFactors[-b]", "-1");
	}

	public void test0004() {
		check("Rubi`AbsurdNumberFactors[b]", "1");
	}

	public void test0005() {
		check("Rubi`AbsurdNumberGCD[-1]", "-1");
	}

	public void test0006() {
		check("Rubi`AbsurdNumberGCD[1]", "1");
	}

	public void test0007() {
		check("Rubi`AbsurdNumberGCD[1, -1]", "1");
	}

	public void test0008() {
		check("Rubi`AbsurdNumberGCD[1, 1]", "1");
	}

	public void test0009() {
		check("Rubi`AbsurdNumberGCDList[{}, {}]", "1");
	}

	public void test0010() {
		check("Rubi`AbsurdNumberGCDList[{{1, 1}}, {}]", "1");
	}

	public void test0011() {
		check("Rubi`AbsurdNumberGCDList[{{1, 1}}, {{-1, 1}}]", "1");
	}

	public void test0012() {
		check("Rubi`AbsurdNumberGCDList[{{1, 1}}, {{1, 1}}]", "1");
	}

	public void test0013() {
		check("Rubi`AbsurdNumberGCDList[{{1, 1}}, Rest[{{-1, 1}}]]", "1");
	}

	public void test0014() {
		check("Rubi`AbsurdNumberQ[-1]", "True");
	}

	public void test0015() {
		check("Rubi`AbsurdNumberQ[a]", "False");
	}

	public void test0016() {
		check("Rubi`AbsurdNumberQ[-b]", "False");
	}

	public void test0017() {
		check("Rubi`AbsurdNumberQ[b]", "False");
	}

	public void test0018() {
		check("Rubi`ActivateTrig[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0019() {
		check("Rubi`AlgebraicTrigFunctionQ[Sin[(a - b)*x], x]", "True");
	}

	public void test0020() {
		check("Rubi`AlgebraicTrigFunctionQ[Sin[(a + b)*x], x]", "True");
	}

	public void test0021() {
		check("Rubi`BinomialParts[Cos[a*x], x]", "False");
	}

	public void test0022() {
		check("Rubi`BinomialParts[Sin[b*x], x]", "False");
	}

	public void test0023() {
		check("Rubi`BinomialQ[Cos[a*x], x]", "False");
	}

	public void test0024() {
		check("Rubi`BinomialQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0025() {
		check("Rubi`BinomialQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0026() {
		check("Rubi`BinomialQ[Sin[b*x], x]", "False");
	}

	public void test0027() {
		check("Rubi`CalculusQ[a - b]", "False");
	}

	public void test0028() {
		check("Rubi`CalculusQ[-b]", "False");
	}

	public void test0029() {
		check("Rubi`CalculusQ[a + b]", "False");
	}

	public void test0030() {
		check("Rubi`CalculusQ[a*x]", "False");
	}

	public void test0031() {
		check("Rubi`CalculusQ[(a - b)*x]", "False");
	}

	public void test0032() {
		check("Rubi`CalculusQ[b*x]", "False");
	}

	public void test0033() {
		check("Rubi`CalculusQ[(a + b)*x]", "False");
	}

	public void test0034() {
		check("Rubi`CalculusQ[Cos[a*x]]", "False");
	}

	public void test0035() {
		check("Rubi`CalculusQ[sin[(a - b)*x]]", "False");
	}

	public void test0036() {
		check("Rubi`CalculusQ[x*sin[(a - b)*x]]", "False");
	}

	public void test0037() {
		check("Rubi`CalculusQ[sin[(a + b)*x]]", "False");
	}

	public void test0038() {
		check("Rubi`CalculusQ[x*sin[(a + b)*x]]", "False");
	}

	public void test0039() {
		check("Rubi`CalculusQ[Sin[(a - b)*x]]", "False");
	}

	public void test0040() {
		check("Rubi`CalculusQ[x*Sin[(a - b)*x]]", "False");
	}

	public void test0041() {
		check("Rubi`CalculusQ[Sin[b*x]]", "False");
	}

	public void test0042() {
		check("Rubi`CalculusQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0043() {
		check("Rubi`CalculusQ[x*Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0044() {
		check("Rubi`CalculusQ[Sin[(a + b)*x]]", "False");
	}

	public void test0045() {
		check("Rubi`CalculusQ[x*Sin[(a + b)*x]]", "False");
	}

	public void test0046() {
		check("Rubi`Coeff[(a - b)*x, x, 1]", "a - b");
	}

	public void test0047() {
		check("Rubi`Coeff[(a + b)*x, x, 1]", "a + b");
	}

	public void test0048() {
		check("Rubi`Coeff[a*x - b*x, x, 1]", "a - b");
	}

	public void test0049() {
		check("Rubi`Coeff[a*x + b*x, x, 1]", "a + b");
	}

	public void test0050() {
		check("Rubi`CommonFactors[{a, -b}]", "{1, a, -b}");
	}

	public void test0051() {
		check("Rubi`CommonFactors[{a, b}]", "{1, a, b}");
	}

	public void test0052() {
		check("Rubi`ComplexNumberQ[1]", "False");
	}

	public void test0053() {
		check("Rubi`ComplexNumberQ[a]", "False");
	}

	public void test0054() {
		check("Rubi`ComplexNumberQ[b]", "False");
	}

	public void test0055() {
		check("Rubi`ComplexNumberQ[x]", "False");
	}

	public void test0056() {
		check("Rubi`ContentFactor[a - b]", "a - b");
	}

	public void test0057() {
		check("Rubi`ContentFactor[a + b]", "a + b");
	}

	public void test0058() {
		check("Rubi`ContentFactorAux[a - b]", "a - b");
	}

	public void test0059() {
		check("Rubi`ContentFactorAux[a + b]", "a + b");
	}

	public void test0060() {
		check("Rubi`DeactivateTrig[Sin[(a - b)*x], x]", "§sin[(a-b)*x]");
	}

	public void test0061() {
		check("Rubi`DeactivateTrig[Sin[(a + b)*x], x]", "§sin[(a+b)*x]");
	}

	public void test0062() {
		check("Rubi`DeactivateTrigAux[Sin[(a - b)*x], x]", "§sin[(a-b)*x]");
	}

	public void test0063() {
		check("Rubi`DeactivateTrigAux[Sin[(a + b)*x], x]", "§sin[(a+b)*x]");
	}

	public void test0064() {
		check("Rubi`EqQ[-1/2, 1/2]", "False");
	}

	public void test0065() {
		check("Rubi`EqQ[0, 0]", "True");
	}

	public void test0066() {
		check("Rubi`EqQ[1/2, 1/2]", "True");
	}

	public void test0067() {
		check("Rubi`EqQ[1, 1]", "True");
	}

	public void test0068() {
		check("Rubi`EqQ[(a - b)^(-1), 1/2]", "False");
	}

	public void test0069() {
		check("Rubi`EqQ[a - b, 1/2]", "False");
	}

	public void test0070() {
		check("Rubi`EqQ[1 + b, 0]", "False");
	}

	public void test0071() {
		check("Rubi`EqQ[(a + b)^(-1), 1/2]", "False");
	}

	public void test0072() {
		check("Rubi`EqQ[a + b, 0]", "False");
	}

	public void test0073() {
		check("Rubi`EqQ[a + b, 1/2]", "False");
	}

	public void test0074() {
		check("Rubi`EqQ[Cos, cos]", "False");
	}

	public void test0075() {
		check("Rubi`EqQ[Cos, Cos]", "True");
	}

	public void test0076() {
		check("Rubi`EqQ[Cos, cot]", "False");
	}

	public void test0077() {
		check("Rubi`EqQ[Cos, Cot]", "False");
	}

	public void test0078() {
		check("Rubi`EqQ[Cos, sin]", "False");
	}

	public void test0079() {
		check("Rubi`EqQ[Cos, Sin]", "False");
	}

	public void test0080() {
		check("Rubi`EqQ[Cos, tan]", "False");
	}

	public void test0081() {
		check("Rubi`EqQ[Cos, Tan]", "False");
	}

	public void test0082() {
		check("Rubi`EqQ[Sin, cos]", "False");
	}

	public void test0083() {
		check("Rubi`EqQ[Sin, Cos]", "False");
	}

	public void test0084() {
		check("Rubi`EqQ[Sin, cot]", "False");
	}

	public void test0085() {
		check("Rubi`EqQ[Sin, Cot]", "False");
	}

	public void test0086() {
		check("Rubi`EqQ[Sin, sin]", "False");
	}

	public void test0087() {
		check("Rubi`EqQ[Sin, Sin]", "True");
	}

	public void test0088() {
		check("Rubi`EqQ[Sin, tan]", "False");
	}

	public void test0089() {
		check("Rubi`EqQ[Sin, Tan]", "False");
	}

	public void test0090() {
		check("Rubi`EqQ[a*x, b*x]", "False");
	}

	public void test0091() {
		check("Rubi`EqQ[b*x, a*x]", "False");
	}

	public void test0092() {
		check("Rubi`EqQ[(a - b)^(-1) + x, 0]", "False");
	}

	public void test0093() {
		check("Rubi`EqQ[a - b + x, 0]", "False");
	}

	public void test0094() {
		check("Rubi`EqQ[a + b + x, 0]", "False");
	}

	public void test0095() {
		check("Rubi`EqQ[(a + b)^(-1) + x, 0]", "False");
	}

	public void test0096() {
		check("Rubi`EqQ[a - b + x^2, 0]", "False");
	}

	public void test0097() {
		check("Rubi`EqQ[a + b + x^2, 0]", "False");
	}

	public void test0098() {
		check("Rubi`EveryQ[#1 === 1 & , {1, 1}]", "True");
	}

	public void test0099() {
		check("Rubi`EveryQ[#1 === 1 & , {1, b}]", "False");
	}

	public void test0100() {
		check("Rubi`ExpandToSum[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0101() {
		check("Rubi`ExpandToSum[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0102() {
		check("Rubi`ExpandToSum[a*x - b*x, x]", "(a - b)*x");
	}

	public void test0103() {
		check("Rubi`ExpandToSum[a*x + b*x, x]", "(a + b)*x");
	}

	public void test0104() {
		check("Rubi`ExpandTrigReduce[Cos[a*x]*Sin[b*x], x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0105() {
		check("Rubi`ExpandTrigReduceAux[Cos[a*x]*Sin[b*x], x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0106() {
		check("Rubi`Expon[(a - b)*x, x, List]", "{1}");
	}

	public void test0107() {
		check("Rubi`Expon[(a + b)*x, x, List]", "{1}");
	}

	public void test0108() {
		check("Rubi`Expon[a*x - b*x, x, List]", "{1}");
	}

	public void test0109() {
		check("Rubi`Expon[a*x + b*x, x, List]", "{1}");
	}

	public void test0110() {
		check("Rubi`FactorAbsurdNumber[-1]", "{{-1, 1}}");
	}

	public void test0111() {
		check("Rubi`FactorAbsurdNumber[1]", "{{1, 1}}");
	}

	public void test0112() {
		check("Rubi`FactorNumericGcd[0]", "0");
	}

	public void test0113() {
		check("Rubi`FactorNumericGcd[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0114() {
		check("Rubi`FactorNumericGcd[a - b]", "a - b");
	}

	public void test0115() {
		check("Rubi`FactorNumericGcd[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0116() {
		check("Rubi`FactorNumericGcd[a + b]", "a + b");
	}

	public void test0117() {
		check("Rubi`FactorNumericGcd[x]", "x");
	}

	public void test0118() {
		check("Rubi`FactorNumericGcd[x^2]", "x^2");
	}

	public void test0119() {
		check("Rubi`FactorNumericGcd[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0120() {
		check("Rubi`FactorNumericGcd[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0121() {
		check("Rubi`FactorNumericGcd[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0122() {
		check("Rubi`FactorNumericGcd[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0123() {
		check("Rubi`FactorNumericGcd[Cos[(a - b)*x]]", "Cos[(a - b)*x]");
	}

	public void test0124() {
		check("Rubi`FactorNumericGcd[Cos[(a + b)*x]]", "Cos[(a + b)*x]");
	}

	public void test0125() {
		check("Rubi`FactorOrder[1, 1]", "0");
	}

	public void test0126() {
		check("Rubi`FactorOrder[a, 1]", "1");
	}

	public void test0127() {
		check("Rubi`FactorOrder[b, 1]", "1");
	}

	public void test0128() {
		check("Rubi`FactorOrder[b, a]", "-1");
	}

	public void test0129() {
		check("Rubi`FalseQ[False]", "True");
	}

	public void test0130() {
		check("Rubi`FalseQ[Null]", "False");
	}

	public void test0131() {
		check("Rubi`FalseQ[a*x]", "False");
	}

	public void test0132() {
		check("Rubi`FindTrigFactor[Cos, Sec, 1, a*x, False]", "False");
	}

	public void test0133() {
		check("Rubi`FindTrigFactor[Cos, Sec, x, a*x, False]", "False");
	}

	public void test0134() {
		check("Rubi`FindTrigFactor[Cos, Sec, b*x, a*x, False]", "False");
	}

	public void test0135() {
		check("Rubi`FindTrigFactor[Sin, Csc, 1, a*x, False]", "False");
	}

	public void test0136() {
		check("Rubi`FindTrigFactor[Sin, Csc, 1, b*x, False]", "False");
	}

	public void test0137() {
		check("Rubi`FindTrigFactor[Sin, Csc, x, a*x, False]", "False");
	}

	public void test0138() {
		check("Rubi`FindTrigFactor[Sin, Csc, x, b*x, False]", "False");
	}

	public void test0139() {
		check("Rubi`FindTrigFactor[Sin, Csc, a*x, b*x, False]", "False");
	}

	public void test0140() {
		check("Rubi`FindTrigFactor[Sin, Csc, b*x, a*x, False]", "False");
	}

	public void test0141() {
		check("Rubi`FindTrigFactor[Tan, Cot, 1, a*x, True]", "False");
	}

	public void test0142() {
		check("Rubi`FindTrigFactor[Tan, Cot, 1, b*x, True]", "False");
	}

	public void test0143() {
		check("Rubi`FindTrigFactor[Tan, Cot, x, a*x, True]", "False");
	}

	public void test0144() {
		check("Rubi`FindTrigFactor[Tan, Cot, x, b*x, True]", "False");
	}

	public void test0145() {
		check("Rubi`FindTrigFactor[Tan, Cot, a*x, b*x, True]", "False");
	}

	public void test0146() {
		check("Rubi`FindTrigFactor[Tan, Cot, b*x, a*x, True]", "False");
	}

	public void test0147() {
		check("Rubi`FixInertTrigFunction[sin[(a - b)*x], x]", "sin[(a - b)*x]");
	}

	public void test0148() {
		check("Rubi`FixInertTrigFunction[sin[(a + b)*x], x]", "sin[(a + b)*x]");
	}

	public void test0149() {
		check("Rubi`FixSimplify[0]", "0");
	}

	public void test0150() {
		check("Rubi`FixSimplify[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0151() {
		check("Rubi`FixSimplify[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0152() {
		check("Rubi`FixSimplify[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0153() {
		check("Rubi`FixSimplify[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0154() {
		check("Rubi`FractionalPowerOfSquareQ[-1]", "False");
	}

	public void test0155() {
		check("Rubi`FractionalPowerOfSquareQ[0]", "False");
	}

	public void test0156() {
		check("Rubi`FractionalPowerOfSquareQ[2]", "False");
	}

	public void test0157() {
		check("Rubi`FractionalPowerOfSquareQ[a]", "False");
	}

	public void test0158() {
		check("Rubi`FractionalPowerOfSquareQ[(a - b)^(-1)]", "False");
	}

	public void test0159() {
		check("Rubi`FractionalPowerOfSquareQ[a - b]", "False");
	}

	public void test0160() {
		check("Rubi`FractionalPowerOfSquareQ[-b]", "False");
	}

	public void test0161() {
		check("Rubi`FractionalPowerOfSquareQ[b]", "False");
	}

	public void test0162() {
		check("Rubi`FractionalPowerOfSquareQ[(a + b)^(-1)]", "False");
	}

	public void test0163() {
		check("Rubi`FractionalPowerOfSquareQ[a + b]", "False");
	}

	public void test0164() {
		check("Rubi`FractionalPowerOfSquareQ[x]", "False");
	}

	public void test0165() {
		check("Rubi`FractionalPowerOfSquareQ[x^2]", "False");
	}

	public void test0166() {
		check("Rubi`FractionalPowerOfSquareQ[x^2/(a - b)]", "False");
	}

	public void test0167() {
		check("Rubi`FractionalPowerOfSquareQ[(a - b)*x^2]", "False");
	}

	public void test0168() {
		check("Rubi`FractionalPowerOfSquareQ[x^2/(a + b)]", "False");
	}

	public void test0169() {
		check("Rubi`FractionalPowerOfSquareQ[(a + b)*x^2]", "False");
	}

	public void test0170() {
		check("Rubi`FractionQ[-1]", "False");
	}

	public void test0171() {
		check("Rubi`FractionQ[2]", "False");
	}

	public void test0172() {
		check("Rubi`FreeFactors[(a - b)*x, x]", "a - b");
	}

	public void test0173() {
		check("Rubi`FreeFactors[(a + b)*x, x]", "a + b");
	}

	public void test0174() {
		check("Rubi`FreeFactors[-Cos[(a - b)*x]/(2*(a - b)), x]", "-1/(2*(a - b))");
	}

	public void test0175() {
		check("Rubi`FreeFactors[Cos[(a - b)*x]/(2*(a - b)), x]", "1/(2*(a - b))");
	}

	public void test0176() {
		check("Rubi`FreeFactors[Cos[(a - b)*x]/(a - b), x]", "(a - b)^(-1)");
	}

	public void test0177() {
		check("Rubi`FreeFactors[Cos[b*x], x]", "1");
	}

	public void test0178() {
		check("Rubi`FreeFactors[-Cos[(a + b)*x]/(2*(a + b)), x]", "-1/(2*(a + b))");
	}

	public void test0179() {
		check("Rubi`FreeFactors[Cos[(a + b)*x]/(a + b), x]", "(a + b)^(-1)");
	}

	public void test0180() {
		check("Rubi`FreeFactors[Sin[a*x], x]", "1");
	}

	public void test0181() {
		check("Rubi`FreeFactors[-Sin[(a - b)*x]/2, x]", "-1/2");
	}

	public void test0182() {
		check("Rubi`FreeFactors[Sin[(a + b)*x]/2, x]", "1/2");
	}

	public void test0183() {
		check("Rubi`FreeTerms[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "0");
	}

	public void test0184() {
		check("Rubi`FunctionOfCosQ[a, b*x, x]", "True");
	}

	public void test0185() {
		check("Rubi`FunctionOfCosQ[x, b*x, x]", "False");
	}

	public void test0186() {
		check("Rubi`FunctionOfCosQ[a*x, b*x, x]", "False");
	}

	public void test0187() {
		check("Rubi`FunctionOfCosQ[Cos[a*x], b*x, x]", "False");
	}

	public void test0188() {
		check("Rubi`FunctionOfCosQ[Cos[a*x], Cos[b*x][[1]], x]", "False");
	}

	public void test0189() {
		check("Rubi`FunctionOfExponentialQ[sin[(a - b)*x], x]", "False");
	}

	public void test0190() {
		check("Rubi`FunctionOfExponentialQ[sin[(a + b)*x], x]", "False");
	}

	public void test0191() {
		check("Rubi`FunctionOfExponentialQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0192() {
		check("Rubi`FunctionOfExponentialQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0193() {
		check("Rubi`FunctionOfExponentialQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0194() {
		check("Rubi`FunctionOfExponentialTest[a, x]", "True");
	}

	public void test0195() {
		check("Rubi`FunctionOfExponentialTest[a - b, x]", "True");
	}

	public void test0196() {
		check("Rubi`FunctionOfExponentialTest[a + b, x]", "True");
	}

	public void test0197() {
		check("Rubi`FunctionOfExponentialTest[x, x]", "False");
	}

	public void test0198() {
		check("Rubi`FunctionOfExponentialTest[a*x, x]", "False");
	}

	public void test0199() {
		check("Rubi`FunctionOfExponentialTest[(a - b)*x, x]", "False");
	}

	public void test0200() {
		check("Rubi`FunctionOfExponentialTest[(a + b)*x, x]", "False");
	}

	public void test0201() {
		check("Rubi`FunctionOfExponentialTest[Cos[a*x], x]", "False");
	}

	public void test0202() {
		check("Rubi`FunctionOfExponentialTest[sin[(a - b)*x], x]", "False");
	}

	public void test0203() {
		check("Rubi`FunctionOfExponentialTest[sin[(a + b)*x], x]", "False");
	}

	public void test0204() {
		check("Rubi`FunctionOfExponentialTest[Sin[(a - b)*x], x]", "False");
	}

	public void test0205() {
		check("Rubi`FunctionOfExponentialTest[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0206() {
		check("Rubi`FunctionOfExponentialTest[Sin[(a + b)*x], x]", "False");
	}

	public void test0207() {
		check("Rubi`FunctionOfLog[x*sin[(a - b)*x], x]", "False");
	}

	public void test0208() {
		check("Rubi`FunctionOfLog[x*sin[(a + b)*x], x]", "False");
	}

	public void test0209() {
		check("Rubi`FunctionOfLog[x*Sin[(a - b)*x], x]", "False");
	}

	public void test0210() {
		check("Rubi`FunctionOfLog[x*Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0211() {
		check("Rubi`FunctionOfLog[x*Sin[(a + b)*x], x]", "False");
	}

	public void test0212() {
		check("Rubi`FunctionOfLog[x, False, False, x]", "False");
	}

	public void test0213() {
		check("Rubi`FunctionOfLog[x*sin[(a - b)*x], False, False, x]", "False");
	}

	public void test0214() {
		check("Rubi`FunctionOfLog[x*sin[(a + b)*x], False, False, x]", "False");
	}

	public void test0215() {
		check("Rubi`FunctionOfLog[x*Sin[(a - b)*x], False, False, x]", "False");
	}

	public void test0216() {
		check("Rubi`FunctionOfLog[x*Cos[a*x]*Sin[b*x], False, False, x]", "False");
	}

	public void test0217() {
		check("Rubi`FunctionOfLog[x*Sin[(a + b)*x], False, False, x]", "False");
	}

	public void test0218() {
		check("Rubi`FunctionOfQ[Cos[b*x], Cos[a*x], x]", "False");
	}

	public void test0219() {
		check("Rubi`FunctionOfQ[Sin[a*x], Sin[b*x], x]", "False");
	}

	public void test0220() {
		check("Rubi`FunctionOfQ[Cos[b*x], Cos[a*x], x, True]", "False");
	}

	public void test0221() {
		check("Rubi`FunctionOfQ[Sin[a*x], Sin[b*x], x, True]", "False");
	}

	public void test0222() {
		check("Rubi`FunctionOfSinQ[b, a*x, x]", "True");
	}

	public void test0223() {
		check("Rubi`FunctionOfSinQ[x, a*x, x]", "False");
	}

	public void test0224() {
		check("Rubi`FunctionOfSinQ[b*x, a*x, x]", "False");
	}

	public void test0225() {
		check("Rubi`FunctionOfSinQ[Sin[b*x], a*x, x]", "False");
	}

	public void test0226() {
		check("Rubi`FunctionOfSinQ[Sin[b*x], Sin[a*x][[1]], x]", "False");
	}

	public void test0227() {
		check("Rubi`FunctionOfTrig[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0228() {
		check("Rubi`FunctionOfTrig[-1, Null, x]", "Null");
	}

	public void test0229() {
		check("Rubi`FunctionOfTrig[a, Null, x]", "Null");
	}

	public void test0230() {
		check("Rubi`FunctionOfTrig[a - b, Null, x]", "Null");
	}

	public void test0231() {
		check("Rubi`FunctionOfTrig[-b, Null, x]", "Null");
	}

	public void test0232() {
		check("Rubi`FunctionOfTrig[b, Null, x]", "Null");
	}

	public void test0233() {
		check("Rubi`FunctionOfTrig[a + b, Null, x]", "Null");
	}

	public void test0234() {
		check("Rubi`FunctionOfTrig[x, Null, x]", "False");
	}

	public void test0235() {
		check("Rubi`FunctionOfTrig[(a - b)*x, Null, x]", "False");
	}

	public void test0236() {
		check("Rubi`FunctionOfTrig[(a + b)*x, Null, x]", "False");
	}

	public void test0237() {
		check("Rubi`FunctionOfTrig[Cos[a*x], Null, x]", "a*x");
	}

	public void test0238() {
		check("Rubi`FunctionOfTrig[sin[(a - b)*x], Null, x]", "False");
	}

	public void test0239() {
		check("Rubi`FunctionOfTrig[sin[(a + b)*x], Null, x]", "False");
	}

	public void test0240() {
		check("Rubi`FunctionOfTrig[Sin[(a - b)*x], Null, x]", "(a - b)*x");
	}

	public void test0241() {
		check("Rubi`FunctionOfTrig[Sin[b*x], a*x, x]", "False");
	}

	public void test0242() {
		check("Rubi`FunctionOfTrig[Cos[a*x]*Sin[b*x], Null, x]", "False");
	}

	public void test0243() {
		check("Rubi`FunctionOfTrig[Sin[(a + b)*x], Null, x]", "(a + b)*x");
	}

	public void test0244() {
		check("Rubi`FunctionOfTrigOfLinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0245() {
		check("Rubi`FunctionOfTrigOfLinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0246() {
		check("Rubi`FunctionOfTrigOfLinearQ[Sin[(a - b)*x], x]", "True");
	}

	public void test0247() {
		check("Rubi`FunctionOfTrigOfLinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0248() {
		check("Rubi`FunctionOfTrigOfLinearQ[Sin[(a + b)*x], x]", "True");
	}

	public void test0249() {
		check("Rubi`HyperbolicQ[a - b]", "False");
	}

	public void test0250() {
		check("Rubi`HyperbolicQ[-b]", "False");
	}

	public void test0251() {
		check("Rubi`HyperbolicQ[a + b]", "False");
	}

	public void test0252() {
		check("Rubi`HyperbolicQ[a*x]", "False");
	}

	public void test0253() {
		check("Rubi`HyperbolicQ[(a - b)*x]", "False");
	}

	public void test0254() {
		check("Rubi`HyperbolicQ[(a + b)*x]", "False");
	}

	public void test0255() {
		check("Rubi`HyperbolicQ[Cos[a*x]]", "False");
	}

	public void test0256() {
		check("Rubi`HyperbolicQ[sin[(a - b)*x]]", "False");
	}

	public void test0257() {
		check("Rubi`HyperbolicQ[sin[(a + b)*x]]", "False");
	}

	public void test0258() {
		check("Rubi`HyperbolicQ[Sin[(a - b)*x]]", "False");
	}

	public void test0259() {
		check("Rubi`HyperbolicQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0260() {
		check("Rubi`HyperbolicQ[Sin[(a + b)*x]]", "False");
	}

	public void test0261() {
		check("Rubi`IGtQ[1, 0]", "True");
	}

	public void test0262() {
		check("Rubi`InertTrigFreeQ[Cos[a*x]]", "True");
	}

	public void test0263() {
		check("Rubi`InertTrigFreeQ[Sin[b*x]]", "True");
	}

	public void test0264() {
		check("Rubi`InertTrigFreeQ[Cos[a*x]*Sin[b*x]]", "True");
	}

	public void test0265() {
		check("Rubi`IntegerQuotientQ[a*x, b*x]", "False");
	}

	public void test0266() {
		check("Rubi`IntegerQuotientQ[b*x, a*x]", "False");
	}

	public void test0267() {
		check("Rubi`IntegralFreeQ[-(Cos[(a - b)*x]/(a - b))]", "True");
	}

	public void test0268() {
		check("Rubi`IntegralFreeQ[-(Cos[(a + b)*x]/(a + b))]", "True");
	}

	public void test0269() {
		check("Rubi`IntSum[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0270() {
		check("Rubi`IntTerm[-Sin[(a - b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0271() {
		check("Rubi`IntTerm[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0272() {
		check("Rubi`IntTerm[Sin[(a + b)*x]/2, x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0273() {
		check("Rubi`InverseFunctionFreeQ[a, x]", "True");
	}

	public void test0274() {
		check("Rubi`InverseFunctionFreeQ[b, x]", "True");
	}

	public void test0275() {
		check("Rubi`InverseFunctionFreeQ[x, x]", "True");
	}

	public void test0276() {
		check("Rubi`InverseFunctionFreeQ[a*x, x]", "True");
	}

	public void test0277() {
		check("Rubi`InverseFunctionFreeQ[b*x, x]", "True");
	}

	public void test0278() {
		check("Rubi`InverseFunctionFreeQ[Cos[a*x], x]", "True");
	}

	public void test0279() {
		check("Rubi`InverseFunctionFreeQ[Sin[b*x], x]", "True");
	}

	public void test0280() {
		check("Rubi`InverseFunctionFreeQ[Cos[a*x]*Sin[b*x], x]", "True");
	}

	public void test0281() {
		check("Rubi`InverseFunctionQ[a*x]", "False");
	}

	public void test0282() {
		check("Rubi`InverseFunctionQ[b*x]", "False");
	}

	public void test0283() {
		check("Rubi`InverseFunctionQ[Cos[a*x]]", "False");
	}

	public void test0284() {
		check("Rubi`InverseFunctionQ[Sin[b*x]]", "False");
	}

	public void test0285() {
		check("Rubi`InverseFunctionQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0286() {
		check("Rubi`InverseHyperbolicQ[a*x]", "False");
	}

	public void test0287() {
		check("Rubi`InverseHyperbolicQ[b*x]", "False");
	}

	public void test0288() {
		check("Rubi`InverseHyperbolicQ[Cos[a*x]]", "False");
	}

	public void test0289() {
		check("Rubi`InverseHyperbolicQ[Sin[b*x]]", "False");
	}

	public void test0290() {
		check("Rubi`InverseHyperbolicQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0291() {
		check("Rubi`InverseTrigQ[a*x]", "False");
	}

	public void test0292() {
		check("Rubi`InverseTrigQ[b*x]", "False");
	}

	public void test0293() {
		check("Rubi`InverseTrigQ[Cos[a*x]]", "False");
	}

	public void test0294() {
		check("Rubi`InverseTrigQ[Sin[b*x]]", "False");
	}

	public void test0295() {
		check("Rubi`InverseTrigQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0296() {
		check("Rubi`LeadBase[1]", "1");
	}

	public void test0297() {
		check("Rubi`LeadBase[a]", "a");
	}

	public void test0298() {
		check("Rubi`LeadBase[b]", "b");
	}

	public void test0299() {
		check("Rubi`LeadBase[x]", "x");
	}

	public void test0300() {
		check("Rubi`LeadBase[a*x]", "a");
	}

	public void test0301() {
		check("Rubi`LeadBase[b*x]", "b");
	}

	public void test0302() {
		check("Rubi`LeadDegree[1]", "1");
	}

	public void test0303() {
		check("Rubi`LeadDegree[a]", "1");
	}

	public void test0304() {
		check("Rubi`LeadDegree[b]", "1");
	}

	public void test0305() {
		check("Rubi`LeadFactor[1]", "1");
	}

	public void test0306() {
		check("Rubi`LeadFactor[a]", "a");
	}

	public void test0307() {
		check("Rubi`LeadFactor[b]", "b");
	}

	public void test0308() {
		check("Rubi`LeadFactor[x]", "x");
	}

	public void test0309() {
		check("Rubi`LeadFactor[a*x]", "a");
	}

	public void test0310() {
		check("Rubi`LeadFactor[b*x]", "b");
	}

	public void test0311() {
		check("Rubi`LeadFactor[First[a*x]]", "a");
	}

	public void test0312() {
		check("Rubi`LeadFactor[First[b*x]]", "b");
	}

	public void test0313() {
		check("Rubi`LinearQ[a*x, x]", "True");
	}

	public void test0314() {
		check("Rubi`LinearQ[(a - b)*x, x]", "True");
	}

	public void test0315() {
		check("Rubi`LinearQ[b*x, x]", "True");
	}

	public void test0316() {
		check("Rubi`LinearQ[(a + b)*x, x]", "True");
	}

	public void test0317() {
		check("Rubi`LinearQ[Cos[a*x], x]", "False");
	}

	public void test0318() {
		check("Rubi`LinearQ[{Cos[a*x], b*x}, x]", "False");
	}

	public void test0319() {
		check("Rubi`LinearQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0320() {
		check("Rubi`LinearQ[{Sin[b*x], a*x}, x]", "False");
	}

	public void test0321() {
		check("Rubi`LinearQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0322() {
		check("Rubi`LinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0323() {
		check("Rubi`LinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0324() {
		check("Rubi`LinearQ[-Sin[(a - b)*x]/2, x]", "False");
	}

	public void test0325() {
		check("Rubi`LinearQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0326() {
		check("Rubi`LinearQ[Sin[b*x], x]", "False");
	}

	public void test0327() {
		check("Rubi`LinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0328() {
		check("Rubi`LinearQ[Sin[(a + b)*x]/2, x]", "False");
	}

	public void test0329() {
		check("Rubi`LinearQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0330() {
		check("Rubi`LogQ[1]", "False");
	}

	public void test0331() {
		check("Rubi`LogQ[a]", "False");
	}

	public void test0332() {
		check("Rubi`LogQ[a*x]", "False");
	}

	public void test0333() {
		check("Rubi`LogQ[b*x]", "False");
	}

	public void test0334() {
		check("Rubi`LogQ[Cos[a*x]]", "False");
	}

	public void test0335() {
		check("Rubi`LogQ[x*sin[(a - b)*x]]", "False");
	}

	public void test0336() {
		check("Rubi`LogQ[x*sin[(a + b)*x]]", "False");
	}

	public void test0337() {
		check("Rubi`LogQ[x*Sin[(a - b)*x]]", "False");
	}

	public void test0338() {
		check("Rubi`LogQ[Sin[b*x]]", "False");
	}

	public void test0339() {
		check("Rubi`LogQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0340() {
		check("Rubi`LogQ[x*Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0341() {
		check("Rubi`LogQ[x*Sin[(a + b)*x]]", "False");
	}

	public void test0342() {
		check("Rubi`MergeableFactorQ[-1/2, 1, Cos[(a - b)*x]]", "False");
	}

	public void test0343() {
		check("Rubi`MergeableFactorQ[-1/2, 1, Cos[(a + b)*x]]", "False");
	}

	public void test0344() {
		check("Rubi`MergeableFactorQ[1/2, 1, Cos[(a - b)*x]]", "False");
	}

	public void test0345() {
		check("Rubi`MergeableFactorQ[a - b, -1, -1/2]", "False");
	}

	public void test0346() {
		check("Rubi`MergeableFactorQ[a - b, -1, 1/2]", "False");
	}

	public void test0347() {
		check("Rubi`MergeableFactorQ[a - b, -1, -Cos[(a - b)*x]/2]", "False");
	}

	public void test0348() {
		check("Rubi`MergeableFactorQ[a - b, -1, Cos[(a - b)*x]/2]", "False");
	}

	public void test0349() {
		check("Rubi`MergeableFactorQ[a - b, -1, Cos[(a - b)*x]]", "False");
	}

	public void test0350() {
		check("Rubi`MergeableFactorQ[a - b, 1, x]", "False");
	}

	public void test0351() {
		check("Rubi`MergeableFactorQ[a + b, -1, -1/2]", "False");
	}

	public void test0352() {
		check("Rubi`MergeableFactorQ[a + b, -1, -Cos[(a + b)*x]/2]", "False");
	}

	public void test0353() {
		check("Rubi`MergeableFactorQ[a + b, -1, Cos[(a + b)*x]]", "False");
	}

	public void test0354() {
		check("Rubi`MergeableFactorQ[a + b, 1, x]", "False");
	}

	public void test0355() {
		check("Rubi`MergeFactors[-1/2, Cos[(a - b)*x]]", "-Cos[(a - b)*x]/2");
	}

	public void test0356() {
		check("Rubi`MergeFactors[-1/2, Cos[(a + b)*x]]", "-Cos[(a + b)*x]/2");
	}

	public void test0357() {
		check("Rubi`MergeFactors[1/2, Cos[(a - b)*x]]", "Cos[(a - b)*x]/2");
	}

	public void test0358() {
		check("Rubi`MergeFactors[-1/(2*(a - b)), Cos[(a - b)*x]]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0359() {
		check("Rubi`MergeFactors[1/(2*(a - b)), Cos[(a - b)*x]]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0360() {
		check("Rubi`MergeFactors[(a - b)^(-1), -Cos[(a - b)*x]/2]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0361() {
		check("Rubi`MergeFactors[(a - b)^(-1), Cos[(a - b)*x]/2]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0362() {
		check("Rubi`MergeFactors[(a - b)^(-1), Cos[(a - b)*x]]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0363() {
		check("Rubi`MergeFactors[a - b, x]", "(a - b)*x");
	}

	public void test0364() {
		check("Rubi`MergeFactors[-1/(2*(a + b)), Cos[(a + b)*x]]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0365() {
		check("Rubi`MergeFactors[(a + b)^(-1), -Cos[(a + b)*x]/2]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0366() {
		check("Rubi`MergeFactors[(a + b)^(-1), Cos[(a + b)*x]]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0367() {
		check("Rubi`MergeFactors[a + b, x]", "(a + b)*x");
	}

	public void test0368() {
		check("Rubi`MostMainFactorPosition[{1, b}]", "2");
	}

	public void test0369() {
		check("Rubi`MostMainFactorPosition[{a, b}]", "1");
	}

	public void test0370() {
		check("Rubi`NeQ[1, -1]", "True");
	}

	public void test0371() {
		check("Rubi`NeQ[a, 0]", "True");
	}

	public void test0372() {
		check("Rubi`NeQ[a - b, 0]", "True");
	}

	public void test0373() {
		check("Rubi`NeQ[b, 0]", "True");
	}

	public void test0374() {
		check("Rubi`NeQ[a + b, 0]", "True");
	}

	public void test0375() {
		check("Rubi`NonabsurdNumberFactors[-1]", "1");
	}

	public void test0376() {
		check("Rubi`NonabsurdNumberFactors[a]", "a");
	}

	public void test0377() {
		check("Rubi`NonabsurdNumberFactors[-b]", "b");
	}

	public void test0378() {
		check("Rubi`NonabsurdNumberFactors[b]", "b");
	}

	public void test0379() {
		check("Rubi`NonfreeFactors[(a - b)*x, x]", "x");
	}

	public void test0380() {
		check("Rubi`NonfreeFactors[(a + b)*x, x]", "x");
	}

	public void test0381() {
		check("Rubi`NonfreeFactors[-Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]");
	}

	public void test0382() {
		check("Rubi`NonfreeFactors[Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]");
	}

	public void test0383() {
		check("Rubi`NonfreeFactors[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]");
	}

	public void test0384() {
		check("Rubi`NonfreeFactors[-Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a + b)*x]");
	}

	public void test0385() {
		check("Rubi`NonfreeFactors[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]");
	}

	public void test0386() {
		check("Rubi`NonfreeFactors[-Sin[(a - b)*x]/2, x]", "Sin[(a - b)*x]");
	}

	public void test0387() {
		check("Rubi`NonfreeFactors[Sin[(a + b)*x]/2, x]", "Sin[(a + b)*x]");
	}

	public void test0388() {
		check("Rubi`NonfreeTerms[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0389() {
		check("Rubi`NonnumericFactors[-1/2]", "1");
	}

	public void test0390() {
		check("Rubi`NonnumericFactors[1/2]", "1");
	}

	public void test0391() {
		check("Rubi`NonnumericFactors[a]", "a");
	}

	public void test0392() {
		check("Rubi`NonnumericFactors[-1/(2*(a - b))]", "(a - b)^(-1)");
	}

	public void test0393() {
		check("Rubi`NonnumericFactors[1/(2*(a - b))]", "(a - b)^(-1)");
	}

	public void test0394() {
		check("Rubi`NonnumericFactors[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0395() {
		check("Rubi`NonnumericFactors[a - b]", "a - b");
	}

	public void test0396() {
		check("Rubi`NonnumericFactors[b]", "b");
	}

	public void test0397() {
		check("Rubi`NonnumericFactors[-1/(2*(a + b))]", "(a + b)^(-1)");
	}

	public void test0398() {
		check("Rubi`NonnumericFactors[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0399() {
		check("Rubi`NonnumericFactors[a + b]", "a + b");
	}

	public void test0400() {
		check("Rubi`NonsumQ[sin[(a - b)*x]]", "True");
	}

	public void test0401() {
		check("Rubi`NonsumQ[sin[(a + b)*x]]", "True");
	}

	public void test0402() {
		check("Rubi`NonsumQ[Sin[(a - b)*x]]", "True");
	}

	public void test0403() {
		check("Rubi`NonsumQ[Cos[a*x]*Sin[b*x]]", "True");
	}

	public void test0404() {
		check("Rubi`NonsumQ[Sin[(a + b)*x]]", "True");
	}

	public void test0405() {
		check("Rubi`NormalizeSumFactors[-1]", "-1");
	}

	public void test0406() {
		check("Rubi`NormalizeSumFactors[-1/2]", "-1/2");
	}

	public void test0407() {
		check("Rubi`NormalizeSumFactors[0]", "0");
	}

	public void test0408() {
		check("Rubi`NormalizeSumFactors[1/2]", "1/2");
	}

	public void test0409() {
		check("Rubi`NormalizeSumFactors[a]", "a");
	}

	public void test0410() {
		check("Rubi`NormalizeSumFactors[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0411() {
		check("Rubi`NormalizeSumFactors[a - b]", "a - b");
	}

	public void test0412() {
		check("Rubi`NormalizeSumFactors[-b]", "-b");
	}

	public void test0413() {
		check("Rubi`NormalizeSumFactors[b]", "b");
	}

	public void test0414() {
		check("Rubi`NormalizeSumFactors[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0415() {
		check("Rubi`NormalizeSumFactors[a + b]", "a + b");
	}

	public void test0416() {
		check("Rubi`NormalizeSumFactors[x]", "x");
	}

	public void test0417() {
		check("Rubi`NormalizeSumFactors[(a - b)*x]", "(a - b)*x");
	}

	public void test0418() {
		check("Rubi`NormalizeSumFactors[(a + b)*x]", "(a + b)*x");
	}

	public void test0419() {
		check("Rubi`NormalizeSumFactors[Cos[(a - b)*x]]", "Cos[(a - b)*x]");
	}

	public void test0420() {
		check("Rubi`NormalizeSumFactors[-Cos[(a - b)*x]/(2*(a - b))]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0421() {
		check("Rubi`NormalizeSumFactors[Cos[(a - b)*x]/(2*(a - b))]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0422() {
		check("Rubi`NormalizeSumFactors[Cos[(a - b)*x]/(a - b)]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0423() {
		check("Rubi`NormalizeSumFactors[Cos[(a + b)*x]]", "Cos[(a + b)*x]");
	}

	public void test0424() {
		check("Rubi`NormalizeSumFactors[-Cos[(a + b)*x]/(2*(a + b))]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0425() {
		check("Rubi`NormalizeSumFactors[Cos[(a + b)*x]/(a + b)]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0426() {
		check("Rubi`NormalizeSumFactors[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0427() {
		check("Rubi`NormalizeTrigReduce[-Sin[a*x - b*x]/2, x]", "-Sin[(a - b)*x]/2");
	}

	public void test0428() {
		check("Rubi`NormalizeTrigReduce[Sin[a*x + b*x]/2, x]", "Sin[(a + b)*x]/2");
	}

	public void test0429() {
		check("Rubi`NumericFactor[-1]", "-1");
	}

	public void test0430() {
		check("Rubi`NumericFactor[-1/2]", "-1/2");
	}

	public void test0431() {
		check("Rubi`NumericFactor[1/2]", "1/2");
	}

	public void test0432() {
		check("Rubi`NumericFactor[a]", "1");
	}

	public void test0433() {
		check("Rubi`NumericFactor[-1/(2*(a - b))]", "-1/2");
	}

	public void test0434() {
		check("Rubi`NumericFactor[1/(2*(a - b))]", "1/2");
	}

	public void test0435() {
		check("Rubi`NumericFactor[(a - b)^(-1)]", "1");
	}

	public void test0436() {
		check("Rubi`NumericFactor[a - b]", "1");
	}

	public void test0437() {
		check("Rubi`NumericFactor[-b]", "-1");
	}

	public void test0438() {
		check("Rubi`NumericFactor[b]", "1");
	}

	public void test0439() {
		check("Rubi`NumericFactor[-1/(2*(a + b))]", "-1/2");
	}

	public void test0440() {
		check("Rubi`NumericFactor[(a + b)^(-1)]", "1");
	}

	public void test0441() {
		check("Rubi`NumericFactor[a + b]", "1");
	}

	public void test0442() {
		check("Rubi`PiecewiseLinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0443() {
		check("Rubi`PiecewiseLinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0444() {
		check("Rubi`PiecewiseLinearQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0445() {
		check("Rubi`PiecewiseLinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0446() {
		check("Rubi`PiecewiseLinearQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0447() {
		check("Rubi`PolyQ[(a - b)*x, x]", "True");
	}

	public void test0448() {
		check("Rubi`PolyQ[(a + b)*x, x]", "True");
	}

	public void test0449() {
		check("Rubi`PolyQ[a*x - b*x, x]", "True");
	}

	public void test0450() {
		check("Rubi`PolyQ[a*x + b*x, x]", "True");
	}

	public void test0451() {
		check("Rubi`PolyQ[Cos[a*x], x]", "False");
	}

	public void test0452() {
		check("Rubi`PolyQ[Sin[b*x], x]", "False");
	}

	public void test0453() {
		check("Rubi`PolyQ[a*x, x, 1]", "True");
	}

	public void test0454() {
		check("Rubi`PolyQ[(a - b)*x, x, 1]", "True");
	}

	public void test0455() {
		check("Rubi`PolyQ[b*x, x, 1]", "True");
	}

	public void test0456() {
		check("Rubi`PolyQ[(a + b)*x, x, 1]", "True");
	}

	public void test0457() {
		check("Rubi`PolyQ[Cos[a*x], x, 1]", "False");
	}

	public void test0458() {
		check("Rubi`PolyQ[Cos[a*x], x, 2]", "False");
	}

	public void test0459() {
		check("Rubi`PolyQ[sin[(a - b)*x], x, 1]", "False");
	}

	public void test0460() {
		check("Rubi`PolyQ[sin[(a + b)*x], x, 1]", "False");
	}

	public void test0461() {
		check("Rubi`PolyQ[-Sin[(a - b)*x]/2, x, 1]", "False");
	}

	public void test0462() {
		check("Rubi`PolyQ[Sin[(a - b)*x], x, 1]", "False");
	}

	public void test0463() {
		check("Rubi`PolyQ[Sin[b*x], x, 1]", "False");
	}

	public void test0464() {
		check("Rubi`PolyQ[Sin[b*x], x, 2]", "False");
	}

	public void test0465() {
		check("Rubi`PolyQ[Cos[a*x]*Sin[b*x], x, 1]", "False");
	}

	public void test0466() {
		check("Rubi`PolyQ[Sin[(a + b)*x]/2, x, 1]", "False");
	}

	public void test0467() {
		check("Rubi`PolyQ[Sin[(a + b)*x], x, 1]", "False");
	}

	public void test0468() {
		check("Rubi`PowerQ[-1/2]", "False");
	}

	public void test0469() {
		check("Rubi`PowerQ[0]", "False");
	}

	public void test0470() {
		check("Rubi`PowerQ[1/2]", "False");
	}

	public void test0471() {
		check("Rubi`PowerQ[1]", "False");
	}

	public void test0472() {
		check("Rubi`PowerQ[a]", "False");
	}

	public void test0473() {
		check("Rubi`PowerQ[-1/(2*(a - b))]", "False");
	}

	public void test0474() {
		check("Rubi`PowerQ[1/(2*(a - b))]", "False");
	}

	public void test0475() {
		check("Rubi`PowerQ[(a - b)^(-1)]", "True");
	}

	public void test0476() {
		check("Rubi`PowerQ[a - b]", "False");
	}

	public void test0477() {
		check("Rubi`PowerQ[-b]", "False");
	}

	public void test0478() {
		check("Rubi`PowerQ[b]", "False");
	}

	public void test0479() {
		check("Rubi`PowerQ[-1/(2*(a + b))]", "False");
	}

	public void test0480() {
		check("Rubi`PowerQ[(a + b)^(-1)]", "True");
	}

	public void test0481() {
		check("Rubi`PowerQ[a + b]", "False");
	}

	public void test0482() {
		check("Rubi`PowerQ[x]", "False");
	}

	public void test0483() {
		check("Rubi`PowerQ[a*x]", "False");
	}

	public void test0484() {
		check("Rubi`PowerQ[(a - b)*x]", "False");
	}

	public void test0485() {
		check("Rubi`PowerQ[(a + b)*x]", "False");
	}

	public void test0486() {
		check("Rubi`PowerQ[x^2]", "True");
	}

	public void test0487() {
		check("Rubi`PowerQ[x^2/(a - b)]", "False");
	}

	public void test0488() {
		check("Rubi`PowerQ[(a - b)*x^2]", "False");
	}

	public void test0489() {
		check("Rubi`PowerQ[x^2/(a + b)]", "False");
	}

	public void test0490() {
		check("Rubi`PowerQ[(a + b)*x^2]", "False");
	}

	public void test0491() {
		check("Rubi`PowerQ[Cos[a*x]]", "False");
	}

	public void test0492() {
		check("Rubi`PowerQ[-Cos[(a - b)*x]/2]", "False");
	}

	public void test0493() {
		check("Rubi`PowerQ[Cos[(a - b)*x]/2]", "False");
	}

	public void test0494() {
		check("Rubi`PowerQ[Cos[(a - b)*x]]", "False");
	}

	public void test0495() {
		check("Rubi`PowerQ[-Cos[(a + b)*x]/2]", "False");
	}

	public void test0496() {
		check("Rubi`PowerQ[Cos[(a + b)*x]]", "False");
	}

	public void test0497() {
		check("Rubi`PowerQ[sin[(a - b)*x]]", "False");
	}

	public void test0498() {
		check("Rubi`PowerQ[sin[(a + b)*x]]", "False");
	}

	public void test0499() {
		check("Rubi`PowerQ[Sin[(a - b)*x]]", "False");
	}

	public void test0500() {
		check("Rubi`PowerQ[Sin[b*x]]", "False");
	}

	public void test0501() {
		check("Rubi`PowerQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0502() {
		check("Rubi`PowerQ[Sin[(a + b)*x]]", "False");
	}

	public void test0503() {
		check("Rubi`ProductQ[-1/2]", "False");
	}

	public void test0504() {
		check("Rubi`ProductQ[0]", "False");
	}

	public void test0505() {
		check("Rubi`ProductQ[1/2]", "False");
	}

	public void test0506() {
		check("Rubi`ProductQ[1]", "False");
	}

	public void test0507() {
		check("Rubi`ProductQ[a]", "False");
	}

	public void test0508() {
		check("Rubi`ProductQ[-1/(2*(a - b))]", "True");
	}

	public void test0509() {
		check("Rubi`ProductQ[1/(2*(a - b))]", "True");
	}

	public void test0510() {
		check("Rubi`ProductQ[(a - b)^(-1)]", "False");
	}

	public void test0511() {
		check("Rubi`ProductQ[a - b]", "False");
	}

	public void test0512() {
		check("Rubi`ProductQ[-b]", "True");
	}

	public void test0513() {
		check("Rubi`ProductQ[b]", "False");
	}

	public void test0514() {
		check("Rubi`ProductQ[-1/(2*(a + b))]", "True");
	}

	public void test0515() {
		check("Rubi`ProductQ[(a + b)^(-1)]", "False");
	}

	public void test0516() {
		check("Rubi`ProductQ[a + b]", "False");
	}

	public void test0517() {
		check("Rubi`ProductQ[x]", "False");
	}

	public void test0518() {
		check("Rubi`ProductQ[a*x]", "True");
	}

	public void test0519() {
		check("Rubi`ProductQ[(a - b)*x]", "True");
	}

	public void test0520() {
		check("Rubi`ProductQ[b*x]", "True");
	}

	public void test0521() {
		check("Rubi`ProductQ[(a + b)*x]", "True");
	}

	public void test0522() {
		check("Rubi`ProductQ[x^2/(a - b)]", "True");
	}

	public void test0523() {
		check("Rubi`ProductQ[(a - b)*x^2]", "True");
	}

	public void test0524() {
		check("Rubi`ProductQ[x^2/(a + b)]", "True");
	}

	public void test0525() {
		check("Rubi`ProductQ[(a + b)*x^2]", "True");
	}

	public void test0526() {
		check("Rubi`ProductQ[Cos[a*x]]", "False");
	}

	public void test0527() {
		check("Rubi`ProductQ[-Cos[(a - b)*x]/2]", "True");
	}

	public void test0528() {
		check("Rubi`ProductQ[Cos[(a - b)*x]/2]", "True");
	}

	public void test0529() {
		check("Rubi`ProductQ[Cos[(a - b)*x]]", "False");
	}

	public void test0530() {
		check("Rubi`ProductQ[-Cos[(a - b)*x]/(2*(a - b))]", "True");
	}

	public void test0531() {
		check("Rubi`ProductQ[Cos[(a - b)*x]/(2*(a - b))]", "True");
	}

	public void test0532() {
		check("Rubi`ProductQ[Cos[(a - b)*x]/(a - b)]", "True");
	}

	public void test0533() {
		check("Rubi`ProductQ[Cos[b*x]]", "False");
	}

	public void test0534() {
		check("Rubi`ProductQ[-Cos[(a + b)*x]/2]", "True");
	}

	public void test0535() {
		check("Rubi`ProductQ[Cos[(a + b)*x]]", "False");
	}

	public void test0536() {
		check("Rubi`ProductQ[-Cos[(a + b)*x]/(2*(a + b))]", "True");
	}

	public void test0537() {
		check("Rubi`ProductQ[Cos[(a + b)*x]/(a + b)]", "True");
	}

	public void test0538() {
		check("Rubi`ProductQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0539() {
		check("Rubi`ProductQ[Sin[a*x]]", "False");
	}

	public void test0540() {
		check("Rubi`ProductQ[-Sin[(a - b)*x]/2]", "True");
	}

	public void test0541() {
		check("Rubi`ProductQ[Sin[b*x]]", "False");
	}

	public void test0542() {
		check("Rubi`ProductQ[Sin[(a + b)*x]/2]", "True");
	}

	public void test0543() {
		check("Rubi`PseudoBinomialPairQ[Cos[a*x], Sin[b*x], x]", "False");
	}

	public void test0544() {
		check("Rubi`PseudoBinomialPairQ[Sin[b*x], Cos[a*x], x]", "False");
	}

	public void test0545() {
		check("Rubi`PseudoBinomialParts[Cos[a*x], x]", "False");
	}

	public void test0546() {
		check("Rubi`PseudoBinomialParts[Sin[b*x], x]", "False");
	}

	public void test0547() {
		check("Rubi`PureFunctionOfCosQ[a, b*x, x]", "True");
	}

	public void test0548() {
		check("Rubi`PureFunctionOfCosQ[x, b*x, x]", "False");
	}

	public void test0549() {
		check("Rubi`PureFunctionOfCosQ[a*x, b*x, x]", "False");
	}

	public void test0550() {
		check("Rubi`PureFunctionOfCosQ[Cos[a*x], b*x, x]", "False");
	}

	public void test0551() {
		check("Rubi`PureFunctionOfCosQ[Cos[a*x], Cos[b*x][[1]], x]", "False");
	}

	public void test0552() {
		check("Rubi`PureFunctionOfSinQ[b, a*x, x]", "True");
	}

	public void test0553() {
		check("Rubi`PureFunctionOfSinQ[x, a*x, x]", "False");
	}

	public void test0554() {
		check("Rubi`PureFunctionOfSinQ[b*x, a*x, x]", "False");
	}

	public void test0555() {
		check("Rubi`PureFunctionOfSinQ[Sin[b*x], a*x, x]", "False");
	}

	public void test0556() {
		check("Rubi`PureFunctionOfSinQ[Sin[b*x], Sin[a*x][[1]], x]", "False");
	}

	public void test0557() {
		check("Rubi`QuadraticQ[Cos[a*x], x]", "False");
	}

	public void test0558() {
		check("Rubi`QuadraticQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0559() {
		check("Rubi`QuadraticQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0560() {
		check("Rubi`QuadraticQ[Sin[b*x], x]", "False");
	}

	public void test0561() {
		check("Rubi`RationalQ[-1]", "True");
	}

	public void test0562() {
		check("Rubi`RationalQ[-1/2]", "True");
	}

	public void test0563() {
		check("Rubi`RationalQ[1/2]", "True");
	}

	public void test0564() {
		check("Rubi`RationalQ[1]", "True");
	}

	public void test0565() {
		check("Rubi`RationalQ[2]", "True");
	}

	public void test0566() {
		check("Rubi`RationalQ[a]", "False");
	}

	public void test0567() {
		check("Rubi`RationalQ[(a - b)^(-1)]", "False");
	}

	public void test0568() {
		check("Rubi`RationalQ[a - b]", "False");
	}

	public void test0569() {
		check("Rubi`RationalQ[a/b]", "False");
	}

	public void test0570() {
		check("Rubi`RationalQ[-b]", "False");
	}

	public void test0571() {
		check("Rubi`RationalQ[b]", "False");
	}

	public void test0572() {
		check("Rubi`RationalQ[(a + b)^(-1)]", "False");
	}

	public void test0573() {
		check("Rubi`RationalQ[a + b]", "False");
	}

	public void test0574() {
		check("Rubi`RationalQ[x]", "False");
	}

	public void test0575() {
		check("Rubi`RationalQ[(a - b)*x]", "False");
	}

	public void test0576() {
		check("Rubi`RationalQ[(a + b)*x]", "False");
	}

	public void test0577() {
		check("Rubi`RationalQ[x^2]", "False");
	}

	public void test0578() {
		check("Rubi`RationalQ[Cos[(a - b)*x]]", "False");
	}

	public void test0579() {
		check("Rubi`RationalQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0580() {
		check("Rubi`RationalQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0581() {
		check("Rubi`RationalQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0582() {
		check("Rubi`RationalQ[Cos[(a + b)*x]]", "False");
	}

	public void test0583() {
		check("Rubi`RationalQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0584() {
		check("Rubi`RationalQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0585() {
		check("Rubi`ReduceInertTrig[sin, (a - b)*x]", "sin[(a - b)*x]");
	}

	public void test0586() {
		check("Rubi`ReduceInertTrig[sin, (a + b)*x]", "sin[(a + b)*x]");
	}

	public void test0587() {
		check("Rubi`RemainingFactors[a]", "1");
	}

	public void test0588() {
		check("Rubi`RemainingFactors[b]", "1");
	}

	public void test0589() {
		check("Rubi`RemainingFactors[x]", "1");
	}

	public void test0590() {
		check("Rubi`RemainingFactors[a*x]", "x");
	}

	public void test0591() {
		check("Rubi`RemainingFactors[b*x]", "x");
	}

	public void test0592() {
		check("Rubi`SignOfFactor[-1]", "{-1, 1}");
	}

	public void test0593() {
		check("Rubi`SignOfFactor[-1/2]", "{-1, 1/2}");
	}

	public void test0594() {
		check("Rubi`SignOfFactor[1/2]", "{1, 1/2}");
	}

	public void test0595() {
		check("Rubi`SignOfFactor[(a - b)^(-1)]", "{1, (a - b)^(-1)}");
	}

	public void test0596() {
		check("Rubi`SignOfFactor[a - b]", "{1, a - b}");
	}

	public void test0597() {
		check("Rubi`SignOfFactor[-b]", "{-1, b}");
	}

	public void test0598() {
		check("Rubi`SignOfFactor[b]", "{1, b}");
	}

	public void test0599() {
		check("Rubi`SignOfFactor[(a + b)^(-1)]", "{1, (a + b)^(-1)}");
	}

	public void test0600() {
		check("Rubi`SignOfFactor[a + b]", "{1, a + b}");
	}

	public void test0601() {
		check("Rubi`SignOfFactor[x]", "{1, x}");
	}

	public void test0602() {
		check("Rubi`SignOfFactor[(a - b)*x]", "{1, (a - b)*x}");
	}

	public void test0603() {
		check("Rubi`SignOfFactor[(a + b)*x]", "{1, (a + b)*x}");
	}

	public void test0604() {
		check("Rubi`SignOfFactor[Cos[(a - b)*x]]", "{1, Cos[(a - b)*x]}");
	}

	public void test0605() {
		check("Rubi`SignOfFactor[-Cos[(a - b)*x]/(2*(a - b))]", "{-1, Cos[(a - b)*x]/(2*(a - b))}");
	}

	public void test0606() {
		check("Rubi`SignOfFactor[Cos[(a - b)*x]/(2*(a - b))]", "{1, Cos[(a - b)*x]/(2*(a - b))}");
	}

	public void test0607() {
		check("Rubi`SignOfFactor[Cos[(a - b)*x]/(a - b)]", "{1, Cos[(a - b)*x]/(a - b)}");
	}

	public void test0608() {
		check("Rubi`SignOfFactor[Cos[(a + b)*x]]", "{1, Cos[(a + b)*x]}");
	}

	public void test0609() {
		check("Rubi`SignOfFactor[-Cos[(a + b)*x]/(2*(a + b))]", "{-1, Cos[(a + b)*x]/(2*(a + b))}");
	}

	public void test0610() {
		check("Rubi`SignOfFactor[Cos[(a + b)*x]/(a + b)]", "{1, Cos[(a + b)*x]/(a + b)}");
	}

	public void test0611() {
		check("Rubi`Simp[0, x]", "0");
	}

	public void test0612() {
		check("Rubi`Simp[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0613() {
		check("Rubi`Simp[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0614() {
		check("Rubi`Simp[-Cos[(a - b)*x]/(2*(a - b)), x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0615() {
		check("Rubi`Simp[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0616() {
		check("Rubi`Simp[-(Cos[(a - b)*x]/(a - b))/2, x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0617() {
		check("Rubi`Simp[-Cos[(a + b)*x]/(2*(a + b)), x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0618() {
		check("Rubi`Simp[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0619() {
		check("Rubi`Simp[-(Cos[(a + b)*x]/(a + b))/2, x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0620() {
		check("Rubi`Simp[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0621() {
		check("Rubi`SimpFixFactor[-1/2, x]", "-1/2");
	}

	public void test0622() {
		check("Rubi`SimpFixFactor[1/2, x]", "1/2");
	}

	public void test0623() {
		check("Rubi`SimpFixFactor[(a - b)^(-1), x]", "(a - b)^(-1)");
	}

	public void test0624() {
		check("Rubi`SimpFixFactor[a - b, x]", "a - b");
	}

	public void test0625() {
		check("Rubi`SimpFixFactor[(a + b)^(-1), x]", "(a + b)^(-1)");
	}

	public void test0626() {
		check("Rubi`SimpFixFactor[a + b, x]", "a + b");
	}

	public void test0627() {
		check("Rubi`SimpFixFactor[x, x]", "x");
	}

	public void test0628() {
		check("Rubi`SimpFixFactor[Cos[(a - b)*x], x]", "Cos[(a - b)*x]");
	}

	public void test0629() {
		check("Rubi`SimpFixFactor[Cos[(a + b)*x], x]", "Cos[(a + b)*x]");
	}

	public void test0630() {
		check("Rubi`SimpHelp[0, x]", "0");
	}

	public void test0631() {
		check("Rubi`SimpHelp[x, x]", "x");
	}

	public void test0632() {
		check("Rubi`SimpHelp[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0633() {
		check("Rubi`SimpHelp[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0634() {
		check("Rubi`SimpHelp[Cos[(a - b)*x], x]", "Cos[(a - b)*x]");
	}

	public void test0635() {
		check("Rubi`SimpHelp[-Cos[(a - b)*x]/(2*(a - b)), x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0636() {
		check("Rubi`SimpHelp[Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0637() {
		check("Rubi`SimpHelp[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0638() {
		check("Rubi`SimpHelp[Cos[(a + b)*x], x]", "Cos[(a + b)*x]");
	}

	public void test0639() {
		check("Rubi`SimpHelp[-Cos[(a + b)*x]/(2*(a + b)), x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0640() {
		check("Rubi`SimpHelp[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0641() {
		check("Rubi`SimpHelp[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0642() {
		check("Rubi`SmartSimplify[0]", "0");
	}

	public void test0643() {
		check("Rubi`SmartSimplify[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0644() {
		check("Rubi`SmartSimplify[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0645() {
		check("Rubi`SmartSimplify[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0646() {
		check("Rubi`SmartSimplify[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0647() {
		check("Rubi`StopFunctionQ[(a - b)^(-1)]", "False");
	}

	public void test0648() {
		check("Rubi`StopFunctionQ[a - b]", "False");
	}

	public void test0649() {
		check("Rubi`StopFunctionQ[-b]", "False");
	}

	public void test0650() {
		check("Rubi`StopFunctionQ[(a + b)^(-1)]", "False");
	}

	public void test0651() {
		check("Rubi`StopFunctionQ[a + b]", "False");
	}

	public void test0652() {
		check("Rubi`StopFunctionQ[(a - b)*x]", "False");
	}

	public void test0653() {
		check("Rubi`StopFunctionQ[(a + b)*x]", "False");
	}

	public void test0654() {
		check("Rubi`StopFunctionQ[Cos[(a - b)*x]]", "False");
	}

	public void test0655() {
		check("Rubi`StopFunctionQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0656() {
		check("Rubi`StopFunctionQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0657() {
		check("Rubi`StopFunctionQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0658() {
		check("Rubi`StopFunctionQ[Cos[(a + b)*x]]", "False");
	}

	public void test0659() {
		check("Rubi`StopFunctionQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0660() {
		check("Rubi`StopFunctionQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0661() {
		check("Rubi`StopFunctionQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0662() {
		check("Rubi`SumQ[-1/2]", "False");
	}

	public void test0663() {
		check("Rubi`SumQ[0]", "False");
	}

	public void test0664() {
		check("Rubi`SumQ[1/2]", "False");
	}

	public void test0665() {
		check("Rubi`SumQ[a]", "False");
	}

	public void test0666() {
		check("Rubi`SumQ[(a - b)^(-1)]", "False");
	}

	public void test0667() {
		check("Rubi`SumQ[a - b]", "True");
	}

	public void test0668() {
		check("Rubi`SumQ[-b]", "False");
	}

	public void test0669() {
		check("Rubi`SumQ[b]", "False");
	}

	public void test0670() {
		check("Rubi`SumQ[(a + b)^(-1)]", "False");
	}

	public void test0671() {
		check("Rubi`SumQ[a + b]", "True");
	}

	public void test0672() {
		check("Rubi`SumQ[x]", "False");
	}

	public void test0673() {
		check("Rubi`SumQ[(a - b)*x]", "False");
	}

	public void test0674() {
		check("Rubi`SumQ[(a + b)*x]", "False");
	}

	public void test0675() {
		check("Rubi`SumQ[Cos[a*x]]", "False");
	}

	public void test0676() {
		check("Rubi`SumQ[Cos[(a - b)*x]]", "False");
	}

	public void test0677() {
		check("Rubi`SumQ[-(Cos[(a - b)*x]/(a - b))]", "False");
	}

	public void test0678() {
		check("Rubi`SumQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0679() {
		check("Rubi`SumQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0680() {
		check("Rubi`SumQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0681() {
		check("Rubi`SumQ[Cos[(a + b)*x]]", "False");
	}

	public void test0682() {
		check("Rubi`SumQ[-(Cos[(a + b)*x]/(a + b))]", "False");
	}

	public void test0683() {
		check("Rubi`SumQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0684() {
		check("Rubi`SumQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0685() {
		check("Rubi`SumQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "True");
	}

	public void test0686() {
		check("Rubi`SumQ[sin[(a - b)*x]]", "False");
	}

	public void test0687() {
		check("Rubi`SumQ[sin[(a + b)*x]]", "False");
	}

	public void test0688() {
		check("Rubi`SumQ[-Sin[(a - b)*x]/2]", "False");
	}

	public void test0689() {
		check("Rubi`SumQ[Sin[(a - b)*x]]", "False");
	}

	public void test0690() {
		check("Rubi`SumQ[Sin[b*x]]", "False");
	}

	public void test0691() {
		check("Rubi`SumQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0692() {
		check("Rubi`SumQ[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2]", "True");
	}

	public void test0693() {
		check("Rubi`SumQ[Sin[(a + b)*x]/2]", "False");
	}

	public void test0694() {
		check("Rubi`SumQ[Sin[(a + b)*x]]", "False");
	}

	public void test0695() {
		check("Rubi`SumQ[-Sin[a*x - b*x]/2 + Sin[a*x + b*x]/2]", "True");
	}

	public void test0696() {
		check("Rubi`TrigQ[a - b]", "False");
	}

	public void test0697() {
		check("Rubi`TrigQ[-b]", "False");
	}

	public void test0698() {
		check("Rubi`TrigQ[a + b]", "False");
	}

	public void test0699() {
		check("Rubi`TrigQ[a*x]", "False");
	}

	public void test0700() {
		check("Rubi`TrigQ[(a - b)*x]", "False");
	}

	public void test0701() {
		check("Rubi`TrigQ[b*x]", "False");
	}

	public void test0702() {
		check("Rubi`TrigQ[(a + b)*x]", "False");
	}

	public void test0703() {
		check("Rubi`TrigQ[Cos[a*x]]", "True");
	}

	public void test0704() {
		check("Rubi`TrigQ[Cos[(a - b)*x]]", "True");
	}

	public void test0705() {
		check("Rubi`TrigQ[Cos[(a + b)*x]]", "True");
	}

	public void test0706() {
		check("Rubi`TrigQ[sin[(a - b)*x]]", "False");
	}

	public void test0707() {
		check("Rubi`TrigQ[sin[(a + b)*x]]", "False");
	}

	public void test0708() {
		check("Rubi`TrigQ[Sin[(a - b)*x]]", "True");
	}

	public void test0709() {
		check("Rubi`TrigQ[Sin[b*x]]", "True");
	}

	public void test0710() {
		check("Rubi`TrigQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0711() {
		check("Rubi`TrigQ[Sin[(a + b)*x]]", "True");
	}

	public void test0712() {
		check("Rubi`TrigSimplify[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0713() {
		check("Rubi`TrigSimplifyAux[a*x]", "a*x");
	}

	public void test0714() {
		check("Rubi`TrigSimplifyAux[b*x]", "b*x");
	}

	public void test0715() {
		check("Rubi`TrigSimplifyAux[Cos[a*x]]", "Cos[a*x]");
	}

	public void test0716() {
		check("Rubi`TrigSimplifyAux[Sin[b*x]]", "Sin[b*x]");
	}

	public void test0717() {
		check("Rubi`TrigSimplifyAux[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0718() {
		check("Rubi`TrigSimplifyQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0719() {
		check("Rubi`TrigSimplifyRecur[a]", "a");
	}

	public void test0720() {
		check("Rubi`TrigSimplifyRecur[b]", "b");
	}

	public void test0721() {
		check("Rubi`TrigSimplifyRecur[x]", "x");
	}

	public void test0722() {
		check("Rubi`TrigSimplifyRecur[a*x]", "a*x");
	}

	public void test0723() {
		check("Rubi`TrigSimplifyRecur[b*x]", "b*x");
	}

	public void test0724() {
		check("Rubi`TrigSimplifyRecur[Cos[a*x]]", "Cos[a*x]");
	}

	public void test0725() {
		check("Rubi`TrigSimplifyRecur[Sin[b*x]]", "Sin[b*x]");
	}

	public void test0726() {
		check("Rubi`TrigSimplifyRecur[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0727() {
		check("Rubi`UnifyInertTrigFunction[sin[(a - b)*x], x]", "sin[(a - b)*x]");
	}

	public void test0728() {
		check("Rubi`UnifyInertTrigFunction[sin[(a + b)*x], x]", "sin[(a + b)*x]");
	}
	
	public void test0800() {
		check("Integrate[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2,x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Rubi`ExpandTrigReduce[Cos[a*x]^1*Sin[b*x]^1,x]", //
				"-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}
	
	public void test0801() {
		// 3 times the same test => test if Module() variables are deleted correctly
		check("Integrate[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2,x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Integrate[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2,x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Integrate[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2,x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
	}

	public void test0802() {
		// Integrate(Cos(a*x)*Sin(b*x),x)
		check("Integrate[Cos[a*x]* Sin[b*x],x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
	}
	
}