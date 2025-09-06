package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>FindMaximum(f, {x, xstart})
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical maximum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>.
 * </p>
 * 
 * <pre>
 * <code>FindMaximum(f, {x, xstart}, Method-&gt;methodName)
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical maximum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>, with one of the following method names:
 * </p>
 * 
 * <pre>
 * <code>FindMaximum(f, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical maximum of the multivariate function <code>f</code> for the
 * variables <code>x, y,...</code> and the corresponding start values
 * <code>xstart, ystart,...</code>.
 * </p>
 * 
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Mathematical_optimization">Wikipedia - Mathematical
 * optimization</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Rosenbrock_function">Wikipedia - Rosenbrock
 * function</a></li>
 * </ul>
 * <h4>&quot;Powell&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/PowellOptimizer.java">Powell</a>
 * optimizer.
 * </p>
 * <p>
 * This is the default method, if no <code>Method</code> is set.
 * </p>
 * <h4>&quot;ConjugateGradient&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/gradient/NonLinearConjugateGradientOptimizer.java">Non-linear
 * conjugate gradient</a> optimizer.<br />
 * This is a derivative based method and the functions must be symbolically differentiable.
 * </p>
 * <h4>&quot;SequentialQuadratic&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/vector/constrained/SQPOptimizerS2.java">Sequential
 * Quadratic Programming</a> optimizer.
 * </p>
 * <p>
 * This is a derivative, multivariate based method and the functions must be symbolically
 * differentiable.
 * </p>
 * <h4>&quot;BOBYQA&quot;</h4>
 * <p>
 * Implements <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java">Powell's
 * BOBYQA</a> optimizer (Bound Optimization BY Quadratic Approximation).
 * </p>
 * <p>
 * The &quot;BOBYQA&quot; method falls back to &quot;CMAES&quot; if the objective function has
 * dimension 1.
 * </p>
 * <h4>&quot;CMAES&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java">Covariance
 * Matrix Adaptation Evolution Strategy (CMA-ES)</a> optimizer.
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * <code>&gt;&gt; FindMaximum(Sin(x), {x, 0.5}) 
 * {1.0,{x-&gt;1.5708}}
 * 
 * &gt;&gt; FindMaximum({(1-x)^2+100*(y-x^2)^2, x &gt;= -2.0 &amp;&amp; 2.0 &gt;= x &amp;&amp; y &gt;= -0.5 &amp;&amp; 1.5 &gt;= y}, {{x, -1.2}, {y,1.0}}, Method-&gt;&quot;BOBYQA&quot;) 
 * {2034.0,{x-&gt;-2.0,y-&gt;-0.5}}
 * </code>
 * </pre>
 */
public class FindMaximum extends FindMinimum {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    GoalType goalType = GoalType.MAXIMIZE;
    try {
      return findExtremum(ast, goalType, engine, options);
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())), engine);
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier().equals(LocalizedCoreFormats.MAX_COUNT_EXCEEDED)) {
        Object[] parts = mise.getParts();
        if (parts != null && parts.length >= 1) {
          // Failed to converge to the requested accuracy or precision within `1` iterations.
          return Errors.printMessage(ast.topHead(), "cvmit", F.list(F.$str(parts[0].toString())),
              engine);
        }
      }
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mise.getMessage())), engine);
    } catch (MathRuntimeException mre) {
      Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())), engine);
      return F.CEmptyList;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.MaxIterations, S.Method}, //
        new IExpr[] {//
            F.C100, S.Automatic});
  }
}
