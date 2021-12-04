package org.matheclipse.io.system;

/** Tests forSolve and Roots functions */
public class PartTest extends AbstractTestCase {

  public PartTest(String name) {
    super(name);
  }

  public void testPartErrors() {
    // Part: Part specification x[[2]] is longer than depth of object.
    check(
        "x[[2]]", //
        "x[[2]]");
    // Part: The expression 3;;1 cannot be used as a part specification.
    check(
        "{1, 2, 3, 4}[[3;;1]]", //
        "{1,2,3,4}[[3;;1]]");
  }

  public void testPartRead() {
    check(
        "A = {a, b, c, d}; A[[3]]", //
        "c");

    check(
        "{a, b, c}[[-2]]", //
        "b");

    check(
        "A = {a, b, c, d}; A[[3]]", //
        "c");
    check(
        "(a + b + c)[[2]]", //
        "b");
    check(
        "(a + b + c)[[0]]", //
        "Plus");

    check(
        "M = {{a, b}, {c, d}}; M[[1, 2]]", //
        "b");

    check(
        "{1, 2, 3, 4}[[2;;4]]", //
        "{2,3,4}");

    check(
        "{1, 2, 3, 4}[[2;;-1]]", //
        "{2,3,4}");

    check(
        "{a, b, c, d}[[{1, 3, 3}]]", //
        "{a,c,c}");
    check(
        "B = {{a, b, c}, {d, e, f}, {g, h, i}}; B[[;;, 2]]", //
        "{b,e,h}");

    check(
        "B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; B[[{1, 3}, -2;;-1]]", //
        "{{2,3},{8,9}}");

    check(
        "{{a, b, c}, {d, e, f}, {g, h, i}}[[All, 3]]", //
        "{c,f,i}");

    check(
        "(a+b+c+d)[[-1;;-2]]", //
        "0");

    check(
        "{1,2,3,4,5}[[3;;1;;-1]]", //
        "{3,2,1}");

    check(
        "{1, 2, 3, 4, 5}[[;; ;; -1]]", //
        "{5,4,3,2,1}");

    check(
        "Range(11)[[-3 ;; 2 ;; -2]]", //
        "{9,7,5,3}");

    check(
        "Range(11)[[-3 ;; -7 ;; -3]]", //
        "{9,6}");

    check(
        "Range(11)[[7 ;; -7;; -2]]", //
        "{7,5}");

    check(
        "A[[1]] + B[[2]] + C[[3]] // Hold // FullForm", //
        "Hold(Plus(Part(A, 1), Part(B, 2), Part(C, 3)))");
  }

  public void testPartWrite() {
    check(
        "B={a,b,c}; {B[[2]],B[[3]],B[[1]]}=B;B", //
        "{c,a,b}");

    check(
        "B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; B[[;;, 2]] = {10, 11, 12}", //
        "{10,11,12}");

    check(
        "B", //
        "{{1,10,3},{4,11,6},{7,12,9}}");

    check(
        "B[[;;, 3]] = 13", //
        "13");

    check(
        "B", //
        "{{1,10,13},{4,11,13},{7,12,13}}");

    check(
        "B[[1;;-2]] = t", //
        "t");

    check(
        "B", //
        "{t,t,{7,12,13}}");

    check(
        "F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3}); F[[;; All, 2 ;; 3, 2]] = t; F", //
        "{{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}");

    check(
        "F[[;; All, 1 ;; 2, 3 ;; 3]] = k; F", //
        "{{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}");

    check(
        "a = {2,3,4}; i = 1; a[[i]] = 0; a", //
        "{0,3,4}");
  }

  public void testPartSparse() {
    check(
        "SparseArray[{{0, a}, {b, 0}}]//Normal", //
        "{{0,a},\n" //
            + " {b,0}}");
  }
}
