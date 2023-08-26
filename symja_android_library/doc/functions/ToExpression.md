## ToExpression

```
ToExpression("string", form)
```

> converts the `string` given in `form` into an expression.
   
### Examples

```
>> ToExpression("1 + 2 - x \\times 4 \\div 5", TeXForm)
3-4/5*x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ToExpression](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2969) 
