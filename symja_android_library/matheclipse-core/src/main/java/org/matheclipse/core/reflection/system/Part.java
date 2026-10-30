package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.EvalFlags.Group;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.fastutil.ints.IntArrayList;

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

  /**
   * <code>expr[[spec, ...]] = rhs</code> from the specification at <code>partPosition</code> on.
   *
   * <p>
   * The specification of one level is a position, <code>All</code>, a <code>Span</code> or a list
   * of positions. When it selects several positions and <code>rhs</code> is a list of the same
   * length, the elements of <code>rhs</code> go to the positions one by one and the deeper levels
   * work on them; any other <code>rhs</code> is assigned as a whole at every position. A position
   * which is selected twice receives the later value. The compound assignments like
   * <code>AddTo</code> or <code>AppendTo</code> of a part read the part and come back here
   * through <code>Set</code>.
   *
   * @param expr the expression to assign into
   * @param part the <code>Part(...)</code> expression
   * @param partPosition the index of the next part specification
   * @param rhs the value which should be assigned
   * @param engine the evaluation engine
   * @return the changed copy of <code>expr</code> or {@link F#NIL} if a message was printed
   */
  private static IExpr assignPart(final IExpr expr, final IAST part, int partPosition,
      IExpr rhs, EvalEngine engine) {
    if (partPosition >= part.size()) {
      // stop recursion
      return rhs;
    }
    if (!expr.isASTOrAssociation()) {
      // Part specification `1` is longer than depth of object.
      return Errors.printMessage(S.Part, "partd", F.list(part), engine);
    }
    final IAST ast = (IAST) expr;
    final IExpr spec = integralPositions(engine.evaluate(part.get(partPosition)));
    if (ast.isAssociation() && (spec.isKey() || spec.isString())) {
      return assignAssociationKey((IAssociation) ast, spec, part, partPosition + 1, rhs, engine);
    }
    final int[] positions = partPositions(ast, spec, engine);
    if (positions == null) {
      return F.NIL;
    }
    // only a list is distributed; an association is assigned as a whole like any other value
    final boolean distribute = !spec.isInteger() && rhs.isList()
        && rhs.argSize() == positions.length;
    final IASTAppendable result = ast.copyAppendable();
    for (int i = 0; i < positions.length; i++) {
      final int position = positions[i];
      final IExpr value = distribute ? rhs.getAt(i + 1) : rhs;
      if (result.isAssociation() && position == 0) {
        // an association keeps its head, its values are the parts which can be assigned
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(F.C0, ast), engine);
      }
      final IExpr element = result.isAssociation() ? ((IAssociation) result).getValue(position)
          : result.get(position);
      final IExpr temp = assignPart(element, part, partPosition + 1, value, engine);
      if (temp.isNIL()) {
        return F.NIL;
      }
      setElement(result, position, temp);
    }
    return result;
  }

  /**
   * A machine real with an integral value stands for that position in an assignment:
   * <code>v[[2.]] = 5</code> assigns the second element, <code>v[[2. ;; 3.]] = 0</code> the second
   * and the third.
   */
  private static IExpr integralPositions(IExpr spec) {
    if (spec instanceof INum) {
      double value = ((INum) spec).doubleValue();
      if (value == Math.rint(value) && Math.abs(value) < Integer.MAX_VALUE) {
        return F.ZZ((int) value);
      }
      return spec;
    }
    if ((spec.isList() || spec.isAST(S.Span)) && ((IAST) spec).exists(x -> x instanceof INum)) {
      // a list of positions, or the bounds and the step of a Span
      return ((IAST) spec).map(Part::integralPositions);
    }
    return spec;
  }

  /**
   * The positions of <code>ast</code> which the part specification <code>spec</code> of one level
   * selects: one for an integer, all arguments for <code>All</code>, the ones in a
   * <code>Span</code> or the ones in a list of integers. Negative positions count from the end.
   *
   * @return <code>null</code> if a message was printed
   */
  private static int[] partPositions(IAST ast, IExpr spec, EvalEngine engine) {
    final int size = ast.size();
    if (spec.isInteger()) {
      final int position = partPosition(ast, spec, engine);
      return position < 0 ? null : new int[] {position};
    }
    if (spec == S.All) {
      final int[] positions = new int[size - 1];
      for (int i = 1; i < size; i++) {
        positions[i - 1] = i;
      }
      return positions;
    }
    final int[] span = spec.isSpan(size);
    if (span != null) {
      final int start = span[0];
      final int last = span[1];
      final int step = span[2];
      if (start < 0 || last < 0 || start >= size || last >= size || step == 0
          || step == Config.INVALID_INT) {
        // the positions as they are written, not as they were resolved against the length
        // Cannot take positions `1` through `2` in `3`.
        Errors.printMessage(S.Part, "take", F.list(spec.first(), spec.second(), ast), engine);
        return null;
      }
      final IntArrayList positions = new IntArrayList();
      if (step > 0) {
        for (int i = start; i <= last; i += step) {
          positions.add(i);
        }
      } else {
        for (int i = start; i >= last; i += step) {
          positions.add(i);
        }
      }
      return positions.toIntArray();
    }
    if (spec.isList()) {
      final IAST list = (IAST) spec;
      final int[] positions = new int[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        final IExpr index = list.get(i);
        final int position = index.isInteger() ? partPosition(ast, index, engine) : -1;
        if (position < 0) {
          if (!index.isInteger()) {
            // Part `1` of `2` does not exist.
            Errors.printMessage(S.Part, "partw", F.list(index, ast), engine);
          }
          return null;
        }
        positions[i - 1] = position;
      }
      return positions;
    }
    // Part `1` of `2` does not exist.
    Errors.printMessage(S.Part, "partw", F.list(spec, ast), engine);
    return null;
  }

  /**
   * The position of the integer <code>index</code> in <code>ast</code>, counted from the end if it
   * is negative. <code>0</code> is the head.
   *
   * @return <code>-1</code> if a message was printed
   */
  private static int partPosition(IAST ast, IExpr index, EvalEngine engine) {
    int position = index.toIntDefault();
    if (position != Integer.MIN_VALUE && position < 0) {
      position = ast.size() + position;
    }
    if (position < 0 || position >= ast.size()) {
      // Part `1` of `2` does not exist.
      Errors.printMessage(S.Part, "partw", F.list(index, ast), engine);
      return -1;
    }
    return position;
  }

  /**
   * Assign the <code>value</code> to the rule of the association which is defined by the given
   * <code>keySpecification</code>. <code>assoc[[key]] = value</code>
   *
   * <p>
   * If <code>keySpecification</code> is the last element of the part specification, an existing
   * rule will be updated or a new <code>key->value</code> rule will be appended to the association.
   * This is the same behaviour as for the <code>assoc[key] = value</code> assignment. Otherwise the
   * key must already exist, because an intermediate key can't be created automatically.
   *
   * @param assoc the association in which the value should be assigned
   * @param keySpecification a string or a <code>Key(...)</code> expression
   * @param part the <code>Part(...)</code> expression
   * @param partPosition the index of the next part specification
   * @param value
   * @param engine the evaluation engine
   * @return {@link F#NIL} if an error occurred
   */
  private static IExpr assignAssociationKey(IAssociation assoc, IExpr keySpecification,
      final IAST part, int partPosition, IExpr value, EvalEngine engine) {
    final IExpr key = keySpecification.isKey() ? keySpecification.first() : keySpecification;
    if (partPosition >= part.size()) {
      IAssociation result = assoc.copy();
      result.appendRule(F.Rule(key, value));
      return result;
    }
    final int index = assoc.getRulePosition(key);
    if (index == 0) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(S.Part, "partw", F.list(keySpecification, assoc), engine);
    }
    IExpr temp = assignPart(assoc.getValue(index), part, partPosition, value, engine);
    if (temp.isNIL()) {
      return F.NIL;
    }
    IAssociation result = assoc.copy();
    result.setValue(index, temp);
    return result;
  }

  /**
   * Set the element at the given <code>position</code>. An {@link IAssociation} stores
   * <code>Rule(key, value)</code> expressions internally, therefore only the value of the rule at
   * <code>position</code> gets replaced, so that the association keys are preserved.
   *
   * @param result the expression in which the element should be set
   * @param position
   * @param value
   */
  private static void setElement(IASTAppendable result, int position, IExpr value) {
    if (result.isAssociation()) {
      ((IAssociation) result).setValue(position, value);
    } else {
      result.set(position, value);
    }
  }

  /**
   * Return <code>expr</code> as an {@link IAST} which <code>Part</code> can index into. Pattern
   * objects like <code>x_Symbol</code> are atoms in Symja, so they get converted into an
   * {@link IAST} which mirrors their <code>FullForm</code> structure.
   *
   * @param expr
   * @return {@link F#NIL} if <code>expr</code> can't be indexed
   * @see IPatternObject#toFullFormAST()
   */
  /**
   * <code>ByteArray[…][[spec]]</code>.
   *
   * <p>
   * One position answers the byte as an integer; a span or a list of positions answers another byte
   * array. Reading the head of an HTTP request is written as <code>message[[i ;; i + 3]]</code>, so
   * without this no request can be parsed.
   *
   * @return {@link F#NIL} if the specification is not one this understands
   */
  private static IExpr byteArrayPart(ByteArrayExpr byteArray, IExpr spec, EvalEngine engine) {
    final byte[] bytes = byteArray.toData();
    if (spec.isZero()) {
      return S.ByteArray;
    }
    if (spec == S.All) {
      return byteArray;
    }
    if (spec.isInteger()) {
      int index = spec.toIntDefault();
      int position = index < 0 ? bytes.length + index + 1 : index;
      if (position < 1 || position > bytes.length) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(spec, byteArray), engine);
      }
      return F.ZZ(bytes[position - 1] & 0xFF);
    }
    int[] span = spec.isSpan(bytes.length + 1);
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      if (step == 0 || start < 1 || last > bytes.length) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(spec, byteArray), engine);
      }
      java.io.ByteArrayOutputStream taken = new java.io.ByteArrayOutputStream();
      if (step > 0) {
        for (int i = start; i <= last; i += step) {
          taken.write(bytes[i - 1]);
        }
      } else {
        for (int i = start; i >= last; i += step) {
          taken.write(bytes[i - 1]);
        }
      }
      return ByteArrayExpr.newInstance(taken.toByteArray());
    }
    if (spec.isList()) {
      IAST positions = (IAST) spec;
      java.io.ByteArrayOutputStream taken = new java.io.ByteArrayOutputStream();
      for (int i = 1; i < positions.size(); i++) {
        int index = positions.get(i).toIntDefault();
        int position = index < 0 ? bytes.length + index + 1 : index;
        if (position < 1 || position > bytes.length) {
          // Part `1` of `2` does not exist.
          return Errors.printMessage(S.Part, "partw", F.list(positions.get(i), byteArray), engine);
        }
        taken.write(bytes[position - 1]);
      }
      return ByteArrayExpr.newInstance(taken.toByteArray());
    }
    return F.NIL;
  }

  private static IAST partTarget(IExpr expr) {
    if (expr.isASTOrAssociation()) {
      return (IAST) expr;
    }
    if (expr instanceof IPatternObject) {
      return ((IPatternObject) expr).toFullFormAST();
    }
    return F.NIL;
  }

  /**
   * Get the element stored at the given <code>position</code>.
   *
   * @param ast
   * @param pos
   * @param engine
   * @return
   */
  private static IExpr getIndex(IAST ast, final int pos, ISymbol head, EvalEngine engine) {
    int position = checkedPosition(ast, pos);
    if (position < 0) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(head, "partw", F.list(F.ZZ(pos), ast), engine);
    }
    return ast.get(position);
  }

  /**
   * The index of the part <code>pos</code> of <code>ast</code>; a negative <code>pos</code> counts
   * from the end.
   *
   * @return <code>-1</code> if the part does not exist
   */
  private static int checkedPosition(IAST ast, final int pos) {
    int position = pos;
    if (position < 0) {
      position = ast.size() + position;
    }
    if ((position < 0) || (position >= ast.size())) {
      return -1;
    }
    return position;
  }

  /**
   * Evaluate the part specifications <code>ast.arg2(), ast.arg3(), ...</code>.
   *
   * @param evaledAST a copy of <code>ast</code> with an already evaluated first argument, or
   *        {@link F#NIL}
   * @return <code>ast</code> if neither the first argument nor a part specification changed
   */
  private static IAST evaluateIndices(IAST ast, IASTMutable evaledAST, EvalEngine engine) {
    final int astSize = ast.size();
    for (int i = 2; i < astSize; i++) {
      IExpr temp = engine.evaluateNIL(ast.get(i));
      if (temp.isPresent()) {
        if (evaledAST.isPresent()) {
          evaledAST.set(i, temp);
        } else {
          evaledAST = ast.setAtCopy(i, temp);
          evaledAST.copyFlagsFrom(ast, Group.MATRIX_OR_VECTOR);
        }
      }
    }
    return evaledAST.orElse(ast);
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
  private static IExpr getIndexRule(IAST ast, final int pos, ISymbol head,
      EvalEngine engine) {
    int position = checkedPosition(ast, pos);
    if (position < 0) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(head, "partw", F.list(F.ZZ(pos), ast), engine);
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
  /**
   * The one part an atom has: position <code>0</code>, its head. <code>Alert[[0]]</code> is
   * <code>Symbol</code>, and <code>Hold[Alert][[1, 0]]</code> is <code>Symbol</code> too - which is
   * how a template asks whether the name it collected stands for a symbol or for a call.
   *
   * @return {@link F#NIL} when the specification asks for anything else
   */
  private static IExpr atomPart(IExpr atom, IAST ast, int pos, EvalEngine engine) {
    IExpr result = atom;
    for (int i = pos; i < ast.size(); i++) {
      if (engine.evaluate(ast.get(i)).toIntDefault() != 0) {
        return F.NIL;
      }
      result = result.head();
    }
    return result;
  }

  public static IExpr part(final IAST arg1, final IAST ast, int pos, EvalEngine engine) {
    return part(arg1, ast, pos, S.Part, engine);
  }

  /**
   * Get the <code>Part[...]</code> of an expression, reporting an invalid part under
   * <code>head</code>.
   *
   * @param arg1 the expression from which parts should be extracted
   * @param ast the <code>Part[...]</code> expression
   * @param pos the index position from which the sub-expressions should be extracted
   * @param head the symbol a message is reported for, <code>Part</code> or the caller such as
   *        <code>Extract</code>
   * @param engine the evaluation engine
   * @return {@link F#NIL} if a part doesn't exist
   */
  public static IExpr part(final IAST arg1, final IAST ast, int pos, ISymbol head,
      EvalEngine engine) {
    final IExpr arg2 = engine.evaluate(ast.get(pos));
    int p1 = pos + 1;
    int[] span = arg2.isSpan(arg1.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      return spanPart(ast, pos, arg1, arg2, start, last, step, p1, head, engine);
    } else if (arg2 == S.All) {
      return spanPart(ast, pos, arg1, arg2, 1, arg1.argSize(), 1, p1, head,
          engine);
    } else if (arg2.isReal()) {
      final int indx = ast.get(pos).toIntDefault();
      if (F.isNotPresent(indx)) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(head, "partw", F.list(ast.get(pos), arg1), engine);
      }
      IExpr result = getIndex(arg1, indx, head, engine);
      if (result.isPresent()) {
        if (p1 < ast.size()) {
          IAST target = partTarget(result);
          if (target.isPresent()) {
            return part(target, ast, p1, head, engine);
          } else if (result.isSparseArray()) {
            return sparsePart((ISparseArray) result, ast, p1, head, engine);
          } else {
            IExpr headOfAtom = atomPart(result, ast, p1, engine);
            if (headOfAtom.isPresent()) {
              return headOfAtom;
            }
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(head, "partd", F.list(result), engine);
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
              return Errors.printMessage(head, "partw", F.list(listArg, arg1), engine);
            }
            IExpr ires = getIndexRule(arg1, indx, head, engine);
            if (ires.isPresent()) {
              if (p1 < ast.size()) {
                IAST iresTarget = partTarget(ires);
                if (iresTarget.isPresent()) {

                  temp = part(iresTarget, ast, p1, head, engine);
                  if (temp.isPresent()) {
                    try {
                      result.appendRule(temp);
                    } catch (IndexOutOfBoundsException ioobex) {
                      return Errors.printMessage(head, "pkspec1", F.list(temp), engine);
                    }
                  } else {
                    // an error occurred
                    return F.NIL;
                  }

                } else {
                  // Part specification `1` is longer than depth of object.
                  return Errors.printMessage(head, "partd", F.list(ires), engine);
                }
              } else {
                try {
                  result.appendRule(ires);
                } catch (IndexOutOfBoundsException ioobex) {
                  return Errors.printMessage(head, "pkspec1", F.list(ires), engine);
                }
              }
            } else {
              return F.NIL;
            }
          } else if (listArg.isKey() || listArg.isString()) {
            // an absent key gives key -> Missing("KeyAbsent", key)
            final IExpr key = listArg.isKey() ? listArg.first() : listArg;
            final IAST rule = assoc.getRule(key);
            result.appendRule(rule.isPresent() ? rule : F.Rule(key, F.Missing(S.KeyAbsent, key)));
          } else {
            // a number which is no position, a symbol, a list: nothing of it can be a part
            // The expression `1` cannot be used as a part specification.
            return Errors.printMessage(head, "pkspec1", F.list(list), engine);
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
            return part((IAST) result, ast, p1, head, engine);
          } else {
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(head, "partd", F.list(result), engine);
          }
        }
        return result;
      }
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      if (arg1.isDataset() && p1 >= ast.size()) {
        IExpr dataset = datasetPart(arg1, list, head, engine);
        if (dataset.isPresent()) {
          return dataset;
        }
      }
      final IASTAppendable result = F.ast(arg1.head(), list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isReal()) {
          final int indx = listArg.toIntDefault();
          if (F.isNotPresent(indx)) {
            if (!listArg.isInteger()) {
              // The expression `1` cannot be used as a part specification.
              return Errors.printMessage(head, "pkspec1", F.list(list), engine);
            }
            // Part `1` of `2` does not exist.
            return Errors.printMessage(head, "partw", F.list(listArg, arg1), engine);
          }
          IExpr ires = getIndex(arg1, indx, head, engine);
          if (ires.isPresent()) {
            if (p1 < ast.size()) {
              IAST iresTarget = partTarget(ires);
              if (iresTarget.isPresent()) {
                temp = part(iresTarget, ast, p1, head, engine);
                if (temp.isPresent()) {
                  result.append(temp);
                } else {
                  // an error occurred
                  return F.NIL;
                }
              } else {
                // Part specification `1` is longer than depth of object.
                return Errors.printMessage(head, "partd", F.list(ires), engine);
              }
            } else {
              result.append(ires);
            }
          } else {
            return F.NIL;
          }
        } else {
          // a symbol or a list is no position either: {1, 2, 3}[[{1, x}]] has no part x, and is
          // not the part 1 alone
          // The expression `1` cannot be used as a part specification.
          return Errors.printMessage(head, "pkspec1", F.list(list), engine);
        }
      }
      return result;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(head, "pkspec1", F.list(arg2), engine);
  }

  /**
   * <code>dataset[[{i, j, ...}]]</code>.
   *
   * <p>
   * The generic path below builds <code>head(items...)</code> from the head of the expression being
   * indexed, which is right for <code>f(a, b, c)[[{1, 3}]]</code> and wrong for a
   * <code>Dataset</code>: it produced <code>Dataset(row, row)</code> - the <code>Dataset</code>
   * head applied to two one-row datasets - and, for an empty specification, a
   * <code>Dataset()</code> that then complained about being called with no arguments.
   *
   * <p>
   * A dataset knows how it is indexed, and it is not indexed uniformly: several rows select rows,
   * but a dataset of one row selects columns. So the whole selection is handed to
   * {@link IAST#getItems(int[], int, int)}, which is where that knowledge lives.
   *
   * @return the selection, or {@link F#NIL} when this is not a plain list of positions and the
   *         generic path should have it after all
   */
  private static IExpr datasetPart(final IAST arg1, final IAST list, ISymbol head,
      EvalEngine engine) {
    final int argSize = arg1.argSize();
    int[] positions = new int[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      IExpr listArg = list.get(i);
      if (!listArg.isReal()) {
        return F.NIL;
      }
      int index = listArg.toIntDefault();
      if (F.isNotPresent(index)) {
        return F.NIL;
      }
      if (index < 0) {
        // counted from the end, as everywhere else in Part
        index = argSize + index + 1;
      }
      if (index < 1 || index > argSize) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(head, "partw", F.list(listArg, arg1), engine);
      }
      positions[i - 1] = index;
    }
    return arg1.getItems(positions, positions.length, 0);
  }

  private static IExpr spanPart(final IAST ast, int pos, final IAST arg1, final IExpr arg2,
      int start, int last, int step, int p1, ISymbol head, EvalEngine engine) {

    final int size = arg1.size();
    if ((step > 0 && start > last + 1) || (step < 0 && start < last - 1)) {
      // a span may be empty, as 3;;2, but not run backwards
      // Cannot take positions `1` through `2` in `3`.
      return Errors.printMessage(head, "take", F.list(F.ZZ(start), F.ZZ(last), arg1), engine);
    }
    if ((step > 0 && start <= last && (start < 1 || last >= size))
        || (step < 0 && start >= last && (last < 1 || start >= size))) {
      // a non-empty span which runs past either end
      // Cannot take positions `1` through `2` in `3`.
      return Errors.printMessage(head, "take", F.list(F.ZZ(start), F.ZZ(last), arg1), engine);
    }
    if (step != 0) {
      IASTAppendable result = arg1.copyHead((last - start) / step + 2);
      for (int i = start; step < 0 ? i >= last : i <= last; i += step) {
        if (p1 >= ast.size()) {
          IExpr temp = getIndexRule(arg1, i, head, engine);
          if (temp.isPresent()) {
            result.appendRule(temp);
            continue;
          }
          return F.NIL;
        }
        if (i >= size || i < 0) {
          // tested before the element is read: a span which runs past the end is an error message
          // and not an IndexOutOfBoundsException
          // Cannot take positions `1` through `2` in `3`.
          return Errors.printMessage(head, "take", F.list(F.ZZ(start), F.ZZ(last), arg1),
              engine);
        }
        IAST target = partTarget(arg1.get(i));
        if (target.isPresent()) {
          if (arg1.isAssociation()) {
            IAST rule = (IAST) arg1.getRule(i);
            IAST argAST = partTarget(rule.second());
            if (argAST.isPresent()) {
              IExpr temp = part(argAST, ast, p1, head, engine);
              if (temp.isPresent()) {
                result.appendRule(rule.setAtCopy(2, temp));
                continue;
              }
            }
          } else {
            IExpr temp = part(target, ast, p1, head, engine);
            if (temp.isPresent()) {
              result.append(temp);
              continue;
            }
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(head, "partd", F.list(arg1.get(i)), engine);
      }
      return result;
    }
    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(head, "pkspec1", F.list(arg2), engine);
  }

  public static IExpr sparsePart(final ISparseArray arg1, final IAST ast, int pos,
      EvalEngine engine) {
    return sparsePart(arg1, ast, pos, S.Part, engine);
  }

  private static IExpr sparsePart(final ISparseArray arg1, final IAST ast, int pos, ISymbol head,
      EvalEngine engine) {
    if (ast.forAll(x -> (x.isInteger() && x.isPositive()) || x == S.All, 2)) {
      return arg1.getPart(ast, pos);
    }
    if (arg1 instanceof SparseArrayExpr) {
      // spans and lists of positions are taken from the stored entries
      IExpr sparse = ((SparseArrayExpr) arg1).partOfSpecifications(ast, pos);
      if (sparse.isPresent()) {
        return sparse;
      }
      long totalSize = 1L;
      for (int d : arg1.getDimension()) {
        totalSize *= d;
      }
      if (totalSize > Config.MAX_AST_SIZE) {
        // The expression `1` cannot be used as a part specification.
        return Errors.printMessage(head, "pkspec1", F.list(ast), engine);
      }
    }

    IExpr temp = arg1.normal(false);
    if (temp.isList()) {
      IExpr res = part((IAST) temp, ast, pos, head, engine);
      if (res.isList()) {
        ISparseArray sparseArray = SparseArrayExpr.newDenseList((IAST) res, arg1.getDefaultValue());
        if (sparseArray != null) {
          return sparseArray;
        }
      }
      // return temp;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(head, "pkspec1", F.list(ast), engine);
  }

  /**
   * The parts of a numeric array: a single row is taken directly, every other specification from
   * the nested lists, and a part which is an array keeps the type of the numeric array.
   */
  private static IExpr numericArrayPart(final IAST ast, IExpr numericArray, EvalEngine engine) {
    if (ast.isAST2() && ast.arg2().isInteger()) {
      int n = ast.arg2().toIntDefault();
      if (n == 0) {
        return numericArray.head();
      }
      if (n > 0 && n < numericArray.size()) {
        return numericArray.get(n);
      }
    }
    return NumericArrayExpr.evalNormal(ast, true, engine);
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    if (ast.isAST1()) {
      return ast.arg1();
    }
    if (ast.size() < 3) { // || ast.hasFlag(Flag.BUILT_IN_EVALED)) {
      return F.NIL;
    }

    IASTMutable evaledAST = F.NIL;
    IExpr arg1 = engine.evaluateNIL(ast.arg1());
    // the expression which the part specifications are applied to; for a pattern object this is the
    // AST which mirrors its FullForm structure
    IAST arg1AST = F.NIL;
    if (arg1.isPresent()) {
      if (arg1 instanceof ByteArrayExpr && ast.isAST2()) {
        IExpr bytes = byteArrayPart((ByteArrayExpr) arg1, engine.evaluate(ast.arg2()), engine);
        if (bytes.isPresent()) {
          return bytes;
        }
      }
      evaledAST = ast.setAtCopy(1, arg1);
      arg1AST = partTarget(arg1);
      if (arg1AST.isNIL()) {
        if (arg1.isNumericArray()) {
          return numericArrayPart(evaledAST, arg1, engine).orElse(evaledAST);
        }
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
      if (arg1 instanceof ByteArrayExpr && ast.isAST2()) {
        IExpr bytes = byteArrayPart((ByteArrayExpr) arg1, engine.evaluate(ast.arg2()), engine);
        if (bytes.isPresent()) {
          return bytes;
        }
      }
      arg1AST = partTarget(arg1);
      if (arg1AST.isNIL()) {
        if (arg1.isNumericArray()) {
          return numericArrayPart(ast, arg1, engine);
        }
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

    return part(arg1AST, evaluateIndices(ast, evaledAST, engine), 2, engine);
  }

  /**
   * The indices of an assignment <code>m[[Sequence @@ {i, j}]] = value</code> are the indices
   * <code>i, j</code>: the left-hand side is held, so the evaluator does not splice the sequence.
   * An index which may give one is evaluated here, once - its value replaces it.
   */
  /**
   * <code>m[[i]][[j]] = value</code> with integer indices <code>i</code> is the assignment
   * <code>m[[i, j]] = value</code>.
   */
  private static IAST flattenNestedPart(IAST part, EvalEngine engine) {
    while (part.arg1().isAST(S.Part) && part.arg1().size() >= 3) {
      IAST inner = (IAST) part.arg1();
      IASTAppendable flat = F.ast(S.Part, inner.size() + part.size());
      flat.append(inner.arg1());
      for (int i = 2; i < inner.size(); i++) {
        IExpr index = engine.evaluate(inner.get(i));
        if (!index.isInteger()) {
          return part;
        }
        flat.append(index);
      }
      final IAST outer = part;
      flat.appendArgs(2, outer.size(), outer::get);
      part = flat;
    }
    return part;
  }

  private static IAST spliceSequences(IAST part, EvalEngine engine) {
    if (!part.exists(x -> x.isAST() && !x.isList() && !x.isAST(S.Span) && !x.isKey(), 2)) {
      return part;
    }
    IASTAppendable spliced = F.ast(part.head(), part.size() + 2);
    spliced.append(part.arg1());
    for (int i = 2; i < part.size(); i++) {
      IExpr index = part.get(i);
      if (index.isAST() && !index.isList() && !index.isAST(S.Span) && !index.isKey()) {
        // evaluated as the element of a list, where a sequence is spliced
        IExpr indices = engine.evaluate(F.List(index));
        if (indices.isList()) {
          spliced.appendArgs((IAST) indices);
          continue;
        }
      }
      spliced.append(index);
    }
    return spliced;
  }

  @Override
  public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
      IBuiltInSymbol builtinSymbol, EvalEngine engine) {
    if (leftHandSide.size() > 1) {
      IAST part = spliceSequences((IAST) leftHandSide, engine);
      if (part.size() < 3) {
        return F.NIL;
      }
      part = flattenNestedPart(part, engine);
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
            ISparseArray sparse = null;
            if (temp.isSparseArray()) {
              // the parts of a SparseArray are assigned in its dense form, and the result is sparse
              // again with the same default value
              sparse = (ISparseArray) temp;
              temp = sparse.normal(false);
            }
            IExpr result = assignPart(temp, part, 2, rightHandSide, engine);
            if (result.isPresent() && sparse != null && result.isList()) {
              IExpr sparseResult =
                  SparseArrayExpr.newDenseList((IAST) result, sparse.getDefaultValue());
              if (sparseResult != null) {
                result = sparseResult;
              }
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
    newSymbol.setAttributes(Attribute.NHOLDREST);
  }


  public IExpr sparseEvaluate(final IAST ast, ISparseArray arg1, EvalEngine engine) {
    // ast.builtinEvaled();
    if (ast.size() >= 3) {
      return sparsePart(arg1, evaluateIndices(ast, F.NIL, engine), 2, engine);
    }
    return F.NIL;
  }

}
