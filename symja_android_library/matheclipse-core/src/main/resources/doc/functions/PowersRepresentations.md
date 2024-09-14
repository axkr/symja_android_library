## PowersRepresentations

```
PowersRepresentations(intNumber, k, exponent)
```

> computes the representations of the `intNumber` as sum of `x^exponent` terms which occur `k` times. 

See
* [Wikipedia - Sum of squares function](https://en.wikipedia.org/wiki/Sum_of_squares_function)

### Examples

```
>> PowersRepresentations(8174, 6, 3)
{{0,0,4,10,13,17},{0,3,6,7,9,19},{0,7,10,12,12,15},{1,1,1,11,14,16},{1,3,5,12,13,16},{1,4,5,5,10,19},{1,5,6,10,10,18},{2,3,4,6,10,19},{3,3,3,4,13,18},{3,5,9,10,13,16},{4,5,6,13,13,15},{4,9,12,12,12,13},{5,9,9,13,13,13},{7,7,10,10,14,14}}

>> 2^3+3^3+4^3+6^3+10^3+19^3
8174
```
 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PowersRepresentations](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L5619) 
