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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
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
    this(context, null);
  }

  /**
   * A path whose current context is <code>context</code>, knowing the contexts <code>outer</code>
   * already knows.
   *
   * <p>
   * Inheriting them is what makes a context mean the same thing inside a package as outside it. A
   * package that begins with an empty map re-creates every context it then mentions, so
   * <code>Other`x</code> read inside the package is a different symbol from the
   * <code>Other`x</code> that was assigned outside it - and when the package ends, the empty
   * re-creation replaces the one holding the values.
   *
   * @param outer the path in force where the package begins, or <code>null</code>
   */
  public ContextPath(Context context, ContextPath outer) {
    fContextMap = outer == null ? new HashMap<>(17) : new HashMap<>(outer.fContextMap);
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
   * <code>`Private`x</code> mean <code>Foo`Private`x</code>. Every package writes its private
   * symbols that way, so a package cannot be read without it.
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
  private static final class SystemAliases {
    /*
     * Built on first use, not when ContextPath is initialized: the tables need F and S, and F's
     * initialization creates an EvalEngine, which needs ContextPath. Two threads which started
     * the two at the same time waited for each other forever.
     */
    static final Map<String, IBuiltInSymbol> EXACT = buildSystemAliases();

    static final Map<String, IBuiltInSymbol> LOWERCASE = buildLowercaseSystemAliases(EXACT);
  }

  private static Map<String, IBuiltInSymbol> buildLowercaseSystemAliases(
      Map<String, IBuiltInSymbol> exact) {
    Map<String, IBuiltInSymbol> aliases = new java.util.HashMap<>();
    for (Map.Entry<String, IBuiltInSymbol> entry : exact.entrySet()) {
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
    aliases.put("Developer`RawCompress", S.RawCompress);
    aliases.put("Developer`RawUncompress", S.RawUncompress);
    // packed arrays: a vector or matrix of machine reals is kept in a double[] storage
    aliases.put("Developer`PackedArrayQ", F.localFunction("PackedArrayQ", packing(0)));
    aliases.put("Developer`ToPackedArray", F.localFunction("ToPackedArray", packing(1)));
    aliases.put("Developer`FromPackedArray", F.localFunction("FromPackedArray", packing(2)));
    // the domain {{min, max}, ...} of an InterpolatingFunction, from the package which defines it
    aliases.put("DifferentialEquations`InterpolatingFunctionAnatomy`InterpolatingFunctionDomain",
        F.localFunction("InterpolatingFunctionDomain", x -> {
          EvalEngine engine = EvalEngine.get();
          org.matheclipse.core.interfaces.IExpr function = engine.evaluate(x);
          if (function.head() != S.InterpolatingFunction) {
            return F.NIL;
          }
          org.matheclipse.core.interfaces.IExpr domain =
              engine.evaluate(F.unaryAST1(function, F.stringx("Domain")));
          return domain.isList() ? domain : F.NIL;
        }));
    // the WLJS notebook shows an image only when Image`ValidImageQHold holds for it
    aliases.put("Image`ValidImageQHold", S.ImageQ);
    // does the expression print with a leading minus sign? FeynCalc tests it on sums of terms
    aliases.put("Internal`SyntacticNegativeQ",
        F.localFunction("SyntacticNegativeQ", //
            x -> F.booleSymbol(syntacticNegativeQ(EvalEngine.get().evaluate(x)))));
    // {{{exponents, coefficient}, ...}, variables} - FeynCalc reads the Newton polytope of a
    // polynomial from it
    aliases.put("GroebnerBasis`DistributedTermsList",
        F.localFunction("DistributedTermsList",
            new org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator() {
              @Override
              public org.matheclipse.core.interfaces.IExpr evaluate(IAST ast, EvalEngine engine) {
                return distributedTermsList(ast, engine);
              }
            }));
    return aliases;
  }

  /**
   * Does <code>expr</code> print with a leading minus sign? A sum is read by its first term, a
   * complex number by its imaginary part (unless that is zero).
   */
  private static boolean syntacticNegativeQ(org.matheclipse.core.interfaces.IExpr expr) {
    if (expr.isPlus()) {
      return syntacticNegativeQ(expr.first());
    }
    if (expr.isNumber() && !expr.isReal()) {
      org.matheclipse.core.interfaces.IReal im = ((org.matheclipse.core.interfaces.INumber) expr).im();
      if (!im.isZero()) {
        return im.isNegative();
      }
    }
    return expr.isNegativeSigned();
  }

  /**
   * The evaluators of <code>PackedArrayQ(expr, ...)</code> (<code>kind == 0</code>),
   * <code>ToPackedArray(expr, ...)</code> (<code>1</code>) and <code>FromPackedArray(expr)</code>
   * (<code>2</code>). Only a vector or matrix of machine reals has a packed storage
   * ({@link ASTRealVector}, {@link ASTRealMatrix}); <code>ToPackedArray</code> gives any other
   * expression back as it is.
   */
  private static org.matheclipse.core.eval.interfaces.IFunctionEvaluator packing(int kind) {
    // a core evaluator, because a built-in without a symbol ID is only dispatched to one
    return new org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator() {
      @Override
      public org.matheclipse.core.interfaces.IExpr evaluate(IAST ast, EvalEngine engine) {
        if (ast.argSize() < 1 || ast.argSize() > (kind == 2 ? 1 : 3)) {
          return F.NIL;
        }
        org.matheclipse.core.interfaces.IExpr arg1 = engine.evaluate(ast.arg1());
        boolean packed = arg1 instanceof ASTRealVector || arg1 instanceof ASTRealMatrix;
        switch (kind) {
          case 0:
            if (packed && ast.argSize() >= 2) {
              // PackedArrayQ(expr, type) and PackedArrayQ(expr, type, rank): the packed storage
              // holds machine reals only
              org.matheclipse.core.interfaces.IExpr type = engine.evaluate(ast.arg2());
              if (type != S.Real) {
                return S.False;
              }
              if (ast.argSize() == 3) {
                int rank = arg1 instanceof ASTRealVector ? 1 : 2;
                return F.booleSymbol(engine.evaluate(ast.arg3()).toIntDefault() == rank);
              }
            }
            return F.booleSymbol(packed);
          case 1:
            if (packed) {
              return arg1;
            }
            if (arg1.isList() && ((IAST) arg1).argSize() > 0) {
              IAST list = (IAST) arg1;
              if (list.forAll(x -> x instanceof Num)) {
                return new ASTRealVector(list.toDoubleVector(), false);
              }
              int[] dims = list.isMatrix(false);
              if (dims != null && dims[1] > 0
                  && list.forAll(row -> ((IAST) row).forAll(x -> x instanceof Num))) {
                return new ASTRealMatrix(list.toDoubleMatrix(), false);
              }
            }
            return arg1;
          default:
            if (arg1 instanceof ASTRealVector) {
              return F.mapRange(1, ((IAST) arg1).size(), i -> ((IAST) arg1).get(i));
            }
            if (arg1 instanceof ASTRealMatrix) {
              IAST matrix = (IAST) arg1;
              return F.mapRange(1, matrix.size(), i -> {
                IAST row = (IAST) matrix.get(i);
                return F.mapRange(1, row.size(), j -> row.get(j));
              });
            }
            return arg1;
        }
      }
    };
  }

  private static org.matheclipse.core.interfaces.IExpr distributedTermsList(IAST ast,
      EvalEngine engine) {
    if (ast.argSize() < 2) {
      return F.NIL;
    }
    org.matheclipse.core.interfaces.IExpr variables = engine.evaluate(ast.arg2());
    if (!variables.isList()) {
      variables = F.list(variables);
    }
    org.matheclipse.core.interfaces.IExpr rules = engine
        .evaluate(F.binaryAST2(S.CoefficientRules, engine.evaluate(ast.arg1()), variables));
    if (!rules.isList() || !((IAST) rules).forAll(x -> x.isRuleAST())) {
      return F.NIL;
    }
    IASTAppendable terms = F.ListAlloc(rules.argSize());
    ((IAST) rules).forEach(rule -> terms.append(F.list(rule.first(), rule.second())));
    return F.list(terms, variables);
  }

  /**
   * The built-in that answers <code>contextName</code> + <code>symbolName</code>, or
   * <code>null</code> when the system provides no such symbol.
   */
  public static IBuiltInSymbol systemAlias(String contextName, String symbolName) {
    IBuiltInSymbol alias = SystemAliases.EXACT.get(contextName + symbolName);
    if (alias != null) {
      return alias;
    }
    // in relaxed syntax the name written in the source has been lower-cased by now
    return SystemAliases.LOWERCASE.get((contextName + symbolName).toLowerCase(Locale.ENGLISH));
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
      result.append(element.completeContextName());
    }
    return result;
  }

  public Context remove(int index) {
    return path.remove(index);
  }

  /** Remove the first occurrence of <code>context</code> from the path. */
  public boolean remove(Context context) {
    return path.remove(context);
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
   * Replace the contexts searched for a name, as an assignment to <code>$ContextPath</code> does.
   * The current context stays what it is.
   *
   * @param contexts the new path, in search order
   */
  public void setPath(List<Context> contexts) {
    path = new ArrayList<>(contexts);
  }

  /**
   * Replace for example a &quot;serialized context&quot; in this context-path.
   *
   * @param context
   * @return
   */
  public boolean setGlobalContext(Context context) {
    return replaceLast(Context.GLOBAL_CONTEXT_NAME, context);
  }

  /**
   * Replace the last context named <code>contextName</code> in this context-path, in the context
   * map and as the current context.
   *
   * @return <code>false</code> if there is no context with this name in the path
   */
  private boolean replaceLast(String contextName, Context context) {
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
    replaceLast(contextName, context);
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

  /** Test if the system context is the first one in which a symbol name is looked up. */
  public boolean isSystemFirst() {
    return !path.isEmpty() && path.get(0) == Context.SYSTEM;
  }

  /**
   * Does a symbol of <code>context</code> print without its context? When the context is on the
   * context path or is the current one, as the short name is looked up there.
   */
  public boolean isShortName(Context context) {
    return path.contains(context) || (fContext != null && sameContext(fContext, context));
  }

  private static boolean sameContext(Context c1, Context c2) {
    return c1 == c2 || c1.completeContextName().equals(c2.completeContextName());
  }

  private boolean isOnPath(Context context) {
    for (Context c : path) {
      if (c == context || c.getContextName().equals(context.getContextName())) {
        return true;
      }
    }
    return false;
  }

  public ISymbol symbol(String symbolName, Context newContext, boolean relaxedSyntax) {
    String name = getName(symbolName, relaxedSyntax);
    // a $Context which isn't on $ContextPath is searched first: inside Begin["Pkg`Private`"] a
    // private symbol hides a public one of the same name. One which is on it counts at its place
    // there, so after System`x; a name x means System`x even if Global`x exists already.
    if (fContext != null && !isOnPath(fContext)) {
      ISymbol symbol = fContext.get(name);
      if (symbol != null) {
        return symbol;
      }
    }
    for (Context context : path) {
      ISymbol symbol = context.get(name);
      if (symbol != null) {
        return symbol;
      }
    }
    for (Context context : path) {
      // a package on the path, as Needs puts it there, whose function the system answers
      IBuiltInSymbol alias = systemAlias(context.completeContextName(), symbolName);
      if (alias != null) {
        return alias;
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
    fContextMap.putIfAbsent(path.fContext.completeContextName(), path.fContext);
    // Contexts the package created are carried out; one that already existed keeps the object it
    // had, which is the one holding the symbols anything outside the package refers to.
    path.fContextMap.forEach(fContextMap::putIfAbsent);
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
