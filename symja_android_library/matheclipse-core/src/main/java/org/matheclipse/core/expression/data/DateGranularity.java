package org.matheclipse.core.expression.data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The calendar granularity of a {@link DateObjectExpr} or {@link TimeObjectExpr}.
 *
 * <p>
 * A granular date does not denote a single instant but the half open interval which starts at
 * {@link #truncate(LocalDateTime)} and ends at {@link #next(LocalDateTime)}. {@link #INSTANT}
 * denotes a single instant, i.e. an interval of length zero.
 * </p>
 */
public enum DateGranularity {
  YEAR("Year", 1), //
  QUARTER("Quarter", 2), //
  MONTH("Month", 2), //
  WEEK("Week", 3), //
  DAY("Day", 3), //
  HOUR("Hour", 4), //
  MINUTE("Minute", 5), //
  SECOND("Second", 6), //
  INSTANT("Instant", 6);

  private final String fName;

  private final int fListSize;

  private DateGranularity(String name, int listSize) {
    fName = name;
    fListSize = listSize;
  }

  /** The name of this granularity, for example <code>"Day"</code>. */
  public String getName() {
    return fName;
  }

  /** Number of elements of the date list which is printed for this granularity. */
  public int getListSize() {
    return fListSize;
  }

  /**
   * <code>true</code> for granularities which are not finer than {@link #DAY}. Those carry no time
   * of day and therefore no time zone.
   */
  public boolean isDateOnly() {
    return ordinal() <= DAY.ordinal();
  }

  /**
   * Map a granularity name to the enum constant.
   *
   * @return <code>null</code> if <code>name</code> is not a supported granularity
   */
  public static DateGranularity of(String name) {
    for (DateGranularity granularity : values()) {
      if (granularity.fName.equals(name)) {
        return granularity;
      }
    }
    return null;
  }

  /**
   * Map the number of elements of a date list to the granularity the resulting
   * {@link DateObjectExpr} gets.
   *
   * @return <code>null</code> if <code>listSize</code> is not in the range <code>1..6</code>
   */
  public static DateGranularity ofListSize(int listSize) {
    switch (listSize) {
      case 1:
        return YEAR;
      case 2:
        return MONTH;
      case 3:
        return DAY;
      case 4:
        return HOUR;
      case 5:
        return MINUTE;
      case 6:
        return INSTANT;
      default:
        return null;
    }
  }

  /** The first instant of the period which contains <code>dateTime</code>. */
  public LocalDateTime truncate(LocalDateTime dateTime) {
    switch (this) {
      case YEAR:
        return dateTime.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
      case QUARTER:
        int firstMonthOfQuarter = ((dateTime.getMonthValue() - 1) / 3) * 3 + 1;
        return dateTime.withMonth(firstMonthOfQuarter).withDayOfMonth(1)
            .truncatedTo(ChronoUnit.DAYS);
      case MONTH:
        return dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
      case WEEK:
        return dateTime.truncatedTo(ChronoUnit.DAYS)
            .minusDays(dateTime.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
      case DAY:
        return dateTime.truncatedTo(ChronoUnit.DAYS);
      case HOUR:
        return dateTime.truncatedTo(ChronoUnit.HOURS);
      case MINUTE:
        return dateTime.truncatedTo(ChronoUnit.MINUTES);
      case SECOND:
        return dateTime.truncatedTo(ChronoUnit.SECONDS);
      default:
        return dateTime;
    }
  }

  /**
   * The first instant after the period which starts at <code>dateTime</code>. For {@link #INSTANT}
   * the argument is returned unchanged, because an instant has no extent.
   */
  public LocalDateTime next(LocalDateTime dateTime) {
    switch (this) {
      case YEAR:
        return dateTime.plusYears(1);
      case QUARTER:
        return dateTime.plusMonths(3);
      case MONTH:
        return dateTime.plusMonths(1);
      case WEEK:
        return dateTime.plusWeeks(1);
      case DAY:
        return dateTime.plusDays(1);
      case HOUR:
        return dateTime.plusHours(1);
      case MINUTE:
        return dateTime.plusMinutes(1);
      case SECOND:
        return dateTime.plusSeconds(1);
      default:
        return dateTime;
    }
  }
}
