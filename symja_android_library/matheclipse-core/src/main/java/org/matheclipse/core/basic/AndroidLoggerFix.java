package org.matheclipse.core.basic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.logging.log4j.util.StackLocator;

public final class AndroidLoggerFix {

  /**
   * {@link StackLocator}
   */
  public static void fix() {
    // Windows: Oracle Corporation
    // Android: The Android Project
    String vendorString = System.getProperty("java.specification.vendor");
    if (vendorString.contains("Android")) {
      try {
        // noinspection ResultOfMethodCallIgnored
        StackLocator.getInstance(); // call static init

        Class<StackLocator> stackLocatorClass = StackLocator.class;
        Field get_caller_class_method =
            stackLocatorClass.getDeclaredField("GET_CALLER_CLASS_METHOD");
        get_caller_class_method.setAccessible(true);
        if (get_caller_class_method.get(null) == null) {
          Class<AndroidLoggerFix> kDebugClass = AndroidLoggerFix.class;
          Method getCallerClassName =
              kDebugClass.getDeclaredMethod("getCallerClassName", int.class);
          get_caller_class_method.set(null, getCallerClassName);

          int magicNumber = 4;
          Field jdk_7U25_offset = stackLocatorClass.getDeclaredField("JDK_7U25_OFFSET");
          jdk_7U25_offset.setAccessible(true);
          jdk_7U25_offset.set(null, magicNumber);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static Class<?> getCallerClassName(int arg) throws ClassNotFoundException {
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    if (stElements.length > arg) {
      String className = stElements[arg].getClassName();
      return Class.forName(className);
    }
    return null;
  }
}
