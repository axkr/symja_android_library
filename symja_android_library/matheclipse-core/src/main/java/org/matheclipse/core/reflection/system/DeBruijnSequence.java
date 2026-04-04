package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * DeBruijnSequence[alphabet, n] - returns a De Bruijn sequence of order n over the given alphabet.
 * DeBruijnSequence[k, n] - returns a De Bruijn sequence over the alphabet {0, 1, ..., k-1}.
 * DeBruijnSequence["string", n] - returns a De Bruijn sequence over the characters of the string,
 * returned as a string.
 */
public class DeBruijnSequence extends AbstractEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();

    if (!arg2.isInteger()) {
      return F.NIL;
    }

    int n = arg2.toIntDefault();
    if (n < 1) {
      return F.NIL;
    }

    IExpr[] alphabet;
    int k = 0;
    boolean returnString = false;
    String strAlphabet = null;

    // Handle DeBruijnSequence[{a, b, ...}, n]
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      k = list.argSize();
      alphabet = new IExpr[k];
      for (int i = 0; i < k; i++) {
        alphabet[i] = list.get(i + 1); // IAST elements are 1-indexed
      }
    }
    // Handle DeBruijnSequence["abcd", n]
    else if (arg1.isString()) {
      returnString = true;
      strAlphabet = arg1.toString();

      // Strip quotes if toString() includes them in the Symja environment
      if (strAlphabet.length() >= 2 && strAlphabet.startsWith("\"") && strAlphabet.endsWith("\"")) {
        strAlphabet = strAlphabet.substring(1, strAlphabet.length() - 1);
      }

      k = strAlphabet.length();
      alphabet = new IExpr[k];
      for (int i = 0; i < k; i++) {
        alphabet[i] = F.stringx(String.valueOf(strAlphabet.charAt(i)));
      }
    }
    // Handle DeBruijnSequence[k, n] -> implies alphabet {0, 1, ..., k-1}
    else if (arg1.isInteger()) {
      k = arg1.toIntDefault(-1);
      if (k < 0) {
        return F.NIL;
      }
      alphabet = new IExpr[k];
      for (int i = 0; i < k; i++) {
        alphabet[i] = F.ZZ(i);
      }
    } else {
      return F.NIL;
    }

    // Base edge cases
    if (k == 0) {
      return returnString ? F.stringx("") : F.List();
    }
    if (k == 1) {
      return returnString ? F.stringx(String.valueOf(strAlphabet.charAt(0))) : F.List(alphabet[0]);
    }

    List<Integer> sequence = new ArrayList<>();
    // Array for Martin's algorithm
    int[] a = new int[k * n + 1];

    db(1, 1, n, k, a, sequence);

    // Format the output according to the input type
    if (returnString) {
      StringBuilder sb = new StringBuilder(sequence.size());
      for (Integer idx : sequence) {
        sb.append(strAlphabet.charAt(idx));
      }
      return F.stringx(sb.toString());
    } else {
      // Map the generated indices back to the actual alphabet expressions
      IASTAppendable result = F.ListAlloc(sequence.size());
      for (Integer idx : sequence) {
        result.append(alphabet[idx]);
      }
      return result;
    }
  }

  /**
   * Martin's algorithm for generating De Bruijn sequences.
   */
  private void db(int t, int p, int n, int k, int[] a, List<Integer> seq) {
    if (t > n) {
      if (n % p == 0) {
        for (int i = 1; i <= p; i++) {
          seq.add(a[i]);
        }
      }
    } else {
      a[t] = a[t - p];
      db(t + 1, p, n, k, a, seq);
      for (int j = a[t - p] + 1; j < k; j++) {
        a[t] = j;
        db(t + 1, t, n, k, a, seq);
      }
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
