package org.matheclipse.core.builtin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.trie.SuggestTree;
import org.matheclipse.parser.trie.SuggestTree.Node;

public class IOFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      // S.General.setEvaluator(new General());
      S.Echo.setEvaluator(new Echo());
      S.EchoFunction.setEvaluator(new EchoFunction());
      S.Message.setEvaluator(new Message());
      S.Messages.setEvaluator(new Messages());
      S.Names.setEvaluator(new Names());
      S.Print.setEvaluator(new Print());
      S.Short.setEvaluator(new Short());
      S.StyleForm.setEvaluator(new StyleForm());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Echo(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints the <code>expr</code> to the default output stream and returns <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Echo(expr, label)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints <code>label</code> before printing <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Echo(expr, label, head)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints <code>label</code> before printing <code>head(expr)</code> and returns <code>expr
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; {Echo(f(x,y)), Print(g(a,b))}
   * {f(x,y),Null}
   * </code>
   * </pre>
   *
   * <p>
   * prints
   *
   * <pre>
   * <code>f(x,y)
   * g(a,b)
   * </code>
   * </pre>
   *
   * <p>
   * and returns
   *
   * <pre>
   * <code>{f(x,y),Null}
   * </code>
   * </pre>
   */
  private static class Echo extends Print {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final PrintStream stream = engine.getOutPrintStream();
      final StringBuilder buf = new StringBuilder();
      OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
      boolean[] convert = new boolean[] {true};
      IExpr arg1 = ast.arg1();
      IExpr result = engine.evaluate(arg1);
      if (ast.argSize() >= 2) {
        IExpr arg2 = engine.evaluate(ast.arg2());
        printExpression(arg2, out, buf, convert, engine);
        if (ast.isAST3()) {
          IExpr arg3 = engine.evaluate(F.unaryAST1(ast.arg3(), arg1));
          printExpression(arg3, out, buf, convert, engine);
        } else {
          printExpression(result, out, buf, convert, engine);
        }
      } else {
        printExpression(result, out, buf, convert, engine);
      }
      stream.println(buf.toString());
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>EchoFunction()[expr]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * operator form of the <code>Echo</code>function. Print the <code>expr</code> to the default
   * output stream and return <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>EchoFunction(head)[expr]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints <code>head(expr)</code> and returns <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>EchoFunction(label, head)[expr]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints <code>label</code> before printing <code>head(expr)</code> and returns <code>expr
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; {EchoFunction()[f(x,y)], Print(g(a,b))}
   * {f(x,y),Null}
   * </code>
   * </pre>
   *
   * <p>
   * prints
   *
   * <pre>
   * <code>f(x,y)
   * g(a,b)
   * </code>
   * </pre>
   *
   * <p>
   * and returns
   *
   * <pre>
   * <code>{f(x,y),Null}
   * </code>
   * </pre>
   */
  private static final class EchoFunction extends Print {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {

      if (ast.isAST1() && ast.head().isAST()) {
        final int size = ast.head().size();
        switch (size) {
          case 1:
            return F.unaryAST1(S.Echo, ast.arg1());
          case 2:
            return echo(ast.arg1(), ast.head().first(), engine);
          case 3:
            return F.ternaryAST3(S.Echo, ast.arg1(), ast.head().first(), ast.head().second());
          default:
        }
      }
      return F.NIL;
    }

    private static IExpr echo(final IExpr arg1, IExpr headFirst, EvalEngine engine) {
      final PrintStream stream = engine.getOutPrintStream();
      final StringBuilder buf = new StringBuilder();
      OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
      boolean[] convert = new boolean[] {true};
      IExpr result = engine.evaluate(arg1);
      IExpr arg3 = engine.evaluate(F.unaryAST1(headFirst, arg1));
      printExpression(arg3, out, buf, convert, engine);
      stream.println(buf.toString());
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2_0;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Message(symbol::msg, expr1, expr2, ...)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * displays the specified message, replacing placeholders in the message text with the
   * corresponding expressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; a::b = &quot;Hello world!&quot;
   * Hello world!
   *
   * &gt;&gt; Message(a::b)
   * a: Hello world!
   *
   * &gt;&gt; a::c := &quot;Hello `1`, Mr 00`2`!&quot;
   *
   * &gt;&gt; Message(a::c, &quot;you&quot;, 3 + 4)
   * a: Hello you, Mr 007!
   * </code>
   * </pre>
   */
  private static class Message extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 1) {
        if (ast.arg1().isString()) {
          String message = ast.arg1().toString();
          for (int i = 2; i < ast.size(); i++) {
            message = message.replaceAll("`" + (i - 1) + "`", ast.get(i).toString());
          }
          return F.stringx(": " + message);
        }
        if (ast.arg1().isAST(S.MessageName, 3)) {
          IAST messageName = (IAST) ast.arg1();
          String messageShortcut = messageName.arg2().toString();
          if (messageName.arg1().isSymbol()) {
            IExpr temp = Errors.message((ISymbol) messageName.arg1(), messageShortcut, ast);
            if (temp.isPresent()) {
              return temp;
            }
          }
          return Errors.message(S.General, messageShortcut, ast);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  private static class Messages extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkIsVariable(ast, 1, engine);
      if (arg1.isSymbol()) {
        ISymbol symbol = (ISymbol) arg1;
        RulesData rulesData = symbol.getRulesData();
        if (rulesData != null) {
          Map<String, IStringX> map = rulesData.getMessages();
          if (map != null) {
            IASTAppendable result = F.ListAlloc(map.size());
            for (Map.Entry<String, IStringX> entry : map.entrySet()) {
              result.append(F.RuleDelayed(F.HoldPattern(F.MessageName(symbol, entry.getKey())),
                  entry.getValue()));
            }
            return result;
          }
        }
        return F.CEmptyList;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class Short extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.stringx(Errors.shorten(ast.arg1()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class StyleForm extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() == S.StyleForm) {
        return ast.apply(S.Style);
      }
      return F.NIL;
    }
  }

  private static final class Names extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return getAllNames();
      }

      IExpr arg1 = ast.arg1();
      boolean ignoreCase = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS;
      if (ast.size() > 2) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine, true);
        IExpr option = options.getOption(S.IgnoreCase);
        if (option.isTrue()) {
          ignoreCase = true;
        }
      }
      if (arg1.isString()) {
        int indx = arg1.toString().indexOf("`");
        if (indx < 0) {
          arg1 = F.$str("System`" + arg1.toString());
        }
      }

      Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
      java.util.regex.Pattern pattern =
          StringFunctions.toRegexPattern(arg1, true, ignoreCase, ast, groups, engine);

      if (pattern == null) {
        return F.NIL;
      }

      return getNamesByPattern(pattern, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class Print extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final PrintStream stream = engine.getOutPrintStream();
      final StringBuilder buf = new StringBuilder();
      OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
      boolean[] convert = new boolean[] {true};
      ast.forEach(x -> {
        IExpr temp = engine.evaluate(x);
        printExpression(temp, out, buf, convert, engine);
      });
      if (!convert[0]) {
        stream.println("ERROR-IN-OUTPUTFORM");
        return S.Null;
      }
      stream.println(buf.toString());
      return S.Null;
    }

    protected static void printExpression(IExpr x, OutputFormFactory out, final StringBuilder buf,
        boolean[] convert, EvalEngine engine) {
      if (x instanceof IStringX) {
        buf.append(x.toString());
      } else {
        if (x.isASTSizeGE(S.Style, 2)) {
          printExpression(x.first(), out, buf, convert, engine);
        } else if (convert[0] && !out.convert(buf, x)) {
          convert[0] = false;
        }
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  public static IAST getNamesByPattern(java.util.regex.Pattern pattern, EvalEngine engine) {
    ContextPath contextPath = engine.getContextPath();
    IASTAppendable list = F.ListAlloc(31);
    Map<String, Context> contextMap = contextPath.getContextMap();
    for (Map.Entry<String, Context> mapEntry : contextMap.entrySet()) {
      Context context = mapEntry.getValue();
      for (Map.Entry<String, ISymbol> entry : context.entrySet()) {
        String fullName = context.completeContextName() + entry.getKey();
        java.util.regex.Matcher matcher = pattern.matcher(fullName);
        if (matcher.matches()) {
          if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
            String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(entry.getValue().getSymbolName());
            if (str != null) {
              list.append(F.$str(str));
              continue;
            }
          }
          ISymbol value = entry.getValue();
          if (context.isGlobal() || context.isSystem()) {
            list.append(F.$str(value.toString()));
          } else {
            list.append(F.$str(fullName));
          }
        }
      }
    }

    for (Context context : contextPath) {
      String completeContextName = context.completeContextName();
      if (!contextMap.containsKey(completeContextName)) {
        for (Map.Entry<String, ISymbol> entry : context.entrySet()) {
          String fullName = completeContextName + entry.getKey();
          java.util.regex.Matcher matcher = pattern.matcher(fullName);
          if (matcher.matches()) {
            if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
              String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(entry.getValue().getSymbolName());
              if (str != null) {
                list.append(F.$str(str));
                continue;
              }
            }
            ISymbol value = entry.getValue();
            if (context.isGlobal() || context.isSystem()) {
              list.append(F.$str(value.toString()));
            } else {
              list.append(F.$str(fullName));
            }
          }
        }
      }
    }
    return list;
  }

  public static IAST getNamesByPrefix(String name) {

    if (name.length() == 0) {
      return F.CEmptyList;
    }
    final boolean exact;
    if (name.charAt(name.length() - 1) == '*') {
      name = name.substring(0, name.length() - 1);
      if (name.length() == 0) {
        return getAllNames();
      }
      exact = false;
    } else {
      exact = true;
    }
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    // name = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? name.toLowerCase() : name;
    name = name.toLowerCase(Locale.US);
    final String autocompleteStr = name;
    Node suggestions = suggestTree.getAutocompleteSuggestions(autocompleteStr);
    if (suggestions != null) {
      return F.mapRange(0, suggestions.listLength(), i -> {
        String identifierStr = suggestions.getSuggestion(i).getTerm();
        String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(identifierStr);
        if (str != null) {
          identifierStr = str;
        }
        if (exact) {
          if (autocompleteStr.equals(identifierStr.toLowerCase(Locale.US))) {
            return F.$s(identifierStr);
          }
          return F.NIL;
        }
        return F.$s(identifierStr);
      });
    }
    return F.CEmptyList;
  }

  public static List<String> getAutoCompletionList(String namePrefix) {
    List<String> list = new ArrayList<String>();
    if (namePrefix.length() == 0) {
      return list;
    }
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    namePrefix =
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? namePrefix.toLowerCase(Locale.US) : namePrefix;
    Node n = suggestTree.getAutocompleteSuggestions(namePrefix);
    if (n != null) {
      for (int i = 0; i < n.listLength(); i++) {
        list.add(n.getSuggestion(i).getTerm());
      }
    }
    return list;
  }

  public static IAST getAllNames() {
    return F.mapRange(0, AST2Expr.FUNCTION_STRINGS.length, i -> F.$s(AST2Expr.FUNCTION_STRINGS[i]));
  }

  private IOFunctions() {}

}
