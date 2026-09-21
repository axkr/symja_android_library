package org.matheclipse.core.reflection.system;

import org.hipparchus.optim.nonlinear.scalar.GoalType;

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
 * <pre>
 * <code>FindMaximum(f, {x, xstart}, {y, ystart}, ...)
 * </code>
 * </pre>
 *
 * <p>
 * is the same search with one search specification per argument.
 * </p>
 *
 * <pre>
 * <code>FindMaximum({f, constraints}, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 *
 * <p>
 * searches for a local numerical maximum subject to the <code>constraints</code>. Bounds of a
 * single variable like <code>x&gt;=1</code> are taken by the methods &quot;CMAES&quot; and
 * &quot;BOBYQA&quot;; linear equations and inequalities like <code>x+y&gt;=4</code> select the
 * &quot;SequentialQuadratic&quot; method. Other constraints are not supported.
 * </p>
 * <p>
 * A search specification can be <code>x</code> or <code>{x}</code> (start value chosen
 * automatically), <code>{x, xstart}</code>, <code>{x, xstart, xstart2}</code> or
 * <code>{x, xstart, xmin, xmax}</code> (the search stays in <code>xmin&lt;=x&lt;=xmax</code>). The
 * variables are localized like in <code>Block</code>.
 * </p>
 * <p>
 * The option <code>MaxIterations</code> (default <code>100</code>) limits the iterations of a
 * method; <code>Automatic</code> and <code>Infinity</code> are possible values.
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
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/CMAESOptimizer.java">Covariance
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
  protected GoalType goalType() {
    return GoalType.MAXIMIZE;
  }
}
