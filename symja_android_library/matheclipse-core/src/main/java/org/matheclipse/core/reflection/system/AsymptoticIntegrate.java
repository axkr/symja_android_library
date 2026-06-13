package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AsymptoticIntegrate extends AbstractFunctionOptionEvaluator {

  public AsymptoticIntegrate() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    IExpr integrand = ast.arg1();
    IExpr integrateSpec = ast.arg2();
    IExpr asympSpec = ast.arg3();

    // 1. Parse the asymptotic specification into variable, point, and order
    IExpr asympVar = F.NIL;
    IExpr asympPoint = F.NIL;

    // Default to 1 to compute the leading asymptotic term if no order is specified
    int order = 1;

    if (asympSpec.isRule()) {
      asympVar = asympSpec.first();
      asympPoint = asympSpec.second();
    } else if (asympSpec.isList() && ((IAST) asympSpec).argSize() >= 2) {
      IAST listSpec = (IAST) asympSpec;
      asympVar = listSpec.arg1();
      asympPoint = listSpec.arg2();
      if (listSpec.argSize() >= 3 && listSpec.arg3().isInteger()) {
        order = listSpec.arg3().toIntDefault();
      }
    } else if (asympSpec.isSymbol()) {
      asympVar = asympSpec;
      asympPoint = S.Infinity;
    }

    if (asympVar.isNIL() || asympPoint.isNIL()) {
      return F.NIL;
    }

    // Determine if the asymptotic variable bounds the integration domain
    boolean isDefiniteWithAsympBounds = false;
    if (integrateSpec.isList()) {
      IAST list = (IAST) integrateSpec;
      if (list.argSize() >= 2 && !list.arg2().isFree(asympVar)) {
        isDefiniteWithAsympBounds = true;
      }
      if (list.argSize() >= 3 && !list.arg3().isFree(asympVar)) {
        isDefiniteWithAsympBounds = true;
      }
    }

    // Indefinite integration strictly limits to 'order'.
    // Definite integration with an asymptotic bound mathematically increases the degree by 1.
    int exactResultOrder = order;
    if (isDefiniteWithAsympBounds) {
      exactResultOrder++;
    }

    IAST seriesSpec = F.List(asympVar, asympPoint, F.ZZ(exactResultOrder));

    // ==============================================================================
    // STRATEGY 1: EXACT SOLUTION -> SERIES EXPANSION
    // Try computing the exact integral first. The order is applied strictly to the
    // resulting expression based on the boundary rule above.
    // ==============================================================================
    IExpr exactIntegral = engine.evaluate(F.Integrate(integrand, integrateSpec));

    if (exactIntegral.isPresent() && !exactIntegral.isAST(S.Integrate)) {
      IExpr seriesRes = engine.evaluate(F.Series(exactIntegral, seriesSpec));

      if (seriesRes.isPresent() && !seriesRes.isAST(S.Series)) {
        return engine.evaluate(seriesRes.normal(false));
      }
    }

    // ==============================================================================
    // STRATEGY 2: INTEGRAND EXPANSION (Term-by-term integration)
    // Invoked if Exact Integration fails. We compute the asymptotic expansion of
    // the integrand first, integrate, and then enforce the target order limit.
    // ==============================================================================
    IExpr integrandExpansionVar = asympVar;
    IAST integrandSeriesSpec = F.List(integrandExpansionVar, asympPoint, F.ZZ(order));
    IExpr expandedIntegrand = engine.evaluate(F.Series(integrand, integrandSeriesSpec));

    if (expandedIntegrand.isPresent() && !expandedIntegrand.isAST(S.Series)) {
      IExpr normalIntegrand = engine.evaluate(expandedIntegrand.normal(false));
      IExpr termByTermIntegral = engine.evaluate(F.Integrate(normalIntegrand, integrateSpec));

      if (termByTermIntegral.isPresent() && !termByTermIntegral.isAST(S.Integrate)) {
        // Truncate the term-by-term integral strictly to the calculated target order
        IExpr truncatedSeries = engine.evaluate(F.Series(termByTermIntegral, seriesSpec));
        if (truncatedSeries.isPresent() && !truncatedSeries.isAST(S.Series)) {
          return engine.evaluate(truncatedSeries.normal(false));
        }
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions, S.Method, S.PerformanceGoal},
        new IExpr[] {S.$Assumptions, S.Automatic, S.Automatic, S.$PerformanceGoal});
  }
}