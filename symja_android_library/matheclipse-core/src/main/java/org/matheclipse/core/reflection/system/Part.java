package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * Part(expr, i)
 * </pre>
 *
 * <p>
 * or
 *
 * <pre>
* expr[[i]]
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns part <code>i</code> of <code>expr</code>.
 *
 * </blockquote>
 *
 * <p>
 * Extract an element from a list:
 *
 * <pre>
* &gt;&gt; A = {a, b, c, d}
* &gt;&gt; A[[3]]
* c
 * </pre>
 *
 * <p>
 * Negative indices count from the end:
 *
 * <pre>
* &gt;&gt; {a, b, c}[[-2]]
* b
 * </pre>
 *
 * <p>
 * <code>Part</code> can be applied on any expression, not necessarily lists.
 *
 * <pre>
* &gt;&gt; (a + b + c)[[2]]
* b
 * </pre>
 *
 * <p>
 * <code>expr[[0]]</code> gives the head of <code>expr</code>:
 *
 * <pre>
* &gt;&gt; (a + b + c)[[0]]
* Plus
 * </pre>
 *
 * <p>
 * Parts of nested lists:
 *
 * <pre>
* &gt;&gt; M = {{a, b}, {c, d}}
* &gt;&gt; M[[1, 2]]
* b
 * </pre>
 *
 * <p>
 * You can use <code>Span</code> to specify a range of parts:
 *
 * <pre>
* &gt;&gt; {1, 2, 3, 4}[[2;;4]]
* {2,3,4}
*
* &gt;&gt; {1, 2, 3, 4}[[2;;-1]]
* {2,3,4}
 * </pre>
 *
 * <p>
 * A list of parts extracts elements at certain indices:
 *
 * <pre>
* &gt;&gt; {a, b, c, d}[[{1, 3, 3}]]
* {a,c,c}
 * </pre>
 *
 * <p>
 * Get a certain column of a matrix:
 *
 * <pre>
* &gt;&gt; B = {{a, b, c}, {d, e, f}, {g, h, i}}
* &gt;&gt; B[[;;, 2]]
* {b, e, h}
 * </pre>
 *
 * <p>
 * Extract a submatrix of 1st and 3rd row and the two last columns:
 *
 * <pre>
* &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
* &gt;&gt; B[[{1, 3}, -2;;-1]]
* {{2,3},{8,9}}
 * </pre>
 *
 * <p>
 * Further examples:
 *
 * <pre>
* &gt;&gt; (a+b+c+d)[[-1;;-2]]
* 0
 * </pre>
 *
 * <p>
 * Part specification is longer than depth of object.
 *
 * <pre>
* &gt;&gt; x[[2]]
* x[[2]]
 * </pre>
 *
 * <p>
 * Assignments to parts are possible:
 *
 * <pre>
* &gt;&gt; B[[;;, 2]] = {10, 11, 12}
* {10, 11, 12}
*
* &gt;&gt; B
* {{1, 10, 3}, {4, 11, 6}, {7, 12, 9}}
*
* &gt;&gt; B[[;;, 3]] = 13
* 13
*
* &gt;&gt; B
* {{1, 10, 13}, {4, 11, 13}, {7, 12, 13}}
*
* &gt;&gt; B[[1;;-2]] = t
* &gt;&gt; B
* {t,t,{7,12,13}}
*
* &gt;&gt; F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3})
* &gt;&gt; F[[;; All, 2 ;; 3, 2]] = t
* &gt;&gt; F
* {{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}
*
* &gt;&gt; F[[;; All, 1 ;; 2, 3 ;; 3]] = k
* &gt;&gt; F
* {{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}
 * </pre>
 *
 * <p>
 * Of course, part specifications have precedence over most arithmetic operations:
 *
 * <pre>
* &gt;&gt; A[[1]] + B[[2]] + C[[3]] // Hold // FullForm
* "Hold(Plus(Plus(Part(A, 1), Part(B, 2)), Part(C, 3)))"
*
* &gt;&gt; a = {2,3,4}; i = 1; a[[i]] = 0; a
* {0, 3, 4}
 * </pre>
 *
 * <p>
 * Negative step
 *
 * <pre>
* &gt;&gt; {1,2,3,4,5}[[3;;1;;-1]]
* {3,2,1}
*
* &gt;&gt; {1, 2, 3, 4, 5}[[;; ;; -1]]
* {5, 4, 3, 2, 1}
*
* &gt;&gt; Range(11)[[-3 ;; 2 ;; -2]]
* {9,7,5,3}
*
* &gt;&gt; Range(11)[[-3 ;; -7 ;; -3]]
* {9,6}
*
* &gt;&gt; Range(11)[[7 ;; -7;; -2]]
* {7,5}
 * </pre>
 *
 * <p>
 * Cannot take positions <code>1</code> through <code>3</code> in <code>{1, 2, 3, 4}</code>.
 *
 * <pre>
* &gt;&gt; {1, 2, 3, 4}[[1;;3;;-1]]
* {1,2,3,4}[[1;;3;;-1]]
 * </pre>
 *
 * <p>
 * Cannot take positions <code>3</code> through <code>1</code> in <code>{1, 2, 3, 4}</code>.
 *
 * <pre>
* &gt;&gt; {1, 2, 3, 4}[[3;;1]]
* {1,2,3,4}[[3;;1]]
 * </pre>
 */
public final class Part extends AbstractFunctionEvaluator implements ISetEvaluator {

  private static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition,
      IAST rhs, int rhsPos, EvalEngine engine) {
    if (!assignedExpr.isASTOrAssociation() || partPosition >= part.size()) {
      return assignedExpr;
    }
    IAST assignedAST = (IAST) assignedExpr;
    final IExpr arg2 = part.get(partPosition);
    int partPositionPlus1 = partPosition + 1;
    int[] span = arg2.isSpan(assignedAST.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      IASTAppendable result = F.NIL;

      if (step < 0 && start >= last) {
        int rhsIndx = 1;
        for (int i = start; i >= last; i += step) {
          IExpr temp = rhs.get(rhsIndx++);
          if (!temp.isList()) {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
          } else {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
          }

          if (temp.isPresent()) {
            if (result.isNIL()) {
              result = assignedAST.copyAppendable();
            }
            result.set(i, temp);
          }
        }
      } else if (step > 0 && (last != 1 || start <= last)) {
        int rhsIndx = 1;
        for (int i = start; i <= last; i += step) {
          IExpr temp = rhs.get(rhsIndx++);
          if (!temp.isList()) {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
          } else {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
          }

          if (temp.isPresent()) {
            if (result.isNIL()) {
              result = assignedAST.copyAppendable();
            }
            result.set(i, temp);
          }
        }
      } else {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
      }
      return result;
    } else if (arg2.isReal()) {
      final int indx = Validate.checkIntType(part, partPosition, Integer.MIN_VALUE);
      IExpr ires = null;
      ires = assignPartValue(assignedAST, indx, rhs);
      if (partPositionPlus1 < part.size()) {
        if (ires.isASTOrAssociation()) {
          return assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
        } else {
          // Part `1` of `2` does not exist.
          return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
              engine);
        }
      }
      return ires;
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isInteger()) {
          IExpr ires = null;

          final int indx = Validate.throwIntType(listArg, Integer.MIN_VALUE, engine);
          ires = assignPartValue(assignedAST, indx, list);
          if (ires == null) {
            return F.NIL;
          }
          if (partPositionPlus1 < part.size()) {
            if (ires.isASTOrAssociation()) {
              temp = assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
              result.append(temp);
            } else {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
                  engine);
            }
          } else {
            result.append(ires);
          }
        }
      }
      return result;
    }
    // Part `1` of `2` does not exist.
    return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
  }

  private static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition,
      IExpr value, EvalEngine engine) {
    if (partPosition >= part.size()) {
      // stop recursion
      return value;
    }
    if (!assignedExpr.isASTOrAssociation()) {
      // Part specification `1` is longer than depth of object.
      return Errors.printMessage(S.Part, "partd", F.list(part), engine);
    }
    IAST assignedAST = (IAST) assignedExpr;
    final IExpr arg2 = engine.evaluate(part.get(partPosition));
    int partPositionPlus1 = partPosition + 1;
    int[] span = arg2.isSpan(assignedAST.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      IASTAppendable result = F.NIL;
      IExpr element;

      if (step < 0 && start >= last) {
        for (int i = start; i >= last; i += step) {
          element = assignedAST.get(i);
          result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i,
              value, engine);
        }
      } else if (step > 0 && (last != 1 || start <= last)) {
        for (int i = start; i <= last; i += step) {
          element = assignedAST.get(i);
          result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i,
              value, engine);
        }
      } else {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), arg2), engine);
      }
      return result;
    } else if (arg2.isReal()) {
      int indx = Validate.throwIntType(arg2, Integer.MIN_VALUE, engine);
      if (indx < 0) {
        indx = assignedAST.size() + indx;
      }
      if ((indx < 0) || (indx >= assignedAST.size())) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(indx), assignedAST), engine);
      }
      IASTAppendable result = F.NIL;
      IExpr temp = assignPart(assignedAST.get(indx), part, partPositionPlus1, value, engine);
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = assignedAST.copyAppendable();
        }
        result.set(indx, temp);
      }
      return result;
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isInteger()) {
          IExpr ires = null;

          final int indx = Validate.throwIntType(listArg, Integer.MIN_VALUE, engine);
          ires = assignPartValue(assignedAST, indx, value);
          if (ires == null) {
            return F.NIL;
          }
          if (partPositionPlus1 < part.size()) {
            if (ires.isASTOrAssociation()) {
              temp = assignPart(ires, part, partPositionPlus1, value, engine);
              result.append(temp);
            } else {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
                  engine);
            }
          } else {
            result.append(ires);
          }
        }
      }
      return result;
    }
    // Part `1` of `2` does not exist.
    return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
  }

  /**
   * Call <code>assignPart(element, ast, pos, value, engine)</code> recursively and assign the
   * result to the given position in the result. <code>result[[position]] = resultValue</code>
   *
   * @param expr
   * @param element
   * @param part
   * @param partPosition
   * @param result will be cloned if an assignment occurs and returned by this method
   * @param position
   * @param value
   * @param engine the evaluation engineF
   * @return the (cloned and value assigned) result AST from input
   */
  private static IASTAppendable assignPartSpanValue(IAST expr, IExpr element, final IAST part,
      int partPosition, IASTAppendable result, int position, IExpr value, EvalEngine engine) {
    IExpr resultValue = assignPart(element, part, partPosition, value, engine);
    if (resultValue.isPresent()) {
      if (result.isNIL()) {
        result = expr.copyAppendable();
      }
      result.set(position, resultValue);
    }
    return result;
  }

  /**
   * Assign the <code>value</code> to the given position in the left-hand-side. <code>
   * lhs[[position]] = value</code>
   *
   * @param lhs left-hand-side
   * @param partPosition
   * @param value
   * @return
   */
  private static IExpr assignPartValue(IAST lhs, int partPosition, IExpr value) {
    if (partPosition < 0) {
      partPosition = lhs.size() + partPosition;
    }
    if ((partPosition < 0) || (partPosition >= lhs.size())) {
      throw new ArgumentTypeException(
          "Part: index " + partPosition + " of " + lhs.toString() + " is out of bounds.");
    }
    return lhs.setAtCopy(partPosition, value);
  }

  /**
   * Get the element stored at the given <code>position</code>.
   *
   * @param ast
   * @param pos
   * @param engine
   * @return
   */
  private static IExpr getIndex(IAST ast, final int pos, EvalEngine engine) {
    int position = pos;
    if (position < 0) {
      position = ast.size() + position;
    }
    if ((position < 0) || (position >= ast.size())) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(pos), ast), engine);
    }
    return ast.get(position);
  }

  /**
   * If <code>ast</code> is an instance of IAssociation return the rule defined at the given
   * position in the association. Otherwise return the element at that position.
   *
   * @param ast
   * @param pos
   * @param engine
   * @return
   */
  private static IExpr getIndexRule(IAST ast, final int pos, EvalEngine engine) {
    int position = pos;
    if (position < 0) {
      position = ast.size() + position;
    }
    if ((position < 0) || (position >= ast.size())) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(pos), ast), engine);
    }
    return ast.getRule(position);
  }


  /**
   * Get the <code>Part[...]</code> of an expression. If the expression is no <code>IAST</code>
   * return the expression.
   *
   * @param arg1 the expression from which parts should be extracted
   * @param ast the <code>Part[...]</code> expression
   * @param pos the index position from which the sub-expressions should be extracted
   * @param engine the evaluation engine
   * @return
   */
  public static IExpr part(final IAST arg1, final IAST ast, int pos, EvalEngine engine) {
    final IExpr arg2 = engine.evaluate(ast.get(pos));
    int p1 = pos + 1;
    int[] span = arg2.isSpan(arg1.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      return spanPart(ast, pos, arg1, arg2, start, last, step, p1, engine);
    } else if (arg2.equals(S.All)) {
      return spanPart(ast, pos, arg1, arg2, 1, arg1.argSize(), 1, p1, engine);
    } else if (arg2.isReal()) {
      final int indx = ast.get(pos).toIntDefault();
      if (F.isNotPresent(indx)) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(ast.get(pos), arg1), engine);
      }
      IExpr result = getIndex(arg1, indx, engine);
      if (result.isPresent()) {
        if (p1 < ast.size()) {
          if (result.isASTOrAssociation()) {
            return part((IAST) result, ast, p1, engine);
          } else if (result.isSparseArray()) {
            return sparsePart((ISparseArray) result, ast, p1, engine);
          } else {
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(S.Part, "partd", F.list(result), engine);
          }
        }
        return result;
      }
      return F.NIL;
    } else if (arg1.isAssociation()) {
      IAssociation assoc = (IAssociation) arg1;
      if (arg2.isList()) {
        IExpr temp = null;
        final IAST list = (IAST) arg2;
        final IAssociation result = F.assoc(); // list.size());

        for (int i = 1; i < list.size(); i++) {
          final IExpr listArg = list.get(i);
          if (listArg.isReal()) {
            final int indx = listArg.toIntDefault();
            if (F.isNotPresent(indx)) {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(listArg, arg1), engine);
            }
            IExpr ires = getIndexRule(arg1, indx, engine);
            if (ires.isPresent()) {
              if (p1 < ast.size()) {
                if (ires.isASTOrAssociation()) {

                  temp = part((IAST) ires, ast, p1, engine);
                  if (temp.isPresent()) {
                    try {
                      result.appendRule(temp);
                    } catch (IndexOutOfBoundsException ioobex) {
                      return Errors.printMessage(S.Part, "pkspec1", F.list(temp), engine);
                    }
                  } else {
                    // an error occurred
                    return F.NIL;
                  }

                } else {
                  // Part specification `1` is longer than depth of object.
                  return Errors.printMessage(S.Part, "partd", F.list(ires), engine);
                }
              } else {
                try {
                  result.appendRule(ires);
                } catch (IndexOutOfBoundsException ioobex) {
                  return Errors.printMessage(S.Part, "pkspec1", F.list(ires), engine);
                }
              }
            } else {
              return F.NIL;
            }
          } else if (listArg.isKey()) {
            result.appendRule(assoc.getRule(listArg.first()));
          } else if (listArg.isString()) {
            result.appendRule(assoc.getRule(listArg));
          } else if (listArg.isNumber()) {
            // The expression `1` cannot be used as a part specification.
            return Errors.printMessage(S.Part, "pkspec1", F.list(list), engine);
          }
        }
        return result;
      }

      IExpr result = F.NIL;
      if (arg2.isKey()) {
        result = assoc.getValue(arg2.first());
      } else if (arg2.isString()) {
        result = assoc.getValue(arg2);
      }

      if (result.isPresent()) {
        if (p1 < ast.size()) {
          if (result.isASTOrAssociation()) {
            return part((IAST) result, ast, p1, engine);
          } else {
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(S.Part, "partd", F.list(result), engine);
          }
        }
        return result;
      }
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ast(arg1.head(), list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isReal()) {
          final int indx = listArg.toIntDefault();
          if (F.isNotPresent(indx)) {
            // Part `1` of `2` does not exist.
            return Errors.printMessage(S.Part, "partw", F.list(listArg, arg1), engine);
          }
          IExpr ires = getIndex(arg1, indx, engine);
          if (ires.isPresent()) {
            if (p1 < ast.size()) {
              if (ires.isASTOrAssociation()) {
                temp = part((IAST) ires, ast, p1, engine);
                if (temp.isPresent()) {
                  result.append(temp);
                } else {
                  // an error occurred
                  return F.NIL;
                }
              } else {
                // Part specification `1` is longer than depth of object.
                return Errors.printMessage(S.Part, "partd", F.list(ires), engine);
              }
            } else {
              result.append(ires);
            }
          } else {
            return F.NIL;
          }
        } else if (listArg.isNumber() || listArg.isString()) {
          // The expression `1` cannot be used as a part specification.
          return Errors.printMessage(S.Part, "pkspec1", F.list(list), engine);
        }
      }
      return result;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(arg2), engine);
  }

  private static IExpr spanPart(final IAST ast, int pos, final IAST arg1, final IExpr arg2,
      int start, int last, int step, int p1, EvalEngine engine) {

    final int size = arg1.size();
    if (step < 0 && start >= last) {
      IASTAppendable result = arg1.copyHead((last - start) / step + 2);
      for (int i = start; i >= last; i += step) {
        if (p1 >= ast.size()) {
          IExpr temp = getIndexRule(arg1, i, engine);
          if (temp.isPresent()) {
            result.appendRule(temp);
            continue;
          }
          return F.NIL;
        }
        if (arg1.get(i).isASTOrAssociation()) {
          if (i >= size) {
            // Cannot take positions `1` through `2` in `3`.
            return Errors.printMessage(S.Part, "take", F.list(F.ZZ(start), F.ZZ(last), arg1),
                engine);
          }
          IExpr temp = part((IAST) arg1.get(i), ast, p1, engine);
          if (temp.isPresent()) {
            result.append(temp);
            continue;
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(S.Part, "partd", F.list(arg1.get(i)), engine);
      }
      return result;
    } else if (step > 0 && (last != 1 || start <= last)) {
      IASTAppendable result = arg1.copyHead((last - start) / step + 2);
      for (int i = start; i <= last; i += step) {
        if (p1 >= ast.size()) {
          IExpr temp = getIndexRule(arg1, i, engine);
          if (temp.isPresent()) {
            result.appendRule(temp);
            continue;
          }
          return F.NIL;
        }
        if (arg1.get(i).isASTOrAssociation()) {
          if (i >= size) {
            // Cannot take positions `1` through `2` in `3`.
            return Errors.printMessage(S.Part, "take", F.list(F.ZZ(start), F.ZZ(last), arg1),
                engine);
          }

          if (arg1.isAssociation()) {
            IAST rule = (IAST) arg1.getRule(i);
            IAST argAST = (IAST) rule.second();

            IExpr temp = part(argAST, ast, p1, engine);
            if (temp.isPresent()) {
              result.appendRule(rule.setAtCopy(2, temp));
              continue;
            }
          } else {
            IAST argAST = (IAST) arg1.get(i);
            IExpr temp = part(argAST, ast, p1, engine);
            if (temp.isPresent()) {
              result.append(temp);
              continue;
            }
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(S.Part, "partd", F.list(arg1.get(i)), engine);
      }
      return result;
    }
    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(arg2), engine);
  }

  public static IExpr sparsePart(final ISparseArray arg1, final IAST ast, int pos,
      EvalEngine engine) {
    if (ast.forAll(x -> (x.isInteger() && x.isPositive()) || x.equals(S.All), 2)) {
      return arg1.getPart(ast, pos);
    }
    // TODO implement more combinations for SparseArray

    IExpr temp = arg1.normal(false);
    if (temp.isList()) {
      IExpr res = part((IAST) temp, ast, pos, engine);
      if (res.isList()) {
        ISparseArray sparseArray = SparseArrayExpr.newDenseList((IAST) res, arg1.getDefaultValue());
        if (sparseArray != null) {
          return sparseArray;
        }
      }
      // return temp;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(ast), engine);
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    if (ast.isAST1()) {
      return ast.arg1();
    }
    if (ast.size() < 3) { // || ast.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
      return F.NIL;
    }

    IASTMutable evaledAST = F.NIL;
    IExpr arg1 = engine.evaluateNIL(ast.arg1());
    if (arg1.isPresent()) {
      evaledAST = ast.setAtCopy(1, arg1);
      if (!arg1.isASTOrAssociation()) {
        if (arg1.isSparseArray()) {
          return sparseEvaluate(evaledAST, (ISparseArray) arg1, engine).orElse(evaledAST);
        }
        if (ast.size() == 3 && ast.arg2().isZero()) {
          return arg1.head();
        }
        // Part specification `1` is longer than depth of object.
        Errors.printMessage(S.Part, "partd", F.list(evaledAST), engine);
        // return the evaluated result:
        return evaledAST;
      }
    } else {
      arg1 = ast.arg1();
      if (!arg1.isASTOrAssociation()) {
        if (arg1.isSparseArray()) {
          return sparseEvaluate(ast, (ISparseArray) arg1, engine);
        }
        if (ast.isAST2()) {
          if (arg1.size() > 0) {
            int n = ast.arg2().toIntDefault();
            if (n == 0) {
              return arg1.head();
            }
            try {
              return arg1.get(n);
            } catch (IndexOutOfBoundsException ioobe) {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(ast.arg2(), arg1), engine);
            }
          }
          if (ast.arg2().isZero()) {
            return arg1.head();
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(S.Part, "partd", F.list(ast), engine);
      }
    }

    IAST arg1AST = (IAST) arg1;
    final int astSize = ast.size();
    for (int i = 2; i < astSize; i++) {
      IExpr temp = engine.evaluateNIL(ast.get(i));
      if (temp.isPresent()) {
        if (evaledAST.isPresent()) {
          evaledAST.set(i, temp);
        } else {
          evaledAST = ast.setAtCopy(i, temp);
          evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
        }
      }
    }
    return part(arg1AST, evaledAST.orElse(ast), 2, engine);
  }

  @Override
  public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
      IBuiltInSymbol builtinSymbol, EvalEngine engine) {
    if (leftHandSide.size() > 1) {
      IAST part = (IAST) leftHandSide;
      if (part.arg1().isSymbol()) {
        ISymbol symbol = (ISymbol) part.arg1();
        IExpr temp = symbol.assignedValue();
        // RulesData rd = symbol.getRulesData();
        if (temp == null) {
          // `1` is not a variable with a value, so its value cannot be changed.
          return Errors.printMessage(builtinSymbol, "rvalue", F.list(symbol), engine);
        } else {
          if (symbol.hasProtectedAttribute()) {
            return Errors.printMessage(builtinSymbol, "write", F.list(symbol), EvalEngine.get());
          }
          try {
            IExpr result;
            if (rightHandSide.isList()) {
              result = assignPart(temp, part, 2, (IAST) rightHandSide, 1, engine);
            } else {
              result = assignPart(temp, part, 2, rightHandSide, engine);
            }
            if (result.isPresent()) {
              symbol.assignValue(result, false);
            }
            return rightHandSide;
          } catch (SymjaMathException sme) {
            return Errors.printMessage(S.Off, sme, engine);
          }
        }
      } else {
        Errors.printMessage(builtinSymbol, "setps", F.list(part), engine);
        return rightHandSide;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDREST);
  }


  public IExpr sparseEvaluate(final IAST ast, ISparseArray arg1, EvalEngine engine) {
    // ast.builtinEvaled();
    if (ast.size() >= 3) {
      IASTMutable evaledAST = F.NIL;
      int astSize = ast.size();
      for (int i = 2; i < astSize; i++) {
        IExpr temp = engine.evaluateNIL(ast.get(i));
        if (temp.isPresent()) {
          if (evaledAST.isPresent()) {
            evaledAST.set(i, temp);
          } else {
            evaledAST = ast.setAtCopy(i, temp);
            evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
          }
        }
      }
      return sparsePart(arg1, evaledAST.orElse(ast), 2, engine);
    }
    return F.NIL;
  }

}
