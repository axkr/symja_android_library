## Dispatch

```
Dispatch({rule1, rule2, ...})
```

> create a dispatch map for a list of rules.
  
 
### Examples

```
>> rul=Dispatch({\"a\" -> 1, \"b\" -> 2, \"c\" -> 3})
Dispatch({a->1,b->2,c->3})

>> {"a", b, "c", d, e} /. rul
{1,b,3,d,e}
        
>> Dispatch({"a" -> 1, "b" -> 2, "c" -> 3}) // Normal // InputForm", //
{"a"->1,"b"->2,"c"->3}
```

### Related terms 
[ReplaceAll](ReplaceAll.md), [ReplaceRepeated](ReplaceRepeated.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Dispatch](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L2316) 
