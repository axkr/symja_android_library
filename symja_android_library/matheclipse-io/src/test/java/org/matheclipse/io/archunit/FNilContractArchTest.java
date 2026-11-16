package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import java.util.Set;
import org.matheclipse.core.annotation.NoNIL;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(packages = "org")
public class FNilContractArchTest {

  /**
   * Methods in F that legitimately accept F.NIL as an argument.
   */
  private static final Set<String> ALLOWED_NIL_PARAMETER_METHODS =
      Set.of("isPresent", "isNIL", "orElse", "ifPresent");

  @ArchTest
  static final ArchRule all_public_static_F_methods_must_annotate_IExpr_parameters_with_NoNIL =
      methods().that().areDeclaredIn(F.class)//
          .and().arePublic()//
          .and().areStatic()//
          .and().haveRawReturnType(IAST.class)//
          .and().haveNameNotMatching(String.join("|", ALLOWED_NIL_PARAMETER_METHODS))//
          .should(haveAllIExprParametersAnnotatedWithNoNIL());

  private static ArchCondition<JavaMethod> haveAllIExprParametersAnnotatedWithNoNIL() {
    return new ArchCondition<>("have all IExpr (and subtype) parameters annotated with @NoNIL") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        for (JavaParameter param : method.getParameters()) {
          // Covers IExpr, IAST, ISymbol, INumber, etc.
          if (param.getRawType().isAssignableTo(IExpr.class)
              && !param.isAnnotatedWith(NoNIL.class)) {
            String message = String.format("Parameter %d (%s) of %s is not annotated with @NoNIL",
                param.getIndex(), param.getRawType().getSimpleName(), method.getFullName());
            events.add(SimpleConditionEvent.violated(method, message));
          }
        }
      }
    };
  }
}
