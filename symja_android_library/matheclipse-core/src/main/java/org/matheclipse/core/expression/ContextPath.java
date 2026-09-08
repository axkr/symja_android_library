package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

public final class ContextPath implements Iterable<Context> {

  public static Set<String> PACKAGES = new TreeSet<>();

  /**
   * The short names contexts may be written under - what <code>$ContextAliases</code> holds and
   * <code>Needs["A`" -&gt; "a`"]</code> adds. A name whose first segment is a key here is read as
   * though the full context had been written: <code>a`x</code> is <code>A`x</code>.
   */
  private static final Map<String, String> CONTEXT_ALIASES = new java.util.LinkedHashMap<>();

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
    cp.fContextMap = new HashMap<>(17);
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

  private Map<String, Context> fContextMap;
  private List<Context> path = new ArrayList<>();

  private Context fContext;

  private ContextPath() {
    // for copy() method
  }

  public ContextPath(Context context) {
    fContextMap = new HashMap<>(17);
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

  /**
   * Resolve a context name written in source into the complete name it denotes.
   *
   * <p>
   * A name starting with a backtick is relative to <code>$Context</code>: inside
   * <code>BeginPackage["Foo`"]; Begin["`Private`"]</code> both <code>`x</code> and
   * <code>`Private`x</code> mean <code>Foo`Private`x</code>. Every Wolfram Language package writes
   * its private symbols that way, so a package cannot be read without it.
   *
   * @param contextName the context as it was written, with or without a leading backtick
   * @param engine supplies the current context
   * @return the complete context name, always ending in a backtick
   */
  public static String resolveContextName(String contextName, EvalEngine engine) {
    if (contextName.length() == 0) {
      return contextName;
    }
    if (contextName.charAt(0) != '`') {
      return expandAlias(contextName);
    }
    String currentContext = engine.getContext().completeContextName();
    if (currentContext.endsWith("`")) {
      currentContext = currentContext.substring(0, currentContext.length() - 1);
    }
    return currentContext + contextName;
  }

  /**
   * Symbols the system provides under a name in another context.
   *
   * <p>
   * <code>Internal`DynamicLibraryExtension[]</code> and
   * <code>PacletManager`Package`loadWolframLanguageCode[…]</code> are written by packages exactly
   * like that, and the built-in that answers them lives in <code>System`</code>. Rather than fill
   * those contexts in every engine, a name that is asked for here is answered with the built-in.
   */
  private static final Map<String, IBuiltInSymbol> SYSTEM_ALIASES = buildSystemAliases();

  private static final Map<String, IBuiltInSymbol> SYSTEM_ALIASES_LOWERCASE =
      buildLowercaseSystemAliases();

  private static Map<String, IBuiltInSymbol> buildLowercaseSystemAliases() {
    Map<String, IBuiltInSymbol> aliases = new java.util.HashMap<>();
    for (Map.Entry<String, IBuiltInSymbol> entry : SYSTEM_ALIASES.entrySet()) {
      aliases.put(entry.getKey().toLowerCase(Locale.ENGLISH), entry.getValue());
    }
    return aliases;
  }

  private static Map<String, IBuiltInSymbol> buildSystemAliases() {
    Map<String, IBuiltInSymbol> aliases = new java.util.HashMap<>();
    aliases.put("Internal`DynamicLibraryExtension", S.DynamicLibraryExtension);
    aliases.put("PacletManager`Package`loadWolframLanguageCode", S.LoadWolframLanguageCode);
    aliases.put("Language`ExtendedFullDefinition", S.ExtendedFullDefinition);
    aliases.put("Experimental`ValueFunction", S.ValueFunction);
    return aliases;
  }

  /**
   * The built-in that answers <code>contextName</code> + <code>symbolName</code>, or
   * <code>null</code> when the system provides no such symbol.
   */
  public static IBuiltInSymbol systemAlias(String contextName, String symbolName) {
    IBuiltInSymbol alias = SYSTEM_ALIASES.get(contextName + symbolName);
    if (alias != null) {
      return alias;
    }
    // in relaxed syntax the name written in the source has been lower-cased by now
    return SYSTEM_ALIASES_LOWERCASE.get((contextName + symbolName).toLowerCase(Locale.ENGLISH));
  }

  /** Add a short name for a context, as <code>Needs["A`" -&gt; "a`"]</code> does. */
  public static synchronized void setContextAlias(String alias, String contextName) {
    CONTEXT_ALIASES.put(alias.endsWith("`") ? alias : alias + "`", contextName);
  }

  /** The aliases in force, for <code>$ContextAliases</code>. */
  public static synchronized Map<String, String> contextAliases() {
    return new java.util.LinkedHashMap<>(CONTEXT_ALIASES);
  }

  /** Replace every alias, for a fresh <code>$ContextAliases</code> assignment. */
  public static synchronized void setContextAliases(Map<String, String> aliases) {
    CONTEXT_ALIASES.clear();
    CONTEXT_ALIASES.putAll(aliases);
  }

  /**
   * <code>a`Sub`x</code> written under the alias <code>a`</code> for <code>Actual`</code> is
   * <code>Actual`Sub`x</code>. Only the first segment is an alias.
   */
  private static synchronized String expandAlias(String contextName) {
    if (CONTEXT_ALIASES.isEmpty()) {
      return contextName;
    }
    int firstBacktick = contextName.indexOf('`');
    if (firstBacktick < 0) {
      return contextName;
    }
    String head = contextName.substring(0, firstBacktick + 1);
    String expanded = CONTEXT_ALIASES.get(head);
    return expanded == null ? contextName : expanded + contextName.substring(firstBacktick + 1);
  }

  public Context getContext(String contextName) {
    Context context = fContextMap.get(contextName);
    if (context != null) {
      return context;
    }
    for (Context ctxt : path) {
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
    return fContextMap.computeIfAbsent(name, n -> new Context(contextName, parentContext));
  }

  public static IAST getContexts() {
    IASTAppendable result = F.ListAlloc(ContextPath.PACKAGES.size());
    for (String str : ContextPath.PACKAGES) {
      result.append(F.$str(str));
    }
    return result;
  }

  public Context getGlobalContext() {
    return fContextMap.get(Context.GLOBAL_CONTEXT_NAME);
  }

  public static ISymbol getSymbol(String symbolName, final Context context, boolean relaxedSyntax) {
    String name = getName(symbolName, relaxedSyntax);
    return getSymbolFromContext(context, name);
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
    IASTAppendable result = F.ListAlloc(path.size());
    for (Context element : path) {
      result.append(element.getContextName());
    }
    return result;
  }

  public Context remove(int index) {
    return path.remove(index);
  }

  public ISymbol removeSymbol(String symbolName) {
    String name = getName(symbolName, ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS);
    for (int i = path.size() - 1; i >= 0; i--) {
      Context context = path.get(i);
      if (context.equals(Context.SYSTEM)) {
        // don't remove predefined symbols
        continue;
      }
      ISymbol symbol = context.remove(name);
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
   * Replace for example a &quot;serialized context&quot; in this context-path.
   *
   * @param context
   * @return
   */
  public boolean setGlobalContext(Context context) {
    String contextName = Context.GLOBAL_CONTEXT_NAME;
    for (int i = path.size() - 1; i >= 0; i--) {
      Context currentContext = path.get(i);
      if (currentContext.getContextName().equals(contextName)) {
        path.set(i, context);
        fContextMap.put(contextName, context);
        if (fContext.getContextName().equals(contextName)) {
          fContext = context;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Replace for example a &quot;serialized context&quot; in this context-path.
   * 
   * @param contextName
   * @param context
   * @return
   */
  public boolean setContext(String contextName, Context context) {
    for (int i = path.size() - 1; i >= 0; i--) {
      Context currentContext = path.get(i);
      if (currentContext.getContextName().equals(contextName)) {
        path.set(i, context);
        fContextMap.put(contextName, context);
        if (fContext.getContextName().equals(contextName)) {
          fContext = context;
        }
        break;
      }
    }
    for (int i = 0; i < path.size(); i++) {
      if (path.get(i).getContextName().equals(contextName)) {
        path.set(i, context);
        return true;
      }
    }
    return false;
  }

  public int size() {
    return path.size();
  }

  public ISymbol symbol(String symbolName, Context newContext, boolean relaxedSyntax) {
    String name = getName(symbolName, relaxedSyntax);

    for (Context context : path) {
      ISymbol symbol = context.get(name);
      if (symbol != null) {
        return symbol;
      }
    }
    return getSymbolFromContext(newContext, name);
  }

  private static String getName(String name, boolean relaxedSyntax) {
    return relaxedSyntax && name.length() != 1 ? name.toLowerCase(Locale.ENGLISH) : name;
  }

  private static ISymbol getSymbolFromContext(Context context, String name) {
    return context.computeIfAbsent(name, n -> {
      ISymbol symbol = new Symbol(n, context);
      if (Config.SERVER_MODE && (n.charAt(0) == '$')) {
        F.SYMBOL_OBSERVER.createUserSymbol(symbol);
      }
      return symbol;
    });
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
    fContextMap.putIfAbsent(path.fContext.getContextName(), path.fContext);
    path.fContextMap.forEach(fContextMap::put);
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
