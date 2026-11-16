package org.matheclipse.errorprone;

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import java.util.List;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.NewClassTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;

/**
 * Reports an argument that may be {@code F.NIL} passed to a parameter declared {@code @NoNIL}.
 *
 * <p>
 * An argument "may be NIL" if it is a call of a method annotated {@code @AllowNIL}, or a variable,
 * field or parameter annotated {@code @AllowNIL}. The annotations are matched by their qualified
 * names, so this module needs no dependency on matheclipse-core. Values flowing through unannotated
 * local variables are not tracked.
 */
@BugPattern(name = "NilToNoNilArgument",
    summary = "An @AllowNIL value is passed to a @NoNIL parameter; check isPresent() first",
    severity = ERROR)
public class NilToNoNilArgument extends BugChecker
    implements MethodInvocationTreeMatcher, NewClassTreeMatcher {
  private static final long serialVersionUID = 1L;

  private static final String ALLOW_NIL = "org.matheclipse.core.annotation.AllowNIL";
  private static final String NO_NIL = "org.matheclipse.core.annotation.NoNIL";

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    return check(ASTHelpers.getSymbol(tree), tree.getArguments(), state);
  }

  @Override
  public Description matchNewClass(NewClassTree tree, VisitorState state) {
    return check(ASTHelpers.getSymbol(tree), tree.getArguments(), state);
  }

  private Description check(MethodSymbol method, List<? extends ExpressionTree> args,
      VisitorState state) {
    if (method == null) {
      return Description.NO_MATCH;
    }
    List<VarSymbol> params = method.getParameters();
    if (params.isEmpty()) {
      return Description.NO_MATCH;
    }
    for (int i = 0; i < args.size(); i++) {
      VarSymbol param = params.get(Math.min(i, params.size() - 1));
      if (!ASTHelpers.hasAnnotation(param, NO_NIL, state)) {
        continue;
      }
      ExpressionTree arg = strip(args.get(i));
      Symbol source = arg instanceof MethodInvocationTree ? ASTHelpers.getSymbol(
          (MethodInvocationTree) arg) : ASTHelpers.getSymbol(arg);
      if (source != null && ASTHelpers.hasAnnotation(source, ALLOW_NIL, state)) {
        return buildDescription(args.get(i))
            .setMessage("'" + source.getSimpleName() + "' is @AllowNIL but parameter '"
                + param.getSimpleName() + "' of " + method.getSimpleName() + " is @NoNIL")
            .build();
      }
    }
    return Description.NO_MATCH;
  }

  private static ExpressionTree strip(ExpressionTree tree) {
    while (true) {
      if (tree instanceof ParenthesizedTree) {
        tree = ((ParenthesizedTree) tree).getExpression();
      } else if (tree instanceof TypeCastTree) {
        tree = ((TypeCastTree) tree).getExpression();
      } else {
        return tree;
      }
    }
  }
}
