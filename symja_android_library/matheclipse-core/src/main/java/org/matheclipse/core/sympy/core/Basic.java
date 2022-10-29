package org.matheclipse.core.sympy.core;

import java.util.function.Predicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Basic {
  public static IASTAppendable _atoms(IASTAppendable result, IAST ast, Predicate<IExpr> test) {
    for (int i = 1; i < ast.size(); i++) {
      IExpr a = ast.getRule(i);
      if (a.isAST()) {
        _atoms(result, (IAST) a, test);
      } else {
        if (test.test(a)) {
          result.append(a);
        }
      }
    }
    return result;
  }

  public static IASTAppendable atoms(IAST ast ) {
    return atoms(ast, x -> x.isAtom());
  }

  public static IASTAppendable atoms(IAST ast, Predicate<IExpr> test) {
    // Returns the atoms that form the current object.
    //
    // By default, only objects that are truly atomic and cannot
    // be divided into smaller pieces are returned: symbols, numbers,
    // and number symbols like I and pi. It is possible to request
    // atoms of any type, however, as demonstrated below.
    //
    // Examples
    // ========
    //
    // >>> from sympy import I, pi, sin
    // >>> from sympy.abc import x, y
    // >>> (1 + x + 2*sin(y + I*pi)).atoms()
    // {1, 2, I, pi, x, y}
    //
    // If one or more types are given, the results will contain only
    // those types of atoms.
    //
    // >>> from sympy import Number, NumberSymbol, Symbol
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(Symbol)
    // {x, y}
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(Number)
    // {1, 2}
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(Number, NumberSymbol)
    // {1, 2, pi}
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(Number, NumberSymbol, I)
    // {1, 2, I, pi}
    //
    // Note that I (imaginary unit) and zoo (complex infinity) are special
    // types of number symbols and are not part of the NumberSymbol class.
    //
    // The type can be given implicitly, too:
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(x) # x is a Symbol
    // {x, y}
    //
    // Be careful to check your assumptions when using the implicit option
    // since ``S(1).is_Integer = True`` but ``type(S(1))`` is ``One``, a special type
    // of SymPy atom, while ``type(S(2))`` is type ``Integer`` and will find all
    // integers in an expression:
    //
    // >>> from sympy import S
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(S(1))
    // {1}
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(S(2))
    // {1, 2}
    //
    // Finally, arguments to atoms() can select more than atomic atoms: any
    // SymPy type (loaded in core/__init__.py) can be listed as an argument
    // and those types of "atoms" as found in scanning the arguments of the
    // expression recursively:
    //
    // >>> from sympy import Function, Mul
    // >>> from sympy.core.function import AppliedUndef
    // >>> f = Function('f')
    // >>> (1 + f(x) + 2*sin(y + I*pi)).atoms(Function)
    // {f(x), sin(y + I*pi)}
    // >>> (1 + f(x) + 2*sin(y + I*pi)).atoms(AppliedUndef)
    // {f(x)}
    //
    // >>> (1 + x + 2*sin(y + I*pi)).atoms(Mul)
    // {I*pi, 2*sin(y + I*pi)}

    IASTAppendable result = F.ListAlloc();
    return _atoms(result, ast, test);
  }

}
