package org.matheclipse.core.examples;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.LinAlg;

public class JASIssue21RowReduce {

  /**
   * Example for <a href="https://github.com/kredel/java-algebra-system/issues/21">JAS issue 21</a>
   *
   * @param args
   */
  public static void main(String[] args) {
    GenMatrix<IExpr> matrix;

    int[] dims = new int[] {3, 3};
    if (dims != null) {
      // RowReduce( {{1, a, 2}, {0, 1, 1}, {-1, 1, 1}} )
      final IExpr[][] elements = new IExpr[dims[0]][dims[1]];
      elements[0][0] = F.ZZ(1);
      elements[0][1] = F.a; // symbolic variable
      elements[0][2] = F.ZZ(2);

      elements[1][0] = F.ZZ(0);
      elements[1][1] = F.ZZ(1);
      elements[1][2] = F.ZZ(1);

      elements[2][0] = F.ZZ(-1);
      elements[2][1] = F.ZZ(1);
      elements[2][2] = F.ZZ(1);

      GenMatrixRing<IExpr> ring = new GenMatrixRing<IExpr>(ExprRingFactory.CONST, dims[0], dims[1]);
      matrix = new GenMatrix<IExpr>(ring, elements);

      LinAlg<IExpr> lalg = new LinAlg<IExpr>();
      GenMatrix<IExpr> rowEchelonForm = lalg.rowEchelonForm(matrix);

      // prints wrong result:
      // [
      // [ 1, a, 2 ],
      // [ 0, 1, -1/(1-a) ],
      // [ 0, 0, 1 ] ]
      System.out.println(rowEchelonForm);

      // should return
      // [
      // [ 1, 0, 0 ],
      // [ 0, 1, 0 ],
      // [ 0, 0, 1 ] ]
    }
  }
}
