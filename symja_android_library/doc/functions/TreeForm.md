## TreeForm
 
```
TreeForm(expr)
```

> create a tree visualization from the given expression `expr`.

```
TreeForm(expr, n)
```

> create a tree visualization from the given expression `expr` from level `0` to `n`.

See:  
* [Wikipedia - Binary expression tree](https://en.wikipedia.org/wiki/Binary_expression_tree) 

### Examples 

Generate a [Graphics](Graphics.md) output to visualize the structure of the input expression:

```
>> TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z)) 
```

### Related terms 
[Manipulate](Manipulate.md) [Graph](Graph.md)  
