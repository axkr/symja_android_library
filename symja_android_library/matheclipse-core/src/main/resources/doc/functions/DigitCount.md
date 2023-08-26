## DigitCount

```
DigitCount(n)
```

> returns a list of the number of integer digits for `n` for `radix` 10.

```
DigitCount(n, radix)
```

> returns a list of the number of integer digits for `n` for `radix`.

```
DigitCount(n, radix, k)
```

> returns a the number of integer digit `k` in `n` for `radix`. 

See
* [Wikipedia - Radix](https://en.wikipedia.org/wiki/Radix)

### Examples

OEIS integer sequence [A098949](https://oeis.org/A098949):

``` 
>> Select(Range(1000), DigitCount( # )[[1]] == 0 && DigitCount( # )[[3]] == 0 && DigitCount( # )[[5]] == 0 && DigitCount( # )[[7]] == 0 && DigitCount( # )[[9]] >0 &)

{9,29,49,69,89,90,92,94,96,98,99,209,229,249,269,289,290,292,294,296,298,299,409,
429,449,469,489,490,492,494,496,498,499,609,629,649,669,689,690,692,694,696,698,
699,809,829,849,869,889,890,892,894,896,898,899,900,902,904,906,908,909,920,922,
924,926,928,929,940,942,944,946,948,949,960,962,964,966,968,969,980,982,984,986,
988,989,990,992,994,996,998,999}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DigitCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L344) 
