package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Parameter;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Wrapper expression that holds a {@link Class} reference.
 *
 * <p>
 * This class extends {@link DataExpr} parameterized with {@code Class<?>} and implements
 * {@link Externalizable} to allow explicit serialization of the contained {@link Class} object.
 *
 * <p>
 * Typical usage: represent Java class objects within the expression tree and assist in reflective
 * method invocation (see {@link #determineParameters}).
 */
public class JavaClassExpr extends DataExpr<Class<?>> implements Externalizable {

  public JavaClassExpr() {
    super(S.JavaClass, null);
  }


  /**
   * Create a new {@code JavaClassExpr} wrapping the supplied class.
   *
   * @param clazz the Java {@link Class} object to wrap
   * @return a new {@code JavaClassExpr} instance containing {@code clazz}
   */
  public static JavaClassExpr newInstance(final Class<?> clazz) {
    return new JavaClassExpr(clazz);
  }

  /**
   * Create a new {@code JavaClassExpr} by loading the class with the provided {@link ClassLoader}.
   *
   * @param className the fully qualified class name to load
   * @param classLoader the class loader to use for loading the class
   * @return a new {@code JavaClassExpr} wrapping the loaded class
   * @throws ClassNotFoundException if the class cannot be found by the loader
   */
  public static JavaClassExpr newInstance(final String className, ClassLoader classLoader)
      throws ClassNotFoundException {
    Class<?> clazz = classLoader.loadClass(className);
    return new JavaClassExpr(clazz);
  }

  /**
   * Protected constructor that wraps the given {@link Class} instance.
   *
   * @param value the {@link Class} to wrap
   */
  protected JavaClassExpr(final Class<?> value) {
    super(S.JavaClass, value);
  }

  /**
   * Equality is based on the wrapped {@link Class} object.
   *
   * @param obj the object to compare
   * @return {@code true} if {@code obj} is the same instance or wraps an equal {@link Class}
   */
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


  /**
   * Determine the Java method call parameters from the given expression arguments and the target
   * method parameters.
   *
   * <p>
   * This method inspects each target parameter's type and attempts to convert the corresponding
   * expression argument into a Java value compatible with that type. Supported conversions include:
   * - direct {@link DataExpr} to underlying data object when it matches the parameter type -
   * boolean, numeric primitives (double, float, int, long, short, byte), char and String -
   * {@link Complex} numbers via {@link IExpr#evalfc()} - {@link Class} via {@link JavaClassExpr}
   *
   * <p>
   * If any argument cannot be converted to the required parameter type, the method returns
   * {@code null}.
   *
   * @param ast the AST expression that contains the method call arguments
   * @param parameters the Java reflection {@link Parameter} array of the target method
   * @param offset the argument offset in {@code ast} where method arguments begin
   * @return an array of Java objects suitable for invoking the method, or {@code null} if
   *         conversion fails for any argument
   */
  public static Object[] determineParameters(final IAST ast, Parameter[] parameters, int offset) {
    try {
      Object[] params = new Object[parameters.length];
      for (int j = 0; j < parameters.length; j++) {
        Parameter p = parameters[j];
        IExpr arg = ast.get(j + offset);
        Class<?> clazz = p.getType();
        if (arg instanceof DataExpr<?>) {
          Object obj = ((DataExpr) arg).toData();
          if (clazz.isInstance(obj)) {
            params[j] = obj;
            continue;
          }
        }
  
        if (clazz.isInstance(arg)) {
          params[j] = arg;
        } else if (clazz.equals(boolean.class)) {
          if (arg.isTrue()) {
            params[j] = Boolean.TRUE;
          } else if (arg.isFalse()) {
            params[j] = Boolean.FALSE;
          } else {
            return null;
          }
        } else if (clazz.equals(double.class)) {
          params[j] = Double.valueOf(arg.evalf());
        } else if (clazz.equals(float.class)) {
          params[j] = Float.valueOf((float) arg.evalf());
        } else if (clazz.equals(int.class)) {
          int n = arg.toIntDefault();
          if (F.isNotPresent(n)) {
            return null;
          }
          params[j] = Integer.valueOf(n);
        } else if (clazz.equals(long.class)) {
          long l = arg.toLongDefault();
          if (F.isNotPresent(l)) {
            return null;
          }
          params[j] = Long.valueOf(l);
        } else if (clazz.equals(short.class)) {
          int s = arg.toIntDefault();
          if (s < Short.MIN_VALUE || s > Short.MAX_VALUE) {
            return null;
          }
          params[j] = Short.valueOf((short) s);
        } else if (clazz.equals(byte.class)) {
          int b = arg.toIntDefault();
          if (b < Byte.MIN_VALUE || b > Byte.MAX_VALUE) {
            return null;
          }
          params[j] = Byte.valueOf((byte) b);
        } else if (clazz.equals(char.class)) {
          if (!arg.isString()) {
            return null;
          }
          String str = arg.toString();
          if (str.length() != 1) {
            return null;
          }
          params[j] = Character.valueOf(str.charAt(0));
        } else if (clazz.equals(String.class)) {
          if (!arg.isString()) {
            return null;
          }
          params[j] = arg.toString();
        } else if (clazz.equals(org.hipparchus.complex.Complex.class)) {
          org.hipparchus.complex.Complex complex = arg.evalfc();
          if (complex == null) {
            return null;
          }
          params[j] = complex;
        } else if (clazz.equals(Class.class) && arg instanceof JavaClassExpr) {
          params[j] = ((JavaClassExpr) arg).toData();
        } else {
          params[j] = arg;
        }
      }
      return params;
    } catch (ArgumentTypeException atex) {
  
    }
    return null;
  }
}
