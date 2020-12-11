## HammingDistance

``` 
HammingDistance(a, b)
```

> returns the Hamming distance of `a` and `b`, i.e. the number of different elements.

See:
* [Wikipedia - Hamming distance](https://en.wikipedia.org/wiki/Hamming_distance)
 

### Examples

```
>> HammingDistance("time", "dime")
1

```

The `IgnoreCase` option makes `EditDistance` ignore the case of letters:

``` 
>> HammingDistance("TIME", "dime", IgnoreCase -> True)
1
```
 