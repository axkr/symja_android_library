package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class JavaClassExpr extends DataExpr<Class<?>> implements Externalizable {

  public JavaClassExpr() {
    super(S.JavaClass, null);
  }

  /**
   * @param clazz
   * @return
   */
  public static JavaClassExpr newInstance(final Class<?> clazz) {
    return new JavaClassExpr(clazz);
  }

  public static JavaClassExpr newInstance(final String className, ClassLoader classLoader)
      throws ClassNotFoundException {
    Class<?> clazz = classLoader.loadClass(className);
    return new JavaClassExpr(clazz);
  }

  protected JavaClassExpr(final Class<?> value) {
    super(S.JavaClass, value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JavaClassExpr) {
      return fData.equals(((JavaClassExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return JAVACLASSEXPRID;
  }

  @Override
  public IExpr copy() {
    return new JavaClassExpr(fData);
  }

  @Override
  public String toString() {
    return fHead + "[" + fData.getCanonicalName() + "]";
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (Class<?>) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
