## Interrupt

```
Interrupt( )
```
 
> Interrupt an evaluation and returns `$Aborted`.  

### Examples

`Print(test1)` prints string: "test1"

```
>> Print(test1); Interrupt(); Print(test2)
$Aborted
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Interrupt](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1308) 
