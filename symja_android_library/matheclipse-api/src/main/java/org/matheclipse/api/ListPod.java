package org.matheclipse.api;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ListPod implements IPod {
  final IAST list;

  public ListPod(IAST list) {
    this.list = list;
  }

  @Override
  public short podType() {
    return LIST_DATA;
  }

  @Override
  public String keyWord() {
    return "list";
  }

  private static int integerListPods(ArrayNode podsArray, IExpr inExpr, IAST list, int formats,
      ObjectMapper mapper, EvalEngine engine) {
    int numpods = 0;

    inExpr = F.Total(list);
    IExpr podOut = engine.evaluate(inExpr);
    Pods.addSymjaPod(podsArray, inExpr, podOut, "Total", "List", formats, engine);
    numpods++;

    inExpr = F.N(F.Norm(list));
    podOut = engine.evaluate(inExpr);
    Pods.addSymjaPod(podsArray, inExpr, podOut, "Vector length", "List", formats, engine);
    numpods++;

    inExpr = F.Normalize(list);
    podOut = engine.evaluate(inExpr);
    Pods.addSymjaPod(podsArray, inExpr, podOut, "Normalized vector", "List", formats, engine);
    numpods++;

    return numpods;
  }

  @Override
  public int addJSON(ArrayNode podsArray, int formats, EvalEngine engine) {

    int numpods = 0;
    boolean intList = list.forAll(x -> x.isInteger());
    IExpr inExpr = list;

    if (list.argSize() == 2) {
      VariablesSet varSet = new VariablesSet(list);
      IAST variables = varSet.getVarList();
      if (variables.argSize() == 1) {
        IExpr arg1 = list.arg1();
        IExpr arg2 = list.arg2();
        if (arg1.isNumericFunction(varSet) && arg2.isNumericFunction(varSet)) {
          boolean isPoly1 = arg1.isPolynomial(variables);
          boolean isPoly2 = arg2.isPolynomial(variables);
          if (isPoly1 && isPoly2) {
            inExpr = F.PolynomialQuotientRemainder(arg1, arg2, variables.arg1());
            IExpr podOut = engine.evaluate(inExpr);
            Pods.addSymjaPod(podsArray, inExpr, podOut, "Polynomial quotient and remainder",
                "Polynomial", formats, engine);
            numpods++;
          }
        }
      }
    }

    if (intList) {
      numpods += integerListPods(podsArray, inExpr, list, formats, Pods.JSON_OBJECT_MAPPER, engine);
    }

    int[] matrixDimension = list.isMatrix();
    if (matrixDimension != null) {
      if (matrixDimension[0] >= 2 && //
          matrixDimension[1] >= 2) {
        if (matrixDimension[0] <= Config.MAX_MATRIX_DIMENSION_SIZE && //
            matrixDimension[1] <= Config.MAX_MATRIX_DIMENSION_SIZE) {
          double[][] matrix = list.toDoubleMatrix();
          if (matrix == null) {
            if (matrixDimension[0] == matrixDimension[1]) {
              IExpr hermitianMatrixQ = F.HermitianMatrixQ(list);
              boolean isHermitian = engine.evalTrue(hermitianMatrixQ);
              if (isHermitian) {
                Pods.addSymjaPod(podsArray, hermitianMatrixQ, F.NIL,
                    "The matrix is hermitian (self-adjoint).", "Properties", "Matrix", formats,
                    engine);
                numpods++;
              }
            }
          } else {
            if (matrixDimension[0] == matrixDimension[1]) {
              IExpr symmetricMatrixQ = F.SymmetricMatrixQ(list);
              boolean isSymmetric = engine.evalTrue(symmetricMatrixQ);
              if (isSymmetric) {
                Pods.addSymjaPod(podsArray, symmetricMatrixQ, F.NIL, "The matrix is symmetric.",
                    "Properties", "Matrix", formats, engine);
                numpods++;
              }
              IExpr detMatrix = F.Det(list); // new ASTRealMatrix(matrix, false));
              IExpr podOut = engine.evaluate(detMatrix);
              if (detMatrix.isZero()) {
                Pods.addSymjaPod(podsArray, symmetricMatrixQ, F.NIL, "The matrix is singular",
                    "Properties", "Matrix", formats, engine);
                numpods++;
              } else {
                Pods.addSymjaPod(podsArray, detMatrix, F.NIL,
                    "The determinant of the matrix is " + podOut.toString(), "Properties", "Matrix",
                    formats, engine);
                numpods++;

                inExpr = F.Inverse(list);
                podOut = engine.evaluate(inExpr);
                Pods.addSymjaPod(podsArray, inExpr, podOut, "Inverse of matrix", "Matrix", formats,
                    engine);
                numpods++;
              }
            }
            if (matrixDimension[1] == 2) {
              ASTRealMatrix m = new ASTRealMatrix(matrix, false);
              IExpr plot2D = F.ListPlot(m);
              IExpr podOut = engine.evaluate(plot2D);
              if (podOut.isAST(S.JSFormData, 3)) {
                int form = Pods.internFormat(Pods.SYMJA, podOut.second().toString());
                Pods.addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                    IStringX.inputForm(plot2D), "Plot points", "Plotter", form, engine);
                numpods++;
              }
            }
          }
        }
      }
    } else {
      double[] vector = list.toDoubleVector();
      if (vector != null) {
        ASTRealVector v = new ASTRealVector(vector, false);
        if (!intList) {
          inExpr = F.Total(v);
          IExpr podOut = engine.evaluate(inExpr);
          Pods.addSymjaPod(podsArray, inExpr, podOut, "Total", "List", formats, engine);
          numpods++;

          inExpr = F.Norm(v);
          podOut = engine.evaluate(inExpr);
          Pods.addSymjaPod(podsArray, inExpr, podOut, "Vector length", "List", formats, engine);
          numpods++;

          inExpr = F.Normalize(v);
          podOut = engine.evaluate(inExpr);
          Pods.addSymjaPod(podsArray, inExpr, podOut, "Normalized vector", "List", formats, engine);
          numpods++;
        }

        if (vector.length > 2) {
          IExpr plot2D = F.ListPlot(v);
          IExpr podOut = engine.evaluate(plot2D);
          if (podOut.isAST(S.JSFormData, 3)) {
            int form = Pods.internFormat(Pods.SYMJA, podOut.second().toString());
            Pods.addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                IStringX.inputForm(plot2D), "Plot points", "Plotter", form, engine);
            numpods++;
          }
        }
      }

      if (list.argSize() > 5) {
        IExpr fivenum = F.FiveNum(list);
        IExpr podOut = engine.evaluate(fivenum);
        Pods.addSymjaPod(podsArray, fivenum, podOut, "Five-number summary", "Statistics", formats,
            engine);
        numpods++;

        IExpr histogram = F.Histogram(list);
        podOut = engine.evaluate(histogram);
        if (podOut.isAST(S.JSFormData, 3)) {
          int form = Pods.internFormat(Pods.SYMJA, podOut.second().toString());
          Pods.addPod(podsArray, inExpr, podOut, podOut.first().toString(),
              IStringX.inputForm(histogram), "Histogram", "Plotter", form, engine);
          numpods++;
        }
      }
    }
    return numpods;
  }
}
