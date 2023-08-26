## Head

```
Head(expr)
```

> returns the head of the expression or atom `expr`.

```
Head(expr, newHead)
```

> returns `newHead(Head(expr))`.


### Examples

```
>> Head(a * b)
Times

>> Head(6)
Integer

>> Head(6+I)
Complex

>> Head(6.0)
Real

>> Head(6.0+I)
Complex

>> Head(3/4)
Rational

>> Head(x)
Symbol
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Head](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L775) 
