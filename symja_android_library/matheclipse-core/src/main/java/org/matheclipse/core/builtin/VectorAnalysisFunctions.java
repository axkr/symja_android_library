package org.matheclipse.core.builtin;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class VectorAnalysisFunctions {


  private static enum Metric {
    UNDEFINED, CARTESIAN, POLAR, CYLINDRICAL, SPHERICAL
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Curl.setEvaluator(new Curl());
      S.Div.setEvaluator(new Div());
      S.Grad.setEvaluator(new Grad());
      S.HessianMatrix.setEvaluator(new HessianMatrix());
      S.Laplacian.setEvaluator(new Laplacian());
      S.RotationMatrix.setEvaluator(new RotationMatrix());
    }
  }

  /**
   *
   *
   * <pre>
   * Curl({f1, f2}, {x1, x2})
   *
   * Curl({f1, f2, f3}, {x1, x2, x3})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the curl.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Curl_%28mathematics%29">Wikipedia - Curl
   * (mathematics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Curl({f(u,v,w),f(v,w,u),f(w,u,v),f(x)}, {u,v,w})
   * {-D(f(v,w,u),w)+D(f(w,u,v),v),-D(f(w,u,v),u)+D(f(u,v,w),w),-D(f(u,v,w),v)+D(f(v,w,u),u),f(x)}
   * </pre>
   */
  private static final class Curl extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Metric metric = Metric.CARTESIAN;
      if (ast.arg2().isList()) {
        IExpr normal = ast.arg1().normal(false);
        if (normal.isList()) {
          IAST list = (IAST) normal;
          IntArrayList dimensions = LinearAlgebra.dimensions(list);
          if (dimensions.size() > 2 || dimensions.size() == 0) {
            return F.NIL;
          }
          IAST variables = (IAST) ast.arg2();
          if (dimensions.getInt(0) != variables.argSize()) {
            // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
            return Errors.printMessage(S.Curl, "ndimv",
                F.List(F.ZZ(variables.argSize()), F.stringx("curl"), F.ZZ(list.argSize()), list),
                engine);
          }
          if (ast.isAST3()) {
            final String metricStr = ast.arg3().toString();
            final int numberOfVars = variables.argSize();
            if (metricStr.equalsIgnoreCase("polar")) {
              metric = Metric.POLAR;
              if (dimensions.size() == 1) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Curl, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                // -(-g+D(f,y))/x+D(g,x)
                return engine.evaluate(F.Plus(
                    F.Times(F.CN1, F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y))), F.D(g, x)));

              }
              if (dimensions.size() == 2) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2 || dimensions.getInt(1) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Curl, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                // 2x2 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(2, 1);
                IExpr i = list.getPart(2, 2);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();

              }


              return F.NIL;
            }


            return F.NIL;
          }

          if (metric == Metric.CARTESIAN) {

            if (dimensions.size() == 1) {
              int dim1 = dimensions.getInt(0);
              IAST f = (IAST) ast.arg1().normal(false);
              IAST v = (IAST) ast.arg2().normal(false);
              if (dim1 == 2 && v.argSize() == 2) {
                // D(f2, v1) - D(f1, v2)
                return F.Subtract(F.D(f.arg2(), v.arg1()), F.D(f.arg1(), v.arg2()));
              }
              if (dim1 == 3 && v.argSize() == 3) {
                // {D(f3, v2) - D(f2, v3), D(f1, v3) - D(f3, v1), D(f2, v1) - D(f1, v2)}
                IASTAppendable curlVector = F.ListAlloc(f.size());
                curlVector.append(F.Subtract(F.D(f.arg3(), v.arg2()), F.D(f.arg2(), v.arg3())));
                curlVector.append(F.Subtract(F.D(f.arg1(), v.arg3()), F.D(f.arg3(), v.arg1())));
                curlVector.append(F.Subtract(F.D(f.arg2(), v.arg1()), F.D(f.arg1(), v.arg2())));
                return curlVector;
              }
            }
          }
        }
      }
      IAST variables = (IAST) ast.arg2();
      if (ast.isAST3()) {
        final String metricStr = ast.arg3().toString();
        final int numberOfVars = variables.argSize();
        if (metricStr.equalsIgnoreCase("polar")) {
          metric = Metric.POLAR;
          if (numberOfVars != 2) {
            // `1` does not define a metric in `2` dimensions.
            return Errors.printMessage(S.Curl, "bdmtrc",
                F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
          }
          IExpr f = ast.arg1();
          IExpr x = variables.arg1();
          IExpr y = variables.arg2();

          // {-D(f,y)/x,D(f,x)}
          return engine.evaluate(F.list(F.Times(F.CN1, F.Power(x, F.CN1), F.D(f, y)), F.D(f, x)));
        }
        if (metricStr.equalsIgnoreCase("cylindrical")) {
          metric = Metric.CYLINDRICAL;
          if (numberOfVars != 3) {
            // `1` does not define a metric in `2` dimensions.
            return Errors.printMessage(S.Curl, "bdmtrc",
                F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
          }
          IExpr f = ast.arg1();
          IExpr x = variables.arg1();
          IExpr y = variables.arg2();
          IExpr z = variables.arg3();

          // {{0,D(f,z),-D(f,y)/x},{-D(f,z),0,D(f,x)},{D(f,y)/x,-D(f,x),0}}
          return engine.evaluate(
              F.list(F.list(F.C0, F.D(f, z), F.Times(F.CN1, F.Power(x, F.CN1), F.D(f, y))),
                  F.list(F.Negate(F.D(f, z)), F.C0, F.D(f, x)),
                  F.list(F.Times(F.Power(x, F.CN1), F.D(f, y)), F.Negate(F.D(f, x)), F.C0)));

        }
        if (metricStr.equalsIgnoreCase("spherical")) {
          metric = Metric.CYLINDRICAL;
          if (numberOfVars != 3) {
            // `1` does not define a metric in `2` dimensions.
            return Errors.printMessage(S.Curl, "bdmtrc",
                F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
          }
          IExpr f = ast.arg1();
          IExpr x = variables.arg1();
          IExpr y = variables.arg2();
          IExpr z = variables.arg3();

          // {{0,(Csc(y)*D(f,z))/x,-D(f,y)/x},{(-Csc(y)*D(f,z))/x,0,D(f,x)},{D(f,y)/x,-D(f,x), 0}}
          return engine.evaluate(F.list(
              F.list(F.C0, F.Times(F.Power(x, F.CN1), F.Csc(y), F.D(f, z)),
                  F.Times(F.CN1, F.Power(x, F.CN1), F.D(f, y))),
              F.list(F.Times(F.CN1, F.Power(x, F.CN1), F.Csc(y), F.D(f, z)), F.C0, F.D(f, x)),
              F.list(F.Times(F.Power(x, F.CN1), F.D(f, y)), F.Negate(F.D(f, x)), F.C0)));


        }

        return F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }


  /**
   *
   *
   * <pre>
   * Div({f1, f2, f3,...},{x1, x2, x3,...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the divergence.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Divergence">Wikipedia - Divergence</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Div({x^2, y^3},{x, y})
   * 2*x+3*y^2
   * </pre>
   */
  private static final class Div extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // if (ast.arg1().isListOfLists()) {
      // return ast.arg1().mapThread(ast, 1);
      // }
      if (ast.arg2().isList()) {
        IExpr normal = ast.arg1().normal(false);
        if (normal.isList()) {
          IAST list = (IAST) normal;
          IntArrayList dimensions = LinearAlgebra.dimensions(list);
          if (dimensions.size() > 2 || dimensions.size() == 0) {
            return F.NIL;
          }
          IAST variables = (IAST) ast.arg2();
          if (dimensions.getInt(0) != variables.argSize()) {
            // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
            return Errors.printMessage(S.Div, "ndimv", F.List(F.ZZ(variables.argSize()),
                F.stringx("divergence"), F.ZZ(list.argSize()), list), engine);
          }
          Metric metric = Metric.CARTESIAN;
          if (ast.isAST3()) {
            final String metricStr = ast.arg3().toString();
            final int numberOfVars = variables.argSize();
            if (metricStr.equalsIgnoreCase("polar")) {
              metric = Metric.POLAR;
              if (dimensions.size() == 1) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                // (f+D(g,y))/x+D(f,x)
                return engine
                    .evaluate(F.Plus(F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(g, y))), F.D(f, x)));
              }
              if (dimensions.size() == 2) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2 || dimensions.getInt(1) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                // 2x2 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(2, 1);
                IExpr i = list.getPart(2, 2);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();

                // (f-i+D(g,y))/x+D(f,x)

                IExpr r1 = F.Plus(F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(i), F.D(g, y))),
                    F.D(f, x));

                // (g+h+D(i,y))/x+D(h,x)
                IExpr r2 = F.Plus(F.Times(F.Power(x, F.CN1), F.Plus(g, h, F.D(i, y))), F.D(h, x));

                return F.List(r1, r2);
              }


              return F.NIL;
            } else if (metricStr.equalsIgnoreCase("cylindrical")) {

              if (dimensions.size() == 1) {
                metric = Metric.CYLINDRICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr h = list.arg3();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // D(h,z)+(f+D(g,y))/x+D(f,x)
                return engine.evaluate(
                    F.Plus(F.D(h, z), F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(g, y))), F.D(f, x)));
              }
              if (dimensions.size() == 2) {
                metric = Metric.CYLINDRICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3 || dimensions.getInt(1) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
                }
                // 3x3 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(1, 3);
                IExpr i = list.getPart(2, 1);
                IExpr j = list.getPart(2, 2);
                IExpr k = list.getPart(2, 3);
                IExpr l = list.getPart(3, 1);
                IExpr m = list.getPart(3, 2);
                IExpr n = list.getPart(3, 3);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // D(h,z)+(f-j+D(g,y))/x+D(f,x)
                IExpr r1 = F.Plus(F.D(h, z),
                    F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(g, y))), F.D(f, x));


                // D(k,z)+(g+i+D(j,y))/x+D(i,x)
                IExpr r2 = F.Plus(F.D(k, z), F.Times(F.Power(x, F.CN1), F.Plus(g, i, F.D(j, y))),
                    F.D(i, x));

                // D(n,z)+(l+D(m,y))/x+D(l,x)
                IExpr r3 =
                    F.Plus(F.D(n, z), F.Times(F.Power(x, F.CN1), F.Plus(l, F.D(m, y))), F.D(l, x));

                return F.List(r1, r2, r3);
              }

              return F.NIL;
            } else if (metricStr.equalsIgnoreCase("spherical")) {

              if (dimensions.size() == 1) {
                metric = Metric.SPHERICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr h = list.arg3();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // (Csc(y)*(Cos(y)*g+f*Sin(y)+D(h,z)))/x+(f+D(g,y))/x+D(f,x)
                return F.Plus(
                    F.Times(F.Power(x, F.CN1), F.Csc(y),
                        F.Plus(F.Times(F.Cos(y), g), F.Times(f, F.Sin(y)), F.D(h, z))),
                    F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(g, y))), F.D(f, x));
              }
              if (dimensions.size() == 2) {
                metric = Metric.SPHERICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3 || dimensions.getInt(1) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
                }
                // 3x3 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(1, 3);
                IExpr i = list.getPart(2, 1);
                IExpr j = list.getPart(2, 2);
                IExpr k = list.getPart(2, 3);
                IExpr l = list.getPart(3, 1);
                IExpr m = list.getPart(3, 2);
                IExpr n = list.getPart(3, 3);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // (Csc(y)*(Cos(y)*g+f*Sin(y)-n*Sin(y)+D(h,z)))/x+(f-j+D(g,y))/x+D(f,x)
                IExpr r1 = F.Plus(
                    F.Times(F.Power(x, F.CN1), F.Csc(y),
                        F.Plus(F.Times(F.Cos(y), g), F.Times(f, F.Sin(y)),
                            F.Times(F.CN1, n, F.Sin(y)), F.D(h, z))),
                    F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(g, y))), F.D(f, x));


                // ((Cos(y)*j-n*Cos(y)+i*Sin(y)+D(k,z)*k)*Csc(y))/x+(g+i+D(j,y))/x+D(i,x)
                IExpr r2 =
                    F.Plus(
                        F.Times(
                            F.Plus(F.Times(F.Cos(y), j), F.Times(F.CN1, n, F.Cos(y)),
                                F.Times(i, F.Sin(y)), F.Times(F.D(k, z), k)),
                            F.Power(x, F.CN1), F.Csc(y)),
                        F.Times(F.Power(x, F.CN1), F.Plus(g, i, F.D(j, y))), F.D(i, x));

                // (Csc(y)*(Cos(y)*k+Cos(y)*m+h*Sin(y)+l*Sin(y)+D(n,z)))/x+(l+D(m,y))/x+D(l,x)
                IExpr r3 = F.Plus(
                    F.Times(F.Power(x, F.CN1), F.Csc(y),
                        F.Plus(F.Times(F.Cos(y), k), F.Times(F.Cos(y), m), F.Times(h, F.Sin(y)),
                            F.Times(l, F.Sin(y)), F.D(n, z))),
                    F.Times(F.Power(x, F.CN1), F.Plus(l, F.D(m, y))), F.D(l, x));

                return F.List(r1, r2, r3);
              }

              return F.NIL;
            }
          }

          if (metric == Metric.CARTESIAN) {
            if (ast.arg1().isListOfLists()) {
              return ast.arg1().mapThread(ast, 1);
            }
            if (dimensions.size() == 1) {
              int size = list.size();
              IASTAppendable divergenceValue = F.PlusAlloc(size);
              return engine.evaluate(
                  divergenceValue.appendArgs(size, i -> F.D(list.get(i), variables.get(i))));
            }
          }
        } else {
          // The scalar expression `1` does not have a `2`.
          return Errors.printMessage(S.Div, "sclr", F.List(ast.arg1(), F.stringx("divergence")),
              engine);
        }
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }


  /**
   * <pre>
   * <code>Grad(function, list-of-variables)
   * </code>
   * </pre>
   * 
   * <p>
   * gives the gradient of the function.
   * </p>
   * 
   * <pre>
   * <code>Grad({f1, f2,...},  {v1, v2,...)
   * </code>
   * </pre>
   * 
   * <p>
   * returns the Jacobian matrix for the vector of functions.
   * </p>
   * 
   * <p>
   * See:
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gradient">Wikipedia - Gradient</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Grad(2*x+3*y^2-Sin(z), {x, y, z})
   * {2,6*y,-Cos(z)}
   * </code>
   * </pre>
   * <p>
   * Create a Jacobian matrix:
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; Grad({f(x, y),g(x,y)}, {x, y})
   * {{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]},{Derivative(1,0)[g][x,y],Derivative(0,1)[g][x,y]}}
   * </code>
   * </pre>
   */
  private static final class Grad extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList() && ast.arg2().size() > 1) {
        final IExpr arg1 = ast.arg1();
        final IAST variables = (IAST) ast.arg2();

        final IExpr function;
        if (arg1.isSparseArray()) {
          int[] dimension = ((ISparseArray) arg1).getDimension();
          if (dimension.length == 1) {
            function = arg1.normal(false);
          } else {
            function = arg1;
          }
        } else {
          function = arg1;
        }

        if (function.isList()) {
          IAST list = (IAST) function;
          IntArrayList dimensions = LinearAlgebra.dimensions(list);
          if (dimensions.size() > 2 || dimensions.size() == 0) {
            return F.NIL;
          }
          Metric metric = Metric.CARTESIAN;
          if (ast.isAST3()) {
            final String metricStr = ast.arg3().toString();
            final int numberOfVars = variables.argSize();
            if (metricStr.equalsIgnoreCase("polar")) {
              metric = Metric.POLAR;
              if (dimensions.size() == 1) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Grad, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();

                // {{D(f,x),(-g+D(f,y))/x},{D(g,x),(f+D(f,y))/x}}
                return F.list(
                    F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y)))),
                    F.list(F.D(g, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(f, y)))));
              }
              if (dimensions.size() == 2) {
                if (numberOfVars != 2 || dimensions.getInt(0) != 2 || dimensions.getInt(1) != 2) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Grad, "bdmtrc",
                      F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
                }
                // 2x2 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(2, 1);
                IExpr i = list.getPart(2, 2);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();

                // {{{D(f,x),(-g-h+D(f,y))/x},{D(g,x),(f-i+D(g,y))/x}},{{D(h,x),(f-i+D(h,y))/x},{D(i,x),(g+h+D(i,y))/x}}}
                return F
                    .list(F.list(
                        F.list(F.D(f, x),
                            F.Times(F.Power(x, F.CN1),
                                F.Plus(F.Negate(g), F.Negate(h), F.D(f, y)))),
                        F.list(F.D(g, x),
                            F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(i), F.D(g, y))))),
                        F.list(
                            F.list(F.D(h, x),
                                F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(i), F.D(h, y)))),
                            F.list(F.D(i, x),
                                F.Times(F.Power(x, F.CN1), F.Plus(g, h, F.D(i, y))))));
              }

            } else if (metricStr.equalsIgnoreCase("cylindrical")) {

              if (dimensions.size() == 1) {
                metric = Metric.CYLINDRICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr h = list.arg3();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // {{D(f,x),(-g+D(f,y))/x,D(f,z)},{D(g,x),(f+D(g,y))/x,D(g,z)},{D(h,x),D(h,y)/x,D(h,z)}}
                return F.list(
                    F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y))),
                        F.D(f, z)),
                    F.list(F.D(g, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(g, y))), F.D(g, z)),
                    F.list(F.D(h, x), F.Times(F.Power(x, F.CN1), F.D(h, y)), F.D(h, z)));

              }
              if (dimensions.size() == 2) {
                metric = Metric.CYLINDRICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3 || dimensions.getInt(1) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
                }
                // 3x3 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(1, 3);
                IExpr i = list.getPart(2, 1);
                IExpr j = list.getPart(2, 2);
                IExpr k = list.getPart(2, 3);
                IExpr l = list.getPart(3, 1);
                IExpr m = list.getPart(3, 2);
                IExpr n = list.getPart(3, 3);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // {{D(f,x),(-g-i+D(f,y))/x,D(f,z)},{D(g,x),(f-j+D(g,y))/x,D(g,z)},{D(h,x),(-k+D(h,y))/x,D(h,z)}}
                IExpr r1 = F.list(
                    F.list(F.D(f, x),
                        F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.Negate(i), F.D(f, y))),
                        F.D(f, z)),
                    F.list(F.D(g, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(g, y))),
                        F.D(g, z)),
                    F.list(F.D(h, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(k), F.D(h, y))),
                        F.D(h, z)));

                // {{D(i,x),(f-j+D(i,y))/x,D(i,z)},{D(j,x),(g+i+D(j,y))/x,D(j,z)},{D(k,x),(h+D(k,y))/x,D(k,z)}}
                IExpr r2 = F.list(
                    F.list(F.D(i, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(i, y))),
                        F.D(i, z)),
                    F.list(F.D(j, x), F.Times(F.Power(x, F.CN1), F.Plus(g, i, F.D(j, y))),
                        F.D(j, z)),
                    F.list(F.D(k, x), F.Times(F.Power(x, F.CN1), F.Plus(h, F.D(k, y))), F.D(k, z)));


                // {{D(l,x),(-m+D(l,y))/x,D(l,z)},{D(m,x),(l+D(m,y))/x,D(m,z)},{D(n,x),D(n,y)/x,D(n,z)}}
                IExpr r3 = F.list(
                    F.list(F.D(l, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(m), F.D(l, y))),
                        F.D(l, z)),
                    F.list(F.D(m, x), F.Times(F.Power(x, F.CN1), F.Plus(l, F.D(m, y))), F.D(m, z)),
                    F.list(F.D(n, x), F.Times(F.Power(x, F.CN1), F.D(n, y)), F.D(n, z)));

                return F.List(r1, r2, r3);
              }

              return F.NIL;
            } else if (metricStr.equalsIgnoreCase("Spherical")) {

              if (dimensions.size() == 1) {
                metric = Metric.SPHERICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
                }
                IExpr f = list.arg1();
                IExpr g = list.arg2();
                IExpr h = list.arg3();
                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // {{D(f,x),(-g+D(f,y))/x,(Csc(y)*(-h*Sin(y)+D(f,z)))/x},{D(g,x),(f+D(g,y))/x,(Csc(y)*(-Cos(y)*h+D(g,z)))/x},{D(h,x),D(h,y)/x,(Csc(y)*(Cos(y)*g+f*Sin(y)+D(h,z)))/x}}
                return F.list(
                    F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, h, F.Sin(y)), F.D(f, z)))),
                    F.list(F.D(g, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.D(g, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, F.Cos(y), h), F.D(g, z)))),
                    F.list(F.D(h, x), F.Times(F.Power(x, F.CN1), F.D(h, y)),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.Cos(y), g), F.Times(f, F.Sin(y)), F.D(h, z)))));

              }
              if (dimensions.size() == 2) {
                metric = Metric.SPHERICAL;
                if (numberOfVars != 3 || dimensions.getInt(0) != 3 || dimensions.getInt(1) != 3) {
                  // `1` does not define a metric in `2` dimensions.
                  return Errors.printMessage(S.Div, "bdmtrc",
                      F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
                }
                // 3x3 matrix
                IExpr f = list.getPart(1, 1);
                IExpr g = list.getPart(1, 2);
                IExpr h = list.getPart(1, 3);
                IExpr i = list.getPart(2, 1);
                IExpr j = list.getPart(2, 2);
                IExpr k = list.getPart(2, 3);
                IExpr l = list.getPart(3, 1);
                IExpr m = list.getPart(3, 2);
                IExpr n = list.getPart(3, 3);

                IExpr x = variables.arg1();
                IExpr y = variables.arg2();
                IExpr z = variables.arg3();

                // {{D(f,x),(-g-i+D(f,y))/x,(Csc(y)*(-h*Sin(y)-l*Sin(y)+D(f,z)))/x},{D(g,x),(f-j+D(g,y))/x,(Csc(y)*(-Cos(y)*h-m*Sin(y)+D(g,z)))/x},{D(h,x),(-k+D(h,y))/x,(Csc(y)*(Cos(y)*g+f*Sin(y)-n*Sin(y)+D(h,z)))/x}}
                IExpr r1 = F.list(
                    F.list(F.D(f, x),
                        F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.Negate(i), F.D(f, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, h, F.Sin(y)), F.Times(F.CN1, l, F.Sin(y)),
                                F.D(f, z)))),
                    F.list(F.D(g, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(g, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, F.Cos(y), h), F.Times(F.CN1, m, F.Sin(y)),
                                F.D(g, z)))),
                    F.list(F.D(h, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(k), F.D(h, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y), F.Plus(F.Times(F.Cos(y), g),
                            F.Times(f, F.Sin(y)), F.Times(F.CN1, n, F.Sin(y)), F.D(h, z)))));

                // {{D(i,x),(f-j+D(i,y))/x,(Csc(y)*(-Cos(y)*l-k*Sin(y)+D(i,z)))/x},{D(j,x),(g+i+D(j,y))/x,(Csc(y)*(-Cos(y)*k-m*Cos(y)+D(j,z)))/x},{D(k,x),(h+D(k,y))/x,(Csc(y)*(Cos(y)*j-n*Cos(y)+i*Sin(y)+D(k,z)))/x}}
                IExpr r2 = F.list(
                    F.list(F.D(i, x), F.Times(F.Power(x, F.CN1), F.Plus(f, F.Negate(j), F.D(i, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, F.Cos(y), l), F.Times(F.CN1, k, F.Sin(y)),
                                F.D(i, z)))),
                    F.list(F.D(j, x), F.Times(F.Power(x, F.CN1), F.Plus(g, i, F.D(j, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.CN1, F.Cos(y), k), F.Times(F.CN1, m, F.Cos(y)),
                                F.D(j, z)))),
                    F.list(F.D(k, x), F.Times(F.Power(x, F.CN1), F.Plus(h, F.D(k, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y), F.Plus(F.Times(F.Cos(y), j),
                            F.Times(F.CN1, n, F.Cos(y)), F.Times(i, F.Sin(y)), F.D(k, z)))));

                // {{D(l,x),(-m+D(l,y))/x,(Csc(y)*(Cos(y)*i+f*Sin(y)-n*Sin(y)+D(l,z)))/x},{D(m,x),(l+D(m,y))/x,(Csc(y)*(Cos(y)*j-n*Cos(y)+g*Sin(y)+D(m,z)))/x},{D(n,x),D(n,y)/x,(Csc(y)*(Cos(y)*k+Cos(y)*m+h*Sin(y)+l*Sin(y)+D(n,z)))/x}}
                IExpr r3 = F.list(
                    F.list(F.D(l, x), F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(m), F.D(l, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.Cos(y), i), F.Times(f, F.Sin(y)),
                                F.Times(F.CN1, n, F.Sin(y)), F.D(l, z)))),
                    F.list(F.D(m, x), F.Times(F.Power(x, F.CN1), F.Plus(l, F.D(m, y))),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.Cos(y), j), F.Times(F.CN1, n, F.Cos(y)),
                                F.Times(g, F.Sin(y)), F.D(m, z)))),
                    F.list(F.D(n, x), F.Times(F.Power(x, F.CN1), F.D(n, y)),
                        F.Times(F.Power(x, F.CN1), F.Csc(y),
                            F.Plus(F.Times(F.Cos(y), k), F.Times(F.Cos(y), m), F.Times(h, F.Sin(y)),
                                F.Times(l, F.Sin(y)), F.D(n, z)))));

                return F.list(r1, r2, r3);


              }

              return F.NIL;
            }
            return F.NIL;
          }
          if (dimensions.size() == 1 && dimensions.getInt(0) > 0) {
            // create jacobian matrix
            return F.Outer.of(engine, S.D, function, variables);
          }
          return F.NIL;
        } else {



          Metric metric = Metric.CARTESIAN;
          if (ast.isAST3()) {
            final String metricStr = ast.arg3().toString();
            final int numberOfVars = variables.argSize();
            if (metricStr.equalsIgnoreCase("polar")) {
              metric = Metric.POLAR;
              if (numberOfVars != 2) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Grad, "bdmtrc",
                    F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
              }
              IExpr f = function;
              IExpr x = variables.arg1();
              IExpr y = variables.arg2();
              // {D(f,x),D(f,y)/x}
              return F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.D(f, y)));
            }
            if (metricStr.equalsIgnoreCase("cylindrical")) {
              if (numberOfVars != 3) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Div, "bdmtrc",
                    F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
              }
              IExpr f = function;
              IExpr x = variables.arg1();
              IExpr y = variables.arg2();
              IExpr z = variables.arg3();

              // {D(f,x),D(f,y)/x,D(f,z)}
              return F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.D(f, y)), F.D(f, z));
            }
            if (metricStr.equalsIgnoreCase("spherical")) {
              if (numberOfVars != 3) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Div, "bdmtrc",
                    F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
              }
              IExpr f = function;
              IExpr x = variables.arg1();
              IExpr y = variables.arg2();
              IExpr z = variables.arg3();

              // {D(f,x),D(f,y)/x,(Csc(y)*D(f,z))/x}
              return F.list(F.D(f, x), F.Times(F.Power(x, F.CN1), F.D(f, y)),
                  F.Times(F.Power(x, F.CN1), F.Csc(y), F.D(f, z)));
            }
          }
        }
        return F.mapList(variables, x -> F.D.of(engine, function, x));
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class HessianMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList() && ast.arg2().size() > 1) {
        final IExpr arg1 = ast.arg1();
        final IAST variables = (IAST) ast.arg2();
        // D(f(x, y), {{x, y}, 2})
        return F.D(arg1, F.List(variables, F.C2));
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class Laplacian extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      final IExpr arg2 = ast.arg2();

      if (arg1.isAST() && arg2.isList()) {
        final IAST variables = (IAST) arg2;
        if (variables.isEmpty()) {
          return F.NIL;
        }

        final IAST fun = (IAST) arg1;
        Metric metric = Metric.CARTESIAN;
        if (ast.isAST3()) {
          final String metricStr = ast.arg3().toString();
          final int numberOfVars = variables.argSize();
          if (metricStr.equalsIgnoreCase("polar")) {
            metric = Metric.POLAR;
            if (arg1.isList()) {
              if (arg1.argSize() != 2) {
                // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
                return Errors.printMessage(S.Laplacian, "ndimv",
                    F.List(F.C2, S.Laplacian, F.ZZ(arg1.argSize()), arg1), engine);
              }
              if (numberOfVars != 2) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Laplacian, "bdmtrc",
                    F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
              }
              return laplacianPolarVector(fun, variables, engine);
            }
            if (numberOfVars != 2) {
              // `1` does not define a metric in `1` dimensions.
              return Errors.printMessage(S.Laplacian, "bdmtrc",
                  F.List(F.stringx("Polar"), F.ZZ(numberOfVars)), engine);
            }
            return laplacianPolar(fun, variables);
          } else if (metricStr.equalsIgnoreCase("cylindrical")) {
            metric = Metric.CYLINDRICAL;
            if (arg1.isList()) {
              if (arg1.argSize() != 3) {
                // // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
                return Errors.printMessage(S.Laplacian, "ndimv",
                    F.List(F.C3, S.Laplacian, F.ZZ(arg1.argSize()), arg1), engine);
              }
              if (numberOfVars != 3) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Laplacian, "bdmtrc",
                    F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
              }
              IAST vector = fun;
              return laplacianCylindricalVector(vector, variables, engine);
            }
            if (numberOfVars != 3) {
              // `1` does not define a metric in `2` dimensions.
              return Errors.printMessage(S.Laplacian, "bdmtrc",
                  F.List(F.stringx("Cylindrical"), F.ZZ(numberOfVars)), engine);
            }
            return laplacianCylindrical(fun, variables);
          } else if (metricStr.equalsIgnoreCase("spherical")) {
            metric = Metric.SPHERICAL;
            if (arg1.isList()) {
              if (arg1.argSize() != 3) {
                // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
                return Errors.printMessage(S.Laplacian, "ndimv",
                    F.List(F.C3, S.Laplacian, F.ZZ(arg1.argSize()), arg1), engine);
              }
              if (numberOfVars != 3) {
                // `1` does not define a metric in `2` dimensions.
                return Errors.printMessage(S.Laplacian, "bdmtrc",
                    F.List(F.stringx("Spherical"), F.ZZ(numberOfVars)), engine);
              }
              IAST vector = fun;
              // if (vector.isFree(variables)) {
              return laplacianSphericalVector(vector, variables, engine);
            }
            if (arg1.argSize() != 3) {
              // There is no `1`-dimensional `2` for the `3`-dimensional vector `4`.
              return Errors.printMessage(S.Laplacian, "ndimv",
                  F.List(F.C3, S.Laplacian, F.ZZ(arg1.argSize()), arg1), engine);
            }
            return laplacianSpherical(fun, variables);
          } else if (metricStr.equalsIgnoreCase("cartesian")) {
            metric = Metric.CARTESIAN;
          } else {
            metric = Metric.UNDEFINED;
          }
        }

        if (metric == Metric.CARTESIAN) {
          if (arg1.isList()) {
            return arg1.mapThread(ast, 1);
          }
          return laplacianCartesian(fun, variables);
        }
      }
      return F.NIL;
    }

    private static IExpr laplacianSphericalVector(IAST vector, final IAST variables,
        EvalEngine engine) {
      IExpr f = vector.arg1();
      IExpr g = vector.arg2();
      IExpr h = vector.arg3();
      IExpr x = variables.arg1();
      IExpr y = variables.arg2();
      IExpr z = variables.arg3();

      // (-(f+D(g,y))/x+(-D(g,y)+D(f,{y,2}))/x+D(f,x))/x+(Csc(y)*(-(Cos(y)*g+f*Sin(y)+D(h,z))/x+(Csc(y)*(-Sin(y)*D(h,z)+D(f,{z,2})))/x+(Cos(y)*(-g+D(f,y)))/x+Sin(y)*D(f,x)))/x+D(f,{x,2})
      IExpr result1 =
          engine.evaluate(F.Plus(
              F.Times(F.Power(x, F.CN1),
                  F.Plus(F.Times(F.CN1, F.Power(x, F.CN1), F.Plus(f, F.D(g, y))),
                      F.Times(F.Power(x, F.CN1),
                          F.Plus(F.Negate(F.D(g, y)), F.D(f, F.list(y, F.C2)))),
                      F.D(f, x))),
              F.Times(F.Power(x, F.CN1), F.Csc(y),
                  F.Plus(
                      F.Times(F.CN1, F.Power(x, F.CN1),
                          F.Plus(F.Times(F.Cos(y), g), F.Times(f, F.Sin(y)), F.D(h, z))),
                      F.Times(F.Power(x, F.CN1), F.Csc(y),
                          F.Plus(F.Times(F.CN1, F.Sin(y), F.D(h, z)), F.D(f, F.list(z, F.C2)))),
                      F.Times(F.Power(x, F.CN1), F.Cos(y), F.Plus(F.Negate(g), F.D(f, y))),
                      F.Times(F.Sin(y), F.D(f, x)))),
              F.D(f, F.list(x, F.C2))));

      // ((-g+D(f,y))/x+(D(f,y)+D(g,{y,2}))/x+D(g,x))/x+(Csc(y)*((-Cot(y)*(Cos(y)*g+f*Sin(y)+D(h,z)))/x+(Csc(y)*(-Cos(y)*D(h,z)+D(g,{z,2})))/x+(Cos(y)*(f+D(g,y)))/x+Sin(y)*D(g,x)))/x+D(x,{x,2})
      IExpr result2 =
          engine.evaluate(F.Plus(
                      F.Times(F.Power(x, F.CN1),
                  F.Plus(F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y))),
                      F.Times(F.Power(x, F.CN1), F.Plus(F.D(f, y), F.D(g, F.list(y, F.C2)))),
                      F.D(g, x))),
              F.Times(F.Power(x, F.CN1), F.Csc(y),
                  F.Plus(
                      F.Times(F.CN1, F.Power(x, F.CN1), F.Cot(y),
                          F.Plus(F.Times(F.Cos(y), g), F.Times(f, F.Sin(y)), F.D(h, z))),
                      F.Times(F.Power(x, F.CN1), F.Csc(y),
                          F.Plus(F.Times(F.CN1, F.Cos(y), F.D(h, z)), F.D(g, F.list(z, F.C2)))),
                      F.Times(F.Power(x, F.CN1), F.Cos(y), F.Plus(f, F.D(g, y))),
                      F.Times(F.Sin(y), F.D(g, x)))),
              F.D(x, F.list(x, F.C2))));

      // (D(h,{y,2})/x+D(h,x))/x+(Csc(y)*((-h*Sin(y)+D(f,z))/x+(Cot(y)*(-Cos(y)*h+D(g,z)))/x+(Csc(y)*(Sin(y)*D(f,z)+Cos(y)*D(g,z)+D(h,{z,2})))/x+(Cos(y)*D(h,y))/x+Sin(y)*D(h,x)))/x+D(h,{x,2})
      IExpr result3 = engine.evaluate(F.Plus(
          F.Times(F.Power(x, F.CN1),
              F.Plus(F.Times(F.Power(x, F.CN1), F.D(h, F.list(y, F.C2))), F.D(h, x))),
          F.Times(F.Power(x, F.CN1), F.Csc(y), F.Plus(
              F.Times(F.Power(x, F.CN1), F.Plus(F.Times(F.CN1, h, F.Sin(y)), F.D(f, z))),
              F.Times(F.Power(x, F.CN1), F.Cot(y), F.Plus(F.Times(F.CN1, F.Cos(y), h), F.D(g, z))),
              F.Times(F.Power(x, F.CN1), F.Csc(y),
                  F.Plus(F.Times(F.Sin(y), F.D(f, z)), F.Times(F.Cos(y), F.D(g, z)),
                      F.D(h, F.list(z, F.C2)))),
              F.Times(F.Power(x, F.CN1), F.Cos(y), F.D(h, y)), F.Times(F.Sin(y), F.D(h, x)))),
          F.D(h, F.list(x, F.C2))));

      return F.List(result1, result2, result3);
    }

    private static IExpr laplacianCylindricalVector(IAST vector, final IAST variables,
        EvalEngine engine) {
      IExpr f = vector.arg1();
      IExpr g = vector.arg2();
      IExpr h = vector.arg3();
      IExpr x = variables.arg1();
      IExpr y = variables.arg2();
      IExpr z = variables.arg3();

      // D(f,{z,2})+(-(f+D(g,y))/x+(-D(g,y)+D(f,{y,2}))/x+D(f,x))/x+D(f,{x,2})
      IExpr result1 =
          engine.evaluate(F.Plus(F.D(f, F.list(z, F.C2)),
              F.Times(F.Power(x, F.CN1),
                  F.Plus(F.Times(F.CN1, F.Power(x, F.CN1), F.Plus(f, F.D(g, y))),
                      F.Times(F.Power(x, F.CN1),
                          F.Plus(F.Negate(F.D(g, y)), F.D(f, F.list(y, F.C2)))),
                      F.D(f, x))),
              F.D(f, F.list(x, F.C2))));

      // D(g,{z,2})+((-g+D(f,y))/x+(D(f,y)+D(g,{y,2}))/x+D(g,x))/x+D(g,{x,2})
      IExpr result2 = engine.evaluate(F.Plus(F.D(g, F.list(z, F.C2)), F.Times(F.Power(x, F.CN1),
          F.Plus(F.Times(F.Power(x, F.CN1), F.Plus(F.Negate(g), F.D(f, y))),
              F.Times(F.Power(x, F.CN1), F.Plus(F.D(f, y), F.D(g, F.list(y, F.C2)))), F.D(g, x))),
          F.D(g, F.list(x, F.C2))));

      // D(h,{z,2})+(D(h,{y,2})/x+D(h,x))/x+D(h,{x,2})
      IExpr result3 = engine.evaluate(F.Plus(F.D(h, F.list(z, F.C2)),
          F.Times(F.Power(x, F.CN1),
              F.Plus(F.Times(F.Power(x, F.CN1), F.D(h, F.list(y, F.C2))), F.D(h, x))),
          F.D(h, F.list(x, F.C2))));

      return F.List(result1, result2, result3);
    }

    private static IExpr laplacianPolarVector(final IAST vector, final IAST variables,
        EvalEngine engine) {
      IExpr f = vector.arg1();
      IExpr g = vector.arg2();
      IExpr r = variables.arg1();
      IExpr t = variables.arg2();

      // (-(f+D(g,t))/r+(-D(g,t)+D(f,{t,2}))/r+D(f,r))/r+D(f,{r,2})
      IExpr result1 =
          engine.evaluate(F.Plus(
              F.Times(F.Power(r, F.CN1),
                  F.Plus(F.Times(F.CN1, F.Power(r, F.CN1), F.Plus(f, F.D(g, t))),
                      F.Times(F.Power(r, F.CN1),
                          F.Plus(F.Negate(F.D(g, t)), F.D(f, F.list(t, F.C2)))),
                      F.D(f, r))),
              F.D(f, F.list(r, F.C2))));

      // ((-g+D(f,t))/r+(D(f,t)+D(g,{t,2}))/r+D(g,r))/r+D(g,{r,2})
      IExpr result2 =
          engine
              .evaluate(
                  F.Plus(
                      F.Times(F.Power(r, F.CN1),
                          F.Plus(F.Times(F.Power(r, F.CN1), F.Plus(F.Negate(g), F.D(f, t))),
                              F.Times(F.Power(r, F.CN1),
                                  F.Plus(F.D(f, t), F.D(g, F.list(t, F.C2)))),
                              F.D(g, r))),
                      F.D(g, F.list(r, F.C2))));

      return F.List(result1, result2);
    }

    /**
     * Return the Laplacian in polar coordinates.
     * 
     * @param f the function
     * @param variables the list of variables which is assumed to have 2 variables
     * @return
     */
    private static IExpr laplacianPolar(IAST f, IAST variables) {
      IExpr r = variables.arg1();
      IExpr t = variables.arg2();

      // (D(f,{t,2})/r + D(f,r))/r + D(f,{r,2})
      return F.Plus(F.Divide(F.Plus(F.Divide(F.D(f, F.List(t, F.C2)), r), F.D(f, r)), r),
          F.D(f, F.List(r, F.C2)));
    }

    /**
     * Return the Laplacian in spherical coordinates.
     * 
     * @param f the function
     * @param variables the list of variables which is assumed to have 3 variables
     * @return
     */
    private static IExpr laplacianSpherical(IAST f, IAST variables) {
      IExpr x = variables.arg1();
      IExpr y = variables.arg2();
      IExpr z = variables.arg3();

      // (D(f,{y,2})/x+D(f,x))/x+(Csc(y)*((Csc(y)*D(f,{z,2}))/x+(Cos(y)*D(f,y))/x+Sin(y)*D(f,x)))/x+D(f,{x,2})
      return F.Plus(
          F.Times(F.Power(x, F.CN1),
              F.Plus(F.Times(F.Power(x, F.CN1), F.D(f, F.list(y, F.C2))), F.D(f, x))),
          F.Times(F.Power(x, F.CN1), F.Csc(y),
              F.Plus(F.Times(F.Power(x, F.CN1), F.Csc(y), F.D(f, F.list(z, F.C2))),
                  F.Times(F.Power(x, F.CN1), F.Cos(y), F.D(f, y)), F.Times(F.Sin(y), F.D(f, x)))),
          F.D(f, F.list(x, F.C2)));
    }

    /**
     * Return the Laplacian in cylindrical coordinates.
     * 
     * @param f the function
     * @param variables the list of variables which is assumed to have 3 variables
     * @return
     */
    private static IExpr laplacianCylindrical(IAST f, IAST variables) {
      IExpr r = variables.arg1();
      IExpr t = variables.arg2();
      IExpr z = variables.arg3();

      // D(f,{z,2}) + (D(f,{t,2})/r + D(f,r))/r + D(f,{r,2})
      return F.Plus(//
          F.D(f, F.List(z, F.C2)), //
          F.Divide(F.Plus(//
              F.Divide(F.D(f, F.List(t, F.C2)), r), //
              F.D(f, r)), r), //
          F.D(f, F.List(r, F.C2)));
    }

    /**
     * Return the Laplacian in Cartesian coordinates.
     * 
     * @param f the function
     * @param variables the list of variables
     * @return
     */
    private static IExpr laplacianCartesian(IAST f, IAST variables) {
      VariablesSet variablesSet = new VariablesSet(f);
      IASTAppendable newVariableList = F.ListAlloc(variables.size());
      for (int i = 1; i <= variables.size(); i++) {
        if (variablesSet.contains(variables.get(i))) {
          newVariableList.append(variables.get(i));
        }
      }

      if (newVariableList.size() <= 1) {
        return F.NIL;
      }
      IASTAppendable laplacian = F.PlusAlloc(newVariableList.argSize());
      for (int i = 1; i < newVariableList.size(); i++) {
        laplacian.append(F.D(f, F.List(newVariableList.get(i), F.C2)));
      }
      return laplacian;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    // set status for partial support
    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  private static final class RotationMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr theta = ast.arg1();
        return
        // [$ {{Cos(theta),-Sin(theta)},{Sin(theta),Cos(theta)}} $]
        F.list(F.list(F.Cos(theta), F.Negate(F.Sin(theta))), F.list(F.Sin(theta), F.Cos(theta))); // $$;
      }
      if (ast.isAST2() && ast.arg2().isAST(S.List, 4)) {
        // TODO generalize for all rotations
        // see https://github.com/corywalker/expreduce/blob/master/expreduce/resources/trig.m
        IExpr theta = ast.arg1();
        IAST vector = (IAST) ast.arg2();
        IExpr x = vector.arg1();
        IExpr y = vector.arg2();
        IExpr z = vector.arg3();
        if (z.isZero()) {
          if (y.isZero()) {
            return
            // [$ {{(x^3*Conjugate(x)^3)/Abs(x)^6,0,0},{0,Cos(theta),-((x^2*Conjugate(x)*
            // Sin(theta))/Abs(x)^3)},{0,(x*Conjugate(x)^2*Sin(theta))/Abs(x)^3,(x^3*Conjugate(x)^3*
            // Cos(theta))/Abs(x)^6}} $]
            F.list(
                F.list(F.Times(F.Power(x, F.C3), F.Power(F.Abs(x), F.CN6),
                    F.Power(F.Conjugate(x), F.C3)), F.C0, F.C0),
                F.list(F.C0, F.Cos(theta),
                    F.Times(F.CN1, F.Sqr(x), F.Power(F.Abs(x), F.CN3), F.Conjugate(x),
                        F.Sin(theta))),
                F.list(F.C0,
                    F.Times(x, F.Power(F.Abs(x), F.CN3), F.Sqr(F.Conjugate(x)), F.Sin(theta)),
                    F.Times(F.Power(x, F.C3), F.Power(F.Abs(x), F.CN6),
                        F.Power(F.Conjugate(x), F.C3), F.Cos(theta)))); // $$;
          }
          if (x.isZero()) {
            return
            // [$
            // {{Cos(theta),0,(y^2*Conjugate(y)*Sin(theta))/Abs(y)^3},{0,(y^3*Conjugate(y)^3)/Abs(y)^6,0},{-((y*Conjugate(y)^2*Sin(theta))/Abs(y)^3),0,(y^3*Conjugate(y)^3*Cos(theta))/Abs(y)^6}}
            // $]
            F.list(
                F.list(F.Cos(theta), F.C0,
                    F.Times(F.Sqr(y), F.Power(F.Abs(y), F.CN3), F.Conjugate(y), F.Sin(theta))),
                F.list(F.C0,
                    F.Times(F.Power(y, F.C3), F.Power(F.Abs(y), F.CN6),
                        F.Power(F.Conjugate(y), F.C3)),
                    F.C0),
                F.list(
                    F.Times(F.CN1, y, F.Power(F.Abs(y), F.CN3), F.Sqr(F.Conjugate(y)),
                        F.Sin(theta)),
                    F.C0, F.Times(F.Power(y, F.C3), F.Power(F.Abs(y), F.CN6),
                        F.Power(F.Conjugate(y), F.C3), F.Cos(theta)))); // $$;
          }
          return F.NIL;
        }
        if (x.isZero() && y.isZero()) {
          return
          // [$
          // {{Cos(theta),-((z*Sin(theta))/Abs(z)),0},{(Conjugate(z)*Sin(theta))/Abs(z),(z*Conjugate(z)*
          // Cos(theta))/Abs(z)^2,0},{0,0,(z*Conjugate(z))/Abs(z)^2}} $]
          F.list(
              F.list(F.Cos(theta), F.Times(F.CN1, z, F.Power(F.Abs(z), F.CN1), F.Sin(theta)), F.C0),
              F.list(F.Times(F.Power(F.Abs(z), F.CN1), F.Conjugate(z), F.Sin(theta)),
                  F.Times(z, F.Power(F.Abs(z), F.CN2), F.Conjugate(z), F.Cos(theta)), F.C0),
              F.list(F.C0, F.C0, F.Times(z, F.Power(F.Abs(z), F.CN2), F.Conjugate(z)))); // $$;
        }
        return F.NIL;
      }
      return F.NIL;
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private VectorAnalysisFunctions() {}
}
