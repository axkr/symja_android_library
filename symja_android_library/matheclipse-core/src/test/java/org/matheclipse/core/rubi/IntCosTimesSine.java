package org.matheclipse.core.rubi;

//Int[Cos[a*x]*Sin[b*x],x,x]
public class IntCosTimesSine extends AbstractRubiTestCase {

public IntCosTimesSine(String name) { super(name, false); }

	public void test0001() {
		check("Integrate::AbsurdNumberFactors[-1]", "-1");
	}

	public void test0002() {
		check("Integrate::AbsurdNumberFactors[a]", "1");
	}

	public void test0003() {
		check("Integrate::AbsurdNumberFactors[-b]", "-1");
	}

	public void test0004() {
		check("Integrate::AbsurdNumberFactors[b]", "1");
	}

	public void test0005() {
		check("Integrate::AbsurdNumberGCD[-1]", "-1");
	}

	public void test0006() {
		check("Integrate::AbsurdNumberGCD[1]", "1");
	}

	public void test0007() {
		check("Integrate::AbsurdNumberGCD[1, -1]", "1");
	}

	public void test0008() {
		check("Integrate::AbsurdNumberGCD[1, 1]", "1");
	}

	public void test0009() {
		check("Integrate::AbsurdNumberGCDList[{}, {}]", "1");
	}

	public void test0010() {
		check("Integrate::AbsurdNumberGCDList[{{1, 1}}, {}]", "1");
	}

	public void test0011() {
		check("Integrate::AbsurdNumberGCDList[{{1, 1}}, {{-1, 1}}]", "1");
	}

	public void test0012() {
		check("Integrate::AbsurdNumberGCDList[{{1, 1}}, {{1, 1}}]", "1");
	}

	public void test0013() {
		check("Integrate::AbsurdNumberGCDList[{{1, 1}}, Rest[{{-1, 1}}]]", "1");
	}

	public void test0014() {
		check("Integrate::AbsurdNumberQ[-1]", "True");
	}

	public void test0015() {
		check("Integrate::AbsurdNumberQ[a]", "False");
	}

	public void test0016() {
		check("Integrate::AbsurdNumberQ[-b]", "False");
	}

	public void test0017() {
		check("Integrate::AbsurdNumberQ[b]", "False");
	}

	public void test0018() {
		check("Integrate::ActivateTrig[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0019() {
		check("Integrate::AlgebraicTrigFunctionQ[Sin[(a - b)*x], x]", "True");
	}

	public void test0020() {
		check("Integrate::AlgebraicTrigFunctionQ[Sin[(a + b)*x], x]", "True");
	}

	public void test0021() {
		check("Integrate::BinomialParts[Cos[a*x], x]", "False");
	}

	public void test0022() {
		check("Integrate::BinomialParts[Sin[b*x], x]", "False");
	}

	public void test0023() {
		check("Integrate::BinomialQ[Cos[a*x], x]", "False");
	}

	public void test0024() {
		check("Integrate::BinomialQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0025() {
		check("Integrate::BinomialQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0026() {
		check("Integrate::BinomialQ[Sin[b*x], x]", "False");
	}

	public void test0027() {
		check("Integrate::CalculusQ[a - b]", "False");
	}

	public void test0028() {
		check("Integrate::CalculusQ[-b]", "False");
	}

	public void test0029() {
		check("Integrate::CalculusQ[a + b]", "False");
	}

	public void test0030() {
		check("Integrate::CalculusQ[a*x]", "False");
	}

	public void test0031() {
		check("Integrate::CalculusQ[(a - b)*x]", "False");
	}

	public void test0032() {
		check("Integrate::CalculusQ[b*x]", "False");
	}

	public void test0033() {
		check("Integrate::CalculusQ[(a + b)*x]", "False");
	}

	public void test0034() {
		check("Integrate::CalculusQ[Cos[a*x]]", "False");
	}

	public void test0035() {
		check("Integrate::CalculusQ[sin[(a - b)*x]]", "False");
	}

	public void test0036() {
		check("Integrate::CalculusQ[x*sin[(a - b)*x]]", "False");
	}

	public void test0037() {
		check("Integrate::CalculusQ[sin[(a + b)*x]]", "False");
	}

	public void test0038() {
		check("Integrate::CalculusQ[x*sin[(a + b)*x]]", "False");
	}

	public void test0039() {
		check("Integrate::CalculusQ[Sin[(a - b)*x]]", "False");
	}

	public void test0040() {
		check("Integrate::CalculusQ[x*Sin[(a - b)*x]]", "False");
	}

	public void test0041() {
		check("Integrate::CalculusQ[Sin[b*x]]", "False");
	}

	public void test0042() {
		check("Integrate::CalculusQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0043() {
		check("Integrate::CalculusQ[x*Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0044() {
		check("Integrate::CalculusQ[Sin[(a + b)*x]]", "False");
	}

	public void test0045() {
		check("Integrate::CalculusQ[x*Sin[(a + b)*x]]", "False");
	}

	public void test0046() {
		check("Integrate::Coeff[(a - b)*x, x, 1]", "a - b");
	}

	public void test0047() {
		check("Integrate::Coeff[(a + b)*x, x, 1]", "a + b");
	}

	public void test0048() {
		check("Integrate::Coeff[a*x - b*x, x, 1]", "a - b");
	}

	public void test0049() {
		check("Integrate::Coeff[a*x + b*x, x, 1]", "a + b");
	}

	public void test0050() {
		check("Integrate::CommonFactors[{a, -b}]", "{1, a, -b}");
	}

	public void test0051() {
		check("Integrate::CommonFactors[{a, b}]", "{1, a, b}");
	}

	public void test0052() {
		check("Integrate::ComplexNumberQ[1]", "False");
	}

	public void test0053() {
		check("Integrate::ComplexNumberQ[a]", "False");
	}

	public void test0054() {
		check("Integrate::ComplexNumberQ[b]", "False");
	}

	public void test0055() {
		check("Integrate::ComplexNumberQ[x]", "False");
	}

	public void test0056() {
		check("Integrate::ContentFactor[a - b]", "a - b");
	}

	public void test0057() {
		check("Integrate::ContentFactor[a + b]", "a + b");
	}

	public void test0058() {
		check("Integrate::ContentFactorAux[a - b]", "a - b");
	}

	public void test0059() {
		check("Integrate::ContentFactorAux[a + b]", "a + b");
	}

	public void test0060() {
		check("Integrate::DeactivateTrig[Sin[(a - b)*x], x]", "§sin[(a-b)*x]");
	}

	public void test0061() {
		check("Integrate::DeactivateTrig[Sin[(a + b)*x], x]", "§sin[(a+b)*x]");
	}

	public void test0062() {
		check("Integrate::DeactivateTrigAux[Sin[(a - b)*x], x]", "§sin[(a-b)*x]");
	}

	public void test0063() {
		check("Integrate::DeactivateTrigAux[Sin[(a + b)*x], x]", "§sin[(a+b)*x]");
	}

	public void test0064() {
		check("Integrate::EqQ[-1/2, 1/2]", "False");
	}

	public void test0065() {
		check("Integrate::EqQ[0, 0]", "True");
	}

	public void test0066() {
		check("Integrate::EqQ[1/2, 1/2]", "True");
	}

	public void test0067() {
		check("Integrate::EqQ[1, 1]", "True");
	}

	public void test0068() {
		check("Integrate::EqQ[(a - b)^(-1), 1/2]", "False");
	}

	public void test0069() {
		check("Integrate::EqQ[a - b, 1/2]", "False");
	}

	public void test0070() {
		check("Integrate::EqQ[1 + b, 0]", "False");
	}

	public void test0071() {
		check("Integrate::EqQ[(a + b)^(-1), 1/2]", "False");
	}

	public void test0072() {
		check("Integrate::EqQ[a + b, 0]", "False");
	}

	public void test0073() {
		check("Integrate::EqQ[a + b, 1/2]", "False");
	}

	public void test0074() {
		check("Integrate::EqQ[Cos, cos]", "False");
	}

	public void test0075() {
		check("Integrate::EqQ[Cos, Cos]", "True");
	}

	public void test0076() {
		check("Integrate::EqQ[Cos, cot]", "False");
	}

	public void test0077() {
		check("Integrate::EqQ[Cos, Cot]", "False");
	}

	public void test0078() {
		check("Integrate::EqQ[Cos, sin]", "False");
	}

	public void test0079() {
		check("Integrate::EqQ[Cos, Sin]", "False");
	}

	public void test0080() {
		check("Integrate::EqQ[Cos, tan]", "False");
	}

	public void test0081() {
		check("Integrate::EqQ[Cos, Tan]", "False");
	}

	public void test0082() {
		check("Integrate::EqQ[Sin, cos]", "False");
	}

	public void test0083() {
		check("Integrate::EqQ[Sin, Cos]", "False");
	}

	public void test0084() {
		check("Integrate::EqQ[Sin, cot]", "False");
	}

	public void test0085() {
		check("Integrate::EqQ[Sin, Cot]", "False");
	}

	public void test0086() {
		check("Integrate::EqQ[Sin, sin]", "False");
	}

	public void test0087() {
		check("Integrate::EqQ[Sin, Sin]", "True");
	}

	public void test0088() {
		check("Integrate::EqQ[Sin, tan]", "False");
	}

	public void test0089() {
		check("Integrate::EqQ[Sin, Tan]", "False");
	}

	public void test0090() {
		check("Integrate::EqQ[a*x, b*x]", "False");
	}

	public void test0091() {
		check("Integrate::EqQ[b*x, a*x]", "False");
	}

	public void test0092() {
		check("Integrate::EqQ[(a - b)^(-1) + x, 0]", "False");
	}

	public void test0093() {
		check("Integrate::EqQ[a - b + x, 0]", "False");
	}

	public void test0094() {
		check("Integrate::EqQ[a + b + x, 0]", "False");
	}

	public void test0095() {
		check("Integrate::EqQ[(a + b)^(-1) + x, 0]", "False");
	}

	public void test0096() {
		check("Integrate::EqQ[a - b + x^2, 0]", "False");
	}

	public void test0097() {
		check("Integrate::EqQ[a + b + x^2, 0]", "False");
	}

	public void test0098() {
		check("Integrate::EveryQ[#1 === 1 & , {1, 1}]", "True");
	}

	public void test0099() {
		check("Integrate::EveryQ[#1 === 1 & , {1, b}]", "False");
	}

	public void test0100() {
		check("Integrate::ExpandToSum[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0101() {
		check("Integrate::ExpandToSum[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0102() {
		check("Integrate::ExpandToSum[a*x - b*x, x]", "(a - b)*x");
	}

	public void test0103() {
		check("Integrate::ExpandToSum[a*x + b*x, x]", "(a + b)*x");
	}

	public void test0104() {
		check("Integrate::ExpandTrigReduce[Cos[a*x]*Sin[b*x], x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0105() {
		check("Integrate::ExpandTrigReduceAux[Cos[a*x]*Sin[b*x], x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0106() {
		check("Integrate::Expon[(a - b)*x, x, List]", "{1}");
	}

	public void test0107() {
		check("Integrate::Expon[(a + b)*x, x, List]", "{1}");
	}

	public void test0108() {
		check("Integrate::Expon[a*x - b*x, x, List]", "{1}");
	}

	public void test0109() {
		check("Integrate::Expon[a*x + b*x, x, List]", "{1}");
	}

	public void test0110() {
		check("Integrate::FactorAbsurdNumber[-1]", "{{-1, 1}}");
	}

	public void test0111() {
		check("Integrate::FactorAbsurdNumber[1]", "{{1, 1}}");
	}

	public void test0112() {
		check("Integrate::FactorNumericGcd[0]", "0");
	}

	public void test0113() {
		check("Integrate::FactorNumericGcd[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0114() {
		check("Integrate::FactorNumericGcd[a - b]", "a - b");
	}

	public void test0115() {
		check("Integrate::FactorNumericGcd[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0116() {
		check("Integrate::FactorNumericGcd[a + b]", "a + b");
	}

	public void test0117() {
		check("Integrate::FactorNumericGcd[x]", "x");
	}

	public void test0118() {
		check("Integrate::FactorNumericGcd[x^2]", "x^2");
	}

	public void test0119() {
		check("Integrate::FactorNumericGcd[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0120() {
		check("Integrate::FactorNumericGcd[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0121() {
		check("Integrate::FactorNumericGcd[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0122() {
		check("Integrate::FactorNumericGcd[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0123() {
		check("Integrate::FactorNumericGcd[Cos[(a - b)*x]]", "Cos[(a - b)*x]");
	}

	public void test0124() {
		check("Integrate::FactorNumericGcd[Cos[(a + b)*x]]", "Cos[(a + b)*x]");
	}

	public void test0125() {
		check("Integrate::FactorOrder[1, 1]", "0");
	}

	public void test0126() {
		check("Integrate::FactorOrder[a, 1]", "1");
	}

	public void test0127() {
		check("Integrate::FactorOrder[b, 1]", "1");
	}

	public void test0128() {
		check("Integrate::FactorOrder[b, a]", "-1");
	}

	public void test0129() {
		check("Integrate::FalseQ[False]", "True");
	}

	public void test0130() {
		check("Integrate::FalseQ[Null]", "False");
	}

	public void test0131() {
		check("Integrate::FalseQ[a*x]", "False");
	}

	public void test0132() {
		check("Integrate::FindTrigFactor[Cos, Sec, 1, a*x, False]", "False");
	}

	public void test0133() {
		check("Integrate::FindTrigFactor[Cos, Sec, x, a*x, False]", "False");
	}

	public void test0134() {
		check("Integrate::FindTrigFactor[Cos, Sec, b*x, a*x, False]", "False");
	}

	public void test0135() {
		check("Integrate::FindTrigFactor[Sin, Csc, 1, a*x, False]", "False");
	}

	public void test0136() {
		check("Integrate::FindTrigFactor[Sin, Csc, 1, b*x, False]", "False");
	}

	public void test0137() {
		check("Integrate::FindTrigFactor[Sin, Csc, x, a*x, False]", "False");
	}

	public void test0138() {
		check("Integrate::FindTrigFactor[Sin, Csc, x, b*x, False]", "False");
	}

	public void test0139() {
		check("Integrate::FindTrigFactor[Sin, Csc, a*x, b*x, False]", "False");
	}

	public void test0140() {
		check("Integrate::FindTrigFactor[Sin, Csc, b*x, a*x, False]", "False");
	}

	public void test0141() {
		check("Integrate::FindTrigFactor[Tan, Cot, 1, a*x, True]", "False");
	}

	public void test0142() {
		check("Integrate::FindTrigFactor[Tan, Cot, 1, b*x, True]", "False");
	}

	public void test0143() {
		check("Integrate::FindTrigFactor[Tan, Cot, x, a*x, True]", "False");
	}

	public void test0144() {
		check("Integrate::FindTrigFactor[Tan, Cot, x, b*x, True]", "False");
	}

	public void test0145() {
		check("Integrate::FindTrigFactor[Tan, Cot, a*x, b*x, True]", "False");
	}

	public void test0146() {
		check("Integrate::FindTrigFactor[Tan, Cot, b*x, a*x, True]", "False");
	}

	public void test0147() {
		check("Integrate::FixInertTrigFunction[sin[(a - b)*x], x]", "sin[(a - b)*x]");
	}

	public void test0148() {
		check("Integrate::FixInertTrigFunction[sin[(a + b)*x], x]", "sin[(a + b)*x]");
	}

	public void test0149() {
		check("Integrate::FixSimplify[0]", "0");
	}

	public void test0150() {
		check("Integrate::FixSimplify[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0151() {
		check("Integrate::FixSimplify[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0152() {
		check("Integrate::FixSimplify[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0153() {
		check("Integrate::FixSimplify[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0154() {
		check("Integrate::FractionalPowerOfSquareQ[-1]", "False");
	}

	public void test0155() {
		check("Integrate::FractionalPowerOfSquareQ[0]", "False");
	}

	public void test0156() {
		check("Integrate::FractionalPowerOfSquareQ[2]", "False");
	}

	public void test0157() {
		check("Integrate::FractionalPowerOfSquareQ[a]", "False");
	}

	public void test0158() {
		check("Integrate::FractionalPowerOfSquareQ[(a - b)^(-1)]", "False");
	}

	public void test0159() {
		check("Integrate::FractionalPowerOfSquareQ[a - b]", "False");
	}

	public void test0160() {
		check("Integrate::FractionalPowerOfSquareQ[-b]", "False");
	}

	public void test0161() {
		check("Integrate::FractionalPowerOfSquareQ[b]", "False");
	}

	public void test0162() {
		check("Integrate::FractionalPowerOfSquareQ[(a + b)^(-1)]", "False");
	}

	public void test0163() {
		check("Integrate::FractionalPowerOfSquareQ[a + b]", "False");
	}

	public void test0164() {
		check("Integrate::FractionalPowerOfSquareQ[x]", "False");
	}

	public void test0165() {
		check("Integrate::FractionalPowerOfSquareQ[x^2]", "False");
	}

	public void test0166() {
		check("Integrate::FractionalPowerOfSquareQ[x^2/(a - b)]", "False");
	}

	public void test0167() {
		check("Integrate::FractionalPowerOfSquareQ[(a - b)*x^2]", "False");
	}

	public void test0168() {
		check("Integrate::FractionalPowerOfSquareQ[x^2/(a + b)]", "False");
	}

	public void test0169() {
		check("Integrate::FractionalPowerOfSquareQ[(a + b)*x^2]", "False");
	}

	public void test0170() {
		check("Integrate::FractionQ[-1]", "False");
	}

	public void test0171() {
		check("Integrate::FractionQ[2]", "False");
	}

	public void test0172() {
		check("Integrate::FreeFactors[(a - b)*x, x]", "a - b");
	}

	public void test0173() {
		check("Integrate::FreeFactors[(a + b)*x, x]", "a + b");
	}

	public void test0174() {
		check("Integrate::FreeFactors[-Cos[(a - b)*x]/(2*(a - b)), x]", "-1/(2*(a - b))");
	}

	public void test0175() {
		check("Integrate::FreeFactors[Cos[(a - b)*x]/(2*(a - b)), x]", "1/(2*(a - b))");
	}

	public void test0176() {
		check("Integrate::FreeFactors[Cos[(a - b)*x]/(a - b), x]", "(a - b)^(-1)");
	}

	public void test0177() {
		check("Integrate::FreeFactors[Cos[b*x], x]", "1");
	}

	public void test0178() {
		check("Integrate::FreeFactors[-Cos[(a + b)*x]/(2*(a + b)), x]", "-1/(2*(a + b))");
	}

	public void test0179() {
		check("Integrate::FreeFactors[Cos[(a + b)*x]/(a + b), x]", "(a + b)^(-1)");
	}

	public void test0180() {
		check("Integrate::FreeFactors[Sin[a*x], x]", "1");
	}

	public void test0181() {
		check("Integrate::FreeFactors[-Sin[(a - b)*x]/2, x]", "-1/2");
	}

	public void test0182() {
		check("Integrate::FreeFactors[Sin[(a + b)*x]/2, x]", "1/2");
	}

	public void test0183() {
		check("Integrate::FreeTerms[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "0");
	}

	public void test0184() {
		check("Integrate::FunctionOfCosQ[a, b*x, x]", "True");
	}

	public void test0185() {
		check("Integrate::FunctionOfCosQ[x, b*x, x]", "False");
	}

	public void test0186() {
		check("Integrate::FunctionOfCosQ[a*x, b*x, x]", "False");
	}

	public void test0187() {
		check("Integrate::FunctionOfCosQ[Cos[a*x], b*x, x]", "False");
	}

	public void test0188() {
		check("Integrate::FunctionOfCosQ[Cos[a*x], Cos[b*x][[1]], x]", "False");
	}

	public void test0189() {
		check("Integrate::FunctionOfExponentialQ[sin[(a - b)*x], x]", "False");
	}

	public void test0190() {
		check("Integrate::FunctionOfExponentialQ[sin[(a + b)*x], x]", "False");
	}

	public void test0191() {
		check("Integrate::FunctionOfExponentialQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0192() {
		check("Integrate::FunctionOfExponentialQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0193() {
		check("Integrate::FunctionOfExponentialQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0194() {
		check("Integrate::FunctionOfExponentialTest[a, x]", "True");
	}

	public void test0195() {
		check("Integrate::FunctionOfExponentialTest[a - b, x]", "True");
	}

	public void test0196() {
		check("Integrate::FunctionOfExponentialTest[a + b, x]", "True");
	}

	public void test0197() {
		check("Integrate::FunctionOfExponentialTest[x, x]", "False");
	}

	public void test0198() {
		check("Integrate::FunctionOfExponentialTest[a*x, x]", "False");
	}

	public void test0199() {
		check("Integrate::FunctionOfExponentialTest[(a - b)*x, x]", "False");
	}

	public void test0200() {
		check("Integrate::FunctionOfExponentialTest[(a + b)*x, x]", "False");
	}

	public void test0201() {
		check("Integrate::FunctionOfExponentialTest[Cos[a*x], x]", "False");
	}

	public void test0202() {
		check("Integrate::FunctionOfExponentialTest[sin[(a - b)*x], x]", "False");
	}

	public void test0203() {
		check("Integrate::FunctionOfExponentialTest[sin[(a + b)*x], x]", "False");
	}

	public void test0204() {
		check("Integrate::FunctionOfExponentialTest[Sin[(a - b)*x], x]", "False");
	}

	public void test0205() {
		check("Integrate::FunctionOfExponentialTest[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0206() {
		check("Integrate::FunctionOfExponentialTest[Sin[(a + b)*x], x]", "False");
	}

	public void test0207() {
		check("Integrate::FunctionOfLog[x*sin[(a - b)*x], x]", "False");
	}

	public void test0208() {
		check("Integrate::FunctionOfLog[x*sin[(a + b)*x], x]", "False");
	}

	public void test0209() {
		check("Integrate::FunctionOfLog[x*Sin[(a - b)*x], x]", "False");
	}

	public void test0210() {
		check("Integrate::FunctionOfLog[x*Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0211() {
		check("Integrate::FunctionOfLog[x*Sin[(a + b)*x], x]", "False");
	}

	public void test0212() {
		check("Integrate::FunctionOfLog[x, False, False, x]", "False");
	}

	public void test0213() {
		check("Integrate::FunctionOfLog[x*sin[(a - b)*x], False, False, x]", "False");
	}

	public void test0214() {
		check("Integrate::FunctionOfLog[x*sin[(a + b)*x], False, False, x]", "False");
	}

	public void test0215() {
		check("Integrate::FunctionOfLog[x*Sin[(a - b)*x], False, False, x]", "False");
	}

	public void test0216() {
		check("Integrate::FunctionOfLog[x*Cos[a*x]*Sin[b*x], False, False, x]", "False");
	}

	public void test0217() {
		check("Integrate::FunctionOfLog[x*Sin[(a + b)*x], False, False, x]", "False");
	}

	public void test0218() {
		check("Integrate::FunctionOfQ[Cos[b*x], Cos[a*x], x]", "False");
	}

	public void test0219() {
		check("Integrate::FunctionOfQ[Sin[a*x], Sin[b*x], x]", "False");
	}

	public void test0220() {
		check("Integrate::FunctionOfQ[Cos[b*x], Cos[a*x], x, True]", "False");
	}

	public void test0221() {
		check("Integrate::FunctionOfQ[Sin[a*x], Sin[b*x], x, True]", "False");
	}

	public void test0222() {
		check("Integrate::FunctionOfSinQ[b, a*x, x]", "True");
	}

	public void test0223() {
		check("Integrate::FunctionOfSinQ[x, a*x, x]", "False");
	}

	public void test0224() {
		check("Integrate::FunctionOfSinQ[b*x, a*x, x]", "False");
	}

	public void test0225() {
		check("Integrate::FunctionOfSinQ[Sin[b*x], a*x, x]", "False");
	}

	public void test0226() {
		check("Integrate::FunctionOfSinQ[Sin[b*x], Sin[a*x][[1]], x]", "False");
	}

	public void test0227() {
		check("Integrate::FunctionOfTrig[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0228() {
		check("Integrate::FunctionOfTrig[-1, Null, x]", "Null");
	}

	public void test0229() {
		check("Integrate::FunctionOfTrig[a, Null, x]", "Null");
	}

	public void test0230() {
		check("Integrate::FunctionOfTrig[a - b, Null, x]", "Null");
	}

	public void test0231() {
		check("Integrate::FunctionOfTrig[-b, Null, x]", "Null");
	}

	public void test0232() {
		check("Integrate::FunctionOfTrig[b, Null, x]", "Null");
	}

	public void test0233() {
		check("Integrate::FunctionOfTrig[a + b, Null, x]", "Null");
	}

	public void test0234() {
		check("Integrate::FunctionOfTrig[x, Null, x]", "False");
	}

	public void test0235() {
		check("Integrate::FunctionOfTrig[(a - b)*x, Null, x]", "False");
	}

	public void test0236() {
		check("Integrate::FunctionOfTrig[(a + b)*x, Null, x]", "False");
	}

	public void test0237() {
		check("Integrate::FunctionOfTrig[Cos[a*x], Null, x]", "a*x");
	}

	public void test0238() {
		check("Integrate::FunctionOfTrig[sin[(a - b)*x], Null, x]", "False");
	}

	public void test0239() {
		check("Integrate::FunctionOfTrig[sin[(a + b)*x], Null, x]", "False");
	}

	public void test0240() {
		check("Integrate::FunctionOfTrig[Sin[(a - b)*x], Null, x]", "(a - b)*x");
	}

	public void test0241() {
		check("Integrate::FunctionOfTrig[Sin[b*x], a*x, x]", "False");
	}

	public void test0242() {
		check("Integrate::FunctionOfTrig[Cos[a*x]*Sin[b*x], Null, x]", "False");
	}

	public void test0243() {
		check("Integrate::FunctionOfTrig[Sin[(a + b)*x], Null, x]", "(a + b)*x");
	}

	public void test0244() {
		check("Integrate::FunctionOfTrigOfLinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0245() {
		check("Integrate::FunctionOfTrigOfLinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0246() {
		check("Integrate::FunctionOfTrigOfLinearQ[Sin[(a - b)*x], x]", "True");
	}

	public void test0247() {
		check("Integrate::FunctionOfTrigOfLinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0248() {
		check("Integrate::FunctionOfTrigOfLinearQ[Sin[(a + b)*x], x]", "True");
	}

	public void test0249() {
		check("Integrate::HyperbolicQ[a - b]", "False");
	}

	public void test0250() {
		check("Integrate::HyperbolicQ[-b]", "False");
	}

	public void test0251() {
		check("Integrate::HyperbolicQ[a + b]", "False");
	}

	public void test0252() {
		check("Integrate::HyperbolicQ[a*x]", "False");
	}

	public void test0253() {
		check("Integrate::HyperbolicQ[(a - b)*x]", "False");
	}

	public void test0254() {
		check("Integrate::HyperbolicQ[(a + b)*x]", "False");
	}

	public void test0255() {
		check("Integrate::HyperbolicQ[Cos[a*x]]", "False");
	}

	public void test0256() {
		check("Integrate::HyperbolicQ[sin[(a - b)*x]]", "False");
	}

	public void test0257() {
		check("Integrate::HyperbolicQ[sin[(a + b)*x]]", "False");
	}

	public void test0258() {
		check("Integrate::HyperbolicQ[Sin[(a - b)*x]]", "False");
	}

	public void test0259() {
		check("Integrate::HyperbolicQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0260() {
		check("Integrate::HyperbolicQ[Sin[(a + b)*x]]", "False");
	}

	public void test0261() {
		check("Integrate::IGtQ[1, 0]", "True");
	}

	public void test0262() {
		check("Integrate::InertTrigFreeQ[Cos[a*x]]", "True");
	}

	public void test0263() {
		check("Integrate::InertTrigFreeQ[Sin[b*x]]", "True");
	}

	public void test0264() {
		check("Integrate::InertTrigFreeQ[Cos[a*x]*Sin[b*x]]", "True");
	}

	public void test0265() {
		check("Integrate::IntegerQuotientQ[a*x, b*x]", "False");
	}

	public void test0266() {
		check("Integrate::IntegerQuotientQ[b*x, a*x]", "False");
	}

	public void test0267() {
		check("Integrate::IntegralFreeQ[-(Cos[(a - b)*x]/(a - b))]", "True");
	}

	public void test0268() {
		check("Integrate::IntegralFreeQ[-(Cos[(a + b)*x]/(a + b))]", "True");
	}

	public void test0269() {
		check("Integrate::IntSum[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0270() {
		check("Integrate::IntTerm[-Sin[(a - b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0271() {
		check("Integrate::IntTerm[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0272() {
		check("Integrate::IntTerm[Sin[(a + b)*x]/2, x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0273() {
		check("Integrate::InverseFunctionFreeQ[a, x]", "True");
	}

	public void test0274() {
		check("Integrate::InverseFunctionFreeQ[b, x]", "True");
	}

	public void test0275() {
		check("Integrate::InverseFunctionFreeQ[x, x]", "True");
	}

	public void test0276() {
		check("Integrate::InverseFunctionFreeQ[a*x, x]", "True");
	}

	public void test0277() {
		check("Integrate::InverseFunctionFreeQ[b*x, x]", "True");
	}

	public void test0278() {
		check("Integrate::InverseFunctionFreeQ[Cos[a*x], x]", "True");
	}

	public void test0279() {
		check("Integrate::InverseFunctionFreeQ[Sin[b*x], x]", "True");
	}

	public void test0280() {
		check("Integrate::InverseFunctionFreeQ[Cos[a*x]*Sin[b*x], x]", "True");
	}

	public void test0281() {
		check("Integrate::InverseFunctionQ[a*x]", "False");
	}

	public void test0282() {
		check("Integrate::InverseFunctionQ[b*x]", "False");
	}

	public void test0283() {
		check("Integrate::InverseFunctionQ[Cos[a*x]]", "False");
	}

	public void test0284() {
		check("Integrate::InverseFunctionQ[Sin[b*x]]", "False");
	}

	public void test0285() {
		check("Integrate::InverseFunctionQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0286() {
		check("Integrate::InverseHyperbolicQ[a*x]", "False");
	}

	public void test0287() {
		check("Integrate::InverseHyperbolicQ[b*x]", "False");
	}

	public void test0288() {
		check("Integrate::InverseHyperbolicQ[Cos[a*x]]", "False");
	}

	public void test0289() {
		check("Integrate::InverseHyperbolicQ[Sin[b*x]]", "False");
	}

	public void test0290() {
		check("Integrate::InverseHyperbolicQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0291() {
		check("Integrate::InverseTrigQ[a*x]", "False");
	}

	public void test0292() {
		check("Integrate::InverseTrigQ[b*x]", "False");
	}

	public void test0293() {
		check("Integrate::InverseTrigQ[Cos[a*x]]", "False");
	}

	public void test0294() {
		check("Integrate::InverseTrigQ[Sin[b*x]]", "False");
	}

	public void test0295() {
		check("Integrate::InverseTrigQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0296() {
		check("Integrate::LeadBase[1]", "1");
	}

	public void test0297() {
		check("Integrate::LeadBase[a]", "a");
	}

	public void test0298() {
		check("Integrate::LeadBase[b]", "b");
	}

	public void test0299() {
		check("Integrate::LeadBase[x]", "x");
	}

	public void test0300() {
		check("Integrate::LeadBase[a*x]", "a");
	}

	public void test0301() {
		check("Integrate::LeadBase[b*x]", "b");
	}

	public void test0302() {
		check("Integrate::LeadDegree[1]", "1");
	}

	public void test0303() {
		check("Integrate::LeadDegree[a]", "1");
	}

	public void test0304() {
		check("Integrate::LeadDegree[b]", "1");
	}

	public void test0305() {
		check("Integrate::LeadFactor[1]", "1");
	}

	public void test0306() {
		check("Integrate::LeadFactor[a]", "a");
	}

	public void test0307() {
		check("Integrate::LeadFactor[b]", "b");
	}

	public void test0308() {
		check("Integrate::LeadFactor[x]", "x");
	}

	public void test0309() {
		check("Integrate::LeadFactor[a*x]", "a");
	}

	public void test0310() {
		check("Integrate::LeadFactor[b*x]", "b");
	}

	public void test0311() {
		check("Integrate::LeadFactor[First[a*x]]", "a");
	}

	public void test0312() {
		check("Integrate::LeadFactor[First[b*x]]", "b");
	}

	public void test0313() {
		check("Integrate::LinearQ[a*x, x]", "True");
	}

	public void test0314() {
		check("Integrate::LinearQ[(a - b)*x, x]", "True");
	}

	public void test0315() {
		check("Integrate::LinearQ[b*x, x]", "True");
	}

	public void test0316() {
		check("Integrate::LinearQ[(a + b)*x, x]", "True");
	}

	public void test0317() {
		check("Integrate::LinearQ[Cos[a*x], x]", "False");
	}

	public void test0318() {
		check("Integrate::LinearQ[{Cos[a*x], b*x}, x]", "False");
	}

	public void test0319() {
		check("Integrate::LinearQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0320() {
		check("Integrate::LinearQ[{Sin[b*x], a*x}, x]", "False");
	}

	public void test0321() {
		check("Integrate::LinearQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0322() {
		check("Integrate::LinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0323() {
		check("Integrate::LinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0324() {
		check("Integrate::LinearQ[-Sin[(a - b)*x]/2, x]", "False");
	}

	public void test0325() {
		check("Integrate::LinearQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0326() {
		check("Integrate::LinearQ[Sin[b*x], x]", "False");
	}

	public void test0327() {
		check("Integrate::LinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0328() {
		check("Integrate::LinearQ[Sin[(a + b)*x]/2, x]", "False");
	}

	public void test0329() {
		check("Integrate::LinearQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0330() {
		check("Integrate::LogQ[1]", "False");
	}

	public void test0331() {
		check("Integrate::LogQ[a]", "False");
	}

	public void test0332() {
		check("Integrate::LogQ[a*x]", "False");
	}

	public void test0333() {
		check("Integrate::LogQ[b*x]", "False");
	}

	public void test0334() {
		check("Integrate::LogQ[Cos[a*x]]", "False");
	}

	public void test0335() {
		check("Integrate::LogQ[x*sin[(a - b)*x]]", "False");
	}

	public void test0336() {
		check("Integrate::LogQ[x*sin[(a + b)*x]]", "False");
	}

	public void test0337() {
		check("Integrate::LogQ[x*Sin[(a - b)*x]]", "False");
	}

	public void test0338() {
		check("Integrate::LogQ[Sin[b*x]]", "False");
	}

	public void test0339() {
		check("Integrate::LogQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0340() {
		check("Integrate::LogQ[x*Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0341() {
		check("Integrate::LogQ[x*Sin[(a + b)*x]]", "False");
	}

	public void test0342() {
		check("Integrate::MergeableFactorQ[-1/2, 1, Cos[(a - b)*x]]", "False");
	}

	public void test0343() {
		check("Integrate::MergeableFactorQ[-1/2, 1, Cos[(a + b)*x]]", "False");
	}

	public void test0344() {
		check("Integrate::MergeableFactorQ[1/2, 1, Cos[(a - b)*x]]", "False");
	}

	public void test0345() {
		check("Integrate::MergeableFactorQ[a - b, -1, -1/2]", "False");
	}

	public void test0346() {
		check("Integrate::MergeableFactorQ[a - b, -1, 1/2]", "False");
	}

	public void test0347() {
		check("Integrate::MergeableFactorQ[a - b, -1, -Cos[(a - b)*x]/2]", "False");
	}

	public void test0348() {
		check("Integrate::MergeableFactorQ[a - b, -1, Cos[(a - b)*x]/2]", "False");
	}

	public void test0349() {
		check("Integrate::MergeableFactorQ[a - b, -1, Cos[(a - b)*x]]", "False");
	}

	public void test0350() {
		check("Integrate::MergeableFactorQ[a - b, 1, x]", "False");
	}

	public void test0351() {
		check("Integrate::MergeableFactorQ[a + b, -1, -1/2]", "False");
	}

	public void test0352() {
		check("Integrate::MergeableFactorQ[a + b, -1, -Cos[(a + b)*x]/2]", "False");
	}

	public void test0353() {
		check("Integrate::MergeableFactorQ[a + b, -1, Cos[(a + b)*x]]", "False");
	}

	public void test0354() {
		check("Integrate::MergeableFactorQ[a + b, 1, x]", "False");
	}

	public void test0355() {
		check("Integrate::MergeFactors[-1/2, Cos[(a - b)*x]]", "-Cos[(a - b)*x]/2");
	}

	public void test0356() {
		check("Integrate::MergeFactors[-1/2, Cos[(a + b)*x]]", "-Cos[(a + b)*x]/2");
	}

	public void test0357() {
		check("Integrate::MergeFactors[1/2, Cos[(a - b)*x]]", "Cos[(a - b)*x]/2");
	}

	public void test0358() {
		check("Integrate::MergeFactors[-1/(2*(a - b)), Cos[(a - b)*x]]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0359() {
		check("Integrate::MergeFactors[1/(2*(a - b)), Cos[(a - b)*x]]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0360() {
		check("Integrate::MergeFactors[(a - b)^(-1), -Cos[(a - b)*x]/2]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0361() {
		check("Integrate::MergeFactors[(a - b)^(-1), Cos[(a - b)*x]/2]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0362() {
		check("Integrate::MergeFactors[(a - b)^(-1), Cos[(a - b)*x]]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0363() {
		check("Integrate::MergeFactors[a - b, x]", "(a - b)*x");
	}

	public void test0364() {
		check("Integrate::MergeFactors[-1/(2*(a + b)), Cos[(a + b)*x]]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0365() {
		check("Integrate::MergeFactors[(a + b)^(-1), -Cos[(a + b)*x]/2]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0366() {
		check("Integrate::MergeFactors[(a + b)^(-1), Cos[(a + b)*x]]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0367() {
		check("Integrate::MergeFactors[a + b, x]", "(a + b)*x");
	}

	public void test0368() {
		check("Integrate::MostMainFactorPosition[{1, b}]", "2");
	}

	public void test0369() {
		check("Integrate::MostMainFactorPosition[{a, b}]", "1");
	}

	public void test0370() {
		check("Integrate::NeQ[1, -1]", "True");
	}

	public void test0371() {
		check("Integrate::NeQ[a, 0]", "True");
	}

	public void test0372() {
		check("Integrate::NeQ[a - b, 0]", "True");
	}

	public void test0373() {
		check("Integrate::NeQ[b, 0]", "True");
	}

	public void test0374() {
		check("Integrate::NeQ[a + b, 0]", "True");
	}

	public void test0375() {
		check("Integrate::NonabsurdNumberFactors[-1]", "1");
	}

	public void test0376() {
		check("Integrate::NonabsurdNumberFactors[a]", "a");
	}

	public void test0377() {
		check("Integrate::NonabsurdNumberFactors[-b]", "b");
	}

	public void test0378() {
		check("Integrate::NonabsurdNumberFactors[b]", "b");
	}

	public void test0379() {
		check("Integrate::NonfreeFactors[(a - b)*x, x]", "x");
	}

	public void test0380() {
		check("Integrate::NonfreeFactors[(a + b)*x, x]", "x");
	}

	public void test0381() {
		check("Integrate::NonfreeFactors[-Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]");
	}

	public void test0382() {
		check("Integrate::NonfreeFactors[Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]");
	}

	public void test0383() {
		check("Integrate::NonfreeFactors[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]");
	}

	public void test0384() {
		check("Integrate::NonfreeFactors[-Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a + b)*x]");
	}

	public void test0385() {
		check("Integrate::NonfreeFactors[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]");
	}

	public void test0386() {
		check("Integrate::NonfreeFactors[-Sin[(a - b)*x]/2, x]", "Sin[(a - b)*x]");
	}

	public void test0387() {
		check("Integrate::NonfreeFactors[Sin[(a + b)*x]/2, x]", "Sin[(a + b)*x]");
	}

	public void test0388() {
		check("Integrate::NonfreeTerms[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2, x]", "-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2");
	}

	public void test0389() {
		check("Integrate::NonnumericFactors[-1/2]", "1");
	}

	public void test0390() {
		check("Integrate::NonnumericFactors[1/2]", "1");
	}

	public void test0391() {
		check("Integrate::NonnumericFactors[a]", "a");
	}

	public void test0392() {
		check("Integrate::NonnumericFactors[-1/(2*(a - b))]", "(a - b)^(-1)");
	}

	public void test0393() {
		check("Integrate::NonnumericFactors[1/(2*(a - b))]", "(a - b)^(-1)");
	}

	public void test0394() {
		check("Integrate::NonnumericFactors[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0395() {
		check("Integrate::NonnumericFactors[a - b]", "a - b");
	}

	public void test0396() {
		check("Integrate::NonnumericFactors[b]", "b");
	}

	public void test0397() {
		check("Integrate::NonnumericFactors[-1/(2*(a + b))]", "(a + b)^(-1)");
	}

	public void test0398() {
		check("Integrate::NonnumericFactors[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0399() {
		check("Integrate::NonnumericFactors[a + b]", "a + b");
	}

	public void test0400() {
		check("Integrate::NonsumQ[sin[(a - b)*x]]", "True");
	}

	public void test0401() {
		check("Integrate::NonsumQ[sin[(a + b)*x]]", "True");
	}

	public void test0402() {
		check("Integrate::NonsumQ[Sin[(a - b)*x]]", "True");
	}

	public void test0403() {
		check("Integrate::NonsumQ[Cos[a*x]*Sin[b*x]]", "True");
	}

	public void test0404() {
		check("Integrate::NonsumQ[Sin[(a + b)*x]]", "True");
	}

	public void test0405() {
		check("Integrate::NormalizeSumFactors[-1]", "-1");
	}

	public void test0406() {
		check("Integrate::NormalizeSumFactors[-1/2]", "-1/2");
	}

	public void test0407() {
		check("Integrate::NormalizeSumFactors[0]", "0");
	}

	public void test0408() {
		check("Integrate::NormalizeSumFactors[1/2]", "1/2");
	}

	public void test0409() {
		check("Integrate::NormalizeSumFactors[a]", "a");
	}

	public void test0410() {
		check("Integrate::NormalizeSumFactors[(a - b)^(-1)]", "(a - b)^(-1)");
	}

	public void test0411() {
		check("Integrate::NormalizeSumFactors[a - b]", "a - b");
	}

	public void test0412() {
		check("Integrate::NormalizeSumFactors[-b]", "-b");
	}

	public void test0413() {
		check("Integrate::NormalizeSumFactors[b]", "b");
	}

	public void test0414() {
		check("Integrate::NormalizeSumFactors[(a + b)^(-1)]", "(a + b)^(-1)");
	}

	public void test0415() {
		check("Integrate::NormalizeSumFactors[a + b]", "a + b");
	}

	public void test0416() {
		check("Integrate::NormalizeSumFactors[x]", "x");
	}

	public void test0417() {
		check("Integrate::NormalizeSumFactors[(a - b)*x]", "(a - b)*x");
	}

	public void test0418() {
		check("Integrate::NormalizeSumFactors[(a + b)*x]", "(a + b)*x");
	}

	public void test0419() {
		check("Integrate::NormalizeSumFactors[Cos[(a - b)*x]]", "Cos[(a - b)*x]");
	}

	public void test0420() {
		check("Integrate::NormalizeSumFactors[-Cos[(a - b)*x]/(2*(a - b))]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0421() {
		check("Integrate::NormalizeSumFactors[Cos[(a - b)*x]/(2*(a - b))]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0422() {
		check("Integrate::NormalizeSumFactors[Cos[(a - b)*x]/(a - b)]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0423() {
		check("Integrate::NormalizeSumFactors[Cos[(a + b)*x]]", "Cos[(a + b)*x]");
	}

	public void test0424() {
		check("Integrate::NormalizeSumFactors[-Cos[(a + b)*x]/(2*(a + b))]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0425() {
		check("Integrate::NormalizeSumFactors[Cos[(a + b)*x]/(a + b)]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0426() {
		check("Integrate::NormalizeSumFactors[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0427() {
		check("Integrate::NormalizeTrigReduce[-Sin[a*x - b*x]/2, x]", "-Sin[(a - b)*x]/2");
	}

	public void test0428() {
		check("Integrate::NormalizeTrigReduce[Sin[a*x + b*x]/2, x]", "Sin[(a + b)*x]/2");
	}

	public void test0429() {
		check("Integrate::NumericFactor[-1]", "-1");
	}

	public void test0430() {
		check("Integrate::NumericFactor[-1/2]", "-1/2");
	}

	public void test0431() {
		check("Integrate::NumericFactor[1/2]", "1/2");
	}

	public void test0432() {
		check("Integrate::NumericFactor[a]", "1");
	}

	public void test0433() {
		check("Integrate::NumericFactor[-1/(2*(a - b))]", "-1/2");
	}

	public void test0434() {
		check("Integrate::NumericFactor[1/(2*(a - b))]", "1/2");
	}

	public void test0435() {
		check("Integrate::NumericFactor[(a - b)^(-1)]", "1");
	}

	public void test0436() {
		check("Integrate::NumericFactor[a - b]", "1");
	}

	public void test0437() {
		check("Integrate::NumericFactor[-b]", "-1");
	}

	public void test0438() {
		check("Integrate::NumericFactor[b]", "1");
	}

	public void test0439() {
		check("Integrate::NumericFactor[-1/(2*(a + b))]", "-1/2");
	}

	public void test0440() {
		check("Integrate::NumericFactor[(a + b)^(-1)]", "1");
	}

	public void test0441() {
		check("Integrate::NumericFactor[a + b]", "1");
	}

	public void test0442() {
		check("Integrate::PiecewiseLinearQ[sin[(a - b)*x], x]", "False");
	}

	public void test0443() {
		check("Integrate::PiecewiseLinearQ[sin[(a + b)*x], x]", "False");
	}

	public void test0444() {
		check("Integrate::PiecewiseLinearQ[Sin[(a - b)*x], x]", "False");
	}

	public void test0445() {
		check("Integrate::PiecewiseLinearQ[Cos[a*x]*Sin[b*x], x]", "False");
	}

	public void test0446() {
		check("Integrate::PiecewiseLinearQ[Sin[(a + b)*x], x]", "False");
	}

	public void test0447() {
		check("Integrate::PolyQ[(a - b)*x, x]", "True");
	}

	public void test0448() {
		check("Integrate::PolyQ[(a + b)*x, x]", "True");
	}

	public void test0449() {
		check("Integrate::PolyQ[a*x - b*x, x]", "True");
	}

	public void test0450() {
		check("Integrate::PolyQ[a*x + b*x, x]", "True");
	}

	public void test0451() {
		check("Integrate::PolyQ[Cos[a*x], x]", "False");
	}

	public void test0452() {
		check("Integrate::PolyQ[Sin[b*x], x]", "False");
	}

	public void test0453() {
		check("Integrate::PolyQ[a*x, x, 1]", "True");
	}

	public void test0454() {
		check("Integrate::PolyQ[(a - b)*x, x, 1]", "True");
	}

	public void test0455() {
		check("Integrate::PolyQ[b*x, x, 1]", "True");
	}

	public void test0456() {
		check("Integrate::PolyQ[(a + b)*x, x, 1]", "True");
	}

	public void test0457() {
		check("Integrate::PolyQ[Cos[a*x], x, 1]", "False");
	}

	public void test0458() {
		check("Integrate::PolyQ[Cos[a*x], x, 2]", "False");
	}

	public void test0459() {
		check("Integrate::PolyQ[sin[(a - b)*x], x, 1]", "False");
	}

	public void test0460() {
		check("Integrate::PolyQ[sin[(a + b)*x], x, 1]", "False");
	}

	public void test0461() {
		check("Integrate::PolyQ[-Sin[(a - b)*x]/2, x, 1]", "False");
	}

	public void test0462() {
		check("Integrate::PolyQ[Sin[(a - b)*x], x, 1]", "False");
	}

	public void test0463() {
		check("Integrate::PolyQ[Sin[b*x], x, 1]", "False");
	}

	public void test0464() {
		check("Integrate::PolyQ[Sin[b*x], x, 2]", "False");
	}

	public void test0465() {
		check("Integrate::PolyQ[Cos[a*x]*Sin[b*x], x, 1]", "False");
	}

	public void test0466() {
		check("Integrate::PolyQ[Sin[(a + b)*x]/2, x, 1]", "False");
	}

	public void test0467() {
		check("Integrate::PolyQ[Sin[(a + b)*x], x, 1]", "False");
	}

	public void test0468() {
		check("Integrate::PowerQ[-1/2]", "False");
	}

	public void test0469() {
		check("Integrate::PowerQ[0]", "False");
	}

	public void test0470() {
		check("Integrate::PowerQ[1/2]", "False");
	}

	public void test0471() {
		check("Integrate::PowerQ[1]", "False");
	}

	public void test0472() {
		check("Integrate::PowerQ[a]", "False");
	}

	public void test0473() {
		check("Integrate::PowerQ[-1/(2*(a - b))]", "False");
	}

	public void test0474() {
		check("Integrate::PowerQ[1/(2*(a - b))]", "False");
	}

	public void test0475() {
		check("Integrate::PowerQ[(a - b)^(-1)]", "True");
	}

	public void test0476() {
		check("Integrate::PowerQ[a - b]", "False");
	}

	public void test0477() {
		check("Integrate::PowerQ[-b]", "False");
	}

	public void test0478() {
		check("Integrate::PowerQ[b]", "False");
	}

	public void test0479() {
		check("Integrate::PowerQ[-1/(2*(a + b))]", "False");
	}

	public void test0480() {
		check("Integrate::PowerQ[(a + b)^(-1)]", "True");
	}

	public void test0481() {
		check("Integrate::PowerQ[a + b]", "False");
	}

	public void test0482() {
		check("Integrate::PowerQ[x]", "False");
	}

	public void test0483() {
		check("Integrate::PowerQ[a*x]", "False");
	}

	public void test0484() {
		check("Integrate::PowerQ[(a - b)*x]", "False");
	}

	public void test0485() {
		check("Integrate::PowerQ[(a + b)*x]", "False");
	}

	public void test0486() {
		check("Integrate::PowerQ[x^2]", "True");
	}

	public void test0487() {
		check("Integrate::PowerQ[x^2/(a - b)]", "False");
	}

	public void test0488() {
		check("Integrate::PowerQ[(a - b)*x^2]", "False");
	}

	public void test0489() {
		check("Integrate::PowerQ[x^2/(a + b)]", "False");
	}

	public void test0490() {
		check("Integrate::PowerQ[(a + b)*x^2]", "False");
	}

	public void test0491() {
		check("Integrate::PowerQ[Cos[a*x]]", "False");
	}

	public void test0492() {
		check("Integrate::PowerQ[-Cos[(a - b)*x]/2]", "False");
	}

	public void test0493() {
		check("Integrate::PowerQ[Cos[(a - b)*x]/2]", "False");
	}

	public void test0494() {
		check("Integrate::PowerQ[Cos[(a - b)*x]]", "False");
	}

	public void test0495() {
		check("Integrate::PowerQ[-Cos[(a + b)*x]/2]", "False");
	}

	public void test0496() {
		check("Integrate::PowerQ[Cos[(a + b)*x]]", "False");
	}

	public void test0497() {
		check("Integrate::PowerQ[sin[(a - b)*x]]", "False");
	}

	public void test0498() {
		check("Integrate::PowerQ[sin[(a + b)*x]]", "False");
	}

	public void test0499() {
		check("Integrate::PowerQ[Sin[(a - b)*x]]", "False");
	}

	public void test0500() {
		check("Integrate::PowerQ[Sin[b*x]]", "False");
	}

	public void test0501() {
		check("Integrate::PowerQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0502() {
		check("Integrate::PowerQ[Sin[(a + b)*x]]", "False");
	}

	public void test0503() {
		check("Integrate::ProductQ[-1/2]", "False");
	}

	public void test0504() {
		check("Integrate::ProductQ[0]", "False");
	}

	public void test0505() {
		check("Integrate::ProductQ[1/2]", "False");
	}

	public void test0506() {
		check("Integrate::ProductQ[1]", "False");
	}

	public void test0507() {
		check("Integrate::ProductQ[a]", "False");
	}

	public void test0508() {
		check("Integrate::ProductQ[-1/(2*(a - b))]", "True");
	}

	public void test0509() {
		check("Integrate::ProductQ[1/(2*(a - b))]", "True");
	}

	public void test0510() {
		check("Integrate::ProductQ[(a - b)^(-1)]", "False");
	}

	public void test0511() {
		check("Integrate::ProductQ[a - b]", "False");
	}

	public void test0512() {
		check("Integrate::ProductQ[-b]", "True");
	}

	public void test0513() {
		check("Integrate::ProductQ[b]", "False");
	}

	public void test0514() {
		check("Integrate::ProductQ[-1/(2*(a + b))]", "True");
	}

	public void test0515() {
		check("Integrate::ProductQ[(a + b)^(-1)]", "False");
	}

	public void test0516() {
		check("Integrate::ProductQ[a + b]", "False");
	}

	public void test0517() {
		check("Integrate::ProductQ[x]", "False");
	}

	public void test0518() {
		check("Integrate::ProductQ[a*x]", "True");
	}

	public void test0519() {
		check("Integrate::ProductQ[(a - b)*x]", "True");
	}

	public void test0520() {
		check("Integrate::ProductQ[b*x]", "True");
	}

	public void test0521() {
		check("Integrate::ProductQ[(a + b)*x]", "True");
	}

	public void test0522() {
		check("Integrate::ProductQ[x^2/(a - b)]", "True");
	}

	public void test0523() {
		check("Integrate::ProductQ[(a - b)*x^2]", "True");
	}

	public void test0524() {
		check("Integrate::ProductQ[x^2/(a + b)]", "True");
	}

	public void test0525() {
		check("Integrate::ProductQ[(a + b)*x^2]", "True");
	}

	public void test0526() {
		check("Integrate::ProductQ[Cos[a*x]]", "False");
	}

	public void test0527() {
		check("Integrate::ProductQ[-Cos[(a - b)*x]/2]", "True");
	}

	public void test0528() {
		check("Integrate::ProductQ[Cos[(a - b)*x]/2]", "True");
	}

	public void test0529() {
		check("Integrate::ProductQ[Cos[(a - b)*x]]", "False");
	}

	public void test0530() {
		check("Integrate::ProductQ[-Cos[(a - b)*x]/(2*(a - b))]", "True");
	}

	public void test0531() {
		check("Integrate::ProductQ[Cos[(a - b)*x]/(2*(a - b))]", "True");
	}

	public void test0532() {
		check("Integrate::ProductQ[Cos[(a - b)*x]/(a - b)]", "True");
	}

	public void test0533() {
		check("Integrate::ProductQ[Cos[b*x]]", "False");
	}

	public void test0534() {
		check("Integrate::ProductQ[-Cos[(a + b)*x]/2]", "True");
	}

	public void test0535() {
		check("Integrate::ProductQ[Cos[(a + b)*x]]", "False");
	}

	public void test0536() {
		check("Integrate::ProductQ[-Cos[(a + b)*x]/(2*(a + b))]", "True");
	}

	public void test0537() {
		check("Integrate::ProductQ[Cos[(a + b)*x]/(a + b)]", "True");
	}

	public void test0538() {
		check("Integrate::ProductQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0539() {
		check("Integrate::ProductQ[Sin[a*x]]", "False");
	}

	public void test0540() {
		check("Integrate::ProductQ[-Sin[(a - b)*x]/2]", "True");
	}

	public void test0541() {
		check("Integrate::ProductQ[Sin[b*x]]", "False");
	}

	public void test0542() {
		check("Integrate::ProductQ[Sin[(a + b)*x]/2]", "True");
	}

	public void test0543() {
		check("Integrate::PseudoBinomialPairQ[Cos[a*x], Sin[b*x], x]", "False");
	}

	public void test0544() {
		check("Integrate::PseudoBinomialPairQ[Sin[b*x], Cos[a*x], x]", "False");
	}

	public void test0545() {
		check("Integrate::PseudoBinomialParts[Cos[a*x], x]", "False");
	}

	public void test0546() {
		check("Integrate::PseudoBinomialParts[Sin[b*x], x]", "False");
	}

	public void test0547() {
		check("Integrate::PureFunctionOfCosQ[a, b*x, x]", "True");
	}

	public void test0548() {
		check("Integrate::PureFunctionOfCosQ[x, b*x, x]", "False");
	}

	public void test0549() {
		check("Integrate::PureFunctionOfCosQ[a*x, b*x, x]", "False");
	}

	public void test0550() {
		check("Integrate::PureFunctionOfCosQ[Cos[a*x], b*x, x]", "False");
	}

	public void test0551() {
		check("Integrate::PureFunctionOfCosQ[Cos[a*x], Cos[b*x][[1]], x]", "False");
	}

	public void test0552() {
		check("Integrate::PureFunctionOfSinQ[b, a*x, x]", "True");
	}

	public void test0553() {
		check("Integrate::PureFunctionOfSinQ[x, a*x, x]", "False");
	}

	public void test0554() {
		check("Integrate::PureFunctionOfSinQ[b*x, a*x, x]", "False");
	}

	public void test0555() {
		check("Integrate::PureFunctionOfSinQ[Sin[b*x], a*x, x]", "False");
	}

	public void test0556() {
		check("Integrate::PureFunctionOfSinQ[Sin[b*x], Sin[a*x][[1]], x]", "False");
	}

	public void test0557() {
		check("Integrate::QuadraticQ[Cos[a*x], x]", "False");
	}

	public void test0558() {
		check("Integrate::QuadraticQ[{Cos[a*x], Sin[b*x]}, x]", "False");
	}

	public void test0559() {
		check("Integrate::QuadraticQ[{Sin[b*x], Cos[a*x]}, x]", "False");
	}

	public void test0560() {
		check("Integrate::QuadraticQ[Sin[b*x], x]", "False");
	}

	public void test0561() {
		check("Integrate::RationalQ[-1]", "True");
	}

	public void test0562() {
		check("Integrate::RationalQ[-1/2]", "True");
	}

	public void test0563() {
		check("Integrate::RationalQ[1/2]", "True");
	}

	public void test0564() {
		check("Integrate::RationalQ[1]", "True");
	}

	public void test0565() {
		check("Integrate::RationalQ[2]", "True");
	}

	public void test0566() {
		check("Integrate::RationalQ[a]", "False");
	}

	public void test0567() {
		check("Integrate::RationalQ[(a - b)^(-1)]", "False");
	}

	public void test0568() {
		check("Integrate::RationalQ[a - b]", "False");
	}

	public void test0569() {
		check("Integrate::RationalQ[a/b]", "False");
	}

	public void test0570() {
		check("Integrate::RationalQ[-b]", "False");
	}

	public void test0571() {
		check("Integrate::RationalQ[b]", "False");
	}

	public void test0572() {
		check("Integrate::RationalQ[(a + b)^(-1)]", "False");
	}

	public void test0573() {
		check("Integrate::RationalQ[a + b]", "False");
	}

	public void test0574() {
		check("Integrate::RationalQ[x]", "False");
	}

	public void test0575() {
		check("Integrate::RationalQ[(a - b)*x]", "False");
	}

	public void test0576() {
		check("Integrate::RationalQ[(a + b)*x]", "False");
	}

	public void test0577() {
		check("Integrate::RationalQ[x^2]", "False");
	}

	public void test0578() {
		check("Integrate::RationalQ[Cos[(a - b)*x]]", "False");
	}

	public void test0579() {
		check("Integrate::RationalQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0580() {
		check("Integrate::RationalQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0581() {
		check("Integrate::RationalQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0582() {
		check("Integrate::RationalQ[Cos[(a + b)*x]]", "False");
	}

	public void test0583() {
		check("Integrate::RationalQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0584() {
		check("Integrate::RationalQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0585() {
		check("Integrate::ReduceInertTrig[sin, (a - b)*x]", "sin[(a - b)*x]");
	}

	public void test0586() {
		check("Integrate::ReduceInertTrig[sin, (a + b)*x]", "sin[(a + b)*x]");
	}

	public void test0587() {
		check("Integrate::RemainingFactors[a]", "1");
	}

	public void test0588() {
		check("Integrate::RemainingFactors[b]", "1");
	}

	public void test0589() {
		check("Integrate::RemainingFactors[x]", "1");
	}

	public void test0590() {
		check("Integrate::RemainingFactors[a*x]", "x");
	}

	public void test0591() {
		check("Integrate::RemainingFactors[b*x]", "x");
	}

	public void test0592() {
		check("Integrate::SignOfFactor[-1]", "{-1, 1}");
	}

	public void test0593() {
		check("Integrate::SignOfFactor[-1/2]", "{-1, 1/2}");
	}

	public void test0594() {
		check("Integrate::SignOfFactor[1/2]", "{1, 1/2}");
	}

	public void test0595() {
		check("Integrate::SignOfFactor[(a - b)^(-1)]", "{1, (a - b)^(-1)}");
	}

	public void test0596() {
		check("Integrate::SignOfFactor[a - b]", "{1, a - b}");
	}

	public void test0597() {
		check("Integrate::SignOfFactor[-b]", "{-1, b}");
	}

	public void test0598() {
		check("Integrate::SignOfFactor[b]", "{1, b}");
	}

	public void test0599() {
		check("Integrate::SignOfFactor[(a + b)^(-1)]", "{1, (a + b)^(-1)}");
	}

	public void test0600() {
		check("Integrate::SignOfFactor[a + b]", "{1, a + b}");
	}

	public void test0601() {
		check("Integrate::SignOfFactor[x]", "{1, x}");
	}

	public void test0602() {
		check("Integrate::SignOfFactor[(a - b)*x]", "{1, (a - b)*x}");
	}

	public void test0603() {
		check("Integrate::SignOfFactor[(a + b)*x]", "{1, (a + b)*x}");
	}

	public void test0604() {
		check("Integrate::SignOfFactor[Cos[(a - b)*x]]", "{1, Cos[(a - b)*x]}");
	}

	public void test0605() {
		check("Integrate::SignOfFactor[-Cos[(a - b)*x]/(2*(a - b))]", "{-1, Cos[(a - b)*x]/(2*(a - b))}");
	}

	public void test0606() {
		check("Integrate::SignOfFactor[Cos[(a - b)*x]/(2*(a - b))]", "{1, Cos[(a - b)*x]/(2*(a - b))}");
	}

	public void test0607() {
		check("Integrate::SignOfFactor[Cos[(a - b)*x]/(a - b)]", "{1, Cos[(a - b)*x]/(a - b)}");
	}

	public void test0608() {
		check("Integrate::SignOfFactor[Cos[(a + b)*x]]", "{1, Cos[(a + b)*x]}");
	}

	public void test0609() {
		check("Integrate::SignOfFactor[-Cos[(a + b)*x]/(2*(a + b))]", "{-1, Cos[(a + b)*x]/(2*(a + b))}");
	}

	public void test0610() {
		check("Integrate::SignOfFactor[Cos[(a + b)*x]/(a + b)]", "{1, Cos[(a + b)*x]/(a + b)}");
	}

	public void test0611() {
		check("Integrate::Simp[0, x]", "0");
	}

	public void test0612() {
		check("Integrate::Simp[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0613() {
		check("Integrate::Simp[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0614() {
		check("Integrate::Simp[-Cos[(a - b)*x]/(2*(a - b)), x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0615() {
		check("Integrate::Simp[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0616() {
		check("Integrate::Simp[-(Cos[(a - b)*x]/(a - b))/2, x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0617() {
		check("Integrate::Simp[-Cos[(a + b)*x]/(2*(a + b)), x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0618() {
		check("Integrate::Simp[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0619() {
		check("Integrate::Simp[-(Cos[(a + b)*x]/(a + b))/2, x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0620() {
		check("Integrate::Simp[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0621() {
		check("Integrate::SimpFixFactor[-1/2, x]", "-1/2");
	}

	public void test0622() {
		check("Integrate::SimpFixFactor[1/2, x]", "1/2");
	}

	public void test0623() {
		check("Integrate::SimpFixFactor[(a - b)^(-1), x]", "(a - b)^(-1)");
	}

	public void test0624() {
		check("Integrate::SimpFixFactor[a - b, x]", "a - b");
	}

	public void test0625() {
		check("Integrate::SimpFixFactor[(a + b)^(-1), x]", "(a + b)^(-1)");
	}

	public void test0626() {
		check("Integrate::SimpFixFactor[a + b, x]", "a + b");
	}

	public void test0627() {
		check("Integrate::SimpFixFactor[x, x]", "x");
	}

	public void test0628() {
		check("Integrate::SimpFixFactor[Cos[(a - b)*x], x]", "Cos[(a - b)*x]");
	}

	public void test0629() {
		check("Integrate::SimpFixFactor[Cos[(a + b)*x], x]", "Cos[(a + b)*x]");
	}

	public void test0630() {
		check("Integrate::SimpHelp[0, x]", "0");
	}

	public void test0631() {
		check("Integrate::SimpHelp[x, x]", "x");
	}

	public void test0632() {
		check("Integrate::SimpHelp[(a - b)*x, x]", "(a - b)*x");
	}

	public void test0633() {
		check("Integrate::SimpHelp[(a + b)*x, x]", "(a + b)*x");
	}

	public void test0634() {
		check("Integrate::SimpHelp[Cos[(a - b)*x], x]", "Cos[(a - b)*x]");
	}

	public void test0635() {
		check("Integrate::SimpHelp[-Cos[(a - b)*x]/(2*(a - b)), x]", "-Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0636() {
		check("Integrate::SimpHelp[Cos[(a - b)*x]/(2*(a - b)), x]", "Cos[(a - b)*x]/(2*(a - b))");
	}

	public void test0637() {
		check("Integrate::SimpHelp[Cos[(a - b)*x]/(a - b), x]", "Cos[(a - b)*x]/(a - b)");
	}

	public void test0638() {
		check("Integrate::SimpHelp[Cos[(a + b)*x], x]", "Cos[(a + b)*x]");
	}

	public void test0639() {
		check("Integrate::SimpHelp[-Cos[(a + b)*x]/(2*(a + b)), x]", "-Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0640() {
		check("Integrate::SimpHelp[Cos[(a + b)*x]/(a + b), x]", "Cos[(a + b)*x]/(a + b)");
	}

	public void test0641() {
		check("Integrate::SimpHelp[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b)), x]", "Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))");
	}

	public void test0642() {
		check("Integrate::SmartSimplify[0]", "0");
	}

	public void test0643() {
		check("Integrate::SmartSimplify[x^2/(a - b)]", "x^2/(a - b)");
	}

	public void test0644() {
		check("Integrate::SmartSimplify[(a - b)*x^2]", "(a - b)*x^2");
	}

	public void test0645() {
		check("Integrate::SmartSimplify[x^2/(a + b)]", "x^2/(a + b)");
	}

	public void test0646() {
		check("Integrate::SmartSimplify[(a + b)*x^2]", "(a + b)*x^2");
	}

	public void test0647() {
		check("Integrate::StopFunctionQ[(a - b)^(-1)]", "False");
	}

	public void test0648() {
		check("Integrate::StopFunctionQ[a - b]", "False");
	}

	public void test0649() {
		check("Integrate::StopFunctionQ[-b]", "False");
	}

	public void test0650() {
		check("Integrate::StopFunctionQ[(a + b)^(-1)]", "False");
	}

	public void test0651() {
		check("Integrate::StopFunctionQ[a + b]", "False");
	}

	public void test0652() {
		check("Integrate::StopFunctionQ[(a - b)*x]", "False");
	}

	public void test0653() {
		check("Integrate::StopFunctionQ[(a + b)*x]", "False");
	}

	public void test0654() {
		check("Integrate::StopFunctionQ[Cos[(a - b)*x]]", "False");
	}

	public void test0655() {
		check("Integrate::StopFunctionQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0656() {
		check("Integrate::StopFunctionQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0657() {
		check("Integrate::StopFunctionQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0658() {
		check("Integrate::StopFunctionQ[Cos[(a + b)*x]]", "False");
	}

	public void test0659() {
		check("Integrate::StopFunctionQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0660() {
		check("Integrate::StopFunctionQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0661() {
		check("Integrate::StopFunctionQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0662() {
		check("Integrate::SumQ[-1/2]", "False");
	}

	public void test0663() {
		check("Integrate::SumQ[0]", "False");
	}

	public void test0664() {
		check("Integrate::SumQ[1/2]", "False");
	}

	public void test0665() {
		check("Integrate::SumQ[a]", "False");
	}

	public void test0666() {
		check("Integrate::SumQ[(a - b)^(-1)]", "False");
	}

	public void test0667() {
		check("Integrate::SumQ[a - b]", "True");
	}

	public void test0668() {
		check("Integrate::SumQ[-b]", "False");
	}

	public void test0669() {
		check("Integrate::SumQ[b]", "False");
	}

	public void test0670() {
		check("Integrate::SumQ[(a + b)^(-1)]", "False");
	}

	public void test0671() {
		check("Integrate::SumQ[a + b]", "True");
	}

	public void test0672() {
		check("Integrate::SumQ[x]", "False");
	}

	public void test0673() {
		check("Integrate::SumQ[(a - b)*x]", "False");
	}

	public void test0674() {
		check("Integrate::SumQ[(a + b)*x]", "False");
	}

	public void test0675() {
		check("Integrate::SumQ[Cos[a*x]]", "False");
	}

	public void test0676() {
		check("Integrate::SumQ[Cos[(a - b)*x]]", "False");
	}

	public void test0677() {
		check("Integrate::SumQ[-(Cos[(a - b)*x]/(a - b))]", "False");
	}

	public void test0678() {
		check("Integrate::SumQ[-Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0679() {
		check("Integrate::SumQ[Cos[(a - b)*x]/(2*(a - b))]", "False");
	}

	public void test0680() {
		check("Integrate::SumQ[Cos[(a - b)*x]/(a - b)]", "False");
	}

	public void test0681() {
		check("Integrate::SumQ[Cos[(a + b)*x]]", "False");
	}

	public void test0682() {
		check("Integrate::SumQ[-(Cos[(a + b)*x]/(a + b))]", "False");
	}

	public void test0683() {
		check("Integrate::SumQ[-Cos[(a + b)*x]/(2*(a + b))]", "False");
	}

	public void test0684() {
		check("Integrate::SumQ[Cos[(a + b)*x]/(a + b)]", "False");
	}

	public void test0685() {
		check("Integrate::SumQ[Cos[(a - b)*x]/(2*(a - b)) - Cos[(a + b)*x]/(2*(a + b))]", "True");
	}

	public void test0686() {
		check("Integrate::SumQ[sin[(a - b)*x]]", "False");
	}

	public void test0687() {
		check("Integrate::SumQ[sin[(a + b)*x]]", "False");
	}

	public void test0688() {
		check("Integrate::SumQ[-Sin[(a - b)*x]/2]", "False");
	}

	public void test0689() {
		check("Integrate::SumQ[Sin[(a - b)*x]]", "False");
	}

	public void test0690() {
		check("Integrate::SumQ[Sin[b*x]]", "False");
	}

	public void test0691() {
		check("Integrate::SumQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0692() {
		check("Integrate::SumQ[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2]", "True");
	}

	public void test0693() {
		check("Integrate::SumQ[Sin[(a + b)*x]/2]", "False");
	}

	public void test0694() {
		check("Integrate::SumQ[Sin[(a + b)*x]]", "False");
	}

	public void test0695() {
		check("Integrate::SumQ[-Sin[a*x - b*x]/2 + Sin[a*x + b*x]/2]", "True");
	}

	public void test0696() {
		check("Integrate::TrigQ[a - b]", "False");
	}

	public void test0697() {
		check("Integrate::TrigQ[-b]", "False");
	}

	public void test0698() {
		check("Integrate::TrigQ[a + b]", "False");
	}

	public void test0699() {
		check("Integrate::TrigQ[a*x]", "False");
	}

	public void test0700() {
		check("Integrate::TrigQ[(a - b)*x]", "False");
	}

	public void test0701() {
		check("Integrate::TrigQ[b*x]", "False");
	}

	public void test0702() {
		check("Integrate::TrigQ[(a + b)*x]", "False");
	}

	public void test0703() {
		check("Integrate::TrigQ[Cos[a*x]]", "True");
	}

	public void test0704() {
		check("Integrate::TrigQ[Cos[(a - b)*x]]", "True");
	}

	public void test0705() {
		check("Integrate::TrigQ[Cos[(a + b)*x]]", "True");
	}

	public void test0706() {
		check("Integrate::TrigQ[sin[(a - b)*x]]", "False");
	}

	public void test0707() {
		check("Integrate::TrigQ[sin[(a + b)*x]]", "False");
	}

	public void test0708() {
		check("Integrate::TrigQ[Sin[(a - b)*x]]", "True");
	}

	public void test0709() {
		check("Integrate::TrigQ[Sin[b*x]]", "True");
	}

	public void test0710() {
		check("Integrate::TrigQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0711() {
		check("Integrate::TrigQ[Sin[(a + b)*x]]", "True");
	}

	public void test0712() {
		check("Integrate::TrigSimplify[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0713() {
		check("Integrate::TrigSimplifyAux[a*x]", "a*x");
	}

	public void test0714() {
		check("Integrate::TrigSimplifyAux[b*x]", "b*x");
	}

	public void test0715() {
		check("Integrate::TrigSimplifyAux[Cos[a*x]]", "Cos[a*x]");
	}

	public void test0716() {
		check("Integrate::TrigSimplifyAux[Sin[b*x]]", "Sin[b*x]");
	}

	public void test0717() {
		check("Integrate::TrigSimplifyAux[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0718() {
		check("Integrate::TrigSimplifyQ[Cos[a*x]*Sin[b*x]]", "False");
	}

	public void test0719() {
		check("Integrate::TrigSimplifyRecur[a]", "a");
	}

	public void test0720() {
		check("Integrate::TrigSimplifyRecur[b]", "b");
	}

	public void test0721() {
		check("Integrate::TrigSimplifyRecur[x]", "x");
	}

	public void test0722() {
		check("Integrate::TrigSimplifyRecur[a*x]", "a*x");
	}

	public void test0723() {
		check("Integrate::TrigSimplifyRecur[b*x]", "b*x");
	}

	public void test0724() {
		check("Integrate::TrigSimplifyRecur[Cos[a*x]]", "Cos[a*x]");
	}

	public void test0725() {
		check("Integrate::TrigSimplifyRecur[Sin[b*x]]", "Sin[b*x]");
	}

	public void test0726() {
		check("Integrate::TrigSimplifyRecur[Cos[a*x]*Sin[b*x]]", "Cos[a*x]*Sin[b*x]");
	}

	public void test0727() {
		check("Integrate::UnifyInertTrigFunction[sin[(a - b)*x], x]", "sin[(a - b)*x]");
	}

	public void test0728() {
		check("Integrate::UnifyInertTrigFunction[sin[(a + b)*x], x]", "sin[(a + b)*x]");
	}
	
	public void test0800() {
		check("Integrate[-Sin[(a - b)*x]/2 + Sin[(a + b)*x]/2,x]", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Integrate::ExpandTrigReduce[Cos[a*x]^1*Sin[b*x]^1,x]", //
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