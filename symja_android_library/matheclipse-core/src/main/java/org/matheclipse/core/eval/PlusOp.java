package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.qty.IQuantity;

/**
 * Plus operator for adding multiple arguments with the <code>plus(argument)</code> method and
 * returning the result, with the <code>getSum()</code> method, if <code>isEvaled()</code> returns
 * <code>true</code>. See: <a
 * href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">Experiments in Hash-coded Algebraic
 * Simplification</a>
 */
public final class PlusOp {
  /** Merge IExpr keys by adding their values into this map. */
  private Map<IExpr, IExpr> plusMap;

  /** <code>true</code> if plus was really evaluated */
  private boolean evaled;

  /** The value of the addition of numbers. */
  private IExpr numberValue;

  private final int capacity;

  /**
   * Constructor.
   *
   * @param capacity the approximated size of the resulting <code>Plus()</code> AST.
   */
  public PlusOp(final int capacity) {
    this.capacity = capacity;
    this.plusMap = null; // new HashMap<IExpr, IExpr>(size + 5 + size / 10);
    this.evaled = false;
    this.numberValue = F.NIL;
  }

  private Map<IExpr, IExpr> getMap() {
    if (plusMap == null) {
      plusMap = new HashMap<IExpr, IExpr>(capacity + 5 + capacity / 10);
    }
    return plusMap;
  }

  /**
   * Add or merge the <code>key, value</code> pair into the given <code>plusMap</code>.
   *
   * @param key the key expression
   * @param value the value expression
   */
  private boolean addMerge(final IExpr key, final IExpr value) {
    final Map<IExpr, IExpr> map = getMap();
    IExpr temp = map.get(key);
    if (temp == null) {
      map.put(key, value);
      return false;
    }
    // merge both values
    if (temp.isNumber() && value.isNumber()) {
      temp = temp.plus(value);
      if (temp.isZero()) {
        map.remove(key);
        return true;
      }
    } else if (temp.head().equals(S.Plus)) {
      if (!(temp instanceof IASTAppendable)) {
        temp = ((IAST) temp).copyAppendable();
      }
      ((IASTAppendable) temp).append(value);
    } else {
      temp = F.Plus(temp, value);
    }
    map.put(key, temp);
    return true;
  }

  /**
   * Get the current evaluated result of the summation as a <code>Plus()</code> expression with
   * respecting the <code>OneIdentity</code> attribute.
   *
   * @return
   */
  public IExpr getSum() {
    if (plusMap == null) {
      if (numberValue.isPresent() && !numberValue.isZero()) {
        return numberValue;
      }
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(plusMap.size() + 1);
    if (numberValue.isPresent() && !numberValue.isZero()) {
      if (numberValue.isComplexInfinity()) {
        return numberValue;
      }
      result.append(numberValue);
    }
    for (Map.Entry<IExpr, IExpr> element : plusMap.entrySet()) {
      final IExpr temp = element.getKey();
      if (element.getValue().isOne()) {
        if (temp.isPlus()) {
          result.appendArgs((IAST) temp);
        } else {
          result.append(temp);
        }
        continue;
      }
      result.append(F.Times(element.getValue(), temp));
    }
    return result.oneIdentity0();
  }

  /**
   * Test if any evaluation occurred by calling the <code>plus()</code> method
   *
   * @return <code>true</code> if an evaluation occurred.
   */
  public boolean isEvaled() {
    return evaled;
  }

  private IExpr negativeInfinityPlus(final IExpr o1) {
    if (o1.isInfinity()) {
      EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
      return S.Indeterminate;
    } else if (o1.isNegativeInfinity()) {
      return F.CNInfinity;
    }
    return F.CNInfinity;
  }

  /**
   * Add an argument <code>arg</code> to this <code>Plus()</code> expression.
   *
   * @param arg
   * @return <code>F.Indeterminate</code> if the result is indeterminated, <code>F.NIL</code>
   *     otherwise.
   */
  public IExpr plus(final IExpr arg) {
    // if (arg.isPlus()) {
    // // flatten the Plus() argument
    // final IAST plusAST = (IAST) arg;
    // return plusUntilPosition(plusAST, plusAST.size());
    // }
    if (arg.isIndeterminate()) {
      return S.Indeterminate;
    }

    try {
      if (numberValue.isPresent() && numberValue.isDirectedInfinity()) {
        if (numberValue.isComplexInfinity()) {
          if (arg.isDirectedInfinity()) {
            return S.Indeterminate;
          }
          numberValue = F.CComplexInfinity;
          evaled = true;
          return F.NIL;
        } else if (numberValue.isInfinity()) {
          if (arg.isInfinity()) {
            evaled = true;
            return F.NIL;
          }
          if (arg.isDirectedInfinity()) {
            return S.Indeterminate;
          }
          if (arg.isRealResult()) {
            evaled = true;
            return F.NIL;
          }
        } else if (numberValue.isNegativeInfinity()) {
          if (arg.isNegativeInfinity()) {
            evaled = true;
            return F.NIL;
          }
          if (arg.isDirectedInfinity()) {
            // Indeterminate expression `1` encountered.
            IOFunctions.printMessage(
                S.Infinity, "indet", F.List(F.Plus(numberValue, arg)), EvalEngine.get());
            return S.Indeterminate;
          }
          if (arg.isRealResult()) {
            evaled = true;
            return F.NIL;
          }
        }
      }

      if (arg.isNumber()) {
        if (arg.isZero()) {
          evaled = true;
          return F.NIL;
        }
        if (!numberValue.isPresent()) {
          numberValue = arg;
          return F.NIL;
        }
        if (numberValue.isNumber()) {
          numberValue = numberValue.plus(arg);
          evaled = true;
          return F.NIL;
        }
        if (numberValue.isInfinity()) {
          if (arg.isNegativeInfinity()) {
            // Indeterminate expression `1` encountered.
            IOFunctions.printMessage(
                S.Infinity, "indet", F.List(F.Plus(numberValue, arg)), EvalEngine.get());
            return S.Indeterminate;
          }
          numberValue = F.CInfinity;
          evaled = true;
          return F.NIL;
        }
        if (numberValue.isNegativeInfinity()) {
          numberValue = negativeInfinityPlus(arg);
          if (numberValue.isIndeterminate()) {
            return S.Indeterminate;
          }
          evaled = true;
          return F.NIL;
        }
        return F.NIL;
      } else if (arg.isQuantity()) {
        // if (arg.isQuantity()) {
        if (!numberValue.isPresent()) {
          numberValue = arg;
          return F.NIL;
        }
        IQuantity q = (IQuantity) arg;
        IExpr temp = q.plus(numberValue, true);
        if (temp.isPresent()) {
          evaled = true;
          numberValue = temp;
        } else {
          if (addMerge(q, F.C1)) {
            evaled = true;
          }
        }
        return F.NIL;
        // }
      } else if (arg.isAST()) {
        final IAST ast = (IAST) arg;
        final int headID = ((IAST) arg).headID();
        if (headID >= ID.DirectedInfinity) {
          switch (headID) {
            case ID.DirectedInfinity:
              if (arg.isDirectedInfinity()) {
                if (!numberValue.isPresent()) {
                  numberValue = arg;
                  if (arg.isComplexInfinity()) {
                    if (plusMap != null && plusMap.size() > 0) {
                      evaled = true;
                    }
                  } else {
                    if (plusMap != null) {
                      Iterator<Entry<IExpr, IExpr>> iterator = plusMap.entrySet().iterator();
                      while (iterator.hasNext()) {
                        Entry<IExpr, IExpr> entry = iterator.next();
                        if (entry.getKey().isRealResult()) {
                          iterator.remove();
                          evaled = true;
                        }
                      }
                    }
                  }
                  return F.NIL;
                }
                if (arg.isInfinity()) {
                  if (numberValue.isNegativeInfinity()) {
                    // Indeterminate expression `1` encountered.
                    IOFunctions.printMessage(
                        S.Infinity, "indet", F.List(F.Plus(arg, numberValue)), EvalEngine.get());
                    return S.Indeterminate;
                  }
                  numberValue = F.CInfinity;
                  evaled = true;
                  return F.NIL;
                } else if (arg.isNegativeInfinity()) {
                  numberValue = negativeInfinityPlus(numberValue);
                  if (numberValue.isIndeterminate()) {
                    return S.Indeterminate;
                  }
                  evaled = true;
                  return F.NIL;
                } else if (arg.isComplexInfinity()) {
                  if (numberValue.isDirectedInfinity()) {
                    // Indeterminate expression `1` encountered.
                    IOFunctions.printMessage(
                        S.Infinity, "indet", F.List(F.Plus(arg, numberValue)), EvalEngine.get());
                    return S.Indeterminate;
                  }
                  numberValue = F.CComplexInfinity;
                  evaled = true;
                  return F.NIL;
                }
              }
              break;
            case ID.Times:
              if (ast.size() > 1) {
                if (ast.arg1().isNumber()) {
                  if (addMerge(ast.rest().oneIdentity1(), ast.arg1())) {
                    evaled = true;
                  }
                  return F.NIL;
                }
                if (addMerge(ast, F.C1)) {
                  evaled = true;
                }
              }
              return F.NIL;
            case ID.Interval:
              if (arg.isInterval()) {
                if (!numberValue.isPresent()) {
                  numberValue = arg;
                  return F.NIL;
                }
                IExpr temp;
                if (numberValue.isInterval()) {
                  temp = IntervalSym.plus((IAST) numberValue, (IAST) arg);
                } else {
                  temp = IntervalSym.plus(numberValue, (IAST) arg);
                }
                if (temp.isPresent()) {
                  numberValue = temp;
                  evaled = true;
                } else {
                  if (addMerge(arg, F.C1)) {
                    evaled = true;
                  }
                }
                return F.NIL;
              }
              break;
              // case ID.Quantity:
              // if (arg.isQuantity()) {
              // if (!numberValue.isPresent()) {
              // numberValue = arg;
              // return F.NIL;
              // }
              // IQuantity q = (IQuantity) arg;
              // numberValue = q.plus(numberValue);
              // if (numberValue.isPresent()) {
              // evaled = true;
              // }
              // return F.NIL;
              // }
              // break;
            case ID.SeriesData:
              if (arg instanceof ASTSeriesData) {
                if (!numberValue.isPresent()) {
                  numberValue = arg;
                  return F.NIL;
                }
                numberValue = ((ASTSeriesData) arg).plus(numberValue);
                evaled = true;
                return F.NIL;
              }
              break;
          }
        }
        // } else if (!numberValue.isPresent() && arg.isRealResult()) {
        // numberValue = arg;
        // return F.NIL;
      }
      if (addMerge(arg, F.C1)) {
        evaled = true;
      }
    } catch (ValidateException ve) {
      if (Config.SHOW_STACKTRACE) {
        ve.printStackTrace();
      }
      throw ve;
    } catch (LimitException le) {
      if (Config.SHOW_STACKTRACE) {
        le.printStackTrace();
      }
      throw le;
    } catch (SymjaMathException sme) {
      if (Config.SHOW_STACKTRACE) {
        sme.printStackTrace();
      }
    }
    return F.NIL;
  }

  // private static IExpr plusInterval(final IExpr o0, final IExpr o1) {
  // return F.Interval(F.List(o0.lower().plus(o1.lower()), o0.upper().plus(o1.upper())));
  // }
}
