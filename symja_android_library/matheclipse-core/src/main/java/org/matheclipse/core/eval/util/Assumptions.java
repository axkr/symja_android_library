package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class Assumptions extends AbstractAssumptions {
  private static class ComplexRelations {

    private final ArrayList<INumber> unequalValues;

    public ComplexRelations() {
      this.unequalValues = new ArrayList<INumber>();
    }

    public final void addUnequals(INumber expr) {
      for (int i = 0; i < unequalValues.size(); i++) {
        if (unequalValues.get(i).equals(expr)) {
          return;
        }
      }
      unequalValues.add(expr);
    }

    public ComplexRelations copy() {
      ComplexRelations clone = new ComplexRelations();
      clone.unequalValues.addAll(this.unequalValues);
      return clone;
    }

    public final ArrayList<INumber> getUnequals() {
      return unequalValues;
    }
  }

  private static class RealRelations {

    EvalEngine engine;
    IAST interval = F.NIL;

    public RealRelations() {
      this.engine = EvalEngine.get();
      this.interval = F.CRealsIntervalData;
    }

    public final void addEqual(IExpr expr, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.close(expr, expr), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.close(expr, expr), engine);
      }
    }

    public final void addGreater(IExpr expr, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.open(expr, F.CInfinity), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.open(expr, F.CInfinity), engine);
      }
    }

    public final void addGreaterEqual(IExpr expr, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.rOpen(expr, F.CInfinity), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.rOpen(expr, F.CInfinity), engine);
      }
    }

    public final void addLess(IExpr expr, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.open(F.CNInfinity, expr), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.open(F.CNInfinity, expr), engine);
      }
    }

    public final void addLess(IExpr lhs, IExpr rhs, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.open(lhs, rhs), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.open(lhs, rhs), engine);
      }
    }

    public final void addLessEqual(IExpr expr, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.lOpen(F.CNInfinity, expr), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.lOpen(F.CNInfinity, expr), engine);
      }
    }

    public final void addLessEqual(IExpr lhs, IExpr rhs, boolean intersection) {
      if (intersection) {
        interval = IntervalDataSym.intersection(interval, //
            IntervalDataSym.close(lhs, rhs), engine);
      } else {
        interval = IntervalDataSym.union(interval, //
            IntervalDataSym.close(lhs, rhs), engine);
      }
    }

    public RealRelations copy() {
      RealRelations clone = new RealRelations();
      clone.engine = this.engine;
      clone.interval = this.interval; 
      return clone;
    }

    public IAST getInterval() {
      return interval;
    }
  }

  private enum RelationalOperator {
    GREATER(S.Greater, RealRelations::addGreater, RealRelations::addLess), //
    GREATER_EQUAL(S.GreaterEqual, RealRelations::addGreaterEqual, RealRelations::addLessEqual), //
    LESS(S.Less, RealRelations::addLess, RealRelations::addGreater), //
    LESS_EQUAL(S.LessEqual, RealRelations::addLessEqual, RealRelations::addGreaterEqual);

    private final ISymbol operator;
    private final TriConsumer<RealRelations, IExpr, Boolean> direct;
    private final TriConsumer<RealRelations, IExpr, Boolean> swapped;

    RelationalOperator(ISymbol operator, TriConsumer<RealRelations, IExpr, Boolean> direct,
        TriConsumer<RealRelations, IExpr, Boolean> swapped) {
      this.operator = operator;
      this.direct = direct;
      this.swapped = swapped;
    }

    public void applyDirect(RealRelations relations, IExpr expr, boolean intersection) {
      direct.accept(relations, expr, intersection);
    }

    public void applySwapped(RealRelations relations, IExpr expr, boolean intersection) {
      swapped.accept(relations, expr, intersection);
    }

    public ISymbol getOperator() {
      return operator;
    }
  }

  @FunctionalInterface
  private interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
  }

  /**
   * Add a distribution.
   *
   * @param element a <code>Distributed(x, &lt;distribution&gt;)</code> expression
   * @param assumptions
   * @return
   */
  private static boolean addDistribution(IAST element, Assumptions assumptions) {
    if (element.arg2().isAST()) {
      IAST dist = (IAST) element.arg2();

      ISymbol head = (ISymbol) dist.head();
      if (head instanceof IBuiltInSymbol) {
        IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
        if (evaluator instanceof IDistribution) {
          IExpr arg1 = element.arg1();
          if (arg1.isAST(S.Alternatives)) {
            ((IAST) arg1).forEach(x -> assumptions.distributionsMap.put(x, dist));
          } else {
            assumptions.distributionsMap.put(arg1, dist);
          }
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Add a domain. Domain can be <code>
   * Algebraics, Booleans, Complexes, Integers, Primes, Rationals, Reals</code>
   *
   * @param element a <code>Element(x, &lt;domain&gt;)</code> expression
   * @param assumptions
   * @return
   */
  private static boolean addElement(IAST element, Assumptions assumptions) {
    if (element.size() >= 3) {
      IExpr arg1 = element.arg1();
      if (arg1.isAlternatives()) {
        IAST list = ((IAST) arg1).apply(S.List);
        list = list.mapThread(element, 1);
        for (int i = 1; i < list.size(); i++) {
          final IExpr arg = list.get(i);
          if (!arg.isAST()) {
            return false;
          }
          if (!addElement((IAST) arg, assumptions)) {
            return false;
          }
        }
        return true;
      }
      if (element.arg2().isSymbol()) {
        ISymbol domain = (ISymbol) element.arg2();
        if (S.isDomain(domain)) {
          if (arg1.isAST(S.Alternatives)) {
            ((IAST) arg1).forEach(x -> assumptions.elementsMap.put(x, domain));
          } else {
            assumptions.elementsMap.put(arg1, domain);
          }
          return true;
        }
      } else if (element.arg2().isAST(S.Arrays, 2, 3)) {
        IAST arrays = (IAST) element.arg2();
        ISymbol domain = S.Complexes;
        if (arrays.size() > 2 && arrays.arg2().isSymbol()) {
          domain = (ISymbol) arrays.arg2();
          if (S.isDomain(domain)) {
            // pass
          } else {
            return false;
          }
        }
        if (arrays.arg1().isList() && arrays.arg1().argSize() >= 2) {
          assumptions.tensorsMap.put(arg1, F.Arrays(arrays.arg1(), domain));
          return true;
        } else {
          // The list `1` of dimensions must have length `2`.
          Errors.printMessage(S.Arrays, "rankl", F.list(arrays.arg1(), F.C2), EvalEngine.get());
        }
      } else if (element.arg2().isAST(S.Matrices, 2, 4)) {
        IAST matrices = (IAST) element.arg2();
        ISymbol domain = S.Complexes;
        if (matrices.size() > 2 && matrices.arg2().isSymbol()) {
          domain = (ISymbol) matrices.arg2();
          if (S.isDomain(domain)) {
            // pass
          } else {
            return false;
          }
        }
        if (matrices.arg1().isList() && matrices.arg1().argSize() == 2) {
          assumptions.tensorsMap.put(arg1, F.Matrices(matrices.arg1(), domain));
          return true;
        } else {
          // The list `1` of dimensions must have length `2`.
          Errors.printMessage(S.Matrices, "rankl", F.list(matrices.arg1(), F.C2), EvalEngine.get());
        }
      } else if (element.arg2().isAST(S.Vectors, 2, 3)) {
        IAST vectors = (IAST) element.arg2();
        ISymbol domain = S.Complexes;
        if (vectors.size() > 2 && vectors.arg2().isSymbol()) {
          domain = (ISymbol) vectors.arg2();
          if (S.isDomain(domain)) {
            // pass
          } else {
            return false;
          }
        }
        if (!vectors.arg1().isList()) {
          assumptions.tensorsMap.put(arg1, F.Vectors(vectors.arg1(), domain));
          return true;
        } else {
          // The list `1` of dimensions must have length `2`.
          Errors.printMessage(S.Vectors, "rankl", F.list(vectors.arg1(), F.C2), EvalEngine.get());
        }
      }
    }
    return false;
  }

  /**
   * Add an equal relation.
   *
   * @param equalsAST a <code>Equal(x, y)</code> or <code>Equal(x, y, z)</code> expression
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>true</code> if the assumption was added successfully, otherwise
   *         <code>false</code>
   */
  private static boolean addEqual(IAST equalsAST, boolean intersection, Assumptions assumptions) {
    // arg1 == arg2
    if (equalsAST.arg2().isNumericFunction(false)) {
      IExpr num = equalsAST.arg2();
      IExpr key = equalsAST.arg1();
      RealRelations relations = assumptions.realRelationsMap.get(key);
      if (relations == null) {
        relations = new RealRelations();
      }
      relations.addEqual(num, intersection);
      assumptions.realRelationsMap.put(key, relations);
      return true;
    }
    if (equalsAST.arg1().isNumericFunction(false)) {
      IExpr num = equalsAST.arg1();
      IExpr key = equalsAST.arg2();
      RealRelations relations = assumptions.realRelationsMap.get(key);
      if (relations == null) {
        relations = new RealRelations();
      }
      relations.addEqual(num, intersection);
      assumptions.realRelationsMap.put(key, relations);
      return true;
    }
    return false;
  }

  /**
   * Add a greater than relation.
   *
   * @param greaterAST a <code>Greater(x, y)</code> or <code>Greater(x, y, z)</code> expression
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>true</code> if the assumption was added successfully, otherwise <code>false
   *     </code>
   */
  private static boolean addGreater(IAST greaterAST, boolean intersection,
      Assumptions assumptions) {
    return addRelationalAssumption(greaterAST, intersection, assumptions,
        RelationalOperator.GREATER);
  }

  /**
   * Add a greater equal relation.
   *
   * @param greaterEqualAST a <code>GreaterEqual(x, y)</code> or <code>GreaterEqual(x, y, z)</code>
   *        expression
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>true</code> if the assumption was added successfully, otherwise <code>false
   *     </code>
   */
  private static boolean addGreaterEqual(IAST greaterEqualAST, boolean intersection,
      Assumptions assumptions) {
    return addRelationalAssumption(greaterEqualAST, intersection, assumptions,
        RelationalOperator.GREATER_EQUAL);
  }

  /**
   * Add a less than relation.
   *
   * @param lessAST a <code>Less(x, y)</code> or <code>Less(x, y, z)</code> expression
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>true</code> if the assumption was added successfully, otherwise <code>false
   *     </code>
   */
  private static boolean addLess(IAST lessAST, boolean intersection, Assumptions assumptions) {
    return addRelationalAssumption(lessAST, intersection, assumptions, RelationalOperator.LESS);
  }

  /**
   * Add a less equal relation.
   *
   * @param lessEqualAST a <code>LessEqual(x, y)</code> or <code>LessEqual(x, y, z)</code>
   *        expression
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>true</code> if the assumption was added successfully, otherwise <code>false
   *     </code>
   */
  private static boolean addLessEqual(IAST lessEqualAST, boolean intersection,
      Assumptions assumptions) {
    return addRelationalAssumption(lessEqualAST, intersection, assumptions,
        RelationalOperator.LESS_EQUAL);
  }

  /**
   * Add assumptions from the given <code>ast</code> expression to the existing interval set.
   * 
   * @param ast the {@link IAST} expression containing the assumptions to be added as an expression
   *        with head {@link S#List},{@link S#And},{@link S#Or}
   * @param intersection if <code>true</code> the assumptions are added as intersection
   *        (corresponding to the {@link S#List} {@link S#And} head) to the existing interval set;
   *        if <code>false</code> the assumptions are added as union (corresponding to the
   *        {@link S#Or} head) to the existing interval set
   * @param assumptions
   * @return <code>null</code> if assumptions couldn't be assigned
   */
  private static IAssumptions addList(IAST ast, boolean intersection, Assumptions assumptions) {
    for (int i = 1; i < ast.size(); i++) {
      if (ast.get(i).isAST()) {
        IAST temp = (IAST) ast.get(i);
        if (temp.isAST(S.Element, 3)) {
          if (!addElement(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Greater, 3, 4)) {
          if (!addGreater(temp, intersection, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.GreaterEqual, 3, 4)) {
          if (!addGreaterEqual(temp, intersection, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Less, 3, 4)) {
          if (!addLess(temp, intersection, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.LessEqual, 3, 4)) {
          if (!addLessEqual(temp, intersection, assumptions)) {
            return null;
          }
        } else if (temp.isEqual()) {
          if (!addEqual(temp, intersection, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Unequal, 3)) {
          if (!addUnequal(temp, assumptions)) {
            return null;
          }
          // } else if (temp.isAnd()) {
          // IAssumptions assum = addList(temp, intersection, assumptions);
          // if (assum == null) {
          // return null;
          // }
        }
      }
    }
    return assumptions;
  }

  private static boolean addRelationalAssumption(IAST relationalAST, boolean intersection,
      Assumptions assumptions, RelationalOperator operator) {
    EvalEngine engine = EvalEngine.get();
    if (relationalAST.isAST3()) {
      // arg1 > arg2 > arg3
      IExpr arg1 = relationalAST.arg1();
      IExpr arg2 = relationalAST.arg2();
      IExpr arg3 = relationalAST.arg3();
      if (arg1.isNumericFunction(false) && arg3.isNumericFunction(false) && !arg2.isNumber()) {
        if (engine.evalTrue(F.binary(operator.getOperator(), arg1, arg3))) {
          IExpr num1 = arg1;
          IExpr num3 = arg3;
          IExpr key = arg2;
          RealRelations relations = assumptions.realRelationsMap.get(key);
          if (relations == null) {
            relations = new RealRelations();
          }
          if (operator == RelationalOperator.GREATER) {
            relations.addLess(num3, num1, intersection);
          } else if (operator == RelationalOperator.GREATER_EQUAL) {
            relations.addLessEqual(num3, num1, intersection);
          } else if (operator == RelationalOperator.LESS) {
            relations.addLess(num1, num3, intersection);
          } else if (operator == RelationalOperator.LESS_EQUAL) {
            relations.addLessEqual(num1, num3, intersection);
          }
          assumptions.realRelationsMap.put(key, relations);
          return true;
        }
      }
      return false;
    }

    // arg1 > arg2
    IExpr num = null;
    if (relationalAST.arg2().isNumericFunction(false)) {
      num = relationalAST.arg2();
    }
    if (num != null) {
      IExpr key = relationalAST.arg1();
      RealRelations relations = assumptions.realRelationsMap.get(key);
      if (relations == null) {
        relations = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      operator.applyDirect(relations, num, intersection);
      assumptions.realRelationsMap.put(key, relations);
      return true;
    }

    num = null;
    if (relationalAST.arg1().isNumericFunction(false)) {
      num = relationalAST.arg1();
    }
    if (num != null) {
      IExpr key = relationalAST.arg2();
      RealRelations relations = assumptions.realRelationsMap.get(key);
      if (relations == null) {
        relations = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      operator.applySwapped(relations, num, intersection);
      assumptions.realRelationsMap.put(key, relations);
      return true;
    }
    return false;
  }

  private static boolean addUnequal(IAST equalsAST, Assumptions assumptions) {
    // arg1 != arg2
    if (equalsAST.arg2().isNumber()) {
      INumber num = (INumber) equalsAST.arg2();
      IExpr key = equalsAST.arg1();

      ComplexRelations relations = assumptions.complexRelationsMap.get(key);
      if (relations == null) {
        relations = new ComplexRelations();
      }
      relations.addUnequals(num);
      assumptions.complexRelationsMap.put(key, relations);
      return true;
    }
    if (equalsAST.arg1().isNumber()) {
      INumber num = (INumber) equalsAST.arg1();
      IExpr key = equalsAST.arg2();
      ComplexRelations relations = assumptions.complexRelationsMap.get(key);
      if (relations == null) {
        relations = new ComplexRelations();
      }
      relations.addUnequals(num);
      assumptions.complexRelationsMap.put(key, relations);
      return true;
    }
    return false;
  }

  @Override
  public IAssumptions copy() {
    Assumptions assumptions = new Assumptions();
    assumptions.distributionsMap = new HashMap<>(distributionsMap);
    assumptions.elementsMap = new HashMap<>(elementsMap);

    assumptions.realRelationsMap = new HashMap<>();
    for (Map.Entry<IExpr, RealRelations> entry : realRelationsMap.entrySet()) {
      assumptions.realRelationsMap.put(entry.getKey(), entry.getValue().copy());
    }

    assumptions.complexRelationsMap = new HashMap<>();
    for (Map.Entry<IExpr, ComplexRelations> entry : complexRelationsMap.entrySet()) {
      assumptions.complexRelationsMap.put(entry.getKey(), entry.getValue().copy());
    }
    return assumptions;
  }

  /**
   * Create a new empty <code>IAssumptions</code>.
   * 
   * @return the empty assumptions instance
   */
  public static IAssumptions getInstance() {
    return new Assumptions();
  }

  public static IAssumptions getInstance(IExpr expr) {
    return getInstance(expr, EvalEngine.get());
  }

  /**
   * Create a new <code>IAssumptions</code> from the given expression. If the creation is not
   * possible return <code>null</code>
   *
   * @param expr
   * @param engine the evaluation engine
   * @return <code>null</code> if <code>Assumptions</code> could not be created from the given
   *         expression.
   */
  public static IAssumptions getInstance(IExpr expr, EvalEngine engine) {
    if (expr.isAST()) {
      Assumptions assumptions = new Assumptions();
      // assumptions.$assumptions = expr;
      if (expr.isAnd() || expr.isSameHeadSizeGE(S.List, 2)) {
        Assumptions.addList((IAST) expr, true, assumptions);
      } else if (expr.isAST()) {
        assumptions.addAssumption(expr);
      }
      if (assumptions.isContradictory()) {
        Errors.printMessage(S.$Assumptions, "cas", F.List(expr));
        return null;
      }
      return assumptions;
    }
    return null;
  }

  /** Map for storing the domain of an expression */
  private HashMap<IExpr, ISymbol> elementsMap = new HashMap<IExpr, ISymbol>();

  private HashMap<IExpr, IAST> distributionsMap = new HashMap<IExpr, IAST>();

  private Map<IExpr, IAST> tensorsMap = new HashMap<IExpr, IAST>();

  private HashMap<IExpr, RealRelations> realRelationsMap = new HashMap<IExpr, RealRelations>();

  private HashMap<IExpr, ComplexRelations> complexRelationsMap =
      new HashMap<IExpr, ComplexRelations>();

  private Assumptions() {}

  @Override
  public IAssumptions addAssumption(IExpr expr) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isAST(S.Element, 3)) {
        if (addElement(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.Greater, 3, 4)) {
        if (addGreater(ast, true, this)) {
          return this;
        }
      } else if (ast.isAST(S.GreaterEqual, 3, 4)) {
        if (addGreaterEqual(ast, true, this)) {
          return this;
        }
      } else if (ast.isAST(S.Less, 3, 4)) {
        if (addLess(ast, true, this)) {
          return this;
        }
      } else if (ast.isAST(S.LessEqual, 3, 4)) {
        if (addLessEqual(ast, true, this)) {
          return this;
        }
      } else if (ast.isEqual()) {
        if (addEqual(ast, true, this)) {
          return this;
        }
      } else if (ast.isAST(S.Unequal, 3)) {
        if (addUnequal(ast, this)) {
          return this;
        }
      } else if (ast.isSameHeadSizeGE(S.And, 2) || ast.isSameHeadSizeGE(S.List, 2)) {
        return addList(ast, true, this);
      } else if (ast.isSameHeadSizeGE(S.Or, 2)) {
        return addList(ast, false, this);
      } else if (ast.isAST(S.Distributed, 3)) {
        if (addDistribution(ast, this)) {
          return this;
        }
      }
    }
    return this;
  }

  private boolean checkDomainProperty(IExpr expr, IExpr number,
      java.util.function.BiFunction<IAST, IExpr, IAST> intervalFunction) {
    RealRelations relations = realRelationsMap.get(expr);
    if (relations != null) {
      IAST interval = relations.getInterval();
      IAST newInterval =
          IntervalDataSym.intersection(interval, intervalFunction.apply(interval, number));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final IAST distribution(IExpr expr) {
    IAST dist = distributionsMap.get(expr);
    return (dist == null) ? F.NIL : dist;
  }

  @Override
  public Map<IExpr, IAST> getTensorsMap() {
    return tensorsMap;
  }

  @Override
  public IAST intervalData(IExpr expr) {
    RealRelations relations = realRelationsMap.get(expr);
    if (relations != null) {
      return relations.getInterval();
    }
    return F.NIL;
  }

  @Override
  public boolean isAlgebraic(IExpr expr) {
    return isDomain(expr, S.Algebraics);
  }

  @Override
  public boolean isBoolean(IExpr expr) {
    return isDomain(expr, S.Booleans);
  }

  @Override
  public boolean isComplex(IExpr expr) {
    return isDomain(expr, S.Complexes);
  }

  @Override
  public boolean isContradictory() {
    if (realRelationsMap.isEmpty()) {
      return false;
    }
    for (RealRelations val : realRelationsMap.values()) {
      IAST interval = val.getInterval();
      if (IntervalDataSym.isEmptySet(interval)) {
        return true;
      }
    }
    return false;
  }

  private final boolean isDomain(IExpr expr, ISymbol domain) {
    ISymbol mappedDomain = elementsMap.get(expr);
    return mappedDomain != null && mappedDomain.equals(domain);
  }

  @Override
  public boolean isEqual(IExpr expr, IExpr number) {
    return checkDomainProperty(expr, number, (i, n) -> IntervalDataSym.close(n, n));
  }

  @Override
  public boolean isGreaterEqual(IExpr expr, IExpr number) {
    return checkDomainProperty(expr, number, (i, n) -> IntervalDataSym.rOpen(n, F.CInfinity));
  }

  @Override
  public boolean isGreaterThan(IExpr expr, IExpr number) {
    return checkDomainProperty(expr, number, (i, n) -> IntervalDataSym.open(n, F.CInfinity));
  }

  @Override
  public boolean isInteger(IExpr expr) {
    return isDomain(expr, S.Integers);
  }

  @Override
  public boolean isLessEqual(IExpr expr, IExpr number) {
    return checkDomainProperty(expr, number, (i, n) -> IntervalDataSym.lOpen(F.CNInfinity, n));
  }

  @Override
  public boolean isLessThan(IExpr expr, IExpr number) {
    return checkDomainProperty(expr, number, (i, n) -> IntervalDataSym.open(F.CNInfinity, n));
  }

  @Override
  public boolean isNegative(IExpr expr) {
    return isLessThan(expr, F.C0);
  }

  @Override
  public boolean isNegativeRational(IExpr expr) {
    return isDomain(expr, S.NegativeRationals);
  }

  @Override
  public boolean isNegativeReal(IExpr expr) {
    return isDomain(expr, S.NegativeReals);
  }

  @Override
  public IExpr substituteValues(IAST function) {
    IExpr substituted = function.replaceAll(x -> {
      RealRelations relations = realRelationsMap.get(x);
      if (relations != null) {
        return relations.getInterval();
      }
      return F.NIL;
    });
    if (substituted.isNumericFunction(true)) {
      return EvalEngine.get().evaluate(substituted);
    }
    return F.NIL;
  }

  @Override
  public boolean isNonNegative(IExpr expr) {
    return isGreaterEqual(expr, F.C0);
  }

  @Override
  public boolean isNonNegativeRational(IExpr expr) {
    return isDomain(expr, S.NonNegativeRationals);
  }

  @Override
  public boolean isNonNegativeReal(IExpr expr) {
    return isDomain(expr, S.NonNegativeReals);
  }

  @Override
  public boolean isPositive(IExpr expr) {
    return isGreaterThan(expr, F.C0);
  }

  @Override
  public boolean isPositiveRational(IExpr expr) {
    return isDomain(expr, S.PositiveRationals);
  }

  @Override
  public boolean isPositiveReal(IExpr expr) {
    return isDomain(expr, S.PositiveReals);
  }

  @Override
  public boolean isPrime(IExpr expr) {
    return isDomain(expr, S.Primes);
  }

  @Override
  public boolean isRational(IExpr expr) {
    return isDomain(expr, S.Rationals);
  }

  @Override
  public boolean isReal(IExpr expr) {
    RealRelations relations = realRelationsMap.get(expr);
    if (relations != null) {// && relation.isLessOrGreaterRelation()) {
      return true;
    }
    if (isDomain(expr, S.Reals)) {
      return true;
    }
    return isDomain(expr, S.Complexes) && isEqual(F.Im(expr), F.C0);
  }

  @Override
  public boolean isUnequal(IExpr expr, INumber number) {
    ComplexRelations relations = complexRelationsMap.get(expr);
    if (relations != null) {
      ArrayList<INumber> unequals = relations.getUnequals();
      if (unequals.contains(number)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IntArrayList reduceRange(IExpr x, final int[] xRange) {
    IExpr temp = elementsMap.get(x);
    if (temp != null) {
      return null;
    }
    temp = distributionsMap.get(x);
    if (temp != null) {
      return null;
    }
    RealRelations rr = realRelationsMap.get(x);
    if (rr != null) {
      IntArrayList iList = new IntArrayList();
      IAST intInterval = rr.getInterval();
      IAST intersection = IntervalDataSym.intersection(intInterval, //
          IntervalDataSym.close(F.ZZ(xRange[0]), F.ZZ(xRange[1])));
      for (int i = 1; i < intersection.size(); i++) {
        IAST list = (IAST) intersection.get(i);
        IExpr min = list.arg1();
        // IBuiltInSymbol left = (IBuiltInSymbol) list.arg2();
        // IBuiltInSymbol right = (IBuiltInSymbol) list.arg3();
        IExpr max = list.arg4();
        int iMin = min.toIntDefault();
        if (F.isNotPresent(iMin)) {
          return null;
        }
        int iMax = max.toIntDefault();
        if (F.isNotPresent(iMax)) {
          return null;
        }
        iList.add(iMin);
        iList.add(iMax);
      }
      if (iList.isEmpty()) {
        return null;
      }
      return iList;
    }
    return null;
  }

  @Override
  public final IAST tensors(IExpr expr) {
    IAST tensor = tensorsMap.get(expr);
    return (tensor == null) ? F.NIL : tensor;
  }
}
