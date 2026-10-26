package org.matheclipse.ortools.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.optimization.IGraphFlowSolver;
import org.matheclipse.core.optimization.IIntegerConstraintSolver;
import org.matheclipse.core.optimization.ILinearOptimizer;
import org.matheclipse.core.optimization.IRoutingSolver;
import org.matheclipse.ortools.OrToolsInit;
import org.matheclipse.ortools.symja.OrToolsGraphFlows;
import org.matheclipse.ortools.symja.OrToolsIntegerConstraintSolver;
import org.matheclipse.ortools.symja.OrToolsLinearOptimizer;
import org.matheclipse.ortools.symja.OrToolsRouting;

/**
 * The Symja functions with the option <code>Method -&gt; "ORTools"</code>. Every test wraps the
 * installed solver in a counter, so that it also proves that OR-Tools was used and the result
 * didn't come from the default method.
 */
public class OrToolsSymjaTest extends AbstractTestCase {

  private static AtomicInteger countLinear() {
    AtomicInteger calls = new AtomicInteger();
    OrToolsLinearOptimizer delegate = new OrToolsLinearOptimizer();
    ILinearOptimizer.install(problem -> {
      calls.incrementAndGet();
      return delegate.solve(problem);
    });
    return calls;
  }

  private static AtomicInteger countInteger() {
    AtomicInteger calls = new AtomicInteger();
    OrToolsIntegerConstraintSolver delegate = new OrToolsIntegerConstraintSolver();
    IIntegerConstraintSolver.install((eqs, vars, userVars, max, domain, lo, hi, engine) -> {
      calls.incrementAndGet();
      return delegate.solve(eqs, vars, userVars, max, domain, lo, hi, engine);
    });
    return calls;
  }

  private static AtomicInteger countFlows() {
    AtomicInteger calls = new AtomicInteger();
    OrToolsGraphFlows delegate = new OrToolsGraphFlows();
    IGraphFlowSolver.install(new IGraphFlowSolver() {
      @Override
      public FlowResult maxFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
          int source, int sink) {
        calls.incrementAndGet();
        return delegate.maxFlow(numNodes, tails, heads, capacities, source, sink);
      }

      @Override
      public FlowResult minCostFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
          long[] unitCosts, long[] supplies) {
        calls.incrementAndGet();
        return delegate.minCostFlow(numNodes, tails, heads, capacities, unitCosts, supplies);
      }
    });
    return calls;
  }

  private static AtomicInteger countRouting() {
    AtomicInteger calls = new AtomicInteger();
    OrToolsRouting delegate = new OrToolsRouting();
    IRoutingSolver.install((distances, timeLimit) -> {
      calls.incrementAndGet();
      return delegate.shortestTour(distances, timeLimit);
    });
    return calls;
  }

  @Test
  public void testLinearProgramming() {
    AtomicInteger calls = countLinear();
    try {
      // the same point as without the option, where exact numbers give an exact result
      check("LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, "
          + "{{6,-1},{12,-1},{0,1},{1,0}}, Method->\"ORTools\")", //
          "{4.0,0.0,1.0}");
      check("LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, "
          + "{{6,-1},{12,-1},{0,1},{1,0}})", //
          "{4,0,1}");
      assertEquals(1, calls.get());
      // maximize x+y over the integer points of 2x+3y<=12, 4x+y<=10, x,y>=0: the optimum 4 is
      // attained at (2,2), (1,3) and (0,4)
      check("{-1,-1}.LinearProgramming({-1,-1},{{2,3},{4,1}},{{12,-1},{10,-1}},0,Integers,"
          + "Method->\"ORTools\")", //
          "-4");
      assertEquals(2, calls.get());
      // infeasible: x>=2 and x<=1
      check("LinearProgramming({1},{{1},{1}},{{2,1},{1,-1}},Method->\"ORTools\")", //
          "LinearProgramming({1},{{1},{1}},{{2,1},{1,-1}},Method->ORTools)");
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testLinearOptimization() {
    AtomicInteger calls = countLinear();
    try {
      check("LinearOptimization(x + y, {x + 2*y >= 3, x >= 0, y >= 0}, {x, y}, "
          + "Method->\"ORTools\")", //
          "{x->0.0,y->1.5}");
      check("LinearOptimization(-2*x+y,{x+2*y<=6,3*x+2*y<=12},{x,y},Method->\"ORTools\")", //
          "{x->4.0,y->0.0}");
      // There are no points which satisfy the constraints.
      check("LinearOptimization(x + y, {x + 2*y >= 3, x < 0, y < 0}, {x, y}, "
          + "Method->\"ORTools\")", //
          "{x->Indeterminate,y->Indeterminate}");
      assertEquals(3, calls.get());
      // the constraint x!=1 isn't linear: no answer which ignores it
      check("LinearOptimization(x + y, {x + 2*y >= 3, x != 1, x >= 0, y >= 0}, {x, y}, "
          + "Method->\"ORTools\")", //
          "LinearOptimization(x+y,{x+2*y>=3,x!=1,x>=0,y>=0},{x,y},Method->ORTools)");
      assertEquals(3, calls.get());
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testLinearOptimizationIntegerDomains() {
    AtomicInteger calls = countLinear();
    try {
      // minimize -5x-4y with 6x+4y<=26, x+2y<=6, x,y>=0: the same answers as the default method
      String f = "-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, ";
      String[][] cases = { //
          {"{x, y}", "{x->3.5,y->1.25}"}, //
          {"{Element(x,Integers), y}", "{x->4,y->0.5}"}, //
          {"{x, y}, {Integers, Reals}", "{x->4,y->0.5}"}, //
          {"{x, y}, Integers", "{x->4,y->0}"}, //
          {"Element({x, y}, Integers)", "{x->4,y->0}"}, //
          {"{Element(x,Integers), Element(y,Integers)}", "{x->4,y->0}"}};
      for (String[] c : cases) {
        check("LinearOptimization(" + f + c[0] + ", Method->\"ORTools\")", c[1]);
        check("LinearOptimization(" + f + c[0] + ")", c[1]);
      }
      assertEquals(cases.length, calls.get());
      check("LinearOptimization(x+y, {2*x==1}, {x, y}, Integers, Method->\"ORTools\")", //
          "{x->Indeterminate,y->Indeterminate}");
      assertEquals(cases.length + 1, calls.get());
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testMinimizeMaximizeIntegers() {
    AtomicInteger calls = countLinear();
    try {
      // the optimum 2 is attained at (1,1) and (0,2)
      check("First(Minimize({x+y, x+2*y>=3 && x>=0 && y>=0},{x,y},Integers,"
          + "Method->\"ORTools\"))", //
          "2");
      check("Maximize({5*x+4*y, 6*x+4*y<=24 && x+2*y<=6 && x>=0 && y>=0},{x,y},Integers,"
          + "Method->\"ORTools\")", //
          "{20,{x->4,y->0}}");
      // over the reals the vertex (3, 3/2)
      check("Maximize({5*x+4*y, 6*x+4*y<=24 && x+2*y<=6 && x>=0 && y>=0},{x,y},"
          + "Method->\"ORTools\")", //
          "{21.0,{x->3.0,y->1.5}}");
      // a strict inequality between integer valued sides: x+y<3 is x+y<=2
      check("Maximize({x+y, x+y<3 && x>=0 && y>=0},{x,y},Integers,Method->\"ORTools\")"
          + "//First", //
          "2");
      assertEquals(4, calls.get());
      // infeasible
      check("Minimize({x, x>=2 && x<=1},{x},Integers,Method->\"ORTools\")", //
          "{Infinity,{x->Indeterminate}}");
      // unbounded
      check("Maximize({x, x>=2},{x},Integers,Method->\"ORTools\")", //
          "{Infinity,{x->Indeterminate}}");
    } finally {
      OrToolsInit.init();
    }
  }

  /**
   * Glop's presolve may prove only "infeasible or unbounded"; the optimizer must still tell an
   * unbounded problem from an infeasible one.
   */
  @Test
  public void testUnboundedLinearProblems() {
    OrToolsLinearOptimizer optimizer = new OrToolsLinearOptimizer();
    double inf = Double.POSITIVE_INFINITY;
    // minimize x subject to x <= 2, x free
    ILinearOptimizer.Result r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {1.0},
        0.0, false, new double[][] {{1.0}}, new double[] {-inf}, new double[] {2.0},
        new double[] {-inf}, new double[] {inf}, null, 10.0));
    assertEquals(ILinearOptimizer.Status.UNBOUNDED, r.status);
    // minimize x - y subject to x + y >= 1, x - y <= 3, x, y free, singleton rows for presolve
    r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {1.0, -1.0}, 0.0, false,
        new double[][] {{1.0, 1.0}, {1.0, -1.0}, {1.0, 0.0}}, new double[] {1.0, -inf, -5.0},
        new double[] {inf, 3.0, inf}, new double[] {-inf, -inf}, new double[] {inf, inf}, null,
        10.0));
    assertEquals(ILinearOptimizer.Status.UNBOUNDED, r.status);
    // the same with integer variables
    r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {1.0, -1.0}, 0.0, false,
        new double[][] {{1.0, 1.0}, {1.0, -1.0}}, new double[] {1.0, -inf},
        new double[] {inf, 3.0}, new double[] {-inf, -inf}, new double[] {inf, inf},
        new boolean[] {true, true}, 10.0));
    assertEquals(ILinearOptimizer.Status.UNBOUNDED, r.status);
    // infeasible stays infeasible: x >= 2 and x <= 1
    r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {1.0}, 0.0, false,
        new double[][] {{1.0}, {1.0}}, new double[] {2.0, -inf}, new double[] {inf, 1.0},
        new double[] {-inf}, new double[] {inf}, null, 10.0));
    assertEquals(ILinearOptimizer.Status.INFEASIBLE, r.status);
    // integer infeasible with a bounded relaxation: 2x == 1, 0 <= x <= 3, minimize x
    r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {1.0}, 0.0, false,
        new double[][] {{2.0}}, new double[] {1.0}, new double[] {1.0}, new double[] {0.0},
        new double[] {3.0}, new boolean[] {true}, 10.0));
    assertEquals(ILinearOptimizer.Status.INFEASIBLE, r.status);
    // integer infeasible with an unbounded relaxation: 2x == 1, minimize -y, y >= 0
    r = optimizer.solve(new ILinearOptimizer.Problem(new double[] {0.0, -1.0}, 0.0, false,
        new double[][] {{2.0, 0.0}}, new double[] {1.0}, new double[] {1.0},
        new double[] {-inf, 0.0}, new double[] {inf, inf}, new boolean[] {true, true}, 10.0));
    assertEquals(ILinearOptimizer.Status.INFEASIBLE, r.status);

    check("Minimize({x, x<=2},{x},Method->\"ORTools\")", //
        "{-Infinity,{x->Indeterminate}}");
    check("Minimize({x-y, x+y>=1 && x-y<=3 && x>=-5},{x,y},Method->\"ORTools\")", //
        "{-Infinity,{x->Indeterminate,y->Indeterminate}}");
  }

  @Test
  public void testNMinimizeNMaximize() {
    AtomicInteger calls = countLinear();
    try {
      check("First(NMinimize({x+y, x+2*y>=3 && x>=0 && y>=0 && Element({x,y},Integers)},{x,y},"
          + "Method->\"ORTools\"))", //
          "2.0");
      check("NMaximize({5*x+4*y, 6*x+4*y<=24 && x+2*y<=6 && x>=0 && y>=0},{x,y},"
          + "Method->\"ORTools\")", //
          "{21.0,{x->3.0,y->1.5}}");
      assertEquals(2, calls.get());
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testSolveIntegers() {
    AtomicInteger calls = countInteger();
    try {
      // the default method and OR-Tools agree; the systems reach the constraint solver stage
      String[] systems = {"x^2+y^2==25 && x>0 && y>0", //
          "x*y==12 && x>0 && y>0 && x<y", //
          "Mod(x,3)==1 && 0<=x<=10", //
          "x^3+y^3==9 && -10<=x<=10 && -10<=y<=10", //
          "Abs(x-y)==2 && 0<=x<=3 && 0<=y<=3", //
          "(x==1 || x==3) && Not(y==x) && 0<=y<=3"};
      for (String system : systems) {
        String vars = system.contains("y") ? "{x,y}" : "{x}";
        String expected = evalString("Solve(" + system + "," + vars + ",Integers)");
        String actual = evalString("Solve(" + system + "," + vars + ",Integers,Method->\"ORTools\")");
        assertEquals(expected, actual, system);
      }
      assertTrue(calls.get() > 0, "the OR-Tools solver was never called");
      // any two of the instances (1,12), (2,6), (3,4)
      check("Map(Function(s, {x*y, x<y} /. s), "
          + "FindInstance(x*y==12 && x>0 && y>0 && x<y,{x,y},Integers,2,Method->\"ORTools\"))", //
          "{{12,True},{12,True}}");
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testKnapsackSolve() {
    // three times the item of cost 3 and value 50
    check("KnapsackSolve({{5, 10}, {4, 40}, {6, 30}, {3, 50}}, 10)", //
        "{0,0,0,3}");
    // the items 2 and 4 at most once: cost 7, value 90
    check("KnapsackSolve({{5, 10}, {4, 40, 1}, {6, 30}, {3, 50, 1}}, 10)", //
        "{0,1,0,1}");
    // two dimensions: weight and volume
    check("KnapsackSolve({{{2,3}, 3, 1}, {{3,1}, 4, 1}, {{4,2}, 5, 1}, {{5,5}, 6, 1}}, {5,4})", //
        "{1,1,0,0}");
    // rational costs
    check("KnapsackSolve({{1/2, 1}, {3/4, 2}}, 2)", //
        "{1,2}");
    check("KnapsackSolve({{0, 1}}, 2)", //
        "KnapsackSolve({{0,1}},2)");
  }

  @Test
  public void testFindMaximumFlow() {
    AtomicInteger calls = countFlows();
    try {
      String graph = "Graph({1->2,1->3,2->4,3->4,2->3},EdgeCapacity->{3,2,1,4,5})";
      for (String property : new String[] {"", ",\"EdgeList\"", ",\"FlowMatrix\""}) {
        String expected = evalString("FindMaximumFlow(" + graph + ",1,4" + property + ")");
        String actual =
            evalString("FindMaximumFlow(" + graph + ",1,4" + property + ",Method->\"ORTools\")");
        assertEquals(expected, actual, property);
      }
      check("FindMaximumFlow(" + graph + ",1,4,Method->\"ORTools\")", //
          "5");
      // undirected edges and lists of sources
      String undirected = "Graph({1<->2,2<->3,3<->4,1<->4},EdgeCapacity->{2,3,4,1})";
      assertEquals(evalString("FindMaximumFlow(" + undirected + ",{1},{3})"),
          evalString("FindMaximumFlow(" + undirected + ",{1},{3},Method->\"ORTools\")"));
      assertTrue(calls.get() >= 4);
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testFindMinimumCostFlow() {
    AtomicInteger calls = countFlows();
    try {
      // a maximum flow of 2: the paths 1->2->4 (cost 2) and 1->3->4 (cost 6)
      // the capacity of an edge is 1
      String graph = "Graph({1->2,1->3,2->4,3->4},EdgeWeight->{1,5,1,1})";
      check("FindMinimumCostFlow(" + graph + ",1,4,Method->\"ORTools\")", //
          "8");
      check("FindMinimumCostFlow(" + graph + ",1,4,1,Method->\"ORTools\")", //
          "2");
      // the default method gives the same costs
      check("FindMinimumCostFlow(" + graph + ",1,4)", //
          "8");
      check("FindMinimumCostFlow(" + graph + ",1,4,1)", //
          "2");
      // a capacity option of the call: two units on each path, 2*2 + 2*6
      check("FindMinimumCostFlow(" + graph + ",1,4,EdgeCapacity->{2,2,2,2},Method->\"ORTools\")", //
          "16");
      check("FindMinimumCostFlow(" + graph + ",1,4,EdgeCapacity->{2,2,2,2})", //
          "16");
      // a flow of value 3 doesn't exist
      check("Head(FindMinimumCostFlow(" + graph + ",1,4,3,Method->\"ORTools\"))", //
          "FindMinimumCostFlow");
      // undirected edges: 1-2-3 costs 2, 1-3 costs 5
      String undirected = "Graph({1<->2,2<->3,1<->3},EdgeWeight->{1,1,5})";
      check("FindMinimumCostFlow(" + undirected + ",1,3,Method->\"ORTools\")", //
          "7");
      check("FindMinimumCostFlow(" + undirected + ",1,3)", //
          "7");
      // an undirected edge with a negative cost would carry flow both ways
      check("Head(FindMinimumCostFlow(Graph({1<->2,2<->3},EdgeWeight->{-5,1}),1,3,"
          + "Method->\"ORTools\"))", //
          "FindMinimumCostFlow");
      assertTrue(calls.get() >= 7);
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testFindShortestTour() {
    AtomicInteger calls = countRouting();
    try {
      String points = "{{1,2},{2,3},{3,1},{5,5},{0,4},{4,0},{2,2}}";
      String expected = evalString("N(First(FindShortestTour(" + points + ")))");
      String actual =
          evalString("N(First(FindShortestTour(" + points + ",Method->\"ORTools\")))");
      assertEquals(expected, actual);
      check("Sort(Last(FindShortestTour(" + points + ",Method->\"ORTools\")))", //
          "{1,1,2,3,4,5,6,7}");
      assertEquals(2, calls.get());
    } finally {
      OrToolsInit.init();
    }
  }

  @Test
  public void testWithoutModule() {
    ILinearOptimizer.install(null);
    try {
      // the default method is used after a message
      check("LinearOptimization(x + y, {x + 2*y >= 3, x >= 0, y >= 0}, {x, y}, "
          + "Method->\"ORTools\")", //
          "{x->0.0,y->1.5}");
    } finally {
      OrToolsInit.init();
    }
  }
}
