package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The shape argument and the elements of ArrayReshape. */
public class ArrayReshapeShapeTest extends ExprEvaluatorTestCase {

  @Test
  public void testIntegerShape() {
    check("ArrayReshape({p, q, r, s, t, u}, 4)", //
        "{p,q,r,s}");
    check("ArrayReshape({p, q}, 4, z)", //
        "{p,q,z,z}");
    check("ArrayReshape({p, q}, 0)", //
        "{}");
  }

  @Test
  public void testElementsOfANestedList() {
    // the elements are read in the order of a full flattening, also from a ragged list
    check("ArrayReshape({{p}, {q, r}, {s}}, {2, 2})", //
        "{{p,q},{r,s}}");
    check("ArrayReshape({{p, q}, {r, s}}, {4})", //
        "{p,q,r,s}");
    check("ArrayReshape({{p, q}, {r, s}}, {3, 2}, z)", //
        "{{p,q},{r,s},{z,z}}");
  }

  @Test
  public void testRankZero() {
    check("ArrayReshape({p, q}, {})", //
        "p");
    check("ArrayReshape({}, {}, z)", //
        "z");
    check("ArrayReshape({}, {3})", //
        "{0,0,0}");
  }

  @Test
  public void testInvalidArguments() {
    check("ArrayReshape(y, {3})", //
        "ArrayReshape(y,{3})");
    check("Check(ArrayReshape(y, {3}), failed, ArrayReshape::listrp)", //
        "failed");
    check("ArrayReshape({p, q}, {1.5})", //
        "ArrayReshape({p,q},{1.5})");
    check("Check(ArrayReshape({p, q}, {1.5}), failed, ArrayReshape::ilsmn)", //
        "failed");
    check("Check(ArrayReshape({p, q}, {-2}), failed, ArrayReshape::ilsmn)", //
        "failed");
    check("Check(ArrayReshape({p, q}, n), failed, ArrayReshape::ilsmn)", //
        "failed");
  }
}
