package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.annotation.DefaultNoNIL;
import org.matheclipse.core.annotation.EnsuresPresent;
import org.matheclipse.core.annotation.NILSafe;
import org.matheclipse.core.annotation.NoNIL;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * The {@code F.NIL} contract of the {@link F} builders and of the guard methods.
 */
@ArchTag("slow")
@AnalyzeClasses(packages = "org")
public class FNilContractArchTest {

  /** {@code @NoNIL} is the implicit default for the parameters of the {@code F} builders. */
  @ArchTest
  static final ArchRule F_must_default_to_NoNIL =
      classes().that().haveFullyQualifiedName(F.class.getName()).should()
          .beAnnotatedWith(DefaultNoNIL.class);

  /**
   * Every {@code IExpr} parameter of a public static {@code F} builder must not be {@code F.NIL}:
   * explicitly with {@code @NoNIL} or through {@code @DefaultNoNIL}. A parameter which is designed
   * to receive {@code F.NIL} says so with {@code @NILSafe}; {@code @AllowNIL} is reserved for return
   * values and variables.
   */
  @ArchTest
  static final ArchRule F_builder_parameters_must_follow_the_NIL_contract =
      methods().that().areDeclaredIn(F.class)//
          .and().arePublic()//
          .and().areStatic()//
          .and().haveRawReturnType(IAST.class)//
          .should(haveNoNILOrNILSafeIExprParameters());

  /** A guard is a parameterless boolean method, like {@code isPresent()} and {@code isNIL()}. */
  @ArchTest
  static final ArchRule EnsuresPresent_marks_parameterless_boolean_guards =
      methods().that().areAnnotatedWith(EnsuresPresent.class)//
          .should().haveRawReturnType(boolean.class)//
          .andShould(haveNoParameters());

  private static ArchCondition<JavaMethod> haveNoNILOrNILSafeIExprParameters() {
    return new ArchCondition<>(
        "have IExpr (and subtype) parameters which are @NoNIL (explicitly or by @DefaultNoNIL) or @NILSafe") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        boolean defaultNoNIL = hasDefaultNoNIL(method.getOwner());
        boolean nilSafeMethod = method.isAnnotatedWith(NILSafe.class);
        for (JavaParameter param : method.getParameters()) {
          // Covers IExpr, IAST, ISymbol, INumber, etc.
          if (!param.getRawType().isAssignableTo(IExpr.class)) {
            continue;
          }
          if (param.isAnnotatedWith(AllowNIL.class)) {
            events.add(SimpleConditionEvent.violated(method,
                String.format("Parameter %d (%s) of %s is @AllowNIL; use @NILSafe for parameters",
                    param.getIndex(), param.getRawType().getSimpleName(), method.getFullName())));
          } else if (!defaultNoNIL && !nilSafeMethod && !param.isAnnotatedWith(NoNIL.class)
              && !param.isAnnotatedWith(NILSafe.class)) {
            events.add(SimpleConditionEvent.violated(method,
                String.format("Parameter %d (%s) of %s is neither @NoNIL nor @NILSafe",
                    param.getIndex(), param.getRawType().getSimpleName(), method.getFullName())));
          }
        }
      }
    };
  }

  private static ArchCondition<JavaMethod> haveNoParameters() {
    return new ArchCondition<>("have no parameters") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        if (!method.getParameters().isEmpty()) {
          events.add(SimpleConditionEvent.violated(method,
              method.getFullName() + " is @EnsuresPresent but has parameters"));
        }
      }
    };
  }

  /** {@code @DefaultNoNIL} on the class or one of its enclosing classes. */
  private static boolean hasDefaultNoNIL(JavaClass javaClass) {
    for (JavaClass c = javaClass; c != null; c = c.getEnclosingClass().orElse(null)) {
      if (c.isAnnotatedWith(DefaultNoNIL.class)) {
        return true;
      }
    }
    return false;
  }
}
