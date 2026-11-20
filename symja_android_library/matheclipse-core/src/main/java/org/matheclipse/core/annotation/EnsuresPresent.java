package org.matheclipse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a boolean guard method of {@code IExpr} which tells whether its receiver is {@code F.NIL}.
 * <p>
 * With {@code value() == true} (for example {@code isPresent()}) a {@code true} result means the
 * receiver is not {@code F.NIL}. With {@code value() == false} (for example {@code isNIL()}) a
 * {@code true} result means the receiver is {@code F.NIL}.
 * <p>
 * Static checks use the guard to narrow an {@link AllowNIL} variable to a {@link NoNIL} value inside
 * the branch where the guard proved it present, e.g.
 *
 * <pre>
 * &#64;AllowNIL
 * IExpr temp = ...;
 * if (temp.isPresent()) {
 *   return F.Expand(temp); // allowed: temp is not F.NIL here
 * }
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface EnsuresPresent {
  /**
   * {@code true} if a {@code true} result of the guard means "not {@code F.NIL}", {@code false} if
   * it means "is {@code F.NIL}".
   */
  boolean value() default true;
}
