## Eliminate 

```
Eliminate(list-of-equations, list-of-variables)
```

> attempts to eliminate the variables from the `list-of-variables` in the `list-of-equations`.

See:  
* [Wikipedia - System of linear equations - Elimination of variables](http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables)
 
### Examples 
```
>>> Eliminate({x==2+y, y==z}, y)
x==2+z
```

### Related terms
[DSolve], [GroebnerBasis], [FindRoot], [NRoots], [Roots], [Solve]