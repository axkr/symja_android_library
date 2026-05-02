## CoefficientArrays

```
CoefficientArrays(list-of-polynomials, list-of-variables)
```

> returns the sparse arrays of coefficients of the `list-of-variables` for the `list-of-polynomials`.
 

See:  
* [Wikipedia - Coefficient](http://en.wikipedia.org/wiki/Coefficient)
* [Wikipedia - Sparse_matrix](https://en.wikipedia.org/wiki/Sparse_matrix)

### Examples

```
>> CoefficientArrays(2*x + 3*y^2 + 4*z + 5, {x, y, z}) // Normal 
{5,{2,0,4},{{0,0,0},{0,3,0},{0,0,0}}}
```
