package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Checks the marching cubes triangle table against the rule that defines it.
 *
 * <p>
 * A row is correct exactly when it references the edges that its own corner signs cut, and no
 * others: an edge is cut when its two corners fall on opposite sides of the contour. A row that
 * breaks the rule leaves a hole in every surface that reaches that configuration, and a hole is
 * hard to attribute by eye to one of 256 table entries. The table this replaced had grown to 368
 * rows of which 149 broke the rule, so {@code ContourPlot3D} produced surfaces full of gaps.
 */
public class MarchingCubesTablesTest {

  @Test
  public void theTableHasOneRowPerConfiguration() {
    assertEquals(256, MarchingCubesTables.TRI_TABLE.length);
    for (int i = 0; i < MarchingCubesTables.TRI_TABLE.length; i++) {
      assertEquals(16, MarchingCubesTables.TRI_TABLE[i].length, "row " + i);
    }
  }

  @Test
  public void everyRowUsesExactlyTheEdgesItsCornersCut() {
    List<String> failures = new ArrayList<>();
    for (int configuration =
        0; configuration < MarchingCubesTables.CONFIGURATIONS; configuration++) {
      boolean[] inside = new boolean[8];
      for (int corner = 0; corner < 8; corner++) {
        inside[corner] = ((configuration >> corner) & 1) == 1;
      }
      Set<Integer> cut = new HashSet<>();
      for (int edge = 0; edge < MarchingCubesTables.EDGE_VERTICES.length; edge++) {
        int a = MarchingCubesTables.EDGE_VERTICES[edge][0];
        int b = MarchingCubesTables.EDGE_VERTICES[edge][1];
        if (inside[a] != inside[b]) {
          cut.add(edge);
        }
      }

      Set<Integer> used = new HashSet<>();
      int count = 0;
      for (int value : MarchingCubesTables.getTriangles(configuration)) {
        if (value == -1) {
          break;
        }
        used.add(value);
        count++;
      }
      if (count % 3 != 0) {
        failures.add(configuration + ": " + count + " edges is not a whole number of triangles");
      } else if (!used.equals(cut)) {
        failures.add(configuration + ": uses " + used + " but its corners cut " + cut);
      }
    }
    assertTrue(failures.isEmpty(), "these configurations do not triangulate their own cut edges:\n"
        + String.join("\n", failures));
  }

  /** The two numberings have to agree with each other, since the table is written against both. */
  @Test
  public void theCornerAndEdgeNumberingAreConsistent() {
    assertEquals(8, MarchingCubesTables.VERTEX_OFFSETS.length);
    assertEquals(12, MarchingCubesTables.EDGE_VERTICES.length);
    for (int[] edge : MarchingCubesTables.EDGE_VERTICES) {
      int[] a = MarchingCubesTables.VERTEX_OFFSETS[edge[0]];
      int[] b = MarchingCubesTables.VERTEX_OFFSETS[edge[1]];
      int differences = 0;
      for (int axis = 0; axis < 3; axis++) {
        if (a[axis] != b[axis]) {
          differences++;
        }
      }
      assertEquals(1, differences,
          "an edge must join two corners that differ along exactly one axis");
    }
  }
}
