package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the <code>ScientificForm, EngineeringForm, NumberForm, AccountingForm, PaddedForm,
 * DecimalForm</code> display wrappers.
 */
public class NumberFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testScientificForm() {
    check("ScientificForm(25000000.0)", //
        "2.5×10^7");
    // a single digit integer part needs no exponent
    check("ScientificForm(2.5)", //
        "2.5");
    check("ScientificForm(0.25)", //
        "2.5×10^-1");
    check("ScientificForm(0.000025)", //
        "2.5×10^-5");
    check("ScientificForm(-25000000.0)", //
        "-2.5×10^7");
  }

  @Test
  public void testScientificFormPrecision() {
    check("ScientificForm(1.23456789, 4)", //
        "1.235");
    check("ScientificForm(1.23456789, {6, 2})", //
        "1.23");
  }

  @Test
  public void testScientificFormExactNumbers() {
    // exact integers are not "real numbers" - they stay as they are
    check("ScientificForm(123456)", //
        "123456");
    check("ScientificForm(1/3)", //
        "1/3");
  }

  @Test
  public void testScientificFormRecursive() {
    check("ScientificForm({1.0*^7, 2.0*^-8})", //
        "{1.×10^7,2.×10^-8}");
    check("ScientificForm(x^2 + 2.5*^7)", //
        "2.5×10^7+x^2");
    check("ScientificForm(2.5*^7 + 3.0*^7*I)", //
        "2.5×10^7+I*3.×10^7");
  }

  @Test
  public void testWrapperIsNotEvaluated() {
    // the head survives evaluation and is only resolved when printing
    check("FullForm(ScientificForm(2.5))", //
        "ScientificForm(2.5`)");
    check("Head(ScientificForm(2.5))", //
        "ScientificForm");
  }

  @Test
  public void testEngineeringForm() {
    // the exponent is always a multiple of 3
    check("EngineeringForm(25000000.0)", //
        "25.×10^6");
    check("EngineeringForm(2.5)", //
        "2.5");
    check("EngineeringForm(1.0*^-7)", //
        "100.×10^-9");
    check("EngineeringForm(1.23456*^8, 3)", //
        "123.×10^6");
  }

  @Test
  public void testNumberForm() {
    // no exponent for -5 <= exponent <= 5
    check("NumberForm(1.5)", //
        "1.5");
    check("NumberForm(1.0*^12)", //
        "1.×10^12");
    check("NumberForm(1.5, {5, 3})", //
        "1.500");
  }

  @Test
  public void testAccountingForm() {
    // negative numbers are shown in parentheses and scientific notation is never used
    check("AccountingForm(2.5)", //
        "2.5");
    check("AccountingForm(-2.5)", //
        "(2.5)");
    check("AccountingForm(1.0*^12)", //
        "1000000000000.");
    check("AccountingForm(-1234567, DigitBlock -> 3)", //
        "(1,234,567)");
  }

  @Test
  public void testDecimalForm() {
    check("DecimalForm(1.0*^12)", //
        "1000000000000.");
    check("DecimalForm(1.0*^-6)", //
        "0.000001");
  }

  @Test
  public void testPaddedForm() {
    // the field holds the requested digits and one position for the sign
    check("PaddedForm(12.3, {6, 2})", //
        "   12.30");
    // the sign occupies that position, so columns line up
    check("PaddedForm(-12.3, {8, 2})", //
        "    -12.30");
    check("PaddedForm(12.3, {8, 2}, SignPadding -> True)", //
        "     12.30");
    check("PaddedForm(-12.3, {8, 2}, SignPadding -> True)", //
        "-    12.30");
    // outside ScientificNotationThreshold -> {-5, 6}
    check("PaddedForm(1.0*^12)", //
        "1.×10^12");
    check("PaddedForm(1.0*^-12)", //
        "1.×10^-12");
  }

  @Test
  public void testDigitBlock() {
    check("NumberForm(1234567, DigitBlock -> 3)", //
        "1,234,567");
    check("NumberForm(1234567, DigitBlock -> 3, NumberSeparator -> \".\")", //
        "1.234.567");
    check("NumberForm(1234567.0, DigitBlock -> 3)", //
        "1.234567×10^6");
  }

  @Test
  public void testExponentFunction() {
    check("ScientificForm(2.5, ExponentFunction -> (# &))", //
        "2.5×10^0");
    // returning Null suppresses scientific notation
    check("ScientificForm(1.0*^7, ExponentFunction -> (Null &))", //
        "10000000.");
  }

  @Test
  public void testExponentStep() {
    check("ScientificForm(1.234*^7, ExponentStep -> 3)", //
        "12.34×10^6");
    check("ExponentStep /. Options(EngineeringForm)", //
        "3");
    check("ExponentStep /. Options(ScientificForm)", //
        "1");
  }

  @Test
  public void testNumberMultiplierAndPoint() {
    check("ScientificForm(2.5*^7, NumberMultiplier -> \" \")", //
        "2.5 10^7");
    check("ScientificForm(2.5*^7, NumberPoint -> \",\")", //
        "2,5×10^7");
  }

  @Test
  public void testNumberFormatOption() {
    check("ScientificForm(2.5*^7, NumberFormat -> (#1 <> \"e\" <> #3 &))", //
        "2.5e7");
  }

  @Test
  public void testDefaultPrintPrecision() {
    check("NumberForm(1.23456789, DefaultPrintPrecision -> 3)", //
        "1.23");
  }

  @Test
  public void testOptionsDifferPerForm() {
    // only NumberForm, PaddedForm and DecimalForm take DefaultPrintPrecision
    check("First /@ Options(ScientificForm)", //
        "{DigitBlock,ExponentFunction,ExponentStep,NumberFormat,NumberMultiplier,NumberPadding,NumberPoint,NumberSeparator,NumberSigns,SignPadding}");
    // only PaddedForm takes ScientificNotationThreshold; DecimalForm takes no exponent options
    check("First /@ Options(DecimalForm)", //
        "{DefaultPrintPrecision,DigitBlock,NumberPadding,NumberPoint,NumberSeparator,NumberSigns,SignPadding}");
  }

  @Test
  public void testInvalidArguments() {
    check("NumberForm(1.5, 1.5)", //
        "1.5");
    check("ScientificForm(1.0*^7, DigitBlock -> foo)", //
        "1.×10^7");
    // an option the form does not accept
    check("ScientificForm(1.0*^7, DefaultPrintPrecision -> 3)", //
        "1.×10^7");
  }

  @Test
  public void testTeXForm() {
    check("TeXForm(ScientificForm(25000000.0))", //
        "2.5\\times {10}^{7}");
    check("TeXForm(EngineeringForm(25000000.0))", //
        "25.\\times {10}^{6}");
    check("TeXForm(AccountingForm(-2.5))", //
        "(2.5)");
    check("TeXForm(NumberForm(1234567, DigitBlock -> 3))", //
        "1,234,567");
  }

  @Test
  public void testNumberFormOptions() {
    check("ToString(NumberForm(1234.5, NumberPoint -> \",\"))", "1234,5");
    check("ToString(NumberForm(1234.5678, 6, NumberPoint -> \",\"))", "1234,57");
    check("ToString(PaddedForm(1.5, 5, NumberPoint -> \",\"))", "1,5");
    check("ToString(NumberForm(12.3, {5, 3}, NumberPadding -> {\" \", \"0\"}))", "12.300");
    check("ToString(NumberForm(12.3, {8, 3}, NumberPadding -> {\"*\", \"0\"}))", "****12.300");
    check("ToString(PaddedForm(123, 6, NumberPadding -> \"0\"))", "0000123");
    check("ToString(PaddedForm(-123, 6, NumberPadding -> \"0\"))", "000-123");
    check("ToString(NumberForm(-12.3, {6, 2}, NumberPadding -> {\"0\", \"0\"}))", "00-12.30");
    check(
        "ToString(NumberForm(-12.3, {6, 2}, SignPadding -> True, NumberPadding -> {\"0\", \"0\"}))",
        "-0012.30");
    check("ToString(PaddedForm(-123, 6, NumberPadding -> \"0\", SignPadding -> True))", "-000123");
    check("ToString(PaddedForm(123, 6, NumberSigns -> {\"\", \"+\"}))", "+123");
    check("ToString(NumberForm(-1234.5, {6, 1}, NumberSigns -> {\"n\", \"p\"}))", "n1234.5");
    check("ToString(AccountingForm(-12.3, NumberSigns -> {\"<\", \">\"}))", "<12.3");
    check("ToString(PaddedForm(123456, 8, DigitBlock -> 3))", "123,456");
    check("ToString(PaddedForm(123456, 10, DigitBlock -> 2, NumberSeparator -> \"'\"))",
        "12'34'56");
    check("ToString(NumberForm(1234.5678, {8, 4}, DigitBlock -> 2))", "12,34.5678");
    check(
        "ToString(NumberForm(1234.5678, {8, 4}, DigitBlock -> 3, NumberSeparator -> {\"'\", \"_\"}))",
        "1'234.567_8");
    check("ToString(AccountingForm(-12.3, {5, 2}))", "(12.3)");
    check("ToString(AccountingForm(12.345, {5, 2}))", "12.35");
    check("ToString(AccountingForm(12.3, {6, 3}, NumberPadding -> {\" \", \"0\"}))", "12.3000");
    check("ToString(AccountingForm(-12.3, {6, 2}, NumberPadding -> {\" \", \"0\"}))", "(12.3)0");
    check("ToString(AccountingForm({-1.5, 2.5}))", "{(1.5),2.5}");
    check("ToString(NumberForm(Pi, 5))", "Pi");
    check("ToString(NumberForm(x + y, 3))", "x+y");
    check("ToString(NumberForm(\"abc\", 3))", "abc");
    check("ToString(NumberForm(1.5 + 2.5 I, 3))", "1.5+2.5*I");
    check("ToString(PaddedForm(Pi, 5))", "Pi");
    check("ToString(NumberForm(3/4))", "3\n-\n4");
    check("ToString(PaddedForm(1234.5678, 3))", "1230.");
    check("ToString(ScientificForm(1234.5, NumberPoint -> \",\"))", "3\n1,2345×10");
    check("ToString(EngineeringForm(1234.5, NumberPoint -> \",\"))", "3\n1,2345×10");
    check("ToString(ScientificForm(1234567.8, 8, DigitBlock -> 3))", "6\n1.2345678×10");
    check("ToString(NumberForm(1234567.8, 8, DigitBlock -> 3, NumberPoint -> \",\"))",
        "6\n1,2345678×10");
  }
}
