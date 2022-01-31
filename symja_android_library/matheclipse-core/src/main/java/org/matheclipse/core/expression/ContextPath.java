package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

public final class ContextPath implements Iterable<Context> {

  public static Set<String> PACKAGES = new TreeSet<String>();

  static {
    PACKAGES.add(Context.RUBI_STR);
    PACKAGES.add(Context.GLOBAL_CONTEXT_NAME);
    PACKAGES.add(Context.SYSTEM_CONTEXT_NAME);
  }

  /**
   * Get the initial context(s) for an evaluation engine.
   *
   * @return
   */
  public static ContextPath initialContext() {
    ContextPath cp = new ContextPath();
    cp.fContextMap = new HashMap<String, Context>(17);
    cp.path.add(Context.SYSTEM);
    cp.fContextMap.put(Context.SYSTEM.getContextName(), Context.SYSTEM);
    // don't put RUBI on the context path
    cp.fContextMap.put(Context.RUBI.getContextName(), Context.RUBI);
    // fContextMap.put(Context.DUMMY.getContextName(), Context.DUMMY);
    Context global = new Context(Context.GLOBAL_CONTEXT_NAME);
    cp.path.add(global);
    cp.fContextMap.put(Context.GLOBAL_CONTEXT_NAME, global);
    cp.fContext = global;
    return cp;
  }

  private HashMap<String, Context> fContextMap;
  private ArrayList<Context> path = new ArrayList<Context>();

  private Context fContext;

  private ContextPath() {
    // for copy() method
  }

  public ContextPath(Context context) {
    fContextMap = new HashMap<String, Context>(17);
    path.add(context);
    path.add(Context.SYSTEM);
    fContextMap.put(Context.SYSTEM.getContextName(), Context.SYSTEM);
    // don't put RUBI on the context path
    fContextMap.put(Context.RUBI.getContextName(), Context.RUBI);
    // fContextMap.put(Context.DUMMY.getContextName(), Context.DUMMY);
    fContext = context;
  }

  public boolean add(Context context) {
    return path.add(context);
  }

  public void add(int index, Context context) {
    path.add(index, context);
  }

  public boolean contains(Context context) {
    return path.contains(context);
  }

  public ContextPath copy() {
    ContextPath cp = new ContextPath();
    cp.fContextMap = new HashMap<>(fContextMap);
    cp.path = new ArrayList<>(path);
    cp.fContext = fContext;
    return cp;
  }

  /**
   * If the current parent context isn't <code>null</code> or <code>Global`</code> print the
   * complete context name prepended with the parent context name.
   *
   * @return
   */
  public IStringX currentCompleteContextName() {
    return F.stringx(fContext.completeContextName());
  }

  public Context currentContext() {
    return fContext;
  }

  /**
   * Print the context name without prepending the parent context name.
   *
   * @return
   */
  public IStringX currentContextString() {
    return F.stringx(fContext.getContextName());
  }

  public Context get(int index) {
    return path.get(index);
  }

  public Context getContext(String contextName) {
    Context context = fContextMap.get(contextName);
    if (context != null) {
      return context;
    }
    for (int i = 0; i < path.size(); i++) {
      Context ctxt = path.get(i);
      if (ctxt.completeContextName().equals(contextName)) {
        return ctxt;
      }
    }
    context = new Context(contextName);
    fContextMap.put(contextName, context);
    return context;
  }

  public Map<String, Context> getContextMap() {
    return fContextMap;
  }

  public Context getContext(String contextName, Context parentContext) {
    String name = contextName;
    if (parentContext != null) {
      String packageName = parentContext.getContextName();
      name = packageName.substring(0, packageName.length() - 1) + name;
    }
    Context context = fContextMap.get(name);
    if (context != null) {
      return context;
    }
    context = new Context(contextName, parentContext);
    fContextMap.put(name, context);
    return context;
  }

  public Context getGlobalContext() {
    return fContextMap.get(Context.GLOBAL_CONTEXT_NAME);
  }

  public static ISymbol getSymbol(String symbolName, final Context context, boolean relaxedSyntax) {
    String name = symbolName;
    if (relaxedSyntax) {
      if (symbolName.length() == 1) {
        name = symbolName;
      } else {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    ISymbol symbol = context.get(name);
    if (symbol != null) {
      return symbol;
    }

    symbol = new Symbol(name, context);
    context.put(name, symbol);
    // engine.putUserVariable(name, symbol);
    if (Config.SERVER_MODE) {
      if (name.charAt(0) == '$') {
        F.SYMBOL_OBSERVER.createUserSymbol(symbol);
      }
    }

    return symbol;
  }

  @Override
  public Iterator<Context> iterator() {
    return path.iterator();
  }

  /**
   * Return the context path as list of context strings.
   *
   * @return
   */
  public IAST pathAsStrings() {
    int size = path.size();
    IASTAppendable result = F.ListAlloc(size);

    for (int i = 0; i < size; i++) {
      result.append(path.get(i).getContextName());
    }
    return result;
  }

  public Context remove(int index) {
    return path.remove(index);
  }

  public ISymbol removeSymbol(String symbolName) {
    String name = symbolName;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (symbolName.length() == 1) {
        name = symbolName;
      } else {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    Context context;
    ISymbol symbol;
    for (int i = path.size() - 1; i >= 0; i--) {
      context = path.get(i);
      if (context.equals(Context.SYSTEM)) {
        // don't remove predefined symbols
        continue;
      }
      symbol = context.remove(name);
      if (symbol != null) {
        return symbol;
      }
    }
    return null;
  }

  public Context set(int index, Context element) {
    return path.set(index, element);
  }

  public void setCurrentContext(Context context) {
    fContext = context;
  }

  /**
   * Replace for example a &quot;serialized context&quot; in this context-path
   *
   * @param context
   * @return
   */
  public boolean setGlobalContext(Context context) {
    int size = path.size();
    int start = size - 1;
    for (int i = start; i >= 0; i--) {
      Context temp = path.get(i);
      if (temp.getContextName().equals(Context.GLOBAL_CONTEXT_NAME)) {
        path.set(i, context);
        fContextMap.put(Context.GLOBAL_CONTEXT_NAME, context);
        if (fContext.getContextName().equals(Context.GLOBAL_CONTEXT_NAME)) {
          fContext = context;
        }
        return true;
      }
    }
    return false;
  }

  public int size() {
    return path.size();
  }

  public ISymbol symbol(String symbolName, Context newContext, boolean relaxedSyntax) {
    String name = symbolName;
    if (relaxedSyntax) {
      if (symbolName.length() == 1) {
        name = symbolName;
      } else {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    Context context;
    ISymbol symbol;
    for (int i = 0; i < path.size(); i++) {
      context = path.get(i);
      symbol = context.get(name);
      if (symbol != null) {
        return symbol;
      }
    }

    symbol = newContext.get(name);
    if (symbol != null) {
      return symbol;
    }

    symbol = new Symbol(name, newContext);
    newContext.put(name, symbol);
    // engine.putUserVariable(name, symbol);
    if (Config.SERVER_MODE) {
      if (name.charAt(0) == '$') {
        F.SYMBOL_OBSERVER.createUserSymbol(symbol);
      }
    }

    return symbol;
  }

  /**
   * Test if the <code>symbolName</code> is defined in the one of the contexts available on the
   * context path.
   *
   * @param symbolName
   * @param relaxedSyntax
   * @return
   */
  public boolean hasSymbol(String symbolName, boolean relaxedSyntax) {
    String name = symbolName;
    if (relaxedSyntax) {
      if (symbolName.length() == 1) {
        name = symbolName;
      } else {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    Context context;
    ISymbol symbol;
    for (int i = 0; i < path.size(); i++) {
      context = path.get(i);
      symbol = context.get(name);
      if (symbol != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Synchronize the contexts back to this context map.
   *
   * @param path
   * @return
   */
  public void synchronize(ContextPath path) {
    Context c = path.fContext;
    if (!fContextMap.containsKey(c.getContextName())) {
      fContextMap.put(c.getContextName(), c);
    }
    for (Map.Entry<String, Context> entry : path.fContextMap.entrySet()) {
      if (!fContextMap.containsKey(entry.getKey())) {
        fContextMap.put(entry.getKey(), entry.getValue());
      }
    }
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
