## ArrayRules

```
ArrayRules(sparse-array)
```

> return the array of rules which define the sparse array.

```
ArrayRules(sparse-array, defaultValue)
```

> return the array of rules which define the sparse array and set the new `defaultValue`.
 
```
ArrayRules(nestedLists)
```

> return the array of rules which define the nested lists.

### Examples

```  
>> a = {{{0,0},{1,1}},{{0,1},{0,1}}} 
{{{0,0},{1,1}},{{0,1},{0,1}}} 

>> ArrayRules(a) 
{{1,2,1}->1,{1,2,2}->1,{2,1,2}->1,{2,2,2}->1,{_,_,_}->0}
```

### Related terms
[SparseArray](SparseArray.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ArrayRules](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SparseArrayFunctions.java#L74) 
