## Append

```
Append(expr, item)
```

> returns `expr` with `item` appended to its leaves.

### Examples

```
>> Append({1, 2, 3}, 4)    
{1,2,3,4}
```

`Append` works on expressions with heads other than `List`:

```
>> Append(f(a, b), c)    
f(a,b,c)
```
 
Unlike `Join`, `Append` does not flatten lists in `item`: 

```
>> Append({a, b}, {c, d})    
{a,b,{c,d}}  
```

Nonatomic expression expected.  

```
>> Append(a, b)     
Append(a,b)   
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Append](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L618) 
