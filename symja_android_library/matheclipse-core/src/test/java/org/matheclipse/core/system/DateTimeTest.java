/*
 * Date and time tests. The output conventions are Symja's: strings print without quotes, a Quantity
 * prints as value[unit], reals always carry a digit after the decimal point, and check() renders
 * doubles with seven significant figures - use checkNumeric() if full precision matters.
 */
package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class DateTimeTest extends ExprEvaluatorTestCase {

  @Test
  public void testAbsoluteTime() {
    check("AbsoluteTime({2000})", "3155673600");
    check("AbsoluteTime(\"6 June 1991\")", "2885155200");
    check("AbsoluteTime({\"01/02/03\", {\"Day\", \"Month\", \"YearShort\"}})", "3.25305*10^9");
    check("AbsoluteTime({\"6-6-91\", {\"Day\", \"Month\", \"YearShort\"}})", "2.88516*10^9");
    check("AbsoluteTime({1991, 6, 6, 12, 0, 0})", "2885198400");
    check("With({t = AbsoluteTime({})}, Head(t) === Real && t > 3155673600)", "True");
    check("AbsoluteTime(DateObject({2020, 3, 5}))", "3792355200");
    check("AbsoluteTime(DateObject({2020, 3, 5, 6, 7, 8}))", "3792377228");
    check("AbsoluteTime(DateObject({2020, 3}))", "3792009600");
    check("AbsoluteTime(DateObject({2020}))", "3786825600");
    check("DateList(AbsoluteTime({2020, 0, 1}))", "{2019,12,1,0,0,0.0}");
    check("DateList(AbsoluteTime({2020, -1, 1}))", "{2019,11,1,0,0,0.0}");
    check("DateList(AbsoluteTime({2020, -12, 1}))", "{2018,12,1,0,0,0.0}");
    check("DateList(AbsoluteTime({2020, 25, 1}))", "{2022,1,1,0,0,0.0}");
    check("DateList(AbsoluteTime({2020, 37, 15}))", "{2023,1,15,0,0,0.0}");
    check("DateList(AbsoluteTime({2020, 0, 0}))", "{2019,11,30,0,0,0.0}");
    check("AbsoluteTime(1000)", "1000");
  }

  @Test
  public void testDateList() {
    check("DateList(0)", "{1900,1,1,0,0,0.0}");
    check("DateList(\"2022-11-23 14:30:00\")", "{2022,11,23,14,30,0.0}");
    check("DateList(\"2022-11-23T14:30:00\")", "{2022,11,23,14,30,0.0}");
    check("DateList(\"2024-02-29 09:15:45\")", "{2024,2,29,9,15,45.0}");
    check("DateList(\"2022-11-23 14:30\")", "{2022,11,23,14,30,0.0}");
    check("DateList(\"2022-11-23\")", "{2022,11,23,0,0,0.0}");
    check("DateList(3155673600)", "{2000,1,1,0,0,0.0}");
    check("DateList(DateObject({2026, 3, 15}))", "{2026,3,15,0,0,0.0}");
    check("DateList(DateObject({2026, 3, 15, 10, 30, 45}))", "{2026,3,15,10,30,45.0}");
    check("DateList(DateObject({2026, 3}))", "{2026,3,1,0,0,0.0}");
    check("DateList({2012, 1, 300., 10})", "{2012,10,26,10,0,0.0}");
    check("DateList(\"31/10/1991\")", "{1991,10,31,0,0,0.0}");
    check("DateList({\"31/10/91\", {\"Day\", \"Month\", \"YearShort\"}})", "{1991,10,31,0,0,0.0}");
    check("DateList({\"31 10/91\", {\"Day\", \" \", \"Month\", \"/\", \"YearShort\"}})",
        "{1991,10,31,0,0,0.0}");
    check("DateList({2003, 5, 0.5, 0.1, 0.767})", "{2003,4,30,12,6,46.02}");
  }

  @Test
  public void testDateBounds() {
    check("DateBounds({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})", "{{2024,1,1},{2024,6,1}}");
    check("DateBounds({{2024, 1, 1}, {2023, 12, 31}, {2024, 12, 31}})",
        "{{2023,12,31},{2024,12,31}}");
    check("DateBounds({{2024, 1, 1}})", "{{2024,1,1},{2024,1,1}}");
    check("DateBounds({{2024, 1, 1, 12, 0, 0}, {2024, 1, 1, 6, 0, 0}})",
        "{{2024,1,1,6,0,0},{2024,1,1,12,0,0}}");
    check("DateBounds({DateObject({2024, 1, 1}), DateObject({2024, 5, 1})})",
        "{DateObject({2024,1,1},Day),DateObject({2024,5,1},Day)}");
    check("DateBounds(DateObject({2024, 1, 1}, \"Year\"))",
        "{DateObject({2024,1,1,0,0,0.0},Instant,Gregorian,None),DateObject({2025,1,1,0,0,0.0},Instant,Gregorian,None)}");
    check("DateBounds(5)", "DateBounds(5)");
  }

  @Test
  public void testMaxDate() {
    check("MaxDate({DateObject({2022, 4, 8}), DateObject({2022, 4, 9}), DateObject({2022, 4, 7})})",
        "DateObject({2022,4,9},Day)");
    check("MaxDate({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})", "{2024,6,1}");
    check("MaxDate({DateObject({2020, 3}), DateObject({2020, 3, 15})})",
        "DateObject({2020,3,15},Day)");
    check("MaxDate({DateObject({2020, 5, 3}), DateObject({2020, 5, 3, 10, 0, 0})})",
        "DateObject({2020,5,3,10,0,0},Instant,Gregorian,0.0)");
    check("MaxDate({DateObject({2022, 4, 8})})", "DateObject({2022,4,8},Day)");
    check("MaxDate(DateInterval({{2020, 1, 1}, {2021, 1, 1}}))", "DateObject({2021,1,1},Day)");
    check("MaxDate(5)", "MaxDate(5)");
  }

  @Test
  public void testMinDate() {
    check("MinDate({DateObject({2022, 4, 8}), DateObject({2022, 4, 9}), DateObject({2022, 4, 7})})",
        "DateObject({2022,4,7},Day)");
    check("MinDate({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})", "{2024,1,1}");
    check("MinDate(DateInterval({{2020, 1, 1}, {2021, 1, 1}}))", "DateObject({2020,1,1},Day)");
    check("MinDate(5)", "MinDate(5)");
  }

  /**
   * The one argument forms. Both were advertised by <code>expectedArgSize</code> and then read a
   * second argument that was not there, so they answered an
   * <code>IndexOutOfBoundsException</code> rather than a value.
   *
   * <p>
   * <code>DatePlus[n]</code> shifts the current instant by the increment, and
   * <code>DateDifference[date]</code> measures from the current instant to the date, which is why
   * it is negative for a date in the past. A bare number is a count of seconds from 1900, so
   * <code>DateDifference[0]</code> reaches back more than a century. Mathematica answers
   * <code>Quantity[-46254.72271229039, "Days"]</code> and
   * <code>{2026,8,22,22,22,29.42}</code> for these; the assertions below are the parts of that
   * which do not depend on when the test runs.
   */
  /**
   * Differences reaching further apart than about 292 years. {@link java.time.Duration#toNanos()}
   * holds only that much in a long and threw an <code>ArithmeticException</code> beyond it, which
   * is every difference reaching back past the 1900 epoch that a bare number denotes. Mathematica
   * answers the first of these with <code>Quantity[29979111601/43200, "Days"]</code>, exactly as
   * below: the exact rational is a convention the two share.
   */
  @Test
  public void testDateDifferenceBeyondLongNanos() {
    check("DateDifference({0, 1, 1, 2}, 2)", "Quantity(29979111601/43200,\"Days\")");
    check("DateDifference({0, 1, Sequence(), 1, 2}, 2)", "Quantity(29979111601/43200,\"Days\")");
    check("DateDifference({1000, 1, 1}, {3000, 1, 1})", "Quantity(730485,\"Days\")");
    check("DateDifference({1000, 1, 1}, {3000, 1, 1}, \"Year\")", "Quantity(2000,\"Years\")");
    check("DateDifference({1000, 1, 1}, {3000, 1, 1}, \"Hour\")", "Quantity(17531640,\"Hours\")");
    // 2000 Gregorian years are 730000 + (500 - 20 + 5) leap days = 730485 days, which is exactly
    // 104355 weeks; a span that is not a whole number of weeks comes back as a machine number
    check("DateDifference({1000, 1, 1}, {3000, 1, 1}, \"Week\")", "Quantity(104355,\"Weeks\")");
    check("DateDifference({1000, 1, 1}, {3000, 1, 2}, \"Week\")", "Quantity(104355.1,\"Weeks\")");
  }

  /**
   * A bare number is a count of seconds from 1900, and one large enough carries the date past the
   * range {@link java.time.LocalDate} can hold. That used to escape as a
   * <code>DateTimeException</code> from whichever function asked for the date; Mathematica answers
   * <code>DayMatchQ[9223372036854775807, 2]</code> with the unevaluated expression, which is what
   * the null contract of the date specification reader already meant to produce.
   */
  /**
   * Spans that a long cannot hold in nanoseconds, and components no calendar can hold at all.
   *
   * <p>
   * <code>MidDate</code> accumulated nanosecond offsets through
   * {@link java.time.Duration#toNanos()}, which overflows past about 292 years, and
   * <code>DayRange</code> read a year of 2147483647 straight into {@link java.time.LocalDate},
   * which cannot express it. Neither is a memory problem, which is what Mathematica runs into on
   * the second one; Symja declines earlier and leaves the expression alone.
   *
   * <p>
   * On the first, Symja and Mathematica agree exactly once they are given the same dates:
   * <code>MidDate[{{1,0},{0,1}}]</code> is <code>{-1, 7, 2, 0, 0, 0}</code> in both. Mathematica
   * answers a three element argument from the first two dates alone, where Symja takes the mean of
   * all three, so <code>{{1,0},{0,1},0}</code> is the one place the two part company.
   */
  @Test
  public void testSpansBeyondLongNanos() {
    check("MidDate({{1,0},{0,1}})", "DateObject({-1,7,2,0,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate({{1,0},{0,1},0})", "DateObject({633,9,1,8,0,0.0},Instant,Gregorian,0.0)");
    check("DayRange(0, {2147483647,-2,3})", "DayRange(0,{2147483647,-2,3})");
    // and the ordinary spans are unchanged
    check("MidDate({{2020,1,1},{2020,1,3}})", //
        "DateObject({2020,1,2,12,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate({{2000,1,1},{2026,1,1}})", //
        "DateObject({2013,1,1,0,0,0.0},Instant,Gregorian,0.0)");
  }

  @Test
  public void testUnplaceableNumericDate() {
    check("DayMatchQ(9223372036854775807, Monday)", "DayMatchQ(9223372036854775807,Monday)");
    check("DayMatchQ(-9223372036854775807, Monday)", "DayMatchQ(-9223372036854775807,Monday)");
    check("DayMatchQ(1.0*^30, Monday)", "DayMatchQ(1.*10^30,Monday)");
    // an infinity reaches the same reader and used to be narrowed to a long on the way in
    check("DayMatchQ(Sinh(1000.0), Monday)", "DayMatchQ(Infinity,Monday)");
    check("DateObject(9223372036854775807)", "DateObject(9223372036854775807)");
    check("DayName(9223372036854775807)", "DayName(9223372036854775807)");
    check("DateDifference(9223372036854775807, 0)", "DateDifference(9223372036854775807,0)");
    // dates that do fit are unaffected: 1900-01-01 was a Monday, a day later a Tuesday
    check("DayMatchQ(0, Monday)", "True");
    check("DayMatchQ(86400, Tuesday)", "True");
    check("DayMatchQ({2026, 8, 23}, Sunday)", "True");
    check("DayMatchQ({2026, 8, 23}, Monday)", "False");
    check("DayName({2026, 8, 23})", "Sunday");
  }

  @Test
  public void testDateOneArgument() {
    check("Head(DatePlus(0))", "List");
    check("Length(DatePlus(0))", "6");
    // the year of the current instant, which is what DateList() reports as well
    check("First(DatePlus(0)) === First(DateList())", "True");
    // three days on is three days later, give or take the nanoseconds between the two readings
    // of the clock
    check("Round(QuantityMagnitude(DateDifference(DatePlus(0), DatePlus(3))))", "3");
    check("QuantityUnit(DateDifference(0))", "Days");
    // 1900 is over 46000 days before any date this code will run on
    check("QuantityMagnitude(DateDifference(0)) < -46000", "True");
    // and the two argument forms are unchanged
    check("DateDifference({2042, 1, 4}, {2057, 1, 1})", "Quantity(5476,\"Days\")");
    check("DatePlus({2010, 2, 5}, 73)", "{2010,4,19}");
  }

  @Test
  public void testDatePlus() {
    check("DatePlus({2010, 2, 5}, 73)", "{2010,4,19}");
    // FullForm evaluates, so the result is the full form itself, not a FullForm(...) wrapper
    check("FullForm(DatePlus(DateObject({2026, 4, 1}, \"Day\"), 7))",
        "DateObject(List(2026, 4, 8), \"Day\")");
    check("DatePlus({2010, 2, 5}, {{8, \"Week\"}, {1, \"Day\"}})", "{2010,4,3}");
    check("DatePlus({2013, 8}, {{1, \"Month\"}, {-1, \"Day\"}})", "{2013,8,31}");
    check("DatePlus({2013, 2}, {{1, \"Month\"}, {-1, \"Day\"}})", "{2013,2,28}");
    check("DatePlus({2020, 1, 31}, {1, \"Month\"})", "{2020,2,29}");
    check("DatePlus({2013, 12, 15}, {1, \"Year\"})", "{2014,12,15}");
    check("DatePlus({2024, 1, 1}, {2, \"Months\"})", "{2024,3,1}");
    check("DatePlus({2024, 1, 15}, {3, \"Days\"})", "{2024,1,18}");
    check("DatePlus({2024, 11, 1}, {3, \"Months\"})", "{2025,2,1}");
    check("DatePlus({2024, 1, 1}, {1, \"Years\"})", "{2025,1,1}");
    check("DatePlus({2024, 1, 1}, {2, \"Weeks\"})", "{2024,1,15}");
    // FullForm evaluates, so the result is the full form itself, not a FullForm(...) wrapper
    check("FullForm(DatePlus(DateObject({2026, 4, 1}, \"Day\"), Quantity(7, \"Days\")))",
        "DateObject(List(2026, 4, 8), \"Day\")");
    // FullForm evaluates, so the result is the full form itself, not a FullForm(...) wrapper
    check("FullForm(DatePlus(DateObject({2026, 4, 1}, \"Day\"), Quantity(4, \"Weeks\")))",
        "DateObject(List(2026, 4, 29), \"Day\")");
    // FullForm evaluates, so the result is the full form itself, not a FullForm(...) wrapper
    check("FullForm(DatePlus(DateObject({2026, 4, 1}, \"Day\"), Quantity(5, \"Months\")))",
        "DateObject(List(2026, 9, 1), \"Day\")");
    check("DatePlus({2026, 1, 31}, Quantity(1, \"Months\"))", "{2026,2,28}");
    check("DatePlus({2026, 4, 1}, Quantity(2, \"Years\"))", "{2028,4,1}");
    check("DayName(DatePlus(DateObject({2026, 4, 1}, \"Day\"), Quantity(5, \"Months\")))",
        "Tuesday");
    check("DatePlus({2024, 7, 4}, Quantity(5, \"Meters\"))",
        "DatePlus({2024,7,4},Quantity(5,\"Meters\"))");
    check("DatePlus(DateObject({2024, 7, 4}), Quantity(3, \"Kilograms\"))",
        "DatePlus(DateObject({2024,7,4},Day),Quantity(3,\"Kilograms\"))");
  }

  @Test
  public void testDateObjectPlusQuantity() {
    check("DateObject({2024, 7, 4}) + Quantity(1, \"Day\")", "DateObject({2024,7,5},Day)");
    check("Quantity(1, \"Day\") + DateObject({2024, 7, 4})", "DateObject({2024,7,5},Day)");
    check("DateObject({2024, 7, 4}) + Quantity(2, \"Months\")", "DateObject({2024,9,4},Day)");
    check("DateObject({2024, 7, 4}) - Quantity(3, \"Days\")", "DateObject({2024,7,1},Day)");
    check("DateObject({2024, 1, 31}) + Quantity(1, \"Months\")", "DateObject({2024,2,29},Day)");
    check("DateObject({2024, 7, 4}) + Quantity(1, \"Days\") + Quantity(1, \"Days\")",
        "DateObject({2024,7,6},Day)");
    check("DateObject({2024, 7, 4}) + Quantity(5, \"Meters\")",
        "Quantity(5,\"Meters\")+DateObject({2024,7,4},Day)");
  }

  @Test
  public void testDateDifference() {
    check("DateDifference({2042, 1, 4}, {2057, 1, 1})", "Quantity(5476,\"Days\")");
    check("DateDifference({1936, 8, 14}, {2000, 12, 1}, \"Year\")", "Quantity(64.29863,\"Years\")");
    check("DateDifference({2010, 6, 1}, {2015, 1, 1}, \"Hour\")", "Quantity(40200,\"Hours\")");
    check("DateDifference({2024, 1, 1, 10, 0, 0}, {2024, 1, 1, 12, 30, 0}, \"Hour\")",
        "Quantity(5/2,\"Hours\")");
    check("DateDifference({2024, 1, 1, 0, 0, 0}, {2024, 1, 1, 0, 0, 10}, \"Hour\")",
        "Quantity(1/360,\"Hours\")");
    check("DateDifference({2024, 1, 1, 0, 0, 0}, {2024, 1, 1, 0, 0, 10}, \"Minute\")",
        "Quantity(1/6,\"Minutes\")");
    check("DateDifference({2024, 1, 1, 0, 0, 0}, {2024, 1, 1, 0, 0, 45}, \"Second\")",
        "Quantity(45,\"Seconds\")");
    check("DateDifference({2024, 1, 1}, {2024, 3, 1}, \"Week\")", "Quantity(8.57143,\"Weeks\")");
    check("DateDifference(DateObject({2020, 1, 1}), DateObject({2020, 1, 11}))",
        "Quantity(10,\"Days\")");
    check("DateDifference({2023, 1, 1}, {2023, 3, 1}, \"Month\")", "Quantity(2,\"Months\")");
    check("DateDifference({2023, 1, 31}, {2023, 2, 28}, \"Month\")", "Quantity(1,\"Months\")");
    check("DateDifference({2023, 1, 1}, {2023, 3, 15}, \"Month\")", "Quantity(2.45161,\"Months\")");
    check("DateDifference({2023, 1, 15}, {2023, 3, 1}, \"Month\")", "Quantity(1.5,\"Months\")");
    check("DateDifference({2023, 3, 1}, {2023, 1, 1}, \"Month\")", "Quantity(-2,\"Months\")");
    check("DateDifference({2020, 1, 1}, {2023, 1, 1}, \"Year\")", "Quantity(3,\"Years\")");
    check("DateDifference({2020, 1, 1}, {2020, 7, 1}, \"Year\")", "Quantity(0.497268,\"Years\")");
    check("DateDifference({2023, 6, 15}, {2025, 9, 20}, \"Year\")", "Quantity(2.26575,\"Years\")");
    check("DateDifference({2024, 1, 1}, x)", "DateDifference({2024,1,1},x)");
  }

  @Test
  public void testDateString() {
    check("DateString({1991, 10, 31, 0, 0}, {\"Day\", \" \", \"MonthName\", \" \", \"Year\"})",
        "31 October 1991");
    check("DateString({2007, 4, 15, 0})", "Sun 15 Apr 2007 00:00:00");
    check("DateString(0)", "Mon 1 Jan 1900 00:00:00");
    check("DateString(3155673600)", "Sat 1 Jan 2000 00:00:00");
    check("DateString(1.5*^9)", "Tue 15 Jul 1947 02:40:00");
    check("DateString(3155673600, \"ISODate\")", "2000-01-01");
    check("DateString({1979, 3, 14}, {\"DayName\", \"  \", \"Month\", \"-\", \"YearShort\"})",
        "Wednesday  03-79");
    check("DateString({2024, 3, 15, 9, 5, 0}, {\"Hour12\", \":\", \"Minute\", \" \", \"AMPM\"})",
        "09:05 AM");
    check("DateString({2024, 3, 15, 21, 0, 0}, {\"Hour12\", \" \", \"AMPM\"})", "09 PM");
    check("DateString({2024, 3, 15, 0, 5, 0}, {\"Hour12\", \" \", \"AMPMLowerCase\"})", "12 am");
    check("DateString({2024, 8, 15}, {\"Quarter\"})", "3");
    check("DateString({2024, 3, 5}, {\"DayShort\", \"/\", \"MonthShort\", \"/\", \"Year\"})",
        "5/3/2024");
    check("DateString({2024, 3, 7, 14, 5}, {\"Hour24\", \":\", \"Minute\"})", "14:05");
    check("DateString({2024, 3, 7, 3, 5}, {\"Hour24\"})", "03");
    check("DateString({2024, 3, 7, 0, 5}, {\"Hour24\"})", "00");
    check("DateString({2024, 3, 7, 3, 5}, {\"Hour24Short\"})", "3");
    check("DateString({2024, 3, 7, 14, 5}, \"Hour24\")", "14");
    check("DateString({2024, 3, 7}, {\"ISOWeekDay\"})", "4");
    check("DateString({2024, 3, 3}, {\"ISOWeekDay\"})", "7");
    check("DateString({2024, 3, 4}, {\"ISOWeekDay\"})", "1");
    check("DateString({1991, 6, 6.5})", "Thu 6 Jun 1991 12:00:00");
    check("DateString({2026, 2, 27, 19, 54, 40}, \"ISODateTime\")", "2026-02-27T19:54:40");
    check("DateString({2026, 2, 27, 19, 54, 40}, \"ISODate\")", "2026-02-27");
    check("DateString({2026, 2, 27, 20, 5, 43}, \"DateTime\")", "Friday 27 February 2026 20:05:43");
    check("DateString({2026, 2, 27, 20, 5, 43}, \"DateTimeShort\")", "Fri 27 Feb 2026 20:05:43");
    check("DateString({2026, 2, 27}, \"Date\")", "Friday 27 February 2026");
    check("DateString({2026, 2, 27}, \"DateShort\")", "Fri 27 Feb 2026");
    check("DateString({2026, 2, 27, 14, 30, 15}, \"Time\")", "14:30:15");
    check("DateString({2026, 2, 27}, \"Year\")", "2026");
    check("DateString({2026, 2, 27}, \"MonthName\")", "February");
    check(
        "DateString(DateObject({2026, 2, 27, 19, 54, 40}, \"Instant\", \"Gregorian\", 0.), \"ISODateTime\")",
        "2026-02-27T19:54:40");
    check("DateString(\"2025-09-24\", {\"Year\", \"-\", \"Month\"})", "2025-09");
    check("DateString(\"2025-09-24\")", "2025-09-24");
    check("DateString(\"6 June 1991\")", "6 June 1991");
    check("DateString(\"March 5, 2025\")", "March 5, 2025");
    // StringLength gives an integer
    check("StringLength(DateString(\"ISODate\"))", "10");
    // StringLength gives an integer
    check("StringLength(DateString(\"ISODateTime\"))", "19");
    check(
        "StringMatchQ(DateString(\"ISODate\"), RegularExpression(\"\\\\d{4}-\\\\d{2}-\\\\d{2}\"))",
        "True");
    check("DateString(\"ISODate\") == DateString(DateList(), \"ISODate\")", "True");
    check("DateString(\"Year\") == DateString(DateList(), \"Year\")", "True");
    check("DateString(\"hello\")", "hello");
    check("DateString(DateObject({2024, 1, 15}, \"Day\"))", "Mon 15 Jan 2024");
    check("DateString(DateObject({2024, 1, 15}))", "Mon 15 Jan 2024");
    check("DateString({2024, 6, 15})", "Sat 15 Jun 2024 00:00:00");
    check("DateString(DateObject({2024, 1, 15, 10, 30, 0}))", "Mon 15 Jan 2024 10:30:00");
    check("DateString(\"2025-09-24\", \"ISODate\")", "2025-09-24");
    check("DateString(\"6 June 1991\", {\"Year\", \"-\", \"Month\", \"-\", \"Day\"})",
        "1991-06-06");
    check("DateString(\"6 June 1991\", {\"Year\", \"-\", \"Month\", \"-\", \"Day2\"})",
        "1991-06-Day2");
  }

  @Test
  public void testDayName() {
    check("DayName({2024, 1, 1})", "Monday");
    check("DayName({2026, 3, 19})", "Thursday");
    check("DayName({2000, 1, 1})", "Saturday");
    check("DayName({1970, 1, 1})", "Thursday");
    check("DayName(DateObject({2024, 6, 15}))", "Saturday");
    check("DayName({2024, 3, 3})", "Sunday");
    check("DayName({2024, 2})", "Thursday");
    check("DayName({2024})", "Monday");
    check("DayName(0)", "Monday");
    check("DayName(2024)", "Monday");
    check("DayName(2024.5)", "Monday");
    check("DayName(3155673600)", "Saturday");
    check("DayName(\"Feb 1 2024\")", "Thursday");
    check("DayName(\"1 Feb 2024\")", "Thursday");
    check("DayName(\"February 1 2024\")", "Thursday");
    check("DayName(\"2024-02-01\")", "Thursday");
    check("DayName() === DayName(Today)", "True");
    check("DayName({}) === DayName(Today)", "True");
    check("Take(DateList({}), 3) === Take(DateList(Today), 3)", "True");
    check("DayName(Yesterday) === DayName(DatePlus(Today, -1))", "True");
    check("DayName({2024, 2, 1}, CalendarType -> \"Gregorian\")", "Thursday");
    check("DayName(\"Wednesday\")", "DayName(Wednesday)");
    check("DayName(\"not a date\")", "DayName(not a date)");
    check("DayName({2024, \"a\"})", "DayName({2024,a})");
    check("DayName(x)", "DayName(x)");
    check("DayName(1, 2)", "DayName(1,2)");
  }

  @Test
  public void testDayPlus() {
    check("DayPlus({2024, 1, 15}, 10)", "DateObject({2024,1,25},Day)");
    check("DayPlus({2024, 1, 15}, -5)", "DateObject({2024,1,10},Day)");
    check("DayPlus({2024, 2, 28}, 1)", "DateObject({2024,2,29},Day)");
    check("DayPlus({2024, 1, 31}, 1)", "DateObject({2024,2,1},Day)");
    check("DayPlus({2024, 1, 15}, 10, \"BusinessDay\")", "DateObject({2024,1,29},Day)");
  }

  @Test
  public void testDated() {
    check("Dated(100, {2015, 3, 1})", "Dated(100,{2015,3,1})");
    check("Head(Dated(100, {2015, 3, 1}))", "Dated");
    check("Dated(100, {2015, 3, 1})[[1]]", "100");
    check("Dated(100, {2015, 3, 1})[[2]]", "{2015,3,1}");
    check("Dated(\"hello\", {2020})", "Dated(hello,{2020})");
  }

  @Test
  public void testLeapYearQ() {
    check("LeapYearQ(2024)", "False");
    check("LeapYearQ(2023)", "False");
    check("LeapYearQ(2000)", "False");
    check("LeapYearQ(1900)", "False");
    check("LeapYearQ({2024})", "True");
    check("LeapYearQ({2023})", "False");
    check("LeapYearQ({2000})", "True");
    check("LeapYearQ({1900})", "False");
    check("LeapYearQ({2004})", "True");
    check("LeapYearQ(\"2024\")", "True");
    check("LeapYearQ(\"2023\")", "False");
    check("LeapYearQ(\"1996\")", "True");
    check("LeapYearQ(\"2000\")", "True");
    check("LeapYearQ(\"1900\")", "False");
    check("LeapYearQ(\"2024-03-01\")", "True");
    check("LeapYearQ(x)", "LeapYearQ(x)");
    check("LeapYearQ(foo(1))", "LeapYearQ(foo(1))");
    check("DayCount({2024, 1, 1}, x)", "DayCount({2024,1,1},x)");
    check("DayCount(x, {2024, 1, 1})", "DayCount(x,{2024,1,1})");
    check("DayCount({2024, 1, 1}, {2024, 2, 1})", "31");
  }

  @Test
  public void testDayRange() {
    check("DayRange({2020, 1, 1}, {2020, 1, 5})",
        "{DateObject({2020,1,1},Day),DateObject({2020,1,2},Day),DateObject({2020,1,3},Day),DateObject({\n"
            + "2020,1,4},Day),DateObject({2020,1,5},Day)}");
    check("DayRange({2020, 1, 1}, {2020, 1, 1})", "{DateObject({2020,1,1},Day)}");
    check("DayRange({2013, 1}, {2013, 1, 31}, Sunday)",
        "{DateObject({2013,1,6},Day),DateObject({2013,1,13},Day),DateObject({2013,1,20},Day),DateObject({\n"
            + "2013,1,27},Day)}");
    check("DayRange({2020, 1, 30}, {2020, 2, 2})",
        "{DateObject({2020,1,30},Day),DateObject({2020,1,31},Day),DateObject({2020,2,1},Day),DateObject({\n"
            + "2020,2,2},Day)}");
    check("DayRange({2019, 12, 30}, {2020, 1, 2})",
        "{DateObject({2019,12,30},Day),DateObject({2019,12,31},Day),DateObject({2020,1,1},Day),DateObject({\n"
            + "2020,1,2},Day)}");
    check("DayRange({2020, 1, 3}, {2020, 1, 1})",
        "{DateObject({2020,1,1},Day),DateObject({2020,1,2},Day),DateObject({2020,1,3},Day)}");
    check("DayRange(DateObject({2020, 1, 1}), DateObject({2020, 1, 3}))",
        "{DateObject({2020,1,1},Day),DateObject({2020,1,2},Day),DateObject({2020,1,3},Day)}");
  }

  @Test
  public void testDateRange() {
    check("DateRange({2024, 1, 1}, {2024, 1, 3})",
        "{{2024,1,1,0,0,0.0},{2024,1,2,0,0,0.0},{2024,1,3,0,0,0.0}}");
    check("DateRange({2024, 1, 1}, {2024, 1, 1})", "{{2024,1,1,0,0,0.0}}");
    check("DateRange({2024, 1, 1}, {2024, 1, 10}, 2)",
        "{{2024,1,1,0,0,0.0},{2024,1,3,0,0,0.0},{2024,1,5,0,0,0.0},{2024,1,7,0,0,0.0},{\n"
            + "2024,1,9,0,0,0.0}}");
    check("DateRange({2024, 1, 1}, {2024, 1, 10}, Quantity(3, \"Days\"))",
        "{{2024,1,1,0,0,0.0},{2024,1,4,0,0,0.0},{2024,1,7,0,0,0.0},{2024,1,10,0,0,0.0}}");
    check("DateRange({2024, 1, 1}, {2024, 1, 15}, Quantity(1, \"Weeks\"))",
        "{{2024,1,1,0,0,0.0},{2024,1,8,0,0,0.0},{2024,1,15,0,0,0.0}}");
    check("DateRange({2024, 1, 1, 12, 0, 0}, {2024, 1, 1, 15, 0, 0}, Quantity(1, \"Hours\"))",
        "{{2024,1,1,12,0,0.0},{2024,1,1,13,0,0.0},{2024,1,1,14,0,0.0},{2024,1,1,15,0,0.0}}");
    check("DateRange({2024, 1, 15}, {2024, 4, 15}, Quantity(1, \"Months\"))",
        "{{2024,1,15,0,0,0.0},{2024,2,15,0,0,0.0},{2024,3,15,0,0,0.0},{2024,4,15,0,0,0.0}}");
    check("DateRange({2020, 6, 1}, {2023, 6, 1}, Quantity(1, \"Years\"))",
        "{{2020,6,1,0,0,0.0},{2021,6,1,0,0,0.0},{2022,6,1,0,0,0.0},{2023,6,1,0,0,0.0}}");
    check("DateRange({2024, 1, 1}, {2024, 3, 1}, \"Month\")",
        "{{2024,1,1,0,0,0.0},{2024,2,1,0,0,0.0},{2024,3,1,0,0,0.0}}");
    check("DateRange({2024, 1, 5}, {2024, 1, 1})", "{}");
  }

  @Test
  public void testTimeObject() {
    check("TimeObject({14, 30})", "TimeObject({14,30},Minute)");
    check("TimeObject({14})", "TimeObject({14},Hour)");
    check("TimeObject({14, 30, 15})", "TimeObject({14,30,15},Instant)");
    check("TimeObject({14, 30, 0}) + Quantity(90, \"Minutes\")",
        "TimeObject({16,0,0},Instant,0.0)");
    check("TimeObject({23, 30, 0}) + Quantity(60, \"Minutes\")",
        "TimeObject({0,30,0},Instant,0.0)");
    check("TimeObject({10, 0, 0}) - Quantity(2, \"Hours\")", "TimeObject({8,0,0},Instant,0.0)");
    check("Quantity(15, \"Minutes\") + TimeObject({9, 0, 0})", "TimeObject({9,15,0},Instant,0.0)");
    check("TimeObject({14, 30}) + Quantity(1, \"Hours\")", "TimeObject({15,30},Minute,0.0)");
    check("TimeObject({14, 30, 15.5})", "TimeObject({14,30,15.5},Instant)");
    check("TimeObject({25, 30})", "TimeObject({1,30},Minute)");
    check("TimeObject({14, 75})", "TimeObject({15,15},Minute)");
    check("TimeObject({14, 30, 75})", "TimeObject({14,31,15},Instant)");
    check("TimeObject({-1, 30})", "TimeObject({23,30},Minute)");
    check("TimeObject({14.5, 30})", "TimeObject({15,0},Minute)");
    check("TimeObject({23, 59, 60})", "TimeObject({0,0,0},Instant)");
    check("TimeObject({14.5})", "TimeObject({14},Hour)");
    check("TimeObject({13, 5, 0})(\"Hour\")", "13");
    check("TimeObject({13, 5, 0})(\"Minute\")", "5");
    check("TimeObject({13, 5, 7})(\"Second\")", "7");
    check("TimeObject({13, 5, 0})(\"Granularity\")", "Instant");
  }

  @Test
  public void testJulianDate() {
    check("JulianDate({2000, 1, 1, 12, 0, 0})", "2.45155*10^6");
    check("JulianDate({2026, 6, 11})", "2.4612*10^6");
    check("JulianDate({2000})", "2.45154*10^6");
    check("JulianDate({2000, 1, 1, 12})", "2.45155*10^6");
    check("JulianDate({1999, 12, 31, 23, 59, 60})", "2.45154*10^6");
    check("JulianDate({1582, 10, 15})", "2.29916*10^6");
    check("JulianDate({2000, 1, 1, 12, 30, 45.5})", "2.45155*10^6");
    check("JulianDate({0, 1, 1})", "1.72106*10^6");
    check("JulianDate({-1, 1, 1})", "1.72106*10^6");
    check("JulianDate({1, 1, 1})", "1.72143*10^6");
    check("JulianDate({-100, 1, 1})", "1.6849*10^6");
    check("JulianDate({-4713, 1, 1})", "37.5");
    check("JulianDate({-4712, 1, 1, 12, 0, 0})", "404.0");
    check("JulianDate(x)", "JulianDate(x)");
  }

  @Test
  public void testFromUnixTime() {
    check("FromUnixTime(0, TimeZone -> 0)", "DateObject({1970,1,1,0,0,0},Instant,Gregorian,0.0)");
    check("FromUnixTime(1577836800, TimeZone -> 0)",
        "DateObject({2020,1,1,0,0,0},Instant,Gregorian,0.0)");
    check("FromUnixTime(1000000000, TimeZone -> 0)",
        "DateObject({2001,9,9,1,46,40},Instant,Gregorian,0.0)");
    check("FromUnixTime(1577836845, TimeZone -> 0)",
        "DateObject({2020,1,1,0,0,45},Instant,Gregorian,0.0)");
    check("FromUnixTime(0, TimeZone -> 1)", "DateObject({1970,1,1,1,0,0},Instant,Gregorian,1.0)");
    check("FromUnixTime(0, TimeZone -> -5)",
        "DateObject({1969,12,31,19,0,0},Instant,Gregorian,-5.0)");
    check("FromUnixTime(0, TimeZone -> 5.5)",
        "DateObject({1970,1,1,5,30,0},Instant,Gregorian,5.5)");
    check("Head(FromUnixTime(0, TimeZone -> 0))", "DateObject");
  }

  @Test
  public void testFromAbsoluteTime() {
    check("FromAbsoluteTime(0)", "DateObject({1900,1,1,0,0,0},Instant,Gregorian,0.0)");
    check("FromAbsoluteTime(86400)", "DateObject({1900,1,2,0,0,0},Instant,Gregorian,0.0)");
    check("FromAbsoluteTime(2208988800)", "DateObject({1970,1,1,0,0,0},Instant,Gregorian,0.0)");
    check("FromAbsoluteTime(3155673600)", "DateObject({2000,1,1,0,0,0},Instant,Gregorian,0.0)");
    check("FromAbsoluteTime(AbsoluteTime({2024, 6, 14}))",
        "DateObject({2024,6,14,0,0,0},Instant,Gregorian,0.0)");
    check("Head(FromAbsoluteTime(0))", "DateObject");
  }

  @Test
  public void testUnixTime() {
    check("UnixTime({2020, 1, 1, 0, 0, 0})", "1577836800");
    check("UnixTime({2020, 1, 1})", "1577836800");
    check("UnixTime(FromUnixTime(1577836800, TimeZone -> 0))", "1577836800");
    check("UnixTime(FromUnixTime(1577836800, TimeZone -> -5))", "1577836800");
    check("UnixTime(FromUnixTime(1000000000, TimeZone -> 5.5))", "1000000000");
  }

  @Test
  public void testDateObject() {
    // the granularity prints unquoted, as everywhere else in this file
    check("DateObject({2020, 4, 15})", "DateObject({2020,4,15},Day)");
    check("DateObject({2024, 7, 4, 15, 30, 45})(\"Day\")", "4");
    check("DateObject({2024, 7, 4, 15, 30, 45})(\"Year\")", "2024");
    check("DateObject({2024, 7, 4, 15, 30, 45})(\"Hour\")", "15");
    check("DateObject({2024, 7, 4, 15, 30, 45})(\"DayName\")", "Thursday");
    check("DateObject({2024, 7, 4})(\"Week\")", "27");
    check("DateObject({2024, 7, 4})(\"Quarter\")", "3");
    check("DateObject({2024, 7, 4})(\"Nonsense\")", "DateObject({2024,7,4},Day)(Nonsense)");
    check("DateObjectQ(Today)", "True");
    check("DateObjectQ(Now)", "True");
    check("DateObjectQ(DateObject({2026, 7, 4}))", "True");
    check("DateObjectQ(DateObject({2026, 13, 45}))", "True");
    check("DateObjectQ(TimeObject({12, 30}))", "False");
    check("DateObjectQ(\"2026-07-04\")", "False");
    check("DateObjectQ(42)", "False");
    check("DateObjectQ(x)", "False");
    check("DateObject({2026, 13, 45})", "DateObject({2027,2,14},Day)");
    check("DateObject({2026, 0, 5})", "DateObject({2025,12,5},Day)");
    check("DateObject({2026, -1, 5})", "DateObject({2025,11,5},Day)");
    check("DateObject({2026, 13}, \"Month\")", "DateObject({2027,1},Month)");
    check("DateObject({2026, 1, 0})", "DateObject({2025,12,31},Day)");
    check("DateObject({2026, 1, -1})", "DateObject({2025,12,30},Day)");
    check("DateObject({2026, 2, 30})", "DateObject({2026,3,2},Day)");
    check("DateObject({2024, 2, 30})", "DateObject({2024,3,1},Day)");
    check("DateObject({2026, 12, 32})", "DateObject({2027,1,1},Day)");
    check("DateObject({2026, 13, 45}, \"Day\")", "DateObject({2027,2,14},Day)");
    check("DateObject({2026, 7, 4, 25, 0, 0})",
        "DateObject({2026,7,5,1,0,0},Instant,Gregorian,0.0)");
    check("DateObject({2026, 7, 4, 23, 61, 0})",
        "DateObject({2026,7,5,0,1,0},Instant,Gregorian,0.0)");
    check("DateObject({2026, 7, 4, 12, 30, 75.5})",
        "DateObject({2026,7,4,12,31,15.5},Instant,Gregorian,0.0)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Year\")", "DateObject({2024},Year)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Month\")", "DateObject({2024,2},Month)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Day\")", "DateObject({2024,2,29},Day)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Hour\")",
        "DateObject({2024,2,29,13},Hour,Gregorian,0.0)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Minute\")",
        "DateObject({2024,2,29,13,5},Minute,Gregorian,0.0)");
    check("DateObject({2024, 2, 29, 13, 5, 7}, \"Instant\")",
        "DateObject({2024,2,29,13,5,7},Instant,Gregorian,0.0)");
    check("DateObject({2024}, \"Day\")", "DateObject({2024,1,1},Day)");
    check("DateObject({2024, 2}, \"Day\")", "DateObject({2024,2,1},Day)");
    check("DateObject({2024, 2, 29}, \"Hour\")", "DateObject({2024,2,29,0},Hour,Gregorian,0.0)");
    check("DateObject({2024, 2, 29})", "DateObject({2024,2,29},Day)");
    check("DateList(DateObject({2024, 2, 29}, \"Month\"))", "{2024,2,1,0,0,0.0}");
    check("DateObject({2024, 2, 29})(\"Granularity\")", "Day");
    check("DateObject({2024, 2, 29}, \"Month\")(\"Granularity\")", "Month");
    check("DateObject({2024, 2, 29, 13})(\"Granularity\")", "Hour");
    check("DateObject({2024})(\"Granularity\")", "Year");
  }

  @Test
  public void testDateValue() {
    check("DateValue({2024, 6, 15}, \"Year\")", "2024");
    check("DateValue({2024, 6, 15}, \"Month\")", "6");
    check("DateValue({2024, 6, 15}, \"Day\")", "15");
    check("DateValue({2024, 6, 15, 10, 30, 0}, \"Hour\")", "10");
    check("DateValue({2024, 6, 15}, \"DayName\")", "Saturday");
    check("ToString(DateValue({2024, 6, 15}, \"DayName\"), InputForm)", "Saturday");
    check("ToString(DateValue({2024, 6, 15}, \"MonthName\"), InputForm)", "\"June\"");
    check("DateValue({2024, 6, 15}, \"MonthName\")", "June");
    check("DateValue({2024, 6, 15}, \"Quarter\")", "2");
    check("DateValue({2024, 3, 15}, \"DayNameShort\")", "Fri");
    check("DateValue({2024, 12, 25}, \"DayNameShort\")", "Wed");
    check("DateValue({2024, 3, 15}, \"MonthNameShort\")", "Mar");
    check("DateValue({2024, 1, 1}, \"MonthNameShort\")", "Jan");
    check("DateValue({2024, 12, 25}, \"MonthNameShort\")", "Dec");
    check("DateValue({2024, 3, 15}, {\"MonthNameShort\", \"DayNameShort\"})", "{Mar,Fri}");
    check("DateValue({2024, 3, 15, 14, 30, 0}, \"Hour12\")", "2");
    check("DateValue({2024, 3, 15, 0, 0, 0}, \"Hour12\")", "12");
    check("DateValue({2024, 3, 15, 12, 0, 0}, \"Hour12\")", "12");
    check("DateValue({2024, 3, 15, 14, 0, 0}, \"AMPM\")", "PM");
    check("DateValue({2024, 3, 15, 0, 0, 0}, \"AMPM\")", "AM");
    check("DateValue({2024, 3, 15}, \"YearShort\")", "24");
    check("DateValue({2005, 3, 15}, \"YearShort\")", "5");
    check("DateValue({2024, 3, 15}, \"MonthShort\")", "3");
    check("DateValue({2024, 3, 15}, \"DayShort\")", "15");
    check("DateValue({2024, 3, 15, 14, 30, 45}, \"HourShort\")", "14");
    check("DateValue({2024, 3, 15, 14, 30, 45}, \"SecondShort\")", "45");
    check("DateValue({2024, 3, 15}, \"ISOWeek\")", "11");
    check("DateValue({2024, 3, 15}, \"ISOWeekYear\")", "2024");
    check("DateValue({2023, 1, 1}, \"ISOWeek\")", "52");
    check("DateValue({2023, 1, 1}, \"ISOWeekYear\")", "2022");
    check("DateValue({2021, 1, 1}, \"ISOWeekYear\")", "2020");
    check("DateValue({2025, 12, 29}, \"ISOWeekYear\")", "2026");
    check("DateValue({2024, 6, 15}, \"DayOfYear\")", "167");
    check("DateValue({2024, 2, 29}, \"DayOfYear\")", "60");
    check("DateValue({2024, 6, 15}, \"ISOWeekDay\")", "6");
    check("DateValue({2024, 3, 15}, \"ISOYearDay\")", "75");
    check("DateValue({2024, 12, 31}, \"ISOYearDay\")", "366");
    check("DateValue({2023, 12, 31}, \"ISOYearDay\")", "365");
    check("DateValue({2024, 6, 15}, \"Week\")", "24");
    check("DateValue({2024, 1, 1}, \"Week\")", "1");
    check("DateValue({2021, 1, 1}, \"Week\")", "53");
    check("DateValue({2024, 12, 30}, \"Week\")", "1");
    check("DateValue({2024, 6, 15}, {\"Year\", \"Month\", \"Day\"})", "{2024,6,15}");
    check("DateValue(DateObject({2024, 6, 15}), \"DayName\")", "Saturday");
    check("DateValue(\"2024-06-15\", \"MonthName\")", "June");
    check("DateValue({2024, 6, 15}, \"DayOfWeek\")", "DateValue({2024,6,15},DayOfWeek)");
    check("DateValue({2024, 12, 31}, \"WeekShort\")", "1");
    check("DateValue({2023, 1, 1}, \"WeekShort\")", "52");
    // QuarterName keeps the space, as in testWeekAndNameDateElements below
    check("DateValue({2024, 12, 31}, \"QuarterName\")", "Quarter 4");
    check("DateValue({2024, 12, 31}, \"QuarterNameShort\")", "Q4");
    check("DateValue({2024, 12, 31}, \"MonthNameInitial\")", "D");
    check("DateValue({2019, 12, 30}, \"DayNameInitial\")", "M");
  }

  @Test
  public void testDayMatchQ() {
    check("DayMatchQ({2024, 6, 15}, Saturday)", "True");
    check("DayMatchQ({2024, 6, 15}, Sunday)", "False");
    check("DayMatchQ({2024, 6, 17}, Monday)", "True");
    check("DayMatchQ({2024, 6, 15}, \"Saturday\")", "True");
    check("DayMatchQ({2024, 6, 15}, \"Weekend\")", "True");
    check("DayMatchQ({2024, 6, 17}, \"Weekend\")", "False");
    check("DayMatchQ({2024, 6, 17}, \"Weekday\")", "True");
    check("DayMatchQ({2024, 6, 15}, \"Weekday\")", "False");
    check("DayMatchQ(DateObject({2024, 6, 15}), Saturday)", "True");
    check("DayMatchQ({2024, 6, 15}, Weekend)", "DayMatchQ({2024,6,15},Weekend)");
  }

  @Test
  public void testDayRound() {
    check("DayRound({2024, 6, 15}, Saturday)", "DateObject({2024,6,15},Day)");
    check("DayRound({2024, 6, 15}, Monday)", "DateObject({2024,6,17},Day)");
    check("DayRound({2024, 6, 15}, Sunday)", "DateObject({2024,6,16},Day)");
    check("DayRound({2024, 6, 15}, Friday)", "DateObject({2024,6,21},Day)");
    check("DayRound({2024, 6, 30}, Monday)", "DateObject({2024,7,1},Day)");
    check("DayRound({2024, 12, 31}, Sunday)", "DateObject({2025,1,5},Day)");
    check("DayRound({2024, 6, 15}, \"Monday\")", "DateObject({2024,6,17},Day)");
    check("DayRound(DateObject({2024, 6, 15}), Monday)", "DateObject({2024,6,17},Day)");
    check("DayRound({2024, 3, 15})", "DateObject({2024,3,15},Day)");
    check("DayRound({2024, 3, 15, 8, 30, 0})", "DateObject({2024,3,15},Day)");
    check("DayRound({2024, 3, 15, 23, 59, 0})", "DateObject({2024,3,15},Day)");
    check("DayRound({2024})", "DateObject({2024,1,1},Day)");
    check("DayRound({2024, 2})", "DateObject({2024,2,1},Day)");
    check("DayRound(DateObject({2024, 3, 15}))", "DateObject({2024,3,15},Day)");
    check("DayRound(DateObject({2024, 7, 8, 14, 30}))", "DateObject({2024,7,8},Day,Gregorian,0.0)");
    check("DayRound(DateObject({2024, 7, 8, 14, 30}), \"Day\")",
        "DateObject({2024,7,8},Day,Gregorian,0.0)");
    check("DayRound({2024, 7, 8, 1, 0}, \"Day\")", "DateObject({2024,7,8},Day)");
    check("DayRound(DateObject({2024, 7, 6}), \"Weekday\")", "DateObject({2024,7,8},Day)");
    check("DayRound(DateObject({2024, 7, 7}), \"Weekday\")", "DateObject({2024,7,8},Day)");
    check("DayRound(DateObject({2024, 7, 12}), \"Weekday\")", "DateObject({2024,7,12},Day)");
    check("DayRound(DateObject({2024, 7, 8}), \"Weekend\")", "DateObject({2024,7,13},Day)");
    check("DayRound(DateObject({2024, 7, 7}), \"Weekend\")", "DateObject({2024,7,7},Day)");
    check("DayRound(DateObject({2024, 7, 12}), \"Weekend\")", "DateObject({2024,7,13},Day)");
  }

  @Test
  public void testNextDate() {
    check("NextDate({2024, 6, 22}, Sunday)", "DateObject({2024,6,23},Day)");
    check("NextDate({2024, 6, 22}, Monday)", "DateObject({2024,6,24},Day)");
    check("NextDate({2024, 6, 23}, Sunday)", "DateObject({2024,6,30},Day)");
    check("NextDate({2024, 6, 22}, Saturday)", "DateObject({2024,6,29},Day)");
    check("NextDate({2024, 12, 31}, Friday)", "DateObject({2025,1,3},Day)");
    check("NextDate({2024, 2, 28}, Thursday)", "DateObject({2024,2,29},Day)");
    check("NextDate({2023, 12, 28}, Monday)", "DateObject({2024,1,1},Day)");
    check("NextDate({2024, 6, 22}, \"Sunday\")", "DateObject({2024,6,23},Day)");
    check("NextDate(DateObject({2024, 6, 22}), Sunday)", "DateObject({2024,6,23},Day)");
    check("NextDate(DateObject({2024, 2, 28}), \"Day\")", "DateObject({2024,2,29},Day)");
    check("NextDate(DateObject({2024, 12, 31}), \"Day\")", "DateObject({2025,1,1},Day)");
    check("NextDate(DateObject({2024, 2, 28}), \"Month\")", "DateObject({2024,3},Month)");
    check("NextDate(DateObject({2024, 12}), \"Month\")", "DateObject({2025,1},Month)");
    check("NextDate(DateObject({2024, 2, 28}), \"Year\")", "DateObject({2025},Year)");
  }

  @Test
  public void testPreviousDate() {
    check("PreviousDate({2024, 6, 22}, Sunday)", "DateObject({2024,6,16},Day)");
    check("PreviousDate({2024, 6, 22}, Monday)", "DateObject({2024,6,17},Day)");
    check("PreviousDate({2024, 6, 23}, Sunday)", "DateObject({2024,6,16},Day)");
    check("PreviousDate({2024, 6, 22}, Saturday)", "DateObject({2024,6,15},Day)");
    check("PreviousDate({2024, 1, 1}, Friday)", "DateObject({2023,12,29},Day)");
    check("PreviousDate({2024, 3, 1}, Thursday)", "DateObject({2024,2,29},Day)");
    check("PreviousDate({2024, 6, 22}, \"Sunday\")", "DateObject({2024,6,16},Day)");
    check("PreviousDate(DateObject({2024, 6, 22}), Sunday)", "DateObject({2024,6,16},Day)");
    check("PreviousDate(DateObject({2024, 3, 1}), \"Day\")", "DateObject({2024,2,29},Day)");
    check("PreviousDate(DateObject({2024, 1, 1}), \"Month\")", "DateObject({2023,12},Month)");
    check("PreviousDate(DateObject({2024, 2, 28}), \"Year\")", "DateObject({2023},Year)");
  }

  @Test
  public void testMidDate() {
    check("MidDate(DateObject({2024, 9}))",
        "DateObject({2024,9,16,0,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate(DateObject({2024}), \"Day\")", "DateObject({2024,7,2},Day)");
    check("MidDate(DateObject({2023}), \"Day\")", "DateObject({2023,7,2},Day)");
    check("MidDate(DateObject({2024, 9, 30}, \"Week\"), \"Hour\", 2/3)",
        "DateObject({2024,10,4,16},Hour,Gregorian,0.0)");
    check(
        "MidDate({DateObject({2024, 12, 30, 18, 1, 56.77401781082153}), DateObject({2024, 4, 8, 12, 54, 47.175180435180664}), DateObject({2024, 8, 13, 22, 45, 35.52135992050171})})",
        "DateObject({2024,8,17,17,54,6.49019},Instant,Gregorian,0.0)");
    check("MidDate({DateObject({2024, 10, 1}), DateObject({2024, 10, 3})}, \"Day\")",
        "DateObject({2024,10,2},Day)");
    check(
        "MidDate({DateObject({2024, 10, 3}), DateObject({2024, 10, 5}), DateObject({2024, 10, 5})}, \"Month\")",
        "DateObject({2024,10},Month)");
    check("MidDate({DateObject({2024, 10, 1}), DateObject({2024, 10, 7}, \"Week\")})",
        "DateObject({2024,10,9,9,0,0.0},Instant,Gregorian,0.0)");
    check(
        "MidDate({DateObject({2024, 10, 1}), DateObject({2024, 10, 7}), DateObject({2024, 10, 8}), DateObject({2024, 10, 9}), DateObject({2024, 10, 10}), DateObject({2024, 10, 11}), DateObject({2024, 10, 12}), DateObject({2024, 10, 13})})",
        "DateObject({2024,10,9,9,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate({DateObject({2024, 2}), DateObject({2024, 4})}, \"Day\")",
        "DateObject({2024,3,16},Day)");
    // deviation from WMA: MidDate weights each date by its granularity, which puts the mean of a
    // mixed Month/Day list one day later
    check("MidDate({DateObject({2024, 2}), DateObject({2024, 10, 4})}, \"Day\")",
        "DateObject({2024,2,23},Day)");
    check("MidDate(DateInterval({{2019, 1, 1}, {2019, 1, 20}}), \"Day\")",
        "DateObject({2019,1,11},Day)");
    check("MidDate(DateInterval({{2019, 1, 1}, {2019, 1, 20}}))",
        "DateObject({2019,1,11,0,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate({{2024, 1, 1}, {2024, 1, 3}}, \"Day\")", "DateObject({2024,1,2},Day)");
    check("MidDate({\"1 January 2019\", \"31 December 2019\"}, \"Day\")",
        "DateObject({2019,7,2},Day)");
    check(
        "MidDate(<|\"date1\" -> DateObject({2024, 9, 22}), \"date2\" -> DateObject({2024, 4, 24}), \"date3\" -> DateObject({2024, 10, 28})|>)",
        "DateObject({2024,8,15,4,0,0.0},Instant,Gregorian,0.0)");
    check("MidDate({DateObject({2023}), DateObject({2025})}, \"Year\")", "DateObject({2024},Year)");
    check("MidDate(DateObject({2024, 10}), \"Week\")", "DateObject({2024,10,14},Week)");
    check("MidDate(DateObject({2024}), \"Day\", 0)", "DateObject({2024,1,1},Day)");
    check("MidDate(DateObject({2024}), \"Day\", 1)", "DateObject({2025,1,1},Day)");
    check("MidDate(x)", "MidDate(x)");
    check("MidDate({})", "MidDate({})");
    check("MidDate(DateObject({2024}), \"Fortnight\")",
        "MidDate(DateObject({2024},Year),Fortnight)");
  }

  @Test
  public void testDateWithinQ() {
    check("DateWithinQ(DateObject({2026}), DateObject({2026, 7, 4}))", "True");
    check("DateWithinQ(DateObject({2026, 7, 4}), DateObject({2026}))", "False");
    check("DateWithinQ(DateObject({2026, 7}), DateObject({2026, 7, 31}))", "True");
    check("DateWithinQ(DateObject({2026, 12}), DateObject({2026, 12, 31}))", "True");
    check("DateWithinQ(DateObject({2026, 7, 4}), DateObject({2026, 7, 4}))", "True");
    check("DateWithinQ(DateObject({2026, 7, 4}), DateObject({2026, 7, 4, 12, 30, 0}))", "True");
    check("DateWithinQ(DateObject({2026}), DateObject({2027, 1, 1}))", "False");
    check("DateWithinQ(DateObject({2026, 7}), DateObject({2027, 1, 1}))", "False");
    check("DateWithinQ(DateObject({2026, 7, 4}), DateObject({2026, 7, 4, 0, 0, 0}))", "True");
    check("DateWithinQ(DateObject({2026, 7, 4}), DateObject({2026, 7, 5, 0, 0, 0}))", "False");
    check("DateWithinQ(DateObject({2026, 7, 4}), \"2026-07-04\")",
        "DateWithinQ(DateObject({2026,7,4},Day),2026-07-04)");
    check("DateWithinQ(DateObject({2026}), x)", "DateWithinQ(DateObject({2026},Year),x)");
    check("DateWithinQ(TimeObject({12, 0}), DateObject({2026}))",
        "DateWithinQ(TimeObject({12,0},Minute),DateObject({2026},Year))");
    check("DateWithinQ(DateObject({2026, 7, 4, 12, 0, 0}), DateObject({2026, 7, 4, 12, 0, 0}))",
        "DateWithinQ(DateObject({2026,7,4,12,0,0},Instant,Gregorian,0.0),DateObject({2026,\n"
            + "7,4,12,0,0},Instant,Gregorian,0.0))");
    check("DateWithinQ(DateInterval({{2024, 1, 1}, {2024, 12, 31}}), DateObject({2024, 6, 15}))",
        "True");
    check("DateWithinQ(DateInterval({{2024, 1, 1}, {2024, 12, 31}}), DateObject({2025, 6, 15}))",
        "False");
    check(
        "DateWithinQ(DateInterval({{2024, 1, 1}, {2024, 12, 31}}), DateInterval({{2024, 3, 1}, {2024, 4, 1}}))",
        "True");
    check(
        "DateWithinQ(DateInterval({{2024, 1, 1}, {2024, 12, 31}}), DateInterval({{2023, 3, 1}, {2024, 4, 1}}))",
        "False");
    check("DateWithinQ(DateObject({2024, 1, 1}, \"Year\"), DateObject({2024, 5, 1}))", "True");
    check("DateWithinQ(DateObject({2024, 1, 1}, \"Year\"), DateObject({2025, 5, 1}))", "False");
    check("DateWithinQ(DateObject({2024, 1, 1}, \"Month\"), DateObject({2024, 1, 15}))", "True");
  }

  @Test
  public void testDateOverlapsQ() {
    check("DateOverlapsQ(DateObject({2020, 1, 1}), DateObject({2020, 1, 1}))", "True");
    check("DateOverlapsQ(DateObject({2020, 1, 1}), DateObject({2020, 1, 2}))", "False");
    check("DateOverlapsQ(DateObject({2020, 1}), DateObject({2020, 2}))", "False");
    check("DateOverlapsQ(DateObject({2020}), DateObject({2021, 1}))", "False");
    check("DateOverlapsQ(DateObject({2020}), DateObject({2020, 6, 1}))", "True");
    check(
        "DateOverlapsQ(DateInterval({{2020, 1, 1}, {2020, 6, 1}}), DateInterval({{2020, 3, 1}, {2020, 9, 1}}))",
        "True");
    check(
        "DateOverlapsQ(DateInterval({{2020, 1, 1}, {2020, 6, 1}}), DateInterval({{2020, 7, 1}, {2020, 9, 1}}))",
        "False");
    check(
        "DateOverlapsQ(DateInterval({{2020, 1, 1}, {2020, 6, 1}}), DateInterval({{2020, 6, 1}, {2020, 9, 1}}))",
        "True");
    check("DateOverlapsQ(DateObject({2020, 3, 15}), DateInterval({{2020, 1, 1}, {2020, 6, 1}}))",
        "True");
    check("DateOverlapsQ(5, DateObject({2020}))", "DateOverlapsQ(5,DateObject({2020},Year))");
    check("DateOverlapsQ(DateObject({2024, 1, 1}, \"Month\"), DateObject({2024, 1, 15}))", "True");
    check("DateOverlapsQ(DateObject({2024, 1, 1}, \"Year\"), DateObject({2024, 6, 1}, \"Month\"))",
        "True");
  }

  @Test
  public void testTimeZoneOffset() {
    check("TimeZoneOffset()", "0.0");
    check("$TimeZone", "0.0");
    check("TimeZoneOffset(5)", "5");
    check("TimeZoneOffset(2.5)", "2.5");
    check("TimeZoneOffset(5, 2)", "3");
    check("TimeZoneOffset(-3, 5/2)", "-11/2");
    check("TimeZoneOffset(1, 2, 3)", "-1");
    check("TimeZoneOffset(x)", "TimeZoneOffset(x)");
  }

  @Test
  public void testFromJulianDate() {
    check("FromJulianDate(2460000)", "DateObject({2023,2,24,12,0,0},Instant,Gregorian,0.0)");
    check("FromJulianDate(0)", "DateObject({-4714,11,24,12,0,0},Instant,Gregorian,0.0)");
    check("FromJulianDate(1721425.5)", "DateObject({1,1,1,0,0,0.0},Instant,Gregorian,0.0)");
    check("FromJulianDate(1721059.5)", "DateObject({-1,1,1,0,0,0.0},Instant,Gregorian,0.0)");
    check("FromJulianDate(2460000.5)", "DateObject({2023,2,25,0,0,0.0},Instant,Gregorian,0.0)");
    check("FromJulianDate(2460000.)", "DateObject({2023,2,24,12,0,0.0},Instant,Gregorian,0.0)");
    check("FromJulianDate(2460000 + 1/4)", "DateObject({2023,2,24,18,0,0},Instant,Gregorian,0.0)");
    check("FromJulianDate(x)", "FromJulianDate(x)");
    check("JulianDate(DateObject({2023, 2, 24, 12, 0, 0}))", "2.46*10^6");
    check("JulianDate(DateObject({2023, 2, 24}))", "2.46*10^6");
    check("FromJulianDate(JulianDate(DateObject({2023, 2, 24, 6, 30, 15})))",
        "DateObject({2023,2,24,6,30,15.00001},Instant,Gregorian,0.0)");
  }

  @Test
  public void testDateSelect() {
    check(
        "DateSelect({DateObject({2020, 1, 1}), DateObject({2020, 6, 15}), DateObject({2021, 3, 1})}, DateValue(#, \"Year\") == 2020 &)",
        "{DateObject({2020,1,1},Day),DateObject({2020,6,15},Day)}");
    check(
        "DateSelect({DateObject({2020, 1, 1}), DateObject({2020, 1, 2}), DateObject({2020, 1, 3})}, #(\"DayName\") == Wednesday &)",
        "{DateObject({2020,1,1},Day)}");
    check("DateSelect(DateInterval({{2020, 1, 1}, {2020, 1, 5}}), DateValue(#, \"Day\") < 3 &)",
        "{DateObject({2020,1,1},Day),DateObject({2020,1,2},Day)}");
    check("DateSelect(DateInterval({{2020, 3, 1}, {2020, 3, 10}}), #(\"DayName\") == Monday &)",
        "{DateObject({2020,3,2},Day),DateObject({2020,3,9},Day)}");
    check("DateSelect({1, 2, 3}, # > 1 &)", "{2,3}");
    check("DateSelect({DateObject({2020, 1, 1})}, DateValue(#, \"Year\") == 2099 &)", "{}");
  }

  @Test
  public void testCalendarConvert() {
    check("CalendarConvert(DateObject({2024, 3, 14}), \"Julian\")",
        "DateObject({2024,3,1},Day,Julian)");
    check("CalendarConvert(DateObject({2000, 1, 1}), \"Julian\")",
        "DateObject({1999,12,19},Day,Julian)");
    check("CalendarConvert(DateObject({1900, 3, 1}), \"Julian\")",
        "DateObject({1900,2,17},Day,Julian)");
    check("CalendarConvert(DateObject({2100, 3, 1}), \"Julian\")",
        "DateObject({2100,2,16},Day,Julian)");
    check("CalendarConvert(DateObject({1, 1, 1}), \"Julian\")", "DateObject({1,1,3},Day,Julian)");
    check("CalendarConvert(DateObject({1582, 10, 15}), \"Julian\")",
        "DateObject({1582,10,5},Day,Julian)");
    check("CalendarConvert(DateObject({2024, 3, 14}), \"Gregorian\")",
        "DateObject({2024,3,14},Day)");
    check("CalendarConvert(DateObject({2024, 3, 14}, \"Julian\"), \"Gregorian\")",
        "CalendarConvert(DateObject({2024,3,14},Julian),Gregorian)");
  }

  @Test
  public void testFromDateString() {
    check("FromDateString(\"2026-07-15\")", "DateObject({2026,7,15},Day)");
    check("FromDateString(\"July 4, 1776\")", "DateObject({1776,7,4},Day)");
    check("FromDateString(\"Jan 1 2000\")", "DateObject({2000,1,1},Day)");
    check("FromDateString(\"4th July 1776\")", "DateObject({1776,7,4},Day)");
    check("FromDateString(\"Jan 8th, 2022\")", "DateObject({2022,1,8},Day)");
    check("FromDateString(\"2026-07-15 14:30:00\")",
        "DateObject({2026,7,15,14,30,0},Instant,Gregorian,0.0)");
    check("FromDateString(\"2026\")", "DateObject({2026},Year)");
    check("DateObject(\"July 4, 1776\")", "DateObject({1776,7,4},Day)");
  }

  @Test
  public void testDateTimeOrdering() {
    check("DateObject({2026, 1, 1}) < DateObject({2026, 6, 1})", "True");
    check("DateObject({2026, 6, 1}) > DateObject({2026, 1, 1})", "True");
    check("DateObject({2026, 6, 1}) < DateObject({2026, 1, 1})", "False");
    check("DateObject({2026, 1, 1}) <= DateObject({2026, 1, 1})", "True");
    check("DateObject({2026, 1, 1, 10, 30, 0}) < DateObject({2026, 1, 1, 14, 0, 0})", "True");
    check("TimeObject({10, 0, 0}) < TimeObject({14, 0, 0})", "True");
    check("TimeObject({14, 0, 0}) <= TimeObject({10, 0, 0})", "False");
  }

  @Test
  public void testWeekAndNameDateElements() {
    check("DateString({2024, 12, 31}, \"ISOWeekDate\")", "2025-W01-2");
    check("DateString({2024, 1, 5}, \"ISOWeekDate\")", "2024-W01-5");
    check("DateString({2023, 1, 1}, \"ISOWeekDate\")", "2022-W52-7");
    check("DateString({2021, 1, 3}, \"ISOWeekDate\")", "2020-W53-7");
    check("DateString({2019, 12, 30}, \"ISOWeekDate\")", "2020-W01-1");
    check("DateString({2024, 12, 31}, \"Week\")", "01");
    // "WeekShort" is the unpadded week number, "Week" is the padded one
    check("DateString({2024, 12, 31}, \"WeekShort\")", "1");
    check("DateString({2020, 12, 31}, \"Week\")", "53");
    check("DateString({2024, 6, 15}, \"Week\")", "24");
    check("DateString({2024, 12, 31}, \"ISOYear\")", "2024");
    check("DateString({2023, 1, 1}, \"ISOYear\")", "2023");
    check("DateString({2024, 12, 31}, \"QuarterName\")", "Quarter 4");
    check("DateString({2024, 1, 5}, \"QuarterNameShort\")", "Q1");
    check("DateString({2024, 12, 31}, \"MonthNameInitial\")", "D");
    check("DateString({2024, 12, 31}, \"DayNameInitial\")", "T");
    check("DateString({2024, 1, 5}, \"DayNameInitial\")", "F");
    check("DateString({2024, 12, 31}, {\"ISOWeekDate\", \" \", \"QuarterNameShort\"})",
        "2025-W01-2 Q4");
    check("DateString({2024, 1, 5}, {\"MonthNameInitial\", \"DayNameInitial\"})", "JF");
  }
}
