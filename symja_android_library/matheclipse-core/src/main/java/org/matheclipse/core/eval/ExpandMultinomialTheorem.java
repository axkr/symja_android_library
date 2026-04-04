package org.matheclipse.core.eval;

import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Expand a polynomial power with the multinomial theorem. See
 * <a href= "http://en.wikipedia.org/wiki/Multinomial_theorem">Wikipedia - Multinomial theorem</a>
 */
public class ExpandMultinomialTheorem {

  /**
   * Expand a multinomial expression of the form <code>(x1 + x2 + ... + xm)^n</code> into a sum of
   * terms of the form <code>c*x1^k1*x2^k2*...*xm^km</code>, where <code>c</code> is a multinomial
   * coefficient and <code>ki</code> are non-negative integers such that <code>k1 + k2 + ... + km =
   * n</code>.
   *
   * @param plusAST the {@link S#Plus} AST to expand
   * @param exponent the exponent <code>n</code>
   * @param numberOfTerms the number of terms in the result
   * @return an {@link IASTAppendable} containing the expanded result
   */
  public static IASTAppendable expand(IAST plusAST, int exponent, int numberOfTerms) {
    final IASTAppendable expandedResult = F.PlusAlloc(numberOfTerms);
    ExpandMultinomialTheorem part = new ExpandMultinomialTheorem(plusAST, exponent, expandedResult);
    part.partition();
    return expandedResult;
  }

  IASTAppendable expandedResult;
  int m;
  final int n;
  int[] parts;

  final IExpr[][] cachedPowers;

  public ExpandMultinomialTheorem(IAST plusAST, int exponent, IASTAppendable expandedResult) {
    this.expandedResult = expandedResult;
    this.n = exponent;
    this.m = plusAST.argSize();
    this.parts = new int[m];

    this.cachedPowers = new IExpr[m][exponent + 1];
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr arg = plusAST.get(i);
      for (int j = 1; j <= exponent; j++) {
        this.cachedPowers[i - 1][j] = arg.pow(j);
      }
    }
  }

  private void addFactor(int[] j) {
    final KPermutationsIterable perm = new KPermutationsIterable(j, m, m);
    IInteger multinomial = AbstractIntegerSym.multinomial(j, n);
    TimesOp timesOp = new TimesOp(32);

    for (int[] indices : perm) {
      if (!multinomial.isOne()) {
        timesOp.appendRecursive(multinomial);
      }
      for (int k = 0; k < m; k++) {
        int idx = indices[k];
        if (idx != 0) {
          IExpr temp = cachedPowers[k][idx];
          if (temp.equals(F.C1)) {
            // keep numeric 1.0 values here
            continue;
          }
          timesOp.appendRecursive(temp);
        }
      }
      expandedResult.append(timesOp.getProduct());
      timesOp.clear();
    }
  }

  public void partition() {
    partition(n, n, 0);
  }

  private void partition(int n, int max, int currentIndex) {
    if (n == 0) {
      addFactor(parts);
      return;
    }
    if (currentIndex >= m) {
      return;
    }
    int old = parts[currentIndex];
    int min = Math.min(max, n);

    for (int i = min; i >= 1; i--) {
      parts[currentIndex] = i;
      partition(n - i, i, currentIndex + 1);
    }
    parts[currentIndex] = old;
  }
}
