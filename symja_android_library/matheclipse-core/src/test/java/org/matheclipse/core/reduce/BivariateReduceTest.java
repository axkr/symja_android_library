package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * <code>Reduce</code> of polynomial inequalities in two real variables, where the second one is of
 * degree 1: a cylindrical decomposition.
 */
public class BivariateReduceTest extends ExprEvaluatorTestCase {

  @Test
  public void testLinearInTheSecondVariable() {
    // the cells of x are the roots of the coefficients of y and the values between them
    check("Reduce(x/y<=0, {x,y}, Reals)", //
        "(x<0&&y>0)||(x==0&&(y<0||y>0))||(x>0&&y<0)");
    check("Reduce(x/y<=0, {x,y})", //
        "(x<0&&y>0)||(x==0&&(y<0||y>0))||(x>0&&y<0)");
    check("Reduce(x/y<=0, {y,x}, Reals)", //
        "(y<0&&x>=0)||(y>0&&x<=0)");
    check("Reduce(x*y>1, {x,y}, Reals)", //
        "(x<0&&y<1/x)||(x>0&&y>1/x)");
    // where two of the bounds of y meet
    check("Reduce(x*y>1 && y<x, {x,y}, Reals)", //
        "(x<=-1&&y<x)||(x>-1&&x<0&&y<1/x)||(x>1&&y>1/x&&y<x)");
    check("Reduce(x^2+y<1 && y>0, {x,y}, Reals)", //
        "x>-1&&x<1&&y>0&&y<1-x^2");
    check("Reduce(Abs(x)+Abs(y)<1, {x,y}, Reals)", //
        "(x>-1&&x<=0&&y>-1-x&&y<1+x)||(x>0&&x<1&&y>-1+x&&y<1-x)");
    check("Reduce(x^2*y<1 && x*y>-1, {x,y}, Reals)", //
        "(x<=-1&&y<1/x^2)||(x>-1&&x<0&&y<-1/x)||x==0||(x>0&&y>-1/x&&y<1/x^2)");
    check("Reduce((x^2-2)*y>1, {x,y}, Reals)", //
        "(x<-Sqrt(2)&&y>1/(-2+x^2))||(x>-Sqrt(2)&&x<Sqrt(2)&&y<1/(-2+x^2))||(x>Sqrt(2)&&y>1/(-\n" //
            + "2+x^2))");
  }

  @Test
  public void testWorkingPrecision() {
    check("Reduce(x*y>1 && x^2<2, {x,y}, Reals, WorkingPrecision->20)", //
        "(x>-1.4142135623730950488&&x<0&&y<1/x)||(x>0&&x<1.4142135623730950488&&y>1/x)");
  }

  @Test
  public void testDenominators() {
    // the poles are no solutions, and the second variable is the one which is solved for
    check("Reduce(1/x+1/y==1 && x>0, {x,y}, Reals)", //
        "(x>0&&x<1&&y==x/(-1+x))||(x>1&&y==x/(-1+x))");
    check("Reduce(1/x+1/y==1, {x,y}, Reals)", //
        "(x<0&&y==x/(-1+x))||(x>0&&x<1&&y==x/(-1+x))||(x>1&&y==x/(-1+x))");
    check("Reduce(x/y==2, {x,y}, Reals)", //
        "(x<0&&y==x/2)||(x>0&&y==x/2)");
    check("Reduce(1/(x-y)>0, {x,y}, Reals)", //
        "y<x");
  }

  @Test
  public void testFactors() {
    // a factor without x has constant roots
    check("Reduce((x-1)/(y^2-1)>0, {x,y}, Reals)", //
        "(x<1&&y>-1&&y<1)||(x>1&&(y<-1||y>1))");
    check("Reduce((x-y)*(x+y)>0, {x,y}, Reals)", //
        "(x<0&&y>x&&y<-x)||(x>0&&y>-x&&y<x)");
    check("Reduce(x^2>y^2, {x,y}, Reals)", //
        "(x<0&&y>x&&y<-x)||(x>0&&y>-x&&y<x)");
    check("Reduce(y^3-y>0 && x>y, {x,y}, Reals)", //
        "(x>-1&&x<=0&&y>-1&&y<x)||(x>0&&x<=1&&y>-1&&y<0)||(x>1&&((y>-1&&y<0)||(y>1&&y<x)))");
  }

  @Test
  public void testDeclined() {
    // degree 2 in the second variable
    check("Reduce(x^2+y^2<1, {x,y}, Reals)", //
        "Reduce(x^2+y^2<1,{x,y},Reals)");
    check("Reduce(x*y^2>1, {x,y}, Reals)", //
        "Reduce(x*y^2>1,{x,y},Reals)");
    // a parameter
    check("Reduce(x/y>a, {x,y}, Reals)", //
        "Reduce(x/y>a,{x,y},Reals)");
  }

  private static final String[] RELATIONS = {"<", "<=", "==", "!=", ">", ">="};

  /** A polynomial of degree 1 in y, with small coefficients. */
  private static String linearInY(Random random) {
    String[] coefficients = {"x", "x-1", "x+2", "x^2-1", "1", "2", "x^2+1", "-x"};
    String[] constants = {"1", "x", "-2", "x^2", "x-3", "0", "2*x+1"};
    return "(" + coefficients[random.nextInt(coefficients.length)] + ")*y+("
        + constants[random.nextInt(constants.length)] + ")";
  }

  private static String atom(Random random) {
    String relation = RELATIONS[random.nextInt(RELATIONS.length)];
    switch (random.nextInt(5)) {
      case 0:
        return "x" + relation + (random.nextInt(5) - 2);
      case 1:
        return "y^2" + relation + (1 + random.nextInt(3));
      default:
        return linearInY(random) + relation + "0";
    }
  }

  private static String formula(Random random) {
    String a = atom(random);
    String b = atom(random);
    switch (random.nextInt(4)) {
      case 0:
        return a;
      case 1:
        return "(" + a + ")&&(" + b + ")";
      case 2:
        return "(" + a + ")||(" + b + ")";
      default:
        return "((" + a + ")&&(" + b + "))||(" + atom(random) + ")";
    }
  }

  /**
   * The result has the truth value of the formula at every point of a grid. The grid has the
   * critical values of the generated formulas among its points.
   */
  @Test
  public void testAgainstGrid() {
    Random random = new Random(7102026L);
    int compared = 0;
    for (int i = 0; i < 60; i++) {
      String phi = formula(random);
      String reduced = evaluator.eval("Reduce(" + phi + ", {x,y}, Reals)").toString();
      if (reduced.startsWith("Reduce(")) {
        continue;
      }
      String differences = evaluator.eval("Quiet(Count(Flatten(Table(TrueQ(" + reduced
          + ")===TrueQ(" + phi + "), {x,-3,3,1/2}, {y,-7/2,7/2,1/4})), False))").toString();
      assertEquals("0", differences, phi + " reduced to " + reduced);
      compared++;
    }
    assertTrue(compared > 40, "compared " + compared);
  }
}
