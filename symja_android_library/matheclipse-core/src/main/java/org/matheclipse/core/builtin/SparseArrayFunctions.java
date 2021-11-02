package org.matheclipse.core.builtin;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;

public class SparseArrayFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ArrayRules.setEvaluator(new ArrayRules());
      S.SparseArray.setEvaluator(new SparseArray());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ArrayRules(sparse-array)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>return the array of rules which define the sparse array.
   *
   * </blockquote>
   *
   * <pre>
   * <code>ArrayRules(nested-lists)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>return the array of rules which define the nested lists.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; a = {{{0,0},{1,1}},{{0,1},{0,1}}}
   * {{{0,0},{1,1}},{{0,1},{0,1}}}
   *
   * &gt;&gt; ArrayRules(a)
   * {{1,2,1}-&gt;1,{1,2,2}-&gt;1,{2,1,2}-&gt;1,{2,2,2}-&gt;1,{_,_,_}-&gt;0}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p><a href="SparseArray.md">SparseArray</a>
   */
  private static class ArrayRules extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr defaultValue = F.C0;
      if (ast.isAST2()) {
        defaultValue = ast.arg2();
      }
      if (arg1.isSparseArray()) {

        ISparseArray sparseArray = (ISparseArray) arg1;
        IExpr d = sparseArray.getDefaultValue();
        if (ast.isAST1()) {
          defaultValue = d;
        } else if (ast.isAST2()) {
          if (!d.equals(defaultValue)) {
            LOGGER.log(
                engine.getLogLevel(),
                "{}: Sparse array default value: {} unequals default value {}",
                ast.topHead(),
                d,
                defaultValue);
            return F.NIL;
          }
        }
        return sparseArray.arrayRules();
      }
      if (arg1.isList()) {
        return SparseArrayExpr.arrayRules((IAST) arg1, defaultValue);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre><code>SparseArray(nested-list)
   * </code></pre>
   *
   * <blockquote>
   *
   * <p>create a sparse array from a <code>nested-list</code> structure.
   *
   * </blockquote>
   *
   * <pre><code>SparseArray(array-rules, list-of-integers, default-value)
   * </code></pre>
   *
   * <blockquote>
   *
   * <p>create a sparse array from <code>array-rules</code> with dimension <code>list-of-integers
   * </code> and undefined elements are having <code>default-value</code>.
   *
   * </blockquote>
   *
   * <pre><code>SparseArray(Automatic, list-of-integers, default-value, crs-list)
   * </code></pre>
   *
   * <blockquote>
   *
   * <p>create a sparse array from the compressed-row-storage <code>crs-list</code> with dimension
   * <code>list-of-integers</code> and undefined elements are having <code>default-value</code>.
   *
   * </blockquote>
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="http://netlib.org/utk/papers/templates/node91.html">Netlib - Compressed Row
   *       Storage (CRS)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; SparseArray({{1, 1} -&gt; 1, {2, 2} -&gt; 2, {3, 3} -&gt; 3, {1, 3} -&gt; 4})
   * SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)
   *
   * &gt;&gt; SparseArray({{1, 1} -&gt; 1, {2, 2} -&gt; 2, {3, 3} -&gt; 3, {1, 3} -&gt; 4}, Automatic, 0)
   * SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)
   * </code></pre>
   */
  private static class SparseArray extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isSparseArray()) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      IExpr defaultValue = F.NIL;
      if (ast.size() >= 4) {
        defaultValue = ast.arg3();
      }
      int defaultDimension = -1;
      int[] dimension = null;
      if (ast.size() >= 3) {
        IExpr arg2 = ast.arg2();
        if (arg2.equals(S.Automatic)) {
          // automatic detection enabled
        } else if (arg2.isList()) {
          // List of Java int numbers expected in `1`.
          dimension = Validate.checkListOfInts(ast, arg2, 1, Integer.MAX_VALUE, engine);
        } else {
          defaultDimension = arg2.toIntDefault();
          if (defaultDimension < 0) {
            return F.NIL;
          }
        }
        if (ast.size() == 5) {
          if (dimension != null && arg1.equals(S.Automatic)) {
            if (ast.arg4().isList3()) {
              IAST list = (IAST) ast.arg4();
              int version = list.arg1().toIntDefault();
              if (version == 1 && list.arg2().isList2() && list.arg3().isList()) {
                IAST second = (IAST) list.arg2();
                int[] columnIndicesDimension = second.arg2().isMatrix(false);
                if (second.arg1().isList()
                    && //
                    columnIndicesDimension != null) {
                  int[] rowPointers =
                      Validate.checkListOfInts(ast, second.arg1(), 0, Integer.MAX_VALUE, engine);
                  IAST columnIndices = (IAST) second.arg2();
                  IAST nonZeroValues = (IAST) list.arg3();
                  ISparseArray result =
                      SparseArrayExpr.newInputForm(
                          dimension, defaultValue, rowPointers, columnIndices, nonZeroValues);
                  if (result != null) {
                    return result;
                  }
                }
              }
              return F.NIL;
            }
          }
        }
      }
      ISparseArray result = null;

      if (arg1.isListOfRules()) {
        if (arg1.size() < 2) {
          // The dimensions cannot be determined from the position `1`.
          return IOFunctions.printMessage(ast.topHead(), "exdims", F.List(arg1), engine);
        }
        result =
            SparseArrayExpr.newArrayRules((IAST) arg1, dimension, defaultDimension, defaultValue);
      } else if (arg1.isList()) {
        if (arg1.size() < 2) {
          // The dimensions cannot be determined from the position `1`.
          return IOFunctions.printMessage(ast.topHead(), "exdims", F.List(arg1), engine);
        }
        result = SparseArrayExpr.newDenseList((IAST) arg1.normal(false), defaultValue);
      } else if (arg1.isSparseArray()) {
        SparseArrayExpr sparseArray = (SparseArrayExpr) arg1;
        boolean checkedDimensions = true;
        if (dimension != null) {
          int[] dims = sparseArray.getDimension();
          checkedDimensions = Arrays.equals(dimension, dims);
        }

        if (checkedDimensions) {
          if (!defaultValue.isPresent() || defaultValue.equals(sparseArray.getDefaultValue())) {
            return sparseArray;
          }
          if (defaultValue.isPresent()) {
            IAST list = sparseArray.normal(false);
            if (list.isPresent()) {
              result = SparseArrayExpr.newDenseList(list, defaultValue);
            }
          }
        }
        if (dimension != null) {
          IAST list = sparseArray.normal(dimension);
          if (list.isPresent()) {
            result = SparseArrayExpr.newDenseList(list, defaultValue);
          }
        }

      } else if (arg1.isRule()) {
        result =
            SparseArrayExpr.newArrayRules(F.List(arg1), dimension, defaultDimension, defaultValue);
      }
      if (result != null) {
        return result;
      }

      // List expected at position `1` in `2`.
      return IOFunctions.printMessage(ast.topHead(), "list", F.List(F.C1, ast), engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SparseArrayFunctions() {}
}
