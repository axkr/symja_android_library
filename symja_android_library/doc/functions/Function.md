## Function

```
Function(body)
```

or

```
body &
```

> represents a pure function with parameters `#1`, `#2`....

```
Function({x1, x2, ...}, body)
```

or

```
(body &)[x1, x2, ...]
```

> represents a pure function with parameters `x1`, `x2`....


```
Function({x1, x2, ...}, body, attr)
```

> assume that the function has the attributes `attr`.

See:  
* [Wikipedia - Pure function](https://en.wikipedia.org/wiki/Pure_function)
* [Wikipedia - Lambda calculus](https://en.wikipedia.org/wiki/Lambda_calculus)
  
### Examples

``` 
>> f := # ^ 2 &
>> f(3)
9

>> #^3& /@ {1, 2, 3}
{1,8,27}

>> #1+#2&[4, 5]
9
```

You can use `Function` with named parameters:
    
```
>> Function({x, y}, x * y)[2, 3]
6
```

Parameters are renamed, when necessary, to avoid confusion:
    
```
>> Function({x}, Function({y}, f(x, y)))[y]
```

### Related terms
[Slot](Slot.md), [SlotSequence](SlotSequence.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Function](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L617) 
