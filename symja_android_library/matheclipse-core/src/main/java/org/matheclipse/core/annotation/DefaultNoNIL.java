package org.matheclipse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes {@link NoNIL} the default for every {@code IExpr} (or subtype) parameter of the methods
 * declared in the annotated class (including its nested classes) or package.
 * <p>
 * A parameter which is intended to receive {@code F.NIL} opts out with {@link NILSafe}. For a
 * varargs parameter whose component type is {@code IExpr} (or a subtype) the default applies to
 * every element passed in varargs form.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface DefaultNoNIL {
}
