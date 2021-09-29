package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.interfaces.ISetValueEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;

public final class PatternMatching {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Default.setEvaluator(new Default());
      S.Evaluate.setEvaluator(new Evaluate());
      S.FilterRules.setEvaluator(new FilterRules());
      S.Hold.setEvaluator(new Hold());
      S.HoldComplete.setEvaluator(new HoldComplete());
      S.HoldPattern.setEvaluator(new HoldPattern());
      S.Identity.setEvaluator(new Identity());
      S.Information.setEvaluator(new Information());
      S.Literal.setEvaluator(new Literal());
      S.MessageName.setEvaluator(new MessageName());
      S.Optional.setEvaluator(Optional.CONST);
      S.Options.setEvaluator(new Options());
      S.OptionValue.setEvaluator(new OptionValue());
      S.ReleaseHold.setEvaluator(new ReleaseHold());
      S.Rule.setEvaluator(new Rule());
      S.RuleDelayed.setEvaluator(new RuleDelayed());
      // if (!Config.FUZZY_PARSER) {
      S.Set.setEvaluator(new Set());
      S.SetDelayed.setEvaluator(new SetDelayed());
      // }
      S.SetSystemOptions.setEvaluator(new SetSystemOptions());
      S.SystemOptions.setEvaluator(new SystemOptions());
      S.Unique.setEvaluator(new Unique());
      if (!Config.FUZZY_PARSER) {
        S.Blank.setEvaluator(Blank.CONST);
        S.BlankSequence.setEvaluator(BlankSequence.CONST);
        S.BlankNullSequence.setEvaluator(BlankNullSequence.CONST);
        S.DownValues.setEvaluator(new DownValues());
        S.Pattern.setEvaluator(Pattern.CONST);
        S.Clear.setEvaluator(new Clear());
        S.ClearAll.setEvaluator(new ClearAll());
        S.Context.setEvaluator(new Context());
        S.Definition.setEvaluator(new Definition());
        S.OptionsPattern.setEvaluator(OptionsPattern.CONST);
        S.OwnValues.setEvaluator(new OwnValues());
        S.Repeated.setEvaluator(Repeated.CONST);
        S.RepeatedNull.setEvaluator(RepeatedNull.CONST);
        S.TagSet.setEvaluator(new TagSet());
        S.TagSetDelayed.setEvaluator(new TagSetDelayed());
        S.Unset.setEvaluator(new Unset());
        S.UpSet.setEvaluator(new UpSet());
        S.UpSetDelayed.setEvaluator(new UpSetDelayed());
        S.UpValues.setEvaluator(new UpValues());
      }
    }
  }

  public static final class Blank extends AbstractCoreFunctionEvaluator {
    public static final Blank CONST = new Blank();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Blank)) {
        if (ast.isAST0()) {
          return F.$b();
        }
        if (ast.isAST1()) {
          return F.$b(ast.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static final class BlankSequence extends AbstractCoreFunctionEvaluator {
    public static final BlankSequence CONST = new BlankSequence();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.BlankSequence)) {
        if (ast.isAST0()) {
          return F.$ps((ISymbol) null);
        }
        if (ast.isAST1() && ast.arg1().isSymbol()) {
          return F.$ps((ISymbol) ast.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static final class BlankNullSequence extends AbstractCoreFunctionEvaluator {
    public static final BlankNullSequence CONST = new BlankNullSequence();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.BlankNullSequence)) {
        if (ast.isAST0()) {
          return F.$ps((ISymbol) null, true);
        }
        if (ast.isAST1() && ast.arg1().isSymbol()) {
          return F.$ps((ISymbol) ast.arg1(), true);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Clear(symbol1, symbol2,...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>clears all values of the given symbols.
   *
   * </blockquote>
   *
   * <p><code>Clear</code> does not remove attributes, options, and default values associated with
   * the symbols. Use <code>ClearAll</code> to do so.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a=2
   * 2
   *
   * &gt;&gt; Definition(a)
   * {a=2}
   *
   * &gt;&gt; Clear(a)
   * &gt;&gt; a
   * a
   * </pre>
   */
  private static final class Clear extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable mutable = ast.copyAST();
      for (int i = 1; i < ast.size(); i++) {
        IExpr x = Validate.checkIdentifierHoldPattern(ast.get(i), ast, engine);
        if (!x.isPresent()) {
          return F.NIL;
        }
        if (((ISymbol) x).isProtected()) {
          // Symbol `1` is Protected.
          IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(x), engine);
          return S.Null;
        }
        mutable.set(i, x);
      }
      Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clear(engine));
      return S.Null;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * ClearAll(symbol1, symbol2,...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>clears all values and attributes associated with the given symbols.
   *
   * </blockquote>
   */
  private static final class ClearAll extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable mutable = ast.copyAST();
      for (int i = 1; i < ast.size(); i++) {
        IExpr x = Validate.checkIdentifierHoldPattern(ast.get(i), ast, engine);
        if (!x.isPresent()) {
          return F.NIL;
        }
        if (((ISymbol) x).isProtected()) {
          // Symbol `1` is Protected.
          IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(x), engine);
          return S.Null;
        }
        mutable.set(i, x);
      }

      Lambda.forEach(mutable, x -> x.isSymbol(), x -> ((ISymbol) x).clearAll(engine));
      return S.Null;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Context(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>return the context of the given symbol.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Context()
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>return the current context.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; $ContextPath
   * {System`,Global`}
   *
   * &gt;&gt; Context(a)
   * Global`
   *
   * &gt;&gt; Context(Sin)
   * System`
   * </code>
   * </pre>
   */
  private static final class Context extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr x = Validate.checkIdentifierHoldPattern(ast.arg1(), ast, engine);
        if (!x.isPresent()) {
          return F.NIL;
        }
        return F.stringx(((ISymbol) x).getContext().getContextName());
      }
      if (ast.isAST0()) {
        return F.stringx(EvalEngine.get().getContext().completeContextName());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * Get the default value for a symbol (i.e. <code>1</code> is the default value for <code>Times
   * </code>, <code>0</code> is the default value for <code>Plus</code>).
   */
  /**
   *
   *
   * <pre>
   * <code>Default(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>Default</code> returns the default value associated with the <code>symbol</code> for a
   * pattern default <code>_.</code> expression.
   *
   * </blockquote>
   *
   * <pre>
   * <code class="language-ition)"></code>
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>Default</code> returns the default value associated with the <code>symbol</code> for a
   * pattern default <code>_.</code> expression at position <code>pos</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Default(test) = 1
   * 1
   *
   * &gt;&gt; test(x_., y_.) = {x, y}
   * {x,y}
   *
   * &gt;&gt; test(a)
   * {a,1}
   *
   * &gt;&gt; test( )
   * {1,1}
   * </code>
   * </pre>
   */
  private static final class Default extends AbstractFunctionEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        if (ast.size() > 2) {
          int pos = ast.arg2().toIntDefault();
          if (pos > 0) {
            return symbol.getDefaultValue(pos);
          }
        } else {
          return symbol.getDefaultValue();
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(
        final IExpr leftHandSide,
        IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol,
        EvalEngine engine) {
      if (leftHandSide.isAST(S.Default) && leftHandSide.size() > 1) {
        if (!leftHandSide.first().isSymbol()) {
          IOFunctions.printMessage(builtinSymbol, "setps", F.List(leftHandSide.first()), engine);
          return rightHandSide;
        }
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (symbol.isProtected()) {
          IOFunctions.printMessage(
              S.Default, "write", F.List(symbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }
        if (leftHandSide.size() == 2 && leftHandSide.first().isSymbol()) {
          symbol.setDefaultValue(rightHandSide);
          return rightHandSide;
        } else if (leftHandSide.size() == 3 && leftHandSide.first().isSymbol()) {
          int pos = leftHandSide.second().toIntDefault();
          if (pos > 1) {
            symbol.setDefaultValue(rightHandSide);
            return rightHandSide;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Definition(symbol)
   * </pre>
   *
   * <blockquote>
   *
   * <p>prints user-defined values and rules associated with <code>symbol</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Definition(ArcSinh)
   * {ArcSinh(0)=0,
   *  ArcSinh(I*1/2)=I*1/6*Pi,
   *  ArcSinh(I)=I*1/2*Pi,
   *  ArcSinh(1)=Log(1+Sqrt(2)),
   *  ArcSinh(I*1/2*Sqrt(2))=I*1/4*Pi,
   *  ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi,
   *  ArcSinh(Infinity)=Infinity,
   *  ArcSinh(I*Infinity)=Infinity,
   *  ArcSinh(ComplexInfinity)=ComplexInfinity}
   *
   * &gt;&gt; a=2
   * 2
   *
   * &gt;&gt; Definition(a)
   * {a=2}
   * </pre>
   */
  private static final class Definition extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        try {
          String definitionString;
          if (symbol.equals(S.In)) {
            IAST list = engine.getEvalHistory().definitionIn();
            definitionString = definitionToString(S.In, list);
          } else if (symbol.equals(S.Out)) {
            IAST list = engine.getEvalHistory().definitionOut();
            definitionString = definitionToString(S.Out, list);
          } else {
            definitionString = symbol.definitionToString();
          }
          return F.stringx(definitionString);
        } catch (IOException e) {
          LOGGER.error("Definition.evaluate()", e);
        }
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }

    public static String definitionToString(ISymbol symbol, IAST list) {

      StringWriter buf = new StringWriter();
      IAST attributesList = AttributeFunctions.attributesList(symbol);
      if (attributesList.size() > 1) {
        buf.append("Attributes(");
        buf.append(symbol.toString());
        buf.append(")=");
        buf.append(attributesList.toString());
        buf.append("\n");
      }

      EvalEngine engine = EvalEngine.get();
      OutputFormFactory off = OutputFormFactory.get(engine.isRelaxedSyntax());
      off.setIgnoreNewLine(true);

      //    IAST list = definition();
      for (int i = 1; i < list.size(); i++) {
        if (!off.convert(buf, list.get(i))) {
          return "ERROR-IN-OUTPUTFORM";
        }
        if (i < list.size() - 1) {
          buf.append("\n");
          off.setColumnCounter(0);
        }
      }
      return buf.toString();
    }
  }

  /**
   *
   *
   * <pre>
   * <code>DownValues(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>prints the down-value rules associated with <code>symbol</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; f(1)=3
   * 3
   *
   * &gt;&gt; f(x_):=x^3
   *
   * &gt;&gt; DownValues(f)
   * {HoldPattern(f(1)):&gt;3,HoldPattern(f(x_)):&gt;x^3}
   * </code>
   * </pre>
   */
  private static final class DownValues extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        RulesData rulesData = symbol.getRulesData();
        if (rulesData == null) {
          return F.CEmptyList;
        }
        return rulesData.downValues();
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

  /**
   *
   *
   * <pre>
   * <code>Evaluate(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>the <code>Evaluate</code> function will be executed even if the function attributes <code>
   * HoldFirst, HoldRest, HoldAll</code> are set for the function head.
   *
   * </blockquote>
   */
  private static final class Evaluate extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        return engine.evaluate(ast.arg1());
      }
      IASTMutable sequence = ast.copy();
      sequence.set(0, S.Identity);
      return engine.evaluate(sequence);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>FilterRules(list-of-option-rules, list-of-rules)
   * </code>
   * </pre>
   *
   * <p>or
   *
   * <pre>
   * <code>FilterRules(list-of-option-rules, list-of-symbols)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>filter the <code>list-of-option-rules</code> by <code>list-of-rules</code>or <code>
   * list-of-symbols</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Options(f) = {a -&gt; 1, b -&gt; 2}
   * {a-&gt;1,b-&gt;2}
   *
   * &gt;&gt; FilterRules({b -&gt; 3, MaxIterations -&gt; 5}, Options(f))
   * {b-&gt;3}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p><a href="Options.md">Options</a>
   */
  private static final class FilterRules extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfRules()) {
        IAST listOfRules = (IAST) ast.arg1();
        if (ast.arg2().isList()) {
          IAST list2 = (IAST) ast.arg2();
          IASTAppendable result = F.ListAlloc(listOfRules.size());
          for (int i = 1; i < listOfRules.size(); i++) {
            IExpr listOfRulesArg = listOfRules.get(i);
            if (listOfRulesArg.isRuleAST()) {
              IAST rule = (IAST) listOfRulesArg;
              for (int j = 1; j < list2.size(); j++) {
                IExpr list2Arg = list2.get(j);
                if (list2Arg.isRuleAST()) {
                  if (rule.first().equals(list2Arg.first())) {
                    result.append(rule);
                    break;
                  }
                } else {
                  if (rule.first().equals(list2Arg)) {
                    result.append(rule);
                    break;
                  }
                }
              }
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Hold(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>Hold</code> doesn't evaluate <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Hold(3*2)
   * Hold(3*2)
   * </pre>
   */
  private static final class Hold extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // UpValues can be evaluated in Hold() but not in HoldComplete()
      return engine.evalUpRules(ast);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  private static final class HoldComplete extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   *
   *
   * <pre>
   * HoldPattern(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>HoldPattern</code> doesn't evaluate <code>expr</code> for pattern-matching.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>One might be very surprised that the following line evaluates to <code>True</code>!
   *
   * <pre>
   * &gt;&gt; MatchQ(And(x, y, z), Times(p__))
   * True
   * </pre>
   *
   * <p>When the line above is evaluated <code>Times(p__)</code> evaluates to <code>(p__)</code>
   * before the kernel checks to see if the pattern matches. <code>MatchQ</code> then determines if
   * <code>And(x,y,z)</code> matches the pattern <code>(p__)</code> and it does because <code>
   * And(x,y,z)</code> is itself a sequence of one.
   *
   * <p>Now the next line also evaluates to <code>True</code> because both <code>( And(p__) )</code>
   * and <code>( Times(p__) )</code> evaluate to <code>( p__ )</code>.
   *
   * <pre>
   * &gt;&gt; Times(p__)===And(p__)
   * True
   * </pre>
   *
   * <p>In the examples above prevent the patterns from evaluating, by wrapping them with <code>
   * HoldPattern</code> as in the following lines.
   *
   * <pre>
   * &gt;&gt; MatchQ(And(x, y, z), HoldPattern(Times(p__)))
   * False
   *
   * &gt;&gt; HoldPattern(Times(p__))===HoldPattern(And(p__))
   * False
   * </pre>
   *
   * <p>In the next lines <code>HoldPattern</code> is used to ensure the head <code>(And)</code> is
   * changed to <code>(List)</code>.<br>
   * The two examples that follow have the same effect, but the use of <code>HoldPattern</code>
   * isn't needed.
   *
   * <pre>
   * &gt;&gt; And(x, y, z)/.HoldPattern(And(a__)) -&gt;List(a)
   * {x,y,z}
   *
   * &gt;&gt; And(x, y, z)/.And-&gt;List
   * {x,y,z}
   *
   * &gt;&gt; And(x, y, z)/.And(a_,b___)-&gt;List(a,b)
   * {x,y,z}
   * </pre>
   */
  private static class HoldPattern extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        IExpr arg1 = ast.arg1();
        if (arg1.isAST()) {
          IExpr temp = engine.evalHoldPattern((IAST) arg1, true, false);
          if (temp == arg1) {
            return F.NIL;
          }
          return F.HoldPattern(temp);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /** @deprecated use {@link HoldPattern} */
  @Deprecated
  private static final class Literal extends HoldPattern {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        IExpr arg1 = ast.arg1();
        if (arg1.isAST()) {
          IExpr temp = engine.evalHoldPattern((IAST) arg1, true, false);
          if (temp == arg1) {
            return F.NIL;
          }
          return F.Literal(temp);
        }
      }
      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Identity(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Identity_function">Wikipedia - Identity
   *       function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Identity(5)
   * 5
   * </code>
   * </pre>
   */
  private static final class Identity extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return ast.arg1();
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

  private static final class Information extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2 || ast.size() == 3) {
        try {
          boolean longForm = true;
          if (ast.size() == 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
            if (options.isFalse(S.LongForm)) {
              longForm = false;
            }
          }

          ISymbol symbol = null;
          if (!ast.arg1().isSymbol()) {
            IExpr arg1 = engine.evaluate(ast.arg1());
            if (arg1.isEmptyList()) {
              return arg1;
            }
            if (!arg1.isSymbol()) {
              LOGGER.log(engine.getLogLevel(), "{}: symbol expected at position 1 instead of {}",
                  ast.topHead(), arg1);
              return F.NIL;
            }
            symbol = (ISymbol) arg1;
          } else {
            symbol = (ISymbol) ast.arg1();
          }
          final PrintStream stream = engine.getOutPrintStream();

          // Set[MessageName(f,"usage"),"text")
          IExpr temp = symbol.evalMessage("usage");
          if (temp.isPresent()) {
            stream.println(temp.toString());
          }
          if (longForm) {
            try {
              Documentation.printDocumentation(stream, symbol.getSymbolName());

              IAST function = F.Attributes(symbol);
              temp = engine.evaluate(F.Attributes(symbol));
              if (temp.isPresent()) {
                stream.println(function.toString() + " = " + temp.toString());
              }

              stream.println(symbol.definitionToString());
            } catch (IOException ioe) {
              LOGGER.debug("Information.evaluate() failed", ioe);
            }
          }
          return S.Null;
        } catch (RuntimeException rex) {
          LOGGER.debug("Information.evaluate() failed", rex);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /** MessageName[{&lt;file name&gt;}} */
  private static final class MessageName extends AbstractFunctionEvaluator
      implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // Here we only validate the arguments
      // The assignment of the message is handled in the Set() function
      if (!ast.arg1().isSymbol()) {
        LOGGER.log(engine.getLogLevel(), "{}: symbol expected at position 1 instead of {}",
            ast.topHead(), ast.arg1());
        return F.NIL;
      }
      if (ast.arg2().isString()) {
        return F.NIL;
      }
      IExpr arg2 = engine.evaluateNIL(ast.arg2());
      if (arg2.isString()) {
        return F.MessageName(ast.arg1(), arg2);
      }
      if (arg2.isPresent()) {
        String str = Validate.checkMessageNameTag(arg2, ast, engine);
        if (str != null) {
          return F.MessageName(ast.arg1(), F.$str(str));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(
        final IExpr leftHandSide,
        IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol,
        EvalEngine engine) {
      if (leftHandSide.isAST(S.MessageName, 3, 4) && leftHandSide.first().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (!symbol.isProtected()) {
          String messageName = leftHandSide.second().toString();
          rightHandSide = engine.evaluate(rightHandSide);
          IStringX message;
          if (rightHandSide instanceof IStringX) {
            message = (IStringX) rightHandSide;
          } else {
            message = F.stringx(rightHandSide.toString());
          }
          symbol.putMessage(IPatternMatcher.SET, messageName, message);
          if (builtinSymbol.equals(S.Set)) {
            return message;
          }
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Optional(patt, default)
   * </pre>
   *
   * <p>or
   *
   * <pre>
   * patt : default
   * </pre>
   *
   * <blockquote>
   *
   * <p>is a pattern which matches <code>patt</code>, which if omitted should be replaced by <code>
   * default</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; f(x_, y_:1) := {x, y}
   * &gt;&gt; f(1, 2)
   * {1,2}
   *
   * &gt;&gt; f(a)
   * {a,1}
   * </pre>
   */
  public static class Optional extends AbstractCoreFunctionEvaluator {
    public static final Optional CONST = new Optional();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Optional)) {
        // convert only special forms of _. or x_.
        if (ast.size() == 2) {
          IExpr arg1 = engine.evaluate(ast.arg1());
          if (arg1.isBlank()) {
            IPattern patt = (IPattern) arg1;
            if (patt.getHeadTest() == null) {
              return F.$b(patt.getHeadTest(), true);
            }
          }
          if (arg1.isPattern()) {
            IPattern patt = (IPattern) arg1;
            if (patt.getHeadTest() == null) {
              return F.$p(patt.getSymbol(), patt.getHeadTest(), true);
            }
          }
        }
        // if (ast.size() == 3) {
        // // convert only special forms of _:v or x_:v.
        // IExpr arg1 = engine.evaluate(ast.arg1());
        // IExpr arg2 = engine.evaluate(ast.arg2());
        // if (arg1.isBlank()) {
        // IPattern patt = (IPattern) arg1;
        // if (patt.getCondition() == null) {
        // return F.$b(patt.getCondition(), arg2);
        // }
        // }
        // if (arg1.isPattern()) {
        // IPattern patt = (IPattern) arg1;
        // if (patt.getCondition() == null) {
        // return F.$p(patt.getSymbol(), patt.getCondition(), arg2);
        // }
        // }
        // }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class Options extends AbstractFunctionEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.arg1().isSymbol()) {
        ISymbol arg1 = (ISymbol) ast.arg1();
        return optionsList(arg1, false);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(
        final IExpr leftHandSide,
        IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol,
        EvalEngine engine) {
      if (leftHandSide.isAST(S.Options, 2) && leftHandSide.first().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (!symbol.isProtected()) {
          try {
            if (!builtinSymbol.equals(S.SetDelayed)) {
              rightHandSide = engine.evaluate(rightHandSide);
            }
          } catch (final ReturnException e) {
            rightHandSide = e.getValue();
          }
          symbol.putDownRule(
              IPatternMatcher.SET, true, leftHandSide, rightHandSide, engine.isPackageMode());
          if (builtinSymbol.equals(S.Set)) {
            return rightHandSide;
          }
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.addAttributes(ISymbol.HOLDALL);
    }
  }

  public static class OptionValue extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.head().equals(S.OptionValue)) {
        return optionValueReplace(ast, false, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>OwnValues(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>prints the own-value rule associated with <code>symbol</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; a=42
   * 42
   *
   * &gt;&gt; OwnValues(a)
   * {HoldPattern(a):&gt;42}
   * </code>
   * </pre>
   */
  private static final class OwnValues extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        IExpr value = symbol.assignedValue();
        if (value == null) {
          return F.CEmptyList;
        }
        return F.List(F.RuleDelayed(F.HoldPattern(symbol), value));
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

  public static final class OptionsPattern extends AbstractCoreFunctionEvaluator {
    public static final OptionsPattern CONST = new OptionsPattern();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.OptionsPattern)) {
        if (ast.isAST0()) {
          return F.$OptionsPattern();
        }
        if (ast.isAST1()) {
          return F.$OptionsPattern(null, ast.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static final class Pattern extends AbstractCoreFunctionEvaluator {
    public static final Pattern CONST = new Pattern();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Pattern)) {
        if (ast.size() == 3) {
          if (ast.arg1().isSymbol()) {
            final ISymbol symbol = (ISymbol) ast.arg1();

            IExpr arg2 = ast.arg2();
            if (arg2.isBlank()) {
              return F.$p(symbol, ((IPattern) arg2).getHeadTest());
            }
            if (arg2.isAST()) {

              if (arg2.size() == 1) {
                if (arg2.isAST(S.Blank)) {
                  return F.$p(symbol);
                }
                if (arg2.isAST(S.BlankSequence)) {
                  return F.$ps(symbol, null, false, false);
                }
                if (arg2.isAST(S.BlankNullSequence)) {
                  return F.$ps(symbol, null, false, true);
                }
                if (arg2.isAST(S.OptionsPattern)) {
                  return F.$OptionsPattern(symbol);
                }
              } else if (arg2.size() == 2) {
                IExpr first = arg2.first();
                if (first.isAST()) {
                  first = engine.evalHoldPattern((IAST) first);
                }
                if (arg2.isAST(S.Blank)) {
                  return F.$p(symbol, first);
                }
                if (arg2.isAST(S.BlankSequence)) {
                  return F.$ps(symbol, first, false, false);
                }
                if (arg2.isAST(S.BlankNullSequence)) {
                  return F.$ps(symbol, first, false, true);
                }
                if (arg2.isAST(S.OptionsPattern)) {
                  return F.$OptionsPattern(symbol, first);
                }
              }

              arg2 = engine.evalHoldPattern((IAST) arg2);
            }
            return PatternNested.valueOf(symbol, arg2);
          } else {
            // First element in `1` is not a valid pattern name.
            return IOFunctions.printMessage(ast.topHead(), "patvar", F.List(ast), engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class ReleaseHold extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        return arg1.replaceAll(ReleaseHold::releaseHold);
      }
      return arg1;
    }

    private static IExpr releaseHold(IExpr expr) {
      IASTMutable result = F.NIL;
      if (expr.isAST()) {
        if (expr.isAST(S.Hold, 2)
            || expr.isAST(S.HoldForm, 2)
            || expr.isAST(S.HoldComplete, 2)
            || expr.isAST(S.HoldPattern, 2)) {
          return expr.first();
        }

        IAST list = (IAST) expr;
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isAST()) {
            IExpr temp = arg.replaceAll(ReleaseHold::releaseHold);
            if (temp.isPresent()) {
              if (!result.isPresent()) {
                result = list.setAtCopy(i, arg.first());
              } else {
                result.set(i, arg.first());
              }
            }
          }
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static class Repeated extends AbstractCoreFunctionEvaluator {
    public static final Repeated CONST = new Repeated();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Repeated)) {
        IExpr arg1 = ast.arg1();
        if (ast.isAST1()) {
          return F.$Repeated(arg1, 1, Integer.MAX_VALUE, engine);
        }
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          return repeatedLimit(arg1, arg2, 1, engine);
        }
      }
      return F.NIL;
    }

    protected static IExpr repeatedLimit(
        IExpr arg1, IExpr arg2, int defaultMin, EvalEngine engine) {
      if (arg2.isList1()) {
        IExpr first = arg2.first();
        int min;
        if (first.isNegativeInfinity()) {
          min = Integer.MIN_VALUE + 1;
        } else if (first.isInfinity()) {
          min = Integer.MAX_VALUE;
        } else {
          min = first.toIntDefault();
        }
        return F.$Repeated(arg1, min, min, engine);
      } else if (arg2.isList2()) {
        IExpr first = arg2.first();
        IExpr second = arg2.second();
        int min;
        if (first.isNegativeInfinity()) {
          min = Integer.MIN_VALUE + 1;
        } else if (first.isInfinity()) {
          min = Integer.MAX_VALUE;
        } else {
          min = first.toIntDefault();
        }
        int max;
        if (second.isNegativeInfinity()) {
          max = Integer.MIN_VALUE + 1;
        } else if (second.isInfinity()) {
          max = Integer.MAX_VALUE;
        } else {
          max = second.toIntDefault();
        }
        if (min == Integer.MIN_VALUE || max == Integer.MIN_VALUE) {
          return F.NIL;
        }
        return F.$Repeated(arg1, min, max, engine);
      } else {
        int max;
        if (arg2.isNegativeInfinity()) {
          max = Integer.MIN_VALUE + 1;
        } else if (arg2.isInfinity()) {
          max = Integer.MAX_VALUE;
        } else {
          max = arg2.toIntDefault();
        }
        if (max == Integer.MIN_VALUE) {
          return F.NIL;
        }
        return F.$Repeated(arg1, defaultMin, max, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static final class RepeatedNull extends Repeated {
    public static final RepeatedNull CONST = new RepeatedNull();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.RepeatedNull)) {
        IExpr arg1 = ast.arg1();
        if (ast.isAST1()) {
          return F.$Repeated(arg1, 0, Integer.MAX_VALUE, engine);
        }
        if (ast.isAST2()) {
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            return repeatedLimit(arg1, arg2, 0, engine);
          }
        }
      }
      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * Rule(x, y)
   *
   * x -&gt; y
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents a rule replacing <code>x</code> with <code>y</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a+b+c /. c-&gt;d
   * a+b+d
   *
   * &gt;&gt; {x,x^2,y} /. x-&gt;3
   * {3,9,y}
   * </pre>
   *
   * <p>Rule called with 3 arguments; 2 arguments are expected.
   *
   * <pre>
   * &gt;&gt; a /. Rule(1, 2, 3) -&gt; t
   * a
   * </pre>
   */
  private static final class Rule extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr leftHandSide = ast.arg1();
      // if (leftHandSide.isAST()) {
      // leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
      // } else {
      leftHandSide = engine.evaluate(leftHandSide);
      // }
      IExpr arg2 = engine.evaluateNIL(ast.arg2());
      if (!arg2.isPresent()) {
        if (leftHandSide.equals(ast.arg1())) {
          return F.NIL;
        }
        return Rule(leftHandSide, ast.arg2());
      }
      return Rule(leftHandSide, arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.SEQUENCEHOLD);
    }
  }

  /**
   *
   *
   * <pre>
   * RuleDelayed(x, y)
   *
   * x :&gt; y
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents a rule replacing <code>x</code> with <code>y</code>, with <code>y</code> held
   * unevaluated.
   *
   * </blockquote>
   */
  private static final class RuleDelayed extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr leftHandSide = ast.arg1();
      // if (leftHandSide.isAST()) {
      // leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
      // } else {
      leftHandSide = engine.evaluate(leftHandSide);
      // }
      if (!leftHandSide.equals(ast.arg1())) {
        return RuleDelayed(leftHandSide, ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST | ISymbol.SEQUENCEHOLD);
    }
  }

  /**
   *
   *
   * <pre>
   * Set(expr, value)
   *
   * expr = value
   * </pre>
   *
   * <blockquote>
   *
   * <p>evaluates <code>value</code> and assigns it to <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * {s1, s2, s3} = {v1, v2, v3}
   * </pre>
   *
   * <blockquote>
   *
   * <p>sets multiple symbols <code>(s1, s2, ...)</code> to the corresponding values <code>
   * (v1, v2, ...)</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p><code>Set</code> can be used to give a symbol a value:<br>
   *
   * <pre>
   * &gt;&gt; a = 3
   * 3
   *
   * &gt;&gt; a
   * 3
   * </pre>
   *
   * <p>You can set multiple values at once using lists:<br>
   *
   * <pre>
   * &gt;&gt; {a, b, c} = {10, 2, 3}
   * {10,2,3}
   *
   * &gt;&gt; {a, b, {c, {d}}} = {1, 2, {{c1, c2}, {a}}}
   * {1,2,{{c1,c2},{10}}}
   *
   * &gt;&gt; d
   * 10
   * </pre>
   *
   * <p><code>Set</code> evaluates its right-hand side immediately and assigns it to the left-hand
   * side:<br>
   *
   * <pre>
   * &gt;&gt; a
   * 1
   *
   * &gt;&gt; x = a
   * 1
   *
   * &gt;&gt; a = 2
   * 2
   *
   * &gt;&gt; x
   * 1
   * </pre>
   *
   * <p>'Set' always returns the right-hand side, which you can again use in an assignment:<br>
   *
   * <pre>
   * &gt;&gt; a = b = c = 2
   * &gt;&gt; a == b == c == 2
   * True
   * </pre>
   *
   * <p>'Set' supports assignments to parts:<br>
   *
   * <pre>
   * &gt;&gt; A = {{1, 2}, {3, 4}}
   * &gt;&gt; A[[1, 2]] = 5
   * 5
   *
   * &gt;&gt; A
   * {{1,5}, {3,4}}
   *
   * &gt;&gt; A[[;;, 2]] = {6, 7}
   * {6,7}
   *
   * &gt;&gt; A
   * {{1,6},{3,7}}
   * </pre>
   *
   * <p>Set a submatrix:
   *
   * <pre>
   * &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
   * &gt;&gt; B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}
   * &gt;&gt; B
   * {{1, t, u}, {4, y, z}, {7, 8, 9}}
   * </pre>
   */
  private static final class Set extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      IExpr head = engine.evaluate(leftHandSide.head());
      if (head.topHead().equals(S.Association)) {
        head = S.Association;
      }
      IExpr rightHandSide = ast.arg2();
      try {
        rightHandSide = engine.evaluate(rightHandSide);
      } catch (final ConditionException e) {
      } catch (final ReturnException e) {
        rightHandSide = e.getValue();
      }

      try {
        if (leftHandSide.isAST()) {
          if (head.isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) head;
            IEvaluator eval = symbol.getEvaluator();
            if (eval instanceof ISetEvaluator) {
              return ((ISetEvaluator) eval).evaluateSet(leftHandSide, rightHandSide, S.Set, engine);
            }
          }
        } else if (leftHandSide.isBuiltInSymbol()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) leftHandSide;
          IEvaluator eval = symbol.getEvaluator();
          if (eval instanceof ISetValueEvaluator) {
            return ((ISetValueEvaluator) eval).evaluateSet(rightHandSide, false, engine);
          }
        }
        return createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);
      } catch (RuleCreationError rce) {
        // Cannot unset object `1`.
        IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
        return rightHandSide;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static IExpr createPatternMatcher(
        IExpr leftHandSide, IExpr rightHandSide, boolean packageMode, final EvalEngine engine)
        throws RuleCreationError {
      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);
      return setDownRule(leftHandSide, flags[0], rightHandSide, packageMode);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST | ISymbol.SEQUENCEHOLD);
    }
  }

  /**
   *
   *
   * <pre>
   * SetDelayed(expr, value)
   *
   * expr := value
   * </pre>
   *
   * <blockquote>
   *
   * <p>assigns <code>value</code> to <code>expr</code>, without evaluating <code>value</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p><code>SetDelayed</code> is like <code>Set</code>, except it has attribute <code>HoldAll
   * </code>, thus it does not evaluate the right-hand side immediately, but evaluates it when
   * needed.<br>
   *
   * <pre>
   * &gt;&gt; Attributes(SetDelayed)
   * {HoldAll}
   *
   * &gt;&gt; a = 1
   * 1
   *
   * &gt;&gt; x := a
   * &gt;&gt; x
   * 1
   * </pre>
   *
   * <p>Changing the value of <code>a</code> affects <code>x</code>:<br>
   *
   * <pre>
   * &gt;&gt; a = 2
   * 2
   *
   * &gt;&gt; x
   * 2
   * </pre>
   *
   * <p><code>Condition</code> (<code>/;</code>) can be used with <code>SetDelayed</code> to make an
   * assignment that only holds if a condition is satisfied:<br>
   *
   * <pre>
   * &gt;&gt; f(x_) := p(x) /; x&gt;0
   * &gt;&gt; f(3)
   * p(3)
   *
   * &gt;&gt; f(-3)
   * f(-3)
   * </pre>
   */
  private static final class SetDelayed extends AbstractCoreFunctionEvaluator {

    // public final static SetDelayed CONST = new SetDelayed();

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      IExpr head = engine.evaluate(leftHandSide.head());
      if (head.isAssociation()) {
        head = S.Association;
      }
      try {
        final IExpr rightHandSide = ast.arg2();
        if (leftHandSide.isAST()) {
          if (head.isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) head;
            IEvaluator eval = symbol.getEvaluator();
            if (eval instanceof ISetEvaluator) {
              IExpr temp =
                  ((ISetEvaluator) eval)
                      .evaluateSet(leftHandSide, rightHandSide, S.SetDelayed, engine);
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
        } else if (leftHandSide.isBuiltInSymbol()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) leftHandSide;
          IEvaluator eval = symbol.getEvaluator();
          if (eval instanceof ISetValueEvaluator) {
            ((ISetValueEvaluator) eval).evaluateSet(rightHandSide, true, engine);
            return S.Null;
          }
        }
        createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);

        return S.Null;
      } catch (RuleCreationError rce) {
        // Cannot unset object `1`.
        IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static void createPatternMatcher(
        IExpr leftHandSide, IExpr rightHandSide, boolean packageMode, final EvalEngine engine)
        throws RuleCreationError {
      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);
      setDelayedDownRule(leftHandSide, flags[0], rightHandSide, packageMode);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }

  private static final class SetSystemOptions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // stub implementation
      if (ast.isAST1() && ast.arg1().isString()) {}

      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class SystemOptions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // stub implementation returning empty list
      if (ast.isAST1() && ast.arg1().isString()) {
        String str = ast.arg1().toString();
        if (str.equals("DifferentiationOptions")) {
          IAST list =
              F.List(
                  S.Hold,
                  S.HoldComplete,
                  S.Less,
                  S.LessEqual,
                  S.Greater,
                  S.GreaterEqual,
                  S.Inequality,
                  S.Unequal,
                  S.Nand,
                  S.Nor,
                  S.Xor,
                  S.Not,
                  S.Element,
                  S.Exists,
                  S.ForAll,
                  S.Implies,
                  S.Positive,
                  S.Negative,
                  S.NonPositive,
                  S.NonNegative,
                  S.Replace,
                  S.ReplaceAll,
                  S.ReplaceRepeated);
          IAST excludedFunctions = F.Rule("ExcludedFunctions", list);
          return F.List(F.Rule("DifferentiationOptions", F.List(excludedFunctions)));
        }
      }
      return F.CEmptyList;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static IExpr setDownRule(
      IExpr leftHandSide, int flags, IExpr rightHandSide, boolean packageMode) {
    // final Object[] result = new Object[] { null, rightHandSide };
    if (leftHandSide.isAST()) {
      final ISymbol lhsSymbol = determineRuleTag(leftHandSide);
      if (lhsSymbol.isProtected()) {
        // Symbol `1` is Protected.
        IOFunctions.printMessage(S.Set, "wrsym", F.List(lhsSymbol), EvalEngine.get());
        return rightHandSide;
      }
      lhsSymbol.putDownRule(IPatternMatcher.SET, false, leftHandSide, rightHandSide, packageMode);
      return rightHandSide;
    }
    if (leftHandSide.isSymbol()) {
      final ISymbol lhsSymbol = (ISymbol) leftHandSide;
      if (lhsSymbol.isProtected()) {
        // Symbol `1` is Protected.
        IOFunctions.printMessage(S.Set, "wrsym", F.List(lhsSymbol), EvalEngine.get());
        return rightHandSide;
      }
      lhsSymbol.assignValue(rightHandSide, false);
      return rightHandSide;
    }

    throw new RuleCreationError(leftHandSide);
  }

  private static ISymbol determineRuleTag(IExpr leftHandSide) {
    while (leftHandSide.isCondition()) {
      if (leftHandSide.first().isAST()) {
        leftHandSide = leftHandSide.first();
        continue;
      }
      break;
    }
    if (leftHandSide.isSymbol()) {
      return (ISymbol) leftHandSide;
    }
    return leftHandSide.topHead();
  }

  public static IExpr setDownRule(
      int flags, IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
    // final Object[] result = new Object[] { null, rightHandSide };
    if (leftHandSide.isAST()) {
      final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
      lhsSymbol.putDownRule(IPatternMatcher.SET, false, leftHandSide, rightHandSide, packageMode);
      return rightHandSide;
    }
    if (leftHandSide.isSymbol()) {
      ((ISymbol) leftHandSide).assignValue(rightHandSide, false);
      return rightHandSide;
    }

    throw new RuleCreationError(leftHandSide);
  }

  private static void setDelayedDownRule(
      IExpr leftHandSide, int flags, IExpr rightHandSide, boolean packageMode) {
    ISymbol lhsSymbol = null;
    if (leftHandSide instanceof PatternNested) {
      PatternNested pn = (PatternNested) leftHandSide;
      IExpr pattern = pn.getPatternExpr();
      lhsSymbol = determineRuleTag(pattern);
    }

    if (leftHandSide.isAST()) {
      lhsSymbol = determineRuleTag(leftHandSide);
    }
    if (lhsSymbol != null) {
      if (lhsSymbol.isProtected()) {
        // Symbol `1` is Protected.
        IOFunctions.printMessage(S.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
        throw new FailedException();
      }
      lhsSymbol.putDownRule(
          flags | IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide, packageMode);
      return;
    }
    if (leftHandSide.isSymbol()) {
      lhsSymbol = (ISymbol) leftHandSide;
      if (lhsSymbol.isProtected()) {
        // Symbol `1` is Protected.
        IOFunctions.printMessage(S.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
        throw new FailedException();
      }
      ((ISymbol) leftHandSide).assignValue(rightHandSide, true);
      return;
    }
    throw new RuleCreationError(leftHandSide);
  }

  public static void setDelayedDownRule(
      int priority, IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
    if (leftHandSide.isAST()) {
      final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

      lhsSymbol.putDownRule(
          IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide, priority, packageMode);
      return;
    }
    if (leftHandSide.isSymbol()) {
      ((ISymbol) leftHandSide).assignValue(rightHandSide, true);
      return;
    }
    throw new RuleCreationError(leftHandSide);
  }

  private static class TagSet extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isSymbol()) {
        ISymbol symbol = (ISymbol) arg1;
        final IExpr leftHandSide = ast.arg2();
        IExpr rightHandSide = ast.arg3();
        try {
          rightHandSide = engine.evaluate(rightHandSide);
        } catch (final ConditionException e) {
        } catch (final ReturnException e) {
          rightHandSide = e.getValue();
        }
        if (symbol.isProtected()) {
          // Tag `1` in `2` is Protected.
          IOFunctions.printMessage(
              S.TagSet, "write", F.List(symbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }

        if (leftHandSide.isList()) {
          // thread over lists
          try {
            rightHandSide = engine.evaluate(rightHandSide);
          } catch (final ReturnException e) {
            rightHandSide = e.getValue();
          }
          IExpr temp =
              engine.threadASTListArgs(
                  F.TagSet(symbol, leftHandSide, rightHandSide), S.TagSet, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
        }
        try {
          Object[] result =
              createPatternMatcher(symbol, leftHandSide, rightHandSide, false, S.TagSet, engine);
          return (IExpr) result[1];
        } catch (final ValidateException ve) {
          LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
          return rightHandSide;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    /**
     * Define an <code>UpValues</code> rule for <code>
     * TagSet(tagSetSymbol, leftHandSide, rightHandSide)</code> or <code>
     * TagSetDelayed(tagSetSymbol, leftHandSide, rightHandSide)</code>.
     *
     * @param tagSetSymbol
     * @param leftHandSide
     * @param rightHandSide
     * @param packageMode
     * @param engine
     * @return
     * @throws RuleCreationError
     */
    protected static Object[] createPatternMatcher(
        ISymbol tagSetSymbol,
        IExpr leftHandSide,
        IExpr rightHandSide,
        boolean packageMode,
        IBuiltInSymbol tagSymbol,
        EvalEngine engine)
        throws RuleCreationError {
      final Object[] result = new Object[2];

      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);

      result[0] = null; // IPatternMatcher
      result[1] = rightHandSide;

      IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
      boolean found = false;
      if (lhsAST.head().equals(tagSetSymbol)) {
        found = true;
      } else {
        if (lhsAST.isCondition() && lhsAST.first().isAST()) {
          found = isTagAvailable(tagSetSymbol, (IAST) lhsAST.first());
        } else {
          found = isTagAvailable(tagSetSymbol, lhsAST);
        }
      }
      if (found) {
        result[0] =
            tagSetSymbol.putUpRule(flags[0] | IPatternMatcher.TAGSET, false, lhsAST, rightHandSide);
        return result;
      }
      // Tag `1` not found in `2`
      IOFunctions.printMessage(tagSymbol, "tagnf", F.List(tagSetSymbol, lhsAST), engine);
      return result;
    }

    private static boolean isTagAvailable(ISymbol tagSetSymbol, IAST lhsAST) {
      for (int i = 1; i < lhsAST.size(); i++) {
        IExpr arg = lhsAST.get(i);
        if (arg.equals(tagSetSymbol) || arg.topHead().equals(tagSetSymbol)) {
          return true;
        }
        if (arg instanceof IPatternObject) {
          IPatternObject pObject = (IPatternObject) arg;
          if (tagSetSymbol.equals(pObject.getHeadTest())) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }

  private static final class TagSetDelayed extends TagSet {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isSymbol()) {
        ISymbol symbol = (ISymbol) arg1;
        final IExpr leftHandSide = ast.arg2();
        final IExpr rightHandSide = ast.arg3();
        if (symbol.isProtected()) {
          // Tag `1` in `2` is Protected.
          IOFunctions.printMessage(
              ast.topHead(), "write", F.List(symbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }
        try {
          createPatternMatcher(symbol, leftHandSide, rightHandSide, false, S.TagSetDelayed, engine);
          return S.Null;
        } catch (final ValidateException ve) {
          LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Unique(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>create a unique symbol of the form <code>expr$...</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Unique(&quot;expr&quot;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>create a unique symbol of the form <code>expr...</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Unique(xy)
   * xy$1
   *
   * &gt;&gt; Unique(&quot;a&quot;)
   * a1
   * </code>
   * </pre>
   */
  private static final class Unique extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        if (ast.arg1().isSymbol()) {
          final String varAppend = ast.arg1().toString() + EvalEngine.uniqueName("$");
          return F.symbol(varAppend, engine);
        } else if (ast.arg1() instanceof IStringX) {
          // TODO start counter by 1....
          final String varAppend = EvalEngine.uniqueName(ast.arg1().toString());
          return F.symbol(varAppend, engine);
        }
      }
      return F.symbol(EvalEngine.uniqueName("$"), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Unset(expr)
   * </pre>
   *
   * <p>or
   *
   * <pre>
   * expr =.
   * </pre>
   *
   * <blockquote>
   *
   * <p>removes any definitions belonging to the left-hand-side <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 2
   * 2
   *
   * &gt;&gt; a =.
   *
   * &gt;&gt; a
   * a
   * </pre>
   *
   * <p>Unsetting an already unset or never defined variable will not change anything:
   *
   * <pre>
   * &gt;&gt; a =.
   *
   * &gt;&gt; b =.
   * </pre>
   *
   * <p><code>Unset</code> can unset particular function values. It will print a message if no
   * corresponding rule is found.
   *
   * <pre>
   * &gt;&gt; f[x_) =.
   * Assignment not found for: f(x_)
   *
   * &gt;&gt; f(x_) := x ^ 2
   *
   * &gt;&gt; f(3)
   * 9
   *
   * &gt;&gt; f(x_) =.
   *
   * &gt;&gt; f(3)
   * f(3)
   * </pre>
   */
  private static final class Unset extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      try {
        if (leftHandSide.isList()) {
          // thread over lists
          IExpr temp =
              engine.threadASTListArgs((IASTMutable) F.Unset(leftHandSide), S.Unset, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
          return F.NIL;
        }

        if (leftHandSide.isAST()) {
          final ISymbol lhsSymbol = determineRuleTag(leftHandSide);
          if (lhsSymbol.isProtected()) {
            // Symbol `1` is Protected.
            IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(lhsSymbol), EvalEngine.get());
            throw new FailedException();
          }
        }
        if (leftHandSide.isSymbol()) {
          final ISymbol lhsSymbol = (ISymbol) leftHandSide;
          if (lhsSymbol.isProtected()) {
            // Symbol `1` is Protected.
            IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(lhsSymbol), EvalEngine.get());
            throw new FailedException();
          }
        }

        removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
        return S.Null;
      } catch (RuleCreationError rce) {
        // Cannot unset object `1`.
        IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private static void removePatternMatcher(
        IExpr leftHandSide, boolean packageMode, EvalEngine engine) throws RuleCreationError {

      if (leftHandSide.isAST()) {
        leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
      }
      removeRule(leftHandSide, packageMode);
    }

    private static void removeRule(IExpr leftHandSide, boolean packageMode) {
      if (leftHandSide.isAST()) {
        final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
        if (!lhsSymbol.removeRule(IPatternMatcher.SET, false, leftHandSide, packageMode)) {
          printAssignmentNotFound(leftHandSide);
        }
        return;
      }
      if (leftHandSide.isSymbol()) {
        final ISymbol lhsSymbol = (ISymbol) leftHandSide;

        if (!lhsSymbol.removeRule(IPatternMatcher.SET, true, leftHandSide, packageMode)) {
          printAssignmentNotFound(leftHandSide);
        }
        return;
      }

      throw new RuleCreationError(leftHandSide);
    }

    private static void printAssignmentNotFound(final IExpr leftHandSide) {
      LOGGER.log(EvalEngine.get().getLogLevel(), "Assignment not found for: {}", leftHandSide);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class UpSet extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      IExpr rightHandSide = ast.arg2();
      try {
        if (leftHandSide.isList()) {
          // thread over lists
          try {
            rightHandSide = engine.evaluate(rightHandSide);
          } catch (final ReturnException e) {
            rightHandSide = e.getValue();
          }
          IExpr temp =
              engine.threadASTListArgs(
                  (IASTMutable) F.UpSet(leftHandSide, rightHandSide), S.UpSet, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
        }
        Object[] result = createPatternMatcher(leftHandSide, rightHandSide, false, engine);
        return (IExpr) result[1];
      } catch (final ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static Object[] createPatternMatcher(
        IExpr leftHandSide, IExpr rightHandSide, boolean packageMode, EvalEngine engine)
        throws RuleCreationError {
      final Object[] result = new Object[2];

      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);

      try {
        rightHandSide = engine.evaluate(rightHandSide);
      } catch (final ConditionException e) {
      } catch (final ReturnException e) {
        rightHandSide = e.getValue();
      }

      result[0] = null; // IPatternMatcher
      result[1] = rightHandSide;

      IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
      for (int i = 1; i < lhsAST.size(); i++) {
        IExpr temp = lhsAST.get(i);
        if (temp instanceof IPatternObject) {
          IExpr headTest = ((IPatternObject) temp).getHeadTest();
          if (headTest != null && headTest.isSymbol()) {
            ISymbol lhsSymbol = (ISymbol) headTest;
            result[0] =
                lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET, false, lhsAST, rightHandSide);
          }
          continue;
        }
        ISymbol lhsSymbol = null;
        if (temp.isSymbol()) {
          lhsSymbol = (ISymbol) temp;
        } else {
          lhsSymbol = lhsAST.get(i).topHead();
        }
        result[0] =
            lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET, false, lhsAST, rightHandSide);
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class UpSetDelayed extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      final IExpr rightHandSide = ast.arg2();
      try {
        createPatternMatcher(leftHandSide, rightHandSide, false, engine);

        return S.Null;
      } catch (final ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static Object[] createPatternMatcher(
        IExpr leftHandSide, IExpr rightHandSide, boolean packageMode, EvalEngine engine)
        throws RuleCreationError {
      final Object[] result = new Object[2];

      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);

      result[0] = null;
      result[1] = rightHandSide;

      IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
      for (int i = 1; i < lhsAST.size(); i++) {
        IExpr temp = lhsAST.get(i);
        if (temp instanceof IPatternObject) {
          IExpr headTest = ((IPatternObject) temp).getHeadTest();
          if (headTest != null && headTest.isSymbol()) {
            ISymbol lhsSymbol = (ISymbol) headTest;
            result[0] =
                lhsSymbol.putUpRule(
                    flags[0] | IPatternMatcher.UPSET_DELAYED, false, lhsAST, rightHandSide);
          }
          continue;
        }
        ISymbol lhsSymbol = null;
        if (temp.isSymbol()) {
          lhsSymbol = (ISymbol) temp;
        } else {
          lhsSymbol = lhsAST.get(i).topHead();
        }
        result[0] =
            lhsSymbol.putUpRule(
                flags[0] | IPatternMatcher.UPSET_DELAYED, false, lhsAST, rightHandSide);
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>UpValues(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>prints the up-value rules associated with <code>symbol</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; u /: v(x_u) := {x}
   *
   * &gt;&gt; UpValues(u)
   * {HoldPattern(v(x_u)):&gt;{x}}
   * </code>
   * </pre>
   */
  private static final class UpValues extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        RulesData rulesData = symbol.getRulesData();
        if (rulesData == null) {
          return F.CEmptyList;
        }
        return rulesData.upValues();
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

  private static IExpr evalLHS(IExpr leftHandSide, int[] flags, EvalEngine engine) {
    if (leftHandSide.isAST()
        && (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK)
            == IAST.NO_FLAG) {
      if (leftHandSide.isHoldPatternOrLiteral()) {
        flags[0] =
            leftHandSide.isAST(S.HoldPattern, 2)
                ? IPatternMatcher.HOLDPATTERN
                : IPatternMatcher.LITERAL;
        return leftHandSide.first();
      }
      return engine.evalHoldPattern((IAST) leftHandSide);
    }
    return leftHandSide;
  }

  public static void extractRules(IExpr x, IASTAppendable optionsPattern) {
    if (x != null) {
      if (x.isSequence() || x.isList()) {
        ((IAST) x).forEach(arg -> extractRules(arg, optionsPattern));
      } else if (x.isRuleAST()) {
        if (x.first().isSymbol()) {
          String name = ((ISymbol) x.first()).getSymbolName();
          optionsPattern.append(F.binaryAST2(x.topHead(), name, x.second()));
        } else {
          optionsPattern.append(x);
        }
      }
    }
  }

  /**
   * Returns a list of the default options of a symbol defined by <code>Option(f)={a->b,...}</code>.
   *
   * @param symbol
   * @param optionValueRules convert to &quot;string&quot;" rules, suitable for <code>OptionValue
   *     </code>
   * @return
   */
  public static IAST optionsList(ISymbol symbol, boolean optionValueRules) {
    RulesData rules = symbol.getRulesData();
    if (rules != null) {
      Map<IExpr, PatternMatcherEquals> map = rules.getEqualDownRules();
      PatternMatcherEquals matcher = map.get(F.Options(symbol));
      if (matcher != null) {
        IExpr temp = matcher.getRHS();
        if (optionValueRules) {
          IASTAppendable result = F.ListAlloc(10);
          extractRules(temp, result);
          return result;
        }
        if (temp.isList()) {
          return (IAST) temp;
        }
        return F.List(temp);
      }
    }
    return F.CEmptyList;
  }

  /**
   * Determine the current <code>OptionValue(...)</code> currently associated with an expreesion.
   *
   * @param ast
   * @param quiet if <code>true</code> print no message if an option value cannot be found
   * @param engine
   * @return {@link F#NIL} if an option value cannot be found; otherwise get the optional value
   */
  public static IExpr optionValueReplace(final IAST ast, boolean quiet, EvalEngine engine) {
    IASTAppendable optionsPattern = null;
    IExpr arg1 = engine.evaluate(ast.arg1());
    IExpr rhsRuleValue = F.NIL;
    IAST optionsList = null;
    if (ast.size() > 2 && arg1.isSymbol()) {
      optionsList = optionsList((ISymbol) arg1, true);
    }
    IExpr optionValue;
    if (ast.isAST3()) {
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (arg3.isList()) {
        return ((IAST) arg3).mapThread(ast, 3);
      }
      optionsPattern = F.ListAlloc(10);
      extractRules(arg2, optionsPattern);
      extractRules(optionsList, optionsPattern);
      optionValue = arg3;
      if (arg3.isSymbol()) {
        optionValue = F.$str(((ISymbol) arg3).getSymbolName());
      }
      if (optionsPattern != null) {
        rhsRuleValue = optionsRHSRuleValue(optionValue, optionsPattern);
        if (rhsRuleValue.isPresent()) {
          return rhsRuleValue;
        }
        if (!quiet) {
          // Option name `2` not found in defaults for `1`
          IOFunctions.printMessage(
              ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
        }
        return optionValue;
      }
      return F.NIL;
    } else if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        return ((IAST) arg2).mapThread(ast, 2);
      }
      optionValue = arg2;
      if (arg2.isSymbol()) {
        optionValue = F.$str(((ISymbol) arg2).getSymbolName());
      }
      if (arg1.isSymbol()) {
        Iterator<IdentityHashMap<ISymbol, IASTAppendable>> iter = engine.optionsStackIterator();
        while (iter.hasNext()) {
          IdentityHashMap<ISymbol, IASTAppendable> map = iter.next();
          if (map != null) {
            optionsPattern = map.get(arg1);
            if (optionsPattern != null) {
              rhsRuleValue = optionsRHSRuleValue(optionValue, optionsPattern);
              if (rhsRuleValue.isPresent()) {
                return rhsRuleValue;
              }
            }
          }
        }
      } else {
        if (arg1.isAST()) {
          optionsList = (IAST) arg1;
        }
      }
      if (optionsPattern == null) {
        optionsPattern = F.ListAlloc(10);
      }
      extractRules(optionsList, optionsPattern);
      if (optionsPattern != null) {
        rhsRuleValue = optionsRHSRuleValue(optionValue, optionsPattern);
        if (rhsRuleValue.isPresent()) {
          return rhsRuleValue;
        }
        if (!quiet) {
          // Option name `2` not found in defaults for `1`
          IOFunctions.printMessage(
              ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
        }
        return optionValue;
      }
      return F.NIL;
    } else { // ast.isAST1()
      optionValue = arg1;
      if (arg1.isSymbol()) {
        optionValue = F.$str(((ISymbol) arg1).getSymbolName());
      }

      Iterator<IdentityHashMap<ISymbol, IASTAppendable>> iter = engine.optionsStackIterator();
      while (iter.hasNext()) {
        IdentityHashMap<ISymbol, IASTAppendable> map = iter.next();
        if (map != null) {
          optionsPattern = map.get(S.LHS_HEAD);
          if (optionsPattern != null) {

            ISymbol lhsHead = optionsPattern.topHead();
            optionsPattern = map.get(lhsHead);
            rhsRuleValue = optionsRHSRuleValue(optionValue, optionsPattern);
            if (rhsRuleValue.isPresent()) {
              return rhsRuleValue;
            }
          }
        }
      }
      //          return arg1;
    }
    if (optionsPattern != null) {
      if (!quiet) {
        // Option name `2` not found in defaults for `1`
        IOFunctions.printMessage(
            ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
      }
      return optionValue;
    }
    return F.NIL;
  }

  /**
   * Get the right-hand-side of an options rule by comparing the <code>lhsOptionValue</code> with
   * the left-hand-side of the rules in <code>optionsPattern</code> for equality.
   *
   * @param lhsOptionValue
   * @param optionsPattern list of options rules
   * @return the right-hand-side expression or {@link F#NIL} if no matching rule was found
   */
  private static IExpr optionsRHSRuleValue(IExpr lhsOptionValue, IASTAppendable optionsPattern) {
    if (optionsPattern != null) {
      for (int i = 1; i < optionsPattern.size(); i++) {
        IAST rule = (IAST) optionsPattern.get(i);
        if (rule.arg1().equals(lhsOptionValue)) {
          return rule.arg2();
        }
      }
    }
    return F.NIL;
  }

  public static IExpr messageName(ISymbol symbol, IExpr expr) {
    RulesData rules = symbol.getRulesData();
    if (rules != null) {
      Map<IExpr, PatternMatcherEquals> map = rules.getEqualDownRules();
      PatternMatcherEquals matcher = map.get(F.MessageName(symbol, expr));
      if (matcher != null) {
        return matcher.getRHS();
      }
    }
    return F.NIL;
  }

  public static void initialize() {
    Initializer.init();
  }

  private PatternMatching() {}
}
