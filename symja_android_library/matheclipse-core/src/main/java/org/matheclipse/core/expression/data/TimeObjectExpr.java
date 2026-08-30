package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalTime;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Data wrapper for a <code>TimeObject</code>.
 *
 * <p>
 * Beside the {@link LocalTime} an instance carries a {@link DateGranularity} restricted to
 * {@link DateGranularity#HOUR}, {@link DateGranularity#MINUTE} and {@link DateGranularity#INSTANT},
 * and a time zone which is {@link S#None} unless the time object was the result of an arithmetic
 * operation.
 * </p>
 */
public class TimeObjectExpr extends DataExpr<LocalTime> implements Externalizable {

  private static final long serialVersionUID = -8103849790860824975L;

  private DateGranularity fGranularity;

  private IExpr fTimeZone;

  private boolean fRealSeconds;

  public TimeObjectExpr() {
    super(S.TimeObject, null);
    fGranularity = DateGranularity.INSTANT;
    fTimeZone = S.None;
    fRealSeconds = false;
  }

  /** Create a time object with {@link DateGranularity#INSTANT} granularity and no time zone. */
  public static TimeObjectExpr newInstance(final LocalTime value) {
    return new TimeObjectExpr(value, DateGranularity.INSTANT, S.None, false);
  }

  public static TimeObjectExpr newInstance(final LocalTime value,
      final DateGranularity granularity) {
    return new TimeObjectExpr(value, granularity, S.None, false);
  }

  public static TimeObjectExpr newInstance(final LocalTime value, final DateGranularity granularity,
      final IExpr timeZone, final boolean realSeconds) {
    return new TimeObjectExpr(value, granularity, timeZone, realSeconds);
  }

  protected TimeObjectExpr(final LocalTime value, final DateGranularity granularity,
      final IExpr timeZone, final boolean realSeconds) {
    super(S.TimeObject, value);
    fGranularity = granularity;
    fTimeZone = timeZone;
    fRealSeconds = realSeconds;
  }

  public DateGranularity getGranularity() {
    return fGranularity;
  }

  public IExpr getTimeZone() {
    return fTimeZone;
  }

  public TimeObjectExpr withTime(LocalTime time) {
    return new TimeObjectExpr(time, fGranularity, fTimeZone, fRealSeconds);
  }

  public TimeObjectExpr withTimeZone(IExpr timeZone) {
    return new TimeObjectExpr(fData, fGranularity, timeZone, fRealSeconds);
  }

  /** The time list which is printed for this time object; its length depends on the granularity. */
  public IAST timeList() {
    IASTAppendable list = F.ListAlloc(3);
    list.append(F.ZZ(fData.getHour()));
    if (fGranularity != DateGranularity.HOUR) {
      list.append(F.ZZ(fData.getMinute()));
    }
    if (fGranularity != DateGranularity.HOUR && fGranularity != DateGranularity.MINUTE) {
      list.append(secondsExpr());
    }
    return list;
  }

  public IExpr secondsExpr() {
    int nano = fData.getNano();
    if (fRealSeconds || nano != 0) {
      return F.num(fData.getSecond() + nano / 1.0e9);
    }
    return F.ZZ(fData.getSecond());
  }

  @Override
  public IAST fullForm() {
    IASTAppendable result = F.ast(S.TimeObject, 3);
    result.append(timeList());
    result.append(F.stringx(fGranularity.getName()));
    if (fTimeZone != S.None) {
      result.append(fTimeZone);
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TimeObjectExpr) {
      TimeObjectExpr other = (TimeObjectExpr) obj;
      return fData.equals(other.fData) && fGranularity == other.fGranularity
          && fTimeZone.equals(other.fTimeZone);
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
    if (expr instanceof TimeObjectExpr) {
      TimeObjectExpr other = (TimeObjectExpr) expr;
      int result = fData.compareTo(other.fData);
      if (result != 0) {
        return result < 0 ? -1 : 1;
      }
      return fGranularity.compareTo(other.fGranularity);
    }
    return super.compareTo(expr);
  }

  /**
   * Evaluate <code>TimeObject(...)("element")</code>, i.e. this time object applied to a time
   * element name.
   */
  @Override
  public IExpr evaluateHead(IAST ast, org.matheclipse.core.eval.EvalEngine engine) {
    if (ast.isAST1() && ast.arg1().isString()) {
      String name = ast.arg1().toString();
      if ("Granularity".equals(name)) {
        return F.stringx(fGranularity.getName());
      }
      return org.matheclipse.core.expression.data.DateObjectExpr.dateElement(name,
          java.time.LocalDateTime.of(java.time.LocalDate.of(2000, 1, 1), fData), null, false);
    }
    return F.NIL;
  }

  @Override
  public int hierarchy() {
    return TIMEOBJECTEXPRID;
  }

  @Override
  public IExpr copy() {
    return new TimeObjectExpr(fData, fGranularity, fTimeZone, fRealSeconds);
  }

  @Override
  public String toString() {
    return fullForm().toString();
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (LocalTime) in.readObject();
    fGranularity = DateGranularity.values()[in.readInt()];
    fTimeZone = (IExpr) in.readObject();
    fRealSeconds = in.readBoolean();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
    output.writeInt(fGranularity.ordinal());
    output.writeObject(fTimeZone);
    output.writeBoolean(fRealSeconds);
  }
}
