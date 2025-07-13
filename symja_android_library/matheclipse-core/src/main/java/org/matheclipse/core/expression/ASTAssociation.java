package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.matheclipse.core.eval.Errors;
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
import org.organicdesign.fp.collections.MutMap;
import org.organicdesign.fp.collections.RrbTree;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.collections.UnmodMap.UnEntry;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class ASTAssociation extends ASTRRBTree implements IAssociation {

  /**
   * Map the <code>IExpr()</code> keys to the index of the values in this AST.
   */
  private transient MutMap<IExpr, Integer> keyToIndexMap;

  /** Public no-arg constructor needed for serialization. */
  public ASTAssociation() {
    super(10, false);
    keyToIndexMap = StaticImports.mutableMap();
    append(S.Association);
  }

  /**
   * Create an association from a list of rules.
   *
   * @param listOfRules
   */
  /* package private */ ASTAssociation(IAST listOfRules) {
    super(listOfRules.size(), false);
    keyToIndexMap = StaticImports.mutableMap();
    append(S.Association);

    appendRules(listOfRules);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean append(IExpr expr) throws ArgumentTypeException {
    if (expr.isRuleAST() || size() == 0) {
      return super.append(expr);
    }
    // The argument `1` is not a rule or a list of rules.
    throw new ArgumentTypeException("invdt2", F.list(expr));
  }

  /**
   * Adds the specified rule at the end of this association.Existing duplicate rule keys will be
   * replaced by the new rule.
   *
   * @param rule the rule to add at the end of this association
   */
  @Override
  public final void appendRule(IExpr rule) {
    if (rule.isRuleAST()) {
      final int index = size();
      final int value = getRulePosition(rule.first());
      if (value == 0) {
        append(rule);
        keyToIndexMap.assoc(rule.first(), index);
      } else {
        set(value, rule);
      }
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
  }

  private static final int appendRule(ASTAssociation assoc, int index, IAST rule) {
    if (rule.isRuleAST()) {
      int indexValue = assoc.getRulePosition(rule.first());
      if (indexValue == 0) {
        assoc.appendRule(rule);
      } else {
        assoc.set(indexValue, rule);
      }
      return index;
    }
    throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
  }

  /**
   * Get the value-index from the internal map for the <code>key</code>
   * 
   * @param key
   * @return <code>0</code> if no value-index was found for the key
   */
  @Override
  public int getRulePosition(IExpr key) {
    Integer value = keyToIndexMap.get(key);
    return (value == null) ? 0 : value;
  }

  @Override
  public void appendRules(IAST listOfRules) {
    appendRules(listOfRules, 1, listOfRules.size());
  }

  @Override
  public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
    appendRules(size(), listOfRules, startPosition, endPosition);
  }

  private void appendRules(int index, IAST listOfRules, int startPosition, int endPosition) {
    if (listOfRules.isRuleAST()) {
      appendRule(this, index, listOfRules);
    } else {
      for (int i = startPosition; i < endPosition; i++) {
        IExpr rule = listOfRules.getRule(i);
        if (rule.isAssociation()) {
          ASTAssociation assoc = (ASTAssociation) rule;
          for (int j = 1; j < assoc.size(); j++) {
            index = appendRule(this, index, assoc.getRule(j));
          }
        } else if (rule.isRuleAST()) {
          index = appendRule(this, index, (IAST) rule);
        } else if (rule.isList()) {
          IAST list = (IAST) rule;
          appendRules(index, list, 1, list.size());
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
    ast.rrbTree = rrbTree.toMutRrbt();
    ast.hashValue = 0;
    ast.keyToIndexMap = keyToIndexMap.toMutMap(x -> x);
    return ast;
  }

  @Override
  public IAssociation copyAppendable() {
    return copy();
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copy();
  }

  @Override
  public IASTAppendable copyAST() {
    return F.mapRange(S.Association, 1, size(), i -> getValue(i));
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
            if (result.isNIL()) {
              result = copy();
            }
            result.set(i, getRule(i).setAtCopy(2, temp));
          }
        }
      }
      if (result.isPresent()) {
        result.builtinEvaled();
        return result;
      }
      builtinEvaled();
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
  public void forEachRule(Consumer<? super IExpr> action, int startOffset) {
    for (int i = startOffset; i < size(); i++) {
      if (i == 0) {
        action.accept(getValue(i));
      } else {
        action.accept(getRule(i));
      }
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
  public IAST getItems(int[] items, int length, int offset) {
    ASTAssociation assoc = new ASTAssociation();
    if (length > 0) {
      for (int i = 0; i < length; i++) {
        assoc.appendRule(getRule(items[i] + offset));
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
    int index = getRulePosition(F.$str(key));
    if (index > 0) {
      return getRule(index);
    }
    return F.NIL;
  }

  @Override
  public IAST getRule(IExpr key) {
    int index = getRulePosition(key);
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
    int index = getRulePosition(key);
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
   * @return <code>true</code>
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
  public boolean isAST(final IBuiltInSymbol header) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IBuiltInSymbol header, final int length) {
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

  /** {@inheritDoc} */
  @Override
  public int[] isAssociationMatrix() {
    final int[] dim = new int[2];
    dim[0] = argSize();
    if (dim[0] > 0) {
      dim[1] = 0;
      if (arg1().isList()) {
        dim[1] = ((IAST) arg1()).argSize();
        for (int i = 1; i < size(); i++) {
          if (!get(i).isList()) {
            // this row is not a list
            return null;
          }
          IAST rowList = (IAST) get(i);
          if (dim[1] != rowList.argSize()) {
            // this row has another dimension
            return null;
          }
          for (int j = 1; j < rowList.size(); j++) {
            if (rowList.get(j).isList()) {
              // this row is not a list
              return null;
            }
          }
        }
        return dim;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int isAssociationVector() {
    final int length = argSize();
    if (length > 0) {
      if (arg1().isList()) {
        return -1;
      }
      for (int i = 2; i < size(); i++) {
        if (get(i).isList()) {
          // row is a list
          return -1;
        }
      }
    }
    return length;
  }

  @Override
  public ArrayList<String> keyNames() {
    ArrayList<String> list = new ArrayList<String>();
    UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
    while (iterator.hasNext()) {
      UnEntry<IExpr, Integer> element = iterator.next();
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

    UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
    while (iterator.hasNext()) {
      UnEntry<IExpr, Integer> element = iterator.next();
      int value = element.getValue();
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
      int value = getRulePosition(key);
      assoc.appendRule(getRule(value));
    }
    return assoc;
  }

  /** {@inheritDoc} */
  @Override
  public IAST map(final Function<IExpr, ? extends IExpr> function, final int startOffset) {
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

  @Override
  public IAST mapReverse(final Function<IExpr, IExpr> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IAST matrixOrList() {

    boolean numericKeys = true;
    try {
      UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
      while (iterator.hasNext()) {
        UnEntry<IExpr, Integer> element = iterator.next();
        IExpr key = element.getKey();
        if (!key.isReal()) {
          @SuppressWarnings("unused")
          double d = key.evalf(); // create possible exception
          numericKeys = false;
          break;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      numericKeys = false;
    }
    if (numericKeys) {
      IASTAppendable list = F.ListAlloc(keyToIndexMap.size());
      UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
      while (iterator.hasNext()) {
        UnEntry<IExpr, Integer> element = iterator.next();
        IExpr key = element.getKey();
        int value = element.getValue();
        list.append(F.list(key, getValue(value)));
      }
      return list;
    } else {
      return F.mapRange(1, size(), i -> getValue(i));
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
    return F.function(symbol, arr);
  }

  /**
   * Adds the specified rule at the start of this association. Existing duplicate rule keys will be
   * replaced by the new rule at position 1.
   *
   * @param rule the rule to add at the end of this association
   * @return always true
   */
  @Override
  public final void prependRule(IExpr rule) {
    if (rule.isRuleAST()) {
      int value = getRulePosition(rule.first());
      hashValue = 0;
      if (value != 0) {
        remove(value);
      }
      insertAt(1, rule);
    } else if (rule.isEmptyList()) {
      // ignore empty list entries
    } else {
      throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
    }
  }

  /**
   * Insert the rule at the position. Re-index the existing positions greater equal the
   * <code>position</code>.
   * 
   * @param position
   * @param rule
   */
  private void insertAt(int position, IExpr rule) {
    rrbTree.insert(position, rule);
    UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
    while (iterator.hasNext()) {
      UnEntry<IExpr, Integer> element = iterator.next();
      int indx = element.getValue();
      if (indx >= position) {
        keyToIndexMap.assoc(element.getKey(), indx + 1);
      }
    }
    keyToIndexMap.assoc(rule.first(), 1);
  }

  @Override
  public final void mergeRule(IAST rule, IExpr head, EvalEngine engine) {
    if (rule.isRuleAST()) {
      int valueIndex = getRulePosition(rule.first());
      if (valueIndex == 0) {
        final int index = size();
        append(rule.setAtClone(2, F.List(rule.second())));
        keyToIndexMap.assoc(rule.first(), index);
      } else {
        IExpr value = getValue(valueIndex);
        IASTMutable newRule = rule.copy();
        IAST newList;
        if (value.isList()) {
          newList = ((IAST) value).appendClone(rule.second());
        } else {
          newList = F.List(value, rule.second());
        }
        newRule.set(2, engine.evaluate(F.unaryAST1(head, newList)));
        set(valueIndex, newRule);
      }
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
      // iterate backwards for prepend
      for (int i = endPosition - 1; i >= startPosition; i--) {
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
    MutMap<IExpr, Integer> mutable = keyToIndexMap.toMutMap(x -> x);
    mutable.without(result.first());
    UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
    while (iterator.hasNext()) {
      UnEntry<IExpr, Integer> element = iterator.next();
      int indx = element.getValue();
      if (indx >= location) {
        mutable = mutable.assoc(element.getKey(), indx - 1);
      }
    }
    keyToIndexMap = mutable;
    return result;
  }

  @Override
  public IExpr removeRule(IExpr key) {
    int index = getRulePosition(key);
    if (index > 0) {
      return remove(index);
    }
    return F.NIL;
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
  public IASTAppendable removePositionsAtCopy(int[] removedPositions, int untilIndex) {
    // if (untilIndex == 1) {
    // return removeAtCopy(removedPositions[0]);
    // }
    ASTAssociation assoc = copy();
    for (int j = untilIndex - 1; j >= 0; j--) {
      assoc.remove(removedPositions[j]);
    }
    return assoc;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable replaceSubset(int[] replacePosition, IExpr[] newEntries,
      int[] removePositions) {
    ASTAssociation assoc = copy();
    for (int j = 0; j < replacePosition.length; j++) {
      int position = replacePosition[j];
      if (position <= 0) {
        break;
      }
      assoc.set(position, newEntries[j]);
    }
    for (int j = removePositions.length - 1; j >= 0; j--) {
      int position = removePositions[j];
      if (position <= 0) {
        break;
      }
      assoc.remove(position);
    }
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
          keyToIndexMap.without(oldRule.first());
        }
        keyToIndexMap.assoc(rule.first(), location);
        rrbTree = rrbTree.replace(location, rule);
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
      MutMap<IExpr, Integer> mutable = keyToIndexMap.toMutMap(x -> x);
      // mutable.without(oldRule.first());
      mutable = mutable.assoc(oldRule.first(), location);
      keyToIndexMap = mutable;
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
    IntArrayList indices = new IntArrayList(argSize());
    for (int i = 1; i < size(); i++) {
      indices.add(i);
    }
    Comparator<Integer> comparator;
    if (comp == null) {
      comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i, Integer j) {
          return getValue(i).compareTo(getValue(j));
        }
      };
    } else {
      comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i, Integer j) {
          return comp.compare(getValue(i), getValue(j));
        }
      };
    }
    Collections.sort(indices, comparator);
    ASTAssociation result = copy();
    MutMap<IExpr, Integer> mutable = keyToIndexMap.toMutMap(x -> x);
    UnmodIterator<UnEntry<IExpr, Integer>> iterator = keyToIndexMap.iterator();
    while (iterator.hasNext()) {
      UnEntry<IExpr, Integer> element = iterator.next();
      int indx = element.getValue();
      for (int i = 0; i < indices.size(); i++) {
        if (indices.getInt(i) == indx) {
          indx = i + 1;
          break;
        }
      }
      int newValue = indices.getInt(indx - 1);
      result.set(indx, getRule(newValue));
      mutable = mutable.assoc(element.getKey(), indx);
    }
    result.keyToIndexMap = mutable;
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
