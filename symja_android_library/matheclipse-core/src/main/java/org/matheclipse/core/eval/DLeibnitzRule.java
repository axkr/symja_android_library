package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * The <code>DLeibnitzRule</code> class implements the
 * <a href="https://en.wikipedia.org/wiki/General_Leibniz_rule#More_than_two_factors">General
 * Leibniz rule</a> for calculating the <code>n</code>-th derivative of a product of functions,
 * represented as a {@link S#Times} AST. It uses a recursive partitioning approach to generate all
 * combinations of derivatives for each term in the product.
 */
public class DLeibnitzRule {

  /**
   * Calculate the <code>n</code>-th derivative of a {@link S#Times} AST with respect to the
   * variable <code>x</code> using the
   * <a href="https://en.wikipedia.org/wiki/General_Leibniz_rule#More_than_two_factors">General
   * Leibniz rule</a>.
   *
   * @param timesAST the {@link S#Plus} AST to differentiate
   * @param x the variable <code>x</code> for which the derivative is calculated
   * @param n the order of the derivative
   * @param numberOfTerms the number of terms in the result
   * @param engine the evaluation engine
   * @return an {@link IASTAppendable} containing the derived result
   */
  public static IASTAppendable nThDerivative(IAST timesAST, IExpr x, int n, int numberOfTerms,
      EvalEngine engine) {
    final IASTAppendable expandedResult = F.ast(S.Plus, numberOfTerms);
    DLeibnitzRule part = new DLeibnitzRule(timesAST, x, n, expandedResult, engine);
    part.partition();
    return expandedResult;
  }

  IASTAppendable derivedResult;
  final int m;
  final int n;
  int[] parts;

  /**
   * Cache repeating {@link S#D} calculations for each part of the {@link S#Times} AST.
   * <p>
   * Includes the <code>0</code> derivative.
   */
  final IASTAppendable[] cachedDerivatives;

  /**
   * 
   *
   * @param timesAST the {@link S#Times} AST
   * @param x the variable <code>x</code> for which the derivative is calculated
   * @param exponent the exponent <code>n</code>
   * @param derivedResult the {@link IASTAppendable} to store the derived result
   */
  public DLeibnitzRule(IAST timesAST, IExpr x, int exponent, IASTAppendable derivedResult,
      EvalEngine engine) {
    this.derivedResult = derivedResult;
    this.n = exponent;
    this.m = timesAST.argSize();
    this.parts = new int[m];
    // cache all {@link S#D} calculations for each part of the {@link S#Plus} AST:
    this.cachedDerivatives = new IASTAppendable[m];
    for (int i = 1; i < timesAST.size(); i++) {
      IExpr derivative = timesAST.get(i);
      cachedDerivatives[i - 1] = F.ListAlloc(exponent + 1);
      for (int j = 0; j <= exponent; j++) {
        if (derivative.isZero()) {
          // append zero for all remaining powers
          for (int k = j; k <= exponent; k++) {
            this.cachedDerivatives[i - 1].append(F.C0);
          }
          break;
        }
        this.cachedDerivatives[i - 1].append(derivative);
        // calculate next derivative for the {@link S#Times} argument
        derivative = engine.evaluate(F.D(derivative, x));
      }
    }
  }

  private void addFactor(final int[] j) {
    IInteger multinomial = null;
    TimesOp timesOp = new TimesOp(32);
    final KPermutationsIterable perm = new KPermutationsIterable(j, m, m);
    for (int[] indices : perm) {
      boolean isZero = false;
      for (int k = 0; k < m; k++) {
        IExpr temp = cachedDerivatives[k].get(indices[k] + 1);
        if (temp.isZero()) {
          isZero = true;
          break;
        }
        if (temp.equals(F.C1)) {
          // keep numeric 1.0 values here
          continue;
        }
        timesOp.appendRecursive(temp);
      }
      if (!isZero) {
        if (multinomial == null) {
          multinomial = IInteger.multinomial(j, n);
        }
        if (!multinomial.isOne()) {
          timesOp.appendRecursive(multinomial);
        }
        derivedResult.append(timesOp.getProduct());
      }
      timesOp.clear();
    }
  }

  private void partition() {
    partition(n, n, 0);
  }

  private void partition(final int n, final int max, final int currentIndex) {
    if (n == 0) {
      addFactor(parts);
      return;
    }
    if (currentIndex >= m) {
      return;
    }
    final int old = parts[currentIndex];
    final int min = Math.min(max, n);

    for (int i = min; i >= 1; i--) {
      parts[currentIndex] = i;
      partition(n - i, i, currentIndex + 1);
    }
    parts[currentIndex] = old;
  }

}
