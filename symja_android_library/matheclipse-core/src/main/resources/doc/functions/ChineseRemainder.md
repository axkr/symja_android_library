## ChineseRemainder

```
ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...})
```

>  the chinese remainder function.

See:  
* [Wikipedia - Chinese_remainder_theorem](https://en.wikipedia.org/wiki/Chinese_remainder_theorem)
* [Rosetta Code - Chinese remainder theorem](https://rosettacode.org/wiki/Chinese_remainder_theorem)

## Examples

```
>> ChineseRemainder({0,3,4},{3,4,5})
39  

>> ChineseRemainder({1,-15}, {284407855036305,47})
8532235651089151

>> ChineseRemainder({-2,-17}, {284407855036305,47})
9669867071234368
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ChineseRemainder](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L653) 
