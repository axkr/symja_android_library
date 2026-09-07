package org.matheclipse.core.reduce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A formula of the linear IR: a Boolean combination of {@link Atom}s under explicit quantifiers.
 *
 * <p>
 * Instances are immutable. {@link #normalized()} is an equivalence rewrite: it flattens associative
 * nodes, folds constant atoms, removes duplicates and sorts children, so that two formulas which
 * describe the same set have the same normal form and can be used as memo keys.
 */
public final class Formula implements Comparable<Formula> {

  public enum Kind {
    FALSE, TRUE, ATOM, NOT, AND, OR, EXISTS, FORALL
  }

  public static final Formula TRUE = new Formula(Kind.TRUE, null, null, null);

  public static final Formula FALSE = new Formula(Kind.FALSE, null, null, null);

  private final Kind kind;

  private final Atom atom;

  private final List<Formula> children;

  private final List<Variable> boundVariables;

  private Formula(Kind kind, Atom atom, List<Formula> children, List<Variable> boundVariables) {
    this.kind = kind;
    this.atom = atom;
    this.children = children;
    this.boundVariables = boundVariables;
  }

  public static Formula of(boolean value) {
    return value ? TRUE : FALSE;
  }

  public static Formula atom(Atom atom) {
    if (atom == null) {
      return null;
    }
    Boolean truth = atom.constantTruth();
    if (truth != null) {
      return of(truth.booleanValue());
    }
    return new Formula(Kind.ATOM, atom, null, null);
  }

  public static Formula and(List<Formula> children) {
    return new Formula(Kind.AND, null, new ArrayList<Formula>(children), null);
  }

  public static Formula and(Formula... children) {
    return and(java.util.Arrays.asList(children));
  }

  public static Formula or(List<Formula> children) {
    return new Formula(Kind.OR, null, new ArrayList<Formula>(children), null);
  }

  public static Formula or(Formula... children) {
    return or(java.util.Arrays.asList(children));
  }

  public static Formula not(Formula child) {
    return new Formula(Kind.NOT, null, Collections.singletonList(child), null);
  }

  public static Formula quantified(Kind quantifier, List<Variable> boundVariables, Formula body) {
    return new Formula(quantifier, null, Collections.singletonList(body),
        new ArrayList<Variable>(boundVariables));
  }

  public Kind kind() {
    return kind;
  }

  public Atom atom() {
    return atom;
  }

  public List<Formula> children() {
    return children == null ? Collections.<Formula>emptyList() : children;
  }

  public List<Variable> boundVariables() {
    return boundVariables == null ? Collections.<Variable>emptyList() : boundVariables;
  }

  public Formula body() {
    return children.get(0);
  }

  public boolean isTrue() {
    return kind == Kind.TRUE;
  }

  public boolean isFalse() {
    return kind == Kind.FALSE;
  }

  public boolean isQuantifier() {
    return kind == Kind.EXISTS || kind == Kind.FORALL;
  }

  /** Negation normal form: no {@link Kind#NOT} sits above a compound formula. */
  public Formula nnf() {
    switch (kind) {
      case TRUE:
      case FALSE:
      case ATOM:
        return this;
      case AND:
      case OR:
        return rebuild(mapChildren(false));
      case EXISTS:
      case FORALL:
        return quantified(kind, boundVariables, body().nnf());
      default:
        return negatedNnf(children.get(0));
    }
  }

  private static Formula negatedNnf(Formula inner) {
    switch (inner.kind) {
      case TRUE:
        return FALSE;
      case FALSE:
        return TRUE;
      case ATOM:
        return atom(inner.atom.negate());
      case NOT:
        return inner.children.get(0).nnf();
      case AND: {
        List<Formula> negated = new ArrayList<Formula>(inner.children.size());
        for (Formula child : inner.children) {
          negated.add(negatedNnf(child));
        }
        return or(negated);
      }
      case OR: {
        List<Formula> negated = new ArrayList<Formula>(inner.children.size());
        for (Formula child : inner.children) {
          negated.add(negatedNnf(child));
        }
        return and(negated);
      }
      case EXISTS:
        return quantified(Kind.FORALL, inner.boundVariables, negatedNnf(inner.body()));
      default:
        return quantified(Kind.EXISTS, inner.boundVariables, negatedNnf(inner.body()));
    }
  }

  private List<Formula> mapChildren(boolean negate) {
    List<Formula> mapped = new ArrayList<Formula>(children.size());
    for (Formula child : children) {
      mapped.add(negate ? negatedNnf(child) : child.nnf());
    }
    return mapped;
  }

  private Formula rebuild(List<Formula> newChildren) {
    return new Formula(kind, null, newChildren, null);
  }

  /**
   * Flatten associative nodes, fold constants, deduplicate and sort children. The result is
   * logically equivalent to the input.
   */
  public Formula normalized() {
    switch (kind) {
      case TRUE:
      case FALSE:
      case ATOM:
        return this;
      case NOT: {
        Formula inner = children.get(0).normalized();
        if (inner.isTrue()) {
          return FALSE;
        }
        if (inner.isFalse()) {
          return TRUE;
        }
        if (inner.kind == Kind.ATOM) {
          return atom(inner.atom.negate());
        }
        if (inner.kind == Kind.NOT) {
          return inner.children.get(0);
        }
        return not(inner);
      }
      case AND:
      case OR: {
        boolean conjunction = kind == Kind.AND;
        TreeSet<Formula> collected = new TreeSet<Formula>();
        List<Formula> pending = new ArrayList<Formula>(children);
        while (!pending.isEmpty()) {
          Formula child = pending.remove(pending.size() - 1).normalized();
          if (child.kind == kind) {
            pending.addAll(child.children);
            continue;
          }
          if (conjunction ? child.isFalse() : child.isTrue()) {
            return conjunction ? FALSE : TRUE;
          }
          if (conjunction ? child.isTrue() : child.isFalse()) {
            continue;
          }
          collected.add(child);
        }
        if (collected.isEmpty()) {
          return conjunction ? TRUE : FALSE;
        }
        if (collected.size() == 1) {
          return collected.first();
        }
        // p && !p is False, p || !p is True
        for (Formula child : collected) {
          if (child.kind == Kind.ATOM && collected.contains(atom(child.atom.negate()))) {
            return conjunction ? FALSE : TRUE;
          }
        }
        return conjunction ? and(new ArrayList<Formula>(collected))
            : or(new ArrayList<Formula>(collected));
      }
      default: {
        Formula normalizedBody = body().normalized();
        List<Variable> remaining = new ArrayList<Variable>();
        for (Variable variable : boundVariables) {
          if (normalizedBody.containsVariable(variable)) {
            remaining.add(variable);
          }
        }
        if (remaining.isEmpty()) {
          return normalizedBody;
        }
        return quantified(kind, remaining, normalizedBody);
      }
    }
  }

  public Formula substitute(Variable variable, AffineTerm replacement) {
    switch (kind) {
      case TRUE:
      case FALSE:
        return this;
      case ATOM: {
        Atom substituted = atom.substitute(variable, replacement);
        return substituted == atom ? this : atom(substituted);
      }
      case EXISTS:
      case FORALL:
        return quantified(kind, boundVariables, body().substitute(variable, replacement));
      default: {
        List<Formula> substituted = new ArrayList<Formula>(children.size());
        boolean changed = false;
        for (Formula child : children) {
          Formula result = child.substitute(variable, replacement);
          changed |= result != child;
          substituted.add(result);
        }
        return changed ? rebuild(substituted) : this;
      }
    }
  }

  public boolean containsVariable(Variable variable) {
    switch (kind) {
      case TRUE:
      case FALSE:
        return false;
      case ATOM:
        return atom.containsVariable(variable);
      default:
        for (Formula child : children) {
          if (child.containsVariable(variable)) {
            return true;
          }
        }
        return false;
    }
  }

  public boolean containsQuantifier() {
    if (isQuantifier()) {
      return true;
    }
    for (Formula child : children()) {
      if (child.containsQuantifier()) {
        return true;
      }
    }
    return false;
  }

  public boolean containsDivisibility() {
    if (kind == Kind.ATOM) {
      return atom.isDivides();
    }
    for (Formula child : children()) {
      if (child.containsDivisibility()) {
        return true;
      }
    }
    return false;
  }

  /** Every variable which occurs, bound or free. */
  public Set<Variable> allVariables() {
    TreeSet<Variable> variables = new TreeSet<Variable>();
    collectVariables(variables);
    return variables;
  }

  private void collectVariables(Set<Variable> output) {
    if (kind == Kind.ATOM) {
      output.addAll(atom.variables());
      return;
    }
    for (Formula child : children()) {
      child.collectVariables(output);
    }
  }

  /** Every variable which occurs outside the scope of a quantifier binding it. */
  public Set<Variable> freeVariables() {
    TreeSet<Variable> variables = new TreeSet<Variable>();
    collectFreeVariables(variables);
    return variables;
  }

  private void collectFreeVariables(Set<Variable> output) {
    if (kind == Kind.ATOM) {
      output.addAll(atom.variables());
      return;
    }
    if (isQuantifier()) {
      TreeSet<Variable> inner = new TreeSet<Variable>();
      body().collectFreeVariables(inner);
      inner.removeAll(boundVariables);
      output.addAll(inner);
      return;
    }
    for (Formula child : children()) {
      child.collectFreeVariables(output);
    }
  }

  /** The number of nodes, used by the resource budgets of the decision procedures. */
  public int size() {
    if (kind == Kind.ATOM || kind == Kind.TRUE || kind == Kind.FALSE) {
      return 1;
    }
    int total = 1;
    for (Formula child : children()) {
      total += child.size();
    }
    return total;
  }

  @Override
  public int compareTo(Formula that) {
    int byKind = this.kind.compareTo(that.kind);
    if (byKind != 0) {
      return byKind;
    }
    if (kind == Kind.ATOM) {
      return this.atom.compareTo(that.atom);
    }
    if (isQuantifier()) {
      int bySize = Integer.compare(this.boundVariables.size(), that.boundVariables.size());
      if (bySize != 0) {
        return bySize;
      }
      for (int i = 0; i < boundVariables.size(); i++) {
        int byVariable = this.boundVariables.get(i).compareTo(that.boundVariables.get(i));
        if (byVariable != 0) {
          return byVariable;
        }
      }
    }
    List<Formula> left = this.children();
    List<Formula> right = that.children();
    int byCount = Integer.compare(left.size(), right.size());
    if (byCount != 0) {
      return byCount;
    }
    for (int i = 0; i < left.size(); i++) {
      int byChild = left.get(i).compareTo(right.get(i));
      if (byChild != 0) {
        return byChild;
      }
    }
    return 0;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Formula && compareTo((Formula) object) == 0;
  }

  @Override
  public int hashCode() {
    int hash = kind.hashCode();
    if (kind == Kind.ATOM) {
      return 31 * hash + atom.hashCode();
    }
    for (Variable variable : boundVariables()) {
      hash = 31 * hash + variable.hashCode();
    }
    for (Formula child : children()) {
      hash = 31 * hash + child.hashCode();
    }
    return hash;
  }

  @Override
  public String toString() {
    switch (kind) {
      case TRUE:
        return "True";
      case FALSE:
        return "False";
      case ATOM:
        return atom.toString();
      case NOT:
        return "!(" + children.get(0) + ")";
      case EXISTS:
        return "Exists" + boundVariables + "(" + body() + ")";
      case FORALL:
        return "ForAll" + boundVariables + "(" + body() + ")";
      default: {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < children.size(); i++) {
          if (i > 0) {
            builder.append(kind == Kind.AND ? " && " : " || ");
          }
          builder.append(children.get(i));
        }
        return builder.append(")").toString();
      }
    }
  }
}
