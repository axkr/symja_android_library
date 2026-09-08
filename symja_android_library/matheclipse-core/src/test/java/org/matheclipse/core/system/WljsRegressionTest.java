package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Wolfram Language behaviour that packages rely on and Symja did not have.
 *
 * <p>
 * Every one of these was found by loading the packages of a real application - the WLJS Notebook -
 * and is here so that it stays fixed. They have nothing else in common, which is why they are
 * together rather than spread through the suites of the functions they belong to.
 */
public class WljsRegressionTest extends ExprEvaluatorTestCase {

  @Test
  public void testTagIsFoundInsideAPattern() {
    // UObject /: MakeBoxes[object : UObject[…], form : StandardForm | TraditionalForm] := …
    // The tag stands under a Pattern, and the rule used to be refused as "tag not found".
    check("UObject /: MakeBoxes(object : UObject(symbol_Symbol), form : StandardForm | TraditionalForm) := \"boxed\"", //
        "");
    check("MakeBoxes(UObject(x), StandardForm)", //
        "boxed");
    // through a test and a condition as well
    check("Sock /: listen(socket : Sock(id_Integer) /; True, handler_) := \"listening\"", //
        "");
    check("listen(Sock(3), f)", //
        "listening");
    // ...but a tag that is really not there is still an error
    check("q /: r(s(x_)) := 1", //
        "$Failed");
  }

  @Test
  public void testOffAndOnSwitchOneMessage() {
    check("Part({1, 2}, 5)", //
        "{1,2}[[5]]", //
        "Part: Part 5 of {1,2} does not exist.");
    check("Off(Part::partw)", //
        "");
    // the message is gone, the value is the same
    check("Part({1, 2}, 5)", //
        "{1,2}[[5]]");
    check("On(Part::partw)", //
        "");
  }

  @Test
  public void testRejoiningAnAbsolutePathKeepsItsRoot() {
    // FileNameSplit["/a/b"] is {"", "a", "b"}: the empty first segment is the root, and dropping
    // it turned every absolute path into a relative one
    check("FileNameJoin(FileNameSplit(\"/Users/someone/x.wl\"))", //
        "/Users/someone/x.wl");
    check("FileNameJoin({\"\", \"Users\", \"someone\"})", //
        "/Users/someone");
  }

  @Test
  public void testAnEmptyListJoinsWithAnAssociation() {
    // a package joins in what it found, and finding nothing is not an incompatibility
    check("Join(<|\"a\" -> 1|>, {})", //
        "<|a->1|>");
    check("Join({}, <|\"a\" -> 1|>)", //
        "<|a->1|>");
    check("Join(<||>, {})", //
        "<||>");
  }

  @Test
  public void testReturnCanNameTheConstructItLeaves() {
    // Return[Null, Module] is written by packages
    check("f(x_) := Module({}, Return(x + 1, Module); 99)", //
        "");
    check("f(1)", //
        "2");
  }

  @Test
  public void testTheDynamicLibraryExtensionIsKnown() {
    // a package that loads a shared library builds the file name from this
    check("MemberQ({\"so\", \"dylib\", \"dll\"}, Internal`DynamicLibraryExtension())", //
        "True");
  }

  @Test
  public void testNeedsOfASystemContextIsSilent() {
    // the kernel provides these, so there is nothing to read and nothing to complain about
    check("Needs(\"Parallel`Developer`\")", //
        "");
    check("Needs(\"Developer`\")", //
        "");
  }

  @Test
  public void testLocalisingVariablesAroundAnAssociation() {
    // the module-variable visitor reads an element and writes it back, and an association answers
    // an element with its value - which it then refused to take
    check("Module({a = <|\"x\" -> 1|>}, <|\"k\" -> a[\"x\"], \"e\" -> True|>)", //
        "<|k->1,e->True|>");
    check("f(assoc_) := Module({b = assoc}, {b[\"n\"] -> Join(<|\"key\" -> b[\"n\"]|>, b)})", //
        "");
    check("f(<|\"n\" -> \"v\"|>)", //
        "{v-><|key->v,n->v|>}");
  }
}
