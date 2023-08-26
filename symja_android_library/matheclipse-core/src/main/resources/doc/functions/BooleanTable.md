## BooleanTable

```
BooleanTable(logical-expr, variables)
```

> generate [truth values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
 
  
### Examples

```
>> BooleanTable(Implies(Implies(p, q), r), {p, q, r})
{True,False,True,True,True,False,True,False}

>> BooleanTable(Xor(p, q, r), {p, q, r})
{True,False,False,True,False,True,True,False}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BooleanTable](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1387) 
