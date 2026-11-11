package org.matheclipse.image.algo;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Thinning of a binary image to lines one pixel wide by the two-subiteration parallel algorithm
 * of Guo and Hall, in the form Lam, Lee and Suen (1992) give it. Everything beyond the border of
 * the image is background.
 *
 * <p>
 * With the neighbours <code>x1, ..., x8</code> of a pixel counterclockwise from the one to the
 * right, a foreground pixel is deleted when
 * <ul>
 * <li>its crossing number - the number of <code>i</code> with <code>x(2i-1) = 0</code> and
 * <code>x(2i) = 1</code> or <code>x(2i+1) = 1</code> - is 1,</li>
 * <li>the smaller of <code>n1 = sum(x(2k-1) or x(2k))</code> and
 * <code>n2 = sum(x(2k) or x(2k+1))</code> is 2 or 3, and</li>
 * <li><code>(x2 or x3 or not x8) and x1</code> is false in the first subiteration,
 * <code>(x6 or x7 or not x4) and x5</code> in the second.</li>
 * </ul>
 * Both subiterations are repeated until neither deletes a pixel.
 */
public final class Thinning {

  /** Row and column offsets of <code>x1, ..., x8</code>; rows grow downwards. */
  private static final int[] DY = {0, -1, -1, -1, 0, 1, 1, 1};

  private static final int[] DX = {1, 1, 0, -1, -1, -1, 0, 1};

  private Thinning() {}

  /**
   * The thinned foreground given row by row from the top.
   *
   * @param foreground the pixels of the foreground; it is not changed
   */
  public static boolean[][] thin(boolean[][] foreground, EvalEngine engine) {
    final int height = foreground.length;
    final int width = height == 0 ? 0 : foreground[0].length;
    boolean[][] pixels = new boolean[height][];
    for (int y = 0; y < height; y++) {
      pixels[y] = foreground[y].clone();
    }
    List<int[]> deletions = new ArrayList<int[]>();
    boolean changed = true;
    while (changed) {
      engine.checkTimeLimit();
      changed = false;
      for (int pass = 0; pass < 2; pass++) {
        deletions.clear();
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            if (pixels[y][x] && deletable(pixels, y, x, pass)) {
              deletions.add(new int[] {y, x});
            }
          }
        }
        for (int[] p : deletions) {
          pixels[p[0]][p[1]] = false;
        }
        changed |= !deletions.isEmpty();
      }
    }
    return pixels;
  }

  private static boolean deletable(boolean[][] pixels, int y, int x, int pass) {
    // b[1..8] are x1..x8, b[9] is x1 again
    boolean[] b = new boolean[10];
    for (int k = 0; k < 8; k++) {
      int yk = y + DY[k];
      int xk = x + DX[k];
      b[k + 1] = yk >= 0 && yk < pixels.length && xk >= 0 && xk < pixels[yk].length
          && pixels[yk][xk];
    }
    b[9] = b[1];
    int crossing = 0;
    int n1 = 0;
    int n2 = 0;
    for (int i = 1; i <= 4; i++) {
      if (!b[2 * i - 1] && (b[2 * i] || b[2 * i + 1])) {
        crossing++;
      }
      if (b[2 * i - 1] || b[2 * i]) {
        n1++;
      }
      if (b[2 * i] || b[2 * i + 1]) {
        n2++;
      }
    }
    int m = Math.min(n1, n2);
    if (crossing != 1 || m < 2 || m > 3) {
      return false;
    }
    if (pass == 0) {
      return !((b[2] || b[3] || !b[8]) && b[1]);
    }
    return !((b[6] || b[7] || !b[4]) && b[5]);
  }
}
