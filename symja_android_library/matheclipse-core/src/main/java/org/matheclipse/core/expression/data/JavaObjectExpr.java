package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class JavaObjectExpr extends DataExpr<Object> implements Externalizable {

  public JavaObjectExpr() {
    super(S.JavaObject, null);
  }

  /**
   * @param object
   * @return
   */
  public static JavaObjectExpr newInstance(final Object object) {
    return new JavaObjectExpr(object);
  }

  protected JavaObjectExpr(final Object value) {
    super(S.JavaObject, value);
  }

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
    fData = (Object) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
