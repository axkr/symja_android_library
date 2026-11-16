package org.matheclipse.errorprone;

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.NewClassTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;

/**
 * Reports an argument that may be {@code F.NIL} passed to a parameter which must not be
 * {@code F.NIL}.
 *
 * <p>
 * A parameter must not be {@code F.NIL} if it is annotated {@code @NoNIL}, or if its type is
 * {@code IExpr} (or a subtype) and its method is declared in a class or package annotated
 * {@code @DefaultNoNIL}. {@code @NILSafe} (on the parameter or its method) and {@code @AllowNIL}
 * opt a parameter out of that default. For a varargs parameter the rule applies to each element
 * passed in varargs form.
 *
 * <p>
 * An argument "may be NIL" if it is a call of a method annotated {@code @AllowNIL}, or a variable,
 * field or parameter annotated {@code @AllowNIL}. A variable is accepted where a guard method
 * annotated {@code @EnsuresPresent} (like {@code isPresent()} / {@code isNIL()}) or a comparison
 * with {@code F.NIL} proves it present: in the matching branch of an {@code if}, a conditional
 * expression, {@code &&} / {@code ||}, or after an earlier {@code if (x.isNIL()) return ...;} in
 * an enclosing block. Reassignments between the guard and the use are not tracked, and values
 * flowing through unannotated local variables are not tracked either.
 *
 * <p>
 * The annotations are matched by their qualified names, so this module needs no dependency on
 * matheclipse-core.
 */
@BugPattern(name = "NilToNoNilArgument",
    summary = "An @AllowNIL value is passed to a @NoNIL parameter; check isPresent() first",
    severity = ERROR)
public class NilToNoNilArgument extends BugChecker
    implements MethodInvocationTreeMatcher, NewClassTreeMatcher {
  private static final long serialVersionUID = 1L;

  private static final String ANNOTATION_PACKAGE = "org.matheclipse.core.annotation.";
  private static final String ALLOW_NIL = ANNOTATION_PACKAGE + "AllowNIL";
  private static final String NO_NIL = ANNOTATION_PACKAGE + "NoNIL";
  private static final String NIL_SAFE = ANNOTATION_PACKAGE + "NILSafe";
  private static final String DEFAULT_NO_NIL = ANNOTATION_PACKAGE + "DefaultNoNIL";
  private static final String ENSURES_PRESENT = ANNOTATION_PACKAGE + "EnsuresPresent";
  private static final String IEXPR = "org.matheclipse.core.interfaces.IExpr";
  private static final String F_CLASS = "org.matheclipse.core.expression.F";

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
    // the last argument may be the varargs array itself instead of its first element
    boolean varargsForm = method.isVarArgs() && !(args.size() == params.size()
        && isArray(ASTHelpers.getType(args.get(args.size() - 1))));
    for (int i = 0; i < args.size(); i++) {
      int p = Math.min(i, params.size() - 1);
      VarSymbol param = params.get(p);
      boolean element = varargsForm && p == params.size() - 1;
      if (!isNoNilParameter(method, param, element, state)) {
        continue;
      }
      ExpressionTree arg = strip(args.get(i));
      Symbol source = arg instanceof MethodInvocationTree
          ? ASTHelpers.getSymbol((MethodInvocationTree) arg)
          : ASTHelpers.getSymbol(arg);
      if (source == null || !ASTHelpers.hasAnnotation(source, ALLOW_NIL, state)) {
        continue;
      }
      if (source instanceof VarSymbol
          && provenPresent(new TreePath(state.getPath(), args.get(i)), source, state)) {
        continue;
      }
      return buildDescription(args.get(i))
          .setMessage("'" + source.getSimpleName() + "' is @AllowNIL but parameter '"
              + param.getSimpleName() + "' of " + method.getSimpleName() + " is @NoNIL")
          .build();
    }
    return Description.NO_MATCH;
  }

  /**
   * Test if the parameter (or, for {@code element == true}, each varargs element) must not be
   * {@code F.NIL}.
   */
  private static boolean isNoNilParameter(MethodSymbol method, VarSymbol param, boolean element,
      VisitorState state) {
    if (ASTHelpers.hasAnnotation(param, NIL_SAFE, state)
        || ASTHelpers.hasAnnotation(method, NIL_SAFE, state)
        || ASTHelpers.hasAnnotation(param, ALLOW_NIL, state)) {
      return false;
    }
    if (ASTHelpers.hasAnnotation(param, NO_NIL, state)) {
      return true;
    }
    if (!hasDefaultNoNil(method, state)) {
      return false;
    }
    Type type = param.asType();
    if (element) {
      if (!isArray(type)) {
        return false;
      }
      type = state.getTypes().elemtype(type);
    }
    Type iexpr = state.getTypeFromString(IEXPR);
    return iexpr != null && type != null && ASTHelpers.isSubtype(type, iexpr, state);
  }

  /** {@code @DefaultNoNIL} on an enclosing class or the package of the method. */
  private static boolean hasDefaultNoNil(MethodSymbol method, VisitorState state) {
    for (Symbol owner = method.owner; owner != null; owner = owner.owner) {
      if (ASTHelpers.hasAnnotation(owner, DEFAULT_NO_NIL, state)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if a guard on the enclosing path proves that {@code variable} is not {@code F.NIL} at the
   * leaf of {@code path}.
   */
  private static boolean provenPresent(TreePath path, Symbol variable, VisitorState state) {
    Tree child = path.getLeaf();
    for (TreePath parent = path.getParentPath(); parent != null; parent =
        parent.getParentPath()) {
      Tree tree = parent.getLeaf();
      if (tree instanceof ConditionalExpressionTree) {
        ConditionalExpressionTree conditional = (ConditionalExpressionTree) tree;
        if (child == conditional.getTrueExpression()
            && fact(conditional.getCondition(), true, variable, state) == Boolean.TRUE) {
          return true;
        }
        if (child == conditional.getFalseExpression()
            && fact(conditional.getCondition(), false, variable, state) == Boolean.TRUE) {
          return true;
        }
      } else if (tree instanceof IfTree) {
        IfTree ifTree = (IfTree) tree;
        if (child == ifTree.getThenStatement()
            && fact(ifTree.getCondition(), true, variable, state) == Boolean.TRUE) {
          return true;
        }
        if (child == ifTree.getElseStatement()
            && fact(ifTree.getCondition(), false, variable, state) == Boolean.TRUE) {
          return true;
        }
      } else if (tree instanceof BinaryTree) {
        BinaryTree binary = (BinaryTree) tree;
        if (child == binary.getRightOperand()) {
          if (binary.getKind() == Tree.Kind.CONDITIONAL_AND
              && fact(binary.getLeftOperand(), true, variable, state) == Boolean.TRUE) {
            return true;
          }
          if (binary.getKind() == Tree.Kind.CONDITIONAL_OR
              && fact(binary.getLeftOperand(), false, variable, state) == Boolean.TRUE) {
            return true;
          }
        }
      } else if (tree instanceof BlockTree) {
        // if (x.isNIL()) { return ...; } ... use of x
        for (StatementTree statement : ((BlockTree) tree).getStatements()) {
          if (statement == child) {
            break;
          }
          if (statement instanceof IfTree) {
            IfTree ifTree = (IfTree) statement;
            if (ifTree.getElseStatement() == null && exits(ifTree.getThenStatement())
                && fact(ifTree.getCondition(), false, variable, state) == Boolean.TRUE) {
              return true;
            }
          }
        }
      } else if (tree instanceof MethodTree || tree instanceof ClassTree) {
        return false;
      }
      child = tree;
    }
    return false;
  }

  /**
   * What the condition evaluating to {@code truth} says about {@code variable}: {@code TRUE} it is
   * present, {@code FALSE} it is {@code F.NIL}, {@code null} nothing is known.
   */
  private static Boolean fact(ExpressionTree condition, boolean truth, Symbol variable,
      VisitorState state) {
    condition = strip(condition);
    if (condition instanceof MethodInvocationTree) {
      MethodInvocationTree invocation = (MethodInvocationTree) condition;
      if (!invocation.getArguments().isEmpty()) {
        return null;
      }
      ExpressionTree receiver = ASTHelpers.getReceiver(invocation);
      if (receiver == null || ASTHelpers.getSymbol(strip(receiver)) != variable) {
        return null;
      }
      Boolean presentWhenTrue = ensuresPresent(ASTHelpers.getSymbol(invocation), state);
      return presentWhenTrue == null ? null : Boolean.valueOf(presentWhenTrue == truth);
    }
    if (condition instanceof UnaryTree
        && condition.getKind() == Tree.Kind.LOGICAL_COMPLEMENT) {
      return fact(((UnaryTree) condition).getExpression(), !truth, variable, state);
    }
    if (condition instanceof BinaryTree) {
      BinaryTree binary = (BinaryTree) condition;
      switch (binary.getKind()) {
        case CONDITIONAL_AND:
          // a && b is true: both are true; false: at least one is false
          return truth ? either(binary, true, variable, state)
              : both(binary, false, variable, state);
        case CONDITIONAL_OR:
          // a || b is false: both are false; true: at least one is true
          return truth ? both(binary, true, variable, state)
              : either(binary, false, variable, state);
        case EQUAL_TO:
        case NOT_EQUAL_TO:
          if (comparesWithNil(binary, variable)) {
            boolean equal = (binary.getKind() == Tree.Kind.EQUAL_TO) == truth;
            return Boolean.valueOf(!equal);
          }
          return null;
        default:
          return null;
      }
    }
    return null;
  }

  private static Boolean either(BinaryTree binary, boolean truth, Symbol variable,
      VisitorState state) {
    Boolean left = fact(binary.getLeftOperand(), truth, variable, state);
    return left != null ? left : fact(binary.getRightOperand(), truth, variable, state);
  }

  private static Boolean both(BinaryTree binary, boolean truth, Symbol variable,
      VisitorState state) {
    Boolean left = fact(binary.getLeftOperand(), truth, variable, state);
    Boolean right = fact(binary.getRightOperand(), truth, variable, state);
    return left != null && left.equals(right) ? left : null;
  }

  /** {@code variable == F.NIL} or {@code F.NIL == variable} (also with {@code !=}). */
  private static boolean comparesWithNil(BinaryTree binary, Symbol variable) {
    Symbol left = ASTHelpers.getSymbol(strip(binary.getLeftOperand()));
    Symbol right = ASTHelpers.getSymbol(strip(binary.getRightOperand()));
    return (left == variable && isNil(right)) || (right == variable && isNil(left));
  }

  private static boolean isNil(Symbol symbol) {
    return symbol instanceof VarSymbol && symbol.getSimpleName().contentEquals("NIL")
        && symbol.owner != null && symbol.owner.getQualifiedName().contentEquals(F_CLASS);
  }

  /**
   * The {@code value()} of the {@code @EnsuresPresent} annotation on the method or a method it
   * overrides, or {@code null} if the method is no guard.
   */
  private static Boolean ensuresPresent(MethodSymbol method, VisitorState state) {
    if (method == null) {
      return null;
    }
    Boolean value = ensuresPresentValue(method);
    if (value != null) {
      return value;
    }
    for (MethodSymbol superMethod : ASTHelpers.findSuperMethods(method, state.getTypes())) {
      value = ensuresPresentValue(superMethod);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Boolean ensuresPresentValue(MethodSymbol method) {
    for (AnnotationMirror mirror : method.getAnnotationMirrors()) {
      Name name = ((javax.lang.model.element.TypeElement) mirror.getAnnotationType().asElement())
          .getQualifiedName();
      if (!name.contentEquals(ENSURES_PRESENT)) {
        continue;
      }
      for (java.util.Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror
          .getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals("value")) {
          return (Boolean) entry.getValue().getValue();
        }
      }
      return Boolean.TRUE;
    }
    return null;
  }

  /** The statement always leaves the enclosing block. */
  private static boolean exits(StatementTree statement) {
    if (statement instanceof ReturnTree || statement instanceof ThrowTree
        || statement instanceof BreakTree || statement instanceof ContinueTree) {
      return true;
    }
    if (statement instanceof BlockTree) {
      List<? extends StatementTree> statements = ((BlockTree) statement).getStatements();
      return !statements.isEmpty() && exits(statements.get(statements.size() - 1));
    }
    if (statement instanceof IfTree) {
      IfTree ifTree = (IfTree) statement;
      return ifTree.getElseStatement() != null && exits(ifTree.getThenStatement())
          && exits(ifTree.getElseStatement());
    }
    return false;
  }

  private static boolean isArray(Type type) {
    return type != null && type.getKind() == javax.lang.model.type.TypeKind.ARRAY;
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
