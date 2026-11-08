package org.matheclipse.internal.system;

import org.junit.jupiter.api.Test;

/** The functions of the <code>Internal`</code> context, in Symja's relaxed syntax. */
public class InternalFunctionsTest extends AbstractTestCase {

  @Test
  public void testBag() {
    check("b = Internal`Bag(); {b, AtomQ(b), Head(b), Internal`BagLength(b)}", //
        "{Internal`Bag[<0>],True,Internal`Bag,0}");
    check("b = Internal`Bag({x, y}); Internal`StuffBag(b, z); Internal`BagPart(b, All)", //
        "{x,y,z}");
    check("Internal`BagPart(Internal`Bag(), 1)", //
        "Internal`BagPart(Internal`Bag(),1)");
    // a list argument other than a List leaves Bag unevaluated
    check("Internal`Bag(7)", //
        "Internal`Bag(7)");
  }

  @Test
  public void testBagIsShared() {
    // a bag is changed in place: both variables see the new element
    check("b1 = Internal`Bag({1}); b2 = b1; Internal`StuffBag(b2, 2); Internal`BagPart(b1, All)", //
        "{1,2}");
    // two bags with the same elements are different expressions
    check("Internal`Bag({1}) === Internal`Bag({1})", //
        "False");
  }

  @Test
  public void testStuffBagLevel() {
    check("b = Internal`Bag(); Internal`StuffBag(b, {{1, 2}, {3}}, 1); Internal`BagLength(b)", //
        "2");
    check("b = Internal`Bag(); Internal`StuffBag(b, {{1, 2}, {3}}, 2); Internal`BagPart(b, All)", //
        "{1,2,3}");
    check("b = Internal`Bag(); Internal`StuffBag(b, {4, 5}, 0); Internal`BagPart(b, All)", //
        "{{4,5}}");
  }

  @Test
  public void testBagPart() {
    check("b = Internal`Bag({p, q, r, s}); {Internal`BagPart(b, 2), Internal`BagPart(b, -1), "
        + "Internal`BagPart(b, {1, 3}), Internal`BagPart(b, 2 ;; 3)}", //
        "{q,s,{p,r},{q,r}}");
    check("b = Internal`Bag({p, q, r}); Internal`BagPart(b, 2) = 42; Internal`BagPart(b, All)", //
        "{p,42,r}");
    check("b = Internal`Bag({p, q, r}); Internal`BagPart(b, -1) = t; Internal`BagPart(b, 3)", //
        "t");
  }

  @Test
  public void testInheritedBlock() {
    check("f(1) = a; Internal`InheritedBlock({f}, f(2) = b; {f(1), f(2)})", //
        "{a,b}");
    // the definition made inside is dropped
    check("{f(1), f(2)}", //
        "{a,f(2)}");
    check("x = 1; {Internal`InheritedBlock({x}, x = x + 10; x), x}", //
        "{11,1}");
    check("Internal`InheritedBlock({x = 5}, x)", //
        "5");
    check("SetAttributes(g, Listable); "
        + "{Internal`InheritedBlock({g}, ClearAttributes(g, Listable); Attributes(g)), Attributes(g)}", //
        "{{},{Listable}}");
  }

  @Test
  public void testLocalizedBlock() {
    check("y = 1; Internal`LocalizedBlock({y}, y = 2; y + 1)", //
        "3");
    check("y", //
        "1");
    check("Internal`LocalizedBlock({Subscript(w, 1)}, Subscript(w, 1) = 5; Subscript(w, 1)^2)", //
        "25");
    check("Subscript(w, 1)", //
        "Subscript(w,1)");
    // the variables of a Manipulate: a symbol and an indexed name, set from the arguments
    check("Internal`LocalizedBlock({u, Subscript(w, 2)}, {u, Subscript(w, 2)} = {3, 4}; "
        + "u*Subscript(w, 2))", //
        "12");
    check("Internal`LocalizedBlock({Subscript(w, 3) = 7}, Subscript(w, 3) + 1)", //
        "8");
    // an indexed name which is left in the result is the original one again
    check("Internal`LocalizedBlock({Subscript(w, 4)}, {Subscript(w, 4)})", //
        "{Subscript(w,4)}");
  }

  @Test
  public void testWithLocalSettings() {
    check("Internal`WithLocalSettings(n = 0, n + 7, n = 1)", //
        "7");
    check("n", //
        "1");
    check("done = False; {CheckAbort(Internal`WithLocalSettings(Null, Abort(); 6, done = True), "
        + "aborted), done}", //
        "{aborted,True}");
    check("done = False; {Catch(Internal`WithLocalSettings(Null, Throw(9), done = True)), done}", //
        "{9,True}");
  }

  @Test
  public void testConditionalValueBody() {
    check("sq(x_) := Internal`ConditionalValueBody(sq, {IntegerQ(x), x > 0}, "
        + "{{\"int\", x}, {\"pos\", x}}, x^2); {sq(4), sq(2.5), sq(-3)}", //
        "{16,$Failed,$Failed}");
  }

  @Test
  public void testPolynomialPredicates() {
    check("Map(Internal`PolynomialFunctionQ(#, t)&, {t^3 - 2*t, Cos(t), a*t + b})", //
        "{True,False,True}");
    check("Internal`PolynomialFunctionQ(s^2*t + 1, {s, t})", //
        "True");
    check("Map(Internal`LinearQ(#, t)&, {3*t - 2, t^2 + t, 4, b*t})", //
        "{True,False,False,True}");
    check("Map(Internal`EPolyQ(#, t)&, {(t - 2)^3, t^3 - 6*t^2 + 12*t - 8, c*(a + b)*t^2, "
        + "Exp(t)})", //
        "{False,True,True,False}");
  }

  @Test
  public void testRationalFunctionQ() {
    check("Map(Internal`RationalFunctionQ(#, s)&, {(s^3 - 1)/(s + 2), Log(q)*s^2, s^(1/3), "
        + "s^1.5, 3^s, g(s), {s}, s == 2, 11})", //
        "{True,True,False,False,False,False,False,False,True}");
    check("Internal`RationalFunctionQ(p/(p + q^2), {p, q})", //
        "True");
    check("Internal`RationalFunctionQ(p)", //
        "Internal`RationalFunctionQ(p)");
  }

  @Test
  public void testExceptionFreeQ() {
    check("Map(Internal`ExceptionFreeQ, {2/0, 0/0, -Infinity, {3, ComplexInfinity}, a + b, "
        + "$Failed})", //
        "{False,False,False,False,True,True}");
  }

  @Test
  public void testDependsOnQ() {
    check("Map(Internal`DependsOnQ(#, t)&, {Cos(t) + s, h(t), s, t(2)})", //
        "{True,True,False,False}");
  }

  @Test
  public void testPatternPredicates() {
    check("Map(Internal`PatternFreeQ, {t_, a*b, Hold(t__), _Real, g(t:_ | s), {1, 2}})", //
        "{False,True,False,False,False,True}");
    check("{Internal`PatternPresentQ(h(__)), Internal`PatternPresentQ(h(1))}", //
        "{True,False}");
  }

  @Test
  public void testLiterallyOccurringQ() {
    check("{Internal`LiterallyOccurringQ(g(p, q), q), Internal`LiterallyOccurringQ(g(p), r)}", //
        "{True,False}");
    check("{Internal`LiterallyAbsentQ(g(p, q), r), Internal`LiterallyAbsentQ(g(p, q), p)}", //
        "{True,False}");
  }

  @Test
  public void testWouldBeNumericQ() {
    check("Map(Internal`WouldBeNumericQ(#, {s, t})&, {Cos(s) + t^3, h(s), s + r, E*t})", //
        "{True,False,False,True}");
  }

  @Test
  public void testNumberPredicates() {
    check("Map(Internal`FundamentalDiscriminantQ, {1, 0, -3, -4, 13, 8, 24, -7, 25, 16, -8, 44})", //
        "{False,False,True,True,True,True,True,True,False,False,True,True}");
    check("Map(Internal`GregorianLeapYearQ, {1700, 1600, 2028, 2027, 2028.0})", //
        "{False,True,True,False,False}");
    check("Map(Internal`RealValuedNumberQ, {3, -1.25, 2*I, E})", //
        "{True,True,False,False}");
    check("Map(Internal`TestIntegerQ, {5, 5.0, s})", //
        "{{5,True},{5.0,False},{s,False}}");
  }

  @Test
  public void testRadicalMemberQ() {
    check("{Internal`RadicalMemberQ(s, {s^3}), Internal`RadicalMemberQ(t, {s^3}), "
        + "Internal`RadicalMemberQ(s - t, {s^2, t^4})}", //
        "{True,False,True}");
  }

  @Test
  public void testListFunctions() {
    check("Internal`PartitionRagged(Range(8), {3, 1, 4})", //
        "{{1,2,3},{4},{5,6,7,8}}");
    // the lengths have to add up to the length of the list
    check("Internal`PartitionRagged({a, b, c}, {1, 1})", //
        "Internal`PartitionRagged({a,b,c},{1,1})");
    check("Internal`Deflatten(Range(8), {2, 2, 2})", //
        "{{{1,2},{3,4}},{{5,6},{7,8}}}");
    check("{Internal`JoinOrFail({a}, {b, c}), Internal`JoinOrFail(a, {b})}", //
        "{{a,b,c},$Failed}");
    check("Internal`ListMin({{2, 3}, {3, 2}, {2, 3}, {4, 4}, {1, 6}})", //
        "{{2,3},{3,2},{1,6}}");
    check("{Internal`DeleteTrailingZeros({0, 3, 0, 4, 0, 0.0}), "
        + "Internal`DeleteTrailingZeros({0})}", //
        "{{0,3,0,4},{}}");
  }

  @Test
  public void testGetIteratorLength() {
    // the iterator variable is held, so a value of it doesn't matter
    check("k = 3; {Internal`GetIteratorLength({k, 4, 11}), "
        + "Internal`GetIteratorLength({k, 0, 2, 0.25}), Internal`GetIteratorLength({k, {p, q}}), "
        + "Internal`GetIteratorLength({9}), Internal`GetIteratorLength({k, 6, 2}), "
        + "Internal`GetIteratorLength({k, 12, 2, -4})}", //
        "{8,9,2,9,0,3}");
    check("Internal`GetIteratorLength({k, 1/3, 5/2, 1/2}) == Length(Table(k, {k, 1/3, 5/2, 1/2}))", //
        "True");
  }

  @Test
  public void testToEquivalenceClass() {
    check("Internal`ToEquivalenceClass({2.03, 1.97, 0.51, s, 7}, 0.1)", //
        "{2.0,2.0,0.5,s,7}");
  }

  @Test
  public void testMakePolynomial() {
    check("p = Internal`MakePolynomial(Cos(t)^3 + Log(s)*t); "
        + "{PolynomialQ(p[[1]], p[[2, All, 1]]), p[[1]] /. p[[2]]}", //
        "{True,Cos(t)^3+t*Log(s)}");
  }

  @Test
  public void testSquareFunctions() {
    check("Internal`Square({3, s, 0.5})", //
        "{9,s^2,0.25}");
    check("Internal`AbsSquare({1 - 2*I, s, -3})", //
        "{5,Abs(s)^2,9}");
    check("Attributes(Internal`AbsSquare)", //
        "{Listable,NumericFunction}");
  }

  @Test
  public void testStringToDouble() {
    check("Map(Internal`StringToDouble, {\"-0.75\", \"3e2\", \"1.5*^-2\", \"x1\"})", //
        "{-0.75,300.0,0.015,$Failed}");
  }

  @Test
  public void testPerfectPower() {
    check("Map(Internal`PerfectPower, {81, 10, 1, 0, -27, -16, 6^12})", //
        "{{3,4},{10,1},{1,1},{0,1},{-3,3},{-16,1},{6,12}}");
  }

  @Test
  public void testCompareNumeric() {
    check("{Internal`CompareNumeric(0, 2, 5), Internal`CompareNumeric(0, 7/2, 7/2), "
        + "Internal`CompareNumeric(0, 4.0, 3.0)}", //
        "{-1,0,1}");
    // a difference in the last digits is ignored with a tolerance of a few digits only
    check("{Internal`CompareNumeric(6, 2.0, 2.0 + 10^-12), "
        + "Internal`CompareNumeric(0, 2.0, 2.0 + 10^-12)}", //
        "{0,-1}");
  }

  @Test
  public void testBinomialPrimePowerDecomposition() {
    check("Internal`BinomialPrimePowerDecomposition(12, 5)", //
        "{{2,3},{3,2},{11,1}}");
  }

  @Test
  public void testDiracGammaMatrix() {
    // the Clifford algebra {gamma^m, gamma^n} = 2 g^(m n) in both bases
    check("Table(With({g = Table(Internal`DiracGammaMatrix(k, \"Basis\" -> basis), {k, 0, 3})}, "
        + "And @@ Flatten(Table(g[[m]].g[[n]] + g[[n]].g[[m]] == "
        + "2*DiagonalMatrix({1, -1, -1, -1})[[m, n]]*IdentityMatrix(4), {m, 4}, {n, 4}))), "
        + "{basis, {\"Dirac\", \"Chiral\"}})", //
        "{True,True}");
    check("With({g = Internal`DiracGammaMatrix(2, \"Metric\" -> {-1, 1, 1, 1})}, "
        + "g.g == IdentityMatrix(4))", //
        "True");
    check("Internal`DiracGammaMatrix(5) == I*Dot @@ Table(Internal`DiracGammaMatrix(k), {k, 0, 3})", //
        "True");
  }

  @Test
  public void testDirectoryByteCount() throws java.io.IOException {
    java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("dbc");
    java.nio.file.Path locked = dir.resolve("locked");
    boolean filesystem = org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED;
    try {
      java.nio.file.Files.writeString(dir.resolve("a.txt"), "12345");
      java.nio.file.Files.createDirectories(dir.resolve("sub"));
      java.nio.file.Files.writeString(dir.resolve("sub").resolve("b.txt"), "123");
      // a subdirectory which may not be read is skipped, not an error
      java.nio.file.Files.createDirectories(locked);
      java.nio.file.Files.writeString(locked.resolve("c.txt"), "1234567");
      locked.toFile().setReadable(false);
      org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED = true;
      String path = dir.toString().replace("\\", "\\\\");
      String expected = locked.toFile().canRead() ? "15" : "8";
      check("Internal`DirectoryByteCount(\"" + path + "\")", //
          expected);
    } finally {
      org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED = filesystem;
      locked.toFile().setReadable(true);
      try (java.util.stream.Stream<java.nio.file.Path> paths = java.nio.file.Files.walk(dir)) {
        paths.sorted(java.util.Comparator.reverseOrder()).map(java.nio.file.Path::toFile)
            .forEach(java.io.File::delete);
      }
    }
  }

  @Test
  public void testSystemAliases() {
    check("Internal`RiccatiSolve === RiccatiSolve", //
        "True");
  }

  @Test
  public void testContext() {
    check("{Context(Internal`Bag), Internal`Bag, FullForm(Internal`Square(s))}", //
        "{Internal`,Internal`Bag,Power(s, 2)}");
    // a Global` symbol of the same name doesn't take the place of the Internal` function
    check("Bag = 5; Internal`BagLength(Internal`Bag({1, 2}))", //
        "2");
  }
}
