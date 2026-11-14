package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ClassifierFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * Applies a trained classifier: <code>c(x)</code> and <code>c(x, prop)</code>, see
 * {@link ClassifierFunctionExpr}. The input form
 * <code>ClassifierFunction(&lt;|"Method" -&gt; method, "TrainingSet" -&gt; {...}|&gt;)</code>
 * trains the same classifier again.
 */
public class ClassifierFunction extends AbstractEvaluator {

  public ClassifierFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();
    if (head instanceof ClassifierFunctionExpr) {
      try {
        return ((ClassifierFunctionExpr) head).evaluate(ast, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.ClassifierFunction, rex, engine);
      }
    }
    if (head == S.ClassifierFunction && ast.isAST1() && ast.arg1().isAssociation()) {
      IAssociation definition = (IAssociation) ast.arg1();
      IExpr method = definition.getValue(F.stringx("Method"));
      IExpr trainingSet = definition.getValue(F.stringx("TrainingSet"));
      if ((method.isString() || method.isList()) && trainingSet.isList()) {
        IExpr classifier = engine.evaluate(F.binaryAST2(S.Classify, trainingSet,
            F.Rule(S.Method, method)));
        if (classifier instanceof ClassifierFunctionExpr) {
          return classifier;
        }
      }
    }
    return F.NIL;
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
    newSymbol.putMessage(IPatternMatcher.SET, "mlbddataev",
        F.stringx("The data being evaluated is not formatted correctly."));
    newSymbol.putMessage(IPatternMatcher.SET, "mlincfttp",
        F.stringx("Incompatible variable type (`1`) and variable value (`2`)."));
  }
}
