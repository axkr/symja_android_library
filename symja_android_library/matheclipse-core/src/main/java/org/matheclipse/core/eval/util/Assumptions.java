package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

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

    static final int GREATER_ID = 0;
    static final int GREATEREQUAL_ID = 1;
    static final int LESS_ID = 2;
    static final int LESSEQUAL_ID = 3;
    static final int EQUALS_ID = 4;

    private final IReal[] values;

    public RealRelations() {
      this.values = new IReal[5];
    }

    public final void addEquals(IReal expr) {
      values[EQUALS_ID] = expr;
    }

    public final void addGreater(IReal expr) {
      values[GREATER_ID] = expr;
    }

    public final void addGreaterEqual(IReal expr) {
      values[GREATEREQUAL_ID] = expr;
    }

    public final void addLess(IReal expr) {
      values[LESS_ID] = expr;
    }

    public final void addLessEqual(IReal expr) {
      values[LESSEQUAL_ID] = expr;
    }

    public final IReal getEquals() {
      return values[EQUALS_ID];
    }

    /**
     * The key has to be greater than the returned value from the values map, if <code>value!=null
     * </code>
     *
     * @return
     */
    public final IReal getGreater() {
      return values[GREATER_ID];
    }

    /**
     * The key has to be greater equal the returned value from the values map, if <code>value!=null
     * </code>
     *
     * @return
     */
    public final IReal getGreaterEqual() {
      return values[GREATEREQUAL_ID];
    }

    /**
     * The key has to be less than the returned value from the values map, if <code>value!=null
     * </code>
     *
     * @return
     */
    public final IReal getLess() {
      return values[LESS_ID];
    }

    /**
     * The key has to be less equal the returned value from the values map, if <code>value!=null
     * </code>
     *
     * @return
     */
    public final IReal getLessEqual() {
      return values[LESSEQUAL_ID];
    }

    public final boolean isLessOrGreaterRelation() {
      for (int i = 0; i <= LESSEQUAL_ID; i++) {
        if (values != null) {
          return true;
        }
      }
      return false;
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
          Errors.printMessage(S.Arrays, "rankl", F.list(arrays.arg1(), F.C2),
              EvalEngine.get());
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
          Errors.printMessage(S.Matrices, "rankl", F.list(matrices.arg1(), F.C2),
              EvalEngine.get());
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
          Errors.printMessage(S.Vectors, "rankl", F.list(vectors.arg1(), F.C2),
              EvalEngine.get());
        }
      }
    }
    return false;
  }

  private static boolean addEqual(IAST equalsAST, Assumptions assumptions) {
    // arg1 == arg2
    if (equalsAST.arg2().isReal()) {
      IReal num = (IReal) equalsAST.arg2();
      IExpr key = equalsAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addEquals(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    if (equalsAST.arg1().isReal()) {
      IReal num = (IReal) equalsAST.arg1();
      IExpr key = equalsAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addEquals(num);
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

  private static boolean addGreater(IAST greaterAST, Assumptions assumptions) {
    if (greaterAST.isAST3()) {
      // arg1 > arg2 > arg3
      IExpr arg1 = greaterAST.arg1();
      IExpr arg2 = greaterAST.arg2();
      IExpr arg3 = greaterAST.arg3();
      if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
        if (((IReal) arg1).isGT(((IReal) arg3))) {
          IReal num1 = (IReal) arg1;
          IReal num3 = (IReal) arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          }
          gla.addLess(num1);
          gla.addGreater(num3);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 > arg2
    IReal num = null;
    if (greaterAST.arg2().isReal()) {
      num = (IReal) greaterAST.arg2();
    } else {
      num = greaterAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = greaterAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addGreater(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }

    num = null;
    if (greaterAST.arg1().isReal()) {
      num = (IReal) greaterAST.arg1();
    } else {
      num = greaterAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = greaterAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addLess(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addGreaterEqual(IAST greaterEqualAST, Assumptions assumptions) {
    if (greaterEqualAST.isAST3()) {
      // arg1 >= arg2 >= arg3
      IExpr arg1 = greaterEqualAST.arg1();
      IExpr arg2 = greaterEqualAST.arg2();
      IExpr arg3 = greaterEqualAST.arg3();
      if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
        if (!((IReal) arg1).isLT(((IReal) arg3))) {
          IReal num1 = (IReal) arg1;
          IReal num3 = (IReal) arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          }
          gla.addLessEqual(num1);
          gla.addGreaterEqual(num3);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 >= arg2
    IReal num = null;
    if (greaterEqualAST.arg2().isReal()) {
      num = (IReal) greaterEqualAST.arg2();
    } else {
      num = greaterEqualAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = greaterEqualAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addGreaterEqual(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }

    num = null;
    if (greaterEqualAST.arg1().isReal()) {
      num = (IReal) greaterEqualAST.arg1();
    } else {
      num = greaterEqualAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = greaterEqualAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addLessEqual(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addLess(IAST lessAST, Assumptions assumptions) {
    if (lessAST.isAST3()) {
      // arg1 < arg2 < arg3;
      IExpr arg1 = lessAST.arg1();
      IExpr arg2 = lessAST.arg2();
      IExpr arg3 = lessAST.arg3();
      if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
        if (((IReal) arg1).isLT(((IReal) arg3))) {
          IReal num1 = (IReal) arg1;
          IReal num3 = (IReal) arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          }
          gla.addGreater(num1);
          gla.addLess(num3);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 < arg2
    IReal num = null;
    if (lessAST.arg2().isReal()) {
      num = (IReal) lessAST.arg2();
    } else {
      num = lessAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = lessAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addLess(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    num = null;
    if (lessAST.arg1().isReal()) {
      num = (IReal) lessAST.arg1();
    } else {
      num = lessAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = lessAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addGreater(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  private static boolean addLessEqual(IAST lessEqualAST, Assumptions assumptions) {

    if (lessEqualAST.isAST3()) {
      // arg1 <= arg2 <= arg3
      IExpr arg1 = lessEqualAST.arg1();
      IExpr arg2 = lessEqualAST.arg2();
      IExpr arg3 = lessEqualAST.arg3();
      if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
        if (((IReal) arg1).isLE(((IReal) arg3))) {
          IReal num1 = (IReal) arg1;
          IReal num3 = (IReal) arg3;
          IExpr key = arg2;
          RealRelations gla = assumptions.realRelationsMap.get(key);
          if (gla == null) {
            gla = new RealRelations();
          }
          gla.addGreaterEqual(num1);
          gla.addLessEqual(num3);
          assumptions.realRelationsMap.put(key, gla);
          return true;
        }
      }
      return false;
    }

    // arg1 <= arg2;
    IReal num = null;
    if (lessEqualAST.arg2().isReal()) {
      num = (IReal) lessEqualAST.arg2();
    } else {
      num = lessEqualAST.arg2().evalReal();
    }
    if (num != null) {
      IExpr key = lessEqualAST.arg1();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addLessEqual(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    num = null;
    if (lessEqualAST.arg1().isReal()) {
      num = (IReal) lessEqualAST.arg1();
    } else {
      num = lessEqualAST.arg1().evalReal();
    }
    if (num != null) {
      IExpr key = lessEqualAST.arg2();
      RealRelations gla = assumptions.realRelationsMap.get(key);
      if (gla == null) {
        gla = new RealRelations();
      }
      gla.addGreaterEqual(num);
      assumptions.realRelationsMap.put(key, gla);
      return true;
    }
    return false;
  }

  /**
   * 
   * @param ast
   * @param assumptions
   * @return <code>null</code> if assumptions couldn't be assigned
   */
  private static IAssumptions addList(IAST ast, Assumptions assumptions) {
    for (int i = 1; i < ast.size(); i++) {
      if (ast.get(i).isAST()) {
        IAST temp = (IAST) ast.get(i);
        if (temp.isAST(S.Element, 3)) {
          if (!addElement(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Greater, 3, 4)) {
          if (!addGreater(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.GreaterEqual, 3, 4)) {
          if (!addGreaterEqual(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Less, 3, 4)) {
          if (!addLess(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.LessEqual, 3, 4)) {
          if (!addLessEqual(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Equal, 3)) {
          if (!addEqual(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAST(S.Unequal, 3)) {
          if (!addUnequal(temp, assumptions)) {
            return null;
          }
        } else if (temp.isAnd()) {
          IAssumptions assum = addList(temp, assumptions);
          if (assum == null) {
            return null;
          }
        }
      }
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

  /**
   * Create a new <code>IAssumptions</code> from the given expression. If the creation is not
   * possible return <code>null</code>
   *
   * @param expr
   * @return <code>null</code> if <code>Assumptions</code> could not be created from the given
   *         expression.
   */
  public static IAssumptions getInstance(IExpr expr) {
    if (expr.isAST()) {
      Assumptions assumptions = new Assumptions();
      // assumptions.$assumptions = expr;
      if (expr.isAnd() || expr.isSameHeadSizeGE(S.List, 2)) {
        Assumptions.addList((IAST) expr, assumptions);
      } else if (expr.isAST()) {
        assumptions.addAssumption(expr);
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
        if (addGreater(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.GreaterEqual, 3, 4)) {
        if (addGreaterEqual(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.Less, 3, 4)) {
        if (addLess(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.LessEqual, 3, 4)) {
        if (addLessEqual(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.Equal, 3)) {
        if (addEqual(ast, this)) {
          return this;
        }
      } else if (ast.isAST(S.Unequal, 3)) {
        if (addUnequal(ast, this)) {
          return this;
        }
      } else if (ast.isAnd() || ast.isSameHeadSizeGE(S.List, 2)) {
        return addList(ast, this);
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
    // if ($assumptions.isPresent()) {
    // assumptions.$assumptions = $assumptions.copy();
    // } else {
    // assumptions.$assumptions = F.NIL;;
    // }
    return assumptions;
  }

  @Override
  public final IAST distribution(IExpr expr) {
    IAST dist = distributionsMap.get(expr);
    return (dist == null) ? F.NIL : dist;
  }

  // @Override
  // public IExpr get$Assumptions() {
  // return $assumptions;
  // }

  @Override
  public Map<IExpr, IAST> getTensorsMap() {
    return tensorsMap;
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

  private final boolean isDomain(IExpr expr, ISymbol domain) {
    ISymbol mappedDomain = elementsMap.get(expr);
    return mappedDomain != null && mappedDomain.equals(domain);
  }

  @Override
  public boolean isEqual(IExpr expr, IReal number) {
    IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      num = gla.getEquals();
      if (num != null) {
        if (num.equals(number)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isGreaterEqual(IExpr expr, IReal number) {
    IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      boolean result = false;
      num = gla.getGreater();
      if (num != null) {
        if (num.equals(number)) {
          result = true;
        }
      }
      if (!result) {
        num = gla.getGreaterEqual();
        if (num != null) {
          if (num.equals(number)) {
            result = true;
          }
        }
      }
      if (result) {
        return true;
      }
      return isGreaterThan(expr, number);
    }
    return false;
  }

  @Override
  public boolean isGreaterThan(IExpr expr, IReal number) {
    IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      boolean result = false;
      num = gla.getGreater();
      if (num != null) {
        if (!num.equals(number)) {
          if (num.isLE(number)) {
            return false;
          }
        }
        result = true;
      }
      if (!result) {
        num = gla.getGreaterEqual();
        if (num != null) {
          if (num.isLE(number)) {
            return false;
          }
          result = true;
        }
      }
      return result;
    }
    return false;
  }

  @Override
  public boolean isInteger(IExpr expr) {
    return isDomain(expr, S.Integers);
  }

  @Override
  public boolean isLessEqual(IExpr expr, IReal number) {
    IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      boolean result = false;
      num = gla.getLess();
      if (num != null) {
        if (num.equals(number)) {
          result = true;
        }
      }
      if (!result) {
        num = gla.getLessEqual();
        if (num != null) {
          if (num.equals(number)) {
            result = true;
          }
        }
      }
      if (result) {
        return true;
      }
      return isLessThan(expr, number);
    }
    return false;
  }

  @Override
  public boolean isLessThan(IExpr expr, IReal number) {
    IReal num;
    RealRelations gla = realRelationsMap.get(expr);
    if (gla != null) {
      boolean result = false;
      num = gla.getLess();
      if (num != null) {
        if (!num.equals(number)) {
          if (num.isGE(number)) {
            return false;
          }
        }
        result = true;
      }
      if (!result) {
        num = gla.getLessEqual();
        if (num != null) {
          if (num.isGE(number)) {
            return false;
          }
          result = true;
        }
      }
      return result;
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
    if (relation != null && relation.isLessOrGreaterRelation()) {
      return true;
    }
    return isDomain(expr, S.Reals);
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
  public int[] reduceRange(IExpr x, final int[] xRange) {
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
      int[] newXRange = new int[] {xRange[0], xRange[1]};
      boolean evaled = false;
      IReal num = rr.getLess();
      if (num != null) {
        int i = num.toIntDefault();
        if (i == Integer.MIN_VALUE) {
          i = num.ceilFraction().toIntDefault();
        }
        if (i != Integer.MIN_VALUE) {
          if (newXRange[1] >= i) {
            evaled = true;
            newXRange[1] = i - 1;
          }
        }
      }
      num = rr.getLessEqual();
      if (num != null) {
        int i = num.toIntDefault();
        if (i == Integer.MIN_VALUE) {
          i = num.floorFraction().toIntDefault();
        }
        if (i != Integer.MIN_VALUE) {
          if (newXRange[1] > i) {
            evaled = true;
            newXRange[1] = i;
          }
        }
      }
      num = rr.getGreater();
      if (num != null) {
        int i = num.toIntDefault();
        if (i == Integer.MIN_VALUE) {
          i = num.floorFraction().toIntDefault();
        }
        if (i != Integer.MIN_VALUE) {
          if (newXRange[0] <= i) {
            evaled = true;
            newXRange[0] = i + 1;
          }
        }
      }
      num = rr.getGreaterEqual();
      if (num != null) {
        int i = num.toIntDefault();
        if (i == Integer.MIN_VALUE) {
          i = num.ceilFraction().toIntDefault();
        }
        if (i != Integer.MIN_VALUE) {
          if (newXRange[0] < i) {
            evaled = true;
            newXRange[0] = i;
          }
        }
      }
      num = rr.getEquals();
      if (num != null) {
        int i = num.toIntDefault();
        if (i == Integer.MIN_VALUE) {
          i = num.ceilFraction().toIntDefault();
        }
        if (i != Integer.MIN_VALUE) {
          if (newXRange[0] < i) {
            evaled = true;
            newXRange[0] = i;
          }
          if (newXRange[1] > i) {
            evaled = true;
            newXRange[1] = i;
          }
        }
      }
      if (evaled) {
        return newXRange;
      }
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
