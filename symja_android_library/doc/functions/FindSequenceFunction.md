## FindSequenceFunction

```
FindSequenceFunction({i1, i2, i3, ...})
```

> searches for a unary integer function, which generates the integer sequence `{i1, i2, i3, ...}`. 

```
FindSequenceFunction({i1, i2, i3, ...}, var)
```

> searches for a unary integer function, which generates the integer sequence `{i1, i2, i3, ...}` applied to the variable `var`
  

See
* [Wikipedia - Integer sequence](https://en.wikipedia.org/wiki/Integer_sequence)
* [Wikipedia - On-Line Encyclopedia of Integer Sequences](https://simple.wikipedia.org/wiki/On-Line_Encyclopedia_of_Integer_Sequences)

### Examples

```
>> FindSequenceFunction({1*3, 2*3, 6*3, 24*3, 120*3, 720*3, 5040*3} ,n)
3*n!

>> FindSequenceFunction({6,18,54,162},n)
2*3^n"); 

>> FindSequenceFunction({7,9,11,13,15})
5+2*#1&
```
  