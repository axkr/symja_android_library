package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for StringPartition, CharacterName, DatePattern, the template objects and the other string functions. */
public class StringUtilityFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testStringPartition() {
    check("StringPartition(\"abcdefg\", 3)", //
        "{abc,def}");
    check("StringPartition(\"abcdefg\", 3, 2)", //
        "{abc,cde,efg}");
    check("StringPartition(\"abc\", 0)", //
        "StringPartition(abc,0)");
  }

  @Test
  public void testCharacterName() {
    check("CharacterName(\"q\")", //
        "LatinSmallLetterQ");
    check("CharacterName(\"B7\")", //
        "{LatinCapitalLetterB,DigitSeven}");
    check("CharacterName(\" \")", //
        "RawSpace");
    check("CharacterName(945)", //
        "Alpha");
    check("CharacterName(\"\\[Beta]\")", //
        "Beta");
  }

  @Test
  public void testCharacterNormalize() {
    check("CharacterNormalize(\"\\[CapitalAAcute]\", \"NFD\") // ToCharacterCode", //
        "{65,769}");
    check("CharacterNormalize({\"ﬁx\", \"x²\"}, \"NFKC\")", //
        "{fix,x2}");
    check("CharacterNormalize(\"STRASSE\", \"NFKCCasefold\")", //
        "strasse");
    check("CharacterNormalize(\"abc\", \"NFX\")", //
        "CharacterNormalize(abc,NFX)");
  }

  @Test
  public void testInsertLinebreaks() {
    check("InsertLinebreaks(\"one two three four five\", 9)", //
        "one two\nthree\nfour five");
    check("InsertLinebreaks(\"abcdefghij kl\", 4)", //
        "abcd\nefgh\nij\nkl");
  }

  @Test
  public void testSnippet() {
    check("Snippet(\"first line\\nsecond line\\nthird line\")", //
        "first line");
    check("Snippet(\"first line\\nsecond line\\nthird line\", 2)", //
        "first line\nsecond line");
    check("Snippet(\"first line\\nsecond line\\nthird line\", -1)", //
        "third line");
    check("Snippet(\"first line\\nsecond line\\nthird line\", 2 ;; 3)", //
        "second line\nthird line");
  }

  @Test
  public void testDamerauLevenshteinDistance() {
    check("DamerauLevenshteinDistance(\"ca\", \"abc\")", //
        "2");
    check("DamerauLevenshteinDistance(\"kitten\", \"sitting\")", //
        "3");
    check("DamerauLevenshteinDistance(\"ABcd\", \"abdc\", IgnoreCase -> True)", //
        "1");
    check("DamerauLevenshteinDistance({1, 2, 3}, {2, 1, 3})", //
        "1");
  }

  @Test
  public void testLongestCommonSequencePositions() {
    check("LongestCommonSequencePositions(\"AXBYC\", \"ABC\")", //
        "{{{1,1},{3,3},{5,5}},{{1,3}}}");
    check("LongestCommonSequencePositions({1, 5, 2, 3}, {1, 2, 3})", //
        "{{{1,1},{3,4}},{{1,3}}}");
    check("LongestCommonSequencePositions(\"abc\", \"xyz\")", //
        "{}");
  }

  @Test
  public void testPatternCharacterClasses() {
    check("StringMatchQ(\"a7F\", HexadecimalCharacter ..)", //
        "True");
    check("StringMatchQ(\"12:30\", DatePattern({\"Hour\", \"Minute\"}, \":\"))", //
        "True");
  }

  @Test
  public void testDatePatternAndPunctuation() {
    check("StringCases(\"hi, you! ok?\", PunctuationCharacter)", //
        "{,,!,?}");
    check("StringCases(\"due 2026-11-27 or 1999/01/05\", DatePattern({\"Year\", \"Month\", \"Day\"}))", //
        "{2026-11-27,1999/01/05}");
    check("StringCases(\"on Friday, 3 March\", DatePattern({\"Day\", \" \", \"MonthName\"}))", //
        "{3 March}");
  }

  @Test
  public void testStringSkeleton() {
    check("StringSkeleton(12)", //
        "<<12>>");
  }

  @Test
  public void testTemplateApply() {
    check("TemplateApply(TemplateObject({TemplateSlot(1), TemplateSlot(1)^2}), {3})", //
        "{3,9}");
    check("TemplateApply(TemplateObject(f(TemplateExpression(TemplateSlot(1) + TemplateSlot(2)))), {2, 5})", //
        "f(7)");
    check("TemplateApply(TemplateObject({TemplateSequence(TemplateSlot(1)^2, {1, 2, 3})}), {})", //
        "{1,4,9}");
    check("TemplateApply(TemplateObject({TemplateSequence(g(TemplateSlot(1)), TemplateSlot(\"v\"), sep)}), <|\"v\" -> {a, b}|>)", //
        "{g(a),sep,g(b)}");
    check("TemplateApply(XMLTemplate(\"<p><wolfram:slot id=\\\"x\\\"/></p>\"), <|\"x\" -> 42|>)", //
        "<p>42</p>");
  }

  @Test
  public void testTemplateObject() {
    check("TemplateObject({TemplateSlot(\"a\") + 1})[<|\"a\" -> 4|>]", //
        "{5}");
  }

  @Test
  public void testXMLTemplate() {
    check("XMLTemplate(\"<b><wolfram:slot>n</wolfram:slot></b>\")[<|\"n\" -> \"bold\"|>]", //
        "<b>bold</b>");
  }

  @Test
  public void testDelimitedSequence() {
    check("DelimitedSequence(\"Integer\")", //
        "DelimitedSequence(Integer)");
  }
}
