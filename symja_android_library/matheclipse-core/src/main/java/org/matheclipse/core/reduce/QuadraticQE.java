package org.matheclipse.core.reduce;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Quantifier elimination over the reals by virtual substitution, for statements whose atoms are
 * polynomial comparisons of degree at most <code>2</code> in the variable which is eliminated
 * (Loos and Weispfenning). The coefficients are polynomials in the other variables with rational
 * numbers, so the product <code>m*x &lt; 1</code> of an outer and an inner variable is in the
 * grammar, where a linear method has to give up.
 * <p>
 * <code>Exists(x, phi)</code> is the disjunction of <code>phi</code> at <code>-Infinity</code> and
 * at the test points made of the roots <code>e</code> of the atoms: <code>e</code> itself for
 * the atoms <code>&lt;=</code> and <code>==</code>, a point just right of it,
 * <code>e+epsilon</code>, for <code>&lt;</code> and <code>!=</code>. A root
 * <code>(alpha+beta*Sqrt(delta))/gamma</code> is never written down: the sign of a polynomial at
 * it is a statement about polynomials in the coefficients again.
 * <p>
 * A statement without free variables comes out as <code>True</code> or <code>False</code>
 * ({@link #decide(IExpr, EvalEngine)}), one with free variables as a condition on them
 * ({@link #eliminate(IExpr, EvalEngine)}). An atom of a higher degree or with something else than
 * a polynomial in it, and a formula which grows beyond the budget are declined.
 */
public final class QuadraticQE {

  private static final int LT = 0;
  private static final int LE = 1;
  private static final int EQ = 2;
  private static final int NE = 3;

  /** The number of atoms one statement may produce. */
  private static final int MAX_ATOMS = 8000;

  /** The time one statement may take, so that a statement which is declined is declined soon. */
  private static final long MAX_MILLIS = 1500L;

  /** The statement is not in the grammar, or too large. */
  private static final class Declined extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Declined() {
      super(null, null, false, false);
    }
  }

  private abstract static class Node {
  }

  private static final class Const extends Node {
    final boolean value;

    Const(boolean value) {
      this.value = value;
    }
  }

  private static final Const TRUE = new Const(true);
  private static final Const FALSE = new Const(false);

  /** <code>p op 0</code> for an expanded polynomial <code>p</code>. */
  private static final class Atom extends Node {
    final IExpr p;
    final int op;

    Atom(IExpr p, int op) {
      this.p = p;
      this.op = op;
    }
  }

  private static final class Junction extends Node {
    final boolean and;
    final List<Node> args;

    Junction(boolean and, List<Node> args) {
      this.and = and;
      this.args = args;
    }
  }

  /** A test point <code>(alpha+beta*Sqrt(delta))/gamma</code>, under its guard. */
  private static final class Point {
    final IExpr alpha;
    final int beta;
    final IExpr delta;
    final IExpr gamma;
    final Node guard;
    final boolean epsilon;

    Point(IExpr alpha, int beta, IExpr delta, IExpr gamma, Node guard, boolean epsilon) {
      this.alpha = alpha;
      this.beta = beta;
      this.delta = delta;
      this.gamma = gamma;
      this.guard = guard;
      this.epsilon = epsilon;
    }

    boolean sameAs(Point other) {
      return beta == other.beta && epsilon == other.epsilon && alpha.equals(other.alpha)
          && delta.equals(other.delta) && gamma.equals(other.gamma);
    }
  }

  private final EvalEngine engine;
  private final long deadline;
  private int atoms = 0;

  private QuadraticQE(EvalEngine engine, long deadline) {
    this.engine = engine;
    this.deadline = deadline;
  }

  /**
   * Decide a statement with <code>ForAll</code> and <code>Exists</code> over the reals.
   *
   * @return <code>True</code>, <code>False</code>, or {@link F#NIL} if the statement is not
   *         decided
   */
  public static IExpr decide(IExpr statement, EvalEngine engine) {
    if (statement.isFree(t -> t.isAST(S.ForAll) || t.isAST(S.Exists), true)
        || hasFreeVariable(statement)) {
      // with a free variable the answer is a condition: see eliminate()
      return F.NIL;
    }
    return decide(statement, System.currentTimeMillis() + MAX_MILLIS, engine);
  }

  private static IExpr decide(IExpr statement, long deadline, EvalEngine engine) {
    try {
      Node node = new QuadraticQE(engine, deadline).node(statement);
      if (node instanceof Const) {
        return ((Const) node).value ? S.True : S.False;
      }
    } catch (Declined declined) {
      // not in the grammar
    }
    return F.NIL;
  }

  /**
   * Eliminate the quantifiers of a statement with free variables: the result is a condition on
   * the free variables, without redundant parts as far as the method itself finds them.
   *
   * @return {@link F#NIL} if the statement has no free variable or is not in the grammar
   */
  public static IExpr eliminate(IExpr statement, EvalEngine engine) {
    if (statement.isFree(t -> t.isAST(S.ForAll) || t.isAST(S.Exists), true)) {
      return F.NIL;
    }
    List<IExpr> free = freeVariables(statement);
    if (free.isEmpty() || free.size() > MAX_FREE_VARIABLES) {
      return F.NIL;
    }
    try {
      QuadraticQE qe = new QuadraticQE(engine, System.currentTimeMillis() + MAX_MILLIS);
      Node node = qe.node(statement);
      final long deadline = System.currentTimeMillis() + MAX_MILLIS;
      IASTAppendable variables = F.ListAlloc(free.size());
      variables.appendAll(free);
      node = qe.closed(qe.prune(qe.normalize(node), null, variables, deadline));
      IExpr condition = qe.expression(node);
      if (free.size() == 1 && !(node instanceof Const)) {
        // one variable: as intervals
        IExpr reduced = engine.evalQuiet(F.Reduce(condition, free.get(0), S.Reals));
        if (reduced.isFree(S.Reduce) && reduced.isFree(S.ConditionalExpression)) {
          return reduced;
        }
      }
      return condition;
    } catch (Declined declined) {
      // not in the grammar
    }
    return F.NIL;
  }

  /**
   * A formula without quantifiers with polynomials as its atoms: the denominators are cleared and
   * <code>Abs</code> is split by the sign of its argument.
   *
   * @return {@link F#NIL} if an atom is no relation of rational functions
   */
  static IExpr polynomialForm(IExpr formula, EvalEngine engine) {
    if (!formula.isFree(t -> t.isAST(S.ForAll) || t.isAST(S.Exists), true)) {
      return F.NIL;
    }
    try {
      QuadraticQE qe = new QuadraticQE(engine, System.currentTimeMillis() + MAX_MILLIS);
      return qe.expression(qe.normalize(qe.node(formula)));
    } catch (Declined declined) {
      return F.NIL;
    }
  }

  /** The number of free variables of a statement whose quantifiers are eliminated. */
  private static final int MAX_FREE_VARIABLES = 4;

  /** The time of one test for a redundant part of a condition. */
  private static final long PRUNE_MILLIS = 250L;

  /** The atoms with a primitive polynomial, the junctions without a part twice. */
  private Node normalize(Node node) {
    if (node instanceof Atom) {
      Atom atom = (Atom) node;
      IExpr list = engine.evaluate(F.unaryAST1(S.FactorTermsList, atom.p));
      if (list.isList2() && list.first().isRational() && !list.first().isZero()) {
        IExpr primitive = expand(list.second());
        boolean negative = list.first().isNegative();
        IExpr first = primitive.isPlus() ? primitive.first() : primitive;
        if ((atom.op == EQ || atom.op == NE)
            && (first.isNegative() || (first.isTimes() && first.first().isNegative()))) {
          // one of p == 0 and -p == 0
          primitive = expand(F.Negate(primitive));
          negative = false;
        }
        if (negative) {
          primitive = expand(F.Negate(primitive));
        }
        return new Atom(primitive, atom.op);
      }
      return node;
    }
    if (node instanceof Junction) {
      Junction junction = (Junction) node;
      List<Node> args = new ArrayList<Node>(junction.args.size());
      List<IExpr> seen = new ArrayList<IExpr>(junction.args.size());
      for (Node arg : junction.args) {
        Node normal = normalize(arg);
        IExpr expr = expression(normal);
        if (!seen.contains(expr)) {
          seen.add(expr);
          args.add(normal);
        }
      }
      return junction(junction.and, args);
    }
    return node;
  }

  /**
   * The formula without the parts which the others imply: a part of a disjunction which implies
   * the rest of it, a part of a conjunction which follows from the rest. A part of a conjunction
   * is simplified under the other parts of it. The implications are statements without free
   * variables, which the method decides itself.
   *
   * @param context what holds where the formula is used, or <code>null</code>
   */
  private Node prune(Node node, IExpr context, IAST variables, long deadline) {
    if (!(node instanceof Junction)) {
      return node;
    }
    Junction junction = (Junction) node;
    List<Node> args = new ArrayList<Node>(junction.args);
    for (int i = 0; i < args.size(); i++) {
      IExpr inner = context;
      if (junction.and) {
        List<Node> rest = new ArrayList<Node>(args);
        rest.remove(i);
        inner = and(context, expression(junction(true, rest)));
      }
      args.set(i, prune(args.get(i), inner, variables, deadline));
    }
    Node flat = junction(junction.and, args);
    if (!(flat instanceof Junction)) {
      return flat;
    }
    junction = (Junction) flat;
    args = new ArrayList<Node>(junction.args);
    for (int i = args.size() - 1; i >= 0 && args.size() > 1; i--) {
      List<Node> rest = new ArrayList<Node>(args);
      Node part = rest.remove(i);
      IExpr others = expression(junction(junction.and, rest));
      boolean redundant = junction.and //
          ? implies(and(context, others), expression(part), variables, deadline) //
          : implies(and(context, expression(part)), others, variables, deadline);
      if (redundant) {
        args.remove(i);
      }
    }
    Node pruned = junction(junction.and, args);
    if (pruned instanceof Junction && !((Junction) pruned).and) {
      // (a && c) || (b && c) where c alone is enough
      List<Node> common = null;
      for (Node arg : ((Junction) pruned).args) {
        List<Node> parts =
            arg instanceof Junction ? ((Junction) arg).args : java.util.Collections.singletonList(arg);
        if (common == null) {
          common = new ArrayList<Node>(parts);
        } else {
          List<IExpr> expressions = new ArrayList<IExpr>(parts.size());
          for (Node part : parts) {
            expressions.add(expression(part));
          }
          common.removeIf(c -> !expressions.contains(expression(c)));
        }
      }
      if (common != null && !common.isEmpty()) {
        Node candidate = junction(true, common);
        if (implies(and(context, expression(candidate)), expression(pruned), variables,
            deadline)) {
          return candidate;
        }
      }
    }
    return pruned;
  }

  /** <code>p &lt; 0 || p == 0</code> as <code>p &lt;= 0</code>, in all disjunctions. */
  private Node closed(Node node) {
    if (!(node instanceof Junction)) {
      return node;
    }
    Junction junction = (Junction) node;
    List<Node> args = new ArrayList<Node>(junction.args.size());
    for (Node arg : junction.args) {
      args.add(closed(arg));
    }
    if (!junction.and) {
      for (int i = 0; i < args.size(); i++) {
        if (!(args.get(i) instanceof Atom) || ((Atom) args.get(i)).op != EQ) {
          continue;
        }
        IExpr p = ((Atom) args.get(i)).p;
        IExpr minus = expand(F.Negate(p));
        for (int j = 0; j < args.size(); j++) {
          if (args.get(j) instanceof Atom && ((Atom) args.get(j)).op == LT
              && (((Atom) args.get(j)).p.equals(p) || ((Atom) args.get(j)).p.equals(minus))) {
            args.set(j, new Atom(((Atom) args.get(j)).p, LE));
            args.remove(i--);
            break;
          }
        }
      }
    }
    return junction(junction.and, args);
  }

  private static IExpr and(IExpr context, IExpr expr) {
    return context == null ? expr : F.And(context, expr);
  }

  /** Whether <code>premise</code> implies <code>conclusion</code> for all values. */
  private boolean implies(IExpr premise, IExpr conclusion, IAST variables, long deadline) {
    final long now = System.currentTimeMillis();
    if (now > deadline) {
      return false;
    }
    return decide(F.ForAll(variables, F.Implies(premise, conclusion)),
        Math.min(deadline, now + PRUNE_MILLIS), engine).isTrue();
  }

  /** The formula as an expression, an atom <code>p op 0</code> with its negative terms right. */
  private IExpr expression(Node node) {
    if (node instanceof Const) {
      return ((Const) node).value ? S.True : S.False;
    }
    if (node instanceof Atom) {
      Atom atom = (Atom) node;
      IAST terms = atom.p.isPlus() ? (IAST) atom.p : F.Plus(atom.p);
      IASTAppendable lhs = F.PlusAlloc(terms.argSize());
      IASTAppendable rhs = F.PlusAlloc(terms.argSize());
      for (int i = 1; i <= terms.argSize(); i++) {
        IExpr term = terms.get(i);
        if (term.isNegative() || (term.isTimes() && term.first().isNegative())) {
          rhs.append(engine.evaluate(F.Negate(term)));
        } else {
          lhs.append(term);
        }
      }
      IExpr left = engine.evaluate(lhs);
      IExpr right = engine.evaluate(rhs);
      // a number is written on the right side
      final boolean swap = left.isNumber() && !right.isNumber();
      switch (atom.op) {
        case LT:
          return swap ? F.Greater(right, left) : F.Less(left, right);
        case LE:
          return swap ? F.GreaterEqual(right, left) : F.LessEqual(left, right);
        case EQ:
          return swap ? F.Equal(right, left) : F.Equal(left, right);
        default:
          return swap ? F.Unequal(right, left) : F.Unequal(left, right);
      }
    }
    Junction junction = (Junction) node;
    IASTAppendable result = F.ast(junction.and ? S.And : S.Or, junction.args.size());
    for (Node arg : junction.args) {
      result.append(expression(arg));
    }
    return result;
  }

  /** Whether the statement has a symbol which no quantifier of it binds. */
  private static boolean hasFreeVariable(IExpr statement) {
    return !freeVariables(statement).isEmpty();
  }

  /** The symbols of the statement which no quantifier of it binds. */
  private static List<IExpr> freeVariables(IExpr statement) {
    final List<IExpr> bound = new ArrayList<IExpr>();
    statement.isFree(t -> {
      if ((t.isAST(S.ForAll) || t.isAST(S.Exists)) && t.argSize() >= 2) {
        IExpr variables = t.first();
        if (variables.isList()) {
          bound.addAll(((IAST) variables).copyTo(new ArrayList<IExpr>()));
        } else {
          bound.add(variables);
        }
      }
      return false;
    }, true);
    final List<IExpr> free = new ArrayList<IExpr>();
    statement.isFree(t -> {
      if (t.isSymbol() && !t.isBuiltInSymbol() && !bound.contains(t) && !free.contains(t)) {
        free.add(t);
      }
      return false;
    }, false);
    return free;
  }

  // --------------------------------------------------------------------------------------------
  // formulas
  // --------------------------------------------------------------------------------------------

  private Node node(IExpr expr) {
    if (expr.isTrue()) {
      return TRUE;
    }
    if (expr.isFalse()) {
      return FALSE;
    }
    if (!expr.isAST()) {
      throw new Declined();
    }
    IAST ast = (IAST) expr;
    if (ast.isAnd() || ast.isOr()) {
      List<Node> args = new ArrayList<Node>(ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        args.add(node(ast.get(i)));
      }
      return junction(ast.isAnd(), args);
    }
    if (ast.isNot()) {
      return not(node(ast.arg1()));
    }
    if (ast.isAST(S.Implies, 3)) {
      return or(not(node(ast.arg1())), node(ast.arg2()));
    }
    if ((ast.isAST(S.ForAll) || ast.isAST(S.Exists)) && (ast.argSize() == 2 || ast.argSize() == 3)) {
      final boolean forAll = ast.isAST(S.ForAll);
      IAST variables = ast.arg1().isList() ? (IAST) ast.arg1() : F.List(ast.arg1());
      Node condition = ast.argSize() == 3 ? node(ast.arg2()) : TRUE;
      Node body = node(ast.last());
      // ForAll(x, c, b) is Not(Exists(x, c && !b))
      Node matrix = and(condition, forAll ? not(body) : body);
      for (int i = variables.argSize(); i >= 1; i--) {
        if (!variables.get(i).isSymbol()) {
          throw new Declined();
        }
        matrix = eliminate(matrix, variables.get(i));
      }
      return forAll ? not(matrix) : matrix;
    }
    if (ast.argSize() >= 2 && (ast.isAST(S.Less) || ast.isAST(S.LessEqual) || ast.isAST(S.Greater)
        || ast.isAST(S.GreaterEqual) || ast.isAST(S.Equal))) {
      List<Node> args = new ArrayList<Node>(ast.argSize() - 1);
      for (int i = 1; i < ast.argSize(); i++) {
        IExpr lhs = ast.get(i);
        IExpr rhs = ast.get(i + 1);
        if (ast.isAST(S.Less)) {
          args.add(relation(lhs, rhs, LT));
        } else if (ast.isAST(S.LessEqual)) {
          args.add(relation(lhs, rhs, LE));
        } else if (ast.isAST(S.Greater)) {
          args.add(relation(rhs, lhs, LT));
        } else if (ast.isAST(S.GreaterEqual)) {
          args.add(relation(rhs, lhs, LE));
        } else {
          args.add(relation(lhs, rhs, EQ));
        }
      }
      return junction(true, args);
    }
    if (ast.isAST(S.Unequal, 3)) {
      return relation(ast.arg1(), ast.arg2(), NE);
    }
    throw new Declined();
  }

  /** <code>lhs op rhs</code>, with <code>Abs</code> on one side and with denominators. */
  private Node relation(IExpr lhs, IExpr rhs, int op) {
    if (lhs.isAbs() && rhs.isFree(S.Abs)) {
      IExpr u = lhs.first();
      IExpr minus = F.Negate(u);
      switch (op) {
        case LT:
        case LE:
          // Abs(u) < c: u < c && -u < c
          return and(relation(u, rhs, op), relation(minus, rhs, op));
        case EQ:
          return and(or(relation(u, rhs, EQ), relation(minus, rhs, EQ)), relation(F.C0, rhs, LE));
        default:
          return not(relation(lhs, rhs, EQ));
      }
    }
    if (rhs.isAbs() && lhs.isFree(S.Abs)) {
      IExpr u = rhs.first();
      IExpr minus = F.Negate(u);
      switch (op) {
        case LT:
        case LE:
          // c < Abs(u): c < u || c < -u
          return or(relation(lhs, u, op), relation(lhs, minus, op));
        case EQ:
          return relation(rhs, lhs, EQ);
        default:
          return not(relation(rhs, lhs, EQ));
      }
    }
    if (!lhs.isFree(S.Abs) || !rhs.isFree(S.Abs)) {
      // Abs(u) somewhere inside, as in 1/Abs(x) < c: by the sign of u
      IExpr abs = innermostAbs(lhs);
      if (abs.isNIL()) {
        abs = innermostAbs(rhs);
      }
      if (abs.isNIL()) {
        throw new Declined();
      }
      IExpr u = abs.first();
      IExpr minus = F.Negate(u);
      return or(
          and(relation(F.C0, u, LE), relation(F.subst(lhs, abs, u), F.subst(rhs, abs, u), op)),
          and(relation(u, F.C0, LT),
              relation(F.subst(lhs, abs, minus), F.subst(rhs, abs, minus), op)));
    }
    IExpr difference = engine.evaluate(F.Together(F.Subtract(lhs, rhs)));
    IExpr numerator = polynomial(engine.evaluate(F.Numerator(difference)));
    IExpr denominator = polynomial(engine.evaluate(F.Denominator(difference)));
    if (denominator.isRational()) {
      if (denominator.isZero()) {
        throw new Declined();
      }
      return atom(denominator.isNegative() ? expand(F.Negate(numerator)) : numerator, op);
    }
    // n/d op 0, where it is defined: the sign of n*d
    IExpr product = expand(F.Times(numerator, denominator));
    Node defined = atom(denominator, NE);
    switch (op) {
      case LT:
        return atom(product, LT);
      case LE:
        return and(atom(product, LE), defined);
      case EQ:
        return and(atom(numerator, EQ), defined);
      default:
        return and(atom(numerator, NE), defined);
    }
  }

  /** An <code>Abs(u)</code> with no <code>Abs</code> inside of <code>u</code>, or NIL. */
  private static IExpr innermostAbs(IExpr expr) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i <= ast.argSize(); i++) {
      IExpr abs = innermostAbs(ast.get(i));
      if (abs.isPresent()) {
        return abs;
      }
    }
    return expr.isAbs() ? expr : F.NIL;
  }

  /** The expanded polynomial, with rational numbers as its only constants. */
  private IExpr polynomial(IExpr expr) {
    IExpr expanded = expand(expr);
    if (!isPolynomial(expanded)) {
      throw new Declined();
    }
    return expanded;
  }

  private static boolean isPolynomial(IExpr expr) {
    if (expr.isRational() || expr.isSymbol() && !expr.isBuiltInSymbol()) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      return ((IAST) expr).forAll(QuadraticQE::isPolynomial);
    }
    if (expr.isPower()) {
      return expr.exponent().isInteger() && expr.exponent().isPositive()
          && isPolynomial(expr.base());
    }
    return false;
  }

  private IExpr expand(IExpr expr) {
    checkTime();
    return engine.evaluate(F.Expand(expr));
  }

  private void checkTime() {
    if (System.currentTimeMillis() > deadline) {
      throw new Declined();
    }
  }

  private Node atom(IExpr p, int op) {
    if (p.isRational()) {
      int sign = p.isZero() ? 0 : (p.isNegative() ? -1 : 1);
      switch (op) {
        case LT:
          return sign < 0 ? TRUE : FALSE;
        case LE:
          return sign <= 0 ? TRUE : FALSE;
        case EQ:
          return sign == 0 ? TRUE : FALSE;
        default:
          return sign != 0 ? TRUE : FALSE;
      }
    }
    if (++atoms > MAX_ATOMS || ((atoms & 63) == 0 && System.currentTimeMillis() > deadline)) {
      throw new Declined();
    }
    return new Atom(p, op);
  }

  private Node not(Node node) {
    if (node instanceof Const) {
      return ((Const) node).value ? FALSE : TRUE;
    }
    if (node instanceof Atom) {
      Atom atom = (Atom) node;
      switch (atom.op) {
        case LT:
          // !(p < 0): -p <= 0
          return atom(expand(F.Negate(atom.p)), LE);
        case LE:
          return atom(expand(F.Negate(atom.p)), LT);
        case EQ:
          return atom(atom.p, NE);
        default:
          return atom(atom.p, EQ);
      }
    }
    Junction junction = (Junction) node;
    List<Node> args = new ArrayList<Node>(junction.args.size());
    for (Node arg : junction.args) {
      args.add(not(arg));
    }
    return junction(!junction.and, args);
  }

  private Node and(Node a, Node b) {
    List<Node> args = new ArrayList<Node>(2);
    args.add(a);
    args.add(b);
    return junction(true, args);
  }

  private Node or(Node a, Node b) {
    List<Node> args = new ArrayList<Node>(2);
    args.add(a);
    args.add(b);
    return junction(false, args);
  }

  /** The conjunction or disjunction, flat and without constants. */
  private Node junction(boolean and, List<Node> args) {
    List<Node> flat = new ArrayList<Node>(args.size());
    for (Node arg : args) {
      if (arg instanceof Const) {
        if (((Const) arg).value != and) {
          // False in a conjunction, True in a disjunction
          return and ? FALSE : TRUE;
        }
      } else if (arg instanceof Junction && ((Junction) arg).and == and) {
        flat.addAll(((Junction) arg).args);
      } else {
        flat.add(arg);
      }
    }
    if (flat.isEmpty()) {
      return and ? TRUE : FALSE;
    }
    return flat.size() == 1 ? flat.get(0) : new Junction(and, flat);
  }

  // --------------------------------------------------------------------------------------------
  // elimination
  // --------------------------------------------------------------------------------------------

  /** <code>{c, b, a}</code> of <code>a*x^2+b*x+c</code>. */
  private IExpr[] coefficients(IExpr p, IExpr x) {
    if (p.isFree(x)) {
      return new IExpr[] {p, F.C0, F.C0};
    }
    int degree = engine.evaluate(F.Exponent(p, x)).toIntDefault();
    if (degree < 0 || degree > 2) {
      throw new Declined();
    }
    return new IExpr[] {engine.evaluate(F.Coefficient(p, x, F.C0)),
        engine.evaluate(F.Coefficient(p, x, F.C1)), engine.evaluate(F.Coefficient(p, x, F.C2))};
  }

  /** <code>Exists(x, phi)</code> without the quantifier. */
  private Node eliminate(Node phi, IExpr x) {
    phi = lowerDegree(phi, x);
    if (phi instanceof Const) {
      return phi;
    }
    List<Point> points = new ArrayList<Point>();
    collectPoints(phi, x, points);
    List<Node> cases = new ArrayList<Node>(points.size() + 1);
    cases.add(substitute(phi, x, null));
    for (Point point : points) {
      cases.add(and(point.guard, substitute(phi, x, point)));
    }
    return junction(false, cases);
  }

  /**
   * An atom of a degree above <code>2</code> in <code>x</code> by the signs of its factors:
   * <code>m^2*x^3-m*x &lt; 0</code> is a statement about <code>m</code>, <code>x</code> and
   * <code>m*x^2-1</code>.
   */
  private Node lowerDegree(Node phi, IExpr x) {
    if (phi instanceof Junction) {
      Junction junction = (Junction) phi;
      List<Node> args = new ArrayList<Node>(junction.args.size());
      for (Node arg : junction.args) {
        args.add(lowerDegree(arg, x));
      }
      return junction(junction.and, args);
    }
    if (!(phi instanceof Atom) || ((Atom) phi).p.isFree(x)) {
      return phi;
    }
    Atom atom = (Atom) phi;
    int degree = engine.evaluate(F.Exponent(atom.p, x)).toIntDefault();
    if (degree >= 0 && degree <= 2) {
      return phi;
    }
    checkTime();
    IExpr factored = engine.evaluate(F.Factor(atom.p));
    IAST product = factored.isTimes() ? (IAST) factored : F.Times(factored);
    List<IExpr> factors = new ArrayList<IExpr>(product.argSize());
    List<Boolean> even = new ArrayList<Boolean>(product.argSize());
    boolean negative = false;
    boolean lowered = false;
    for (int i = 1; i <= product.argSize(); i++) {
      IExpr factor = product.get(i);
      int exponent = 1;
      if (factor.isPower() && factor.exponent().isInteger() && factor.exponent().isPositive()) {
        exponent = factor.exponent().toIntDefault();
        factor = factor.base();
        lowered = true;
        if (exponent < 1) {
          throw new Declined();
        }
      }
      if (factor.isRational()) {
        if (factor.isZero()) {
          throw new Declined();
        }
        negative ^= factor.isNegative() && (exponent % 2 == 1);
        continue;
      }
      IExpr polynomial = polynomial(factor);
      if (!polynomial.isFree(x)) {
        int d = engine.evaluate(F.Exponent(polynomial, x)).toIntDefault();
        if (d < 0 || d > 2) {
          throw new Declined();
        }
      }
      factors.add(polynomial);
      even.add(exponent % 2 == 0);
    }
    if (factors.size() < 2 && !lowered) {
      // one factor to the first power: nothing was gained
      throw new Declined();
    }
    return productSign(factors, even, 0, atom.op, negative);
  }

  /** The sign of the product of the factors from the <code>index</code>-th on, negated or not. */
  private Node productSign(List<IExpr> factors, List<Boolean> even, int index, int op,
      boolean negative) {
    if (index == factors.size()) {
      return atom(negative ? F.CN1 : F.C1, op);
    }
    IExpr f = factors.get(index);
    if (op == EQ) {
      return or(atom(f, EQ), productSign(factors, even, index + 1, EQ, negative));
    }
    if (op == NE) {
      return and(atom(f, NE), productSign(factors, even, index + 1, NE, negative));
    }
    if (even.get(index)) {
      // a square: positive, or zero
      return op == LT //
          ? and(atom(f, NE), productSign(factors, even, index + 1, LT, negative))
          : or(atom(f, EQ), productSign(factors, even, index + 1, LE, negative));
    }
    // f < 0 and the rest positive, or f > 0 and the rest negative
    Node strict = or(
        and(atom(f, LT), productSign(factors, even, index + 1, LT, !negative)),
        and(atom(expand(F.Negate(f)), LT), productSign(factors, even, index + 1, LT, negative)));
    if (op == LT) {
      return strict;
    }
    return or(strict, productSign(factors, even, index, EQ, negative));
  }

  private void collectPoints(Node phi, IExpr x, List<Point> points) {
    if (phi instanceof Junction) {
      for (Node arg : ((Junction) phi).args) {
        collectPoints(arg, x, points);
      }
      return;
    }
    if (!(phi instanceof Atom) || ((Atom) phi).p.isFree(x)) {
      return;
    }
    Atom atom = (Atom) phi;
    IExpr[] k = coefficients(atom.p, x);
    final IExpr c = k[0];
    final IExpr b = k[1];
    final IExpr a = k[2];
    final boolean epsilon = atom.op == LT || atom.op == NE;
    if (!a.isZero()) {
      // (-b +- Sqrt(b^2-4*a*c))/(2*a)
      IExpr delta = expand(F.Subtract(F.Sqr(b), F.Times(F.C4, a, c)));
      IExpr minusB = expand(F.Negate(b));
      IExpr twoA = expand(F.Times(F.C2, a));
      Node guard = and(atom(a, NE), atom(expand(F.Negate(delta)), LE));
      add(points, new Point(minusB, 1, delta, twoA, guard, epsilon));
      add(points, new Point(minusB, -1, delta, twoA, guard, epsilon));
    }
    if (!b.isZero() && !a.isRational()) {
      // the linear root -c/b, where a vanishes
      Node guard = and(atom(a, EQ), atom(b, NE));
      add(points, new Point(expand(F.Negate(c)), 0, F.C0, b, guard, epsilon));
    } else if (!b.isZero() && a.isZero()) {
      add(points, new Point(expand(F.Negate(c)), 0, F.C0, b, atom(b, NE), epsilon));
    }
  }

  private static void add(List<Point> points, Point point) {
    if (point.guard == FALSE) {
      return;
    }
    for (Point other : points) {
      if (other.sameAs(point)) {
        return;
      }
    }
    points.add(point);
  }

  /** <code>phi</code> with the point in the place of <code>x</code>; <code>null</code> is -Infinity. */
  private Node substitute(Node phi, IExpr x, Point point) {
    if (phi instanceof Const) {
      return phi;
    }
    if (phi instanceof Junction) {
      Junction junction = (Junction) phi;
      List<Node> args = new ArrayList<Node>(junction.args.size());
      for (Node arg : junction.args) {
        args.add(substitute(arg, x, point));
      }
      return junction(junction.and, args);
    }
    Atom atom = (Atom) phi;
    if (atom.p.isFree(x)) {
      return atom;
    }
    IExpr[] k = coefficients(atom.p, x);
    if (point == null) {
      return atMinusInfinity(k, atom.op);
    }
    return point.epsilon ? rightOf(k, point, atom.op) : at(k, point, atom.op);
  }

  /** Whether <code>a*x^2+b*x+c</code> is the zero polynomial. */
  private Node isZero(IExpr[] k) {
    return junction(true, list(atom(k[2], EQ), atom(k[1], EQ), atom(k[0], EQ)));
  }

  private static List<Node> list(Node... nodes) {
    List<Node> list = new ArrayList<Node>(nodes.length);
    for (Node node : nodes) {
      list.add(node);
    }
    return list;
  }

  /** The sign of <code>a*x^2+b*x+c</code> for <code>x -> -Infinity</code>. */
  private Node atMinusInfinity(IExpr[] k, int op) {
    final IExpr c = k[0];
    final IExpr b = k[1];
    final IExpr a = k[2];
    switch (op) {
      case LT:
      case LE:
        // a < 0 || (a == 0 && (b > 0 || (b == 0 && c op 0)))
        return or(atom(a, LT), and(atom(a, EQ),
            or(atom(expand(F.Negate(b)), LT), and(atom(b, EQ), atom(c, op)))));
      case EQ:
        return isZero(k);
      default:
        return not(isZero(k));
    }
  }

  /**
   * The sign of the polynomial at the point <code>e == (alpha+beta*Sqrt(delta))/gamma</code>:
   * <code>gamma^2*g(e) == A+B*Sqrt(delta)</code>, and
   *
   * <pre>
   * A+B*Sqrt(delta) == 0  &lt;=&gt;  A*B &lt;= 0 &amp;&amp; A^2-B^2*delta == 0
   * A+B*Sqrt(delta) &lt;= 0  &lt;=&gt;  (A &lt;= 0 &amp;&amp; A^2-B^2*delta &gt;= 0) || (B &lt;= 0 &amp;&amp; A^2-B^2*delta &lt;= 0)
   * A+B*Sqrt(delta) &lt;  0  &lt;=&gt;  (A &lt; 0 &amp;&amp; A^2-B^2*delta &gt; 0) || (B &lt;= 0 &amp;&amp; (A &lt; 0 || A^2-B^2*delta &lt; 0))
   * </pre>
   */
  private Node at(IExpr[] k, Point e, int op) {
    final IExpr c = k[0];
    final IExpr b = k[1];
    final IExpr a = k[2];
    // A == a*(alpha^2+beta^2*delta)+b*alpha*gamma+c*gamma^2
    IExpr bigA = expand(F.Plus(
        F.Times(a, F.Plus(F.Sqr(e.alpha), F.Times(F.ZZ(e.beta * e.beta), e.delta))),
        F.Times(b, e.alpha, e.gamma), F.Times(c, F.Sqr(e.gamma))));
    if (e.beta == 0) {
      return atom(bigA, op);
    }
    // B == beta*(2*a*alpha+b*gamma)
    IExpr bigB = expand(
        F.Times(F.ZZ(e.beta), F.Plus(F.Times(F.C2, a, e.alpha), F.Times(b, e.gamma))));
    if (bigB.isZero()) {
      return atom(bigA, op);
    }
    IExpr d = expand(F.Subtract(F.Sqr(bigA), F.Times(F.Sqr(bigB), e.delta)));
    IExpr minusD = expand(F.Negate(d));
    switch (op) {
      case EQ:
        return and(atom(expand(F.Times(bigA, bigB)), LE), atom(d, EQ));
      case NE:
        return or(atom(expand(F.Negate(F.Times(bigA, bigB))), LT), atom(d, NE));
      case LE:
        return or(and(atom(bigA, LE), atom(minusD, LE)), and(atom(bigB, LE), atom(d, LE)));
      default:
        return or(and(atom(bigA, LT), atom(minusD, LT)),
            and(atom(bigB, LE), or(atom(bigA, LT), atom(d, LT))));
    }
  }

  /** The sign of the polynomial just right of the point. */
  private Node rightOf(IExpr[] k, Point e, int op) {
    switch (op) {
      case EQ:
        return isZero(k);
      case NE:
        return not(isZero(k));
      case LE:
        return or(rightOf(k, e, LT), isZero(k));
      default:
        break;
    }
    // g < 0 right of e: g(e) < 0, or g(e) == 0 and the derivative is negative right of e
    if (k[1].isZero() && k[2].isZero()) {
      return atom(k[0], LT);
    }
    IExpr[] derivative = {k[1], expand(F.Times(F.C2, k[2])), F.C0};
    return or(at(k, e, LT), and(at(k, e, EQ), rightOf(derivative, e, LT)));
  }
}
