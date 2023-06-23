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