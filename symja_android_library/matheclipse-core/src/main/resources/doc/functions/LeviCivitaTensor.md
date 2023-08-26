## LeviCivitaTensor

```
LeviCivitaTensor(n)
```

> returns the `n`-dimensional Levi-Civita tensor as sparse array. The Levi-Civita symbol represents a collection of numbers; defined from the sign of a permutation of the natural numbers `1, 2, …, n`, for some positive integer `n`.  

See:
* [Wikipedia - Levi-Civita symbol](https://en.wikipedia.org/wiki/Levi-Civita_symbol)
 

### Examples

```
>> LeviCivitaTensor(4) // Normal 
{{{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},{{0,0,0,0},{0,0,0,0},{0,0,0,1},{0,0,- 
1,0}},{{0,0,0,0},{0,0,0,-1},{0,0,0,0},{0,1,0,0}},{{0,0,0,0},{0,0,1,0},{0,-1,0,0},{
0,0,0,0}}},{{{0,0,0,0},{0,0,0,0},{0,0,0,-1},{0,0,1,0}},{{0,0,0,0},{0,0,0,0},{0,0,
0,0},{0,0,0,0}},{{0,0,0,1},{0,0,0,0},{0,0,0,0},{-1,0,0,0}},{{0,0,-1,0},{0,0,0,0},{
1,0,0,0},{0,0,0,0}}},{{{0,0,0,0},{0,0,0,1},{0,0,0,0},{0,-1,0,0}},{{0,0,0,-1},{0,
0,0,0},{0,0,0,0},{1,0,0,0}},{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},{{0,1,0,0},{-
1,0,0,0},{0,0,0,0},{0,0,0,0}}},{{{0,0,0,0},{0,0,-1,0},{0,1,0,0},{0,0,0,0}},{{0,0,
1,0},{0,0,0,0},{-1,0,0,0},{0,0,0,0}},{{0,-1,0,0},{1,0,0,0},{0,0,0,0},{0,0,0,0}},{{
0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}}}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LeviCivitaTensor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L268) 
