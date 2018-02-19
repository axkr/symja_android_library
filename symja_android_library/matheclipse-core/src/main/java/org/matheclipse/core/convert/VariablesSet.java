package org.matheclipse.core.convert;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.VisitorCollectionBoolean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.duy.lambda.Consumer;
import com.duy.lambda.Predicate;

/**
 * Determine the variable symbols from a Symja expression and store them internally in a <code>java.util.Set</code>.
 */
public class VariablesSet {
    /**
     * The set of all collected variables.
     */
    private final Set<IExpr> fVariablesSet;

    /**
     * Constructor for an empty instance.
     */
    public VariablesSet() {
        super();
        fVariablesSet = new TreeSet<IExpr>();
    }

    /**
     * Determine the variable symbols from a Symja expression.
     */
    public VariablesSet(final IExpr expression) {
        super();
        fVariablesSet = new TreeSet<IExpr>();
        expression.accept(new VariablesVisitor(fVariablesSet));
    }

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

    /**
     * Transform the set of variables into an <code>IAST</code> list of ordered variables. Looks only inside sums,
     * products, and rational powers and lists for variables.
     *
     * @return the ordered list of variables.
     */
    public static IAST getVariables(IExpr expr) {
        Set<IExpr> fVariablesSet = new TreeSet<IExpr>();
        return addVariables(fVariablesSet, expr);
    }

    public static IAST addVariables(Set<IExpr> fVariablesSet, IExpr expr) {
        expr.accept(new AlgebraVariablesVisitor(fVariablesSet));
        final Iterator<IExpr> iter = fVariablesSet.iterator();
        final IASTAppendable list = F.ListAlloc(fVariablesSet.size());
        while (iter.hasNext()) {
            list.append(iter.next());
        }
        return list;
    }

    /**
     * Add the symbol to the set of variables.
     *
     * @param symbol
     * @return <tt>true</tt> if the underlying set did not already contain the symbol
     */
    public boolean add(final IExpr symbol) {
        return fVariablesSet.add(symbol);
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
     * Determine the variable symbols from a Symja expression.
     */
    // public VariablesSet(final IExpr expression, final Comparator<IExpr>
    // comparator) {
    // super();
    // fVariablesSet = new TreeSet<ISymbol>();
    // expression.accept(new VariablesVisitor(fVariablesSet));
    // }

    /**
     * Add the variables which satisfy the <code>IExpr#isVariable()</code> predicate and which are used in logical
     * functions like Not, And, OR, Xor,...
     *
     * @param expression
     */
    public void addBooleanVarList(final IExpr expression) {
        expression.accept(new BooleanVariablesVisitor(fVariablesSet));
    }

    /**
     * Add the variables of the given expression
     *
     * @param expression
     */
    public void addVarList(final IAST rest, int fromIndex) {
        for (int i = 2; i < rest.size(); i++) {
            IExpr temp = rest.get(i);
            if (temp.isRuleAST()) {
                return;
            }
            temp.accept(new VariablesVisitor(fVariablesSet));
        }
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
     * Searches the set for the specified object.
     *
     * @param o
     * @return
     * @see java.util.Set#contains(java.lang.Object)
     */
    public boolean contains(ISymbol o) {
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
     * Check if the expression contains the given number of variables.
     *
     * @param expr
     * @return <code>true</code> if the expr contains the given number of variables.
     */
    public boolean isSize(int size) {
        return fVariablesSet.size() == size;
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
     * Collect the variables which satisfy the <code>IExpr#isVariable()</code> predicate and which are used in logical
     * functions like Not, And, OR, Xor,...
     *
     * @see IExpr#isVariable()
     */
    public static class BooleanVariablesVisitor extends VisitorCollectionBoolean<IExpr> {

        public BooleanVariablesVisitor(Collection<IExpr> collection) {
            super(collection);
        }

        @Override
        public boolean visit(IAST ast) {
            ISymbol[] logicEquationHeads = {F.And, F.Or, F.Not, F.Xor, F.Nand, F.Nor, F.Implies, F.Equivalent, F.Equal,
                    F.Unequal};
            for (int i = 0; i < logicEquationHeads.length; i++) {
                if (ast.isAST(logicEquationHeads[i])) {
                    ast.forEach(new Consumer<IExpr>() {
                        @Override
                        public void accept(IExpr x) {
                            x.accept(BooleanVariablesVisitor.this);
                        }
                    });
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
     * Collect the variables with the <code>IExpr#isVariable()</code> method.
     *
     * @see IExpr#isVariable()
     */
    public static class AlgebraVariablesVisitor extends VisitorCollectionBoolean<IExpr> {
        public AlgebraVariablesVisitor(Collection<IExpr> collection) {
            super(collection);
        }

        @Override
        public boolean visit(ISymbol symbol) {
            if (symbol.isVariable()) {
                fCollection.add(symbol);
                return true;
            }
            return false;
        }

        @Override
        public boolean visit(IAST list) {
            if (list.isList() || list.isPlus() || list.isTimes()) {
                list.forEach(new Consumer<IExpr>() {
                    @Override
                    public void accept(IExpr x) {
                        x.accept(AlgebraVariablesVisitor.this);
                    }
                });
                return false;
            } else if (list.isPower()) {
                IExpr base = list.arg1();
                IExpr exponent = list.arg2();

                if (exponent.isRational()) {
                    list.forEach(new Consumer<IExpr>() {
                        @Override
                        public void accept(IExpr x) {
                            x.accept(AlgebraVariablesVisitor.this);
                        }
                    });
                    return false;
                } else if (exponent.isNumber()) {
                    fCollection.add(list);
                } else if (!base.isNumericFunction()) {
                    fCollection.add(list);
                }
            } else {
                fCollection.add(list);
            }
            return true;
        }
    }

    /**
     * Collect the variables with the <code>IExpr#isVariable()</code> method.
     *
     * @see IExpr#isVariable()
     */
    public static class VariablesVisitor extends VisitorCollectionBoolean<IExpr> {
        public VariablesVisitor(Collection<IExpr> collection) {
            super(collection);
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
    public class IsMemberVisitor extends AbstractVisitorBoolean {
        public IsMemberVisitor() {
            super();
        }

        @Override
        public boolean visit(IAST list) {
            return list.exists(new Predicate<IExpr>() {
                @Override
                public boolean test(IExpr x) {
                    return x.accept(IsMemberVisitor.this);
                }
            }, 1);
        }

        @Override
        public boolean visit(ISymbol symbol) {
            if (symbol.isVariable()) {
                return fVariablesSet.contains(symbol);
            }
            return false;
        }
    }
}
