package org.matheclipse.errorprone;

import org.junit.jupiter.api.Test;
import com.google.errorprone.CompilationTestHelper;

/**
 * Tests {@link NilToNoNilArgument} against small stand-ins for the Symja annotations,
 * {@code IExpr} and {@code F}.
 */
public class NilToNoNilArgumentTest {

  private CompilationTestHelper helper() {
    return CompilationTestHelper.newInstance(NilToNoNilArgument.class, getClass())
        .addSourceLines("AllowNIL.java", "package org.matheclipse.core.annotation;",
            "public @interface AllowNIL {}")
        .addSourceLines("NoNIL.java", "package org.matheclipse.core.annotation;",
            "public @interface NoNIL {}")
        .addSourceLines("NILSafe.java", "package org.matheclipse.core.annotation;",
            "public @interface NILSafe {}")
        .addSourceLines("DefaultNoNIL.java", "package org.matheclipse.core.annotation;",
            "public @interface DefaultNoNIL {}")
        .addSourceLines("EnsuresPresent.java", "package org.matheclipse.core.annotation;",
            "public @interface EnsuresPresent { boolean value() default true; }")
        .addSourceLines("IExpr.java", "package org.matheclipse.core.interfaces;",
            "import org.matheclipse.core.annotation.EnsuresPresent;", //
            "public interface IExpr {", //
            "  @EnsuresPresent(true) default boolean isPresent() { return true; }",
            "  @EnsuresPresent(false) default boolean isNIL() { return false; }", //
            "}")
        .addSourceLines("IAST.java", "package org.matheclipse.core.interfaces;",
            "public interface IAST extends IExpr {}")
        .addSourceLines("F.java", "package org.matheclipse.core.expression;",
            "import org.matheclipse.core.annotation.*;",
            "import org.matheclipse.core.interfaces.*;", //
            "@DefaultNoNIL", //
            "public class F {", //
            "  public static final IAST NIL = null;", //
            "  public static IAST Expand(IExpr expr) { return null; }",
            "  public static IAST Sin(@NILSafe IExpr expr) { return null; }",
            "  public static IAST list(IExpr... args) { return null; }",
            "  public static IAST count(int n, String name) { return null; }", //
            "}");
  }

  @Test
  public void defaultNoNilParameter() {
    helper().addSourceLines("Use.java", //
        "import org.matheclipse.core.annotation.*;",
        "import org.matheclipse.core.expression.F;",
        "import org.matheclipse.core.interfaces.*;", //
        "class Use {", //
        "  @AllowNIL static IExpr maybe() { return F.NIL; }", //
        "  void run() {", //
        "    // BUG: Diagnostic contains: 'maybe' is @AllowNIL",
        "    F.Expand(maybe());", //
        "    // BUG: Diagnostic contains: 'maybe' is @AllowNIL",
        "    F.list(F.Expand(F.NIL), maybe());", //
        "    F.Sin(maybe());", // @NILSafe
        "  }", //
        "}").doTest();
  }

  @Test
  public void guardsNarrowAllowNilVariables() {
    helper().addSourceLines("Use.java", //
        "import org.matheclipse.core.annotation.*;",
        "import org.matheclipse.core.expression.F;",
        "import org.matheclipse.core.interfaces.*;", //
        "class Use {", //
        "  @AllowNIL static IExpr maybe() { return F.NIL; }", //
        "  IExpr ifPresent() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    if (x.isPresent()) {", //
        "      return F.Expand(x);", //
        "    }", //
        "    // BUG: Diagnostic contains: 'x' is @AllowNIL",
        "    return F.Expand(x);", //
        "  }", //
        "  IExpr earlyReturn() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    if (x.isNIL()) {", //
        "      return F.NIL;", //
        "    }", //
        "    return F.Expand(x);", //
        "  }", //
        "  IExpr conditional() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    return x.isNIL() ? F.NIL : F.Expand(x);", //
        "  }", //
        "  IExpr wrongBranch() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    // BUG: Diagnostic contains: 'x' is @AllowNIL",
        "    return x.isNIL() ? F.Expand(x) : F.NIL;", //
        "  }", //
        "  boolean shortCircuit() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    return x.isPresent() && F.Expand(x) != null", //
        "        || x.isNIL() || F.Expand(x) != null;", //
        "  }", //
        "  IExpr comparison() {", //
        "    @AllowNIL IExpr x = maybe();", //
        "    if (x != F.NIL && !x.isNIL()) {", //
        "      return F.Expand(x);", //
        "    }", //
        "    return F.NIL;", //
        "  }", //
        "}").doTest();
  }
}
