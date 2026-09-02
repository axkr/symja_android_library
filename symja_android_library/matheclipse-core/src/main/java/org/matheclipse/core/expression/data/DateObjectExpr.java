package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Data wrapper for a <code>DateObject</code>.
 *
 * <p>
 * Beside the {@link LocalDateTime} instant an instance carries
 * </p>
 * <ul>
 * <li>a {@link DateGranularity} - the calendar period the date denotes,</li>
 * <li>a calendar type - {@value #GREGORIAN} or {@value #JULIAN}; the wrapped {@link LocalDateTime}
 * always holds the field values <i>as displayed</i> in that calendar,</li>
 * <li>a time zone - {@link S#None} for date only granularities, otherwise the numerical offset to
 * GMT.</li>
 * </ul>
 *
 * <p>
 * The seconds component keeps track of whether it was derived from an inexact number, so that
 * <code>DateObject({2024,2,29,13,5,7})</code> prints an integer while a date computed from a
 * machine number prints a real.
 * </p>
 */
public class DateObjectExpr extends DataExpr<LocalDateTime> implements Externalizable {

  private static final long serialVersionUID = 33260626252103831L;

  public static final String GREGORIAN = "Gregorian";

  public static final String JULIAN = "Julian";

  /** Indexed by {@link DayOfWeek#getValue()} minus one. */
  public static final IBuiltInSymbol[] WEEKDAY_SYMBOLS = new IBuiltInSymbol[] { //
      S.Monday, S.Tuesday, S.Wednesday, S.Thursday, S.Friday, S.Saturday, S.Sunday};

  /**
   * All supported date element names. The index into this array is used by {@link dateElement}.
   */
  public static final String[] DATE_ELEMENTS = new String[] { //
      "Year", "YearShort", "Quarter", "QuarterName", "QuarterNameShort", //
      "Month", "MonthShort", "MonthName", "MonthNameShort", "MonthNameInitial", //
      "Day", "DayShort", "DayName", "DayNameShort", "DayNameInitial", //
      "Hour", "HourShort", "Hour12", "Hour12Short", "Hour24", "Hour24Short", //
      "AMPM", "AMPMLowerCase", //
      "Minute", "MinuteShort", "Second", "SecondShort", "Millisecond", "MillisecondShort", //
      "Week", "WeekShort", "ISOWeek", "ISOWeekYear", "ISOYear", "ISOWeekDay", //
      "ISOYearDay", "ISOYearDayShort", "DayOfYear", "ISOWeekDate", "Granularity"};

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
    int index = DateObjectExpr.dateElementIndex(name);
    if (index < 0) {
      return F.NIL;
    }
    int quarter = (date.getMonthValue() - 1) / 3 + 1;
    switch (name) {
      case "Year":
        return integerOrPadded(toDisplayYear(date.getYear()), 4, asString);
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
        return integerOrPadded(toDisplayYear(date.getYear()), 4, asString);
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

  public static int dateElementIndex(String name) {
    for (int i = 0; i < DateObjectExpr.DATE_ELEMENTS.length; i++) {
      if (DateObjectExpr.DATE_ELEMENTS[i].equals(name)) {
        return i;
      }
    }
    return -1;
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

  /**
   * Create a date object with {@link DateGranularity#INSTANT} granularity in the Gregorian calendar
   * without a time zone.
   */
  public static DateObjectExpr newInstance(final LocalDateTime value) {
    return new DateObjectExpr(value, DateGranularity.INSTANT, GREGORIAN, S.None, false);
  }

  /** Create a date object in the Gregorian calendar. */
  public static DateObjectExpr newInstance(final LocalDateTime value,
      final DateGranularity granularity) {
    return new DateObjectExpr(value, granularity, GREGORIAN,
        granularity.isDateOnly() ? S.None : F.CD0, false);
  }

  public static DateObjectExpr newInstance(final LocalDateTime value,
      final DateGranularity granularity, final String calendar, final IExpr timeZone,
      final boolean realSeconds) {
    return new DateObjectExpr(value, granularity, calendar, timeZone, realSeconds);
  }

  /**
   * Map a weekday symbol or a weekday name string to a {@link DayOfWeek}.
   *
   * @return <code>null</code> if <code>expr</code> is not a weekday specification
   */
  public static DayOfWeek toDayOfWeek(IExpr expr) {
    for (int i = 0; i < DateObjectExpr.WEEKDAY_SYMBOLS.length; i++) {
      if (expr == DateObjectExpr.WEEKDAY_SYMBOLS[i]) {
        return DayOfWeek.of(i + 1);
      }
    }
    if (expr.isString()) {
      String name = expr.toString();
      for (int i = 0; i < DateObjectExpr.WEEKDAY_SYMBOLS.length; i++) {
        if (DateObjectExpr.WEEKDAY_SYMBOLS[i].getSymbolName().equalsIgnoreCase(name)) {
          return DayOfWeek.of(i + 1);
        }
      }
    }
    return null;
  }

  /** The inverse of {@link #toJavaYear(int)}. */
  public static int toDisplayYear(int year) {
    return year <= 0 ? year - 1 : year;
  }

  /**
   * Convert a year as it is written in a date specification to the astronomical year numbering
   * {@link java.time.LocalDate} uses.
   *
   * <p>
   * There is no year zero, so <code>-1</code> denotes 1 BCE which is the astronomical year
   * <code>0</code>. Positive years are the same in both numberings.
   * </p>
   */
  public static int toJavaYear(int year) {
    return year < 0 ? year + 1 : year;
  }

  public static IBuiltInSymbol weekdaySymbol(DayOfWeek dayOfWeek) {
    return DateObjectExpr.WEEKDAY_SYMBOLS[dayOfWeek.getValue() - 1];
  }

  private DateGranularity fGranularity;

  private String fCalendar;

  private IExpr fTimeZone;

  private boolean fRealSeconds;

  /**
   * No-argument constructor required for {@link Externalizable} deserialization.
   */
  public DateObjectExpr() {
    super(S.DateObject, null);
    fGranularity = DateGranularity.INSTANT;
    fCalendar = GREGORIAN;
    fTimeZone = S.None;
    fRealSeconds = false;
  }

  protected DateObjectExpr(final LocalDateTime value, final DateGranularity granularity,
      final String calendar, final IExpr timeZone, final boolean realSeconds) {
    super(S.DateObject, value);
    fGranularity = granularity;
    fCalendar = calendar;
    fTimeZone = timeZone;
    fRealSeconds = realSeconds;
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof DateObjectExpr) {
      DateObjectExpr other = (DateObjectExpr) expr;
      int result = fData.compareTo(other.fData);
      if (result != 0) {
        return result < 0 ? -1 : 1;
      }
      return fGranularity.compareTo(other.fGranularity);
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return new DateObjectExpr(fData, fGranularity, fCalendar, fTimeZone, fRealSeconds);
  }

  /**
   * The date list which is printed for this date object; its length depends on the granularity.
   */
  public IAST dateList() {
    int listSize = fGranularity.getListSize();
    IASTAppendable list = F.ListAlloc(listSize);
    list.append(F.ZZ(toDisplayYear(fData.getYear())));
    if (listSize >= 2) {
      list.append(F.ZZ(fData.getMonthValue()));
    }
    if (listSize >= 3) {
      list.append(F.ZZ(fData.getDayOfMonth()));
    }
    if (listSize >= 4) {
      list.append(F.ZZ(fData.getHour()));
    }
    if (listSize >= 5) {
      list.append(F.ZZ(fData.getMinute()));
    }
    if (listSize >= 6) {
      list.append(secondsExpr());
    }
    return list;
  }

  /**
   * The first instant after the period this date object denotes. Equal to {@link #start()} for
   * {@link DateGranularity#INSTANT}.
   */
  public LocalDateTime end() {
    return fGranularity.next(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DateObjectExpr) {
      DateObjectExpr other = (DateObjectExpr) obj;
      return fData.equals(other.fData) && fGranularity == other.fGranularity
          && fCalendar.equals(other.fCalendar) && fTimeZone.equals(other.fTimeZone);
    }
    return false;
  }

  /**
   * Evaluate <code>DateObject(...)("element")</code>, i.e. this date object applied to a date
   * element name.
   */
  @Override
  public IExpr evaluateHead(IAST ast, org.matheclipse.core.eval.EvalEngine engine) {
    if (ast.isAST1() && ast.arg1().isString()) {
      return org.matheclipse.core.expression.data.DateObjectExpr.dateElement(ast.arg1().toString(),
          fData, this, false);
    }
    return F.NIL;
  }

  /**
   * The <code>DateObject(...)</code> expression which represents this data object. Trailing
   * components which are at their default value are omitted.
   */
  @Override
  public IAST fullForm() {
    boolean printTimeZone = !fGranularity.isDateOnly() || fTimeZone != S.None;
    boolean printCalendar = printTimeZone || !isGregorian();
    IASTAppendable result = F.ast(S.DateObject, 4);
    result.append(dateList());
    result.append(F.stringx(fGranularity.getName()));
    if (printCalendar) {
      result.append(F.stringx(fCalendar));
    }
    if (printTimeZone) {
      result.append(fTimeZone);
    }
    return result;
  }

  public String getCalendar() {
    return fCalendar;
  }

  public DateGranularity getGranularity() {
    return fGranularity;
  }

  public IExpr getTimeZone() {
    return fTimeZone;
  }

  @Override
  public int hashCode() {
    int hash = (fData == null) ? 353 : 353 + fData.hashCode();
    return hash + 31 * fGranularity.hashCode();
  }

  @Override
  public int hierarchy() {
    return DATEOBJECTEXPRID;
  }

  public boolean isGregorian() {
    return GREGORIAN.equals(fCalendar);
  }

  public boolean isRealSeconds() {
    return fRealSeconds;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (LocalDateTime) in.readObject();
    fGranularity = DateGranularity.values()[in.readInt()];
    fCalendar = in.readUTF();
    fTimeZone = (IExpr) in.readObject();
    fRealSeconds = in.readBoolean();
  }

  /** The seconds component including a possible fraction. */
  public IExpr secondsExpr() {
    int nano = fData.getNano();
    if (fRealSeconds || nano != 0) {
      return F.num(fData.getSecond() + nano / 1.0e9);
    }
    return F.ZZ(fData.getSecond());
  }

  /** The first instant of the period this date object denotes. */
  public LocalDateTime start() {
    return fData;
  }

  @Override
  public String toString() {
    return fullForm().toString();
  }

  /** Return a copy of this date object which denotes another instant. */
  public DateObjectExpr withDateTime(LocalDateTime dateTime) {
    return new DateObjectExpr(dateTime, fGranularity, fCalendar, fTimeZone, fRealSeconds);
  }

  /**
   * Return a copy of this date object with a different granularity. The instant is truncated to the
   * new granularity, the time zone is kept.
   */
  public DateObjectExpr withGranularity(DateGranularity granularity) {
    return new DateObjectExpr(granularity.truncate(fData), granularity, fCalendar, fTimeZone,
        fRealSeconds);
  }

  public DateObjectExpr withRealSeconds(boolean realSeconds) {
    return new DateObjectExpr(fData, fGranularity, fCalendar, fTimeZone, realSeconds);
  }

  public DateObjectExpr withTimeZone(IExpr timeZone) {
    return new DateObjectExpr(fData, fGranularity, fCalendar, timeZone, fRealSeconds);
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
    output.writeInt(fGranularity.ordinal());
    output.writeUTF(fCalendar);
    output.writeObject(fTimeZone);
    output.writeBoolean(fRealSeconds);
  }
}
