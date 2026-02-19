## FileHash

```
FileHash(file)
```

> computes an MD5 hash for the contents of the specified `file`. The FileHash function computes a cryptographic hash for the contents of a `file`. It is useful for verifying file integrity and detecting changes.  

```
FileHash(file, hash-type)
```

> computes a hash of the specified `hash-type` (e.g., "SHA-256") for the file.

### Examples

```
>> FileHash(StringToStream("Hello World"),"MD5") 
235328152096874191772633713977838157797
```

### Related terms 
[Hash](Hash.md) 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FileHash](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FileHash.java#L16) 
