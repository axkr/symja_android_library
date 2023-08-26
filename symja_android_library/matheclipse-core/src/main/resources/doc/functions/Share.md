## Share

```
Share(function)
```

> replace internally equal common subexpressions in `function` by the same reference to reduce memory consumption and return the number of times where `Share(function)` could replace a common subexpression.  
 
See
* [Wikipedia - Common subexpression elimination](https://en.wikipedia.org/wiki/Common_subexpression_elimination)

### Examples

```
>> people = <|236234 -> <|"name" -> "bob", "age" -> 18, "sex" -> "M"|>,253456 -> <|"name" -> "sue", "age" -> 25, "sex" -> "F"|>, 323442 -> <|"name" -> "ann", "age" -> 18, "sex" -> "F"|>

<|236234-><|name->bob,age->20,sex->M|>,253456-><|name->sue,age->25,sex->F|>,323442-><|name->ann,age->18,sex->F|>|>
```

`"age" -> 18` and `"sex" -> "F"` could be replaced once each times by the same reference internally:

```
>> Share(people) 
2
```

```
>> people 
<|236234-><|name->bob,age->20,sex->M|>,253456-><|name->sue,age->25,sex->F|>,323442-><|name->ann,age->18,sex->F|>|>"
```

### Related terms 
[Compile](Compile.md), [CompilePrint](CompilePrint.md), [OptimizeExpression](OptimizeExpression.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Share](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Share.java#L18) 
