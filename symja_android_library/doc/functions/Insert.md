## Insert

```
Insert(list, elem, n)
```

> inserts `elem` at position `n` in `list`. When `n` is negative, the position is counted from the end.

### Examples

```
>> Insert({a,b,c,d,e}, x, 3)
{a,b,x,c,d,e}
    
>> Insert({a,b,c,d,e}, x, -2)
{a,b,c,d,x,e}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Insert](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L3661) 
