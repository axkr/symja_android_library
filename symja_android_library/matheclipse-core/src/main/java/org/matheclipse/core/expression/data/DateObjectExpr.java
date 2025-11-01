package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Data wrapper that stores a {@link LocalDateTime}.
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link LocalDateTime} and implements
 * {@link Externalizable} to support custom serialization of the date/time value. Instances are
 * represented with the head symbol {@link S#DateObject}.
 * </p>
 */
public class DateObjectExpr extends DataExpr<LocalDateTime> implements Externalizable {

  private static final long serialVersionUID = 33260626252103830L;

  /**
   * Factory method to create a new {@link DateObjectExpr} containing the supplied date/time.
   *
   * @param value the {@link LocalDateTime} value to wrap
   * @return a new {@link DateObjectExpr} instance containing {@code value}
   */
  public static DateObjectExpr newInstance(final LocalDateTime value) {
    return new DateObjectExpr(value);
  }

  /**
   * No-argument constructor required for {@link Externalizable} deserialization. Initializes the
   * expression with head {@link S#DateObject} and a null payload.
   */
  public DateObjectExpr() {
    super(S.DateObject, null);
  }

  /**
   * Protected constructor that wraps the provided {@link LocalDateTime}.
   *
   * @param position the {@link LocalDateTime} to store as the expression data
   */
  protected DateObjectExpr(final LocalDateTime position) {
    super(S.DateObject, position);
  }

  /**
   * Equality is based on the wrapped {@link LocalDateTime} value.
   *
   * @param obj the object to compare with
   * @return {@code true} if {@code obj} is a {@link DateObjectExpr} whose contained
   *         {@link LocalDateTime} equals this instance's value
   */
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

  /**
   * Return the internal hierarchy id for this expression type.
   *
   * @return the hierarchy id constant @ {@link IExpr#DATEOBJECTEXPRID}
   */
  @Override
  public int hierarchy() {
    return DATEOBJECTEXPRID;
  }

  /**
   * Create a shallow copy of this {@link DateObjectExpr}. The contained {@link LocalDateTime}
   * reference is reused (immutable).
   *
   * @return a new {@link DateObjectExpr} with the same date/time value
   */
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
