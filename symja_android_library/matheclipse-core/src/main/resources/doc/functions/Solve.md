## Solve 

```
Solve(equations, vars)
```

> attempts to solve `equations` for the variables `vars`.

```
Solve(equations, vars, domain)
```

> attempts to solve `equations` for the variables `vars` in the given `domain`.

### Options

- `GenerateConditions` - if `True` (default value) the solutions for multi-valued inverse functions are generated with the `ConditionalExpression` function; if `False` some solutions for multi-valued inverse functions get lost. 
- `MaxRoots` the maximum number of roots, which should be returned 

### Examples

It's important to use the `==` operator to define the equations. If you have unintentionally assigned a value to the variables `x, y` with the `=` operator you have to call `Clear(x,y)` to clear the definitions for these variables.

```
>> Solve({x^2==4,x+y^2==6}, {x,y})
{{x->2,y->2},{x->2,y->-2},{x->-2,y->2*2^(1/2)},{x->-2,y->(-2)*2^(1/2)}}

>> Solve({2*x + 3*y == 4, 3*x - 4*y <= 5,x - 2*y > -21}, {x,  y}, Integers)
{{x->-7,y->6},{x->-4,y->4},{x->-1,y->2}}

>> Solve(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)
{{a->False,b->True,c->False,d->False},{a->True,b->False,c->False,d->False}}

>> Solve(30*p+7*q==1,{p,q}, Integers, MaxRoots->10)
{{p->-31,q->133},{p->-24,q->103},{p->-17,q->73},{p->-10,q->43},{p->-3,q->13},{p->4,q->-17},{p->11,q->-47},{p->18,q->-77},{p->25,q->-107},{p->32,q->-137}}

>> Solve(a+b+c==100,{a,b,c},Primes, MaxRoots->100)
{{a->2,b->19,c->79},{a->2,b->31,c->67},{a->2,b->37,c->61},{a->2,b->61,c->37},{a->2,b->67,c->31},{a->2,b->79,c->19},{a->19,b->2,c->79},{a->19,b->79,c->2},{a->31,b->2,c->67},{a->31,b->67,c->2},{a->37,b->2,c->61},{a->37,b->61,c->2},{a->61,b->2,c->37},{a->61,b->37,c->2},{a->67,b->2,c->31},{a->67,b->31,c->2},{a->79,b->2,c->19},{a->79,b->19,c->2}}
```

The solutions for multi-valued inverse functions are generated with the `ConditionalExpression` function.

```
>> Solve(Sin(x^2)==0,x)
{{x->ConditionalExpression(Sqrt(2*Pi)*Sqrt(C(1)),C(1)∈Integers)},{x>ConditionalExpression(-Sqrt(2*Pi)*Sqrt(C(1)),C(1)∈Integers)},{x->ConditionalExpression(-Sqrt(Pi+2*Pi*C(1)),C(1)∈Integers)},{x->ConditionalExpression(Sqrt(Pi+2*Pi*C(1)),C(1)∈Integers)}}

>> Solve(Sin(x^2)==0,x,GenerateConditions->False) 
{{x->0}}    
```

### Related terms 
[DSolve](DSolve.md), [Eliminate](Eliminate.md), [GroebnerBasis](GroebnerBasis.md), [FindInstance](FindInstance.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md), [NSolve](NSolve.md), [Reduce](Reduce.md), [Roots](Roots.md) 


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Solve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Solve.java#L103) 
