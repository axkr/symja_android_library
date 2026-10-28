package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * One goodness-of-fit test of {@link DistributionFitTest} as a function of its own:
 * <code>AndersonDarlingTest</code>, <code>CramerVonMisesTest</code>, <code>KuiperTest</code>,
 * <code>PearsonChiSquareTest</code> and <code>WatsonUSquareTest</code>.
 *
 * <p>
 * <code>test(data, dist)</code> is the p-value of the test of <code>data</code> against the fully
 * specified distribution <code>dist</code>, <code>test(data, dist, "property")</code> one of the
 * properties <code>"PValue"</code>, <code>"TestStatistic"</code>, <code>"TestData"</code> or
 * <code>"HypothesisTestData"</code>. The Pearson test also accepts a distribution with symbolic
 * parameters, which are estimated from the data at the cost of one degree of freedom each, and
 * <code>Automatic</code> (or no distribution) for a normal distribution with estimated mean and
 * standard deviation.
 */
public class GoodnessOfFitTest extends AbstractFunctionEvaluator {

  /** The test's name in {@link DistributionFitTest#TESTS}. */
  private final String test;

  public GoodnessOfFitTest(String test) {
    this.test = test;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr data = ast.arg1();
    double[] values = data.isList() ? data.toDoubleVector() : null;
    if (values == null || values.length == 0) {
      return F.NIL;
    }
    IExpr distribution = ast.argSize() >= 2 ? ast.arg2() : S.Automatic;
    int estimated = 0;
    boolean pearson = test.equals("PearsonChiSquare");
    if (distribution == S.Automatic
        || distribution.isAST() && ((IAST) distribution).exists(x -> x.isVariable())) {
      if (!pearson) {
        return F.NIL;
      }
      IAST model = distribution == S.Automatic ? F.NormalDistribution(F.Dummy("m"), F.Dummy("s"))
          : (IAST) distribution;
      if (!model.isDistribution()) {
        return F.NIL;
      }
      IExpr rules = MaximumLikelihood.estimate(F.binaryAST2(S.FindDistributionParameters, data,
          model), engine);
      if (!rules.isList()) {
        return F.NIL;
      }
      estimated = rules.argSize();
      distribution = F.subst(model, (IAST) rules);
    }
    double[] u = DistributionFitTest.cdfValues(values, distribution, engine);
    if (u == null) {
      return F.NIL;
    }
    if (test.equals("CramerVonMises") && u.length < DistributionFitTest.CRAMER_VON_MISES_MINIMUM) {
      // The `1` test is only valid for sample sizes between `2` and `3`.
      Errors.printMessage(ast.topHead(), "htdrng", F.List(F.stringx(test),
          F.ZZ(DistributionFitTest.CRAMER_VON_MISES_MINIMUM), F.CInfinity), engine);
    }
    double[] result = pearson ? DistributionFitTest.pearsonChiSquare(u, estimated)
        : DistributionFitTest.test(test, u);
    if (Double.isNaN(result[1])) {
      return F.NIL;
    }
    IExpr property = ast.argSize() == 3 ? ast.arg3() : F.stringx("PValue");
    if (!property.isString()) {
      return F.NIL;
    }
    switch (property.toString()) {
      case "PValue":
        return F.num(result[1]);
      case "TestStatistic":
        return F.num(result[0]);
      case "TestData":
        return F.List(F.num(result[0]), F.num(result[1]));
      case "HypothesisTestData":
        return DistributionFitTest.hypothesisTestData(u, distribution);
      default:
        // The argument `1` is not a valid property.
        return Errors.printMessage(ast.topHead(), "invprp", F.list(property), engine);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
