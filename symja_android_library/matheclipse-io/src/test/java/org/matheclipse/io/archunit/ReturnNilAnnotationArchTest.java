package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.tngtech.archunit.core.domain.JavaFieldAccess;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(packages = "org")
public class ReturnNilAnnotationArchTest {

  @ArchTest
  static final ArchRule methods_accessing_F_NIL_must_be_annotated_with_AllowNIL =
      methods().that().areDeclaredInClassesThat().resideInAPackage("org.matheclipse.core.eval")
          .and().haveRawReturnType(IAST.class) // or check isAssignableTo(IExpr.class) in condition
          .should(beAnnotatedWithAllowNILIfUsingFNil());

  private static ArchCondition<JavaMethod> beAnnotatedWithAllowNILIfUsingFNil() {
    return new ArchCondition<>("be annotated with @AllowNIL if accessing F.NIL") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        // Check return type is IExpr or a subtype (IAST, ISymbol, etc.)
        if (!method.getRawReturnType().isAssignableTo(IExpr.class)) {
          return;
        }

        boolean accessesFNil =
            method.getFieldAccesses().stream().anyMatch(ReturnNilAnnotationArchTest::isFNilRead);

        if (accessesFNil && !method.isAnnotatedWith(AllowNIL.class)) {
          String message = String.format(
              "Method %s accesses F.NIL and returns %s, so it must be annotated with @AllowNIL",
              method.getFullName(), method.getRawReturnType().getSimpleName());
          events.add(SimpleConditionEvent.violated(method, message));
        }
      }
    };
  }

  private static boolean isFNilRead(JavaFieldAccess access) {
    return access.getAccessType() == JavaFieldAccess.AccessType.GET
        && access.getTarget().getOwner().isEquivalentTo(F.class)
        && access.getTarget().getName().equals("NIL");
  }
}
