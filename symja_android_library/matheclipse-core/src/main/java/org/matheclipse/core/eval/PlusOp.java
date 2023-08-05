package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.qty.IQuantity;

/**
 * Plus operator for adding multiple arguments with the <code>plus(argument)</code> method and
 * returning the result, with the <code>getSum()</code> method, if <code>isEvaled()</code> returns
 * <code>true</code>. See:
 * <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">Experiments in Hash-coded
 * Algebraic Simplification</a>
 */
public final class PlusOp {
  // private static final Logger LOGGER = LogManager.getLogger();

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
      final IExpr key = element.getKey();
      final IExpr value = element.getValue();
      if (value.isOne()) {
        if (key.isPlus()) {
          result.appendArgs((IAST) key);
        } else {
          if (key.isTimes()) {
            EvalAttributes.sortWithFlags((IASTMutable) key);
          }
          result.append(key);
        }
      } else if (key.isTimes()) {
        IASTAppendable times = F.TimesAlloc(((IAST) key).size());
        times.append(value);
        times.appendArgs((IAST) key);
        EvalAttributes.sortWithFlags(times);
        result.append(times);
      } else {
        IASTMutable times = F.Times(value, key);
        EvalAttributes.sortWithFlags(times);
        result.append(times);
      }

    }
    IExpr temp = result.oneIdentity0();
    if (temp.isPlus()) {
      EvalAttributes.sortWithFlags((IASTMutable) temp);
    }
    return temp;
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
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Infinity, "indet", F.List(F.Plus(F.CInfinity, F.CNInfinity)));
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
   *         otherwise.
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
            Errors.printMessage(S.Infinity, "indet", F.list(F.Plus(numberValue, arg)),
                EvalEngine.get());
            return S.Indeterminate;
          }
          if (arg.isRealResult()) {
            evaled = true;
            return F.NIL;
          }
        }
      }

      if (numberValue.isAST(S.Overflow, 1) && arg.isNumericFunction()) {
        evaled = true;
        return F.NIL;
      }

      if (arg.isNumber()) {
        if (arg.isZero()) {
          evaled = true;
          return F.NIL;
        }
        if (numberValue.isNIL()) {
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
            Errors.printMessage(S.Infinity, "indet", F.list(F.Plus(numberValue, arg)),
                EvalEngine.get());
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
        if (numberValue.isNIL()) {
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
                if (numberValue.isNIL()) {
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
                    Errors.printMessage(S.Infinity, "indet", F.list(F.Plus(arg, numberValue)),
                        EvalEngine.get());
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
                    Errors.printMessage(S.Infinity, "indet", F.list(F.Plus(arg, numberValue)),
                        EvalEngine.get());
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
                if (numberValue.isNIL()) {
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
            case ID.IntervalData:
              if (arg.isIntervalData()) {
                if (numberValue.isNIL()) {
                  numberValue = arg;
                  return F.NIL;
                }
                IExpr temp = F.NIL;
                if (numberValue.isIntervalData()) {
                  temp = IntervalDataSym.plus((IAST) numberValue, (IAST) arg);
                } else {
                  if (!numberValue.isInterval()) {
                    temp = IntervalDataSym.plus(numberValue, (IAST) arg);
                  }
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
            // if (numberValue.isNIL()) {
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
                if (numberValue.isNIL()) {
                  numberValue = arg;
                  return F.NIL;
                }
                numberValue = ((ASTSeriesData) arg).plus(numberValue);
                evaled = true;
                return F.NIL;
              }
              break;
            case ID.Overflow:
              if (arg.isAST0()) {
                if (numberValue.isNIL()) {
                  numberValue = arg;
                  return F.NIL;
                } else if (numberValue.isAST(S.Underflow, 1)) {
                  numberValue = arg;
                  evaled = true;
                  return F.NIL;
                } else if (numberValue.isNumericFunction()) {
                  numberValue = arg;
                  evaled = true;
                  return F.NIL;
                }
              }
              break;
            case ID.Underflow:
              if (arg.isAST0()) {
                if (numberValue.isNIL()) {
                  if (EvalEngine.get().isNumericMode()) {
                    // same as 0.0
                    numberValue = F.CD0;
                    evaled = true;
                    return F.NIL;
                  }
                  numberValue = arg;
                  return F.NIL;
                } else if (numberValue.isAST(S.Overflow, 1)) {
                  evaled = true;
                  return F.NIL;
                } else if (EvalEngine.get().isNumericMode()) {
                  // same as 0.0
                  evaled = true;
                  return F.NIL;
                }
              }
              break;
          }
        }
        // } else if (numberValue.isNIL() && arg.isRealResult()) {
        // numberValue = arg;
        // return F.NIL;
      }
      if (addMerge(arg, F.C1)) {
        evaled = true;
      }
    } catch (ValidateException | LimitException e) {
      throw e;
    } catch (SymjaMathException sme) {
      // `1`.
      Errors.printMessage(S.Plus, sme, EvalEngine.get());
    }
    return F.NIL;
  }

  // private static IExpr plusInterval(final IExpr o0, final IExpr o1) {
  // return F.Interval(F.list(o0.lower().plus(o1.lower()), o0.upper().plus(o1.upper())));
  // }
}
