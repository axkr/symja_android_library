package org.matheclipse.core.system;

/**
 * Tests for Sequence... functions
 */
public class SequenceTestCase extends ExprEvaluatorTestCase {

  public SequenceTestCase(String name) {
    super(name);
  }

  public void testSequenceCases() {
    check("SequenceCases({a, b, x, x, a, c}, {a, _})", //
        "{{a,b},{a,c}}");

    check("SequenceCases(Range(1000), {_?PrimeQ, _, _?PrimeQ})", //
        "{{3,4,5},{11,12,13},{17,18,19},{29,30,31},{41,42,43},{59,60,61},{71,72,73},{101,\n"//
            + "102,103},{107,108,109},{137,138,139},{149,150,151},{179,180,181},{191,192,193},{\n"//
            + "197,198,199},{227,228,229},{239,240,241},{269,270,271},{281,282,283},{311,312,\n"//
            + "313},{347,348,349},{419,420,421},{431,432,433},{461,462,463},{521,522,523},{569,\n"//
            + "570,571},{599,600,601},{617,618,619},{641,642,643},{659,660,661},{809,810,811},{\n"//
            + "821,822,823},{827,828,829},{857,858,859},{881,882,883}}");

    check("SequenceCases(Range(10), list_ /; Length(list)==6, Overlaps -> True)", //
        "{{1,2,3,4,5,6},{2,3,4,5,6,7},{3,4,5,6,7,8},{4,5,6,7,8,9},{5,6,7,8,9,10}}");

    check("SequenceCases({a, b, c, d}, {__}, Overlaps -> True)", //
        "{{a,b,c,d},{b,c,d},{c,d},{d}}");

    check("SequenceCases({a, b, c, d}, {__}, Overlaps -> False)", //
        "{{a,b,c,d}}");

    check("SequenceCases({a, b, c, d}, {__}, Overlaps -> All)", //
        "{{a,b,c,d},{a,b,c},{a,b},{a},{b,c,d},{b,c},{b},{c,d},{c},{d}}");
  }

  public void testSequenceCasesWithReplacement() {
    check("SequenceCases({a, b, x, x, a, c}, {a, x_} :> f(x))", //
        "{f(b),f(c)}");
    // TODO
    // check("SequenceCases({a, b, b, a, b, b, b}, {p : Repeated(b)} :> Length({p}))", //
    // "{2,3}");

  }

  public void testSequenceSplit() {

    check("SequenceSplit({x, x, a, b, y, a, c, z}, {a, _})", //
        "{{x,x},{y},{z}}");
    check("SequenceSplit({x, x, a, b, y, a, c, z}, {a, _}, 2)", //
        "{{x,x},{y,a,c,z}}");
    check("SequenceSplit({x, x, a, b, y, a, c, z}, {a, _}, 1)", //
        "{{x,x,a,b,y,a,c,z}}");


    check("SequenceSplit({1, \"a\", \"a\", 2, 3, \"b\", \"b\", 4, 5}, {\"a\", \"a\"})", //
        "{{1},{2,3,b,b,4,5}}");

    check("SequenceSplit({1, 2, 3, 4, 5, 6, 7}, {2, 3} | {6})", //
        "{{1},{4,5},{7}}");

    check("SequenceSplit({a, b, 1, 2, c, d, 3, 4, e, f}, {__Integer})", //
        "{{a,b},{c,d},{e,f}}");

    check("SequenceSplit({a, b, 1, 2, c, d, 3, 4}, {__Symbol})", //
        "{{1,2},{3,4}}");



  }

  public void testSequenceSplitWithInsertion() {
    check("SequenceSplit({x, x, a, b, y, a, c, z}, {a, e_} :> {e})", //
        "{{x,x},{b},{y},{c},{z}}");
    check("SequenceSplit({1, 2, 3, 4, 5}, {2, 3} -> {a, b})", //
        "{{1},{a,b},{4,5}}");
    check("SequenceSplit({1, 2, 3, 4, 5}, {2, 3} -> Sequence(a, b))", //
        "{{1},a,b,{4,5}}");

    check("t = \"On friday we left, and on sunday we came back.\";", //
        "");
    check("Map(StringJoin, SequenceSplit(Characters(t), {\"w\", \"e\"} -> \"they\"))", //
        "{On friday ,they, left, and on sunday ,they, came back.}");
  }

  public void testSequenceReplace() {
    // operator form
    check("SequenceReplace({a, e_} :> e)[{a, b, x, x, a, c}]", //
        "{b,x,x,c}");


    check("SequenceReplace({a, b, x, x, a, c}, {a, e_} :> e)", //
        "{b,x,x,c}");
    check("SequenceReplace({a, b, x, x, a, c}, {{a, e_} :> e, {x} -> y})", //
        "{b,y,y,c}");
    check("SequenceReplace({a, b, x, x, a, c}, {{a, e_} :> e, {x} -> y}, 2)", //
        "{b,y,x,a,c}");

    check("SequenceReplace({a, b, 1, 2, c, d, 3, 4}, {__Symbol} -> \"X\")", //
        "{X,1,2,X,3,4}");

    check("t = \"On friday we left, and on sunday we came back.\";", //
        "");
    check("StringJoin(SequenceReplace(Characters(t), {\"w\", \"e\"} -> \"they\"))", //
        "On friday they left, and on sunday they came back.");
    check("StringReplace(t, \"we\" -> \"they\")", //
        "On friday they left, and on sunday they came back.");

    check("SequenceReplace({1, a, 2, 3, a, b, c}, {a, x_Integer} :> f(x))", //
        "{1,f(2),3,a,b,c}");
  }

}
