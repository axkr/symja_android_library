package org.matheclipse.image.algo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

/** The filters of a volume and the picture it is shown as. */
public class VolumesTest {

  /** A volume of <code>size</code> voxels along every direction with one voxel set. */
  private static double[][][] dot(int size, int z, int y, int x) {
    double[][][] volume = new double[size][size][size];
    volume[z][y][x] = 1.0;
    return volume;
  }

  private static double total(double[][][] volume) {
    double sum = 0.0;
    for (double[][] slice : volume) {
      for (double[] row : slice) {
        for (double sample : row) {
          sum += sample;
        }
      }
    }
    return sum;
  }

  @Test
  public void testDilationAndErosionOfACube() {
    // a voxel grows into the cube around it, and the cube shrinks back to the voxel
    double[][][] dilated = Volumes.block(dot(5, 2, 2, 2), 1, Volumes.MAX);
    assertEquals(27.0, total(dilated), 0.0);
    assertEquals(1.0, dilated[1][3][1], 0.0);
    assertEquals(0.0, dilated[0][2][2], 0.0);
    double[][][] eroded = Volumes.block(dilated, 1, Volumes.MIN);
    assertEquals(1.0, total(eroded), 0.0);
    assertEquals(1.0, eroded[2][2][2], 0.0);
  }

  @Test
  public void testBlockIsCutOffAtTheBorder() {
    // a voxel in a corner grows into the 8 voxels of the cube which lie inside of the volume
    assertEquals(8.0, total(Volumes.block(dot(4, 0, 0, 0), 1, Volumes.MAX)), 0.0);
    // the erosion of a volume of ones doesn't see the outside
    double[][][] ones = new double[3][4][5];
    for (double[][] slice : ones) {
      for (double[] row : slice) {
        java.util.Arrays.fill(row, 1.0);
      }
    }
    assertEquals(60.0, total(Volumes.block(ones, 2, Volumes.MIN)), 0.0);
    // the mean is the one of the samples inside: 1 of the 8 voxels around a corner
    double[][][] mean = Volumes.block(dot(4, 0, 0, 0), 1, Volumes.MEAN);
    assertEquals(1.0 / 8.0, mean[0][0][0], 1e-15);
    assertEquals(1.0 / 12.0, mean[0][0][1], 1e-15);
    assertEquals(1.0 / 27.0, mean[1][1][1], 1e-15);
    assertEquals(0.0, mean[2][2][2], 0.0);
  }

  @Test
  public void testMedian() {
    // one voxel of 27 is no median, 14 of them are
    assertEquals(0.0, total(Volumes.block(dot(3, 1, 1, 1), 1, Volumes.MEDIAN)), 0.0);
    double[][][] half = new double[3][3][3];
    int count = 0;
    for (double[][] slice : half) {
      for (double[] row : slice) {
        for (int x = 0; x < 3 && count < 14; x++, count++) {
          row[x] = 1.0;
        }
      }
    }
    assertEquals(1.0, Volumes.block(half, 1, Volumes.MEDIAN)[1][1][1], 0.0);
  }

  @Test
  public void testStructuringElement() {
    // the element {{{1, 1, 0}}} lies on the voxel itself and on the one before it in the row
    boolean[][][] element = {{{true, true, false}}};
    double[][][] volume = new double[1][1][5];
    volume[0][0][2] = 1.0;
    assertArrayEquals(new double[] {0, 0, 1, 1, 0},
        Volumes.extreme(volume, element, true)[0][0], 0.0);
    volume[0][0] = new double[] {0, 0, 1, 1, 1};
    assertArrayEquals(new double[] {0, 0, 0, 1, 1},
        Volumes.extreme(volume, element, false)[0][0], 0.0);
    // a cube of ones is the block filter
    boolean[][][] cube = new boolean[3][3][3];
    for (boolean[][] slice : cube) {
      for (boolean[] row : slice) {
        java.util.Arrays.fill(row, true);
      }
    }
    assertEquals(27.0, total(Volumes.extreme(dot(5, 2, 2, 2), cube, true)), 0.0);
  }

  @Test
  public void testSeparableFilter() {
    double[] kernel = {0.25, 0.5, 0.25};
    double[][][] filtered = Volumes.separable(dot(5, 2, 2, 2), kernel);
    assertEquals(0.125, filtered[2][2][2], 1e-15);
    assertEquals(0.25 * 0.25 * 0.25, filtered[1][1][1], 1e-15);
    assertEquals(1.0, total(filtered), 1e-12);
    // the border is continued with its own voxels, so a constant volume stays constant
    double[][][] constant = new double[2][3][4];
    for (double[][] slice : constant) {
      for (double[] row : slice) {
        java.util.Arrays.fill(row, 0.3);
      }
    }
    assertEquals(0.3, Volumes.separable(constant, kernel)[0][0][0], 1e-15);
  }

  @Test
  public void testPicture() {
    BufferedImage empty = Volumes.picture(new double[][][][] {new double[4][5][6]});
    BufferedImage dot = Volumes.picture(new double[][][][] {dot(5, 2, 2, 2)});
    assertTrue(empty.getWidth() > 50 && empty.getHeight() > 50);
    // the voxel in the middle of the volume is drawn in the middle of the picture
    int middle = dot.getRGB(dot.getWidth() / 2, dot.getHeight() / 2) & 0xFF;
    assertTrue(middle > 100, "middle " + middle);
    assertEquals(0, dot.getRGB(1, 1) & 0xFF);
    // three channels are a colour
    double[][][][] red = {dot(5, 2, 2, 2), new double[5][5][5], new double[5][5][5]};
    BufferedImage colour = Volumes.picture(red);
    int rgb = colour.getRGB(colour.getWidth() / 2, colour.getHeight() / 2);
    assertTrue(((rgb >> 16) & 0xFF) > 100 && (rgb & 0xFF) == 0);
  }
}
