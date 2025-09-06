package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Deque;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.interfaces.ISetValueEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.BuiltinUsage;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;

public final class PatternMatching {

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
      S.Nothing.setEvaluator(new Nothing());
      S.Optional.setEvaluator(Optional.CONST);
      S.Options.setEvaluator(new Options());
      S.OptionValue.setEvaluator(new OptionValue());
      S.ReleaseHold.setEvaluator(new ReleaseHold());
      S.Rule.setEvaluator(new Rule());
      S.RuleDelayed.setEvaluator(new RuleDelayed());
      S.Sequence.setEvaluator(new Sequence());
      // if (!Config.FUZZY_PARSER) {
      S.Set.setEvaluator(new Set());
      S.SetDelayed.setEvaluator(new SetDelayed());
      // }
      S.SetSystemOptions.setEvaluator(new SetSystemOptions());
      S.Splice.setEvaluator(new Splice());
      S.SystemOptions.setEvaluator(new SystemOptions());
      S.Unique.setEvaluator(new Unique());
      if (!Config.FUZZY_PARSER) {
        S.Blank.setEvaluator(Blank.CONST);
        S.BlankSequence.setEvaluator(BlankSequence.CONST);
        S.BlankNullSequence.setEvaluator(BlankNullSequence.CONST);
        S.DownValues.setEvaluator(new DownValues());
        S.Pattern.setEvaluator(Pattern.CONST);
        S.PatternTest.setEvaluator(new PatternTest());
        S.Clear.setEvaluator(new Clear());
        S.ClearAll.setEvaluator(new ClearAll());
        S.Context.setEvaluator(new Context());
        S.Contexts.setEvaluator(new Contexts());
        S.Definition.setEvaluator(new Definition());
        S.FullDefinition.setEvaluator(new FullDefinition());
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
   * <p>
   * clears all values of the given symbols.
   *
   * </blockquote>
   *
   * <p>
   * <code>Clear</code> does not remove attributes, options, and default values associated with the
   * symbols. Use <code>ClearAll</code> to do so.
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
        if (x.isNIL()) {
          return F.NIL;
        }
        if (((ISymbol) x).hasProtectedAttribute()) {
          // Symbol `1` is Protected.
          Errors.printMessage(ast.topHead(), "wrsym", F.list(x), engine);
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
   * <p>
   * clears all values and attributes associated with the given symbols.
   *
   * </blockquote>
   */
  private static final class ClearAll extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable mutable = ast.copyAST();
      for (int i = 1; i < ast.size(); i++) {
        IExpr x = Validate.checkIdentifierHoldPattern(ast.get(i), ast, engine);
        if (x.isNIL()) {
          return F.NIL;
        }
        if (((ISymbol) x).hasProtectedAttribute()) {
          // Symbol `1` is Protected.
          Errors.printMessage(ast.topHead(), "wrsym", F.list(x), engine);
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
   * <p>
   * return the context of the given symbol.
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
   * <p>
   * return the current context.
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
        if (x.isNIL()) {
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
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  private static final class Contexts extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return ContextPath.getContexts();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
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
   * <p>
   * <code>Default</code> returns the default value associated with the <code>symbol</code> for a
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
   * <p>
   * <code>Default</code> returns the default value associated with the <code>symbol</code> for a
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
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.Default) && leftHandSide.size() > 1) {
        if (!leftHandSide.first().isSymbol()) {
          Errors.printMessage(builtinSymbol, "setps", F.list(leftHandSide.first()), engine);
          return rightHandSide;
        }
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (symbol.hasProtectedAttribute()) {
          Errors.printMessage(S.Default, "write", F.list(symbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }
        if (leftHandSide.size() == 2 && leftHandSide.first().isSymbol()) {
          symbol.setDefaultValue(rightHandSide);
          return rightHandSide;
        } else if (leftHandSide.size() == 3 && leftHandSide.first().isSymbol()) {
          int pos = leftHandSide.second().toIntDefault();
          if (pos > 0) {
            symbol.setDefaultValue(pos, rightHandSide);
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
   * <pre>
   * <code>Definition(symbol)
   * </code>
   * </pre>
   * 
   * <p>
   * prints values and rules associated with <code>symbol</code>.
   * </p>
   * 
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Definition(ArcSinh)
   * Attributes(ArcSinh)={Listable,NumericFunction,Protected}
   * 
   * ArcSinh(I/Sqrt(2))=I*1/4*Pi
   * 
   * ArcSinh(Undefined)=Undefined
   * 
   * ArcSinh(Infinity)=Infinity
   * 
   * ArcSinh(I*Infinity)=Infinity
   * 
   * ArcSinh(I)=I*1/2*Pi
   * 
   * ArcSinh(0)=0
   * 
   * ArcSinh(I*1/2)=I*1/6*Pi
   * 
   * ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi
   * 
   * ArcSinh(ComplexInfinity)=ComplexInfinity
   * </code>
   * </pre>
   * 
   * <pre>
   * <code>&gt;&gt; a=2
   * 2
   * 
   * &gt;&gt; Definition(a)
   * {a=2}
   * </code>
   * </pre>
   */
  private static final class Definition extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        try {
          String definitionString = symbol.definitionToString();
          return F.stringx(definitionString);
        } catch (IOException ioe) {
          return Errors.printMessage(S.Definition, ioe, engine);
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
   * <p>
   * prints the down-value rules associated with <code>symbol</code>.
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
   * <p>
   * the <code>Evaluate</code> function will be executed even if the function attributes <code>
   * HoldFirst, HoldRest, HoldAll</code> are set for the function head.
   *
   * </blockquote>
   */
  private static final class Evaluate extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        return ast.arg1().eval(engine);
      }
      IASTMutable sequence = ast.copy();
      sequence.set(0, S.Sequence);
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
   * <p>
   * or
   *
   * <pre>
   * <code>FilterRules(list-of-option-rules, list-of-symbols)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * filter the <code>list-of-option-rules</code> by <code>list-of-rules</code>or <code>
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
   * <p>
   * <a href="Options.md">Options</a>
   */
  private static final class FilterRules extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isListOfRules() && ast.arg2().isList()) {
        final IAST listOfRules = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();
        return F.mapList(listOfRules, rule -> filterRuleKeyFromList(rule, list2));
      }
      return F.NIL;
    }

    private static IExpr filterRuleKeyFromList(IExpr possibleRule, IAST list) {
      if (possibleRule.isRuleAST()) {
        final IAST rule = (IAST) possibleRule;
        final IExpr key = rule.first();
        for (int j = 1; j < list.size(); j++) {
          IExpr element = list.get(j);
          if (element.isRuleAST()) {
            element = element.first();
          }
          if (key.equals(element)) {
            return rule;
          }
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
   * <pre>
   * <code>FullDefinition(symbol)
   * </code>
   * </pre>
   * 
   * <p>
   * prints value and rule definitions associated with <code>symbol</code> and dependent symbols
   * without attribute <code>Protected</code> recursively.
   * </p>
   * 
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; FullDefinition(ArcSinh)
   * Attributes(ArcSinh)={Listable,NumericFunction,Protected}
   * 
   * ArcSinh(I/Sqrt(2))=I*1/4*Pi
   * 
   * ArcSinh(Undefined)=Undefined
   * 
   * ArcSinh(Infinity)=Infinity
   * 
   * ArcSinh(I*Infinity)=Infinity
   * 
   * ArcSinh(I)=I*1/2*Pi
   * 
   * ArcSinh(0)=0
   * 
   * ArcSinh(I*1/2)=I*1/6*Pi
   * 
   * ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi
   * 
   * ArcSinh(ComplexInfinity)=ComplexInfinity
   * </code>
   * </pre>
   * 
   * <pre>
   * <code>&gt;&gt; a(x_):=b(x,y);b[u_,v_]:={{u,v},a} 
   * 
   * &gt;&gt; FullDefinition(a) 
   * a(x_):=b(x,y)
   * 
   * b(u_,v_):={{u,v},a}
   * </code>
   * </pre>
   */
  private static final class FullDefinition extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isPresent()) {
        ISymbol symbol = (ISymbol) arg1;
        String definitionString = symbol.fullDefinitionToString();
        return F.stringx(definitionString);
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
   * <p>
   * <code>Hold</code> doesn't evaluate <code>expr</code>.
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

  /**
   * <pre>
   * <code>HoldComplete(expr)
   * </code>
   * </pre>
   * 
   * <p>
   * <code>HoldComplete</code> doesn't evaluate <code>expr</code>. <code>Hold</code> evaluates
   * <code>UpValues</code>for its arguments. <code>HoldComplete</code> doesn't evaluate
   * <code>UpValues</code>.
   * </p>
   * 
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; HoldComplete(3*2)
   * HoldComplete(3*2) 
   * 
   * &gt;&gt; Attributes(HoldComplete)
   * {HoldAllComplete,Protected,SequenceHold}
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="Hold.md">Hold</a>, <a href="HoldForm.md">HoldForm</a>,
   * <a href="HoldPattern.md">HoldPattern</a>, <a href="ReleaseHold.md">ReleaseHold</a>,
   * <a href="Unevaluated.md">Unevaluated</a>
   * </p>
   */
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
   * <p>
   * <code>HoldPattern</code> doesn't evaluate <code>expr</code> for pattern-matching.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * One might be very surprised that the following line evaluates to <code>True</code>!
   *
   * <pre>
   * &gt;&gt; MatchQ(And(x, y, z), Times(p__))
   * True
   * </pre>
   *
   * <p>
   * When the line above is evaluated <code>Times(p__)</code> evaluates to <code>(p__)</code> before
   * the kernel checks to see if the pattern matches. <code>MatchQ</code> then determines if
   * <code>And(x,y,z)</code> matches the pattern <code>(p__)</code> and it does because <code>
   * And(x,y,z)</code> is itself a sequence of one.
   *
   * <p>
   * Now the next line also evaluates to <code>True</code> because both <code>( And(p__) )</code>
   * and <code>( Times(p__) )</code> evaluate to <code>( p__ )</code>.
   *
   * <pre>
   * &gt;&gt; Times(p__)===And(p__)
   * True
   * </pre>
   *
   * <p>
   * In the examples above prevent the patterns from evaluating, by wrapping them with <code>
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
   * <p>
   * In the next lines <code>HoldPattern</code> is used to ensure the head <code>(And)</code> is
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
   * <p>
   * returns <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Identity_function">Wikipedia - Identity function</a>
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
  private static final class Identity extends AbstractFunctionEvaluator {

    @Override
    public boolean evalIsReal(IAST ast) {
      return ast.forAll(x -> {
        if (x.isRealResult()) {
          return true;
        }
        return false;
      });
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return ast.arg1();
      }
      Errors.printMessage(S.General, "argx", F.List(S.Identity, F.ZZ(ast.argSize())), engine);
      return F.NIL;
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
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
              // "sym", "Argument `1` at position `2` is expected to be a symbol.", //
              return Errors.printMessage(S.Information, "sym", F.List(arg1, F.C1), engine);
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
          } else {
            if (symbol.isBuiltInSymbol()) {
              String summaryText = BuiltinUsage.summaryText(((IBuiltInSymbol) symbol));
              if (summaryText.length() > 0) {
                stream.println(symbol.toString() + " - " + summaryText);
              }
            }
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
              return Errors.printMessage(S.Information, ioe, engine);
            }
          }
          return S.Null;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.Information, rex, engine);
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
        // "sym", "Argument `1` at position `2` is expected to be a symbol.", //
        return Errors.printMessage(S.MessageName, "sym", F.List(ast.arg1(), F.C1), engine);
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
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.MessageName, 3, 4) && leftHandSide.first().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (!symbol.hasProtectedAttribute()) {
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


  private static final class Nothing extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return S.Nothing;
    }
  }


  /**
   *
   *
   * <pre>
   * Optional(patt, default)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * patt : default
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is a pattern which matches <code>patt</code>, which if omitted should be replaced by <code>
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
        return org.matheclipse.core.expression.OptionsPattern.optionsList(arg1, false);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.Options, 2) && leftHandSide.first().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.first();
        if (!symbol.hasProtectedAttribute()) {
          try {
            if (!builtinSymbol.equals(S.SetDelayed)) {
              rightHandSide = engine.evaluate(rightHandSide);
            }
          } catch (final ReturnException e) {
            rightHandSide = e.getValue();
          }
          symbol.putDownRule(IPatternMatcher.SET, true, (IAST) leftHandSide, rightHandSide,
              engine.isPackageMode());
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
        return IPatternMap.optionValueReplace(ast, false, engine);
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
   * <p>
   * prints the own-value rule associated with <code>symbol</code>.
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
        return F.list(F.RuleDelayed(F.HoldPattern(symbol), value));
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
            return PatternNested.valueOf(symbol, null, arg2);
          } else {
            // First element in `1` is not a valid pattern name.
            return Errors.printMessage(ast.topHead(), "patvar", F.list(ast), engine);
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


  private static final class PatternTest extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof IPatternObject) {
        IPatternObject po = (IPatternObject) arg1;
        if (po.isPatternDefault()) {
          // Pattern `1` contains inappropriate optional object.
          return Errors.printMessage(S.PatternTest, "patop", F.list(ast), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

  }


  private static final class ReleaseHold extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      return F.subst(arg1, ReleaseHold::releaseHold);
    }

    private static IExpr releaseHold(IExpr expr) {
      if (expr.isAST()) {
        IAST holdAST = (IAST) expr;
        if (holdAST.isFunctionID(ID.Hold, ID.HoldForm, ID.HoldComplete, ID.HoldPattern)) {
          if (holdAST.isAST1()) {
            return holdAST.arg1();
          }
          return holdAST.apply(S.Sequence);
        }

        IASTMutable result = F.NIL;
        IAST list = holdAST;
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isAST()) {
            IExpr temp = arg.replaceAll(ReleaseHold::releaseHold);
            if (temp.isPresent()) {
              result = result.setIfPresent(list, i, temp);
            }
          }
        }
        return result;

      }
      return F.NIL;
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

    protected static IExpr repeatedLimit(IExpr arg1, IExpr arg2, int defaultMin,
        EvalEngine engine) {
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
        if (F.isNotPresent(min) || F.isNotPresent(max)) {
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
        if (F.isNotPresent(max)) {
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
   * <p>
   * represents a rule replacing <code>x</code> with <code>y</code>.
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
   * <p>
   * Rule called with 3 arguments; 2 arguments are expected.
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
      if (arg2.isNIL()) {
        if (leftHandSide.equals(ast.arg1())) {
          return F.NIL;
        }
        return F.Rule(leftHandSide, ast.arg2());
      }
      return F.Rule(leftHandSide, arg2);
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
   * <p>
   * represents a rule replacing <code>x</code> with <code>y</code>, with <code>y</code> held
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
        return F.RuleDelayed(leftHandSide, ast.arg2());
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


  private static final class Sequence extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Deque<IExpr> stack = engine.getStack();
      if (stack.size() == 1 && ast.head() == S.Sequence) {
        return ast.setAtClone(0, S.Identity);
      }

      return F.NIL;
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
   * <p>
   * evaluates <code>value</code> and assigns it to <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * {s1, s2, s3} = {v1, v2, v3}
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sets multiple symbols <code>(s1, s2, ...)</code> to the corresponding values <code>
   * (v1, v2, ...)</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Set</code> can be used to give a symbol a value:<br>
   *
   * <pre>
   * &gt;&gt; a = 3
   * 3
   *
   * &gt;&gt; a
   * 3
   * </pre>
   *
   * <p>
   * You can set multiple values at once using lists:<br>
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
   * <p>
   * <code>Set</code> evaluates its right-hand side immediately and assigns it to the left-hand
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
   * <p>
   * 'Set' always returns the right-hand side, which you can again use in an assignment:<br>
   *
   * <pre>
   * &gt;&gt; a = b = c = 2
   * &gt;&gt; a == b == c == 2
   * True
   * </pre>
   *
   * <p>
   * 'Set' supports assignments to parts:<br>
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
   * <p>
   * Set a submatrix:
   *
   * <pre>
   * &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
   * &gt;&gt; B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}
   * &gt;&gt; B
   * {{1, t, u}, {4, y, z}, {7, 8, 9}}
   * </pre>
   */
  private static final class Set extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();

      IExpr head = engine.evaluate(leftHandSide.head());
      if (head.topHead().equals(S.Association)) {
        head = S.Association;
      }
      IExpr rightHandSide = ast.arg2();
      // try {
      // rightHandSide = engine.evaluate(rightHandSide);
      // } catch (final ConditionException e) {
      // } catch (final ReturnException e) {
      // rightHandSide = e.getValue();
      // }
      if (!leftHandSide.isAST() && !leftHandSide.isSymbolOrPattern()) {
        // Cannot assign to raw object `1`.
        Errors.printMessage(S.Set, "setraw", F.list(leftHandSide), engine);
        return rightHandSide;
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
        Errors.printMessage(ast.topHead(), "usraw", F.list(leftHandSide), engine);
        return rightHandSide;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static IExpr createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide,
        boolean packageMode, final EvalEngine engine) throws RuleCreationError {
      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      IExpr temp = evalLHS(leftHandSide, flags, engine);
      return setDownRule(temp, leftHandSide, flags[0], rightHandSide, packageMode);
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
   * <p>
   * assigns <code>value</code> to <code>expr</code>, without evaluating <code>value</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>SetDelayed</code> is like <code>Set</code>, except it has attribute <code>HoldAll
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
   * <p>
   * Changing the value of <code>a</code> affects <code>x</code>:<br>
   *
   * <pre>
   * &gt;&gt; a = 2
   * 2
   *
   * &gt;&gt; x
   * 2
   * </pre>
   *
   * <p>
   * <code>Condition</code> (<code>/;</code>) can be used with <code>SetDelayed</code> to make an
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
  private static final class SetDelayed extends AbstractFunctionEvaluator {

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
        if (!leftHandSide.isAST() && !leftHandSide.isSymbolOrPattern()) {
          // Cannot assign to raw object `1`.
          Errors.printMessage(S.SetDelayed, "setraw", F.list(leftHandSide), engine);
          return rightHandSide;
        }

        if (leftHandSide.isAST()) {
          if (head.isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) head;
            IEvaluator eval = symbol.getEvaluator();
            if (eval instanceof ISetEvaluator) {
              IExpr temp = ((ISetEvaluator) eval).evaluateSet(leftHandSide, rightHandSide,
                  S.SetDelayed, engine);
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
        Errors.printMessage(ast.topHead(), "usraw", F.list(leftHandSide), engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static void createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide,
        boolean packageMode, final EvalEngine engine) throws RuleCreationError {
      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      IExpr temp = evalLHS(leftHandSide, flags, engine);
      setDelayedDownRule(temp, leftHandSide, flags[0], rightHandSide, packageMode);
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
      if (ast.isAST1() && ast.arg1().isString()) {
      }

      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <pre>
   * <code>Splice(list-of-elements)
   * </code>
   * </pre>
   * 
   * <p>
   * the <code>list-of-elements</code> will automatically be converted into a <code>Sequence</code>
   * of elements.
   * </p>
   * 
   * <pre>
   * <code>Splice(list-of-elements, head-pattern)
   * </code>
   * </pre>
   * 
   * <p>
   * the <code>list-of-elements</code> will automatically be converted into a <code>Sequence</code>
   * of elements, if the calling expression matches the <code>head-pattern</code>.
   * </p>
   * 
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; h(a, b, c, Splice({{1,1},{2,2}}, h), d, e) 
   * h(a,b,c,{1,1},{2,2},d,e)
   * </code>
   * </pre>
   */
  private static final class Splice extends AbstractEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        IAST list = (IAST) arg1;
        IExpr headPattern = S.List;
        if (ast.isAST2()) {
          headPattern = ast.arg2();
        }
        IExpr peek = engine.getStackFrame(1);
        if (peek.isPresent() && peek.isAST()) {
          IExpr topHead = peek.head();
          if (headPattern.isFreeOfPatterns()) {
            if (topHead.equals(headPattern)) {
              if (list.size() == 1) {
                return S.Nothing;
              }
              return list.apply(S.Sequence);
            }
            return F.NIL;
          }

          IPatternMatcher matcher = engine.evalPatternMatcher(headPattern);
          if (matcher.test(topHead)) {
            if (topHead.size() == 1) {
              return S.Nothing;
            }
            return ((IAST) arg1).apply(S.Sequence);
          }
          return F.NIL;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

  }

  private static final class SystemOptions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // stub implementation returning empty list
      if (ast.isAST1() && ast.arg1().isString()) {
        String str = ast.arg1().toString();
        if (str.equals("DifferentiationOptions")) {
          IAST list = F.List(S.Hold, S.HoldComplete, S.Less, S.LessEqual, S.Greater, S.GreaterEqual,
              S.Inequality, S.Unequal, S.Nand, S.Nor, S.Xor, S.Not, S.Element, S.Exists, S.ForAll,
              S.Implies, S.Positive, S.Negative, S.NonPositive, S.NonNegative, S.Replace,
              S.ReplaceAll, S.ReplaceRepeated);
          IAST excludedFunctions = F.Rule("ExcludedFunctions", list);
          return F.list(F.Rule("DifferentiationOptions", F.list(excludedFunctions)));
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

  private static IExpr setDownRule(IExpr evaledLHS, IExpr originalLHS, int flags,
      IExpr rightHandSide, boolean packageMode) {
    ISymbol lhsSymbol = getLookupReferenceName(evaledLHS);
    if (lhsSymbol == null) {
      // `1` does not contain a symbol to attach a rule to.
      Errors.printMessage(S.Set, "nosym", F.list(originalLHS), EvalEngine.get());
      return rightHandSide;
    }
    if (lhsSymbol.hasProtectedAttribute()) {
      // Symbol `1` is Protected.
      Errors.printMessage(S.Set, "wrsym", F.list(lhsSymbol), EvalEngine.get());
      return rightHandSide;
    }
    if (originalLHS.isAST()) {
      if (lhsSymbol.hasProtectedAttribute()) {
        // Tag `1` in `2` is Protected.
        Errors.printMessage(S.Set, "write", F.list(lhsSymbol, originalLHS), EvalEngine.get());
        throw new FailedException();
      }
      if (evaledLHS instanceof IPatternObject) {
        lhsSymbol.putDownRule(flags | IPatternMatcher.SET, false, (IPatternObject) evaledLHS,
            rightHandSide, packageMode);
      } else {
        if (evaledLHS.isAST()) {
          originalLHS = evaledLHS;
        } else if (!originalLHS.isAST()) {
          originalLHS = F.HoldPattern(originalLHS);
        }

        if (evaledLHS.isSymbol()) {
          ((ISymbol) evaledLHS).assignValue(rightHandSide, true);
        } else {
          lhsSymbol.putDownRule(IPatternMatcher.SET, false, (IAST) originalLHS, rightHandSide,
              packageMode);
        }


      }
      return rightHandSide;
    }

    if (evaledLHS.isSymbol()) {
      lhsSymbol = (ISymbol) evaledLHS;
      if (lhsSymbol.hasProtectedAttribute()) {
        // Symbol `1` is Protected.
        Errors.printMessage(S.Set, "wrsym", F.list(lhsSymbol), EvalEngine.get());
        return rightHandSide;
      }
      lhsSymbol.assignValue(rightHandSide, false);
      return rightHandSide;
    }

    throw new RuleCreationError(originalLHS);
  }

  // private static ISymbol determineRuleTag(IExpr leftHandSide) {
  // while (leftHandSide.isCondition()) {
  // if (leftHandSide.first().isAST()) {
  // leftHandSide = leftHandSide.first();
  // continue;
  // }
  // break;
  // }
  // if (leftHandSide.isSymbol()) {
  // return (ISymbol) leftHandSide;
  // }
  // return leftHandSide.topHead();
  // }

  private static void setDelayedDownRule(IExpr evaledLHS, IExpr originalLHS, int flags,
      IExpr rightHandSide, boolean packageMode) {
    ISymbol lhsSymbol = getLookupReferenceName(evaledLHS);
    if (lhsSymbol == null) {
      // `1` does not contain a symbol to attach a rule to.
      Errors.printMessage(S.SetDelayed, "nosym", F.list(originalLHS), EvalEngine.get());
      throw new FailedException();
    }
    if (lhsSymbol.hasProtectedAttribute()) {
      // Symbol `1` is Protected.
      Errors.printMessage(S.SetDelayed, "wrsym", F.list(lhsSymbol), EvalEngine.get());
      throw new FailedException();
    }
    // if (evaledLHS.isAST()) {
    if (lhsSymbol.hasProtectedAttribute()) {
      // Tag `1` in `2` is Protected.
      Errors.printMessage(S.SetDelayed, "write", F.list(lhsSymbol, originalLHS), EvalEngine.get());
      throw new FailedException();
    }
    if (evaledLHS instanceof IPatternObject) {
      lhsSymbol.putDownRule(flags | IPatternMatcher.SET_DELAYED, false, (IPatternObject) evaledLHS,
          rightHandSide, packageMode);
    } else {
      if (evaledLHS.isAST()) {
        originalLHS = evaledLHS;
      } else if (!originalLHS.isAST()) {
        originalLHS = F.HoldPattern(originalLHS);
      }

      if (evaledLHS.isSymbol()) {
        ((ISymbol) evaledLHS).assignValue(rightHandSide, true);
      } else {
        lhsSymbol.putDownRule(flags | IPatternMatcher.SET_DELAYED, false, (IAST) originalLHS,
            rightHandSide, packageMode);
      }
    }
    return;
    // }
    // if (evaledLHS.isSymbol()) {
    // lhsSymbol = (ISymbol) evaledLHS;
    // if (lhsSymbol.hasProtectedAttribute()) {
    // // Symbol `1` is Protected.
    // Errors.printMessage(S.SetDelayed, "wrsym", F.list(lhsSymbol), EvalEngine.get());
    // throw new FailedException();
    // }
    // ((ISymbol) evaledLHS).assignValue(rightHandSide, true);
    // return;
    // }
    // throw new RuleCreationError(evaledLHS);
  }

  private static class TagSet extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isSymbol()) {
        final ISymbol symbol = (ISymbol) arg1;
        final IExpr leftHandSide = ast.arg2();
        IExpr rightHandSide = ast.arg3();
        try {
          rightHandSide = engine.evaluate(rightHandSide);
        } catch (final ConditionException e) {
        } catch (final ReturnException e) {
          rightHandSide = e.getValue();
        }
        if (symbol.hasProtectedAttribute()) {
          // Tag `1` in `2` is Protected.
          Errors.printMessage(S.TagSet, "write", F.list(symbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }

        if (leftHandSide.isList()) {
          // thread over lists
          try {
            rightHandSide = engine.evaluate(rightHandSide);
          } catch (final ReturnException e) {
            rightHandSide = e.getValue();
          }
          IExpr temp = engine.threadASTListArgs(F.TagSet(symbol, leftHandSide, rightHandSide),
              S.TagSet, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
        }
        try {
          Object[] result =
              createPatternMatcher(symbol, leftHandSide, rightHandSide, false, S.TagSet, engine);
          return (IExpr) result[1];
        } catch (final ValidateException ve) {
          Errors.printMessage(ast.topHead(), ve, engine);
          return rightHandSide;
        }
      } else {
        // Argument `1` at position `2` is expected to be a symbol.
        Errors.printMessage(S.TagSet, "sym", F.list(arg1, F.C1), EvalEngine.get());
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
    protected static Object[] createPatternMatcher(ISymbol tagSetSymbol, IExpr leftHandSide,
        IExpr rightHandSide, boolean packageMode, IBuiltInSymbol tagSymbol, EvalEngine engine)
        throws RuleCreationError {
      final Object[] result = new Object[2];

      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);

      result[0] = null; // IPatternMatcher
      result[1] = rightHandSide;

      IAST lhsAST = Validate.checkASTTagRuleType(leftHandSide, S.TagSet);
      boolean found = false;
      boolean putDownRule = false;
      if (lhsAST.head().equals(tagSetSymbol)) {
        found = true;
      } else {
        if (lhsAST.isCondition() && lhsAST.first().isAST()) {
          if (lhsAST.first().head().equals(tagSetSymbol)) {
            putDownRule = true;
          } else {
            found = isTagAvailable(tagSetSymbol, (IAST) lhsAST.first());
          }
        } else {
          if (lhsAST.head().equals(tagSetSymbol)) {
            putDownRule = true;
          } else {
            found = isTagAvailable(tagSetSymbol, lhsAST);
          }
        }
      }
      if (putDownRule) {
        boolean pMode = packageMode;
        if (!packageMode && tagSetSymbol.hasProtectedAttribute()) {
          // Tag `1` in `2` is Protected.
          Errors.printMessage(tagSymbol.topHead(), "write", F.list(tagSetSymbol, leftHandSide),
              EvalEngine.get());
          throw new FailedException();
        }
        pMode = true;
        result[0] = tagSetSymbol.putDownRule(flags[0] | IPatternMatcher.SET, false, lhsAST,
            rightHandSide, pMode);
        return result;
      }
      if (found) {
        result[0] =
            tagSetSymbol.putUpRule(flags[0] | IPatternMatcher.TAGSET, false, lhsAST, rightHandSide);
        return result;
      }
      if (!lhsAST.isFree(tagSetSymbol)) {
        // Cannot assign to raw object `1`.
        String str = Errors.getMessage("tagpos", F.list(tagSetSymbol, lhsAST), engine);
        throw new ArgumentTypeException(str);
      }
      // Tag `1` not found in `2`
      Errors.printMessage(tagSymbol, "tagnf", F.list(tagSetSymbol, lhsAST), engine);
      throw new FailedException();

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
        final ISymbol symbol = (ISymbol) arg1;
        final IExpr leftHandSide = ast.arg2();
        final IExpr rightHandSide = ast.arg3();
        if (symbol.hasProtectedAttribute()) {
          // Tag `1` in `2` is Protected.
          Errors.printMessage(ast.topHead(), "write", F.list(symbol, leftHandSide),
              EvalEngine.get());
          throw new FailedException();
        }
        try {
          createPatternMatcher(symbol, leftHandSide, rightHandSide, false, S.TagSetDelayed, engine);
          return S.Null;
        } catch (final ValidateException ve) {
          Errors.printMessage(ast.topHead(), ve, engine);
          throw new FailedException();
        }
      } else {
        // Argument `1` at position `2` is expected to be a symbol.
        Errors.printMessage(S.TagSetDelayed, "sym", F.list(arg1, F.C1), EvalEngine.get());
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
   * <p>
   * create a unique symbol of the form <code>expr$...</code>.
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
   * <p>
   * create a unique symbol of the form <code>expr...</code>.
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
   * <p>
   * or
   *
   * <pre>
   * expr =.
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * removes any definitions belonging to the left-hand-side <code>expr</code>.
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
   * <p>
   * Unsetting an already unset or never defined variable will not change anything:
   *
   * <pre>
   * &gt;&gt; a =.
   *
   * &gt;&gt; b =.
   * </pre>
   *
   * <p>
   * <code>Unset</code> can unset particular function values. It will print a message if no
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
          IExpr temp = engine.threadASTListArgs(F.Unset(leftHandSide), S.Unset, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
          return F.NIL;
        }

        if (leftHandSide.isAST()) {
          final ISymbol lhsSymbol = getLookupReferenceName(leftHandSide);
          if (lhsSymbol == null) {
            // `1` does not contain a symbol to attach a rule to.
            Errors.printMessage(S.Unset, "nosym", F.list(leftHandSide), EvalEngine.get());
            throw new FailedException();
          }
          if (lhsSymbol.hasProtectedAttribute()) {
            // Symbol `1` is Protected.
            Errors.printMessage(S.Unset, "wrsym", F.list(lhsSymbol), EvalEngine.get());
            throw new FailedException();
          }
        }
        if (leftHandSide.isSymbol()) {
          final ISymbol lhsSymbol = (ISymbol) leftHandSide;
          if (lhsSymbol.hasProtectedAttribute()) {
            // Symbol `1` is Protected.
            Errors.printMessage(ast.topHead(), "wrsym", F.list(lhsSymbol), EvalEngine.get());
            throw new FailedException();
          }
        }

        removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
        return S.Null;
      } catch (RuleCreationError rce) {
        // Cannot unset object `1`.
        Errors.printMessage(ast.topHead(), "usraw", F.list(leftHandSide), engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private static void removePatternMatcher(IExpr leftHandSide, boolean packageMode,
        EvalEngine engine) throws RuleCreationError {

      if (leftHandSide.isAST()) {
        leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
      }
      removeRule(leftHandSide, packageMode);
    }

    private static void removeRule(IExpr leftHandSide, boolean packageMode) {
      if (leftHandSide.isAST()) {
        final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
        if (!lhsSymbol.removeRule(IPatternMatcher.SET, false, leftHandSide, packageMode)) {
          printAssignmentNotFound(lhsSymbol, leftHandSide);
        }
        return;
      }
      if (leftHandSide.isSymbol()) {
        final ISymbol lhsSymbol = (ISymbol) leftHandSide;

        if (!lhsSymbol.removeRule(IPatternMatcher.SET, true, leftHandSide, packageMode)) {
          printAssignmentNotFound(lhsSymbol, leftHandSide);
        }
        return;
      }

      throw new RuleCreationError(leftHandSide);
    }

    private static void printAssignmentNotFound(final ISymbol lhsSymbol, final IExpr leftHandSide) {
      // Assignment on `2` for `1` not found.
      Errors.printMessage(S.Unset, "norep", F.List(leftHandSide, lhsSymbol));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class UpSet extends AbstractFunctionEvaluator {

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
              engine.threadASTListArgs(F.UpSet(leftHandSide, rightHandSide), S.UpSet, "tdlen");
          if (temp.isPresent()) {
            return engine.evaluate(temp);
          }
        }
        if (!leftHandSide.isAST() && !leftHandSide.isSymbolOrPattern()) {
          // Cannot assign to raw object `1`.
          Errors.printMessage(S.UpSet, "setraw", F.list(leftHandSide), engine);
          return rightHandSide;
        }
        Object[] result = createPatternMatcher(leftHandSide, rightHandSide, false, engine);
        return (IExpr) result[1];
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide,
        boolean packageMode, EvalEngine engine) throws RuleCreationError {
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

      IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide, S.UpSet);
      for (int i = 1; i < lhsAST.size(); i++) {
        IExpr temp = lhsAST.get(i);
        ISymbol lhsSymbol = getLookupReferenceName(temp);
        if (lhsSymbol == null) {
          continue;
        }
        if (lhsSymbol.hasProtectedAttribute()) {
          // Tag `1` in `2` is Protected.
          Errors.printMessage(S.UpSet, "write", F.list(lhsSymbol, leftHandSide), EvalEngine.get());
          return result;
        }
        // if (lhsSymbol.hasProtectedAttribute()) {
        // // Tag `1` in `2` is Protected.
        // Errors.printMessage(S.TagSet, "write", F.list(lhsSymbol, leftHandSide),
        // EvalEngine.get());
        // throw new FailedException();
        // }
        if (lhsSymbol != null) {
          result[0] =
              lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET, false, lhsAST, rightHandSide);
        }
      }
      if (result[0] == null) {
        // `1` does not contain a symbol to attach a rule to.
        Errors.printMessage(S.UpSet, "nosym", F.list(lhsAST), engine);
        throw new FailedException();
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }

  private static final class UpSetDelayed extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr leftHandSide = ast.arg1();
      final IExpr rightHandSide = ast.arg2();
      try {
        if (!leftHandSide.isAST() && !leftHandSide.isSymbolOrPattern()) {
          // Cannot assign to raw object `1`.
          Errors.printMessage(S.UpSetDelayed, "setraw", F.list(leftHandSide), engine);
          return rightHandSide;
        }

        createPatternMatcher(leftHandSide, rightHandSide, false, engine);

        return S.Null;
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide,
        boolean packageMode, EvalEngine engine) throws RuleCreationError {
      final Object[] result = new Object[2];

      int[] flags = new int[] {IPatternMatcher.NOFLAG};
      leftHandSide = evalLHS(leftHandSide, flags, engine);

      result[0] = null;
      result[1] = rightHandSide;

      IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide, S.UpSetDelayed);
      for (int i = 1; i < lhsAST.size(); i++) {
        IExpr temp = lhsAST.get(i);
        ISymbol lhsSymbol = getLookupReferenceName(temp);
        if (lhsSymbol == null) {
          continue;
        }
        if (lhsSymbol.hasProtectedAttribute()) {
          // Tag `1` in `2` is Protected.
          Errors.printMessage(S.UpSet, "write", F.list(lhsSymbol, leftHandSide), EvalEngine.get());
          throw new FailedException();
        }
        result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET_DELAYED, false, lhsAST,
            rightHandSide);

        // if (temp instanceof IPatternObject) {
        // IExpr headTest = ((IPatternObject) temp).getHeadTest();
        // if (headTest != null && headTest.isSymbol()) {
        // ISymbol lhsSymbol = (ISymbol) headTest;
        // result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET_DELAYED, false,
        // lhsAST,
        // rightHandSide);
        // }
        // continue;
        // }
        // ISymbol lhsSymbol = null;
        // if (temp.isSymbol()) {
        // lhsSymbol = (ISymbol) temp;
        // } else {
        // lhsSymbol = lhsAST.get(i).topHead();
        // }
        // result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET_DELAYED, false,
        // lhsAST,
        // rightHandSide);

      }
      if (result[0] == null) {
        // `1` does not contain a symbol to attach a rule to.
        Errors.printMessage(S.UpSetDelayed, "nosym", F.list(lhsAST), engine);
        throw new FailedException();
      }
      return result;
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
   * <code>UpValues(symbol)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prints the up-value rules associated with <code>symbol</code>.
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
    if (leftHandSide.isAST() && (((IAST) leftHandSide).getEvalFlags()
        & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
      if (leftHandSide.isHoldPatternOrLiteral()) {
        flags[0] = leftHandSide.isAST(S.HoldPattern, 2) ? IPatternMatcher.HOLDPATTERN
            : IPatternMatcher.LITERAL;
        return leftHandSide.first();
      }
      return engine.evalHoldPattern((IAST) leftHandSide);
    }
    return leftHandSide;
  }

  /**
   * Find the lookup name of the reference expression associated to <code>expr</code>, or
   * {@link F#NIL} if there is no such a reference.
   * <p>
   * In general, the lookup reference name coincides with the lookup name of the expression.
   * However, there are some exceptions:
   * <ul>
   * <li>Expressions with heads {@link S#Condition}, {@link S#HoldPattern} or {@link S#PatternTest}
   * are not considered reference expressions. The reference expression is the reference expression
   * of its first element.
   * <li>(named) {@link IPatternObject} expressions takes its lookup reference name from the pattern
   * they hold.
   * <li>{@link S#Verbatim} expressions pick the lookup reference name from the lookup name of the
   * expression they hold.
   * </ul>
   * 
   * @param expr
   * @return
   */
  private static ISymbol getLookupReferenceName(IExpr expr) {
    IExpr leftHandSide = getReferenceExpression(expr);
    if (leftHandSide == null) {
      leftHandSide = expr;
    }
    if (leftHandSide.isSymbol()) {
      return (ISymbol) leftHandSide;
    } else if (leftHandSide instanceof IPatternObject) {
      IPatternObject temp = (IPatternObject) leftHandSide;
      if (leftHandSide instanceof PatternNested) {
        return getLookupName(((PatternNested) leftHandSide).getPatternExpr());
      }
      return getLookupName(temp);
    } else if (leftHandSide.isAST(S.Verbatim, 2)) {
      return getLookupName(leftHandSide.first());
    } else if (leftHandSide.isFunctionID(ID.Condition, ID.HoldPattern, ID.PatternTest)) {
      return getReferenceExpression(leftHandSide.first());
    }
    return leftHandSide.topHead();
  }

  private static ISymbol getLookupName(IExpr expr) {
    IExpr leftHandSide = getReferenceExpression(expr);
    if (leftHandSide == null) {
      leftHandSide = expr;
    }
    if (leftHandSide.isSymbol()) {
      return (ISymbol) leftHandSide;
    }
    if (leftHandSide instanceof IPatternObject) {
      IExpr headTest = ((IPatternObject) leftHandSide).getHeadTest();
      if (headTest != null && headTest.isSymbol()) {
        return (ISymbol) headTest;
      }
    }
    return null;
  }

  private static ISymbol getReferenceExpression(IExpr leftHandSide) {
    if (leftHandSide.isSymbol()) {
      return (ISymbol) leftHandSide;
    }
    if (leftHandSide.isAST() && leftHandSide.argSize() > 0) {
      IExpr lhsHead = leftHandSide.head();
      while (leftHandSide.isFunctionID(ID.Condition, ID.HoldPattern, ID.PatternTest)) {
        if (leftHandSide.isAST() && leftHandSide.argSize() > 0) {
          leftHandSide = getReferenceExpression(leftHandSide.first());
          if (leftHandSide == null) {
            return null;
          }
          if (leftHandSide.isSymbol()) {
            return (ISymbol) leftHandSide;
          }
          lhsHead = leftHandSide.head();
        } else {
          break;
        }
      }
      return getLookupReferenceName(lhsHead);
    }
    return null;
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
