package org.matheclipse.core.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.operator.Precedence;

/**
 * The row the display wrappers <code>Infix</code>, <code>Prefix</code> and <code>Postfix</code> are
 * shown as: the operands of the wrapped expression with operators between, in front of or after
 * them. The wrappers don't evaluate; each output form ({@code OutputFormFactory},
 * {@code TeXFormFactory}, the boxes of {@code MakeBoxes}) writes the same row in its own notation.
 *
 * <ul>
 * <li><code>Infix(f(a, b, c))</code> is <code>a ~f~ b ~f~ c</code>,
 * <code>Infix(f(a, b, c), h)</code> is <code>a h b h c</code>; a list of operators is written as
 * the list it is.
 * <li><code>Prefix(f(x))</code> is <code>f @ x</code> and <code>Prefix(f(x), h)</code> is
 * <code>h x</code>.
 * <li><code>Postfix(f(x))</code> is <code>x // f</code> and <code>Postfix(f(x), h)</code> is
 * <code>x h</code>.
 * </ul>
 * A string operator is written as its characters, any other operator as the expression it is.
 * Without a precedence an operand which is a sum or binds still less tightly is put in parentheses,
 * except in front of the loosely binding <code>//</code>. With a precedence as third argument an
 * operand which binds less tightly, or as tightly, is put in parentheses, and so is the whole row
 * inside an operator which binds more tightly. A wrapper with a fourth argument, the grouping, is
 * written as the function it is.
 */
public final class OperatorFormLayout {

  /** The kinds of pieces a row is made of. */
  public enum Kind {
    /** An operand of the wrapped expression. */
    OPERAND,
    /** An operator: a string written as its characters or an expression. */
    OPERATOR,
    /**
     * The sign of the default operator next to the head: <code>~</code>, <code>@</code> or
     * <code>//</code>, see {@link OperatorFormLayout#defaultSign}.
     */
    SIGN
  }

  /** One element of the row. */
  public static final class Piece {
    /** The operand or the operator; <code>null</code> for a {@link Kind#SIGN}. */
    public final IExpr expr;

    public final Kind kind;

    /**
     * The precedence an operand is written in; {@link Integer#MIN_VALUE} if it never takes
     * parentheses.
     */
    public final int precedence;

    Piece(IExpr expr, Kind kind, int precedence) {
      this.expr = expr;
      this.kind = kind;
      this.precedence = precedence;
    }

    /** Whether the piece is an operator given as a string, which is written as its characters. */
    public boolean isText() {
      return kind == Kind.OPERATOR && expr.isString();
    }
  }

  /** The pieces of the row, from left to right. */
  public final List<Piece> pieces;

  /**
   * The precedence of the row as a whole: the row is put in parentheses where an operator which
   * binds more tightly surrounds it. {@link Integer#MIN_VALUE} if it never takes parentheses.
   */
  public final int precedence;

  /** The sign of the default operator: <code>~</code>, <code>@</code> or <code>//</code>. */
  public final String defaultSign;

  /**
   * The precedence an operand is written in if the wrapper gives none: a product, and any operator
   * which binds less tightly, is put in parentheses. A product written as a quotient or with a
   * leading minus sign binds as tightly as that sign, see {@link #operandPrecedence(IExpr, int)}.
   */
  public static final int DEFAULT_OPERAND_PRECEDENCE = Precedence.TIMES + 1;

  private OperatorFormLayout(List<Piece> pieces, int precedence, String defaultSign) {
    this.pieces = Collections.unmodifiableList(pieces);
    this.precedence = precedence;
    this.defaultSign = defaultSign;
  }

  /**
   * The precedence an operand is written in: a product written as <code>-x</code> or
   * <code>a/b</code> binds as tightly as the sign in front or the quotient and takes no parentheses
   * where a product does.
   */
  private static int operandPrecedence(IExpr operand, int precedence) {
    if (precedence != Integer.MIN_VALUE && precedence > Precedence.TIMES
        && precedence <= Precedence.DIVIDE && operand.isTimes()) {
      final IAST product = (IAST) operand;
      if (product.arg1().isNegative()
          || product.exists(x -> x.isFraction() || (x.isPower() && x.exponent().isNegative()))) {
        return Precedence.TIMES;
      }
    }
    return precedence;
  }

  /** Whether the row needs parentheses inside an operator of the precedence <code>outer</code>. */
  public boolean isParenthesized(int outer) {
    return precedence != Integer.MIN_VALUE && precedence < outer;
  }

  /**
   * The row of an <code>Infix</code>, <code>Prefix</code> or <code>Postfix</code> wrapper.
   *
   * @return <code>null</code> if the expression is no such wrapper or has arguments it can't be
   *         shown with; it is then written as the function it is
   */
  public static OperatorFormLayout of(IExpr expression) {
    if (!(expression.isAST(S.Infix) || expression.isAST(S.Prefix)
        || expression.isAST(S.Postfix))) {
      return null;
    }
    final IAST wrapper = (IAST) expression;
    if (wrapper.argSize() < 1 || wrapper.argSize() > 3 || !wrapper.arg1().isAST()) {
      return null;
    }
    final IAST expr = (IAST) wrapper.arg1();
    int precedence = Integer.MIN_VALUE;
    // the precedence the operands are written in
    int operandPrecedence = DEFAULT_OPERAND_PRECEDENCE;
    if (wrapper.argSize() == 3) {
      precedence = wrapper.arg3().toIntDefault();
      if (precedence == Integer.MIN_VALUE) {
        return null;
      }
      operandPrecedence = precedence;
    }
    final List<Piece> pieces = new ArrayList<Piece>(2 * expr.size() + 2);
    if (wrapper.isAST(S.Infix)) {
      if (expr.argSize() < 1) {
        return null;
      }
      final int last = expr.argSize();
      if (precedence != Integer.MIN_VALUE && last > 1) {
        // an operand of the same precedence takes parentheses
        operandPrecedence = precedence + 1;
      }
      for (int i = 1; i <= last; i++) {
        if (i > 1) {
          if (wrapper.argSize() == 1) {
            // the head is the operator: a ~f~ b
            pieces.add(new Piece(null, Kind.SIGN, Integer.MIN_VALUE));
            pieces.add(new Piece(expr.head(), Kind.OPERATOR, Integer.MIN_VALUE));
            pieces.add(new Piece(null, Kind.SIGN, Integer.MIN_VALUE));
          } else {
            pieces.add(new Piece(wrapper.arg2(), Kind.OPERATOR, Integer.MIN_VALUE));
          }
        }
        pieces.add(new Piece(expr.get(i), Kind.OPERAND,
            operandPrecedence(expr.get(i), last > 1 ? operandPrecedence : precedence)));
      }
      return new OperatorFormLayout(pieces, precedence, "~");
    }
    if (expr.argSize() != 1) {
      return null;
    }
    final boolean prefix = wrapper.isAST(S.Prefix);
    if (!prefix && wrapper.argSize() == 1) {
      // x // f: the operator binds less tightly than any operand
      operandPrecedence = Integer.MIN_VALUE;
    }
    final Piece operand =
        new Piece(expr.arg1(), Kind.OPERAND, operandPrecedence(expr.arg1(), operandPrecedence));
    if (!prefix) {
      pieces.add(operand);
    }
    if (wrapper.argSize() >= 2) {
      pieces.add(new Piece(wrapper.arg2(), Kind.OPERATOR, Integer.MIN_VALUE));
    } else if (prefix) {
      // f @ x
      pieces.add(new Piece(expr.head(), Kind.OPERATOR, Integer.MIN_VALUE));
      pieces.add(new Piece(null, Kind.SIGN, Integer.MIN_VALUE));
    } else {
      // x // f
      pieces.add(new Piece(null, Kind.SIGN, Integer.MIN_VALUE));
      pieces.add(new Piece(expr.head(), Kind.OPERATOR, Integer.MIN_VALUE));
    }
    if (prefix) {
      pieces.add(operand);
    }
    return new OperatorFormLayout(pieces, precedence, prefix ? "@" : "//");
  }
}
