package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Substitution;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.CFalse;
import org.logicng.formulas.CTrue;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaFactoryConfig;
import org.logicng.formulas.FormulaFactoryConfig.FormulaMergeStrategy;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.knowledgecompilation.bdds.BDD;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.transformations.cnf.BDDCNFTransformation;
import org.logicng.transformations.cnf.CNFFactorization;
import org.logicng.transformations.dnf.DNFFactorization;
import org.logicng.transformations.simplification.AdvancedSimplifier;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.NoEvalException;
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
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.BDDExpr;
import org.matheclipse.core.expression.data.BDDParser;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBooleanFormula;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.parser.client.math.MathException;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public final class BooleanFunctions {

  private static final int[] FULL_BITSETS = new int[] { //
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

      CompareUtil.CONST_EQUAL = new Equal();
      CompareUtil.CONST_GREATER = new Greater();
      CompareUtil.CONST_LESS = new Less();
      CompareUtil.CONST_GREATER_EQUAL = new GreaterEqual();
      CompareUtil.CONST_LESS_EQUAL = new LessEqual();

      S.AllTrue.setEvaluator(new AllTrue());
      S.And.setEvaluator(new And());
      S.AnyTrue.setEvaluator(new AnyTrue());
      S.Between.setEvaluator(new Between());
      S.Boole.setEvaluator(new Boole());
      S.BooleanConvert.setEvaluator(new BooleanConvert());
      S.BooleanFunction.setEvaluator(new BooleanFunction());
      S.BooleanCountingFunction.setEvaluator(new BooleanCountingFunction());
      S.BooleanMaxterms.setEvaluator(new BooleanMaxterms());
      S.BooleanMinimize.setEvaluator(new BooleanMinimize());
      S.BooleanMinterms.setEvaluator(new BooleanMinterms());
      S.BooleanTable.setEvaluator(new BooleanTable());
      S.BooleanVariables.setEvaluator(new BooleanVariables());
      S.Equal.setEvaluator(CompareUtil.CONST_EQUAL);
      S.EqualTo.setEvaluator(new CompareOperator(S.EqualTo, S.Equal));
      S.Equivalent.setEvaluator(new Equivalent());
      S.Exists.setEvaluator(new Exists());
      S.ForAll.setEvaluator(new ForAll());
      S.Greater.setEvaluator(CompareUtil.CONST_GREATER);
      S.GreaterEqual.setEvaluator(new GreaterEqual());
      S.GreaterEqualThan.setEvaluator(new CompareOperator(S.GreaterEqualThan, S.GreaterEqual));
      S.GreaterThan.setEvaluator(new CompareOperator(S.GreaterThan, S.Greater));
      S.Implies.setEvaluator(new Implies());
      S.Inequality.setEvaluator(new Inequality());
      S.Less.setEvaluator(CompareUtil.CONST_LESS);
      S.LessEqual.setEvaluator(new LessEqual());
      S.LessEqualThan.setEvaluator(new CompareOperator(S.LessEqualThan, S.LessEqual));
      S.LessThan.setEvaluator(new CompareOperator(S.LessThan, S.Less));
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
      S.Xnor.setEvaluator(new Xnor());
      S.Xor.setEvaluator(new Xor());

    }
  }

  public static final class LogicFormula {

    /**
     * Create a map which assigns the position of each variable name in the given <code>vars</code>
     * array to the corresponding variable name.
     */
    public static Object2IntMap<String> name2Position(Variable[] vars) {
      Object2IntMap<String> map = new Object2IntArrayMap<String>();
      map.defaultReturnValue(-1);
      for (int i = 0; i < vars.length; i++) {
        map.put(vars[i].name(), i);
      }
      return map;
    }

    public static Object2IntMap<String> name2Position(List<Variable> vars) {
      Object2IntMap<String> map = new Object2IntArrayMap<String>();
      map.defaultReturnValue(-1);
      for (int i = 0; i < vars.size(); i++) {
        map.put(vars.get(i).name(), i);
      }
      return map;
    }

    final FormulaFactory factory;

    Map<IExpr, Variable> symbol2variableMap = new HashMap<IExpr, Variable>();
    Map<Variable, IExpr> variable2symbolMap = new HashMap<Variable, IExpr>();

    public LogicFormula() {
      FormulaFactoryConfig build =
          FormulaFactoryConfig.builder().formulaMergeStrategy(FormulaMergeStrategy.IMPORT).build();
      this.factory = new FormulaFactory(build);
    }

    public LogicFormula(List<Variable> variables) {
      this();
      for (int i = 0; i < variables.size(); i++) {
        addVariableToMap(variables.get(i));
      }
    }

    public LogicFormula(Variable[] variables) {
      this();
      for (int i = 0; i < variables.length; i++) {
        addVariableToMap(variables[i]);
      }
    }

    public Variable[] ast2Variable(final IAST listOfSymbols) {
      Variable[] result = new Variable[listOfSymbols.argSize()];
      for (int i = 1; i < listOfSymbols.size(); i++) {
        final IExpr arg = listOfSymbols.get(i);
        if (arg.isSymbol()) {
          ISymbol symbol = (ISymbol) arg;
          if (symbol.isFalse() || symbol.isTrue()) {
            String str = Errors.getMessage("ivar", F.list(symbol), EvalEngine.get());
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
          String str = Errors.getMessage("ivar", F.list(arg), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
      }
      return result;
    }

    /**
     * Convert the {@link Formula} back to a {@link IExpr}.
     */
    public IExpr booleanFunction2Expr(final Formula formula) {
      if (formula instanceof org.logicng.formulas.And) {
        org.logicng.formulas.And a = (org.logicng.formulas.And) formula;
        IExpr[] result = new IExpr[a.numberOfOperands()];
        int i = 0;
        for (Formula f : a) {
          result[i++] = booleanFunction2Expr(f);
        }
        Arrays.sort(result, Comparators.LEXICAL_COMPARATOR);
        return F.And(result);
      } else if (formula instanceof org.logicng.formulas.Or) {
        org.logicng.formulas.Or a = (org.logicng.formulas.Or) formula;
        IExpr[] result = new IExpr[a.numberOfOperands()];
        int i = 0;
        for (Formula f : a) {
          result[i++] = booleanFunction2Expr(f);
        }
        Arrays.sort(result, Comparators.LEXICAL_COMPARATOR);
        return F.Or(result);
      } else if (formula instanceof org.logicng.formulas.Not) {
        org.logicng.formulas.Not a = (org.logicng.formulas.Not) formula;
        return F.Not(booleanFunction2Expr(a.operand()));
      } else if (formula instanceof CFalse) {
        return S.False;
      } else if (formula instanceof CTrue) {
        return S.True;
      } else if (formula instanceof Literal) {
        Literal a = (Literal) formula;
        if (a.phase()) {
          return mapToSymbol(a.variable());
        }
        IExpr expr = mapToSymbol(a.variable());
        return F.Not(expr);
      }
      String str = Errors.getMessage("argillegal",
          F.list(F.stringx(formula.toString()), F.stringx("LogicFormula")), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }

    public IExpr factorSimplifyCNF(final Formula formula) {
      final AdvancedSimplifier simplifier = new AdvancedSimplifier();
      final Formula simplified = formula.transform(simplifier);
      return booleanFunction2Expr(new CNFFactorization().apply(simplified, false));
    }

    public IExpr factorSimplifyDNF(final Formula formula) {
      final AdvancedSimplifier simplifier = new AdvancedSimplifier();
      final Formula simplified = formula.transform(simplifier);
      return booleanFunction2Expr(new DNFFactorization().apply(simplified, false));
    }

    private IExpr mapToSymbol(Variable v) {
      IExpr s = variable2symbolMap.get(v);
      if (s != null) {
        return s;
      }
      s = F.Dummy(EvalEngine.uniqueName("LF$"));
      variable2symbolMap.put(v, s);
      return s;
    }

    private Formula convertEquivalent(IAST ast, boolean substituteExpressions) {
      Formula[] result1 = new Formula[ast.argSize()];
      Formula[] result2 = new Formula[ast.argSize()];
      for (int i = 1; i < ast.size(); i++) {
        result1[i - 1] = factory.not(expr2LogicNGFormula(ast.get(i), substituteExpressions));
        result2[i - 1] = factory.not(result1[i - 1]);
      }
      return factory.or(factory.and(result1), factory.and(result2));
    }

    public Formula expr2LogicNGFormula(final IExpr logicExpr, boolean substituteExpressions)
        throws ArgumentTypeException {
      if (logicExpr instanceof IAST) {
        final IAST ast = (IAST) logicExpr;
        if (ast.head() instanceof BDDExpr) {
          BDD bdd = ((BDDExpr) ast.head()).toData();
          List<Variable> variableOrder = bdd.getVariableOrder();

          Formula formula = bdd.toFormula();
          if (ast.size() > 1) {
            FormulaFactory factoryBDD = bdd.underlyingKernel().factory();
            HashMap<Variable, Formula> map = new HashMap<Variable, Formula>();
            for (int i = 1; i < ast.size(); i++) {
              Variable value = factoryBDD.variable(ast.get(i).toString());
              map.put(variableOrder.get(i - 1), value);
            }
            return formula.substitute(new Substitution(map));
          }
          return formula;
        }
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
                ast.forEach((x, i) -> {
                  result[i - 1] = factory.not(expr2LogicNGFormula(x, substituteExpressions));
                });
                return factory.or(result);
              }
              break;
            case ID.Nor:
              if (ast.isSameHeadSizeGE(S.Nor, 3)) {
                Formula[] result = new Formula[ast.argSize()];
                ast.forEach((x, i) -> {
                  result[i - 1] = factory.not(expr2LogicNGFormula(x, substituteExpressions));
                });
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
                return expr2LogicNGFormula(dnf, substituteExpressions);
              }
              break;
            case ID.Xnor:
              if (ast.isSameHeadSizeGE(S.Xnor, 3)) {
                IAST dnf = xorToDNF(ast);
                if (dnf.isOr()) {
                  return factory.not(convertOr(dnf, substituteExpressions));
                }
                if (dnf.isAnd()) {
                  return factory.not(convertAnd(dnf, substituteExpressions));
                }
                return factory.not(expr2LogicNGFormula(dnf, substituteExpressions));
              }
              break;
            case ID.Implies:
              if (ast.isAST(S.Implies, 3)) {
                return factory.implication(expr2LogicNGFormula(ast.arg1(), substituteExpressions),
                    expr2LogicNGFormula(ast.arg2(), substituteExpressions));
              }
              break;
            case ID.Not:
              if (ast.isNot()) {
                IExpr expr = ast.arg1();
                return factory.not(expr2LogicNGFormula(expr, substituteExpressions));
              }
              break;
            case ID.NotElement:
              if (ast.isAST(S.NotElement, 3)) {
                return factory.not(
                    expr2LogicNGFormula(F.Element(ast.arg1(), ast.arg2()), substituteExpressions));
              }
              break;
            case ID.Slot:
              return addSymbolOrSlotToMap(ast);
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
        if (!symbol.isVariable() || symbol.hasProtectedAttribute()) {
          String message = Errors.getMessage("ivar", F.list(symbol), EvalEngine.get());
          throw new ArgumentTypeException(message);
        }
        return addSymbolOrSlotToMap(symbol);
      } else if (logicExpr instanceof BDDExpr) {
        BDD bdd = ((BDDExpr) logicExpr).toData();
        return bdd.toFormula();
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
      String str = Errors.getMessage("argillegal", F.list(logicExpr, F.stringx("LogicFormula")),
          EvalEngine.get());
      throw new ArgumentTypeException(str);
    }

    private IExpr addVariableToMap(Variable variable) {
      IExpr v = variable2symbolMap.get(variable);
      if (v == null) {
        v = variableToExpr(variable);
        symbol2variableMap.put(v, variable);
        variable2symbolMap.put(variable, v);
      }
      return v;
    }

    public static IExpr variableToExpr(Variable variable) {
      String name = variable.name();
      final IExpr v;
      if (name.length() > 0 && name.charAt(0) == '#') {
        int slotNumber = 1;
        if (name.length() > 1) {
          String slotString = name.substring(1);
          slotNumber = Integer.parseInt(slotString);
        }
        v = F.Slot(slotNumber);
      } else {
        v = F.symbol(name);
      }
      return v;
    }

    private Formula addSymbolOrSlotToMap(IExpr symbolOrSlot) {
      Variable v = symbol2variableMap.get(symbolOrSlot);
      if (v == null) {
        final Variable value = factory.variable(symbolOrSlot.toString());
        symbol2variableMap.put(symbolOrSlot, value);
        variable2symbolMap.put(value, symbolOrSlot);
        return value;
      }
      return v;
    }

    private Formula convertOr(final IAST ast, boolean substituteExpressions) {
      final Formula[] result = new Formula[ast.argSize()];
      ast.forEach((x, i) -> result[i - 1] = expr2LogicNGFormula(x, substituteExpressions));
      return factory.or(result);
    }

    private Formula convertAnd(final IAST ast, boolean substituteExpressions) {
      final Formula[] result = new Formula[ast.argSize()];
      ast.forEach((x, i) -> result[i - 1] = expr2LogicNGFormula(x, substituteExpressions));
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
          String str = Errors.getMessage("argillegal", F.list(arg, list), EvalEngine.get());
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

    public IAST literals2BooleanList(final SortedSet<Literal> literals, Object2IntMap<String> map) {
      IASTMutable list = F.astMutable(S.List, map.size());
      for (int i = 0; i < map.size(); i++) {
        list.set(i + 1, S.Null);
      }
      for (Literal a : literals) {
        int val = map.getInt(a.name());
        if (val != -1) {
          if (a.phase()) {
            list.set(val + 1, S.True);
          } else {
            list.set(val + 1, S.False);
          }
        }
      }
      return list;
    }

    public IAST literals2VariableList(final SortedSet<Literal> literals,
        Object2IntMap<String> map) {
      IASTMutable list = F.astMutable(S.List, map.size());
      for (int i = 0; i < map.size(); i++) {
        list.set(i + 1, S.Null);
      }

      for (Literal a : literals) {
        final int val = map.getInt(a.name());
        if (val != -1) {
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
      if (ast.arg1().isASTOrAssociation()) {
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

    public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalAnd = F.And();

      if (!list.forAllValues(x -> {
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
          if (temp.isUndefined()) {
            return S.Undefined;
          }
          if (temp.isFalse()) {
            if (flattenedAST.exists(x -> x.isUndefined(), i + 1)) {
              return S.Undefined;
            }
            return S.False;
          } else if (temp.isTrue()) {
            continue;
          }
        }

        temp = engine.evaluateNIL(temp);
        if (temp.isPresent()) {
          if (temp.isFalse()) {
            if (flattenedAST.exists(x -> x.isUndefined(), i + 1)) {
              return S.Undefined;
            }
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
            if (i != j && symbols[i] == notSymbols[j]
                && (result.equalsAt(i, result.get(j).first()))) {
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

  private static class AnyTrue extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
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

    public IExpr anyTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalOr = F.Or();
      if (list.exists(x -> anyTrueArgument(x, head, logicalOr, engine))) {
        return S.True;
      }
      return logicalOr.isAST0() ? S.False : logicalOr;
    }

    private static boolean anyTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector,
        EvalEngine engine) {
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

  private static class Between extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isListOfLists()) {
        if (arg2.argSize() > 0) {
          IAST listOfPairs = (IAST) arg2;
          if (listOfPairs.forAll(IExpr::isList2)) {
            return betweenPairs(arg1, listOfPairs);
          }
        }
      } else if (arg2.isList2()) {
        IExpr min = arg2.first();
        IExpr max = arg2.second();
        return F.And(F.LessEqual(min, arg1), F.LessEqual(arg1, max));
      } else if (arg2.isInterval()) {
        if (arg2.argSize() == 0) {
          return S.False;
        }
        IAST intervalOfPairs = (IAST) arg2;
        if (intervalOfPairs.forAll(IExpr::isList2)) {
          return betweenPairs(arg1, intervalOfPairs);
        }
      }

      if (arg2.isList() || arg2.isInterval()) {
        return Errors.printMessage(S.Between, "pair", F.List(arg2), engine);
      }
      return F.NIL;
    }

    private static IExpr betweenPairs(IExpr x, IAST sequenceOfPairs) {
      if (sequenceOfPairs.argSize() == 1) {
        IAST pair = (IAST) sequenceOfPairs.arg1();
        IExpr min = pair.arg1();
        IExpr max = pair.arg2();
        return F.And(F.LessEqual(min, x), F.LessEqual(x, max));
      }
      IASTAppendable result = F.ast(S.Or, sequenceOfPairs.argSize());
      for (int i = 1; i < sequenceOfPairs.size(); i++) {
        IAST pair = (IAST) sequenceOfPairs.get(i);
        IExpr min = pair.arg1();
        IExpr max = pair.arg2();
        result.append(F.And(F.LessEqual(min, x), F.LessEqual(x, max)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Boole extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isTrue()) {
        return F.C1;
      } else if (arg1.isFalse()) {
        return F.C0;
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

  private static class BooleanConvert extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return booleanConvert(ast, engine);
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

  private static class BooleanFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() == S.BooleanFunction && (ast.argSize() < 1 || ast.argSize() > 4)) {
        return Errors.printMessage(S.BooleanFunction, "argb",
            F.List(S.BooleanFunction, F.ZZ(ast.argSize()), F.C1, F.C4), engine);
      }
      if (ast.head() instanceof BDDExpr && ast.size() > 1) {
        BDDExpr bddExpr = (BDDExpr) ast.head();
        BDD bdd = bddExpr.toData();
        List<Variable> variableOrder = bdd.getVariableOrder();
        if (ast.argSize() != 0 && ast.argSize() != variableOrder.size()) {
          return Errors.printMessage(S.BooleanFunction, "argrx",
              F.List(bddExpr, F.ZZ(ast.argSize()), F.ZZ(variableOrder.size())), engine);
        }
        FormulaFactory factory = bdd.underlyingKernel().factory();
        List<Literal> literals = new ArrayList<Literal>(variableOrder.size());
        for (int i = 0; i < ast.argSize(); i++) {
          Variable variable = variableOrder.get(i);
          final String name = variable.name();
          IExpr expr = ast.get(i + 1);
          if (expr.isTrue()) {
            literals.add(factory.literal(name, true));
          } else if (expr.isFalse()) {
            literals.add(factory.literal(name, false));
          } else {
            return F.NIL;
          }
        }
        BDD restrictedBDD = bdd.restrict(literals);
        if (restrictedBDD.isContradiction()) {
          return S.False;
        }
        if (restrictedBDD.isTautology()) {
          return S.True;
        }
        return BDDExpr.newInstance(restrictedBDD, bddExpr.isPureBooleanFunction());

      }
      if (ast.head() == S.BooleanFunction && (ast.isAST1() || ast.isAST2())) {
        IExpr arg1 = ast.arg1();

        // --- Parse: BooleanFunction("BDD" -> {-3, 0, 1, -2, 1, 3, -1, 2, 1, -1}) ---
        if (arg1.isRule()) {
          IAST rule = (IAST) arg1;
          if (rule.arg1().isString() && rule.arg1().toString().equals("BDD")
              && rule.arg2().isList()) {
            BDDExpr bddExpr = null;
            IAST list = (IAST) rule.arg2();
            int[] intVector = list.toIntVector();
            if (intVector != null && intVector.length >= 1) {
              BDD bdd = BDDParser.parseBDD(intVector, new FormulaFactory());
              if (bdd != null) {
                bddExpr = BDDExpr.newInstance(bdd, true);
                if (ast.isAST2() && ast.arg2().isList()) {
                  IAST variableList = (IAST) ast.arg2();
                  return booleanConvert(variableList.apply(bddExpr), false,
                      variableList.apply(bddExpr), engine);
                }
                return bddExpr;
              }
            }
          }
          return F.NIL;
        }
        // ---------------------------------------------------------------------------

        if (arg1.isListOfRules(false) && arg1.argSize() > 0) {
          IAST listOfRule = (IAST) arg1;
          IAST rule = (IAST) listOfRule.arg1();
          if (rule.arg1().isList()) {
            boolean isPureBooleanFuntion = false;
            IAST lhsRule = (IAST) rule.arg1();
            final int argSize = lhsRule.argSize();

            FormulaFactory factory = new FormulaFactory();
            isPureBooleanFuntion = true;
            Variable[] variables = new Variable[argSize];
            for (int i = 1; i < argSize + 1; i++) {
              variables[i - 1] = factory.variable("#" + i);
            }
            Formula[] orFormula = new Formula[listOfRule.argSize()];
            for (int i = 1; i < listOfRule.size(); i++) {
              rule = (IAST) listOfRule.get(i);
              lhsRule = (IAST) rule.arg1();
              IExpr rhsRule = rule.arg2();
              if (rhsRule.isFalse()) {
                orFormula[i - 1] = factory.falsum();
                continue;
              } else if (rhsRule.isTrue()) {
                Formula[] andFormula = new Formula[argSize];
                for (int j = 0; j < argSize; j++) {
                  IExpr booleValue = lhsRule.get(j + 1);
                  if (booleValue.isTrue()) {
                    andFormula[j] = variables[j];
                  } else if (booleValue.isFalse()) {
                    andFormula[j] = factory.not(variables[j]);
                  } else {
                    return F.NIL;
                  }
                }
                orFormula[i - 1] = factory.and(andFormula);
              } else {
                return F.NIL;
              }
            }
            BDDExpr bddResult =
                BDDExpr.newInstance(factory.or(orFormula).bdd(), isPureBooleanFuntion);
            if (ast.isAST2()) {
              if (ast.arg2().isList() && ast.arg2().argSize() == argSize) {
                IAST variableList = (IAST) ast.arg2();
                return booleanConvert(variableList.apply(bddResult), false,
                    variableList.apply(bddResult), engine);
              }
              return F.NIL;
            }
            return isPureBooleanFuntion ? F.Function(bddResult) : bddResult;
          }
          return F.NIL;
        }
        if (arg1.isInteger() && ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          IInteger k = (IInteger) arg1;
          if (!k.isNegative()) {
            if (arg2.isList()) {
              IAST listOfVariables = (IAST) arg2;
              int n = ast.arg2().argSize();
              if (n > 0 && n <= 64) {
                Formula booleanOrFormula = null;
                Variable[] variables = new Variable[n];
                FormulaFactory factory = new FormulaFactory();
                for (int i = 1; i < listOfVariables.size(); i++) {
                  IExpr variable = listOfVariables.get(i);
                  variables[i - 1] = factory.variable(variable.toString());
                }
                booleanOrFormula = booleanFormula(engine, k, variables, factory);
                if (booleanOrFormula == null) {
                  return F.NIL;
                }
                LogicFormula lf = new LogicFormula(variables);
                return lf.factorSimplifyDNF(booleanOrFormula);
              }
            } else {
              int n = arg2.toIntDefault();
              if (n > 0 && n <= 64) {
                Formula booleanOrFormula = null;
                Variable[] variables = new Variable[n];
                FormulaFactory factory = new FormulaFactory();
                for (int i = 0; i < n; i++) {
                  variables[i] = factory.variable("#" + (i + 1));
                }
                booleanOrFormula = booleanFormula(engine, k, variables, factory);
                if (booleanOrFormula == null) {
                  return F.NIL;
                }
                return BDDExpr.newInstance(booleanOrFormula.bdd(), true);
              }
            }
          }
        }
      }
      return F.NIL;
    }

    private Formula booleanFormula(EvalEngine engine, IInteger k, Variable[] variables,
        FormulaFactory factory) {
      final int n = variables.length;
      BitSet bs = IntegerFunctions.integerToBitSet(k);
      List<Formula> orFormula = new ArrayList<Formula>();
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        Formula[] andFormula = new Formula[n];
        int shiftCounter = i;
        for (int j = n - 1; j >= 0; j--) {
          if ((shiftCounter & 1) == 1) {
            andFormula[j] = variables[j];
          } else {
            andFormula[j] = factory.not(variables[j]);
          }
          shiftCounter >>>= 1;
        }
        orFormula.add(factory.and(andFormula));
      }
      return factory.or(orFormula);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class BooleanCountingFunction extends AbstractFunctionEvaluator {
    private static final int SPEC_AT_MOST = 0;
    private static final int SPEC_EXACT = 1;
    private static final int SPEC_MULTI = 2;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg1();
      IExpr arg2 = ast.arg2();

      IExpr formArg = null;
      if (ast.argSize() == 3) {
        IExpr arg3 = ast.arg3();
        if (!arg3.isString()) {
          return F.NIL;
        }
        formArg = arg3;
      }

      final boolean slotForm;
      final IAST variableList;
      final int n;
      if (arg2.isList()) {
        slotForm = false;
        variableList = (IAST) arg2;
        n = variableList.argSize();
      } else {
        slotForm = true;
        n = arg2.toIntDefault();
        IASTAppendable slots = F.ListAlloc(n > 0 ? n : 1);
        for (int i = 1; i <= n; i++) {
          slots.append(F.Slot(i));
        }
        variableList = slots;
      }
      if (n <= 0 || n > Config.MAX_BOOLEAN_COUNTING_FUNCTION_VARIABLES) {
        return F.NIL;
      }

      int kind;
      int kValue = -1;
      boolean[] allowedCount = null;
      if (spec.isInteger() && !spec.isNegative()) {
        kind = SPEC_AT_MOST;
        kValue = spec.toIntDefault();
        if (kValue < 0) {
          kValue = n;
        }
      } else if (spec.isList()) {
        IAST list = (IAST) spec;
        if (list.argSize() == 1) {
          kind = SPEC_EXACT;
          kValue = list.arg1().toIntDefault();
        } else if (list.argSize() == 2 || list.argSize() == 3) {
          kind = SPEC_MULTI;
          int kmin = list.arg1().toIntDefault();
          int kmax = list.arg2().toIntDefault();
          int step = list.argSize() == 3 ? list.arg3().toIntDefault() : 1;
          if (kmin == Integer.MIN_VALUE || kmax == Integer.MIN_VALUE || step == Integer.MIN_VALUE
              || step <= 0) {
            return F.NIL;
          }
          allowedCount = new boolean[n + 1];
          for (int c = kmin; c <= kmax; c += step) {
            if (c >= 0 && c <= n) {
              allowedCount[c] = true;
            }
          }
        } else {
          return F.NIL;
        }
      } else {
        return F.NIL;
      }

      FormulaFactory factory = new FormulaFactory();
      Variable[] variables = new Variable[n];
      for (int i = 0; i < n; i++) {
        String name = slotForm ? ("#" + (i + 1)) : variableList.get(i + 1).toString();
        variables[i] = factory.variable(name);
      }

      final Formula formula;
      switch (kind) {
        case SPEC_EXACT:
          formula = exactlyFormula(kValue, variables, factory);
          break;
        case SPEC_AT_MOST:
          formula = atMostFormula(kValue, variables, factory);
          break;
        default:
          formula = countingFormula(allowedCount, variables, factory);
          break;
      }

      if (slotForm && formArg == null) {
        return BDDExpr.newInstance(formula.bdd(), true);
      }

      LogicFormula lf = new LogicFormula(variables);
      IExpr result =
          (kind == SPEC_MULTI) ? lf.factorSimplifyDNF(formula) : lf.booleanFunction2Expr(formula);

      if (formArg == null) {
        return result;
      }
      IExpr converted = booleanConvert(F.BooleanConvert(result, formArg), engine);
      if (!converted.isPresent()) {
        return F.NIL;
      }
      return slotForm ? F.Function(converted) : converted;
    }

    private static Formula exactlyFormula(int k, Variable[] variables, FormulaFactory factory) {
      final int n = variables.length;
      if (k < 0 || k > n) {
        return factory.falsum();
      }
      boolean[] selected = new boolean[n];
      List<Formula> orFormula = new ArrayList<Formula>();
      for (int[] subset : combinations(n, k)) {
        Arrays.fill(selected, false);
        for (int idx : subset) {
          selected[idx] = true;
        }
        Formula[] andFormula = new Formula[n];
        for (int j = 0; j < n; j++) {
          andFormula[j] = selected[j] ? variables[j] : factory.not(variables[j]);
        }
        orFormula.add(factory.and(andFormula));
      }
      return factory.or(orFormula);
    }

    private static Formula atMostFormula(int k, Variable[] variables, FormulaFactory factory) {
      final int n = variables.length;
      if (k < 0) {
        return factory.falsum();
      }
      if (k >= n) {
        return factory.verum();
      }
      final int m = n - k;
      List<Formula> orFormula = new ArrayList<Formula>();
      for (int[] subset : combinations(n, m)) {
        Formula[] andFormula = new Formula[m];
        for (int t = 0; t < m; t++) {
          andFormula[t] = factory.not(variables[subset[t]]);
        }
        orFormula.add(factory.and(andFormula));
      }
      return factory.or(orFormula);
    }

    private static Formula countingFormula(boolean[] allowedCount, Variable[] variables,
        FormulaFactory factory) {
      final int n = variables.length;
      List<Formula> orFormula = new ArrayList<Formula>();
      final long total = 1L << n;
      for (long state = 0; state < total; state++) {
        int count = Long.bitCount(state);
        if (count <= n && allowedCount[count]) {
          Formula[] andFormula = new Formula[n];
          long shiftCounter = state;
          for (int j = n - 1; j >= 0; j--) {
            andFormula[j] = ((shiftCounter & 1L) == 1L) ? variables[j] : factory.not(variables[j]);
            shiftCounter >>>= 1;
          }
          orFormula.add(factory.and(andFormula));
        }
      }
      return factory.or(orFormula);
    }

    private static List<int[]> combinations(int n, int k) {
      List<int[]> result = new ArrayList<int[]>();
      if (k < 0 || k > n) {
        return result;
      }
      int[] c = new int[k];
      for (int i = 0; i < k; i++) {
        c[i] = i;
      }
      while (true) {
        result.add(c.clone());
        int i = k - 1;
        while (i >= 0 && c[i] == n - k + i) {
          i--;
        }
        if (i < 0) {
          break;
        }
        c[i]++;
        for (int j = i + 1; j < k; j++) {
          c[j] = c[j - 1] + 1;
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }


  private static class BooleanMinimize extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        LogicFormula lf = new LogicFormula();
        Formula formula = lf.expr2LogicNGFormula(ast.arg1(), true);
        final AdvancedSimplifier simplifier = new AdvancedSimplifier();
        FormulaTransformation transformation = transformation(ast, engine);
        if (transformation == null) {
          return F.NIL;
        }

        final Formula simplified = formula.transform(simplifier).transform(transformation);
        IExpr result = lf.booleanFunction2Expr(simplified);
        return result;
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
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

  private static class BooleanMinterms extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return minMaxtermsEval(ast, engine, true);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * Evaluates the boolean minterms or maxterms for valid signatures and simplifies the resulting
     * Disjunctive/Conjunctive Normal Form. Invalid specifications print a "bspec" message and
     * return F.NIL.
     *
     * @param ast the AST passed to the evaluation
     * @param engine the evaluation engine
     * @param isMinterm true if computing minterms, false for maxterms
     * @return the evaluated expression or F.NIL if evaluation is not possible
     */
    protected static IExpr minMaxtermsEval(final IAST ast, EvalEngine engine, boolean isMinterm) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.argSize() > 1 ? ast.arg2() : null;

      IAST vars = null;
      int n = -1;
      boolean returnFunction = false;

      if (arg2 != null) {
        // Check if arg2 is the number of variables (n) or a list of variables ({a1, a2, ...})
        if (arg2.isInteger()) {
          n = arg2.toIntDefault();
          if (n < 0 || n > 62) {
            return F.NIL;
          }
          returnFunction = true;
          IASTAppendable varsAppendable = F.ListAlloc(n);
          for (int i = 1; i <= n; i++) {
            varsAppendable.append(F.Slot(i));
          }
          vars = varsAppendable;
        } else if (arg2.isList()) {
          vars = (IAST) arg2;
          n = vars.argSize();
          if (n > 62) {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      } else {
        // Single argument signature: BooleanMinterms(matrix)
        if (arg1.isListOfLists() && arg1.size() > 1) {
          IAST firstRow = (IAST) ((IAST) arg1).arg1();
          n = firstRow.argSize();
          if (n > 62) {
            return F.NIL;
          }
          returnFunction = true;
          IASTAppendable varsAppendable = F.ListAlloc(n);
          for (int i = 1; i <= n; i++) {
            varsAppendable.append(F.Slot(i));
          }
          vars = varsAppendable;
        } else {
          return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
        }
      }

      List<Long> kValues = new ArrayList<>();

      // Matrix of truth values (e.g., {{1, 1}, {0, 1}})
      if (arg1.isListOfLists()) {
        IAST matrix = (IAST) arg1;
        for (int i = 1; i <= matrix.argSize(); i++) {
          IExpr rowExpr = matrix.get(i);
          if (rowExpr.isList()) {
            IAST row = (IAST) rowExpr;
            if (row.argSize() != n) {
              return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
            }
            long k = 0L;
            for (int j = 1; j <= row.argSize(); j++) {
              k <<= 1;
              if (row.get(j).isTrue() || row.get(j).equals(F.C1)) {
                k |= 1L;
              } else if (!row.get(j).isFalse() && !row.get(j).equals(F.C0)) {
                return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
              }
            }
            kValues.add(k);
          } else {
            return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
          }
        }
      } else if (arg1.isInteger()) {
        // Single integer k
        long k = arg1.toLongDefault();
        if (k < 0L || (n < 62 && k >= (1L << n))) {
          return F.NIL;
        }
        kValues.add(k);
      } else if (arg1.isList()) {
        // List of integers {k1, k2, ...}
        IAST kList = (IAST) arg1;
        for (int i = 1; i <= kList.argSize(); i++) {
          IExpr kExpr = kList.get(i);
          if (kExpr.isInteger()) {
            long k = kExpr.toLongDefault();
            if (k < 0L || (n < 62 && k >= (1L << n))) {
              return F.NIL;
            }
            kValues.add(k);
          } else {
            return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
          }
        }
      } else {
        // Invalid specification (e.g. Boolean Formula)
        return Errors.printMessage(ast.topHead(), "bspec", F.List(ast), engine);
      }

      // Build logic formula trees utilizing LogicNG
      FormulaFactory factory = new FormulaFactory();
      Variable[] variables = new Variable[n];
      for (int i = 0; i < n; i++) {
        variables[i] = factory.variable(vars.get(i + 1).toString());
      }

      List<Formula> outerFormulas = new ArrayList<>();
      for (long k : kValues) {
        Formula[] innerFormulas = new Formula[n];
        for (int i = 1; i <= n; i++) {
          boolean bit = ((k >> (n - i)) & 1L) == 1L;
          Variable var = variables[i - 1];
          innerFormulas[i - 1] = bit ? var : factory.not(var);
        }
        if (isMinterm) {
          outerFormulas.add(factory.and(innerFormulas));
        } else {
          outerFormulas.add(factory.or(innerFormulas));
        }
      }

      Formula resultFormula;
      if (isMinterm) {
        resultFormula = factory.or(outerFormulas);
      } else {
        resultFormula = factory.and(outerFormulas);
      }

      // Return a Compiled BDD BooleanFunction object directly if requested
      if (returnFunction) {
        return BDDExpr.newInstance(resultFormula.bdd(), true);
      }

      // Process minimization
      LogicFormula lf = new LogicFormula(variables);
      IExpr result;
      if (isMinterm) {
        result = lf.factorSimplifyDNF(resultFormula);
      } else {
        result = lf.factorSimplifyCNF(resultFormula);
      }

      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class BooleanMaxterms extends BooleanMinterms {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return minMaxtermsEval(ast, engine, false);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class BooleanTable extends AbstractFunctionEvaluator {
    private static class BooleanTableFormula {
      public IAST variables;
      public IASTAppendable resultList;
      public EvalEngine engine;

      public BooleanTableFormula(IAST variables, EvalEngine engine) {
        this.variables = variables;
        this.resultList = F.ListAlloc(variables.size());
        this.engine = engine;
      }

      public final IExpr evalSymbolTrue(IExpr expr) {
        if (expr instanceof BDDExpr) {
          expr = variables.setAtCopy(0, expr);
        }
        IExpr result = engine.evaluate(expr);
        return result;
      }

      public IAST booleanTableRecursive(IExpr expr, int position) {
        if (variables.size() <= position) {
          if (expr.isList()) {
            resultList.append(F.mapList((IAST) expr, x -> evalSymbolTrue(x)));
          } else {
            resultList.append(evalSymbolTrue(expr));
          }
          return resultList;
        }
        IExpr sym = variables.get(position);
        if (sym.isBuiltInSymbol() || !sym.isVariable()) {
          throw new ArgumentTypeException(
              Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
        }
        if (sym.isSymbol()) {
          ISymbol symbol = (ISymbol) sym;
          IExpr oldSymbolValue = symbol.assignedValue();
          try {
            symbol.assignValue(S.True, false);
            booleanTableRecursive(expr, position + 1);
          } finally {
            symbol.clearValue(oldSymbolValue);
          }
          try {
            symbol.assignValue(S.False, false);
            booleanTableRecursive(expr, position + 1);
          } finally {
            symbol.clearValue(oldSymbolValue);
          }
          return resultList;
        }
        return F.NIL;
      }
    }

    private static class BooleanTableBDD {
      public IASTAppendable slotValues;
      public IASTAppendable resultList;
      public EvalEngine engine;

      public BooleanTableBDD(int numberOfSlots, EvalEngine engine) {
        this.resultList = F.ListAlloc(numberOfSlots);
        this.slotValues = F.ListAlloc(numberOfSlots);
        for (int i = 0; i < numberOfSlots; i++) {
          this.slotValues.append(S.True);
        }
        this.engine = engine;
      }

      private final IExpr evalSlotTrue(BDDExpr expr) {
        return slotValues.setAtCopy(0, expr).eval(engine);
      }

      public IAST booleanTableBDDRecursive(BDDExpr expr, int position) {
        if (slotValues.size() <= position) {
          resultList.append(evalSlotTrue(expr));
          return resultList;
        }
        int slotNumber = position;
        IExpr value = slotValues.get(slotNumber);
        try {
          slotValues.set(slotNumber, S.True);
          booleanTableBDDRecursive(expr, position + 1);
        } finally {
          slotValues.set(slotNumber, value);
        }
        try {
          slotValues.set(slotNumber, S.False);
          booleanTableBDDRecursive(expr, position + 1);
        } finally {
          slotValues.set(slotNumber, value);
        }
        return resultList;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof BDDExpr) {
        BDDExpr bddExpr = (BDDExpr) ast.arg1();
        final int n;
        if (ast.isAST2()) {
          IAST variables = ast.arg2().makeList();
          n = variables.argSize();
        } else {
          BDD bdd = bddExpr.toData();
          List<Variable> variableOrder = bdd.getVariableOrder();
          n = variableOrder.size();
        }
        BooleanTableBDD btp = new BooleanTableBDD(n, engine);
        return btp.booleanTableBDDRecursive(bddExpr, 1);
      }
      final IAST variables;
      if (ast.isAST2()) {
        variables = ast.arg2().makeList();
      } else {
        variables = BooleanVariables.booleanVariables(ast.arg1());
      }
      BooleanTableFormula btp = new BooleanTableFormula(variables, engine);
      return btp.booleanTableRecursive(ast.arg1(), 1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class BooleanVariables extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr result = booleanVariables(arg1);
      if (result.isEmptyList()) {
        arg1 = engine.evaluate(arg1);
        if (arg1.isPureFunction() && arg1.size() > 1) {
          try {
            IExpr booleanValuedExpression = arg1.first();
            int highestSlotNumber = VariablesSet.highestSlotNumber(booleanValuedExpression);
            if (highestSlotNumber > 0) {
              return F.ZZ(highestSlotNumber);
            }
          } catch (NoEvalException nee) {
          }
          return Errors.printMessage(S.BooleanVariables, "bfun", F.List(ast), engine);
        }
        if (arg1 instanceof BDDExpr) {
          return F.ZZ(((BDDExpr) arg1).toData().getVariableOrder().size());
        }
        result = booleanVariables(arg1);
      }
      return result;
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
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Equal extends AbstractFunctionEvaluator implements IComparatorFunction {
    private static IExpr createComparatorResult(IBuiltInSymbol equalOrUnequalSymbol, IAST lhsAST,
        IExpr rhs) {
      return F.binaryAST2(equalOrUnequalSymbol, lhsAST.rest(), rhs);
    }

    protected static IExpr simplifyCompare(IBuiltInSymbol equalOrUnequalSymbol, IExpr a1,
        IExpr a2) {
      IExpr lhs;
      INumber rhs;
      if (a2.isNumber()) {
        lhs = a1;
        rhs = (INumber) a2;
      } else if (a1.isNumber()) {
        lhs = a2;
        rhs = (INumber) a1;
      } else {
        return F.NIL;
      }
      if (lhs.isAST()) {
        IAST lhsAST = (IAST) lhs;
        if (lhsAST.isTimes()) {
          if (lhsAST.arg1().isNumber()) {
            rhs = rhs.divide((INumber) lhsAST.arg1());
            return createComparatorResult(equalOrUnequalSymbol, lhsAST, rhs);
          }
        } else if (lhsAST.isPlus() && lhsAST.arg1().isNumber()) {
          rhs = rhs.subtract((INumber) lhsAST.arg1());
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
          return equalNIL(ast.arg1(), ast.arg2(), engine);
        }
        boolean evaled = false;
        IASTAppendable result = ast.copyAppendable();
        int i = 2;
        IExpr arg1 = F.expandAll(result.arg1(), true, true);
        while (i < result.size()) {
          IExpr arg2 = F.expandAll(result.get(i), true, true);
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

    private static IExpr equalNIL(final IExpr a1, final IExpr a2, EvalEngine engine) {
      if ((a1.isExactNumber() || a1.isString()) && (a2.isExactNumber() || a2.isString())) {
        if (a1.isQuantity() && a2.isQuantity()) {
          return BooleanFunctions.quantityEquals((IQuantity) a1, (IQuantity) a2);
        }
        return a1.equals(a2) ? S.True : S.False;
      }
      IExpr.COMPARE_TERNARY b;
      final IExpr arg1;
      if (a1.isNumber()) {
        arg1 = a1;
      } else {
        arg1 = engine.evaluate(F.expandAll(a1, true, true));
      }
      IExpr arg2;
      if (a2.isNumber()) {
        arg2 = a2;
      } else {
        arg2 = engine.evaluate(F.expandAll(a2, true, true));
      }

      b = arg1.equalTernary(arg2, engine);
      if (b == IExpr.COMPARE_TERNARY.FALSE) {
        return S.False;
      }
      if (b == IExpr.COMPARE_TERNARY.TRUE) {
        return S.True;
      }

      return BooleanFunctions.Equal.simplifyCompare(S.Equal, a1, a2);
    }
  }

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
          if (boole.isNIL()) {
            boole = S.False;
          } else if (boole.isTrue()) {
            return S.False;
          }
          evaled = true;
        } else if (arg.isTrue()) {
          if (boole.isNIL()) {
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
        } else if (result.isAST1() && boole.isNIL()) {
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
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        evaled = true;
      } else {
        arg1 = ast.arg1();
      }
      IAST vars = arg1.makeList();

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
          return F.Exists(vars, expr, arg3);
        }
        return F.NIL;
      }

      if (expr.isFree(x -> vars.contains(x), true)) {
        return expr;
      }
      if (evaled) {
        return F.Exists(vars, expr);
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

  private static class Greater extends AbstractCoreFunctionEvaluator
      implements ITernaryComparator, IComparatorFunction {
    public static final Greater CONST = new Greater();

    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        if (arg1.isNonNegativeResult()) {
          return S.True;
        }
      } else if (arg2.isPositive()) {
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.False;
        }
      } else if (arg2.isZero()) {
        if (arg1.isPositiveResult()) {
          return S.True;
        }
        if (arg1.isNegativeResult()) {
          return S.False;
        }
      }
      IReal a2 = arg2.evalReal();
      if (a2 != null && !arg1.hasComplexNumber()) {
        if (AbstractAssumptions.assumeGreaterThan(arg1, arg2)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeLessThan(arg1, arg2)) {
          return S.False;
        }
      }
      IReal a1 = arg1.evalReal();
      if (a1 != null && !arg2.hasComplexNumber()) {
        if (AbstractAssumptions.assumeLessThan(arg2, arg1)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeGreaterThan(arg2, arg1)) {
          return S.False;
        }
      }
      return F.NIL;
    }

    /**
     * Lower/upper bound of an operand together with the information whether each bound belongs to
     * the value range. A real number is treated as the degenerate closed interval
     * <code>[x,x]</code>.
     */
    protected static final class Bounds {
      final IExpr lower;
      final IExpr upper;
      final boolean lowerClosed;
      final boolean upperClosed;

      Bounds(IExpr lower, boolean lowerClosed, boolean upperClosed, IExpr upper) {
        this.lower = lower;
        this.lowerClosed = lowerClosed;
        this.upperClosed = upperClosed;
        this.upper = upper;
      }
    }

    /**
     * Extract the bounds of an <code>IntervalData</code> with a single sub-interval, or of a real
     * number. Returns <code>null</code> if the expression has no such bounds.
     */
    protected static Bounds boundsOf(IExpr expr) {
      if (expr.isIntervalData() && expr.size() == 2) {
        IExpr sub = expr.first();
        if (sub.isList4()) {
          IAST subList = (IAST) sub;
          return new Bounds(subList.arg1(), subList.arg2() == S.LessEqual,
              subList.arg3() == S.LessEqual, subList.arg4());
        }
        return null;
      }
      if (expr.isReal()) {
        return new Bounds(expr, true, true, expr);
      }
      return null;
    }

    /**
     * Ternary comparison of two <code>IntervalData</code> bound pairs for the strict
     * <code>Greater</code> relation, taking open/closed bounds into account. Unlike
     * <code>Interval</code>, touching bounds can be decided when at least one of them is open:
     * <code>(1,2) &gt; ...</code> excludes the touching value.
     */
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalDataTernary(Bounds b0, Bounds b1) {
      if (b0.lower.greaterThan(b1.upper).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      }
      if (b0.lower.equals(b1.upper) && (!b0.lowerClosed || !b1.upperClosed)) {
        // the intervals only touch and the touching value is excluded on at least one side
        return IExpr.COMPARE_TERNARY.TRUE;
      }
      if (b0.upper.lessThan(b1.lower).isTrue() || b0.upper.equals(b1.lower)) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    /**
     * Ternary comparison of two intervals for the strict <code>Greater</code> relation. The
     * <code>GreaterEqual</code> / <code>LessEqual</code> subclasses override this with an inclusive
     * variant so that touching intervals (e.g. <code>[1,2]</code> and <code>[2,3]</code>) compare
     * as <code>True</code>.
     */
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(final IExpr lower0,
        final IExpr upper0, final IExpr lower1, final IExpr upper1) {
      if (lower0.greaterThan(upper1).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else {
        if (upper0.lessThan(lower1).isTrue()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (a0.isIntervalData() || a1.isIntervalData()) {
        Bounds b0 = boundsOf(a0);
        Bounds b1 = boundsOf(a1);
        if (b0 != null && b1 != null) {
          return compareGreaterIntervalDataTernary(b0, b1);
        }
      }
      if (a0.isReal()) {
        if (a1.isReal()) {
          return ((IReal) a0).isGT((IReal) a1) ? IExpr.COMPARE_TERNARY.TRUE
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
        if (a1.isInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
        if (a1.isRealResult() || a1.isNegativeInfinity()) {
          return IExpr.COMPARE_TERNARY.TRUE;
        }
      } else if (a0.isNegativeInfinity()) {
        if (a1.isNegativeInfinity()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
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
        if (F.isNotPresent(comp)) {
          return IExpr.COMPARE_TERNARY.UNDECIDABLE;
        }
        return comp > 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
      }

      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult() && !a0.isList()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }

      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    private IAST createComparatorResult(IExpr lhs, IExpr rhs, boolean useOppositeHeader,
        ISymbol originalHead, ISymbol oppositeHead) {
      return F.binaryAST2(useOppositeHeader ? oppositeHead : originalHead, lhs, rhs);
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() <= 2) {
        if (ast.isAST1() && ast.arg1().isUndefined()) {
          return S.Undefined;
        }
        return S.True;
      }

      IASTAppendable flattened;
      if ((flattened = EvalAttributes.flattenDeep(ast)).isPresent()) {
        ast = flattened;
      }
      if (ast instanceof IASTMutable) {
        IExpr temp = engine.evalAttributes((ISymbol) ast.head(), (IASTMutable) ast);
        if (temp.isPresent()) {
          return temp;
        }
      }

      IAST astEvaled = ast;
      if (astEvaled.isAST2()) {
        IExpr arg1 = astEvaled.arg1();
        IExpr arg2 = astEvaled.arg2();
        if (arg1.isUndefined() || arg2.isUndefined()) {
          return S.Undefined;
        }
        IExpr result = simplifyCompare(arg1, arg2);
        if (result.isPresent()) {
          return result;
        }
        IExpr.COMPARE_TERNARY ternaryCompare = prepareCompare(arg1, arg2, engine);
        if (ternaryCompare == IExpr.COMPARE_TERNARY.FALSE) {
          return S.False;
        }
        if (ternaryCompare == IExpr.COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (engine.getAssumptions() != null
            && (arg1.isNumericFunction(true) || arg2.isNumericFunction(true))) {
          IExpr temp2 = checkAssumptions(arg1, arg2);
          if (temp2.isPresent()) {
            return temp2;
          }
        }
        return F.NIL;
      }
      boolean evaled = false;

      IExpr.COMPARE_TERNARY[] cResult = new IExpr.COMPARE_TERNARY[astEvaled.size()];
      cResult[0] = IExpr.COMPARE_TERNARY.TRUE;
      IReal lastReal = null;
      for (int i = 1; i < astEvaled.argSize(); i++) {
        final IExpr arg = astEvaled.get(i);
        if (arg.isUndefined()) {
          return S.Undefined;
        }
        IExpr.COMPARE_TERNARY ternaryCompare = prepareCompare(arg, astEvaled.get(i + 1), engine);
        if (ternaryCompare == IExpr.COMPARE_TERNARY.FALSE) {
          return S.False;
        }
        if (ternaryCompare == IExpr.COMPARE_TERNARY.TRUE) {
          evaled = true;
        }
        cResult[i] = ternaryCompare;
        if (arg.isReal()) {
          if (isTernaryFalse(lastReal, (IReal) arg, engine)) {
            return S.False;
          }
          lastReal = (IReal) arg;
        }
      }
      final IExpr lastArg = astEvaled.get(astEvaled.argSize());
      if (lastArg.isReal() && isTernaryFalse(lastReal, (IReal) lastArg, engine)) {
        return S.False;
      }
      cResult[astEvaled.argSize()] = IExpr.COMPARE_TERNARY.TRUE;
      if (!evaled) {
        return F.NIL;
      }
      int i = 2;
      evaled = false;
      IASTAppendable result = astEvaled.copyAppendable();
      for (int j = 1; j < astEvaled.size(); j++) {
        if (cResult[j - 1] == IExpr.COMPARE_TERNARY.TRUE
            && cResult[j] == IExpr.COMPARE_TERNARY.TRUE) {
          evaled = true;
          result.remove(i - 1);
          continue;
        }
        i++;
      }

      if (evaled) {
        if (result.size() <= 2) {
          return S.True;
        }
        return result;
      }
      return F.NIL;
    }

    private boolean isTernaryFalse(IReal lastArg, IReal currentArg, EvalEngine engine) {
      if (lastArg != null) {
        IExpr.COMPARE_TERNARY ternaryCompare = prepareCompare(lastArg, currentArg, engine);
        if (ternaryCompare == IExpr.COMPARE_TERNARY.FALSE) {
          return true;
        }
      }
      return false;
    }

    @Override
    public IExpr.COMPARE_TERNARY prepareCompare(IExpr a0, IExpr a1, EvalEngine engine) {
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
      return simplifyCompare(a1, a2, S.Greater, S.Less, true);
    }

    protected final IExpr simplifyCompare(IExpr a1, IExpr a2, IBuiltInSymbol originalHead,
        IBuiltInSymbol oppositeHead, boolean setTrue) {
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
          return setTrue ? S.True : S.False;
        }
        if (lhsAST.isNegativeInfinity() && rhs.isRealResult()) {
          return setTrue ? S.False : S.True;
        }
        if (rhs.isInfinity() && lhsAST.isRealResult()) {
          return setTrue ? S.False : S.True;
        }
        if (rhs.isNegativeInfinity() && lhsAST.isRealResult()) {
          return setTrue ? S.True : S.False;
        }
        if (lhsAST.isTimes()) {
          IAST result = lhsAST.partitionTimes(x -> x.isNumericFunction(true), F.C0, F.C1, S.List);
          if (!result.arg1().isZero()) {
            if (result.arg1().hasComplexNumber() || result.arg2().hasComplexNumber()) {
              return Errors.printMessage(S.General, "nord", F.list(result.arg1()),
                  EvalEngine.get());
            }
            if (result.arg1().isNegative()) {
              useOppositeHeader = !useOppositeHeader;
            }
            rhs = rhs.divide(result.arg1());
            return createComparatorResult(result.arg2(), rhs, useOppositeHeader, originalHead,
                oppositeHead);
          }
        } else if (lhsAST.isPlus()) {
          IAST result = lhsAST.partitionPlus(x -> x.isNumericFunction(true), F.C0, F.C0, S.List);
          if (!result.arg1().isZero()) {
            if (result.arg1().hasComplexNumber() || result.arg2().hasComplexNumber()) {
              return Errors.printMessage(S.General, "nord", F.list(result.arg1()),
                  EvalEngine.get());
            }
            rhs = rhs.subtract(result.arg1());
            return createComparatorResult(result.arg2(), rhs, useOppositeHeader, originalHead,
                oppositeHead);
          }
        }
      }
      return F.NIL;
    }
  }

  private static final class GreaterEqual extends Greater {
    public static final GreaterEqual CONST = new GreaterEqual();

    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        if (arg1.isNonNegativeResult()) {
          return S.True;
        }
      } else if (arg2.isPositive()) {
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.False;
        }
      } else if (arg2.isZero()) {
        if (arg1.isNonNegativeResult()) {
          return S.True;
        }
        if (arg1.isNegativeResult()) {
          return S.False;
        }
      }

      IReal a2 = arg2.evalReal();
      if (a2 != null && !arg1.hasComplexNumber()) {
        if (AbstractAssumptions.assumeGreaterEqual(arg1, arg2)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeLessThan(arg1, arg2)) {
          return S.False;
        }
      }
      IReal a1 = arg1.evalReal();
      if (a1 != null && !arg2.hasComplexNumber()) {
        if (AbstractAssumptions.assumeLessThan(arg2, arg1)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeGreaterEqual(arg2, arg1)) {
          return S.False;
        }
      }
      return F.NIL;
    }

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

    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (a0.isQuantity() && a1.isQuantity()) {
        int comp = quantityCompareTo((IQuantity) a0, (IQuantity) a1);
        if (F.isNotPresent(comp)) {
          return IExpr.COMPARE_TERNARY.UNDECIDABLE;
        }
        return comp >= 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
      }
      return super.compareTernary(a0, a1);
    }

    @Override
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(final IExpr lower0,
        final IExpr upper0, final IExpr lower1, final IExpr upper1) {
      // inclusive: lower0 >= upper1 makes the whole interval relation True (touching allowed)
      if (lower0.greaterEqualThan(upper1).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (upper0.lessThan(lower1).isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalDataTernary(Bounds b0, Bounds b1) {
      // inclusive: touching bounds still satisfy ">=" for every pair of values
      if (b0.lower.greaterEqualThan(b1.upper).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      }
      if (b0.upper.lessThan(b1.lower).isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (b0.upper.equals(b1.lower) && (!b0.upperClosed || !b1.lowerClosed)) {
        // b0 lies strictly below b1, so ">=" never holds
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
  }

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
    static final IBuiltInSymbol[] COMPARATOR_SYMBOLS =
        {F.Equal, F.Greater, F.GreaterEqual, F.Less, F.LessEqual, F.Unequal};

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
      for (int i = 0; i < ast.argSize() / 2; i++) {
        final IExpr lhs = ast.get(2 * i + 1);
        final IExpr op = ast.get(2 * i + 2);
        final IExpr rhs = ast.get(2 * i + 3);
        for (int rhsI = 2 * i + 3; rhsI < ast.size(); rhsI += 2) {
          final IExpr arg = engine.evaluate(F.binaryAST2(op, lhs, ast.get(rhsI)));
          if (arg.isFalse()) {
            return S.False;
          }
        }
        IExpr evalRes = engine.evaluate(F.binaryAST2(op, lhs, rhs));
        if (!evalRes.isTrue()) {
          if (engine.evaluate(F.SameQ(lhs, res.get(res.argSize()))).isFalse()) {
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
    }
  }

  private static final class Less extends Greater {
    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        if (arg1.isPositiveResult()) {
          return S.False;
        }
      } else if (arg2.isPositive()) {
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.True;
        }
      } else if (arg2.isZero()) {
        if (arg1.isNegativeResult()) {
          return S.True;
        }
        if (arg1.isPositiveResult()) {
          return S.False;
        }
      }

      IReal a2 = arg2.evalReal();
      if (a2 != null && !arg1.hasComplexNumber()) {
        if (AbstractAssumptions.assumeLessThan(arg1, arg2)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeGreaterThan(arg1, arg2)) {
          return S.False;
        }
      }
      IReal a1 = arg1.evalReal();
      if (a1 != null && !arg2.hasComplexNumber()) {
        if (AbstractAssumptions.assumeGreaterThan(arg2, arg1)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeLessThan(arg2, arg1)) {
          return S.False;
        }
      }
      return F.NIL;
    }

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

    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      return super.compareTernary(a1, a0);
    }
  }

  private static final class LessEqual extends Greater {
    @Override
    protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
      if (arg2.isNegative()) {
        if (arg1.isNonNegativeResult()) {
          return S.False;
        }
      } else if (arg2.isPositive()) {
        if (arg1.isNegativeResult() || arg1.isZero()) {
          return S.True;
        }
      } else if (arg2.isZero()) {
        if (arg1.isNegativeResult()) {
          return S.True;
        }
        if (arg1.isPositiveResult()) {
          return S.False;
        }
      }

      IReal a2 = arg2.evalReal();
      if (a2 != null && !arg1.hasComplexNumber()) {
        if (AbstractAssumptions.assumeLessEqual(arg1, arg2)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeGreaterThan(arg1, arg2)) {
          return S.False;
        }
      }
      IReal a1 = arg1.evalReal();
      if (a1 != null && !arg2.hasComplexNumber()) {
        if (AbstractAssumptions.assumeGreaterThan(arg2, arg1)) {
          return S.True;
        }
        if (AbstractAssumptions.assumeLessEqual(arg2, arg1)) {
          return S.False;
        }
      }
      return F.NIL;
    }

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

    @Override
    public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
      if (a0.equals(a1) && a0.isRealResult() && a1.isRealResult()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (a0.isQuantity() && a1.isQuantity()) {
        int comp = quantityCompareTo((IQuantity) a0, (IQuantity) a1);
        if (F.isNotPresent(comp)) {
          return IExpr.COMPARE_TERNARY.UNDECIDABLE;
        }
        return comp <= 0 ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
      }
      return super.compareTernary(a1, a0);
    }

    @Override
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(final IExpr lower0,
        final IExpr upper0, final IExpr lower1, final IExpr upper1) {
      // LessEqual delegates to Greater with swapped arguments; use the inclusive bound so that
      // touching intervals (e.g. [1,2] <= [2,3]) compare as True.
      if (lower0.greaterEqualThan(upper1).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      } else if (upper0.lessThan(lower1).isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    protected IExpr.COMPARE_TERNARY compareGreaterIntervalDataTernary(Bounds b0, Bounds b1) {
      // see GreaterEqual: LessEqual reaches this with swapped arguments
      if (b0.lower.greaterEqualThan(b1.upper).isTrue()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      }
      if (b0.upper.lessThan(b1.lower).isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (b0.upper.equals(b1.lower) && (!b0.upperClosed || !b1.lowerClosed)) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
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

  private static class Max extends Min {
    private final static ISymbol MAX_DUMMY_SYMBOL = F.Dummy("$Max");

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IASTMutable copy = ast.setAtCopy(0, getDummySymbol());
      IExpr dummyEvaled = engine.evaluateNIL(copy);
      boolean evaled = false;
      if (dummyEvaled.isNIL()) {
        dummyEvaled = copy;
      } else {
        evaled = true;
      }
      if (dummyEvaled.isAST(getDummySymbol())) {
        copy = (IASTMutable) dummyEvaled;
        copy.set(0, S.Max);
        ast = copy;
        if (ast.isAST0()) {
          return F.CNInfinity;
        }
        if (ast.arg1().isInterval()) {
          return IntervalSym.max((IAST) ast.arg1());
        }
        if (ast.arg1().isIntervalData()) {
          try {
            IAST intervalData = IntervalDataSym.normalize((IAST) ast.arg1());
            return F.eval(F.mapFunction(S.Max, intervalData, list -> ((IAST) list).arg4()));
          } catch (ArgumentTypeException e) {
            return F.NIL;
          }
        }

        int allocSize = F.allocLevel1(ast, x -> x.isList());
        IASTAppendable result = F.ast(S.Max, allocSize);
        evaled = flattenListRecursive(ast, result, engine) || evaled;
        return maximum(result, evaled, engine);
      }
      return F.NIL;
    }

    @Override
    protected ISymbol getDummySymbol() {
      return MAX_DUMMY_SYMBOL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    private static IExpr maximum(IAST list, boolean flattenedList, EvalEngine engine) {
      boolean evaled = false;
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

      IExpr comp;
      f = list.copyHead();
      for (int i = 2; i < list.size(); i++) {
        max2 = list.get(i);
        if (max1.equals(max2)) {
          continue;
        }
        comp = engine.evaluate(F.Greater(max1, max2));

        if (comp.isFalse()) {
          max1 = max2;
          evaled = true;
        } else if (comp.isTrue()) {
          evaled = true;
        } else {
          if (max1.isRealResult()) {
            f.append(max2);
          } else {
            f.append(max1);
            max1 = max2;
          }
        }
      }
      if (f.size() > 1) {
        f.append(max1);
        if (!evaled) {
          return F.NIL;
        }
        f.sortInplace();
        return f;
      } else {
        return max1;
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(
          ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
      MAX_DUMMY_SYMBOL.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT);
    }
  }

  private static class Min extends AbstractCoreFunctionEvaluator {
    private final static ISymbol MIN_DUMMY_SYMBOL = F.Dummy("$Min");

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IASTMutable copy = ast.setAtCopy(0, getDummySymbol());
      IExpr dummyEvaled = engine.evaluateNIL(copy);
      boolean evaled = false;
      if (dummyEvaled.isNIL()) {
        dummyEvaled = copy;
      } else {
        evaled = true;
      }
      if (dummyEvaled.isAST(getDummySymbol())) {
        copy = (IASTMutable) dummyEvaled;
        copy.set(0, S.Min);
        ast = copy;
        if (ast.isAST0()) {
          return F.CInfinity;
        }
        if (ast.arg1().isInterval()) {
          return IntervalSym.min((IAST) ast.arg1());
        }
        if (ast.arg1().isIntervalData()) {
          try {
            IAST intervalData = IntervalDataSym.normalize((IAST) ast.arg1());
            return F.eval(F.mapFunction(S.Min, intervalData, list -> ((IAST) list).arg1()));
          } catch (ArgumentTypeException e) {
            return F.NIL;
          }
        }

        int allocSize = F.allocLevel1(ast, x -> x.isList());
        IASTAppendable result = F.ast(S.Min, allocSize);
        evaled = flattenListRecursive(ast, result, engine) || evaled;
        return minimum(result, evaled, engine);
      }
      return F.NIL;
    }

    protected ISymbol getDummySymbol() {
      return MIN_DUMMY_SYMBOL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    protected boolean flattenListRecursive(IAST ast, IASTAppendable result, EvalEngine engine) {
      boolean evaled = false;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        int dim = arg.isVector();
        if (dim >= 0) {
          IExpr normal = arg.normal(false);
          if (normal.isList()) {
            flattenListRecursive((IAST) normal, result, engine);
            evaled = true;
            continue;
          }
        } else if (arg.isListOrAssociation()) {
          flattenListRecursive((IAST) arg, result, engine);
          evaled = true;
          continue;
        }
        result.append(arg);
      }
      return evaled;
    }

    private static IExpr minimum(IAST list, final boolean flattenedList, EvalEngine engine) {
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

      IExpr comp;
      for (int i = 2; i < list.size(); i++) {
        min2 = list.get(i);
        if (min2.isInfinity()) {
          evaled = true;
          continue;
        }

        if (min1.equals(min2)) {
          continue;
        }
        comp = engine.evaluate(F.Less(min1, min2));
        if (comp.isFalse()) {
          min1 = min2;
          evaled = true;
        } else if (comp.isTrue()) {
          evaled = true;
        } else {
          if (min1.isRealResult()) {
            f.append(min2);
          } else {
            f.append(min1);
            min1 = min2;
          }
        }
      }
      if (f.size() > 1) {
        f.append(1, min1);
        if (!evaled) {
          return F.NIL;
        }
        f.sortInplace();
        return f;
      } else {
        return min1;
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(
          ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
      MIN_DUMMY_SYMBOL.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT);

    }
  }

  private static class MinMax extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isList() || arg1.isAssociation() || arg1.isInterval() || arg1.isIntervalData()) {
          if ((arg1.isInterval() || arg1.isIntervalData()) && !isRealValuedInterval(arg1)) {
            // symbolic interval endpoints are not orderable -> leave MinMax unevaluated
            return F.NIL;
          }
          return F.list(F.Min(arg1), F.Max(arg1));
        }
      } else if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg1.isList() || arg1.isAssociation() || arg1.isInterval() || arg1.isIntervalData()) {
          if ((arg1.isInterval() || arg1.isIntervalData()) && !isRealValuedInterval(arg1)) {
            return F.NIL;
          }
          if (arg2.isList()) {
            if (arg2.size() == 3 && arg2.first().isNumericFunction(true)
                && arg2.second().isNumericFunction(true)) {
              return F.list(F.Subtract(F.Min(arg1), arg2.first()),
                  F.Plus(F.Max(arg1), arg2.second()));
            }
          } else if (arg2.isNumericFunction(true)) {
            return F.list(F.Subtract(F.Min(arg1), arg2), F.Plus(F.Max(arg1), arg2));
          } else if (arg2.isAST(S.Scaled, 2) && arg2.first().isNumericFunction(true)) {
            IExpr delta =
                engine.evaluate(F.Times(arg2.first(), F.Subtract(F.Max(arg1), F.Min(arg1))));
            return F.list(F.Subtract(F.Min(arg1), delta), F.Plus(F.Max(arg1), delta));
          }
        }
      }

      return F.NIL;
    }

    /**
     * Test whether every endpoint of an <code>Interval</code> or <code>IntervalData</code> is a
     * real-valued (or infinite) bound, so that <code>Min</code> and <code>Max</code> can order
     * them. Returns <code>false</code> for symbolic endpoints like <code>Interval({a,b})</code>.
     */
    private static boolean isRealValuedInterval(IExpr interval) {
      IAST ast = (IAST) interval;
      for (int i = 1; i < ast.size(); i++) {
        IExpr sub = ast.get(i);
        if (!sub.isList() || ((IAST) sub).size() < 3) {
          return false;
        }
        IAST subList = (IAST) sub;
        IExpr lo = subList.arg1();
        IExpr hi = subList.last();
        if (!isOrderableBound(lo) || !isOrderableBound(hi)) {
          return false;
        }
      }
      return true;
    }

    private static boolean isOrderableBound(IExpr bound) {
      return bound.isRealResult() || bound.isInfinity() || bound.isNegativeInfinity();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

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
      final IReal realNumber = arg1.evalReal();
      if (realNumber != null) {
        return F.booleSymbol(realNumber.isNegative());
      }
      if (arg1.isNegativeInfinity()) {
        return S.True;
      }
      if (arg1.isInfinity()) {
        return S.False;
      }
      if (arg1.isInterval()) {
        COMPARE_TERNARY forAll = IntervalSym.forAll((IAST) arg1, x -> x.isNegativeResult(),
            x -> x.isNonNegativeResult());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
        }
      }
      if (arg1.isIntervalData()) {
        COMPARE_TERNARY forAll = IntervalDataSym.forAll((IAST) arg1, //
            (x, closed) -> closed ? x.isNegativeResult() : (x.isNegativeResult() || x.isZero()), //
            (x, closed) -> x.isNonNegativeResult());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
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
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class NoneTrue extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
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

    public IExpr noneTrue(IAST list, IExpr head, EvalEngine engine) {
      IASTAppendable logicalNor = F.ast(S.Nor);
      if (list.exists(x -> noneTrueArgument(x, head, logicalNor, engine))) {
        return S.False;
      }
      return logicalNor.isAST0() ? S.True : logicalNor;
    }

    private static boolean noneTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector,
        EvalEngine engine) {
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
      final IReal realNumber = arg1.evalReal();
      if (realNumber != null) {
        return F.booleSymbol(!realNumber.isNegative());
      }
      if (arg1.isNegativeInfinity()) {
        return S.False;
      }
      if (arg1.isInfinity()) {
        return S.True;
      }
      if (arg1.isInterval()) {
        COMPARE_TERNARY forAll = IntervalSym.forAll((IAST) arg1, x -> x.isNonNegativeResult(),
            x -> x.isNegativeResult() || x.isZero());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
        }
      }
      if (arg1.isIntervalData()) {
        COMPARE_TERNARY forAll = IntervalDataSym.forAll((IAST) arg1, //
            (x, closed) -> x.isNonNegativeResult(), //
            (x, closed) -> closed ? x.isNegativeResult() : (x.isNegativeResult() || x.isZero()));
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
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
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

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
      final IReal realNumber = arg1.evalReal();
      if (realNumber != null) {
        return F.booleSymbol(realNumber.isNegative() || realNumber.isZero());
      }
      if (arg1.isInterval()) {
        COMPARE_TERNARY forAll = IntervalSym.forAll((IAST) arg1,
            x -> x.isNegativeResult() || x.isZero(), x -> x.isNonNegativeResult());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
        }
      }
      if (arg1.isIntervalData()) {
        COMPARE_TERNARY forAll = IntervalDataSym.forAll((IAST) arg1, //
            (x, closed) -> x.isNegativeResult() || x.isZero(), //
            (x, closed) -> closed ? x.isPositiveResult() : x.isNonNegativeResult());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
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
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

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

  private static class Not extends AbstractArg1 implements IBooleanFormula {
    @Override
    public IExpr e1ObjArg(final IExpr o) {
      if (o.isTrue()) {
        return S.False;
      }
      if (o.isFalse()) {
        return S.True;
      }
      if (o.isUndefined()) {
        return S.Undefined;
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
              case ID.Element:
                return temp.apply(S.NotElement);
              case ID.NotElement:
                return temp.apply(S.Element);
              default:
                break;
            }
          }
        }
      }
      return F.NIL;
    }
  }

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
        if (arg.isUndefined()) {
          return S.Undefined;
        }
        if (arg.isTrue()) {
          if (flattenedAST.exists(x -> x.isUndefined(), i + 1)) {
            return S.Undefined;
          }
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
            if (flattenedAST.exists(x -> x.isUndefined(), i + 1)) {
              return S.Undefined;
            }
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
            if (i != j && symbols[i] == notSymbols[j]
                && (result.equalsAt(i, result.get(j).first()))) {
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
      final IReal realNumber = arg1.evalReal();
      if (realNumber != null) {
        return F.booleSymbol(realNumber.isPositive());
      }
      if (arg1.isNegativeInfinity()) {
        return S.False;
      }
      if (arg1.isInfinity()) {
        return S.True;
      }
      if (arg1.isInterval()) {
        COMPARE_TERNARY forAll = IntervalSym.forAll((IAST) arg1, x -> x.isPositiveResult(),
            x -> x.isNegativeResult() || x.isZero());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
        }
      }
      if (arg1.isIntervalData()) {
        COMPARE_TERNARY forAll = IntervalDataSym.forAll((IAST) arg1, //
            (x, closed) -> closed ? x.isPositiveResult() : x.isNonNegativeResult(), //
            (x, closed) -> x.isNegativeResult() || x.isZero());
        if (forAll == COMPARE_TERNARY.TRUE) {
          return S.True;
        }
        if (forAll == COMPARE_TERNARY.FALSE) {
          return S.False;
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
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static final class SameQ extends AbstractCoreFunctionEvaluator
      implements IPredicate, IComparatorFunction {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 2) {
        IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE, false).orElse(ast);
        if (temp.isAST2()) {
          return temp.arg1().isSame(temp.arg2()) ? S.True : S.False;
        }
        if (temp.existsLeft((x, y) -> !x.isSame(y))) {
          return S.False;
        }
      }
      return S.True;
    }
  }

  private static final class SatisfiabilityCount extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST userDefinedVariables;
      IExpr arg1 = ast.arg1();

      String method = "SAT";
      if (ast.size() > 2) {
        userDefinedVariables = ast.arg2().makeList();
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
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
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    private static IInteger logicNGSatisfiabilityCount(IExpr booleanExpression, IAST variables) {
      LogicFormula lf = new LogicFormula();
      final Formula formula = lf.expr2LogicNGFormula(booleanExpression, true);
      final SATSolver miniSat = MiniSat.miniSat(lf.getFactory());
      miniSat.add(formula);
      Variable[] vars = lf.ast2Variable(variables);
      List<Assignment> assignments = miniSat.enumerateAllModels(vars);
      return F.ZZ(assignments.size());
    }
  }

  private static final class SatisfiabilityInstances extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST userDefinedVariables = F.NIL;
      IExpr arg1 = ast.arg1();

      VariablesSet vSet = new VariablesSet(arg1);
      IAST variablesInFormula = vSet.getVarList();
      String method = "SAT";
      int maxChoices = 1;
      int argSize = ast.argSize();
      if (argSize > 1) {
        IExpr argN = ast.last();
        if (!argN.isRule()) {
          if (argN.equals(S.All)) {
            maxChoices = Integer.MAX_VALUE;
            argSize--;
          } else if (argN.isNumber()) {
            maxChoices = Validate.checkPositiveIntType(ast, argSize);
            argSize--;
          }
        }
        if (argSize > 1) {
          userDefinedVariables = ast.arg2().makeList();
          IExpr complement = S.Complement.of(engine, userDefinedVariables, variablesInFormula);
          if (complement.size() > 1 && complement.isList()) {
            IASTAppendable or = F.Or();
            or.append(arg1);
            arg1 = or;
            or.appendArgs((IAST) complement);
          }

          if (argSize > 2) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
            IExpr optionMethod = options.getOption(S.Method);
            if (optionMethod.isString()) {
              method = optionMethod.toString();
            }
          }
        }
      }
      if (userDefinedVariables.isNIL()) {
        userDefinedVariables = variablesInFormula;
      }
      return satisfiabilityInstances(arg1, userDefinedVariables, maxChoices);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class SatisfiableQ extends AbstractFunctionEvaluator implements IPredicate {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable userDefinedVariables;
      IExpr arg1 = ast.arg1();

      try {
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
      } catch (MathException me) {
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    private static IExpr logicNGSatisfiableQ(IExpr arg1) {
      LogicFormula lf = new LogicFormula();
      final Formula formula = lf.expr2LogicNGFormula(arg1, true);
      final SATSolver miniSat = MiniSat.miniSat(lf.getFactory());
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

    private static boolean bruteForceSatisfiableQ(IExpr expr, IAST variables, int position) {
      if (variables.size() <= position) {
        return EvalEngine.get().evalTrue(expr);
      }
      IExpr sym = variables.get(position);
      if (sym.isSymbol()) {
        if (sym.isBuiltInSymbol() || !sym.isVariable()) {
          throw new ArgumentTypeException(
              Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
        }
        ISymbol symbol = (ISymbol) sym;
        IExpr oldValue = symbol.assignedValue();
        try {
          symbol.assignValue(S.True, false);
          if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
            return true;
          }
        } finally {
          symbol.clearValue(oldValue);
        }
        try {
          symbol.assignValue(S.False, false);
          if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
            return true;
          }
        } finally {
          symbol.clearValue(oldValue);
        }
      }
      return false;
    }
  }

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
      } catch (MathException me) {
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IExpr logicNGTautologyQ(IExpr arg1) {
      IExpr temp = SatisfiableQ.logicNGSatisfiableQ(F.Not(arg1));
      if (temp.isPresent()) {
        return temp.isTrue() ? S.False : S.True;
      }
      return F.NIL;
    }

    private static boolean bruteForceTautologyQ(IExpr expr, IAST variables, int position) {
      if (variables.size() <= position) {
        return EvalEngine.get().evalTrue(expr);
      }
      IExpr sym = variables.get(position);
      if (sym.isSymbol()) {
        if (sym.isBuiltInSymbol() || !sym.isVariable()) {
          throw new ArgumentTypeException(
              Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
        }
        ISymbol symbol = (ISymbol) sym;
        IExpr oldValue = symbol.assignedValue();
        try {
          symbol.assignValue(S.True, false);
          if (!bruteForceTautologyQ(expr, variables, position + 1)) {
            return false;
          }
        } finally {
          symbol.clearValue(oldValue);
        }
        try {
          symbol.assignValue(S.False, false);
          if (!bruteForceTautologyQ(expr, variables, position + 1)) {
            return false;
          }
        } finally {
          symbol.clearValue(oldValue);
        }
      }
      return true;
    }
  }

  private static class TrueQ extends AbstractFunctionEvaluator implements IPredicate {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.equalsAt(1, S.True));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class Unequal extends Equal {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.exists(x -> x.equals(S.Undefined))) {
        return S.Undefined;
      }
      if (ast.size() > 2) {
        IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDECIDABLE;
        if (ast.isAST2()) {
          return unequalNIL(ast.arg1(), ast.arg2(), engine);
        }

        IASTMutable result = ast.copy();
        result.setArgs(result.size(), i -> F.expandAll(result.get(i), true, true));
        int i = 2;
        int j;
        while (i < result.size()) {
          j = i;
          while (j < result.size()) {
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

  private static final class UnsameQ extends AbstractCoreFunctionEvaluator
      implements IPredicate, IComparatorFunction {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 2) {
        IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE, false).orElse(ast);
        if (temp.isAST2()) {
          return temp.arg1().isSame(temp.arg2()) ? S.False : S.True;
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
      }
      return S.True;
    }
  }

  private static class Xnor extends AbstractFunctionEvaluator implements IBooleanFormula {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEmpty()) {
        return S.True;
      }
      if (ast.isAST1()) {
        return S.Not.of(engine, ast.arg1());
      }

      int size = ast.size();
      IASTAppendable xnor = F.ast(S.Xnor, ast.argSize());
      boolean evaled = false;
      boolean invert = false;

      IExpr last = null;
      for (int i = 1; i < size; i++) {
        final IExpr arg = ast.get(i);
        if (arg.isTrue()) {
          invert = !invert;
          evaled = true;
        } else if (arg.isFalse()) {
          evaled = true; // False does not alter parity for Xnor
        } else {
          // Cancel identical adjacent arguments (works due to ORDERLESS attribute)
          if (last != null && arg.equals(last)) {
            xnor.remove(xnor.size() - 1);
            if (xnor.size() > 1) {
              last = xnor.get(xnor.size() - 1);
            } else {
              last = null;
            }
            evaled = true;
          } else {
            xnor.append(arg);
            last = arg;
          }
        }
      }

      if (evaled) {
        IExpr result;
        if (xnor.isAST0()) {
          result = S.True;
        } else if (xnor.isAST1()) {
          result = S.Not.of(engine, xnor.arg1());
        } else {
          result = xnor;
        }

        if (invert) {
          return S.Not.of(engine, result);
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

  private static class Xor extends AbstractFunctionEvaluator implements IBooleanFormula {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEmpty()) {
        return S.False;
      }
      if (ast.isAST1()) {
        return ast.arg1();
      }

      int size = ast.size();
      IASTAppendable xor = F.ast(S.Xor, ast.argSize());
      boolean evaled = false;
      boolean invert = false;

      IExpr last = null;
      for (int i = 1; i < size; i++) {
        final IExpr arg = ast.get(i);
        if (arg.isTrue()) {
          invert = !invert;
          evaled = true;
        } else if (arg.isFalse()) {
          evaled = true;
        } else {
          // Cancel identical adjacent arguments (works due to ORDERLESS attribute)
          if (last != null && arg.equals(last)) {
            xor.remove(xor.size() - 1);
            if (xor.size() > 1) {
              last = xor.get(xor.size() - 1);
            } else {
              last = null;
            }
            evaled = true;
          } else {
            xor.append(arg);
            last = arg;
          }
        }
      }

      if (evaled) {
        IExpr result;
        if (xor.isAST0()) {
          result = S.False;
        } else if (xor.isAST1()) {
          result = xor.arg1();
        } else {
          result = xor;
        }

        if (invert) {
          return S.Not.of(engine, result);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
    }
  }


  private static IExpr quantityEquals(IQuantity q1, IQuantity q2) {
    try {
      if (!q1.unit().equals(q2.unit())) {
        org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
            org.matheclipse.core.tensor.qty.UnitConvert.SI();
        q2 = (IQuantity) unitConvert.to(q1.unit()).apply(q2);
      }
      if (q1.unit().equals(q2.unit())) {
        return F.booleSymbol(q1.value().equals(q2.value()));
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  private static IExpr quantityUnequals(IQuantity q1, IQuantity q2) {
    try {
      if (!q1.unit().equals(q2.unit())) {
        org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
            org.matheclipse.core.tensor.qty.UnitConvert.SI();
        q2 = (IQuantity) unitConvert.to(q1.unit()).apply(q2);
      }
      if (q1.unit().equals(q2.unit())) {
        return F.booleSymbol(!q1.value().equals(q2.value()));
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

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
      Errors.rethrowsInterruptException(rex);
    }
    return Config.INVALID_INT;
  }

  public static IExpr unequalNIL(IExpr a1, IExpr a2, EvalEngine engine) {
    if ((a1.isExactNumber() || a1.isString()) && (a2.isExactNumber() || a2.isString())) {
      if (a1.isQuantity() && a2.isQuantity()) {
        return quantityUnequals((IQuantity) a1, (IQuantity) a2);
      }
      return a1.equals(a2) ? S.False : S.True;
    }
    IExpr.COMPARE_TERNARY b;
    final IExpr arg1;
    if (a1.isNumber()) {
      arg1 = a1;
    } else {
      arg1 = engine.evaluate(F.expandAll(a1, true, true));
    }
    IExpr arg2;
    if (a2.isNumber()) {
      arg2 = a2;
    } else {
      arg2 = engine.evaluate(F.expandAll(a2, true, true));
    }
    b = arg1.equalTernary(arg2, engine);
    if (b == IExpr.COMPARE_TERNARY.FALSE) {
      return S.True;
    }
    if (b == IExpr.COMPARE_TERNARY.TRUE) {
      return S.False;
    }

    return Equal.simplifyCompare(S.Unequal, arg1, arg2);
  }

  private static IExpr equals(final IAST ast) {
    return Equal.equalNIL(ast.arg1(), ast.arg2(), EvalEngine.get()).orElse(ast);
  }

  public static IAST satisfiabilityInstances(IExpr booleanExpression, IAST variables,
      int maxChoices) {
    List<Assignment> assignments;
    LogicFormula lf = new LogicFormula();
    Object2IntMap<String> map;
    if (booleanExpression instanceof BDDExpr) {
      BDD bdd = ((BDDExpr) booleanExpression).toData();
      List<Variable> vars = bdd.getVariableOrder();
      final SATSolver miniSat = MiniSat.miniSat(lf.getFactory());
      miniSat.add(bdd.dnf());
      assignments = miniSat.enumerateAllModels(vars);
      map = LogicFormula.name2Position(vars);
    } else {
      Variable[] vars = lf.ast2Variable(variables);
      assignments = logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maxChoices);
      map = LogicFormula.name2Position(vars);
    }
    IASTAppendable list = F.ListAlloc(assignments.size());
    for (int i = 0; i < assignments.size(); i++) {
      if (i >= maxChoices) {
        break;
      }
      list.append(lf.literals2BooleanList(assignments.get(i).literals(), map));
    }
    EvalAttributes.sort(list, Comparators.REVERSE_CANONICAL_COMPARATOR);
    return list;
  }

  public static IAST solveInstances(IExpr booleanExpression, IAST variables,
      int maximumNumberOfResults) {
    if (!booleanExpression.isBooleanFormula()) {
      return F.NIL;
    }
    LogicFormula lf = new LogicFormula();
    Variable[] vars = lf.ast2Variable(variables);
    List<Assignment> assignments =
        logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maximumNumberOfResults);
    Object2IntMap<String> map = LogicFormula.name2Position(vars);
    IASTAppendable list = F.ListAlloc(assignments.size());
    for (int i = 0; i < assignments.size(); i++) {
      if (i >= maximumNumberOfResults) {
        break;
      }
      list.append(lf.literals2VariableList(assignments.get(i).literals(), map));
    }
    EvalAttributes.sort(list, Comparators.REVERSE_CANONICAL_COMPARATOR);
    return list;
  }

  private static FormulaTransformation transformation(final IAST ast, EvalEngine engine) {
    int size = ast.argSize();
    if (size > 1 && ast.get(size).isString()) {
      IStringX lastArg = (IStringX) ast.get(size);
      String method = lastArg.toString();
      if (method.equals("DNF") || method.equals("SOP")) {
        return new DNFFactorization();
      } else if (method.equals("CNF") || method.equals("POS")) {
        return new BDDCNFTransformation();
      }
      Errors.printMessage(ast.topHead(), "unsupported", F.list(lastArg, S.Method), engine);
      return null;
    }
    return new DNFFactorization();
  }

  public static IExpr booleanConvert(final IAST ast, EvalEngine engine) throws ValidateException {
    IExpr arg1 = ast.arg1();
    boolean isFunction = false;
    if (arg1.isAST(S.Function, 2)) {
      arg1 = arg1.first();
      isFunction = true;
    }
    if (arg1 instanceof BDDExpr) {
      return booleanConvertBDDExpr((BDDExpr) arg1, isFunction, arg1, ast, engine);
    } else if (arg1.head() instanceof BDDExpr) {
      return booleanConvertBDDExpr((BDDExpr) arg1.head(), isFunction, arg1, ast, engine);
    } else {
      IExpr head = engine.evaluate(arg1.head());
      if (head instanceof BDDExpr) {
        return booleanConvertBDDExpr((BDDExpr) head, isFunction, arg1, ast, engine);
      }
    }
    if (ast.isAST2() && ast.arg2().isString()) {
      IStringX arg2 = (IStringX) ast.arg2();
      String method = arg2.toString();
      if (method.equals("BFF") || method.equals("BooleanFunction")) {
        LogicFormula lf = new LogicFormula();
        Formula formula = lf.expr2LogicNGFormula(arg1, true);
        BDDExpr bddExpr = BDDExpr.newInstance(formula.bdd(), false);
        return isFunction ? F.Function(bddExpr) : bddExpr;
      }
    }
    FormulaTransformation transformation = transformation(ast, engine);
    if (transformation != null) {
      LogicFormula lf = new LogicFormula();
      Formula formula = lf.expr2LogicNGFormula(arg1, true).transform(transformation);
      return lf.booleanFunction2Expr(formula);
    }
    return F.NIL;
  }

  private static IExpr booleanConvertBDDExpr(BDDExpr head, boolean isFunction, IExpr arg1,
      final IAST booleanConvertAST, EvalEngine engine) {
    IExpr temp = booleanConvert(head, isFunction, booleanConvertAST, engine);
    if (temp.isPresent()) {
      if (arg1.size() > 0) {
        return ((IAST) arg1).setAtCopy(0, temp);
      }
      return temp;
    }
    return F.NIL;
  }

  private static IExpr booleanConvert(IExpr arg1, boolean isFunction, final IAST ast,
      EvalEngine engine) {
    FormulaTransformation transformation = transformation(ast, engine);
    if (transformation != null) {
      final BDDExpr bddExpr;
      if (arg1 instanceof BDDExpr) {
        bddExpr = (BDDExpr) arg1;
      } else {
        bddExpr = (BDDExpr) arg1.head();
      }
      final BDD data = bddExpr.toData();
      List<Variable> variableOrder = data.getVariableOrder();
      LogicFormula lf = new LogicFormula(variableOrder);
      Formula formula = null;
      if (transformation instanceof BDDCNFTransformation) {
        formula = data.cnf();
      } else if (transformation instanceof DNFFactorization) {
        formula = data.dnf();
      }
      if (formula != null) {
        IExpr result;
        if (transformation instanceof BDDCNFTransformation) {
          result = lf.factorSimplifyCNF(formula);
        } else if (transformation instanceof DNFFactorization) {
          result = lf.factorSimplifyDNF(formula);
        } else {
          result = lf.booleanFunction2Expr(formula);
        }
        if (arg1 instanceof BDDExpr) {
          return bddExpr.isPureBooleanFunction() || isFunction ? F.Function(result) : result;
        }
        return ((IAST) arg1).setAtCopy(0, F.Function(result));
      }
    }
    return F.NIL;
  }

  public static List<Assignment> logicNGSatisfiabilityInstances(IExpr booleanExpression,
      Variable[] vars, LogicFormula lf, int maxChoices) {

    final Formula formula = lf.expr2LogicNGFormula(booleanExpression, true);
    final SATSolver miniSat = MiniSat.miniSat(lf.getFactory());
    miniSat.add(formula);
    return miniSat.enumerateAllModels(vars);
  }

  public static IAST xorToDNF(IAST xorForm) {
    int size = xorForm.argSize();
    if (size > 2) {
      if (size <= 15) {
        IASTAppendable orAST = F.Or();
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

  private BooleanFunctions() {}
}
