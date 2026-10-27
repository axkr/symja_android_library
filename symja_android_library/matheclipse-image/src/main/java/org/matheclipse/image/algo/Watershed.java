package org.matheclipse.image.algo;

import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 * The watershed transform of a grey level relief by priority flooding: every basin grows from its
 * seed, the lowest pixel at the border of all basins being added first, and a pixel reached by two
 * different basins at once becomes part of the watershed between them.
 *
 * <p>
 * Pixels are neighbours through their edges, and also through their corners if
 * <code>cornerNeighbors</code> is set. Pixels of the same height are taken in the order they were
 * reached, so a plateau between two basins is split down its middle.
 */
public final class Watershed {

  private Watershed() {}

  /**
   * The basins of <code>relief[row][column]</code>, grown from its regional minima: the connected
   * plateaus no neighbour of which is lower. The basins are numbered from <code>1</code> in the
   * order their first pixel is met row by row; the watershed pixels are <code>0</code>.
   */
  public static int[][] fromMinima(double[][] relief, boolean cornerNeighbors) {
    final int height = relief.length;
    final int width = relief[0].length;
    int[][] label = new int[height][width];
    boolean[][] visited = new boolean[height][width];
    int basins = 0;
    ArrayDeque<int[]> plateau = new ArrayDeque<int[]>();
    ArrayDeque<int[]> members = new ArrayDeque<int[]>();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (visited[y][x]) {
          continue;
        }
        // the plateau of equal height around (y, x), and whether it is a regional minimum
        double level = relief[y][x];
        boolean minimum = true;
        visited[y][x] = true;
        plateau.add(new int[] {y, x});
        members.clear();
        while (!plateau.isEmpty()) {
          int[] pixel = plateau.poll();
          members.add(pixel);
          for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
              int row = pixel[0] + dy;
              int column = pixel[1] + dx;
              if (!isNeighbor(dy, dx, cornerNeighbors) || row < 0 || row >= height || column < 0
                  || column >= width) {
                continue;
              }
              double value = relief[row][column];
              if (value < level) {
                minimum = false;
              } else if (value == level && !visited[row][column]) {
                visited[row][column] = true;
                plateau.add(new int[] {row, column});
              }
            }
          }
        }
        if (minimum) {
          basins++;
          for (int[] pixel : members) {
            label[pixel[0]][pixel[1]] = basins;
          }
        }
      }
    }
    return flood(relief, label, cornerNeighbors);
  }

  /**
   * The basins of <code>relief[row][column]</code>, grown from the seeds in <code>label</code>:
   * the pixels with a positive number belong to the basin of that number from the start, and the
   * pixels with <code>0</code> are flooded. The watershed pixels are <code>0</code>.
   *
   * @param label the seeds, which is filled in and returned
   */
  public static int[][] flood(double[][] relief, int[][] label, boolean cornerNeighbors) {
    final int height = relief.length;
    final int width = relief[0].length;
    boolean[][] queued = new boolean[height][width];
    // ordered by height, and pixels of the same height in the order they were reached
    PriorityQueue<double[]> queue = new PriorityQueue<double[]>(
        (a, b) -> a[0] != b[0] ? Double.compare(a[0], b[0]) : Double.compare(a[1], b[1]));
    long[] order = new long[1];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (label[y][x] > 0) {
          queued[y][x] = true;
        }
      }
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (label[y][x] > 0) {
          enqueueNeighbors(relief, queued, queue, order, y, x, cornerNeighbors);
        }
      }
    }
    while (!queue.isEmpty()) {
      double[] entry = queue.poll();
      int y = (int) entry[2];
      int x = (int) entry[3];
      int basin = 0;
      boolean watershed = false;
      for (int dy = -1; dy <= 1 && !watershed; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
          int row = y + dy;
          int column = x + dx;
          if (!isNeighbor(dy, dx, cornerNeighbors) || row < 0 || row >= height || column < 0
              || column >= width) {
            continue;
          }
          int neighbor = label[row][column];
          if (neighbor > 0) {
            if (basin == 0) {
              basin = neighbor;
            } else if (basin != neighbor) {
              watershed = true;
              break;
            }
          }
        }
      }
      if (!watershed && basin > 0) {
        label[y][x] = basin;
        enqueueNeighbors(relief, queued, queue, order, y, x, cornerNeighbors);
      }
    }
    return label;
  }

  private static void enqueueNeighbors(double[][] relief, boolean[][] queued,
      PriorityQueue<double[]> queue, long[] order, int y, int x, boolean cornerNeighbors) {
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        int row = y + dy;
        int column = x + dx;
        if (!isNeighbor(dy, dx, cornerNeighbors) || row < 0 || row >= relief.length
            || column < 0 || column >= relief[0].length || queued[row][column]) {
          continue;
        }
        queued[row][column] = true;
        queue.add(new double[] {relief[row][column], order[0]++, row, column});
      }
    }
  }

  private static boolean isNeighbor(int dy, int dx, boolean cornerNeighbors) {
    int offsets = Math.abs(dy) + Math.abs(dx);
    return offsets != 0 && (cornerNeighbors || offsets == 1);
  }
}
