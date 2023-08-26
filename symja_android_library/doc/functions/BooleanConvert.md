## BooleanConvert

```
BooleanConvert(logical-expr)
```

> convert the `logical-expr` to [disjunctive normal form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)

```
BooleanConvert(logical-expr, "CNF")
```

> convert the `logical-expr` to [conjunctive normal form](https://en.wikipedia.org/wiki/Conjunctive_normal_form)

```
BooleanConvert(logical-expr, "DNF")
```

> convert the `logical-expr` to [disjunctive normal form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
 
### Examples

```
>> BooleanConvert(Xor(x,y))
x&&!y||y&&!x

>> BooleanConvert(Xor(x,y), "CNF")
(x||y)&&(!x||!y)

>> BooleanConvert(Xor(x,y), "DNF")
x&&!y||y&&!x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BooleanConvert](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1008) 
