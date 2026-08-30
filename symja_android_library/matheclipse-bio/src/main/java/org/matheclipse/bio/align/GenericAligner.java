package org.matheclipse.bio.align;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Pairwise sequence alignment over arbitrary {@link IExpr} elements, with affine gap costs (Gotoh's
 * algorithm).
 *
 * <p>
 * BioJava's aligners are typed over biological alphabets (DNA / RNA / amino acid) and cannot
 * represent an arbitrary expression, but <code>SequenceAlignment</code>,
 * <code>SmithWatermanSimilarity</code> and <code>NeedlemanWunschSimilarity</code> accept any string
 * or list. This class covers that case; biological input goes through BioJava instead.
 */
public class GenericAligner {

  /** Scoring of one aligned pair of elements. */
  public interface Scorer {
    int score(IExpr a, IExpr b);

    boolean isMatch(IExpr a, IExpr b);
  }

  public enum Mode {
    /** Needleman-Wunsch: align the sequences end to end. */
    GLOBAL,
    /** Smith-Waterman: align the best-scoring pair of subsequences. */
    LOCAL
  }

  /**
   * One alignment. {@link #alignedA} and {@link #alignedB} have equal length; a <code>null</code>
   * entry is a gap.
   */
  public static class Alignment {
    public final IExpr[] alignedA;
    public final IExpr[] alignedB;
    public final int score;
    public final int matchCount;

    Alignment(IExpr[] alignedA, IExpr[] alignedB, int score, int matchCount) {
      this.alignedA = alignedA;
      this.alignedB = alignedB;
      this.score = score;
      this.matchCount = matchCount;
    }
  }

  private static final int NEG_INF = Integer.MIN_VALUE / 4;

  private static final byte FROM_M = 0;
  private static final byte FROM_X = 1;
  private static final byte FROM_Y = 2;

  /**
   * @param a first sequence
   * @param b second sequence
   * @param scorer substitution scoring
   * @param gapOpen cost of the first element of a gap run, as a positive number
   * @param gapExtend cost of each further element of a gap run, as a positive number
   * @param mode global or local
   */
  public static Alignment align(IExpr[] a, IExpr[] b, Scorer scorer, int gapOpen, int gapExtend,
      Mode mode) {
    final int n = a.length;
    final int m = b.length;
    final boolean local = mode == Mode.LOCAL;

    // M ends in a substitution, X ends in a gap in b, Y ends in a gap in a
    int[][] matchScore = new int[n + 1][m + 1];
    int[][] gapInB = new int[n + 1][m + 1];
    int[][] gapInA = new int[n + 1][m + 1];
    byte[][] matchFrom = new byte[n + 1][m + 1];
    byte[][] gapInBFrom = new byte[n + 1][m + 1];
    byte[][] gapInAFrom = new byte[n + 1][m + 1];

    matchScore[0][0] = 0;
    gapInB[0][0] = NEG_INF;
    gapInA[0][0] = NEG_INF;
    for (int i = 1; i <= n; i++) {
      matchScore[i][0] = NEG_INF;
      gapInA[i][0] = NEG_INF;
      gapInB[i][0] = local ? 0 : -gapOpen - (i - 1) * gapExtend;
      gapInBFrom[i][0] = i == 1 ? FROM_M : FROM_X;
    }
    for (int j = 1; j <= m; j++) {
      matchScore[0][j] = NEG_INF;
      gapInB[0][j] = NEG_INF;
      gapInA[0][j] = local ? 0 : -gapOpen - (j - 1) * gapExtend;
      gapInAFrom[0][j] = j == 1 ? FROM_M : FROM_Y;
    }

    int best = local ? 0 : NEG_INF;
    int bestI = 0;
    int bestJ = 0;
    byte bestMatrix = FROM_M;

    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        int substitution = scorer.score(a[i - 1], b[j - 1]);

        int previous = max3(matchScore[i - 1][j - 1], gapInB[i - 1][j - 1], gapInA[i - 1][j - 1]);
        matchFrom[i][j] =
            which3(matchScore[i - 1][j - 1], gapInB[i - 1][j - 1], gapInA[i - 1][j - 1]);
        int value = previous <= NEG_INF ? NEG_INF : previous + substitution;
        if (local && value < 0) {
          value = 0;
        }
        matchScore[i][j] = value;

        int openFromMatch = safeSubtract(matchScore[i - 1][j], gapOpen);
        int extendGap = safeSubtract(gapInB[i - 1][j], gapExtend);
        if (openFromMatch >= extendGap) {
          gapInB[i][j] = openFromMatch;
          gapInBFrom[i][j] = FROM_M;
        } else {
          gapInB[i][j] = extendGap;
          gapInBFrom[i][j] = FROM_X;
        }
        if (local && gapInB[i][j] < 0) {
          gapInB[i][j] = 0;
        }

        openFromMatch = safeSubtract(matchScore[i][j - 1], gapOpen);
        extendGap = safeSubtract(gapInA[i][j - 1], gapExtend);
        if (openFromMatch >= extendGap) {
          gapInA[i][j] = openFromMatch;
          gapInAFrom[i][j] = FROM_M;
        } else {
          gapInA[i][j] = extendGap;
          gapInAFrom[i][j] = FROM_Y;
        }
        if (local && gapInA[i][j] < 0) {
          gapInA[i][j] = 0;
        }

        if (local && matchScore[i][j] > best) {
          best = matchScore[i][j];
          bestI = i;
          bestJ = j;
          bestMatrix = FROM_M;
        }
      }
    }

    if (!local) {
      bestI = n;
      bestJ = m;
      best = max3(matchScore[n][m], gapInB[n][m], gapInA[n][m]);
      bestMatrix = which3(matchScore[n][m], gapInB[n][m], gapInA[n][m]);
    }

    // traceback
    java.util.ArrayList<IExpr> outA = new java.util.ArrayList<IExpr>();
    java.util.ArrayList<IExpr> outB = new java.util.ArrayList<IExpr>();
    int i = bestI;
    int j = bestJ;
    byte matrix = bestMatrix;
    int matchCount = 0;
    while (i > 0 || j > 0) {
      if (local && currentScore(matrix, matchScore, gapInB, gapInA, i, j) <= 0) {
        break;
      }
      if (matrix == FROM_M) {
        if (i == 0 || j == 0) {
          break;
        }
        outA.add(a[i - 1]);
        outB.add(b[j - 1]);
        if (scorer.isMatch(a[i - 1], b[j - 1])) {
          matchCount++;
        }
        byte next = matchFrom[i][j];
        i--;
        j--;
        matrix = next;
      } else if (matrix == FROM_X) {
        if (i == 0) {
          break;
        }
        outA.add(a[i - 1]);
        outB.add(null);
        byte next = gapInBFrom[i][j];
        i--;
        matrix = next;
      } else {
        if (j == 0) {
          break;
        }
        outA.add(null);
        outB.add(b[j - 1]);
        byte next = gapInAFrom[i][j];
        j--;
        matrix = next;
      }
    }

    int size = outA.size();
    IExpr[] alignedA = new IExpr[size];
    IExpr[] alignedB = new IExpr[size];
    for (int k = 0; k < size; k++) {
      alignedA[k] = outA.get(size - 1 - k);
      alignedB[k] = outB.get(size - 1 - k);
    }
    return new Alignment(alignedA, alignedB, best, matchCount);
  }

  private static int currentScore(byte matrix, int[][] matchScore, int[][] gapInB, int[][] gapInA,
      int i, int j) {
    switch (matrix) {
      case FROM_M:
        return matchScore[i][j];
      case FROM_X:
        return gapInB[i][j];
      default:
        return gapInA[i][j];
    }
  }

  private static int safeSubtract(int value, int cost) {
    return value <= NEG_INF ? NEG_INF : value - cost;
  }

  private static int max3(int x, int y, int z) {
    return Math.max(x, Math.max(y, z));
  }

  private static byte which3(int x, int y, int z) {
    if (x >= y && x >= z) {
      return FROM_M;
    }
    return y >= z ? FROM_X : FROM_Y;
  }

  private GenericAligner() {}
}
