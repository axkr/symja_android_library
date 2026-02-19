## CreateUUID

```
CreateUUID( )
```

> retrieve a type 4 (pseudo randomly generated) UUID. The UUID is generated using a cryptographically strong pseudo random number generator.

See:
* [Wikipedia: Universally unique identifier](https://en.wikipedia.org/wiki/Universally_unique_identifier)

### Examples

```
>> CreateUUID() 
a13f186a-e9a3-4636-9e97-d0b3cbe7ada3

>> CreateUUID("test-") 
test-8029ae50-5db7-4bda-80e5-56eab144f5da
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CreateUUID](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L294) 
