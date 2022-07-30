package org.matheclipse.core.expression;

import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.interfaces.IAST;

/**
 * Automatically generated with class
 * <code>org.matheclipse.core.preprocessor.JavaObjectFunctionGenerator</code>. Don't change
 * manually.
 */
public class J extends S {

  public static IAST plus(final Object... a) {
    switch (a.length) {
      case 1:
        return new AST1(Plus, Object2Expr.convert(a[0]));
      case 2:
        return new B2.Plus(Object2Expr.convert(a[0]), Object2Expr.convert(a[1]));
      case 3:
        return new AST3(Plus, Object2Expr.convert(a[0]), Object2Expr.convert(a[1]),
            Object2Expr.convert(a[2]));
      default:
        return new AST(Plus, Object2Expr.convertArray(a, false, false));
    }
  }

  public static IAST times(final Object... a) {
    switch (a.length) {
      case 1:
        return new AST1(Times, Object2Expr.convert(a[0]));
      case 2:
        return new B2.Times(Object2Expr.convert(a[0]), Object2Expr.convert(a[1]));
      case 3:
        return new AST3(Times, Object2Expr.convert(a[0]), Object2Expr.convert(a[1]),
            Object2Expr.convert(a[2]));
      default:
        return new AST(Times, Object2Expr.convertArray(a, false, false));
    }
  }

  public static IAST abs(final Object a1) {
    return new AST1(Abs, Object2Expr.convert(a1));
  }

  public static IAST absoluteCorrelation(final Object a1) {
    return new AST1(AbsoluteCorrelation, Object2Expr.convert(a1));
  }

  public static IAST absoluteCorrelation(final Object a1, final Object a2) {
    return new AST2(AbsoluteCorrelation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST absoluteTiming(final Object a1) {
    return new AST1(AbsoluteTiming, Object2Expr.convert(a1));
  }

  public static IAST accumulate(final Object a1) {
    return new AST1(Accumulate, Object2Expr.convert(a1));
  }

  public static IAST addSides(final Object a1) {
    return new AST1(AddSides, Object2Expr.convert(a1));
  }

  public static IAST addSides(final Object a1, final Object a2) {
    return new AST2(AddSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST addTo(final Object a1) {
    return new AST1(AddTo, Object2Expr.convert(a1));
  }

  public static IAST addTo(final Object a1, final Object a2) {
    return new AST2(AddTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST addToClassPath(final Object a1) {
    return new AST1(AddToClassPath, Object2Expr.convert(a1));
  }

  public static IAST addToClassPath(final Object a1, final Object a2) {
    return new AST2(AddToClassPath, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST addToClassPath(final Object a1, final Object a2, final Object a3) {
    return new AST3(AddToClassPath, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST adjacencyMatrix(final Object a1) {
    return new AST1(AdjacencyMatrix, Object2Expr.convert(a1));
  }

  public static IAST airyAi(final Object a1) {
    return new AST1(AiryAi, Object2Expr.convert(a1));
  }

  public static IAST airyAiPrime(final Object a1) {
    return new AST1(AiryAiPrime, Object2Expr.convert(a1));
  }

  public static IAST airyBi(final Object a1) {
    return new AST1(AiryBi, Object2Expr.convert(a1));
  }

  public static IAST airyBiPrime(final Object a1) {
    return new AST1(AiryBiPrime, Object2Expr.convert(a1));
  }

  public static IAST allTrue(final Object a1) {
    return new AST1(AllTrue, Object2Expr.convert(a1));
  }

  public static IAST allTrue(final Object a1, final Object a2) {
    return new AST2(AllTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST alphabet(final Object a1) {
    return new AST1(Alphabet, Object2Expr.convert(a1));
  }

  public static IAST angleVector(final Object a1) {
    return new AST1(AngleVector, Object2Expr.convert(a1));
  }

  public static IAST angleVector(final Object a1, final Object a2) {
    return new AST2(AngleVector, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST annuity(final Object a1) {
    return new AST1(Annuity, Object2Expr.convert(a1));
  }

  public static IAST annuity(final Object a1, final Object a2) {
    return new AST2(Annuity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST annuity(final Object a1, final Object a2, final Object a3) {
    return new AST3(Annuity, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST annuityDue(final Object a1) {
    return new AST1(AnnuityDue, Object2Expr.convert(a1));
  }

  public static IAST annuityDue(final Object a1, final Object a2) {
    return new AST2(AnnuityDue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST annuityDue(final Object a1, final Object a2, final Object a3) {
    return new AST3(AnnuityDue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST antihermitianMatrixQ(final Object a1) {
    return new AST1(AntihermitianMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST antihermitianMatrixQ(final Object a1, final Object a2) {
    return new AST2(AntihermitianMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST antisymmetricMatrixQ(final Object a1) {
    return new AST1(AntisymmetricMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST antisymmetricMatrixQ(final Object a1, final Object a2) {
    return new AST2(AntisymmetricMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST anyTrue(final Object a1) {
    return new AST1(AnyTrue, Object2Expr.convert(a1));
  }

  public static IAST anyTrue(final Object a1, final Object a2) {
    return new AST2(AnyTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST apart(final Object a1) {
    return new AST1(Apart, Object2Expr.convert(a1));
  }

  public static IAST apart(final Object a1, final Object a2) {
    return new AST2(Apart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST append(final Object a1) {
    return new AST1(Append, Object2Expr.convert(a1));
  }

  public static IAST append(final Object a1, final Object a2) {
    return new AST2(Append, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST appendTo(final Object a1) {
    return new AST1(AppendTo, Object2Expr.convert(a1));
  }

  public static IAST appendTo(final Object a1, final Object a2) {
    return new AST2(AppendTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST apply(final Object a1) {
    return new AST1(Apply, Object2Expr.convert(a1));
  }

  public static IAST apply(final Object a1, final Object a2) {
    return new AST2(Apply, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST apply(final Object a1, final Object a2, final Object a3) {
    return new AST3(Apply, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST applySides(final Object a1) {
    return new AST1(ApplySides, Object2Expr.convert(a1));
  }

  public static IAST applySides(final Object a1, final Object a2) {
    return new AST2(ApplySides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arcCos(final Object a1) {
    return new AST1(ArcCos, Object2Expr.convert(a1));
  }

  public static IAST arcCosh(final Object a1) {
    return new AST1(ArcCosh, Object2Expr.convert(a1));
  }

  public static IAST arcCot(final Object a1) {
    return new AST1(ArcCot, Object2Expr.convert(a1));
  }

  public static IAST arcCoth(final Object a1) {
    return new AST1(ArcCoth, Object2Expr.convert(a1));
  }

  public static IAST arcCsc(final Object a1) {
    return new AST1(ArcCsc, Object2Expr.convert(a1));
  }

  public static IAST arcCsch(final Object a1) {
    return new AST1(ArcCsch, Object2Expr.convert(a1));
  }

  public static IAST arcSec(final Object a1) {
    return new AST1(ArcSec, Object2Expr.convert(a1));
  }

  public static IAST arcSech(final Object a1) {
    return new AST1(ArcSech, Object2Expr.convert(a1));
  }

  public static IAST arcSin(final Object a1) {
    return new AST1(ArcSin, Object2Expr.convert(a1));
  }

  public static IAST arcSinh(final Object a1) {
    return new AST1(ArcSinh, Object2Expr.convert(a1));
  }

  public static IAST arcTan(final Object a1) {
    return new AST1(ArcTan, Object2Expr.convert(a1));
  }

  public static IAST arcTan(final Object a1, final Object a2) {
    return new AST2(ArcTan, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arcTanh(final Object a1) {
    return new AST1(ArcTanh, Object2Expr.convert(a1));
  }

  public static IAST arg(final Object a1) {
    return new AST1(Arg, Object2Expr.convert(a1));
  }

  public static IAST argMax(final Object a1) {
    return new AST1(ArgMax, Object2Expr.convert(a1));
  }

  public static IAST argMax(final Object a1, final Object a2) {
    return new AST2(ArgMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST argMin(final Object a1) {
    return new AST1(ArgMin, Object2Expr.convert(a1));
  }

  public static IAST argMin(final Object a1, final Object a2) {
    return new AST2(ArgMin, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arithmeticGeometricMean(final Object a1) {
    return new AST1(ArithmeticGeometricMean, Object2Expr.convert(a1));
  }

  public static IAST arithmeticGeometricMean(final Object a1, final Object a2) {
    return new AST2(ArithmeticGeometricMean, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST array(final Object a1) {
    return new AST1(Array, Object2Expr.convert(a1));
  }

  public static IAST array(final Object a1, final Object a2) {
    return new AST2(Array, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST array(final Object a1, final Object a2, final Object a3) {
    return new AST3(Array, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST arrayDepth(final Object a1) {
    return new AST1(ArrayDepth, Object2Expr.convert(a1));
  }

  public static IAST arrayPad(final Object a1) {
    return new AST1(ArrayPad, Object2Expr.convert(a1));
  }

  public static IAST arrayPad(final Object a1, final Object a2) {
    return new AST2(ArrayPad, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arrayPad(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayPad, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST arrayQ(final Object a1) {
    return new AST1(ArrayQ, Object2Expr.convert(a1));
  }

  public static IAST arrayQ(final Object a1, final Object a2) {
    return new AST2(ArrayQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arrayQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST arrayReshape(final Object a1) {
    return new AST1(ArrayReshape, Object2Expr.convert(a1));
  }

  public static IAST arrayReshape(final Object a1, final Object a2) {
    return new AST2(ArrayReshape, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arrayReshape(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayReshape, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST arrayRules(final Object a1) {
    return new AST1(ArrayRules, Object2Expr.convert(a1));
  }

  public static IAST arrayRules(final Object a1, final Object a2) {
    return new AST2(ArrayRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arrow(final Object a1) {
    return new AST1(Arrow, Object2Expr.convert(a1));
  }

  public static IAST arrow(final Object a1, final Object a2) {
    return new AST2(Arrow, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST arrow(final Object a1, final Object a2, final Object a3) {
    return new AST3(Arrow, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST associateTo(final Object a1) {
    return new AST1(AssociateTo, Object2Expr.convert(a1));
  }

  public static IAST associateTo(final Object a1, final Object a2) {
    return new AST2(AssociateTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST associationMap(final Object a1) {
    return new AST1(AssociationMap, Object2Expr.convert(a1));
  }

  public static IAST associationMap(final Object a1, final Object a2) {
    return new AST2(AssociationMap, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST associationQ(final Object a1) {
    return new AST1(AssociationQ, Object2Expr.convert(a1));
  }

  public static IAST associationThread(final Object a1) {
    return new AST1(AssociationThread, Object2Expr.convert(a1));
  }

  public static IAST associationThread(final Object a1, final Object a2) {
    return new AST2(AssociationThread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST assuming(final Object a1) {
    return new AST1(Assuming, Object2Expr.convert(a1));
  }

  public static IAST assuming(final Object a1, final Object a2) {
    return new AST2(Assuming, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST atomQ(final Object a1) {
    return new AST1(AtomQ, Object2Expr.convert(a1));
  }

  public static IAST attributes(final Object a1) {
    return new AST1(Attributes, Object2Expr.convert(a1));
  }

  public static IAST bartlettWindow(final Object a1) {
    return new AST1(BartlettWindow, Object2Expr.convert(a1));
  }

  public static IAST baseDecode(final Object a1) {
    return new AST1(BaseDecode, Object2Expr.convert(a1));
  }

  public static IAST baseEncode(final Object a1) {
    return new AST1(BaseEncode, Object2Expr.convert(a1));
  }

  public static IAST baseForm(final Object a1) {
    return new AST1(BaseForm, Object2Expr.convert(a1));
  }

  public static IAST baseForm(final Object a1, final Object a2) {
    return new AST2(BaseForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST begin(final Object a1) {
    return new AST1(Begin, Object2Expr.convert(a1));
  }

  public static IAST beginPackage(final Object a1) {
    return new AST1(BeginPackage, Object2Expr.convert(a1));
  }

  public static IAST beginPackage(final Object a1, final Object a2) {
    return new AST2(BeginPackage, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bellB(final Object a1) {
    return new AST1(BellB, Object2Expr.convert(a1));
  }

  public static IAST bellB(final Object a1, final Object a2) {
    return new AST2(BellB, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bellY(final Object a1) {
    return new AST1(BellY, Object2Expr.convert(a1));
  }

  public static IAST bellY(final Object a1, final Object a2) {
    return new AST2(BellY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bellY(final Object a1, final Object a2, final Object a3) {
    return new AST3(BellY, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST bernoulliB(final Object a1) {
    return new AST1(BernoulliB, Object2Expr.convert(a1));
  }

  public static IAST bernoulliB(final Object a1, final Object a2) {
    return new AST2(BernoulliB, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bernoulliDistribution(final Object a1) {
    return new AST1(BernoulliDistribution, Object2Expr.convert(a1));
  }

  public static IAST bernsteinBasis(final Object a1) {
    return new AST1(BernsteinBasis, Object2Expr.convert(a1));
  }

  public static IAST bernsteinBasis(final Object a1, final Object a2) {
    return new AST2(BernsteinBasis, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bernsteinBasis(final Object a1, final Object a2, final Object a3) {
    return new AST3(BernsteinBasis, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST besselI(final Object a1) {
    return new AST1(BesselI, Object2Expr.convert(a1));
  }

  public static IAST besselI(final Object a1, final Object a2) {
    return new AST2(BesselI, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST besselJ(final Object a1) {
    return new AST1(BesselJ, Object2Expr.convert(a1));
  }

  public static IAST besselJ(final Object a1, final Object a2) {
    return new AST2(BesselJ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST besselJZero(final Object a1) {
    return new AST1(BesselJZero, Object2Expr.convert(a1));
  }

  public static IAST besselJZero(final Object a1, final Object a2) {
    return new AST2(BesselJZero, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST besselK(final Object a1) {
    return new AST1(BesselK, Object2Expr.convert(a1));
  }

  public static IAST besselK(final Object a1, final Object a2) {
    return new AST2(BesselK, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST besselY(final Object a1) {
    return new AST1(BesselY, Object2Expr.convert(a1));
  }

  public static IAST besselY(final Object a1, final Object a2) {
    return new AST2(BesselY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST besselYZero(final Object a1) {
    return new AST1(BesselYZero, Object2Expr.convert(a1));
  }

  public static IAST besselYZero(final Object a1, final Object a2) {
    return new AST2(BesselYZero, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST beta(final Object a1) {
    return new AST1(Beta, Object2Expr.convert(a1));
  }

  public static IAST beta(final Object a1, final Object a2) {
    return new AST2(Beta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST beta(final Object a1, final Object a2, final Object a3) {
    return new AST3(Beta, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST binCounts(final Object a1) {
    return new AST1(BinCounts, Object2Expr.convert(a1));
  }

  public static IAST binCounts(final Object a1, final Object a2) {
    return new AST2(BinCounts, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST binaryDistance(final Object a1) {
    return new AST1(BinaryDistance, Object2Expr.convert(a1));
  }

  public static IAST binaryDistance(final Object a1, final Object a2) {
    return new AST2(BinaryDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST binaryRead(final Object a1) {
    return new AST1(BinaryRead, Object2Expr.convert(a1));
  }

  public static IAST binaryRead(final Object a1, final Object a2) {
    return new AST2(BinaryRead, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST binaryWrite(final Object a1) {
    return new AST1(BinaryWrite, Object2Expr.convert(a1));
  }

  public static IAST binaryWrite(final Object a1, final Object a2) {
    return new AST2(BinaryWrite, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST binaryWrite(final Object a1, final Object a2, final Object a3) {
    return new AST3(BinaryWrite, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST binomial(final Object a1) {
    return new AST1(Binomial, Object2Expr.convert(a1));
  }

  public static IAST binomial(final Object a1, final Object a2) {
    return new AST2(Binomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST binomialDistribution(final Object a1) {
    return new AST1(BinomialDistribution, Object2Expr.convert(a1));
  }

  public static IAST binomialDistribution(final Object a1, final Object a2) {
    return new AST2(BinomialDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bioSequence(final Object a1) {
    return new AST1(BioSequence, Object2Expr.convert(a1));
  }

  public static IAST bioSequence(final Object a1, final Object a2) {
    return new AST2(BioSequence, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST bioSequenceQ(final Object a1) {
    return new AST1(BioSequenceQ, Object2Expr.convert(a1));
  }

  public static IAST bitLength(final Object a1) {
    return new AST1(BitLength, Object2Expr.convert(a1));
  }

  public static IAST blackmanHarrisWindow(final Object a1) {
    return new AST1(BlackmanHarrisWindow, Object2Expr.convert(a1));
  }

  public static IAST blackmanNuttallWindow(final Object a1) {
    return new AST1(BlackmanNuttallWindow, Object2Expr.convert(a1));
  }

  public static IAST blackmanWindow(final Object a1) {
    return new AST1(BlackmanWindow, Object2Expr.convert(a1));
  }

  public static IAST blank(final Object a1) {
    return new AST1(Blank, Object2Expr.convert(a1));
  }

  public static IAST blankNullSequence(final Object a1) {
    return new AST1(BlankNullSequence, Object2Expr.convert(a1));
  }

  public static IAST blankSequence(final Object a1) {
    return new AST1(BlankSequence, Object2Expr.convert(a1));
  }

  public static IAST block(final Object a1) {
    return new AST1(Block, Object2Expr.convert(a1));
  }

  public static IAST block(final Object a1, final Object a2) {
    return new AST2(Block, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST boole(final Object a1) {
    return new AST1(Boole, Object2Expr.convert(a1));
  }

  public static IAST booleanConvert(final Object a1) {
    return new AST1(BooleanConvert, Object2Expr.convert(a1));
  }

  public static IAST booleanConvert(final Object a1, final Object a2) {
    return new AST2(BooleanConvert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST booleanMaxterms(final Object a1) {
    return new AST1(BooleanMaxterms, Object2Expr.convert(a1));
  }

  public static IAST booleanMaxterms(final Object a1, final Object a2) {
    return new AST2(BooleanMaxterms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST booleanMinimize(final Object a1) {
    return new AST1(BooleanMinimize, Object2Expr.convert(a1));
  }

  public static IAST booleanMinimize(final Object a1, final Object a2) {
    return new AST2(BooleanMinimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST booleanMinterms(final Object a1) {
    return new AST1(BooleanMinterms, Object2Expr.convert(a1));
  }

  public static IAST booleanMinterms(final Object a1, final Object a2) {
    return new AST2(BooleanMinterms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST booleanQ(final Object a1) {
    return new AST1(BooleanQ, Object2Expr.convert(a1));
  }

  public static IAST booleanTable(final Object a1) {
    return new AST1(BooleanTable, Object2Expr.convert(a1));
  }

  public static IAST booleanTable(final Object a1, final Object a2) {
    return new AST2(BooleanTable, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST booleanVariables(final Object a1) {
    return new AST1(BooleanVariables, Object2Expr.convert(a1));
  }

  public static IAST brayCurtisDistance(final Object a1) {
    return new AST1(BrayCurtisDistance, Object2Expr.convert(a1));
  }

  public static IAST brayCurtisDistance(final Object a1, final Object a2) {
    return new AST2(BrayCurtisDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST byteArrayQ(final Object a1) {
    return new AST1(ByteArrayQ, Object2Expr.convert(a1));
  }

  public static IAST byteArrayToString(final Object a1) {
    return new AST1(ByteArrayToString, Object2Expr.convert(a1));
  }

  public static IAST byteCount(final Object a1) {
    return new AST1(ByteCount, Object2Expr.convert(a1));
  }

  public static IAST cdf(final Object a1) {
    return new AST1(CDF, Object2Expr.convert(a1));
  }

  public static IAST cdf(final Object a1, final Object a2) {
    return new AST2(CDF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cForm(final Object a1) {
    return new AST1(CForm, Object2Expr.convert(a1));
  }

  public static IAST canberraDistance(final Object a1) {
    return new AST1(CanberraDistance, Object2Expr.convert(a1));
  }

  public static IAST canberraDistance(final Object a1, final Object a2) {
    return new AST2(CanberraDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cancel(final Object a1) {
    return new AST1(Cancel, Object2Expr.convert(a1));
  }

  public static IAST carlsonRC(final Object a1) {
    return new AST1(CarlsonRC, Object2Expr.convert(a1));
  }

  public static IAST carlsonRC(final Object a1, final Object a2) {
    return new AST2(CarlsonRC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST carlsonRD(final Object a1) {
    return new AST1(CarlsonRD, Object2Expr.convert(a1));
  }

  public static IAST carlsonRD(final Object a1, final Object a2) {
    return new AST2(CarlsonRD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST carlsonRD(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST carlsonRF(final Object a1) {
    return new AST1(CarlsonRF, Object2Expr.convert(a1));
  }

  public static IAST carlsonRF(final Object a1, final Object a2) {
    return new AST2(CarlsonRF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST carlsonRF(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRF, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST carlsonRG(final Object a1) {
    return new AST1(CarlsonRG, Object2Expr.convert(a1));
  }

  public static IAST carlsonRG(final Object a1, final Object a2) {
    return new AST2(CarlsonRG, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST carlsonRG(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRG, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST carmichaelLambda(final Object a1) {
    return new AST1(CarmichaelLambda, Object2Expr.convert(a1));
  }

  public static IAST cartesianProduct(final Object a1) {
    return new AST1(CartesianProduct, Object2Expr.convert(a1));
  }

  public static IAST cartesianProduct(final Object a1, final Object a2) {
    return new AST2(CartesianProduct, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cartesianProduct(final Object a1, final Object a2, final Object a3) {
    return new AST3(CartesianProduct, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST cases(final Object a1) {
    return new AST1(Cases, Object2Expr.convert(a1));
  }

  public static IAST cases(final Object a1, final Object a2) {
    return new AST2(Cases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cases(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST catalanNumber(final Object a1) {
    return new AST1(CatalanNumber, Object2Expr.convert(a1));
  }

  public static IAST $catch(final Object a1) {
    return new AST1(Catch, Object2Expr.convert(a1));
  }

  public static IAST $catch(final Object a1, final Object a2) {
    return new AST2(Catch, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $catch(final Object a1, final Object a2, final Object a3) {
    return new AST3(Catch, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST catenate(final Object a1) {
    return new AST1(Catenate, Object2Expr.convert(a1));
  }

  public static IAST cauchyDistribution(final Object a1) {
    return new AST1(CauchyDistribution, Object2Expr.convert(a1));
  }

  public static IAST cauchyDistribution(final Object a1, final Object a2) {
    return new AST2(CauchyDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ceiling(final Object a1) {
    return new AST1(Ceiling, Object2Expr.convert(a1));
  }

  public static IAST ceiling(final Object a1, final Object a2) {
    return new AST2(Ceiling, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST centralMoment(final Object a1) {
    return new AST1(CentralMoment, Object2Expr.convert(a1));
  }

  public static IAST centralMoment(final Object a1, final Object a2) {
    return new AST2(CentralMoment, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST characterRange(final Object a1) {
    return new AST1(CharacterRange, Object2Expr.convert(a1));
  }

  public static IAST characterRange(final Object a1, final Object a2) {
    return new AST2(CharacterRange, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST characteristicPolynomial(final Object a1) {
    return new AST1(CharacteristicPolynomial, Object2Expr.convert(a1));
  }

  public static IAST characteristicPolynomial(final Object a1, final Object a2) {
    return new AST2(CharacteristicPolynomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST characters(final Object a1) {
    return new AST1(Characters, Object2Expr.convert(a1));
  }

  public static IAST chebyshevT(final Object a1) {
    return new AST1(ChebyshevT, Object2Expr.convert(a1));
  }

  public static IAST chebyshevT(final Object a1, final Object a2) {
    return new AST2(ChebyshevT, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST chebyshevU(final Object a1) {
    return new AST1(ChebyshevU, Object2Expr.convert(a1));
  }

  public static IAST chebyshevU(final Object a1, final Object a2) {
    return new AST2(ChebyshevU, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST check(final Object a1) {
    return new AST1(Check, Object2Expr.convert(a1));
  }

  public static IAST check(final Object a1, final Object a2) {
    return new AST2(Check, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST check(final Object a1, final Object a2, final Object a3) {
    return new AST3(Check, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST checkAbort(final Object a1) {
    return new AST1(CheckAbort, Object2Expr.convert(a1));
  }

  public static IAST checkAbort(final Object a1, final Object a2) {
    return new AST2(CheckAbort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST chessboardDistance(final Object a1) {
    return new AST1(ChessboardDistance, Object2Expr.convert(a1));
  }

  public static IAST chessboardDistance(final Object a1, final Object a2) {
    return new AST2(ChessboardDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST chineseRemainder(final Object a1) {
    return new AST1(ChineseRemainder, Object2Expr.convert(a1));
  }

  public static IAST chineseRemainder(final Object a1, final Object a2) {
    return new AST2(ChineseRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST choleskyDecomposition(final Object a1) {
    return new AST1(CholeskyDecomposition, Object2Expr.convert(a1));
  }

  public static IAST chop(final Object a1) {
    return new AST1(Chop, Object2Expr.convert(a1));
  }

  public static IAST chop(final Object a1, final Object a2) {
    return new AST2(Chop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST circle(final Object a1) {
    return new AST1(Circle, Object2Expr.convert(a1));
  }

  public static IAST circle(final Object a1, final Object a2) {
    return new AST2(Circle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST circle(final Object a1, final Object a2, final Object a3) {
    return new AST3(Circle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST circlePoints(final Object a1) {
    return new AST1(CirclePoints, Object2Expr.convert(a1));
  }

  public static IAST clip(final Object a1) {
    return new AST1(Clip, Object2Expr.convert(a1));
  }

  public static IAST clip(final Object a1, final Object a2) {
    return new AST2(Clip, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST clip(final Object a1, final Object a2, final Object a3) {
    return new AST3(Clip, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST close(final Object a1) {
    return new AST1(Close, Object2Expr.convert(a1));
  }

  public static IAST close(final Object a1, final Object a2) {
    return new AST2(Close, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST closenessCentrality(final Object a1) {
    return new AST1(ClosenessCentrality, Object2Expr.convert(a1));
  }

  public static IAST coefficient(final Object a1) {
    return new AST1(Coefficient, Object2Expr.convert(a1));
  }

  public static IAST coefficient(final Object a1, final Object a2) {
    return new AST2(Coefficient, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST coefficient(final Object a1, final Object a2, final Object a3) {
    return new AST3(Coefficient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST coefficientList(final Object a1) {
    return new AST1(CoefficientList, Object2Expr.convert(a1));
  }

  public static IAST coefficientList(final Object a1, final Object a2) {
    return new AST2(CoefficientList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST coefficientRules(final Object a1) {
    return new AST1(CoefficientRules, Object2Expr.convert(a1));
  }

  public static IAST coefficientRules(final Object a1, final Object a2) {
    return new AST2(CoefficientRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST coefficientRules(final Object a1, final Object a2, final Object a3) {
    return new AST3(CoefficientRules, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST cofactor(final Object a1) {
    return new AST1(Cofactor, Object2Expr.convert(a1));
  }

  public static IAST cofactor(final Object a1, final Object a2) {
    return new AST2(Cofactor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST collect(final Object a1) {
    return new AST1(Collect, Object2Expr.convert(a1));
  }

  public static IAST collect(final Object a1, final Object a2) {
    return new AST2(Collect, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST collect(final Object a1, final Object a2, final Object a3) {
    return new AST3(Collect, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST collinearPoints(final Object a1) {
    return new AST1(CollinearPoints, Object2Expr.convert(a1));
  }

  public static IAST commonest(final Object a1) {
    return new AST1(Commonest, Object2Expr.convert(a1));
  }

  public static IAST commonest(final Object a1, final Object a2) {
    return new AST2(Commonest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST compile(final Object a1) {
    return new AST1(Compile, Object2Expr.convert(a1));
  }

  public static IAST compile(final Object a1, final Object a2) {
    return new AST2(Compile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST compile(final Object a1, final Object a2, final Object a3) {
    return new AST3(Compile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST compilePrint(final Object a1) {
    return new AST1(CompilePrint, Object2Expr.convert(a1));
  }

  public static IAST compilePrint(final Object a1, final Object a2) {
    return new AST2(CompilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST compilePrint(final Object a1, final Object a2, final Object a3) {
    return new AST3(CompilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST complement(final Object a1) {
    return new AST1(Complement, Object2Expr.convert(a1));
  }

  public static IAST complement(final Object a1, final Object a2) {
    return new AST2(Complement, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST complement(final Object a1, final Object a2, final Object a3) {
    return new AST3(Complement, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST completeGraph(final Object a1) {
    return new AST1(CompleteGraph, Object2Expr.convert(a1));
  }

  public static IAST complex(final Object a1) {
    return new AST1(Complex, Object2Expr.convert(a1));
  }

  public static IAST complex(final Object a1, final Object a2) {
    return new AST2(Complex, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST complexExpand(final Object a1) {
    return new AST1(ComplexExpand, Object2Expr.convert(a1));
  }

  public static IAST complexExpand(final Object a1, final Object a2) {
    return new AST2(ComplexExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST compoundExpression(final Object a1) {
    return new AST1(CompoundExpression, Object2Expr.convert(a1));
  }

  public static IAST compoundExpression(final Object a1, final Object a2) {
    return new AST2(CompoundExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST compoundExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(CompoundExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST condition(final Object a1) {
    return new AST1(Condition, Object2Expr.convert(a1));
  }

  public static IAST condition(final Object a1, final Object a2) {
    return new AST2(Condition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST conditionalExpression(final Object a1) {
    return new AST1(ConditionalExpression, Object2Expr.convert(a1));
  }

  public static IAST conditionalExpression(final Object a1, final Object a2) {
    return new AST2(ConditionalExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cone(final Object a1) {
    return new AST1(Cone, Object2Expr.convert(a1));
  }

  public static IAST cone(final Object a1, final Object a2) {
    return new AST2(Cone, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST conjugate(final Object a1) {
    return new AST1(Conjugate, Object2Expr.convert(a1));
  }

  public static IAST conjugateTranspose(final Object a1) {
    return new AST1(ConjugateTranspose, Object2Expr.convert(a1));
  }

  public static IAST conjugateTranspose(final Object a1, final Object a2) {
    return new AST2(ConjugateTranspose, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST constantArray(final Object a1) {
    return new AST1(ConstantArray, Object2Expr.convert(a1));
  }

  public static IAST constantArray(final Object a1, final Object a2) {
    return new AST2(ConstantArray, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST containsAll(final Object a1) {
    return new AST1(ContainsAll, Object2Expr.convert(a1));
  }

  public static IAST containsAll(final Object a1, final Object a2) {
    return new AST2(ContainsAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST containsAny(final Object a1) {
    return new AST1(ContainsAny, Object2Expr.convert(a1));
  }

  public static IAST containsAny(final Object a1, final Object a2) {
    return new AST2(ContainsAny, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST containsExactly(final Object a1) {
    return new AST1(ContainsExactly, Object2Expr.convert(a1));
  }

  public static IAST containsExactly(final Object a1, final Object a2) {
    return new AST2(ContainsExactly, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST containsNone(final Object a1) {
    return new AST1(ContainsNone, Object2Expr.convert(a1));
  }

  public static IAST containsNone(final Object a1, final Object a2) {
    return new AST2(ContainsNone, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST containsOnly(final Object a1) {
    return new AST1(ContainsOnly, Object2Expr.convert(a1));
  }

  public static IAST containsOnly(final Object a1, final Object a2) {
    return new AST2(ContainsOnly, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST context(final Object a1) {
    return new AST1(Context, Object2Expr.convert(a1));
  }

  public static IAST $continue(final Object a1) {
    return new AST1(Continue, Object2Expr.convert(a1));
  }

  public static IAST continuedFraction(final Object a1) {
    return new AST1(ContinuedFraction, Object2Expr.convert(a1));
  }

  public static IAST continuedFraction(final Object a1, final Object a2) {
    return new AST2(ContinuedFraction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST convergents(final Object a1) {
    return new AST1(Convergents, Object2Expr.convert(a1));
  }

  public static IAST convexHullMesh(final Object a1) {
    return new AST1(ConvexHullMesh, Object2Expr.convert(a1));
  }

  public static IAST coordinateBoundingBox(final Object a1) {
    return new AST1(CoordinateBoundingBox, Object2Expr.convert(a1));
  }

  public static IAST coordinateBoundingBox(final Object a1, final Object a2) {
    return new AST2(CoordinateBoundingBox, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST coplanarPoints(final Object a1) {
    return new AST1(CoplanarPoints, Object2Expr.convert(a1));
  }

  public static IAST correlation(final Object a1) {
    return new AST1(Correlation, Object2Expr.convert(a1));
  }

  public static IAST correlation(final Object a1, final Object a2) {
    return new AST2(Correlation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST correlationDistance(final Object a1) {
    return new AST1(CorrelationDistance, Object2Expr.convert(a1));
  }

  public static IAST correlationDistance(final Object a1, final Object a2) {
    return new AST2(CorrelationDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cos(final Object a1) {
    return new AST1(Cos, Object2Expr.convert(a1));
  }

  public static IAST cosIntegral(final Object a1) {
    return new AST1(CosIntegral, Object2Expr.convert(a1));
  }

  public static IAST cosh(final Object a1) {
    return new AST1(Cosh, Object2Expr.convert(a1));
  }

  public static IAST coshIntegral(final Object a1) {
    return new AST1(CoshIntegral, Object2Expr.convert(a1));
  }

  public static IAST cosineDistance(final Object a1) {
    return new AST1(CosineDistance, Object2Expr.convert(a1));
  }

  public static IAST cosineDistance(final Object a1, final Object a2) {
    return new AST2(CosineDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cot(final Object a1) {
    return new AST1(Cot, Object2Expr.convert(a1));
  }

  public static IAST coth(final Object a1) {
    return new AST1(Coth, Object2Expr.convert(a1));
  }

  public static IAST count(final Object a1) {
    return new AST1(Count, Object2Expr.convert(a1));
  }

  public static IAST count(final Object a1, final Object a2) {
    return new AST2(Count, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST count(final Object a1, final Object a2, final Object a3) {
    return new AST3(Count, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST countDistinct(final Object a1) {
    return new AST1(CountDistinct, Object2Expr.convert(a1));
  }

  public static IAST counts(final Object a1) {
    return new AST1(Counts, Object2Expr.convert(a1));
  }

  public static IAST covariance(final Object a1) {
    return new AST1(Covariance, Object2Expr.convert(a1));
  }

  public static IAST covariance(final Object a1, final Object a2) {
    return new AST2(Covariance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST createDirectory(final Object a1) {
    return new AST1(CreateDirectory, Object2Expr.convert(a1));
  }

  public static IAST createFile(final Object a1) {
    return new AST1(CreateFile, Object2Expr.convert(a1));
  }

  public static IAST cross(final Object a1) {
    return new AST1(Cross, Object2Expr.convert(a1));
  }

  public static IAST cross(final Object a1, final Object a2) {
    return new AST2(Cross, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cross(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cross, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST csc(final Object a1) {
    return new AST1(Csc, Object2Expr.convert(a1));
  }

  public static IAST csch(final Object a1) {
    return new AST1(Csch, Object2Expr.convert(a1));
  }

  public static IAST cubeRoot(final Object a1) {
    return new AST1(CubeRoot, Object2Expr.convert(a1));
  }

  public static IAST cuboid(final Object a1) {
    return new AST1(Cuboid, Object2Expr.convert(a1));
  }

  public static IAST cuboid(final Object a1, final Object a2) {
    return new AST2(Cuboid, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cuboid(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cuboid, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST curl(final Object a1) {
    return new AST1(Curl, Object2Expr.convert(a1));
  }

  public static IAST curl(final Object a1, final Object a2) {
    return new AST2(Curl, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cycleGraph(final Object a1) {
    return new AST1(CycleGraph, Object2Expr.convert(a1));
  }

  public static IAST cycles(final Object a1) {
    return new AST1(Cycles, Object2Expr.convert(a1));
  }

  public static IAST cyclotomic(final Object a1) {
    return new AST1(Cyclotomic, Object2Expr.convert(a1));
  }

  public static IAST cyclotomic(final Object a1, final Object a2) {
    return new AST2(Cyclotomic, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST cylinder(final Object a1) {
    return new AST1(Cylinder, Object2Expr.convert(a1));
  }

  public static IAST cylinder(final Object a1, final Object a2) {
    return new AST2(Cylinder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST d(final Object a1) {
    return new AST1(D, Object2Expr.convert(a1));
  }

  public static IAST d(final Object a1, final Object a2) {
    return new AST2(D, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST d(final Object a1, final Object a2, final Object a3) {
    return new AST3(D, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST dSolve(final Object a1) {
    return new AST1(DSolve, Object2Expr.convert(a1));
  }

  public static IAST dSolve(final Object a1, final Object a2) {
    return new AST2(DSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(DSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST dateObject(final Object a1) {
    return new AST1(DateObject, Object2Expr.convert(a1));
  }

  public static IAST dateObject(final Object a1, final Object a2) {
    return new AST2(DateObject, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dateString(final Object a1) {
    return new AST1(DateString, Object2Expr.convert(a1));
  }

  public static IAST dateString(final Object a1, final Object a2) {
    return new AST2(DateString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dateValue(final Object a1) {
    return new AST1(DateValue, Object2Expr.convert(a1));
  }

  public static IAST dateValue(final Object a1, final Object a2) {
    return new AST2(DateValue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dateValue(final Object a1, final Object a2, final Object a3) {
    return new AST3(DateValue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST decrement(final Object a1) {
    return new AST1(Decrement, Object2Expr.convert(a1));
  }

  public static IAST $default(final Object a1) {
    return new AST1(Default, Object2Expr.convert(a1));
  }

  public static IAST $default(final Object a1, final Object a2) {
    return new AST2(Default, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST defer(final Object a1) {
    return new AST1(Defer, Object2Expr.convert(a1));
  }

  public static IAST definition(final Object a1) {
    return new AST1(Definition, Object2Expr.convert(a1));
  }

  public static IAST delete(final Object a1) {
    return new AST1(Delete, Object2Expr.convert(a1));
  }

  public static IAST delete(final Object a1, final Object a2) {
    return new AST2(Delete, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST deleteCases(final Object a1) {
    return new AST1(DeleteCases, Object2Expr.convert(a1));
  }

  public static IAST deleteCases(final Object a1, final Object a2) {
    return new AST2(DeleteCases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST deleteCases(final Object a1, final Object a2, final Object a3) {
    return new AST3(DeleteCases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST deleteDuplicates(final Object a1) {
    return new AST1(DeleteDuplicates, Object2Expr.convert(a1));
  }

  public static IAST deleteDuplicates(final Object a1, final Object a2) {
    return new AST2(DeleteDuplicates, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST deleteDuplicatesBy(final Object a1) {
    return new AST1(DeleteDuplicatesBy, Object2Expr.convert(a1));
  }

  public static IAST deleteDuplicatesBy(final Object a1, final Object a2) {
    return new AST2(DeleteDuplicatesBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST denominator(final Object a1) {
    return new AST1(Denominator, Object2Expr.convert(a1));
  }

  public static IAST denominator(final Object a1, final Object a2) {
    return new AST2(Denominator, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST depth(final Object a1) {
    return new AST1(Depth, Object2Expr.convert(a1));
  }

  public static IAST designMatrix(final Object a1) {
    return new AST1(DesignMatrix, Object2Expr.convert(a1));
  }

  public static IAST designMatrix(final Object a1, final Object a2) {
    return new AST2(DesignMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST designMatrix(final Object a1, final Object a2, final Object a3) {
    return new AST3(DesignMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST det(final Object a1) {
    return new AST1(Det, Object2Expr.convert(a1));
  }

  public static IAST det(final Object a1, final Object a2) {
    return new AST2(Det, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST diagonal(final Object a1) {
    return new AST1(Diagonal, Object2Expr.convert(a1));
  }

  public static IAST diagonal(final Object a1, final Object a2) {
    return new AST2(Diagonal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST diagonalMatrix(final Object a1) {
    return new AST1(DiagonalMatrix, Object2Expr.convert(a1));
  }

  public static IAST diagonalMatrix(final Object a1, final Object a2) {
    return new AST2(DiagonalMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST diagonalMatrixQ(final Object a1) {
    return new AST1(DiagonalMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST diagonalMatrixQ(final Object a1, final Object a2) {
    return new AST2(DiagonalMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST diceDissimilarity(final Object a1) {
    return new AST1(DiceDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST diceDissimilarity(final Object a1, final Object a2) {
    return new AST2(DiceDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST differenceDelta(final Object a1) {
    return new AST1(DifferenceDelta, Object2Expr.convert(a1));
  }

  public static IAST differenceDelta(final Object a1, final Object a2) {
    return new AST2(DifferenceDelta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST differences(final Object a1) {
    return new AST1(Differences, Object2Expr.convert(a1));
  }

  public static IAST differences(final Object a1, final Object a2) {
    return new AST2(Differences, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST differences(final Object a1, final Object a2, final Object a3) {
    return new AST3(Differences, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST digitCount(final Object a1) {
    return new AST1(DigitCount, Object2Expr.convert(a1));
  }

  public static IAST digitCount(final Object a1, final Object a2) {
    return new AST2(DigitCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST digitCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(DigitCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST digitQ(final Object a1) {
    return new AST1(DigitQ, Object2Expr.convert(a1));
  }

  public static IAST digitQ(final Object a1, final Object a2) {
    return new AST2(DigitQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dimensions(final Object a1) {
    return new AST1(Dimensions, Object2Expr.convert(a1));
  }

  public static IAST dimensions(final Object a1, final Object a2) {
    return new AST2(Dimensions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST directedInfinity(final Object a1) {
    return new AST1(DirectedInfinity, Object2Expr.convert(a1));
  }

  public static IAST dirichletEta(final Object a1) {
    return new AST1(DirichletEta, Object2Expr.convert(a1));
  }

  public static IAST dirichletWindow(final Object a1) {
    return new AST1(DirichletWindow, Object2Expr.convert(a1));
  }

  public static IAST discreteUniformDistribution(final Object a1) {
    return new AST1(DiscreteUniformDistribution, Object2Expr.convert(a1));
  }

  public static IAST discriminant(final Object a1) {
    return new AST1(Discriminant, Object2Expr.convert(a1));
  }

  public static IAST discriminant(final Object a1, final Object a2) {
    return new AST2(Discriminant, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST disjointQ(final Object a1) {
    return new AST1(DisjointQ, Object2Expr.convert(a1));
  }

  public static IAST disjointQ(final Object a1, final Object a2) {
    return new AST2(DisjointQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST distribute(final Object a1) {
    return new AST1(Distribute, Object2Expr.convert(a1));
  }

  public static IAST distribute(final Object a1, final Object a2) {
    return new AST2(Distribute, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST distribute(final Object a1, final Object a2, final Object a3) {
    return new AST3(Distribute, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST div(final Object a1) {
    return new AST1(Div, Object2Expr.convert(a1));
  }

  public static IAST div(final Object a1, final Object a2) {
    return new AST2(Div, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divide(final Object a1) {
    return new AST1(Divide, Object2Expr.convert(a1));
  }

  public static IAST divide(final Object a1, final Object a2) {
    return new AST2(Divide, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divideBy(final Object a1) {
    return new AST1(DivideBy, Object2Expr.convert(a1));
  }

  public static IAST divideBy(final Object a1, final Object a2) {
    return new AST2(DivideBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divideSides(final Object a1) {
    return new AST1(DivideSides, Object2Expr.convert(a1));
  }

  public static IAST divideSides(final Object a1, final Object a2) {
    return new AST2(DivideSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divisible(final Object a1) {
    return new AST1(Divisible, Object2Expr.convert(a1));
  }

  public static IAST divisible(final Object a1, final Object a2) {
    return new AST2(Divisible, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divisorSigma(final Object a1) {
    return new AST1(DivisorSigma, Object2Expr.convert(a1));
  }

  public static IAST divisorSigma(final Object a1, final Object a2) {
    return new AST2(DivisorSigma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divisorSum(final Object a1) {
    return new AST1(DivisorSum, Object2Expr.convert(a1));
  }

  public static IAST divisorSum(final Object a1, final Object a2) {
    return new AST2(DivisorSum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST divisorSum(final Object a1, final Object a2, final Object a3) {
    return new AST3(DivisorSum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST divisors(final Object a1) {
    return new AST1(Divisors, Object2Expr.convert(a1));
  }

  public static IAST $do(final Object a1) {
    return new AST1(Do, Object2Expr.convert(a1));
  }

  public static IAST $do(final Object a1, final Object a2) {
    return new AST2(Do, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $do(final Object a1, final Object a2, final Object a3) {
    return new AST3(Do, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST dodecahedron(final Object a1) {
    return new AST1(Dodecahedron, Object2Expr.convert(a1));
  }

  public static IAST dodecahedron(final Object a1, final Object a2) {
    return new AST2(Dodecahedron, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST dodecahedron(final Object a1, final Object a2, final Object a3) {
    return new AST3(Dodecahedron, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST downValues(final Object a1) {
    return new AST1(DownValues, Object2Expr.convert(a1));
  }

  public static IAST drop(final Object a1) {
    return new AST1(Drop, Object2Expr.convert(a1));
  }

  public static IAST drop(final Object a1, final Object a2) {
    return new AST2(Drop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST drop(final Object a1, final Object a2, final Object a3) {
    return new AST3(Drop, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST duplicateFreeQ(final Object a1) {
    return new AST1(DuplicateFreeQ, Object2Expr.convert(a1));
  }

  public static IAST duplicateFreeQ(final Object a1, final Object a2) {
    return new AST2(DuplicateFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST easterSunday(final Object a1) {
    return new AST1(EasterSunday, Object2Expr.convert(a1));
  }

  public static IAST echo(final Object a1) {
    return new AST1(Echo, Object2Expr.convert(a1));
  }

  public static IAST echo(final Object a1, final Object a2) {
    return new AST2(Echo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST echo(final Object a1, final Object a2, final Object a3) {
    return new AST3(Echo, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST echoFunction(final Object a1) {
    return new AST1(EchoFunction, Object2Expr.convert(a1));
  }

  public static IAST echoFunction(final Object a1, final Object a2) {
    return new AST2(EchoFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST edgeList(final Object a1) {
    return new AST1(EdgeList, Object2Expr.convert(a1));
  }

  public static IAST edgeQ(final Object a1) {
    return new AST1(EdgeQ, Object2Expr.convert(a1));
  }

  public static IAST edgeQ(final Object a1, final Object a2) {
    return new AST2(EdgeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST edgeRules(final Object a1) {
    return new AST1(EdgeRules, Object2Expr.convert(a1));
  }

  public static IAST editDistance(final Object a1) {
    return new AST1(EditDistance, Object2Expr.convert(a1));
  }

  public static IAST editDistance(final Object a1, final Object a2) {
    return new AST2(EditDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST effectiveInterest(final Object a1) {
    return new AST1(EffectiveInterest, Object2Expr.convert(a1));
  }

  public static IAST effectiveInterest(final Object a1, final Object a2) {
    return new AST2(EffectiveInterest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST eigenvalues(final Object a1) {
    return new AST1(Eigenvalues, Object2Expr.convert(a1));
  }

  public static IAST eigenvalues(final Object a1, final Object a2) {
    return new AST2(Eigenvalues, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST eigenvectors(final Object a1) {
    return new AST1(Eigenvectors, Object2Expr.convert(a1));
  }

  public static IAST eigenvectors(final Object a1, final Object a2) {
    return new AST2(Eigenvectors, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST element(final Object a1) {
    return new AST1(Element, Object2Expr.convert(a1));
  }

  public static IAST element(final Object a1, final Object a2) {
    return new AST2(Element, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST elementData(final Object a1) {
    return new AST1(ElementData, Object2Expr.convert(a1));
  }

  public static IAST elementData(final Object a1, final Object a2) {
    return new AST2(ElementData, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST eliminate(final Object a1) {
    return new AST1(Eliminate, Object2Expr.convert(a1));
  }

  public static IAST eliminate(final Object a1, final Object a2) {
    return new AST2(Eliminate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ellipticE(final Object a1) {
    return new AST1(EllipticE, Object2Expr.convert(a1));
  }

  public static IAST ellipticE(final Object a1, final Object a2) {
    return new AST2(EllipticE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ellipticF(final Object a1) {
    return new AST1(EllipticF, Object2Expr.convert(a1));
  }

  public static IAST ellipticF(final Object a1, final Object a2) {
    return new AST2(EllipticF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ellipticK(final Object a1) {
    return new AST1(EllipticK, Object2Expr.convert(a1));
  }

  public static IAST ellipticPi(final Object a1) {
    return new AST1(EllipticPi, Object2Expr.convert(a1));
  }

  public static IAST ellipticPi(final Object a1, final Object a2) {
    return new AST2(EllipticPi, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ellipticPi(final Object a1, final Object a2, final Object a3) {
    return new AST3(EllipticPi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST ellipticTheta(final Object a1) {
    return new AST1(EllipticTheta, Object2Expr.convert(a1));
  }

  public static IAST ellipticTheta(final Object a1, final Object a2) {
    return new AST2(EllipticTheta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ellipticTheta(final Object a1, final Object a2, final Object a3) {
    return new AST3(EllipticTheta, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST entropy(final Object a1) {
    return new AST1(Entropy, Object2Expr.convert(a1));
  }

  public static IAST entropy(final Object a1, final Object a2) {
    return new AST2(Entropy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST equalTo(final Object a1) {
    return new AST1(EqualTo, Object2Expr.convert(a1));
  }

  public static IAST erf(final Object a1) {
    return new AST1(Erf, Object2Expr.convert(a1));
  }

  public static IAST erf(final Object a1, final Object a2) {
    return new AST2(Erf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST erfc(final Object a1) {
    return new AST1(Erfc, Object2Expr.convert(a1));
  }

  public static IAST erfi(final Object a1) {
    return new AST1(Erfi, Object2Expr.convert(a1));
  }

  public static IAST erlangDistribution(final Object a1) {
    return new AST1(ErlangDistribution, Object2Expr.convert(a1));
  }

  public static IAST erlangDistribution(final Object a1, final Object a2) {
    return new AST2(ErlangDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST euclideanDistance(final Object a1) {
    return new AST1(EuclideanDistance, Object2Expr.convert(a1));
  }

  public static IAST euclideanDistance(final Object a1, final Object a2) {
    return new AST2(EuclideanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST eulerE(final Object a1) {
    return new AST1(EulerE, Object2Expr.convert(a1));
  }

  public static IAST eulerPhi(final Object a1) {
    return new AST1(EulerPhi, Object2Expr.convert(a1));
  }

  public static IAST eulerianGraphQ(final Object a1) {
    return new AST1(EulerianGraphQ, Object2Expr.convert(a1));
  }

  public static IAST evenQ(final Object a1) {
    return new AST1(EvenQ, Object2Expr.convert(a1));
  }

  public static IAST evenQ(final Object a1, final Object a2) {
    return new AST2(EvenQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST exactNumberQ(final Object a1) {
    return new AST1(ExactNumberQ, Object2Expr.convert(a1));
  }

  public static IAST exists(final Object a1) {
    return new AST1(Exists, Object2Expr.convert(a1));
  }

  public static IAST exists(final Object a1, final Object a2) {
    return new AST2(Exists, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST exists(final Object a1, final Object a2, final Object a3) {
    return new AST3(Exists, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST exit(final Object a1) {
    return new AST1(Exit, Object2Expr.convert(a1));
  }

  public static IAST exp(final Object a1) {
    return new AST1(Exp, Object2Expr.convert(a1));
  }

  public static IAST expIntegralE(final Object a1) {
    return new AST1(ExpIntegralE, Object2Expr.convert(a1));
  }

  public static IAST expIntegralE(final Object a1, final Object a2) {
    return new AST2(ExpIntegralE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST expIntegralEi(final Object a1) {
    return new AST1(ExpIntegralEi, Object2Expr.convert(a1));
  }

  public static IAST expToTrig(final Object a1) {
    return new AST1(ExpToTrig, Object2Expr.convert(a1));
  }

  public static IAST expand(final Object a1) {
    return new AST1(Expand, Object2Expr.convert(a1));
  }

  public static IAST expand(final Object a1, final Object a2) {
    return new AST2(Expand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST expandAll(final Object a1) {
    return new AST1(ExpandAll, Object2Expr.convert(a1));
  }

  public static IAST expandAll(final Object a1, final Object a2) {
    return new AST2(ExpandAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST expectation(final Object a1) {
    return new AST1(Expectation, Object2Expr.convert(a1));
  }

  public static IAST expectation(final Object a1, final Object a2) {
    return new AST2(Expectation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST exponent(final Object a1) {
    return new AST1(Exponent, Object2Expr.convert(a1));
  }

  public static IAST exponent(final Object a1, final Object a2) {
    return new AST2(Exponent, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST exponent(final Object a1, final Object a2, final Object a3) {
    return new AST3(Exponent, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST exponentialDistribution(final Object a1) {
    return new AST1(ExponentialDistribution, Object2Expr.convert(a1));
  }

  public static IAST export(final Object a1) {
    return new AST1(Export, Object2Expr.convert(a1));
  }

  public static IAST export(final Object a1, final Object a2) {
    return new AST2(Export, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST export(final Object a1, final Object a2, final Object a3) {
    return new AST3(Export, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST exportString(final Object a1) {
    return new AST1(ExportString, Object2Expr.convert(a1));
  }

  public static IAST exportString(final Object a1, final Object a2) {
    return new AST2(ExportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST extendedGCD(final Object a1) {
    return new AST1(ExtendedGCD, Object2Expr.convert(a1));
  }

  public static IAST extendedGCD(final Object a1, final Object a2) {
    return new AST2(ExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST extendedGCD(final Object a1, final Object a2, final Object a3) {
    return new AST3(ExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST extract(final Object a1) {
    return new AST1(Extract, Object2Expr.convert(a1));
  }

  public static IAST extract(final Object a1, final Object a2) {
    return new AST2(Extract, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST extract(final Object a1, final Object a2, final Object a3) {
    return new AST3(Extract, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST factor(final Object a1) {
    return new AST1(Factor, Object2Expr.convert(a1));
  }

  public static IAST factor(final Object a1, final Object a2) {
    return new AST2(Factor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST factorSquareFree(final Object a1) {
    return new AST1(FactorSquareFree, Object2Expr.convert(a1));
  }

  public static IAST factorSquareFree(final Object a1, final Object a2) {
    return new AST2(FactorSquareFree, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST factorSquareFreeList(final Object a1) {
    return new AST1(FactorSquareFreeList, Object2Expr.convert(a1));
  }

  public static IAST factorTerms(final Object a1) {
    return new AST1(FactorTerms, Object2Expr.convert(a1));
  }

  public static IAST factorTerms(final Object a1, final Object a2) {
    return new AST2(FactorTerms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST factorial(final Object a1) {
    return new AST1(Factorial, Object2Expr.convert(a1));
  }

  public static IAST factorialPower(final Object a1) {
    return new AST1(FactorialPower, Object2Expr.convert(a1));
  }

  public static IAST factorialPower(final Object a1, final Object a2) {
    return new AST2(FactorialPower, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST factorialPower(final Object a1, final Object a2, final Object a3) {
    return new AST3(FactorialPower, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST fibonacci(final Object a1) {
    return new AST1(Fibonacci, Object2Expr.convert(a1));
  }

  public static IAST fibonacci(final Object a1, final Object a2) {
    return new AST2(Fibonacci, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST file(final Object a1) {
    return new AST1(File, Object2Expr.convert(a1));
  }

  public static IAST file(final Object a1, final Object a2) {
    return new AST2(File, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fileFormat(final Object a1) {
    return new AST1(FileFormat, Object2Expr.convert(a1));
  }

  public static IAST fileNameJoin(final Object a1) {
    return new AST1(FileNameJoin, Object2Expr.convert(a1));
  }

  public static IAST fileNameJoin(final Object a1, final Object a2) {
    return new AST2(FileNameJoin, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fileNameTake(final Object a1) {
    return new AST1(FileNameTake, Object2Expr.convert(a1));
  }

  public static IAST fileNameTake(final Object a1, final Object a2) {
    return new AST2(FileNameTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fileNames(final Object a1) {
    return new AST1(FileNames, Object2Expr.convert(a1));
  }

  public static IAST fileNames(final Object a1, final Object a2) {
    return new AST2(FileNames, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fileNames(final Object a1, final Object a2, final Object a3) {
    return new AST3(FileNames, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST filePrint(final Object a1) {
    return new AST1(FilePrint, Object2Expr.convert(a1));
  }

  public static IAST filePrint(final Object a1, final Object a2) {
    return new AST2(FilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST filterRules(final Object a1) {
    return new AST1(FilterRules, Object2Expr.convert(a1));
  }

  public static IAST filterRules(final Object a1, final Object a2) {
    return new AST2(FilterRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findClusters(final Object a1) {
    return new AST1(FindClusters, Object2Expr.convert(a1));
  }

  public static IAST findClusters(final Object a1, final Object a2) {
    return new AST2(FindClusters, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findClusters(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindClusters, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findCycle(final Object a1) {
    return new AST1(FindCycle, Object2Expr.convert(a1));
  }

  public static IAST findCycle(final Object a1, final Object a2) {
    return new AST2(FindCycle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findCycle(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindCycle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findEulerianCycle(final Object a1) {
    return new AST1(FindEulerianCycle, Object2Expr.convert(a1));
  }

  public static IAST findGraphIsomorphism(final Object a1) {
    return new AST1(FindGraphIsomorphism, Object2Expr.convert(a1));
  }

  public static IAST findGraphIsomorphism(final Object a1, final Object a2) {
    return new AST2(FindGraphIsomorphism, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findHamiltonianCycle(final Object a1) {
    return new AST1(FindHamiltonianCycle, Object2Expr.convert(a1));
  }

  public static IAST findInstance(final Object a1) {
    return new AST1(FindInstance, Object2Expr.convert(a1));
  }

  public static IAST findInstance(final Object a1, final Object a2) {
    return new AST2(FindInstance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findInstance(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindInstance, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findMaximum(final Object a1) {
    return new AST1(FindMaximum, Object2Expr.convert(a1));
  }

  public static IAST findMaximum(final Object a1, final Object a2) {
    return new AST2(FindMaximum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findMaximum(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindMaximum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findMinimum(final Object a1) {
    return new AST1(FindMinimum, Object2Expr.convert(a1));
  }

  public static IAST findMinimum(final Object a1, final Object a2) {
    return new AST2(FindMinimum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findMinimum(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindMinimum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findPermutation(final Object a1) {
    return new AST1(FindPermutation, Object2Expr.convert(a1));
  }

  public static IAST findPermutation(final Object a1, final Object a2) {
    return new AST2(FindPermutation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findRoot(final Object a1) {
    return new AST1(FindRoot, Object2Expr.convert(a1));
  }

  public static IAST findRoot(final Object a1, final Object a2) {
    return new AST2(FindRoot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findRoot(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindRoot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findShortestPath(final Object a1) {
    return new AST1(FindShortestPath, Object2Expr.convert(a1));
  }

  public static IAST findShortestPath(final Object a1, final Object a2) {
    return new AST2(FindShortestPath, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findShortestPath(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindShortestPath, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findShortestTour(final Object a1) {
    return new AST1(FindShortestTour, Object2Expr.convert(a1));
  }

  public static IAST findShortestTour(final Object a1, final Object a2) {
    return new AST2(FindShortestTour, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST findShortestTour(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindShortestTour, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST findSpanningTree(final Object a1) {
    return new AST1(FindSpanningTree, Object2Expr.convert(a1));
  }

  public static IAST findVertexCover(final Object a1) {
    return new AST1(FindVertexCover, Object2Expr.convert(a1));
  }

  public static IAST first(final Object a1) {
    return new AST1(First, Object2Expr.convert(a1));
  }

  public static IAST first(final Object a1, final Object a2) {
    return new AST2(First, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST firstCase(final Object a1) {
    return new AST1(FirstCase, Object2Expr.convert(a1));
  }

  public static IAST firstCase(final Object a1, final Object a2) {
    return new AST2(FirstCase, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST firstCase(final Object a1, final Object a2, final Object a3) {
    return new AST3(FirstCase, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST firstPosition(final Object a1) {
    return new AST1(FirstPosition, Object2Expr.convert(a1));
  }

  public static IAST firstPosition(final Object a1, final Object a2) {
    return new AST2(FirstPosition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST firstPosition(final Object a1, final Object a2, final Object a3) {
    return new AST3(FirstPosition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST fit(final Object a1) {
    return new AST1(Fit, Object2Expr.convert(a1));
  }

  public static IAST fit(final Object a1, final Object a2) {
    return new AST2(Fit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fit(final Object a1, final Object a2, final Object a3) {
    return new AST3(Fit, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST fiveNum(final Object a1) {
    return new AST1(FiveNum, Object2Expr.convert(a1));
  }

  public static IAST fixedPoint(final Object a1) {
    return new AST1(FixedPoint, Object2Expr.convert(a1));
  }

  public static IAST fixedPoint(final Object a1, final Object a2) {
    return new AST2(FixedPoint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fixedPoint(final Object a1, final Object a2, final Object a3) {
    return new AST3(FixedPoint, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST fixedPointList(final Object a1) {
    return new AST1(FixedPointList, Object2Expr.convert(a1));
  }

  public static IAST fixedPointList(final Object a1, final Object a2) {
    return new AST2(FixedPointList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fixedPointList(final Object a1, final Object a2, final Object a3) {
    return new AST3(FixedPointList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST flatTopWindow(final Object a1) {
    return new AST1(FlatTopWindow, Object2Expr.convert(a1));
  }

  public static IAST flatten(final Object a1) {
    return new AST1(Flatten, Object2Expr.convert(a1));
  }

  public static IAST flatten(final Object a1, final Object a2) {
    return new AST2(Flatten, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST flatten(final Object a1, final Object a2, final Object a3) {
    return new AST3(Flatten, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST flattenAt(final Object a1) {
    return new AST1(FlattenAt, Object2Expr.convert(a1));
  }

  public static IAST flattenAt(final Object a1, final Object a2) {
    return new AST2(FlattenAt, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST floor(final Object a1) {
    return new AST1(Floor, Object2Expr.convert(a1));
  }

  public static IAST floor(final Object a1, final Object a2) {
    return new AST2(Floor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fold(final Object a1) {
    return new AST1(Fold, Object2Expr.convert(a1));
  }

  public static IAST fold(final Object a1, final Object a2) {
    return new AST2(Fold, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fold(final Object a1, final Object a2, final Object a3) {
    return new AST3(Fold, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST foldList(final Object a1) {
    return new AST1(FoldList, Object2Expr.convert(a1));
  }

  public static IAST foldList(final Object a1, final Object a2) {
    return new AST2(FoldList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST foldList(final Object a1, final Object a2, final Object a3) {
    return new AST3(FoldList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST $for(final Object a1) {
    return new AST1(For, Object2Expr.convert(a1));
  }

  public static IAST $for(final Object a1, final Object a2) {
    return new AST2(For, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $for(final Object a1, final Object a2, final Object a3) {
    return new AST3(For, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST forAll(final Object a1) {
    return new AST1(ForAll, Object2Expr.convert(a1));
  }

  public static IAST forAll(final Object a1, final Object a2) {
    return new AST2(ForAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST forAll(final Object a1, final Object a2, final Object a3) {
    return new AST3(ForAll, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST fourier(final Object a1) {
    return new AST1(Fourier, Object2Expr.convert(a1));
  }

  public static IAST fourierMatrix(final Object a1) {
    return new AST1(FourierMatrix, Object2Expr.convert(a1));
  }

  public static IAST fractionalPart(final Object a1) {
    return new AST1(FractionalPart, Object2Expr.convert(a1));
  }

  public static IAST frechetDistribution(final Object a1) {
    return new AST1(FrechetDistribution, Object2Expr.convert(a1));
  }

  public static IAST frechetDistribution(final Object a1, final Object a2) {
    return new AST2(FrechetDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST freeQ(final Object a1) {
    return new AST1(FreeQ, Object2Expr.convert(a1));
  }

  public static IAST freeQ(final Object a1, final Object a2) {
    return new AST2(FreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fresnelC(final Object a1) {
    return new AST1(FresnelC, Object2Expr.convert(a1));
  }

  public static IAST fresnelS(final Object a1) {
    return new AST1(FresnelS, Object2Expr.convert(a1));
  }

  public static IAST frobeniusNumber(final Object a1) {
    return new AST1(FrobeniusNumber, Object2Expr.convert(a1));
  }

  public static IAST frobeniusSolve(final Object a1) {
    return new AST1(FrobeniusSolve, Object2Expr.convert(a1));
  }

  public static IAST frobeniusSolve(final Object a1, final Object a2) {
    return new AST2(FrobeniusSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST frobeniusSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(FrobeniusSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST fromContinuedFraction(final Object a1) {
    return new AST1(FromContinuedFraction, Object2Expr.convert(a1));
  }

  public static IAST fromDigits(final Object a1) {
    return new AST1(FromDigits, Object2Expr.convert(a1));
  }

  public static IAST fromDigits(final Object a1, final Object a2) {
    return new AST2(FromDigits, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fromLetterNumber(final Object a1) {
    return new AST1(FromLetterNumber, Object2Expr.convert(a1));
  }

  public static IAST fromLetterNumber(final Object a1, final Object a2) {
    return new AST2(FromLetterNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST fromPolarCoordinates(final Object a1) {
    return new AST1(FromPolarCoordinates, Object2Expr.convert(a1));
  }

  public static IAST fullForm(final Object a1) {
    return new AST1(FullForm, Object2Expr.convert(a1));
  }

  public static IAST fullSimplify(final Object a1) {
    return new AST1(FullSimplify, Object2Expr.convert(a1));
  }

  public static IAST fullSimplify(final Object a1, final Object a2) {
    return new AST2(FullSimplify, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST functionExpand(final Object a1) {
    return new AST1(FunctionExpand, Object2Expr.convert(a1));
  }

  public static IAST functionExpand(final Object a1, final Object a2) {
    return new AST2(FunctionExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST functionRange(final Object a1) {
    return new AST1(FunctionRange, Object2Expr.convert(a1));
  }

  public static IAST functionRange(final Object a1, final Object a2) {
    return new AST2(FunctionRange, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST functionRange(final Object a1, final Object a2, final Object a3) {
    return new AST3(FunctionRange, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST functionURL(final Object a1) {
    return new AST1(FunctionURL, Object2Expr.convert(a1));
  }

  public static IAST gamma(final Object a1) {
    return new AST1(Gamma, Object2Expr.convert(a1));
  }

  public static IAST gamma(final Object a1, final Object a2) {
    return new AST2(Gamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST gamma(final Object a1, final Object a2, final Object a3) {
    return new AST3(Gamma, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST gammaRegularized(final Object a1) {
    return new AST1(GammaRegularized, Object2Expr.convert(a1));
  }

  public static IAST gammaRegularized(final Object a1, final Object a2) {
    return new AST2(GammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST gammaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(GammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST gather(final Object a1) {
    return new AST1(Gather, Object2Expr.convert(a1));
  }

  public static IAST gather(final Object a1, final Object a2) {
    return new AST2(Gather, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST gatherBy(final Object a1) {
    return new AST1(GatherBy, Object2Expr.convert(a1));
  }

  public static IAST gatherBy(final Object a1, final Object a2) {
    return new AST2(GatherBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST gaussianWindow(final Object a1) {
    return new AST1(GaussianWindow, Object2Expr.convert(a1));
  }

  public static IAST gegenbauerC(final Object a1) {
    return new AST1(GegenbauerC, Object2Expr.convert(a1));
  }

  public static IAST gegenbauerC(final Object a1, final Object a2) {
    return new AST2(GegenbauerC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST gegenbauerC(final Object a1, final Object a2, final Object a3) {
    return new AST3(GegenbauerC, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST geoDistance(final Object a1) {
    return new AST1(GeoDistance, Object2Expr.convert(a1));
  }

  public static IAST geoDistance(final Object a1, final Object a2) {
    return new AST2(GeoDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST geoPosition(final Object a1) {
    return new AST1(GeoPosition, Object2Expr.convert(a1));
  }

  public static IAST geometricDistribution(final Object a1) {
    return new AST1(GeometricDistribution, Object2Expr.convert(a1));
  }

  public static IAST geometricMean(final Object a1) {
    return new AST1(GeometricMean, Object2Expr.convert(a1));
  }

  public static IAST get(final Object a1) {
    return new AST1(Get, Object2Expr.convert(a1));
  }

  public static IAST gompertzMakehamDistribution(final Object a1) {
    return new AST1(GompertzMakehamDistribution, Object2Expr.convert(a1));
  }

  public static IAST gompertzMakehamDistribution(final Object a1, final Object a2) {
    return new AST2(GompertzMakehamDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST grad(final Object a1) {
    return new AST1(Grad, Object2Expr.convert(a1));
  }

  public static IAST grad(final Object a1, final Object a2) {
    return new AST2(Grad, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST graph(final Object a1) {
    return new AST1(Graph, Object2Expr.convert(a1));
  }

  public static IAST graph(final Object a1, final Object a2) {
    return new AST2(Graph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST graph(final Object a1, final Object a2, final Object a3) {
    return new AST3(Graph, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST graphCenter(final Object a1) {
    return new AST1(GraphCenter, Object2Expr.convert(a1));
  }

  public static IAST graphData(final Object a1) {
    return new AST1(GraphData, Object2Expr.convert(a1));
  }

  public static IAST graphDiameter(final Object a1) {
    return new AST1(GraphDiameter, Object2Expr.convert(a1));
  }

  public static IAST graphPeriphery(final Object a1) {
    return new AST1(GraphPeriphery, Object2Expr.convert(a1));
  }

  public static IAST graphQ(final Object a1) {
    return new AST1(GraphQ, Object2Expr.convert(a1));
  }

  public static IAST graphRadius(final Object a1) {
    return new AST1(GraphRadius, Object2Expr.convert(a1));
  }

  public static IAST graphUnion(final Object a1) {
    return new AST1(GraphUnion, Object2Expr.convert(a1));
  }

  public static IAST graphUnion(final Object a1, final Object a2) {
    return new AST2(GraphUnion, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST graphUnion(final Object a1, final Object a2, final Object a3) {
    return new AST3(GraphUnion, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST greater(final Object a1) {
    return new AST1(Greater, Object2Expr.convert(a1));
  }

  public static IAST greater(final Object a1, final Object a2) {
    return new AST2(Greater, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST greater(final Object a1, final Object a2, final Object a3) {
    return new AST3(Greater, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST greaterEqual(final Object a1) {
    return new AST1(GreaterEqual, Object2Expr.convert(a1));
  }

  public static IAST greaterEqual(final Object a1, final Object a2) {
    return new AST2(GreaterEqual, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST greaterEqual(final Object a1, final Object a2, final Object a3) {
    return new AST3(GreaterEqual, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST greaterEqualThan(final Object a1) {
    return new AST1(GreaterEqualThan, Object2Expr.convert(a1));
  }

  public static IAST greaterThan(final Object a1) {
    return new AST1(GreaterThan, Object2Expr.convert(a1));
  }

  public static IAST groupBy(final Object a1) {
    return new AST1(GroupBy, Object2Expr.convert(a1));
  }

  public static IAST groupBy(final Object a1, final Object a2) {
    return new AST2(GroupBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST groupBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(GroupBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST gudermannian(final Object a1) {
    return new AST1(Gudermannian, Object2Expr.convert(a1));
  }

  public static IAST hamiltonianGraphQ(final Object a1) {
    return new AST1(HamiltonianGraphQ, Object2Expr.convert(a1));
  }

  public static IAST hammingDistance(final Object a1) {
    return new AST1(HammingDistance, Object2Expr.convert(a1));
  }

  public static IAST hammingDistance(final Object a1, final Object a2) {
    return new AST2(HammingDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hammingWindow(final Object a1) {
    return new AST1(HammingWindow, Object2Expr.convert(a1));
  }

  public static IAST hankelH1(final Object a1) {
    return new AST1(HankelH1, Object2Expr.convert(a1));
  }

  public static IAST hankelH1(final Object a1, final Object a2) {
    return new AST2(HankelH1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hankelH2(final Object a1) {
    return new AST1(HankelH2, Object2Expr.convert(a1));
  }

  public static IAST hankelH2(final Object a1, final Object a2) {
    return new AST2(HankelH2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hannWindow(final Object a1) {
    return new AST1(HannWindow, Object2Expr.convert(a1));
  }

  public static IAST harmonicMean(final Object a1) {
    return new AST1(HarmonicMean, Object2Expr.convert(a1));
  }

  public static IAST harmonicNumber(final Object a1) {
    return new AST1(HarmonicNumber, Object2Expr.convert(a1));
  }

  public static IAST harmonicNumber(final Object a1, final Object a2) {
    return new AST2(HarmonicNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST haversine(final Object a1) {
    return new AST1(Haversine, Object2Expr.convert(a1));
  }

  public static IAST head(final Object a1) {
    return new AST1(Head, Object2Expr.convert(a1));
  }

  public static IAST head(final Object a1, final Object a2) {
    return new AST2(Head, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hermiteH(final Object a1) {
    return new AST1(HermiteH, Object2Expr.convert(a1));
  }

  public static IAST hermiteH(final Object a1, final Object a2) {
    return new AST2(HermiteH, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hermitianMatrixQ(final Object a1) {
    return new AST1(HermitianMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST hermitianMatrixQ(final Object a1, final Object a2) {
    return new AST2(HermitianMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hilbertMatrix(final Object a1) {
    return new AST1(HilbertMatrix, Object2Expr.convert(a1));
  }

  public static IAST hilbertMatrix(final Object a1, final Object a2) {
    return new AST2(HilbertMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hodgeDual(final Object a1) {
    return new AST1(HodgeDual, Object2Expr.convert(a1));
  }

  public static IAST hold(final Object a1) {
    return new AST1(Hold, Object2Expr.convert(a1));
  }

  public static IAST hold(final Object a1, final Object a2) {
    return new AST2(Hold, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hold(final Object a1, final Object a2, final Object a3) {
    return new AST3(Hold, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST holdPattern(final Object a1) {
    return new AST1(HoldPattern, Object2Expr.convert(a1));
  }

  public static IAST horner(final Object a1) {
    return new AST1(Horner, Object2Expr.convert(a1));
  }

  public static IAST hornerForm(final Object a1) {
    return new AST1(HornerForm, Object2Expr.convert(a1));
  }

  public static IAST hornerForm(final Object a1, final Object a2) {
    return new AST2(HornerForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hurwitzLerchPhi(final Object a1) {
    return new AST1(HurwitzLerchPhi, Object2Expr.convert(a1));
  }

  public static IAST hurwitzLerchPhi(final Object a1, final Object a2) {
    return new AST2(HurwitzLerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hurwitzLerchPhi(final Object a1, final Object a2, final Object a3) {
    return new AST3(HurwitzLerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST hurwitzZeta(final Object a1) {
    return new AST1(HurwitzZeta, Object2Expr.convert(a1));
  }

  public static IAST hurwitzZeta(final Object a1, final Object a2) {
    return new AST2(HurwitzZeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypercubeGraph(final Object a1) {
    return new AST1(HypercubeGraph, Object2Expr.convert(a1));
  }

  public static IAST hypergeometric0F1(final Object a1) {
    return new AST1(Hypergeometric0F1, Object2Expr.convert(a1));
  }

  public static IAST hypergeometric0F1(final Object a1, final Object a2) {
    return new AST2(Hypergeometric0F1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometric1F1(final Object a1) {
    return new AST1(Hypergeometric1F1, Object2Expr.convert(a1));
  }

  public static IAST hypergeometric1F1(final Object a1, final Object a2) {
    return new AST2(Hypergeometric1F1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometric1F1(final Object a1, final Object a2, final Object a3) {
    return new AST3(Hypergeometric1F1, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST hypergeometricDistribution(final Object a1) {
    return new AST1(HypergeometricDistribution, Object2Expr.convert(a1));
  }

  public static IAST hypergeometricDistribution(final Object a1, final Object a2) {
    return new AST2(HypergeometricDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometricDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(HypergeometricDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST hypergeometricPFQ(final Object a1) {
    return new AST1(HypergeometricPFQ, Object2Expr.convert(a1));
  }

  public static IAST hypergeometricPFQ(final Object a1, final Object a2) {
    return new AST2(HypergeometricPFQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometricPFQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(HypergeometricPFQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST hypergeometricPFQRegularized(final Object a1) {
    return new AST1(HypergeometricPFQRegularized, Object2Expr.convert(a1));
  }

  public static IAST hypergeometricPFQRegularized(final Object a1, final Object a2) {
    return new AST2(HypergeometricPFQRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometricPFQRegularized(final Object a1, final Object a2,
      final Object a3) {
    return new AST3(HypergeometricPFQRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST hypergeometricU(final Object a1) {
    return new AST1(HypergeometricU, Object2Expr.convert(a1));
  }

  public static IAST hypergeometricU(final Object a1, final Object a2) {
    return new AST2(HypergeometricU, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST hypergeometricU(final Object a1, final Object a2, final Object a3) {
    return new AST3(HypergeometricU, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST icosahedron(final Object a1) {
    return new AST1(Icosahedron, Object2Expr.convert(a1));
  }

  public static IAST icosahedron(final Object a1, final Object a2) {
    return new AST2(Icosahedron, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST icosahedron(final Object a1, final Object a2, final Object a3) {
    return new AST3(Icosahedron, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST identity(final Object a1) {
    return new AST1(Identity, Object2Expr.convert(a1));
  }

  public static IAST identityMatrix(final Object a1) {
    return new AST1(IdentityMatrix, Object2Expr.convert(a1));
  }

  public static IAST identityMatrix(final Object a1, final Object a2) {
    return new AST2(IdentityMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $if(final Object a1) {
    return new AST1(If, Object2Expr.convert(a1));
  }

  public static IAST $if(final Object a1, final Object a2) {
    return new AST2(If, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $if(final Object a1, final Object a2, final Object a3) {
    return new AST3(If, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST im(final Object a1) {
    return new AST1(Im, Object2Expr.convert(a1));
  }

  public static IAST image(final Object a1) {
    return new AST1(Image, Object2Expr.convert(a1));
  }

  public static IAST image(final Object a1, final Object a2) {
    return new AST2(Image, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST implies(final Object a1) {
    return new AST1(Implies, Object2Expr.convert(a1));
  }

  public static IAST implies(final Object a1, final Object a2) {
    return new AST2(Implies, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $import(final Object a1) {
    return new AST1(Import, Object2Expr.convert(a1));
  }

  public static IAST $import(final Object a1, final Object a2) {
    return new AST2(Import, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST importString(final Object a1) {
    return new AST1(ImportString, Object2Expr.convert(a1));
  }

  public static IAST importString(final Object a1, final Object a2) {
    return new AST2(ImportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST in(final Object a1) {
    return new AST1(In, Object2Expr.convert(a1));
  }

  public static IAST increment(final Object a1) {
    return new AST1(Increment, Object2Expr.convert(a1));
  }

  public static IAST inexactNumberQ(final Object a1) {
    return new AST1(InexactNumberQ, Object2Expr.convert(a1));
  }

  public static IAST inner(final Object a1) {
    return new AST1(Inner, Object2Expr.convert(a1));
  }

  public static IAST inner(final Object a1, final Object a2) {
    return new AST2(Inner, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inner(final Object a1, final Object a2, final Object a3) {
    return new AST3(Inner, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inputStream(final Object a1) {
    return new AST1(InputStream, Object2Expr.convert(a1));
  }

  public static IAST inputStream(final Object a1, final Object a2) {
    return new AST2(InputStream, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST insert(final Object a1) {
    return new AST1(Insert, Object2Expr.convert(a1));
  }

  public static IAST insert(final Object a1, final Object a2) {
    return new AST2(Insert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST insert(final Object a1, final Object a2, final Object a3) {
    return new AST3(Insert, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST $instanceof(final Object a1) {
    return new AST1(InstanceOf, Object2Expr.convert(a1));
  }

  public static IAST $instanceof(final Object a1, final Object a2) {
    return new AST2(InstanceOf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerDigits(final Object a1) {
    return new AST1(IntegerDigits, Object2Expr.convert(a1));
  }

  public static IAST integerDigits(final Object a1, final Object a2) {
    return new AST2(IntegerDigits, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerDigits(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntegerDigits, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST integerExponent(final Object a1) {
    return new AST1(IntegerExponent, Object2Expr.convert(a1));
  }

  public static IAST integerExponent(final Object a1, final Object a2) {
    return new AST2(IntegerExponent, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerLength(final Object a1) {
    return new AST1(IntegerLength, Object2Expr.convert(a1));
  }

  public static IAST integerLength(final Object a1, final Object a2) {
    return new AST2(IntegerLength, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerName(final Object a1) {
    return new AST1(IntegerName, Object2Expr.convert(a1));
  }

  public static IAST integerName(final Object a1, final Object a2) {
    return new AST2(IntegerName, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerPart(final Object a1) {
    return new AST1(IntegerPart, Object2Expr.convert(a1));
  }

  public static IAST integerPartitions(final Object a1) {
    return new AST1(IntegerPartitions, Object2Expr.convert(a1));
  }

  public static IAST integerPartitions(final Object a1, final Object a2) {
    return new AST2(IntegerPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST integerPartitions(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntegerPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST integerQ(final Object a1) {
    return new AST1(IntegerQ, Object2Expr.convert(a1));
  }

  public static IAST interpolatingFunction(final Object a1) {
    return new AST1(InterpolatingFunction, Object2Expr.convert(a1));
  }

  public static IAST interpolatingFunction(final Object a1, final Object a2) {
    return new AST2(InterpolatingFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST interpolatingFunction(final Object a1, final Object a2, final Object a3) {
    return new AST3(InterpolatingFunction, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST interpolatingPolynomial(final Object a1) {
    return new AST1(InterpolatingPolynomial, Object2Expr.convert(a1));
  }

  public static IAST interpolatingPolynomial(final Object a1, final Object a2) {
    return new AST2(InterpolatingPolynomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST interpolation(final Object a1) {
    return new AST1(Interpolation, Object2Expr.convert(a1));
  }

  public static IAST interpolation(final Object a1, final Object a2) {
    return new AST2(Interpolation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST interpolation(final Object a1, final Object a2, final Object a3) {
    return new AST3(Interpolation, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST interquartileRange(final Object a1) {
    return new AST1(InterquartileRange, Object2Expr.convert(a1));
  }

  public static IAST intersectingQ(final Object a1) {
    return new AST1(IntersectingQ, Object2Expr.convert(a1));
  }

  public static IAST intersectingQ(final Object a1, final Object a2) {
    return new AST2(IntersectingQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST intervalIntersection(final Object a1) {
    return new AST1(IntervalIntersection, Object2Expr.convert(a1));
  }

  public static IAST intervalIntersection(final Object a1, final Object a2) {
    return new AST2(IntervalIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST intervalIntersection(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntervalIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST intervalMemberQ(final Object a1) {
    return new AST1(IntervalMemberQ, Object2Expr.convert(a1));
  }

  public static IAST intervalMemberQ(final Object a1, final Object a2) {
    return new AST2(IntervalMemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST intervalUnion(final Object a1) {
    return new AST1(IntervalUnion, Object2Expr.convert(a1));
  }

  public static IAST intervalUnion(final Object a1, final Object a2) {
    return new AST2(IntervalUnion, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST intervalUnion(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntervalUnion, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inverse(final Object a1) {
    return new AST1(Inverse, Object2Expr.convert(a1));
  }

  public static IAST inverse(final Object a1, final Object a2) {
    return new AST2(Inverse, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseBetaRegularized(final Object a1) {
    return new AST1(InverseBetaRegularized, Object2Expr.convert(a1));
  }

  public static IAST inverseBetaRegularized(final Object a1, final Object a2) {
    return new AST2(InverseBetaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseBetaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseBetaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inverseErf(final Object a1) {
    return new AST1(InverseErf, Object2Expr.convert(a1));
  }

  public static IAST inverseErfc(final Object a1) {
    return new AST1(InverseErfc, Object2Expr.convert(a1));
  }

  public static IAST inverseFourier(final Object a1) {
    return new AST1(InverseFourier, Object2Expr.convert(a1));
  }

  public static IAST inverseFunction(final Object a1) {
    return new AST1(InverseFunction, Object2Expr.convert(a1));
  }

  public static IAST inverseFunction(final Object a1, final Object a2) {
    return new AST2(InverseFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseFunction(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseFunction, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inverseGammaRegularized(final Object a1) {
    return new AST1(InverseGammaRegularized, Object2Expr.convert(a1));
  }

  public static IAST inverseGammaRegularized(final Object a1, final Object a2) {
    return new AST2(InverseGammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseGammaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseGammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inverseGudermannian(final Object a1) {
    return new AST1(InverseGudermannian, Object2Expr.convert(a1));
  }

  public static IAST inverseHaversine(final Object a1) {
    return new AST1(InverseHaversine, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiCD(final Object a1) {
    return new AST1(InverseJacobiCD, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiCD(final Object a1, final Object a2) {
    return new AST2(InverseJacobiCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseJacobiCN(final Object a1) {
    return new AST1(InverseJacobiCN, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiCN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiCN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseJacobiDN(final Object a1) {
    return new AST1(InverseJacobiDN, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiDN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiDN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseJacobiSC(final Object a1) {
    return new AST1(InverseJacobiSC, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiSC(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseJacobiSD(final Object a1) {
    return new AST1(InverseJacobiSD, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiSD(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseJacobiSN(final Object a1) {
    return new AST1(InverseJacobiSN, Object2Expr.convert(a1));
  }

  public static IAST inverseJacobiSN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseLaplaceTransform(final Object a1) {
    return new AST1(InverseLaplaceTransform, Object2Expr.convert(a1));
  }

  public static IAST inverseLaplaceTransform(final Object a1, final Object a2) {
    return new AST2(InverseLaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseLaplaceTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseLaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST inverseZTransform(final Object a1) {
    return new AST1(InverseZTransform, Object2Expr.convert(a1));
  }

  public static IAST inverseZTransform(final Object a1, final Object a2) {
    return new AST2(InverseZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST inverseZTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST isomorphicGraphQ(final Object a1) {
    return new AST1(IsomorphicGraphQ, Object2Expr.convert(a1));
  }

  public static IAST isomorphicGraphQ(final Object a1, final Object a2) {
    return new AST2(IsomorphicGraphQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jSForm(final Object a1) {
    return new AST1(JSForm, Object2Expr.convert(a1));
  }

  public static IAST jSForm(final Object a1, final Object a2) {
    return new AST2(JSForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jaccardDissimilarity(final Object a1) {
    return new AST1(JaccardDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST jaccardDissimilarity(final Object a1, final Object a2) {
    return new AST2(JaccardDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiAmplitude(final Object a1) {
    return new AST1(JacobiAmplitude, Object2Expr.convert(a1));
  }

  public static IAST jacobiAmplitude(final Object a1, final Object a2) {
    return new AST2(JacobiAmplitude, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiCD(final Object a1) {
    return new AST1(JacobiCD, Object2Expr.convert(a1));
  }

  public static IAST jacobiCD(final Object a1, final Object a2) {
    return new AST2(JacobiCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiCN(final Object a1) {
    return new AST1(JacobiCN, Object2Expr.convert(a1));
  }

  public static IAST jacobiCN(final Object a1, final Object a2) {
    return new AST2(JacobiCN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiDN(final Object a1) {
    return new AST1(JacobiDN, Object2Expr.convert(a1));
  }

  public static IAST jacobiDN(final Object a1, final Object a2) {
    return new AST2(JacobiDN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiMatrix(final Object a1) {
    return new AST1(JacobiMatrix, Object2Expr.convert(a1));
  }

  public static IAST jacobiMatrix(final Object a1, final Object a2) {
    return new AST2(JacobiMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiSC(final Object a1) {
    return new AST1(JacobiSC, Object2Expr.convert(a1));
  }

  public static IAST jacobiSC(final Object a1, final Object a2) {
    return new AST2(JacobiSC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiSD(final Object a1) {
    return new AST1(JacobiSD, Object2Expr.convert(a1));
  }

  public static IAST jacobiSD(final Object a1, final Object a2) {
    return new AST2(JacobiSD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiSN(final Object a1) {
    return new AST1(JacobiSN, Object2Expr.convert(a1));
  }

  public static IAST jacobiSN(final Object a1, final Object a2) {
    return new AST2(JacobiSN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiSymbol(final Object a1) {
    return new AST1(JacobiSymbol, Object2Expr.convert(a1));
  }

  public static IAST jacobiSymbol(final Object a1, final Object a2) {
    return new AST2(JacobiSymbol, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST jacobiZeta(final Object a1) {
    return new AST1(JacobiZeta, Object2Expr.convert(a1));
  }

  public static IAST jacobiZeta(final Object a1, final Object a2) {
    return new AST2(JacobiZeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST javaForm(final Object a1) {
    return new AST1(JavaForm, Object2Expr.convert(a1));
  }

  public static IAST javaForm(final Object a1, final Object a2) {
    return new AST2(JavaForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST javaNew(final Object a1) {
    return new AST1(JavaNew, Object2Expr.convert(a1));
  }

  public static IAST javaNew(final Object a1, final Object a2) {
    return new AST2(JavaNew, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST javaNew(final Object a1, final Object a2, final Object a3) {
    return new AST3(JavaNew, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST javaObjectQ(final Object a1) {
    return new AST1(JavaObjectQ, Object2Expr.convert(a1));
  }

  public static IAST join(final Object a1) {
    return new AST1(Join, Object2Expr.convert(a1));
  }

  public static IAST join(final Object a1, final Object a2) {
    return new AST2(Join, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST join(final Object a1, final Object a2, final Object a3) {
    return new AST3(Join, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST kOrderlessPartitions(final Object a1) {
    return new AST1(KOrderlessPartitions, Object2Expr.convert(a1));
  }

  public static IAST kOrderlessPartitions(final Object a1, final Object a2) {
    return new AST2(KOrderlessPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST kPartitions(final Object a1) {
    return new AST1(KPartitions, Object2Expr.convert(a1));
  }

  public static IAST kPartitions(final Object a1, final Object a2) {
    return new AST2(KPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST key(final Object a1) {
    return new AST1(Key, Object2Expr.convert(a1));
  }

  public static IAST keyExistsQ(final Object a1) {
    return new AST1(KeyExistsQ, Object2Expr.convert(a1));
  }

  public static IAST keyExistsQ(final Object a1, final Object a2) {
    return new AST2(KeyExistsQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST keySelect(final Object a1) {
    return new AST1(KeySelect, Object2Expr.convert(a1));
  }

  public static IAST keySelect(final Object a1, final Object a2) {
    return new AST2(KeySelect, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST keySort(final Object a1) {
    return new AST1(KeySort, Object2Expr.convert(a1));
  }

  public static IAST keySort(final Object a1, final Object a2) {
    return new AST2(KeySort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST keyTake(final Object a1) {
    return new AST1(KeyTake, Object2Expr.convert(a1));
  }

  public static IAST keyTake(final Object a1, final Object a2) {
    return new AST2(KeyTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST keys(final Object a1) {
    return new AST1(Keys, Object2Expr.convert(a1));
  }

  public static IAST keys(final Object a1, final Object a2) {
    return new AST2(Keys, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST kleinInvariantJ(final Object a1) {
    return new AST1(KleinInvariantJ, Object2Expr.convert(a1));
  }

  public static IAST kurtosis(final Object a1) {
    return new AST1(Kurtosis, Object2Expr.convert(a1));
  }

  public static IAST lcm(final Object a1) {
    return new AST1(LCM, Object2Expr.convert(a1));
  }

  public static IAST lcm(final Object a1, final Object a2) {
    return new AST2(LCM, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lcm(final Object a1, final Object a2, final Object a3) {
    return new AST3(LCM, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST lUDecomposition(final Object a1) {
    return new AST1(LUDecomposition, Object2Expr.convert(a1));
  }

  public static IAST lUDecomposition(final Object a1, final Object a2) {
    return new AST2(LUDecomposition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST laguerreL(final Object a1) {
    return new AST1(LaguerreL, Object2Expr.convert(a1));
  }

  public static IAST laguerreL(final Object a1, final Object a2) {
    return new AST2(LaguerreL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST laguerreL(final Object a1, final Object a2, final Object a3) {
    return new AST3(LaguerreL, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST lambertW(final Object a1) {
    return new AST1(LambertW, Object2Expr.convert(a1));
  }

  public static IAST laplaceTransform(final Object a1) {
    return new AST1(LaplaceTransform, Object2Expr.convert(a1));
  }

  public static IAST laplaceTransform(final Object a1, final Object a2) {
    return new AST2(LaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST laplaceTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(LaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST last(final Object a1) {
    return new AST1(Last, Object2Expr.convert(a1));
  }

  public static IAST last(final Object a1, final Object a2) {
    return new AST2(Last, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST leafCount(final Object a1) {
    return new AST1(LeafCount, Object2Expr.convert(a1));
  }

  public static IAST leastSquares(final Object a1) {
    return new AST1(LeastSquares, Object2Expr.convert(a1));
  }

  public static IAST leastSquares(final Object a1, final Object a2) {
    return new AST2(LeastSquares, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST legendreP(final Object a1) {
    return new AST1(LegendreP, Object2Expr.convert(a1));
  }

  public static IAST legendreP(final Object a1, final Object a2) {
    return new AST2(LegendreP, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST legendreP(final Object a1, final Object a2, final Object a3) {
    return new AST3(LegendreP, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST legendreQ(final Object a1) {
    return new AST1(LegendreQ, Object2Expr.convert(a1));
  }

  public static IAST legendreQ(final Object a1, final Object a2) {
    return new AST2(LegendreQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST legendreQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(LegendreQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST length(final Object a1) {
    return new AST1(Length, Object2Expr.convert(a1));
  }

  public static IAST lengthWhile(final Object a1) {
    return new AST1(LengthWhile, Object2Expr.convert(a1));
  }

  public static IAST lengthWhile(final Object a1, final Object a2) {
    return new AST2(LengthWhile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lerchPhi(final Object a1) {
    return new AST1(LerchPhi, Object2Expr.convert(a1));
  }

  public static IAST lerchPhi(final Object a1, final Object a2) {
    return new AST2(LerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lerchPhi(final Object a1, final Object a2, final Object a3) {
    return new AST3(LerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST less(final Object a1) {
    return new AST1(Less, Object2Expr.convert(a1));
  }

  public static IAST less(final Object a1, final Object a2) {
    return new AST2(Less, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST less(final Object a1, final Object a2, final Object a3) {
    return new AST3(Less, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST lessEqual(final Object a1) {
    return new AST1(LessEqual, Object2Expr.convert(a1));
  }

  public static IAST lessEqual(final Object a1, final Object a2) {
    return new AST2(LessEqual, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lessEqual(final Object a1, final Object a2, final Object a3) {
    return new AST3(LessEqual, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST lessEqualThan(final Object a1) {
    return new AST1(LessEqualThan, Object2Expr.convert(a1));
  }

  public static IAST lessThan(final Object a1) {
    return new AST1(LessThan, Object2Expr.convert(a1));
  }

  public static IAST letterCounts(final Object a1) {
    return new AST1(LetterCounts, Object2Expr.convert(a1));
  }

  public static IAST letterNumber(final Object a1) {
    return new AST1(LetterNumber, Object2Expr.convert(a1));
  }

  public static IAST letterNumber(final Object a1, final Object a2) {
    return new AST2(LetterNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST letterQ(final Object a1) {
    return new AST1(LetterQ, Object2Expr.convert(a1));
  }

  public static IAST level(final Object a1) {
    return new AST1(Level, Object2Expr.convert(a1));
  }

  public static IAST level(final Object a1, final Object a2) {
    return new AST2(Level, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST level(final Object a1, final Object a2, final Object a3) {
    return new AST3(Level, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST levelQ(final Object a1) {
    return new AST1(LevelQ, Object2Expr.convert(a1));
  }

  public static IAST leviCivitaTensor(final Object a1) {
    return new AST1(LeviCivitaTensor, Object2Expr.convert(a1));
  }

  public static IAST limit(final Object a1) {
    return new AST1(Limit, Object2Expr.convert(a1));
  }

  public static IAST limit(final Object a1, final Object a2) {
    return new AST2(Limit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST limit(final Object a1, final Object a2, final Object a3) {
    return new AST3(Limit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST line(final Object a1) {
    return new AST1(Line, Object2Expr.convert(a1));
  }

  public static IAST lineGraph(final Object a1) {
    return new AST1(LineGraph, Object2Expr.convert(a1));
  }

  public static IAST linearModelFit(final Object a1) {
    return new AST1(LinearModelFit, Object2Expr.convert(a1));
  }

  public static IAST linearModelFit(final Object a1, final Object a2) {
    return new AST2(LinearModelFit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST linearModelFit(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearModelFit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST linearProgramming(final Object a1) {
    return new AST1(LinearProgramming, Object2Expr.convert(a1));
  }

  public static IAST linearProgramming(final Object a1, final Object a2) {
    return new AST2(LinearProgramming, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST linearProgramming(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearProgramming, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST linearRecurrence(final Object a1) {
    return new AST1(LinearRecurrence, Object2Expr.convert(a1));
  }

  public static IAST linearRecurrence(final Object a1, final Object a2) {
    return new AST2(LinearRecurrence, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST linearRecurrence(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearRecurrence, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST linearSolve(final Object a1) {
    return new AST1(LinearSolve, Object2Expr.convert(a1));
  }

  public static IAST linearSolve(final Object a1, final Object a2) {
    return new AST2(LinearSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST linearSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST liouvilleLambda(final Object a1) {
    return new AST1(LiouvilleLambda, Object2Expr.convert(a1));
  }

  public static IAST listConvolve(final Object a1) {
    return new AST1(ListConvolve, Object2Expr.convert(a1));
  }

  public static IAST listConvolve(final Object a1, final Object a2) {
    return new AST2(ListConvolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST listCorrelate(final Object a1) {
    return new AST1(ListCorrelate, Object2Expr.convert(a1));
  }

  public static IAST listCorrelate(final Object a1, final Object a2) {
    return new AST2(ListCorrelate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST listQ(final Object a1) {
    return new AST1(ListQ, Object2Expr.convert(a1));
  }

  public static IAST literal(final Object a1) {
    return new AST1(Literal, Object2Expr.convert(a1));
  }

  public static IAST loadJavaClass(final Object a1) {
    return new AST1(LoadJavaClass, Object2Expr.convert(a1));
  }

  public static IAST log(final Object a1) {
    return new AST1(Log, Object2Expr.convert(a1));
  }

  public static IAST log(final Object a1, final Object a2) {
    return new AST2(Log, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST log10(final Object a1) {
    return new AST1(Log10, Object2Expr.convert(a1));
  }

  public static IAST log2(final Object a1) {
    return new AST1(Log2, Object2Expr.convert(a1));
  }

  public static IAST logGamma(final Object a1) {
    return new AST1(LogGamma, Object2Expr.convert(a1));
  }

  public static IAST logIntegral(final Object a1) {
    return new AST1(LogIntegral, Object2Expr.convert(a1));
  }

  public static IAST logicalExpand(final Object a1) {
    return new AST1(LogicalExpand, Object2Expr.convert(a1));
  }

  public static IAST logisticSigmoid(final Object a1) {
    return new AST1(LogisticSigmoid, Object2Expr.convert(a1));
  }

  public static IAST lookup(final Object a1) {
    return new AST1(Lookup, Object2Expr.convert(a1));
  }

  public static IAST lookup(final Object a1, final Object a2) {
    return new AST2(Lookup, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lookup(final Object a1, final Object a2, final Object a3) {
    return new AST3(Lookup, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST lowerCaseQ(final Object a1) {
    return new AST1(LowerCaseQ, Object2Expr.convert(a1));
  }

  public static IAST lowerTriangularize(final Object a1) {
    return new AST1(LowerTriangularize, Object2Expr.convert(a1));
  }

  public static IAST lowerTriangularize(final Object a1, final Object a2) {
    return new AST2(LowerTriangularize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST lucasL(final Object a1) {
    return new AST1(LucasL, Object2Expr.convert(a1));
  }

  public static IAST lucasL(final Object a1, final Object a2) {
    return new AST2(LucasL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST machineNumberQ(final Object a1) {
    return new AST1(MachineNumberQ, Object2Expr.convert(a1));
  }

  public static IAST makeBoxes(final Object a1) {
    return new AST1(MakeBoxes, Object2Expr.convert(a1));
  }

  public static IAST makeBoxes(final Object a1, final Object a2) {
    return new AST2(MakeBoxes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mangoldtLambda(final Object a1) {
    return new AST1(MangoldtLambda, Object2Expr.convert(a1));
  }

  public static IAST manhattanDistance(final Object a1) {
    return new AST1(ManhattanDistance, Object2Expr.convert(a1));
  }

  public static IAST manhattanDistance(final Object a1, final Object a2) {
    return new AST2(ManhattanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST manipulate(final Object a1) {
    return new AST1(Manipulate, Object2Expr.convert(a1));
  }

  public static IAST manipulate(final Object a1, final Object a2) {
    return new AST2(Manipulate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST manipulate(final Object a1, final Object a2, final Object a3) {
    return new AST3(Manipulate, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST mantissaExponent(final Object a1) {
    return new AST1(MantissaExponent, Object2Expr.convert(a1));
  }

  public static IAST mantissaExponent(final Object a1, final Object a2) {
    return new AST2(MantissaExponent, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST map(final Object a1) {
    return new AST1(Map, Object2Expr.convert(a1));
  }

  public static IAST map(final Object a1, final Object a2) {
    return new AST2(Map, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST map(final Object a1, final Object a2, final Object a3) {
    return new AST3(Map, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST mapAll(final Object a1) {
    return new AST1(MapAll, Object2Expr.convert(a1));
  }

  public static IAST mapAll(final Object a1, final Object a2) {
    return new AST2(MapAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mapAt(final Object a1) {
    return new AST1(MapAt, Object2Expr.convert(a1));
  }

  public static IAST mapAt(final Object a1, final Object a2) {
    return new AST2(MapAt, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mapAt(final Object a1, final Object a2, final Object a3) {
    return new AST3(MapAt, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST mapIndexed(final Object a1) {
    return new AST1(MapIndexed, Object2Expr.convert(a1));
  }

  public static IAST mapIndexed(final Object a1, final Object a2) {
    return new AST2(MapIndexed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mapIndexed(final Object a1, final Object a2, final Object a3) {
    return new AST3(MapIndexed, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST mapThread(final Object a1) {
    return new AST1(MapThread, Object2Expr.convert(a1));
  }

  public static IAST mapThread(final Object a1, final Object a2) {
    return new AST2(MapThread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mapThread(final Object a1, final Object a2, final Object a3) {
    return new AST3(MapThread, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST matchQ(final Object a1) {
    return new AST1(MatchQ, Object2Expr.convert(a1));
  }

  public static IAST matchQ(final Object a1, final Object a2) {
    return new AST2(MatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matchingDissimilarity(final Object a1) {
    return new AST1(MatchingDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST matchingDissimilarity(final Object a1, final Object a2) {
    return new AST2(MatchingDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mathMLForm(final Object a1) {
    return new AST1(MathMLForm, Object2Expr.convert(a1));
  }

  public static IAST matrixD(final Object a1) {
    return new AST1(MatrixD, Object2Expr.convert(a1));
  }

  public static IAST matrixD(final Object a1, final Object a2) {
    return new AST2(MatrixD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matrixD(final Object a1, final Object a2, final Object a3) {
    return new AST3(MatrixD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST matrixExp(final Object a1) {
    return new AST1(MatrixExp, Object2Expr.convert(a1));
  }

  public static IAST matrixFunction(final Object a1) {
    return new AST1(MatrixFunction, Object2Expr.convert(a1));
  }

  public static IAST matrixFunction(final Object a1, final Object a2) {
    return new AST2(MatrixFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matrixLog(final Object a1) {
    return new AST1(MatrixLog, Object2Expr.convert(a1));
  }

  public static IAST matrixMinimalPolynomial(final Object a1) {
    return new AST1(MatrixMinimalPolynomial, Object2Expr.convert(a1));
  }

  public static IAST matrixMinimalPolynomial(final Object a1, final Object a2) {
    return new AST2(MatrixMinimalPolynomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matrixPower(final Object a1) {
    return new AST1(MatrixPower, Object2Expr.convert(a1));
  }

  public static IAST matrixPower(final Object a1, final Object a2) {
    return new AST2(MatrixPower, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matrixQ(final Object a1) {
    return new AST1(MatrixQ, Object2Expr.convert(a1));
  }

  public static IAST matrixQ(final Object a1, final Object a2) {
    return new AST2(MatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST matrixRank(final Object a1) {
    return new AST1(MatrixRank, Object2Expr.convert(a1));
  }

  public static IAST matrixRank(final Object a1, final Object a2) {
    return new AST2(MatrixRank, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST max(final Object a1) {
    return new AST1(Max, Object2Expr.convert(a1));
  }

  public static IAST max(final Object a1, final Object a2) {
    return new AST2(Max, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST max(final Object a1, final Object a2, final Object a3) {
    return new AST3(Max, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST maxFilter(final Object a1) {
    return new AST1(MaxFilter, Object2Expr.convert(a1));
  }

  public static IAST maxFilter(final Object a1, final Object a2) {
    return new AST2(MaxFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST maxMemoryUsed(final Object a1) {
    return new AST1(MaxMemoryUsed, Object2Expr.convert(a1));
  }

  public static IAST maximize(final Object a1) {
    return new AST1(Maximize, Object2Expr.convert(a1));
  }

  public static IAST maximize(final Object a1, final Object a2) {
    return new AST2(Maximize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mean(final Object a1) {
    return new AST1(Mean, Object2Expr.convert(a1));
  }

  public static IAST meanDeviation(final Object a1) {
    return new AST1(MeanDeviation, Object2Expr.convert(a1));
  }

  public static IAST meanFilter(final Object a1) {
    return new AST1(MeanFilter, Object2Expr.convert(a1));
  }

  public static IAST meanFilter(final Object a1, final Object a2) {
    return new AST2(MeanFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST median(final Object a1) {
    return new AST1(Median, Object2Expr.convert(a1));
  }

  public static IAST medianFilter(final Object a1) {
    return new AST1(MedianFilter, Object2Expr.convert(a1));
  }

  public static IAST medianFilter(final Object a1, final Object a2) {
    return new AST2(MedianFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST memberQ(final Object a1) {
    return new AST1(MemberQ, Object2Expr.convert(a1));
  }

  public static IAST memberQ(final Object a1, final Object a2) {
    return new AST2(MemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST memberQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(MemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST memoryInUse(final Object a1) {
    return new AST1(MemoryInUse, Object2Expr.convert(a1));
  }

  public static IAST mersennePrimeExponent(final Object a1) {
    return new AST1(MersennePrimeExponent, Object2Expr.convert(a1));
  }

  public static IAST mersennePrimeExponentQ(final Object a1) {
    return new AST1(MersennePrimeExponentQ, Object2Expr.convert(a1));
  }

  public static IAST messageName(final Object a1) {
    return new AST1(MessageName, Object2Expr.convert(a1));
  }

  public static IAST messageName(final Object a1, final Object a2) {
    return new AST2(MessageName, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST messages(final Object a1) {
    return new AST1(Messages, Object2Expr.convert(a1));
  }

  public static IAST min(final Object a1) {
    return new AST1(Min, Object2Expr.convert(a1));
  }

  public static IAST min(final Object a1, final Object a2) {
    return new AST2(Min, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST min(final Object a1, final Object a2, final Object a3) {
    return new AST3(Min, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST minFilter(final Object a1) {
    return new AST1(MinFilter, Object2Expr.convert(a1));
  }

  public static IAST minFilter(final Object a1, final Object a2) {
    return new AST2(MinFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST minMax(final Object a1) {
    return new AST1(MinMax, Object2Expr.convert(a1));
  }

  public static IAST minMax(final Object a1, final Object a2) {
    return new AST2(MinMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST minimize(final Object a1) {
    return new AST1(Minimize, Object2Expr.convert(a1));
  }

  public static IAST minimize(final Object a1, final Object a2) {
    return new AST2(Minimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST minors(final Object a1) {
    return new AST1(Minors, Object2Expr.convert(a1));
  }

  public static IAST minus(final Object a1) {
    return new AST1(Minus, Object2Expr.convert(a1));
  }

  public static IAST missingQ(final Object a1) {
    return new AST1(MissingQ, Object2Expr.convert(a1));
  }

  public static IAST mod(final Object a1) {
    return new AST1(Mod, Object2Expr.convert(a1));
  }

  public static IAST mod(final Object a1, final Object a2) {
    return new AST2(Mod, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST mod(final Object a1, final Object a2, final Object a3) {
    return new AST3(Mod, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST module(final Object a1) {
    return new AST1(Module, Object2Expr.convert(a1));
  }

  public static IAST module(final Object a1, final Object a2) {
    return new AST2(Module, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST moebiusMu(final Object a1) {
    return new AST1(MoebiusMu, Object2Expr.convert(a1));
  }

  public static IAST monomialList(final Object a1) {
    return new AST1(MonomialList, Object2Expr.convert(a1));
  }

  public static IAST monomialList(final Object a1, final Object a2) {
    return new AST2(MonomialList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST monomialList(final Object a1, final Object a2, final Object a3) {
    return new AST3(MonomialList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST most(final Object a1) {
    return new AST1(Most, Object2Expr.convert(a1));
  }

  public static IAST multiplicativeOrder(final Object a1) {
    return new AST1(MultiplicativeOrder, Object2Expr.convert(a1));
  }

  public static IAST multiplicativeOrder(final Object a1, final Object a2) {
    return new AST2(MultiplicativeOrder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST multiplySides(final Object a1) {
    return new AST1(MultiplySides, Object2Expr.convert(a1));
  }

  public static IAST multiplySides(final Object a1, final Object a2) {
    return new AST2(MultiplySides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST n(final Object a1) {
    return new AST1(N, Object2Expr.convert(a1));
  }

  public static IAST n(final Object a1, final Object a2) {
    return new AST2(N, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nd(final Object a1) {
    return new AST1(ND, Object2Expr.convert(a1));
  }

  public static IAST nd(final Object a1, final Object a2) {
    return new AST2(ND, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nd(final Object a1, final Object a2, final Object a3) {
    return new AST3(ND, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST nDSolve(final Object a1) {
    return new AST1(NDSolve, Object2Expr.convert(a1));
  }

  public static IAST nDSolve(final Object a1, final Object a2) {
    return new AST2(NDSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nDSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(NDSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nFourierTransform(final Object a1) {
    return new AST1(NFourierTransform, Object2Expr.convert(a1));
  }

  public static IAST nFourierTransform(final Object a1, final Object a2) {
    return new AST2(NFourierTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nFourierTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(NFourierTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nIntegrate(final Object a1) {
    return new AST1(NIntegrate, Object2Expr.convert(a1));
  }

  public static IAST nIntegrate(final Object a1, final Object a2) {
    return new AST2(NIntegrate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nIntegrate(final Object a1, final Object a2, final Object a3) {
    return new AST3(NIntegrate, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nMaximize(final Object a1) {
    return new AST1(NMaximize, Object2Expr.convert(a1));
  }

  public static IAST nMaximize(final Object a1, final Object a2) {
    return new AST2(NMaximize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nMinimize(final Object a1) {
    return new AST1(NMinimize, Object2Expr.convert(a1));
  }

  public static IAST nMinimize(final Object a1, final Object a2) {
    return new AST2(NMinimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nRoots(final Object a1) {
    return new AST1(NRoots, Object2Expr.convert(a1));
  }

  public static IAST nRoots(final Object a1, final Object a2) {
    return new AST2(NRoots, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nSolve(final Object a1) {
    return new AST1(NSolve, Object2Expr.convert(a1));
  }

  public static IAST nSolve(final Object a1, final Object a2) {
    return new AST2(NSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(NSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nameQ(final Object a1) {
    return new AST1(NameQ, Object2Expr.convert(a1));
  }

  public static IAST names(final Object a1) {
    return new AST1(Names, Object2Expr.convert(a1));
  }

  public static IAST names(final Object a1, final Object a2) {
    return new AST2(Names, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nearest(final Object a1) {
    return new AST1(Nearest, Object2Expr.convert(a1));
  }

  public static IAST nearest(final Object a1, final Object a2) {
    return new AST2(Nearest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nearest(final Object a1, final Object a2, final Object a3) {
    return new AST3(Nearest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nearestTo(final Object a1) {
    return new AST1(NearestTo, Object2Expr.convert(a1));
  }

  public static IAST nearestTo(final Object a1, final Object a2) {
    return new AST2(NearestTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST needs(final Object a1) {
    return new AST1(Needs, Object2Expr.convert(a1));
  }

  public static IAST negative(final Object a1) {
    return new AST1(Negative, Object2Expr.convert(a1));
  }

  public static IAST nest(final Object a1) {
    return new AST1(Nest, Object2Expr.convert(a1));
  }

  public static IAST nest(final Object a1, final Object a2) {
    return new AST2(Nest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nest(final Object a1, final Object a2, final Object a3) {
    return new AST3(Nest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nestList(final Object a1) {
    return new AST1(NestList, Object2Expr.convert(a1));
  }

  public static IAST nestList(final Object a1, final Object a2) {
    return new AST2(NestList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nestList(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nestWhile(final Object a1) {
    return new AST1(NestWhile, Object2Expr.convert(a1));
  }

  public static IAST nestWhile(final Object a1, final Object a2) {
    return new AST2(NestWhile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nestWhile(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestWhile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nestWhileList(final Object a1) {
    return new AST1(NestWhileList, Object2Expr.convert(a1));
  }

  public static IAST nestWhileList(final Object a1, final Object a2) {
    return new AST2(NestWhileList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nestWhileList(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestWhileList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST newLimit(final Object a1) {
    return new AST1(NewLimit, Object2Expr.convert(a1));
  }

  public static IAST newLimit(final Object a1, final Object a2) {
    return new AST2(NewLimit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST newLimit(final Object a1, final Object a2, final Object a3) {
    return new AST3(NewLimit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST nextPrime(final Object a1) {
    return new AST1(NextPrime, Object2Expr.convert(a1));
  }

  public static IAST nextPrime(final Object a1, final Object a2) {
    return new AST2(NextPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST nonNegative(final Object a1) {
    return new AST1(NonNegative, Object2Expr.convert(a1));
  }

  public static IAST nonPositive(final Object a1) {
    return new AST1(NonPositive, Object2Expr.convert(a1));
  }

  public static IAST noneTrue(final Object a1) {
    return new AST1(NoneTrue, Object2Expr.convert(a1));
  }

  public static IAST noneTrue(final Object a1, final Object a2) {
    return new AST2(NoneTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST norm(final Object a1) {
    return new AST1(Norm, Object2Expr.convert(a1));
  }

  public static IAST norm(final Object a1, final Object a2) {
    return new AST2(Norm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST normal(final Object a1) {
    return new AST1(Normal, Object2Expr.convert(a1));
  }

  public static IAST normal(final Object a1, final Object a2) {
    return new AST2(Normal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST normalDistribution(final Object a1) {
    return new AST1(NormalDistribution, Object2Expr.convert(a1));
  }

  public static IAST normalDistribution(final Object a1, final Object a2) {
    return new AST2(NormalDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST normalize(final Object a1) {
    return new AST1(Normalize, Object2Expr.convert(a1));
  }

  public static IAST normalize(final Object a1, final Object a2) {
    return new AST2(Normalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST not(final Object a1) {
    return new AST1(Not, Object2Expr.convert(a1));
  }

  public static IAST notElement(final Object a1) {
    return new AST1(NotElement, Object2Expr.convert(a1));
  }

  public static IAST notElement(final Object a1, final Object a2) {
    return new AST2(NotElement, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST notListQ(final Object a1) {
    return new AST1(NotListQ, Object2Expr.convert(a1));
  }

  public static IAST nullSpace(final Object a1) {
    return new AST1(NullSpace, Object2Expr.convert(a1));
  }

  public static IAST numberQ(final Object a1) {
    return new AST1(NumberQ, Object2Expr.convert(a1));
  }

  public static IAST numerator(final Object a1) {
    return new AST1(Numerator, Object2Expr.convert(a1));
  }

  public static IAST numerator(final Object a1, final Object a2) {
    return new AST2(Numerator, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST numericArray(final Object a1) {
    return new AST1(NumericArray, Object2Expr.convert(a1));
  }

  public static IAST numericArray(final Object a1, final Object a2) {
    return new AST2(NumericArray, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST numericArrayQ(final Object a1) {
    return new AST1(NumericArrayQ, Object2Expr.convert(a1));
  }

  public static IAST numericArrayType(final Object a1) {
    return new AST1(NumericArrayType, Object2Expr.convert(a1));
  }

  public static IAST numericQ(final Object a1) {
    return new AST1(NumericQ, Object2Expr.convert(a1));
  }

  public static IAST nuttallWindow(final Object a1) {
    return new AST1(NuttallWindow, Object2Expr.convert(a1));
  }

  public static IAST octahedron(final Object a1) {
    return new AST1(Octahedron, Object2Expr.convert(a1));
  }

  public static IAST octahedron(final Object a1, final Object a2) {
    return new AST2(Octahedron, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST octahedron(final Object a1, final Object a2, final Object a3) {
    return new AST3(Octahedron, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST oddQ(final Object a1) {
    return new AST1(OddQ, Object2Expr.convert(a1));
  }

  public static IAST oddQ(final Object a1, final Object a2) {
    return new AST2(OddQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST off(final Object a1) {
    return new AST1(Off, Object2Expr.convert(a1));
  }

  public static IAST off(final Object a1, final Object a2) {
    return new AST2(Off, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST off(final Object a1, final Object a2, final Object a3) {
    return new AST3(Off, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST on(final Object a1) {
    return new AST1(On, Object2Expr.convert(a1));
  }

  public static IAST on(final Object a1, final Object a2) {
    return new AST2(On, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST on(final Object a1, final Object a2, final Object a3) {
    return new AST3(On, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST openAppend(final Object a1) {
    return new AST1(OpenAppend, Object2Expr.convert(a1));
  }

  public static IAST openRead(final Object a1) {
    return new AST1(OpenRead, Object2Expr.convert(a1));
  }

  public static IAST openWrite(final Object a1) {
    return new AST1(OpenWrite, Object2Expr.convert(a1));
  }

  public static IAST operate(final Object a1) {
    return new AST1(Operate, Object2Expr.convert(a1));
  }

  public static IAST operate(final Object a1, final Object a2) {
    return new AST2(Operate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST operate(final Object a1, final Object a2, final Object a3) {
    return new AST3(Operate, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST optimizeExpression(final Object a1) {
    return new AST1(OptimizeExpression, Object2Expr.convert(a1));
  }

  public static IAST optionValue(final Object a1) {
    return new AST1(OptionValue, Object2Expr.convert(a1));
  }

  public static IAST optionValue(final Object a1, final Object a2) {
    return new AST2(OptionValue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST optionValue(final Object a1, final Object a2, final Object a3) {
    return new AST3(OptionValue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST optional(final Object a1) {
    return new AST1(Optional, Object2Expr.convert(a1));
  }

  public static IAST optional(final Object a1, final Object a2) {
    return new AST2(Optional, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST options(final Object a1) {
    return new AST1(Options, Object2Expr.convert(a1));
  }

  public static IAST options(final Object a1, final Object a2) {
    return new AST2(Options, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST optionsPattern(final Object a1) {
    return new AST1(OptionsPattern, Object2Expr.convert(a1));
  }

  public static IAST order(final Object a1) {
    return new AST1(Order, Object2Expr.convert(a1));
  }

  public static IAST order(final Object a1, final Object a2) {
    return new AST2(Order, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST orderedQ(final Object a1) {
    return new AST1(OrderedQ, Object2Expr.convert(a1));
  }

  public static IAST orderedQ(final Object a1, final Object a2) {
    return new AST2(OrderedQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ordering(final Object a1) {
    return new AST1(Ordering, Object2Expr.convert(a1));
  }

  public static IAST ordering(final Object a1, final Object a2) {
    return new AST2(Ordering, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ordering(final Object a1, final Object a2, final Object a3) {
    return new AST3(Ordering, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST orthogonalMatrixQ(final Object a1) {
    return new AST1(OrthogonalMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST orthogonalize(final Object a1) {
    return new AST1(Orthogonalize, Object2Expr.convert(a1));
  }

  public static IAST orthogonalize(final Object a1, final Object a2) {
    return new AST2(Orthogonalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST out(final Object a1) {
    return new AST1(Out, Object2Expr.convert(a1));
  }

  public static IAST outer(final Object a1) {
    return new AST1(Outer, Object2Expr.convert(a1));
  }

  public static IAST outer(final Object a1, final Object a2) {
    return new AST2(Outer, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST outer(final Object a1, final Object a2, final Object a3) {
    return new AST3(Outer, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST outputStream(final Object a1) {
    return new AST1(OutputStream, Object2Expr.convert(a1));
  }

  public static IAST outputStream(final Object a1, final Object a2) {
    return new AST2(OutputStream, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ownValues(final Object a1) {
    return new AST1(OwnValues, Object2Expr.convert(a1));
  }

  public static IAST pdf(final Object a1) {
    return new AST1(PDF, Object2Expr.convert(a1));
  }

  public static IAST pdf(final Object a1, final Object a2) {
    return new AST2(PDF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST padLeft(final Object a1) {
    return new AST1(PadLeft, Object2Expr.convert(a1));
  }

  public static IAST padLeft(final Object a1, final Object a2) {
    return new AST2(PadLeft, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST padLeft(final Object a1, final Object a2, final Object a3) {
    return new AST3(PadLeft, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST padRight(final Object a1) {
    return new AST1(PadRight, Object2Expr.convert(a1));
  }

  public static IAST padRight(final Object a1, final Object a2) {
    return new AST2(PadRight, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST padRight(final Object a1, final Object a2, final Object a3) {
    return new AST3(PadRight, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST padeApproximant(final Object a1) {
    return new AST1(PadeApproximant, Object2Expr.convert(a1));
  }

  public static IAST padeApproximant(final Object a1, final Object a2) {
    return new AST2(PadeApproximant, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST paretoDistribution(final Object a1) {
    return new AST1(ParetoDistribution, Object2Expr.convert(a1));
  }

  public static IAST paretoDistribution(final Object a1, final Object a2) {
    return new AST2(ParetoDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST paretoDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(ParetoDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST part(final Object a1) {
    return new AST1(Part, Object2Expr.convert(a1));
  }

  public static IAST part(final Object a1, final Object a2) {
    return new AST2(Part, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST part(final Object a1, final Object a2, final Object a3) {
    return new AST3(Part, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST partition(final Object a1) {
    return new AST1(Partition, Object2Expr.convert(a1));
  }

  public static IAST partition(final Object a1, final Object a2) {
    return new AST2(Partition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST partition(final Object a1, final Object a2, final Object a3) {
    return new AST3(Partition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST partitionsP(final Object a1) {
    return new AST1(PartitionsP, Object2Expr.convert(a1));
  }

  public static IAST partitionsQ(final Object a1) {
    return new AST1(PartitionsQ, Object2Expr.convert(a1));
  }

  public static IAST parzenWindow(final Object a1) {
    return new AST1(ParzenWindow, Object2Expr.convert(a1));
  }

  public static IAST pattern(final Object a1) {
    return new AST1(Pattern, Object2Expr.convert(a1));
  }

  public static IAST pattern(final Object a1, final Object a2) {
    return new AST2(Pattern, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST patternTest(final Object a1) {
    return new AST1(PatternTest, Object2Expr.convert(a1));
  }

  public static IAST patternTest(final Object a1, final Object a2) {
    return new AST2(PatternTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST pauliMatrix(final Object a1) {
    return new AST1(PauliMatrix, Object2Expr.convert(a1));
  }

  public static IAST pause(final Object a1) {
    return new AST1(Pause, Object2Expr.convert(a1));
  }

  public static IAST perfectNumber(final Object a1) {
    return new AST1(PerfectNumber, Object2Expr.convert(a1));
  }

  public static IAST perfectNumberQ(final Object a1) {
    return new AST1(PerfectNumberQ, Object2Expr.convert(a1));
  }

  public static IAST permutationCycles(final Object a1) {
    return new AST1(PermutationCycles, Object2Expr.convert(a1));
  }

  public static IAST permutationCycles(final Object a1, final Object a2) {
    return new AST2(PermutationCycles, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST permutationCyclesQ(final Object a1) {
    return new AST1(PermutationCyclesQ, Object2Expr.convert(a1));
  }

  public static IAST permutationList(final Object a1) {
    return new AST1(PermutationList, Object2Expr.convert(a1));
  }

  public static IAST permutationList(final Object a1, final Object a2) {
    return new AST2(PermutationList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST permutationListQ(final Object a1) {
    return new AST1(PermutationListQ, Object2Expr.convert(a1));
  }

  public static IAST permutationReplace(final Object a1) {
    return new AST1(PermutationReplace, Object2Expr.convert(a1));
  }

  public static IAST permutationReplace(final Object a1, final Object a2) {
    return new AST2(PermutationReplace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST permutations(final Object a1) {
    return new AST1(Permutations, Object2Expr.convert(a1));
  }

  public static IAST permutations(final Object a1, final Object a2) {
    return new AST2(Permutations, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST permute(final Object a1) {
    return new AST1(Permute, Object2Expr.convert(a1));
  }

  public static IAST permute(final Object a1, final Object a2) {
    return new AST2(Permute, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST petersenGraph(final Object a1) {
    return new AST1(PetersenGraph, Object2Expr.convert(a1));
  }

  public static IAST petersenGraph(final Object a1, final Object a2) {
    return new AST2(PetersenGraph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST pick(final Object a1) {
    return new AST1(Pick, Object2Expr.convert(a1));
  }

  public static IAST pick(final Object a1, final Object a2) {
    return new AST2(Pick, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST pick(final Object a1, final Object a2, final Object a3) {
    return new AST3(Pick, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST piecewise(final Object a1) {
    return new AST1(Piecewise, Object2Expr.convert(a1));
  }

  public static IAST piecewise(final Object a1, final Object a2) {
    return new AST2(Piecewise, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST piecewiseExpand(final Object a1) {
    return new AST1(PiecewiseExpand, Object2Expr.convert(a1));
  }

  public static IAST piecewiseExpand(final Object a1, final Object a2) {
    return new AST2(PiecewiseExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST piecewiseExpand(final Object a1, final Object a2, final Object a3) {
    return new AST3(PiecewiseExpand, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST planarGraphQ(final Object a1) {
    return new AST1(PlanarGraphQ, Object2Expr.convert(a1));
  }

  public static IAST pochhammer(final Object a1) {
    return new AST1(Pochhammer, Object2Expr.convert(a1));
  }

  public static IAST pochhammer(final Object a1, final Object a2) {
    return new AST2(Pochhammer, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST point(final Object a1) {
    return new AST1(Point, Object2Expr.convert(a1));
  }

  public static IAST point(final Object a1, final Object a2) {
    return new AST2(Point, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST point(final Object a1, final Object a2, final Object a3) {
    return new AST3(Point, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST poissonDistribution(final Object a1) {
    return new AST1(PoissonDistribution, Object2Expr.convert(a1));
  }

  public static IAST polyGamma(final Object a1) {
    return new AST1(PolyGamma, Object2Expr.convert(a1));
  }

  public static IAST polyGamma(final Object a1, final Object a2) {
    return new AST2(PolyGamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polyLog(final Object a1) {
    return new AST1(PolyLog, Object2Expr.convert(a1));
  }

  public static IAST polyLog(final Object a1, final Object a2) {
    return new AST2(PolyLog, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polygon(final Object a1) {
    return new AST1(Polygon, Object2Expr.convert(a1));
  }

  public static IAST polygon(final Object a1, final Object a2) {
    return new AST2(Polygon, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polygon(final Object a1, final Object a2, final Object a3) {
    return new AST3(Polygon, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST polygonalNumber(final Object a1) {
    return new AST1(PolygonalNumber, Object2Expr.convert(a1));
  }

  public static IAST polygonalNumber(final Object a1, final Object a2) {
    return new AST2(PolygonalNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialExtendedGCD(final Object a1) {
    return new AST1(PolynomialExtendedGCD, Object2Expr.convert(a1));
  }

  public static IAST polynomialExtendedGCD(final Object a1, final Object a2) {
    return new AST2(PolynomialExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialExtendedGCD(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST polynomialQ(final Object a1) {
    return new AST1(PolynomialQ, Object2Expr.convert(a1));
  }

  public static IAST polynomialQ(final Object a1, final Object a2) {
    return new AST2(PolynomialQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialQuotient(final Object a1) {
    return new AST1(PolynomialQuotient, Object2Expr.convert(a1));
  }

  public static IAST polynomialQuotient(final Object a1, final Object a2) {
    return new AST2(PolynomialQuotient, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialQuotient(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialQuotient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST polynomialQuotientRemainder(final Object a1) {
    return new AST1(PolynomialQuotientRemainder, Object2Expr.convert(a1));
  }

  public static IAST polynomialQuotientRemainder(final Object a1, final Object a2) {
    return new AST2(PolynomialQuotientRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialQuotientRemainder(final Object a1, final Object a2,
      final Object a3) {
    return new AST3(PolynomialQuotientRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST polynomialRemainder(final Object a1) {
    return new AST1(PolynomialRemainder, Object2Expr.convert(a1));
  }

  public static IAST polynomialRemainder(final Object a1, final Object a2) {
    return new AST2(PolynomialRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST polynomialRemainder(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST position(final Object a1) {
    return new AST1(Position, Object2Expr.convert(a1));
  }

  public static IAST position(final Object a1, final Object a2) {
    return new AST2(Position, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST position(final Object a1, final Object a2, final Object a3) {
    return new AST3(Position, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST positive(final Object a1) {
    return new AST1(Positive, Object2Expr.convert(a1));
  }

  public static IAST possibleZeroQ(final Object a1) {
    return new AST1(PossibleZeroQ, Object2Expr.convert(a1));
  }

  public static IAST possibleZeroQ(final Object a1, final Object a2) {
    return new AST2(PossibleZeroQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST powerExpand(final Object a1) {
    return new AST1(PowerExpand, Object2Expr.convert(a1));
  }

  public static IAST powerExpand(final Object a1, final Object a2) {
    return new AST2(PowerExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST powerMod(final Object a1) {
    return new AST1(PowerMod, Object2Expr.convert(a1));
  }

  public static IAST powerMod(final Object a1, final Object a2) {
    return new AST2(PowerMod, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST powerMod(final Object a1, final Object a2, final Object a3) {
    return new AST3(PowerMod, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST preDecrement(final Object a1) {
    return new AST1(PreDecrement, Object2Expr.convert(a1));
  }

  public static IAST preIncrement(final Object a1) {
    return new AST1(PreIncrement, Object2Expr.convert(a1));
  }

  public static IAST precision(final Object a1) {
    return new AST1(Precision, Object2Expr.convert(a1));
  }

  public static IAST prepend(final Object a1) {
    return new AST1(Prepend, Object2Expr.convert(a1));
  }

  public static IAST prepend(final Object a1, final Object a2) {
    return new AST2(Prepend, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST prependTo(final Object a1) {
    return new AST1(PrependTo, Object2Expr.convert(a1));
  }

  public static IAST prependTo(final Object a1, final Object a2) {
    return new AST2(PrependTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST prime(final Object a1) {
    return new AST1(Prime, Object2Expr.convert(a1));
  }

  public static IAST primeOmega(final Object a1) {
    return new AST1(PrimeOmega, Object2Expr.convert(a1));
  }

  public static IAST primePi(final Object a1) {
    return new AST1(PrimePi, Object2Expr.convert(a1));
  }

  public static IAST primePowerQ(final Object a1) {
    return new AST1(PrimePowerQ, Object2Expr.convert(a1));
  }

  public static IAST primeQ(final Object a1) {
    return new AST1(PrimeQ, Object2Expr.convert(a1));
  }

  public static IAST primeQ(final Object a1, final Object a2) {
    return new AST2(PrimeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST primitiveRoot(final Object a1) {
    return new AST1(PrimitiveRoot, Object2Expr.convert(a1));
  }

  public static IAST primitiveRoot(final Object a1, final Object a2) {
    return new AST2(PrimitiveRoot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST primitiveRootList(final Object a1) {
    return new AST1(PrimitiveRootList, Object2Expr.convert(a1));
  }

  public static IAST printableASCIIQ(final Object a1) {
    return new AST1(PrintableASCIIQ, Object2Expr.convert(a1));
  }

  public static IAST product(final Object a1) {
    return new AST1(Product, Object2Expr.convert(a1));
  }

  public static IAST product(final Object a1, final Object a2) {
    return new AST2(Product, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST product(final Object a1, final Object a2, final Object a3) {
    return new AST3(Product, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST productLog(final Object a1) {
    return new AST1(ProductLog, Object2Expr.convert(a1));
  }

  public static IAST productLog(final Object a1, final Object a2) {
    return new AST2(ProductLog, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST projection(final Object a1) {
    return new AST1(Projection, Object2Expr.convert(a1));
  }

  public static IAST projection(final Object a1, final Object a2) {
    return new AST2(Projection, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST projection(final Object a1, final Object a2, final Object a3) {
    return new AST3(Projection, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST pseudoInverse(final Object a1) {
    return new AST1(PseudoInverse, Object2Expr.convert(a1));
  }

  public static IAST pseudoInverse(final Object a1, final Object a2) {
    return new AST2(PseudoInverse, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST put(final Object a1) {
    return new AST1(Put, Object2Expr.convert(a1));
  }

  public static IAST put(final Object a1, final Object a2) {
    return new AST2(Put, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST put(final Object a1, final Object a2, final Object a3) {
    return new AST3(Put, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST qRDecomposition(final Object a1) {
    return new AST1(QRDecomposition, Object2Expr.convert(a1));
  }

  public static IAST quadraticIrrationalQ(final Object a1) {
    return new AST1(QuadraticIrrationalQ, Object2Expr.convert(a1));
  }

  public static IAST quantile(final Object a1) {
    return new AST1(Quantile, Object2Expr.convert(a1));
  }

  public static IAST quantile(final Object a1, final Object a2) {
    return new AST2(Quantile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quantile(final Object a1, final Object a2, final Object a3) {
    return new AST3(Quantile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST quantity(final Object a1) {
    return new AST1(Quantity, Object2Expr.convert(a1));
  }

  public static IAST quantity(final Object a1, final Object a2) {
    return new AST2(Quantity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quantityMagnitude(final Object a1) {
    return new AST1(QuantityMagnitude, Object2Expr.convert(a1));
  }

  public static IAST quantityMagnitude(final Object a1, final Object a2) {
    return new AST2(QuantityMagnitude, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quantityQ(final Object a1) {
    return new AST1(QuantityQ, Object2Expr.convert(a1));
  }

  public static IAST quantityQ(final Object a1, final Object a2) {
    return new AST2(QuantityQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quantityUnit(final Object a1) {
    return new AST1(QuantityUnit, Object2Expr.convert(a1));
  }

  public static IAST quartiles(final Object a1) {
    return new AST1(Quartiles, Object2Expr.convert(a1));
  }

  public static IAST quartiles(final Object a1, final Object a2) {
    return new AST2(Quartiles, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quiet(final Object a1) {
    return new AST1(Quiet, Object2Expr.convert(a1));
  }

  public static IAST quiet(final Object a1, final Object a2) {
    return new AST2(Quiet, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quit(final Object a1) {
    return new AST1(Quit, Object2Expr.convert(a1));
  }

  public static IAST quotient(final Object a1) {
    return new AST1(Quotient, Object2Expr.convert(a1));
  }

  public static IAST quotient(final Object a1, final Object a2) {
    return new AST2(Quotient, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST quotient(final Object a1, final Object a2, final Object a3) {
    return new AST3(Quotient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST quotientRemainder(final Object a1) {
    return new AST1(QuotientRemainder, Object2Expr.convert(a1));
  }

  public static IAST quotientRemainder(final Object a1, final Object a2) {
    return new AST2(QuotientRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rGBColor(final Object a1) {
    return new AST1(RGBColor, Object2Expr.convert(a1));
  }

  public static IAST rGBColor(final Object a1, final Object a2) {
    return new AST2(RGBColor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rGBColor(final Object a1, final Object a2, final Object a3) {
    return new AST3(RGBColor, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST ramp(final Object a1) {
    return new AST1(Ramp, Object2Expr.convert(a1));
  }

  public static IAST randomChoice(final Object a1) {
    return new AST1(RandomChoice, Object2Expr.convert(a1));
  }

  public static IAST randomChoice(final Object a1, final Object a2) {
    return new AST2(RandomChoice, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomComplex(final Object a1) {
    return new AST1(RandomComplex, Object2Expr.convert(a1));
  }

  public static IAST randomComplex(final Object a1, final Object a2) {
    return new AST2(RandomComplex, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomGraph(final Object a1) {
    return new AST1(RandomGraph, Object2Expr.convert(a1));
  }

  public static IAST randomGraph(final Object a1, final Object a2) {
    return new AST2(RandomGraph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomInteger(final Object a1) {
    return new AST1(RandomInteger, Object2Expr.convert(a1));
  }

  public static IAST randomInteger(final Object a1, final Object a2) {
    return new AST2(RandomInteger, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomPermutation(final Object a1) {
    return new AST1(RandomPermutation, Object2Expr.convert(a1));
  }

  public static IAST randomPermutation(final Object a1, final Object a2) {
    return new AST2(RandomPermutation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomPrime(final Object a1) {
    return new AST1(RandomPrime, Object2Expr.convert(a1));
  }

  public static IAST randomPrime(final Object a1, final Object a2) {
    return new AST2(RandomPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomReal(final Object a1) {
    return new AST1(RandomReal, Object2Expr.convert(a1));
  }

  public static IAST randomReal(final Object a1, final Object a2) {
    return new AST2(RandomReal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomSample(final Object a1) {
    return new AST1(RandomSample, Object2Expr.convert(a1));
  }

  public static IAST randomSample(final Object a1, final Object a2) {
    return new AST2(RandomSample, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST randomVariate(final Object a1) {
    return new AST1(RandomVariate, Object2Expr.convert(a1));
  }

  public static IAST randomVariate(final Object a1, final Object a2) {
    return new AST2(RandomVariate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST range(final Object a1) {
    return new AST1(Range, Object2Expr.convert(a1));
  }

  public static IAST range(final Object a1, final Object a2) {
    return new AST2(Range, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST range(final Object a1, final Object a2, final Object a3) {
    return new AST3(Range, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST rankedMax(final Object a1) {
    return new AST1(RankedMax, Object2Expr.convert(a1));
  }

  public static IAST rankedMax(final Object a1, final Object a2) {
    return new AST2(RankedMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rankedMin(final Object a1) {
    return new AST1(RankedMin, Object2Expr.convert(a1));
  }

  public static IAST rankedMin(final Object a1, final Object a2) {
    return new AST2(RankedMin, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rational(final Object a1) {
    return new AST1(Rational, Object2Expr.convert(a1));
  }

  public static IAST rational(final Object a1, final Object a2) {
    return new AST2(Rational, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rationalize(final Object a1) {
    return new AST1(Rationalize, Object2Expr.convert(a1));
  }

  public static IAST rationalize(final Object a1, final Object a2) {
    return new AST2(Rationalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST re(final Object a1) {
    return new AST1(Re, Object2Expr.convert(a1));
  }

  public static IAST read(final Object a1) {
    return new AST1(Read, Object2Expr.convert(a1));
  }

  public static IAST read(final Object a1, final Object a2) {
    return new AST2(Read, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST readString(final Object a1) {
    return new AST1(ReadString, Object2Expr.convert(a1));
  }

  public static IAST realAbs(final Object a1) {
    return new AST1(RealAbs, Object2Expr.convert(a1));
  }

  public static IAST realDigits(final Object a1) {
    return new AST1(RealDigits, Object2Expr.convert(a1));
  }

  public static IAST realNumberQ(final Object a1) {
    return new AST1(RealNumberQ, Object2Expr.convert(a1));
  }

  public static IAST realSign(final Object a1) {
    return new AST1(RealSign, Object2Expr.convert(a1));
  }

  public static IAST reap(final Object a1) {
    return new AST1(Reap, Object2Expr.convert(a1));
  }

  public static IAST reap(final Object a1, final Object a2) {
    return new AST2(Reap, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST reap(final Object a1, final Object a2, final Object a3) {
    return new AST3(Reap, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST rectangle(final Object a1) {
    return new AST1(Rectangle, Object2Expr.convert(a1));
  }

  public static IAST rectangle(final Object a1, final Object a2) {
    return new AST2(Rectangle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rectangle(final Object a1, final Object a2, final Object a3) {
    return new AST3(Rectangle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST reduce(final Object a1) {
    return new AST1(Reduce, Object2Expr.convert(a1));
  }

  public static IAST reduce(final Object a1, final Object a2) {
    return new AST2(Reduce, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST reduce(final Object a1, final Object a2, final Object a3) {
    return new AST3(Reduce, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST refine(final Object a1) {
    return new AST1(Refine, Object2Expr.convert(a1));
  }

  public static IAST refine(final Object a1, final Object a2) {
    return new AST2(Refine, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST refine(final Object a1, final Object a2, final Object a3) {
    return new AST3(Refine, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST releaseHold(final Object a1) {
    return new AST1(ReleaseHold, Object2Expr.convert(a1));
  }

  public static IAST removeDiacritics(final Object a1) {
    return new AST1(RemoveDiacritics, Object2Expr.convert(a1));
  }

  public static IAST repeated(final Object a1) {
    return new AST1(Repeated, Object2Expr.convert(a1));
  }

  public static IAST repeated(final Object a1, final Object a2) {
    return new AST2(Repeated, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST repeatedNull(final Object a1) {
    return new AST1(RepeatedNull, Object2Expr.convert(a1));
  }

  public static IAST repeatedNull(final Object a1, final Object a2) {
    return new AST2(RepeatedNull, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST repeatedTiming(final Object a1) {
    return new AST1(RepeatedTiming, Object2Expr.convert(a1));
  }

  public static IAST replace(final Object a1) {
    return new AST1(Replace, Object2Expr.convert(a1));
  }

  public static IAST replace(final Object a1, final Object a2) {
    return new AST2(Replace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST replace(final Object a1, final Object a2, final Object a3) {
    return new AST3(Replace, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST replaceAll(final Object a1) {
    return new AST1(ReplaceAll, Object2Expr.convert(a1));
  }

  public static IAST replaceAll(final Object a1, final Object a2) {
    return new AST2(ReplaceAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST replaceList(final Object a1) {
    return new AST1(ReplaceList, Object2Expr.convert(a1));
  }

  public static IAST replaceList(final Object a1, final Object a2) {
    return new AST2(ReplaceList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST replaceList(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplaceList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST replacePart(final Object a1) {
    return new AST1(ReplacePart, Object2Expr.convert(a1));
  }

  public static IAST replacePart(final Object a1, final Object a2) {
    return new AST2(ReplacePart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST replacePart(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplacePart, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST replaceRepeated(final Object a1) {
    return new AST1(ReplaceRepeated, Object2Expr.convert(a1));
  }

  public static IAST replaceRepeated(final Object a1, final Object a2) {
    return new AST2(ReplaceRepeated, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST replaceRepeated(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplaceRepeated, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST rescale(final Object a1) {
    return new AST1(Rescale, Object2Expr.convert(a1));
  }

  public static IAST rescale(final Object a1, final Object a2) {
    return new AST2(Rescale, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rescale(final Object a1, final Object a2, final Object a3) {
    return new AST3(Rescale, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST rest(final Object a1) {
    return new AST1(Rest, Object2Expr.convert(a1));
  }

  public static IAST resultant(final Object a1) {
    return new AST1(Resultant, Object2Expr.convert(a1));
  }

  public static IAST resultant(final Object a1, final Object a2) {
    return new AST2(Resultant, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST resultant(final Object a1, final Object a2, final Object a3) {
    return new AST3(Resultant, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST $return(final Object a1) {
    return new AST1(Return, Object2Expr.convert(a1));
  }

  public static IAST reverse(final Object a1) {
    return new AST1(Reverse, Object2Expr.convert(a1));
  }

  public static IAST riccatiSolve(final Object a1) {
    return new AST1(RiccatiSolve, Object2Expr.convert(a1));
  }

  public static IAST riccatiSolve(final Object a1, final Object a2) {
    return new AST2(RiccatiSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST riffle(final Object a1) {
    return new AST1(Riffle, Object2Expr.convert(a1));
  }

  public static IAST riffle(final Object a1, final Object a2) {
    return new AST2(Riffle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rogersTanimotoDissimilarity(final Object a1) {
    return new AST1(RogersTanimotoDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST rogersTanimotoDissimilarity(final Object a1, final Object a2) {
    return new AST2(RogersTanimotoDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST romanNumeral(final Object a1) {
    return new AST1(RomanNumeral, Object2Expr.convert(a1));
  }

  public static IAST rootIntervals(final Object a1) {
    return new AST1(RootIntervals, Object2Expr.convert(a1));
  }

  public static IAST rootReduce(final Object a1) {
    return new AST1(RootReduce, Object2Expr.convert(a1));
  }

  public static IAST roots(final Object a1) {
    return new AST1(Roots, Object2Expr.convert(a1));
  }

  public static IAST roots(final Object a1, final Object a2) {
    return new AST2(Roots, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rotateLeft(final Object a1) {
    return new AST1(RotateLeft, Object2Expr.convert(a1));
  }

  public static IAST rotateLeft(final Object a1, final Object a2) {
    return new AST2(RotateLeft, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rotateRight(final Object a1) {
    return new AST1(RotateRight, Object2Expr.convert(a1));
  }

  public static IAST rotateRight(final Object a1, final Object a2) {
    return new AST2(RotateRight, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST round(final Object a1) {
    return new AST1(Round, Object2Expr.convert(a1));
  }

  public static IAST round(final Object a1, final Object a2) {
    return new AST2(Round, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rowReduce(final Object a1) {
    return new AST1(RowReduce, Object2Expr.convert(a1));
  }

  public static IAST rowReduce(final Object a1, final Object a2) {
    return new AST2(RowReduce, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST rule(final Object a1) {
    return new AST1(Rule, Object2Expr.convert(a1));
  }

  public static IAST rule(final Object a1, final Object a2) {
    return new AST2(Rule, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST ruleDelayed(final Object a1) {
    return new AST1(RuleDelayed, Object2Expr.convert(a1));
  }

  public static IAST ruleDelayed(final Object a1, final Object a2) {
    return new AST2(RuleDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST russellRaoDissimilarity(final Object a1) {
    return new AST1(RussellRaoDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST russellRaoDissimilarity(final Object a1, final Object a2) {
    return new AST2(RussellRaoDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sameObjectQ(final Object a1) {
    return new AST1(SameObjectQ, Object2Expr.convert(a1));
  }

  public static IAST sameObjectQ(final Object a1, final Object a2) {
    return new AST2(SameObjectQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST satisfiabilityCount(final Object a1) {
    return new AST1(SatisfiabilityCount, Object2Expr.convert(a1));
  }

  public static IAST satisfiabilityCount(final Object a1, final Object a2) {
    return new AST2(SatisfiabilityCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST satisfiabilityCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiabilityCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST satisfiabilityInstances(final Object a1) {
    return new AST1(SatisfiabilityInstances, Object2Expr.convert(a1));
  }

  public static IAST satisfiabilityInstances(final Object a1, final Object a2) {
    return new AST2(SatisfiabilityInstances, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST satisfiabilityInstances(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiabilityInstances, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST satisfiableQ(final Object a1) {
    return new AST1(SatisfiableQ, Object2Expr.convert(a1));
  }

  public static IAST satisfiableQ(final Object a1, final Object a2) {
    return new AST2(SatisfiableQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST satisfiableQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiableQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST scaled(final Object a1) {
    return new AST1(Scaled, Object2Expr.convert(a1));
  }

  public static IAST scaled(final Object a1, final Object a2) {
    return new AST2(Scaled, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST scan(final Object a1) {
    return new AST1(Scan, Object2Expr.convert(a1));
  }

  public static IAST scan(final Object a1, final Object a2) {
    return new AST2(Scan, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST scan(final Object a1, final Object a2, final Object a3) {
    return new AST3(Scan, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST sec(final Object a1) {
    return new AST1(Sec, Object2Expr.convert(a1));
  }

  public static IAST sech(final Object a1) {
    return new AST1(Sech, Object2Expr.convert(a1));
  }

  public static IAST seedRandom(final Object a1) {
    return new AST1(SeedRandom, Object2Expr.convert(a1));
  }

  public static IAST select(final Object a1) {
    return new AST1(Select, Object2Expr.convert(a1));
  }

  public static IAST select(final Object a1, final Object a2) {
    return new AST2(Select, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST select(final Object a1, final Object a2, final Object a3) {
    return new AST3(Select, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST selectFirst(final Object a1) {
    return new AST1(SelectFirst, Object2Expr.convert(a1));
  }

  public static IAST selectFirst(final Object a1, final Object a2) {
    return new AST2(SelectFirst, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST selectFirst(final Object a1, final Object a2, final Object a3) {
    return new AST3(SelectFirst, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST semanticImport(final Object a1) {
    return new AST1(SemanticImport, Object2Expr.convert(a1));
  }

  public static IAST semanticImport(final Object a1, final Object a2) {
    return new AST2(SemanticImport, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST semanticImportString(final Object a1) {
    return new AST1(SemanticImportString, Object2Expr.convert(a1));
  }

  public static IAST semanticImportString(final Object a1, final Object a2) {
    return new AST2(SemanticImportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST set(final Object a1) {
    return new AST1(Set, Object2Expr.convert(a1));
  }

  public static IAST set(final Object a1, final Object a2) {
    return new AST2(Set, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST setDelayed(final Object a1) {
    return new AST1(SetDelayed, Object2Expr.convert(a1));
  }

  public static IAST setDelayed(final Object a1, final Object a2) {
    return new AST2(SetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST share(final Object a1) {
    return new AST1(Share, Object2Expr.convert(a1));
  }

  public static IAST $short(final Object a1) {
    return new AST1(Short, Object2Expr.convert(a1));
  }

  public static IAST sign(final Object a1) {
    return new AST1(Sign, Object2Expr.convert(a1));
  }

  public static IAST signCmp(final Object a1) {
    return new AST1(SignCmp, Object2Expr.convert(a1));
  }

  public static IAST signature(final Object a1) {
    return new AST1(Signature, Object2Expr.convert(a1));
  }

  public static IAST simplify(final Object a1) {
    return new AST1(Simplify, Object2Expr.convert(a1));
  }

  public static IAST simplify(final Object a1, final Object a2) {
    return new AST2(Simplify, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST simplify(final Object a1, final Object a2, final Object a3) {
    return new AST3(Simplify, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST sin(final Object a1) {
    return new AST1(Sin, Object2Expr.convert(a1));
  }

  public static IAST sinIntegral(final Object a1) {
    return new AST1(SinIntegral, Object2Expr.convert(a1));
  }

  public static IAST sinc(final Object a1) {
    return new AST1(Sinc, Object2Expr.convert(a1));
  }

  public static IAST singularValueDecomposition(final Object a1) {
    return new AST1(SingularValueDecomposition, Object2Expr.convert(a1));
  }

  public static IAST singularValueList(final Object a1) {
    return new AST1(SingularValueList, Object2Expr.convert(a1));
  }

  public static IAST singularValueList(final Object a1, final Object a2) {
    return new AST2(SingularValueList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sinh(final Object a1) {
    return new AST1(Sinh, Object2Expr.convert(a1));
  }

  public static IAST sinhIntegral(final Object a1) {
    return new AST1(SinhIntegral, Object2Expr.convert(a1));
  }

  public static IAST skewness(final Object a1) {
    return new AST1(Skewness, Object2Expr.convert(a1));
  }

  public static IAST sokalSneathDissimilarity(final Object a1) {
    return new AST1(SokalSneathDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST sokalSneathDissimilarity(final Object a1, final Object a2) {
    return new AST2(SokalSneathDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST solve(final Object a1) {
    return new AST1(Solve, Object2Expr.convert(a1));
  }

  public static IAST solve(final Object a1, final Object a2) {
    return new AST2(Solve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST solve(final Object a1, final Object a2, final Object a3) {
    return new AST3(Solve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST sort(final Object a1) {
    return new AST1(Sort, Object2Expr.convert(a1));
  }

  public static IAST sort(final Object a1, final Object a2) {
    return new AST2(Sort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sortBy(final Object a1) {
    return new AST1(SortBy, Object2Expr.convert(a1));
  }

  public static IAST sortBy(final Object a1, final Object a2) {
    return new AST2(SortBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sow(final Object a1) {
    return new AST1(Sow, Object2Expr.convert(a1));
  }

  public static IAST sow(final Object a1, final Object a2) {
    return new AST2(Sow, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sparseArray(final Object a1) {
    return new AST1(SparseArray, Object2Expr.convert(a1));
  }

  public static IAST sparseArray(final Object a1, final Object a2) {
    return new AST2(SparseArray, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sparseArray(final Object a1, final Object a2, final Object a3) {
    return new AST3(SparseArray, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST sphere(final Object a1) {
    return new AST1(Sphere, Object2Expr.convert(a1));
  }

  public static IAST sphere(final Object a1, final Object a2) {
    return new AST2(Sphere, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sphericalBesselJ(final Object a1) {
    return new AST1(SphericalBesselJ, Object2Expr.convert(a1));
  }

  public static IAST sphericalBesselJ(final Object a1, final Object a2) {
    return new AST2(SphericalBesselJ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sphericalBesselY(final Object a1) {
    return new AST1(SphericalBesselY, Object2Expr.convert(a1));
  }

  public static IAST sphericalBesselY(final Object a1, final Object a2) {
    return new AST2(SphericalBesselY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sphericalHankelH1(final Object a1) {
    return new AST1(SphericalHankelH1, Object2Expr.convert(a1));
  }

  public static IAST sphericalHankelH1(final Object a1, final Object a2) {
    return new AST2(SphericalHankelH1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sphericalHankelH2(final Object a1) {
    return new AST1(SphericalHankelH2, Object2Expr.convert(a1));
  }

  public static IAST sphericalHankelH2(final Object a1, final Object a2) {
    return new AST2(SphericalHankelH2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST split(final Object a1) {
    return new AST1(Split, Object2Expr.convert(a1));
  }

  public static IAST split(final Object a1, final Object a2) {
    return new AST2(Split, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST splitBy(final Object a1) {
    return new AST1(SplitBy, Object2Expr.convert(a1));
  }

  public static IAST splitBy(final Object a1, final Object a2) {
    return new AST2(SplitBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sqrt(final Object a1) {
    return new AST1(Sqrt, Object2Expr.convert(a1));
  }

  public static IAST squareFreeQ(final Object a1) {
    return new AST1(SquareFreeQ, Object2Expr.convert(a1));
  }

  public static IAST squareFreeQ(final Object a1, final Object a2) {
    return new AST2(SquareFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST squareMatrixQ(final Object a1) {
    return new AST1(SquareMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST squaredEuclideanDistance(final Object a1) {
    return new AST1(SquaredEuclideanDistance, Object2Expr.convert(a1));
  }

  public static IAST squaredEuclideanDistance(final Object a1, final Object a2) {
    return new AST2(SquaredEuclideanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stack(final Object a1) {
    return new AST1(Stack, Object2Expr.convert(a1));
  }

  public static IAST stackBegin(final Object a1) {
    return new AST1(StackBegin, Object2Expr.convert(a1));
  }

  public static IAST standardDeviation(final Object a1) {
    return new AST1(StandardDeviation, Object2Expr.convert(a1));
  }

  public static IAST standardize(final Object a1) {
    return new AST1(Standardize, Object2Expr.convert(a1));
  }

  public static IAST starGraph(final Object a1) {
    return new AST1(StarGraph, Object2Expr.convert(a1));
  }

  public static IAST stieltjesGamma(final Object a1) {
    return new AST1(StieltjesGamma, Object2Expr.convert(a1));
  }

  public static IAST stieltjesGamma(final Object a1, final Object a2) {
    return new AST2(StieltjesGamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stirlingS1(final Object a1) {
    return new AST1(StirlingS1, Object2Expr.convert(a1));
  }

  public static IAST stirlingS1(final Object a1, final Object a2) {
    return new AST2(StirlingS1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stirlingS2(final Object a1) {
    return new AST1(StirlingS2, Object2Expr.convert(a1));
  }

  public static IAST stirlingS2(final Object a1, final Object a2) {
    return new AST2(StirlingS2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringCases(final Object a1) {
    return new AST1(StringCases, Object2Expr.convert(a1));
  }

  public static IAST stringCases(final Object a1, final Object a2) {
    return new AST2(StringCases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringCases(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringCases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringContainsQ(final Object a1) {
    return new AST1(StringContainsQ, Object2Expr.convert(a1));
  }

  public static IAST stringContainsQ(final Object a1, final Object a2) {
    return new AST2(StringContainsQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringContainsQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringContainsQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringCount(final Object a1) {
    return new AST1(StringCount, Object2Expr.convert(a1));
  }

  public static IAST stringCount(final Object a1, final Object a2) {
    return new AST2(StringCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringDrop(final Object a1) {
    return new AST1(StringDrop, Object2Expr.convert(a1));
  }

  public static IAST stringDrop(final Object a1, final Object a2) {
    return new AST2(StringDrop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringExpression(final Object a1) {
    return new AST1(StringExpression, Object2Expr.convert(a1));
  }

  public static IAST stringExpression(final Object a1, final Object a2) {
    return new AST2(StringExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringFormat(final Object a1) {
    return new AST1(StringFormat, Object2Expr.convert(a1));
  }

  public static IAST stringFreeQ(final Object a1) {
    return new AST1(StringFreeQ, Object2Expr.convert(a1));
  }

  public static IAST stringFreeQ(final Object a1, final Object a2) {
    return new AST2(StringFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringFreeQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringInsert(final Object a1) {
    return new AST1(StringInsert, Object2Expr.convert(a1));
  }

  public static IAST stringInsert(final Object a1, final Object a2) {
    return new AST2(StringInsert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringInsert(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringInsert, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringLength(final Object a1) {
    return new AST1(StringLength, Object2Expr.convert(a1));
  }

  public static IAST stringMatchQ(final Object a1) {
    return new AST1(StringMatchQ, Object2Expr.convert(a1));
  }

  public static IAST stringMatchQ(final Object a1, final Object a2) {
    return new AST2(StringMatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringMatchQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringMatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringPart(final Object a1) {
    return new AST1(StringPart, Object2Expr.convert(a1));
  }

  public static IAST stringPart(final Object a1, final Object a2) {
    return new AST2(StringPart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringPosition(final Object a1) {
    return new AST1(StringPosition, Object2Expr.convert(a1));
  }

  public static IAST stringPosition(final Object a1, final Object a2) {
    return new AST2(StringPosition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringPosition(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringPosition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringQ(final Object a1) {
    return new AST1(StringQ, Object2Expr.convert(a1));
  }

  public static IAST stringRepeat(final Object a1) {
    return new AST1(StringRepeat, Object2Expr.convert(a1));
  }

  public static IAST stringRepeat(final Object a1, final Object a2) {
    return new AST2(StringRepeat, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringRepeat(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringRepeat, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringReplace(final Object a1) {
    return new AST1(StringReplace, Object2Expr.convert(a1));
  }

  public static IAST stringReplace(final Object a1, final Object a2) {
    return new AST2(StringReplace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringReplace(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringReplace, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringReverse(final Object a1) {
    return new AST1(StringReverse, Object2Expr.convert(a1));
  }

  public static IAST stringRiffle(final Object a1) {
    return new AST1(StringRiffle, Object2Expr.convert(a1));
  }

  public static IAST stringRiffle(final Object a1, final Object a2) {
    return new AST2(StringRiffle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringRiffle(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringRiffle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringSplit(final Object a1) {
    return new AST1(StringSplit, Object2Expr.convert(a1));
  }

  public static IAST stringSplit(final Object a1, final Object a2) {
    return new AST2(StringSplit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringSplit(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringSplit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST stringTake(final Object a1) {
    return new AST1(StringTake, Object2Expr.convert(a1));
  }

  public static IAST stringTake(final Object a1, final Object a2) {
    return new AST2(StringTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST stringToByteArray(final Object a1) {
    return new AST1(StringToByteArray, Object2Expr.convert(a1));
  }

  public static IAST stringToStream(final Object a1) {
    return new AST1(StringToStream, Object2Expr.convert(a1));
  }

  public static IAST stringTrim(final Object a1) {
    return new AST1(StringTrim, Object2Expr.convert(a1));
  }

  public static IAST stringTrim(final Object a1, final Object a2) {
    return new AST2(StringTrim, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST structure(final Object a1) {
    return new AST1(Structure, Object2Expr.convert(a1));
  }

  public static IAST struveH(final Object a1) {
    return new AST1(StruveH, Object2Expr.convert(a1));
  }

  public static IAST struveH(final Object a1, final Object a2) {
    return new AST2(StruveH, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST struveL(final Object a1) {
    return new AST1(StruveL, Object2Expr.convert(a1));
  }

  public static IAST struveL(final Object a1, final Object a2) {
    return new AST2(StruveL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subdivide(final Object a1) {
    return new AST1(Subdivide, Object2Expr.convert(a1));
  }

  public static IAST subdivide(final Object a1, final Object a2) {
    return new AST2(Subdivide, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subdivide(final Object a1, final Object a2, final Object a3) {
    return new AST3(Subdivide, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST subfactorial(final Object a1) {
    return new AST1(Subfactorial, Object2Expr.convert(a1));
  }

  public static IAST subsetQ(final Object a1) {
    return new AST1(SubsetQ, Object2Expr.convert(a1));
  }

  public static IAST subsetQ(final Object a1, final Object a2) {
    return new AST2(SubsetQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subsets(final Object a1) {
    return new AST1(Subsets, Object2Expr.convert(a1));
  }

  public static IAST subsets(final Object a1, final Object a2) {
    return new AST2(Subsets, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subtract(final Object a1) {
    return new AST1(Subtract, Object2Expr.convert(a1));
  }

  public static IAST subtract(final Object a1, final Object a2) {
    return new AST2(Subtract, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subtractFrom(final Object a1) {
    return new AST1(SubtractFrom, Object2Expr.convert(a1));
  }

  public static IAST subtractFrom(final Object a1, final Object a2) {
    return new AST2(SubtractFrom, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST subtractSides(final Object a1) {
    return new AST1(SubtractSides, Object2Expr.convert(a1));
  }

  public static IAST subtractSides(final Object a1, final Object a2) {
    return new AST2(SubtractSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sum(final Object a1) {
    return new AST1(Sum, Object2Expr.convert(a1));
  }

  public static IAST sum(final Object a1, final Object a2) {
    return new AST2(Sum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST sum(final Object a1, final Object a2, final Object a3) {
    return new AST3(Sum, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST summary(final Object a1) {
    return new AST1(Summary, Object2Expr.convert(a1));
  }

  public static IAST surd(final Object a1) {
    return new AST1(Surd, Object2Expr.convert(a1));
  }

  public static IAST surd(final Object a1, final Object a2) {
    return new AST2(Surd, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $switch(final Object a1) {
    return new AST1(Switch, Object2Expr.convert(a1));
  }

  public static IAST $switch(final Object a1, final Object a2) {
    return new AST2(Switch, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $switch(final Object a1, final Object a2, final Object a3) {
    return new AST3(Switch, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST symbolName(final Object a1) {
    return new AST1(SymbolName, Object2Expr.convert(a1));
  }

  public static IAST symbolQ(final Object a1) {
    return new AST1(SymbolQ, Object2Expr.convert(a1));
  }

  public static IAST symmetricMatrixQ(final Object a1) {
    return new AST1(SymmetricMatrixQ, Object2Expr.convert(a1));
  }

  public static IAST symmetricMatrixQ(final Object a1, final Object a2) {
    return new AST2(SymmetricMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST syntaxLength(final Object a1) {
    return new AST1(SyntaxLength, Object2Expr.convert(a1));
  }

  public static IAST syntaxQ(final Object a1) {
    return new AST1(SyntaxQ, Object2Expr.convert(a1));
  }

  public static IAST syntaxQ(final Object a1, final Object a2) {
    return new AST2(SyntaxQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tTest(final Object a1) {
    return new AST1(TTest, Object2Expr.convert(a1));
  }

  public static IAST tTest(final Object a1, final Object a2) {
    return new AST2(TTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST table(final Object a1) {
    return new AST1(Table, Object2Expr.convert(a1));
  }

  public static IAST table(final Object a1, final Object a2) {
    return new AST2(Table, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST table(final Object a1, final Object a2, final Object a3) {
    return new AST3(Table, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST tableForm(final Object a1) {
    return new AST1(TableForm, Object2Expr.convert(a1));
  }

  public static IAST tagSet(final Object a1) {
    return new AST1(TagSet, Object2Expr.convert(a1));
  }

  public static IAST tagSet(final Object a1, final Object a2) {
    return new AST2(TagSet, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tagSet(final Object a1, final Object a2, final Object a3) {
    return new AST3(TagSet, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST tagSetDelayed(final Object a1) {
    return new AST1(TagSetDelayed, Object2Expr.convert(a1));
  }

  public static IAST tagSetDelayed(final Object a1, final Object a2) {
    return new AST2(TagSetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tagSetDelayed(final Object a1, final Object a2, final Object a3) {
    return new AST3(TagSetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST take(final Object a1) {
    return new AST1(Take, Object2Expr.convert(a1));
  }

  public static IAST take(final Object a1, final Object a2) {
    return new AST2(Take, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST take(final Object a1, final Object a2, final Object a3) {
    return new AST3(Take, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST takeLargest(final Object a1) {
    return new AST1(TakeLargest, Object2Expr.convert(a1));
  }

  public static IAST takeLargest(final Object a1, final Object a2) {
    return new AST2(TakeLargest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST takeLargestBy(final Object a1) {
    return new AST1(TakeLargestBy, Object2Expr.convert(a1));
  }

  public static IAST takeLargestBy(final Object a1, final Object a2) {
    return new AST2(TakeLargestBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST takeLargestBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(TakeLargestBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST takeSmallest(final Object a1) {
    return new AST1(TakeSmallest, Object2Expr.convert(a1));
  }

  public static IAST takeSmallest(final Object a1, final Object a2) {
    return new AST2(TakeSmallest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST takeSmallestBy(final Object a1) {
    return new AST1(TakeSmallestBy, Object2Expr.convert(a1));
  }

  public static IAST takeSmallestBy(final Object a1, final Object a2) {
    return new AST2(TakeSmallestBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST takeSmallestBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(TakeSmallestBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST takeWhile(final Object a1) {
    return new AST1(TakeWhile, Object2Expr.convert(a1));
  }

  public static IAST takeWhile(final Object a1, final Object a2) {
    return new AST2(TakeWhile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tally(final Object a1) {
    return new AST1(Tally, Object2Expr.convert(a1));
  }

  public static IAST tally(final Object a1, final Object a2) {
    return new AST2(Tally, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tan(final Object a1) {
    return new AST1(Tan, Object2Expr.convert(a1));
  }

  public static IAST tanh(final Object a1) {
    return new AST1(Tanh, Object2Expr.convert(a1));
  }

  public static IAST tautologyQ(final Object a1) {
    return new AST1(TautologyQ, Object2Expr.convert(a1));
  }

  public static IAST tautologyQ(final Object a1, final Object a2) {
    return new AST2(TautologyQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST teXForm(final Object a1) {
    return new AST1(TeXForm, Object2Expr.convert(a1));
  }

  public static IAST templateApply(final Object a1) {
    return new AST1(TemplateApply, Object2Expr.convert(a1));
  }

  public static IAST templateApply(final Object a1, final Object a2) {
    return new AST2(TemplateApply, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST templateIf(final Object a1) {
    return new AST1(TemplateIf, Object2Expr.convert(a1));
  }

  public static IAST templateIf(final Object a1, final Object a2) {
    return new AST2(TemplateIf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST templateIf(final Object a1, final Object a2, final Object a3) {
    return new AST3(TemplateIf, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST templateSlot(final Object a1) {
    return new AST1(TemplateSlot, Object2Expr.convert(a1));
  }

  public static IAST templateSlot(final Object a1, final Object a2) {
    return new AST2(TemplateSlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST templateSlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(TemplateSlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST tensorDimensions(final Object a1) {
    return new AST1(TensorDimensions, Object2Expr.convert(a1));
  }

  public static IAST tensorRank(final Object a1) {
    return new AST1(TensorRank, Object2Expr.convert(a1));
  }

  public static IAST tensorRank(final Object a1, final Object a2) {
    return new AST2(TensorRank, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tensorSymmetry(final Object a1) {
    return new AST1(TensorSymmetry, Object2Expr.convert(a1));
  }

  public static IAST tensorSymmetry(final Object a1, final Object a2) {
    return new AST2(TensorSymmetry, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST testReport(final Object a1) {
    return new AST1(TestReport, Object2Expr.convert(a1));
  }

  public static IAST tetrahedron(final Object a1) {
    return new AST1(Tetrahedron, Object2Expr.convert(a1));
  }

  public static IAST tetrahedron(final Object a1, final Object a2) {
    return new AST2(Tetrahedron, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tetrahedron(final Object a1, final Object a2, final Object a3) {
    return new AST3(Tetrahedron, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST textString(final Object a1) {
    return new AST1(TextString, Object2Expr.convert(a1));
  }

  public static IAST thread(final Object a1) {
    return new AST1(Thread, Object2Expr.convert(a1));
  }

  public static IAST thread(final Object a1, final Object a2) {
    return new AST2(Thread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST through(final Object a1) {
    return new AST1(Through, Object2Expr.convert(a1));
  }

  public static IAST through(final Object a1, final Object a2) {
    return new AST2(Through, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST $throw(final Object a1) {
    return new AST1(Throw, Object2Expr.convert(a1));
  }

  public static IAST $throw(final Object a1, final Object a2) {
    return new AST2(Throw, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST timeConstrained(final Object a1) {
    return new AST1(TimeConstrained, Object2Expr.convert(a1));
  }

  public static IAST timeConstrained(final Object a1, final Object a2) {
    return new AST2(TimeConstrained, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST timeConstrained(final Object a1, final Object a2, final Object a3) {
    return new AST3(TimeConstrained, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST timeObject(final Object a1) {
    return new AST1(TimeObject, Object2Expr.convert(a1));
  }

  public static IAST timeValue(final Object a1) {
    return new AST1(TimeValue, Object2Expr.convert(a1));
  }

  public static IAST timeValue(final Object a1, final Object a2) {
    return new AST2(TimeValue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST timeValue(final Object a1, final Object a2, final Object a3) {
    return new AST3(TimeValue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST timesBy(final Object a1) {
    return new AST1(TimesBy, Object2Expr.convert(a1));
  }

  public static IAST timesBy(final Object a1, final Object a2) {
    return new AST2(TimesBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST timing(final Object a1) {
    return new AST1(Timing, Object2Expr.convert(a1));
  }

  public static IAST toBoxes(final Object a1) {
    return new AST1(ToBoxes, Object2Expr.convert(a1));
  }

  public static IAST toBoxes(final Object a1, final Object a2) {
    return new AST2(ToBoxes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST toCharacterCode(final Object a1) {
    return new AST1(ToCharacterCode, Object2Expr.convert(a1));
  }

  public static IAST toExpression(final Object a1) {
    return new AST1(ToExpression, Object2Expr.convert(a1));
  }

  public static IAST toExpression(final Object a1, final Object a2) {
    return new AST2(ToExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST toExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(ToExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST toLowerCase(final Object a1) {
    return new AST1(ToLowerCase, Object2Expr.convert(a1));
  }

  public static IAST toPolarCoordinates(final Object a1) {
    return new AST1(ToPolarCoordinates, Object2Expr.convert(a1));
  }

  public static IAST toString(final Object a1) {
    return new AST1(ToString, Object2Expr.convert(a1));
  }

  public static IAST toUnicode(final Object a1) {
    return new AST1(ToUnicode, Object2Expr.convert(a1));
  }

  public static IAST toUpperCase(final Object a1) {
    return new AST1(ToUpperCase, Object2Expr.convert(a1));
  }

  public static IAST toeplitzMatrix(final Object a1) {
    return new AST1(ToeplitzMatrix, Object2Expr.convert(a1));
  }

  public static IAST toeplitzMatrix(final Object a1, final Object a2) {
    return new AST2(ToeplitzMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST together(final Object a1) {
    return new AST1(Together, Object2Expr.convert(a1));
  }

  public static IAST total(final Object a1) {
    return new AST1(Total, Object2Expr.convert(a1));
  }

  public static IAST total(final Object a1, final Object a2) {
    return new AST2(Total, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tr(final Object a1) {
    return new AST1(Tr, Object2Expr.convert(a1));
  }

  public static IAST tr(final Object a1, final Object a2) {
    return new AST2(Tr, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tr(final Object a1, final Object a2, final Object a3) {
    return new AST3(Tr, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }

  public static IAST trace(final Object a1) {
    return new AST1(Trace, Object2Expr.convert(a1));
  }

  public static IAST trace(final Object a1, final Object a2) {
    return new AST2(Trace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST traceForm(final Object a1) {
    return new AST1(TraceForm, Object2Expr.convert(a1));
  }

  public static IAST traceForm(final Object a1, final Object a2) {
    return new AST2(TraceForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST transliterate(final Object a1) {
    return new AST1(Transliterate, Object2Expr.convert(a1));
  }

  public static IAST transliterate(final Object a1, final Object a2) {
    return new AST2(Transliterate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST transpose(final Object a1) {
    return new AST1(Transpose, Object2Expr.convert(a1));
  }

  public static IAST transpose(final Object a1, final Object a2) {
    return new AST2(Transpose, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST treeForm(final Object a1) {
    return new AST1(TreeForm, Object2Expr.convert(a1));
  }

  public static IAST treeForm(final Object a1, final Object a2) {
    return new AST2(TreeForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST trigExpand(final Object a1) {
    return new AST1(TrigExpand, Object2Expr.convert(a1));
  }

  public static IAST trigReduce(final Object a1) {
    return new AST1(TrigReduce, Object2Expr.convert(a1));
  }

  public static IAST trigSimplifyFu(final Object a1) {
    return new AST1(TrigSimplifyFu, Object2Expr.convert(a1));
  }

  public static IAST trigSimplifyFu(final Object a1, final Object a2) {
    return new AST2(TrigSimplifyFu, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST trigToExp(final Object a1) {
    return new AST1(TrigToExp, Object2Expr.convert(a1));
  }

  public static IAST trueQ(final Object a1) {
    return new AST1(TrueQ, Object2Expr.convert(a1));
  }

  public static IAST tube(final Object a1) {
    return new AST1(Tube, Object2Expr.convert(a1));
  }

  public static IAST tube(final Object a1, final Object a2) {
    return new AST2(Tube, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST tukeyWindow(final Object a1) {
    return new AST1(TukeyWindow, Object2Expr.convert(a1));
  }

  public static IAST tuples(final Object a1) {
    return new AST1(Tuples, Object2Expr.convert(a1));
  }

  public static IAST tuples(final Object a1, final Object a2) {
    return new AST2(Tuples, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST uRLFetch(final Object a1) {
    return new AST1(URLFetch, Object2Expr.convert(a1));
  }

  public static IAST unequalTo(final Object a1) {
    return new AST1(UnequalTo, Object2Expr.convert(a1));
  }

  public static IAST unevaluated(final Object a1) {
    return new AST1(Unevaluated, Object2Expr.convert(a1));
  }

  public static IAST uniformDistribution(final Object a1) {
    return new AST1(UniformDistribution, Object2Expr.convert(a1));
  }

  public static IAST unique(final Object a1) {
    return new AST1(Unique, Object2Expr.convert(a1));
  }

  public static IAST unitConvert(final Object a1) {
    return new AST1(UnitConvert, Object2Expr.convert(a1));
  }

  public static IAST unitConvert(final Object a1, final Object a2) {
    return new AST2(UnitConvert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST unitVector(final Object a1) {
    return new AST1(UnitVector, Object2Expr.convert(a1));
  }

  public static IAST unitVector(final Object a1, final Object a2) {
    return new AST2(UnitVector, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST unitize(final Object a1) {
    return new AST1(Unitize, Object2Expr.convert(a1));
  }

  public static IAST unset(final Object a1) {
    return new AST1(Unset, Object2Expr.convert(a1));
  }

  public static IAST upSet(final Object a1) {
    return new AST1(UpSet, Object2Expr.convert(a1));
  }

  public static IAST upSet(final Object a1, final Object a2) {
    return new AST2(UpSet, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST upSetDelayed(final Object a1) {
    return new AST1(UpSetDelayed, Object2Expr.convert(a1));
  }

  public static IAST upSetDelayed(final Object a1, final Object a2) {
    return new AST2(UpSetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST upValues(final Object a1) {
    return new AST1(UpValues, Object2Expr.convert(a1));
  }

  public static IAST upperCaseQ(final Object a1) {
    return new AST1(UpperCaseQ, Object2Expr.convert(a1));
  }

  public static IAST upperTriangularize(final Object a1) {
    return new AST1(UpperTriangularize, Object2Expr.convert(a1));
  }

  public static IAST upperTriangularize(final Object a1, final Object a2) {
    return new AST2(UpperTriangularize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST valueQ(final Object a1) {
    return new AST1(ValueQ, Object2Expr.convert(a1));
  }

  public static IAST values(final Object a1) {
    return new AST1(Values, Object2Expr.convert(a1));
  }

  public static IAST values(final Object a1, final Object a2) {
    return new AST2(Values, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST vandermondeMatrix(final Object a1) {
    return new AST1(VandermondeMatrix, Object2Expr.convert(a1));
  }

  public static IAST variables(final Object a1) {
    return new AST1(Variables, Object2Expr.convert(a1));
  }

  public static IAST variance(final Object a1) {
    return new AST1(Variance, Object2Expr.convert(a1));
  }

  public static IAST vectorAngle(final Object a1) {
    return new AST1(VectorAngle, Object2Expr.convert(a1));
  }

  public static IAST vectorAngle(final Object a1, final Object a2) {
    return new AST2(VectorAngle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST vectorQ(final Object a1) {
    return new AST1(VectorQ, Object2Expr.convert(a1));
  }

  public static IAST vectorQ(final Object a1, final Object a2) {
    return new AST2(VectorQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST verificationTest(final Object a1) {
    return new AST1(VerificationTest, Object2Expr.convert(a1));
  }

  public static IAST verificationTest(final Object a1, final Object a2) {
    return new AST2(VerificationTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST verificationTest(final Object a1, final Object a2, final Object a3) {
    return new AST3(VerificationTest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST vertexEccentricity(final Object a1) {
    return new AST1(VertexEccentricity, Object2Expr.convert(a1));
  }

  public static IAST vertexEccentricity(final Object a1, final Object a2) {
    return new AST2(VertexEccentricity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST vertexList(final Object a1) {
    return new AST1(VertexList, Object2Expr.convert(a1));
  }

  public static IAST vertexQ(final Object a1) {
    return new AST1(VertexQ, Object2Expr.convert(a1));
  }

  public static IAST vertexQ(final Object a1, final Object a2) {
    return new AST2(VertexQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST volume(final Object a1) {
    return new AST1(Volume, Object2Expr.convert(a1));
  }

  public static IAST weberE(final Object a1) {
    return new AST1(WeberE, Object2Expr.convert(a1));
  }

  public static IAST weberE(final Object a1, final Object a2) {
    return new AST2(WeberE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST weberE(final Object a1, final Object a2, final Object a3) {
    return new AST3(WeberE, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST weibullDistribution(final Object a1) {
    return new AST1(WeibullDistribution, Object2Expr.convert(a1));
  }

  public static IAST weibullDistribution(final Object a1, final Object a2) {
    return new AST2(WeibullDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST weibullDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(WeibullDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST weierstrassHalfPeriods(final Object a1) {
    return new AST1(WeierstrassHalfPeriods, Object2Expr.convert(a1));
  }

  public static IAST weierstrassInvariants(final Object a1) {
    return new AST1(WeierstrassInvariants, Object2Expr.convert(a1));
  }

  public static IAST weierstrassP(final Object a1) {
    return new AST1(WeierstrassP, Object2Expr.convert(a1));
  }

  public static IAST weierstrassP(final Object a1, final Object a2) {
    return new AST2(WeierstrassP, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST weierstrassPPrime(final Object a1) {
    return new AST1(WeierstrassPPrime, Object2Expr.convert(a1));
  }

  public static IAST weierstrassPPrime(final Object a1, final Object a2) {
    return new AST2(WeierstrassPPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST weightedAdjacencyMatrix(final Object a1) {
    return new AST1(WeightedAdjacencyMatrix, Object2Expr.convert(a1));
  }

  public static IAST weightedGraphQ(final Object a1) {
    return new AST1(WeightedGraphQ, Object2Expr.convert(a1));
  }

  public static IAST wheelGraph(final Object a1) {
    return new AST1(WheelGraph, Object2Expr.convert(a1));
  }

  public static IAST $while(final Object a1) {
    return new AST1(While, Object2Expr.convert(a1));
  }

  public static IAST $while(final Object a1, final Object a2) {
    return new AST2(While, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST whittakerM(final Object a1) {
    return new AST1(WhittakerM, Object2Expr.convert(a1));
  }

  public static IAST whittakerM(final Object a1, final Object a2) {
    return new AST2(WhittakerM, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST whittakerM(final Object a1, final Object a2, final Object a3) {
    return new AST3(WhittakerM, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST whittakerW(final Object a1) {
    return new AST1(WhittakerW, Object2Expr.convert(a1));
  }

  public static IAST whittakerW(final Object a1, final Object a2) {
    return new AST2(WhittakerW, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST whittakerW(final Object a1, final Object a2, final Object a3) {
    return new AST3(WhittakerW, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST with(final Object a1) {
    return new AST1(With, Object2Expr.convert(a1));
  }

  public static IAST with(final Object a1, final Object a2) {
    return new AST2(With, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST with(final Object a1, final Object a2, final Object a3) {
    return new AST3(With, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST write(final Object a1) {
    return new AST1(Write, Object2Expr.convert(a1));
  }

  public static IAST write(final Object a1, final Object a2) {
    return new AST2(Write, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST writeString(final Object a1) {
    return new AST1(WriteString, Object2Expr.convert(a1));
  }

  public static IAST writeString(final Object a1, final Object a2) {
    return new AST2(WriteString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST yuleDissimilarity(final Object a1) {
    return new AST1(YuleDissimilarity, Object2Expr.convert(a1));
  }

  public static IAST yuleDissimilarity(final Object a1, final Object a2) {
    return new AST2(YuleDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST zTransform(final Object a1) {
    return new AST1(ZTransform, Object2Expr.convert(a1));
  }

  public static IAST zTransform(final Object a1, final Object a2) {
    return new AST2(ZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

  public static IAST zTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(ZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

  public static IAST zeta(final Object a1) {
    return new AST1(Zeta, Object2Expr.convert(a1));
  }

  public static IAST zeta(final Object a1, final Object a2) {
    return new AST2(Zeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }

}
