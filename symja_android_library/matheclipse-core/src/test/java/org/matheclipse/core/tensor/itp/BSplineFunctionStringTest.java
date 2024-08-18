package org.matheclipse.core.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.sca.Clips;

// cubic basis functions over unit interval [0, 1]
// {(1 - t)^3, 4 - 6 t^2 + 3 t^3, 1 + 3 t + 3 t^2 - 3 t^3, t^3}/6
class BSplineFunctionStringTest {
  @Test
  void testConstant() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(0, F.List(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(F.num(0)), F.ZZ(2));
    assertEquals(bSplineFunction.apply(F.num(0.3333)), F.ZZ(2));
    assertEquals(bSplineFunction.apply(F.num(0.5)), F.ZZ(1));
    assertEquals(bSplineFunction.apply(F.num(1.25)), F.ZZ(1));
    assertEquals(bSplineFunction.apply(F.num(1.49)), F.ZZ(1));
    assertEquals(bSplineFunction.apply(F.num(1.75)), F.ZZ(5));
    assertEquals(bSplineFunction.apply(F.num(2)), F.ZZ(5));
    assertEquals(bSplineFunction.apply(F.num(2.50)), F.ZZ(0));
    assertEquals(bSplineFunction.apply(F.num(3)), F.ZZ(0));
    assertEquals(bSplineFunction.apply(F.num(3.5)), F.ZZ(-2));
    assertEquals(bSplineFunction.apply(F.num(4)), F.ZZ(-2));
  }

  @Test
  void testLinear() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(1, F.List(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(F.num(0)), F.num(2.0));
    assertEquals(bSplineFunction.apply(F.QQ(1, 2)), F.QQ(3, 2));
    assertEquals(bSplineFunction.apply(F.num(1)), F.num(1.0));
    assertEquals(bSplineFunction.apply(F.num(1.25)), F.num(2));
    assertEquals(bSplineFunction.apply(F.num(1.50)), F.num(3));
    assertEquals(bSplineFunction.apply(F.num(1.75)), F.num(4));
    assertEquals(bSplineFunction.apply(F.num(2)), F.num(5.0));
    assertEquals(bSplineFunction.apply(F.num(2.50)), F.num(2.5));
    assertEquals(bSplineFunction.apply(F.num(3)), F.num(0.0));
    assertEquals(bSplineFunction.apply(F.num(3.5)), F.num(-1));
    assertEquals(bSplineFunction.apply(F.num(4)), F.ZZ(-2));
  }

  @Test
  void testLinearCurve() {
    IAST control = F.List(F.List(2, 3), F.List(1, 0), F.List(5, 7), F.List(0, 0), F.List(-2, 3));
    // IAST control = Tensors.fromString("{{2, 3}, {1, 0}, {5, 7}, {0, 0}, {-2, 3}}");
    assertTrue(S.MatrixQ.ofQ(control));
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(1, control);
    assertEquals(bSplineFunction.apply(F.num(1.5)), F.List(3, 3.5));
    assertEquals(bSplineFunction.apply(F.num(4)), F.List(-2, 3));
  }

  @Test
  void testQuadratic() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(2, F.List(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(F.num(0)), F.num(2.0));
    assertEquals(bSplineFunction.apply(F.num(1)), F.num(5.0 / 3.0));
    assertEquals(bSplineFunction.apply(F.num(2)), F.num(31.0 / 8.0));
    assertEquals(bSplineFunction.apply(F.num(4)), F.ZZ(-2));
  }

  @Test
  void testQuadraticSymmetry() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(2, F.List(0, 1, 2, 3));
    IExpr r1 = bSplineFunction.apply(F.num(0.5));
    IExpr r2 = bSplineFunction.apply(F.num(1.5));
    IExpr r3 = bSplineFunction.apply(F.num(2.5)); // does not evaluate correctly
    assertTrue(F.isFuzzyEquals(r1.evalf(), 1.0 / 3.0, Config.DOUBLE_TOLERANCE));
    assertEquals(r2.toString(), //
        "1.5");
    assertTrue(F.isFuzzyEquals(r3.evalf(), 8.0 / 3.0, Config.DOUBLE_TOLERANCE));
  }

  @Test
  void testCubic() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, F.List(2, 1, 5, 0, -2));
    assertEquals(bSplineFunction.apply(F.num(0)), F.num(2.0));
    assertEquals(bSplineFunction.apply(F.num(0.5)), F.num(173.0 / 96.0));
    assertEquals(bSplineFunction.apply(F.num(1)), F.num(23.0 / 12.0));
    assertEquals(bSplineFunction.apply(F.num(4)), F.ZZ(-2));
  }

  @Test
  void testCubicLinear() {
    Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, F.List(2, 1, 0, -1, -2));
    assertEquals(bSplineFunction.apply(F.num(0)), F.num(2.0));
    assertEquals(bSplineFunction.apply(F.num(1)), F.num(13.0 / 12.0));
    assertEquals(bSplineFunction.apply(F.num(2)), F.num(0));
    assertTrue(F.isFuzzyEquals(bSplineFunction.apply(F.num(3.999999999999)).evalf(), -2.0,
        Config.DOUBLE_TOLERANCE));
    // Tolerance.CHOP.requireClose(bSplineFunction.apply(F.num(3.999999999999)), F.num(-2));
    assertEquals(bSplineFunction.apply(F.num(4)), F.ZZ(-2));
  }

  @Test
  void testSymmetric() {
    IAST control = F.List(1, 5, 3, -1, 0);
    int n = control.argSize() - 1;
    for (int degree = 0; degree <= 5; ++degree) {
      Function<IExpr, IExpr> bsf = BSplineFunctionString.of(degree, control);
      Function<IExpr, IExpr> bsr = BSplineFunctionString.of(degree, (IAST) S.Reverse.of(control));
      IAST res1f = F.subdivide(0, n, 10).map(bsf);
      IAST res1r = F.subdivide(n, 0, 10).map(bsr);
      System.out.println(res1f);
      // degree 0: {1, 1, 5, 5, 3, 3, 3, -1, -1, 0, 0}
      IExpr zeroDifference = EvalEngine.get().evalN(F.Total(res1f.subtract(res1r)));
      assertTrue(F.isFuzzyEquals(zeroDifference.evalf(), 0.0, Config.DEFAULT_CHOP_DELTA));
    }
  }
  //
  // @Test
  // void testQuantity() {
  // IAST control = Tensors.fromString("{2[m], 7[m], 3[m]}");
  // Clip clip = Clips.interval(2, 7);
  // int n = control.length() - 1;
  // for (int degree = 0; degree <= 5; ++degree) {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(degree, control);
  // IAST tensor = Subdivide.of(0, n, 10).map(bSplineFunction);
  // VectorQ.require(tensor);
  // IAST nounit = tensor.map(QuantityMagnitude.SI().in("m"));
  // ExactTensorQ.require(nounit);
  // nounit.map(clip::requireInside);
  // }
  // }

  @Test
  void testSingleton() {
    for (int degree = 0; degree < 4; ++degree) {
      Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(degree, F.List(99));
      assertEquals(bSplineFunction.apply(F.CD0), F.ZZ(99));
      assertEquals(bSplineFunction.apply(F.num(0.0)), F.ZZ(99));
    }
  }

  @Test
  void testIndex() {
    EvalEngine engine = EvalEngine.get();
    for (int degree = 0; degree <= 5; ++degree) {
      for (int length = 2; length <= 6; ++length) {
        Function<IExpr, IExpr> bSplineFunction =
            BSplineFunctionString.of(degree, (IAST) S.IdentityMatrix.of(engine, length));

        IAST subDivide = F.subdivide(0, length - 1, 13);
        for (IExpr x : subDivide) {
          IAST tensor = (IAST) bSplineFunction.apply(x);
          assertTrue(tensor.stream().map(IExpr.class::cast).allMatch(Clips.unit()::isInside));
          assertTrue(//
              F.isFuzzyEquals(1.0, //
                  engine.evalN(F.Total(tensor)).evalf(), //
                  Config.DOUBLE_TOLERANCE));
          // ExactTensorQ.require(tensor);
        }
      }
    }
  }

  //
  // @Test
  // void testSerializable() throws Exception {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, F.List(2, 1, 0, -1, -2));
  // IAST value0 = bSplineFunction.apply(F.num(2.3));
  // Function<IExpr, IExpr> copy = Serialization.copy(bSplineFunction);
  // IAST value1 = copy.apply(F.num(2.3));
  // assertEquals(value0, value1);
  // }
  //
  // @Test
  // void testSymmetry() {
  // Distribution distribution = DiscreteUniformDistribution.of(-4, 7);
  // int n = 20;
  // IAST domain = Subdivide.of(0, n - 1, 31);
  // for (int degree = 1; degree < 8; ++degree) {
  // IAST control = RandomVariate.of(distribution, n, 3);
  // Function<IExpr, IExpr> mapForward = BSplineFunctionString.of(degree, control);
  // IAST forward = domain.map(mapForward);
  // Function<IExpr, IExpr> mapReverse = BSplineFunctionString.of(degree, Reverse.of(control));
  // IAST reverse = Reverse.of(domain.map(mapReverse));
  // assertEquals(forward, reverse);
  // ExactTensorQ.require(forward);
  // ExactTensorQ.require(reverse);
  // }
  // }
  //
  // @Test
  // void testBasisWeights1a() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(1, UnitVector.of(3, 1));
  // IAST limitMask = Range.of(1, 2).map(bSplineFunction);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1}"));
  // }
  //
  // @Test
  // void testBasisWeights2() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(2, UnitVector.of(5, 2));
  // IAST limitMask = Range.of(1, 4).map(bSplineFunction);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/8, 3/4, 1/8}"));
  // }
  //
  // @Test
  // void testBasisWeights3a() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, UnitVector.of(7, 3));
  // IAST limitMask = Range.of(2, 5).map(bSplineFunction);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  // }
  //
  // @Test
  // void testBasisWeights3b() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, UnitVector.of(5, 2));
  // IAST limitMask = Range.of(1, 4).map(bSplineFunction);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/6, 2/3, 1/6}"));
  // }
  //
  // @Test
  // void testBasisWeights4() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(4, UnitVector.of(9, 4));
  // IAST limitMask = Range.of(2, 7).map(bSplineFunction);
  // assertEquals(Total.of(limitMask), RealScalar.ONE);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/384, 19/96, 115/192, 19/96, 1/384}"));
  // }
  //
  // @Test
  // void testBasisWeights5a() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(11, 5));
  // IAST limitMask = Range.of(3, 8).map(bSplineFunction);
  // assertEquals(Total.of(limitMask), RealScalar.ONE);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  // }
  //
  // @Test
  // void testBasisWeights5b() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(9, 4));
  // IAST limitMask = Range.of(2, 7).map(bSplineFunction);
  // assertEquals(Total.of(limitMask), RealScalar.ONE);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  // }
  //
  // @Test
  // void testBasisWeights5c() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(5, UnitVector.of(7, 3));
  // IAST limitMask = Range.of(1, 6).map(bSplineFunction);
  // assertEquals(Total.of(limitMask), RealScalar.ONE);
  // ExactTensorQ.require(limitMask);
  // assertEquals(limitMask, Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
  // }
  //
  // @Test
  // void testEmptyFail() {
  // assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-2, F.CEmptyList));
  // assertThrows(IllegalArgumentException.class, () -> BSplineFunctionString.of(-1, F.CEmptyList));
  // assertThrows(Throw.class, () -> BSplineFunctionString.of(-0, F.CEmptyList));
  // assertThrows(Throw.class, () -> BSplineFunctionString.of(+1, F.CEmptyList));
  // assertThrows(Throw.class, () -> BSplineFunctionString.of(+2, F.CEmptyList));
  // }
  //
  // @Test
  // void testNegativeFail() {
  // assertThrows(IllegalArgumentException.class,
  // () -> BSplineFunctionString.of(-1, F.List(1, 2, 3, 4)));
  // }
  //
  // @Test
  // void testOutsideFail() {
  // Function<IExpr, IExpr> bSplineFunction = BSplineFunctionString.of(3, F.List(2, 1, 0, -1, -2));
  // bSplineFunction.apply(F.num(4));
  // assertThrows(Throw.class, () -> bSplineFunction.apply(F.num(-0.1)));
  // assertThrows(Throw.class, () -> bSplineFunction.apply(F.num(5.1)));
  // assertThrows(Throw.class, () -> bSplineFunction.apply(F.num(4.1)));
  // }
}
