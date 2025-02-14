package org.matheclipse.core.interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import org.matheclipse.core.eval.EvalEngine;

public interface IAssociation extends IASTAppendable {

  /**
   * Append a list of rules.
   *
   * @param listOfRules
   */
  public void appendRules(IAST listOfRules);

  /**
   * Append a range of a list of rules.
   *
   * @param listOfRules
   * @param startPosition the start position inclusive
   * @param endPosition the end position exclusive
   */
  public void appendRules(IAST listOfRules, int startPosition, int endPosition);

  /** Copy this association */
  @Override
  public IAssociation copy();

  @Override
  public IASTAppendable copyAST();

  /** Copy this association as empty association */
  @Override
  public IASTAppendable copyHead(final int intialCapacity);

  /**
   * Return the key which points to the <code>position</code>.
   *
   * @param position
   * @return
   */
  public IExpr getKey(int position);


  /**
   * Assuming this is a list of rules or an <code>IAssociation</code>. Return the first rule which
   * equals the <code>key</code> argument, otherwise return <code>F.NIL</code>.
   *
   * @param key
   * @return <code>F.NIL</code> if no rule was found.
   */
  @Override
  public IAST getRule(IExpr key);

  /**
   * Return the <code>position</code> of the rule with the given <code>key</code>.
   * 
   * @param key
   * @return
   */
  public int getRulePosition(IExpr key);

  @Override
  public IAST getRule(int position);

  /**
   * Assuming this is a list of rules or an <code>IAssociation</code>. Return the first rule which
   * equals the <code>key</code> argument, otherwise return <code>F.NIL</code>.
   *
   * @param key
   * @return <code>F.NIL</code> if no rule was found.
   */
  @Override
  public IAST getRule(String key);

  /**
   * Return the value associated to the <code>key</code>. If no value is available return <code>
   * Missing("KeyAbsent", key)</code>
   *
   * @param key
   * @return
   */
  public IExpr getValue(IExpr key);

  /**
   * Return the value associated to the <code>key</code>. If no value is available return the <code>
   * defaultValue</code>
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue);

  public int[] isAssociationMatrix();

  public int isAssociationVector();

  /**
   * Test if the <code>expr</code> is a key in this association.
   *
   * @param expr
   * @return
   */
  public boolean isKey(IExpr expr);

  /**
   * Get the key names of this association as an ArrayList<code>key1, key2,...</code>
   *
   * @return
   */
  public List<String> keyNames();

  /**
   * Get the keys of this association as a<code>List(key1, key2,...)</code>
   *
   * @return
   */
  public IASTMutable keys();

  /**
   * Return a new association sorted by the keys of the association.
   *
   * @return the sorted association
   */
  public IAssociation keySort();

  /**
   * Return a new association has the key values sorted by the <code>comparator</code>.
   *
   * @return the sorted association
   */
  public IAssociation keySort(Comparator<IExpr> comparator);

  /** Return the list of rules as a matrix or list */
  public IAST matrixOrList();

  public void mergeRule(IAST rule, IExpr head, EvalEngine engine);

  /** Return the list of rules <code>{a->b, c:>d, ...}</code> represented by this association. */
  @Override
  public IASTMutable normal(boolean nilIfUnevaluated);

  /**
   * Prepend a list of rules.
   *
   * @param listOfRules
   */
  public void prependRules(IAST listOfRules);

  /**
   * Prepend a range of a list of rules.
   *
   * @param listOfRules
   * @param startPosition the start position inclusive
   * @param endPosition the end position exclusive
   */
  public void prependRules(IAST listOfRules, int startPosition, int endPosition);

  public IAssociation reverse(IAssociation newAssoc);

  /**
   * Return a new association sorted by the values of the association.
   *
   * @return the sorted association
   */
  public IAssociation sort();

  /**
   * Return a new association sorted by the comparator.
   *
   * @return the sorted association
   */
  public IAssociation sort(Comparator<IExpr> comparator);

  /**
   * Get the values of this association as a<code>List(value1, value2,...)</code>
   *
   * @return
   */
  public IASTMutable values();
}
