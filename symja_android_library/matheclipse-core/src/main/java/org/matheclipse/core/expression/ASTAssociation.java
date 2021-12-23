package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.IVisitor;
import org.organicdesign.fp.StaticImports;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.MutMap;
import org.organicdesign.fp.collections.RrbTree;
import org.organicdesign.fp.collections.RrbTree.MutRrbt;

public class ASTAssociation extends ASTRRBTree implements IAssociation {

  /**
   * Map the <code>IExpr()</code> keys to the index of the values in this AST. For <code>Rule()
   * </code> the index is greater 0 and <code>get(index)</code> returns the value of the <code>
   * Rule()</code. For <code>RuleDelyed()</code> the index is less 0 and must be multiplied by -1
   * and <code>get(index * (-1))</code> returns the value of the <code>RuleDelayed()</code>.
   */
  private transient ImMap<IExpr, Integer> keyToIndexMap;

  /** Public no-arg constructor needed for serialization. */
  public ASTAssociation() {
    super(10, false);
    //    keyToIndexMap = new Object2IntOpenHashMap<IExpr>();
    keyToIndexMap = StaticImports.map();
    append(S.Association);
  }

  /**
   * Create an association from a list of rules.
   *
   * @param listOfRules
   */
  /* package private*/ ASTAssociation(IAST listOfRules) {
    super(listOfRules.size(), false);
    //    keyToIndexMap = new Object2IntOpenHashMap<IExpr>();
    keyToIndexMap = StaticImports.map();
    append(S.Association);

    appendRules(listOfRules);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean append(IExpr expr) {
    if (expr.isRuleAST() || size() == 0) {
      return super.append(expr);
    }
    // The argument `1` is not a rule or a list of rules.
    throw new ArgumentTypeException("invdt2", F.List(expr));
  }

  /**
   * Adds the specified rule at the end of this association.Existing duplicate rule keys will be
   * replaced by the new rule.
   *
   * @param rule the rule to add at the end of this association
   * @return always true
   */
  @Override
  public final void appendRule(IExpr rule) {
    int index = size();
    if (rule.isRuleAST()) {
      int value = getInt(rule.first());
      if (value == 0) {
        append(rule);
        keyToIndexMap = keyToIndexMap.mutable().assoc(rule.first(), index++).immutable();
      } else {
        set(value, rule);
        keyToIndexMap = keyToIndexMap.mutable().assoc(rule.first(), index++).immutable();
      }
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
  }

  private static final int appendRule(
      ASTAssociation assoc, MutMap<IExpr, Integer> map, int index, IExpr rule) {
    if (rule.isRuleAST()) {
      Integer value = map.get(rule.first());
      if (value == null) {
        assoc.append(rule);
        map.assoc(rule.first(), index++);
      } else {
        assoc.set(value, rule);
        map.assoc(rule.first(), value);
      }
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
    return index;
  }

  private int getInt(IExpr key) {
    Integer value = (Integer) keyToIndexMap.get(key);
    if (value == null) {
      return 0;
    }
    return value;
  }

  @Override
  public void appendRules(IAST listOfRules) {
    appendRules(listOfRules, 1, listOfRules.size());
  }

  @Override
  public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
    MutMap<IExpr, Integer> temp = keyToIndexMap.mutable();
    appendRules(temp, size(), listOfRules, startPosition, endPosition);
    keyToIndexMap = temp.immutable();
  }

  private int appendRules(
      MutMap<IExpr, Integer> temp,
      int index,
      IAST listOfRules,
      int startPosition,
      int endPosition) {
    if (listOfRules.isRuleAST()) {
      index = appendRule(this, temp, index, listOfRules);
    } else {
      for (int i = startPosition; i < endPosition; i++) {
        IExpr rule = listOfRules.getRule(i);
        if (rule.isAssociation()) {
          ASTAssociation assoc = (ASTAssociation) rule;
          for (int j = 1; j < assoc.size(); j++) {
            rule = assoc.getRule(j);
            index = appendRule(this, temp, index, rule);
          }
        } else if (rule.isRuleAST()) {
          index = appendRule(this, temp, index, rule);
        } else if (rule.isList()) {
          IAST list = (IAST) rule;
          index = appendRules(temp, index, list, 1, list.size());
        } else {
          throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
        }
      }
    }
    return index;
  }

  @Override
  public IExpr arg1() {
    return get(1);
  }

  @Override
  public IExpr arg2() {
    return get(2);
  }

  @Override
  public IExpr arg3() {
    return get(3);
  }

  @Override
  public IExpr arg4() {
    return get(4);
  }

  @Override
  public IExpr arg5() {
    return get(5);
  }

  @Override
  public ASTAssociation copy() {
    ASTAssociation ast = new ASTAssociation();
    //    ast.fProperties = null;
    ast.rrbTree = rrbTree.toMutRrbt();
    ast.hashValue = 0;
    ast.keyToIndexMap = keyToIndexMap.mutable().immutable();
    return ast;
  }

  @Override
  public IASTAppendable copyAppendable() {
    return copy();
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copy();
  }

  @Override
  public IASTMutable copyAST() {
    IASTAppendable result = F.ast(S.Association, size());
    for (int i = 1; i < size(); i++) {
      result.append(getValue(i));
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyHead() {
    return F.ast(S.Association, size());
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyHead(final int intialCapacity) {
    return F.ast(S.Association, intialCapacity);
  }

  // public boolean appendAllRules(ASTAssociation ast, int startPosition, int endPosition) {
  // if (ast.size() > 0 && startPosition < endPosition) {
  // normalCache = null;
  // appendAll(ast, startPosition, endPosition);
  // for (Object2IntMap.Entry<IExpr> element : ast.map.object2IntEntrySet()) {
  // int value = element.getIntValue();
  // if (Math.abs(value) >= startPosition && Math.abs(value) < endPosition) {
  // keyToIndexMap = keyToIndexMap.__put(element.getKey(), value);
  // }
  // }
  // return true;
  // }
  // return false;
  // }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyUntil(int index) {
    return copyUntil(index, index);
  }

  /** {@inheritDoc} */
  @Override
  public final IASTAppendable copyUntil(final int intialCapacity, int index) {
    ASTAssociation result = new ASTAssociation();
    result.appendRules(this.normal(false), 1, index);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ASTAssociation) {
      final ASTAssociation assoc = (ASTAssociation) obj;
      final RrbTree<IExpr> imList = assoc.rrbTree;
      final int size = rrbTree.size();
      if (imList.size() == size) {
        for (int i = 0; i < size; i++) {
          if (!rrbTree.get(i).equals(imList.get(i))) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
      IAssociation result = F.NIL;
      for (int i = 1; i < size(); i++) {
        IExpr arg = getRule(i);
        if (arg.isRule()) {
          // for Rules eval rhs / for RuleDelayed don't
          IExpr temp = engine.evaluateNIL(arg.second());
          if (temp.isPresent()) {
            if (!result.isPresent()) {
              result = copy();
            }
            result.set(i, getRule(i).setAtCopy(2, temp));
          }
        }
      }
      if (result.isPresent()) {
        result.addEvalFlags(IAST.BUILT_IN_EVALED);
        return result;
      }
      addEvalFlags(IAST.BUILT_IN_EVALED);
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    if (filterAST instanceof ASTAssociation) {
      for (int i = 1; i < size(); i++) {
        if (predicate.test(getValue(i))) {
          ((ASTAssociation) filterAST).appendRule(getRule(i));
        }
      }
      return filterAST;
    }
    return super.filter(filterAST, predicate);
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, int maxMatches) {
    if (filterAST instanceof ASTAssociation) {
      int[] count = new int[1];
      if (count[0] >= maxMatches) {
        return filterAST;
      }
      for (int i = 1; i < size(); i++) {
        if (predicate.test(getValue(i))) {
          if (++count[0] == maxMatches) {
            ((ASTAssociation) filterAST).appendRule(getRule(i));
            break;
          }
          ((ASTAssociation) filterAST).appendRule(getRule(i));
        }
      }
      return filterAST;
    }
    return super.filter(filterAST, predicate, maxMatches);
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    for (int i = startOffset; i < size(); i++) {
      action.accept(getValue(i));
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
    for (int i = startOffset; i < endOffset; i++) {
      action.accept(getValue(i));
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, ObjIntConsumer<? super IExpr> action) {
    for (int i = startOffset; i < endOffset; i++) {
      action.accept(getValue(i), i);
    }
  }

  @Override
  public String fullFormString() {
    return normal(S.Association).fullFormString();
  }

  @Override
  public IExpr get(int position) {
    if (position == 0) {
      return head();
    }
    return super.get(position).second();
  }

  @Override
  public IAST getItems(int[] items, int length) {
    ASTAssociation assoc = new ASTAssociation();
    if (length > 0) {
      for (int i = 0; i < length; i++) {
        assoc.appendRule(getRule(items[i]));
      }
    }
    return assoc;
  }

  @Override
  public IExpr getKey(int position) {
    IExpr temp = getRule(position).first();
    if (temp.isPresent()) {
      return F.Key(temp);
    }
    return F.C0;
  }

  @Override
  public IAST getRule(String key) {
    int index = (Integer) keyToIndexMap.get(F.$str(key));
    if (index > 0) {
      return getRule(index);
    }
    return F.NIL;
  }

  @Override
  public IAST getRule(IExpr key) {
    int index = getInt(key);
    if (index > 0) {
      return getRule(index);
    }
    return F.NIL;
  }

  @Override
  public IAST getRule(int position) {
    IExpr temp = super.get(position);
    if (temp != null && temp.isRuleAST()) {
      return (IAST) temp;
    }
    return F.NIL;
  }

  @Override
  public IExpr getValue(IExpr key) {
    return getValue(key, () -> F.Missing(F.stringx("KeyAbsent"), key));
  }

  @Override
  public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue) {
    int index = getInt(key);
    if (index == 0) {
      return defaultValue.get(); // F.Missing(F.stringx("KeyAbsent"), key);
    }
    return getValue(index);
  }

  @Override
  public IExpr getValue(int position) {
    if (position == 0) {
      return super.get(position);
    }
    return super.get(position).second();
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 19;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    for (int i = 1; i < size(); i++) {
      if (expr.equals(get(i))) {
        return i;
      }
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    for (int i = 1; i < size(); i++) {
      if (predicate.test(get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Test if this AST is an association <code>&lt;|a-&gt;b, c-&gt;d|&gt;</code>(i.e. type <code>
   * AssociationAST</code>)
   *
   * @return
   */
  @Override
  public boolean isAssociation() {
    return true;
  }

  @Override
  public boolean isAST() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IExpr header) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IExpr header, final int length) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(IExpr header, int length, IExpr... args) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST(IExpr head, int minLength, int maxLength) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST(final String symbol) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST(final String symbol, final int length) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST0() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST1() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST2() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST3() {
    return false;
  }

  @Override
  public boolean isAtom() {
    return true;
  }

  @Override
  public boolean isKey(IExpr key) {
    return keyToIndexMap.containsKey(key);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isListOrAssociation() {
    return true;
  }

  @Override
  public ArrayList<String> keyNames() {
    ArrayList<String> list = new ArrayList<String>();
    ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
    for (Entry<IExpr, Integer> element : set) {
      list.add(element.getKey().toString());
    }
    return list;
  }

  @Override
  public IASTMutable keys() {
    return keys(S.List);
  }

  protected IASTMutable keys(IBuiltInSymbol symbol) {
    IASTMutable list = F.astMutable(symbol, argSize());
    ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
    for (Entry<IExpr, Integer> element : set) {
      int value = element.getValue();
      if (value < 0) {
        value *= -1;
      }
      list.set(value, element.getKey());
    }
    return list;
  }

  @Override
  public IAssociation keySort() {
    return keySort(null);
  }

  @Override
  public IAssociation keySort(Comparator<IExpr> comparator) {
    IASTMutable list = keys();
    if (comparator == null) {
      EvalAttributes.sort(list);
    } else {
      EvalAttributes.sort(list, comparator);
    }
    ASTAssociation assoc = new ASTAssociation();
    for (int i = 1; i < list.size(); i++) {
      IExpr key = list.get(i);
      int value = getInt(key);
      assoc.appendRule(getRule(value));
    }
    return assoc;
  }

  /** {@inheritDoc} */
  @Override
  public IAST map(final Function<IExpr, IExpr> function, final int startOffset) {
    IExpr temp;
    ASTAssociation result = null;
    int i = startOffset;
    int size = size();
    while (i < size) {
      temp = function.apply(getValue(i));
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        result = copy();
        result.set(i, getRule(i).setAtCopy(2, temp));
        i++;
        break;
      }
      i++;
    }
    if (result != null) {
      while (i < size) {
        temp = function.apply(getValue(i));
        if (temp.isPresent()) {
          result.set(i, getRule(i).setAtCopy(2, temp));
        }
        i++;
      }
    }
    if (result != null) {
      return result;
    }
    return this;
  }

  public IAST mapReverse(final Function<IExpr, IExpr> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IAST matrixOrList() {

    boolean numericKeys = true;
    try {
      ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
      for (Entry<IExpr, Integer> element : set) {
        IExpr key = element.getKey();
        if (!key.isReal()) {
          double d = key.evalDouble(); // create possible exception
          numericKeys = false;
          break;
        }
      }
    } catch (RuntimeException rex) {
      numericKeys = false;
    }
    if (numericKeys) {
      IASTAppendable list = F.ListAlloc(keyToIndexMap.size());
      ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
      for (Entry<IExpr, Integer> element : set) {
        IExpr key = element.getKey();
        int value = element.getValue();
        list.append(F.List(key, getValue(value)));
      }
      return list;
    } else {
      IASTAppendable list = F.ListAlloc(size());
      for (int i = 1; i < size(); i++) {
        list.append(getValue(i));
      }
      return list;
    }
  }

  /** {@inheritDoc} */
  @Override
  public IAST most() {
    if (size() > 1) {
      return splice(argSize());
    }
    return F.NIL;
  }

  @Override
  public IASTMutable normal(boolean nilIfUnevaluated) {
    return normal(S.List);
  }

  protected IASTMutable normal(IBuiltInSymbol symbol) {
    IExpr[] arr = new IExpr[size() - 1];
    for (int i = 1; i < rrbTree.size(); i++) {
      arr[i - 1] = rrbTree.get(i);
    }
    return F.ast(arr, symbol);
  }

  /**
   * Adds the specified rule at the start of this association. Existing duplicate rule keys will be
   * replaced by the new rule.
   *
   * @param rule the rule to add at the end of this association
   * @return always true
   */
  @Override
  public final void prependRule(IExpr rule) {
    if (rule.isRuleAST()) {
      int value = getInt(rule.first());
      hashValue = 0;
      if (value == 0) {
        final int firstPosition = 1;
        MutRrbt<IExpr> mutableList = StaticImports.mutableRrb();
        mutableList.append(S.Association);
        mutableList.append(rule);
        for (int i = 1; i < rrbTree.size(); i++) {
          mutableList.append(rrbTree.get(i));
        }
        rrbTree = mutableList.toMutRrbt();

        MutMap<IExpr, Integer> mutableMap = keyToIndexMap.mutable();
        ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
        for (Entry<IExpr, Integer> element : set) {
          int indx = element.getValue();
          if (indx >= firstPosition) {
            mutableMap.assoc(element.getKey(), indx + 1);
          }
        }
        mutableMap.assoc(rule.first(), firstPosition);
        keyToIndexMap = mutableMap.immutable();
      } else {
        super.set(value, rule);
        keyToIndexMap = keyToIndexMap.mutable().assoc(rule.first(), value).immutable();
      }
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
  }

  @Override
  public void prependRules(IAST listOfRules) {
    prependRules(listOfRules, 1, listOfRules.size());
  }

  @Override
  public void prependRules(IAST listOfRules, int startPosition, int endPosition) {
    if (listOfRules.isRuleAST()) {
      prependRule(listOfRules);
    } else {
      for (int i = startPosition; i < endPosition; i++) {
        IExpr rule = listOfRules.getRule(i);
        if (rule.isAssociation()) {
          ASTAssociation assoc = (ASTAssociation) rule;
          for (int j = 1; j < assoc.size(); j++) {
            rule = assoc.getRule(j);
            prependRule(rule);
          }
        } else if (rule.isRuleAST()) {
          prependRule(rule);
        } else if (rule.isList()) {
          IAST list = (IAST) rule;
          prependRules(list, 1, list.size());
        } else {
          throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
        }
      }
    }
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    IAST ast = (IAST) objectInput.readObject();
    for (int i = 1; i < ast.size(); i++) {
      appendRule(ast.get(i));
    }
  }

  @Override
  public IExpr remove(int location) throws IndexOutOfBoundsException {
    hashValue = 0;
    // throws IndexOutOfBoundsException
    IExpr result = super.remove(location);
    MutMap<IExpr, Integer> mutable = keyToIndexMap.mutable();
    mutable.without(result.first());
    ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
    for (Entry<IExpr, Integer> element : set) {
      int indx = element.getValue();
      if (indx >= location) {
        mutable.assoc(element.getKey(), indx - 1);
      }
    }
    keyToIndexMap = mutable.immutable();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public final IASTMutable removeAtCopy(int position) {
    ASTAssociation assoc = copy();
    assoc.remove(position);
    return assoc;
  }

  /** {@inheritDoc} */
  @Override
  public IAST rest() {
    if (size() > 1) {
      return removeAtCopy(1);
    }
    return this;
  }

  @Override
  public IAssociation reverse(IAssociation newAssoc) {
    for (int i = argSize(); i >= 1; i--) {
      newAssoc.appendRule(getRule(i));
    }
    return newAssoc;
  }

  @Override
  public IExpr set(final int location, final IExpr rule) {
    if (location > 0) {
      if (rule.isRuleAST()) {
        final IAST oldRule = getRule(location);
        MutMap<IExpr, Integer> mutable = keyToIndexMap.mutable();
        if (oldRule.isPresent()) {
          mutable.without(oldRule.first());
        }
        mutable.assoc(rule.first(), location);
        rrbTree = rrbTree.replace(location, rule);
        keyToIndexMap = mutable.immutable();
        return oldRule;
      }
      // illegal arguments: \"`1`\" in `2`
      ArgumentTypeException.throwArg(rule, S.Association);
      return F.NIL;
    }
    // set header
    return super.set(location, rule);
  }

  @Override
  public IExpr setValue(final int location, final IExpr value) {
    if (location > 0) {
      final IAST oldRule = getRule(location);
      MutMap<IExpr, Integer> mutable = keyToIndexMap.mutable();
      //      mutable.without(oldRule.first());
      mutable.assoc(oldRule.first(), location);
      keyToIndexMap = mutable.immutable();
      return super.set(location, oldRule.setAtCopy(2, value));
    }
    // set header
    return super.set(0, value);
  }

  @Override
  public IAssociation sort() {
    return sort(null);
  }

  @Override
  public IAssociation sort(Comparator<IExpr> comp) {
    List<Integer> indices = new ArrayList<Integer>(argSize());
    for (int i = 1; i < size(); i++) {
      indices.add(i);
    }
    Comparator<Integer> comparator;
    if (comp == null) {
      comparator =
          new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
              return getValue(i).compareTo(getValue(j));
            }
          };
    } else {
      comparator =
          new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
              return comp.compare(getValue(i), getValue(j));
            }
          };
    }
    Collections.sort(indices, comparator);
    ASTAssociation result = copy();
    MutMap<IExpr, Integer> mutable = keyToIndexMap.mutable();
    ImSet<Entry<IExpr, Integer>> set = keyToIndexMap.entrySet();
    for (Entry<IExpr, Integer> element : set) {
      int indx = element.getValue();
      for (int i = 0; i < indices.size(); i++) {
        if (indices.get(i) == indx) {
          indx = i + 1;
          break;
        }
      }
      int newValue = indices.get(indx - 1);
      result.set(indx, getRule(newValue));
      mutable.assoc(element.getKey(), indx);
    }
    result.keyToIndexMap = mutable.immutable();
    return result;
  }

  @Override
  public IASTMutable values() {
    return values(S.List);
  }

  protected IASTMutable values(IBuiltInSymbol symbol) {
    IASTMutable list = copyAST();
    list.set(0, symbol);
    return list;
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    IAST ast = normal(false);
    objectOutput.writeObject(ast);
  }
}
