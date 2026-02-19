## HermiteDecomposition

```
HermiteDecomposition(matrix)
```

> calculate the Hermite-decomposition as a list `{u,r}` of a square `matrix`.
 
See:    
* [Wikipedia - Hermite normal form](https://en.wikipedia.org/wiki/Hermite_normal_form) 
 
### Examples

```
>> HermiteDecomposition({{ 5, 2, -1, 4, 0}, {3,8,2,0,6}, {1,-4,5,2,1}, {0,3,7,9,2}, {6,1,0,5,8}}) 
{{{-298,6,-69,5,257},{-51,1,-12,1,44},{-457,9,-106,8,394},{-270,5,-63,5,233},{703,14,-163,12,606}},{{1,0,0,0,2033},{0,1,0,1,348},{0,0,1,2,3116},{0,0,0,4,1841},{0,0,0,0,4793}}}
```
 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HermiteDecomposition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/HermiteDecomposition.java#L22) 
