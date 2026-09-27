## Eliminate 

```
Eliminate(list-of-equations, list-of-variables)
```

> attempts to eliminate the variables from the `list-of-variables` in the `list-of-equations`.

See:

* [Wikipedia - System of linear equations - Elimination of variables](http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables)
 
### Examples

```
>> Eliminate({x==2+y, y==z}, y)
x-z==2

>> Eliminate(2*x+3*y+4*z==1 && 9*x+8*y+7*z==2, z)
11/2*x+11/4*y==1/4

>> Eliminate({f==x^5+y^5, a==x+y, b==x*y}, {x,y})
-a^5+5*a^3*b-5*a*b^2+f==0
```

### Related terms
[DSolve](DSolve.md), [GroebnerBasis](GroebnerBasis.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md), [Roots](Roots.md),  [Solve](Solve.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Eliminate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Eliminate.java#L69) 

* [Rule definitions of Eliminate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rule_sets/EliminateRules.m) 
