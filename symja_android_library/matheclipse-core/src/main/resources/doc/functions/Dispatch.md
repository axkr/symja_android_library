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