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

public class TimeObjectExpr extends DataExpr<LocalTime> implements Externalizable {

  private static final long serialVersionUID = -8103849790860824974L;

  public TimeObjectExpr() {
    super(S.TimeObject, null);
  }

  /**
   * @param value
   * @return
   */
  public static TimeObjectExpr newInstance(final LocalTime value) {
    return new TimeObjectExpr(value);
  }

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
