package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITensorAccess;

/**
 * The Permanent function. *
 * 
 * <pre>
 * Permanent(m)
 * </pre>
 * 
 * * <blockquote>
 * <p>
 * computes the permanent of the matrix <code>m</code>.
 * </p>
 * </blockquote> *
 * 
 * <pre>
 * Permanent(m, Modulus -&gt; n)
 * </pre>
 * 
 * * <blockquote>
 * <p>
 * computes the permanent modulo <code>n</code>. The matrix m must be an integer matrix.
 * </p>
 * </blockquote>
 */
public class Permanent extends AbstractFunctionOptionEvaluator {

  public Permanent() {
    // default constructor
  }

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr matrix = ast.arg1();

    if (matrix.isEmptyList()) {
      return F.C0;
    }

    int[] dims = matrix.isMatrix(false);

    // Ensure we are working with a valid square matrix
    if (dims == null || dims[0] != dims[1]) {
      // Argument `1` at position `2` is not a non-empty square matrix.
      return Errors.printMessage(S.Permanent, "matsq", F.List(matrix, F.C1));
    }

    int n = dims[0];
    // TODO add more methods; only Ryser's formula at the moment
    IExpr method = options[0];
    IExpr modulus = options[1];

    if (n == 0) {
      return F.C1;
    }

    if (n > 30) {
      // Prevent extremely large dimensions that would overflow bitwise logic
      // or cause excessive evaluator overhead.
      return F.NIL;
    }

    ITensorAccess matrixTensor = (ITensorAccess) matrix;

    // Validate integer requirement for Modulus
    if (modulus.isPresent() && !modulus.equals(S.None) && !modulus.equals(S.Automatic)) {
      boolean allIntegers = true;
      for (int i = 1; i <= n; i++) {
        IAST row = (IAST) matrixTensor.get(i);
        for (int j = 1; j <= n; j++) {
          if (!row.get(j).isInteger()) {
            allIntegers = false;
            break;
          }
        }
        if (!allIntegers) {
          break;
        }
      }

      if (!allIntegers) {
        // `1` is not valid modulo `2`.
        Errors.printMessage(ast.topHead(), "nmod", F.List(matrix, modulus));
        return F.NIL;
      }
    }

    // Implementation of Ryser's formula
    IASTAppendable sumAlloc = F.PlusAlloc();
    long maxSubset = (1L << n) - 1L;

    for (long k = 1; k <= maxSubset; k++) {
      int c = Long.bitCount(k);
      IASTAppendable prodAlloc = F.TimesAlloc(n);

      for (int i = 1; i <= n; i++) {
        ITensorAccess row = (ITensorAccess) matrixTensor.get(i);
        IASTAppendable rowSum = F.PlusAlloc(c);

        for (int j = 0; j < n; j++) {
          if ((k & (1L << j)) != 0) {
            rowSum.append(row.get(j + 1));
          }
        }
        // Evaluate row sum immediately to prevent large unresolved memory trees
        prodAlloc.append(engine.evaluate(rowSum));
      }

      IExpr term = engine.evaluate(prodAlloc);

      // Multiply by sign: (-1)^(n - c)
      if ((n - c) % 2 != 0) {
        term = engine.evaluate(F.Times(F.CN1, term));
      }
      sumAlloc.append(term);
    }

    // Final evaluation and polynomial expansion
    IExpr result = engine.evaluate(sumAlloc);
    result = engine.evaluate(F.Expand(result));

    // Apply modulus if specified
    if (modulus.isPresent() && !modulus.equals(S.None) && !modulus.equals(S.Automatic)) {
      result = engine.evaluate(F.Mod(result, modulus));
    }

    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // Setup default options for the Modulus and Method configurations
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Method, S.Modulus},
        new IExpr[] {S.Automatic, S.None});
  }
}
