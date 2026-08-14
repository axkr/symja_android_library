package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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

  /** The inverse of {@link #toJavaYear(int)}. */
  public static int toDisplayYear(int year) {
    return year <= 0 ? year - 1 : year;
  }

  private DateGranularity fGranularity;

  private String fCalendar;

  private IExpr fTimeZone;

  private boolean fRealSeconds;

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

  public DateGranularity getGranularity() {
    return fGranularity;
  }

  public String getCalendar() {
    return fCalendar;
  }

  public IExpr getTimeZone() {
    return fTimeZone;
  }

  public boolean isRealSeconds() {
    return fRealSeconds;
  }

  public boolean isGregorian() {
    return GREGORIAN.equals(fCalendar);
  }

  /**
   * Return a copy of this date object with a different granularity. The instant is truncated to the
   * new granularity, the time zone is kept.
   */
  public DateObjectExpr withGranularity(DateGranularity granularity) {
    return new DateObjectExpr(granularity.truncate(fData), granularity, fCalendar, fTimeZone,
        fRealSeconds);
  }

  /** Return a copy of this date object which denotes another instant. */
  public DateObjectExpr withDateTime(LocalDateTime dateTime) {
    return new DateObjectExpr(dateTime, fGranularity, fCalendar, fTimeZone, fRealSeconds);
  }

  public DateObjectExpr withTimeZone(IExpr timeZone) {
    return new DateObjectExpr(fData, fGranularity, fCalendar, timeZone, fRealSeconds);
  }

  public DateObjectExpr withRealSeconds(boolean realSeconds) {
    return new DateObjectExpr(fData, fGranularity, fCalendar, fTimeZone, realSeconds);
  }

  /** The first instant of the period this date object denotes. */
  public LocalDateTime start() {
    return fData;
  }

  /**
   * The first instant after the period this date object denotes. Equal to {@link #start()} for
   * {@link DateGranularity#INSTANT}.
   */
  public LocalDateTime end() {
    return fGranularity.next(fData);
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

  /** The seconds component including a possible fraction. */
  public IExpr secondsExpr() {
    int nano = fData.getNano();
    if (fRealSeconds || nano != 0) {
      return F.num(fData.getSecond() + nano / 1.0e9);
    }
    return F.ZZ(fData.getSecond());
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

  @Override
  public int hashCode() {
    int hash = (fData == null) ? 353 : 353 + fData.hashCode();
    return hash + 31 * fGranularity.hashCode();
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

  /**
   * Evaluate <code>DateObject(...)("element")</code>, i.e. this date object applied to a date
   * element name.
   */
  @Override
  public IExpr evaluateHead(IAST ast, org.matheclipse.core.eval.EvalEngine engine) {
    if (ast.isAST1() && ast.arg1().isString()) {
      return org.matheclipse.core.builtin.DateTimeFunctions.dateElement(ast.arg1().toString(),
          fData, this, false);
    }
    return F.NIL;
  }

  @Override
  public int hierarchy() {
    return DATEOBJECTEXPRID;
  }

  @Override
  public IExpr copy() {
    return new DateObjectExpr(fData, fGranularity, fCalendar, fTimeZone, fRealSeconds);
  }

  @Override
  public String toString() {
    return fullForm().toString();
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (LocalDateTime) in.readObject();
    fGranularity = DateGranularity.values()[in.readInt()];
    fCalendar = in.readUTF();
    fTimeZone = (IExpr) in.readObject();
    fRealSeconds = in.readBoolean();
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
