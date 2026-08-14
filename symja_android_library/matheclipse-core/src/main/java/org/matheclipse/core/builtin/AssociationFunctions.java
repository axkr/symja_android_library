package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.MutableInt;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AssociationFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AssociateTo.setEvaluator(new AssociateTo());
      S.Association.setEvaluator(new Association());
      S.AssociationMap.setEvaluator(new AssociationMap());
      S.AssociationThread.setEvaluator(new AssociationThread());
      S.Counts.setEvaluator(new Counts());
      S.KeyDrop.setEvaluator(new KeyDrop());
      S.KeyExistsQ.setEvaluator(new KeyExistsQ());
      S.Key.setEvaluator(new Key());
      S.Keys.setEvaluator(new Keys());
      S.KeySelect.setEvaluator(new KeySelect());
      S.KeySort.setEvaluator(new KeySort());
      S.KeyTake.setEvaluator(new KeyTake());
      S.KeyValueMap.setEvaluator(new KeyValueMap());
      S.LetterCounts.setEvaluator(new LetterCounts());
      S.Lookup.setEvaluator(new Lookup());
      S.Merge.setEvaluator(new Merge());
      S.Query.setEvaluator(new Query());
      S.Structure.setEvaluator(new Structure());
      S.Summary.setEvaluator(new Summary());
      S.Values.setEvaluator(new Values());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>AssociateTo(assoc, rule)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * append <code>rule</code> to the association <code>assoc</code> and assign the result to
   * <code>assoc</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>AssociateTo(assoc, list-of-rules)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * append the <code>list-of-rules</code> to the association <code>assoc</code> and assign the
   * result to <code>assoc</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; assoc = &lt;|&quot;A&quot; -&gt; &lt;|&quot;a&quot; -&gt; 1, &quot;b&quot; -&gt; 2, &quot;c&quot; -&gt; 3|&gt;|&gt;
   * &lt;|A-&gt;&lt;|a-&gt;1,b-&gt;2,c-&gt;3|&gt;|&gt;
   *
   * &gt;&gt; AssociateTo(assoc, &quot;A&quot; -&gt; 11)
   * &lt;|A-&gt;11|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="AssociationQ.md">AssociationQ</a>,
   * <a href="AssociationMap.md">AssociationMap</a>,
   * <a href="AssociationThread.md">AssociationThread</a>, <a href="Counts.md">Counts</a>,
   * <a href="Lookup.md">Lookup</a>, <a href="KeyExistsQ.md">KeyExistsQ</a>,
   * <a href="Keys.md">Keys</a>, <a href="KeySort.md">KeySort</a>, <a href="Values.md">Values</a>
   */
  private static final class AssociateTo extends AbstractCoreFunctionEvaluator {

    private static class AssociateToFunction implements Function<IExpr, IExpr> {
      private final IExpr value;

      public AssociateToFunction(final IExpr value) {
        this.value = value;
      }

      @Override
      public IExpr apply(final IExpr symbolValue) {
        if (symbolValue.isAssociation()) {
          if (value.isRuleAST() || value.isListOfRules() || value.isAssociation()) {
            IAssociation result = ((IAssociation) symbolValue); // .copy();
            result.appendRules((IAST) value);
            return result;
          } else {
            // The argument is not a rule or a list of rules.
            return Errors.printMessage(S.AssociateTo, "invdt", F.List(), EvalEngine.get());
          }
        }
        // The argument `1` is not a valid Association.
        return Errors.printMessage(S.AssociateTo, "invak", F.list(symbolValue), EvalEngine.get());
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr leftHandSide = ast.arg1();
      if (leftHandSide.isSymbol()) {
        ISymbol sym = (ISymbol) leftHandSide;
        IExpr arg2 = engine.evaluate(ast.arg2());
        Function<IExpr, IExpr> function = new AssociateToFunction(arg2);
        IExpr[] results = sym.reassignSymbolValue(function, S.AssociateTo, engine);
        if (results != null) {
          return results[1];
        }
        return F.NIL;
      }
      if (leftHandSide.isASTSizeGE(S.Part, 3) && leftHandSide.first().isSymbol()) {
        ISymbol sym = (ISymbol) leftHandSide.first();
        return assignPartTo(sym, (IAST) leftHandSide, ast, engine);
      }

      // `1` is not a variable with a value, so its value cannot be changed.
      return Errors.printMessage(ast.topHead(), "rvalue", F.list(leftHandSide), engine);
    }

    private static IExpr assignPartTo(ISymbol symbol, IAST part, final IAST ast,
        EvalEngine engine) {
      if (symbol.hasAssignedSymbolValue()) {
        IExpr value = ast.arg2();
        if (value.isRuleAST() || value.isListOfRules() || value.isAssociation()) {
          IExpr oldValue = engine.evaluate(part);
          if (oldValue.isAssociation()) {
            IAssociation newResult = ((IAssociation) oldValue).copy();
            newResult.appendRules((IAST) value);
            engine.evaluate(F.Set(part, newResult));
            return symbol.assignedValue();
          }
          // The argument `1` is not a valid Association.
          return Errors.printMessage(ast.topHead(), "invak", F.list(oldValue), EvalEngine.get());
        }
        // The argument is not a rule or a list of rules.
        return Errors.printMessage(ast.topHead(), "invdt", F.List(), EvalEngine.get());
      }
      // `1` is not a variable with a value, so its value cannot be changed.
      return Errors.printMessage(ast.topHead(), "rvalue", F.list(symbol), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Association(list-of-rules)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a <code>key-&gt;value</code> association map from the <code>list-of-rules</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Association({ahey-&gt;avalue, bkey-&gt;bvalue, ckey-&gt;cvalue})
   * &lt;|akey-&gt;avalue,bkey-&gt;bvalue,ckey-&gt;cvalue|&gt;
   * </code>
   * </pre>
   *
   * <p>
   * <code>Association</code> is the head of associations:
   *
   * <pre>
   * <code>&gt;&gt; Head(&lt;|a -&gt; x, b -&gt; y, c -&gt; z|&gt;)
   * Association
   *
   * &gt;&gt; &lt;|a -&gt; x, b -&gt; y|&gt;
   * &lt;|a -&gt; x, b -&gt; y|&gt;
   *
   * &gt;&gt; Association({a -&gt; x, b -&gt; y})
   * &lt;|a -&gt; x, b -&gt; y|&gt;
   * </code>
   * </pre>
   *
   * <p>
   * Associations can be nested:
   *
   * <pre>
   * <code>&gt;&gt; &lt;|a -&gt; x, b -&gt; y, &lt;|a -&gt; z, d -&gt; t|&gt;|&gt;
   * &lt;|a -&gt; z, b -&gt; y, d -&gt; t|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="AssociationQ.md">AssociationQ</a>, <a href="Counts.md">Counts</a>,
   * <a href="Keys.md">Keys</a>, <a href="KeySort.md">KeySort</a>, <a href="Lookup.md">Lookup</a>,
   * <a href="Values.md">Values</a>
   */
  private static class Association extends AbstractEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAssociation()) {
        return F.NIL;
      }
      if (ast.head() != S.Association) {
        return F.NIL;
      }
      if (ast.isAST0()) {
        return F.assoc(F.List());
      } else if (ast.size() > 1) {
        IASTMutable assocList = F.NIL;
        boolean evaled = false;
        try {
          assocList = ast.copy();
          for (int i = 1; i < ast.size(); i++) {
            final IExpr arg = ast.get(i);
            if (!arg.isAssociation()) {
              final IExpr temp = engine.evaluateNIL(arg);
              if (temp.isPresent()) {
                evaled = true;
                assocList.set(i, temp);
              }
            }
          }

          IAssociation assoc = F.assoc();
          for (int i = 1; i < assocList.size(); i++) {
            final IExpr arg = assocList.get(i);
            // only a rule, a list of rules or an association defines the rules of the result; an
            // arbitrary head like `f(a->1)` isn't a valid element of an association
            if (arg.isRuleAST() || arg.isList() || arg.isAssociation()) {
              assoc.appendRules((IAST) arg);
            } else {
              return evaled ? assocList : F.NIL;
            }
          }
          return assoc;
        } catch (ValidateException ve) {
          Errors.printMessage(S.Association, ve, engine);
        }
        return evaled ? assocList : F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.head().isSymbol()) {
        ISymbol symbol = (ISymbol) leftHandSide.head();

        IExpr temp = symbol.assignedValue();
        if (temp == null) {
          // `1` is not a variable with a value, so its value cannot be changed.
          return Errors.printMessage(builtinSymbol, "rvalue", F.list(symbol), engine);
        } else {
          if (symbol.hasProtectedAttribute()) {
            // Symbol `1` is Protected.
            return Errors.printMessage(builtinSymbol, "wrsym", F.list(symbol), EvalEngine.get());
          }
          try {
            IExpr lhsHead = engine.evaluate(symbol);
            if (lhsHead.isAssociation()) {
              if (leftHandSide.isAST0()) {
                // `1` called with `2` arguments; 1 argument is expected.
                Errors.printMessage(builtinSymbol, "argx",
                    F.list(symbol, F.ZZ(leftHandSide.argSize())), engine);
                return rightHandSide;
              }
              IExpr assoc = assignKeys((IAssociation) lhsHead, (IAST) leftHandSide, 1,
                  rightHandSide, builtinSymbol, engine);
              if (assoc.isPresent()) {
                symbol.assignValue(assoc, false);
              }
              return rightHandSide;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(builtinSymbol, ve, engine);
          }
        }
      }
      Errors.printMessage(builtinSymbol, "setps", F.list(leftHandSide.head()), engine);
      return rightHandSide;
    }

    /**
     * Assign the <code>value</code> to the association for the keys which are given by the
     * arguments of the <code>keys</code> expression, starting at <code>keyPosition</code>.
     * <code>assoc(key1, key2,...) = value</code>
     *
     * <p>
     * If <code>keyPosition</code> points to the last key, an existing rule will be updated or a new
     * <code>key->value</code> rule will be appended to the association. Otherwise the key must
     * already be mapped to a nested association, because an intermediate key can't be created
     * automatically.
     *
     * <p>
     * The keys are used as they are. A <code>Key(...)</code> expression isn't unwrapped, because an
     * association which is applied to arguments uses these arguments as the keys. This is different
     * from the <code>assoc[[key1, key2,...]] = value</code> assignment.
     *
     * @param assoc the association in which the value should be assigned
     * @param keys the left-hand-side of the assignment, whose arguments are the keys
     * @param keyPosition the position of the current key in <code>keys</code>
     * @param value
     * @param builtinSymbol
     * @param engine the evaluation engine
     * @return {@link F#NIL} if the assignment couldn't be made
     */
    private static IExpr assignKeys(IAssociation assoc, IAST keys, int keyPosition, IExpr value,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      final IExpr key = engine.evaluate(keys.get(keyPosition));
      if (keyPosition == keys.argSize()) {
        IAssociation result = assoc.copy();
        result.appendRule(F.Rule(key, value));
        return result;
      }
      final int index = assoc.getRulePosition(key);
      final IExpr nested = index == 0 ? F.NIL : assoc.getValue(index);
      if (!nested.isAssociation()) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(builtinSymbol, "partw", F.list(key, assoc), engine);
      }
      IExpr temp =
          assignKeys((IAssociation) nested, keys, keyPosition + 1, value, builtinSymbol, engine);
      if (temp.isNIL()) {
        return F.NIL;
      }
      IAssociation result = assoc.copy();
      result.setValue(index, temp);
      return result;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>AssociationMap(header, &lt;|k1-&gt;v1, k2-&gt;v2,...|&gt;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create an association <code>&lt;|header(k1-&gt;v1), header(k2-&gt;v2),...|&gt;</code> with the
   * rules mapped by the <code>header</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>AssociationMap(header, {k1, k2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create an association <code>&lt;|k1-&gt;header(k1), k2-&gt;header(k2),...|&gt;</code> with the
   * rules mapped by the <code>header</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; AssociationMap(Reverse,&lt;|U-&gt;1,V-&gt;2|&gt;)
   * &lt;|1-&gt;U,2-&gt;V|&gt;
   *
   * &gt;&gt; AssociationMap(f,{U,V})
   * &lt;|U-&gt;f(U),V-&gt;f(V)|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="AssociationQ.md">AssociationQ</a>,
   * <a href="AssociationThread.md">AssociationThread</a>, <a href="Counts.md">Counts</a>,
   * <a href="Lookup.md">Lookup</a>, <a href="KeyExistsQ.md">KeyExistsQ</a>,
   * <a href="Keys.md">Keys</a>, <a href="KeySort.md">KeySort</a>, <a href="Values.md">Values</a>
   */
  private static class AssociationMap extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        return associationMap(S.Rule, arg1, arg2, engine);
      }
      return F.NIL;
    }

    private static IExpr associationMap(ISymbol symbol, IExpr arg1, IExpr arg2, EvalEngine engine) {
      if (arg2.isList()) {
        IAST list2 = (IAST) arg2;
        IAssociation result = F.assoc(); // list2.size());
        for (int i = 1; i < list2.size(); i++) {
          final IExpr function = engine.evaluate(F.unaryAST1(arg1, list2.get(i)));
          result.appendRule(F.binaryAST2(symbol, list2.get(i), function));
        }
        return result;
      }
      if (arg2.isAssociation()) {
        IAssociation list2 = (IAssociation) arg2;
        IASTAppendable result = F.ast(S.Association, list2.size());
        for (int i = 1; i < list2.size(); i++) {
          final IExpr function = engine.evaluate(F.unaryAST1(arg1, list2.getRule(i)));
          result.appendRule(function);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>AssociationThread({k1,k2,...}, {v1,v2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create an association with rules from the keys <code>{k1,k2,...}</code> and values <code>
   * {v1,v2,...}</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>AssociationThread({k1,k2,...} -&gt; {v1,v2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create an association with rules from the keys <code>{k1,k2,...}</code> and values <code>
   * {v1,v2,...}</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; AssociationThread({&quot;U&quot;,&quot;V&quot;},{1,2})
   * &lt;|U-&gt;1,V-&gt;2|&gt;
   *
   * &gt;&gt; AssociationThread({&quot;U&quot;,&quot;V&quot;} :&gt; {1,2})
   * &lt;|U:&gt;1,V:&gt;2|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="AssociateTo.md">AssociateTo</a>, <a href="Association.md">Association</a>,
   * <a href="AssociationQ.md">AssociationQ</a>, <a href="AssociationMap.md">AssociationMap</a>,
   * <a href="Counts.md">Counts</a>, <a href="Lookup.md">Lookup</a>,
   * <a href="KeyExistsQ.md">KeyExistsQ</a>, <a href="Keys.md">Keys</a>,
   * <a href="KeySort.md">KeySort</a>, <a href="Values.md">Values</a>
   */
  private static class AssociationThread extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        return associationThread(S.Rule, arg1, arg2);
      }
      if (arg1.isRuleAST()) {
        IAST rule = (IAST) arg1;

        return associationThread((ISymbol) rule.head(), rule.arg1(), rule.arg2());
      }
      return F.NIL;
    }

    private static IExpr associationThread(ISymbol symbol, IExpr arg1, IExpr arg2) {
      if (arg1.isList() && arg2.isList()) {
        if (arg1.size() == arg2.size()) {
          final IAST list1 = (IAST) arg1;
          final IAST list2 = (IAST) arg2;
          final IASTAppendable listOfRules =
              F.mapRange(1, list1.size(), i -> F.binaryAST2(symbol, list1.get(i), list2.get(i)));
          return F.assoc(listOfRules);
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
   * <code>Counts({elem1, elem2, elem3, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * count the number of each distinct element in the list <code>{elem1, elem2, elem3, ...}
   * </code> and return the result as an association <code>&lt;|elem1-&gt;counter1, ...|&gt;</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Counts({1,2,3,4,5,6,7,8,9,7,5,4,5,6,7,3,2,1,3,4,5,2,2,2,3,3,3,3,3})
   * &lt;|1-&gt;2,2-&gt;5,3-&gt;8,4-&gt;3,5-&gt;4,6-&gt;2,7-&gt;3,8-&gt;1,9-&gt;1|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Commonest.md">Commonest</a>, <a href="Tally.md">Tally</a>
   */
  private static class Counts extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        Map<IExpr, MutableInt> histogram = MutableInt.createHistogram(list);
        IAssociation assoc = F.assoc();
        for (Map.Entry<IExpr, MutableInt> elem : histogram.entrySet()) {
          assoc.appendRule(F.Rule(elem.getKey(), F.ZZ(elem.getValue().value())));
        }
        return assoc;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class KeyDrop extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isListOrAssociation()) {
          final IAST arg1 = (IAST) ast.arg1();
          if (arg1.forAll(x -> x.isListOfRulesOrAssociation(true))) {
            return arg1.mapThread(ast, 1);
          }
          if (arg1.isListOfRulesOrAssociation(true)) {
            IAST arg2 = ast.arg2().makeList();
            IAssociation assoc = F.NIL;
            if (arg1.isAssociation()) {
              assoc = (IAssociation) arg1;
            } else {
              assoc = F.assoc(arg1);
            }

            if (assoc.isPresent()) {
              return keyDrop(assoc, arg2);
            }
          }
        }
        // The argument `1` is not a valid Association or a list of rules.
        return Errors.printMessage(ast.topHead(), "invrl", F.List(ast.arg1()), engine);
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(ast.topHead(), rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class KeyExistsQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg1.isAssociation()) {
          return ((IAssociation) arg1).isKey(arg2) ? S.True : S.False;
        }
        if (arg1.isListOfRules(true)) {
          IAST listOfRules = (IAST) arg1;
          for (int i = 1; i < listOfRules.size(); i++) {
            final IExpr rule = listOfRules.get(i);
            if (rule.isRuleAST()) {
              if (arg2.equals(rule.first())) {
                return S.True;
              }
            }
          }
        }
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_2;
    }
  }

  private static class Key extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().isKey()) {
        if (ast.isAST1() && ast.arg1().isAssociation()) {
          IExpr key = ast.head().first();
          IAssociation arg1 = (IAssociation) ast.arg1();
          IAST rule = arg1.getRule(key);
          if (rule.isPresent()) {
            return rule.second();
          }
          return F.Missing(S.KeyAbsent, key);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1_0;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Keys(association)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return a list of keys of the <code>association</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Keys(&lt;|ahey-&gt;avalue, bkey-&gt;bvalue, ckey-&gt;cvalue|&gt;)
   * {ahey,bkey,ckey}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="AssociationQ.md">AssociationQ</a>,
   * <a href="Counts.md">Counts</a>, <a href="Lookup.md">Lookup</a>,
   * <a href="KeySort.md">KeySort</a>, <a href="Values.md">Values</a>
   */
  private static class Keys extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
      if (arg1.isAssociation()) {
        IASTMutable list = ((IAssociation) arg1).keys();
        return mapHeadIfPresent(list, head);
      } else if (arg1.isDataset()) {
        return ((IASTDataset) arg1).columnNames();
      } else if (arg1.isRuleAST()) {
        if (head.isPresent()) {
          return F.unaryAST1(head, arg1.first());
        }
        return arg1.first();
      } else if (arg1.isList()) {
        if (arg1.isListOfRules(true)) {
          IAST listOfRules = (IAST) arg1;
          IASTAppendable list = F.ast(S.List, listOfRules.argSize());
          for (int i = 1; i < listOfRules.size(); i++) {
            final IExpr rule = listOfRules.get(i);
            if (rule.isRuleAST()) {
              list.append(rule.first());
            } else if (rule.isEmptyList()) {
              list.append(rule);
            } else {
              // The argument `1` is not a vaild Association or list of rules.
              throw new ArgumentTypeException("invrl", F.list(rule));
            }
          }
          return mapHeadIfPresent(list, head);
        }

        // thread over Lists in first argument
        return arg1.mapThread(ast.setAtCopy(1, F.Slot1), 1);
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
   * <code>KeySelect(&lt;|key1-&gt;value1, ...|&gt;, head)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an association of the elements for which <code>head(keyi)</code> returns <code>True
   * </code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>KeySelect({key1-&gt;value1, ...}, head)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an association of the elements for which <code>head(keyi)</code> returns <code>True
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; r = {beta -&gt; 4, alpha -&gt; 2, x -&gt; 4, z -&gt; 2, w -&gt; 0.8};
   *
   * &gt;&gt; KeySelect(r, MatchQ(#,alpha|x)&amp;)
   * &lt;|alpha-&gt;2,x-&gt;4|&gt;
   * </code>
   * </pre>
   */
  private static final class KeySelect extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (size == 3) {
        IExpr arg1 = ast.arg1();
        if (arg1.isListOfRulesOrAssociation(false)) {
          IAST list = (IAST) arg1;
          IExpr predicateHead = ast.arg2();
          return keySelect(list, x -> engine.evalTrue(predicateHead, x));
        }
        // The argument `1` is not a valid Association or a list of rules.
        return Errors.printMessage(ast.topHead(), "invrl", F.list(arg1), engine);
      }
      return F.NIL;
    }

    private IAST keySelect(IAST assoc, Predicate<? super IExpr> predicate) {
      int[] items = new int[assoc.size()];
      int length = 0;
      for (int i = 1; i < assoc.size(); i++) {
        final IAST rule = (IAST) assoc.getRule(i);
        if (predicate.test(rule.first())) {
          items[length++] = i;
        }
      }
      if (length == assoc.argSize()) {
        return assoc;
      }
      IAssociation result = F.assoc();
      if (length > 0) {
        for (int i = 0; i < length; i++) {
          result.appendRule(assoc.getRule(items[i]));
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>KeySort(&lt;|key1-&gt;value1, ...|&gt;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sort the <code>&lt;|key1-&gt;value1, ...|&gt;</code> entries by the <code>key</code> values.
   *
   * </blockquote>
   *
   * <pre>
   * <code>KeySort(&lt;|key1-&gt;value1, ...|&gt;, comparator)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sort the entries by the <code>comparator</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; KeySort(&lt;|2 -&gt; y, 3 -&gt; z, 1 -&gt; x|&gt;)
   * &lt;|1-&gt;x,2-&gt;y,3-&gt;z|&gt;
   *
   * &gt;&gt; KeySort(&lt;|2 -&gt; y, 3 -&gt; z, 1 -&gt; x|&gt;, Greater)
   * &lt;|3-&gt;z,2-&gt;y,1-&gt;x|&gt;
   * </code>
   * </pre>
   */
  private static class KeySort extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isASTOrAssociation()) {
        IAST arg1 = (IAST) ast.arg1();
        if (arg1.isAssociation()) {
          if (ast.isAST2()) {
            return ((IAssociation) arg1).keySort(new Predicates.IsBinaryFalse(ast.arg2()));
          }
          return ((IAssociation) arg1).keySort();
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
   * <code>LetterCounts(string)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * count the number of each distinct character in the <code>string</code> and return the result as
   * an association <code>&lt;|char-&gt;counter1, ...|&gt;</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href=
   * "https://en.wikipedia.org/wiki/The_quick_brown_fox_jumps_over_the_lazy_dog">Wikipedia - The
   * quick brown fox jumps over the lazy dog</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; LetterCounts(&quot;The quick brown fox jumps over the lazy dog&quot;) // InputForm
   * &lt;|&quot;T&quot;-&gt;1,&quot; &quot;-&gt;8,&quot;a&quot;-&gt;1,&quot;b&quot;-&gt;1,&quot;c&quot;-&gt;1,&quot;d&quot;-&gt;1,&quot;e&quot;-&gt;3,&quot;f&quot;-&gt;1,&quot;g&quot;-&gt;1,&quot;h&quot;-&gt;2,&quot;i&quot;-&gt;1,&quot;j&quot;-&gt;1,
   * &quot;k&quot;-&gt;1,&quot;l&quot;-&gt;1,&quot;m&quot;-&gt;1,&quot;n&quot;-&gt;1,&quot;o&quot;-&gt;4,&quot;p&quot;-&gt;1,&quot;q&quot;-&gt;1,&quot;r&quot;-&gt;2,&quot;s&quot;-&gt;1,&quot;t&quot;-&gt;1,&quot;u&quot;-&gt;2,&quot;v&quot;-&gt;1,&quot;w&quot;-&gt;1,&quot;x&quot;-&gt;1,&quot;y&quot;-&gt;1,&quot;z&quot;-&gt;1|&gt;
   * </code>
   * </pre>
   */
  private static class LetterCounts extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        String str = arg1.toString();
        HashMap<Character, MutableInt> map = new HashMap<Character, MutableInt>();
        for (int i = 0; i < str.length(); i++) {
          map.compute(str.charAt(i), //
              (k, v) -> (v == null) ? new MutableInt(1) : v.increment());
        }
        IAssociation assoc = F.assoc();
        for (Map.Entry<Character, MutableInt> elem : map.entrySet()) {
          assoc.appendRule(F.Rule(F.$str(elem.getKey()), F.ZZ(elem.getValue().value())));
        }
        return assoc;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Lookup(association, key)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the value in the <code>association</code> which is associated with the <code>key
   * </code>. If no value is available return <code>Missing(&quot;KeyAbsent&quot;,key)</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Lookup(association, key, defaultValue)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the value in the <code>association</code> which is associated with the <code>key
   * </code>. If no value is available return <code>defaultValue</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Lookup(&lt;|a -&gt; 11, b -&gt; 17|&gt;, a)
   * 11
   *
   * &gt;&gt; Lookup(&lt;|a -&gt; 1, b -&gt; 2|&gt;, c)
   * Missing(KeyAbsent,c)
   *
   * &gt;&gt; Lookup(&lt;|a -&gt; 1, b -&gt; 2|&gt;, c, 42)
   * 42
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="AssociationQ.md">AssociationQ</a>,
   * <a href="Counts.md">Counts</a>, <a href="Keys.md">Keys</a>, <a href="KeySort.md">KeySort</a>,
   * <a href="Values.md">Values</a>
   */
  private static class Lookup extends AbstractEvaluator implements ICoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isList()) {
        if (ast.size() > 2) {
          if (arg1.isListOfRules(true)) {
            IExpr key = engine.evaluate(ast.arg2());
            if (key.isList()) {
              return key.mapThread(ast, 2);
            }
            if (key.isKey()) {
              key = key.first();
            }
            IAST listOfRules = (IAST) arg1;
            for (int i = 1; i < listOfRules.size(); i++) {
              final IExpr rule = listOfRules.get(i);
              if (rule.isRuleAST()) {
                if (rule.first().equals(key)) {
                  return rule.second();
                }
              }
            }
            if (ast.isAST3()) {
              return engine.evaluate(ast.arg3());
            }
            return F.Missing(F.stringx("KeyAbsent"), key);
          }
        }
        return ((IAST) arg1).mapThread(ast, 1);
      } else if (arg1.isAssociation()) {
        if (ast.isAST2()) {
          IExpr key = engine.evaluate(ast.arg2());
          if (key.isList()) {
            return ((IAST) key).mapThread(ast, 2);
          }
          if (key.isKey()) {
            key = key.first();
          }

          return ((IAssociation) arg1).getValue(key);
        }
        if (ast.isAST3()) {
          IExpr key = engine.evaluate(ast.arg2());
          if (key.isList()) {
            return ((IAST) key).mapThread(ast, 2);
          }
          if (key.isKey()) {
            key = key.first();
          }
          final IExpr arg3 = ast.arg3();
          return ((IAssociation) arg1).getValue(key, () -> engine.evaluate(arg3));
        }
      } else {
        // The argument `1` is not a valid Association or list of rules.
        return Errors.printMessage(ast.topHead(), "invrl", F.List(), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
    }
  }

  private static class Merge extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        Map<IExpr, IASTAppendable> map = new LinkedHashMap<>();
        if (collectMergeRules((IAST) arg1, map, engine)) {
          IAssociation result = F.assoc();
          IExpr head = ast.arg2();
          for (Map.Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
            result.appendRule(
                F.Rule(entry.getKey(), engine.evaluate(F.unaryAST1(head, entry.getValue()))));
          }
          return result;
        }
        return F.NIL;
      }
      // The argument `1` is not a valid list of Associations or rules or list of rules.
      return Errors.printMessage(S.Merge, "list1", F.list(arg1), engine);
    }

    private static boolean collectMergeRules(final IAST list, Map<IExpr, IASTAppendable> map,
        EvalEngine engine) {
      for (int i = 1; i < list.size(); i++) {
        IExpr temp = list.get(i);
        if (temp.isRuleAST()) {
          IAST rule = (IAST) temp;
          map.computeIfAbsent(rule.arg1(), k -> F.ListAlloc()).append(rule.arg2());
        } else if (temp.isAssociation()) {
          IAssociation assoc = (IAssociation) temp;
          for (int j = 1; j < assoc.size(); j++) {
            IAST rule = assoc.getRule(j);
            map.computeIfAbsent(rule.arg1(), k -> F.ListAlloc()).append(rule.arg2());
          }
        } else if (temp.isList()) {
          if (!collectMergeRules((IAST) temp, map, engine)) {
            return false;
          }
        } else {
          // The argument `1` is not a valid list of Associations or rules or list of rules.
          Errors.printMessage(S.Merge, "list1", F.list(temp), engine);
          return false;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Query(levelspec1, levelspec2, ...)[expr]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * apply the level specifications <code>levelspec1, levelspec2, ...</code> to the successive
   * levels of the association or list <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * A level specification either <i>descends</i> into the parts of the current level or is an
   * <i>operator</i> which is applied to the result of the deeper levels:
   *
   * <ul>
   * <li><code>All</code> - descend into all parts of the level and preserve the structure of the
   * level. The keys of an association are preserved.
   * <li><code>"key"</code>, <code>Key(key)</code>, <code>i</code>, <code>i;;j</code> or a list of
   * these part specifications - descend into these parts of the level.
   * <li>any other expression <code>f</code> - descend into the parts of the level with the
   * remaining level specifications and apply <code>f</code> to the result. Aggregating functions
   * like <code>Total</code> or <code>Mean</code> can be used here.
   * </ul>
   *
   * <p>
   * <code>Query()[expr]</code> returns <code>expr</code> unchanged.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Query(All, &quot;a&quot;)[{&lt;|&quot;a&quot; -&gt; 1, &quot;b&quot; -&gt; 2|&gt;, &lt;|&quot;a&quot; -&gt; 3, &quot;b&quot; -&gt; 4|&gt;}]
   * {1,3}
   *
   * &gt;&gt; Query(Total, &quot;a&quot;)[{&lt;|&quot;a&quot; -&gt; 1|&gt;, &lt;|&quot;a&quot; -&gt; 3|&gt;}]
   * 4
   *
   * &gt;&gt; Query(All, Total)[&lt;|&quot;x&quot; -&gt; {1, 2}, &quot;y&quot; -&gt; {3, 4}|&gt;]
   * &lt;|x-&gt;3,y-&gt;7|&gt;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="Key.md">Key</a>,
   * <a href="Lookup.md">Lookup</a>, <a href="Part.md">Part</a>
   */
  private static class Query extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.head().isAST(S.Query)) {
        // the operator form Query(levelspec1, levelspec2, ...)[expr]
        return query((IAST) ast.head(), 1, ast.arg1(), engine);
      }
      return F.NIL;
    }

    /**
     * Apply the level specifications of a <code>Query(...)</code> operator, starting at
     * <code>position</code>, to <code>expr</code>.
     *
     * @param levelSpecs the <code>Query(...)</code> operator, whose arguments are the level
     *        specifications
     * @param position the position of the level specification which should be applied to
     *        <code>expr</code>
     * @param expr the current level of the queried expression
     * @param engine the evaluation engine
     * @return {@link F#NIL} if the query couldn't be applied
     */
    private static IExpr query(IAST levelSpecs, int position, IExpr expr, EvalEngine engine) {
      if (position >= levelSpecs.size()) {
        // no more level specifications - the current level is the result
        return expr;
      }
      final IExpr levelSpec = levelSpecs.get(position);
      if (levelSpec.equals(S.All)) {
        return queryLevel(levelSpecs, position + 1, expr, engine);
      }
      if (isPartSpecification(levelSpec)) {
        if (!isApplicablePart(levelSpec, expr)) {
          return F.NIL;
        }
        final IAST partAST = F.Part(expr, levelSpec);
        IExpr part = engine.evaluate(partAST);
        if (part.equals(partAST)) {
          // the part couldn't be extracted
          return F.NIL;
        }
        return query(levelSpecs, position + 1, part, engine);
      }
      // an operator - descend into the parts of this level first and apply the operator to the
      // result of the deeper levels
      IExpr result = expr;
      if (position + 1 < levelSpecs.size()) {
        result = queryLevel(levelSpecs, position + 1, expr, engine);
        if (result.isNIL()) {
          return F.NIL;
        }
      }
      return engine.evaluate(F.unaryAST1(levelSpec, result));
    }

    /**
     * Apply the remaining level specifications to every part of <code>expr</code> and preserve the
     * structure of <code>expr</code>. The keys of an association are preserved.
     *
     * @param levelSpecs the <code>Query(...)</code> operator, whose arguments are the level
     *        specifications
     * @param position the position of the level specification for the parts of <code>expr</code>
     * @param expr the current level of the queried expression
     * @param engine the evaluation engine
     * @return {@link F#NIL} if <code>expr</code> has no parts to descend into
     */
    private static IExpr queryLevel(IAST levelSpecs, int position, IExpr expr, EvalEngine engine) {
      if (expr.isAssociation()) {
        IAssociation assoc = (IAssociation) expr;
        IAssociation result = assoc.copy();
        for (int i = 1; i < assoc.size(); i++) {
          IExpr value = query(levelSpecs, position, assoc.getValue(i), engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          result.setValue(i, value);
        }
        return result;
      }
      if (expr.isList()) {
        IAST list = (IAST) expr;
        IASTAppendable result = list.copyAppendable();
        for (int i = 1; i < list.size(); i++) {
          IExpr value = query(levelSpecs, position, list.get(i), engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          result.set(i, value);
        }
        return result;
      }
      // there are no parts to descend into
      return F.NIL;
    }

    /**
     * Test if the level specification descends into the parts of the current level: an association
     * key given as a string or a <code>Key(...)</code> expression, an integer index, a
     * <code>Span</code> or a list of these part specifications.
     *
     * @param levelSpec
     * @return <code>false</code> if the level specification is an operator which should be applied
     *         to the result of the deeper levels
     */
    private static boolean isPartSpecification(IExpr levelSpec) {
      if (levelSpec.isString() || levelSpec.isInteger() || levelSpec.isKey()
          || levelSpec.isAST(S.Span)) {
        return true;
      }
      if (levelSpec.isList()) {
        return ((IAST) levelSpec)
            .forAll(x -> x.isString() || x.isInteger() || x.isKey() || x.isAST(S.Span));
      }
      return false;
    }

    /**
     * Test if the part specification can be applied to the current level at all. An association key
     * can only be extracted from an association.
     *
     * @param levelSpec a part specification
     * @param expr the current level of the queried expression
     * @return <code>false</code> if the part can't be extracted from <code>expr</code>
     */
    private static boolean isApplicablePart(IExpr levelSpec, IExpr expr) {
      if (!expr.isASTOrAssociation()) {
        return false;
      }
      if (levelSpec.isList()) {
        return ((IAST) levelSpec).forAll(x -> isApplicablePart(x, expr));
      }
      return (levelSpec.isString() || levelSpec.isKey()) ? expr.isAssociation() : true;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static class Structure extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isDataset()) {
        return ((IASTDataset) arg1).structure();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>KeyTake(&lt;|key1-&gt;value1, ...|&gt;, {k1, k2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an association of the rules for which the <code>k1, k2,...</code> are keys in the
   * association.
   *
   * </blockquote>
   *
   * <pre>
   * <code>KeySelect({key1-&gt;value1, ...}, {k1, k2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an association of the rules for which the <code>k1, k2,...</code> are keys in the
   * association.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; r = {beta -&gt; 4, alpha -&gt; 2, x -&gt; 4, z -&gt; 2, w -&gt; 0.8};
   *
   * &gt;&gt; KeyTake(r, {alpha,x})
   * &lt;|alpha-&gt;2,x-&gt;4|&gt;
   * </code>
   * </pre>
   */
  private static final class KeyTake extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isListOfRulesOrAssociation(true) || ast.arg1().isListOfLists()) {
          final IAST arg1 = (IAST) ast.arg1();
          if (arg1.forAll(x -> x.isListOfRulesOrAssociation(true))) {
            return arg1.mapThread(ast, 1);
          }
          IAST arg2 = ast.arg2().makeList();

          return keyTake(arg1, arg2);
        } else {
          // The argument `1` is not a valid Association or a list of rules.
          return Errors.printMessage(ast.topHead(), "invrl", F.List(ast.arg1()), engine);
        }
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(ast.topHead(), rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    private static IAST keyTake(final IAST expr, final IAST list) {
      final int size = list.size();
      // final IASTAppendable assoc = F.assoc(expr);
      final IASTAppendable resultAssoc = F.assoc(); // 10 > size ? size : 10);
      for (int i = 1; i < size; i++) {
        final IExpr rule = expr.getRule(list.get(i));
        if (rule.isPresent()) {
          resultAssoc.appendRule(rule);
        }
      }
      return resultAssoc;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class KeyValueMap extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return keyValueMap(ast.arg1(), engine.evaluate(ast.arg2()), engine);
    }

    private static IExpr keyValueMap(IExpr f, IExpr arg2, EvalEngine engine) {
      if (arg2.isAssociation()) {
        IAssociation assoc = (IAssociation) arg2;
        IASTAppendable result = F.ListAlloc(assoc.argSize());
        for (int i = 1; i < assoc.size(); i++) {
          IAST rule = assoc.getRule(i);
          result.append(F.binaryAST2(f, rule.arg1(), rule.arg2()));
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_2;
    }
  }
  private static class Summary extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isDataset()) {
        return ((IASTDataset) arg1).summary();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Values(association)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return a list of values of the <code>association</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Values(&lt;|ahey-&gt;avalue, bkey-&gt;bvalue, ckey-&gt;cvalue|&gt;)
   * {avalue,bvalue,cvalue}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Association.md">Association</a>, <a href="AssociationQ.md">AssociationQ</a>,
   * <a href="Counts.md">Counts</a>, <a href="Keys.md">Keys</a>, <a href="KeySort.md">KeySort</a>,
   * <a href="Lookup.md">Lookup</a>
   */
  private static class Values extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
      if (arg1.isAssociation()) {
        IASTMutable list = ((IAssociation) arg1).values();
        return mapHeadIfPresent(list, head);
      } else if (arg1.isRuleAST()) {
        if (head.isPresent()) {
          return F.unaryAST1(head, arg1.second());
        }
        return arg1.second();
      } else if (arg1.isList()) {
        if (arg1.isListOfRules(true)) {
          IAST listOfRules = (IAST) arg1;
          IASTAppendable list = F.mapList(listOfRules, rule -> {
            if (rule.isRuleAST()) {
              return rule.second();
            } else if (rule.isEmptyList()) {
              return rule;
            }
            return F.NIL;
          });
          return mapHeadIfPresent(list, head);
        }

        // thread over Lists in first argument
        return arg1.mapThread(ast.setAtCopy(1, F.Slot1), 1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

  }

  /**
   * Convert the given expression into an {@link IAssociation}, if it is an association or a list of
   * rules <code>{x0-&gt;y0, x1-&gt;y1, ... }</code>.
   *
   * @param expr
   * @return <code>null</code> if the expression is neither an association nor a list of rules
   */
  public static IAssociation toAssociation(final IExpr expr) {
    if (expr.isAssociation()) {
      return (IAssociation) expr;
    }
    if (expr.isListOfRules(false)) {
      return F.assoc((IAST) expr);
    }
    return null;
  }

  private static IAST keyDrop(final IAssociation list1, final IAST list2) {
    IAssociation assoc = list1;
    final IAssociation result = assoc.copyAppendable();
    final int size = list2.size();
    boolean evaled = false;
    for (int i = 1; i < size; i++) {
      final IExpr rule = result.removeRule(list2.get(i));
      if (rule.isPresent()) {
        evaled = true;
      }
    }
    return evaled ? result : assoc;
  }


  /**
   * If <code>head.isPresent()</code> map the <code>head</code> on each argument of list. Otherwise
   * return <code>list</code>.
   *
   * @param list
   * @param head
   * @return
   */
  private static IExpr mapHeadIfPresent(IASTMutable list, final IExpr head) {
    if (head.isPresent()) {
      return list.mapThread(x -> F.unaryAST1(head, x));
    }
    return list;
  }

  public static void initialize() {
    Initializer.init();
  }

  private AssociationFunctions() {}
}
