package org.matheclipse.core.eval.util;

/**
 * Recognises the failure a builtin produces when it needs AWT and the runtime has none.
 *
 * <p>
 * A GraalVM native image on macOS ships without libawt (oracle/graal#13272). The first class to
 * need it - <code>BufferedImage</code>, <code>ColorModel</code>, <code>ImageIO</code>,
 * <code>Toolkit</code>, <code>Font</code> - fails in its static initializer with an
 * {@link ExceptionInInitializerError} wrapping an {@link UnsatisfiedLinkError}, and every later use
 * of that class fails with <code>NoClassDefFoundError: Could not initialize class ...</code>. Both
 * are {@link LinkageError}s, which the evaluator does not otherwise catch, so one image function
 * would end the whole process, or a notebook kernel.
 *
 * <p>
 * The test is deliberately narrow: only a missing native library, or a class from the AWT and
 * image I/O packages that could not be initialised. Any other linkage error is a genuine classpath
 * problem and must keep propagating.
 */
public final class AwtSupport {

  private static final String[] AWT_PACKAGES = {"java.awt", "javax.imageio", "sun.awt",
      "sun.java2d", "java/awt", "javax/imageio", "sun/awt", "sun/java2d"};

  private AwtSupport() {}

  /**
   * @return <code>true</code> when <code>problem</code>, or anything in its cause chain, is a
   *         native library that could not be loaded or an AWT class that could not be initialised
   */
  public static boolean isMissingNativeLibrary(Throwable problem) {
    // bounded walk: a cause chain can in principle be cyclic
    Throwable t = problem;
    for (int depth = 0; t != null && depth < 16; depth++) {
      if (t instanceof UnsatisfiedLinkError) {
        return true;
      }
      if ((t instanceof NoClassDefFoundError || t instanceof ExceptionInInitializerError)
          && namesAwt(t.getMessage())) {
        return true;
      }
      if (t.getCause() == t) {
        break;
      }
      t = t.getCause();
    }
    return false;
  }

  private static boolean namesAwt(String message) {
    if (message == null) {
      return false;
    }
    for (String awtPackage : AWT_PACKAGES) {
      if (message.contains(awtPackage)) {
        return true;
      }
    }
    return false;
  }
}
