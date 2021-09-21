package org.matheclipse.core.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.VisitorCollectionBoolean;

/**
 * Determine the variable symbols from a Symja expression and store them internally in a <code>
 * java.util.Set</code>.
 */
public class VariablesSet {
  /**
   * Collect the variables with the <code>IExpr#isVariable()</code> method.
   *
   * @see IExpr#isVariable()
   */
  static class AlgebraVariablesVisitor extends VisitorCollectionBoolean<IExpr> {
    public AlgebraVariablesVisitor(Collection<IExpr> collection) {
      super(collection);
    }

    @Override
    public boolean visit(IAST list) {
      if (list.isList() || list.isPlus() || list.isTimes()) {
        list.forEach(x -> x.accept(this));
        return false;
      } else if (list.isPower()) {
        IExpr base = list.base();
        IExpr exponent = list.exponent();

        if (exponent.isRational()) {
          list.forEach(x -> x.accept(this));
          return false;
        } else if (exponent.isNumber()) {
          fCollection.add(list);
        } else if (!base.isNumericFunction(true)) {
          fCollection.add(list);
        }
      } else {
        if (!list.head().isBuiltInSymbol()
            || ((ISymbol) list.head()).isNumericFunctionAttribute()) {
          if (!list.isNumericFunction(true)) {
            fCollection.add(list);
          }
        }
      }
      return true;
    }

    @Override
    public boolean visit(ISymbol symbol) {
      if (symbol.isVariable()) {
        fCollection.add(symbol);
        return true;
      }
      return false;
    }
  }

  /**
   * Collect the variables which satisfy the <code>IExpr#isVariable()</code> predicate and which are
   * used in logical functions like Not, And, OR, Xor,...
   *
   * @see IExpr#isVariable()
   */
  static class BooleanVariablesVisitor extends VisitorCollectionBoolean<IExpr> {

    public BooleanVariablesVisitor(Collection<IExpr> collection) {
      super(collection);
    }

    @Override
    public boolean visit(IAST ast) {
      ISymbol[] logicEquationHeads = {
        S.And, S.Or, S.Not, S.Xor, S.Nand, S.Nor, S.Implies, S.Equivalent, S.Equal, S.Unequal
      };
      for (int i = 0; i < logicEquationHeads.length; i++) {
        if (ast.isAST(logicEquationHeads[i])) {
          ast.forEach(x -> x.accept(this));
          break;
        }
      }

      return false;
    }

    @Override
    public boolean visit(ISymbol symbol) {
      if (symbol.isVariable()) {
        fCollection.add(symbol);
        return true;
      }
      return false;
    }
  }

  /**
   * Return <code>true</code>, if the expression contains one of the variable store in the internal
   * <code>java.util.Set</code>.
   *
   * @see IExpr#isVariable()
   */
  class IsMemberVisitor extends AbstractVisitorBoolean {
    public IsMemberVisitor() {
      super();
    }

    @Override
    public boolean visit(IAST list) {
      return list.exists(x -> x.accept(this));
    }

    @Override
    public boolean visit(ISymbol symbol) {
      if (symbol.isVariable()) {
        return fVariablesSet.contains(symbol);
      }
      return false;
    }
  }

  /**
   * Collect the variables with the <code>IExpr#isVariable()</code> method.
   *
   * @see IExpr#isVariable()
   */
  static class VariablesVisitor extends VisitorCollectionBoolean<IExpr> {
    public VariablesVisitor(Collection<IExpr> collection) {
      super(collection);
    }

    @Override
    public boolean visit(IAST list) {
      if (!list.isPresent()) {
        return false;
      }
      if (list instanceof ASTSeriesData) {
        fCollection.add(((ASTSeriesData) list).getX());
        return true;
      }
      IExpr head = list.head();
      if (head.isVariable() && list.forAll(x -> x.isInteger())) {
        if (!list.isNumericFunction(true)) {
          fCollection.add(list);
          return true;
        }
      }
      return super.visit(list);
    }

    @Override
    public boolean visit(ISymbol symbol) {
      if (symbol.isVariable()) {
        fCollection.add(symbol);
        return true;
      }
      return false;
    }
  }

  /**
   * See the Variables() function in <code>Cos(x) + Sin(x)</code>, <code>Cos(x)</code> and <code>
   * Sin(x)</code> are extracted as variables^.
   *
   * @param fVariablesSet
   * @param expr
   * @return
   */
  public static IAST addAlgebraicVariables(Set<IExpr> fVariablesSet, IExpr expr) {
    expr.accept(new AlgebraVariablesVisitor(fVariablesSet));
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    final IASTAppendable list = F.ListAlloc(fVariablesSet.size());
    while (iter.hasNext()) {
      list.append(iter.next());
    }
    return list;
  }

  /**
   * Get the set of all variables from the <code>expr</code> and return list of ordered variables.
   *
   * @param fVariablesSet
   * @param expr
   * @return
   */
  public static IAST addVariables(Set<IExpr> fVariablesSet, IExpr expr) {
    expr.accept(new VariablesVisitor(fVariablesSet));
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    final IASTAppendable list = F.ListAlloc(fVariablesSet.size());
    while (iter.hasNext()) {
      list.append(iter.next());
    }
    return list;
  }

  /**
   * Transform the set of variables into an <code>IAST</code> list of ordered variables. Looks only
   * inside sums, products, and rational powers and lists for variables. See the Variables()
   * function in <code>Cos(x) + Sin(x)</code>, <code>Cos(x)</code> and <code>Sin(x)</code> are
   * extracted as variables^.
   *
   * @return the ordered list of variables.
   */
  public static IAST getAlgebraicVariables(IExpr expr) {
    Set<IExpr> fVariablesSet = new TreeSet<IExpr>();
    return addAlgebraicVariables(fVariablesSet, expr);
  }

  /**
   * Transform the set of variables into an <code>IAST</code> list of ordered variables.
   *
   * @return the ordered list of variables.
   */
  public static IAST getVariables(IExpr expr) {
    Set<IExpr> fVariablesSet = new TreeSet<IExpr>();
    return addVariables(fVariablesSet, expr);
  }

  /** Determine the variable symbols from a Symja expression. */
  // public VariablesSet(final IExpr expression, final Comparator<IExpr>
  // comparator) {
  // super();
  // fVariablesSet = new TreeSet<ISymbol>();
  // expression.accept(new VariablesVisitor(fVariablesSet));
  // }

  /**
   * Return a <code>Predicate</code> which tests, if the given input is free of the variables set.
   *
   * @param exprVar
   * @return
   */
  public static Predicate<IExpr> isFree(final VariablesSet exprVar) {
    return new Predicate<IExpr>() {
      final IsMemberVisitor visitor = exprVar.new IsMemberVisitor();

      @Override
      public boolean test(IExpr input) {
        return !input.accept(visitor);
      }
    };
  }

  /** The set of all collected variables. */
  private final Set<IExpr> fVariablesSet;

  /** Constructor for an empty instance. */
  public VariablesSet() {
    super();
    fVariablesSet = new TreeSet<IExpr>();
  }

  /** Determine the variable symbols from a Symja expression. */
  public VariablesSet(final IExpr expression) {
    super();
    fVariablesSet = new TreeSet<IExpr>();
    expression.accept(new VariablesVisitor(fVariablesSet));
  }

  /**
   * Check if the symbol is a valid variable. If yes, add the symbol to the set of variables.
   *
   * @param symbol
   */
  public void add(final IExpr symbol) {
    symbol.accept(new VariablesVisitor(fVariablesSet));
  }

  public boolean addAll(final Set<? extends IExpr> symbols) {
    return fVariablesSet.addAll(symbols);
  }

  /**
   * Add the variables which satisfy the <code>IExpr#isVariable()</code> predicate and which are
   * used in logical functions like Not, And, OR, Xor,...
   *
   * @param expression
   */
  public void addBooleanVarList(final IExpr expression) {
    expression.accept(new BooleanVariablesVisitor(fVariablesSet));
  }

  /**
   * Add the variables of the arguments in <code>ast</code> starting at index <code>fromIndex</code>
   * and stopping at rule arguments by testing {@link IAST#isRuleAST()}.
   *
   * @param ast
   * @param fromIndex
   */
  public void addVarList(final IAST ast, int fromIndex) {
    for (int i = fromIndex; i < ast.size(); i++) {
      IExpr temp = ast.get(i);
      if (temp.isRuleAST()) {
        return;
      }
      temp.accept(new VariablesVisitor(fVariablesSet));
    }
  }

  /**
   * Add the variables of the given expression
   *
   * @param expression
   */
  public void addVarList(final IExpr expression) {
    expression.accept(new VariablesVisitor(fVariablesSet));
  }

  /**
   * Append the set of variables to a <code>List&lt;IExpr&gt;</code> list of variables.
   *
   * @return the list of variables.
   */
  public List<IExpr> appendToList(final IAST ast) {
    List<IExpr> list = new ArrayList<IExpr>();
    for (int i = 1; i < ast.size(); i++) {
      list.add(ast.get(i));
    }
    return list;
  }

  /**
   * Append the set of variables to a <code>List&lt;IExpr&gt;</code> list of variables.
   *
   * @return the list of variables.
   */
  public List<IExpr> appendToList(final List<IExpr> list) {
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    return list;
  }

  public void clear() {
    fVariablesSet.clear();
  }

  /**
   * Searches the set for the specified object.
   *
   * @param o
   * @return
   * @see java.util.Set#contains(java.lang.Object)
   */
  public boolean contains(IExpr o) {
    return fVariablesSet.contains(o);
  }

  /**
   * Searches this set for all objects in the specified collection.
   *
   * @param c
   * @return
   * @see java.util.Set#containsAll(java.util.Collection)
   */
  public boolean containsAll(Collection<? extends IExpr> c) {
    return fVariablesSet.containsAll(c);
  }

  /**
   * Transform the set of variables into a <code>List&lt;IExpr&gt;</code> list of ordered variables.
   *
   * @return the ordered list of variables.
   */
  public List<IExpr> getArrayList() {
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    final List<IExpr> list = new ArrayList<IExpr>();
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    return list;
  }

  /**
   * Transform the set of variables into an <code>IAST</code> list of ordered variables.
   *
   * @return the ordered list of variables.
   */
  public IASTAppendable getVarList() {
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    final IASTAppendable list = F.ListAlloc(fVariablesSet.size());
    while (iter.hasNext()) {
      list.append(iter.next());
    }
    return list;
  }

  public String[] getVarListAsString() {
    String[] result = new String[fVariablesSet.size()];
    final Iterator<IExpr> iter = fVariablesSet.iterator();
    int i = 0;
    while (iter.hasNext()) {
      result[i++] = iter.next().toString();
    }
    return result;
  }

  /**
   * Returns true if this set of variables has no elements.
   *
   * @return
   * @see java.util.Set#isEmpty()
   */
  public boolean isEmpty() {
    return fVariablesSet.isEmpty();
  }

  /**
   * Check if the variable set equals the given number of variables.
   *
   * @param numberOfVars
   * @return <code>true</code> if the variable set equals the given number of variables.
   */
  public boolean isSize(final int numberOfVars) {
    return fVariablesSet.size() == numberOfVars;
  }

  /**
   * The number of determined variables.
   *
   * @return
   */
  public int size() {
    return fVariablesSet.size();
  }

  /**
   * @param a
   * @return
   * @see java.util.Set#toArray(T[])
   */
  public IExpr[] toArray(IExpr[] a) {
    return fVariablesSet.toArray(a);
  }

  /**
   * Copy the variables which are symbols to the <code>scopedVariablesMap</code>
   *
   * @param scopedVariablesMap
   */
  public void putAllSymbols(Map<ISymbol, IExpr> scopedVariablesMap) {
    for (IExpr expr : fVariablesSet) {
      if (expr.isSymbol()) {
        scopedVariablesMap.put((ISymbol) expr, F.NIL);
      }
    }
  }

  @Override
  public String toString() {
    return "VariablesSet = " + fVariablesSet.toString();
  }
}
