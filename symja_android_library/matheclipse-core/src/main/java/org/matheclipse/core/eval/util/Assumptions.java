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
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
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

    public final ArrayList<INumber> getUnequals() {
      return unequalValues;
    }
  }

  private static class RealRelations {

    EvalEngine engine;
    IAST interval = F.NIL;

    public IAST getInterval() {
      return interval;
    }

    public RealRelations() {
      this.engine = EvalEngine.get();
      this.interval = F.CRealsIntervalData;
    }

    public final void addEquals(IExpr expr, boolean intersection) {
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

  private static boolean addEqual(IAST equalsAST, boolean intersection, Assumptions assumptions) {
    // arg1 == arg2
    if (equalsAST.arg2().isNumericFunction(false)) {
      IExpr num = equalsAST.arg2();
      IExpr key = equalsAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addEquals(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    if (equalsAST.arg1().isNumericFunction(false)) {
      IExpr num = equalsAST.arg1();
      IExpr key = equalsAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addEquals(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addUnequal(IAST equalsAST, Assumptions assumptions) {
    // arg1 != arg2
    if (equalsAST.arg2().isNumber()) {
      INumber num = (INumber) equalsAST.arg2();
      IExpr key = equalsAST.arg1();

      ComplexRelations gla = assumptions.complexRelationsMap.get(key);
      if (gla == null) {
        gla = new ComplexRelations();
      }
      gla.addUnequals(num);
      assumptions.complexRelationsMap.put(key, gla);
      return true;
    }
    if (equalsAST.arg1().isNumber()) {
      INumber num = (INumber) equalsAST.arg1();
      IExpr key = equalsAST.arg2();
      ComplexRelations gla = assumptions.complexRelationsMap.get(key);
      if (gla == null) {
        gla = new ComplexRelations();
      }
      gla.addUnequals(num);
      assumptions.complexRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addGreater(IAST greaterAST, boolean intersection,
      Assumptions assumptions) {
    EvalEngine engine = EvalEngine.get();
    if (greaterAST.isAST3()) {
      // arg1 > arg2 > arg3
      IExpr arg1 = greaterAST.arg1();
      IExpr arg2 = greaterAST.arg2();
      IExpr arg3 = greaterAST.arg3();
      if (arg1.isNumericFunction(false) && arg3.isNumericFunction(false) && !arg2.isNumber()) {
        if (engine.evalGreater(arg1, arg3)) {
          IExpr num1 = arg1;
          IExpr num3 = arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          } else {
            // TODO Warning contradictory assumption(s) `1` encountered.
            // Errors.printMessage(S.$Assumptions, "cas", F.list(arg1, arg2, arg3) );
          }
          gla.addLess(num3, num1, intersection);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 > arg2
    IExpr num = null;
    if (greaterAST.arg2().isNumericFunction(false)) {
      num = greaterAST.arg2();
      // } else {
      // num = greaterAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = greaterAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addGreater(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }

    num = null;
    if (greaterAST.arg1().isNumericFunction(false)) {
      num = greaterAST.arg1();
      // } else {
      // num = greaterAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = greaterAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addLess(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addGreaterEqual(IAST greaterEqualAST, boolean intersection,
      Assumptions assumptions) {
    EvalEngine engine = EvalEngine.get();
    if (greaterEqualAST.isAST3()) {
      // arg1 >= arg2 >= arg3
      IExpr arg1 = greaterEqualAST.arg1();
      IExpr arg2 = greaterEqualAST.arg2();
      IExpr arg3 = greaterEqualAST.arg3();
      if (arg1.isNumericFunction(false) && arg3.isNumericFunction(false) && !arg2.isNumber()) {
        // if (!((IReal) arg1).isLT(((IReal) arg3))) {
        if (engine.evalGreaterEqual(arg1, arg3)) {
          IExpr num1 = arg1;
          IExpr num3 = arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          } else {
            // check for contradictory assumptions
          }
          gla.addLess(num3, num1, intersection);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 >= arg2
    IExpr num = null;
    if (greaterEqualAST.arg2().isNumericFunction(false)) {
      num = greaterEqualAST.arg2();
      // } else {
      // num = greaterEqualAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = greaterEqualAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addGreaterEqual(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }

    num = null;
    if (greaterEqualAST.arg1().isNumericFunction(false)) {
      num = greaterEqualAST.arg1();
    } else {
      num = greaterEqualAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = greaterEqualAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addLessEqual(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addLess(IAST lessAST, boolean intersection, Assumptions assumptions) {
    EvalEngine engine = EvalEngine.get();
    if (lessAST.isAST3()) {
      // arg1 < arg2 < arg3;
      IExpr arg1 = lessAST.arg1();
      IExpr arg2 = lessAST.arg2();
      IExpr arg3 = lessAST.arg3();
      if (arg1.isNumericFunction(false) && arg3.isNumericFunction(false) && !arg2.isNumber()) {
        // if (((IReal) arg1).isLT(((IReal) arg3))) {
        if (engine.evalLess(arg1, arg3)) {
          IExpr num1 = arg1;
          IExpr num3 = arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          } else {
            // check for contradictory assumptions
          }
          gla.addLess(num1, num3, intersection);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 < arg2
    IExpr num = null;
    if (lessAST.arg2().isNumericFunction(false)) {
      num = lessAST.arg2();
      // } else {
      // num = lessAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = lessAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addLess(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    num = null;
    if (lessAST.arg1().isNumericFunction(false)) {
      num = lessAST.arg1();
      // } else {
      // num = lessAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = lessAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addGreater(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addLessEqual(IAST lessEqualAST, boolean intersection,
      Assumptions assumptions) {
    EvalEngine engine = EvalEngine.get();
    if (lessEqualAST.isAST3()) {
      // arg1 <= arg2 <= arg3
      IExpr arg1 = lessEqualAST.arg1();
      IExpr arg2 = lessEqualAST.arg2();
      IExpr arg3 = lessEqualAST.arg3();
      if (arg1.isNumericFunction(false) && arg3.isNumericFunction(false) && !arg2.isNumber()) {
        // if (((IReal) arg1).isLE(((IReal) arg3))) {
        if (engine.evalLessEqual(arg1, arg3)) {
          IExpr num1 = arg1;
          IExpr num3 = arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          } else {
            // check for contradictory assumptions
          }
          gla.addLessEqual(num1, num3, intersection);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 <= arg2;
    IExpr num = null;
    if (lessEqualAST.arg2().isNumericFunction(false)) {
      num = lessEqualAST.arg2();
      // } else {
      // num = lessEqualAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = lessEqualAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addLessEqual(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    num = null;
    if (lessEqualAST.arg1().isNumericFunction(false)) {
      num = lessEqualAST.arg1();
      // } else {
      // num = lessEqualAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = lessEqualAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      } else {
        // check for contradictory assumptions
      }
      gla.addGreaterEqual(num, intersection);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  /**
   * 
   * @param ast
   * @param intersection TODO
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


  @Override
  public IInteger determineInteger(IExpr expr) {
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IInteger intNumber = null;
      IAST intervalData = gla.getInterval();

      for (int i = 1; i < intervalData.size(); i++) {
        IAST intervalList = (IAST) intervalData.get(i);
        if (intervalList.isList4()) {
          IExpr min = intervalList.arg1();
          IBuiltInSymbol lessMin = (IBuiltInSymbol) intervalList.arg2();
          IBuiltInSymbol lessMax = (IBuiltInSymbol) intervalList.arg3();
          IExpr max = intervalList.arg4();

        }
      }

    }
    return null;
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

  @Override
  public IAssumptions copy() {
    Assumptions assumptions = new Assumptions();
    assumptions.distributionsMap = new HashMap<>(distributionsMap);
    assumptions.elementsMap = new HashMap<>(elementsMap);
    assumptions.realRelationsMap = new HashMap<>(realRelationsMap);
    assumptions.complexRelationsMap = new HashMap<>(complexRelationsMap);
    return assumptions;
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
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      return gla.getInterval();
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

  @Override
  public boolean isComplex(IExpr expr) {
    return isDomain(expr, S.Complexes);
  }

  private final boolean isDomain(IExpr expr, ISymbol domain) {
    ISymbol mappedDomain = elementsMap.get(expr);
    return mappedDomain != null && mappedDomain.equals(domain);
  }

  @Override
  public boolean isEqual(IExpr expr, IExpr number) {
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IAST interval = gla.getInterval();
      IAST newInterval = IntervalDataSym.intersection(interval, //
          IntervalDataSym.close(number, number));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isGreaterEqual(IExpr expr, IExpr number) {
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IAST interval = gla.getInterval();
      IAST newInterval = IntervalDataSym.intersection(interval, //
          IntervalDataSym.rOpen(number, F.CInfinity));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isGreaterThan(IExpr expr, IExpr number) {
    // IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IAST interval = gla.getInterval();
      IAST newInterval = IntervalDataSym.intersection(interval, //
          IntervalDataSym.open(number, F.CInfinity));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isInteger(IExpr expr) {
    return isDomain(expr, S.Integers);
  }

  @Override
  public boolean isLessEqual(IExpr expr, IExpr number) {
    // IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IAST interval = gla.getInterval();
      IAST newInterval = IntervalDataSym.intersection(interval, //
          IntervalDataSym.lOpen(F.CNInfinity, number));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isLessThan(IExpr expr, IExpr number) {
    // IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      IAST interval = gla.getInterval();
      IAST newInterval = IntervalDataSym.intersection(interval, //
          IntervalDataSym.open(F.CNInfinity, number));
      if (interval.equals(newInterval)) {
        return true;
      }
    }
    return false;
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
    RealRelations relation = realRelationsMap.get(expr);
    if (relation != null) {// && relation.isLessOrGreaterRelation()) {
      return true;
    }
    if (isDomain(expr, S.Reals)) {
      return true;
    }
    return isDomain(expr, S.Complexes) && isEqual(F.Im(expr), F.C0);
  }

  @Override
  public boolean isUnequal(IExpr expr, INumber number) {
    ComplexRelations gla = complexRelationsMap.get(expr);
    if (gla != null) {
      ArrayList<INumber> unequals = gla.getUnequals();
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
        if (F.isNotPresent(iMin)) {
          return null;
        }
        iList.add(iMin);
        iList.add(iMax);
      }
      if (iList.isEmpty()) {
        return null;
      }
      return iList;
      // int[] newXRange = new int[] {xRange[0], xRange[1]};
      // boolean evaled = false;
      // IReal num = rr.getLess();
      // if (num != null) {
      // int i = num.toIntDefault();
      // if (F.isNotPresent(i)) {
      // i = num.ceilFraction().toIntDefault();
      // }
      // if (F.isPresent(i)) {
      // if (newXRange[1] >= i) {
      // evaled = true;
      // newXRange[1] = i - 1;
      // }
      // }
      // }
      // num = rr.getLessEqual();
      // if (num != null) {
      // int i = num.toIntDefault();
      // if (F.isNotPresent(i)) {
      // i = num.floorFraction().toIntDefault();
      // }
      // if (F.isPresent(i)) {
      // if (newXRange[1] > i) {
      // evaled = true;
      // newXRange[1] = i;
      // }
      // }
      // }
      // num = rr.getGreater();
      // if (num != null) {
      // int i = num.toIntDefault();
      // if (F.isNotPresent(i)) {
      // i = num.floorFraction().toIntDefault();
      // }
      // if (F.isPresent(i)) {
      // if (newXRange[0] <= i) {
      // evaled = true;
      // newXRange[0] = i + 1;
      // }
      // }
      // }
      // num = rr.getGreaterEqual();
      // if (num != null) {
      // int i = num.toIntDefault();
      // if (F.isNotPresent(i)) {
      // i = num.ceilFraction().toIntDefault();
      // }
      // if (F.isPresent(i)) {
      // if (newXRange[0] < i) {
      // evaled = true;
      // newXRange[0] = i;
      // }
      // }
      // }
      // num = rr.getEquals();
      // if (num != null) {
      // int i = num.toIntDefault();
      // if (F.isNotPresent(i)) {
      // i = num.ceilFraction().toIntDefault();
      // }
      // if (F.isPresent(i)) {
      // if (newXRange[0] < i) {
      // evaled = true;
      // newXRange[0] = i;
      // }
      // if (newXRange[1] > i) {
      // evaled = true;
      // newXRange[1] = i;
      // }
      // }
      // }
      // if (evaled) {
      // return newXRange;
      // }
    }
    return null;
  }


  // @Override
  // public void set$Assumptions(IExpr $assumptions) {
  // this.$assumptions = $assumptions;
  // }

  @Override
  public final IAST tensors(IExpr expr) {
    IAST tensor = tensorsMap.get(expr);
    return (tensor == null) ? F.NIL : tensor;
  }
}
