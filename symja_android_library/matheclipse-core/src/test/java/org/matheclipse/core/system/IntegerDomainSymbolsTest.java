package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The domains PositiveIntegers, NonNegativeIntegers and NegativeIntegers in Element and in assumptions. */
public class IntegerDomainSymbolsTest extends ExprEvaluatorTestCase {

  @Test
  public void testElement() {
    check("Element(4, PositiveIntegers)", //
        "True");
    check("Element(0, PositiveIntegers)", //
        "False");
    check("Element(-7, PositiveIntegers)", //
        "False");
    check("Element(5/2, PositiveIntegers)", //
        "False");
    check("Element(Sqrt(9), PositiveIntegers)", //
        "True");
    check("Element(0, NonNegativeIntegers)", //
        "True");
    check("Element(12, NonNegativeIntegers)", //
        "True");
    check("Element(-1, NonNegativeIntegers)", //
        "False");
    check("Element(-3, NegativeIntegers)", //
        "True");
    check("Element(0, NegativeIntegers)", //
        "False");
    check("Element(2.0, NegativeIntegers)", //
        "False");
    check("Element(Pi, PositiveIntegers)", //
        "False");
    check("Element(I, PositiveIntegers)", //
        "False");
    check("Element(m, PositiveIntegers)", //
        "m∈PositiveIntegers");
    check("Element({1, 2, 3}, PositiveIntegers)", //
        "True");
    check("Element({1, -2, 3}, PositiveIntegers)", //
        "False");
    check("Element({1, k}, PositiveIntegers)", //
        "k∈Integers&&k>0");
    check("Element(m, PositiveReals)", //
        "m∈PositiveReals");
  }

  @Test
  public void testRefine() {
    check("Refine(m > 0, Element(m, PositiveIntegers))", //
        "True");
    check("Refine(m >= 1, Element(m, PositiveIntegers))", //
        "True");
    check("Refine(m < 0, Element(m, NonNegativeIntegers))", //
        "False");
    check("Refine(Element(m, Integers), Element(m, NegativeIntegers))", //
        "True");
    check("Refine(Sin(m*Pi), Element(m, PositiveIntegers))", //
        "0");
    check("Refine(Cos(2*m*Pi), Element(m, NonNegativeIntegers))", //
        "1");
    check("Refine((-1)^(2*m), Element(m, NegativeIntegers))", //
        "1");
    check("Refine(Sqrt(m^2), Element(m, PositiveIntegers))", //
        "m");
    check("Refine(Abs(m), Element(m, NegativeIntegers))", //
        "-m");
    check("Refine(Element(m, PositiveIntegers), Element(m, PositiveIntegers))", //
        "True");
    check("Refine(Element(m, PositiveIntegers), m > 3 && Element(m, Integers))", //
        "True");
    check("Refine(m > 0, Element({m, n}, PositiveIntegers))", //
        "True");
  }

  @Test
  public void testSimplify() {
    check("Simplify(Floor(m), Element(m, NonNegativeIntegers))", //
        "m");
    check("Simplify(Sin(n*Pi) + Abs(n), Element(n, PositiveIntegers))", //
        "n");
  }

  @Test
  public void testAssuming() {
    check("Assuming(Element(m, PositiveIntegers), Refine(m > -2))", //
        "True");
  }
}
