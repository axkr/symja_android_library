## Solve 

```
Solve(equations, vars)
```

> attempts to solve `equations` for the variables `vars`.

### Examples 
```
>>> Solve({x^2==4,x+y^2==6}, {x,y})
{{x->2,y->2},{x->2,y->-2},{x->-2,y->2*2^(1/2)},{x->-2,y->(-2)*2^(1/2)}}
```

### Related terms 
[DSolve], [Symbols/Eliminate|Eliminate], [Symbols/GroebnerBasis|GroebnerBasis], [Symbols/FindRoot|FindRoot], [Symbols/NRoots|NRoots], [Symbols/Roots|Roots]