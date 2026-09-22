package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

public class ParallelMapTest extends ExprEvaluatorTestCase {

  @Test
  public void testSameResultAsMap() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("ParallelMap(f, {1,2,3})", //
          "{f(1),f(2),f(3)}");
      check("ParallelMap(#^2&, Range(10))", //
          "{1,4,9,16,25,36,49,64,81,100}");
      check("ParallelMap(f, g(a,b,c))", //
          "g(f(a),f(b),f(c))");
      check("ParallelMap(PrimeQ(2^# - 1)&, Range(40)) == Map(PrimeQ(2^# - 1)&, Range(40))", //
          "True");
      // the element goes into a held function as it is
      check("ParallelMap(Hold, {1+1, 2+2})", //
          "{Hold(2),Hold(4)}");
      check("ParallelMap(Hold, {x, y})", //
          "{Hold(x),Hold(y)}");
      check("ParallelMap(f, {x})", //
          "{f(x)}");
      check("ParallelMap(f, x)", //
          "x");
      check("ParallelMap(f, {1,2,3}, Method->\"FinestGrained\")", //
          "{f(1),f(2),f(3)}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testMappedOnOneThread() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      // the elements of Hold(...) are not evaluated
      check("ParallelMap(f, Hold(1+1, 2+2))", //
          "Hold(f(1+1),f(2+2))");
      check("ParallelMap(f, {{a,b},{c,d}}, {2})", //
          "{{f(a),f(b)},{f(c),f(d)}}");
      check("ParallelMap(f, <|a->1, b->2|>)", //
          "<|a->f(1),b->f(2)|>");
      check("ParallelMap(f, g(a,b), Heads->True)", //
          "f(g)[f(a),f(b)]");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testDefinitionsAndSideEffects() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("h(0)=1; h(n_) := n*h(n-1); ParallelMap(h, Range(0,8))", //
          "{1,1,2,6,24,120,720,5040,40320}");
      check("k=0; ParallelMap((k++; #)&, {1,2,3}); k", //
          "0");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testWithoutThreads() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    int kernels = Config.MAX_PARALLEL_KERNELS;
    try {
      Config.FILESYSTEM_ENABLED = false;
      check("ParallelMap(f, {1,2,3})", //
          "{f(1),f(2),f(3)}");
      Config.FILESYSTEM_ENABLED = true;
      Config.MAX_PARALLEL_KERNELS = 1;
      check("ParallelMap(#^2&, {1,2,3})", //
          "{1,4,9}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
      Config.MAX_PARALLEL_KERNELS = kernels;
    }
  }
}
