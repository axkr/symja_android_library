package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ClassifierFunctionExpr;
import org.matheclipse.core.expression.data.ClassifierMeasurementsObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * <code>ClassifierMeasurements(c, test)
 * ClassifierMeasurements(c, test, prop)
 * </code>
 * </pre>
 *
 * <p>
 * measures the classifier <code>c</code> on the test set <code>test</code>, given in one of the
 * forms of the training data of <code>Classify</code>. The first form gives a
 * <code>ClassifierMeasurementsObject</code> which is asked for properties, the second the
 * property <code>prop</code> (or a list of them) directly; see
 * {@link ClassifierMeasurementsObjectExpr}.
 */
public class ClassifierMeasurements extends AbstractEvaluator {

  public ClassifierMeasurements() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();
    if (head instanceof ClassifierMeasurementsObjectExpr) {
      return ((ClassifierMeasurementsObjectExpr) head).evaluate(ast, engine);
    }
    if (head != S.ClassifierMeasurements || ast.argSize() < 2 || ast.argSize() > 3
        || !(ast.arg1() instanceof ClassifierFunctionExpr)) {
      return F.NIL;
    }
    IAST testSet = Classify.trainingSet(ast.arg2());
    if (testSet.isNIL()) {
      return F.NIL;
    }
    try {
      ClassifierMeasurementsObjectExpr measurements = ClassifierMeasurementsObjectExpr
          .newInstance((ClassifierFunctionExpr) ast.arg1(), testSet, engine);
      if (measurements == null) {
        return F.NIL;
      }
      if (ast.argSize() == 3) {
        return measurements.property(ast.arg3());
      }
      return measurements;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.ClassifierMeasurements, rex, engine);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return null;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    ClassifierMeasurementsObjectExpr.registerHead();
  }
}
