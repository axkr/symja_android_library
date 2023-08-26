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

Generate HTML/JavaScript output to visualize the structure of the input expression:

```
>> TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z)) 
```

### Related terms 
[Manipulate](Manipulate.md) [Graph](Graph.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TreeForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L795) 
