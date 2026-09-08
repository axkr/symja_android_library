## Reduce

```
Reduce(logic-expression, var)
```

> returns a simplified form of the `logic-expression` which describes all values of `var` that fulfill it.

```
Reduce(logic-expression, {var1, var2, ...})
```

> reduces the `logic-expression` for the given list of variables.

```
Reduce(logic-expression, vars, domain)
```

> reduces the `logic-expression` over `domain`, which is one of `Complexes` (the default),
> `Reals`, `Integers`, `Primes`, `Rationals` or `Booleans`.

An ordering relation (`<`, `<=`, `>`, `>=`) is only defined over the real numbers, so an
inequality is always reduced over the reals - independent of the requested domain.

`Reduce` describes the *complete* solution set. A statement which none of its methods can
decide is returned unevaluated instead of being answered incompletely.

### Examples

Equations are solved for all of their roots:

```
>> Reduce(x^6-1==0 && x>0, x)
x==1

>> Reduce(x^2==4, x)
x==-2||x==2

>> Reduce(Abs(x)==1, x, Reals)
x==-1||x==1
```

A parametric equation generates the case analysis of its leading coefficients:

```
>> Reduce(a*x==b, x)
(a!=0&&x==b/a)||(a==0&&b==0)
```

Inequalities are reduced by the sign analysis of the real roots and poles:

```
>> Reduce(4*x^3-4*x>0, x)
(x>-1&&x<0)||x>1

>> Reduce(1/x<1, x, Reals)
x<0||x>1

>> Reduce(Abs(x-2)+Abs(x-3)==1, x, Reals)
x>=2&&x<=3
```

A periodic equation is solved with integer parameters `C(1)`, `C(2)`, ...:

```
>> Reduce(Sin(x)==1/2, x)
C(1)∈Integers&&(x==Pi/6+2*Pi*C(1)||x==5/6*Pi+2*Pi*C(1))
```

Over the reals only its real members are solutions:

```
>> Reduce(Sin(x)==2, x, Reals)
False

>> Reduce(Sinh(x)==1, x, Reals)
x==ArcSinh(1)
```

Bounding the variable turns a periodic equation into finitely many solutions and a periodic
inequality into finitely many intervals:

```
>> Reduce(Sin(x)==1/2 && 0<x<2*Pi, x)
x==Pi/6||x==5/6*Pi

>> Reduce(Sin(x)>1/2 && 0<x<2*Pi, x)
x>Pi/6&&x<5/6*Pi
```

Over the integers a bounded solution set is enumerated and an unbounded one is described by a ray:

```
>> Reduce(x>0 && x<4, x, Integers)
x==1||x==2||x==3

>> Reduce(x^2>1, x, Integers)
x<=-2||x>=2

>> Reduce(2*x+3*y==1, {x, y}, Integers)
C(1)∈Integers&&x==-1+3*C(1)&&y==1-2*C(1)
```

Systems of equations are returned in disjunctive normal form:

```
>> Reduce({x^2==4, y==2}, {x, y})
(x==-2&&y==2)||(x==2&&y==2)
```

### Options

* `Backsubstitution` - substitute the values of the eliminated variables back into each other
* `Cubics`, `Quartics` - return the radicals of a general cubic/quartic instead of `Root` objects
* `GeneratedParameters` - the head of the generated parameters, `C` by default
* `Modulus` - reduce in the residue class ring of the given modulus
* `WorkingPrecision` - the precision the exact result is converted to

```
>> Reduce(x^2==2, x, Modulus->7)
x==3||x==4
```

### Related terms
[Solve](Solve.md), [NSolve](NSolve.md), [Roots](Roots.md), [NRoots](NRoots.md), [Resolve](Resolve.md), [Eliminate](Eliminate.md), [FindInstance](FindInstance.md)






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of Reduce](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Reduce.java#L24) 
