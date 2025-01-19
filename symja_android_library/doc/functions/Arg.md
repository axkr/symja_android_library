## Arg

```
Arg(expr)
```

> returns the argument of the complex number `expr`.

See:
* [Wikipedia - Argument (complex analysis)](https://en.wikipedia.org/wiki/Argument_%28complex_analysis%29)

### Examples

```
>> Arg(1+I)   
Pi/4
```

`Arg` evaluates the direction of `DirectedInfinity` quantities by the `Arg` of its arguments:

```
>> Arg(DirectedInfinity(1+I))  
Pi/4
```