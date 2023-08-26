## Throw

      
```
Throw(value)
```

> stops evaluation and returns `value` as the value of the nearest enclosing `Catch`. `Catch(value, tag)` is caught only by `Catch(expr, form)`, where `tag` matches `form`.
 

### Examples

Using `Throw` can affect the structure of what is returned by a function:

```
>> NestList(#^2 + 1 &, 1, 7)
{1,2,5,26,677,458330,210066388901,44127887745906175987802}
 
>> Catch(NestList(If(# > 1000, Throw(#), #^2 + 1) &, 1, 7))
458330
```

A `Throw` without an enclosing `Catch` prints the message: `Uncaught Throw(1) returned to top level.`

```
>> Throw[1]
Hold(Throw(1))
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Throw](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3204) 
