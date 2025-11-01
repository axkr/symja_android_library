package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents a time object as a data expression, wrapping a {@link java.time.LocalTime} instance.
 * This class is {@link Externalizable} for serialization.
 */
public class TimeObjectExpr extends DataExpr<LocalTime> implements Externalizable {

  private static final long serialVersionUID = -8103849790860824974L;

  public TimeObjectExpr() {
    super(S.TimeObject, null);
  }

  /**
   * Factory method to create a new {@code TimeObjectExpr}.
   *
   * @param value The {@link LocalTime} to wrap.
   * @return A new instance of {@code TimeObjectExpr}.
   */
  public static TimeObjectExpr newInstance(final LocalTime value) {
    return new TimeObjectExpr(value);
  }

  /**
   * Protected constructor to initialize with a {@link LocalTime} value.
   *
   * @param value The time value.
   */
  protected TimeObjectExpr(final LocalTime value) {
    super(S.TimeObject, value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TimeObjectExpr) {
      return fData.equals(((TimeObjectExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return TIMEOBJECTEXPRID;
  }

  @Override
  public IExpr copy() {
    return new TimeObjectExpr(fData);
  }

  /**
   * Returns the string representation of the time formatted as {@link DateTimeFormatter#ISO_TIME}.
   */
  @Override
  public String toString() {
    return fData.format(DateTimeFormatter.ISO_TIME);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (LocalTime) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
