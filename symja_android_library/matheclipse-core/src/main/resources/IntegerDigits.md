## IntegerDigits

```
IntegerDigits(n, base)
```

> returns a list of integer digits for `n` under `base`.

```
IntegerDigits(n, base, padLeft)
```

>  pads the result list on the left with maximum `padLeft` zeros.

### Examples

```
>> IntegerDigits(123)
{1,2,3}

>> IntegerDigits(-123)
{1,2,3}

>> IntegerDigits(123, 2)
{1,1,1,1,0,1,1}

>> IntegerDigits(123, 2, 10)
{0,0,0,1,1,1,1,0,1,1}

>> IntegerDigits({123,456,789}, 2, 10)
{{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}
```