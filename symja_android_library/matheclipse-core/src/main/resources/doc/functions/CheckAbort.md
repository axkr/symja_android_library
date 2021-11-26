## CheckAbort

```
CheckAbort(expr, failure-expr)
```

> evaluates `expr`, and returns the result, unless `Abort` was called during the evaluation, in which case `failure-expr` will be returned.

### Examples
 
```
>> CheckAbort(Abort(); -1, 41) + 1 
42

>> CheckAbort(abc; -1, 41) + 1
0
```
