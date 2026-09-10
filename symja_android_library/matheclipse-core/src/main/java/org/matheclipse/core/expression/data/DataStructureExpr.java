package org.matheclipse.core.expression.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DataStructure["DynamicArray"|"HashSet"|"HashTable", …]</code>: a container which is changed
 * in place.
 *
 * <p>
 * Everything else in Symja is a value: <code>Append</code> answers a new list and leaves the old
 * one alone. A data structure is the exception - <code>ds["Append", x]</code> changes the very
 * object every other expression holding it can see, which is what makes it usable as the buffer of
 * a server that is filled by one packet and read by the next.
 *
 * <p>
 * The methods are those of the Wolfram Language, spelled as a first string argument.
 */
public class DataStructureExpr extends DataExpr<Object> {

  private static final long serialVersionUID = 1L;

  /** The kind of container, as it was named to {@link #newInstance}. */
  private final String type;

  public static DataStructureExpr newDynamicArray(List<IExpr> elements) {
    return new DataStructureExpr("DynamicArray", new ArrayList<IExpr>(elements));
  }

  public static DataStructureExpr newHashSet(List<IExpr> elements) {
    return new DataStructureExpr("HashSet", new LinkedHashSet<IExpr>(elements));
  }

  public static DataStructureExpr newHashTable() {
    return new DataStructureExpr("HashTable", new LinkedHashMap<IExpr, IExpr>());
  }

  /**
   * A container of the named kind, or <code>null</code> if the name is not one this implements.
   */
  public static DataStructureExpr newInstance(String type, List<IExpr> elements) {
    switch (type) {
      case "DynamicArray":
        return newDynamicArray(elements);
      case "HashSet":
        return newHashSet(elements);
      case "HashTable":
        DataStructureExpr table = newHashTable();
        for (IExpr element : elements) {
          table.insert(element);
        }
        return table;
      default:
        return null;
    }
  }

  private DataStructureExpr(String type, Object data) {
    super(S.DataStructure, data);
    this.type = type;
  }

  public String type() {
    return type;
  }

  @SuppressWarnings("unchecked")
  private List<IExpr> array() {
    return (List<IExpr>) fData;
  }

  @SuppressWarnings("unchecked")
  private Set<IExpr> set() {
    return (Set<IExpr>) fData;
  }

  @SuppressWarnings("unchecked")
  private Map<IExpr, IExpr> map() {
    return (Map<IExpr, IExpr>) fData;
  }

  /** Add one element; for a hash table the element is a rule <code>key -> value</code>. */
  private IExpr insert(IExpr element) {
    switch (type) {
      case "DynamicArray":
        array().add(element);
        return S.Null;
      case "HashSet":
        return F.booleSymbol(set().add(element));
      case "HashTable":
        if (element.isRuleAST()) {
          map().put(element.first(), element.second());
          return S.Null;
        }
        return F.NIL;
      default:
        return F.NIL;
    }
  }

  private int length() {
    switch (type) {
      case "DynamicArray":
        return array().size();
      case "HashSet":
        return set().size();
      case "HashTable":
        return map().size();
      default:
        return 0;
    }
  }

  /** The contents as a list; for a hash table the values, as the Wolfram Language answers them. */
  private IAST elements() {
    IASTAppendable result = F.ListAlloc(length());
    switch (type) {
      case "DynamicArray":
        result.appendAll(array(), 0, array().size());
        break;
      case "HashSet":
        for (IExpr element : set()) {
          result.append(element);
        }
        break;
      case "HashTable":
        for (IExpr value : map().values()) {
          result.append(value);
        }
        break;
      default:
        break;
    }
    return result;
  }

  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    if (ast.size() < 2 || !ast.arg1().isString()) {
      return F.NIL;
    }
    String method = ast.arg1().toString();
    switch (method) {
      case "Length":
        return F.ZZ(length());
      case "Elements":
        return elements();
      case "Type":
        return F.stringx(type);
      case "EmptyQ":
        return F.booleSymbol(length() == 0);
      case "DropAll":
        switch (type) {
          case "DynamicArray":
            array().clear();
            break;
          case "HashSet":
            set().clear();
            break;
          case "HashTable":
            map().clear();
            break;
          default:
            break;
        }
        return S.Null;
      case "Append":
      case "Push":
        return ast.isAST2() && "DynamicArray".equals(type) ? insert(ast.arg2()) : F.NIL;
      case "Insert":
        return ast.isAST2() ? insert(ast.arg2()) : F.NIL;
      case "Part":
        if (ast.isAST2() && "DynamicArray".equals(type)) {
          int position = ast.arg2().toIntDefault();
          if (position != Integer.MIN_VALUE) {
            List<IExpr> array = array();
            int index = position < 0 ? array.size() + position : position - 1;
            if (index >= 0 && index < array.size()) {
              return array.get(index);
            }
          }
        }
        return F.NIL;
      case "MemberQ":
        if (ast.isAST2()) {
          switch (type) {
            case "HashSet":
              return F.booleSymbol(set().contains(ast.arg2()));
            case "DynamicArray":
              return F.booleSymbol(array().contains(ast.arg2()));
            default:
              return F.NIL;
          }
        }
        return F.NIL;
      case "KeyExistsQ":
        return ast.isAST2() && "HashTable".equals(type) //
            ? F.booleSymbol(map().containsKey(ast.arg2()))
            : F.NIL;
      case "Lookup":
        if (ast.isAST2() && "HashTable".equals(type)) {
          IExpr value = map().get(ast.arg2());
          return value == null ? F.Missing(F.stringx("KeyAbsent"), ast.arg2()) : value;
        }
        return F.NIL;
      case "Keys":
        if ("HashTable".equals(type)) {
          IASTAppendable keys = F.ListAlloc(map().size());
          for (IExpr key : map().keySet()) {
            keys.append(key);
          }
          return keys;
        }
        return F.NIL;
      case "Values":
        return "HashTable".equals(type) ? elements() : F.NIL;
      case "Remove":
      case "RemoveKey":
        if (ast.isAST2()) {
          switch (type) {
            case "HashSet":
              return F.booleSymbol(set().remove(ast.arg2()));
            case "HashTable":
              return F.booleSymbol(map().remove(ast.arg2()) != null);
            case "DynamicArray":
              return F.booleSymbol(array().remove(ast.arg2()));
            default:
              return F.NIL;
          }
        }
        return F.NIL;
      case "Pop":
        if ("DynamicArray".equals(type) && !array().isEmpty()) {
          return array().remove(array().size() - 1);
        }
        return F.NIL;
      default:
        return F.NIL;
    }
  }

  @Override
  public IAST fullForm() {
    return F.binaryAST2(S.DataStructure, F.stringx(type), F.ZZ(length()));
  }

  /** Identity, not contents: two containers are the same one or they are different. */
  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public String toString() {
    return "DataStructure[" + type + ", <" + length() + ">]";
  }
}
