package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * JordanDecomposition(m)
 * </pre>
 * 
 * * <blockquote>
 * <p>
 * yields the Jordan decomposition of a square matrix m. The result is a list {s,j} where s is a
 * similarity matrix and j is the Jordan canonical form of m.
 * </p>
 * </blockquote>
 */
public class JordanDecomposition extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix(false);
    if (dims == null || dims[0] != dims[1]) {
      return F.NIL;
    }
    int n = dims[0];

    boolean togetherMode = engine.isTogetherMode();
    try {
      engine.setTogetherMode(true);

      // Utilize Eigensystem to handle the completely diagonalizable case efficiently
      IExpr eigensystem = engine.evaluate(F.Eigensystem(arg1));
      if (eigensystem.isList2()) {
        IAST evals = (IAST) ((IAST) eigensystem).arg1();
        IAST evecs = (IAST) ((IAST) eigensystem).arg2();

        boolean isDiagonalizable = true;
        for (int i = 1; i <= evecs.argSize(); i++) {
          if (isZeroVector(evecs.get(i), engine)) {
            isDiagonalizable = false;
            break;
          }
        }

        if (isDiagonalizable) {
          // Sort eigenvalues and corresponding eigenvectors in canonical order
          List<IExpr[]> pairs = new ArrayList<>();
          for (int i = 1; i <= evals.argSize(); i++) {
            pairs.add(new IExpr[] {evals.get(i), evecs.get(i)});
          }
          pairs.sort((p1, p2) -> p1[0].compareTo(p2[0]));

          IASTAppendable sortedEvals = F.ListAlloc(n);
          IASTAppendable sortedEvecs = F.ListAlloc(n);
          for (IExpr[] p : pairs) {
            sortedEvals.append(p[0]);
            sortedEvecs.append(p[1]);
          }

          IExpr s = engine.evaluate(F.Transpose(sortedEvecs));
          IExpr j = engine.evaluate(F.DiagonalMatrix(sortedEvals));
          return F.List(s, j);
        }
      }

      // Try the bottom-up approach which naturally mimics standard basis selection
      IExpr bottomUpDecomp = computeBottomUp(arg1, n, engine);
      if (bottomUpDecomp.isList()) {
        return bottomUpDecomp;
      }

      // 3. Fallback to robust top-down if the standard null-space basis is entangled
      return computeTopDown(arg1, n, engine);

    } finally {
      engine.setTogetherMode(togetherMode);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  private boolean isZeroVector(IExpr vector, EvalEngine engine) {
    if (vector.isList()) {
      return ((IAST) vector).forAll(x -> engine.evaluate(F.PossibleZeroQ(x)).isTrue());
    }
    return false;
  }

  private int getMatrixRank(IAST matrix, EvalEngine engine) {
    if (matrix.argSize() == 0) {
      return 0;
    }
    IExpr rankExpr = engine.evaluate(F.MatrixRank(matrix));
    return Math.max(0, rankExpr.toIntDefault(0));
  }

  /**
   * Computes the Jordan decomposition bottom-up. This algorithm solves B * v_{k+1} = v_k starting
   * from the null space. It matches similarity matrix S by inheriting standard basis vectors
   * directly from NullSpace() and LinearSolve().
   */
  private IExpr computeBottomUp(IExpr matrix, int n, EvalEngine engine) {
    IExpr eigenvaluesExpr = engine.evaluate(F.Eigenvalues(matrix));
    if (!eigenvaluesExpr.isList()) {
      return F.NIL;
    }
    IAST eigenvaluesList = (IAST) eigenvaluesExpr;

    Map<IExpr, Integer> rootCounts = new LinkedHashMap<>();
    for (int i = 1; i <= eigenvaluesList.argSize(); i++) {
      IExpr root = eigenvaluesList.get(i);
      rootCounts.put(root, rootCounts.getOrDefault(root, 0) + 1);
    }

    // Sort eigenvalues in canonical order (matching Mathematica)
    List<IExpr> sortedRoots = new ArrayList<>(rootCounts.keySet());
    sortedRoots.sort(IExpr::compareTo);

    IASTAppendable sCols = F.ListAlloc(n);
    IASTAppendable jMatrix = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      IASTAppendable row = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        row.append(F.C0);
      }
      jMatrix.append(row);
    }

    IExpr identity = engine.evaluate(F.IdentityMatrix(n));
    int currentIndex = 1;

    for (IExpr lambda : sortedRoots) {
      int algMult = rootCounts.get(lambda);

      IExpr bMat = engine.evaluate(F.Subtract(matrix, F.Times(lambda, identity)));
      IExpr nullSpaceExpr = engine.evaluate(F.NullSpace(bMat));
      if (!nullSpaceExpr.isList()) {
        return F.NIL;
      }
      IAST nullSpace = (IAST) nullSpaceExpr;
      int geomMult = nullSpace.argSize();

      int totalVectors = 0;
      List<List<IExpr>> allChains = new ArrayList<>();

      for (int i = 1; i <= geomMult; i++) {
        List<IExpr> chain = new ArrayList<>();
        IExpr curr = nullSpace.get(i);
        chain.add(curr);
        totalVectors++;

        while (totalVectors < algMult) {
          IExpr next = engine.evaluate(F.LinearSolve(bMat, curr));
          if (!next.isList()) {
            break; // Chain terminates here
          }
          chain.add(next);
          curr = next;
          totalVectors++;
        }
        allChains.add(chain);
      }

      if (totalVectors != algMult) {
        return F.NIL; // Fallback to top-down due to misaligned null space
      }

      // Sort chains descending by length to match the canonical block format
      allChains.sort((c1, c2) -> Integer.compare(c2.size(), c1.size()));

      for (List<IExpr> chain : allChains) {
        for (int i = 0; i < chain.size(); i++) {
          sCols.append(chain.get(i));
          ((IASTAppendable) jMatrix.get(currentIndex)).set(currentIndex, lambda);
          if (i < chain.size() - 1) {
            ((IASTAppendable) jMatrix.get(currentIndex)).set(currentIndex + 1, F.C1);
          }
          currentIndex++;
        }
      }
    }

    IExpr s = engine.evaluate(F.Transpose(sCols));
    return F.List(s, jMatrix);
  }

  /**
   * Computes the Jordan decomposition for defective matrices by constructing chains of generalized
   * eigenvectors from the top down. Extremely robust but generates arbitrary choices of independent
   * bases.
   */
  private IExpr computeTopDown(IExpr matrix, int n, EvalEngine engine) {
    IExpr eigenvaluesExpr = engine.evaluate(F.Eigenvalues(matrix));
    if (!eigenvaluesExpr.isList()) {
      return F.NIL;
    }
    IAST eigenvaluesList = (IAST) eigenvaluesExpr;

    Map<IExpr, Integer> rootCounts = new LinkedHashMap<>();
    for (int i = 1; i <= eigenvaluesList.argSize(); i++) {
      IExpr root = eigenvaluesList.get(i);
      rootCounts.put(root, rootCounts.getOrDefault(root, 0) + 1);
    }

    // Sort eigenvalues in canonical order (matching Mathematica)
    List<IExpr> sortedRoots = new ArrayList<>(rootCounts.keySet());
    sortedRoots.sort(IExpr::compareTo);

    IASTAppendable sCols = F.ListAlloc(n);
    IASTAppendable jMatrix = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      IASTAppendable row = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        row.append(F.C0);
      }
      jMatrix.append(row);
    }

    IExpr identity = engine.evaluate(F.IdentityMatrix(n));
    int currentIndex = 1;

    for (IExpr lambda : sortedRoots) {
      int algMult = rootCounts.get(lambda);

      IExpr bMat = engine.evaluate(F.Subtract(matrix, F.Times(lambda, identity)));

      List<IAST> nullSpaces = new ArrayList<>();
      nullSpaces.add(F.CEmptyList);

      int p = 0;
      int currentDim = 0;
      while (currentDim < algMult) {
        p++;
        IExpr bp = engine.evaluate(F.MatrixPower(bMat, F.ZZ(p)));
        IExpr nsExpr = engine.evaluate(F.NullSpace(bp));
        if (!nsExpr.isList()) {
          return F.NIL;
        }
        IAST ns = (IAST) nsExpr;
        nullSpaces.add(ns);
        if (ns.argSize() == currentDim) {
          break;
        }
        currentDim = ns.argSize();
      }

      List<List<IExpr>> allChains = new ArrayList<>();
      List<IExpr> activeTopVectors = new ArrayList<>();

      for (int k = p; k >= 1; k--) {
        if (k >= nullSpaces.size())
          continue;
        IAST nk = nullSpaces.get(k);
        IAST nPrev = nullSpaces.get(k - 1);

        IASTAppendable wSubspace = F.ListAlloc();
        for (int i = 1; i <= nPrev.argSize(); i++) {
          wSubspace.append(nPrev.get(i));
        }
        for (IExpr v : activeTopVectors) {
          wSubspace.append(v);
        }

        int currentRank = getMatrixRank(wSubspace, engine);
        List<IExpr> newTopVectors = new ArrayList<>();

        for (int i = 1; i <= nk.argSize(); i++) {
          IExpr candidate = nk.get(i);
          wSubspace.append(candidate);
          int newRank = getMatrixRank(wSubspace, engine);

          if (newRank > currentRank) {
            currentRank = newRank;
            newTopVectors.add(candidate);

            List<IExpr> chain = new ArrayList<>();
            IExpr curr = candidate;
            chain.add(curr);
            for (int step = 1; step < k; step++) {
              curr = engine.evaluate(F.Dot(bMat, curr));
              chain.add(0, curr);
            }
            allChains.add(chain);
          } else {
            wSubspace.remove(wSubspace.size() - 1);
          }
        }

        for (int i = 0; i < activeTopVectors.size(); i++) {
          activeTopVectors.set(i, engine.evaluate(F.Dot(bMat, activeTopVectors.get(i))));
        }
        for (IExpr v : newTopVectors) {
          activeTopVectors.add(engine.evaluate(F.Dot(bMat, v)));
        }
      }

      allChains.sort((c1, c2) -> Integer.compare(c2.size(), c1.size()));

      int vectorsFound = 0;
      for (List<IExpr> chain : allChains) {
        vectorsFound += chain.size();
        for (int i = 0; i < chain.size(); i++) {
          sCols.append(chain.get(i));
          ((IASTAppendable) jMatrix.get(currentIndex)).set(currentIndex, lambda);
          if (i < chain.size() - 1) {
            ((IASTAppendable) jMatrix.get(currentIndex)).set(currentIndex + 1, F.C1);
          }
          currentIndex++;
        }
      }

      if (vectorsFound != algMult) {
        return F.NIL;
      }
    }

    IExpr s = engine.evaluate(F.Transpose(sCols));
    return F.List(s, jMatrix);
  }
}