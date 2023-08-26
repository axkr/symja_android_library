## LetterCounts

```
LetterCounts(string)
```

> count the number of each distinct character in the `string` and return the result as an association `<|char->counter1, ...|>`.

See
* [Wikipedia - The quick brown fox jumps over the lazy dog](https://en.wikipedia.org/wiki/The_quick_brown_fox_jumps_over_the_lazy_dog) 

### Examples

```
>> LetterCounts("The quick brown fox jumps over the lazy dog") // InputForm
<|"T"->1," "->8,"a"->1,"b"->1,"c"->1,"d"->1,"e"->3,"f"->1,"g"->1,"h"->2,"i"->1,"j"->1,
"k"->1,"l"->1,"m"->1,"n"->1,"o"->4,"p"->1,"q"->1,"r"->2,"s"->1,"t"->1,"u"->2,"v"->1,"w"->1,"x"->1,"y"->1,"z"->1|>
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LetterCounts](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L871) 
