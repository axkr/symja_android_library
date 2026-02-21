package org.matheclipse.core.graphics;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A builder utility to construct highly optimized GraphicsComplex objects. Separating coordinates
 * from topology drastically reduces AST sizes and ensures smooth VertexNormal interpolation across
 * adjacent faces in WebGL.
 */
public class GraphicsComplexBuilder {

  private final IASTAppendable vertices;
  private final IASTAppendable normals;
  private final IASTAppendable colors;
  private final IASTAppendable polygonIndices;
  private final IASTAppendable otherPrimitives;
  private final Map<String, Integer> vertexMap;

  private int vertexCounter;
  private final boolean useNormals;
  private final boolean useColors;
  private final IASTAppendable styles;

  /**
   * Defines styling primitives (e.g. RGBColor, EdgeForm) that should be prepended before the
   * geometry in the final GraphicsComplex.
   */
  public void setStyle(IExpr... styleExprs) {
    for (IExpr style : styleExprs) {
      styles.append(style);
    }
  }

  /**
   * Assembles the final GraphicsComplex AST.
   * 
   * @return The AST expression or F.NIL if no vertices were added.
   */
  public IExpr build() {
    if (vertices.argSize() == 0) {
      return F.NIL;
    }

    IASTAppendable gc = F.ast(S.GraphicsComplex);
    gc.append(vertices);

    IASTAppendable allPrimitives = F.ListAlloc();

    // Prepend styles (Color, EdgeForm, Opacity, etc.)
    for (int i = 1; i <= styles.argSize(); i++) {
      allPrimitives.append(styles.get(i));
    }

    // Append geometry
    if (polygonIndices.argSize() > 0) {
      allPrimitives.append(F.Polygon(polygonIndices));
    }
    for (int i = 1; i <= otherPrimitives.argSize(); i++) {
      allPrimitives.append(otherPrimitives.get(i));
    }

    gc.append(allPrimitives);

    if (useNormals && normals.argSize() > 0) {
      gc.append(F.Rule(S.VertexNormals, normals));
    }
    if (useColors && colors.argSize() > 0) {
      gc.append(F.Rule(S.VertexColors, colors));
    }

    return gc;
  }

  public GraphicsComplexBuilder(boolean useNormals, boolean useColors) {
    this.vertices = F.ListAlloc();
    this.normals = useNormals ? F.ListAlloc() : null;
    this.colors = useColors ? F.ListAlloc() : null;
    this.polygonIndices = F.ListAlloc();
    this.otherPrimitives = F.ListAlloc();
    this.vertexMap = new HashMap<>();
    this.vertexCounter = 1;
    this.useNormals = useNormals;
    this.useColors = useColors;
    this.styles = F.ListAlloc();
  }

  /**
   * Adds a vertex to the pool, automatically welding it to existing vertices to guarantee a
   * seamless continuous mesh for lighting calculation.
   */
  public int addVertex(double x, double y, double z, double[] normal, IExpr color) {
    // Scaled rounding eliminates microscopic floating-point seam gaps
    long kx = Math.round(x * 1e5);
    long ky = Math.round(y * 1e5);
    long kz = Math.round(z * 1e5);
    String key = kx + "_" + ky + "_" + kz;

    if (!vertexMap.containsKey(key)) {
      vertexMap.put(key, vertexCounter++);
      vertices.append(F.List(F.num(x), F.num(y), F.num(z)));

      if (useNormals && normal != null && normal.length >= 3) {
        normals.append(F.List(F.num(normal[0]), F.num(normal[1]), F.num(normal[2])));
      }
      if (useColors && color != null) {
        colors.append(color);
      }
    }
    return vertexMap.get(key);
  }

  /**
   * Append a triangle face to the shared polygon group.
   */
  public void addPolygon(int v1, int v2, int v3) {
    polygonIndices.append(F.List(F.ZZ(v1), F.ZZ(v2), F.ZZ(v3)));
  }

  /**
   * Append an arbitrary n-gon face.
   */
  public void addPolygon(int[] indices) {
    IASTAppendable polyArgs = F.ListAlloc(indices.length);
    for (int idx : indices) {
      polyArgs.append(F.ZZ(idx));
    }
    polygonIndices.append(polyArgs);
  }

  /**
   * Append independent primitives like Point or Line that rely on the index map.
   */
  public void addPrimitive(IExpr primitive) {
    otherPrimitives.append(primitive);
  }

}
