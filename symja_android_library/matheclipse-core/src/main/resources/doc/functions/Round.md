## Round

```
Round(expr)
```

> round a given `expr` to nearest integer.


```
Round(expr, factor)
```

> round a given `expr` up to the next nearest multiple of a `factor`.

See
* [Wikipedia - Floor and ceiling functions](https://en.wikipedia.org/wiki/Floor_and_ceiling_functions)

### Examples

```
>> Round(-3.6)
-4

>> Round(1.234512, 0.01) 
1.23 

>> Round(1.235512, 0.01) 
1.24
```

Round to nearest multiple of 5

```
>> Round({12.5, 62.1, 68.3, 74.5, 80.7}, 5) 
{10,60,70,75,80}
```

### Related terms 
[IntegerPart](IntegerPart.md), [Ceiling](Ceiling.md), [Floor](Floor.md), [FractionalPart](FractionalPart.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Round](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L1618) 
