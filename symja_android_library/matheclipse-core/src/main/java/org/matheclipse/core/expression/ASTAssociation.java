package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ASTAssociation extends AST implements IAssociation {

  /**
   * Map the <code>IExpr()</code> keys to the index of the values in this AST. For <code>Rule()
   * </code> the index is greater 0 and <code>get(index)</code> returns the value of the <code>
   * Rule()</code. For <code>RuleDelyed()</code> the index is less 0 and must be multiplied by -1
   * and <code>get(index * (-1))</code> returns the value of the <code>RuleDelayed()</code>.
   */
  private transient Object2IntOpenHashMap<IExpr> keyToIndexMap;

  /** Public no-arg constructor only needed for serialization */
  public ASTAssociation() {
    super(10, false);
    keyToIndexMap = new Object2IntOpenHashMap<IExpr>();
  }

  /**
   * Create an association from a list of rules.
   *
   * @param listOfRules
   */
  public ASTAssociation(IAST listOfRules) {
    super(listOfRules.size(), false);
    keyToIndexMap = new Object2IntOpenHashMap<IExpr>();
    append(S.Association);

    appendRules(listOfRules);
  }

  /**
   * Create an empty association with <code>initialCapacity</code>.
   *
   * @param initialCapacity
   */
  public ASTAssociation(final int initialCapacity) {
    this(initialCapacity, false);
  }

  /**
   * Create an empty association with <code>initialCapacity</code>.
   *
   * @param initialCapacity
   * @param setLength
   */
  public ASTAssociation(final int initialCapacity, final boolean setLength) {
    super(initialCapacity, setLength);
    keyToIndexMap = new Object2IntOpenHashMap<IExpr>();
    if (setLength) {
      set(0, S.Association);
    } else {
      append(S.Association);
    }
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
    throw new UnsupportedOperationException();
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
      int value = keyToIndexMap.getInt(rule.first());
      if (value == 0) {
        append(rule);
        keyToIndexMap.put(rule.first(), index++);
      } else {
        set(value, rule);
        keyToIndexMap.put(rule.first(), value);
      }
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
  }

  @Override
  public void appendRules(IAST listOfRules) {
    appendRules(listOfRules, 1, listOfRules.size());
  }

  @Override
  public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
    if (listOfRules.isRuleAST()) {
      appendRule(listOfRules);
      // int value = keyToIndexMap.getInt(listOfRules.first());
      // if (value == 0) {
      // append(listOfRules);
      // keyToIndexMap.put(listOfRules.first(), index++);
      // } else {
      // set(value, listOfRules);
      // keyToIndexMap.put(listOfRules.first(), value);
      // }
    } else {
      for (int i = startPosition; i < endPosition; i++) {
        IExpr rule = listOfRules.getRule(i);
        if (rule.isAssociation()) {
          ASTAssociation assoc = (ASTAssociation) rule;
          for (int j = 1; j < assoc.size(); j++) {
            rule = assoc.getRule(j);
            appendRule(rule);
            // int value = keyToIndexMap.getInt(rule.first());
            // if (value == 0) {
            // append(rule);
            // keyToIndexMap.put(rule.first(), index++);
            // } else {
            // set(value, rule);
            // keyToIndexMap.put(rule.first(), value);
            // }
          }
        } else if (rule.isRuleAST()) {
          appendRule(rule);
          // int value = keyToIndexMap.getInt(rule.first());
          // if (value == 0) {
          // append(rule);
          // keyToIndexMap.put(rule.first(), index++);
          // } else {
          // set(value, rule);
          // keyToIndexMap.put(rule.first(), value);
          // }
        } else if (rule.isList()) {
          IAST list = (IAST) rule;
          appendRules(list, 1, list.size());
        } else {
          throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
        }
      }
    }
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
    // ast.fProperties = null;
    ast.array = array.clone();
    ast.hashValue = 0;
    ast.firstIndex = firstIndex;
    ast.lastIndex = lastIndex;
    ast.keyToIndexMap = keyToIndexMap.clone();
    return ast;
  }

  @Override
  public IASTAppendable copyAppendable() {
    return copy();
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    ASTAssociation ast = new ASTAssociation();
    // ast.fProperties = null;
    if (size() + additionalCapacity > array.length) {
      ast.array = new IExpr[size() + additionalCapacity];
    } else {
      ast.array = array.clone();
    }
    ast.hashValue = 0;
    ast.firstIndex = firstIndex;
    ast.lastIndex = lastIndex;
    ast.keyToIndexMap = keyToIndexMap.clone();
    return ast;
  }

  @Override
  public IASTMutable copyAST() {
    IASTMutable result = super.copy();
    for (int i = 1; i < size(); i++) {
      result.set(i, getValue(i));
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
  // keyToIndexMap.put(element.getKey(), value);
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
    ASTAssociation result = new ASTAssociation(intialCapacity, false);
    result.appendRules(this.normal(false), 1, index);
    return result;
  }

  private void decIndex(int location) {
    hashValue = 0;
    for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
      // for (Map.Entry<IExpr, Integer> element : keyToIndexMap.entrySet()) {
      int indx = element.getIntValue();
      if (indx >= location) {
        keyToIndexMap.put(element.getKey(), indx - 1);
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ASTAssociation)) {
      return false;
    }
    return super.equals(obj);
    // keyToIndexMap doesn't matter for equals
    // ASTAssociation other = (ASTAssociation) obj;
    // if (keyToIndexMap == null || other.map == null) {
    // return keyToIndexMap == other.map;
    // }
    // return keyToIndexMap.equals(other.map);
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
    ASTAssociation assoc = new ASTAssociation(length, false);
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
    int index = keyToIndexMap.getInt(F.$str(key));
    if (index > 0) {
      return getRule(index);
    }
    return F.NIL;
  }

  @Override
  public IAST getRule(IExpr key) {
    int index = keyToIndexMap.getInt(key);
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
    int index = keyToIndexMap.getInt(key);
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

  private void incIndex(int location) {
    hashValue = 0;
    for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
      // for (Map.Entry<IExpr, Integer> element : keyToIndexMap.entrySet()) {
      int indx = element.getIntValue();
      if (indx >= location) {
        keyToIndexMap.put(element.getKey(), indx + 1);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    int index = 1;
    int start = firstIndex + index;
    for (int i = start; i < lastIndex; i++) {
      if (expr.equals(get(i))) {
        return index;
      }
      index++;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    int index = fromIndex;
    int start = firstIndex + index;
    for (int i = start; i < lastIndex; i++) {
      if (predicate.test(get(i))) {
        return index;
      }
      index++;
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
    for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
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
    for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
      int value = element.getIntValue();
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
    ASTAssociation assoc = new ASTAssociation(list.argSize(), false);
    for (int i = 1; i < list.size(); i++) {
      IExpr key = list.get(i);
      int value = keyToIndexMap.getInt(key);
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
      for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
        IExpr key = element.getKey();
        if (!key.isReal()) {
          double d = key.evalDouble();
          numericKeys = false;
          break;
        }
      }
    } catch (RuntimeException rex) {
      numericKeys = false;
    }
    if (numericKeys) {
      IASTAppendable list = F.ListAlloc(keyToIndexMap.size());
      for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
        IExpr key = element.getKey();
        int value = element.getIntValue();
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
    System.arraycopy(array, 1, arr, 0, size() - 1);
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
      int value = keyToIndexMap.getInt(rule.first());
      if (value == 0) {
        append(1, rule);
        incIndex(1);
        keyToIndexMap.put(rule.first(), 1);
      } else {
        set(value, rule);
        keyToIndexMap.put(rule.first(), value);
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
    append(S.Association);
    IAST ast = (IAST) objectInput.readObject();
    for (int i = 1; i < ast.size(); i++) {
      appendRule(ast.get(i));
    }
  }

  @Override
  public IExpr remove(int location) {
    IExpr result = super.remove(location);
    keyToIndexMap.remove(result.first(), location);
    decIndex(location);
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
        if (oldRule.isPresent()) {
          keyToIndexMap.removeInt(oldRule.first());
        }
        keyToIndexMap.put(rule.first(), location);
        return super.set(location, rule);
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
      keyToIndexMap.removeInt(oldRule.first());
      keyToIndexMap.put(oldRule.first(), location);
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
    ASTAssociation result = new ASTAssociation(argSize(), true);
    for (Object2IntMap.Entry<IExpr> element : keyToIndexMap.object2IntEntrySet()) {
      int indx = element.getIntValue();
      for (int i = 0; i < indices.size(); i++) {
        if (indices.get(i) == indx) {
          indx = i + 1;
          break;
        }
      }
      int newValue = indices.get(indx - 1);
      result.set(indx, getRule(newValue));
      result.keyToIndexMap.put(element.getKey(), indx);
    }
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
