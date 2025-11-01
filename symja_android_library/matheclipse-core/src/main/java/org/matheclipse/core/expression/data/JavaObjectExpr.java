package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Wrapper expression that holds an arbitrary Java {@link Object}.
 *
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link Object} and implements
 * {@link Externalizable} so the contained object can be explicitly serialized and deserialized.
 *
 * <p>
 * Typical usage: embed Java objects inside the expression tree so they can be passed through
 * evaluation and retrieved by Java-aware operations.
 */
public class JavaObjectExpr extends DataExpr<Object> implements Externalizable {

  public JavaObjectExpr() {
    super(S.JavaObject, null);
  }

  /**
   * Create a new {@code JavaObjectExpr} that wraps the supplied object.
   *
   * @param object the Java object to wrap (may be {@code null})
   * @return a new {@code JavaObjectExpr} instance containing {@code object}
   */
  public static JavaObjectExpr newInstance(final Object object) {
    return new JavaObjectExpr(object);
  }

  /**
   * Protected constructor that wraps the given object value.
   *
   * @param value the Java object to wrap
   */
  protected JavaObjectExpr(final Object value) {
    super(S.JavaObject, value);
  }

  /**
   * Equality is based on the wrapped object.
   *
   * @param obj the object to compare with
   * @return {@code true} if {@code obj} is the same instance or wraps an equal object
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JavaObjectExpr) {
      return fData.equals(((JavaObjectExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return JAVAOBJECTEXPRID;
  }

  /**
   * Creates a shallow copy of this expression. The copy references the same wrapped object.
   *
   * @return a new {@code JavaObjectExpr} wrapping the same object
   */
  @Override
  public IExpr copy() {
    return new JavaObjectExpr(fData);
  }

  @Override
  public String toString() {
    return fHead + "[" + fData.getClass() + "]";
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
