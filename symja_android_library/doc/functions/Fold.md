## Fold

```
Fold[f, x, {a, b}]
```

>  returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary length. 

### Examples
 
```
>> Fold(test, t1, {a, b, c, d})
test(test(test(test(t1,a),b),c),d)
```