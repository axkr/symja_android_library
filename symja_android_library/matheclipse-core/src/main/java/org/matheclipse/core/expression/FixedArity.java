package org.matheclipse.core.expression;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import java.util.function.ObjIntConsumer;

/**
 * Method bodies shared by the fixed-size AST classes with two arguments ({@link AST2}, {@link B2})
 * and with three arguments ({@link AST3}, {@link B3}). The two hierarchies have no common
 * superclass, so each class passes its head and argument slots in; the JIT inlines these small
 * static methods, so the classes keep their field access speed.
 *
 * <p>
 * <code>a0</code> is the head, <code>a1</code>, <code>a2</code>, <code>a3</code> are the arguments.
 */
final class FixedArity {

  private FixedArity() {}

  // ---- two arguments: size 3 ----

  static boolean exists(Predicate<? super IExpr> predicate, int startOffset, IExpr a0, IExpr a1,
      IExpr a2) {
    switch (startOffset) {
      case 0:
        return predicate.test(a0) || predicate.test(a1) || predicate.test(a2);
      case 1:
        return predicate.test(a1) || predicate.test(a2);
      case 2:
        return predicate.test(a2);
      default:
        return false;
    }
  }

  static IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate, IExpr a1, IExpr a2) {
    filterOne(filterAST, restAST, predicate, a1);
    filterOne(filterAST, restAST, predicate, a2);
    return filterAST;
  }

  static IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, IExpr a1,
      IExpr a2) {
    if (predicate.test(a1)) {
      filterAST.append(a1);
    }
    if (predicate.test(a2)) {
      filterAST.append(a2);
    }
    return filterAST;
  }

  static IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
      Function<IExpr, IExpr> function, IExpr a1, IExpr a2) {
    filterFunctionOne(filterAST, restAST, function, a1);
    filterFunctionOne(filterAST, restAST, function, a2);
    return filterAST;
  }

  static IExpr findFirst(Function<IExpr, IExpr> function, IExpr a1, IExpr a2) {
    IExpr temp = function.apply(a1);
    if (temp.isPresent()) {
      return temp;
    }
    return function.apply(a2);
  }

  static boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset, IExpr a0,
      IExpr a1, IExpr a2) {
    switch (startOffset) {
      case 0:
        return predicate.test(a0, 0) && predicate.test(a1, 1) && predicate.test(a2, 2);
      case 1:
        return predicate.test(a1, 1) && predicate.test(a2, 2);
      case 2:
        return predicate.test(a2, 2);
      default:
        return true;
    }
  }

  static void forEach(int start, int end, ObjIntConsumer<? super IExpr> action, IExpr a0,
      IExpr a1, IExpr a2) {
    if (start < end) {
      switch (start) {
        case 0:
          action.accept(a0, 0);
          if (1 < end) {
            action.accept(a1, 1);
            if (2 < end) {
              action.accept(a2, 2);
            }
          }
          break;
        case 1:
          action.accept(a1, 1);
          if (2 < end) {
            action.accept(a2, 2);
          }
          break;
        case 2:
          action.accept(a2, 2);
          break;
        default:
          throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(start) + ", Size: 3");
      }
    }
  }

  static IExpr get(int location, IExpr a0, IExpr a1, IExpr a2) {
    switch (location) {
      case 0:
        return a0;
      case 1:
        return a1;
      case 2:
        return a2;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
    }
  }

  static int indexOf(IExpr expr, IExpr a1, IExpr a2) {
    if (a1.equals(expr)) {
      return 1;
    }
    if (a2.equals(expr)) {
      return 2;
    }
    return -1;
  }

  static int indexOf(Predicate<? super IExpr> predicate, int fromIndex, IExpr a1, IExpr a2) {
    if (fromIndex == 1 && predicate.test(a1)) {
      return 1;
    }
    if ((fromIndex == 1 || fromIndex == 2) && predicate.test(a2)) {
      return 2;
    }
    return -1;
  }

  // ---- three arguments: size 4 ----

  static boolean exists(Predicate<? super IExpr> predicate, int startOffset, IExpr a0, IExpr a1,
      IExpr a2, IExpr a3) {
    switch (startOffset) {
      case 0:
        return predicate.test(a0) || predicate.test(a1) || predicate.test(a2)
            || predicate.test(a3);
      case 1:
        return predicate.test(a1) || predicate.test(a2) || predicate.test(a3);
      case 2:
        return predicate.test(a2) || predicate.test(a3);
      case 3:
        return predicate.test(a3);
      default:
        return false;
    }
  }

  static boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate, IExpr a1, IExpr a2,
      IExpr a3) {
    return stopPredicate.test(a1, a2) || stopPredicate.test(a2, a3);
  }

  static IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate, IExpr a1, IExpr a2, IExpr a3) {
    filterOne(filterAST, restAST, predicate, a1);
    filterOne(filterAST, restAST, predicate, a2);
    filterOne(filterAST, restAST, predicate, a3);
    return filterAST;
  }

  static IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, IExpr a1,
      IExpr a2, IExpr a3) {
    if (predicate.test(a1)) {
      filterAST.append(a1);
    }
    if (predicate.test(a2)) {
      filterAST.append(a2);
    }
    if (predicate.test(a3)) {
      filterAST.append(a3);
    }
    return filterAST;
  }

  static IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
      Function<IExpr, IExpr> function, IExpr a1, IExpr a2, IExpr a3) {
    filterFunctionOne(filterAST, restAST, function, a1);
    filterFunctionOne(filterAST, restAST, function, a2);
    filterFunctionOne(filterAST, restAST, function, a3);
    return filterAST;
  }

  static IExpr findFirst(Function<IExpr, IExpr> function, IExpr a1, IExpr a2, IExpr a3) {
    IExpr temp = function.apply(a1);
    if (temp.isPresent()) {
      return temp;
    }
    temp = function.apply(a2);
    if (temp.isPresent()) {
      return temp;
    }
    return function.apply(a3);
  }

  static boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset, IExpr a0,
      IExpr a1, IExpr a2, IExpr a3) {
    switch (startOffset) {
      case 0:
        return predicate.test(a0, 0) && predicate.test(a1, 1) && predicate.test(a2, 2)
            && predicate.test(a3, 3);
      case 1:
        return predicate.test(a1, 1) && predicate.test(a2, 2) && predicate.test(a3, 3);
      case 2:
        return predicate.test(a2, 2) && predicate.test(a3, 3);
      case 3:
        return predicate.test(a3, 3);
      default:
        return true;
    }
  }

  static void forEach(int start, int end, ObjIntConsumer<? super IExpr> action, IExpr a0,
      IExpr a1, IExpr a2, IExpr a3) {
    if (start < end) {
      switch (start) {
        case 0:
          action.accept(a0, 0);
          if (1 < end) {
            action.accept(a1, 1);
            if (2 < end) {
              action.accept(a2, 2);
              if (3 < end) {
                action.accept(a3, 3);
              }
            }
          }
          break;
        case 1:
          action.accept(a1, 1);
          if (2 < end) {
            action.accept(a2, 2);
            if (3 < end) {
              action.accept(a3, 3);
            }
          }
          break;
        case 2:
          action.accept(a2, 2);
          if (3 < end) {
            action.accept(a3, 3);
          }
          break;
        case 3:
          action.accept(a3, 3);
          break;
        default:
          throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(start) + ", Size: 4");
      }
    }
  }

  static IExpr get(int location, IExpr a0, IExpr a1, IExpr a2, IExpr a3) {
    switch (location) {
      case 0:
        return a0;
      case 1:
        return a1;
      case 2:
        return a2;
      case 3:
        return a3;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 4");
    }
  }

  static int indexOf(IExpr expr, IExpr a1, IExpr a2, IExpr a3) {
    if (a1.equals(expr)) {
      return 1;
    }
    if (a2.equals(expr)) {
      return 2;
    }
    if (a3.equals(expr)) {
      return 3;
    }
    return -1;
  }

  static int indexOf(Predicate<? super IExpr> predicate, int fromIndex, IExpr a1, IExpr a2,
      IExpr a3) {
    if (fromIndex == 1 && predicate.test(a1)) {
      return 1;
    }
    if ((fromIndex == 1 || fromIndex == 2) && predicate.test(a2)) {
      return 2;
    }
    if ((fromIndex == 1 || fromIndex == 2 || fromIndex == 3) && predicate.test(a3)) {
      return 3;
    }
    return -1;
  }

  // ---- one argument slot ----

  private static void filterOne(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate, IExpr arg) {
    if (predicate.test(arg)) {
      filterAST.append(arg);
    } else {
      restAST.append(arg);
    }
  }

  private static void filterFunctionOne(IASTAppendable filterAST, IASTAppendable restAST,
      Function<IExpr, IExpr> function, IExpr arg) {
    IExpr expr = function.apply(arg);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg);
    }
  }
}
