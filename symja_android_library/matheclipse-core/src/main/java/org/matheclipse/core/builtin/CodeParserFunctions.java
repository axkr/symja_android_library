package org.matheclipse.core.builtin;

import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.CodeTokenizer;

/**
 * <code>CodeParser`</code>: reading Wolfram Language source as source.
 *
 * <p>
 * These answer with a description of the text rather than with what it computes, and every token
 * carries the extent of the source it covers. That is what lets a caller cut a file up and put it
 * back together - a template engine splitting a <code>.wlx</code> file at the newlines between its
 * top-level expressions, or an editor pointing at where a syntax error is.
 *
 * <p>
 * A subset: the tokens tile the source exactly and the bracket nesting is right, which is what those
 * uses need. The token kinds are coarser than the Wolfram Language's own - every run of operator
 * characters is <code>Token`Operator</code> rather than a name of its own - and
 * <code>CodeParse</code> answers the shape of the expression without positions.
 */
public class CodeParserFunctions {

  /** The context these symbols are written in, as in <code>CodeParser`LeafNode</code>. */
  public static final String CODE_PARSER_CONTEXT = "CodeParser`";

  /** The context of the token names, as in <code>Token`Newline</code>. */
  public static final String TOKEN_CONTEXT = "Token`";

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.CodeTokenize.setEvaluator(new CodeTokenize());
        S.CodeConcreteParse.setEvaluator(new CodeConcreteParse());
        S.CodeParse.setEvaluator(new CodeParse());
      }
    }
  }

  /**
   * Make the names of <code>CodeParser`</code> exist in this session and put the context on
   * <code>$ContextPath</code>, which is what <code>Needs["CodeParser`"]</code> amounts to here.
   */
  public static void loadContext(EvalEngine engine) {
    for (String name : new String[] {"LeafNode", "CallNode", "GroupNode", "ContainerNode",
        "ErrorNode", "SourceConvention", "CodeTokenize", "CodeConcreteParse", "CodeParse"}) {
      symbol(name, CODE_PARSER_CONTEXT, engine);
    }
    // the three built-ins answer under their qualified names too
    assignAlias("CodeTokenize", S.CodeTokenize, engine);
    assignAlias("CodeConcreteParse", S.CodeConcreteParse, engine);
    assignAlias("CodeParse", S.CodeParse, engine);
  }

  private static void assignAlias(String name, ISymbol builtin, EvalEngine engine) {
    ISymbol qualified = symbol(name, CODE_PARSER_CONTEXT, engine);
    if (qualified != builtin && !qualified.hasAssignedSymbolValue()) {
      qualified.assignValue(builtin, false);
    }
  }

  private static ISymbol symbol(String name, String context, EvalEngine engine) {
    return F.symbol(name, context, null, engine);
  }

  /** <code>CodeTokenize[str]</code>: every token of the source, in order. */
  private static class CodeTokenize extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      boolean characterIndex = isCharacterIndex(ast, engine);
      String source = ast.arg1().toString();
      List<CodeTokenizer.Token> tokens = CodeTokenizer.tokenize(source);
      IASTAppendable result = F.ListAlloc(tokens.size());
      for (CodeTokenizer.Token token : tokens) {
        result.append(leafNode(token, source, characterIndex, engine));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>CodeConcreteParse[str]</code>: the tokens, with everything between a pair of brackets
   * gathered into a <code>GroupNode</code>.
   *
   * <p>
   * The nesting is the point. A newline written between two top-level expressions is a child of the
   * container, and one written inside a bracket is not, so a caller can tell them apart by looking
   * at the children of the container alone.
   */
  private static class CodeConcreteParse extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      boolean characterIndex = isCharacterIndex(ast, engine);
      String source = ast.arg1().toString();
      List<CodeTokenizer.Token> tokens = CodeTokenizer.tokenize(source);

      java.util.Deque<IASTAppendable> stack = new java.util.ArrayDeque<IASTAppendable>();
      java.util.Deque<CodeTokenizer.Token> openTokens =
          new java.util.ArrayDeque<CodeTokenizer.Token>();
      IASTAppendable children = F.ListAlloc(tokens.size());
      for (CodeTokenizer.Token token : tokens) {
        IExpr leaf = leafNode(token, source, characterIndex, engine);
        if (token.isOpen()) {
          IASTAppendable group = F.ListAlloc(8);
          group.append(leaf);
          stack.push(children);
          openTokens.push(token);
          children = group;
          continue;
        }
        if (token.isClose() && !stack.isEmpty()) {
          children.append(leaf);
          CodeTokenizer.Token open = openTokens.pop();
          IExpr group = F.ternaryAST3(//
              symbol("GroupNode", CODE_PARSER_CONTEXT, engine), //
              symbol(groupName(open.kind()), CODE_PARSER_CONTEXT, engine), //
              children, //
              metadata(open.start(), token.end(), source, characterIndex, engine));
          children = stack.pop();
          children.append(group);
          continue;
        }
        children.append(leaf);
      }
      // a bracket that was never closed leaves its contents where they are
      while (!stack.isEmpty()) {
        IASTAppendable outer = stack.pop();
        openTokens.pop();
        for (int i = 1; i < children.size(); i++) {
          outer.append(children.get(i));
        }
        children = outer;
      }

      return F.ternaryAST3(//
          symbol("ContainerNode", CODE_PARSER_CONTEXT, engine), //
          S.String, //
          children, //
          F.assoc());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>CodeParse[str]</code>: the shape of the expression, as
   * <code>CallNode</code>s and <code>LeafNode</code>s.
   *
   * <p>
   * Built from the parsed expression rather than from the tokens, so it says what the source means;
   * it carries no positions, which is the part callers ask <code>CodeConcreteParse</code> for.
   */
  private static class CodeParse extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String source = ast.arg1().toString();
      IExpr parsed;
      try {
        parsed = engine.parse(source);
      } catch (org.matheclipse.parser.client.SyntaxError se) {
        return F.ternaryAST3(//
            symbol("ContainerNode", CODE_PARSER_CONTEXT, engine), //
            S.String, //
            F.list(F.ternaryAST3(symbol("ErrorNode", CODE_PARSER_CONTEXT, engine),
                symbol("Error", TOKEN_CONTEXT, engine), F.stringx(se.getMessage()), F.assoc())), //
            F.assoc());
      }
      return F.ternaryAST3(//
          symbol("ContainerNode", CODE_PARSER_CONTEXT, engine), //
          S.String, //
          F.list(node(parsed, engine)), //
          F.assoc());
    }

    /** One expression as a node: a call for a compound expression, a leaf for anything else. */
    private static IExpr node(IExpr expr, EvalEngine engine) {
      if (expr.isAST()) {
        IAST call = (IAST) expr;
        IASTAppendable arguments = F.ListAlloc(call.argSize());
        for (int i = 1; i < call.size(); i++) {
          arguments.append(node(call.get(i), engine));
        }
        return F.ternaryAST3(symbol("CallNode", CODE_PARSER_CONTEXT, engine), //
            node(call.head(), engine), arguments, F.assoc());
      }
      String kind = expr.isString() ? "String"
          : expr.isInteger() ? "Integer" : expr.isNumber() ? "Real" : "Symbol";
      IExpr text = expr.isString() ? expr : F.stringx(expr.toString());
      return F.ternaryAST3(symbol("LeafNode", CODE_PARSER_CONTEXT, engine), //
          symbol(kind, TOKEN_CONTEXT, engine), text, F.assoc());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /** <code>LeafNode[Token`Kind, "text", &lt;|Source -&gt; …|&gt;]</code> for one token. */
  private static IExpr leafNode(CodeTokenizer.Token token, String source, boolean characterIndex,
      EvalEngine engine) {
    return F.ternaryAST3(//
        symbol("LeafNode", CODE_PARSER_CONTEXT, engine), //
        symbol(token.kind(), TOKEN_CONTEXT, engine), //
        F.stringx(token.text()), //
        metadata(token.start(), token.end(), source, characterIndex, engine));
  }

  /**
   * The <code>&lt;|Source -&gt; …|&gt;</code> of a node: a pair of character indices under
   * <code>SourceConvention -&gt; "SourceCharacterIndex"</code>, and a pair of line and column
   * positions otherwise, which is what the Wolfram Language answers by default.
   */
  private static IExpr metadata(int start, int end, String source, boolean characterIndex,
      EvalEngine engine) {
    IExpr sourceValue;
    if (characterIndex) {
      sourceValue = F.list(F.ZZ(start), F.ZZ(end));
    } else {
      sourceValue = F.list(lineColumn(source, start), lineColumn(source, end));
    }
    return F.assoc(F.list(F.Rule(S.Source, sourceValue)));
  }

  /** The line and column of a one-based character index. */
  private static IExpr lineColumn(String source, int position) {
    int line = 1;
    int column = 1;
    for (int i = 0; i < position - 1 && i < source.length(); i++) {
      if (source.charAt(i) == '\n') {
        line++;
        column = 1;
      } else {
        column++;
      }
    }
    return F.list(F.ZZ(line), F.ZZ(column));
  }

  /** The name a group of this bracket has, as <code>GroupSquare</code> for <code>[</code>. */
  private static String groupName(String openKind) {
    switch (openKind) {
      case "OpenSquare":
        return "GroupSquare";
      case "OpenCurly":
        return "GroupCurly";
      case "LessBar":
        return "GroupAssociation";
      default:
        return "GroupParen";
    }
  }

  /**
   * Was <code>SourceConvention -&gt; "SourceCharacterIndex"</code> asked for?
   *
   * <p>
   * The option is matched by name, because a caller writes it as
   * <code>CodeParser`SourceConvention</code> and the symbol that names it is not the system's.
   */
  private static boolean isCharacterIndex(IAST ast, EvalEngine engine) {
    for (int i = 2; i < ast.size(); i++) {
      IExpr argument = engine.evaluate(ast.get(i));
      IAST rules = argument.isList() ? (IAST) argument : F.list(argument);
      for (int j = 1; j < rules.size(); j++) {
        IExpr rule = rules.get(j);
        if (rule.isRuleAST() && rule.first().toString().equalsIgnoreCase("SourceConvention")) {
          // in relaxed syntax the name written in the source has been lower-cased by now
          return rule.second().toString().equalsIgnoreCase("SourceCharacterIndex");
        }
      }
    }
    return false;
  }

  public static void initialize() {
    Initializer.init();
  }

  private CodeParserFunctions() {}
}
