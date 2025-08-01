package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class B2 extends AbstractAST implements Externalizable, RandomAccess {
  static final class And extends B2 {
    public And() {
      super();
    }

    And(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new And(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.And;
    }

    @Override
    public final int headID() {
      return ID.And;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }
  }

  static final class B2Set extends B2 {
    public B2Set() {
      super();
    }

    B2Set(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new B2Set(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Set;
    }

    @Override
    public final int headID() {
      return ID.Set;
    }
  }

  static final class Condition extends B2 {
    public Condition() {
      super();
    }

    Condition(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Condition(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      if (engine.isEvalRHSMode()) {
        return B2.conditionEval(arg1, arg2, engine);
      }
      return F.NIL;
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Condition;
    }

    @Override
    public final int headID() {
      return ID.Condition;
    }
  }

  static final class DirectedEdge extends B2 {
    public DirectedEdge() {
      super();
    }

    DirectedEdge(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new DirectedEdge(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.DirectedEdge;
    }

    @Override
    public final int headID() {
      return ID.DirectedEdge;
    }
  }

  public static final class Divide extends B2 {
    public Divide() {
      super();
    }

    public Divide(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Divide(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Divide;
    }

    @Override
    public final int headID() {
      return ID.Divide;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return false;
    }

    @Override
    public boolean isPlus() {
      return false;
    }

    @Override
    public boolean isPlusTimesPower() {
      return false;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return false;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Divide;
    }
  }

  static final class Equal extends B2 {
    public Equal() {
      super();
    }

    Equal(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Equal(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Equal;
    }

    @Override
    public final int headID() {
      return ID.Equal;
    }
  }

  static final class FreeQ extends B2 {
    public FreeQ() {
      super();
    }

    FreeQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new FreeQ(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      return PredicateQ.freeQ(arg1, arg2, engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.FreeQ;
    }

    @Override
    public final int headID() {
      return ID.FreeQ;
    }
  }

  static final class Greater extends B2 {
    public Greater() {
      super();
    }

    Greater(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Greater(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Greater;
    }

    @Override
    public final int headID() {
      return ID.Greater;
    }
  }

  static final class GreaterEqual extends B2 {
    public GreaterEqual() {
      super();
    }

    GreaterEqual(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new GreaterEqual(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.GreaterEqual;
    }

    @Override
    public final int headID() {
      return ID.GreaterEqual;
    }
  }

  static final class If extends B2 {
    public If() {
      super();
    }

    If(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new If(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      return S.If.evaluate(this, engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.If;
    }

    @Override
    public final int headID() {
      return ID.If;
    }
  }

  static final class Integrate extends B2 {
    public Integrate() {
      super();
    }

    Integrate(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Integrate(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Integrate;
    }

    @Override
    public final int headID() {
      return ID.Integrate;
    }
  }

  static final class Less extends B2 {
    public Less() {
      super();
    }

    Less(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Less(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Less;
    }

    @Override
    public final int headID() {
      return ID.Less;
    }
  }

  static final class LessEqual extends B2 {
    public LessEqual() {
      super();
    }

    LessEqual(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new LessEqual(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.LessEqual;
    }

    @Override
    public final int headID() {
      return ID.LessEqual;
    }
  }

  static final class List extends B2 {
    public List() {
      super();
    }

    List(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new List(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.List;
    }

    @Override
    public final int headID() {
      return ID.List;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isList() {
      return true;
    }

    @Override
    public boolean isList2() {
      return true;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.List;
    }

  }

  static final class MemberQ extends B2 {
    public MemberQ() {
      super();
    }

    MemberQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new MemberQ(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.MemberQ;
    }

    @Override
    public final int headID() {
      return ID.MemberQ;
    }
  }

  static final class Or extends B2 {
    public Or() {
      super();
    }

    Or(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Or(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Or;
    }

    @Override
    public final int headID() {
      return ID.Or;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }

  }

  static final class Part extends B2 {
    public Part() {
      super();
    }

    Part(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Part(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Part;
    }

    @Override
    public final int headID() {
      return ID.Part;
    }
  }

  static final class Plus extends B2 {
    public Plus() {
      super();
    }

    Plus(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Plus(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      if (arg1.isNumber() && arg2.isNumber()) {
        return ((INumber) arg1).plusExpr((INumber) arg2);
        // System.out.println("Plus: " + arg1 + "+" + arg2 + "=>" + result);
      }
      return super.evaluate(engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Plus;
    }

    @Override
    public final int headID() {
      return ID.Plus;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }

    @Override
    public boolean isPlus() {
      return true;
    }

    @Override
    public boolean isPlusTimesPower() {
      return true;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return false;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Plus;
    }
  }

  static final class PolynomialQ extends B2 {
    public PolynomialQ() {
      super();
    }

    PolynomialQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new PolynomialQ(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.PolynomialQ;
    }

    @Override
    public final int headID() {
      return ID.PolynomialQ;
    }
  }

  static final class Power extends B2 {
    public Power() {
      super();
    }

    Power(IExpr base, IExpr exponent) {
      super(base, exponent);
    }

    @Override
    public IExpr base() {
      return arg1;
    }

    @Override
    public IASTMutable copy() {
      return new Power(arg1, arg2);
    }

    // @Override
    // public IExpr evaluate(EvalEngine engine) {
    // if (arg1.isNumber() && arg2.isInteger()) {
    // long exp = arg2.toLongDefault();
    // if (F.isPresent(exp)) {
    // // System.out.println("Power: " + arg1 + "^" + exp);
    // if (exp > 0) {
    // if (exp == 1 || arg1.isZero() || arg1.isOne()) {
    // return arg1;
    // }
    // return arg1.power(exp);
    // } else if (exp <= 0 && !arg1.isZero()) {
    // if (exp == 0) {
    // return F.C1;
    // }
    // // if (exp == -1) {
    // // System.out.println("Power: " + arg1 + "^" + exp);
    // // return arg1.inverse();
    // // }
    // // return arg1.power(-exp).inverse();
    // }
    // }
    // }
    // return super.evaluate(engine);
    // }

    @Override
    public IExpr exponent() {
      return arg2;
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Power;
    }

    @Override
    public final int headID() {
      return ID.Power;
    }

    @Override
    public boolean isPlus() {
      return false;
    }

    @Override
    public boolean isPlusTimesPower() {
      return true;
    }

    @Override
    public boolean isPower() {
      return true;
    }

    @Override
    public boolean isTimes() {
      return false;
    }
  }

  static final class Rule extends B2 {
    public Rule() {
      super();
    }

    Rule(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Rule(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Rule;
    }

    @Override
    public final int headID() {
      return ID.Rule;
    }
  }

  static final class RuleDelayed extends B2 {
    public RuleDelayed() {
      super();
    }

    RuleDelayed(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new RuleDelayed(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.RuleDelayed;
    }

    @Override
    public final int headID() {
      return ID.RuleDelayed;
    }
  }

  static final class SameQ extends B2 {
    public SameQ() {
      super();
    }

    SameQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new SameQ(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.SameQ;
    }

    @Override
    public final int headID() {
      return ID.SameQ;
    }
  }

  public static final class Subtract extends B2 {
    public Subtract() {
      super();
    }

    public Subtract(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Subtract(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      if (arg1.isNumber() && arg2.isNumber()) {
        INumber result = ((INumber) arg1).subtract((INumber) arg2);
        // System.out.println("Subtract: " + arg1 + "-" + arg2 + "=>" + result);
        return result;
      }
      return super.evaluate(engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Subtract;
    }

    @Override
    public final int headID() {
      return ID.Subtract;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return false;
    }

    @Override
    public boolean isPlus() {
      return false;
    }

    @Override
    public boolean isPlusTimesPower() {
      return false;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return false;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Subtract;
    }
  }

  static final class Times extends B2 {
    public Times() {
      super();
    }

    Times(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new Times(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      if (arg1.isNumber() && arg2.isNumber()) {
        return ((INumber) arg1).timesExpr((INumber) arg2);
        // System.out.println("Times: " + arg1 + "*" + arg2 + "=>" + result);
      }
      // if (arg1.isNumber() && arg2.isNumber()) {
      // IExpr result = super.evaluate(engine);
      // System.out.println("Times: " + arg1 + "*" + arg2 + "=>" + result);
      // }
      return super.evaluate(engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Times;
    }

    @Override
    public final int headID() {
      return ID.Times;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }

    @Override
    public boolean isPlus() {
      return false;
    }

    @Override
    public boolean isPlusTimesPower() {
      return true;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return true;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Times;
    }
  }

  static final class UndirectedEdge extends B2 {
    public UndirectedEdge() {
      super();
    }

    UndirectedEdge(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new UndirectedEdge(arg1, arg2);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.UndirectedEdge;
    }

    @Override
    public final int headID() {
      return ID.UndirectedEdge;
    }
  }


  static final class With extends B2 {
    public With() {
      super();
    }

    With(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public IASTMutable copy() {
      return new With(arg1, arg2);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      return S.With.evaluate(this, engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.With;
    }

    @Override
    public final int headID() {
      return ID.With;
    }
  }

  private static final int SIZE = 3;

  /**
   * If the second argument is true, evaluate the first argument and return the result. Otherwise
   * throw a condition {@link ConditionException#CONDITION_NIL}.
   * 
   * @param arg1
   * @param arg2
   * @param engine
   * @throws ConditionException
   */
  public static IExpr conditionEval(IExpr arg1, IExpr arg2, EvalEngine engine)
      throws ConditionException {
    if (engine.evalTrue(arg2)) {
      return engine.evaluate(arg1);
    }
    throw ConditionException.CONDITION_NIL;
  }

  /** The second argument of this function. */
  protected IExpr arg1;

  /** The second argument of this function. */
  protected IExpr arg2;

  /** ctor for deserialization */
  public B2() {
    super();
  }

  /**
   * Create a function with two arguments (i.e. <code>head[arg1, arg2]</code> ).
   *
   * @param arg1 the first argument of the function
   * @param arg2 the second argument of the function
   */
  public B2(IExpr arg1, IExpr arg2) {
    this.arg1 = arg1;
    this.arg2 = arg2;
  }

  /**
   * Get the first argument (i.e. the second element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(1) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()
   * </code> returns <code>x</code>.
   *
   * @return the first argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public final IExpr arg1() {
    return arg1;
  }

  /**
   * Get the second argument (i.e. the third element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(2) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>
   * Power(x, y)</code>), <code>arg2()</code> returns <code>y</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public final IExpr arg2() {
    return arg2;
  }

  /**
   * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(3) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()
   * </code> returns <code>c</code>.
   *
   * @return the third argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg3() {
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 3, Size: " + size());
  }

  /**
   * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(4) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>
   * arg4()</code> returns <code>d</code>.
   *
   * @return the fourth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg4() {
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 4, Size: " + size());
  }

  /**
   * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(5) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>
   * arg5()</code> returns <code>e</code> .
   *
   * @return the fifth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg5() {
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 5, Size: " + size());
  }

  /** {@inheritDoc} */
  @Override
  final public int argSize() {
    return SIZE - 1;
  }

  @Override
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
    SortedSet<IExpr> set = new TreeSet<>(comparator);
    set.add(arg1);
    set.add(arg2);
    return set;
  }

  /**
   * Returns a new {@code AST2} with the same elements, the same size and the same capacity as this
   * {@code AST2}.
   *
   * @return a shallow copy of this {@code ArrayList}
   * @see java.lang.Cloneable
   */
  @Override
  public IAST clone() {
    return copy();
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(Object object) {
    return head().equals(object) || arg1.equals(object) || arg2.equals(object);
  }

  /** {@inheritDoc} */
  @Override
  public abstract IASTMutable copy();

  @Override
  public IASTAppendable copyAppendable() {
    return new AST(head(), arg1, arg2);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    IASTAppendable result = F.ast(head(), additionalCapacity + 2);
    result.append(arg1);
    result.append(arg2);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractAST) {
      final IAST list = (IAST) obj;
      final ISymbol head = head();
      if (head != list.head()) {
        // compared with IBuiltInSymbol object identity
        return false;
      }
      return list.size() == SIZE && arg1.equals(list.arg1()) && arg2.equals(list.arg2());
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) || predicate.test(arg1, 1) || predicate.test(arg2, 2);
      case 1:
        return predicate.test(arg1, 1) || predicate.test(arg2, 2);
      case 2:
        return predicate.test(arg2, 2);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) || predicate.test(arg1) || predicate.test(arg2);
      case 1:
        return predicate.test(arg1) || predicate.test(arg2);
      case 2:
        return predicate.test(arg2);
    }
    return false;
  }

  @Override
  public boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate) {
    return stopPredicate.test(arg1, arg2);
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate) {
    if (predicate.test(arg1)) {
      filterAST.append(arg1);
    } else {
      restAST.append(arg1);
    }
    if (predicate.test(arg2)) {
      filterAST.append(arg2);
    } else {
      restAST.append(arg2);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    if (predicate.test(arg1)) {
      filterAST.append(arg1);
    }
    if (predicate.test(arg2)) {
      filterAST.append(arg2);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
      final Function<IExpr, IExpr> function) {
    IExpr expr = function.apply(arg1);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg1);
    }
    expr = function.apply(arg2);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg2);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr findFirst(Function<IExpr, IExpr> function) {
    IExpr temp = function.apply(arg1);
    if (temp.isPresent()) {
      return temp;
    }
    return function.apply(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) && predicate.test(arg1, 1) && predicate.test(arg2, 2);
      case 1:
        return predicate.test(arg1, 1) && predicate.test(arg2, 2);
      case 2:
        return predicate.test(arg2, 2);
      default:
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) && predicate.test(arg1) && predicate.test(arg2);
      case 1:
        return predicate.test(arg1) && predicate.test(arg2);
      case 2:
        return predicate.test(arg2);
      default:
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action) {
    action.accept(arg1);
    action.accept(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    switch (startOffset) {
      case 0:
        action.accept(head());
        action.accept(arg1);
        action.accept(arg2);
        break;
      case 1:
        action.accept(arg1);
        action.accept(arg2);
        break;
      case 2:
        action.accept(arg2);
        break;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(startOffset) + ", Size: 3");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, final int endOffset, final Consumer<? super IExpr> action) {
    if (startOffset < endOffset) {
      switch (startOffset) {
        case 0:
          action.accept(head());
          if (++startOffset < endOffset) {
            action.accept(arg1);
            if (++startOffset < endOffset) {
              action.accept(arg2);
            }
          }
          break;
        case 1:
          action.accept(arg1);
          if (++startOffset < endOffset) {
            action.accept(arg2);
          }
          break;
        case 2:
          action.accept(arg2);
          break;
        default:
          throw new IndexOutOfBoundsException(
              "Index: " + Integer.valueOf(startOffset) + ", Size: 3");
      }
    }
  }

  @Override
  public void forEach(int start, final int end, ObjIntConsumer<? super IExpr> action) {
    if (start < end) {
      switch (start) {
        case 0:
          action.accept(head(), 0);
          if (++start < end) {
            action.accept(arg1, 1);
            if (++start < end) {
              action.accept(arg2, 2);
            }
          }
          break;
        case 1:
          action.accept(arg1, 1);
          if (++start < end) {
            action.accept(arg2, 2);
          }
          break;
        case 2:
          action.accept(arg2, 2);
          break;
        default:
          throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(start) + ", Size: 3");
      }
    }
  }

  @Override
  public final void forEach(ObjIntConsumer<? super IExpr> action) {
    action.accept(arg1, 1);
    action.accept(arg2, 2);
  }

  @Override
  public final void forEach2(ObjIntConsumer<? super IExpr> action) {
    action.accept(arg2, 2);
  }

  @Override
  public IExpr get(int location) {
    switch (location) {
      case 0:
        return head();
      case 1:
        return arg1;
      case 2:
        return arg2;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
    }
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    if (length == 0) {
      return new AST0(head());
    }
    AST result = new AST(length, true);
    result.set(0, head());
    for (int i = 0; i < length; i++) {
      result.set(i + 1, get(items[i] + offset));
    }
    return result;
  }


  @Override
  public int hashCode() {
    if (hashValue == 0 && arg2 != null) {
      hashValue = (0x811c9dc5 * 16777619) ^ (SIZE & 0xff);// decimal 2166136261;
      hashValue = (hashValue * 16777619) ^ (head().hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg1.hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg2.hashCode() & 0xff);
    }
    return hashValue;
  }

  @Override
  public abstract ISymbol head();

  @Override
  public int headID() {
    final IExpr head = head();
    return head instanceof IBuiltInSymbol ? ((IBuiltInSymbol) head).ordinal() : ID.UNKNOWN;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    if (arg1.equals(expr)) {
      return 1;
    }
    if (arg2.equals(expr)) {
      return 2;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    if (fromIndex == 1 && predicate.test(arg1)) {
      return 1;
    }
    if ((fromIndex == 1 || fromIndex == 2) && predicate.test(arg2)) {
      return 2;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST1() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST2() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST3() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isBuiltInFunction() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFlatAST() {
    return false;
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public boolean isList1() {
    return false;
  }

  @Override
  public boolean isList2() {
    return false;
  }

  @Override
  public boolean isList3() {
    return false;
  }

  @Override
  public boolean isList4() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus() {
    return head() == S.Plus;
  }

  @Override
  public boolean isPlusTimesPower() {
    final ISymbol head = head();
    return head == S.Plus || head == S.Times || head == S.Power;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPower() {
    return head() == S.Power;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int length) {
    return head() == head && length == SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    return head() == head && minLength <= SIZE && maxLength >= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return head() == head && length <= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes() {
    return head() == S.Times;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr last() {
    return arg2;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr oneIdentity(IExpr defaultValue) {
    return this;
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();

    // int size;
    // byte attributeFlags = objectInput.readByte();
    // if (attributeFlags != 0) {
    // size = attributeFlags;
    // int exprIDSize = objectInput.readByte();
    // for (int i = 0; i < exprIDSize; i++) {
    // set(i, F.GLOBAL_IDS[objectInput.readShort()]);
    // }
    // for (int i = exprIDSize; i < size; i++) {
    // set(i, (IExpr) objectInput.readObject());
    // }
    // return;
    // }

    // size = objectInput.readInt();
    for (int i = 1; i < SIZE; i++) {
      set(i, (IExpr) objectInput.readObject());
    }
  }

  @Override
  public IAST removeFromEnd(int fromPosition) {
    if (fromPosition == 1) {
      return new AST0(head());
    }
    if (fromPosition == 2) {
      return new AST1(head(), arg1);
    }
    if (fromPosition == 3) {
      return this;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
  }

  @Override
  public IASTAppendable reverse(IASTAppendable resultList) {
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(argSize());
    }
    resultList.append(arg2);
    resultList.append(arg1);
    return resultList;
  }

  /**
   * Replaces the element at the specified location in this {@code ArrayList} with the specified
   * object. Internally the <code>hashValue</code> will be reset to <code>0</code>.
   *
   * @param location the index at which to put the specified object.
   * @param object the object to add.
   * @return the previous element at the index.
   * @throws IndexOutOfBoundsException when {@code location < 0 || >= size()}
   */
  @Override
  public IExpr set(int location, IExpr object) {
    hashValue = 0;
    IExpr result;
    switch (location) {
      case 0:
        throw new IndexOutOfBoundsException("Index: 0, Size: 3");
      case 1:
        result = arg1;
        arg1 = object;
        return result;
      case 2:
        result = arg2;
        arg2 = object;
        return result;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
    }
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    if (i == 0) {
      return new AST2(expr, arg1(), arg2());
    }
    IASTMutable ast = copy();
    ast.set(i, expr);
    return ast;
  }

  /**
   * Returns the number of elements in this {@code ArrayList}.
   *
   * @return the number of elements in this {@code ArrayList}.
   */
  @Override
  final public int size() {
    return SIZE;
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    return new IExpr[] {head(), arg1, arg2};
  }

  @Override
  public ISymbol topHead() {
    return head();
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);

    // int size = size();
    // byte attributeFlags = (byte) 0;
    //
    // ExprID temp = F.GLOBAL_IDS_MAP.get(head());
    // if (temp != null) {
    // short exprID = temp.getExprID();
    // if (exprID <= Short.MAX_VALUE) {
    // int exprIDSize = 1;
    // short[] exprIDArray = new short[size];
    // exprIDArray[0] = exprID;
    // for (int i = 1; i < size; i++) {
    // temp = F.GLOBAL_IDS_MAP.get(get(i));
    // if (temp == null) {
    // break;
    // }
    // exprID = temp.getExprID();
    // if (exprID <= Short.MAX_VALUE) {
    // exprIDArray[i] = exprID;
    // exprIDSize++;
    // } else {
    // break;
    // }
    // }
    // // optimized path
    // attributeFlags = (byte) size;
    // objectOutput.writeByte(attributeFlags);
    // objectOutput.writeByte((byte) exprIDSize);
    // for (int i = 0; i < exprIDSize; i++) {
    // objectOutput.writeShort(exprIDArray[i]);
    // }
    // for (int i = exprIDSize; i < size; i++) {
    // objectOutput.writeObject(get(i));
    // }
    // return;
    // }
    // }

    // objectOutput.writeByte(attributeFlags);
    // objectOutput.writeInt(size-1);
    for (int i = 1; i < SIZE; i++) {
      objectOutput.writeObject(get(i));
    }
  }
}
