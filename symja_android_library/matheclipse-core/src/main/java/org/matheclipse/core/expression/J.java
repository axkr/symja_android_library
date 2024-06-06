package org.matheclipse.core.expression;

import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.interfaces.IAST;

/**
 * Automatically generated with class
 * <code>org.matheclipse.core.preprocessor.JavaObjectFunctionGenerator</code>. Don't change
 * manually. The methods in this class use <code>Object2Expr.convert(oject)</code> to convert a Java
 * object into a Symja object and may be slower than the methods used in the {@link F} class.
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


  /**
   * AASTriangle(alpha, beta, a) - returns a triangle from 2 angles `alpha`, `beta` and side `a`
   * (which is not between the angles).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AASTriangle.md">AASTriangle
   *      documentation</a>
   */
  public static IAST aASTriangle(final Object a1, final Object a2, final Object a3) {
    return new AST3(AASTriangle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Abs(expr) - returns the absolute value of the real or complex number `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abs.md">Abs
   *      documentation</a>
   */
  public static IAST abs(final Object a1) {
    return new AST1(Abs, Object2Expr.convert(a1));
  }


  public static IAST absoluteCorrelation(final Object a1, final Object a2) {
    return new AST2(AbsoluteCorrelation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AbsoluteTiming(x) - returns a list with the first entry containing the evaluation time of `x`
   * and the second entry is the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsoluteTiming.md">AbsoluteTiming
   *      documentation</a>
   */
  public static IAST absoluteTiming(final Object a1) {
    return new AST1(AbsoluteTiming, Object2Expr.convert(a1));
  }


  /**
   * Accumulate(list) - accumulate the values of `list` returning a new list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Accumulate.md">Accumulate
   *      documentation</a>
   */
  public static IAST accumulate(final Object a1) {
    return new AST1(Accumulate, Object2Expr.convert(a1));
  }


  /**
   * AddSides(compare-expr, value) - add `value` to all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or an comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddSides.md">AddSides
   *      documentation</a>
   */
  public static IAST addSides(final Object a1, final Object a2) {
    return new AST2(AddSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AddTo(x, dx) - is equivalent to `x = x + dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddTo.md">AddTo
   *      documentation</a>
   */
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


  /**
   * AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix in sparse array format.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyMatrix.md">AdjacencyMatrix
   *      documentation</a>
   */
  public static IAST adjacencyMatrix(final Object a1) {
    return new AST1(AdjacencyMatrix, Object2Expr.convert(a1));
  }


  /**
   * Adjugate(matrix) - calculate the adjugate matrix `Inverse(matrix)*Det(matrix)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Adjugate.md">Adjugate
   *      documentation</a>
   */
  public static IAST adjugate(final Object a1) {
    return new AST1(Adjugate, Object2Expr.convert(a1));
  }


  /**
   * Adjugate(matrix) - calculate the adjugate matrix `Inverse(matrix)*Det(matrix)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Adjugate.md">Adjugate
   *      documentation</a>
   */
  public static IAST adjugate(final Object a1, final Object a2) {
    return new AST2(Adjugate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AiryAi(z) - returns the Airy function of the first kind of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAi.md">AiryAi
   *      documentation</a>
   */
  public static IAST airyAi(final Object a1) {
    return new AST1(AiryAi, Object2Expr.convert(a1));
  }


  /**
   * AiryAiPrime(z) - returns the derivative of the `AiryAi` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAiPrime.md">AiryAiPrime
   *      documentation</a>
   */
  public static IAST airyAiPrime(final Object a1) {
    return new AST1(AiryAiPrime, Object2Expr.convert(a1));
  }


  /**
   * AiryBi(z) - returns the Airy function of the second kind of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBi.md">AiryBi
   *      documentation</a>
   */
  public static IAST airyBi(final Object a1) {
    return new AST1(AiryBi, Object2Expr.convert(a1));
  }


  /**
   * AiryBiPrime(z) - returns the derivative of the `AiryBi` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBiPrime.md">AiryBiPrime
   *      documentation</a>
   */
  public static IAST airyBiPrime(final Object a1) {
    return new AST1(AiryBiPrime, Object2Expr.convert(a1));
  }


  /**
   * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1,
   * expr2, ...` evaluate to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllTrue.md">AllTrue
   *      documentation</a>
   */
  public static IAST allTrue(final Object a1, final Object a2) {
    return new AST2(AllTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Alphabet() - gives the list of lowercase letters `a-z` in the English or Latin alphabet .
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alphabet.md">Alphabet
   *      documentation</a>
   */
  public static IAST alphabet(final Object a1) {
    return new AST1(Alphabet, Object2Expr.convert(a1));
  }


  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *      documentation</a>
   */
  public static IAST angleVector(final Object a1) {
    return new AST1(AngleVector, Object2Expr.convert(a1));
  }


  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *      documentation</a>
   */
  public static IAST angleVector(final Object a1, final Object a2) {
    return new AST2(AngleVector, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Annuity(p, t) - returns an annuity object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity
   *      documentation</a>
   */
  public static IAST annuity(final Object a1, final Object a2) {
    return new AST2(Annuity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Annuity(p, t) - returns an annuity object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity
   *      documentation</a>
   */
  public static IAST annuity(final Object a1, final Object a2, final Object a3) {
    return new AST3(Annuity, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * AnnuityDue(p, t) - returns an annuity due object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue
   *      documentation</a>
   */
  public static IAST annuityDue(final Object a1, final Object a2) {
    return new AST2(AnnuityDue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AnnuityDue(p, t) - returns an annuity due object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue
   *      documentation</a>
   */
  public static IAST annuityDue(final Object a1, final Object a2, final Object a3) {
    return new AST3(AnnuityDue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *      documentation</a>
   */
  public static IAST antihermitianMatrixQ(final Object a1) {
    return new AST1(AntihermitianMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *      documentation</a>
   */
  public static IAST antihermitianMatrixQ(final Object a1, final Object a2) {
    return new AST2(AntihermitianMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ
   *      documentation</a>
   */
  public static IAST antisymmetricMatrixQ(final Object a1) {
    return new AST1(AntisymmetricMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ
   *      documentation</a>
   */
  public static IAST antisymmetricMatrixQ(final Object a1, final Object a2) {
    return new AST2(AntisymmetricMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnyTrue.md">AnyTrue
   *      documentation</a>
   */
  public static IAST anyTrue(final Object a1, final Object a2) {
    return new AST2(AnyTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart
   *      documentation</a>
   */
  public static IAST apart(final Object a1) {
    return new AST1(Apart, Object2Expr.convert(a1));
  }


  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart
   *      documentation</a>
   */
  public static IAST apart(final Object a1, final Object a2) {
    return new AST2(Apart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Append(expr, item) - returns `expr` with `item` appended to its leaves.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Append.md">Append
   *      documentation</a>
   */
  public static IAST append(final Object a1, final Object a2) {
    return new AST2(Append, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppendTo.md">AppendTo
   *      documentation</a>
   */
  public static IAST appendTo(final Object a1, final Object a2) {
    return new AST2(AppendTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * f @ expr - returns `f(expr)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *      documentation</a>
   */
  public static IAST apply(final Object a1) {
    return new AST1(Apply, Object2Expr.convert(a1));
  }


  /**
   * f @ expr - returns `f(expr)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *      documentation</a>
   */
  public static IAST apply(final Object a1, final Object a2) {
    return new AST2(Apply, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * f @ expr - returns `f(expr)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *      documentation</a>
   */
  public static IAST apply(final Object a1, final Object a2, final Object a3) {
    return new AST3(Apply, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ApplySides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ApplySides.md">ApplySides
   *      documentation</a>
   */
  public static IAST applySides(final Object a1, final Object a2) {
    return new AST2(ApplySides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCos.md">ArcCos
   *      documentation</a>
   */
  public static IAST arcCos(final Object a1) {
    return new AST1(ArcCos, Object2Expr.convert(a1));
  }


  /**
   * ArcCosh(z) - returns the inverse hyperbolic cosine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCosh.md">ArcCosh
   *      documentation</a>
   */
  public static IAST arcCosh(final Object a1) {
    return new AST1(ArcCosh, Object2Expr.convert(a1));
  }


  /**
   * ArcCot(z) - returns the inverse cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCot.md">ArcCot
   *      documentation</a>
   */
  public static IAST arcCot(final Object a1) {
    return new AST1(ArcCot, Object2Expr.convert(a1));
  }


  /**
   * ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCoth.md">ArcCoth
   *      documentation</a>
   */
  public static IAST arcCoth(final Object a1) {
    return new AST1(ArcCoth, Object2Expr.convert(a1));
  }


  /**
   * ArcCsc(z) - returns the inverse cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsc.md">ArcCsc
   *      documentation</a>
   */
  public static IAST arcCsc(final Object a1) {
    return new AST1(ArcCsc, Object2Expr.convert(a1));
  }


  /**
   * ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsch.md">ArcCsch
   *      documentation</a>
   */
  public static IAST arcCsch(final Object a1) {
    return new AST1(ArcCsch, Object2Expr.convert(a1));
  }


  /**
   * ArcLength(geometric-form) - returns the length of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcLength.md">ArcLength
   *      documentation</a>
   */
  public static IAST arcLength(final Object a1) {
    return new AST1(ArcLength, Object2Expr.convert(a1));
  }


  /**
   * ArcSec(z) - returns the inverse secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSec.md">ArcSec
   *      documentation</a>
   */
  public static IAST arcSec(final Object a1) {
    return new AST1(ArcSec, Object2Expr.convert(a1));
  }


  /**
   * ArcSech(z) - returns the inverse hyperbolic secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSech.md">ArcSech
   *      documentation</a>
   */
  public static IAST arcSech(final Object a1) {
    return new AST1(ArcSech, Object2Expr.convert(a1));
  }


  /**
   * ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSin.md">ArcSin
   *      documentation</a>
   */
  public static IAST arcSin(final Object a1) {
    return new AST1(ArcSin, Object2Expr.convert(a1));
  }


  /**
   * ArcSinh(z) - returns the inverse hyperbolic sine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSinh.md">ArcSinh
   *      documentation</a>
   */
  public static IAST arcSinh(final Object a1) {
    return new AST1(ArcSinh, Object2Expr.convert(a1));
  }


  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan
   *      documentation</a>
   */
  public static IAST arcTan(final Object a1) {
    return new AST1(ArcTan, Object2Expr.convert(a1));
  }


  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan
   *      documentation</a>
   */
  public static IAST arcTan(final Object a1, final Object a2) {
    return new AST2(ArcTan, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArcTanh(z) - returns the inverse hyperbolic tangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTanh.md">ArcTanh
   *      documentation</a>
   */
  public static IAST arcTanh(final Object a1) {
    return new AST1(ArcTanh, Object2Expr.convert(a1));
  }


  /**
   * Area(geometric-form) - returns the area of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Area.md">Area
   *      documentation</a>
   */
  public static IAST area(final Object a1) {
    return new AST1(Area, Object2Expr.convert(a1));
  }


  /**
   * Arg(expr) - returns the argument of the complex number `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arg.md">Arg
   *      documentation</a>
   */
  public static IAST arg(final Object a1) {
    return new AST1(Arg, Object2Expr.convert(a1));
  }


  /**
   * ArgMax(function, variable) - returns a maximizer point for a univariate `function`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMax.md">ArgMax
   *      documentation</a>
   */
  public static IAST argMax(final Object a1, final Object a2) {
    return new AST2(ArgMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArgMin(function, variable) - returns a minimizer point for a univariate `function`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMin.md">ArgMin
   *      documentation</a>
   */
  public static IAST argMin(final Object a1, final Object a2) {
    return new AST2(ArgMin, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b,
   * c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArithmeticGeometricMean.md">ArithmeticGeometricMean
   *      documentation</a>
   */
  public static IAST arithmeticGeometricMean(final Object a1, final Object a2) {
    return new AST2(ArithmeticGeometricMean, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array
   *      documentation</a>
   */
  public static IAST array(final Object a1, final Object a2) {
    return new AST2(Array, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array
   *      documentation</a>
   */
  public static IAST array(final Object a1, final Object a2, final Object a3) {
    return new AST3(Array, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as
   * `Length(Dimensions(a))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayDepth.md">ArrayDepth
   *      documentation</a>
   */
  public static IAST arrayDepth(final Object a1) {
    return new AST1(ArrayDepth, Object2Expr.convert(a1));
  }


  public static IAST arrayFlatten(final Object a1) {
    return new AST1(ArrayFlatten, Object2Expr.convert(a1));
  }


  public static IAST arrayFlatten(final Object a1, final Object a2) {
    return new AST2(ArrayFlatten, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad
   *      documentation</a>
   */
  public static IAST arrayPad(final Object a1, final Object a2) {
    return new AST2(ArrayPad, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad
   *      documentation</a>
   */
  public static IAST arrayPad(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayPad, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ArrayPlot( matrix-of-values ) - generate a rectangle image for the `matrix-of-values`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPlot.md">ArrayPlot
   *      documentation</a>
   */
  public static IAST arrayPlot(final Object a1) {
    return new AST1(ArrayPlot, Object2Expr.convert(a1));
  }


  /**
   * ArrayPlot( matrix-of-values ) - generate a rectangle image for the `matrix-of-values`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPlot.md">ArrayPlot
   *      documentation</a>
   */
  public static IAST arrayPlot(final Object a1, final Object a2) {
    return new AST2(ArrayPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArrayPlot( matrix-of-values ) - generate a rectangle image for the `matrix-of-values`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPlot.md">ArrayPlot
   *      documentation</a>
   */
  public static IAST arrayPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *      documentation</a>
   */
  public static IAST arrayQ(final Object a1) {
    return new AST1(ArrayQ, Object2Expr.convert(a1));
  }


  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *      documentation</a>
   */
  public static IAST arrayQ(final Object a1, final Object a2) {
    return new AST2(ArrayQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *      documentation</a>
   */
  public static IAST arrayQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape
   *      documentation</a>
   */
  public static IAST arrayReshape(final Object a1, final Object a2) {
    return new AST2(ArrayReshape, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape
   *      documentation</a>
   */
  public static IAST arrayReshape(final Object a1, final Object a2, final Object a3) {
    return new AST3(ArrayReshape, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules
   *      documentation</a>
   */
  public static IAST arrayRules(final Object a1) {
    return new AST1(ArrayRules, Object2Expr.convert(a1));
  }


  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules
   *      documentation</a>
   */
  public static IAST arrayRules(final Object a1, final Object a2) {
    return new AST2(ArrayRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *      documentation</a>
   */
  public static IAST arrow(final Object a1) {
    return new AST1(Arrow, Object2Expr.convert(a1));
  }


  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *      documentation</a>
   */
  public static IAST arrow(final Object a1, final Object a2) {
    return new AST2(Arrow, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *      documentation</a>
   */
  public static IAST arrow(final Object a1, final Object a2, final Object a3) {
    return new AST3(Arrow, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ASATriangle(alpha, c, beta) - returns a triangle from 2 angles `alpha`, `beta` and side `c`
   * (which is between the angles).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ASATriangle.md">ASATriangle
   *      documentation</a>
   */
  public static IAST aSATriangle(final Object a1, final Object a2, final Object a3) {
    return new AST3(ASATriangle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * AssociateTo(assoc, rule) - append `rule` to the association `assoc` and assign the result to
   * `assoc`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociateTo.md">AssociateTo
   *      documentation</a>
   */
  public static IAST associateTo(final Object a1, final Object a2) {
    return new AST2(AssociateTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap
   *      documentation</a>
   */
  public static IAST associationMap(final Object a1) {
    return new AST1(AssociationMap, Object2Expr.convert(a1));
  }


  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap
   *      documentation</a>
   */
  public static IAST associationMap(final Object a1, final Object a2) {
    return new AST2(AssociationMap, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationQ.md">AssociationQ
   *      documentation</a>
   */
  public static IAST associationQ(final Object a1) {
    return new AST1(AssociationQ, Object2Expr.convert(a1));
  }


  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread
   *      documentation</a>
   */
  public static IAST associationThread(final Object a1) {
    return new AST1(AssociationThread, Object2Expr.convert(a1));
  }


  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread
   *      documentation</a>
   */
  public static IAST associationThread(final Object a1, final Object a2) {
    return new AST2(AssociationThread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Assuming(assumption, expression) - evaluate the `expression` with the assumptions appended to
   * the default `$Assumptions` assumptions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Assuming.md">Assuming
   *      documentation</a>
   */
  public static IAST assuming(final Object a1, final Object a2) {
    return new AST2(Assuming, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part').
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomQ.md">AtomQ
   *      documentation</a>
   */
  public static IAST atomQ(final Object a1) {
    return new AST1(AtomQ, Object2Expr.convert(a1));
  }


  /**
   * Attributes(symbol) - returns the list of attributes which are assigned to `symbol`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Attributes.md">Attributes
   *      documentation</a>
   */
  public static IAST attributes(final Object a1) {
    return new AST1(Attributes, Object2Expr.convert(a1));
  }


  public static IAST bartlettWindow(final Object a1) {
    return new AST1(BartlettWindow, Object2Expr.convert(a1));
  }


  /**
   * BaseDecode(string) - decodes a Base64 encoded `string` into a `ByteArray` using the Base64
   * encoding scheme.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseDecode.md">BaseDecode
   *      documentation</a>
   */
  public static IAST baseDecode(final Object a1) {
    return new AST1(BaseDecode, Object2Expr.convert(a1));
  }


  /**
   * BaseEncode(byte-array) - encodes the specified `byte-array` into a string using the Base64
   * encoding scheme.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseEncode.md">BaseEncode
   *      documentation</a>
   */
  public static IAST baseEncode(final Object a1) {
    return new AST1(BaseEncode, Object2Expr.convert(a1));
  }


  /**
   * BaseForm(integer, radix) - prints the `integer` number in base `radix` form.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseForm.md">BaseForm
   *      documentation</a>
   */
  public static IAST baseForm(final Object a1, final Object a2) {
    return new AST2(BaseForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Begin("<context-name>") - start a new context definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Begin.md">Begin
   *      documentation</a>
   */
  public static IAST begin(final Object a1) {
    return new AST1(Begin, Object2Expr.convert(a1));
  }


  /**
   * BeginPackage("<context-name>") - start a new package definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage
   *      documentation</a>
   */
  public static IAST beginPackage(final Object a1) {
    return new AST1(BeginPackage, Object2Expr.convert(a1));
  }


  /**
   * BeginPackage("<context-name>") - start a new package definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage
   *      documentation</a>
   */
  public static IAST beginPackage(final Object a1, final Object a2) {
    return new AST2(BeginPackage, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB
   *      documentation</a>
   */
  public static IAST bellB(final Object a1) {
    return new AST1(BellB, Object2Expr.convert(a1));
  }


  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB
   *      documentation</a>
   */
  public static IAST bellB(final Object a1, final Object a2) {
    return new AST2(BellB, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *      documentation</a>
   */
  public static IAST bellY(final Object a1) {
    return new AST1(BellY, Object2Expr.convert(a1));
  }


  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *      documentation</a>
   */
  public static IAST bellY(final Object a1, final Object a2) {
    return new AST2(BellY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *      documentation</a>
   */
  public static IAST bellY(final Object a1, final Object a2, final Object a3) {
    return new AST3(BellY, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB
   *      documentation</a>
   */
  public static IAST bernoulliB(final Object a1) {
    return new AST1(BernoulliB, Object2Expr.convert(a1));
  }


  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB
   *      documentation</a>
   */
  public static IAST bernoulliB(final Object a1, final Object a2) {
    return new AST2(BernoulliB, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BernoulliDistribution(p) - returns the Bernoulli distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliDistribution.md">BernoulliDistribution
   *      documentation</a>
   */
  public static IAST bernoulliDistribution(final Object a1) {
    return new AST1(BernoulliDistribution, Object2Expr.convert(a1));
  }


  /**
   * BernsteinBasis(n, v, expr) - computes the Bernstein basis for the expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernsteinBasis.md">BernsteinBasis
   *      documentation</a>
   */
  public static IAST bernsteinBasis(final Object a1, final Object a2, final Object a3) {
    return new AST3(BernsteinBasis, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * BesselI(n, z) - modified Bessel function of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselI.md">BesselI
   *      documentation</a>
   */
  public static IAST besselI(final Object a1, final Object a2) {
    return new AST2(BesselI, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BesselJ(n, z) - Bessel function of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJ.md">BesselJ
   *      documentation</a>
   */
  public static IAST besselJ(final Object a1, final Object a2) {
    return new AST2(BesselJ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJZero.md">BesselJZero
   *      documentation</a>
   */
  public static IAST besselJZero(final Object a1, final Object a2) {
    return new AST2(BesselJZero, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BesselK(n, z) - modified Bessel function of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselK.md">BesselK
   *      documentation</a>
   */
  public static IAST besselK(final Object a1, final Object a2) {
    return new AST2(BesselK, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BesselY(n, z) - Bessel function of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselY.md">BesselY
   *      documentation</a>
   */
  public static IAST besselY(final Object a1, final Object a2) {
    return new AST2(BesselY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselYZero.md">BesselYZero
   *      documentation</a>
   */
  public static IAST besselYZero(final Object a1, final Object a2) {
    return new AST2(BesselYZero, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta
   *      documentation</a>
   */
  public static IAST beta(final Object a1, final Object a2) {
    return new AST2(Beta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta
   *      documentation</a>
   */
  public static IAST beta(final Object a1, final Object a2, final Object a3) {
    return new AST3(Beta, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are
   * unequal. `1` if `u` and `v` are equal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDistance.md">BinaryDistance
   *      documentation</a>
   */
  public static IAST binaryDistance(final Object a1, final Object a2) {
    return new AST2(BinaryDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST binaryRead(final Object a1) {
    return new AST1(BinaryRead, Object2Expr.convert(a1));
  }


  public static IAST binaryRead(final Object a1, final Object a2) {
    return new AST2(BinaryRead, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST binaryWrite(final Object a1, final Object a2) {
    return new AST2(BinaryWrite, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST binaryWrite(final Object a1, final Object a2, final Object a3) {
    return new AST3(BinaryWrite, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *      documentation</a>
   */
  public static IAST binCounts(final Object a1) {
    return new AST1(BinCounts, Object2Expr.convert(a1));
  }


  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *      documentation</a>
   */
  public static IAST binCounts(final Object a1, final Object a2) {
    return new AST2(BinCounts, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binomial.md">Binomial
   *      documentation</a>
   */
  public static IAST binomial(final Object a1, final Object a2) {
    return new AST2(Binomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BinomialDistribution(n, p) - returns the binomial distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinomialDistribution.md">BinomialDistribution
   *      documentation</a>
   */
  public static IAST binomialDistribution(final Object a1, final Object a2) {
    return new AST2(BinomialDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST bioSequence(final Object a1, final Object a2) {
    return new AST2(BioSequence, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST bioSequenceQ(final Object a1) {
    return new AST1(BioSequenceQ, Object2Expr.convert(a1));
  }


  /**
   * BitLength(x) - gives the number of bits needed to represent the integer `x`. The sign of `x`
   * is ignored.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength
   *      documentation</a>
   */
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


  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block
   *      documentation</a>
   */
  public static IAST block(final Object a1, final Object a2) {
    return new AST2(Block, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to
   * `False`; and gives no result otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Boole.md">Boole
   *      documentation</a>
   */
  public static IAST boole(final Object a1) {
    return new AST1(Boole, Object2Expr.convert(a1));
  }


  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert
   *      documentation</a>
   */
  public static IAST booleanConvert(final Object a1) {
    return new AST1(BooleanConvert, Object2Expr.convert(a1));
  }


  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert
   *      documentation</a>
   */
  public static IAST booleanConvert(final Object a1, final Object a2) {
    return new AST2(BooleanConvert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BooleaMaxterms({{b1,b2,...}}, {v1,v2,...}) - create the conjunction of the variables
   * `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMaxterms.md">BooleanMaxterms
   *      documentation</a>
   */
  public static IAST booleanMaxterms(final Object a1, final Object a2) {
    return new AST2(BooleanMaxterms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize
   *      documentation</a>
   */
  public static IAST booleanMinimize(final Object a1) {
    return new AST1(BooleanMinimize, Object2Expr.convert(a1));
  }


  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize
   *      documentation</a>
   */
  public static IAST booleanMinimize(final Object a1, final Object a2) {
    return new AST2(BooleanMinimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BooleanMinterms({{b1,b2,...}}, {v1,v2,...}) - create the disjunction of the variables
   * `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinterms.md">BooleanMinterms
   *      documentation</a>
   */
  public static IAST booleanMinterms(final Object a1, final Object a2) {
    return new AST2(BooleanMinterms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanQ.md">BooleanQ
   *      documentation</a>
   */
  public static IAST booleanQ(final Object a1) {
    return new AST1(BooleanQ, Object2Expr.convert(a1));
  }


  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable
   *      documentation</a>
   */
  public static IAST booleanTable(final Object a1) {
    return new AST1(BooleanTable, Object2Expr.convert(a1));
  }


  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable
   *      documentation</a>
   */
  public static IAST booleanTable(final Object a1, final Object a2) {
    return new AST2(BooleanTable, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the
   * `logical-expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanVariables.md">BooleanVariables
   *      documentation</a>
   */
  public static IAST booleanVariables(final Object a1) {
    return new AST1(BooleanVariables, Object2Expr.convert(a1));
  }


  /**
   * BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BrayCurtisDistance.md">BrayCurtisDistance
   *      documentation</a>
   */
  public static IAST brayCurtisDistance(final Object a1, final Object a2) {
    return new AST2(BrayCurtisDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }



  /**
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayQ.md">ByteArrayQ
   *      documentation</a>
   */
  public static IAST byteArrayQ(final Object a1) {
    return new AST1(ByteArrayQ, Object2Expr.convert(a1));
  }


  /**
   * ByteArrayToString(byte-array) - decoding the specified `byte-array` using the default character
   * set `UTF-8`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayToString.md">ByteArrayToString
   *      documentation</a>
   */
  public static IAST byteArrayToString(final Object a1) {
    return new AST1(ByteArrayToString, Object2Expr.convert(a1));
  }


  public static IAST byteCount(final Object a1) {
    return new AST1(ByteCount, Object2Expr.convert(a1));
  }


  /**
   * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted
   * version of the Manhattan distance.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CanberraDistance.md">CanberraDistance
   *      documentation</a>
   */
  public static IAST canberraDistance(final Object a1, final Object a2) {
    return new AST2(CanberraDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cancel(expr) - cancels out common factors in numerators and denominators.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cancel.md">Cancel
   *      documentation</a>
   */
  public static IAST cancel(final Object a1) {
    return new AST1(Cancel, Object2Expr.convert(a1));
  }


  /**
   * CarlsonRC(x, y) - returns the Carlson RC function..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRC.md">CarlsonRC
   *      documentation</a>
   */
  public static IAST carlsonRC(final Object a1, final Object a2) {
    return new AST2(CarlsonRC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CarlsonRD(x, y, z) - returns the Carlson RD function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRD.md">CarlsonRD
   *      documentation</a>
   */
  public static IAST carlsonRD(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CarlsonRF(x, y, z) - returns the Carlson RF function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRF.md">CarlsonRF
   *      documentation</a>
   */
  public static IAST carlsonRF(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRF, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CarlsonRG(x, y, z) - returns the Carlson RG function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRG.md">CarlsonRG
   *      documentation</a>
   */
  public static IAST carlsonRG(final Object a1, final Object a2, final Object a3) {
    return new AST3(CarlsonRG, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CarmichaelLambda(n) - the Carmichael function of `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarmichaelLambda.md">CarmichaelLambda
   *      documentation</a>
   */
  public static IAST carmichaelLambda(final Object a1) {
    return new AST1(CarmichaelLambda, Object2Expr.convert(a1));
  }


  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct
   *      documentation</a>
   */
  public static IAST cartesianProduct(final Object a1, final Object a2) {
    return new AST2(CartesianProduct, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct
   *      documentation</a>
   */
  public static IAST cartesianProduct(final Object a1, final Object a2, final Object a3) {
    return new AST3(CartesianProduct, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *      documentation</a>
   */
  public static IAST cases(final Object a1) {
    return new AST1(Cases, Object2Expr.convert(a1));
  }


  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *      documentation</a>
   */
  public static IAST cases(final Object a1, final Object a2) {
    return new AST2(Cases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *      documentation</a>
   */
  public static IAST cases(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CatalanNumber(n) - returns the catalan number for the argument `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CatalanNumber.md">CatalanNumber
   *      documentation</a>
   */
  public static IAST catalanNumber(final Object a1) {
    return new AST1(CatalanNumber, Object2Expr.convert(a1));
  }


  /**
   * Catch(expr) - returns the value argument of the first `Throw(value)` generated in the
   * evaluation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catch.md">Catch
   *      documentation</a>
   */
  public static IAST $catch(final Object a1) {
    return new AST1(Catch, Object2Expr.convert(a1));
  }


  /**
   * Catch(expr) - returns the value argument of the first `Throw(value)` generated in the
   * evaluation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catch.md">Catch
   *      documentation</a>
   */
  public static IAST $catch(final Object a1, final Object a2) {
    return new AST2(Catch, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Catch(expr) - returns the value argument of the first `Throw(value)` generated in the
   * evaluation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catch.md">Catch
   *      documentation</a>
   */
  public static IAST $catch(final Object a1, final Object a2, final Object a3) {
    return new AST3(Catch, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catenate.md">Catenate
   *      documentation</a>
   */
  public static IAST catenate(final Object a1) {
    return new AST1(Catenate, Object2Expr.convert(a1));
  }


  /**
   * CauchyDistribution(a,b) - returns the Cauchy distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CauchyDistribution.md">CauchyDistribution
   *      documentation</a>
   */
  public static IAST cauchyDistribution(final Object a1) {
    return new AST1(CauchyDistribution, Object2Expr.convert(a1));
  }


  /**
   * CauchyDistribution(a,b) - returns the Cauchy distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CauchyDistribution.md">CauchyDistribution
   *      documentation</a>
   */
  public static IAST cauchyDistribution(final Object a1, final Object a2) {
    return new AST2(CauchyDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF
   *      documentation</a>
   */
  public static IAST cdf(final Object a1) {
    return new AST1(CDF, Object2Expr.convert(a1));
  }


  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF
   *      documentation</a>
   */
  public static IAST cdf(final Object a1, final Object a2) {
    return new AST2(CDF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling
   *      documentation</a>
   */
  public static IAST ceiling(final Object a1) {
    return new AST1(Ceiling, Object2Expr.convert(a1));
  }


  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling
   *      documentation</a>
   */
  public static IAST ceiling(final Object a1, final Object a2) {
    return new AST2(Ceiling, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CentralMoment(list, r) - gives the the `r`-th central moment (i.e. the `r`th moment about the
   * mean) of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMoment.md">CentralMoment
   *      documentation</a>
   */
  public static IAST centralMoment(final Object a1, final Object a2) {
    return new AST2(CentralMoment, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST cForm(final Object a1) {
    return new AST1(CForm, Object2Expr.convert(a1));
  }


  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial
   *      documentation</a>
   */
  public static IAST characteristicPolynomial(final Object a1, final Object a2) {
    return new AST2(CharacteristicPolynomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CharacterRange(min-character, max-character) - computes a list of character strings from
   * `min-character` to `max-character`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacterRange.md">CharacterRange
   *      documentation</a>
   */
  public static IAST characterRange(final Object a1, final Object a2) {
    return new AST2(CharacterRange, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST characters(final Object a1) {
    return new AST1(Characters, Object2Expr.convert(a1));
  }


  /**
   * ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevT.md">ChebyshevT
   *      documentation</a>
   */
  public static IAST chebyshevT(final Object a1, final Object a2) {
    return new AST2(ChebyshevT, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevU.md">ChebyshevU
   *      documentation</a>
   */
  public static IAST chebyshevU(final Object a1, final Object a2) {
    return new AST2(ChebyshevU, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check
   *      documentation</a>
   */
  public static IAST check(final Object a1, final Object a2) {
    return new AST2(Check, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check
   *      documentation</a>
   */
  public static IAST check(final Object a1, final Object a2, final Object a3) {
    return new AST3(Check, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CheckAbort(expr, failure-expr) - evaluates `expr`, and returns the result, unless `Abort` was
   * called during the evaluation, in which case `failure-expr` will be returned.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CheckAbort.md">CheckAbort
   *      documentation</a>
   */
  public static IAST checkAbort(final Object a1, final Object a2) {
    return new AST2(CheckAbort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance)
   * between `u` and `v`, which is the number of moves a king on a chessboard needs to get from
   * square `u` to square `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChessboardDistance.md">ChessboardDistance
   *      documentation</a>
   */
  public static IAST chessboardDistance(final Object a1, final Object a2) {
    return new AST2(ChessboardDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChineseRemainder.md">ChineseRemainder
   *      documentation</a>
   */
  public static IAST chineseRemainder(final Object a1, final Object a2) {
    return new AST2(ChineseRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive
   * definite square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CholeskyDecomposition.md">CholeskyDecomposition
   *      documentation</a>
   */
  public static IAST choleskyDecomposition(final Object a1) {
    return new AST1(CholeskyDecomposition, Object2Expr.convert(a1));
  }


  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop
   *      documentation</a>
   */
  public static IAST chop(final Object a1) {
    return new AST1(Chop, Object2Expr.convert(a1));
  }


  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop
   *      documentation</a>
   */
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


  /**
   * CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePoints.md">CirclePoints
   *      documentation</a>
   */
  public static IAST circlePoints(final Object a1) {
    return new AST1(CirclePoints, Object2Expr.convert(a1));
  }


  /**
   * ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAttributes.md">ClearAttributes
   *      documentation</a>
   */
  public static IAST clearAttributes(final Object a1, final Object a2) {
    return new AST2(ClearAttributes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ClebschGordan({j1,m1},{j2,m2},{j3,m3}) - get the Clebsch–Gordan coefficients. Clebsch–Gordan
   * coefficients are numbers that arise in angular momentum coupling in quantum mechanic.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClebschGordan.md">ClebschGordan
   *      documentation</a>
   */
  public static IAST clebschGordan(final Object a1, final Object a2, final Object a3) {
    return new AST3(ClebschGordan, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *      documentation</a>
   */
  public static IAST clip(final Object a1) {
    return new AST1(Clip, Object2Expr.convert(a1));
  }


  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *      documentation</a>
   */
  public static IAST clip(final Object a1, final Object a2) {
    return new AST2(Clip, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *      documentation</a>
   */
  public static IAST clip(final Object a1, final Object a2, final Object a3) {
    return new AST3(Clip, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Close(stream) - closes an input or output `stream`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Close.md">Close
   *      documentation</a>
   */
  public static IAST close(final Object a1) {
    return new AST1(Close, Object2Expr.convert(a1));
  }


  /**
   * Close(stream) - closes an input or output `stream`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Close.md">Close
   *      documentation</a>
   */
  public static IAST close(final Object a1, final Object a2) {
    return new AST2(Close, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ClosenessCentrality(graph) - Computes the closeness centrality of each vertex of a `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClosenessCentrality.md">ClosenessCentrality
   *      documentation</a>
   */
  public static IAST closenessCentrality(final Object a1) {
    return new AST1(ClosenessCentrality, Object2Expr.convert(a1));
  }


  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *      documentation</a>
   */
  public static IAST coefficient(final Object a1, final Object a2) {
    return new AST2(Coefficient, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *      documentation</a>
   */
  public static IAST coefficient(final Object a1, final Object a2, final Object a3) {
    return new AST3(Coefficient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientList.md">CoefficientList
   *      documentation</a>
   */
  public static IAST coefficientList(final Object a1, final Object a2) {
    return new AST2(CoefficientList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *      documentation</a>
   */
  public static IAST coefficientRules(final Object a1) {
    return new AST1(CoefficientRules, Object2Expr.convert(a1));
  }


  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *      documentation</a>
   */
  public static IAST coefficientRules(final Object a1, final Object a2) {
    return new AST2(CoefficientRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *      documentation</a>
   */
  public static IAST coefficientRules(final Object a1, final Object a2, final Object a3) {
    return new AST3(CoefficientRules, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Cofactor(matrix, {i,j}) - calculate the cofactor of the matrix
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cofactor.md">Cofactor
   *      documentation</a>
   */
  public static IAST cofactor(final Object a1, final Object a2) {
    return new AST2(Cofactor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect
   *      documentation</a>
   */
  public static IAST collect(final Object a1, final Object a2) {
    return new AST2(Collect, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect
   *      documentation</a>
   */
  public static IAST collect(final Object a1, final Object a2, final Object a3) {
    return new AST3(Collect, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CollinearPoints({{x1,y1},{x2,y2},{a,b},...}) - returns true if the point `{a,b]` is on the line
   * defined by the first two points `{x1,y1},{x2,y2}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CollinearPoints.md">CollinearPoints
   *      documentation</a>
   */
  public static IAST collinearPoints(final Object a1) {
    return new AST1(CollinearPoints, Object2Expr.convert(a1));
  }


  /**
   * Commonest(dataValueList) - the mode of a list of data values is the value that appears most
   * often.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *      documentation</a>
   */
  public static IAST commonest(final Object a1) {
    return new AST1(Commonest, Object2Expr.convert(a1));
  }


  /**
   * Commonest(dataValueList) - the mode of a list of data values is the value that appears most
   * often.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *      documentation</a>
   */
  public static IAST commonest(final Object a1, final Object a2) {
    return new AST2(Commonest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compiled result in an
   * `CompiledFunction` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile
   *      documentation</a>
   */
  public static IAST compile(final Object a1, final Object a2) {
    return new AST2(Compile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compiled result in an
   * `CompiledFunction` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile
   *      documentation</a>
   */
  public static IAST compile(final Object a1, final Object a2, final Object a3) {
    return new AST3(Compile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CompilePrint(list-of-arguments}, expression) - compile the `expression` into a Java function
   * and return the corresponding Java source code function, which has the arguments defined in
   * `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to
   * Java binary code.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilePrint.md">CompilePrint
   *      documentation</a>
   */
  public static IAST compilePrint(final Object a1, final Object a2) {
    return new AST2(CompilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CompilePrint(list-of-arguments}, expression) - compile the `expression` into a Java function
   * and return the corresponding Java source code function, which has the arguments defined in
   * `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to
   * Java binary code.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilePrint.md">CompilePrint
   *      documentation</a>
   */
  public static IAST compilePrint(final Object a1, final Object a2, final Object a3) {
    return new AST3(CompilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement
   *      documentation</a>
   */
  public static IAST complement(final Object a1, final Object a2) {
    return new AST2(Complement, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement
   *      documentation</a>
   */
  public static IAST complement(final Object a1, final Object a2, final Object a3) {
    return new AST3(Complement, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CompleteGraph(order) - returns the complete graph with `order` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompleteGraph.md">CompleteGraph
   *      documentation</a>
   */
  public static IAST completeGraph(final Object a1) {
    return new AST1(CompleteGraph, Object2Expr.convert(a1));
  }


  /**
   * Complex - is the head of complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *      documentation</a>
   */
  public static IAST complex(final Object a1) {
    return new AST1(Complex, Object2Expr.convert(a1));
  }


  /**
   * Complex - is the head of complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *      documentation</a>
   */
  public static IAST complex(final Object a1, final Object a2) {
    return new AST2(Complex, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ComplexExpand(expr) - expands `expr`. All variable symbols in `expr` are assumed to be non
   * complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand
   *      documentation</a>
   */
  public static IAST complexExpand(final Object a1) {
    return new AST1(ComplexExpand, Object2Expr.convert(a1));
  }


  /**
   * ComplexExpand(expr) - expands `expr`. All variable symbols in `expr` are assumed to be non
   * complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand
   *      documentation</a>
   */
  public static IAST complexExpand(final Object a1, final Object a2) {
    return new AST2(ComplexExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CompositeQ(n) - returns `True` if `n` is a composite integer number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompositeQ.md">CompositeQ
   *      documentation</a>
   */
  public static IAST compositeQ(final Object a1) {
    return new AST1(CompositeQ, Object2Expr.convert(a1));
  }


  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *      documentation</a>
   */
  public static IAST compoundExpression(final Object a1) {
    return new AST1(CompoundExpression, Object2Expr.convert(a1));
  }


  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *      documentation</a>
   */
  public static IAST compoundExpression(final Object a1, final Object a2) {
    return new AST2(CompoundExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *      documentation</a>
   */
  public static IAST compoundExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(CompoundExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to
   * match if `expr` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition
   *      documentation</a>
   */
  public static IAST condition(final Object a1, final Object a2) {
    return new AST2(Condition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ConditionalExpression(expr, condition) - if `condition` evaluates to `True` return `expr`, if
   * `condition` evaluates to `False` return `Undefined`. Otherwise return the
   * `ConditionalExpression` unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConditionalExpression.md">ConditionalExpression
   *      documentation</a>
   */
  public static IAST conditionalExpression(final Object a1, final Object a2) {
    return new AST2(ConditionalExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST cone(final Object a1) {
    return new AST1(Cone, Object2Expr.convert(a1));
  }


  public static IAST cone(final Object a1, final Object a2) {
    return new AST2(Cone, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Conjugate(z) - returns the complex conjugate of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conjugate.md">Conjugate
   *      documentation</a>
   */
  public static IAST conjugate(final Object a1) {
    return new AST1(Conjugate, Object2Expr.convert(a1));
  }


  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose
   *      documentation</a>
   */
  public static IAST conjugateTranspose(final Object a1) {
    return new AST1(ConjugateTranspose, Object2Expr.convert(a1));
  }


  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose
   *      documentation</a>
   */
  public static IAST conjugateTranspose(final Object a1, final Object a2) {
    return new AST2(ConjugateTranspose, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST connectedGraphQ(final Object a1) {
    return new AST1(ConnectedGraphQ, Object2Expr.convert(a1));
  }


  /**
   * ConstantArray(expr, n) - returns a list of `n` copies of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantArray.md">ConstantArray
   *      documentation</a>
   */
  public static IAST constantArray(final Object a1, final Object a2) {
    return new AST2(ConstantArray, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST containsAll(final Object a1, final Object a2) {
    return new AST2(ContainsAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST containsAny(final Object a1, final Object a2) {
    return new AST2(ContainsAny, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST containsExactly(final Object a1, final Object a2) {
    return new AST2(ContainsExactly, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST containsNone(final Object a1, final Object a2) {
    return new AST2(ContainsNone, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in
   * `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly
   *      documentation</a>
   */
  public static IAST containsOnly(final Object a1, final Object a2) {
    return new AST2(ContainsOnly, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Context(symbol) - yields the name of the context where `symbol` is defined in.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context
   *      documentation</a>
   */
  public static IAST context(final Object a1) {
    return new AST1(Context, Object2Expr.convert(a1));
  }


  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction
   *      documentation</a>
   */
  public static IAST continuedFraction(final Object a1) {
    return new AST1(ContinuedFraction, Object2Expr.convert(a1));
  }


  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction
   *      documentation</a>
   */
  public static IAST continuedFraction(final Object a1, final Object a2) {
    return new AST2(ContinuedFraction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents
   *      documentation</a>
   */
  public static IAST convergents(final Object a1) {
    return new AST1(Convergents, Object2Expr.convert(a1));
  }


  public static IAST convexHullMesh(final Object a1) {
    return new AST1(ConvexHullMesh, Object2Expr.convert(a1));
  }


  /**
   * CoordinateBoundingBox({{x1,y1,...},{x2,y2,...},{x3,y3,...},...}) - calculate the bounding box
   * of the points `{{x1,y1,...},{x2,y2,...},{x3,y3,...},...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoordinateBoundingBox.md">CoordinateBoundingBox
   *      documentation</a>
   */
  public static IAST coordinateBoundingBox(final Object a1) {
    return new AST1(CoordinateBoundingBox, Object2Expr.convert(a1));
  }


  /**
   * CoordinateBoundingBox({{x1,y1,...},{x2,y2,...},{x3,y3,...},...}) - calculate the bounding box
   * of the points `{{x1,y1,...},{x2,y2,...},{x3,y3,...},...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoordinateBoundingBox.md">CoordinateBoundingBox
   *      documentation</a>
   */
  public static IAST coordinateBoundingBox(final Object a1, final Object a2) {
    return new AST2(CoordinateBoundingBox, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST coordinateBounds(final Object a1) {
    return new AST1(CoordinateBounds, Object2Expr.convert(a1));
  }


  public static IAST coordinateBounds(final Object a1, final Object a2) {
    return new AST2(CoordinateBounds, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...}) - returns true if the point
   * `{a,b,c]` is on the plane defined by the first three points `{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoplanarPoints.md">CoplanarPoints
   *      documentation</a>
   */
  public static IAST coplanarPoints(final Object a1) {
    return new AST1(CoplanarPoints, Object2Expr.convert(a1));
  }


  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation
   *      documentation</a>
   */
  public static IAST correlation(final Object a1) {
    return new AST1(Correlation, Object2Expr.convert(a1));
  }


  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation
   *      documentation</a>
   */
  public static IAST correlation(final Object a1, final Object a2) {
    return new AST2(Correlation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CorrelationDistance(u, v) - returns the correlation distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CorrelationDistance.md">CorrelationDistance
   *      documentation</a>
   */
  public static IAST correlationDistance(final Object a1, final Object a2) {
    return new AST2(CorrelationDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate
   * automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos
   *      documentation</a>
   */
  public static IAST cos(final Object a1) {
    return new AST1(Cos, Object2Expr.convert(a1));
  }


  /**
   * Cosh(z) - returns the hyperbolic cosine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cosh.md">Cosh
   *      documentation</a>
   */
  public static IAST cosh(final Object a1) {
    return new AST1(Cosh, Object2Expr.convert(a1));
  }


  /**
   * CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoshIntegral.md">CoshIntegral
   *      documentation</a>
   */
  public static IAST coshIntegral(final Object a1) {
    return new AST1(CoshIntegral, Object2Expr.convert(a1));
  }


  /**
   * CosineDistance(u, v) - returns the cosine distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosineDistance.md">CosineDistance
   *      documentation</a>
   */
  public static IAST cosineDistance(final Object a1, final Object a2) {
    return new AST2(CosineDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CosIntegral(expr) - returns the cosine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosIntegral.md">CosIntegral
   *      documentation</a>
   */
  public static IAST cosIntegral(final Object a1) {
    return new AST1(CosIntegral, Object2Expr.convert(a1));
  }


  /**
   * Cot(expr) - the cotangent function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot
   *      documentation</a>
   */
  public static IAST cot(final Object a1) {
    return new AST1(Cot, Object2Expr.convert(a1));
  }


  /**
   * Coth(z) - returns the hyperbolic cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coth.md">Coth
   *      documentation</a>
   */
  public static IAST coth(final Object a1) {
    return new AST1(Coth, Object2Expr.convert(a1));
  }


  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *      documentation</a>
   */
  public static IAST count(final Object a1) {
    return new AST1(Count, Object2Expr.convert(a1));
  }


  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *      documentation</a>
   */
  public static IAST count(final Object a1, final Object a2) {
    return new AST2(Count, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *      documentation</a>
   */
  public static IAST count(final Object a1, final Object a2, final Object a3) {
    return new AST3(Count, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CountDistinct(list) - returns the number of distinct entries in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CountDistinct.md">CountDistinct
   *      documentation</a>
   */
  public static IAST countDistinct(final Object a1) {
    return new AST1(CountDistinct, Object2Expr.convert(a1));
  }


  /**
   * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list
   * `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1,
   * ...|>`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Counts.md">Counts
   *      documentation</a>
   */
  public static IAST counts(final Object a1) {
    return new AST1(Counts, Object2Expr.convert(a1));
  }


  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance
   *      documentation</a>
   */
  public static IAST covariance(final Object a1) {
    return new AST1(Covariance, Object2Expr.convert(a1));
  }


  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance
   *      documentation</a>
   */
  public static IAST covariance(final Object a1, final Object a2) {
    return new AST2(Covariance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST createDirectory(final Object a1) {
    return new AST1(CreateDirectory, Object2Expr.convert(a1));
  }


  public static IAST createFile(final Object a1) {
    return new AST1(CreateFile, Object2Expr.convert(a1));
  }


  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public static IAST cross(final Object a1) {
    return new AST1(Cross, Object2Expr.convert(a1));
  }


  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public static IAST cross(final Object a1, final Object a2) {
    return new AST2(Cross, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public static IAST cross(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cross, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Csc(z) - returns the cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csc.md">Csc
   *      documentation</a>
   */
  public static IAST csc(final Object a1) {
    return new AST1(Csc, Object2Expr.convert(a1));
  }


  /**
   * Csch(z) - returns the hyperbolic cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csch.md">Csch
   *      documentation</a>
   */
  public static IAST csch(final Object a1) {
    return new AST1(Csch, Object2Expr.convert(a1));
  }


  public static IAST cube(final Object a1) {
    return new AST1(Cube, Object2Expr.convert(a1));
  }


  public static IAST cube(final Object a1, final Object a2) {
    return new AST2(Cube, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST cube(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cube, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * CubeRoot(n) - finds the real-valued cube root of the given `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CubeRoot.md">CubeRoot
   *      documentation</a>
   */
  public static IAST cubeRoot(final Object a1) {
    return new AST1(CubeRoot, Object2Expr.convert(a1));
  }


  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public static IAST cuboid(final Object a1) {
    return new AST1(Cuboid, Object2Expr.convert(a1));
  }


  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public static IAST cuboid(final Object a1, final Object a2) {
    return new AST2(Cuboid, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public static IAST cuboid(final Object a1, final Object a2, final Object a3) {
    return new AST3(Cuboid, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Curl({f1, f2}, {x1, x2}) - returns the curl `D(f2, x1) - D(f1, x2)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl
   *      documentation</a>
   */
  public static IAST curl(final Object a1, final Object a2) {
    return new AST2(Curl, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * CycleGraph(order) - returns the cycle graph with `order` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CycleGraph.md">CycleGraph
   *      documentation</a>
   */
  public static IAST cycleGraph(final Object a1) {
    return new AST1(CycleGraph, Object2Expr.convert(a1));
  }


  /**
   * Cycles(a, b) - expression for defining canonical cycles of a permutation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cycles.md">Cycles
   *      documentation</a>
   */
  public static IAST cycles(final Object a1) {
    return new AST1(Cycles, Object2Expr.convert(a1));
  }


  /**
   * Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyclotomic.md">Cyclotomic
   *      documentation</a>
   */
  public static IAST cyclotomic(final Object a1, final Object a2) {
    return new AST2(Cyclotomic, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Cylinder({{x1, y1, z1}, {x2, y2, z2}}) - represents a cylinder of radius `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cylinder.md">Cylinder
   *      documentation</a>
   */
  public static IAST cylinder(final Object a1) {
    return new AST1(Cylinder, Object2Expr.convert(a1));
  }


  /**
   * Cylinder({{x1, y1, z1}, {x2, y2, z2}}) - represents a cylinder of radius `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cylinder.md">Cylinder
   *      documentation</a>
   */
  public static IAST cylinder(final Object a1, final Object a2) {
    return new AST2(Cylinder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *      documentation</a>
   */
  public static IAST d(final Object a1) {
    return new AST1(D, Object2Expr.convert(a1));
  }


  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *      documentation</a>
   */
  public static IAST d(final Object a1, final Object a2) {
    return new AST2(D, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *      documentation</a>
   */
  public static IAST d(final Object a1, final Object a2, final Object a3) {
    return new AST3(D, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
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


  /**
   * Decrement(x) - decrements `x` by `1`, returning the original value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Decrement.md">Decrement
   *      documentation</a>
   */
  public static IAST decrement(final Object a1) {
    return new AST1(Decrement, Object2Expr.convert(a1));
  }


  /**
   * DedekindNumber(n) - returns the `n`th Dedekind number. Currently `0 <= n <= 9` can be computed,
   * otherwise the function returns unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DedekindNumber.md">DedekindNumber
   *      documentation</a>
   */
  public static IAST dedekindNumber(final Object a1) {
    return new AST1(DedekindNumber, Object2Expr.convert(a1));
  }


  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default
   *      documentation</a>
   */
  public static IAST $default(final Object a1) {
    return new AST1(Default, Object2Expr.convert(a1));
  }


  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default
   *      documentation</a>
   */
  public static IAST $default(final Object a1, final Object a2) {
    return new AST2(Default, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer
   *      documentation</a>
   */
  public static IAST defer(final Object a1) {
    return new AST1(Defer, Object2Expr.convert(a1));
  }


  /**
   * Definition(symbol) - prints user-defined values and rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Definition.md">Definition
   *      documentation</a>
   */
  public static IAST definition(final Object a1) {
    return new AST1(Definition, Object2Expr.convert(a1));
  }


  /**
   * Delete(expr, n) - returns `expr` with part `n` removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete
   *      documentation</a>
   */
  public static IAST delete(final Object a1, final Object a2) {
    return new AST2(Delete, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases
   *      documentation</a>
   */
  public static IAST deleteCases(final Object a1, final Object a2) {
    return new AST2(DeleteCases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases
   *      documentation</a>
   */
  public static IAST deleteCases(final Object a1, final Object a2, final Object a3) {
    return new AST3(DeleteCases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates
   *      documentation</a>
   */
  public static IAST deleteDuplicates(final Object a1) {
    return new AST1(DeleteDuplicates, Object2Expr.convert(a1));
  }


  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates
   *      documentation</a>
   */
  public static IAST deleteDuplicates(final Object a1, final Object a2) {
    return new AST2(DeleteDuplicates, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy
   *      documentation</a>
   */
  public static IAST deleteDuplicatesBy(final Object a1) {
    return new AST1(DeleteDuplicatesBy, Object2Expr.convert(a1));
  }


  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy
   *      documentation</a>
   */
  public static IAST deleteDuplicatesBy(final Object a1, final Object a2) {
    return new AST2(DeleteDuplicatesBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST deleteMissing(final Object a1) {
    return new AST1(DeleteMissing, Object2Expr.convert(a1));
  }


  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator
   *      documentation</a>
   */
  public static IAST denominator(final Object a1) {
    return new AST1(Denominator, Object2Expr.convert(a1));
  }


  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator
   *      documentation</a>
   */
  public static IAST denominator(final Object a1, final Object a2) {
    return new AST2(Denominator, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Depth(expr) - gets the depth of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Depth.md">Depth
   *      documentation</a>
   */
  public static IAST depth(final Object a1) {
    return new AST1(Depth, Object2Expr.convert(a1));
  }


  /**
   * DesignMatrix(m, f, x) - returns the design matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DesignMatrix.md">DesignMatrix
   *      documentation</a>
   */
  public static IAST designMatrix(final Object a1, final Object a2, final Object a3) {
    return new AST3(DesignMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Det(matrix) - computes the determinant of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det
   *      documentation</a>
   */
  public static IAST det(final Object a1) {
    return new AST1(Det, Object2Expr.convert(a1));
  }


  /**
   * Det(matrix) - computes the determinant of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det
   *      documentation</a>
   */
  public static IAST det(final Object a1, final Object a2) {
    return new AST2(Det, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal
   *      documentation</a>
   */
  public static IAST diagonal(final Object a1) {
    return new AST1(Diagonal, Object2Expr.convert(a1));
  }


  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal
   *      documentation</a>
   */
  public static IAST diagonal(final Object a1, final Object a2) {
    return new AST2(Diagonal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix
   *      documentation</a>
   */
  public static IAST diagonalMatrix(final Object a1) {
    return new AST1(DiagonalMatrix, Object2Expr.convert(a1));
  }


  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix
   *      documentation</a>
   */
  public static IAST diagonalMatrix(final Object a1, final Object a2) {
    return new AST2(DiagonalMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DiagonalMatrixQ(matrix) - returns `True` if all elements of the `matrix` are `0` except the
   * elements on the `diagonal`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrixQ.md">DiagonalMatrixQ
   *      documentation</a>
   */
  public static IAST diagonalMatrixQ(final Object a1) {
    return new AST1(DiagonalMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * DiagonalMatrixQ(matrix) - returns `True` if all elements of the `matrix` are `0` except the
   * elements on the `diagonal`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrixQ.md">DiagonalMatrixQ
   *      documentation</a>
   */
  public static IAST diagonalMatrixQ(final Object a1, final Object a2) {
    return new AST2(DiagonalMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)`
   * and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiceDissimilarity.md">DiceDissimilarity
   *      documentation</a>
   */
  public static IAST diceDissimilarity(final Object a1, final Object a2) {
    return new AST2(DiceDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DifferenceDelta(f(x), x) - generates a forward difference `f(x+1) - f(x)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceDelta.md">DifferenceDelta
   *      documentation</a>
   */
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


  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *      documentation</a>
   */
  public static IAST digitCount(final Object a1) {
    return new AST1(DigitCount, Object2Expr.convert(a1));
  }


  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *      documentation</a>
   */
  public static IAST digitCount(final Object a1, final Object a2) {
    return new AST2(DigitCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *      documentation</a>
   */
  public static IAST digitCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(DigitCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * DigitQ(str) - returns `True` if `str` is a string which contains only digits.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitQ.md">DigitQ
   *      documentation</a>
   */
  public static IAST digitQ(final Object a1) {
    return new AST1(DigitQ, Object2Expr.convert(a1));
  }


  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions
   *      documentation</a>
   */
  public static IAST dimensions(final Object a1) {
    return new AST1(Dimensions, Object2Expr.convert(a1));
  }


  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions
   *      documentation</a>
   */
  public static IAST dimensions(final Object a1, final Object a2) {
    return new AST2(Dimensions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DirectedInfinity(z) - represents an infinite multiple of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedInfinity.md">DirectedInfinity
   *      documentation</a>
   */
  public static IAST directedInfinity(final Object a1) {
    return new AST1(DirectedInfinity, Object2Expr.convert(a1));
  }


  public static IAST dirichletEta(final Object a1) {
    return new AST1(DirichletEta, Object2Expr.convert(a1));
  }


  public static IAST dirichletWindow(final Object a1) {
    return new AST1(DirichletWindow, Object2Expr.convert(a1));
  }


  /**
   * DiscretePlot( expr, {x, nmax} ) - plots `expr` with `x` ranging from `1` to `nmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscretePlot.md">DiscretePlot
   *      documentation</a>
   */
  public static IAST discretePlot(final Object a1, final Object a2) {
    return new AST2(DiscretePlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DiscretePlot( expr, {x, nmax} ) - plots `expr` with `x` ranging from `1` to `nmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscretePlot.md">DiscretePlot
   *      documentation</a>
   */
  public static IAST discretePlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(DiscretePlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteUniformDistribution.md">DiscreteUniformDistribution
   *      documentation</a>
   */
  public static IAST discreteUniformDistribution(final Object a1) {
    return new AST1(DiscreteUniformDistribution, Object2Expr.convert(a1));
  }


  /**
   * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to
   * the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Discriminant.md">Discriminant
   *      documentation</a>
   */
  public static IAST discriminant(final Object a1, final Object a2) {
    return new AST2(Discriminant, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST disjointQ(final Object a1, final Object a2) {
    return new AST2(DisjointQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST disk(final Object a1) {
    return new AST1(Disk, Object2Expr.convert(a1));
  }


  public static IAST disk(final Object a1, final Object a2) {
    return new AST2(Disk, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST disk(final Object a1, final Object a2, final Object a3) {
    return new AST3(Disk, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *      documentation</a>
   */
  public static IAST distribute(final Object a1) {
    return new AST1(Distribute, Object2Expr.convert(a1));
  }


  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *      documentation</a>
   */
  public static IAST distribute(final Object a1, final Object a2) {
    return new AST2(Distribute, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *      documentation</a>
   */
  public static IAST distribute(final Object a1, final Object a2, final Object a3) {
    return new AST3(Distribute, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Div.md">Div
   *      documentation</a>
   */
  public static IAST div(final Object a1, final Object a2) {
    return new AST2(Div, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Divide(a, b) - represents the division of `a` by `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divide.md">Divide
   *      documentation</a>
   */
  public static IAST divide(final Object a1, final Object a2) {
    return new AST2(Divide, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DivideBy(x, dx) - is equivalent to `x = x / dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideBy.md">DivideBy
   *      documentation</a>
   */
  public static IAST divideBy(final Object a1, final Object a2) {
    return new AST2(DivideBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DivideSides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideSides.md">DivideSides
   *      documentation</a>
   */
  public static IAST divideSides(final Object a1) {
    return new AST1(DivideSides, Object2Expr.convert(a1));
  }


  /**
   * DivideSides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideSides.md">DivideSides
   *      documentation</a>
   */
  public static IAST divideSides(final Object a1, final Object a2) {
    return new AST2(DivideSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Divisible(n, m) - returns `True` if `n` could be divide by `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisible.md">Divisible
   *      documentation</a>
   */
  public static IAST divisible(final Object a1, final Object a2) {
    return new AST2(Divisible, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Divisors(n) - returns all integers that divide the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisors.md">Divisors
   *      documentation</a>
   */
  public static IAST divisors(final Object a1) {
    return new AST1(Divisors, Object2Expr.convert(a1));
  }


  /**
   * DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSigma.md">DivisorSigma
   *      documentation</a>
   */
  public static IAST divisorSigma(final Object a1, final Object a2) {
    return new AST2(DivisorSigma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum
   *      documentation</a>
   */
  public static IAST divisorSum(final Object a1, final Object a2) {
    return new AST2(DivisorSum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum
   *      documentation</a>
   */
  public static IAST divisorSum(final Object a1, final Object a2, final Object a3) {
    return new AST3(DivisorSum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Do(expr, {max}) - evaluates `expr` `max` times.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do
   *      documentation</a>
   */
  public static IAST $do(final Object a1, final Object a2) {
    return new AST2(Do, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Do(expr, {max}) - evaluates `expr` `max` times.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do
   *      documentation</a>
   */
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


  /**
   * DownValues(symbol) - prints the down-value rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownValues.md">DownValues
   *      documentation</a>
   */
  public static IAST downValues(final Object a1) {
    return new AST1(DownValues, Object2Expr.convert(a1));
  }


  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop
   *      documentation</a>
   */
  public static IAST drop(final Object a1, final Object a2) {
    return new AST2(Drop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop
   *      documentation</a>
   */
  public static IAST drop(final Object a1, final Object a2, final Object a3) {
    return new AST3(Drop, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve
   *      documentation</a>
   */
  public static IAST dSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(DSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
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


  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *      documentation</a>
   */
  public static IAST echo(final Object a1) {
    return new AST1(Echo, Object2Expr.convert(a1));
  }


  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *      documentation</a>
   */
  public static IAST echo(final Object a1, final Object a2) {
    return new AST2(Echo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *      documentation</a>
   */
  public static IAST echo(final Object a1, final Object a2, final Object a3) {
    return new AST3(Echo, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction
   *      documentation</a>
   */
  public static IAST echoFunction(final Object a1) {
    return new AST1(EchoFunction, Object2Expr.convert(a1));
  }


  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction
   *      documentation</a>
   */
  public static IAST echoFunction(final Object a1, final Object a2) {
    return new AST2(EchoFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EdgeList(graph) - convert the `graph` into a list of edges.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeList.md">EdgeList
   *      documentation</a>
   */
  public static IAST edgeList(final Object a1) {
    return new AST1(EdgeList, Object2Expr.convert(a1));
  }


  /**
   * EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeQ.md">EdgeQ
   *      documentation</a>
   */
  public static IAST edgeQ(final Object a1, final Object a2) {
    return new AST2(EdgeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EdgeRules(graph) - convert the `graph` into a list of rules. All edge types (undirected,
   * directed) are represented by a rule `lhs->rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeRules.md">EdgeRules
   *      documentation</a>
   */
  public static IAST edgeRules(final Object a1) {
    return new AST1(EdgeRules, Object2Expr.convert(a1));
  }


  /**
   * EditDistance(a, b) - returns the Levenshtein distance of `a` and `b`, which is defined as the
   * minimum number of insertions, deletions and substitutions on the constituents of `a` and `b`
   * needed to transform one into the other.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EditDistance.md">EditDistance
   *      documentation</a>
   */
  public static IAST editDistance(final Object a1, final Object a2) {
    return new AST2(EditDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest
   *      documentation</a>
   */
  public static IAST effectiveInterest(final Object a1) {
    return new AST1(EffectiveInterest, Object2Expr.convert(a1));
  }


  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest
   *      documentation</a>
   */
  public static IAST effectiveInterest(final Object a1, final Object a2) {
    return new AST2(EffectiveInterest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Eigensystem(matrix) - return the numerical eigensystem of the `matrix` as a list `{eigenvalues,
   * eigenvectors}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigensystem.md">Eigensystem
   *      documentation</a>
   */
  public static IAST eigensystem(final Object a1) {
    return new AST1(Eigensystem, Object2Expr.convert(a1));
  }


  /**
   * Eigensystem(matrix) - return the numerical eigensystem of the `matrix` as a list `{eigenvalues,
   * eigenvectors}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigensystem.md">Eigensystem
   *      documentation</a>
   */
  public static IAST eigensystem(final Object a1, final Object a2) {
    return new AST2(Eigensystem, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues
   *      documentation</a>
   */
  public static IAST eigenvalues(final Object a1) {
    return new AST1(Eigenvalues, Object2Expr.convert(a1));
  }


  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues
   *      documentation</a>
   */
  public static IAST eigenvalues(final Object a1, final Object a2) {
    return new AST2(Eigenvalues, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST eigenvectorCentrality(final Object a1) {
    return new AST1(EigenvectorCentrality, Object2Expr.convert(a1));
  }


  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors
   *      documentation</a>
   */
  public static IAST eigenvectors(final Object a1) {
    return new AST1(Eigenvectors, Object2Expr.convert(a1));
  }


  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors
   *      documentation</a>
   */
  public static IAST eigenvectors(final Object a1, final Object a2) {
    return new AST2(Eigenvectors, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Element(symbol, domain) - assume (or test) that the `symbol` is in the domain `domain`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Element.md">Element
   *      documentation</a>
   */
  public static IAST element(final Object a1, final Object a2) {
    return new AST2(Element, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData
   *      documentation</a>
   */
  public static IAST elementData(final Object a1) {
    return new AST1(ElementData, Object2Expr.convert(a1));
  }


  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData
   *      documentation</a>
   */
  public static IAST elementData(final Object a1, final Object a2) {
    return new AST2(ElementData, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
   * `list-of-variables` in the `list-of-equations`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eliminate.md">Eliminate
   *      documentation</a>
   */
  public static IAST eliminate(final Object a1, final Object a2) {
    return new AST2(Eliminate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE
   *      documentation</a>
   */
  public static IAST ellipticE(final Object a1) {
    return new AST1(EllipticE, Object2Expr.convert(a1));
  }


  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE
   *      documentation</a>
   */
  public static IAST ellipticE(final Object a1, final Object a2) {
    return new AST2(EllipticE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EllipticF(z) - returns the incomplete elliptic integral of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticF.md">EllipticF
   *      documentation</a>
   */
  public static IAST ellipticF(final Object a1, final Object a2) {
    return new AST2(EllipticF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EllipticK(z) - returns the complete elliptic integral of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticK.md">EllipticK
   *      documentation</a>
   */
  public static IAST ellipticK(final Object a1) {
    return new AST1(EllipticK, Object2Expr.convert(a1));
  }


  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi
   *      documentation</a>
   */
  public static IAST ellipticPi(final Object a1, final Object a2) {
    return new AST2(EllipticPi, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi
   *      documentation</a>
   */
  public static IAST ellipticPi(final Object a1, final Object a2, final Object a3) {
    return new AST3(EllipticPi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST ellipticTheta(final Object a1, final Object a2) {
    return new AST2(EllipticTheta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST ellipticTheta(final Object a1, final Object a2, final Object a3) {
    return new AST3(EllipticTheta, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *      documentation</a>
   */
  public static IAST entropy(final Object a1) {
    return new AST1(Entropy, Object2Expr.convert(a1));
  }


  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *      documentation</a>
   */
  public static IAST entropy(final Object a1, final Object a2) {
    return new AST2(Entropy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST equalTo(final Object a1) {
    return new AST1(EqualTo, Object2Expr.convert(a1));
  }


  /**
   * Erf(z) - returns the error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf
   *      documentation</a>
   */
  public static IAST erf(final Object a1) {
    return new AST1(Erf, Object2Expr.convert(a1));
  }


  /**
   * Erf(z) - returns the error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf
   *      documentation</a>
   */
  public static IAST erf(final Object a1, final Object a2) {
    return new AST2(Erf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Erfc(z) - returns the complementary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfc.md">Erfc
   *      documentation</a>
   */
  public static IAST erfc(final Object a1) {
    return new AST1(Erfc, Object2Expr.convert(a1));
  }


  /**
   * Erfi(z) - returns the imaginary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfi.md">Erfi
   *      documentation</a>
   */
  public static IAST erfi(final Object a1) {
    return new AST1(Erfi, Object2Expr.convert(a1));
  }


  /**
   * ErlangDistribution({k, lambda}) - returns a Erlang distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ErlangDistribution.md">ErlangDistribution
   *      documentation</a>
   */
  public static IAST erlangDistribution(final Object a1, final Object a2) {
    return new AST2(ErlangDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EuclideanDistance.md">EuclideanDistance
   *      documentation</a>
   */
  public static IAST euclideanDistance(final Object a1, final Object a2) {
    return new AST2(EuclideanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EulerE(n) - gives the euler number `En`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE
   *      documentation</a>
   */
  public static IAST eulerE(final Object a1) {
    return new AST1(EulerE, Object2Expr.convert(a1));
  }


  /**
   * EulerE(n) - gives the euler number `En`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE
   *      documentation</a>
   */
  public static IAST eulerE(final Object a1, final Object a2) {
    return new AST2(EulerE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerianGraphQ.md">EulerianGraphQ
   *      documentation</a>
   */
  public static IAST eulerianGraphQ(final Object a1) {
    return new AST1(EulerianGraphQ, Object2Expr.convert(a1));
  }


  /**
   * EulerPhi(n) - compute Euler's totient function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerPhi.md">EulerPhi
   *      documentation</a>
   */
  public static IAST eulerPhi(final Object a1) {
    return new AST1(EulerPhi, Object2Expr.convert(a1));
  }


  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ
   *      documentation</a>
   */
  public static IAST evenQ(final Object a1) {
    return new AST1(EvenQ, Object2Expr.convert(a1));
  }


  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ
   *      documentation</a>
   */
  public static IAST evenQ(final Object a1, final Object a2) {
    return new AST2(EvenQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExactNumberQ.md">ExactNumberQ
   *      documentation</a>
   */
  public static IAST exactNumberQ(final Object a1) {
    return new AST1(ExactNumberQ, Object2Expr.convert(a1));
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


  /**
   * Exp(z) - the exponential function `E^z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp
   *      documentation</a>
   */
  public static IAST exp(final Object a1) {
    return new AST1(Exp, Object2Expr.convert(a1));
  }


  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand
   *      documentation</a>
   */
  public static IAST expand(final Object a1) {
    return new AST1(Expand, Object2Expr.convert(a1));
  }


  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand
   *      documentation</a>
   */
  public static IAST expand(final Object a1, final Object a2) {
    return new AST2(Expand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll
   *      documentation</a>
   */
  public static IAST expandAll(final Object a1) {
    return new AST1(ExpandAll, Object2Expr.convert(a1));
  }


  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll
   *      documentation</a>
   */
  public static IAST expandAll(final Object a1, final Object a2) {
    return new AST2(ExpandAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expectation.md">Expectation
   *      documentation</a>
   */
  public static IAST expectation(final Object a1, final Object a2) {
    return new AST2(Expectation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralE.md">ExpIntegralE
   *      documentation</a>
   */
  public static IAST expIntegralE(final Object a1, final Object a2) {
    return new AST2(ExpIntegralE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralEi.md">ExpIntegralEi
   *      documentation</a>
   */
  public static IAST expIntegralEi(final Object a1) {
    return new AST1(ExpIntegralEi, Object2Expr.convert(a1));
  }


  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent
   *      documentation</a>
   */
  public static IAST exponent(final Object a1, final Object a2) {
    return new AST2(Exponent, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent
   *      documentation</a>
   */
  public static IAST exponent(final Object a1, final Object a2, final Object a3) {
    return new AST3(Exponent, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution
   *      documentation</a>
   */
  public static IAST exponentialDistribution(final Object a1) {
    return new AST1(ExponentialDistribution, Object2Expr.convert(a1));
  }


  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *      documentation</a>
   */
  public static IAST export(final Object a1, final Object a2) {
    return new AST2(Export, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *      documentation</a>
   */
  public static IAST export(final Object a1, final Object a2, final Object a3) {
    return new AST3(Export, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST exportString(final Object a1, final Object a2) {
    return new AST2(ExportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST expToTrig(final Object a1) {
    return new AST1(ExpToTrig, Object2Expr.convert(a1));
  }


  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *      documentation</a>
   */
  public static IAST extendedGCD(final Object a1) {
    return new AST1(ExtendedGCD, Object2Expr.convert(a1));
  }


  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *      documentation</a>
   */
  public static IAST extendedGCD(final Object a1, final Object a2) {
    return new AST2(ExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *      documentation</a>
   */
  public static IAST extendedGCD(final Object a1, final Object a2, final Object a3) {
    return new AST3(ExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *      documentation</a>
   */
  public static IAST extract(final Object a1, final Object a2) {
    return new AST2(Extract, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *      documentation</a>
   */
  public static IAST extract(final Object a1, final Object a2, final Object a3) {
    return new AST3(Extract, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Factor(expr) - factors the polynomial expression `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor
   *      documentation</a>
   */
  public static IAST factor(final Object a1) {
    return new AST1(Factor, Object2Expr.convert(a1));
  }


  /**
   * Factor(expr) - factors the polynomial expression `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor
   *      documentation</a>
   */
  public static IAST factor(final Object a1, final Object a2) {
    return new AST2(Factor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Factorial(n) - returns the factorial number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial.md">Factorial
   *      documentation</a>
   */
  public static IAST factorial(final Object a1) {
    return new AST1(Factorial, Object2Expr.convert(a1));
  }


  /**
   * Factorial2(n) - returns the double factorial number of the integer `n` as `n*(n-2)*(n-4)...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial2.md">Factorial2
   *      documentation</a>
   */
  public static IAST factorial2(final Object a1) {
    return new AST1(Factorial2, Object2Expr.convert(a1));
  }


  public static IAST factorialPower(final Object a1, final Object a2) {
    return new AST2(FactorialPower, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST factorialPower(final Object a1, final Object a2, final Object a3) {
    return new AST3(FactorialPower, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree
   *      documentation</a>
   */
  public static IAST factorSquareFree(final Object a1) {
    return new AST1(FactorSquareFree, Object2Expr.convert(a1));
  }


  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree
   *      documentation</a>
   */
  public static IAST factorSquareFree(final Object a1, final Object a2) {
    return new AST2(FactorSquareFree, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFreeList.md">FactorSquareFreeList
   *      documentation</a>
   */
  public static IAST factorSquareFreeList(final Object a1) {
    return new AST1(FactorSquareFreeList, Object2Expr.convert(a1));
  }


  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms
   *      documentation</a>
   */
  public static IAST factorTerms(final Object a1) {
    return new AST1(FactorTerms, Object2Expr.convert(a1));
  }


  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms
   *      documentation</a>
   */
  public static IAST factorTerms(final Object a1, final Object a2) {
    return new AST2(FactorTerms, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FactorTermsList(poly) - pulls out any overall numerical factor in `poly` and returns the result
   * in a list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTermsList.md">FactorTermsList
   *      documentation</a>
   */
  public static IAST factorTermsList(final Object a1) {
    return new AST1(FactorTermsList, Object2Expr.convert(a1));
  }


  /**
   * FactorTermsList(poly) - pulls out any overall numerical factor in `poly` and returns the result
   * in a list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTermsList.md">FactorTermsList
   *      documentation</a>
   */
  public static IAST factorTermsList(final Object a1, final Object a2) {
    return new AST2(FactorTermsList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci
   *      documentation</a>
   */
  public static IAST fibonacci(final Object a1) {
    return new AST1(Fibonacci, Object2Expr.convert(a1));
  }


  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci
   *      documentation</a>
   */
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


  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *      documentation</a>
   */
  public static IAST fileNames(final Object a1) {
    return new AST1(FileNames, Object2Expr.convert(a1));
  }


  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *      documentation</a>
   */
  public static IAST fileNames(final Object a1, final Object a2) {
    return new AST2(FileNames, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *      documentation</a>
   */
  public static IAST fileNames(final Object a1, final Object a2, final Object a3) {
    return new AST3(FileNames, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST fileNameTake(final Object a1) {
    return new AST1(FileNameTake, Object2Expr.convert(a1));
  }


  public static IAST fileNameTake(final Object a1, final Object a2) {
    return new AST2(FileNameTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FilePrint(file) - prints the raw contents of `file`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilePrint.md">FilePrint
   *      documentation</a>
   */
  public static IAST filePrint(final Object a1) {
    return new AST1(FilePrint, Object2Expr.convert(a1));
  }


  /**
   * FilePrint(file) - prints the raw contents of `file`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilePrint.md">FilePrint
   *      documentation</a>
   */
  public static IAST filePrint(final Object a1, final Object a2) {
    return new AST2(FilePrint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FilterRules(list-of-option-rules, list-of-rules) - filter the `list-of-option-rules` by
   * `list-of-rules`or `list-of-symbols`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilterRules.md">FilterRules
   *      documentation</a>
   */
  public static IAST filterRules(final Object a1, final Object a2) {
    return new AST2(FilterRules, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters
   *      documentation</a>
   */
  public static IAST findClusters(final Object a1) {
    return new AST1(FindClusters, Object2Expr.convert(a1));
  }


  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters
   *      documentation</a>
   */
  public static IAST findClusters(final Object a1, final Object a2) {
    return new AST2(FindClusters, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters
   *      documentation</a>
   */
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


  /**
   * FindEulerianCycle(graph) - find an eulerian cycle in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEulerianCycle.md">FindEulerianCycle
   *      documentation</a>
   */
  public static IAST findEulerianCycle(final Object a1) {
    return new AST1(FindEulerianCycle, Object2Expr.convert(a1));
  }


  /**
   * FindGraphIsomorphism(graph1, graph2) - returns an isomorphism between `graph1` and `graph2` if
   * it exists. Return an empty list if no isomorphism exists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindGraphIsomorphism.md">FindGraphIsomorphism
   *      documentation</a>
   */
  public static IAST findGraphIsomorphism(final Object a1, final Object a2) {
    return new AST2(FindGraphIsomorphism, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindHamiltonianCycle.md">FindHamiltonianCycle
   *      documentation</a>
   */
  public static IAST findHamiltonianCycle(final Object a1) {
    return new AST1(FindHamiltonianCycle, Object2Expr.convert(a1));
  }


  public static IAST findIndependentVertexSet(final Object a1) {
    return new AST1(FindIndependentVertexSet, Object2Expr.convert(a1));
  }


  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance
   *      documentation</a>
   */
  public static IAST findInstance(final Object a1, final Object a2) {
    return new AST2(FindInstance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance
   *      documentation</a>
   */
  public static IAST findInstance(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindInstance, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindLinearRecurrence(list) - compute a minimal linear recurrence which returns list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindLinearRecurrence.md">FindLinearRecurrence
   *      documentation</a>
   */
  public static IAST findLinearRecurrence(final Object a1) {
    return new AST1(FindLinearRecurrence, Object2Expr.convert(a1));
  }


  /**
   * FindLinearRecurrence(list) - compute a minimal linear recurrence which returns list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindLinearRecurrence.md">FindLinearRecurrence
   *      documentation</a>
   */
  public static IAST findLinearRecurrence(final Object a1, final Object a2) {
    return new AST2(FindLinearRecurrence, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindMaximum(f, {x, xstart}) - searches for a local numerical maximum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMaximum.md">FindMaximum
   *      documentation</a>
   */
  public static IAST findMaximum(final Object a1, final Object a2) {
    return new AST2(FindMaximum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindMaximum(f, {x, xstart}) - searches for a local numerical maximum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMaximum.md">FindMaximum
   *      documentation</a>
   */
  public static IAST findMaximum(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindMaximum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindMinimum(f, {x, xstart}) - searches for a local numerical minimum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMinimum.md">FindMinimum
   *      documentation</a>
   */
  public static IAST findMinimum(final Object a1, final Object a2) {
    return new AST2(FindMinimum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindMinimum(f, {x, xstart}) - searches for a local numerical minimum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMinimum.md">FindMinimum
   *      documentation</a>
   */
  public static IAST findMinimum(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindMinimum, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation
   *      documentation</a>
   */
  public static IAST findPermutation(final Object a1) {
    return new AST1(FindPermutation, Object2Expr.convert(a1));
  }


  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation
   *      documentation</a>
   */
  public static IAST findPermutation(final Object a1, final Object a2) {
    return new AST2(FindPermutation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *      documentation</a>
   */
  public static IAST findRoot(final Object a1, final Object a2) {
    return new AST2(FindRoot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *      documentation</a>
   */
  public static IAST findRoot(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindRoot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from
   * `source` to `destination`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestPath.md">FindShortestPath
   *      documentation</a>
   */
  public static IAST findShortestPath(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindShortestPath, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *      documentation</a>
   */
  public static IAST findShortestTour(final Object a1) {
    return new AST1(FindShortestTour, Object2Expr.convert(a1));
  }


  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *      documentation</a>
   */
  public static IAST findShortestTour(final Object a1, final Object a2) {
    return new AST2(FindShortestTour, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *      documentation</a>
   */
  public static IAST findShortestTour(final Object a1, final Object a2, final Object a3) {
    return new AST3(FindShortestTour, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FindSpanningTree(graph) - find the minimum spanning tree in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSpanningTree.md">FindSpanningTree
   *      documentation</a>
   */
  public static IAST findSpanningTree(final Object a1) {
    return new AST1(FindSpanningTree, Object2Expr.convert(a1));
  }


  /**
   * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a
   * set of vertices that touches all the edges in the graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexCover.md">FindVertexCover
   *      documentation</a>
   */
  public static IAST findVertexCover(final Object a1) {
    return new AST1(FindVertexCover, Object2Expr.convert(a1));
  }


  /**
   * First(expr) - returns the first element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First
   *      documentation</a>
   */
  public static IAST first(final Object a1) {
    return new AST1(First, Object2Expr.convert(a1));
  }


  /**
   * First(expr) - returns the first element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First
   *      documentation</a>
   */
  public static IAST first(final Object a1, final Object a2) {
    return new AST2(First, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *      documentation</a>
   */
  public static IAST firstCase(final Object a1) {
    return new AST1(FirstCase, Object2Expr.convert(a1));
  }


  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *      documentation</a>
   */
  public static IAST firstCase(final Object a1, final Object a2) {
    return new AST2(FirstCase, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *      documentation</a>
   */
  public static IAST firstCase(final Object a1, final Object a2, final Object a3) {
    return new AST3(FirstCase, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition
   *      documentation</a>
   */
  public static IAST firstPosition(final Object a1, final Object a2) {
    return new AST2(FirstPosition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition
   *      documentation</a>
   */
  public static IAST firstPosition(final Object a1, final Object a2, final Object a3) {
    return new AST3(FirstPosition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit
   *      documentation</a>
   */
  public static IAST fit(final Object a1, final Object a2, final Object a3) {
    return new AST3(Fit, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that
   * provide information about a `dataset`. It consists of the five most important sample
   * percentiles:
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiveNum.md">FiveNum
   *      documentation</a>
   */
  public static IAST fiveNum(final Object a1) {
    return new AST1(FiveNum, Object2Expr.convert(a1));
  }


  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint
   *      documentation</a>
   */
  public static IAST fixedPoint(final Object a1, final Object a2) {
    return new AST2(FixedPoint, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint
   *      documentation</a>
   */
  public static IAST fixedPoint(final Object a1, final Object a2, final Object a3) {
    return new AST3(FixedPoint, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList
   *      documentation</a>
   */
  public static IAST fixedPointList(final Object a1, final Object a2) {
    return new AST2(FixedPointList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList
   *      documentation</a>
   */
  public static IAST fixedPointList(final Object a1, final Object a2, final Object a3) {
    return new AST3(FixedPointList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *      documentation</a>
   */
  public static IAST flatten(final Object a1) {
    return new AST1(Flatten, Object2Expr.convert(a1));
  }


  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *      documentation</a>
   */
  public static IAST flatten(final Object a1, final Object a2) {
    return new AST2(Flatten, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *      documentation</a>
   */
  public static IAST flatten(final Object a1, final Object a2, final Object a3) {
    return new AST3(Flatten, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FlattenAt.md">FlattenAt
   *      documentation</a>
   */
  public static IAST flattenAt(final Object a1, final Object a2) {
    return new AST2(FlattenAt, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST flatTopWindow(final Object a1) {
    return new AST1(FlatTopWindow, Object2Expr.convert(a1));
  }


  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor
   *      documentation</a>
   */
  public static IAST floor(final Object a1) {
    return new AST1(Floor, Object2Expr.convert(a1));
  }


  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor
   *      documentation</a>
   */
  public static IAST floor(final Object a1, final Object a2) {
    return new AST2(Floor, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold
   *      documentation</a>
   */
  public static IAST fold(final Object a1, final Object a2) {
    return new AST2(Fold, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold
   *      documentation</a>
   */
  public static IAST fold(final Object a1, final Object a2, final Object a3) {
    return new AST3(Fold, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList
   *      documentation</a>
   */
  public static IAST foldList(final Object a1, final Object a2) {
    return new AST2(FoldList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList
   *      documentation</a>
   */
  public static IAST foldList(final Object a1, final Object a2, final Object a3) {
    return new AST3(FoldList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as
   * long as test evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/For.md">For
   *      documentation</a>
   */
  public static IAST $for(final Object a1, final Object a2, final Object a3) {
    return new AST3(For, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  public static IAST forAll(final Object a1, final Object a2) {
    return new AST2(ForAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST forAll(final Object a1, final Object a2, final Object a3) {
    return new AST3(ForAll, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fourier.md">Fourier
   *      documentation</a>
   */
  public static IAST fourier(final Object a1) {
    return new AST1(Fourier, Object2Expr.convert(a1));
  }


  /**
   * FourierDCTMatrix(n) - gives a discrete cosine transform matrix with the dimension `(n,n)` and
   * method `DCT-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDCTMatrix.md">FourierDCTMatrix
   *      documentation</a>
   */
  public static IAST fourierDCTMatrix(final Object a1) {
    return new AST1(FourierDCTMatrix, Object2Expr.convert(a1));
  }


  /**
   * FourierDCTMatrix(n) - gives a discrete cosine transform matrix with the dimension `(n,n)` and
   * method `DCT-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDCTMatrix.md">FourierDCTMatrix
   *      documentation</a>
   */
  public static IAST fourierDCTMatrix(final Object a1, final Object a2) {
    return new AST2(FourierDCTMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FourierDSTMatrix(n) - gives a discrete sine transform matrix with the dimension `(n,n)` and
   * method `DST-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDSTMatrix.md">FourierDSTMatrix
   *      documentation</a>
   */
  public static IAST fourierDSTMatrix(final Object a1) {
    return new AST1(FourierDSTMatrix, Object2Expr.convert(a1));
  }


  /**
   * FourierDSTMatrix(n) - gives a discrete sine transform matrix with the dimension `(n,n)` and
   * method `DST-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDSTMatrix.md">FourierDSTMatrix
   *      documentation</a>
   */
  public static IAST fourierDSTMatrix(final Object a1, final Object a2) {
    return new AST2(FourierDSTMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FourierMatrix(n) - gives a fourier matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierMatrix.md">FourierMatrix
   *      documentation</a>
   */
  public static IAST fourierMatrix(final Object a1) {
    return new AST1(FourierMatrix, Object2Expr.convert(a1));
  }


  /**
   * FractionalPart(number) - get the fractional part of a `number`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FractionalPart.md">FractionalPart
   *      documentation</a>
   */
  public static IAST fractionalPart(final Object a1) {
    return new AST1(FractionalPart, Object2Expr.convert(a1));
  }


  /**
   * FrechetDistribution(a,b) - returns a Frechet distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrechetDistribution.md">FrechetDistribution
   *      documentation</a>
   */
  public static IAST frechetDistribution(final Object a1, final Object a2) {
    return new AST2(FrechetDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *      documentation</a>
   */
  public static IAST freeQ(final Object a1) {
    return new AST1(FreeQ, Object2Expr.convert(a1));
  }


  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *      documentation</a>
   */
  public static IAST freeQ(final Object a1, final Object a2) {
    return new AST2(FreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST fresnelC(final Object a1) {
    return new AST1(FresnelC, Object2Expr.convert(a1));
  }


  public static IAST fresnelS(final Object a1) {
    return new AST1(FresnelS, Object2Expr.convert(a1));
  }


  /**
   * FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1,
   * ... ,aN}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusNumber.md">FrobeniusNumber
   *      documentation</a>
   */
  public static IAST frobeniusNumber(final Object a1) {
    return new AST1(FrobeniusNumber, Object2Expr.convert(a1));
  }


  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve
   *      documentation</a>
   */
  public static IAST frobeniusSolve(final Object a1, final Object a2) {
    return new AST2(FrobeniusSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve
   *      documentation</a>
   */
  public static IAST frobeniusSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(FrobeniusSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string
   * of corresponding characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromCharacterCode.md">FromCharacterCode
   *      documentation</a>
   */
  public static IAST fromCharacterCode(final Object a1) {
    return new AST1(FromCharacterCode, Object2Expr.convert(a1));
  }


  /**
   * FromContinuedFraction({n1, n2, ...}) - reconstructs a number from the list of its continued
   * fraction terms `{n1, n2, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromContinuedFraction.md">FromContinuedFraction
   *      documentation</a>
   */
  public static IAST fromContinuedFraction(final Object a1) {
    return new AST1(FromContinuedFraction, Object2Expr.convert(a1));
  }


  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *      documentation</a>
   */
  public static IAST fromDigits(final Object a1) {
    return new AST1(FromDigits, Object2Expr.convert(a1));
  }


  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *      documentation</a>
   */
  public static IAST fromDigits(final Object a1, final Object a2) {
    return new AST2(FromDigits, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FromLetterNumber(number) - get the corresponding characters from the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLetterNumber.md">FromLetterNumber
   *      documentation</a>
   */
  public static IAST fromLetterNumber(final Object a1) {
    return new AST1(FromLetterNumber, Object2Expr.convert(a1));
  }


  /**
   * FromLetterNumber(number) - get the corresponding characters from the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLetterNumber.md">FromLetterNumber
   *      documentation</a>
   */
  public static IAST fromLetterNumber(final Object a1, final Object a2) {
    return new AST2(FromLetterNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r,
   * t}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromPolarCoordinates.md">FromPolarCoordinates
   *      documentation</a>
   */
  public static IAST fromPolarCoordinates(final Object a1) {
    return new AST1(FromPolarCoordinates, Object2Expr.convert(a1));
  }


  /**
   * FromRomanNumeral(roman-number-string) - converts the given `roman-number-string` to an integer
   * number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromRomanNumeral.md">FromRomanNumeral
   *      documentation</a>
   */
  public static IAST fromRomanNumeral(final Object a1) {
    return new AST1(FromRomanNumeral, Object2Expr.convert(a1));
  }


  /**
   * FromSphericalCoordinates({r, t, p}) - returns the cartesian coordinates for the spherical
   * coordinates `{r, t, p}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromSphericalCoordinates.md">FromSphericalCoordinates
   *      documentation</a>
   */
  public static IAST fromSphericalCoordinates(final Object a1) {
    return new AST1(FromSphericalCoordinates, Object2Expr.convert(a1));
  }


  /**
   * FullForm(expression) - shows the internal representation of the given `expression`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullForm.md">FullForm
   *      documentation</a>
   */
  public static IAST fullForm(final Object a1) {
    return new AST1(FullForm, Object2Expr.convert(a1));
  }


  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify
   *      documentation</a>
   */
  public static IAST fullSimplify(final Object a1) {
    return new AST1(FullSimplify, Object2Expr.convert(a1));
  }


  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify
   *      documentation</a>
   */
  public static IAST fullSimplify(final Object a1, final Object a2) {
    return new AST2(FullSimplify, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST functionDomain(final Object a1, final Object a2) {
    return new AST2(FunctionDomain, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand
   *      documentation</a>
   */
  public static IAST functionExpand(final Object a1) {
    return new AST1(FunctionExpand, Object2Expr.convert(a1));
  }


  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand
   *      documentation</a>
   */
  public static IAST functionExpand(final Object a1, final Object a2) {
    return new AST2(FunctionExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST functionRange(final Object a1, final Object a2, final Object a3) {
    return new AST3(FunctionRange, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation
   * in the [Symja GitHub repository](https://github.com/axkr/symja_android_library).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionURL.md">FunctionURL
   *      documentation</a>
   */
  public static IAST functionURL(final Object a1) {
    return new AST1(FunctionURL, Object2Expr.convert(a1));
  }


  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *      documentation</a>
   */
  public static IAST gamma(final Object a1) {
    return new AST1(Gamma, Object2Expr.convert(a1));
  }


  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *      documentation</a>
   */
  public static IAST gamma(final Object a1, final Object a2) {
    return new AST2(Gamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *      documentation</a>
   */
  public static IAST gamma(final Object a1, final Object a2, final Object a3) {
    return new AST3(Gamma, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * GammaDistribution(a,b) - returns a gamma distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution
   *      documentation</a>
   */
  public static IAST gammaDistribution(final Object a1, final Object a2) {
    return new AST2(GammaDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GammaDistribution(a,b) - returns a gamma distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution
   *      documentation</a>
   */
  public static IAST gammaDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(GammaDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST gammaRegularized(final Object a1, final Object a2) {
    return new AST2(GammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST gammaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(GammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather
   *      documentation</a>
   */
  public static IAST gather(final Object a1) {
    return new AST1(Gather, Object2Expr.convert(a1));
  }


  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather
   *      documentation</a>
   */
  public static IAST gather(final Object a1, final Object a2) {
    return new AST2(Gather, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy
   *      documentation</a>
   */
  public static IAST gatherBy(final Object a1) {
    return new AST1(GatherBy, Object2Expr.convert(a1));
  }


  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy
   *      documentation</a>
   */
  public static IAST gatherBy(final Object a1, final Object a2) {
    return new AST2(GatherBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST gaussianWindow(final Object a1) {
    return new AST1(GaussianWindow, Object2Expr.convert(a1));
  }


  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC
   *      documentation</a>
   */
  public static IAST gegenbauerC(final Object a1, final Object a2) {
    return new AST2(GegenbauerC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC
   *      documentation</a>
   */
  public static IAST gegenbauerC(final Object a1, final Object a2, final Object a3) {
    return new AST3(GegenbauerC, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance
   *      documentation</a>
   */
  public static IAST geoDistance(final Object a1, final Object a2) {
    return new AST2(GeoDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GeometricDistribution(p) - returns a geometric distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricDistribution.md">GeometricDistribution
   *      documentation</a>
   */
  public static IAST geometricDistribution(final Object a1) {
    return new AST1(GeometricDistribution, Object2Expr.convert(a1));
  }


  /**
   * GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricMean.md">GeometricMean
   *      documentation</a>
   */
  public static IAST geometricMean(final Object a1) {
    return new AST1(GeometricMean, Object2Expr.convert(a1));
  }


  public static IAST geoPosition(final Object a1) {
    return new AST1(GeoPosition, Object2Expr.convert(a1));
  }


  /**
   * Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Get.md">Get
   *      documentation</a>
   */
  public static IAST get(final Object a1) {
    return new AST1(Get, Object2Expr.convert(a1));
  }


  public static IAST gompertzMakehamDistribution(final Object a1, final Object a2) {
    return new AST2(GompertzMakehamDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Grad(function, list-of-variables) - gives the gradient of the function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grad.md">Grad
   *      documentation</a>
   */
  public static IAST grad(final Object a1, final Object a2) {
    return new AST2(Grad, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public static IAST graph(final Object a1) {
    return new AST1(Graph, Object2Expr.convert(a1));
  }


  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public static IAST graph(final Object a1, final Object a2) {
    return new AST2(Graph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public static IAST graph(final Object a1, final Object a2, final Object a3) {
    return new AST3(Graph, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices
   * of graph eccentricity equal to the `graph` radius.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphCenter.md">GraphCenter
   *      documentation</a>
   */
  public static IAST graphCenter(final Object a1) {
    return new AST1(GraphCenter, Object2Expr.convert(a1));
  }


  /**
   * GraphComplement(graph) - returns the graph complement of `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphComplement.md">GraphComplement
   *      documentation</a>
   */
  public static IAST graphComplement(final Object a1) {
    return new AST1(GraphComplement, Object2Expr.convert(a1));
  }


  public static IAST graphData(final Object a1) {
    return new AST1(GraphData, Object2Expr.convert(a1));
  }


  /**
   * GraphDiameter(graph) - return the diameter of the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDiameter.md">GraphDiameter
   *      documentation</a>
   */
  public static IAST graphDiameter(final Object a1) {
    return new AST1(GraphDiameter, Object2Expr.convert(a1));
  }


  /**
   * GraphDifference(graph1, graph2) - returns the graph difference of `graph1`, `graph2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDifference.md">GraphDifference
   *      documentation</a>
   */
  public static IAST graphDifference(final Object a1, final Object a2) {
    return new AST2(GraphDifference, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GraphDisjointUnion(graph1, graph2, graph3,...) - returns the disjoint graph union of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDisjointUnion.md">GraphDisjointUnion
   *      documentation</a>
   */
  public static IAST graphDisjointUnion(final Object a1, final Object a2) {
    return new AST2(GraphDisjointUnion, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GraphDisjointUnion(graph1, graph2, graph3,...) - returns the disjoint graph union of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDisjointUnion.md">GraphDisjointUnion
   *      documentation</a>
   */
  public static IAST graphDisjointUnion(final Object a1, final Object a2, final Object a3) {
    return new AST3(GraphDisjointUnion, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST graphics3DJSON(final Object a1) {
    return new AST1(Graphics3DJSON, Object2Expr.convert(a1));
  }


  public static IAST graphicsComplex(final Object a1, final Object a2) {
    return new AST2(GraphicsComplex, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST graphicsJSON(final Object a1) {
    return new AST1(GraphicsJSON, Object2Expr.convert(a1));
  }


  /**
   * GraphIntersection(graph1, graph2, graph3,...) - returns the graph intersection of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphIntersection.md">GraphIntersection
   *      documentation</a>
   */
  public static IAST graphIntersection(final Object a1, final Object a2) {
    return new AST2(GraphIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GraphIntersection(graph1, graph2, graph3,...) - returns the graph intersection of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphIntersection.md">GraphIntersection
   *      documentation</a>
   */
  public static IAST graphIntersection(final Object a1, final Object a2, final Object a3) {
    return new AST3(GraphIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of
   * vertices of graph eccentricity equal to the graph diameter.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPeriphery.md">GraphPeriphery
   *      documentation</a>
   */
  public static IAST graphPeriphery(final Object a1) {
    return new AST1(GraphPeriphery, Object2Expr.convert(a1));
  }


  /**
   * GraphQ(expr) - test if `expr` is a graph object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphQ.md">GraphQ
   *      documentation</a>
   */
  public static IAST graphQ(final Object a1) {
    return new AST1(GraphQ, Object2Expr.convert(a1));
  }


  /**
   * GraphRadius(graph) - return the radius of the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphRadius.md">GraphRadius
   *      documentation</a>
   */
  public static IAST graphRadius(final Object a1) {
    return new AST1(GraphRadius, Object2Expr.convert(a1));
  }


  /**
   * GraphUnion(graph1, graph2, graph3,...) - returns the graph union of `graph1`, `graph2`,
   * `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphUnion.md">GraphUnion
   *      documentation</a>
   */
  public static IAST graphUnion(final Object a1, final Object a2) {
    return new AST2(GraphUnion, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GraphUnion(graph1, graph2, graph3,...) - returns the graph union of `graph1`, `graph2`,
   * `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphUnion.md">GraphUnion
   *      documentation</a>
   */
  public static IAST graphUnion(final Object a1, final Object a2, final Object a3) {
    return new AST3(GraphUnion, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *      documentation</a>
   */
  public static IAST greater(final Object a1) {
    return new AST1(Greater, Object2Expr.convert(a1));
  }


  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *      documentation</a>
   */
  public static IAST greater(final Object a1, final Object a2) {
    return new AST2(Greater, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *      documentation</a>
   */
  public static IAST greater(final Object a1, final Object a2, final Object a3) {
    return new AST3(Greater, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *      documentation</a>
   */
  public static IAST greaterEqual(final Object a1) {
    return new AST1(GreaterEqual, Object2Expr.convert(a1));
  }


  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *      documentation</a>
   */
  public static IAST greaterEqual(final Object a1, final Object a2) {
    return new AST2(GreaterEqual, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *      documentation</a>
   */
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


  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *      documentation</a>
   */
  public static IAST groupBy(final Object a1) {
    return new AST1(GroupBy, Object2Expr.convert(a1));
  }


  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *      documentation</a>
   */
  public static IAST groupBy(final Object a1, final Object a2) {
    return new AST2(GroupBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *      documentation</a>
   */
  public static IAST groupBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(GroupBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Gudermannian(expr) - computes the gudermannian function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gudermannian.md">Gudermannian
   *      documentation</a>
   */
  public static IAST gudermannian(final Object a1) {
    return new AST1(Gudermannian, Object2Expr.convert(a1));
  }


  /**
   * HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HamiltonianGraphQ.md">HamiltonianGraphQ
   *      documentation</a>
   */
  public static IAST hamiltonianGraphQ(final Object a1) {
    return new AST1(HamiltonianGraphQ, Object2Expr.convert(a1));
  }


  /**
   * HammingDistance(a, b) - returns the Hamming distance of `a` and `b`, i.e. the number of
   * different elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HammingDistance.md">HammingDistance
   *      documentation</a>
   */
  public static IAST hammingDistance(final Object a1, final Object a2) {
    return new AST2(HammingDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST hammingWindow(final Object a1) {
    return new AST1(HammingWindow, Object2Expr.convert(a1));
  }


  public static IAST hankelH1(final Object a1, final Object a2) {
    return new AST2(HankelH1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST hankelH2(final Object a1, final Object a2) {
    return new AST2(HankelH2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST hannWindow(final Object a1) {
    return new AST1(HannWindow, Object2Expr.convert(a1));
  }


  /**
   * HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicMean.md">HarmonicMean
   *      documentation</a>
   */
  public static IAST harmonicMean(final Object a1) {
    return new AST1(HarmonicMean, Object2Expr.convert(a1));
  }


  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber
   *      documentation</a>
   */
  public static IAST harmonicNumber(final Object a1) {
    return new AST1(HarmonicNumber, Object2Expr.convert(a1));
  }


  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber
   *      documentation</a>
   */
  public static IAST harmonicNumber(final Object a1, final Object a2) {
    return new AST2(HarmonicNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Haversine(z) - returns the haversine function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine
   *      documentation</a>
   */
  public static IAST haversine(final Object a1) {
    return new AST1(Haversine, Object2Expr.convert(a1));
  }


  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *      documentation</a>
   */
  public static IAST head(final Object a1) {
    return new AST1(Head, Object2Expr.convert(a1));
  }


  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *      documentation</a>
   */
  public static IAST head(final Object a1, final Object a2) {
    return new AST2(Head, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteH.md">HermiteH
   *      documentation</a>
   */
  public static IAST hermiteH(final Object a1, final Object a2) {
    return new AST2(HermiteH, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ
   *      documentation</a>
   */
  public static IAST hermitianMatrixQ(final Object a1) {
    return new AST1(HermitianMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ
   *      documentation</a>
   */
  public static IAST hermitianMatrixQ(final Object a1, final Object a2) {
    return new AST2(HermitianMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * HessenbergDecomposition(matrix) - calculate the Hessenberg-decomposition as a list `{p, h}` of
   * a square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HessenbergDecomposition.md">HessenbergDecomposition
   *      documentation</a>
   */
  public static IAST hessenbergDecomposition(final Object a1) {
    return new AST1(HessenbergDecomposition, Object2Expr.convert(a1));
  }


  /**
   * HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HilbertMatrix.md">HilbertMatrix
   *      documentation</a>
   */
  public static IAST hilbertMatrix(final Object a1) {
    return new AST1(HilbertMatrix, Object2Expr.convert(a1));
  }


  public static IAST hodgeDual(final Object a1) {
    return new AST1(HodgeDual, Object2Expr.convert(a1));
  }


  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *      documentation</a>
   */
  public static IAST hold(final Object a1) {
    return new AST1(Hold, Object2Expr.convert(a1));
  }


  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *      documentation</a>
   */
  public static IAST hold(final Object a1, final Object a2) {
    return new AST2(Hold, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *      documentation</a>
   */
  public static IAST hold(final Object a1, final Object a2, final Object a3) {
    return new AST3(Hold, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldPattern.md">HoldPattern
   *      documentation</a>
   */
  public static IAST holdPattern(final Object a1) {
    return new AST1(HoldPattern, Object2Expr.convert(a1));
  }


  public static IAST horner(final Object a1) {
    return new AST1(Horner, Object2Expr.convert(a1));
  }


  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm
   *      documentation</a>
   */
  public static IAST hornerForm(final Object a1) {
    return new AST1(HornerForm, Object2Expr.convert(a1));
  }


  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm
   *      documentation</a>
   */
  public static IAST hornerForm(final Object a1, final Object a2) {
    return new AST2(HornerForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * HurwitzLerchPhi(z, s, a) - returns the Lerch transcendent function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzLerchPhi.md">HurwitzLerchPhi
   *      documentation</a>
   */
  public static IAST hurwitzLerchPhi(final Object a1, final Object a2, final Object a3) {
    return new AST3(HurwitzLerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * HurwitzZeta(s, a) - returns the Hurwitz zeta function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzZeta.md">HurwitzZeta
   *      documentation</a>
   */
  public static IAST hurwitzZeta(final Object a1, final Object a2) {
    return new AST2(HurwitzZeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST hypercubeGraph(final Object a1) {
    return new AST1(HypercubeGraph, Object2Expr.convert(a1));
  }


  public static IAST hyperfactorial(final Object a1) {
    return new AST1(Hyperfactorial, Object2Expr.convert(a1));
  }


  /**
   * Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric0F1.md">Hypergeometric0F1
   *      documentation</a>
   */
  public static IAST hypergeometric0F1(final Object a1, final Object a2) {
    return new AST2(Hypergeometric0F1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric1F1.md">Hypergeometric1F1
   *      documentation</a>
   */
  public static IAST hypergeometric1F1(final Object a1, final Object a2, final Object a3) {
    return new AST3(Hypergeometric1F1, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricDistribution.md">HypergeometricDistribution
   *      documentation</a>
   */
  public static IAST hypergeometricDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(HypergeometricDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricPFQ.md">HypergeometricPFQ
   *      documentation</a>
   */
  public static IAST hypergeometricPFQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(HypergeometricPFQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST hypergeometricPFQRegularized(final Object a1, final Object a2,
      final Object a3) {
    return new AST3(HypergeometricPFQRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
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


  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *      documentation</a>
   */
  public static IAST identity(final Object a1) {
    return new AST1(Identity, Object2Expr.convert(a1));
  }


  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *      documentation</a>
   */
  public static IAST identity(final Object a1, final Object a2) {
    return new AST2(Identity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *      documentation</a>
   */
  public static IAST identity(final Object a1, final Object a2, final Object a3) {
    return new AST3(Identity, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix
   *      documentation</a>
   */
  public static IAST identityMatrix(final Object a1) {
    return new AST1(IdentityMatrix, Object2Expr.convert(a1));
  }


  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix
   *      documentation</a>
   */
  public static IAST identityMatrix(final Object a1, final Object a2) {
    return new AST2(IdentityMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If
   *      documentation</a>
   */
  public static IAST $if(final Object a1, final Object a2) {
    return new AST2(If, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If
   *      documentation</a>
   */
  public static IAST $if(final Object a1, final Object a2, final Object a3) {
    return new AST3(If, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * Im(z) - returns the imaginary component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Im.md">Im
   *      documentation</a>
   */
  public static IAST im(final Object a1) {
    return new AST1(Im, Object2Expr.convert(a1));
  }


  public static IAST image(final Object a1) {
    return new AST1(Image, Object2Expr.convert(a1));
  }


  public static IAST image(final Object a1, final Object a2) {
    return new AST2(Image, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST imageCrop(final Object a1) {
    return new AST1(ImageCrop, Object2Expr.convert(a1));
  }


  public static IAST imageCrop(final Object a1, final Object a2) {
    return new AST2(ImageCrop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Implies(arg1, arg2) - Logical implication.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Implies.md">Implies
   *      documentation</a>
   */
  public static IAST implies(final Object a1, final Object a2) {
    return new AST2(Implies, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import
   *      documentation</a>
   */
  public static IAST $import(final Object a1) {
    return new AST1(Import, Object2Expr.convert(a1));
  }


  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import
   *      documentation</a>
   */
  public static IAST $import(final Object a1, final Object a2) {
    return new AST2(Import, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST importString(final Object a1) {
    return new AST1(ImportString, Object2Expr.convert(a1));
  }


  public static IAST importString(final Object a1, final Object a2) {
    return new AST2(ImportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * In(k) - gives the `k`th line of input.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/In.md">In
   *      documentation</a>
   */
  public static IAST in(final Object a1) {
    return new AST1(In, Object2Expr.convert(a1));
  }


  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment
   *      documentation</a>
   */
  public static IAST increment(final Object a1) {
    return new AST1(Increment, Object2Expr.convert(a1));
  }


  public static IAST indexGraph(final Object a1) {
    return new AST1(IndexGraph, Object2Expr.convert(a1));
  }


  public static IAST indexGraph(final Object a1, final Object a2) {
    return new AST2(IndexGraph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InexactNumberQ.md">InexactNumberQ
   *      documentation</a>
   */
  public static IAST inexactNumberQ(final Object a1) {
    return new AST1(InexactNumberQ, Object2Expr.convert(a1));
  }


  public static IAST infix(final Object a1) {
    return new AST1(Infix, Object2Expr.convert(a1));
  }


  public static IAST infix(final Object a1, final Object a2) {
    return new AST2(Infix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication
   * function `f` and an addition function `g`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inner.md">Inner
   *      documentation</a>
   */
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


  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *      documentation</a>
   */
  public static IAST insert(final Object a1) {
    return new AST1(Insert, Object2Expr.convert(a1));
  }


  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *      documentation</a>
   */
  public static IAST insert(final Object a1, final Object a2) {
    return new AST2(Insert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *      documentation</a>
   */
  public static IAST insert(final Object a1, final Object a2, final Object a3) {
    return new AST3(Insert, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * InstanceOf[java-object, "class-name"] - return the result of the Java expression `java-object
   * instanceof class`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InstanceOf.md">InstanceOf
   *      documentation</a>
   */
  public static IAST $instanceof(final Object a1, final Object a2) {
    return new AST2(InstanceOf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *      documentation</a>
   */
  public static IAST integerDigits(final Object a1) {
    return new AST1(IntegerDigits, Object2Expr.convert(a1));
  }


  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *      documentation</a>
   */
  public static IAST integerDigits(final Object a1, final Object a2) {
    return new AST2(IntegerDigits, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *      documentation</a>
   */
  public static IAST integerDigits(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntegerDigits, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent
   *      documentation</a>
   */
  public static IAST integerExponent(final Object a1) {
    return new AST1(IntegerExponent, Object2Expr.convert(a1));
  }


  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent
   *      documentation</a>
   */
  public static IAST integerExponent(final Object a1, final Object a2) {
    return new AST2(IntegerExponent, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength
   *      documentation</a>
   */
  public static IAST integerLength(final Object a1) {
    return new AST1(IntegerLength, Object2Expr.convert(a1));
  }


  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength
   *      documentation</a>
   */
  public static IAST integerLength(final Object a1, final Object a2) {
    return new AST2(IntegerLength, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName
   *      documentation</a>
   */
  public static IAST integerName(final Object a1) {
    return new AST1(IntegerName, Object2Expr.convert(a1));
  }


  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName
   *      documentation</a>
   */
  public static IAST integerName(final Object a1, final Object a2) {
    return new AST2(IntegerName, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerPart(expr) - for real `expr` return the integer part of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPart.md">IntegerPart
   *      documentation</a>
   */
  public static IAST integerPart(final Object a1) {
    return new AST1(IntegerPart, Object2Expr.convert(a1));
  }


  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *      documentation</a>
   */
  public static IAST integerPartitions(final Object a1) {
    return new AST1(IntegerPartitions, Object2Expr.convert(a1));
  }


  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *      documentation</a>
   */
  public static IAST integerPartitions(final Object a1, final Object a2) {
    return new AST2(IntegerPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *      documentation</a>
   */
  public static IAST integerPartitions(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntegerPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerQ.md">IntegerQ
   *      documentation</a>
   */
  public static IAST integerQ(final Object a1) {
    return new AST1(IntegerQ, Object2Expr.convert(a1));
  }


  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *      documentation</a>
   */
  public static IAST interpolatingFunction(final Object a1) {
    return new AST1(InterpolatingFunction, Object2Expr.convert(a1));
  }


  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *      documentation</a>
   */
  public static IAST interpolatingFunction(final Object a1, final Object a2) {
    return new AST2(InterpolatingFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *      documentation</a>
   */
  public static IAST interpolatingFunction(final Object a1, final Object a2, final Object a3) {
    return new AST3(InterpolatingFunction, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given
   * `data-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingPolynomial.md">InterpolatingPolynomial
   *      documentation</a>
   */
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


  /**
   * InterquartileRange(list) - returns the interquartile range (IQR), which is between upper and
   * lower quartiles, IQR = Q3 − Q1.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterquartileRange.md">InterquartileRange
   *      documentation</a>
   */
  public static IAST interquartileRange(final Object a1) {
    return new AST1(InterquartileRange, Object2Expr.convert(a1));
  }


  public static IAST intersectingQ(final Object a1, final Object a2) {
    return new AST2(IntersectingQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntervalComplement(interval_1, interval_2) - compute the complement of the intervals
   * `interval_1 \ interval_2`. The intervals must be of structure `IntervalData` (closed/opened
   * ends of interval) and not of structure `Interval` (only closed ends)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalComplement.md">IntervalComplement
   *      documentation</a>
   */
  public static IAST intervalComplement(final Object a1, final Object a2) {
    return new AST2(IntervalComplement, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *      documentation</a>
   */
  public static IAST intervalIntersection(final Object a1) {
    return new AST1(IntervalIntersection, Object2Expr.convert(a1));
  }


  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *      documentation</a>
   */
  public static IAST intervalIntersection(final Object a1, final Object a2) {
    return new AST2(IntervalIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *      documentation</a>
   */
  public static IAST intervalIntersection(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntervalIntersection, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * IntervalMemberQ(interval, intervalOrRealNumber) - returns `True`, if
   * `intervalOrRealNumber` is completly sourrounded by `interval`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMemberQ.md">IntervalMemberQ
   *      documentation</a>
   */
  public static IAST intervalMemberQ(final Object a1, final Object a2) {
    return new AST2(IntervalMemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *      documentation</a>
   */
  public static IAST intervalUnion(final Object a1) {
    return new AST1(IntervalUnion, Object2Expr.convert(a1));
  }


  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *      documentation</a>
   */
  public static IAST intervalUnion(final Object a1, final Object a2) {
    return new AST2(IntervalUnion, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *      documentation</a>
   */
  public static IAST intervalUnion(final Object a1, final Object a2, final Object a3) {
    return new AST3(IntervalUnion, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Inverse(matrix) - computes the inverse of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse
   *      documentation</a>
   */
  public static IAST inverse(final Object a1) {
    return new AST1(Inverse, Object2Expr.convert(a1));
  }


  /**
   * Inverse(matrix) - computes the inverse of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse
   *      documentation</a>
   */
  public static IAST inverse(final Object a1, final Object a2) {
    return new AST2(Inverse, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseBetaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseBetaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * InverseErf(z) - returns the inverse error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErf.md">InverseErf
   *      documentation</a>
   */
  public static IAST inverseErf(final Object a1) {
    return new AST1(InverseErf, Object2Expr.convert(a1));
  }


  /**
   * InverseErfc(z) - returns the inverse complementary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErfc.md">InverseErfc
   *      documentation</a>
   */
  public static IAST inverseErfc(final Object a1) {
    return new AST1(InverseErfc, Object2Expr.convert(a1));
  }


  /**
   * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFourier.md">InverseFourier
   *      documentation</a>
   */
  public static IAST inverseFourier(final Object a1) {
    return new AST1(InverseFourier, Object2Expr.convert(a1));
  }


  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *      documentation</a>
   */
  public static IAST inverseFunction(final Object a1) {
    return new AST1(InverseFunction, Object2Expr.convert(a1));
  }


  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *      documentation</a>
   */
  public static IAST inverseFunction(final Object a1, final Object a2) {
    return new AST2(InverseFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *      documentation</a>
   */
  public static IAST inverseFunction(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseFunction, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST inverseGammaRegularized(final Object a1, final Object a2) {
    return new AST2(InverseGammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseGammaRegularized(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseGammaRegularized, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * InverseGudermannian(expr) - computes the inverse gudermannian function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseGudermannian.md">InverseGudermannian
   *      documentation</a>
   */
  public static IAST inverseGudermannian(final Object a1) {
    return new AST1(InverseGudermannian, Object2Expr.convert(a1));
  }


  /**
   * InverseHaversine(z) - returns the inverse haversine function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseHaversine.md">InverseHaversine
   *      documentation</a>
   */
  public static IAST inverseHaversine(final Object a1) {
    return new AST1(InverseHaversine, Object2Expr.convert(a1));
  }


  public static IAST inverseJacobiCD(final Object a1, final Object a2) {
    return new AST2(InverseJacobiCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseJacobiCN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiCN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseJacobiDN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiDN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseJacobiSC(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseJacobiSD(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST inverseJacobiSN(final Object a1, final Object a2) {
    return new AST2(InverseJacobiSN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseLaplaceTransform.md">InverseLaplaceTransform
   *      documentation</a>
   */
  public static IAST inverseLaplaceTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseLaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * InverseZTransform(x,z,n) - returns the inverse Z-Transform of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseZTransform.md">InverseZTransform
   *      documentation</a>
   */
  public static IAST inverseZTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(InverseZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * IsomorphicGraphQ(graph1, graph2) - returns `True` if an isomorphism exists between `graph1` and
   * `graph2`. Return `False`in all other cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IsomorphicGraphQ.md">IsomorphicGraphQ
   *      documentation</a>
   */
  public static IAST isomorphicGraphQ(final Object a1, final Object a2) {
    return new AST2(IsomorphicGraphQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is
   * `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JaccardDissimilarity.md">JaccardDissimilarity
   *      documentation</a>
   */
  public static IAST jaccardDissimilarity(final Object a1, final Object a2) {
    return new AST2(JaccardDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiAmplitude.md">JacobiAmplitude
   *      documentation</a>
   */
  public static IAST jacobiAmplitude(final Object a1, final Object a2) {
    return new AST2(JacobiAmplitude, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiCD(x, m) - returns the Jacobian elliptic function `cd(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCD.md">JacobiCD
   *      documentation</a>
   */
  public static IAST jacobiCD(final Object a1, final Object a2) {
    return new AST2(JacobiCD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCN.md">JacobiCN
   *      documentation</a>
   */
  public static IAST jacobiCN(final Object a1, final Object a2) {
    return new AST2(JacobiCN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiDN.md">JacobiDN
   *      documentation</a>
   */
  public static IAST jacobiDN(final Object a1, final Object a2) {
    return new AST2(JacobiDN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiMatrix(matrix, var) - creates a Jacobian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiMatrix.md">JacobiMatrix
   *      documentation</a>
   */
  public static IAST jacobiMatrix(final Object a1, final Object a2) {
    return new AST2(JacobiMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiSC(x, m) - returns the Jacobian elliptic function `sc(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSC.md">JacobiSC
   *      documentation</a>
   */
  public static IAST jacobiSC(final Object a1, final Object a2) {
    return new AST2(JacobiSC, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiSD(x, m) - returns the Jacobian elliptic function `sd(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSD.md">JacobiSD
   *      documentation</a>
   */
  public static IAST jacobiSD(final Object a1, final Object a2) {
    return new AST2(JacobiSD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSN.md">JacobiSN
   *      documentation</a>
   */
  public static IAST jacobiSN(final Object a1, final Object a2) {
    return new AST2(JacobiSN, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JacobiSymbol(m, n) - calculates the Jacobi symbol.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSymbol.md">JacobiSymbol
   *      documentation</a>
   */
  public static IAST jacobiSymbol(final Object a1, final Object a2) {
    return new AST2(JacobiSymbol, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST jacobiZeta(final Object a1, final Object a2) {
    return new AST2(JacobiZeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm
   *      documentation</a>
   */
  public static IAST javaForm(final Object a1) {
    return new AST1(JavaForm, Object2Expr.convert(a1));
  }


  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm
   *      documentation</a>
   */
  public static IAST javaForm(final Object a1, final Object a2) {
    return new AST2(JavaForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *      documentation</a>
   */
  public static IAST javaNew(final Object a1) {
    return new AST1(JavaNew, Object2Expr.convert(a1));
  }


  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *      documentation</a>
   */
  public static IAST javaNew(final Object a1, final Object a2) {
    return new AST2(JavaNew, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *      documentation</a>
   */
  public static IAST javaNew(final Object a1, final Object a2, final Object a3) {
    return new AST3(JavaNew, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * JavaObjectQ[java-object] - return `True` if `java-object` is a `JavaObject` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObjectQ.md">JavaObjectQ
   *      documentation</a>
   */
  public static IAST javaObjectQ(final Object a1) {
    return new AST1(JavaObjectQ, Object2Expr.convert(a1));
  }


  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *      documentation</a>
   */
  public static IAST join(final Object a1) {
    return new AST1(Join, Object2Expr.convert(a1));
  }


  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *      documentation</a>
   */
  public static IAST join(final Object a1, final Object a2) {
    return new AST2(Join, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *      documentation</a>
   */
  public static IAST join(final Object a1, final Object a2, final Object a3) {
    return new AST3(Join, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *      documentation</a>
   */
  public static IAST jSForm(final Object a1) {
    return new AST1(JSForm, Object2Expr.convert(a1));
  }


  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *      documentation</a>
   */
  public static IAST jSForm(final Object a1, final Object a2) {
    return new AST2(JSForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Key(key) - represents a `key` used to access a value in an association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Key.md">Key
   *      documentation</a>
   */
  public static IAST key(final Object a1) {
    return new AST1(Key, Object2Expr.convert(a1));
  }


  public static IAST keyExistsQ(final Object a1) {
    return new AST1(KeyExistsQ, Object2Expr.convert(a1));
  }


  public static IAST keyExistsQ(final Object a1, final Object a2) {
    return new AST2(KeyExistsQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Keys(association) - return a list of keys of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys
   *      documentation</a>
   */
  public static IAST keys(final Object a1) {
    return new AST1(Keys, Object2Expr.convert(a1));
  }


  /**
   * Keys(association) - return a list of keys of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys
   *      documentation</a>
   */
  public static IAST keys(final Object a1, final Object a2) {
    return new AST2(Keys, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect
   *      documentation</a>
   */
  public static IAST keySelect(final Object a1) {
    return new AST1(KeySelect, Object2Expr.convert(a1));
  }


  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect
   *      documentation</a>
   */
  public static IAST keySelect(final Object a1, final Object a2) {
    return new AST2(KeySelect, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort
   *      documentation</a>
   */
  public static IAST keySort(final Object a1) {
    return new AST1(KeySort, Object2Expr.convert(a1));
  }


  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort
   *      documentation</a>
   */
  public static IAST keySort(final Object a1, final Object a2) {
    return new AST2(KeySort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake
   *      documentation</a>
   */
  public static IAST keyTake(final Object a1) {
    return new AST1(KeyTake, Object2Expr.convert(a1));
  }


  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake
   *      documentation</a>
   */
  public static IAST keyTake(final Object a1, final Object a2) {
    return new AST2(KeyTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST kleinInvariantJ(final Object a1) {
    return new AST1(KleinInvariantJ, Object2Expr.convert(a1));
  }


  public static IAST kOrderlessPartitions(final Object a1, final Object a2) {
    return new AST2(KOrderlessPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST kPartitions(final Object a1, final Object a2) {
    return new AST2(KPartitions, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * KroneckerProduct(t1, t2, ...) - Kronecker product of the tensors `t1, t2, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerProduct.md">KroneckerProduct
   *      documentation</a>
   */
  public static IAST kroneckerProduct(final Object a1, final Object a2) {
    return new AST2(KroneckerProduct, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * KroneckerProduct(t1, t2, ...) - Kronecker product of the tensors `t1, t2, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerProduct.md">KroneckerProduct
   *      documentation</a>
   */
  public static IAST kroneckerProduct(final Object a1, final Object a2, final Object a3) {
    return new AST3(KroneckerProduct, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing
   * outliers).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Kurtosis.md">Kurtosis
   *      documentation</a>
   */
  public static IAST kurtosis(final Object a1) {
    return new AST1(Kurtosis, Object2Expr.convert(a1));
  }


  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *      documentation</a>
   */
  public static IAST laguerreL(final Object a1, final Object a2) {
    return new AST2(LaguerreL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *      documentation</a>
   */
  public static IAST laguerreL(final Object a1, final Object a2, final Object a3) {
    return new AST3(LaguerreL, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST lambertW(final Object a1) {
    return new AST1(LambertW, Object2Expr.convert(a1));
  }


  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform
   *      documentation</a>
   */
  public static IAST laplaceTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(LaplaceTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Last(expr) - returns the last element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last
   *      documentation</a>
   */
  public static IAST last(final Object a1) {
    return new AST1(Last, Object2Expr.convert(a1));
  }


  /**
   * Last(expr) - returns the last element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last
   *      documentation</a>
   */
  public static IAST last(final Object a1, final Object a2) {
    return new AST2(Last, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *      documentation</a>
   */
  public static IAST lcm(final Object a1) {
    return new AST1(LCM, Object2Expr.convert(a1));
  }


  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *      documentation</a>
   */
  public static IAST lcm(final Object a1, final Object a2) {
    return new AST2(LCM, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *      documentation</a>
   */
  public static IAST lcm(final Object a1, final Object a2, final Object a3) {
    return new AST3(LCM, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeafCount.md">LeafCount
   *      documentation</a>
   */
  public static IAST leafCount(final Object a1) {
    return new AST1(LeafCount, Object2Expr.convert(a1));
  }


  /**
   * LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeastSquares.md">LeastSquares
   *      documentation</a>
   */
  public static IAST leastSquares(final Object a1, final Object a2) {
    return new AST2(LeastSquares, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP
   *      documentation</a>
   */
  public static IAST legendreP(final Object a1, final Object a2) {
    return new AST2(LegendreP, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP
   *      documentation</a>
   */
  public static IAST legendreP(final Object a1, final Object a2, final Object a3) {
    return new AST3(LegendreP, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ
   *      documentation</a>
   */
  public static IAST legendreQ(final Object a1, final Object a2) {
    return new AST2(LegendreQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ
   *      documentation</a>
   */
  public static IAST legendreQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(LegendreQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Length(expr) - returns the number of leaves in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Length.md">Length
   *      documentation</a>
   */
  public static IAST length(final Object a1) {
    return new AST1(Length, Object2Expr.convert(a1));
  }


  /**
   * LengthWhile({e1, e2, ...}, head) - returns the number of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LengthWhile.md">LengthWhile
   *      documentation</a>
   */
  public static IAST lengthWhile(final Object a1, final Object a2) {
    return new AST2(LengthWhile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST lerchPhi(final Object a1, final Object a2, final Object a3) {
    return new AST3(LerchPhi, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *      documentation</a>
   */
  public static IAST less(final Object a1) {
    return new AST1(Less, Object2Expr.convert(a1));
  }


  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *      documentation</a>
   */
  public static IAST less(final Object a1, final Object a2) {
    return new AST2(Less, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *      documentation</a>
   */
  public static IAST less(final Object a1, final Object a2, final Object a3) {
    return new AST3(Less, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *      documentation</a>
   */
  public static IAST lessEqual(final Object a1) {
    return new AST1(LessEqual, Object2Expr.convert(a1));
  }


  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *      documentation</a>
   */
  public static IAST lessEqual(final Object a1, final Object a2) {
    return new AST2(LessEqual, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *      documentation</a>
   */
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


  /**
   * LetterCounts(string) - count the number of each distinct character in the `string` and return
   * the result as an association `<|char->counter1, ...|>`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCounts.md">LetterCounts
   *      documentation</a>
   */
  public static IAST letterCounts(final Object a1) {
    return new AST1(LetterCounts, Object2Expr.convert(a1));
  }


  /**
   * LetterNumber(character) - returns the position of the `character` in the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterNumber.md">LetterNumber
   *      documentation</a>
   */
  public static IAST letterNumber(final Object a1) {
    return new AST1(LetterNumber, Object2Expr.convert(a1));
  }


  /**
   * LetterNumber(character) - returns the position of the `character` in the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterNumber.md">LetterNumber
   *      documentation</a>
   */
  public static IAST letterNumber(final Object a1, final Object a2) {
    return new AST2(LetterNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LetterQ(expr) - tests whether `expr` is a string, which only contains letters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterQ.md">LetterQ
   *      documentation</a>
   */
  public static IAST letterQ(final Object a1) {
    return new AST1(LetterQ, Object2Expr.convert(a1));
  }


  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level
   *      documentation</a>
   */
  public static IAST level(final Object a1, final Object a2) {
    return new AST2(Level, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level
   *      documentation</a>
   */
  public static IAST level(final Object a1, final Object a2, final Object a3) {
    return new AST3(Level, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LevelQ(expr) - tests whether `expr` is a valid level specification.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LevelQ.md">LevelQ
   *      documentation</a>
   */
  public static IAST levelQ(final Object a1) {
    return new AST1(LevelQ, Object2Expr.convert(a1));
  }


  /**
   * LeviCivitaTensor(n) - returns the `n`-dimensional Levi-Civita tensor as sparse array. The
   * Levi-Civita symbol represents a collection of numbers; defined from the sign of a permutation
   * of the natural numbers `1, 2, …, n`, for some positive integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeviCivitaTensor.md">LeviCivitaTensor
   *      documentation</a>
   */
  public static IAST leviCivitaTensor(final Object a1) {
    return new AST1(LeviCivitaTensor, Object2Expr.convert(a1));
  }


  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit
   *      documentation</a>
   */
  public static IAST limit(final Object a1, final Object a2) {
    return new AST2(Limit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit
   *      documentation</a>
   */
  public static IAST limit(final Object a1, final Object a2, final Object a3) {
    return new AST3(Limit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST line(final Object a1) {
    return new AST1(Line, Object2Expr.convert(a1));
  }


  /**
   * LinearModelFit({{x1,y1},{x2,y2},...}, expr, symbol) - Create a linear regression model from a
   * matrix of observed value pairs `{x_i, y_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearModelFit.md">LinearModelFit
   *      documentation</a>
   */
  public static IAST linearModelFit(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearModelFit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST linearOptimization(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearOptimization, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList,
   * constraintRelationList) - the `LinearProgramming` function provides an implementation of
   * [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for
   * solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearProgramming.md">LinearProgramming
   *      documentation</a>
   */
  public static IAST linearProgramming(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearProgramming, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated
   * sequence of elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearRecurrence.md">LinearRecurrence
   *      documentation</a>
   */
  public static IAST linearRecurrence(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearRecurrence, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LinearSolve(matrix, right) - solves the linear equation system `matrix . x = right` and returns
   * one corresponding solution `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *      documentation</a>
   */
  public static IAST linearSolve(final Object a1) {
    return new AST1(LinearSolve, Object2Expr.convert(a1));
  }


  /**
   * LinearSolve(matrix, right) - solves the linear equation system `matrix . x = right` and returns
   * one corresponding solution `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *      documentation</a>
   */
  public static IAST linearSolve(final Object a1, final Object a2) {
    return new AST2(LinearSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LinearSolve(matrix, right) - solves the linear equation system `matrix . x = right` and returns
   * one corresponding solution `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *      documentation</a>
   */
  public static IAST linearSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(LinearSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST lineGraph(final Object a1) {
    return new AST1(LineGraph, Object2Expr.convert(a1));
  }


  public static IAST liouvilleLambda(final Object a1) {
    return new AST1(LiouvilleLambda, Object2Expr.convert(a1));
  }


  /**
   * ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with
   * `tensor-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListConvolve.md">ListConvolve
   *      documentation</a>
   */
  public static IAST listConvolve(final Object a1, final Object a2) {
    return new AST2(ListConvolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with
   * `tensor-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCorrelate.md">ListCorrelate
   *      documentation</a>
   */
  public static IAST listCorrelate(final Object a1, final Object a2) {
    return new AST2(ListCorrelate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST listDensityPlot(final Object a1) {
    return new AST1(ListDensityPlot, Object2Expr.convert(a1));
  }


  public static IAST listDensityPlot(final Object a1, final Object a2) {
    return new AST2(ListDensityPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST listDensityPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListDensityPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *      documentation</a>
   */
  public static IAST listLinePlot(final Object a1) {
    return new AST1(ListLinePlot, Object2Expr.convert(a1));
  }


  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *      documentation</a>
   */
  public static IAST listLinePlot(final Object a1, final Object a2) {
    return new AST2(ListLinePlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *      documentation</a>
   */
  public static IAST listLinePlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListLinePlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListLinePlot3D( { list-of-lines } ) - generate a JavaScript list plot 3D control for the
   * `list-of-lines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot3D.md">ListLinePlot3D
   *      documentation</a>
   */
  public static IAST listLinePlot3D(final Object a1) {
    return new AST1(ListLinePlot3D, Object2Expr.convert(a1));
  }


  /**
   * ListLinePlot3D( { list-of-lines } ) - generate a JavaScript list plot 3D control for the
   * `list-of-lines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot3D.md">ListLinePlot3D
   *      documentation</a>
   */
  public static IAST listLinePlot3D(final Object a1, final Object a2) {
    return new AST2(ListLinePlot3D, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListLinePlot3D( { list-of-lines } ) - generate a JavaScript list plot 3D control for the
   * `list-of-lines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot3D.md">ListLinePlot3D
   *      documentation</a>
   */
  public static IAST listLinePlot3D(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListLinePlot3D, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST listLogLinearPlot(final Object a1) {
    return new AST1(ListLogLinearPlot, Object2Expr.convert(a1));
  }


  public static IAST listLogLinearPlot(final Object a1, final Object a2) {
    return new AST2(ListLogLinearPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST listLogLinearPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListLogLinearPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListLogLogPlot( { list-of-points } ) - generate an image of a logarithmic X and logarithmic Y
   * plot for the `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogLogPlot.md">ListLogLogPlot
   *      documentation</a>
   */
  public static IAST listLogLogPlot(final Object a1) {
    return new AST1(ListLogLogPlot, Object2Expr.convert(a1));
  }


  /**
   * ListLogLogPlot( { list-of-points } ) - generate an image of a logarithmic X and logarithmic Y
   * plot for the `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogLogPlot.md">ListLogLogPlot
   *      documentation</a>
   */
  public static IAST listLogLogPlot(final Object a1, final Object a2) {
    return new AST2(ListLogLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListLogLogPlot( { list-of-points } ) - generate an image of a logarithmic X and logarithmic Y
   * plot for the `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogLogPlot.md">ListLogLogPlot
   *      documentation</a>
   */
  public static IAST listLogLogPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListLogLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListLogPlot( { list-of-points } ) - generate an image of a logarithmic Y plot for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogPlot.md">ListLogPlot
   *      documentation</a>
   */
  public static IAST listLogPlot(final Object a1) {
    return new AST1(ListLogPlot, Object2Expr.convert(a1));
  }


  /**
   * ListLogPlot( { list-of-points } ) - generate an image of a logarithmic Y plot for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogPlot.md">ListLogPlot
   *      documentation</a>
   */
  public static IAST listLogPlot(final Object a1, final Object a2) {
    return new AST2(ListLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListLogPlot( { list-of-points } ) - generate an image of a logarithmic Y plot for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogPlot.md">ListLogPlot
   *      documentation</a>
   */
  public static IAST listLogPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *      documentation</a>
   */
  public static IAST listPlot(final Object a1) {
    return new AST1(ListPlot, Object2Expr.convert(a1));
  }


  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *      documentation</a>
   */
  public static IAST listPlot(final Object a1, final Object a2) {
    return new AST2(ListPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *      documentation</a>
   */
  public static IAST listPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST listPolarPlot(final Object a1) {
    return new AST1(ListPolarPlot, Object2Expr.convert(a1));
  }


  public static IAST listPolarPlot(final Object a1, final Object a2) {
    return new AST2(ListPolarPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST listPolarPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ListPolarPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ListQ(expr) - tests whether `expr` is a `List`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListQ.md">ListQ
   *      documentation</a>
   */
  public static IAST listQ(final Object a1) {
    return new AST1(ListQ, Object2Expr.convert(a1));
  }


  public static IAST literal(final Object a1) {
    return new AST1(Literal, Object2Expr.convert(a1));
  }


  /**
   * LoadJavaClass["class-name"] - loads the class with the specified `class-name` and return a
   * `JavaClass` expression. All static method names are assigned to a context which will be created
   * by the last part of the class name.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LoadJavaClass.md">LoadJavaClass
   *      documentation</a>
   */
  public static IAST loadJavaClass(final Object a1) {
    return new AST1(LoadJavaClass, Object2Expr.convert(a1));
  }


  /**
   * Log(z) - returns the natural logarithm of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log
   *      documentation</a>
   */
  public static IAST log(final Object a1) {
    return new AST1(Log, Object2Expr.convert(a1));
  }


  /**
   * Log(z) - returns the natural logarithm of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log
   *      documentation</a>
   */
  public static IAST log(final Object a1, final Object a2) {
    return new AST2(Log, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to
   * `Log(z)/Log(10)` in symbolic mode.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log10.md">Log10
   *      documentation</a>
   */
  public static IAST log10(final Object a1) {
    return new AST1(Log10, Object2Expr.convert(a1));
  }


  /**
   * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)`
   * in symbolic mode.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log2.md">Log2
   *      documentation</a>
   */
  public static IAST log2(final Object a1) {
    return new AST1(Log2, Object2Expr.convert(a1));
  }


  /**
   * LogGamma(z) - is the logarithmic gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogGamma.md">LogGamma
   *      documentation</a>
   */
  public static IAST logGamma(final Object a1) {
    return new AST1(LogGamma, Object2Expr.convert(a1));
  }


  public static IAST logicalExpand(final Object a1) {
    return new AST1(LogicalExpand, Object2Expr.convert(a1));
  }


  /**
   * LogIntegral(expr) - returns the integral logarithm of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogIntegral.md">LogIntegral
   *      documentation</a>
   */
  public static IAST logIntegral(final Object a1) {
    return new AST1(LogIntegral, Object2Expr.convert(a1));
  }


  /**
   * LogisticSigmoid(z) - returns the logistic sigmoid of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticSigmoid.md">LogisticSigmoid
   *      documentation</a>
   */
  public static IAST logisticSigmoid(final Object a1) {
    return new AST1(LogisticSigmoid, Object2Expr.convert(a1));
  }


  public static IAST logLinearPlot(final Object a1, final Object a2) {
    return new AST2(LogLinearPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST logLinearPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(LogLinearPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST logLogPlot(final Object a1, final Object a2) {
    return new AST2(LogLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST logLogPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(LogLogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST logPlot(final Object a1, final Object a2) {
    return new AST2(LogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST logPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(LogPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *      documentation</a>
   */
  public static IAST lookup(final Object a1) {
    return new AST1(Lookup, Object2Expr.convert(a1));
  }


  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *      documentation</a>
   */
  public static IAST lookup(final Object a1, final Object a2) {
    return new AST2(Lookup, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *      documentation</a>
   */
  public static IAST lookup(final Object a1, final Object a2, final Object a3) {
    return new AST3(Lookup, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * LowerCaseQ(str) - is `True` if the given `str` is a string which only contains lower case
   * characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerCaseQ.md">LowerCaseQ
   *      documentation</a>
   */
  public static IAST lowerCaseQ(final Object a1) {
    return new AST1(LowerCaseQ, Object2Expr.convert(a1));
  }


  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize
   *      documentation</a>
   */
  public static IAST lowerTriangularize(final Object a1) {
    return new AST1(LowerTriangularize, Object2Expr.convert(a1));
  }


  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize
   *      documentation</a>
   */
  public static IAST lowerTriangularize(final Object a1, final Object a2) {
    return new AST2(LowerTriangularize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LowerTriangularMatrixQ(matrix) - returns `True` if `matrix` is lower triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularMatrixQ.md">LowerTriangularMatrixQ
   *      documentation</a>
   */
  public static IAST lowerTriangularMatrixQ(final Object a1) {
    return new AST1(LowerTriangularMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * LowerTriangularMatrixQ(matrix) - returns `True` if `matrix` is lower triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularMatrixQ.md">LowerTriangularMatrixQ
   *      documentation</a>
   */
  public static IAST lowerTriangularMatrixQ(final Object a1, final Object a2) {
    return new AST2(LowerTriangularMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LucasL(n) - gives the `n`th Lucas number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL
   *      documentation</a>
   */
  public static IAST lucasL(final Object a1) {
    return new AST1(LucasL, Object2Expr.convert(a1));
  }


  /**
   * LucasL(n) - gives the `n`th Lucas number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL
   *      documentation</a>
   */
  public static IAST lucasL(final Object a1, final Object a2) {
    return new AST2(LucasL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition
   *      documentation</a>
   */
  public static IAST lUDecomposition(final Object a1) {
    return new AST1(LUDecomposition, Object2Expr.convert(a1));
  }


  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition
   *      documentation</a>
   */
  public static IAST lUDecomposition(final Object a1, final Object a2) {
    return new AST2(LUDecomposition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ
   *      documentation</a>
   */
  public static IAST machineNumberQ(final Object a1) {
    return new AST1(MachineNumberQ, Object2Expr.convert(a1));
  }


  public static IAST makeBoxes(final Object a1) {
    return new AST1(MakeBoxes, Object2Expr.convert(a1));
  }


  public static IAST makeBoxes(final Object a1, final Object a2) {
    return new AST2(MakeBoxes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MangoldtLambda(n) - the von Mangoldt function of `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MangoldtLambda.md">MangoldtLambda
   *      documentation</a>
   */
  public static IAST mangoldtLambda(final Object a1) {
    return new AST1(MangoldtLambda, Object2Expr.convert(a1));
  }


  /**
   * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the
   * number of horizontal or vertical moves in the grid like Manhattan city layout to get from `u`
   * to `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ManhattanDistance.md">ManhattanDistance
   *      documentation</a>
   */
  public static IAST manhattanDistance(final Object a1, final Object a2) {
    return new AST2(ManhattanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *      documentation</a>
   */
  public static IAST manipulate(final Object a1) {
    return new AST1(Manipulate, Object2Expr.convert(a1));
  }


  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *      documentation</a>
   */
  public static IAST manipulate(final Object a1, final Object a2) {
    return new AST2(Manipulate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *      documentation</a>
   */
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


  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map
   *      documentation</a>
   */
  public static IAST map(final Object a1, final Object a2) {
    return new AST2(Map, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map
   *      documentation</a>
   */
  public static IAST map(final Object a1, final Object a2, final Object a3) {
    return new AST3(Map, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
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


  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed
   *      documentation</a>
   */
  public static IAST mapIndexed(final Object a1, final Object a2) {
    return new AST2(MapIndexed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed
   *      documentation</a>
   */
  public static IAST mapIndexed(final Object a1, final Object a2, final Object a3) {
    return new AST3(MapIndexed, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread
   *      documentation</a>
   */
  public static IAST mapThread(final Object a1, final Object a2) {
    return new AST2(MapThread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread
   *      documentation</a>
   */
  public static IAST mapThread(final Object a1, final Object a2, final Object a3) {
    return new AST3(MapThread, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D
   * lists `u` and `v`, which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is
   * the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchingDissimilarity.md">MatchingDissimilarity
   *      documentation</a>
   */
  public static IAST matchingDissimilarity(final Object a1, final Object a2) {
    return new AST2(MatchingDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MatchQ(expr, form) - tests whether `expr` matches `form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchQ.md">MatchQ
   *      documentation</a>
   */
  public static IAST matchQ(final Object a1, final Object a2) {
    return new AST2(MatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MathMLForm(expr) - returns the MathML form of the evaluated `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MathMLForm.md">MathMLForm
   *      documentation</a>
   */
  public static IAST mathMLForm(final Object a1) {
    return new AST1(MathMLForm, Object2Expr.convert(a1));
  }


  /**
   * MatrixD(f, X) - gives the matrix derivative of `f` with respect to the matrix `X`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixD.md">MatrixD
   *      documentation</a>
   */
  public static IAST matrixD(final Object a1, final Object a2) {
    return new AST2(MatrixD, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MatrixD(f, X) - gives the matrix derivative of `f` with respect to the matrix `X`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixD.md">MatrixD
   *      documentation</a>
   */
  public static IAST matrixD(final Object a1, final Object a2, final Object a3) {
    return new AST3(MatrixD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * MatrixExp(matrix) - computes the matrix exponential of the square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixExp.md">MatrixExp
   *      documentation</a>
   */
  public static IAST matrixExp(final Object a1) {
    return new AST1(MatrixExp, Object2Expr.convert(a1));
  }


  public static IAST matrixFunction(final Object a1, final Object a2) {
    return new AST2(MatrixFunction, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST matrixLog(final Object a1) {
    return new AST1(MatrixLog, Object2Expr.convert(a1));
  }


  /**
   * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for
   * the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixMinimalPolynomial.md">MatrixMinimalPolynomial
   *      documentation</a>
   */
  public static IAST matrixMinimalPolynomial(final Object a1, final Object a2) {
    return new AST2(MatrixMinimalPolynomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MatrixPower(matrix, n) - computes the `n`th power of a `matrix`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPower.md">MatrixPower
   *      documentation</a>
   */
  public static IAST matrixPower(final Object a1, final Object a2) {
    return new AST2(MatrixPower, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ
   *      documentation</a>
   */
  public static IAST matrixQ(final Object a1) {
    return new AST1(MatrixQ, Object2Expr.convert(a1));
  }


  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ
   *      documentation</a>
   */
  public static IAST matrixQ(final Object a1, final Object a2) {
    return new AST2(MatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MatrixRank(matrix) - returns the rank of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank
   *      documentation</a>
   */
  public static IAST matrixRank(final Object a1) {
    return new AST1(MatrixRank, Object2Expr.convert(a1));
  }


  /**
   * MatrixRank(matrix) - returns the rank of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank
   *      documentation</a>
   */
  public static IAST matrixRank(final Object a1, final Object a2) {
    return new AST2(MatrixRank, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public static IAST max(final Object a1) {
    return new AST1(Max, Object2Expr.convert(a1));
  }


  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public static IAST max(final Object a1, final Object a2) {
    return new AST2(Max, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public static IAST max(final Object a1, final Object a2, final Object a3) {
    return new AST3(Max, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter
   *      documentation</a>
   */
  public static IAST maxFilter(final Object a1, final Object a2) {
    return new AST2(MaxFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize
   *      documentation</a>
   */
  public static IAST maximize(final Object a1, final Object a2) {
    return new AST2(Maximize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Mean(list) - returns the statistical mean of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean
   *      documentation</a>
   */
  public static IAST mean(final Object a1) {
    return new AST1(Mean, Object2Expr.convert(a1));
  }


  public static IAST meanDeviation(final Object a1) {
    return new AST1(MeanDeviation, Object2Expr.convert(a1));
  }


  /**
   * MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanFilter.md">MeanFilter
   *      documentation</a>
   */
  public static IAST meanFilter(final Object a1, final Object a2) {
    return new AST2(MeanFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Median(list) - returns the median of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median
   *      documentation</a>
   */
  public static IAST median(final Object a1) {
    return new AST1(Median, Object2Expr.convert(a1));
  }


  /**
   * MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianFilter.md">MedianFilter
   *      documentation</a>
   */
  public static IAST medianFilter(final Object a1, final Object a2) {
    return new AST2(MedianFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *      documentation</a>
   */
  public static IAST memberQ(final Object a1) {
    return new AST1(MemberQ, Object2Expr.convert(a1));
  }


  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *      documentation</a>
   */
  public static IAST memberQ(final Object a1, final Object a2) {
    return new AST2(MemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *      documentation</a>
   */
  public static IAST memberQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(MemberQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST memoryInUse(final Object a1) {
    return new AST1(MemoryInUse, Object2Expr.convert(a1));
  }


  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number. Currently `0 < n <= 47` can be computed, otherwise the function returns unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent
   *      documentation</a>
   */
  public static IAST mersennePrimeExponent(final Object a1) {
    return new AST1(MersennePrimeExponent, Object2Expr.convert(a1));
  }


  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 47` can be computed in reasonable time.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ
   *      documentation</a>
   */
  public static IAST mersennePrimeExponentQ(final Object a1) {
    return new AST1(MersennePrimeExponentQ, Object2Expr.convert(a1));
  }


  /**
   * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of
   * message IDs of the form `symbol::tag`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MessageName.md">MessageName
   *      documentation</a>
   */
  public static IAST messageName(final Object a1, final Object a2) {
    return new AST2(MessageName, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Messages(symbol) - return all messages which are asociated to `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Messages.md">Messages
   *      documentation</a>
   */
  public static IAST messages(final Object a1) {
    return new AST1(Messages, Object2Expr.convert(a1));
  }


  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public static IAST min(final Object a1) {
    return new AST1(Min, Object2Expr.convert(a1));
  }


  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public static IAST min(final Object a1, final Object a2) {
    return new AST2(Min, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public static IAST min(final Object a1, final Object a2, final Object a3) {
    return new AST3(Min, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter
   *      documentation</a>
   */
  public static IAST minFilter(final Object a1, final Object a2) {
    return new AST2(MinFilter, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Minimize(unary-function, variable) - returns the minimum of the unary function for the given
   * `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minimize.md">Minimize
   *      documentation</a>
   */
  public static IAST minimize(final Object a1, final Object a2) {
    return new AST2(Minimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST minMax(final Object a1) {
    return new AST1(MinMax, Object2Expr.convert(a1));
  }


  public static IAST minMax(final Object a1, final Object a2) {
    return new AST2(MinMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Minors(matrix) - returns the minors of the matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minors.md">Minors
   *      documentation</a>
   */
  public static IAST minors(final Object a1) {
    return new AST1(Minors, Object2Expr.convert(a1));
  }


  /**
   * Minors(matrix) - returns the minors of the matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minors.md">Minors
   *      documentation</a>
   */
  public static IAST minors(final Object a1, final Object a2) {
    return new AST2(Minors, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Minus(expr) - is the negation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus
   *      documentation</a>
   */
  public static IAST minus(final Object a1) {
    return new AST1(Minus, Object2Expr.convert(a1));
  }


  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ
   *      documentation</a>
   */
  public static IAST missingQ(final Object a1) {
    return new AST1(MissingQ, Object2Expr.convert(a1));
  }


  /**
   * Mod(x, m) - returns `x` modulo `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod
   *      documentation</a>
   */
  public static IAST mod(final Object a1, final Object a2) {
    return new AST2(Mod, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Mod(x, m) - returns `x` modulo `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod
   *      documentation</a>
   */
  public static IAST mod(final Object a1, final Object a2, final Object a3) {
    return new AST3(Mod, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * ModularInverse(k, n) - returns the modular inverse `k^(-1) mod n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ModularInverse.md">ModularInverse
   *      documentation</a>
   */
  public static IAST modularInverse(final Object a1, final Object a2) {
    return new AST2(ModularInverse, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * by renaming local variables.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module
   *      documentation</a>
   */
  public static IAST module(final Object a1, final Object a2) {
    return new AST2(Module, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MoebiusMu(expr) - calculate the Möbius function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoebiusMu.md">MoebiusMu
   *      documentation</a>
   */
  public static IAST moebiusMu(final Object a1) {
    return new AST1(MoebiusMu, Object2Expr.convert(a1));
  }


  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *      documentation</a>
   */
  public static IAST monomialList(final Object a1) {
    return new AST1(MonomialList, Object2Expr.convert(a1));
  }


  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *      documentation</a>
   */
  public static IAST monomialList(final Object a1, final Object a2) {
    return new AST2(MonomialList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *      documentation</a>
   */
  public static IAST monomialList(final Object a1, final Object a2, final Object a3) {
    return new AST3(MonomialList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Most(expr) - returns `expr` with the last element removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most
   *      documentation</a>
   */
  public static IAST most(final Object a1) {
    return new AST1(Most, Object2Expr.convert(a1));
  }


  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public static IAST multinomial(final Object a1) {
    return new AST1(Multinomial, Object2Expr.convert(a1));
  }


  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public static IAST multinomial(final Object a1, final Object a2) {
    return new AST2(Multinomial, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public static IAST multinomial(final Object a1, final Object a2, final Object a3) {
    return new AST3(Multinomial, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplicativeOrder.md">MultiplicativeOrder
   *      documentation</a>
   */
  public static IAST multiplicativeOrder(final Object a1, final Object a2) {
    return new AST2(MultiplicativeOrder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * MultiplySides(compare-expr, value) - multiplies `value` with all elements of the
   * `compare-expr`. `compare-expr` can be `True`, `False` or a comparison expression with head
   * `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplySides.md">MultiplySides
   *      documentation</a>
   */
  public static IAST multiplySides(final Object a1, final Object a2) {
    return new AST2(MultiplySides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * N(expr) - gives the numerical value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N
   *      documentation</a>
   */
  public static IAST n(final Object a1) {
    return new AST1(N, Object2Expr.convert(a1));
  }


  /**
   * N(expr) - gives the numerical value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N
   *      documentation</a>
   */
  public static IAST n(final Object a1, final Object a2) {
    return new AST2(N, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST nameQ(final Object a1) {
    return new AST1(NameQ, Object2Expr.convert(a1));
  }


  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names
   *      documentation</a>
   */
  public static IAST names(final Object a1) {
    return new AST1(Names, Object2Expr.convert(a1));
  }


  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names
   *      documentation</a>
   */
  public static IAST names(final Object a1, final Object a2) {
    return new AST2(Names, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND
   *      documentation</a>
   */
  public static IAST nd(final Object a1, final Object a2, final Object a3) {
    return new AST3(ND, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * NDSolve({equation-list}, functions, t) - attempts to solve the linear differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an
   * `InterpolatingFunction` function object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolve.md">NDSolve
   *      documentation</a>
   */
  public static IAST nDSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(NDSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
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


  /**
   * Negative(x) - returns `True` if `x` is a negative real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative
   *      documentation</a>
   */
  public static IAST negative(final Object a1) {
    return new AST1(Negative, Object2Expr.convert(a1));
  }


  /**
   * Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the
   * final result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nest.md">Nest
   *      documentation</a>
   */
  public static IAST nest(final Object a1, final Object a2, final Object a3) {
    return new AST3(Nest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a
   * list of all intermediate results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestList.md">NestList
   *      documentation</a>
   */
  public static IAST nestList(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhile.md">NestWhile
   *      documentation</a>
   */
  public static IAST nestWhile(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestWhile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`. It returns a list of all intermediate
   * results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhileList.md">NestWhileList
   *      documentation</a>
   */
  public static IAST nestWhileList(final Object a1, final Object a2, final Object a3) {
    return new AST3(NestWhileList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST newLimit(final Object a1, final Object a2) {
    return new AST2(NewLimit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST newLimit(final Object a1, final Object a2, final Object a3) {
    return new AST3(NewLimit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NextPrime(n) - gives the next prime after `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime
   *      documentation</a>
   */
  public static IAST nextPrime(final Object a1) {
    return new AST1(NextPrime, Object2Expr.convert(a1));
  }


  /**
   * NextPrime(n) - gives the next prime after `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime
   *      documentation</a>
   */
  public static IAST nextPrime(final Object a1, final Object a2) {
    return new AST2(NextPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST nFourierTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(NFourierTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate
   *      documentation</a>
   */
  public static IAST nIntegrate(final Object a1, final Object a2) {
    return new AST2(NIntegrate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate
   *      documentation</a>
   */
  public static IAST nIntegrate(final Object a1, final Object a2, final Object a3) {
    return new AST3(NIntegrate, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMaximize.md">NMaximize
   *      documentation</a>
   */
  public static IAST nMaximize(final Object a1, final Object a2) {
    return new AST2(NMaximize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMinimize.md">NMinimize
   *      documentation</a>
   */
  public static IAST nMinimize(final Object a1, final Object a2) {
    return new AST2(NMinimize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoneTrue.md">NoneTrue
   *      documentation</a>
   */
  public static IAST noneTrue(final Object a1, final Object a2) {
    return new AST2(NoneTrue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NonNegative(x) - returns `True` if `x` is a positive real number or zero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonNegative.md">NonNegative
   *      documentation</a>
   */
  public static IAST nonNegative(final Object a1) {
    return new AST1(NonNegative, Object2Expr.convert(a1));
  }


  /**
   * NonPositive(x) - returns `True` if `x` is a negative real number or zero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonPositive.md">NonPositive
   *      documentation</a>
   */
  public static IAST nonPositive(final Object a1) {
    return new AST1(NonPositive, Object2Expr.convert(a1));
  }


  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm
   *      documentation</a>
   */
  public static IAST norm(final Object a1) {
    return new AST1(Norm, Object2Expr.convert(a1));
  }


  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm
   *      documentation</a>
   */
  public static IAST norm(final Object a1, final Object a2) {
    return new AST2(Norm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal
   *      documentation</a>
   */
  public static IAST normal(final Object a1) {
    return new AST1(Normal, Object2Expr.convert(a1));
  }


  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal
   *      documentation</a>
   */
  public static IAST normal(final Object a1, final Object a2) {
    return new AST2(Normal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution
   *      documentation</a>
   */
  public static IAST normalDistribution(final Object a1) {
    return new AST1(NormalDistribution, Object2Expr.convert(a1));
  }


  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution
   *      documentation</a>
   */
  public static IAST normalDistribution(final Object a1, final Object a2) {
    return new AST2(NormalDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize
   *      documentation</a>
   */
  public static IAST normalize(final Object a1) {
    return new AST1(Normalize, Object2Expr.convert(a1));
  }


  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize
   *      documentation</a>
   */
  public static IAST normalize(final Object a1, final Object a2) {
    return new AST2(Normalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST normalMatrixQ(final Object a1) {
    return new AST1(NormalMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`.
   * Returns `False` if the `expr` is `True`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not
   *      documentation</a>
   */
  public static IAST not(final Object a1) {
    return new AST1(Not, Object2Expr.convert(a1));
  }


  public static IAST notElement(final Object a1, final Object a2) {
    return new AST2(NotElement, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST notListQ(final Object a1) {
    return new AST1(NotListQ, Object2Expr.convert(a1));
  }


  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots
   *      documentation</a>
   */
  public static IAST nRoots(final Object a1) {
    return new AST1(NRoots, Object2Expr.convert(a1));
  }


  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots
   *      documentation</a>
   */
  public static IAST nRoots(final Object a1, final Object a2) {
    return new AST2(NRoots, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve
   *      documentation</a>
   */
  public static IAST nSolve(final Object a1, final Object a2) {
    return new AST2(NSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve
   *      documentation</a>
   */
  public static IAST nSolve(final Object a1, final Object a2, final Object a3) {
    return new AST3(NSolve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NullSpace.md">NullSpace
   *      documentation</a>
   */
  public static IAST nullSpace(final Object a1) {
    return new AST1(NullSpace, Object2Expr.convert(a1));
  }


  /**
   * NumberLinePlot( list-of-numbers ) - generates a JavaScript control, which plots a list of
   * values along a line. for the `list-of-numbers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberLinePlot.md">NumberLinePlot
   *      documentation</a>
   */
  public static IAST numberLinePlot(final Object a1) {
    return new AST1(NumberLinePlot, Object2Expr.convert(a1));
  }


  /**
   * NumberLinePlot( list-of-numbers ) - generates a JavaScript control, which plots a list of
   * values along a line. for the `list-of-numbers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberLinePlot.md">NumberLinePlot
   *      documentation</a>
   */
  public static IAST numberLinePlot(final Object a1, final Object a2) {
    return new AST2(NumberLinePlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NumberLinePlot( list-of-numbers ) - generates a JavaScript control, which plots a list of
   * values along a line. for the `list-of-numbers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberLinePlot.md">NumberLinePlot
   *      documentation</a>
   */
  public static IAST numberLinePlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(NumberLinePlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ
   *      documentation</a>
   */
  public static IAST numberQ(final Object a1) {
    return new AST1(NumberQ, Object2Expr.convert(a1));
  }


  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator
   *      documentation</a>
   */
  public static IAST numerator(final Object a1) {
    return new AST1(Numerator, Object2Expr.convert(a1));
  }


  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator
   *      documentation</a>
   */
  public static IAST numerator(final Object a1, final Object a2) {
    return new AST2(Numerator, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NumericalOrder(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to numerical order of
   * `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericalOrder.md">NumericalOrder
   *      documentation</a>
   */
  public static IAST numericalOrder(final Object a1, final Object a2) {
    return new AST2(NumericalOrder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * NumericalSort(list) - `NumericalSort(list)` is evaluated by calling `Sort(list,
   * NumericalOrder)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericalSort.md">NumericalSort
   *      documentation</a>
   */
  public static IAST numericalSort(final Object a1) {
    return new AST1(NumericalSort, Object2Expr.convert(a1));
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


  /**
   * NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericQ.md">NumericQ
   *      documentation</a>
   */
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


  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ
   *      documentation</a>
   */
  public static IAST oddQ(final Object a1) {
    return new AST1(OddQ, Object2Expr.convert(a1));
  }


  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ
   *      documentation</a>
   */
  public static IAST oddQ(final Object a1, final Object a2) {
    return new AST2(OddQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Off( ) - switch off the interactive trace.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *      documentation</a>
   */
  public static IAST off(final Object a1) {
    return new AST1(Off, Object2Expr.convert(a1));
  }


  /**
   * Off( ) - switch off the interactive trace.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *      documentation</a>
   */
  public static IAST off(final Object a1, final Object a2) {
    return new AST2(Off, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Off( ) - switch off the interactive trace.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *      documentation</a>
   */
  public static IAST off(final Object a1, final Object a2, final Object a3) {
    return new AST3(Off, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *      documentation</a>
   */
  public static IAST on(final Object a1) {
    return new AST1(On, Object2Expr.convert(a1));
  }


  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *      documentation</a>
   */
  public static IAST on(final Object a1, final Object a2) {
    return new AST2(On, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *      documentation</a>
   */
  public static IAST on(final Object a1, final Object a2, final Object a3) {
    return new AST3(On, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * OpenAppend("file-name") - opens a file and returns an OutputStream to which writes are
   * appended.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenAppend.md">OpenAppend
   *      documentation</a>
   */
  public static IAST openAppend(final Object a1) {
    return new AST1(OpenAppend, Object2Expr.convert(a1));
  }


  public static IAST openRead(final Object a1) {
    return new AST1(OpenRead, Object2Expr.convert(a1));
  }


  /**
   * OpenWrite() - creates an empty file in the default temporary-file directory and returns an
   * OutputStream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenWrite.md">OpenWrite
   *      documentation</a>
   */
  public static IAST openWrite(final Object a1) {
    return new AST1(OpenWrite, Object2Expr.convert(a1));
  }


  /**
   * Operate(p, expr) - applies `p` to the head of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate
   *      documentation</a>
   */
  public static IAST operate(final Object a1, final Object a2) {
    return new AST2(Operate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Operate(p, expr) - applies `p` to the head of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate
   *      documentation</a>
   */
  public static IAST operate(final Object a1, final Object a2, final Object a3) {
    return new AST3(Operate, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * OptimizeExpression(function) - common subexpressions elimination for a complicated `function`
   * by generating "dummy" variables for these subexpressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptimizeExpression.md">OptimizeExpression
   *      documentation</a>
   */
  public static IAST optimizeExpression(final Object a1) {
    return new AST1(OptimizeExpression, Object2Expr.convert(a1));
  }


  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional
   *      documentation</a>
   */
  public static IAST optional(final Object a1) {
    return new AST1(Optional, Object2Expr.convert(a1));
  }


  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional
   *      documentation</a>
   */
  public static IAST optional(final Object a1, final Object a2) {
    return new AST2(Optional, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options
   *      documentation</a>
   */
  public static IAST options(final Object a1) {
    return new AST1(Options, Object2Expr.convert(a1));
  }


  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options
   *      documentation</a>
   */
  public static IAST options(final Object a1, final Object a2) {
    return new AST2(Options, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST optionsPattern(final Object a1) {
    return new AST1(OptionsPattern, Object2Expr.convert(a1));
  }


  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *      documentation</a>
   */
  public static IAST optionValue(final Object a1) {
    return new AST1(OptionValue, Object2Expr.convert(a1));
  }


  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *      documentation</a>
   */
  public static IAST optionValue(final Object a1, final Object a2) {
    return new AST2(OptionValue, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *      documentation</a>
   */
  public static IAST optionValue(final Object a1, final Object a2, final Object a3) {
    return new AST3(OptionValue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and
   * `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Order.md">Order
   *      documentation</a>
   */
  public static IAST order(final Object a1, final Object a2) {
    return new AST2(Order, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * OrderedQ({a, b,...}) - is `True` if `a` sorts before `b` according to canonical ordering for
   * all adjacent elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ
   *      documentation</a>
   */
  public static IAST orderedQ(final Object a1) {
    return new AST1(OrderedQ, Object2Expr.convert(a1));
  }


  /**
   * OrderedQ({a, b,...}) - is `True` if `a` sorts before `b` according to canonical ordering for
   * all adjacent elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ
   *      documentation</a>
   */
  public static IAST orderedQ(final Object a1, final Object a2) {
    return new AST2(OrderedQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *      documentation</a>
   */
  public static IAST ordering(final Object a1) {
    return new AST1(Ordering, Object2Expr.convert(a1));
  }


  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *      documentation</a>
   */
  public static IAST ordering(final Object a1, final Object a2) {
    return new AST2(Ordering, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *      documentation</a>
   */
  public static IAST ordering(final Object a1, final Object a2, final Object a3) {
    return new AST3(Ordering, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize
   *      documentation</a>
   */
  public static IAST orthogonalize(final Object a1) {
    return new AST1(Orthogonalize, Object2Expr.convert(a1));
  }


  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize
   *      documentation</a>
   */
  public static IAST orthogonalize(final Object a1, final Object a2) {
    return new AST2(Orthogonalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrthogonalMatrixQ.md">OrthogonalMatrixQ
   *      documentation</a>
   */
  public static IAST orthogonalMatrixQ(final Object a1) {
    return new AST1(OrthogonalMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * Out(k) - gives the result of the `k`th input line.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Out.md">Out
   *      documentation</a>
   */
  public static IAST out(final Object a1) {
    return new AST1(Out, Object2Expr.convert(a1));
  }


  /**
   * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in
   * place of multiplication.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Outer.md">Outer
   *      documentation</a>
   */
  public static IAST outer(final Object a1, final Object a2, final Object a3) {
    return new AST3(Outer, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * OutputStream("file-name") - opens a file and returns an OutputStream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OutputStream.md">OutputStream
   *      documentation</a>
   */
  public static IAST outputStream(final Object a1) {
    return new AST1(OutputStream, Object2Expr.convert(a1));
  }


  /**
   * OutputStream("file-name") - opens a file and returns an OutputStream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OutputStream.md">OutputStream
   *      documentation</a>
   */
  public static IAST outputStream(final Object a1, final Object a2) {
    return new AST2(OutputStream, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * OwnValues(symbol) - prints the own-value rule associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OwnValues.md">OwnValues
   *      documentation</a>
   */
  public static IAST ownValues(final Object a1) {
    return new AST1(OwnValues, Object2Expr.convert(a1));
  }


  public static IAST padeApproximant(final Object a1, final Object a2) {
    return new AST2(PadeApproximant, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *      documentation</a>
   */
  public static IAST padLeft(final Object a1) {
    return new AST1(PadLeft, Object2Expr.convert(a1));
  }


  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *      documentation</a>
   */
  public static IAST padLeft(final Object a1, final Object a2) {
    return new AST2(PadLeft, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *      documentation</a>
   */
  public static IAST padLeft(final Object a1, final Object a2, final Object a3) {
    return new AST3(PadLeft, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *      documentation</a>
   */
  public static IAST padRight(final Object a1) {
    return new AST1(PadRight, Object2Expr.convert(a1));
  }


  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *      documentation</a>
   */
  public static IAST padRight(final Object a1, final Object a2) {
    return new AST2(PadRight, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *      documentation</a>
   */
  public static IAST padRight(final Object a1, final Object a2, final Object a3) {
    return new AST3(PadRight, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST parallelMap(final Object a1, final Object a2) {
    return new AST2(ParallelMap, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST parallelMap(final Object a1, final Object a2, final Object a3) {
    return new AST3(ParallelMap, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot
   *      documentation</a>
   */
  public static IAST parametricPlot(final Object a1, final Object a2) {
    return new AST2(ParametricPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot
   *      documentation</a>
   */
  public static IAST parametricPlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(ParametricPlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ParetoDistribution(k,a) - returns a Pareto distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParetoDistribution.md">ParetoDistribution
   *      documentation</a>
   */
  public static IAST paretoDistribution(final Object a1, final Object a2) {
    return new AST2(ParetoDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ParetoDistribution(k,a) - returns a Pareto distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParetoDistribution.md">ParetoDistribution
   *      documentation</a>
   */
  public static IAST paretoDistribution(final Object a1, final Object a2, final Object a3) {
    return new AST3(ParetoDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Part(expr, i) - returns part `i` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *      documentation</a>
   */
  public static IAST part(final Object a1) {
    return new AST1(Part, Object2Expr.convert(a1));
  }


  /**
   * Part(expr, i) - returns part `i` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *      documentation</a>
   */
  public static IAST part(final Object a1, final Object a2) {
    return new AST2(Part, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Part(expr, i) - returns part `i` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *      documentation</a>
   */
  public static IAST part(final Object a1, final Object a2, final Object a3) {
    return new AST3(Part, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition
   *      documentation</a>
   */
  public static IAST partition(final Object a1, final Object a2) {
    return new AST2(Partition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition
   *      documentation</a>
   */
  public static IAST partition(final Object a1, final Object a2, final Object a3) {
    return new AST3(Partition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsP.md">PartitionsP
   *      documentation</a>
   */
  public static IAST partitionsP(final Object a1) {
    return new AST1(PartitionsP, Object2Expr.convert(a1));
  }


  /**
   * PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsQ.md">PartitionsQ
   *      documentation</a>
   */
  public static IAST partitionsQ(final Object a1) {
    return new AST1(PartitionsQ, Object2Expr.convert(a1));
  }


  public static IAST parzenWindow(final Object a1) {
    return new AST1(ParzenWindow, Object2Expr.convert(a1));
  }


  public static IAST pathGraph(final Object a1) {
    return new AST1(PathGraph, Object2Expr.convert(a1));
  }


  public static IAST pathGraphQ(final Object a1) {
    return new AST1(PathGraphQ, Object2Expr.convert(a1));
  }


  public static IAST pattern(final Object a1, final Object a2) {
    return new AST2(Pattern, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of
   * `test(expr)` yields `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest
   *      documentation</a>
   */
  public static IAST patternTest(final Object a1, final Object a2) {
    return new AST2(PatternTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PauliMatrix(n) - returns `n`th Pauli spin `2x2` matrix for `n` between `0` and `4`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PauliMatrix.md">PauliMatrix
   *      documentation</a>
   */
  public static IAST pauliMatrix(final Object a1) {
    return new AST1(PauliMatrix, Object2Expr.convert(a1));
  }


  /**
   * Pause(seconds) - pause the thread for the number of `seconds`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pause.md">Pause
   *      documentation</a>
   */
  public static IAST pause(final Object a1) {
    return new AST1(Pause, Object2Expr.convert(a1));
  }


  /**
   * PDF(distribution, value) - returns the probability density function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF
   *      documentation</a>
   */
  public static IAST pdf(final Object a1) {
    return new AST1(PDF, Object2Expr.convert(a1));
  }


  /**
   * PDF(distribution, value) - returns the probability density function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF
   *      documentation</a>
   */
  public static IAST pdf(final Object a1, final Object a2) {
    return new AST2(PDF, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PearsonCorrelationTest(real-vector1, real-vector2) - `"value"` can be `"TestStatistic"`,
   * `"TestData"` or `"PValue"`. In statistics, the Pearson correlation coefficient (PCC) is a
   * correlation coefficient that measures linear correlation between two sets of data.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PearsonCorrelationTest.md">PearsonCorrelationTest
   *      documentation</a>
   */
  public static IAST pearsonCorrelationTest(final Object a1, final Object a2) {
    return new AST2(PearsonCorrelationTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PearsonCorrelationTest(real-vector1, real-vector2) - `"value"` can be `"TestStatistic"`,
   * `"TestData"` or `"PValue"`. In statistics, the Pearson correlation coefficient (PCC) is a
   * correlation coefficient that measures linear correlation between two sets of data.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PearsonCorrelationTest.md">PearsonCorrelationTest
   *      documentation</a>
   */
  public static IAST pearsonCorrelationTest(final Object a1, final Object a2, final Object a3) {
    return new AST3(PearsonCorrelationTest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a
   * positive integer that is equal to the sum of its proper
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber
   *      documentation</a>
   */
  public static IAST perfectNumber(final Object a1) {
    return new AST1(PerfectNumber, Object2Expr.convert(a1));
  }


  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ
   *      documentation</a>
   */
  public static IAST perfectNumberQ(final Object a1) {
    return new AST1(PerfectNumberQ, Object2Expr.convert(a1));
  }


  /**
   * Perimeter(geometric-form) - returns the perimeter of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Perimeter.md">Perimeter
   *      documentation</a>
   */
  public static IAST perimeter(final Object a1) {
    return new AST1(Perimeter, Object2Expr.convert(a1));
  }


  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles
   *      documentation</a>
   */
  public static IAST permutationCycles(final Object a1) {
    return new AST1(PermutationCycles, Object2Expr.convert(a1));
  }


  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles
   *      documentation</a>
   */
  public static IAST permutationCycles(final Object a1, final Object a2) {
    return new AST2(PermutationCycles, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PermutationCyclesQ(cyclesExpression) - if `cyclesExpression` is a valid `Cycles({{...},{...},
   * ...})` expression return `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCyclesQ.md">PermutationCyclesQ
   *      documentation</a>
   */
  public static IAST permutationCyclesQ(final Object a1) {
    return new AST1(PermutationCyclesQ, Object2Expr.convert(a1));
  }


  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList
   *      documentation</a>
   */
  public static IAST permutationList(final Object a1) {
    return new AST1(PermutationList, Object2Expr.convert(a1));
  }


  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList
   *      documentation</a>
   */
  public static IAST permutationList(final Object a1, final Object a2) {
    return new AST2(PermutationList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PermutationListQ(permutation-list) - if `permutation-list` is a valid permutation list return
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationListQ.md">PermutationListQ
   *      documentation</a>
   */
  public static IAST permutationListQ(final Object a1) {
    return new AST1(PermutationListQ, Object2Expr.convert(a1));
  }


  /**
   * PermutationReplace(list-or-integer, Cycles({{...},{...}, ...})) - replace the arguments of the
   * first expression with the corresponding element from the `Cycles({{...},{...}, ...})`
   * expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationReplace.md">PermutationReplace
   *      documentation</a>
   */
  public static IAST permutationReplace(final Object a1, final Object a2) {
    return new AST2(PermutationReplace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Permutations(list) - gives all possible orderings of the items in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations
   *      documentation</a>
   */
  public static IAST permutations(final Object a1) {
    return new AST1(Permutations, Object2Expr.convert(a1));
  }


  /**
   * Permutations(list) - gives all possible orderings of the items in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations
   *      documentation</a>
   */
  public static IAST permutations(final Object a1, final Object a2) {
    return new AST2(Permutations, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Permute(list, Cycles({permutationCycles})) - permutes the `list` from the cycles in
   * `permutationCycles`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permute.md">Permute
   *      documentation</a>
   */
  public static IAST permute(final Object a1, final Object a2) {
    return new AST2(Permute, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph
   *      documentation</a>
   */
  public static IAST petersenGraph(final Object a1) {
    return new AST1(PetersenGraph, Object2Expr.convert(a1));
  }


  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph
   *      documentation</a>
   */
  public static IAST petersenGraph(final Object a1, final Object a2) {
    return new AST2(PetersenGraph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Pick(nestedList, nestedSelection) - returns the elements of `nestedList` that have value
   * `True` in the corresponding position in `nestedSelection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick
   *      documentation</a>
   */
  public static IAST pick(final Object a1, final Object a2) {
    return new AST2(Pick, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Pick(nestedList, nestedSelection) - returns the elements of `nestedList` that have value
   * `True` in the corresponding position in `nestedSelection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick
   *      documentation</a>
   */
  public static IAST pick(final Object a1, final Object a2, final Object a3) {
    return new AST3(Pick, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise
   *      documentation</a>
   */
  public static IAST piecewise(final Object a1) {
    return new AST1(Piecewise, Object2Expr.convert(a1));
  }


  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise
   *      documentation</a>
   */
  public static IAST piecewise(final Object a1, final Object a2) {
    return new AST2(Piecewise, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *      documentation</a>
   */
  public static IAST piecewiseExpand(final Object a1) {
    return new AST1(PiecewiseExpand, Object2Expr.convert(a1));
  }


  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *      documentation</a>
   */
  public static IAST piecewiseExpand(final Object a1, final Object a2) {
    return new AST2(PiecewiseExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *      documentation</a>
   */
  public static IAST piecewiseExpand(final Object a1, final Object a2, final Object a3) {
    return new AST3(PiecewiseExpand, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PlanarGraphQ(g) - Returns `True` if `g` is a planar graph and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlanarGraphQ.md">PlanarGraphQ
   *      documentation</a>
   */
  public static IAST planarGraphQ(final Object a1) {
    return new AST1(PlanarGraphQ, Object2Expr.convert(a1));
  }


  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot
   *      documentation</a>
   */
  public static IAST plot(final Object a1, final Object a2) {
    return new AST2(Plot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot
   *      documentation</a>
   */
  public static IAST plot(final Object a1, final Object a2, final Object a3) {
    return new AST3(Plot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D
   *      documentation</a>
   */
  public static IAST plot3D(final Object a1, final Object a2) {
    return new AST2(Plot3D, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D
   *      documentation</a>
   */
  public static IAST plot3D(final Object a1, final Object a2, final Object a3) {
    return new AST3(Plot3D, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer
   * number `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pochhammer.md">Pochhammer
   *      documentation</a>
   */
  public static IAST pochhammer(final Object a1, final Object a2) {
    return new AST2(Pochhammer, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *      documentation</a>
   */
  public static IAST point(final Object a1) {
    return new AST1(Point, Object2Expr.convert(a1));
  }


  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *      documentation</a>
   */
  public static IAST point(final Object a1, final Object a2) {
    return new AST2(Point, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *      documentation</a>
   */
  public static IAST point(final Object a1, final Object a2, final Object a3) {
    return new AST3(Point, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PoissonDistribution(m) - returns a Poisson distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonDistribution.md">PoissonDistribution
   *      documentation</a>
   */
  public static IAST poissonDistribution(final Object a1) {
    return new AST1(PoissonDistribution, Object2Expr.convert(a1));
  }


  /**
   * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot
   * expressions `function` in the `t` range `{t, tMin, tMax}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarPlot.md">PolarPlot
   *      documentation</a>
   */
  public static IAST polarPlot(final Object a1, final Object a2) {
    return new AST2(PolarPlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma
   *      documentation</a>
   */
  public static IAST polyGamma(final Object a1) {
    return new AST1(PolyGamma, Object2Expr.convert(a1));
  }


  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma
   *      documentation</a>
   */
  public static IAST polyGamma(final Object a1, final Object a2) {
    return new AST2(PolyGamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *      documentation</a>
   */
  public static IAST polygon(final Object a1) {
    return new AST1(Polygon, Object2Expr.convert(a1));
  }


  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *      documentation</a>
   */
  public static IAST polygon(final Object a1, final Object a2) {
    return new AST2(Polygon, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *      documentation</a>
   */
  public static IAST polygon(final Object a1, final Object a2, final Object a3) {
    return new AST3(Polygon, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PolygonalNumber(nPoints) - returns the triangular number for `nPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolygonalNumber.md">PolygonalNumber
   *      documentation</a>
   */
  public static IAST polygonalNumber(final Object a1) {
    return new AST1(PolygonalNumber, Object2Expr.convert(a1));
  }


  /**
   * PolygonalNumber(nPoints) - returns the triangular number for `nPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolygonalNumber.md">PolygonalNumber
   *      documentation</a>
   */
  public static IAST polygonalNumber(final Object a1, final Object a2) {
    return new AST2(PolygonalNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PolyLog(s, z) - returns the polylogarithm function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyLog.md">PolyLog
   *      documentation</a>
   */
  public static IAST polyLog(final Object a1, final Object a2) {
    return new AST2(PolyLog, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PolyLog(s, z) - returns the polylogarithm function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyLog.md">PolyLog
   *      documentation</a>
   */
  public static IAST polyLog(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolyLog, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the
   * univariate polynomials `p` and `q`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialExtendedGCD.md">PolynomialExtendedGCD
   *      documentation</a>
   */
  public static IAST polynomialExtendedGCD(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialExtendedGCD, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ
   *      documentation</a>
   */
  public static IAST polynomialQ(final Object a1) {
    return new AST1(PolynomialQ, Object2Expr.convert(a1));
  }


  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ
   *      documentation</a>
   */
  public static IAST polynomialQ(final Object a1, final Object a2) {
    return new AST2(PolynomialQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q`
   * for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotient.md">PolynomialQuotient
   *      documentation</a>
   */
  public static IAST polynomialQuotient(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialQuotient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and
   * remainder of the polynomials `p` and `q` for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotientRemainder.md">PolynomialQuotientRemainder
   *      documentation</a>
   */
  public static IAST polynomialQuotientRemainder(final Object a1, final Object a2,
      final Object a3) {
    return new AST3(PolynomialQuotientRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder
   *      documentation</a>
   */
  public static IAST polynomialRemainder(final Object a1, final Object a2, final Object a3) {
    return new AST3(PolynomialRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public static IAST position(final Object a1) {
    return new AST1(Position, Object2Expr.convert(a1));
  }


  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public static IAST position(final Object a1, final Object a2) {
    return new AST2(Position, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public static IAST position(final Object a1, final Object a2, final Object a3) {
    return new AST3(Position, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Positive(x) - returns `True` if `x` is a positive real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive
   *      documentation</a>
   */
  public static IAST positive(final Object a1) {
    return new AST1(Positive, Object2Expr.convert(a1));
  }


  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggests that
   * `expr` has value zero, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ
   *      documentation</a>
   */
  public static IAST possibleZeroQ(final Object a1) {
    return new AST1(PossibleZeroQ, Object2Expr.convert(a1));
  }


  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggests that
   * `expr` has value zero, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ
   *      documentation</a>
   */
  public static IAST possibleZeroQ(final Object a1, final Object a2) {
    return new AST2(PossibleZeroQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST postfix(final Object a1) {
    return new AST1(Postfix, Object2Expr.convert(a1));
  }


  public static IAST postfix(final Object a1, final Object a2) {
    return new AST2(Postfix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand
   *      documentation</a>
   */
  public static IAST powerExpand(final Object a1) {
    return new AST1(PowerExpand, Object2Expr.convert(a1));
  }


  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand
   *      documentation</a>
   */
  public static IAST powerExpand(final Object a1, final Object a2) {
    return new AST2(PowerExpand, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PowerMod(x, y, m) - computes `x^y` modulo `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod
   *      documentation</a>
   */
  public static IAST powerMod(final Object a1, final Object a2, final Object a3) {
    return new AST3(PowerMod, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST precision(final Object a1) {
    return new AST1(Precision, Object2Expr.convert(a1));
  }


  /**
   * PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreDecrement.md">PreDecrement
   *      documentation</a>
   */
  public static IAST preDecrement(final Object a1) {
    return new AST1(PreDecrement, Object2Expr.convert(a1));
  }


  public static IAST prefix(final Object a1) {
    return new AST1(Prefix, Object2Expr.convert(a1));
  }


  public static IAST prefix(final Object a1, final Object a2) {
    return new AST2(Prefix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PreIncrement(x) - increments `x` by `1`, returning the new value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreIncrement.md">PreIncrement
   *      documentation</a>
   */
  public static IAST preIncrement(final Object a1) {
    return new AST1(PreIncrement, Object2Expr.convert(a1));
  }


  /**
   * Prepend(expr, item) - returns `expr` with `item` prepended to its leaves.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prepend.md">Prepend
   *      documentation</a>
   */
  public static IAST prepend(final Object a1, final Object a2) {
    return new AST2(Prepend, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrependTo.md">PrependTo
   *      documentation</a>
   */
  public static IAST prependTo(final Object a1, final Object a2) {
    return new AST2(PrependTo, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Prime(n) - returns the `n`th prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime
   *      documentation</a>
   */
  public static IAST prime(final Object a1) {
    return new AST1(Prime, Object2Expr.convert(a1));
  }


  /**
   * PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeOmega.md">PrimeOmega
   *      documentation</a>
   */
  public static IAST primeOmega(final Object a1) {
    return new AST1(PrimeOmega, Object2Expr.convert(a1));
  }


  /**
   * PrimePi(x) - gives the number of primes less than or equal to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePi.md">PrimePi
   *      documentation</a>
   */
  public static IAST primePi(final Object a1) {
    return new AST1(PrimePi, Object2Expr.convert(a1));
  }


  /**
   * PrimePowerQ(n) - returns `True` if `n` is a power of a prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePowerQ.md">PrimePowerQ
   *      documentation</a>
   */
  public static IAST primePowerQ(final Object a1) {
    return new AST1(PrimePowerQ, Object2Expr.convert(a1));
  }


  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ
   *      documentation</a>
   */
  public static IAST primeQ(final Object a1) {
    return new AST1(PrimeQ, Object2Expr.convert(a1));
  }


  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ
   *      documentation</a>
   */
  public static IAST primeQ(final Object a1, final Object a2) {
    return new AST2(PrimeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST primitiveRoot(final Object a1) {
    return new AST1(PrimitiveRoot, Object2Expr.convert(a1));
  }


  public static IAST primitiveRoot(final Object a1, final Object a2) {
    return new AST2(PrimitiveRoot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * PrimitiveRootList(n) - returns the list of the primitive roots of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimitiveRootList.md">PrimitiveRootList
   *      documentation</a>
   */
  public static IAST primitiveRootList(final Object a1) {
    return new AST1(PrimitiveRootList, Object2Expr.convert(a1));
  }


  public static IAST principalComponents(final Object a1) {
    return new AST1(PrincipalComponents, Object2Expr.convert(a1));
  }


  /**
   * PrintableASCIIQ(str) - returns `True` if all characters in `str` are ASCII characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrintableASCIIQ.md">PrintableASCIIQ
   *      documentation</a>
   */
  public static IAST printableASCIIQ(final Object a1) {
    return new AST1(PrintableASCIIQ, Object2Expr.convert(a1));
  }


  /**
   * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the
   * given `data-set`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Probability.md">Probability
   *      documentation</a>
   */
  public static IAST probability(final Object a1, final Object a2) {
    return new AST2(Probability, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product
   *      documentation</a>
   */
  public static IAST product(final Object a1, final Object a2) {
    return new AST2(Product, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product
   *      documentation</a>
   */
  public static IAST product(final Object a1, final Object a2, final Object a3) {
    return new AST3(Product, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *      documentation</a>
   */
  public static IAST productLog(final Object a1) {
    return new AST1(ProductLog, Object2Expr.convert(a1));
  }


  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *      documentation</a>
   */
  public static IAST productLog(final Object a1, final Object a2) {
    return new AST2(ProductLog, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *      documentation</a>
   */
  public static IAST projection(final Object a1, final Object a2) {
    return new AST2(Projection, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *      documentation</a>
   */
  public static IAST projection(final Object a1, final Object a2, final Object a3) {
    return new AST3(Projection, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse
   *      documentation</a>
   */
  public static IAST pseudoInverse(final Object a1) {
    return new AST1(PseudoInverse, Object2Expr.convert(a1));
  }


  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse
   *      documentation</a>
   */
  public static IAST pseudoInverse(final Object a1, final Object a2) {
    return new AST2(PseudoInverse, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST put(final Object a1, final Object a2) {
    return new AST2(Put, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST put(final Object a1, final Object a2, final Object a3) {
    return new AST3(Put, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a
   * decomposition of a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper
   * triangular matrix `R`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QRDecomposition.md">QRDecomposition
   *      documentation</a>
   */
  public static IAST qRDecomposition(final Object a1) {
    return new AST1(QRDecomposition, Object2Expr.convert(a1));
  }


  /**
   * QuadraticIrrationalQ(expr) - returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) /
   * q` for integers `p,q,d,s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuadraticIrrationalQ.md">QuadraticIrrationalQ
   *      documentation</a>
   */
  public static IAST quadraticIrrationalQ(final Object a1) {
    return new AST1(QuadraticIrrationalQ, Object2Expr.convert(a1));
  }


  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *      documentation</a>
   */
  public static IAST quantile(final Object a1) {
    return new AST1(Quantile, Object2Expr.convert(a1));
  }


  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *      documentation</a>
   */
  public static IAST quantile(final Object a1, final Object a2) {
    return new AST2(Quantile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *      documentation</a>
   */
  public static IAST quantile(final Object a1, final Object a2, final Object a3) {
    return new AST3(Quantile, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *      documentation</a>
   */
  public static IAST quantity(final Object a1) {
    return new AST1(Quantity, Object2Expr.convert(a1));
  }


  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *      documentation</a>
   */
  public static IAST quantity(final Object a1, final Object a2) {
    return new AST2(Quantity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude
   *      documentation</a>
   */
  public static IAST quantityMagnitude(final Object a1) {
    return new AST1(QuantityMagnitude, Object2Expr.convert(a1));
  }


  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude
   *      documentation</a>
   */
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


  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles
   *      documentation</a>
   */
  public static IAST quartiles(final Object a1) {
    return new AST1(Quartiles, Object2Expr.convert(a1));
  }


  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles
   *      documentation</a>
   */
  public static IAST quartiles(final Object a1, final Object a2) {
    return new AST2(Quartiles, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during
   * evaluation).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quiet.md">Quiet
   *      documentation</a>
   */
  public static IAST quiet(final Object a1) {
    return new AST1(Quiet, Object2Expr.convert(a1));
  }


  public static IAST quit(final Object a1) {
    return new AST1(Quit, Object2Expr.convert(a1));
  }


  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient
   *      documentation</a>
   */
  public static IAST quotient(final Object a1, final Object a2) {
    return new AST2(Quotient, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient
   *      documentation</a>
   */
  public static IAST quotient(final Object a1, final Object a2, final Object a3) {
    return new AST3(Quotient, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder
   *      documentation</a>
   */
  public static IAST quotientRemainder(final Object a1) {
    return new AST1(QuotientRemainder, Object2Expr.convert(a1));
  }


  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder
   *      documentation</a>
   */
  public static IAST quotientRemainder(final Object a1, final Object a2) {
    return new AST2(QuotientRemainder, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ramp.md">Ramp
   *      documentation</a>
   */
  public static IAST ramp(final Object a1) {
    return new AST1(Ramp, Object2Expr.convert(a1));
  }


  /**
   * RamseyNumber(r, s) - returns the Ramsey number `R(r,s)`. Currently not all values are known for
   * `1 <= r <= 4`. The function returns unevaluated if the value is unknown.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RamseyNumber.md">RamseyNumber
   *      documentation</a>
   */
  public static IAST ramseyNumber(final Object a1, final Object a2) {
    return new AST2(RamseyNumber, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice
   *      documentation</a>
   */
  public static IAST randomChoice(final Object a1) {
    return new AST1(RandomChoice, Object2Expr.convert(a1));
  }


  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice
   *      documentation</a>
   */
  public static IAST randomChoice(final Object a1, final Object a2) {
    return new AST2(RandomChoice, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex
   *      documentation</a>
   */
  public static IAST randomComplex(final Object a1) {
    return new AST1(RandomComplex, Object2Expr.convert(a1));
  }


  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex
   *      documentation</a>
   */
  public static IAST randomComplex(final Object a1, final Object a2) {
    return new AST2(RandomComplex, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomGraph({number-of-vertices,number-of-edges}) - create a random graph with
   * `number-of-vertices` vertices and `number-of-edges` edges.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomGraph.md">RandomGraph
   *      documentation</a>
   */
  public static IAST randomGraph(final Object a1) {
    return new AST1(RandomGraph, Object2Expr.convert(a1));
  }


  /**
   * RandomGraph({number-of-vertices,number-of-edges}) - create a random graph with
   * `number-of-vertices` vertices and `number-of-edges` edges.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomGraph.md">RandomGraph
   *      documentation</a>
   */
  public static IAST randomGraph(final Object a1, final Object a2) {
    return new AST2(RandomGraph, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger
   *      documentation</a>
   */
  public static IAST randomInteger(final Object a1) {
    return new AST1(RandomInteger, Object2Expr.convert(a1));
  }


  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger
   *      documentation</a>
   */
  public static IAST randomInteger(final Object a1, final Object a2) {
    return new AST2(RandomInteger, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomPermutation(s) - create a pseudo random permutation between `1` and `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPermutation.md">RandomPermutation
   *      documentation</a>
   */
  public static IAST randomPermutation(final Object a1) {
    return new AST1(RandomPermutation, Object2Expr.convert(a1));
  }


  /**
   * RandomPermutation(s) - create a pseudo random permutation between `1` and `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPermutation.md">RandomPermutation
   *      documentation</a>
   */
  public static IAST randomPermutation(final Object a1, final Object a2) {
    return new AST2(RandomPermutation, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime
   *      documentation</a>
   */
  public static IAST randomPrime(final Object a1) {
    return new AST1(RandomPrime, Object2Expr.convert(a1));
  }


  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime
   *      documentation</a>
   */
  public static IAST randomPrime(final Object a1, final Object a2) {
    return new AST2(RandomPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomReal() - create a random number between `0.0` and `1.0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal
   *      documentation</a>
   */
  public static IAST randomReal(final Object a1) {
    return new AST1(RandomReal, Object2Expr.convert(a1));
  }


  /**
   * RandomReal() - create a random number between `0.0` and `1.0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal
   *      documentation</a>
   */
  public static IAST randomReal(final Object a1, final Object a2) {
    return new AST2(RandomReal, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample
   *      documentation</a>
   */
  public static IAST randomSample(final Object a1) {
    return new AST1(RandomSample, Object2Expr.convert(a1));
  }


  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample
   *      documentation</a>
   */
  public static IAST randomSample(final Object a1, final Object a2) {
    return new AST2(RandomSample, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RandomVariate(distribution) - create a pseudo random variate from the `distribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomVariate.md">RandomVariate
   *      documentation</a>
   */
  public static IAST randomVariate(final Object a1) {
    return new AST1(RandomVariate, Object2Expr.convert(a1));
  }


  /**
   * RandomVariate(distribution) - create a pseudo random variate from the `distribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomVariate.md">RandomVariate
   *      documentation</a>
   */
  public static IAST randomVariate(final Object a1, final Object a2) {
    return new AST2(RandomVariate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *      documentation</a>
   */
  public static IAST range(final Object a1) {
    return new AST1(Range, Object2Expr.convert(a1));
  }


  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *      documentation</a>
   */
  public static IAST range(final Object a1, final Object a2) {
    return new AST2(Range, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *      documentation</a>
   */
  public static IAST range(final Object a1, final Object a2, final Object a3) {
    return new AST3(Range, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * RankedMax({e_1, e_2, ..., e_i}, n) - returns the n-th largest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMax.md">RankedMax
   *      documentation</a>
   */
  public static IAST rankedMax(final Object a1, final Object a2) {
    return new AST2(RankedMax, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RankedMin({e_1, e_2, ..., e_i}, n) - returns the n-th smallest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMin.md">RankedMin
   *      documentation</a>
   */
  public static IAST rankedMin(final Object a1, final Object a2) {
    return new AST2(RankedMin, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Rational - is the head of rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *      documentation</a>
   */
  public static IAST rational(final Object a1) {
    return new AST1(Rational, Object2Expr.convert(a1));
  }


  /**
   * Rational - is the head of rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *      documentation</a>
   */
  public static IAST rational(final Object a1, final Object a2) {
    return new AST2(Rational, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize
   *      documentation</a>
   */
  public static IAST rationalize(final Object a1) {
    return new AST1(Rationalize, Object2Expr.convert(a1));
  }


  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize
   *      documentation</a>
   */
  public static IAST rationalize(final Object a1, final Object a2) {
    return new AST2(Rationalize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Re(z) - returns the real component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Re.md">Re
   *      documentation</a>
   */
  public static IAST re(final Object a1) {
    return new AST1(Re, Object2Expr.convert(a1));
  }


  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *      documentation</a>
   */
  public static IAST read(final Object a1) {
    return new AST1(Read, Object2Expr.convert(a1));
  }


  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *      documentation</a>
   */
  public static IAST read(final Object a1, final Object a2) {
    return new AST2(Read, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST readString(final Object a1) {
    return new AST1(ReadString, Object2Expr.convert(a1));
  }


  /**
   * RealAbs(x) - returns the absolute value of the real number `x`. For complex number arguments
   * the function will be left unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealAbs.md">RealAbs
   *      documentation</a>
   */
  public static IAST realAbs(final Object a1) {
    return new AST1(RealAbs, Object2Expr.convert(a1));
  }


  public static IAST realDigits(final Object a1) {
    return new AST1(RealDigits, Object2Expr.convert(a1));
  }


  /**
   * RealSign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   * For complex number arguments the function will be left unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealSign.md">RealSign
   *      documentation</a>
   */
  public static IAST realSign(final Object a1) {
    return new AST1(RealSign, Object2Expr.convert(a1));
  }


  /**
   * RealValuedNumberQ(expr) - returns `True` if `expr` is an explicit real number with no imaginary
   * component.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealValuedNumberQ.md">RealValuedNumberQ
   *      documentation</a>
   */
  public static IAST realValuedNumberQ(final Object a1) {
    return new AST1(RealValuedNumberQ, Object2Expr.convert(a1));
  }


  public static IAST realValuedNumericQ(final Object a1) {
    return new AST1(RealValuedNumericQ, Object2Expr.convert(a1));
  }


  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *      documentation</a>
   */
  public static IAST reap(final Object a1) {
    return new AST1(Reap, Object2Expr.convert(a1));
  }


  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *      documentation</a>
   */
  public static IAST reap(final Object a1, final Object a2) {
    return new AST2(Reap, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *      documentation</a>
   */
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


  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public static IAST reduce(final Object a1) {
    return new AST1(Reduce, Object2Expr.convert(a1));
  }


  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public static IAST reduce(final Object a1, final Object a2) {
    return new AST2(Reduce, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public static IAST reduce(final Object a1, final Object a2, final Object a3) {
    return new AST3(Reduce, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public static IAST refine(final Object a1) {
    return new AST1(Refine, Object2Expr.convert(a1));
  }


  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public static IAST refine(final Object a1, final Object a2) {
    return new AST2(Refine, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public static IAST refine(final Object a1, final Object a2, final Object a3) {
    return new AST3(Refine, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ReIm(z) - returns a list of the real and imaginary component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReIm.md">ReIm
   *      documentation</a>
   */
  public static IAST reIm(final Object a1) {
    return new AST1(ReIm, Object2Expr.convert(a1));
  }


  /**
   * ReleaseHold(expr) - removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from
   * `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReleaseHold.md">ReleaseHold
   *      documentation</a>
   */
  public static IAST releaseHold(final Object a1) {
    return new AST1(ReleaseHold, Object2Expr.convert(a1));
  }


  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics
   *      documentation</a>
   */
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


  /**
   * RepeatedTiming(x) - returns a list with the first entry containing the average evaluation time
   * of `x` and the second entry containing the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RepeatedTiming.md">RepeatedTiming
   *      documentation</a>
   */
  public static IAST repeatedTiming(final Object a1) {
    return new AST1(RepeatedTiming, Object2Expr.convert(a1));
  }


  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *      documentation</a>
   */
  public static IAST replace(final Object a1) {
    return new AST1(Replace, Object2Expr.convert(a1));
  }


  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *      documentation</a>
   */
  public static IAST replace(final Object a1, final Object a2) {
    return new AST2(Replace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *      documentation</a>
   */
  public static IAST replace(final Object a1, final Object a2, final Object a3) {
    return new AST3(Replace, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *      documentation</a>
   */
  public static IAST replaceAll(final Object a1) {
    return new AST1(ReplaceAll, Object2Expr.convert(a1));
  }


  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *      documentation</a>
   */
  public static IAST replaceAll(final Object a1, final Object a2) {
    return new AST2(ReplaceAll, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList
   *      documentation</a>
   */
  public static IAST replaceList(final Object a1, final Object a2) {
    return new AST2(ReplaceList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList
   *      documentation</a>
   */
  public static IAST replaceList(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplaceList, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart
   *      documentation</a>
   */
  public static IAST replacePart(final Object a1, final Object a2) {
    return new AST2(ReplacePart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart
   *      documentation</a>
   */
  public static IAST replacePart(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplacePart, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated
   *      documentation</a>
   */
  public static IAST replaceRepeated(final Object a1, final Object a2) {
    return new AST2(ReplaceRepeated, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated
   *      documentation</a>
   */
  public static IAST replaceRepeated(final Object a1, final Object a2, final Object a3) {
    return new AST3(ReplaceRepeated, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public static IAST rescale(final Object a1) {
    return new AST1(Rescale, Object2Expr.convert(a1));
  }


  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public static IAST rescale(final Object a1, final Object a2) {
    return new AST2(Rescale, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public static IAST rescale(final Object a1, final Object a2, final Object a3) {
    return new AST3(Rescale, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Rest(expr) - returns `expr` with the first element removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rest.md">Rest
   *      documentation</a>
   */
  public static IAST rest(final Object a1) {
    return new AST1(Rest, Object2Expr.convert(a1));
  }


  /**
   * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resultant.md">Resultant
   *      documentation</a>
   */
  public static IAST resultant(final Object a1, final Object a2, final Object a3) {
    return new AST3(Resultant, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Return(expr) - aborts a function call and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Return.md">Return
   *      documentation</a>
   */
  public static IAST $return(final Object a1) {
    return new AST1(Return, Object2Expr.convert(a1));
  }


  /**
   * Reverse(list) - reverse the elements of the `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reverse.md">Reverse
   *      documentation</a>
   */
  public static IAST reverse(final Object a1) {
    return new AST1(Reverse, Object2Expr.convert(a1));
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


  /**
   * RiccatiSolve({A,B},{Q,R}) - An algebraic Riccati equation is a type of nonlinear equation that
   * arises in the context of infinite-horizon optimal control problems in continuous time or
   * discrete time. The continuous time algebraic Riccati equation (CARE):
   * `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller is: `K =
   * R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiccatiSolve.md">RiccatiSolve
   *      documentation</a>
   */
  public static IAST riccatiSolve(final Object a1, final Object a2) {
    return new AST2(RiccatiSolve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle
   *      documentation</a>
   */
  public static IAST riffle(final Object a1, final Object a2) {
    return new AST2(Riffle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two
   * boolean 1-D lists `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`,
   * `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf +
   * c_ft)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RogersTanimotoDissimilarity.md">RogersTanimotoDissimilarity
   *      documentation</a>
   */
  public static IAST rogersTanimotoDissimilarity(final Object a1, final Object a2) {
    return new AST2(RogersTanimotoDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral
   * string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RomanNumeral.md">RomanNumeral
   *      documentation</a>
   */
  public static IAST romanNumeral(final Object a1) {
    return new AST1(RomanNumeral, Object2Expr.convert(a1));
  }


  public static IAST rootIntervals(final Object a1) {
    return new AST1(RootIntervals, Object2Expr.convert(a1));
  }


  public static IAST rootReduce(final Object a1) {
    return new AST1(RootReduce, Object2Expr.convert(a1));
  }


  /**
   * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with
   * respect to the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Roots.md">Roots
   *      documentation</a>
   */
  public static IAST roots(final Object a1, final Object a2) {
    return new AST2(Roots, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft
   *      documentation</a>
   */
  public static IAST rotateLeft(final Object a1) {
    return new AST1(RotateLeft, Object2Expr.convert(a1));
  }


  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft
   *      documentation</a>
   */
  public static IAST rotateLeft(final Object a1, final Object a2) {
    return new AST2(RotateLeft, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight
   *      documentation</a>
   */
  public static IAST rotateRight(final Object a1) {
    return new AST1(RotateRight, Object2Expr.convert(a1));
  }


  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight
   *      documentation</a>
   */
  public static IAST rotateRight(final Object a1, final Object a2) {
    return new AST2(RotateRight, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RotationTransform(phi) - gives a rotation by `phi`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationTransform.md">RotationTransform
   *      documentation</a>
   */
  public static IAST rotationTransform(final Object a1) {
    return new AST1(RotationTransform, Object2Expr.convert(a1));
  }


  /**
   * RotationTransform(phi) - gives a rotation by `phi`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationTransform.md">RotationTransform
   *      documentation</a>
   */
  public static IAST rotationTransform(final Object a1, final Object a2) {
    return new AST2(RotationTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Round(expr) - round a given `expr` to nearest integer.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round
   *      documentation</a>
   */
  public static IAST round(final Object a1) {
    return new AST1(Round, Object2Expr.convert(a1));
  }


  /**
   * Round(expr) - round a given `expr` to nearest integer.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round
   *      documentation</a>
   */
  public static IAST round(final Object a1, final Object a2) {
    return new AST2(Round, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce
   *      documentation</a>
   */
  public static IAST rowReduce(final Object a1) {
    return new AST1(RowReduce, Object2Expr.convert(a1));
  }


  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce
   *      documentation</a>
   */
  public static IAST rowReduce(final Object a1, final Object a2) {
    return new AST2(RowReduce, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Rule(x, y) - represents a rule replacing `x` with `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule
   *      documentation</a>
   */
  public static IAST rule(final Object a1, final Object a2) {
    return new AST2(Rule, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed
   *      documentation</a>
   */
  public static IAST ruleDelayed(final Object a1, final Object a2) {
    return new AST2(RuleDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij`
   * is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RussellRaoDissimilarity.md">RussellRaoDissimilarity
   *      documentation</a>
   */
  public static IAST russellRaoDissimilarity(final Object a1, final Object a2) {
    return new AST2(RussellRaoDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SameObjectQ[java-object1, java-object2] - gives `True` if the Java `==` operator for the Java
   * objects gives true. `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameObjectQ.md">SameObjectQ
   *      documentation</a>
   */
  public static IAST sameObjectQ(final Object a1, final Object a2) {
    return new AST2(SameObjectQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SASTriangle(a, gamma, b) - returns a triangle from 2 sides `a`, `b` and angle `gamma` (which is
   * between the sides).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SASTriangle.md">SASTriangle
   *      documentation</a>
   */
  public static IAST sASTriangle(final Object a1, final Object a2, final Object a3) {
    return new AST3(SASTriangle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount
   *      documentation</a>
   */
  public static IAST satisfiabilityCount(final Object a1) {
    return new AST1(SatisfiabilityCount, Object2Expr.convert(a1));
  }


  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount
   *      documentation</a>
   */
  public static IAST satisfiabilityCount(final Object a1, final Object a2) {
    return new AST2(SatisfiabilityCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount
   *      documentation</a>
   */
  public static IAST satisfiabilityCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiabilityCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances
   *      documentation</a>
   */
  public static IAST satisfiabilityInstances(final Object a1) {
    return new AST1(SatisfiabilityInstances, Object2Expr.convert(a1));
  }


  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances
   *      documentation</a>
   */
  public static IAST satisfiabilityInstances(final Object a1, final Object a2) {
    return new AST2(SatisfiabilityInstances, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances
   *      documentation</a>
   */
  public static IAST satisfiabilityInstances(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiabilityInstances, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *      documentation</a>
   */
  public static IAST satisfiableQ(final Object a1) {
    return new AST1(SatisfiableQ, Object2Expr.convert(a1));
  }


  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *      documentation</a>
   */
  public static IAST satisfiableQ(final Object a1, final Object a2) {
    return new AST2(SatisfiableQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *      documentation</a>
   */
  public static IAST satisfiableQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(SatisfiableQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SawtoothWave(expr) - returns the sawtooth wave value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SawtoothWave.md">SawtoothWave
   *      documentation</a>
   */
  public static IAST sawtoothWave(final Object a1) {
    return new AST1(SawtoothWave, Object2Expr.convert(a1));
  }


  /**
   * SawtoothWave(expr) - returns the sawtooth wave value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SawtoothWave.md">SawtoothWave
   *      documentation</a>
   */
  public static IAST sawtoothWave(final Object a1, final Object a2) {
    return new AST2(SawtoothWave, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST scaled(final Object a1) {
    return new AST1(Scaled, Object2Expr.convert(a1));
  }


  public static IAST scaled(final Object a1, final Object a2) {
    return new AST2(Scaled, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ScalingTransform(v) - gives a scaling transform of `v`. `v` may be a scalar or a vector.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ScalingTransform.md">ScalingTransform
   *      documentation</a>
   */
  public static IAST scalingTransform(final Object a1) {
    return new AST1(ScalingTransform, Object2Expr.convert(a1));
  }


  /**
   * ScalingTransform(v) - gives a scaling transform of `v`. `v` may be a scalar or a vector.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ScalingTransform.md">ScalingTransform
   *      documentation</a>
   */
  public static IAST scalingTransform(final Object a1, final Object a2) {
    return new AST2(ScalingTransform, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns `Null`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *      documentation</a>
   */
  public static IAST scan(final Object a1) {
    return new AST1(Scan, Object2Expr.convert(a1));
  }


  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns `Null`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *      documentation</a>
   */
  public static IAST scan(final Object a1, final Object a2) {
    return new AST2(Scan, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns `Null`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *      documentation</a>
   */
  public static IAST scan(final Object a1, final Object a2, final Object a3) {
    return new AST3(Scan, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SchurDecomposition(matrix) - calculate the Schur-decomposition as a list `{q, t}` of a square
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SchurDecomposition.md">SchurDecomposition
   *      documentation</a>
   */
  public static IAST schurDecomposition(final Object a1) {
    return new AST1(SchurDecomposition, Object2Expr.convert(a1));
  }


  /**
   * Sec(z) - returns the secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sec.md">Sec
   *      documentation</a>
   */
  public static IAST sec(final Object a1) {
    return new AST1(Sec, Object2Expr.convert(a1));
  }


  /**
   * Sech(z) - returns the hyperbolic secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sech.md">Sech
   *      documentation</a>
   */
  public static IAST sech(final Object a1) {
    return new AST1(Sech, Object2Expr.convert(a1));
  }


  public static IAST seedRandom(final Object a1) {
    return new AST1(SeedRandom, Object2Expr.convert(a1));
  }


  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *      documentation</a>
   */
  public static IAST select(final Object a1) {
    return new AST1(Select, Object2Expr.convert(a1));
  }


  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *      documentation</a>
   */
  public static IAST select(final Object a1, final Object a2) {
    return new AST2(Select, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *      documentation</a>
   */
  public static IAST select(final Object a1, final Object a2, final Object a3) {
    return new AST3(Select, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *      documentation</a>
   */
  public static IAST selectFirst(final Object a1) {
    return new AST1(SelectFirst, Object2Expr.convert(a1));
  }


  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *      documentation</a>
   */
  public static IAST selectFirst(final Object a1, final Object a2) {
    return new AST2(SelectFirst, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *      documentation</a>
   */
  public static IAST selectFirst(final Object a1, final Object a2, final Object a3) {
    return new AST3(SelectFirst, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport
   *      documentation</a>
   */
  public static IAST semanticImport(final Object a1) {
    return new AST1(SemanticImport, Object2Expr.convert(a1));
  }


  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport
   *      documentation</a>
   */
  public static IAST semanticImport(final Object a1, final Object a2) {
    return new AST2(SemanticImport, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *      documentation</a>
   */
  public static IAST semanticImportString(final Object a1) {
    return new AST1(SemanticImportString, Object2Expr.convert(a1));
  }


  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *      documentation</a>
   */
  public static IAST semanticImportString(final Object a1, final Object a2) {
    return new AST2(SemanticImportString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *      documentation</a>
   */
  public static IAST semanticImportString(final Object a1, final Object a2, final Object a3) {
    return new AST3(SemanticImportString, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST sequenceCases(final Object a1, final Object a2) {
    return new AST2(SequenceCases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST sequenceCases(final Object a1, final Object a2, final Object a3) {
    return new AST3(SequenceCases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST sequenceReplace(final Object a1, final Object a2) {
    return new AST2(SequenceReplace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST sequenceReplace(final Object a1, final Object a2, final Object a3) {
    return new AST3(SequenceReplace, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST sequenceSplit(final Object a1, final Object a2) {
    return new AST2(SequenceSplit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST sequenceSplit(final Object a1, final Object a2, final Object a3) {
    return new AST3(SequenceSplit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set
   *      documentation</a>
   */
  public static IAST set(final Object a1, final Object a2) {
    return new AST2(Set, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAttributes.md">SetAttributes
   *      documentation</a>
   */
  public static IAST setAttributes(final Object a1, final Object a2) {
    return new AST2(SetAttributes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed
   *      documentation</a>
   */
  public static IAST setDelayed(final Object a1, final Object a2) {
    return new AST2(SetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Share(function) - replace internally equal common subexpressions in `function` by the same
   * reference to reduce memory consumption and return the number of times where `Share(function)`
   * could replace a common subexpression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Share.md">Share
   *      documentation</a>
   */
  public static IAST share(final Object a1) {
    return new AST1(Share, Object2Expr.convert(a1));
  }


  /**
   * ShearingTransform(phi, {1, 0}, {0, 1}) - gives a horizontal shear by the angle `phi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ShearingTransform.md">ShearingTransform
   *      documentation</a>
   */
  public static IAST shearingTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(ShearingTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST $short(final Object a1) {
    return new AST1(Short, Object2Expr.convert(a1));
  }


  /**
   * Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive. For
   * complex numbers `Sign` is defined as `x/Abs(x)`, if x is nonzero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sign.md">Sign
   *      documentation</a>
   */
  public static IAST sign(final Object a1) {
    return new AST1(Sign, Object2Expr.convert(a1));
  }


  /**
   * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`)
   * parity. Returns `0` if two elements in the `permutation-list` are equal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Signature.md">Signature
   *      documentation</a>
   */
  public static IAST signature(final Object a1) {
    return new AST1(Signature, Object2Expr.convert(a1));
  }


  public static IAST signCmp(final Object a1) {
    return new AST1(SignCmp, Object2Expr.convert(a1));
  }


  /**
   * Simplify(expr) - simplifies `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *      documentation</a>
   */
  public static IAST simplify(final Object a1) {
    return new AST1(Simplify, Object2Expr.convert(a1));
  }


  /**
   * Simplify(expr) - simplifies `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *      documentation</a>
   */
  public static IAST simplify(final Object a1, final Object a2) {
    return new AST2(Simplify, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Simplify(expr) - simplifies `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *      documentation</a>
   */
  public static IAST simplify(final Object a1, final Object a2, final Object a3) {
    return new AST3(Simplify, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Sin(expr) - returns the sine of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin
   *      documentation</a>
   */
  public static IAST sin(final Object a1) {
    return new AST1(Sin, Object2Expr.convert(a1));
  }


  /**
   * Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinc.md">Sinc
   *      documentation</a>
   */
  public static IAST sinc(final Object a1) {
    return new AST1(Sinc, Object2Expr.convert(a1));
  }


  /**
   * SingularValueDecomposition(matrix) - calculates the singular value decomposition for the
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SingularValueDecomposition.md">SingularValueDecomposition
   *      documentation</a>
   */
  public static IAST singularValueDecomposition(final Object a1) {
    return new AST1(SingularValueDecomposition, Object2Expr.convert(a1));
  }


  public static IAST singularValueList(final Object a1) {
    return new AST1(SingularValueList, Object2Expr.convert(a1));
  }


  public static IAST singularValueList(final Object a1, final Object a2) {
    return new AST2(SingularValueList, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Sinh(z) - returns the hyperbolic sine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinh.md">Sinh
   *      documentation</a>
   */
  public static IAST sinh(final Object a1) {
    return new AST1(Sinh, Object2Expr.convert(a1));
  }


  /**
   * SinhIntegral(expr) - returns the sine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral
   *      documentation</a>
   */
  public static IAST sinhIntegral(final Object a1) {
    return new AST1(SinhIntegral, Object2Expr.convert(a1));
  }


  /**
   * SinIntegral(expr) - returns the hyperbolic sine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinIntegral.md">SinIntegral
   *      documentation</a>
   */
  public static IAST sinIntegral(final Object a1) {
    return new AST1(SinIntegral, Object2Expr.convert(a1));
  }


  /**
   * SixJSymbol({j1,j2,j3},{j4,j5,j6}) - get the 6-j symbol coefficients.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SixJSymbol.md">SixJSymbol
   *      documentation</a>
   */
  public static IAST sixJSymbol(final Object a1, final Object a2) {
    return new AST2(SixJSymbol, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for
   * estimating the symmetry of a distribution).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Skewness.md">Skewness
   *      documentation</a>
   */
  public static IAST skewness(final Object a1) {
    return new AST1(Skewness, Object2Expr.convert(a1));
  }


  /**
   * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SokalSneathDissimilarity.md">SokalSneathDissimilarity
   *      documentation</a>
   */
  public static IAST sokalSneathDissimilarity(final Object a1, final Object a2) {
    return new AST2(SokalSneathDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *      documentation</a>
   */
  public static IAST solve(final Object a1, final Object a2) {
    return new AST2(Solve, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *      documentation</a>
   */
  public static IAST solve(final Object a1, final Object a2, final Object a3) {
    return new AST3(Solve, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort
   *      documentation</a>
   */
  public static IAST sort(final Object a1) {
    return new AST1(Sort, Object2Expr.convert(a1));
  }


  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort
   *      documentation</a>
   */
  public static IAST sort(final Object a1, final Object a2) {
    return new AST2(Sort, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SortBy(list, f) - sorts `list` (or the elements of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy
   *      documentation</a>
   */
  public static IAST sortBy(final Object a1) {
    return new AST1(SortBy, Object2Expr.convert(a1));
  }


  /**
   * SortBy(list, f) - sorts `list` (or the elements of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy
   *      documentation</a>
   */
  public static IAST sortBy(final Object a1, final Object a2) {
    return new AST2(SortBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow
   *      documentation</a>
   */
  public static IAST sow(final Object a1) {
    return new AST1(Sow, Object2Expr.convert(a1));
  }


  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow
   *      documentation</a>
   */
  public static IAST sow(final Object a1, final Object a2) {
    return new AST2(Sow, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SparseArray(nestedList) - create a sparse array from a `nestedList` structure.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public static IAST sparseArray(final Object a1) {
    return new AST1(SparseArray, Object2Expr.convert(a1));
  }


  /**
   * SparseArray(nestedList) - create a sparse array from a `nestedList` structure.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public static IAST sparseArray(final Object a1, final Object a2) {
    return new AST2(SparseArray, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SparseArray(nestedList) - create a sparse array from a `nestedList` structure.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public static IAST sparseArray(final Object a1, final Object a2, final Object a3) {
    return new AST3(SparseArray, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Sphere({x, y, z}) - is a sphere of radius `1` centered at the point `{x, y, z}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sphere.md">Sphere
   *      documentation</a>
   */
  public static IAST sphere(final Object a1) {
    return new AST1(Sphere, Object2Expr.convert(a1));
  }


  /**
   * Sphere({x, y, z}) - is a sphere of radius `1` centered at the point `{x, y, z}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sphere.md">Sphere
   *      documentation</a>
   */
  public static IAST sphere(final Object a1, final Object a2) {
    return new AST2(Sphere, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselJ.md">SphericalBesselJ
   *      documentation</a>
   */
  public static IAST sphericalBesselJ(final Object a1, final Object a2) {
    return new AST2(SphericalBesselJ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselY.md">SphericalBesselY
   *      documentation</a>
   */
  public static IAST sphericalBesselY(final Object a1, final Object a2) {
    return new AST2(SphericalBesselY, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST sphericalHankelH1(final Object a1, final Object a2) {
    return new AST2(SphericalHankelH1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST sphericalHankelH2(final Object a1, final Object a2) {
    return new AST2(SphericalHankelH2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Split(list) - splits `list` into collections of consecutive identical elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split
   *      documentation</a>
   */
  public static IAST split(final Object a1) {
    return new AST1(Split, Object2Expr.convert(a1));
  }


  /**
   * Split(list) - splits `list` into collections of consecutive identical elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split
   *      documentation</a>
   */
  public static IAST split(final Object a1, final Object a2) {
    return new AST2(Split, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same
   * result when `f` is applied.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SplitBy.md">SplitBy
   *      documentation</a>
   */
  public static IAST splitBy(final Object a1, final Object a2) {
    return new AST2(SplitBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Sqrt(expr) - returns the square root of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt
   *      documentation</a>
   */
  public static IAST sqrt(final Object a1) {
    return new AST1(Sqrt, Object2Expr.convert(a1));
  }


  /**
   * SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaredEuclideanDistance.md">SquaredEuclideanDistance
   *      documentation</a>
   */
  public static IAST squaredEuclideanDistance(final Object a1, final Object a2) {
    return new AST2(SquaredEuclideanDistance, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ
   *      documentation</a>
   */
  public static IAST squareFreeQ(final Object a1) {
    return new AST1(SquareFreeQ, Object2Expr.convert(a1));
  }


  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ
   *      documentation</a>
   */
  public static IAST squareFreeQ(final Object a1, final Object a2) {
    return new AST2(SquareFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ
   *      documentation</a>
   */
  public static IAST squareMatrixQ(final Object a1) {
    return new AST1(SquareMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * SSSTriangle(a, b, c) - returns a triangle from 3 sides `a`, `b` and `c`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SSSTriangle.md">SSSTriangle
   *      documentation</a>
   */
  public static IAST sSSTriangle(final Object a1, final Object a2, final Object a3) {
    return new AST3(SSSTriangle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Stack.md">Stack
   *      documentation</a>
   */
  public static IAST stack(final Object a1) {
    return new AST1(Stack, Object2Expr.convert(a1));
  }


  /**
   * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackBegin.md">StackBegin
   *      documentation</a>
   */
  public static IAST stackBegin(final Object a1) {
    return new AST1(StackBegin, Object2Expr.convert(a1));
  }


  /**
   * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of
   * numerical values or symbols. Numerical values may be real or complex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviation.md">StandardDeviation
   *      documentation</a>
   */
  public static IAST standardDeviation(final Object a1) {
    return new AST1(StandardDeviation, Object2Expr.convert(a1));
  }


  /**
   * Standardize(list-of-values) - shifts the `list-of-values` by `Mean(list-of-values)`and scales
   * by `StandardDeviation(list-of-values)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Standardize.md">Standardize
   *      documentation</a>
   */
  public static IAST standardize(final Object a1) {
    return new AST1(Standardize, Object2Expr.convert(a1));
  }


  /**
   * Standardize(list-of-values) - shifts the `list-of-values` by `Mean(list-of-values)`and scales
   * by `StandardDeviation(list-of-values)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Standardize.md">Standardize
   *      documentation</a>
   */
  public static IAST standardize(final Object a1, final Object a2) {
    return new AST2(Standardize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Standardize(list-of-values) - shifts the `list-of-values` by `Mean(list-of-values)`and scales
   * by `StandardDeviation(list-of-values)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Standardize.md">Standardize
   *      documentation</a>
   */
  public static IAST standardize(final Object a1, final Object a2, final Object a3) {
    return new AST3(Standardize, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StarGraph(order) - create a new star graph with `order` number of total vertices including the
   * center vertex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarGraph.md">StarGraph
   *      documentation</a>
   */
  public static IAST starGraph(final Object a1) {
    return new AST1(StarGraph, Object2Expr.convert(a1));
  }


  /**
   * StieltjesGamma(a) - returns Stieltjes constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma
   *      documentation</a>
   */
  public static IAST stieltjesGamma(final Object a1) {
    return new AST1(StieltjesGamma, Object2Expr.convert(a1));
  }


  /**
   * StieltjesGamma(a) - returns Stieltjes constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma
   *      documentation</a>
   */
  public static IAST stieltjesGamma(final Object a1, final Object a2) {
    return new AST2(StieltjesGamma, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StirlingS1(n, k) - returns the Stirling numbers of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS1.md">StirlingS1
   *      documentation</a>
   */
  public static IAST stirlingS1(final Object a1, final Object a2) {
    return new AST2(StirlingS1, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the
   * number of ways of partitioning an `n`-element set into `k` non-empty subsets.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS2.md">StirlingS2
   *      documentation</a>
   */
  public static IAST stirlingS2(final Object a1, final Object a2) {
    return new AST2(StirlingS2, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *      documentation</a>
   */
  public static IAST stringCases(final Object a1) {
    return new AST1(StringCases, Object2Expr.convert(a1));
  }


  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *      documentation</a>
   */
  public static IAST stringCases(final Object a1, final Object a2) {
    return new AST2(StringCases, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *      documentation</a>
   */
  public static IAST stringCases(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringCases, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *      documentation</a>
   */
  public static IAST stringContainsQ(final Object a1) {
    return new AST1(StringContainsQ, Object2Expr.convert(a1));
  }


  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *      documentation</a>
   */
  public static IAST stringContainsQ(final Object a1, final Object a2) {
    return new AST2(StringContainsQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *      documentation</a>
   */
  public static IAST stringContainsQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringContainsQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *      documentation</a>
   */
  public static IAST stringCount(final Object a1) {
    return new AST1(StringCount, Object2Expr.convert(a1));
  }


  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *      documentation</a>
   */
  public static IAST stringCount(final Object a1, final Object a2) {
    return new AST2(StringCount, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *      documentation</a>
   */
  public static IAST stringCount(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringCount, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST stringDrop(final Object a1, final Object a2) {
    return new AST2(StringDrop, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *      documentation</a>
   */
  public static IAST stringExpression(final Object a1) {
    return new AST1(StringExpression, Object2Expr.convert(a1));
  }


  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *      documentation</a>
   */
  public static IAST stringExpression(final Object a1, final Object a2) {
    return new AST2(StringExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *      documentation</a>
   */
  public static IAST stringExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST stringForm(final Object a1) {
    return new AST1(StringForm, Object2Expr.convert(a1));
  }


  public static IAST stringForm(final Object a1, final Object a2) {
    return new AST2(StringForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST stringForm(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringForm, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST stringFormat(final Object a1) {
    return new AST1(StringFormat, Object2Expr.convert(a1));
  }


  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *      documentation</a>
   */
  public static IAST stringFreeQ(final Object a1) {
    return new AST1(StringFreeQ, Object2Expr.convert(a1));
  }


  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *      documentation</a>
   */
  public static IAST stringFreeQ(final Object a1, final Object a2) {
    return new AST2(StringFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *      documentation</a>
   */
  public static IAST stringFreeQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringFreeQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringInsert(string, new-string, position) - returns a string with `new-string` inserted
   * starting at `position` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringInsert.md">StringInsert
   *      documentation</a>
   */
  public static IAST stringInsert(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringInsert, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringLength(string) - gives the length of `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringLength.md">StringLength
   *      documentation</a>
   */
  public static IAST stringLength(final Object a1) {
    return new AST1(StringLength, Object2Expr.convert(a1));
  }


  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ
   *      documentation</a>
   */
  public static IAST stringMatchQ(final Object a1, final Object a2) {
    return new AST2(StringMatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ
   *      documentation</a>
   */
  public static IAST stringMatchQ(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringMatchQ, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringPart(str, pos) - return the character at position `pos` from the `str` string expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPart.md">StringPart
   *      documentation</a>
   */
  public static IAST stringPart(final Object a1, final Object a2) {
    return new AST2(StringPart, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *      documentation</a>
   */
  public static IAST stringPosition(final Object a1) {
    return new AST1(StringPosition, Object2Expr.convert(a1));
  }


  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *      documentation</a>
   */
  public static IAST stringPosition(final Object a1, final Object a2) {
    return new AST2(StringPosition, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *      documentation</a>
   */
  public static IAST stringPosition(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringPosition, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringQ(x) - is `True` if `x` is a string object, or `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringQ.md">StringQ
   *      documentation</a>
   */
  public static IAST stringQ(final Object a1) {
    return new AST1(StringQ, Object2Expr.convert(a1));
  }


  public static IAST stringRepeat(final Object a1, final Object a2) {
    return new AST2(StringRepeat, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST stringRepeat(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringRepeat, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *      documentation</a>
   */
  public static IAST stringReplace(final Object a1) {
    return new AST1(StringReplace, Object2Expr.convert(a1));
  }


  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *      documentation</a>
   */
  public static IAST stringReplace(final Object a1, final Object a2) {
    return new AST2(StringReplace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *      documentation</a>
   */
  public static IAST stringReplace(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringReplace, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringReplace(string) - reverse the `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReverse.md">StringReverse
   *      documentation</a>
   */
  public static IAST stringReverse(final Object a1) {
    return new AST1(StringReverse, Object2Expr.convert(a1));
  }


  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *      documentation</a>
   */
  public static IAST stringRiffle(final Object a1) {
    return new AST1(StringRiffle, Object2Expr.convert(a1));
  }


  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *      documentation</a>
   */
  public static IAST stringRiffle(final Object a1, final Object a2) {
    return new AST2(StringRiffle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *      documentation</a>
   */
  public static IAST stringRiffle(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringRiffle, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *      documentation</a>
   */
  public static IAST stringSplit(final Object a1) {
    return new AST1(StringSplit, Object2Expr.convert(a1));
  }


  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *      documentation</a>
   */
  public static IAST stringSplit(final Object a1, final Object a2) {
    return new AST2(StringSplit, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *      documentation</a>
   */
  public static IAST stringSplit(final Object a1, final Object a2, final Object a3) {
    return new AST3(StringSplit, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * StringTake("string", n) - gives the first `n` characters in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTake.md">StringTake
   *      documentation</a>
   */
  public static IAST stringTake(final Object a1, final Object a2) {
    return new AST2(StringTake, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StringToByteArray(string) - encodes the `string` into a sequence of bytes using the default
   * character set `UTF-8`, storing the result into into a `ByteArray`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToByteArray.md">StringToByteArray
   *      documentation</a>
   */
  public static IAST stringToByteArray(final Object a1) {
    return new AST1(StringToByteArray, Object2Expr.convert(a1));
  }


  /**
   * StringToStream("string") - converts a `string` to an open input stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToStream.md">StringToStream
   *      documentation</a>
   */
  public static IAST stringToStream(final Object a1) {
    return new AST1(StringToStream, Object2Expr.convert(a1));
  }


  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim
   *      documentation</a>
   */
  public static IAST stringTrim(final Object a1) {
    return new AST1(StringTrim, Object2Expr.convert(a1));
  }


  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim
   *      documentation</a>
   */
  public static IAST stringTrim(final Object a1, final Object a2) {
    return new AST2(StringTrim, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST structure(final Object a1) {
    return new AST1(Structure, Object2Expr.convert(a1));
  }


  /**
   * StruveH(n, z) - returns the Struve function `H_n(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveH.md">StruveH
   *      documentation</a>
   */
  public static IAST struveH(final Object a1, final Object a2) {
    return new AST2(StruveH, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * StruveL(n, z) - returns the modified Struve function `L_n(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveL.md">StruveL
   *      documentation</a>
   */
  public static IAST struveL(final Object a1, final Object a2) {
    return new AST2(StruveL, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *      documentation</a>
   */
  public static IAST subdivide(final Object a1) {
    return new AST1(Subdivide, Object2Expr.convert(a1));
  }


  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *      documentation</a>
   */
  public static IAST subdivide(final Object a1, final Object a2) {
    return new AST2(Subdivide, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *      documentation</a>
   */
  public static IAST subdivide(final Object a1, final Object a2, final Object a3) {
    return new AST3(Subdivide, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Subfactorial(n) - returns the subfactorial number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subfactorial.md">Subfactorial
   *      documentation</a>
   */
  public static IAST subfactorial(final Object a1) {
    return new AST1(Subfactorial, Object2Expr.convert(a1));
  }


  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ
   *      documentation</a>
   */
  public static IAST subsetQ(final Object a1, final Object a2) {
    return new AST2(SubsetQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *      documentation</a>
   */
  public static IAST subsets(final Object a1) {
    return new AST1(Subsets, Object2Expr.convert(a1));
  }


  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *      documentation</a>
   */
  public static IAST subsets(final Object a1, final Object a2) {
    return new AST2(Subsets, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Subtract(a, b) - represents the subtraction of `b` from `a`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subtract.md">Subtract
   *      documentation</a>
   */
  public static IAST subtract(final Object a1, final Object a2) {
    return new AST2(Subtract, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SubtractFrom(x, dx) - is equivalent to `x = x - dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractFrom.md">SubtractFrom
   *      documentation</a>
   */
  public static IAST subtractFrom(final Object a1, final Object a2) {
    return new AST2(SubtractFrom, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * SubtractSides(compare-expr, value) - subtracts `value` from all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractSides.md">SubtractSides
   *      documentation</a>
   */
  public static IAST subtractSides(final Object a1) {
    return new AST1(SubtractSides, Object2Expr.convert(a1));
  }


  /**
   * SubtractSides(compare-expr, value) - subtracts `value` from all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractSides.md">SubtractSides
   *      documentation</a>
   */
  public static IAST subtractSides(final Object a1, final Object a2) {
    return new AST2(SubtractSides, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum
   *      documentation</a>
   */
  public static IAST sum(final Object a1, final Object a2) {
    return new AST2(Sum, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum
   *      documentation</a>
   */
  public static IAST sum(final Object a1, final Object a2, final Object a3) {
    return new AST3(Sum, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  public static IAST summary(final Object a1) {
    return new AST1(Summary, Object2Expr.convert(a1));
  }


  /**
   * Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Surd.md">Surd
   *      documentation</a>
   */
  public static IAST surd(final Object a1, final Object a2) {
    return new AST2(Surd, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *      documentation</a>
   */
  public static IAST $switch(final Object a1) {
    return new AST1(Switch, Object2Expr.convert(a1));
  }


  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *      documentation</a>
   */
  public static IAST $switch(final Object a1, final Object a2) {
    return new AST2(Switch, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *      documentation</a>
   */
  public static IAST $switch(final Object a1, final Object a2, final Object a3) {
    return new AST3(Switch, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * SymbolName(s) - returns the name of the symbol `s` (without any leading context name).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolName.md">SymbolName
   *      documentation</a>
   */
  public static IAST symbolName(final Object a1) {
    return new AST1(SymbolName, Object2Expr.convert(a1));
  }


  /**
   * SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolQ.md">SymbolQ
   *      documentation</a>
   */
  public static IAST symbolQ(final Object a1) {
    return new AST1(SymbolQ, Object2Expr.convert(a1));
  }


  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ
   *      documentation</a>
   */
  public static IAST symmetricMatrixQ(final Object a1) {
    return new AST1(SymmetricMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ
   *      documentation</a>
   */
  public static IAST symmetricMatrixQ(final Object a1, final Object a2) {
    return new AST2(SymmetricMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST syntaxLength(final Object a1) {
    return new AST1(SyntaxLength, Object2Expr.convert(a1));
  }


  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ
   *      documentation</a>
   */
  public static IAST syntaxQ(final Object a1) {
    return new AST1(SyntaxQ, Object2Expr.convert(a1));
  }


  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ
   *      documentation</a>
   */
  public static IAST syntaxQ(final Object a1, final Object a2) {
    return new AST2(SyntaxQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table
   *      documentation</a>
   */
  public static IAST table(final Object a1, final Object a2) {
    return new AST2(Table, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table
   *      documentation</a>
   */
  public static IAST table(final Object a1, final Object a2, final Object a3) {
    return new AST3(Table, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST tableForm(final Object a1) {
    return new AST1(TableForm, Object2Expr.convert(a1));
  }


  /**
   * TagSet(f, expr, value) - assigns the evaluated `value` to `expr` and associates the
   * corresponding rule with the symbol `f`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSet.md">TagSet
   *      documentation</a>
   */
  public static IAST tagSet(final Object a1, final Object a2, final Object a3) {
    return new AST3(TagSet, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TagSetDelayed(f, expr, value) - assigns `value` to `expr`, without evaluating `value` and
   * associates the corresponding rule with the symbol `f`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSetDelayed.md">TagSetDelayed
   *      documentation</a>
   */
  public static IAST tagSetDelayed(final Object a1, final Object a2, final Object a3) {
    return new AST3(TagSetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take
   *      documentation</a>
   */
  public static IAST take(final Object a1, final Object a2) {
    return new AST2(Take, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take
   *      documentation</a>
   */
  public static IAST take(final Object a1, final Object a2, final Object a3) {
    return new AST3(Take, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TakeLargest({e_1, e_2, ..., e_i}, n) - returns the `n` largest real values from the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargest.md">TakeLargest
   *      documentation</a>
   */
  public static IAST takeLargest(final Object a1) {
    return new AST1(TakeLargest, Object2Expr.convert(a1));
  }


  /**
   * TakeLargest({e_1, e_2, ..., e_i}, n) - returns the `n` largest real values from the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargest.md">TakeLargest
   *      documentation</a>
   */
  public static IAST takeLargest(final Object a1, final Object a2) {
    return new AST2(TakeLargest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TakeLargestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is largest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargestBy.md">TakeLargestBy
   *      documentation</a>
   */
  public static IAST takeLargestBy(final Object a1, final Object a2) {
    return new AST2(TakeLargestBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TakeLargestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is largest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargestBy.md">TakeLargestBy
   *      documentation</a>
   */
  public static IAST takeLargestBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(TakeLargestBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TakeSmallest({e_1, e_2, ..., e_i}, n) - returns the `n` smallest real values from the list
   * `{e_1, e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallest.md">TakeSmallest
   *      documentation</a>
   */
  public static IAST takeSmallest(final Object a1) {
    return new AST1(TakeSmallest, Object2Expr.convert(a1));
  }


  /**
   * TakeSmallest({e_1, e_2, ..., e_i}, n) - returns the `n` smallest real values from the list
   * `{e_1, e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallest.md">TakeSmallest
   *      documentation</a>
   */
  public static IAST takeSmallest(final Object a1, final Object a2) {
    return new AST2(TakeSmallest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is smallest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallestBy.md">TakeSmallestBy
   *      documentation</a>
   */
  public static IAST takeSmallestBy(final Object a1, final Object a2) {
    return new AST2(TakeSmallestBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is smallest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallestBy.md">TakeSmallestBy
   *      documentation</a>
   */
  public static IAST takeSmallestBy(final Object a1, final Object a2, final Object a3) {
    return new AST3(TakeSmallestBy, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TakeWhile({e1, e2, ...}, head) - returns the list of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeWhile.md">TakeWhile
   *      documentation</a>
   */
  public static IAST takeWhile(final Object a1, final Object a2) {
    return new AST2(TakeWhile, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binaryPredicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binaryPredicate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally
   *      documentation</a>
   */
  public static IAST tally(final Object a1) {
    return new AST1(Tally, Object2Expr.convert(a1));
  }


  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binaryPredicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binaryPredicate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally
   *      documentation</a>
   */
  public static IAST tally(final Object a1, final Object a2) {
    return new AST2(Tally, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Tan(expr) - returns the tangent of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan
   *      documentation</a>
   */
  public static IAST tan(final Object a1) {
    return new AST1(Tan, Object2Expr.convert(a1));
  }


  /**
   * Tanh(z) - returns the hyperbolic tangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tanh.md">Tanh
   *      documentation</a>
   */
  public static IAST tanh(final Object a1) {
    return new AST1(Tanh, Object2Expr.convert(a1));
  }


  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ
   *      documentation</a>
   */
  public static IAST tautologyQ(final Object a1) {
    return new AST1(TautologyQ, Object2Expr.convert(a1));
  }


  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ
   *      documentation</a>
   */
  public static IAST tautologyQ(final Object a1, final Object a2) {
    return new AST2(TautologyQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TemplateApply(string, values) - renders a `StringTemplate` expression by replacing
   * `TemplateSlot`s with mapped values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateApply.md">TemplateApply
   *      documentation</a>
   */
  public static IAST templateApply(final Object a1) {
    return new AST1(TemplateApply, Object2Expr.convert(a1));
  }


  /**
   * TemplateApply(string, values) - renders a `StringTemplate` expression by replacing
   * `TemplateSlot`s with mapped values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateApply.md">TemplateApply
   *      documentation</a>
   */
  public static IAST templateApply(final Object a1, final Object a2) {
    return new AST2(TemplateApply, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TemplateIf(condition-expression, true-expression, false-expression) - in `TemplateApply`
   * evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise
   * insert `false-expression`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateIf.md">TemplateIf
   *      documentation</a>
   */
  public static IAST templateIf(final Object a1, final Object a2) {
    return new AST2(TemplateIf, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TemplateIf(condition-expression, true-expression, false-expression) - in `TemplateApply`
   * evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise
   * insert `false-expression`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateIf.md">TemplateIf
   *      documentation</a>
   */
  public static IAST templateIf(final Object a1, final Object a2, final Object a3) {
    return new AST3(TemplateIf, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *      documentation</a>
   */
  public static IAST templateSlot(final Object a1) {
    return new AST1(TemplateSlot, Object2Expr.convert(a1));
  }


  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *      documentation</a>
   */
  public static IAST templateSlot(final Object a1, final Object a2) {
    return new AST2(TemplateSlot, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *      documentation</a>
   */
  public static IAST templateSlot(final Object a1, final Object a2, final Object a3) {
    return new AST3(TemplateSlot, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * TensorDimensions(t) - return the dimensions of the tensor `t`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorDimensions.md">TensorDimensions
   *      documentation</a>
   */
  public static IAST tensorDimensions(final Object a1) {
    return new AST1(TensorDimensions, Object2Expr.convert(a1));
  }


  /**
   * TensorRank(t) - return the rank of the tensor `t`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorRank.md">TensorRank
   *      documentation</a>
   */
  public static IAST tensorRank(final Object a1) {
    return new AST1(TensorRank, Object2Expr.convert(a1));
  }


  /**
   * TensorRank(t) - return the rank of the tensor `t`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorRank.md">TensorRank
   *      documentation</a>
   */
  public static IAST tensorRank(final Object a1, final Object a2) {
    return new AST2(TensorRank, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST tensorSymmetry(final Object a1) {
    return new AST1(TensorSymmetry, Object2Expr.convert(a1));
  }


  public static IAST tensorSymmetry(final Object a1, final Object a2) {
    return new AST2(TensorSymmetry, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TestReport("file-name-string") - load the unit tests from a `file-name-string` and print a
   * summary of the `VerificationTest` included in the file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestReport.md">TestReport
   *      documentation</a>
   */
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


  /**
   * TeXForm(expr) - returns the TeX form of the evaluated `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TeXForm.md">TeXForm
   *      documentation</a>
   */
  public static IAST teXForm(final Object a1) {
    return new AST1(TeXForm, Object2Expr.convert(a1));
  }


  public static IAST text(final Object a1) {
    return new AST1(Text, Object2Expr.convert(a1));
  }


  public static IAST text(final Object a1, final Object a2) {
    return new AST2(Text, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST textString(final Object a1) {
    return new AST1(TextString, Object2Expr.convert(a1));
  }


  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread
   *      documentation</a>
   */
  public static IAST thread(final Object a1) {
    return new AST1(Thread, Object2Expr.convert(a1));
  }


  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread
   *      documentation</a>
   */
  public static IAST thread(final Object a1, final Object a2) {
    return new AST2(Thread, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ThreeJSymbol({j1,m1},{j2,m2},{j3,m3}) - get the 3-j symbol coefficients.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ThreeJSymbol.md">ThreeJSymbol
   *      documentation</a>
   */
  public static IAST threeJSymbol(final Object a1, final Object a2, final Object a3) {
    return new AST3(ThreeJSymbol, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Through(p(f)[x]) - gives `p(f(x))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through
   *      documentation</a>
   */
  public static IAST through(final Object a1) {
    return new AST1(Through, Object2Expr.convert(a1));
  }


  /**
   * Through(p(f)[x]) - gives `p(f(x))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through
   *      documentation</a>
   */
  public static IAST through(final Object a1, final Object a2) {
    return new AST2(Through, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Throw(value) - stops evaluation and returns `value` as the value of the nearest enclosing
   * `Catch`. `Catch(value, tag)` is caught only by `Catch(expr, form)`, where `tag` matches `form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Throw.md">Throw
   *      documentation</a>
   */
  public static IAST $throw(final Object a1) {
    return new AST1(Throw, Object2Expr.convert(a1));
  }


  /**
   * Throw(value) - stops evaluation and returns `value` as the value of the nearest enclosing
   * `Catch`. `Catch(value, tag)` is caught only by `Catch(expr, form)`, where `tag` matches `form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Throw.md">Throw
   *      documentation</a>
   */
  public static IAST $throw(final Object a1, final Object a2) {
    return new AST2(Throw, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained
   *      documentation</a>
   */
  public static IAST timeConstrained(final Object a1, final Object a2) {
    return new AST2(TimeConstrained, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained
   *      documentation</a>
   */
  public static IAST timeConstrained(final Object a1, final Object a2, final Object a3) {
    return new AST3(TimeConstrained, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST timeObject(final Object a1) {
    return new AST1(TimeObject, Object2Expr.convert(a1));
  }


  /**
   * TimesBy(x, dx) - is equivalent to `x = x * dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimesBy.md">TimesBy
   *      documentation</a>
   */
  public static IAST timesBy(final Object a1, final Object a2) {
    return new AST2(TimesBy, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TimeValue(p, i, n) - returns a time value calculation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue
   *      documentation</a>
   */
  public static IAST timeValue(final Object a1, final Object a2, final Object a3) {
    return new AST3(TimeValue, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Timing(x) - returns a list with the first entry containing the evaluation CPU time of `x` and
   * the second entry is the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Timing.md">Timing
   *      documentation</a>
   */
  public static IAST timing(final Object a1) {
    return new AST1(Timing, Object2Expr.convert(a1));
  }


  public static IAST toBoxes(final Object a1) {
    return new AST1(ToBoxes, Object2Expr.convert(a1));
  }


  public static IAST toBoxes(final Object a1, final Object a2) {
    return new AST2(ToBoxes, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ToCharacterCode(string) - converts `string` into a list of corresponding integer character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToCharacterCode.md">ToCharacterCode
   *      documentation</a>
   */
  public static IAST toCharacterCode(final Object a1) {
    return new AST1(ToCharacterCode, Object2Expr.convert(a1));
  }


  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix
   *      documentation</a>
   */
  public static IAST toeplitzMatrix(final Object a1) {
    return new AST1(ToeplitzMatrix, Object2Expr.convert(a1));
  }


  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix
   *      documentation</a>
   */
  public static IAST toeplitzMatrix(final Object a1, final Object a2) {
    return new AST2(ToeplitzMatrix, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *      documentation</a>
   */
  public static IAST toExpression(final Object a1) {
    return new AST1(ToExpression, Object2Expr.convert(a1));
  }


  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *      documentation</a>
   */
  public static IAST toExpression(final Object a1, final Object a2) {
    return new AST2(ToExpression, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *      documentation</a>
   */
  public static IAST toExpression(final Object a1, final Object a2, final Object a3) {
    return new AST3(ToExpression, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * Together(expr) - writes sums of fractions in `expr` together.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Together.md">Together
   *      documentation</a>
   */
  public static IAST together(final Object a1) {
    return new AST1(Together, Object2Expr.convert(a1));
  }


  public static IAST toIntervalData(final Object a1, final Object a2) {
    return new AST2(ToIntervalData, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ToLowerCase(string) - converts `string` into a string of corresponding lowercase character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToLowerCase.md">ToLowerCase
   *      documentation</a>
   */
  public static IAST toLowerCase(final Object a1) {
    return new AST1(ToLowerCase, Object2Expr.convert(a1));
  }


  /**
   * ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x,
   * y}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToPolarCoordinates.md">ToPolarCoordinates
   *      documentation</a>
   */
  public static IAST toPolarCoordinates(final Object a1) {
    return new AST1(ToPolarCoordinates, Object2Expr.convert(a1));
  }


  /**
   * ToSphericalCoordinates({x, y, z}) - returns the spherical coordinates for the cartesian
   * coordinates `{x, y, z}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToSphericalCoordinates.md">ToSphericalCoordinates
   *      documentation</a>
   */
  public static IAST toSphericalCoordinates(final Object a1) {
    return new AST1(ToSphericalCoordinates, Object2Expr.convert(a1));
  }


  /**
   * ToString(expr) - converts `expr` into a string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToString.md">ToString
   *      documentation</a>
   */
  public static IAST toString(final Object a1) {
    return new AST1(ToString, Object2Expr.convert(a1));
  }


  /**
   * Total(list) - adds all values in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total
   *      documentation</a>
   */
  public static IAST total(final Object a1) {
    return new AST1(Total, Object2Expr.convert(a1));
  }


  /**
   * Total(list) - adds all values in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total
   *      documentation</a>
   */
  public static IAST total(final Object a1, final Object a2) {
    return new AST2(Total, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ToUnicode(string) - converts `string` into a string of corresponding unicode character codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUnicode.md">ToUnicode
   *      documentation</a>
   */
  public static IAST toUnicode(final Object a1) {
    return new AST1(ToUnicode, Object2Expr.convert(a1));
  }


  /**
   * ToUpperCase(string) - converts `string` into a string of corresponding uppercase character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUpperCase.md">ToUpperCase
   *      documentation</a>
   */
  public static IAST toUpperCase(final Object a1) {
    return new AST1(ToUpperCase, Object2Expr.convert(a1));
  }


  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *      documentation</a>
   */
  public static IAST tr(final Object a1) {
    return new AST1(Tr, Object2Expr.convert(a1));
  }


  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *      documentation</a>
   */
  public static IAST tr(final Object a1, final Object a2) {
    return new AST2(Tr, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *      documentation</a>
   */
  public static IAST tr(final Object a1, final Object a2, final Object a3) {
    return new AST3(Tr, Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));
  }


  /**
   * Trace(expr) - return the evaluation steps which are used to get the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace
   *      documentation</a>
   */
  public static IAST trace(final Object a1) {
    return new AST1(Trace, Object2Expr.convert(a1));
  }


  /**
   * Trace(expr) - return the evaluation steps which are used to get the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace
   *      documentation</a>
   */
  public static IAST trace(final Object a1, final Object a2) {
    return new AST2(Trace, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST traceForm(final Object a1) {
    return new AST1(TraceForm, Object2Expr.convert(a1));
  }


  public static IAST traceForm(final Object a1, final Object a2) {
    return new AST2(TraceForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TranslationTransform(v) - gives a `TransformationFunction` that translates points by vector
   * `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TranslationTransform.md">TranslationTransform
   *      documentation</a>
   */
  public static IAST translationTransform(final Object a1) {
    return new AST1(TranslationTransform, Object2Expr.convert(a1));
  }


  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate
   *      documentation</a>
   */
  public static IAST transliterate(final Object a1) {
    return new AST1(Transliterate, Object2Expr.convert(a1));
  }


  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate
   *      documentation</a>
   */
  public static IAST transliterate(final Object a1, final Object a2) {
    return new AST2(Transliterate, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose
   *      documentation</a>
   */
  public static IAST transpose(final Object a1) {
    return new AST1(Transpose, Object2Expr.convert(a1));
  }


  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose
   *      documentation</a>
   */
  public static IAST transpose(final Object a1, final Object a2) {
    return new AST2(Transpose, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm
   *      documentation</a>
   */
  public static IAST treeForm(final Object a1) {
    return new AST1(TreeForm, Object2Expr.convert(a1));
  }


  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm
   *      documentation</a>
   */
  public static IAST treeForm(final Object a1, final Object a2) {
    return new AST2(TreeForm, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand
   *      documentation</a>
   */
  public static IAST trigExpand(final Object a1) {
    return new AST1(TrigExpand, Object2Expr.convert(a1));
  }


  /**
   * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms
   * of trigonometric functions with combined arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce
   *      documentation</a>
   */
  public static IAST trigReduce(final Object a1) {
    return new AST1(TrigReduce, Object2Expr.convert(a1));
  }


  public static IAST trigSimplifyFu(final Object a1) {
    return new AST1(TrigSimplifyFu, Object2Expr.convert(a1));
  }


  public static IAST trigSimplifyFu(final Object a1, final Object a2) {
    return new AST2(TrigSimplifyFu, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp
   *      documentation</a>
   */
  public static IAST trigToExp(final Object a1) {
    return new AST1(TrigToExp, Object2Expr.convert(a1));
  }


  /**
   * TrueQ(expr) - returns `True` if and only if `expr` is `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrueQ.md">TrueQ
   *      documentation</a>
   */
  public static IAST trueQ(final Object a1) {
    return new AST1(TrueQ, Object2Expr.convert(a1));
  }


  /**
   * TTest(real-vector) - Returns the *observed significance level*, or *p-value*, associated with a
   * one-sample, two-tailed t-test comparing the mean of the input vector with the constant
   * <code>0.0</code>.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TTest.md">TTest
   *      documentation</a>
   */
  public static IAST tTest(final Object a1) {
    return new AST1(TTest, Object2Expr.convert(a1));
  }


  /**
   * TTest(real-vector) - Returns the *observed significance level*, or *p-value*, associated with a
   * one-sample, two-tailed t-test comparing the mean of the input vector with the constant
   * <code>0.0</code>.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TTest.md">TTest
   *      documentation</a>
   */
  public static IAST tTest(final Object a1, final Object a2) {
    return new AST2(TTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
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


  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples
   *      documentation</a>
   */
  public static IAST tuples(final Object a1) {
    return new AST1(Tuples, Object2Expr.convert(a1));
  }


  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples
   *      documentation</a>
   */
  public static IAST tuples(final Object a1, final Object a2) {
    return new AST2(Tuples, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST unequalTo(final Object a1) {
    return new AST1(UnequalTo, Object2Expr.convert(a1));
  }


  /**
   * Unevaluated(expr) - temporarily leaves `expr` in an unevaluated form when it appears as a
   * function argument.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unevaluated.md">Unevaluated
   *      documentation</a>
   */
  public static IAST unevaluated(final Object a1) {
    return new AST1(Unevaluated, Object2Expr.convert(a1));
  }


  /**
   * UniformDistribution({min, max}) - returns a uniform distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformDistribution.md">UniformDistribution
   *      documentation</a>
   */
  public static IAST uniformDistribution(final Object a1) {
    return new AST1(UniformDistribution, Object2Expr.convert(a1));
  }


  /**
   * Unique(expr) - create a unique symbol of the form `expr$...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unique.md">Unique
   *      documentation</a>
   */
  public static IAST unique(final Object a1) {
    return new AST1(Unique, Object2Expr.convert(a1));
  }


  /**
   * UnitaryMatrixQ(U) - returns `True` if a complex square matrix `U` is unitary, that is, if its
   * conjugate transpose `U^(*)` is also its inverse, that is, if `U^(*).U = U.U^(*) = U.U^(-1) - 1
   * = I` where `I` is the identity matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitaryMatrixQ.md">UnitaryMatrixQ
   *      documentation</a>
   */
  public static IAST unitaryMatrixQ(final Object a1) {
    return new AST1(UnitaryMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *      documentation</a>
   */
  public static IAST unitConvert(final Object a1) {
    return new AST1(UnitConvert, Object2Expr.convert(a1));
  }


  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *      documentation</a>
   */
  public static IAST unitConvert(final Object a1, final Object a2) {
    return new AST2(UnitConvert, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *      documentation</a>
   */
  public static IAST unitize(final Object a1) {
    return new AST1(Unitize, Object2Expr.convert(a1));
  }


  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *      documentation</a>
   */
  public static IAST unitize(final Object a1, final Object a2) {
    return new AST2(Unitize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector
   *      documentation</a>
   */
  public static IAST unitVector(final Object a1) {
    return new AST1(UnitVector, Object2Expr.convert(a1));
  }


  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector
   *      documentation</a>
   */
  public static IAST unitVector(final Object a1, final Object a2) {
    return new AST2(UnitVector, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset
   *      documentation</a>
   */
  public static IAST unset(final Object a1) {
    return new AST1(Unset, Object2Expr.convert(a1));
  }


  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ
   *      documentation</a>
   */
  public static IAST upperCaseQ(final Object a1) {
    return new AST1(UpperCaseQ, Object2Expr.convert(a1));
  }


  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize
   *      documentation</a>
   */
  public static IAST upperTriangularize(final Object a1) {
    return new AST1(UpperTriangularize, Object2Expr.convert(a1));
  }


  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize
   *      documentation</a>
   */
  public static IAST upperTriangularize(final Object a1, final Object a2) {
    return new AST2(UpperTriangularize, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * UpperTriangularMatrixQ(matrix) - returns `True` if `matrix` is upper triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularMatrixQ.md">UpperTriangularMatrixQ
   *      documentation</a>
   */
  public static IAST upperTriangularMatrixQ(final Object a1) {
    return new AST1(UpperTriangularMatrixQ, Object2Expr.convert(a1));
  }


  /**
   * UpperTriangularMatrixQ(matrix) - returns `True` if `matrix` is upper triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularMatrixQ.md">UpperTriangularMatrixQ
   *      documentation</a>
   */
  public static IAST upperTriangularMatrixQ(final Object a1, final Object a2) {
    return new AST2(UpperTriangularMatrixQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST upSet(final Object a1, final Object a2) {
    return new AST2(UpSet, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST upSetDelayed(final Object a1, final Object a2) {
    return new AST2(UpSetDelayed, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues
   *      documentation</a>
   */
  public static IAST upValues(final Object a1) {
    return new AST1(UpValues, Object2Expr.convert(a1));
  }


  public static IAST uRLFetch(final Object a1) {
    return new AST1(URLFetch, Object2Expr.convert(a1));
  }


  /**
   * ValueQ(expr) - returns `True` if and only if `expr` is defined.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValueQ.md">ValueQ
   *      documentation</a>
   */
  public static IAST valueQ(final Object a1) {
    return new AST1(ValueQ, Object2Expr.convert(a1));
  }


  /**
   * Values(association) - return a list of values of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values
   *      documentation</a>
   */
  public static IAST values(final Object a1) {
    return new AST1(Values, Object2Expr.convert(a1));
  }


  /**
   * Values(association) - return a list of values of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values
   *      documentation</a>
   */
  public static IAST values(final Object a1, final Object a2) {
    return new AST2(Values, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VandermondeMatrix.md">VandermondeMatrix
   *      documentation</a>
   */
  public static IAST vandermondeMatrix(final Object a1) {
    return new AST1(VandermondeMatrix, Object2Expr.convert(a1));
  }


  /**
   * Variables(expr) - gives a list of the variables that appear in the polynomial `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variables.md">Variables
   *      documentation</a>
   */
  public static IAST variables(final Object a1) {
    return new AST1(Variables, Object2Expr.convert(a1));
  }


  /**
   * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or
   * symbols. Numerical values may be real or complex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variance.md">Variance
   *      documentation</a>
   */
  public static IAST variance(final Object a1) {
    return new AST1(Variance, Object2Expr.convert(a1));
  }


  /**
   * VectorAngle(u, v) - gives the angles between vectors `u` and `v`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAngle.md">VectorAngle
   *      documentation</a>
   */
  public static IAST vectorAngle(final Object a1, final Object a2) {
    return new AST2(VectorAngle, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST vectorGreater(final Object a1) {
    return new AST1(VectorGreater, Object2Expr.convert(a1));
  }


  public static IAST vectorGreaterEqual(final Object a1) {
    return new AST1(VectorGreaterEqual, Object2Expr.convert(a1));
  }


  public static IAST vectorLess(final Object a1) {
    return new AST1(VectorLess, Object2Expr.convert(a1));
  }


  public static IAST vectorLessEqual(final Object a1) {
    return new AST1(VectorLessEqual, Object2Expr.convert(a1));
  }


  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ
   *      documentation</a>
   */
  public static IAST vectorQ(final Object a1) {
    return new AST1(VectorQ, Object2Expr.convert(a1));
  }


  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ
   *      documentation</a>
   */
  public static IAST vectorQ(final Object a1, final Object a2) {
    return new AST2(VectorQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *      documentation</a>
   */
  public static IAST verificationTest(final Object a1) {
    return new AST1(VerificationTest, Object2Expr.convert(a1));
  }


  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *      documentation</a>
   */
  public static IAST verificationTest(final Object a1, final Object a2) {
    return new AST2(VerificationTest, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *      documentation</a>
   */
  public static IAST verificationTest(final Object a1, final Object a2, final Object a3) {
    return new AST3(VerificationTest, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's
   * the length of the longest shortest path from the `vertex` to every other vertex in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexEccentricity.md">VertexEccentricity
   *      documentation</a>
   */
  public static IAST vertexEccentricity(final Object a1, final Object a2) {
    return new AST2(VertexEccentricity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * VertexList(graph) - convert the `graph` into a list of vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList
   *      documentation</a>
   */
  public static IAST vertexList(final Object a1) {
    return new AST1(VertexList, Object2Expr.convert(a1));
  }


  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ
   *      documentation</a>
   */
  public static IAST vertexQ(final Object a1, final Object a2) {
    return new AST2(VertexQ, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST volume(final Object a1) {
    return new AST1(Volume, Object2Expr.convert(a1));
  }


  public static IAST weaklyConnectedGraphQ(final Object a1) {
    return new AST1(WeaklyConnectedGraphQ, Object2Expr.convert(a1));
  }


  public static IAST weberE(final Object a1, final Object a2) {
    return new AST2(WeberE, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST weberE(final Object a1, final Object a2, final Object a3) {
    return new AST3(WeberE, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution
   *      documentation</a>
   */
  public static IAST weibullDistribution(final Object a1, final Object a2) {
    return new AST2(WeibullDistribution, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution
   *      documentation</a>
   */
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


  /**
   * WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeierstrassP.md">WeierstrassP
   *      documentation</a>
   */
  public static IAST weierstrassP(final Object a1, final Object a2) {
    return new AST2(WeierstrassP, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST weierstrassPPrime(final Object a1, final Object a2) {
    return new AST2(WeierstrassPPrime, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * WeightedAdjacencyMatrix(graph) - convert the `graph` into a weighted adjacency matrix in sparse
   * array format.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedAdjacencyMatrix.md">WeightedAdjacencyMatrix
   *      documentation</a>
   */
  public static IAST weightedAdjacencyMatrix(final Object a1) {
    return new AST1(WeightedAdjacencyMatrix, Object2Expr.convert(a1));
  }


  /**
   * WeightedGraphQ(expr) - test if `expr` is an explicit weighted graph object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedGraphQ.md">WeightedGraphQ
   *      documentation</a>
   */
  public static IAST weightedGraphQ(final Object a1) {
    return new AST1(WeightedGraphQ, Object2Expr.convert(a1));
  }


  public static IAST wheelGraph(final Object a1) {
    return new AST1(WheelGraph, Object2Expr.convert(a1));
  }


  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While
   *      documentation</a>
   */
  public static IAST $while(final Object a1) {
    return new AST1(While, Object2Expr.convert(a1));
  }


  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While
   *      documentation</a>
   */
  public static IAST $while(final Object a1, final Object a2) {
    return new AST2(While, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST whittakerM(final Object a1, final Object a2, final Object a3) {
    return new AST3(WhittakerM, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST whittakerW(final Object a1, final Object a2, final Object a3) {
    return new AST3(WhittakerW, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With
   *      documentation</a>
   */
  public static IAST with(final Object a1, final Object a2) {
    return new AST2(With, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With
   *      documentation</a>
   */
  public static IAST with(final Object a1, final Object a2, final Object a3) {
    return new AST3(With, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }


  public static IAST write(final Object a1, final Object a2) {
    return new AST2(Write, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  public static IAST writeString(final Object a1, final Object a2) {
    return new AST2(WriteString, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/YuleDissimilarity.md">YuleDissimilarity
   *      documentation</a>
   */
  public static IAST yuleDissimilarity(final Object a1, final Object a2) {
    return new AST2(YuleDissimilarity, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * Zeta(z) - returns the Riemann zeta function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta
   *      documentation</a>
   */
  public static IAST zeta(final Object a1) {
    return new AST1(Zeta, Object2Expr.convert(a1));
  }


  /**
   * Zeta(z) - returns the Riemann zeta function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta
   *      documentation</a>
   */
  public static IAST zeta(final Object a1, final Object a2) {
    return new AST2(Zeta, Object2Expr.convert(a1), Object2Expr.convert(a2));
  }


  /**
   * ZTransform(x,n,z) - returns the Z-Transform of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZTransform.md">ZTransform
   *      documentation</a>
   */
  public static IAST zTransform(final Object a1, final Object a2, final Object a3) {
    return new AST3(ZTransform, Object2Expr.convert(a1), Object2Expr.convert(a2),
        Object2Expr.convert(a3));
  }

}
