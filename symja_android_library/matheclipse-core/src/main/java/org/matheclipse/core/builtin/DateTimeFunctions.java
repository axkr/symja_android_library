package org.matheclipse.core.builtin;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateGranularity;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Date and time functions.
 *
 * <p>
 * All calculations are done on a {@link LocalDateTime} in the proleptic Gregorian calendar without
 * daylight saving time corrections and without leap seconds.
 * </p>
 */
public class DateTimeFunctions {

  /** The epoch of {@link S#AbsoluteTime}. */
  private static final LocalDateTime EPOCH_1900 = LocalDateTime.of(1900, 1, 1, 0, 0);

  /** The epoch of {@link S#UnixTime}. */
  private static final LocalDateTime EPOCH_1970 = LocalDateTime.of(1970, 1, 1, 0, 0);

  /**
   * Julian day number of 1970-01-01T00:00. Julian day 0 is noon of November 24th, 4714 BCE in the
   * proleptic Gregorian calendar.
   */
  private static final double JULIAN_DATE_EPOCH_1970 = 2440587.5;

  private static final long NANOS_PER_DAY = 86400_000_000_000L;

  /** Indexed by {@link DayOfWeek#getValue()} minus one. */
  private static final IBuiltInSymbol[] WEEKDAY_SYMBOLS = new IBuiltInSymbol[] { //
      S.Monday, S.Tuesday, S.Wednesday, S.Thursday, S.Friday, S.Saturday, S.Sunday};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.$TimeZone.setEvaluator(new TimeZoneConstant());

      S.AbsoluteTime.setEvaluator(new AbsoluteTime());
      S.CalendarConvert.setEvaluator(new CalendarConvert());
      S.DateBounds.setEvaluator(new DateBounds());
      S.DateDifference.setEvaluator(new DateDifference());
      S.DateInterval.setEvaluator(new DateInterval());
      S.DateList.setEvaluator(new DateList());
      S.DateObject.setEvaluator(new DateObject());
      S.DateObjectQ.setEvaluator(new DateObjectQ());
      S.DateOverlapsQ.setEvaluator(new DateOverlapsQ());
      S.DatePlus.setEvaluator(new DatePlus());
      S.DateRange.setEvaluator(new DateRange());
      S.DateSelect.setEvaluator(new DateSelect());
      S.DateString.setEvaluator(new DateString());
      S.DateValue.setEvaluator(new DateValue());
      S.DateWithinQ.setEvaluator(new DateWithinQ());
      S.Dated.setEvaluator(new Dated());
      S.DayCount.setEvaluator(new DayCount());
      S.DayMatchQ.setEvaluator(new DayMatchQ());
      S.DayName.setEvaluator(new DayName());
      S.DayPlus.setEvaluator(new DayPlus());
      S.DayRange.setEvaluator(new DayRange());
      S.DayRound.setEvaluator(new DayRound());
      S.FromAbsoluteTime.setEvaluator(new FromAbsoluteTime());
      S.FromDateString.setEvaluator(new FromDateString());
      S.FromJulianDate.setEvaluator(new FromJulianDate());
      S.FromUnixTime.setEvaluator(new FromUnixTime());
      S.JulianDate.setEvaluator(new JulianDate());
      S.LeapYearQ.setEvaluator(new LeapYearQ());
      S.MaxDate.setEvaluator(new MaxDate());
      S.MidDate.setEvaluator(new MidDate());
      S.MinDate.setEvaluator(new MinDate());
      S.NextDate.setEvaluator(new NextDate());
      S.PreviousDate.setEvaluator(new PreviousDate());
      S.TimeObject.setEvaluator(new TimeObject());
      S.TimeZoneOffset.setEvaluator(new TimeZoneOffset());
      S.UnixTime.setEvaluator(new UnixTime());
      S.Yesterday.setEvaluator(new Yesterday());
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  // ==================================================================================
  // date interpretation
  // ==================================================================================

  /**
   * The interpretation of an arbitrary expression as a date.
   *
   * <p>
   * Beside the {@link DateObjectExpr} - which has the granularity implied by the input and
   * therefore a truncated instant - the untruncated {@link #instant} is kept, because
   * {@link S#DateList}, {@link S#DateString} and {@link S#AbsoluteTime} must see for example the
   * <code>12:00</code> of <code>{1991, 6, 6.5}</code> even though the date list has only three
   * elements.
   * </p>
   */
  static final class DateSpec {
    /** the date object with the granularity implied by the input */
    final DateObjectExpr dateObject;

    /** the untruncated instant */
    final LocalDateTime instant;

    /** number of elements if the input was a date list, otherwise <code>0</code> */
    final int listSize;

    /** <code>true</code> if a component of the input was an inexact number */
    final boolean real;

    private DateSpec(DateObjectExpr dateObject, LocalDateTime instant, int listSize, boolean real) {
      this.dateObject = dateObject;
      this.instant = instant;
      this.listSize = listSize;
      this.real = real;
    }
  }

  /**
   * Interpret <code>expr</code> as a date in any of the standard date notations:
   * {@link DateObjectExpr}, a date list, a number of seconds since 1900, a date string or a
   * <code>{"string", {"element",...}}</code> pair.
   *
   * @return <code>null</code> if <code>expr</code> is not a date specification
   */
  static DateSpec dateSpec(IExpr expr) {
    return dateSpec(expr, null);
  }

  /**
   * @param granularity forces the granularity of the resulting date object, may be
   *        <code>null</code>
   */
  static DateSpec dateSpec(IExpr expr, DateGranularity granularity) {
    if (expr instanceof DateObjectExpr) {
      DateObjectExpr dateObject = (DateObjectExpr) expr;
      if (granularity != null) {
        dateObject = dateObject.withGranularity(granularity);
      }
      return new DateSpec(dateObject, dateObject.start(), 0, dateObject.isRealSeconds());
    }
    if (expr instanceof TimeObjectExpr) {
      return null;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.isEmpty()) {
        // DateList({}) is the current date
        LocalDateTime now = LocalDateTime.now();
        DateGranularity gran = granularity == null ? DateGranularity.INSTANT : granularity;
        return new DateSpec(DateObjectExpr.newInstance(gran.truncate(now), gran), now, 0, true);
      }
      if (list.argSize() == 2 && list.arg1().isString() && list.arg2().isList()) {
        LocalDateTime dateTime = parseFormatted(list.arg1().toString(), (IAST) list.arg2());
        if (dateTime == null) {
          return null;
        }
        DateGranularity gran = granularity == null ? DateGranularity.INSTANT : granularity;
        return new DateSpec(DateObjectExpr.newInstance(gran.truncate(dateTime), gran,
            DateObjectExpr.GREGORIAN, gran.isDateOnly() ? S.None : F.CD0, true), dateTime, 0, true);
      }
      boolean[] real = new boolean[1];
      LocalDateTime dateTime = dateTimeFromList(list, real);
      if (dateTime == null) {
        return null;
      }
      DateGranularity gran =
          granularity == null ? DateGranularity.ofListSize(list.argSize()) : granularity;
      if (gran == null) {
        return null;
      }
      DateObjectExpr dateObject = DateObjectExpr.newInstance(gran.truncate(dateTime), gran,
          DateObjectExpr.GREGORIAN, gran.isDateOnly() ? S.None : F.CD0, real[0]);
      return new DateSpec(dateObject, dateTime, list.argSize(), real[0]);
    }
    if (expr.isString()) {
      ParsedDateString parsed = parseDateString(expr.toString());
      if (parsed == null) {
        return null;
      }
      DateGranularity gran = granularity == null ? parsed.granularity : granularity;
      DateObjectExpr dateObject = DateObjectExpr.newInstance(gran.truncate(parsed.dateTime), gran,
          DateObjectExpr.GREGORIAN, gran.isDateOnly() ? S.None : F.CD0, false);
      return new DateSpec(dateObject, parsed.dateTime, 0, false);
    }
    if (expr.isNumber() && expr.isReal()) {
      double seconds = expr.evalf();
      LocalDateTime dateTime = plusSeconds(EPOCH_1900, seconds);
      DateGranularity gran = granularity == null ? DateGranularity.INSTANT : granularity;
      return new DateSpec(
          DateObjectExpr.newInstance(gran.truncate(dateTime), gran, DateObjectExpr.GREGORIAN,
              gran.isDateOnly() ? S.None : F.CD0, expr.isInexactNumber()),
          dateTime, 0, expr.isInexactNumber());
    }
    return null;
  }

  /** Convenience wrapper which only returns the {@link DateObjectExpr}. */
  static DateObjectExpr toDateObject(IExpr expr) {
    DateSpec spec = dateSpec(expr);
    return spec == null ? null : spec.dateObject;
  }

  /**
   * Normalize a date list <code>{y}</code> ... <code>{y,m,d,h,mi,s}</code> to a
   * {@link LocalDateTime}.
   *
   * <p>
   * Out of range components are rolled over, including <code>0</code> and negative values. The day,
   * hour, minute and second components may be fractional.
   * </p>
   *
   * @param real set to <code>true</code> if one of the components was an inexact number
   * @return <code>null</code> if <code>list</code> is not a date list
   */
  static LocalDateTime dateTimeFromList(IAST list, boolean[] real) {
    int argSize = list.argSize();
    if (argSize < 1 || argSize > 6) {
      return null;
    }
    int displayYear = list.arg1().toIntDefault();
    if (!F.isPresent(displayYear)) {
      return null;
    }
    int month = 1;
    if (argSize >= 2) {
      month = list.arg2().toIntDefault();
      if (!F.isPresent(month)) {
        return null;
      }
    }
    double[] rest = new double[] {1.0, 0.0, 0.0, 0.0};
    for (int i = 3; i <= argSize; i++) {
      IExpr arg = list.get(i);
      double value = arg.toDoubleDefault(Double.NaN);
      if (Double.isNaN(value)) {
        return null;
      }
      if (arg.isInexactNumber()) {
        real[0] = true;
      }
      rest[i - 3] = value;
    }
    return dateTimeOf(DateObjectExpr.toJavaYear(displayYear), month, rest[0], rest[1], rest[2],
        rest[3]);
  }

  /**
   * Build a {@link LocalDateTime} from possibly out of range and possibly fractional components.
   */
  static LocalDateTime dateTimeOf(int year, int month, double day, double hour, double minute,
      double second) {
    long monthIndex = (long) year * 12L + (month - 1L);
    int normYear = (int) Math.floorDiv(monthIndex, 12L);
    int normMonth = (int) Math.floorMod(monthIndex, 12L) + 1;
    long dayInt = (long) Math.floor(day);
    long hourInt = (long) Math.floor(hour);
    long minuteInt = (long) Math.floor(minute);
    long secondInt = (long) Math.floor(second);
    double fractionSeconds = (day - dayInt) * 86400.0 //
        + (hour - hourInt) * 3600.0 //
        + (minute - minuteInt) * 60.0 //
        + (second - secondInt);
    return LocalDateTime.of(normYear, normMonth, 1, 0, 0) //
        .plusDays(dayInt - 1) //
        .plusHours(hourInt) //
        .plusMinutes(minuteInt) //
        .plusSeconds(secondInt) //
        .plusNanos(Math.round(fractionSeconds * 1.0e9));
  }

  /** Add a possibly fractional number of seconds to a date. */
  static LocalDateTime plusSeconds(LocalDateTime dateTime, double seconds) {
    long whole = (long) Math.floor(seconds);
    long nanos = Math.round((seconds - whole) * 1.0e9);
    return dateTime.plusSeconds(whole).plusNanos(nanos);
  }

  // ==================================================================================
  // date string parsing
  // ==================================================================================

  private static final class ParsedDateString {
    final LocalDateTime dateTime;
    final DateGranularity granularity;

    private ParsedDateString(LocalDateTime dateTime, DateGranularity granularity) {
      this.dateTime = dateTime;
      this.granularity = granularity;
    }
  }

  private static final String[] MONTH_NAMES = new String[] {"january", "february", "march", "april",
      "may", "june", "july", "august", "september", "october", "november", "december"};

  /** Map a full or abbreviated english month name to <code>1..12</code>, or <code>-1</code>. */
  private static int monthOfName(String name) {
    String lower = name.toLowerCase(Locale.US);
    for (int i = 0; i < MONTH_NAMES.length; i++) {
      if (MONTH_NAMES[i].equals(lower) || MONTH_NAMES[i].substring(0, 3).equals(lower)) {
        return i + 1;
      }
    }
    return -1;
  }

  /**
   * Parse a date string in one of the commonly used notations.
   *
   * @return <code>null</code> if the string cannot be interpreted as a date
   */
  private static ParsedDateString parseDateString(String str) {
    String text = str.trim();
    if (text.isEmpty()) {
      return null;
    }
    // split off a time of day
    LocalTime time = null;
    java.util.regex.Matcher timeMatcher = java.util.regex.Pattern
        .compile("[T ](\\d{1,2}):(\\d{2})(?::(\\d{2}(?:\\.\\d+)?))?\\s*$").matcher(text);
    if (timeMatcher.find()) {
      int hour = Integer.parseInt(timeMatcher.group(1));
      int minute = Integer.parseInt(timeMatcher.group(2));
      double second = timeMatcher.group(3) == null ? 0.0 : Double.parseDouble(timeMatcher.group(3));
      int wholeSecond = (int) second;
      int nanos = (int) Math.round((second - wholeSecond) * 1.0e9);
      if (hour > 23 || minute > 59 || wholeSecond > 59) {
        return null;
      }
      time = LocalTime.of(hour, minute, wholeSecond, nanos);
      text = text.substring(0, timeMatcher.start()).trim();
    }

    LocalDate date = parseDateOnly(text);
    if (date == null) {
      return null;
    }
    if (time != null) {
      return new ParsedDateString(LocalDateTime.of(date, time), DateGranularity.INSTANT);
    }
    if (text.matches("-?\\d{1,6}")) {
      return new ParsedDateString(date.atStartOfDay(), DateGranularity.YEAR);
    }
    if (text.matches("-?\\d{1,6}[-/]\\d{1,2}")) {
      return new ParsedDateString(date.atStartOfDay(), DateGranularity.MONTH);
    }
    return new ParsedDateString(date.atStartOfDay(), DateGranularity.DAY);
  }

  /** Parse the date part of a date string. */
  private static LocalDate parseDateOnly(String text) {
    try {
      // 2026, 2026-07, 2026-07-15
      java.util.regex.Matcher m = java.util.regex.Pattern
          .compile("^(-?\\d{1,6})(?:[-/](\\d{1,2})(?:[-/](\\d{1,2}))?)?$").matcher(text);
      if (m.matches()) {
        int year = Integer.parseInt(m.group(1));
        int month = m.group(2) == null ? 1 : Integer.parseInt(m.group(2));
        int day = m.group(3) == null ? 1 : Integer.parseInt(m.group(3));
        return LocalDate.of(DateObjectExpr.toJavaYear(year), month, day);
      }
      // 31/10/1991 or 31/10/91 - day first, because a leading four digit group was matched above
      m = java.util.regex.Pattern.compile("^(\\d{1,2})[-./](\\d{1,2})[-./](\\d{2}|\\d{4})$")
          .matcher(text);
      if (m.matches()) {
        int day = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2));
        int year = expandYear(Integer.parseInt(m.group(3)), m.group(3).length());
        return LocalDate.of(year, month, day);
      }
      // 6 June 1991, 4th July 1776, 1 Feb 2024
      m = java.util.regex.Pattern
          .compile("^(\\d{1,2})(?:st|nd|rd|th)?\\.?\\s+([A-Za-z]+)\\.?,?\\s+(-?\\d{1,6})$")
          .matcher(text);
      if (m.matches()) {
        int month = monthOfName(m.group(2));
        if (month > 0) {
          return LocalDate.of(DateObjectExpr.toJavaYear(Integer.parseInt(m.group(3))), month,
              Integer.parseInt(m.group(1)));
        }
        return null;
      }
      // July 4, 1776 / Jan 8th, 2022 / Feb 1 2024 / March 5, 2025
      m = java.util.regex.Pattern
          .compile("^([A-Za-z]+)\\.?\\s+(\\d{1,2})(?:st|nd|rd|th)?,?\\s+(-?\\d{1,6})$")
          .matcher(text);
      if (m.matches()) {
        int month = monthOfName(m.group(1));
        if (month > 0) {
          return LocalDate.of(DateObjectExpr.toJavaYear(Integer.parseInt(m.group(3))), month,
              Integer.parseInt(m.group(2)));
        }
        return null;
      }
    } catch (RuntimeException rex) {
      return null;
    }
    return null;
  }

  /** Expand a two digit year. */
  private static int expandYear(int year, int digits) {
    if (digits > 2) {
      return year;
    }
    return year < 50 ? 2000 + year : 1900 + year;
  }

  /**
   * Parse a date string with an explicit list of format elements, for example
   * <code>{"01/02/03", {"Day", "Month", "YearShort"}}</code>.
   *
   * @return <code>null</code> if the string does not match the elements
   */
  private static LocalDateTime parseFormatted(String str, IAST elements) {
    int year = 1900;
    int month = 1;
    int day = 1;
    int hour = 0;
    int minute = 0;
    int second = 0;
    boolean yearSeen = false;
    int position = 0;
    String text = str;
    for (int i = 1; i < elements.size(); i++) {
      IExpr element = elements.get(i);
      if (!element.isString()) {
        return null;
      }
      String name = element.toString();
      if (isSeparatorElement(name)) {
        // literal separator - skip it if it is there
        while (position < text.length() && !Character.isLetterOrDigit(text.charAt(position))) {
          position++;
        }
        continue;
      }
      // skip any separator characters before a value
      while (position < text.length() && !Character.isLetterOrDigit(text.charAt(position))) {
        position++;
      }
      if (position >= text.length()) {
        return null;
      }
      if (Character.isDigit(text.charAt(position))) {
        int end = position;
        while (end < text.length() && Character.isDigit(text.charAt(end))) {
          end++;
        }
        String digits = text.substring(position, end);
        int value = Integer.parseInt(digits);
        position = end;
        switch (name) {
          case "Year":
            year = value;
            yearSeen = true;
            break;
          case "YearShort":
            year = expandYear(value, digits.length());
            yearSeen = true;
            break;
          case "Month":
          case "MonthShort":
            month = value;
            break;
          case "Day":
          case "DayShort":
            day = value;
            break;
          case "Hour":
          case "Hour24":
          case "Hour12":
          case "HourShort":
            hour = value;
            break;
          case "Minute":
          case "MinuteShort":
            minute = value;
            break;
          case "Second":
          case "SecondShort":
            second = value;
            break;
          default:
            return null;
        }
      } else {
        int end = position;
        while (end < text.length() && Character.isLetter(text.charAt(end))) {
          end++;
        }
        String word = text.substring(position, end);
        position = end;
        switch (name) {
          case "MonthName":
          case "MonthNameShort":
            month = monthOfName(word);
            if (month < 0) {
              return null;
            }
            break;
          case "DayName":
          case "DayNameShort":
            break;
          case "AMPM":
          case "AMPMLowerCase":
            if (word.equalsIgnoreCase("PM") && hour < 12) {
              hour += 12;
            } else if (word.equalsIgnoreCase("AM") && hour == 12) {
              hour = 0;
            }
            break;
          default:
            return null;
        }
      }
    }
    if (!yearSeen) {
      year = LocalDateTime.now().getYear();
    }
    try {
      return dateTimeOf(DateObjectExpr.toJavaYear(year), month, day, hour, minute, second);
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /** <code>true</code> for a format element which is not a date element but a literal separator. */
  private static boolean isSeparatorElement(String name) {
    return dateElementIndex(name) < 0;
  }

  // ==================================================================================
  // date elements
  // ==================================================================================

  /**
   * All supported date element names. The index into this array is used by
   * {@link #dateElement(int, LocalDateTime, DateObjectExpr, boolean)}.
   */
  private static final String[] DATE_ELEMENTS = new String[] { //
      "Year", "YearShort", "Quarter", "QuarterName", "QuarterNameShort", //
      "Month", "MonthShort", "MonthName", "MonthNameShort", "MonthNameInitial", //
      "Day", "DayShort", "DayName", "DayNameShort", "DayNameInitial", //
      "Hour", "HourShort", "Hour12", "Hour12Short", "Hour24", "Hour24Short", //
      "AMPM", "AMPMLowerCase", //
      "Minute", "MinuteShort", "Second", "SecondShort", "Millisecond", "MillisecondShort", //
      "Week", "WeekShort", "ISOWeek", "ISOWeekYear", "ISOYear", "ISOWeekDay", //
      "ISOYearDay", "ISOYearDayShort", "DayOfYear", "ISOWeekDate", "Granularity"};

  private static int dateElementIndex(String name) {
    for (int i = 0; i < DATE_ELEMENTS.length; i++) {
      if (DATE_ELEMENTS[i].equals(name)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Compute a date element.
   *
   * @param asString <code>true</code> for the {@link S#DateString} rendering - zero padded strings
   *        - and <code>false</code> for the {@link S#DateValue} rendering - integers, symbols and
   *        strings
   * @return {@link F#NIL} if <code>name</code> is not a supported date element
   */
  public static IExpr dateElement(String name, LocalDateTime date, DateObjectExpr dateObject,
      boolean asString) {
    int index = dateElementIndex(name);
    if (index < 0) {
      return F.NIL;
    }
    int quarter = (date.getMonthValue() - 1) / 3 + 1;
    switch (name) {
      case "Year":
        return integerOrPadded(DateObjectExpr.toDisplayYear(date.getYear()), 4, asString);
      case "YearShort":
        return integerOrPadded(Math.floorMod(date.getYear(), 100), 2, asString);
      case "Quarter":
        return integerOrPadded(quarter, 1, asString);
      case "QuarterName":
        return F.stringx("Quarter " + quarter);
      case "QuarterNameShort":
        return F.stringx("Q" + quarter);
      case "Month":
        return integerOrPadded(date.getMonthValue(), 2, asString);
      case "MonthShort":
        return integerOrPadded(date.getMonthValue(), 1, asString);
      case "MonthName":
        return F.stringx(date.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
      case "MonthNameShort":
        return F.stringx(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.US));
      case "MonthNameInitial":
        return F.stringx(date.getMonth().getDisplayName(TextStyle.NARROW, Locale.US));
      case "Day":
        return integerOrPadded(date.getDayOfMonth(), 2, asString);
      case "DayShort":
        return integerOrPadded(date.getDayOfMonth(), 1, asString);
      case "DayName":
        return asString ? F.stringx(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US))
            : weekdaySymbol(date.getDayOfWeek());
      case "DayNameShort":
        return F.stringx(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
      case "DayNameInitial":
        return F.stringx(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.US));
      case "Hour":
      case "Hour24":
        return integerOrPadded(date.getHour(), 2, asString);
      case "HourShort":
      case "Hour24Short":
        return integerOrPadded(date.getHour(), 1, asString);
      case "Hour12":
        return integerOrPadded(hour12(date.getHour()), 2, asString);
      case "Hour12Short":
        return integerOrPadded(hour12(date.getHour()), 1, asString);
      case "AMPM":
        return F.stringx(date.getHour() < 12 ? "AM" : "PM");
      case "AMPMLowerCase":
        return F.stringx(date.getHour() < 12 ? "am" : "pm");
      case "Minute":
        return integerOrPadded(date.getMinute(), 2, asString);
      case "MinuteShort":
        return integerOrPadded(date.getMinute(), 1, asString);
      case "Second":
        return integerOrPadded(date.getSecond(), 2, asString);
      case "SecondShort":
        return integerOrPadded(date.getSecond(), 1, asString);
      case "Millisecond":
        return integerOrPadded(date.getNano() / 1_000_000, 3, asString);
      case "MillisecondShort":
        return integerOrPadded(date.getNano() / 1_000_000, 1, asString);
      case "Week":
      case "ISOWeek":
        return integerOrPadded(date.get(WeekFields.ISO.weekOfWeekBasedYear()), 2, asString);
      case "WeekShort":
        return integerOrPadded(date.get(WeekFields.ISO.weekOfWeekBasedYear()), 1, asString);
      case "ISOWeekYear":
        return integerOrPadded(date.get(WeekFields.ISO.weekBasedYear()), 4, asString);
      case "ISOYear":
        return integerOrPadded(DateObjectExpr.toDisplayYear(date.getYear()), 4, asString);
      case "ISOWeekDay":
        return integerOrPadded(date.getDayOfWeek().getValue(), 1, asString);
      case "ISOYearDay":
        return integerOrPadded(date.getDayOfYear(), 3, asString);
      case "ISOYearDayShort":
      case "DayOfYear":
        return integerOrPadded(date.getDayOfYear(), 1, asString);
      case "ISOWeekDate":
        return F.stringx(
            String.format(Locale.US, "%04d-W%02d-%d", date.get(WeekFields.ISO.weekBasedYear()),
                date.get(WeekFields.ISO.weekOfWeekBasedYear()), date.getDayOfWeek().getValue()));
      case "Granularity":
        return F.stringx(dateObject == null ? DateGranularity.INSTANT.getName()
            : dateObject.getGranularity().getName());
      default:
        return F.NIL;
    }
  }

  private static int hour12(int hour) {
    int result = hour % 12;
    return result == 0 ? 12 : result;
  }

  private static IExpr integerOrPadded(int value, int digits, boolean asString) {
    if (!asString) {
      return F.ZZ(value);
    }
    if (value < 0) {
      return F.stringx(Integer.toString(value));
    }
    StringBuilder buf = new StringBuilder(Integer.toString(value));
    while (buf.length() < digits) {
      buf.insert(0, '0');
    }
    return F.stringx(buf.toString());
  }

  static IBuiltInSymbol weekdaySymbol(DayOfWeek dayOfWeek) {
    return WEEKDAY_SYMBOLS[dayOfWeek.getValue() - 1];
  }

  /**
   * Map a weekday symbol or a weekday name string to a {@link DayOfWeek}.
   *
   * @return <code>null</code> if <code>expr</code> is not a weekday specification
   */
  static DayOfWeek toDayOfWeek(IExpr expr) {
    for (int i = 0; i < WEEKDAY_SYMBOLS.length; i++) {
      if (expr == WEEKDAY_SYMBOLS[i]) {
        return DayOfWeek.of(i + 1);
      }
    }
    if (expr.isString()) {
      String name = expr.toString();
      for (int i = 0; i < WEEKDAY_SYMBOLS.length; i++) {
        if (WEEKDAY_SYMBOLS[i].getSymbolName().equalsIgnoreCase(name)) {
          return DayOfWeek.of(i + 1);
        }
      }
    }
    return null;
  }

  // ==================================================================================
  // date formatting
  // ==================================================================================

  /** The default date format, i.e. <code>Mon 1 Jan 1900 00:00:00</code>. */
  private static final String[] DEFAULT_DATE_TIME_FORMAT = new String[] {"DayNameShort", " ",
      "DayShort", " ", "MonthNameShort", " ", "Year", " ", "Hour24", ":", "Minute", ":", "Second"};

  /** The default format for a date object which carries no time of day. */
  private static final String[] DEFAULT_DATE_FORMAT =
      new String[] {"DayNameShort", " ", "DayShort", " ", "MonthNameShort", " ", "Year"};

  /**
   * The named {@link S#DateString} formats which expand to a list of elements. Single date elements
   * such as <code>"Year"</code> are handled by {@link #dateElement} directly.
   */
  private static String[] namedFormat(String name) {
    switch (name) {
      case "ISODate":
        return new String[] {"Year", "-", "Month", "-", "Day"};
      case "ISOTime":
        return new String[] {"Hour24", ":", "Minute", ":", "Second"};
      case "ISODateTime":
        return new String[] {"Year", "-", "Month", "-", "Day", "T", "Hour24", ":", "Minute", ":",
            "Second"};
      case "Date":
        return new String[] {"DayName", " ", "DayShort", " ", "MonthName", " ", "Year"};
      case "DateShort":
        return DEFAULT_DATE_FORMAT;
      case "Time":
        return new String[] {"Hour24", ":", "Minute", ":", "Second"};
      case "DateTime":
        return new String[] {"DayName", " ", "DayShort", " ", "MonthName", " ", "Year", " ",
            "Hour24", ":", "Minute", ":", "Second"};
      case "DateTimeShort":
        return DEFAULT_DATE_TIME_FORMAT;
      default:
        return null;
    }
  }

  /**
   * Format <code>date</code> with a list of format elements. Elements which are not date elements
   * are concatenated literally.
   */
  private static String formatDate(LocalDateTime date, DateObjectExpr dateObject, IAST elements) {
    StringBuilder buf = new StringBuilder();
    for (int i = 1; i < elements.size(); i++) {
      IExpr element = elements.get(i);
      if (element.isString()) {
        appendFormatElement(buf, date, dateObject, element.toString());
      } else {
        buf.append(element.toString());
      }
    }
    return buf.toString();
  }

  private static String formatDate(LocalDateTime date, DateObjectExpr dateObject,
      String[] elements) {
    StringBuilder buf = new StringBuilder();
    for (String element : elements) {
      appendFormatElement(buf, date, dateObject, element);
    }
    return buf.toString();
  }

  private static void appendFormatElement(StringBuilder buf, LocalDateTime date,
      DateObjectExpr dateObject, String name) {
    String[] expanded = namedFormat(name);
    if (expanded != null) {
      for (String element : expanded) {
        appendFormatElement(buf, date, dateObject, element);
      }
      return;
    }
    IExpr value = dateElement(name, date, dateObject, true);
    if (value.isPresent()) {
      buf.append(value.toString());
    } else {
      // "Any other string given in the list of elements is concatenated literally"
      buf.append(name);
    }
  }

  // ==================================================================================
  // absolute time / unix time / julian date
  // ==================================================================================

  /** Seconds since 1900-01-01, as an exact integer if possible. */
  static IExpr absoluteTime(LocalDateTime date, boolean real) {
    long seconds = ChronoUnit.SECONDS.between(EPOCH_1900, date);
    int nanos = date.getNano();
    if (real || nanos != 0) {
      return F.num(seconds + nanos / 1.0e9);
    }
    return F.ZZ(seconds);
  }

  /** Seconds since 1970-01-01, as an exact integer if possible. */
  static IExpr unixTime(LocalDateTime date, boolean real) {
    long seconds = ChronoUnit.SECONDS.between(EPOCH_1970, date);
    int nanos = date.getNano();
    if (real || nanos != 0) {
      return F.num(seconds + nanos / 1.0e9);
    }
    return F.ZZ(seconds);
  }

  /**
   * The Julian day number - the count of days at noon - of a proleptic Gregorian date.
   */
  static long julianDayNumber(LocalDate date) {
    long a = (14 - date.getMonthValue()) / 12;
    long y = date.getYear() + 4800 - a;
    long m = date.getMonthValue() + 12 * a - 3;
    return date.getDayOfMonth() + (153 * m + 2) / 5 + 365 * y + Math.floorDiv(y, 4)
        - Math.floorDiv(y, 100) + Math.floorDiv(y, 400) - 32045;
  }

  /**
   * Convert a proleptic Gregorian date to the year, month and day of the Julian calendar.
   *
   * @return an array <code>{year, month, day}</code>
   */
  static int[] toJulianCalendar(LocalDate date) {
    long c = julianDayNumber(date) + 32082;
    long d = (4 * c + 3) / 1461;
    long e = c - (1461 * d) / 4;
    long m = (5 * e + 2) / 153;
    int day = (int) (e - (153 * m + 2) / 5 + 1);
    int month = (int) (m + 3 - 12 * (m / 10));
    int year = (int) (d - 4800 + m / 10);
    return new int[] {year, month, day};
  }

  /** The Julian date of a {@link LocalDateTime}. */
  static double julianDate(LocalDateTime date) {
    long epochDay = date.toLocalDate().toEpochDay();
    double dayFraction = date.toLocalTime().toNanoOfDay() / (double) NANOS_PER_DAY;
    return JULIAN_DATE_EPOCH_1970 + epochDay + dayFraction;
  }

  /** Create a date object at instant granularity in the given time zone. */
  private static DateObjectExpr instantObject(LocalDateTime date, IExpr timeZone,
      boolean realSeconds) {
    return DateObjectExpr.newInstance(date, DateGranularity.INSTANT, DateObjectExpr.GREGORIAN,
        timeZone, realSeconds);
  }

  // ==================================================================================
  // granularity and unit helpers
  // ==================================================================================

  /**
   * Map a granularity name to a {@link DateGranularity}.
   *
   * @return <code>null</code> if <code>expr</code> is not a granularity name
   */
  static DateGranularity toGranularity(IExpr expr) {
    if (expr.isString()) {
      return DateGranularity.of(expr.toString());
    }
    return null;
  }

  /**
   * The calendar unit denoted by a unit name such as <code>"Months"</code>. Both the singular and
   * the plural form are accepted.
   *
   * @return <code>null</code> if <code>name</code> is not a calendar unit
   */
  static String toCalendarUnit(String name) {
    String singular = name.endsWith("s") ? name.substring(0, name.length() - 1) : name;
    switch (singular) {
      case "Year":
      case "Quarter":
      case "Month":
      case "Week":
      case "Day":
      case "Hour":
      case "Minute":
      case "Second":
        return singular;
      default:
        return null;
    }
  }

  /**
   * Add <code>count</code> calendar units to a date. Month and year arithmetic clips to the end of
   * the target month, i.e. the &quot;RollBackward&quot; method.
   */
  static LocalDateTime plusCalendarUnit(LocalDateTime date, String unit, double count) {
    long whole = (long) count;
    switch (unit) {
      case "Year":
        return date.plusYears(whole);
      case "Quarter":
        return date.plusMonths(3 * whole);
      case "Month":
        return date.plusMonths(whole);
      case "Week":
        return plusSeconds(date, count * 7 * 86400.0);
      case "Day":
        return plusSeconds(date, count * 86400.0);
      case "Hour":
        return plusSeconds(date, count * 3600.0);
      case "Minute":
        return plusSeconds(date, count * 60.0);
      case "Second":
        return plusSeconds(date, count);
      default:
        return null;
    }
  }

  /** <code>true</code> if the unit is finer than a day. */
  private static boolean isTimeUnit(String unit) {
    return "Hour".equals(unit) || "Minute".equals(unit) || "Second".equals(unit);
  }

  /**
   * Extract the <code>{count, unit}</code> increments of a {@link S#DatePlus} or
   * {@link S#DateRange} increment specification.
   *
   * @return <code>null</code> if <code>expr</code> is not an increment specification
   */
  static List<Object[]> toIncrements(IExpr expr) {
    List<Object[]> result = new ArrayList<Object[]>();
    if (expr.isQuantity()) {
      IAST quantity = (IAST) expr;
      if (!quantity.arg2().isString()) {
        return null;
      }
      String unit = toCalendarUnit(quantity.arg2().toString());
      if (unit == null) {
        return null;
      }
      double count = quantity.arg1().toDoubleDefault(Double.NaN);
      if (Double.isNaN(count)) {
        return null;
      }
      result.add(new Object[] {unit, Double.valueOf(count)});
      return result;
    }
    if (expr.isString()) {
      String unit = toCalendarUnit(expr.toString());
      if (unit == null) {
        return null;
      }
      result.add(new Object[] {unit, Double.valueOf(1.0)});
      return result;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.argSize() == 2 && list.arg2().isString()) {
        String unit = toCalendarUnit(list.arg2().toString());
        double count = list.arg1().toDoubleDefault(Double.NaN);
        if (unit == null || Double.isNaN(count)) {
          return null;
        }
        result.add(new Object[] {unit, Double.valueOf(count)});
        return result;
      }
      for (int i = 1; i < list.size(); i++) {
        List<Object[]> nested = toIncrements(list.get(i));
        if (nested == null) {
          return null;
        }
        result.addAll(nested);
      }
      return result;
    }
    if (expr.isNumber() && expr.isReal()) {
      double count = expr.evalf();
      result.add(new Object[] {"Day", Double.valueOf(count)});
      return result;
    }
    return null;
  }

  /** Apply a list of increments in the order they are given. */
  static LocalDateTime applyIncrements(LocalDateTime date, List<Object[]> increments) {
    LocalDateTime result = date;
    for (Object[] increment : increments) {
      result =
          plusCalendarUnit(result, (String) increment[0], ((Double) increment[1]).doubleValue());
      if (result == null) {
        return null;
      }
    }
    return result;
  }

  /** <code>true</code> if one of the increments is finer than a day. */
  static boolean hasTimeIncrement(List<Object[]> increments) {
    for (Object[] increment : increments) {
      if (isTimeUnit((String) increment[0])) {
        return true;
      }
    }
    return false;
  }

  // ==================================================================================
  // arithmetic and comparison hooks
  // ==================================================================================

  /**
   * Add the time quantity arguments of a <code>Plus(...)</code> expression to its single
   * {@link DateObjectExpr} or {@link TimeObjectExpr} argument.
   *
   * @return {@link F#NIL} if the sum contains no date object, more than one date object or no
   *         quantity with a calendar unit
   */
  public static IExpr plusDateObject(IAST ast) {
    int datePosition = -1;
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg instanceof DateObjectExpr || arg instanceof TimeObjectExpr) {
        if (datePosition > 0) {
          return F.NIL;
        }
        datePosition = i;
      }
    }
    if (datePosition < 0) {
      return F.NIL;
    }
    List<Object[]> increments = new ArrayList<Object[]>();
    IASTAppendable rest = F.PlusAlloc(ast.size());
    for (int i = 1; i < ast.size(); i++) {
      if (i == datePosition) {
        continue;
      }
      IExpr arg = ast.get(i);
      if (arg.isQuantity() && ((IAST) arg).arg2().isString()) {
        IAST quantity = (IAST) arg;
        String unit = toCalendarUnit(quantity.arg2().toString());
        double count = quantity.arg1().toDoubleDefault(Double.NaN);
        if (unit != null && !Double.isNaN(count)) {
          increments.add(new Object[] {unit, Double.valueOf(count)});
          continue;
        }
      }
      rest.append(arg);
    }
    if (increments.isEmpty()) {
      return F.NIL;
    }
    IExpr date = ast.get(datePosition);
    IExpr shifted;
    if (date instanceof DateObjectExpr) {
      DateObjectExpr dateObject = (DateObjectExpr) date;
      LocalDateTime result = applyIncrements(dateObject.start(), increments);
      if (result == null) {
        return F.NIL;
      }
      shifted = dateObject.withDateTime(dateObject.getGranularity().truncate(result));
    } else {
      TimeObjectExpr timeObject = (TimeObjectExpr) date;
      LocalDateTime result = applyIncrements(
          LocalDateTime.of(LocalDate.of(2000, 1, 1), timeObject.toData()), increments);
      if (result == null) {
        return F.NIL;
      }
      // a time object wraps around midnight and gets an explicit time zone
      shifted = timeObject.withTime(result.toLocalTime()).withTimeZone(F.CD0);
    }
    if (rest.argSize() == 0) {
      return shifted;
    }
    rest.append(shifted);
    return rest;
  }

  /**
   * Rewrite the implicit multiplication of a date or time object with a string into the property
   * access it was written as.
   *
   * <p>
   * With <code>ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS</code> - the relaxed syntax mode - the
   * input <code>DateObject({2024,7,4})("Day")</code> is parsed as
   * <code>Times(DateObject(...), "Day")</code>, because <code>expr(...)</code> is read as an
   * implicit product. Multiplying a date by a string has no meaning of its own, so it is turned
   * back into <code>DateObject(...)("Day")</code> which the {@link S#DateObject} evaluator
   * resolves.
   * </p>
   *
   * @return {@link F#NIL} if the product is not a date or time object multiplied by a string
   */
  public static IExpr timesDateObject(IAST ast) {
    if (ast.argSize() != 2) {
      return F.NIL;
    }
    IExpr first = ast.arg1();
    IExpr second = ast.arg2();
    if (isDateOrTimeObject(first) && second.isString()) {
      return F.unaryAST1(first, second);
    }
    if (isDateOrTimeObject(second) && first.isString()) {
      return F.unaryAST1(second, first);
    }
    return F.NIL;
  }

  private static boolean isDateOrTimeObject(IExpr expr) {
    return expr instanceof DateObjectExpr || expr instanceof TimeObjectExpr;
  }

  /**
   * Compare two {@link DateObjectExpr} or two {@link TimeObjectExpr} expressions by their start
   * instant.
   *
   * @return {@link F#NIL} if the two expressions are not comparable dates or times
   */
  public static IExpr compareDateObject(IExpr a0, IExpr a1) {
    if (a0 instanceof DateObjectExpr && a1 instanceof DateObjectExpr) {
      return F.ZZ(((DateObjectExpr) a0).start().compareTo(((DateObjectExpr) a1).start()));
    }
    if (a0 instanceof TimeObjectExpr && a1 instanceof TimeObjectExpr) {
      return F.ZZ(((TimeObjectExpr) a0).toData().compareTo(((TimeObjectExpr) a1).toData()));
    }
    return F.NIL;
  }

  // ==================================================================================
  // date lists as results
  // ==================================================================================

  /** The six element date list of a {@link LocalDateTime}, seconds as a machine real. */
  static IAST dateListOf(LocalDateTime date) {
    return F.List(F.ZZ(DateObjectExpr.toDisplayYear(date.getYear())), F.ZZ(date.getMonthValue()),
        F.ZZ(date.getDayOfMonth()), F.ZZ(date.getHour()), F.ZZ(date.getMinute()),
        F.num(date.getSecond() + date.getNano() / 1.0e9));
  }

  /** The three element date list <code>{y, m, d}</code> of a {@link LocalDateTime}. */
  static IAST dayListOf(LocalDateTime date) {
    return F.List(F.ZZ(DateObjectExpr.toDisplayYear(date.getYear())), F.ZZ(date.getMonthValue()),
        F.ZZ(date.getDayOfMonth()));
  }

  // ==================================================================================
  // evaluators
  // ==================================================================================

  /** <code>$TimeZone</code> - the time zone to assume for dates and times. */
  private static final class TimeZoneConstant extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.CD0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set the CONSTANT attribute, the value may be changed by the user
    }
  }

  /** <code>Yesterday</code> - the previous day as a day granularity date object. */
  private static final class Yesterday extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return DateObjectExpr.newInstance(
          LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1), DateGranularity.DAY);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  private static final class AbsoluteTime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.num(absoluteTime(LocalDateTime.now(), true).evalf());
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isNumber() && arg1.isReal()) {
        // AbsoluteTime of a number of seconds is the identity
        return arg1;
      }
      DateSpec spec = dateSpec(arg1);
      if (spec == null) {
        return F.NIL;
      }
      boolean real = spec.real;
      if (arg1.isList() && ((IAST) arg1).argSize() == 2 && ((IAST) arg1).arg1().isString()) {
        real = true;
      }
      return absoluteTime(spec.instant, real);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class FromAbsoluteTime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isNumber() || !arg1.isReal()) {
        return F.NIL;
      }
      LocalDateTime date = plusSeconds(EPOCH_1900, arg1.evalf());
      return instantObject(date, F.CD0, arg1.isInexactNumber());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class UnixTime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return unixTime(LocalDateTime.now(), false);
      }
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      LocalDateTime date = spec.instant;
      IExpr timeZone = spec.dateObject.getTimeZone();
      if (timeZone != S.None && timeZone.isReal()) {
        // the date object is given in a local time zone - convert back to GMT
        date = plusSeconds(date, -timeZone.evalf() * 3600.0);
      }
      return unixTime(date, spec.real);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class FromUnixTime extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isNumber() || !arg1.isReal()) {
        return F.NIL;
      }
      IExpr timeZone = F.CD0;
      if (ast.argSize() > 1) {
        OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        IExpr option = options.getOption(S.TimeZone);
        if (option.isPresent() && option.isReal()) {
          timeZone = F.num(option.evalf());
        }
      }
      double offsetSeconds = timeZone.isReal() ? timeZone.evalf() * 3600.0 : 0.0;
      LocalDateTime date = plusSeconds(EPOCH_1970, arg1.evalf() + offsetSeconds);
      return instantObject(date, timeZone, arg1.isInexactNumber());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class JulianDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.num(julianDate(LocalDateTime.now()));
      }
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      return F.num(julianDate(spec.instant));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class FromJulianDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isNumber() || !arg1.isReal()) {
        return F.NIL;
      }
      double julianDate = arg1.evalf();
      double days = julianDate - JULIAN_DATE_EPOCH_1970;
      long epochDay = (long) Math.floor(days);
      double dayFraction = days - epochDay;
      LocalDateTime date = LocalDate.ofEpochDay(epochDay).atStartOfDay()
          .plusNanos(Math.round(dayFraction * NANOS_PER_DAY));
      return instantObject(date, F.CD0, arg1.isInexactNumber());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DateList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return dateListOf(LocalDateTime.now());
      }
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      return dateListOf(spec.instant);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class DateObject extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() instanceof DateObjectExpr) {
        // property access, i.e. DateObject(...)("Year")
        return propertyValue((DateObjectExpr) ast.head(), ast);
      }
      if (ast.isAST0()) {
        return DateObjectExpr.newInstance(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS),
            DateGranularity.DAY);
      }
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        if (arg1 instanceof DateObjectExpr && ast.arg2() instanceof TimeObjectExpr) {
          LocalDate localDate = ((DateObjectExpr) arg1).start().toLocalDate();
          LocalTime localTime = ((TimeObjectExpr) ast.arg2()).toData();
          return instantObject(LocalDateTime.of(localDate, localTime), F.CD0, false);
        }
        DateGranularity granularity = toGranularity(ast.arg2());
        if (granularity == null) {
          return F.NIL;
        }
        DateSpec spec = dateSpec(arg1, granularity);
        return spec == null ? F.NIL : spec.dateObject;
      }
      DateSpec spec = dateSpec(arg1);
      return spec == null ? F.NIL : spec.dateObject;
    }

    /** Evaluate <code>DateObject(...)("element")</code>. */
    private static IExpr propertyValue(DateObjectExpr dateObject, IAST ast) {
      if (ast.argSize() != 1 || !ast.arg1().isString()) {
        return F.NIL;
      }
      String name = ast.arg1().toString();
      return dateElement(name, dateObject.start(), dateObject, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }
  }

  private static final class DateObjectQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof DateObjectExpr);
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class TimeObject extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() instanceof TimeObjectExpr) {
        TimeObjectExpr timeObject = (TimeObjectExpr) ast.head();
        if (ast.argSize() != 1 || !ast.arg1().isString()) {
          return F.NIL;
        }
        String name = ast.arg1().toString();
        if ("Granularity".equals(name)) {
          return F.stringx(timeObject.getGranularity().getName());
        }
        return dateElement(name, LocalDateTime.of(LocalDate.of(2000, 1, 1), timeObject.toData()),
            null, false);
      }
      if (ast.isAST0()) {
        return TimeObjectExpr.newInstance(LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
            DateGranularity.INSTANT);
      }
      IExpr arg1 = ast.arg1();
      DateGranularity granularity = null;
      if (ast.isAST2()) {
        granularity = toGranularity(ast.arg2());
        if (granularity == null) {
          return F.NIL;
        }
      }
      if (arg1 instanceof TimeObjectExpr) {
        TimeObjectExpr timeObject = (TimeObjectExpr) arg1;
        return granularity == null ? timeObject
            : TimeObjectExpr.newInstance(granularity
                .truncate(LocalDateTime.of(LocalDate.of(2000, 1, 1), timeObject.toData()))
                .toLocalTime(), granularity);
      }
      if (arg1 instanceof DateObjectExpr) {
        DateObjectExpr dateObject = (DateObjectExpr) arg1;
        return TimeObjectExpr.newInstance(dateObject.start().toLocalTime(),
            granularity == null ? DateGranularity.INSTANT : granularity);
      }
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        int argSize = list.argSize();
        if (argSize < 1 || argSize > 3) {
          return F.NIL;
        }
        double[] components = new double[] {0.0, 0.0, 0.0};
        boolean real = false;
        for (int i = 1; i <= argSize; i++) {
          IExpr arg = list.get(i);
          double value = arg.toDoubleDefault(Double.NaN);
          if (Double.isNaN(value)) {
            return F.NIL;
          }
          if (arg.isInexactNumber()) {
            real = true;
          }
          components[i - 1] = value;
        }
        long nanoOfDay = Math.round(components[0] * 3600.0e9 + components[1] * 60.0e9 //
            + components[2] * 1.0e9);
        LocalTime time = LocalTime.ofNanoOfDay(Math.floorMod(nanoOfDay, NANOS_PER_DAY));
        DateGranularity gran = granularity;
        if (gran == null) {
          gran = argSize == 1 ? DateGranularity.HOUR
              : (argSize == 2 ? DateGranularity.MINUTE : DateGranularity.INSTANT);
        }
        time = gran.truncate(LocalDateTime.of(LocalDate.of(2000, 1, 1), time)).toLocalTime();
        return TimeObjectExpr.newInstance(time, gran, S.None,
            real && gran == DateGranularity.INSTANT && time.getNano() != 0);
      }
      if (arg1.isNumber() && arg1.isReal()) {
        double hours = arg1.evalf();
        long nanoOfDay = Math.round(hours * 3600.0e9);
        LocalTime time = LocalTime.ofNanoOfDay(Math.floorMod(nanoOfDay, NANOS_PER_DAY));
        DateGranularity gran = granularity == null ? DateGranularity.HOUR : granularity;
        time = gran.truncate(LocalDateTime.of(LocalDate.of(2000, 1, 1), time)).toLocalTime();
        return TimeObjectExpr.newInstance(time, gran);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }
  }

  private static final class DateString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.stringx(formatDate(LocalDateTime.now(), null, DEFAULT_DATE_TIME_FORMAT));
      }
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isString()) {
          // a single string argument is a format specification for the current date
          return F.stringx(formatDate(LocalDateTime.now(), null, new String[] {arg1.toString()}));
        }
        if (arg1.isList() && !isFormattedStringPair(arg1)) {
          DateSpec spec = dateSpec(arg1);
          if (spec == null) {
            return F.NIL;
          }
          return F.stringx(formatDate(spec.instant, spec.dateObject, DEFAULT_DATE_TIME_FORMAT));
        }
        DateSpec spec = dateSpec(arg1);
        if (spec == null) {
          return F.NIL;
        }
        String[] format = spec.dateObject != null && spec.dateObject.getGranularity().isDateOnly()
            && arg1 instanceof DateObjectExpr ? DEFAULT_DATE_FORMAT : DEFAULT_DATE_TIME_FORMAT;
        return F.stringx(formatDate(spec.instant, spec.dateObject, format));
      }
      // two arguments: date and format
      DateSpec spec = dateSpec(arg1);
      if (spec == null) {
        return F.NIL;
      }
      IExpr arg2 = ast.arg2();
      if (arg2.isString()) {
        return F.stringx(formatDate(spec.instant, spec.dateObject, new String[] {arg2.toString()}));
      }
      if (arg2.isList()) {
        return F.stringx(formatDate(spec.instant, spec.dateObject, (IAST) arg2));
      }
      return F.NIL;
    }

    private static boolean isFormattedStringPair(IExpr expr) {
      if (expr.isList()) {
        IAST list = (IAST) expr;
        return list.argSize() == 2 && list.arg1().isString() && list.arg2().isList();
      }
      return false;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class DateValue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isString()) {
          return dateElement(arg1.toString(), LocalDateTime.now(), null, false);
        }
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        return F.NIL;
      }
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        IASTAppendable result = F.ListAlloc(arg2.size());
        for (int i = 1; i < ((IAST) arg2).size(); i++) {
          IExpr value = elementValue(spec, ((IAST) arg2).get(i));
          if (value.isNIL()) {
            return F.NIL;
          }
          result.append(value);
        }
        return result;
      }
      return elementValue(spec, arg2);
    }

    private static IExpr elementValue(DateSpec spec, IExpr element) {
      if (!element.isString()) {
        return F.NIL;
      }
      return dateElement(element.toString(), spec.instant, spec.dateObject, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class FromDateString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      DateSpec spec = dateSpec(arg1);
      return spec == null ? F.NIL : spec.dateObject;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DayName extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return weekdaySymbol(LocalDateTime.now().getDayOfWeek());
      }
      for (int i = 2; i < ast.size(); i++) {
        if (!ast.get(i).isRuleAST()) {
          // only options such as CalendarType -> "Gregorian" may follow the date
          return F.NIL;
        }
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isString() && toDayOfWeek(arg1) != null) {
        // a weekday name is not a date
        return F.NIL;
      }
      DateSpec spec = dateSpec(arg1);
      if (spec == null) {
        return F.NIL;
      }
      return weekdaySymbol(spec.instant.getDayOfWeek());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class LeapYearQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      return F.booleSymbol(LocalDate.of(spec.instant.getYear(), 1, 1).isLeapYear());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DayCount extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec from = dateSpec(ast.arg1());
      DateSpec to = dateSpec(ast.arg2());
      if (from == null || to == null) {
        return F.NIL;
      }
      LocalDate start = from.instant.toLocalDate();
      LocalDate end = to.instant.toLocalDate();
      if (ast.argSize() >= 3) {
        DayOfWeek dayOfWeek = toDayOfWeek(ast.arg3());
        String dayType = ast.arg3().isString() ? ast.arg3().toString() : null;
        long count = 0;
        LocalDate current = start.plusDays(1);
        while (!current.isAfter(end)) {
          if (matchesDayType(current, dayOfWeek, dayType)) {
            count++;
          }
          current = current.plusDays(1);
        }
        return F.ZZ(count);
      }
      return F.ZZ(ChronoUnit.DAYS.between(start, end));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>true</code> if <code>date</code> matches a weekday or one of the supported day type
   * strings.
   */
  static boolean matchesDayType(LocalDate date, DayOfWeek dayOfWeek, String dayType) {
    if (dayOfWeek != null) {
      return date.getDayOfWeek() == dayOfWeek;
    }
    if (dayType == null) {
      return true;
    }
    boolean weekend = date.getDayOfWeek() == DayOfWeek.SATURDAY //
        || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    switch (dayType) {
      case "Weekend":
      case "WeekendDay":
        return weekend;
      case "Weekday":
      case "BusinessDay":
        return !weekend;
      case "All":
        return true;
      case "EndOfMonth":
        return date.getDayOfMonth() == date.lengthOfMonth();
      case "BeginningOfMonth":
        return date.getDayOfMonth() == 1;
      default:
        return false;
    }
  }

  /**
   * <code>true</code> if the day specification is one this implementation understands.
   */
  static boolean isDaySpec(IExpr expr) {
    if (toDayOfWeek(expr) != null) {
      return true;
    }
    if (expr.isString()) {
      switch (expr.toString()) {
        case "Weekend":
        case "WeekendDay":
        case "Weekday":
        case "BusinessDay":
        case "All":
        case "EndOfMonth":
        case "BeginningOfMonth":
          return true;
        default:
          return false;
      }
    }
    return expr == S.All;
  }

  private static final class DayMatchQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null || !isDaySpec(ast.arg2())) {
        return F.NIL;
      }
      DayOfWeek dayOfWeek = toDayOfWeek(ast.arg2());
      String dayType = ast.arg2().isString() ? ast.arg2().toString() : null;
      return F.booleSymbol(matchesDayType(spec.instant.toLocalDate(), dayOfWeek, dayType));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class DayPlus extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      int count = ast.arg2().toIntDefault();
      if (!F.isPresent(count)) {
        return F.NIL;
      }
      LocalDate date = spec.instant.toLocalDate();
      if (ast.argSize() >= 3) {
        if (!isDaySpec(ast.arg3())) {
          return F.NIL;
        }
        DayOfWeek dayOfWeek = toDayOfWeek(ast.arg3());
        String dayType = ast.arg3().isString() ? ast.arg3().toString() : null;
        int step = count >= 0 ? 1 : -1;
        int remaining = Math.abs(count);
        while (remaining > 0) {
          date = date.plusDays(step);
          if (matchesDayType(date, dayOfWeek, dayType)) {
            remaining--;
          }
        }
      } else {
        date = date.plusDays(count);
      }
      return DateObjectExpr.newInstance(date.atStartOfDay(), DateGranularity.DAY);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class DayRound extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec spec = dateSpec(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      if (ast.argSize() >= 3) {
        // the rounding direction ("Next", "Preceding", ...) is not implemented; don't silently
        // round in the default direction instead
        return F.NIL;
      }
      LocalDate date = spec.instant.toLocalDate();
      if (ast.argSize() >= 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isString() && "Day".equals(arg2.toString())) {
          // nothing to do, the date is rounded to day granularity below
        } else {
          if (!isDaySpec(arg2)) {
            return F.NIL;
          }
          DayOfWeek dayOfWeek = toDayOfWeek(arg2);
          String dayType = arg2.isString() ? arg2.toString() : null;
          while (!matchesDayType(date, dayOfWeek, dayType)) {
            date = date.plusDays(1);
          }
        }
      }
      // a date object keeps its time zone, any other date specification gets none
      IExpr timeZone =
          ast.arg1() instanceof DateObjectExpr ? spec.dateObject.getTimeZone() : S.None;
      return DateObjectExpr.newInstance(date.atStartOfDay(), DateGranularity.DAY,
          DateObjectExpr.GREGORIAN, timeZone, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class DayRange extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec from = dateSpec(ast.arg1());
      DateSpec to = dateSpec(ast.arg2());
      if (from == null || to == null) {
        return F.NIL;
      }
      LocalDate start = from.instant.toLocalDate();
      LocalDate end = to.instant.toLocalDate();
      if (start.isAfter(end)) {
        LocalDate swap = start;
        start = end;
        end = swap;
      }
      DayOfWeek dayOfWeek = null;
      String dayType = null;
      if (ast.argSize() >= 3) {
        if (!isDaySpec(ast.arg3())) {
          return F.NIL;
        }
        dayOfWeek = toDayOfWeek(ast.arg3());
        dayType = ast.arg3().isString() ? ast.arg3().toString() : null;
      }
      IASTAppendable result = F.ListAlloc();
      LocalDate current = start;
      while (!current.isAfter(end)) {
        if (matchesDayType(current, dayOfWeek, dayType)) {
          result.append(DateObjectExpr.newInstance(current.atStartOfDay(), DateGranularity.DAY));
        }
        current = current.plusDays(1);
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class DateRange extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec from = dateSpec(ast.arg1());
      DateSpec to = dateSpec(ast.arg2());
      if (from == null || to == null) {
        return F.NIL;
      }
      List<Object[]> increments;
      if (ast.argSize() >= 3) {
        increments = toIncrements(ast.arg3());
        if (increments == null || increments.isEmpty()) {
          return F.NIL;
        }
      } else {
        increments = toIncrements(F.C1);
      }
      LocalDateTime current = from.instant;
      LocalDateTime end = to.instant;
      IASTAppendable result = F.ListAlloc();
      int iterations = 0;
      while (!current.isAfter(end)) {
        result.append(dateListOf(current));
        LocalDateTime next = applyIncrements(current, increments);
        if (next == null || !next.isAfter(current) || ++iterations > 100000) {
          break;
        }
        current = next;
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class NextDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return nextOrPreviousDate(ast, true);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class PreviousDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return nextOrPreviousDate(ast, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static IExpr nextOrPreviousDate(IAST ast, boolean forward) {
    IExpr dateArgument;
    IExpr specArgument;
    if (ast.isAST1()) {
      dateArgument = F.NIL;
      specArgument = ast.arg1();
    } else {
      dateArgument = ast.arg1();
      specArgument = ast.arg2();
    }
    DateSpec spec;
    if (dateArgument.isNIL()) {
      LocalDateTime now = LocalDateTime.now();
      spec = new DateSpec(DateObjectExpr.newInstance(now), now, 0, false);
    } else {
      spec = dateSpec(dateArgument);
      if (spec == null) {
        return F.NIL;
      }
    }
    DayOfWeek dayOfWeek = toDayOfWeek(specArgument);
    if (dayOfWeek != null) {
      LocalDate date = spec.instant.toLocalDate();
      do {
        date = forward ? date.plusDays(1) : date.minusDays(1);
      } while (date.getDayOfWeek() != dayOfWeek);
      return DateObjectExpr.newInstance(date.atStartOfDay(), DateGranularity.DAY);
    }
    DateGranularity granularity = toGranularity(specArgument);
    if (granularity != null) {
      LocalDateTime start = granularity.truncate(spec.instant);
      LocalDateTime result =
          forward ? granularity.next(start) : granularity.truncate(start.minusNanos(1));
      return DateObjectExpr.newInstance(result, granularity);
    }
    return F.NIL;
  }

  private static final class DatePlus extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      DateSpec spec = dateSpec(arg1);
      if (spec == null) {
        return F.NIL;
      }
      List<Object[]> increments = toIncrements(ast.arg2());
      if (increments == null) {
        return F.NIL;
      }
      LocalDateTime result = applyIncrements(spec.instant, increments);
      if (result == null) {
        return F.NIL;
      }
      if (arg1 instanceof DateObjectExpr) {
        DateObjectExpr dateObject = (DateObjectExpr) arg1;
        return dateObject.withDateTime(dateObject.getGranularity().truncate(result));
      }
      if (spec.listSize > 0) {
        return hasTimeIncrement(increments) || spec.listSize > 3 ? dateListOf(result)
            : dayListOf(result);
      }
      return instantObject(result, spec.dateObject.getTimeZone(), spec.real);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DateDifference extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DateSpec from = dateSpec(ast.arg1());
      DateSpec to = dateSpec(ast.arg2());
      if (from == null || to == null) {
        return F.NIL;
      }
      String unit = "Day";
      if (ast.argSize() >= 3) {
        if (!ast.arg3().isString()) {
          return F.NIL;
        }
        unit = toCalendarUnit(ast.arg3().toString());
        if (unit == null) {
          return F.NIL;
        }
      }
      IExpr value = difference(from.instant, to.instant, unit);
      if (value.isNIL()) {
        return F.NIL;
      }
      return F.Quantity(value, F.stringx(pluralUnit(unit)));
    }

    private static String pluralUnit(String unit) {
      return unit + "s";
    }

    /**
     * The difference of two dates in the given unit. Exact whenever the result is a whole number of
     * units.
     */
    private static IExpr difference(LocalDateTime from, LocalDateTime to, String unit) {
      switch (unit) {
        case "Day":
          return exactOrReal(Duration.between(from, to).toNanos(), NANOS_PER_DAY);
        case "Week":
          // a whole number of weeks is exact, any other difference is a machine number
          long nanos = Duration.between(from, to).toNanos();
          long weekNanos = 7 * NANOS_PER_DAY;
          return nanos % weekNanos == 0 ? F.ZZ(nanos / weekNanos)
              : F.num(nanos / (double) weekNanos);
        case "Hour":
          return exactOrReal(Duration.between(from, to).toNanos(), 3600_000_000_000L);
        case "Minute":
          return exactOrReal(Duration.between(from, to).toNanos(), 60_000_000_000L);
        case "Second":
          return exactOrReal(Duration.between(from, to).toNanos(), 1_000_000_000L);
        case "Month":
          return monthOrYearDifference(from, to, 1);
        case "Quarter":
          return monthOrYearDifference(from, to, 3);
        case "Year":
          return monthOrYearDifference(from, to, 12);
        default:
          return F.NIL;
      }
    }

    /**
     * A whole number of units between the two dates is exact. Otherwise the remaining part is
     * measured against the length of the unit it falls into:
     * <code>DateDifference[{2020,1,1},{2020,7,1},"Year"]</code> is <code>182/366</code> because the
     * year 2020 has 366 days.
     */
    private static IExpr monthOrYearDifference(LocalDateTime from, LocalDateTime to,
        int monthsPerUnit) {
      // the number of whole units, using the same end of month clipping as DatePlus
      long units = ChronoUnit.MONTHS.between(from, to) / monthsPerUnit;
      while (!from.plusMonths((units + 1) * monthsPerUnit).isAfter(to)) {
        units++;
      }
      while (from.plusMonths(units * monthsPerUnit).isAfter(to)) {
        units--;
      }
      LocalDateTime shifted = from.plusMonths(units * monthsPerUnit);
      if (shifted.equals(to)) {
        return F.ZZ(units);
      }
      LocalDateTime nextUnit = from.plusMonths((units + 1) * monthsPerUnit);
      double span = Duration.between(shifted, nextUnit).toNanos();
      double fraction = Duration.between(shifted, to).toNanos() / span;
      return F.num(units + fraction);
    }

    /** An exact rational if the nano difference is a multiple of the unit, otherwise a real. */
    private static IExpr exactOrReal(long nanos, long nanosPerUnit) {
      if (nanos % nanosPerUnit == 0) {
        return F.ZZ(nanos / nanosPerUnit);
      }
      long gcd = gcd(Math.abs(nanos), nanosPerUnit);
      return F.QQ(nanos / gcd, nanosPerUnit / gcd);
    }

    private static long gcd(long a, long b) {
      while (b != 0) {
        long temp = b;
        b = a % b;
        a = temp;
      }
      return a == 0 ? 1 : a;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class TimeZoneOffset extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.CD0;
      }
      IExpr zone = ast.arg1();
      if (zone == S.None) {
        return F.C0;
      }
      if (!zone.isReal()) {
        return F.NIL;
      }
      IExpr base = F.C0;
      if (ast.argSize() >= 2) {
        base = ast.arg2();
        if (base == S.None) {
          base = F.C0;
        } else if (!base.isReal()) {
          return F.NIL;
        }
      }
      return engine.evaluate(F.Subtract(zone, base));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }
  }

  private static final class Dated extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // Dated is an inert wrapper
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DateInterval extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // DateInterval stays in its unevaluated form; the endpoints are interpreted by the functions
      // which consume it
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The half open span <code>[start, end)</code> of a date or a date interval.
   *
   * <p>
   * A granular date denotes the whole period it names, so <code>DateObject({2024, 2})</code> spans
   * all of February. A <code>DateInterval</code> includes the full extent of its end date, i.e.
   * <code>DateInterval({{2019,1,1},{2019,1,20}})</code> ends at the beginning of January 21st.
   * </p>
   *
   * @return <code>null</code> if <code>expr</code> is not a date specification
   */
  static LocalDateTime[] boundsOf(IExpr expr) {
    if (isInterval(expr) && expr.first().isList()) {
      IAST endpoints = (IAST) expr.first();
      if (endpoints.argSize() == 2) {
        DateSpec start = dateSpec(endpoints.arg1());
        DateSpec end = dateSpec(endpoints.arg2());
        if (start == null || end == null) {
          return null;
        }
        return new LocalDateTime[] {start.dateObject.start(), end.dateObject.end()};
      }
      return null;
    }
    DateSpec spec = dateSpec(expr);
    if (spec == null) {
      return null;
    }
    return new LocalDateTime[] {spec.dateObject.start(), spec.dateObject.end()};
  }

  /** The granularity of a date or date interval, used to expand an interval to single dates. */
  static DateGranularity granularityOf(IExpr expr) {
    if (expr.isAST(S.DateInterval, 2) && expr.first().isList()) {
      IAST endpoints = (IAST) expr.first();
      if (endpoints.argSize() == 2) {
        DateSpec start = dateSpec(endpoints.arg1());
        return start == null ? null : start.dateObject.getGranularity();
      }
      return null;
    }
    DateSpec spec = dateSpec(expr);
    return spec == null ? null : spec.dateObject.getGranularity();
  }

  private static final class DateBounds extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        if (!isDateSpecList(arg1)) {
          return F.NIL;
        }
        IAST list = (IAST) arg1;
        IExpr min = extremeDate(list, false);
        IExpr max = extremeDate(list, true);
        if (min.isNIL() || max.isNIL()) {
          return F.NIL;
        }
        return F.List(min, max);
      }
      if (!isDateOrInterval(arg1)) {
        // a plain number or string has no bounds
        return F.NIL;
      }
      LocalDateTime[] bounds = boundsOf(arg1);
      if (bounds == null) {
        return F.NIL;
      }
      DateSpec spec = dateSpec(arg1);
      IExpr timeZone = spec == null ? S.None : spec.dateObject.getTimeZone();
      return F.List(instantObject(bounds[0], timeZone, true),
          instantObject(bounds[1], timeZone, true));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>true</code> if every element of the list can be interpreted as a date. */
  static boolean isDateSpecList(IExpr expr) {
    if (!expr.isList()) {
      return false;
    }
    IAST list = (IAST) expr;
    if (list.isEmpty()) {
      return false;
    }
    for (int i = 1; i < list.size(); i++) {
      if (dateSpec(list.get(i)) == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * The earliest or latest element of a list of dates, returned in the form in which it was given.
   * Dates with an equal start instant are ordered by granularity, so that the finer date wins for
   * {@link S#MaxDate} and the coarser for {@link S#MinDate}.
   */
  static IExpr extremeDate(IAST list, boolean maximum) {
    IExpr best = F.NIL;
    DateObjectExpr bestDate = null;
    for (int i = 1; i < list.size(); i++) {
      DateSpec spec = dateSpec(list.get(i));
      if (spec == null) {
        return F.NIL;
      }
      if (bestDate == null || isBetter(spec.dateObject, bestDate, maximum)) {
        bestDate = spec.dateObject;
        best = list.get(i);
      }
    }
    return best;
  }

  private static boolean isBetter(DateObjectExpr candidate, DateObjectExpr current,
      boolean maximum) {
    int comparison = candidate.start().compareTo(current.start());
    if (comparison == 0) {
      comparison = candidate.getGranularity().compareTo(current.getGranularity());
    }
    return maximum ? comparison > 0 : comparison < 0;
  }

  private static final class MaxDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return minMaxDate(ast, true);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class MinDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return minMaxDate(ast, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static IExpr minMaxDate(IAST ast, boolean maximum) {
    IExpr arg1 = ast.arg1();
    if (isInterval(arg1)) {
      // MinDate and MaxDate give the endpoints as they were specified, not the half open span
      if (!arg1.first().isList() || ((IAST) arg1.first()).argSize() != 2) {
        return F.NIL;
      }
      IAST endpoints = (IAST) arg1.first();
      DateSpec endpoint = dateSpec(endpoints.get(maximum ? 2 : 1));
      return endpoint == null ? F.NIL : endpoint.dateObject;
    }
    if (arg1.isList() && isDateSpecList(arg1)) {
      return extremeDate((IAST) arg1, maximum);
    }
    return F.NIL;
  }

  private static final class DateWithinQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (!isDateOrInterval(arg1) || !isDateOrInterval(arg2)) {
        return F.NIL;
      }
      LocalDateTime[] outer = boundsOf(arg1);
      LocalDateTime[] inner = boundsOf(arg2);
      if (outer == null || inner == null) {
        return F.NIL;
      }
      if (outer[0].equals(outer[1]) && inner[0].equals(inner[1])) {
        // two instants have no extent - the question is undecidable
        return F.NIL;
      }
      boolean within;
      if (inner[0].equals(inner[1])) {
        // an instant lies in the half open span [start, end)
        within = !inner[0].isBefore(outer[0]) && inner[0].isBefore(outer[1]);
      } else {
        within = !inner[0].isBefore(outer[0]) && !inner[1].isAfter(outer[1]);
      }
      return F.booleSymbol(within);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class DateOverlapsQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (!isDateOrInterval(arg1) || !isDateOrInterval(arg2)) {
        return F.NIL;
      }
      LocalDateTime[] first = boundsOf(arg1);
      LocalDateTime[] second = boundsOf(arg2);
      if (first == null || second == null) {
        return F.NIL;
      }
      boolean overlaps = !first[0].isAfter(second[1]) && !second[0].isAfter(first[1]);
      if (overlaps && !first[0].equals(first[1]) && !second[0].equals(second[1])) {
        // half open intervals only touch if one starts where the other ends
        if (first[1].equals(second[0]) || second[1].equals(first[0])) {
          overlaps = isInterval(arg1) || isInterval(arg2);
        }
      }
      return F.booleSymbol(overlaps);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static boolean isInterval(IExpr expr) {
    return expr.isAST(S.DateInterval, 2);
  }

  /** <code>true</code> for a {@link DateObjectExpr} or a <code>DateInterval(...)</code>. */
  private static boolean isDateOrInterval(IExpr expr) {
    return expr instanceof DateObjectExpr || isInterval(expr);
  }

  private static final class MidDate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      DateGranularity granularity = null;
      if (ast.argSize() >= 2) {
        granularity = toGranularity(ast.arg2());
        if (granularity == null) {
          return F.NIL;
        }
      }
      double fraction = 0.5;
      boolean explicitFraction = false;
      if (ast.argSize() >= 3) {
        double value = ast.arg3().toDoubleDefault(Double.NaN);
        if (Double.isNaN(value)) {
          return F.NIL;
        }
        fraction = value;
        explicitFraction = true;
      }
      List<LocalDateTime[]> spans = new ArrayList<LocalDateTime[]>();
      IExpr timeZone = S.None;
      if (arg1.isAssociation()) {
        IAssociation association = (IAssociation) arg1;
        for (int i = 1; i < association.size(); i++) {
          IExpr value = association.getRule(i).second();
          LocalDateTime[] bounds = boundsOf(value);
          if (bounds == null) {
            return F.NIL;
          }
          spans.add(bounds);
        }
      } else if (arg1.isList()) {
        IAST list = (IAST) arg1;
        if (list.isEmpty()) {
          return F.NIL;
        }
        if (isDateSpec(list)) {
          LocalDateTime[] bounds = boundsOf(list);
          if (bounds == null) {
            return F.NIL;
          }
          spans.add(bounds);
        } else {
          for (int i = 1; i < list.size(); i++) {
            LocalDateTime[] bounds = boundsOf(list.get(i));
            if (bounds == null) {
              return F.NIL;
            }
            spans.add(bounds);
          }
        }
      } else {
        LocalDateTime[] bounds = boundsOf(arg1);
        if (bounds == null) {
          return F.NIL;
        }
        spans.add(bounds);
        DateSpec spec = dateSpec(arg1);
        if (spec != null) {
          timeZone = spec.dateObject.getTimeZone();
        }
      }
      if (spans.isEmpty()) {
        return F.NIL;
      }
      LocalDateTime result;
      if (spans.size() == 1) {
        result = interpolate(spans.get(0)[0], spans.get(0)[1], fraction);
      } else if (explicitFraction) {
        // an explicit fraction refers to the overall bounds of the whole collection
        LocalDateTime start = spans.get(0)[0];
        LocalDateTime end = spans.get(0)[1];
        for (LocalDateTime[] span : spans) {
          if (span[0].isBefore(start)) {
            start = span[0];
          }
          if (span[1].isAfter(end)) {
            end = span[1];
          }
        }
        result = interpolate(start, end, fraction);
      } else {
        result = granularMean(spans);
      }
      if (granularity == null) {
        return instantObject(result, timeZone == S.None ? F.CD0 : timeZone, true);
      }
      return DateObjectExpr.newInstance(granularity.truncate(result), granularity);
    }

    /** <code>true</code> if the list itself is a date specification and not a list of dates. */
    private static boolean isDateSpec(IAST list) {
      if (list.argSize() < 1 || list.argSize() > 6) {
        return false;
      }
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isNumber()) {
          return false;
        }
      }
      return dateSpec(list) != null;
    }

    private static LocalDateTime interpolate(LocalDateTime start, LocalDateTime end,
        double fraction) {
      long nanos = Duration.between(start, end).toNanos();
      return start.plusNanos(Math.round(nanos * fraction));
    }

    /**
     * The default &quot;GranularMean&quot; method: the mean of the midpoints of the single spans,
     * each weighted by the length of its span measured in the finest granularity present.
     *
     * <p>
     * Weighting by the finest granularity makes a list of dates which all have the same granularity
     * an unweighted mean, while a coarse date in a list of fine ones counts as often as it contains
     * the finer period.
     * <code>MidDate({DateObject({2024,10,1}), DateObject({2024,10,7},"Week")})</code> therefore
     * gives the same result as listing the seven days of that week separately.
     * </p>
     */
    private static LocalDateTime granularMean(List<LocalDateTime[]> spans) {
      long unit = Long.MAX_VALUE;
      for (LocalDateTime[] span : spans) {
        long length = Duration.between(span[0], span[1]).toNanos();
        if (length > 0 && length < unit) {
          unit = length;
        }
      }
      if (unit == Long.MAX_VALUE) {
        unit = 1L;
      }
      // accumulate as an exact mean of nanosecond offsets relative to the first span
      LocalDateTime origin = spans.get(0)[0];
      java.math.BigInteger weightedSum = java.math.BigInteger.ZERO;
      java.math.BigInteger totalWeight = java.math.BigInteger.ZERO;
      for (LocalDateTime[] span : spans) {
        long length = Duration.between(span[0], span[1]).toNanos();
        long weight = Math.max(1L, length / unit);
        long midpoint = Duration.between(origin, span[0]).toNanos() + length / 2;
        weightedSum = weightedSum.add(
            java.math.BigInteger.valueOf(weight).multiply(java.math.BigInteger.valueOf(midpoint)));
        totalWeight = totalWeight.add(java.math.BigInteger.valueOf(weight));
      }
      java.math.BigInteger[] division = weightedSum.divideAndRemainder(totalWeight);
      long offset = division[0].longValue();
      // round the remainder to the nearest nanosecond
      if (division[1].abs().shiftLeft(1).compareTo(totalWeight.abs()) >= 0) {
        offset += weightedSum.signum() < 0 ? -1 : 1;
      }
      return origin.plusNanos(offset);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class DateSelect extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr criterion = ast.arg2();
      IAST dates;
      if (isInterval(arg1)) {
        LocalDateTime[] bounds = boundsOf(arg1);
        DateGranularity granularity = granularityOf(arg1);
        if (bounds == null || granularity == null) {
          return F.NIL;
        }
        IASTAppendable list = F.ListAlloc();
        LocalDateTime current = granularity.truncate(bounds[0]);
        int iterations = 0;
        while (current.isBefore(bounds[1]) && ++iterations < 100000) {
          list.append(DateObjectExpr.newInstance(current, granularity));
          LocalDateTime next = granularity.next(current);
          if (!next.isAfter(current)) {
            break;
          }
          current = next;
        }
        dates = list;
      } else if (arg1.isList()) {
        dates = (IAST) arg1;
      } else {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(dates.size());
      for (int i = 1; i < dates.size(); i++) {
        IExpr test = engine.evaluate(F.unaryAST1(criterion, dates.get(i)));
        if (test.isTrue()) {
          result.append(dates.get(i));
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class CalendarConvert extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof DateObjectExpr) || !ast.arg2().isString()) {
        return F.NIL;
      }
      DateObjectExpr dateObject = (DateObjectExpr) ast.arg1();
      String calendar = ast.arg2().toString();
      if (!dateObject.isGregorian()) {
        // only the conversion from the Gregorian calendar is supported
        return F.NIL;
      }
      if (DateObjectExpr.GREGORIAN.equals(calendar)) {
        return dateObject;
      }
      if (!DateObjectExpr.JULIAN.equals(calendar)) {
        return F.NIL;
      }
      LocalDateTime start = dateObject.start();
      int[] julian = toJulianCalendar(start.toLocalDate());
      LocalDateTime julianFields = LocalDateTime.of(julian[0], julian[1], julian[2],
          start.getHour(), start.getMinute(), start.getSecond(), start.getNano());
      return DateObjectExpr.newInstance(julianFields, dateObject.getGranularity(),
          DateObjectExpr.JULIAN, dateObject.getTimeZone(), dateObject.isRealSeconds());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private DateTimeFunctions() {}
}
