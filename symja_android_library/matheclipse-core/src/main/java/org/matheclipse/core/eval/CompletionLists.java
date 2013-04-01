package org.matheclipse.core.eval;

import java.util.List;

public class CompletionLists {
  private final static String[] COMPLETIONS = { "True", "False", "Abs[]",
      "AddTo[]", "And[]", "Apart[]", "Append[]", "Apply[]", "ArcCos[]",
      "ArcSin[]", "ArcTan[]", "Arg[]", "Array[]", "AtomQ[]", "Binomial[]",
      "Blank[]", "Block[]", "Break[]", "Cases[]", "Catalan", "CatalanNumber[]",
      "Ceiling[]", "ChessboardDistance[]", "Clear[]", "ClearAll[]",
      "Complement[]", "Complex[]", "ComplexInfinity", "ComposeList[]",
      "CompoundExpression[]", "Condition[]", "Conjugate[]", "Continue[]",
      "ContinuedFraction[]", "CoprimeQ[]", "Cos[]", "Cosh[]", "Cross[]",
      "Curl[]", "D[]", "Decrement[]", "Definition[]", "Degree",
      "Denominator[]", "Depth[]", "Det[]", "DiagonalMatrix[]", "DigitQ[]",
      "Dimensions[]", "Divergence[]", "DivideBy[]", "Dot[]", "Drop[]", "E",
      "Eigenvalues[]", "Eigenvectors[]", "Equal[]", "Erf[]",
      "EuclidianDistance[]", "EulerGamma", "EulerPhi[]", "EvenQ[]", "Exp[]",
      "Expand[]", "ExpandAll[]", "ExtendedGCD[]", "Extract[]", "Factor[]",
      "Factorial[]", "Factorial2[]", "FactorInteger[]", "FactorTerms[]",
      "Fibonacci[]", "FindRoot[]", "First[]", "Fit[]", "FixedPoint[]",
      "Floor[]", "Fold[]", "FoldList[]", "For[]", "FreeQ[]",
      "FromCharacterCode[]", "FromContinuedFraction[]", "FullForm[]",
      "Function[]", "Gamma[]", "GCD[]", "Glaisher", "GoldenRatio", "Greater[]",
      "GreaterEqual[]", "GroebnerBasis[]", "HarmonicNumber[]", "Head[]",
      "HilbertMatrix[]", "Hold[]", "Horner[]", "I", "IdentityMatrix[]", "If[]",
      "Im[]", "Increment[]", "Infinity", "Inner[]", "IntegerQ[]",
      "Integrate[]", "Intersection[]", "Inverse[]", "JacobiMatrix[]",
      "JacobiSymbol[]", "Join[]", "Khinchin", "KOrderlessPartitions[]",
      "KPartitions[]", "Last[]", "LCM[]", "LeafCount$LeafCountVisitor[]",
      "LeafCount[]", "Length[]", "Less[]", "LessEqual[]", "LetterQ[]",
      "Level[]", "LinearSolve[]", "Log[]", "LowerCaseQ[]", "LUDecomposition[]",
      "ManhattanDistance[]", "Map[]", "MapAll[]", "MapThread[]",
      "MatrixPower[]", "MatrixQ[]", "Max[]", "Mean[]", "Median[]", "MemberQ[]",
      "Min[]", "Mod[]", "MoebiusMu[]", "Most[]", "Multinomial[]", "N[]",
      "Negative[]", "Nest[]", "NestList[]", "NextPrime[]", "NIntegrate[]",
      "NonCommutativeMultiply[]", "NonNegative[]", "Norm[]", "Not[]",
      "NRoots[]", "NumberPartitions[]", "NumberQ[]", "Numerator[]", "OddQ[]",
      "Or[]", "Order[]", "OrderedQ[]", "Out[]", "Outer[]", "Package[]",
      "ParametricPlot[]", "Part[]", "Partition[]", "Pattern[]",
      "Permutations[]", "Pi", "Plot[]", "Plot3D[]", "Plus[]",
      "PolynomialGCD[]", "PolynomialQ[]", "PolynomialQuotient[]",
      "PolynomialQuotientRemainder[]", "PolynomialRemainder[]", "Position[]",
      "Positive[]", "Power[]", "PowerMod[]", "PreDecrement[]",
      "PreIncrement[]", "Prepend[]", "PrimeQ[]", "PrimitiveRoots[]", "Print[]",
      "Product[]", "Quotient[]", "Range[]", "Rational[]", "Rationalize[]",
      "Re[]", "ReplaceAll[]", "Rest[]", "Return[]", "Reverse[]", "Roots[]",
      "RotateLeft[]", "RotateRight[]", "Rule[]", "SameQ[]", "Select[]",
      "Set[]", "SetAttributes[]", "SetDelayed[]", "Sign[]", "SignCmp[]",
      "Sin[]", "SingularValueDecomposition[]", "Sinh[]", "Sort[]", "Sqrt[]",
      "SquaredEuclidianDistance[]", "StringDrop[]", "StringJoin[]",
      "StringLength[]", "StringTake[]", "Subsets[]", "SubtractFrom[]", "Sum[]",
      "SyntaxLength[]", "SyntaxQ[]", "Table[]", "Take[]", "Tan[]", "Tanh[]",
      "Taylor[]", "Thread[]", "Through[]", "Times[]", "TimesBy[]", "Timing[]",
      "ToCharacterCode[]", "Together[]", "ToString[]", "Total[]",
      "ToUnicode[]", "Tr[]", "Trace[]", "Transpose[]", "TrueQ[]", "Trunc[]",
      "Unequal[]", "Union[]", "UnsameQ[]", "UpperCaseQ[]",
      "VandermondeMatrix[]", "Variables[]", "VectorQ[]", "While[]" };
  List<String> fReplaceWords;
  List<String> fWords;

  public CompletionLists(List<String> words, List<String> replaceWords) {
    fWords = words;
    fReplaceWords = replaceWords;
    for (int i = 0; i < COMPLETIONS.length; i++) {
      fWords.add(COMPLETIONS[i].toLowerCase());
      fReplaceWords.add(COMPLETIONS[i]);
    }
  }

}
