package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Function;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.CFalse;
import org.logicng.formulas.CTrue;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.transformations.cnf.BDDCNFTransformation;
import org.logicng.transformations.dnf.DNFFactorization;
import org.logicng.transformations.simplification.AdvancedSimplifier;
import org.logicng.transformations.simplification.DefaultRatingFunction;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBooleanFormula;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;
import org.matheclipse.core.tensor.qty.IQuantity;

public final class BooleanFunctions {
  public static final Equal CONST_EQUAL = new Equal();
  public static final Greater CONST_GREATER = new Greater();
  public static final Less CONST_LESS = new Less();
  public static final GreaterEqual CONST_GREATER_EQUAL = new GreaterEqual();
  public static final LessEqual CONST_LESS_EQUAL = new LessEqual();

  private static final int[] FULL_BITSETS =
      new int[] { //
        0b1, //
        0b11, //
        0b111, //
        0b1111, //
        0b11111, //
        0b111111, //
        0b1111111, //
        0b11111111, //
        0b111111111, //
        0b1111111111, //
        0b11111111111, //
        0b111111111111, //
        0b1111111111111, //
        0b11111111111111, //
        0b111111111111111 //
      };

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AllTrue.setEvaluator(new AllTrue());
      S.And.setEvaluator(new And());
      S.AnyTrue.setEvaluator(new AnyTrue());
      S.Boole.setEvaluator(new Boole());
      S.BooleanConvert.setEvaluator(new BooleanConvert());
      S.BooleanMinimize.setEvaluator(new BooleanMinimize());
      S.BooleanTable.setEvaluator(new BooleanTable());
      S.BooleanVariables.setEvaluator(new BooleanVariables());
      S.Equal.setEvaluator(CONST_EQUAL);
      S.EqualTo.setEvaluator(new CompareOperator(S.EqualTo, S.Equal));
      S.Equivalent.setEvaluator(new Equivalent());
      S.Exists.setEvaluator(new Exists());
      S.ForAll.setEvaluator(new ForAll());
      S.Greater.setEvaluator(CONST_GREATER);
      S.GreaterEqual.setEvaluator(new GreaterEqual());
      S.GreaterEqualThan.setEvaluator(new CompareOperator(S.GreaterEqualThan, S.GreaterEqual));
      S.GreaterThan.setEvaluator(new CompareOperator(S.GreaterThan, S.Greater));
      S.Implies.setEvaluator(new Implies());
      S.Inequality.setEvaluator(new Inequality());
      S.Less.setEvaluator(CONST_LESS);
      S.LessEqual.setEvaluator(new LessEqual());
      S.LessEqualThan.setEvaluator(new CompareOperator(S.LessEqualThan, S.LessEqual));
      S.LessThan.setEvaluator(new CompareOperator(S.LessThan, S.Less));
      S.LogicalExpand.setEvaluator(new LogicalExpand());
      S.Max.setEvaluator(new Max());
      S.Min.setEvaluator(new Min());
      S.MinMax.setEvaluator(new MinMax());
      S.Nand.setEvaluator(new Nand());
      S.Negative.setEvaluator(new Negative());
      S.NoneTrue.setEvaluator(new NoneTrue());
      S.NonNegative.setEvaluator(new NonNegative());
      S.NonPositive.setEvaluator(new NonPositive());
      S.Nor.setEvaluator(new Nor());
      S.Not.setEvaluator(new Not());
      S.Or.setEvaluator(new Or());
      S.Positive.setEvaluator(new Positive());
      S.SameQ.setEvaluator(new SameQ());
      S.SatisfiabilityCount.setEvaluator(new SatisfiabilityCount());
      S.SatisfiabilityInstances.setEvaluator(new SatisfiabilityInstances());
      S.SatisfiableQ.setEvaluator(new SatisfiableQ());
      S.TautologyQ.setEvaluator(new TautologyQ());
      S.TrueQ.setEvaluator(new TrueQ());
      S.Unequal.setEvaluator(new Unequal());
      S.UnequalTo.setEvaluator(new CompareOperator(S.UnequalTo, S.Unequal));
      S.UnsameQ.setEvaluator(new UnsameQ());
      S.Xor.setEvaluator(new Xor());
    }
  }

  private static final class LogicFormula {

    /**
     * Create a map which assigns the position of each variable name in the given <code>vars</code>
     * array to the corresponding variable name.
     *
     * @param vars an array of variables
     * @return
     */
    public static Map<String, Integer> name2Position(Variable[] vars) {
      Map<String, Integer> map = new HashMap<String, Integer>();
      for (int i = 0; i < vars.length; i++) {
        map.put(vars[i].name(), i);
      }
      return map;
    }

    final FormulaFactory factory;

    Map<IExpr, Variable> symbol2variableMap = new HashMap<IExpr, Variable>();
    Map<Variable, IExpr> variable2symbolMap = new HashMap<Variable, IExpr>();

    public LogicFormula() {
      this(new FormulaFactory());
    }

    public LogicFormula(FormulaFactory factory) {
      this.factory = factory;
    }

    public Variable[] ast2Variable(final IAST listOfSymbols) {
      if (listOfSymbols instanceof IAST) {
        Variable[] result = new Variable[listOfSymbols.argSize()];
        for (int i = 1; i < listOfSymbols.size(); i++) {
          final IExpr arg = listOfSymbols.get(i);
          if (arg.isSymbol()) {
            ISymbol symbol = (ISymbol) arg;
            if (symbol.isFalse() || symbol.isTrue()) {
              // `1` is not a valid variable.
              String str = IOFunctions.getMessage("ivar", F.List(symbol), EvalEngine.get());
              throw new ArgumentTypeException(str);
            }
            Variable v = symbol2variableMap.get(symbol);
            if (v == null) {
              final Variable value = factory.variable(symbol.getSymbolName());
              symbol2variableMap.put(symbol, value);
              variable2symbolMap.put(value, symbol);
              result[i - 1] = value;
            } else {
              result[i - 1] = v;
            }
          } else {
            // `1` is not a valid variable.
            String str = IOFunctions.getMessage("ivar", F.List(arg), EvalEngine.get());
            throw new ArgumentTypeException(str);
          }
        }
        return result;
      }

      // `1` is not a valid variable.
      String str = IOFunctions.getMessage("ivar", F.List(listOfSymbols), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }

    /**
     * Convert the {@link Formula} back to a {@link IExpr}.
     *
     * @param formula
     * @return
     * @see #expr2BooleanFunction(IExpr, boolean)
     */
    public IExpr booleanFunction2Expr(final Formula formula) {
      if (formula instanceof org.logicng.formulas.And) {
        org.logicng.formulas.And a = (org.logicng.formulas.And) formula;
        IExpr[] result = new IExpr[a.numberOfOperands()];
        int i = 0;
        for (Formula f : a) {
          result[i++] = booleanFunction2Expr(f);
        }
        Arrays.sort(result, Comparators.CANONICAL_COMPARATOR);
        return F.And(result);
      } else if (formula instanceof org.logicng.formulas.Or) {
        org.logicng.formulas.Or a = (org.logicng.formulas.Or) formula;
        IExpr[] result = new IExpr[a.numberOfOperands()];
        int i = 0;
        for (Formula f : a) {
          result[i++] = booleanFunction2Expr(f);
        }
        Arrays.sort(result, Comparators.CANONICAL_COMPARATOR);
        return F.Or(result);
      } else if (formula instanceof org.logicng.formulas.Not) {
        org.logicng.formulas.Not a = (org.logicng.formulas.Not) formula;
        return F.Not(booleanFunction2Expr(a.operand()));
      } else if (formula instanceof CFalse) {
        return S.False;
      } else if (formula instanceof CTrue) {
        return S.True;
      } else if (formula instanceof Literal) {
        // also used for Variable
        Literal a = (Literal) formula;
        if (a.phase()) {
          return mapToSymbol(a.variable());
        }
        return F.Not(mapToSymbol(a.variable()));
      }
      // illegal arguments: \"`1`\" in `2`
      String str =
          IOFunctions.getMessage(
              "argillegal",
              F.List(F.stringx(formula.toString()), F.stringx("LogicFormula")),
              EvalEngine.get());
      throw new ArgumentTypeException(str);
    }

    private IExpr mapToSymbol(Variable v) {
      IExpr s = variable2symbolMap.get(v);
      if (s != null) {
        return s;
      }
      EvalEngine engine = EvalEngine.get();
      // long moduleCounter = engine.incModuleCounter();
      // final String varAppend = "LF" + "$" + moduleCounter;
      s = F.Dummy(engine.uniqueName("LF$"));
      variable2symbolMap.put(v, s);
      return s;
    }

    private Formula convertEquivalent(IAST ast, boolean substituteExpressions) {
      Formula[] result1 = new Formula[ast.argSize()];
      Formula[] result2 = new Formula[ast.argSize()];
      for (int i = 1; i < ast.size(); i++) {
        result1[i - 1] = factory.not(expr2BooleanFunction(ast.get(i), substituteExpressions));
        result2[i - 1] = factory.not(result1[i - 1]);
      }
      return factory.or(factory.and(result1), factory.and(result2));
    }

    // private Formula convertXor(IAST ast, boolean substituteExpressions) {
    // Formula arg1 = expr2BooleanFunction(ast.arg1(), substituteExpressions);
    // Formula arg2 = expr2BooleanFunction(ast.arg2(), substituteExpressions);if(ast.size()>3)
    // {
    // IASTAppendable clone = ast.copyAppendable();
    // clone.remove(1);
    // arg2 = convertXor(clone);
    // }return factory.or(factory.and(arg1,factory.not(arg2)),factory.and(factory.not(arg1),arg2));
    // }

    /**
     * Convert the {@link IExpr} to a LogicNG {@link Formula}.
     *
     * @param logicExpr the expression which should be converted
     * @param substituteExpressions if <code>false</code> substitute only symbols with a virtual new
     *     {@link FormulaFactory#variable(String)} variable. if <code>true</code> substitute non
     *     boolean expressions with a virtual new {@link FormulaFactory#variable(String)} variable
     *     by the string of their {@link IExpr#fullFormString()}
     * @return
     * @throws ArgumentTypeException
     * @see {@link #booleanFunction2Expr(Formula)}
     */
    public Formula expr2BooleanFunction(final IExpr logicExpr, boolean substituteExpressions)
        throws ArgumentTypeException {
      if (logicExpr instanceof IAST) {
        final IAST ast = (IAST) logicExpr;
        int functionID = ast.headID();
        if (functionID > ID.UNKNOWN) {
          switch (functionID) {
            case ID.And:
              if (ast.isAnd()) {
                return convertAnd(ast, substituteExpressions);
              }
              break;
            case ID.Or:
              if (ast.isOr()) {
                return convertOr(ast, substituteExpressions);
              }
              break;
            case ID.Nand:
              if (ast.isSameHeadSizeGE(S.Nand, 3)) {
                final Formula[] result = new Formula[ast.argSize()];
                ast.forEach(
                    (x, i) -> {
                      result[i - 1] = factory.not(expr2BooleanFunction(x, substituteExpressions));
                    });
                // for (int i = 1; i < ast.size(); i++) {
                // result[i - 1] = factory.not(expr2BooleanFunction(ast.get(i)));
                // }
                return factory.or(result);
              }
              break;
            case ID.Nor:
              if (ast.isSameHeadSizeGE(S.Nor, 3)) {
                Formula[] result = new Formula[ast.argSize()];
                ast.forEach(
                    (x, i) -> {
                      result[i - 1] = factory.not(expr2BooleanFunction(x, substituteExpressions));
                    });
                // for (int i = 1; i < ast.size(); i++) {
                // result[i - 1] = factory.not(expr2BooleanFunction(ast.get(i)));
                // }
                return factory.and(result);
              }
              break;
            case ID.Equivalent:
              if (ast.isSameHeadSizeGE(S.Equivalent, 3)) {
                return convertEquivalent(ast, substituteExpressions);
              }
              break;
            case ID.Xor:
              if (ast.isSameHeadSizeGE(S.Xor, 3)) {
                IAST dnf = xorToDNF(ast);
                if (dnf.isOr()) {
                  return convertOr(dnf, substituteExpressions);
                }
                if (dnf.isAnd()) {
                  return convertAnd(dnf, substituteExpressions);
                }
                return expr2BooleanFunction(dnf, substituteExpressions);
              }
              break;
            case ID.Implies:
              if (ast.isAST(S.Implies, 3)) {
                return factory.implication(
                    expr2BooleanFunction(ast.arg1(), substituteExpressions),
                    expr2BooleanFunction(ast.arg2(), substituteExpressions));
              }
              break;
            case ID.Not:
              if (ast.isNot()) {
                IExpr expr = ast.arg1();
                return factory.not(expr2BooleanFunction(expr, substituteExpressions));
              }
              break;
          }
        }
      } else if (logicExpr instanceof ISymbol) {
        ISymbol symbol = (ISymbol) logicExpr;
        if (symbol.isFalse()) {
          return factory.falsum();
        }
        if (symbol.isTrue()) {
          return factory.verum();
        }
        if (!symbol.isVariable() || symbol.isProtected()) {
          // `1` is not a valid variable.
          String message = IOFunctions.getMessage("ivar", F.List(symbol), EvalEngine.get());
          throw new ArgumentTypeException(message);
        }
        Variable v = symbol2variableMap.get(symbol);
        if (v == null) {
          final Variable value = factory.variable(symbol.getSymbolName());
          symbol2variableMap.put(symbol, value);
          variable2symbolMap.put(value, symbol);
          return value;
        }
        return v;
      }
      if (substituteExpressions) {
        Variable v = symbol2variableMap.get(logicExpr);
        if (v == null) {
          final Variable value = factory.variable(logicExpr.fullFormString());
          symbol2variableMap.put(logicExpr, value);
          variable2symbolMap.put(value, logicExpr);
          return value;
        }
        return v;
      }
      //       illegal arguments: \"`1`\" in `2`
      String str =
          IOFunctions.getMessage(
              "argillegal", F.List(logicExpr, F.stringx("LogicFormula")), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }

    private Formula convertOr(final IAST ast, boolean substituteExpressions) {
      final Formula[] result = new Formula[ast.argSize()];
      ast.forEach(
          (x, i) -> {
            result[i - 1] = expr2BooleanFunction(x, substituteExpressions);
          });
      return factory.or(result);
    }

    private Formula convertAnd(final IAST ast, boolean substituteExpressions) {
      final Formula[] result = new Formula[ast.argSize()];
      ast.forEach(
          (x, i) -> {
            result[i - 1] = expr2BooleanFunction(x, substituteExpressions);
          });
      return factory.and(result);
    }

    public FormulaFactory getFactory() {
      return factory;
    }

    private Collection<Variable> list2LiteralCollection(final IAST list) {
      Collection<Variable> arr = new ArrayList<Variable>(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        final IExpr arg = list.get(i);
        if (!arg.isSymbol()) {
          // illegal arguments: \"`1`\" in `2`
          String str = IOFunctions.getMessage("argillegal", F.List(arg, list), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }

        ISymbol symbol = (ISymbol) arg;

        Variable v = symbol2variableMap.get(symbol);
        if (v == null) {
          final Variable value = factory.variable(symbol.getSymbolName());
          symbol2variableMap.put(symbol, value);
          variable2symbolMap.put(value, symbol);
        }
        arr.add(v);
      }
      return arr;
    }

    /**
     * Convert the literals into a <code>List()</code> of <code>False, True</code> values according
     * to the assigned position for the variable name in the given <code>map</code>.
     *
     * @param literals a set of literals which could be converted to False and True values
     * @param map a map which maps a variable name to the position in the resulting list
     * @return
     */
    public IAST literals2BooleanList(final SortedSet<Literal> literals, Map<String, Integer> map) {
      IASTMutable list = F.astMutable(S.List, map.size());

      // initialize all list elements with Null
      for (int i = 0; i < map.size(); i++) {
        list.set(i + 1, S.Null);
      }

      for (Literal a : literals) {
        final Integer val = map.get(a.name());
        if (val != null) {
          if (a.phase()) {
            list.set(val + 1, S.True);
          } else {
            list.set(val + 1, S.False);
          }
        }
      }
      return list;
    }

    public IAST literals2VariableList(final SortedSet<Literal> literals, Map<String, Integer> map) {
      IASTMutable list = F.astMutable(S.List, map.size());

      // initialize all list elements with Null
      for (int i = 0; i < map.size(); i++) {
        list.set(i + 1, S.Null);
      }

      for (Literal a : literals) {
        final Integer val = map.get(a.name());
        if (val != null) {
          if (a.phase()) {
            list.set(val + 1, F.Rule(variable2symbolMap.get(a.variable()), S.True));
          } else {
            list.set(val + 1, F.Rule(variable2symbolMap.get(a.variable()), S.False));
          }
        }
      }
      return list;
    }
  }

  private static class AllTrue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.arg1().isAST()) {
        IAST list = (IAST) ast.arg1();
        IExpr head = ast.arg2();
        return allTrue(list, head, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    /**
     * If all expressions evaluates to <code>true</code> for a given unary predicate function return
     * <code>True</code>, if any expression evaluates to <code>false</code> return <code>False
     * </code>, else return an <code>And(...)</code> expression of the result expressions.
     *
     * @param list list of expressions
     * @param head the head of a unary predicate function
     * @param engine
     * @return
     */
    public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalAnd = F.And();

      if (!list.forAll(
          x -> {
            IExpr temp = engine.evaluate(F.unaryAST1(head, x));
            if (temp.isTrue()) {
              return true;
            } else if (temp.isFalse()) {
              return false;
            }
            logicalAnd.append(temp);
            return true;
          })) {
        return S.False;
      }

      if (logicalAnd.size() > 1) {
        return logicalAnd;
      }
      return S.True;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * And(expr1, expr2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>expr1 &amp;&amp; expr2 &amp;&amp; ...</code> evaluates each expression in turn,
   * returning <code>False</code> as soon as an expression evaluates to <code>False</code>. If all
   * expressions evaluate to <code>True</code>, <code>And</code> returns <code>True</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; True &amp;&amp; True &amp;&amp; False
   * False
   * </pre>
   *
   * <p>If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>And
   * </code> returns a result in symbolic form:
   *
   * <pre>
   * &gt;&gt; a &amp;&amp; b &amp;&amp; True &amp;&amp; c
   * a &amp;&amp; b &amp;&amp; c
   * </pre>
   */
  private static class And extends AbstractCoreFunctionEvaluator implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return S.True;
      }

      boolean evaled = false;

      int index = 1;
      IExpr temp = F.NIL;

      IAST flattenedAST = EvalAttributes.flattenDeep(ast);
      if (flattenedAST.isPresent()) {
        evaled = true;
      } else {
        flattenedAST = ast;
      }

      int start = -1;
      for (int i = 1; i < flattenedAST.size(); i++) {
        temp = flattenedAST.get(i);
        if (temp.isBuiltInSymbol()) {
          if (temp.isFalse()) {
            return S.False;
          } else if (temp.isTrue()) {
            continue;
          }
        }

        temp = engine.evaluateNIL(temp);
        if (temp.isPresent()) {
          if (temp.isFalse()) {
            return S.False;
          } else if (temp.isTrue()) {
            continue;
          }
          evaled = true;
        } else {
          temp = flattenedAST.get(i);
        }
        start = i;
        break;
      }
      if (start < 0) {
        return S.True;
      }

      IASTAppendable result = flattenedAST.copyFrom(start);
      result.set(index, temp);

      int[] symbols = new int[flattenedAST.size()];
      int[] notSymbols = new int[flattenedAST.size()];
      for (int i = start; i < flattenedAST.size(); i++) {
        temp = flattenedAST.get(i);
        if (temp.isFalse()) {
          return S.False;
        } else if (temp.isTrue()) {
          result.remove(index);
          evaled = true;
          continue;
        }

        temp = engine.evaluateNIL(temp);
        if (temp.isPresent()) {
          if (temp.isFalse()) {
            return S.False;
          } else if (temp.isTrue()) {
            result.remove(index);
            evaled = true;
            continue;
          }
          result.set(index, temp);
          evaled = true;
        } else {
          temp = flattenedAST.get(i);
        }

        if (temp.isSymbol()) {
          symbols[i] = flattenedAST.get(i).hashCode();
        } else if (temp.isNot()) {
          IExpr sym = temp.first();
          if (sym.isSymbol()) {
            notSymbols[i] = sym.hashCode();
          }
        }
        index++;
      }
      for (int i = 1; i < symbols.length; i++) {
        if (symbols[i] != 0) {
          for (int j = 1; j < notSymbols.length; j++) {
            if (i != j
                && symbols[i] == notSymbols[j]
                && (result.equalsAt(i, result.get(j).first()))) {
              // And[a, Not[a]] => False
              return S.False;
            }
          }
        }
      }
      if (result.isAST1()) {
        return result.arg1();
      }
      if (evaled) {
        if (result.isAST0()) {
          return S.True;
        }

        return result;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
    }
  }

  /**
   *
   *
   * <pre>
   * AnyTrue({expr1, expr2, ...}, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if any application of <code>test</code> to <code>expr1, expr2, ...
   * </code> evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * AnyTrue(list, test, level)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if any application of <code>test</code> to items of <code>list
   * </code> at <code>level</code> evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * AnyTrue(test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>gives an operator that may be applied to expressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; AnyTrue({1, 3, 5}, EvenQ)
   * False
   * &gt;&gt; AnyTrue({1, 4, 5}, EvenQ)
   * True
   * &gt;&gt; AnyTrue({}, EvenQ)
   * False
   * </pre>
   */
  private static class AnyTrue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.arg1().isAST()) {
        IAST list = (IAST) ast.arg1();
        IExpr head = ast.arg2();
        return anyTrue(list, head, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    /**
     * If any expression evaluates to <code>true</code> for a given unary predicate function return
     * <code>True</code>, if all are <code>false</code> return <code>False</code>, else return an
     * <code>Or(...)</code> expression of the result expressions.
     *
     * @param list list of expressions
     * @param head the head of a unary predicate function
     * @param engine
     * @return
     */
    public IExpr anyTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalOr = F.Or();
      if (list.exists(x -> anyTrueArgument(x, head, logicalOr, engine))) {
        return S.True;
      }
      return logicalOr.isAST0() ? S.False : logicalOr;
    }

    private static boolean anyTrueArgument(
        IExpr x, IExpr head, IASTAppendable resultCollector, EvalEngine engine) {
      IExpr temp = engine.evaluate(F.unaryAST1(head, x));
      if (temp.isTrue()) {
        return true;
      } else if (!temp.isFalse()) {
        resultCollector.append(temp);
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Boole(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>1</code> if <code>expr</code> evaluates to <code>True</code>; returns <code>0
   * </code> if <code>expr</code> evaluates to <code>False</code>; and gives no result otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Boole(2 == 2)
   * 1
   * &gt;&gt; Boole(7 &lt; 5)
   * 0
   * &gt;&gt; Boole(a == 7)
   * Boole(a==7)
   * </pre>
   */
  private static class Boole extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        return booleValue(arg1, F.Boole(arg1));
      }
      return booleValue(ast.arg1(), F.NIL);
    }

    private IExpr booleValue(final IExpr arg1, IExpr defaultValue) {
      if (arg1.isSymbol()) {
        if (arg1.isTrue()) {
          return F.C1;
        }
        if (arg1.isFalse()) {
          return F.C0;
        }
        return defaultValue;
      }
      if (arg1.isList()) {
        // Boole has attribute Listable
        return ((IAST) arg1).mapThread(x -> F.Boole(x));
      }
      return defaultValue;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * BooleanConvert(logical - expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>convert the <code>logical-expr</code> to <a
   * href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">disjunctive normal form</a>
   *
   * </blockquote>
   *
   * <pre>
   * BooleanConvert(logical - expr, "CNF")
   * </pre>
   *
   * <blockquote>
   *
   * <p>convert the <code>logical-expr</code> to <a
   * href="https://en.wikipedia.org/wiki/Conjunctive_normal_form">conjunctive normal form</a>
   *
   * </blockquote>
   *
   * <pre>
   * BooleanConvert(logical - expr, "DNF")
   * </pre>
   *
   * <blockquote>
   *
   * <p>convert the <code>logical-expr</code> to <a
   * href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">disjunctive normal form</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BooleanConvert(Xor(x,y))
   * x&amp;&amp;!y||y&amp;&amp;!x
   *
   * &gt;&gt; BooleanConvert(Xor(x,y), "CNF")
   * (x||y)&amp;&amp;(!x||!y)
   *
   * &gt;&gt; BooleanConvert(Xor(x,y), "DNF")
   * x&amp;&amp;!y||y&amp;&amp;!x
   * </pre>
   */
  private static class BooleanConvert extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        return booleanConvert(ast, engine);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * BooleanMinimize(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>minimizes a boolean function with the <a
   * href="https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine McCluskey
   * algorithm</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BooleanMinimize(x&amp;&amp;y||(!x)&amp;&amp;y)
   * y
   *
   * &gt;&gt; BooleanMinimize((a&amp;&amp;!b)||(!a&amp;&amp;b)||(b&amp;&amp;!c)||(!b&amp;&amp;c))
   * a&amp;&amp;!b||!a&amp;&amp;c||b&amp;&amp;!c
   * </pre>
   */
  private static class BooleanMinimize extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        FormulaFactory factory = new FormulaFactory();
        LogicFormula lf = new LogicFormula(factory);
        Formula formula = lf.expr2BooleanFunction(ast.arg1(), true);
        final AdvancedSimplifier simplifier = new AdvancedSimplifier(new DefaultRatingFunction());
        FormulaTransformation transformation = transformation(ast, engine);
        if (transformation == null) {
          return F.NIL;
        }

        final Formula simplified = formula.transform(simplifier).transform(transformation);
        // formula = QuineMcCluskeyAlgorithm.compute(formula);
        // System.out.println(formula.toString());
        IExpr result = lf.booleanFunction2Expr(simplified);
        return result;
      } catch (final ValidateException ve) {
        // int number validation
        engine.printMessage(ast.topHead(), ve);
      } catch (RuntimeException rex) {

      }
      return ast.arg1();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * BooleanTable(logical - expr, variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>generate <a href="https://en.wikipedia.org/wiki/Truth_table">truth values</a> from the
   * <code>logical-expr</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BooleanTable(Implies(Implies(p, q), r), {p, q, r})
   * {True,False,True,True,True,False,True,False}
   *
   * &gt;&gt; BooleanTable(Xor(p, q, r), {p, q, r})
   * {True,False,False,True,False,True,True,False}
   * </pre>
   */
  private static class BooleanTable extends AbstractFunctionEvaluator {

    private static class BooleanTableParameter {
      public IAST variables;
      public IASTAppendable resultList;
      public EvalEngine engine;

      public BooleanTableParameter(IAST variables, EvalEngine engine) {
        this.variables = variables;
        this.resultList = F.ListAlloc(variables.size());
        this.engine = engine;
      }

      public IAST booleanTable(IExpr expr, int position) {
        if (variables.size() <= position) {
          if (expr.isList()) {
            IAST list = (IAST) expr;
            IASTAppendable newList = F.ListAlloc(list.size());
            list.forEach(x -> newList.append(engine.evalSymbolTrue(x)));
            resultList.append(newList);
          } else {
            resultList.append(engine.evalSymbolTrue(expr));
          }
          return resultList;
        }
        IExpr sym = variables.get(position);
        if (sym.isSymbol()) {
          if (sym.isBuiltInSymbol() || !sym.isVariable()) {
            // Cannot assign to raw object `1`.
            throw new ArgumentTypeException(
                IOFunctions.getMessage("setraw", F.List(sym), EvalEngine.get()));
          }
          ISymbol symbol = (ISymbol) sym;
          IExpr value = symbol.assignedValue();
          try {
            symbol.assignValue(S.True, false);
            booleanTable(expr, position + 1);
          } finally {
            symbol.assignValue(value, false);
          }
          try {
            symbol.assignValue(S.False, false);
            booleanTable(expr, position + 1);
          } finally {
            symbol.assignValue(value, false);
          }
        }
        return resultList;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IAST variables;
        if (ast.isAST2()) {
          variables = ast.arg2().orNewList();
        } else {
          variables = BooleanVariables.booleanVariables(ast.arg1());
        }

        BooleanTableParameter btp = new BooleanTableParameter(variables, engine);
        return btp.booleanTable(ast.arg1(), 1);
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      }
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
   * BooleanVariables(logical - expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>gives a list of the boolean variables that appear in the <code>logical-expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BooleanVariables(Xor(p,q,r))
   * {p,q,r}
   * </pre>
   */
  private static class BooleanVariables extends AbstractFunctionEvaluator {

    /** */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return booleanVariables(ast.arg1());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private static IAST booleanVariables(final IExpr expr) {
      VariablesSet eVar = new VariablesSet();
      eVar.addBooleanVarList(expr);
      return eVar.getVarList();
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
   * Equal(x, y)
   *
   * x == y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>True</code> if <code>x</code> and <code>y</code> are known to be equal, or
   * <code>False</code> if <code>x</code> and <code>y</code> are known to be unequal.
   *
   * </blockquote>
   *
   * <pre>
   * lhs == rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the equation <code>lhs = rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a==a
   * True
   *
   * &gt;&gt; a==b
   * a == b
   *
   * &gt;&gt; 1==1
   * True
   * </pre>
   *
   * <p>Lists are compared based on their elements:
   *
   * <pre>
   * &gt;&gt; {{1}, {2}} == {{1}, {2}}
   * True
   * &gt;&gt; {1, 2} == {1, 2, 3}
   * False
   * </pre>
   *
   * <p>Symbolic constants are compared numerically:
   *
   * <pre>
   * &gt;&gt; E &gt; 1
   * True
   *
   * &gt;&gt; Pi == 3.14
   * False
   *
   * &gt;&gt; Pi ^ E == E ^ Pi
   * False
   *
   * &gt;&gt; N(E, 3) == N(E)
   * True
   *
   * &gt;&gt; {1, 2, 3} &lt; {1, 2, 3}
   * {1, 2, 3} &lt; {1, 2, 3}
   *
   * &gt;&gt; E == N(E)
   * True
   *
   * &gt;&gt; {Equal(Equal(0, 0), True), Equal(0, 0) == True}
   * {True, True}
   *
   * &gt;&gt; {Mod(6, 2) == 0, Mod(6, 4) == 0, (Mod(6, 2) == 0) == (Mod(6, 4) == 0), (Mod(6, 2) == 0) != (Mod(6, 4) == 0)}
   * {True,False,False,True}
   *
   * &gt;&gt; a == a == a
   * True
   *
   * &gt;&gt; {Equal(), Equal(x), Equal(1)}
   * {True, True, True}
   * </pre>
   */
  private static class Equal extends AbstractFunctionEvaluator implements IComparatorFunction {

    /**
     * Create the result for a <code>simplifyCompare()</code> step <code>
     * equalOrUnequalSymbol[lhsAST.rest(), rhs]</code>
     *
     * @param equalOrUnequalSymbol
     * @param lhsAST
     * @param rhs
     * @return
     */
    private static IExpr createComparatorResult(
        IBuiltInSymbol equalOrUnequalSymbol, IAST lhsAST, IExpr rhs) {
      return F.binaryAST2(equalOrUnequalSymbol, lhsAST.rest(), rhs);
    }

    /**
     * Try to simplify a comparator expression. Example: <code>3*x > 6</code> will be simplified to
     * <code>x > 2</code>.
     *
     * @param equalOrUnequalSymbol symbol for which the simplification was started
     * @param a1 left-hand-side of the comparator expression
     * @param a2 right-hand-side of the comparator expression
     * @return the simplified comparator expression or <code>F.NIL</code> if no simplification was
     *     found
     */
    protected static IExpr simplifyCompare(
        IBuiltInSymbol equalOrUnequalSymbol, IExpr a1, IExpr a2) {
      IExpr lhs, rhs;
      if (a2.isNumber()) {
        lhs = a1;
        rhs = a2;
      } else if (a1.isNumber()) {
        lhs = a2;
        rhs = a1;
      } else {
        return F.NIL;
      }
      if (lhs.isAST()) {
        IAST lhsAST = (IAST) lhs;
        if (lhsAST.isTimes()) {
          if (lhsAST.arg1().isNumber()) {
            INumber sn = (INumber) lhsAST.arg1();
            rhs = F.eval(F.Divide(rhs, sn));
            return createComparatorResult(equalOrUnequalSymbol, lhsAST, rhs);
          }
        } else if (lhsAST.isPlus() && lhsAST.arg1().isNumber()) {
          INumber sn = (INumber) lhsAST.arg1();
          rhs = F.eval(F.Subtract(rhs, sn));
          return createComparatorResult(equalOrUnequalSymbol, lhsAST, rhs);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.exists(x -> x.equals(S.Undefined))) {
        return S.Undefined;
      }
      if (ast.size() > 2) {
        IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDECIDABLE;
        if (ast.isAST2()) {
          return equalNull(ast.arg1(), ast.arg2(), engine);
        }
        boolean evaled = false;
        IASTAppendable result = ast.copyAppendable();
        int i = 2;
        IExpr arg1 = F.expandAll(result.arg1(), true, true);
        while (i < result.size()) {
          IExpr arg2 = F.expandAll(result.get(i), true, true);
          //          b = compareTernary(arg1, arg2);
          b = arg1.equalTernary(arg2, engine);
          if (b == IExpr.COMPARE_TERNARY.FALSE) {
            return S.False;
          } else if (b == IExpr.COMPARE_TERNARY.TRUE) {
            evaled = true;
            result.remove(i - 1);
          } else {
            result.set(i - 1, arg1);
            i++;
            arg1 = arg2;
          }
        }
        if (evaled) {
          if (result.isAST1()) {
            return S.True;
          }
          return result;
        }
        return F.NIL;
      }
      return S.True;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Equivalent(arg1, arg2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>Equivalence relation. <code>Equivalent(A, B)</code> is <code>True</code> iff <code>A</code>
   * and <code>B</code> are both <code>True</code> or both <code>False</code>. Returns <code>True
   * </code> if all of the arguments are logically equivalent. Returns <code>False</code> otherwise.
   * <code>Equivalent(arg1, arg2, ...)</code> is equivalent to <code>
   * (arg1 &amp;&amp; arg2 &amp;&amp; ...) || (!arg1 &amp;&amp; !arg2 &amp;&amp; ...)</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Equivalent(True, True, False)
   * False
   *
   * &gt;&gt; Equivalent(x, x &amp;&amp; True)
   * True
   * </pre>
   *
   * <p>If all expressions do not evaluate to 'True' or 'False', 'Equivalent' returns a result in
   * symbolic form:
   *
   * <pre>
   * &gt;&gt; Equivalent(a, b, c)
   * Equivalent(a,b,c)
   * </pre>
   *
   * <p>Otherwise, 'Equivalent' returns a result in DNF
   *
   * <pre>
   * &gt;&gt; Equivalent(a, b, True, c)
   * a &amp;&amp; b &amp;&amp; c
   * &gt;&gt; Equivalent()
   * True
   * &gt;&gt; Equivalent(a)
   * True
   * </pre>
   */
  private static final class Equivalent extends AbstractFunctionEvaluator
      implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0() || ast.isAST1()) {
        return S.True;
      }
      IASTAppendable result = ast.copyHead();
      IExpr last = F.NIL;
      IExpr boole = F.NIL;
      boolean evaled = false;

      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg.isFalse()) {
          if (!boole.isPresent()) {
            boole = S.False;
          } else if (boole.isTrue()) {
            return S.False;
          }
          evaled = true;
        } else if (arg.isTrue()) {
          if (!boole.isPresent()) {
            boole = S.True;
          } else if (boole.isFalse()) {
            return S.False;
          }
          evaled = true;
        } else {
          if (!last.equals(arg)) {
            result.append(arg);
          } else {
            evaled = true;
          }

          last = arg;
        }
      }
      if (evaled) {
        if (result.isAST0()) {
          if (boole.isPresent()) {
            return S.True;
          }
        } else if (result.isAST1() && !boole.isPresent()) {
          return S.True;
        }
        if (boole.isPresent()) {
          result = result.apply(S.And);
          if (boole.isTrue()) {
            return result;
          } else {
            return result.mapThread(F.Not(F.Slot1), 1);
          }
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS);
    }
  }

  private static final class Exists extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean evaled = false;
      // TODO localize x
      IExpr x = engine.evaluateNIL(ast.arg1());
      if (x.isPresent()) {
        evaled = true;
      } else {
        x = ast.arg1();
      }

      IExpr expr = engine.evaluateNIL(ast.arg2());
      if (expr.isPresent()) {
        evaled = true;
      } else {
        expr = ast.arg2();
      }

      if (ast.isAST3()) {
        IExpr arg3 = engine.evaluateNIL(ast.arg3());
        if (arg3.isPresent()) {
          evaled = true;
        } else {
          arg3 = ast.arg3();
        }
        if (evaled) {
          return F.Exists(x, expr, arg3);
        }
        return F.NIL;
      }

      if (expr.isFree(x)) {
        return expr;
      }
      if (evaled) {
        return F.Exists(x, expr);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class ForAll extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean evaled = false;
      // TODO localize x
      IExpr x = engine.evaluateNIL(ast.arg1());
      if (x.isPresent()) {
        evaled = true;
      } else {
        x = ast.arg1();
      }

      IExpr expr = engine.evaluateNIL(ast.arg2());
      if (expr.isPresent()) {
        evaled = true;
      } else {
        expr = ast.arg2();
      }

      if (ast.isAST3()) {
        IExpr arg3 = engine.evaluateNIL(ast.arg3());
        if (arg3.isPresent()) {
          evaled = true;
        } else {
          arg3 = ast.arg3();
        }
        if (evaled) {
          return F.ForAll(x, expr, arg3);
        }
        return F.NIL;
      }

      if (expr.isFree(x)) {
        return expr;
      }
      if (evaled) {
        return F.ForAll(x, expr);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
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
   * Greater(x, y)
   *
   * x &gt; y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>True</code> if <code>x</code> is known to be greater than <code>y</code>.
   *
   * </blockquote>
   *
   * <pre>
   * lhs &gt; rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the inequality <code>lhs &gt; rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Pi&gt;0
   * True
   *
   * &gt;&gt; {Greater(), Greater(x), Greater(1)}
   * {True, True, True}
   * </pre>
   */
  public static class Greater extends AbstractCoreFunctionEvaluator
      implements ITernaryComparator, IComparatorFunction {
    public static final Greater CONST = new Greater();

    /**
     * Check assumptions for the comparison operator. Will be overridden in <code>
     * GreaterEqual, Less, LessEqual</code>.
     *
     * @param arg1 the left-hand-side of the comparison
     * @param arg2 the right-hand-side of the comparison which is tested with {@link
     *     IExpr#isNumericFunction(boolean)} equals <code>true</code>.
     * @return
     */
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        // arg1 > "negative number"
        if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
          return S.True;
        }
      } else if (arg2.isZero()) {
        // arg1 > 0
        if (arg1.isPositiveResult()) {
          return S.True;
        }
        if (arg1.isNegativeResult()) {
          return S.False;
        }
      } else {
        // arg1 < "positive number"
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.False;
        }
      }
      ISignedNumber a2 = arg2.evalReal();
      if (a2 != null && AbstractAssumptions.assumeGreaterThan(arg1, a2)) {
        return S.True;
      }
      return F.NIL;
    }

    /**
     * Compare two intervals if they are greater.
     *
     * <ul>
     *   <li>Return TRUE if the comparison is <code>true</code>
     *   <li>Return FALSE if the comparison is <code>false</code>
     *   <li>Return UNDEFINED if the comparison is undetermined (i.e. could not be evaluated)
     * </ul>
     *
     * @param lower0 the lower bound of the first interval
     * @param upper0 the upper bound of the first interval
     * @param lower1 the lower bound of the second interval
     * @param upper1 the upper bound of the second interval
     * @return
     */
    private static IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(
        final IExpr lower0, final IExpr upper0, final IExpr lower1, final IExpr upper1) {
      if (lower0.greaterThan(upper1).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else {
        if (upper0.lessThan(lower1).isTrue()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    /** {@inheritDoc} */
    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      // don't compare strings
      if (a0.isReal()) {
        if (a1.isReal()) {
          return ((ISignedNumber) a0).isGT((ISignedNumber) a1)
              ? IExpr.COMPARE_TERNARY.TRUE
              : IExpr.COMPARE_TERNARY.FALSE;
        } else if (a1.isInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        } else if (a1.isNegativeInfinity()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        } else if (a1.isInterval1()) {
          return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
        }
      } else if (a1.isReal()) {
        if (a0.isInfinity()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        } else if (a0.isNegativeInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        } else if (a0.isInterval1()) {
          return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
        }
      } else if (a0.isInfinity()) {
        if (a1.isRealResult() || a1.isNegativeInfinity()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        }
      } else if (a0.isNegativeInfinity()) {
        if (a1.isRealResult() || a1.isInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
      } else if (a1.isInfinity() && a0.isRealResult()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      } else if (a1.isNegativeInfinity() && a0.isRealResult()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (a0.isInterval1() && a1.isInterval1()) {
        return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
      } else if (a0.isQuantity() && a1.isQuantity()) {
        int comp = quantityCompareTo((IQuantity) a0, (IQuantity) a1);
        if (comp != Integer.MIN_VALUE) {
          return comp > 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
        }
        return IExpr.COMPARE_TERNARY.UNDECIDABLE;
      }

      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult() && !a0.isList()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }

      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    /**
     * Create the result for a <code>simplifyCompare()</code> step.
     *
     * @param lhs left-hand-side of the comparator expression
     * @param rhs right-hand-side of the comparator expression
     * @param useOppositeHeader use the opposite header to create the result
     * @param originalHead symbol of the comparator operator for which the simplification was
     *     started
     * @param oppositeHead opposite of the symbol of the comparator operator for which the
     *     comparison was started
     * @return
     */
    private IAST createComparatorResult(
        IExpr lhs,
        IExpr rhs,
        boolean useOppositeHeader,
        ISymbol originalHead,
        ISymbol oppositeHead) {
      return F.binaryAST2(useOppositeHeader ? oppositeHead : originalHead, lhs, rhs);
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() <= 2) {
        if (ast.isAST1() && ast.arg1().equals(S.Undefined)) {
          return S.Undefined;
        }
        return S.True;
      }

      IASTAppendable flattened;
      if ((flattened = EvalAttributes.flattenDeep(ast)).isPresent()) {
        ast = flattened;
      }
      IExpr temp = engine.evalAttributes((ISymbol) ast.head(), ast);
      if (temp.isPresent()) {
        return temp;
      }

      IAST astEvaled = ast;
      if (astEvaled.isAST2()) {
        IExpr arg1 = astEvaled.arg1();
        IExpr arg2 = astEvaled.arg2();
        IExpr result = simplifyCompare(arg1, arg2);
        if (result.isPresent()) {
          // the result may return an AST with an "opposite header"
          // (i.e. Less instead of Greater)
          return result;
        }
        if (arg2.isNumericFunction(true)) {
          // this part is used in other comparator operations like
          // Less,
          // GreaterEqual,...
          IExpr temp2 = checkAssumptions(arg1, arg2);
          if (temp2.isPresent()) {
            return temp2;
          }
        }
      }
      boolean evaled = false;

      IASTAppendable result = astEvaled.copyAppendable();
      IExpr.COMPARE_TERNARY[] cResult = new IExpr.COMPARE_TERNARY[astEvaled.size()];
      cResult[0] = IExpr.COMPARE_TERNARY.TRUE;
      for (int i = 1; i < astEvaled.argSize(); i++) {
        IExpr arg = result.get(i);
        if (arg.equals(S.Undefined)) {
          return S.Undefined;
        }
        final IExpr.COMPARE_TERNARY b = prepareCompare(arg, result.get(i + 1), engine);
        if (b == IExpr.COMPARE_TERNARY.FALSE) {
          return S.False;
        }
        if (b == IExpr.COMPARE_TERNARY.TRUE) {
          evaled = true;
        }
        cResult[i] = b;
      }
      cResult[astEvaled.argSize()] = IExpr.COMPARE_TERNARY.TRUE;
      if (!evaled) {
        // expression doesn't change
        return F.NIL;
      }
      int i = 2;
      evaled = false;
      for (int j = 1; j < astEvaled.size(); j++) {
        if (cResult[j - 1] == IExpr.COMPARE_TERNARY.TRUE
            && cResult[j] == IExpr.COMPARE_TERNARY.TRUE) {
          evaled = true;
          result.remove(i - 1);
        } else {
          i++;
        }
      }

      if (evaled) {
        if (result.size() <= 2) {
          return S.True;
        }
        return result;
      }
      return F.NIL;
    }

    private IExpr.COMPARE_TERNARY prepareCompare(IExpr a0, IExpr a1, EvalEngine engine) {
      if ((!a0.isReal() && a0.isNumericFunction(true))
          || (a1.isInexactNumber() && a0.isRational())) {
        a0 = engine.evalN(a0);
      }
      if ((!a1.isReal() && a1.isNumericFunction(true))
          || (a0.isInexactNumber() && a1.isRational())) {
        a1 = engine.evalN(a1);
      }

      return compareTernary(a0, a1);
    }

    public IExpr.COMPARE_TERNARY prepareCompare(final IExpr o0, final IExpr o1) {
      return prepareCompare(o0, o1, EvalEngine.get());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't assign ISymbol.FLAT
    }

    /**
     * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> will be simplified
     * to <code>x &gt; 2</code>.
     *
     * @param a1 left-hand-side of the comparator expression
     * @param a2 right-hand-side of the comparator expression
     * @return the simplified comparator expression or <code>null</code> if no simplification was
     *     found
     */
    protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
      return simplifyCompare(a1, a2, S.Greater, S.Less, true);
    }

    /**
     * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> wll be simplified
     * to <code>x &gt; 2</code>.
     *
     * @param a1 left-hand-side of the comparator expression
     * @param a2 right-hand-side of the comparator expression
     * @param originalHead symbol of the comparator operator for which the simplification was
     *     started
     * @param oppositeHead opposite of the symbol of the comparator operator for which the
     *     comparison was started
     * @param setTrue if <code>true</code> return S.True otherwise S.False
     * @return the simplified comparator expression or <code>F.NIL</code> if no simplification was
     *     found
     */
    protected final IExpr simplifyCompare(
        IExpr a1,
        IExpr a2,
        IBuiltInSymbol originalHead,
        IBuiltInSymbol oppositeHead,
        boolean setTrue) {
      if (a1.isInfinity() && a2.isInfinity()) {
        return S.False;
      }
      if (a1.isNegativeInfinity() && a2.isNegativeInfinity()) {
        return S.False;
      }
      IExpr lhs = F.NIL;
      IExpr rhs = F.NIL;
      boolean useOppositeHeader = false;
      if (a2.isNumericFunction(true)) {
        lhs = a1;
        rhs = a2;
      } else if (a1.isNumericFunction(true)) {
        lhs = a2;
        rhs = a1;
        useOppositeHeader = true;
      } else if (a1.isRealResult() && a2.isRealResult()) {
        lhs = F.eval(F.Subtract(a1, a2));
        rhs = F.C0;
        if (lhs.isReal()) {
          return createComparatorResult(lhs, rhs, useOppositeHeader, originalHead, oppositeHead);
        }
      }
      if (lhs.isAST()) {
        IAST lhsAST = (IAST) lhs;
        if (useOppositeHeader) {
          setTrue = !setTrue;
        }
        if (lhsAST.isInfinity() && rhs.isRealResult()) {
          // Infinity > rhs ?
          return setTrue ? S.True : S.False;
        }
        if (lhsAST.isNegativeInfinity() && rhs.isRealResult()) {
          // -Infinity > rhs ?
          return setTrue ? S.False : S.True;
        }
        if (rhs.isInfinity() && lhsAST.isRealResult()) {
          // lhs > Infinity ?
          return setTrue ? S.False : S.True;
        }
        if (rhs.isNegativeInfinity() && lhsAST.isRealResult()) {
          // lhs > -Infinity ?
          return setTrue ? S.True : S.False;
        }
        if (lhsAST.isTimes()) {
          IAST result = lhsAST.partitionTimes(x -> x.isNumericFunction(true), F.C0, F.C1, S.List);
          if (!result.arg1().isZero()) {
            if (result.arg1().hasComplexNumber() || result.arg2().hasComplexNumber()) {
              return IOFunctions.printMessage(
                  originalHead, "nord", F.List(result.arg1()), EvalEngine.get());
            }
            if (result.arg1().isNegative()) {
              useOppositeHeader = !useOppositeHeader;
            }
            rhs = rhs.divide(result.arg1());
            return createComparatorResult(
                result.arg2(), rhs, useOppositeHeader, originalHead, oppositeHead);
          }
        } else if (lhsAST.isPlus()) {
          IAST result = lhsAST.partitionPlus(x -> x.isNumericFunction(true), F.C0, F.C0, S.List);
          if (!result.arg1().isZero()) {
            if (result.arg1().hasComplexNumber() || result.arg2().hasComplexNumber()) {
              return IOFunctions.printMessage(
                  originalHead, "nord", F.List(result.arg1()), EvalEngine.get());
            }
            rhs = rhs.subtract(result.arg1());
            return createComparatorResult(
                result.arg2(), rhs, useOppositeHeader, originalHead, oppositeHead);
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
   * GreaterEqual(x, y)
   *
   * x &gt;= y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>True</code> if <code>x</code> is known to be greater than or equal to <code>y
   * </code>.
   *
   * </blockquote>
   *
   * <pre>
   * lhs &gt;= rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the inequality <code>lhs &gt;= rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; x&gt;=x
   * True
   *
   * &gt;&gt; {GreaterEqual(), GreaterEqual(x), GreaterEqual(1)}
   * {True, True, True}
   * </pre>
   */
  public static final class GreaterEqual extends Greater {
    public static final GreaterEqual CONST = new GreaterEqual();

    /** {@inheritDoc} */
    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        // arg1 >= "negative number"
        if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
          return S.True;
        }
      } else if (arg2.isZero()) {
        // arg1 >= 0
        if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
          return S.True;
        }
        if (arg1.isNegativeResult()) {
          return S.False;
        }
      } else {
        // arg1 >= "positive number" > 0
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.False;
        }
      }
      ISignedNumber a2 = arg2.evalReal();
      if (a2 != null && AbstractAssumptions.assumeGreaterEqual(arg1, a2)) {
        return S.True;
      }
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
      if (a1.isInfinity() && a2.isInfinity()) {
        return S.True;
      }
      if (a1.isNegativeInfinity() && a2.isNegativeInfinity()) {
        return S.True;
      }
      return simplifyCompare(a1, a2, S.GreaterEqual, S.LessEqual, true);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (a0.isQuantity() && a1.isQuantity()) {
        int comp = quantityCompareTo((IQuantity) a0, (IQuantity) a1);
        if (comp != Integer.MIN_VALUE) {
          return comp >= 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
        }
        return IExpr.COMPARE_TERNARY.UNDECIDABLE;
      }
      return super.compareTernary(a0, a1);
    }
  }

  /**
   *
   *
   * <pre>
   * Implies(arg1, arg2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>Logical implication. <code>Implies(A, B)</code> is equivalent to <code>!A || B</code>.
   * <code>Implies(expr1, expr2)</code> evaluates each argument in turn, returning <code>True</code>
   * as soon as the first argument evaluates to <code>False</code>. If the first argument evaluates
   * to <code>True</code>, <code>Implies</code> returns the second argument.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Implies(False, a)
   * True
   * &gt;&gt; Implies(True, a)
   * a
   * </pre>
   *
   * <p>If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Implies
   * </code> returns a result in symbolic form:
   *
   * <pre>
   * &gt;&gt; Implies(a, Implies(b, Implies(True, c)))
   * Implies(a,Implies(b,c))
   * </pre>
   */
  private static final class Implies extends AbstractCoreFunctionEvaluator
      implements IBooleanFormula {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean evaled = false;
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        evaled = true;
      } else {
        arg1 = ast.arg1();
      }
      if (arg1.isTrue()) {
        return ast.arg2();
      }
      if (arg1.isFalse()) {
        return S.True;
      }

      IExpr arg2 = engine.evaluateNIL(ast.arg2());
      if (arg2.isPresent()) {
        evaled = true;
      } else {
        arg2 = ast.arg2();
      }
      if (arg2.isTrue()) {
        return S.True;
      }
      if (arg2.isFalse()) {
        return F.Not(arg1);
      }
      if (arg1.equals(arg2)) {
        return S.True;
      }

      if (evaled) {
        return F.Implies(arg1, arg2);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class Inequality extends AbstractEvaluator implements IComparatorFunction {
    static final IBuiltInSymbol[] COMPARATOR_SYMBOLS = {
      F.Equal, F.Greater, F.GreaterEqual, F.Less, F.LessEqual, F.Unequal
    };

    private static final int UNKNOWN = -3;
    private static final int UNEQUAL = -2;
    private static final int LESS_OR_LESSEQUAL = -1;
    private static final int EQUAL = 2;
    private static final int GREATER_OR_GREATEREQUAL = 1;

    private static int getCompSign(IExpr e) {
      if (e.isSymbol()) {
        if (e.equals(S.Less) || e.equals(S.LessEqual)) {
          return LESS_OR_LESSEQUAL;
        }
        if (e.equals(S.Equal)) {
          return EQUAL;
        }
        if (e.equals(S.Greater) || e.equals(S.GreaterEqual)) {
          return GREATER_OR_GREATEREQUAL;
        }
        if (e.equals(S.Unequal)) {
          return UNEQUAL;
        }
      }
      return UNKNOWN;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        return S.True;
      }
      try {
        // Validate.checkRange(ast, 4, Integer.MAX_VALUE);
        if (ast.size() < 4) {
          return F.NIL;
        }
        if (ast.size() == 4) {
          for (IBuiltInSymbol sym : COMPARATOR_SYMBOLS) {
            if (sym.equals(ast.arg2())) {
              return F.binaryAST2(ast.arg2(), ast.arg1(), ast.arg3());
            }
          }
          return F.NIL;
        }

        if ((ast.size()) % 2 != 0) {
          return F.NIL;
        }
        int firstSign = getCompSign(ast.arg2());
        if (firstSign == UNKNOWN) {
          return F.NIL;
        }

        for (int i = 4; i < ast.size(); i += 2) {
          final int thisSign = getCompSign(ast.get(i));
          if (thisSign == UNKNOWN) {
            return F.NIL;
          }
          if (thisSign == -firstSign || thisSign == UNEQUAL || firstSign == UNEQUAL) {
            IASTAppendable firstIneq = F.ast(S.Inequality);
            IASTAppendable secondIneq = F.ast(S.Inequality);
            for (int j = 1; j < ast.size(); j++) {
              final IExpr arg = ast.get(j);
              if (j < i) {
                firstIneq.append(arg);
              }
              if (j > (i - 2)) {
                secondIneq.append(arg);
              }
            }
            return F.And(firstIneq, secondIneq);
          }
        }

        IASTAppendable res = F.ast(S.Inequality);
        IExpr lastOp = F.NIL;
        for (int i = 0; i < (ast.size() - 1) / 2; i++) {
          final IExpr lhs = ast.get(2 * i + 1);
          final IExpr op = ast.get(2 * i + 2);
          final IExpr rhs = ast.get(2 * i + 3);
          for (int rhsI = 2 * i + 3; rhsI < ast.size(); rhsI += 2) {
            final IExpr arg = engine.evaluate(F.binaryAST2(op, lhs, ast.get(rhsI)));
            if (arg.isFalse()) {
              // explicitly tested for False symbol
              return S.False;
            }
          }
          IExpr evalRes = engine.evaluate(F.binaryAST2(op, lhs, rhs));
          if (!evalRes.isTrue()) {
            if (engine.evaluate(F.SameQ(lhs, res.get(res.size() - 1))).isFalse()) {
              if (lastOp.isPresent() && res.size() > 2) {
                res.append(lastOp);
              }
              res.append(lhs);
            }
            res.append(op);
            res.append(rhs);
            lastOp = F.NIL;
          } else {
            lastOp = op;
          }
        }
        if (res.isEmpty()) {
          return S.True;
        }
        if (res.size() == 4) {
          return F.binaryAST2(res.arg2(), res.arg1(), res.arg3());
        }
        if (res.size() == ast.size()) {
          return F.NIL;
        }
        return res;

        // return inequality(ast, engine);
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      }
    }
  }

  /**
   *
   *
   * <pre>
   * Less(x, y)
   *
   * x &lt; y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>True</code> if <code>x</code> is known to be less than <code>y</code>.
   *
   * </blockquote>
   *
   * <pre>
   * lhs &lt; rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the inequality <code>lhs &lt; rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 3&lt;4
   * True
   *
   * &gt;&gt; {Less(), Less(x), Less(1)}
   * {True, True, True}
   * </pre>
   */
  public static final class Less extends Greater {

    /** {@inheritDoc} */
    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        // arg1 < "negative number"
        if (arg1.isPositiveResult()) {
          return S.False;
        }

      } else if (arg2.isZero()) {
        // arg1 < 0
        if (arg1.isNegativeResult()) {
          return S.True;
        }
        if (arg1.isPositiveResult()) {
          return S.False;
        }
      } else {
        // arg1 < "positive number"
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.True;
        }
      }
      ISignedNumber a2 = arg2.evalReal();
      if (a2 != null && AbstractAssumptions.assumeLessThan(arg1, a2)) {
        return S.True;
      }
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
      if (a1.isInfinity() && a2.isInfinity()) {
        return S.False;
      }
      if (a1.isNegativeInfinity() && a2.isNegativeInfinity()) {
        return S.False;
      }
      return simplifyCompare(a1, a2, S.Less, S.Greater, false);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      // swap arguments
      return super.compareTernary(a1, a0);
    }
  }

  /**
   *
   *
   * <pre>
   * LessEqual(x, y)
   *
   * x &lt;= y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>True</code> if <code>x</code> is known to be less than or equal <code>y</code>.
   *
   * </blockquote>
   *
   * <pre>
   * lhs &lt;= rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the inequality <code>lhs ≤ rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 3&lt;=4
   * True
   *
   * &gt;&gt; {LessEqual(), LessEqual(x), LessEqual(1)}
   * {True, True, True}
   * </pre>
   */
  public static final class LessEqual extends Greater {

    /** {@inheritDoc} */
    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        // arg1 <= "negative number"
        if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
          return S.False;
        }
      } else if (arg2.isZero()) {
        // arg1 <= 0
        if (arg1.isNegativeResult()) {
          return S.True;
        }
        if (arg1.isPositiveResult()) {
          return S.False;
        }
      } else {
        // arg1 <= "positive number"
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.True;
        }
      }
      ISignedNumber a2 = arg2.evalReal();
      if (a2 != null && AbstractAssumptions.assumeLessEqual(arg1, a2)) {
        return S.True;
      }
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
      if (a1.isInfinity() && a2.isInfinity()) {
        return S.True;
      }
      if (a1.isNegativeInfinity() && a2.isNegativeInfinity()) {
        return S.True;
      }
      return simplifyCompare(a1, a2, S.LessEqual, S.GreaterEqual, false);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      // don't compare strings
      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (a0.isQuantity() && a1.isQuantity()) {
        int comp = quantityCompareTo((IQuantity) a0, (IQuantity) a1);
        if (comp != Integer.MIN_VALUE) {
          return comp <= 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
        }
        return IExpr.COMPARE_TERNARY.UNDECIDABLE;
      }
      // swap arguments
      return super.compareTernary(a1, a0);
    }
  }

  private static class CompareOperator extends AbstractFunctionEvaluator {
    IBuiltInSymbol operatorHead;
    IBuiltInSymbol comparatorHead;

    public CompareOperator(IBuiltInSymbol operatorHead, IBuiltInSymbol comparatorHead) {
      this.operatorHead = operatorHead;
      this.comparatorHead = comparatorHead;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr head = ast.head();
        if (head.isAST(operatorHead, 2)) {
          return F.binaryAST2(comparatorHead, ast.arg1(), head.first());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class LogicalExpand extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.arg1().isAST()) {
        IExpr subst = ast.arg1().replaceAll(logicalExpand(engine));
        if (subst.isPresent()) {
          return subst;
        }
      }
      return arg1;
    }

    private static Function<IExpr, IExpr> logicalExpand(EvalEngine engine) {
      return x -> {
        if (x.isAST()) {
          IAST formula = (IAST) x;
          if (x.isNot() && x.first().isOr()) {
            IAST result = ((IAST) x.first()).apply(S.And);
            result = result.map(arg -> F.Not(arg));
            //            for (int i = 1; i < result.size(); i++) {
            //              result.set(i, F.Not(result.get(i)));
            //            }
            return engine.evaluate(result);
          }
          if (formula.isSameHeadSizeGE(S.Xor, 3)) {
            return xorToDNF(formula);
          }
          try {
            return booleanConvert(F.BooleanConvert(formula, F.stringx("DNF")), engine);
          } catch (final ValidateException ve) {
          }
        }
        return F.NIL;
      };
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Max(e_1, e_2, ..., e_i)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the expression with the greatest value among the <code>e_i</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>Maximum of a series of numbers:
   *
   * <pre>
   * &gt;&gt; Max(4, -8, 1)
   * 4
   * </pre>
   *
   * <p><code>Max</code> flattens lists in its arguments:
   *
   * <pre>
   * &gt;&gt; Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})
   * 3.5
   * </pre>
   *
   * <p><code>Max</code> with symbolic arguments remains in symbolic form:
   *
   * <pre>
   * &gt;&gt; Max(x, y)
   * Max(x,y)
   *
   * &gt;&gt; Max(5, x, -3, y, 40)
   * Max(40,x,y)
   * </pre>
   *
   * <p>With no arguments, <code>Max</code> gives <code>-Infinity</code>:
   *
   * <pre>
   * &gt;&gt; Max()
   * -Infinity
   *
   * &gt;&gt; Max(x)
   * x
   * </pre>
   */
  private static class Max extends Min {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.CNInfinity;
      }

      if (ast.arg1().isInterval()) {
        return IntervalSym.max((IAST) ast.arg1());
      }

      int allocSize = F.allocLevel1(ast, x -> x.isList());
      IASTAppendable result = F.ast(S.Max, allocSize);
      boolean evaled = flattenListRecursive(ast, result);
      return maximum(result, evaled);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return null;
    }

    private static IExpr maximum(IAST list, boolean flattenedList) {
      boolean evaled = false;
      // int j = 1;
      IASTAppendable f = list.remove(x -> x.isNegativeInfinity());
      if (f.isPresent()) {
        if (f.isAST0()) {
          return F.CNInfinity;
        }
        list = f;
        evaled = true;
      }
      if (!evaled) {
        evaled = flattenedList;
      }

      if (list.isEmpty()) {
        return F.CNInfinity;
      }
      IExpr max1;
      IExpr max2;
      max1 = list.arg1();

      IExpr.COMPARE_TERNARY comp;
      f = list.copyHead();
      for (int i = 2; i < list.size(); i++) {
        max2 = list.get(i);
        if (max1.equals(max2)) {
          continue;
        }
        comp = BooleanFunctions.CONST_LESS.prepareCompare(max1, max2);

        if (comp == IExpr.COMPARE_TERNARY.TRUE) {
          max1 = max2;
          evaled = true;
        } else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
          evaled = true;
        } else {
          if (comp == IExpr.COMPARE_TERNARY.UNDECIDABLE) {
            // undetermined
            if (max1.isRealResult()) {
              f.append(max2);
            } else {
              f.append(max1);
              max1 = max2;
            }
          }
        }
      }
      if (f.size() > 1) {
        f.append(max1);
        if (!evaled) {
          return F.NIL;
        }
        return f;
      } else {
        return max1;
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(
          ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Min(e_1, e_2, ..., e_i)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the expression with the lowest value among the <code>e_i</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>Minimum of a series of numbers:
   *
   * <pre>
   * &gt;&gt; Max(4, -8, 1)
   * -8
   * </pre>
   *
   * <p><code>Min</code> flattens lists in its arguments:
   *
   * <pre>
   * &gt;&gt; Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})
   * -Infinity
   * </pre>
   *
   * <p><code>Min</code> with symbolic arguments remains in symbolic form:
   *
   * <pre>
   * &gt;&gt; Min(x, y)
   * Min(x,y)
   *
   * &gt;&gt; Min(5, x, -3, y, 40)
   * Min(-3,x,y)
   * </pre>
   *
   * <p>With no arguments, <code>Min</code> gives <code>Infinity</code>:
   *
   * <pre>
   * &gt;&gt; Min()
   * Infinity
   *
   * &gt;&gt; Min(x)
   * x
   * </pre>
   */
  private static class Min extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.CInfinity;
      }

      if (ast.arg1().isInterval()) {
        return IntervalSym.min((IAST) ast.arg1());
      }
      int allocSize = F.allocLevel1(ast, x -> x.isList());
      IASTAppendable result = F.ast(S.Min, allocSize);
      boolean evaled = flattenListRecursive(ast, result);
      return minimum(result, evaled);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return null;
    }

    protected boolean flattenListRecursive(IAST ast, IASTAppendable result) {
      boolean evaled = false;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        int dim = arg.isVector();
        if (dim >= 0) {
          IExpr normal = arg.normal(false);
          if (normal.isList()) {
            flattenListRecursive((IAST) normal, result);
            evaled = true;
            continue;
          }
        } else if (arg.isList()) {
          flattenListRecursive((IAST) arg, result);
          evaled = true;
          continue;
        }
        result.append(arg);
      }
      return evaled;
    }

    private static IExpr minimum(IAST list, final boolean flattenedList) {
      boolean evaled = false;
      IASTAppendable f = list.remove(x -> x.isInfinity());
      if (f.isPresent()) {
        if (f.isAST0()) {
          return F.CNInfinity;
        }
        list = f;
        evaled = true;
      }
      if (!evaled) {
        evaled = flattenedList;
      }

      if (list.isEmpty()) {
        return F.CInfinity;
      }
      IExpr min1;
      IExpr min2;
      min1 = list.arg1();
      f = list.copyHead();
      IExpr.COMPARE_TERNARY comp;
      for (int i = 2; i < list.size(); i++) {
        min2 = list.get(i);
        if (min2.isInfinity()) {
          evaled = true;
          continue;
        }

        if (min1.equals(min2)) {
          continue;
        }
        comp = BooleanFunctions.CONST_GREATER.prepareCompare(min1, min2);

        if (comp == IExpr.COMPARE_TERNARY.TRUE) {
          min1 = min2;
          evaled = true;
        } else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
          evaled = true;
        } else {
          if (comp == IExpr.COMPARE_TERNARY.UNDECIDABLE) {
            // undetermined
            if (min1.isRealResult()) {
              f.append(min2);
            } else {
              f.append(min1);
              min1 = min2;
            }
          }
        }
      }
      if (f.size() > 1) {
        f.append(1, min1);
        if (!evaled) {
          return F.NIL;
        }
        return f;
      } else {
        return min1;
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(
          ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class MinMax extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isList() || arg1.isAssociation()) {
          return F.List(F.Min(arg1), F.Max(arg1));
        }
      } else if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg1.isList() || arg1.isAssociation()) {
          if (arg2.isList()) {
            if (arg2.size() == 3
                && //
                arg2.first().isNumericFunction(true)
                && //
                arg2.second().isNumericFunction(true)) {
              return F.List(
                  F.Subtract(F.Min(arg1), arg2.first()), F.Plus(F.Max(arg1), arg2.second()));
            }
          } else if (arg2.isNumericFunction(true)) {
            return F.List(F.Subtract(F.Min(arg1), arg2), F.Plus(F.Max(arg1), arg2));
          } else if (arg2.isAST(S.Scaled, 2)
              && //
              arg2.first().isNumericFunction(true)) {
            IExpr delta =
                engine.evaluate(F.Times(arg2.first(), F.Subtract(F.Max(arg1), F.Min(arg1))));
            return F.List(F.Subtract(F.Min(arg1), delta), F.Plus(F.Max(arg1), delta));
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Nand(arg1, arg2, ...)'
   * </pre>
   *
   * <blockquote>
   *
   * <p>Logical NAND function. It evaluates its arguments in order, giving <code>True</code>
   * immediately if any of them are <code>False</code>, and <code>False</code> if they are all
   * <code>True</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Nand(True, True, True)
   * False
   *
   * &gt;&gt; Nand(True, False, a)
   * True
   * </pre>
   */
  private static final class Nand extends AbstractCoreFunctionEvaluator implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return S.False;
      }
      if (ast.isAST1()) {
        return F.Not(ast.arg1());
      }
      IASTAppendable result = ast.copyHead();
      boolean evaled = false;

      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = engine.evaluate(ast.get(i));
        if (arg.isFalse()) {
          return S.True;
        } else if (arg.isTrue()) {
          evaled = true;
        } else {
          result.append(arg);
        }
      }
      if (evaled) {
        if (result.isAST0()) {
          return S.False;
        }
        if (result.isAST1()) {
          return F.Not(result.arg1());
        }
        return result;
      }
      return F.NIL;
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
   * Negative(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is a negative real number.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Negative(0)
   * False
   *
   * &gt;&gt; Negative(-3)
   * True
   *
   * &gt;&gt; Negative(10/7)
   * False
   *
   * &gt;&gt; Negative(1+2*I)
   * False
   *
   * &gt;&gt; Negative(a + b)
   * Negative(a+b)
   *
   * &gt;&gt; Negative(-E)
   * True
   *
   * &gt;&gt; Negative(Sin({11, 14}))
   * {True, False}
   * </pre>
   */
  private static class Negative extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isNegativeResult()) {
        return S.True;
      }
      if (arg1.isNumber()) {
        return S.False;
      }
      ISignedNumber signedNumber = arg1.evalReal();
      if (signedNumber != null) {
        return F.bool(signedNumber.isNegative());
      }
      if (arg1.isNegativeInfinity()) {
        return S.True;
      }
      if (arg1.isInfinity()) {
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * NoneTrue({expr1, expr2, ...}, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if no application of <code>test</code> to <code>expr1, expr2, ...
   * </code> evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * NoneTrue(list, test, level)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if no application of <code>test</code> to items of <code>list
   * </code> at <code>level</code> evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * NoneTrue(test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>gives an operator that may be applied to expressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NoneTrue({1, 3, 5}, EvenQ)
   * True
   * &gt;&gt; NoneTrue({1, 4, 5}, EvenQ)
   * False
   * &gt;&gt; NoneTrue({}, EvenQ)
   * True
   * </pre>
   */
  private static class NoneTrue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.arg1().isAST()) {
        IAST list = (IAST) ast.arg1();
        IExpr head = ast.arg2();
        return noneTrue(list, head, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    /**
     * If any expression evaluates to <code>true</code> for a given unary predicate function return
     * <code>False</code>, if all are <code>false</code> return <code>True</code>, else return an
     * <code>Nor(...)</code> expression of the result expressions.
     *
     * @param list list of expressions
     * @param head the head of a unary predicate function
     * @param engine
     * @return
     */
    public IExpr noneTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalNor = F.ast(S.Nor);
      if (list.exists(x -> noneTrueArgument(x, head, logicalNor, engine))) {
        return S.False;
      }
      return logicalNor.isAST0() ? S.True : logicalNor;
    }

    private static boolean noneTrueArgument(
        IExpr x, IExpr head, IASTAppendable resultCollector, EvalEngine engine) {
      IExpr temp = engine.evaluate(F.unaryAST1(head, x));
      if (temp.isTrue()) {
        return true;
      } else if (!temp.isFalse()) {
        resultCollector.append(temp);
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * NonNegative(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is a positive real number or zero.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; {Positive(0), NonNegative(0)}
   * {False,True}
   * </pre>
   */
  private static final class NonNegative extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isNonNegativeResult()) {
        return S.True;
      }
      if (arg1.isNumber()) {
        return S.False;
      }
      ISignedNumber signedNumber = arg1.evalReal();
      if (signedNumber != null) {
        return F.bool(!signedNumber.isNegative());
      }
      if (arg1.isNegativeInfinity()) {
        return S.False;
      }
      if (arg1.isInfinity()) {
        return S.True;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * NonPositive(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is a negative real number or zero.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; {Negative(0), NonPositive(0)}
   * {False,True}
   * </pre>
   */
  private static final class NonPositive extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isNegativeResult() || arg1.isZero()) {
        return S.True;
      }
      if (arg1.isNumber()) {
        return S.False;
      }
      ISignedNumber signedNumber = arg1.evalReal();
      if (signedNumber != null) {
        return F.bool(signedNumber.isNegative() || signedNumber.isZero());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Nor(arg1, arg2, ...)'
   * </pre>
   *
   * <blockquote>
   *
   * <p>Logical NOR function. It evaluates its arguments in order, giving <code>False</code>
   * immediately if any of them are <code>True</code>, and <code>True</code> if they are all <code>
   * False</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Nor(False, False, False)
   * True
   *
   * &gt;&gt; Nor(False, True, a)
   * False
   * </pre>
   */
  private static class Nor extends AbstractCoreFunctionEvaluator implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return S.True;
      }
      if (ast.isAST1()) {
        return F.Not(ast.arg1());
      }
      IASTAppendable result = ast.copyHead();
      boolean evaled = false;

      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = engine.evaluate(ast.get(i));
        if (arg.isTrue()) {
          return S.False;
        } else if (arg.isFalse()) {
          evaled = true;
        } else {
          result.append(arg);
        }
      }
      if (evaled) {
        if (result.isAST0()) {
          return S.True;
        }
        if (result.isAST1()) {
          return F.Not(result.arg1());
        }
        return result;
      }
      return F.NIL;
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
   * Not(expr)
   * </pre>
   *
   * <p>or
   *
   * <pre>
   * !expr
   * </pre>
   *
   * <blockquote>
   *
   * <p>Logical Not function (negation). Returns <code>True</code> if the statement is <code>False
   * </code>. Returns <code>False</code> if the <code>expr</code> is <code>True</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; !True
   * False
   * &gt;&gt; !False
   * True
   * &gt;&gt; !b
   * !b
   * </pre>
   */
  private static class Not extends AbstractArg1 implements IBooleanFormula {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      if (o.isTrue()) {
        return S.False;
      }
      if (o.isFalse()) {
        return S.True;
      }
      if (o.isAST()) {
        IAST temp = (IAST) o;
        if (o.isNot()) {
          return o.first();
        }
        if (temp.isAST2()) {
          int functionID = temp.headID();
          if (functionID > ID.UNKNOWN) {
            switch (functionID) {
              case ID.Exists:
                return F.ForAll(temp.first(), F.Not(temp.second()));
              case ID.ForAll:
                return F.Exists(temp.first(), F.Not(temp.second()));
              case ID.Equal:
                return temp.apply(S.Unequal);
              case ID.Unequal:
                return temp.apply(S.Equal);
              case ID.Greater:
                return temp.apply(S.LessEqual);
              case ID.GreaterEqual:
                return temp.apply(S.Less);
              case ID.Less:
                return temp.apply(S.GreaterEqual);
              case ID.LessEqual:
                return temp.apply(S.Greater);
              default:
                break;
            }
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
   * Or(expr1, expr2, ...)'
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>expr1 || expr2 || ...</code> evaluates each expression in turn, returning <code>True
   * </code> as soon as an expression evaluates to <code>True</code>. If all expressions evaluate to
   * <code>False</code>, <code>Or</code> returns <code>False</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; False || True
   * True
   * </pre>
   *
   * <p>If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Or
   * </code> returns a result in symbolic form:
   *
   * <pre>
   * &gt;&gt; a || False || b
   * a || b
   * </pre>
   */
  private static class Or extends AbstractCoreFunctionEvaluator implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return S.False;
      }

      boolean evaled = false;
      IAST flattenedAST = EvalAttributes.flattenDeep(ast);
      if (flattenedAST.isPresent()) {
        evaled = true;
      } else {
        flattenedAST = ast;
      }

      IASTAppendable result = flattenedAST.copyAppendable();
      IExpr sym;
      int[] symbols = new int[flattenedAST.size()];
      int[] notSymbols = new int[flattenedAST.size()];
      int index = 1;

      for (int i = 1; i < flattenedAST.size(); i++) {
        IExpr arg = flattenedAST.get(i);
        if (arg.isTrue()) {
          return S.True;
        }
        if (arg.isFalse()) {
          result.remove(index);
          evaled = true;
          continue;
        }

        arg = engine.evaluateNIL(arg);
        if (arg.isPresent()) {
          if (arg.isTrue()) {
            return S.True;
          }
          if (arg.isFalse()) {
            result.remove(index);
            evaled = true;
            continue;
          }
          result.set(index, arg);
          evaled = true;
        } else {
          arg = flattenedAST.get(i);
        }

        if (arg.isSymbol()) {
          symbols[i] = arg.hashCode();
        } else if (arg.isNot()) {
          sym = arg.first();
          if (sym.isSymbol()) {
            notSymbols[i] = sym.hashCode();
          }
        }
        index++;
      }
      for (int i = 1; i < symbols.length; i++) {
        if (symbols[i] != 0) {
          for (int j = 1; j < notSymbols.length; j++) {
            if (i != j
                && symbols[i] == notSymbols[j]
                && (result.equalsAt(i, result.get(j).first()))) {
              // Or[a, Not[a]] => True
              return S.True;
            }
          }
        }
      }
      if (result.isAST1()) {
        return result.arg1();
      }
      if (evaled) {
        if (result.isAST0()) {
          return S.False;
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
    }
  }

  /**
   *
   *
   * <pre>
   * Positive(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is a positive real number.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Positive(1)
   * True
   * </pre>
   *
   * <p><code>Positive</code> returns <code>False</code> if <code>x</code> is zero or a complex
   * number:
   *
   * <pre>
   * &gt;&gt; Positive(0)
   * False
   *
   * &gt;&gt; Positive(1 + 2 I)
   * False
   *
   * &gt;&gt; Positive(Pi)
   * True
   *
   * &gt;&gt; Positive(x)
   * Positive(x)
   *
   * &gt;&gt; Positive(Sin({11, 14}))
   * {False, True}
   * </pre>
   */
  private static final class Positive extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isPositiveResult()) {
        return S.True;
      }
      if (arg1.isNumber()) {
        return S.False;
      }
      ISignedNumber signedNumber = arg1.evalReal();
      if (signedNumber != null) {
        return F.bool(signedNumber.isPositive());
      }
      if (arg1.isNegativeInfinity()) {
        return S.False;
      }
      if (arg1.isInfinity()) {
        return S.True;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * SameQ(x, y)
   *
   * x===y
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> and <code>y</code> are structurally identical.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>Any object is the same as itself:
   *
   * <pre>
   * &gt;&gt; a===a
   * True
   * </pre>
   *
   * <p>Unlike <code>Equal</code>, <code>SameQ</code> only yields <code>True</code> if <code>x
   * </code> and <code>y</code> have the same type:
   *
   * <pre>
   * &gt;&gt; {1==1., 1===1.}
   * {True,False}
   * </pre>
   */
  private static final class SameQ extends AbstractCoreFunctionEvaluator
      implements IPredicate, IComparatorFunction {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 1) {
        IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
        return F.bool(!temp.existsLeft((x, y) -> !x.isSame(y)));
      }
      return S.False;
    }
  }

  /**
   *
   *
   * <pre>
   * SatisfiabilityCount(boolean-expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>
   * False</code> and <code>True</code> values for the variables of the boolean expression and
   * return the number of possible combinations.
   *
   * </blockquote>
   *
   * <pre>
   * SatisfiabilityCount(boolean-expr, list-of-variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>
   * False</code> and <code>True</code> values for the <code>list-of-variables</code> and return the
   * number of possible combinations.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SatisfiabilityCount((a || b) &amp;&amp; (! a || ! b), {a, b})
   * 2
   * </pre>
   */
  private static final class SatisfiabilityCount extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST userDefinedVariables;
      IExpr arg1 = ast.arg1();
      try {
        // currently only SAT is available
        String method = "SAT";
        if (ast.size() > 2) {
          userDefinedVariables = ast.arg2().orNewList();
          if (ast.size() > 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
            // "BDD" (binary decision diagram), "SAT", "TREE" ?
            IExpr optionMethod = options.getOption(S.Method);
            if (optionMethod.isString()) {
              method = optionMethod.toString();
            }
          }
        } else {
          VariablesSet vSet = new VariablesSet(arg1);
          userDefinedVariables = vSet.getVarList();
        }
        return logicNGSatisfiabilityCount(arg1, userDefinedVariables);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    /**
     * Use LogicNG MiniSAT method.
     *
     * @param booleanExpression
     * @param variables a list of variables
     * @return
     */
    private static IInteger logicNGSatisfiabilityCount(IExpr booleanExpression, IAST variables) {
      FormulaFactory factory = new FormulaFactory();
      LogicFormula lf = new LogicFormula(factory);
      final Formula formula = lf.expr2BooleanFunction(booleanExpression, false);
      final SATSolver miniSat = MiniSat.miniSat(factory);
      miniSat.add(formula);
      Variable[] vars = lf.ast2Variable(variables);
      List<Assignment> assignments = miniSat.enumerateAllModels(vars);
      return F.ZZ(assignments.size());
    }
  }

  /**
   *
   *
   * <pre>
   * SatisfiabilityInstances(boolean-expr, list-of-variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>
   * False</code> and <code>True</code> values for the <code>list-of-variables</code> and return
   * exactly one instance of <code>True, False</code> combinations if possible.
   *
   * </blockquote>
   *
   * <pre>
   * SatisfiabilityInstances(boolean-expr, list-of-variables, combinations)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>
   * False</code> and <code>True</code> values for the <code>list-of-variables</code> and return up
   * to <code>combinations</code> instances of <code>True, False</code> combinations if possible.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SatisfiabilityInstances((a || b) &amp;&amp; (! a || ! b), {a, b}, All)
   * {{False,True},{True,False}}
   * </pre>
   */
  private static final class SatisfiabilityInstances extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST userDefinedVariables;
      IExpr arg1 = ast.arg1();
      try {
        VariablesSet vSet = new VariablesSet(arg1);
        IAST variablesInFormula = vSet.getVarList();
        // currently only SAT is available
        String method = "SAT";
        int maxChoices = 1;
        if (ast.size() > 2) {
          if (ast.arg2().equals(S.All)) {
            maxChoices = Integer.MAX_VALUE;
            userDefinedVariables = variablesInFormula;
          } else {
            userDefinedVariables = ast.arg2().orNewList();
          }
          IExpr complement = S.Complement.of(engine, userDefinedVariables, variablesInFormula);
          if (complement.size() > 1 && complement.isList()) {
            IASTAppendable or = F.Or();
            or.append(arg1);
            arg1 = or;
            or.appendArgs((IAST) complement);
          }

          if (ast.size() > 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
            // "BDD" (binary decision diagram), "SAT", "TREE" ?
            IExpr optionMethod = options.getOption(S.Method);
            if (optionMethod.isString()) {
              method = optionMethod.toString();
            }
          }

          IExpr argN = ast.last();
          if (!argN.isRule()) {
            if (argN.equals(S.All)) {
              maxChoices = Integer.MAX_VALUE;
            } else if (argN.isNumber()) {
              maxChoices = Validate.checkPositiveIntType(ast, ast.argSize());
            }
          }
        } else {
          userDefinedVariables = variablesInFormula;
        }
        return satisfiabilityInstances(arg1, userDefinedVariables, maxChoices);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ast.topHead(), ve);
      }
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
   * SatisfiableQ(boolean-expr, list-of-variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>
   * False</code> and <code>True</code> values for the <code>list-of-variables</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SatisfiableQ((a || b) &amp;&amp; (! a || ! b), {a, b})
   * True
   * </pre>
   */
  private static final class SatisfiableQ extends AbstractFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable userDefinedVariables;
      IExpr arg1 = ast.arg1();
      try {
        // currently only SAT is available
        String method = "SAT";
        if (ast.size() > 2) {
          if (ast.arg2().isList()) {
            userDefinedVariables = ((IAST) ast.arg2()).copy();
            EvalAttributes.sort(userDefinedVariables);
          } else {
            userDefinedVariables = F.ListAlloc(ast.arg2());
          }
          if (ast.size() > 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
            // "BDD" (binary decision diagram), "SAT", "TREE" ?
            IExpr optionMethod = options.getOption(S.Method);
            if (optionMethod.isString()) {
              method = optionMethod.toString();
            }
          }
          VariablesSet vSet = new VariablesSet(arg1);
          IAST variables = vSet.getVarList();
          if (variables.equals(userDefinedVariables)) {
            return logicNGSatisfiableQ(arg1);
          }

        } else {
          return logicNGSatisfiableQ(arg1);
        }
        return bruteForceSatisfiableQ(arg1, userDefinedVariables, 1) ? S.True : S.False;

      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    /**
     * Use LogicNG MiniSAT method.
     *
     * @param arg1
     * @return
     */
    private static IExpr logicNGSatisfiableQ(IExpr arg1) {
      FormulaFactory factory = new FormulaFactory();
      LogicFormula lf = new LogicFormula(factory);
      final Formula formula = lf.expr2BooleanFunction(arg1, false);
      final SATSolver miniSat = MiniSat.miniSat(factory);
      miniSat.add(formula);
      final Tristate result = miniSat.sat();
      if (result == Tristate.TRUE) {
        return S.True;
      }
      if (result == Tristate.FALSE) {
        return S.False;
      }
      return F.NIL;
    }

    /**
     * Use brute force method.
     *
     * @param expr
     * @param variables
     * @param position
     * @return
     */
    private static boolean bruteForceSatisfiableQ(IExpr expr, IAST variables, int position) {
      if (variables.size() <= position) {
        return EvalEngine.get().evalTrue(expr);
      }
      IExpr sym = variables.get(position);
      if (sym.isSymbol()) {
        if (sym.isBuiltInSymbol() || !sym.isVariable()) {
          // Cannot assign to raw object `1`.
          throw new ArgumentTypeException(
              IOFunctions.getMessage("setraw", F.List(sym), EvalEngine.get()));
        }
        ISymbol symbol = (ISymbol) sym;
        IExpr value = symbol.assignedValue();
        try {
          symbol.assignValue(S.True, false);
          if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
            return true;
          }
        } finally {
          symbol.assignValue(value, false);
        }
        try {
          symbol.assignValue(S.False, false);
          if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
            return true;
          }
        } finally {
          symbol.assignValue(value, false);
        }
      }
      return false;
    }
  }

  /**
   *
   *
   * <pre>
   * TautologyQ(boolean-expr, list-of-variables)
   * </pre>
   *
   * <blockquote>
   *
   * <p>test whether the <code>boolean-expr</code> is satisfiable by all combinations of boolean
   * <code>False</code> and <code>True</code> values for the <code>list-of-variables</code>.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Tautology_(logic)">Wikipedia - Tautology
   *       (logic)</a>
   * </ul>
   */
  private static class TautologyQ extends AbstractFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable userDefinedVariables;
      IExpr arg1 = ast.arg1();
      try {
        if (ast.isAST2()) {
          if (ast.arg2().isList()) {
            userDefinedVariables = ((IAST) ast.arg2()).copy();
            EvalAttributes.sort(userDefinedVariables);
          } else {
            userDefinedVariables = F.ListAlloc(ast.arg2());
          }
          VariablesSet vSet = new VariablesSet(arg1);
          IAST variables = vSet.getVarList();
          if (variables.equals(userDefinedVariables)) {
            return logicNGTautologyQ(arg1);
          }
        } else {
          return logicNGTautologyQ(arg1);
        }

        return bruteForceTautologyQ(arg1, userDefinedVariables, 1) ? S.True : S.False;
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * Use LogicNG MiniSAT method.
     *
     * <p><b>Note:</b> <code>TautologyQ(formula)</code> is equivalent to <code>
     * !SatisfiableQ(!formula)</code>.
     *
     * @param arg1
     * @return
     */
    private static IExpr logicNGTautologyQ(IExpr arg1) {
      IExpr temp = SatisfiableQ.logicNGSatisfiableQ(F.Not(arg1));
      if (temp.isPresent()) {
        return temp.isTrue() ? S.False : S.True;
      }
      return F.NIL;
    }

    /**
     * Use brute force method.
     *
     * @param expr
     * @param variables
     * @param position
     * @return
     */
    private static boolean bruteForceTautologyQ(IExpr expr, IAST variables, int position) {
      if (variables.size() <= position) {
        return EvalEngine.get().evalTrue(expr);
      }
      IExpr sym = variables.get(position);
      if (sym.isSymbol()) {
        if (sym.isBuiltInSymbol() || !sym.isVariable()) {
          // Cannot assign to raw object `1`.
          throw new ArgumentTypeException(
              IOFunctions.getMessage("setraw", F.List(sym), EvalEngine.get()));
        }
        ISymbol symbol = (ISymbol) sym;
        IExpr value = symbol.assignedValue();
        try {
          symbol.assignValue(S.True, false);
          if (!bruteForceTautologyQ(expr, variables, position + 1)) {
            return false;
          }
        } finally {
          symbol.assignValue(value, false);
        }
        try {
          symbol.assignValue(S.False, false);
          if (!bruteForceTautologyQ(expr, variables, position + 1)) {
            return false;
          }
        } finally {
          symbol.assignValue(value, false);
        }
      }
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * TrueQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if and only if <code>expr</code> is <code>True</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; TrueQ(True)
   * True
   * &gt;&gt; TrueQ(False)
   * False
   * &gt;&gt; TrueQ(a)
   * False
   * </pre>
   */
  private static class TrueQ extends AbstractFunctionEvaluator implements IPredicate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.bool(ast.equalsAt(1, S.True));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Unequal(x, y)
   *
   * x != y
   * </pre>
   *
   * <blockquote>
   *
   * <p>yields <code>False</code> if <code>x</code> and <code>y</code> are known to be equal, or
   * <code>True</code> if <code>x</code> and <code>y</code> are known to be unequal.
   *
   * </blockquote>
   *
   * <pre>
   * lhs != rhs
   * </pre>
   *
   * <blockquote>
   *
   * <p>represents the inequality <code>lhs &lt;&gt; rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 1 != 1
   * False
   * </pre>
   *
   * <p>Lists are compared based on their elements:
   *
   * <pre>
   * &gt;&gt; {1} != {2}
   * True
   *
   * &gt;&gt; {1, 2} != {1, 2}
   * False
   *
   * &gt;&gt; {a} != {a}
   * False
   *
   * &gt;&gt; "a" != "b"
   * True
   *
   * &gt;&gt; "a" != "a"
   * False
   *
   * &gt;&gt; Pi != N(Pi)
   * False
   *
   * &gt;&gt; a_ != b_
   * a_ != b_
   *
   * &gt;&gt; a != a != a
   * False
   *
   * &gt;&gt; "abc" != "def" != "abc"
   * False
   *
   * &gt;&gt; a != a != b
   * False
   *
   * &gt;&gt; a != b != a
   * a != b != a
   *
   * &gt;&gt; {Unequal(), Unequal(x), Unequal(1)}
   * {True, True, True}
   * </pre>
   */
  private static final class Unequal extends Equal {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.exists(x -> x.equals(S.Undefined))) {
        return S.Undefined;
      }
      if (ast.size() > 2) {
        IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDECIDABLE;
        if (ast.isAST2()) {
          return unequalNull(ast.arg1(), ast.arg2(), engine);
        }

        IASTMutable result = ast.copy();
        result.setArgs(result.size(), i -> F.expandAll(result.get(i), true, true));
        int i = 2;
        int j;
        while (i < result.size()) {
          j = i;
          while (j < result.size()) {
            //            b = compareTernary(result.get(i - 1), result.get(j++));
            b = result.get(i - 1).equalTernary(result.get(j++), engine);
            if (b == IExpr.COMPARE_TERNARY.TRUE) {
              return S.False;
            } else if (b == IExpr.COMPARE_TERNARY.UNDECIDABLE) {
              return F.NIL;
            }
          }
          i++;
        }
      }
      return S.True;
    }
  }

  /**
   *
   *
   * <pre>
   * UnsameQ(x, y)
   *
   * x=!=y
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> and <code>y</code> are not structurally
   * identical.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>Any object is the same as itself:
   *
   * <pre>
   * &gt;&gt; a=!=a
   *  = False
   * </pre>
   *
   * <pre>
   * ```
   * &gt;&gt; 1=!=1
   * True
   * </pre>
   */
  private static final class UnsameQ extends AbstractCoreFunctionEvaluator
      implements IPredicate, IComparatorFunction {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 1) {
        IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
        if (ast.isAST2()) {
          return F.bool(!temp.arg1().isSame(temp.arg2()));
        }
        int i = 2;
        int j;
        while (i < temp.size()) {
          j = i;
          while (j < temp.size()) {
            if (temp.get(i - 1).isSame(temp.get(j++))) {
              return S.False;
            }
          }
          i++;
        }
        return S.True;
      }
      return S.False;
    }
  }

  /**
   *
   *
   * <pre>
   * Xor(arg1, arg2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>Logical XOR (exclusive OR) function. Returns <code>True</code> if an odd number of the
   * arguments are <code>True</code> and the rest are <code>False</code>. Returns <code>False</code>
   * if an even number of the arguments are <code>True</code> and the rest are <code>False</code>.
   *
   * </blockquote>
   *
   * <p>See: <a href="https://en.wikipedia.org/wiki/Exclusive_or">Wikipedia: Exclusive or</a>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Xor(False, True)
   * True
   * &gt;&gt; Xor(True, True)
   * False
   * </pre>
   *
   * <p>If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Xor
   * </code> returns a result in symbolic form:
   *
   * <pre>
   * &gt;&gt; Xor(a, False, b)
   * Xor(a,b)
   * &gt;&gt; Xor()
   * False
   * &gt;&gt; Xor(a)
   * a
   * &gt;&gt; Xor(False)
   * False
   * &gt;&gt; Xor(True)
   * True
   * &gt;&gt; Xor(a, b)
   * Xor(a,b)
   * </pre>
   */
  private static class Xor extends AbstractFunctionEvaluator implements IBooleanFormula {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEmpty()) {
        return S.False;
      }
      if (ast.size() == 2) {
        return ast.arg1();
      }

      IExpr result = ast.arg1();
      int size = ast.size();
      IASTAppendable xor = F.ast(S.Xor, size - 1);
      boolean evaled = false;
      for (int i = 2; i < size; i++) {
        final IExpr arg = ast.get(i);
        if (arg.isTrue()) {
          if (result.isTrue()) {
            result = S.False;
          } else if (result.isFalse()) {
            result = S.True;
          } else {
            result = S.Not.of(engine, result);
          }
          evaled = true;
        } else if (arg.isFalse()) {
          if (result.isTrue()) {
            result = S.True;
          } else if (result.isFalse()) {
            result = S.False;
          }
          evaled = true;
        } else {
          if (arg.equals(result)) {
            result = S.False;
            evaled = true;
          } else {
            if (result.isTrue()) {
              result = S.Not.of(engine, arg);
              evaled = true;
            } else if (result.isFalse()) {
              result = arg;
              evaled = true;
            } else {
              xor.append(arg);
            }
          }
        }
      }
      if (evaled) {
        xor.append(result);
        return xor;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
    }
  }

  /**
   * Compare if the first and second argument are equal after expanding the arguments.
   *
   * @param a1 first argument
   * @param a2 second argument
   * @return <code>F.NIL</code> or the simplified expression, if equality couldn't be determined.
   */
  private static IExpr equalNull(final IExpr a1, final IExpr a2, EvalEngine engine) {
    if ((a1.isExactNumber() || a1.isString()) //
        && (a2.isExactNumber() || a2.isString())) {
      if (a1.isQuantity() && a2.isQuantity()) {
        return quantityEquals((IQuantity) a1, (IQuantity) a2);
      }
      return a1.equals(a2) ? S.True : S.False;
    }
    IExpr.COMPARE_TERNARY b;
    IExpr arg1 = F.expandAll(a1, true, true);
    IExpr arg2 = F.expandAll(a2, true, true);

    //    b = CONST_EQUAL.compareTernary(arg1, arg2);
    b = arg1.equalTernary(arg2, engine);
    if (b == IExpr.COMPARE_TERNARY.FALSE) {
      return S.False;
    }
    if (b == IExpr.COMPARE_TERNARY.TRUE) {
      return S.True;
    }

    return Equal.simplifyCompare(S.Equal, a1, a2);
  }

  /**
   * If the <code>IQuantity#equals()</code> method could be executed because the same unit types
   * could be derived for comparison, return the result <code>S.True or S.False</code> otherwise
   * return <code>F.NIL</code>.
   *
   * @param q1
   * @param q2
   * @return <code>F.NIL</code> is the evaluation wasn't possible
   */
  private static IExpr quantityEquals(IQuantity q1, IQuantity q2) {
    try {
      if (!q1.unit().equals(q2.unit())) {
        org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
            org.matheclipse.core.tensor.qty.UnitConvert.SI();
        q2 = (IQuantity) unitConvert.to(q1.unit()).apply(q2);
      }
      if (q1.unit().equals(q2.unit())) {
        return F.bool(q1.value().equals(q2.value()));
      }
    } catch (RuntimeException rex) {
      //
    }
    return F.NIL;
  }

  /**
   * If the <code>IQuantity#equals()</code> method could be executed because the same unit types
   * could be derived for comparison, return the result <code>S.True or S.False</code> otherwise
   * return <code>F.NIL</code>.
   *
   * @param q1
   * @param q2
   * @return <code>F.NIL</code> is the evaluation wasn't possible
   */
  private static IExpr quantityUnequals(IQuantity q1, IQuantity q2) {
    try {
      if (!q1.unit().equals(q2.unit())) {
        org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
            org.matheclipse.core.tensor.qty.UnitConvert.SI();
        q2 = (IQuantity) unitConvert.to(q1.unit()).apply(q2);
      }
      if (q1.unit().equals(q2.unit())) {
        return F.bool(!q1.value().equals(q2.value()));
      }
    } catch (RuntimeException rex) {
      //
    }
    return F.NIL;
  }

  /**
   * If the <code>IQuantity#compareTo()</code> method could be executed because the same unit types
   * could be used for comparison, return the result <code>-1, 0 or 1</code> otherwise return <code>
   * Integer.MIN_VALUE</code>
   *
   * @param q1
   * @param q2
   * @return <code>Integer.MIN_VALUE</code> if the <code>compareTo()</code> method could not be
   *     executed, because of different unit types
   */
  private static int quantityCompareTo(IQuantity q1, IQuantity q2) {
    try {
      if (!q1.unit().equals(q2.unit())) {
        org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
            org.matheclipse.core.tensor.qty.UnitConvert.SI();
        q2 = (IQuantity) unitConvert.to(q1.unit()).apply(q2);
      }
      if (q1.unit().equals(q2.unit())) {
        return q1.value().compareTo(q2.value());
      }
    } catch (RuntimeException rex) {
      //
    }
    return Integer.MIN_VALUE;
  }

  /**
   * Compare if the first and second argument are unequal after expanding the arguments.
   *
   * @param a1 first argument
   * @param a2 second argument
   * @return <code>F.NIL</code> or the simplified expression, if equality couldn't be determined.
   */
  public static IExpr unequalNull(IExpr a1, IExpr a2, EvalEngine engine) {
    if ((a1.isExactNumber() || a1.isString()) //
        && (a2.isExactNumber() || a2.isString())) {
      if (a1.isQuantity() && a2.isQuantity()) {
        return quantityUnequals((IQuantity) a1, (IQuantity) a2);
      }
      return a1.equals(a2) ? S.False : S.True;
    }
    IExpr.COMPARE_TERNARY b;
    IExpr arg1 = F.expandAll(a1, true, true);
    IExpr arg2 = F.expandAll(a2, true, true);
    //    b = CONST_EQUAL.compareTernary(arg1, arg2);
    b = arg1.equalTernary(arg2, engine);
    if (b == IExpr.COMPARE_TERNARY.FALSE) {
      return S.True;
    }
    if (b == IExpr.COMPARE_TERNARY.TRUE) {
      return S.False;
    }

    return Equal.simplifyCompare(S.Unequal, arg1, arg2);
  }

  /**
   * Transform <code>Inequality()</code> AST to <code>And()</code> expression.
   *
   * @param ast an Inequality AST with <code>size()>=4</code>.
   * @return
   */
  public static IAST inequality2And(final IAST ast) {
    IASTAppendable result = F.And();
    for (int i = 3; i < ast.size(); i += 2) {
      result.append(F.binaryAST2(ast.get(i - 1), ast.get(i - 2), ast.get(i)));
    }
    return result;
  }

  public static IExpr equals(final IAST ast) {
    return equalNull(ast.arg1(), ast.arg2(), EvalEngine.get()).orElse(ast);
  }

  /**
   * Use LogicNG MiniSAT method.
   *
   * <p>Example: Create a list of rules in the form <code>
   * {{False,True,False,False},{True,False,False,False},...}</code> for the variables <code>
   * {a,b,c,d}</code>
   *
   * @param booleanExpression an expression build from symbols and boolean operators like <code>
   *     And, Or, Not, Xor, Nand, Nor, Implies, Equivalent,...</code>
   * @param variables the possible variables. Example: <code>{a,b,c,d}</code>
   * @param maxChoices maximum number of choices, which satisfy the given boolean expression
   * @return
   */
  public static IAST satisfiabilityInstances(
      IExpr booleanExpression, IAST variables, int maxChoices) {
    LogicFormula lf = new LogicFormula();
    Variable[] vars = lf.ast2Variable(variables);
    List<Assignment> assignments =
        logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maxChoices);
    Map<String, Integer> map = LogicFormula.name2Position(vars);
    IASTAppendable list = F.ListAlloc(assignments.size());
    for (int i = 0; i < assignments.size(); i++) {
      if (i >= maxChoices) {
        break;
      }
      list.append( //
          lf.literals2BooleanList(assignments.get(i).literals(), map) //
          );
    }
    EvalAttributes.sort(list, Comparators.REVERSE_CANONICAL_COMPARATOR);
    return list;
  }

  /**
   * Example: Create a list of rules in the form <code>
   * {{a->False,b->True,c->False,d->False},{a->True,b->False,c->False,d->False},...}</code> for the
   * variables <code>{a,b,c,d}</code>
   *
   * @param booleanExpression an expression build from symbols and boolean operators like <code>
   *     And, Or, Not, Xor, Nand, Nor, Implies, Equivalent,...</code>
   * @param variables the possible variables. Example: <code>{a,b,c,d}</code>
   * @param maxChoices
   * @return
   */
  public static IAST solveInstances(IExpr booleanExpression, IAST variables, int maxChoices) {
    LogicFormula lf = new LogicFormula();
    Variable[] vars = lf.ast2Variable(variables);
    List<Assignment> assignments =
        logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maxChoices);
    Map<String, Integer> map = LogicFormula.name2Position(vars);
    IASTAppendable list = F.ListAlloc(assignments.size());
    for (int i = 0; i < assignments.size(); i++) {
      if (i >= maxChoices) {
        break;
      }
      list.append( //
          lf.literals2VariableList(assignments.get(i).literals(), map) //
          );
    }
    EvalAttributes.sort(list, Comparators.REVERSE_CANONICAL_COMPARATOR);
    return list;
  }

  /**
   * Get the transformation from the ast options. Default is DNF.
   *
   * @param ast
   * @param engine
   * @return <code>null</code> if no or wrong method is defined as option
   */
  private static FormulaTransformation transformation(final IAST ast, EvalEngine engine) {
    int size = ast.argSize();
    if (size > 1 && ast.get(size).isString()) {
      IStringX lastArg = (IStringX) ast.get(size);
      String method = lastArg.toString();
      if (method.equals("DNF") || method.equals("SOP")) {
        return new DNFFactorization();
      } else if (method.equals("CNF") || method.equals("POS")) {
        return new BDDCNFTransformation(); // new CNFFactorization( );
      }
      // `1` currently not supported in `2`.
      IOFunctions.printMessage(ast.topHead(), "unsupported", F.List(lastArg, S.Method), engine);
      return null;
    }
    return new DNFFactorization();
  }

  private static IExpr booleanConvert(final IAST ast, EvalEngine engine) throws ValidateException {
    FormulaTransformation transformation = transformation(ast, engine);
    if (transformation != null) {
      LogicFormula lf = new LogicFormula();
      Formula formula = lf.expr2BooleanFunction(ast.arg1(), false).transform(transformation);
      // CNFSubsumption s = new CNFSubsumption();
      // formula=s.apply(formula, false);
      return lf.booleanFunction2Expr(formula);
    }
    return F.NIL;
  }

  public static List<Assignment> logicNGSatisfiabilityInstances(
      IExpr booleanExpression, Variable[] vars, LogicFormula lf, int maxChoices) {

    final Formula formula = lf.expr2BooleanFunction(booleanExpression, false);
    // MiniSatConfig config = new MiniSatConfig.Builder().initialPhase(true).build();
    final SATSolver miniSat = MiniSat.miniSat(lf.getFactory()); // , config);
    miniSat.add(formula);
    return miniSat.enumerateAllModels(vars);
  }

  /**
   * Convert the XOR expression into DNF format.
   *
   * @param xorForm the <code>Xor(...)</code> expression
   * @return
   */
  private static IAST xorToDNF(IAST xorForm) {
    int size = xorForm.argSize();
    if (size > 2) {
      if (size <= 15) {
        IASTAppendable orAST = F.Or();
        // allBits filled with '1' up to size of bits
        final int allBits = FULL_BITSETS[size - 1];
        for (int i = allBits; i >= 0; i--) {
          int singleBit = 0b1;
          int count = 0;
          for (int j = 0; j < size; j++) {
            if ((singleBit & i) != 0) {
              count++;
            }
            singleBit <<= 1;
          }
          if ((count & 1) == 1) {
            IASTMutable andAST = F.astMutable(S.And, size);
            singleBit = 0b1;
            int startPos = 1;
            int startNotPos = count + 1;
            for (int j = 0; j < size; j++) {
              if ((singleBit & i) == 0) {
                andAST.set(startNotPos++, F.Not(xorForm.get(j + 1)));
              } else {
                andAST.set(startPos++, xorForm.get(j + 1));
              }
              singleBit <<= 1;
            }
            orAST.append(andAST);
          }
        }

        return orAST;
      }
      throw new ASTElementLimitExceeded(Short.MAX_VALUE);
    }
    IExpr arg1 = xorForm.arg1();
    IExpr arg2 = xorForm.arg2();
    return F.Or(F.And(arg1, F.Not(arg2)), F.And(F.Not(arg1), arg2));
  }

  public static void initialize() {
    Initializer.init();
  }

  //  public static IExpr.COMPARE_TERNARY compareEqual(final IExpr o0, final IExpr o1) {
  //    if (o0.isSame(o1)) {
  //      return IExpr.COMPARE_TERNARY.TRUE;
  //    }
  //
  //    if (o0.isTrue()) {
  //      if (o1.isTrue()) {
  //        return IExpr.COMPARE_TERNARY.TRUE;
  //      } else if (o1.isFalse()) {
  //        return IExpr.COMPARE_TERNARY.FALSE;
  //      }
  //    } else if (o0.isFalse()) {
  //      if (o1.isTrue()) {
  //        return IExpr.COMPARE_TERNARY.FALSE;
  //      } else if (o1.isFalse()) {
  //        return IExpr.COMPARE_TERNARY.TRUE;
  //      }
  //    }
  //    if (o0.isConstantAttribute() && o1.isConstantAttribute()) {
  //      return IExpr.COMPARE_TERNARY.FALSE;
  //    }
  //
  //    if ((o0 instanceof StringX) && (o1 instanceof StringX)) {
  //      return IExpr.COMPARE_TERNARY.FALSE;
  //    }
  //    IExpr difference = F.eval(F.Subtract(o0, o1));
  //    if (difference.isNumber()) {
  //      if (difference.isZero()) {
  //        return IExpr.COMPARE_TERNARY.TRUE;
  //      }
  //      return IExpr.COMPARE_TERNARY.FALSE;
  //    }
  //    if (difference.isConstantAttribute()) {
  //      return IExpr.COMPARE_TERNARY.FALSE;
  //    }
  //
  //    if (o0.isNumber() && o1.isNumber()) {
  //      return IExpr.COMPARE_TERNARY.FALSE;
  //    }
  //
  //    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  //  }

  private BooleanFunctions() {}
}
