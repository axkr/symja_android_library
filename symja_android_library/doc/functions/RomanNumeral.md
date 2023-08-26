## RomanNumeral

```
RomanNumeral(positive-int-value) 
```

> converts the given `positive-int-value` to a roman numeral string.


See
* [Wikipedia - Roman numerals](https://en.wikipedia.org/wiki/Roman_numerals)
* [Unicode Number Forms - Archaic Roman Numerals](http://www.unicode.org/charts/PDF/U2150.pdf)

### Examples

```
>> RomanNumeral({4548,3267,3603,1929,2575,746,666,4108,1457,3828}) 
{MↁDXLVIII,MMMCCLXVII,MMMDCIII,MCMXXIX,MMDLXXV,DCCXLVI,DCLXVI,MↁCVIII,MCDLVII,MMMDCCCXXVIII}
```

Zeros are represented by `N`

```
>> RomanNumeral(0) 
N
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RomanNumeral](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L628) 
