## RandomReal

```
Random()
```

> gives a pseudorandom real number in the range `0.0` to `1.0`.
         
```
Random(number-type, range)
```

> gives a pseudorandom number of the type `number-type`, in the specified interval `range`. Possible `number-type`s are `Integer`, `Real` or `Complex`.

Deprecated function superseded by: [RandomComplex](RandomComplex.md), [RandomInteger](RandomInteger.md), [RandomReal](RandomReal.md)

### Examples

```
>> Random( )
0.53275
```

Eight random integers in the range 1..100:

```
>> Table(Random(Integer, {1, 100}), {8})
...
```

 