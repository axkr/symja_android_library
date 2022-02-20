## IntegerName

```
IntegerName(integer-number)
```

> gives the spoken number string of `integer-number` in language `English`.

```
IntegerName(integer-number, "language")
```

> gives the spoken number string of `integer-number` in language `language`.

See
* [Wikipedia - Integer](https://en.wikipedia.org/wiki/Integer)

### Examples

```
>> IntegerName(0) 
zero

>> IntegerName(42) 
forty-two

>> IntegerName(-42)
minus forty-two

>> IntegerName(-123007,"German")
minus einhundertdreiundzwanzigtausendsieben
				
>> IntegerName(123007,"Spanish") 
ciento veintitrés mil siete
```

### Github

* [Implementation of IntegerName](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L301) 
