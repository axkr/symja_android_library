package org.matheclipse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter which intentionally accepts {@code F.NIL}, for example a helper which starts
 * with {@code if (arg.isNIL()) return F.NIL;}. On a method it applies to all of its parameters.
 * <p>
 * It overrides {@link DefaultNoNIL} and distinguishes "this parameter is designed to handle
 * {@code F.NIL}" from "this parameter has not been annotated yet".
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface NILSafe {
}
