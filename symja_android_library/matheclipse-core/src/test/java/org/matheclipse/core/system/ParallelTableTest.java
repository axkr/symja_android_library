package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

public class ParallelTableTest extends ExprEvaluatorTestCase {

  @Test
  public void testIteratorForms() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("ParallelTable(x, {5})", //
          "{x,x,x,x,x}");
      check("ParallelTable(x, 5)", //
          "{x,x,x,x,x}");
      check("ParallelTable(i^2, {i, 10})", //
          "{1,4,9,16,25,36,49,64,81,100}");
      check("ParallelTable(i, {i, 3, 8})", //
          "{3,4,5,6,7,8}");
      check("ParallelTable(i, {i, 1, 2, 1/4})", //
          "{1,5/4,3/2,7/4,2}");
      check("ParallelTable(i^2, {i, {a, b, c}})", //
          "{a^2,b^2,c^2}");
      check("ParallelTable({i, j}, {i, 4}, {j, i})", //
          "{{{1,1}},{{2,1},{2,2}},{{3,1},{3,2},{3,3}},{{4,1},{4,2},{4,3},{4,4}}}");
      check("ParallelTable(Subscript(a, 1)^2, {Subscript(a, 1), 3})", //
          "{1,4,9}");
      check("ParallelTable(i, {i, 0})", //
          "{}");
      check("ParallelTable(i, {i, 1})", //
          "{1}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testSameResultAsTable() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {

      // the bounds of an inner iterator depend on the outer variable
      check("nl={2,1,4}; ParallelTable(x, {dn1, 1, Length(nl)}, {x, 0, nl[[dn1]] - 1})", //
          "{{0,1},{0},{0,1,2,3}}");
      // a global value of the iterator variable is not used
      check("Block({i=7}, ParallelTable(x, {i, 2}, {x, 0, i}))", //
          "{{0,1},{0,1,2}}");
      check("ParallelTable(PrimeQ(2^i - 1), {i, 40}) == Table(PrimeQ(2^i - 1), {i, 40})", //
          "True");
      // a result is put together from symbols, patterns, functions and associations of the user
      check("ParallelTable(Function({x}, x^i), {i, 3})", //
          "{Function({x},x^1),Function({x},x^2),Function({x},x^3)}");
      check("ParallelTable(u_ :> u^i, {i, 3})", //
          "{u_:>u^1,u_:>u^2,u_:>u^3}");
      check("ParallelTable(<|a -> i|>, {i, 3})", //
          "{<|a->1|>,<|a->2|>,<|a->3|>}");
      check("ParallelTable(ParallelTable(i*j, {j, 3}), {i, 3})", //
          "{{1,2,3},{2,4,6},{3,6,9}}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testDefinitionsAreDistributed() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("h(0)=1; h(n_) := n*h(n-1); ParallelTable(h(i), {i, 0, 8})", //
          "{1,1,2,6,24,120,720,5040,40320}");
      check("data = Range(10)^2; ParallelTable(data[[i]], {i, 10})", //
          "{1,4,9,16,25,36,49,64,81,100}");
      // the attributes are distributed as well
      check(
          "SetAttributes(hf, HoldAll); hf(x_) := Hold(x); "
              + "ParallelTable(hf(1+i), {i, 2}) === Table(hf(1+i), {i, 2})", //
          "True");
      check("SetAttributes(hf, HoldAll); hf(x_) := Hold(x); ParallelTable(hf(1+i), {i, 2})", //
          "{Hold(1+1),Hold(1+2)}");
      check(
          "Options(fo) = {\"p\" -> 2}; fo(x_, OptionsPattern()) := x^OptionValue(\"p\"); "
              + "ParallelTable(fo(i), {i, 4})", //
          "{1,4,9,16}");
      // without the definition the kernels leave f(i) alone; it is evaluated when it comes back
      check("f(n_) := n^2; ParallelTable(f(i), {i, 4}, DistributedContexts -> None)", //
          "{1,4,9,16}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  /**
   * The point of the kernels having symbols of their own: <code>k</code> and <code>s</code> are
   * assigned by every call, and every call runs on several threads at the same time.
   */
  @Test
  public void testKernelsDoNotShareVariables() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check(
          "f(n_) := Sum(k, {k, n}); g(n_) := Block({s = 0}, Do(s = s + j, {j, n}); s); "
              + "And @@ Table(ParallelTable({f(i), g(i)}, {i, 60}, Method -> \"FinestGrained\") "
              + "== Table({i*(i+1)/2, i*(i+1)/2}, {i, 60}), {20})", //
          "True");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testSideEffectsStayOnTheKernels() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {

      check("c = 0; ParallelTable(c++, {i, 8}); c", //
          "0");
      check("Catch(ParallelTable(If(i == 5, Throw(found(i)), i), {i, 10}))", //
          "found(5)");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testMethod() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      String expected = "{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}";
      check("ParallelTable(i, {i, 20}, Method -> \"FinestGrained\")", expected);
      check("ParallelTable(i, {i, 20}, Method -> \"CoarsestGrained\")", expected);
      check("ParallelTable(i, {i, 20}, Method -> (\"ItemsPerEvaluation\" -> 3))", expected);
      check("ParallelTable(i, {i, 20}, Method -> (\"EvaluationsPerKernel\" -> 2))", expected);
      check("ParallelTable(i, {i, 20}, Method -> Automatic, ProgressReporting -> False)", expected);
      // message parmthd
      check("ParallelTable(i, {i, 20}, Method -> \"Foo\")", //
          "ParallelTable(i,{i,20},Method->Foo)");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testUnevaluated() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {

      check("ParallelTable(i, {i, 1, n})", //
          "ParallelTable(i,{i,1,n})");
      check("ParallelTable(i, {i, 3}, {j, 1, n})", //
          "ParallelTable(i,{i,3},{j,1,n})");
      // message iterb
      check("ParallelTable(i, {i, 1, 5, 0})", //
          "ParallelTable(i,{i,1,5,0})");
      // message itform
      check("ParallelTable(i, {i, 3}, {1,2,3,4,5,6})", //
          "ParallelTable(i,{i,3},{1,2,3,4,5,6})");
      check("ParallelTable(x)", //
          "ParallelTable(x)");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testTimeConstrained() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {

      check("TimeConstrained(ParallelTable(Pause(1); i, {i, 200}), 2)", //
          "$Aborted");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testWithoutThreads() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    int kernels = Config.MAX_PARALLEL_KERNELS;
    try {
      Config.MAX_PARALLEL_KERNELS = 1;
      check("ParallelTable({i, j}, {i, 3}, {j, i})", //
          "{{{1,1}},{{2,1},{2,2}},{{3,1},{3,2},{3,3}}}");
      check("ParallelTable(i, {i, 1, n})", //
          "ParallelTable(i,{i,1,n})");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
      Config.MAX_PARALLEL_KERNELS = kernels;
    }
  }
}
