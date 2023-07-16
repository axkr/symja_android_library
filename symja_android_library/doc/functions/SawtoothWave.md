## SawtoothWave

```
SawtoothWave(expr)
```

> returns the sawtooth wave value of `expr`. 

```
SawtoothWave({minimum, maximum}, expr)
```

> returns the sawtooth wave value of `expr` in the range `minimum`, `maximum`

See
* [Wikipedia - Sawtooth wave](https://en.wikipedia.org/wiki/Sawtooth_wave)

### Examples

```
>> SawtoothWave(41/42) 
41/42

>> SawtoothWave(42/41) 
1/41
```

### Github

* [Implementation of SawtoothWave](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L839) 
