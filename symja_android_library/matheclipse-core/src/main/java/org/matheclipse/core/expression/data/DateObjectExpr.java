package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class DateObjectExpr extends DataExpr<LocalDateTime> implements Externalizable {

  private static final long serialVersionUID = 33260626252103830L;

  /**
   * @param value
   * @return
   */
  public static DateObjectExpr newInstance(final LocalDateTime value) {
    return new DateObjectExpr(value);
  }

  public DateObjectExpr() {
    super(S.DateObject, null);
  }

  protected DateObjectExpr(final LocalDateTime position) {
    super(S.DateObject, position);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DateObjectExpr) {
      return fData.equals(((DateObjectExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return DATEOBJECTEXPRID;
  }

  @Override
  public IExpr copy() {
    return new DateObjectExpr(fData);
  }

  @Override
  public String toString() {
    return fData.toString();
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (LocalDateTime) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
