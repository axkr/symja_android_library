## BooleanMinimize

```
BooleanMinimize(expr)
```

> minimizes a boolean function with the [Quine McCluskey algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
 
### Examples

```
>> BooleanMinimize(x&&y||(!x)&&y)
y

>> BooleanMinimize((a&&!b)||(!a&&b)||(b&&!c)||(!b&&c))
a&&!b||!a&&c||b&&!c
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BooleanMinimize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1272) 
