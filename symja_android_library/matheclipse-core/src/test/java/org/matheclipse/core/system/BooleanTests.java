package org.matheclipse.core.system;

public class BooleanTests extends ExprEvaluatorTestCase {
  public BooleanTests(String name) {
    super(name);
  }

  public void testAllTrue() {
    check("AllTrue({}, EvenQ)", //
        "True");
    check("AllTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
        "False");
    check("AllTrue({2, 4, 6, 8}, EvenQ)", //
        "True");
    check("AllTrue({2, 6, x, 4, y}, # < 10 &)", //
        "x<10&&y<10");
    check("AllTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
        "False");
    check("AllTrue(f(1, 7, 3), OddQ)", //
        "True");
  }

  public void testAnd() {
    // let x&&x unevaluated because of pattern matching
    check("x&&x", //
        "x&&x");
    check("Not(And(And(True, x), Or(x, False)))", //
        "!(x&&x)");
    check("True && True && False", //
        "False");
    check("a && b && True && c", //
        "a&&b&&c");
    check("And()", //
        "True");
    check("And(4)", //
        "4");
    check("2 > 1 && Pi > 3", //
        "True");
    check("a && b && ! c", //
        "a&&b&&!c");
    check("x + 2*y == 3 && 4*x + 5*y == 6", //
        "x+2*y==3&&4*x+5*y==6");
    check("FullForm( And(x, And(y, z)) )", //
        "And(x, y, z)");
    check("And(x, True, z)", //
        "x&&z");
    check("And(x, False, z)", //
        "False");
  }

  public void testAnyTrue() {
    check("AnyTrue({}, EvenQ)", //
        "False");
    check("AnyTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
        "True");
    check("AnyTrue(EvenQ)[{1, 2, 3, 4, 5, 6}]", //
        "True");
    check("AnyTrue({1, 3, 5}, EvenQ)", //
        "False");
    check("AnyTrue({12, 16, x, 14, y}, # < 10 &)", //
        "x<10||y<10");
    check("AnyTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
        "False");
    check("AnyTrue(f(2, 7, 6), OddQ)", //
        "True");
  }

  public void testBoole() {
    check("Boole(2 == 2)", //
        "1");
    check("Boole(7 < 5)  ", //
        "0");
    check("Boole(a == 7)", //
        "Boole(a==7)");

    check("{Boole(False), Boole(True)}", //
        "{0,1}");
    check("Boole({True, False, True, True, False})", //
        "{1,0,1,1,0}");
    check("Boole({a, False, b, True, f()})", //
        "{Boole(a),0,Boole(b),1,Boole(f())}");
  }

  public void testBooleanConvert() {
    check("BooleanConvert((a && !b || !a && b) && " //
        + "(c && b && !a && !d && !e || c && !b && a && !d && !e || c && !b && !a && d && !e || c && !b && !a && !d && e || !c && b && a && !d && !e || !c && b && !a && d && !e || !c && b && !a && !d && e || !c && !b && a && d && !e || !c && !b && a && !d && e || !c && !b && !a && d && e) && "
        + "(a && !f || !a && f) && (e && !c || !e && c), \"CNF\")", //
        "(a||b)&&(a||!b||c||d||e)&&(a||!b||c||d||!e||f)&&(a||!b||c||!d)&&(a||!b||!c||d||e||f)&&(a||!b||!c||d||!e)&&(a||!b||!c||!d)&&(!a||b||c||d||e)&&(!a||b||c||d||!e||!f)&&(!a||b||c||!d)&&(!a||b||!c||d||e||!f)&&(!a||b||!c||d||!e)&&(!a||b||!c||!d)&&(!a||!b)");

    check("BooleanConvert((a||b)&&(c||d), \"CNF\")", //
        "(a||b)&&(c||d)");

    check("BooleanConvert(a&&!b||!a&&c||b&&!c, \"DNF\")", //
        "(a&&!b)||(!a&&c)||(b&&!c)");

    check("BooleanConvert(Implies(x, y), \"CNF\")", //
        "!x||y");
    check("BooleanConvert(! (a && b), \"CNF\")", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c), \"CNF\")", //
        "!a&&!b&&!c");
    check("BooleanConvert(Xor(x,y), \"CNF\")", //
        "(x||y)&&(!x||!y)");
    check("BooleanConvert(Xnor(x,y))", //
        "(x&&y)||(!x&&!y)");
    check("BooleanConvert(Xnor(x,y), \"CNF\")", //
        "(x||!y)&&(!x||y)");
    check("BooleanConvert(Xor(p,q,r),\"CNF\")", //
        "(p||q||r)&&(p||!q||!r)&&(!p||q||!r)&&(!p||!q||r)");
    check("BooleanConvert(Nand(p, q, r), \"CNF\")", //
        "!p||!q||!r");
    check("BooleanConvert(!Nand(p, q, r), \"CNF\")", //
        "p&&q&&r");
    check("BooleanConvert(Nor(p, q, r), \"CNF\")", //
        "!p&&!q&&!r");
    check("BooleanConvert(!Nor(p, q, r), \"CNF\")", //
        "p||q||r");
    check("BooleanConvert(! (a && b), \"CNF\")", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c), \"CNF\")", //
        "!a&&!b&&!c");
    check("BooleanConvert(Equivalent(x, y, z), \"CNF\")", //
        "(x||y||!z)&&(x||!y)&&(!x||y)&&(!x||!y||z)");

    check("BooleanConvert(Implies(x, y))", //
        "!x||y");
    check("BooleanConvert(! (a && b))", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c))", //
        "!a&&!b&&!c");
    check("BooleanConvert(Xor(x,y))", //
        "(x&&!y)||(!x&&y)");
    check("BooleanConvert(Xor(p,q,r))", //
        "(p&&q&&r)||(p&&!q&&!r)||(!p&&q&&!r)||(!p&&!q&&r)");
    check("BooleanConvert(Nand(p, q, r))", //
        "!p||!q||!r");
    check("BooleanConvert(!Nand(p, q, r))", //
        "p&&q&&r");
    check("BooleanConvert(Nor(p, q, r))", //
        "!p&&!q&&!r");
    check("BooleanConvert(!Nor(p, q, r))", //
        "p||q||r");
    check("BooleanConvert(! (a && b))", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c))", //
        "!a&&!b&&!c");
    check("BooleanConvert(Equivalent(x, y, z))", //
        "(x&&y&&z)||(!x&&!y&&!z)");

    check("BooleanConvert(Implies(x, y), \"DNF\")", //
        "!x||y");
    check("BooleanConvert(! (a && b), \"DNF\")", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c), \"DNF\")", //
        "!a&&!b&&!c");
    check("BooleanConvert(Xor(x,y), \"DNF\")", //
        "(x&&!y)||(!x&&y)");
    check("BooleanConvert(Nand(p, q, r), \"DNF\")", //
        "!p||!q||!r");
    check("BooleanConvert(!Nand(p, q, r), \"DNF\")", //
        "p&&q&&r");
    check("BooleanConvert(Nor(p, q, r), \"DNF\")", //
        "!p&&!q&&!r");
    check("BooleanConvert(!Nor(p, q, r), \"DNF\")", //
        "p||q||r");
    check("BooleanConvert(! (a && b), \"DNF\")", //
        "!a||!b");
    check("BooleanConvert(! (a || b || c), \"DNF\")", //
        "!a&&!b&&!c");
    check("BooleanConvert(Equivalent(x, y, z), \"DNF\")", //
        "(x&&y&&z)||(!x&&!y&&!z)");
  }

  public void testBooleanFunction001() {
    check("f=BooleanFunction(42,3);", //
        "");
    // message BooleanFunction: BooleanFunction(Index: 17 Number of variables: 3) called with 2
    // arguments; 3 arguments are expected.
    check("f(True,False)", //
        "BooleanFunction(Index: 6 Number of variables: 3)");
    check("BooleanConvert(f(True,False,x), \"DNF\")", //
        "x");
    check("f(True,False,False)", //
        "False");
    check("BooleanConvert(f, \"DNF\")", //
        "(!#1&&#3)||(!#2&&#3)&");
    check("BooleanConvert(BooleanFunction({{True, True} -> True}, {x,y}))", //
        "x&&y");
    check(
        "BooleanConvert(BooleanFunction({{False, True} -> True,{True, False} -> True,{True, True} ->True}, {x,y}))", // "
        "x||y");

    check("f=BooleanConvert(Xor(x,y,z), \"BFF\")", //
        "BooleanFunction(Index: 24 Number of variables: 3)");
    check("g=BooleanTable(f, {x, y});", //
        "");
    check("BooleanConvert(g)", //
        "{z,!z,!z,z}");

    check("BooleanTable(f, {x, y, z})", //
        "{True,False,False,True,False,True,True,False}");
    check("BooleanTable({f, Xor[x, y, z]}, {x, y, z})", //
        "{{True,True},{False,False},{False,False},{True,True},{False,False},{True,True},{True,True},{False,False}}");

    check("f=BooleanFunction(12,2)", //
        "BooleanFunction(Index: 2 Number of variables: 2)");
    check("BooleanConvert(f)", //
        "#1&");

    check(
        "f=BooleanFunction({{False, False} -> True, {False, True} -> False, {True, False} -> True, {True, True} -> True} )", //
        "BooleanFunction(Index: 9 Number of variables: 2)&");
    check("BooleanConvert(f,\"CNF\")", //
        "#1||!#2&");

    check("f=BooleanConvert(!#1&&!#2 &, \"BFF\")", // BoolenFunctionForm
        "BooleanFunction(Index: 6 Number of variables: 2)&");
    check("BooleanConvert(f)", //
        "!#1&&!#2&");

    check("f=BooleanConvert(Xor(x,y,z), \"BFF\")", //
        "BooleanFunction(Index: 24 Number of variables: 3)");
    check("f(True,True,False)", //
        "False");
    check("f(False,True,False)", //
        "True");
    check("BooleanConvert(Xor(x,y,z))", //
        "(x&&y&&z)||(x&&!y&&!z)||(!x&&y&&!z)||(!x&&!y&&z)");

    check("f=BooleanFunction(30, {x, y, z})", //
        "(x&&!y&&!z)||(!x&&y)||(!x&&z)");
  }

  public void testBooleanFunction002() {

    check("f = BooleanFunction(30, 3)", //
        "BooleanFunction(Index: 23 Number of variables: 3)");
    check("f(True, False, True)", //
        "False");
    check("BooleanConvert(f(x, y, z), \"DNF\")", //
        "(x&&!y&&!z)||(!x&&y)||(!x&&z)");
    check("BooleanConvert(BooleanFunction(30, 3)[x, y, z], \"DNF\")", //
        "(x&&!y&&!z)||(!x&&y)||(!x&&z)");

    check(
        "BooleanFunction({{False, False} -> True, {False, True} -> False, {True, False} -> True,  {True, True} -> True}, {x, y})", //
        "x||!y");

    check("f=BooleanConvert(Xor(x,y,z), \"BFF\")", //
        "BooleanFunction(Index: 24 Number of variables: 3)");
    check("BooleanConvert(f)", //
        "(x&&y&&z)||(x&&!y&&!z)||(!x&&y&&!z)||(!x&&!y&&z)");
    check("BooleanTable({f, Xor(x, y, z)}, {x, y, z})", //
        "{{True,True},{False,False},{False,False},{True,True},{False,False},{True,True},{True,True},{False,False}}");

    check("f=BooleanFunction(11,2)", //
        "BooleanFunction(Index: 9 Number of variables: 2)");
    check("f(False,False)", //
        "True");
    check("f(False,True)", //
        "True");
    check("f(True,False)", //
        "False");
    check("f(True,True)", //
        "True");
    check("f(x,y)", //
        "f(x,y)");

  }

  public void testBooleanMinimize() {
    check("BooleanMinimize((x+1)&&(x+1))", //
        "1+x");

    // TODO CNF "blows up" some formulas
    check("BooleanMinimize(f(x))", //
        "f(x)");
    check("BooleanMinimize(Or(z,a, z))", //
        "a||z");
    check(
        "BooleanMinimize((a&&b&&!c)||(a&&!b&&c)||(a&&!c&&d)||(!a&&b&&c)||(b&&c&&!d)||(b&&!c&&d)||(!b&&c&&d))", //
        "(a&&b&&!c)||(a&&c&&!d)||(a&&!c&&d)||(!a&&b&&c)||(b&&!c&&d)||(!b&&c&&d)");

    // https://github.com/logic-ng/LogicNG/issues/23
    // a4 & a2 & a0 | a5 & a2 & a0 | a4 & a3 & a0 | a5 & a3 & a0 | a4 & a2 & a1 | a5 & a2 & a1 | a4
    // & a3 & a1 | a5 &
    // a3 & a1
    // a8 & a6 | a9 & a6 | a8 & a7 | a9 & a7
    // a10 | a11 | a12
    check(
        "BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1)", //
        "(a0&&a2&&a4)||(a0&&a2&&a5)||(a0&&a3&&a4)||(a0&&a3&&a5)||(a1&&a2&&a4)||(a1&&a2&&a5)||(a1&&a3&&a4)||(a1&&a3&&a5)");
    check(
        "BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1, \"CNF\")", //
        "(a0||a1)&&(a2||a3)&&(a4||a5)");

    check("BooleanMinimize(a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7)", //
        "(a6&&a8)||(a6&&a9)||(a7&&a8)||(a7&&a9)");
    check("BooleanMinimize(a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7, \"CNF\")", //
        "(a6||a7)&&(a8||a9)");

    check(
        "BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1 || a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7 || a10 || a11 || a12)", //
        "a10||a11||a12||(a0&&a2&&a4)||(a0&&a2&&a5)||(a0&&a3&&a4)||(a0&&a3&&a5)||(a1&&a2&&a4)||(a1&&a2&&a5)||(a1&&a3&&a4)||(a1&&a3&&a5)||(a6&&a8)||(a6&&a9)||(a7&&a8)||(a7&&a9)");
    check(
        "BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1 || a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7 || a10 || a11 || a12, \"CNF\")", //
        "(a0||a1||a10||a11||a12||a6||a7||a8||!a9)&&(a0||a1||a10||a11||a12||a6||a7||!a8)&&(a0||a1||a10||a11||a12||a8||a9)&&(!a0||a1||a10||a11||a12||a2||a3||a4||!a5||a6||a7||a8||!a9)&&(!a0||a1||a10||a11||a12||a2||a3||a4||!a5||a6||a7||!a8)&&(!a0||a1||a10||a11||a12||a2||a3||a4||!a5||a8||a9)&&(!a0||a1||a10||a11||a12||a2||a3||!a4||a6||a7||a8||!a9)&&(!a0||a1||a10||a11||a12||a2||a3||!a4||a6||a7||!a8)&&(!a0||a1||a10||a11||a12||a2||a3||!a4||a8||a9)&&(!a0||a1||a10||a11||a12||a4||a5||a6||a7||a8||!a9)&&(!a0||a1||a10||a11||a12||a4||a5||a6||a7||!a8)&&(!a0||a1||a10||a11||a12||a4||a5||a8||a9)&&(!a1||a10||a11||a12||a2||a3||a4||!a5||a6||a7||a8||!a9)&&(!a1||a10||a11||a12||a2||a3||a4||!a5||a6||a7||!a8)&&(!a1||a10||a11||a12||a2||a3||a4||!a5||a8||a9)&&(!a1||a10||a11||a12||a2||a3||!a4||a6||a7||a8||!a9)&&(!a1||a10||a11||a12||a2||a3||!a4||a6||a7||!a8)&&(!a1||a10||a11||a12||a2||a3||!a4||a8||a9)&&(!a1||a10||a11||a12||a4||a5||a6||a7||a8||!a9)&&(!a1||a10||a11||a12||a4||a5||a6||a7||!a8)&&(!a1||a10||a11||a12||a4||a5||a8||a9)");

    check(
        "BooleanMinimize((a0 || a1) && (a2 || a3) && (a4 || a5) || (a6 || a7) && (a8 || a9) || a10 || a11 || a12)", //
        "a10||a11||a12||(a0&&a2&&a4)||(a0&&a2&&a5)||(a0&&a3&&a4)||(a0&&a3&&a5)||(a1&&a2&&a4)||(a1&&a2&&a5)||(a1&&a3&&a4)||(a1&&a3&&a5)||(a6&&a8)||(a6&&a9)||(a7&&a8)||(a7&&a9)");
    check(
        "BooleanMinimize((a0 || a1) && (a2 || a3) && (a4 || a5) || (a6 || a7) && (a8 || a9) || a10 || a11 || a12, \"CNF\")", //
        "(a0||a1||a10||a11||a12||a6||a7)&&(a0||a1||a10||a11||a12||a6||!a7||a8||a9)&&(a0||a1||a10||a11||a12||!a6||a8||a9)&&(a0||!a1||a10||a11||a12||a2||a3||a6||a7)&&(a0||!a1||a10||a11||a12||a2||a3||a6||!a7||a8||a9)&&(a0||!a1||a10||a11||a12||a2||a3||!a6||a8||a9)&&(a0||!a1||a10||a11||a12||a2||!a3||a4||a5||a6||a7)&&(a0||!a1||a10||a11||a12||a2||!a3||a4||a5||a6||!a7||a8||a9)&&(a0||!a1||a10||a11||a12||a2||!a3||a4||a5||!a6||a8||a9)&&(a0||!a1||a10||a11||a12||!a2||a4||a5||a6||a7)&&(a0||!a1||a10||a11||a12||!a2||a4||a5||a6||!a7||a8||a9)&&(a0||!a1||a10||a11||a12||!a2||a4||a5||!a6||a8||a9)&&(!a0||a10||a11||a12||a2||a3||a6||a7)&&(!a0||a10||a11||a12||a2||a3||a6||!a7||a8||a9)&&(!a0||a10||a11||a12||a2||a3||!a6||a8||a9)&&(!a0||a10||a11||a12||a2||!a3||a4||a5||a6||a7)&&(!a0||a10||a11||a12||a2||!a3||a4||a5||a6||!a7||a8||a9)&&(!a0||a10||a11||a12||a2||!a3||a4||a5||!a6||a8||a9)&&(!a0||a10||a11||a12||!a2||a4||a5||a6||a7)&&(!a0||a10||a11||a12||!a2||a4||a5||a6||!a7||a8||a9)&&(!a0||a10||a11||a12||!a2||a4||a5||!a6||a8||a9)");

    check(
        "BooleanMinimize(!e && !d && c && b && a || !c && !f && !b && a || !f && a && !c && b || !i && f && h && !a || c && d && b && a || !b && g && a || !j && !h && !a)", //
        "(a&&b&&c&&d)||(a&&b&&c&&!e)||(a&&!b&&g)||(a&&!c&&!f)||(!a&&f&&h&&!i)||(!a&&!h&&!j)");
    check("BooleanMinimize((a && !b || !a && b) && " //
        + "(c && b && !a && !d && !e || c && !b && a && !d && !e || c && !b && !a && d && !e || c && !b && !a && !d && e || !c && b && a && !d && !e || !c && b && !a && d && !e || !c && b && !a && !d && e || !c && !b && a && d && !e || !c && !b && a && !d && e || !c && !b && !a && d && e) && "
        + "(a && !f || !a && f) && (e && !c || !e && c))", //
        "(a&&!b&&c&&!d&&!e&&!f)||(a&&!b&&!c&&!d&&e&&!f)||(!a&&b&&c&&!d&&!e&&f)||(!a&&b&&!c&&!d&&e&&f)");
    check("BooleanMinimize((a&&!b)||(!a&&b)||(b&&!c)||(!b&&c))", //
        "(a&&!b)||(!a&&c)||(b&&!c)");
    check("BooleanMinimize((a||b)&&(c||d))", //
        "(a&&c)||(a&&d)||(b&&c)||(b&&d)");
    check("BooleanMinimize(a && b || ! a && b)", //
        "b");
    check("BooleanMinimize(Equivalent(x, y, z))", //
        "(x&&y&&z)||(!x&&!y&&!z)");
    check("BooleanMinimize(Equivalent(x, y, z), \"CNF\")", //
        "(x||y||!z)&&(x||!y)&&(!x||y)&&(!x||!y||z)");

    check("BooleanMinimize((a&&!b)||(!a&&b)||(b&&!c)||(!b&&c), \"CNF\")", //
        "(a||b||c)&&(!a||!b||!c)");
    check("BooleanMinimize((a||b)&&(c||d), \"CNF\")", //
        "(a||b)&&(c||d)");
  }

  public void testBooleanMaxterms() {
    check("BooleanMaxterms({{1,1,1,1}}, {a, b, c,d})", //
        "a||b||c||d");
    check("BooleanMaxterms({{True,True,True,True}}, {a, b, c,d})", //
        "a||b||c||d");

    check("BooleanMaxterms({{True,False,True}}, {a, b, c})", //
        "a||!b||c");
    check("BooleanMaxterms({{1, 0, 1}}, {a, b, c})", //
        "a||!b||c");
    check("BooleanMaxterms({IntegerDigits(11,2,3)},{a,b,c})", //
        "!a||b||c");
  }

  public void testBooleanMinterms() {
    check("BooleanMinterms({{1,1,1,1}}, {a, b, c,d})", //
        "a&&b&&c&&d");
    check("BooleanMinterms({{True,True,True,True}}, {a, b, c,d})", //
        "a&&b&&c&&d");


    check("IntegerDigits(11,2,3) ", //
        "{0,1,1}");

    check("BooleanMinterms({{True,False,True}}, {a, b, c})", //
        "a&&!b&&c");
    check("BooleanMinterms({{1, 0, 1}}, {a, b, c})", //
        "a&&!b&&c");
    check("IntegerDigits(11,2,3) ", //
        "{0,1,1}");
    check("BooleanMinterms({IntegerDigits(11,2,3)},{a,b,c})", //
        "!a&&b&&c");
  }

  public void testBooleanQ() {
    check("BooleanQ(True)", //
        "True");
    check("BooleanQ(False)", //
        "True");
    check("BooleanQ(f(x))", //
        "False");
    check("BooleanQ(Together(x/y + y/x))", //
        "False");
  }

  public void testBooleanTable() {
    check("BooleanTable(BooleanFunction(30, 3))", //
        "{False,False,False,True,True,True,True,False}");

    check("BooleanTable({a,b,c,(a&&b)||c},{a,b,c}) // TableForm", //
        "True  True  True  True \n" + //
            "True  True  False True \n" + //
            "True  False True  True \n" + //
            "True  False False False\n" + //
            "False True  True  True \n" + //
            "False True  False False\n" + //
            "False False True  True \n" + //
            "False False False False");
    check("BooleanTable(Xor(p, q, r))", //
        "{True,False,False,True,False,True,True,False}");
    check("BooleanTable(Xor(p, q, r), {p, q, r})", //
        "{True,False,False,True,False,True,True,False}");
    check("BooleanTable(Implies(Implies(p, q), r), {p, q, r})", //
        "{True,False,True,True,True,False,True,False}");
    check("BooleanTable(p || q, {p, q})", //
        "{True,True,True,False}");
    check("BooleanTable(And(a, b, c), {a, b, c})", //
        "{True,False,False,False,False,False,False,False}");

    check("BooleanTable({And(a, b), Or(a, b), Xor(a, b), Xnor(a, b)}, {a, b}) // TableForm", //
        "True  True  False True \n"//
            + "False True  True  False\n"//
            + "False True  True  False\n"//
            + "False False False True ");

  }

  public void testBooleanVariables() {
    check("BooleanVariables(#1 && #199 &)", //
        "199");
    check("BooleanVariables(BooleanFunction(30,3))", //
        "3");
    check("BooleanVariables(a || ! b && b)", //
        "{a,b}");
    check("BooleanVariables(Xor(a, And(b, Or(c, d))))", //
        "{a,b,c,d}");
    check("BooleanVariables(a && b || ! a && b)", //
        "{a,b}");
    check("BooleanVariables(Xor(p,q,r))", //
        "{p,q,r}");
    check("BooleanVariables(a + b*c)", //
        "{}");
  }

  public void testEqual() {
    // TODO
    // check(
    // "0.1111111111111111 == 0.1111111111111126", //
    // "True");

    // mathics expects result: 2. + 3.14159 I == 2 + 3 a
    check("2.+ I*Pi == 2 + 3*a", //
        "a==I*1.0472");

    check("0.25==a", //
        "0.25==a");
    check("Sqrt(2)==Infinity", //
        "False");
    check("-Infinity==Sqrt(2)", //
        "False");
    check("Sqrt(2)==\"1 / 4\"", //
        "Sqrt(2)==1 / 4");
    check("Sqrt(2)==I", //
        "False");
    check("Sqrt(2)==0", //
        "False");
    check("1 / 4==Sqrt(2) ", //
        "False");
    check("0.25==Sqrt(2) ", //
        "False");
    check("Sqrt(2)==BesselJ(0,2)", //
        "False");
    check("Sqrt(2)==3+2*I", //
        "False");
    check("3.0+2.0*I==3+2*I", //
        "True");
    check("Infinity== -Infinity", //
        "False");
    check("-Infinity== -Infinity", //
        "True");
    check("-Infinity==Infinity", //
        "False");
    check("BesselJ(0,2)==BesselJ(0,2)", //
        "True");
    check("2.0+ I*Pi == Infinity", //
        "False");
    check("2.0+ I*Pi == Sqrt(I)*Infinity", //
        "False");
    check("I==0", //
        "False");
    check("I + 0 == 1*I - 0", //
        "True");
    check("I==I", //
        "True");
    check("3+I*Pi == I", //
        "False");

    check("\"test\"==\"test\"", //
        "True");
    check("\"11\"==11", //
        "False");

    check("foo(x == 2, y, x) == foo(x == 2., y, x)", //
        "True");
    check("foo(a,b)==foo(a,b)", //
        "True");
    check("2.0==2", //
        "True");

    // https://github.com/axkr/symja_android_library/issues/142
    check("20-x*y*(x+y)==0", //
        "x*y*(x+y)==20");
    check("Tan(5*ArcTan(29/278) + 7 ArcTan(3/79))==1", //
        "True");
    check("-I==1", //
        "False");
    check("1/2*(1+Sqrt(5))==GoldenRatio", //
        "True");
    check("x^2+4*x+4==(x+2)^2", //
        "True");
    check("x^2+x==x*(x+1)", //
        "True");

    // github issue #42
    check("1-i==1.0-i", //
        "True");

    // Issue #174
    check("x/(y*x)==0.25", //
        "1/y==0.25");

    check("a==a", //
        "True");
    check("a==b", //
        "a==b");
    check("1==1.", //
        "True");
    check("{{1}, {2}} == {{1}, {2}}", //
        "True");
    check("{1, 2} == {1, 2, 3}", //
        "False");
    // check("N(E, 100) == N(E, 150)", "True");

    check("E > 1", //
        "True");
    check("Pi == 3.14", //
        "False");
    check("Pi ^ E == E ^ Pi", //
        "False");
    check("N(E, 3) == N(E)", //
        "True");
    check("{1, 2, 3} < {1, 2, 3}", //
        "{1,2,3}<{1,2,3}");
    check("E == N(E)", //
        "True");
    check("{Equal(Equal(0, 0), True), Equal(0, 0) == True}", //
        "{True,True}");
    check("{True,False,True==False,True!=False}", //
        "{True,False,False,True}");
    check(
        "{Mod(6, 2) == 0, Mod(6, 4) == 0, (Mod(6, 2) == 0) == (Mod(6, 4) == 0), (Mod(6, 2) == 0) != (Mod(6, 4) == 0)}", //
        "{True,False,False,True}");
    check("a == a == a", //
        "True");
    check("{Equal(), Equal(x), Equal(1)}", //
        "{True,True,True}");

    check("{\"a\",\"b\"}=={\"a\",\"b\"}", //
        "True");
    check("{\"a\",\"b\"}=={\"b\",\"a\"}", //
        "False");
    check("{\"a\",b}=={\"a\",c}", //
        "{a,b}=={a,c}");
    check("a==a==b==c", //
        "a==b==c");
    check("a==a==a==a", //
        "True");
    check("Pi==3", //
        "False");
    check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi==0", //
        "True");
  }

  public void testEquivalent() {
    check("Equivalent(True, True, False)", //
        "False");
    check("Equivalent(a,b,c)", //
        "a⇔b⇔c");
    check("Equivalent(a \\[Implies] b,c)", //
        "c⇔(a⇒b)");
    check("Equivalent(a \\[Equivalent] b,c)", //
        "c⇔a⇔b");
    check("Equivalent(a,b,True,c)", //
        "a&&b&&c");
    check("Equivalent(a)", //
        "True");

    check("Equivalent()", //
        "True");
    check("Equivalent(4)", //
        "True");
    check("Equivalent(a,a)", //
        "True");
    check("Equivalent(a,b,a,b,c)", //
        "a⇔b⇔c");
    check("Equivalent(a,b,c,True,False)", //
        "False");
    check("Equivalent(a,b,c,True)", //
        "a&&b&&c");
    check("Equivalent(a,b,c,False)", //
        "!a&&!b&&!c");
    check("Equivalent(a && (b || c), a && b || a && c) // TautologyQ", //
        "True");
  }

  public void testExists() {
    check("Exists(a, f(b)>c)", //
        "f(b)>c");
  }

  public void testForAll() {
    check("ForAll(a, f(b)>c)", //
        "f(b)>c");
  }

  public void testGreater() {
    // github #200
    check("-I*Sqrt(11)>0", //
        "-I*Sqrt(11)>0");

    check("x>x", //
        "x>x");
    check("x+1>x", //
        "1+x>x");

    check("42>Infinity", //
        "False");

    check("Infinity>Infinity", //
        "False");

    check("Refine(Infinity>x, x>0)", //
        "True");
    check("Refine(-Infinity>x, x>0)", //
        "False");

    check("{Greater(), Greater(x), Greater(1)}", //
        "{True,True,True}");
    check("Pi>0", //
        "True");
    check("Pi+E<8", //
        "True");
    check("2/17 > 1/5 > Pi/10", //
        "False");
    check("x<x", //
        "x<x");
    check("x<=x", //
        "x<=x");
    check("x>x", //
        "x>x");
    check("x>=x", //
        "x>=x");
  }

  public void testGreaterEqual() {
    check("x>=x", //
        "x>=x");
    check("Infinity>=Infinity", //
        "True");

    check("Refine(Infinity>=x, x>0)", //
        "True");
    check("Refine(-Infinity>=x, x>0)", //
        "False");

    check("{GreaterEqual(), GreaterEqual(x), GreaterEqual(1)}", //
        "{True,True,True}");
    check("Pi>=0", //
        "True");
    check("Pi+E<=8", //
        "True");
    check("2/17 >= 1/5 >= Pi/10", //
        "False");
    check("x>=x", //
        "x>=x");
    check("x>x", //
        "x>x");
  }

  public void testImplies() {
    check("Implies(!a,!a)", //
        "True");
    check("Implies(False, a)", //
        "True");
    check("Implies(True, a)", //
        "a");
    check("Implies(a,Implies(b,Implies(True,c)))", //
        "a⇒b⇒c");

    check("Implies(p,q)", //
        "p⇒q");

    check("Implies(a,True)", //
        "True");
    check("Implies(a,False)", //
        "!a");
    check("Implies(a,a)", //
        "True");
  }

  public void testInequality() {
    // check("Inequality(-1,Less,0,Lest,1)", //
    // "Inequality(0,Lest,1)");
    check("b != c != d", //
        "b!=c!=d");
    check("a<b!=c!=d", //
        "a<b&&b!=c&&c!=d");
    check("3 < 4 >= 2 != 1", //
        "True");
    check("Inequality(-1,Less,0,LessEqual,a,Less,3,Less,4,Less,5,Less,b,Less,10,Less,11)", //
        "0<=a<3<5<b<10");

    check("Inequality(c,Less,0,Less,a)", //
        "c<0<a");
    check("Inequality(-Pi,Less,0,LessEqual,Pi)", //
        "True");
    check("Inequality(c,Less,0)", //
        "c<0");

    check("Inequality(c,Less)", //
        "Inequality(c,Less)");
    check("Inequality(c)", //
        "True");
    check("Inequality(False)", //
        "True");
    check("Inequality( )", //
        "Inequality()");

    check("Inequality(-1,Less,a,Less,0,Less,1)", //
        "-1<a<0");
    check("Inequality(-1,Less,0,Less,a,Less,1)", //
        "0<a<1");
    check("Inequality(-1,Less,a,Less,-2)", //
        "False");
    check("Inequality(-Pi,Less,0,GreaterEqual,a)", //
        "0>=a");
    check("Inequality(0,Less,a,Greater,0,Greater,k)", //
        "0<a&&a>0>k");
    check("Inequality(0,Greater,a,Less,0)", //
        "0>a&&a<0");
    check("Inequality(0,Less,a,Less,1)", //
        "0<a<1");
    check("0<a && a<1", //
        "0<a&&a<1");
    check("Inequality(a,Less,b,LessEqual,c)", //
        "a<b<=c");

    check("Inequality(a,Less,b,LessEqual,c,Equal,d,GreaterEqual,e,Greater,f)", //
        "a<b<=c==d&&d>=e>f");
    check("Inequality(a,Less,b,LessEqual,c,Unequal,d,GreaterEqual,e,Greater,f)", //
        "a<b<=c&&c!=d&&d>=e>f");
    check("Inequality(a,Greater,b,GreaterEqual,c,Equal,d,LessEqual,e,Less,f)", //
        "a>b>=c==d&&d<=e<f");
    check("Inequality(a,Greater,b,GreaterEqual,c,Equal,d,GreaterEqual,e,Less,f)", //
        "a>b>=c==d>=e&&e<f");
    check("Inequality(a,Greater,1,GreaterEqual,c,Equal,d,GreaterEqual,5,Less,f)", //
        "False");
    check("a<1<2<3<4<=b", //
        "a<1<4<=b");
    check("a<1<2<3<4<=b<5", //
        "a<1<4<=b<5");
    check("Inequality(-1,Less,0,Lest,1)", //
        "Inequality(-1,Less,0,lest,1)");
    check("Inequality(-1,Lest,0,Less,1)", //
        "Inequality(-1,lest,0,Less,1)");
  }

  public void testLess() {
    check("Infinity<Infinity", //
        "False");

    check("x<x", //
        "x<x");
    check("I<0", //
        "I<0");
    check("3+x<4+x", //
        "3+x<4+x");
    check("3+x>4+x", //
        "3+x>4+x");

    check("Refine(Infinity<x, x>0)", //
        "False");
    check("Refine(-Infinity<x, x>0)", //
        "True");

    check("3<4", //
        "True");
    check("3<4<5", //
        "True");
    check("{Less(), Less(x), Less(1)}", //
        "{True,True,True}");
    check("(2*x+5)<(5^(1/2))", //
        "x<1/2*(-5+Sqrt(5))");
    check("(-2*x+5)<(5^(1/2))", //
        "x>1/2*(5-Sqrt(5))");
  }

  public void testLessEqual() {
    check("x<=x", //
        "x<=x");
    check("3+x<=4+x", //
        "3+x<=4+x");
    check("3+x>=4+x", //
        "3+x>=4+x");

    check("Infinity<=Infinity", //
        "True");

    check("Refine(Infinity<=x, x>0)", //
        "False");
    check("Refine(-Infinity<=x, x>0)", //
        "True");

    check("3<=4", //
        "True");
    check("3<=4<=5", //
        "True");
    check("{LessEqual(), LessEqual(x), LessEqual(1)}", //
        "{True,True,True}");
    check("(2*x+5)<=(5^(1/2))", //
        "x<=1/2*(-5+Sqrt(5))");
    check("(-2*x+5)<=(5^(1/2))", //
        "x>=1/2*(5-Sqrt(5))");
  }

  public void testLogicalExpand() {
    // TODO
    // check("LogicalExpand(r && s && q || r || s)", //
    // " r || s");
    // check("LogicalExpand(x == a && y == b || x == a || y == b)", //
    // "");
    check("e1=Implies(Xor(a, b, c), (a || b) && c)", //
        "Xor(a,b,c)⇒(a||b)&&c");
    check("e2=LogicalExpand(Implies(Xor(a, b, c), (a || b) && c))", //
        "(a&&b&&!c)||(a&&!b&&c)||(a&&c)||(!a&&b&&c)||(!a&&!b&&!c)||(b&&c)");
    check("Table(e1 == e2, {a, {True, False}}, {b, {True, False}}, {c, {True, False}})", //
        "{{{True,True},{True,True}},{{True,True},{True,True}}}");

    check("LogicalExpand((a || b) && ! (c || d || e))", //
        "(a&&!c&&!d&&!e)||(b&&!c&&!d&&!e)");
    check("LogicalExpand(p && ! (q || r))", //
        "p&&!q&&!r");
    check("LogicalExpand(a || b || ! a)", //
        "True");
    check("LogicalExpand(a && b && ! a)", //
        "False");
    check("LogicalExpand(Xor(p, q, r))", //
        "(p&&q&&r)||(r&&!p&&!q)||(q&&!p&&!r)||(p&&!q&&!r)");
    check("LogicalExpand(Xor(p, q, r, s))", //
        "(q&&r&&s&&!p)||(p&&r&&s&&!q)||(p&&q&&s&&!r)||(s&&!p&&!q&&!r)||(p&&q&&r&&!s)||(r&&!p&&!q&&!s)||(q&&!p&&!r&&!s)||(p&&!q&&!r&&!s)");
    check("LogicalExpand(Xor(p, q, r, s, t))", //
        "(p&&q&&r&&s&&t)||(r&&s&&t&&!p&&!q)||(q&&s&&t&&!p&&!r)||(p&&s&&t&&!q&&!r)||(q&&r&&t&&!p&&!s)||"
            + //
            "(p&&r&&t&&!q&&!s)||(p&&q&&t&&!r&&!s)||(t&&!p&&!q&&!r&&!s)||(q&&r&&s&&!p&&!t)||" + //
            "(p&&r&&s&&!q&&!t)||(p&&q&&s&&!r&&!t)||(s&&!p&&!q&&!r&&!t)||(p&&q&&r&&!s&&!t)||" + //
            "(r&&!p&&!q&&!s&&!t)||(q&&!p&&!r&&!s&&!t)||(p&&!q&&!r&&!s&&!t)");
  }

  public void testSatisfiabilityCount() {
    check("SatisfiabilityCount(Equivalent(a, b), {a, b})", //
        "2");
    check("SatisfiabilityCount(a || b, {a, b})", //
        "3");
    check("SatisfiabilityCount(a || b)", //
        "3");
    check("SatisfiabilityCount(Xor(a, b, c), {a, b, c} )", //
        "4");

    check("SatisfiabilityCount(a&&!(b||!c) )", //
        "1");
    check("SatisfiabilityCount((a || b) && (! a || ! b) )", //
        "2");
    check("SatisfiabilityCount((a || b) && (! a || ! b), {a, b})", //
        "2");

    check("SatisfiabilityCount(!Implies(Implies(a, b) && ! b, ! a))", //
        "0");
    check("SatisfiabilityCount((a && b) && (! a || ! b) )", //
        "0");
    check("SatisfiabilityCount((a && b) && (! a || ! b), {a, b})", //
        "0");
  }

  public void testSatisfiabilityInstances() {
    check("SatisfiabilityInstances(BooleanFunction(30, 3), 3)", //
        "{{True,False,False},{False,True,False},{False,False,True}}");
    // message SatisfiabilityInstances: Null is not a valid variable.
    check("SatisfiabilityInstances(Null,{x},3)", //
        "SatisfiabilityInstances(Null,{x},3)");

    check("SatisfiabilityInstances(a || b,All)", //
        "{{True,True},{True,False},{False,True}}");

    check("SatisfiabilityInstances(a || b, {a, b, c},All)", //
        "{{True,True,True},{True,True,False},{True,False,True},{True,False,False},{False,True,True},{False,True,False},{False,False,True}}");
    check("SatisfiabilityInstances((a || b || c) && (! a || ! b || ! c), {a, b, c},2)", //
        "{{False,True,True},{False,True,False}}");

    check("SatisfiabilityInstances((a || b || c) && (! a || ! b || ! c), {a, b, c},All)", //
        "{{True,True,False},{True,False,True},{True,False,False},{False,True,True},{False,True,False},{False,False,True}}");
    check("SatisfiabilityInstances(a&&!(b||!c), {b,a,c}, All )", //
        "{{False,True,True}}");
    check("SatisfiabilityInstances(a&&!(b||!c), {a,b,c}, All )", //
        "{{True,False,True}}");

    check("SatisfiabilityInstances(a || b, {a, b}, All)", //
        "{{True,True},{True,False},{False,True}}");
    check("SatisfiabilityInstances(Equivalent(a, b), {a, b}, 4)", //
        "{{True,True},{False,False}}");
    check("SatisfiabilityInstances(Equivalent(a, b), {a, b})", //
        "{{True,True}}");
    check("SatisfiabilityInstances(Xor(a, b, c), {a, b, c}, 2^3)", //
        "{{True,True,True},{True,False,False},{False,True,False},{False,False,True}}");

    check("SatisfiabilityInstances(a&&!(b||!c) )", //
        "{{True,False,True}}");

    check("SatisfiabilityInstances((a || b) && (! a || ! b) )", //
        "{{False,True}}");
    check("SatisfiabilityInstances((a || b) && (! a || ! b), {a, b}, All)", //
        "{{True,False},{False,True}}");

    check("SatisfiabilityInstances(!Implies(Implies(a, b) && ! b, ! a))", //
        "{}");
    check("SatisfiabilityInstances((a && b) && (! a || ! b) )", //
        "{}");
    check("SatisfiabilityInstances((a && b) && (! a || ! b), {a, b})", //
        "{}");
  }

  public void testSatisfiableQ() {
    check("SatisfiableQ(a&&!(b||!c) )", //
        "True");
    check("SatisfiableQ((a || b) && (! a || ! b) )", //
        "True");
    check("SatisfiableQ((a || b) && (! a || ! b), {a, b})", //
        "True");

    check("SatisfiableQ(!Implies(Implies(a, b) && ! b, ! a))", //
        "False");
    check("SatisfiableQ((a && b) && (! a || ! b) )", //
        "False");
    check("SatisfiableQ((a && b) && (! a || ! b), {a, b})", //
        "False");
    check(
        "SatisfiableQ((Equivalent(b11D, b21U)) && (Equivalent(b12D, b22U)) && \n"
            + " (Equivalent(b13D, b23U)) && (Equivalent(b14D, b24U)) && \n"
            + " (Equivalent(b15D, b25U)) && (Equivalent(b21D, b31U)) && \n"
            + " (Equivalent(b22D, b32U)) && (Equivalent(b23D, b33U)) && \n"
            + " (Equivalent(b24D, b34U)) && (Equivalent(b25D, b35U)) && \n"
            + " (Equivalent(b31D, b41U)) && (Equivalent(b32D, b42U)) && \n"
            + " (Equivalent(b33D, b43U)) && (Equivalent(b34D, b44U)) && \n"
            + " (Equivalent(b35D, b45U)) && (Equivalent(b41D, b51U)) && \n"
            + " (Equivalent(b42D, b52U)) && (Equivalent(b43D, b53U)) && \n"
            + " (Equivalent(b44D, b54U)) && (Equivalent(b45D, b55U)) && \n"
            + " (Equivalent(b11R, b12L)) && (Equivalent(b12R, b13L)) && \n"
            + " (Equivalent(b13R, b14L)) && (Equivalent(b14R, b15L)) && \n"
            + " (Equivalent(b21R, b22L)) && (Equivalent(b22R, b23L)) && \n"
            + " (Equivalent(b23R, b24L)) && (Equivalent(b24R, b25L)) && \n"
            + " (Equivalent(b31R, b32L)) && (Equivalent(b32R, b33L)) && \n"
            + " (Equivalent(b33R, b34L)) && (Equivalent(b34R, b35L)) && \n"
            + " (Equivalent(b41R, b42L)) && (Equivalent(b42R, b43L)) && \n"
            + " (Equivalent(b43R, b44L)) && (Equivalent(b44R, b45L)) && \n"
            + " (Equivalent(b51R, b52L)) && (Equivalent(b52R, b53L)) && \n"
            + " (Equivalent(b53R, b54L)) && (Equivalent(b54R, b55L)) &&  !b11L &&  !b21L && \n"
            + "  !b31L &&  !b41L &&  !b51L &&  !b15R &&  !b25R &&  !b35R &&  !b45R && \n"
            + "  !b55R &&  !b11U &&  !b12U &&  !b13U &&  !b14U &&  !b15U &&  !b51D && \n"
            + "  !b52D &&  !b53D &&  !b54D &&  !b55D && \n"
            + " ((b11U &&  !b11D &&  !b11L &&  !b11R) || (b11D &&  !b11U &&  !b11L && \n"
            + "    !b11R) || (b11L &&  !b11U &&  !b11D &&  !b11R) || \n"
            + "  (b11R &&  !b11U &&  !b11D &&  !b11L)) && \n"
            + " ((b13U &&  !b13D &&  !b13L &&  !b13R) || (b13D &&  !b13U &&  !b13L && \n"
            + "    !b13R) || (b13L &&  !b13U &&  !b13D &&  !b13R) || \n"
            + "  (b13R &&  !b13U &&  !b13D &&  !b13L)) && \n"
            + " ((b15U &&  !b15D &&  !b15L &&  !b15R) || (b15D &&  !b15U &&  !b15L && \n"
            + "    !b15R) || (b15L &&  !b15U &&  !b15D &&  !b15R) || \n"
            + "  (b15R &&  !b15U &&  !b15D &&  !b15L)) && \n"
            + " ((b23U &&  !b23D &&  !b23L &&  !b23R) || (b23D &&  !b23U &&  !b23L && \n"
            + "    !b23R) || (b23L &&  !b23U &&  !b23D &&  !b23R) || \n"
            + "  (b23R &&  !b23U &&  !b23D &&  !b23L)) && \n"
            + " ((b25U &&  !b25D &&  !b25L &&  !b25R) || (b25D &&  !b25U &&  !b25L && \n"
            + "    !b25R) || (b25L &&  !b25U &&  !b25D &&  !b25R) || \n"
            + "  (b25R &&  !b25U &&  !b25D &&  !b25L)) && \n"
            + " ((b42U &&  !b42D &&  !b42L &&  !b42R) || (b42D &&  !b42U &&  !b42L && \n"
            + "    !b42R) || (b42L &&  !b42U &&  !b42D &&  !b42R) || \n"
            + "  (b42R &&  !b42U &&  !b42D &&  !b42L)) && \n"
            + " ((b44U &&  !b44D &&  !b44L &&  !b44R) || (b44D &&  !b44U &&  !b44L && \n"
            + "    !b44R) || (b44L &&  !b44U &&  !b44D &&  !b44R) || \n"
            + "  (b44R &&  !b44U &&  !b44D &&  !b44L)) && \n"
            + " ((b52U &&  !b52D &&  !b52L &&  !b52R) || (b52D &&  !b52U &&  !b52L && \n"
            + "    !b52R) || (b52L &&  !b52U &&  !b52D &&  !b52R) || \n"
            + "  (b52R &&  !b52U &&  !b52D &&  !b52L)) && \n"
            + " ((b53U &&  !b53D &&  !b53L &&  !b53R) || (b53D &&  !b53U &&  !b53L && \n"
            + "    !b53R) || (b53L &&  !b53U &&  !b53D &&  !b53R) || \n"
            + "  (b53R &&  !b53U &&  !b53D &&  !b53L)) && \n"
            + " ((b55U &&  !b55D &&  !b55L &&  !b55R) || (b55D &&  !b55U &&  !b55L && \n"
            + "    !b55R) || (b55L &&  !b55U &&  !b55D &&  !b55R) || \n"
            + "  (b55R &&  !b55U &&  !b55D &&  !b55L)) && \n"
            + " ((b12U && b12D &&  !b12L &&  !b12R) || (b12U && b12L &&  !b12D &&  !b12R) || \n"
            + "  (b12U && b12R &&  !b12D &&  !b12L) || (b12D && b12L &&  !b12R &&  !b12U) || \n"
            + "  (b12D && b12R &&  !b12L &&  !b12U) || (b12L && b12R &&  !b12D && \n"
            + "    !b12U)) && ((b14U && b14D &&  !b14L &&  !b14R) || \n"
            + "  (b14U && b14L &&  !b14D &&  !b14R) || (b14U && b14R &&  !b14D &&  !b14L) || \n"
            + "  (b14D && b14L &&  !b14R &&  !b14U) || (b14D && b14R &&  !b14L &&  !b14U) || \n"
            + "  (b14L && b14R &&  !b14D &&  !b14U)) && \n"
            + " ((b21U && b21D &&  !b21L &&  !b21R) || (b21U && b21L &&  !b21D &&  !b21R) || \n"
            + "  (b21U && b21R &&  !b21D &&  !b21L) || (b21D && b21L &&  !b21R &&  !b21U) || \n"
            + "  (b21D && b21R &&  !b21L &&  !b21U) || (b21L && b21R &&  !b21D && \n"
            + "    !b21U)) && ((b22U && b22D &&  !b22L &&  !b22R) || \n"
            + "  (b22U && b22L &&  !b22D &&  !b22R) || (b22U && b22R &&  !b22D &&  !b22L) || \n"
            + "  (b22D && b22L &&  !b22R &&  !b22U) || (b22D && b22R &&  !b22L &&  !b22U) || \n"
            + "  (b22L && b22R &&  !b22D &&  !b22U)) && \n"
            + " ((b24U && b24D &&  !b24L &&  !b24R) || (b24U && b24L &&  !b24D &&  !b24R) || \n"
            + "  (b24U && b24R &&  !b24D &&  !b24L) || (b24D && b24L &&  !b24R &&  !b24U) || \n"
            + "  (b24D && b24R &&  !b24L &&  !b24U) || (b24L && b24R &&  !b24D && \n"
            + "    !b24U)) && ((b31U && b31D &&  !b31L &&  !b31R) || \n"
            + "  (b31U && b31L &&  !b31D &&  !b31R) || (b31U && b31R &&  !b31D &&  !b31L) || \n"
            + "  (b31D && b31L &&  !b31R &&  !b31U) || (b31D && b31R &&  !b31L &&  !b31U) || \n"
            + "  (b31L && b31R &&  !b31D &&  !b31U)) && \n"
            + " ((b32U && b32D &&  !b32L &&  !b32R) || (b32U && b32L &&  !b32D &&  !b32R) || \n"
            + "  (b32U && b32R &&  !b32D &&  !b32L) || (b32D && b32L &&  !b32R &&  !b32U) || \n"
            + "  (b32D && b32R &&  !b32L &&  !b32U) || (b32L && b32R &&  !b32D && \n"
            + "    !b32U)) && ((b33U && b33D &&  !b33L &&  !b33R) || \n"
            + "  (b33U && b33L &&  !b33D &&  !b33R) || (b33U && b33R &&  !b33D &&  !b33L) || \n"
            + "  (b33D && b33L &&  !b33R &&  !b33U) || (b33D && b33R &&  !b33L &&  !b33U) || \n"
            + "  (b33L && b33R &&  !b33D &&  !b33U)) && \n"
            + " ((b34U && b34D &&  !b34L &&  !b34R) || (b34U && b34L &&  !b34D &&  !b34R) || \n"
            + "  (b34U && b34R &&  !b34D &&  !b34L) || (b34D && b34L &&  !b34R &&  !b34U) || \n"
            + "  (b34D && b34R &&  !b34L &&  !b34U) || (b34L && b34R &&  !b34D && \n"
            + "    !b34U)) && ((b35U && b35D &&  !b35L &&  !b35R) || \n"
            + "  (b35U && b35L &&  !b35D &&  !b35R) || (b35U && b35R &&  !b35D &&  !b35L) || \n"
            + "  (b35D && b35L &&  !b35R &&  !b35U) || (b35D && b35R &&  !b35L &&  !b35U) || \n"
            + "  (b35L && b35R &&  !b35D &&  !b35U)) && \n"
            + " ((b41U && b41D &&  !b41L &&  !b41R) || (b41U && b41L &&  !b41D &&  !b41R) || \n"
            + "  (b41U && b41R &&  !b41D &&  !b41L) || (b41D && b41L &&  !b41R &&  !b41U) || \n"
            + "  (b41D && b41R &&  !b41L &&  !b41U) || (b41L && b41R &&  !b41D && \n"
            + "    !b41U)) && ((b43U && b43D &&  !b43L &&  !b43R) || \n"
            + "  (b43U && b43L &&  !b43D &&  !b43R) || (b43U && b43R &&  !b43D &&  !b43L) || \n"
            + "  (b43D && b43L &&  !b43R &&  !b43U) || (b43D && b43R &&  !b43L &&  !b43U) || \n"
            + "  (b43L && b43R &&  !b43D &&  !b43U)) && \n"
            + " ((b45U && b45D &&  !b45L &&  !b45R) || (b45U && b45L &&  !b45D &&  !b45R) || \n"
            + "  (b45U && b45R &&  !b45D &&  !b45L) || (b45D && b45L &&  !b45R &&  !b45U) || \n"
            + "  (b45D && b45R &&  !b45L &&  !b45U) || (b45L && b45R &&  !b45D && \n"
            + "    !b45U)) && ((b51U && b51D &&  !b51L &&  !b51R) || \n"
            + "  (b51U && b51L &&  !b51D &&  !b51R) || (b51U && b51R &&  !b51D &&  !b51L) || \n"
            + "  (b51D && b51L &&  !b51R &&  !b51U) || (b51D && b51R &&  !b51L &&  !b51U) || \n"
            + "  (b51L && b51R &&  !b51D &&  !b51U)) && \n"
            + " ((b54U && b54D &&  !b54L &&  !b54R) || (b54U && b54L &&  !b54D &&  !b54R) || \n"
            + "  (b54U && b54R &&  !b54D &&  !b54L) || (b54D && b54L &&  !b54R &&  !b54U) || \n"
            + "  (b54D && b54R &&  !b54L &&  !b54U) || (b54L && b54R &&  !b54D && \n"
            + "    !b54U)) && ((c12a &&  !c12b &&  !c12c &&  !c12d &&  !c12e) || \n"
            + "  (c12b &&  !c12a &&  !c12c &&  !c12d &&  !c12e) || \n"
            + "  (c12c &&  !c12a &&  !c12b &&  !c12d &&  !c12e) || \n"
            + "  (c12d &&  !c12a &&  !c12b &&  !c12c &&  !c12e) || \n"
            + "  (c12e &&  !c12a &&  !c12b &&  !c12c &&  !c12d)) && \n"
            + " ((c14a &&  !c14b &&  !c14c &&  !c14d &&  !c14e) || \n"
            + "  (c14b &&  !c14a &&  !c14c &&  !c14d &&  !c14e) || \n"
            + "  (c14c &&  !c14a &&  !c14b &&  !c14d &&  !c14e) || \n"
            + "  (c14d &&  !c14a &&  !c14b &&  !c14c &&  !c14e) || \n"
            + "  (c14e &&  !c14a &&  !c14b &&  !c14c &&  !c14d)) && \n"
            + " ((c21a &&  !c21b &&  !c21c &&  !c21d &&  !c21e) || \n"
            + "  (c21b &&  !c21a &&  !c21c &&  !c21d &&  !c21e) || \n"
            + "  (c21c &&  !c21a &&  !c21b &&  !c21d &&  !c21e) || \n"
            + "  (c21d &&  !c21a &&  !c21b &&  !c21c &&  !c21e) || \n"
            + "  (c21e &&  !c21a &&  !c21b &&  !c21c &&  !c21d)) && \n"
            + " ((c22a &&  !c22b &&  !c22c &&  !c22d &&  !c22e) || \n"
            + "  (c22b &&  !c22a &&  !c22c &&  !c22d &&  !c22e) || \n"
            + "  (c22c &&  !c22a &&  !c22b &&  !c22d &&  !c22e) || \n"
            + "  (c22d &&  !c22a &&  !c22b &&  !c22c &&  !c22e) || \n"
            + "  (c22e &&  !c22a &&  !c22b &&  !c22c &&  !c22d)) && \n"
            + " ((c24a &&  !c24b &&  !c24c &&  !c24d &&  !c24e) || \n"
            + "  (c24b &&  !c24a &&  !c24c &&  !c24d &&  !c24e) || \n"
            + "  (c24c &&  !c24a &&  !c24b &&  !c24d &&  !c24e) || \n"
            + "  (c24d &&  !c24a &&  !c24b &&  !c24c &&  !c24e) || \n"
            + "  (c24e &&  !c24a &&  !c24b &&  !c24c &&  !c24d)) && \n"
            + " ((c31a &&  !c31b &&  !c31c &&  !c31d &&  !c31e) || \n"
            + "  (c31b &&  !c31a &&  !c31c &&  !c31d &&  !c31e) || \n"
            + "  (c31c &&  !c31a &&  !c31b &&  !c31d &&  !c31e) || \n"
            + "  (c31d &&  !c31a &&  !c31b &&  !c31c &&  !c31e) || \n"
            + "  (c31e &&  !c31a &&  !c31b &&  !c31c &&  !c31d)) && \n"
            + " ((c32a &&  !c32b &&  !c32c &&  !c32d &&  !c32e) || \n"
            + "  (c32b &&  !c32a &&  !c32c &&  !c32d &&  !c32e) || \n"
            + "  (c32c &&  !c32a &&  !c32b &&  !c32d &&  !c32e) || \n"
            + "  (c32d &&  !c32a &&  !c32b &&  !c32c &&  !c32e) || \n"
            + "  (c32e &&  !c32a &&  !c32b &&  !c32c &&  !c32d)) && \n"
            + " ((c33a &&  !c33b &&  !c33c &&  !c33d &&  !c33e) || \n"
            + "  (c33b &&  !c33a &&  !c33c &&  !c33d &&  !c33e) || \n"
            + "  (c33c &&  !c33a &&  !c33b &&  !c33d &&  !c33e) || \n"
            + "  (c33d &&  !c33a &&  !c33b &&  !c33c &&  !c33e) || \n"
            + "  (c33e &&  !c33a &&  !c33b &&  !c33c &&  !c33d)) && \n"
            + " ((c34a &&  !c34b &&  !c34c &&  !c34d &&  !c34e) || \n"
            + "  (c34b &&  !c34a &&  !c34c &&  !c34d &&  !c34e) || \n"
            + "  (c34c &&  !c34a &&  !c34b &&  !c34d &&  !c34e) || \n"
            + "  (c34d &&  !c34a &&  !c34b &&  !c34c &&  !c34e) || \n"
            + "  (c34e &&  !c34a &&  !c34b &&  !c34c &&  !c34d)) && \n"
            + " ((c35a &&  !c35b &&  !c35c &&  !c35d &&  !c35e) || \n"
            + "  (c35b &&  !c35a &&  !c35c &&  !c35d &&  !c35e) || \n"
            + "  (c35c &&  !c35a &&  !c35b &&  !c35d &&  !c35e) || \n"
            + "  (c35d &&  !c35a &&  !c35b &&  !c35c &&  !c35e) || \n"
            + "  (c35e &&  !c35a &&  !c35b &&  !c35c &&  !c35d)) && \n"
            + " ((c41a &&  !c41b &&  !c41c &&  !c41d &&  !c41e) || \n"
            + "  (c41b &&  !c41a &&  !c41c &&  !c41d &&  !c41e) || \n"
            + "  (c41c &&  !c41a &&  !c41b &&  !c41d &&  !c41e) || \n"
            + "  (c41d &&  !c41a &&  !c41b &&  !c41c &&  !c41e) || \n"
            + "  (c41e &&  !c41a &&  !c41b &&  !c41c &&  !c41d)) && \n"
            + " ((c43a &&  !c43b &&  !c43c &&  !c43d &&  !c43e) || \n"
            + "  (c43b &&  !c43a &&  !c43c &&  !c43d &&  !c43e) || \n"
            + "  (c43c &&  !c43a &&  !c43b &&  !c43d &&  !c43e) || \n"
            + "  (c43d &&  !c43a &&  !c43b &&  !c43c &&  !c43e) || \n"
            + "  (c43e &&  !c43a &&  !c43b &&  !c43c &&  !c43d)) && \n"
            + " ((c45a &&  !c45b &&  !c45c &&  !c45d &&  !c45e) || \n"
            + "  (c45b &&  !c45a &&  !c45c &&  !c45d &&  !c45e) || \n"
            + "  (c45c &&  !c45a &&  !c45b &&  !c45d &&  !c45e) || \n"
            + "  (c45d &&  !c45a &&  !c45b &&  !c45c &&  !c45e) || \n"
            + "  (c45e &&  !c45a &&  !c45b &&  !c45c &&  !c45d)) && \n"
            + " ((c51a &&  !c51b &&  !c51c &&  !c51d &&  !c51e) || \n"
            + "  (c51b &&  !c51a &&  !c51c &&  !c51d &&  !c51e) || \n"
            + "  (c51c &&  !c51a &&  !c51b &&  !c51d &&  !c51e) || \n"
            + "  (c51d &&  !c51a &&  !c51b &&  !c51c &&  !c51e) || \n"
            + "  (c51e &&  !c51a &&  !c51b &&  !c51c &&  !c51d)) && \n"
            + " ((c54a &&  !c54b &&  !c54c &&  !c54d &&  !c54e) || \n"
            + "  (c54b &&  !c54a &&  !c54c &&  !c54d &&  !c54e) || \n"
            + "  (c54c &&  !c54a &&  !c54b &&  !c54d &&  !c54e) || \n"
            + "  (c54d &&  !c54a &&  !c54b &&  !c54c &&  !c54e) || \n"
            + "  (c54e &&  !c54a &&  !c54b &&  !c54c &&  !c54d)) && \n"
            + " Implies(b11D, c21a &&  !c21b &&  !c21c &&  !c21d &&  !c21e) && \n"
            + " Implies(b12D, (Equivalent(c12a, c22a)) && (Equivalent(c12b, c22b)) && \n"
            + "   (Equivalent(c12c, c22c)) && (Equivalent(c12d, c22d)) && \n"
            + "   (Equivalent(c12e, c22e))) &&  !b13D && \n"
            + " Implies(b14D, (Equivalent(c14a, c24a)) && (Equivalent(c14b, c24b)) && \n"
            + "   (Equivalent(c14c, c24c)) && (Equivalent(c14d, c24d)) && \n"
            + "   (Equivalent(c14e, c24e))) &&  !b15D && \n"
            + " Implies(b21D, (Equivalent(c21a, c31a)) && (Equivalent(c21b, c31b)) && \n"
            + "   (Equivalent(c21c, c31c)) && (Equivalent(c21d, c31d)) && \n"
            + "   (Equivalent(c21e, c31e))) && Implies(b22D, (Equivalent(c22a, c32a)) && \n"
            + "   (Equivalent(c22b, c32b)) && (Equivalent(c22c, c32c)) && \n"
            + "   (Equivalent(c22d, c32d)) && (Equivalent(c22e, c32e))) && \n"
            + " Implies(b23D,  !c33a &&  !c33b && c33c &&  !c33d &&  !c33e) && \n"
            + " Implies(b24D, (Equivalent(c24a, c34a)) && (Equivalent(c24b, c34b)) && \n"
            + "   (Equivalent(c24c, c34c)) && (Equivalent(c24d, c34d)) && \n"
            + "   (Equivalent(c24e, c34e))) && Implies(b25D,  !c35a &&  !c35b &&  !c35c && \n"
            + "    !c35d && c35e) && Implies(b31D, (Equivalent(c31a, c41a)) && \n"
            + "   (Equivalent(c31b, c41b)) && (Equivalent(c31c, c41c)) && \n"
            + "   (Equivalent(c31d, c41d)) && (Equivalent(c31e, c41e))) && \n"
            + " Implies(b32D,  !c32a && c32b &&  !c32c &&  !c32d &&  !c32e) && \n"
            + " Implies(b33D, (Equivalent(c33a, c43a)) && (Equivalent(c33b, c43b)) && \n"
            + "   (Equivalent(c33c, c43c)) && (Equivalent(c33d, c43d)) && \n"
            + "   (Equivalent(c33e, c43e))) && Implies(b34D,  !c34a &&  !c34b &&  !c34c && \n"
            + "   c34d &&  !c34e) && Implies(b35D, (Equivalent(c35a, c45a)) && \n"
            + "   (Equivalent(c35b, c45b)) && (Equivalent(c35c, c45c)) && \n"
            + "   (Equivalent(c35d, c45d)) && (Equivalent(c35e, c45e))) && \n"
            + " Implies(b41D, (Equivalent(c41a, c51a)) && (Equivalent(c41b, c51b)) && \n"
            + "   (Equivalent(c41c, c51c)) && (Equivalent(c41d, c51d)) && \n"
            + "   (Equivalent(c41e, c51e))) &&  !b42D && \n"
            + " Implies(b43D,  !c43a &&  !c43b && c43c &&  !c43d &&  !c43e) && \n"
            + " Implies(b44D,  !c54a &&  !c54b &&  !c54c && c54d &&  !c54e) && \n"
            + " Implies(b45D,  !c45a &&  !c45b &&  !c45c &&  !c45d && c45e) && \n"
            + " Implies(b11R, c12a &&  !c12b &&  !c12c &&  !c12d &&  !c12e) && \n"
            + " Implies(b12R,  !c12a && c12b &&  !c12c &&  !c12d &&  !c12e) && \n"
            + " Implies(b13R,  !c14a && c14b &&  !c14c &&  !c14d &&  !c14e) && \n"
            + " Implies(b14R,  !c14a &&  !c14b &&  !c14c && c14d &&  !c14e) && \n"
            + " Implies(b21R, (Equivalent(c21a, c22a)) && (Equivalent(c21b, c22b)) && \n"
            + "   (Equivalent(c21c, c22c)) && (Equivalent(c21d, c22d)) && \n"
            + "   (Equivalent(c21e, c22e))) && Implies(b22R,  !c22a &&  !c22b && c22c && \n"
            + "    !c22d &&  !c22e) && Implies(b23R,  !c24a &&  !c24b && c24c &&  !c24d && \n"
            + "    !c24e) && Implies(b24R,  !c24a &&  !c24b &&  !c24c &&  !c24d && c24e) && \n"
            + " Implies(b31R, (Equivalent(c31a, c32a)) && (Equivalent(c31b, c32b)) && \n"
            + "   (Equivalent(c31c, c32c)) && (Equivalent(c31d, c32d)) && \n"
            + "   (Equivalent(c31e, c32e))) && Implies(b32R, (Equivalent(c32a, c33a)) && \n"
            + "   (Equivalent(c32b, c33b)) && (Equivalent(c32c, c33c)) && \n"
            + "   (Equivalent(c32d, c33d)) && (Equivalent(c32e, c33e))) && \n"
            + " Implies(b33R, (Equivalent(c33a, c34a)) && (Equivalent(c33b, c34b)) && \n"
            + "   (Equivalent(c33c, c34c)) && (Equivalent(c33d, c34d)) && \n"
            + "   (Equivalent(c33e, c34e))) && Implies(b34R, (Equivalent(c34a, c35a)) && \n"
            + "   (Equivalent(c34b, c35b)) && (Equivalent(c34c, c35c)) && \n"
            + "   (Equivalent(c34d, c35d)) && (Equivalent(c34e, c35e))) && \n"
            + " Implies(b41R,  !c41a && c41b &&  !c41c &&  !c41d &&  !c41e) && \n"
            + " Implies(b42R,  !c43a && c43b &&  !c43c &&  !c43d &&  !c43e) && \n"
            + " Implies(b43R,  !c43a &&  !c43b &&  !c43c && c43d &&  !c43e) && \n"
            + " Implies(b44R,  !c45a &&  !c45b &&  !c45c && c45d &&  !c45e) && \n"
            + " Implies(b51R, c51a &&  !c51b &&  !c51c &&  !c51d &&  !c51e) &&  !b52R && \n"
            + " Implies(b53R,  !c54a &&  !c54b && c54c &&  !c54d &&  !c54e) && \n"
            + " Implies(b54R,  !c54a &&  !c54b &&  !c54c &&  !c54d && c54e))", //
        "False");
  }

  public void testXnor() {
    // check("Xnor(False, True)", //
    // "False");

    check("Xnor(False, True,a)", //
        "a");
    check("Xnor(False, False,a)", //
        "!a");
    check("Xnor(False, False,a,b)", //
        "Xnor(a,b)");
    check("Xnor(c,a,b)", //
        "Xnor(a,b,c)");
  }

  public void testXor() {
    check("Xor(False, True)", //
        "True");
    check("Xor(True, True)", //
        "False");
    check("Xor(a, False, b)", //
        "Xor(a,b)");
    check("Xor(a, b)", //
        "Xor(a,b)");

    check("Xor()", //
        "False");
    check("Xor(False)", //
        "False");
    check("Xor(True)", //
        "True");
    check("Xor(f(x))", //
        "f(x)");
    check("Xor(a,a)", //
        "False");
    check("Xor(a,a,a,b)", //
        "Xor(a,b)");
    check("Xor(a,c,a,b)", //
        "Xor(b,c)");
    check("Xor(True, False, False)", //
        "True");
    check("Xor(True, True, True)", //
        "True");
    check("Xor(True, True, True, True)", //
        "False");
    check("Xor(False, False, False, False)", //
        "False");
    check("Xor(True, False, True)", //
        "False");
  }
}
