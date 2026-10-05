package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;
import org.matheclipse.core.system.TestTags;

/**
 * <code>Resolve</code> of statements with atoms of degree 2: quantifier elimination by virtual
 * substitution.
 */
public class QuadraticQETest extends ExprEvaluatorTestCase {

  @Test
  public void testLimitStatements() {
    // x^2 -> Infinity for x -> Infinity: a quadratic atom, where the linear elimination gives up
    check("Resolve(ForAll(m, m>0, Exists(n, n>0, ForAll(x, x>n, x^2>m))), Reals)", //
        "True");
    check("Resolve(ForAll(m, m>0, Exists(n, n>0, ForAll(x, x>n, x^2<m))), Reals)", //
        "False");
    // the product of an outer and an inner variable
    check("Resolve(ForAll(m, m>0, Exists(dl, dl>0, ForAll(x, 0<x<dl, m*x<1))), Reals)", //
        "True");
    check("Resolve(ForAll(m, m>0, Exists(dl, dl>0, ForAll(x, 0<x<dl, m*x>1))), Reals)", //
        "False");
    check("Resolve(ForAll(ep, ep>0, Exists(n, n>0, ForAll(x, x>n, ep*x>1))), Reals)", //
        "True");
    // 1/x^2 -> Infinity for x -> 0: the atom m*x^4-x^2 < 0 by the signs of its factors
    check("Resolve(ForAll(m, m>0, Exists(dl, dl>0, ForAll(x, 0<Abs(x)<dl, 1/x^2>m))), Reals)", //
        "True");
    check("Resolve(ForAll(ep, ep>0, Exists(n, n>0, ForAll(x, x>n, 1/x^2<ep))), Reals)", //
        "True");
    check("Resolve(ForAll(ep, ep>0, Exists(n, n>0, ForAll(x, x>n, Abs(1/x)<ep))), Reals)", //
        "True");
  }

  @Test
  public void testClosedStatements() {
    check("Resolve(ForAll(x, x^2-4*x+4>=0), Reals)", //
        "True");
    check("Resolve(ForAll(x, x^2-4*x+4>0), Reals)", //
        "False");
    check("Resolve(Exists(x, x^2+3<0), Reals)", //
        "False");
    check("Resolve(Exists(x, x^2-3==0), Reals)", //
        "True");
    check("Resolve(ForAll({a, b}, a^2+b^2>=2*a*b), Reals)", //
        "True");
    check("Resolve(ForAll({a, b}, a^2+b^2>2*a*b), Reals)", //
        "False");
    check("Resolve(ForAll(x, Exists(y, x*y==2)), Reals)", //
        "False");
    check("Resolve(ForAll(x, x!=0, Exists(y, x*y==2)), Reals)", //
        "True");
    check("Resolve(Exists({x, y}, x^2+y^2<1 && x+y>2), Reals)", //
        "False");
    check("Resolve(Exists({x, y}, x^2+y^2<1 && x+y>1), Reals)", //
        "True");
    check("Resolve(ForAll(a, -1<a<1, a<1), Reals)", //
        "True");
    check("Resolve(ForAll(a, Exists(x, x^2+a*x+1==0)), Reals)", //
        "False");
    check("Resolve(ForAll(a, a^2>=4, Exists(x, x^2+a*x+1==0)), Reals)", //
        "True");
  }

  @Test
  public void testFreeVariables() {
    // the answer is a condition on the free variables, without the parts which the others imply
    check("Resolve(Exists(x, x>n && x^2<=m), Reals)", //
        "m>=0&&(n<0||n^2<m)");
    check("Resolve(Exists(y, x/y<=0 && y>1), Reals)", //
        "x<=0");
    check("Resolve(Exists(y, x*y>1 && y^2<m), Reals)", //
        "m*x^2>1");
    check("Resolve(ForAll(y, y>0, x*y+m>0), Reals)", //
        "m>=0&&x>=0&&(x!=0||m!=0)");
    check("Resolve(ForAll(x, x^2+b*x+c>0), Reals)", //
        "b^2<4*c");
    check("Resolve(Exists(y, y^2+x*y+1<0), Reals)", //
        "x<-2||x>2");
    check("Resolve(ForAll(y, y^2+a*y+b>=0), Reals)", //
        "a^2<=4*b");
    check("Resolve(Exists({y,z}, y+z==a && y*z==b), Reals)", //
        "4*b<=a^2");
    check("Resolve(Exists(y, y>a && y<b), Reals)", //
        "b>a");
    check("Resolve(ForAll(y, Implies(y>a, y>b)), Reals)", //
        "b<=a");
    check("Resolve(Exists(y, Abs(y-a)<1 && y>b), Reals)", //
        "b<1+a");
    check("Resolve(Exists(y, y^2<a && y>1 && x<y), Reals)", //
        "(a>1&&x<=1)||(x^2<a&&x>=1)");
    // answered before by the discriminant
    check("Resolve(Exists(x, x^2+b*x+c==0), Reals)", //
        "b^2-4*c>=0");
  }

  @Test
  public void testDeclined() {
    // degree 3 in the variable which is eliminated
    check("Resolve(Exists(x, x^3+a*x+1==0 && x>b), Reals)", //
        "Resolve(Exists({x},a*x+x^3==-1&&x>b),Reals)");
  }

  /**
   * One free variable: at every value of a grid the condition which is returned for
   * <code>Exists(y, phi)</code> has the verdict of the statement without a free variable.
   */
  @Test
  public void testFreeVariableAgainstClosedStatements() {
    Random random = new Random(6102026L);
    int compared = 0;
    for (int i = 0; i < 40; i++) {
      String phi = formula(random, "x", "y");
      String condition = evaluator.eval("Resolve(Exists(y, " + phi + "), Reals)").toString();
      if (condition.startsWith("Resolve(")) {
        continue;
      }
      for (String value : new String[] {"-3", "-3/2", "-1", "-1/2", "0", "1/3", "1", "2", "5/2"}) {
        String closed = evaluator
            .eval("Resolve(Exists(y, (" + phi + ") /. x->" + value + "), Reals)").toString();
        if (closed.equals("True") || closed.equals("False")) {
          String atValue = evaluator.eval("(" + condition + ") /. x->" + value).toString();
          assertEquals(closed, atValue, phi + " at x == " + value + ": " + condition);
          compared++;
        }
      }
    }
    assertTrue(compared > 150, "compared " + compared);
  }

  /** Statements whose elimination takes about a second. */
  @Tag(TestTags.SLOW)
  @Test
  public void testSlowStatements() {
    // x^2 is continuous at 3
    check("Resolve(ForAll(ep, ep>0, Exists(dl, dl>0, ForAll(x, Abs(x-3)<dl, Abs(x^2-9)<ep))), Reals)", //
        "True");
    check("Resolve(ForAll(ep, ep>0, Exists(dl, dl>0, ForAll(x, Abs(x-3)<dl, Abs(x^2-8)<ep))), Reals)", //
        "False");
    // degree 4 after the first elimination
    check("Resolve(ForAll(ep, ep>0, Exists(dl, dl>0, ForAll({x,y}, 0<x^2+y^2<dl^2, Abs(x+y)<ep))), Reals)", //
        "Resolve(ForAll(ep,ep>0,Exists(dl,dl>0,ForAll({x,y},0<x^2+y^2<dl^2,Abs(x+y)<ep))),Reals)");
  }

  private static final String[] RELATIONS = {"<", "<=", "==", "!=", ">", ">="};

  private static String quadratic(Random random, String x, String y) {
    StringBuilder buf = new StringBuilder();
    buf.append(random.nextInt(5) - 2).append("*").append(x).append("^2+");
    buf.append(random.nextInt(7) - 3).append("*").append(x);
    if (y != null) {
      buf.append("+").append(random.nextInt(5) - 2).append("*").append(x).append("*").append(y);
      buf.append("+").append(random.nextInt(5) - 2).append("*").append(y).append("^2");
      buf.append("+").append(random.nextInt(7) - 3).append("*").append(y);
    }
    buf.append("+").append(random.nextInt(9) - 4);
    return buf.toString();
  }

  private static String formula(Random random, String x, String y) {
    int atoms = 1 + random.nextInt(3);
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < atoms; i++) {
      if (i > 0) {
        buf.append(random.nextBoolean() ? " && " : " || ");
      }
      buf.append("(").append(quadratic(random, x, y))
          .append(RELATIONS[random.nextInt(RELATIONS.length)]).append("0)");
    }
    return buf.toString();
  }

  /**
   * One variable: <code>Exists(x, phi)</code> is true exactly if the solution set which
   * <code>Reduce</code> computes for <code>phi</code> is not empty.
   */
  @Test
  public void testAgainstReduce() {
    Random random = new Random(20261005L);
    int decided = 0;
    for (int i = 0; i < 200; i++) {
      String phi = formula(random, "x", null);
      String verdict = evaluator.eval("Resolve(Exists(x, " + phi + "), Reals)").toString();
      String set = evaluator.eval("Reduce(" + phi + ", x, Reals)").toString();
      if (set.startsWith("Reduce(")) {
        continue;
      }
      assertEquals(set.equals("False") ? "False" : "True", verdict, phi);
      decided++;
    }
    assertTrue(decided > 150, "decided " + decided);
  }

  /**
   * Two variables: a point of a grid which satisfies <code>phi</code> is a witness for
   * <code>Exists({x,y}, phi)</code> and refutes <code>ForAll({x,y}, !phi)</code>; and the negation
   * of an alternating statement, which is eliminated on another way, has the other verdict.
   */
  @Test
  public void testAgainstGrid() {
    Random random = new Random(5102026L);
    int decided = 0;
    for (int i = 0; i < 50; i++) {
      String phi = formula(random, "x", "y");
      boolean witness = evaluator.eval("AnyTrue(Flatten(Table(" + phi
          + ", {x,-3,3,1/2}, {y,-3,3,1/2})), TrueQ)").isTrue();
      String exists = evaluator.eval("Resolve(Exists({x,y}, " + phi + "), Reals)").toString();
      String forAll = evaluator.eval("Resolve(ForAll({x,y}, !(" + phi + ")), Reals)").toString();
      if (exists.equals("True") || exists.equals("False")) {
        decided++;
        if (witness) {
          assertEquals("True", exists, phi);
        }
        if (forAll.equals("True") || forAll.equals("False")) {
          assertEquals(exists.equals("True") ? "False" : "True", forAll, phi);
        }
      }
      String mixed = evaluator.eval("Resolve(ForAll(x, Exists(y, " + phi + ")), Reals)").toString();
      String dual =
          evaluator.eval("Resolve(Exists(x, ForAll(y, !(" + phi + "))), Reals)").toString();
      if ((mixed.equals("True") || mixed.equals("False"))
          && (dual.equals("True") || dual.equals("False"))) {
        assertEquals(mixed.equals("True") ? "False" : "True", dual, phi);
      }
    }
    assertTrue(decided > 25, "decided " + decided);
  }
}
