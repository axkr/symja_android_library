package org.matheclipse.core.system;

import org.junit.Test;

public class PartTest extends ExprEvaluatorTestCase {

  @Test
  public void testIndexed() {
    check("Indexed({7}+Sqrt(2),1)", //
        "7+Sqrt(2)");
    // message Part: Part 2 of {7+Sqrt(2)} does not exist.
    check("Indexed({7}+Sqrt(2),2)", //
        "Indexed({7+Sqrt(2)},{2})");

    check("Indexed({a, b}, 1)", //
        "a");
    check("Indexed(c,1)", //
        "Indexed(c,{1})");
    check("Indexed(c, {1,2})", //
        "Indexed(c,{1,2})");
  }

  @Test
  public void testPartErrors() {

    //
    check("<|a->0,b:>1|>[[{-2+I*2,1,1,1}]]", //
        "<|a->0,b:>1|>[[{-2+I*2,1,1,1}]]");


    // Part: Part specification x[[2]] is longer than depth of object.
    check("x[[2]]", //
        "x[[2]]");
    // Part: The expression 3;;1 cannot be used as a part specification.
    check("{1, 2, 3, 4}[[3;;1]]", //
        "{1,2,3,4}[[3;;1]]");
  }

  @Test
  public void testPartRead() {
    check("A = {a, b, c, d}; A[[3]]", //
        "c");

    check("{a, b, c}[[-2]]", //
        "b");

    check("A = {a, b, c, d}; A[[3]]", //
        "c");
    check("(a + b + c)[[2]]", //
        "b");
    check("(a + b + c)[[0]]", //
        "Plus");

    check("M = {{a, b}, {c, d}}; M[[1, 2]]", //
        "b");

    check("{1, 2, 3, 4}[[2;;4]]", //
        "{2,3,4}");

    check("{1, 2, 3, 4}[[2;;-1]]", //
        "{2,3,4}");

    check("{a, b, c, d}[[{1, 3, 3}]]", //
        "{a,c,c}");
    check("B = {{a, b, c}, {d, e, f}, {g, h, i}}; B[[;;, 2]]", //
        "{b,e,h}");

    check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; B[[{1, 3}, -2;;-1]]", //
        "{{2,3},{8,9}}");

    check("{{a, b, c}, {d, e, f}, {g, h, i}}[[All, 3]]", //
        "{c,f,i}");

    check("(a+b+c+d)[[-1;;-2]]", //
        "0");

    check("{1,2,3,4,5}[[3;;1;;-1]]", //
        "{3,2,1}");

    check("{1, 2, 3, 4, 5}[[;; ;; -1]]", //
        "{5,4,3,2,1}");

    check("Range(11)[[-3 ;; 2 ;; -2]]", //
        "{9,7,5,3}");

    check("Range(11)[[-3 ;; -7 ;; -3]]", //
        "{9,6}");

    check("Range(11)[[7 ;; -7;; -2]]", //
        "{7,5}");

    check("A[[1]] + B[[2]] + C[[3]] // Hold // FullForm", //
        "Hold(Plus(Part(A, 1), Part(B, 2), Part(C, 3)))");
  }

  @Test
  public void testPartWrite() {
    check("B={a,b,c}; {B[[2]],B[[3]],B[[1]]}=B;B", //
        "{c,a,b}");

    check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; B[[;;, 2]] = {10, 11, 12}", //
        "{10,11,12}");

    check("B", //
        "{{1,10,3},{4,11,6},{7,12,9}}");

    check("B[[;;, 3]] = 13", //
        "13");

    check("B", //
        "{{1,10,13},{4,11,13},{7,12,13}}");

    check("B[[1;;-2]] = t", //
        "t");

    check("B", //
        "{t,t,{7,12,13}}");

    check("F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3}); F[[;; All, 2 ;; 3, 2]] = t; F", //
        "{{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}");

    check("F[[;; All, 1 ;; 2, 3 ;; 3]] = k; F", //
        "{{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}");

    check("a = {2,3,4}; i = 1; a[[i]] = 0; a", //
        "{0,3,4}");
  }

  @Test
  public void testPartSparse() {
    check("sp=SparseArray[{{0, a}, {b, 0}}]", //
        "SparseArray(Number of elements: 2 Dimensions: {2,2} Default value: 0)");
    check("sp//Normal", //
        "{{0,a},\n" //
            + " {b,0}}");
    check("sps=sp[[2]]", //
        "SparseArray(Number of elements: 1 Dimensions: {2} Default value: 0)");
    check("sps//Normal", //
        "{b,0}");
    check("sps=sp[[2,1]]", //
        "b");
  }

  @Test
  public void testPart() {
    check("(3/2)[[0]]", //
        "Rational");
    check("(atesta)[[0]]", //
        "Symbol");
    check("x[[]]", //
        "x");
    check("Part(x)", //
        "x");
    check("{1, 2, 3}[[2]] = 0.5", //
        "0.5");
    check("<|2 -> b, 1 -> a|>[[Key[1]]]", //
        "a");
    check("<|2 -> b, 1 -> a|>[[1]]", //
        "b");
    check("<|\"a\" -> 5, \"b\" -> 6|>[[\"a\"]]", //
        "5");
    check("{}[[-3;;-7;;-3]]", //
        "{}[[-3;;-7;;-3]]");
    check("lhs=10;lhs[[1;;All,2;;3,2]]=-I;lhs", //
        "10");
    check("{a, b, c, d, e, f, g, h, i, j, k}[[3 ;; -3 ;; 2]]", //
        "{c,e,g,i}");
    check("{a, b, c, d, e, f, g, h, i, j, k}[[;; ;; 2]]", //
        "{a,c,e,g,i,k}");
    check("test[[i;;]] // FullForm", //
        "Part(test, Span(i, All))");
    check("test={g,h,k}", //
        "{g,h,k}");
    check("test[[Position({x,v,w },{x,_,_})[[1,1]],2]] = ({a,b})[[2]]", //
        "b");
    check("f(a, b, c)[[{2, 3}]]", //
        "f(b,c)");
    check("f(g(a, b), h(c, d))[[{1, 2}, {2}]]", //
        "f(g(b),h(d))");
    check("{d, e, a, b, c}[[{3,4,5,1,2}]]", //
        "{a,b,c,d,e}");

    check("m = {a, b, c, d};", //
        "");
    check("m[[2]] += x", //
        "b+x");
    check("m", //
        "{a,b+x,c,d}");

    check("m[[2]] *= x", //
        "x*(b+x)");
    check("m", //
        "{a,x*(b+x),c,d}");

    check("m[[2]] -= y", //
        "x*(b+x)-y");
    check("m", //
        "{a,x*(b+x)-y,c,d}");

    check("m[[2]] /= z^2", //
        "(x*(b+x)-y)/z^2");
    check("m", //
        "{a,(x*(b+x)-y)/z^2,c,d}");

    // Part: Part 1000000000000 of a(x,y,z,f) does not exist.
    check("{a(x,y,z,f),b,c,d}[[1,1000000000000]]", //
        "{a(x,y,z,f),b,c,d}[[1,1000000000000]]");
    // Part: Part -30 of a(x,y,z,f) does not exist.
    check("{a(x,y,z,f),b,c,d}[[1,-30]]", //
        "{a(x,y,z,f),b,c,d}[[1,-30]]");
    // Part: The expression None cannot be used as a part specification.
    check("{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[None]]", //
        "{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[None]]");
    // Part: Part specification asdf[[{1,2}]] is longer than depth of object.
    check("Part[asdf,{1,2}]", //
        "asdf[[{1,2}]]");

    check("v = {a, b, c, d, e, f}", //
        "{a,b,c,d,e,f}");
    check("v[[2 ;; 4]] = x", //
        "x");
    check("v", //
        "{a,x,x,x,e,f}");
    check("f(g(a, b), g(c, d))[[2, 1]]", //
        "c");
    check("(1 + 2 * x^2 + y^2)[[3]]", //
        "y^2");
    check("{x -> 4, y -> 5}[[1, 2]]", //
        "4");
    check("{{a, b, c}, {d, e, f}}[[1]][[2]]", //
        "b");
    check("{{a, b, c}, {d, e, f}, {g, h, i}}[[{1, 3}, {2, 3}]]", //
        "{{b,c},{h,i}}");

    check("{{a, b, c}, {d, e, f}, {g, h, i}}[[All, 2]]", //
        "{b,e,h}");
    check("{a, b, c, d, e, f}[[-2]]", //
        "e");
    check("{a, b, c, d, e, f}[[{1, 3, 1, 2, -1, -1}]]", //
        "{a,c,a,b,f,f}");

    check("1/(b-a*c)[[2]]", //
        "-1/(a*c)");
    check("{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}[[2]]", //
        "{0,1,0}");
    check("T = {a, b, c, d}", //
        "{a,b,c,d}");
    check("T[[2]]=3", //
        "3");

    check("{a(x,y,z,f),b,c,d}[[1,2]]", //
        "y");
    check("{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[All,2]]", //
        "{1,1,1,1,1}");

    check("lst=False;lst[[2]]", //
        "False[[2]]");
    check("T = {a, b, c, d}", //
        "{a,b,c,d}");
    check("T[[2]]=3", //
        "3");
    check("T", //
        "{a,3,c,d}");

    check("A = {a, b, c, d}", //
        "{a,b,c,d}");
    check("A[[3]]", //
        "c");
    check("{a, b, c}[[-2]]", //
        "b");
    check("(a + b + c)[[2]]", //
        "b");
    check("(a + b + c)[[0]]", //
        "Plus");
    check("M = {{a, b}, {c, d}}", //
        "{{a,b},{c,d}}");
    check("M[[1, 2]]", //
        "b");
    check("M[[1, 2]] = x", //
        "x");
    check("M[[2, 2]] = y", //
        "y");
    check("M", //
        "{{a,x},{c,y}}");
    check("M[[1, 1+1]] = y", //
        "y");
    check("M", //
        "{{a,y},{c,y}}");
    check("{1, 2, 3, 4}[[2;;4]]", //
        "{2,3,4}");
    check("{1, 2, 3, 4}[[2;;-1]]", //
        "{2,3,4}");
    check("{a, b, c, d}[[{1, 3, 3}]]", //
        "{a,c,c}");
    check("B = {{a, b, c}, {d, e, f}, {g, h, i}}", //
        "{{a,b,c},{d,e,f},{g,h,i}}");
    check("B[[;; 2]]", //
        "{{a,b,c},{d,e,f}}");
    check("B[[;;, 2]]", //
        "{b,e,h}");
    check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}", //
        "{{1,2,3},{4,5,6},{7,8,9}}");
    check("B[[{1, 3}, -2;;-1]]", //
        "{{2,3},{8,9}}");
    check("(a+b+c+d)[[-1;;-2]]", //
        "0");
    check("x[[2]]", //
        "x[[2]]");
    // Assignment
    check("B[[;;, 2]] = {10, 11, 12}", //
        "{10,11,12}");
    check("B", //
        "{{1,10,3},{4,11,6},{7,12,9}}");
    check("B[[;;, 3]] = 13", //
        "13");
    check("B", //
        "{{1,10,13},{4,11,13},{7,12,13}}");

    check("B[[1;;-2]] = t", //
        "t");
    check("B", //
        "{t,t,{7,12,13}}");

    check("F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3})",
        "{{{1,2,3},{2,4,6},{3,6,9}},{{2,4,6},{4,8,12},{6,12,18}},{{3,6,9},{6,12,18},{9,18,\n"
            + "27}}}");

    check("F[[;; All, 2 ;; 3, 2]] = t", //
        "t");
    check("F", //
        "{{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}");
    check("F[[;; All, 1 ;; 2, 3 ;; 3]] = k", //
        "k");
    check("F", //
        "{{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}");

    check("A[[1]] + B[[2]] + C[[3]] // Hold // FullForm", //
        "Hold(Plus(Part(A, 1), Part(B, 2), Part(C, 3)))");
    check("a = {2,3,4}; i = 1; a[[i]] = 0; a", //
        "{0,3,4}");

    check("{1,2,3,4,5}[[3;;1;;-1]]", //
        "{3,2,1}");
    check("{1, 2, 3, 4, 5}[[;; ;; -1]]", //
        "{5,4,3,2,1}");

    check("Range(11)[[-3 ;; 2 ;; -2]]", //
        "{9,7,5,3}");
    check("Range(11)[[-3 ;; -7 ;; -3]]", //
        "{9,6}");
    check("Range(11)[[7 ;; -7;; -2]]", //
        "{7,5}");

    check("{1, 2, 3, 4}[[1;;3;;-1]]", //
        "{1,2,3,4}[[1;;3;;-1]]");
    check("{1, 2, 3, 4}[[3;;1]]", //
        "{1,2,3,4}[[3;;1]]");
    check("{1, 2, 3, 4}[[3;;2]]", //
        "{}");

    check("(1 + 2*x^2 + y^2)[[2]]", //
        "2*x^2");
    check("(1 + 2*x^2 + y^2)[[1]]", //
        "1");
    check("(x/y)[[2]]", //
        "1/y");
    check("(y/x)[[2]]", //
        "y");

    check("{{a, b, c}, {d, e, f}}[[1]][[2]]", //
        "b");
    check("{{a, b, c}, {d, e, f}}[[1, 2]]", //
        "b");
  }
}
