## DifferenceRoot

```
DifferenceRoot(equation)
```

> operator for generating a holonomic sequence defined by a linear difference `equation`.
 

See:  
* [Wikipedia - Holonomic_function](https://en.wikipedia.org/wiki/Holonomic_function)

### Examples

The sequence of Catalan numbers:

```  
>> dr=DifferenceRoot(Function({y,n},{(-4*n-2)*y(n)+(n+2)*y(n+1)==0,y(0)==1})); 

>> dr(10)
16796

>> CatalanNumber(10)
16796 
```
