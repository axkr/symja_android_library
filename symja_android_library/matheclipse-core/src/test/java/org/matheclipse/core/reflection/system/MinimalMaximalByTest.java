package org.matheclipse.core.reflection.system;

import org.junit.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class MinimalMaximalByTest extends ExprEvaluatorTestCase {

  @Test
  public void testMinimalBy01() {
    check("MinimalBy(<|a->0,b:>1|>,-1+Sqrt(2),0.0,Graph({},{}))", //
        "<||>");
    check("MinimalBy(<|\"a\" -> {4, 1}, \"b\" -> {2, 3}, \"b\" -> {4, 2}, \"d\" -> {1, 1}|>, Last)", //
        "<|a->{4,1},d->{1,1}|>");


    check("MinimalBy({{a, 3}, {b, 2}, {a, 2}, {d, 1}, {b, 3}}, Last)", //
        "{{d,1}}");
    check("MinimalBy(Last)[{{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}]", //
        "{{a,1},{b,1},{d,1}}");
    check("MinimalBy({{d, 1}, {b, 2}, {b, 3}, {d, 2}, {a, 1}}, Last)", //
        "{{d,1},{a,1}}");

  }


  @Test
  public void testMaximalBy01() {
    check("MaximalBy(<|\"a\" -> {1, 1}, \"b\" -> {2, 3}, \"c\" -> {4, 2}, \"d\" -> {1, 3}|>, Last)", //
        "<|b->{2,3},d->{1,3}|>");
    check("MaximalBy({{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}, Last)", //
        "{{b,3}}");
    check("MaximalBy(Last)[{{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}]", //
        "{{b,3}}");
    check("MaximalBy({{a, 1}, {b, 1}, {b, 3}, {d, 2}, {a, 3}}, Last)", //
        "{{b,3},{a,3}}");
  }
}
