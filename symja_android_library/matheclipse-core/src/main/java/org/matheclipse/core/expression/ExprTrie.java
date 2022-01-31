package org.matheclipse.core.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.parser.trie.TrieNode;

public class ExprTrie {
  private Trie<String, IExpr> exprMap;

  public ExprTrie() {
    this.exprMap = Config.TRIE_STRING2EXPR_BUILDER.build();
  }

  public void clear() {
    exprMap.clear();
  }

  public ExprTrie copy() {
    ExprTrie trie = new ExprTrie();
    trie.exprMap = Config.TRIE_STRING2EXPR_BUILDER.build();
    for (TrieNode<String, IExpr> entry : this.nodeSet()) {
      trie.exprMap.put(entry.getKey(), entry.getValue());
    }
    return trie;
  }

  public IExpr compute(String key,
      BiFunction<? super String, ? super IExpr, ? extends IExpr> remappingFunction) {
    return exprMap.compute(key, remappingFunction);
  }

  public IExpr computeIfAbsent(String key,
      Function<? super String, ? extends IExpr> mappingFunction) {
    return exprMap.computeIfAbsent(key, mappingFunction);
  }

  public IExpr computeIfPresent(String key,
      BiFunction<? super String, ? super IExpr, ? extends IExpr> remappingFunction) {
    return exprMap.computeIfPresent(key, remappingFunction);
  }

  public boolean containsKey(String key) {
    return exprMap.containsKey(key);
  }

  public boolean containsValue(IExpr value) {
    return exprMap.containsValue(value);
  }

  public Set<Entry<String, IExpr>> entrySet() {
    return exprMap.entrySet();
  }

  public Set<Entry<String, IExpr>> entrySet(String sequence) {
    return exprMap.entrySet(sequence);
  }

  public Set<Entry<String, IExpr>> entrySet(String sequence, TrieMatch match) {
    return exprMap.entrySet(sequence, match);
  }

  public boolean equals(Object obj) {
    if (obj instanceof ExprTrie) {
      return exprMap.equals(((ExprTrie) obj).exprMap);
    }
    return false;
  }

  public void forEach(BiConsumer<? super String, ? super IExpr> action) {
    exprMap.forEach(action);
  }

  public IExpr get(String sequence) {
    return exprMap.get(sequence);
  }

  public IExpr get(String sequence, TrieMatch match) {
    return exprMap.get(sequence, match);
  }

  public TrieMatch getDefaultMatch() {
    return exprMap.getDefaultMatch();
  }

  public IExpr getDefaultValue() {
    return exprMap.getDefaultValue();
  }

  public IExpr getOrDefault(String key, IExpr defaultValue) {
    return exprMap.getOrDefault(key, defaultValue);
  }

  public boolean has(String sequence) {
    return exprMap.has(sequence);
  }

  public boolean has(String sequence, TrieMatch match) {
    return exprMap.has(sequence, match);
  }

  public int hashCode() {
    return exprMap.hashCode();
  }

  public boolean isEmpty() {
    return exprMap.isEmpty();
  }

  public Set<String> keySet() {
    return exprMap.keySet();
  }

  public Set<String> keySet(String sequence) {
    return exprMap.keySet(sequence);
  }

  public Set<String> keySet(String sequence, TrieMatch match) {
    return exprMap.keySet(sequence, match);
  }

  public IExpr merge(String key, IExpr value,
      BiFunction<? super IExpr, ? super IExpr, ? extends IExpr> remappingFunction) {
    return exprMap.merge(key, value, remappingFunction);
  }

  public Trie<String, IExpr> newEmptyClone() {
    return exprMap.newEmptyClone();
  }

  public Set<TrieNode<String, IExpr>> nodeSet() {
    return exprMap.nodeSet();
  }

  public Set<TrieNode<String, IExpr>> nodeSet(String sequence) {
    return exprMap.nodeSet(sequence);
  }

  public Set<TrieNode<String, IExpr>> nodeSet(String sequence, TrieMatch match) {
    return exprMap.nodeSet(sequence, match);
  }

  public Iterable<TrieNode<String, IExpr>> nodeSetAll() {
    return exprMap.nodeSetAll();
  }

  public Iterable<TrieNode<String, IExpr>> nodeSetAll(String sequence) {
    return exprMap.nodeSetAll(sequence);
  }

  public Iterable<TrieNode<String, IExpr>> nodeSetAll(String sequence, TrieMatch match) {
    return exprMap.nodeSetAll(sequence, match);
  }

  public IExpr put(String query, Function<IExpr, IExpr> updater) {
    return exprMap.put(query, updater);
  }

  public IExpr put(String query, Function<IExpr, IExpr> updater, IExpr defaultPrevious) {
    return exprMap.put(query, updater, defaultPrevious);
  }

  public IExpr put(String query, IExpr value) {
    return exprMap.put(query, value);
  }

  public void putAll(Map<? extends String, ? extends IExpr> map) {
    exprMap.putAll(map);
  }

  public IExpr putIfAbsent(String key, IExpr value) {
    return exprMap.putIfAbsent(key, value);
  }

  public IExpr remove(String sequence) {
    return exprMap.remove(sequence);
  }

  public boolean remove(String key, IExpr value) {
    return exprMap.remove(key, value);
  }

  public IExpr replace(String key, IExpr value) {
    return exprMap.replace(key, value);
  }

  public boolean replace(String key, IExpr oldValue, IExpr newValue) {
    return exprMap.replace(key, oldValue, newValue);
  }

  public void replaceAll(BiFunction<? super String, ? super IExpr, ? extends IExpr> function) {
    exprMap.replaceAll(function);
  }

  public void setDefaultMatch(TrieMatch match) {
    exprMap.setDefaultMatch(match);
  }

  public void setDefaultValue(IExpr defaultValue) {
    exprMap.setDefaultValue(defaultValue);
  }

  public int size() {
    return exprMap.size();
  }

  public String toString() {
    return exprMap.toString();
  }

  public Collection<IExpr> values() {
    return exprMap.values();
  }

  public Collection<IExpr> values(String sequence) {
    return exprMap.values(sequence);
  }

  public Collection<IExpr> values(String sequence, TrieMatch match) {
    return exprMap.values(sequence, match);
  }
}
