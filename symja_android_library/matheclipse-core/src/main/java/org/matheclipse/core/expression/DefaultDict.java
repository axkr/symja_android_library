package org.matheclipse.core.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * DefaultDict is a container map similar to {@link java.util.Map}. The functionality of both maps
 * and DefaultDict are almost the same, except for the fact that {@link DefaultDict#getValue(IExpr)}
 * never returns a <code>null</code> value. It provides a default value for the key that does not
 * exists. When the {@link IASTAppendable} interface is passed as the generic <code>T</code>
 * argument, then a DefaultDict is created with the values that are appendable lists.
 * 
 * @param <T>
 */
public class DefaultDict<T extends IExpr> {

  final Supplier<IExpr> defaultFactory;
  final Map<IExpr, IExpr> map;

  public DefaultDict() {
    this.defaultFactory = () -> F.ListAlloc();
    this.map = new TreeMap<IExpr, IExpr>();
  }

  public DefaultDict(Supplier<T> defaultFactory) {
    this.defaultFactory = (Supplier<IExpr>) defaultFactory;
    this.map = new TreeMap<IExpr, IExpr>();
  }

  public DefaultDict(Map<IExpr, IExpr> map, Supplier<T> defaultFactory) {
    this.defaultFactory = (Supplier<IExpr>) defaultFactory;
    this.map = map;
  }

  /**
   * Get the underlying {@link java.util.Map} of this dictionary
   * 
   * @return
   */
  public Map<IExpr, IExpr> getMap() {
    return map;
  }


  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(IExpr key) {
    return map.containsKey(key);
  }

  public boolean containsValue(IExpr value) {
    return map.containsValue(value);
  }

  /**
   * <code>biFunction.apply(entry.getKey(), entry.getValue())</code> will be called for every entry
   * of this dictionary. If it returns {@link F#NIL}, the result won't be appended to
   * <code>appendableList</code>. Otherwise the result will be appended to
   * <code>appendableList</code>.
   * 
   * @param appendableList
   * @param biFunction
   * @return <code>appendableList</code>
   */
  public IASTAppendable forEach(IASTAppendable appendableList,
      BiFunction<IExpr, IExpr, IExpr> biFunction) {
    for (Map.Entry<IExpr, IExpr> entry : entrySet()) {
      IExpr bf = biFunction.apply(entry.getKey(), entry.getValue());
      if (bf.isPresent()) {
        appendableList.append(bf);
      }
    }
    return appendableList;
  }

  /**
   * 
   * @param key
   * @return
   * @deprecated use {@link #getNull(IExpr)} instead
   */
  @Deprecated
  public T get(IExpr key) {
    return getNull(key);
  }

  /**
   * {@link DefaultDict#getNull(IExpr)} returns a <code>null</code> value, if the map doesn't
   * contain the <code>key</code>.
   * 
   * @param key
   * @return <code>null</code> if the map doesn't contain the <code>key</code>
   * @see <code>getValue(IExpr)</code> for returning a default value
   */
  public T getNull(IExpr key) {
    return (T) map.get(key);
  }

  /**
   * {@link DefaultDict#getValue(IExpr)} never returns a <code>null</code> value. It provides a
   * default value for the key that does not exists. When the {@link IASTAppendable} interface is
   * passed as the generic <code>T</code> argument, then a DefaultDict is created with the values
   * that are appendable lists of type {@link IASTAppendable}.
   * 
   * @param key
   * @return
   * @see <code>getNull(IExpr)</code> for returning <code>null</code> if the map doesn't contain the
   *      <code>key</code>
   */
  public T getValue(IExpr key) {
    IExpr value = map.get(key);
    if (value == null) {
      IExpr defaultValue = defaultFactory.get();
      map.put(key, defaultValue);
      return (T) defaultValue;
    }
    return (T) value;
  }

  public IExpr put(IExpr key, T value) {
    return map.put(key, value);
  }

  /**
   * Removes the mapping for a key from this map if it is present (optional operation). More
   * formally, if this map contains a mapping from key {@code k} to value {@code v} such that
   * {@code Objects.equals(key, k)}, that mapping is removed. (The map can contain at most one such
   * mapping.)
   *
   * @param key
   * @return the previous value associated with key, or null if there was no mapping for key.
   */
  public IExpr remove(IExpr key) {
    return map.remove(key);
  }

  public void clear() {
    map.clear();
  }

  public Set<IExpr> keySet() {
    return map.keySet();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DefaultDict) {
      return map.equals(((DefaultDict) o).map);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  public void forEach(BiConsumer<? super IExpr, ? super IExpr> action) {
    map.forEach(action);
  }

  /**
   * Removes the entry for the specified key only if it is currently mapped to the specified value.
   *
   * <p>
   * The default implementation makes no guarantees about synchronization or atomicity properties of
   * this method. Any implementation providing atomicity guarantees must override this method and
   * document its concurrency properties.
   *
   * @param key key with which the specified value is associated
   * @param value value expected to be associated with the specified key
   * @return {@code true} if the value was removed
   * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this
   *         map (<a href=
   *         "{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
   * @throws ClassCastException if the key or value is of an inappropriate type for this map
   *         (<a href=
   *         "{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
   * @throws NullPointerException if the specified key or value is null, and this map does not
   *         permit null keys or values (<a href=
   *         "{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
   */
  public boolean remove(IExpr key, IExpr value) {
    return map.remove(key, value);
  }

  @Override
  public String toString() {
    return map.toString();
  }


  public Set<Entry<IExpr, IExpr>> entrySet() {
    return map.entrySet();
  }

  public Collection<T> values() {
    return (Collection<T>) map.values();
  }
}
