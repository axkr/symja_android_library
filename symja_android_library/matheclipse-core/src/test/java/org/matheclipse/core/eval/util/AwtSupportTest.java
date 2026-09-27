package org.matheclipse.core.eval.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * The shapes a missing libawt takes on a native image, and the linkage errors that must not be
 * mistaken for it.
 */
public class AwtSupportTest {

  @Test
  public void firstUseWrapsTheLinkError() {
    // what BufferedImage's static initializer throws the first time
    assertTrue(AwtSupport.isMissingNativeLibrary(
        new ExceptionInInitializerError(new UnsatisfiedLinkError("Can't load library: awt"))));
  }

  @Test
  public void bareLinkError() {
    assertTrue(AwtSupport
        .isMissingNativeLibrary(new UnsatisfiedLinkError("Can't load library: awt | java.library.path = [.]")));
  }

  @Test
  public void laterUsesNameTheUninitialisedClass() {
    // every use after the first failure
    assertTrue(AwtSupport.isMissingNativeLibrary(
        new NoClassDefFoundError("Could not initialize class java.awt.image.ColorModel")));
    assertTrue(AwtSupport.isMissingNativeLibrary(
        new NoClassDefFoundError("Could not initialize class javax.imageio.ImageIO")));
  }

  @Test
  public void causeFurtherDownTheChain() {
    NoClassDefFoundError outer =
        new NoClassDefFoundError("Could not initialize class org.matheclipse.image.Something");
    outer.initCause(new ExceptionInInitializerError(new UnsatisfiedLinkError("awt")));
    assertTrue(AwtSupport.isMissingNativeLibrary(outer));
  }

  @Test
  public void anOrdinaryMissingClassIsNotHidden() {
    // a genuinely absent class is a classpath fault and has to keep propagating
    assertFalse(AwtSupport.isMissingNativeLibrary(new NoClassDefFoundError("org/foo/Bar")));
    assertFalse(AwtSupport.isMissingNativeLibrary(new IncompatibleClassChangeError("awt-free")));
    assertFalse(AwtSupport.isMissingNativeLibrary(new NoClassDefFoundError()));
    assertFalse(AwtSupport.isMissingNativeLibrary(null));
  }
}
