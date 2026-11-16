package org.matheclipse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element (typically a method return value) is permitted to be
 * {@code F.NIL}. Callers must check {@code expr.isPresent()} or {@code expr.isNIL()} before passing
 * it to {@code F.*} builder methods.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD})
public @interface AllowNIL {
}
