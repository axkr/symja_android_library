package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ResolveTest extends ExprEvaluatorTestCase {

  @Test
  public void testResolveExistsReals() {
    check("Resolve(Exists(x, x^2 == 4), Reals)", //
        "True");
    check("Resolve(Exists(x, x^2 == -1), Reals)", //
        "False");
    check("Resolve(Exists(x, x > 0 && x < 1))", //
        "True");
    check("Resolve(Exists({x}, x^2 < 1))", //
        "True");
  }

  @Test
  public void testResolveForAllReals() {
    check("Resolve(ForAll(x, x^2 >= 0), Reals)", //
        "True");
    check("Resolve(ForAll(x, x^2 > 0), Reals)", //
        "False");
  }

  @Test
  public void testResolveParametric() {
    // the extrema of the polynomial depend on the free parameter c
    check("Resolve(Exists(x, x^2 == c), Reals)", //
        "c>=0");
    check("Resolve(ForAll(x, x^2 + c > 0), Reals)", //
        "c>0");
  }

  @Test
  public void testResolveComplexes() {
    // no domain and no inequality: the variables default to the complexes, where a non constant
    // polynomial always has a root
    check("Resolve(Exists({x, y}, x^2 + y^2 == -1))", //
        "True");
    check("Resolve(Exists({x, y}, x^2 + y^2 == -1), Reals)", //
        "False");
  }

  @Test
  public void testResolveExistsMultivariate() {
    check("Resolve(Exists({x, y}, x^2 + y^2 < 1))", //
        "True");
    check("Resolve(Exists({x, y}, x^2 + y^2 < 1), Reals)", //
        "True");
    check("Resolve(Exists({x, y}, x^2 + y^2 < -1))", //
        "False");
    check("Resolve(Exists({x, y}, x^2 - y^2 == 5), Reals)", //
        "True");
    check("Resolve(Exists({x, y, z}, x^2 + y^2 + z^2 < 1), Reals)", //
        "True");
  }

  @Test
  public void testResolveForAllMultivariate() {
    check("Resolve(ForAll({x, y}, x^2 + y^2 >= 0), Reals)", //
        "True");
    check("Resolve(ForAll({x, y}, x^2 + y^2 > 0), Reals)", //
        "False");
    check("Resolve(ForAll({x, y}, x^2 + y^2 + 1 > 0), Reals)", //
        "True");
    check("Resolve(ForAll({x, y}, x^2 - y^2 > 0), Reals)", //
        "False");
    check("Resolve(ForAll({x, y}, x^2 + y^2 != -1), Reals)", //
        "True");
  }

  @Test
  public void testResolveQuantifierFree() {
    // an expression without quantifiers is returned unchanged
    check("Resolve(x > 0)", //
        "x>0");
    check("Resolve(True)", //
        "True");
  }

  @Test
  public void testResolveSolutionSetFallback() {
    // quantifiers which the extremum analysis cannot decide fall back on the solution set which
    // `Reduce` computes for the condition - `Reduce` delegates here, so both must agree
    check("Resolve(Exists(x, x^3 + a*x + b == 0))", //
        "True");
    check("Reduce(Exists(x, x^3 + a*x + b == 0))", //
        "True");
    check("Resolve(ForAll(x, x^2 - 2*x + 1 >= 0))", //
        "True");
    check("Reduce(ForAll(x, x^2 - 2*x + 1 >= 0))", //
        "True");
    // a power of a non zero base is never zero, so the existence claim is refuted
    check("Resolve(Exists(x, E^x == 0))", //
        "False");
    check("Reduce(Exists(x, E^x == 0))", //
        "False");
  }
}
