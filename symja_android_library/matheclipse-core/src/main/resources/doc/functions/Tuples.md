## Tuples

```
Tuples(list, n)
```

> creates a list of all `n`-tuples of elements in `list`.

```
Tuples({list1, list2, ...})
```

> returns a list of tuples with elements from the given lists.

See
* [Wikipedia - Tuple](https://en.wikipedia.org/wiki/Tuple) 


### Examples

```
>> Tuples({a, b, c}, 2)
{{a,a},{a,b},{a,c},{b,a},{b,b},{b,c},{c,a},{c,b},{c,c}}

>> Tuples[{{a, b}, {1, 2, 3}}]
{{a,1},{a,2},{a,3},{b,1},{b,2},{b,3}}

>> Thread(Tuples({0, 1}, 2) -> {a, b, c, d}) 
{{0,0}->a,{0,1}->b,{1,0}->c,{1,1}->d}
```

The head of list need not be `List`:

```
>> Tuples(f(a, b, c), 2) 
{f(a,a),f(a,b),f(a,c),f(b,a),f(b,b),f(b,c),f(c,a),f(c,b),f(c,c)}
```

However, when specifying multiple expressions, `List` is always used:

```
>> Tuples({f(a, b), g(x, y)})
{{a,x},{a,y},{b,x},{b,y}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Tuples](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L2809) 
