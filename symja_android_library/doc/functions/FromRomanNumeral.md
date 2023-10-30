## FromRomanNumeral

```
FromRomanNumeral(roman-number-string) 
```

> converts the given `roman-number-string` to an integer number.


See
* [Wikipedia - Roman numerals](https://en.wikipedia.org/wiki/Roman_numerals)
* [Unicode Number Forms - Archaic Roman Numerals](http://www.unicode.org/charts/PDF/U2150.pdf)

### Examples

```
>> FromRomanNumeral("MDCLXVI") 
1666
```

Zeros are represented by `N`

```
>> FromRomanNumeral("N") 
0
```

### Related terms 
[RomanNumeral](RomanNumeral.md) 
